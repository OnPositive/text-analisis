package com.onpositive.text.analysis;

import java.util.List;

public class ParserComposition2 extends ParserComposition {
	
	public ParserComposition2(AbstractParser... parsers) {
		super(parsers);
		setHandleBoundsFalse();
	}


	public ParserComposition2(boolean isGloballyRecursive, AbstractParser... parsers) {
		super(isGloballyRecursive,parsers);
		setHandleBoundsFalse();
	}	
	
	
	private void setHandleBoundsFalse() {
		for(AbstractParser ap : this.parsers){
			ap.setHandleBounds(false);
		}
	}
	
	
	@Override
	public List<IToken> process(List<IToken> tokens) {
		// TODO Auto-generated method stub
		return super.process(tokens);
	}

}
