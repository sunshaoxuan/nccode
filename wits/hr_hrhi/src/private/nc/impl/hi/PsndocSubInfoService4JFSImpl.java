package nc.impl.hi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.impl.hr.pub.Calculator;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.itf.hi.PsndocDefUtil;
import nc.itf.twhr.IGroupinsuranceMaintain;
import nc.itf.twhr.INhiCalcGenerateSrv;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.twhr.IRangetablePubQuery;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.psn.PsnjobVO;
import nc.vo.hi.psndoc.PsndocDefVO;
import nc.vo.ml.LanguageVO;
import nc.vo.ml.MultiLangContext;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.twhr.basedoc.BaseDocVO;
import nc.vo.twhr.groupinsurance.CalcModelEnum;
import nc.vo.twhr.groupinsurance.GroupInsuranceSettingVO;
import nc.vo.twhr.nhicalc.NhiCalcVO;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;
import nc.vo.twhr.rangetable.RangeLineVO;
import nc.vo.twhr.rangetable.RangeTableAggVO;
import nc.vo.twhr.rangetable.RangeTableTypeEnum;

import org.apache.commons.lang.StringUtils;

public class PsndocSubInfoService4JFSImpl implements IPsndocSubInfoService4JFS {
	private BaseDAO baseDao;
	// 級距表
	private RangeTableAggVO[] rangeTables = null;
	private boolean baseOnAverageSalary = false;

	private RangeTableAggVO[] getRangeTables(String pk_org, String cyear, String cperiod) throws BusinessException {
		if (rangeTables == null) {
			IRangetablePubQuery qry = (IRangetablePubQuery) NCLocator.getInstance().lookup(IRangetablePubQuery.class);

			rangeTables = qry.queryRangetableByType(pk_org, -1, this.getFirstDayOfMonth(cyear, cperiod));
		}
		return rangeTables;
	}

	private RangeTableAggVO[] getRangeTables(String pk_org, UFDate date) throws BusinessException {
		if (rangeTables == null) {
			IRangetablePubQuery qry = (IRangetablePubQuery) NCLocator.getInstance().lookup(IRangetablePubQuery.class);

			rangeTables = qry.queryRangetableByType(pk_org, -1, date);
		}
		return rangeTables;
	}

	private UFDate getFirstDayOfMonth(String year, String month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(year), Integer.valueOf(month) - 1, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return new UFDate(calendar.getTime()).asBegin();
	}

