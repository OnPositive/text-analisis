package com.onpositive.text.analisys.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.onpositive.text.analysis.filtering.AbbreviationsFilter;
import com.onpositive.text.analysis.lexic.PrimitiveTokenizer;
import com.onpositive.text.analysis.lexic.WordFormParser;
import com.onpositive.text.analysis.projection.creators.NotPartRemover;

public class WordFormParserTest extends TestCase{

	private static final double E = 0.0001;

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
		EuristicAnalyzingParser euristicAnalyzingParser = configureDefaultAnalyzer(euristics);
		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("отдала мою"));
		assertNotNull(processed);
		System.out.println("//========================================Результат разбора==================================================");
		for(IToken t : processed){
			System.out.print(t.getStartPosition() + "-" + t.getEndPosition() + " " + TokenTypeResolver.getResolvedType(t) + " " + t.getStringValue()+ " ");
		}
		System.out.println();
	}
	
	private EuristicAnalyzingParser configureDefaultAnalyzer(List<Euristic> euristics) {
		EuristicAnalyzingParser euristicAnalyzingParser = new EuristicAnalyzingParser(euristics);
		euristicAnalyzingParser.addTokenFilter(new AbbreviationsFilter());
		return euristicAnalyzingParser;
	}
	
	public void testAnalyzer1() {
//		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("Он был монтером Ваней, но в духе парижан себе присвоил звание электротехник Жан"));
//		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("Влияет ли обилие белил на сохранность полотен")); сет №25, правило №9
//		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("отдала мою душу")); сет №6, правило №5
//		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("закинул он блесну")); - "блесну" - глагол, сет №25, правило №6
//		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("при головной боли")); - не разбирается, сет №25, правило №2
//		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("на лице гримаса боли")); - не разбирается, сет №25, правило №9
//		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("они обследовали все заводи острова")); - не разбирается, сет №25, правило №6
//		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("необходимость вести борьбу")); "вести борьбу" воспринимает как одно целое, сет №7, правило №7
//		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("ребенок должен учиться вести себя прилично")); - не разбирается, сет №7, правило №6
//		String str = "отдала мою душу";
		String str = "шестьдесят минут прошло";
//		String str = "в обеденный перерыв он приходил домой";
		doBasicAnalyzerTest(str, getFullRulesList());			
	}
	
	public void testAnalyzer2() {
		String str ="Что так поздно пришёл";
		List<Euristic> euristics = getFullRulesList();
		euristics.addAll(getRulesList30());
		euristics.addAll(getRulesList31());
		euristics.addAll(getRulesList32());
		euristics.addAll(getRulesList33());	
		euristics.addAll(getRulesList34());
		euristics.addAll(getRulesList35());
		EuristicAnalyzingParser euristicAnalyzingParser = configureDefaultAnalyzer(euristics);
		euristicAnalyzingParser.process(getWordFormTokens(str));
		List<List<IToken>> possibleChains = euristicAnalyzingParser.getPossibleChains();
		printProcessingResult(str, possibleChains);		
	}
	
	public void testAnalyzer3() {
		String str = "употреблять белила аккуратно и экономно";
		doBasicAnalyzerTest(str, getFullRulesList());		
	}

	public void testAnalyzer4() {
		doBasicAnalyzerTest("Не умеешь петь - не пей", getFullRulesList());				
	}

	private void doBasicAnalyzerTest(String str, List<Euristic> euristics) {
		EuristicAnalyzingParser euristicAnalyzingParser = configureDefaultAnalyzer(euristics);
		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens(str));
		printChain(str, processed);
		matched(getFullRulesList(), str);
	}

	private List<Euristic> getFullRulesList() {
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
		return euristics;
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
		Euristic matched = matched(getRulesList5(), "перед яблока");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList5() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicPrepWordConj = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.NOUN),
				Euristic.word("тем", PartOfSpeech.ADJF),
				Euristic.word("как", PartOfSpeech.CONJ)
				);
		euristics.add(euristicPrepWordConj);
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
		Euristic euristicPrepAdjf = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.NOUN),
				Euristic.all((PartOfSpeech.ADJF), (FeaturesGramem.Anum))
				);
		euristics.add(euristicPrepAdjf);
		Euristic euristicPrepNounInst = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.NOUN),
				Euristic.all((PartOfSpeech.NOUN), (Case.ABLT))
				);
		euristics.add(euristicPrepNounInst);
		Euristic euristicPrepNpro = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.NOUN),
				Euristic.all((PartOfSpeech.NPRO), (Extras.Anph))
				);
		euristics.add(euristicPrepNpro);
		Euristic euristicPrepAdjf1 = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.NOUN),
				Euristic.all(PartOfSpeech.ADJF, Case.ABLT)
				);
		euristics.add(euristicPrepAdjf1);
		Euristic euristicNounNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.PREP),
				Euristic.all(PartOfSpeech.NOUN, Case.GENT)
				);
		euristics.add(euristicNounNoun);
		return euristics;
	}
	
	// глагол - местоимение
	public void test06() {
		
		Euristic matched = matched(getRulesList6(), "тщательно мой");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList6() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicNounNpro = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.VERB), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro))
				);
		euristics.add(euristicNounNpro);
		Euristic euristicAdjNpro = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.VERB), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro))
				);
		euristics.add(euristicAdjNpro);
		Euristic euristicPrepNpro = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.VERB), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro))
				);
		euristics.add(euristicPrepNpro);
		Euristic euristicVerbNpro = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.VERB), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro))
				);
		euristics.add(euristicVerbNpro);
		Euristic euristicNproNoun = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.VERB), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro)),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicNproNoun);
		Euristic euristicNproAdj = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.VERB), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro)),
				Euristic.any(PartOfSpeech.ADJF)
				);
		euristics.add(euristicNproAdj);		
		Euristic euristicNotNounNpro = Euristic.concat(
				Euristic.not(Euristic.any(PartOfSpeech.NOUN)),
				Euristic.and(Euristic.conflicting(PartOfSpeech.VERB, PartOfSpeech.ADJF), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro))
				);
		euristics.add(euristicNotNounNpro);
		Euristic euristicNotAdjNpro = Euristic.concat(
				Euristic.not(Euristic.any(PartOfSpeech.ADJF)),
				Euristic.and(Euristic.conflicting(PartOfSpeech.VERB, PartOfSpeech.ADJF), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro))
				);
		euristics.add(euristicNotAdjNpro);
		Euristic euristicNotPrepNpro = Euristic.concat(
				Euristic.not(Euristic.any(PartOfSpeech.PREP)),
				Euristic.and(Euristic.conflicting(PartOfSpeech.VERB, PartOfSpeech.ADJF), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro))
				);
		euristics.add(euristicNotPrepNpro);
		Euristic euristicNotVerbNpro = Euristic.concat(
				Euristic.not(Euristic.any(PartOfSpeech.VERB)),
				Euristic.and(Euristic.conflicting(PartOfSpeech.VERB, PartOfSpeech.ADJF), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro))
				);
		euristics.add(euristicNotVerbNpro);
		Euristic euristicNotNproNoun = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.VERB, PartOfSpeech.ADJF), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro)),
				Euristic.not(Euristic.any(PartOfSpeech.NOUN))
				);
		euristics.add(euristicNotNproNoun);
		Euristic euristicNotNproAdj = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.VERB, PartOfSpeech.ADJF), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro)),
				Euristic.not(Euristic.any(PartOfSpeech.ADJF))
				);
		euristics.add(euristicNotNproAdj);
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
		Euristic euristicAdjsNounM = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJS, Gender.MASC),
				Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.ADJS), Euristic.all(PartOfSpeech.NOUN, Gender.MASC))
				);
		euristics.add(euristicAdjsNounM);
		Euristic euristicNounMAdjs = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.ADJS), Euristic.all(PartOfSpeech.NOUN, Gender.MASC)),
				Euristic.all(PartOfSpeech.ADJS, Gender.MASC)
				);
		euristics.add(euristicNounMAdjs);
		Euristic euristicNounFAdjs = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.ADJS), Euristic.all(PartOfSpeech.NOUN, Gender.FEMN)),
				Euristic.all(PartOfSpeech.ADJS, Gender.FEMN)
				);
		euristics.add(euristicNounFAdjs);
				Euristic euristicAdjsNounF = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJS, Gender.FEMN),
				Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.ADJS), Euristic.all(PartOfSpeech.NOUN, Gender.FEMN))
				);
		euristics.add(euristicAdjsNounF);
		Euristic euristicAdjsNounN = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJS, Gender.NEUT),
				Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.ADJS), Euristic.all(PartOfSpeech.NOUN, Gender.NEUT))
				);
		euristics.add(euristicAdjsNounN);
		Euristic euristicNounNAdjs = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.ADJS), Euristic.all(PartOfSpeech.NOUN, Gender.NEUT)),
				Euristic.all(PartOfSpeech.ADJS, Gender.NEUT)
				);
		euristics.add(euristicNounNAdjs);		
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
//		Euristic euristicNounPrep = Euristic.concat(
//				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS),
//				Euristic.any(PartOfSpeech.PREP)
//				);
//		euristics.add(euristicNounPrep); очень зол на меня
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
		Euristic euristicNounNounG = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS),
				Euristic.all(PartOfSpeech.NOUN, Case.GENT)
				);
		euristics.add(euristicNounNounG);
		Euristic euristicNounNounG1 = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS),
				Euristic.all(PartOfSpeech.NOUN, Case.GEN1)
				);
		euristics.add(euristicNounNounG1);
		Euristic euristicNounPred = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.PRED)
				);
		euristics.add(euristicNounPred);
		Euristic euristicNounPrtfs = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS),
				Euristic.or(Euristic.any(PartOfSpeech.PRTF), Euristic.any(PartOfSpeech.PRTS))
				);
		euristics.add(euristicNounPrtfs);
		Euristic euristicPrtfsNoun = Euristic.concat(
				Euristic.or(Euristic.any(PartOfSpeech.PRTF), Euristic.any(PartOfSpeech.PRTS)),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJS)
				);
		euristics.add(euristicPrtfsNoun);
