package com.onpositive.text.analisys.tests;

import java.util.ArrayList;
import java.util.List;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.composite.CompositeWordnet;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.NumericsParser;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.lexic.dimension.DimensionParser;
import com.onpositive.text.analysis.lexic.dimension.UnitGroupParser;
import com.onpositive.text.analysis.lexic.dimension.UnitParser;
import com.onpositive.text.analysis.lexic.scalar.ScalarParser;

import junit.framework.TestCase;

public class NumericParserTest extends TestCase{

	public void testBasic(){
		CompositeWordnet wn=new CompositeWordnet();
		wn.addUrl("/numerics.xml");
		wn.addUrl("/dimensions.xml");
		wn.prepare();
		PrimitiveTokenizer pt = new PrimitiveTokenizer();
		AbstractWordNet wordNet = wn;
		WordFormParser wfParser = new WordFormParser(wordNet);
		ScalarParser scalarParser = new ScalarParser();
		UnitParser unitParser = new UnitParser(wordNet);
		UnitGroupParser unitGroupParser = new UnitGroupParser(wordNet);
		DimensionParser dimParser = new DimensionParser();		
		
		String str = "Он ехал со скоростью двадцать два кмч";		
		List<IToken> tokens = pt.tokenize(str);		
		ArrayList<IToken> processed0 = wfParser.process(tokens);
		ArrayList<IToken> processed1 = scalarParser.process(processed0);
		processed1=new NumericsParser(wordNet).process(processed1);
		ArrayList<IToken> processed2 = unitParser.process(processed1);
		ArrayList<IToken> processed3 = unitGroupParser.process(processed2);
		ArrayList<IToken> processed4 = dimParser.process(processed3);
		for(IToken t : processed4){
			System.out.println(t.getStartPosition() + "-" + t.getEndPosition() + " " + TokenTypeResolver.getResolvedType(t) + " " + t.getStringValue());
		}
		System.out.println();
	}
}
