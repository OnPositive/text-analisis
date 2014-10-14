package com.onpositive.semantic.words3;

import java.util.Arrays;

import com.onpositive.semantic.words2.SimpleWordNet;
import com.onpositive.semantic.words2.WordNetProvider;
import com.onpositive.semantic.words3.model.TextElement;
import com.onpositive.semantic.words3.model.WordRelation;

public class Test {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ReadOnlyWordNet ww=null;
		try {
			SimpleWordNet instance = (SimpleWordNet) WordNetProvider.getInstance();
			ww=new ReadOnlyWordNet(instance);
			WordRelation[] possibleGrammarForms = ww.getPossibleGrammarForms("политический деятель");
			TextElement word = (TextElement) possibleGrammarForms[0].getWord();
			if (word instanceof WordSequenceHandle){
				WordSequenceHandle s=(WordSequenceHandle) word;
				WordHandle[] words = s.getWords();
				System.out.println(Arrays.toString(words));
			}
			WordRelation[] semanticRelations = word.getSemanticRelations();
			System.out.println(Arrays.toString(semanticRelations));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}