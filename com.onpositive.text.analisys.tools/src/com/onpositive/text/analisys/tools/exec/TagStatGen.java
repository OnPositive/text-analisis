package com.onpositive.text.analisys.tools.exec;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.onpositive.semantic.wordnet.composite.CompositeWordnet;
import com.onpositive.text.analisys.tools.TagStatGenerator;
import com.onpositive.text.analisys.tools.data.HtmlRemover;
import com.onpositive.text.analysis.IToken;

public class TagStatGen {
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
	
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("Usage: java -jar tagsg.jar FILENAME OUTPUT\n");
			return;
		}

 		String filename = args[0];
		
		String contents = "";
		
 		DataOutputStream out = null; 		
 		try {
 			contents = readFile(filename, Charset.availableCharsets().get("windows-1251"));
			contents = HtmlRemover.removeHTML(contents);
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(args[1], false)));
		} catch (IOException e) {
			System.err.println("IO error on " + filename + ": " + e.getMessage());
			return;
		}
 		TagStatGenerator sg = new TagStatGenerator(CreateWordnet());

		try {
			sg.setOnProcess((x,y)->System.err.print("\r[" + x + "/" + y + "]"));
			List<IToken> processed = sg.parse(contents);
		
			ObjectArrayList<short[]> stats = sg.stats(processed);
			
			for (ObjectCursor<short[]> vv : stats) {
				for (short v : vv.value) out.writeShort(v);
				out.writeShort(-1);
			}
			out.flush();
			out.close();			
		} catch (Exception e) {
			System.err.println("FAIL " + filename + ": " + e.getMessage());
		}		
 		
	}

}
