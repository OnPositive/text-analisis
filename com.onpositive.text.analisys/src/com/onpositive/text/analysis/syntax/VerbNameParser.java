package com.onpositive.text.analysis.syntax;

import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;

public class VerbNameParser extends VerbGroupParser {

	private static final UnaryMatcher<SyntaxToken> adjectiveMatch
			= and(hasAny(PartOfSpeech.ADJF),not(DirectObjectParser.directObjectCasesMatch),not(hasAny(Case.NOMN)));
	
	private static final UnaryMatcher<SyntaxToken> nounMatch
			= and(hasAny(PartOfSpeech.NOUN),not(DirectObjectParser.directObjectCasesMatch),not(hasAny(Case.NOMN)));
	
	private static final UnaryMatcher<SyntaxToken> nounAdjectiveMatch
			= and(hasAny(PartOfSpeech.NOUN,PartOfSpeech.ADJF),not(DirectObjectParser.directObjectCasesMatch),not(hasAny(Case.NOMN)));
	
	private static final UnaryMatcher<SyntaxToken> casesByReflexiveVerbMatch
		= hasAny(Case.DATV, Case.ABLT);

	public VerbNameParser(AbstractWordNet wordNet) {
		super(wordNet);
	}

	@Override
	protected int getType(SyntaxToken token) {
		if(nounMatch.match(token)){
			return IToken.TOKEN_TYPE_VERB_NOUN;
		}
		else if(adjectiveMatch.match(token)){
			return IToken.TOKEN_TYPE_VERB_ADJECTIVE;
		}
		return -1;
	}

	@Override
	protected boolean checkAdditionalToken(IToken token) {		
		return and(nounAdjectiveMatch,not(prepMatch)).match(token);
	}

	@Override
	protected boolean checkVerb(IToken token) {
		return verbMatch.match(token);
	}
	
	@Override
	protected boolean matchTokensCouple(Stack<IToken> sample) {
		
		IToken token0 = sample.get(0);
		IToken token1 = sample.get(1);
		
		SyntaxToken verbToken;
		SyntaxToken nounToken;
		if(checkVerb(token0)||token0.getType()==IToken.TOKEN_TYPE_CLAUSE){
			verbToken = (SyntaxToken) token0;
			nounToken = (SyntaxToken) token1;
		}
		else{
			verbToken = (SyntaxToken) token1;
			nounToken = (SyntaxToken) token0;
		}
		
		if(verbToken instanceof ClauseToken){
			verbToken = ((ClauseToken)verbToken).getPredicate();
		}
		
		String basicForm = verbToken.getBasicForm();
		if(!basicForm.endsWith("ся")){
			return true;
		}
		boolean result = casesByReflexiveVerbMatch.match(nounToken);
		return result;
	}
}
