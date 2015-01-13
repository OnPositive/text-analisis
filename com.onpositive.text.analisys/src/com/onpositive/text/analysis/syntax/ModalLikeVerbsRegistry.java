package com.onpositive.text.analysis.syntax;

import java.util.HashSet;
import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.words3.MetaLayer;

public class ModalLikeVerbsRegistry {
	
	public ModalLikeVerbsRegistry(AbstractWordNet wordNet) {
		this.wordNet = wordNet;
	}
	
	private AbstractWordNet wordNet;
	
	private HashSet<String> set = null;
	
	public boolean isModalLike(String str){
		
		if(set==null){
			init();
		}
		return set.contains(str);		
	}

	private void init() {
		set = new HashSet<String>();
		
		MetaLayer<Object> layer = wordNet.getMetaLayers().getLayer("modalLikeVerbs");
		if(layer==null){
			return;
		}
		int[] allIds = layer.getAllIds();
		for(int id : allIds){
			MeaningElement me = wordNet.getConceptInfo(id);
			TextElement te = me.getParentTextElement();
			String basicForm = te.getBasicForm();
			set.add(basicForm);
		}		
	}

}
