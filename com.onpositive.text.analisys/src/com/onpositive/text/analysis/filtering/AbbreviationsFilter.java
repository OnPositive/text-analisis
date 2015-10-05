package com.onpositive.text.analysis.filtering;

import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.Grammem.SemanGramem;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.WordFormToken;

public class AbbreviationsFilter implements ITokenFilter {

	@Override
	public boolean shouldFilterOut(IToken token) {
		return token instanceof WordFormToken &&
			((WordFormToken) token).hasGrammem(PartOfSpeech.NOUN) && 
			((WordFormToken) token).hasGrammem(SemanGramem.ABBR);
	}

}
