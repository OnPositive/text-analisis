package com.onpositive.text.analysis.neural;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.MorphologicParser;
import com.onpositive.text.analysis.lexic.SymbolToken;
import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.syntax.SyntaxToken;
import com.onpositive.text.analysis.utils.MorphologicUtils;

import static com.onpositive.text.analysis.neural.NeuralConstants.*;

public class NeuralParser extends MorphologicParser {
	
	private BasicNetwork network;
	
	private IDataSetGenerator dataSetGenerator;
	
	public NeuralParser() {
		network = (BasicNetwork) EncogDirectoryPersistence.loadObject(getClass().getResourceAsStream("morphology.nnet"));
		dataSetGenerator = createDataSetGenerator();
	}

	@Override
	public List<IToken> processPlain(List<IToken> tokens) {
		doFiltering(tokens);
		tokens = tokens.stream().filter(token -> !(token instanceof SymbolToken)).collect(Collectors.toList());
		List<IToken> chain = MorphologicUtils.getWithNoConflicts(tokens);
		for (int i = 0; i < chain.size(); i++) {
			IToken token = chain.get(i);
			if (token instanceof WordFormToken && token.hasConflicts()) {
				List<List<IToken>> localVariants = getTrigrams(chain, i);
				int interestingIdx = i < TOKEN_WINDOW_SIZE / 2 ? i : Math.min(chain.size() - 1, TOKEN_WINDOW_SIZE / 2);
				if (i > chain.size() - (TOKEN_WINDOW_SIZE / 2 + 1)) {
					interestingIdx = i - Math.max(0,(chain.size() - TOKEN_WINDOW_SIZE));
				}
				double maxVal= -1;
				List<IToken> passedResult = null; 
				for (int j = 0; j < localVariants.size(); j++) {
					List<IToken> curResult = localVariants.get(j);
					double value = calcNeural(curResult);
					if (value > maxVal) {
						maxVal = value;
						passedResult = curResult;
					}
				}
				IToken passedToken = passedResult.get(interestingIdx);
				passedToken.setCorrelation(1,1);
				List<IToken> conflicts = passedToken.getConflicts();
				for (IToken failedToken : conflicts) {
					failedToken.setCorrelation(0, Double.POSITIVE_INFINITY);
				}
			} 
		}
		return tokens;
	}
	
//	protected void setFailedCorrelation(IToken token, double failedCorrelation) {
//		if (failedCorrelation > 0) {
//			token.setCorrelation(failedCorrelation,1);
//		} else {
//			token.setCorrelation(failedCorrelation,Double.POSITIVE_INFINITY);
//		}
//	}

	private double calcNeural(List<IToken> curResult) {
		double[] dataSet = getDataSet(curResult);
		double[] result = new double[1];
 		network.compute(dataSet, result);
 		return result[0];
	}

	protected IDataSetGenerator createDataSetGenerator() {
		return new BinaryDataSetGenerator();
	}

	@Override
	public boolean isRecursive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setHandleBounds(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IToken> getNewTokens() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTokenIdProvider(TokenIdProvider tokenIdProvider) {
		// TODO Auto-generated method stub

	}

	@Override
	public TokenIdProvider getTokenIdProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clean() {
		// TODO Auto-generated method stub

	}
	
	private double[] getDataSet(List<IToken> tokens) {
		if (TOKEN_WINDOW_SIZE < tokens.size()) {
			throw new IllegalArgumentException("tokens list to large, should be " + TOKEN_WINDOW_SIZE + " tokens at most");
		}
		int i = 0;
		int datasetSize = dataSetGenerator.getDatasetSize();
		double[] result = new double[TOKEN_WINDOW_SIZE * datasetSize];
		for (IToken curToken : tokens) {
			List<Grammem> allGrammems = new ArrayList<Grammem>();
			if (curToken instanceof WordFormToken) {
				((SyntaxToken) curToken).getGrammemSets().stream().forEach(grammem -> allGrammems.addAll(grammem.grammems()));
				double[] dataset = dataSetGenerator.generateDataset(allGrammems);
				for (int j = 0; j < dataset.length; j++) {
					result[i * datasetSize + j] = dataset[j];
				}
			}
			i++;
		}
		return result;
	}
	
	private double getProperty(Collection<Grammem> grammems, Class<?> propClass) {
		for (Grammem grammem : grammems) {
			if (propClass.isAssignableFrom(grammem.getClass())) {
				return grammem.intId - grammem.getInitialId() + 1;
			}
		}
		return 0;
	}

	private List<List<IToken>> getTrigrams(List<IToken> tokens, int index) {
		List<List<IToken>> result = new ArrayList<List<IToken>>();
		if (tokens.size() == 1) {
			IToken token = tokens.get(0);
			result.add(Collections.singletonList(token));
			List<IToken> conflicts = token.getConflicts();
			for (IToken curToken : conflicts) {
				result.add(Collections.singletonList(curToken));	
			}
			return result;
		}
		result.add(new ArrayList<IToken>());
		int subListStart = index - 1;
		if (index == 0) {
			subListStart = 0;
		}
		if (index == tokens.size() - 1) {
			subListStart = Math.max(0,tokens.size() - TOKEN_WINDOW_SIZE);
		}
		int subListEnd = Math.min(tokens.size(), subListStart + TOKEN_WINDOW_SIZE);
		if (subListEnd - subListStart < 2) {
			return Collections.emptyList();
		}
		for (int i = subListStart; i < subListEnd ; i++) {
			IToken curToken = tokens.get(i);
			if (curToken.hasConflicts()) {
				List<IToken> conflicts = new ArrayList<IToken>(curToken.getConflicts());
				conflicts.add(curToken);
				conflicts = conflicts.stream().filter(token -> !token.hasCorrelation() || token.getCorrelation() > 0).collect(Collectors.toList());
				result = generateVariants(result, conflicts);
			} else {
				addItem(result,curToken);
			}
//			List<IToken> curList = new ArrayList<IToken>();
//			for (int j = i; j < i + SEQUENCE_LENGTH; j++) {
//				curList.add(tokens.get(j));
//			}
//			result.add(curList);
		}
		return result;
	}

}
