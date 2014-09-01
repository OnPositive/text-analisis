package com.onpositive.semantic.words2;



public class BasicTest {


	public static void main(String[] args) {
		WordNet instance = WordNetProvider.getInstance();
		SimpleWordNet ww=(SimpleWordNet) instance;
		ww.prepareWordSeqs();
		test(instance);
	}
	
	private static void test(WordNet instance) {
		SimpleWordNet ww=(SimpleWordNet) instance;
		WordSequence parse = ww.parse("экскаваторы");
		WordRelation[] posibleWords = ww.getPosibleWords("промышленный");		
		RelationTarget word = posibleWords[0].getWord();
		System.out.println(word);
	}
}