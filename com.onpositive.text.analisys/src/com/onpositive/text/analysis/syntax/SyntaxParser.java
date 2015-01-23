package com.onpositive.text.analysis.syntax;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.text.analysis.AbstractParser;
import com.onpositive.text.analysis.BasicCleaner;
import com.onpositive.text.analysis.IParser;
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
		UniformNounsParser.class,
		GenitiveChainParser.class//,
//		PrepositionGroupParser.class
	};
	
	private static final Class<?>[] nameSyntaxRecursiveParsersArray = new Class<?>[]{
		GenitiveChainParser.class
	};
	
	private static final Class<?>[] verbGroupSyntaxParsersArray = new Class<?>[]{
		VerbAdverbParser.class,
		DirectObjectParser.class,
		VerbNameComposition.class,		
		VerbGerundParser.class,
		VerbNameComposition.class
	};
	
	private static final Class<?>[] participleParserArray = new Class<?>[]{
		ParticipleAttachingParser.class
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
	
	private ParserComposition participleParsers;
	
	private ClauseParser clauseParser;
	
	private IncompleteClauseParser incompleteClauseParser;

	private PrimitiveTokenizer primitiveTokenizer = new PrimitiveTokenizer();
	
	private SentenceSplitter sentenceSplitter = new SentenceSplitter();
	
	private ArrayList<IParser> syntaxParsers;
	
	
	public List<IToken> parse(String str){
		setText(str);
		List<IToken> primitiveTokens = primitiveTokenizer.tokenize(str);
		List<IToken> lexicProcessed = lexicParsers.process(primitiveTokens);
		List<IToken> sentences = sentenceSplitter.split(lexicProcessed);
		
		for(IToken sentence : sentences){
			List<IToken> initialTokens = new ArrayList<IToken>(sentence.getChildren());
			for(IParser p : syntaxParsers){
				p.setBaseTokens(initialTokens);
			}
			List<IToken> namesProcessed = nameSyntaxParsers.process(initialTokens);
			List<IToken> recNamesProcessed1 = nameRecursiveSyntaxParsers.process(namesProcessed);
			List<IToken> verbsProcessed1 = verbGroupSyntaxParsers.process(recNamesProcessed1);
			List<IToken> participlesProcessed = participleParsers.process(verbsProcessed1);
			List<IToken> clausesFormed = clauseParser.process(participlesProcessed);			
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
		this.participleParsers = createParsers(participleParserArray, false);
		this.clauseParser = new ClauseParser(this.wordNet);
		this.incompleteClauseParser = new IncompleteClauseParser(this.wordNet);
		this.syntaxParsers = new ArrayList<IParser>();
		
		this.syntaxParsers.add(nameSyntaxParsers);
		this.syntaxParsers.add(nameRecursiveSyntaxParsers);
		this.syntaxParsers.add(verbGroupSyntaxParsers);
		this.syntaxParsers.add(clauseParser);
		this.syntaxParsers.add(incompleteClauseParser);
		this.syntaxParsers.add(participleParsers);
	}

	private ParserComposition createParsers(Class<?>[] array,boolean isGloballyRecursive) {
		
		ArrayList<IParser> list = new ArrayList<IParser>();
		for(Class<?> clazz : array){
			IParser parser = createParser(clazz);
			if(parser != null){
				list.add(parser);
			}
		}
		IParser[] arr = list.toArray(new IParser[list.size()]);
		ParserComposition result = new ParserComposition(isGloballyRecursive, arr);
		return result;
	}

	private IParser createParser(Class<?> clazz) {
		
		boolean isParser = extendsClass(clazz,AbstractParser.class) || hasInterface(clazz, IParser.class);
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
		IParser instance = null;
		try{
			if(constr.getParameterTypes().length==0){
				instance = (IParser) constr.newInstance();
			}
			else{
				instance = (IParser) constr.newInstance(this.wordNet);
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

	private boolean hasInterface(Class<?> clazz, Class<IParser> iClass) {
		boolean isParser = false;
		for(Class<?> cl = clazz ; cl != null ; cl = cl.getSuperclass()){
			
			if(cl==iClass){
				return true;
			}
			
			Class<?>[] interfaces = cl.getInterfaces();
			if(interfaces==null){
				continue;
			}
			for(Class<?> i : interfaces){
				if(hasInterface(i, iClass)){
					return true;
				}
			}
		}
		return isParser;
	}


}
