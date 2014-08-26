package com.onpositive.semantic.words2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.onpositive.wikipedia.dumps.builder.XMLPageParser;


public class WordNetProvider {

	private static WordNet instance;
	
	public static final String DEFAULT_INDEX_FOLDER = "D:/se1";

	public static WordNet getInstance()
	{
		if (instance==null){
			String property = System.getProperty("engineConfigDir");
			if (property==null){
				property=DEFAULT_INDEX_FOLDER;
			}
			File fl=new File(property);
			File ind=new File(fl,"wnet.dat");
			if (ind.exists())
			{
				try {
					instance=SimpleWordNet.read(ind.getAbsolutePath());
				} catch (FileNotFoundException e) {
					
				} catch (IOException e) {
					
				}
			}
			if (instance==null){
				File[] listFiles = fl.listFiles();
				for (File f:listFiles){
					if (f.getName().startsWith("ruwik")){
						if (f.getName().endsWith(".xml")){
							SimpleWordNet wi = new SimpleWordNet();
							WictionaryParser.fill(new XMLPageParser(), wi, f.getAbsolutePath());
							try {
								wi.write(ind.getAbsolutePath());
							} catch (FileNotFoundException e) {
								throw new IllegalStateException(e);
							} catch (IOException e) {
								throw new IllegalStateException(e);
							}
							instance=wi;
						}
					}
				}
			}
		}
		return instance;
	}
}