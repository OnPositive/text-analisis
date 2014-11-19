package com.onpositive.text.analisys.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.WordFormParser;

public class WordFormParserTest extends TestCase{

	public void testWordFormParser() {
		
		PrimitiveTokenizer pt = new PrimitiveTokenizer();
		AbstractWordNet instance = WordNetProvider.getInstance();
		GrammarRelation[] possibleGrammarForms = instance.getPossibleGrammarForms("автоматический");
		TextElement[] possibleContinuations = instance.getPossibleContinuations(possibleGrammarForms[0].getWord());
		//ww.prepareWordSeqs();
		WordFormParser wfParser = new WordFormParser(instance);
		
		String str = "Сработал автоматический определитель номера. Чудовище село на ковёр-самолёт и полетело.";		
		List<IToken> tokens = pt.tokenize(str);		
		ArrayList<IToken> processed = wfParser.process(tokens);
		
		for(IToken t : processed){
			System.out.println(t.getStartPosition() + "-" + t.getEndPosition() + " " + TokenTypeResolver.getResolvedType(t) + " " + t.getStringValue());
		}
		System.out.println();
	}
	
}