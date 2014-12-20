package com.onpositive.text.analysis;

import java.util.Collection;
import java.util.List;

public interface IToken {
	
	public static enum Direction{
		
		START, END;
		
		public Direction opposite(){
			return this == START ? END : START;
		}
	}
	
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
	
	static final int TOKEN_TYPE_SENTENCE = 1100;	
	
	static final int TOKEN_TYPE_WORD_FORM = 11001;
	
	static final int TOKEN_TYPE_NOUN_ADJECTIVE = 11011;
	
	static final int TOKEN_TYPE_ADJECTIVE_ADVERB = 11012;
	
	static final int TOKEN_TYPE_VERB_ADVERB = 11013;
	
	static final int TOKEN_TYPE_DIRECT_OBJECT_NAME = 11014;
	
	static final int TOKEN_TYPE_DIRECT_OBJECT_INF = 11015;
	
	static final int TOKEN_TYPE_CLAUSE = 11030;
	
	static final int TOKEN_TYPE_ADVERB_WITH_MODIFICATOR = 11041;
	
	static final int TOKEN_TYPE_UNIFORM_PREDICATIVE = 11051;
	
	static final int TOKEN_TYPE_UNIFORM_ADVERB = 11052;
	
	static final int TOKEN_TYPE_UNIFORM_ADJECTIVE = 11053;

	static final int TOKEN_TYPE_UNIFORM_NOUN = 11054;

	static final int TOKEN_TYPE_MEASURED_NOUN = 11055;
	
	static final int TOKEN_TYPE_LINK = 11056;
	
	
	
	int id();
	
	void setId(int id);
	
	String getStringValue();
	
	int getType();
	
	int getStartPosition();
	
	int getEndPosition();
	
	int getLength();
	
	IToken getNext();
	
	IToken getPrevious();
	
	IToken getNeighbour(Direction direction);
	
	void setNext(IToken token);
	
	void setPrevious(IToken token);
	
	void setNeighbour(IToken token, Direction direction);
	
	List<IToken> getNextTokens();
	
	List<IToken> getPreviousToken();
	
	List<IToken> getNeighbours(Direction direction);
	
	void addNextToken(IToken token);
	
	void addPreviousToken(IToken token);
	
	void addNeighbour(IToken token, Direction direction);
	
	public void removeNextToken(IToken token);
	
	public void removePreviousToken(IToken token);
	
	public void removeNeighbour(Direction direction, IToken token);
	
	List<IToken> getChildren();
	
	void addChild(IToken child);
	
	void addChildren(Collection<IToken> children);
	
	void setChildren(Collection<IToken> children);
	
	IToken getFirstChild(Direction direction);
	
	IToken getChild(int pos, Direction direction);
	
	List<IToken> getParents();
	
	void addParent(IToken parent);

	boolean hasSpaceAfter();
	
	boolean hasSpaceBefore();
	
	boolean isContinuous();
	
	public String getLink();

	public void setLink(String link) ;

}