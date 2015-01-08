package com.onpositive.text.analysis;

import java.util.List;

import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;

public class ParserComposition {

	public ParserComposition(AbstractParser... parsers) {
		super();
		this.parsers = parsers;
	}
	
	public ParserComposition(boolean isGloballyRecursive, AbstractParser... parsers) {
		super();
		this.parsers = parsers;
		this.isGloballyRecursive = isGloballyRecursive;
	}

	private AbstractParser[]parsers;
	
	private PrimitiveTokenizer tokenizer=new PrimitiveTokenizer();
	
	private boolean isGloballyRecursive = false;
	
	public PrimitiveTokenizer getTokenizer() {
		return tokenizer;
	}

	public void setTokenizer(PrimitiveTokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	public List<IToken>parse(String str){
		setText(str);
		List<IToken> tokenize = tokenizer.tokenize(str);		
		tokenize = process(tokenize);
		return tokenize;
	}

	public void setText(String str) {
		for (AbstractParser p:parsers){
			p.setText(str);			
		}
	}

	public List<IToken> process(List<IToken> tokens) {
		
		boolean globalTriggered = true;
		boolean localTriggered;
		
		while(globalTriggered){
			
			globalTriggered = false;
			for (AbstractParser parser : parsers){
				do{
					tokens = applyParser(tokens, parser);
					localTriggered = parser.hasTriggered();
					globalTriggered |= localTriggered;
				}
				while(localTriggered&&parser.isRecursive());
			}
			if(!isGloballyRecursive){
				break;
			}
		}
		return tokens;
	}
	
	private final List<IToken> applyParser(List<IToken> tokens,	AbstractParser parser) {
		parser.resetTrigger();
		List<IToken> result = parser.process(tokens);
		return result;
	}
}
