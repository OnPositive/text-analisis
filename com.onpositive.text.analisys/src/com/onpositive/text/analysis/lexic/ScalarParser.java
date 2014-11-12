package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;

import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.utils.VulgarFraction;

public class ScalarParser extends AbstractParser {
	
	
	public ScalarParser() {

		delimeterPatterns = new DelimeterPattern[]{
				new DelimeterPattern(",".intern(), ".".intern(), " ".intern()),
				new DelimeterPattern(".".intern(), ",".intern(), " ".intern()),
				new DelimeterPattern(" ".intern(), ".".intern(), ",".intern()),
				new DelimeterPattern(" ".intern(), ",".intern(), ".".intern()),
				new DelimeterPattern(",".intern(), null        , ".".intern())};
	}	
	private ArrayList<String> cancelledDecimalDelimeters = new ArrayList<String>();
	private ArrayList<String> cancelledValueDelimeters = new ArrayList<String>();
	private ArrayList<String> cancelledFractionDelimeters = new ArrayList<String>();
	
	private ArrayList<String> occuredDecimalDelimeters = new ArrayList<String>();
	private ArrayList<String> occuredValueDelimeters = new ArrayList<String>();
	private ArrayList<String> occuredFractionDelimeters = new ArrayList<String>();
	
	private DelimeterPattern[] delimeterPatterns;
	
	private static final String[] acceptedSymbols = new String[]{",".intern(),".".intern(),";".intern()};
	
	
	@Override
	protected void combineUnits(Stack<IToken> sample, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens){
		
		resetDelimeterPatterns();
		
		ArrayList<Integer> valueBounds = detectInitialValueBounds(sample);
		detectDelimeters(sample, valueBounds);
		
		boolean gotPattern = false;
		for(DelimeterPattern pattern : delimeterPatterns){
			if(!pattern.isCanceled()){
				
				Collection<IToken> values = applyPattern(pattern, sample, valueBounds);
				if(values != null && !values.isEmpty()){
					gotPattern = true;
					reliableTokens.addAll(values);
				}
			}
		}
		if(!gotPattern){
			int size = sample.size();
			for(int i = 0 ; i < size ; i++){
				IToken token = sample.get(i);
				IToken scalar = null;
				int type = token.getType();
				if(type==IToken.TOKEN_TYPE_DIGIT){
					int startPosition = token.getStartPosition() ;
					int endPosition = token.getEndPosition() ;
					String val = token.getStringValue();
					
					String frac = null;
					if(i<size-1){
						IToken next = sample.get(i+1);
						if(next.getType()==IToken.TOKEN_TYPE_VULGAR_FRACTION){
							frac = next.getStringValue();
						}
						i++;
						endPosition = next.getEndPosition() ;
					}
					scalar = createScalar(val,null,frac,startPosition,endPosition);
				}
				else if(type == IToken.TOKEN_TYPE_VULGAR_FRACTION){
					String frac = token.getStringValue();
					scalar = createScalar(null,null,frac,i,i+1);
				}
				else{
					continue;
				}
				reliableTokens.add(scalar);
			}
		}
	}
	
	private IToken createScalar(
			String evenPart,
			String decimalFractionPart,
			String vulgarFractionPart,
			int startPosition,
			int endPosition)
	{
		
		if(evenPart==null||evenPart.isEmpty()){
			if(vulgarFractionPart!=null&&!vulgarFractionPart.isEmpty()){
				int[] iPair = VulgarFraction.getIntegerPair(vulgarFractionPart.charAt(0));
				return new ScalarToken(iPair[0], iPair[1], false, startPosition, endPosition);
			}
		}
		else{
			Integer iVal = Integer.parseInt(evenPart);
			if(decimalFractionPart==null||decimalFractionPart.isEmpty()){				
				return new ScalarToken(iVal, startPosition, endPosition);
			}
			else{
				Integer fVal = Integer.parseInt(decimalFractionPart);
				return new ScalarToken(iVal, fVal,true,startPosition, endPosition);
			}			
		}
		return null;
	}