//		Euristic euristicNproAdj = Euristic.concat(
//				Euristic.any(PartOfSpeech.NPRO),
//				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.NOUN)
//				);
//		euristics.add(euristicNproAdj); моих чёрных кос
//		Euristic euristicNounAdjs = Euristic.concat(
//				Euristic.any(PartOfSpeech.NOUN), 
//				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.NOUN)
//				);
//		euristics.add(euristicNounAdjs); чёрных кос (чёрных - существительное)
		Euristic euristicAdvbAdjs = Euristic.concat(
				Euristic.any(PartOfSpeech.ADVB),
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.NOUN)
				);
		euristics.add(euristicAdvbAdjs);
//		Euristic euristicAdjsNoun = Euristic.concat(
//				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.NOUN),
//				Euristic.any(PartOfSpeech.NOUN)
//				);
//		euristics.add(euristicAdjsNoun); кос чёрных (чёрных - существительное)
//		Euristic euristicAdjsVerb = Euristic.concat(
//				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.NOUN),
//				Euristic.any(PartOfSpeech.VERB)
//				);
//		euristics.add(euristicAdjsVerb);
//		Euristic euristicAdjsAdvb = Euristic.concat(
//				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.NOUN),
//				Euristic.any(PartOfSpeech.ADVB)
//				);
//		euristics.add(euristicAdjsAdvb); мелок вдруг рассыпался
		Euristic euristicAdjsNoun1 = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.NOUN),
				Euristic.all(PartOfSpeech.NOUN, Case.NOMN, Gender.MASC)
				);
		euristics.add(euristicAdjsNoun1);
		Euristic euristicNounAdjs = Euristic.concat(
				Euristic.all(PartOfSpeech.NOUN, Case.NOMN, Gender.MASC),
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.NOUN)
				);
		euristics.add(euristicNounAdjs);
		Euristic euristicAdjsNounAb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.NOUN),
				Euristic.all(PartOfSpeech.NOUN, Case.ABLT)
				);
		euristics.add(euristicAdjsNounAb);
		return euristics;				
	}
	
	// прилательное - существительное
	public void test09() {
		Euristic matched = matched(getRulesList9(), "сильный красный");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList9() {
		List<Euristic> euristics = new ArrayList<Euristic>();
//		Euristic euristicPrepNoun = Euristic.concat(
//				Euristic.or(Euristic.word("за", PartOfSpeech.PREP), Euristic.word("между", PartOfSpeech.PREP), Euristic.word("меж", PartOfSpeech.PREP), Euristic.word("над", PartOfSpeech.PREP), Euristic.word("надо", PartOfSpeech.PREP)),
//				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF)
//				);
//		euristics.add(euristicPrepNoun);
//		Euristic euristicPrep2Noun = Euristic.concat(
//				Euristic.or(Euristic.word("перед", PartOfSpeech.PREP), Euristic.word("передо", PartOfSpeech.PREP), Euristic.word("пред", PartOfSpeech.PREP), Euristic.word("предо", PartOfSpeech.PREP)),
//				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF)
//				);
//		euristics.add(euristicPrep2Noun);
//		Euristic euristicPrep3Noun = Euristic.concat(
//				Euristic.or(Euristic.word("под", PartOfSpeech.PREP), Euristic.word("подо", PartOfSpeech.PREP), Euristic.word("с", PartOfSpeech.PREP), Euristic.word("со", PartOfSpeech.PREP)),
//				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF)
//				);
//		euristics.add(euristicPrep3Noun);
		Euristic euristicAdjNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF)
				);
		euristics.add(euristicAdjNoun);
//		Euristic euristicVerbNoun = Euristic.concat(
//				Euristic.any(PartOfSpeech.VERB), 
//				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF)
//				);
//		euristics.add(euristicVerbNoun); услышал лёгкие шаги
		Euristic euristicNounVerb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF),
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicNounVerb);
		Euristic euristicProNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, Extras.Anph, FeaturesGramem.Apro), 
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF)
				);
		euristics.add(euristicProNoun);
		//		Euristic euristicNounNoun = Euristic.concat(
