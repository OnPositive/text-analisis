package com.onpositive.text.analisys.tests.neural;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.Perceptron;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.Gender;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.semantic.wordnet.Grammem.SingularPlural;
import com.onpositive.semantic.wordnet.Grammem.TransKind;
import com.onpositive.semantic.wordnet.Grammem.VerbKind;
import com.onpositive.semantic.wordnet.WordNetProvider;
import com.onpositive.text.analisys.tests.ParsedTokensLoader;
import com.onpositive.text.analisys.tests.euristics.SimplifiedToken;

public class Trainer {
	
	private static final int TOKEN_WINDOW_SIZE = 3;
	Class<? extends Grammem>[] props = (Class<? extends Grammem>[]) new Class<?>[] {PartOfSpeech.class, Gender.class, SingularPlural.class, VerbKind.class, TransKind.class }; 
	
	public void train() {
		int inputSize = TOKEN_WINDOW_SIZE * props.length;
		MultiLayerPerceptron perceptron = new MultiLayerPerceptron(inputSize, inputSize, inputSize, 1);
		File dir = new File("corpora");
		File[] listedFiles = dir.listFiles();
		AbstractWordNet wordNet = WordNetProvider.getInstance();
		DataSet dataSet = new DataSet(inputSize, 1);
		for (int i = 0; i < listedFiles.length / 2; i++) {
			File curFile = listedFiles[i];
			try {
				BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(curFile));
				ParsedTokensLoader loader = new ParsedTokensLoader(inputStream);
				List<SimplifiedToken> tokens = loader.getTokens();
				tokens = tokens.stream().filter(token -> token.hasValidGrammemSet()).collect(Collectors.toList());
				for (int j = 0; j < tokens.size(); j++) {
					List<List<SimplifiedToken>> trigrams = getTrigrams(tokens, j, wordNet);
					for (int k = 0; k < trigrams.size(); k++) {
						List<SimplifiedToken> trigram = trigrams.get(k);
						double desired = k == 0 ? 1: 0;
						dataSet.addRow(new DataSetRow(getDataSet(trigram), new double[]{desired}));
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		perceptron.learn(dataSet);
	}
	
	private List<List<SimplifiedToken>> getTrigrams(List<SimplifiedToken> tokens, int j, AbstractWordNet wordNet) {
		List<List<SimplifiedToken>> result = new ArrayList<List<SimplifiedToken>>();
		int subListStart = j - 1;
		if (j == 0) {
			subListStart = 0;
		}
		if (j == tokens.size() - 1) {
			subListStart = tokens.size() - TOKEN_WINDOW_SIZE;
		}
		List<SimplifiedToken> correctList = tokens.subList(subListStart, subListStart + TOKEN_WINDOW_SIZE);
		result.add(correctList);
		int interestingToken = j - subListStart;
		SimplifiedToken correctToken = correctList.get(interestingToken);
		GrammarRelation[] forms = wordNet.getPossibleGrammarForms(correctToken.getWord());
		if (forms != null &&  forms.length > 1) {
			PartOfSpeech correctPart = correctToken.getPartOfSpeech();
			for (GrammarRelation grammarRelation : forms) {
				if (!grammarRelation.hasGrammem(correctPart)) {
					List<SimplifiedToken> incorrectList = new ArrayList<SimplifiedToken>(correctList);
					incorrectList.remove(interestingToken);
					incorrectList.add(interestingToken, new SimplifiedToken(correctToken.getWord(), grammarRelation.getGrammems()));
					result.add(incorrectList);
				}
			}
		}
		return result;
	}

	private double[] getDataSet(List<SimplifiedToken> tokens) {
		int i = 0;
		double[] result = new double[tokens.size() * props.length];
		for (SimplifiedToken simplifiedToken : tokens) {
			Collection<Grammem> grammems = simplifiedToken.getGrammems();
			for (int j = 0; j < props.length; j++) {
				result[i * props.length + j] = getProperty(grammems, props[j]);
			}
			i++;
		}
		return result;
	}
	
	private double getProperty(Collection<Grammem> grammems, Class<?> propClass) {
		for (Grammem grammem : grammems) {
			if (propClass.isAssignableFrom(grammem.getClass())) {
				return grammem.intId;
			}
		}
		return 0;
	}
		
	
}
