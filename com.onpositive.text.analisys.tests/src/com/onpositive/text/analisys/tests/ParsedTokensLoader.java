package com.onpositive.text.analisys.tests;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.text.analisys.tests.euristics.SimplifiedToken;

public class ParsedTokensLoader {
	
	private List<SimplifiedToken> tokens = new ArrayList<SimplifiedToken>();
	
	private String initialText;
	
	DefaultHandler handler = new DefaultHandler() {
		
		private String curName;
		private StringBuilder textBuilder = new StringBuilder();
		private List<Grammem> grammems = new ArrayList<Grammem>();
		private boolean ignore = false;
		private String thisElement;
		
		public void startElement(String uri, String localName, String qName, org.xml.sax.Attributes attributes) throws SAXException {
			thisElement = qName;
			if ("tfr".equalsIgnoreCase(qName)) {
				curName = attributes.getValue("t");
			} else if ("g".equalsIgnoreCase(qName) && curName != null) {
				String grammemName = attributes.getValue("v");
				if ("PNCT".equalsIgnoreCase(grammemName)) {
					ignore = true;
				}
				Grammem gr = Grammem.get(grammemName);
				if (gr == null) {
					gr = Grammem.get(grammemName.toLowerCase());
				}
				if (gr == null) {
					gr = Grammem.get(grammemName.toUpperCase());
				}
				grammems.add(gr);
			} 
		};
		
		public void characters(char[] ch, int start, int length) throws SAXException {
			if ("source".equalsIgnoreCase(thisElement)) {
				textBuilder.append(new String(ch, start, length));
			}
		};
		
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if ("tfr".equalsIgnoreCase(qName)) {
				if (!ignore) {
//					System.out.println("Token: " + curName + ", Grammmems: " + grammems.toString());
					tokens.add(new SimplifiedToken(curName, grammems));
				}
				curName = null;
				grammems = new ArrayList<Grammem>();
				ignore = false;
			};
		
		}
		
		public void endDocument() throws SAXException {
			initialText = textBuilder.toString().replaceAll("\\t","");
		};
	
	};
	
	
	public ParsedTokensLoader(InputStream stream) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(stream, handler);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public List<SimplifiedToken> getTokens() {
		return tokens;
	}
	
	public String getInitialText() {
		return initialText;
	}
}
