package com.onpositive.text.analysis.lexic;

import com.onpositive.text.analysis.AbstractToken;
import com.onpositive.text.analysis.IToken;


public class DimensionToken extends AbstractToken {

	protected DimensionToken(IToken scalar, Unit unit, int startPosition, int endPosition) {
		super(IToken.TOKEN_TYPE_DIMENSION, startPosition, endPosition);
		this.scalar = scalar;
		this.unit = unit;
	}

	private final IToken scalar;
	
	private final Unit unit;
	
	@Override
	public String getStringValue() {
		
		return scalar.getStringValue()+unit.toString();
	}

}
