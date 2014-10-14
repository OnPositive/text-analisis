package com.onpositive.semantic.words2;



public class SimpleWord extends Word{	

	@Override
	public String toString() {
		if (this.isVerb()){
			return "V:"+basicForm;
		}
		if (this.isAdjective()){
			return "A:"+basicForm;
		}
		if (this.isNoun()){
			if ((features&SimpleWord.FEATURE_NAME)!=0){
				return "NAME:"+basicForm;
			}
			if ((features&SimpleWord.FEATURE_TOPONIM)!=0){
				return "TOPONIM:"+basicForm;
			}
			return "N:"+basicForm;
		}
		return basicForm;
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private static final WordRelation[] NO_RELATIONS = new WordRelation[0];
	protected WordFormTemplate template;
	protected int id;
	protected String foundation;
	protected String foundation1;
	protected String foundation2;
	
	//protected WordRelation[] relations;
	int kind;
	int features;
	
	public boolean isLooksLikePersonalOrPlaceName(){
		if ((features&SimpleWord.FEATURE_NAME)!=0){
			return true;
		}
		if ((features&SimpleWord.FEATURE_TOPONIM)!=0){
			return true;
		}
		return false;
	}
	
	public int getFeatures(){
		return features;
	}
	
	protected final String basicForm;
	
	public SimpleWord(String basicForm,int id) {
		super();
		this.basicForm = basicForm;		
		this.id=id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((basicForm == null) ? 0 : basicForm.hashCode());
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
		SimpleWord other = (SimpleWord) obj;
		if (basicForm == null) {
			if (other.basicForm != null)
				return false;
		} else if (!basicForm.equals(other.basicForm))
			return false;
		return true;
	}
	

	@Override
	public boolean isNoun() {
		return (kind&NOUN)!=0;
	}

	@Override
	public boolean isAdjective() {
		return (kind&ADJ)!=0;
	}

	@Override
	public boolean isVerb() {
		return (kind&VERB)!=0;
	}

	@Override
	public String getBasicForm() {
		return basicForm;
	}

	@Override
	public String getFoundation(int number) {
		if (number==0){
			return foundation;
		}
		if (number==1){
			return foundation1;
		}
		if (number==2){
			return foundation2;
		}
		return null;
	}

	@Override
	protected void registerFoundation(int number, String foundation) {
		if (foundation!=null&&foundation.length()>0){
			if (number==0){
				if (this.foundation==null){
				this.foundation=foundation;
				}
			}
			if (number==1){
				if (this.foundation1==null){
				this.foundation1=foundation;
				}
			}
			if (number==2){
				if (this.foundation2==null){
				this.foundation2=foundation;
				}
			}
		}
	}

	@Override
	protected void setTemplate(WordFormTemplate findTemplate) {
		this.template=findTemplate;
	}

	@Override
	protected void setKind(int kind) {
		this.kind|=kind;		
	}

	@Override
	protected void setFeature(int featureKind) {
		this.features|=featureKind;
	}

	
	public Word[] getWords() {
		return new Word[]{this};
	}

	@Override
	public int id() {
		return id;
	}

	@Override
	public boolean hasFeature(int feature) {
		return (features&feature)!=0;
	}

	public int getKind() {
		return kind;
	}

	
}