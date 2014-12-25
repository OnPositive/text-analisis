package com.onpositive.text.analysis.syntax;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;

public class DirectObjectParser extends VerbGroupParser {

	


	public DirectObjectParser(AbstractWordNet wordNet) {
		super(wordNet);
	}

	private static final UnaryMatcher<SyntaxToken> acceptedNames = hasAny(
			PartOfSpeech.NOUN/*, PartOfSpeech.ADJF*/);
	private static final UnaryMatcher<SyntaxToken> acceptedAcc = hasAny(caseMatchMap
			.get(Case.ACCS));

	private static final UnaryMatcher<SyntaxToken> acceptedGC = hasAny(caseMatchMap
			.get(Case.GENT));

	protected static final UnaryMatcher<SyntaxToken> directObjectCasesMatch
		= or(acceptedAcc, and(acceptedGC, not(has(Grammem.SingularPlural.SINGULAR))));
	
	@SuppressWarnings("unchecked")
	private final UnaryMatcher<SyntaxToken> checkName = and(
			acceptedNames,
			directObjectCasesMatch);
	

	@Override
	protected boolean checkVerb(IToken token0) {
		return transitiveVerbMatch.match(token0);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected boolean checkAdditionalToken(IToken token1) {
		return and(or(checkName, infnMatch),not(prepMatch)).match(token1);
	}

	@Override
	protected int getType(SyntaxToken objToken) {
		int subjType = infnMatch.match(objToken) ? IToken.TOKEN_TYPE_DIRECT_OBJECT_INF
				: IToken.TOKEN_TYPE_DIRECT_OBJECT_NAME;
		return subjType;
	}
}