//				Euristic.any(PartOfSpeech.NOUN), 
//				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF)
//				);
//		euristics.add(euristicNounNoun); красные красные шли
		Euristic euristicNounPrep = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF), 
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicNounPrep);
//		Euristic euristicNounNounG = Euristic.concat(
//				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF), 
//				Euristic.all(PartOfSpeech.NOUN, Case.GENT)
//				);
//		euristics.add(euristicNounNounG); "для новых встреч"
//		Euristic euristicNounVerb = Euristic.concat(
//				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADJF), 
//				Euristic.any(PartOfSpeech.VERB)
//				);
//		euristics.add(euristicNounVerb); очень живой раздел
		Euristic euristicPrepAdj = Euristic.concat(
				Euristic.or(Euristic.word("на", PartOfSpeech.PREP), Euristic.word("о", PartOfSpeech.PREP), Euristic.word("в", PartOfSpeech.PREP)), 
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.NOUN)
				);
		euristics.add(euristicPrepAdj);
//		Euristic euristicVerbAdj = Euristic.concat(
//				Euristic.any(PartOfSpeech.VERB), 
//				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.NOUN)
//				);
//		euristics.add(euristicVerbAdj); ударяют косой по столу
		Euristic euristicAdjNoun1 = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.NOUN), 
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicAdjNoun1);
		Euristic euristicAdvbAdjf = Euristic.concat(
				Euristic.any(PartOfSpeech.ADVB),
				Euristic.createConflictChecker(PartOfSpeech.ADJF, PartOfSpeech.NOUN)
				);
		euristics.add(euristicAdvbAdjf);	
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
				Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.GRND), Euristic.all(PartOfSpeech.ADJF, Case.NOMN, SingularPlural.SINGULAR, Gender.FEMN))
				);
		euristics.add(euristicNounAdj);
		Euristic euristicAdjAdj = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.GRND), Euristic.all(PartOfSpeech.ADJF, Case.NOMN, SingularPlural.SINGULAR, Gender.FEMN))
				);
		euristics.add(euristicAdjAdj);
		Euristic euristicAdjNoun = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.GRND), Euristic.all(PartOfSpeech.ADJF, Case.NOMN, SingularPlural.SINGULAR, Gender.FEMN)),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicAdjNoun);
		Euristic euristicAdjAdjf = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.GRND), Euristic.all(PartOfSpeech.ADJF, Case.NOMN, SingularPlural.SINGULAR, Gender.FEMN)),
				Euristic.any(PartOfSpeech.ADJF)
				);
		euristics.add(euristicAdjAdjf);
		return euristics;
	}
	
	// глагол - прилагательное
	public void test12() {
		Euristic matched = matched(getRulesList12(), "весело праздную");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList12() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicNproVerb = Euristic.concat(
				Euristic.all(PartOfSpeech.NPRO, Personality.PERS1),
				Euristic.and(Euristic.conflicting(PartOfSpeech.VERB, PartOfSpeech.ADJF), Euristic.all(PartOfSpeech.VERB, Personality.PERS1, SingularPlural.SINGULAR, Time.PRESENT))
				);
		euristics.add(euristicNproVerb);
		Euristic euristicAdvbVerb = Euristic.concat(
				Euristic.any(PartOfSpeech.ADVB),
				Euristic.and(Euristic.conflicting(PartOfSpeech.VERB, PartOfSpeech.ADJF), Euristic.all(PartOfSpeech.VERB, Personality.PERS1, SingularPlural.SINGULAR, Time.PRESENT))
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
		Euristic matched = matched(getRulesList13(), "солнце росло");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList13() {
		List<Euristic> euristics = new ArrayList<Euristic>();
//		Euristic euristicAdvbVerb = Euristic.concat(
//				Euristic.any(PartOfSpeech.ADVB),
//				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.ADJS)
//				);
//		euristics.add(euristicAdvbVerb); мальчик очень вял
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
		Euristic euristicOchAdjs = Euristic.concat(
				Euristic.word("очень", PartOfSpeech.ADVB),
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.VERB)
				);
		euristics.add(euristicOchAdjs);	
		Euristic euristicNotAdvbVerb = Euristic.concat(
				Euristic.not(Euristic.any(PartOfSpeech.ADVB)),
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.VERB)
				);
		euristics.add(euristicNotAdvbVerb);
		Euristic euristicNotVerbAdvb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.VERB),
				Euristic.not(Euristic.any(PartOfSpeech.ADVB))
				);
		euristics.add(euristicNotVerbAdvb);
		Euristic euristicNotVerbPrep = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.VERB),
				Euristic.not(Euristic.any(PartOfSpeech.PREP))
				);
		euristics.add(euristicNotVerbPrep);
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
		Euristic euristicAdjfANoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADVB)
				);
		euristics.add(euristicAdjfANoun);
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
		Euristic euristicNounAdjf = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.ADVB),
				Euristic.any(PartOfSpeech.ADJF)
				);
		euristics.add(euristicNounAdjf);
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
//		Euristic euristicNproAdvb = Euristic.concat(
//				Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro),
//				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.NOUN)
//				);
//		euristics.add(euristicNproAdvb);
		Euristic euristicGrndAdvb = Euristic.concat(
				Euristic.any(PartOfSpeech.GRND),
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.NOUN)
				);
		euristics.add(euristicGrndAdvb);
		Euristic euristicPrtfAdvb = Euristic.concat(
				Euristic.any(PartOfSpeech.PRTF),
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.NOUN)
				);
		euristics.add(euristicPrtfAdvb);
//		Euristic euristicAdvbNoun = Euristic.concat(
//				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.NOUN),
//				Euristic.any(PartOfSpeech.NOUN)
//				);
//		euristics.add(euristicAdvbNoun); перед случайным наездом силовиков
		Euristic euristicAdvbAdvb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.ADVB)
				);
		euristics.add(euristicAdvbAdvb);
