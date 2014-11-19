package com.onpositive.text.analisys.tests;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.onpositive.semantic.wordnet.edit.IWordNetEditInterface;
import com.onpositive.semantic.wordnet.edit.WordNetPatch;
import com.onpositive.text.analysis.lexic.dimension.Unit;
import com.onpositive.text.analysis.lexic.dimension.UnitsProvider;

import junit.framework.TestCase;


public class DimensionParserTest extends TestCase{
	

	public static void main(String[] args){
		new DimensionParserTest().test();
	}
	
	
	public void test() {
		
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
		TextElement wordElement = editable.getWordNet().getWordElement("_ALL_DIMENSION_UNITS".toLowerCase());
		TextElement wordElement2 = editable.getWordNet().getWordElement("_UNITs_SIZE".toLowerCase());
		UnitsProvider unitsProvider = new UnitsProvider(editable.getWordNet());
		List<Unit> unit = unitsProvider.getUnit("км/ч");
		
		System.out.println("done");
	}

}
