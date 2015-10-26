package com.onpositive.text.analisys.tests.euristics;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.syntax.SyntaxToken.GrammemSet;

public class SimplifiedToken {
	
	private String word;
	
	private Collection<Grammem> grammems;

	public SimplifiedToken(String word, Collection<Grammem> grammems) {
		super();
		this.word = word;
		this.grammems = grammems;
	}

	public String getWord() {
		return word;
	}

	public Collection<Grammem> getGrammems() {
		return grammems;
	}
	
	public List<Grammem> getMissedGrammems(WordFormToken wordFormToken) {
		List<GrammemSet> grammemSets = wordFormToken.getGrammemSets();
		List<Grammem> missedGrammems = grammems.stream().filter(grammem -> {for (GrammemSet grammemSet : grammemSets) {
				if (grammemSet.hasGrammem(grammem)) {
					return true;
				}
			} 
			return false;
		}).collect(Collectors.toList());
		return missedGrammems;
			
	}
	
	public boolean wordEquals(WordFormToken comparedToken) {
		String stringValue = comparedToken.getShortStringValue();
		return word.trim().equalsIgnoreCase(stringValue.trim());
	}
	
	public boolean hasValidGrammemSet() {
		for (Grammem grammem : grammems) {
			if (grammem instanceof PartOfSpeech) {
				return true;
			}
		}
		return false;
	}
	
	public PartOfSpeech getPartOfSpeech() {
		for (Grammem grammem : grammems) {
			if (grammem instanceof PartOfSpeech) {
				return (PartOfSpeech) grammem;
			}
		}
		return null;
	}
		
	@Override
	public String toString() {
		return word + ", Grammmems: " + grammems.toString();
	}
	
}
