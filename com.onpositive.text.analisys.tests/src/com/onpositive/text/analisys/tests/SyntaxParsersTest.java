package com.onpositive.text.analisys.tests;

import java.util.List;

import com.onpositive.semantic.wordnet.composite.CompositeWordnet;
import com.onpositive.text.analysis.IToken;
import com.onpositive.text.analysis.lexic.dimension.Unit;
import com.onpositive.text.analysis.lexic.dimension.UnitKind;
import com.onpositive.text.analysis.syntax.SyntaxParser;

public class SyntaxParsersTest extends ParserTest{

	private Unit kilometerPerHourUnit = new Unit("километр в час",UnitKind.SPEED,1);
	
	public SyntaxParsersTest() {
		super();
		CompositeWordnet wn=new CompositeWordnet();
		wn.addUrl("/numerics.xml");
		wn.addUrl("/dimensions.xml");
		wn.addUrl("/modificator-adverb.xml");
		wn.prepare();
		SyntaxParser syntaxParser = new SyntaxParser(wn);
		this.composition = syntaxParser;
	}
	
	public void test001(){
		String str = "Потом он красной ручкой расписался в дневнике.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("NOUN_ADJECTIVE(WORD_FORM красный([кач, ПРИЛ]), <main>WORD_FORM ручка([жр, СУЩ, неод]))", processed);
	}
	
	public void test002(){
		String str = "Петя в красивой вязаной шапке идёт в большой магазин.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("NOUN_ADJECTIVE(UNIFORM_ADJECTIVE(WORD_FORM красивый([кач, ПРИЛ]),<main>WORD_FORM вязаный([ПРИЛ])),<main>WORD_FORM шапка([жр, СУЩ, неод]))", processed);
		assertTestTokenPrint("NOUN_ADJECTIVE(WORD_FORM большой([кач, ПРИЛ]), <main>WORD_FORM магазин([мр, СУЩ, неод]))", processed);
	}
	
	public void test003(){
		String str = "Петя в красивой красной шапке идёт в большой магазин.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("NOUN_ADJECTIVE(UNIFORM_ADJECTIVE(WORD_FORM красивый([кач, ПРИЛ]),<main>WORD_FORM красный([ПРИЛ])),<main>WORD_FORM шапка([жр, СУЩ, неод]))", processed);
		assertTestTokenPrint("NOUN_ADJECTIVE(WORD_FORM большой([кач, ПРИЛ]), <main>WORD_FORM магазин([мр, СУЩ, неод]))", processed);
	}
	
	public void test004(){
		String str = "Тот дом, своя улица.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("NOUN_ADJECTIVE(WORD_FORM тот([мест-п, Анаф, субст?, ПРИЛ]), <main>WORD_FORM дом([мр, СУЩ, неод]))", processed);
		assertTestTokenPrint("NOUN_ADJECTIVE(WORD_FORM свой([мест-п, Анаф, ПРИЛ]), <main>WORD_FORM улица([жр, СУЩ, неод]))", processed);
	}
	
	public void test005(){
		String str = "Я иду работать. Но я люблю смотреть кино. Я смотрю кино каждую неделю.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("DIRECT_SUBJECT_NAME(<main>DIRECT_SUBJECT_INF(<main>WORD_FORM люблю([ГЛ, перех, несов]), WORD_FORM смотреть([перех, несов, ИНФ])), WORD_FORM кино([СУЩ, ср, 0, неод]))", processed);
	}
	
	public void test006(){
		String str = "Но я люблю смотреть кино.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("DIRECT_SUBJECT_NAME(<main>DIRECT_SUBJECT_INF(<main>WORD_FORM люблю([ГЛ, перех, несов]), WORD_FORM смотреть([перех, несов, ИНФ])), WORD_FORM кино([СУЩ, ср, 0, неод]))", processed);
	}
	
