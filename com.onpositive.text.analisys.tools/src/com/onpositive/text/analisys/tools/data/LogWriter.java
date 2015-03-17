package com.onpositive.text.analisys.tools.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public LogWriter(String path) {
		super();
		this.file = new File(path);
	}

	private File file;
	
	public void reset() {
		
		if (file.exists()) {
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(new byte[0]);
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void writeLn(String str) {

		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, true);
			fw.write(str);
			fw.write(LINE_SEPARATOR);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
