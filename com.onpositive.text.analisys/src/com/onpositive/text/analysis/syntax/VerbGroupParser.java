package com.onpositive.text.analysis.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.text.analysis.IToken;

public abstract class VerbGroupParser extends AbstractSyntaxParser {

	public VerbGroupParser(AbstractWordNet wordNet) {
		super(wordNet);
	}

	protected abstract int getType(SyntaxToken token);

	protected abstract boolean checkAdditionalToken(IToken token);

	protected abstract boolean checkVerb(IToken token);

	@Override
	protected void combineTokens(Stack<IToken> sample, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens) {
		if (sample.size() < 2) {
			return;
		}
	
		SyntaxToken token0 = (SyntaxToken) sample.get(0);
		SyntaxToken token1 = (SyntaxToken) sample.peek();
		
		boolean isContinuous = sample.size()==2;
	
		if (checkIfAlreadyProcessed(token0, token1)) {
			return;
		}
	
		SyntaxToken predToken = null;
		SyntaxToken objToken = null;
		ClauseToken clauseToken = null;
		if (checkVerb(token0) && checkAdditionalToken(token1)) {
			predToken = token0;
			objToken = token1;
		} else if (checkVerb(token1) && checkAdditionalToken(token0)) {
			predToken = token1;
			objToken = token0;
		}
		else if(token0.getType()==IToken.TOKEN_TYPE_CLAUSE){
			clauseToken = (ClauseToken) token0;
			predToken = clauseToken.getPredicate();
			token0=predToken;
			objToken = token1;
		}
		else if(token1.getType()==IToken.TOKEN_TYPE_CLAUSE){
			clauseToken = (ClauseToken) token1;
			predToken = clauseToken.getPredicate();
			token1=predToken;
			objToken = token0;
		}
		else{
			return;
		}
	
		int subjType = getType(objToken);
		
		int startPosition = token0.getStartPosition();
		int endPosition = token1.getEndPosition();		
		SyntaxToken newToken = new SyntaxToken(subjType, predToken, null, startPosition, endPosition, isContinuous);
	
		if(clauseToken!=null){
			boolean doSet = false;
			if(!isContinuous){
				ArrayList<IToken> children = new ArrayList<IToken>(Arrays.asList(token0,token1));
				if (checkParents(newToken, children)) {
					newToken.addChildren(children);
					for(IToken ch: children){
						ch.addParent(newToken);
					}
					doSet = true;
				}
			}
			else{
				doSet=true;
			}
			if(doSet){
				newToken.addChild(token0);
				newToken.addChild(token1);
				clauseToken.setPredicate(newToken);
			}
		}
		else if (checkParents(newToken, sample)) {
			reliableTokens.add(newToken);
		}
	}

	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample, IToken newToken) {
		IToken token0 = sample.get(0);
		IToken token1 = newToken;
		if (checkVerb(token0)||token0.getType()==IToken.TOKEN_TYPE_CLAUSE){
			if(checkAdditionalToken(token1)) {
				return ACCEPT_AND_BREAK;
			}
		} else if (checkVerb(token1)||token1.getType()==IToken.TOKEN_TYPE_CLAUSE) {
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
		return DO_NOT_ACCEPT_AND_BREAK;
	}

}