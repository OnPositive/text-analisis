package com.onpositive.text.analisys.tests;

import java.util.ArrayList;
import java.util.List;

import com.onpositive.semantic.words2.SimpleWordNet;
import com.onpositive.semantic.words2.WordNet;
import com.onpositive.semantic.words2.WordNetProvider;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.WordFormParser;

public class WordFormParserTest {

	public static void main(String[] args) {
		PrimitiveTokenizer pt = new PrimitiveTokenizer();
		WordNet instance = WordNetProvider.getInstance();
		SimpleWordNet ww=(SimpleWordNet) instance;
		//ww.prepareWordSeqs();
		WordFormParser wfParser = new WordFormParser(instance);
		doProcessString(pt, wfParser);		
		
	}

	private static ArrayList<IToken> doProcessString(PrimitiveTokenizer pt,WordFormParser wfParser) {
		String str = "Чудовище село на ковёр-самолёт и полетело.";		
		List<IToken> tokens = pt.tokenize(str);		
		ArrayList<IToken> processed = wfParser.process(tokens);
		return processed;
	}
	
}
