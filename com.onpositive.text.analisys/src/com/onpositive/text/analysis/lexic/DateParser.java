package com.onpositive.text.analysis.lexic;

import java.util.Set;
import java.util.Stack;

import com.onpositive.text.analysis.IUnit;

public class DateParser extends AbstractParser {

	@Override
	protected Set<IUnit> combineUnits(Stack<IUnit> sample) {
		return null;
	}

	@Override
	protected int continuePush(Stack<IUnit> sample) {
		return 0;
	}

	@Override
	protected boolean checkAndPrepare(IUnit unit) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
