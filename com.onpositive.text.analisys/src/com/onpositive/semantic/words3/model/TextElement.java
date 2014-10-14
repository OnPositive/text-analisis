package com.onpositive.semantic.words3.model;

public abstract class TextElement extends RelationTarget{

	public abstract String getBasicForm();
	
	public abstract ConceptElement[] getConcepts();
	
	@Override
	public String toString() {
		return getBasicForm();
	}
}
