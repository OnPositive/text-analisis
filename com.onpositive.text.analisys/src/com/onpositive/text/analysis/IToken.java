package com.onpositive.text.analysis;

import java.util.List;

public interface IToken {
	
	static final int TOKEN_TYPE_UNDEFINED = 00001;
	
	static final int TOKEN_TYPE_DIGIT = 10001;
	
	static final int TOKEN_TYPE_LETTER = 10002;
	
	static final int TOKEN_TYPE_SYMBOL = 10003;
	
	static final int TOKEN_TYPE_VULGAR_FRACTION = 10004;
	
	static final int TOKEN_TYPE_EXPONENT = 10005;
	
	static final int TOKEN_TYPE_LINEBREAK = 100101;
	
	static final int TOKEN_TYPE_NON_BREAKING_SPACE = 100102;
	
	static final int TOKEN_TYPE_OTHER_WHITESPACE = 100103;
	
	static final int TOKEN_TYPE_SCALAR = 10201;
	
	static final int TOKEN_TYPE_DATE = 10202;
	
	static final int TOKEN_TYPE_DIMENSION = 10203;
	
	static final int TOKEN_TYPE_UNIT = 10213;
	
	static final int TOKEN_TYPE_WORD_FORM = 11001;	
	
	String getStringValue();
	
	int getType();
	
	int getStartPosition();
	
	int getEndPosition();
	
	int getLength();
	
	IToken getNext();
	
	IToken getPrevious();
	
	void setNext(IToken unit);
	
	void setPrevious(IToken unit);
	
	List<IToken> getNextTokens();
	
	List<IToken> getPreviousToken();
	
	void addNextUnit(IToken unit);
	
	void addPreviousUnit(IToken unit);
	
	List<IToken> getChildren();
	
	void addChild(IToken child);
	
	List<IToken> getParents();
	
	void addParent(IToken parent);

	boolean hasSpaceAfter();
	
	boolean hasSpaceBefore();
}