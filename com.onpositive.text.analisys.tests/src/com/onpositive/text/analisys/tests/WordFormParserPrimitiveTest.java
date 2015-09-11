package com.onpositive.text.analisys.tests;

import java.util.List;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.SymbolToken;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.lexic.WordFormToken;

import junit.framework.TestCase;

public class WordFormParserPrimitiveTest extends TestCase {
	
	public void test01() {
		checkForErrors("вести борьбу");
	}
	
	public void test02() {
		checkForErrors("надо бороться");
	}
	
	private void checkForErrors(String str) {
		List<IToken> wordFormTokens = getWordFormTokens(str);
		for (IToken token : wordFormTokens) {
			if (token instanceof SymbolToken) {
				continue;
			}
			if (!(token instanceof WordFormToken) ||
				((WordFormToken) token).getGrammarRelations().isEmpty() ||
				((WordFormToken) token).getChildren().size() > 1) {
				System.out.println("Ошибка в словаре - " + token);
			}
		}
	}
	
	private List<IToken> getWordFormTokens(String str) {
		PrimitiveTokenizer pt = new PrimitiveTokenizer();
		AbstractWordNet instance = WordNetProvider.getInstance();
		WordFormParser wfParser = new WordFormParser(instance);
		List<IToken> tokens = pt.tokenize(str);		
		List<IToken> processed = wfParser.process(tokens);
		return processed;
	}

}
