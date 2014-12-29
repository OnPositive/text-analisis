package com.onpositive.text.analysis.lexic;

import java.util.HashMap;
import java.util.Set;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.words3.MetaLayer;

public class PrepConjRegistry {
	
	public PrepConjRegistry(AbstractWordNet wordNet) {
		super();
		this.wordNet = wordNet;
	}
	
	private AbstractWordNet wordNet;
	
	private HashMap<String,MeaningElement> prepMap;
	
	private HashMap<String,MeaningElement> conjMap;

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
			initMap(prepMap, "prepositions", PartOfSpeech.PREP);
		}
		if(conjMap==null){
			conjMap = new HashMap<String, MeaningElement>();
			initMap(conjMap, "conjunctions", PartOfSpeech.CONJ);
		}
		
	}

	private void initMap( HashMap<String, MeaningElement> map, String layerId, PartOfSpeech partOfSpeech) {
		
		MetaLayer<Object> layer = wordNet.getMetaLayers().getLayer(layerId);
		if(layer==null){
			return;
		}
		int[] allIds = layer.getAllIds();
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
			for(MeaningElement me1 : concepts){
				Set<Grammem> grammems = me1.getGrammems();
				if(grammems==null){
					continue;
				}
				if(grammems.contains(partOfSpeech)){
					map.put(basicForm, me1);
					break;
				}
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
}
