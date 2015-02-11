package com.onpositive.text.analysis.lexic.disambig;

import java.util.Arrays;

import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.semantic.wordnet.TextElement;

public class DisambiguationData {
	public final TextElement textElement;
	public final MeaningElement[] elements;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(elements);
		result = prime * result
				+ ((textElement == null) ? 0 : textElement.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DisambiguationData other = (DisambiguationData) obj;
		if (!Arrays.equals(elements, other.elements))
			return false;
		if (textElement == null) {
			if (other.textElement != null)
				return false;
		} else if (!textElement.equals(other.textElement))
			return false;
		return true;
	}

	public DisambiguationData(TextElement textElement, MeaningElement[] elements) {
		super();
		this.textElement = textElement;
		this.elements = elements;
	}

}
