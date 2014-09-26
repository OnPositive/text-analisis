package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.onpositive.text.analysis.IToken;

public abstract class AbstractParser {
	
	protected static final int CONTINUE_PUSH = -1;
	
	protected String text;	
	
	abstract protected Set<IToken> combineUnits(Stack<IToken> sample);
	
	abstract protected int continuePush(Stack<IToken> sample);
	
	abstract protected boolean checkAndPrepare(IToken unit);
	
	public ArrayList<IToken> process(List<IToken> units){
		
		ArrayList<IToken> result = new ArrayList<IToken>();		
		for( int i = 0 ; i < units.size() ; i++ ){
			IToken unit = units.get(i);			
			List<IToken> parents = unit.getParents();
			if(parents!=null&&!parents.isEmpty()){
				continue;
			}
			List<IToken> startingUnits = parseStartingUnits(unit);
			if(startingUnits==null){
				result.add(unit);
			}
			else{
				result.addAll(startingUnits);
			}
		}
		return result;
	}

	private List<IToken> parseStartingUnits(IToken unit) {
		
		if(!checkAndPrepare(unit)){
			return null;
		}
		
		ArrayList<IToken> result = new ArrayList<IToken>();
		Stack<IToken> sample = new Stack<IToken>();
		sample.add(unit);		
		boolean gotRecursion = parseRecursively(sample,result);
		
		if(result.isEmpty()){
			return null;
		}
		
		for(IToken newUnit : result){
			List<IToken> children = newUnit.getChildren();
			IToken first = children.get(0);
			IToken last = children.get(children.size()-1);
			
			IToken prev = first.getPrevious();
			if(prev!=null){
				handlePreviousBound(newUnit, first, prev, gotRecursion);
			}
			else{
				List<IToken> previousUnits = first.getPreviousUnits();
				if(previousUnits!=null){
					for(IToken pu : previousUnits){
						handlePreviousBound(newUnit, first, pu, gotRecursion);
					}
				}
			}
			IToken next = last.getNext();
			if(next!=null){
				handleNextBound(newUnit, last, next, gotRecursion);
			}
			else{
				List<IToken> nextUnits = last.getNextUnits();
				if(nextUnits!=null){
					for(IToken nu : nextUnits){
						handleNextBound(newUnit, last, nu, gotRecursion);
					}
				}
			}
			
		}		
		return result;
	}

	private void handlePreviousBound(IToken newUnit, IToken firstChild,IToken previousUnit, boolean gotRecursion)
	{
		newUnit.addPreviousUnit(previousUnit);
		if(gotRecursion){
			previousUnit.addNextUnit(newUnit);
		}
		else{
			IToken nextOfPrevoius = previousUnit.getNext();
			if(nextOfPrevoius!=null){
				if(nextOfPrevoius==firstChild){
					previousUnit.setNext(newUnit);
				}
				else{
					previousUnit.addNextUnit(newUnit);
				}
			}
			else{
				List<IToken> nextUnitsOfPreviousUnit = previousUnit.getNextUnits();
				for(int i = 0 ; i < nextUnitsOfPreviousUnit.size();i++){
					if(nextUnitsOfPreviousUnit.get(i)==firstChild){
						nextUnitsOfPreviousUnit.set(i, newUnit);
						return;
					}
				}
				nextUnitsOfPreviousUnit.add(newUnit);
			}
		}
	}
	
	private void handleNextBound(IToken newUnit, IToken lastChild, IToken nextUnit, boolean gotRecursion)
	{
		newUnit.addNextUnit(nextUnit);
		if(gotRecursion){
			nextUnit.addPreviousUnit(newUnit);
		}
		else{
			IToken previousOfNext = nextUnit.getPrevious();
			if(previousOfNext!=null){
				if(previousOfNext==lastChild){
					nextUnit.setPrevious(newUnit);
				}
				else{
					nextUnit.addPreviousUnit(newUnit);
				}
			}
			else{
				List<IToken> previousUnitsOfNextUnit = nextUnit.getPreviousUnits();
				for(int i = 0 ; i < previousUnitsOfNextUnit.size();i++){
					if(previousUnitsOfNextUnit.get(i)==lastChild){
						previousUnitsOfNextUnit.set(i, newUnit);
						return;
					}
				}
				previousUnitsOfNextUnit.add(newUnit);
			}
		}
	}
	

	private boolean parseRecursively( Stack<IToken> sample, ArrayList<IToken> result)
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
					int beforeCount = result.size();
					for(IToken nu : nextUnits){
						sample.add(nu);						
						parseRecursively(sample, result);
						sample.pop();						
					}
					int afterCount = result.size();					
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
				Set<IToken> newUnits = combineUnits(sample);				
				if(newUnits!=null&&!newUnits.isEmpty()){
					handleChildrenAndParents(sample, newUnits);
					result.addAll(newUnits);
				}
				if(unitsAdded > 0){
					sample.pop();
				}
				unitsAdded--;
				if(newUnits!=null){
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

