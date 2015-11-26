package com.onpositive.text.analysis.filtering;

import java.util.List;

import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.SymbolToken;
import com.onpositive.text.analysis.lexic.WordFormToken;

/**
 * Should be used only for splitted sentences!
 * @author 32kda
 */
public class IntjFilter implements ITokenFilter {

	@Override
	public boolean shouldFilterOut(IToken token) {
		IToken intj = getIntj(token);
		if (intj != null) {
			IToken prev = getPrevToken(token);
			if (isSentenceTerminator(prev)) {
				prev = null;
			}
			IToken next = getNextToken(token);
			if (isSentenceTerminator(next)) {
				prev = null;
			}
			if (token != intj) {
				if (prev == null && next == null) {
					return true;
				}
				if (prev == null && 
					next instanceof SymbolToken && 
					",".equals(next.getStringValue().trim())) {
					return true;
				}
				if (next == null && 
					prev instanceof SymbolToken && 
					",".equals(prev.getStringValue().trim())) {
					return true;
				}
			}
		}
		return false;
	}

	protected IToken getPrevToken(IToken token) {
		IToken previous = token.getPrevious();
		if (previous == null) {
			if (token.getPreviousTokens() != null && !token.getPreviousTokens().isEmpty()) {
				return token.getPreviousTokens().get(0);
			}
		}
		return previous;
	}
	
	protected IToken getNextToken(IToken token) {
		IToken next = token.getNext();
		if (next == null) {
			if (token.getNextTokens() != null && !token.getNextTokens().isEmpty()) {
				return token.getNextTokens().get(0);
			}
		}
		return next;
	}
	
	private boolean isSentenceTerminator(IToken prev) {
		if (prev != null) {
			String value = prev.getStringValue().trim();
			return prev instanceof SymbolToken && 
			(".".equals(value) || "!".equals(value) || "?".equals(value));
		}
		return false;
	}

	protected IToken getIntj(IToken token) {
		if (isIntj(token)) {
			return token;
		}
		List<IToken> conflicts = token.getConflicts();
		for (IToken curToken : conflicts) {
			if (isIntj(curToken)) {
				return curToken;
			}
		}
		return token;
	}

	protected boolean isIntj(IToken token) {
		return token instanceof WordFormToken && ((WordFormToken) token).getPartOfSpeech() == PartOfSpeech.INTJ;
	}

}
