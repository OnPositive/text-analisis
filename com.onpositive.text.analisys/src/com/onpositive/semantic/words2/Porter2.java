package com.onpositive.semantic.words2;

import com.onpositive.semantic.words3.model.RelationTarget;
import com.onpositive.semantic.words3.model.WordRelation;
import com.onpositive.wikipedia.dumps.builder.Porter;

public class Porter2 {

	public static String stem(String source){
		WordRelation[] posibleWords = WordNetProvider.getInstance().getPosibleWords(source);
		for (WordRelation r:posibleWords){
			RelationTarget word = r.getWord();
			if (word instanceof SimpleWord){
				SimpleWord wr=(SimpleWord) word;
				if (wr.foundation!=null){
					return wr.foundation.replace(""+WordFormTemplate.lc[1],"");
				}
			}
		}
		if (posibleWords.length>0){
			RelationTarget word = posibleWords[0].getWord();
			if (word instanceof SimpleWord){
				SimpleWord w=(SimpleWord) word;
				return w.getBasicForm();
			}
		}
		return Porter.stem(source);		
	}
		
}
