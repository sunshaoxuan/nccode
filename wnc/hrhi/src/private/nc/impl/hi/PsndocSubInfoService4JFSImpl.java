package nc.impl.hi;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.InSQLCreator;
import nc.impl.hi.psndoc.PsndocDAO;
import nc.impl.hi.utils.LegalOrgUtils;
import nc.impl.hr.pub.Calculator;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.itf.hi.PsndocDefUtil;
import nc.itf.hr.hi.InsuranceTypeEnum;
import nc.itf.twhr.IGroupgradeMaintain;
import nc.itf.twhr.IGroupinsuranceMaintain;
import nc.itf.twhr.INhiCalcGenerateSrv;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.twhr.IRangetablePubQuery;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.psn.PsnjobVO;
import nc.vo.hi.psndoc.GroupInsuranceDetailVO;
import nc.vo.hi.psndoc.GroupInsuranceRecordVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocDefVO;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.twhr.basedoc.BaseDocVO;
import nc.vo.twhr.groupinsurance.CalcModelEnum;
import nc.vo.twhr.groupinsurance.GroupInsuranceGradeVO;
import nc.vo.twhr.groupinsurance.GroupInsuranceSettingVO;
import nc.vo.twhr.nhicalc.NhiCalcUtils;
import nc.vo.twhr.nhicalc.NhiCalcVO;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;
import nc.vo.twhr.rangetable.RangeLineVO;
import nc.vo.twhr.rangetable.RangeTableAggVO;
import nc.vo.twhr.rangetable.RangeTableTypeEnum;

import org.apache.commons.lang.StringUtils;

public class PsndocSubInfoService4JFSImpl implements IPsndocSubInfoService4JFS {
	private String[] LABORINS = new String[] { "glbdef2", "glbdef3", "glbdef4", "glbdef7", "glbdef8" };
	private String[] HEALTHINS = new String[] { "glbdef6", "glbdef7", "glbdef16" };

	private BaseDAO baseDao;
	// 級距表
	private RangeTableAggVO[] rangeTables = null;
	private boolean baseOnAverageSalary = false;
	private Set<String> foreignAndTeachSet = new HashSet<>();
	// Ares.Tank 法人组织
	private String pkLegalOrg = null;

	// Ares.Tank
	private final int ADD_FORM = 1;// 投保形态-加保
	private final int DEL_FORM = 2;// 投保形态-退保
	private final int ADJ_FORM = 3;// 投保形态-保薪调整

	private RangeTableAggVO[] getRangeTables(String pk_org, String cyear, String cperiod) throws BusinessException {
		if (rangeTables == null || rangeTables.length == 0) {
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
					UFDouble stdUpperValue = line.getRangeupper();
					if (stdUpperValue == null || stdUpperValue.equals(UFDouble.ZERO_DBL)) {
						stdUpperValue = new UFDouble(Double.MAX_VALUE);
					}
					UFDouble stdLowerValue = line.getRangelower();
					if (stdLowerValue == null) {
						stdLowerValue = UFDouble.ZERO_DBL;
					}
					if (salAmount.doubleValue() >= stdLowerValue.doubleValue()
							&& salAmount.doubleValue() <= (stdUpperValue.toDouble() == 0 ? Double.MAX_VALUE
									: stdUpperValue.doubleValue())) {
						if ((RangeTableTypeEnum.LABOR_RANGETABLE.toIntValue() == rangeType && Integer.valueOf(line
								.getRangegrade()) == 30)
								|| RangeTableTypeEnum.LABOR_RANGETABLE.toIntValue() != rangeType) {
							return line;
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
		List<String> pk_psndocs = getGroupInsurancePsnList(pk_org, cyear, cperiod);

		// 取人员子集设定
		Map<String, Map<String, GroupInsSettingVO>> groupSettings = getGroupSettings(cstartdate, cenddate, pk_psndocs,
				pk_org, cyear, cperiod);

		Map<String, String[]> groupInsRatePair = new HashMap<String, String[]>();
		// 取团保费率表
		getGroupInsuranceSettings(groupSettings, groupInsRatePair);

		// 計算團保 <pk_psndoc, GroupInsDetailVO>
		Map<String, GroupInsDetailVO> calcRst = calculateGroupInsurance(pk_org, cstartdate, cenddate, groupSettings,
				groupInsRatePair);
		// 計載每月每名員工與眷屬總共需負擔之團保費，如遇以戶計算的則為平分，除不盡者找其中一個人塞餘額
		Map<String, List<FamEmpGroupInsDetailVO>> calcFEdetails = calcFEdetailGroupIns(pk_org, cstartdate, cenddate,
				groupSettings, groupInsRatePair);
		// 回写团保明细子集
		writeBackPsndoc(calcRst);
		// 回写眷属与员工团保投保明细 by he
		writeBackfamempdetail(calcFEdetails);
		// 清空薪資發放已計算標記
		clearWaDataCalculateFlag(pk_org, pk_wa_class, cyear, cperiod, calcRst);
	}

	private void writeBackfamempdetail(Map<String, List<FamEmpGroupInsDetailVO>> calcFEdetails)
			throws BusinessException, DAOException {
		if (calcFEdetails.size() > 0) {
			SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
			service.setLazyLoad(true);
			for (String rst : calcFEdetails.keySet()) {
				for (FamEmpGroupInsDetailVO famempvo : calcFEdetails.get(rst)) {
					String strSQL = "";
					strSQL += "delete from groupinsurance_detail where pk_psndoc='" + famempvo.getPk_psndoc()
							+ "' and begindate='" + famempvo.getdStartDate();
					if (null == famempvo.getdEndDate()) {
						strSQL += "' and (enddate is null or enddate = 'null' or enddate ='~')";
					} else {
						strSQL += "' and enddate='" + famempvo.getdEndDate() + "'";
					}
					strSQL += "and name = '" + famempvo.getName() + "'";
					strSQL += "and insurancecode = '" + getdefname(famempvo.getInsurancecode()) + "'";
					strSQL += "and identitycode = '" + getdefname(famempvo.getIdentitycode()) + "'";
					this.getBaseDao().executeUpdate(strSQL);
					strSQL = "update groupinsurance_detail set recordnum = recordnum +1 "
							+ " where pk_psndoc='"
							+ famempvo.getPk_psndoc()
							+ "' and exists(select * from groupinsurance_detail def where groupinsurance_detail.pk_psndoc = def.pk_psndoc and def.recordnum = 1)";
					this.getBaseDao().executeUpdate(strSQL);

					// PsndocDefVO defVO =
					// PsndocDefUtil.getFamEmpInsuranceDetailVO();
					GroupInsuranceDetailVO defVO = new GroupInsuranceDetailVO();

					defVO.setBegindate(famempvo.getdStartDate());
					defVO.setEnddate(famempvo.getdEndDate());
					defVO.setPk_psndoc(rst);
					defVO.setRecordnum(0);
					defVO.setLastflag(UFBoolean.TRUE);
					defVO.setDr(0);
					defVO.setAttributeValue("name", famempvo.getName());
					defVO.setAttributeValue("insurancecode", getdefname(famempvo.getInsurancecode()));
					defVO.setAttributeValue("identitycode", getdefname(famempvo.getIdentitycode()));
					defVO.setAttributeValue("ishousehold", famempvo.getIshousehold());
					defVO.setAttributeValue("insuranceamount", famempvo.getInsuranceamount());
					this.getBaseDao().insertVO(defVO);
				}
			}
		}

	}

	private String getdefname(String insurancecode) {
		List<DefdocVO> inlist = null;
		try {
			inlist = (List<DefdocVO>) this.getBaseDao().retrieveByClause(DefdocVO.class,
					"pk_defdoc='" + insurancecode + "'");
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return inlist.get(0).getName();
	}

	private Map<String, List<FamEmpGroupInsDetailVO>> calcFEdetailGroupIns(String pk_org, String cstartdate,
			String cenddate, Map<String, Map<String, GroupInsSettingVO>> groupSettings,
			Map<String, String[]> groupInsRatePair) throws BusinessException {
		IGroupinsuranceMaintain srv = NCLocator.getInstance().lookup(IGroupinsuranceMaintain.class);
		GroupInsuranceSettingVO[] setVOs = srv.queryByCondition(pk_org, groupInsRatePair.values());
		// 计算团保费用
		Map<String, List<FamEmpGroupInsDetailVO>> calcFEdetails = new HashMap<String, List<FamEmpGroupInsDetailVO>>();
		if (groupSettings != null && groupSettings.size() > 0 && setVOs != null && setVOs.length > 0) {
			for (Entry<String, Map<String, GroupInsSettingVO>> grpSets : groupSettings.entrySet()) {
				String pk_psndoc = grpSets.getKey();
				// 这list用来存员工和眷属的险种-身份
				List<String> idinslists = new ArrayList<String>();
				List<FamEmpGroupInsDetailVO> famemplist = new ArrayList<FamEmpGroupInsDetailVO>();
				for (Entry<String, GroupInsSettingVO> psnSet : grpSets.getValue().entrySet()) {
					FamEmpGroupInsDetailVO famempvo = new FamEmpGroupInsDetailVO();
					GroupInsuranceSettingVO setting = getGroupInsSetting(setVOs, psnSet.getValue()
							.getPk_GroupInsurance(), psnSet.getValue().getPk_RelationType());

					UFDouble psn_sub_amount = UFDouble.ZERO_DBL;
					// 根據加退保日期判斷是否計算
					// UFLiteralDate curMonthStart = new
					// UFLiteralDate(cstartdate);
					UFLiteralDate curMonthEnd = new UFLiteralDate(cenddate);
					// 2017-12新規：只要包含本月最後一天就計算全月保費
					boolean isCalc = (psnSet.getValue().getdStartDate().isSameDate(curMonthEnd) || psnSet.getValue()
							.getdStartDate().before(curMonthEnd))
							&& (psnSet.getValue().getdEndDate().isSameDate(curMonthEnd) || psnSet.getValue()
									.getdEndDate().after(curMonthEnd));

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

							if (!calcFEdetails.containsKey(pk_psndoc)) {
								famempvo.setPk_org(pk_org);
								famempvo.setPk_psndoc(pk_psndoc);
								famempvo.setdStartDate(new UFLiteralDate(cstartdate));
								famempvo.setdStartDate(new UFLiteralDate(cenddate));
								famempvo.setInsurancecode(psnSet.getValue().getPk_GroupInsurance());
								famempvo.setIdentitycode(psnSet.getValue().getPk_RelationType());
								famempvo.setIshousehold(setting.getIshousehold());
								famempvo.setName(psnSet.getValue().getsName());
								// 险种-身份
								idinslists.add(famempvo.getInsurancecode() + famempvo.getIdentitycode());
							}
							if (setting.getBselfpay().booleanValue()) {
								famempvo.setInsuranceamount(psn_sub_amount);

							} else {
								famempvo.setInsuranceamount(UFDouble.ZERO_DBL);
							}
							famemplist.add(famempvo);

						} else {
							throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
									"twhr_personalmgt", "068J61035-0013")/*
																		 * @res
																		 * 未找到團保險種檔案
																		 */);
						}// 按團保險種計算end
					}// 計算全月end
				}

				// 取出相同身份+险种的人员，将金额修改
				for (GroupInsuranceSettingVO setvo : setVOs) {
					// 包含这个险种-身份的数据就更新金额
					List<FamEmpGroupInsDetailVO> hlist = new ArrayList<FamEmpGroupInsDetailVO>();
					List<FamEmpGroupInsDetailVO> hnlist = new ArrayList<FamEmpGroupInsDetailVO>();
					for (FamEmpGroupInsDetailVO famempvo : famemplist) {
						if ((famempvo.getInsurancecode() + famempvo.getIdentitycode()).equals(setvo.getCgrpinsid()
								+ setvo.getCgrpinsrelid())) {
							hlist.add(famempvo);
						} else {
							hnlist.add(famempvo);
						}
					}

					int i = 1;
					if (hlist.size() > 0) {
						for (FamEmpGroupInsDetailVO famempvo : hlist) {
							// rem(3,2)=1

							int insuranceamount = (int) famempvo.getInsuranceamount().doubleValue();
							if (i == 1) {
								famempvo.setInsuranceamount(new UFDouble((insuranceamount / hlist.size())
										+ (insuranceamount % (hlist.size()))));
							} else {

								famempvo.setInsuranceamount(new UFDouble((insuranceamount / hlist.size())));
							}
							i++;
						}
						famemplist.clear();
						famemplist.addAll(hlist);
						famemplist.addAll(hnlist);
					}
				}
				calcFEdetails.put(pk_psndoc, famemplist);
			}
		}
		return calcFEdetails;
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

	private List<String> getGroupInsurancePsnList(String pk_org, String cyear, String cperiod) throws BusinessException {
		List<String> psnList = findPersonList(pk_org, cyear, cperiod);
		if (psnList == null || psnList.size() == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
					"068J61035-0012")/* @res 不存在要计算团保的员工 */);
		}
		return psnList;
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
				String strSQL = "delete from hi_psndoc_groupinsrecord where pk_psndoc='"
						+ rst.getValue().getPk_psndoc() + "' and begindate='" + rst.getValue().getdStartDate()
						+ "' and enddate='" + rst.getValue().getdEndDate() + "'";
				this.getBaseDao().executeUpdate(strSQL);
				strSQL = "update hi_psndoc_groupinsrecord set recordnum = recordnum +1 " + " where pk_psndoc='"
						+ rst.getValue().getPk_psndoc()
						+ "' and exists(select * from hi_psndoc_groupinsrecord def where hi_psndoc_groupinsrecord"
						+ ".pk_psndoc = def.pk_psndoc and def.recordnum = 1)";
				this.getBaseDao().executeUpdate(strSQL);

