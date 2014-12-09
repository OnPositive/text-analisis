package com.onpositive.text.analysis.syntax;

import java.util.Map;
import java.util.Set;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.syntax.UniformSentencePartsParser;

public class UniformNounsParser extends UniformSentencePartsParser {

	public UniformNounsParser(AbstractWordNet wordNet) {
		super(wordNet, IToken.TOKEN_TYPE_UNIFORM_NOUN, new PartOfSpeech[]{PartOfSpeech.NOUN});
	}
	
	protected boolean refineGrammemSet(Set<Grammem> grammems, SyntaxToken token) {
		
		Set<Grammem> tokenGrammems = token.getAllGrammems();
		
		Set<Case> caseSet = extractGrammems(grammems, Case.class);
		Set<Case> tokenCaseSet = extractGrammems(tokenGrammems, Case.class);
		Map<Case, Case> matchedCase = matchCase(caseSet, tokenCaseSet);
		if(matchedCase==null||matchedCase.isEmpty()||(matchedCase.size()==1&&matchedCase.containsKey(Case.NOMN))){
			return false;
		}
		grammems.clear();
		grammems.addAll(matchedCase.keySet());
		return true;
	}

}
