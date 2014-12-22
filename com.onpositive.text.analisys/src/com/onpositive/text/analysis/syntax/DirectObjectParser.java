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

public class DirectObjectParser extends AbstractSyntaxParser {

	public DirectObjectParser(AbstractWordNet wordNet) {
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
//	private final UnaryMatcher<SyntaxToken> nounMatchGrammems = hasAny(
//			PartOfSpeech.NOUN, PartOfSpeech.NPRO);	
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

		SyntaxToken predToken = null;
		SyntaxToken objToken = null;
		ClauseToken clauseToken = null;
		if (verbMatchGrammems.match(token0) && or(checkName, infnGrammems).match(token1)) {
			predToken = token0;
			objToken = token1;
		} else if (verbMatchGrammems.match(token1) && or(checkName, infnGrammems).match(token0)) {
			predToken = token1;
			objToken = token0;
		}
		else if(token0.getType()==IToken.TOKEN_TYPE_CLAUSE){
			clauseToken = (ClauseToken) token0;
			predToken = clauseToken.getPredicate();
			token0=predToken;
			objToken = token1;
		}
		else if(token1.getType()==IToken.TOKEN_TYPE_CLAUSE){
			clauseToken = (ClauseToken) token1;
			predToken = clauseToken.getPredicate();
			token1=predToken;
			objToken = token0;
		}
		else{
			return;
		}

		int subjType = infnGrammems.match(objToken) ? IToken.TOKEN_TYPE_DIRECT_OBJECT_INF
				: IToken.TOKEN_TYPE_DIRECT_OBJECT_NAME;
		
		int startPosition = token0.getStartPosition();
		int endPosition = token1.getEndPosition();		
		SyntaxToken newToken = new SyntaxToken(subjType, predToken, null, startPosition, endPosition, isContinuous);

		if(clauseToken!=null){
			boolean doSet = false;
			if(!isContinuous){
				ArrayList<IToken> children = new ArrayList<IToken>(Arrays.asList(token0,token1));
				if (checkParents(newToken, children)) {
					newToken.addChildren(children);
					for(IToken ch: children){
						ch.addParent(newToken);
					}
					doSet = true;
				}
			}
			else{
				doSet=true;
			}
			if(doSet){
				newToken.addChild(token0);
				newToken.addChild(token1);
				clauseToken.setPredicate(newToken);
			}
		}
		else if (checkParents(newToken, sample)) {
			reliableTokens.add(newToken);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample,IToken newToken)
	{
		IToken token0 = sample.get(0);
		IToken token1 = newToken;
		if (verbMatchGrammems.match(token0)||token0.getType()==IToken.TOKEN_TYPE_CLAUSE){
			if(or(checkName, infnGrammems).match(token1)) {
				return ACCEPT_AND_BREAK;
			}
		} else if (verbMatchGrammems.match(token1)) {
			return ACCEPT_AND_BREAK;
		}
//		else if(sample.size()==1 && nounbMatchGrammems.match(newToken)){
//			return CONTINUE_PUSH;
//		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}

	@Override
	protected ProcessingResult checkToken(IToken newToken) {
		if (verbInforName.match(newToken)) {
			return CONTINUE_PUSH;
		}
		if(newToken.getType()==IToken.TOKEN_TYPE_CLAUSE){
			return CONTINUE_PUSH;
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}
}
