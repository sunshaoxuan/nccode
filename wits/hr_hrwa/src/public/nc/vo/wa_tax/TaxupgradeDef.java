package nc.vo.wa_tax;

/**
 * ���������ӿ�
 * @author xuhw
 *
 */
public interface TaxupgradeDef {
	
	/**Ӧ��˰���ö�Ԥ��*/
	String TAXITEM_CODE_TAXABLE_INCOME_YZ1 = "taxable_income_yz";
	/**�ۼ�Ӧ��˰���ö�Ԥ��*/
	String TAXITEM_CODE_TOTAL_TAXABLE_INCOME_YZ2 = "total_taxable_income_yz";
	/**�ۼ�Ӧ��˰*/
	String TAXITEM_CODE_TOTAL_TAXABLEAMT_YZ3 = "total_taxableamt_yz";
	/**�ۼ��ѿ�˰*/
	String TAXITEM_CODE_TOTAL_TAXEDAMT_YZ4 = "total_taxedamt_yz";
	/**���ο�˰*/
	String TAXITEM_CODE_TAXABLEAMT_YZ5 = "taxableamt_yz";
	
	/**��˰��_����˰��*/
	String TAXITEM_CODE_TAX_RATE_YZ6 = "tax_rate_yz";
	/**��˰��_����۳���*/
	String TAXITEM_CODE_TAX_QUICK_DEDUCTION_YZ7 = "quick_deduction_yz";
	/**��˰��_�����۳���*/
	String TAXITEM_CODE_TAX_BASIC_DEDUCTION_YZ8 = "tax_basic_deduction_yz";
	/**��˰��_����ר��۳��ϼ�*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ9 = "tax_deduction_yz";
	/**��˰��_����ר��۳��ϼ�-��������*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ10 = "tax_deduction_parent_yz";
	/**��˰��_����ר��۳��ϼ�-��Ů����*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ11 = "tax_deduction_child_yz";
	/**��˰��_����ר��۳��ϼ�-��ҽ��*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ12 = "tax_deduction_health_yz";
	/**��˰��_����ר��۳��ϼ�-��������*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ13 = "tax_deduction_education_yz";
	/**��˰��_����ר��۳��ϼ�-�ⷿ����*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ14 = "tax_deduction_hoursezu_yz";
	/**��˰��_����ר��۳��ϼ�-ס������*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ15 = "tax_deduction_house_yz";
	/**��˰��_�ۼ�ר��Ӧ��*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ16 = "tax_deduction_totalable_yz";
	/**��˰��_�ۼ�ר���ѿ�*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ17 = "tax_deduction_totaled_yz";
	//-- ��������
	/**Ӧ��˰���ö�Ԥ��*/
	String TAXITEM_NAME_TAXABLE_INCOME_YZ_ZW1 = "Ӧ��˰���ö�_Ԥ��";
	/**�ۼ�Ӧ��˰���ö�Ԥ��*/
	String TAXITEM_NAME_TOTAL_TAXABLE_INCOME_YZ_ZW2 = "�ۼ�Ӧ��˰���ö�_Ԥ��";
	/**�ۼ�Ӧ��˰*/
	String TAXITEM_NAME_TOTAL_TAXABLEAMT_YZ_ZW3 = "�ۼ�Ӧ��˰_Ԥ��";
	/**�ۼ��ѿ�˰*/
	String TAXITEM_NAME_TOTAL_TAXEDAMT_YZ_ZW4 = "�ۼ��ѿ�˰_Ԥ��";
	/**���ο�˰*/
	String TAXITEM_NAME_TAXABLEAMT_YZ_ZW5 = "���ο�˰_Ԥ��";
	
