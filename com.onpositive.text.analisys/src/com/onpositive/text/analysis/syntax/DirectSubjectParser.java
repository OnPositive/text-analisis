package com.onpositive.text.analysis.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.Grammem.TransKind;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;

public class DirectSubjectParser extends AbstractSyntaxParser {

	public DirectSubjectParser(AbstractWordNet wordNet) {
		super(wordNet);
	}

	private final UnaryMatcher<SyntaxToken> acceptedNames = hasAny(
			PartOfSpeech.NOUN/*, PartOfSpeech.ADJF*/);
	private final UnaryMatcher<SyntaxToken> acceptedAcc = hasAny(caseMatchMap
			.get(Case.ACCS));

	private final UnaryMatcher<SyntaxToken> acceptedGC = hasAny(caseMatchMap
			.get(Case.GENT));

	@SuppressWarnings("unchecked")
	private final UnaryMatcher<SyntaxToken> checkName = and(
			acceptedNames,
			or(acceptedAcc, and(acceptedGC, not(has(Grammem.SingularPlural.SINGULAR)))));
	private final UnaryMatcher<SyntaxToken> verbMatchGrammems = hasAll(
			PartOfSpeech.VERB, TransKind.tran);
	private final UnaryMatcher<SyntaxToken> nounbMatchGrammems = hasAny(
			PartOfSpeech.NOUN, PartOfSpeech.NPRO);	
	private final UnaryMatcher<SyntaxToken> infnGrammems = has(PartOfSpeech.INFN);

	@SuppressWarnings("unchecked")
	UnaryMatcher<SyntaxToken> verbInforName = or(verbMatchGrammems,
			infnGrammems, checkName);

	@Override
	protected void combineTokens(Stack<IToken> sample,
			Set<IToken> reliableTokens, Set<IToken> doubtfulTokens) {
		if (sample.size() < 2) {
			return;
		}

		SyntaxToken token0 = (SyntaxToken) sample.get(0);
		SyntaxToken token1 = (SyntaxToken) sample.peek();
		
		boolean isContinuous = sample.size()==2;

		if (checkIfAlreadyProcessed(token0, token1)) {
			return;
		}

		SyntaxToken verbToken = null;
		SyntaxToken subjToken = null;
		if (verbMatchGrammems.match(token0) && or(checkName, infnGrammems).match(token1)) {
			verbToken = token0;
			subjToken = token1;
		} else if (verbMatchGrammems.match(token1) && or(checkName, infnGrammems).match(token0)) {
			verbToken = token1;
			subjToken = token0;
		}
		else{
			return;
		}

		int subjType = infnGrammems.match(subjToken) ? IToken.TOKEN_TYPE_DIRECT_SUBJECT_INF
				: IToken.TOKEN_TYPE_DIRECT_SUBJECT_NAME;
		
		int startPosition = token0.getStartPosition();
		int endPosition = token1.getEndPosition();		
		IToken newToken = new SyntaxToken(subjType, verbToken, null, startPosition, endPosition, isContinuous);
		if(!isContinuous){
			ArrayList<IToken> children = new ArrayList<IToken>(Arrays.asList(token0,token1));
			if (checkParents(newToken, children)) {
				newToken.addChildren(children);
				for(IToken ch: children){
					ch.addParent(newToken);
				}
				reliableTokens.add(newToken);
			}			
		}
		else if (checkParents(newToken, sample)) {
			reliableTokens.add(newToken);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample,
			IToken newToken) {
		IToken token0 = sample.get(0);
		IToken token1 = newToken;
		if (verbMatchGrammems.match(token0)
				&& or(checkName, infnGrammems).match(token1)) {
			return ACCEPT_AND_BREAK;
		} else if (verbMatchGrammems.match(token1)) {
			return ACCEPT_AND_BREAK;
		}
		else if(sample.size()==1 && nounbMatchGrammems.match(newToken)){
			return CONTINUE_PUSH;
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		if (verbInforName.match(newToken)) {
			return CONTINUE_PUSH;
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}
}
