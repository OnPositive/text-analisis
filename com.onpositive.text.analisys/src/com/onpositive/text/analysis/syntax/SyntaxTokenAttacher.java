package com.onpositive.text.analysis.syntax;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.TokenAttacher;

public class SyntaxTokenAttacher extends TokenAttacher {
	
	@Override
	protected Set<AttachmentPlace> findAttachmentPlaces(IToken token) {
		
		if(token instanceof SyntaxToken){
			HashSet<AttachmentPlace> set = new HashSet<AttachmentPlace>();
			SyntaxToken st = (SyntaxToken) token;			
			ArrayList<SyntaxToken> list = new ArrayList<SyntaxToken>();
			list.add(st);
			for(int i  = 0 ; i < list.size() ; i++){
				SyntaxToken t = list.get(i);
				List<IToken> parents = t.getParents();
				for(IToken p : parents){
					if(p instanceof ClauseToken){
						set.add(new AttachmentPlace(p, t));
					}
					if(p instanceof SyntaxToken){
						SyntaxToken stp = (SyntaxToken) p;
						if(stp.getMainWord()==t){
							list.add(stp);
						}
						else{
							set.add(new AttachmentPlace(p, t));
						}
					}
					else{
						set.add(new AttachmentPlace(p, t));
					}
				}
			}
			return set;
		}
		else{
			return super.findAttachmentPlaces(token);
		}
	}

	@Override
	protected IToken createNewToken(IToken token, IToken attached, int type) {
		
		int sp0 = token.getStartPosition();
		int sp1 = attached.getStartPosition();
		int startPosition = Math.min(sp0, sp1);
		int endPosition = Math.max(token.getEndPosition(), attached.getEndPosition());
		
		SyntaxToken newToken = new SyntaxToken(type, (SyntaxToken) token, null, startPosition, endPosition);
		
		if(sp0<=sp1){
			newToken.addChild(token);
			newToken.addChild(attached);
		}
		else{
			newToken.addChild(attached);
			newToken.addChild(token);						
		}
		
		
		
		return newToken;
	}

}
