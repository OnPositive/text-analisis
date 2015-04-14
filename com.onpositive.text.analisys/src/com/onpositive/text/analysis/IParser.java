package com.onpositive.text.analysis;

import java.util.List;

import com.onpositive.text.analysis.utils.ILogger;

public interface IParser {

	List<IToken> process(List<IToken> tokens);

	String getText();

	void setText(String text);

	void resetTrigger();

	boolean hasTriggered();

	boolean isRecursive();

	void setHandleBounds(boolean b);

	List<IToken> getNewTokens();
	
	void setTokenIdProvider(TokenIdProvider tokenIdProvider);

	TokenIdProvider getTokenIdProvider();
	
	List<IToken> getBaseTokens();
	
	void setBaseTokens(List<IToken> baseTokens);

	void clean();
	
	void setLogger(ILogger logger);
	
	void setErrorLogger(ILogger logger);

	public class TokenIdProvider{
		
		private int currentTokenId;
		
		private boolean blocked;

		public void prepare(List<IToken> tokens){
			if(blocked){
				return;
			}
			currentTokenId = 0;
			for(IToken token : tokens){
				currentTokenId = Math.max(currentTokenId, token.id());
			}
		}

		public int getVacantId() {
			return ++currentTokenId;
		}

		public void block() {
			this.blocked = true;
		}
		
		public void unblock() {
			this.blocked = false;
		}
	}

}