package com.onpositive.text.analysis.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.onpositive.semantic.wordnet.GrammarRelation;
import com.onpositive.semantic.wordnet.Grammem;
import com.onpositive.semantic.wordnet.MeaningElement;
import com.onpositive.text.analysis.AbstractToken;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.WordFormToken;

public class SyntaxToken extends AbstractToken{

	public SyntaxToken(int tokenType, SyntaxToken mainGroup, int startPosition, int endPosition) {
		super(tokenType, startPosition, endPosition);
		this.mainGroup = mainGroup;
	}

	protected SyntaxToken mainGroup;
	
	
	public boolean hasGrammem(Grammem gr){
		
		WordFormToken mainWord = getMainWord();
		MeaningElement meaningElement = mainWord.getMeaningElement();
		Set<Grammem> grammems = meaningElement.getGrammems();
		if(grammems!=null&&grammems.contains(gr)){
			return true;
		}
		List<GrammarRelation> grammarRelations = mainWord.getGrammarRelations();
		if(grammarRelations!=null){
			for(GrammarRelation rel : grammarRelations){
				if(rel.hasGrammem(gr)){
					return true;
				}
			}
		}
		return false;
	}
	
	public Set<Grammem> getAllGrammems(){
		Set<Grammem> grammems = new HashSet<Grammem>();
		
		WordFormToken mainWord = getMainWord();
		MeaningElement meaningElement = mainWord.getMeaningElement();
		Set<Grammem> meGr = meaningElement.getGrammems();
		if(meGr!=null){
			grammems.addAll(meGr);
		}
		List<GrammarRelation> grammarRelations = mainWord.getGrammarRelations();
		if(grammarRelations!=null){
			for(GrammarRelation rel : grammarRelations){
				HashSet<Grammem> grammems2 = rel.getGrammems();				
				if(grammems2!=null){
					grammems.addAll(grammems2);
				}
			}
		}
		return grammems;
	}
	
	
	public boolean hasAnyGrammem(Collection<? extends Grammem> col){
		
		WordFormToken mainWord = getMainWord();
		MeaningElement meaningElement = mainWord.getMeaningElement();
		Set<Grammem> grammems = meaningElement.getGrammems();
		if(grammems!=null){
			for(Grammem gr : col){
				if(grammems.contains(gr)){
					return true;
				}
			}
		}
		List<GrammarRelation> grammarRelations = mainWord.getGrammarRelations();
		if(grammarRelations!=null){
			for(GrammarRelation rel : grammarRelations){
				for(Grammem gr : col){
					if(rel.hasGrammem(gr)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	public boolean hasAllGrammems(Collection<? extends Grammem> col)
	{
		HashSet<Grammem> set = new HashSet<Grammem>();
		WordFormToken mainWord = getMainWord();
		MeaningElement meaningElement = mainWord.getMeaningElement();
		Set<Grammem> grammems = meaningElement.getGrammems();
		if(grammems!=null){			
			for(Grammem gr : col){
				if(grammems.contains(gr)){
					set.add(gr);
					if(set.size() == col.size()){
						return true;
					}
				}
			}
		}
		List<GrammarRelation> grammarRelations = mainWord.getGrammarRelations();
		if(grammarRelations!=null){
			for(GrammarRelation rel : grammarRelations){
				for(Grammem gr : col){
					if(rel.hasGrammem(gr)){
						set.add(gr);
						if(set.size() == col.size()){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public String getStringValue() {
		
		StringBuilder bld = new StringBuilder();
		List<IToken> children = getChildren();
		for(IToken t : children){
			bld.append(t.getStringValue()).append(" ");
		}
		return bld.toString();
	}

	public WordFormToken getMainWord() {		
		SyntaxToken token = this;
		while(!(token instanceof WordFormToken)){
			token = token.getMainGroup();
		}
		return (WordFormToken) token;
	}
	
	public SyntaxToken getMainGroup(){
		return mainGroup;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((mainGroup == null||mainGroup==this) ? 0 : mainGroup.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SyntaxToken other = (SyntaxToken) obj;
		if (mainGroup == null) {
			if (other.mainGroup != null)
				return false;

		} else{
			if(other.mainGroup==null){
				return false;
			}
			if(mainGroup.getType() == IToken.TOKEN_TYPE_WORD_FORM){
				if(other.mainGroup.getType() != IToken.TOKEN_TYPE_WORD_FORM){
					return false;
				}
			}
			else if (!mainGroup.equals(other.mainGroup))		
				return false;
		}
		if(children==null){
			if(other.children!=null){
				return false;
			}
		}
		else{
			if(other.children==null){
				return false;
			}
			int size = this.children.size();
			if(size!=other.children.size()){
				return false;
			}
			for(int i = 0 ; i < size ; i++){
				if(!this.children.get(i).equals(other.children.get(i))){
					return false;
				}
			}
		}
		return true;
	}


	
	
}
