package com.onpositive.semantic.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import com.onpositive.semantic.references.TermReference;
import com.onpositive.semantic.words2.NounFormRule;
import com.onpositive.semantic.words2.RelationTarget;
import com.onpositive.semantic.words2.Word;
import com.onpositive.semantic.words2.WordNetProvider;
import com.onpositive.semantic.words2.WordRelation;

public class DefinitionExractor {
	
	protected static class FormMatch {
		protected int rating;
		protected RelationTarget form;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((form == null) ? 0 : form.hashCode());
			result = prime * result + rating;
			return result;
		}
		public int match(TermReference reference) {
			ArrayList<ParsedWord> words2 = reference.getWords();
			ParsedWord startWord = words2.get(0);
			int total = 0;
			if (rating>2){
				return 0;
			}
			for (int a = -1; a > -10; a--) {
				ParsedWord lookup = startWord.lookup(a);
				if (lookup == null) {
					break;
				}
				HashSet<Word> ws = lookup.ws;
				for (Word wq : ws) {
					if (wq.equals(form)) {

						System.out.println(wq);
						total += 20 + a;
						break;
					}
				}
			}
			if (total > 0) {
				if (!additionalFactors.isEmpty()) {
					for (Word q:additionalFactors){
						for (int a = -1; a > -10; a--) {
							ParsedWord lookup = startWord.lookup(a);
							if (lookup == null) {
								break;
							}
							HashSet<Word> ws = lookup.ws;
							for (Word wq : ws) {
								if (wq.equals(q)) {
									//System.out.println(wq);
									total += 10 + a;
									break;
								}
							}
						}			
					}
				}
			}
			return total;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FormMatch other = (FormMatch) obj;
			if (form == null) {
				if (other.form != null)
					return false;
			} else if (!form.equals(other.form))
				return false;
			if (rating != other.rating)
				return false;
			return true;
		}

		public FormMatch(int rating, RelationTarget form,
				HashSet<Word> additionalFactors) {
			super();
			this.rating = rating;
			this.form = form;
			this.additionalFactors = additionalFactors;
		}

		@Override
		public String toString() {
			return form + ":" + rating + additionalFactors;
		}

		protected HashSet<Word> additionalFactors = new HashSet<Word>();


	}
	
	static HashSet<String> wordsToIgnore = new HashSet<String>();

	static {
		wordsToIgnore.add("год");
		wordsToIgnore.add("€нварь");
		wordsToIgnore.add("февраль");
		wordsToIgnore.add("март");
		wordsToIgnore.add("апрель");
		wordsToIgnore.add("май");
		wordsToIgnore.add("июнь");
		wordsToIgnore.add("июль");
		wordsToIgnore.add("август");
		wordsToIgnore.add("сент€брь");
		wordsToIgnore.add("окт€брь");
		wordsToIgnore.add("но€брь");
		wordsToIgnore.add("декабрь");
		wordsToIgnore.add("вид");
		wordsToIgnore.add("истори€");
		wordsToIgnore.add("без");
		wordsToIgnore.add("категори€");
		wordsToIgnore.add("страница");
		wordsToIgnore.add("викификаци€");
		wordsToIgnore.add("стать€");
		wordsToIgnore.add("список");
		wordsToIgnore.add("алфавит");
		wordsToIgnore.add("ссылка");
		wordsToIgnore.add("статус");
	}

	public void parseWordsFromPlainText(String plainTextAbstract,
			HashMap<String, FormMatch> matches) {
		ParsedText ts = new ParsedText(plainTextAbstract);
		if (!ts.sequences.isEmpty()) {
			ParsedSequence parsedSequence = ts.sequences.get(0);
			ArrayList<ISentenceElement> all = parsedSequence.all;
			// searching for separator;
			int index = -1;
			int level = 0;
			int aIndex = -1;
			int a = 0;
			boolean hasRParent = false;
			for (ISentenceElement q : all) {
				if (q instanceof SignElement) {
					SignElement se = (SignElement) q;
					char content = se.getContent();
					if (content == '(') {
						level++;
						// we have separator;
					}
					if (content == ')') {
						level--;
						hasRParent = true;
						// we have separator;
					}
					if (content == 'Ч') {
						if (level == 0 && aIndex == -1) {
							aIndex = a;
						}
						if (hasRParent) {
							index = a;
						}
						// we have separator;
					}
				}
				a++;
			}
			if (index != -1 && aIndex == -1) {
				aIndex = index;
			}
			if (aIndex == -1) {
				aIndex = 0;
			}
			// now we know were intereting part starts;
			int size = all.size();
			int rating = 5;
			for (a = aIndex; a < size; a++) {
				ISentenceElement element = all.get(a);
				if (element instanceof SignElement) {
					SignElement se = (SignElement) element;
					if (se.getContent() == ',' || se.getContent() == ';') {
						rating += 5;
					}
				}
				if (element instanceof ParsedWord) {
					ParsedWord word = (ParsedWord) element;
					int indexOf2 = word.content.indexOf('-');
					if (indexOf2 != -1) {
						String[] split = word.content.split("-");
						for (String s : split) {
							WordRelation[] posibleWords = WordNetProvider
									.getInstance().getPosibleWords(s);
							for (WordRelation r : posibleWords) {
								RelationTarget word2 = r.getWord();
								if (word2 instanceof Word) {
									Word z = (Word) word2;
									if (z.isNoun()
											&& z.getBasicForm().length() > 2) {
										processWord(matches, rating, word, z,
												posibleWords);
									}
								}
							}
						}
					}
					if (word.isNoun() || (rating == 5 && !word.isAdjective())) {
						if (word.content.length() <= 2) {
							continue;
						}

						Word soleWord = word.soleWord;
						/*if (soleWord != null
								&& soleWord.hasFeature(Word.FEATURE_NUMBER)) {
							continue;
						}*/
						if (soleWord == null) {
							continue;
						}
						if (wordsToIgnore
								.contains(word.soleWord.getBasicForm())) {
							continue;
						}
						processWord(matches, rating, word, soleWord,
								word.wordForms);
						rating++;
					}
				}
			}
		}
	}

	public int processWord(HashMap<String, FormMatch> matches, int rating,
			ParsedWord word, Word soleWord, WordRelation[] rs) {
		int hs = rating;
		if (soleWord.hasFeature(Word.FEATURE_TOPONIM)) {
			return hs;
		}
		for (WordRelation r : rs) {
			if (r.relation == NounFormRule.NOM_PL
					|| (r.relation == NounFormRule.NOM_SG)) {
				hs -= 5;
			}
		}
		if (soleWord != null) {
			FormMatch match = new FormMatch(hs, soleWord, new HashSet<Word>());
			String lowerCase = soleWord.getBasicForm().toLowerCase();
			matches.put(lowerCase, match);
			if (word.sourceArcs != null) {
				for (SyntaxArc arc : word.sourceArcs) {
					Word soleWord1 = arc.target.soleWord;
					if (soleWord1 != null) {
						match.additionalFactors.add(soleWord1);
					}
				}
			}
		}
		return hs;
	}
}