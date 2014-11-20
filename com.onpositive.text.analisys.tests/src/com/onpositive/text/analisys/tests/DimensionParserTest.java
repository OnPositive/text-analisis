package com.onpositive.text.analisys.tests;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.onpositive.semantic.wordnet.edit.IWordNetEditInterface;
import com.onpositive.semantic.wordnet.edit.WordNetPatch;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.lexic.dimension.DimensionParser;
import com.onpositive.text.analysis.lexic.dimension.UnitGroupParser;
import com.onpositive.text.analysis.lexic.dimension.UnitParser;
import com.onpositive.text.analysis.lexic.scalar.ScalarParser;

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
		AbstractWordNet wordNet = editable.getWordNet();
		WordFormParser wfParser = new WordFormParser(wordNet);
		ScalarParser scalarParser = new ScalarParser();
		UnitParser unitParser = new UnitParser(wordNet);
		UnitGroupParser unitGroupParser = new UnitGroupParser(wordNet);
		DimensionParser dimParser = new DimensionParser();		
		
		String str = "Проехал два км со скоростью 10 метров в секунду. Вскопал 2 км^2 земли. Обработал 4 км² асфальта.";		
		List<IToken> tokens = pt.tokenize(str);		
		ArrayList<IToken> processed0 = wfParser.process(tokens);
		ArrayList<IToken> processed1 = scalarParser.process(processed0);
		ArrayList<IToken> processed2 = unitParser.process(processed1);
		ArrayList<IToken> processed3 = unitGroupParser.process(processed2);
		ArrayList<IToken> processed4 = dimParser.process(processed3);
		
		
		for(IToken t : processed4){
			System.out.println(t.getStartPosition() + "-" + t.getEndPosition() + " " + TokenTypeResolver.getResolvedType(t) + " " + t.getStringValue());
		}
		System.out.println();
	}

}
