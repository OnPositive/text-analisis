package com.onpositive.text.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.projection.IProjectionCreator;
import com.onpositive.text.analysis.projection.Projection;
import com.onpositive.text.analysis.rules.RuleSet;
import com.onpositive.text.analysis.syntax.SyntaxToken;
import com.onpositive.text.analysis.utils.MorphologicUtils;

public class EuristicAnalyzingParser extends MorphologicParser{
	
	private static final double CORRELATION_E = 0.01;

	private static final boolean DEBUG = true;
	
	private static final int SEQUENCE_LENGTH = 2;
	
	private List<Euristic> euristics;
	
	private List<Euristic> preffixEuristics = new ArrayList<Euristic>();
	
	private List<Euristic> postfixEuristics = new ArrayList<Euristic>();
	
	private List<Euristic> indifferentEuristics = new ArrayList<Euristic>();
	
	private List<IProjectionCreator> projectionCreators = new ArrayList<IProjectionCreator>();
	
	private IToken checkedToken;

	private Map<IToken, Euristic> matchedEuristics = new HashMap<IToken, Euristic>();
	
	public EuristicAnalyzingParser() {
		this(RuleSet.getFullRulesList());
	}
	
	public EuristicAnalyzingParser(List<Euristic> euristics) {
		this.euristics = euristics;
		sortByTypes();
	}

	private void sortByTypes() {
		for (Euristic euristic : euristics) {
			if (euristic.getContextType() == null) {
				euristic.defineContextType();
			}
			switch (euristic.getContextType()) {
				case PREFIX: preffixEuristics.add(euristic); break;
				case POSTFIX: postfixEuristics.add(euristic); break;
				case INDIFFERENT: indifferentEuristics.add(euristic); break;
			}
		}
	}

	public List<IToken> processPlain(List<IToken> tokens) {
		tokens = tokens.stream().filter(curToken -> (curToken instanceof SyntaxToken)).collect(Collectors.toList());
		List<IToken> resultTokens = applyEuristicsNew(tokens);
		return resultTokens;
	}

	protected List<List<IToken>> applyToProjections(List<List<IToken>> possibleChains) {
		List<Projection> projections = new ArrayList<Projection>();
		for (List<IToken> chain : possibleChains) {
			List<IToken> workingCopy = new ArrayList<IToken>(chain);
			boolean wasApplied = false;
			for (IProjectionCreator creator : projectionCreators) {
				if (creator.isApplicable(workingCopy)) {
					wasApplied = true;
					creator.applyTo(workingCopy);
				}
				if (wasApplied) {
					projections.add(new Projection(chain,workingCopy));
				}
				
			}
		}
		List<List<IToken>> result = new ArrayList<List<IToken>>();
		for (Projection projection : projections) {
			if (projectionMatches(projection)) {
				result.add(projection.original);
			}
		}
		return result;
	}

	private boolean projectionMatches(Projection projection) {
		for (int i = 0; i < projection.tokens.size(); i++) {
			IToken token = projection.tokens.get(i);
			checkedToken = token;
			if (token instanceof WordFormToken && hasConflicts(token) && !tryMatchConflicting(getSequences(projection.tokens,i ))) {
				return false;
			}
		}
		if (DEBUG) {
			System.out.println("Проекция разобрана успешно");
			System.out.println("Оригинал " + projection.original);
			System.out.println("Проекция " + projection.tokens);
		}
		return true;
	}

	protected List<List<IToken>> applyEuristics(List<List<IToken>> possibleChains) {
		List<List<IToken>> result = new ArrayList<List<IToken>>(possibleChains);
		List<IToken> curChain = result.get(0);
		for (int i = 0; i < curChain.size(); i++) {
			IToken token = curChain.get(i);
			if (token instanceof WordFormToken && hasConflicts(token)) {
				List<List<IToken>> invalidChains = new ArrayList<List<IToken>>();
				for (int j = 0; j < result.size(); j++) {
					List<IToken> curResult = result.get(j);
					if (!tryMatchConflicting(getSequences(curResult,i))) {
						invalidChains.add(result.get(j));
					}
				}
				if (!invalidChains.isEmpty() && invalidChains.size() < result.size()) { //If nothing was matched - leave all possibilities for now 
					setTriggered(true);
					result.removeAll(invalidChains);
					if (!result.isEmpty()) {
						curChain = result.get(0);
					} else {
						return Collections.emptyList();
					}
				}
			} 
		}
		return result;
	}
	
