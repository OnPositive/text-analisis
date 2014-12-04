package com.onpositive.text.analysis.syntax;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
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
import com.onpositive.text.analysis.rules.matchers.AndMatcher;
import com.onpositive.text.analysis.rules.matchers.HasAllGrammems;
import com.onpositive.text.analysis.rules.matchers.HasAnyOfGrammems;
import com.onpositive.text.analysis.rules.matchers.HasGrammem;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;

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
		NounAdjectiveParser.class,
		DirectSubjectParser.class
	};
	
	public SyntaxParser(AbstractWordNet wordnet) {
		super();
		this.wordNet = wordnet;
		initParsers();
	}

	private AbstractWordNet wordNet;
	
	private ParserComposition lexicParser;
	
	private List<AbstractParser> syntaxParsers ;
	
	private PrimitiveTokenizer primitiveTokenizer = new PrimitiveTokenizer();
	
	private PrimitiveSentenceMaker sentenceMaker = new PrimitiveSentenceMaker();
	
	
	public List<IToken> parse(String str){
		
		List<IToken> primitiveTokens = primitiveTokenizer.tokenize(str);
		List<IToken> lexicProcessed = lexicParser.process(primitiveTokens);
		List<IToken> sentences = sentenceMaker.formSentences(lexicProcessed);
		
		for(IToken sentence : sentences){
			List<IToken> tokens = new ArrayList<IToken>(sentence.getChildren());
			for(AbstractParser parser : syntaxParsers){
				do{
					parser.resetTrigger();
					tokens = parser.process(tokens);
				}
				while(parser.hasTriggered());
			}
			sentence.setChildren(tokens);
		}
		return sentences;
	}
	
	
	private void initParsers() {
		List<AbstractParser> lp = createParsers(lexicParsersArray) ;
		lexicParser = new ParserComposition(lp.toArray(new AbstractParser[lp.size()]));
		this.syntaxParsers = createParsers(syntaxParsersArray);		
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