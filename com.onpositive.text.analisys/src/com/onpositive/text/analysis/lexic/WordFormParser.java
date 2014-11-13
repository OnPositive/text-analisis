package com.onpositive.text.analysis.lexic;

import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.text.analysis.IToken;

public class WordFormParser extends AbstractParser {

	public WordFormParser(AbstractWordNet wordNet) {
		super();
		this.wordNet = wordNet;
	}
	
	AbstractWordNet wordNet;

	@Override
	protected ProcessingResult combineUnits(Stack<IToken> sample, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens){
		
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
		GrammarRelation[] possibleWords = wordNet.getPossibleGrammarForms(str);
		if(possibleWords==null||possibleWords.length==0){
			return;
		}
		int startPosition = sample.firstElement().getStartPosition();
		int endPosition = sample.peek().getEndPosition();

		for(GrammarRelation wr : possibleWords){
			WordFormToken wordFormToken = new WordFormToken(wr, startPosition, endPosition);
			reliableTokens.add(wordFormToken);
		}
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
			return CONTINUE_PUSH;
		}
		return CONTINUE_PUSH;
	}

	@Override
	protected boolean checkAndPrepare(IToken unit) {
		return checkToken(unit)<0;
	}

}
