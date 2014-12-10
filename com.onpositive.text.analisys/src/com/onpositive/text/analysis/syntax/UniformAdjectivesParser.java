package com.onpositive.text.analysis.syntax;

import java.util.Map;
import java.util.Set;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.Gender;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.Grammem.SingularPlural;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.syntax.SyntaxToken.GrammemSet;
import com.onpositive.text.analysis.syntax.UniformSentencePartsParser;

public class UniformAdjectivesParser extends UniformSentencePartsParser {

	public UniformAdjectivesParser(AbstractWordNet wordNet) {
		super(wordNet, IToken.TOKEN_TYPE_UNIFORM_ADJECTIVE, new PartOfSpeech[]{PartOfSpeech.ADJF});
	}
	
	protected boolean checkGrammemSetCorrespondence(GrammemSet gs0, GrammemSet gs1) {
		
		Set<Gender> matchedGender = matchGender(gs0,gs1);
		if(matchedGender==null||matchedGender.isEmpty()){
			return false;
		}

		Map<SingularPlural,SingularPlural> matchedSp = matchSP(gs0,gs1);
		if(matchedSp==null||matchedSp.isEmpty()){
			return false;
		}
		
		Map<Case, Case> matchedCase = matchCase(gs0,gs1);
		if(matchedCase==null||matchedCase.isEmpty()){
			return false;
		}
		return true;
	}

}
