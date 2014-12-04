package com.onpositive.text.analysis.rules.matchers;

public class AndMatcher<T> extends UnaryMatcher<T> {

	protected UnaryMatcher<T>[]matchers;
	
	public AndMatcher(Class<T> clazz,UnaryMatcher<T>... matchers) {
		super(clazz);
		this.matchers=matchers;
	}
	

	@Override
	public boolean match(T token) {
		for (UnaryMatcher<T>m:matchers){
			if (!m.match(token)){
				return false;
			}
		}
		return true;
	}
	
}
