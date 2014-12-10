package com.onpositive.text.analysis.syntax;

import java.util.Map;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.syntax.SyntaxToken.GrammemSet;

public class UniformNounsParser extends UniformSentencePartsParser {

	public UniformNounsParser(AbstractWordNet wordNet) {
		super(wordNet, IToken.TOKEN_TYPE_UNIFORM_NOUN, new PartOfSpeech[]{PartOfSpeech.NOUN});
	}
	
	protected boolean checkGrammemSetCorrespondence(GrammemSet gs0, GrammemSet gs1) {
		
		Map<Case, Case> matchedCase = matchCase(gs0,gs1);
		if(matchedCase==null||matchedCase.isEmpty()||(matchedCase.size()==1&&matchedCase.containsKey(Case.NOMN))){
			return false;
		}
		return true;
	}

}
