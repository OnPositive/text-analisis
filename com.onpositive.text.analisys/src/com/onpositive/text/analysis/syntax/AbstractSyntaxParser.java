package com.onpositive.text.analysis.syntax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.Gender;
import com.onpositive.semantic.wordnet.Grammem.SingularPlural;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.AbstractParser;
import com.onpositive.text.analysis.lexic.WordFormToken;
import com.onpositive.text.analysis.rules.matchers.AndMatcher;
import com.onpositive.text.analysis.rules.matchers.HasAllGrammems;
import com.onpositive.text.analysis.rules.matchers.HasAnyOfGrammems;
import com.onpositive.text.analysis.rules.matchers.HasGrammem;
import com.onpositive.text.analysis.rules.matchers.OrMatcher;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;

public abstract class AbstractSyntaxParser extends AbstractParser {

	protected AbstractWordNet wordNet;

	protected static Map<Case, Set<Case>> caseMatchMap = new HashMap<Case, Set<Case>>();

	protected static Map<SingularPlural, Set<SingularPlural>> spMatchMap
			= new HashMap<SingularPlural, Set<SingularPlural>>();

	static {
		fillGrammemMap(Case.all, caseMatchMap);
		fillGrammemMap(SingularPlural.all, spMatchMap);
	}

	protected <T extends Grammem> Map<T, T> matchGrammem(Set<T> set0, Set<T> set1, Map<T, Set<T>> matchMap)
	{
		Map<T, T> map = new HashMap<T, T>();
		for (T c0 : set0) {
			Set<T> matches = matchMap.get(c0);
			for (T c1 : matches) {
				if (set1.contains(c1)) {
					map.put(c0, c1);
					break;
				}
			}
		}
		return map.isEmpty() ? null : map;
	}

	@SuppressWarnings("unchecked")
	protected static <T extends Grammem> void fillGrammemMap(Set<T> all, Map<T, Set<T>> map)
	{
		HashSet<T> set1 = new HashSet<T>();
		HashSet<T> set2 = new HashSet<T>();
		for (T cs : all) {
			set1.clear();
			set2.clear();
			set1.add(cs);
			while (cs.parent != null) {
				cs = (T) cs.parent;
				set1.add(cs);
			}
			set2.addAll(set1);
			for (T c : set1) {
				Set<T> set3 = map.get(c);
				if (set3 != null) {
					set2.addAll(set3);
				}
			}
			for (T c : set2) {
				Set<T> set3 = map.get(c);
				if (set3 == null) {
					set3 = new HashSet<T>();
					map.put(c, set3);
				}
				set3.addAll(set2);
			}
		}
	}

	public AbstractSyntaxParser() {
		super();
	}

	@SuppressWarnings("unchecked")
	protected <T extends Grammem> Set<T> extractGrammems(Set<Grammem> grammems,
			Class<T> clazz) {

		HashSet<T> set = new HashSet<T>();
		for (Grammem gr : grammems) {
			if (clazz.isInstance(gr)) {
				set.add((T) gr);
			}
		}
		return set;
	}

	protected boolean checkParents(IToken newToken, List<IToken> children) {
		
		for(IToken ch : children){
			List<IToken> parents = ch.getParents();
			if(parents==null){
				continue;
			}
			if(parents.contains(newToken)){
				return false;
			}
		}
		return true;
	}
	
	protected List<IToken> combineNames( SyntaxToken mainGroup,	SyntaxToken token, int tokenType )
	{
		ArrayList<IToken> tokens = new ArrayList<IToken>(); 
		int startPosition = Math.min(mainGroup.getStartPosition(), token.getStartPosition());
		int endPosition = Math.max(mainGroup.getEndPosition(), token.getEndPosition());
		
		Map<GrammarRelation,Set<Grammem>> tokenGrammems = prepareGrammemsMap(token);
		Map<GrammarRelation,Set<Grammem>> mainGroupGrammems = prepareGrammemsMap(mainGroup);
		
		for(Map.Entry<GrammarRelation, Set<Grammem>> entry0 : mainGroupGrammems.entrySet()){
			
			Set<Grammem> nounGrammems = entry0.getValue();
			Set<Case> nounCases = extractGrammems(nounGrammems,Case.class);
			Set<SingularPlural> nounSP = extractGrammems(nounGrammems,SingularPlural.class);
			Set<Gender> nounGender = extractGrammems(nounGrammems,Gender.class);
			
			for(Map.Entry<GrammarRelation, Set<Grammem>> entry1 : tokenGrammems.entrySet()){
				
				Set<Grammem> adjvGrammems = entry1.getValue();
				Set<Case> adjvCases = extractGrammems(adjvGrammems,Case.class);
				Map<Case, Case> matchCase = matchCase(nounCases,adjvCases);
				if(matchCase==null){
					continue;
				}
				Set<SingularPlural> adjvSP = extractGrammems(adjvGrammems,SingularPlural.class);
				Map<SingularPlural, SingularPlural> matchSP = matchSP(nounSP,adjvSP);
				if(matchSP==null){
					continue;
				}
				Set<Gender> adjvGender = extractGrammems(adjvGrammems,Gender.class);
				Set<Gender> matchGender = matchGender(nounGender,adjvGender);
				if(matchGender==null){
					continue;
				}
				SyntaxToken newToken = new SyntaxToken(tokenType, mainGroup, startPosition, endPosition);
				tokens.add(newToken);
				break;
			}
		}
		return tokens;
	}
	
