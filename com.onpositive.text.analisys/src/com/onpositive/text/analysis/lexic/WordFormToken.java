package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.text.analysis.syntax.SyntaxToken;

public class WordFormToken extends SyntaxToken {

	public WordFormToken(MeaningElement meaningElement, int startPosition, int endPosition) {
		super(TOKEN_TYPE_WORD_FORM, null, null, startPosition, endPosition);
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
	public List<GrammemSet> getGrammemSets(){
		if(this.grammemSets==null){
			initGrammemSets();
		}
		return grammemSets;
	}
	
	protected void initGrammemSets()
	{
		this.grammemSets = new ArrayList<SyntaxToken.GrammemSet>();
		MeaningElement meaningElement = getMeaningElement();
		Set<Grammem> grammems = meaningElement.getGrammems();
		List<GrammarRelation> grammarRelations = getGrammarRelations();
		if(grammarRelations!=null){
			for(GrammarRelation rel : grammarRelations){
				HashSet<Grammem> relGrammems = new HashSet<Grammem>(rel.getGrammems());
				if(grammems!=null){
					relGrammems.addAll(grammems);
				}
				grammemSets.add(new GrammemSet(relGrammems));
			}
		}
		else if(grammems!=null){
			grammemSets.add(new GrammemSet(grammems));
		}
	}
	
	public String getBasicForm() {
		return meaningElement.getParentTextElement().getBasicForm();
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
