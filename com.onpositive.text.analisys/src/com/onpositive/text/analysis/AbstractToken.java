package com.onpositive.text.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.onpositive.text.analysis.IToken.Direction;


public abstract class AbstractToken implements IToken {
	
	protected AbstractToken(int tokenType, int startPosition, int endPosition)
	{
		this.tokenType = tokenType;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}
	
	private int id;
	
	private IToken next;
	
	private IToken previous;
	
	private ArrayList<IToken> nextTokens;
	
	private ArrayList<IToken> previousTokens;	
	
	private final int tokenType;
	
	private final int startPosition;
	
	private final int endPosition;
	
	protected List<IToken> children;
	
	private List<IToken> parents;
	
	public int id() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public void addNextToken(IToken token) {
		if(this.next == null){
			if( this.nextTokens==null||this.nextTokens.isEmpty()){		
				this.next = token;
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
		if(!this.nextTokens.contains(token)){
			this.nextTokens.add(token);
		}
	}
	
	public void removeNextToken(IToken token) {
		if(this.next == null){
			if( this.nextTokens==null){		
				return;
			}
			else{
				this.nextTokens.remove(token);
			}
		}
		else{
			if(this.next==token){
				this.next = null;
			}
		}
	}
	
	public void removePreviousToken(IToken token) {
		if(this.previous == null){
			if( this.previousTokens==null||this.previousTokens.isEmpty()){		
				return;
			}
			else{
				this.previousTokens.remove(token);
			}
		}
		else{
			if(this.previous==token){
				this.previous = null;
			}
		}
	}
	
	public void removeNeighbour(Direction direction, IToken token) {
		if(direction == Direction.END){
			removeNextToken(token);
		}
		else{
			removePreviousToken(token);
		}
	}

	public List<IToken> getPreviousToken() {
		return previousTokens;
	}

	public void addPreviousToken(IToken token) {
		if(this.previous == null){
			if( this.previousTokens==null){		
				this.previous = token;
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
		if(!this.previousTokens.contains(token)){
			this.previousTokens.add(token);
		}
	}
	

	@Override
	public IToken getNeighbour(Direction direction) {
		if(direction == Direction.END){
			return getNext();
		}
		else{
			return getPrevious();
		}
	}

	@Override
	public void setNeighbour(IToken token, Direction direction) {
		if(direction == Direction.END){
			setNext(token);
		}
		else{
			setPrevious(token);
		}		
	}

	@Override
	public List<IToken> getNeighbours(Direction direction) {
		if(direction == Direction.END){
			return getNextTokens();
		}
		else{
			return getPreviousToken();
		}
	}

	@Override
	public void addNeighbour(IToken token, Direction direction) {
		if(direction == Direction.END){
			addNextToken(token);
		}
		else{
			addPreviousToken(token);
		}
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
	
	public void addChildren(Collection<IToken> children){
		if(this.children==null){
			this.children = new ArrayList<IToken>();
		}
		this.children.addAll(children);
	}
	
	public void setChildren(Collection<IToken> children){
		this.children.clear();
		this.addChildren(children);
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
	
	public IToken getFirstChild(Direction direction){
		if(direction==Direction.START){
			return children.get(0);
		}
		else{
			return children.get(children.size()-1);
		}
	}
	
	public IToken getChild(int pos, Direction direction){
		
		int size = children.size();
		if(size<pos){
			return null;
		}
		if(direction==Direction.START){
			return children.get(pos);
		}
		else{
			return children.get(size-1-pos);
		}
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
