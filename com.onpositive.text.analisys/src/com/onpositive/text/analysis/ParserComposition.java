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
	PrimitiveTokenizer tokenizer=new PrimitiveTokenizer();
	
	public PrimitiveTokenizer getTokenizer() {
		return tokenizer;
	}

	public void setTokenizer(PrimitiveTokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	public List<IToken>parse(String str){
		List<IToken> tokenize = tokenizer.tokenize(str);
		tokenize = process(tokenize);
		return tokenize;
	}

	public List<IToken> process(List<IToken> tokenize) {
		for (AbstractParser p:parsers){
			tokenize=p.process(tokenize);
		}
		return tokenize;
	}
}
