package com.onpositive.text.analisys.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.scalar.ScalarParser;

public class ScalarTokenTest {

	private static final String UNIT_TYPE_NAME_PREFIX = "UNIT_TYPE_";
	
	public static void main(String[] args) {
		
		HashMap<Integer,String> map = new HashMap<Integer, String>();
		
		Field[] fields = IToken.class.getFields();
		for(Field f : fields){
			int mdf = f.getModifiers();
			if(!Modifier.isStatic(mdf)){
				continue;
			}
			
			if( f.getType() != int.class ){
				continue;
			}
			
			String fName = f.getName();
			if(!fName.startsWith(UNIT_TYPE_NAME_PREFIX)){
				continue;
			}
			
			f.setAccessible(true);
			
			try {
				int code = (Integer) f.get(null);
				map.put(code, fName.substring(UNIT_TYPE_NAME_PREFIX.length()));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
		
		String[] sArr = new String[]{
				"У меня есть 25 яблок",
				"Прилетело 1 000 000 попугаев",
				"Прилетело попугаев 1,000,000.2,000,000 -- это прилетело ворон.",
				"У меня есть " + '\u2159' + " яблока"
		};
		
		PrimitiveTokenizer pt = new PrimitiveTokenizer();
		ScalarParser sp = new ScalarParser();
		
		for(String s : sArr){
			
			List<IToken> tokens = pt.tokenize(s);
			sp.setText(s);
			ArrayList<IToken> tokens2 = sp.process(tokens);
			for(IToken t : tokens2){
				System.out.println(t.getStartPosition() + "-" + t.getEndPosition() + " " + map.get(t.getType()) + " " + t.getStringValue());
			}
			System.out.println();
		}
		
	}

}
