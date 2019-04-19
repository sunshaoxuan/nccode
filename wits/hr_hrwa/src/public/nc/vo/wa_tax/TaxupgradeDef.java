package nc.vo.wa_tax;

/**
 * 升级常量接口
 * @author xuhw
 *
 */
public interface TaxupgradeDef {
	
	/**应纳税所得额预制*/
	String TAXITEM_CODE_TAXABLE_INCOME_YZ1 = "taxable_income_yz";
	/**累计应纳税所得额预制*/
	String TAXITEM_CODE_TOTAL_TAXABLE_INCOME_YZ2 = "total_taxable_income_yz";
	/**累计应纳税*/
	String TAXITEM_CODE_TOTAL_TAXABLEAMT_YZ3 = "total_taxableamt_yz";
	/**累计已扣税*/
	String TAXITEM_CODE_TOTAL_TAXEDAMT_YZ4 = "total_taxedamt_yz";
	/**本次扣税*/
	String TAXITEM_CODE_TAXABLEAMT_YZ5 = "taxableamt_yz";
	
	/**新税改_适用税率*/
	String TAXITEM_CODE_TAX_RATE_YZ6 = "tax_rate_yz";
	/**新税改_速算扣除数*/
	String TAXITEM_CODE_TAX_QUICK_DEDUCTION_YZ7 = "quick_deduction_yz";
	/**新税改_基本扣除数*/
	String TAXITEM_CODE_TAX_BASIC_DEDUCTION_YZ8 = "tax_basic_deduction_yz";
	/**新税改_附加专项扣除合计*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ9 = "tax_deduction_yz";
	/**新税改_附加专项扣除合计-赡养老人*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ10 = "tax_deduction_parent_yz";
	/**新税改_附加专项扣除合计-子女教育*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ11 = "tax_deduction_child_yz";
	/**新税改_附加专项扣除合计-大病医疗*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ12 = "tax_deduction_health_yz";
	/**新税改_附加专项扣除合计-继续教育*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ13 = "tax_deduction_education_yz";
	/**新税改_附加专项扣除合计-租房补贴*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ14 = "tax_deduction_hoursezu_yz";
	/**新税改_附加专项扣除合计-住房补贴*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ15 = "tax_deduction_house_yz";
	/**新税改_累计专项应扣*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ16 = "tax_deduction_totalable_yz";
	/**新税改_累计专项已扣*/
	String TAXITEM_CODE_TAX_SPECIAL_DEDUCTION_YZ17 = "tax_deduction_totaled_yz";
	//-- 中文名称
	/**应纳税所得额预制*/
	String TAXITEM_NAME_TAXABLE_INCOME_YZ_ZW1 = "应纳税所得额_预制";
	/**累计应纳税所得额预制*/
	String TAXITEM_NAME_TOTAL_TAXABLE_INCOME_YZ_ZW2 = "累计应纳税所得额_预制";
	/**累计应纳税*/
	String TAXITEM_NAME_TOTAL_TAXABLEAMT_YZ_ZW3 = "累计应纳税_预制";
	/**累计已扣税*/
	String TAXITEM_NAME_TOTAL_TAXEDAMT_YZ_ZW4 = "累计已扣税_预制";
	/**本次扣税*/
	String TAXITEM_NAME_TAXABLEAMT_YZ_ZW5 = "本次扣税_预制";
	
	/**新税改_适用税率*/
	String TAXITEM_NAME_TAX_RATE_YZ6 = "适用税率_预制";
	/**新税改_速算扣除数*/
	String TAXITEM_NAME_TAX_QUICK_DEDUCTION_YZ7 = "速算扣除数_预制";
	/**新税改_基本扣除数*/
	String TAXITEM_NAME_TAX_BASIC_DEDUCTION_YZ8 = "基本费用扣除额_预制";
	/**新税改_附加专项扣除合计-本期*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ9 = "附加专项扣除合计_预制";
	/**新税改_附加专项扣除合计-赡养老人*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ10 = "专项赡养老人_预制";
	/**新税改_附加专项扣除合计-子女教育*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ11 = "专项子女教育_预制";
	/**新税改_附加专项扣除合计-大病医疗*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ12 = "专项大病医疗_预制";
	/**新税改_附加专项扣除合计-继续教育*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ13 = "专项继续教育_预制";
	/**新税改_附加专项扣除合计-租房补贴*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ14 = "专项租房补贴_预制";
	/**新税改_附加专项扣除合计-住房补贴*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ15 = "专项住房补贴_预制";
	/**新税改_累计专项应扣*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ16 = "累计专项应扣_预制";
	/**新税改_累计专项已扣*/
	String TAXITEM_NAME_TAX_SPECIAL_DEDUCTION_YZ17 = "累计专项已扣_预制";
	