//		Euristic euristicAdvbPrep = Euristic.concat(
//				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.NOUN),
//				Euristic.any(PartOfSpeech.PREP)
//				);
//		euristics.add(euristicAdvbPrep);
		Euristic euristicAdvbVerb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicAdvbVerb);
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
//		Euristic euristicWordPred = Euristic.concat(
//				Euristic.word("ещё", PartOfSpeech.ADVB),
//				Euristic.createConflictChecker(PartOfSpeech.PRED, PartOfSpeech.PREP)
//				);
//		euristics.add(euristicWordPred);
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
				Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.NUMR), Euristic.word("семью"))
				);
		euristics.add(euristicNproNoun);
		Euristic euristicAdjfNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, Case.ACCS),
				Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.NUMR), Euristic.word("семью"))
				);
		euristics.add(euristicAdjfNoun);
		Euristic euristicInfnNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.NUMR), Euristic.word("семью"))
				);
		euristics.add(euristicInfnNoun);
		Euristic euristicVerbNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.NUMR), Euristic.word("семью"))
				);
		euristics.add(euristicVerbNoun);
		Euristic euristicNounPrep = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.NUMR), Euristic.word("семью")),
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicNounPrep);
		Euristic euristicNumrAdjf = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.NUMR, PartOfSpeech.NOUN), Euristic.word("семью")),
				Euristic.all(PartOfSpeech.ADJF, Case.ABLT)
				);
		euristics.add(euristicNumrAdjf);
		Euristic euristicNumrNoun = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.NUMR, PartOfSpeech.NOUN), Euristic.word("семью")),
				Euristic.all(PartOfSpeech.NOUN, Case.ABLT)
				);
		euristics.add(euristicNumrNoun);
		Euristic euristicNumrNumr = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.NUMR, PartOfSpeech.NOUN), Euristic.word("семью")),
				Euristic.any(PartOfSpeech.NUMR)
				);
		euristics.add(euristicNumrNumr);
		return euristics;
	}
	
	// существительное - числительное "сорока"
	public void test19() {
		Euristic matched = matched(getRulesList19(), "лет сорока");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList19() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicNounNumr = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.and(Euristic.conflicting(PartOfSpeech.NUMR, PartOfSpeech.NOUN), Euristic.word("сорока"))
				);
		euristics.add(euristicNounNumr);
		Euristic euristicNumrNumr = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.NUMR, PartOfSpeech.NOUN), Euristic.word("сорока")),
				Euristic.all(PartOfSpeech.NUMR, Case.GENT)
				);
		euristics.add(euristicNumrNumr);
		Euristic euristicNumrNoun = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.NUMR, PartOfSpeech.NOUN), Euristic.word("сорока")),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicNumrNoun);
		Euristic euristicPrepNumr = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.and(Euristic.conflicting(PartOfSpeech.NUMR, PartOfSpeech.NOUN), Euristic.word("сорока"))
				);
		euristics.add(euristicPrepNumr);
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
		Euristic euristicPrepNumr = Euristic.concat(
			Euristic.or(Euristic.word("в", PartOfSpeech.PREP), Euristic.word("на", PartOfSpeech.PREP), Euristic.word("при", PartOfSpeech.PREP), Euristic.word("о", PartOfSpeech.PREP)),
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Anum))
		);
		euristics.add(euristicPrepNumr);
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
		Euristic euristicPrepNoun = Euristic.concat(
			Euristic.or(Euristic.word("за", PartOfSpeech.PREP), Euristic.word("над", PartOfSpeech.PREP), Euristic.word("перед", PartOfSpeech.PREP), Euristic.word("под", PartOfSpeech.PREP), Euristic.word("с", PartOfSpeech.PREP)),
			Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.ADJF), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Anum))
		);
		euristics.add(euristicPrepNoun);
		return euristics;
	}
	
	// глагол - числительное "три"
	public void test22() {
		Euristic matched = matched(getRulesList22(), "три тщательно"); //Ломается, проблема - падеж рд1 вместо просто рд
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
				Euristic.all(PartOfSpeech.NOUN, Case.GEN1)
				);
		euristics.add(euristicNumrNoun);
		Euristic euristicNumrAdjf = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NUMR, PartOfSpeech.VERB),
				Euristic.all(PartOfSpeech.ADJF, Case.GENT)
				);
		euristics.add(euristicNumrAdjf);
		Euristic euristicNotNounNumr = Euristic.concat(
				Euristic.not(Euristic.all(PartOfSpeech.NOUN, Case.GENT)),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NUMR)
				);
		euristics.add(euristicNotNounNumr);
		Euristic euristicNotNumrNumr = Euristic.concat(
				Euristic.not(Euristic.any(PartOfSpeech.NUMR)),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NUMR)
				);
		euristics.add(euristicNotNumrNumr);
		Euristic euristicNotVerbNumr = Euristic.concat(
				Euristic.not(Euristic.any(PartOfSpeech.VERB)),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NUMR)
				);
		euristics.add(euristicNotVerbNumr);
		Euristic euristicNotPrepNumr = Euristic.concat(
				Euristic.not(Euristic.any(PartOfSpeech.PREP)),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NUMR)
				);
		euristics.add(euristicNotPrepNumr);
		Euristic euristicNotAdjfNumr = Euristic.concat(
				Euristic.not(Euristic.any(PartOfSpeech.ADJF)),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NUMR)
				);
		euristics.add(euristicNotAdjfNumr);
		Euristic euristicNotInfnNumr = Euristic.concat(
				Euristic.not(Euristic.any(PartOfSpeech.INFN)),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NUMR)
				);
		euristics.add(euristicNotInfnNumr);
		Euristic euristicNotNumrNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NUMR),
				Euristic.not(Euristic.all(PartOfSpeech.NOUN, Case.GEN1))
				);
		euristics.add(euristicNotNumrNoun);
		Euristic euristicNotNumrAdjf = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NUMR),
				Euristic.not(Euristic.all(PartOfSpeech.ADJF, Case.GENT))
				);
		euristics.add(euristicNotNumrAdjf);
		return euristics;
	}
	
	// прилагательное - наречие
	public void test23() {
		Euristic matched = matched(getRulesList23(), "горько плача");
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
		Euristic euristicInfnAdvb = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.ADJS)
				);
		euristics.add(euristicInfnAdvb);
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
		Euristic euristicAdvbGrnd = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.GRND)
				);
		euristics.add(euristicAdvbGrnd); //??горько плача
		Euristic euristicAdvbPrtf = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.PRTF)
				);
		euristics.add(euristicAdvbPrtf);
		Euristic euristicAdvbPrts = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.PRTS)
				);
		euristics.add(euristicAdvbPrts);
		Euristic euristicAdvb1Advb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.ADVB)
				);
		euristics.add(euristicAdvb1Advb);
		Euristic euristicAdvbAdvb2 = Euristic.concat(
				Euristic.any(PartOfSpeech.ADVB),
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.ADJS)
				);
		euristics.add(euristicAdvbAdvb2);
		Euristic euristicNproAdvb = Euristic.concat(
				Euristic.all(PartOfSpeech.NPRO, Case.DATV),
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.ADJS)
				);
		euristics.add(euristicNproAdvb);
		Euristic euristicAdvbNpro = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.ADJS),
				Euristic.all(PartOfSpeech.NPRO, Case.DATV)
				);
		euristics.add(euristicAdvbNpro);
		Euristic euristicAdvbAdjf = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.ADJF)
				);
		euristics.add(euristicAdvbAdjf);
		Euristic euristicAdjsNounM = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.ADJS, PartOfSpeech.ADVB), Euristic.all(PartOfSpeech.ADJS, Gender.MASC)),
				Euristic.all(PartOfSpeech.NOUN, Gender.MASC, SingularPlural.SINGULAR, Case.NOMN)
				);
		euristics.add(euristicAdjsNounM);
		Euristic euristicNounMAdjs = Euristic.concat(
				Euristic.all(PartOfSpeech.NOUN, Gender.MASC, SingularPlural.SINGULAR, Case.NOMN),
				Euristic.and(Euristic.conflicting(PartOfSpeech.ADJS, PartOfSpeech.ADVB), Euristic.all(PartOfSpeech.ADJS, Gender.MASC))
				);
		euristics.add(euristicNounMAdjs);
		Euristic euristicNproAdjs = Euristic.concat(
				Euristic.all(PartOfSpeech.NPRO, Gender.MASC, SingularPlural.SINGULAR, Case.NOMN),
				Euristic.and(Euristic.conflicting(PartOfSpeech.ADJS, PartOfSpeech.ADVB), Euristic.all(PartOfSpeech.ADJS, Gender.MASC))
				);
		euristics.add(euristicNproAdjs);
		Euristic euristicAdjsNpro = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.ADJS, PartOfSpeech.ADVB), Euristic.all(PartOfSpeech.ADJS, Gender.MASC)),
				Euristic.all(PartOfSpeech.NPRO, Gender.MASC, SingularPlural.SINGULAR, Case.NOMN)
				);
		euristics.add(euristicAdjsNpro);
		Euristic euristicNounAdjs = Euristic.concat(
				Euristic.all(PartOfSpeech.NOUN, Gender.NEUT, SingularPlural.SINGULAR, Case.NOMN),
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.ADVB)
				);
		euristics.add(euristicNounAdjs);
		Euristic euristicNoun1Adjs = Euristic.concat(
				Euristic.all(PartOfSpeech.NOUN, Gender.NEUT, SingularPlural.UNCHANGABLE, Case.NOMN),
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.ADVB)
				);
		euristics.add(euristicNoun1Adjs);
		Euristic euristicAdjsNoun = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.ADVB),
				Euristic.all(PartOfSpeech.NOUN, Gender.NEUT, SingularPlural.SINGULAR, Case.NOMN)
				);
		euristics.add(euristicAdjsNoun);
		Euristic euristicAdjsNoun1 = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.ADVB),
				Euristic.all(PartOfSpeech.NOUN, Gender.NEUT, SingularPlural.UNCHANGABLE, Case.NOMN)
				);
		euristics.add(euristicAdjsNoun1);
		Euristic euristicAdjsAdjf = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.ADJS, PartOfSpeech.ADVB),
				Euristic.all(PartOfSpeech.ADJF, Gender.NEUT, SingularPlural.SINGULAR, Case.NOMN)
				);
		euristics.add(euristicAdjsAdjf);		
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
//		Euristic euristicAdjsNoun = Euristic.concat(
//				Euristic.any(PartOfSpeech.ADJS),
//				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.GRND)
//				);
//		euristics.add(euristicAdjsNoun); внезапно выплыв из воды
		Euristic euristicNounAdjs = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.GRND),
				Euristic.any(PartOfSpeech.ADJS)
				);
		euristics.add(euristicNounAdjs);
		Euristic euristicGrndNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.GRND),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.GRND)
				);
		euristics.add(euristicGrndNoun);
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
				Euristic.all(PartOfSpeech.NOUN, Case.GENT)
				);
		euristics.add(euristicNounNoun2);
