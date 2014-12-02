package com.onpositive.text.analysis.syntax;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.onpositive.semantic.wordnet.AbstractWordNet;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.Grammem.Case;
import com.onpositive.semantic.wordnet.Grammem.SingularPlural;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.AbstractParser;

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

}