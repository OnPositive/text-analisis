package com.onpositive.text.analisys.tests;

import java.util.List;

import junit.framework.TestCase;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.composite.CompositeWordnet;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.ParserComposition;
import com.onpositive.text.analysis.lexic.DimensionToken;
import com.onpositive.text.analysis.lexic.NumericsParser;
import com.onpositive.text.analysis.lexic.ScalarToken;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.lexic.dimension.DimensionParser;
import com.onpositive.text.analysis.lexic.dimension.UnitGroupParser;
import com.onpositive.text.analysis.lexic.dimension.UnitParser;
import com.onpositive.text.analysis.lexic.scalar.ScalarParser;

public class NumericParserTest extends TestCase{

	ParserComposition composition;
	
	public NumericParserTest() {
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
	void assertTestScalar(double value,List<IToken>tk){
		boolean found=false;
		for (IToken z:tk){
			if (z instanceof ScalarToken){
				ScalarToken k=(ScalarToken) z;
				if (k.getValue()==value){
					found=true;
				}
			}
		}
		TestCase.assertTrue(found);
	}
	void printTokens(List<IToken> processed) {
		System.out.println();
		System.out.println("-----");
		
		for(IToken t : processed){
			System.out.println(t.getStartPosition() + "-" + t.getEndPosition() + " " + TokenTypeResolver.getResolvedType(t) + " " + t.getStringValue());
		}
	}
	
	public void testBasic(){
		String str = "Он ехал со скоростью двадцать два кмч";		
		List<IToken> processed=composition.parse(str);
		printTokens(processed);
		assertTestDimension(22.0, processed);
	}
	public void testThousand(){
		String str = "Миг-31 способен разгоняться до 3.5 тысяч кмч";		
		List<IToken> processed=composition.parse(str);
		printTokens(processed);
		assertTestDimension(3500, processed);
	}
	
	public void testMulti(){
		String str = "раз два три";		
		List<IToken> processed=composition.parse(str);
		printTokens(processed);
		assertTestScalar(1, processed);
		assertTestScalar(2, processed);
		assertTestScalar(3, processed);
	}
	
	public void testM2(){
		String str = "50 тысяч 222 кмч";		
		List<IToken> processed=composition.parse(str);
		printTokens(processed);
		assertTestDimension(50222, processed);
	}
	
	public void testM7(){
		String str = "двадцать кмч";		
		List<IToken> processed=composition.parse(str);
		printTokens(processed);
		assertTestDimension(20, processed);
	}
	
	public void testM8(){
		String str = "девяносто кмч";		
		List<IToken> processed=composition.parse(str);
		printTokens(processed);
		assertTestDimension(90, processed);
	}
	
	public void testM3(){
		String str = "50 два 34 двадцать два";		
		List<IToken> processed=composition.parse(str);
		printTokens(processed);
		assertTestScalar(50, processed);
		assertTestScalar(2, processed);
		assertTestScalar(34, processed);
		assertTestScalar(22, processed);
	}
	
	public void testM4(){
		String str = "трёхтысячный отряд";		
		List<IToken> processed=composition.parse(str);
		printTokens(processed);
		
	}
	
	public void testM5(){
		String str = "Население новосибирска составляет около полутора миллионов человек";		
		List<IToken> processed=composition.parse(str);
		printTokens(processed);
		assertTestScalar(1500000, processed);
	}
	
}
