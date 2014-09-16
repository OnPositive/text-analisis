package com.onpositive.text.analysis.lexic;

import com.onpositive.text.analysis.AbstractToken;
import com.onpositive.text.analysis.IUnit;

public class SymbolToken extends AbstractToken {

	protected SymbolToken(String value, int tokenType, int startPosition, int endPosition) {
		super(tokenType, startPosition, endPosition);
		
		this.value = value.intern();
	}
	
	public SymbolToken(char ch, int tokenType, int startPosition, int endPosition) {
		super(tokenType, startPosition, endPosition);
		
		this.value = ("" + ch).intern();
	}

	private final String value;

	@Override
	public String getStringValue() {
		return value;
	}
}
