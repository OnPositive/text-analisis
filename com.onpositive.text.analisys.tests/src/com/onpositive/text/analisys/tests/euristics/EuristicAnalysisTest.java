package com.onpositive.text.analisys.tests.euristics;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.stream.Collectors;

import junit.framework.TestCase;

import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analisys.tests.ParsedTokensLoader;
import com.onpositive.text.analisys.tests.util.TestingUtil;
import com.onpositive.text.analysis.Euristic;
import com.onpositive.text.analysis.EuristicAnalyzingParser;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.MorphologicParser;
import com.onpositive.text.analysis.filtering.AbbreviationsFilter;
import com.onpositive.text.analysis.filtering.AdditionalPartsPresetFilter;
import com.onpositive.text.analysis.lexic.SentenceSplitter;
import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.neural.NeuralParser;
import com.onpositive.text.analysis.rules.RuleSet;
import com.onpositive.text.analysis.syntax.SyntaxToken;

import static com.onpositive.text.analisys.tests.util.TestingUtil.*;

public class EuristicAnalysisTest extends TestCase{
	
	private static final String CORPORA_PATH = "D:\\Лена\\NoAmbig";

	static class ValueComparator<K, V extends Comparable<V>> implements Comparator<K> {
	
	    Map<K, V> map;
	
	    public ValueComparator(Map<K, V> base) {
	        this.map = base;
	    }
	
	    @Override
	    public int compare(K o1, K o2) {
	         return map.get(o2).compareTo(map.get(o1));
	    }
	}

	private static final int MAX_NEUTRALIZATION_LOOKAHEAD = 10;
	private static final double E = 0.001;
	private boolean PRINT_ALL_CONFLICTS = true;
	
	private Map<String, Integer> mistakesMap = new HashMap<String, Integer>();
	
	private Map<Euristic, Integer> usedEuristicsMap = new HashMap<Euristic, Integer>();
	
	private Map<Euristic, Integer> wrongEuristicsMap = new HashMap<Euristic, Integer>();
	
	ArrayList<Double> percents = new ArrayList<Double>();
	private List<Euristic> euristics;
	
//	public void test01() {
//		testNeuralWithFile("3344.xml");
//		testNeuralWithFile("2176.xml");
//		testNeuralWithFile("2241.xml");
//	}
	
//	public void test02() {
//		testWithFile("1551.xml");
//	}
	
	public void test03() {
		File folder = new File(CORPORA_PATH);
		globalTestForFolder(folder, false);
	}
	
//	public void test04() {
//		testEuristicWithFile(new File(CORPORA_PATH + "\\" + "993.xml"));
//		MorphologicParser euristicAnalyzingParser = configureDefaultAnalyzer(createRulesList());
//		List<IToken> processed = euristicAnalyzingParser.process(getWordFormTokens("Из квалифицированных рабочих и служащих"));
//		NeuralParser neuralParser = new NeuralParser();
//		useCurWord(neuralParser);
//		neuralParser.addTokenFilter(new AdditionalPartsPresetFilter());
//		neuralParser.addTokenFilter(new AbbreviationsFilter());
//		List<IToken> processed = neuralParser.process(getWordFormTokens("Шестьдесят минут прошло"));
//		TestingUtil.printChain(processed);
//	}

	protected void globalTestForFolder(File folder, boolean euristic) {
		if (folder.exists() && folder.isDirectory()) {
			File[] listedFiles = folder.listFiles();
			for (File file : listedFiles) {
				if (file.length() > 200000) {
					System.out.println("Файл " + file.getName());
					if (euristic) {
						testEuristicWithFile(file);
					} else {
						testNeuralWithFile(file);
					}
				}
			}
			
			int percentSum = 0;
			for (Double percent : percents) {
				percentSum += Math.round(percent);
			}
			
			System.out.println(String.format("** Суммарно верно снято для %1$,.2f процентов лексем", percentSum * 1.0 / percents.size()));
			
			ValueComparator<String, Integer> comparator = new ValueComparator<String, Integer> (mistakesMap);
		    Map<String, Integer> sortedMap = new TreeMap<String, Integer> (comparator);
		    sortedMap.putAll(mistakesMap);
		    
		    System.out.println("Частые ошибки:");
		    for (String word : sortedMap.keySet()) {
				int count = sortedMap.get(word);
				if (count > 10) {
					System.out.println(word + " - " + count + " раз");
				} else {
					break;
				}
			}
		    
		    if (!usedEuristicsMap.isEmpty()) {
		    	printEuristicsData(usedEuristicsMap, "Часто использовались эвристики:");
		    	printEuristicsData(wrongEuristicsMap, "Часто были ошибочными:");
		    }
		}
	}

