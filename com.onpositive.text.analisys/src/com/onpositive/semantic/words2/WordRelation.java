package com.onpositive.semantic.words2;

import java.io.Serializable;


public class WordRelation implements Serializable{
	
	protected SimpleWordNet owner;

	public WordRelation(SimpleWordNet owner, SimpleWord word, int relation) {
		super();
		this.word = word.id;
		this.relation = relation;
		this.owner=owner;
		
	}
	@Override
	public String toString() {
		
		return owner.getWord(word)+":"+relation;
	}

	public WordRelation(SimpleWordNet owner2, int i, int relation2) {
		this.owner=owner2;
		this.word=i;
		this.relation=relation2;		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final int word;
	
	public final int relation;
	
	public static final int EXACT_FORM=0;
	
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + relation;
		return result;
	}
	public RelationTarget getWord() {
		Object result = owner.getWord(word);
		if (!(result instanceof RelationTarget)) {
			return null;
		}
		return (RelationTarget) result;
	}
}
