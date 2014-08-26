package com.onpositive.semantic.words2;

public abstract class WordFormsStore {
	protected abstract void registerWordForm(String wf,WordRelation form);
	public abstract WordRelation[] getPosibleWords(String wf);
}
