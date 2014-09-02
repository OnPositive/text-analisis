package com.onpositive.semantic.references;

import java.util.List;

import com.onpositive.semantic.parsing.ParsedWord;


public interface ITerm {

	TermReference matches(ParsedWord word);

	int relatedTo(ITerm term);

	List<ISemanticConnection> getConnections();
	
	ITerm resolveToPrimary();
}
