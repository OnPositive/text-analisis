package com.onpositive.text.analysis.syntax;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.Gender;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.Grammem.SingularPlural;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.WordFormToken;

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
		IToken token0 = sample.get(0);
		IToken token1 = sample.get(1);
		
		List<IToken> parents1 = token1.getParents();
		List<IToken> parents0 = token0.getParents();
		if((parents1!=null&&!parents1.isEmpty())&&(parents0!=null&&!parents0.isEmpty())){
			for(IToken parent : parents0){
				if(parents1.contains(parent)){
					return;
				}
			}
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


	private ArrayList<IToken> matchMeanings(IToken token0, IToken token1) {
		
		SyntaxToken st0 = (SyntaxToken) token0;
		SyntaxToken st1 = (SyntaxToken) token1;
		
		WordFormToken wft0 = st0.getMainWord();
		WordFormToken wft1 = st1.getMainWord();
				
		MeaningElement me0 = wft0.getMeaningElement();
		MeaningElement me1 = wft1.getMeaningElement();
		
		ArrayList<IToken> tokens = new ArrayList<IToken>();
					
		boolean isNoun0 = me0.getGrammems().contains(PartOfSpeech.NOUN);
		boolean isAdjv0 = me0.getGrammems().contains(PartOfSpeech.ADJF);
			
		boolean isNoun1 = me1.getGrammems().contains(PartOfSpeech.NOUN);
		boolean isAdjv1 = me1.getGrammems().contains(PartOfSpeech.ADJF);
				
		IToken newToken = null;
		if(isNoun0 && isAdjv1){
			newToken = combineNounAndAdjective(wft0,wft1,me0,me1,st0);
		}
		if(isAdjv0 && isNoun1){
			newToken = combineNounAndAdjective(wft1,wft0,me1,me0,st1);
		}
		if(newToken!=null){
			tokens.add(newToken);
		}
		return tokens;
	}
	
	
//	@Override
//	protected boolean keepInputToken() {
//		return false;
//	}


	private IToken combineNounAndAdjective(
			WordFormToken nounToken,
			WordFormToken adjvToken,
			MeaningElement nounMeaning,
			MeaningElement adjvMeaning,
			SyntaxToken mainGroup)
	{
		int startPosition = Math.min(nounToken.getStartPosition(), adjvToken.getStartPosition());
		int endPosition = Math.max(nounToken.getEndPosition(), adjvToken.getEndPosition());
		List<GrammarRelation> nounRelations = nounToken.getGrammarRelations();
		List<GrammarRelation> adjvRelations = adjvToken.getGrammarRelations();
		
		for(GrammarRelation nounRel : nounRelations){
			
			Set<Grammem> nounGrammems = new HashSet<Grammem>(nounRel.getGrammems());
			MeaningElement[] nounConcepts = nounRel.getWord().getConcepts();
			for(MeaningElement me : nounConcepts){
				nounGrammems.addAll(me.getGrammems());
			}
			Set<Case> nounCases = extractGrammems(nounGrammems,Case.class);
			Set<SingularPlural> nounSP = extractGrammems(nounGrammems,SingularPlural.class);
			Set<Gender> nounGender = extractGrammems(nounGrammems,Gender.class);
			
			for(GrammarRelation adjvRel : adjvRelations){
				Set<Grammem> adjvGrammems = adjvRel.getGrammems();
				Set<Case> adjvCases = extractGrammems(adjvGrammems,Case.class);
				Map<Case, Case> matchCase = matchCase(nounCases,adjvCases);
				if(matchCase==null){
					continue;
				}
				Set<SingularPlural> adjvSP = extractGrammems(adjvGrammems,SingularPlural.class);
				Map<SingularPlural, SingularPlural> matchSP = matchSP(nounSP,adjvSP);
				if(matchSP==null){
					continue;
				}
				Set<Gender> adjvGender = extractGrammems(adjvGrammems,Gender.class);
				Set<Gender> matchGender = matchGender(nounGender,adjvGender);
				if(matchGender==null){
					continue;
				}
				return new SyntaxToken(IToken.TOKEN_TYPE_NOUN_ADJECTIVE, mainGroup, startPosition, endPosition);
			}
		}
		return null;
	}



	private Set<Gender> matchGender(Set<Gender> set0, Set<Gender> set1) {
		
		if(set0.contains(Gender.UNKNOWN)){
			set1.remove(Gender.UNKNOWN);
			return set1;
		}
		if(set1.contains(Gender.UNKNOWN)){
			return set0;
		}
		if(set0.contains(Gender.COMMON)){
			return set1;
		}
		if(set1.contains(Gender.COMMON)){
			return set0;
		}
		HashSet<Gender> result = new HashSet<Grammem.Gender>();
		for(Gender g : set0){
			if(set1.contains(g)){
				result.add(g);
			}
		}
		return result.isEmpty() ? null : result;
	}


	private Map<SingularPlural,SingularPlural> matchSP(Set<SingularPlural> set0, Set<SingularPlural> set1) {
		return matchGrammem(set0, set1, spMatchMap);
	}


	private Map<Case,Case> matchCase(Set<Case> set0, Set<Case> set1) {
		return matchGrammem(set0, set1, caseMatchMap);
	}


	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample,IToken newToken) {
		
		if(!(newToken instanceof SyntaxToken)){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		
		WordFormToken token1 = ((SyntaxToken) newToken).getMainWord();		
		MeaningElement me1 = token1.getMeaningElement();
		Set<Grammem> grammems1 = me1.getGrammems();
		if(grammems1.contains(PartOfSpeech.PREP)){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		
		WordFormToken token0 = ((SyntaxToken) sample.peek()).getMainWord();
		MeaningElement me0 = token0.getMeaningElement();
		
		Set<Grammem> grammems0 = me0.getGrammems();
		if(grammems0.contains(PartOfSpeech.NOUN)){
			if(grammems1.contains(PartOfSpeech.ADJF)||grammems1.contains(PartOfSpeech.ADJS)){
				return ACCEPT_AND_BREAK;
			}
		}
		if(grammems0.contains(PartOfSpeech.ADJF)||grammems0.contains(PartOfSpeech.ADJS)){
			if(grammems1.contains(PartOfSpeech.NOUN)){
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
		
		WordFormToken wft = ((SyntaxToken) newToken).getMainWord();
		MeaningElement me = wft.getMeaningElement();
		Set<Grammem> grammems = me.getGrammems();
		if(grammems.contains(PartOfSpeech.PREP)){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		if(grammems.contains(PartOfSpeech.NOUN)){
			return CONTINUE_PUSH;
		}
		if(grammems.contains(PartOfSpeech.ADJF)){
			return CONTINUE_PUSH;
		}
		if(grammems.contains(PartOfSpeech.ADJS)){
			return CONTINUE_PUSH;
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}
}
