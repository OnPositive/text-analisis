package com.onpositive.semantic.parsing;

import java.util.ArrayList;


public class ParsedText {

	protected String originalText;
	
	protected ArrayList<ParsedSequence>sequences=new ArrayList<ParsedSequence>();
	
	public ParsedText(String text){
		this.originalText=text;
		createSequences();		
	}

	private void createSequences() {
		parse(this, originalText);
		for (ParsedSequence s:sequences){
			s.reconcile();			
		}
	}
	
	void parse(ParsedText text,String content) {
		StringBuilder cw = null;
		int lastOffset = 0;
		boolean inQuta = false;
		boolean qtS = false;
		boolean inParen=false;
		ParsedSequence seq=new ParsedSequence(text);
		ParsedWord last=null;
		for (int a = 0; a < content.length(); a++) {
			char c = content.charAt(a);
			if (Character.isJavaIdentifierPart(c) | c == '-') {
				if (cw == null) {
					cw = new StringBuilder();
					lastOffset = a;
				}
				cw.append(c);
			} else {
				if (cw != null) {
					ParsedWord e = seq.addWord(lastOffset, cw.toString());
					e.qts = qtS;
					last=e;
					qtS=false;
					
				}
				if(!Character.isWhitespace(c)&&!Character.isJavaIdentifierPart(c) && c != '-'){
					qtS=false;	
					if (c == '(' ) {
						inParen = true;
						
					} else if (inParen) {
						if (c == ')') {
							inParen=false;
						}
					}
					if (c == '«' || c == '"' || c == '\'') {
						inQuta = true;
						qtS=true;
					} else if (inQuta) {
						if (c == '»' || c == '"' || c == '\'') {
							if (last!=null){
								last.qte=true;
							}
							inQuta = false;
						}
					}
					seq.addSign(a, c);
				}
				if (c=='\n'||c=='\r'){
					text.add(seq);
					seq=new ParsedSequence(text);
				}
				if (c=='.'&&!inQuta&&!inParen){
					if (last!=null&&last.length>2){
						if (!last.content.equals("Inc")){
						
							text.add(seq);
							seq=new ParsedSequence(text);
						}
						
					}
				}
				cw = null;
				lastOffset = -1;
			}
		}
		if (cw != null && cw.length() > 0) {
			ParsedWord e = seq.addWord(lastOffset, cw.toString());
			e.qts = qtS;			
		}
		text.add(seq);				
	}


	protected void add(ParsedSequence seq) {
		if (!seq.isEmpty()){
			sequences.add(seq);
		}
	}

	public ArrayList<ParsedSequence> sequences() {
		return sequences;
	}
}