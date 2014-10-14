package com.onpositive.semantic.words3.model;


public abstract class AbstractWordNet {

	public abstract int wordCount();

	public abstract int conceptCount();
	
	public abstract int grammarFormsCount();
	
	public abstract TextElement getWordElement(int wordId);
	
	
	public abstract ConceptElement getConceptInfo(int conceptId);
	
	public abstract WordRelation[] getPossibleGrammarForms(String wordForm);
	
	public abstract TextElement getWordElement(String basicForm);
	
}
