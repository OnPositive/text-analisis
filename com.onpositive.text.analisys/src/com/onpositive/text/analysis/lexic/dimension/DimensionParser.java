package com.onpositive.text.analysis.lexic.dimension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.AbstractParser;
import com.onpositive.text.analysis.lexic.DimensionToken;
import com.onpositive.text.analysis.lexic.AbstractParser.ProcessingResult;

public class DimensionParser extends AbstractParser {
	
	private static final HashSet<String> acceptedSymbols
		= new HashSet<String>(Arrays.asList("/","^")); 
	
	public DimensionParser(AbstractWordNet wordNet) {
		this.wordNet = wordNet;
		this.unitsProvider = new UnitsProvider(wordNet);
	}
	
	private AbstractWordNet wordNet;
	
	private UnitsProvider unitsProvider;

	@Override
	protected void combineTokens(Stack<IToken> sample,Set<IToken> reliableTokens, Set<IToken> doubtfulTokens)
	{
		ArrayList<Integer> offsets = new ArrayList<Integer>();
		ArrayList<DimensionToken> list = new ArrayList<DimensionToken>();
		
		detectSimpleDimensions(sample, offsets, list);
		
		groupDimensions(list,offsets,reliableTokens,doubtfulTokens);		
	}

	private void groupDimensions(
			ArrayList<DimensionToken> list,
			ArrayList<Integer> offsets,
			Set<IToken> reliableTokens,
			Set<IToken> doubtfulTokens)
	{
		ArrayList<DimensionToken> group = new ArrayList<DimensionToken>();
		int size = offsets.size();
		for(int i = 0 ; i < size ; i += 2){
			
			group.clear();
			for(int ind = offsets.get(i) ; ind < offsets.get(i+1) ; ind++){
				group.add(list.get(ind));
l0:				for(int j = i+2 ; j < size ; j+= 2){
					boolean gotSimilar = false;
					for(int ind2 = offsets.get(j) ; ind2 < offsets.get(j+1) ; ind2++){
						//if(Units.areCongruent(unit0, unit1))
					}
				}
			}
			
		}		
	}

	private void detectSimpleDimensions(Stack<IToken> sample,
			ArrayList<Integer> offsets, ArrayList<DimensionToken> list) {
		int bound = sample.size()-1;
		for(int i = 0 ; i < bound ; i++){
			
			IToken token = sample.get(i);
			int type = token.getType();
			
			if(type==IToken.TOKEN_TYPE_SCALAR){
				IToken nextToken = sample.get(i+1);
				int nextType = nextToken.getType();
				if(nextType==IToken.TOKEN_TYPE_LETTER){
					
					Unit[] units = Units.getUnits(nextToken.getStringValue());					
					if(units.length!=0){
						offsets.add(list.size());
						offsets.add(units.length);
						for(Unit u : units){
							DimensionToken dt = new DimensionToken(token, u, token.getStartPosition(), nextToken.getEndPosition());
							list.add(dt);
						}
					}
					i++;
				}
			}
			else if( type == IToken.TOKEN_TYPE_LETTER){
				
			}
		}
	}

	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample,IToken nextToken) {
		
		IToken token = sample.peek();
		int type = token.getType();
		String value = token.getStringValue();
		if(type == IToken.TOKEN_TYPE_SCALAR){
			return AbstractParser.CONTINUE_PUSH;
		}
		else if(type == IToken.TOKEN_TYPE_LETTER||type == IToken.TOKEN_TYPE_LETTER){
			
			GrammarRelation[] possibleGrammarForms = wordNet.getPossibleGrammarForms(value);
			
		}
		else if(type == IToken.TOKEN_TYPE_SYMBOL){
			if(acceptedSymbols.contains(value)){
				return CONTINUE_PUSH;
			}
			else{
				return DO_NOT_ACCEPT_AND_BREAK;
			}
		}
		else if(type == IToken.TOKEN_TYPE_EXPONENT){
			return CONTINUE_PUSH;
		}
		return DO_NOT_ACCEPT_AND_BREAK;
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
