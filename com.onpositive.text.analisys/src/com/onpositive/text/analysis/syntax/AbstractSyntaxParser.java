package com.onpositive.text.analysis.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.AnimateProperty;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.Gender;
import com.onpositive.semantic.wordnet.Grammem.SingularPlural;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.AbstractParser;
import com.onpositive.text.analysis.lexic.PrepConjRegistry;
import com.onpositive.text.analysis.rules.matchers.AndMatcher;
import com.onpositive.text.analysis.rules.matchers.HasAllGrammems;
import com.onpositive.text.analysis.rules.matchers.HasAnyOfGrammems;
import com.onpositive.text.analysis.rules.matchers.HasGrammem;
import com.onpositive.text.analysis.rules.matchers.OrMatcher;
import com.onpositive.text.analysis.rules.matchers.UnaryMatcher;
import com.onpositive.text.analysis.syntax.SyntaxToken.GrammemSet;

public abstract class AbstractSyntaxParser extends AbstractParser {

	protected static Map<Case, Set<Case>> caseMatchMap = new HashMap<Case, Set<Case>>();

	protected static Map<SingularPlural, Set<SingularPlural>> spMatchMap
			= new HashMap<SingularPlural, Set<SingularPlural>>();

	static {
		fillGrammemMap(Case.all, caseMatchMap);
		fillGrammemMap(SingularPlural.all, spMatchMap);
	}
	
	private static PrepConjRegistry prepConjRegistry;

	public AbstractSyntaxParser(AbstractWordNet wordNet) {
		super();
		this.wordNet = wordNet;
	}
	
	private AbstractWordNet wordNet;
	
	public boolean isIterative(){
		return false;
	}

