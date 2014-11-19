package com.onpositive.text.analysis.lexic.dimension;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.AbstractParser;
import com.onpositive.text.analysis.lexic.WordFormToken;

public class UnitParser extends AbstractParser {
	
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
			 units.addAll(unitsProvider.getUnits(me));
		}

	}
	
	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample, IToken newToken){		
		return ACCEPT_AND_BREAK;
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		
		int type = newToken.getType();
		if(type==IToken.TOKEN_TYPE_WORD_FORM){
			return CONTINUE_PUSH;
		}		
		return DO_NOT_ACCEPT_AND_BREAK;
	}

}
