package com.onpositive.semantic.words3;

import java.util.LinkedHashSet;

import com.onpositive.semantic.words3.ReadOnlyWordNet.GrammarRelation;
import com.onpositive.semantic.words3.ReadOnlyWordNet.WordStore;
import com.onpositive.semantic.words3.dto.ConceptInfo;
import com.onpositive.semantic.words3.hds.StringCoder;
import com.onpositive.semantic.words3.model.ConceptElement;
import com.onpositive.semantic.words3.model.TextElement;
import com.onpositive.semantic.words3.model.WordRelation;

public class SenseElementHandle extends TextElement{
	
	public final class EmbeddedConcept extends ConceptElement {
		private final byte count;

		public EmbeddedConcept(byte count) {
			this.count = count;
		}

		@Override
		public int id() {
			return SenseElementHandle.this.id();					
		}

		@Override
		public WordRelation[] getSemanticRelations() {
			return basicGetRelations(count);
		}

		@Override
		public TextElement getParentTextElement() {
			return SenseElementHandle.this;
		}

		@Override
		public short getKind() {
			int msk = -((int) count);
			msk += WordStore.CUSTOM_CASE_OFFSET;
			int pos=address+4;
			boolean smallKind = (msk & WordStore.SMALL_KIND) != 0;
			if (smallKind) {
				return store.buffer()[pos];												
			} else {
				return ReadOnlyWordNet.WordStore.makeShort(store.buffer()[pos+1],store.buffer()[pos]);						
			}					
		}

		@Override
		public short getFeatures() {
			int msk = -((int) count);
			msk += WordStore.CUSTOM_CASE_OFFSET;
			int pos=address+4;
			boolean noFeatures = (msk & WordStore.NO_FEATURES) != 0;
			boolean smallFeatures = (msk & WordStore.SMALL_FEATURES) != 0;
			boolean smallKind = (msk & WordStore.SMALL_KIND) != 0;
			if (smallKind) {
				pos++;
			} else {
				pos += 2;
			}
			if (!noFeatures) {
				if (smallFeatures) {
					return store.buffer()[pos];			
				} else {
					return ReadOnlyWordNet.WordStore.makeShort(store.buffer()[pos+1],store.buffer()[pos]);
				}
			}
			return 0;
		}
	}

	private static final WordRelation[] WORD_RELATIONS = new WordRelation[0];

	protected final int address;
	
	protected final WordStore store;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + address;
		result = prime * result + store.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SenseElementHandle other = (SenseElementHandle) obj;
		if (address != other.address)
			return false;
		if (this.store==other.store){
			return false;
		}
		return true;
	}
	
	public SenseElementHandle(int address, WordStore store) {
		this.address=address;
		this.store=store;
	}
	
	public int id(){
		int address2 = address;
		byte[] block2 = this.store.buffer();
		return StringCoder.makeInt(block2[address2+2], block2[address2+1], block2[address2]);		
	}


	@Override
	public String getBasicForm() {
		return this.store.getStringBeforeData(store.buffer(), address);
	}

	@Override
	public WordRelation[] getSemanticRelations() {
		byte count = this.store.buffer()[address + 3];
		if (count<0){
			return basicGetRelations(count);
		}
		else{
			int sPos=address + 4;
			LinkedHashSet<WordRelation>relations=new LinkedHashSet<WordRelation>();
			byte[] block2 = this.store.buffer();
			for (int a=0;a<count;a++){
				WordRelation[] semanticRelations = new SeparateConceptHandle(sPos,store).getSemanticRelations();
				for (WordRelation w:semanticRelations){
					GrammarRelation g=(GrammarRelation) w;
					g.setOwner(store.getWordNet());
					relations.add(w);
				}
				sPos+=ConceptInfo.getConceptLength(sPos, block2);				
			}
			return relations.toArray(new WordRelation[relations.size()]);
		}
	}

	@Override
	public ConceptElement[] getConcepts() {
		final byte count = this.store.buffer()[address + 3];
		if (count<0){
			return new ConceptElement[]{new EmbeddedConcept(count)};
		}
		else{
			ConceptElement[] result=new ConceptElement[count];
			int sPos=address + 4;
			byte[] block2 = this.store.buffer();
			for (int a=0;a<count;a++){
				result[a]=new SeparateConceptHandle(sPos,store);
				sPos+=ConceptInfo.getConceptLength(sPos, block2);
			}
			return result;
		}
	}

	protected WordRelation[] basicGetRelations(byte count) {
		byte[] buffer = store.buffer();
		int msk = -((int) count);
		msk += WordStore.CUSTOM_CASE_OFFSET;
		int pos=address+4;
		boolean noFeatures = (msk & WordStore.NO_FEATURES) != 0;
		boolean smallFeatures = (msk & WordStore.SMALL_FEATURES) != 0;
		boolean smallKind = (msk & WordStore.SMALL_KIND) != 0;
		boolean noRelations = (msk & WordStore.NO_RELATIONS) != 0;
		if (smallKind) {
			pos++;
		} else {
			pos += 2;
		}
		if (!noFeatures) {
			if (smallFeatures) {
				pos++;
			} else {
				pos += 2;
			}
		}
		WordRelation[] rels = null;
		if (noRelations) {
			rels = WORD_RELATIONS;
		} else
			rels = ReadOnlyWordNet.decodeRelations(pos, buffer);
		for (WordRelation q:rels){
			GrammarRelation g=(GrammarRelation) q;
			g.setOwner(store.getWordNet());
		}
		return rels;
	}
}