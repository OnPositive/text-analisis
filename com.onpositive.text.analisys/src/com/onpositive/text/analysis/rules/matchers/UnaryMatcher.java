package com.onpositive.text.analysis.rules.matchers;

public abstract class UnaryMatcher<T> {

	private final Class<T> clazz;

	public UnaryMatcher(Class<T> clazz) {
		super();
		this.clazz = clazz;
	}

	public abstract boolean match(T token);
	
	public Class<T>getTokenClass(){
		return clazz;
	}
}