	protected List<IToken> applyEuristicsNew(List<IToken> tokens) {
		
		doFiltering(tokens);
		List<IToken> chain = MorphologicUtils.getWithNoConflicts(tokens);
		for (int i = 0; i < chain.size(); i++) {
			IToken token = chain.get(i);
			if (token instanceof WordFormToken && hasConflicts(token)) {
				Set<IToken> passed = new HashSet<IToken>();
				passed.addAll(getPrefixMatched(chain, i));
				passed.addAll(getPostfixMatched(chain,i));
				if (!indifferentEuristics.isEmpty()) {
					List<List<IToken>> localVariants = getLocalVariants(chain, i);
					int interestingIdx = i == 0 ? 0 : SEQUENCE_LENGTH - 1;
					for (int j = 0; j < localVariants.size(); j++) {
						List<IToken> curResult = localVariants.get(j);
						checkedToken = curResult.get(interestingIdx);
						if (tryMatchConflicting(getLocalSequences(curResult))) {
							passed.add(checkedToken);
						}
					}
				}
				List<IToken> allConflicts = new ArrayList<IToken>(token.getConflicts());
				allConflicts.add(token);
				double failedCorrelation = passed.isEmpty() ? 1.0 / allConflicts.size() : 0;
				for (IToken curConflicting : allConflicts) {
					if (passed.contains(curConflicting)) {
						curConflicting.setCorrelation(1,1);
					} else {
						setFailedCorrelation(curConflicting, failedCorrelation);
					} 
				}
			} 
		}
		return tokens;
	}

	private Collection<IToken> getPrefixMatched(List<IToken> chain, int i) {
		if (chain.size() == 1 || i == 0 || preffixEuristics.isEmpty()) {
			return Collections.emptyList();
		}
		List<List<IToken>> pairs = getPairs(chain, i-1, i);
		return tryMatchDirectional(pairs, 1, preffixEuristics);
	}
	
	private Collection<IToken> getPostfixMatched(List<IToken> chain, int i) {
		if (chain.size() == 1 || i == chain.size() - 1 || postfixEuristics.isEmpty()) {
			return Collections.emptyList();
		}
		List<List<IToken>> pairs = getPairs(chain, i, i + 1);
		return tryMatchDirectional(pairs, 0, postfixEuristics);
	}

	protected Collection<IToken> tryMatchDirectional(List<List<IToken>> pairs, int checkedIdx, List<Euristic> euristics) {
		Set<IToken> passed = new HashSet<IToken>();
		for (List<IToken> pair : pairs) {
			this.checkedToken = pair.get(checkedIdx);
			if (matchedNonConflict(euristics, pair)) {
				passed.add(checkedToken);
			}
		}
		return passed;
	}

	private List<List<IToken>> getPairs(List<IToken> chain, int firstIdx, int secondIdx) {
		IToken first = chain.get(firstIdx);
		IToken second = chain.get(secondIdx);
		List<IToken> firstVariantsList = getVariantsList(first);
		List<IToken> secondVariantsList = getVariantsList(second);
		List<List<IToken>> result = new ArrayList<List<IToken>>();
		for (IToken token1 : firstVariantsList) {
			for (IToken token2 : secondVariantsList) {
				List<IToken> pair = new ArrayList<IToken>();
				pair.add(token1);
				pair.add(token2);
				result.add(pair);
			}
		}
		return result;
	}

