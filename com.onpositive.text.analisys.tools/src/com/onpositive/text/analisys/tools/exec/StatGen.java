package com.onpositive.text.analisys.tools.exec;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.onpositive.semantic.wordnet.composite.CompositeWordnet;
import com.onpositive.text.analisys.tools.WFTConflictStat;
import com.onpositive.text.analisys.tools.WFTConflictStat.ConflictInfo;
import com.onpositive.text.analisys.tools.data.HtmlRemover;
import com.onpositive.text.analysis.IToken;

public class StatGen {
	
	private static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public static CompositeWordnet CreateWordnet() {
		CompositeWordnet wn=new CompositeWordnet();
		wn.addUrl("/numerics.xml");
		wn.addUrl("/dimensions.xml");
		wn.addUrl("/modificator-adverb.xml");
		wn.addUrl("/prepositions.xml");
		wn.addUrl("/conjunctions.xml");
		wn.addUrl("/modalLikeVerbs.xml");
		wn.addUrl("/participles.xml");
		wn.prepare();
		return wn;
	}
	
	public static void main(String [] args) {
		
 		if (args.length != 1) {
			System.err.println("Usage: java -jar statgen.jar FILENAME\n");
			return;
		}
 				
		WFTConflictStat sg = new WFTConflictStat(CreateWordnet());
		
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
				System.out.println("FAIL " + filename + ": No tokens produced.");
			else
				sg.getStatistic(processed).forEach(stat->stat.forEach(x->System.out.println(PrintConflicts(x))));
			
		} catch (Exception e) {
			System.out.println("FAIL " + filename + ": " + e.getMessage());
		}
	}
	
	public static String PrintConflicts(ConflictInfo[] conflicts) {
		try {
			if (conflicts == null || conflicts.length == 0) return null;
			
			JSONArray cs = new JSONArray();
			
			for (ConflictInfo c : conflicts) 
			{
				JSONObject conflict = new JSONObject();
				conflict.put("word", c.wft.getBasicForm());
				conflict.put("partofspeech", c.wft.getPartOfSpeech().toString());
				conflict.put("tokens", (c.tokenTypes != null ? new JSONArray(c.tokenTypes) : new JSONArray()));				
				cs.put(conflict);
			}
			return cs.toString();
		} catch (JSONException e) {
			return null;
		}
	}
}
