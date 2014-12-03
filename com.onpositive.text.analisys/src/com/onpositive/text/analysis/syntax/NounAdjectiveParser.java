package com.onpositive.text.analysis.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;

public class NounAdjectiveParser extends AbstractSyntaxParser{

	
	public NounAdjectiveParser(AbstractWordNet wordNet) {
		super();
		this.wordNet = wordNet;
	}

	@Override
	protected void combineTokens(Stack<IToken> sample,Set<IToken> reliableTokens, Set<IToken> doubtfulTokens)
	{
		if(sample.size()<2){
			return;
		}
		SyntaxToken token0 = (SyntaxToken) sample.get(0);
		SyntaxToken token1 = (SyntaxToken) sample.get(1);
		
		if(checkIfAlreadyProcessed(token0, token1)){
			return;
		}
		
		ArrayList<IToken> rawTokens = matchMeanings(token0, token1);
		ArrayList<IToken> tokens = new ArrayList<IToken>();
		for(IToken newToken : rawTokens){
			if(checkParents(newToken,sample)){
				tokens.add(newToken);
			}
		}
		
		if(tokens.size()==1){
			reliableTokens.add(tokens.get(0));
		}
		else if(!tokens.isEmpty()){
			doubtfulTokens.addAll(tokens);
		}
	}

	private ArrayList<IToken> matchMeanings(SyntaxToken token0, SyntaxToken token1)
	{
		int tokenType = IToken.TOKEN_TYPE_NOUN_ADJECTIVE;
		ArrayList<IToken> tokens = new ArrayList<IToken>();
		if(token0.hasGrammem(PartOfSpeech.NOUN)&&token1.hasGrammem(PartOfSpeech.ADJF)){
			tokens.addAll(combineNames(token0,token1,tokenType));
		}
		if(token1.hasGrammem(PartOfSpeech.NOUN)&&token0.hasGrammem(PartOfSpeech.ADJF)){
			tokens.addAll(combineNames(token1,token0,tokenType));
		}
		return tokens;
	}
	
	
//	@Override
//	protected boolean keepInputToken() {
//		return false;
//	}

	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample,IToken newToken) {
		
		if(!(newToken instanceof SyntaxToken)){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		
		SyntaxToken token1 = (SyntaxToken) newToken;
		if(token1.hasGrammem(PartOfSpeech.PREP)){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		
		SyntaxToken token0 = (SyntaxToken) sample.peek();		
		if(token0.hasGrammem(PartOfSpeech.NOUN)){
			if(token1.hasGrammem(PartOfSpeech.ADJF)){
				return ACCEPT_AND_BREAK;
			}
		}
		if(token0.hasGrammem(PartOfSpeech.ADJF)){
			if(token1.hasGrammem(PartOfSpeech.NOUN)){
				return ACCEPT_AND_BREAK;
			}
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}
	
	private static final Set<PartOfSpeech> acceptedPartsOfSpeech
			= new HashSet<Grammem.PartOfSpeech>(Arrays.asList(PartOfSpeech.NOUN, PartOfSpeech.ADJF));

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		
		if(!(newToken instanceof SyntaxToken)){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		
		SyntaxToken token = (SyntaxToken) newToken;
		if(token.hasGrammem(PartOfSpeech.PREP)){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		if(token.hasOneOfGrammems(acceptedPartsOfSpeech)){
			return CONTINUE_PUSH;
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}
}