	private List<IToken> getVariantsList(IToken token) {
		if (!token.hasConflicts()) {
			return Collections.singletonList(token);
		} else {
			List<IToken> result = new ArrayList<IToken>();
			if (isAvailable(token)) {
				result.add(token);
			}
			result.addAll(token.getConflicts().stream().filter(curToken -> isAvailable(curToken)).collect(Collectors.toList()));
			return result;
		}
	}

	protected boolean isAvailable(IToken token) {
		return !token.hasCorrelation() || token.getCorrelation() > CORRELATION_E;
	}

	protected boolean hasConflicts(IToken token) {
		if (token.hasConflicts()) {
			int availCount = 0;
			if (isAvailable(token)) {
				availCount++;
			}
			List<IToken> conflicts = token.getConflicts();
			for (IToken curToken : conflicts) {
				if (isAvailable(curToken)) {
					availCount++;
				}	
			}
			return availCount > 1;
		}
		return false;
	}

	protected void setFailedCorrelation(IToken token, double failedCorrelation) {
		if (failedCorrelation > 0) {
			token.setCorrelation(failedCorrelation,1);
		} else {
			token.setCorrelation(failedCorrelation,Double.POSITIVE_INFINITY);
		}
	}
	
	private List<List<IToken>> getLocalSequences(List<IToken> original) {
		List<List<IToken>> result = new ArrayList<List<IToken>>();
		for (int i = 0; i < original.size() - SEQUENCE_LENGTH + 1; i++) {
			List<IToken> curList = new ArrayList<IToken>();
			for (int j = i; j < i + SEQUENCE_LENGTH; j++) {
				curList.add(original.get(j));
			}
			result.add(curList);
		}
		return result;
	}

	protected boolean tryMatchConflicting(List<List<IToken>> sequences) {
		for (List<IToken> curSequence: sequences) {
			if (matchedNonConflict(indifferentEuristics, curSequence)) {
				return true;
			}
		}
		return false;
	}
	
	public void addProjectionCreator(IProjectionCreator projectionCreator) {
		projectionCreators.add(projectionCreator);
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
	
	private List<List<IToken>> getLocalVariants(List<IToken> tokens, int idx) {
		List<List<IToken>> result = new ArrayList<List<IToken>>();
		result.add(new ArrayList<IToken>());
		int start = Math.max(0, idx - SEQUENCE_LENGTH + 1);
		int end = Math.min(tokens.size() - 1, idx + SEQUENCE_LENGTH - 1);
		for (int i = start; i <= end; i++) {
			IToken curToken = tokens.get(i);
			if (hasConflicts(curToken)) {
				List<IToken> conflicts = new ArrayList<IToken>(curToken.getConflicts());
				conflicts.add(curToken);
				conflicts = conflicts.stream().filter(token -> !token.hasCorrelation() || token.getCorrelation() > 0).collect(Collectors.toList());
				result = generateVariants(result, conflicts);
			} else {
				addItem(result,curToken);
			}
		}
		return result;
	}
	
	private boolean matchedNonConflict(List<Euristic> euristicsToTry, List<IToken> tokens) {
		for (Euristic euristic : euristicsToTry) {
			if (euristic.match(tokens.toArray(new IToken[0]))) {
				if (DEBUG) {
					matchedEuristics.put(checkedToken, euristic);
				}
				return true;
			}
		}
		return false;
	}
	
	public Euristic getMatchedEuristic(IToken token) {
		return matchedEuristics.get(token);
	}
	
	@Override
	public boolean isRecursive() {
		return false;
	}

	@Override
	public void setHandleBounds(boolean b) {
		// Do nothing
	}

	@Override
	public List<IToken> getNewTokens() {
		// Nothing for now
		return null;
	}

	@Override
	public void setTokenIdProvider(TokenIdProvider tokenIdProvider) {
		// Do nothing
	}

	@Override
	public TokenIdProvider getTokenIdProvider() {
		// Do nothing
		return null;
	}

	@Override
	public void clean() {
		// Do nothing
	}

}
