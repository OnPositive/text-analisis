package com.onpositive.text.analysis;

import java.util.List;

import com.onpositive.text.analysis.lexic.AbstractParser;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;

public class ParserComposition {

	public ParserComposition(AbstractParser... parsers) {
		super();
		this.parsers = parsers;
	}

	AbstractParser[]parsers;
	PrimitiveTokenizer pt=new PrimitiveTokenizer();
	
	public List<IToken>parse(String str){
		List<IToken> tokenize = pt.tokenize(str);
		for (AbstractParser p:parsers){
			tokenize=p.process(tokenize);
		}
		return tokenize;
	}
}