//		Euristic euristicGrndNoun1 = Euristic.concat(
//				Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.NOUN),
//				Euristic.all(PartOfSpeech.NOUN, Case.ACCS)
//				);
//		euristics.add(euristicGrndNoun1); первый выплыв Степана
		Euristic euristicGrndAdjf = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.NOUN),
				Euristic.all(PartOfSpeech.ADJF, Case.ACCS)
				);
		euristics.add(euristicGrndAdjf);
		Euristic euristicNproNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, Extras.Anph, FeaturesGramem.Apro),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.GRND)
				);
		euristics.add(euristicNproNoun);
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
//		Euristic euristicGrndPrep = Euristic.concat(
//				Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.NOUN),
//				Euristic.any(PartOfSpeech.PREP)
//				);
//		euristics.add(euristicGrndPrep); этот нагрев от огня
		return euristics;
	}
				
	// существительное - глагол
	public void test25() {
		Euristic matched = matched(getRulesList25(), "я баню");
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
		Euristic euristicPrtsNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.PRTS),
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB)
				);
		euristics.add(euristicPrtsNoun);
		Euristic euristicNounPrts = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.PRTS)
				);
		euristics.add(euristicNounPrts);
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
//		Euristic euristicNproNoun = Euristic.concat(
//				Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro, Extras.Anph),
//				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB)
//				);
//		euristics.add(euristicNproNoun);
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
//		Euristic euristicNounNoun = Euristic.concat(
//				Euristic.any(PartOfSpeech.NOUN),
//				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB)
//				);
//		euristics.add(euristicNounNoun); лебедей кормила
		Euristic euristicNounVerb = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicNounVerb);
		Euristic euristicNounNounG = Euristic.concat(
				Euristic.createConflictChecker(PartOfSpeech.NOUN, PartOfSpeech.VERB),
				Euristic.all(PartOfSpeech.NOUN, Case.GEN2)
				);
		euristics.add(euristicNounNounG);
		Euristic euristicNounAdjsS = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.VERB), Euristic.all(PartOfSpeech.NOUN, SingularPlural.SINGULAR)),
				Euristic.all(PartOfSpeech.ADJS, SingularPlural.SINGULAR)
				);
		euristics.add(euristicNounAdjsS);
		Euristic euristicNounAdjsP = Euristic.concat(
				Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.VERB), Euristic.all(PartOfSpeech.NOUN, SingularPlural.PLURAL)),
				Euristic.all(PartOfSpeech.ADJS, SingularPlural.PLURAL)
				);
		euristics.add(euristicNounAdjsP);
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
		Euristic euristicNoun1Verb = Euristic.concat(
				Euristic.all(PartOfSpeech.NOUN, Case.NOMN),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN)
				);
		euristics.add(euristicNoun1Verb);
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
		Euristic euristicNounVVerb = Euristic.concat(
				Euristic.all(PartOfSpeech.NOUN, Case.ACCS),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN)
				);
		euristics.add(euristicNounVVerb);
