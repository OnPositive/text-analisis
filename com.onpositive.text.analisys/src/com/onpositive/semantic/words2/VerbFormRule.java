package com.onpositive.semantic.words2;

import java.io.Serializable;

public class VerbFormRule implements IFormRule,Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int IA=1;
	public static final int IA_PAST=1;
	public static final int WE=2;
	public static final int WE_PAST=3;
	
	public static final int YOU=4;
	public static final int YOU_PAST=5;
	public static final int YOU_DIRECT=6;
	
	
	public static final int ON_ONA_ON=4;
	public static final int ON_ONA_ONO_PAST=4;
	
	public static final int ONI=6;
	public static final int ONI_PAST=7;
	
	public static final int FUTURE=8;
	public static final int INFINITIVE=9;
	
	@FieldMapping(value="�",relation=IA)
	String ia;
	
	@FieldMapping(value="� (����.)",relation=IA_PAST)
	String ia_past;
	
	@FieldMapping(value="��",relation=WE)
	String we;
	
	@FieldMapping(value="�� (����.)",relation=WE_PAST)
	String we_past;
	
	@FieldMapping(value="��",relation=YOU)
	String thou;
	
	@FieldMapping(value="�� (����.)",relation=YOU_PAST)
	String thou_past;
	
	@FieldMapping(value="�� (�����.)",relation=YOU_DIRECT)
	String thou_direct;
	
	@FieldMapping(value="��",relation=YOU)
	String you;
	
	@FieldMapping(value="�� (����.)",relation=YOU_PAST)
	String you_past;
	
	@FieldMapping(value="�� (�����.)",relation=YOU_DIRECT)
	String you_direct;
	
	@FieldMapping(value="��/���/���",relation=ON_ONA_ON)
	String third_form;
	
	@FieldMapping(value="��/���/��� (����.)",relation=ON_ONA_ONO_PAST)
	String third_form_past;
	
	@FieldMapping(value="���",relation=YOU_DIRECT)
	String they;
	
	@FieldMapping(value="��� (����.)",relation=YOU_DIRECT)
	String they_past;
	
	@FieldMapping(value="�������",relation=FUTURE)
	String future;
	
	@FieldMapping(value="���������",relation=INFINITIVE)
	String infinitive;


/*	|�          ={{{������1}}}�
			|� (����.) ={{{������}}}�<br />{{{������}}}��
			|�� ={{{������1}}}��
			|�� (����.) ={{{������}}}��
			|�� ={{{������1}}}���
			|�� (����.) ={{{������}}}�<br />{{{������}}}��
			|�� (�����.)={{{������1}}}�
			|�� ={{{������1}}}���
			|�� (����.) ={{{������}}}��
			|�� (�����.)={{{������1}}}���
			|��/���/��� ={{{������1}}}��
			|��/���/��� (����.)={{{������}}}�<br />{{{������}}}��<br />{{{������}}}��
			|��� ={{{������1}}}��
			|��� (����.)={{{������}}}��
			|�������� = {{{������1}}}����
			|�������� = {{{������}}}����
			|��������� = {{{������1}}}�
			|��������� = {{{������}}}�, {{{������}}}���
			|��������� = {{{������1}}}����
			|������������� = {{#if:{{{2�|}}}|{{{������2}}}�����|{{{�������������|}}}}}
			|������� = ����/������� {{{������}}}��
			|��������� = {{{������}}}��
			|hide-text={{{hide-text|}}}
			|�����={{{�����|}}}
			|���������=2a
			|���={{#if:{{{2�|}}}|2|�}}
			|��={{{��|}}}
			|�����={{{�����|}}}
			|�����-��={{{�����-��|}}}
			|�������={{{�������|}}}
			}}*/
	public VerbFormRule(){
		
	}
}
