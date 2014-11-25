package com.onpositive.text.analysis.lexic.dimension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.AbstractParser;
import com.onpositive.text.analysis.lexic.UnitToken;
import com.onpositive.text.analysis.lexic.WordFormToken;

public class UnitParser extends AbstractParser {
	
	private static HashMap<String,Integer> scalePrefixMap = new HashMap<String, Integer>();
	
	private static int[] scalePrefixLengths;
	
	static{
		scalePrefixMap.put("дека", 1);
		scalePrefixMap.put("гекто", 2);
		scalePrefixMap.put("кило", 3);
		scalePrefixMap.put("мега", 6);
		scalePrefixMap.put("гига", 9);
		scalePrefixMap.put("тера", 12);
		scalePrefixMap.put("пета", 15);
		scalePrefixMap.put("экса", 18);
		scalePrefixMap.put("зетта", 21);
		scalePrefixMap.put("иотта", 24);

		scalePrefixMap.put("деци", -1);
		scalePrefixMap.put("санти", -2);
		scalePrefixMap.put("милли", -3);
		scalePrefixMap.put("микро", -6);
		scalePrefixMap.put("нано", -9);
		scalePrefixMap.put("пико", -12);
		scalePrefixMap.put("фемто", -15);
		scalePrefixMap.put("атто", -18);
		scalePrefixMap.put("зепто", -21);
		scalePrefixMap.put("иокто", -24);
		
		IntOpenHashSet list = new IntOpenHashSet(); 
		for(String str : scalePrefixMap.keySet()){
			list.add(str.length());
		}
		scalePrefixLengths = list.toArray();
		Arrays.sort(scalePrefixLengths);
	}
	
	public static String detectScalePrefix(String value) {
		
		for(int prefLength : scalePrefixLengths){
			if(value.length()<=prefLength){
				break;
			}
			String pref = value.substring(0,prefLength);
			if(scalePrefixMap.containsKey(pref)){
				return pref;
			}
		}
		return null;
	}
	
	public static Integer getScale(String scalePrefix) {
		
		return scalePrefixMap.get(scalePrefix);
	}
	
	public UnitParser(AbstractWordNet wordNet) {
		this.unitsProvider = new UnitsProvider(wordNet);
	}	  
	
	private UnitsProvider unitsProvider;

	@Override
	protected void combineTokens(Stack<IToken> sample, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens)
	{
		IToken token = sample.peek();
		if(!(token instanceof WordFormToken)){
			return;
		}
		
		WordFormToken wordFormToken = (WordFormToken) token;
		TextElement te = wordFormToken.getTextElement();
		
		LinkedHashSet<Unit> units = new LinkedHashSet<Unit>(); 
		MeaningElement[] concepts = te.getConcepts();
		for(MeaningElement me : concepts){
			List<Unit> list = unitsProvider.getUnits(me);
			if(list!=null){
				units.addAll(list);
			}
		}
		if(units.isEmpty()&&!te.isMultiWord()){
			String value = te.getBasicForm();
			String pref = detectScalePrefix(value);
			if(pref!=null){
				int exp = getScale(pref);
				String baseValue = value.substring(pref.length());
				List<Unit> baseUnits = unitsProvider.getUnits(baseValue);
				if(baseUnits!=null){
					double rel = Math.pow(10, exp);
					for(Unit baseUnit : baseUnits){
						Unit unit = new Unit(value, baseUnit.getKind(), rel);
						units.add(unit);
					}
				}
			}
		}
		int startPosition = token.getStartPosition();
		int endPosition = token.getEndPosition();
		if(units.size()==1){
			Unit unit = units.iterator().next();
			UnitToken unitToken = new UnitToken(unit, startPosition, endPosition);
			reliableTokens.add(unitToken);
		}
		else{
			for(Unit unit:units){
				UnitToken unitToken = new UnitToken(unit, startPosition, endPosition);
				doubtfulTokens.add(unitToken);
			}
		}
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		
		int type = newToken.getType();
		if(type==IToken.TOKEN_TYPE_WORD_FORM){
			return ACCEPT_AND_BREAK;
		}		
		return DO_NOT_ACCEPT_AND_BREAK;
	}

}
