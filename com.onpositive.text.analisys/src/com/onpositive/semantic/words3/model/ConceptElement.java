package com.onpositive.semantic.words3.model;

public abstract class ConceptElement extends RelationTarget{

	public abstract TextElement getParentTextElement();

	public abstract short getKind();
	
	public abstract short getFeatures();
}
