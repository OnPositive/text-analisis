package com.onpositive.text.analysis;

import java.util.List;

public interface IUnit {
	
	static final int UNIT_TYPE_UNDEFINED = 00001;
	
	static final int UNIT_TYPE_DIGIT = 10001;
	
	static final int UNIT_TYPE_LETTER = 10002;
	
	static final int UNIT_TYPE_SYMBOL = 10003;
	
	static final int UNIT_TYPE_VULGAR_FRACTION = 10004;
	
	static final int UNIT_TYPE_LINEBREAK = 100101;
	
	static final int UNIT_TYPE_NON_BREAKING_SPACE = 100102;
	
	static final int UNIT_TYPE_OTHER_WHITESPACE = 100103;
	
	static final int UNIT_TYPE_SCALAR = 10201;
	
	static final int UNIT_TYPE_DATE = 10202;
	
	static final int UNIT_TYPE_DIMENSIONR = 10203;
	
	static final int UNIT_TYPE_WORD_FORM = 11001;
	
	String getStringValue();
	
	int getType();
	
	int getStartPosition();
	
	int getEndPosition();
	
	int getLength();
	
	IUnit getNext();
	
	IUnit getPrevious();
	
	void setNext(IUnit unit);
	
	void setPrevious(IUnit unit);
	
	List<IUnit> getNextUnits();
	
	List<IUnit> getPreviousUnits();
	
	void addNextUnit(IUnit unit);
	
	void addPreviousUnit(IUnit unit);
	
	List<IUnit> getChildren();
	
	void addChild(IUnit child);
	
	List<IUnit> getParents();
	
	void addParent(IUnit parent);
}
