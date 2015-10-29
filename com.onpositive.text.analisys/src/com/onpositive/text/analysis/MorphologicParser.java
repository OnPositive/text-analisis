package com.onpositive.text.analysis;

import java.util.ArrayList;
import java.util.List;

import com.onpositive.text.analysis.filtering.ITokenFilter;

public abstract class MorphologicParser extends AbstractParser {

	protected List<ITokenFilter> tokenFilters = new ArrayList<ITokenFilter>();

	public MorphologicParser() {
		super();
	}

	public void addTokenFilter(ITokenFilter filter) {
		tokenFilters.add(filter);
	}

	protected void doFiltering(List<IToken> tokens) {
		for (IToken curToken : tokens) {
			for (ITokenFilter filter : tokenFilters) {
				if (filter.shouldFilterOut(curToken)) {
					curToken.setCorrelation(0.0, Double.POSITIVE_INFINITY);
				}
			}
		}
	}

	protected List<List<IToken>> generateVariants(List<List<IToken>> prevResult, List<IToken> conflicts) {
			List<List<IToken>> result = new ArrayList<List<IToken>>();
			for (List<IToken> list : prevResult) {
				for (IToken curToken : conflicts) {
	//				if (curToken.getCorrelation() < E) {
	//					continue;
	//				}
					List<IToken> newList = new ArrayList<IToken>(list);
					newList.add(curToken);
					result.add(newList);
				}
			}
			return result;
		}

	protected void addItem(List<List<IToken>> result, IToken token) {
		for (List<IToken> list : result) {
			list.add(token);
		}
	}
	
}