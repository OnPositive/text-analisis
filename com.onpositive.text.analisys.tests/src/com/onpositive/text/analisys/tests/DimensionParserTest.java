package com.onpositive.text.analisys.tests;

import java.util.List;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.composite.CompositeWordnet;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.NumericsParser;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.lexic.dimension.DimensionParser;
import com.onpositive.text.analysis.lexic.dimension.Unit;
import com.onpositive.text.analysis.lexic.dimension.UnitGroupParser;
import com.onpositive.text.analysis.lexic.dimension.UnitKind;
import com.onpositive.text.analysis.lexic.dimension.UnitParser;
import com.onpositive.text.analysis.lexic.scalar.ScalarParser;


public class DimensionParserTest extends ParserTest{
	
	private Unit squareKilometerUnit = new Unit("километр^2",UnitKind.AREA,1000*1000);
	
	private Unit kilometerUnit = new Unit("километр",UnitKind.SIZE,1000);
	
	private Unit meterUnit = new Unit("метр",UnitKind.SIZE,1);

	private Unit meterPerSecondUnit = new Unit("метр в секунду",UnitKind.SPEED,1);
	
	public DimensionParserTest() {
		super();
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
		setParsers(wfParser,scalarParser,numericsParser,unitParser,unitGroupParser,dimParser);
	}

	public void testBasic(){
		String str = "Проехал два км со скоростью 10 метров в секунду";		
		List<IToken> processed = process(str);		
		assertTestDimension(new Double[]{2.0,10.0}, new Unit[]{kilometerUnit,meterPerSecondUnit},processed );
	}
	

	public void testArea1(){
		String str = "Вскопал 2 км^2 земли.";		
		List<IToken> processed = process(str);
		assertTestDimension(2.0, squareKilometerUnit,processed);
	}
	
	public void testArea2(){
		String str = "Обработал 4 км² асфальта.";		
		List<IToken> processed = process(str);
		assertTestDimension(4.0,squareKilometerUnit,processed);
	}
}
