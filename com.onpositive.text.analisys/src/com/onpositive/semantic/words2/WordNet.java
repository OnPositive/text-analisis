package com.onpositive.semantic.words2;

import com.onpositive.semantic.words3.model.RelationTarget;
import com.onpositive.semantic.words3.model.WordRelation;

public abstract class WordNet extends WordFormsStore implements Iterable<AbstractRelationTarget>{
	
	int fullyCorrectNouns;
	int fullyCorrectAdj;
	int fullyCorrectVerbs;
	
	int incorrectNouns;
	int incorrectAdj;
	int incorrectVerbs;

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