	private RangeLineVO findRangeLine(RangeTableAggVO[] rtAggVOs, int rangeType, UFDouble salAmount) {
		for (RangeTableAggVO agg : rtAggVOs) {
			if (agg.getParentVO().getTabletype().equals(rangeType)) {
				RangeLineVO[] lines = (RangeLineVO[]) agg.getChildren(RangeLineVO.class);
				for (RangeLineVO line : lines) {
					UFDouble stdUpperValue = ((RangeLineVO) line).getRangeupper();
					if (stdUpperValue == null || stdUpperValue.equals(UFDouble.ZERO_DBL)) {
						stdUpperValue = new UFDouble(Double.MAX_VALUE);
					}
					UFDouble stdLowerValue = ((RangeLineVO) line).getRangelower();
					if (stdLowerValue == null) {
						stdLowerValue = UFDouble.ZERO_DBL;
					}
					if (salAmount.doubleValue() >= stdLowerValue.doubleValue()
							&& salAmount.doubleValue() <= (stdUpperValue.toDouble() == 0 ? Double.MAX_VALUE
									: stdUpperValue.doubleValue())) {
						if ((RangeTableTypeEnum.LABOR_RANGETABLE.toIntValue() == rangeType && Integer.valueOf(line
								.getRangegrade()) == 30)
								|| RangeTableTypeEnum.LABOR_RANGETABLE.toIntValue() != rangeType) {
							return (RangeLineVO) line;
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public void calculateGroupIns(String pk_org, String pk_wa_class, String cYear, String cPeriod)
			throws BusinessException {
		// 取得计算年月（未审核当前期间）
		Map periodMap = getCalculatePeriod(pk_org, cYear, cPeriod);

		String cyear = (String) periodMap.get("cyear");
		String cperiod = (String) periodMap.get("cperiod");
		String cstartdate = (String) periodMap.get("cstartdate");
		String cenddate = (String) periodMap.get("cenddate");

		// 按組織、期間取本次生成人員列表
		String pk_psndocs = getGroupInsurancePsnList(pk_org, cyear, cperiod);

		// 取人员子集设定
		Map<String, Map<String, GroupInsSettingVO>> groupSettings = getGroupSettings(cstartdate, cenddate, pk_psndocs,
				pk_org, cyear, cperiod);

		Map<String, String[]> groupInsRatePair = new HashMap<String, String[]>();
		// 取团保费率表
		getGroupInsuranceSettings(groupSettings, groupInsRatePair);

		// 計算團保 <pk_psndoc, GroupInsDetailVO>
		Map<String, GroupInsDetailVO> calcRst = calculateGroupInsurance(pk_org, cstartdate, cenddate, groupSettings,
				groupInsRatePair);

		// 回写团保明细子集
		writeBackPsndoc(calcRst);

		// 清空薪資發放已計算標記
		clearWaDataCalculateFlag(pk_org, pk_wa_class, cyear, cperiod, calcRst);
	}

	private void clearWaDataCalculateFlag(String pk_org, String pk_wa_class, String cyear, String cperiod,
			Map<String, GroupInsDetailVO> calcRst) throws DAOException {
		if (calcRst.size() > 0) {
			for (Entry<String, GroupInsDetailVO> rst : calcRst.entrySet()) {
				String pk_psndoc = rst.getKey();
				String strSQL = "update wa_data set caculateflag='N' where pk_org='" + pk_org + "' and pk_psndoc='"
						+ pk_psndoc + "' and pk_wa_class = '" + pk_wa_class + "' and cyear='" + cyear
						+ "' and cperiod='" + cperiod + "' and dr=0";
				this.getBaseDao().executeUpdate(strSQL);
			}
		}
	}

	private Map getCalculatePeriod(String pk_org, String cYear, String cPeriod) throws DAOException, BusinessException {
		String strSQL = "select cyear, cperiod, cstartdate, cenddate from wa_period where pk_wa_period in"
				+ " (select pk_wa_period from wa_periodstate where " + " enableflag='Y' and isapproved='N' "
				+ " and pk_org = '" + pk_org + "') and cyear = '" + cYear + "' and cperiod = '" + cPeriod + "'";

		Map periodMap = (Map) this.getBaseDao().executeQuery(strSQL, new MapProcessor());
		if (periodMap == null || periodMap.size() == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
					"068J61035-0011")/* @res 不存在要计算团保的薪资期间 */);
		}
		return periodMap;
	}

	private String getGroupInsurancePsnList(String pk_org, String cyear, String cperiod) throws BusinessException {
		List<String> psnList = findPersonList(pk_org, cyear, cperiod);
		if (psnList == null || psnList.size() == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
					"068J61035-0012")/* @res 不存在要计算团保的员工 */);
		}
		String pk_psndocs = "";
		for (String pk_psndoc : psnList) {
			if (StringUtils.isEmpty(pk_psndocs)) {
				pk_psndocs = "'" + pk_psndoc + "'";
			} else {
				pk_psndocs = pk_psndocs + ",'" + pk_psndoc + "'";
			}
		}
		return pk_psndocs;
	}

	private void getGroupInsuranceSettings(Map<String, Map<String, GroupInsSettingVO>> groupSettings,
			Map<String, String[]> groupInsRatePair) {
		if (groupSettings.size() > 0) {
			for (Entry<String, Map<String, GroupInsSettingVO>> psnSet : groupSettings.entrySet()) {
				if (psnSet.getValue().size() > 0) {
					for (Entry<String, GroupInsSettingVO> psnSubSet : psnSet.getValue().entrySet()) {
						String[] pair = new String[2]; // 險種:身份組合
						pair[0] = psnSubSet.getValue().getPk_GroupInsurance();
						pair[1] = psnSubSet.getValue().getPk_RelationType();
						if (!groupInsRatePair.containsKey(pair[0] + pair[1])) {
							groupInsRatePair.put(pair[0] + pair[1], pair);
						}
					}
				}
			}
		}
	}

	private void writeBackPsndoc(Map<String, GroupInsDetailVO> calcRst) throws BusinessException, DAOException {
		if (calcRst.size() > 0) {
			SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
			service.setLazyLoad(true);
			for (Entry<String, GroupInsDetailVO> rst : calcRst.entrySet()) {
				String strSQL = "delete from " + PsndocDefTableUtil.getGroupInsuranceDetailTablename()
						+ " where pk_psndoc='" + rst.getValue().getPk_psndoc() + "' and begindate='"
						+ rst.getValue().getdStartDate() + "' and enddate='" + rst.getValue().getdEndDate() + "'";
				this.getBaseDao().executeUpdate(strSQL);

				PsndocDefVO defVO = PsndocDefUtil.getGroupInsuranceDetailVO();
				defVO.setBegindate(rst.getValue().getdStartDate());
				defVO.setEnddate(rst.getValue().getdEndDate());
				defVO.setPk_psndoc(rst.getKey());
				defVO.setRecordnum(0);
				defVO.setLastflag(UFBoolean.TRUE);
				defVO.setDr(0);
				defVO.setAttributeValue("glbdef1", rst.getValue().getiStuffPay());
				defVO.setAttributeValue("glbdef2", rst.getValue().getiCompanyPay());
				service.insert(defVO);

				// 重新整理recordnum
				strSQL = getRecountRecordnumSQL(PsndocDefTableUtil.getGroupInsuranceDetailTablename());
				strSQL += " where pk_psndoc='" + rst.getValue().getPk_psndoc() + "'";
				this.getBaseDao().executeUpdate(strSQL);
			}
		}
	}

	private String getRecountRecordnumSQL(String tableName) throws BusinessException {
		String strSQL;
		strSQL = "";
		strSQL += " UPDATE ";
		strSQL += tableName;
		strSQL += " SET ";
		strSQL += "     recordnum = ";
		strSQL += "     ( ";
		strSQL += "         SELECT ";
		strSQL += "             rowno ";
		strSQL += "         FROM ";
		strSQL += "             ( ";
		strSQL += "                 SELECT ";
		strSQL += "                     pk_psndoc, ";
		strSQL += "                     pk_psndoc_sub, ";
		strSQL += "                     begindate, ";
		strSQL += "                     ( ";
		strSQL += "                         SELECT ";
		strSQL += "                             COUNT(*) ";
		strSQL += "                         FROM ";
		strSQL += "                             " + tableName + " def ";
		strSQL += "                         WHERE ";
		strSQL += "                             def.pk_psndoc=" + tableName + ".pk_psndoc) - row_number() over ";
		strSQL += "                     (partition BY pk_psndoc ORDER BY begindate) rowno ";
		strSQL += "                 FROM ";
		strSQL += "                     " + tableName + ") tmp ";
		strSQL += "         WHERE ";
		strSQL += "             tmp.pk_psndoc_sub=" + tableName + ".pk_psndoc_sub) ";
		return strSQL;
	}

	private Map<String, GroupInsDetailVO> calculateGroupInsurance(String pk_org, String cstartdate, String cenddate,
			Map<String, Map<String, GroupInsSettingVO>> groupSettings, Map<String, String[]> groupInsRatePair)
			throws BusinessException {
		IGroupinsuranceMaintain srv = NCLocator.getInstance().lookup(IGroupinsuranceMaintain.class);
		GroupInsuranceSettingVO[] setVOs = srv.queryByCondition(pk_org, groupInsRatePair.values());

		// 计算团保费用
		Map<String, GroupInsDetailVO> calcRst = new HashMap<String, GroupInsDetailVO>();
		if (groupSettings != null && groupSettings.size() > 0 && setVOs != null && setVOs.length > 0) {
			for (Entry<String, Map<String, GroupInsSettingVO>> grpSets : groupSettings.entrySet()) {
				String pk_psndoc = grpSets.getKey();
				for (Entry<String, GroupInsSettingVO> psnSet : grpSets.getValue().entrySet()) {
					GroupInsuranceSettingVO setting = getGroupInsSetting(setVOs, psnSet.getValue()
							.getPk_GroupInsurance(), psnSet.getValue().getPk_RelationType());
					UFDouble psn_sub_amount = UFDouble.ZERO_DBL;
					// 根據加退保日期判斷是否計算
					// UFLiteralDate curMonthStart = new
					// UFLiteralDate(cstartdate);
					UFLiteralDate curMonthEnd = new UFLiteralDate(cenddate);
					// 2017-12新規：只要包含本月最後一天就計算全月保費
					// 刪除結束日期判斷  只要開始日期包含在本月最後一天之前   by George 20190620  缺陷Bug #27974
					boolean isCalc = (psnSet.getValue().getdStartDate().isSameDate(curMonthEnd) || psnSet.getValue()
							.getdStartDate().before(curMonthEnd));

					if (isCalc) // 計算全月start
					{
						if (setting != null) // 按團保險種計算start
						{
							UFDouble salaryBase = (psnSet.getValue().getiSalaryBase() == null || psnSet.getValue()
									.getiSalaryBase().equals(UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : psnSet
									.getValue().getiSalaryBase();
							UFDouble times = (setting.getDtimes() == null || setting.getDtimes().equals(
									UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : setting.getDtimes();
							UFDouble upper = (setting.getDupper() == null || setting.getDupper().equals(
									UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : setting.getDupper();
							UFDouble rate = (setting.getDrate() == null || setting.getDrate().equals(UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL
									: setting.getDrate();
							if (CalcModelEnum.FIXAMOUNT.equalsValue(setting.getIcalmode())) // 計算方式start
							{
								// 定額
								// 即為所訂之金額
								psn_sub_amount = (setting.getDfixamount() == null || setting.getDfixamount().equals(
										UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : setting.getDfixamount();
							} else if (CalcModelEnum.SALARYBASE.equalsValue(setting.getIcalmode())) {
								// 保薪基數
								// 計算方式為保薪基數*倍數，如小於等於上限，則使用(保薪基數*倍數)*費率
								psn_sub_amount = salaryBase.multiply(times).doubleValue() > upper.doubleValue() ? upper
										.multiply(rate) : salaryBase.multiply(times).multiply(rate);
							} else if (CalcModelEnum.FORMULAR.equalsValue(setting.getIcalmode())) {
								// 公式
								String formular = setting.getCformularstr();
								// iif( glbdef6 <= 200000 , 200000 , glbdef6 *
								// 200000)
								formular = formular.replace("glbdef6", salaryBase.toString());
								psn_sub_amount = Calculator.evalExp(formular);
							} // 計算方式end

							if (!calcRst.containsKey(pk_psndoc)) {
								calcRst.put(pk_psndoc, new GroupInsDetailVO());
								calcRst.get(pk_psndoc).setPk_org(pk_org);
								calcRst.get(pk_psndoc).setPk_psndoc(pk_psndoc);
								calcRst.get(pk_psndoc).setdStartDate(new UFLiteralDate(cstartdate));
								calcRst.get(pk_psndoc).setdEndDate(new UFLiteralDate(cenddate));
								calcRst.get(pk_psndoc).setiStuffPay(UFDouble.ZERO_DBL);
								calcRst.get(pk_psndoc).setiCompanyPay(UFDouble.ZERO_DBL);
							}

							if (setting.getBselfpay().booleanValue()) {
								calcRst.get(pk_psndoc).setiStuffPay(
										calcRst.get(pk_psndoc).getiStuffPay()
												.add(psn_sub_amount.setScale(0, UFDouble.ROUND_HALF_UP))); // 個人負擔, MOD 增加四捨五入 by ssx on 2019-06-05
							} else {
								calcRst.get(pk_psndoc).setiCompanyPay(
										calcRst.get(pk_psndoc).getiCompanyPay()
												.add(psn_sub_amount.setScale(0, UFDouble.ROUND_HALF_UP))); // 公司負擔, MOD 增加四捨五入 by ssx on 2019-06-05
							}
						} else {
							throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
									"twhr_personalmgt", "068J61035-0013")/*
																		 * @res
																		 * 未找到團保險種檔案
																		 */);
						}// 按團保險種計算end
					}// 計算全月end
				}
			}
		}
		return calcRst;
	}

	private GroupInsuranceSettingVO getGroupInsSetting(GroupInsuranceSettingVO[] setVOs, String pk_GroupInsurance,
			String pk_PsnType) {
		for (GroupInsuranceSettingVO vo : setVOs) {
			if (vo.getCgrpinsid().equals(pk_GroupInsurance) && vo.getCgrpinsrelid().equals(pk_PsnType)) {
				return vo;
			}
		}

		return null;
	}

	private Map<String, Map<String, GroupInsSettingVO>> getGroupSettings(String cstartdate, String cenddate,
			String pk_psndocs, String pk_org, String cYear, String cPeriod) throws BusinessException {
		Map<String, Map<String, GroupInsSettingVO>> groupSettings = new HashMap<String, Map<String, GroupInsSettingVO>>();

		String strSQL = "select begindate, isnull(enddate, '9999-12-31 23:59:59') enddate, pk_psndoc, pk_psndoc_sub,"
				+ "glbdef1 name, glbdef2 id, glbdef3 birthday, glbdef4 pk_reltype, "
				+ "glbdef5 pk_groupins, isnull(glbdef6, 0) salarybase, glbdef7 isend from "
				+ PsndocDefTableUtil.getGroupInsuranceTablename()
				+ " where begindate <= '"
				+ cenddate
				+ "' "
				+ " and isnull(enddate, '9999-12-31 23:59:59') >= '"
				+ cstartdate
				+ "' and glbdef1 is not null "
				+ " and glbdef2 is not null "
				+ " and glbdef4 is not null "
				+ " and glbdef5 is not null "
				+ " and glbdef7 <> 'Y'"
				+ " and pk_psndoc in ("
				+ pk_psndocs
				+ ")"
				+ " and pk_psndoc not in ( select distinct pk_psndoc from wa_data wd "
				+ " inner join wa_waclass wc on wd.pk_wa_class = wc.pk_wa_class "
				+ "  inner join wa_periodstate  wp on wc.pk_wa_class = wp.pk_wa_class "
				+ " where wc.isapporve = 'Y' and wc.pk_org = '"
				+ pk_org
				+ "' and wp.isapproved='Y' and wd.cyear = '"
				+ cYear + "' and wd.cperiod = '" + cPeriod + "' )";
		List<Map> grpinsSetting = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());

		if (grpinsSetting != null && grpinsSetting.size() != 0) {
			for (Map psnSetting : grpinsSetting) {
				String pk_psndoc = (String) psnSetting.get("pk_psndoc");
				String pk_psndoc_sub = (String) psnSetting.get("pk_psndoc_sub");
				if (!groupSettings.containsKey(pk_psndoc)) {
					groupSettings.put(pk_psndoc, new HashMap<String, GroupInsSettingVO>());
				}

				if (!groupSettings.get(pk_psndoc).containsKey(pk_psndoc_sub)) {
					groupSettings.get(pk_psndoc).put(pk_psndoc_sub, new GroupInsSettingVO());
				}

				GroupInsSettingVO vo = groupSettings.get(pk_psndoc).get(pk_psndoc_sub);
				vo.setdStartDate(new UFLiteralDate((String) psnSetting.get("begindate")));
				vo.setdEndDate(new UFLiteralDate((String) psnSetting.get("enddate")));
				vo.setsName((String) psnSetting.get("name"));
				vo.setsID((String) psnSetting.get("id"));
				vo.setdBirthday(new UFLiteralDate((String) psnSetting.get("birthday")));
				vo.setPk_GroupInsurance((String) psnSetting.get("pk_groupins"));
				vo.setPk_RelationType((String) psnSetting.get("pk_reltype"));
				vo.setiSalaryBase(new UFDouble((BigDecimal) psnSetting.get("salarybase")));
				vo.setbIsEnd(UFBoolean.valueOf((String) psnSetting.get("isend")));
			}
		}
		return groupSettings;
	}

	@Override
	public void renewRange(String pk_org, String cyear, String cperiod) throws BusinessException {
		// 按組織、期間取本次生成人員列表
		List<String> psnList = findPersonList(pk_org, cyear, cperiod);

		// 取級距表
		this.getRangeTables(pk_org, cyear, cperiod);

		// 按組織期間取定調薪與勞健保設定對比資料
		INhiCalcGenerateSrv srv = NCLocator.getInstance().lookup(INhiCalcGenerateSrv.class);
		NhiCalcVO[] nhiFinalVOs = srv.getAdjustNHIData(psnList, pk_org, cyear, cperiod);
		// 切分有差異及定調資不為0的勞健保記錄
		StringBuilder strSQL = getUpdateSQL(nhiFinalVOs);

		// 新建勞健保資料
		PsndocDefVO[] savedVOs = getNewVOs(nhiFinalVOs, pk_org, cyear, cperiod);

		this.getBaseDao().executeUpdate(strSQL.toString());
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
		if (savedVOs != null && savedVOs.length > 0) {
			for (PsndocDefVO vo : savedVOs) {
				service.insert(vo);
			}
		}
	}

	private PsndocDefVO[] getNewVOs(NhiCalcVO[] nhiFinalVOs, String pk_org, String cyear, String cperiod)
			throws BusinessException {
		List<PsndocDefVO> psnNhiVOs = new ArrayList<PsndocDefVO>();

		if (nhiFinalVOs != null && nhiFinalVOs.length > 0) {
			for (NhiCalcVO vo : nhiFinalVOs) {
				Map<String, Object> originValues = getOriginValues(vo);
				if ((vo.getLaborrange() != null && !vo.getLaborrange().equals(vo.getOldlaborrange()) && !UFDouble.ZERO_DBL
						.equals(vo.getLaborrange()))
						|| (vo.getRetirerange() != null && !vo.getRetirerange().equals(vo.getOldretirerange()) && !UFDouble.ZERO_DBL
								.equals(vo.getRetirerange()))) {
					PsndocDefVO newLaborVO = PsndocDefUtil.getPsnLaborVO();
					newLaborVO.setPk_psndoc(vo.getPk_psndoc());
					newLaborVO.setLastflag(UFBoolean.TRUE);
					newLaborVO.setBegindate(new UFLiteralDate(vo.getBegindate().toString()));
					newLaborVO.setEnddate(originValues.get("labor_enddate") == null ? null : new UFLiteralDate(
							(String) originValues.get("labor_enddate")));
					newLaborVO.setAttributeValue("glbdef1", originValues.get("labor_glbdef1"));
					if ((vo.getLaborrange() != null && !vo.getLaborrange().equals(vo.getOldlaborrange()) && !UFDouble.ZERO_DBL
							.equals(vo.getLaborrange()))) {
						newLaborVO.setAttributeValue("glbdef2", vo.getLaborsalary());
						newLaborVO.setAttributeValue("glbdef3", UFDouble.ZERO_DBL);
						newLaborVO.setAttributeValue("glbdef4", vo.getLaborrange());
					} else {
						newLaborVO.setAttributeValue("glbdef2", originValues.get("labor_glbdef2"));
						newLaborVO.setAttributeValue("glbdef3", originValues.get("labor_glbdef3"));
						newLaborVO.setAttributeValue("glbdef4", originValues.get("labor_glbdef4"));
					}
					newLaborVO.setAttributeValue("glbdef5", originValues.get("labor_glbdef5"));
					newLaborVO.setAttributeValue("glbdef6", UFBoolean.FALSE);
					// glbdef7 勞退級距
					if (vo.getOldretirerange() == null) {
						vo.setOldretirerange(UFDouble.ZERO_DBL);
					}
					boolean isRetireRangeChanged = false;
					if (vo.getRetirerange() != null && !vo.getRetirerange().equals(vo.getOldretirerange())
							&& !UFDouble.ZERO_DBL.equals(vo.getRetirerange())) {
						newLaborVO.setAttributeValue("glbdef7", vo.getRetirerange());
						isRetireRangeChanged = true;
					} else {
						newLaborVO.setAttributeValue("glbdef7", originValues.get("labor_glbdef7"));
					}
					// glbdef8 勞退自提金額
					if (((BigDecimal) originValues.get("labor_glbdef8")).doubleValue() > 0 && isRetireRangeChanged) {
						RangeLineVO laborLine = findRangeLine(this.getRangeTables(pk_org, cyear, cperiod),
								RangeTableTypeEnum.RETIRE_RANGETABLE.toIntValue(),
								(UFDouble) newLaborVO.getAttributeValue("glbdef7"));
						newLaborVO.setAttributeValue("glbdef8", laborLine.getEmployeeamount());
					} else {
						newLaborVO.setAttributeValue("glbdef8", originValues.get("labor_glbdef8"));
					}
					newLaborVO.setAttributeValue("glbdef9", originValues.get("labor_glbdef9"));
					newLaborVO.setAttributeValue("glbdef10", originValues.get("labor_glbdef10"));
					newLaborVO.setAttributeValue("glbdef11", originValues.get("labor_glbdef11"));
					newLaborVO.setAttributeValue("glbdef12", originValues.get("labor_glbdef12"));
					newLaborVO.setAttributeValue("glbdef13", originValues.get("labor_glbdef13"));
					// 勞退投保開始日期
					newLaborVO.setAttributeValue("glbdef14", newLaborVO.getBegindate());

					psnNhiVOs.add(newLaborVO);
				}
				if (vo.getHealthrange() != null && !vo.getHealthrange().equals(vo.getOldhealthrange())
						&& !UFDouble.ZERO_DBL.equals(vo.getHealthrange())) {
					PsndocDefVO newHealVO = PsndocDefUtil.getPsnHealthVO();
					newHealVO.setPk_psndoc(vo.getPk_psndoc());
					newHealVO.setLastflag(UFBoolean.TRUE);
					newHealVO.setBegindate(new UFLiteralDate(vo.getBegindate().toString()));
					newHealVO.setEnddate(originValues.get("heal_enddate") == null ? null : new UFLiteralDate(
							(String) originValues.get("heal_enddate")));
					newHealVO.setAttributeValue("glbdef1", originValues.get("heal_glbdef1"));
					newHealVO.setAttributeValue("glbdef2", originValues.get("heal_glbdef2"));
					newHealVO.setAttributeValue("glbdef3", originValues.get("heal_glbdef3"));
					newHealVO.setAttributeValue("glbdef4", originValues.get("heal_glbdef4"));
					newHealVO.setAttributeValue("glbdef5", originValues.get("heal_glbdef5"));
					newHealVO.setAttributeValue("glbdef6", vo.getHealthsalary());
					// glbdef16 健保级距
					newHealVO.setAttributeValue("glbdef16", vo.getHealthrange());
					newHealVO.setAttributeValue("glbdef7", UFDouble.ZERO_DBL);
					newHealVO.setAttributeValue("glbdef8", originValues.get("heal_glbdef8"));
					newHealVO.setAttributeValue("glbdef9", originValues.get("heal_glbdef9"));
					newHealVO.setAttributeValue("glbdef10", originValues.get("heal_glbdef10"));
					newHealVO.setAttributeValue("glbdef11", originValues.get("heal_glbdef11"));
					newHealVO.setAttributeValue("glbdef12", originValues.get("heal_glbdef12"));
					newHealVO.setAttributeValue("glbdef13", UFBoolean.FALSE);
					newHealVO.setAttributeValue("glbdef14", originValues.get("heal_glbdef14"));
					newHealVO.setAttributeValue("glbdef15", originValues.get("heal_glbdef15"));

					psnNhiVOs.add(newHealVO);
				}
			}
		}
		return psnNhiVOs.toArray(new PsndocDefVO[0]);
	}

	private Map<String, Object> getOriginValues(NhiCalcVO vo) throws BusinessException {
		StringBuilder strSQL = new StringBuilder();
		strSQL.append("SELECT labor.enddate labor_enddate, labor.glbdef1 labor_glbdef1, labor.glbdef5 labor_glbdef5, "
				+ "		 labor.glbdef2 labor_glbdef2,labor.glbdef3 labor_glbdef3,labor.glbdef4 labor_glbdef4,labor.glbdef7 labor_glbdef7, "
				+ "       isnull(labor.glbdef8, 0) labor_glbdef8, labor.glbdef9 labor_glbdef9, labor.glbdef10 labor_glbdef10, labor.glbdef11 labor_glbdef11, "
				+ "		 labor.glbdef12 labor_glbdef12, labor.glbdef13 labor_glbdef13, heal.enddate heal_enddate, heal.glbdef1 heal_glbdef1,  "
				+ "       heal.glbdef2 heal_glbdef2, heal.glbdef3 heal_glbdef3, heal.glbdef4 heal_glbdef4,  "
				+ "       heal.glbdef5 heal_glbdef5, heal.glbdef8 heal_glbdef8, heal.glbdef9 heal_glbdef9,  "
				+ "       heal.glbdef10 heal_glbdef10, heal.glbdef11 heal_glbdef11, heal.glbdef12 heal_glbdef12, "
				+ "       heal.glbdef14 heal_glbdef14, heal.glbdef15 heal_glbdef15, heal.glbdef16 heal_glbdef16 "
				+ "FROM bd_psndoc psn LEFT OUTER JOIN " + PsndocDefTableUtil.getPsnLaborTablename()
				+ " labor ON psn.pk_psndoc = labor.pk_psndoc AND labor.begindate < '" + vo.getEnddate()
				+ "' AND isnull(labor.enddate, '9999-12-31') > '" + vo.getBegindate() + "' LEFT OUTER JOIN "
				+ PsndocDefTableUtil.getPsnHealthTablename()
				+ " heal ON psn.pk_psndoc = heal.pk_psndoc AND heal.begindate < '" + vo.getEnddate()
				+ "' AND isnull(heal.enddate, '9999-12-31') > '" + vo.getBegindate()
				+ "' AND psn.id = heal.glbdef3 WHERE (psn.pk_psndoc = '" // 身份證字號：確認只取員工自己
				+ vo.getPk_psndoc() + "')");
		return (Map<String, Object>) this.getBaseDao().executeQuery(strSQL.toString(), new MapProcessor());
	}

	private StringBuilder getUpdateSQL(NhiCalcVO[] nhiFinalVOs) throws BusinessException {
		StringBuilder strSQL = new StringBuilder();
		if (nhiFinalVOs != null && nhiFinalVOs.length > 0) {
			for (NhiCalcVO vo : nhiFinalVOs) {
				if (vo.getOldlaborrange() == null) {
					vo.setOldlaborrange(UFDouble.ZERO_DBL);
				}
				if ((!vo.getOldlaborrange().equals(vo.getLaborrange()) && !UFDouble.ZERO_DBL.equals(vo.getLaborrange()))
						|| (!vo.getOldretirerange().equals(vo.getRetirerange()) && !UFDouble.ZERO_DBL.equals(vo
								.getRetirerange()))) {
					strSQL.append(" UPDATE " + PsndocDefTableUtil.getPsnLaborTablename() + " SET enddate = '"
							+ vo.getBegindate().getDateBefore(1).asEnd().toStdString()
							+ "', modifiedtime=CONVERT([nchar](19), GETDATE(), 20), modifier='"
							+ InvocationInfoProxy.getInstance().getUserId()
							+ "', ts = CONVERT([nchar](19), GETDATE(), 20), lastflag = 'N' " + "WHERE (pk_psndoc = '"
							+ vo.getPk_psndoc() + "') AND (begindate < '" + vo.getEnddate()
							+ "') AND (isnull(enddate, '9999-12-31') > '" + vo.getBegindate() + "'); ");
				}
				if (vo.getOldhealthrange() == null) {
					vo.setOldhealthrange(UFDouble.ZERO_DBL);
				}
				if (!vo.getOldhealthrange().equals(vo.getHealthrange())
						&& !UFDouble.ZERO_DBL.equals(vo.getHealthrange())) {
					strSQL.append(" UPDATE heal "
							+ " SET  enddate='"
							+ vo.getBegindate().getDateBefore(1).asEnd().toStdString()
							+ "', lastflag='N', modifier='"
							+ InvocationInfoProxy.getInstance().getUserId()
							+ "', modifiedtime=CONVERT([nchar](19),getdate(),(20)),ts=CONVERT([nchar](19),getdate(),(20)) "
							+ " FROM "
							+ PsndocDefTableUtil.getPsnHealthTablename()
							+ " heal "
							+ " INNER JOIN bd_psndoc psn ON heal.pk_psndoc = psn.pk_psndoc AND (heal.glbdef1 = psn.name OR heal.glbdef3 = psn.id) "
							+ " WHERE heal.pk_psndoc='" + vo.getPk_psndoc() + "' AND heal.begindate < '"
							+ vo.getEnddate() + "' AND isnull(heal.enddate, '9999-12-31') > '" + vo.getBegindate()
							+ "'; ");
				}
			}
		}

		strSQL.append(" DELETE FROM " + PsndocDefTableUtil.getPsnLaborTablename()
				+ " WHERE  isnull(enddate, '9999-12-31') < begindate; ");

		strSQL.append(" DELETE FROM " + PsndocDefTableUtil.getPsnHealthTablename()
				+ " WHERE isnull(enddate, '9999-12-31') < begindate; ");
		return strSQL;
	}

	private List<String> findPersonList(String pk_org, String cYear, String cPeriod) throws BusinessException {
		String strSQL = "";
		strSQL += " SELECT distinct b.pk_psndoc ";
		strSQL += " FROM bd_psndoc b ";
		strSQL += " INNER JOIN hi_psnorg e ON b.pk_psndoc = e.pk_psndoc ";
		strSQL += " INNER JOIN ( ";
		strSQL += " 	SELECT MAX(orgrelaid) orgrelaid ";
		strSQL += " 		,pk_psndoc ";
		strSQL += " 	FROM hi_psnorg ";
		strSQL += " 	WHERE (indocflag = 'Y') ";
		strSQL += " 	GROUP BY pk_psndoc ";
		strSQL += " 	)  tmp ON e.pk_psndoc = tmp.pk_psndoc ";
		strSQL += " 	AND e.orgrelaid = tmp.orgrelaid ";
		strSQL += " INNER JOIN hi_psnjob a ON a.pk_psnorg = e.pk_psnorg ";
		strSQL += " 	AND a.lastflag = 'Y' ";
		// 判斷是否正職  by George  20190531  缺陷Bug #27249
		// strSQL += " 	AND a.ismainjob = 'Y' ";
		strSQL += " INNER JOIN org_admin_enable o ON o.pk_adminorg = a.pk_org ";
		// strSQL +=
		// " INNER JOIN om_jobrank jr ON a.jobglbdef1 = jr.pk_jobrank ";
		strSQL += " WHERE (e.indocflag = 'Y') ";
		strSQL += " 	AND (e.psntype = 0) ";
		// 判斷 hi_psnorg組織關西 是否結束  by George  20190619  缺陷Bug #27943
		// strSQL += " 	AND (e.endflag = 'N') ";
		// 判斷 hi_psnorg工作紀錄 是否結束  by George  20190619  缺陷Bug #27943
		// strSQL += " 	AND (a.endflag = 'N') ";
		strSQL += " 	AND (a.pk_hrorg = '" + pk_org + "') ";
		List psnlist = (List) this.getBaseDao().executeQuery(strSQL, new ColumnListProcessor("pk_psndoc"));
		return psnlist;
	}

	private BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

	@Override
	public void dismissPsnNHI(String pk_org, String pk_psndoc, UFLiteralDate enddate) throws BusinessException {
		String strSQL = "UPDATE " + PsndocDefTableUtil.getPsnLaborTablename() + " SET enddate='" + enddate.toString()
				+ "' WHERE ISNULL(enddate, '9999-12-31') > '" + enddate.toString() + "' AND dr=0 AND pk_psndoc = '"
				+ pk_psndoc + "'";
		this.getBaseDao().executeUpdate(strSQL);

		strSQL = "UPDATE " + PsndocDefTableUtil.getPsnLaborTablename() + " SET glbdef15='" + enddate.toString()
				+ "' WHERE ISNULL(glbdef15, '9999-12-31') > '" + enddate.toString() + "' AND dr=0 AND pk_psndoc = '"
				+ pk_psndoc + "'";
		this.getBaseDao().executeUpdate(strSQL);

		// TODO: 補充新增健保欄位及回寫邏輯
		// ssx modified on 2018-03-19
		// for changes of psn leave
		// 離職自動退保時預製退保原因別為：2.轉出
		// 退保說明別為：1.離職
		Collection defvos = (Collection) this.getBaseDao().retrieveByClause(DefdocVO.class,
				"code='2' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI005')");
		String glbdef18 = (defvos == null || defvos.size() == 0) ? "~" : ((DefdocVO) defvos.toArray()[0])
				.getPk_defdoc();
		defvos = (Collection) this.getBaseDao().retrieveByClause(DefdocVO.class,
				"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI006')");
		String glbdef19 = (defvos == null || defvos.size() == 0) ? "~" : ((DefdocVO) defvos.toArray()[0])
				.getPk_defdoc();

		strSQL = "UPDATE " + PsndocDefTableUtil.getPsnHealthTablename() + " SET enddate='" + enddate.toString()
				+ "', glbdef18='" + glbdef18 + "', glbdef19='" + glbdef19 + "' WHERE ISNULL(enddate, '9999-12-31') > '"
				+ enddate.toString() + "' AND dr=0 AND pk_psndoc = '" + pk_psndoc + "'";
		this.getBaseDao().executeUpdate(strSQL);
	}

	@Override
	public boolean isExistsGroupInsCalculateResults(String pk_psndoc, String pk_psndoc_sub) throws BusinessException {
		String strSQL = "select * from " + PsndocDefTableUtil.getGroupInsuranceDetailTablename()
				+ " where pk_psndoc = '" + pk_psndoc + "' and begindate <= (select isnull(enddate, '9999-12-31') from "
				+ PsndocDefTableUtil.getGroupInsuranceTablename() + " where pk_psndoc = "
				+ PsndocDefTableUtil.getGroupInsuranceDetailTablename() + ".pk_psndoc and pk_psndoc_sub = '"
				+ pk_psndoc_sub + "') and enddate >= (select begindate from "
				+ PsndocDefTableUtil.getGroupInsuranceTablename() + " where pk_psndoc = "
				+ PsndocDefTableUtil.getGroupInsuranceDetailTablename() + ".pk_psndoc and pk_psndoc_sub = '"
				+ pk_psndoc_sub + "')";
		IRowSet rowSet = new DataAccessUtils().query(strSQL);

		return rowSet.size() > 0;
	}

	@Override
	public void renewRangeEx(String pk_org, String[] pk_psndocs, String pk_wa_class, String startperiod,
			String endperiod, UFDate effectivedate) throws BusinessException {
		if (pk_psndocs != null && pk_psndocs.length > 0) {
			// 取級距表
			this.getRangeTables(pk_org, effectivedate);

			// 校驗薪資期間有效性
			checkPeriodValid(startperiod, endperiod);
			// 檢查是否存在生效日期以後的起始日期
			checkExistDateValid(pk_psndocs, effectivedate);

			Map<String, List<String>> periodItem = new HashMap<String, List<String>>();
			// 取指定期間有效薪資項目
			getValidWaItemByPeriod(pk_wa_class, startperiod, endperiod, periodItem);
			for (String pk_psndoc : pk_psndocs) {
				if (periodItem.size() == 0) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_personalmgt", "068J61035-0032"));
				}

				// 取指定期間平均薪資
				UFDouble avgSalary = getAvgSalary(pk_wa_class, periodItem, pk_psndoc);
				if (avgSalary == null) {
					continue;
				}

				if (avgSalary.equals(UFDouble.ZERO_DBL)) {
					IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
					PsndocVO[] vos = psnQry.queryPsndocByPks(new String[] { pk_psndoc });
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_personalmgt", "068J61035-0031", null, new String[] { vos[0].getCode() })/*
																										 * 員工
																										 * [
																										 * {
																										 * 0
																										 * }
																										 * ]
																										 * 在指定的期間範圍內沒有薪資資料
																										 * ，
																										 * 無法計算平均薪資
																										 * 。
																										 */);
				}

				List<String> updateSQLs = new ArrayList<String>();

				// 處理劳保劳退
				List<PsndocDefVO> subInfoLines = dealLaborInfoset(effectivedate, pk_psndoc, avgSalary, updateSQLs);

				// 處理健保
				dealHealInfoset(effectivedate, pk_psndoc, avgSalary, updateSQLs, subInfoLines);

				// 更新已存在記錄
				if (updateSQLs.size() > 0) {
					for (String sql : updateSQLs) {
						this.getBaseDao().executeUpdate(sql);
					}
				}

				// 回寫子集
				SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
				if (subInfoLines != null && subInfoLines.size() > 0) {
					for (PsndocDefVO vo : subInfoLines) {
						service.insert(vo);
					}
				}
			}
		}
	}

	private void checkExistDateValid(String[] pk_psndocs, UFDate effectivedate) throws BusinessException {
		IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
		String strSQL = "";

		for (String pk_psndoc : pk_psndocs) {
			if (StringUtils.isEmpty(strSQL)) {
				strSQL = "'" + pk_psndoc + "'";
			} else {
				strSQL += ", '" + pk_psndoc + "'";
			}
		}

		strSQL = "select pk_psndoc from " + PsndocDefTableUtil.getPsnLaborTablename() + " where pk_psndoc in ("
				+ strSQL + ") and begindate >= '" + effectivedate.toDate().toString() + "'";
		String existsPsn = (String) this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
		if (!StringUtils.isEmpty(existsPsn)) {
			PsndocVO[] vos = psnQry.queryPsndocByPks(new String[] { existsPsn });
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
					"068J61035-0029", null, new String[] { vos[0].getCode() })/*
																			 * 員工
																			 * [
																			 * {
																			 * 0
																			 * }
																			 * ]
																			 * 的勞保勞退信息中已存在生效日期之後的有效資料
																			 * ，
																			 * 無法進行同步作業
																			 * 。
																			 */);
		}
		strSQL = strSQL.replace(PsndocDefTableUtil.getPsnLaborTablename(), PsndocDefTableUtil.getPsnHealthTablename());
		existsPsn = (String) this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
		if (!StringUtils.isEmpty(existsPsn)) {
			PsndocVO[] vos = psnQry.queryPsndocByPks(new String[] { existsPsn });
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
					"068J61035-0030", null, new String[] { vos[0].getCode() })/*
																			 * 員工
																			 * [
																			 * {
																			 * 0
																			 * }
																			 * ]
																			 * 的健保信息中已存在生效日期之後的有效資料
																			 * ，
																			 * 無法進行同步作業
																			 * 。
																			 */);
		}
	}

	private void checkPeriodValid(String startperiod, String endperiod) throws BusinessException {
		// 校驗薪資期間有效性
		if ((new UFDate(startperiod + "-01")).after(new UFDate(endperiod + "-01"))) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
					"068J61035-0028")/* 起始期間不能晚於截止期間 */);
		}
	}

