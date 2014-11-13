package com.onpositive.text.analysis.lexic.scalar;

import java.util.ArrayList;

import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.ScalarToken;
import com.onpositive.text.analysis.utils.Utils;
import com.onpositive.text.analysis.utils.VulgarFraction;

class ScalarProcessingData{
	
	private ArrayList<IToken> tokens = new ArrayList<IToken>();
	
	private DelimeterPattern pattern;
	
	private int startPosition;
	
	private int endPosition;
	
	private StringBuilder bld = new StringBuilder();
	
	private boolean invalidPattern = false;
	
	private boolean isFraction;
	
	void reset(DelimeterPattern pattern){
		this.pattern = pattern;
		this.startPosition = 0;
		this.endPosition = 0;
		this.isFraction = false;
		this.invalidPattern = false;
		this.bld.delete(0, this.bld.length());
		this.tokens.clear();
	}
	
	void appendToken(IToken token,boolean forceDump){			
		
		if(token!=null){
			if(bld.length()==0){
				startPosition = token.getStartPosition();
			}
			endPosition = token.getEndPosition();
			if(isFraction||token.getType()==IToken.TOKEN_TYPE_VULGAR_FRACTION){
				forceDump = true;
			}
			else{
				bld.append(token.getStringValue());
				token = null;
			}
		}
		if(forceDump){
			if(token!=null||bld.length()!=0){
				IToken scalar = ScalarParser.createScalar(bld.toString(), token,startPosition,endPosition);
				if(scalar!=null){
					tokens.add(scalar);
				}
				else{
					invalidPattern = true;
				}
				bld.delete(0, bld.length());
				isFraction = false;
			}
		}
	}
	


	public DelimeterPattern getPattern() {
		return pattern;
	}

	public void setInvalidPattern(boolean b) {
		this.invalidPattern = b;		
	}

	public ArrayList<IToken> getTokens() {
		return new ArrayList<IToken>(this.tokens);
	}

	public void setFraction(boolean b) {
		this.isFraction = b;		
	}

}
