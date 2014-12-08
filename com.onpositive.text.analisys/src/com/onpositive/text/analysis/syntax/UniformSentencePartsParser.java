package com.onpositive.text.analysis.syntax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.StringToken;
import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.rules.matchers.BiMatcher;
import com.onpositive.text.analysis.rules.matchers.BiOrMatcher;
import com.onpositive.text.analysis.rules.matchers.HasGrammem;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;

public class UniformSentencePartsParser extends AbstractSyntaxParser {
	
	private static final Set<String> noCommaConjunctions = new HashSet<String>(Arrays.asList(
		"и", "да", "или", "либо"
	));
		
		
	private static final Set<String> commaConjunctions = new HashSet<String>(Arrays.asList(
		"да", "но", "а" , "однако", "хотя", "и"
	));
		
		
	private static final Set<String> acceptedConjunctions = new HashSet<String>();
	static{
		acceptedConjunctions.addAll(noCommaConjunctions);
		acceptedConjunctions.addAll(commaConjunctions);
	}
	
	
	public UniformSentencePartsParser(AbstractWordNet wordNet, int tokenType, Collection<PartOfSpeech> acceptedParts) {
		this(wordNet, tokenType, acceptedParts.toArray(new PartOfSpeech[acceptedParts.size()]));
	}

	public UniformSentencePartsParser(AbstractWordNet wordNet, int tokenType, PartOfSpeech... acceptedParts) {
		super(wordNet);
		this.acceptedParts = acceptedParts;
		this.tokenType = tokenType;
		initMatchers();
	}
	
	private int tokenType;

	private PartOfSpeech[] acceptedParts;

	@Override
	protected void combineTokens(Stack<IToken> sample, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens)
	{
		List<IToken> parts = collectParts(sample);
		if(parts.size()<2){
			return;
		}
		matchMembers(parts);
		int count = parts.size();
		if(count<2){
			return;
		}
		SyntaxToken last = (SyntaxToken) parts.get(count-1);
		int startPosition = sample.get(0).getStartPosition();
		int endPosition = last.getEndPosition();
		SyntaxToken newToken = new SyntaxToken(tokenType, last, startPosition, endPosition);
		if(checkParents(newToken,sample)){
			reliableTokens.add(newToken);
		}
	}

	private Set<Grammem> matchMembers(List<IToken> parts) {
		
		SyntaxToken token0 = (SyntaxToken)parts.get(0);
		Set<Grammem> grammems = token0.getAllGrammems();		
		int size = parts.size();
		int i = 1;
		for( ; i < size ; i++ ){
			IToken token = parts.get(i);
			if(!refineGrammemSet(grammems,(SyntaxToken) token)){
				break;
			}
		}
		for(int j = size ; j > i ; j--){
			parts.remove(j-1);
		}
		return grammems;
	}

	protected boolean refineGrammemSet(Set<Grammem> grammems, SyntaxToken token) {
		return true;
	}

	private List<IToken> collectParts(Stack<IToken> tokens) {
		ArrayList<IToken> result = new ArrayList<IToken>();
		for(IToken token : tokens){
			if(partOfSpeechMatcher.match(token)){
				result.add(token);
			}
		}
		return result;
	}
	
	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample,IToken newToken)
	{
		IToken token0 = sample.peek();
		if(commaMatcher.match(token0)){
			return checkToken(newToken);
		}
		else if(conjunctionMatcher.match(newToken)){
			if(partOfSpeechMatcher.match(newToken)){
				return CONTINUE_PUSH;
			}
			else{
				return DO_NOT_ACCEPT_AND_BREAK;
			}
		}
		else{
			if(commaMatcher.match(newToken)){
				return CONTINUE_PUSH;
			}
			return checkToken(newToken);
		}
	}

	@Override
	@SuppressWarnings("unchecked")	
	protected ProcessingResult checkToken(IToken newToken) {
		
		if(or(partOfSpeechMatcher,conjunctionMatcher).match(newToken)){
			return CONTINUE_PUSH;
		}
		return DO_NOT_ACCEPT_AND_BREAK;
	}
	
	protected UnaryMatcher<StringToken> commaMatcher;
	
	protected UnaryMatcher<SyntaxToken> conjunctionMatcher;
	
	protected UnaryMatcher<SyntaxToken> partOfSpeechMatcher;
	
	private void initMatchers(){
		commaMatcher = new UnaryMatcher<StringToken>(StringToken.class) {
		
			@Override
			protected boolean innerMatch(StringToken token) {
				int type = token.getType();
				if(type != IToken.TOKEN_TYPE_SYMBOL){
					return false;
				}
				boolean result = token.getStringValue().equals(",");			
				return result;
			}
		};
		
		conjunctionMatcher = new UnaryMatcher<SyntaxToken>(SyntaxToken.class) {
		
			@Override
			protected boolean innerMatch(SyntaxToken token) {
				
				if(!(token instanceof WordFormToken)){
					return false;
				}
				
				String stringValue = token.getStringValue().toLowerCase();
				if(new HasGrammem(PartOfSpeech.CONJ).match(token)){			
					if(acceptedConjunctions.contains(stringValue)){
						return true;
					}
				}
				return false;
			}
		};
	
		partOfSpeechMatcher = hasAny(acceptedParts);
	}
}