	private void dealHealInfoset(UFDate effectivedate, String pk_psndoc, UFDouble avgSalary, List<String> updateSQLs,
			List<PsndocDefVO> subInfoLines) throws BusinessException, DAOException {
		String strSQL;
		strSQL = "select * from " + PsndocDefTableUtil.getPsnHealthTablename() + " where pk_psndoc='" + pk_psndoc
				+ "' and dr=0 and begindate <= '" + effectivedate.toString()
				+ "' and isnull(enddate, '9999-12-31') >='" + effectivedate.toString() + "' and glbdef14='Y'";
		List<Map> healResult = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());
		// MOD 原始級距設在這裡剛好隔開每個家庭,每個家庭隨每一個本人而異動 by Andy on 2019-02-20
		UFDouble originHealLevel = new UFDouble();
		if (healResult != null && healResult.size() > 0) {
			List<PsndocDefVO> newSubLines = new ArrayList<PsndocDefVO>();
			for (Map healset : healResult) {
				// MOD 修改原始級距的參照,使家人的健保級距都會參照本人,隨本人而異動 by Andy on 2019-02-20
				if (healset.get("glbdef2").equals("本人")) {
					originHealLevel = new UFDouble((BigDecimal) healset.get("glbdef16"));
				}
				// UFDouble originHealLevel = healset.get("glbdef16") == null ?
				// UFDouble.ZERO_DBL
				// : new UFDouble((BigDecimal) healset.get("glbdef16"));
				RangeLineVO newRangeLine = findRangeLine(rangeTables, RangeTableTypeEnum.NHI_RANGETABLE.toIntValue(),
						avgSalary);
				UFDouble newHealLevel = newRangeLine.getRangeupper().equals(UFDouble.ZERO_DBL) ? newRangeLine
						.getRangelower().sub(1) : newRangeLine.getRangeupper();
				if (!originHealLevel.equals(newHealLevel)) {
					// 更新原有健保行
					updateSQLs.add("update " + PsndocDefTableUtil.getPsnHealthTablename() + " set enddate = '"
							+ effectivedate.getDateBefore(1).toLocalString()
							+ "', lastflag = 'N' where pk_psndoc_sub='" + (String) healset.get("pk_psndoc_sub") + "'");
					updateSQLs.add("update " + PsndocDefTableUtil.getPsnHealthTablename()
							+ " set recordnum=isnull(recordnum,0)+1 where pk_psndoc = '" + pk_psndoc + "'");
					// 新增新的健保行
					PsndocDefVO newVO = getNewSubInfoLineByOrigin(healset, effectivedate, avgSalary, newHealLevel, null);
					newSubLines.add(newVO);
				}
			}
			int rowno = newSubLines.size() - 1;
			for (PsndocDefVO vo : newSubLines) {
				vo.setRecordnum(rowno--);
			}
			subInfoLines.addAll(newSubLines);
		}
	}

	private List<PsndocDefVO> dealLaborInfoset(UFDate effectivedate, String pk_psndoc, UFDouble avgSalary,
			List<String> updateSQLs) throws BusinessException, DAOException {
		String strSQL = "select * from " + PsndocDefTableUtil.getPsnLaborTablename() + " where pk_psndoc = '"
				+ pk_psndoc + "' and dr=0 and begindate <= '" + effectivedate.toString()
				+ "' and isnull(enddate, '9999-12-31') >='" + effectivedate.toString()
				+ "' and (glbdef10 = 'Y' or glbdef11 = 'Y')";
		Map<String, Object> laborResult = (Map<String, Object>) this.getBaseDao().executeQuery(strSQL,
				new MapProcessor());

		List<PsndocDefVO> subInfoLines = new ArrayList<PsndocDefVO>();

		if (laborResult != null && !laborResult.isEmpty()) {
			UFDouble originLaborLevel = laborResult.get("glbdef4") == null ? UFDouble.ZERO_DBL : new UFDouble(
					(BigDecimal) laborResult.get("glbdef4"));
			UFDouble originRetireLevel = laborResult.get("glbdef7") == null ? UFDouble.ZERO_DBL : new UFDouble(
					(BigDecimal) laborResult.get("glbdef7"));
			RangeLineVO newRangeLine = findRangeLine(rangeTables, RangeTableTypeEnum.LABOR_RANGETABLE.toIntValue(),
					avgSalary);
			UFDouble newLaborLevel = newRangeLine.getRangeupper().equals(UFDouble.ZERO_DBL) ? newRangeLine
					.getRangelower().sub(1) : newRangeLine.getRangeupper();
			newRangeLine = findRangeLine(rangeTables, RangeTableTypeEnum.RETIRE_RANGETABLE.toIntValue(), avgSalary);
			UFDouble newRetireLevel = newRangeLine.getRangeupper().equals(UFDouble.ZERO_DBL) ? newRangeLine
					.getRangelower().sub(1) : newRangeLine.getRangeupper();
			if (!originLaborLevel.equals(newLaborLevel) || !originRetireLevel.equals(newRetireLevel)) {
				// 更新原有劳保劳退行
				updateSQLs.add("update " + PsndocDefTableUtil.getPsnLaborTablename() + " set enddate = '"
						+ effectivedate.getDateBefore(1).toLocalString() + "', glbdef15='"
						+ effectivedate.getDateBefore(1).toLocalString() + "', lastflag = 'N' where pk_psndoc_sub='"
						+ (String) laborResult.get("pk_psndoc_sub") + "'");
				updateSQLs.add("update " + PsndocDefTableUtil.getPsnLaborTablename()
						+ " set recordnum=isnull(recordnum,0)+1 where pk_psndoc = '" + pk_psndoc + "'");
				// 新增新的劳保劳退行
				subInfoLines.add(getNewSubInfoLineByOrigin(laborResult, effectivedate, avgSalary, newLaborLevel,
						newRetireLevel));
			}
		}
		return subInfoLines;
	}

	private UFDouble getAvgSalary(String pk_wa_class, Map<String, List<String>> periodItem, String pk_psndoc)
			throws DAOException {
		String strSQL;
		String strTotelSQL = "";
		UFDouble sumRst = UFDouble.ZERO_DBL;
		if (periodItem.size() > 0) {
			for (Entry<String, List<String>> entry : periodItem.entrySet()) {
				String keys = "";
				if (entry.getValue() != null && entry.getValue().size() > 0) {
					for (String key : entry.getValue()) {
						if (StringUtils.isEmpty(keys)) {
							keys = key;
						} else {
							keys += "+" + key;
						}
					}
				}

				strSQL = "select " + keys + " nhisalary from wa_data " + " where cyear||'-'||cperiod = '"
						+ entry.getKey() + "' and pk_psndoc = '" + pk_psndoc + "' and pk_wa_class = '" + pk_wa_class
						+ "' and dr=0 ";

				if (StringUtils.isEmpty(strTotelSQL)) {
					strTotelSQL = strSQL;
				} else {
					strTotelSQL += " UNION ALL " + strSQL;
				}
			}
			Object rstObj = this.getBaseDao().executeQuery(
					"select sum(nhisalary) nhisalary from (" + strTotelSQL + ") A", new ColumnProcessor());
			if (rstObj != null) {
				sumRst = new UFDouble((BigDecimal) rstObj);
			} else {
				Logger.error("SKIP RENEW: psn group insurance salary sum is null: " + pk_psndoc);
				return null;
			}
		}
		// 平均薪资
		UFDouble avgSalary = new UFDouble(Math.round(sumRst.div(periodItem.size()).toDouble()));
		return avgSalary;
	}

	private void getValidWaItemByPeriod(String pk_wa_class, String startperiod, String endperiod,
			Map<String, List<String>> periodItem) throws DAOException {
		String strSQL = "select cyear||'-'||cperiod cyearperiod, itemkey from wa_classitem where pk_wa_item in ("
				+ " select itm.pk_wa_item from wa_item itm"
				+ " inner join twhr_waitem_30 tw on itm.pk_wa_item = tw.pk_wa_item"
				+ " where tw.isnhiitem_30 = 'Y') and pk_wa_class = '" + pk_wa_class + "' and cyear||'-'||cperiod >= '"
				+ startperiod + "' and cyear||'-'||cperiod <= '" + endperiod + "' ";
		List<Map> rst = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());

		if (rst != null && rst.size() > 0) {
			String cyearperiod = "";
			for (Map rstMap : rst) {
				cyearperiod = (String) rstMap.get("cyearperiod");
				if (!periodItem.containsKey(cyearperiod)) {
					periodItem.put(cyearperiod, new ArrayList<String>());
				}

				periodItem.get(cyearperiod).add((String) rstMap.get("itemkey"));
			}
		}
	}

	private void getValidWaItemByPeriod4GroupIns(String pk_org, String pk_wa_class, String startperiod,
			String endperiod, Map<String, List<String>> periodItem) throws DAOException {
		String strSQL = "select cyear||'-'||cperiod cyearperiod, itemkey from wa_classitem where pk_wa_item in ("
				+ " select itm.pk_wa_item from wa_item itm"
				+ " inner join twhr_basedoc tw on itm.pk_wa_item = tw.waitemvalue and tw.code like 'GRPSUM%'"
				+ " where tw.pk_org = '" + pk_org + "') and pk_wa_class = '" + pk_wa_class
				+ "' and cyear||'-'||cperiod >= '" + startperiod + "' and cyear||'-'||cperiod <= '" + endperiod + "' ";
		List<Map> rst = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());

		if (rst != null && rst.size() > 0) {
			String cyearperiod = "";
			for (Map rstMap : rst) {
				cyearperiod = (String) rstMap.get("cyearperiod");
				if (!periodItem.containsKey(cyearperiod)) {
					periodItem.put(cyearperiod, new ArrayList<String>());
				}

				periodItem.get(cyearperiod).add((String) rstMap.get("itemkey"));
			}
		}
	}

	private PsndocDefVO getNewSubInfoLineByOrigin(Map<String, Object> originValues, UFDate effectivedate,
			UFDouble avgSalary, UFDouble newMainLevel, UFDouble newRetireLevel) throws BusinessException {
		if (newRetireLevel != null) {
			PsndocDefVO newLaborVO = PsndocDefUtil.getPsnLaborVO();
			newLaborVO.setPk_psndoc((String) originValues.get("pk_psndoc"));
			newLaborVO.setLastflag(UFBoolean.TRUE);
			newLaborVO.setRecordnum(0);
			newLaborVO.setBegindate(new UFLiteralDate(effectivedate.asBegin().toString()));
			newLaborVO.setEnddate(originValues.get("enddate") == null ? null : new UFLiteralDate(originValues.get(
					"enddate").toString()));
			newLaborVO.setAttributeValue("glbdef1", originValues.get("glbdef1"));
			newLaborVO.setAttributeValue("glbdef2", avgSalary);
			newLaborVO.setAttributeValue("glbdef3", UFDouble.ZERO_DBL);
			newLaborVO.setAttributeValue("glbdef4", newMainLevel);
			newLaborVO.setAttributeValue("glbdef5", originValues.get("glbdef5"));
			newLaborVO.setAttributeValue("glbdef6", UFBoolean.FALSE);
			newLaborVO.setAttributeValue("glbdef7", newRetireLevel);
			newLaborVO.setAttributeValue("glbdef8", originValues.get("glbdef8"));
			newLaborVO.setAttributeValue("glbdef9", originValues.get("glbdef9"));
			newLaborVO.setAttributeValue("glbdef10", originValues.get("glbdef10"));
			newLaborVO.setAttributeValue("glbdef11", originValues.get("glbdef11"));
			newLaborVO.setAttributeValue("glbdef12", originValues.get("glbdef12"));
			newLaborVO.setAttributeValue("glbdef13", originValues.get("glbdef13"));
			// 勞退開始日期處理
			UFDate originStartDate = originValues.get("glbdef14") == null ? null : new UFDate(
					(String) originValues.get("glbdef14"));
			UFDate originEndDate = originValues.get("glbdef15") == null ? null : new UFDate(
					(String) originValues.get("glbdef15"));
			if (originStartDate == null || originStartDate.after(effectivedate)
					|| (originEndDate != null && originEndDate.before(effectivedate))) {
				newLaborVO.setAttributeValue("glbdef14", originStartDate);
			} else {
				newLaborVO.setAttributeValue("glbdef14", effectivedate.asBegin());
			}
			// 勞退結束日期
			newLaborVO.setAttributeValue("glbdef15", originValues.get("glbdef15"));
			//
			return newLaborVO;
		} else {
			PsndocDefVO newHealVO = PsndocDefUtil.getPsnHealthVO();
			newHealVO.setPk_psndoc((String) originValues.get("pk_psndoc"));
			newHealVO.setLastflag(UFBoolean.TRUE);
			newHealVO.setBegindate(new UFLiteralDate(effectivedate.toString()));
			newHealVO.setEnddate(originValues.get("enddate") == null ? null : new UFLiteralDate(originValues.get(
					"enddate").toString()));
			newHealVO.setAttributeValue("glbdef1", originValues.get("glbdef1"));
			newHealVO.setAttributeValue("glbdef2", originValues.get("glbdef2"));
			newHealVO.setAttributeValue("glbdef3", originValues.get("glbdef3"));
			newHealVO.setAttributeValue("glbdef4", originValues.get("glbdef4"));
			newHealVO.setAttributeValue("glbdef5", originValues.get("glbdef5"));
			newHealVO.setAttributeValue("glbdef6", avgSalary);
			// glbdef16 健保级距
			newHealVO.setAttributeValue("glbdef16", newMainLevel);
			newHealVO.setAttributeValue("glbdef7", UFDouble.ZERO_DBL);
			newHealVO.setAttributeValue("glbdef8", originValues.get("glbdef8"));
			newHealVO.setAttributeValue("glbdef9", originValues.get("glbdef9"));
			newHealVO.setAttributeValue("glbdef10", originValues.get("glbdef10"));
			newHealVO.setAttributeValue("glbdef11", originValues.get("glbdef11"));
			newHealVO.setAttributeValue("glbdef12", originValues.get("glbdef12"));
			newHealVO.setAttributeValue("glbdef13", UFBoolean.FALSE);
			newHealVO.setAttributeValue("glbdef14", originValues.get("glbdef14"));
			newHealVO.setAttributeValue("glbdef15", originValues.get("glbdef15"));
			return newHealVO;
		}
	}

	@Override
	public void generatePsnNHI(String pk_org, String[] pk_psndocs) throws BusinessException {
		// 参数检查
		if (StringUtils.isEmpty(pk_org)) {
			throw new BusinessException("組織不能為空");
		}

		if (pk_psndocs == null || pk_psndocs.length == 0) {
			throw new BusinessException("未選取生成勞健保設定的員工");
		}
		// 检查人员是否有劳健保设定
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
		for (String pk_psndoc : pk_psndocs) {
			PsndocDefVO[] vos = service.queryByCondition(PsndocDefUtil.getPsnLaborVO().getClass(), " pk_psndoc='"
					+ pk_psndoc + "' ");
			if (vos != null && vos.length > 0) {
				throw new BusinessException("已存在勞保投保設定");
			}

			vos = service
					.queryByCondition(PsndocDefUtil.getPsnHealthVO().getClass(), " pk_psndoc='" + pk_psndoc + "' ");
			if (vos != null && vos.length > 0) {
				throw new BusinessException("已存在投保健保設定");
			}
		}
		// 取人员入职日期
		IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
		// Psndoc
		PsndocVO[] psndocVOs = psnQry.queryPsndocByPks(pk_psndocs);

		for (String pk_psndoc : pk_psndocs) {
			PsnjobVO[] psnjobVos = psnQry.getOldPsnjobVO(pk_psndoc);
			UFLiteralDate earlist = new UFLiteralDate("9999-12-31");
			for (PsnjobVO psnvo : psnjobVos) {
				if (psnvo.getIndutydate().before(earlist) && psnvo.getEnddutydate() == null) {
					earlist = psnvo.getIndutydate();
				}
			}
			String cyear = String.valueOf(earlist.getYear()); // 到职日期所在年
			String cperiod = String.valueOf(earlist.getMonth()); // 到职日期所在期间
			// 取級距表
			this.getRangeTables(pk_org, cyear, cperiod);

			// 按組織期間取定調薪與勞健保設定對比資料
			INhiCalcGenerateSrv srv = NCLocator.getInstance().lookup(INhiCalcGenerateSrv.class);
			NhiCalcVO[] nhiFinalVOs = srv.getAdjustNHIData(pk_psndoc, pk_org, earlist);

			// 新建勞健保資料
			PsndocDefVO[] savedVOs = getNewVOs(nhiFinalVOs, pk_org, cyear, cperiod);

			PsndocVO psnVO = null;
			for (PsndocVO psnvo : psndocVOs) {
				if (psnvo.getPk_psndoc().equals(pk_psndoc)) {
					psnVO = psnvo;
				}
			}

			if (savedVOs != null && savedVOs.length > 0) {
				for (PsndocDefVO vo : savedVOs) {
					if (vo.getClass().equals(PsndocDefUtil.getPsnLaborVO().getClass())) {
						// 劳保
						// 是否劳保投保 glbdef10
						vo.setAttributeValue("glbdef10", UFBoolean.TRUE);
						// 劳保身份 glbdef1
						vo.setAttributeValue("glbdef1", SysInitQuery.getParaString(pk_org, "TWHR03"));
						// 是否劳退投保 glbdef11
						vo.setAttributeValue("glbdef11", UFBoolean.TRUE);

						// ssx added for 勞健退申報
						// 特殊身份別為空白、提繳身分別為1.強制提繳
						DefdocVO defvo = getHIPsnTypeDefVO((String) vo.getAttributeValue("glbdef1"));
						vo.setAttributeValue("glbdef16", null);
						vo.setAttributeValue("glbdef17", defvo != null ? defvo.getPk_defdoc() : null);

					} else if (vo.getClass().equals(PsndocDefUtil.getPsnHealthVO().getClass())) {
						// 健保
						// 投保人或眷属姓名 glbdef1
						int suffix = -1;
						LanguageVO[] langs = MultiLangContext.getInstance().getEnableLangVOs();
						for (LanguageVO lvo : langs) {
							if (lvo.getLangcode().equals("tradchn")) {
								suffix = lvo.getLangseq();
							}
						}

						String psnName = "";
						if (suffix != -1) {
							psnName = (String) psnVO.getAttributeValue(PsndocVO.NAME + String.valueOf(suffix));
						} else {
							psnName = psnVO.getName();
						}
						vo.setAttributeValue("glbdef1", psnName);
						// 称谓 glbdef2
						vo.setAttributeValue("glbdef2", "本人");
						// 身份证号码 glbdef3
						vo.setAttributeValue("glbdef3", psnVO.getId());
						// 出生日期 glbdef4
						vo.setAttributeValue("glbdef4", new UFDate(psnVO.getBirthdate().toDate()));
						// 健保身份 glbdef5
						vo.setAttributeValue("glbdef5", SysInitQuery.getParaString(pk_org, "TWHR05"));
						// 是否投保 glbdef14
						vo.setAttributeValue("glbdef14", UFBoolean.TRUE);
						// 是否所得税抚养人 glbdef15
						vo.setAttributeValue("glbdef15", UFBoolean.TRUE);

						// ssx added for 勞健退申報
						// 健保資訊自動加入健保加保原因為"到職起薪"
						Collection defvos = (Collection) this
								.getBaseDao()
								.retrieveByClause(DefdocVO.class,
										"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI004')");
						vo.setAttributeValue("glbdef17", (defvos == null || defvos.size() == 0) ? "~"
								: ((DefdocVO) defvos.toArray()[0]).getPk_defdoc());
					}
				}
			}

			if (savedVOs != null && savedVOs.length > 0) {
				for (PsndocDefVO vo : savedVOs) {
					vo.setBegindate(earlist);
					vo.setEnddate(new UFLiteralDate("9999-12-31"));
					vo.setRecordnum(0);
					service.insert(vo);
				}
			}
		}
	}

	Map<String, DefdocVO> defMap = new HashMap<String, DefdocVO>();

	private DefdocVO getHIPsnTypeDefVO(String pk_psntype) throws BusinessException {
		// ssx added for 勞健退申報
		// 提繳身份別
		if (!defMap.containsKey(pk_psntype)) {
			Collection defvos = (Collection) this.getBaseDao().retrieveByClause(DefdocVO.class,
					"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI003')");

			if (defvos != null && defvos.size() > 0) {
				defMap.put(pk_psntype, ((DefdocVO) defvos.toArray()[0]));
			}
		}

		return defMap.get(pk_psntype);
	}

	@Override
	public void generateGroupIns(String pk_org, String[] pk_psndocs) throws BusinessException {
		// 参数检查
		if (StringUtils.isEmpty(pk_org)) {
			throw new BusinessException("組織不能為空");
		}

		if (pk_psndocs == null || pk_psndocs.length == 0) {
			throw new BusinessException("未選取生成團保設定的員工");
		}

		// 检查人员是否有團保设定
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
		for (String pk_psndoc : pk_psndocs) {
			// 查詢 hi_psndoc_glbdef7團保信息 是否有值  by George  20190614  缺陷Bug #27378
			PsndocDefVO[] vos = service.queryByCondition(PsndocDefUtil.getGroupInsuranceVO().getClass(), " pk_psndoc='"
					+ pk_psndoc + "' ");
			// 查詢 hi_psnorg組織關係 是否 第一家任職公司  by George  20190614  缺陷Bug #27378
			String strSQL = "select max(orgrelaid)-1 from hi_psnorg where  pk_psndoc='" + pk_psndoc + "' ";
			
			if (vos != null && vos.length > 0 && Integer.parseInt(String.valueOf(this.getBaseDao().executeQuery(strSQL, new ColumnProcessor()))) < 1) {
				throw new BusinessException("已存在團保投保設定");
			}
			// 如果不是第一家任職公司  by George
			if (Integer.parseInt(String.valueOf(this.getBaseDao().executeQuery(strSQL, new ColumnProcessor()))) >= 1) {
				// 查詢 上一家任職公司結束日期之後 hi_psndoc_glbdef7團保信息 是否有值  by George  20190614  缺陷Bug #27378
				PsndocDefVO[] vos2 = service.queryByCondition(PsndocDefUtil.getGroupInsuranceVO().getClass(), " pk_psndoc='"
						+ pk_psndoc + "' and (enddate is NULL or enddate > (select enddate from hi_psnorg where pk_psndoc='" + pk_psndoc + 
						"' and orgrelaid = '" + String.valueOf(this.getBaseDao().executeQuery(strSQL, new ColumnProcessor())) + "'))");
				
				if (vos2 != null && vos2.length > 0) {
					throw new BusinessException("已存在團保投保設定");
				}
			}
			
		}

		// 取團保費率表
		IGroupinsuranceMaintain settingSrv = NCLocator.getInstance().lookup(IGroupinsuranceMaintain.class);
		GroupInsuranceSettingVO[] setting = settingSrv.queryOnDuty(pk_org);

		// 取人员入职日期
		IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
		// Psndoc
		PsndocVO[] psndocVOs = psnQry.queryPsndocByPks(pk_psndocs);

		for (String pk_psndoc : pk_psndocs) {
			PsnjobVO[] psnjobVos = psnQry.getOldPsnjobVO(pk_psndoc);
			UFLiteralDate earlist = new UFLiteralDate("9999-12-31");
			for (PsnjobVO psnvo : psnjobVos) {
				if (psnvo.getIndutydate().before(earlist) && psnvo.getEnddutydate() == null) {
					earlist = psnvo.getIndutydate();
				}
			}

			PsndocVO psnVO = null;
			for (PsndocVO psnvo : psndocVOs) {
				if (psnvo.getPk_psndoc().equals(pk_psndoc)) {
					psnVO = psnvo;
				}
			}

			UFDouble grpSum = getGroupInsWadocBaseSalaryByPsnDate(pk_org, pk_psndoc, earlist);

			// 保薪基數 // MOD 取消四捨五入取整到千 by Andy on 2019-02-25
			UFDouble psnSum = grpSum;// .div(1000)
			// .setScale(0, UFDouble.ROUND_HALF_UP).multiply(1000);

			// 新建團保資料(信息)
			PsndocDefVO[] savedVOs = getNewGroupInsVO(pk_psndoc, pk_org, psnSum, setting, psnVO);

			if (savedVOs != null && savedVOs.length > 0) {
				for (PsndocDefVO vo : savedVOs) {
					vo.setBegindate(earlist);
					vo.setEnddate(null);
					service.insert(vo);
					
					// 不是第一次任職公司 之前公司保險信息的 recordnum紀錄序號 依照新建團保信息數增加序號號碼  by George  20190614  缺陷Bug #27378
					String strSQL = "update " + PsndocDefTableUtil.getGroupInsuranceTablename()
							+ " set recordnum=isnull(recordnum,0)+1 where pk_psndoc = '" + pk_psndoc + "' and enddate is not null";
					this.getBaseDao().executeUpdate(strSQL);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public UFDouble getGroupInsWadocBaseSalaryByPsnDate(String pk_org, String pk_psndoc, UFLiteralDate salaryDate)
			throws BusinessException {
		// 檢查勞健保參數是否有團保保薪基數加總項目
		List<BaseDocVO> grpWaitems = (List<BaseDocVO>) this.getBaseDao().retrieveByClause(BaseDocVO.class,
				"code like 'GRPSUM%' and pk_org='" + pk_org + "'", "code");
		if (grpWaitems == null || grpWaitems.size() == 0) {
			throw new BusinessException("請在參數設定中增加編碼為GRPSUMnn的薪資參數");
		}

		String waitems = "";
		for (BaseDocVO bdVO : grpWaitems) {
			if (StringUtils.isEmpty(waitems)) {
				waitems += "'" + bdVO.getWaitemvalue() + "'";
			} else {
				waitems += ",'" + bdVO.getWaitemvalue() + "'";
			}
		}

		String strSQL = "select sum(nmoney) grpsum from hi_psndoc_wadoc where pk_wa_item in (" + waitems
				+ ")  and pk_org= '" + pk_org + "' and pk_psndoc = '" + pk_psndoc + "' and '" + salaryDate.toString()
				+ "' between begindate and isnull(enddate, '9999-12-31') and waflag='Y' and lastflag='Y'";
		// MOD 修改外包人員同步保薪基數,定調資空值抱錯問題,即遇到外包人員的定調資為空時就不繼續同步下去 by Andy on
		// 2019-02-25
		if (!String.valueOf(this.getBaseDao().executeQuery(strSQL, new ColumnProcessor())).equals("null")) {
			// MOD 定調資不為空時,回傳保薪基數 by Andy on 2019-02-25
			UFDouble grpSum = new UFDouble(
					String.valueOf(this.getBaseDao().executeQuery(strSQL, new ColumnProcessor())));
			return grpSum;
		} // MOD 定調資為空值即回傳空物件 by Andy on 2019-02-25
		else {
			UFDouble grpSum = new UFDouble();
			return grpSum;
		}

	}

	private PsndocDefVO[] getNewGroupInsVO(String pk_psndoc, String pk_org, UFDouble psnSum,
			GroupInsuranceSettingVO[] setting, PsndocVO psndocVO) throws BusinessException {
		List<PsndocDefVO> psnNhiVOs = new ArrayList<PsndocDefVO>();
		int i = 0;
		for (GroupInsuranceSettingVO setvo : setting) {
			PsndocDefVO newGrpInsVO = PsndocDefUtil.getGroupInsuranceVO();
			newGrpInsVO.setPk_psndoc(pk_psndoc);
			newGrpInsVO.setLastflag(UFBoolean.TRUE);
			newGrpInsVO.setRecordnum(i++);
			newGrpInsVO.setLastflag(i == setting.length ? UFBoolean.TRUE : UFBoolean.FALSE);
			newGrpInsVO.setAttributeValue("glbdef1", psndocVO.getName());
			newGrpInsVO.setAttributeValue("glbdef2", psndocVO.getId());
			newGrpInsVO.setAttributeValue("glbdef3", new UFDate(psndocVO.getBirthdate().toDate()));
			newGrpInsVO.setAttributeValue("glbdef4", setvo.getCgrpinsrelid());
			newGrpInsVO.setAttributeValue("glbdef5", setvo.getCgrpinsid());
			newGrpInsVO.setAttributeValue("glbdef6", psnSum);
			newGrpInsVO.setAttributeValue("glbdef7", "N");
			psnNhiVOs.add(newGrpInsVO);
		}
		return psnNhiVOs.toArray(new PsndocDefVO[0]);
	}

	@Override
	public void dismissPsnGroupIns(String pk_org, String pk_psndoc, UFLiteralDate enddate) throws BusinessException {
		String strSQL = "UPDATE " + PsndocDefTableUtil.getGroupInsuranceTablename() + " SET enddate='"
				+ enddate.toString() + "' WHERE ISNULL(enddate, '9999-12-31') > '" + enddate.toString()
				+ "' AND dr=0 AND pk_psndoc = '" + pk_psndoc + "'";
		this.getBaseDao().executeUpdate(strSQL);
	}

	@Override
	public void renewGroupIns(String pk_org, String[] pk_psndocs, String pk_wa_class, String startperiod,
			String endperiod, UFDate effectivedate) throws BusinessException {
		if (pk_psndocs != null && pk_psndocs.length > 0) {
			// 校驗薪資期間有效性
			checkPeriodValid(startperiod, endperiod);
			// 檢查是否存在生效日期以後的起始日期
			checkExistDateValid4GroupIns(pk_psndocs, effectivedate);

			Map<String, List<String>> periodItem = new HashMap<String, List<String>>();
			// 取指定期間有效薪資項目
			getValidWaItemByPeriod4GroupIns(pk_org, pk_wa_class, startperiod, endperiod, periodItem);
			for (String pk_psndoc : pk_psndocs) {
				if (periodItem.size() == 0) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_personalmgt", "068J61035-0032"));
				}

				// 取保薪资基数
				UFDouble baseSalary = UFDouble.ZERO_DBL;
				if (baseOnAverageSalary) {
					// 取六个月的平均薪资
					baseSalary = getAvgSalary(pk_wa_class, periodItem, pk_psndoc);
					if (baseSalary == null) {
						continue;
					}

					if (baseSalary.equals(UFDouble.ZERO_DBL)) {
						IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
						PsndocVO[] vos = psnQry.queryPsndocByPks(new String[] { pk_psndoc });
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"twhr_personalmgt", "068J61035-0031", null, new String[] { vos[0].getCode() })/*
																											 * 員工
																											 * [
																											 * {
																											 * 0
																											 * }
																											 * ]
																											 * 在指定的期間範圍內沒有薪資資料
																											 * ，
																											 * 無法計算平均薪資
																											 * 。
																											 */);
					}
				} else {
					// 取定调资
					IPsndocSubInfoService4JFS salaryQry = NCLocator.getInstance().lookup(
							IPsndocSubInfoService4JFS.class);
					baseSalary = salaryQry.getGroupInsWadocBaseSalaryByPsnDate(pk_org, pk_psndoc, new UFLiteralDate(
							effectivedate.toString()));
				}

				List<String> updateSQLs = new ArrayList<String>();

				// 處理劳保劳退
				List<PsndocDefVO> subInfoLines = dealGroupInsInfoset(effectivedate, pk_psndoc, baseSalary, updateSQLs);

				// 更新已存在記錄
				if (updateSQLs.size() > 0) {
					for (String sql : updateSQLs) {
						this.getBaseDao().executeUpdate(sql);
					}
				}

				// 回寫子集
				SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
				if (subInfoLines != null && subInfoLines.size() > 0) {
					for (PsndocDefVO vo : subInfoLines) {
						service.insert(vo);
					}
				}
			}
		}
	}

	private void checkExistDateValid4GroupIns(String[] pk_psndocs, UFDate effectivedate) throws BusinessException {
		IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
		String strSQL = "";

		for (String pk_psndoc : pk_psndocs) {
			if (StringUtils.isEmpty(strSQL)) {
				strSQL = "'" + pk_psndoc + "'";
			} else {
				strSQL += ", '" + pk_psndoc + "'";
			}
		}

		strSQL = "select pk_psndoc from " + PsndocDefTableUtil.getGroupInsuranceTablename() + " where pk_psndoc in ("
				+ strSQL + ") and begindate >= '" + effectivedate.toDate().toString() + "'";
		String existsPsn = (String) this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
		if (!StringUtils.isEmpty(existsPsn)) {
			PsndocVO[] vos = psnQry.queryPsndocByPks(new String[] { existsPsn });
			throw new BusinessException("員工 [" + vos[0].getCode() + "] 的團保信息中已存在生效日期之後的有效資料，無法進行同步作業。");
		}
	}

	private List<PsndocDefVO> dealGroupInsInfoset(UFDate effectivedate, String pk_psndoc, UFDouble avgSalary,
			List<String> updateSQLs) throws BusinessException {
		String strSQL;
		List<PsndocDefVO> subInfoLines = new ArrayList<PsndocDefVO>();

		strSQL = "select * from " + PsndocDefTableUtil.getGroupInsuranceTablename() + " where pk_psndoc='" + pk_psndoc
				+ "' and dr=0 and begindate <= '" + effectivedate.toString()
				+ "' and isnull(enddate, '9999-12-31') >='" + effectivedate.toString() + "' and glbdef7='N'";
		List<Map> groupInsResult = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());

		if (groupInsResult != null && groupInsResult.size() > 0) {
			List<PsndocDefVO> newSubLines = new ArrayList<PsndocDefVO>();
			for (Map groupSet : groupInsResult) {
				UFDouble originSalaryBase = groupSet.get("glbdef6") == null ? UFDouble.ZERO_DBL : new UFDouble(
						(BigDecimal) groupSet.get("glbdef6"));
				// MOD 取消四捨五入取整到千 by Andy on 2019-02-22
				UFDouble newSalaryBase = avgSalary;// .div(1000)
				// .setScale(0, UFDouble.ROUND_HALF_UP).multiply(1000);
				if (!originSalaryBase.equals(newSalaryBase)) {
					// 更新原有團保設定行
					updateSQLs.add("update " + PsndocDefTableUtil.getGroupInsuranceTablename() + " set enddate = '"
							+ effectivedate.getDateBefore(1).toLocalString()
							+ "', lastflag = 'N' where pk_psndoc_sub='" + (String) groupSet.get("pk_psndoc_sub") + "'");
					updateSQLs.add("update " + PsndocDefTableUtil.getGroupInsuranceTablename()
							+ " set recordnum=isnull(recordnum,0)+1 where pk_psndoc = '" + pk_psndoc + "'");
					// 新增新的團保設定行
					PsndocDefVO newVO = getNewSubInfoLineByOrigin4GroupIns(groupSet, effectivedate, newSalaryBase);
					newSubLines.add(newVO);
				}
			}
			int rowno = newSubLines.size() - 1;
			for (PsndocDefVO vo : newSubLines) {
				vo.setRecordnum(rowno--);
			}
			subInfoLines.addAll(newSubLines);
		}
		return subInfoLines;
	}

	private PsndocDefVO getNewSubInfoLineByOrigin4GroupIns(Map<String, Object> originValues, UFDate effectivedate,
			UFDouble avgSalary) throws BusinessException {
		PsndocDefVO newHealVO = PsndocDefUtil.getGroupInsuranceVO();
		newHealVO.setPk_psndoc((String) originValues.get("pk_psndoc"));
		newHealVO.setLastflag(UFBoolean.TRUE);
		newHealVO.setBegindate(new UFLiteralDate(effectivedate.toString()));
		newHealVO.setEnddate(originValues.get("enddate") == null ? null : new UFLiteralDate(originValues.get("enddate")
				.toString()));
		newHealVO.setAttributeValue("glbdef1", originValues.get("glbdef1"));
		newHealVO.setAttributeValue("glbdef2", originValues.get("glbdef2"));
		newHealVO.setAttributeValue("glbdef3", originValues.get("glbdef3"));
		newHealVO.setAttributeValue("glbdef4", originValues.get("glbdef4"));
		newHealVO.setAttributeValue("glbdef5", originValues.get("glbdef5"));
		newHealVO.setAttributeValue("glbdef6", avgSalary);
		newHealVO.setAttributeValue("glbdef7", originValues.get("glbdef7"));

		return newHealVO;

	}
}