	//公式内容 - formulastr
	/**应纳税所得额预制*/
	String TAXITEM_FORMULASTR_TAXABLE_INCOME_YZ1 = "新税改_本期应纳税所得额(本次扣税基数,#附加专项扣除合计_预制#)";
	/**累计应纳税所得额预制*///zhaochxs 修改累计应纳税所得额算法
	String TAXITEM_FORMULASTR_TOTAL_TAXABLE_INCOME_YZ2 = "新税改_累计应纳税所得额(本次扣税基数,#累计专项应扣_预制#)";
	/**累计应纳税*/
	String TAXITEM_FORMULASTR_TOTAL_TAXABLEAMT_YZ3 = "新税改_累计应纳税(#累计应纳税所得额_预制#,#基本费用扣除额_预制#)";
	/**累计已扣税*/
	String TAXITEM_FORMULASTR_TOTAL_TAXEDAMT_YZ4 = "新税改_累计已扣税(本次扣税)";
	/**本次扣税*/
	String TAXITEM_FORMULASTR_TAXABLEAMT_YZ5 = "[薪资发放项目.#累计应纳税_预制#] - [薪资发放项目.#累计已扣税_预制#]";
	
	/**新税改_适用税率*/
	String TAXITEM_FORMULASTR_TAX_RATE_YZ6 = "新税改_适用税率(#累计应纳税所得额_预制#,#基本费用扣除额_预制#)";
	/**新税改_速算扣除数*/
	String TAXITEM_FORMULASTR_TAX_QUICK_DEDUCTION_YZ7 = "新税改_速算扣除数(#累计应纳税所得额_预制#,#基本费用扣除额_预制#)";
	/**新税改_基本扣除数*/
	String TAXITEM_FORMULASTR_TAX_BASIC_DEDUCTION_YZ8 = "新税改_基本费用扣除额()";
	/**新税改_附加专项扣除合计*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ9 = "新税改_专项附加费用扣除(全部)";
	/**新税改_附加专项扣除合计*/
//	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ9_1 = "[公共薪资项目.#累计专项应扣_预制#] - [公共薪资项目.#累计专项已扣_预制#]";
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ9_1 = " 如果  [公共薪资项目.累计专项应扣_预制] - [公共薪资项目.累计专项已扣_预制]  <= 0 则 0 否则 [公共薪资项目.累计专项应扣_预制] - [公共薪资项目.累计专项已扣_预制]";
	
	/**新税改_附加专项扣除合计*/
//	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ9_2 = "[薪资发放项目.#累计专项应扣_预制#] - [薪资发放项目.#累计专项已扣_预制#]";
	
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ9_2 = " 如果  [薪资发放项目.累计专项应扣_预制] - [薪资发放项目.累计专项已扣_预制]  <= 0 则 0 否则 [薪资发放项目.累计专项应扣_预制] - [薪资发放项目.累计专项已扣_预制]";
	
