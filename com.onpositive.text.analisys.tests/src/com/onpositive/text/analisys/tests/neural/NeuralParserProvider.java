package com.onpositive.text.analisys.tests.neural;

import com.onpositive.text.analysis.neural.NeuralParser;

public class NeuralParserProvider {
	
	private static NeuralParser parserInstance;
	
	private NeuralParserProvider() {
	}
	
	public static NeuralParser getParser() {
		if (parserInstance == null) {
			parserInstance = new NeuralParser();
		}
		return parserInstance;
	}
}
