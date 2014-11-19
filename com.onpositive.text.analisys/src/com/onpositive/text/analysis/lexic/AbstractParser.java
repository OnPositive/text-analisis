package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.onpositive.text.analysis.IToken;

public abstract class AbstractParser {
	

	protected static boolean isOneOf(IToken token, String[] acceptedSymbols)
	{
		String val = token.getStringValue().intern();
		boolean isAccepted = false;				
		for(String s : acceptedSymbols){
			isAccepted |= (s == val);
		}
		return isAccepted;
	}
	
	public static class ProcessingResult{
		
		protected int stepBack;

		public ProcessingResult(int stepBack) {
			super();
			this.stepBack = stepBack;
		}
		
	}
	
	protected String text;
	
	protected static final ProcessingResult CONTINUE_PUSH = new ProcessingResult(-1);
	protected static final ProcessingResult ACCEPT_AND_BREAK = new ProcessingResult(0);
	protected static final ProcessingResult DO_NOT_ACCEPT_AND_BREAK = new ProcessingResult(1);
	
	protected static ProcessingResult stepBack(int count){
		return new ProcessingResult(count);
	}
	
	abstract protected void combineTokens(Stack<IToken> sample, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens);
	
	protected ProcessingResult continuePush(Stack<IToken> sample,IToken newToken){
		return checkToken(newToken);
	}
	
	abstract protected ProcessingResult checkToken(IToken newToken);
	
	protected ProcessingResult checkPossibleStart(IToken token){
		return checkToken(token);
	};
	
	protected void beforeProcess(List<IToken> tokens){};
	
	
	protected void prepare(){}
	
	
	protected void cleanUp(){}
	
	public ArrayList<IToken> process(List<IToken> tokens){
		
		beforeProcess(tokens);
		
		LinkedHashSet<IToken> reliableTokens = new LinkedHashSet<IToken>();
		LinkedHashSet<IToken> doubtfulTokens = new LinkedHashSet<IToken>(); 
		
		ArrayList<IToken> result = new ArrayList<IToken>();
		for( int i = 0 ; i < tokens.size() ; i++ ){
			IToken unit = tokens.get(i);			
			List<IToken> parents = unit.getParents();
			if(parents!=null&&!parents.isEmpty()){
				continue;
			}
			reliableTokens.clear();
			doubtfulTokens.clear();
			parseStartingTokens(unit,reliableTokens,doubtfulTokens);
			if(reliableTokens.isEmpty()){
				result.add(unit);
			}
			else{
				result.addAll(reliableTokens);
			}
			result.addAll(doubtfulTokens);
		}
		return result;
	}	

	private void parseStartingTokens(IToken unit, LinkedHashSet<IToken> reliableTokens, LinkedHashSet<IToken> doubtfulTokens) {
		
		if(checkPossibleStart(unit).stepBack>=0){
			return;
		}		
		prepare();		
		
		Stack<IToken> sample = new Stack<IToken>();
		sample.add(unit);		
		boolean gotRecursion = parseRecursively(sample,reliableTokens,doubtfulTokens);
		
		if(reliableTokens.isEmpty()&&doubtfulTokens.isEmpty()){
			return;
		}
		
		for(IToken newUnit : reliableTokens){
			handleBounds(gotRecursion, newUnit, true);
		}
		for(IToken newUnit : doubtfulTokens){
			handleBounds(gotRecursion, newUnit, false);
		}		
		cleanUp();
	}

	private void handleBounds(boolean gotRecursion, IToken newUnit, boolean isReliable) {
		List<IToken> children = newUnit.getChildren();
		IToken first = children.get(0);
		IToken last = children.get(children.size()-1);
		
		IToken prev = first.getPrevious();
		if(prev!=null){
			handlePreviousBound(newUnit, first, prev, gotRecursion,isReliable);
		}
		else{
			List<IToken> previousTokens = first.getPreviousToken();
			if(previousTokens!=null){
				for(IToken pu : previousTokens){
					handlePreviousBound(newUnit, first, pu, gotRecursion,isReliable);
				}
			}
		}
		IToken next = last.getNext();
		if(next!=null){
			handleNextBound(newUnit, last, next, gotRecursion,isReliable);
		}
		else{
			List<IToken> nextTokens = last.getNextTokens();
			if(nextTokens!=null){
				for(IToken nu : nextTokens){
					handleNextBound(newUnit, last, nu, gotRecursion,isReliable);
				}
			}
		}
	}

