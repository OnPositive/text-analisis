package com.onpositive.text.analisys.tests;

import java.util.List;

import com.onpositive.text.analysis.IToken;

public class App {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ParserTest v = new SyntaxParsersTest();
		List<IToken> process = v.process("Мама мыла раму");
		ParserTest.testTokenPrint(process);
	}

}
