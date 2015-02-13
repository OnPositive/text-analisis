package com.onpositive.text.analysis.syntax;

import java.util.List;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IParser;
import com.onpositive.text.analysis.ParserComposition2;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;

public class VerbNameComposition extends ParserComposition2 {
	
	private static UnaryMatcher<SyntaxToken> verbMatch = AbstractSyntaxParser.hasAny( PartOfSpeech.VERB, PartOfSpeech.INFN );
	
	private static IParser[] createParsers(AbstractWordNet wordNet){
		return new IParser[]{
				new VerbNamePrepositionParser(wordNet),
				new VerbNameParser(wordNet),
				new DirectObjectParser(wordNet),
				new NounNamePrepositionParser(wordNet)
		};
	};
	
	
	public VerbNameComposition( AbstractWordNet wordNet) {
		super(true, createParsers(wordNet));
	}

	@Override
	protected void initRegistry() {
		
		this.reg = new TokenModificationRegistry(this.parsers){
			
			@Override
			protected void resolveConflictPrecisely(List<TokenModificationData> dataList) {


				int validParserId = -1;
				for(TokenModificationData data : dataList){
					
					SyntaxToken token = (SyntaxToken) data.getProducedToken();
					if(verbMatch.match(token)){
						validParserId = data.getParserId();
						break;
					}
				}
				if(validParserId==-1){
					validParserId = dataList.get(0).getParserId();
				}
				for(TokenModificationData data : dataList)
				{
					int parserId = data.getParserId();
					if(!matchParserId(validParserId, parserId)){
						data.setCanceled(true,validParserId);
					}
				}
								
			}

			protected boolean matchParserId(int validParserId, int parserId) {
				if(parserId != validParserId){
					if((parserId == 1 && validParserId == 2) || (parserId == 2 && validParserId == 1)){
						return true;
					}
					else{
						return false;
					}
				}
				return true;
			}
		};
	}

}
