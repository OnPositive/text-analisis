package com.onpositive.text.analysis.lexic;

import java.util.Collection;
import java.util.List;

import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.dimension.Unit;
import com.onpositive.text.analysis.syntax.SyntaxToken;

public class UnitToken extends SyntaxToken {

	public UnitToken(Unit unit, SyntaxToken mainGroup, Collection<GrammemSet> grammemSets, int startPosition, int endPosition) {
		super(IToken.TOKEN_TYPE_UNIT, mainGroup, grammemSets, startPosition, endPosition);
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
	public List<GrammemSet> getGrammemSets() {
		if(this.mainGroup!=null){
			return super.getGrammemSets();
		}
		else{
			return uniformGrammems;
		}
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
