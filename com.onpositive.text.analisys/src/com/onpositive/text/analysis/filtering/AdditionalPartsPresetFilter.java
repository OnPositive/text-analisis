package com.onpositive.text.analysis.filtering;

import java.util.HashMap;
import java.util.Map;

import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.syntax.SyntaxToken;

public class AdditionalPartsPresetFilter implements ITokenFilter {
	
	private static Map<String, PartOfSpeech> presets = new HashMap<String, PartOfSpeech>();
	
	static {
		presets.put("и",PartOfSpeech.CONJ);
		presets.put("или",PartOfSpeech.CONJ);
		presets.put("а",PartOfSpeech.CONJ);
		presets.put("но",PartOfSpeech.CONJ);
		presets.put("однако",PartOfSpeech.CONJ);
		presets.put("чтоб",PartOfSpeech.CONJ);
		presets.put("чтобы",PartOfSpeech.CONJ);
		presets.put("хотя", PartOfSpeech.CONJ);
		
		presets.put("на",PartOfSpeech.PREP);
		presets.put("для",PartOfSpeech.PREP);
		presets.put("к",PartOfSpeech.PREP);
		presets.put("из",PartOfSpeech.PREP);
		presets.put("у",PartOfSpeech.PREP);
		presets.put("о", PartOfSpeech.PREP);
		presets.put("при", PartOfSpeech.PREP);
		
		presets.put("спасибо",PartOfSpeech.INTJ);
		presets.put("пожалуйста", PartOfSpeech.INTJ);
		
		presets.put("было", PartOfSpeech.VERB);
		
		presets.put("потом", PartOfSpeech.ADVB);
		
	}

	@Override
	public boolean shouldFilterOut(IToken token) {
		if (!(token instanceof SyntaxToken)) {
			return false;
		}
		String val = token.getShortStringValue().toLowerCase().trim();
		PartOfSpeech partOfSpeech = presets.get(val);
		if (partOfSpeech != null && !((SyntaxToken) token).hasGrammem(partOfSpeech)) {
			return true;
		}
		return false;
	}

}