	public void test007(){
		String str = "Я смотрю кино каждую неделю. Я смотрю фильмы каждую неделю.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("DIRECT_SUBJECT_NAME(<main>WORD_FORM смотрю([ГЛ, перех, несов]), WORD_FORM кино([СУЩ, ср, 0, неод]))", processed);
		assertTestTokenPrint("NOUN_ADJECTIVE(WORD_FORM каждый([мест-п, ПРИЛ]), <main>WORD_FORM неделя([жр, СУЩ, неод]))", processed);
		assertTestTokenPrint("DIRECT_SUBJECT_NAME(<main>WORD_FORM смотрю([ГЛ, перех, несов]), WORD_FORM фильм([мр, СУЩ, неод]))", processed);
		assertTestTokenPrint("NOUN_ADJECTIVE(WORD_FORM каждый([мест-п, ПРИЛ]), <main>WORD_FORM неделя([жр, СУЩ, неод]))", processed);
	}
	
	public void test009(){
		String str = "Я смотрю сериал каждую неделю.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("DIRECT_SUBJECT_NAME(<main>WORD_FORM смотрю([ГЛ, перех, несов]), WORD_FORM сериал([мр, СУЩ, неод]))", processed);
		assertTestTokenPrint("NOUN_ADJECTIVE(WORD_FORM каждый([мест-п, ПРИЛ]), <main>WORD_FORM неделя([жр, СУЩ, неод]))", processed);
	}
	
	public void test010(){
		String str = "Петя любит петь.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("DIRECT_SUBJECT_INF(<main>WORD_FORM люблю([ГЛ, перех, несов]), WORD_FORM петь([ГЛ]))", processed);
		assertTestTokenPrint("DIRECT_SUBJECT_NAME(<main>WORD_FORM люблю([ГЛ, перех, несов]), WORD_FORM петя([имя, мр, од, СУЩ]))", processed);
	}
	
	public void test011(){
		String str = "Я занял денег.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("DIRECT_SUBJECT_NAME(<main>WORD_FORM занял([сов, ГЛ, перех]), WORD_FORM деньга([жр, СУЩ, неод]))", processed);
	}
	
	public void test012(){
		String str = "Мы очень быстро едем.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("ADVERB_WITH_MODIFICATOR(WORD_FORM очень([]),<main>WORD_FORM быстро([Н])  )", processed);
	}
	
	public void test013(){
		String str = "Мы быстро и комфортно едем на автобусе.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("UNIFORM_ADVERB(WORD_FORM быстро([Н]),WORD_FORM и([СОЮЗ]),<main>WORD_FORM комфортно([Н]))", processed);
	}
	
	public void test014(){
		String str = "Мы очень быстро и комфортно едем на автобусе.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("UNIFORM_ADVERB(ADVERB_WITH_MODIFICATOR(WORD_FORM очень([]),<main>WORD_FORM быстро([Н])),WORD_FORM и([СОЮЗ]),<main>WORD_FORM комфортно([Н]))", processed);
	}
	
	public void test015(){
		String str = "В лесу стоит очень красивое дерево.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("NOUN_ADJECTIVE(ADJECTIVE_ADVERB(WORD_FORM очень([Н]),<main>WORD_FORM красивый([кач, ПРИЛ])),<main>WORD_FORM дерево([СУЩ, ср, неод]))", processed);
	}
	
	public void test016(){
		String str = "Мы встретили красивую Машу, умного Петю и весёлого Васю.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("DIRECT_SUBJECT_NAME("
								+"<main>WORD_FORM встретил([сов, ГЛ, перех])"
								+"UNIFORM_NOUN("
									+"NOUN_ADJECTIVE("
										+"WORD_FORM красивый([кач, ПРИЛ])"
										+"<main>WORD_FORM маша([имя, жр, од, СУЩ])  )"
									+"SYMBOL ,"
									+"NOUN_ADJECTIVE("
										+"WORD_FORM умный([кач, ПРИЛ])"
										+"<main>WORD_FORM петя([имя, мр, од, СУЩ])  )"
									+"WORD_FORM и([СОЮЗ])"
									+"<main>NOUN_ADJECTIVE("
										+"WORD_FORM весёлый([кач, ПРИЛ])"
										+"<main>WORD_FORM вася([имя, мр, од, СУЩ])  )  )  )", processed);
	}
}
