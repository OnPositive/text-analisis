package com.onpositive.semantic.words2;

import java.io.Serializable;

public abstract class AbstractRelationTarget extends RelationTarget implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final WordRelation[] NO_RELATIONS = new WordRelation[0];
	protected int[] relations;

	public AbstractRelationTarget() {
		super();
	}

	
	public WordRelation[] getRelations() {
		if (relations==null)
		{
			return NO_RELATIONS;
		}
		WordRelation[] result=new WordRelation[relations.length/2];
		for (int a=0;a<relations.length;a+=2){
			WordRelation wordRelation = new WordRelation(null,relations[a+1],relations[a]);
			
			result[a/2]=wordRelation;
			
		}
		return result;
	}

	protected void registerRelation(int kind, RelationTarget wordRelation) {
		if (relations==null){
			relations=new int[0];
		}
		int length = relations.length;
		int[] ne=new int[length+2];
		System.arraycopy(relations, 0, ne,0, relations.length);
		ne[relations.length]=kind;
		ne[relations.length+1]=wordRelation.id();
		this.relations=ne;
	}
	
	public boolean isSynonim(AbstractRelationTarget other){
		int id = other.id();
		if (this.relations!=null){
			for (int a=0;a<this.relations.length;a+=2){
				int q=this.relations[a+1];
				if (q==id){
					int rel=this.relations[a];
					if (rel==WordRelation.SYNONIM|rel==WordRelation.SYNONIM_BACK_LINK){
						return true;
					}
				}				
			}
		}
		return false;		
	}
	public boolean isSpecialization(AbstractRelationTarget other){
		int id = other.id();
		if (this.relations!=null){
			for (int a=0;a<this.relations.length;a+=2){
				int q=this.relations[a+1];
				if (q==id){
					int rel=this.relations[a];
					if (rel==WordRelation.SPECIALIZATION|rel==WordRelation.SPECIALIZATION_BACK_LINK){
						return true;
					}
				}				
			}
		}
		return false;		
	}
	public boolean isGeneralization(AbstractRelationTarget other){
		int id = other.id();
		if (this.relations!=null){
			for (int a=0;a<this.relations.length;a+=2){
				int q=this.relations[a+1];
				if (q==id){
					int rel=this.relations[a];
					if (rel==WordRelation.GENERALIZATION|rel==WordRelation.GENERALIZATION_BACK_LINK){
						return true;
					}
				}				
			}
		}
		return false;		
	}
	
	public boolean isRelated(AbstractRelationTarget r){
		return isSynonim(r)||isGeneralization(r)||isSpecialization(r);
	}
	
	@Override
	public boolean matchRelated(RelationTarget relationTarget) {
		if (this.equals(relationTarget)){
			return true;
		}
		return isRelated((AbstractRelationTarget) relationTarget);
	}

}