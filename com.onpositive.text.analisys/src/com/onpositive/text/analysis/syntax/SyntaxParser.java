package com.onpositive.text.analysis.syntax;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.text.analysis.AbstractParser;
import com.onpositive.text.analysis.BasicCleaner;
import com.onpositive.text.analysis.CompositToken;
import com.onpositive.text.analysis.IParser;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.IToken.Direction;
import com.onpositive.text.analysis.ParserComposition;
import com.onpositive.text.analysis.SentenceTreeBuilder;
import com.onpositive.text.analysis.SentenceTreeRuleFactory;
import com.onpositive.text.analysis.SentenceTreeBuilder.DecisionRule;
import com.onpositive.text.analysis.SentenceTreeBuilder.TokenArrayBuffer;
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
	
	public class TreeBuilder extends SentenceTreeBuilder{
		
		private TokenIdProvider tip;

		public TreeBuilder() {
			super();
			SentenceTreeRuleFactory factory = new SentenceTreeRuleFactory();
			this.setRules(factory.getDetectionRules());
			this.setDecisionRules(factory.getRules());
		}
		
		@Override
		public List<IToken> gatherTree(List<IToken> tokens) {
			this.tip = new TokenIdProvider();
			tip.prepare(tokens);
			return super.gatherTree(tokens);
		}

		@Override
		protected List<IToken> produceResultToken(SentenceNode node, IToken parent,DecisionRule rule, List<IToken> regionTokens) {
			int startPosition = Math.min(parent.getStartPosition(), regionTokens.get(0).getStartPosition());
			int endPosition = Math.max(parent.getEndPosition(), regionTokens.get(regionTokens.size()-1).getEndPosition());
			SyntaxToken newToken = new SyntaxToken(parent.getType(), (SyntaxToken) parent, null, startPosition, endPosition);
			if(parent.getStartPosition() < regionTokens.get(0).getStartPosition()){
				newToken.addChild(parent);
				newToken.addChildren(regionTokens);
			}
			else{
				newToken.addChildren(regionTokens);
				newToken.addChild(parent);
			}
			newToken.setId(tip.getVacantId());
			ArrayList<IToken> result = new ArrayList<IToken>();
			result.add(newToken);
			return result;
		}

		@Override
		protected List<IToken> processContent(List<IToken> tokens,DecisionRule rule) {			
			return SyntaxParser.this.parseSyntax(tokens,tip);
		}

		@Override
		protected List<IToken> processBound(List<IToken> tokens,DecisionRule rule, Direction dir)
		{
			int startPosition = tokens.get(0).getStartPosition();
			int endPosition = tokens.get(tokens.size()-1).getEndPosition();			
			CompositToken newToken = new CompositToken(tokens,IToken.TOKEN_TYPE_REGION_BOUND,startPosition,endPosition);
			newToken.addChildren(tokens);
			newToken.setId(tip.getVacantId());
			ArrayList<IToken> result = new ArrayList<IToken>();
			result.add(newToken);
			return result;
		}

		@Override
		protected List<IToken> produceRegionToken(List<IToken> content, List<IToken> startBound, List<IToken> endBound, DecisionRule rule) {
			int startPosition = !startBound.isEmpty()
					? startBound.get(0).getStartPosition()
					: content.get(0).getStartPosition();
					
			int endPosition = !endBound.isEmpty()
					? endBound.get(endBound.size()-1).getEndPosition()
					: content.get(content.size()-1).getEndPosition();
			
			ArrayList<IToken> tokens = new ArrayList<IToken>();
			tokens.addAll(startBound);
			tokens.addAll(content);
			tokens.addAll(endBound);
			
			CompositToken newToken = new CompositToken(tokens,rule.getResultTokenType(),startPosition,endPosition);
			newToken.setId(tip.getVacantId());
			newToken.addChildren(tokens);
			ArrayList<IToken> result = new ArrayList<IToken>();
			result.add(newToken);
			return result;
		}

		public TokenIdProvider getTokenIdProvider() {
			return tip;
		}
		
	}
	
	private TreeBuilder treeBuilder = new TreeBuilder();
	
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
			List<IToken> tokens = treeBuilder.gatherTree(initialTokens);
			List<IToken> tokens1 = parseSyntax(tokens,treeBuilder.getTokenIdProvider());
			sentence.setChildren(new BasicCleaner().clean(tokens1));
		}
		return sentences;
	}


	protected List<IToken> parseSyntax(List<IToken> initialTokens, TokenIdProvider tip) {
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
		return tokens;
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
