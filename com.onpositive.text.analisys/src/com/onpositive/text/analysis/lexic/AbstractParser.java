package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.onpositive.text.analysis.IUnit;

public abstract class AbstractParser {
	
	protected static final int CONTINUE_PUSH = -1;
	
	protected String text;	
	
	abstract protected Set<IUnit> combineUnits(Stack<IUnit> sample);
	
	abstract protected int continuePush(Stack<IUnit> sample);
	
	abstract protected boolean checkAndPrepare(IUnit unit);
	
	public ArrayList<IUnit> process(List<IUnit> units){
		
		ArrayList<IUnit> result = new ArrayList<IUnit>();		
		for( int i = 0 ; i < units.size() ; i++ ){
			IUnit unit = units.get(i);			
			List<IUnit> parents = unit.getParents();
			if(parents!=null&&!parents.isEmpty()){
				continue;
			}
			List<IUnit> startingUnits = parseStartingUnits(unit);
			if(startingUnits==null){
				result.add(unit);
			}
			else{
				result.addAll(startingUnits);
			}
		}
		return result;
	}

	private List<IUnit> parseStartingUnits(IUnit unit) {
		
		if(!checkAndPrepare(unit)){
			return null;
		}
		
		ArrayList<IUnit> result = new ArrayList<IUnit>();
		Stack<IUnit> sample = new Stack<IUnit>();
		sample.add(unit);		
		boolean gotRecursion = parseRecursively(sample,result);
		
		if(result.isEmpty()){
			return null;
		}
		
		for(IUnit newUnit : result){
			List<IUnit> children = newUnit.getChildren();
			IUnit first = children.get(0);
			IUnit last = children.get(children.size()-1);
			
			IUnit prev = first.getPrevious();
			if(prev!=null){
				handlePreviousBound(newUnit, first, prev, gotRecursion);
			}
			else{
				List<IUnit> previousUnits = first.getPreviousUnits();
				if(previousUnits!=null){
					for(IUnit pu : previousUnits){
						handlePreviousBound(newUnit, first, pu, gotRecursion);
					}
				}
			}
			IUnit next = last.getNext();
			if(next!=null){
				handleNextBound(newUnit, last, next, gotRecursion);
			}
			else{
				List<IUnit> nextUnits = last.getNextUnits();
				if(nextUnits!=null){
					for(IUnit nu : nextUnits){
						handleNextBound(newUnit, last, nu, gotRecursion);
					}
				}
			}
			
		}		
		return result;
	}

	private void handlePreviousBound(IUnit newUnit, IUnit firstChild,IUnit previousUnit, boolean gotRecursion)
	{
		newUnit.addPreviousUnit(previousUnit);
		if(gotRecursion){
			previousUnit.addNextUnit(newUnit);
		}
		else{
			IUnit nextOfPrevoius = previousUnit.getNext();
			if(nextOfPrevoius!=null){
				if(nextOfPrevoius==firstChild){
					previousUnit.setNext(newUnit);
				}
				else{
					previousUnit.addNextUnit(newUnit);
				}
			}
			else{
				List<IUnit> nextUnitsOfPreviousUnit = previousUnit.getNextUnits();
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
	
	private void handleNextBound(IUnit newUnit, IUnit lastChild, IUnit nextUnit, boolean gotRecursion)
	{
		newUnit.addNextUnit(nextUnit);
		if(gotRecursion){
			nextUnit.addPreviousUnit(newUnit);
		}
		else{
			IUnit previousOfNext = nextUnit.getPrevious();
			if(previousOfNext!=null){
				if(previousOfNext==lastChild){
					nextUnit.setPrevious(newUnit);
				}
				else{
					nextUnit.addPreviousUnit(newUnit);
				}
			}
			else{
				List<IUnit> previousUnitsOfNextUnit = nextUnit.getPreviousUnits();
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
	

	private boolean parseRecursively( Stack<IUnit> sample, ArrayList<IUnit> result)
	{		
		IUnit last = sample.peek();
		int unitsAdded = 0 ;
		boolean gotRecursion = false;
		int popCount = -1;
		while((popCount = continuePush(sample))==CONTINUE_PUSH){
			IUnit next = last.getNext();
			if(next!=null){
				sample.add(next);
				unitsAdded++;				
				last = next;
			}
			else{
				List<IUnit> nextUnits = last.getNextUnits();
				if(nextUnits!=null&&!nextUnits.isEmpty()){					
					int beforeCount = result.size();
					for(IUnit nu : nextUnits){
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
				Set<IUnit> newUnits = combineUnits(sample);				
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

	private void handleChildrenAndParents(Stack<IUnit> sample,Set<IUnit> newUnits)
	{
		
		for(IUnit newUnit : newUnits){
			int sp = newUnit.getStartPosition();
			int ep = newUnit.getEndPosition();
			
			int childIndex = 0;			
			IUnit child = sample.get(childIndex);
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

