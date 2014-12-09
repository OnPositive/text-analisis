package com.onpositive.text.analysis.syntax;

import java.util.Map;
import java.util.Set;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.Gender;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.Grammem.SingularPlural;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.syntax.UniformSentencePartsParser;

public class UniformAdjectivesParser extends UniformSentencePartsParser {

	public UniformAdjectivesParser(AbstractWordNet wordNet) {
		super(wordNet, IToken.TOKEN_TYPE_UNIFORM_ADJECTIVE, new PartOfSpeech[]{PartOfSpeech.ADJF});
	}
	
	protected boolean refineGrammemSet(Set<Grammem> grammems, SyntaxToken token) {
		
		Set<Grammem> tokenGrammems = token.getAllGrammems();
		
		Set<Gender> genderSet = extractGrammems(grammems, Gender.class);
		Set<Gender> tokenGenderSet = extractGrammems(tokenGrammems, Gender.class);
		Set<Gender> matchedGender = matchGender(genderSet, tokenGenderSet);
		if(matchedGender==null||matchedGender.isEmpty()){
			return false;
		}
		
		Set<SingularPlural> spSet = extractGrammems(grammems, SingularPlural.class);
		Set<SingularPlural> tokenSpSet = extractGrammems(tokenGrammems, SingularPlural.class);
		Map<SingularPlural,SingularPlural> matchedSp = matchSP(spSet, tokenSpSet);
		if(matchedSp==null||matchedSp.isEmpty()){
			return false;
		}
		
		Set<Case> caseSet = extractGrammems(grammems, Case.class);
		Set<Case> tokenCaseSet = extractGrammems(tokenGrammems, Case.class);
		Map<Case, Case> matchedCase = matchCase(caseSet, tokenCaseSet);
		if(matchedCase==null||matchedCase.isEmpty()){
			return false;
		}
		grammems.clear();
		grammems.addAll(matchedGender);
		grammems.addAll(matchedSp.keySet());
		grammems.addAll(matchedCase.keySet());
		return true;
	}

}
