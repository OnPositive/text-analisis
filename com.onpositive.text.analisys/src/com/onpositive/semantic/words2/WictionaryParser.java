package com.onpositive.semantic.words2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import com.carrotsearch.hppc.CharOpenHashSet;
import com.onpositive.wikipedia.dumps.builder.IPageVisitor;
import com.onpositive.wikipedia.dumps.builder.PageModel;
import com.onpositive.wikipedia.dumps.builder.XMLPageParser;

public class WictionaryParser {

	static final String russianLetters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";

	static void fill(XMLPageParser pp, final WordNet wi, String name) {
		try {
			pp.visitContent(new BufferedReader(new InputStreamReader(
					new FileInputStream(name), "UTF-8")), new IPageVisitor() {

				@Override
				public void visit(PageModel model) {
					if (model.getNamespace() == 10) {
						if (model.getTitle().startsWith("Шаблон:")) {
							System.out.println(model.getTitle());
						}
						if (model.getTitle().startsWith("Шаблон:сущ ru")) {
							try {
								WordFormTemplate wordFormTemplate = new WordFormTemplate(
										model.getTitle(), model.getText(), wi,
										Word.NOUN);
								wi.registerTemplate(wordFormTemplate);
							} catch (IOException e) {
							}
						}
						if (model.getTitle().startsWith("Шаблон:прил ru")) {
							try {
								WordFormTemplate wordFormTemplate = new WordFormTemplate(
										model.getTitle(), model.getText(), wi,
										Word.ADJ);
								wi.registerTemplate(wordFormTemplate);
							} catch (IOException e) {
							}
						}
						if (model.getTitle().startsWith("Шаблон:гл ru")) {
							try {
								WordFormTemplate wordFormTemplate = new WordFormTemplate(
										model.getTitle(), model.getText(), wi,
										Word.VERB);
								wi.registerTemplate(wordFormTemplate);
								/*
								 * WordFormTemplate wordFormTemplate = new
								 * WordFormTemplate(model.getText());
								 * wi.registerTemplate
								 * (model.getTitle(),wordFormTemplate);
								 */
							} catch (Exception e) {
							}
						}
						// System.out.println(model.getTitle());
					}
				}

			});
		} catch (UnsupportedEncodingException e1) {
		} catch (FileNotFoundException e1) {

		}
		System.out.println("Templates built!!");
		try {
			final CharOpenHashSet mm = new CharOpenHashSet();
			for (char c : russianLetters.toCharArray()) {
				mm.add(c);
				mm.add(Character.toUpperCase(c));
			}
			try {

				pp.visitContent(new BufferedReader(new InputStreamReader(
						new FileInputStream(name), "UTF-8")),
						new IPageVisitor() {

							@Override
							public void visit(PageModel model) {

								String title = model.getTitle();
								if (title.equals("промышленный")) {
									System.out.println("A");
								}
								int length = title.length();
								for (int a = 0; a < length; a++) {
									if (!mm.contains(title.charAt(a))) {
										return;
									}
								}
								if (model.getText().startsWith("#")) {
									String lowerCase = model.getText()
											.toLowerCase();
									if (lowerCase.startsWith("#redirect [[")) {
										String sm = model.getText().substring(
												"#REDIRECT [[".length());
										sm = sm.substring(0, sm.length() - 2);
										wi.markRedirect(model.getTitle(), sm);
										return;
									}
									if (lowerCase
											.startsWith("#перенаправление [[")) {
										String sm = model.getText().substring(
												"#перенаправление [[".length());
										sm = sm.substring(0, sm.length() - 2);
										wi.markRedirect(model.getTitle(), sm);
										return;
									}

								}
								if (!model.getText().contains("{{-ru-}}")) {
									return;
								}
								BufferedReader rs = new BufferedReader(
										new StringReader(model.getText()));
								boolean inMode = false;
								int kind = -1;
								Word orCreate = (Word) wi.getOrCreateWord(model
										.getTitle().toLowerCase());
								while (true) {
									String readLine;
									try {

										readLine = rs.readLine();

										if (readLine == null) {
											break;
										}
										if (readLine.startsWith("{{")) {
											String rl = readLine.substring(2)
													.trim();
											if (rl.startsWith("сущ")
													|| rl.startsWith("сущ")) {
												String substring = cleanTemplate(rl);
												WordFormTemplate findTemplate = innerFindTemplate(wi, substring);
												if (findTemplate != null) {
													orCreate.setTemplate(findTemplate);

												}
												orCreate.setKind(Word.NOUN);
											}
											if (rl.startsWith("прил")) {
												String substring = cleanTemplate(rl);
												WordFormTemplate findTemplate = innerFindTemplate(wi, substring);
												if (findTemplate != null) {
													orCreate.setTemplate(findTemplate);
												}
												orCreate.setKind(Word.ADJ);
											}

											if (rl.startsWith("гл")) {
												orCreate.setKind(Word.VERB);
												String substring = cleanTemplate(rl);
												WordFormTemplate findTemplate = innerFindTemplate(wi, substring);
												if (findTemplate != null) {
													orCreate.setTemplate(findTemplate);
												}
												orCreate.setKind(Word.VERB);
											}
										}
										if (readLine.contains("{{собств.}}")) {
											orCreate.setFeature(Word.FEATURE_NAME);
										}

										if (readLine.contains("{{топоним}}")) {
											orCreate.setFeature(Word.FEATURE_TOPONIM);
										}
										if (readLine.contains("топоним")) {
											orCreate.setFeature(Word.FEATURE_TOPONIM);
										}
										{
										String s = readLine;
										while (s.indexOf("|основа") > -1) {
											s = s.substring(
													s.indexOf("|основа"));
											if (s.startsWith("|основа")) {
												if (s.indexOf('=') > 0) {
													char c = s
															.charAt("|основа"
																	.length());
													int oo = 0;
													if (Character.isDigit(c)) {
														oo = Integer
																.parseInt(""
																		+ c);
													}
													String trim = s
															.substring(
																	s.indexOf('=') + 1)
															.trim();
													if (trim.length() == 0) {
														trim = null;
													}
													if (trim!=null){
													int indexOf = trim.indexOf('|');
													if (indexOf!=-1){
														trim=trim.substring(0,indexOf);
													}
													indexOf = trim.indexOf('}');
													if (indexOf!=-1){
														trim=trim.substring(0,indexOf);
													}
													orCreate.registerFoundation(
															oo, trim);
													}
												}
											}
											s=s.substring("|основа".length());
										}}

										if (readLine.contains("= Гиперонимы =")) {
											inMode = true;
											kind = WordRelation.GENERALIZATION;
											continue;
										}
										if (readLine.contains("= Синонимы =")) {
											inMode = true;
											kind = WordRelation.SYNONIM;
											continue;
										}
										if (readLine.contains("= Антонимы =")) {
											inMode = true;
											kind = WordRelation.ANTONIM;
											continue;
										}
										if (readLine.contains("= Гипонимы =")) {
											inMode = true;
											kind = WordRelation.SPECIALIZATION;
											continue;
										}
										if (inMode) {
											if (readLine.startsWith("==")) {
												inMode = false;
											} else {
												if (readLine.startsWith("#")) {
													readLine = readLine
															.substring(1)
															.trim();
												}
												if (readLine.length() == 0) {
													continue;
												}
												readLine = readLine.replace(
														';', ',');
												String[] split = readLine
														.split(",");
												for (String s : split) {
													s = s.trim();
													if (s.startsWith("[[")) {
														if (s.endsWith("]]")) {
															String value = s
																	.substring(
																			2,
																			s.length() - 2);
															orCreate.registerRelation(
																	kind,
																	wi.getOrCreateRelationTarget(value));
														}
													}
												}

											}
										}
									} catch (IOException e) {
										break;
									}

								}

							}

							private WordFormTemplate innerFindTemplate(
									final WordNet wi, String substring) {
								int indexOf = substring.indexOf('|');
								if (indexOf!=-1){
									substring=substring.substring(0,indexOf);
								}
								return wi
										.findTemplate("Шаблон:"
												+ substring);
							}

							private String cleanTemplate(String readLine) {
								String substring = readLine;
								if (substring.endsWith("\"")) {
									substring = substring.substring(0,
											substring.length() - 1);
								}
								if (substring.endsWith("'")) {
									substring = substring.substring(0,
											substring.length() - 1);
								}
								substring = substring.trim();
								return substring;
							}
						});
				wi.init();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
