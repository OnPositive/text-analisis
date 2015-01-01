package com.onpositive.text.analysis.syntax;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;

public class VerbNamePrepositionParser extends VerbGroupParser {
	

	private static final UnaryMatcher<SyntaxToken> adjectiveMatch = hasAny(PartOfSpeech.ADJF);
	
	private static final UnaryMatcher<SyntaxToken> nounMatch = hasAny(PartOfSpeech.NOUN);
	
	private static final UnaryMatcher<SyntaxToken> nounAdjectiveMatch = hasAny(PartOfSpeech.NOUN,PartOfSpeech.ADJF);

	public VerbNamePrepositionParser(AbstractWordNet wordNet) {
		super(wordNet);
	}

	@Override
	protected int getType(SyntaxToken token) {
		if(nounMatch.match(token)){
			return IToken.TOKEN_TYPE_VERB_NOUN_PREP;
		}
		else if(adjectiveMatch.match(token)){
			return IToken.TOKEN_TYPE_VERB_ADJECTIVE_PREP;
		}
		return -1;
	}
	
	@Override
	protected boolean adjustTokens(SyntaxToken[] orderedTokens) {
		
		String prepString = orderedTokens[2].getBasicForm();
		UnaryMatcher<SyntaxToken> matcher = getPrepConjRegistry().getPrepCaseMatcher(prepString);
		SyntaxToken nameToken = orderedTokens[1];
		boolean result = matcher.match(nameToken);
		return result;
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
	protected boolean acceptsPreposition() {
		return true;
	}
	
	@Override
	protected boolean requiresPreposition(){
		return true;
	};

}
