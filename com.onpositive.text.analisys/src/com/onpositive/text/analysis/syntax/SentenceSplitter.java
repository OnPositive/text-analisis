package com.onpositive.text.analysis.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.SemanGramem;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.WordFormToken;

public class SentenceSplitter {
	
	private static final ArrayList<Grammem> abbr = new ArrayList<Grammem>(Arrays.asList(SemanGramem.ABBR)); 
	
	public List<IToken> split(List<IToken> tokens){
		
		ArrayList<IToken> sentences = new ArrayList<IToken>();
		
		int start = 0;
		int size = tokens.size();
		for(int i = 0 ; i < size ; i++ )
		{
			IToken token = tokens.get(i);
			if(token.getType()!=IToken.TOKEN_TYPE_SYMBOL){
				continue;
			}
			
			String value = token.getStringValue();
			if(!value.equals(".")){
				continue;
			}
			
			if(i==0){
				continue;
			}
			
			IToken prev = tokens.get(i-1);
			String prevValue = prev.getStringValue();
			int prevType = prev.getType();
			if(prevType==IToken.TOKEN_TYPE_LETTER){
				if(prevValue.length()==1){
					continue;
				}
			}
			else if(prevType==IToken.TOKEN_TYPE_WORD_FORM){
				WordFormToken wft = (WordFormToken) prev;
				if(wft.hasAnyGrammem(abbr)){
					continue;
				}
			}
			SentenceToken sent = new SentenceToken(tokens.get(start).getStartPosition(), token.getEndPosition());
			sent.setChildren(tokens.subList(start, i+1));
			sentences.add(sent);
			start = i + 1;
		}
		if(start<size){
			SentenceToken sent = new SentenceToken(tokens.get(start).getStartPosition(), tokens.get(size-1).getEndPosition());
			sent.setChildren(tokens.subList(start, size));
			sentences.add(sent);
		}
		return sentences;
	}

}
