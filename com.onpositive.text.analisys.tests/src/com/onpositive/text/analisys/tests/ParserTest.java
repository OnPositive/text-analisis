package com.onpositive.text.analisys.tests;

import java.util.ArrayList;
import java.util.List;

import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.ParserComposition;
import com.onpositive.text.analysis.lexic.AbstractParser;
import com.onpositive.text.analysis.lexic.DimensionToken;
import com.onpositive.text.analysis.lexic.ScalarToken;
import com.onpositive.text.analysis.lexic.dimension.Unit;
import com.onpositive.text.analysis.syntax.SentenceToken;

import junit.framework.TestCase;

public class ParserTest extends TestCase {
	
	protected ParserComposition composition;
	

	protected void setParsers(AbstractParser... parsers){
		if(parsers==null||parsers.length==0){
			return;
		}
		else{
			this.composition = new ParserComposition(parsers);
		}
	}
	
	
	protected List<IToken> process(String str){
		List<IToken> processed = composition.parse(str);
		ArrayList<IToken> list = new ArrayList<>();
		for(IToken t : processed){
			if(t instanceof SentenceToken){
				list.addAll(t.getChildren());
			}
			else{
				list.add(t);
			}
		}
		printTokens(list);
		return list;
	}	
	
	
	protected static void printTokens(List<IToken> processed) {
		System.out.println();
		System.out.println("-----");
		
		for(IToken t : processed){
			System.out.println(t.getStartPosition() + "-" + t.getEndPosition() + " " + TokenTypeResolver.getResolvedType(t) + " " + t.getStringValue());
		}
	}
	
	protected static void assertTestDimension(double value, Unit unit,List<IToken> tk){
		boolean found=false;
		for (IToken z:tk){
			if (z instanceof DimensionToken){
				DimensionToken k=(DimensionToken) z;
				if (k.getValue()==value){
					Unit unit0 = k.getUnit();
					String shortName0 = unit0.getShortName().toLowerCase();
					String shortName = unit.getShortName().toLowerCase();
					if(shortName0.equals(shortName)){
						if(unit0.getKind()==unit.getKind()){
							found = true;
						}
					}
				}
			}
		}
		TestCase.assertTrue(found);
	}
	
	protected static void assertTestDimension(Double[] values, Unit[] units,List<IToken> tk) {
		int ind = 0 ;
		for (IToken z:tk){
			if (z instanceof DimensionToken){
				DimensionToken k=(DimensionToken) z;
				if (k.getValue()==values[ind]){
					Unit unit0 = k.getUnit();
					Unit unit = units[ind];
					String shortName = unit.getShortName().toLowerCase();
					String shortName0 = unit0.getShortName().toLowerCase();
					if(shortName0.equals(shortName)){
						if(unit0.getKind()==unit.getKind()){
							ind++;
						}
					}					
				}
			}
		}
		TestCase.assertTrue(ind==values.length);
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

}
