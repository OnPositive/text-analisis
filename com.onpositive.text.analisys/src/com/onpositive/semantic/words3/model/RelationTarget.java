package com.onpositive.semantic.words3.model;

/**
 * Super class of all  elements which may be in semantic relations with other elements
 * @author kor
 *
 */
public abstract class RelationTarget {

	public abstract int id() ;
	
	public abstract WordRelation[] getSemanticRelations();
	
}
