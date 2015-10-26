package com.onpositive.text.analisys.tests.neural;

import com.onpositive.semantic.wordnet.Grammem;

public interface IDataSetGenerator<T> {
	
	public double generateDataset(T object, Class<? extends Grammem>[] props);

}
