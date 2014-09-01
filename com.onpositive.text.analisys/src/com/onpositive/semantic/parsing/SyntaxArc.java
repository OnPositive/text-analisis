package com.onpositive.semantic.parsing;

import java.util.ArrayList;

public class SyntaxArc {

	public static final int DESCRIPTION = 1;
	public static final int PARENS = 2;
	
	
	public final ParsedWord source;
	public final int kind;
	public final ParsedWord target;

	public SyntaxArc(ParsedWord source, int kind, ParsedWord target) {
		super();
		this.source = source;
		this.kind = kind;
		this.target = target;
		if (source.sourceArcs == null) {
			source.sourceArcs = new ArrayList<SyntaxArc>();
		}
		source.sourceArcs.add(this);

		if (target.targetArcs == null) {
			target.targetArcs = new ArrayList<SyntaxArc>();
		}
		target.targetArcs.add(this);
	}
}
