package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.Gender;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.Grammem.SemanGramem;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.syntax.SyntaxToken.GrammemSet;

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
					if(!corresponds(me,gr)){
						continue;
					}
					me = refinePrepositionOrConjunction(me,startPosition,endPosition);
					int id = me.id();
					IToken token = tokens.get(id);
					int endPosition1 = considerAbbrEndPosition(sample,endPosition, me);
					if(token == null){						
						token = new WordFormToken(me, startPosition, endPosition1);
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
					me = refinePrepositionOrConjunction(me,startPosition,endPosition);
					int id = me.id();
					IToken token = tokens.get(id);
					int endIndex = data.getSequenceLength()-1;
					int endPosition1 = sample.get(endIndex).getEndPosition();
					if(token == null){
						token = new WordFormToken(me, startPosition, endPosition1);
						tokens.put(id, token);
					}
					WordFormToken wft = (WordFormToken) token;
					registerGrammarRelation(null, wft);
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

	private MeaningElement refinePrepositionOrConjunction(MeaningElement me, int sp, int ep) {
		
		TextElement te = me.getParentTextElement();
		String str = te.getBasicForm();
		if(te.isMultiWord()){
			StringBuilder bld = new StringBuilder();			
			for(TextElement t : te.getParts()){
				bld.append(" ").append(t.getBasicForm());
			}
			str = bld.toString();
		}
		String txt = getText();
		if(!str.endsWith(" ")&&txt.charAt(ep)=='.'&&me.getGrammems().contains(SemanGramem.ABBR)){
			return me;
		}
		
		MeaningElement preposition = getPreposition(str);
		if(preposition!=null){
			return preposition;
		}
		MeaningElement conjunction = getConjunction(str);
		if(conjunction!=null){
			return conjunction;
		}
		return me;
	}

	protected int considerAbbrEndPosition(Stack<IToken> sample,	int endPosition, MeaningElement me) {
		if(me.getGrammems().contains(SemanGramem.ABBR)){
			if (sample.size()>1){
				IToken secondToken = sample.get(1);
				if(secondToken.getStringValue().equals(".")){
					if(endPosition==secondToken.getStartPosition()){
						endPosition = secondToken.getEndPosition();
					}
				}
			}
		}
		return endPosition;
	}

	private boolean corresponds(MeaningElement me, GrammarRelation gr) {
		
		Set<Grammem> grammems0 = me.getGrammems();
		Set<Grammem> grammems1 = gr.getGrammems();
		if(!matchGrammems(grammems0, grammems1,PartOfSpeech.class)){
			return false;
		}
		if(!matchGrammems(grammems0, grammems1,Gender.class)){
			return false;
		}
		return true;
	}

	protected boolean matchGrammems(Set<Grammem> grammems0, Set<Grammem> grammems1, Class<? extends Grammem> clazz) {
		Set<? extends Grammem> ext0 = GrammemSet.extractGrammems(grammems0, clazz);
		if(!ext0.isEmpty()){
			Set<? extends Grammem> ext1 = GrammemSet.extractGrammems(grammems1, clazz);
			if(!ext1.isEmpty()){
				if(!ext1.containsAll(ext0)){
					return false;
				}
			}
		}
		return true;
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
		int type = newToken.getType();
		if (type==IToken.TOKEN_TYPE_DIGIT){
			return DO_NOT_ACCEPT_AND_BREAK; 
		}
		
		if(type==IToken.TOKEN_TYPE_SYMBOL){
			if(isAbbr&&sample.size()==1&&newToken.getStringValue()=="."){
				return ACCEPT_AND_BREAK;
			}
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
		checkAbbr();
		IntOpenHashSet set = new IntOpenHashSet();
		for(GrammarRelation gr : possibleGrammarForms){
			TextElement word = gr.getWord();
			TextElement[] possibleContinuations = wordNet.getPossibleContinuations(word);
			if(possibleContinuations!=null){
				for(TextElement te : possibleContinuations){
					if(set.contains(te.id())){
						continue;
					}
					TextElement[] parts = te.getParts();
					IntArrayList arr = new IntArrayList();
					for(TextElement part : parts){
						if(part!=null){
							arr.add(part.id());
						}
					}
					if(arr.size()<2){
						continue;
					}
					WordSequenceData data = new WordSequenceData(te,arr.toArray());
					dataList.add(data);
					set.add(te.id());
				}
			}
		}
		
		return CONTINUE_PUSH;
	}
	
	private boolean isAbbr = false;
	
	protected void checkAbbr() {
		isAbbr = false;
		for(GrammarRelation gr : firstWordForms){
			if(gr.getWord().hasGrammem(SemanGramem.ABBR)){
				isAbbr = true;
				return;
			}
		}
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
	
	private static PrepConjRegistry prepConjRegistry;	
	
	protected MeaningElement getPreposition(String str){
		return lookupConjunctionOrPreposition(str, PartOfSpeech.PREP);
	}
	
	protected MeaningElement getConjunction(String str){
		return lookupConjunctionOrPreposition(str, PartOfSpeech.CONJ);
	}

	protected MeaningElement lookupConjunctionOrPreposition(String str, PartOfSpeech pos){ 

		MeaningElement me = null;
		if(pos==PartOfSpeech.PREP){
			me = getPrepConjRegistry().getPreposition(str);
		}
		else if(pos==PartOfSpeech.CONJ){
			me = getPrepConjRegistry().getConjunction(str);
		}
		else{
			return null;
		}
		return me;
	}

	protected PrepConjRegistry getPrepConjRegistry() {
		if(prepConjRegistry==null){
			prepConjRegistry = new PrepConjRegistry(wordNet);
		}
		return prepConjRegistry;
	}

}
