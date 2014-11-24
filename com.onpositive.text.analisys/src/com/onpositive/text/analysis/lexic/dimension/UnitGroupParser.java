package com.onpositive.text.analysis.lexic.dimension;

import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.AbstractParser;
import com.onpositive.text.analysis.lexic.ScalarToken;
import com.onpositive.text.analysis.lexic.StringToken;
import com.onpositive.text.analysis.lexic.UnitToken;
import com.onpositive.text.analysis.utils.Exponent;

public class UnitGroupParser extends AbstractParser{
	
	
	public UnitGroupParser(AbstractWordNet wordNet) {
		this.unitsProvider = new UnitsProvider(wordNet);
	}

	private UnitsProvider unitsProvider;
	
	@Override
	protected void combineTokens(Stack<IToken> sample,Set<IToken> reliableTokens, Set<IToken> doubtfulTokens)
	{
		List<UnitToken> newUnitTokens = null;
		int startPosition = sample.get(0).getStartPosition();
		int endPosition = sample.peek().getEndPosition();
		if(sample.size()==2){
			IToken token0 = sample.get(0);
			if(!(token0 instanceof UnitToken)){
				return;
			}
			UnitToken unitToken = (UnitToken) token0;			
			IToken token1 = sample.get(1);
			if(token1.getType() != IToken.TOKEN_TYPE_EXPONENT){
				return;
			}
			Integer exp = Exponent.getExponentValue(token1.getStringValue());
			if(exp==null){
				return;
			}
			newUnitTokens = groupUnit(unitToken,exp,startPosition,endPosition);			
		}
		else if(sample.size()==3){
			IToken token0 = sample.get(0);
			if(!(token0 instanceof UnitToken)){
				return;
			}
			UnitToken unitToken = (UnitToken) token0;			
			IToken token1 = sample.get(1);
			if(!token1.getStringValue().equals("^")){
				return;
			}
			IToken token2 = sample.get(2);
			if(!(token2 instanceof ScalarToken)){
				return;
			}
			ScalarToken scalarToken = (ScalarToken) token2;
			double exp = scalarToken.getValue1();
			newUnitTokens = groupUnit(unitToken,(int)exp,startPosition,endPosition);
		}
		if(newUnitTokens!=null){
			if(newUnitTokens.size()==1){
				reliableTokens.addAll(newUnitTokens);
			}
			else{
				doubtfulTokens.addAll(newUnitTokens);
			}
		}
	}

	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample, IToken newToken )
	{
//		if(sample.size()==1){
//			return CONTINUE_PUSH;
//		}
		
		int type = newToken.getType();		
		if(sample.size()==1){
			if(type==IToken.TOKEN_TYPE_EXPONENT){
				return ACCEPT_AND_BREAK;
			}
			else if(type==IToken.TOKEN_TYPE_SYMBOL){
				String value = newToken.getStringValue();
				if(value.equals("^")){
					return CONTINUE_PUSH;
				}
			}
		}
		else if(sample.size()==2){
			if(type==IToken.TOKEN_TYPE_SCALAR){
				return ACCEPT_AND_BREAK;
			}
		}
		return new ProcessingResult(sample.size());
	}

	@Override
	protected ProcessingResult checkPossibleStart(IToken token) {
		if(token.getType()==IToken.TOKEN_TYPE_UNIT){
			return CONTINUE_PUSH;
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}
	
	

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		throw new UnsupportedOperationException("Check Token not supported for Unit Group Parser"); 
	}
	
	
	private List<UnitToken> groupUnit(UnitToken unitToken, Integer exp,int startPosition,int endPosition) {
		
		String shortName = unitToken.getUnit().getShortName();
		shortName += "^" +exp;
		List<Unit> units = unitsProvider.getUnits(shortName);
		if(units!=null&&!units.isEmpty()){
			ArrayList<UnitToken> list = new ArrayList<UnitToken>();
			for(Unit unit : units){
				UnitToken token = new UnitToken(unit, startPosition, endPosition);
				list.add(token);				
			}
			return list;
		}
		return null;
	}

}
