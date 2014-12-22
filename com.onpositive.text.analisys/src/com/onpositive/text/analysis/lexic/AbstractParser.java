package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.IToken.Direction;

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
		
		final protected int stepBack;
		
		final protected boolean acceptToken;
		
		final protected boolean stop;

		public ProcessingResult(int stepBack,boolean acceptToken, boolean stop) {
			super();
			this.stepBack = stepBack;
			this.acceptToken = acceptToken;
			this.stop = stop;
		}
		
		protected boolean tokenAccepted(){
			return acceptToken;
		}
		
		protected boolean stopped(){
			return stop;
		}
		
	}
	
	protected String text;
	
	protected boolean hasTriggered = false;
	
	private int currentTokenId;
	
	protected IntObjectOpenHashMap<IToken> parsedTokens = new IntObjectOpenHashMap<IToken>();
	
	protected static final ProcessingResult CONTINUE_PUSH = new ProcessingResult(0,true,false);
	protected static final ProcessingResult ACCEPT_AND_BREAK = new ProcessingResult(0,true,true);
	protected static final ProcessingResult DO_NOT_ACCEPT_AND_BREAK = new ProcessingResult(0,false,true);
	
	protected static ProcessingResult stepBack(int count){
		return new ProcessingResult(count,false,true);
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
	
	
	protected boolean keepInputToken(){
		return true;
	}
	
	
	private Set<IToken> branchRegistry = new HashSet<IToken>();
	
	
	public ArrayList<IToken> process(List<IToken> tokens){
		
		prepareParser(tokens);		
		beforeProcess(tokens);
		
		LinkedHashSet<IToken> reliableTokens = new LinkedHashSet<IToken>();
		LinkedHashSet<IToken> doubtfulTokens = new LinkedHashSet<IToken>(); 
		
		ArrayList<IToken> result = new ArrayList<IToken>();
		ArrayList<IToken> toDiscard = new ArrayList<IToken>();
		for( int i = 0 ; i < tokens.size() ; i++ ){
			IToken token = tokens.get(i);			
			if(inspectBranch(token)){
				continue;
			}
			parseStartingTokens(token,reliableTokens,doubtfulTokens);
			
			boolean tokenReleased = handleBounds(token, reliableTokens, doubtfulTokens);
			if(!tokenReleased){
				insertToken(result, token);
				parsedTokens.put(token.id(),token);
			}
			else{
				toDiscard.add(token);
			}

			result.addAll(reliableTokens);
			result.addAll(doubtfulTokens);
			setTriggered(!(reliableTokens.isEmpty()&&doubtfulTokens.isEmpty()));			
		}
		discardToken(toDiscard);
		return result;
	}

	public static void discardToken(List<IToken> toDiscard) {
		for(IToken t : toDiscard){
			discardNeighbours(t, Direction.START);
			discardNeighbours(t, Direction.END);
		}
	}

	protected static void discardNeighbours(IToken token, Direction dir) {
		IToken neighbour = token.getNeighbour(dir);
		if(neighbour!=null){
			neighbour.removeNeighbour(dir.opposite(), token);
		}
		
		List<IToken> neighbours = token.getNeighbours(dir);
		if(neighbours!=null){
			for(IToken n : neighbours){
				n.removeNeighbour(dir.opposite(), token);
			}
		}
	}

	protected void insertToken(ArrayList<IToken> result, IToken token) {
		
		int sp = token.getStartPosition();
		int ind = -1;
		for( int i = result.size()-1; i >= 0 ; i--){
			IToken t = result.get(i);
			if(t.getStartPosition() <= sp){
				break;
			}
			else{
				ind = i;
			}
		}
		if(ind<0){				
			result.add(token);
		}
		else{
			result.add(null);
			for( int i = result.size()-1; i > ind ; i--){
				result.set(i, result.get(i-1));
			}
			result.set(ind, token);
		}
	}
	

	private boolean checkNextMatches(IToken token,	Set<IToken> reliableTokens, Set<IToken> doubtfulTokens)
	{
		IToken next = token.getNext();
		if(next!=null){
			return checkNextMatch(next,reliableTokens,doubtfulTokens);
		}
		else{
			List<IToken> nextTokens = token.getNextTokens();
			if(nextTokens!=null){
				boolean result = true;
				for(IToken t : nextTokens){
					result &= checkNextMatch(t,reliableTokens,doubtfulTokens);
				}
				return result;
			}
			return false;
		}
	}

	private boolean checkNextMatch(IToken next,Set<IToken> reliableTokens, Set<IToken> doubtfulTokens) {
		
		List<IToken> parents = next.getParents();
		if(parents==null||parents.isEmpty()){
			branchRegistry.add(next);
			return false;
		}
		else{
			boolean gotParent = false;
			for(IToken parent : parents){
				if(reliableTokens.contains(parent)||(!keepInputToken()&&doubtfulTokens.contains(parent))){
					gotParent = true;
					break;
				}
			}
			if(!gotParent){
				branchRegistry.add(next);
				return false;
			}
			return true;
		}
	}

	private boolean inspectBranch(IToken token) {
//		List<IToken> parents = token.getParents();
//		if(parents==null||parents.isEmpty()){
//			return false;
//		}
//		for(IToken parent: parents){
//			if(parsedTokens.containsKey(parent.id())){
//				return true;
//			}
//		}
//		return false;
		if(!branchRegistry.contains(token)){
			List<IToken> parents = token.getParents();
			if(parents!=null&&!parents.isEmpty()){
				for(IToken parent: parents){
					if(parsedTokens.containsKey(parent.id())){
						return true;
					}
				}
			}
		}
		else{
			branchRegistry.remove(token);
		}
		return false;
	}
	
	private void parseStartingTokens(IToken token, LinkedHashSet<IToken> reliableTokens, LinkedHashSet<IToken> doubtfulTokens) {
		
		reliableTokens.clear();
		doubtfulTokens.clear();
		prepare();
		
		ProcessingResult pr = checkPossibleStart(token);
		if(!pr.tokenAccepted()){
			return;
		}				
		
		Stack<IToken> sample = new Stack<IToken>();
		sample.add(token);		
		parseRecursively(sample,pr,reliableTokens,doubtfulTokens);
		
		if(reliableTokens.isEmpty()&&doubtfulTokens.isEmpty()){
			return;
		}
		registerParsedTokens(reliableTokens);
		registerParsedTokens(doubtfulTokens);		
		cleanUp();
	}



	private void registerParsedTokens(Set<IToken> tokens) {
		for(IToken token : tokens){
			int id = ++currentTokenId;
			token.setId(id);
			parsedTokens.put(id, token);
		}		
	}

	private boolean handleBounds(IToken token, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens)
	{
		boolean isUnitLength = true;
		for(IToken t : reliableTokens){
			if(t.getChildren().size()>1){
				isUnitLength=false;
				break;
			}
		}
		if(isUnitLength&&!keepInputToken()){
			for(IToken t : doubtfulTokens){
				if(t.getChildren().size()>1){
					isUnitLength=false;
					break;
				}
			}
		}
		boolean matchesAll = checkNextMatches(token, reliableTokens, doubtfulTokens);
		if(isUnitLength){
			matchesAll = !reliableTokens.isEmpty() || (!keepInputToken()&&!doubtfulTokens.isEmpty());
		}
		for(IToken t : reliableTokens){
			handleBounds(t,true);
		}
		for(IToken t : doubtfulTokens){
			handleBounds(t,true);
		}
		if(!matchesAll){
			handleBounds(token,false);
		}
		return matchesAll;
	}

	private void handleBounds(IToken token, boolean newLevel) {
		
		IToken boundToken = null;
		if(newLevel){
			boundToken = token.getFirstChild(Direction.START);
		}
		else{
			boundToken = token;
		}

		IToken neighbour = boundToken.getNeighbour(Direction.START);
		if(neighbour!=null){
			handleBounds(neighbour,boundToken,token);
		}
		else{
			List<IToken> neighbours = boundToken.getNeighbours(Direction.START);
			if(neighbours!=null){
				neighbours = new ArrayList<IToken>(neighbours);
				for(IToken n : neighbours){
					handleBounds(n,boundToken,token);
				}
			}
		}
	}

	private void handleBounds(IToken neighbour, IToken boundToken,IToken token) {
		if(parsedTokens.containsKey(neighbour.id())){
			neighbour.addNeighbour(token, Direction.END);
			token.addNeighbour(neighbour, Direction.START);
		}
		List<IToken> parents = neighbour.getParents();
		if(parents==null){
			return;
		}
		for(IToken parent : parents){
			if(!parsedTokens.containsKey(parent.id())){
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

	
	private boolean parseRecursively( Stack<IToken> sample, ProcessingResult pr, LinkedHashSet<IToken> reliableTokens, HashSet<IToken> doubtfulTokens)
	{
		int inputSize = sample.size();
		IToken last = sample.peek();
		boolean gotRecursion = false;
		
		while(pr == CONTINUE_PUSH) {
			IToken next = last.getNext();
			if(next!=null){
				pr = continuePush(sample,next);
				if(pr.tokenAccepted()){
					sample.add(next);
					last = next;
				}
			}
			else{
				List<IToken> nextTokens = last.getNextTokens();
				if(nextTokens!=null&&!nextTokens.isEmpty()){					
					int beforeCount1 = reliableTokens.size();
					int beforeCount2 = doubtfulTokens.size();
					for(IToken nt : nextTokens){
						pr = continuePush(sample,nt);
						if(pr.tokenAccepted()){
							sample.add(nt);
							parseRecursively(sample, pr, reliableTokens,doubtfulTokens);
							sample.pop();
							rollBackState(1);
						}						
					}
					int afterCount1 = reliableTokens.size();
					int afterCount2 = doubtfulTokens.size();
					gotRecursion |= afterCount1 != beforeCount1 || afterCount2 != beforeCount2;
				}
				break;
			}
		}		
		
		int popCount = Math.min(pr.stepBack,sample.size()-inputSize);
		rollBackState(popCount);
		for(int i = 0 ; i < popCount ; i++ ){
			sample.pop();
		}
		
		if(!gotRecursion){
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
		}
		popCount = sample.size()-inputSize;
		rollBackState(popCount);
		for(int i = 0 ; i < popCount ; i++ ){
			sample.pop();
		}	
		
		return gotRecursion;
	}

	protected void rollBackState(int stepCount) {}

	private void handleChildrenAndParents(Stack<IToken> sample,Set<IToken> newTokens)
	{
		
		for(IToken newToken : newTokens){
			List<IToken> children = newToken.getChildren();
			if(children!=null&&!children.isEmpty()){
				continue;
			}
			int sp = newToken.getStartPosition();
			int ep = newToken.getEndPosition();
			
			int childIndex = 0;			
			IToken child = sample.get(childIndex);
			while(child.getEndPosition()<=ep){
				if(child.getStartPosition()>=sp){
					newToken.addChild(child);
					child.addParent(newToken);
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

	public boolean hasTriggered() {
		return hasTriggered;
	}

	public void resetTrigger() {
		this.hasTriggered = false;
	}

	private void setTriggered(boolean value) {
		this.hasTriggered |= value;
	}	
	
	private void prepareParser(List<IToken> tokens) {
		
		parsedTokens.clear();		
		currentTokenId = 0;
		for(IToken token : tokens){
			currentTokenId = Math.max(currentTokenId, token.id());
		}
	}

	public boolean isRecursive() {
		return true;
	}
	
}

