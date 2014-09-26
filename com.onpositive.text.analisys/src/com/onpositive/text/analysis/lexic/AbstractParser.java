package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.onpositive.text.analysis.IToken;

public abstract class AbstractParser {
	
	protected static final int CONTINUE_PUSH = -1;
	
	protected String text;	
	
	abstract protected void combineUnits(Stack<IToken> sample, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens);
	
	abstract protected int continuePush(Stack<IToken> sample);
	
	abstract protected boolean checkAndPrepare(IToken unit);
	
	public ArrayList<IToken> process(List<IToken> units){
		
		
		LinkedHashSet<IToken> reliableTokens = new LinkedHashSet<IToken>();
		LinkedHashSet<IToken> doubtfulTokens = new LinkedHashSet<IToken>(); 
		
		ArrayList<IToken> result = new ArrayList<IToken>();
		for( int i = 0 ; i < units.size() ; i++ ){
			IToken unit = units.get(i);			
			List<IToken> parents = unit.getParents();
			if(parents!=null&&!parents.isEmpty()){
				continue;
			}
			reliableTokens.clear();
			doubtfulTokens.clear();
			parseStartingUnits(unit,reliableTokens,doubtfulTokens);
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

	private void parseStartingUnits(IToken unit, LinkedHashSet<IToken> reliableTokens, LinkedHashSet<IToken> doubtfulTokens) {
		
		if(!checkAndPrepare(unit)){
			return;
		}
		
		
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
			List<IToken> previousUnits = first.getPreviousUnits();
			if(previousUnits!=null){
				for(IToken pu : previousUnits){
					handlePreviousBound(newUnit, first, pu, gotRecursion,isReliable);
				}
			}
		}
		IToken next = last.getNext();
		if(next!=null){
			handleNextBound(newUnit, last, next, gotRecursion,isReliable);
		}
		else{
			List<IToken> nextUnits = last.getNextUnits();
			if(nextUnits!=null){
				for(IToken nu : nextUnits){
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
				List<IToken> nextUnitsOfPreviousUnit = previousUnit.getNextUnits();
				if(isReliable){
					for(int i = 0 ; i < nextUnitsOfPreviousUnit.size();i++){
						if(nextUnitsOfPreviousUnit.get(i)==firstChild){
							nextUnitsOfPreviousUnit.set(i, newUnit);
							return;
						}
					}
				}
				nextUnitsOfPreviousUnit.add(newUnit);
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
				List<IToken> previousUnitsOfNextUnit = nextUnit.getPreviousUnits();
				if(isReliable){
					for(int i = 0 ; i < previousUnitsOfNextUnit.size();i++){
						if(previousUnitsOfNextUnit.get(i)==lastChild){
							previousUnitsOfNextUnit.set(i, newUnit);
							return;
						}
					}
				}
				previousUnitsOfNextUnit.add(newUnit);
			}
		}
	}
	

	private boolean parseRecursively( Stack<IToken> sample, LinkedHashSet<IToken> reliableTokens, HashSet<IToken> doubtfulTokens)
	{		
		IToken last = sample.peek();
		int unitsAdded = 0 ;
		boolean gotRecursion = false;
		int popCount = -1;
		while((popCount = continuePush(sample))==CONTINUE_PUSH){
			IToken next = last.getNext();
			if(next!=null){
				sample.add(next);
				unitsAdded++;				
				last = next;
			}
			else{
				List<IToken> nextUnits = last.getNextUnits();
				if(nextUnits!=null&&!nextUnits.isEmpty()){					
					int beforeCount = reliableTokens.size();
					for(IToken nu : nextUnits){
						sample.add(nu);						
						parseRecursively(sample, reliableTokens,doubtfulTokens);						
						sample.pop();						
					}
					int afterCount = reliableTokens.size();					
					gotRecursion = afterCount != beforeCount;
				}
				break;
			}
		}
		while(popCount-- > 0){
			sample.pop();
			unitsAdded--;
		}
		if(!gotRecursion){
			while(unitsAdded >= 0){
				LinkedHashSet<IToken> rt = new LinkedHashSet<IToken>();
				LinkedHashSet<IToken> dt = new LinkedHashSet<IToken>();
				combineUnits(sample,rt,dt);
				if(!rt.isEmpty()){
					handleChildrenAndParents(sample, rt);
					reliableTokens.addAll(rt);
				}
				if(!dt.isEmpty()){
					handleChildrenAndParents(sample, dt);
					doubtfulTokens.addAll(dt);
				}
				if(unitsAdded > 0){
					sample.pop();
				}
				unitsAdded--;
				if(!rt.isEmpty()||!dt.isEmpty()){
					break;
				}
			}
		}
		while(unitsAdded-- > 0){
			sample.pop();
		}
		return gotRecursion;
	}

	private void handleChildrenAndParents(Stack<IToken> sample,Set<IToken> newUnits)
	{
		
		for(IToken newUnit : newUnits){
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

