package com.onpositive.text.analysis.syntax;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.text.analysis.BasicCleaner;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.ParserComposition;
import com.onpositive.text.analysis.lexic.AbstractParser;
import com.onpositive.text.analysis.lexic.NumericsParser;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.WordFormParser;
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
		NumericsParser.class
	};
	
	private static final Class<?>[] syntaxParsersArray = new Class<?>[]{
		AdverbModificatorParser.class,
		UniformAdverbParser.class,
		AdjectiveAdverbParser.class,
		VerbAdverbParser.class,
		UniformAdjectivesParser.class,
		NounAdjectiveParser.class,
		NounDimensionParser.class,
		UniformNounsParser.class,		
		DirectObjectParser.class,
		ClauseParser.class
	};
	
	private static final Class<?>[] syntaxParsersArray2 = new Class<?>[]{
		VerbAdverbParser.class,
		DirectObjectParser.class,
	};
	
	public SyntaxParser(AbstractWordNet wordnet) {
		super();
		this.wordNet = wordnet;
		initParsers();
	}

	private AbstractWordNet wordNet;
	
	private ParserComposition lexicParser;
	
	private List<AbstractParser> syntaxParsers ;
	
	private List<AbstractParser> syntaxParsers2 ;
	
	private PrimitiveTokenizer primitiveTokenizer = new PrimitiveTokenizer();
	
	private PrimitiveSentenceMaker sentenceMaker = new PrimitiveSentenceMaker();
	
	
	public List<IToken> parse(String str){
		setText(str);
		List<IToken> primitiveTokens = primitiveTokenizer.tokenize(str);
		List<IToken> lexicProcessed = lexicParser.process(primitiveTokens);
		List<IToken> sentences = sentenceMaker.formSentences(lexicProcessed);
		
		for(IToken sentence : sentences){
			List<IToken> tokens = new ArrayList<IToken>(sentence.getChildren());
			for(AbstractParser parser : syntaxParsers){
				do{
					tokens = applyParser(tokens, parser);
				}
				while(parser.hasTriggered()&&parser.isRecursive());
			}
			for(AbstractParser parser : syntaxParsers2){
				tokens = applyParser(tokens, parser);
			}
			sentence.setChildren(new BasicCleaner().clean(tokens));
		}
		return sentences;
	}


	public void setText(String str) {
		lexicParser.setText(str);
		for(AbstractParser ap : syntaxParsers){
			ap.setText(str);
		}
	}


	private final List<IToken> applyParser(List<IToken> tokens,	AbstractParser parser) {
		parser.resetTrigger();
		List<IToken> result = parser.process(tokens);
		return result;
	}
	
	
	private void initParsers() {
		List<AbstractParser> lp = createParsers(lexicParsersArray) ;
		lexicParser = new ParserComposition(lp.toArray(new AbstractParser[lp.size()]));
		this.syntaxParsers = createParsers(syntaxParsersArray);
		this.syntaxParsers2 = createParsers(syntaxParsersArray2);	
	}

	private List<AbstractParser> createParsers(Class<?>[] array) {
		
		ArrayList<AbstractParser> list = new ArrayList<AbstractParser>();
		for(Class<?> clazz : array){
			AbstractParser parser = createParser(clazz);
			if(parser != null){
				list.add(parser);
			}
		}
		return list;
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
