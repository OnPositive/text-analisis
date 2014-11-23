package com.onpositive.text.analysis.lexic;

import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.words3.MetaLayer;
import com.onpositive.text.analysis.IToken;

public class NumericsParser extends AbstractParser{

	AbstractWordNet net;
	private MetaLayer<Object> layer;
	
	public NumericsParser(AbstractWordNet net) {
		super();
		this.net = net;
		layer = net.getMetaLayers().getLayer("numbers");
		
	}

	@Override
	protected void combineTokens(Stack<IToken> sample,
			Set<IToken> reliableTokens, Set<IToken> doubtfulTokens) {
		double value=0;
		int start=Integer.MAX_VALUE;
		int end=Integer.MIN_VALUE;
		for (IToken q:sample){
			start=Math.min(start, q.getStartPosition());
			end=Math.max(end, q.getEndPosition());
			if (q instanceof ScalarToken){
				ScalarToken tk=(ScalarToken) q;
				value=tk.getValue1();
			}
			
			if (q instanceof WordFormToken){
				WordFormToken tk=(WordFormToken) q;
				TextElement textElement = tk.getTextElement();
				if (PartOfSpeech.NUMR.mayBeThisPartOfSpech(textElement)){
					Double value2 = (Double) layer.getValue(textElement);
					if (value2==null){
						
						return;
					}
					value+=value2;
				}
			}
		}
		reliableTokens.add(new ScalarToken(value, start, end));
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		if(newToken instanceof ScalarToken){
			return CONTINUE_PUSH;
		}
		if (newToken instanceof WordFormToken){
			WordFormToken tk=(WordFormToken) newToken;
			TextElement textElement = tk.getTextElement();
			if (PartOfSpeech.NUMR.mayBeThisPartOfSpech(textElement)){
				return CONTINUE_PUSH;
			}
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}

}
