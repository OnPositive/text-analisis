package com.onpositive.semantic.parsing;

public class SignElement implements ISentenceElement {

	private int offset;
	public int getOffset() {
		return offset;
	}

	public char getContent() {
		return content;
	}

	private char content;

	public SignElement(int offset, char content) {
		this.offset=offset;
		this.content=content;
	}

}