	/**新税改_累计专项应扣*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ16 = "[公共薪资项目.#专项赡养老人_预制#] " +
			"+ [公共薪资项目.#专项子女教育_预制#] + [公共薪资项目.#专项大病医疗_预制#] " +
			"+ [公共薪资项目.#专项继续教育_预制#] + [公共薪资项目.#专项租房补贴_预制#] " +
			"+ [薪资项目.#专项住房补贴_预制#]";
	/**新税改_累计专项已扣*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ17 = "新税改_累计专项已扣(#累计专项应扣_预制#)";
	/**新税改_附加专项扣除合计-赡养老人*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ10 = "新税改_专项附加费用扣除(赡养老人)";
	/**新税改_附加专项扣除合计-子女教育*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ11 = "新税改_专项附加费用扣除(子女教育)";
	/**新税改_附加专项扣除合计-大病医疗*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ12 = "新税改_专项附加费用扣除(大病医疗)";
	/**新税改_附加专项扣除合计-继续教育*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ13 = "新税改_专项附加费用扣除(继续教育)";
	/**新税改_附加专项扣除合计-租房补贴*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ14 = "新税改_专项附加费用扣除(租房补贴)";
	/**新税改_附加专项扣除合计-住房补贴*/
	String TAXITEM_FORMULASTR_TAX_SPECIAL_DEDUCTION_YZ15 = "新税改_专项附加费用扣除(住房补贴)";
	
	//公式内容 - formula
	/**应纳税所得额预制*/
	String TAXITEM_FORMULA_TAXABLE_INCOME_YZ1 = "taxfunTaxableAmtCurrentPeriod(f_4,@tax_special_deduction_yz@)";
	/**累计应纳税所得额预制*///zhaochxs 修改累计应纳税所得额算法
	String TAXITEM_FORMULA_TOTAL_TAXABLE_INCOME_YZ2 = "taxfunTotalTaxAbleIncomeTrue(f_4,@tax_deduction_totalable_yz@)";
	/**累计应纳税*/
	String TAXITEM_FORMULA_TOTAL_TAXABLEAMT_YZ3 = "taxfunTotalTaxAbleAmt(@total_taxable_income_yz@,@tax_basic_deduction_yz@)";
	/**累计已扣税*/
	String TAXITEM_FORMULA_TOTAL_TAXEDAMT_YZ4 = "taxfunTotalTaxedAmt(f_5)";
//	/**本次扣税*/
//	String TAXITEM_FORMULA_TAXABLEAMT_YZ5 = "wa_data.@total_taxableamt_yz@ - wa_data.@total_taxedamt_yz@";
	
	/**新税改_适用税率*/
	String TAXITEM_FORMULA_TAX_RATE_YZ6 = "taxfunTaxRate(@total_taxable_income_yz@,@tax_basic_deduction_yz@)";
	/**新税改_速算扣除数*/
	String TAXITEM_FORMULA_TAX_QUICK_DEDUCTION_YZ7 = "taxfunTaxQuickDeduction(@total_taxable_income_yz@,@tax_basic_deduction_yz@)";
	/**新税改_基本扣除数*/
	String TAXITEM_FORMULA_TAX_BASIC_DEDUCTION_YZ8 = "taxfunTaxBasicDeduction()";
	/**新税改_附加专项扣除合计*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ9 = "taxfunSpecialAdditionaDeduction(0)";
	/**新税改_附加专项扣除合计*/
//	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ9_1 = "wa_data.@deu16@ - wa_data.@deu17@";
	// iif( wa_data.f_52 - wa_data.f_53  <= 0 , 0 , wa_data.f_52 - wa_data.f_53)
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ9_1 = "iif( wa_data.@deu16@ - wa_data.@deu17@  <= 0 , 0 , wa_data.@deu16@ - wa_data.@deu17@)";
	/**新税改_附加专项扣除合计-赡养老人*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ10 = "taxfunSpecialAdditionaDeduction(5)";
	/**新税改_附加专项扣除合计-子女教育*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ11 = "taxfunSpecialAdditionaDeduction(1)";
	/**新税改_附加专项扣除合计-大病医疗*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ12 = "taxfunSpecialAdditionaDeduction(6)";
	/**新税改_附加专项扣除合计-继续教育*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ13 = "taxfunSpecialAdditionaDeduction(2)";
	/**新税改_附加专项扣除合计-租房补贴*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ14 = "taxfunSpecialAdditionaDeduction(4)";
	/**新税改_附加专项扣除合计-住房补贴*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ15 = "taxfunSpecialAdditionaDeduction(3)";
	/**新税改_累计专项应扣*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ16 = "wa_data.@deu10@ + wa_data.@deu11@ + wa_data.@deu12@+ wa_data.@deu13@+ wa_data.@deu14@+ wa_data.@deu15@";
	/**新税改_累计专项已扣*/
	String TAXITEM_FORMULA_TAX_SPECIAL_DEDUCTION_YZ17 = "taxDeductionTotaled(@tax_deduction_totalable_yz@)";
	
