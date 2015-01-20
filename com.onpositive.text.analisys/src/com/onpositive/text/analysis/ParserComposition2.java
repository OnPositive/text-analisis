package com.onpositive.text.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.IntOpenHashSet;
import com.onpositive.text.analysis.syntax.SyntaxToken;

public class ParserComposition2 extends ParserComposition {
	
	public ParserComposition2(AbstractParser... parsers) {
		super(parsers);
		setHandleBoundsFalse();
		initRegistry();
	}
	
	TokenModificationRegistry reg;


	private void initRegistry() {
		reg = new TokenModificationRegistry(this.parsers);
	}


	public ParserComposition2(boolean isGloballyRecursive, AbstractParser... parsers) {
		super(isGloballyRecursive,parsers);
		setHandleBoundsFalse();
	}	
	
	
	private void setHandleBoundsFalse() {
		for(AbstractParser ap : this.parsers){
			ap.setHandleBounds(false);
		}
	}
	
	
	@Override
	public List<IToken> process(List<IToken> tokens) {
		
		List<IToken> result = reg.process(tokens);
		return result;
	}
	
	protected static class ParserData{
		
		
		public ParserData(TokenModificationRegistry reg, AbstractParser ap, int id) {
			super();
			this.reg = reg;
			this.parser = ap;
			this.parserId = id;
		}

		private final TokenModificationRegistry reg;
		
		private final AbstractParser parser;
		
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
				
				int tokenId = token.id();				
				IToken mainGroup = null;
				if(token instanceof SyntaxToken){
					mainGroup = ((SyntaxToken)token).getMainGroup();
				}
				List<IToken> children = token.getChildren();				
				for(IToken ch : children){
					int modType = ( mainGroup == null || ch !=mainGroup )
							? TokenModificationData.CONSUMED : TokenModificationData.ENRICHED;					
					
					TokenModificationData md = new TokenModificationData(reg, tokenId, parserId, modType);
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
			return !isFinished ;
		}
		
		public void setFinished(boolean isFinished) {
			this.isFinished = isFinished;
		}

		public void setNeedRepeat(){
			this.isFinished = false;
		}
	}
	
	
	private static class TokenModificationData{
		
		public TokenModificationData(TokenModificationRegistry reg, int producedTokenId, int parserId, int modType) {
			super();
			this.reg = reg;
			this.parserId = parserId;
			this.producedTokenId = producedTokenId;
			this.modType = modType;
		}
		
		
		private static int CONSUMED = 0;
		
		private static int ENRICHED = 0;
		
		private TokenModificationRegistry reg;
		
		private int parserId;
		
		private int producedTokenId;

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

		public boolean isCanceled() {
			return isCanceled;
		}

		public void setCanceled(boolean isCanceled) {
			this.isCanceled = isCanceled;
			if(isCanceled){
				reg.enableParserRepeat(parserId);
				reg.cancelProducedToken(parserId);
			}
		}

		public boolean isEnriched() {
			return getModType() == ENRICHED;
		}
	}
	
	private static class TokenModificationRegistry{
		
		private ArrayList<ParserData> parserDataList = new ArrayList<ParserData>();
		
		private IntObjectOpenHashMap<List<TokenModificationData>> map
			= new IntObjectOpenHashMap<List<TokenModificationData>>();
		
		private List<IToken> original;
		
		private IntOpenHashSet cancelledProducedTokens;
		
		public void cancelProducedToken(int id){
			cancelledProducedTokens.add(id);
		}

		public TokenModificationRegistry(AbstractParser[] parsers) {
			
			
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
			
			clean();
			
			this.original = tokens;
			
			while(true){

				for( ParserData pd : parserDataList){
					if(pd.isFinished()){
						continue;
					}
					pd.process(tokens);
					pd.setFinished(true);
				}
				
				resolveConflicts();
				
				boolean finished = finished();
				if(finished){
					break;
				}
			}
			
			List<IToken> result = collectTokens();
			return result;			
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
						List<IToken> children = token.getChildren();						
						for(IToken ch : children){
							ignoreSet.add(ch.id());
						}
					}
				}
			}
			
			ArrayList<IToken> result = new ArrayList<IToken>();
			for(ParserData pd : parserDataList){
				List<IToken> res = pd.getResult();
				for(IToken t : res){
					int id = t.id();
					if(ignoreSet.contains(id)){
						continue;
					}
					result.add(t);
					resultTokensMap.put(t.id(),t);
				}
			}
			Collections.sort(result, new Comparator<IToken>() {

				@Override
				public int compare(IToken o1, IToken o2) {
					return o1.getStartPosition()-o2.getStartPosition();
				}
			});
			
			TokenBoundsHandler tbh = new TokenBoundsHandler();
			tbh.setNewTokens(newTokensMap);
			tbh.setResultTokens(resultTokensMap);
			tbh.handleBounds(result);
			
			return result;
		}

		private void clean() {
			this.cancelledProducedTokens.clear();
			this.map.clear();
			for(ParserData pd : this.parserDataList){
				pd.setFinished(false);
			}
		}

		private void resolveConflicts() {
			
			for(IToken t : original){
				
				List<TokenModificationData> dataList = map.get(t.id());
				if(dataList==null||dataList.size()<2){
					continue;
				}
				resolveConflict(dataList);
			}
			
		}

		private void resolveConflict(List<TokenModificationData> dataList) {
			
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
						tmd.setCanceled(true);
					}
				}
				if(enriched.size()>1){
					resolveConflictPrecisely(enriched);
				}
			}
			
		}

		private void resolveConflictPrecisely(List<TokenModificationData> dataList) {
			for(int i = 2 ; i < dataList.size() ; i++){
				dataList.get(i).setCanceled(true);
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
