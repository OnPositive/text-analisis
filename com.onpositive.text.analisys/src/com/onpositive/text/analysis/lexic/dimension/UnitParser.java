package com.onpositive.text.analysis.lexic.dimension;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.AbstractParser;
import com.onpositive.text.analysis.lexic.UnitToken;
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
			List<Unit> list = unitsProvider.getUnits(me);
			if(list!=null){
				units.addAll(list);
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