	/**初始化公共薪资项目*/
	String OPTYPE_INITITEM = "initpubitem";
	/**初始化薪资发放项目*/
	String OPTYPE_INITCLASSITEM = "initclassitem";
	/**预制年度税率表*/
	String OPTYPE_INITTAXTABLE = "inityeartaxtable";
	/**升级薪资期间*/
	String OPTYPE_INITTAXPERIOD = "inittaxperiod";
	/**卸载*/
	String OPTYPE_UNINIT = "unInit";
	String ADDDEDUCTION_FORMULA_UPDATE = "initAddFormula";
	/**对已升级过的方案进行二次升级**/
	String OPTYPE_UPGRADE_SPECIALDEDUCTION = "updateDeduction";
	/**升级纳税组织**/
	String OPTYPE_UPGRADE_TAXORG = "updateTaxorg";
	/**升级累计应纳税所得额公式**/
	String OPTYPE_UPGRADE_TOTALTAXABLEIncome = "updateTotalTaxableIncome";
	/**升级个人所得税**/
	String OPTYPE_UPGRADE_PSNTAX = "updatepsntax";
	
	//系统本次扣税函数 - 年度 taxRate(0,wa_data.f_4,null,null)	taxRate(0,[薪资发放项目.本次扣税基数],null,null)
	String F5_FormulaStr = "@扣税函数@(3, [薪资发放项目.#累计应纳税_预制#], [薪资发放项目.#累计已扣税_预制#], null)";
	//系统本次扣税 函数 年度 
	String F5_Formula = "taxRate(3, wa_data.@total_taxableamt_yz@, wa_data.@total_taxedamt_yz@, null)";
	
	
	/**未生成*/
	int TAXTYPE_UNGEN = 0;
	/**已生成*/
	int TAXTYPE_GENED = 1;
	/**已锁定*/
	int TAXTYPE_LOCKED = 2;
	
	//---专项附加费用扣除---
//	 * String[] ml = {"0","1","2","3","4","5"};
//		String[] mlDefault = new String[]{"全部","子女教育","继续教育","住房贷款利息","住房租金","赡养老人"};
//*
	/**专项附件扣除-全部*/
	int ADDDEDUCTION_KEY_ALL = 0;
	/**专项附件扣除-子女教育*/
	int ADDDEDUCTION_KEY_CHILD = 1;
	/**专项附件扣除-继续教育*/
	int ADDDEDUCTION_KEY_EDUCATION = 2;
	/**专项附件扣除-住房贷款利息*/
	int ADDDEDUCTION_KEY_HOURSE_ZU = 3;
	/**专项附件扣除-住房租金*/
	int ADDDEDUCTION_KEY_HOURSE = 4;
	/**专项附件扣除-赡养老人*/
	int ADDDEDUCTION_KEY_PARENT = 5;
	/**专项附件扣除-大病医疗*/
	int ADDDEDUCTION_KEY_HEALTH = 6;
	
	/**专项附件扣除-子女教育*/
	String ADDDEDUCTION_KEY_CHILD_NAME = "子女教育";
	/**专项附件扣除-继续教育*/
	String ADDDEDUCTION_KEY_EDUCATION_NAME = "继续教育";
	/**专项附件扣除-住房贷款利息*/
	String ADDDEDUCTION_KEY_HOURSE_ZU_NAME = "住房贷款利息";
	/**专项附件扣除-住房租金*/
	String ADDDEDUCTION_KEY_HOURSE_NAME = "住房租金";
	/**专项附件扣除-赡养老人*/
	String ADDDEDUCTION_KEY_PARENT_NAME = "赡养老人";
	/**专项附件扣除-大病医疗*/
	String ADDDEDUCTION_KEY_HEALTH_NAME = "大病医疗";
	
	//------------export
	short SHOW_ITEM_CNT = 10;
	short ERROR_REASON_LINE = SHOW_ITEM_CNT + 1;  //错误输出行数应该为 数据行数加1
}
