package com.onpositive.text.analisys.tests.euristics;

import java.util.ArrayList;
import java.util.List;

import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.syntax.SyntaxToken.GrammemSet;

public class DefaultComparator implements ITokenComparator {

	@Override
	public List<Grammem> calculateMissed(SimplifiedToken etalonToken, WordFormToken comparedToken) {
		List<Grammem> missed = new ArrayList<Grammem>();
		List<GrammemSet> grammemSets = comparedToken.getGrammemSets();
		for (GrammemSet grammemSet : grammemSets) {
			for (Grammem grammem : grammemSet.grammems()) {
				if (conainsGrammem(etalonToken, grammem)) {
					missed.add(grammem);
				}
			}
		}
		return missed;
	}

	protected boolean conainsGrammem(SimplifiedToken etalonToken, Grammem grammem) {
		return !etalonToken.getGrammems().contains(grammem);
	}

}
