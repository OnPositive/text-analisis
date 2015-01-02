package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.words3.MetaLayer;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;
import com.onpositive.text.analysis.syntax.AbstractSyntaxParser;
import com.onpositive.text.analysis.syntax.SyntaxToken;

public class PrepConjRegistry {
	
	public PrepConjRegistry(AbstractWordNet wordNet) {
		super();
		this.wordNet = wordNet;
	}
	
	private AbstractWordNet wordNet;
	
	private HashMap<String,MeaningElement> prepMap;
	
	private HashMap<String,MeaningElement> conjMap;
	
	private HashMap<String,List<Case>> prepCaseMap = new HashMap<String, List<Case>>();
	
	private HashMap<String,UnaryMatcher<SyntaxToken>> prepCaseMatchersMap = new HashMap<String, UnaryMatcher<SyntaxToken>>(); 

	public MeaningElement getPreposition(String str){
		initMaps();
		return prepMap.get(str.trim().toLowerCase());
	}
	
	public MeaningElement getConjunction(String str){
		initMaps();
		return conjMap.get(str.trim().toLowerCase());
	}

	private void initMaps() {
		
		if(prepMap==null){
			prepMap = new HashMap<String, MeaningElement>();
			initMap(prepMap, "prepositions", PartOfSpeech.PREP, true);
		}
		if(conjMap==null){
			conjMap = new HashMap<String, MeaningElement>();
			initMap(conjMap, "conjunctions", PartOfSpeech.CONJ, false);
		}
		
	}

	private void initMap( HashMap<String, MeaningElement> map, String layerId, PartOfSpeech partOfSpeech, boolean readValue) {
		
		MetaLayer<Object> layer = wordNet.getMetaLayers().getLayer(layerId);
		if(layer==null){
			return;
		}
		int[] allIds = layer.getAllIds();
		HashMap<String,List<Case>> caseListCache = new HashMap<String, List<Case>>();
		HashMap<String,UnaryMatcher<SyntaxToken>> caseMatchersCache = new HashMap<String, UnaryMatcher<SyntaxToken>>();
		for(int id : allIds){
			MeaningElement me = wordNet.getConceptInfo(id);
			TextElement te = me.getParentTextElement();
			String basicForm = te.getBasicForm();
			if(te.isMultiWord()){
				StringBuilder bld = new StringBuilder();
				bld.append(basicForm);
				TextElement[] parts = te.getParts();
				for(int i = 1 ; i < parts.length ; i++){
					bld.append(" ").append(parts[i].getBasicForm());
				}
				basicForm = bld.toString();
			}
			basicForm = basicForm.trim().toLowerCase();
			MeaningElement[] concepts = te.getConcepts();
			boolean gotMeaning = false;
			for(MeaningElement me1 : concepts){
				Set<Grammem> grammems = me1.getGrammems();
				if(grammems==null){
					continue;
				}
				if(grammems.contains(partOfSpeech)){
					map.put(basicForm, me1);
					gotMeaning = true;
					break;
				}
			}
			
			if(gotMeaning && readValue){
				String valueString = layer.getValue(id).toString();
				ArrayList<String> orderedCases = new ArrayList<String>(Arrays.asList(valueString.split(" ")));
				StringBuilder bld = new StringBuilder();
				bld.append(orderedCases.get(0));
				for(int i = 1 ; i < orderedCases.size(); i++){
					bld.append(" ").append(orderedCases.get(i));
				}
				String canonicString = bld.toString();
				List<Case> list = caseListCache.get(canonicString);
				UnaryMatcher<SyntaxToken> matcher = caseMatchersCache.get(basicForm);
				if(list==null||matcher==null){
					list = new ArrayList<Grammem.Case>();
					caseListCache.put(canonicString, list);
					for(String caseName : orderedCases){
						Grammem grammem = Grammem.get(caseName.toLowerCase());
						list.add((Case) grammem);
					}
					matcher = AbstractSyntaxParser.createCaseMatcher(list);
					caseMatchersCache.put(canonicString, matcher);
				}
				prepCaseMap.put(basicForm, list);
				prepCaseMatchersMap.put(basicForm, matcher);
			}
		}
	}


	public MeaningElement lookupConjunctionOrPreposition(String str, PartOfSpeech pos){ 

		MeaningElement me = null;
		if(pos==PartOfSpeech.PREP){
			me = getPreposition(str);
		}
		else if(pos==PartOfSpeech.CONJ){
			me = getConjunction(str);
		}
		else{
			return null;
		}
		return me;
	}
	
	public List<Case> getPrepCases(String basicForm){
		return prepCaseMap.get(basicForm);
	}
	
	public UnaryMatcher<SyntaxToken> getPrepCaseMatcher(String basicForm){
		return prepCaseMatchersMap.get(basicForm);
	}
}
