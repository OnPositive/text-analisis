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
import com.onpositive.semantic.wordnet.Grammem.Personality;
import com.onpositive.semantic.wordnet.Grammem.SingularPlural;
import com.onpositive.semantic.wordnet.Grammem.Time;
import com.onpositive.semantic.wordnet.TextElement;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.Grammem.VerbKind;
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
		String str = "меж тем";
		List<IToken> tokens = pt.tokenize(str);		
		List<IToken> processed = wfParser.process(tokens);
		
		System.out.println("----------------------------------------------------------------------------------------");
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
	
	public void testConflicting() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristic1 = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.and(Euristic.conflicting(PartOfSpeech.VERB, PartOfSpeech.NOUN), Euristic.any(PartOfSpeech.NOUN))
				);
		euristics.add(euristic1);
		Euristic matched = matched(euristics, "налить белил");
		assertNotNull(matched);
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
	
	public void test03() {
		List<Euristic> rulesList1 = getRulesList1();
		Euristic matched = matched(rulesList1, "сделал чертёж");
		assertNotNull(matched);
	}
	
	public void test04() {
		List<Euristic> rulesList1 = getRulesList1();
		Euristic matched = matched(rulesList1, "покрасил лестницу");
		assertNotNull(matched);
	}
	
	public void test05() { // ...
		
		Euristic matched = matched(getRulesList5(), "меж двух");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList5() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicPrepWord = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.word("тем", FeaturesGramem.Apro)
				);
		euristics.add(euristicPrepWord);
		Euristic euristicPrepNumR = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.any(PartOfSpeech.NUMR)
				);
		euristics.add(euristicPrepNumR);
		Euristic euristicPrepPrInst = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.all((PartOfSpeech.NPRO), (Case.ABLT))
				);
		euristics.add(euristicPrepPrInst);
		Euristic euristicPrepPrGen = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.all((PartOfSpeech.NPRO), (Case.GENT))
				);
		euristics.add(euristicPrepPrGen);
		Euristic euristicPrepNounInst = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.all((PartOfSpeech.NOUN), (Case.ABLT))
				);
		euristics.add(euristicPrepNounInst);
		Euristic euristicPrepNounGen = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.all((PartOfSpeech.NOUN), (Case.GENT))
				);
		euristics.add(euristicPrepNounGen);
		return euristics;
	}
	
	// глагол - местоимение
	public void test06() {
		
		Euristic matched = matched(getRulesList6(), "моя машина");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList6() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicNounNpro = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.any(FeaturesGramem.Apro)
				);
		euristics.add(euristicNounNpro);
		Euristic euristicAdjNpro = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.any(FeaturesGramem.Apro)
				);
		euristics.add(euristicAdjNpro);
		Euristic euristicPrepNpro = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.any(FeaturesGramem.Apro)
				);
		euristics.add(euristicPrepNpro);
		Euristic euristicNproVerb = Euristic.concat(
				Euristic.any(FeaturesGramem.Apro),
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicNproVerb);
		Euristic euristicNproNoun = Euristic.concat(
				Euristic.any(FeaturesGramem.Apro),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicNproNoun);
		Euristic euristicNproAdj = Euristic.concat(
				Euristic.any(FeaturesGramem.Apro),
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
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicPrepNoun);
		Euristic euristicAdjfNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicAdjfNoun);
		Euristic euristicVerbInf = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.INFN)
				);
		euristics.add(euristicVerbInf);
		Euristic euristicNounInf = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.INFN)
				);
		euristics.add(euristicNounInf);
		Euristic euristicPredInf = Euristic.concat(
				Euristic.any(PartOfSpeech.PRED),
				Euristic.any(PartOfSpeech.INFN)
				);
		euristics.add(euristicPredInf);
		Euristic euristicInfInf = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.any(PartOfSpeech.INFN)
				);
		euristics.add(euristicInfInf);
		Euristic euristicInfNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.all((PartOfSpeech.NOUN), Case.ACCS)
				);
		euristics.add(euristicInfNoun);
		Euristic euristicInfNpro = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.any(PartOfSpeech.NPRO)
				);
		euristics.add(euristicInfNpro);
		Euristic euristicInfPrep = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicInfPrep);
		return euristics;		
	}
	
	private List<Euristic> getRulesList1() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		euristics.add(Euristic.concat(
				Euristic.all(PartOfSpeech.VERB, VerbKind.PERFECT),
				Euristic.any(PartOfSpeech.NOUN)
				));
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
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicNumrNoun);
		Euristic euristicPrepNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicPrepNoun);
		Euristic euristicAdjNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicAdjNoun);
		Euristic euristicNproNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicNproNoun);
		Euristic euristicVerbNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicVerbNoun);
		Euristic euristicProNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, Extras.Anph, FeaturesGramem.Apro),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicProNoun);
		Euristic euristicInfNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicInfNoun);
		Euristic euristicGrndNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.GRND),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicGrndNoun);
		Euristic euristicNounPrep = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicNounPrep);
		Euristic euristicNounAdj = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.ADJF)
				);
		euristics.add(euristicNounAdj);
		Euristic euristicNounVerb = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicNounVerb);
		Euristic euristicNounNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.all(PartOfSpeech.NOUN, Case.DATV)
				);
		euristics.add(euristicNounNoun);
		Euristic euristicNounPred = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.PRED)
				);
		euristics.add(euristicNounPred);
		Euristic euristicNproAdj = Euristic.concat(
				Euristic.any(PartOfSpeech.NPRO),
				Euristic.any(PartOfSpeech.ADJS)
				);
		euristics.add(euristicNproAdj);
		Euristic euristicNounAdjs = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN), 
				Euristic.any(PartOfSpeech.ADJS)
				);
		euristics.add(euristicNounAdjs);
		Euristic euristicAdvbAdjs = Euristic.concat(
				Euristic.any(PartOfSpeech.ADVB),
				Euristic.any(PartOfSpeech.ADJS)
				);
		euristics.add(euristicAdvbAdjs);
		Euristic euristicAdjsNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicAdjsNoun);
		Euristic euristicAdjsVerb = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicAdjsVerb);
		Euristic euristicAdjsAdvb = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJS),
				Euristic.any(PartOfSpeech.ADVB)
				);
		euristics.add(euristicAdjsAdvb);
		return euristics;				
	}
	
	// прилательное - существительное
	public void test09() {
		Euristic matched = matched(getRulesList9(), "на глубоком");
		assertNotNull(matched);
	}
	
	private List<Euristic> getRulesList9() {
		List<Euristic> euristics = new ArrayList<Euristic>();
		Euristic euristicPrepNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicPrepNoun);
		Euristic euristicAdjNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF), 
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicAdjNoun);
		Euristic euristicVerbNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB), 
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicVerbNoun);
		Euristic euristicProNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, Extras.Anph, FeaturesGramem.Apro), 
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicProNoun);
		Euristic euristicNumrNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.NUMR), 
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicNumrNoun);
		Euristic euristicNounNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN), 
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicNounNoun);
		Euristic euristicNounPrep = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN), 
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicNounPrep);
		Euristic euristicNounNounG = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN), 
				Euristic.all(PartOfSpeech.NOUN, Case.GENT)
				);
		euristics.add(euristicNounNounG);
		Euristic euristicNounVerb = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN), 
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicNounVerb);
		Euristic euristicPrepAdj = Euristic.concat(
				Euristic.or(Euristic.word("на", PartOfSpeech.PREP), Euristic.word("о", PartOfSpeech.PREP), Euristic.word("в", PartOfSpeech.PREP)), 
				Euristic.any(PartOfSpeech.ADJF)
				);
		euristics.add(euristicPrepAdj);
		Euristic euristicVerbAdj = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB), 
				Euristic.any(PartOfSpeech.ADJF)
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
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicAdjNoun);
		Euristic euristicPrepNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.PREP),
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicPrepNoun);
		Euristic euristicProNoun = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, Extras.Anph, FeaturesGramem.Apro), 
				Euristic.any(PartOfSpeech.NOUN)
				);
		euristics.add(euristicProNoun);
		Euristic euristicVerbComp = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.COMP)
				);
		euristics.add(euristicVerbComp);
		Euristic euristicNounComp = Euristic.concat(
				Euristic.any(PartOfSpeech.NOUN),
				Euristic.any(PartOfSpeech.COMP)
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
				Euristic.any(PartOfSpeech.GRND)
				);
		euristics.add(euristicAdvbGrnd);
		Euristic euristicGrndPrep = Euristic.concat(
				Euristic.any(PartOfSpeech.GRND),
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicGrndPrep);
		Euristic euristicGrndAdvb = Euristic.concat(
				Euristic.any(PartOfSpeech.GRND),
				Euristic.any(PartOfSpeech.ADVB)
				);
		euristics.add(euristicGrndAdvb);
		Euristic euristicGrndNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.GRND),
				Euristic.all(PartOfSpeech.NOUN, Case.ACCS)
				);
		euristics.add(euristicGrndNoun);
		Euristic euristicGrndInf = Euristic.concat(
				Euristic.any(PartOfSpeech.GRND),
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
				Euristic.any(PartOfSpeech.ADJF)
				);
		euristics.add(euristicPrepAdjf);
		Euristic euristicNounAdjf = Euristic.concat(
				Euristic.all(PartOfSpeech.NOUN, Case.ACCS),
				Euristic.any(PartOfSpeech.ADJF)
				);
		euristics.add(euristicNounAdjf);
		Euristic euristicNproAdjf = Euristic.concat(
				Euristic.all(PartOfSpeech.ADJF, FeaturesGramem.Apro),
				Euristic.any(PartOfSpeech.ADJF)
				);
		euristics.add(euristicNproAdjf);
		Euristic euristicVerbAdjf = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.ADJF)
				);
		euristics.add(euristicVerbAdjf);
		Euristic euristicInfAdjf = Euristic.concat(
				Euristic.any(PartOfSpeech.INFN),
				Euristic.any(PartOfSpeech.ADJF)
				);
		euristics.add(euristicInfAdjf);
		Euristic euristicAdjAdjf = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
				Euristic.any(PartOfSpeech.ADJF)
				);
		euristics.add(euristicAdjAdjf);
		Euristic euristicAdjfNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.ADJF),
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
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicAdvbVerb);
		Euristic euristicVerbAdvb = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.ADVB)
				);
		euristics.add(euristicVerbAdvb);
		Euristic euristicVerbPrep = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
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
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicNounVerb);
		Euristic euristicVerbNoun = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.all(PartOfSpeech.NOUN, Case.ACCS)
				);
		euristics.add(euristicVerbNoun);
		Euristic euristicVerbPrep = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.PREP)
				);
		euristics.add(euristicVerbPrep);
		Euristic euristicVerbAdvb = Euristic.concat(
				Euristic.any(PartOfSpeech.VERB),
				Euristic.any(PartOfSpeech.ADVB)
				);
		euristics.add(euristicVerbAdvb);
		Euristic euristicGrndAdvb = Euristic.concat(
				Euristic.any(PartOfSpeech.GRND),
				Euristic.any(PartOfSpeech.ADVB)
				);
		euristics.add(euristicGrndAdvb);
		Euristic euristicAdvbVerb = Euristic.concat(
				Euristic.any(PartOfSpeech.ADVB),
				Euristic.any(PartOfSpeech.VERB)
				);
		euristics.add(euristicAdvbVerb);
		Euristic euristicAdvbGrnd = Euristic.concat(
				Euristic.any(PartOfSpeech.ADVB),
				Euristic.any(PartOfSpeech.GRND)
				);
		euristics.add(euristicAdvbGrnd);
		return euristics;
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
