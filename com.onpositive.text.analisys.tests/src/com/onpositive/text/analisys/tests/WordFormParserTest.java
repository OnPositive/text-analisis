package com.onpositive.text.analisys.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.Extras;
import com.onpositive.semantic.wordnet.Grammem.FeaturesGramem;
import com.onpositive.semantic.wordnet.Grammem.Gender;
import com.onpositive.semantic.wordnet.Grammem.Mood;
import com.onpositive.semantic.wordnet.Grammem.Personality;
import com.onpositive.semantic.wordnet.Grammem.SingularPlural;
import com.onpositive.semantic.wordnet.Grammem.Time;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.Euristic;
import com.onpositive.text.analysis.EuristicAnalyzingParser;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.WordFormParser;

public class WordFormParserTest extends TestCase{

	public void testWordFormParser() {
		
//		String str = "Сработал автоматический определитель номера. Чудовище село на ковёр-самолёт и полетело.";		
//		String str = "Сработал электрический детонатор. Чудовище село на ковёр-самолёт и полетело.";
//		GrammarRelation[] possibleGrammarForms = instance.getPossibleGrammarForms("автоматический");
		String str = "меж тем";
		List<IToken> processed = getWordFormTokens(str);
		
		System.out.println("----------------------------------------------------------------------------------------");
		for(IToken t : processed){
			System.out.println(t.getStartPosition() + "-" + t.getEndPosition() + " " + TokenTypeResolver.getResolvedType(t) + " " + t.getStringValue());
		}
		
		List<List<IToken>> possibleChains = calcVariants(processed);
		

		
		
		
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

	public List<IToken> getWordFormTokens(String str) {
		PrimitiveTokenizer pt = new PrimitiveTokenizer();
		AbstractWordNet instance = WordNetProvider.getInstance();
		WordFormParser wfParser = new WordFormParser(instance);
		List<IToken> tokens = pt.tokenize(str);		
		List<IToken> processed = wfParser.process(tokens);
		return processed;
	}
	
	public void testConflicting() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristic1 = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN)
				);
		euristics.add(euristic1);
		Euristic matched = matched(euristics, "налить белил");
		assertNotNull(matched);
	}
	
	public void testAnalyzer() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristic1 = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB)
				);
		euristics.add(euristic1);
		EuristicAnalyzingParser euristicAnalyzingParser = new EuristicAnalyzingParser(euristics);
		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("налить белил"));
		assertNotNull(processed);
		System.out.println("//========================================Результат разбора==================================================");
		for(IToken t : processed){
			System.out.print(t.getStartPosition() + "-" + t.getEndPosition() + " " + TokenTypeResolver.getResolvedType(t) + " " + t.getStringValue()+ " ");
		}
		System.out.println();
	}
	
	public void testAnalyzer1() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		euristics.addAll(getRulesList5());
		euristics.addAll(getRulesList6());
		euristics.addAll(getRulesList7());
		euristics.addAll(getRulesList8());
		euristics.addAll(getRulesList9());
		euristics.addAll(getRulesList10());
		euristics.addAll(getRulesList11());
		euristics.addAll(getRulesList12());
		euristics.addAll(getRulesList13());
		euristics.addAll(getRulesList14());
		euristics.addAll(getRulesList15());
		euristics.addAll(getRulesList16());
		euristics.addAll(getRulesList17());
		euristics.addAll(getRulesList18());
		euristics.addAll(getRulesList19());
		euristics.addAll(getRulesList20());
		euristics.addAll(getRulesList21());
		euristics.addAll(getRulesList22());
		euristics.addAll(getRulesList23());
		euristics.addAll(getRulesList24());
		euristics.addAll(getRulesList25());
		euristics.addAll(getRulesList26());
		euristics.addAll(getRulesList27());
		euristics.addAll(getRulesList28());
		euristics.addAll(getRulesList29());
		EuristicAnalyzingParser euristicAnalyzingParser = new EuristicAnalyzingParser(euristics);
