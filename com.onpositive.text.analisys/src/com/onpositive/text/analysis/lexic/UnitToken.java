package com.onpositive.text.analysis.lexic;

import com.onpositive.text.analysis.AbstractToken;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.dimension.Unit;

public class UnitToken extends AbstractToken {

	public UnitToken(Unit unit, int startPosition, int endPosition) {
		super(IToken.TOKEN_TYPE_UNIT, startPosition, endPosition);
		this.unit = unit;
	}
	
	private Unit unit;

	@Override
	public String getStringValue() {
		return unit.toString();
	}

	public Unit getUnit() {
		return unit;
	}

}
