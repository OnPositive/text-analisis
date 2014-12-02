package com.onpositive.text.analysis.syntax;

import java.util.List;

import com.onpositive.text.analysis.AbstractToken;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.WordFormToken;

public class SyntaxToken extends AbstractToken{

	public SyntaxToken(int tokenType, SyntaxToken mainGroup, int startPosition, int endPosition) {
		super(tokenType, startPosition, endPosition);
		this.mainGroup = mainGroup;
	}

	protected SyntaxToken mainGroup;
	
	@Override
	public String getStringValue() {
		
		StringBuilder bld = new StringBuilder();
		List<IToken> children = getChildren();
		for(IToken t : children){
			bld.append(t.getStringValue()).append(" ");
		}
		return bld.toString();
	}

	public WordFormToken getMainWord() {		
		SyntaxToken token = this;
		while(!(token instanceof WordFormToken)){
			token = token.getMainGroup();
		}
		return (WordFormToken) token;
	}
	
	SyntaxToken getMainGroup(){
		return mainGroup;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((mainGroup == null||mainGroup==this) ? 0 : mainGroup.hashCode());
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
		SyntaxToken other = (SyntaxToken) obj;
		if (mainGroup == null) {
			if (other.mainGroup != null)
				return false;

		} else{
			if(other.mainGroup==null){
				return false;
			}
			if(mainGroup.getType() == IToken.TOKEN_TYPE_WORD_FORM){
				if(other.mainGroup.getType() != IToken.TOKEN_TYPE_WORD_FORM){
					return false;
				}
			}
			else if (!mainGroup.equals(other.mainGroup))		
				return false;
		}
		return true;
	}


	
	
}
