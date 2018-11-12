package nc.ui.hr.func;

import java.util.HashMap;
import java.util.Map;

import nc.hr.utils.ResHelper;
import nc.ui.hr.itemsource.view.WaConvertor;

import org.apache.commons.lang.StringUtils;

public class AvgSalaryConvertor extends WaConvertor {
    private static Map<String, String> map = new HashMap<String, String>();

    public AvgSalaryConvertor() {
    }

    static {
	// 基准日期
	String[] baseml = { "salarybegindate", "salaryenddate", "sepaydate", "retirementdate", "specialdate" };
	String[] basemlDefault = new String[] { ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0286")/*
														 * @
														 * res
														 * "薪资开始期间"
														 */,
		ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0287")/*
										 * @
										 * res
										 * "薪资结束日期"
										 */,
		ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0288")/*
										 * @
										 * res
										 * "资遣日期"
										 */,
		ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0289")/*
										 * @
										 * res
										 * "退休日期"
										 */,
		ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0290") /*
										  * @
										  * res
										  * "特休日期"
										  */};/*
										       * -=
										       * notranslate
										       * =
										       * -
										       */

	for (int i = 0; i < baseml.length; i++) {
	    map.put(baseml[i], basemlDefault[i]);
	}
	// 平均天数
	String[] avgml = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
	String[] avgmlDefault = new String[] { ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0291")/*
													        * @
													        * res
													        * "1月"
													        */,
		ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0292")/*
										 * @
										 * res
										 * "2月"
										 */,
		ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0282")/*
										 * @
										 * res
										 * "3月"
										 */,
		ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0293")/*
										 * @
										 * res
										 * "4月份"
										 */,
		ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0294")/*
										 * @
										 * res
										 * "5月份"
										 */,
		ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0283")/*
										 * @
										 * res
										 * "6月份"
										 */,
		ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0295")/*
										 * @
										 * res
										 * "7月份"
										 */,
		ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0296")/*
										 * @
										 * res
										 * "8月份"
										 */,
		ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0284")/*
										 * @
										 * res
										 * "9月份"
										 */,
		ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0297")/*
										 * @
										 * res
										 * "10月份"
										 */,
		ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0298")/*
										 * @
										 * res
										 * "11月份"
										 */,
		ResHelper.getString("6013salaryctymgt", "06013salaryctymgt0285"),/*
										  * @
										  * res
										  * "12月份"
										  */};/*
										       * -=
										       * notranslate
										       * =
										       * -
										       */

	for (int i = 0; i < avgml.length; i++) {
	    map.put(avgml[i], avgmlDefault[i]);
	}

    }

    @Override
    public String postConvert(String formula) {
	// TODO Auto-generated method stub
	if (StringUtils.isNotEmpty(formula)) {

	    String param = formula.substring(formula.indexOf("(") + 1, formula.indexOf(")"));
	    String[] params = param.split(",");
	    if (null != params && params.length > 1) {
		for (int i = 0; i < params.length; i++) {
		    formula = formula
			    .replaceAll(params[i], null == map.get(params[i]) ? params[i] : map.get(params[i]));
		}
	    }

	}

	return formula;
    }
}
