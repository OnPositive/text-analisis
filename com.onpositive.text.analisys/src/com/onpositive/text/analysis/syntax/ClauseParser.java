package com.onpositive.text.analysis.syntax;

import java.util.List;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;

public class ClauseParser extends AbstractSyntaxParser{
	
	private static final UnaryMatcher<SyntaxToken> isNoun
			= hasAny( PartOfSpeech.NOUN, PartOfSpeech.NPRO);
	
	private static final UnaryMatcher<SyntaxToken> acceptedNomn
			= hasAny(caseMatchMap.get(Case.NOMN));

	@SuppressWarnings("unchecked")
	private static final UnaryMatcher<SyntaxToken> checkNoun = and(isNoun, acceptedNomn, not(prepConjMatch));
	
	private static final UnaryMatcher<SyntaxToken> verbMatchGrammems
			= hasAll(PartOfSpeech.VERB);
	

	@SuppressWarnings("unchecked")
	private static UnaryMatcher<SyntaxToken> verbOrNoun = or(verbMatchGrammems,checkNoun);

	public ClauseParser(AbstractWordNet wordNet) {
		super(wordNet);
	}

	@Override
	protected void combineTokens(Stack<IToken> sample, ProcessingData processingData)
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
		int endPosition = computeEndPoosition(token1);		
		
		IToken newToken = new ClauseToken(nounToken, verbToken, startPosition, endPosition);
		if(checkParents(newToken, sample)){
			processingData.addReliableToken(newToken);
		}
	}

	protected int computeEndPoosition(SyntaxToken token) {
		
		int endPosition = token.getEndPosition();
		
		List<IToken> children = token.getChildren();
		IToken last = children.get(children.size()-1);
		IToken next = last.getNext();
		if(next!=null){
			if(next.getType()==IToken.TOKEN_TYPE_SYMBOL&&next.getStringValue().equals(".")){
				endPosition = next.getEndPosition();
			}
		}
		else{
			List<IToken> nextTokens = last.getNextTokens();
			if(nextTokens!=null){
				for(IToken n : nextTokens){
					if(n.getType()==IToken.TOKEN_TYPE_SYMBOL&&n.getStringValue().equals(".")){
						endPosition = n.getEndPosition();
						break;
					}					
				}
			}
		}
		return endPosition;
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
