package com.onpositive.text.analysis;

import java.util.ArrayList;
import java.util.List;

import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.onpositive.text.analysis.IToken.Direction;

public class TokenBoundsHandler {
	
	protected IntObjectOpenHashMap<IToken> resultTokens = new IntObjectOpenHashMap<IToken>();
	
	protected IntObjectOpenHashMap<IToken> newTokens = new IntObjectOpenHashMap<IToken>();
	
	public void setResultTokens(IntObjectOpenHashMap<IToken> resultTokens) {
		this.resultTokens = resultTokens;
	}

	public void setNewTokens(IntObjectOpenHashMap<IToken> newTokens) {
		this.newTokens = newTokens;
	}

	public void handleBounds(List<IToken> tokens){		
		for(IToken t : tokens){
			processToken(t, newTokens.containsKey(t.id()));
		}		
	}
	
	private void processToken(IToken token, boolean newLevel) {
		
		if(newLevel){
			List<IToken> children = token.getChildren();
			for(IToken ch : children){
				ch.addParent(token);
			}
		}
		
		IToken boundToken = null;
		if(newLevel){
			boundToken = token.getFirstChild(Direction.START);
		}
		else{
			boundToken = token;
		}

		IToken neighbour = boundToken.getNeighbour(Direction.START);
		if(neighbour!=null){
			processNeighbour(neighbour,boundToken,token);
		}
		else{
			List<IToken> neighbours = boundToken.getNeighbours(Direction.START);
			if(neighbours!=null){
				neighbours = new ArrayList<IToken>(neighbours);
				for(IToken n : neighbours){
					processNeighbour(n,boundToken,token);
				}
			}
		}
	}

	private void processNeighbour(IToken neighbour, IToken boundToken,IToken token) {
		
		if(resultTokens.containsKey(neighbour.id())){
			neighbour.addNeighbour(token, Direction.END);
			token.addNeighbour(neighbour, Direction.START);
		}
		List<IToken> parents = neighbour.getParents();
		if(parents==null){
			return;
		}
		for(IToken parent : parents){
			if(!resultTokens.containsKey(parent.id())){
				continue;
			}
			IToken lastChildOfParent = parent.getChild(0, Direction.END);
			if(lastChildOfParent!=neighbour){
				continue;
			}
			parent.addNeighbour(token, Direction.END);
			token.addNeighbour(parent, Direction.START);
		}
	}


}