	protected static <T extends Grammem> Map<T, T> matchGrammem(Set<T> set0, Set<T> set1, Map<T, Set<T>> matchMap)
	{
		Map<T, T> map = new HashMap<T, T>();
		for (T c0 : set0) {
			Set<T> matches = matchMap.get(c0);
			for (T c1 : matches) {
				if (set1.contains(c1)) {
					map.put(c0, c1);
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
	
	protected static boolean checkParents(IToken newToken, List<IToken> children) {
		for(IToken ch : children){
			List<IToken> parents = ch.getParents();
			if(parents==null){
				continue;
			}
			int s0 = children.size();
l0:			for(IToken parent: parents){
				if(parent.isDoubtful()){
					continue;
				}
				List<IToken> children1 = parent.getChildren();
				int s1 = children1.size();
				if(s0!=s1){
					continue;
				}
				for(int i = 0 ; i < s0 ; i++){
					IToken ch0 = children.get(i);
					IToken ch1 = children1.get(i);
					if(!ch0.equals(ch1)){
						continue l0;
					}
				}
				return false;
			}
		}
		return true;
	}
	
	protected static SyntaxToken combineNames(SyntaxToken mainGroup, SyntaxToken token, int tokenType )
	{
		int startPosition = Math.min(mainGroup.getStartPosition(), token.getStartPosition());
		int endPosition = Math.max(mainGroup.getEndPosition(), token.getEndPosition());
		
		ArrayList<GrammemSet> grammemSets = new ArrayList<SyntaxToken.GrammemSet>();
l0:		for(GrammemSet gs0 : mainGroup.getGrammemSets()){			
			for(GrammemSet gs1 : token.getGrammemSets())
			{
				Set<AnimateProperty> matchedAnim = matchAnimatedProperty(gs0,gs1);
				if(matchedAnim==null){
					continue;
				}
				
				Map<SingularPlural,SingularPlural> matchedSp = matchSP(gs0,gs1);
				if(matchedSp==null||matchedSp.isEmpty()){
					continue;
				}				
				
				if(matchedSp.size()!=1||!(matchedSp.containsKey(SingularPlural.PLURAL)||matchedSp.containsKey(SingularPlural.PLURAL))){
					Set<Gender> matchedGender = matchGender(gs0,gs1);
					if(matchedGender==null||matchedGender.isEmpty()){
						continue;
					}
				}
				
				Map<Case, Case> matchedCase = matchCase(gs0,gs1);
				if(matchedCase==null||matchedCase.isEmpty()){
					continue;
				}
				grammemSets.add(gs0);
				continue l0;
			}
		}
		if(grammemSets.isEmpty()){
			return null;
		}
		SyntaxToken result = new SyntaxToken(tokenType, mainGroup, grammemSets, startPosition, endPosition);
		return result;
	}
	
	private static Set<AnimateProperty> matchAnimatedProperty(GrammemSet gs0, GrammemSet gs1) {
		Set<AnimateProperty> anim0 = gs0.extractGrammems(AnimateProperty.class);
		Set<AnimateProperty> anim1 = gs1.extractGrammems(AnimateProperty.class);
		if(anim0.contains(AnimateProperty.ANim)){
			return anim1;
		}
		if(anim0.isEmpty()){
			return anim1;
		}
		if(anim1.contains(AnimateProperty.ANim)){
			return anim0;
		}
		if(anim1.isEmpty()){
			return anim0;
		}
		HashSet<AnimateProperty> set = new HashSet<AnimateProperty>();
		for(AnimateProperty ap : anim0){
			if(anim1.contains(ap)){
				set.add(ap);
			}
		}
		return set.isEmpty() ? null : set;
	}

	public static Set<Gender> matchGender(GrammemSet gs0, GrammemSet gs1){
		return matchGender(gs0.extractGrammems(Gender.class), gs1.extractGrammems(Gender.class));
	}

	protected static Set<Gender> matchGender(Set<Gender> set0, Set<Gender> set1) {
		
		if(set0.contains(Gender.UNKNOWN)){
			return set1.isEmpty() ? set0 : set1;
		}
		if(set1.contains(Gender.UNKNOWN)){
			return set0.isEmpty() ? set1 : set0;
		}
		if(set0.contains(Gender.COMMON)){
			return set1.isEmpty() ? set0 : set1;
		}
		if(set1.contains(Gender.COMMON)){
			return set0.isEmpty() ? set1 : set0;
		}
		HashSet<Gender> result = new HashSet<Grammem.Gender>();
		for(Gender g : set0){
			if(set1.contains(g)){
				result.add(g);
			}
		}
		return result.isEmpty() ? null : result;
	}
	
	public static Map<SingularPlural,SingularPlural> matchSP(GrammemSet gs0, GrammemSet gs1){
		return matchSP(gs0.extractGrammems(SingularPlural.class), gs1.extractGrammems(SingularPlural.class));
	}


	protected static Map<SingularPlural,SingularPlural> matchSP(Set<SingularPlural> set0, Set<SingularPlural> set1) {
		return matchGrammem(set0, set1, spMatchMap);
	}
	
	public static Map<Case,Case> matchCase(GrammemSet gs0, GrammemSet gs1){
		return matchCase(gs0.extractGrammems(Case.class), gs1.extractGrammems(Case.class));
	}


	protected static Map<Case,Case> matchCase(Set<Case> set0, Set<Case> set1) {
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

	protected boolean isContained(IToken token0, IToken token1) {
		int sp0 = token0.getStartPosition();
		int ep0 = token0.getEndPosition();
		
		int sp1 = token1.getStartPosition();
		int ep1 = token1.getEndPosition();
		
		if(sp0<ep1&&sp1<ep0){
			return true;
		}
		return false;
	}

	public static final <T extends Grammem>UnaryMatcher<SyntaxToken> hasAll(T... tran) {
		return new HasAllGrammems(tran);
	}

	public static final <T extends Grammem>UnaryMatcher<SyntaxToken> has(T infn) {
		return new HasGrammem(infn);
	}

	public static final UnaryMatcher<SyntaxToken> not(final UnaryMatcher<SyntaxToken> infn) {
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
	public static final UnaryMatcher<SyntaxToken>or(UnaryMatcher<SyntaxToken>...matchers){
		return new OrMatcher<SyntaxToken>(SyntaxToken.class, matchers);
	}

	public static final UnaryMatcher<SyntaxToken> hasAny(Grammem...gf) {
		return new HasAnyOfGrammems(gf);
	}
	
	public static final UnaryMatcher<SyntaxToken> createCaseMatcher(Collection<Case> cases){
		HashSet<Case> set = new HashSet<Grammem.Case>();
		for(Case c : cases){
			set.addAll(caseMatchMap.get(c));
		}
		Case[] arr = set.toArray(new Case[set.size()]);
		return hasAny(arr);
	}

	public static final <T extends Grammem>UnaryMatcher<SyntaxToken> hasAny(Set<T> set) {
		return hasAny(set.toArray(new Grammem[set.size()]));
	}
	
	protected boolean isPrepOrConj(IToken newToken){
		if(!(newToken instanceof SyntaxToken)){
			return false;
		}
		String basicForm = ((SyntaxToken) newToken).getBasicForm();
		if(basicForm==null){
			return false;
		}
		MeaningElement prep = getPrepConjRegistry().getPreposition(basicForm);
		MeaningElement conj = getPrepConjRegistry().getConjunction(basicForm);
		return conj!=null || prep != null;
	}
	
	protected PrepConjRegistry getPrepConjRegistry() {
		if(prepConjRegistry==null){
			prepConjRegistry = new PrepConjRegistry(wordNet);
		}
		return prepConjRegistry;
	}
}
