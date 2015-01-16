package com.onpositive.text.analysis.syntax;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.text.analysis.AbstractParser;
import com.onpositive.text.analysis.BasicCleaner;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.ParserComposition;
import com.onpositive.text.analysis.lexic.NumericsParser;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.lexic.dates.DateCombineParser;
import com.onpositive.text.analysis.lexic.dates.DateParser;
import com.onpositive.text.analysis.lexic.dates.LongNameParser;
import com.onpositive.text.analysis.lexic.dimension.DimensionParser;
import com.onpositive.text.analysis.lexic.dimension.UnitGroupParser;
import com.onpositive.text.analysis.lexic.dimension.UnitParser;
import com.onpositive.text.analysis.lexic.scalar.ScalarParser;

public class SyntaxParser extends ParserComposition {
	
	private static final Class<?>[] lexicParsersArray = new Class<?>[]{
		WordFormParser.class,
		ScalarParser.class,
		UnitParser.class,
		UnitGroupParser.class,
		DimensionParser.class,
		NumericsParser.class,
		DateParser.class,
		DateCombineParser.class,
		LongNameParser.class
	};
	
	private static final Class<?>[] nameSyntaxParsersArray = new Class<?>[]{
		AdverbModificatorParser.class,
		UniformAdverbParser.class,
		AdjectiveAdverbParser.class,		
		UniformAdjectivesParser.class,
		NounAdjectiveParser.class,
		NounDimensionParser.class,
		UniformNounsParser.class
	};
	
	private static final Class<?>[] nameSyntaxRecursiveParsersArray = new Class<?>[]{
		GenitiveChainParser.class
	};
	
	private static final Class<?>[] verbGroupSyntaxParsersArray = new Class<?>[]{
		VerbAdverbParser.class,
		DirectObjectParser.class,
		VerbNamePrepositionParser.class,		
		VerbNameParser.class,
		VerbGerundParser.class,
		VerbNamePrepositionParser.class,
		NounNamePrepositionParser.class
	};
	
	public SyntaxParser(AbstractWordNet wordnet) {
		super();
		this.wordNet = wordnet;
		initParsers();
	}

	private AbstractWordNet wordNet;
	
	private ParserComposition lexicParsers;
	
	private ParserComposition nameSyntaxParsers;
	
	private ParserComposition nameRecursiveSyntaxParsers;
	
	private ParserComposition verbGroupSyntaxParsers;
	
	private ClauseParser clauseParser;
	
	private IncompleteClauseParser incompleteClauseParser;

	private PrimitiveTokenizer primitiveTokenizer = new PrimitiveTokenizer();
	
	private SentenceSplitter sentenceSplitter = new SentenceSplitter();
	
	
	public List<IToken> parse(String str){
		setText(str);
		List<IToken> primitiveTokens = primitiveTokenizer.tokenize(str);
		List<IToken> lexicProcessed = lexicParsers.process(primitiveTokens);
		List<IToken> sentences = sentenceSplitter.split(lexicProcessed);
		
		for(IToken sentence : sentences){
			List<IToken> initialTokens = new ArrayList<IToken>(sentence.getChildren());
			List<IToken> namesProcessed = nameSyntaxParsers.process(initialTokens);
			List<IToken> recNamesProcessed1 = nameRecursiveSyntaxParsers.process(namesProcessed);
			List<IToken> verbsProcessed1 = verbGroupSyntaxParsers.process(recNamesProcessed1);
			List<IToken> clausesFormed = clauseParser.process(verbsProcessed1);			
			List<IToken> recNamesProcessed2 = nameRecursiveSyntaxParsers.process(clausesFormed);
			List<IToken> verbsProcessed2 = verbGroupSyntaxParsers.process(recNamesProcessed2);
			List<IToken> incompleteClausesFormed = incompleteClauseParser.process(verbsProcessed2);
			List<IToken> tokens = incompleteClausesFormed;
			sentence.setChildren(new BasicCleaner().clean(tokens));
		}
		return sentences;
	}


	public void setText(String str) {
		lexicParsers.setText(str);		
		this.verbGroupSyntaxParsers.setText(str);
		this.nameSyntaxParsers.setText(str);
		this.nameRecursiveSyntaxParsers.setText(str);
		this.clauseParser.setText(str);
		this.incompleteClauseParser.setText(str);
	}
	
	private void initParsers() {
		this.lexicParsers = createParsers(lexicParsersArray,false) ;		
		this.verbGroupSyntaxParsers = createParsers(verbGroupSyntaxParsersArray,true);
		this.nameSyntaxParsers = createParsers(nameSyntaxParsersArray, false);
		this.nameRecursiveSyntaxParsers = createParsers(nameSyntaxRecursiveParsersArray, true);
		this.clauseParser = new ClauseParser(this.wordNet);
		this.incompleteClauseParser = new IncompleteClauseParser(this.wordNet);
	}

	private ParserComposition createParsers(Class<?>[] array,boolean isGloballyRecursive) {
		
		ArrayList<AbstractParser> list = new ArrayList<AbstractParser>();
		for(Class<?> clazz : array){
			AbstractParser parser = createParser(clazz);
			if(parser != null){
				list.add(parser);
			}
		}
		AbstractParser[] arr = list.toArray(new AbstractParser[list.size()]);
		ParserComposition result = new ParserComposition(isGloballyRecursive, arr);
		return result;
	}

	private AbstractParser createParser(Class<?> clazz) {
		
		boolean isParser = extendsClass(clazz,AbstractParser.class);
		if(!isParser){
			return null;
		}
		Constructor<?> constr = null;
		Constructor<?>[] constructors = clazz.getConstructors();
		for(Constructor<?> c : constructors){
			Class<?>[] params = c.getParameterTypes();
			if(params.length==0){
				constr = c;
			}
			else if(params.length==1){
				Class<?> paramClass = params[0];
				if(extendsClass(paramClass, AbstractWordNet.class)){
					constr = c;
					break;
				}
			}
		}
		if(constr==null){
			return null;
		}
		AbstractParser instance = null;
		try{
			if(constr.getParameterTypes().length==0){
				instance = (AbstractParser) constr.newInstance();
			}
			else{
				instance = (AbstractParser) constr.newInstance(this.wordNet);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return instance;
	}

	private boolean extendsClass(Class<?> clazz, Class<?> parent) {
		boolean isParser = false;
		for(Class<?> cl = clazz ; cl != null ; cl = cl.getSuperclass()){
			isParser = (cl==parent);
			if(isParser){
				break;
			}
		}
		return isParser;
	}


}