//		Euristic euristicVerbPrep = Euristic.concat(
//				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN),
//				Euristic.any(PartOfSpeech.PREP)
//				);
//		euristics.add(euristicVerbPrep); живой раздел в книге
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
//		Euristic euristicVerbInfn = Euristic.concat(
//				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN),
//				Euristic.any(PartOfSpeech.INFN)
//				);
//		euristics.add(euristicVerbInfn); откуда в такой глуши взяться оптимизму
		Euristic euristicNounVerbNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.NOUN),
				Euristic.all(PartOfSpeech.NOUN, Case.ACCS)
				);
		euristics.add(euristicNounVerbNoun);
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
				Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.NPRO), Euristic.word("кто"))
				);
		euristics.add(euristicPrepNoun);
		Euristic euristicPrepNpro = Euristic.concat(
				Euristic.word("к", PartOfSpeech.PREP),
				Euristic.and(Euristic.conflicting(PartOfSpeech.NPRO, PartOfSpeech.NOUN), Euristic.word("кто"))
				);
		euristics.add(euristicPrepNpro);
		Euristic euristicVerbNpro = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.and(Euristic.conflicting(PartOfSpeech.NPRO, PartOfSpeech.NOUN), Euristic.word("кто"))
				);
		euristics.add(euristicVerbNpro);
		return euristics;
	}
	
	// существительное - местоимение "тем"
	public void test27() {
		Euristic matched = matched(getRulesList27(), "к тем кто");
		assertNotNull(matched);
	}
			
	private List<Euristic> getRulesList27() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicAdjfNpro = Euristic.concat(
			Euristic.any(PartOfSpeech.ADJF),
			Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.ADJF), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro), Euristic.word("тем"))
		);
		euristics.add(euristicAdjfNpro);
		Euristic euristicPrepNpro = Euristic.concat(
			Euristic.any(PartOfSpeech.PREP),
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro), Euristic.word("тем"))
		);
		euristics.add(euristicPrepNpro);
		Euristic euristicNproSam = Euristic.concat(
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro), Euristic.word("тем")),
			Euristic.word("самым", PartOfSpeech.ADJF)
		);
		euristics.add(euristicNproSam);
		Euristic euristicNproNeMen = Euristic.concat(
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro), Euristic.word("тем")),
			Euristic.word("не менее", PartOfSpeech.ADVB)
		);
		euristics.add(euristicNproNeMen);
		Euristic euristicNproBol = Euristic.concat(
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro), Euristic.word("тем")),
			Euristic.word("более", PartOfSpeech.ADVB)
		);
		euristics.add(euristicNproBol);
		Euristic euristicPrepAdjfNpro = Euristic.concat(
			Euristic.any(PartOfSpeech.PREP),
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro), Euristic.word("тем")),
			Euristic.any(PartOfSpeech.NPRO)
		);
		euristics.add(euristicPrepAdjfNpro);
		Euristic euristicPrepAdjfPrep = Euristic.concat(
			Euristic.any(PartOfSpeech.PREP),
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro), Euristic.word("тем")),
			Euristic.any(PartOfSpeech.PREP)
		);
		euristics.add(euristicPrepAdjfPrep);
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
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro), Euristic.word("том"))
		);
		euristics.add(euristicPrepNpro);
		Euristic euristicNotPrepNpro = Euristic.concat(
			Euristic.not(Euristic.any(PartOfSpeech.PREP)),
			Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.ADJF), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro), Euristic.word("том"))
		);
		euristics.add(euristicNotPrepNpro);
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
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro), Euristic.word("тому"))
		);
		euristics.add(euristicPrepNpro);
		Euristic euristicNproPodob = Euristic.concat(
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro), Euristic.word("тому")),
			Euristic.word("подобный", PartOfSpeech.ADJF)
		);
		euristics.add(euristicNproPodob);
		Euristic euristicNproNazad = Euristic.concat(
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro), Euristic.word("тому")),
			Euristic.word("назад", PartOfSpeech.ADVB)
		);
		euristics.add(euristicNproNazad);
		Euristic euristicNotNproPodob = Euristic.concat(
			Euristic.and(Euristic.conflicting(PartOfSpeech.NOUN, PartOfSpeech.ADJF), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro), Euristic.word("тому")),
			Euristic.not(Euristic.word("подобный", PartOfSpeech.ADJF))
		);
		euristics.add(euristicNotNproPodob);
		Euristic euristicNotNproNazad = Euristic.concat(
			Euristic.and(Euristic.conflicting(PartOfSpeech.ADJF, PartOfSpeech.NOUN), Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro), Euristic.word("тому")),
			Euristic.not(Euristic.word("назад", PartOfSpeech.ADVB))
		);
		euristics.add(euristicNotNproNazad);
		return euristics;
	}
	
	// предлог - деепричастие "для"
	public void test30() {
		Euristic matched = matched(getRulesList30(), "для лису");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList30() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicPrepNpro = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.GRND),
			Euristic.all(PartOfSpeech.NPRO, Case.GENT)
		);
			euristics.add(euristicPrepNpro);
		Euristic euristicPrepNoun = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.GRND),
			Euristic.all(PartOfSpeech.NOUN, Case.GENT)
		);
			euristics.add(euristicPrepNoun);
		Euristic euristicPrepAdjf = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.GRND),
			Euristic.all(PartOfSpeech.ADJF, Case.GENT)
		);
			euristics.add(euristicPrepAdjf);
		Euristic euristicAdvbGrnd = Euristic.concat(
			Euristic.any(PartOfSpeech.ADVB),
			Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.PREP)
		);
			euristics.add(euristicAdvbGrnd); 
