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
		wn.addUrl("/prepositions.xml");
		wn.addUrl("/conjunctions.xml");
		wn.addUrl("/modalLikeVerbs.xml");
		wn.prepare();
		SyntaxParser syntaxParser = new SyntaxParser(wn);
		this.composition = syntaxParser;
	}
	
	public void test000(){
		String str = "Идёт в большой магазин.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>no subject"
		        + "<predicate>VERB_NOUN_PREP("
				     + "<main>WORD_FORM иду([ГЛ, неперех, несов])"
				     + "WORD_FORM в([ПР])"
				     + "NOUN_ADJECTIVE("
					    + "WORD_FORM большой([кач, ПРИЛ])"
					    + "<main>WORD_FORM магазин([мр, СУЩ, неод])  )  )  )");
	}
	
	public void test001(){
		String str = "Потом он красной ручкой расписался в дневнике.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>WORD_FORM он([мр, Анаф, МС, 3л])"
		        + "<predicate>VERB_ADVERB("
			        + "WORD_FORM потом([Н])"
			        + "<main>VERB_NOUN("
				        + "NOUN_ADJECTIVE("
					        + "WORD_FORM красный([кач, ПРИЛ])"
					        + "<main>WORD_FORM ручка([жр, СУЩ, неод])  )"
				        + "<main>VERB_NOUN_PREP("
					        + "<main>WORD_FORM расписался([сов, ГЛ, неперех])"
					        + "WORD_FORM в([ПР])"
						    + "WORD_FORM дневник([мр, СУЩ, неод])  )  )  )  )");
	}
	
	public void test002(){
		String str = "Петя в красивой вязаной шапке идёт в большой магазин.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,""
		+ "CLAUSE("
		        + "<subject>NOUN_NAME_PREP("
		          + "<main>WORD_FORM петя([имя, мр, од, СУЩ])"
		          + "WORD_FORM в([ПР])"
		          + "NOUN_ADJECTIVE("
		            + "WORD_FORM красивый([кач, ПРИЛ])"
		            + "<main>NOUN_ADJECTIVE("
		              + "WORD_FORM вязаный([ПРИЛ])"
		              + "<main>WORD_FORM шапка([жр, СУЩ, неод])  )  )  )"
		        + "<predicate>VERB_NOUN_PREP("
		          + "<main>WORD_FORM иду([ГЛ, неперех, несов])"
		          + "WORD_FORM в([ПР])"
		          + "NOUN_ADJECTIVE("
		            + "WORD_FORM большой([кач, ПРИЛ])"
		            + "<main>WORD_FORM магазин([мр, СУЩ, неод])  )  )  )");
	}
	
	public void test003(){
		String str = "Петя в красивой красной шапке идёт в большой магазин.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>NOUN_NAME_PREP("
		          + "<main>WORD_FORM петя([имя, мр, од, СУЩ])"
		          + "WORD_FORM в([ПР])"
		          + "NOUN_ADJECTIVE("
		            + "WORD_FORM красивый([кач, ПРИЛ])"
		            + "<main>NOUN_ADJECTIVE("
		              + "WORD_FORM красный([кач, ПРИЛ])"
		              + "<main>WORD_FORM шапка([жр, СУЩ, неод])  )  )  )"
		        + "<predicate>VERB_NOUN_PREP("
		          + "<main>WORD_FORM иду([ГЛ, неперех, несов])"
		          + "WORD_FORM в([ПР])"
		          + "NOUN_ADJECTIVE("
		            + "WORD_FORM большой([кач, ПРИЛ])"
		            + "<main>WORD_FORM магазин([мр, СУЩ, неод])  )  )  )");
	}
	
	public void test004(){
		String str = "Тот дом, своя улица.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>UNIFORM_NOUN("
		          + "NOUN_ADJECTIVE("
		            + "WORD_FORM тот([мест-п, Анаф, субст?, ПРИЛ])"
		            + "<main>WORD_FORM дом([мр, СУЩ, неод])  )"
		          + "SYMBOL ,"
		          + "<main>NOUN_ADJECTIVE("
		            + "WORD_FORM свой([мест-п, Анаф, ПРИЛ])"
		            + "<main>WORD_FORM улица([жр, СУЩ, неод])  )  )"
		        + "<predicate>no predicate  )");
	}
	
	/**
	 *  INVALID TEST !!!
	 **/
	public void test005(){
		String str = "Но я люблю смотреть кино.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>WORD_FORM я([МС, 1л])"
				+ "<predicate>DIRECT_OBJECT_NAME("
					+ "<main>DIRECT_OBJECT_INF("
						+ "<main>WORD_FORM люблю([ГЛ, перех, несов])"
						+ "WORD_FORM смотреть([перех, несов, ИНФ])  )"
					+ "WORD_FORM кино([СУЩ, ср, 0, неод])  )  )");
	}
	/**
	 *  INVALID TEST !!!
	 **/
	public void test006(){
		String str = "Я смотрю сериал каждую неделю.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>WORD_FORM я([МС, 1л])"
		        + "<predicate>DIRECT_OBJECT_NAME("
		          + "<main>DIRECT_OBJECT_NAME("
		            + "<main>WORD_FORM смотрю([ГЛ, перех, несов])"
		            + "WORD_FORM сериал([мр, СУЩ, неод])  )"
		          + "NOUN_ADJECTIVE("
		            + "WORD_FORM каждый([мест-п, ПРИЛ])"
		            + "<main>WORD_FORM неделя([жр, СУЩ, неод])  )  )  )");
	}
	
	public void test007(){
		String str = "Петя любит петь.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>WORD_FORM петя([имя, мр, од, СУЩ])"
				+ "<predicate>DIRECT_OBJECT_INF("
					+ "<main>WORD_FORM люблю([ГЛ, перех, несов])"
					+ "WORD_FORM петь([перех, несов, ИНФ])  )  )");
	}
	
	public void test008(){
		String str = "Мы очень быстро едем.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>WORD_FORM мы([МС, 1л])"
				+ "<predicate>VERB_ADVERB("
					+ "ADVERB_WITH_MODIFICATOR("
						+ "WORD_FORM очень([Н])"
						+ "<main>WORD_FORM быстро([Н])  )"
					+ "<main>WORD_FORM еду([ГЛ, неперех, несов])  )  )");
	}
	
	public void test009(){
		String str = "Мы быстро и комфортно едем на автобусе.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
			     + "<subject>WORD_FORM мы([МС, 1л])"
			     + "<predicate>VERB_NOUN_PREP("
			       + "<main>VERB_ADVERB("
			         + "UNIFORM_ADVERB("
			           + "WORD_FORM быстро([Н])"
			           + "WORD_FORM и([СОЮЗ])"
			           + "<main>WORD_FORM комфортно([Н])  )"
			         + "<main>WORD_FORM еду([ГЛ, неперех, несов])  )"
			       + "WORD_FORM на([ПР])"
				   + "WORD_FORM автобус([мр, СУЩ, неод])  )  )");
	}
	
	public void test010(){
		String str = "Мы очень быстро и комфортно едем на автобусе.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
			     + "<subject>WORD_FORM мы([МС, 1л])"
			     + "<predicate>VERB_NOUN_PREP("
			       + "<main>VERB_ADVERB("
			         + "UNIFORM_ADVERB("
			           + "ADVERB_WITH_MODIFICATOR("
			             + "WORD_FORM очень([Н])"
			             + "<main>WORD_FORM быстро([Н])  )"
			           + "WORD_FORM и([СОЮЗ])"
			           + "<main>WORD_FORM комфортно([Н])  )"
			         + "<main>WORD_FORM еду([ГЛ, неперех, несов])  )"
			       + "WORD_FORM на([ПР])"
				   + "WORD_FORM автобус([мр, СУЩ, неод])  )  )");
	}
	
	public void test011(){
		String str = "В лесу стоит очень красивое дерево.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			" CLAUSE("
			     + "<subject>NOUN_ADJECTIVE("
			       + "ADJECTIVE_ADVERB("
			         + "WORD_FORM очень([Н])"
			         + "<main>WORD_FORM красивый([кач, ПРИЛ])  )"
			       + "<main>WORD_FORM дерево([СУЩ, ср, неод])  )"
			     + "<predicate>VERB_NOUN_PREP("
			       + "WORD_FORM в([ПР])"
				   + "WORD_FORM лес([мр, СУЩ, неод])"
			       + "<main>WORD_FORM стою([ГЛ, неперех, несов])  )  )");
	}
	
	public void test012(){
		String str = "Мы встретили красивую Машу, умного Петю и весёлого Васю.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
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
								+ "<main>WORD_FORM вася([имя, мр, од, СУЩ])  )  )  )  )");
	}
	
	public void test013(){
		String str = "Вчера чинил суровый злой сантехник водопровод.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>NOUN_ADJECTIVE("
		          + "WORD_FORM суровый([кач, ПРИЛ])"
		          + "<main>NOUN_ADJECTIVE("
		            + "WORD_FORM злой([кач, ПРИЛ])"
		            + "<main>WORD_FORM сантехник([мр, од, СУЩ])  )  )"
		        + "<predicate>DIRECT_OBJECT_NAME("
		          + "<main>VERB_ADVERB("
		            + "WORD_FORM вчера([Н])"
		            + "<main>WORD_FORM чиню([ГЛ, перех, несов])  )"
		          + "WORD_FORM водопровод([мр, СУЩ, неод])  )  )");
	}
	
	public void test014(){
		String str = "Вчера смотрели наши коллеги фильм.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>NOUN_ADJECTIVE("
					+ "WORD_FORM наш([мест-п, ПРИЛ])"
					+ "<main>WORD_FORM коллега([жр, од, СУЩ, ор])  )"
				+ "<predicate>DIRECT_OBJECT_NAME("
					+ "<main>VERB_ADVERB("
						+ "WORD_FORM вчера([Н])"
						+ "<main>WORD_FORM смотрю([ГЛ, перех, несов]) )"
					+ "WORD_FORM фильм([мр, СУЩ, неод])  )  )");
	}
	
	
	public void test015(){
		String str = "Я занял денег.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,"CLAUSE("
			+ "<subject>WORD_FORM я([МС, 1л])"
			+ "<predicate>DIRECT_OBJECT_NAME("
				+ "<main>WORD_FORM занял([сов, ГЛ, перех])"
				+ "WORD_FORM деньга([жр, СУЩ, неод])))");
	}
	
	
	public void test016(){
		String str = "Он быстро бежал за красивой девушкой.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		     + "<subject>WORD_FORM он([мр, Анаф, МС, 3л])"
		     + "<predicate>VERB_NOUN_PREP("
		       + "<main>VERB_ADVERB("
		         + "WORD_FORM быстро([Н])"
		         + "<main>WORD_FORM бегу([ГЛ, неперех, несов])  )"
		       + "WORD_FORM за([ПР])"
			   + "NOUN_ADJECTIVE("
			     + "WORD_FORM красивый([кач, ПРИЛ])"
			     + "<main>WORD_FORM девушка([жр, од, СУЩ])  )  )  )");
	}
	
	public void test017(){
		String str = "Длина составляет 200 километров.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>WORD_FORM длина([жр, СУЩ, неод])"
				+ "<predicate>DIRECT_OBJECT_NAME("
					+ "<main>WORD_FORM составляю([ГЛ, перех, несов])"
					+ "DIMENSION 200.0 километр(SIZE)  )  )");
	}
	
	public void test018(){
		String str = "Длина составляет 200 км.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>WORD_FORM длина([жр, СУЩ, неод])"
				+ " <predicate>DIRECT_OBJECT_NAME("
					+ "<main>WORD_FORM составляю([ГЛ, перех, несов])"
					+ "DIMENSION 200.0 километр(SIZE)  )  )");
	}
	
	public void test019(){
		String str = "Дайте 5 апельсинов.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>no subject"
				+ "<predicate>DIRECT_OBJECT_NAME("
					+ "<main>WORD_FORM дал([сов, ГЛ, перех])"
					+ "NOUN_ADJECTIVE("
						+ "SCALAR 5.0"
						+ "<main>WORD_FORM апельсин([мр, СУЩ, неод])  )  )  )");
	}
	
	public void test020(){
		String str = "Взвесьте 5 кг апельсинов.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>no subject"
				+ "<predicate>DIRECT_OBJECT_NAME("
					+ "<main>WORD_FORM взвесил([сов, ГЛ, перех])"
					+ "MEASURED_NOUN("
						+ "DIMENSION 5.0 килограмм(WEIGHT)"
						+ "<main>WORD_FORM апельсин([мр, СУЩ, неод])  )  )  )");
	}
	
	public void test021(){
		String str = "Взвесьте 5 кг зелёных яблок.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>no subject"
		        + "<predicate>DIRECT_OBJECT_NAME("
			        + "<main>WORD_FORM взвесил([сов, ГЛ, перех])"
			        + "MEASURED_NOUN("
				        + "DIMENSION 5.0 килограмм(WEIGHT)"
				        + "<main>NOUN_ADJECTIVE("
					        + "WORD_FORM зелёный([кач, ПРИЛ])"
					        + "<main>WORD_FORM яблоко([СУЩ, ср, неод])  )  )  )  )");
	}
	
	
	public void test022(){
		String str = "Читаем книгу мы быстро.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
			+ "<subject>WORD_FORM мы([МС, 1л])"
			+ "<predicate>VERB_ADVERB("
				+ "<main>DIRECT_OBJECT_NAME("
					+ "<main>WORD_FORM читаю([ГЛ, перех, несов])"
					+ "WORD_FORM книга([жр, СУЩ, неод])  )"
				+ "WORD_FORM быстро([Н])  )  )");
	}
	
	public void test023(){
		String str = "Читаем быстро мы книгу.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
	        + "<subject>WORD_FORM мы([МС, 1л])"
	        + "<predicate>DIRECT_OBJECT_NAME("
		        + "<main>VERB_ADVERB("
		        	+ "<main>WORD_FORM читаю([ГЛ, перех, несов])"
		        	+ "WORD_FORM быстро([Н])  )"
		        + "WORD_FORM книга([жр, СУЩ, неод])  )  )");
	}
	
	public void test024(){
		String str = "Быстро читаем мы книгу.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>WORD_FORM мы([МС, 1л])"
	        + "<predicate>DIRECT_OBJECT_NAME("
		        + "<main>VERB_ADVERB("
			        + "WORD_FORM быстро([Н])"
			        + "<main>WORD_FORM читаю([ГЛ, перех, несов])  )"
		        + "WORD_FORM книга([жр, СУЩ, неод])  )  )");
	}
	
	public void test025(){
		String str = "Быстро мы читаем книгу.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
	        + "<subject>WORD_FORM мы([МС, 1л])"
	        + "<predicate>VERB_ADVERB("
		        + "WORD_FORM быстро([Н])"
		        + "<main>DIRECT_OBJECT_NAME("
			        + "<main>WORD_FORM читаю([ГЛ, перех, несов])"
			        + "WORD_FORM книга([жр, СУЩ, неод])  )  )  )");
	}
	
	public void test026(){
		String str = "Книгу мы читаем быстро.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
	        + "<subject>WORD_FORM мы([МС, 1л])"
	        + "<predicate>DIRECT_OBJECT_NAME("
		        + "WORD_FORM книга([жр, СУЩ, неод])"
		        + "<main>VERB_ADVERB("
			        + "<main>WORD_FORM читаю([ГЛ, перех, несов])"
			        + "WORD_FORM быстро([Н])  )  )  )");
	}
	
	public void test027(){
		String str = "Я занял глебу денег.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>WORD_FORM я([МС, 1л])"
		        + "<predicate>DIRECT_OBJECT_NAME("
			        + "<main>VERB_NOUN("
				        + "<main>WORD_FORM занял([сов, ГЛ, перех])"
				        + "WORD_FORM глеб([имя, мр, од, СУЩ])  )"
			        + "WORD_FORM деньга([жр, СУЩ, неод])  )  )");
	}
	//TODO The test is invalid
	public void test028(){
		String str = "Ученые узнали о влиянии пальцев владельцев смартфонов на мозг.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>WORD_FORM учёный([мр, од, СУЩ])"
		        + "<predicate>VERB_NOUN_PREP("
			        + "<main>WORD_FORM узнал([сов, ГЛ, перех])"
			        + "WORD_FORM о([ПР])"
			        + "NOUN_NAME_PREP("
				        + "<main>GENITIVE_CHAIN("
					        + "<main>WORD_FORM влияние([СУЩ, ср, неод])"
					        + "WORD_FORM палец([мр, СУЩ, неод])"
					        + "WORD_FORM владелец([мр, од, СУЩ])"
					        + "WORD_FORM смартфон([мр, СУЩ, неод])  )"
				        + "WORD_FORM на([ПР])"
				        + "WORD_FORM мозг([мр, СУЩ, неод])  )  )  )");
	}
	
	public void test030(){
		String str = "Российские следователи ответили на упреки украинских силовиков.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>NOUN_ADJECTIVE("
			        + "WORD_FORM российский([ПРИЛ])"
			        + "<main>WORD_FORM следователь([мр, од, СУЩ])  )"
		        + "<predicate>VERB_NOUN_PREP("
			        + "<main>WORD_FORM ответил([сов, ГЛ, неперех])"
			        + "WORD_FORM на([ПР])"
				    + "GENITIVE_CHAIN("
					    + "<main>WORD_FORM упрёк([мр, СУЩ, неод])"
					    + "NOUN_ADJECTIVE("
					       + "WORD_FORM украинский([ПРИЛ, гео])"
					       + "<main>WORD_FORM силовик([мр, од, СУЩ])  )  )  )  )");
	}
	
	public void test031(){
		String str = "В Якутии составят электронную родословную всего населения.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>no subject"
				+ "<predicate>VERB_NOUN_PREP("
					+ "WORD_FORM в([ПР])"
					+ "WORD_FORM якутия([sg, жр, СУЩ, гео, неод])"
					+ "<main>DIRECT_OBJECT_NAME("
			            + "<main>WORD_FORM составил([сов, ГЛ, перех])"
			            + "GENITIVE_CHAIN("
				            + "<main>NOUN_ADJECTIVE("
					            + "WORD_FORM электронный([ПРИЛ])"
					            + "<main>WORD_FORM родословная([жр, СУЩ, неод])  )"
				            + "NOUN_ADJECTIVE("
					            + "WORD_FORM весь([мест-п, ПРИЛ])"
					            + "<main>WORD_FORM население([СУЩ, ср, неод])  )  )  )  )  )");
	}
	
	public void test032(){
		String str = "Президент Гамбии опроверг попытку государственного переворота.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>GENITIVE_CHAIN("
			        + "<main>WORD_FORM президент([мр, од, СУЩ])"
			        + "WORD_FORM гамбия([sg, жр, СУЩ, гео, неод])  )"
		        + "<predicate>DIRECT_OBJECT_NAME("
			        + "<main>WORD_FORM опроверг([сов, ГЛ, перех])"
			        + "GENITIVE_CHAIN("
				        + "<main>WORD_FORM попытка([жр, СУЩ, неод])"
				        + "NOUN_ADJECTIVE("
					        + "WORD_FORM государственный([ПРИЛ])"
					        + "<main>WORD_FORM переворот([мр, СУЩ, неод])  )  )  )  )");
	}
	
	public void test033(){
		String str = "Инфляция в России достигла 10 процентов.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
			   + "<subject>WORD_FORM инфляция([жр, СУЩ, неод])"
			   + "<predicate>VERB_NOUN_PREP("
			   		+ "WORD_FORM в([ПР])"
			   		+ "WORD_FORM россия([sg, жр, СУЩ, гео, неод])"
					+ "<main>DIRECT_OBJECT_NAME("
						+ "<main>WORD_FORM достиг([сов, ГЛ, перех])"
						+ "NOUN_ADJECTIVE("
							+ "SCALAR 10.0"
							+ "<main>WORD_FORM процент([мр, СУЩ, неод])  )  )  )  )");
	}
	
	public void test034(){
		String str = "Я люблю, напевая, гулять.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>WORD_FORM я([МС, 1л])"
		        + "<predicate>DIRECT_OBJECT_INF("
			        + "<main>WORD_FORM люблю([ГЛ, перех, несов])"
			        + "VERB_GERUND("
			            + "SYMBOL ,"
			            + "WORD_FORM напевая([ДЕЕПР, перех, несов])"
			            + "SYMBOL ,"
			            + "<main>WORD_FORM гулять([неперех, несов, ИНФ])  )  )  )");
	}
	
	public void test035(){
		String str = "Я иду с Машей.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>WORD_FORM я([МС, 1л])"
		        + "<predicate>VERB_NOUN_PREP("
			        + "<main>WORD_FORM иду([ГЛ, неперех, несов])"
			        + "WORD_FORM с([ПР])"
			        + "WORD_FORM маша([имя, жр, од, СУЩ])  )  )");
	}
	
	public void test036(){
		String str = "Полёт длился 254 с и завершился удачно.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>WORD_FORM я([МС, 1л])"
		        + "<predicate>VERB_NOUN_PREP("
			        + "<main>WORD_FORM иду([ГЛ, неперех, несов])"
			        + "WORD_FORM с([ПР])"
			        + "WORD_FORM маша([имя, жр, од, СУЩ])  )  )");
	}
	
	public void test037(){
		String str = "Время полёта составило 254 с.";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>GENITIVE_CHAIN("
					+ "<main>WORD_FORM время([СУЩ, ср, неод])"
					+ "WORD_FORM полёт([мр, СУЩ, неод]) )"
		        + "<predicate>DIRECT_OBJECT_NAME("
			        + "<main>WORD_FORM составил([сов, ГЛ, перех])"
		            + "NOUN_ADJECTIVE("
		                + "SCALAR 254.0"
		                + "<main>WORD_FORM с([СУЩ, ср, 0, аббр, неод])  )  )  )");
	}
	
	public void test038(){
		String str = "В 1960 году сформировалась новая граница Москвы";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>GENITIVE_CHAIN("
					+ "<main>NOUN_ADJECTIVE("
						+ "WORD_FORM новый([кач, ПРИЛ])"
						+ "<main>WORD_FORM граница([жр, СУЩ, неод])  )"
					+ "WORD_FORM москва([sg, жр, СУЩ, гео, неод])  )"
		        + "<predicate>VERB_NOUN_PREP("
			        + "WORD_FORM в([ПР])"
				    + "DATE 1960.0 год([мр, СУЩ, неод])" 
			        + "<main>WORD_FORM сформировался([сов, ГЛ, неперех])  )  )");
	}
	
	
	public void test039(){
		String str = "С победой большевиков в 1920 году в войне началась новая, советская эпоха в развитии города";		
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>NOUN_NAME_PREP("
			        + "<main>NOUN_ADJECTIVE("
				        + "UNIFORM_ADJECTIVE("
					        + "WORD_FORM новый([кач, ПРИЛ])"
					        + "SYMBOL ,"
					        + "<main>WORD_FORM советский([ПРИЛ])  )"
				        + "<main>WORD_FORM эпоха([жр, СУЩ, неод])  )"
			        + "WORD_FORM в([ПР])"
			        + "GENITIVE_CHAIN("
				        + "<main>WORD_FORM развитие([СУЩ, ср, неод])"
				        + "WORD_FORM город([мр, СУЩ, неод])  )  )"
		        + "<predicate>VERB_NOUN_PREP("
			        + "WORD_FORM с([ПР])"
			        + "NOUN_NAME_PREP("
				        + "<main>GENITIVE_CHAIN("
					        + "<main>WORD_FORM победа([жр, СУЩ, неод])"
					        + "WORD_FORM большевик([мр, од, СУЩ])  )"
					    + "WORD_FORM в([ПР])"
					    + "DATE 1920.0 год([мр, СУЩ, неод])   )"
					+ "<main>VERB_NOUN_PREP("
				        + "WORD_FORM в([ПР])"
				        + "WORD_FORM война([жр, СУЩ, неод])"
				        + "<main>WORD_FORM начался([сов, ГЛ, неперех])  )  )  )",
			"CLAUSE("
					+ "<subject>NOUN_NAME_PREP("
						+ "<main>NOUN_ADJECTIVE("
							+ "UNIFORM_ADJECTIVE("
								+ "WORD_FORM новый([кач, ПРИЛ])"
								+ "SYMBOL ,"
								+ "<main>WORD_FORM советский([ПРИЛ])  )"
							+ "<main>WORD_FORM эпоха([жр, СУЩ, неод])  )"
						+ "WORD_FORM в([ПР])"
						+ "GENITIVE_CHAIN("
							+ "<main>WORD_FORM развитие([СУЩ, ср, неод])"
							+ "WORD_FORM город([мр, СУЩ, неод])  )  )"
					+ "<predicate>VERB_NOUN_PREP("
						+ "WORD_FORM с([ПР])"
						+ "NOUN_NAME_PREP("
							+ "<main>NOUN_NAME_PREP("
								+ "<main>GENITIVE_CHAIN("
									+ "<main>WORD_FORM победа([жр, СУЩ, неод])"
									+ "WORD_FORM большевик([мр, од, СУЩ])  )"
								+ "WORD_FORM в([ПР])"
								+ "DATE 1920.0 год([мр, СУЩ, неод])   )"
							+ "WORD_FORM в([ПР])"
							+ "WORD_FORM война([жр, СУЩ, неод])  )"
						+ "<main>WORD_FORM начался([сов, ГЛ, неперех])  )  )");
	}
	
	public void test040(){
		String str = "Мы наблюдаем рыжего кота, играющего в углу.";
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>WORD_FORM мы([МС, 1л])"
				+ "<predicate>DIRECT_OBJECT_NAME("
					+ "<main>WORD_FORM наблюдаю([ГЛ, перех, несов])"					
					+ "NOUN_PARTICIPLE("
						+ "<main>NOUN_ADJECTIVE("
							+ "WORD_FORM рыжий([кач, ПРИЛ])"
							+ "<main>WORD_FORM кот([мр, од, СУЩ])  )"
						+ "VERB_NOUN_PREP("
							+ "<main>WORD_FORM играющий([действ, ПРИЧ, перех, наст, несов])"
							+ "WORD_FORM в([ПР])"
							+ "WORD_FORM угол([мр, СУЩ, неод])  )  )  )  )");
	}
	
	public void test041(){
		String str = "Мы наблюдаем рыжего играющего в углу кота.";
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
				+ "<subject>WORD_FORM мы([МС, 1л])"
				+ "<predicate>DIRECT_OBJECT_NAME("
					+ "<main>WORD_FORM наблюдаю([ГЛ, перех, несов])"					
					+ "NOUN_PARTICIPLE("
						+ "<main>NOUN_ADJECTIVE("
							+ "WORD_FORM рыжий([кач, ПРИЛ])"
							+ "<main>WORD_FORM кот([мр, од, СУЩ])  )"
						+ "VERB_NOUN_PREP("
							+ "<main>WORD_FORM играющий([действ, ПРИЧ, перех, наст, несов])"
							+ "WORD_FORM в([ПР])"
							+ "WORD_FORM угол([мр, СУЩ, неод])  )  )  )  )");
	}
	
	public void test042(){
		String str = "Москва - столица России.";
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		        + "<subject>WORD_FORM москва([sg, жр, СУЩ, гео, неод])"
		        + "<predicate>VERB_NOUN("
		          + "<main>WORD_FORM есть([ГЛ, неперех, несов])"
		          + "GENITIVE_CHAIN("
		            + "<main>WORD_FORM столица([жр, СУЩ, неод])"
		            + "WORD_FORM россия([sg, жр, СУЩ, гео, неод])  )  )  )");
	}
	
	public void test043(){
		String str = "Александр Сергеевич Пушкин (26 мая [6 июня] 1799, Москва — 29 января [10 февраля] 1837, Санкт-Петербург) — русский поэт, драматург и прозаик.";
		List<IToken> processed = process(str);
		assertTestTokenPrint( processed,
			"CLAUSE("
		          + "<subject>GENITIVE_CHAIN("
		            + "NAME александра([имя, жр, од, СУЩ]) сергеевич([мр, од, СУЩ, отч]) "
		            + "<main>WORD_FORM("
		              + "<main>WORD_FORM пушкин([sg, мр, од, СУЩ, фам])"
		              + "BRACKETS("
		                + "REGION_BOUND("
		                  + "SYMBOL (  )"
		                + "CLAUSE("
		                  + "<subject>NOUN_ADJECTIVE("
		                    + "SCALAR 26.0"
		                    + "<main>WORD_FORM мая([sg, жр, СУЩ, гео, неод])  )"
		                  + "<predicate>no predicate  )"
		                + "DATE 26.0 май([sg, мр, СУЩ, неод]) "
		                + "SCALAR 26.0"
		                + "SYMBOL ["
		                + "DATE 6.0 июнь([мр, СУЩ, неод]) "
		                + "SYMBOL ]"
		                + "SCALAR 1799.0"
		                + "SYMBOL ,"
		                + "CLAUSE("
		                  + "<subject>WORD_FORM москва([sg, жр, СУЩ, гео, неод])"
		                  + "<predicate>no predicate  )"
		                + "SYMBOL —"
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
		                  + "SYMBOL )  )  )  )  )"
		          + "<predicate>VERB_NOUN("
		            + "<main>WORD_FORM есть([ГЛ, неперех, несов])"
		            + "UNIFORM_NOUN("
		              + "NOUN_ADJECTIVE("
		                + "WORD_FORM русский([субст?, ПРИЛ])"
		                + "<main>WORD_FORM поэт([мр, од, СУЩ])  )"
		              + "SYMBOL ,"
		              + "WORD_FORM драматург([мр, од, СУЩ])"
		              + "WORD_FORM и([СОЮЗ])"
		              + "<main>WORD_FORM прозаик([мр, од, СУЩ])  )  )  )");
	}
	

}
