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
	 * ������������ ������������ 
	 */
	String nom_sg;
	/**
	 * ������������ �������������
	 */
	String nom_pl;
	
	/**
	 * ����������� ������������ 
	 */
	String gen_sg;
	/**
	 * ����������� �������������
	 */
	String gen_pl;
	
	/**
	 * ��������� ������������ 
	 */
	String dat_sg;
	/**
	 * ��������� �������������
	 */
	String dat_pl;
	
	/**
	 * ��������� ������������ 
	 */
	String acc_sg;
	/**
	 * ��������� �������������
	 */
	String acc_pl;
	
	/**
	 * ������������ ������������
	 */
	String ins_sg;
	
	/**
	 * ������������ ��������������
	 */
	String ins_pl;
	
	/**
	 * ���������� ������������
	 */
	String prp_sg;
	
	/**
	 * ���������� �������������
	 */
	String prp_pl;
}