	private Collection<IToken> applyPattern(DelimeterPattern pattern,	
			Stack<IToken> sample, ArrayList<Integer> valueBounds) {
		
		
		String decimalDelimeter = pattern.getDecimalDelimeter();
		String fractionDelimeter = pattern.getFractureDelimeter();
		String valueDelimeter = pattern.getValueDelimeter();
		
		ArrayList<IToken> result = new ArrayList<IToken>();
		for(int i = 0 ; i < valueBounds.size() ; i+=2){
			
			int startIndex = valueBounds.get(i);
			int endIndex = valueBounds.get(i+1);			
			
			StringBuilder bld = new StringBuilder();
			boolean isFraction = false;
			int startPosition=Integer.MAX_VALUE;
			for(int j = startIndex ; j < endIndex ; j++){
				
				IToken token = sample.get(j);
				String val = token.getStringValue();
				int type = token.getType();
				
				if(type == IToken.TOKEN_TYPE_DIGIT){
					if(bld.length()!=0){
						if(isFraction){
							IToken scalar = createScalar(bld.toString(), val, null,startPosition,token.getEndPosition());
							result.add(scalar);
							bld.delete(0, bld.length());
						}
						else{
							if(i>startIndex){
								IToken prev = sample.get(i-1);
								if(prev.getType()==IToken.TOKEN_TYPE_DIGIT){
									if(!" ".equals(valueDelimeter)){
										return null;
									}
								}
							}
							bld.append(val);
						}
					}
					else{
						startPosition = token.getStartPosition();
						bld.append(val);
					}
				}
				else if(type == IToken.TOKEN_TYPE_SYMBOL){
					val = val.intern();
					if(val == valueDelimeter){
						if(bld.length()!=0){
							String str = bld.toString();
							IToken scalar = createScalar(str, null, null,startPosition,sample.get(j-1).getEndPosition());
							result.add(scalar);
							bld.delete(0, bld.length());
						}
					}
					else if(val == decimalDelimeter){
						if(bld.length()==0){
							return null;
						}
					}
					else if(val == fractionDelimeter){
						if(bld.length()==0){
							return null;
						}
						isFraction = true;
					}
				}
				else if(type == IToken.TOKEN_TYPE_NON_BREAKING_SPACE){
					val = " ".intern();
					if(val == valueDelimeter){
						String str = bld.toString();
						IToken scalar = createScalar(str, null, null,startPosition,sample.get(j-1).getEndPosition());
						result.add(scalar);
						bld.delete(0, bld.length());
					}
					else if(val == decimalDelimeter){
						if(bld.length()==0){
							return null;
						}
					}
				}
				else if(type == IToken.TOKEN_TYPE_VULGAR_FRACTION){
					
					if(i<endIndex-1){
						IToken next = sample.get(i+1);
						if(next.getType()==IToken.TOKEN_TYPE_SYMBOL){
							if(!next.getStringValue().equals(valueDelimeter)){
								return null;
							}
						}
						else if(next.getType()==IToken.TOKEN_TYPE_DIGIT){
							if(!" ".equals(valueDelimeter)){
								return null;
							}
							if(next.getStartPosition() == token.getEndPosition()){
								return null;
							}
						}
					}
					String evenpart = null;
					if(bld.length()!=0){
						evenpart = bld.toString();
						bld.delete(0, bld.length());
					}
					else{
						startPosition = sample.get(j).getStartPosition();
					}
					IToken scalar = createScalar(evenpart, null, val,startPosition,sample.get(j).getEndPosition());
					result.add(scalar);
				}
			}
			if(bld.length()!=0){
				IToken lastToken = sample.get(endIndex-1);
				if(lastToken.getType()!= IToken.TOKEN_TYPE_DIGIT ){
					return null;
				}
				IToken scalar = createScalar(bld.toString(), null, null,startPosition,lastToken.getEndPosition());
				result.add(scalar);
			}
		}
		return result;
	}

