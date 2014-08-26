package com.onpositive.semantic.words2;

public abstract class WordNet extends WordFormsStore implements Iterable<Word>{
	
	int fullyCorrectNouns;
	int fullyCorrectAdj;
	int incorrectNouns;
	int incorrectAdj;

	protected abstract void registerWord(Word word);
	
	public abstract WordRelation[] getPosibleWords(String wf);
	
	public abstract RelationTarget getWord(String basicForm);

	public abstract WordFormTemplate findTemplate(String string);
	
	protected abstract void registerTemplate(WordFormTemplate tpl);

	public abstract void markRedirect(String from, String to) ;

	protected abstract Word getOrCreateWord(String lowerCase);

	protected abstract void init() ;
	
	protected abstract RelationTarget getOrCreateRelationTarget(String s) ;
}
