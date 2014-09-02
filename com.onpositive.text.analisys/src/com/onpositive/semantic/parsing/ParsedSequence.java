package com.onpositive.semantic.parsing;
import java.util.ArrayList;

public class ParsedSequence {
	protected ArrayList<ParsedWord> words;
	protected ArrayList<ISentenceElement> all;
	protected ParsedText text;

	public ParsedSequence(ParsedText text) {
		;
		this.text = text;
	}

	protected ParsedWord addWord(int offset, String content) {
		boolean fw = false;
		if (content == null || content.length() == 0) {
			return null;
		}
		if (words == null) {
			words = new ArrayList<ParsedWord>();
			fw = true;
		}
		if (all == null) {
			all = new ArrayList<ISentenceElement>();
		}
		ParsedWord e = new ParsedWord(content, offset, this);
		e.index = words.size();
		e.indexInAll=all.size();
		e.isStartOfSequence = fw;
		words.add(e);
		all.add(e);
		return e;
	}

	protected void addSign(int offset, char content) {
		if (all == null) {
			all = new ArrayList<ISentenceElement>();
		}
		all.add(new SignElement(offset, content));
	}

	public boolean isEmpty() {
		return words == null || words.size() == 0;
	}

	protected void reconcile() {
		markAdjectivesToNouns();
		markParensRegions();
		markExtensions();
	}

	private void markExtensions() {

	}

	public void markParensRegions() {
		ParsedWord lw = null;
		ParsedWord tp = null;
		int level = 0;
		ArrayList<ParsedWord> adj = new ArrayList<ParsedWord>();
		ArrayList<ParsedWord> copy = null;
		for (ISentenceElement el : all) {
			if (el instanceof SignElement) {
				char c = ((SignElement) el).getContent();
				if (c == '(') {
					tp = lw;
					adj.clear();
					level++;
				}
				if (c == ')') {
					level--;
					if (level == 0) {
						if (tp != null) {
							for (ParsedWord w : adj) {
								new SyntaxArc(tp, SyntaxArc.PARENS, w);
							}
							tp = null;
						}
					} else {
						copy = new ArrayList<ParsedWord>(adj);
					}
				}
			} else {
				lw = (ParsedWord) el;
			}
		}
		if (tp != null && copy != null) {
			for (ParsedWord w : copy) {
				new SyntaxArc(tp, SyntaxArc.PARENS, w);
			}
			tp = null;
		}
	}

	public void markAdjectivesToNouns() {
		ArrayList<ParsedWord> adj = new ArrayList<ParsedWord>();
		for (ISentenceElement a : all) {
			if (a instanceof ParsedWord) {
				ParsedWord w=(ParsedWord) a;
				if (w.isNoun()) {
					if (!adj.isEmpty()) {
						for (ParsedWord q : adj) {
							new SyntaxArc(w, SyntaxArc.DESCRIPTION, q);
						}
					}
				}

				if (w.isAdjective()) {
					adj.add(w);
				} else {
					adj.clear();
				}
			}
			else{
				adj.clear();
			}
		}
	}

	public ArrayList<ParsedWord> getWords() {
		return words;
	}
}