	private void detectDelimeters(Stack<IToken> sample, ArrayList<Integer> valueBounds)
	{
		for(int i = 0 ; i < valueBounds.size() ; i+=2 ){
			
			int startIndex = valueBounds.get(i);
			int endIndex = valueBounds.get(i+1);
			for(int j = startIndex ; j < endIndex ; j++)
			{
				IToken token = sample.get(j);
				int type = token.getType();
				if(type==IToken.TOKEN_TYPE_DIGIT){
					
					String chl = null;
					String chr = null;
					
					if(i>startIndex){
						IToken prev = sample.get(i-1);
						if(prev.getType()==IToken.TOKEN_TYPE_SYMBOL){
							chl = prev.getStringValue();
						}
						else if( prev.getType() == IToken.TOKEN_TYPE_NON_BREAKING_SPACE
								||prev.getType() == IToken.TOKEN_TYPE_DIGIT ){
							chl = " ";
						}
					}					
					if(i<endIndex-1){
						IToken next = sample.get(i+1);
						if(next.getType()==IToken.TOKEN_TYPE_SYMBOL){
							chr = next.getStringValue();
						}						
						else if( next.getType() == IToken.TOKEN_TYPE_NON_BREAKING_SPACE
								||next.getType() == IToken.TOKEN_TYPE_DIGIT ){
							chr = " ";
						}
					}
					int l = token.getLength();
					if(l==3){
						if(chl!=null){
							pointDecimalDelimeter(chl);
						}
						if(chr!=null){
							pointDecimalDelimeter(chr);
//							if(chr.equals(chl)){
//								veryLikelyToBeDecDelimeters.add(chr);
//							}
						}						
					}
					else{
						if(chl!=null){
							cancelDecimalDelimeter(chl);
						}
						if(l>3){
							if(chr!=null){
								cancelDecimalDelimeter(chr);
							}
						}
					}
					if(chl!=null&&chr!=null&&chl.equals(chr)){
						cancelFractionDelimeter(chr);
					}
				}
				else if(type == IToken.TOKEN_TYPE_SYMBOL){
					String ch = token.getStringValue();
					boolean canBeFractionDelimeter = true;
					if(i>startIndex+1){
						if(sample.get(i-1).getType()==IToken.TOKEN_TYPE_DIGIT){
							IToken lt = sample.get(i-2);
							if(lt.getType()==IToken.TOKEN_TYPE_SYMBOL){
								canBeFractionDelimeter = !lt.getStringValue().equals(ch);
							}
						}
					}
					if(i<endIndex-1){
						IToken next = sample.get(i+1);
						int nextType = next.getType();
						if(nextType==IToken.TOKEN_TYPE_VULGAR_FRACTION){
							canBeFractionDelimeter = false;
							cancelDecimalDelimeter(ch);
						}
						else if( nextType == IToken.TOKEN_TYPE_DIGIT){
							if(i<endIndex-2){
								IToken rt = sample.get(i+2);
								if(rt.getType()==IToken.TOKEN_TYPE_SYMBOL){
									canBeFractionDelimeter = !rt.getStringValue().equals(ch);
								}
								else if(rt.getType() == IToken.TOKEN_TYPE_VULGAR_FRACTION){
									if(next.getEndPosition() == rt.getStartPosition()){
										canBeFractionDelimeter = false;
									}
								}
							}
						}
					}
					if(canBeFractionDelimeter){
						pointFractionDelimeter(ch);
					}
					else{
						cancelFractionDelimeter(ch);
					}
				}
			}
		}
	}

