package com.onpositive.text.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class AbstractToken implements IUnit {

	protected AbstractToken(int tokenType, int startPosition, int endPosition)
	{
		this.tokenType = tokenType;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}
	
	private IUnit next;
	
	private IUnit previous;
	
	private ArrayList<IUnit> nextTokens;
	
	private ArrayList<IUnit> previousTokens;	
	
	private final int tokenType;
	
	private final int startPosition;
	
	private final int endPosition;
	
	private List<IUnit> children;
	
	private List<IUnit> parents;
	
	public int getType() {
		return tokenType;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public int getLength() {
		return endPosition - startPosition;
	}

	public IUnit getNext() {
		return next;
	}

	public IUnit getPrevious() {
		return previous;
	}
	
	public void setNext(IUnit unit) {
		this.next = unit;
	}

	public void setPrevious(IUnit unit) {
		this.previous = unit;
	}

	public List<IUnit> getNextUnits() {
		return nextTokens;
	}

	public void addNextUnit(IUnit unit) {
		if(this.next == null){
			if( this.nextTokens==null){		
				this.next = unit;
				return;
			}
		}
		else{
			if(this.nextTokens==null){
				this.nextTokens = new ArrayList<IUnit>();
				this.nextTokens.add(this.next);
				this.next = null;
			}
		}
		this.nextTokens.add(unit);
	}

	public List<IUnit> getPreviousUnits() {
		return previousTokens;
	}

	public void addPreviousUnit(IUnit unit) {
		if(this.previous == null){
			if( this.previousTokens==null){		
				this.previous = unit;
				return;
			}
		}
		else{
			if(this.previousTokens==null){
				this.previousTokens = new ArrayList<IUnit>();
				this.previousTokens.add(this.previous);
				this.previous = null;
			}
		}
		this.previousTokens.add(unit);
	}
	
	public List<IUnit> getChildren(){
		return this.children;
	}	
	
	public void addChild(IUnit child){
		if(this.children==null){
			this.children = new ArrayList<IUnit>();
		}
		this.children.add(child);
	}
	
	public List<IUnit> getParents(){
		return this.parents;
	}
	
	public void addParent(IUnit parent){
		if(this.parents==null){
			this.parents = new ArrayList<IUnit>();
		}
		this.parents.add(parent);
	}
	
	@Override
	public String toString() {
		return getStringValue();
	}
}
