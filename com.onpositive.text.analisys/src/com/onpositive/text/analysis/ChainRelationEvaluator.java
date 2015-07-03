package com.onpositive.text.analysis;

import java.io.File;

import com.onpositive.text.analysis.lexic.StringToken;
import com.onpositive.text.analysis.lexic.SymbolToken;
import com.onpositive.text.analysis.lexic.WordFormToken;

public class ChainRelationEvaluator extends AbstractRelationEvaluator {

	public static final String CHAINDATA_FILE_NAME = "chains.dat";
	
	
	private MarkovChain chains = null;
	
	public ChainRelationEvaluator() {
		chains = MarkovChain.load(new File(CHAINDATA_FILE_NAME));
	}
	
	
	@Override
	public void clear() {}

	
	private double L1 =  0.1, L2 = 0.5, L3 = 0.4;	
	
	private WordFormToken[] prevs(IToken token) {
		return token
			.getPreviousTokens()
			.stream()
			.filter(x->x instanceof WordFormToken)
			.map(x->(WordFormToken)x)
			.toArray(WordFormToken[]::new);
	}
	
	private double calcPP(WordFormToken token) {
		if (token == null || token.getPreviousTokens() == null) return 0;
		
		if (token.getMeaningElements().length < 1) return 0;
		
		short k = token.getMeaningElements()[0].getGrammemCode();
	
		double W1 = chains.P(k);
		
		double W2 = 0.0, W3 = 0.0;
		int W2c = 0, W3c = 0;
		
		WordFormToken[] ps = prevs(token);
		
		for (WordFormToken p : ps) {
			if (p.getMeaningElements().length < 1) continue;
			short k1 = p.getMeaningElements()[0].getGrammemCode();
			W2 = (W2 * W2c + chains.P(k1, k)) / (++W2c);
			WordFormToken[] pps = prevs(p);
			for (WordFormToken pp : pps) {
				if (pp.getMeaningElements().length < 1) continue;
				short k0 = pp.getMeaningElements()[0].getGrammemCode();
				W3 = (W3 * W3c + chains.P(k0, k1, k)) / (++W3c);
			}
		}
		
		return L1 * W1 + L2 * W2 + L3 * W3;
	}
	
	
	@Override
	protected double calculate(IToken token) {
		if (chains == null || (token instanceof StringToken || token instanceof SymbolToken) || (token instanceof WordFormToken && token.getConflicts().size() == 0))
			return 1.0;

		if (token instanceof WordFormToken) {
			WordFormToken wft = (WordFormToken) token;
			return calcPP(wft);
		} else {
			if (token.getChildren() == null) return 0;
			double P = 1;
			for (IToken ch : token.getChildren()) P *= ch.getCorrelation();
			return P;
		}
	}

}
