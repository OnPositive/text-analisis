package com.onpositive.text.analisys.tests;

import java.util.List;

import com.onpositive.semantic.wordnet.composite.CompositeWordnet;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.dimension.Unit;
import com.onpositive.text.analysis.lexic.dimension.UnitKind;
import com.onpositive.text.analysis.syntax.SyntaxParser;

public class FragmentExtractorTest extends ParserTest{

	private Unit kilometerPerHourUnit = new Unit("километр в час",UnitKind.SPEED,1);
	
	public FragmentExtractorTest() {
		super();
		CompositeWordnet wn=new CompositeWordnet();
		wn.addUrl("/numerics.xml");
		wn.addUrl("/dimensions.xml");
		wn.addUrl("/modificator-adverb.xml");
		wn.addUrl("/prepositions.xml");
		wn.addUrl("/conjunctions.xml");
		wn.addUrl("/modalLikeVerbs.xml");
		wn.prepare();
		SyntaxParser syntaxParser = new SyntaxParser(wn);
		this.composition = syntaxParser;
	}
	
	public void test_brackets000(){
		String str = "Петя (он работает (в институте) программистом) является моим другом.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>WORD_FORM("
		          + "<main>WORD_FORM петя([имя, мр, од, СУЩ])"
		          + "BRACKETS("
		            + "REGION_BOUND("
		              + "SYMBOL (  )"
		            + "CLAUSE("
		              + "<subject>WORD_FORM он([мр, Анаф, МС, 3л])"
		              + "<predicate>VERB_NOUN("
		                + "<main>WORD_FORM("
		                  + "<main>WORD_FORM работаю([ГЛ, неперех, несов])"
		                  + "BRACKETS("
		                    + "REGION_BOUND("
		                      + "SYMBOL (  )"
		                    + "WORD_FORM в([ПР])"
		                    + "WORD_FORM институт([мр, СУЩ, неод])"
		                    + "REGION_BOUND("
		                      + "SYMBOL )  )  )  )"
		                + "WORD_FORM программист([мр, од, СУЩ])  )  )"
		            + "CLAUSE("
		              + "<subject>WORD_FORM он([мр, Анаф, МС, 3л])"
		              + "<predicate>VERB_NOUN("
		                + "<main>WORD_FORM("
		                  + "<main>WORD_FORM работаю([ГЛ, неперех, несов])"
		                  + "BRACKETS("
		                    + "REGION_BOUND("
		                      + "SYMBOL (  )"
		                    + "WORD_FORM в([ПР])"
		                    + "WORD_FORM институт([мр, СУЩ, неод])"
		                    + "REGION_BOUND("
		                      + "SYMBOL )  )  )  )"
		                + "WORD_FORM программист([мр, од, СУЩ])  )  )"
		            + "WORD_FORM программист([мр, од, СУЩ])"
		            + "REGION_BOUND("
		              + "SYMBOL )  )  )  )"
		        + "<predicate>VERB_NOUN("
		          + "<main>WORD_FORM являюсь([ГЛ, неперех, несов])"
		          + "NOUN_ADJECTIVE("
		            + "WORD_FORM мой([мест-п, ПРИЛ])"
		            + "<main>WORD_FORM друг([мр, од, СУЩ])  )  )  )");
	}
	
	public void test_brackets001(){
		String str = "Пушкин (26 мая [6 июня] 1799, Москва — 29 января [10 февраля] 1837, Санкт-Петербург) является русским поэтом, драматургом и прозаиком.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		          + "<subject>WORD_FORM("
		            + "<main>WORD_FORM пушкин([sg, мр, од, СУЩ, фам])"
		            + "BRACKETS("
		              + "REGION_BOUND("
		                + "SYMBOL (  )"
		              + "CLAUSE("
		                + "<subject>NOUN_ADJECTIVE("
		                  + "SCALAR 26.0"
		                  + "<main>WORD_FORM мая([sg, жр, СУЩ, гео, неод])  )"
		                + "<predicate>no predicate  )"
		              + "SCALAR 26.0"
		              + "DATE 26.0 май([sg, мр, СУЩ, неод]) "
		              + "SYMBOL ["
		              + "DATE 6.0 июнь([мр, СУЩ, неод]) "
		              + "SYMBOL ]"
		              + "SCALAR 1799.0"
		              + "SYMBOL ,"
		              + "CLAUSE("
		                + "<subject>WORD_FORM москва([sg, жр, СУЩ, гео, неод])"
		                + "<predicate>no predicate  )"
		              + "WORD_FORM —([])"
		              + "DATE 29.0 январь([мр, СУЩ, неод]) "
		              + "SYMBOL ["
		              + "DATE 10.0 февраль([мр, СУЩ, неод]) "
		              + "SYMBOL ]"
		              + "SCALAR 1837.0"
		              + "SYMBOL ,"
		              + "WORD_FORM санкт([])"
		              + "SYMBOL -"
		              + "CLAUSE("
		                + "<subject>WORD_FORM петербург([мр, СУЩ, гео, неод])"
		                + "<predicate>no predicate  )"
		              + "REGION_BOUND("
		                + "SYMBOL )  )  )  )"
		          + "<predicate>VERB_NOUN("
		            + "<main>WORD_FORM являюсь([ГЛ, неперех, несов])"
		            + "UNIFORM_NOUN("
		              + "NOUN_ADJECTIVE("
		                + "WORD_FORM русский([субст?, ПРИЛ])"
		                + "<main>WORD_FORM поэт([мр, од, СУЩ])  )"
		              + "SYMBOL ,"
		              + "WORD_FORM драматург([мр, од, СУЩ])"
		              + "WORD_FORM и([СОЮЗ])"
		              + "<main>WORD_FORM прозаик([мр, од, СУЩ])  )  )  )");
	}
	
	public void test_direct_speech000(){
		String str = "\"Здравствуйте вам ?\" - сказал он.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>WORD_FORM он([мр, Анаф, МС, 3л])"
		        + "<predicate>WORD_FORM("
		          + "DIRECT_SPEACH("
		            + "REGION_BOUND("
		              + "SYMBOL \"  )"
		            + "CLAUSE("
		              + "<subject>no subject"
		              + "<predicate>WORD_FORM здравствую([ГЛ, неперех, несов])  )"
		            + "WORD_FORM вы([МС, 2л])"
		            + "SYMBOL ?"
		            + "REGION_BOUND("
		              + "SYMBOL \""
		              + "SYMBOL -  )  )"
		          + "<main>WORD_FORM сказал([сов, ГЛ, перех])  )  )");
	}
	
	public void test_direct_speech001(){
		String str = "\"Здравствуйте вам\", - сказал он.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>WORD_FORM он([мр, Анаф, МС, 3л])"
		        + "<predicate>WORD_FORM("
		          + "DIRECT_SPEACH("
		            + "REGION_BOUND("
		              + "SYMBOL \"  )"
		            + "CLAUSE("
		              + "<subject>no subject"
		              + "<predicate>WORD_FORM здравствую([ГЛ, неперех, несов])  )"
		            + "WORD_FORM вы([МС, 2л])"
		            + "REGION_BOUND("
		              + "SYMBOL \""
		              + "SYMBOL ,"
		              + "SYMBOL -  )  )"
		          + "<main>WORD_FORM сказал([сов, ГЛ, перех])  )  )");
	}
	
	public void test_direct_speech002(){
		String str = "Он сказал: \"Здравствуйте вам!\"";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>WORD_FORM он([мр, Анаф, МС, 3л])"
		        + "<predicate>WORD_FORM("
		          + "<main>WORD_FORM сказал([сов, ГЛ, перех])"
		          + "DIRECT_SPEACH("
		            + "REGION_BOUND("
		              + "SYMBOL :"
		              + "SYMBOL \"  )"
		            + "CLAUSE("
		              + "<subject>no subject"
		              + "<predicate>WORD_FORM здравствую([ГЛ, неперех, несов])  )"
		            + "WORD_FORM вы([МС, 2л])"
		            + "SYMBOL !"
		            + "REGION_BOUND("
		              + "SYMBOL \"  )  )  )  )");
	}
	
	public void test_direct_speech003(){
		String str = "Он сказал: \"Здравствуйте вам!\" - радостным голосом.";
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>WORD_FORM он([мр, Анаф, МС, 3л])"
		        + "<predicate>VERB_NOUN("
		          + "<main>WORD_FORM("
		            + "<main>WORD_FORM сказал([сов, ГЛ, перех])"
		            + "DIRECT_SPEACH("
		              + "REGION_BOUND("
		                + "SYMBOL :"
		                + "SYMBOL \"  )"
		              + "CLAUSE("
		                + "<subject>no subject"
		                + "<predicate>WORD_FORM здравствую([ГЛ, неперех, несов])  )"
		              + "WORD_FORM вы([МС, 2л])"
		              + "SYMBOL !"
		              + "REGION_BOUND("
		                + "SYMBOL \""
		                + "SYMBOL -  )  )  )"
		          + "NOUN_ADJECTIVE("
		            + "WORD_FORM радостный([кач, ПРИЛ])"
		            + "<main>WORD_FORM голос([мр, СУЩ, неод])  )  )  )");
	}
	
	public void test_direct_speech004(){
		String str = "Он сказал: \"Здравствуйте вам\".";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>WORD_FORM он([мр, Анаф, МС, 3л])"
		        + "<predicate>WORD_FORM("
		          + "<main>WORD_FORM сказал([сов, ГЛ, перех])"
		          + "DIRECT_SPEACH("
		            + "REGION_BOUND("
		              + "SYMBOL :"
		              + "SYMBOL \"  )"
		            + "CLAUSE("
		              + "<subject>no subject"
		              + "<predicate>WORD_FORM здравствую([ГЛ, неперех, несов])  )"
		            + "WORD_FORM вы([МС, 2л])"		            
		            + "REGION_BOUND("
		              + "SYMBOL \"  )  )  )  )");
	}
	
	public void test_direct_speech005(){
		String str = "Он сказал: \"Здравствуйте вам\", - радостным голосом.";
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>WORD_FORM он([мр, Анаф, МС, 3л])"
		        + "<predicate>VERB_NOUN("
		          + "<main>WORD_FORM("
		            + "<main>WORD_FORM сказал([сов, ГЛ, перех])"
		            + "DIRECT_SPEACH("
		              + "REGION_BOUND("
		                + "SYMBOL :"
		                + "SYMBOL \"  )"
		              + "CLAUSE("
		                + "<subject>no subject"
		                + "<predicate>WORD_FORM здравствую([ГЛ, неперех, несов])  )"
		              + "WORD_FORM вы([МС, 2л])"		              
		              + "REGION_BOUND("
		                + "SYMBOL \""
		                + "SYMBOL ,"
		                + "SYMBOL -  )  )  )"
		          + "NOUN_ADJECTIVE("
		            + "WORD_FORM радостный([кач, ПРИЛ])"
		            + "<main>WORD_FORM голос([мр, СУЩ, неод])  )  )  )");
	}
	
	public void test_title000(){
		String str = "Поставим систему \"Окна\".";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>no subject"
		        + "<predicate>DIRECT_OBJECT_NAME("
		          + "<main>WORD_FORM поставил([сов, ГЛ, перех])"
		          + "WORD_FORM("
		            + "<main>WORD_FORM система([жр, СУЩ, неод])"
		            + "TITLE("
		              + "REGION_BOUND("
		                + "SYMBOL \"  )"
		              + "CLAUSE("
		                + "<subject>WORD_FORM окно([СУЩ, ср, неод])"
		                + "<predicate>no predicate  )"
		              + "REGION_BOUND("
		                + "SYMBOL \"  )  )  )  )  )");
	}
	
	public void test_title001(){
		String str = "\"Окна\" мы поставим.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>WORD_FORM("
		          + "TITLE("
		            + "REGION_BOUND("
		              + "SYMBOL \"  )"
		            + "CLAUSE("
		              + "<subject>WORD_FORM окно([СУЩ, ср, неод])"
		              + "<predicate>no predicate  )"
		            + "REGION_BOUND("
		              + "SYMBOL \"  )  )"
		          + "<main>WORD_FORM мы([МС, 1л])  )"
		        + "<predicate>WORD_FORM поставил([сов, ГЛ, перех])  )");
	}
	
	public void test_title002(){
		String str = "Поставим систему \"Окна\" мы.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>WORD_FORM мы([МС, 1л])"
		        + "<predicate>DIRECT_OBJECT_NAME("
		          + "<main>WORD_FORM поставил([сов, ГЛ, перех])"
		          + "WORD_FORM("
		            + "<main>WORD_FORM система([жр, СУЩ, неод])"
		            + "TITLE("
		              + "REGION_BOUND("
		                + "SYMBOL \"  )"
		              + "CLAUSE("
		                + "<subject>WORD_FORM окно([СУЩ, ср, неод])"
		                + "<predicate>no predicate  )"
		              + "REGION_BOUND("
		                + "SYMBOL \"  )  )  )  )  )");
	}
		

	
	
	public void test_enumeration000(){
		String str = "Вася купил продукты: сыр, колбасу и мороженое,- в магазине через дорогу.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>WORD_FORM вася([имя, мр, од, СУЩ])"
		        + "<predicate>DIRECT_OBJECT_NAME("
		          + "<main>WORD_FORM купил([сов, ГЛ, перех])"
		          + "NOUN_NAME_PREP("
		            + "<main>WORD_FORM("
		              + "<main>WORD_FORM продукт([мр, СУЩ, неод])"
		              + "ENUMERATION("
		                + "REGION_BOUND("
		                  + "SYMBOL :  )"
		                + "UNIFORM_NOUN("
		                  + "WORD_FORM сыр([мр, СУЩ, неод])"
		                  + "SYMBOL ,"
		                  + "WORD_FORM колбаса([жр, СУЩ, неод])"
		                  + "WORD_FORM и([СОЮЗ])"
		                  + "<main>WORD_FORM мороженое([СУЩ, ср, неод])  )"
		                + "CLAUSE("
		                  + "<subject>WORD_FORM сыр([мр, СУЩ, неод])"
		                  + "<predicate>no predicate  )"
		                + "WORD_FORM сыр([кач, КР_ПРИЛ])"
		                + "SYMBOL ,"
		                + "UNIFORM_NOUN("
		                  + "WORD_FORM колбаса([жр, СУЩ, неод])"
		                  + "WORD_FORM и([СОЮЗ])"
		                  + "<main>WORD_FORM мороженое([СУЩ, ср, неод])  )"
		                + "WORD_FORM колбаса([жр, СУЩ, неод])"
		                + "CLAUSE("
		                  + "<subject>WORD_FORM и([СОЮЗ])"
		                  + "<predicate>no predicate  )"
		                + "UNIFORM_NOUN("
		                  + "WORD_FORM и([СОЮЗ])"
		                  + "<main>WORD_FORM мороженое([СУЩ, ср, неод])  )"
		                + "CLAUSE("
		                  + "<subject>WORD_FORM мороженое([СУЩ, ср, неод])"
		                  + "<predicate>no predicate  )"
		                + "WORD_FORM мороженый([ПРИЛ])"
		                + "REGION_BOUND("
		                  + "SYMBOL ,"
		                  + "SYMBOL -  )  )  )"
		            + "WORD_FORM в([ПР])"
		            + "NOUN_NAME_PREP("
		              + "<main>WORD_FORM магазин([мр, СУЩ, неод])"
		              + "WORD_FORM через([ПР])"
		              + "WORD_FORM дорога([жр, СУЩ, неод])  )  )  )  )",
		      "CLAUSE("
		        + "<subject>WORD_FORM вася([имя, мр, од, СУЩ])"
		        + "<predicate>VERB_NOUN_PREP("
		          + "<main>DIRECT_OBJECT_NAME("
		            + "<main>WORD_FORM купил([сов, ГЛ, перех])"
		            + "WORD_FORM("
		              + "<main>WORD_FORM продукт([мр, СУЩ, неод])"
		              + "ENUMERATION("
		                + "REGION_BOUND("
		                  + "SYMBOL :  )"
		                + "UNIFORM_NOUN("
		                  + "WORD_FORM сыр([мр, СУЩ, неод])"
		                  + "SYMBOL ,"
		                  + "WORD_FORM колбаса([жр, СУЩ, неод])"
		                  + "WORD_FORM и([СОЮЗ])"
		                  + "<main>WORD_FORM мороженое([СУЩ, ср, неод])  )"
		                + "WORD_FORM сыр([кач, КР_ПРИЛ])"
		                + "CLAUSE("
		                  + "<subject>WORD_FORM сыр([мр, СУЩ, неод])"
		                  + "<predicate>no predicate  )"
		                + "SYMBOL ,"
		                + "UNIFORM_NOUN("
		                  + "WORD_FORM колбаса([жр, СУЩ, неод])"
		                  + "WORD_FORM и([СОЮЗ])"
		                  + "<main>WORD_FORM мороженое([СУЩ, ср, неод])  )"
		                + "WORD_FORM колбаса([жр, СУЩ, неод])"
		                + "CLAUSE("
		                  + "<subject>WORD_FORM и([СОЮЗ])"
		                  + "<predicate>no predicate  )"
		                + "UNIFORM_NOUN("
		                  + "WORD_FORM и([СОЮЗ])"
		                  + "<main>WORD_FORM мороженое([СУЩ, ср, неод])  )"
		                + "WORD_FORM мороженый([ПРИЛ])"
		                + "CLAUSE("
		                  + "<subject>WORD_FORM мороженое([СУЩ, ср, неод])"
		                  + "<predicate>no predicate  )"
		                + "REGION_BOUND("
		                  + "SYMBOL ,"
		                  + "SYMBOL -  )  )  )  )"
		          + "WORD_FORM в([ПР])"
		          + "NOUN_NAME_PREP("
		            + "<main>WORD_FORM магазин([мр, СУЩ, неод])"
		            + "WORD_FORM через([ПР])"
		            + "WORD_FORM дорога([жр, СУЩ, неод])  )  )  )");
	}
}
