package com.onpositive.text.analisys.tests;

import java.util.List;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.composite.CompositeWordnet;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.ParserComposition;
import com.onpositive.text.analysis.lexic.DimensionToken;
import com.onpositive.text.analysis.lexic.NumericsParser;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.lexic.dimension.DimensionParser;
import com.onpositive.text.analysis.lexic.dimension.UnitGroupParser;
import com.onpositive.text.analysis.lexic.dimension.UnitParser;
import com.onpositive.text.analysis.lexic.scalar.ScalarParser;

import junit.framework.TestCase;


public class DimensionParserTest extends TestCase{
	
	ParserComposition composition;
	
	public DimensionParserTest() {
		CompositeWordnet wn=new CompositeWordnet();
		wn.addUrl("/numerics.xml");
		wn.addUrl("/dimensions.xml");
		wn.prepare();
		AbstractWordNet wordNet = wn;
		WordFormParser wfParser = new WordFormParser(wordNet);
		ScalarParser scalarParser = new ScalarParser();
		UnitParser unitParser = new UnitParser(wordNet);
		UnitGroupParser unitGroupParser = new UnitGroupParser(wordNet);
		DimensionParser dimParser = new DimensionParser();		
		NumericsParser numericsParser = new NumericsParser(wn);
		composition=new ParserComposition(wfParser,scalarParser,numericsParser,unitParser,unitGroupParser,dimParser);
	}
	
	void printTokens(List<IToken> processed) {
		System.out.println();
		System.out.println("-----");
		
		for(IToken t : processed){
			System.out.println(t.getStartPosition() + "-" + t.getEndPosition() + " " + TokenTypeResolver.getResolvedType(t) + " " + t.getStringValue());
		}
	}
	
	void assertTestDimension(double value,List<IToken>tk){
		boolean found=false;
		for (IToken z:tk){
			if (z instanceof DimensionToken){
				DimensionToken k=(DimensionToken) z;
				if (k.getValue()==value){
					found=true;
				}
			}
		}
		TestCase.assertTrue(found);
	}
	
	private void assertTestDimension(Double[] values, List<IToken> tk) {
		int ind = 0 ;
		for (IToken z:tk){
			if (z instanceof DimensionToken){
				DimensionToken k=(DimensionToken) z;
				if (k.getValue()==values[ind]){
					ind++;
				}
			}
		}
		TestCase.assertTrue(ind==values.length);
	}
	
	public void testBasic(){
		String str = "Проехал два км со скоростью 10 метров в секунду";		
		List<IToken> processed=composition.parse(str);
		printTokens(processed);
		assertTestDimension(new Double[]{2.0,10.0}, processed);
	}
	

	public void testArea1(){
		String str = "Вскопал 2 км^2 земли.";		
		List<IToken> processed=composition.parse(str);
		printTokens(processed);
		assertTestDimension(2.0, processed);
	}
	
	public void testArea2(){
		String str = "Обработал 4 км² асфальта.";		
		List<IToken> processed=composition.parse(str);
		printTokens(processed);
		assertTestDimension(4.0, processed);
	}
}