	/**��˰��_����˰��*/
	String TAXITEM_NAME_TAX_RATE_YZ6 = "����˰��_Ԥ��";
	/**��˰��_����۳���*/
	String TAXITEM_NAME_TAX_QUICK_DEDUCTION_YZ7 = "����۳���_Ԥ��";
	/**��˰��_�����۳���*/
	String TAXITEM_NAME_TAX_BASIC_DEDUCTION_YZ8 = "�������ÿ۳���_Ԥ��";
	/**��˰��_����ר��۳��ϼ�-����*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ9 = "����ר��۳��ϼ�_Ԥ��";
	/**��˰��_����ר��۳��ϼ�-��������*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ10 = "ר����������_Ԥ��";
	/**��˰��_����ר��۳��ϼ�-��Ů����*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ11 = "ר����Ů����_Ԥ��";
	/**��˰��_����ר��۳��ϼ�-��ҽ��*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ12 = "ר���ҽ��_Ԥ��";
	/**��˰��_����ר��۳��ϼ�-��������*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ13 = "ר���������_Ԥ��";
	/**��˰��_����ר��۳��ϼ�-�ⷿ����*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ14 = "ר���ⷿ����_Ԥ��";
	/**��˰��_����ר��۳��ϼ�-ס������*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ15 = "ר��ס������_Ԥ��";
	/**��˰��_�ۼ�ר��Ӧ��*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ16 = "�ۼ�ר��Ӧ��_Ԥ��";
	/**��˰��_�ۼ�ר���ѿ�*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ17 = "�ۼ�ר���ѿ�_Ԥ��";
	
	//��ʽ���� - formulastr
	/**Ӧ��˰���ö�Ԥ��*/
	String TAXITEM_FORMULASTR_TAXABLE_INCOME_YZ1 = "��˰��_����Ӧ��˰���ö�(���ο�˰����,#����ר��۳��ϼ�_Ԥ��#)";
	/**�ۼ�Ӧ��˰���ö�Ԥ��*/
	String TAXITEM_FORMULASTR_TOTAL_TAXABLE_INCOME_YZ2 = "��˰��_�ۼ�Ӧ��˰���ö�(#Ӧ��˰���ö�_Ԥ��#)";
	/**�ۼ�Ӧ��˰*/
	String TAXITEM_FORMULASTR_TOTAL_TAXABLEAMT_YZ3 = "��˰��_�ۼ�Ӧ��˰(#�ۼ�Ӧ��˰���ö�_Ԥ��#)";
	/**�ۼ��ѿ�˰*/
	String TAXITEM_FORMULASTR_TOTAL_TAXEDAMT_YZ4 = "��˰��_�ۼ��ѿ�˰(#�ۼ�Ӧ��˰���ö�_Ԥ��#,#Ӧ��˰���ö�_Ԥ��#)";
	/**���ο�˰*/
	String TAXITEM_FORMULASTR_TAXABLEAMT_YZ5 = "[н�ʷ�����Ŀ.#�ۼ�Ӧ��˰_Ԥ��#] - [н�ʷ�����Ŀ.#�ۼ��ѿ�˰_Ԥ��#]";
	
	/**��˰��_����˰��*/
	String TAXITEM_FORMULASTR_TAX_RATE_YZ6 = "��˰��_����˰��(#�ۼ�Ӧ��˰���ö�_Ԥ��#)";
	/**��˰��_����۳���*/
	String TAXITEM_FORMULASTR_TAX_QUICK_DEDUCTION_YZ7 = "��˰��_����۳���(#�ۼ�Ӧ��˰���ö�_Ԥ��#)";
	/**��˰��_�����۳���*/
	String TAXITEM_FORMULASTR_TAX_BASIC_DEDUCTION_YZ8 = "��˰��_�������ÿ۳���()";
	/**��˰��_����ר��۳��ϼ�*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ9 = "��˰��_ר��ӷ��ÿ۳�(ȫ��)";
	/**��˰��_����ר��۳��ϼ�*/
//	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ9_1 = "[����н����Ŀ.#�ۼ�ר��Ӧ��_Ԥ��#] - [����н����Ŀ.#�ۼ�ר���ѿ�_Ԥ��#]";
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ9_1 = " ���  [����н����Ŀ.�ۼ�ר��Ӧ��_Ԥ��] - [����н����Ŀ.�ۼ�ר���ѿ�_Ԥ��]  <= 0 �� 0 ���� [����н����Ŀ.�ۼ�ר��Ӧ��_Ԥ��] - [����н����Ŀ.�ۼ�ר���ѿ�_Ԥ��]";
	
	/**��˰��_����ר��۳��ϼ�*/
//	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ9_2 = "[н�ʷ�����Ŀ.#�ۼ�ר��Ӧ��_Ԥ��#] - [н�ʷ�����Ŀ.#�ۼ�ר���ѿ�_Ԥ��#]";
	
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ9_2 = " ���  [н�ʷ�����Ŀ.�ۼ�ר��Ӧ��_Ԥ��] - [н�ʷ�����Ŀ.�ۼ�ר���ѿ�_Ԥ��]  <= 0 �� 0 ���� [н�ʷ�����Ŀ.�ۼ�ר��Ӧ��_Ԥ��] - [н�ʷ�����Ŀ.�ۼ�ר���ѿ�_Ԥ��]";
	
