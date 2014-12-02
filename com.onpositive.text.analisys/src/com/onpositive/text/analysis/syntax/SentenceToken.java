package com.onpositive.text.analysis.syntax;

import java.util.List;

import com.onpositive.text.analysis.AbstractToken;
import com.onpositive.text.analysis.IToken;

public class SentenceToken extends AbstractToken{

	protected SentenceToken(int startPosition, int endPosition) {
		super(IToken.TOKEN_TYPE_SENTENCE, startPosition, endPosition);		
	}

	@Override
	public String getStringValue() {
		
		StringBuilder bld = new StringBuilder();
		List<IToken> children = getChildren();
		for(IToken t : children){
			bld.append(t.getStringValue()).append("\n");
		}
		return bld.toString();
	}

}
