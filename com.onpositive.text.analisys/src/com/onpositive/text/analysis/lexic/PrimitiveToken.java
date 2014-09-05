package com.onpositive.text.analysis.lexic;

import com.onpositive.text.analysis.IUnit;

public class PrimitiveToken implements IUnit{	

	
	protected PrimitiveToken(String value, int tokenType, int startPosition, int endPosition)
	{
		this.value = value;
		this.tokenType = new int[]{ tokenType };
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}

	private final String value;
	
	private final int[] tokenType;
	
	private final int startPosition;
	
	private final int endPosition;
	
	
	@Override
	public String getStringValue() {		
		return value;
	}

	@Override
	public int[] getType() {
		return tokenType;
	}

	@Override
	public int getStartPosition() {
		return startPosition;
	}

	@Override
	public int getEndPosition() {
		return endPosition;
	}

	@Override
	public int getLength() {
		return endPosition - startPosition;
	}
	
	

}
