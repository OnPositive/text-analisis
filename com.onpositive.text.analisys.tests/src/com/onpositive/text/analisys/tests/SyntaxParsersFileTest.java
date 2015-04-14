package com.onpositive.text.analisys.tests;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;

import com.onpositive.text.analysis.IToken;
import com.onpositive.semantic.wordnet.composite.CompositeWordnet;
import com.onpositive.text.analysis.syntax.SyntaxParser;

public class SyntaxParsersFileTest extends ParserTest {

	public SyntaxParsersFileTest() {
		super();
		CompositeWordnet wn=new CompositeWordnet();
		wn.addUrl("/numerics.xml");
		wn.addUrl("/dimensions.xml");
		wn.addUrl("/modificator-adverb.xml");
		wn.addUrl("/prepositions.xml");
		wn.addUrl("/conjunctions.xml");
		wn.addUrl("/modalLikeVerbs.xml");
		wn.prepare();
		SyntaxParser syntaxParser = new SyntaxParser(wn);
		this.composition = syntaxParser;
	}
	
	private String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	
	public void test002() {
		String str = "Можно было без труда следить за движениями";
		List<IToken> processed = process(str);
		
		assertTrue(processed != null);
	}
	
	public void test003() {
		String str = "Странно будет без очков смотреть на его рукоплескания.";
		List<IToken> processed = process(str);
		
		assertTrue(processed != null);
	}
	
	public void test004() {
		String str = "Акула, чувствуя, что ее вытаскивают, забилась.";
		List<IToken> processed = process(str);
		
		assertTrue(processed != null);
	}
	
	public void test001() {
		try {
			String str = readFile("c:\\users\\yhaskell\\desktop\\dkg.txt", Charset.defaultCharset());			
			List<IToken> processed = process(str);
			
			assertTrue(processed != null);						
		} catch (IOException e) {
			assertTrue(false);
		}
		
	}

	
}
