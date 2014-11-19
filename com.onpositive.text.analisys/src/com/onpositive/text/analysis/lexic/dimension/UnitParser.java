package com.onpositive.text.analysis.lexic.dimension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.AbstractParser;

public class UnitParser extends AbstractParser {
	
	public UnitParser(AbstractWordNet wordNet) {
		this.unitsProvider = new UnitsProvider(wordNet);
	}
	
	private UnitsProvider unitsProvider;

	@Override
	protected void combineTokens(Stack<IToken> sample, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens)
	{
		int start = sample.get(0).getStartPosition();

	}
	
	private HashSet<Integer> acceptedTypes = new HashSet<Integer>(Arrays.asList(
			IToken.TOKEN_TYPE_LETTER,
			IToken.TOKEN_TYPE_SYMBOL,
			IToken.TOKEN_TYPE_EXPONENT
		)); 
	
	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample, IToken newToken){
		
		int type = newToken.getType();
		if(acceptedTypes.contains(type)){
			int start = sample.get(0).getStartPosition();
			int end = sample.peek().getEndPosition();
			
			String value = getText().substring(start, end);
			if(unitsProvider.canBeUnitStart(value)){
				return CONTINUE_PUSH;
			}
		}		
		return DO_NOT_ACCEPT_AND_BREAK;
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		
		int type = newToken.getType();
		if(type==IToken.TOKEN_TYPE_LETTER){
			String value = newToken.getStringValue();
			if(unitsProvider.canBeUnitStart(value)){
				return CONTINUE_PUSH;
			}
		}		
		return DO_NOT_ACCEPT_AND_BREAK;
	}

}