//		Euristic euristicNounGrnd = Euristic.concat(
//			Euristic.all(PartOfSpeech.NOUN, Case.ACCS),
//			Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.PREP)
//		);
//			euristics.add(euristicNounGrnd);
		Euristic euristicAdjfGrnd = Euristic.concat(
			Euristic.all(PartOfSpeech.ADJF, Case.ACCS, SingularPlural.SINGULAR),
			Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.PREP)
		);
			euristics.add(euristicAdjfGrnd);
		Euristic euristicGrndAdvb = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.PREP),
			Euristic.any(PartOfSpeech.ADVB)
		);
			euristics.add(euristicGrndAdvb);
		Euristic euristicGrndNoun = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.PREP),
			Euristic.all(PartOfSpeech.NOUN, Case.ACCS, SingularPlural.SINGULAR)
		);
			euristics.add(euristicGrndNoun);
		Euristic euristicGrndAdjf = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.PREP),
			Euristic.all(PartOfSpeech.ADJF, Case.ACCS, SingularPlural.SINGULAR)
		);
			euristics.add(euristicGrndAdjf);
		return euristics;
	}
	
	// предлог - глагол "при"
	public void test31() {
		Euristic matched = matched(getRulesList31(), "при лису");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList31() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicPrepNpro = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.VERB),
			Euristic.all(PartOfSpeech.NPRO, Case.LOCT)
		);
			euristics.add(euristicPrepNpro);
		Euristic euristicPrepNoun = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.VERB),
			Euristic.all(PartOfSpeech.NOUN, Case.LOCT)
		);
			euristics.add(euristicPrepNoun);
		Euristic euristicPrepAdjf = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.VERB),
			Euristic.all(PartOfSpeech.ADJF, Case.LOCT)
		);
			euristics.add(euristicPrepAdjf);
		Euristic euristicAdvbVerb = Euristic.concat(
			Euristic.any(PartOfSpeech.ADVB),
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.PREP)
		);
			euristics.add(euristicAdvbVerb);
		Euristic euristicNounVerb = Euristic.concat(
			Euristic.all(PartOfSpeech.NOUN, Case.ACCS),
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.PREP)
		);
			euristics.add(euristicNounVerb);
		Euristic euristicNproVerb = Euristic.concat(
			Euristic.all(PartOfSpeech.NPRO, Case.ACCS),
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.PREP)
		);
			euristics.add(euristicNproVerb);
		Euristic euristicAdjfVerb = Euristic.concat(
			Euristic.all(PartOfSpeech.ADJF, Case.ACCS),
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.PREP)
		);
			euristics.add(euristicAdjfVerb);
		Euristic euristicVerbAdvb = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.PREP),
			Euristic.any(PartOfSpeech.ADVB)
		);
			euristics.add(euristicVerbAdvb);
		Euristic euristicVerbNoun = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.PREP),
			Euristic.all(PartOfSpeech.NOUN, Case.ACCS)
		);
			euristics.add(euristicVerbNoun);
		Euristic euristicVerbAdjf = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.PREP),
			Euristic.all(PartOfSpeech.ADJF, Case.ACCS)
		);
			euristics.add(euristicVerbAdjf);
		Euristic euristicVerbNpro = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.PREP),
			Euristic.all(PartOfSpeech.NPRO, Case.ACCS)
		);
			euristics.add(euristicVerbNpro);
		return euristics;
	}
	
	// глагол - компаратив
	public void test32() {
		Euristic matched = matched(getRulesList32(), "светлей быстро");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList32() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicCompVerb = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.COMP, PartOfSpeech.VERB),
			Euristic.any(PartOfSpeech.VERB)
		);
			euristics.add(euristicCompVerb);
		Euristic euristicCompVse = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.COMP, PartOfSpeech.VERB),
			Euristic.word("все", PartOfSpeech.PRCL)
		);
			euristics.add(euristicCompVse);
		Euristic euristicCompNoun = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.COMP, PartOfSpeech.VERB),
			Euristic.all(PartOfSpeech.NOUN, Case.GENT)
		);
			euristics.add(euristicCompNoun);
		Euristic euristicCompAdjf = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.COMP, PartOfSpeech.VERB),
			Euristic.all(PartOfSpeech.ADJF, Case.GENT)
		);
			euristics.add(euristicCompAdjf);
		Euristic euristicCompNpro = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.COMP, PartOfSpeech.VERB),
			Euristic.all(PartOfSpeech.NPRO, Case.GENT)
		);
			euristics.add(euristicCompNpro);
		Euristic euristicVerbComp = Euristic.concat(
			Euristic.any(PartOfSpeech.VERB),
			Euristic.createConflictChecker(PartOfSpeech.COMP, PartOfSpeech.VERB)
		);
			euristics.add(euristicVerbComp);
		Euristic euristicVseComp = Euristic.concat(
			Euristic.word("все", PartOfSpeech.PRCL),
			Euristic.createConflictChecker(PartOfSpeech.COMP, PartOfSpeech.VERB)
		);
			euristics.add(euristicVseComp);
		Euristic euristicNounComp = Euristic.concat(
			Euristic.all(PartOfSpeech.NOUN, Case.GENT),
			Euristic.createConflictChecker(PartOfSpeech.COMP, PartOfSpeech.VERB)
		);
			euristics.add(euristicNounComp);
		Euristic euristicAdjfComp = Euristic.concat(
			Euristic.all(PartOfSpeech.ADJF, Case.GENT),
			Euristic.createConflictChecker(PartOfSpeech.COMP, PartOfSpeech.VERB)
		);
			euristics.add(euristicAdjfComp);
		Euristic euristicNproComp = Euristic.concat(
			Euristic.all(PartOfSpeech.NPRO, Case.GENT),
			Euristic.createConflictChecker(PartOfSpeech.COMP, PartOfSpeech.VERB)
		);
			euristics.add(euristicNproComp);			
		Euristic euristicNotCompVerb = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.COMP),
			Euristic.not(Euristic.any(PartOfSpeech.VERB))
		);
		euristics.add(euristicNotCompVerb);
		Euristic euristicNotCompVse = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.COMP),
			Euristic.not(Euristic.word("все", PartOfSpeech.PRCL))
		);
		euristics.add(euristicNotCompVse);
		Euristic euristicNotCompNoun = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.COMP),
			Euristic.not(Euristic.all(PartOfSpeech.NOUN, Case.GENT))
		);
		euristics.add(euristicNotCompNoun);
		Euristic euristicNotCompAdjf = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.COMP),
			Euristic.not(Euristic.all(PartOfSpeech.ADJF, Case.GENT))
		);
		euristics.add(euristicNotCompAdjf);
		Euristic euristicNotCompNpro = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.COMP),
			Euristic.not(Euristic.all(PartOfSpeech.NPRO, Case.GENT))
		);
		euristics.add(euristicNotCompNpro);
		Euristic euristicNotVerbComp = Euristic.concat(
			Euristic.not(Euristic.any(PartOfSpeech.VERB)),
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.COMP)
		);
		euristics.add(euristicNotVerbComp);
		Euristic euristicNotVseComp = Euristic.concat(
			Euristic.not(Euristic.word("все", PartOfSpeech.PRCL)),
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.COMP)
		);
		euristics.add(euristicNotVseComp);
		Euristic euristicNotNounComp = Euristic.concat(
			Euristic.not(Euristic.all(PartOfSpeech.NOUN, Case.GENT)),
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.COMP)
		);
		euristics.add(euristicNotNounComp);
		Euristic euristicNotAdjfComp = Euristic.concat(
			Euristic.not(Euristic.all(PartOfSpeech.ADJF, Case.GENT)),
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.COMP)
		);
		euristics.add(euristicNotAdjfComp);
		Euristic euristicNotNproComp = Euristic.concat(
			Euristic.not(Euristic.all(PartOfSpeech.NPRO, Case.GENT)),
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.COMP)
		);
		euristics.add(euristicNotNproComp);
		return euristics;
	}
	
	// предлог - наречие "кругом"
	public void test33() {
		Euristic matched = matched(getRulesList33(), "кругом природа");
		assertNotNull(matched);
	}

	private List<Euristic> getRulesList33() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicPrepNoun = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.ADVB),
			Euristic.all(PartOfSpeech.NOUN, Case.GENT)
		);
		euristics.add(euristicPrepNoun);
		Euristic euristicPrepAdjf = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.ADVB),
			Euristic.all(PartOfSpeech.ADJF, Case.GENT)
		);
		euristics.add(euristicPrepAdjf);
		Euristic euristicPrepNpro = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.PREP, PartOfSpeech.ADVB),
			Euristic.all(PartOfSpeech.NPRO, Case.GENT)
		);
		euristics.add(euristicPrepNpro);
		Euristic euristicNotPrepNoun = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.PREP),
			Euristic.not(Euristic.all(PartOfSpeech.NOUN, Case.GENT))
		);	
		euristics.add(euristicNotPrepNoun);
		Euristic euristicNotPrepAdjf = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.PREP),
			Euristic.not(Euristic.all(PartOfSpeech.ADJF, Case.GENT))
		);
		euristics.add(euristicNotPrepAdjf);
		Euristic euristicNotPrepNpro = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.PREP),
			Euristic.not(Euristic.all(PartOfSpeech.NOUN, Case.GENT))
		);
		euristics.add(euristicNotPrepNpro);
		Euristic euristicVerbAdvb = Euristic.concat(
			Euristic.any(PartOfSpeech.VERB),
			Euristic.createConflictChecker(PartOfSpeech.ADVB, PartOfSpeech.PREP)
		); 
		euristics.add(euristicVerbAdvb);
		return euristics;
	}
	
	// частица - глагол "было"
	public void test34() {
		Euristic matched = matched(getRulesList34(), "было винить");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList34() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicPrclVerb = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.PRCL, PartOfSpeech.VERB),
			Euristic.all(PartOfSpeech.VERB, Time.PAST)
		);
			euristics.add(euristicPrclVerb);
		Euristic euristicVerbPrcl = Euristic.concat(
			Euristic.all(PartOfSpeech.VERB, Time.PAST),
			Euristic.createConflictChecker(PartOfSpeech.PRCL, PartOfSpeech.VERB)
		);
			euristics.add(euristicVerbPrcl);
		Euristic euristicVerbInfn = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.PRCL),
			Euristic.any(PartOfSpeech.INFN)
		);
			euristics.add(euristicVerbInfn);
		Euristic euristicInfnVerb = Euristic.concat(
			Euristic.any(PartOfSpeech.INFN),
			Euristic.createConflictChecker(PartOfSpeech.VERB, PartOfSpeech.PRCL)
		);
			euristics.add(euristicInfnVerb);
		return euristics;
	}
	
	// союз - деепричастие ("хотя")
	public void test35() {
		Euristic matched = matched(getRulesList35(), "хотя пошел");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList35() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicGrndNoun = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.CONJ),
			Euristic.all(PartOfSpeech.NOUN, Case.ACCS)
		);
		euristics.add(euristicGrndNoun);
		Euristic euristicGrndNpro = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.CONJ),
			Euristic.all(PartOfSpeech.NPRO, Case.ACCS)
		);
		euristics.add(euristicGrndNpro);
		Euristic euristicGrndAdjf = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.GRND, PartOfSpeech.CONJ),
			Euristic.all(PartOfSpeech.ADJF, Case.ACCS)
		);
		euristics.add(euristicGrndAdjf);
		Euristic euristicNotGrndNpro = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.CONJ, PartOfSpeech.GRND),
			Euristic.not(Euristic.all(PartOfSpeech.NPRO, Case.ACCS))
		);
		euristics.add(euristicNotGrndNpro);
		Euristic euristicNotGrndNoun = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.CONJ, PartOfSpeech.GRND),
			Euristic.not(Euristic.all(PartOfSpeech.NOUN, Case.ACCS))
		);
		euristics.add(euristicNotGrndNoun);
		Euristic euristicNotGrndAdjf = Euristic.concat(
			Euristic.createConflictChecker(PartOfSpeech.CONJ, PartOfSpeech.GRND),
			Euristic.not(Euristic.all(PartOfSpeech.ADJF, Case.ACCS))
		);
		euristics.add(euristicNotGrndAdjf);
	return euristics;
	}
	
	public void test36() {
		Euristic matched = matched(getRulesList25(), "обилие белил");
		assertNotNull(matched);
	}
	
	public void test37() {
		Euristic matched = matched(getRulesList14(), "еду домой");
		assertNotNull(matched);
	}
	
	public void test38() {
		Euristic matched = matched(getRulesList14(), "в определённом");
		assertNotNull(matched);
	}
	
	public void test39() {
		String str = "шестьдесят минут прошло";
		Collection<List<IToken>> matched = getAllMatched(getFullRulesList(), str);
		printProcessingResult(str, matched);
		assertTrue(matched.size() > 0);
	}
		
	private void printProcessingResult(String str, Collection<List<IToken>> possibleChains) {
		System.out.println("//============Результаты разбора, строка '" + str +  "' ==================================================");
		if (possibleChains == null || possibleChains.isEmpty()) {
			System.out.println("Результатов не найдено");
			return;
		}
		for(List<IToken> chain : possibleChains){
			for (IToken token : chain) {
				System.out.print(token.getStartPosition() + "-" + token.getEndPosition() + " " + TokenTypeResolver.getResolvedType(token) + " " + token.getStringValue()+ " ");
			}
			System.out.println();
		}
	}
	
	private Collection<List<IToken>> getAllMatched(List<Euristic> euristicsToTry, String testString) {
		PrimitiveTokenizer pt = new PrimitiveTokenizer();
		WordFormParser wordFormParser = new WordFormParser(WordNetProvider.getInstance());
		List<IToken> tokens = pt.tokenize(testString);		
		List<IToken> processed = wordFormParser.process(tokens);
		List<List<IToken>> possibleChains = calcVariants(processed);
		return getAllMatched(euristicsToTry, possibleChains);
	}

	private Collection<List<IToken>> getAllMatched(
			List<Euristic> euristicsToTry, List<List<IToken>> possibleChains) {
		Set<List<IToken>> allMatched = new HashSet<List<IToken>>();
		for (Euristic euristic : euristicsToTry) {
			for (List<IToken> list : possibleChains) {
				if (euristic.match(list.toArray(new IToken[0]))) {
					allMatched.add(list);
				}
			}
		}
		return allMatched;
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
	
	private void printChain(String str, List<IToken> chain) {
		System.out.println("//============Результаты разбора, строка '" + str +  "' ==================================================");
		for (IToken token : chain) {
			if (token.hasConflicts() && Math.abs(token.getCorrelation()) < E) {
				boolean matchedAny = false;
				List<IToken> conflicts = token.getConflicts();
				for (IToken conflictToken : conflicts) {
					if (conflictToken.getCorrelation() > E) {
						matchedAny = true;
						break;
					}
				}
				if (!matchedAny) {
					System.out.println("Результатов не найдено");
					return;
				}
			}
			
		}
		for (int i = 0; i < chain.size(); i++) {
			IToken token = chain.get(i);
			if (hasConflicts(token)) {
				System.out.print(" Конфликт[ ");
				if (token.getCorrelation() > E) {
					printToken(token);
				}
				List<IToken> conflicts = token.getConflicts();
				for (IToken conflictToken : conflicts) {
					if (conflictToken.getCorrelation() > E) {
						System.out.print(" ");
						printToken(conflictToken);
					}
				}
				System.out.print(" ]");
			} else {
				printToken(getValidToken(token));
			}
			i += getConflictingCount(token);
		}
		System.out.println();
	}

	protected int getConflictingCount(IToken token) {
		if (token.getConflicts() == null) {
			return 0;
		}
		return token.getConflicts().size();
	}

	private IToken getValidToken(IToken token) {
		if (token.getCorrelation() > E || !token.hasConflicts()) {
			return token;
		}
		List<IToken> conflicts = token.getConflicts();
		for (IToken conflictToken : conflicts) {
			if (conflictToken.getCorrelation() > E) {
				return conflictToken;
			}
		}
		return null;
	}

	private boolean hasConflicts(IToken token) {
		if (!token.hasConflicts())
			return false;
		int validCount = token.getCorrelation() > E ? 0 : 1;
		List<IToken> conflicts = token.getConflicts();
		for (IToken conflictToken : conflicts) {
			if (conflictToken.getCorrelation() > E) {
				validCount++;
			}
		}
		return validCount > 1;
	}

	protected void printToken(IToken token) {
		System.out.print(token.getStartPosition() + "-" + token.getEndPosition() + " " + TokenTypeResolver.getResolvedType(token) + " " + token.getStringValue()+ " ");
	}
}
