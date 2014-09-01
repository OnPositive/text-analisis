package com.onpositive.semantic.references;

import java.util.Collection;

import com.onpositive.semantic.parsing.ParsedWord;

public interface ITermLookup {

	Collection<TermReference>doLookup(ParsedWord word);

}
