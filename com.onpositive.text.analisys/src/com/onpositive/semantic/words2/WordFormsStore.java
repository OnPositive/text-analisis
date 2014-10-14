package com.onpositive.semantic.words2;

import com.onpositive.semantic.words3.model.AbstractWordNet;
import com.onpositive.semantic.words3.model.WordRelation;

public abstract class WordFormsStore extends AbstractWordNet{
	protected abstract void registerWordForm(String wf,WordRelation form);
	public abstract WordRelation[] getPosibleWords(String wf);
}
