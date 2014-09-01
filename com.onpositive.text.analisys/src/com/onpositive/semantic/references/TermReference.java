package com.onpositive.semantic.references;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.onpositive.semantic.parsing.ParsedWord;

public class TermReference {
	
	protected ArrayList<ParsedWord> words;
	
	public Collection<ITerm>terms(){
		return Collections.singleton(start);
	}
	
	protected ITerm start;
	protected int offset;
	protected int length;

	public TermReference(ArrayList<ParsedWord> ws, ITerm basicTerm) {
		words=ws;
		this.offset=ws.get(0).offset;
		this.start=basicTerm;
		ParsedWord parsedWord = ws.get(ws.size()-1);
		this.length=parsedWord.offset+parsedWord.length-this.offset;
	}
	public int rating;
	
	public int getOffset() {
		return offset;
	}	
	public int getLength() {
		return length;
	}
	
	@Override
	public String toString() {
		return start.toString();
	}
	
	public ArrayList<ParsedWord> getWords() {
		return words;
	}
}