//		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("Он был монтером Ваней, но в духе парижан себе присвоил звание электротехник Жан"));
//		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("Что касается до белил и до сурьмы"));
//		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("Влияет ли обилие белил на сохранность полотен"));
		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("Завтра я еду домой"));
		assertNotNull(processed);
		System.out.println("//========================================Результат разбора==================================================");
		for(IToken t : processed){
			System.out.print(t.getStartPosition() + "-" + t.getEndPosition() + " " + TokenTypeResolver.getResolvedType(t) + " " + t.getStringValue()+ " ");
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
	

	// существительное - предлог
	public void test05() {
		
		Euristic matched = matched(getRulesList5(), "меж двух");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList5() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicPrepWord = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.NOUN),
				Euristic.word("тем", FeaturesGramem.Apro)
				);
		euristics.add(euristicPrepWord);
		Euristic euristicPrepNumR = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.NUMR)
				);
		euristics.add(euristicPrepNumR);
		Euristic euristicPrepPrInst = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.NOUN),
				Euristic.all((PartOfSpeech.NPRO), (Case.ABLT))
				);
		euristics.add(euristicPrepPrInst);
		Euristic euristicPrepPrGen = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.NOUN),
				Euristic.all((PartOfSpeech.NPRO), (Case.GENT))
				);
		euristics.add(euristicPrepPrGen);
		Euristic euristicPrepNounInst = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.NOUN),
				Euristic.all((PartOfSpeech.NOUN), (Case.ABLT))
				);
		euristics.add(euristicPrepNounInst);
		Euristic euristicPrepNounGen = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.NOUN),
				Euristic.all((PartOfSpeech.NOUN), (Case.GENT))
				);
		euristics.add(euristicPrepNounGen);
		return euristics;
	}
	
	// глагол - местоимение
	public void test06() {
		
		Euristic matched = matched(getRulesList6(), "друг мой");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList6() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicNounNpro = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.VERB)
				);
		euristics.add(euristicNounNpro);
		Euristic euristicAdjNpro = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.VERB)
				);
		euristics.add(euristicAdjNpro);
		Euristic euristicPrepNpro = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.VERB)
				);
		euristics.add(euristicPrepNpro);
		Euristic euristicNproVerb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicNproVerb);
		Euristic euristicNproNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicNproNoun);
		Euristic euristicNproAdj = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.ADJF)
				);
		euristics.add(euristicNproAdj);
		return euristics;
	}
	
	// инфинитив - существительное
	
	public void test07() {
		Euristic matched = matched(getRulesList7(), "без вести");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList7() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicPrepNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.INFN)
				);
		euristics.add(euristicPrepNoun);
		Euristic euristicAdjfNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.INFN)
				);
		euristics.add(euristicAdjfNoun);
		Euristic euristicVerbInf = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.createConflictChecker(PartOfSpeech.INFN, PartOfSpeech.NOUN)
				);
		euristics.add(euristicVerbInf);
		Euristic euristicNounInf = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.createConflictChecker(PartOfSpeech.INFN, PartOfSpeech.NOUN)
				);
		euristics.add(euristicNounInf);
		Euristic euristicPredInf = Euristic.concat(
				Euristic.any(PartOfSpeech.PRED),
				Euristic.createConflictChecker(PartOfSpeech.INFN, PartOfSpeech.NOUN)
				);
		euristics.add(euristicPredInf);
		Euristic euristicInfInf = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.createConflictChecker(PartOfSpeech.INFN, PartOfSpeech.NOUN)
				);
		euristics.add(euristicInfInf);
		Euristic euristicInfNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.INFN, PartOfSpeech.NOUN),
				Euristic.all((PartOfSpeech.NOUN), Case.ACCS)
				);
		euristics.add(euristicInfNoun);
		Euristic euristicInfNpro = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.INFN, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.NPRO)
				);
		euristics.add(euristicInfNpro);
		Euristic euristicInfPrep = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.INFN, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicInfPrep);
		return euristics;		
	}
	
	//краткое прилагательное - существительное
	public void test08() {
		Euristic matched = matched(getRulesList8(), "его вещи");
		assertNotNull(matched);
	}
	private List<Euristic> getRulesList8() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicNumrNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.NUMR),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS)
				);
		euristics.add(euristicNumrNoun);
		Euristic euristicPrepNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS)
				);
		euristics.add(euristicPrepNoun);
		Euristic euristicAdjNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS)
				);
		euristics.add(euristicAdjNoun);
		Euristic euristicNproNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS)
				);
		euristics.add(euristicNproNoun);
		Euristic euristicVerbNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS)
				);
		euristics.add(euristicVerbNoun);
		Euristic euristicProNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, Extras.Anph, FeaturesGramem.Apro),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS)
				);
		euristics.add(euristicProNoun);
		Euristic euristicInfNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS)
				);
		euristics.add(euristicInfNoun);
		Euristic euristicGrndNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.GRND),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS)
				);
		euristics.add(euristicGrndNoun);
		Euristic euristicNounPrep = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicNounPrep);
		Euristic euristicNounAdj = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.ADJF)
				);
		euristics.add(euristicNounAdj);
		Euristic euristicNounVerb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicNounVerb);
		Euristic euristicNounNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS),
				Euristic.all(PartOfSpeech.NOUN, Case.DATV)
				);
		euristics.add(euristicNounNoun);
		Euristic euristicNounPred = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.PRED)
				);
		euristics.add(euristicNounPred);
		Euristic euristicNproAdj = Euristic.concat(
				Euristic.any(PartOfSpeech.NPRO),
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.NOUN)
				);
		euristics.add(euristicNproAdj);
		Euristic euristicNounAdjs = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN), 
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.NOUN)
				);
		euristics.add(euristicNounAdjs);
		Euristic euristicAdvbAdjs = Euristic.concat(
				Euristic.any(PartOfSpeech.ADVB),
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.NOUN)
				);
		euristics.add(euristicAdvbAdjs);
		Euristic euristicAdjsNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicAdjsNoun);
		Euristic euristicAdjsVerb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicAdjsVerb);
		Euristic euristicAdjsAdvb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.ADVB)
				);
		euristics.add(euristicAdjsAdvb);
		return euristics;				
	}
	
	// прилательное - существительное
	public void test09() {
		Euristic matched = matched(getRulesList9(), "будь другом");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList9() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicPrepNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF)
				);
		euristics.add(euristicPrepNoun);
		Euristic euristicAdjNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF), 
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF)
				);
		euristics.add(euristicAdjNoun);
		Euristic euristicVerbNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB), 
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF)
				);
		euristics.add(euristicVerbNoun);
		Euristic euristicProNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, Extras.Anph, FeaturesGramem.Apro), 
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF)
				);
		euristics.add(euristicProNoun);
		Euristic euristicNumrNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.NUMR), 
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF)
				);
		euristics.add(euristicNumrNoun);
		Euristic euristicNounNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN), 
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF)
				);
		euristics.add(euristicNounNoun);
		Euristic euristicNounPrep = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF), 
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicNounPrep);
		Euristic euristicNounNounG = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF), 
				Euristic.all(PartOfSpeech.NOUN, Case.GENT)
				);
		euristics.add(euristicNounNounG);
		Euristic euristicNounVerb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF), 
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicNounVerb);
		Euristic euristicPrepAdj = Euristic.concat(
				Euristic.or(Euristic.word("на", PartOfSpeech.PREP), Euristic.word("о", PartOfSpeech.PREP), Euristic.word("в", PartOfSpeech.PREP)), 
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.NOUN)
				);
		euristics.add(euristicPrepAdj);
		Euristic euristicVerbAdj = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB), 
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.NOUN)
				);
		euristics.add(euristicVerbAdj);
		return euristics;
	}
	
	// сравнительная степень прилагательного - существительное
	public void test10() {
		Euristic matched = matched(getRulesList10(), "этой жиже");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList10() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicAdjNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.COMP)
				);
		euristics.add(euristicAdjNoun);
		Euristic euristicPrepNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.COMP)
				);
		euristics.add(euristicPrepNoun);
		Euristic euristicProNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, Extras.Anph, FeaturesGramem.Apro), 
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.COMP)
				);
		euristics.add(euristicProNoun);
		Euristic euristicVerbComp = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.createConflictChecker(PartOfSpeech.COMP, PartOfSpeech.NOUN)
				);
		euristics.add(euristicVerbComp);
		Euristic euristicNounComp = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.createConflictChecker(PartOfSpeech.COMP, PartOfSpeech.NOUN)
				);
		euristics.add(euristicNounComp);
		return euristics;
	}
	
	// деепричастие - прилагательное
	public void test11() {
		GrammarRelation[] ddd = WordNetProvider.getInstance().getPossibleGrammarForms("витая");
		System.out.println(Arrays.toString(ddd));
		Euristic matched = matched(getRulesList11(), "ложечка витая");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList11() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicAdvbGrnd = Euristic.concat(
				Euristic.any(PartOfSpeech.ADVB),
				Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.ADJF)
				);
		euristics.add(euristicAdvbGrnd);
		Euristic euristicGrndPrep = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.ADJF),
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicGrndPrep);
		Euristic euristicGrndAdvb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.ADJF),
				Euristic.any(PartOfSpeech.ADVB)
				);
		euristics.add(euristicGrndAdvb);
		Euristic euristicGrndNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.ADJF),
				Euristic.all(PartOfSpeech.NOUN, Case.ACCS)
				);
		euristics.add(euristicGrndNoun);
		Euristic euristicGrndInf = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.ADJF),
				Euristic.any(PartOfSpeech.INFN)
				);
		euristics.add(euristicGrndInf);
		Euristic euristicNounAdj = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.all(PartOfSpeech.ADJF, Case.NOMN, SingularPlural.SINGULAR, Gender.FEMN)
				);
		euristics.add(euristicNounAdj);
		Euristic euristicAdjAdj = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.all(PartOfSpeech.ADJF, Case.NOMN, SingularPlural.SINGULAR, Gender.FEMN)
				);
		euristics.add(euristicAdjAdj);
		Euristic euristicAdjNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, Case.NOMN, SingularPlural.SINGULAR, Gender.FEMN),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicAdjNoun);
		Euristic euristicAdjAdjf = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, Case.NOMN, SingularPlural.SINGULAR, Gender.FEMN),
				Euristic.any(PartOfSpeech.ADJF)
				);
		euristics.add(euristicAdjAdjf);
		return euristics;
	}
	
	// глагол - прилагательное
	public void test12() {
		Euristic matched = matched(getRulesList12(), "на праздную");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList12() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicNproVerb = Euristic.concat(
				Euristic.all(PartOfSpeech.NPRO, Personality.PERS1),
				Euristic.all(PartOfSpeech.VERB, Personality.PERS1, SingularPlural.SINGULAR, Time.PRESENT)
				);
		euristics.add(euristicNproVerb);
		Euristic euristicAdvbVerb = Euristic.concat(
				Euristic.any(PartOfSpeech.ADVB),
				Euristic.all(PartOfSpeech.VERB, Personality.PERS1, SingularPlural.SINGULAR, Time.PRESENT)
				);
		euristics.add(euristicAdvbVerb);
		Euristic euristicPrepAdjf = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.VERB)
				);
		euristics.add(euristicPrepAdjf);
		Euristic euristicNounAdjf = Euristic.concat(
				Euristic.all(PartOfSpeech.NOUN, Case.ACCS),
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.VERB)
				);
		euristics.add(euristicNounAdjf);
		Euristic euristicNproAdjf = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro),
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.VERB)
				);
		euristics.add(euristicNproAdjf);
		Euristic euristicVerbAdjf = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.VERB)
				);
		euristics.add(euristicVerbAdjf);
		Euristic euristicInfAdjf = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.VERB)
				);
		euristics.add(euristicInfAdjf);
		Euristic euristicAdjAdjf = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.VERB)
				);
		euristics.add(euristicAdjAdjf);
		Euristic euristicAdjfNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.VERB),
				Euristic.all(PartOfSpeech.NOUN, Case.ACCS)
				);
		euristics.add(euristicAdjfNoun);
		return euristics;
	}
	
	// глагол - краткое прилагательное
	public void test13() {
		Euristic matched = matched(getRulesList13(), "плавно росло");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList13() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicAdvbVerb = Euristic.concat(
				Euristic.any(PartOfSpeech.ADVB),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.ADJS)
				);
		euristics.add(euristicAdvbVerb);
		Euristic euristicVerbAdvb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.ADVB)
				);
		euristics.add(euristicVerbAdvb);
		Euristic euristicVerbPrep = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicVerbPrep);
		return euristics;
	}
	
	// глагол - наречие
	
	public void test14() {
		Euristic matched = matched(getRulesList14(), "показал издали");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList14() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicNounVerb = Euristic.concat(
				Euristic.all(PartOfSpeech.NOUN, Case.ACCS),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.ADVB)
				);
		euristics.add(euristicNounVerb);
		Euristic euristicVerbNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.ADVB),
				Euristic.all(PartOfSpeech.NOUN, Case.ACCS)
				);
		euristics.add(euristicVerbNoun);
		Euristic euristicVerbPrep = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.ADVB),
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicVerbPrep);
		Euristic euristicVerbAdvb = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.VERB)
				);
		euristics.add(euristicVerbAdvb);
		Euristic euristicGrndAdvb = Euristic.concat(
				Euristic.any(PartOfSpeech.GRND),
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.VERB)
				);
		euristics.add(euristicGrndAdvb);
		Euristic euristicAdvbVerb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicAdvbVerb);
		Euristic euristicAdvbGrnd = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.GRND)
				);
		euristics.add(euristicAdvbGrnd);
		return euristics;
	}
	
	// глагол - союз
	
	public void test15() {
		Euristic matched = matched(getRulesList15(), "коли уж");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList15() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicNproVerb = Euristic.concat(
				Euristic.all(PartOfSpeech.NPRO, Case.ACCS),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.CONJ)
				);
		euristics.add(euristicNproVerb);
		Euristic euristicNounVerb = Euristic.concat(
				Euristic.all(PartOfSpeech.NOUN, Case.ACCS),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.CONJ)
				);
		euristics.add(euristicNounVerb);
		Euristic euristicVerbNpro = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.CONJ),
				Euristic.all(PartOfSpeech.NPRO, Case.ACCS)
				);
		euristics.add(euristicVerbNpro);
		Euristic euristicVerbNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.CONJ),
				Euristic.all(PartOfSpeech.NOUN, Case.ACCS)
				);
		euristics.add(euristicVerbNoun);
		Euristic euristicConjWord = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.CONJ, PartOfSpeech.VERB),
				Euristic.word("уж", PartOfSpeech.PRCL)
				);
		euristics.add(euristicConjWord);
		Euristic euristicConjNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.CONJ, PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicConjNoun);
		Euristic euristicConjVerb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.CONJ, PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicConjVerb);
		return euristics;
	}
	
	// существительное - наречие
	public void test16() {
		Euristic matched = matched(getRulesList16(), "забывая порой");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList16() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicAdjfNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADVB)
				);
		euristics.add(euristicAdjfNoun);
		Euristic euristicPrepNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADVB)
				);
		euristics.add(euristicPrepNoun);
		Euristic euristicNounNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADVB),
				Euristic.all(PartOfSpeech.NOUN, Case.GENT)
				);
		euristics.add(euristicNounNoun);
		Euristic euristicVerbAdvb = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.NOUN)
				);
		euristics.add(euristicVerbAdvb);
		Euristic euristicNounAdvb = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.NOUN)
				);
		euristics.add(euristicNounAdvb);
		Euristic euristicNproAdvb = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro),
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.NOUN)
				);
		euristics.add(euristicNproAdvb);
		Euristic euristicGrndAdvb = Euristic.concat(
				Euristic.any(PartOfSpeech.GRND),
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.NOUN)
				);
		euristics.add(euristicGrndAdvb);
		Euristic euristicAdvbNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicAdvbNoun);
		Euristic euristicAdvbAdvb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.ADVB)
				);
		euristics.add(euristicAdvbAdvb);
		Euristic euristicAdvbPrep = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicAdvbPrep);
		return euristics;
	}
	
	// предикат - предлог
	
	public void test17() {
		Euristic matched = matched(getRulesList17(), "надо мною");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList17() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicInfnPred = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.createConflictChecker(PartOfSpeech.PRED, PartOfSpeech.PREP)
				);
		euristics.add(euristicInfnPred);
		Euristic euristicWordPred = Euristic.concat(
				Euristic.word("ещё", PartOfSpeech.ADVB),
				Euristic.createConflictChecker(PartOfSpeech.PRED, PartOfSpeech.PREP)
				);
		euristics.add(euristicWordPred);
		Euristic euristicPredWord = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PRED, PartOfSpeech.PREP),
				Euristic.word("бы", PartOfSpeech.PRCL)
				);
		euristics.add(euristicPredWord);
		Euristic euristicPredAdvb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PRED, PartOfSpeech.PREP),
				Euristic.any(PartOfSpeech.ADVB)
				);
		euristics.add(euristicPredAdvb);
		Euristic euristicPredInfn = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PRED, PartOfSpeech.PREP),
				Euristic.any(PartOfSpeech.INFN)
				);
		euristics.add(euristicPredInfn);
		Euristic euristicPredZhe = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PRED, PartOfSpeech.PREP),
				Euristic.word("же", PartOfSpeech.PRCL)
				);
		euristics.add(euristicPredZhe);
		Euristic euristicPredNpro = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PRED, PartOfSpeech.PREP),
				Euristic.all(PartOfSpeech.NPRO, Case.DATV)
				);
		euristics.add(euristicPredNpro);
		Euristic euristicPredNounD = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PRED, PartOfSpeech.PREP),
				Euristic.all(PartOfSpeech.NOUN, Case.DATV)
				);
		euristics.add(euristicPredNounD);
		Euristic euristicPredEshe = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PRED, PartOfSpeech.PREP),
				Euristic.word("ещё", PartOfSpeech.PRCL)
				);
		euristics.add(euristicPredEshe);
		Euristic euristicPredNounA = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PRED, PartOfSpeech.PREP),
				Euristic.all(PartOfSpeech.NOUN, Case.ACCS)
				);
		euristics.add(euristicPredNounA);
		Euristic euristicPrepNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.PRED),
				Euristic.all(PartOfSpeech.NOUN, Case.ABLT)
				);
		euristics.add(euristicPrepNoun);
		Euristic euristicPrepNpro = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.PRED),
				Euristic.all(PartOfSpeech.NPRO, Case.ABLT)
				);
		euristics.add(euristicPrepNpro);
		return euristics;
	}
	
	// существительное - числительное "семью"
	public void test18() {
		Euristic matched = matched(getRulesList18(), "создаст семью");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList18() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicNproNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro, Case.ACCS),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.NUMR)
				);
		euristics.add(euristicNproNoun);
		Euristic euristicAdjfNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, Case.ACCS),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.NUMR)
				);
		euristics.add(euristicAdjfNoun);
		Euristic euristicInfnNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.NUMR)
				);
		euristics.add(euristicInfnNoun);
		Euristic euristicVerbNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.NUMR)
				);
		euristics.add(euristicVerbNoun);
		Euristic euristicNounPrep = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.NUMR),
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicNounPrep);
		Euristic euristicNumrAdjf = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.NOUN),
				Euristic.all(PartOfSpeech.ADJF, Case.ABLT)
				);
		euristics.add(euristicNumrAdjf);
		Euristic euristicNumrNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.NOUN),
				Euristic.all(PartOfSpeech.NOUN, Case.ABLT)
				);
		euristics.add(euristicNumrNoun);
		Euristic euristicNumrNumr = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.NUMR)
				);
		euristics.add(euristicNumrNumr);
		return euristics;
	}
	
	// существительное - числительное "сорока"
	public void test19() {
		Euristic matched = matched(getRulesList19(), "сорока лет");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList19() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicNounNumr = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.NOUN)
				);
		euristics.add(euristicNounNumr);
		Euristic euristicNumrNumr = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.NOUN),
				Euristic.all(PartOfSpeech.NUMR, Case.GENT)
				);
		euristics.add(euristicNumrNumr);
		Euristic euristicNumrNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicNumrNoun);
		return euristics;
	}
	
	// существительное - числительное "сот"
