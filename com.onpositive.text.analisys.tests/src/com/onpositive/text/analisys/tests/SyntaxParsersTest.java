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
	
	public void test000(){
		String str = "Идёт в большой магазин.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"NOUN_ADJECTIVE("
				+ "WORD_FORM большой([кач, ПРИЛ]),"
				+ "<main>WORD_FORM магазин([мр, СУЩ, неод]))", processed);
	}
	
	public void test001(){
		String str = "Потом он красной ручкой расписался в дневнике.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("NOUN_ADJECTIVE(WORD_FORM красный([кач, ПРИЛ]), <main>WORD_FORM ручка([жр, СУЩ, неод]))", processed);
	}
	
	public void test002(){
		String str = "Петя в красивой вязаной шапке идёт в большой магазин.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(""
			+ "NOUN_ADJECTIVE("
				+ "UNIFORM_ADJECTIVE("
					+ "WORD_FORM красивый([кач, ПРИЛ]),"
					+ "<main>WORD_FORM вязаный([ПРИЛ])),"
				+ "<main>WORD_FORM шапка([жр, СУЩ, неод]))", processed);
		assertTestTokenPrint(
			"NOUN_ADJECTIVE("
				+ "WORD_FORM большой([кач, ПРИЛ]),"
				+ "<main>WORD_FORM магазин([мр, СУЩ, неод]))", processed);
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
	
	/**
	 *  INVALID TEST !!!
	 **/
	public void test005(){
		String str = "Но я люблю смотреть кино.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
				+ "<subject>WORD_FORM я([МС, 1л])"
				+ "<predicate>DIRECT_OBJECT_NAME("
					+ "<main>DIRECT_OBJECT_INF("
						+ "<main>WORD_FORM люблю([ГЛ, перех, несов])"
						+ "WORD_FORM смотреть([перех, несов, ИНФ])  )"
					+ "WORD_FORM кино([СУЩ, ср, 0, неод])  )  )", processed);
	}
	/**
	 *  INVALID TEST !!!
	 **/
	public void test006(){
		String str = "Я смотрю сериал каждую неделю.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
				+ "<subject>WORD_FORM я([МС, 1л])"
				+ "<predicate>DIRECT_OBJECT_NAME("
					+ "<main>WORD_FORM смотрю([ГЛ, перех, несов])"
					+ "UNIFORM_NOUN("
						+ "WORD_FORM сериал([мр, СУЩ, неод])"
						+ "<main>NOUN_ADJECTIVE("
							+ "WORD_FORM каждый([мест-п, ПРИЛ])"
							+ "<main>WORD_FORM неделя([жр, СУЩ, неод])  )  )  )  )", processed);
	}
	
	public void test007(){
		String str = "Петя любит петь.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
				+ "<subject>WORD_FORM петя([имя, мр, од, СУЩ])"
				+ "<predicate>DIRECT_OBJECT_INF("
					+ "<main>WORD_FORM люблю([ГЛ, перех, несов])"
					+ "WORD_FORM петь([перех, несов, ИНФ])  )  )", processed);
	}
	
	public void test008(){
		String str = "Мы очень быстро едем.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
				+ "<subject>WORD_FORM мы([МС, 1л])"
				+ "<predicate>VERB_ADVERB("
					+ "ADVERB_WITH_MODIFICATOR("
						+ "WORD_FORM очень([Н])"
						+ "<main>WORD_FORM быстро([Н])  )"
					+ "<main>WORD_FORM еду([ГЛ, неперех, несов])  )  )", processed);
	}
	
	public void test009(){
		String str = "Мы быстро и комфортно едем на автобусе.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
				+ "<subject>WORD_FORM мы([МС, 1л])"
				+ "<predicate>VERB_ADVERB("
					+ "UNIFORM_ADVERB("
							+ "WORD_FORM быстро([Н])"
							+ "WORD_FORM и([СОЮЗ])"
						+ "<main>WORD_FORM комфортно([Н])  )"
					+ "<main>WORD_FORM еду([ГЛ, неперех, несов])  )  )", processed);
	}
	
	public void test010(){
		String str = "Мы очень быстро и комфортно едем на автобусе.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
				+ "<subject>WORD_FORM мы([МС, 1л])"
				+ "<predicate>VERB_ADVERB("
					+ "UNIFORM_ADVERB("
						+ "ADVERB_WITH_MODIFICATOR("
							+ "WORD_FORM очень([])"
							+ "<main>WORD_FORM быстро([Н])  )"
						+ "WORD_FORM и([СОЮЗ])"
						+ "<main>WORD_FORM комфортно([Н])  )"
					+ "<main>WORD_FORM еду([ГЛ, неперех, несов])  )  )", processed);
	}
	
	public void test011(){
		String str = "В лесу стоит очень красивое дерево.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("		        
		        + "<subject>NOUN_ADJECTIVE("
			        + "ADJECTIVE_ADVERB("
				        + "WORD_FORM очень([Н])"
				        + "<main>WORD_FORM красивый([кач, ПРИЛ])  )"
			        + "<main>WORD_FORM дерево([СУЩ, ср, неод])  )"
			    + "<predicate>WORD_FORM стою([ГЛ, неперех, несов])  )", processed);
	}
	
	public void test012(){
		String str = "Мы встретили красивую Машу, умного Петю и весёлого Васю.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
				+ "<subject>WORD_FORM мы([МС, 1л])"
				+ "<predicate>DIRECT_OBJECT_NAME("
					+ "<main>WORD_FORM встретил([сов, ГЛ, перех])"
						+ "UNIFORM_NOUN("
							+ "NOUN_ADJECTIVE("
								+ "WORD_FORM красивый([кач, ПРИЛ])"
								+ "<main>WORD_FORM маша([имя, жр, од, СУЩ])  )"
							+ "SYMBOL ,"
							+ "NOUN_ADJECTIVE("
								+ "WORD_FORM умный([кач, ПРИЛ])"
								+ "<main>WORD_FORM петя([имя, мр, од, СУЩ])  )"
							+ "WORD_FORM и([СОЮЗ])"
							+ "<main>NOUN_ADJECTIVE("
								+ "WORD_FORM весёлый([кач, ПРИЛ])"
								+ "<main>WORD_FORM вася([имя, мр, од, СУЩ])  )  )  )  )", processed);
	}
	
	public void test013(){
		String str = "Вчера чинил суровый злой сантехник водопровод.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
				+ "<subject>NOUN_ADJECTIVE("
					+ "UNIFORM_ADJECTIVE("
						+ "WORD_FORM суровый([кач, ПРИЛ])"
						+ "<main>WORD_FORM злой([кач, ПРИЛ])  )"
					+ "<main>WORD_FORM сантехник([мр, од, СУЩ])  )"
				+ "<predicate>DIRECT_OBJECT_NAME("
					+ "<main>VERB_ADVERB("
						+ "WORD_FORM вчера([Н])"
						+ "<main>WORD_FORM чиню([ГЛ, перех, несов])  )"
					+ "WORD_FORM водопровод([мр, СУЩ, неод])  )  )", processed);
	}
	
	public void test014(){
		String str = "Вчера смотрели наши коллеги фильм.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
				+ "<subject>NOUN_ADJECTIVE("
					+ "WORD_FORM наш([мест-п, ПРИЛ])"
					+ "<main>WORD_FORM коллега([жр, од, СУЩ, ор])  )"
				+ "<predicate>DIRECT_OBJECT_NAME("
					+ "<main>VERB_ADVERB("
						+ "WORD_FORM вчера([Н])"
						+ "<main>WORD_FORM смотрю([ГЛ, перех, несов]) )"
					+ "WORD_FORM фильм([мр, СУЩ, неод])  )  )", processed);
	}
	
	
	public void test015(){
		String str = "Я занял денег.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint("CLAUSE("
			+ "<subject>WORD_FORM я([МС, 1л])"
			+ "<predicate>DIRECT_OBJECT_NAME("
				+ "<main>WORD_FORM занял([сов, ГЛ, перех])"
				+ "WORD_FORM деньга([жр, СУЩ, неод])))", processed);
	}
	
	
	public void test016(){
		String str = "Он быстро бежал за красивой девушкой.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
				+ "<subject>WORD_FORM он([мр, Анаф, МС, 3л])"
				+ "<predicate>VERB_ADVERB("
					+ "WORD_FORM быстро([Н])"
					+ "<main>WORD_FORM бегу([ГЛ, неперех, несов])  )  )", processed);
		assertTestTokenPrint(
			"NOUN_ADJECTIVE("
				+ "WORD_FORM красивый([кач, ПРИЛ])"
				+ "<main>WORD_FORM девушка([жр, од, СУЩ])  )", processed);
	}
	
	public void test017(){
		String str = "Длина составляет 200 километров.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
				+ "<subject>WORD_FORM длина([жр, СУЩ, неод])"
				+ "<predicate>DIRECT_OBJECT_NAME("
					+ "<main>WORD_FORM составляю([ГЛ, перех, несов])"
					+ "DIMENSION 200.0 километр(SIZE)  )  )", processed);
	}
	
	public void test018(){
		String str = "Длина составляет 200 км.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
				+ "<subject>WORD_FORM длина([жр, СУЩ, неод])"
				+ " <predicate>DIRECT_OBJECT_NAME("
					+ "<main>WORD_FORM составляю([ГЛ, перех, несов])"
					+ "DIMENSION 200.0 километр(SIZE)  )  )", processed);
	}
	
	public void test019(){
		String str = "Дайте 5 апельсинов.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"DIRECT_OBJECT_NAME("
				+ "<main>WORD_FORM дал([сов, ГЛ, перех])"
				+ "NOUN_ADJECTIVE("
					+ "SCALAR 5.0"
					+ "<main>WORD_FORM апельсин([мр, СУЩ, неод])  )  )", processed);
	}
	
	public void test020(){
		String str = "Взвесьте 5 кг апельсинов.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"DIRECT_OBJECT_NAME("
				+ "<main>WORD_FORM взвесил([сов, ГЛ, перех])"
				+ "MEASURED_NOUN("
					+ "DIMENSION 5.0 килограмм(WEIGHT)"
					+ "<main>WORD_FORM апельсин([мр, СУЩ, неод])  )  )", processed);
	}
	
	public void test021(){
		String str = "Взвесьте 5 кг зелёных яблок.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"DIRECT_OBJECT_NAME("
				+ "<main>WORD_FORM взвесил([сов, ГЛ, перех])"
				+ "MEASURED_NOUN("
					+ "DIMENSION 5.0 килограмм(WEIGHT)"
					+ "<main>NOUN_ADJECTIVE("
						+ "WORD_FORM зелёный([кач, ПРИЛ])"
						+ "<main>WORD_FORM яблоко([СУЩ, ср, неод])  )  )  )", processed);
	}
	
	
	public void test022(){
		String str = "Читаем книгу мы быстро.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
			+ "<subject>WORD_FORM мы([МС, 1л])"
			+ "<predicate>VERB_ADVERB("
				+ "<main>DIRECT_OBJECT_NAME("
					+ "<main>WORD_FORM читаю([ГЛ, перех, несов])"
					+ "WORD_FORM книга([жр, СУЩ, неод])  )"
				+ "WORD_FORM быстро([Н])  )  )", processed);
	}
	
	public void test023(){
		String str = "Читаем быстро мы книгу.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
	        + "<subject>WORD_FORM мы([МС, 1л])"
	        + "<predicate>DIRECT_OBJECT_NAME("
		        + "<main>VERB_ADVERB("
		        	+ "<main>WORD_FORM читаю([ГЛ, перех, несов])"
		        	+ "WORD_FORM быстро([Н])  )"
		        + "WORD_FORM книга([жр, СУЩ, неод])  )  )", processed);
	}
	
	public void test024(){
		String str = "Быстро читаем мы книгу.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
				+ "<subject>WORD_FORM мы([МС, 1л])"
	        + "<predicate>DIRECT_OBJECT_NAME("
		        + "<main>VERB_ADVERB("
			        + "WORD_FORM быстро([Н])"
			        + "<main>WORD_FORM читаю([ГЛ, перех, несов])  )"
		        + "WORD_FORM книга([жр, СУЩ, неод])  )  )", processed);
	}
	
	public void test025(){
		String str = "Быстро мы читаем книгу.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
	        + "<subject>WORD_FORM мы([МС, 1л])"
	        + "<predicate>VERB_ADVERB("
		        + "WORD_FORM быстро([Н])"
		        + "<main>DIRECT_OBJECT_NAME("
			        + "<main>WORD_FORM читаю([ГЛ, перех, несов])"
			        + "WORD_FORM книга([жр, СУЩ, неод])  )  )  )", processed);
	}
	
	public void test026(){
		String str = "Книгу мы читаем быстро.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint(
			"CLAUSE("
	        + "<subject>WORD_FORM мы([МС, 1л])"
	        + "<predicate>DIRECT_OBJECT_NAME("
		        + "WORD_FORM книга([жр, СУЩ, неод])"
		        + "<main>VERB_ADVERB("
			        + "<main>WORD_FORM читаю([ГЛ, перех, несов])"
			        + "WORD_FORM быстро([Н])  )  )  )", processed);
	}
}
