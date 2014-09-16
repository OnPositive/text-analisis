package com.onpositive.text.analysis.lexic;

import java.util.List;
import java.util.Stack;

import com.onpositive.text.analysis.IUnit;

public class DateParser extends AbstractParser {

	@Override
	protected List<IUnit> combineUnits(Stack<IUnit> sample) {
		return null;
	}

	@Override
	protected boolean continueParsing(Stack<IUnit> sample) {
		return false;
	}

	
}
