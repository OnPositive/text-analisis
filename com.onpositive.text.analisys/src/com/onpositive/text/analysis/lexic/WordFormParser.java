package com.onpositive.text.analysis.lexic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import com.onpositive.text.analysis.AbstractParser;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;
import com.onpositive.text.analysis.syntax.SyntaxToken;
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
	protected void combineTokens(Stack<IToken> sample, ProcessingData processingData){
		
		IntObjectOpenHashMap<IToken> tokens = new IntObjectOpenHashMap<IToken>();
		IToken firstToken = sample.get(0);
		int startPosition = firstToken.getStartPosition();
		int endPosition = firstToken.getEndPosition();
		
		boolean gotSequence = false;
		for(WordSequenceData data : dataList){
			gotSequence |= data.gotMatch();
		}
		
		if(!gotSequence){
l0:			for(GrammarRelation gr : firstWordForms){
				gr.getGrammems();
				TextElement word = gr.getWord();
				MeaningElement[] concepts = word.getConcepts();
				for(MeaningElement me : concepts){
					if(!corresponds(me,gr)){
						continue;
					}
					
					boolean matchPrep = false;
					MeaningElement prep = refinePrepositionOrConjunction(me,startPosition,endPosition);
					if(prep!=null){
						me = prep;
						matchPrep = matchPreposition(firstToken);
						if(matchPrep){
							tokens.clear();
						}
					}
					
					int id = me.id();
					IToken token = tokens.get(id);
					int endPosition1 = considerAbbrEndPosition(sample,endPosition, me, gr);
					if(token == null){						
						token = new WordFormToken(me, startPosition, endPosition1);
						tokens.put(id, token);
					}
					WordFormToken wft = (WordFormToken) token;
					wft.setLink(firstToken.getLink());
					registerGrammarRelation(gr, wft);
					
					if(matchPrep){
						break l0;
					}
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
					
					boolean matchPrep = false;
					MeaningElement prep = refinePrepositionOrConjunction(me,startPosition,endPosition);
					if(prep!=null){
						me = prep;
						matchPrep = matchPreposition(firstToken);
						if(matchPrep){
							tokens.clear();
						}
					}
					
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
					
					if(matchPrep){
						break;
					}
				}
			}
		}
		IToken[] array = tokens.values().toArray(IToken.class);
		if(array.length==1){			
			processingData.addReliableToken(array[0]);
		}
		else if(array.length>0){
			processingData.addDoubtfulTokens(array);
		}
	}

	private boolean matchPreposition(IToken token) {

		String val = token.getStringValue().toLowerCase();
		UnaryMatcher<SyntaxToken> matcher = prepConjRegistry.getPrepCaseMatcher(val);
		if (matcher == null) {
			return false;
		}

		IToken next = token.getNext();
		ArrayList<WordFormToken> nextWFTs = new ArrayList<WordFormToken>();
		if (next != null) {			
			nextWFTs.addAll(createDrafts(next));
		} else {
			List<IToken> nextTokens = token.getNextTokens();
			if (nextTokens != null) {
				for (IToken n : nextTokens) {
					nextWFTs.addAll(createDrafts(n));
				}
			}
		}
		for(WordFormToken t : nextWFTs){
			if(matcher.match(t)){
				return true;
			}
		}
		return false;
	}

	private Collection<? extends WordFormToken> createDrafts(IToken next) {
		
		ArrayList<WordFormToken> list = new ArrayList<WordFormToken>();
		if(next.getType() != IToken.TOKEN_TYPE_LETTER){
			return list;
		}
		
		int startPosition = next.getStartPosition();
		int endPosition = next.getEndPosition();
		
		IntObjectOpenHashMap<WordFormToken> map = new IntObjectOpenHashMap<WordFormToken>();
		
		String sv = next.getStringValue().toLowerCase();
		GrammarRelation[] possibleGrammarForms = wordNet.getPossibleGrammarForms(sv);		
		for(GrammarRelation gr : possibleGrammarForms){
			TextElement word = gr.getWord();
			String bf = word.getBasicForm();
			if(prepConjRegistry.getPreposition(bf)!=null){
				continue;
			}
			if(prepConjRegistry.getConjunction(bf)!=null){
				continue;
			}
			MeaningElement[] concepts = word.getConcepts();
			for(MeaningElement me : concepts){
				
				if(!corresponds(me,gr)){
					continue;
				}
				
				int id = me.id();
				WordFormToken t = map.get(id);
				if(t==null){
					t = new WordFormToken(me, startPosition, endPosition);
					map.put(id, t);
				}
				registerGrammarRelation(gr, t);
			}
		}
		
		list.addAll(Arrays.asList(map.values().toArray(WordFormToken.class)));
		return list;
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
		if(ep<txt.length()&&!str.endsWith(" ")&&txt.charAt(ep)=='.'&&me.getGrammems().contains(SemanGramem.ABBR)){
			return null;
		}
		
		MeaningElement preposition = getPrepConjRegistry().getPreposition(str);
		if(preposition!=null){
			return preposition;
		}
		MeaningElement conjunction = getPrepConjRegistry().getConjunction(str);
		if(conjunction!=null){
			return conjunction;
		}
		return null;
	}

	protected int considerAbbrEndPosition(Stack<IToken> sample,	int endPosition, MeaningElement me, GrammarRelation gr) {
		if(me.getGrammems().contains(SemanGramem.ABBR)||gr.hasGrammem(SemanGramem.ABBR)){
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

	protected PrepConjRegistry getPrepConjRegistry() {
		if(prepConjRegistry==null){
			prepConjRegistry = new PrepConjRegistry(wordNet);
		}
		return prepConjRegistry;
	}

}
