package com.onpositive.semantic.parsing;

import java.util.ArrayList;
import java.util.HashSet;

import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.words2.Word;
import com.onpositive.semantic.words2.WordNetProvider;
import com.onpositive.semantic.words3.model.RelationTarget;
import com.onpositive.semantic.words3.model.WordRelation;

public class ParsedWord implements ISentenceElement{
	private static final ParsedWord PARSED_WORD = new ParsedWord("*", -1, null);
	public String content;
	protected ParsedSequence sequence;
	public int offset;
	public int length;

	
	protected GrammarRelation[] wordForms;
	protected int index;
	protected int indexInAll;
	

	protected ArrayList<SyntaxArc>sourceArcs;
	protected ArrayList<SyntaxArc>targetArcs;
	
	public ParsedWord(String content, int offset, ParsedSequence sequence) {
		super();
		this.sequence = sequence;
		this.content = content;
		this.offset = offset;
		this.length = content.length();
		doBasicMorphology(content);
	}
	
	protected HashSet<Word>ws=new HashSet<Word>();

	public void doBasicMorphology(String content) {
		
		wordForms = WordNetProvider.getInstance().getPosibleWords(content);
		if (wordForms.length == 0) {
			if (Character.isUpperCase(content.charAt(0))) {
				wordForms = WordNetProvider.getInstance().getPosibleWords(
						content.toLowerCase());
			} else if (Character.isLowerCase(content.charAt(0))) {
				wordForms = WordNetProvider.getInstance().getPosibleWords(
						Character.toUpperCase(content.charAt(0))
								+ content.substring(1));
			}
		}
		if (wordForms.length==0){
			content=content.replace('�','�');
		}
		wordForms = WordNetProvider.getInstance().getPosibleWords(content);
		if (wordForms.length == 0) {
			if (Character.isUpperCase(content.charAt(0))) {
				wordForms = WordNetProvider.getInstance().getPosibleWords(
						content.toLowerCase());
			} else if (Character.isLowerCase(content.charAt(0))) {
				wordForms = WordNetProvider.getInstance().getPosibleWords(
						Character.toUpperCase(content.charAt(0))
								+ content.substring(1));
			}
		}
		for (WordRelation q:wordForms){
			RelationTarget word = q.getWord();
			if (word instanceof Word){
				ws.add((Word) word);
			}
		}
		if (ws.size()==1){
			soleWord=ws.iterator().next();
		}
	}
	
	Word soleWord;

	public boolean matchesLemma(String text) {
		String content2 = content;
		boolean weakEquivalence = weakEquivalence(text, content2);
		if (!weakEquivalence) {
			WordRelation[] posibleWords = wordForms;
			for (WordRelation w : posibleWords) {
				RelationTarget word = w.getWord();
				com.onpositive.semantic.words2.Word[] words = word.getWords();
				if (words.length == 1) {
					if (weakEquivalence(words[0].getBasicForm(), text)) {
						return true;
					}
				}
			}
		}
		return weakEquivalence;
	}

	public boolean weakEquivalence(String text, String content2) {
		boolean equals = content2.toLowerCase().equals(text.toLowerCase());
		if (equals) {
			return true;
		}
		String replace = content2.replace('�', '�');
		equals = replace.equals(text);
		if (equals) {
			return true;
		}
		equals = text.replace('�', '�').equals(content2);
		if (equals) {
			return true;
		}
		equals = text.replace('�', '�').equals(replace);
		if (equals) {
			return true;
		}
		equals = text.replace('�', '�').equals(replace);
		if (equals) {
			return true;
		}
		return false;
	}

	public static boolean looksLikeId(String content) {
		boolean goodId = false;
		// if (pageId != -1) {

		if (content.contains("-")) {
			goodId = true;
		}
		int count = 0;
		for (char c : content.toCharArray()) {
			if (c == ' ') {
				count++;
			}
			if (c > 'a' && c < 'z') {
				goodId = true;
			}
			if (c > 'A' && c < 'Z') {
				goodId = true;
			}
		}
		if (count > 1) {
			return true;
		}
		if (!goodId && content.length() > 1) {
			goodId = true;
			for (char c : content.toCharArray()) {
				if (!Character.isUpperCase(c)) {
					goodId = false;
					break;
				}
			}
		}
		// }
		return goodId;
	}

	public int mark = -1;
	boolean qts;
	boolean qte;

	public boolean isStartOfSequence;

	ParsedWord lookup(int pos) {
		if (pos < 0 && index + pos >= 0) {
			return sequence.words.get(index + pos);
		}
		if (pos >= 0 && index + pos < sequence.words.size()) {
			return sequence.words.get(index + pos);
		}
		return null;
	}

	@Override
	public String toString() {
		return content;
	}

	public ParsedWord matchNear(RelationTarget q) {
		Word[] words = q.getWords();
		l2:for (Word w : words) {
			for (int a = index - 1; a >= 0; a--) {
				ParsedWord parsedWord = sequence.words.get(a);
				if (parsedWord.content.length()<=2){
					continue;
				}
				if(parsedWord.isVerb()){
					break;
				}
				if (parsedWord.matchWord(w)){
					return parsedWord;
				}
			}
			return null;
		}
		return PARSED_WORD;
	}

	private boolean isVerb() {
		return soleWord!=null&&soleWord.isVerb();
	}

	public boolean matchWord(Word w) {
		for(Word r:ws){
			if (r.matchRelated(w)&&!w.hasFeature(Word.FEATURE_TOPONIM)){
				return true;
			}
		}
		return false;
	}

	public boolean looksLikeId() {
		if(Character.isUpperCase(content.charAt(0))){
		
			return true;
		}
		ParsedWord lookup = lookup(1);
		if (lookup!=null){
			if(Character.isUpperCase(lookup.content.charAt(0))){
				return true;
			}	
		}
		return false;
	}

	public boolean isAdjective() {
		return soleWord!=null&&soleWord.isAdjective();
	}
	
	public boolean isNoun() {
		return soleWord!=null&&soleWord.isNoun();
	}

	public boolean isUpperCase() {
		return Character.isUpperCase(content.charAt(0));
	}
}