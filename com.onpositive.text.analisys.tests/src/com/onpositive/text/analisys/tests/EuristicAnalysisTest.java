package com.onpositive.text.analisys.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.onpositive.text.analysis.Euristic;
import com.onpositive.text.analysis.EuristicAnalyzingParser;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.filtering.AbbreviationsFilter;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.rules.RuleSet;

import junit.framework.TestCase;

public class EuristicAnalysisTest  extends TestCase{
	
	public void test00() {
		InputStream resourceAsStream = getClass().getResourceAsStream("text1.txt");
		try {
			String content = new Scanner(resourceAsStream).useDelimiter("\\Z").next();
			ParsedTokensLoader loader = new ParsedTokensLoader(EuristicAnalysisTest.class.getResourceAsStream("corpora_parsed.xml"));
			List<SimplifiedToken> etalonTokens = loader.getTokens();
			EuristicAnalyzingParser euristicAnalyzingParser = configureDefaultAnalyzer(RuleSet.getFullRulesList());
			List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens(content));
			compare(etalonTokens,processed);
		} catch (Exception e) {
		} finally {
			if (resourceAsStream != null) {
				try {resourceAsStream.close();} catch (IOException e) {}
			}
		}
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
			while (j < tokens.size() && !(tokens.get(j) instanceof WordFormToken)) {
				j++;
			}
			if (j == tokens.size()) {
				return;
			}
			WordFormToken wordFormToken = (WordFormToken) tokens.get(j);
			comparedTokens.add(wordFormToken);
			List<IToken> conflicts = wordFormToken.getConflicts();
			if (conflicts != null) {
				comparedTokens.addAll((Collection<? extends WordFormToken>) conflicts);
				j += conflicts.size();
			}
			if (comparedTokens.size() == 1 && !comparedTokens.get(0).hasCorrelation()) {
				boolean wordEquals = etalonToken.wordEquals(comparedTokens.get(0));
				if (!wordEquals) {
					System.out.println("Word mismatch: expeected " + etalonToken.getWord() + " found " + comparedTokens.get(0).getStringValue());
				}
			} else {
				comparedTokens = comparedTokens.stream().filter(token -> token.getCorrelation() > 0).collect(Collectors.toList());
				if (comparedTokens.size() > 0) {
					boolean wordEquals = etalonToken.wordEquals(comparedTokens.get(0));
					if (!wordEquals) {
						System.out.println("Word mismatch: expeected " + etalonToken.getWord() + " found " + comparedTokens.get(0).getShortStringValue());
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
	
}
