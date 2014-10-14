package com.onpositive.text.analysis.lexic;

import com.onpositive.text.analysis.AbstractToken;

public class ScalarToken extends AbstractToken {
	
	public ScalarToken(double value, int startPosition, int endPosition) {
		super(TOKEN_TYPE_SCALAR, startPosition, endPosition);
		this.value1 = value;
		this.value2 = Integer.MIN_VALUE;
		this.isFracture = false;
		this.isDecimal = false;
	}
	
	public ScalarToken(int value1, int value2, boolean isDecimal, int startPosition, int endPosition) {
		super(TOKEN_TYPE_SCALAR, startPosition, endPosition);
		this.value1 = value1;
		this.value2 = value2;
		this.isFracture = true;
		this.isDecimal = isDecimal;
	}
	private final boolean isFracture;
	
	private final boolean isDecimal;

	private final double value1;
	
	private final double value2;

	@Override
	public String getStringValue() {
		
		if(isFracture()){
			if(isDecimal){
				return "" +value1 + "." + value2;
			}
			else{
				return "" +value1 + "/" + value2;
			}
		}
		else{
			return "" + value1;			
		}		
	}

	public double getValue1() {
		return value1;
	}

	public double getValue2() {
		return value2;
	}

	public boolean isFracture() {
		return isFracture;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isDecimal ? 1231 : 1237);
		result = prime * result + (isFracture ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(value1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(value2);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScalarToken other = (ScalarToken) obj;
		if (isDecimal != other.isDecimal)
			return false;
		if (isFracture != other.isFracture)
			return false;
		if (Double.doubleToLongBits(value1) != Double
				.doubleToLongBits(other.value1))
			return false;
		if (Double.doubleToLongBits(value2) != Double
				.doubleToLongBits(other.value2))
			return false;
		return true;
	}

}
