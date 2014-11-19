package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.text.analysis.IToken;

public class WordFormParser extends AbstractParser {

	public WordFormParser(AbstractWordNet wordNet) {
		super();
		this.wordNet = wordNet;
	}
	
	private AbstractWordNet wordNet;
	
	private ArrayList<WordSequenceData> dataList = new ArrayList<WordSequenceData>();
	
	private GrammarRelation[] firstWordForms;
	
	@Override
	protected void combineTokens(Stack<IToken> sample, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens){
		
		ArrayList<WordFormToken> tokens = new ArrayList<WordFormToken>();
		IToken firstToken = sample.get(0);
		int startPosition = firstToken.getStartPosition();
		int endPosition = firstToken.getEndPosition();
		
		HashSet<TextElement> occuredElements = new HashSet<TextElement>(); 
		
		if(dataList.isEmpty()){
			for(GrammarRelation gr : firstWordForms){
				TextElement word = gr.getWord();
				if(occuredElements.contains(word)){
					continue;
				}
				occuredElements.add(word);
				WordFormToken token = new WordFormToken(word, startPosition, endPosition);
				tokens.add(token);
			}
		}
		else{
			for(WordSequenceData data : dataList)
			{
				if(!data.gotMatch()){
					continue;
				}
				TextElement te = data.textElement();
				if(occuredElements.contains(te)){
					continue;
				}
				occuredElements.add(te);
				
				int endIndex = data.getSequenceLength()-1;
				endPosition = sample.get(endIndex).getEndPosition();				
				WordFormToken token = new WordFormToken(te, startPosition, endPosition);
				tokens.add(token);
			}
		}
		if(tokens.size()==1){
			reliableTokens.add(tokens.get(0));
		}
		else{
			doubtfulTokens.addAll(tokens);
		}
	}

	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample, IToken newToken) {
		
		ProcessingResult result = CONTINUE_PUSH;
		
		String value = newToken.getStringValue();
		GrammarRelation[] possibleGrammarForms = wordNet.getPossibleGrammarForms(value.toLowerCase());
		
		if( sample.size() == 1 ){
			firstWordForms = possibleGrammarForms;
			
			for(GrammarRelation gr : possibleGrammarForms){
				TextElement word = gr.getWord();
				TextElement[] possibleContinuations = wordNet.getPossibleContinuations(word);
				if(possibleContinuations!=null){
					for(TextElement te : possibleContinuations){
						TextElement[] parts = te.getParts();
						int[] arr = new int[parts.length];
						for(int i = 0 ; i < parts.length ; i++){
							arr[i] = parts[i].id();
						}
						WordSequenceData data = new WordSequenceData(te,arr);
						dataList.add(data);
					}
				}
			}
		}
		else{
			if(dataList.isEmpty()){
				result = DO_NOT_ACCEPT_AND_BREAK;
			}
			else{
				for(WordSequenceData data : dataList){
					data.setPosition(sample.size()-1);
				}
				if(possibleGrammarForms!=null&&possibleGrammarForms.length!=0){
					
					boolean needContinue = false;
					for(WordSequenceData data : dataList){
						for(GrammarRelation gr : possibleGrammarForms){
							int wordId = gr.getWord().id();
							needContinue |= data.check(wordId);
						}
					}
					if(!needContinue){
						result = DO_NOT_ACCEPT_AND_BREAK;
					}
				}
				else{
					result = DO_NOT_ACCEPT_AND_BREAK;
				}
			}
		}
		return result;
	}
	
	protected ProcessingResult checkPossibleStart(IToken token){
		
		String value = token.getStringValue();
		GrammarRelation[] possibleGrammarForms = wordNet.getPossibleGrammarForms(value.toLowerCase());
		if(possibleGrammarForms==null||possibleGrammarForms.length==0){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		return CONTINUE_PUSH;
	};

	protected ProcessingResult checkToken(IToken unit) {
		throw new UnsupportedOperationException("Check Token not supported for Word Form Parser");
	}
	
	protected void prepare(){
		dataList.clear();
		firstWordForms = null;
	}
	
	
	protected void cleanUp(){
		dataList.clear();
		firstWordForms = null;
	}
	
	protected void rollBackState(int stepCount) {
		for(WordSequenceData data : dataList){
			data.rollBack(stepCount);
		}
	}
	
	protected boolean keepInputToken(){
		return false;
	}
	
	private static class WordSequenceData{
		
		public WordSequenceData(TextElement textElement,int[] sequence) {
			this.textElement = textElement;
			this.sequence = sequence;
		}

		private final int[] sequence;
		
		private final TextElement textElement;
		
		private int matchPosition = 0;
		
		private int currentPosition = 0;
		
		private boolean gotMatch(){
			boolean result = matchPosition == sequence.length-1;
			return result;
		}
		
		private boolean check(int wordId){
			
			if(currentPosition<sequence.length && currentPosition == matchPosition+1){
				if(sequence[currentPosition]==wordId){
					matchPosition = currentPosition;
				}
			}
			boolean result = matchPosition == currentPosition;
			return result;
		}
		

		public void setPosition(int pos) {
			this.currentPosition = pos;
			
		}
		
		private void rollBack(int stepCount){
			currentPosition -= stepCount;
			matchPosition = Math.min(currentPosition, matchPosition);
		}

		public TextElement textElement() {
			return textElement;
		}
		
		public int getSequenceLength(){
			return sequence.length;
		}
	}

}
