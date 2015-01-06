package com.onpositive.text.analysis.syntax;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;

public class VerbAdverbParser extends VerbGroupParser{

	public VerbAdverbParser(AbstractWordNet wordNet) {
		super(wordNet);
	}

	@Override
	protected int getType(SyntaxToken token) {
		return IToken.TOKEN_TYPE_VERB_ADVERB;
	}

	@Override
	protected boolean checkAdditionalToken(IToken token) {		
		return hasAll(PartOfSpeech.ADVB).match(token);
	}

}
