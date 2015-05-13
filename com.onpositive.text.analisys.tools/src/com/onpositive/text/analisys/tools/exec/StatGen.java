package com.onpositive.text.analisys.tools.exec;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import com.onpositive.semantic.wordnet.composite.CompositeWordnet;
import com.onpositive.text.analisys.tools.LexicParserStatGenerator;
import com.onpositive.text.analisys.tools.data.HtmlRemover;
import com.onpositive.text.analisys.tools.exec.ProcessFile.CLibrary;
import com.onpositive.text.analysis.IToken;

public class StatGen {
	
	private static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public static void main(String [] args) {
		
 		if (args.length != 1) {
			System.err.println("Usage: java -jar statgen.jar FILENAME\n");
			return;
		}
 		PrintStream out = getSysout();
 		
		CompositeWordnet wn=new CompositeWordnet();
		wn.addUrl("/numerics.xml");
		wn.addUrl("/dimensions.xml");
		wn.addUrl("/modificator-adverb.xml");
		wn.addUrl("/prepositions.xml");
		wn.addUrl("/conjunctions.xml");
		wn.addUrl("/modalLikeVerbs.xml");
		wn.prepare();
		LexicParserStatGenerator sg = new LexicParserStatGenerator(wn);
		
		String filename = args[0];
		
		String contents = "";
		
		try {			
			contents = readFile(filename, Charset.availableCharsets().get("windows-1251"));
			contents = HtmlRemover.removeHTML(contents);
		} catch (IOException e) {
			System.err.println("IO error on " + filename + ": " + e.getMessage());
			return;
		}
				
		try {
			List<IToken> processed = sg.parse(contents);
			
			if (processed == null || processed.size() == 0)
				out.println("Fail on " + filename + ": No tokens produced.");
			else {
				out.println("Pass on " + filename + ": Count = " + processed.size());				
				sg.stats(processed).forEach(stat -> System.out.println(stat.start + " -- " + stat.end + ": " + stat.structure.toString()));				
			}
		} catch (Exception e) {
			System.out.println("Fail on " + filename + ": " + e.getMessage());
		}
	}

	private static PrintStream getSysout() {
		PrintStream out = null;
		try {
			out = new PrintStream(System.out, true, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return out;
	}
}