	/**��˰��_�ۼ�ר��Ӧ��*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ16 = "[����н����Ŀ.#ר����������_Ԥ��#] " +
			"+ [����н����Ŀ.#ר����Ů����_Ԥ��#] + [����н����Ŀ.#ר���ҽ��_Ԥ��#] " +
			"+ [����н����Ŀ.#ר���������_Ԥ��#] + [����н����Ŀ.#ר���ⷿ����_Ԥ��#] " +
			"+ [н����Ŀ.#ר��ס������_Ԥ��#]";
	/**��˰��_�ۼ�ר���ѿ�*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ17 = "��˰��_�ۼ�ר���ѿ�(#�ۼ�ר��Ӧ��_Ԥ��#)";
	/**��˰��_����ר��۳��ϼ�-��������*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ10 = "��˰��_ר��ӷ��ÿ۳�(��������)";
	/**��˰��_����ר��۳��ϼ�-��Ů����*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ11 = "��˰��_ר��ӷ��ÿ۳�(��Ů����)";
	/**��˰��_����ר��۳��ϼ�-��ҽ��*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ12 = "��˰��_ר��ӷ��ÿ۳�(��ҽ��)";
	/**��˰��_����ר��۳��ϼ�-��������*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ13 = "��˰��_ר��ӷ��ÿ۳�(��������)";
	/**��˰��_����ר��۳��ϼ�-�ⷿ����*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ14 = "��˰��_ר��ӷ��ÿ۳�(�ⷿ����)";
	/**��˰��_����ר��۳��ϼ�-ס������*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ15 = "��˰��_ר��ӷ��ÿ۳�(ס������)";
	
	//��ʽ���� - formula
	/**Ӧ��˰���ö�Ԥ��*/
	String TAXITEM_FORMULA_TAXABLE_INCOME_YZ1 = "taxfunTaxableAmtCurrentPeriod(f_4,@tax_special_deduction_yz@)";
	/**�ۼ�Ӧ��˰���ö�Ԥ��*/
	String TAXITEM_FORMULA_TOTAL_TAXABLE_INCOME_YZ2 = "taxfunTotalTaxAbleIncome(@taxable_income_yz@)";
	/**�ۼ�Ӧ��˰*/
	String TAXITEM_FORMULA_TOTAL_TAXABLEAMT_YZ3 = "taxfunTotalTaxAbleAmt(@total_taxable_income_yz@)";
	/**�ۼ��ѿ�˰*/
	String TAXITEM_FORMULA_TOTAL_TAXEDAMT_YZ4 = "taxfunTotalTaxedAmt(@total_taxable_income_yz@,@taxable_income_yz@)";
//	/**���ο�˰*/
//	String TAXITEM_FORMULA_TAXABLEAMT_YZ5 = "wa_data.@total_taxableamt_yz@ - wa_data.@total_taxedamt_yz@";
	
	/**��˰��_����˰��*/
	String TAXITEM_FORMULA_TAX_RATE_YZ6 = "taxfunTaxRate(@total_taxable_income_yz@)";
	/**��˰��_����۳���*/
	String TAXITEM_FORMULA_TAX_QUICK_DEDUCTION_YZ7 = "taxfunTaxQuickDeduction(@total_taxable_income_yz@)";
	/**��˰��_�����۳���*/
	String TAXITEM_FORMULA_TAX_BASIC_DEDUCTION_YZ8 = "taxfunTaxBasicDeduction()";
	/**��˰��_����ר��۳��ϼ�*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ9 = "taxfunSpecialAdditionaDeduction(0)";
	/**��˰��_����ר��۳��ϼ�*/
//	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ9_1 = "wa_data.@deu16@ - wa_data.@deu17@";
	// iif( wa_data.f_52 - wa_data.f_53  <= 0 , 0 , wa_data.f_52 - wa_data.f_53)
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ9_1 = "iif( wa_data.@deu16@ - wa_data.@deu17@  <= 0 , 0 , wa_data.@deu16@ - wa_data.@deu17@)";
	/**��˰��_����ר��۳��ϼ�-��������*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ10 = "taxfunSpecialAdditionaDeduction(5)";
	/**��˰��_����ר��۳��ϼ�-��Ů����*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ11 = "taxfunSpecialAdditionaDeduction(1)";
	/**��˰��_����ר��۳��ϼ�-��ҽ��*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ12 = "taxfunSpecialAdditionaDeduction(6)";
	/**��˰��_����ר��۳��ϼ�-��������*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ13 = "taxfunSpecialAdditionaDeduction(2)";
	/**��˰��_����ר��۳��ϼ�-�ⷿ����*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ14 = "taxfunSpecialAdditionaDeduction(4)";
	/**��˰��_����ר��۳��ϼ�-ס������*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ15 = "taxfunSpecialAdditionaDeduction(3)";
	/**��˰��_�ۼ�ר��Ӧ��*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ16 = "wa_data.@deu10@ + wa_data.@deu11@ + wa_data.@deu12@+ wa_data.@deu13@+ wa_data.@deu14@+ wa_data.@deu15@";
	/**��˰��_�ۼ�ר���ѿ�*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ17 = "taxDeductionTotaled(@tax_deduction_totalable_yz@)";
	
	/**��ʼ������н����Ŀ*/
	String OPTYPE_INITITEM = "initpubitem";
	/**��ʼ��н�ʷ�����Ŀ*/
	String OPTYPE_INITCLASSITEM = "initclassitem";
	/**Ԥ�����˰�ʱ�*/
	String OPTYPE_INITTAXTABLE = "inityeartaxtable";
	/**����н���ڼ�*/
	String OPTYPE_INITTAXPERIOD = "inittaxperiod";
	/**ж��*/
	String OPTYPE_UNINIT = "unInit";
	String ADDDEDUCTION_FORMULA_UPDATE = "initAddFormula";
	/**�����������ķ������ж�������**/
	String OPTYPE_UPGRADE_SPECIALDEDUCTION = "updateDeduction";
	
