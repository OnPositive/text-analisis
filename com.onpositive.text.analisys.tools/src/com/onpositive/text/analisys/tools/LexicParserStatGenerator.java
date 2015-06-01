package com.onpositive.text.analisys.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.syntax.SyntaxParser;

public class LexicParserStatGenerator extends SyntaxParser {

	public class Statistic {
		public int start;
		public int end;
		public List<String> structure;
		
		public Statistic() { start = end = 0; structure = null; }
		public Statistic(int start, int end, List<String> struct) { this.start = start; this.end = end; this.structure = struct; }
	}
	
	
	public LexicParserStatGenerator(AbstractWordNet wordnet) { super(wordnet);	}
	
	public List<IToken> parse(String str){
		setText(str);
		List<IToken> primitiveTokens = primitiveTokenizer.tokenize(str);
		List<IToken> lexicProcessed = lexicParsers.process(primitiveTokens);
		
		List<IToken> sentences = sentenceSplitter.split(lexicProcessed);

		List<IToken> result = new ArrayList<IToken>();
		
		for (IToken snt: sentences) {
	nextsen:
			for (IToken ch : snt.getChildren()) {
				if (!(ch instanceof WordFormToken)) continue nextsen;
				if (ch.getNext() == null && ch.getNextTokens() != null && ch.getNextTokens().size() > 1) {
					List<IToken> nt = ch.getNextTokens();
					boolean already = false;
					for (IToken n : nt) {
						if (n instanceof WordFormToken)
							if (already) continue nextsen;
							else already = true;
					}
				}
			}
			result.add(snt);
		}
		return result;
	}
	
	private WordFormToken getFirst(IToken snt) { 		
		for (IToken ch : snt.getChildren()) {
			if (ch instanceof WordFormToken)
				if (ch.getPrevious() == null && ch.getPreviousTokens() == null) {
				return (WordFormToken) ch;
			} else if (ch.getPrevious() != null) {
				IToken prev = ch.getPrevious();
				if (prev.getEndPosition() < snt.getStartPosition())
					return (WordFormToken) ch;
			} else if (ch.getPreviousTokens() != null)
				for (IToken prev : ch.getPreviousTokens()) 
					if (prev.getEndPosition() < snt.getStartPosition())
						return (WordFormToken) ch;
		}
		return null;
	}
	
	String meaningValue(WordFormToken owft) {		
		
		MeaningElement[] mes = owft.getMeaningElements();
		if ( mes == null || mes.length == 0) return "Unknown";		
		else {
			Set<String> parts = new HashSet<String>();
			for (MeaningElement me : mes) {
				PartOfSpeech pos = me.getPartOfSpeech(); 
				if (pos != null)
					parts.add(pos.toString());
			}
			Object[] parr = parts.toArray(); 
			if (parr.length == 0) return "NONE";
			StringBuilder sb = new StringBuilder(parr[0].toString());
			for (int i = 1; i < parr.length; i++)
				sb.append(" | " + parr[i].toString());
			
			return sb.toString();
		}
		
	}
	
	public List<Statistic> stats(List<IToken> sentences) {
		List<Statistic> result = new ArrayList<Statistic>();		
		for (IToken snt : sentences) {
			WordFormToken wft = getFirst(snt);
			if (wft == null) continue; 
			List<String> res = new ArrayList<String>();
			while (wft != null) {
				WordFormToken owft = wft;
				wft = null;
				
				res.add(meaningValue(owft));
				
				if (owft.getNext() != null)
					wft = (WordFormToken) owft.getNext();
				else {
					for (IToken t : owft.getNextTokens())
						if (t instanceof WordFormToken) {
							wft = (WordFormToken) t;
							break;
						}
				}
			}			
			result.add(new Statistic(snt.getStartPosition(), snt.getEndPosition(), res));
		}		
		return result;
	}
	
}
