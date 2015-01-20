package com.onpositive.text.analysis.syntax;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.text.analysis.IToken;

public class VerbNamePrepositionParser extends VerbGroupParser {
	

	public VerbNamePrepositionParser(AbstractWordNet wordNet) {
		super(wordNet);
	}

	@Override
	protected int getType(SyntaxToken token) {
		
		if(!(token instanceof PrepositionGroupToken)){
			return -1;
		}
		
		PrepositionGroupToken prepToken = (PrepositionGroupToken) token;
		SyntaxToken word = prepToken.getWord();
		
		if(nounMatch.match(word)){
			return IToken.TOKEN_TYPE_VERB_NOUN_PREP;
		}
		else if(adjectiveMatch.match(word)){
			return IToken.TOKEN_TYPE_VERB_ADJECTIVE_PREP;
		}
		else if(adverbMatch.match(word)){
			return IToken.TOKEN_TYPE_VERB_ADVERB_PREP;
		}
		return -1;
	}

	@Override
	protected boolean checkAdditionalToken(IToken token) {		
		boolean result = token.getType() == IToken.TOKEN_TYPE_PREPOSITION_GROUP;
		return result;
	}

}
