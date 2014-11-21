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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
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
		UnitToken other = (UnitToken) obj;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		return true;
	}

}
