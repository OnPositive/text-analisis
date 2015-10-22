package com.onpositive.text.analisys.tests.euristics;

import java.util.List;

import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.text.analysis.lexic.WordFormToken;

public interface ITokenComparator {
	
	public List<Grammem> calculateMissed(SimplifiedToken etalonToken, WordFormToken comparedToken);
	

}