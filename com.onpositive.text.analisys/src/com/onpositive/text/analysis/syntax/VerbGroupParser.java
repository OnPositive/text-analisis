package com.onpositive.text.analysis.syntax;

import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;

public abstract class VerbGroupParser extends AbstractSyntaxParser {

	public VerbGroupParser(AbstractWordNet wordNet) {
		super(wordNet);
	}

	protected abstract int getType(SyntaxToken token);

	protected abstract boolean checkAdditionalToken(IToken token);

	protected abstract boolean checkVerb(IToken token);
	
	protected abstract boolean acceptsPreposition();
	
	private UnaryMatcher<SyntaxToken> prepMatch = hasAny(PartOfSpeech.PREP);

	@Override
	protected void combineTokens(Stack<IToken> sample, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens) {
		if (sample.size() < 2) {
			return;
		}			
	
		SyntaxToken[] orderedTokens = extractMainTokens(sample);
		if(orderedTokens==null){
			return;
		}
		
		if (checkIfAlreadyProcessed(orderedTokens[0], orderedTokens[1])) {
			return;
		}
		
		int startPosition = sample.get(0).getStartPosition();
		int endPosition = sample.peek().getEndPosition();
		
		ClauseToken clauseToken = null;
		SyntaxToken predToken = null;
		if(orderedTokens[0].getType()==IToken.TOKEN_TYPE_CLAUSE){
			clauseToken = (ClauseToken) orderedTokens[0];
			predToken = clauseToken.getPredicate();
		}
		else{
			predToken = orderedTokens[0];
		}	
		int subjType = getType(orderedTokens[1]);
			
		SyntaxToken newToken = new SyntaxToken(subjType, predToken, null, startPosition, endPosition, true);
	
		if(clauseToken!=null){
//			boolean doSet = false;
			ArrayList<IToken> children = new ArrayList<IToken>();
			for(IToken t : sample){
				children.add(t==clauseToken?predToken:t);
			}
//			if(!isContinuous){				
//				if (checkParents(newToken, children)) {
//					newToken.addChildren(children);
//					for(IToken ch: children){
//						ch.addParent(newToken);
//					}
//					doSet = true;
//				}
//			}
//			else{
//				doSet=true;
//			}
//			if(doSet){
				newToken.addChildren(children);
				clauseToken.setPredicate(newToken);
//			}
		}
		else if (checkParents(newToken, sample)) {
			reliableTokens.add(newToken);
		}
	}

	private SyntaxToken[] extractMainTokens(Stack<IToken> sample) {
		if(sample.size()==2){
			return fillMainTokenArray(sample.get(0),sample.get(1),new SyntaxToken[2]); 
		}
		else if(sample.size()==3){
			
			if(prepMatch.match(sample.get(0))){
				SyntaxToken[] result = fillMainTokenArray(sample.get(1),sample.get(2),new SyntaxToken[3]);
				result[2]=(SyntaxToken) sample.get(0);
				return result;
			}
			else if(checkPreposition(sample.get(1))){
				SyntaxToken[] result = fillMainTokenArray(sample.get(0),sample.get(2),new SyntaxToken[3]);
				result[2]=(SyntaxToken) sample.get(1);
				return result;
			}
		}
		return null;
	}

	protected boolean checkPreposition(IToken token) {
		return prepMatch.match(token);
	}

	private SyntaxToken[] fillMainTokenArray(IToken token0, IToken token1,	SyntaxToken[] arr) {
		
		if (checkVerb(token0) && checkAdditionalToken(token1)) {
			arr[0] = (SyntaxToken) token0;
			arr[1] = (SyntaxToken) token1;
		} else if (checkVerb(token1) && checkAdditionalToken(token0)) {
			arr[0] = (SyntaxToken) token1;
			arr[1] = (SyntaxToken) token0;
		}
		else if(token0.getType()==IToken.TOKEN_TYPE_CLAUSE && checkAdditionalToken(token1)){
			arr[0] = (SyntaxToken) token0;
			arr[1] = (SyntaxToken) token1;
		}
		else if(token1.getType()==IToken.TOKEN_TYPE_CLAUSE && checkAdditionalToken(token0)){
			arr[0] = (SyntaxToken) token1;
			arr[1] = (SyntaxToken) token0;
		}
		return arr;
	}

	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample, IToken newToken) {
		
		IToken last = sample.peek();
		if(checkPreposition(newToken)){
			if(!acceptsPreposition()){
				return DO_NOT_ACCEPT_AND_BREAK;
			}
			if(sample.size()>1){
				return DO_NOT_ACCEPT_AND_BREAK;
			}
			if(checkAdditionalToken(last)){
				return CONTINUE_PUSH;
			}
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		if((checkVerb(newToken)||(newToken.getType()==IToken.TOKEN_TYPE_CLAUSE))&&checkAdditionalToken(last)){
			return ACCEPT_AND_BREAK;
		}
		if(checkAdditionalToken(newToken)&&(checkVerb(last)
				||last.getType()==IToken.TOKEN_TYPE_CLAUSE
				||(acceptsPreposition()&&checkPreposition(last)))){		
			return ACCEPT_AND_BREAK;
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		if (checkVerb(newToken)||checkAdditionalToken(newToken)) {
			return CONTINUE_PUSH;
		}
		if(newToken.getType()==IToken.TOKEN_TYPE_CLAUSE){
			return CONTINUE_PUSH;
		}
		if(acceptsPreposition()&&checkPreposition(newToken)){
			return CONTINUE_PUSH;
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}

}