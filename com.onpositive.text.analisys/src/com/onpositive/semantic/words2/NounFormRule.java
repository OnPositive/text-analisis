package com.onpositive.semantic.words2;

import java.io.Serializable;

public class NounFormRule implements Serializable,IFormRule{
	
	public static final int NOM_SG=1;
	public static final int NOM_PL=2;
	
	public static final int GEN_SG=3;
	public static final int GEN_PL=4;
	
	public static final int DAT_SG=5;
	public static final int DAT_PL=6;
	
	public static final int ACC_SG=7;
	public static final int ACC_PL=8;
	
	public static final int INS_SG=9;
	public static final int INS_PL=10;
	
	public static final int PRP_SG=11;
	public static final int PRP_PL=12;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Именительный единственное 
	 */
	String nom_sg;
	/**
	 * Именительный множественное
	 */
	String nom_pl;
	
	/**
	 * Родительный единственное 
	 */
	String gen_sg;
	/**
	 * Родительный множественное
	 */
	String gen_pl;
	
	/**
	 * Дательный единственное 
	 */
	String dat_sg;
	/**
	 * Дательный множественное
	 */
	String dat_pl;
	
	/**
	 * Дательный единственное 
	 */
	String acc_sg;
	/**
	 * Дательный множественное
	 */
	String acc_pl;
	
	/**
	 * Творительный единственное
	 */
	String ins_sg;
	
	/**
	 * Творительный множенственное
	 */
	String ins_pl;
	
	/**
	 * Предложный единственное
	 */
	String prp_sg;
	
	/**
	 * Предложный множественное
	 */
	String prp_pl;
}