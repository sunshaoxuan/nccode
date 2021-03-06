package nc.impl.wa.func;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.pub.WaLoginContext;

/**
 * #21266 按日合计加班费函数解析器
 * 
 * @author yejk
 * @date 2018-9-7
 */
@SuppressWarnings({ "serial" })
public class OvertimeFeeParse extends AbstractPreExcutorFormulaParse {

	/**
	 * @Description: 执行解析
	 * @author yejk
	 * @date 2018-9-7
	 * @param formula
	 * @param waLoginContext
	 * @throws BusinessException
	 * @return
	 */
	@Override
	public void excute(Object formula, WaLoginContext waLoginContext) throws BusinessException {
		BaseDAO basedao = new BaseDAO();
		// 薪资方案主键
		String pk_wa_class = waLoginContext.getWaLoginVO().getPk_wa_class();
		// 组织
		String pk_org = waLoginContext.getPk_org();
		// 薪资期间
		// UFLiteralDate startDate =
		// waLoginContext.getWaLoginVO().getPeriodVO().getCstartdate();
		// UFLiteralDate endDate =
		// waLoginContext.getWaLoginVO().getPeriodVO().getCenddate();
		// 薪资期间年份
		String caccyear = waLoginContext.getWaLoginVO().getPeriodVO().getCaccyear();
		// 薪资期间月份
		String cperiod = waLoginContext.getWaLoginVO().getPeriodVO().getCperiod();

		/* 通过薪资期间获取考勤期间的起止日期 start */
		String queryDateSql = "select tbm_period.begindate,tbm_period.enddate from tbm_period where tbm_period.accyear = ?  and tbm_period.accmonth  = ? and tbm_period.pk_org = ?";
		SQLParameter params = new SQLParameter();
		params.addParam(caccyear);
		params.addParam(cperiod);
		params.addParam(pk_org);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> dateListMap = (List<Map<String, Object>>) basedao.executeQuery(queryDateSql, params,
				new MapListProcessor());
		if (null == dateListMap) {
			throw new BusinessException("通过薪资期间获取考勤期间起止日期为空");
		}
		UFLiteralDate startDate = new UFLiteralDate(dateListMap.get(0).get("begindate").toString());
		UFLiteralDate endDate = new UFLiteralDate(dateListMap.get(0).get("enddate").toString());
		/* 通过薪资期间获取考勤期间的起止日期 end */

		// 分离参数
		String[] arguments = getArguments(formula.toString());
		Pattern p = Pattern.compile("\\d");

		Matcher m = p.matcher(String.valueOf(arguments[0]).replaceAll("\'", ""));
		int intComp = 0;
		if (m.find()) {
			intComp = Integer.valueOf(m.group());
		}

		m = p.matcher(String.valueOf(arguments[1]).replaceAll("\'", ""));
		// 是否免税 0否 1是
		int isTaxFreeFlag = 0;
		if (m.find()) {
			isTaxFreeFlag = Integer.valueOf(m.group());
		}

		// 薪资项目分组
		String pk_group_item = String.valueOf(arguments[2]).replaceAll("\'", "");

		/* 获取计算人员集合 start */
		String psndocsSql = "select wa_cacu_data.pk_psndoc from wa_cacu_data where wa_cacu_data.pk_wa_class = '"
				+ pk_wa_class + "'";

		List<String> psndocList = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>) basedao.executeQuery(psndocsSql,
				new MapListProcessor());
		if (null == result) {
			throw new BusinessException("应税(免税)加班费计算-获取人员pk为空");
		}
		for (int i = 0; i < result.size(); i++) {
			Map<String, Object> map = result.get(i);
			String pk_psndoc1 = map.get("pk_psndoc").toString();
			psndocList.add(pk_psndoc1);
		}
		String[] psndocArr = psndocList.toArray(new String[0]);
		/* 获取计算人员集合 end */

		int rows = (int) basedao.executeQuery("select case when (sum("
				+ ("null".equals(pk_group_item) ? "taxfreehours+taxablehours" : "amounttaxfree+amounttaxable")
				+ ")) > 0 then 1 else 0 end cnt from wa_cacu_overtimefee where pk_wa_class='" + pk_wa_class
				+ "' and creator='" + this.getContext().getPk_loginUser() + "' and intComp=" + String.valueOf(intComp),
				new ColumnProcessor());
		if (rows == 0) {
			// 调用接口返回应税或免税加班费
			ISegDetailService segDetailService = NCLocator.getInstance().lookup(ISegDetailService.class);
			// 计算加班费,传入分组入口
			/*
			 * Map<String, UFDouble[]> ovtFeeResult =
			 * segDetailService.calculateTaxableByDate(pk_org, psndocArr,
			 * startDate, endDate, null, null,pk_group_item);
			 */
			// ssx modified for Multi-Thread Calculation
			Map<String, UFDouble[]> ovtFeeResult = segDetailService.calculateOvertimeFeeByDate(pk_org, psndocArr,
					startDate, endDate, null, null, (pk_group_item.equals("null") ? null : pk_group_item), false);
			// end
			if (null == ovtFeeResult || ovtFeeResult.size() == 0) {
				throw new BusinessException("调用接口ISegDetailService获取应税(免税)加班费为空");
			} else {
				writeToWaOTTempData(pk_wa_class, this.getContext().getPk_loginUser(), ovtFeeResult, false);
			}
		}
	}

	@Override
	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {

		excute(formula, getContext());

		FunctionReplaceVO fvo = new FunctionReplaceVO();
		// 分离参数
		String[] arguments = getArguments(formula.toString());
		Pattern p = Pattern.compile("\\d");

		Matcher m = p.matcher(String.valueOf(arguments[0]).replaceAll("\'", ""));
		int intComp = 0;
		if (m.find()) {
			intComp = Integer.valueOf(m.group());
		}

		m = p.matcher(String.valueOf(arguments[1]).replaceAll("\'", ""));
		// 是否免税 0否 1是
		int isTaxFreeFlag = 0;
		if (m.find()) {
			isTaxFreeFlag = Integer.valueOf(m.group());
		}

		// 薪资项目分组
		String pk_group_item = String.valueOf(arguments[2]).replaceAll("\'", "");

		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr("coalesce("
				+ "(select "
				+ ((pk_group_item == null || pk_group_item.equals("null")) ? (isTaxFreeFlag == 0 ? "taxablehours"
						: "taxfreehours") : (isTaxFreeFlag == 0 ? "amounttaxable" : "amounttaxfree"))
				+ " from wa_cacu_overtimefee where pk_wa_class=wa_cacu_data.pk_wa_class and creator='"
				+ this.getContext().getPk_loginUser() + "' and intcomp=" + String.valueOf(intComp)
				+ " and pk_psndoc=wa_cacu_data.pk_psndoc)" + ", 0)");
		return fvo;
	}
}
