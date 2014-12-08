package com.onpositive.text.analysis.syntax;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;

public class UniformAdverbParser extends UniformSentencePartsParser {

	public UniformAdverbParser(AbstractWordNet wordNet){
		super(wordNet, IToken.TOKEN_TYPE_UNIFORM_ADVERB, new PartOfSpeech[]{PartOfSpeech.ADVB});
	}

}
