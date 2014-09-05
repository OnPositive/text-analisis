package com.onpositive.text.analysis.lexic;

import com.onpositive.text.analysis.IUnit;

public interface ITokenizerExtension {
	
	IUnit readUnit(String str, int pos);
}
