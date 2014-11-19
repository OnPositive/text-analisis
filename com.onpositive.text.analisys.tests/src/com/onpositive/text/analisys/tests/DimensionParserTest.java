package com.onpositive.text.analisys.tests;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.onpositive.semantic.wordnet.edit.IWordNetEditInterface;
import com.onpositive.semantic.wordnet.edit.WordNetPatch;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.lexic.dimension.Unit;
import com.onpositive.text.analysis.lexic.dimension.UnitsProvider;

import junit.framework.TestCase;


public class DimensionParserTest extends TestCase{
	
	
	public void testDimensionParser() {
		
		try {
			URL is = Thread.currentThread().getContextClassLoader().getResource("tst.xml");			
			InputStreamReader isr = new InputStreamReader(is.openStream(),"UTF-8");
			WordNetPatch parse = WordNetPatch.parse(isr);
//			TestCase.assertEquals(14, parse.size());
			IWordNetEditInterface editable = WordNetProvider.editable(WordNetProvider.getInstance());
			TextElement wordElement3 = editable.getWordNet().getWordElement("метр");
			TestCase.assertTrue(wordElement3!=null);
			TextElement[] possibleContinuations = editable.getWordNet().getPossibleContinuations(wordElement3);
			parse.execute(editable);
			
			doTest(editable);			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	private void doTest(IWordNetEditInterface editable) {
		PrimitiveTokenizer pt = new PrimitiveTokenizer();
		WordFormParser wfParser = new WordFormParser(editable.getWordNet());
		
		String str = "Проехал два километра со скоростью 50 километров в час.";		
		List<IToken> tokens = pt.tokenize(str);		
		ArrayList<IToken> processed = wfParser.process(tokens);
		
		for(IToken t : processed){
			System.out.println(t.getStartPosition() + "-" + t.getEndPosition() + " " + TokenTypeResolver.getResolvedType(t) + " " + t.getStringValue());
		}
		System.out.println();
	}

}
