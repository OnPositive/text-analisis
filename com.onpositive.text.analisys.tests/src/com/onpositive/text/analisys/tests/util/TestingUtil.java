package com.onpositive.text.analisys.tests.util;

import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.onpositive.text.analisys.tests.TokenTypeResolver;
import com.onpositive.text.analysis.Euristic;
import com.onpositive.text.analysis.EuristicAnalyzingParser;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.filtering.AbbreviationsFilter;
import com.onpositive.text.analysis.filtering.AdditionalPartsPresetFilter;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.SentenceSplitter;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.lexic.scalar.ScalarParser;
import com.onpositive.text.analysis.syntax.SyntaxToken;
import com.onpositive.text.analysis.utils.MorphologicUtils;

public class TestingUtil {
	
	private static final double CORRELATION_THRESHOLD = 0.01;

	public static void checkHas(List<IToken> result, int i, Grammem wantedGrammem) {
		TestCase.assertTrue(i < result.size() && (result.get(i) instanceof SyntaxToken));
		result = MorphologicUtils.getWithNoConflicts(result);
		SyntaxToken wordFormToken = (SyntaxToken) result.get(i);
		if (wordFormToken.getCorrelation() > CORRELATION_THRESHOLD && wordFormToken.hasGrammem(wantedGrammem)) {
			return;
		}
		List<IToken> conflicts = wordFormToken.getConflicts();
		for (IToken conflictingToken : conflicts) {
			if (conflictingToken.getCorrelation() > CORRELATION_THRESHOLD && 
				conflictingToken instanceof SyntaxToken && 
				((SyntaxToken) conflictingToken).hasGrammem(wantedGrammem)) {
				return;
			}	
		}
		throw new AssertionFailedError("Assertion failed, " + result.get(i).getShortStringValue() + " isn't " + wantedGrammem);
	}
	
	public static List<IToken> getWordFormTokens(String str) {
		PrimitiveTokenizer pt = new PrimitiveTokenizer();
		AbstractWordNet instance = WordNetProvider.getInstance();
		WordFormParser wfParser = new WordFormParser(instance);
		wfParser.setIgnoreCombinations(true);
		List<IToken> tokens = pt.tokenize(str);		
		List<IToken> processed = wfParser.process(tokens);
		ScalarParser scalarParser = new ScalarParser();
		processed = scalarParser.process(processed);
		return processed;
	}
	
	public static List<IToken> getSentences(String str) {
		return new SentenceSplitter().split(getWordFormTokens(str));
	}
	
	public static EuristicAnalyzingParser configureDefaultAnalyzer(List<Euristic> euristics) {
		EuristicAnalyzingParser euristicAnalyzingParser = new EuristicAnalyzingParser(euristics);
		euristicAnalyzingParser.addTokenFilter(new AbbreviationsFilter());
		euristicAnalyzingParser.addTokenFilter(new AdditionalPartsPresetFilter());
		return euristicAnalyzingParser;
	}
	
	public static void printChain(String str, List<IToken> chain) {
		System.out.println("//============Результаты разбора, строка '" + str +  "' ==================================================");
		printChain(chain);
		System.out.println();
	}


	public static void printChain(List<IToken> chain) {
		for (IToken token : chain) {
			if (token.hasConflicts() && Math.abs(token.getCorrelation()) < CORRELATION_THRESHOLD) {
				boolean matchedAny = false;
				List<IToken> conflicts = token.getConflicts();
				for (IToken conflictToken : conflicts) {
					if (conflictToken.getCorrelation() > CORRELATION_THRESHOLD) {
						matchedAny = true;
						break;
					}
				}
				if (!matchedAny) {
					System.out.println("Результатов не найдено");
					return;
				}
			}
			
		}
		for (int i = 0; i < chain.size(); i++) {
			IToken token = chain.get(i);
			if (hasConflicts(token)) {
				System.out.print(" Конфликт[ ");
				if (token.getCorrelation() > CORRELATION_THRESHOLD) {
					printToken(token);
				}
				List<IToken> conflicts = token.getConflicts();
				conflicts.stream().filter(conflictToken -> conflictToken.getCorrelation() > CORRELATION_THRESHOLD).forEach(curToken -> {
					System.out.print(" "); printToken(curToken);
				});
				System.out.print("]");
			} else {
				printToken(getValidToken(token));
			}
			i += getConflictingCount(token);
		}
	}

	protected static int getConflictingCount(IToken token) {
		if (token.getConflicts() == null) {
			return 0;
		}
		return token.getConflicts().size();
	}

	private static IToken getValidToken(IToken token) {
		if (token.getCorrelation() > CORRELATION_THRESHOLD || !token.hasConflicts()) {
			return token;
		}
		List<IToken> conflicts = token.getConflicts();
		for (IToken conflictToken : conflicts) {
			if (conflictToken.getCorrelation() > CORRELATION_THRESHOLD) {
				return conflictToken;
			}
		}
		return null;
	}

	private static boolean hasConflicts(IToken token) {
		if (!token.hasConflicts())
			return false;
		int initialCount = token.getCorrelation() > CORRELATION_THRESHOLD ? 1 : 0;
		List<IToken> conflicts = token.getConflicts();
		long count = conflicts.stream().filter(conflictToken -> conflictToken.getCorrelation() > CORRELATION_THRESHOLD).count();
		return initialCount + count > 1;
	}
	
	private static void printToken(IToken token) {
		System.out.print(token.getStartPosition() + "-" + token.getEndPosition() + " " + TokenTypeResolver.getResolvedType(token) + " " + token.getStringValue()+ " ");
	}
}
