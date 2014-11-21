package com.onpositive.text.analysis.lexic;

import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.text.analysis.AbstractToken;

public class WordFormToken extends AbstractToken {

	public WordFormToken(TextElement grammarRelation, int startPosition, int endPosition) {
		super(TOKEN_TYPE_WORD_FORM, startPosition, endPosition);
		this.textElement = grammarRelation;
	}
	
	private final TextElement textElement;
	

	public TextElement getTextElement() {
		return textElement;
	}

	@Override
	public String getStringValue() {
		return textElement == null ? "null" : textElement.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((textElement == null) ? 0 : textElement.id());
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
		if (textElement == null) {
			if (other.textElement != null)
				return false;
		} else if (textElement.id() == other.textElement.id())
			return false;
		return true;
	}
}
