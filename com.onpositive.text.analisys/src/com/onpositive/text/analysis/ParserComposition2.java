package com.onpositive.text.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.onpositive.text.analysis.IToken.Direction;
import com.onpositive.text.analysis.syntax.SyntaxToken;

public class ParserComposition2 extends ParserComposition {
	
	public ParserComposition2(boolean isGloballyRecursive, IParser... parsers) {
		super(isGloballyRecursive,parsers);
		setHandleBoundsFalse();
		initRegistry();
	}
	
	public ParserComposition2(IParser... parsers) {
		super(parsers);
		setHandleBoundsFalse();
		initRegistry();
	}
	
	protected TokenModificationRegistry reg;


	protected void initRegistry() {
		reg = new TokenModificationRegistry(this.parsers);
	}
	
	
	private void setHandleBoundsFalse() {
		for(IParser ap : this.parsers){
			ap.setHandleBounds(false);
		}
	}
	
	
	@Override
	public List<IToken> process(List<IToken> tokens) {
		
		List<IToken> result = reg.process(tokens);
		return result;
	}
	
	protected static class ParserData{
		
		
		public ParserData(TokenModificationRegistry reg, IParser ap, int id) {
			super();
			this.reg = reg;
			this.parser = ap;
			this.parserId = id;
		}

		private final TokenModificationRegistry reg;
		
		private final IParser parser;
		
		private final int parserId;
		
		private List<IToken> result;

		private boolean isFinished = false;
		
		public List<IToken> process(List<IToken> tokens){			
			
			result = parser.process(tokens);
			processModificationData();
			return result;
		}
		
		public List<IToken> getNewTokens(){
			return parser.getNewTokens();
		}
		
		
		public void processModificationData() {
			
			List<IToken> newTokens = getNewTokens();
			
			for( IToken token : newTokens){
				
				IToken mainGroup = null;
				if(token instanceof SyntaxToken){
					mainGroup = ((SyntaxToken)token).getMainGroup();
				}
				List<IToken> children = token.getChildren();				
				for(IToken ch : children){
					int modType = ( mainGroup == null || ch !=mainGroup )
							? TokenModificationData.CONSUMED : TokenModificationData.ENRICHED;					
					
					TokenModificationData md = new TokenModificationData(reg, ch, token, parserId, modType);
					reg.registerData(ch.id(),md);
				}
			}
			
		}

		public List<IToken> getResult() {
			return result;
		}

		public void setResult(List<IToken> result) {
			this.result = result;
		}

		public boolean isFinished() {
			return isFinished ;
		}
		
		public void setFinished(boolean isFinished) {
			this.isFinished = isFinished;
		}

		public void setNeedRepeat(){
			this.isFinished = false;
		}
		
		@Override
		public String toString() {
			String name = parser.getClass().getSimpleName();
			StringBuilder bld = new StringBuilder(name);
			if(isFinished){
				bld.append(", finished");
			}
			return bld.toString();
		}
	}
	
	
	protected static class TokenModificationData{
		
		public TokenModificationData(TokenModificationRegistry reg, IToken token, IToken producedToken, int parserId, int modType) {
			super();
			this.reg = reg;
			this.parserId = parserId;
			this.producedToken = producedToken;
			this.modType = modType;
			this.token = token;
		}
		
		
		private static int CONSUMED = 0;
		
		private static int ENRICHED = 1;
		
		private TokenModificationRegistry reg;
		
		private int parserId;
		
		private IToken token;
		
		private IToken producedToken;

		private int modType;
		
		private boolean isCanceled = false;

		public int getParserId() {
			return parserId;
		}

		public void setParserId(int parserId) {
			this.parserId = parserId;
		}

		public int getModType() {
			return modType;
		}

		public void setModType(int modType) {
			this.modType = modType;
		}

		public IToken getProducedToken() {
			return producedToken;
		}

		public boolean isCanceled() {
			return isCanceled;
		}

		public void setCanceled(boolean isCanceled, int validParserId) {
			this.isCanceled = isCanceled;
			if(isCanceled && validParserId != parserId){
				reg.enableParserRepeat(parserId);
				reg.cancelProducedToken(producedToken.id());
			}
		}

		public boolean isEnriched() {
			return getModType() == ENRICHED;
		}
		
		@Override
		public String toString() {
			return token.toString() + " -> " + producedToken.toString();
		}
	}
	
	protected static class TokenModificationRegistry{
		
		private ArrayList<ParserData> parserDataList = new ArrayList<ParserData>();
		
		private IntObjectOpenHashMap<List<TokenModificationData>> map
			= new IntObjectOpenHashMap<List<TokenModificationData>>();
		
		private List<IToken> original;
		
		private List<IToken> currentTokensArray;
		
		private IntOpenHashSet cancelledProducedTokens = new IntOpenHashSet();
		
		private HashSet<IToken> modifiedTokens = new HashSet<IToken>();
		
		public void cancelProducedToken(int id){
			cancelledProducedTokens.add(id);
		}

		public TokenModificationRegistry(IParser[] parsers) {
			
			
			for(int i = 0 ; i < parsers.length ; i++){
				ParserData pd = new ParserData(this,parsers[i],i);
				parserDataList.add(pd);
			}
		}

		public void enableParserRepeat(int parserId) {
			
			int size = parserDataList.size();
			if(parserId<0||parserId>size-1){
				return;
			}
			parserDataList.get(parserId).setNeedRepeat();
			
		}

		public List<IToken> process(List<IToken> tokens) {
			
			prepare(tokens);
			
			this.original = tokens;
			this.currentTokensArray = this.original;
			
			while(true){

				for( ParserData pd : parserDataList){
					if(pd.isFinished()){
						continue;
					}
					pd.process(this.currentTokensArray);
					pd.setFinished(true);
				}
				
				resolveConflicts();
				
				this.currentTokensArray = collectTokens();				
				boolean finished = finished();
				if(finished){
					break;
				}
				clean();
			}
			return this.currentTokensArray;
		}

