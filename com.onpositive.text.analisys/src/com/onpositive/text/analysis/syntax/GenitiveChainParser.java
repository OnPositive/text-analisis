package com.onpositive.text.analysis.syntax;

import java.util.ArrayList;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;

public class GenitiveChainParser extends AbstractSyntaxParser {

	public GenitiveChainParser(AbstractWordNet wordNet) {
		super(wordNet);
	}
	
	@SuppressWarnings("unchecked")
	private static final UnaryMatcher<SyntaxToken> nounGenMatcher
			= and(hasAll(PartOfSpeech.NOUN),hasAny(Case.GENT, Case.GEN1, Case.GEN2));
	
	private static final UnaryMatcher<SyntaxToken> nounMatcher	= hasAll(PartOfSpeech.NOUN);

	@Override
	protected void combineTokens(Stack<IToken> sample, ProcessingData processingData)
	{
		if(sample.size()<2){
			return;
		}
		if(!checkParents(null, sample)){
			return;
		}
		
		IToken token0 = sample.get(0);
		IToken token1 = sample.peek();
		
		boolean isMember0 = nounGenMatcher.match(token0);
		boolean isMember1 = nounGenMatcher.match(token1);
		
		int type = 0;		
		ClauseToken clauseToken = null;
		SyntaxToken mainGroup = null;
		if(isMember1&&!isMember0){
			if(token0 instanceof ClauseToken){
				clauseToken = (ClauseToken) token0;
				mainGroup = clauseToken.getSubject();
			}
			else{
				mainGroup = (SyntaxToken) token0;
			}
			type = IToken.TOKEN_TYPE_GENITIVE_CHAIN;
		}
		else if(isMember0&&!isMember1){
			if(token1 instanceof ClauseToken){
				clauseToken = (ClauseToken) token1;
				mainGroup = clauseToken.getSubject();
			}
			else{
				mainGroup = (SyntaxToken) token1;
			}
			type = IToken.TOKEN_TYPE_GENITIVE_CHAIN;
		}
		else{
			if(token0 instanceof ClauseToken){
				clauseToken = (ClauseToken) token0;
				mainGroup = clauseToken.getSubject();
			}
			else if(token1 instanceof ClauseToken){
				clauseToken = (ClauseToken) token1;
				mainGroup = clauseToken.getSubject();
			}
			else{
				mainGroup = (SyntaxToken) token0;
			}
			type = IToken.TOKEN_TYPE_UNIFORM_NOUN;
		}
		
		if(mainGroup instanceof ClauseToken){
			clauseToken = (ClauseToken) mainGroup;
			mainGroup = clauseToken.getSubject();
		}
		
		if(clauseToken==null){
			int startPosition = token0.getStartPosition();
			int endPosition = token1.getEndPosition();
			SyntaxToken newToken = new SyntaxToken(type, mainGroup, null, startPosition, endPosition);
			if(!checkParents(newToken, sample)){
				return;
			}
			processingData.addDoubtfulToken(newToken);
		}
		else{
			int startPosition = Integer.MAX_VALUE;
			int endPosition = clauseToken.getStartPosition();
			int count = 0;
			for(IToken t : sample){
				
				if(t==clauseToken){
					continue;
				}				
				if(isContained((SyntaxToken) t, clauseToken)){
					continue;
				}
				count++;
				startPosition = Math.min(startPosition, t.getStartPosition());
				endPosition = Math.max(endPosition, t.getEndPosition());
			}
			if(count<1){
				return;
			}
			SyntaxToken newToken = new SyntaxToken(type, mainGroup, null, startPosition, endPosition);
			ArrayList<IToken> children = new ArrayList<IToken>();
			for(IToken t : sample){
				children.add(t==clauseToken?mainGroup:t);
			}
			newToken.setChildren(children);
			clauseToken.setSubject(newToken);
		}
	}
	
	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample, IToken newToken) {
		
		if(isPrepOrConj(newToken)){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		
		if(nounGenMatcher.match(newToken)){
			return CONTINUE_PUSH;
		}
		
		if(!(nounMatcher.match(newToken)||newToken.getType()==IToken.TOKEN_TYPE_CLAUSE)){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		
		IToken token0 = sample.get(0);
		if(nounGenMatcher.match(token0)){
			return ACCEPT_AND_BREAK;
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		
		if(isPrepOrConj(newToken)){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		
		return nounMatcher.match(newToken)||newToken.getType()==IToken.TOKEN_TYPE_CLAUSE ?
				CONTINUE_PUSH : DO_NOT_ACCEPT_AND_BREAK;
	}
	
	@Override
	public boolean isRecursive() {
		return false;
	}

}
