package com.onpositive.text.analisys.tests;


import org.junit.Test;

import com.onpositive.semantic.parsing.DefinitionExractor;
import com.onpositive.semantic.words2.WordNetProvider;
import com.onpositive.semantic.words3.model.WordRelation;

import junit.framework.TestCase;

public class SequenceSeparatorTests extends TestCase {

	@Test
	public void test() {
		WordRelation[] posibleWords = WordNetProvider.getInstance().getPosibleWords("политический деятель");
		System.out.println(posibleWords);
	}
}
