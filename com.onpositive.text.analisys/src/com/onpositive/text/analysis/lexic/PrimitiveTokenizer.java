package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onpositive.text.analysis.IUnit;
import com.onpositive.text.analysis.utils.Utils;

public class PrimitiveTokenizer {

	private static final HashMap<Character,Integer> map = new HashMap<Character, Integer>();
	
	static{
		fillMap();
	}
	
	private ArrayList<ITokenizerExtension> extensions = new ArrayList<ITokenizerExtension>();
	
	public List<IUnit> tokenize(String str){
		
		if( Utils.isEmptyString(str) ){
			return new ArrayList<IUnit>();
		}
		
		List<IUnit> tokens;
		if(extensions.isEmpty()){
			tokens = tokenizeSimply(str);			
		}
		else{
			tokens = tokenizeExtensively(str);
		}
		
		for(int i = 1 ; i < tokens.size() ; i++){
			IUnit prev = tokens.get(i-1);
			IUnit curr = tokens.get(i);
			
			prev.addNextUnit(curr);
			curr.addPreviousUnit(prev);
		}
		
		return tokens;
	}

	private List<IUnit> tokenizeSimply(String str) {
		
		ArrayList<IUnit> list = new ArrayList<IUnit>();		
		
		int start = 0 ;
		int type = detectType(str.charAt(start));		
		int l = str.length();
		for(int i = 1 ; i < l; i++){			
			
			char ch = str.charAt(i);
			int cType = detectType(ch);
			if(cType==type){
				continue;
			}
			
			if(type != IUnit.UNIT_TYPE_OTHER_WHITESPACE){
				String segment = str.substring(start,i);
				tokenizeSegment(list, start, type, segment);
			}
			start = i;
			type = cType;
		}
		String segment = str.substring(start,l);
		tokenizeSegment(list, start, type, segment);
		
		return list;
	}

	private final void tokenizeSegment(ArrayList<IUnit> list, int start, int type, String segment)
	{
		int sl = segment.length();
		if(type == IUnit.UNIT_TYPE_SYMBOL){
			for(int i = 0 ; i < sl ; i++){
				char ch = segment.charAt(i); 
				SymbolToken pt = new SymbolToken( ch, type, start+i, start+i+1 );
				list.add(pt);
			}
		}
		else{
			StringToken pt = new StringToken(segment, type, start, start + sl );
			list.add(pt);
		}		
	}


	private List<IUnit> tokenizeExtensively(String str) {
		
		ArrayList<IUnit> list = new ArrayList<IUnit>();		
		
		int type = -1;
		int start = 0 ;
		int l = str.length();
		for(int i = 0 ; i < l; i++){			
			
			IUnit extendedUnit = null;			
			for(ITokenizerExtension te : extensions){
				extendedUnit = te.readUnit(str, i);
				if(extendedUnit!=null){
					break;
				}
			}
			
			if(extendedUnit!=null){				
				if( type >=0 && type != IUnit.UNIT_TYPE_OTHER_WHITESPACE ){
					String segment = str.substring(start,i);
					tokenizeSegment(list, start, type, segment);
					type = -1;
				}				
				list.add(extendedUnit);
				
				start = extendedUnit.getEndPosition();
				i = extendedUnit.getEndPosition()-1;
			}
			else{
				char ch = str.charAt(i);
				int cType = detectType(ch);
				if(type < 0){
					type = cType;
					continue;
				}
				if(cType==type){
					continue;
				}				
				if(type != IUnit.UNIT_TYPE_OTHER_WHITESPACE){
					String segment = str.substring(start,i);
					tokenizeSegment(list, start, type, segment);
				}
				start = i;
				type = cType;
			}
		}
		if(start<l){
			String segment = str.substring(start,l);
			tokenizeSegment(list, start, type, segment);
		}		
		return list;
	}
	
	private int detectType(char ch) {
		
		Integer type = map.get(ch);
		if(type !=null)
			return type;
		
		if(Character.isWhitespace(ch)){
			return IUnit.UNIT_TYPE_OTHER_WHITESPACE;
		}
		
		if(Character.isDigit(ch)){
			return IUnit.UNIT_TYPE_DIGIT;
		}
		
		if(Character.isLetter(ch)){
			return IUnit.UNIT_TYPE_LETTER;
		}
		
		return IUnit.UNIT_TYPE_UNDEFINED;
	}
	
	private static void fillMap() {
		
		for(char ch : new char[]{'\r', '\n'}){
			map.put(ch, IUnit.UNIT_TYPE_LINEBREAK);
		}
		
		for(char ch : new char[]{'\u00A0', '\u2007', '\u202F'}){
			map.put(ch, IUnit.UNIT_TYPE_NON_BREAKING_SPACE);
		}
		
		char[] symbols = new char[]{
				'`', '~', '!',  '@',  '#', '$', '%', '^', '&', '*', '(', ')',
				'-', '_', '=',  '+', '\\', '|', ',', '.', '<', '>', '/', '?',
				';', ':', '\'', '"', '[', ']', '{', '}' , '—', '«', '»'
			};
		for(char ch : symbols){
			map.put(ch, IUnit.UNIT_TYPE_SYMBOL);
		}
	}
}
