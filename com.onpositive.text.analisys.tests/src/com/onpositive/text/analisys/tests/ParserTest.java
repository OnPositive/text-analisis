package com.onpositive.text.analisys.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.onpositive.text.analysis.BasicCleaner;
import com.onpositive.text.analysis.CompositToken;
import com.onpositive.text.analysis.IParser;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.ParserComposition;
import com.onpositive.text.analysis.lexic.ComplexClause;
import com.onpositive.text.analysis.lexic.DateToken;
import com.onpositive.text.analysis.lexic.DimensionToken;
import com.onpositive.text.analysis.lexic.ScalarToken;
import com.onpositive.text.analysis.lexic.dimension.Unit;
import com.onpositive.text.analysis.syntax.ClauseToken;
import com.onpositive.text.analysis.syntax.PrepositionGroupToken;
import com.onpositive.text.analysis.syntax.SentenceToken;
import com.onpositive.text.analysis.syntax.SyntaxToken;
import com.onpositive.text.analysis.utils.TokenLogger;

public class ParserTest extends TestCase {
	
	private static final Set<Class<?>> printTreeClasses = new HashSet<Class<?>>(Arrays.asList(
			SyntaxToken.class, ClauseToken.class, PrepositionGroupToken.class, CompositToken.class, ComplexClause.class));
	
	private static final String childOffStr = "  ";
	protected ParserComposition composition;
	

	protected void setParsers(IParser... parsers){
		if(parsers==null||parsers.length==0){
			return;
		}
		else{
			this.composition = new ParserComposition(parsers);
		}
	}
	
	
	protected List<IToken> process(String str){
		List<IToken> processed = composition.parse(str);
		ArrayList<IToken> list = new ArrayList<IToken>();
		for(IToken t : processed){
			if(t instanceof SentenceToken){
				list.addAll(new BasicCleaner().clean(t.getChildren()));
			}
			else{
				list.add(t);
			}
		}
		printTokens(list);
		return list;
	}	
	
	
	protected static void printTokens(List<IToken> processed) {
		
		System.out.println();
		System.out.println("-----");
		
		if(processed==null||processed.isEmpty()){
			return;
		}
		
		int l = (""+processed.get(processed.size()-1).getEndPosition()).length();
		
		for(IToken t : processed){
			System.out.format("%0" + l + "d", t.getStartPosition());
			System.out.print("-");
			System.out.format("%0" + l + "d", t.getEndPosition());
			System.out.println( " " + printToken(t, l+l+2).trim());//TokenTypeResolver.getResolvedType(t) + " " + t.getStringValue());
		}
	}
	
	protected static void assertTestDimension(double value, Unit unit,List<IToken> tk){
		boolean found=false;
		for (IToken z:tk){
			if (z instanceof DimensionToken){
				DimensionToken k=(DimensionToken) z;
				if (k.getValue()==value){
					Unit unit0 = k.getUnit();
					String shortName0 = unit0.getShortName().toLowerCase();
					String shortName = unit.getShortName().toLowerCase();
					if(shortName0.equals(shortName)){
						if(unit0.getKind()==unit.getKind()){
							found = true;
						}
					}
				}
			}
		}
		TestCase.assertTrue(found);
	}
	
	protected static void assertTestDimension(Double[] values, Unit[] units,List<IToken> tk) {
		int ind = 0 ;
		for (IToken z:tk){
			if (z instanceof DimensionToken){
				DimensionToken k=(DimensionToken) z;
				if (k.getValue()==values[ind]){
					Unit unit0 = k.getUnit();
					Unit unit = units[ind];
					String shortName = unit.getShortName().toLowerCase();
					String shortName0 = unit0.getShortName().toLowerCase();
					if(shortName0.equals(shortName)){
						if(unit0.getKind()==unit.getKind()){
							ind++;
						}
					}					
				}
			}
		}
		TestCase.assertTrue(ind==values.length);
	}
	
