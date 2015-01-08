package com.onpositive.text.analysis.lexic.dimension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.text.analysis.AbstractParser;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.StringToken;
import com.onpositive.text.analysis.lexic.UnitToken;
import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.syntax.SyntaxToken;

public class UnitParser extends AbstractParser {
	
	public UnitParser(AbstractWordNet wordNet) {
		this.unitsProvider = new UnitsProvider(wordNet);
	}	  
	
	private UnitsProvider unitsProvider;

	@Override
	protected void combineTokens(Stack<IToken> sample, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens)
	{
		IToken token = sample.peek();
		if(token instanceof WordFormToken){
			
			List<IToken> tokens = processWordForm((WordFormToken) token);
			if(tokens!=null&&!tokens.isEmpty()){
				appendTokens(tokens,reliableTokens,doubtfulTokens);
				return;
			}			
		}		
		String unitName = getUnitName(token);
		if(unitName==null){
			return;
		}
		List<Unit> constructed = unitsProvider.constructUnits(unitName);
		if(constructed==null||constructed.isEmpty()){
			return;
		}
		
		int startPosition = token.getStartPosition();
		int endPosition = token.getEndPosition();
		SyntaxToken mainGroup = (token instanceof SyntaxToken) ? (SyntaxToken)token : null;
		ArrayList<IToken> tokens = createUnitTokens(constructed, mainGroup, startPosition, endPosition);
		appendTokens(tokens,reliableTokens,doubtfulTokens);
	}

	private String getUnitName(IToken token) {
		
		if(token instanceof WordFormToken){
			TextElement te = ((WordFormToken)token).getMeaningElement().getParentTextElement();			
			return te.getBasicForm();
		}
		else if( token instanceof StringToken){
			return token.getStringValue();
		}
		return null;
	}

	private void appendTokens(List<IToken> tokens, Set<IToken> reliableTokens,Set<IToken> doubtfulTokens) {
		if(tokens==null||tokens.isEmpty()){
			return;
		}
		else if(tokens.size()==1){
			reliableTokens.add(tokens.get(0));
		}
		else{
			doubtfulTokens.addAll(tokens);
		}
	}

	private List<IToken> processWordForm(WordFormToken token) {

		int startPosition = token.getStartPosition();
		int endPosition = token.getEndPosition();
		
		List<Unit> units = unitsProvider.getUnits(token.getMeaningElement());
		if(units==null){
			return null;
		}
		ArrayList<IToken> tokens = createUnitTokens(units, token, startPosition, endPosition);
		return tokens;
	}

	private ArrayList<IToken> createUnitTokens(
			Collection<Unit> units,
			SyntaxToken mainGroup,
			int startPosition,
			int endPosition)
	{
		ArrayList<IToken> tokens = new ArrayList<IToken>();
		for(Unit unit : units){
			UnitToken unitToken = new UnitToken(unit, mainGroup, null, startPosition, endPosition);
			tokens.add(unitToken);
		}
		return tokens;
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		
		int type = newToken.getType();
		if(type==IToken.TOKEN_TYPE_WORD_FORM||type==IToken.TOKEN_TYPE_LETTER){
			return ACCEPT_AND_BREAK;
		}		
		return DO_NOT_ACCEPT_AND_BREAK;
	}

}
