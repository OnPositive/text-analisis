package com.onpositive.semantic.references;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import com.onpositive.semantic.parsing.ParsedSequence;
import com.onpositive.semantic.parsing.ParsedText;
import com.onpositive.semantic.parsing.ParsedWord;

public class TermReferenceLayer {

	protected ArrayList<TermReference>references=new ArrayList<TermReference>();
	
	protected ITermLookup lookup;

	private HashMap<ITerm, ArrayList<TermReference>> refMap;

	private ArrayList<ParsedSequence> sequences;
	
	private HashMap<ParsedWord, ArrayList<TermReference>>wordsToRefs=new HashMap<ParsedWord, ArrayList<TermReference>>();
	
	public void parseRefs() {
		preparePotencialReferences();
		reconcileReferences();
		initRefMap();
		
	}
	
	public TermReferenceLayer(ParsedText text){
		this.sequences=text.sequences();
	}

	private void preparePotencialReferences() {
		for(ParsedSequence s:sequences){
			for (ParsedWord w:s.getWords()){
				Collection<TermReference> doLookup = lookup.doLookup(w);
				if (doLookup!=null&&doLookup.size()>0){
					for (TermReference q:doLookup){
						for (ParsedWord w2:q.words){
							ArrayList<TermReference> arrayList = wordsToRefs.get(w2);
							if (arrayList==null){
								arrayList=new ArrayList<TermReference>();
								wordsToRefs.put(w2, arrayList);
							}
							arrayList.add(q);
						}
					}
					references.addAll(doLookup);
				}
			}
		}
	}
	
	public void initRefMap() {
		refMap = new HashMap<ITerm, ArrayList<TermReference>>();
		for (TermReference q:references){
			Collection<ITerm> terms = q.terms();
			for (ITerm t:terms){
				ArrayList<TermReference> arrayList = refMap.get(t);
				if (arrayList==null){
					arrayList=new ArrayList<TermReference>();
					refMap.put(t, arrayList);
				}
				arrayList.add(q);
			}
		}
		scorePurge();
	}


	public void scorePurge() {
		HashMap<ITerm, Integer>scores=new HashMap<ITerm, Integer>();
		addCorelationScores(scores);
		//System.out.println(index);
		addSemanticConnectionScores(scores);
		//purgeZeros(scores);
		correctMultiRefs(scores);
		System.out.println(references);
	}


	public void correctMultiRefs(HashMap<ITerm, Integer> scores) {
		for(TermReference q:new ArrayList<TermReference>(references)){
			if (q instanceof MultiReference){
				MultiReference ms=(MultiReference) q;
				if (ms.terms.isEmpty()){
					references.remove(ms);
				}
				if (ms.terms.size()==1){
					references.remove(ms);
					TermReference rr=new TermReference(ms.words, ms.terms.iterator().next());
					references.add(rr);
				}
				ITerm bestCandidate=null;
				int maxScore=0;
				for (ITerm t:ms.terms){
					Integer integer = scores.get(t);
					if (integer!=null)
					if (integer>maxScore){
						bestCandidate=t;
						maxScore=integer;
					}
				}
				boolean cb=true;
				for (ITerm t:ms.terms){
					Integer integer = scores.get(t);
					if (integer!=null)
					if (integer!=maxScore){
						if (maxScore-integer<3){
							cb=false;
							break;
						}
					}
					else{
						if (t!=bestCandidate){
							cb=false;
						}
					}
				}
				if (bestCandidate!=null&&cb){
					references.remove(ms);
					TermReference rr=new TermReference(ms.words, bestCandidate);
					references.add(rr);
				}
			}
		}
	}


	public void purgeZeros(HashMap<ITerm, Integer> scores) {
		for (ITerm q: refMap.keySet()){
			Integer integer = scores.get(q);
			if (integer==null||integer.intValue()==0){
				ArrayList<TermReference> c = refMap.get(q);
				for (TermReference qa:c){
					if (qa instanceof MultiReference){
						MultiReference mr=(MultiReference) qa;
						mr.terms.remove(q);						
					}
					else{
						references.remove(qa);
					}
				}
			}
		}
	}


