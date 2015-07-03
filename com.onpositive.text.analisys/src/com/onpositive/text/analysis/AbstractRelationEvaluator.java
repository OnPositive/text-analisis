package com.onpositive.text.analysis;

import java.util.HashSet;
import java.util.Set;

import com.carrotsearch.hppc.ObjectDoubleOpenHashMap;
import com.carrotsearch.hppc.ObjectObjectOpenHashMap;
import com.onpositive.text.analysis.lexic.SymbolToken;
import com.onpositive.text.analysis.lexic.WordFormToken;

public abstract class AbstractRelationEvaluator {
	
	private static ObjectObjectOpenHashMap<Class<? extends AbstractRelationEvaluator>, AbstractRelationEvaluator> instances;
	
	public static AbstractRelationEvaluator getInstance(Class<? extends AbstractRelationEvaluator> type) {
		AbstractRelationEvaluator localInstance = instances.get(type);
        if (localInstance == null) {
            synchronized (AbstractRelationEvaluator.class) {
                localInstance = instances.get(type);
                if (localInstance == null) {
                    try {
						localInstance = type.newInstance();
						instances.put(type, localInstance);
					} catch (Exception e) {
						return null;
					}
                }
            }
        }
        return localInstance;
	}
	
	
	
	private ObjectDoubleOpenHashMap<IToken> mem = new ObjectDoubleOpenHashMap<>();
	
	void save(IToken token, double value) { mem.put(token, value);	}
	
	double load(IToken token) {
		if (!mem.containsKey(token)) 
			mem.put(token, calculate(token));
		return mem.get(token);
	}
	
	public abstract void clear();
	
	protected abstract double calculate(IToken token);

	private Set<IToken> used = new HashSet<IToken>(); 

	private void go(IToken token, boolean propagate) {
		if (token.childrenCount() > 0)
			for (IToken child : token.getChildren())
				go(child, propagate);
		
		if (used.contains(token)) return;
		if (!propagate && (token instanceof WordFormToken || token instanceof SymbolToken)) {
			used.add(token);
			return;
		}
		
		double rel = load(token);
		token.setCorrelation(rel);
		used.add(token);
	}
	
	
	public void process(IToken token, boolean propagate) {
		clear();
		used.clear();
		
		go(token, propagate);
		
	}	
}