	private void handlePreviousBound(IToken newUnit, IToken firstChild,IToken previousUnit, boolean gotRecursion, boolean isReliable)
	{
		newUnit.addPreviousUnit(previousUnit);
		if(gotRecursion){
			previousUnit.addNextUnit(newUnit);
		}
		else{
			IToken nextOfPrevoius = previousUnit.getNext();
			if(nextOfPrevoius!=null){
				if(nextOfPrevoius==firstChild&&isReliable){
					previousUnit.setNext(newUnit);
				}
				else{
					previousUnit.addNextUnit(newUnit);
				}
			}
			else{
				List<IToken> nextTokensOfPreviousToken = previousUnit.getNextTokens();
				if(isReliable){
					for(int i = 0 ; i < nextTokensOfPreviousToken.size();i++){
						if(nextTokensOfPreviousToken.get(i)==firstChild){
							nextTokensOfPreviousToken.set(i, newUnit);
							return;
						}
					}
				}
				nextTokensOfPreviousToken.add(newUnit);
			}
		}
	}
	
	private void handleNextBound(IToken newUnit, IToken lastChild, IToken nextUnit, boolean gotRecursion, boolean isReliable)
	{
		newUnit.addNextUnit(nextUnit);
		if(gotRecursion){
			nextUnit.addPreviousUnit(newUnit);
		}
		else{
			IToken previousOfNext = nextUnit.getPrevious();
			if(previousOfNext!=null){
				if(previousOfNext==lastChild&&isReliable){
					nextUnit.setPrevious(newUnit);
				}
				else{
					nextUnit.addPreviousUnit(newUnit);
				}
			}
			else{
				List<IToken> previousTokensOfNextToken = nextUnit.getPreviousToken();
				if(isReliable){
					for(int i = 0 ; i < previousTokensOfNextToken.size();i++){
						if(previousTokensOfNextToken.get(i)==lastChild){
							previousTokensOfNextToken.set(i, newUnit);
							return;
						}
					}
				}
				previousTokensOfNextToken.add(newUnit);
			}
		}
	}
	

	private boolean parseRecursively( Stack<IToken> sample, LinkedHashSet<IToken> reliableTokens, HashSet<IToken> doubtfulTokens)
	{		
		IToken last = sample.peek();
		int tokensAdded = 0 ;
		boolean gotRecursion = false;
		ProcessingResult pr = CONTINUE_PUSH;
		while((pr = continuePush(sample,last))==CONTINUE_PUSH){
			IToken next = last.getNext();
			if(next!=null){
				sample.add(next);
				tokensAdded++;				
				last = next;
			}
			else{
				List<IToken> nextTokens = last.getNextTokens();
				if(nextTokens!=null&&!nextTokens.isEmpty()){					
					int beforeCount = reliableTokens.size();
					for(IToken nu : nextTokens){
						sample.add(nu);						
						parseRecursively(sample, reliableTokens,doubtfulTokens);						
						sample.pop();
						rollBackState(1);
					}
					int afterCount = reliableTokens.size();					
					gotRecursion = afterCount != beforeCount;
				}
				break;
			}
		}
		int popCount = pr.stepBack;
		rollBackState(popCount);
		
		while(popCount-- > 0){
			sample.pop();
			tokensAdded--;
		}
		
		if(!gotRecursion){
			while(tokensAdded >= 0){
				LinkedHashSet<IToken> rt = new LinkedHashSet<IToken>();
				LinkedHashSet<IToken> dt = new LinkedHashSet<IToken>();
				combineTokens(sample,rt,dt);
				if(!rt.isEmpty()){
					handleChildrenAndParents(sample, rt);
					reliableTokens.addAll(rt);
				}
				if(!dt.isEmpty()){
					handleChildrenAndParents(sample, dt);
					doubtfulTokens.addAll(dt);
				}
				if(tokensAdded > 0){
					sample.pop();
				}
				rollBackState(1);
				tokensAdded--;
				if(!rt.isEmpty()||!dt.isEmpty()){
					break;
				}
			}
		}
		rollBackState(tokensAdded);
		while(tokensAdded-- > 0){
			sample.pop();
		}	
		
		return gotRecursion;
	}

	protected void rollBackState(int stepCount) {}

	private void handleChildrenAndParents(Stack<IToken> sample,Set<IToken> newTokens)
	{
		
		for(IToken newUnit : newTokens){
			int sp = newUnit.getStartPosition();
			int ep = newUnit.getEndPosition();
			
			int childIndex = 0;			
			IToken child = sample.get(childIndex);
			while(child.getEndPosition()<=ep){
				if(child.getStartPosition()>=sp){
					newUnit.addChild(child);
					child.addParent(newUnit);
				}
				childIndex++;
				if(childIndex>=sample.size()){
					break;
				}
				child = sample.get(childIndex);
			}
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}