	private Map<GrammarRelation,Set<Grammem>> prepareGrammemsMap(SyntaxToken token) {
		
		WordFormToken mainWord = token.getMainWord();
		Set<Grammem> grammems = mainWord.getMeaningElement().getGrammems();
		List<GrammarRelation> grammarRelations = mainWord.getGrammarRelations();
		Map<GrammarRelation,Set<Grammem>> map = new HashMap<GrammarRelation, Set<Grammem>>(); 
		for(GrammarRelation gr : grammarRelations){
			Set<Grammem> set = new HashSet<Grammem>(gr.getGrammems());
			set.addAll(grammems);
			map.put(gr, set);
		}
		return map;
	}

	private Set<Gender> matchGender(Set<Gender> set0, Set<Gender> set1) {
		
		if(set0.contains(Gender.UNKNOWN)){
			set1.remove(Gender.UNKNOWN);
			return set1;
		}
		if(set1.contains(Gender.UNKNOWN)){
			return set0;
		}
		if(set0.contains(Gender.COMMON)){
			return set1;
		}
		if(set1.contains(Gender.COMMON)){
			return set0;
		}
		HashSet<Gender> result = new HashSet<Grammem.Gender>();
		for(Gender g : set0){
			if(set1.contains(g)){
				result.add(g);
			}
		}
		return result.isEmpty() ? null : result;
	}


	private Map<SingularPlural,SingularPlural> matchSP(Set<SingularPlural> set0, Set<SingularPlural> set1) {
		return matchGrammem(set0, set1, spMatchMap);
	}


	private Map<Case,Case> matchCase(Set<Case> set0, Set<Case> set1) {
		return matchGrammem(set0, set1, caseMatchMap);
	}

	protected boolean checkIfAlreadyProcessed(SyntaxToken token0, SyntaxToken token1) {
		List<IToken> parents1 = token1.getParents();
		List<IToken> parents0 = token0.getParents();
		if((parents1!=null&&!parents1.isEmpty())&&(parents0!=null&&!parents0.isEmpty())){
			for(IToken parent : parents0){
				if(parents1.contains(parent)){
					return true;
				}
			}
		}
		return false;
	}
	public final UnaryMatcher<SyntaxToken> hasAll(Grammem... tran) {
		return new HasAllGrammems(tran);
	}

	public final UnaryMatcher<SyntaxToken> has(Grammem infn) {
		return new HasGrammem(infn);
	}

	public final UnaryMatcher<SyntaxToken> not(final UnaryMatcher<SyntaxToken> infn) {
		return new UnaryMatcher<SyntaxToken>(SyntaxToken.class) {

			@Override
			protected boolean innerMatch(SyntaxToken token) {
				return !infn.match(token);
			}
		};
	}
	public static final UnaryMatcher<SyntaxToken>and(UnaryMatcher<SyntaxToken>...matchers){
		return new AndMatcher<SyntaxToken>(SyntaxToken.class, matchers);
	}
	public final UnaryMatcher<SyntaxToken>or(UnaryMatcher<SyntaxToken>...matchers){
		return new OrMatcher<SyntaxToken>(SyntaxToken.class, matchers);
	}

	public final UnaryMatcher<SyntaxToken> hasAny(Grammem...gf) {
		return new HasAnyOfGrammems(gf);
	}

	public final UnaryMatcher<SyntaxToken> hasAny(Set<? extends Grammem> set) {
		return hasAny(set.toArray(new Grammem[set.size()]));
	}
}
