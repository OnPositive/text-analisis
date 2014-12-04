package com.onpositive.text.analysis.rules.matchers;

import java.util.Set;

import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.PartOfSpeech;
import com.onpositive.text.analysis.syntax.SyntaxToken;

public class GrammemMathers {

	public static UnaryMatcher<SyntaxToken> hasAll(Grammem... tran) {
		return new HasAllGrammems(tran);
	}

	public static UnaryMatcher<SyntaxToken> has(Grammem infn) {
		return new HasGrammem(infn);
	}
	public static UnaryMatcher<SyntaxToken>and(UnaryMatcher<SyntaxToken>...matchers){
		return new AndMatcher<SyntaxToken>(SyntaxToken.class, matchers);
	}

	public static UnaryMatcher<SyntaxToken> hasAny(Grammem...gf) {
		return new HasAnyOfGrammems(gf);
	}

	public static UnaryMatcher<SyntaxToken> hasAny(Set<? extends Grammem> set) {
		return hasAny(set.toArray(new Grammem[set.size()]));
	}

	
}
