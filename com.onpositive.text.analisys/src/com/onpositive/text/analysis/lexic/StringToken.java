package com.onpositive.text.analysis.lexic;

import com.onpositive.text.analysis.AbstractToken;
import com.onpositive.text.analysis.IUnit;

public class StringToken extends AbstractToken{	

	protected StringToken( String value, int tokenType,	int startPosition, int endPosition) {
		super(tokenType, startPosition, endPosition);
		
		this.value = value;
	}


	private final String value;
	
	
	@Override
	public String getStringValue() {		
		return value;
	}

}
