package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.onpositive.semantic.words2.WordNet;
import com.onpositive.semantic.words2.WordRelation;
import com.onpositive.text.analysis.IUnit;

public class WordFormParser extends AbstractParser {

	public WordFormParser(WordNet wordNet) {
		super();
		this.wordNet = wordNet;
	}
	
	WordNet wordNet;

	@Override
	protected List<IUnit> combineUnits(Stack<IUnit> sample) {
		
		StringBuilder bld = new StringBuilder();
		for(IUnit unit : sample){
			if(unit.getType()==IUnit.UNIT_TYPE_LETTER){
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

		ArrayList<IUnit> result = new ArrayList<IUnit>();
		for(WordRelation wr : possibleWords){
			WordFormToken wordFormToken = new WordFormToken(wr, startPosition, endPosition);
			result.add(wordFormToken);
		}
		return result;
	}

	@Override
	protected boolean continueParsing(Stack<IUnit> sample) {
		
		if(sample.size()>3)
			return false;

		IUnit lastToken = sample.peek();
		if(lastToken.getType() != IUnit.UNIT_TYPE_LETTER){
			if(lastToken.getType() != IUnit.UNIT_TYPE_SYMBOL){
				if(lastToken.getType() != IUnit.UNIT_TYPE_NON_BREAKING_SPACE){
					return false;
				}
			}
			String val = lastToken.getStringValue();
			if(!val.equals("-")){
				return false;
			}
			return true;
		}
		return true;
	}

}
