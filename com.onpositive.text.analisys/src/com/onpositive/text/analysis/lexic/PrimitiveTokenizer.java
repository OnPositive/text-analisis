package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onpositive.text.analysis.IUnit;
import com.onpositive.text.analysis.utils.Utils;

public class PrimitiveTokenizer {
	
	public PrimitiveTokenizer() {
		fillMap();
	}

	private static final HashMap<Character,Integer> map = new HashMap<Character, Integer>();
	
	private ArrayList<ITokenizerExtension> extensions = new ArrayList<ITokenizerExtension>();
	
	public List<IUnit> tokenize(String str){
		
		if( Utils.isEmptyString(str) ){
			return new ArrayList<IUnit>();
		}
		
		if(extensions.isEmpty()){
			return tokenizeSimply(str);
		}
		else{
			return tokenizeExtensively(str);
		}
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
				PrimitiveToken pt = new PrimitiveToken(segment, type, start, i);
				list.add(pt);
			}
			start = i;
			type = cType;
		}
		String segment = str.substring(start,l);
		PrimitiveToken pt = new PrimitiveToken(segment, type, start, l);
		list.add(pt);
		
		return list;
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
					PrimitiveToken pt = new PrimitiveToken(segment, type, start, i);
					list.add(pt);
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
					PrimitiveToken pt = new PrimitiveToken(segment, type, start, i);
					list.add(pt);
				}
				start = i;
				type = cType;
			}
		}
		if(start<l){
			String segment = str.substring(start,l);
			PrimitiveToken pt = new PrimitiveToken(segment, type, start, l);
			list.add(pt);
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
	
	private void fillMap() {
		
		for(char ch : new char[]{'\r', '\n'}){
			map.put(ch, IUnit.UNIT_TYPE_LINEBREAK);
		}
		
		for(char ch : new char[]{'\u00A0', '\u2007', '\u202F'}){
			map.put(ch, IUnit.UNIT_TYPE_NON_BREAKING_SPACE);
		}
		
		char[] symbols = new char[]{
				'`', '~', '!',  '@',  '#', '$', '%', '^', '&', '*', '(', ')',
				'-', '_', '=',  '+', '\\', '|', ',', '.', '<', '>', '/', '?',
				';', ':', '\'', '"', '[', ']', '{', '}'
			};
		for(char ch : symbols){
			map.put(ch, IUnit.UNIT_TYPE_SIGN);
		}
		
		char[] rusLetters = new char[] {
				'à', 'á', 'â', 'ã', 'ä',
				'å', '¸', 'æ', 'ý', 'è',
				'é', 'ê', 'ë', 'ì', 'í',
				'î', 'ï', 'ð', 'ñ', 'ò',
				'ó', 'ô', 'õ', 'ö', '÷',
				'ø', 'ù', 'ü', 'û', 'ú',
				'ý', 'þ', 'ÿ',
				'À', 'Á', 'Â', 'Ã', 'Ä',
				'Å', '¨', 'Æ', 'Ç', 'È',
				'É', 'Ê', 'Ë', 'Ì', 'Í',
				'Î', 'Ï', 'Ð', 'Ñ', 'Ò',
				'Ó', 'Ô', 'Õ', 'Ö', '×',
				'Ø', 'Ù', 'Ü', 'Û', 'Ú',
				'Ý', 'Þ', 'ß'
			};
		for(char ch : rusLetters){
			map.put(ch, IUnit.UNIT_TYPE_LETTER);
		}
	}
}
