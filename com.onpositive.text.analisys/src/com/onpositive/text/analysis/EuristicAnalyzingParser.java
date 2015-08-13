package com.onpositive.text.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.onpositive.text.analysis.lexic.WordFormToken;

public class EuristicAnalyzingParser extends AbstractParser{
	
	private static final int SEQUENCE_LENGTH = 2;
	
	private List<Euristic> euristics;
	
	private List<List<IToken>> possibleChains;
	
	public EuristicAnalyzingParser(List<Euristic> euristics) {
		this.euristics = euristics;
	}

	public List<IToken> process(List<IToken> tokens) {
		List<List<IToken>> possibleChains = calcVariants(tokens);
		if (possibleChains.isEmpty()) {
			this.possibleChains = Collections.emptyList(); 
			return Collections.emptyList();
		}
		List<IToken> curChain = possibleChains.get(0);
		for (int i = 0; i < curChain.size(); i++) {
			IToken token = curChain.get(i);
			if (token instanceof WordFormToken && token.hasConflicts()) {
				List<List<IToken>> invalidChains = new ArrayList<List<IToken>>();
				for (int j = 0; j < possibleChains.size(); j++) {
					List<List<IToken>> sequences = getSequences(possibleChains.get(j), i);
					boolean found = false;
					for (List<IToken> curSequence: sequences) {
						if (matchedNonConflict(euristics, curSequence)) {
							found = true;
							break;
						}
					}
					if (!found) {
						invalidChains.add(possibleChains.get(j));
					}
				}
				if (!invalidChains.isEmpty()) {
					possibleChains.removeAll(invalidChains);
					if (!possibleChains.isEmpty()) {
						curChain = possibleChains.get(0);
					} else {
						this.possibleChains = Collections.emptyList(); 
						return Collections.emptyList();
					}
				}
			} 
		}
		this.possibleChains = possibleChains;
		return possibleChains.get(0);
	}
	
	private List<List<IToken>> getSequences(List<IToken> tokens, int idx) {
		List<List<IToken>> result = new ArrayList<List<IToken>>();
		int start = Math.max(0, idx - SEQUENCE_LENGTH + 1);
		for (int i = start; i <= idx && i + SEQUENCE_LENGTH <= tokens.size(); i++) {
			List<IToken> curList = new ArrayList<IToken>();
			for (int j = i; j < i + SEQUENCE_LENGTH; j++) {
				curList.add(tokens.get(j));
			}
			result.add(curList);
		}
		return result;
	}
		
	private List<IToken> doGetMatched(List<Euristic> euristicsToTry, List<List<IToken>> possibleChains) {
		for (Euristic euristic : euristicsToTry) {
			for (List<IToken> list : possibleChains) {
				boolean match = euristic.match(list.toArray(new IToken[0]));
				if (match) {
					return list;
				}
			}
		}
		return null;
	}
	
	private boolean matchedNonConflict(List<Euristic> euristicsToTry, List<IToken> tokens) {
		for (Euristic euristic : euristicsToTry) {
			if (euristic.match(tokens.toArray(new IToken[0]))) {
				return true;
			}
		}
		return false;
	}

	private List<List<IToken>> calcVariants(List<IToken> source) {
		List<IToken> workingCopy = new ArrayList<IToken>(source);
		List<List<IToken>> result = new ArrayList<List<IToken>>();
		result.add(new ArrayList<IToken>());
		for (int i = 0; i < workingCopy.size(); i++) {
			if (workingCopy.get(i).getConflicts() == null || workingCopy.get(i).getConflicts().isEmpty()) {
				addItem(result, workingCopy.get(i));
			} else {
				List<IToken> conflicts = new ArrayList<IToken>(workingCopy.get(i).getConflicts());
				conflicts.add(0, workingCopy.get(i));
				while (i+1 < workingCopy.size() && conflicts.contains(workingCopy.get(i+1))) {
					workingCopy.remove(i+1);
				}
				result = generateVariants(result,conflicts);
			}
		}
		return result;
	}
	
	private List<List<IToken>> generateVariants(List<List<IToken>> prevResult, List<IToken> conflicts) {
		List<List<IToken>> result = new ArrayList<List<IToken>>();
		for (List<IToken> list : prevResult) {
			for (IToken curToken : conflicts) {
				List<IToken> newList = new ArrayList<IToken>(list);
				newList.add(curToken);
				result.add(newList);
			}
		}
		return result;
	}

	private void addItem(List<List<IToken>> result, IToken token) {
		for (List<IToken> list : result) {
			list.add(token);
		}
	}


	@Override
	public boolean isRecursive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setHandleBounds(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<IToken> getNewTokens() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTokenIdProvider(TokenIdProvider tokenIdProvider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TokenIdProvider getTokenIdProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clean() {
		// TODO Auto-generated method stub
		
	}

	public List<List<IToken>> getPossibleChains() {
		return possibleChains;
	}


}