		private List<IToken> collectTokens() {
			
			IntOpenHashSet ignoreSet = new IntOpenHashSet();
			IntObjectOpenHashMap<IToken> newTokensMap = new IntObjectOpenHashMap<IToken>();
			IntObjectOpenHashMap<IToken> resultTokensMap = new IntObjectOpenHashMap<IToken>();
			
			for(ParserData pd : parserDataList){
				List<IToken> newTokens = pd.getNewTokens();
				for(IToken token : newTokens){
					int id = token.id();
					if(cancelledProducedTokens.contains(id)){
						ignoreSet.add(id);
						continue;
					}
					else{
						newTokensMap.put(id,token);
						//if(!token.isDoubtful()){
							List<IToken> children = token.getChildren();						
							modifiedTokens.addAll(children);
						//}
					}
				}
			}
			
			markPreservedTokens(newTokensMap,resultTokensMap);
			
			ArrayList<IToken> result = new ArrayList<IToken>();
			result.addAll(Arrays.asList(resultTokensMap.values().toArray(IToken.class)));
			result.addAll(Arrays.asList(newTokensMap.values().toArray(IToken.class)));
			Collections.sort(result, new Comparator<IToken>() {

				@Override
				public int compare(IToken o1, IToken o2) {
					return o1.getStartPosition()-o2.getStartPosition();
				}
			});
			
			resultTokensMap.putAll(newTokensMap);
			
			TokenBoundsHandler tbh = new TokenBoundsHandler();
			tbh.setNewTokens(newTokensMap);
			tbh.setResultTokens(resultTokensMap);
			tbh.handleBounds(result);
			
			TokenBoundsHandler.discardTokens(new ArrayList<IToken>(modifiedTokens));			
			return result;
		}

		protected void markPreservedTokens(	IntObjectOpenHashMap<IToken> newTokensMap, IntObjectOpenHashMap<IToken> resultTokensMap) {
			for(IToken t : currentTokensArray){
				if(!modifiedTokens.contains(t)){
					resultTokensMap.put(t.id(), t);
				}
			}
			
			int size = 0;
			while(size != resultTokensMap.size()){
				size = resultTokensMap.size();
				for(IToken newToken : newTokensMap.values().toArray(IToken.class))
				{
					List<IToken> children = newToken.getChildren();
					int childrenCount = children.size();
					if(childrenCount==1){
						continue;
					}
					
					IToken first = children.get(0);
					IToken last = children.get(childrenCount-1);
					checkNeighbours(resultTokensMap, first, Direction.END);
					for(int i = 1; i < childrenCount-1 ; i++ ){
						IToken ch = children.get(i);
						checkNeighbours(resultTokensMap, ch,Direction.END);
						checkNeighbours(resultTokensMap, ch,Direction.START);
					}
					checkNeighbours(resultTokensMap, last, Direction.START);
				}
			}
		}

		private void checkNeighbours(IntObjectOpenHashMap<IToken> resultTokensMap, IToken token, Direction dir) {
			
			IToken neighbour = token.getNeighbour(dir);
			if(neighbour!=null){
				if(!modifiedTokens.contains(neighbour)){
					resultTokensMap.put(token.id(), token);
					modifiedTokens.remove(token);
				}
			}
			else{
				List<IToken> neighbours = token.getNeighbours(dir);
				if(neighbours!=null){
					for(IToken n : neighbours){
						if(!modifiedTokens.contains(n)){
							resultTokensMap.put(token.id(), token);
							modifiedTokens.remove(token);
							break;
						}						
					}
				}
			}
			
		}

		private void prepare(List<IToken> tokens) {
			
			clean();			
			this.original = null;
			for(ParserData pd : this.parserDataList){
				pd.setFinished(false);
			}
		}

		protected void clean() {
			this.map.clear();
			this.cancelledProducedTokens.clear();
			this.modifiedTokens.clear();
		}

		protected void resolveConflicts() {
			
			for(IToken t : currentTokensArray){
				
				List<TokenModificationData> dataList = map.get(t.id());
				if(dataList==null||dataList.size()<2){
					continue;
				}
				resolveConflict(dataList);
			}
			
		}

		protected void resolveConflict(List<TokenModificationData> dataList) {
			
			List<TokenModificationData> enriched = new ArrayList<TokenModificationData>();
			for(TokenModificationData tmd : dataList){
				if(tmd.isEnriched()){
					enriched.add(tmd);
				}
			}
			if(enriched.size()==0){
				resolveConflictPrecisely(dataList);
			}
			else{
				for(TokenModificationData tmd : dataList){
					if(!tmd.isEnriched()){
						tmd.setCanceled(true,-1);
					}
				}
				if(enriched.size()>1){
					resolveConflictPrecisely(enriched);
				}
			}
			
		}

		protected void resolveConflictPrecisely(List<TokenModificationData> dataList) {
			int validParserId = dataList.get(0).getParserId();
			for(int i = 2 ; i < dataList.size() ; i++){
				dataList.get(i).setCanceled(true, validParserId);
			}
		}

		protected boolean finished() {
			
			boolean done = true;
			for(ParserData pd : parserDataList){
				
				if(!pd.isFinished()){
					done = false;
					break;
				}
			}
			return done;
		}

		public void registerData(int id, TokenModificationData md) {
			
			List<TokenModificationData> list = map.get(id);
			if(list==null){
				list = new ArrayList<TokenModificationData>();
				map.put(id,list);				
			}
			list.add(md);
			
		}
	}

}
