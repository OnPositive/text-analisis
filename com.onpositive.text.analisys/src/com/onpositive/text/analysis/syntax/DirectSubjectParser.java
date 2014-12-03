package com.onpositive.text.analysis.syntax;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.TransKind;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.WordFormToken;

public class DirectSubjectParser extends AbstractSyntaxParser {

	@Override
	protected void combineTokens(Stack<IToken> sample, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens)
	{
		if(sample.size()<2){
			return;
		}

		SyntaxToken token0 = (SyntaxToken) sample.get(0);
		SyntaxToken token1 = (SyntaxToken) sample.peek();
		
		int startPosition = token0.getStartPosition();
		int endPosition = token1.getEndPosition();
		
		Set<Grammem> grammems0 = ((SyntaxToken)token0).getMainWord().getMeaningElement().getGrammems();
		SyntaxToken verbToken = null;
		SyntaxToken subjToken = null;
		if(checkVerb(grammems0)){
			verbToken = token0;
			subjToken = token1;
		}
		else{
			verbToken = token1;
			subjToken = token0;
		}
		int subjType = checkInf(subjToken.getMainWord().getMeaningElement().getGrammems())
				? IToken.TOKEN_TYPE_DIRECT_SUBJECT_INF
				: IToken.TOKEN_TYPE_DIRECT_SUBJECT_NAME;

		IToken newToken = new SyntaxToken(subjType, verbToken, startPosition, endPosition);
		if(checkParents(newToken,sample)){
			reliableTokens.add(newToken);
		}
	}
	
	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample,IToken newToken)
	{
		if(!(newToken instanceof SyntaxToken)){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		
		IToken firstToken = sample.peek();
		Set<Grammem> grammems0 = ((SyntaxToken)firstToken).getMainWord().getMeaningElement().getGrammems();
		
		WordFormToken mainWord1 = ((SyntaxToken)newToken).getMainWord();
		MeaningElement me1 = mainWord1.getMeaningElement();		
		if(checkVerb(grammems0)){
			
			List<GrammarRelation> grammarRelations = mainWord1.getGrammarRelations();
			for(GrammarRelation gr : grammarRelations){
				Set<Grammem> grammems1 = new HashSet<Grammem>(me1.getGrammems());
				grammems1.addAll(gr.getGrammems());
				if(checkName(grammems1)){
					return ACCEPT_AND_BREAK;
				}
				if(checkInf(grammems1)){
					return ACCEPT_AND_BREAK;
				}
			}
		}
		else{
			Set<Grammem> grammems1 = me1.getGrammems();
			if(checkVerb(grammems1)){
				return ACCEPT_AND_BREAK;
			}
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		
		if(!(newToken instanceof SyntaxToken)){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		
		WordFormToken mainWord = ((SyntaxToken) newToken).getMainWord();
		WordFormToken wft = mainWord;
		MeaningElement me = wft.getMeaningElement();
		
		List<GrammarRelation> grammarRelations = wft.getGrammarRelations();		
		for(GrammarRelation gr : grammarRelations){
			Set<Grammem> grammems = new HashSet<Grammem>(me.getGrammems());
			grammems.addAll(gr.getGrammems());
			if(checkVerb(grammems)){
				return CONTINUE_PUSH;
			}
			if(checkInf(grammems)){
				return CONTINUE_PUSH;
			}
			if(checkName(grammems)){
				return CONTINUE_PUSH;
			}
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}

	
	private final static PartOfSpeech[] acceptedNames = 
			new PartOfSpeech[]{	PartOfSpeech.NOUN, PartOfSpeech.ADJF };
	
	
	private boolean checkName(Set<Grammem> grammems)
	{
		for(PartOfSpeech pt : acceptedNames){
			if(grammems.contains(pt)){
				Set<Case> cases = caseMatchMap.get(Case.ACCS);
				for(Case c : cases){
					if(grammems.contains(c)){
						return true;
					}
				}
			}
		}
		return false;
	}
	

	private boolean checkVerb(Set<Grammem> grammems) {
		if(grammems.contains(PartOfSpeech.VERB)&&grammems.contains(TransKind.tran)){
			return true;
		}
		return false;
	}
	
	
	private boolean checkInf(Set<Grammem> grammems) {
		if(grammems.contains(PartOfSpeech.INFN)){
			return true;
		}
		return false;
	}
}
