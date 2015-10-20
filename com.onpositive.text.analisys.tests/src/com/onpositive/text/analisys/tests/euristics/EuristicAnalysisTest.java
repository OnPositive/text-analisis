package com.onpositive.text.analisys.tests.euristics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import junit.framework.TestCase;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
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

public class EuristicAnalysisTest  extends TestCase{
	
	private static final int MAX_NEUTRALIZATION_LOOKAHEAD = 10; 
	
	public void test00() {
		List<IToken> processed = getWordFormTokens("с трудом прошел");
		System.out.println(processed);
	}
	
	public void test01() {
		ParsedTokensLoader loader = new ParsedTokensLoader(EuristicAnalysisTest.class.getResourceAsStream("corpora_parsed.xml"));
		List<SimplifiedToken> etalonTokens = loader.getTokens();
		String text = loader.getInitialText();
		EuristicAnalyzingParser euristicAnalyzingParser = configureDefaultAnalyzer(RuleSet.getFullRulesList());
		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens(text));
		compare(etalonTokens,processed);
	}
	
	public void test02() {
		ParsedTokensLoader loader = new ParsedTokensLoader(EuristicAnalysisTest.class.getResourceAsStream("3163.xml"));
		List<SimplifiedToken> etalonTokens = loader.getTokens();
		String text = loader.getInitialText();
		EuristicAnalyzingParser euristicAnalyzingParser = configureDefaultAnalyzer(RuleSet.getFullRulesList());
		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens(text));
		compare(etalonTokens,processed);
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
		while (i < etalonTokens.size() && j < tokens.size()) {
			SimplifiedToken etalonToken = etalonTokens.get(i);
			List<WordFormToken> comparedTokens = new ArrayList<WordFormToken>();
			j = getNextWordTokenIdx(tokens, j, etalonToken);
			if (j == -1) {
				return;
			}
			WordFormToken wordFormToken = (WordFormToken) tokens.get(j);
			if (!etalonToken.wordEquals(wordFormToken)) {
				i = tryEtalonNeutralization(i, etalonTokens, wordFormToken);
				if (i == -1) {
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
				comparedTokens = comparedTokens.stream().filter(token -> token.getCorrelation() > 0).collect(Collectors.toList());
				if (comparedTokens.size() > 0) {
					boolean wordEquals = etalonToken.wordEquals(comparedTokens.get(0));
					if (!wordEquals) {
						System.out.println("Word mismatch: expected " + etalonToken.getWord() + " found " + comparedTokens.get(0).getShortStringValue());
					}
					for (WordFormToken comparedToken : comparedTokens) {
						List<Grammem> missedGrammems = etalonToken.calculateMissed(comparedToken);
						if (!missedGrammems.isEmpty()) {
							System.out.println("Incorrect grammem set for token " + etalonToken);
							System.out.println("Wrong grammems: " + missedGrammems);
						}
					}
				}
			}
			i++;
			j++;
			
		}
		
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
		}
		return j;
	}

	protected int skipNonWordTokens(List<IToken> tokens, int j) {
		while (j < tokens.size() && !(tokens.get(j) instanceof WordFormToken)) {
			if (Character.isLetter(tokens.get(j).getStringValue().charAt(0))) {
				System.out.println(tokens.get(j).getStringValue());
			}
			j++;
		}
		if (j == tokens.size()) {
			return -1;
		}
		return j;
	}
	
}
