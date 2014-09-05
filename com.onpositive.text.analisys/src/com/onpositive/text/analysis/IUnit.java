package com.onpositive.text.analysis;

public interface IUnit {
	
	static final int UNIT_TYPE_UNDEFINED = 00001;
	
	static final int UNIT_TYPE_DIGIT = 10001;
	
	static final int UNIT_TYPE_LETTER = 10002;
	
	static final int UNIT_TYPE_SIGN = 10003;
	
	static final int UNIT_TYPE_LINEBREAK = 100101;
	
	static final int UNIT_TYPE_NON_BREAKING_SPACE = 100102;
	
	static final int UNIT_TYPE_OTHER_WHITESPACE = 100103;
	
	String getStringValue();
	
	int[] getType();
	
	int getStartPosition();
	
	int getEndPosition();
	
	int getLength();
}
