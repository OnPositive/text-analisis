//package com.onpositive.text.analysis.lexic;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.Stack;
//
//import com.onpositive.text.analysis.IToken;
//
//public class DateParser extends AbstractParser {
//	
//
//	public DateParser() {
//		
//		acceptedWords.addAll(Month.getAllForms());		
//		acceptedWords.addAll(YearPart.getAllForms());
//		acceptedWords.addAll(MonthPart.getAllForms());
//	}
//
//	private HashSet<String> acceptedWords = new HashSet<String>();
//	
//	public enum Month {
//		JANUARY("€нварь", "€нвар€", "€нваре", "€нварю", "€нварем"),
//		FEBRUARY("февраль", "феврал€", "феврале", "февралю", "февралем"),
//		MARCH("март", "марта", "марте", "марту", "мартом"),
//		APRIL("апрель", "апрел€", "апреле", "апрелю", "апрелем"),
//		MAY("май", "ма€", "мае", "маю", "маем"),
//		JUNE("июнь", "июн€", "июне", "июню", "июнем"),
//		JULY("июль", "июл€", "июле", "июлю", "июлем"),
//		AUGUST("август", "августа", "августе", "августу", "августом"),
//		SEPTEMBER("сент€брь", "сент€бр€", "сент€бре", "сент€брю", "сент€брем"),
//		OCTOBER("окт€брь", "окт€бр€", "окт€бре", "окт€брю", "окт€брем"),
//		NOVEMBER("но€брь", "но€бр€", "но€бре", "но€брю", "но€брем"),
//		DECEMBER("декабрь", "декабр€", "декабре", "декабрю", "декабрем");
//		
//		private List<String> forms;
//		
//		private Month(String... forms) {
//			this.forms = Arrays.asList(forms);
//		}
//		
//		public List<String> getForms() {
//			return Collections.unmodifiableList(forms);
//		}
//		
//		public String getName() {
//			return forms.get(0);
//		}
//		
//		public int getMonthNumber() {
//			return this.ordinal();
//		}
//		
//		public static Month fromMonthNumber(int monthNumber) {
//			int ordinal = monthNumber;
//			Month[] values = Month.values();
//			
//			if (ordinal < 0 || ordinal > values.length - 1) {
//				return null;
//			}
//			
//			return values[ordinal];
//		}
//		
//		public static Month fromNameForm(String nameForm) {
//			if (nameForm == null) {
//				return null;
//			}
//			
//			String lowercasedNameForm = nameForm.toLowerCase();
//			
//			for (Month currentValue : Month.values()) {
//				List<String> forms = currentValue.getForms();
//				
//				for (String currentForm : forms) {
//					if (lowercasedNameForm.equals(currentForm)) {
//						return currentValue;
//					}
//				}
//			}
//			
//			return null;
//		}
//		
//		public static List<String> getAllForms() {
//			List<String> result = new ArrayList<String>();
//			
//			for (Month currentValue : values()) {
//				result.addAll(currentValue.getForms());
//			}
//			
//			return result;
//		}
//	}
//	
//	public enum MonthPart {
//		BEGINNING("начало", "начала", "начале", "началу", "началом"),
//		END("конец", "конца", "конце", "концу", "концом"),
//		MIDDLE("середина", "середины", "середине", "серединой", "середину");
//		
//		private List<String> forms;
//		
//		private MonthPart(String... forms) {
//			this.forms = Arrays.asList(forms);
//		}
//		
//		public String getName() {
//			return forms.get(0);
//		}
//		
//		public static MonthPart fromNameForm(String nameForm) {
//			if (nameForm == null) {
//				return null;
//			}
//			
//			String lowercasedNameForm = nameForm.toLowerCase();
//			
//			for (MonthPart currentValue : MonthPart.values()) {
//				List<String> forms = currentValue.getForms();
//				
//				for (String currentForm : forms) {
//					if (lowercasedNameForm.equals(currentForm)) {
//						return currentValue;
//					}
//				}
//			}
//			
//			return null;
//		}
//		
//		public static List<String> getAllForms() {
//			List<String> result = new ArrayList<String>();
//			
//			for (MonthPart currentValue : values()) {
//				result.addAll(currentValue.getForms());
//			}
//			
//			return result;
//		}
//		
//		public List<String> getForms() {
//			return Collections.unmodifiableList(forms);
//		}
//	}
//	
//	public enum YearPart {
//		BEGINNING("начало", "начала", "начале", "началу", "началом"),
//		END("конец", "конца", "конце", "концу", "концом"),
//		MIDDLE("середина", "середины", "середине", "серединой", "середину"),
//		SPRING("весна", "весны", "весной", "весне"),
//		SUMMER("лето", "лета", "летом", "лету"),
//		FALL("осень", "осени", "осенью", "осени"),
//		WINTER("зима", "зимы", "зимой", "зиме");
//		
//		
//		private List<String> forms;
//		
//		private YearPart(String... forms) {
//			this.forms = Arrays.asList(forms);
//		}
//		
//		public String getName() {
//			return forms.get(0);
//		}
//		
//		public static YearPart fromNameForm(String nameForm) {
//			if (nameForm == null) {
//				return null;
//			}
//			
//			String lowercasedNameForm = nameForm.toLowerCase();
//			
//			for (YearPart currentValue : YearPart.values()) {
//				List<String> forms = currentValue.getForms();
//				
//				for (String currentForm : forms) {
//					if (lowercasedNameForm.equals(currentForm)) {
//						return currentValue;
//					}
//				}
//			}
//			
//			return null;
//		}
//		
//		public static List<String> getAllForms() {
//			List<String> result = new ArrayList<String>();
//			
//			for (YearPart currentValue : values()) {
//				result.addAll(currentValue.getForms());
//			}
//			
//			return result;
//		}
//		
//		public List<String> getForms() {
//			return Collections.unmodifiableList(forms);
//		}
//	}
//	
//	public enum Hour {
//		BEGINNING("час", "начала", "начале", "началу", "началом"),
//		END("конец", "конца", "конце", "концу", "концом"),
//		MIDDLE("середина", "середины", "середине", "серединой", "середину"),
//		SPRING("весна", "весны", "весной", "весне"),
//		SUMMER("лето", "лета", "летом", "лету"),
//		FALL("осень", "осени", "осенью", "осени"),
//		WINTER("зима", "зимы", "зимой", "зиме");
//		
//		
//		private List<String> forms;
//		
//		private Hour(String... forms) {
//			this.forms = Arrays.asList(forms);
//		}
//		
//		public String getName() {
//			return forms.get(0);
//		}
//		
//		public static YearPart fromNameForm(String nameForm) {
//			if (nameForm == null) {
//				return null;
//			}
//			
//			String lowercasedNameForm = nameForm.toLowerCase();
//			
//			for (YearPart currentValue : YearPart.values()) {
//				List<String> forms = currentValue.getForms();
//				
//				for (String currentForm : forms) {
//					if (lowercasedNameForm.equals(currentForm)) {
//						return currentValue;
//					}
//				}
//			}
//			
//			return null;
//		}
//		
//		public static List<String> getAllForms() {
//			List<String> result = new ArrayList<String>();
//			
//			for (Hour currentValue : values()) {
//				result.addAll(currentValue.getForms());
//			}
//			
//			return result;
//		}
//		
//		public List<String> getForms() {
//			return Collections.unmodifiableList(forms);
//		}
//	}
//	
//	@Override
//	protected void combineTokens(Stack<IToken> sample, Set<IToken> reliableTokens, Set<IToken> doubtfulTokens){
//		
//		
//		
//	}
//
//	@Override
//	protected int continuePush(Stack<IToken> sample) {
//		
//		IToken token = sample.peek();
//		if(checkToken(token)){
//			return CONTINUE_PUSH;
//		}
//		return 1;
//	}
//
//	@Override
//	protected boolean checkPossibleStart(IToken unit) {		
//		return checkToken(unit);
//	}
//
//
//	private boolean checkToken(IToken token){
//		int type = token.getType();
//		String str = token.getStringValue().trim().toLowerCase();
//		
//		if(type == IToken.TOKEN_TYPE_SCALAR){
//			return true;
//		}
//		else if(type == IToken.TOKEN_TYPE_SYMBOL){
//			return true;
//		}
//		else if(type == IToken.TOKEN_TYPE_NON_BREAKING_SPACE){
//			return true;
//		}
//		else if(type == IToken.TOKEN_TYPE_LETTER){
//			return acceptedWords.contains(str);			
//		}
//		return false;
//	}
//	
//}
