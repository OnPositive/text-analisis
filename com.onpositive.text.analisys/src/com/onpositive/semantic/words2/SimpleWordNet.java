package com.onpositive.semantic.words2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class SimpleWordNet extends WordNet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected HashMap<String, RelationTarget> wordMap = new HashMap<String, RelationTarget>();
	protected ArrayList<RelationTarget> words = new ArrayList<RelationTarget>();

	protected HashMap<String, WordFormTemplate> wordTemplateMap = new HashMap<String, WordFormTemplate>();
	protected HashMap<String, ArrayList<WordRelation>> wordforms = new HashMap<String, ArrayList<WordRelation>>();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Iterator<Word> iterator() {
		return new ArrayList(wordMap.values()).iterator();
	}

	public void write(String string) throws FileNotFoundException, IOException {
		ObjectOutputStream os = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream(string))) {

			@Override
			public void defaultWriteObject() throws IOException {
				super.defaultWriteObject();
			}
		};
		os.writeObject(this);
		os.close();
	}

	public static WordNet read(String path) throws FileNotFoundException,
			IOException {
		ObjectInputStream si = new ObjectInputStream(new BufferedInputStream(
				new FileInputStream(path)));
		try {
			Object s = si.readObject();
			si.close();
			SimpleWordNet s2 = (SimpleWordNet) s;
			s2.prepareWordSeqs();
			return s2;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public WordSequence parse(String string) {
		return parse(string, false);
	}

	public WordSequence parse(String string, boolean expand) {
		String[] split = string.split(" ");
		ArrayList<Word> w = new ArrayList<Word>();
		ArrayList<WordRelation[]> options = new ArrayList<WordRelation[]>();
		int qq = 0;
		for (String s : split) {
			WordRelation[] posibleWords = getPosibleWords(s.toLowerCase());
			if (posibleWords == null || posibleWords.length == 0) {
				if (qq != 0) {
					if (Character.isUpperCase(s.charAt(0))) {
						final SimpleWord word2 = new SimpleWord(s,
								Integer.MAX_VALUE);
						word2.setKind(SimpleWord.NOUN);
						word2.setFeature(SimpleWord.FEATURE_NAME);
						posibleWords = new WordRelation[] { new WordRelation(
								this, word2, 0) {
							/**
									 * 
									 */
									private static final long serialVersionUID = 1L;

							@Override
							public RelationTarget getWord() {
								return word2;
							}
						} };
					}
				}
				if (s.endsWith("ые")) {
					final SimpleWord word2 = new SimpleWord(s,
							Integer.MAX_VALUE);
					word2.setKind(SimpleWord.ADJ);
					posibleWords = new WordRelation[] { new WordRelation(this,
							word2, 0) {
						/**
								 * 
								 */
								private static final long serialVersionUID = 1L;

						@Override
						public RelationTarget getWord() {
							return word2;
						}
					} };
				}
			}
			qq++;
			Word word = getSingleWord(posibleWords);
			if (word == null) {
				if (s.contains("-")) {
					String[] split2 = s.split("-");
					for (String m : split2) {
						WordRelation[] posibleWords2 = getPosibleWords(m
								.toLowerCase());
						if (posibleWords2 != null && posibleWords2.length > 0) {
							word = getSingleWord(posibleWords2);
							if (word != null) {
								w.add(word);
								options.add(posibleWords);
							}
						}
					}
				}
				// return null;
				continue;
			} else {
				w.add(word);
				options.add(posibleWords);
			}
		}
		// now we should collapse possible sequences;

		int size = w.size();
		ArrayList<RelationTarget> targets = new ArrayList<RelationTarget>();
		for (int a = 0; a < size; a++) {
			Word word = w.get(a);
			Sequences sequences = seq.get(word);
			if (sequences != null) {
				SimpleSequence match = sequences.match(a, w);
				if (match != null) {
					a += match.words.length - 1;
					targets.add(match);
					continue;
				}
			}
			targets.add(word);
		}
		WordSequence wordSequence = new WordSequence(targets);
		for (int a = 0; a < wordSequence.targets.length; a++) {
			if (wordSequence.targets[a] == w.get(a)) {
				wordSequence.sequence.put(wordSequence.targets[a],
						options.get(a));
			}
		}
		return wordSequence;
	}

	@Override
	protected void registerWord(Word word) {
		if (wordMap.containsKey(word.getBasicForm())) {
			if (!word.equals(wordMap.get(word.getBasicForm()))) {
				throw new IllegalStateException();
			}
		} else {
			words.add(word);
			wordMap.put(word.getBasicForm(), word);
		}
	}

	static final WordRelation[] wordRelations = new WordRelation[0];

	@Override
	public WordRelation[] getPosibleWords(String wf) {
		ArrayList<WordRelation> arrayList = wordforms.get(wf);
		if (arrayList == null) {
			RelationTarget word = wordMap.get(wf);
			if (word != null) {
				return new WordRelation[] { new WordRelation(this, word.id(), 0) };
			}
			// now we should try to parse sequence
			return wordRelations;
		}
		return arrayList.toArray(new WordRelation[arrayList.size()]);
	}

	@Override
	protected void registerWordForm(String wf, WordRelation form) {
		wf = wf.toLowerCase();
		ArrayList<WordRelation> arrayList = wordforms.get(wf);
		if (arrayList == null) {
			arrayList = new ArrayList<WordRelation>();
			wordforms.put(wf, arrayList);
		}
		arrayList.add(form);
	}

	@Override
	public RelationTarget getWord(String basicForm) {
		return wordMap.get(basicForm);
	}

	@Override
	public WordFormTemplate findTemplate(String string) {
		return wordTemplateMap.get(string);
	}

	protected void registerTemplate(WordFormTemplate template) {
		wordTemplateMap.put(template.title, template);
	}

	@Override
	public void markRedirect(String from, String to) {

	}

	public RelationTarget getWord(int id) {
		return words.get(id);
	}

	@Override
	protected Word getOrCreateWord(String lowerCase) {
		if (wordMap.containsKey(lowerCase)) {
			return (Word) wordMap.get(lowerCase);
		}
		Word s = new SimpleWord(lowerCase, words.size());
		registerWord(s);
		return s;
	}

	@Override
	protected void init() {
		for (Word w : this) {
			SimpleWord s = (SimpleWord) w;
			if (s.template != null) {
				s.template.register(w);
			}
		}
	}

	@Override
	protected RelationTarget getOrCreateRelationTarget(String s) {
		String lowerCase = s.toLowerCase();
		return getOrCreateWord(lowerCase);
	}

	protected void prepareWordSeqs() {
		for (AbstractRelationTarget w : this) {
			if (!(w instanceof Word)) {
				continue;
			}
			WordRelation[] relations = w.getRelations();
			for (WordRelation q : relations) {
				if (q.owner == null) {
					q.owner = this;
				}
				if (q.relation == WordRelation.SPECIALIZATION) {
					AbstractRelationTarget word = (AbstractRelationTarget) getWord(q.word);
					word.registerRelation(
							WordRelation.GENERALIZATION_BACK_LINK, w);
				}
				if (q.relation == WordRelation.GENERALIZATION) {
					AbstractRelationTarget word = (AbstractRelationTarget) getWord(q.word);
					word.registerRelation(
							WordRelation.SPECIALIZATION_BACK_LINK, w);
				}
				if (q.relation == WordRelation.SYNONIM) {
					AbstractRelationTarget word = (AbstractRelationTarget) getWord(q.word);
					word.registerRelation(WordRelation.SYNONIM_BACK_LINK, w);
				}
			}
		}
		l2: for (AbstractRelationTarget q : this) {
			if (!(q instanceof Word)) {
				continue;
			}
			Word w = (Word) q;
			if (w.getBasicForm().indexOf(' ') != -1) {
				String[] split = w.getBasicForm().split(" ");
				ArrayList<Word> sequence = new ArrayList<Word>();
				for (String s : split) {
					Word singlePossibleWord = getSinglePossibleWord(s);
					if (singlePossibleWord == null) {
						continue l2;
					}
					sequence.add(singlePossibleWord);
				}
				SimpleSequence s = new SimpleSequence(
						sequence.toArray(new Word[sequence.size()]), w.id());
				s.relations = w.relations;
				registerSequence(s);
				// register sequence
				words.set(w.id(), s);
				wordMap.put(w.getBasicForm(), s);
			}
		}
	}

	protected class SimpleSequence extends AbstractRelationTarget implements
			Serializable {

		protected Word[] words;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + id;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SimpleSequence other = (SimpleSequence) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (id != other.id)
				return false;
			return true;
		}

		public SimpleSequence(Word[] words, int id) {
			super();
			this.words = words;
			this.id = id;
		}

		protected int id;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Word[] getWords() {
			return words;
		}

		@Override
		public int id() {
			return id;
		}

		@Override
		public String toString() {
			return Arrays.toString(words);
		}

		private SimpleWordNet getOuterType() {
			return SimpleWordNet.this;
		}

		public Word get(int i) {
			return words[i];
		}

		public boolean match(ArrayList<Word> w, int a) {
			for (int i = a; i < a + words.length; i++) {
				if (i >= w.size()) {
					return false;
				}
				if (!words[i - a].equals(w.get(i))) {
					return false;
				}
			}
			return true;
		}

	}

	protected class Sequences implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected ArrayList<SimpleSequence> ws = new ArrayList<SimpleSequence>();

		public void add(SimpleSequence sequence) {
			ws.add(sequence);
			System.out.println(sequence);
		}

		public SimpleSequence match(int a, ArrayList<Word> w) {
			for (SimpleSequence q : ws) {
				if (q.match(w, a)) {
					return q;
				}
			}
			return null;

		}

	}

	protected HashMap<Word, Sequences> seq = new HashMap<Word, SimpleWordNet.Sequences>();

	private Sequences registerSequence(SimpleSequence sequence) {
		if (seq == null) {
			seq = new HashMap<Word, SimpleWordNet.Sequences>();
		}
		Word key = sequence.get(0);
		Sequences sequences = seq.get(key);
		if (sequences == null)

		{
			sequences = new Sequences();
			seq.put(key, sequences);
		}
		sequences.add(sequence);
		return sequences;
	}

	public Word getSinglePossibleWord(String s) {
		WordRelation[] posibleWords = getPosibleWords(s);
		return getSingleWord(posibleWords);
	}

	private Word getSingleWord(WordRelation[] posibleWords) {
		if (posibleWords == null || posibleWords.length == 0) {
			return null;
		}
		if (posibleWords.length == 1) {
			return (Word) posibleWords[0].getWord();
		}
		int id = -1;
		for (WordRelation r : posibleWords) {
			RelationTarget word = getWord(r.word);
			if (word instanceof Word) {
				Word wr = (Word) word;
				if (wr.isNoun()) {
					if (r.relation == NounFormRule.NOM_SG) {
						return wr;
					}
					if (r.relation == NounFormRule.NOM_PL) {
						return wr;
					}
					if (r.relation == 0) {
						return wr;
					}
				}
			}
			if (id == -1) {
				id = r.word;
			} else if (id != r.word) {
				return null;
			}
		}
		if (id == -1) {
			return null;
		}
		return (Word) getWord(id);
	}
}