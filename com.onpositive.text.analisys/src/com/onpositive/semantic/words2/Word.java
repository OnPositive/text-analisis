package com.onpositive.semantic.words2;

import java.io.Serializable;

public abstract class Word extends AbstractRelationTarget implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract boolean isNoun();
	
	public abstract boolean isAdjective();
	
	public abstract boolean isVerb();
	
	public abstract String getBasicForm();

	public abstract String getFoundation(int number);
	
	protected abstract void registerFoundation(int number,String foundation);
	
	
	public static final int NOUN=1;
	public static final int VERB=2;
	public static final int ADJ=4;
	
	
	public static final int FEATURE_NAME=1;
	public static final int FEATURE_TOPONIM=2;
	
	protected abstract void setTemplate(WordFormTemplate findTemplate) ;

	protected abstract void setKind(int kind) ;
	
	protected abstract void setFeature(int featureKind);

	public abstract boolean hasFeature(int feature);
}
