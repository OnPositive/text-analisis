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
		wn.prepare();
		SyntaxParser syntaxParser = new SyntaxParser(wn);
		this.composition = syntaxParser;
	}
	
	public void test001(){
		String str = "Потом он красной ручкой расписался в дневнике.";		
		List<IToken> processed = process(str);
	}
	
	public void test002(){
		String str = "Петя в красивой вязаной шапке идёт в большой магазин.";		
		List<IToken> processed = process(str);
	}
	
	public void test003(){
		String str = "Петя в красивой красной шапке идёт в большой магазин.";		
		List<IToken> processed = process(str);
	}
	
	public void test004(){
		String str = "Тот дом, своя улица.";		
		List<IToken> processed = process(str);
	}
	
	public void test005(){
		String str = "Я иду работать. Но я люблю смотреть кино. Я смотрю кино каждую неделю.";		
		List<IToken> processed = process(str);
	}
	
	public void test006(){
		String str = "Но я люблю смотреть кино.";		
		List<IToken> processed = process(str);
	}
	
	public void test007(){
		String str = "Я смотрю кино каждую неделю. Я смотрю фильмы каждую неделю.";		
		List<IToken> processed = process(str);
	}
	
	public void test009(){
		String str = "Я смотрю сериал каждую неделю.";		
		List<IToken> processed = process(str);
	}
	
	public void test010(){
		String str = "Петя любит петь.";		
		List<IToken> processed = process(str);
	}
	
	public void test011(){
		String str = "Я занял денег.";		
		List<IToken> processed = process(str);
	}
}
