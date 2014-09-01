package com.onpositive.semantic.references;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import com.onpositive.semantic.parsing.ParsedWord;

public class MultiReference extends TermReference{

	LinkedHashSet<ITerm>terms=new LinkedHashSet<ITerm>();
	
	public boolean add(ITerm e) {
		return terms.add(e);
	}

	public MultiReference(ArrayList<ParsedWord> ws, ITerm basicTerm) {
		super(ws, basicTerm);
		terms.add(basicTerm);
	}
	@Override
	public Collection<ITerm> terms() {
		ArrayList<ITerm> arrayList = new ArrayList<ITerm>(terms);
		//arrayList.addAll(super.terms());
		return arrayList;
	}
	
	@Override
	public String toString() {
		return terms.toString();
	}

}
