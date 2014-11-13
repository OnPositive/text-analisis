//package com.onpositive.text.analysis.lexic;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.Stack;
//
//import com.onpositive.text.analysis.IToken;
//
//public class DimensionParser extends AbstractParser {
//
//	@Override
//	protected void combineTokens(Stack<IToken> sample,Set<IToken> reliableTokens, Set<IToken> doubtfulTokens)
//	{
//		ArrayList<Integer> offsets = new ArrayList<Integer>();
//		ArrayList<DimensionToken> list = new ArrayList<DimensionToken>();
//		
//		detectSimpleDimensions(sample, offsets, list);
//		
//		groupDimensions(list,offsets,reliableTokens,doubtfulTokens);		
//	}
//
//	private void groupDimensions(
//			ArrayList<DimensionToken> list,
//			ArrayList<Integer> offsets,
//			Set<IToken> reliableTokens,
//			Set<IToken> doubtfulTokens)
//	{
//		ArrayList<DimensionToken> group = new ArrayList<DimensionToken>();
//		int size = offsets.size();
//		for(int i = 0 ; i < size ; i += 2){
//			
//			group.clear();
//			for(int ind = offsets.get(i) ; ind < offsets.get(i+1) ; ind++){
//				group.add(list.get(ind));
//l0:				for(int j = i+2 ; j < size ; j+= 2){
//					boolean gotSimilar = false;
//					for(int ind2 = offsets.get(j) ; ind2 < offsets.get(j+1) ; ind2++){
//						//if(Units.areCongruent(unit0, unit1))
//					}
//				}
//			}
//			
//		}		
//	}
//
//	private void detectSimpleDimensions(Stack<IToken> sample,
//			ArrayList<Integer> offsets, ArrayList<DimensionToken> list) {
//		int bound = sample.size()-1;
//		for(int i = 0 ; i < bound ; i++){
//			
//			IToken token = sample.get(i);
//			int type = token.getType();
//			
//			if(type==IToken.TOKEN_TYPE_SCALAR){
//				IToken nextToken = sample.get(i+1);
//				int nextType = nextToken.getType();
//				if(nextType==IToken.TOKEN_TYPE_LETTER){
//					
//					Unit[] units = Units.getUnits(nextToken.getStringValue());					
//					if(units.length!=0){
//						offsets.add(list.size());
//						offsets.add(units.length);
//						for(Unit u : units){
//							DimensionToken dt = new DimensionToken(token, u, token.getStartPosition(), nextToken.getEndPosition());
//							list.add(dt);
//						}
//					}
//					i++;
//				}
//			}
//			else if( type == IToken.TOKEN_TYPE_LETTER){
//				
//			}
//		}
//	}
//
//	@Override
//	protected int continuePush(Stack<IToken> sample) {
//		
//		IToken token = sample.peek();
//		int type = token.getType();
//		if(type == IToken.TOKEN_TYPE_SCALAR){
//			return AbstractParser.CONTINUE_PUSH;
//		}
//		else if(type == IToken.TOKEN_TYPE_LETTER){
//			if(Units.isUnit(token.getStringValue())){
//				return AbstractParser.CONTINUE_PUSH;
//			}
//			else{
//				return 1;
//			}
//		}		
//		return 1;
//	}
//
//	@Override
//	protected boolean checkPossibleStart(IToken unit) {
//
//		boolean result = false;
//		int type = unit.getType();
//		if(type==IToken.TOKEN_TYPE_LETTER){
//			result = Units.isUnit(unit.getStringValue());
//		}
//		else if(type==IToken.TOKEN_TYPE_SCALAR){
//			
//			IToken next = unit.getNext();			
//			if(next!=null){
//				int nextType = next.getType();
//				if(nextType==IToken.TOKEN_TYPE_LETTER){
//					result = Units.isUnit(next.getStringValue());
//				}
//			}
//			else{
//				List<IToken> nextUnits = unit.getNextTokens();
//				if(nextUnits!=null){
//					for(IToken nextToken : nextUnits){
//						int nextType = nextToken.getType();
//						if(nextType==IToken.TOKEN_TYPE_LETTER){
//							result = Units.isUnit(nextToken.getStringValue());
//							if(result){
//								break;
//							}
//						}						
//					}
//				}
//			}
//		}
//		return result;
//	}
//}
