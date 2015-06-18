package com.onpositive.text.analysis;

public interface ICorrelationEvaluator {
	
	public void calculate(IToken token);
	public void propagate(IToken token);
}