//	public void test20() {
//		Euristic matched = matched(getRulesList20(), "из сот");
//		assertNotNull(matched);
//	}
	
	private List<Euristic> getRulesList20() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicNounNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.NUMR)
				);
		euristics.add(euristicNounNoun);
		Euristic euristicPrepNumr = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.NUMR)
				);
		euristics.add(euristicPrepNumr);
		Euristic euristicAdjfNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.NUMR)
				);
		euristics.add(euristicAdjfNoun);
		Euristic euristicNumrNoun = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Anum)),
				Euristic.all(PartOfSpeech.NOUN, Case.GENT)
				);
		euristics.add(euristicNumrNoun);
		Euristic euristicNumrNumr = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.NUMR)
				);
		euristics.add(euristicNumrNumr);
		return euristics;
	}

// существительное - числительное "шестом"
	public void test21() {
		Euristic matched = matched(getRulesList21(), "в шестом");
		assertNotNull(matched);
	}
			
	private List<Euristic> getRulesList21() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicPrepNoun = Euristic.concat(
			Euristic.any(PartOfSpeech.PREP),
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Anum))
		);
		euristics.add(euristicPrepNoun);
		Euristic euristicVerbNoun = Euristic.concat(
			Euristic.any(PartOfSpeech.VERB),
			Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.ADJF), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Anum))
		);
		euristics.add(euristicVerbNoun);
		Euristic euristicNounNpro = Euristic.concat(
			Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.ADJF), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Anum)),
			Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro, Case.ABLT)
		);
		euristics.add(euristicNounNpro);
		Euristic euristicNumrNumr = Euristic.concat(
			Euristic.any(PartOfSpeech.NUMR),
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Anum))
		);
		euristics.add(euristicNumrNumr);
		return euristics;
	}
	
	// глагол - числительное "три"
	public void test22() {
		Euristic matched = matched(getRulesList22(), "три года");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList22() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicNounNumr = Euristic.concat(
				Euristic.all(PartOfSpeech.NOUN, Case.GENT),
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.VERB)
				);
		euristics.add(euristicNounNumr);
		Euristic euristicNumrNumr = Euristic.concat(
				Euristic.any(PartOfSpeech.NUMR),
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.VERB)
				);
		euristics.add(euristicNumrNumr);
		Euristic euristicVerbNumr = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.VERB)
				);
		euristics.add(euristicVerbNumr);
		Euristic euristicPrepNumr = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.VERB)
				);
		euristics.add(euristicPrepNumr);
		Euristic euristicAdjfNumr = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.VERB)
				);
		euristics.add(euristicAdjfNumr);
		Euristic euristicInfnNumr = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.VERB)
				);
		euristics.add(euristicInfnNumr);
		Euristic euristicNumrNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.VERB),
				Euristic.all(PartOfSpeech.NOUN, Case.GENT)
				);
		euristics.add(euristicNumrNoun);
		Euristic euristicNumrAdjf = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.VERB),
				Euristic.all(PartOfSpeech.ADJF, Case.GENT)
				);
		euristics.add(euristicNumrAdjf);
		return euristics;
	}
	
	// прилагательное - наречие
	public void test23() {
		Euristic matched = matched(getRulesList23(), "съест чуток");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList23() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicVerbAdvb = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.ADJS)
				);
		euristics.add(euristicVerbAdvb);
		Euristic euristicGrndAdvb = Euristic.concat(
				Euristic.any(PartOfSpeech.GRND),
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.ADJS)
				);
		euristics.add(euristicGrndAdvb);
		Euristic euristicAdvbPrep = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicAdvbPrep);
		Euristic euristicAdvbInfn = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.INFN)
				);
		euristics.add(euristicAdvbInfn);
		Euristic euristicAdvbVerb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicAdvbVerb);
		Euristic euristicAdvbComp = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.COMP)
				);
		euristics.add(euristicAdvbComp);
		return euristics;
	}
	
	// существительное - деепричастие
	public void test24() {
		Euristic matched = matched(getRulesList24(), "воя надрывно");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList24() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicAdjfNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.GRND)
				);
		euristics.add(euristicAdjfNoun);
		Euristic euristicPrepNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.GRND)
				);
		euristics.add(euristicPrepNoun);
		Euristic euristicNounNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.GRND)
				);
		euristics.add(euristicNounNoun);
		Euristic euristicNounNoun2 = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.GRND),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicNounNoun2);
		Euristic euristicAdvbGrnd = Euristic.concat(
				Euristic.any(PartOfSpeech.ADVB),
				Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.NOUN)
				);
		euristics.add(euristicAdvbGrnd);
		Euristic euristicGrndAdvb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.ADVB)
				);
		euristics.add(euristicGrndAdvb);
		Euristic euristicGrndPrep = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicGrndPrep);
		return euristics;
	}
				
	// существительное - глагол
	public void test25() {
		Euristic matched = matched(getRulesList25(), "обилие белил");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList25() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicPrepNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB)
				);
		euristics.add(euristicPrepNoun);
		Euristic euristicAdjfNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB)
				);
		euristics.add(euristicAdjfNoun);
		Euristic euristicPrtfNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.PRTF),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB)
				);
		euristics.add(euristicPrtfNoun);
		Euristic euristicInfnNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB)
				);
		euristics.add(euristicInfnNoun);
		Euristic euristicVerbNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB)
				);
		euristics.add(euristicVerbNoun);
		Euristic euristicNproNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB)
				);
		euristics.add(euristicNproNoun);
		Euristic euristicNumrNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.NUMR),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB)
				);
		euristics.add(euristicNumrNoun);
		Euristic euristicImprNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.VERB, Mood.impr),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB)
				);
		euristics.add(euristicImprNoun);
		Euristic euristicNounNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB)
				);
		euristics.add(euristicNounNoun);
		Euristic euristicNounVerb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicNounVerb);
		Euristic euristicNounNounG = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB),
				Euristic.all(PartOfSpeech.NOUN, Case.GENT)
				);
		euristics.add(euristicNounNounG);
		Euristic euristicAdvbVerb = Euristic.concat(
				Euristic.any(PartOfSpeech.ADVB),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN)
				);
		euristics.add(euristicAdvbVerb);
		Euristic euristicNproVerb = Euristic.concat(
				Euristic.any(PartOfSpeech.NPRO),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN)
				);
		euristics.add(euristicNproVerb);
		Euristic euristicWordVerb = Euristic.concat(
				Euristic.word("давай", PartOfSpeech.VERB),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN)
				);
		euristics.add(euristicWordVerb);
		Euristic euristicVerbNounT = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN),
				Euristic.all(PartOfSpeech.NOUN, Case.ABLT)
				);
		euristics.add(euristicVerbNounT);
		Euristic euristicVerbNounV = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN),
				Euristic.all(PartOfSpeech.NOUN, Case.ACCS)
				);
		euristics.add(euristicVerbNounV);
		Euristic euristicVerbPrep = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicVerbPrep);
		Euristic euristicVerbAdvb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.ADVB)
				);
		euristics.add(euristicVerbAdvb);
		Euristic euristicVerbNounD = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN),
				Euristic.all(PartOfSpeech.NOUN, Case.DATV)
				);
		euristics.add(euristicVerbNounD);
		Euristic euristicVerbNproD = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN),
				Euristic.all(PartOfSpeech.NPRO, Case.DATV)
				);
		euristics.add(euristicVerbNproD);
		Euristic euristicVerbNproV = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN),
				Euristic.all(PartOfSpeech.NPRO, Case.ACCS)
				);
		euristics.add(euristicVerbNproV);
		Euristic euristicVerbInfn = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.INFN)
				);
		euristics.add(euristicVerbInfn);
		return euristics;
	}
	
	// существительное - местоимение "кому"
	public void test26() {
		Euristic matched = matched(getRulesList26(), "к кому");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList26() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicPrepNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.NPRO)
				);
		euristics.add(euristicPrepNoun);
		Euristic euristicPrepNpro = Euristic.concat(
				Euristic.word("к", PartOfSpeech.PREP),
				Euristic.createConflictChecker(PartOfSpeech.NPRO, PartOfSpeech.NOUN)
				);
		euristics.add(euristicPrepNpro);
		Euristic euristicVerbNpro = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.createConflictChecker(PartOfSpeech.NPRO, PartOfSpeech.NOUN)
				);
		euristics.add(euristicVerbNpro);
		return euristics;
	}
	
	// существительное - местоимение "тем"
	public void test27() {
		Euristic matched = matched(getRulesList27(), "тем самым");
		assertNotNull(matched);
	}
			
	private List<Euristic> getRulesList27() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicAdjfNpro = Euristic.concat(
			Euristic.any(PartOfSpeech.ADJF),
			Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.ADJF), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro))
		);
		euristics.add(euristicAdjfNpro);
		Euristic euristicPrepNpro = Euristic.concat(
			Euristic.any(PartOfSpeech.PREP),
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro))
		);
		euristics.add(euristicPrepNpro);
		Euristic euristicNproSam = Euristic.concat(
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro)),
			Euristic.word("самым", PartOfSpeech.ADJF)
		);
		euristics.add(euristicNproSam);
		Euristic euristicNproNeMen = Euristic.concat(
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro)),
			Euristic.word("не менее", PartOfSpeech.ADVB)
		);
		euristics.add(euristicNproNeMen);
		Euristic euristicNproBol = Euristic.concat(
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro)),
			Euristic.word("более", PartOfSpeech.ADVB)
		);
		euristics.add(euristicNproBol);
		return euristics;
	}

	// существительное - местоимение "том"
	public void test28() {
		Euristic matched = matched(getRulesList28(), "о том");
		assertNotNull(matched);
	}
			
	private List<Euristic> getRulesList28() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicPrepNpro = Euristic.concat(
			Euristic.any(PartOfSpeech.PREP),
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro))
		);
		euristics.add(euristicPrepNpro);
		return euristics;
	}
	
	// существительное - местоимение "тому"
	public void test29() {
		Euristic matched = matched(getRulesList29(), "тому подобное");
		assertNotNull(matched);
	}
			
	private List<Euristic> getRulesList29() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicPrepNpro = Euristic.concat(
			Euristic.any(PartOfSpeech.PREP),
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro))
		);
		euristics.add(euristicPrepNpro);
		Euristic euristicNproPodob = Euristic.concat(
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro)),
			Euristic.word("подобный", PartOfSpeech.ADJF)
		);
		euristics.add(euristicNproPodob);
		Euristic euristicNproNazad = Euristic.concat(
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro)),
			Euristic.word("назад", PartOfSpeech.ADVB)
		);
			euristics.add(euristicNproNazad);
		return euristics;
	}
	
	public void test30() {
		Euristic matched = matched(getRulesList25(), "обилие белил");
		assertNotNull(matched);
	}
	
	public void test31() {
		Euristic matched = matched(getRulesList14(), "еду домой");
		assertNotNull(matched);
	}
		
	private Euristic matched(List<Euristic> euristicsToTry, String testString) {
		PrimitiveTokenizer pt = new PrimitiveTokenizer();
		WordFormParser wordFormParser = new WordFormParser(WordNetProvider.getInstance());
		List<IToken> tokens = pt.tokenize(testString);		
		List<IToken> processed = wordFormParser.process(tokens);
		List<List<IToken>> possibleChains = calcVariants(processed);
		System.out.println("-------------------------------------------------------------------------------------------------------");
		if (possibleChains.size() > 1) {
			System.out.println("Строка '" + testString + "' Варианты разбора:");
		}
		for (List<IToken> list : possibleChains) {
			for(IToken t : list){
				System.out.print(t.getStartPosition() + "-" + t.getEndPosition() + " " + TokenTypeResolver.getResolvedType(t) + " " + t.getStringValue()+ " ");
			}
			System.out.println();
		}
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
