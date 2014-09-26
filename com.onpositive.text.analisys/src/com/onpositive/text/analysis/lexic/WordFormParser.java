package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.words2.WordNet;
import com.onpositive.semantic.words2.WordRelation;
import com.onpositive.text.analysis.IToken;

public class WordFormParser extends AbstractParser {

	public WordFormParser(WordNet wordNet) {
		super();
		this.wordNet = wordNet;
	}
	
	WordNet wordNet;

	@Override
	protected Set<IToken> combineUnits(Stack<IToken> sample) {
		
		StringBuilder bld = new StringBuilder();
		for(IToken unit : sample){
			if(unit.getType()==IToken.TOKEN_TYPE_LETTER){
				bld.append(unit.getStringValue());
				bld.append(" ");
			}
			else{
				//symbol or non-breaking whitespace
				int length = bld.length();
				int lastInd = length-1;
				if(lastInd>=0){
					char lastChar = bld.charAt(lastInd);
					if(lastChar==' '){
						bld.delete(lastInd, length);					
					}
				}
				bld.append(unit.getStringValue());
			}
		}
		
		String str = bld.toString().trim().toLowerCase();
		WordRelation[] possibleWords = wordNet.getPosibleWords(str);
		if(possibleWords==null||possibleWords.length==0){
			return null;
		}
		int startPosition = sample.firstElement().getStartPosition();
		int endPosition = sample.peek().getEndPosition();

		LinkedHashSet<IToken> result = new LinkedHashSet<IToken>();
		for(WordRelation wr : possibleWords){
			WordFormToken wordFormToken = new WordFormToken(wr, startPosition, endPosition);
			result.add(wordFormToken);
		}
		return result;
	}

	@Override
	protected int continuePush(Stack<IToken> sample) {
		
		if(sample.size()>2)
			return 0;

		IToken lastToken = sample.peek();
		return checkToken(lastToken);
	}

	private int checkToken(IToken unit) {
		if(unit.getType() != IToken.TOKEN_TYPE_LETTER){
			if(unit.getType() != IToken.TOKEN_TYPE_SYMBOL){
				if(unit.getType() != IToken.TOKEN_TYPE_NON_BREAKING_SPACE){
					return 1;
				}
			}
			String val = unit.getStringValue();
			if(!val.equals("-")){
				return 1;
			}
			return 1;
		}
		return CONTINUE_PUSH;
	}

	@Override
	protected boolean checkAndPrepare(IToken unit) {
		return checkToken(unit)<0;
	}

}
