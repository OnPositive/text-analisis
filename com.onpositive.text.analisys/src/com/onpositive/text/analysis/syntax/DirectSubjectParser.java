package com.onpositive.text.analysis.syntax;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.TransKind;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;

public class DirectSubjectParser extends AbstractSyntaxParser {

	@Override
	protected void combineTokens(Stack<IToken> sample, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens)
	{
		if(sample.size()<2){
			return;
		}

		SyntaxToken token0 = (SyntaxToken) sample.get(0);
		SyntaxToken token1 = (SyntaxToken) sample.peek();
		
		if(checkIfAlreadyProcessed(token0, token1)){
			return;
		}
		
		int startPosition = token0.getStartPosition();
		int endPosition = token1.getEndPosition();
		
		SyntaxToken verbToken = null;
		SyntaxToken subjToken = null;
		if(checkVerb(token0)){
			verbToken = token0;
			subjToken = token1;
		}
		else{
			verbToken = token1;
			subjToken = token0;
		}
		int subjType = checkInf(subjToken)
				? IToken.TOKEN_TYPE_DIRECT_SUBJECT_INF
				: IToken.TOKEN_TYPE_DIRECT_SUBJECT_NAME;

		IToken newToken = new SyntaxToken(subjType, verbToken, startPosition, endPosition);
		if(checkParents(newToken,sample)){
			reliableTokens.add(newToken);
		}
	}
	
	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample,IToken newToken)
	{
		if(!(newToken instanceof SyntaxToken)){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		
		SyntaxToken token0 = (SyntaxToken) sample.peek();
		SyntaxToken token1 = (SyntaxToken)newToken;
		
		if(checkVerb(token0)){
			if(checkName(token1)){
				return ACCEPT_AND_BREAK;
			}
			if(checkInf(token1)){
				return ACCEPT_AND_BREAK;
			}
		}
		else{
			if(checkVerb(token1)){
				return ACCEPT_AND_BREAK;
			}
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		
		if(!(newToken instanceof SyntaxToken)){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		
		SyntaxToken st = (SyntaxToken) newToken;
		if(checkVerb(st)){
			return CONTINUE_PUSH;
		}
		if(checkInf(st)){
			return CONTINUE_PUSH;
		}
		if(checkName(st)){
			return CONTINUE_PUSH;
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}

	
	private final static Set<PartOfSpeech> acceptedNames = 
			new HashSet<Grammem.PartOfSpeech>(Arrays.asList(PartOfSpeech.NOUN, PartOfSpeech.ADJF));
	
	
	private boolean checkName(SyntaxToken token)
	{
		if(token.hasOneOfGrammems(acceptedNames)){
			if(token.hasOneOfGrammems(caseMatchMap.get(Case.ACCS))){
				return true;
			}
		}
		return false;
	}
	
	private final static Set<Grammem> verbMatchGrammems = 
			new HashSet<Grammem>(Arrays.asList(PartOfSpeech.VERB, TransKind.tran));
	

	private boolean checkVerb(SyntaxToken token) {		
		return token.hasAllGrammems(verbMatchGrammems);
	}
	
	
	private boolean checkInf(SyntaxToken token){
		boolean hasGrammem = token.hasGrammem(PartOfSpeech.INFN);
		return hasGrammem;
	}
}
