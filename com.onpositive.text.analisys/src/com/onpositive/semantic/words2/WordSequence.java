package com.onpositive.semantic.words2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import com.carrotsearch.hppc.IntOpenHashSet;

public class WordSequence {

	RelationTarget[] targets;

	protected HashMap<RelationTarget, WordRelation[]> sequence = new HashMap<RelationTarget, WordRelation[]>();

	public WordSequence(ArrayList<RelationTarget> targets) {
		this.targets = targets.toArray(new RelationTarget[targets.size()]);
	}

	@Override
	public String toString() {
		return Arrays.toString(targets);
	}

	public RelationTarget getCore() {
		LinkedHashSet<RelationTarget> ns = new LinkedHashSet<RelationTarget>();
		for (RelationTarget t : targets) {			
			if (t instanceof SimpleWord) {
				SimpleWord w = (SimpleWord) t;
				if (w.isAdjective()) {
					continue;
				}
				String basicForm = w.getBasicForm();
				if (basicForm.equals("классификация")) {
					continue;
				}
				
				if (basicForm.equals("по")) {
					continue;
				}
				if (basicForm.equals("и")) {
					continue;
				}
				if (basicForm.equals("вид")) {
					continue;
				}
				if (w.getBasicForm().equals("типы")) {
					continue;
				}
				if (w.isNoun()) {
					ns.add(w);
				}
			}
		}
		if (ns.size() == 1) {
			return ns.iterator().next();
		}
		if (ns.size()==0){
			for (RelationTarget q:targets){
				Word[] words = q.getWords();
				for (Word qa:words){
					if (qa.isNoun()) {
						ns.add(qa);
					}
				}
			}
		}
		if (ns.size() == 1) {
			return ns.iterator().next();
		}
		RelationTarget cand = null;
		HashSet<RelationTarget>ns1=new HashSet<RelationTarget>();
		for (RelationTarget t:ns){
			if (t.getWords().length==1&&t.getWords()[0].getBasicForm().equals("история")) {
				continue;
			}
			if (t.getWords().length==1&&t.getWords()[0].getBasicForm().equals("год")) {
				continue;
			}
			if (t.getWords().length==1&&t.getWords()[0].getBasicForm().equals("вид")) {
				continue;
			}
			ns1.add(t);
		}
		if (ns1.size()==1)
		{
			return ns1.iterator().next();
		}
		l2: for (RelationTarget t : ns) {
			
			WordRelation[] r = sequence.get(t);
			if (r == null) {
				return null;
			}
			RelationTarget c = null;
			for (WordRelation q : r) {
				if (q.relation == NounFormRule.NOM_PL
						|| q.relation == NounFormRule.NOM_SG||q.relation==0) {
					if (q.relation == NounFormRule.NOM_PL) {
						if (t instanceof SimpleWord) {
							SimpleWord z = (SimpleWord) t;
							if ((z.features & SimpleWord.FEATURE_NAME) != 0) {
								continue;
							}
							if ((z.features & SimpleWord.FEATURE_TOPONIM) != 0) {
								continue;
							}
						}
					}
					if (cand == null) {
						cand = t;
					} else {
						c = cand;
						cand = null;
					}
				}
				if (q.relation == NounFormRule.GEN_SG
						|| q.relation == NounFormRule.GEN_PL) {
					if (c != null) {
						cand = c;
						continue l2;
					}
				}
			}
		}
		return cand;
	}

	public int matchRelated(WordSequence sequence) {
		AbstractRelationTarget core = (AbstractRelationTarget) getCore();
		AbstractRelationTarget core2 = (AbstractRelationTarget) sequence
				.getCore();
		if (core != null && core2 != null) {
			if (core.equals(core2)){
				return 2;
			}
			WordRelation[] relations = core.getRelations();
			WordRelation[] relations2 = core2.getRelations();
			IntOpenHashSet m = new IntOpenHashSet();
			for (WordRelation q : relations2) {
				q.owner=(SimpleWordNet) WordNetProvider.getInstance();
				m.add(q.word);
			}
			for (WordRelation z : relations) {
				z.owner=(SimpleWordNet) WordNetProvider.getInstance();
				if (m.contains(z.word)) {
					return 7;
				}
			}
			if (m.contains(core.id())){
				return 6;
			}
		}
		//if (core2==null){
		for (RelationTarget t:sequence.targets){
			if (t.equals(core)){
				return 10;
			}
		}
		//}
		return -1;
	}

	public Collection<RelationTarget> getNouns() {
		ArrayList<RelationTarget>tm=new ArrayList<RelationTarget>();
		boolean first=true;
		for (RelationTarget q:targets){
			if (q instanceof Word){
				Word z=(Word) q;
				String basicForm = z.getBasicForm();
				boolean equals = basicForm.equals("в");
				if (equals){
					if (!tm.isEmpty()){
						break;	
					}
					
				}
				if( !z.isNoun()){
					if (z.isVerb()||z.isAdjective()){
						continue;
					}
				}
				if (z.getBasicForm().equals("и")){
					continue;
				}
				if (z.getBasicForm().equals("по")){
					continue;
				}
				if (z.getBasicForm().equals("о")){
					continue;
				}
				if (z.getBasicForm().equals("с")){
					continue;
				}
				if (z.hasFeature(Word.FEATURE_TOPONIM)){
					continue;
				}
				WordRelation[] wordRelations = sequence.get(q);
				
				boolean hasTT=false;
				if (wordRelations==null){
					hasTT=true;
				}
				else for (WordRelation r:wordRelations){
					if (r.relation==NounFormRule.NOM_PL||(r.relation==NounFormRule.NOM_SG)){
						hasTT=true;
						if (first){
							tm.add(q);
							return tm;
						}
					}
				}
				
				if (!hasTT&&wordRelations.length>1){
					first=false;
					continue;
				}
			}
			first=false;
			tm.add(q);
		}
		return tm;
	}
}