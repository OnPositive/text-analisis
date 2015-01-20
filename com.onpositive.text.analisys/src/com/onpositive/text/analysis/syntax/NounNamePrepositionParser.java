package com.onpositive.text.analysis.syntax;

import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.DimensionToken;
import com.onpositive.text.analysis.lexic.ScalarToken;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;

public class NounNamePrepositionParser extends AbstractSyntaxParser {

	public NounNamePrepositionParser(AbstractWordNet wordNet) {
		super(wordNet);
	}

	protected boolean checkName(IToken token){
		if(token instanceof DimensionToken){
			return true;
		}
		if(token instanceof ScalarToken){
			return true;
		}
		return nameMatch.match(token);
	}

	protected boolean checkNoun(IToken token) {
		return nounMatch.match(token);
	}
	
	protected boolean acceptsPreposition(){
		return false;
	};
	
	protected boolean requiresPreposition(){
		return false;
	};
	
	protected static final UnaryMatcher<SyntaxToken> nameMatch = hasAny( PartOfSpeech.NOUN, PartOfSpeech.ADJF );
	
	@Override
	public boolean isRecursive() {
		return true;
	}

	@Override
	protected void combineTokens(Stack<IToken> sample, ProcessingData processingData) {

		if (sample.size() < 3) {
			return;
		}
		
		if(checkParents(null, sample)){
			return;
		}
		
		IToken token0 = sample.get(0);
		SyntaxToken mainToken = (SyntaxToken) (prepMatch.match(token0) ? sample.get(2) : token0);
		ClauseToken clauseToken = null;
		if(mainToken instanceof ClauseToken){
			clauseToken = (ClauseToken) mainToken;
			mainToken = clauseToken.getSubject();
		}
		
		int startPosition = token0.getStartPosition();
		int endPosition = sample.peek().getEndPosition();
			
		SyntaxToken newToken = new SyntaxToken(IToken.TOKEN_TYPE_NOUN_NAME_PREP, mainToken, null, startPosition, endPosition, true);
	
		if(clauseToken!=null){
			newToken.addChildren(sample);
			clauseToken.setSubject(newToken);
		}
		else if (checkParents(newToken, sample)) {
			processingData.addReliableToken(newToken);
		}
	}

	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample, IToken newToken) {
		
		IToken last = sample.peek();
		if(prepMatch.match(newToken)){
			if(sample.size()>1){
				return DO_NOT_ACCEPT_AND_BREAK;
			}
			if(prepMatch.match(last)){
				return DO_NOT_ACCEPT_AND_BREAK;
			}
			if(!nounMatch.match(last)){
				return DO_NOT_ACCEPT_AND_BREAK;
			}
			return CONTINUE_PUSH;
		}
		if(checkName(newToken)){
			if(sample.size()<2){
				return CONTINUE_PUSH;
			}
			boolean gotNoun = nounMatch.match(newToken);
			boolean gotPrep = false;
			int prepInd = 0 ;
			for(int i = 0 ; i < sample.size() ; i++){
				IToken t = sample.get(i);
				gotNoun|=(nounMatch.match(t)||t instanceof ClauseToken);
				if(prepMatch.match(t)){
					gotPrep = true;
					prepInd = i;
				}
			}
			if(!(gotPrep&&gotNoun)){
				return DO_NOT_ACCEPT_AND_BREAK;				
			}
			SyntaxToken prepToken = (SyntaxToken)sample.get(prepInd);
			SyntaxToken nameToken = (SyntaxToken) (prepInd == 0 ? sample.get(1) : newToken);
			UnaryMatcher<SyntaxToken> prepCaseMatcher
				= getPrepConjRegistry().getPrepCaseMatcher(prepToken.getBasicForm());
			
			if(!prepCaseMatcher.match(nameToken)){
				return DO_NOT_ACCEPT_AND_BREAK;
			}
			return ACCEPT_AND_BREAK;			
		}
		if(newToken instanceof ClauseToken){
			
			if(sample.size()<2){
				return DO_NOT_ACCEPT_AND_BREAK;
			}

			SyntaxToken prepToken = (SyntaxToken)sample.get(0);
			SyntaxToken nameToken = (SyntaxToken)sample.get(1);
			if(!prepMatch.match(prepToken)){
				return DO_NOT_ACCEPT_AND_BREAK;
			}
			if(!checkName(nameToken)){
				return DO_NOT_ACCEPT_AND_BREAK;
			}
			UnaryMatcher<SyntaxToken> prepCaseMatcher
				= getPrepConjRegistry().getPrepCaseMatcher(prepToken.getBasicForm());
			
			if(!prepCaseMatcher.match(nameToken)){
				return DO_NOT_ACCEPT_AND_BREAK;
			}
			return ACCEPT_AND_BREAK;			
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}

	protected boolean matchTokensCouple(Stack<IToken> sample) {
		return true;
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		if (checkName(newToken)) {
			return CONTINUE_PUSH;
		}
		if(newToken.getType()==IToken.TOKEN_TYPE_CLAUSE){
			return CONTINUE_PUSH;
		}
		if(prepMatch.match(newToken)){
			return DO_NOT_ACCEPT_AND_BREAK;//CONTINUE_PUSH;
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}

}