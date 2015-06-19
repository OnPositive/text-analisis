package com.onpositive.text.analisys.tools;

import java.util.HashMap;
import java.util.List;

import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.ShortArrayList;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.syntax.SyntaxParser;

public class TagStatGenerator extends SyntaxParser {

	AbstractWordNet wordnet;
	
	public TagStatGenerator(AbstractWordNet wordnet) {
		super(wordnet);
		this.wordnet = wordnet;		
	}
	
	public List<IToken> parse(String str){
		setText(str);
		List<IToken> primitiveTokens = primitiveTokenizer.tokenize(str);
		List<IToken> lexicProcessed = lexicParsers.process(primitiveTokens);
		
		List<IToken> sentences = sentenceSplitter.split(lexicProcessed);
		
		return sentences;
	}
	
	public ObjectArrayList<short[]> stat(IToken token) {
		ObjectArrayList<short[]> result = new ObjectArrayList<short[]>();
		ShortArrayList cresult = new ShortArrayList(); 

		if (token.childrenCount() == 0) return result;
		
		for (IToken ch : token.getChildren()) {
			if (!(ch instanceof WordFormToken) || ch.getConflicts().size() > 0 || ((WordFormToken)ch).getMeaningElements().length < 1) {
				if (cresult.size() > 0) {
					result.add(cresult.toArray());
					cresult.clear();
				}				
			} else
				cresult.add(((WordFormToken) ch).getMeaningElements()[0].getGrammemCode());			
		}
		return result;
	}
	
	public ObjectArrayList<short[]> stats(List<IToken> sentences) { 
		ObjectArrayList<short[]> result = new ObjectArrayList<short[]>();
		for (IToken token : sentences) 
			for (ObjectCursor<short[]> v : stat(token))
				result.add(v.value);
		return result;
	}
}
