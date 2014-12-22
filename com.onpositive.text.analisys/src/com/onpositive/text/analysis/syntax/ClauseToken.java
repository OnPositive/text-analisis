package com.onpositive.text.analysis.syntax;

import com.onpositive.text.analysis.IToken;

public class ClauseToken extends SyntaxToken {

	public ClauseToken(SyntaxToken subject, SyntaxToken predicate, int startPosition, int endPosition) {
		super(IToken.TOKEN_TYPE_CLAUSE, null, null, startPosition, endPosition);
		this.subject = subject;
		this.predicate = predicate;
	}
	
	private SyntaxToken subject;
	
	private SyntaxToken predicate;

	public SyntaxToken getSubject() {
		return subject;
	}

	public SyntaxToken getPredicate() {
		return predicate;
	}

	public void setSubject(SyntaxToken subject) {
		this.subject = subject;
	}

	public void setPredicate(SyntaxToken predicate) {
		this.predicate = predicate;
	}

	@Override
	public String getStringValue() {
		StringBuilder bld = new StringBuilder();
		if(subject!=null){
			bld.append(subject);
		}
		bld.append(" ");
		if(predicate!=null){
			bld.append(predicate);
		}
		return bld.toString().trim();
	}

}