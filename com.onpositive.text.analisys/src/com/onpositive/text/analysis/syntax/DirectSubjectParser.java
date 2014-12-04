package com.onpositive.text.analysis.syntax;

import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.Grammem.TransKind;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.rules.matchers.BiMatcher;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;

public class DirectSubjectParser extends AbstractSyntaxParser {

	private final UnaryMatcher<SyntaxToken> acceptedNames = hasAny(
			PartOfSpeech.NOUN, PartOfSpeech.ADJF);
	private final UnaryMatcher<SyntaxToken> acceptedAcc = hasAny(caseMatchMap
			.get(Case.ACCS));

	private final UnaryMatcher<SyntaxToken> acceptedGC = hasAny(caseMatchMap
			.get(Case.GENT));

	@SuppressWarnings("unchecked")
	private final UnaryMatcher<SyntaxToken> checkName = and(
			acceptedNames,
			or(acceptedAcc, and(acceptedGC, not(has(Grammem.SingularPlural.SINGULAR)))));
	private final UnaryMatcher<SyntaxToken> verbMatchGrammems = hasAll(
			PartOfSpeech.VERB, TransKind.tran);
	private final UnaryMatcher<SyntaxToken> infnGrammems = has(PartOfSpeech.INFN);

	@SuppressWarnings("unchecked")
	private final BiMatcher firstOption = both(verbMatchGrammems,
			or(checkName, infnGrammems));

	@SuppressWarnings("unchecked")
	UnaryMatcher<SyntaxToken> verbInforName = or(verbMatchGrammems,
			infnGrammems, checkName);

	@Override
	protected void combineTokens(Stack<IToken> sample,
			Set<IToken> reliableTokens, Set<IToken> doubtfulTokens) {
		if (sample.size() < 2) {
			return;
		}

		SyntaxToken token0 = (SyntaxToken) sample.get(0);
		SyntaxToken token1 = (SyntaxToken) sample.peek();

		if (checkIfAlreadyProcessed(token0, token1)) {
			return;
		}

		SyntaxToken verbToken = null;
		SyntaxToken subjToken = null;
		if (verbMatchGrammems.match(token0)) {
			verbToken = token0;
			subjToken = token1;
		} else {
			verbToken = token1;
			subjToken = token0;
		}

		int subjType = infnGrammems.match(subjToken) ? IToken.TOKEN_TYPE_DIRECT_SUBJECT_INF
				: IToken.TOKEN_TYPE_DIRECT_SUBJECT_NAME;

		createAndAdd(sample, reliableTokens, token0, token1, verbToken,
				subjType);
	}

	void createAndAdd(Stack<IToken> sample, Set<IToken> reliableTokens,
			SyntaxToken first, SyntaxToken last, SyntaxToken mainToken,
			int subjType) {
		IToken newToken = new SyntaxToken(subjType, mainToken,
				first.getStartPosition(), last.getEndPosition());
		if (checkParents(newToken, sample)) {
			reliableTokens.add(newToken);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample,
			IToken newToken) {
		IToken token0 = sample.peek();
		IToken token1 = newToken;

		return toAcceptBreak(firstOption.match(token0, token1)
				|| verbMatchGrammems.match(token1));
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		return toPush(verbInforName.match(newToken));
	}
}