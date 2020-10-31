package nc.impl.wa.paydata;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.wa.log.WaBusilogUtil;
import nc.impl.wa.end.MonthEndDAO;
import nc.impl.wa.pub.WapubDAO;
import nc.itf.hr.frame.IHRDataPermissionPubService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.wa.IAmoSchemeQuery;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.IPayfileManageService;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.jdbc.framework.JdbcPersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.jdbc.framework.type.SQLParamType;
import nc.jdbc.framework.type.SQLTypeFactory;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.vo.dataitem.pub.DataVOUtils;
import nc.vo.hi.psndoc.Attribute;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.append.AppendableVO;
import nc.vo.hr.combinesort.SortVO;
import nc.vo.hr.combinesort.SortconVO;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.hr.pub.FormatVO;
import nc.vo.hr.tools.dbtool.util.db.DBUtil;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.hr.tools.pub.Pair;
import nc.vo.hrp.budgetitem.BudgetItemVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.BDVersionValidationUtil;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.classitempower.ItemPowerUtil;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.paydata.AggPayDataVO;
import nc.vo.wa.paydata.DataSVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.paydata.ICommonAlterName;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.SalaryPmtCommonValue;
import nc.vo.wa.pub.WaClassStateHelper;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaState;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class PaydataDAO extends AppendBaseDAO implements ICommonAlterName {

	/**
	 * 根据用户信息获得该用户有权限的该期间的薪资项目
	 * 
	 * @author zhangg on 2009-11-26
	 * @param loginVO
	 * @return
	 * @throws BusinessException
	 */
	public WaClassItemVO[] getUserClassItemVOs(WaLoginContext loginVO) throws BusinessException {
		WaClassItemVO[] classitems = testgetRoleClassItemVOs(loginVO.getWaLoginVO(), null);

		return classitems;
	}

	public static String getLeavePsndocSQL(String pk_org, String pk_wa_class, String cyear, String cperiod,
			UFLiteralDate leaveScopeBeginDate, UFLiteralDate leaveScopeEndDate, String creator) {
		String strSQL = "select DISTINCT wa_cacu_data.pk_psndoc from wa_cacu_data "
				+ " inner join hi_psnjob on wa_cacu_data.pk_psndoc = hi_psnjob.pk_psndoc AND hi_psnjob.begindate = (SELECT CASE WHEN MAX(pj.begindate) BETWEEN '"
				+ leaveScopeBeginDate.toString()
				+ "' AND '"
				+ leaveScopeEndDate.toString()
				+ "'  THEN MAX(pj.begindate)  ELSE NULL END begindate FROM hi_psnjob pj WHERE  pj.pk_psndoc = hi_psnjob.pk_psndoc "
				+ " AND (trnsevent = 4 or trnstype = '1001X110000000003O5G')  AND pj.begindate <= "
				+ " (SELECT (substr(to_char((to_date(tbm_period.enddate, 'yyyy-MM-dd') + 15), 'yyyy-MM-dd'),0,8) || '01') FROM tbm_period "
				+ " inner join wa_period on tbm_period.accyear = wa_period.caccyear and tbm_period.accmonth = wa_period.caccperiod"
				+ " INNER JOIN wa_waclass ON wa_waclass.pk_periodscheme = wa_period.pk_periodscheme "
				+ " WHERE tbm_period.pk_org = '"
				+ pk_org
				+ "' and wa_waclass.pk_wa_class = '"
				+ pk_wa_class
				+ "' AND wa_period.caccyear = '"
				+ cyear
				+ "' AND wa_period.caccperiod = '"
				+ cperiod
				+ "') "
				+ " AND isnull(pj.enddate, '9999-12-31') >= (SELECT tbm_period.begindate FROM tbm_period "
				+ " inner join wa_period on tbm_period.accyear = wa_period.caccyear and tbm_period.accmonth = wa_period.caccperiod"
				+ " INNER JOIN wa_waclass ON wa_waclass.pk_periodscheme = wa_period.pk_periodscheme WHERE tbm_period.pk_org = '"
				+ pk_org
				+ "' and wa_waclass.pk_wa_class = '"
				+ pk_wa_class
				+ "' "
				+ " AND wa_period.caccyear = '"
				+ cyear
				+ "' AND wa_period.caccperiod = '"
				+ cperiod
				+ "')) where wa_cacu_data.pk_wa_class= '"
				+ pk_wa_class + "' and wa_cacu_data.creator = '" + creator + "'";

		return strSQL;
	}

	public static String getLeaveHoursSQLByPeriodCondtions(String pk_leaveitem, Object[] periodDates,
			String psndocCondition) throws BusinessException {
		UFLiteralDate sumStartDate = new UFLiteralDate((String) periodDates[0]);
		UFLiteralDate sumEndDate = new UFLiteralDate((String) periodDates[1]);
		String sql = "select tbm_leavereg.pk_psndoc psndoc,effectivedate begindate , "
				// ssx added on 2019-11-27
				// 考慮未來期間已銷假，本期間時數被沖抵，下面還要取出本期銷假單用於抵銷本期假單
				+ " sum(case when isleaveoff='Y' then (select sum(regleavehourcopy) from tbm_leaveoff where pk_leavereg = tbm_leavereg.pk_leavereg) else leavehour end) leavehour, "
				// end
				// ssx added on 2020-05-11
				// 扣除掉之前月份已審批通過的過去時間的請假單之銷假時數
				+ " sum(case when isleaveoff='Y' then (select sum(regleavehourcopy) from tbm_leaveoff where pk_leavereg = tbm_leavereg.pk_leavereg) else leavehour end) deduct_last_leaveoff "
				// end
				+ " from tbm_leavereg inner join hi_psnjob on hi_psnjob.pk_psnjob = tbm_leavereg.pk_psnjob where pk_leavetype = '"
				+ pk_leaveitem
				// 派遣人員不參與時數計算
				+ "'  AND hi_psnjob.jobglbdef8 <> (select pk_defdoc from bd_defdoc where pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'HR006_0xx') and code = 'C') and ("
				// (( 歸屬日 BETWEEN 期間起 AND 期間迄 AND 核准日 <= 本期自然月最後一天)
				// OR (歸屬日 < 期間起 AND 核准日 BETWEEN 本期自然月第一天 AND 本期自然月最後一天))
				+ "(( effectivedate BETWEEN '" + sumStartDate + "' AND '" + sumEndDate + "' AND approve_time <= '"
				+ sumEndDate.getDateAfter(sumEndDate.getDaysMonth() - sumEndDate.getDay()) + " 23:59:59')"
				+ "OR (effectivedate < '" + sumStartDate + "' AND approve_time BETWEEN '"
				+ sumStartDate.getDateAfter(sumStartDate.getDaysMonth() - sumStartDate.getDay() + 1)
				+ " 00:00:00' AND '" + sumEndDate.getDateAfter(sumEndDate.getDaysMonth() - sumEndDate.getDay())
				+ " 23:59:59'))" + ") and tbm_leavereg.pk_psndoc in (" + psndocCondition
				+ ") group by tbm_leavereg.pk_psndoc, effectivedate";
		sql += " union all ";
		sql += "select tbm_leaveoff.pk_psndoc psndoc, tbm_leavereg.effectivedate begindate, sum(tbm_leaveoff.differencehour) leavehour, "
				// ssx added on 2020-05-11
				// 扣除掉之前月份已審批通過的過去時間的請假單之銷假時數
				+ "sum(case when tbm_leavereg.effectivedate < '"
				+ sumStartDate
				+ "' and tbm_leavereg.approve_time < '"
				+ sumStartDate.getDateAfter(sumStartDate.getDaysMonth() - sumStartDate.getDay() + 1)
				+ " 00:00:00' then 0 else tbm_leaveoff.differencehour end) deduct_last_leaveoff "
				// end
				+ " from tbm_leaveoff "
				+ " inner join tbm_leavereg on tbm_leavereg.pk_leavereg = tbm_leaveoff.pk_leavereg "
				+ " inner join hi_psnjob on hi_psnjob.pk_psnjob = tbm_leavereg.pk_psnjob "
				+ " where "
				+ "         tbm_leavereg.pk_leavetype = '"
				+ pk_leaveitem
				// 派遣人員不參與時數計算
				+ "'  AND hi_psnjob.jobglbdef8 <> (select pk_defdoc from bd_defdoc where pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'HR006_0xx') and code = 'C')"
				+ "        AND tbm_leaveoff.pk_psndoc IN ( "
				+ psndocCondition
				+ ")"
				+ " AND (( tbm_leavereg.effectivedate BETWEEN '"
				+ sumStartDate
				+ "' AND '"
				+ sumEndDate
				+ "' AND tbm_leaveoff.approve_time <= '"
				+ sumEndDate.getDateAfter(sumEndDate.getDaysMonth() - sumEndDate.getDay())
				+ " 23:59:59') OR (tbm_leavereg.effectivedate < '"
				+ sumStartDate
				+ "' AND tbm_leaveoff.approve_time BETWEEN '"
				+ sumStartDate.getDateAfter(sumStartDate.getDaysMonth() - sumStartDate.getDay() + 1)
				+ " 00:00:00' AND '"
				+ sumEndDate.getDateAfter(sumEndDate.getDaysMonth() - sumEndDate.getDay())
				+ " 23:59:59'))"
				//
				+ " group by tbm_leaveoff.pk_psndoc, tbm_leavereg.effectivedate";
		return sql;
	}

	/**
	 * 查询用户设置显示的项目
	 * 
	 * wa_classitemdsp 该表已经不用了
	 * 
	 * @author liangxr on 2010-7-2
	 * @param loginVO
	 * @return
	 * @throws BusinessException
	 */
	public WaClassItemVO[] getUserShowClassItemVOs(WaLoginContext loginVO) throws BusinessException {
		String condition = " wa_classitem.pk_wa_classitem not in (select pk_wa_classitem from wa_classitemdsp "
				+ "where pk_wa_class = '" + loginVO.getPk_wa_class() + "' and cyear = '" + loginVO.getWaYear()
				+ "' and cperiod = '" + loginVO.getWaPeriod() + "' and pk_user = '" + PubEnv.getPk_user()
				+ "' and bshow = 'N' )";
		WaClassItemVO[] classitems = testgetRoleClassItemVOs(loginVO.getWaLoginVO(), condition);
		return classitems;
	}

	public WaClassItemVO[] getRepayUserShowClassItemVOs(WaLoginContext waLoginContext) throws BusinessException {

		WaLoginVO waLoginVO = waLoginContext.getWaLoginVO();
		String condition = "";
		if ((!StringUtils.isEmpty(waLoginVO.getReperiod())) && (!waLoginVO.getReperiod().equals("-1"))) {
			condition = " wa_classitem.pk_wa_item in (select pk_wa_item from wa_classitem where pk_wa_class = '"
					+ waLoginVO.getPk_prnt_class() + "' and cyear = '" + waLoginVO.getReyear() + "' and cperiod = '"
					+ waLoginVO.getReperiod() + "' ) ";
		}

		WaClassItemVO[] classitems = testgetRoleClassItemVOs(waLoginVO, condition);
		return classitems;
	}

	/**
	 * 查询纳入审批的薪资项目
	 * 
	 * @author liangxr on 2010-7-2
	 * @param loginVO
	 * @return
	 * @throws BusinessException
	 */
	public WaClassItemVO[] getApprovedClassItemVOs(WaLoginContext loginVO) throws BusinessException {
		String condition = " wa_classitem.inapproveditem = 'Y'";
		WaClassItemVO[] classitems = null;
		if ("60130payslipaly".equals(loginVO.getNodeCode())) {
			classitems = testgetRoleClassItemVOs(loginVO.getWaLoginVO(), condition);
		} else {
			classitems = getRoleClassItemVOsNoPower(loginVO.getWaLoginVO(), condition);
		}
		return classitems;
	}

	/**
	 * 判断一个方案（无论是单次还是多次，还是多次中的一次）是否已经发放过（一次）
	 * 
	 * @param pk_wa_data
	 * @return
	 * @throws BusinessException
	 */
	public boolean isAnyTimesPayed(String pk_wa_class, String cyear, String cperiod) throws BusinessException {
		StringBuilder sbd = new StringBuilder();
		sbd.append("  select top 1 wa_periodstate.pk_wa_period from wa_periodstate,wa_period ");
		sbd.append("  	where wa_periodstate.pk_wa_period = wa_period.pk_wa_period  ");
		sbd.append("  	 and wa_period.cyear = ?  ");
		sbd.append("  	and wa_period.cperiod=? and wa_periodstate.payoffflag='Y' ");
		sbd.append("  and ( wa_periodstate.pk_wa_class  in ( ");
		sbd.append("  select pk_childclass  from wa_inludeclass where pk_parentclass = ? and wa_inludeclass.cyear = ? and wa_inludeclass.cperiod = ?  ");
		sbd.append("   ) or  wa_periodstate.pk_wa_class  = ? ) ");

		SQLParameter para = new SQLParameter();
		para.addParam(cyear);
		para.addParam(cperiod);
		para.addParam(pk_wa_class);
		para.addParam(cyear);
		para.addParam(cperiod);
		para.addParam(pk_wa_class);

		return isValueExist(sbd.toString(), para);
	}

	/**
	 * 薪资审核
	 * 
	 * @author zhangg on 2009-12-2
	 * @param waLoginVO
	 * @param whereCondition
	 * @param isRangeAll
	 * @throws nc.vo.pub.BusinessException
	 */
	public boolean onCheck(WaLoginVO waLoginVO, String whereCondition, Boolean isRangeAll)
			throws nc.vo.pub.BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("   wa_data.checkflag = 'N' "); // 3
		sqlBuffer.append("   and wa_data.stopflag = 'N' and wa_data.caculateflag='Y' "); // 5
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(getCommonWhereCondtion4Data(waLoginVO)));

		if (!isRangeAll) {
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(whereCondition));
		}
		// 先设置wa_data的审核标志
		updateTableByColKey("wa_data", PayfileVO.CHECKFLAG, UFBoolean.TRUE, sqlBuffer.toString());

		// 查看薪资数据是否全部审核
		sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_data.pk_wa_data "); // 1
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" where wa_data.checkflag = 'N' ");
		sqlBuffer.append("   and wa_data.stopflag = 'N' ");
		// 数据权限guoqt数据使用权
		// sqlBuffer.append(WherePartUtil.formatAddtionalWhere(getCommonWhereCondtion4Data(waLoginVO)));
		sqlBuffer.append("   and wa_data.pk_wa_class ='" + waLoginVO.getPk_wa_class() + "' ");
		sqlBuffer.append("   and wa_data.cyear ='" + waLoginVO.getCyear() + "' ");
		sqlBuffer.append("   and wa_data.cperiod ='" + waLoginVO.getCperiod() + "' ");

		boolean isAllChecked = !isValueExist(sqlBuffer.toString());
		if (isAllChecked) {
			// 设置期间的审核标志
			updatePeriodState(PeriodStateVO.CHECKFLAG, UFBoolean.TRUE, waLoginVO);
			return true;
		}
		return false;
	}

	public void onUnCheck(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll)
			throws nc.vo.pub.BusinessException {
		// 审批流审批的类别，如果在审批中的单据则不能取消，//如不需要复审且已经增加到审批单据中的，则不能取消审核
		PeriodStateVO period = waLoginVO.getPeriodVO();
		if (period != null) {
			if (period.getIsapproved().booleanValue()) {
				throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0447")/*
																									 * @
																									 * res
																									 * "该薪资方案已经审批通过，无法取消审核！"
																									 */);// "该薪资类别已经审批通过，无法取消审核！"

			}
			Boolean isInApproveing = new WapubDAO().isInApproveing(waLoginVO);
			if (isInApproveing) {
				throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0448")/*
																									 * @
																									 * res
																									 * "该薪资方案已经加到审批单据中，无法取消审核！"
																									 */);// "该薪资类别已经加到审批单据中，无法取消审核！"
			}
		}
		// 同一个会计期间下方案是否引用, 校验合并计税
		String caccyear = waLoginVO.getPeriodVO().getCaccyear();
		String caccperiod = waLoginVO.getPeriodVO().getCaccperiod();
		// String sqlWhereAccPeriod = " cpreclassid = '" +
		// waLoginVO.getPk_wa_class()
		// + "' and checkflag = 'Y'";
		// sqlWhereAccPeriod += " and caccyear='" + caccyear +
		// "' and caccperiod='" + caccperiod + "'";
		// StringBuffer sqlBuffer = new
		// StringBuffer(WapubDAO.getPeriodViewTable());
		// sqlBuffer.append(WherePartUtil.addWhereKeyWord2Condition(sqlWhereAccPeriod));
		// if (isValueExist(sqlBuffer.toString())) {
		// throw new nc.vo.pub.BusinessException(MessageFormat.format(
		// ResHelper.getString("60130paydata","060130paydata0469")
		// /*@res "{0}引用本方案的数据进行了计算，并审核，本方案不能取消审核！"*/,
		// getRelateClassName(waLoginVO)));
		// }
		checkTaxRelate(waLoginVO, whereCondition, isRangeAll);

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("   wa_data.stopflag = 'N' "); // 5
		sqlBuffer.append("   and wa_data.checkflag = 'Y' "); // 6
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(getCommonWhereCondtion4Data(waLoginVO)));
		if (!isRangeAll) {
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(whereCondition));
		}
		dealInPayLeavePsn(waLoginVO, whereCondition, isRangeAll);

		// 先设置wa_data的prewadata
		updateTableByColKey("wa_data", "prewadata", SQLTypeFactory.getNullType(Types.CHAR), sqlBuffer.toString());

		// 再设置wa_data的审核标志
		updateTableByColKey("wa_data", PayfileVO.CHECKFLAG, UFBoolean.FALSE, sqlBuffer.toString());

		// 先设置期间的审核标志
		updatePeriodState(PeriodStateVO.CHECKFLAG, UFBoolean.FALSE, waLoginVO);

		// 设置合并计税引用方案的计算状态
		String sqlWhereAccPeriod = " cpreclassid = '" + waLoginVO.getPk_wa_class() + "'";
		sqlWhereAccPeriod += " and pk_wa_period in(select pk_wa_period from wa_period where caccyear='" + caccyear
				+ "' and caccperiod='" + caccperiod + "')";
		updateTableByColKey("wa_periodstate", "caculateflag", UFBoolean.FALSE,
				WherePartUtil.addWhereKeyWord2Condition(sqlWhereAccPeriod));

		// 取消审核时，wa_data的 prewadata 清空
	}

	private void checkTaxRelate(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll)
			throws BusinessException {

		String addSql = getCommonWhereCondtion4Data(waLoginVO);
		if (!isRangeAll && whereCondition != null && whereCondition.length() > 0) {
			addSql += " and wa_data.pk_wa_data in(select pk_wa_data from wa_data where " + whereCondition + ")";
		}

		String sql = "select "
				+ SQLHelper.getMultiLangNameColumn("b1.name")
				+ " as psnname,"
				+ SQLHelper.getMultiLangNameColumn("w1.name")
				+ " as classname,w2.batch "
				+ "from  wa_data d2 "
				+ "INNER JOIN wa_waclass w1 ON d2.pk_wa_class = w1.pk_wa_class "
				+ "LEFT OUTER JOIN wa_inludeclass w2 	ON w2.pk_childclass = d2.pk_wa_class AND w2.cyear = d2.cyear AND w2.cperiod = d2.cperiod  "
				+ "INNER JOIN bd_psndoc b1  ON b1.pk_psndoc = d2.pk_psndoc WHERE "
				+ " d2.prewadata in(select pk_wa_data from wa_data where " + addSql + ") AND d2.checkflag = 'Y'  ";

		List result = (List) getBaseDao().executeQuery(sql, new MapListProcessor());
		if (result == null || result.size() == 0) {
			return;
		}
		Map map = null;
		Integer batch = 0;
		StringBuffer msg = new StringBuffer();
		// 2015-10-26 zhousze 薪资发放合并计税方案取消审核提示错误 begin
		msg.append(ResHelper.getString("60130paydata", "060130paydata0473"))/*
																			 * @res
																			 * "以下人员的数据已被引用，无法取消审核：\r\n"
																			 */;
		// end
		for (int i = 0; i < result.size(); i++) {
			map = (Map) result.get(i);
			batch = ((Integer) map.get("batch"));
			msg.append("[" + map.get("psnname"));
			msg.append(":" + map.get("classname"));
			if (batch == null || batch == 0) {
				// do nothing
			} else if (batch > 100) {
				msg.append(ResHelper.getString("60130paydata", "060130paydata0470"))/*
																					 * @
																					 * res
																					 * "-离职发薪"
																					 */;
			} else {
				msg.append(MessageFormat.format(ResHelper.getString("60130paydata", "060130paydata0471")/*
																										 * @
																										 * res
																										 * "{0}次发薪"
																										 */,
						nc.vo.format.FormatGenerator.getIndexFormat().format(batch)));
			}
			msg.append("]");
		}
		throw new BusinessException(msg.toString());
	}

	/**
	 * 删除离职发薪中的人员
	 * 
	 * @param waLoginVO
	 * @param whereCondition
	 * @param isRangeAll
	 * @throws BusinessException
	 */
	private void dealInPayLeavePsn(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll)
			throws BusinessException {
		String sql = "select * FROM wa_data " + "WHERE wa_data.pk_wa_class IN( SELECT w2.pk_childclass "
				+ "								   FROM wa_inludeclass w2 " + "								  WHERE w2.pk_parentclass = ? "
				+ "									AND w2.cyear = ? " + "									AND w2.cperiod = ? )" + "	AND wa_data.cyear = ? "
				+ "	AND wa_data.cperiod =? " + "	AND wa_data.checkflag = 'N' "
				+ "	AND wa_data.pk_psndoc IN( SELECT pk_psndoc " + "								FROM wa_data w1 "
				+ "							   WHERE w1.pk_wa_class = ? " + "								 AND w1.cyear = ? " + "								 AND w1.cperiod = ? "
				+ "								 AND w1.checkflag = 'Y')";
		if (!isRangeAll) {
			sql += WherePartUtil.formatAddtionalWhere(whereCondition);
		}
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_prnt_class());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());
		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());
		List<PayfileVO> delVos = (List<PayfileVO>) getBaseDao().executeQuery(sql, parameter,
				new BeanListProcessor(PayfileVO.class));

		NCLocator.getInstance().lookup(IPayfileManageService.class).delPsnVOs(delVos.toArray(new PayfileVO[0]));

	}

	/**
	 * 取消审核时校验离职发薪
	 * 
	 * @param waLoginVO
	 * @param whereCondition
	 * @param isRangeAll
	 * @return
	 * @throws DAOException
	 */
	public PayfileVO[] getInPayLeavePsn(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll)
			throws DAOException {
		String sql = "select bd_psndoc.code as psncode," + SQLHelper.getMultiLangNameColumn("bd_psndoc.name")
				+ " as psnname " + "from wa_data ,bd_psndoc " + "where wa_data.pk_psndoc = bd_psndoc.pk_psndoc "
				+ " and wa_data.pk_wa_class = ? " + "	and wa_data.cyear = ? " + "	and wa_data.cperiod = ? "
				+ " and wa_data.checkflag = 'Y' " + "	and wa_data.pk_psndoc in(select w1.pk_psndoc "
				+ "						from wa_data w1 ,wa_inludeclass w2 " + "						where w1.pk_wa_class = w2.pk_childclass "
				+ "							and w1.cyear = w2.cyear " + "							and w1.cperiod = w2.cperiod "
				+ "							and w2.batch >100 " + "							and w1.checkflag = 'N' " + "							and w2.pk_parentclass = ? "
				+ "							and w1.cyear = ? " + "							and w1.cperiod = ? " + "	)";
		if (!isRangeAll && whereCondition != null && whereCondition.length() > 0) {
			sql += " and wa_data.pk_wa_data in(select pk_wa_data from wa_data where " + whereCondition + ")";
		}
		SQLParameter parameter = getCommonParameter(waLoginVO);
		parameter.addParam(waLoginVO.getPk_prnt_class());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());
		PayfileVO[] vos = executeQueryVOs(sql, parameter, PayfileVO.class);
		return vos;
	}

	/**
	 * 取得依赖方案名称
	 * 
	 * @param waLoginVO
	 * @return
	 * @throws DAOException
	 */
	public String getRelateClassName(WaLoginVO waLoginVO) throws DAOException {
		String sql = "select w1.name,w2.batch from wa_periodstate p1,wa_waclass w1,wa_inludeclass w2 "
				+ "where  p1.pk_wa_class = w1.pk_wa_class " + "and w1.pk_wa_class = w2.pk_childclass "
				+ "and p1.cpreclassid = ? " + "and w2.cyear = ? " + "and w2.cperiod = ? ";
		SQLParameter parameter = getCommonParameter(waLoginVO);
		Map map = (Map) getBaseDao().executeQuery(sql, parameter, new MapProcessor());
		String name = map.get("name").toString();
		int batch = Integer.parseInt(map.get("batch").toString());
		if (batch > 100) {
			return name + ResHelper.getString("60130paydata", "060130paydata0470")
			/* @res "-离职发薪" */;
		}

		return name + MessageFormat.format(ResHelper.getString("60130paydata", "060130paydata0471")
		/* @res "{0}次发薪" */, nc.vo.format.FormatGenerator.getIndexFormat().format(batch));

	}

	/**
	 * 审批流单据是否处于提交态
	 * 
	 * @author liangxr on 2010-6-1
	 * @param waLoginVO
	 * @return
	 * @throws DAOException
	 */
	public boolean isPayrollSubmit(WaLoginVO waLoginVO) throws DAOException {
		String classid = waLoginVO.getPk_wa_class();
		String cyear = waLoginVO.getCyear();
		String cperiod = waLoginVO.getCperiod();

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select 1 from wa_periodstate ");
		sqlBuffer.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
		sqlBuffer.append("  wa_period.pk_wa_period and wa_periodstate.enableflag = 'Y') ");
		sqlBuffer.append("where wa_periodstate.isapproved != 'Y' ");
		sqlBuffer.append("and exists (select 1 from wa_payroll  ");
		sqlBuffer.append("where wa_payroll.pk_wa_class = wa_periodstate.pk_wa_class ");
		sqlBuffer.append(" and wa_payroll.billstate='").append(IPfRetCheckInfo.COMMIT);
		sqlBuffer.append("' and wa_payroll.pk_wa_class = '").append(classid);
		sqlBuffer.append("' and wa_payroll.cyear = '").append(cyear);
		sqlBuffer.append("' and wa_payroll.cperiod = '").append(cperiod);
		sqlBuffer.append("') and wa_periodstate.pk_wa_class = '").append(classid);
		sqlBuffer.append("' and wa_period.cyear = '").append(cyear);
		sqlBuffer.append("' and wa_period.cperiod = '").append(cperiod).append("' ");

		return isValueExist(sqlBuffer.toString());
	}

	/**
	 * 判断审批流单据是否处于自由态
	 * 
	 * @author liangxr on 2010-6-1
	 * @param waLoginVO
	 * @return
	 * @throws DAOException
	 */
	public boolean isPayrollFree(WaLoginVO waLoginVO) throws DAOException {
		String classid = waLoginVO.getPk_wa_class();
		String cyear = waLoginVO.getCyear();
		String cperiod = waLoginVO.getCperiod();

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select 1 from wa_periodstate ");
		sqlBuffer.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
		sqlBuffer.append(" wa_period.pk_wa_period and wa_periodstate.enableflag = 'Y') ");
		sqlBuffer.append("where wa_periodstate.isapproved != 'Y' ");
		sqlBuffer.append("and exists (select 1 from wa_payroll  ");
		sqlBuffer.append("where wa_payroll.pk_wa_class = wa_periodstate.pk_wa_class ");
		sqlBuffer.append(" and wa_payroll.billstate='").append(IPfRetCheckInfo.NOSTATE);
		sqlBuffer.append("' and wa_payroll.pk_wa_class = '").append(classid);
		sqlBuffer.append("' and wa_payroll.cyear = '").append(cyear);
		sqlBuffer.append("' and wa_payroll.cperiod = '").append(cperiod);
		sqlBuffer.append("') and wa_periodstate.pk_wa_class = '").append(classid);
		sqlBuffer.append("' and wa_period.cyear = '").append(cyear);
		sqlBuffer.append("' and wa_period.cperiod = '").append(cperiod).append("' ");

		return isValueExist(sqlBuffer.toString());
	}

	/**
	 * 获取薪资发放项目
	 * 
	 * @author liangxr on 2010-5-13
	 * @param waLoginVO
	 * @return
	 * @throws BusinessException
	 */
	private WaClassItemVO[] testgetRoleClassItemVOs(WaLoginVO waLoginVO, String condition) throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select wa_item.itemkey, wa_item.iitemtype,wa_item.defaultflag, ");
		sqlBuffer.append(" wa_item.ifldwidth,wa_item.category_id, ");
		sqlBuffer.append(" wa_classitem.*, 'Y' editflag,");
		sqlBuffer.append(" 'Y' as showflag,");
		sqlBuffer.append(" wa_classitem.idisplayseq as idisplayseq,  ");
		sqlBuffer.append("itempower.editflag ");
		sqlBuffer.append("from wa_classitem , wa_item,");
		sqlBuffer.append("(SELECT pk_wa_item,MAX(editflag) as editflag");
		sqlBuffer.append("   FROM wa_itempower ");
		// 判断是否是多次发放
		// WaClassVO parentvo = NCLocator.getInstance().lookup(IWaClass.class)
		// .queryParentClass(waLoginVO.getPk_wa_class(),
		// waLoginVO.getCyear(), waLoginVO.getCperiod());

		sqlBuffer.append("  WHERE pk_wa_class = '" + waLoginVO.getPk_prnt_class() + "'");
		sqlBuffer.append("    AND pk_group ='" + waLoginVO.getPk_group() + "'");

		// 20160104 shenliangc NCdp205568081 已审批通过的发放申请通知消息，打开单据后，单据子表的审批项目丢失
		// 历史遗留问题，通知消息双击打开发放申请节点，查询审批项目时waLoginVO.getPk_org()得到的是系统默认主组织，而不是正确的方案主组织。
		sqlBuffer.append("    AND pk_org = (select pk_org from wa_waclass where pk_wa_class = '"
				+ waLoginVO.getPk_prnt_class() + "')");
		// sqlBuffer.append("    AND pk_org = '"+waLoginVO.getPk_org()+"'");

		sqlBuffer.append("    AND ( pk_subject IN(SELECT pk_role ");
		sqlBuffer.append("				       FROM sm_user_role ");
		sqlBuffer.append("				      WHERE cuserid = '" + PubEnv.getPk_user() + "'");
		sqlBuffer.append("                   ) or pk_subject = '" + PubEnv.getPk_user() + "') ");
		sqlBuffer.append("  GROUP BY pk_wa_item ) as itempower");
		sqlBuffer.append(" where wa_classitem.pk_wa_item = wa_item.pk_wa_item ");
		sqlBuffer.append(" and wa_classitem.pk_wa_item = itempower.pk_wa_item ");
		sqlBuffer.append(" and wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append(" and wa_classitem.cyear = ?  and wa_classitem.cperiod = ? ");
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition));
		sqlBuffer.append(" order by wa_classitem.idisplayseq");

		SQLParameter parameter = getCommonParameter(waLoginVO);
		return executeQueryVOs(sqlBuffer.toString(), parameter, WaClassItemVO.class);
	}

	/**
	 * 获取薪资发放项目
	 * 
	 * @author liangxr on 2010-5-13
	 * @param waLoginVO
	 * @return
	 * @throws BusinessException
	 */
	private WaClassItemVO[] getRoleClassItemVOsNoPower(WaLoginVO waLoginVO, String condition) throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select wa_item.itemkey, wa_item.iitemtype,wa_item.defaultflag, ");
		sqlBuffer.append(" wa_item.ifldwidth,wa_item.category_id, ");
		sqlBuffer.append(" wa_classitem.*, 'Y' editflag,");
		sqlBuffer.append("isnull(wa_classitemdsp.bshow,'Y') as showflag,");
		sqlBuffer.append("isnull(wa_classitemdsp.displayseq,wa_classitem.idisplayseq) as idisplayseq ");
		sqlBuffer.append("from wa_classitem LEFT OUTER JOIN wa_classitemdsp  ");
		sqlBuffer.append(" ON wa_classitem.pk_wa_class = wa_classitemdsp.pk_wa_class  ");
		sqlBuffer.append(" AND wa_classitem.cyear = wa_classitemdsp.cyear  ");
		sqlBuffer.append(" AND wa_classitem.cperiod = wa_classitemdsp.cperiod  ");
		sqlBuffer
				.append(" AND wa_classitem.pk_wa_classitem = wa_classitemdsp.pk_wa_classitem and wa_classitemdsp.pk_user = '"
						+ PubEnv.getPk_user() + "' , wa_item ");
		sqlBuffer.append(" where wa_classitem.pk_wa_item = wa_item.pk_wa_item ");
		sqlBuffer.append(" and wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append(" and wa_classitem.cyear = ?  and wa_classitem.cperiod = ? ");
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition));
		sqlBuffer.append(" order by wa_classitem.idisplayseq");

		SQLParameter parameter = getCommonParameter(waLoginVO);
		return executeQueryVOs(sqlBuffer.toString(), parameter, WaClassItemVO.class);
	}

	public DataSVO[] getDataSVOs(WaLoginContext loginContext) throws BusinessException {
		// TODO 增加人员权限
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_datas.pk_wa_data, "); // 1
		sqlBuffer.append("       wa_datas.pk_wa_datas, "); // 2
		sqlBuffer.append("       wa_datas.pk_wa_classitem, "); // 3
		sqlBuffer.append("       wa_datas.ts, "); // 4
		sqlBuffer.append("       wa_datas.value, "); // 5
		sqlBuffer.append("       wa_datas.to_next, "); // 5
		sqlBuffer.append("       wa_datas.caculatevalue, "); // 6
		sqlBuffer.append("       wa_datas.notes, "); // 7
		sqlBuffer.append("       wa_item.itemkey itemkey, "); // 8
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("wa_classitem.name") + "  itemname, "); // 8
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + PSNNAME + ", "); // 9
		sqlBuffer.append("       wa_classitem.iflddecimal, "); // 10
		sqlBuffer.append("       bd_psndoc.code " + PSNCODE + ", "); // 11
		sqlBuffer.append("       hi_psnjob.clerkcode, "); // 3
		sqlBuffer.append("       wa_data.checkflag "); // 12
		sqlBuffer.append("  from wa_datas ");
		sqlBuffer.append(" inner join wa_data on wa_datas.pk_wa_data = wa_data.pk_wa_data ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join wa_classitem on wa_datas.pk_wa_classitem = wa_classitem.pk_wa_classitem ");
		sqlBuffer.append(" inner join wa_item on wa_classitem.pk_wa_item = wa_item.pk_wa_item ");

		sqlBuffer.append(WherePartUtil.addWhereKeyWord2Condition(getCommonWhereCondtion4Data(loginContext
				.getWaLoginVO())));
		String powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(), IHRWADataResCode.WADATA,
				IHRWAActionCode.SpecialPsnAction, "hi_psnjob");
		if (!StringUtils.isBlank(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		sqlBuffer.append("  and  wa_item.pk_wa_item in (" + ItemPowerUtil.getItemPower(loginContext) + ")");
		sqlBuffer.append(" order by wa_datas.pk_wa_data, wa_datas.pk_wa_classitem");
		return executeQueryVOs(sqlBuffer.toString(), DataSVO.class);
	}

	public DataVO[] queryByCondition(WaLoginContext context, String condition, String orderCondtion)
			throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + PSNNAME + ", "); // 1
		// shenliangc 20140702 银行报盘增加户名
		sqlBuffer.append("       bd_psndoc.code " + PSNCODE + ",  "); // 2
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psnidtype.name") + "  as idtype, "); // 4
		sqlBuffer.append("       hi_psnjob.clerkcode, bd_psndoc.id as id, "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + DEPTNAME + ", "); // 4
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  " + ORGNAME + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  " + PLSNAME + ", "); // 5
		// shenliangc 20140702 银行报盘增加户名
		// guoqt套接字问题
		// 2015-07-30 zhosuze NCdp205099799 薪资发放界面添加财务组织财务部门 begin
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("financeorg.name") + "  " + FINANCEORG + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("financedept.name") + "  " + FINANCEDEPT + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("liabilityorg.name") + "  " + LIABILITYORG
				+ ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("liabilitydept.name") + "  " + LIABILITYDEPT
				+ ", "); // 3
		// end

		sqlBuffer.append("       om_job.jobname, "); // 6
		// guoqt岗位
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  " + POSTNAME + ", "); // 6
		sqlBuffer.append("       wa_data.* "); // 7
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" left join org_orgs_v on org_orgs_v.pk_vid = wa_data.workorgvid ");
		sqlBuffer.append(" left join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid ");
		sqlBuffer.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		// shenliangc 20140702 银行报盘增加户名
		// 2015-07-30 zhosuze NCdp205099799 薪资发放界面添加财务组织财务部门 begin
		sqlBuffer.append("  left join org_orgs financeorg on wa_data.pk_financeorg = financeorg.pk_org ");
		sqlBuffer.append("  left join org_dept financedept on wa_data.pk_financedept = financedept.pk_dept ");
		sqlBuffer.append("  left join org_orgs liabilityorg on wa_data.pk_liabilityorg = liabilityorg.pk_org ");
		sqlBuffer.append("  left join org_dept liabilitydept on wa_data.pk_liabilitydept = liabilitydept.pk_dept ");
		// end

		sqlBuffer.append("  left outer join bd_psnidtype on bd_psndoc.idtype = bd_psnidtype.pk_identitype ");
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		// TODO 完善查询
		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");
			sqlBuffer.append(sqlpart);
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition)).append(")");
		}
		// guoqt 薪资发放导出增加权限控制
		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		} else {
			sqlBuffer.append(" order by org_dept_v.code,hi_psnjob.clerkcode");
		}
		return executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);

	}

	public DataVO[] queryByConditionWithItem(WaLoginContext context, String orderCondtion, WaClassItemVO[] classItemVOs)
			throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + PSNNAME + ", "); // 1
		sqlBuffer.append("       bd_psndoc.code " + PSNCODE + ", "); // 2
		sqlBuffer.append("       hi_psnjob.clerkcode, "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + DEPTNAME + ", "); // 4
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  " + ORGNAME + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  " + PLSNAME + ", "); // 5
		sqlBuffer.append("       om_job.jobname, "); // 6
		sqlBuffer.append("       om_post.postname, "); // 6
		sqlBuffer.append("       wa_data.f_1,wa_data.f_3 "); // 7
		if (!ArrayUtils.isEmpty(classItemVOs)) {
			for (WaClassItemVO classItemVO : classItemVOs) {
				sqlBuffer.append("       ,wa_data." + classItemVO.getItemkey()); // 7
			}
		}
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" left join org_orgs_v on org_orgs_v.pk_vid = wa_data.workorgvid ");
		sqlBuffer.append(" left join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid ");
		sqlBuffer.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		// // TODO 完善查询
		// if (!StringUtil.isEmpty(condition)) {
		// sqlBuffer
		// .append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");
		// sqlBuffer.append(sqlpart);
		// sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition))
		// .append(")");
		// }
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		} else {
			sqlBuffer.append(" order by org_dept_v.code,hi_psnjob.clerkcode");
		}
		return executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);

	}

	public DataVO[] querySumDataByCondition(WaLoginContext context, String orderCondtion, WaClassItemVO[] classItemVOs)
			throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  "); // 1
		sqlBuffer.append("  org_orgs_v.code orgcode ,"); // 2
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  " + ORGNAME + ", "); // 3
		sqlBuffer.append("  org_dept_v.code deptcode ,"); // 2
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + DEPTNAME + ", "); // 4
		sqlBuffer.append("   count(wa_data.pk_psnjob ) psnnum"); // 7
		if (!ArrayUtils.isEmpty(classItemVOs)) {
			for (WaClassItemVO classItemVO : classItemVOs) {
				if (classItemVO.getIitemtype() != null
						&& classItemVO.getIitemtype().intValue() == TypeEnumVO.FLOATTYPE.value().intValue()) {
					sqlBuffer.append("       ,sum(" + classItemVO.getItemkey() + ") " + classItemVO.getItemkey()); // 7
				}
			}
		}

		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" left join org_orgs_v on org_orgs_v.pk_vid = wa_data.workorgvid ");
		sqlBuffer.append(" left join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		// // TODO 完善查询
		// if (!StringUtil.isEmpty(condition)) {
		// sqlBuffer
		// .append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");
		// sqlBuffer.append(sqlpart);
		// sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition))
		// .append(")");
		// }
		sqlBuffer.append(" group by org_orgs_v.code," + SQLHelper.getMultiLangNameColumn("org_orgs_v.name")
				+ ",org_dept_v.code," + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + " ");
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		} else {
			sqlBuffer.append(" order by org_dept_v.code");
		}
		return executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);

	}

	public DataVO[] queryByPKSCondition(String condition, String orderCondtion) throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select /*+ optimizer_features_enable('10.2.0.1') */ "
				+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + PSNNAME + ", "); // 1
		sqlBuffer.append("       bd_psndoc.code " + PSNCODE + ", "); // 2
		sqlBuffer.append("       hi_psnjob.clerkcode, "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + DEPTNAME + ", "); // 4
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  " + ORGNAME + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  " + PLSNAME + ", "); // 5
		// shenliangc 20140702 银行报盘增加户名
		// 2015-07-30 zhosuze NCdp205099799 薪资发放界面添加财务组织财务部门 begin
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("financeorg.name") + "  " + FINANCEORG + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("financedept.name") + "  " + FINANCEDEPT + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("liabilityorg.name") + "  " + LIABILITYORG
				+ ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("liabilitydept.name") + "  " + LIABILITYDEPT
				+ ", "); // 3
		// end

		sqlBuffer.append("       om_job.jobname, "); // 6
		// guoqt岗位
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  " + POSTNAME + ", "); // 6
		sqlBuffer.append("       wa_data.*,datapower.operateflag "); // 7
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" left join org_orgs_v on org_orgs_v.pk_vid = wa_data.workorgvid ");
		sqlBuffer.append(" left join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid ");
		sqlBuffer.append(" left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");
		// shenliangc 20140702 银行报盘增加户名
		// 2015-07-30 zhosuze NCdp205099799 薪资发放界面添加财务组织财务部门 begin
		sqlBuffer.append("  left join org_orgs financeorg on wa_data.pk_financeorg = financeorg.pk_org ");
		sqlBuffer.append("  left join org_dept financedept on wa_data.pk_financedept = financedept.pk_dept ");
		sqlBuffer.append("  left join org_orgs liabilityorg on wa_data.pk_liabilityorg = liabilityorg.pk_org ");
		sqlBuffer.append("  left join org_dept liabilitydept on wa_data.pk_liabilitydept = liabilitydept.pk_dept ");
		// end

		// String operateConditon = NCLocator
		// .getInstance()
		// .lookup(IDataPermissionPubService.class)
		// .getDataPermissionSQLWherePartByMetaDataOperation(
		// PubEnv.getPk_user(), IHRWADataResCode.WADATA,
		// IHRWAActionCode.EDIT, PubEnv.getPk_group());

		// 20150703 xiejie3 取出维护权限，如果operateConditon为空，则说明其有所有的权限。begin
		String operateConditon = NCLocator
				.getInstance()
				.lookup(IHRDataPermissionPubService.class)
				.getDataRefSQLWherePartByMDOperationCode(PubEnv.getPk_user(), PubEnv.getPk_group(),
						IHRWADataResCode.WADATA, IHRWAActionCode.EDIT, "wa_data");
		if (StringUtils.isEmpty(operateConditon)) {
			operateConditon = " 1 = 1 ";
		}
		// end

		sqlBuffer.append(" left outer join (select 'Y' as operateflag,pk_wa_data from wa_data  where ");
		sqlBuffer.append(operateConditon);
		sqlBuffer.append(") datapower on wa_data.pk_wa_data = datapower.pk_wa_data ");
		// TODO 完善查询
		if (!StringUtil.isEmpty(condition)) {

			sqlBuffer.append(" where  wa_data.pk_wa_data in (" + condition + ")");

		}
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		}

		return executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);

	}

	public String[] queryPKSByCondition(WaLoginContext context, String condition, String orderCondtion)
			throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select ");
		sqlBuffer.append("       wa_data.pk_wa_data "); // 7
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" left join org_orgs_v on wa_data.WORKORGVID = org_orgs_v.PK_VID ");
		sqlBuffer.append(" LEFT OUTER JOIN org_dept_v ON wa_data.WORKDEPTVID = org_dept_v.PK_VID  ");
		sqlBuffer.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		// TODO 完善查询
		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");
			sqlBuffer.append(sqlpart);
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition)).append(")");
		}
		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		}
		DataVO[] vos = executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);

		String[] pks = new String[0];
		if (vos != null) {
			pks = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				pks[i] = vos[i].getPk_wa_data();
			}

		}
		return pks;

	}

	/**
	 * 查询薪资发放数据
	 * 
	 * @author zhangg on 2009-12-10
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggPayDataVO queryAggPayDataVOByCondition(WaLoginContext loginContext, String condition, String orderCondtion)
			throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();

		// 薪资类别信息
		WaLoginVO waLoginVO = WaClassStateHelper.getWaclassVOWithState(loginContext.getWaLoginVO());
		aggPayDataVO.setLoginVO(waLoginVO);

		// 有权限的薪资项目
		WaClassItemVO[] classItemVOs = getUserClassItemVOs(loginContext);
		aggPayDataVO.setClassItemVOs(classItemVOs);
		// 薪资发放数据。 应该不需要了
		// DataVO[] dataVOs = queryByCondition(loginContext, condition,
		// orderCondtion);
		// aggPayDataVO.setDataVOs(dataVOs);

		String[] pks = queryPKSByCondition(loginContext, condition, orderCondtion);
		aggPayDataVO.setDataPKs(pks);
		aggPayDataVO.setSumData(querySumData(loginContext, condition, classItemVOs));
		DataSVO[] dsvos = getDataSVOs(loginContext);

		aggPayDataVO.setDataSmallVO(dsvos);

		return aggPayDataVO;
	}

	public DataVO querySumData(WaLoginContext context, String condition, WaClassItemVO[] classItemVOs)
			throws DAOException {
		if (ArrayUtils.isEmpty(classItemVOs)) {
			return new DataVO();
		}
		StringBuffer sumSql = new StringBuffer();

		for (int i = 0; i < classItemVOs.length; i++) {
			if (classItemVOs[i].getIitemtype() == 0) {
				sumSql.append("sum(SALARY_DECRYPT(wa_data." + classItemVOs[i].getItemkey() + ")) as "
						+ classItemVOs[i].getItemkey() + ",");
			}
		}
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select ");
		sqlBuffer.append(sumSql); // 7
		sqlBuffer.append(" '1' as pk_wa_data from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" left join org_orgs_v on wa_data.WORKORGVID = org_orgs_v.PK_VID ");
		sqlBuffer.append(" LEFT OUTER JOIN org_dept_v ON wa_data.WORKDEPTVID = org_dept_v.PK_VID  ");
		sqlBuffer.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);
		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");
			sqlBuffer.append(sqlpart);
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition)).append(")");
		}
		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		DataVO vo = executeQueryAppendableVO(sqlBuffer.toString(), DataVO.class);
		// 2016-11-23 zhousze 薪资加密：处理合计行合计数据解密 begin
		// DataVO[] vos = { vo };
		// vo = SalaryDecryptUtil.decrypt4Array(vos)[0];
		// end
		return vo;
	}

	/**
	 * 该方法用于根据薪资方案、期间方案、年度、期间在期间状态表中查询主键、TS用于加版本校验 2015-11-5
	 * 
	 * @author zhousze
	 * @param pk_wa_class
	 * @param pk_periodscheme
	 * @param cperiod
	 * @param cyear
	 * @return
	 * @throws DAOException
	 */
	public PeriodStateVO queryPeriodStateVOByPk(String pk_wa_class, String pk_periodscheme, String cperiod, String cyear)
			throws DAOException {
		PeriodStateVO periodstateVO = new PeriodStateVO();
		StringBuffer sqlA = new StringBuffer();
		sqlA.append("select pk_periodstate from wa_periodstate where pk_wa_period = "
				+ "(select pk_wa_period from wa_period where pk_periodscheme = '" + pk_periodscheme + "' and cyear = '"
				+ cyear + "' and cperiod  = '" + cperiod + "') " + " and pk_wa_class = '" + pk_wa_class + "' ");
		String pk_periodstate = (String) this.getBaseDao().executeQuery(sqlA.toString(), new ResultSetProcessor() {

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				String result = null;
				if (rs.next()) {
					result = rs.getString(1);
				}
				return result;
			}
		});
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select ts from wa_periodstate where pk_wa_period = "
				+ "(select pk_wa_period from wa_period where pk_periodscheme = '" + pk_periodscheme + "' and cyear = '"
				+ cyear + "' and cperiod  = '" + cperiod + "') " + " and pk_wa_class = '" + pk_wa_class + "' ");
		String ts = (String) this.getBaseDao().executeQuery(
		// sqlB.toString(), new BeanProcessor(UFDateTime.class));
				sqlB.toString(), new ColumnProcessor());
		periodstateVO.setPk_periodstate(pk_periodstate);
		periodstateVO.setTs(new UFDateTime(ts));
		return periodstateVO;
	}

	public AggPayDataVO queryAggPayDataVOs(WaLoginContext loginContext, String condition, String orderCondtion)
			throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();

		// 薪资类别信息
		WaLoginVO waLoginVO = WaClassStateHelper.getWaclassVOWithState(loginContext.getWaLoginVO());
		aggPayDataVO.setLoginVO(waLoginVO);

		// 有权限的薪资项目
		WaClassItemVO[] classItemVOs = getUserClassItemVOs(loginContext);
		aggPayDataVO.setClassItemVOs(classItemVOs);

		// 薪资发放数据。 应该不需要了
		DataVO[] dataVOs = queryByCondition(loginContext, condition, orderCondtion);
		aggPayDataVO.setDataVOs(dataVOs);

		// String[] pks = queryPKSByCondition(loginContext, condition,
		// orderCondtion);
		// aggPayDataVO.setDataPKs(pks);

		// DataSVO. 有明细调整的项目
		DataSVO[] dsvos = getDataSVOs(loginContext);

		aggPayDataVO.setDataSmallVO(dsvos);
		return aggPayDataVO;
	}

	/**
	 * 查询申请单包含的详细数据
	 * 
	 * @author zhangg on 2009-12-10
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggPayDataVO queryAggPayDataVOForroll(WaLoginContext loginContext) throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();

		// 审批的薪资项目
		WaClassItemVO[] classItemVOs = getApprovedClassItemVOs(loginContext);
		aggPayDataVO.setClassItemVOs(classItemVOs);

		// // 薪资发放项目
		// String powerSql =
		// WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(),
		// HICommonValue.RESOUCECODE_6007PSNJOB,
		// IHRWADataResCode.WADEFAULT, "wa_data");

		// 2015-7-27 xiejie3 NCdp205377513
		// 补丁合并，【发放申请】增加申请单时，申请单里人员顺序与【薪资发放】的人员顺序保持一致begin
		// 如果没有排序字段 先到数据库中查询有没有当前用户的排序设置 by wangqim
		String orderCondtion = "";
		SortVO sortVOs[] = null;
		SortconVO sortconVOs[] = null;
		String strCondition = " func_code='" + "60130paydata" + "'" + " and group_code= 'TableCode' and ((pk_corp='"
				+ PubEnv.getPk_group() + "' and pk_user='" + PubEnv.getPk_user()
				+ "') or pk_corp ='@@@@') order by pk_corp";

		sortVOs = (SortVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, SortVO.class, strCondition);
		Vector<Attribute> vectSortField = new Vector<Attribute>();
		if (sortVOs != null && sortVOs.length > 0) {
			strCondition = "pk_hr_sort='" + sortVOs[0].getPrimaryKey() + "' order by field_seq ";
			sortconVOs = (SortconVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
					.retrieveByClause(null, SortconVO.class, strCondition);
			for (int i = 0; sortconVOs != null && i < sortconVOs.length; i++) {
				Pair<String> field = new Pair<String>(sortconVOs[i].getField_name(), sortconVOs[i].getField_code());
				Attribute attribute = new Attribute(field, sortconVOs[i].getAscend_flag().booleanValue());
				vectSortField.addElement(attribute);
			}
			orderCondtion = getOrderby(vectSortField);
		}
		if (StringUtils.isBlank(orderCondtion)) {
			orderCondtion = " org_dept_v.code , hi_psnjob.clerkcode ";
		}
		// end

		// DataVO[] dataVOs = queryByConditionWithItem(loginContext,
		// null, classItemVOs);
		DataVO[] dataVOs = queryByConditionWithItem(loginContext, orderCondtion, classItemVOs);

		aggPayDataVO.setDataVOs(dataVOs);
		return aggPayDataVO;
	}

	// 2015-7-27 xiejie3 NCdp205377513
	// 补丁合并，【发放申请】增加申请单时，申请单里人员顺序与【薪资发放】的人员顺序保持一致begin
	public static String getOrderby(Vector<Attribute> vectSortField) {
		if (vectSortField == null || vectSortField.size() == 0) {
			return "";
		}
		String strOrderBy = "";
		String strFullCode = null;
		for (Attribute attr : vectSortField) {
			strFullCode = attr.getAttribute().getValue();
			// if(!(strFullCode.equalsIgnoreCase("bd_psndoc.name")
			// ||strFullCode.equalsIgnoreCase("org_dept_v.name")
			// ||strFullCode.equalsIgnoreCase("queryByConditionWithItem")
			// ||strFullCode.equalsIgnoreCase("bd_psncl.name")
			// ||strFullCode.equalsIgnoreCase("om_post.postname"))){
			// continue;
			// }
			if (strFullCode == null || strFullCode.isEmpty()) {
				continue;
			}
			strOrderBy = strOrderBy + "," + strFullCode + (attr.isAscend() ? "" : " desc");
		}
		return strOrderBy.length() > 0 ? strOrderBy.substring(1) : "";
	}

	// end

	/**
	 * 查询申请单包含的详细数据
	 * 
	 * @author zhangg on 2009-12-10
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggPayDataVO querySumDataVOForroll(WaLoginContext loginContext) throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();

		// 审批的薪资项目
		WaClassItemVO[] classItemVOs = getApprovedClassItemVOs(loginContext);
		// // 薪资发放项目
		// String powerSql = WaPowerSqlHelper.getWaPowerSql(
		// loginContext.getPk_group(),
		// HICommonValue.RESOUCECODE_6007PSNJOB,
		// IHRWADataResCode.WADEFAULT, "wa_data");
		DataVO[] dataVOs = querySumDataByCondition(loginContext, null, classItemVOs);

		aggPayDataVO.setDataVOs(dataVOs);
		return aggPayDataVO;
	}

	// guoqt 薪资发放申请及审批增加合计行
	public DataVO[] querySumDataByConditionAll(WaLoginContext context, String condition, String orderCondtion,
			WaClassItemVO[] classItemVOs) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  COUNT(wa_data.pk_psnjob ) psnnum "); // 1
		if (!ArrayUtils.isEmpty(classItemVOs)) {
			for (WaClassItemVO classItemVO : classItemVOs) {
				// 判断是否是数值型项目
				if (classItemVO.getIitemtype() != null
						&& classItemVO.getIitemtype().intValue() == TypeEnumVO.FLOATTYPE.value().intValue()) {
					sqlBuffer.append("       ,sum(" + classItemVO.getItemkey() + ") " + classItemVO.getItemkey()); // 7
				}
			}
		}

		sqlBuffer.append("  from wa_data where");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		// TODO 完善查询
		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");
			sqlBuffer.append(sqlpart);
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition)).append(")");
		}
		return executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);

	}

	/**
	 * 查询申请单包含的详细数据（合计行）
	 * 
	 * @author guoqt
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggPayDataVO querySumDataVOForrollAll(WaLoginContext loginContext) throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();

		// 审批的薪资项目
		WaClassItemVO[] classItemVOs = getApprovedClassItemVOs(loginContext);
		// 薪资发放项目
		String powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB, IHRWADataResCode.WADEFAULT, "wa_data");
		DataVO[] dataVOs = querySumDataByConditionAll(loginContext, powerSql, null, classItemVOs);

		aggPayDataVO.setDataVOs(dataVOs);
		return aggPayDataVO;
	}

	private void updatePeriodState(String[] colKeys, Object[] colValues, WaLoginVO waLoginVO) throws BusinessException {
		updateTableByColKey("wa_periodstate", colKeys, colValues, getCommonWhereCondtion4PeriodState(waLoginVO));
	}

	public void updatePeriodState(String colKey, Object colValue, WaLoginVO waLoginVO) throws BusinessException {
		updatePeriodState(new String[] { colKey }, new Object[] { colValue }, waLoginVO);
	}

	// shenliangc 20140830 合并计税方案部分审核只计算界面上查询出来的人员数据，需要传入过滤条件。
	public void reCaculate(WaLoginContext loginContext, String whereCondition) throws nc.vo.pub.BusinessException {
		// 合并计税计算及限制移至审核
		TaxBindCaculateService caculateService = new TaxBindCaculateService(loginContext, whereCondition);
		caculateService.doCaculate();
	}

	/**
	 * 薪资发放
	 * 
	 * @author zhangg on 2009-12-4
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	public void onPay(WaLoginContext loginContext) throws nc.vo.pub.BusinessException {
		WaLoginVO waLoginVO = loginContext.getWaLoginVO();
		// 合并计税计算及限制移至审核
		// boolean isInTaxgroup =
		// wapubDAO.isInTaxGroup(waLoginVO.getPk_wa_class());
		//
		// if (isInTaxgroup) {
		// TaxBindCaculateService caculateService = new
		// TaxBindCaculateService(loginContext);
		// caculateService.doCaculate();
		// }

		String[] colKeys = new String[] { PeriodStateVO.PAYOFFFLAG, PeriodStateVO.VPAYCOMMENT, PeriodStateVO.CPAYDATE };
		String comment = waLoginVO.getPeriodVO().getVpaycomment();
		SQLParamType nullValue = SQLTypeFactory.getNullType(Types.VARCHAR);
		UFDate paydate = waLoginVO.getPeriodVO().getCpaydate();

		Object[] colValues = new Object[] { UFBoolean.TRUE, comment == null ? nullValue : comment,
				paydate == null ? nullValue : paydate };
		updatePeriodState(colKeys, colValues, waLoginVO);

		// 如果子方案全都发放，更新父方案状态
		if (!isChildPayoff(loginContext)) {
			colKeys = new String[] { PeriodStateVO.PAYOFFFLAG };
			colValues = new Object[] { UFBoolean.TRUE };
			String cond = getPeriodstateCond(waLoginVO.getPk_prnt_class(), waLoginVO.getCyear(), waLoginVO.getCperiod());
			updateTableByColKey("wa_periodstate", colKeys, colValues, cond);
		}
		colKeys = new String[] { PeriodStateVO.VPAYCOMMENT, PeriodStateVO.CPAYDATE };
		colValues = new Object[] { comment == null ? nullValue : comment, paydate == null ? nullValue : paydate };
		updateTableByColKey("wa_data", colKeys, colValues, WherePartUtil.getCommonWhereCondtion4ChildData(waLoginVO)
				+ " ");
		// guoqt NCdp205075407更新发放日期跟发放原因（无论原先是否有发放日期及发放原因）
		// + " and wa_data.cpaydate is null and wa_data.vpaycomment is null");
		// 更新单据状态为执行态
		String updatePayrollSql = "update wa_payroll set billstate = '" + HRConstEnum.EXECUTED
				+ "' where billstate = '" + IPfRetCheckInfo.PASSING + "' and pk_wa_class = '"
				+ waLoginVO.getPk_wa_class() + "'";
		getBaseDao().executeUpdate(updatePayrollSql);
	}

	public boolean isChildPayoff(WaLoginContext loginContext) throws DAOException {
		String sql = "SELECT wa_inludeclass.pk_childclass " + "FROM wa_inludeclass,wa_periodstate,wa_period "
				+ "WHERE wa_inludeclass.pk_childclass = wa_periodstate.pk_wa_class "
				+ "	AND wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
				+ "	AND wa_inludeclass.cyear = wa_period.cyear " + "	AND wa_inludeclass.cperiod = wa_period.cperiod "
				+ "	AND wa_inludeclass.pk_parentclass = ? " + "	AND wa_inludeclass.cyear = ? "
				+ "	AND wa_inludeclass.cperiod = ? " + "	AND wa_periodstate.payoffflag = 'N' ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(loginContext.getPk_prnt_class());
		parameter.addParam(loginContext.getWaYear());
		parameter.addParam(loginContext.getWaPeriod());
		return isValueExist(sql, parameter);
	}

	private String getPeriodstateCond(String pk_wa_class, String cyear, String cperiod) {
		String cond = " pk_wa_class = '" + pk_wa_class + "' and exists "
				+ "(select wa_period.pk_wa_period  from wa_period  "
				+ "where wa_period.pk_wa_period = wa_periodstate.pk_wa_period" + " and wa_period.cyear =  '" + cyear
				+ "' and wa_period.cperiod =  '" + cperiod + "' and  " + "wa_periodstate.pk_wa_class =  '"
				+ pk_wa_class + "')";
		return cond;
	}

	public void update(Object object, WaLoginVO waLoginVO) throws BusinessException {
		if (!object.getClass().isArray()) {
			DataVO datavo = (DataVO) object;
			// 但强 薪资加密遗漏 2018年5月29日10:00:17
			DataVO[] dataVos = new DataVO[1];
			dataVos[0] = datavo;
			dataVos = SalaryEncryptionUtil.encryption4Array(dataVos);
			singleUpdate(waLoginVO.getPk_wa_class(), dataVos[0]);

		} else {
			Object[] objs = (Object[]) object;
			List<Object> list = Arrays.asList(objs);
			DataVO[] dataVos = list.toArray(new DataVO[objs.length]);
			// 2016-11-22 zhousze 薪资加密：处理所有数值型项目，加密处理 begin
			dataVos = SalaryEncryptionUtil.encryption4Array(dataVos);
			// end
			batchUpdate(waLoginVO.getPk_wa_class(), dataVos);
			// for (Object vo : objs) {
			// DataVO datavo = (DataVO) vo;
			// singleUpdate(datavo);
			// }
		}
		// 更新期间计算状态
		updatePeriodState("caculateflag", UFBoolean.FALSE, waLoginVO);
	}

	/**
	 * 批量更新 <br>
	 * 注意：所有的datavo 的属性必须统一。 Created on 2012-10-8 15:32:57<br>
	 * 
	 * @param objs
	 * @throws BusinessException
	 * @author daicy
	 */
	private void batchUpdate(String pk_wa_class, DataVO... objs) throws BusinessException {
		// 加锁
		BDPKLockUtil.lockSuperVO(objs);
		// 版本校验（时间戳校验）
		BDVersionValidationUtil.validateSuperVO(objs);
		InSQLCreator inSQLCreator = new InSQLCreator();
		try {
			// String sql =
			// "select wa_data.*,bd_psndoc.name psnname from wa_data inner join bd_psndoc on wa_data.PK_PSNDOC = bd_psndoc.pk_psndoc where "
			// + DataVO.PK_WA_DATA + " in (" + inSQLCreator.getInSQL(objs,
			// DataVO.PK_WA_DATA) + ")";
			// //BmDataVO[] dbdatavos = retrieveByClause(BmDataVO.class,
			// condition);
			// DataVO[] dbdatavos = executeQueryVOs(sql, DataVO.class);
			//
			// if (dbdatavos == null || dbdatavos.length == 0)
			// {
			// return;
			// }

			// 20160119 shenliangc 薪资接口导入9000人，耗时520秒，顾问反馈还是慢，请考虑是否可以优化。
			// 这个查询逻辑查询出来所有vo之后没有别的处理，只是判断了数量，所以语句需要改成 select count(*)。
			String sql = "select count(*) from wa_data inner join bd_psndoc on wa_data.PK_PSNDOC = bd_psndoc.pk_psndoc where "
					+ DataVO.PK_WA_DATA + " in (" + inSQLCreator.getInSQL(objs, DataVO.PK_WA_DATA) + ")";
			Integer count = (Integer) getBaseDao().executeQuery(sql, new ColumnProcessor());
			if (count == null || count == 0) {
				return;
			}

			// 必须根据参数Objs来确定 需要更新的attribute。不能根据dbdatavos 来确定。
			// 注意：所有的datavo 的属性必须统一。
			List<String> needUpdateNamesList = getNeedUpdateNamesList(objs[0]);
			List<DataVO> list_update = new ArrayList<DataVO>();
			for (int i = 0; i < objs.length; i++) {

				DataVO temp_datavo = objs[i];
				temp_datavo.setCaculateflag(UFBoolean.FALSE);
				// 尚未审核的才更新
				if (!temp_datavo.getCheckflag().booleanValue()) {
					list_update.add(temp_datavo);
				}
			}
			JdbcPersistenceManager.clearColumnTypes(DataVO.getDefaultTableName());
			DataVO[] updateVOs = list_update.toArray(new DataVO[list_update.size()]);
			getBaseDao().updateVOArray(updateVOs, needUpdateNamesList.toArray(new String[needUpdateNamesList.size()]));

			if (list_update.size() > 0) {
				// for (int i = 0; i < list_update.size(); i++){
				WaBusilogUtil.writeEditLog(pk_wa_class, updateVOs);
				// }
			}
		} finally {
			inSQLCreator.clear();
		}
	}

	private List<String> getNeedUpdateNamesList(DataVO datavo) {

		String[] attributeNames = datavo.getAttributeNames();
		List<String> needUpdateNamesList = new LinkedList<String>();
		for (String attributeName : attributeNames) {
			if (DataVOUtils.isAppendAttribute(attributeName)) {
				needUpdateNamesList.add(attributeName);
			}
		}
		// NCdp205555043 薪资发放数据在列表界面修改后，计算标志没有去掉
		needUpdateNamesList.add(DataVO.CACULATEFLAG);
		needUpdateNamesList.add(DataVO.CPAYDATE);
		needUpdateNamesList.add(DataVO.VPAYCOMMENT);

		return needUpdateNamesList;

	}

	private DataVO singleUpdate(String pk_wa_class, DataVO datavo) throws BusinessException {
		// 加锁
		BDPKLockUtil.lockSuperVO(datavo);

		// 版本校验（时间戳校验）
		BDVersionValidationUtil.validateSuperVO(datavo);

		DataVO dbdatavo = retrieveByPK(DataVO.class, datavo.getPk_wa_data());
		if (dbdatavo == null) {
			throw new BusinessException(datavo.getAttributeValue("psnname")
					+ ResHelper.getString("60130paydata", "060130paydata0449")/*
																			 * @res
																			 * "在该方案的薪资档案中已经被删除!"
																			 */);
		}
		if (dbdatavo.getCheckflag().booleanValue()) {
			throw new BusinessException(datavo.getAttributeValue("psnname")
					+ ResHelper.getString("60130paydata", "060130paydata0450")/*
																			 * @res
																			 * "在该方案的中数据已经被审核!"
																			 */);
		}

		datavo.setCaculateflag(UFBoolean.FALSE);
		String[] attributeNames = datavo.getAttributeNames();
		List<String> needUpdateNamesList = new LinkedList<String>();
		for (String attributeName : attributeNames) {
			if (DataVOUtils.isAppendAttribute(attributeName)) {
				needUpdateNamesList.add(attributeName);
			}
		}
		needUpdateNamesList.add(DataVO.CACULATEFLAG);
		needUpdateNamesList.add(DataVO.CPAYDATE);
		needUpdateNamesList.add(DataVO.VPAYCOMMENT);

		// 记录日志。
		JdbcPersistenceManager.clearColumnTypes(DataVO.getDefaultTableName());
		getBaseDao().updateVO(datavo, needUpdateNamesList.toArray(new String[0]));

		WaBusilogUtil.writeEditLog(pk_wa_class, new DataVO[] { datavo });

		return datavo;
	}

	/**
	 * 薪资取消发放
	 * 
	 * @author zhangg on 2009-12-4
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	public void onUnPay(WaLoginVO waLoginVO) throws nc.vo.pub.BusinessException {

		String checkSql = "SELECT batch " + "FROM wa_inludeclass "
				+ "WHERE pk_parentclass=( select distinct pk_parentclass from wa_inludeclass where pk_childclass =  '"
				+ waLoginVO.getPk_wa_class() + "') and cyear = '" + waLoginVO.getCyear() + "' and cperiod = '"
				+ waLoginVO.getCperiod() + "' and batch > " + waLoginVO.getBatch()
				+ " and batch<100 and pk_childclass <>'" + waLoginVO.getPk_wa_class() + "'";
		if (isValueExist(checkSql)) {
			throw new nc.vo.pub.BusinessException(ResHelper.getString("60130paydata", "060130paydata0451")/*
																										 * @
																										 * res
																										 * "已经增加了新的子方案，该方案不能取消发放！"
																										 */);
		}

		checkSql = "SELECT 1 " + "FROM wa_data " + "WHERE fipendflag = 'Y' " + "and pk_wa_class = ? "
				+ "and cyear = ? " + "and cperiod = ?";
		// 2016-1-19 NCdp205575005 zhousze
		// 多次发薪或离职发薪方案，银企直连汇总传递后，其发薪次数应不不能取消发放。这里在对汇总方案判断 begin
		SQLParameter param = new SQLParameter();
		param.addParam(waLoginVO.getPk_prnt_class());
		param.addParam(waLoginVO.getPeriodVO().getCyear());
		param.addParam(waLoginVO.getPeriodVO().getCperiod());

		if (isValueExist(checkSql, getCommonParameter(waLoginVO)) || isValueExist(checkSql, param)) {
			throw new nc.vo.pub.BusinessException(ResHelper.getString("60130paydata", "060130paydata0534")/*
																										 * @
																										 * res
																										 * "该方案或者该方案的汇总方案已存在财务结算数据，不能取消发放！"
																										 */);
		}
		// end
		// 该方案是否制单
		// if(waLoginVO.isMultiClass()){
		boolean isbill = NCLocator.getInstance().lookup(IAmoSchemeQuery.class).isApportion(waLoginVO);
		if (isbill == true) {
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0452")/*
																								 * @
																								 * res
																								 * "该薪资方案已经制单，不能取消发放！"
																								 */);// "该工资类别已经制单，不能取消发放！"
		}
		// }
		// 合并计税计算及限制移至审核
		// 同一个会计期间下方案是否引用, 应该只有合并计税才能有数
		// String caccyear = waLoginVO.getPeriodVO().getCaccyear();
		// String caccperiod = waLoginVO.getPeriodVO().getCaccperiod();
		// String sqlWhereAccPeriod = " cpreclassid = '" +
		// waLoginVO.getPk_wa_class()
		// + "' and checkflag = 'Y'";
		// sqlWhereAccPeriod += " and caccyear='" + caccyear +
		// "' and caccperiod='" + caccperiod + "'";
		// sqlBuffer = new StringBuffer(WapubDAO.getPeriodViewTable());
		// sqlBuffer.append(WherePartUtil.addWhereKeyWord2Condition(sqlWhereAccPeriod));
		// if (isValueExist(sqlBuffer.toString())) {
		// throw new
		// nc.vo.pub.BusinessException(ResHelper.getString("60130paydata","060130paydata0453")/*@res
		// "该薪资方案发放后已有别的方案引用该方案的数据进行了计算，并审核，该方案不能取消发放！"*/);//
		// "该工资类别发放后已有别的类别引用该类别的数据进行了计算，并审核，该类别不能取消审核！"
		// }

		// 单据状态更新wa_periodstate表isapproved字段值
		// updatePeriodState("isapproved", UFBoolean.FALSE, waLoginVO);
		// 删除审批单项

		// 更新发放标志
		// String[] colKeys = new String[] { "payoffflag", "vpaycomment",
		// "cpaydate", "cpreclassid" };//合并计税计算及限制移至审核
		String[] colKeys = new String[] { "payoffflag", "vpaycomment", "cpaydate" };
		SQLParamType value = SQLTypeFactory.getNullType(Types.VARCHAR);
		// Object[] colValues = new Object[] { UFBoolean.FALSE, value, value,
		// value};//合并计税计算及限制移至审核
		Object[] colValues = new Object[] { UFBoolean.FALSE, value, value };
		updatePeriodState(colKeys, colValues, waLoginVO);
		// //取消发放时将发放日期和发放说明置空
		// SQLParamType nullValue = SQLTypeFactory.getNullType(Types.VARCHAR);
		// colKeys = new String[] {
		// PeriodStateVO.VPAYCOMMENT,PeriodStateVO.CPAYDATE };
		// colValues = new Object[] {nullValue ,nullValue};
		// updateTableByColKey("wa_data", colKeys,
		// colValues,WherePartUtil.getCommonWhereCondtion4ChildData(waLoginVO));
		// // 计算状态 //合并计税计算及限制移至审核
		// //sqlWhereAccPeriod = " cpreclassid like '%" +
		// waLoginVO.getPk_wa_class() + "%'";
		// String sqlWhereAccPeriod = " cpreclassid = '" +
		// waLoginVO.getPk_wa_class() + "'";
		// sqlWhereAccPeriod +=
		// " and pk_wa_period in(select pk_wa_period from wa_period where caccyear='"
		// + caccyear + "' and caccperiod='" + caccperiod + "')";
		// updateTableByColKey("wa_periodstate", "caculateflag",
		// UFBoolean.FALSE, WherePartUtil
		// .addWhereKeyWord2Condition(sqlWhereAccPeriod));

		// boolean isInTaxgroup =
		// wapubDAO.isInTaxGroup(waLoginVO.getPk_wa_class());
		//
		// if (isInTaxgroup) {
		// // FIXME 计算
		// // caculateFormuItem_afterTax_zero(aRecaVO);
		// }

		// 如果是多次发放，更新父方案状态
		// if(waLoginVO.isMultiClass()){
		colKeys = new String[] { PeriodStateVO.PAYOFFFLAG };
		colValues = new Object[] { UFBoolean.FALSE };
		String cond = getPeriodstateCond(waLoginVO.getPk_prnt_class(), waLoginVO.getCyear(), waLoginVO.getCperiod());
		updateTableByColKey("wa_periodstate", colKeys, colValues, cond);
		// }
		// 更新单据状态为执行态
		String updatePayrollSql = "update wa_payroll set billstate = '" + IPfRetCheckInfo.PASSING
				+ "' where billstate = '" + HRConstEnum.EXECUTED + "' and pk_wa_class = '" + waLoginVO.getPk_wa_class()
				+ "' and cyear = '" + waLoginVO.getCyear() + "' and cperiod = '" + waLoginVO.getCperiod() + "' ";
		getBaseDao().executeUpdate(updatePayrollSql);

	}

	/**
	 * 替换
	 * 
	 * @author zhangg on 2009-12-8
	 * @param waLoginVO
	 * @param whereCondition
	 * @param replaceItem
	 * @param formula
	 * @throws BusinessException
	 */

	public void onReplace(WaLoginVO waLoginVO, String whereCondition, WaClassItemVO replaceItem, String formula)
			throws BusinessException {

		String sql = "update wa_data set ";

		if (TypeEnumVO.FLOATTYPE.value().equals(replaceItem.getIitemtype())) {// 数字型
			// 2016-11-22 zhousze 薪资加密：薪资发放-替换薪资数据加密 begin
			double formula_d = SalaryEncryptionUtil.encryption(Double.parseDouble(formula));
			formula = Double.toString(formula_d);
			// end
			sql += replaceItem.getItemkey() + "= '" + formula + "',";
			// + WaCommonImpl.getRoundSql(getBaseDao().getDBType(),formula,
			// replaceItem.getIflddecimal(), replaceItem.getRound_type());
		} else {// 字符型
			sql += replaceItem.getItemkey() + "=" + formula + ", ";
		}

		sql += "caculateflag= 'N'  ";
		sql += " where checkflag='N' and pk_wa_class = ?  and  cyear = ?  and cperiod = ? ";
		sql += " and dr=0  and stopflag = 'N' ";

		sql += WherePartUtil.formatAddtionalWhere(whereCondition);

		// 2016-1-15 NCdp205573835 zhousze 全部替换时，在执行下面的update时抛出异常，现在封装业务异常抛出
		// begin
		try {
			getBaseDao().executeUpdate(sql, getCommonParameter(waLoginVO));
		} catch (DAOException e) {
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0490")/*
																								 * @
																								 * res
																								 * "公式设置错误"
																								 */);
		}
		// end

		// 写业务日志
		WaBusilogUtil.writePaydataReplaceBusiLog(waLoginVO, replaceItem, formula);

		// 更新类别状态
		updatePeriodState("caculateflag", UFBoolean.FALSE, waLoginVO);
	}

	/**
	 * 进行版本控制。 使事务并行进行，从而略去不必要的检查
	 * 
	 * @param waLoginVO
	 * @param dataSVOs
	 * @throws BusinessException
	 */
	public void onSaveDataSVOs(WaLoginVO waLoginVO, DataSVO[] dataSVOs) throws BusinessException {
		if (dataSVOs == null) {
			return;
		}
		DataVO[] dataVOs = new DataVO[dataSVOs.length];
		for (int i = 0; i < dataVOs.length; i++) {
			dataVOs[i] = new DataVO();
			dataVOs[i].setPk_wa_data(dataSVOs[i].getPk_wa_data());
			dataVOs[i].setCaculateflag(UFBoolean.FALSE);
		}
		// 加锁
		BDPKLockUtil.lockSuperVO(dataVOs);

		// 校验&划分
		List<DataSVO> deleteList = new LinkedList<DataSVO>();
		List<DataSVO> updateList = new LinkedList<DataSVO>();
		List<DataSVO> insertList = new LinkedList<DataSVO>();

		InSQLCreator isc = new InSQLCreator();
		String insql = isc.getInSQL(dataVOs, DataVO.PK_WA_DATA);
		DataVO[] newdataVOs = retrieveByClause(DataVO.class, " pk_wa_data in (" + insql + ")");
		if (newdataVOs == null) {
			return;
		}
		HashMap<String, DataVO> map = new HashMap<String, DataVO>();
		for (DataVO dvo : newdataVOs) {
			map.put(dvo.getPk_wa_data(), dvo);
		}
		for (DataSVO vo : dataSVOs) {
			if (map.get(vo.getPk_wa_data()) == null) {
				String psnname = ""; // zhoumxc 修改错误提示，获取个别调整列表中的人员姓名 2014.08.09
				if (map.get(vo.getPk_wa_data()) != null) {
					psnname = vo.getPsnname();
				}
				throw new BusinessException(psnname + ResHelper.getString("60130paydata", "060130paydata0449")/*
																											 * @
																											 * res
																											 * "在该方案的薪资档案中已经被删除!"
																											 */);
			}
			if (map.get(vo.getPk_wa_data()).getCheckflag().booleanValue()) {
				String psnname = ""; // zhoumxc 修改错误提示，获取个别调整列表中的人员姓名 2014.08.09
				if (map.get(vo.getPk_wa_data()) != null) {
					psnname = vo.getPsnname();
				}
				throw new BusinessException(psnname + ResHelper.getString("60130paydata", "060130paydata0450")/*
																											 * @
																											 * res
																											 * "在该方案的中数据已经被审核!"
																											 */);
			}
			if (vo.getStatus() == VOStatus.NEW) {
				insertList.add(vo);
			} else if (vo.getStatus() == VOStatus.DELETED) {
				deleteList.add(vo);
			} else if (vo.getStatus() == VOStatus.UPDATED) {
				updateList.add(vo);
			}
		}

		// 先删除
		getBaseDao().deleteVOArray(deleteList.toArray(new DataSVO[0]));
		// 再修改
		StringBuffer sqlB = new StringBuffer();

		// 检查人员项目是否重复
		DataSVO[] updatesvo = updateList.toArray(new DataSVO[updateList.size()]);
		String[] field = HRWACommonConstants.DATASCOLUMN;
		String tableName = isc.insertValues(HRWACommonConstants.WA_TEMP_DATAS, field, field, updatesvo);
		if (tableName != null) {
			sqlB.append("select wa_datas.pk_wa_datas, ");
			sqlB.append(SQLHelper.getMultiLangNameColumn("bd_psndoc.name"));
			sqlB.append(" psnname ,");
			sqlB.append(SQLHelper.getMultiLangNameColumn("wa_classitem.name"));
			sqlB.append(" itemname");
			sqlB.append(" from wa_datas inner join wa_data on wa_datas.pk_wa_data = wa_data.pk_wa_data");
			sqlB.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
			sqlB.append(" inner join wa_classitem on wa_classitem.pk_wa_classitem = wa_datas.pk_wa_classitem ");
			sqlB.append(" inner join ");
			sqlB.append(tableName);
			sqlB.append(" on (wa_datas.pk_wa_data = ");
			sqlB.append(tableName);
			sqlB.append(".pk_wa_data ");
			sqlB.append(" and wa_datas.pk_wa_classitem = ");
			sqlB.append(tableName);
			sqlB.append(".pk_wa_classitem ");
			sqlB.append(" and wa_datas.pk_wa_datas != ");
			sqlB.append(tableName);
			sqlB.append(".pk_wa_datas )");

			DataSVO[] resultvos = executeQueryVOs(sqlB.toString(), DataSVO.class);
			if (resultvos != null && resultvos.length > 0) {
				StringBuffer eMsg = new StringBuffer();
				for (DataSVO resultvo : resultvos) {
					eMsg.append(ResHelper.getString("60130paydata", "060130paydata0454", resultvo.getPsnname(),
							resultvo.getItemname())
					/* @res "人员姓名为{0}的{1}发放项目已经存在！" */);
					eMsg.append("/r/n");
				}
				throw new BusinessException(eMsg.toString());
			}
			getBaseDao().updateVOArray(updatesvo);
		}

		// 最后插入

		// 检查人员项目是否重复
		DataSVO[] insertsvo = insertList.toArray(new DataSVO[insertList.size()]);
		String[] field2 = HRWACommonConstants.DATAS2COLUMN;
		String tableName2 = isc.insertValues(HRWACommonConstants.WA_TEMP_DATAS2, field2, field2, insertsvo);
		if (tableName2 != null) {
			sqlB = new StringBuffer();
			sqlB.append("select ");
			sqlB.append(SQLHelper.getMultiLangNameColumn("bd_psndoc.name"));
			sqlB.append(" psnname ,");
			sqlB.append(SQLHelper.getMultiLangNameColumn("wa_classitem.name"));
			sqlB.append(" itemname");
			sqlB.append(" from wa_datas inner join wa_data on wa_datas.pk_wa_data = wa_data.pk_wa_data");
			sqlB.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
			sqlB.append(" inner join wa_classitem on wa_classitem.pk_wa_classitem = wa_datas.pk_wa_classitem ");
			sqlB.append(" inner join ");
			sqlB.append(tableName2);
			sqlB.append(" on (wa_datas.pk_wa_data = ");
			sqlB.append(tableName2);
			sqlB.append(".pk_wa_data ");
			sqlB.append(" and wa_datas.pk_wa_classitem = ");
			sqlB.append(tableName2);
			sqlB.append(".pk_wa_classitem )");

			DataSVO[] iresultvos = executeQueryVOs(sqlB.toString(), DataSVO.class);
			if (iresultvos != null && iresultvos.length > 0) {
				StringBuffer eMsg = new StringBuffer();
				for (DataSVO iresultvo : iresultvos) {
					eMsg.append(ResHelper.getString("60130paydata", "060130paydata0454", iresultvo.getPsnname(),
							iresultvo.getItemname())
					/* @res "人员姓名为{0}的{1}发放项目已经存在！" */);
				}
				throw new BusinessException(eMsg.toString());
			}
			// al = new ArrayList<DataSVO>();
			// // 检查人员项目是否重复
			// for (DataSVO dataSVO : insertList) {
			// DataSVO condtionVO = new DataSVO();
			// condtionVO.setPk_wa_data(dataSVO.getPk_wa_data());
			// condtionVO.setPk_wa_classitem(dataSVO.getPk_wa_classitem());
			// if (isVoExist(condtionVO)) {
			// throw new BusinessException(ResHelper.getString("60130paydata",
			// "060130paydata0454", dataSVO.getPsnname(), dataSVO
			// .getItemname())
			// /* @res "人员姓名为{0}的{1}发放项目已经存在！" */);
			// }
			// al.add(dataSVO);
			// }
			getBaseDao().insertVOArray(insertsvo);
		}

		// 更新薪资计算状态
		getBaseDao().updateVOArray(dataVOs, new String[] { DataVO.CACULATEFLAG });
	}

	/**
	 * pk_wa_class year, period
	 * 
	 * @author zhangg on 2009-12-2
	 * @param waLoginVO
	 * @return
	 */
	public static SQLParameter getCommonParameter(WaLoginVO waLoginVO) {
		return WherePartUtil.getCommonParameter(waLoginVO);
	}

	public static SQLParameter getCommonParameterTwice(WaLoginVO waLoginVO) {
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());
		return parameter;
	}

	/**
	 * 
	 * @author zhangg on 2009-12-4
	 * @param waLoginVO
	 * @return
	 */
	public static String getCommonWhereCondtion4Data(WaLoginVO waLoginVO) {
		return WherePartUtil.getCommonWhereCondtion4Data(waLoginVO);
	}

	/**
	 * 
	 * @author zhangg on 2009-12-4
	 * @param waLoginVO
	 * @return
	 */
	public static String getCommonWhereCondtion4PeriodState(WaLoginVO waLoginVO) {
		return WherePartUtil.getCommonWhereCondtion4PeriodState(waLoginVO);
	}

	/**
	 * 判断状态是否发生 变化
	 * 
	 * @author zhangg on 2009-8-12
	 * @param waLoginVO
	 * @param whereCondition
	 * @return
	 * @throws BusinessException
	 */
	public void checkWaClassStateChange(WaLoginVO waLoginVO, String whereCondition) throws BusinessException {
		WaState oldStates = waLoginVO.getState();
		WaState newStates = WaClassStateHelper.getWaclassVOWithState(waLoginVO).getState();

		if (!(oldStates == newStates)) {
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0457")/*
																								 * @
																								 * res
																								 * "选择的薪资方案状态发生变化, 请刷新后再试！"
																								 */);// 选择的薪资类别状态发生变化,
			// 请刷新后再试！
		}
	}

	/**
	 * 得到状态变化了的VO
	 * 
	 * @author zhangg on 2009-8-13
	 * @param waLoginVO
	 * @param wa_data_where
	 * @return
	 * @throws BusinessException
	 */
	public WaLoginVO getNewWaclassVOWithState(WaLoginVO waLoginVO) throws BusinessException {
		return WaClassStateHelper.getWaclassVOWithState(waLoginVO);
	}

	/**
	 * 对比明细
	 * 
	 * @author zhangg on 2010-1-4
	 * @param context
	 * @param whereCondition
	 * @return
	 * @throws BusinessException
	 */
	public DataVO[] getContractDataVOs(WaLoginContext context, String whereCondition, String orderCondition)
			throws BusinessException {
		String pk_wa_class = context.getPk_wa_class();
		WaLoginVO waLoginVO = context.getWaLoginVO();

		String waYear = waLoginVO.getPeriodVO().getCyear();
		String waPeriod = waLoginVO.getPeriodVO().getCperiod();
		UFBoolean checkFlag = waLoginVO.getPeriodVO().getCheckflag();
		if (!pk_wa_class.equals(waLoginVO.getPk_prnt_class())) {
			pk_wa_class = waLoginVO.getPk_prnt_class();
			if (checkFlag != null && !checkFlag.booleanValue()) {
				throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0548")/*
																									 * @
																									 * res
																									 * "由于本次发放数据未审核，请点击汇总后再进行明细对比！"
																									 */);
			}
		}

		// 此处不能直接传waloginvo，如果当前期间已结帐，则则waloginvo中是最新期间，并不是当前期间
		WaClassVO classvo = new WaClassVO();
		classvo.setPk_wa_class(pk_wa_class);
		classvo.setCyear(waYear);
		classvo.setCperiod(waPeriod);
		PeriodStateVO periodStateVO = new MonthEndDAO().getSubclassPrePeriodVO(classvo);

		if (periodStateVO == null) {
			return null;
		}
		String preYear = periodStateVO.getCyear();
		String prePeriod = periodStateVO.getCperiod();

		WaClassItemVO[] classItemVOs = getUserShowClassItemVOs(context);
		// 存在在两个期间不同的部分
		StringBuffer tempsql = new StringBuffer();
		tempsql.append(" select a.pk_psndoc from wa_data a, wa_data b where a.pk_wa_class = b.pk_wa_class and a.pk_psndoc = b.pk_psndoc ");
		if (classItemVOs != null && classItemVOs.length > 0) {
			tempsql.append(" and ( ");
			for (int i = 0; i < classItemVOs.length - 1; i++) {
				tempsql.append(" a.");
				tempsql.append(classItemVOs[i].getItemkey());
				tempsql.append(" <> b.");
				tempsql.append(classItemVOs[i].getItemkey()).append(" or ");
			}
			tempsql.append(" a.");
			tempsql.append(classItemVOs[classItemVOs.length - 1].getItemkey());
			tempsql.append(" <> b.");
			tempsql.append(classItemVOs[classItemVOs.length - 1].getItemkey());
			tempsql.append(" ) ");
		}

		tempsql.append(" and a.pk_wa_class = '");
		tempsql.append(pk_wa_class);
		tempsql.append("' and a.cyear = '");
		tempsql.append(waYear);
		tempsql.append("' and a.cperiod = '");
		tempsql.append(waPeriod);
		tempsql.append("' and b.pk_wa_class = '");
		tempsql.append(pk_wa_class);
		tempsql.append("' and b.cyear = '");
		tempsql.append(preYear);
		tempsql.append("' and b.cperiod = '");
		tempsql.append(prePeriod);
		tempsql.append("'  ");

		// 增加或减少的部分
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_data.pk_psndoc "); // 1
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" where wa_data.pk_psndoc in ");
		sqlBuffer.append("       (select wa_data.pk_psndoc ");
		sqlBuffer.append("          from wa_data ");
		sqlBuffer.append("         where ((wa_data.pk_wa_class = '" + pk_wa_class + "' and wa_data.cyear = '" + waYear
				+ "' and wa_data.cperiod = '" + waPeriod + "') or (wa_data.pk_wa_class = '" + pk_wa_class
				+ "' and wa_data.cyear = '" + preYear + "' and wa_data.cperiod = '" + prePeriod + "')) ");
		sqlBuffer.append("   ) ");
		sqlBuffer.append("   and wa_data.pk_psndoc not in ");
		sqlBuffer.append("       (select a.pk_psndoc ");
		sqlBuffer.append("          from wa_data a, wa_data b ");
		sqlBuffer.append("         where a.pk_wa_class = b.pk_wa_class ");
		sqlBuffer.append("           and a.pk_psndoc = b.pk_psndoc ");
		sqlBuffer.append("           and a.pk_wa_class = '" + pk_wa_class + "' ");
		sqlBuffer.append("           and a.cyear = '" + waYear + "' ");
		sqlBuffer.append("           and a.cperiod = '" + waPeriod + "' ");
		sqlBuffer.append("           and b.pk_wa_class = '" + pk_wa_class + "' ");
		sqlBuffer.append("           and b.cyear = '" + preYear + "' ");
		sqlBuffer.append("           and b.cperiod = '" + prePeriod + "' ");
		sqlBuffer.append(") ");

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select  " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + PSNNAME + ", "); // 1
		sqlB.append("       bd_psndoc.code " + PSNCODE + ", "); // 2
		sqlB.append("       hi_psnjob.clerkcode, "); // 3
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + DEPTNAME + ", "); // 4
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  " + PLSNAME + ", "); // 5
		// 2015-09-20 xiejie3
		// 2015-07-30 zhosuze NCdp205099799 薪资发放界面添加财务组织财务部门 begin
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("financeorg.name") + "  " + FINANCEORG + ", "); // 3
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("financedept.name") + "  " + FINANCEDEPT + ", "); // 3
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("liabilityorg.name") + "  " + LIABILITYORG + ", "); // 3
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("liabilitydept.name") + "  " + LIABILITYDEPT + ", "); // 3
		// end

		sqlB.append("       om_job.jobname, "); // 6
		// guoqt岗位多语
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  " + POSTNAME + ", "); // 6
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  orgname, "); // 6
		sqlB.append("       wa_data.* "); // 7
		sqlB.append("  from wa_data ");
		sqlB.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlB.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlB.append(" inner join org_orgs_v on wa_data.WORKORGVID = org_orgs_v.PK_VID ");
		sqlB.append("  inner join org_dept_v ON wa_data.WORKDEPTVID = org_dept_v.PK_VID  ");
		sqlB.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlB.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		// 2015-09-20 xiejie3
		// 2015-07-30 zhosuze NCdp205099799 薪资发放界面添加财务组织财务部门 begin
		sqlB.append("  left join org_orgs financeorg on wa_data.pk_financeorg = financeorg.pk_org ");
		sqlB.append("  left join org_dept financedept on wa_data.pk_financedept = financedept.pk_dept ");
		sqlB.append("  left join org_orgs liabilityorg on wa_data.pk_liabilityorg = liabilityorg.pk_org ");
		sqlB.append("  left join org_dept liabilitydept on wa_data.pk_liabilitydept = liabilitydept.pk_dept ");
		// end

		sqlB.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");

		String condtion = " where wa_data.pk_wa_class = '" + pk_wa_class + "'  ";
		condtion += " and ((wa_data.cyear = '" + waYear + "' and wa_data.cperiod = '" + waPeriod
				+ "') or (wa_data.cyear = '" + preYear + "' and wa_data.cperiod = '" + prePeriod + "')) ";

		// 20140728 shenliangc 本期间停发人员在明细对比中不论发放数据有没有变化，都只显示上一期间的发放数据。
		condtion += " and stopflag = 'N' ";

		condtion += " and (wa_data.pk_psndoc in ( " + tempsql.toString() + ")or wa_data.pk_psndoc in ("
				+ sqlBuffer.toString() + ")) ";
		if (whereCondition != null && whereCondition.startsWith("pk_"))
			whereCondition = " hi_psnjob." + whereCondition;
		condtion += WherePartUtil.formatAddtionalWhere(whereCondition);

		sqlB.append(condtion);
		// 数据权限guoqt明细对比
		String powerSql = WaPowerSqlHelper.getWaPowerSql(waLoginVO.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtils.isBlank(powerSql)) {
			sqlB.append(" and " + powerSql);
		}
		if (StringUtil.isEmpty(orderCondition)) {
			orderCondition = " org_dept_v.code , hi_psnjob.clerkcode";
		}
		sqlB.append(" order by ").append(orderCondition + ",wa_data.cyear,wa_data.cperiod");

		return executeQueryAppendableVOs(sqlB.toString(), DataVO.class);
	}

	/**
	 * 得到需要汇总的薪资项目 规则： 在汇总类别中， 并且在被汇总的类别中的数值型项目手工输入的项目
	 * 
	 * @author liangxr on 2010-5-26
	 * @param aRecaVO
	 * @return
	 * @throws DAOException
	 */
	public WaItemVO[] getUnitDigitItem(WaLoginVO waLoginVO) throws DAOException {

		// 得到需要汇总的薪资项目
		// 规则： 在汇总类别中， 并且在被汇总的类别中的数值型项目手工输入的项目
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_item.itemkey, wa_item.iproperty "); // 1
		sqlBuffer.append("  from wa_item ");
		sqlBuffer.append(" where wa_item.pk_wa_item in ");
		sqlBuffer.append("       (select wa_classitem.pk_wa_item ");
		sqlBuffer.append("          from wa_classitem ");
		sqlBuffer.append("         where wa_classitem.pk_wa_class in ");
		sqlBuffer.append("               (select wa_unitctg.classedid from wa_unitctg,wa_waclass ");
		sqlBuffer.append("				where wa_waclass.pk_wa_class = wa_unitctg.classedid ");
		sqlBuffer.append("				and wa_unitctg.pk_wa_class = ? and wa_waclass.stopflag='N') ");
		sqlBuffer.append("           and wa_classitem.cyear = ? ");
		sqlBuffer.append("           and wa_classitem.cperiod = ?) ");
		sqlBuffer.append("   and wa_item.pk_wa_item in ");
		sqlBuffer.append("       (select wa_classitem.pk_wa_item ");
		sqlBuffer.append("          from wa_classitem ");
		sqlBuffer.append("         where wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append("           and wa_classitem.cyear = ? ");
		sqlBuffer.append("           and wa_classitem.cperiod = ?) ");
		sqlBuffer.append("   and wa_item.iitemtype = 0 ");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_prnt_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());
		return executeQueryVOs(sqlBuffer.toString(), parameter, WaItemVO.class);

	}

	public WaItemVO[] getParentClassDigitItem(WaLoginVO waLoginVO) throws DAOException {

		// 得到需要汇总的薪资项目
		// 规则： 在汇总类别中， 并且在被汇总的类别中的数值型项目手工输入的项目
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_item.itemkey, wa_item.iproperty "); // 1
		sqlBuffer.append("  from wa_item ");
		sqlBuffer.append("   where  wa_item.pk_wa_item in ");
		sqlBuffer.append("       (select wa_classitem.pk_wa_item ");
		sqlBuffer.append("          from wa_classitem ");
		sqlBuffer.append("         where wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append("           and wa_classitem.cyear = ? ");
		sqlBuffer.append("           and wa_classitem.cperiod = ?) ");
		sqlBuffer.append("   and wa_item.iitemtype = 0 ");

		return executeQueryVOs(sqlBuffer.toString(), getCommonParameter(waLoginVO), WaItemVO.class);

	}

	/**
	 * 删除汇总类别的人员档案信息
	 * 
	 * @author liangxr on 2010-5-26
	 * @param waLoginVO
	 * @throws DAOException
	 */
	public void deleteUnitClassPsn(WaLoginVO waLoginVO) throws DAOException {

		SQLParameter parameter = getCommonParameter(waLoginVO);
		String sql = "delete from wa_data where pk_wa_class = ? and cyear= ? and cperiod = ? ";
		// StringBuffer sqlBuffer = new StringBuffer();
		// sqlBuffer
		// .append("delete from wa_data where pk_wa_class = ? and cyear= ? and cperiod = ? and pk_psnjob not in");
		// sqlBuffer.append("          (select wa_data.pk_psnjob ");
		// sqlBuffer.append("             from wa_data ");
		// sqlBuffer.append("            where wa_data.pk_wa_class in ");
		// sqlBuffer.append("               (select wa_unitctg.classedid from wa_unitctg,wa_waclass ");
		// sqlBuffer.append("				where wa_waclass.pk_wa_class = wa_unitctg.classedid ");
		// sqlBuffer.append("				and wa_unitctg.pk_wa_class = ? and wa_waclass.stopflag='N') ");
		// sqlBuffer.append("              and wa_data.cyear = ? ");
		// sqlBuffer.append("              and wa_data.cperiod = ? ");
		// sqlBuffer.append("              and wa_data.stopflag = 'N' )");

		getBaseDao().executeUpdate(sql, parameter);

	}

	/**
	 * 重新汇总时处理相关表
	 * 
	 * @author liangxr on 2010-7-2
	 * @param waLoginVO
	 * @throws DAOException
	 */
	public void deleteUnitRelation(WaLoginVO waLoginVO) throws DAOException {
		SQLParameter parameter = getCommonParameterTwice(waLoginVO);

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer
				.append("delete from wa_redata where pk_wa_class = ? and cyear= ? and cperiod = ? and pk_psnjob not in");
		sqlBuffer.append("          (select wa_data.pk_psnjob ");
		sqlBuffer.append("             from wa_data ");
		sqlBuffer.append("            where wa_data.pk_wa_class  = ? ");
		sqlBuffer.append("              and wa_data.cyear = ? ");
		sqlBuffer.append("              and wa_data.cperiod = ? )");
		getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);
		// sqlBuffer = new StringBuffer();
		// sqlBuffer
		// .append("delete from wa_dataz where pk_wa_class = ? and cyear= ? and cperiod = ? and pk_psnjob not in");
		// sqlBuffer.append("          (select wa_data.pk_psnjob ");
		// sqlBuffer.append("             from wa_data ");
		// sqlBuffer.append("            where wa_data.pk_wa_class  = ? ");
		// sqlBuffer.append("              and wa_data.cyear = ? ");
		// sqlBuffer.append("              and wa_data.cperiod = ? )");
		// getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);
		//
		// sqlBuffer = new StringBuffer();
		// sqlBuffer
		// .append("delete from wa_dataf where pk_wa_class = ? and cyear= ? and cperiod = ? and pk_psnjob not in");
		// sqlBuffer.append("          (select wa_data.pk_psnjob ");
		// sqlBuffer.append("             from wa_data ");
		// sqlBuffer.append("            where wa_data.pk_wa_class  = ? ");
		// sqlBuffer.append("              and wa_data.cyear = ? ");
		// sqlBuffer.append("              and wa_data.cperiod = ? )");
		// getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);

		// sqlBuffer = new StringBuffer();
		// sqlBuffer
		// .append("delete from wa_tax where pk_wa_class = ? and vcalyear= ? and vcalmonth = ?  and pk_psnjob not in");
		// sqlBuffer.append("          (select wa_data.pk_psnjob ");
		// sqlBuffer.append("             from wa_data ");
		// sqlBuffer.append("            where wa_data.pk_wa_class  = ? ");
		// sqlBuffer.append("              and wa_data.cyear = ? ");
		// sqlBuffer.append("              and wa_data.cperiod = ? )");
		// getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);

	}

	/**
	 * 需要增加的汇总的人员 规则：汇总类别已经有数据的保留汇总类别数据信息。 对于子类别，扣税标志和税率表默认汇总类别
	 * 
	 * @author zhangg on 2009-3-17
	 * @param aRecaVO
	 * @return
	 * @throws DAOException
	 */
	public PayfileVO[] getUnitPsnVOs(WaLoginVO waLoginVO) throws DAOException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select pk_psnjob," + "pk_psndoc," + "pk_psnorg," + "taxtype," + "taxtableid," + "isderate,"
				+ "isndebuct," + "derateptg," + "pk_group," + "pk_org," + "pk_bankaccbas1," + "pk_bankaccbas2,"
				+ "pk_bankaccbas3," + "partflag," + "stopflag, wa_data.* from wa_data ");
		sqlBuffer.append("     where wa_data.pk_wa_class in  ");
		sqlBuffer.append("               (select wa_unitctg.classedid from wa_unitctg,wa_waclass ");
		sqlBuffer.append("				where wa_waclass.pk_wa_class = wa_unitctg.classedid ");
		sqlBuffer.append("				and wa_unitctg.pk_wa_class = ? and wa_waclass.stopflag='N') ");
		sqlBuffer.append("      and wa_data.cyear = ? ");
		sqlBuffer.append("      and wa_data.cperiod = ? ");
		sqlBuffer.append("      and wa_data.stopflag = 'N' ");
		sqlBuffer.append("      and wa_data.pk_psndoc not in ");
		sqlBuffer.append("       (select pk_psndoc ");
		sqlBuffer.append("       		from wa_data ");
		sqlBuffer.append("          	where wa_data.pk_wa_class = ? ");
		sqlBuffer.append("            	and wa_data.cyear = ? ");
		sqlBuffer.append("              and wa_data.cperiod = ?)");

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_prnt_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());
		return executeQueryVOs(sqlBuffer.toString(), parameter, PayfileVO.class);

	}

	public void updateData(WaLoginVO waLoginVO, WaItemVO[] itemVOs) throws DAOException {
		if (itemVOs == null) {
			return;
		}
		// 分数据库进行更新
		if (getBaseDao().getDBType() == DBUtil.SQLSERVER) {
			updateDataSQLDbs(itemVOs, waLoginVO);
		} else {
			updateDataOracleDbs(itemVOs, waLoginVO);
		}

	}

	/**
	 * 更新汇总字段值（sqlserver）
	 * 
	 * @author liangxr on 2010-5-27
	 * @param itemVOs
	 * @param waLoginVO
	 * @throws DAOException
	 */
	private void updateDataSQLDbs(WaItemVO[] itemVOs, WaLoginVO waLoginVO) throws DAOException {

		String tableName = getDataTableName(waLoginVO);

		// 需要更新的字段
		List<String> list = new LinkedList<String>();
		for (WaItemVO itemVO : itemVOs) {
			list.add(tableName + "." + itemVO.getItemkey() + " = sum_data." + itemVO.getItemkey() + "");
		}

		String colNames = FormatVO.formatListToString(list, "");

		// SUM的字段
		List<String> sumList = new LinkedList<String>();
		for (WaItemVO itemVO : itemVOs) {
			sumList.add("sum(" + tableName + "." + itemVO.getItemkey() + ") " + itemVO.getItemkey());
		}

		String sumColNames = FormatVO.formatListToString(sumList, "");
		String extraConditon = "";

		// // 只合计非停放人员
		// if (tableName.equalsIgnoreCase("wa_dataz")) {
		// extraConditon = " and exists (select 1"; // 1
		// extraConditon += "          from wa_data";
		// extraConditon += "         where wa_dataz.classid = wa_data.classid";
		// extraConditon += "           and wa_dataz.cyear = wa_data.cyear";
		// extraConditon += "           and wa_dataz.cperiod = wa_data.cperiod";
		// extraConditon += "           and wa_dataz.psnid = wa_data.psnid";
		// extraConditon += "           and wa_data.istopflag = 'N') ";
		//
		// } else {
		extraConditon = " and wa_data.stopflag = 'N' ";
		// }

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update " + tableName + " "); // 1
		sqlBuffer.append("   set " + colNames + "  "); // 2
		sqlBuffer.append("   from (select " + sumColNames + ", " + tableName + ".pk_psndoc ");
		sqlBuffer.append("     from " + tableName + " ");
		sqlBuffer.append("    where " + tableName + ".pk_wa_class in ");
		sqlBuffer.append("               (select wa_waclass.pk_wa_class ");
		sqlBuffer.append("				from wa_unitctg,wa_waclass  ");
		sqlBuffer.append("                where  wa_waclass.pk_wa_class = wa_unitctg.classedid  ");
		sqlBuffer.append("                and wa_unitctg.pk_wa_class =  ? ");
		sqlBuffer.append("				and wa_waclass.stopflag='N' )");

		sqlBuffer.append("      and " + tableName + ".cyear = ? ");
		sqlBuffer.append("      and " + tableName + ".cperiod = ? ");
		sqlBuffer.append(extraConditon);

		// 汇总一定是按照pk_psndoc汇总
		sqlBuffer.append("    group by " + tableName + ".pk_psndoc) sum_data ");
		sqlBuffer.append(" where " + tableName + ".pk_wa_class = ? ");
		sqlBuffer.append(" and " + tableName + ".cyear = ?  ");
		sqlBuffer.append(" and " + tableName + ".cperiod = ?  ");
		sqlBuffer.append(" and " + tableName + ".pk_psndoc = sum_data.pk_psndoc ");
		SQLParameter parameter = new SQLParameter();

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());
		getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);

	}

	/**
	 * 更新汇总字段值（oracle）
	 * 
	 * @author liangxr on 2010-5-27
	 * @param itemVOs
	 * @param waLoginVO
	 * @throws DAOException
	 */
	private void updateDataOracleDbs(WaItemVO[] itemVOs, WaLoginVO waLoginVO) throws DAOException {

		String tableName = getDataTableName(waLoginVO);

		// 需要更新的字段
		List<String> list = new LinkedList<String>();
		for (WaItemVO itemVO : itemVOs) {
			list.add("unit." + itemVO.getItemkey());
		}

		String colNames = FormatVO.formatListToString(list, "");

		// SUM的字段
		List<String> sumList = new LinkedList<String>();
		for (WaItemVO itemVO : itemVOs) {
			sumList.add(" nvl(sum(" + tableName + "." + itemVO.getItemkey() + "),0)");
		}

		String sumColNames = FormatVO.formatListToString(sumList, "");

		String extraConditon = "";
		// 只合计非停放人员
		// if (tableName.equalsIgnoreCase("wa_dataz")) {
		// extraConditon = " and exists (select 1"; // 1
		// extraConditon += "          from wa_data";
		// extraConditon += "         where wa_dataz.classid = wa_data.classid";
		// extraConditon += "           and wa_dataz.cyear = wa_data.cyear";
		// extraConditon += "           and wa_dataz.cperiod = wa_data.cperiod";
		// extraConditon += "           and wa_dataz.psnid = wa_data.psnid";
		// extraConditon += "           and wa_data.istopflag = 0) ";
		//
		// } else {
		extraConditon = " and wa_data.stopflag = 'N' ";
		// }

		// 组织SQL语句
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update " + tableName + " unit "); // 1
		sqlBuffer.append("   set (" + colNames + ") = (select  " + sumColNames + " "); // 2
		sqlBuffer.append("                                  from " + tableName + " where " + tableName
				+ ".pk_wa_class in ");
		sqlBuffer.append("               (select wa_waclass.pk_wa_class ");
		sqlBuffer.append("				from wa_unitctg,wa_waclass  ");
		sqlBuffer.append("                where  wa_waclass.pk_wa_class = wa_unitctg.classedid  ");
		sqlBuffer.append("                and wa_unitctg.pk_wa_class =  ? ");
		sqlBuffer.append("				and wa_waclass.stopflag='N' )");

		sqlBuffer.append("                                       and " + tableName + ".cyear = ?  ");
		sqlBuffer.append("                                       and " + tableName + ".cperiod = ?  ");
		sqlBuffer.append(extraConditon);
		sqlBuffer.append("                                       and " + tableName
				+ ".pk_psndoc = unit.pk_psndoc group by " + tableName + ".pk_psndoc) ");
		sqlBuffer.append(" where unit.pk_wa_class = ? ");
		sqlBuffer.append("   and unit.cyear = ? ");
		sqlBuffer.append("   and unit.cperiod = ? ");

		// 仅仅汇总子方案中有的人员
		sqlBuffer
				.append(" and unit.pk_psndoc in  (select wa_data.pk_psndoc    "
						+ "from wa_data  where wa_data.pk_wa_class in "
						+ "(select wa_waclass.pk_wa_class from wa_unitctg, wa_waclass  where wa_waclass.pk_wa_class = wa_unitctg.classedid and wa_unitctg.pk_wa_class = ? and wa_waclass.stopflag = 'N'  ) and wa_data.cyear = ? and wa_data.cperiod = ? and wa_data.stopflag = 'N') ");
		SQLParameter parameter = new SQLParameter();

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);
	}

	/**
	 * 汇总后更新标志
	 * 
	 * @author liangxr on 2010-6-1
	 * @throws BusinessException
	 */
	public void updateStateforTotal(WaLoginVO waLoginVO) throws BusinessException {
		// 重计算标志
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("   wa_data.stopflag = 'N' ");
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(getCommonWhereCondtion4Data(waLoginVO)));
		updateTableByColKey("wa_data", new String[] { PayfileVO.CACULATEFLAG, PayfileVO.CHECKFLAG }, new Object[] {
				UFBoolean.TRUE, UFBoolean.FALSE }, sqlBuffer.toString());

		// 所有人员都已计算完毕（汇总）
		updatePeriodState("caculateflag", UFBoolean.TRUE, waLoginVO);
	}

	/**
	 * 得到需要汇总的表
	 * 
	 * @author zhangg on 2009-3-25
	 * @param aRecaVO
	 * @return
	 */
	public String getDataTableName(WaLoginVO waLoginVO) {
		String tableName = "wa_data";
		// boolean isZhuBi = waLoginVO.getCurrentBO().isZhuBi();
		//
		// // 如果是主币，说明所有参与汇总的类别都是主币或各币种不同
		// if (isZhuBi) {
		// // 更新主币汇总数据
		// tableName = "wa_dataz";
		// }
		// // 如果不是主币，说明所有参与汇总的各类别币种相同，且不是主币
		// else {
		// // 更新原币汇总数据
		// tableName = "wa_data";
		// }
		return tableName;

	}

	/**
	 * wa_data计算状态修改为全部未计算 wa_data审核状态改为全部未审核 wa_periodstate 修改为未计算,未审核
	 * 
	 * @author liangxr on 2010-7-7
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @throws DAOException
	 */
	public void updatePaydataFlag(String pk_wa_class, String cyear, String cperiod) throws DAOException {
		String sql = "update wa_data set checkflag ='N', caculateflag='N' where pk_wa_class =? and cyear=? and cperiod=?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_wa_class);
		parameter.addParam(cyear);
		parameter.addParam(cperiod);
		getBaseDao().executeUpdate(sql, parameter);

		sql = "update wa_periodstate  set checkflag= 'N',caculateflag = 'N' " + "where pk_wa_class = ? and exists "
				+ "(select wa_period.pk_wa_period  from wa_period  "
				+ "where wa_period.pk_wa_period = wa_periodstate.pk_wa_period"
				+ " and wa_period.cyear = ? and wa_period.cperiod = ? and  " + "wa_periodstate.pk_wa_class = ?)";
		parameter = new SQLParameter();
		parameter.addParam(pk_wa_class);
		parameter.addParam(cyear);
		parameter.addParam(cperiod);
		parameter.addParam(pk_wa_class);
		getBaseDao().executeUpdate(sql, parameter);
	}

	/**
	 * 清空Wa_data中某个项目的值、
	 * 
	 * @author liangxr on 2010-7-8
	 * @param vo
	 * @throws DAOException
	 */
	public void clearClassItemData(WaClassItemVO vo) throws DAOException {
		String sql = " update wa_data set " + vo.getItemkey() + " = ? where pk_wa_class=? and cyear=? and cperiod=?";

		SQLParameter parameter = new SQLParameter();
		if (vo.getItemkey().startsWith("f_")) {
			parameter.addParam(0);
		} else {
			SQLParamType value = SQLTypeFactory.getNullType(Types.VARCHAR);
			parameter.addParam(value);
		}
		parameter.addParam(vo.getPk_wa_class());
		parameter.addParam(vo.getCyear());
		parameter.addParam(vo.getCperiod());
		getBaseDao().executeUpdate(sql, parameter);

	}

	/**
	 * 计算当前选中没有审核的人员数据 --getCorpTmSelected
	 * 
	 * @author liangxr on 2010-7-12
	 * @param cacuItem
	 * @param whereStr
	 * @return
	 * @throws DAOException
	 */
	public BigDecimal getOrgTmSelected(String cacuItem, String whereStr) throws DAOException {

		StringBuffer strSql = new StringBuffer();
		strSql.append("SELECT sum(");
		strSql.append(cacuItem);
		strSql.append(")  FROM wa_data,wa_waclass where ");
		strSql.append(whereStr);

		return new BigDecimal(getBaseDao().executeQuery(strSql.toString(), new ColumnProcessor()).toString());

	}

	/**
	 * 按组织计算总值公共函数
	 * 
	 * @author liangxr on 2010-7-12
	 * @param cacuItem
	 * @param pk_org
	 * @param accYear
	 * @param accPeriod
	 * @param pk_wa_class
	 * @param sumtype
	 *            总额类型 1:本期间，2：当年累计到本期间，3：年度
	 * @return
	 * @throws DAOException
	 */
	public BigDecimal getOrgTm(String cacuItem, String pk_org, String accYear, String accPeriod, String pk_wa_class,
			int sumtype) throws DAOException {

		StringBuffer strSql = new StringBuffer();
		strSql.append("SELECT isnull(sum(");
		strSql.append(cacuItem);
		strSql.append("),0) FROM wa_data,wa_periodstate,wa_period,wa_waclass WHERE ");
		strSql.append(" wa_data.workorg = '" + pk_org + "' ");
		strSql.append(" and wa_data.pk_wa_class=wa_waclass.pk_wa_class and wa_waclass.collectflag='N' and wa_waclass.mutipleflag = 'N' "
				+ " and wa_data.pk_wa_class= wa_periodstate.pk_wa_class"
				+ " and wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
				+ " and wa_data.cyear= wa_period.cyear"
				+ " and wa_data.cperiod=wa_period.cperiod and wa_period.caccyear='");
		strSql.append(accYear);
		if (sumtype == SalaryPmtCommonValue.CURRPERIOD) {
			strSql.append("' and wa_period.caccperiod='");
			strSql.append(accPeriod);
		} else if (sumtype == SalaryPmtCommonValue.ACUMULATE_TO_CURRPERIOD) {
			strSql.append("' and wa_period.caccperiod<='");
			strSql.append(accPeriod);
		}
		strSql.append("' and wa_data.checkflag = 'Y' ");
		// strSql.append("' and wa_data.checkflag = 'Y' and wa_data.pk_wa_class='");
		// strSql.append(pk_wa_class).append("'");

		return new BigDecimal(getBaseDao().executeQuery(strSql.toString(), new ColumnProcessor()).toString());
	}

	/**
	 * 按组织计算实际发生值
	 * 
	 * @throws BusinessException
	 */
	public AppendableVO[] getOrgRealTm(String[] pk_orgs, AppendableVO[] items, String accYear, String accPeriod,
			int sumtype) throws BusinessException {
		InSQLCreator inSQLCreator = new InSQLCreator();
		HashMap<String, String> itemmap = new HashMap<String, String>();
		String[] strItems = null;
		for (AppendableVO item : items) {
			String pk_budgetItem = item.getAttributeValue(BudgetItemVO.PK_BUDGET_ITEM).toString();
			String computerule = item.getAttributeValue("computerule").toString();
			itemmap.put(pk_budgetItem, computerule);
			strItems = (String[]) ArrayUtils.add(strItems, pk_budgetItem);
		}
		try {
			StringBuffer strSql = new StringBuffer();
			strSql.append("SELECT wa_data.workorg pk_org");
			for (String strItem : strItems) {
				strSql.append(",isnull(sum(");
				strSql.append(itemmap.get(strItem));
				strSql.append("),0) realvalue");
				strSql.append(strItem);
			}
			strSql.append(" FROM wa_data,wa_periodstate,wa_period,wa_waclass WHERE  ");
			strSql.append(" wa_data.workorg in (" + inSQLCreator.getInSQL(pk_orgs) + ") ");
			strSql.append("  and wa_data.pk_wa_class=wa_waclass.pk_wa_class and wa_waclass.collectflag='N' and wa_waclass.mutipleflag = 'N' "
					+ " and wa_data.pk_wa_class= wa_periodstate.pk_wa_class"
					+ " and wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
					+ " and wa_data.cyear= wa_period.cyear"
					+ " and wa_data.cperiod=wa_period.cperiod and wa_period.caccyear='");
			strSql.append(accYear);
			if (sumtype == SalaryPmtCommonValue.CURRPERIOD) {
				strSql.append("' and wa_period.caccperiod='");
				strSql.append(accPeriod);
			} else if (sumtype == SalaryPmtCommonValue.ACUMULATE_TO_CURRPERIOD) {
				strSql.append("' and wa_period.caccperiod<='");
				strSql.append(accPeriod);
			}
			strSql.append("' and wa_data.checkflag = 'Y' ");

			strSql.append(" group by wa_data.workorg ");

			AppendableVO[] vos = executeQueryAppendableVOs(strSql.toString(), AppendableVO.class);
			AppendableVO[] itemvos = null;
			if (ArrayUtils.isEmpty(vos)) {
				return null;
			}
			for (AppendableVO vo : vos) {
				String pk_org = vo.getAttributeValue("pk_org").toString();
				for (String strItem : strItems) {

					AppendableVO tvo = new AppendableVO();
					tvo.setAttributeValue("pk_org", pk_org);
					tvo.setAttributeValue("realvalue", vo.getAttributeValue("realvalue" + strItem));
					tvo.setAttributeValue("pk_budgetItem", strItem);
					itemvos = (AppendableVO[]) ArrayUtils.add(itemvos, tvo);
				}
			}
			return itemvos;

		} finally {

		}

	}

	/**
	 * 按部门 计算总值公共函数
	 * 
	 * @author liangxr on 2010-7-12
	 * @param cacuItem
	 * @param pk_org
	 * @param accYear
	 * @param accPeriod
	 * @param pk_wa_class
	 * @param sumtype
	 *            总额类型 1:本期间，2：当年累计到本期间，3：年度
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, BigDecimal> getDeptTm(String cacuItem, String pk_org, String accYear, String accPeriod,
			String pk_wa_class, int sumtype) throws DAOException {

		StringBuffer strSql = new StringBuffer();
		strSql.append("SELECT wa_data.workdept pk_dept,sum(");
		strSql.append(cacuItem);
		strSql.append(") FROM wa_data,wa_periodstate,wa_period,wa_waclass WHERE  ");
		strSql.append(" wa_data.workorg = '" + pk_org + "' ");
		strSql.append(" and wa_data.pk_wa_class=wa_waclass.pk_wa_class and wa_waclass.collectflag='N' and wa_waclass.mutipleflag = 'N'"
				+ " and wa_data.pk_wa_class= wa_periodstate.pk_wa_class"
				+ " and wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
				+ " and wa_data.cyear= wa_period.cyear"
				+ " and wa_data.cperiod=wa_period.cperiod and wa_period.caccyear='");
		strSql.append(accYear);
		if (sumtype == SalaryPmtCommonValue.CURRPERIOD) {
			strSql.append("' and wa_period.caccperiod='");
			strSql.append(accPeriod);
		} else if (sumtype == SalaryPmtCommonValue.ACUMULATE_TO_CURRPERIOD) {
			strSql.append("' and wa_period.caccperiod<='");
			strSql.append(accPeriod);
		}
		strSql.append("' and wa_data.checkflag = 'Y' ");
		// strSql.append("' and wa_data.checkflag = 'Y' and wa_data.pk_wa_class='");
		// strSql.append(pk_wa_class).append("'");
		strSql.append(" group by wa_data.workdept");

		List<Object[]> list = (List<Object[]>) getBaseDao().executeQuery(strSql.toString(), new ArrayListProcessor());
		Map<String, BigDecimal> resultMap = new HashMap<String, BigDecimal>();
		for (Object[] obj : list) {
			BigDecimal bigdecimal = new BigDecimal(0);
			if (obj[1] != null) {
				bigdecimal = new BigDecimal(obj[1].toString());
			}
			resultMap.put((String) obj[0], bigdecimal);
		}
		return resultMap;
	}

	/**
	 * 取得部门实际发生值
	 * 
	 * @param pk_depts
	 * @param cacuItem
	 * @param accYear
	 * @param accPeriod
	 * @param pk_wa_class
	 * @param sumtype
	 * @return
	 * @throws BusinessException
	 */
	public AppendableVO[] getRealDeptTm(String pk_org, String[] pk_depts, AppendableVO[] items, String accYear,
			String accPeriod, int sumtype) throws BusinessException {

		InSQLCreator inSQLCreator = new InSQLCreator();
		HashMap<String, String> itemmap = new HashMap<String, String>();
		String[] strItems = null;
		for (AppendableVO item : items) {
			String pk_budgetItem = item.getAttributeValue(BudgetItemVO.PK_BUDGET_ITEM).toString();
			String computerule = item.getAttributeValue("computerule").toString();
			itemmap.put(computerule, pk_budgetItem);
			// Logger.debug("pk_budgetItem=:"+pk_budgetItem);
			// Logger.debug("computerule=:"+computerule);
			// Logger.debug("mapget="+itemmap.get(computerule));
			strItems = (String[]) ArrayUtils.add(strItems, computerule);
		}
		try {

			StringBuffer strSql = new StringBuffer();
			strSql.append("SELECT wa_data.workdept pk_dept");
			for (AppendableVO item : items) {
				strSql.append(",isnull(sum(");
				strSql.append((String) item.getAttributeValue("computerule"));
				strSql.append("),0) realvalue");
				strSql.append((String) item.getAttributeValue(BudgetItemVO.PK_BUDGET_ITEM));
			}
			strSql.append(" FROM wa_data,wa_periodstate,wa_period,wa_waclass WHERE  ");
			strSql.append(" wa_data.workorg = '" + pk_org + "' ");
			strSql.append(" and wa_data.workdept in (" + inSQLCreator.getInSQL(pk_depts) + ") ");

			strSql.append(" and wa_data.pk_wa_class=wa_waclass.pk_wa_class and wa_waclass.collectflag='N' and wa_waclass.mutipleflag = 'N' "
					+ " and wa_data.pk_wa_class= wa_periodstate.pk_wa_class"
					+ " and wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
					+ " and wa_data.cyear= wa_period.cyear"
					+ " and wa_data.cperiod=wa_period.cperiod and wa_period.caccyear='");
			strSql.append(accYear);
			if (sumtype == SalaryPmtCommonValue.CURRPERIOD) {
				strSql.append("' and wa_period.caccperiod='");
				strSql.append(accPeriod);
			} else if (sumtype == SalaryPmtCommonValue.ACUMULATE_TO_CURRPERIOD) {
				strSql.append("' and wa_period.caccperiod<='");
				strSql.append(accPeriod);
			}
			strSql.append("' and wa_data.checkflag = 'Y' ");

			strSql.append(" group by wa_data.workdept ");

			AppendableVO[] vos = executeQueryAppendableVOs(strSql.toString(), AppendableVO.class);
			if (vos == null) {
				return null;
			}
			AppendableVO[] itemvos = null;
			for (AppendableVO vo : vos) {
				// String pk_dept = vo.getAttributeValue("pk_dept").toString();
				for (AppendableVO item : items) {
					String pk_budgetItem = (String) item.getAttributeValue(BudgetItemVO.PK_BUDGET_ITEM);
					AppendableVO tvo = new AppendableVO();
					tvo.setAttributeValue("pk_dept", vo.getAttributeValue("pk_dept").toString());
					tvo.setAttributeValue("realvalue", vo.getAttributeValue("realvalue" + pk_budgetItem));
					tvo.setAttributeValue("pk_budgetItem", item.getAttributeValue(BudgetItemVO.PK_BUDGET_ITEM));
					itemvos = (AppendableVO[]) ArrayUtils.add(itemvos, tvo);
				}
			}
			return itemvos;

		} finally {

		}

	}

	/**
	 * 计算当前选中没有审核的人员数据
	 * 
	 * @author liangxr on 2010-7-23
	 * @param cacuItem
	 * @param whereStr
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, BigDecimal> getDeptTmSelected(String cacuItem, String whereStr) throws DAOException {

		StringBuffer strSql = new StringBuffer();
		strSql.append("SELECT sum(");
		strSql.append(cacuItem);
		strSql.append("),wa_data.workdept  FROM wa_data,wa_waclass where ");
		strSql.append(whereStr);
		strSql.append(" group by wa_data.workdept");
		List<Object[]> list = (List<Object[]>) getBaseDao().executeQuery(strSql.toString(), new ArrayListProcessor());
		Map<String, BigDecimal> resultMap = new HashMap<String, BigDecimal>();
		for (Object[] obj : list) {
			BigDecimal bigdecimal = new BigDecimal(0);
			if (obj[0] != null) {
				bigdecimal = new BigDecimal(obj[0].toString());
			}

			resultMap.put((String) obj[1], bigdecimal);
		}
		return resultMap;
	}

	// shenliangc 20140826 时点薪资计算保存之后清除主界面对应人员的发放数据计算标志
	public void updateCalFlag4OnTime(String pk_wa_class, String cyear, String cperiod, String[] pk_psndocs)
			throws BusinessException {
		// 清除计算并保存时点薪资人员的薪资发放数据计算标志
		String updateWaDataSql = "update wa_data set wa_data.caculateflag = 'N' where " + " wa_data.pk_wa_class = '"
				+ pk_wa_class + "' and " + " wa_data.cyear = '" + cyear + "' and " + " wa_data.cperiod = '" + cperiod
				+ "' and " + " wa_data.pk_psndoc in (" + SQLHelper.joinToInSql(pk_psndocs, -1) + ") and "
				+ " wa_data.checkflag = 'N' ";

		this.executeSQLs(updateWaDataSql);

		// 更新薪资方案期间状态计算标志
		String updatePeriodStateSql = "update wa_periodstate set wa_periodstate.caculateflag = 'N' where "
				+ " wa_periodstate.pk_wa_class = '" + pk_wa_class + "' and " + " wa_periodstate.pk_wa_period in "
				+ " ( " + "	select pk_wa_period from wa_period where cyear = '" + cyear + "' and cperiod = '" + cperiod
				+ "' and "
				+ " 		pk_periodscheme in ( select pk_periodscheme from wa_waclass where wa_waclass.pk_wa_class = '"
				+ pk_wa_class + "')" + " ) " + " and " + " wa_periodstate.checkflag = 'N' ";

		this.executeSQLs(updatePeriodStateSql);
	}

	// 20151031 shenliangc 薪资发放项目修改数据来源保存的同时更新发放项目权限数据。
	// 手工输入——》手工输入，非手工输入——》非手工输入，项目权限数据保持不变；
	// 非手工输入——》手工输入，手工输入——》非手工输入，如果修改后为手工输入，则可编辑权更新为Y，否则更新为N。
	// 非手工输入——》手工输入，还要将wa_data中的明细数据更新为初始值。
	public void clearPaydataByClassitem(WaClassItemVO vo) throws DAOException {
		Integer itemType = vo.getIitemtype();
		String value = "null";
		if (itemType.equals(TypeEnumVO.FLOATTYPE.value())) {
			value = "0";
		}
		// 这里只考虑了普通方案的情况，可能还需要处理集团方案项目修改、多次方案发放项目修改的情况。
		String updateSql = "update wa_data set wa_data." + vo.getItemkey() + " = " + value + " where "
				+ " (wa_data.pk_wa_class = '" + vo.getPk_wa_class() + "' or "
				+ " wa_data.pk_wa_class in (select pk_parentclass from wa_inludeclass where pk_childclass = '"
				+ vo.getPk_wa_class() + "') or "
				+ " wa_data.pk_wa_class in (select pk_childclass from wa_inludeclass where pk_parentclass = '"
				+ vo.getPk_wa_class() + "')) and " + " wa_data.cyear = '" + vo.getCyear() + "' and "
				+ " wa_data.cperiod = '" + vo.getCperiod() + "' and " +
				// 20151121 shenliangc NCdp205546984 薪资发放多次发放后，第一次的薪资项目变为了0值
				" wa_data.checkflag = 'N' ";

		this.getBaseDao().executeUpdate(updateSql);
	}

}