	//ϵͳ���ο�˰���� - ��� taxRate(0,wa_data.f_4,null,null)	taxRate(0,[н�ʷ�����Ŀ.���ο�˰����],null,null)
	String F5_FormulaStr = "@��˰����@(3, [н�ʷ�����Ŀ.#�ۼ�Ӧ��˰_Ԥ��#], [н�ʷ�����Ŀ.#�ۼ��ѿ�˰_Ԥ��#], null)";
	//ϵͳ���ο�˰ ���� ��� 
	String F5_Formula = "taxRate(3, wa_data.@total_taxableamt_yz@, wa_data.@total_taxedamt_yz@, null)";
	
	
	/**δ����*/
	int TAXTYPE_UNGEN = 0;
	/**������*/
	int TAXTYPE_GENED = 1;
	/**������*/
	int TAXTYPE_LOCKED = 2;
	
	//---ר��ӷ��ÿ۳�---
//	 * String[] ml = {"0","1","2","3","4","5"};
//		String[] mlDefault = new String[]{"ȫ��","��Ů����","��������","ס��������Ϣ","ס�����","��������"};
//*
	/**ר����۳�-ȫ��*/
	int ADDDEDUCTION_KEY_ALL = 0;
	/**ר����۳�-��Ů����*/
	int ADDDEDUCTION_KEY_CHILD = 1;
	/**ר����۳�-��������*/
	int ADDDEDUCTION_KEY_EDUCATION = 2;
	/**ר����۳�-ס��������Ϣ*/
	int ADDDEDUCTION_KEY_HOURSE_ZU = 3;
	/**ר����۳�-ס�����*/
	int ADDDEDUCTION_KEY_HOURSE = 4;
	/**ר����۳�-��������*/
	int ADDDEDUCTION_KEY_PARENT = 5;
	/**ר����۳�-��ҽ��*/
	int ADDDEDUCTION_KEY_HEALTH = 6;
	
	/**ר����۳�-��Ů����*/
	String ADDDEDUCTION_KEY_CHILD_NAME = "��Ů����";
	/**ר����۳�-��������*/
	String ADDDEDUCTION_KEY_EDUCATION_NAME = "��������";
	/**ר����۳�-ס��������Ϣ*/
	String ADDDEDUCTION_KEY_HOURSE_ZU_NAME = "ס��������Ϣ";
	/**ר����۳�-ס�����*/
	String ADDDEDUCTION_KEY_HOURSE_NAME = "ס�����";
	/**ר����۳�-��������*/
	String ADDDEDUCTION_KEY_PARENT_NAME = "��������";
	/**ר����۳�-��ҽ��*/
	String ADDDEDUCTION_KEY_HEALTH_NAME = "��ҽ��";
	
	//------------export
	short SHOW_ITEM_CNT = 10;
	short ERROR_REASON_LINE = SHOW_ITEM_CNT + 1;  //�����������Ӧ��Ϊ ����������1
}