	void assertTestScalar(double value,List<IToken>tk){
		boolean found=false;
		for (IToken z:tk){
			if (z instanceof ScalarToken){
				ScalarToken k=(ScalarToken) z;
				if (k.getValue()==value){
					found=true;
				}
			}
		}
		TestCase.assertTrue(found);
	}
	void assertTestDate(Integer year,Integer month,Integer day,List<IToken>tk){
		boolean found=false;
		for (IToken z:tk){
			if (z instanceof DateToken){
				DateToken k=(DateToken) z;
				if (year!=null){
					TestCase.assertEquals(year, k.getYear());
					found=true;
				}
				if (month!=null){
					TestCase.assertEquals(month, k.getMonth());
					found=true;
				}
				if (day!=null){
					TestCase.assertEquals(day, k.getDay());
					found=true;
				}
			}
		}
		TestCase.assertTrue(found);
	}
	
	public static String printToken(IToken token, int off){
		
		StringBuilder offsetBld = new StringBuilder();
		for(int i = 0 ; i < off ; i ++){
			offsetBld.append(" ");
		}
		String offStr = offsetBld.toString();
		
		StringBuilder bld = new StringBuilder();
		
		bld.append(offStr);
		bld.append(TokenTypeResolver.getResolvedType(token));
		
		if(printTreeClasses.contains(token.getClass())){
			IToken mainGroup = token instanceof SyntaxToken ? ((SyntaxToken)token).getMainGroup() : null;
			List<IToken> children = token.getChildren();
			bld.append("(");
			if(token.getType()==IToken.TOKEN_TYPE_CLAUSE){
				ClauseToken ct = (ClauseToken) token;
				{
					bld.append("\n");
					SyntaxToken subject = ct.getSubject();
					String childStr = subject != null? printToken(subject,off + 2).trim() : "no subject";
					bld.append(offStr).append(childOffStr).append("<subject>");
					bld.append(childStr);
				}
				{
					bld.append("\n");
					SyntaxToken predicate = ct.getPredicate();
					String childStr = predicate != null ? printToken(predicate,off + 2).trim() : "no predicate";;
					bld.append(offStr).append(childOffStr).append("<predicate>");
					bld.append(childStr);
				}				
			}
			else if(token.getType() == IToken.TOKEN_TYPE_PREPOSITION_GROUP){
				PrepositionGroupToken pgt = (PrepositionGroupToken) token;
				{
					bld.append("\n");
					SyntaxToken prepToken = pgt.getPrepToken();
					String childStr = printToken(prepToken,off + 2).trim();
					bld.append(offStr).append(childOffStr).append("<preposition>");
					bld.append(childStr);
				}
				{
					bld.append("\n");
					SyntaxToken word = pgt.getWord();
					String childStr = word != null ? printToken(word,off + 2).trim() : "no predicate";;
					bld.append(offStr).append(childOffStr);
					bld.append(childStr);
				}
			}
			else{
				for(int i = 0 ; i < children.size() ; i++){
					bld.append("\n");
					IToken ch = children.get(i);
					String childStr = printToken(ch,off + 2);
					if(ch==mainGroup){
						bld.append(offStr).append(childOffStr).append("<main>");
						childStr = childStr.trim();
					}
					bld.append(childStr);
				}
			}
			bld.append("  )");
		}
		else{
			bld.append(" ").append(token.getStableStringValue());
		}
		String result = bld.toString();
		return result;
	}
	
	protected static void assertTestTokenPrint(List<IToken> tokens, String... print){
		boolean gotPrint = false;
l0:		for(String s : print){
			String str = s.replaceAll("(\\s|\\,)", "");			
			for(IToken token : tokens){
				String s1 = printToken(token,0).replaceAll("(\\s|\\,)", "");
				if(str.equals(s1)){
					gotPrint = true;
					break l0;
				}
			}
		}
		
		TestCase.assertTrue(gotPrint);
	}
	
	protected static void assertTestTokenPrintContains(String print, List<IToken> tokens){
		String str = print.replaceAll("(\\s|\\,)", "");
		boolean gotPrint = false;
		for(IToken token : tokens){
			String s1 = printToken(token,0).replaceAll("(\\s|\\,)", "");
			if(s1.contains(str)){
				gotPrint = true;
				break;
			}
		}
		TestCase.assertTrue(gotPrint);
	}
	
	public void setLogger(IParser parser){
		String loggerPath = System.getProperty("loggerPath");
		if(loggerPath == null){
			return;
		}
		TokenLogger logger = new TokenLogger(loggerPath);
		logger.clean();
		parser.setLogger(logger);
	}
}
