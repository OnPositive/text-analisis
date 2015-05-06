package com.onpositive.text.analisys.tests;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;

import com.onpositive.text.analisys.tools.data.HtmlRemover;
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
		
		this.togglePrint(false);
		
	}
	
	private String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	private List<IToken> processFile(String path) {
		try {
			String str = readFile(path, Charset.availableCharsets().get("windows-1251"));
			String contents = HtmlRemover.removeHTML(str);
			List<IToken> processed = process(contents);			
			return processed;
		} catch (Exception e) {
			return null;
		}
	}
	
	
	public void test001() {
		List<IToken> processed = processFile("c:\\users\\yhaskell\\desktop\\dkg.txt");
		
		assertTrue(processed != null);
	}
	
	public void test002() {
		List<IToken> processed = processFile("c:/lib/book/ADAMS/hitch3.txt.html");
		
		assertTrue(processed != null);
	}

	
}
