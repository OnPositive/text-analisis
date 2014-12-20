package com.onpositive.text.analysis.syntax;

import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.Grammem.TransKind;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;

public class ClauseParser extends AbstractSyntaxParser{
	
	private static final UnaryMatcher<SyntaxToken> isNoun
			= hasAny( PartOfSpeech.NOUN, PartOfSpeech.NPRO);
	
	private static final UnaryMatcher<SyntaxToken> acceptedNomn
			= hasAny(caseMatchMap.get(Case.NOMN));

	@SuppressWarnings("unchecked")
	private static final UnaryMatcher<SyntaxToken> checkNoun = and(isNoun, acceptedNomn);
	
	private static final UnaryMatcher<SyntaxToken> verbMatchGrammems
			= hasAll(PartOfSpeech.VERB);
	

	@SuppressWarnings("unchecked")
	private static UnaryMatcher<SyntaxToken> verbOrNoun = or(verbMatchGrammems,checkNoun);

	public ClauseParser(AbstractWordNet wordNet) {
		super(wordNet);
	}

	@Override
	protected void combineTokens(Stack<IToken> sample,Set<IToken> reliableTokens, Set<IToken> doubtfulTokens)
	{
		if(sample.size()<2){
			return;
		}
		
		SyntaxToken token0 = (SyntaxToken) sample.get(0);
		SyntaxToken token1 = (SyntaxToken) sample.peek();
		
		SyntaxToken verbToken = null;
		SyntaxToken nounToken = null;
		if (verbMatchGrammems.match(token0)) {
			verbToken = token0;
			nounToken = token1;
		} else {
			verbToken = token1;
			nounToken = token0;
		}
		int startPosition = token0.getStartPosition();
		int endPosition = token1.getEndPosition();
		IToken newToken = new ClauseToken(nounToken, verbToken, startPosition, endPosition);
		if(checkParents(newToken, sample)){
			reliableTokens.add(newToken);
		}
	}

	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample,
			IToken newToken) {
		IToken token0 = sample.get(0);
		IToken token1 = newToken;
		if (verbMatchGrammems.match(token0)	&& checkNoun.match(token1)){
			return ACCEPT_AND_BREAK;
		} else if (verbMatchGrammems.match(token1)) {
			return ACCEPT_AND_BREAK;
		}		
		return DO_NOT_ACCEPT_AND_BREAK;
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		if (verbOrNoun.match(newToken)) {
			return CONTINUE_PUSH;
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}
	
	@Override
	public boolean isIterative() {
		return false;
	}

}