	protected void printEuristicsData(Map<Euristic, Integer> euristicsMap, String title) {
		ValueComparator<Euristic, Integer> comparator1 = new ValueComparator<Euristic, Integer> (euristicsMap);
		Map<Euristic, Integer> sortedMap1 = new TreeMap<Euristic, Integer> (comparator1);
		sortedMap1.putAll(euristicsMap);
		System.out.println(title);
		for (Euristic curEuristic : sortedMap1.keySet()) {
			int count = sortedMap1.get(curEuristic);
			if (count > 3) {
				System.out.println(curEuristic + " - " + count + " раз");
			} else {
				break;
			}
		}
	}
		
	private void testNeuralWithFile(File file) {
		ParsedTokensLoader loader;
		try {
			loader = new ParsedTokensLoader(new BufferedInputStream(new FileInputStream(file)));
			testNeural(loader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void testNeuralWithFile(String filename) {
		ParsedTokensLoader loader = new ParsedTokensLoader(EuristicAnalysisTest.class.getResourceAsStream(filename));
		testNeural(loader);
	}

	protected void testNeural(ParsedTokensLoader loader) {
		List<SimplifiedToken> etalonTokens = loader.getTokens();
		String text = loader.getInitialText();
		NeuralParser neuralParser = new NeuralParser();
		useCurWord(neuralParser);
//		neuralParser.addTokenFilter(new IntjFilter());
		neuralParser.addTokenFilter(new AdditionalPartsPresetFilter());
		neuralParser.addTokenFilter(new AbbreviationsFilter());
		List<IToken> wordTokens = TestingUtil.getWordFormTokens(text);
		List<IToken> splitted = new SentenceSplitter().split(wordTokens);
//		EuristicAnalyzingParser euristicAnalyzingParser = new EuristicAnalyzingParser(RuleSet.getRulesList25());
//		euristicAnalyzingParser.process(splitted);
		neuralParser.process(splitted);
		compare(etalonTokens,wordTokens, neuralParser);
		System.out.println("//--------------------------------------------------------------------------------------------------");
	}

	protected void useCurWord(NeuralParser neuralParser) {
//		neuralParser.setUseCurWord(true);
//		try {
//			Scanner scanner = new Scanner(new File("specific.txt"));
//			while (scanner.hasNext()) {
//				neuralParser.addSpecificWord(scanner.next());
//			}
//			scanner.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	
	private void testEuristicWithFile(ParsedTokensLoader loader) {
		
		List<SimplifiedToken> etalonTokens = loader.getTokens();
		String text = loader.getInitialText();
		MorphologicParser euristicAnalyzingParser = TestingUtil.configureDefaultAnalyzer(createRulesList());
		List<IToken> wordTokens = TestingUtil.getWordFormTokens(text);
		List<IToken> processed = euristicAnalyzingParser.process(new SentenceSplitter().split(wordTokens));
		compare(etalonTokens,wordTokens, (EuristicAnalyzingParser) euristicAnalyzingParser);
//		System.out.println("//---------------------------------With sentences---------------------------------------------------");
//		List<IToken> tokens1 = TestingUtil.getWordFormTokens(text);
//		List<IToken> sentences = new SentenceSplitter().split(tokens1);
//		processed = euristicAnalyzingParser.process(sentences);
//		compare(etalonTokens,tokens1);
		System.out.println("//--------------------------------------------------------------------------------------------------");
	}
	
	private void testEuristicWithFile(File file) {
		ParsedTokensLoader loader;
		try {
			loader = new ParsedTokensLoader(new BufferedInputStream(new FileInputStream(file)));
			testEuristicWithFile(loader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void testEuristicWithFile(String filename) {
		System.out.println("Файл " + filename);
		ParsedTokensLoader loader = new ParsedTokensLoader(EuristicAnalysisTest.class.getResourceAsStream(filename));
		testEuristicWithFile(loader);
	}
	
	
	private List<Euristic> createRulesList() {
		if (euristics == null) {
			euristics = new ArrayList<Euristic>();
	//		euristics.addAll(RuleSet.getRulesList5());
			euristics.addAll(RuleSet.getRulesList6());
			euristics.addAll(RuleSet.getRulesList7());
			euristics.addAll(RuleSet.getRulesList8());
			euristics.addAll(RuleSet.getRulesList9());
			euristics.addAll(RuleSet.getRulesList10());
			euristics.addAll(RuleSet.getRulesList11());
			euristics.addAll(RuleSet.getRulesList12());
			euristics.addAll(RuleSet.getRulesList13());
			euristics.addAll(RuleSet.getRulesList14());
			euristics.addAll(RuleSet.getRulesList15());
			euristics.addAll(RuleSet.getRulesList16());
			euristics.addAll(RuleSet.getRulesList17());
			euristics.addAll(RuleSet.getRulesList18());
			euristics.addAll(RuleSet.getRulesList19());
			euristics.addAll(RuleSet.getRulesList20());
			euristics.addAll(RuleSet.getRulesList21());
			euristics.addAll(RuleSet.getRulesList22());
			euristics.addAll(RuleSet.getRulesList23());
			euristics.addAll(RuleSet.getRulesList24());
			euristics.addAll(RuleSet.getRulesList25());
			euristics.addAll(RuleSet.getRulesList26());
			euristics.addAll(RuleSet.getRulesList27());
			euristics.addAll(RuleSet.getRulesList28());
			euristics.addAll(RuleSet.getRulesList29());
	//		euristics.addAll(RuleSet.getRulesList30());
	//		euristics.addAll(RuleSet.getRulesList31());
			euristics.addAll(RuleSet.getRulesList32());
			euristics.addAll(RuleSet.getRulesList33());
			euristics.addAll(RuleSet.getRulesList34());
	//		euristics.addAll(RuleSet.getRulesList35());
			euristics.addAll(RuleSet.getRulesList36());
			euristics.addAll(RuleSet.getRulesList37());
			euristics.addAll(RuleSet.getRulesList38());
			euristics.addAll(RuleSet.getRulesList39());
			euristics.addAll(RuleSet.getRulesList40());
			euristics.addAll(RuleSet.getRulesList41());
			euristics.addAll(RuleSet.getRulesList42());
			euristics.addAll(RuleSet.getRulesList43());
			euristics.addAll(RuleSet.getRulesList44());
		}
		return euristics;
	}

	@SuppressWarnings("unchecked")
	private void compare(List<SimplifiedToken> etalonTokens, List<IToken> tokens, MorphologicParser parser) {
		EuristicAnalyzingParser euristicParser = parser instanceof EuristicAnalyzingParser?(EuristicAnalyzingParser)parser:null;
		int i = 0;
		int j = 0;
		int filteredByFilters = parser.getFilteredCount();
		ITokenComparator tokenComparator = new PartOfSpeechComparator();
		Map<PartOfSpeech, Integer> comparedCounts = new HashMap<PartOfSpeech, Integer>();
		int conflictingCount = 0;
		int wrongCount = 0;
		int notFoundCount = 0;
		long filteredOut = 0;
		while (i < etalonTokens.size() && j < tokens.size()) {
			SimplifiedToken etalonToken = etalonTokens.get(i);
			List<WordFormToken> comparedTokens = new ArrayList<WordFormToken>();
			j = getNextWordTokenIdx(tokens, j, etalonToken);
			if (j == -1) {
				printComparisonResults(comparedCounts, i, conflictingCount, wrongCount, filteredOut, filteredByFilters, notFoundCount);
				return;
			}
			WordFormToken wordFormToken = (WordFormToken) tokens.get(j);
			if (!etalonToken.wordEquals(wordFormToken)) {
				i = tryEtalonNeutralization(i, etalonTokens, wordFormToken);
				if (i == -1) {
					printComparisonResults(comparedCounts, i, conflictingCount, wrongCount, filteredOut, filteredByFilters, notFoundCount);
					return;
				} else {
					etalonToken = etalonTokens.get(i);
				}
			}
			
			comparedTokens.add(wordFormToken);
			List<IToken> conflicts = wordFormToken.getConflicts();
			if (conflicts != null) {
				comparedTokens.addAll((Collection<? extends WordFormToken>) conflicts);
				j += conflicts.size();
			}
			if (comparedTokens.size() == 1) {
				boolean wordEquals = etalonToken.wordEquals(comparedTokens.get(0));
				if (!wordEquals) {
					System.out.println("Несовпадение слова: нужно " + etalonToken.getWord() + " найдено " + comparedTokens.get(0).getStringValue());
				}
			} else {
				if (PRINT_ALL_CONFLICTS) {
					System.out.println("Неоднозначность: слово " + wordFormToken.getShortStringValue() + " фраза " + getPhrase(etalonTokens, i));
				}
				if (comparedTokens.size() > 1) {
					conflictingCount++;
				}				
				filteredOut += comparedTokens.stream().filter(token -> token.getCorrelation() <= E).count();
				comparedTokens = comparedTokens.stream().filter(token -> token.getCorrelation() > E).collect(Collectors.toList());
				if (comparedTokens.size() > 1 && !isSamePart(comparedTokens)) {
					wrongCount++;
					handleWrongEuristics(etalonToken, comparedTokens, tokenComparator, parser);
					StringJoiner joiner = new StringJoiner(", ");
					comparedTokens.stream().forEach(token -> joiner.add(token.toString()));
					System.out.println("Осталось несколько вариантов: " + joiner.toString());
					handleMistake(etalonToken);
					if (euristicParser != null) {
						if (euristicParser.getMatchedEuristic(comparedTokens.get(0)) == null) {
							System.out.println("Правила не найдено");
							notFoundCount++;
						} else {
							StringJoiner joiner1 = new StringJoiner(", ");
							comparedTokens.stream().map(token -> euristicParser.getMatchedEuristic(token)).forEach(euristic ->joiner1.add(euristic != null ? euristic.toString():"ОШИБКА")); 
							System.out.println("Правила: " + joiner1.toString());
						}
					}
				} else if (comparedTokens.size() == 1) {
					WordFormToken token = comparedTokens.get(0);
					boolean wordEquals = etalonToken.wordEquals(token);
					if (!wordEquals) {
						System.out.println("Несовпадение слова: нужно " + etalonToken.getWord() + " найдено " + token.getShortStringValue());
					}
					incrementCount(comparedCounts, token.getPartOfSpeech());
					Collection<Grammem> wrongGrammems = tokenComparator.calculateWrong(etalonToken, token);
					if (!wrongGrammems.isEmpty()) {
						if (euristicParser != null) {
							handleWrongEuristic(euristicParser.getMatchedEuristic(token));
						}
						System.out.println("Ошибочное определение для слова " + etalonToken + " во фразе " + getPhrase(etalonTokens, i));
						handleMistake(etalonToken);
						String wrongTxt = "Неверные граммемы: " + wrongGrammems;
						if (euristicParser != null && euristicParser.getMatchedEuristic(token) != null) {
							wrongTxt += " Эвристика: " + euristicParser.getMatchedEuristic(token);
						}
						System.out.println(wrongTxt);
						wrongCount++;
					} else if (parser instanceof EuristicAnalyzingParser){
						handleGoodEuristic(((EuristicAnalyzingParser) parser).getMatchedEuristic(token));
					}
						
				}
			}
			i++;
			j++;
			
		}
		printComparisonResults(comparedCounts, i, conflictingCount, wrongCount, filteredOut, filteredByFilters, notFoundCount);
	}

	private void handleWrongEuristics(SimplifiedToken etalonToken, List<WordFormToken> comparedTokens, ITokenComparator tokenComparator, MorphologicParser parser) {
		if (parser instanceof EuristicAnalyzingParser) {
			for (SyntaxToken curToken : comparedTokens) {
				if (!tokenComparator.calculateWrong(etalonToken, curToken).isEmpty()) {
					Euristic matchedEuristic = ((EuristicAnalyzingParser) parser).getMatchedEuristic(curToken);
					handleWrongEuristic(matchedEuristic);
				}
			}
		}
	}

	private void handleWrongEuristic(Euristic matchedEuristic) {
		if (matchedEuristic == null) {
			return;
		}
		Integer count = wrongEuristicsMap.get(matchedEuristic);
		if (count == null) {
			count = 1;
		} else {
			count++;
		}
		wrongEuristicsMap.put(matchedEuristic, count);
	}
	
	private void handleGoodEuristic(Euristic matchedEuristic) {
		if (matchedEuristic == null) {
			return;
		}
		Integer count = usedEuristicsMap.get(matchedEuristic);
		if (count == null) {
			count = 1;
		} else {
			count++;
		}
		usedEuristicsMap.put(matchedEuristic, count);
	}

	private void handleMistake(SimplifiedToken etalonToken) {
		String word = etalonToken.getWord().toLowerCase().trim();
		Integer count = mistakesMap.get(word);
		if (count == null) {
			count = 1;
		} else {
			count++;
		}
		mistakesMap.put(word, count);
	}

	private String getPhrase(List<SimplifiedToken> etalonTokens, int j) {
		SimplifiedToken curToken = etalonTokens.get(j);
		String sentenceId = curToken.getSentenceId();
		StringBuilder builder = new StringBuilder();
		if (j > 0 && (sentenceId == null || sentenceId.equals(etalonTokens.get(j - 1).getSentenceId()))) {
			builder.append(etalonTokens.get(j - 1).getWord());
			builder.append(" ");
		}
		builder.append(curToken.getWord());
		if (j < etalonTokens.size() - 1 && (sentenceId == null || sentenceId.equals(etalonTokens.get(j + 1).getSentenceId()))) {
			builder.append(" ");
			builder.append(etalonTokens.get(j + 1).getWord());
		}
		return builder.toString();
	}

	private boolean isSamePart(List<WordFormToken> comparedTokens) {
		WordFormToken first = comparedTokens.get(0);
		PartOfSpeech partOfSpeech = first.getPartOfSpeech();
		for (int i = 1; i < comparedTokens.size(); i++) {
			if (comparedTokens.get(i).getPartOfSpeech() != partOfSpeech) {
				return false;
			}
		}
		return true;
	}

	protected void printComparisonResults(Map<PartOfSpeech, Integer> comparedCounts, int comparedCount, int conflictingCount, int wrongCount, long filteredOut, int filteredByFilters, int notFoundCount) {
		System.out.println("** Всего сравнили " + comparedCount + " лексем **");
		System.out.println("** Из них с омонимией: " + conflictingCount + " лексем **");
		double percent = (conflictingCount - wrongCount) * 100.0 / conflictingCount;
		percents.add(percent);
		System.out.println(String.format("** Верно снято для %1$,.2f процентов лексем **", percent));
		System.out.println("** Исключено: " + filteredOut + " лексем **");
		System.out.println("** Из них фильтром: " + filteredByFilters + "шт.**");
		System.out.println("** Не найдено правил для: " + notFoundCount + "шт.**");
		System.out.println("** Части речи: ");
		comparedCounts.keySet().stream().sorted((part1, part2) -> {return part1.intId - part2.intId;}).forEach( part -> {
			System.out.println("** " + part.description + " = " + comparedCounts.get(part));
		});
	}

	private void incrementCount(Map<PartOfSpeech, Integer> comparedCounts, PartOfSpeech partOfSpeech) {
		if (partOfSpeech == null) {
			return;
		}
		Integer count = comparedCounts.get(partOfSpeech);
		if (count == null) {
			count = 1;
		} else {
			count++;
		}
		comparedCounts.put(partOfSpeech, count);
	}

	private int tryEtalonNeutralization(int i, List<SimplifiedToken> etalonTokens, SyntaxToken wordFormToken) {
		i++;
		for (int k = i; k < i + MAX_NEUTRALIZATION_LOOKAHEAD && k < etalonTokens.size(); k++) {
			SimplifiedToken curToken = etalonTokens.get(k);
			if (curToken.wordEquals(wordFormToken)) {
				return k;
			}
		}
		return -1;
	}

	protected int getNextWordTokenIdx(List<IToken> tokens, int j, SimplifiedToken etalonToken) {
		j = skipNonWordTokens(tokens, j);
		if (j == -1) {
			return -1;
		}
		int lookahead = 0;
		SyntaxToken token = (SyntaxToken) tokens.get(j);
		while (j > -1 && lookahead < MAX_NEUTRALIZATION_LOOKAHEAD && !etalonToken.wordEquals(token)) {
			j++;
			lookahead++;
			j = skipNonWordTokens(tokens, j);
			if (j == -1) {
				return -1;
			}
			token = (SyntaxToken) tokens.get(j);
		}
		if (lookahead == MAX_NEUTRALIZATION_LOOKAHEAD) {
			return -1;
		}
		return j;
	}

	protected int skipNonWordTokens(List<IToken> tokens, int j) {
		while (j < tokens.size() && !(tokens.get(j) instanceof WordFormToken)) {			
			j++;
		}
		if (j == tokens.size()) {
			return -1;
		}
		return j;
	}
	
}