	private ArrayList<Integer> detectInitialValueBounds(Stack<IToken> sample) {
		ArrayList<Integer> valueBounds = new ArrayList<Integer>();		
		
		boolean isInsideValue = false;
		int size = sample.size();
		
		for(int i = 0 ; i < size; i++){
			
			IToken token = sample.get(i);
			int type = token.getType();
			IToken previousToken = null;
			IToken nextToken = null;
			int previousType = Integer.MIN_VALUE;
			if(i>0){
				previousToken = sample.get(i-1);
				previousType = previousToken.getType();
			}
			
			if(i<size-1){
				nextToken = sample.get(i+1);
			}
			
			if( type==IToken.TOKEN_TYPE_DIGIT || type == IToken.TOKEN_TYPE_VULGAR_FRACTION )
			{
				if(!isInsideValue){				
					valueBounds.add(i);
					isInsideValue = true;
				}
				else if(previousType==IToken.TOKEN_TYPE_VULGAR_FRACTION){
					valueBounds.add(i);
					valueBounds.add(i);
				}
				else if(previousType==IToken.TOKEN_TYPE_DIGIT){
					if(previousToken.getLength()>3||token.getLength()!=3){
						valueBounds.add(i);
						valueBounds.add(i);
					}
				}
			}
			else if( type == IToken.TOKEN_TYPE_VULGAR_FRACTION )
			{
				if(!isInsideValue){				
					valueBounds.add(i);
					isInsideValue = true;
				}
				else if(previousType==IToken.TOKEN_TYPE_VULGAR_FRACTION){
					valueBounds.add(i);
					valueBounds.add(i);
				}
			}
			else if( type == IToken.TOKEN_TYPE_SYMBOL || type == IToken.TOKEN_TYPE_NON_BREAKING_SPACE )
			{
				if(isInsideValue){
					if(previousType!=IToken.TOKEN_TYPE_DIGIT){
						valueBounds.add(i-1);
					}
					else{
						if( previousToken != null && previousToken.getEndPosition() != token.getStartPosition() ){
							valueBounds.add(i-1);
							isInsideValue = false;
						}
						if( nextToken != null && nextToken.getStartPosition() != token.getEndPosition() ){
							valueBounds.add(i-1);
							isInsideValue = false;
						}
					}
				}
			}
			else if(type == IToken.TOKEN_TYPE_LINEBREAK){
				if(isInsideValue){				
					if(previousType==IToken.TOKEN_TYPE_DIGIT||previousType==IToken.TOKEN_TYPE_VULGAR_FRACTION){
						valueBounds.add(i);						
					}
					else{
						valueBounds.add(i-1);
					}
					isInsideValue = false;
				}
			}
		}
		if(isInsideValue){
			valueBounds.add(size);
		}
		return valueBounds;
	}
	
	
	//accept digit, symbol, vulgar fraction and line break and non breaking whitespace.
	@Override
	protected int continuePush(Stack<IToken> sample) {
		
		int result = CONTINUE_PUSH;
		
		int size = sample.size();
		boolean isInsideValue = (size & 1) != 0;
		IToken token = sample.peek();
		int type = token.getType();
		
		int previousType = Integer.MIN_VALUE;
		if(size>1){
			IToken previous = sample.get(size-2);
			previousType = previous.getType();
		}
		
		if( type==IToken.TOKEN_TYPE_DIGIT || type == IToken.TOKEN_TYPE_VULGAR_FRACTION )
		{
			
//			if(!isInsideValue){				
//				valueBounds.add(size-1);
//			}
//			else{
//				if(previousType==IUnit.TOKEN_TYPE_VULGER_FRACTION){
//					valueBounds.add(size-1);
//					valueBounds.add(size-1);
//				}
//			}
		}
		else if( type == IToken.TOKEN_TYPE_SYMBOL ){
			String val = token.getStringValue().intern();
			boolean isAccepted = false;
			for(String s : acceptedSymbols){
				isAccepted |= (s == val);
			}
			if(!isAccepted){
				result = 1;
			}
		}
		else if ( type == IToken.TOKEN_TYPE_NON_BREAKING_SPACE )
		{
//			if(isInsideValue){
//				if(previousType!=IUnit.TOKEN_TYPE_DIGIT){
//					isInsideValue = false;
//					valueBounds.add(size-2);
//				}
//			}
		}
		else if(type == IToken.TOKEN_TYPE_LINEBREAK){
//			if(isInsideValue){				
//				if(previousType!=IUnit.TOKEN_TYPE_DIGIT){						
//					valueBounds.add(size-2);
//				}
//				else{
//					valueBounds.add(size-1);
//				}
//				isInsideValue = false;
//			}
		}
		else{
//			if(isInsideValue){
//				if(previousType==IUnit.TOKEN_TYPE_DIGIT||previousType==IUnit.TOKEN_TYPE_VULGAR_FRACTION){
//					valueBounds.add(size-1);
//				}
//				else{
//					valueBounds.add(size-2);
//				}
//			}
			result = 1;
		}
		return result;
	}

	@Override
	protected boolean checkAndPrepare(IToken unit) {
		if(unit.getType() != IToken.TOKEN_TYPE_DIGIT
				&& unit.getType() != IToken.TOKEN_TYPE_VULGAR_FRACTION){
			return false;
		}		
		return true;
	}
	
