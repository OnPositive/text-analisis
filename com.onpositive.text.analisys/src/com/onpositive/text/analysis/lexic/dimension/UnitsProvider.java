package com.onpositive.text.analysis.lexic.dimension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.semantic.wordnet.SemanticRelation;
import com.onpositive.semantic.wordnet.TextElement;

public class UnitsProvider {
	
	public UnitsProvider(AbstractWordNet wordNet) {
		this.wordNet = wordNet;		
	}
	
	private final HashMap<Integer,UnitKind> unitKindMap = new HashMap<Integer,UnitKind>(); 

	private AbstractWordNet wordNet;
	
	private HashMap<String,Unit> map = new HashMap<String, Unit>();
	
	private MeaningElement ultimateUnit;
	
	public List<Unit> getUnits(String str){
		
		init();
		
		LinkedHashSet<Unit> set=null;
		
		GrammarRelation[] possibleGrammarForms = wordNet.getPossibleGrammarForms(str);
		for(GrammarRelation gr : possibleGrammarForms){
			MeaningElement[] concepts = gr.getWord().getConcepts();
			for(MeaningElement me : concepts){
				List<Unit> units = getUnits(me);
				if(units.isEmpty()){
					continue;
				}
				
				if(set==null){
					set = new LinkedHashSet<Unit>();
				}				
				set.addAll(units);
			}
		}
		
		return new ArrayList<Unit>(set);
	}
	
	public boolean canBeUnitStart(String str){
		
		init();

		GrammarRelation[] possibleGrammarForms = wordNet.getPossibleGrammarForms(str);
		for(GrammarRelation gr : possibleGrammarForms){

			TextElement word = gr.getWord();
			TextElement[] possibleContinuations = wordNet.getPossibleContinuations(word);
			for(TextElement te : possibleContinuations){
				MeaningElement[] concepts = te.getConcepts();
				for(MeaningElement me : concepts){
					List<Integer> unitTypes = me.detectGeneralizations(unitKindMap.keySet());
					if(!unitTypes.isEmpty()){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public List<Unit> getUnits(MeaningElement me){
		
		LinkedHashSet<Unit> set=null;
		
		List<Integer> unitTypes = me.detectGeneralizations(unitKindMap.keySet());
		for(int ut : unitTypes){
			Unit unit = new Unit(me.getParentTextElement().getBasicForm(), unitKindMap.get(ut), 1);
			if(set==null){
				set = new LinkedHashSet<Unit>();
			}
			set.add(unit);
		}
		return new ArrayList<Unit>(set);
	}

	private void init() {
		if(ultimateUnit!=null)
			return;
		
		ultimateUnit = getMeaning("_ALL_DIMENSION_UNITS".toLowerCase());
		
		for( UnitKind uk : UnitKind.values()){
			String name = uk.name();
			MeaningElement meaning = getMeaning(("_UNITS_"+name).toLowerCase());
			if(meaning!=null){
				unitKindMap.put(meaning.id(), uk);
			}
		}		
		
		if(ultimateUnit==null){
			throw new RuntimeException("Wordnet does not support measure units.");
		}
	}

	private MeaningElement getMeaning(String name) {
		GrammarRelation[] pgf = wordNet.getPossibleGrammarForms(name);
		if(pgf!=null&&pgf.length!=0){
			MeaningElement[] concepts = pgf[0].getWord().getConcepts();
			if(concepts!=null&&concepts.length!=0){
				return concepts[0];
			}
		}
		return null;
	}

}