				PsndocDefVO defVO = new GroupInsuranceRecordVO();
				defVO.setBegindate(rst.getValue().getdStartDate());
				defVO.setEnddate(rst.getValue().getdEndDate());
				defVO.setPk_psndoc(rst.getKey());
				defVO.setRecordnum(0);
				defVO.setLastflag(UFBoolean.TRUE);
				defVO.setDr(0);
				defVO.setAttributeValue("stuffpay", rst.getValue().getiStuffPay());
				defVO.setAttributeValue("companypay", rst.getValue().getiCompanyPay());
				defVO.setAttributeValue("empinsurancemoney", rst.getValue().getEmpinsurancemoney());
				defVO.setAttributeValue("fayinsurancemoney", rst.getValue().getFayinsurancemoney());
				service.insert(defVO);
			}
		}
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
				// 是否以户计算
				Map<String, String> ishouse = new HashMap<String, String>();
				String pk_psndoc = grpSets.getKey();
				for (Entry<String, GroupInsSettingVO> psnSet : grpSets.getValue().entrySet()) {
					GroupInsuranceSettingVO setting = getGroupInsSetting(setVOs, psnSet.getValue()
							.getPk_GroupInsurance(), psnSet.getValue().getPk_RelationType());
					if (null == ishouse || ishouse.size() < 0
							|| !ishouse.containsKey(setting.getCgrpinsid() + "-" + setting.getCgrpinsrelid())) {

						UFDouble psn_sub_amount = UFDouble.ZERO_DBL;
						// 根據加退保日期判斷是否計算
						// UFLiteralDate curMonthStart = new
						// UFLiteralDate(cstartdate);
						UFLiteralDate curMonthEnd = new UFLiteralDate(cenddate);
						// 2017-12新規：只要包含本月最後一天就計算全月保費
						boolean isCalc = (psnSet.getValue().getdStartDate().isSameDate(curMonthEnd) || psnSet
								.getValue().getdStartDate().before(curMonthEnd))
								&& (psnSet.getValue().getdEndDate().isSameDate(curMonthEnd) || psnSet.getValue()
										.getdEndDate().after(curMonthEnd));

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
								UFDouble rate = (setting.getDrate() == null || setting.getDrate().equals(
										UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : setting.getDrate();
								if (CalcModelEnum.FIXAMOUNT.equalsValue(setting.getIcalmode())) // 計算方式start
								{
									// 定額
									// 即為所訂之金額
									psn_sub_amount = (setting.getDfixamount() == null || setting.getDfixamount()
											.equals(UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : setting.getDfixamount();
								} else if (CalcModelEnum.SALARYBASE.equalsValue(setting.getIcalmode())) {
									// 保薪基數
									// 計算方式為保薪基數*倍數，如小於等於上限，則使用(保薪基數*倍數)*費率
									psn_sub_amount = salaryBase.multiply(times).doubleValue() > upper.doubleValue() ? upper
											.multiply(rate) : salaryBase.multiply(times).multiply(rate);
								} else if (CalcModelEnum.FORMULAR.equalsValue(setting.getIcalmode())) {
									// 公式
									String formular = setting.getCformularstr();
									// iif( glbdef6 <= 200000 , 200000 , glbdef6
									// *
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
									calcRst.get(pk_psndoc).setEmpinsurancemoney(UFDouble.ZERO_DBL);
									calcRst.get(pk_psndoc).setFayinsurancemoney(UFDouble.ZERO_DBL);
								}

								if (setting.getBselfpay().booleanValue()) {
									calcRst.get(pk_psndoc).setiStuffPay(
											calcRst.get(pk_psndoc).getiStuffPay().add(psn_sub_amount)); // 個人負擔
									if (null != setting.getFamilygroupinsurance()) {
										if (setting.getFamilygroupinsurance().booleanValue()) {
											calcRst.get(pk_psndoc).setFayinsurancemoney(
													calcRst.get(pk_psndoc).getFayinsurancemoney().add(psn_sub_amount));
										} else {
											calcRst.get(pk_psndoc).setEmpinsurancemoney(
													calcRst.get(pk_psndoc).getEmpinsurancemoney().add(psn_sub_amount));
										}
									}
								} else {
									calcRst.get(pk_psndoc).setiCompanyPay(
											calcRst.get(pk_psndoc).getiCompanyPay().add(psn_sub_amount)); // 公司負擔
								}
							} else {
								PsndocDAO dao = new PsndocDAO();
								PsndocAggVO psn = dao.queryByPk(PsndocAggVO.class, grpSets.getKey());
								// (PsndocAggVO)
								// this.getBaseDao().retrieveByPK(PsndocAggVO.class,
								// grpSets.getKey());
								DefdocVO insr = (DefdocVO) this.getBaseDao().retrieveByPK(DefdocVO.class,
										psnSet.getValue().getPk_GroupInsurance());
								DefdocVO psntype = (DefdocVO) this.getBaseDao().retrieveByPK(DefdocVO.class,
										psnSet.getValue().getPk_RelationType());
								throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
										"twhr_personalmgt", "068J61035-0013")/*
																			 * @res
																			 * 未找到團保險種檔案
																			 */
										+ ": 員工 ["
										+ psn.getParentVO().getCode()
										+ "], 投保險種 ["
										+ insr.getName()
										+ "], 投保身份 [" + psntype.getName() + "]");
							}// 按團保險種計算end
						}// 計算全月end
					}
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
			List<String> pk_psndocs, String pk_org, String cYear, String cPeriod) throws BusinessException {
		Map<String, Map<String, GroupInsSettingVO>> groupSettings = new HashMap<String, Map<String, GroupInsSettingVO>>();
		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(pk_psndocs.toArray(new String[0]));

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
				// Ares.Tank 2018-9-12 11:46:21 glbdef7
				// 字段可能是null,把null作为N对待,此处业务逻辑为:
				// "有結束日期＆是否結束勾的時候要計算，結束日期為空＆是否結束沒勾的時候也計算" --by Ryan
				+ " and ((isnull(glbdef7,'N')='Y' and enddate is not null) or (isnull(glbdef7,'N')='N' and enddate is null)) "
				+ " and pk_psndoc in ("
				+ psndocsInSQL
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
				vo.setiSalaryBase(new UFDouble(SalaryDecryptUtil.decrypt(new UFDouble(String.valueOf(psnSetting
						.get("salarybase"))).doubleValue())));
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
		PsndocDefVO[] savedVOs = getNewVOs(nhiFinalVOs, pk_org, cyear, cperiod, false, true, true, true);

		this.getBaseDao().executeUpdate(strSQL.toString());
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
		if (savedVOs != null && savedVOs.length > 0) {
			for (PsndocDefVO vo : savedVOs) {
				service.insert(vo);
			}
		}
	}

	/**
	 * 
	 * @param nhiFinalVOs
	 * @param pk_org
	 * @param cyear
	 * @param cperiod
	 * @param state
	 *            是否是入职定调资--true 为加保 false:保薪调整
	 * @return
	 * @throws BusinessException
	 */

	private PsndocDefVO[] getNewVOs(NhiCalcVO[] nhiFinalVOs, String pk_org, String cyear, String cperiod,
			boolean state, boolean isGenLabor, boolean isGenRetire, boolean isGenHeal) throws BusinessException {
		List<PsndocDefVO> psnNhiVOs = new ArrayList<PsndocDefVO>();
		// 初始化外籍人员和表,用于判断人员是否需要进行劳退

		initForeignAndTeachSet(nhiFinalVOs);

		if (nhiFinalVOs != null && nhiFinalVOs.length > 0) {
			for (NhiCalcVO vo : nhiFinalVOs) {
				Map<String, Object> originValues = getOriginValues(vo);
				if ((vo.getLaborrange() != null && !vo.getLaborrange().equals(vo.getOldlaborrange()) && !UFDouble.ZERO_DBL
						.equals(vo.getLaborrange()))
						|| (vo.getRetirerange() != null && !vo.getRetirerange().equals(vo.getOldretirerange()) && !UFDouble.ZERO_DBL
								.equals(vo.getRetirerange()))) {
					if (isGenLabor || isGenRetire) {
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
							// Ares.Tank 外籍或建教人员劳退清0 2018-9-12 20:47:26
							newLaborVO.setAttributeValue(
									"glbdef7",
									foreignAndTeachSet.contains(vo.getPk_psndoc()) ? new UFDouble(0) : vo
											.getRetirerange());
							isRetireRangeChanged = true;
						} else {
							// Ares.Tank 外籍或建教人员劳退清0 2018-9-12 20:47:26
							newLaborVO.setAttributeValue(
									"glbdef7",
									foreignAndTeachSet.contains(vo.getPk_psndoc()) ? new UFDouble(0) : originValues
											.get("labor_glbdef7"));
						}
						// glbdef8 勞退自提金額
						if (new UFDouble(String.valueOf(originValues.get("labor_glbdef8"))).doubleValue() > 0
								&& isRetireRangeChanged) {
							RangeLineVO laborLine = findRangeLine(this.getRangeTables(pk_org, cyear, cperiod),
									RangeTableTypeEnum.RETIRE_RANGETABLE.toIntValue(),
									(UFDouble) newLaborVO.getAttributeValue("glbdef7"));
							// Ares.Tank 外籍或建教人员劳退清0 2018-9-12 20:47:26
							newLaborVO.setAttributeValue(
									"glbdef8",
									foreignAndTeachSet.contains(vo.getPk_psndoc()) ? new UFDouble(0) : laborLine
											.getEmployeeamount());
						} else {
							// Ares.Tank 外籍或建教人员劳退清0 2018-9-12 20:47:26
							newLaborVO.setAttributeValue(
									"glbdef8",
									foreignAndTeachSet.contains(vo.getPk_psndoc()) ? new UFDouble(0) : originValues
											.get("labor_glbdef8"));
						}
						// Ares.Tank 外籍或建教人员劳退清0 2018-9-12 20:47:26
						newLaborVO.setAttributeValue(
								"glbdef9",
								foreignAndTeachSet.contains(vo.getPk_psndoc()) ? UFBoolean.TRUE : originValues
										.get("labor_glbdef9"));
						newLaborVO.setAttributeValue("glbdef10", new UFBoolean(isGenLabor));
						// Ares.Tank 外籍或建教人员劳退清0 2018-9-12 20:47:26
						newLaborVO.setAttributeValue("glbdef11",
								foreignAndTeachSet.contains(vo.getPk_psndoc()) ? UFBoolean.FALSE : new UFBoolean(
										isGenRetire));
						newLaborVO.setAttributeValue("glbdef12", originValues.get("labor_glbdef12"));
						// Ares.Tank 外籍或建教人员劳退清0 2018-9-12 20:47:26
						newLaborVO.setAttributeValue("glbdef13", foreignAndTeachSet.contains(vo.getPk_psndoc()) ? null
								: originValues.get("labor_glbdef13"));
						// 勞退投保開始日期
						// Ares.Tank 外籍或建教人员劳退清0 2018-9-12 20:47:26
						newLaborVO.setAttributeValue("glbdef14", foreignAndTeachSet.contains(vo.getPk_psndoc()) ? null
								: newLaborVO.getBegindate());

						// Ares.Tank 2018-9-14 14:36:29 修复一个类型装换错误
						/*
						 * newLaborVO.setAttributeValue("glbdef14", new
						 * UFDate(newLaborVO.getBegindate().toDate()));
						 */
						// Ares.Tank 劳保法人组织和投保形态设设置
						newLaborVO.setAttributeValue("legalpersonorg", pkLegalOrg);
						// 按理来说,此处应该用枚举来set值的,为什么用数字呢?
						// 1.因为没有枚举类 2.hrtaovertimereg.bmf这个元数据是有点毛病
						// -->3.数字比中文更适合编码
						// 定调资--加保,同步劳保级距--保薪调整
						if (state) {
							newLaborVO.setAttributeValue("insuranceform", ADD_FORM);
						} else {
							newLaborVO.setAttributeValue("insuranceform", ADJ_FORM);
						}
						psnNhiVOs.add(newLaborVO);
					}
				}
				if (vo.getHealthrange() != null && !vo.getHealthrange().equals(vo.getOldhealthrange())
						&& !UFDouble.ZERO_DBL.equals(vo.getHealthrange())) {
					if (isGenHeal) {
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

						// Ares.Tank 劳保法人组织和投保形态设设置
						newHealVO.setAttributeValue("legalpersonorg", pkLegalOrg);
						// 按理来说,此处应该用枚举来set值的,为什么用数字呢?
						// 1.因为没有枚举类 2.hrtaovertimereg.bmf这个元数据是有点毛病
						// -->3.数字比中文更适合编码
						// // 定调资--加保,同步劳保级距--保薪调整
						if (state) {
							newHealVO.setAttributeValue("insuranceform", ADD_FORM);
						} else {
							newHealVO.setAttributeValue("insuranceform", ADJ_FORM);
						}
						psnNhiVOs.add(newHealVO);
					}
				}
			}
		}
		return psnNhiVOs.toArray(new PsndocDefVO[0]);
	}

	/**
	 * 查询外籍人员和建教人员列表
	 * 
	 * @param nhiFinalVOs
	 * @throws BusinessException
	 * @author Ares.Tank
	 * @date 2018-9-12 20:48:06
	 */
	private void initForeignAndTeachSet(NhiCalcVO[] nhiFinalVOs) throws BusinessException {
		foreignAndTeachSet = new HashSet<>();
		Set<String> pkPsndoc = new HashSet<>();
		for (NhiCalcVO vo : nhiFinalVOs) {
			pkPsndoc.add(vo.getPk_psndoc());
		}
		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(pkPsndoc.toArray(new String[0]));
		String strSQL = "SELECT psndoc FROM bd_defdoc INNER JOIN "
				+ "(  SELECT HI_PSNJOB.JOBGLBDEF8 as defdoc,hi_psnjob.PK_PSNDOC as psndoc  FROM  hi_psnjob "
				+ "WHERE HI_PSNJOB.PK_PSNDOC in ( " + psndocsInSQL
				+ ") AND lastflag = 'Y')psn ON BD_DEFDOC.PK_DEFDOC=psn.defdoc "
				+ "where (BD_DEFDOC.CODE='F' or BD_DEFDOC.CODE='G') ";
		List<Map> foreignAndTeachPsndocList = (List<Map>) this.getBaseDao()
				.executeQuery(strSQL, new MapListProcessor());
		if (foreignAndTeachPsndocList != null && foreignAndTeachPsndocList.size() != 0) {
			for (Map psnMap : foreignAndTeachPsndocList) {
				if (psnMap != null) {
					foreignAndTeachSet.add((String) psnMap.get("psndoc"));
				}
			}
		}

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
		strSQL += " 	AND a.ismainjob = 'Y' ";
		strSQL += " INNER JOIN org_admin_enable o ON o.pk_adminorg = a.pk_org ";
		// strSQL +=
		// " INNER JOIN om_jobrank jr ON a.jobglbdef1 = jr.pk_jobrank ";
		strSQL += " WHERE (e.indocflag = 'Y') ";
		strSQL += " 	AND (e.psntype = 0) ";
		strSQL += " 	AND (e.endflag = 'N') ";
		strSQL += " 	AND (a.endflag = 'N') ";
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
		if (null == enddate) {
			return;
		}
		String strSQL = "UPDATE " + PsndocDefTableUtil.getPsnLaborTablename() + " SET enddate='" + enddate.toString()
				+ "' , insuranceform = " + DEL_FORM + " WHERE ISNULL(enddate, '9999-12-31') > '" + enddate.toString()
				+ "' AND dr=0 AND pk_psndoc = '" + pk_psndoc + "'";
		this.getBaseDao().executeUpdate(strSQL);

		strSQL = "UPDATE " + PsndocDefTableUtil.getPsnLaborTablename() + " SET glbdef15='" + enddate.toString()
				+ "' WHERE ISNULL(glbdef15, '9999-12-31') > '" + enddate.toString() + "' AND dr=0 AND pk_psndoc = '"
				+ pk_psndoc + "'" + " and lastflag = 'Y'";
		this.getBaseDao().executeUpdate(strSQL);

		// TODO: 補充新增健保欄位及回寫邏輯
		// ssx modified on 2018-03-19
		// for changes of psn leave
		// 離職自動退保時預製退保原因別為：2.轉出

		// Ares.Tank mod on 2018-9-15 22:55:16
		// 新逻辑:投保狀態為退保、健保退保原因別為退保、健保退保原因說明別為離職。--Jessie

		// 退保說明別為：1.離職
		Collection defvos = this.getBaseDao().retrieveByClause(DefdocVO.class,
				"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI005')");
		String glbdef18 = (defvos == null || defvos.size() == 0) ? "~" : ((DefdocVO) defvos.toArray()[0])
				.getPk_defdoc();
		defvos = this.getBaseDao().retrieveByClause(DefdocVO.class,
				"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI006')");
		String glbdef19 = (defvos == null || defvos.size() == 0) ? "~" : ((DefdocVO) defvos.toArray()[0])
				.getPk_defdoc();
		// Ares.Tank
		strSQL = "UPDATE " + PsndocDefTableUtil.getPsnHealthTablename() + " SET insuranceform = " + DEL_FORM
				+ ", enddate='" + enddate.toString() + "', glbdef18='" + glbdef18 + "', glbdef19='" + glbdef19
				+ "' WHERE ISNULL(enddate, '9999-12-31') > '" + enddate.toString() + "' AND dr=0 AND pk_psndoc = '"
				+ pk_psndoc + "'";
		this.getBaseDao().executeUpdate(strSQL);

	}

	@Override
	public boolean isExistsGroupInsCalculateResults(String pk_psndoc, String pk_psndoc_sub) throws BusinessException {
		String strSQL = "select * from hi_psndoc_groupinsrecord where pk_psndoc = '"
				+ pk_psndoc
				+ "' and begindate <= (select isnull(enddate, '9999-12-31') from hi_psndoc_groupinsrecord where pk_psndoc =  hi_psndoc_groupinsrecord.pk_psndoc and pk_psndoc_sub = '"
				+ pk_psndoc_sub
				+ "') and enddate >= (select begindate from hi_psndoc_groupinsrecord where pk_psndoc =  hi_psndoc_groupinsrecord"
				+ ".pk_psndoc and pk_psndoc_sub = '" + pk_psndoc_sub + "')";
		IRowSet rowSet = new DataAccessUtils().query(strSQL);

		return rowSet.size() > 0;
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

	private UFDate checkPeriodValid(String[] pk_psndocs, UFDate cBaseDate, String avgmoncount, UFDate effectivedate)
			throws BusinessException {
		// 校驗薪資期間有效性
		UFDate startdate = calendarclac(cBaseDate, avgmoncount);
		if (cBaseDate.after(effectivedate)) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
					"068J61035-0028"));
		} else {
			for (String pk_psndoc : pk_psndocs) {
				Collection<PsnjobVO> psnjobs = this.getBaseDao().retrieveByClause(PsnjobVO.class,
						"pk_psndoc='" + pk_psndoc + "'");
				if (psnjobs == null || psnjobs.size() != 1) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_personalmgt", "068J61035-0028"));
				} else {
					for (PsnjobVO psnjob : psnjobs) {
						if (null == psnjob.getEnddutydate()) {
							UFDate begindate = new UFDate(psnjob.getIndutydate().toDate());

							if (begindate.after(startdate)) {
								startdate = begindate;
							}
						}

					}
				}

			}
		}
		return startdate;

	}

	private UFDate calendarclac(UFDate cBaseDate, String avgmoncount) {
		UFDate startdate = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date dt = sdf.parse(cBaseDate.toString());

			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(dt);// 使用给定的 Date 设置此 Calendar 的时间。
			Integer avgmonday = Integer.parseInt(avgmoncount);
			rightNow.add(Calendar.MONTH, (0 - avgmonday));// 日期减avgmonday个月
			Date dt1 = rightNow.getTime();
			startdate = new UFDate(dt1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return startdate;
	}

	private void dealHealInfoset(UFDate effectivedate, String pk_psndoc, UFDouble avgSalary, List<String> updateSQLs,
			List<PsndocDefVO> subInfoLines) throws BusinessException, DAOException {
		String strSQL;
		strSQL = "select * from " + PsndocDefTableUtil.getPsnHealthTablename() + " where pk_psndoc='" + pk_psndoc
				+ "' and dr=0 and begindate <= '" + effectivedate.toString()
				+ "' and isnull(enddate, '9999-12-31') >='" + effectivedate.toString() + "' and glbdef14='Y'";
		List<Map> healResult = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());

		if (healResult != null && healResult.size() > 0) {
			List<PsndocDefVO> newSubLines = new ArrayList<PsndocDefVO>();
			for (Map healset : healResult) {
				UFDouble originHealLevel = healset.get("glbdef16") == null ? UFDouble.ZERO_DBL : new UFDouble(
						SalaryDecryptUtil.decrypt(new UFDouble((BigDecimal) healset.get("glbdef16")).doubleValue()));
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
					SalaryDecryptUtil.decrypt(new UFDouble((BigDecimal) laborResult.get("glbdef4")).doubleValue()));
			UFDouble originRetireLevel = laborResult.get("glbdef7") == null ? UFDouble.ZERO_DBL : new UFDouble(
					SalaryDecryptUtil.decrypt(new UFDouble((BigDecimal) laborResult.get("glbdef7")).doubleValue()));
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

	private void getValidWaItemByPeriod(String pk_wa_class, String begindate, String endate,
			Map<String, List<String>> periodItem) throws DAOException {
		UFDate startdate = new UFDate(begindate);
		UFDate basedate = new UFDate(endate);
		String startperiod = startdate.getYear() + "-" + begindate.split("-")[1];
		String endperiod = basedate.getYear() + "-" + endate.split("-")[1];

		String strSQL = "select cyear||'-'||cperiod cyearperiod, itemkey from wa_classitem where pk_wa_item in ("
				+ " select itm.pk_wa_item from wa_item itm"
				+ " inner join twhr_waitem_30 tw on itm.pk_wa_item = tw.pk_wa_item"
				+ " where tw.isnhiitem_30 = 'Y' and itm.avgcalcsalflag = 'Y')and pk_wa_class = '" + pk_wa_class
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

	private void getValidWaItemByPeriod4GroupIns(String pk_wa_class, String pk_org, String begindate, String endate,
			Map<String, List<String>> periodItem) throws DAOException {
		UFDate startdate = new UFDate(begindate);
		UFDate basedate = new UFDate(endate);
		String startperiod = startdate.getYear() + "-" + begindate.split("-")[1];
		String endperiod = basedate.getYear() + "-" + endate.split("-")[1];
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
			newLaborVO.setAttributeValue("glbdef2",
					new UFDouble(SalaryEncryptionUtil.encryption(avgSalary.doubleValue())));
			newLaborVO.setAttributeValue("glbdef3", UFDouble.ZERO_DBL);
			newLaborVO.setAttributeValue("glbdef4",
					new UFDouble(SalaryEncryptionUtil.encryption(newMainLevel.doubleValue())));
			newLaborVO.setAttributeValue("glbdef5", originValues.get("glbdef5"));
			newLaborVO.setAttributeValue("glbdef6", UFBoolean.FALSE);
			newLaborVO.setAttributeValue("glbdef7",
					new UFDouble(SalaryEncryptionUtil.encryption(newRetireLevel.doubleValue())));
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
			// Ares.Tank 劳保法人组织和投保形态设设置
			newLaborVO.setAttributeValue("legalpersonorg", pkLegalOrg);
			// 按理来说,此处应该用枚举来set值的,为什么用数字呢?
			// 1.因为没有枚举类 2.hrtaovertimereg.bmf这个元数据是有点毛病 -->3.数字比中文更适合编码
			// 定调资--加保,同步劳保级距--保薪调整

			newLaborVO.setAttributeValue("insuranceform", ADJ_FORM);
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
			newHealVO.setAttributeValue("glbdef6",
					new UFDouble(SalaryEncryptionUtil.encryption(avgSalary.doubleValue())));
			// glbdef16 健保级距
			newHealVO.setAttributeValue("glbdef16",
					new UFDouble(SalaryEncryptionUtil.encryption(newMainLevel.doubleValue())));
			newHealVO.setAttributeValue("glbdef7", UFDouble.ZERO_DBL);
			newHealVO.setAttributeValue("glbdef8", originValues.get("glbdef8"));
			newHealVO.setAttributeValue("glbdef9", originValues.get("glbdef9"));
			newHealVO.setAttributeValue("glbdef10", originValues.get("glbdef10"));
			newHealVO.setAttributeValue("glbdef11", originValues.get("glbdef11"));
			newHealVO.setAttributeValue("glbdef12", originValues.get("glbdef12"));
			newHealVO.setAttributeValue("glbdef13", UFBoolean.FALSE);
			newHealVO.setAttributeValue("glbdef14", originValues.get("glbdef14"));
			newHealVO.setAttributeValue("glbdef15", originValues.get("glbdef15"));
			// Ares.Tank 劳保法人组织和投保形态设设置
			newHealVO.setAttributeValue("legalpersonorg", pkLegalOrg);
			// 按理来说,此处应该用枚举来set值的,为什么用数字呢?
			// 1.因为没有枚举类 2.hrtaovertimereg.bmf这个元数据是有点毛病 -->3.数字比中文更适合编码
			// 定调资--加保,同步劳保级距--保薪调整

			newHealVO.setAttributeValue("insuranceform", ADJ_FORM);
			return newHealVO;
		}
	}

	@Override
	public void generatePsnNHI(String pk_org, String[] pk_psndocs, UFLiteralDate specificStartDate, boolean isGenLabor,
			boolean isGenRetire, boolean isGenHeal) throws BusinessException {
		// 查詢本次加保组织的法人组织 Ares.Tank
		String[] pkLegalOrgs = { pk_org };
		pkLegalOrg = LegalOrgUtils.getLegalOrgByOrgs(pkLegalOrgs).get(pk_org);
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
			// Ares.Tank 2018-9-14 15:59:03
			// 若目前最新投保紀錄投保型態(非退保)與本次定薪的法人公司不同，則可使用此按鈕同步級距
			PsndocDefVO[] vos = service.queryByCondition(PsndocDefUtil.getPsnLaborVO().getClass(), " pk_psndoc='"
					+ pk_psndoc + "' ");
			if (vos != null && vos.length > 0) {
				for (PsndocDefVO vo : vos) {
					if (vo == null || !vo.getLastflag().booleanValue()) {
						continue;// 寻找最新的投保记录
					}
					if (vo.getAttributeValue("insuranceform") != null
							&& Integer.parseInt(vo.getAttributeValue("insuranceform").toString()) != DEL_FORM
							&& vo.getAttributeValue("legalpersonorg") != null
							&& !vo.getAttributeValue("legalpersonorg").equals(pkLegalOrg)) {
						;
					} else {
						throw new BusinessException("已存在相同法人組織的勞保投保設定");
					}
				}

			}

			vos = service
					.queryByCondition(PsndocDefUtil.getPsnHealthVO().getClass(), " pk_psndoc='" + pk_psndoc + "' ");
			if (vos != null && vos.length > 0) {
				for (PsndocDefVO vo : vos) {
					if (vo == null || !vo.getLastflag().booleanValue()) {
						continue;// 寻找最新的投保记录
					}
					if (vo.getAttributeValue("insuranceform") != null
							&& Integer.parseInt(vo.getAttributeValue("insuranceform").toString()) != DEL_FORM
							&& vo.getAttributeValue("legalpersonorg") != null
							&& !vo.getAttributeValue("legalpersonorg").equals(pkLegalOrg)) {
						;
					} else {
						throw new BusinessException("已存在相同法人組織的投保健保設定");
					}
				}

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
			RangeTableAggVO[] rts = this.getRangeTables(pk_org, cyear, cperiod);
			if (rts == null || rts.length == 0) {
				throw new BusinessException("未找到員工到職期間 [" + cyear + "-" + cperiod + "] 的有效級距表");
			} else {
				boolean hasLabor = false;
				boolean hasRetire = false;
				boolean hasHeal = false;
				for (RangeTableAggVO rt : rts) {
					if (RangeTableTypeEnum.LABOR_RANGETABLE.toIntValue() == rt.getParentVO().getTabletype()) {
						hasLabor = true;
					} else if (RangeTableTypeEnum.RETIRE_RANGETABLE.toIntValue() == rt.getParentVO().getTabletype()) {
						hasRetire = true;
					} else if (RangeTableTypeEnum.NHI_RANGETABLE.toIntValue() == rt.getParentVO().getTabletype()) {
						hasHeal = true;
					}
				}
				if (!hasLabor && isGenLabor) {
					throw new BusinessException("未找到員工到職期間 [" + cyear + "-" + cperiod + "] 的有效勞保級距表");
				}
				if (!hasRetire && isGenRetire) {
					throw new BusinessException("未找到員工到職期間 [" + cyear + "-" + cperiod + "] 的有效勞退級距表");
				}
				if (!hasHeal && isGenHeal) {
					throw new BusinessException("未找到員工到職期間 [" + cyear + "-" + cperiod + "] 的有效健保級距表");
				}
			}

			// 按組織期間取定調薪與勞健保設定對比資料
			INhiCalcGenerateSrv srv = NCLocator.getInstance().lookup(INhiCalcGenerateSrv.class);
			NhiCalcVO[] nhiFinalVOs = srv.getAdjustNHIData(pk_psndoc, pk_org, earlist);

			// 新建勞健保資料
			PsndocDefVO[] savedVOs = getNewVOs(nhiFinalVOs, pk_org, cyear, cperiod, true, isGenLabor, isGenRetire,
					isGenHeal);

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
						// 是否劳退投保 glbdef11 Ares.Tank 外籍和劳教人员劳退清0
						vo.setAttributeValue("glbdef11",
								foreignAndTeachSet.contains(vo.getPk_psndoc()) ? UFBoolean.FALSE : UFBoolean.TRUE);

						// ssx added for 勞健退申報
						// 特殊身份別為空白、提繳身分別為1.強制提繳
						DefdocVO defvo = getHIPsnTypeDefVO((String) vo.getAttributeValue("glbdef1"));
						vo.setAttributeValue("glbdef16", null);
						vo.setAttributeValue("glbdef17", defvo != null ? defvo.getPk_defdoc() : null);
						// ssx added for Encryption
						// on 2018-11-07
						for (String fn : NhiCalcUtils.getLaborInsEncryptionAttributes()) {
							UFDouble originValue = (UFDouble) vo.getAttributeValue(fn);
							double encryptedValue = SalaryEncryptionUtil.encryption(originValue == null ? 0
									: originValue.doubleValue());
							vo.setAttributeValue(fn, new UFDouble(encryptedValue));
						}
						// end
					} else if (vo.getClass().equals(PsndocDefUtil.getPsnHealthVO().getClass())) {
						// 健保
						// 投保人或眷属姓名 glbdef1
						vo.setAttributeValue("glbdef1", psnVO.getName());
						// 称谓 glbdef2
						vo.setAttributeValue("glbdef2", "本人");
						// 身份证号码 glbdef3
						vo.setAttributeValue("glbdef3", psnVO.getId());
						// 出生日期 glbdef4
						if (null != psnVO.getBirthdate()) {
							vo.setAttributeValue("glbdef4", new UFDate(psnVO.getBirthdate().toDate()));
						} else {
							vo.setAttributeValue("glbdef4", null);
							// throw new BusinessException("出身日期不能为空");
						}
						// 健保身份 glbdef5
						vo.setAttributeValue("glbdef5", SysInitQuery.getParaString(pk_org, "TWHR05"));
						// 是否投保 glbdef14
						vo.setAttributeValue("glbdef14", UFBoolean.TRUE);
						// 是否所得税抚养人 glbdef15
						vo.setAttributeValue("glbdef15", UFBoolean.TRUE);
						// vo.setAttributeValue("lastflag", UFBoolean.TRUE);
						// ssx added for 勞健退申報
						// 健保資訊自動加入健保加保原因為"到職起薪"
						Collection defvos = (Collection) this
								.getBaseDao()
								.retrieveByClause(DefdocVO.class,
										"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI004')");
						vo.setAttributeValue("glbdef17", (defvos == null || defvos.size() == 0) ? "~"
								: ((DefdocVO) defvos.toArray()[0]).getPk_defdoc());
						// ssx added for Encryption
						// on 2018-11-07
						for (String fn : NhiCalcUtils.getHealthInsEncryptionAttributes()) {
							UFDouble originValue = (UFDouble) vo.getAttributeValue(fn);
							double encryptedValue = SalaryEncryptionUtil.encryption(originValue == null ? 0
									: originValue.doubleValue());
							vo.setAttributeValue(fn, new UFDouble(encryptedValue));
						}
						// end
					}
				}
			}

			if (savedVOs != null && savedVOs.length > 0) {
				SimpleDocServiceTemplate service2 = new SimpleDocServiceTemplate("TWHRGLBDEF");

				for (PsndocDefVO vo : savedVOs) {
					vo.setBegindate(earlist);
					vo.setEnddate(new UFLiteralDate("9999-12-31"));
					vo.setRecordnum(0);

					service2.insert(vo);
				}
			}
		}
	}

	Map<String, DefdocVO> defMap = new HashMap<String, DefdocVO>();

	private DefdocVO getHIPsnTypeDefVO(String pk_psntype) throws BusinessException {
		// ssx added for 勞健退申報
		// 提繳身份別
		if (!defMap.containsKey(pk_psntype)) {
			Collection defvos = this.getBaseDao().retrieveByClause(DefdocVO.class,
					"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI003')");

			if (defvos != null && defvos.size() > 0) {
				defMap.put(pk_psntype, ((DefdocVO) defvos.toArray()[0]));
			}
		}

		return defMap.get(pk_psntype);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void generateGroupIns(String pk_org, String[] pk_psndocs, UFLiteralDate specificStartDate)
			throws BusinessException {
		// 参数检查
		if (StringUtils.isEmpty(pk_org)) {
			throw new BusinessException("組織不能為空");
		}

		if (pk_psndocs == null || pk_psndocs.length == 0) {
			throw new BusinessException("未選取生成團保設定的員工");
		}

		if (specificStartDate != null && specificStartDate.isSameDate(new UFLiteralDate("9999-12-31"))) {
			specificStartDate = null;
		}

		// 过滤人员别为3 DL-OP 并且用工型式为 F 外籍人员
		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(pk_psndocs);
		List<PsndocVO> psnlist = (List<PsndocVO>) this.getBaseDao().retrieveByClause(
				PsndocVO.class,
				"PK_PSNCL <> (select pk_psncl from bd_psncl where code='3') and "
						+ "jobglbdef8 <> (SELECT pk_defdoc FROM BD_DEFDOCLIST defdoclist , "
						+ "BD_DEFDOC defdoc WHERE defdoclist.pk_defdoclist= defdoc.pk_defdoclist "
						+ "and defdoclist.code='HR006_0xx' and defdoc.code='F')");
		List<String> pk_psndoclist = new ArrayList<String>();
		if (psnlist.size() > 0) {
			for (PsndocVO psnvo : psnlist) {
				pk_psndoclist.add(psnvo.getPk_psndoc());
			}
		}
		if (specificStartDate == null) {
			// 检查人员是否有團保设定
			SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
			for (String pk_psndoc : pk_psndoclist.toArray(new String[0])) {
				PsndocDefVO[] vos = service.queryByCondition(PsndocDefUtil.getGroupInsuranceVO().getClass(),
						" pk_psndoc='" + pk_psndoc + "' ");
				if (vos != null && vos.length > 0) {
					throw new BusinessException("已存在團保投保設定");
				}
			}
		}

		// 取團保費率表
		IGroupinsuranceMaintain settingSrv = NCLocator.getInstance().lookup(IGroupinsuranceMaintain.class);
		GroupInsuranceSettingVO[] setting = settingSrv.queryOnDuty(pk_org);

		// 取團保職等對照
		IGroupgradeMaintain gradeSrv = NCLocator.getInstance().lookup(IGroupgradeMaintain.class);
		GroupInsuranceGradeVO[] grades = gradeSrv.queryOnDuty(pk_org);
		// 取人员入职日期
		IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
		// Psndoc
		PsndocVO[] psndocVOs = psnQry.queryPsndocByPks(pk_psndoclist.toArray(new String[0]));

		for (String pk_psndoc : pk_psndoclist.toArray(new String[0])) {
			// 根據人員查找進入日期
			UFLiteralDate earlist = getOrgEnterDateByPsndoc(specificStartDate, pk_psndoc);
			String pk_jobrank = getLastJobRank(pk_psndoc);

			PsndocVO psnVO = null;
			for (PsndocVO psnvo : psndocVOs) {
				if (psnvo.getPk_psndoc().equals(pk_psndoc)) {
					psnVO = psnvo;
				}
			}

			UFDouble grpSum = getGroupInsWadocBaseSalaryByPsnDate(pk_org, pk_psndoc, earlist);

			// MOD (Redmine:21795) 不再進行取整到千的操作
			// ssx modified on 2018-08-23
			// 保薪基數
			// UFDouble psnSum = grpSum.div(1000)
			// .setScale(0, UFDouble.ROUND_HALF_UP).multiply(1000);

			// 新建團保資料
			PsndocDefVO[] savedVOs = getNewGroupInsVO(pk_psndoc, pk_org, grpSum, setting, grades, psnVO, pk_jobrank);

			if (savedVOs != null && savedVOs.length > 0) {
				SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
				for (PsndocDefVO vo : savedVOs) {
					vo.setBegindate(earlist);
					vo.setEnddate(null);
					service.insert(vo);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private UFLiteralDate getOrgEnterDateByPsndoc(UFLiteralDate specificStartDate, String pk_psndoc)
			throws DAOException, BusinessException {
		UFLiteralDate earlist = new UFLiteralDate("9999-12-31");
		if (specificStartDate == null) {
			Collection<PsnOrgVO> psnorgvos = this.getBaseDao().retrieveByClause(
					PsnOrgVO.class,
					"pk_psnorg in (select pk_psnorg from hi_psnjob where endflag='N' and ismainjob='Y' and lastflag='Y'  and pk_psndoc = '"
							+ pk_psndoc + "')");
			if (psnorgvos != null && psnorgvos.size() > 0) {
				for (PsnOrgVO psnorg : psnorgvos) {
					if (pk_psndoc.equals(psnorg.getPk_psndoc())) {
						earlist = psnorg.getBegindate();
					}
				}
			} else {
				throw new BusinessException("人員組織關係查詢發生錯誤");
			}
		} else {
			earlist = specificStartDate;
		}
		return earlist;
	}

	private String getLastJobRank(String pk_psndoc) throws BusinessException {
		Collection<PsnJobVO> psnjobVOs = this.getBaseDao().retrieveByClause(PsnJobVO.class,
				"pk_psndoc='" + pk_psndoc + "'");

		String pk_jobrank = "";
		UFLiteralDate date = new UFLiteralDate("0000-01-01");
		for (PsnJobVO vo : psnjobVOs) {
			if (vo.getBegindate().after(date)) {
				date = vo.getBegindate();
				pk_jobrank = vo.getPk_jobrank();
			}
		}
		return pk_jobrank;
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
			if (StringUtils.isEmpty(bdVO.getRefvalue())) {
				throw new BusinessException("參數設定中編碼為 [" + bdVO.getCode() + "] 的參數值不能為空。");
			}
			if (StringUtils.isEmpty(waitems)) {
				waitems += "'" + bdVO.getRefvalue() + "'";
			} else {
				waitems += ",'" + bdVO.getRefvalue() + "'";
			}
		}

		String strSQL = "select sum(nmoney) grpsum from hi_psndoc_wadoc where pk_wa_item in (" + waitems
				+ ")  and pk_org= '" + pk_org + "' and pk_psndoc = '" + pk_psndoc + "' and '" + salaryDate.toString()
				+ "' between begindate and isnull(enddate, '9999-12-31') and waflag='Y' and lastflag='Y'";
		Object value = this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
		UFDouble grpSum = new UFDouble(value == null ? "0.0" : String.valueOf(value));
		// Ares.Tank 保薪基数还是要解密的,不然会有一堆问题 2018-8-22 11:55:57
		return new UFDouble(SalaryDecryptUtil.decrypt(grpSum.doubleValue()));
	}

	private PsndocDefVO[] getNewGroupInsVO(String pk_psndoc, String pk_org, UFDouble psnSum,
			GroupInsuranceSettingVO[] setting, GroupInsuranceGradeVO[] grades, PsndocVO psndocVO, String pk_jobrank)
			throws BusinessException {
		List<PsndocDefVO> psnNhiVOs = new ArrayList<PsndocDefVO>();
		int i = 0;
		for (GroupInsuranceSettingVO setvo : setting) {
			if (isMatchJobRank(psndocVO, setvo, grades, pk_jobrank)) {
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
				// Ares.Tank start 2018-8-8 10:35:11 团保保薪基数加密
				newGrpInsVO.setAttributeValue("glbdef6", SalaryEncryptionUtil.encryption(psnSum.getDouble()));
				// Ares.Tank end 2018-8-8 10:35:37 团保保薪基数
				newGrpInsVO.setAttributeValue("glbdef7", "N");
				newGrpInsVO.setAttributeValue("insurancecompany", setvo.getInsurancecompany());
				psnNhiVOs.add(newGrpInsVO);
			}
		}
		return psnNhiVOs.toArray(new PsndocDefVO[0]);
	}

	private boolean isMatchJobRank(PsndocVO psndocVO, GroupInsuranceSettingVO setvo, GroupInsuranceGradeVO[] grades,
			String pk_jobrank) {
		String psnJobRank = pk_jobrank == null ? "" : pk_jobrank;
		for (GroupInsuranceGradeVO vo : grades) {
			if (psnJobRank.equals(vo.getPk_jobrank())) {
				if (setvo.getId().equals(vo.getPk_groupinsurance())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void dismissPsnGroupIns(String pk_org, String pk_psndoc, UFLiteralDate enddate) throws BusinessException {
		String infosetCode = PsndocDefTableUtil.getGroupInsuranceTablename();
		if (StringUtils.isEmpty(infosetCode)) {
			throw new BusinessException("無法找到團保子集設置，請檢查自定義項(TWHR000)的設定內容。");
		}

		String strSQL = "UPDATE " + infosetCode + " SET glbdef7='Y', enddate='" + enddate.toString()
				+ "' WHERE ISNULL(enddate, '9999-12-31') > '" + enddate.toString() + "' AND dr=0 AND pk_psndoc = '"
				+ pk_psndoc + "'";
		this.getBaseDao().executeUpdate(strSQL);
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
						String.valueOf(groupSet.get("glbdef6")));
				// MOD (Redmine:21795) 去除千位取整操作
				// ssx modified on 2018-08-23
				UFDouble newSalaryBase = avgSalary;// .div(1000).setScale(0,
													// UFDouble.ROUND_HALF_UP).multiply(1000);
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

	@SuppressWarnings("all")
	@Override
	public void renewRangeEx(String pk_org, String[] pk_psndocs, String[] pk_wa_class, UFDate cbaseDate,
			String avgmoncount, UFDate effectivedate) throws BusinessException {
		// 查詢本次加保组织的法人组织 Ares.Tank
		String[] pkLegalOrgs = { pk_org };
		pkLegalOrg = LegalOrgUtils.getLegalOrgByOrgs(pkLegalOrgs).get(pk_org);
		InSQLCreator insql = new InSQLCreator();
		String waclassInSQL = insql.getInSQL(pk_wa_class);
		// 根据薪资方案筛选出公共薪资项目中勾选了平均薪资项的薪资薪资项
		List<Map<String, String>> walist = (List<Map<String, String>>) this.getBaseDao().executeQuery(
				"SELECT wa_item.itemkey as itemkey from WA_ITEM INNER JOIN TWHR_WAITEM_30 on "
						+ "WA_ITEM.PK_WA_ITEM= TWHR_WAITEM_30.PK_WA_ITEM where WA_ITEM.PK_WA_ITEM IN ("
						+ "select distinct PK_WA_ITEM from WA_CLASSITEM where PK_WA_CLASS in (" + waclassInSQL + "))"
						+ "and AVGCALCSALFLAG='Y' and TWHR_WAITEM_30.isnhiitem_30 = 'Y' ", new MapListProcessor());
		if (walist.size() == 0) {
			throw new BusinessException("没有是否计算劳保健保的薪资项");
		}
		String cumn = "";
		for (Map<String, String> waitem : walist) {
			cumn += waitem.get("itemkey") + "+";
		}
		// 取所有人的平均月薪
		if (pk_psndocs != null && pk_psndocs.length > 0) {
			for (String pk_psndoc : pk_psndocs) {
				// 取級距表
				this.getRangeTables(pk_org, effectivedate);
				// 校驗薪資期間有效性
				// avgmoncount, effectivedate);
				// 檢查是否存在生效日期以後的起始日期
				checkExistDateValid(pk_psndocs, effectivedate);
				// 先获取基准日期，再根据基准日期和平均月数获取开始日期
				// 获取基准日期往前推的日期,返回的日期即为需要计算的开始日期
				UFDate basemonthdate = ComdateUtils.calendarclac(cbaseDate, avgmoncount);
				// 判断人员是否月薪(以基准日?谖?)
				// 获取这个员工在此薪资方案下的薪资期间和wa_data值
				List<Map<String, Object>> waperiodlist = (List<Map<String, Object>>) this.getBaseDao().executeQuery(
						"select WA_PERIOD.CSTARTDATE, WA_PERIOD.CENDDATE,(" + cumn.substring(0, cumn.length() - 1)
								+ ") as f_itemkey "
								+ "from wa_data,WA_WACLASS,WA_PERIOD where WA_DATA.PK_WA_CLASS=WA_WACLASS.PK_WA_CLASS "
								+ " and WA_WACLASS.PK_PERIODSCHEME=WA_PERIOD.PK_PERIODSCHEME "
								+ " and WA_DATA.CYEAR=WA_PERIOD.CYEAR " + " and WA_DATA.CPERIOD=WA_PERIOD.CPERIOD"
								+ " and WA_WACLASS.PK_WA_CLASS in (" + waclassInSQL + ")" + " and WA_DATA.PK_PSNDOC='"
								+ pk_psndoc + "';", new MapListProcessor());
				UFBoolean flag = ismonthsalary(pk_psndoc, cbaseDate);
				// UFDouble avgSalary = UFDouble.ZERO_DBL;
				UFDouble salary = UFDouble.ZERO_DBL;
				if (flag.booleanValue()) {
					// 判断这个员工工作是否满六个月,无论是否满六个月，其计算方式都一样
					if (sixmonth(pk_psndoc, pk_org).booleanValue()) {
						salary = getavgsalary(cbaseDate, basemonthdate, waperiodlist).multiply(30);
					} else {
						salary = getavgsalary(cbaseDate, basemonthdate, waperiodlist).multiply(30);
					}
				} else {
					// 非月薪
					UFDouble avgSalary = getavgsalary(cbaseDate, basemonthdate, waperiodlist);
					UFDouble actavgSalary = getnoavgsalary(pk_psndoc, cbaseDate, basemonthdate, waperiodlist);
					/**
					 * 則使用工作期間總工資除總日數，取得日薪後乘以30為平均工資，
					 * 如果此工資少於工資總額除實際工作日數乘30所得之平均工資的百分之六十， 則使用後者計算出之金額作為實際的平均工資
					 */
					UFDouble avgmonsalary = avgSalary.multiply(30);
					UFDouble actmontsalary = actavgSalary.multiply(30);

					if (avgmonsalary.doubleValue() - (actmontsalary.multiply(0.6).doubleValue()) > 0) {
						salary = actmontsalary.multiply(0.6);
					} else {
						salary = actmontsalary;
					}
				}
				if (salary == null) {
					continue;
				}

				if (salary.equals(UFDouble.ZERO_DBL)) {
					IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
					PsndocVO[] vos = psnQry.queryPsndocByPks(new String[] { pk_psndoc });
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
							"twhr_personalmgt", "068J61035-0031", null, new String[] { vos[0].getCode() })
					/*
					 * * 員工 [ { 0 } ] 在指定的期間範圍內沒有薪資資料 ， 無法計算平均薪資 。
					 */
					);
				}

				List<String> updateSQLs = new ArrayList<String>();

				// 處理劳保劳退
				List<PsndocDefVO> subInfoLines = dealLaborInfoset(effectivedate, pk_psndoc, salary, updateSQLs);

				// 處理健保
				dealHealInfoset(effectivedate, pk_psndoc, salary, updateSQLs, subInfoLines);

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

	@SuppressWarnings("all")
	private List<String> getnewdates(List<String> datelists, List<String> waperioddates) {
		List<String> newdates = new ArrayList<String>();
		for (String datelist : datelists) {
			for (String waperioddate : waperioddates) {
				String insertdates = new ComdateUtils().getAlphalDate(datelist.toString().split(":")[0],
						datelist.split(":")[1], waperioddate.split(":")[0], waperioddate.split(":")[1]);
				newdates.add(insertdates);
			}
		}
		if (null != newdates && newdates.size() != 0) {
			for (int i = 0; i < newdates.size(); i++) {
				if (newdates.get(i).toString().equals("0:0")) {
					newdates.remove(i);
				}
			}
		}
		return newdates;
	}

	/**
	 * 员工调配时劳健保处理
	 * 
	 * @param ufLiteralDate
	 *            退保日期
	 * @param psnJobVO
	 *            新工作
	 * @param is2LegalOrg
	 *            是否加保(同步投保记录到新组织)
	 * @author Ares.Tank 2018-9-16 11:20:53
	 */
	@Override
	public void redeployPsnNHI(UFLiteralDate endDate, PsnJobVO psnJobVO, boolean is2LegalOrg) throws BusinessException {

		// 查詢本次加保组织的法人组织,这就是为什么在调配的时候要新开一个事物去更新工作记录的原因,
		// 这里已经可以从后台直接查询新的工作记录
		String sqlStr = "select PK_ORG from hi_psnjob where pk_psndoc = '" + psnJobVO.getPk_psndoc()
				+ "' and lastflag = 'Y' and endflag = 'N' and dr = 0";
		String pk_org = null;
		try {
			pk_org = (String) this.getBaseDao().executeQuery(sqlStr, new ColumnProcessor());
		} catch (Exception e) {
			return;
		}

		if (null == pk_org) {
			return;
		}
		String[] pkLegalOrgs = { pk_org };

		pkLegalOrg = LegalOrgUtils.getLegalOrgByOrgs(pkLegalOrgs).get(pk_org);
		// 新建勞健保資料
		List<PsndocDefVO> savedVOList = new ArrayList<>();
		// 查找最新的记录
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
		// 定调资处理
		// dealPsnWaDoc(psnJobVOs);
		List<String> lastLaborPkList = new ArrayList<>();
		List<String> lastHealthPkList = new ArrayList<>();
		if (is2LegalOrg) {
			/*
			 * 一.同步相同級距的投保資料: 1.開始日期:delLarbolDate後一天 done 2.投保型態:加保 done
			 * 3.投保組織:新的法人組織 done
			 */

			PsndocDefVO[] vos = service.queryByCondition(PsndocDefUtil.getPsnLaborVO().getClass(), " pk_psndoc='"
					+ psnJobVO.getPk_psndoc() + "' ");

			if (vos != null && vos.length > 0) {

				for (PsndocDefVO vo : vos) {
					if (vo == null || !vo.getLastflag().booleanValue()) {
						continue;// 寻找最新的投保记录
					}
					PsndocDefVO newLaborVO = (PsndocDefVO) vo.clone();
					newLaborVO.setBegindate(endDate.getDateAfter(1));
					newLaborVO.setEnddate(new UFLiteralDate("9999-12-31"));
					// 劳退开始和结束时间
					newLaborVO.setAttributeValue("glbdef14", endDate.getDateAfter(1));
					newLaborVO.setAttributeValue("glbdef15", new UFLiteralDate("9999-12-31"));

					newLaborVO.setAttributeValue("legalpersonorg", pkLegalOrg);
					newLaborVO.setAttributeValue("insuranceform", ADD_FORM);
					if (savedVOList.size() <= 0) {
						savedVOList.add(newLaborVO);
					}
				}

			}
			vos = service.queryByCondition(PsndocDefUtil.getPsnHealthVO().getClass(),
					" pk_psndoc='" + psnJobVO.getPk_psndoc() + "' ");
			if (vos != null && vos.length > 0) {
				for (PsndocDefVO vo : vos) {
					if (vo == null || !vo.getLastflag().booleanValue()) {
						continue;// 寻找最新的投保记录
					}
					PsndocDefVO newLaborVO = (PsndocDefVO) vo.clone();
					newLaborVO.setBegindate(endDate.getDateAfter(1));
					newLaborVO.setEnddate(new UFLiteralDate("9999-12-31"));
					newLaborVO.setAttributeValue("legalpersonorg", pkLegalOrg);
					newLaborVO.setAttributeValue("insuranceform", ADD_FORM);
					if (savedVOList.size() <= 1) {
						savedVOList.add(newLaborVO);
					}

				}

			}

		}
		//
		/*
		 * 一:回寫最後一筆投保紀錄 :退保 1.結束日期:delLarbolDate done 2.投保狀態:退保 done
		 * 3.健保退保原因別:退保 done 4.健保退保原因說明別:離職。 done
		 */
		// 退保
		delPNI(psnJobVO.getPk_psndoc(), endDate);

		// 加保
		if (savedVOList != null && savedVOList.size() > 0) {
			for (PsndocDefVO vo : savedVOList) {
				vo.setPk_psndoc_sub(null);
				service.insert(vo);
			}
		}

		// 回写最新標誌--保險起見,回寫以前的全部為n,只保留一條最新數據
		StringBuilder strSQLSB = new StringBuilder();
		for (String lastLaborPk : lastLaborPkList) {
			strSQLSB.delete(0, strSQLSB.length());
			strSQLSB.append("UPDATE ").append(PsndocDefTableUtil.getPsnLaborTablename())
					.append(" SET  lastflag = 'N' ").append(" WHERE PK_PSNDOC_SUB = '").append(lastLaborPk).append("'");
			this.getBaseDao().executeUpdate(strSQLSB.toString());
		}

		for (String lastHealthPk : lastHealthPkList) {
			strSQLSB.delete(0, strSQLSB.length());
			strSQLSB.append("UPDATE ").append(PsndocDefTableUtil.getPsnHealthTablename())
					.append(" SET  lastflag = 'N' ").append(" WHERE PK_PSNDOC_SUB = '").append(lastHealthPk)
					.append("'");
			this.getBaseDao().executeUpdate(strSQLSB.toString());
		}

	}

	/**
	 * 员工留停複職时劳健保处理
	 * 
	 * @param ufLiteralDate
	 *            退保日期
	 * @param psnJobVO
	 *            新工作
	 * @author Ares.Tank 2018-9-16 11:20:53
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void returnPsnNHI(UFLiteralDate startDate, PsnJobVO psnJobVO) throws BusinessException {

		// mod 回写逻辑修改 Ares.Tank 2018-10-2 10:06:23

		/*
		 * Ares.Tank 2018-9-22 10:55:54 回寫原本投保紀錄記錄: 開始日期:startDate done done
		 * 結束日期:9999 done done 投保型態:加保 done done 健保加保原因別:到職起薪 done mod Ares.Tank
		 * in 2018-10-2 10:56:14 业务逻辑变更:不回写原有的投保记录,而是增加新的投保记录
		 */
		// 查找最新的记录
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
		// 新建勞健保資料
		List<PsndocDefVO> savedVOList = new ArrayList<>();
		PsndocDefVO[] vos = service.queryByCondition(PsndocDefUtil.getPsnLaborVO().getClass(), " pk_psndoc='"
				+ psnJobVO.getPk_psndoc() + "' ");
		List<String> lastLaborPkList = new ArrayList<>();
		if (vos != null && vos.length > 0) {
			for (PsndocDefVO vo : vos) {
				if (vo == null || !vo.getLastflag().booleanValue()) {
					continue;// 寻找最新的投保记录
				}
				lastLaborPkList.add(vo.getPk_psndoc_sub());
				PsndocDefVO newLaborVO = (PsndocDefVO) vo.clone();
				newLaborVO.setBegindate(startDate);
				newLaborVO.setEnddate(new UFLiteralDate("9999-12-31"));

				// 劳退开始和结束时间
				newLaborVO.setAttributeValue("glbdef14", startDate);
				newLaborVO.setAttributeValue("glbdef15", new UFLiteralDate("9999-12-31"));

				// newLaborVO.setAttributeValue("legalpersonorg", pkLegalOrg);
				newLaborVO.setAttributeValue("insuranceform", ADD_FORM);
				if (savedVOList.size() <= 0) {
					// 有可能出現臟數據,保證只有一條薪資

					savedVOList.add(newLaborVO);
				}

			}

		}
		Collection defvos = this.getBaseDao().retrieveByClause(DefdocVO.class,
				"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI004')");
		String glbdef17 = (defvos == null || defvos.size() == 0) ? "~" : ((DefdocVO) defvos.toArray()[0])
				.getPk_defdoc();
		vos = service.queryByCondition(PsndocDefUtil.getPsnHealthVO().getClass(),
				" pk_psndoc='" + psnJobVO.getPk_psndoc() + "' ");
		List<String> lastHealthPkList = new ArrayList<>();
		if (vos != null && vos.length > 0) {
			for (PsndocDefVO vo : vos) {
				if (vo == null || !vo.getLastflag().booleanValue()) {
					continue;// 寻找最新的投保记录
				}
				lastHealthPkList.add(vo.getPk_psndoc_sub());
				PsndocDefVO newLaborVO = (PsndocDefVO) vo.clone();
				newLaborVO.setBegindate(startDate);
				newLaborVO.setEnddate(new UFLiteralDate("9999-12-31"));

				// newLaborVO.setAttributeValue("legalpersonorg", pkLegalOrg);
				newLaborVO.setAttributeValue("insuranceform", ADD_FORM);
				newLaborVO.setAttributeValue("glbdef17", glbdef17);
				if (savedVOList.size() <= 1) {
					savedVOList.add(newLaborVO);
				}

			}

		}

		// 加保
		if (savedVOList != null && savedVOList.size() > 0) {
			for (PsndocDefVO vo : savedVOList) {
				vo.setPk_psndoc_sub(null);
				service.insert(vo);
			}
		} else {
			throw new BusinessException("此員工沒有投保紀錄");
		}
		// 回写最新標誌--保險起見,回寫以前的全部為n,只保留一條最新數據
		StringBuilder strSQLSB = new StringBuilder();
		for (String lastLaborPk : lastLaborPkList) {
			strSQLSB.delete(0, strSQLSB.length());
			strSQLSB.append("UPDATE ").append(PsndocDefTableUtil.getPsnLaborTablename()).append(" SET lastflag = 'N' ")
					.append(" WHERE PK_PSNDOC_SUB = '").append(lastLaborPk).append("'");
			this.getBaseDao().executeUpdate(strSQLSB.toString());
		}

		for (String lastHealthPk : lastHealthPkList) {
			strSQLSB.delete(0, strSQLSB.length());
			strSQLSB.append("UPDATE ").append(PsndocDefTableUtil.getPsnHealthTablename())
					.append(" SET lastflag = 'N' ").append(" WHERE PK_PSNDOC_SUB = '").append(lastHealthPk).append("'");
			this.getBaseDao().executeUpdate(strSQLSB.toString());
		}

	}

	/**
	 * 员工留職停薪时劳健保处理
	 * 
	 * @param ufLiteralDate
	 *            退保日期
	 * @param psnJobVO
	 *            新工作
	 * @author Ares.Tank 2018-9-16 11:20:53
	 * @throws BusinessException
	 */
	@Override
	public void transPsnNHI(UFLiteralDate endDate, PsnJobVO psnJobVO) throws BusinessException {
		/*
		 * 回寫原本投保紀錄記錄 1.結束日期:endDate 2.投保型態:退保 3.健保退保原因別:退保 4.健保退保原因說明別:離職。
		 */

		// 退保
		delPNI(psnJobVO.getPk_psndoc(), endDate);
	}

	/**
	 * 調配/離職記錄時的退保
	 * 
	 * @param pk_psndoc
	 * @param endDate
	 *            退保日期
	 * @throws BusinessException
	 */
	private void delPNI(String pk_psndoc, UFLiteralDate endDate) throws BusinessException {
		// 修改的行数,如果没有更新则说明没有投保记录
		int resultRow = 0;

		// 回写结束日期和投保形态--劳保
		String strSQL = "UPDATE " + PsndocDefTableUtil.getPsnLaborTablename() + " SET enddate='" + endDate.toString()
				+ "' , insuranceform = " + DEL_FORM + " WHERE ISNULL(enddate, '9999-12-31') > '" + endDate.toString()
				+ "' AND dr=0 AND pk_psndoc = '" + pk_psndoc + "'" + " and lastflag = 'Y'";
		resultRow = this.getBaseDao().executeUpdate(strSQL);

		strSQL = "UPDATE " + PsndocDefTableUtil.getPsnLaborTablename() + " SET glbdef15='" + endDate.toString()
				+ "' WHERE ISNULL(glbdef15, '9999-12-31') > '" + endDate.toString() + "' AND dr=0 AND pk_psndoc = '"
				+ pk_psndoc + "'" + " and lastflag = 'Y'";
		resultRow += this.getBaseDao().executeUpdate(strSQL);

		// : 補充新增健保欄位及回寫邏輯
		// ssx modified on 2018-03-19
		// for changes of psn leave
		// 離職自動退保時預製退保原因別為：2.轉出 ×

		// Ares.Tank mod on 2018-9-15 22:55:16
		// 新逻辑:投保狀態為退保、健保退保原因別為1.退保、健保退保原因說明別為1.離職。--Jessie

		// 退保說明別為：1.離職
		Collection defvos = this.getBaseDao().retrieveByClause(DefdocVO.class,
				"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI005')");
		String glbdef18 = (defvos == null || defvos.size() == 0) ? "~" : ((DefdocVO) defvos.toArray()[0])
				.getPk_defdoc();
		defvos = this.getBaseDao().retrieveByClause(DefdocVO.class,
				"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI006')");
		String glbdef19 = (defvos == null || defvos.size() == 0) ? "~" : ((DefdocVO) defvos.toArray()[0])
				.getPk_defdoc();
		String updatesql = "update " + PsndocDefTableUtil.getPsnHealthTablename() + " set lastflag='Y' WHERE dr=0 AND "
				+ "glbdef3=( SELECT ID FROM BD_PSNDOC " + "WHERE PK_PSNDOC='" + pk_psndoc + "') and "
				+ "(enddate is null or enddate='9999-12-31' or enddate = '~')";
		this.getBaseDao().executeUpdate(updatesql);

		String updatesqls = "update " + PsndocDefTableUtil.getPsnHealthTablename()
				+ " set lastflag='N' WHERE dr=0 AND " + "glbdef3 !=( SELECT ID FROM BD_PSNDOC " + "WHERE PK_PSNDOC='"
				+ pk_psndoc + "') and " + "(enddate is null or enddate='9999-12-31' or enddate = '~')";
		this.getBaseDao().executeUpdate(updatesqls);
		strSQL = "UPDATE " + PsndocDefTableUtil.getPsnHealthTablename() + " SET enddate='" + endDate.toString()
				+ "', glbdef18='" + glbdef18 + "', glbdef19='" + glbdef19 + "' , insuranceform = " + DEL_FORM
				+ " WHERE ISNULL(enddate, '9999-12-31') > '" + endDate.toString() + "' AND dr=0 AND pk_psndoc = '"
				+ pk_psndoc + "'" + " and (enddate is null or enddate='9999-12-31' or enddate = '~') ";
		resultRow += this.getBaseDao().executeUpdate(strSQL);
		String clearsql = "update " + PsndocDefTableUtil.getPsnHealthTablename() + "set glbdef18='~', "
				+ "glbdef19='~' WHERE dr=0 AND " + "glbdef3 !=( SELECT ID FROM BD_PSNDOC " + "WHERE PK_PSNDOC='"
				+ pk_psndoc + "') and " + "enddate='" + endDate.toString() + "'";
		this.getBaseDao().executeUpdate(clearsql);
		if (resultRow <= 0) {
			throw new BusinessException("此員工沒有投保紀錄");
		}

	}

	/**
	 * 获取是否月薪
	 * 
	 * @param pk_psndoc
	 * @param context
	 * @param basedate
	 * @return
	 * @throws BusinessException
	 */
	public UFBoolean ismonthsalary(String pk_psndoc, UFDate basedate) throws BusinessException {
		List<PsnJobVO> psnjobvos = (List<PsnJobVO>) this.getBaseDao().retrieveByClause(PsnJobVO.class,
				"pk_psndoc='" + pk_psndoc + "'");
		UFBoolean ismonthsalary = UFBoolean.TRUE;
		for (PsnJobVO psnjob : psnjobvos) {
			// 取基准日期在任职记录的区间
			if (psnjob.getBegindate().before(new UFLiteralDate(basedate.toString()))
					|| psnjob.getBegindate().isSameDate(new UFLiteralDate(basedate.toString()))
					&& (psnjob.getEnddate() == null || psnjob.getEnddate().equals("~")
							|| psnjob.getEnddate().isSameDate(new UFLiteralDate(basedate.toString())) || psnjob
							.getEnddate().after(new UFLiteralDate(basedate.toString())))) {
				return (UFBoolean) psnjob.getAttributeValue("ismonsalary");
			}
		}
		return ismonthsalary;
	}

	/**
	 * 判断这个员工入职是否满六个月
	 * 
	 * @param pk_psndoc
	 * @param context
	 * @return
	 * @throws BusinessException
	 */
	public UFBoolean sixmonth(String pk_psndoc, String pk_org) throws BusinessException {
		List<PsnJobVO> joblist = (List<PsnJobVO>) this.getBaseDao().retrieveByClause(PsnJobVO.class,
				"pk_psndoc='" + pk_psndoc + "' and pk_org='" + pk_org + "'");
		UFBoolean sixmonthflag = UFBoolean.TRUE;
		return sixmonthflag;
	}

	/**
	 * 返回日薪(基准时间段--取左不取右，薪资期间段--既取左也取右)
	 * 
	 * @param basedateperiod
	 * @param waperiodlist
	 * @return
	 */
	private UFDouble getavgsalary(UFDate basedate, UFDate basemonthdate, List<Map<String, Object>> waperiodlist) {
		// 取薪资期间和基准日期段的时间交集
		int sumSalary = 0;
		int sumDays = 0;
		for (Map<String, Object> waperiod : waperiodlist) {
			String waperiodstartdate = (String) waperiod.get("cstartdate");
			String waperiodenddate = (String) waperiod.get("cenddate");
			String periodtime = ComdateUtils.getAlphalDate(basemonthdate.toString(),
					ComdateUtils.getcheckdate(basedate.toString(), -1), waperiodstartdate, waperiodenddate);
			// 日期交集的天数
			int days = 0;
			if (null != periodtime) {
				days = ComdateUtils.daysOfTwo(periodtime.split(":")[0], periodtime.split(":")[1]);
			}
			sumDays += days;
			// 获取薪资期间的天数
			int periodays = ComdateUtils.daysOfTwo(waperiodstartdate, waperiodenddate);
			sumSalary += (Integer.parseInt(waperiod.get("f_itemkey").toString()) / periodays) * days;
		}
		if (sumDays == 0) {
			return UFDouble.ZERO_DBL;
		}

		return (new UFDouble(sumSalary + "").div(new UFDouble(sumDays + "")));
	}

	/**
	 * 非月薪的人员计算
	 * 
	 * @param pk_psndoc
	 * @param basedate
	 * @param basemonthdate
	 * @param waperiodlist
	 * @return
	 * @throws BusinessException
	 */
	public UFDouble getnoavgsalary(String pk_psndoc, UFDate basedate, UFDate basemonthdate,
			List<Map<String, Object>> waperiodlist) throws BusinessException {
		// 取薪资期间和基准日期段的时间交集
		long sumSalary = 0;
		long sumDays = 0;
		for (Map<String, Object> waperiod : waperiodlist) {
			String waperiodstartdate = (String) waperiod.get("cstartdate");
			String waperiodenddate = (String) waperiod.get("cenddate");
			String periodtime = ComdateUtils.getAlphalDate(basemonthdate.toString(),
					ComdateUtils.getcheckdate(basedate.toString(), -1), waperiodstartdate, waperiodenddate);
			// 日期交集的天数
			long days = 0;
			if (null != periodtime) {
				days = ComdateUtils.daysOfTwo(periodtime.split(":")[0], periodtime.split(":")[1]);
			} else {
				days = 0;
			}
			sumDays += days;
			// 获取薪资期间的天数
			long periodays = ComdateUtils.daysOfTwo(waperiodstartdate, waperiodenddate);
			sumSalary += ((Long.parseLong(waperiod.get("f_itemkey").toString())) / periodays) * days;
		}
		if (sumDays == 0) {
			return UFDouble.ZERO_DBL;
		}
		// 计算非月薪人员的实际日薪
		List<PsnCalendarVO> psncalendarlist = (List<PsnCalendarVO>) this.getBaseDao().retrieveByClause(
				PsnCalendarVO.class,
				"pk_psndoc='" + pk_psndoc + "'  and " + "calendar BETWEEN '" + basemonthdate + "' and '"
						+ ComdateUtils.getcheckdate(basedate.toString(), -1) + "'");
		int actdays = 0;
		for (PsnCalendarVO psnvo : psncalendarlist) {
			if (null != psnvo.getGzsj() && psnvo.getGzsj().getDouble() > 0) {
				actdays++;
			}
		}
		// 实际日薪为
		UFDouble actdaysalary = UFDouble.ZERO_DBL;
		if (0 != actdays) {
			actdaysalary = new UFDouble(sumSalary + "").div(new UFDouble(actdays + ""));
		}
		return actdaysalary;
	}

	/**
	 * 同步团保级距
	 */

	@Override
	public void renewGroupIns(String pk_org, String[] pk_psndocs, UFDate effectivedate) throws BusinessException {
		// 查詢本次加保组织的法人组织 Ares.Tank
		String[] pkLegalOrgs = { pk_org };
		pkLegalOrg = LegalOrgUtils.getLegalOrgByOrgs(pkLegalOrgs).get(pk_org);
		// 查询员工的定调资信息
		if (pk_psndocs != null && pk_psndocs.length > 0) {
			for (String pk_psndoc : pk_psndocs) {
				// 取級距表
				this.getRangeTables(pk_org, effectivedate);
				// 校驗薪資期間有效性
				// 檢查是否存在生效日期以後的起始日期
				checkExistDateValid(pk_psndocs, effectivedate);

				// 查询员工在参数设定中的薪资项
				// 获取法人组织下的薪资项
				List<String> salaryitems = new ArrayList<String>();
				InSQLCreator insql = new InSQLCreator();
				List<BaseDocVO> basedocvos = (List<BaseDocVO>) this.getBaseDao().retrieveByClause(BaseDocVO.class,
						"pk_org='" + pkLegalOrg + "' and code like 'GRPSUM%'");
				for (BaseDocVO basedocvo : basedocvos) {
					if (null != basedocvo.getRefvalue()) {
						salaryitems.add(basedocvo.getRefvalue());
					}
				}
				String waitemInSQL = insql.getInSQL(salaryitems.toArray(new String[0]));
				if (null == salaryitems || salaryitems.size() == 0) {
					throw new BusinessException("参数设定中没有需要计算的薪资项");
				}
				// 通过薪资项去定调资表里面查找出所有的最新一条记录
				List<PsndocWadocVO> wavos = (List<PsndocWadocVO>) this.getBaseDao().retrieveByClause(
						PsndocWadocVO.class,
						"pk_psndoc='" + pk_psndoc + "' and pk_wa_item in (" + waitemInSQL + ") and enddate is null");
				UFDouble sumSalary = UFDouble.ZERO_DBL;
				for (PsndocWadocVO psndocwa : wavos) {
					sumSalary = sumSalary.add(psndocwa.getNmoney());
				}
				if (baseOnAverageSalary) {
					if (sumSalary == null) {
						continue;
					}

					if (sumSalary.equals(UFDouble.ZERO_DBL)) {
						IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
						PsndocVO[] vos = psnQry.queryPsndocByPks(new String[] { pk_psndoc });
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
								"twhr_personalmgt", "068J61035-0031", null, new String[] { vos[0].getCode() })
						/*
						 * 員工 [{0}] 在指定的期間範圍內沒有薪資資料 ， 無法計算平均薪資 。
						 */);
					}
				} /*
				 * else { // 取定调资 IPsndocSubInfoService4JFS salaryQry =
				 * NCLocator.getInstance().lookup(
				 * IPsndocSubInfoService4JFS.class); sumSalary =
				 * salaryQry.getGroupInsWadocBaseSalaryByPsnDate(pk_org,
				 * pk_psndoc, new UFLiteralDate( effectivedate.toString())); }
				 */
				List<String> updateSQLs = new ArrayList<String>();

				// 處理劳保劳退
				List<PsndocDefVO> subInfoLines = dealGroupInsInfoset(effectivedate, pk_psndoc, sumSalary, updateSQLs);

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

	/**
	 * 生成勞健保設定
	 * 
	 * @param pk_org
	 *            組織
	 * @param <pk_psndoc,Set<加保类型>> psndocsAndInsuranceTypeMap 人員列表
	 * @param NHIInsMap
	 *            加保信息 map key 参照:nc.itf.hr.hi.BatchInsuranceFieldDeclaration
	 * @throws BusinessException
	 */
	@Override
	public void generatePsnNHI(String pk_org, Map<String, Set<InsuranceTypeEnum>> psndocsAndInsuranceTypeMap,
			Map<String, String> NHIInsMap) throws BusinessException {
		// TODO Auto-generated method stub

	}

	/**
	 * 生成團保設定
	 * 
	 * @param pk_org
	 *            組織
	 * @param pk_psndocs
	 *            人員列表
	 * @throws BusinessException
	 */
	@Override
	public void generateGroupIns(String pk_org, String[] pk_psndocs, Map<String, String> groupInsMap)
			throws BusinessException {
		// TODO Auto-generated method stub

	}

	/**
	 * 劳健保批量退保
	 * 
	 * @param pk_org
	 * @param @param <pk_psndoc,Set<加保类型>> psndocsAndInsuranceTypeMap
	 * @param outMap
	 *            退保信息 map key 参照:nc.itf.hr.hi.BatchInsuranceFieldDeclaration
	 * @throws BusinessException
	 */
	@Override
	public void delPsnNHI(String pk_org, Map<String, Set<InsuranceTypeEnum>> psndocsAndInsuranceTypeMap,
			Map<String, String> outMap) throws BusinessException {
		// TODO Auto-generated method stub

	}

	/**
	 * 团报批量退保
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param outMap
	 *            退保信息 mapKey map key
	 *            参照:nc.itf.hr.hi.BatchInsuranceFieldDeclaration
	 * @throws BusinessException
	 */
	@Override
	public void delGroupIns(String pk_org, String[] pk_psndocs, Map<String, String> outMap) throws BusinessException {
		// TODO Auto-generated method stub

	}

}
