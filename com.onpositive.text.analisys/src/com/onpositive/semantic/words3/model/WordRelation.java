package com.onpositive.semantic.words3.model;

import java.io.Serializable;



public class WordRelation implements Serializable{
	
	/**
	 * Semantic relations
	 */
	public static final int SYNONIM=1;
	public static final int SPECIALIZATION=2;
	public static final int GENERALIZATION=3;
	public static final int GENERALIZATION_BACK_LINK=5;
	public static final int SYNONIM_BACK_LINK=6;
	public static final int ANTONIM = 4;
	public static final int SPECIALIZATION_BACK_LINK = 7;
	
	public AbstractWordNet owner;

	public WordRelation(AbstractWordNet owner, RelationTarget word, int relation) {
		super();
		this.word = word.id();
		this.relation = relation;
		this.owner=owner;		
	}
	
	@Override
	public String toString() {
		return owner.getWordElement(word)+":"+relation;
	}
	
	public WordRelation(AbstractWordNet owner2, int word, int relation2) {
		this.owner=owner2;
		this.word=word;
		this.relation=relation2;		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final int word;
	
	public final int relation;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + relation;
		result = prime * result + word;
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
		WordRelation other = (WordRelation) obj;
		if (relation != other.relation)
			return false;
		if (word != other.word)
			return false;
		return true;
	}
	public static final int EXACT_FORM=0;
	
	public RelationTarget getWord() {
		RelationTarget result = owner.getWordElement(word);
		return (RelationTarget) result;
	}
}