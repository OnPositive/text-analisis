package com.onpositive.semantic.words3.dto;

import java.util.Arrays;
import com.carrotsearch.hppc.ByteArrayList;
import com.onpositive.semantic.words3.ReadOnlyWordNet;
import com.onpositive.semantic.words3.model.WordRelation;

import static com.onpositive.semantic.words3.hds.StringCoder.*;

public final class ConceptInfo {

	public int parentTextElement;
	public final int senseId;
	public final short kind;
	public final short features;
	public final WordRelation[] relations;
	private int sizeB=-1;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + features;
		result = prime * result + kind;
		result = prime * result + Arrays.hashCode(relations);
		result = prime * result + senseId;
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
		ConceptInfo other = (ConceptInfo) obj;
		if (features != other.features)
			return false;
		if (kind != other.kind)
			return false;
		if (!Arrays.equals(relations, other.relations))
			return false;
		if (senseId != other.senseId)
			return false;
		return true;
	}

	public ConceptInfo(int parentTextElement,int senseId, short kind, short features,
			WordRelation[] relations) {
		super();
		this.parentTextElement=parentTextElement;
		this.senseId = senseId;
		this.kind = kind;
		this.features = features;
		this.relations = relations;
	}	
	
	public byte[] toByteArray(){
		byte[] encodeWordInfo = encodeWordInfo(parentTextElement,senseId, kind, features,  relations);
		ConceptInfo fromBytes = ConceptInfo.getFromBytes(encodeWordInfo, 0);
		if (!fromBytes.equals(this)){
			throw new IllegalStateException();
		}
		return encodeWordInfo;
	}
	
	public static ConceptInfo getFromBytes(byte[] b,int position){
		int id=makeInt((byte)0, b[2+position], b[1+position], b[0+position]);
		short kind=(short) makeInt((byte)0, (byte)0, b[4+position], b[3+position]);
		short features=(short) makeInt((byte)0, (byte)0, b[6+position], b[5+position]);
		int pid=makeInt((byte)0, b[9+position], b[8+position], b[7+position]);
		WordRelation[] relations=ReadOnlyWordNet.decodeRelations(10+position, b);
		int length = ReadOnlyWordNet.length(relations);
		//not actually clear how to fill it;
		ConceptInfo wordSenseInfo = new ConceptInfo(pid,id, kind, features, relations);
		wordSenseInfo.sizeB=7+length;
		return wordSenseInfo;		
	}
	protected byte[] encodeWordInfo(int pel,int id,int kind,int feafures,WordRelation[] relations){
		if (kind>65535||feafures>65535){
			throw new IllegalStateException();
		}
		byte[] encodeRelations = ReadOnlyWordNet.encodeRelations(relations);
		byte[] totalResult=new byte[4+3+3+encodeRelations.length];
		totalResult[0]=int0(id);
		totalResult[1]=int1(id);
		totalResult[2]=int2(id);
		totalResult[3]=int0(kind);
		totalResult[4]=int1(kind);
		totalResult[5]=int0(feafures);
		totalResult[6]=int1(feafures);
		totalResult[7]=int0(pel);
		totalResult[8]=int1(pel);
		totalResult[9]=int2(pel);
		for (int a=0;a<encodeRelations.length;a++){
			totalResult[a+10]=encodeRelations[a];
		}
		return totalResult;
	}
	
	public static int getConceptLength(int addr,byte[] buf){
		return ReadOnlyWordNet.estimateRelationsLength(addr+10,buf)+10;
	}
	
	public byte[] encodeWordInfoSmall(int kind,int feafures,WordRelation[] relations){
		ByteArrayList rs=new ByteArrayList();
		if (kind<255){
			rs.add((byte) kind);
		}
		else{
			rs.add(int0(kind));
			rs.add(int1(kind));
		}
		if (feafures!=0){
			if (feafures<127){
				rs.add((byte) features);	
			}
			else{
				rs.add(int0(feafures));
				rs.add(int1(feafures));
			}
		}
		if (relations.length==0){
			return rs.toArray();
		}
		byte[] encodeRelations = ReadOnlyWordNet.encodeRelations(relations);
		rs.add(encodeRelations);
		return rs.toArray();
	}

	public int sizeInBytes() {
		return sizeB;
	}
}