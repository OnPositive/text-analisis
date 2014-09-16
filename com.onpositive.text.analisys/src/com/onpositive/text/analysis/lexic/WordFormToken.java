package com.onpositive.text.analysis.lexic;

import com.onpositive.semantic.words2.WordRelation;
import com.onpositive.text.analysis.AbstractToken;

public class WordFormToken extends AbstractToken {

	public WordFormToken(WordRelation wordRelation, int startPosition, int endPosition) {
		super(UNIT_TYPE_WORD_FORM, startPosition, endPosition);
		this.wordRelation = wordRelation;
	}
	
	private final WordRelation wordRelation;

	@Override
	public String getStringValue() {
		return wordRelation == null ? "null" : wordRelation.toString();
	}
}
