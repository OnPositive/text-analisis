package com.onpositive.text.analisys.tools.exec;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;

import javax.swing.text.html.HTMLDocument.HTMLReader;

import com.onpositive.semantic.wordnet.composite.CompositeWordnet;
import com.onpositive.text.analisys.tools.data.HtmlRemover;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.syntax.SyntaxParser;

public class ProcessFile {

	private static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.err.println("Usage: java -jar process.jar FILENAME\n");
			return;
		}
		
		// Initialize parser
		CompositeWordnet wn=new CompositeWordnet();
		wn.addUrl("/numerics.xml");
		wn.addUrl("/dimensions.xml");
		wn.addUrl("/modificator-adverb.xml");
		wn.addUrl("/prepositions.xml");
		wn.addUrl("/conjunctions.xml");
		wn.addUrl("/modalLikeVerbs.xml");
		wn.prepare();
		SyntaxParser parser = new SyntaxParser(wn);

		String filename = args[0];
		
		String contents = "";
		
		try {
			contents = readFile(filename, Charset.defaultCharset());
			contents = HtmlRemover.removeHTML(contents);
		} catch (IOException e) {
			System.err.println("IO error on " + filename + ": " + e.getMessage());
			return;
		}
		
		try {
			List<IToken> processed = parser.parse(contents);
			if (processed == null || processed.size() == 0)
				System.out.println("Fail on " + filename + ": No tokens produced.");
			else
				System.out.println("Pass on " + filename);
		} catch (Exception e) {
			System.out.println("Fail on " + filename + ": " + e.getMessage());
		}
	}
}
