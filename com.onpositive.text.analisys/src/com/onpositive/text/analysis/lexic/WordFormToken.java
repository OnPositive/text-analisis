package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.List;

import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.text.analysis.syntax.SyntaxToken;

public class WordFormToken extends SyntaxToken {

	public WordFormToken(MeaningElement meaningElement, int startPosition, int endPosition) {
		super(TOKEN_TYPE_WORD_FORM, null, startPosition, endPosition);
		this.meaningElement = meaningElement;
		this.mainGroup = this;
	}
	
	private final MeaningElement meaningElement;
	
	private ArrayList<GrammarRelation> grammarRelations = new ArrayList<GrammarRelation>();

	public MeaningElement getMeaningElement() {
		return meaningElement;
	}

	public void addGrammarRelation(GrammarRelation gr) {
		grammarRelations.add(gr);		
	}

	public List<GrammarRelation> getGrammarRelations() {
		return grammarRelations;
	}

	@Override
	public String getStringValue() {
		return meaningElement == null ? "null" : meaningElement.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((grammarRelations == null) ? 0 : grammarRelations.hashCode());
		result = prime * result
				+ ((meaningElement == null) ? 0 : meaningElement.hashCode());
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
		if (grammarRelations == null) {
			if (other.grammarRelations != null)
				return false;
		} else if (!grammarRelations.equals(other.grammarRelations))
			return false;
		if (meaningElement == null) {
			if (other.meaningElement != null)
				return false;
		} else if (!meaningElement.equals(other.meaningElement))
			return false;
		return true;
	}

}
