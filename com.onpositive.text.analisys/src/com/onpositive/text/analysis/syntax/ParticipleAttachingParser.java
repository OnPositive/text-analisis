package com.onpositive.text.analysis.syntax;

import java.util.List;
import java.util.Stack;

import com.carrotsearch.hppc.IntOpenHashSet;
import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.text.analysis.BaseArrayInspector;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.IToken.Direction;

public class ParticipleAttachingParser extends AbstractSyntaxParser {

	public ParticipleAttachingParser(AbstractWordNet wordNet) {
		super(wordNet);
	}
	
	private BaseArrayInspector baseArrayInspector = new BaseArrayInspector() {
		
		@Override
		protected boolean match(IToken t) {			
			boolean result = nounMatch.match(t);
			return result;
		}
	};
	
	private SyntaxTokenAttacher attacher = new SyntaxTokenAttacher();

	@Override
	protected void combineTokens(Stack<IToken> sample,ProcessingData processingData)
	{
		IToken token = sample.peek();
		List<IToken> baseTokens = getBaseTokens();
		IntOpenHashSet baseTokenIDs = getBaseTokenIDs();
		List<IToken> lNouns = baseArrayInspector.findToken(token, Direction.START, baseTokens,baseTokenIDs);
		List<IToken> rNouns = baseArrayInspector.findToken(token, Direction.END, baseTokens,baseTokenIDs);
		for(IToken t : lNouns){
			attacher.attachToken(token, t, IToken.TOKEN_TYPE_NOUN_PARTICIPLE);
		}
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		if(prepMatch.match(newToken)){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		if(participleMatch.match(newToken)){
			return ACCEPT_AND_BREAK;
		}		
		return DO_NOT_ACCEPT_AND_BREAK;
	}
	
	@Override
	public boolean isHandleBounds() {
		return false;
	}

}
