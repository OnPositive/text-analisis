package com.onpositive.semantic.words2;

public abstract class RelationTarget {

	public abstract Word[] getWords();

	public abstract int id() ;

	public abstract boolean matchRelated(RelationTarget relationTarget) ;
	
	
}
