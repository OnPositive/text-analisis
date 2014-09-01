package com.onpositive.text.analisys.tests;


import org.junit.Test;

import com.onpositive.semantic.words2.WordNetProvider;
import com.onpositive.semantic.words2.WordRelation;

import junit.framework.TestCase;

public class SequenceSeparatorTests extends TestCase {

	@Test
	public void test() {
		WordRelation[] posibleWords = WordNetProvider.getInstance().getPosibleWords("молоко");
		assertTrue(posibleWords.length>0);
	}
}
