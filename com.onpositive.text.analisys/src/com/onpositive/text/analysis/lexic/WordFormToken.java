package com.onpositive.text.analysis.lexic;

import com.onpositive.semantic.words2.WordRelation;
import com.onpositive.text.analysis.AbstractToken;

public class WordFormToken extends AbstractToken {

	public WordFormToken(WordRelation wordRelation, int startPosition, int endPosition) {
		super(TOKEN_TYPE_WORD_FORM, startPosition, endPosition);
		this.wordRelation = wordRelation;
	}
	
	private final WordRelation wordRelation;

	@Override
	public String getStringValue() {
		return wordRelation == null ? "null" : wordRelation.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((wordRelation == null) ? 0 : wordRelation.hashCode());
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
		WordFormToken other = (WordFormToken) obj;
		if (wordRelation == null) {
			if (other.wordRelation != null)
				return false;
		} else if (wordRelation.getWord() != other.wordRelation.getWord())
			return false;
		return true;
	}
}
