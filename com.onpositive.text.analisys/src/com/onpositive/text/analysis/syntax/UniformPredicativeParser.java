package com.onpositive.text.analysis.syntax;

import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;

public class UniformPredicativeParser extends UniformSentencePartsParser {

	public UniformPredicativeParser() {
		super(IToken.TOKEN_TYPE_UNIFORM_PREDICATIVE, new PartOfSpeech[]{PartOfSpeech.PRED});
	}
}
