package com.onpositive.text.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class AbstractToken implements IToken {

	protected AbstractToken(int tokenType, int startPosition, int endPosition)
	{
		this.tokenType = tokenType;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}
	
	private IToken next;
	
	private IToken previous;
	
	private ArrayList<IToken> nextTokens;
	
	private ArrayList<IToken> previousTokens;	
	
	private final int tokenType;
	
	private final int startPosition;
	
	private final int endPosition;
	
	private List<IToken> children;
	
	private List<IToken> parents;
	
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

	public IToken getNext() {
		return next;
	}

	public IToken getPrevious() {
		return previous;
	}
	
	public void setNext(IToken unit) {
		this.next = unit;
	}

	public void setPrevious(IToken unit) {
		this.previous = unit;
	}

	public List<IToken> getNextTokens() {
		return nextTokens;
	}

	public void addNextUnit(IToken unit) {
		if(this.next == null){
			if( this.nextTokens==null){		
				this.next = unit;
				return;
			}
		}
		else{
			if(this.nextTokens==null){
				this.nextTokens = new ArrayList<IToken>();
				this.nextTokens.add(this.next);
				this.next = null;
			}
		}
		this.nextTokens.add(unit);
	}

	public List<IToken> getPreviousToken() {
		return previousTokens;
	}

	public void addPreviousUnit(IToken unit) {
		if(this.previous == null){
			if( this.previousTokens==null){		
				this.previous = unit;
				return;
			}
		}
		else{
			if(this.previousTokens==null){
				this.previousTokens = new ArrayList<IToken>();
				this.previousTokens.add(this.previous);
				this.previous = null;
			}
		}
		this.previousTokens.add(unit);
	}
	
	public List<IToken> getChildren(){
		return this.children;
	}	
	
	public void addChild(IToken child){
		if(this.children==null){
			this.children = new ArrayList<IToken>();
		}
		this.children.add(child);
	}
	
	public List<IToken> getParents(){
		return this.parents;
	}
	
	public void addParent(IToken parent){
		if(this.parents==null){
			this.parents = new ArrayList<IToken>();
		}
		this.parents.add(parent);
	}
	
	public boolean hasSpaceAfter(){
		
		int startOfNext = 0;
		if(next!=null){
			startOfNext = next.getStartPosition();
		}
		else if(nextTokens!=null&&!nextTokens.isEmpty()){
			startOfNext = nextTokens.get(0).getStartPosition();
		}
		else{
			return false;
		}
		return this.endPosition < startOfNext;
	}
	
	public boolean hasSpaceBefore(){
		int endOfPrevious = 0;
		if(previous!=null){
			endOfPrevious = previous.getEndPosition();
		}
		else if(previousTokens!=null&&!previousTokens.isEmpty()){
			endOfPrevious = previousTokens.get(0).getEndPosition();
		}
		else{
			return false;
		}
		return this.startPosition > endOfPrevious;
	}
	
	@Override
	public String toString() {
		return getStringValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + endPosition;
		result = prime * result + startPosition;
		result = prime * result + tokenType;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractToken other = (AbstractToken) obj;
		if (endPosition != other.endPosition)
			return false;
		if (startPosition != other.startPosition)
			return false;
		if (tokenType != other.tokenType)
			return false;
		return true;
	}
}