	private static class DelimeterPattern{
		
		public DelimeterPattern(String decimalDelimeter,
				String fractureDelimeter, String valueDelimeter) {
			super();
			this.decimalDelimeter = decimalDelimeter;
			this.fractureDelimeter = fractureDelimeter;
			this.valueDelimeter = valueDelimeter;
		}
		
		private boolean isCanceled;
		
		private boolean isOccured;
		
		private final String decimalDelimeter;
		
		private final String fractureDelimeter;
		
		private final String valueDelimeter;

		public boolean isCanceled() {
			return isCanceled;
		}

		public void setCanceled(boolean isCanceled) {
			this.isCanceled = isCanceled;
			if(isCanceled){
				this.isOccured = false;
			}
		}

		public String getDecimalDelimeter() {
			return decimalDelimeter;
		}

		public String getFractureDelimeter() {
			return fractureDelimeter;
		}

		public String getValueDelimeter() {
			return valueDelimeter;
		}

		public boolean isOccured() {
			return isOccured;
		}

		public void setOccured(boolean occured) {
			if(!this.isCanceled){
				this.isOccured = occured;
			}
		}		
	}
	
	private void registarPattern(DelimeterPattern p, String key,
			HashMap<String, ArrayList<DelimeterPattern>> map) {
		
		ArrayList<DelimeterPattern> arr = map.get(key);
		if(arr==null){
			arr = new ArrayList<ScalarParser.DelimeterPattern>();
			map.put(key, arr);
		}
		arr.add(p);
	}
	
	private void resetDelimeterPatterns(){
		for(DelimeterPattern p : delimeterPatterns){
			p.setCanceled(false);
			p.setOccured(false);
		}
		cancelledDecimalDelimeters.clear();
		cancelledFractionDelimeters.clear();
		cancelledValueDelimeters.clear();
		
		occuredDecimalDelimeters.clear();
		occuredFractionDelimeters.clear();
		occuredValueDelimeters.clear();
	}
	
	private void cancelDecimalDelimeter(String s){
		s = s.intern();
		if(cancelledDecimalDelimeters.contains(s)){
			return;
		}
		cancelledDecimalDelimeters.add(s);
		for( DelimeterPattern p : delimeterPatterns){
			if(p.getDecimalDelimeter()==s){
				p.setCanceled(true);
			}
		}
	}
	
	private void cancelValueDelimeter(String s){
		s = s.intern();
		if(cancelledValueDelimeters.contains(s)){
			return;
		}
		cancelledValueDelimeters.add(s);
		for( DelimeterPattern p : delimeterPatterns){
			if(p.getValueDelimeter()==s){
				p.setCanceled(true);
			}
		}
	}
	
	private void cancelFractionDelimeter(String s){
		s = s.intern();
		if(cancelledFractionDelimeters.contains(s)){
			return;
		}
		cancelledFractionDelimeters.add(s);
		for( DelimeterPattern p : delimeterPatterns){
			if(p.getFractureDelimeter()==s){
				p.setCanceled(true);
			}
		}
	}
	
	private void pointDecimalDelimeter(String s){
		s = s.intern();
		if(occuredDecimalDelimeters.contains(s)){
			return;
		}
		occuredDecimalDelimeters.add(s);
		for( DelimeterPattern p : delimeterPatterns){
			if(p.getDecimalDelimeter()==s){
				p.setOccured(true);
			}
		}
	}
	
	private void pointValueDelimeter(String s){
		s = s.intern();
		if(occuredValueDelimeters.contains(s)){
			return;
		}
		occuredValueDelimeters.add(s);
		for( DelimeterPattern p : delimeterPatterns){
			if(p.getValueDelimeter()==s){
				p.setOccured(true);
			}
		}
	}
	
	private void pointFractionDelimeter(String s){
		s = s.intern();
		if(occuredFractionDelimeters.contains(s)){
			return;
		}
		occuredFractionDelimeters.add(s);
		for( DelimeterPattern p : delimeterPatterns){
			if(p.getFractureDelimeter()==s){
				p.setOccured(true);
			}
		}
	}
}
