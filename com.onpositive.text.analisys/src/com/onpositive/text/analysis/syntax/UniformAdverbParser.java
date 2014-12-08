package com.onpositive.text.analysis.syntax;

import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;

public class UniformAdverbParser extends UniformSentencePartsParser {

	public UniformAdverbParser(){
		super(IToken.TOKEN_TYPE_UNIFORM_ADVERB, new PartOfSpeech[]{PartOfSpeech.ADVB});
	}

}
