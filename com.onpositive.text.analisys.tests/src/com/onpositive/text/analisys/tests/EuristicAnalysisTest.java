package com.onpositive.text.analisys.tests;

import junit.framework.TestCase;

public class EuristicAnalysisTest  extends TestCase{
	
	public void test00() {
		new ParsedTokensLoader(EuristicAnalysisTest.class.getResourceAsStream("corpora_parsed.xml"));
	}

}
