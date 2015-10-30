package com.onpositive.text.analisys.tests.euristics;

import java.util.List;

import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.text.analysis.syntax.SyntaxToken;

public interface ITokenComparator {
	
	public List<Grammem> calculateWrong(SimplifiedToken etalonToken, SyntaxToken comparedToken);
	

}
