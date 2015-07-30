package com.onpositive.text.analisys.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analisys.Euristic;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.WordFormParser;

public class WordFormParserTest extends TestCase{

	public void testWordFormParser() {
		
		PrimitiveTokenizer pt = new PrimitiveTokenizer();
		AbstractWordNet instance = WordNetProvider.getInstance();
		GrammarRelation[] possibleGrammarForms = instance.getPossibleGrammarForms("автоматический");
		
		TextElement[] possibleContinuations = instance.getPossibleContinuations(possibleGrammarForms[0].getWord());
		//ww.prepareWordSeqs();
		WordFormParser wfParser = new WordFormParser(instance);
		
//		String str = "Сработал автоматический определитель номера. Чудовище село на ковёр-самолёт и полетело.";		
//		String str = "Сработал электрический детонатор. Чудовище село на ковёр-самолёт и полетело.";
		String str = "ты дрожи";
		List<IToken> tokens = pt.tokenize(str);		
		List<IToken> processed = wfParser.process(tokens);
		
		for(IToken t : processed){
			System.out.println(t.getStartPosition() + "-" + t.getEndPosition() + " " + TokenTypeResolver.getResolvedType(t) + " " + t.getStringValue());
		}
		
		List<List<IToken>> possibleChains = calcVariants(processed);
		
		Euristic.register(
				WordFormParser.class,
				
				Euristic.any(PartOfSpeech.NOUN, PartOfSpeech.NPRO),
				Euristic.any(PartOfSpeech.VERB)
		);
		Euristic.register(
				WordFormParser.class,
				
				Euristic.word("части", PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.or(Euristic.any(PartOfSpeech.ADJF), Euristic.any(PartOfSpeech.ADJS))
		);
		Euristic.register(
				WordFormParser.class,
				
				Euristic.any(PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.any(PartOfSpeech.NOUN)
		);
		Euristic.register(
				WordFormParser.class,
				
				Euristic.any(PartOfSpeech.PREP),
				Euristic.any(PartOfSpeech.NOUN)
		);
		//Правило инфинитив + существительное
		Euristic.register(
				WordFormParser.class,
				
				Euristic.any(PartOfSpeech.INFN),
				Euristic.any(PartOfSpeech.NOUN)
		);
		Euristic.register(
				WordFormParser.class,
				
				Euristic.any(PartOfSpeech.PREP),
				Euristic.any(PartOfSpeech.NOUN)
				);
		Euristic.register(
				WordFormParser.class,
				
				Euristic.or(Euristic.any(PartOfSpeech.ADJF), Euristic.any(PartOfSpeech.PRTF)),
				Euristic.any(PartOfSpeech.NOUN)
				);
		
		Euristic.register(
				WordFormParser.class,
				
				Euristic.any(PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.NOUN)
				);
		Euristic.register(
				WordFormParser.class,
				
				Euristic.any(PartOfSpeech.NUMR),
				Euristic.any(PartOfSpeech.NOUN)
				);
		Euristic.register(
				WordFormParser.class,
				
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.VERB)
				);
		Euristic.register(
				WordFormParser.class,
				
				Euristic.any(PartOfSpeech.NPRO),
				Euristic.any(PartOfSpeech.VERB)
				);
		Euristic.register(
				WordFormParser.class,
				
				Euristic.any(PartOfSpeech.ADVB),
				Euristic.any(PartOfSpeech.VERB)
				);
		Euristic.register(
				WordFormParser.class,
				
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.NOUN)
				);
		Euristic.register(
				WordFormParser.class,
				
				Euristic.any(PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.PREP)
				);
		Euristic.register(
				WordFormParser.class,
				
				Euristic.any(PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.INFN)
				);
		
		
		
		
		System.out.println("----------------------------------------------------------------------------------------");
		List<Euristic> euristics = Euristic.match(WordFormParser.class);
		for (Euristic euristic : euristics) {
			for (List<IToken> list : possibleChains) {
				boolean match = euristic.match(list.toArray(new IToken[0]));
				if (match) {
					System.out.println(list);
				}
			}
		}
		
		System.out.println();
	}
	
	public void test00() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		euristics.add(Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.VERB)
				));
		euristics.add(Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.INFN)
				));
		Euristic matched = matched(euristics, "грузовик поехал");
		assertNotNull(matched);
	}
	
	public void test01() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicNounVerb = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.VERB)
				);
		Euristic euristicVerbInfn = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.INFN)
				);
		
		euristics.add(euristicNounVerb);
		euristics.add(euristicVerbInfn);
		Euristic matched = matched(euristics, "грузовик поехал");
		assertEquals(matched, euristicNounVerb);
	}
	
	public void test02() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		euristics.add(Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.INFN)
				));
		
		Euristic matched = matched(euristics, "грузовик поехал");
		assertNull(matched);
	}
	
	private Euristic matched(List<Euristic> euristicsToTry, String testString) {
		PrimitiveTokenizer pt = new PrimitiveTokenizer();
		WordFormParser wordFormParser = new WordFormParser(WordNetProvider.getInstance());
		List<IToken> tokens = pt.tokenize(testString);		
		List<IToken> processed = wordFormParser.process(tokens);
		
//		for(IToken t : processed){
//			System.out.println(t.getStartPosition() + "-" + t.getEndPosition() + " " + TokenTypeResolver.getResolvedType(t) + " " + t.getStringValue());
//		}
		
		List<List<IToken>> possibleChains = calcVariants(processed);
		return matched(euristicsToTry, possibleChains);
	}
	
	private Euristic matched(List<Euristic> euristicsToTry, List<List<IToken>> possibleChains) {
		for (Euristic euristic : euristicsToTry) {
			for (List<IToken> list : possibleChains) {
				boolean match = euristic.match(list.toArray(new IToken[0]));
				if (match) {
					return euristic;
				}
			}
		}
		return null;
	}

	private List<List<IToken>> calcVariants(List<IToken> processed) {
		List<IToken> workingCopy = new ArrayList<IToken>(processed);
		List<List<IToken>> result = new ArrayList<List<IToken>>();
		result.add(new ArrayList<IToken>());
		for (int i = 0; i < workingCopy.size(); i++) {
			if (workingCopy.get(i).getConflicts() == null || workingCopy.get(i).getConflicts().isEmpty()) {
				addItem(result, workingCopy.get(i));
			} else {
				List<IToken> conflicts = new ArrayList<IToken>(workingCopy.get(i).getConflicts());
				conflicts.add(0, workingCopy.get(i));
				while (i+1 < workingCopy.size() && conflicts.contains(workingCopy.get(i+1))) {
					workingCopy.remove(i+1);
				}
				result = generateVariants(result,conflicts);
			}
		}
		return result;
	}

	private List<List<IToken>> generateVariants(List<List<IToken>> prevResult, List<IToken> conflicts) {
		List<List<IToken>> result = new ArrayList<List<IToken>>();
		for (List<IToken> list : prevResult) {
			for (IToken curToken : conflicts) {
				List<IToken> newList = new ArrayList<IToken>(list);
				newList.add(curToken);
				result.add(newList);
			}
		}
		return result;
	}

	private void addItem(List<List<IToken>> result, IToken token) {
		for (List<IToken> list : result) {
			list.add(token);
		}
	}
	
}