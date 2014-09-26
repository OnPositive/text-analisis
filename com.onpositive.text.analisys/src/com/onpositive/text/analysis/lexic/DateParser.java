package com.onpositive.text.analysis.lexic;

import java.util.Set;
import java.util.Stack;

import com.onpositive.text.analysis.IToken;

public class DateParser extends AbstractParser {

	@Override
	protected Set<IToken> combineUnits(Stack<IToken> sample) {
		return null;
	}

	@Override
	protected int continuePush(Stack<IToken> sample) {
		return 0;
	}

	@Override
	protected boolean checkAndPrepare(IToken unit) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
