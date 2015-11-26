package com.onpositive.text.analysis.filtering;

import java.util.HashMap;
import java.util.Map;

import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.WordFormToken;
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
		presets.put("после", PartOfSpeech.PREP);
		presets.put("посредством", PartOfSpeech.PREP);
		presets.put("путём", PartOfSpeech.PREP);
		
		presets.put("спасибо",PartOfSpeech.INTJ);
		presets.put("пожалуйста", PartOfSpeech.INTJ);
		
		presets.put("было", PartOfSpeech.VERB);
		
		presets.put("потом", PartOfSpeech.ADVB);
		//XXX тестовое
		presets.put("где - то", PartOfSpeech.ADVB);
		
		presets.put("всей", PartOfSpeech.ADJF);
		
		presets.put("типа", PartOfSpeech.NOUN);
		presets.put("части", PartOfSpeech.NOUN);
		presets.put("случай", PartOfSpeech.NOUN);
		presets.put("душа", PartOfSpeech.NOUN);
		
		//
		
		presets.put("это", PartOfSpeech.NPRO);
		presets.put("уже", PartOfSpeech.PRCL);
		presets.put("что", PartOfSpeech.CONJ);
		presets.put("всякий", PartOfSpeech.ADJF);
		presets.put("все", PartOfSpeech.ADJF);
		presets.put("всё", PartOfSpeech.ADJF);
		presets.put("чего", PartOfSpeech.NPRO);
		presets.put("тем", PartOfSpeech.CONJ);
		presets.put("так", PartOfSpeech.ADVB);
		presets.put("даже", PartOfSpeech.PRCL);
		presets.put("где", PartOfSpeech.CONJ);
		presets.put("кстати", PartOfSpeech.CONJ);
		presets.put("конечно", PartOfSpeech.CONJ);
		presets.put("же", PartOfSpeech.PRCL);
		presets.put("чуть", PartOfSpeech.ADVB);
		presets.put("всё - таки", PartOfSpeech.PRCL);
		presets.put("все - таки", PartOfSpeech.PRCL);
		presets.put("там", PartOfSpeech.ADVB);
		presets.put("ещё", PartOfSpeech.ADVB);
		presets.put("еще", PartOfSpeech.ADVB);
		presets.put("по - моему", PartOfSpeech.CONJ);
		presets.put("ли", PartOfSpeech.PRCL);
		presets.put("ль", PartOfSpeech.PRCL);
		presets.put("всего", PartOfSpeech.ADJF);
		presets.put("то", PartOfSpeech.ADJF);
//		presets.put("этом", PartOfSpeech.ADJF);
//		presets.put("этого", PartOfSpeech.ADJF);
//		presets.put("этим", PartOfSpeech.ADJF);
//		presets.put("этому", PartOfSpeech.ADJF);
		presets.put("его", PartOfSpeech.ADJF);
		presets.put("как", PartOfSpeech.CONJ);
		presets.put("были", PartOfSpeech.VERB);
		presets.put("их", PartOfSpeech.ADJF);
		presets.put("только", PartOfSpeech.PRCL);
		presets.put("тоже", PartOfSpeech.ADVB);
		presets.put("нужно", PartOfSpeech.PRED);
		presets.put("надо", PartOfSpeech.PRED);
		presets.put("либо", PartOfSpeech.CONJ);
		presets.put("когда", PartOfSpeech.CONJ);
		presets.put("ни", PartOfSpeech.PRCL);
		presets.put("тому", PartOfSpeech.ADJF);
		presets.put("сей", PartOfSpeech.ADJF);
		presets.put("лишь", PartOfSpeech.CONJ);
		presets.put("вообще", PartOfSpeech.CONJ);
		presets.put("как - то", PartOfSpeech.ADVB);
		presets.put("хоть", PartOfSpeech.PRCL);
		presets.put("ну", PartOfSpeech.PRCL);
		presets.put("вне", PartOfSpeech.PREP);
		presets.put("право", PartOfSpeech.NOUN);
		presets.put("короче", PartOfSpeech.COMP);
		presets.put("тут", PartOfSpeech.ADVB);
		presets.put("наряду", PartOfSpeech.ADVB);
		presets.put("нет", PartOfSpeech.PRED);
		presets.put("ничего", PartOfSpeech.NPRO);
		presets.put("почти", PartOfSpeech.ADVB);
		presets.put("ведь", PartOfSpeech.PRCL);
		presets.put("что - то", PartOfSpeech.NPRO);
		presets.put("времени", PartOfSpeech.NOUN);
		presets.put("пусть", PartOfSpeech.PRCL);
		presets.put("такая", PartOfSpeech.ADJF);
		
	}

	@Override
	public boolean shouldFilterOut(IToken token) {
		if (!(token instanceof SyntaxToken)) {
			return false;
		}
		String val = token.getShortStringValue().toLowerCase().trim();
		PartOfSpeech partOfSpeech = presets.get(val);
		if (partOfSpeech == null) {
			return false;
		}
		if (token instanceof WordFormToken && ((WordFormToken) token).getPartOfSpeech() != null) {
			return ((WordFormToken) token).getPartOfSpeech() != partOfSpeech;
		}
		if (partOfSpeech != null && !((SyntaxToken) token).hasGrammem(partOfSpeech)) {
			return true;
		}
		return false;
	}

}
