package com.onpositive.text.analisys.tests.euristics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import junit.framework.TestCase;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.onpositive.text.analisys.tests.ParsedTokensLoader;
import com.onpositive.text.analysis.Euristic;
import com.onpositive.text.analysis.EuristicAnalyzingParser;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.filtering.AbbreviationsFilter;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.rules.RuleSet;

public class EuristicAnalysisTest extends TestCase{
	
	private static final int MAX_NEUTRALIZATION_LOOKAHEAD = 10; 
	
	public void test02() {
		testWithFile("3344.xml");
		testWithFile("2176.xml");
		testWithFile("2241.xml");
	}

	private void testWithFile(String filename) {
		ParsedTokensLoader loader = new ParsedTokensLoader(EuristicAnalysisTest.class.getResourceAsStream(filename));
		List<SimplifiedToken> etalonTokens = loader.getTokens();
		String text = loader.getInitialText();
		EuristicAnalyzingParser euristicAnalyzingParser = configureDefaultAnalyzer(createRulesList());
		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens(text));
		compare(etalonTokens,processed);
		System.out.println("//--------------------------------------------------------------------------------------------------");
	}
	
	
	private List<Euristic> createRulesList() {
		List<Euristic> euristics = new ArrayList<Euristic>();
//		euristics.addAll(RuleSet.getRulesList5());
//		euristics.addAll(RuleSet.getRulesList6());
//		euristics.addAll(RuleSet.getRulesList7());
//		euristics.addAll(RuleSet.getRulesList8());
//		euristics.addAll(RuleSet.getRulesList9());
//		euristics.addAll(RuleSet.getRulesList10());
//		euristics.addAll(RuleSet.getRulesList11());
//		euristics.addAll(RuleSet.getRulesList12());
//		euristics.addAll(RuleSet.getRulesList13());
//		euristics.addAll(RuleSet.getRulesList14());
//		euristics.addAll(RuleSet.getRulesList15());
//		euristics.addAll(RuleSet.getRulesList16());
//		euristics.addAll(RuleSet.getRulesList17());
//		euristics.addAll(RuleSet.getRulesList18());
//		euristics.addAll(RuleSet.getRulesList19());
//		euristics.addAll(RuleSet.getRulesList20());
//		euristics.addAll(RuleSet.getRulesList21());
//		euristics.addAll(RuleSet.getRulesList22());
//		euristics.addAll(RuleSet.getRulesList23());
//		euristics.addAll(RuleSet.getRulesList24());
//		euristics.addAll(RuleSet.getRulesList25());
//		euristics.addAll(RuleSet.getRulesList26());
//		euristics.addAll(RuleSet.getRulesList27());
//		euristics.addAll(RuleSet.getRulesList28());
//		euristics.addAll(RuleSet.getRulesList29());
//		euristics.addAll(RuleSet.getRulesList30());
//		euristics.addAll(RuleSet.getRulesList31());
//		euristics.addAll(RuleSet.getRulesList32());
//		euristics.addAll(RuleSet.getRulesList33());
//		euristics.addAll(RuleSet.getRulesList34());
//		euristics.addAll(RuleSet.getRulesList35());
//		euristics.addAll(RuleSet.getRulesList36());
//		euristics.addAll(RuleSet.getRulesList37());
		return euristics;
	}

	private EuristicAnalyzingParser configureDefaultAnalyzer(List<Euristic> euristics) {
		EuristicAnalyzingParser euristicAnalyzingParser = new EuristicAnalyzingParser(euristics);
		euristicAnalyzingParser.addTokenFilter(new AbbreviationsFilter());
		return euristicAnalyzingParser;
	}
	
	public List<IToken> getWordFormTokens(String str) {
		PrimitiveTokenizer pt = new PrimitiveTokenizer();
		AbstractWordNet instance = WordNetProvider.getInstance();
		WordFormParser wfParser = new WordFormParser(instance);
		wfParser.setIgnoreCombinations(true);
		List<IToken> tokens = pt.tokenize(str);		
		List<IToken> processed = wfParser.process(tokens);
		return processed;
	}
	
	@SuppressWarnings("unchecked")
	private void compare(List<SimplifiedToken> etalonTokens, List<IToken> tokens) { 
		int i = 0;
		int j = 0;
		ITokenComparator tokenComparator = new PartOfSpeechComparator();
		Map<PartOfSpeech, Integer> comparedCounts = new HashMap<PartOfSpeech, Integer>();
		int comparedCount = 0;
		int conflictingCount = 0;
		int wrongCount = 0;
		while (i < etalonTokens.size() && j < tokens.size()) {
			SimplifiedToken etalonToken = etalonTokens.get(i);
			List<WordFormToken> comparedTokens = new ArrayList<WordFormToken>();
			j = getNextWordTokenIdx(tokens, j, etalonToken);
			if (j == -1) {
				printComparisonResults(comparedCounts, comparedCount, conflictingCount, wrongCount);
				return;
			}
			WordFormToken wordFormToken = (WordFormToken) tokens.get(j);
			if (!etalonToken.wordEquals(wordFormToken)) {
				i = tryEtalonNeutralization(i, etalonTokens, wordFormToken);
				if (i == -1) {
					printComparisonResults(comparedCounts, comparedCount, conflictingCount, wrongCount);
					return;
				} else {
					etalonToken = etalonTokens.get(i);
				}
			}
			
			comparedTokens.add(wordFormToken);
			List<IToken> conflicts = wordFormToken.getConflicts();
			if (conflicts != null) {
				comparedTokens.addAll((Collection<? extends WordFormToken>) conflicts);
				j += conflicts.size();
			}
			if (comparedTokens.size() == 1 && !comparedTokens.get(0).hasCorrelation()) {
				boolean wordEquals = etalonToken.wordEquals(comparedTokens.get(0));
				if (!wordEquals) {
					System.out.println("Word mismatch: expected " + etalonToken.getWord() + " found " + comparedTokens.get(0).getStringValue());
				}
			} else {
				if (comparedTokens.size() > 1) {
					conflictingCount++;
				}
				comparedTokens = comparedTokens.stream().filter(token -> token.getCorrelation() > 0).collect(Collectors.toList());
				if (comparedTokens.size() > 0) {
					boolean wordEquals = etalonToken.wordEquals(comparedTokens.get(0));
					if (!wordEquals) {
						System.out.println("Word mismatch: expected " + etalonToken.getWord() + " found " + comparedTokens.get(0).getShortStringValue());
					}
					for (WordFormToken comparedToken : comparedTokens) {
						incrementCount(comparedCounts, comparedToken.getPartOfSpeech());
						comparedCount++;
						List<Grammem> missedGrammems = tokenComparator.calculateMissed(etalonToken, comparedToken);
						if (!missedGrammems.isEmpty()) {
							System.out.println("Incorrect grammem set for token " + etalonToken);
							System.out.println("Wrong grammems: " + missedGrammems);
							wrongCount++;
						}
					}
				}
			}
			i++;
			j++;
			
		}
		printComparisonResults(comparedCounts, comparedCount, conflictingCount, wrongCount);
	}

	protected void printComparisonResults(Map<PartOfSpeech, Integer> comparedCounts, int comparedCount, int conflictingCount, int wrongCount) {
		System.out.println("** Totally compared " + comparedCount + " tokens **");
		System.out.println("** Has conflicting: " + conflictingCount + " tokens **");
		System.out.println(String.format("** Correct %1$,.2f percent tokens **", (conflictingCount - wrongCount) * 100.0 / conflictingCount));
		System.out.println("** Parts of speech ");
		comparedCounts.keySet().stream().sorted((part1, part2) -> {return part1.intId - part2.intId;}).forEach( part -> {
			System.out.println("** " + part.description + " = " + comparedCounts.get(part));
		});
	}

	private void incrementCount(Map<PartOfSpeech, Integer> comparedCounts, PartOfSpeech partOfSpeech) {
		if (partOfSpeech == null) {
			return;
		}
		Integer count = comparedCounts.get(partOfSpeech);
		if (count == null) {
			count = 1;
		} else {
			count++;
		}
		comparedCounts.put(partOfSpeech, count);
	}

	private int tryEtalonNeutralization(int i, List<SimplifiedToken> etalonTokens, WordFormToken wordFormToken) {
		i++;
		for (int k = i; k < i + MAX_NEUTRALIZATION_LOOKAHEAD && k < etalonTokens.size(); k++) {
			SimplifiedToken curToken = etalonTokens.get(k);
			if (curToken.wordEquals(wordFormToken)) {
				return k;
			}
		}
		return -1;
	}

	protected int getNextWordTokenIdx(List<IToken> tokens, int j, SimplifiedToken etalonToken) {
		j = skipNonWordTokens(tokens, j);
		if (j == -1) {
			return -1;
		}
		int lookahead = 0;
		WordFormToken token = (WordFormToken) tokens.get(j);
		while (j > -1 && lookahead < MAX_NEUTRALIZATION_LOOKAHEAD && !etalonToken.wordEquals(token)) {
			j++;
			lookahead++;
			j = skipNonWordTokens(tokens, j);
			token = (WordFormToken) tokens.get(j);
		}
		if (lookahead == MAX_NEUTRALIZATION_LOOKAHEAD) {
			return -1;
		}
		return j;
	}

	protected int skipNonWordTokens(List<IToken> tokens, int j) {
		while (j < tokens.size() && !(tokens.get(j) instanceof WordFormToken)) {			
			j++;
		}
		if (j == tokens.size()) {
			return -1;
		}
		return j;
	}
	
}
