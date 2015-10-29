package com.onpositive.text.analisys.tests.neural;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Test;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.SentenceSplitter;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.neural.NeuralParser;
import com.onpositive.text.analysis.syntax.SyntaxToken;
import com.onpositive.text.analysis.utils.MorphologicUtils;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class NeuralTest extends TestCase {
	
private static final double CORRELATION_THRESHOLD = 0.1;

//	@Test
//	public void test00() throws Exception {
//		new Trainer().testEncog();
//	}
	
	@Test
	public void test01() throws FileNotFoundException {
		basicNeuralTest("ложечка витая");
	}
	
	@Test
	public void test02() throws FileNotFoundException {
		List<IToken> result = basicNeuralTest("шестьдесят минут прошло");
		checkHas(result, 1, PartOfSpeech.NOUN);
		checkHas(result, 2, PartOfSpeech.VERB);
	}
	
	@Test
	public void test03() throws FileNotFoundException {
		List<IToken> result = basicNeuralTest("не покупать белил совсем");
		checkHas(result, 2, PartOfSpeech.NOUN);
	}
	
	private void checkHas(List<IToken> result, int i, Grammem wantedGrammem) {
		assertTrue(i < result.size() && (result.get(i) instanceof SyntaxToken));
		result = MorphologicUtils.getWithNoConflicts(result);
		WordFormToken wordFormToken = (WordFormToken) result.get(i);
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

	private List<IToken> basicNeuralTest(String str) throws FileNotFoundException {
		NeuralParser neuralParser = new NeuralParser();
		return neuralParser.process(getWordFormTokens(str));
	}
	
	private List<IToken> getWordFormTokens(String str) {
		PrimitiveTokenizer pt = new PrimitiveTokenizer();
		AbstractWordNet instance = WordNetProvider.getInstance();
		WordFormParser wfParser = new WordFormParser(instance);
		wfParser.setIgnoreCombinations(true);
		List<IToken> tokens = pt.tokenize(str);		
		List<IToken> processed = wfParser.process(tokens);
		return processed;
	}
	
	private List<IToken> getSentences(String str) {
		return new SentenceSplitter().split(getWordFormTokens(str));
	}

}
