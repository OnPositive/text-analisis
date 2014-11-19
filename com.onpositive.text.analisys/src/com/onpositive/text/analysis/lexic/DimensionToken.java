package com.onpositive.text.analysis.lexic;

import com.onpositive.text.analysis.AbstractToken;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.dimension.Unit;


public class DimensionToken extends AbstractToken {

	public DimensionToken(IToken scalar, Unit unit, int startPosition, int endPosition) {
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
