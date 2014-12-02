package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.MeaningElement;
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
		
		IntObjectOpenHashMap<IToken> tokens = new IntObjectOpenHashMap<IToken>();
		IToken firstToken = sample.get(0);
		int startPosition = firstToken.getStartPosition();
		int endPosition = firstToken.getEndPosition();
		
		boolean gotSequence = false;
		for(WordSequenceData data : dataList){
			gotSequence |= data.gotMatch();
		}
		
		if(!gotSequence){
			for(GrammarRelation gr : firstWordForms){
				TextElement word = gr.getWord();
				MeaningElement[] concepts = word.getConcepts();
				for(MeaningElement me : concepts){
					int id = me.id();
					IToken token = tokens.get(id);
					if(token == null){
						token = new WordFormToken(me, startPosition, endPosition);
						tokens.put(id, token);
					}
					WordFormToken wft = (WordFormToken) token;
					registerGrammarRelation(gr, wft);
				}
			}
		}
		else{
			for(WordSequenceData data : dataList)
			{
				if(!data.gotMatch()){
					continue;
				}
				TextElement te = data.textElement();
				MeaningElement[] concepts = te.getConcepts();
				for(MeaningElement me : concepts){
					int id = me.id();
					if(tokens.containsKey(id)){
						continue;
					}
					int endIndex = data.getSequenceLength()-1;
					endPosition = sample.get(endIndex).getEndPosition();
					WordFormToken token = new WordFormToken(me, startPosition, endPosition);
					registerGrammarRelation(null, token);
					tokens.put(id,token);
				}
			}
		}
		IToken[] array = tokens.values().toArray(IToken.class);
		if(array.length==1){			
			reliableTokens.add(array[0]);
		}
		else if(array.length>0){
			for(IToken t : array){
				doubtfulTokens.add(t);
			}
		}
	}

	private void registerGrammarRelation(GrammarRelation gr, WordFormToken wft)
	{
		if(gr!=null){
			List<GrammarRelation> grammarRelations = wft.getGrammarRelations();
			boolean exists = false;
			for(GrammarRelation rel : grammarRelations){
				if(rel.equals(gr)){
					exists = true;
					break;
				}
			}
			if(!exists){
				wft.addGrammarRelation(gr);
			}
		}
	}

	@Override
	protected ProcessingResult continuePush(Stack<IToken> sample, IToken newToken) {
		if (newToken.getType()==IToken.TOKEN_TYPE_DIGIT){
			return DO_NOT_ACCEPT_AND_BREAK; 
		}
		ProcessingResult result = CONTINUE_PUSH;
		
		String value = newToken.getStringValue();
		GrammarRelation[] possibleGrammarForms = wordNet.getPossibleGrammarForms(value.toLowerCase());
		
		if(dataList.isEmpty()){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		else{
			for(WordSequenceData data : dataList){
				data.setPosition(sample.size());
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
		return result;
	}
	
	protected ProcessingResult checkPossibleStart(IToken token){
		if (token.getType()==IToken.TOKEN_TYPE_DIGIT){
			return DO_NOT_ACCEPT_AND_BREAK; 
		}
		String value = token.getStringValue();
		GrammarRelation[] possibleGrammarForms = wordNet.getPossibleGrammarForms(value.toLowerCase());
		if(possibleGrammarForms==null||possibleGrammarForms.length==0){
			return DO_NOT_ACCEPT_AND_BREAK;
		}
		
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
