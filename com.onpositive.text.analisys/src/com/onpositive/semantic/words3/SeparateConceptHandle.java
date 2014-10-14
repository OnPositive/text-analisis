package com.onpositive.semantic.words3;

import com.onpositive.semantic.words3.ReadOnlyWordNet.WordStore;
import com.onpositive.semantic.words3.hds.StringCoder;
import com.onpositive.semantic.words3.model.ConceptElement;
import com.onpositive.semantic.words3.model.TextElement;
import com.onpositive.semantic.words3.model.WordRelation;

public class SeparateConceptHandle extends ConceptElement{
	
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
		SeparateConceptHandle other = (SeparateConceptHandle) obj;
		if (address != other.address)
			return false;
		if (this.store==other.store){
			return false;
		}
		return true;
	}
	
	public SeparateConceptHandle(int address, WordStore store) {
		this.address=address;
		this.store=store;
	}
	
	public int id(){
		int address2 = address;
		byte[] block2 = this.store.buffer();
		return StringCoder.makeInt(block2[address2+2], block2[address2+1], block2[address2]);		
	}
	
	public int textElementId(){
		int address2 = address;
		byte[] block2 = this.store.buffer();
		return StringCoder.makeInt(block2[address2+9], block2[address2+8], block2[address2+7]);		
	}
	
	public short getKind(){
		int address2 = address;
		byte[] block2 = this.store.buffer();
		return StringCoder.makeShort(block2[address2+4], block2[address2+3]);
	}

	public short getFeatures(){
		int address2 = address;
		byte[] block2 = this.store.buffer();
		return StringCoder.makeShort(block2[address2+6], block2[address2+5]);
	}


	@Override
	public WordRelation[] getSemanticRelations() {
		return ReadOnlyWordNet.decodeRelations(address+10, this.store.buffer());
	}

	@Override
	public TextElement getParentTextElement() {
		return this.store.getWordNet().getWordElement(textElementId());
	}
}