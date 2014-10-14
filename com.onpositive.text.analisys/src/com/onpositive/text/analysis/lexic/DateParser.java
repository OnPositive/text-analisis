package com.onpositive.text.analysis.lexic;

import java.util.Set;
import java.util.Stack;

import com.onpositive.text.analysis.IToken;

public class DateParser extends AbstractParser {

	@Override
	protected void combineUnits(Stack<IToken> sample, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens){
		
	}

	@Override
	protected int continuePush(Stack<IToken> sample) {
		if(sample.size()==1){
			
		}
		else{
			
		}		
		return 1;
	}

	@Override
	protected boolean checkAndPrepare(IToken unit) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
