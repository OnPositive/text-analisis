package com.onpositive.text.analysis.rules.matchers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.text.analysis.syntax.SyntaxToken;

public class HasGrammem extends UnaryMatcher<SyntaxToken>{

	private Grammem grammems;

	public HasGrammem(Grammem gramems) {
		super(SyntaxToken.class);
		this.grammems=gramems;
	}

	@Override
	public boolean innerMatch(SyntaxToken token) {
		return token.hasGrammem(grammems);
	}

}