	public void addSemanticConnectionScores(HashMap<ITerm, Integer> scores) {
		for (ITerm t:refMap.keySet()){
			List<ISemanticConnection> connections = t.getConnections();
			for (ISemanticConnection cm:connections){
				ArrayList<TermReference> values = refMap.get(t);
				for (TermReference tr:values){
					int match = cm.match(tr);
					if (match>0){
					inc(scores, t, match);
					}
				}
			}
		}
	}


	public void addCorelationScores(HashMap<ITerm, Integer> scores) {
		for (ITerm q:refMap.keySet()){
			for (ITerm q1:refMap.keySet()){
				if (q!=q1){
					HashSet<TermReference>ts = new HashSet<TermReference>(refMap.get(q));
					HashSet<TermReference>ts1 = new HashSet<TermReference>(refMap.get(q1));
					boolean removeAll = ts.removeAll(refMap.get(q1));
					boolean removeAll1 = ts1.removeAll(refMap.get(q));
					if (removeAll||removeAll1){
						//this rerms are located in same position
						//they are competing so we should ignore coreferences from them
						continue;
					}

					int correlate = correlate(q,q1);
					if (correlate>0){						
						int delta=correlate;
						inc(scores, q, delta);
					}
				}
			}
		}
	}


	public void inc(HashMap<ITerm, Integer> scores, ITerm q, int delta) {
		Integer integer = scores.get(q);
		if (integer==null){
			integer=0;
		}
		scores.put(q, integer+delta);
	}
	
	private int correlate(ITerm q, ITerm q1) {
		int relatedTo = q.relatedTo(q1);
		int relatedTo1 = q1.relatedTo(q);
		if (relatedTo>0&&relatedTo1>0){
			return relatedTo+relatedTo1*4;
		}
		return relatedTo+relatedTo1;
	}

	private void reconcileReferences() {
		LinkedHashSet<TermReference>newList=new LinkedHashSet<TermReference>();
		int lastOffset=-1;
		TermReference lastTerm=null;
		ArrayList<TermReference>toR=new ArrayList<TermReference>();
		for (TermReference r:references){
			if (r.offset==lastOffset){
				if (lastTerm!=null){
					if (r.length>lastTerm.length||(r.length==lastTerm.length&&r.rating>lastTerm.rating)){
						newList.remove(lastTerm);
						lastTerm=r;
						newList.add(r);
					}
					else if (r.length==lastTerm.length){
						if (r.start.equals(lastTerm.start)){
							continue;
						}
						if (lastTerm instanceof MultiReference){
							MultiReference mz=(MultiReference) lastTerm;
							mz.add(r.start);
						}
						else{
							MultiReference multiReference = new MultiReference(lastTerm.words, lastTerm.start);
							multiReference.add(r.start);
							newList.remove(lastTerm);
							newList.add(multiReference);
							lastTerm=multiReference;
						}
					}
				}
				else{
					lastTerm=r;
					lastOffset=r.offset;
					newList.add(r);
				}
			}
			else{
				if (lastTerm!=null){
					if(r.offset+r.length<=lastTerm.offset+lastTerm.length){
						continue;
						//it is inside already chosen term;
					}
					else{
						lastTerm=r;
						lastOffset=r.offset;
						newList.add(r);
						//lets add something
					}
				}
				else{
					lastTerm=r;
					lastOffset=r.offset;
					newList.add(r);
					//lets add something
				}
			}			
		}	
		if(!newList.contains(lastTerm)&&lastTerm!=null){
			newList.add(lastTerm);
		}
		newList.removeAll(toR);
		references=new ArrayList<TermReference>(newList);
	}
	
	public TermReference[] getReferences(){
		return references.toArray(new TermReference[references.size()]);
	}

}
