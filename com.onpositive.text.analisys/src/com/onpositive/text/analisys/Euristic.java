package com.onpositive.text.analisys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.AbstractParser;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.lexic.WordFormToken;

public class Euristic {

	private static HashMap<Class<? extends AbstractParser>, List<Euristic>> registered = new HashMap<>(); 
	
	public static void register(Class<? extends AbstractParser> clazz, Euristic ... euristics)
	{
		Euristic eur = new Euristic(euristics);
		eur.type = EURISTIC_CONCAT;
		
		if (!registered.containsKey(clazz)) registered.put(clazz, new ArrayList<Euristic>());
		
		List<Euristic> list = registered.get(clazz);
		list.add(eur);
	}
	
	public static List<Euristic> match(Class<? extends AbstractParser> clazz) {  
		if (!registered.containsKey(clazz)) 
			registered.put(clazz, new ArrayList<Euristic>());
		
		return registered.get(clazz);
	}
	
	private boolean matchWord(IToken token) {
		if (!(token instanceof WordFormToken)) return false;
		WordFormToken wft = (WordFormToken) token;
		if (wft.getBasicForm().equals(this.word) == false) return false;
		
		return matchAny(token);
	}
	
	private boolean matchAny(IToken token) {
		if (!(token instanceof WordFormToken)) return false;
		WordFormToken wft = (WordFormToken) token;
		
		if (this.grammems.length == 0) return true;
		if (wft.getMeaningElements().length == 0) return false;
		Set<Grammem> gs = wft.getMeaningElements()[0].getGrammems();
		for (Grammem gr : this.grammems)
			if (gs.contains(gr) == false) return false;
		return true;
	}
	
	private boolean matchOr(IToken token) {
		if (euristics == null) return true;
		for (Euristic eur : euristics)
			if (eur.match(token) == false) return false;
		return true;
	}
	
	private boolean matchConcat(IToken[] tokens) {
		if (euristics == null) return true;
		if (tokens == null) return false;
		if (euristics.length > tokens.length) return false;
		
		for (int i = 0; i < euristics.length; i++) {
			if (euristics[i].match(tokens[i]) == false) return false;
		}
		
		return true;
	}
	
	public boolean match(IToken ... tokens) {
		switch (type) {
			case EURISTIC_WORD:
				if (tokens.length != 1) return false;
				else return matchWord(tokens[0]);
			case EURISTIC_GRAMMEM:
				if (tokens.length != 1) return false;
				else return matchAny(tokens[0]);
			case EURISTIC_OR:
				if (tokens.length != 1) return false;
				return matchOr(tokens[0]);
			case EURISTIC_CONCAT:
				return matchConcat(tokens);
			default:
				return false; // TODO: add other classes
		}
	}
	
	public static Euristic word(String word, Grammem ... gr) {
		return new Euristic(word, gr);
	}
	
	public static Euristic any(Grammem ... gr) {
		return new Euristic(gr);
	}
	
	
	public Euristic then(Euristic a) {
		Euristic eur = new Euristic(Arrays.asList(this, a).toArray(new Euristic[0]));
		eur.type = EURISTIC_CONCAT;
		return eur;
	}
	public static Euristic or (Euristic ... euristics) { 
		Euristic eur = new Euristic(euristics);
		eur.type = EURISTIC_OR;
		return eur; 
	}		
		
	static final int EURISTIC_WORD = 40001;
	static final int EURISTIC_GRAMMEM = 40002;
	static final int EURISTIC_CONCAT = 40003;
	static final int EURISTIC_OR = 40004;
	
	String word = null;
	Grammem[] grammems = null;
	
	int type;
	
	Euristic[] euristics = null;
	
	private Euristic(String word, Grammem[] grammems) {
		this.type = EURISTIC_WORD;
		this.word = word;
		this.grammems = grammems;
	}
	
	private Euristic(Grammem[] grammems) {
		this.type = EURISTIC_GRAMMEM;
		this.grammems = grammems;
	}
	
	private Euristic(Euristic[] euristics) {
		this.euristics = euristics; 
	}
	
	public static void Example() {

		
		Euristic.register(
				WordFormParser.class,
				
				word("части", PartOfSpeech.NOUN),
				any(PartOfSpeech.ADJF),
				or(any(PartOfSpeech.ADJF), any(PartOfSpeech.ADJS))
		);
	}
}




