package com.onpositive.text.analysis.lexic.dimension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.AbstractParser;
import com.onpositive.text.analysis.lexic.DimensionToken;
import com.onpositive.text.analysis.lexic.ScalarToken;
import com.onpositive.text.analysis.lexic.UnitToken;

public class DimensionParser extends AbstractParser {
	
	private static final HashSet<String> acceptedSymbols
		= new HashSet<String>(Arrays.asList("/","^")); 
	

	@Override
	protected void combineTokens(Stack<IToken> sample,Set<IToken> reliableTokens, Set<IToken> doubtfulTokens)
	{
		if(sample.size()!=2){
			return;
		}
		IToken token0 = sample.get(0);
		if(!(token0 instanceof ScalarToken)){
			return;
		}
		ScalarToken scalarToken = (ScalarToken) token0;
		
		IToken token1 = sample.get(1);
		if(!(token1 instanceof UnitToken)){
			return;
		}
		UnitToken unitToken = (UnitToken) token1;
		
		int startPosition = token0.getStartPosition();
		int endPosition = token1.getEndPosition();		
		DimensionToken dimensionToken = new DimensionToken(scalarToken, unitToken.getUnit(), startPosition, endPosition);
		reliableTokens.add(dimensionToken);
	}

	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample,IToken nextToken) {
		
		if(sample.size()==1){
			return CONTINUE_PUSH;
		}
		IToken token = sample.peek();
		int type = token.getType();
		if(type == IToken.TOKEN_TYPE_UNIT){
			return ACCEPT_AND_BREAK;
		}
		else{
			return new ProcessingResult(sample.size());		
		}
	}

	@Override
	protected ProcessingResult checkPossibleStart(IToken newToken) {

		int type = newToken.getType();
		if(type==IToken.TOKEN_TYPE_SCALAR){
			return CONTINUE_PUSH;
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		throw new UnsupportedOperationException("Check Token not supported for Dimension Parser"); 
	}
}
