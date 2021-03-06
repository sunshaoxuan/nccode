package nc.impl.hi;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import nc.bs.logging.Logger;
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
import nc.itf.hr.wa.IPsndocwadocManageService;
import nc.itf.twhr.IGroupgradeMaintain;
import nc.itf.twhr.IGroupinsuranceMaintain;
import nc.itf.twhr.INhiCalcGenerateSrv;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.metadata.Field;
import nc.pub.smart.metadata.MetaData;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.twhr.IBasedocPubQuery;
import nc.pubitf.twhr.IRangetablePubQuery;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.psn.PsnjobVO;
import nc.vo.hi.psndoc.FamilyVO;
import nc.vo.hi.psndoc.Glbdef1VO;
import nc.vo.hi.psndoc.Glbdef2VO;
import nc.vo.hi.psndoc.Glbdef6VO;
import nc.vo.hi.psndoc.Glbdef7VO;
import nc.vo.hi.psndoc.GroupInsuranceDetailVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocDefVO;
import nc.vo.hi.trnstype.TrnstypeVO;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.trn.transmng.AggStapply;
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

			rangeTables = qry.queryRangetableByType(-1, this.getFirstDayOfMonth(cyear, cperiod));
		}
		return rangeTables;
	}

	private RangeTableAggVO[] getRangeTables(String pk_org, UFDate date) throws BusinessException {
		if (rangeTables == null) {
			IRangetablePubQuery qry = (IRangetablePubQuery) NCLocator.getInstance().lookup(IRangetablePubQuery.class);

			rangeTables = qry.queryRangetableByType(-1, date);
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

	// MOD by ssx on 2020-04-27
	// 增加只計算功能，給產出薪資明細檔邏輯使用
	// 後續應可作為統計用途，另外再修改邏輯
	// 注意後續修改時，不要影響薪資明細檔取數
	@Override
	public void calculateGroupIns(String pk_org, String pk_wa_class, String cYear, String cPeriod, String[] pk_psndocs,
			boolean onlyCalculate) throws BusinessException {
		if (onlyCalculate) {
			setInsComStates(null);
		}

		// 取得计算年月（未审核当前期间），只計算時不管薪資檔案是否關閉(MOD on 2020-04-27)
		Map periodMap = getCalculatePeriod(pk_org, cYear, cPeriod, onlyCalculate);

		String cyear = (String) periodMap.get("cyear");
		String cperiod = (String) periodMap.get("cperiod");
		String cstartdate = (String) periodMap.get("cstartdate");
		String cenddate = (String) periodMap.get("cenddate");

		// 按組織、期間取本次生成人員列表
		List<String> listPsns = getGroupInsurancePsnList(pk_org, cyear, cperiod, pk_psndocs);
		// 取員工ID
		Map<String, String> psnIDs = getPsnIDMap(listPsns);

		// 取人员子集设定
		Map<String, Map<String, GroupInsSettingVO>> groupSettings = getGroupSettings(cstartdate, cenddate, listPsns,
				pk_org, cyear, cperiod);

		Map<String, String[]> groupInsRatePair = new HashMap<String, String[]>();
		// 取团保费率表
		getGroupInsuranceSettings(groupSettings, groupInsRatePair);
		// 勞保投保級距
		Map<String, Double> grand = getGrand(listPsns, cstartdate, cenddate);

		// 計算團保 <pk_psndoc, GroupInsDetailVO>
		Map<String, GroupInsDetailVO> calcRst = calculateGroupInsurance(pk_org, cstartdate, cenddate, groupSettings,
				groupInsRatePair, psnIDs, grand);

		// 不是只計算時，回寫子集
		if (!onlyCalculate) {
			// 計載每月每名員工與眷屬總共需負擔之團保費，如遇以戶計算的則為平分，除不盡者找其中一個人塞餘額
			Map<String, List<FamEmpGroupInsDetailVO>> calcFEdetails = calcFEdetailGroupIns(pk_org, cstartdate,
					cenddate, groupSettings, groupInsRatePair, grand);
			// 回写团保明细子集
			writeBackPsndoc(calcRst);
			// 回写眷属与员工团保投保明细 by he
			writeBackfamempdetail(calcFEdetails);
			// 清空薪資發放已計算標記
			clearWaDataCalculateFlag(pk_org, pk_wa_class, cyear, cperiod, calcRst);
		}
	} // end MOD

	@SuppressWarnings("unchecked")
	private Map<String, String> getPsnIDMap(List<String> pk_psndocs) throws BusinessException {
		List<Map> idmaps = (List<Map>) this.getBaseDao().executeQuery(
				"select pk_psndoc, id from bd_psndoc where pk_psndoc in ("
						+ new InSQLCreator().getInSQL(pk_psndocs.toArray(new String[0])) + ")", new MapListProcessor());
		Map<String, String> rtn = new HashMap<String, String>();
		if (idmaps != null && idmaps.size() > 0) {
			for (Map idmap : idmaps) {
				String pk_psndoc = (String) idmap.get("pk_psndoc");
				String id = (String) idmap.get("id");
				rtn.put(pk_psndoc, id);
			}
		}
		return rtn;
	}

	@SuppressWarnings("unchecked")
	private void writeBackfamempdetail(Map<String, List<FamEmpGroupInsDetailVO>> calcFEdetails)
			throws BusinessException, DAOException {
		if (calcFEdetails.size() > 0) {
			SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
			service.setLazyLoad(true);
			List<String> codelist = new ArrayList<String>();
			List<String> pk_psndocs = new ArrayList<String>();

			for (String rst : calcFEdetails.keySet()) {
				for (FamEmpGroupInsDetailVO famempvo : calcFEdetails.get(rst)) {
					codelist.add(famempvo.getIdentitycode());
					codelist.add(famempvo.getInsurancecode());
					pk_psndocs.add(famempvo.getPk_psndoc());
				}
			}
			List<DefdocVO> inlist = null;
			InSQLCreator insql = new InSQLCreator();
			try {
				inlist = (List<DefdocVO>) this.getBaseDao().retrieveByClause(DefdocVO.class,
						"pk_defdoc in (" + insql.getInSQL(codelist.toArray(new String[0])) + ")");
			} catch (DAOException e) {
				e.printStackTrace();
			}
			// 查询出明细
			List<GroupInsuranceDetailVO> deletelist = new ArrayList<GroupInsuranceDetailVO>();
			List<GroupInsuranceDetailVO> insertvos = new ArrayList<GroupInsuranceDetailVO>();

			Map<String, List<GroupInsuranceDetailVO>> mapVOs = new HashMap<String, List<GroupInsuranceDetailVO>>();
			List<List<String>> groupedPsn = getGroupedKeys(calcFEdetails.keySet());
			for (String pk_psndoc : calcFEdetails.keySet()) {
				for (FamEmpGroupInsDetailVO famempvo : calcFEdetails.get(pk_psndoc)) {
					if (!mapVOs.containsKey(pk_psndoc)) {
						mapVOs = getGroupDetailByGroupKeys(pk_psndoc, groupedPsn);
					}

					if (!mapVOs.containsKey(pk_psndoc)) {
						continue;
					}

					for (GroupInsuranceDetailVO vo : mapVOs.get(pk_psndoc)) {
						if (famempvo.getdStartDate().isSameDate(vo.getBegindate())
								&& famempvo.getdEndDate().isSameDate(vo.getEnddate())) {
							deletelist.add(vo);
						}
					}
					GroupInsuranceDetailVO defVO = new GroupInsuranceDetailVO();

					defVO.setBegindate(famempvo.getdStartDate());
					defVO.setEnddate(famempvo.getdEndDate());
					defVO.setPk_psndoc(pk_psndoc);
					defVO.setRecordnum(0);
					defVO.setLastflag(UFBoolean.TRUE);
					defVO.setDr(0);
					defVO.setAttributeValue("name", famempvo.getName());
					defVO.setAttributeValue("insurancecode", getdefname(famempvo.getInsurancecode(), inlist));
					defVO.setAttributeValue("identitycode", getdefname(famempvo.getIdentitycode(), inlist));
					defVO.setAttributeValue("ishousehold", famempvo.getIshousehold());
					defVO.setAttributeValue("insuranceamount", famempvo.getInsuranceamount());
					insertvos.add(defVO);
					// this.getBaseDao().insertVO(defVO);
				}
			}
			this.getBaseDao().deleteVOList(deletelist);
			String strSQL = "update groupinsurance_detail set recordnum = recordnum +1 "
					+ " where pk_psndoc in ("
					+ insql.getInSQL(pk_psndocs.toArray(new String[0]))
					+ ") and exists(select * from groupinsurance_detail def where groupinsurance_detail.pk_psndoc = def.pk_psndoc and def.recordnum = 1)";
			this.getBaseDao().executeUpdate(strSQL);
			this.getBaseDao().insertVOArray(insertvos.toArray(new GroupInsuranceDetailVO[0]));
		}

	}

	@SuppressWarnings("unchecked")
	private Map<String, List<GroupInsuranceDetailVO>> getGroupDetailByGroupKeys(String pk_psndoc,
			List<List<String>> groupedPsn) throws BusinessException {
		Map<String, List<GroupInsuranceDetailVO>> detailMap = new HashMap<String, List<GroupInsuranceDetailVO>>();
		List<String> curLoadPsns = null;
		for (List<String> psnList : groupedPsn) {
			if (psnList.contains(pk_psndoc)) {
				curLoadPsns = psnList;
				break;
			}
		}
		if (curLoadPsns != null) {
			Collection<GroupInsuranceDetailVO> vos = this.getBaseDao().retrieveByClause(GroupInsuranceDetailVO.class,
					"pk_psndoc in (" + new InSQLCreator().getInSQL(curLoadPsns.toArray(new String[0])) + ")");

			if (vos != null && vos.size() > 0) {
				for (GroupInsuranceDetailVO vo : vos) {
					if (!detailMap.containsKey(vo.getPk_psndoc())) {
						detailMap.put(vo.getPk_psndoc(), new ArrayList<GroupInsuranceDetailVO>());
					}

					detailMap.get(vo.getPk_psndoc()).add(vo);
				}
			}
		}
		return detailMap;
	}

	private List<List<String>> getGroupedKeys(Set<String> keySet) throws BusinessException {
		Integer groupSize = 199;
		Integer count = 0;
		List<List<String>> groupedKeys = new ArrayList<List<String>>();
		if (keySet != null && keySet.size() > 0) {
			List<String> keyGroup = new ArrayList<String>();
			for (String key : keySet) {
				if (count.equals(groupSize)) {
					groupedKeys.add(keyGroup);
					keyGroup = new ArrayList<String>();
					count = 0;
				}

				keyGroup.add(key);
				count++;
			}
			if (count > 0) {
				groupedKeys.add(keyGroup);
			}
		}
		return groupedKeys;
	}

	private String getdefname(String insurancecode, List<DefdocVO> vos) {
		String name = null;
		for (DefdocVO vo : vos) {
			if (insurancecode.equals(vo.getPk_defdoc())) {
				name = vo.getName();
			}
		}
		return name;
	}

	private Map<String, List<FamEmpGroupInsDetailVO>> calcFEdetailGroupIns(String pk_org, String cstartdate,
			String cenddate, Map<String, Map<String, GroupInsSettingVO>> groupSettings,
			Map<String, String[]> groupInsRatePair, Map<String, Double> grand) throws BusinessException {
		IGroupinsuranceMaintain srv = NCLocator.getInstance().lookup(IGroupinsuranceMaintain.class);
		GroupInsuranceSettingVO[] setVOs = srv.queryByCondition(pk_org, groupInsRatePair.values());
		// 计算团保费用
		Map<String, List<FamEmpGroupInsDetailVO>> calcFEdetails = new HashMap<String, List<FamEmpGroupInsDetailVO>>();
		if (groupSettings != null && groupSettings.size() > 0 && setVOs != null && setVOs.length > 0) {
			for (Entry<String, Map<String, GroupInsSettingVO>> grpSets : groupSettings.entrySet()) {
				String pk_psndoc = grpSets.getKey();
				Double grandvalue = null == grand.get(pk_psndoc) ? 0 : grand.get(pk_psndoc);
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
								formular = null == formular ? null : formular.replace("glbdef6", salaryBase.toString());
								// 投保级距
								// double grand =
								// getGrand(pk_psndoc,cstartdate,cenddate);
								formular = null == formular ? null : formular.replace("glbdef4",
										String.valueOf(grandvalue));
								psn_sub_amount = Calculator.evalExp(null == formular ? "" : formular);
							} // 計算方式end

							if (!calcFEdetails.containsKey(pk_psndoc)) {
								famempvo.setPk_org(pk_org);
								famempvo.setPk_psndoc(pk_psndoc);
								famempvo.setdStartDate(new UFLiteralDate(cstartdate));
								famempvo.setdEndDate(new UFLiteralDate(cenddate));
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
							throw new BusinessException("員工 ["
									+ psnSet.getValue().getsName()
									+ "] 未找到團保投保費率組合: \r\n投保險種 ["
									+ ((DefdocVO) this.getBaseDao().retrieveByPK(DefdocVO.class,
											psnSet.getValue().getPk_GroupInsurance())).getName()
									+ "], \r\n投保身份 ["
									+ ((DefdocVO) this.getBaseDao().retrieveByPK(DefdocVO.class,
											psnSet.getValue().getPk_RelationType())).getName() + "] ");
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
							double insuranceamount = (int) famempvo.getInsuranceamount().doubleValue();

							if (famempvo.getIshousehold().booleanValue()) {
//								if (i == 1) {
//									famempvo.setInsuranceamount(new UFDouble((insuranceamount / hlist.size())
//											+ (insuranceamount % (hlist.size()))));
//								} else {
//
//									famempvo.setInsuranceamount(new UFDouble((insuranceamount / hlist.size())));
//								}
								
								//replace by jimmy20201029----begin
								UFDouble ub;
								ub = new UFDouble((insuranceamount / hlist.size()));
								//取商。每项算完無條件捨去
								ub = ub.setScale(0, UFDouble.ROUND_FLOOR);
								if (i == 1) {
									famempvo.setInsuranceamount(ub.add((insuranceamount % (hlist.size()))));
								} else {
									famempvo.setInsuranceamount(ub);
								}
								//replace by jimmy20201029----end
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

	private Map getCalculatePeriod(String pk_org, String cYear, String cPeriod, boolean onlyCalculate)
			throws DAOException, BusinessException {
		String strSQL = "select cyear, cperiod, cstartdate, cenddate from wa_period where pk_wa_period in"
				+ " (select pk_wa_period from wa_periodstate where "
				+ (onlyCalculate ? "" : " enableflag='Y' and isapproved='N' and ") + " pk_org = '" + pk_org
				+ "') and cyear = '" + cYear + "' and cperiod = '" + cPeriod + "'";

		Map periodMap = (Map) this.getBaseDao().executeQuery(strSQL, new MapProcessor());
		if (periodMap == null || periodMap.size() == 0) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
					"068J61035-0011")/* @res 不存在要计算团保的薪资期间 */);
		}
		return periodMap;
	}

	private List<String> getGroupInsurancePsnList(String pk_org, String cyear, String cperiod, String[] pk_psndocs)
			throws BusinessException {
		List<String> psnList = new ArrayList<String>();

		if (pk_psndocs != null) {
			psnList = new ArrayList<String>(Arrays.asList(pk_psndocs));
		} else {
			psnList = findPersonList(pk_org, cyear, cperiod);
			if (psnList == null || psnList.size() == 0) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
						"068J61035-0012")/* @res 不存在要计算团保的员工 */);
			}
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
				String strSQL = "delete from hi_psndoc_glbdef7 where pk_psndoc='" + rst.getValue().getPk_psndoc()
						+ "' and begindate='" + rst.getValue().getdStartDate() + "' and enddate='"
						+ rst.getValue().getdEndDate() + "'";
				this.getBaseDao().executeUpdate(strSQL);
				strSQL = "update hi_psndoc_glbdef7 set recordnum = recordnum +1, ts='" + new UFDateTime().toString()
						+ "', modifiedtime='" + new UFDateTime().toString() + "',modifier='"
						+ InvocationInfoProxy.getInstance().getUserId() + "' where pk_psndoc='"
						+ rst.getValue().getPk_psndoc()
						+ "' and exists(select * from hi_psndoc_glbdef7 def where hi_psndoc_glbdef7"
						+ ".pk_psndoc = def.pk_psndoc and def.recordnum = 1)";
				this.getBaseDao().executeUpdate(strSQL);

				PsndocDefVO defVO = new Glbdef7VO();
				defVO.setBegindate(rst.getValue().getdStartDate());
				defVO.setEnddate(rst.getValue().getdEndDate());
				defVO.setPk_psndoc(rst.getKey());
				defVO.setRecordnum(0);
				defVO.setLastflag(UFBoolean.TRUE);
				defVO.setDr(0);
				defVO.setAttributeValue("glbdef1", rst.getValue().getiStuffPay());
				defVO.setAttributeValue("glbdef2", rst.getValue().getiCompanyPay());
				defVO.setAttributeValue("empinsurancemoney", rst.getValue().getEmpinsurancemoney());
				defVO.setAttributeValue("fayinsurancemoney", rst.getValue().getFayinsurancemoney());
				service.insert(defVO);
			}
		}
	}

	// 以戶計算標識
	List<String> psnHouseHolded = null;

	private Map<String, GroupInsDetailVO> calculateGroupInsurance(String pk_org, String cstartdate, String cenddate,
			Map<String, Map<String, GroupInsSettingVO>> groupSettings, Map<String, String[]> groupInsRatePair,
			Map<String, String> psnIDs, Map<String, Double> grand) throws BusinessException {
		IGroupinsuranceMaintain srv = NCLocator.getInstance().lookup(IGroupinsuranceMaintain.class);
		GroupInsuranceSettingVO[] setVOs = srv.queryByCondition(pk_org, groupInsRatePair.values());
		// 计算团保费用
		Map<String, GroupInsDetailVO> calcRst = new HashMap<String, GroupInsDetailVO>();
		String errorMsg = "";
		psnHouseHolded = new ArrayList<String>();
		if (groupSettings != null && groupSettings.size() > 0 && setVOs != null && setVOs.length > 0) {
			for (Entry<String, Map<String, GroupInsSettingVO>> grpSets : groupSettings.entrySet()) {
				errorMsg = calculateGroupInsByPsn(pk_org, cstartdate, cenddate, psnIDs, setVOs, calcRst, grpSets, grand);
			}
		}

		if (errorMsg.length() > 0) {
			errorMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt", "068J61035-0013")/*
																												 * @
																												 * res
																												 * 未找到團保險種檔案
																												 */
					+ "：\r\n" + errorMsg;
			throw new BusinessException(errorMsg);
		}
		return calcRst;
	}

	private String calculateGroupInsByPsn(String pk_org, String cstartdate, String cenddate,
			Map<String, String> psnIDs, GroupInsuranceSettingVO[] setVOs, Map<String, GroupInsDetailVO> calcRst,
			Entry<String, Map<String, GroupInsSettingVO>> grpSets, Map<String, Double> grand) throws BusinessException,
			DAOException {
		String pk_psndoc = grpSets.getKey();
		double grandvalue = null == grand.get(pk_psndoc) ? 0 : grand.get(pk_psndoc);
		String errorMsg = "";
		for (Entry<String, GroupInsSettingVO> psnSet : grpSets.getValue().entrySet()) {
			GroupInsuranceSettingVO setting = getGroupInsSetting(setVOs, psnSet.getValue().getPk_GroupInsurance(),
					psnSet.getValue().getPk_RelationType());
			if (setting == null) {
				PsndocDAO dao = new PsndocDAO();
				PsndocAggVO psn = dao.queryByPk(PsndocAggVO.class, grpSets.getKey());
				DefdocVO insr = (DefdocVO) this.getBaseDao().retrieveByPK(DefdocVO.class,
						psnSet.getValue().getPk_GroupInsurance());
				DefdocVO psntype = (DefdocVO) this.getBaseDao().retrieveByPK(DefdocVO.class,
						psnSet.getValue().getPk_RelationType());
				errorMsg += " 員工 [" + psn.getParentVO().getCode() + "], 投保險種 [" + insr.getName() + "], 投保身份 ["
						+ psntype.getName() + "] \r\n";
				continue;
			}
			// 根據加退保日期判斷是否計算
			// UFLiteralDate curMonthStart = new
			// UFLiteralDate(cstartdate);
			UFLiteralDate curMonthEnd = new UFLiteralDate(cenddate);
			// 2017-12新規：只要包含本月最後一天就計算全月保費
			boolean isCalc = (psnSet.getValue().getdStartDate().isSameDate(curMonthEnd) || psnSet.getValue()
					.getdStartDate().before(curMonthEnd))
					&& (psnSet.getValue().getdEndDate().isSameDate(curMonthEnd) || psnSet.getValue().getdEndDate()
							.after(curMonthEnd));

			if (isCalc) // 計算全月start
			{
				if (setting != null) // 按團保險種計算start
				{
					calculateGroupInsBySetting(pk_org, cstartdate, cenddate, psnIDs, calcRst, pk_psndoc, psnSet,
							setting, grandvalue);
				} // 按團保險種計算end
			}// 計算全月end
		}
		return errorMsg;
	}

	private Map<String, Map<String, UFDouble>> insComStates;

	/**
	 * @return insComStates
	 */
	public Map<String, Map<String, UFDouble>> getInsComStates() {
		if (insComStates == null) {
			insComStates = new HashMap<String, Map<String, UFDouble>>();
		}
		return insComStates;
	}

	/**
	 * @param insComStates
	 *            要設定的 insComStates
	 */
	public void setInsComStates(Map<String, Map<String, UFDouble>> insComStates) {
		this.insComStates = insComStates;
	}

	private void calculateGroupInsBySetting(String pk_org, String cstartdate, String cenddate,
			Map<String, String> psnIDs, Map<String, GroupInsDetailVO> calcRst, String pk_psndoc,
			Entry<String, GroupInsSettingVO> psnSet, GroupInsuranceSettingVO setting, Double grandvalue)
			throws BusinessException {
		UFDouble psn_sub_amount = UFDouble.ZERO_DBL;
		UFDouble salaryBase = (psnSet.getValue().getiSalaryBase() == null || psnSet.getValue().getiSalaryBase()
				.equals(UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL : psnSet.getValue().getiSalaryBase();
		UFDouble times = (setting.getDtimes() == null || setting.getDtimes().equals(UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL
				: setting.getDtimes();
		UFDouble upper = (setting.getDupper() == null || setting.getDupper().equals(UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL
				: setting.getDupper();
		UFDouble rate = (setting.getDrate() == null || setting.getDrate().equals(UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL
				: setting.getDrate();
		if (CalcModelEnum.FIXAMOUNT.equalsValue(setting.getIcalmode())) // 計算方式start
		{
			// 定額
			// 即為所訂之金額
			psn_sub_amount = (setting.getDfixamount() == null || setting.getDfixamount().equals(UFDouble.ZERO_DBL)) ? UFDouble.ZERO_DBL
					: setting.getDfixamount();
		} else if (CalcModelEnum.SALARYBASE.equalsValue(setting.getIcalmode())) {
			// 保薪基數
			// 計算方式為保薪基數*倍數，如小於等於上限，則使用(保薪基數*倍數)*費率
			psn_sub_amount = salaryBase.multiply(times).doubleValue() > upper.doubleValue() ? upper.multiply(rate)
					: salaryBase.multiply(times).multiply(rate);
		} else if (CalcModelEnum.FORMULAR.equalsValue(setting.getIcalmode())) {
			// 公式
			String formular = setting.getCformularstr();
			// iif( glbdef6 <= 200000 , 200000 , glbdef6
			// *
			// 200000)
			formular = null == formular ? null : formular.replace("glbdef6", salaryBase.toString());
			// 投保级距
			// double grand = getGrand(pk_psndoc,cstartdate,cenddate);
			formular = null == formular ? null : formular.replace("glbdef4", String.valueOf(grandvalue));
			psn_sub_amount = Calculator.evalExp(null == formular ? "" : formular);
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

		// MOD 以戶計算時，只要出現過員工+險種組合，就不再纍計
		// ssx added on 2019-03-09
		if (setting.getIshousehold() != null && setting.getIshousehold().booleanValue()) {
			if (psnHouseHolded.contains(pk_psndoc + setting.getCgrpinsid())) {
				return;
			} else {
				psnHouseHolded.add(pk_psndoc + setting.getCgrpinsid());
			}
		}
		// end

		// ssx added on 2019-08-22
		// 每项算完四舍五入
		psn_sub_amount = psn_sub_amount.setScale(0, UFDouble.ROUND_HALF_UP);
		// end

		// MOD by ssx on 2020-04-27
		// 按保險公司維度統計, 員工身份證號為Key
		String insuranceCom = setting.getInsurancecompany();
		if (!StringUtils.isEmpty(psnIDs.get(pk_psndoc))) {
			if (!getInsComStates().containsKey(psnIDs.get(pk_psndoc))) {
				getInsComStates().put(psnIDs.get(pk_psndoc), new HashMap<String, UFDouble>());
			}

			if (!getInsComStates().get(psnIDs.get(pk_psndoc)).containsKey(insuranceCom)) {
				getInsComStates().get(psnIDs.get(pk_psndoc)).put(insuranceCom, UFDouble.ZERO_DBL);
			}

			// 只統計個人負擔金額
			if (setting.getBselfpay().booleanValue()) {
				getInsComStates().get(psnIDs.get(pk_psndoc)).put(insuranceCom,
						getInsComStates().get(psnIDs.get(pk_psndoc)).get(insuranceCom).add(psn_sub_amount));
			}
		}
		// end MOD

		if (setting.getBselfpay().booleanValue()) {
			calcRst.get(pk_psndoc).setiStuffPay(calcRst.get(pk_psndoc).getiStuffPay().add(psn_sub_amount)); // 個人負擔
		} else {
			calcRst.get(pk_psndoc).setiCompanyPay(calcRst.get(pk_psndoc).getiCompanyPay().add(psn_sub_amount)); // 公司負擔
		}

		// MOD 總團保費下區分員工及眷屬金額
		// 後續可由參數控管，是否只統計員工自費的部分
		// ssx modified on 2019-03-08
		if (psnIDs.get(pk_psndoc).equals(psnSet.getValue().getsID())) {
			calcRst.get(pk_psndoc).setEmpinsurancemoney(
					calcRst.get(pk_psndoc).getEmpinsurancemoney().add(psn_sub_amount)); // 員工團保費
		} else {
			calcRst.get(pk_psndoc).setFayinsurancemoney(
					calcRst.get(pk_psndoc).getFayinsurancemoney().add(psn_sub_amount));// 眷屬團保費
		}
		// end
	}

	/**
	 * 计算投保级距 逻辑： 公式內新增取數來源為員工資訊維護的勞保勞退資訊中的「勞保級距」，取級距的來源是 同個期間開始日期最新的投保級距。
	 * 
	 * @param pk_psndoc
	 * @param cstartdate
	 * @param cenddate
	 * @return
	 * @throws BusinessException
	 */
	private double getGrand(String pk_psndoc, String cstartdate, String cenddate) throws BusinessException {
		String sql = "select h.glbdef4  from hi_psndoc_glbdef1 h where  h.pk_psndoc='" + pk_psndoc
				+ "' and (h.begindate<='" + cstartdate + "'" + " and (case when h.enddate is null then '9999-12-31' "
				+ "else  h.enddate end)>='" + cenddate + "'  )  order by h.begindate desc";
		// sql="select h.glbdef4  from hi_psndoc_glbdef1 h where  h.pk_psndoc='0001X110000000011GVQ' and (h.begindate<='2018-11-01'   ) order by h.begindate desc";
		IUAPQueryBS service = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		Object object = service.executeQuery(sql, new ColumnProcessor());
		Double glbdef4 = Double.parseDouble(object.toString());
		return null == glbdef4 ? 0 : glbdef4;
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
						// ssx remarded on 2020-07-12
						// 取消所有勞退行上的舊制邏輯
						// Ares.Tank 外籍或建教人员劳退清0 2018-9-12 20:47:26
						// newLaborVO.setAttributeValue(
						// "glbdef9",
						// foreignAndTeachSet.contains(vo.getPk_psndoc()) ?
						// UFBoolean.FALSE : originValues
						// .get("labor_glbdef9"));
						// end
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
				+ "inner join hi_psnorg on hi_psnjob.pk_psnorg = hi_psnorg.pk_psnorg  WHERE HI_PSNJOB.PK_PSNDOC in ( "
				+ psndocsInSQL
				+ ") AND hi_psnjob.lastflag = 'Y' and hi_psnorg.lastflag = 'Y')psn ON BD_DEFDOC.PK_DEFDOC=psn.defdoc "
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
		String strSQL = "select * from hi_psndoc_glbdef7 where pk_psndoc = '"
				+ pk_psndoc
				+ "' and begindate <= (select isnull(enddate, '9999-12-31') from hi_psndoc_glbdef7 where pk_psndoc =  hi_psndoc_glbdef7.pk_psndoc and pk_psndoc_sub = '"
				+ pk_psndoc_sub
				+ "') and enddate >= (select begindate from hi_psndoc_glbdef7 where pk_psndoc =  hi_psndoc_glbdef7"
				+ ".pk_psndoc and pk_psndoc_sub = '" + pk_psndoc_sub + "')";
		IRowSet rowSet = new DataAccessUtils().query(strSQL);

		return rowSet.size() > 0;
	}

	private void checkExistDateValid(String[] pk_psndocs, UFDate effectivedate) throws BusinessException {
		IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
		String strSQL = "";

		strSQL = "select pk_psndoc from " + PsndocDefTableUtil.getPsnLaborTablename() + " where pk_psndoc in ("
				+ new InSQLCreator().getInSQL(pk_psndocs) + ") and begindate >= '" + effectivedate.toDate().toString()
				+ "'";
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
				if (newRangeLine != null) {
					UFDouble newHealLevel = newRangeLine.getRangeupper().equals(UFDouble.ZERO_DBL) ? newRangeLine
							.getRangelower().sub(1) : newRangeLine.getRangeupper();
					if (!originHealLevel.equals(newHealLevel)
							// ssx added on 2020-04-08
							// 开始日期等于生效日期且结束日期晚于生效日期（即已经有已生效的投保记录）的不同步
							&& !(effectivedate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).isSameDate(
									new UFLiteralDate(String.valueOf(healset.get("begindate")))) || effectivedate
									.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).before(
											new UFLiteralDate(String.valueOf(healset.get("begindate")))))
					// end
					) {
						// 更新原有健保行
						updateSQLs.add("update " + PsndocDefTableUtil.getPsnHealthTablename() + " set enddate = '"
								+ effectivedate.getDateBefore(1).toLocalString()
								+ "', lastflag = 'N' where pk_psndoc_sub='" + (String) healset.get("pk_psndoc_sub")
								+ "'");
						updateSQLs.add("update " + PsndocDefTableUtil.getPsnHealthTablename()
								+ " set recordnum=isnull(recordnum,0)+1 where pk_psndoc = '" + pk_psndoc + "'");
						// 新增新的健保行
						PsndocDefVO newVO = getNewSubInfoLineByOrigin(healset, effectivedate, avgSalary, newHealLevel,
								null);
						newSubLines.add(newVO);
					}
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

			if (newRangeLine != null) {
				UFDouble newLaborLevel = newRangeLine.getRangeupper().equals(UFDouble.ZERO_DBL) ? newRangeLine
						.getRangelower().sub(1) : newRangeLine.getRangeupper();
				newRangeLine = findRangeLine(rangeTables, RangeTableTypeEnum.RETIRE_RANGETABLE.toIntValue(), avgSalary);
				UFDouble newRetireLevel = newRangeLine.getRangeupper().equals(UFDouble.ZERO_DBL) ? newRangeLine
						.getRangelower().sub(1) : newRangeLine.getRangeupper();
				if ((!originLaborLevel.equals(newLaborLevel) || !originRetireLevel.equals(newRetireLevel))
						// ssx added on 2020-04-08
						// 开始日期等于生效日期且结束日期晚于生效日期（即已经有已生效的投保记录）的不同步
						&& !(effectivedate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).isSameDate(
								new UFLiteralDate(String.valueOf(laborResult.get("begindate")))) || effectivedate
								.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).before(
										new UFLiteralDate(String.valueOf(laborResult.get("begindate")))))
				// end
				) {
					// 更新原有劳保劳退行
					updateSQLs.add("update " + PsndocDefTableUtil.getPsnLaborTablename() + " set enddate = '"
							+ effectivedate.getDateBefore(1).toLocalString() + "', glbdef15='"
							+ effectivedate.getDateBefore(1).toLocalString()
							+ "', lastflag = 'N' where pk_psndoc_sub='" + (String) laborResult.get("pk_psndoc_sub")
							+ "'");
					updateSQLs.add("update " + PsndocDefTableUtil.getPsnLaborTablename()
							+ " set recordnum=isnull(recordnum,0)+1 where pk_psndoc = '" + pk_psndoc + "'");
					// 新增新的劳保劳退行
					subInfoLines.add(getNewSubInfoLineByOrigin(laborResult, effectivedate, avgSalary, newLaborLevel,
							newRetireLevel));
				}
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
			Collection<PsnOrgVO> psnorgs = this.getBaseDao().retrieveByClause(PsnOrgVO.class,
					"pk_psndoc='" + pk_psndoc + "' and endflag='N' and lastflag='Y'");
			if (psnorgs != null && psnorgs.size() == 0) {
				continue;
			}

			UFLiteralDate orgBeginDate = psnorgs.toArray(new PsnOrgVO[0])[0].getBegindate();
			// Ares.Tank 2018-9-14 15:59:03
			// 若目前最新投保紀錄投保型態(非退保)與本次定薪的法人公司不同，則可使用此按鈕同步級距
			PsndocDefVO[] vos = service.queryByCondition(PsndocDefUtil.getPsnLaborVO().getClass(), " pk_psndoc='"
					+ pk_psndoc + "' ");
			if (vos != null && vos.length > 0) {
				for (PsndocDefVO vo : vos) {
					// ssx added on 2019-08-15
					// 離職回任時，應保證可加保
					UFLiteralDate nhiEndDate = vo.getEnddate() == null ? new UFLiteralDate("9999-12-31") : vo
							.getEnddate();
					if (nhiEndDate.before(orgBeginDate)) {
						continue;
					}
					// end

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
					// ssx added on 2019-08-15
					// 離職回任時，應保證可加保
					UFLiteralDate nhiEndDate = vo.getEnddate() == null ? new UFLiteralDate("9999-12-31") : vo
							.getEnddate();
					if (nhiEndDate.before(orgBeginDate)) {
						continue;
					}
					// end

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
			UFLiteralDate earlist = getOrgEnterDateByPsndoc(specificStartDate, pk_psndoc);

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
						// 提交身份别 glbdef17 Ares.Tank 外籍和劳教人员劳退清0 2018-11-14
						// 19:08:14
						vo.setAttributeValue("glbdef17", foreignAndTeachSet.contains(vo.getPk_psndoc()) ? null
								: (defvo != null ? defvo.getPk_defdoc() : null));
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

				reRangeOrderByPsn(pk_psndoc, PsndocDefTableUtil.getPsnLaborTablename());
				reRangeOrderByPsn(pk_psndoc, PsndocDefTableUtil.getPsnHealthTablename());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void reRangeOrderByPsn(String pk_psndoc, String tableName) throws BusinessException {
		List<String> pk_psndoc_subs = (List<String>) this.getBaseDao().executeQuery(
				"select pk_psndoc_sub from " + tableName + " where pk_psndoc='" + pk_psndoc
						+ "' order by begindate, ts", new ColumnListProcessor());

		if (pk_psndoc_subs != null && pk_psndoc_subs.size() > 0) {
			int size = pk_psndoc_subs.size() - 1;
			for (String pk_psndoc_sub : pk_psndoc_subs) {
				this.getBaseDao().executeUpdate(
						"update " + tableName + " set recordnum=" + size + ", modifiedtime='"
								+ new UFDateTime().toString() + "', modifier='"
								+ InvocationInfoProxy.getInstance().getUserId() + "' where pk_psndoc_sub='"
								+ pk_psndoc_sub + "'");
				size -= 1;
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
		// MOD (2019新规不再过滤人员别为3且国籍为菲律宾人的员工)
		// ssx modified on 2019-04-02
		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(pk_psndocs);
		String sqlStr = "select psn.* from bd_psndoc psn "
				+ "left join hi_psnjob job on job.pk_psndoc = psn.pk_psndoc and lastflag = 'Y' and endflag = 'N' and trnsevent<>4 "
				+ "where psn.pk_psndoc in (" + psndocsInSQL + ") and "
				+ "job.jobglbdef8 <> (SELECT pk_defdoc FROM BD_DEFDOCLIST defdoclist , "
				+ "BD_DEFDOC defdoc WHERE defdoclist.pk_defdoclist= defdoc.pk_defdoclist "
				+ "and defdoclist.code='HR006_0xx' and defdoc.code='C')";
		// MOD end
		List<PsndocVO> psnlist = (List<PsndocVO>) getBaseDao().executeQuery(sqlStr,
				new BeanListProcessor(PsndocVO.class));
		List<String> pk_psndoclist = new ArrayList<String>();
		if (psnlist.size() > 0) {
			for (PsndocVO psnvo : psnlist) {
				pk_psndoclist.add(psnvo.getPk_psndoc());
			}
		}
		// 取團保費率表
		IGroupinsuranceMaintain settingSrv = NCLocator.getInstance().lookup(IGroupinsuranceMaintain.class);
		GroupInsuranceSettingVO[] setting = settingSrv.queryOnDuty(pk_org);

		// 取團保職等對照
		IGroupgradeMaintain gradeSrv = NCLocator.getInstance().lookup(IGroupgradeMaintain.class);
		GroupInsuranceGradeVO[] grades = gradeSrv.queryOnDuty(pk_org);
		// 取人员信息
		IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
		// Psndoc
		PsndocVO[] psndocVOs = psnQry.queryPsndocByPks(pk_psndoclist.toArray(new String[0]));
		// 保薪调整记录
		int count = 0;
		UFLiteralDate backBeginDate = null;

		if (specificStartDate == null) {
			// 检查人员是否有團保设定
			SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
			for (String pk_psndoc : pk_psndoclist.toArray(new String[0])) {
				Collection<PsnOrgVO> psnorgs = this.getBaseDao().retrieveByClause(PsnOrgVO.class,
						"pk_psndoc='" + pk_psndoc + "' and endflag='N' and lastflag='Y'");
				if (psnorgs != null && psnorgs.size() == 0) {
					continue;
				}

				UFLiteralDate orgBeginDate = psnorgs.toArray(new PsnOrgVO[0])[0].getBegindate();

				// ssx added on 2019-12-20
				// 支持留停複職
				String pk_backTrnstype = SysInitQuery.getParaString(pk_org, "TWHR12");
				if (!StringUtils.isEmpty(pk_backTrnstype)) {
					String strJobStartDate = (String) this.getBaseDao().executeQuery(
							"select max(begindate) begindate from hi_psnjob where pk_psnorg='"
									+ psnorgs.toArray(new PsnOrgVO[0])[0].getPk_psnorg() + "' and trnstype='"
									+ pk_backTrnstype + "'", new ColumnProcessor());
					if (!StringUtils.isEmpty(strJobStartDate)) {
						if (new UFLiteralDate(strJobStartDate).after(orgBeginDate)) {
							backBeginDate = new UFLiteralDate(strJobStartDate);
						}
					}
				}
				// end

				PsndocDefVO[] vos = service.queryByCondition(PsndocDefUtil.getGroupInsuranceVO().getClass(),
						" pk_psndoc='" + pk_psndoc + "' ");

				if (vos != null && vos.length > 0) {
					// 到這裡,說明已經存在了
					// 检查是否需要保薪调整及进行保薪调整
					if (checkIsSalaryAdjAndDo(vos, pk_psndoc, pk_org, setting, grades, psndocVOs)) {
						count++;
						continue;
					}

					for (PsndocDefVO vo : vos) {
						// ssx added on 2019-08-15
						// 離職回任時，應保證可加保
						UFLiteralDate nhiEndDate = vo.getEnddate() == null ? new UFLiteralDate("9999-12-31") : vo
								.getEnddate();
						if (nhiEndDate.before(backBeginDate == null ? orgBeginDate : backBeginDate)) {
							continue;
						} else {
							throw new BusinessException("已存在團保投保設定或已進行保薪調整");
						}
						// end
					}
				}
			}
		}
		// 如果进行了保薪调整,那么就不再进行加保工作.
		if (count > 0) {
			return;
		}

		for (String pk_psndoc : pk_psndoclist.toArray(new String[0])) {
			// 根據人員查找進入日期
			UFLiteralDate earlist = getOrgEnterDateByPsndoc(specificStartDate, pk_psndoc);
			if (backBeginDate != null && backBeginDate.after(earlist)) {
				earlist = backBeginDate;
			}
			String pk_jobrank = getJobRankByPsnDate(pk_psndoc, earlist);

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
			List<PsndocDefVO> savedVOs = getNewGroupInsVO(pk_psndoc, pk_org, grpSum, setting, grades, psnVO, pk_jobrank);

			if (savedVOs != null && savedVOs.size() > 0) {
				SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
				for (PsndocDefVO vo : savedVOs) {
					vo.setBegindate(earlist);
					vo.setEnddate(null);
					service.insert(vo);
				}

				reRangeOrderByPsn(pk_psndoc, PsndocDefTableUtil.getGroupInsuranceTablename());
			}
		}
	}

	/**
	 * 檢查重複的保薪的調整,如果都沒有重複,那麼進行保薪調整
	 * 
	 * @param vos
	 * @param setting
	 *            團保費率表
	 * @param grades
	 *            取團保職等對照
	 * @return
	 */
	private boolean checkIsSalaryAdjAndDo(PsndocDefVO[] vos, String pk_psndoc, String pk_org,
			GroupInsuranceSettingVO[] setting, GroupInsuranceGradeVO[] grades, PsndocVO[] psndocVOs)
			throws BusinessException {
		// 取定调资最新一条记录的开始日期
		UFLiteralDate wadocBeginDate = getLastGroupInsWadocBase(pk_org, pk_psndoc);

		if (null == wadocBeginDate) {
			// 照理說,這裡不應該進入,因為存在定調資項目才會走到這個方法
			return false;
		}
		// 取调配记录最新一条记录的开始日期
		String strSQL = "select top 1 begindate from hi_psnjob hi_psnjob " + " where pk_psndoc = '" + pk_psndoc + "' "
				+ " and assgid = 1 and ismainjob = 'Y' and lastflag = 'Y' " + " and pk_org = '" + pk_org + "' ";
		Object value = this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
		UFLiteralDate jobLastBeginDate = null;
		try {
			jobLastBeginDate = (value == null ? null : new UFLiteralDate(String.valueOf(value)));
		} catch (Exception e) {
			jobLastBeginDate = null;
		}
		if (null == jobLastBeginDate) {
			strSQL = "select code from bd_psndoc where pk_psndoc = '" + pk_psndoc + "'";
			throw new BusinessException("员工:"
					+ String.valueOf(getBaseDao().executeQuery(strSQL, new ColumnProcessor())) + ",工作記錄未找到!");
		}
		// 最新的保薪基數
		UFDouble grpSum = getGroupInsWadocBaseSalaryByPsnDate(pk_org, pk_psndoc, wadocBeginDate);
		// 查询人员生效中的團保设定
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
		PsndocDefVO[] psndocDefVOs = service.queryByCondition(PsndocDefUtil.getGroupInsuranceVO().getClass(),
				" pk_psndoc='" + pk_psndoc + "' and isnull(enddate,'9999-12-31') = '9999-12-31' ");
		if (psndocDefVOs == null || psndocDefVOs.length <= 0) {
			// 照理来说,不应该进到这里
			return false;
		}
		// 比较开始日期
		if (jobLastBeginDate.compareTo(wadocBeginDate) != 0L) {
			// 不相等 ,職等不變

			List<PsndocDefVO> newGroupIns = new ArrayList<>();
			// 1.将当前人员的所有生效中的加保记录进行退保(结束日期为定调资项目的前一天,打上结束标志)
			if (psndocDefVOs != null && psndocDefVOs.length > 0) {
				service = new SimpleDocServiceTemplate("TWHRGLBDEF");
				for (PsndocDefVO vo : psndocDefVOs) {
					// 已經存在的保薪基數
					UFDouble grpSumOld = null;
					try {
						// MOD 補充將grpSumOld解密,使下方有相同日期與保薪基數的投保校驗 不會失靈by Andy on
						// 2019-01-28
						grpSumOld = SalaryDecryptUtil.decrypt(new UFDouble(String.valueOf(vo
								.getAttributeValue("glbdef6"))));

					} catch (Exception e) {
						grpSumOld = null;
					}
					// 如果已經有相同日期,保薪基數的投保,直接拋異常
					if (vo.getBegindate().compareTo(wadocBeginDate) == 0 && grpSumOld != null
							&& grpSumOld.compareTo(grpSum) == 0) {
						throw new BusinessException("已經進行過保薪調整!");
					}
					newGroupIns.add((PsndocDefVO) vo.clone());
					vo.setEnddate(wadocBeginDate.getDateBefore(1));
					vo.setAttributeValue("glbdef7", "Y");
					service.update(vo, true);
				}
			}
			// 2.将这些保险clone一份,日期变为定调资项目的开始日期,保薪基数用最新的

			if (newGroupIns != null && newGroupIns.size() > 0) {
				service = new SimpleDocServiceTemplate("TWHRGLBDEF");
				for (PsndocDefVO vo : newGroupIns) {
					vo.setBegindate(wadocBeginDate);
					vo.setAttributeValue("glbdef6", SalaryEncryptionUtil.encryption(grpSum.doubleValue()));
					vo.setPrimaryKey(null);
					service.insert(vo);
				}
			}
			return true;
		} else {
			// 相等
			// 認為是職等改變

			// 1.全部退保
			// 记录非入职加保的投保(包括本人非入职的加保和眷属加保)
			List<PsndocDefVO> notAutoInsList = new ArrayList<>();
			// 获取该人员上个职等所有的险种
			Set<String> oldAutoInsSet = getOldAutoInsSet(pk_psndoc, setting, grades);
			// 1.将当前人员的所有生效中的加保记录进行退保(结束日期为定调资项目的前一天,打上结束标志)
			if (psndocDefVOs != null && psndocDefVOs.length > 0) {
				service = new SimpleDocServiceTemplate("TWHRGLBDEF");
				for (PsndocDefVO vo : psndocDefVOs) {
					// 已經存在的保薪基數
					UFDouble grpSumOld = null;
					try {
						// MOD 補充將grpSumOld解密,使下方有相同日期與保薪基數的投保校驗 不會失靈by Andy on
						// 2019-01-28
						grpSumOld = SalaryDecryptUtil.decrypt(new UFDouble(String.valueOf(vo
								.getAttributeValue("glbdef6"))));
					} catch (Exception e) {
						grpSumOld = null;
					}
					// 如果已經有相同日期,保薪基數的投保,直接拋異常
					if (vo.getBegindate().compareTo(wadocBeginDate) == 0 && grpSumOld != null
							&& grpSumOld.compareTo(grpSum) == 0) {
						throw new BusinessException("已經進行過保薪調整!");
					}
					if (!oldAutoInsSet.contains(String.valueOf(vo.getAttributeValue("glbdef5")))) {
						// 非入职加保的本人险种
						notAutoInsList.add((PsndocDefVO) vo.clone());
					}
					vo.setEnddate(wadocBeginDate.getDateBefore(1));
					vo.setAttributeValue("glbdef7", "Y");
					service.update(vo, true);
				}
			}
			// 2.重新生成該職等入職加保是的團保內容
			doReGroupIns(wadocBeginDate, pk_psndoc, psndocVOs, pk_org, setting, grades);
			// 3.重新生成不為入职加保和眷属的团保内容
			for (PsndocDefVO vo : notAutoInsList) {
				vo.setBegindate(wadocBeginDate);
				vo.setAttributeValue("glbdef6", SalaryEncryptionUtil.encryption(grpSum.doubleValue()));
				vo.setPrimaryKey(null);
				service.insert(vo);
			}

			return true;
		}

	}

	/**
	 * 获取人员上一个对应职等的所有团保险种
	 * 
	 * @param pk_psndoc
	 *            人员
	 * @param wadocBeginDate
	 *            职等变更日期
	 * @return
	 * @throws BusinessException
	 */
	private Set<String> getOldAutoInsSet(String pk_psndoc, GroupInsuranceSettingVO[] setting,
			GroupInsuranceGradeVO[] grades) throws BusinessException {
		Set<String> result = new HashSet<>();
		// 获取上一个职等
		String secLastPkRange = getSecLastJobRank(pk_psndoc);
		// 获取符合改职等的保险信息
		for (GroupInsuranceSettingVO setvo : setting) {
			if (isMatchJobRank(setvo, grades, secLastPkRange)) {
				result.add(setvo.getCgrpinsid());
			}
		}
		return result;
	}

	private void doReGroupIns(UFLiteralDate addDate, String pk_psndoc, PsndocVO[] psndocVOs, String pk_org,
			GroupInsuranceSettingVO[] setting, GroupInsuranceGradeVO[] grades) throws BusinessException {
		// 根據人員查找進入日期
		UFLiteralDate earlist = addDate;
		String pk_jobrank = getJobRankByPsnDate(pk_psndoc, addDate);
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
		List<PsndocDefVO> savedVOs = getNewGroupInsVO(pk_psndoc, pk_org, grpSum, setting, grades, psnVO, pk_jobrank);

		if (savedVOs != null && savedVOs.size() > 0) {
			SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
			for (PsndocDefVO vo : savedVOs) {
				vo.setBegindate(earlist);
				vo.setEnddate(null);
				service.insert(vo);
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

	private String getJobRankByPsnDate(String pk_psndoc, UFLiteralDate checkDate) throws BusinessException {
		Collection<PsnJobVO> psnjobVOs = this.getBaseDao().retrieveByClause(
				PsnJobVO.class,
				"pk_psndoc='" + pk_psndoc + "' and '" + checkDate
						+ "' between begindate and  isnull(enddate, '9999-12-31') and ismainjob='Y' and trnsevent<>4");

		String pk_jobrank = "";

		if (psnjobVOs != null && psnjobVOs.size() > 0) {
			pk_jobrank = psnjobVOs.toArray(new PsnJobVO[0])[0].getPk_jobrank();
		}

		return pk_jobrank;
	}

	private String getSecLastJobRank(String pk_psndoc) throws BusinessException {
		Collection<PsnJobVO> psnjobVOs = this.getBaseDao().retrieveByClause(PsnJobVO.class,
				"pk_psndoc='" + pk_psndoc + "'");

		String pk_jobrank = "";
		String sec_pk_jobrank = "";
		UFLiteralDate date = new UFLiteralDate("0000-01-01");
		for (PsnJobVO vo : psnjobVOs) {
			if (vo.getBegindate().after(date)) {
				date = vo.getBegindate();
				sec_pk_jobrank = pk_jobrank;
				pk_jobrank = vo.getPk_jobrank();
			}
		}
		return sec_pk_jobrank;
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

		String strSQL = "select sum(SALARY_DECRYPT(nmoney)) grpsum from hi_psndoc_wadoc where pk_wa_item in ("
				+ waitems + ")  and pk_org= '" + pk_org + "' and pk_psndoc = '" + pk_psndoc + "' and '"
				+ salaryDate.toString()
				+ "' between begindate and isnull(enddate, '9999-12-31') and waflag='Y' and lastflag='Y'";
		Object value = this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
		UFDouble grpSum = new UFDouble(value == null ? "0.0" : String.valueOf(value));
		// MOD by ssx on 2020-02-28
		// 加解密算法变更，不能先sum再解密，只能使用数据库函数解密，再取消原来的解密方法
		return grpSum;
	}

	private List<PsndocDefVO> getNewGroupInsVO(String pk_psndoc, String pk_org, UFDouble psnSum,
			GroupInsuranceSettingVO[] setting, GroupInsuranceGradeVO[] grades, PsndocVO psndocVO, String pk_jobrank)
			throws BusinessException {
		List<PsndocDefVO> psnNhiVOs = new ArrayList<PsndocDefVO>();
		int i = 0;
		for (GroupInsuranceSettingVO setvo : setting) {
			if (isMatchJobRank(setvo, grades, pk_jobrank)) {
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
		return psnNhiVOs;
	}

	/**
	 * 不分職等,進行團保加保
	 * 
	 * @param pk_psndoc
	 * @param pk_org
	 * @param psnSum
	 * @param setting
	 * @param grades
	 * @param psndocVO
	 * @param pk_jobrank
	 * @return
	 * @throws BusinessException
	 */
	private List<PsndocDefVO> getNewGroupInsVOAll(String pk_psndoc, UFDouble psnSum, GroupInsuranceSettingVO[] setting,
			PsndocVO psndocVO) throws BusinessException {
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
			// Ares.Tank start 2018-8-8 10:35:11 团保保薪基数加密
			newGrpInsVO.setAttributeValue("glbdef6", SalaryEncryptionUtil.encryption(psnSum.getDouble()));
			// Ares.Tank end 2018-8-8 10:35:37 团保保薪基数
			newGrpInsVO.setAttributeValue("glbdef7", "N");
			newGrpInsVO.setAttributeValue("insurancecompany", setvo.getInsurancecompany());
			psnNhiVOs.add(newGrpInsVO);
		}
		return psnNhiVOs;
	}

	private boolean isMatchJobRank(GroupInsuranceSettingVO setvo, GroupInsuranceGradeVO[] grades, String pk_jobrank) {
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
				+ "',modifiedtime='" + new UFDateTime().toString() + "', modifier='"
				+ InvocationInfoProxy.getInstance().getUserId() + "' WHERE ISNULL(enddate, '9999-12-31') > '"
				+ enddate.toString() + "' AND dr=0 AND pk_psndoc = '" + pk_psndoc + "'";
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
		boolean isSameSalary = false;
		boolean ownGenerated = false;
		List<PsndocDefVO> ownVOs = null;

		strSQL = "select * from " + PsndocDefTableUtil.getGroupInsuranceTablename() + " where pk_psndoc='" + pk_psndoc
				+ "' and dr=0 and begindate <= '"
				+ effectivedate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).toString()
				+ "' and isnull(enddate, '9999-12-31') >='"
				+ effectivedate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).toString()
				+ "' and isnull(glbdef7,'N')='N'";
		List<Map> groupInsResult = (List<Map>) this.getBaseDao().executeQuery(strSQL, new MapListProcessor());

		if (groupInsResult != null && groupInsResult.size() > 0) {
			Collection<PsnJobVO> psnjobs = this.getBaseDao().retrieveByClause(
					PsnJobVO.class,
					"pk_psndoc='" + pk_psndoc + "' and '" + effectivedate
							+ "' between begindate and isnull(enddate, '9999-12-31')");
			String pk_org = psnjobs.toArray(new PsnJobVO[0])[0].getPk_org();
			String pk_jobrank = psnjobs.toArray(new PsnJobVO[0])[0].getPk_jobrank();

			// 取團保費率表
			IGroupinsuranceMaintain settingSrv = NCLocator.getInstance().lookup(IGroupinsuranceMaintain.class);
			GroupInsuranceSettingVO[] setting = settingSrv.queryOnDuty(pk_org);

			// 取團保職等對照
			IGroupgradeMaintain gradeSrv = NCLocator.getInstance().lookup(IGroupgradeMaintain.class);
			GroupInsuranceGradeVO[] grades = gradeSrv.queryOnDuty(pk_org);

			// 本人身份
			String psnOwn = (String) this
					.getBaseDao()
					.executeQuery(
							"select pk_defdoc from bd_defdoc doc inner join bd_defdoclist lst on lst.pk_defdoclist = doc.pk_defdoclist where lst.code = 'TWHR010' and doc.code = '0'",
							new ColumnProcessor());

			List<PsndocDefVO> newSubLines = new ArrayList<PsndocDefVO>();
			for (Map groupSet : groupInsResult) {
				UFDouble originSalaryBase = groupSet.get("glbdef6") == null ? UFDouble.ZERO_DBL : SalaryDecryptUtil
						.decrypt(new UFDouble(String.valueOf(groupSet.get("glbdef6"))));
				// MOD (Redmine:21795) 去除千位取整操作
				// ssx modified on 2018-08-23
				// UFDouble newSalaryBase = avgSalary;// .div(1000).setScale(0,
				// UFDouble.ROUND_HALF_UP).multiply(1000);
				if (!originSalaryBase.equals(avgSalary)) {
					if (!ownGenerated) {
						ownVOs = this.getNewGroupInsVO(pk_psndoc, pk_org, avgSalary, setting, grades, (PsndocVO) this
								.getBaseDao().retrieveByPK(PsndocVO.class, pk_psndoc), pk_jobrank);
						newSubLines.addAll(ownVOs);
						ownGenerated = true;
					}
					// 更新原有團保設定行
					updateSQLs.add("update " + PsndocDefTableUtil.getGroupInsuranceTablename() + " set enddate = '"
							+ effectivedate.getDateBefore(1).toLocalString()
							+ "',glbdef7='Y', lastflag = 'N', modifiedtime='" + new UFDateTime().toString()
							+ "', modifier='" + InvocationInfoProxy.getInstance().getUserId()
							+ "' where pk_psndoc_sub='" + (String) groupSet.get("pk_psndoc_sub") + "'");
					updateSQLs.add("update " + PsndocDefTableUtil.getGroupInsuranceTablename()
							+ ", set recordnum=isnull(recordnum,0)+1, modifiedtime='" + new UFDateTime().toString()
							+ "', modifier='" + InvocationInfoProxy.getInstance().getUserId() + "' where pk_psndoc = '"
							+ pk_psndoc + "'");

					// 如果不是本人，正常加保（家屬延保）
					// 或者是本人，但不在職等列表中（本人延保）
					if (!psnOwn.equals(groupSet.get("glbdef4"))
							|| !containsInOwnVOs((String) groupSet.get("glbdef5"), ownVOs)) {
						// 新增新的團保設定行
						PsndocDefVO newVO = getNewSubInfoLineByOrigin4GroupIns(groupSet, effectivedate, avgSalary,
								grades, pk_jobrank);
						newSubLines.add(newVO);
					}
				} else {
					isSameSalary = true;
					break;
				}
			}

			if (!isSameSalary) {
				int rowno = newSubLines.size() - 1;
				for (PsndocDefVO vo : newSubLines) {
					vo.setBegindate(effectivedate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE));
					vo.setRecordnum(rowno--);
				}
				subInfoLines.addAll(newSubLines);
			}
		}
		return subInfoLines;
	}

	private boolean containsInOwnVOs(String cinsid, List<PsndocDefVO> ownVOs) {
		if (ownVOs != null && ownVOs.size() > 0) {
			for (PsndocDefVO vo : ownVOs) {
				if (cinsid.equals(vo.getAttributeValue("glbdef5"))) {
					return true;
				}
			}
		}
		return false;
	}

	private PsndocDefVO getNewSubInfoLineByOrigin4GroupIns(Map<String, Object> originValues, UFDate effectivedate,
			UFDouble avgSalary, GroupInsuranceGradeVO[] grades, String pk_jobrank) throws BusinessException {
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
		newHealVO.setAttributeValue("glbdef6", new UFDouble(SalaryEncryptionUtil.encryption(avgSalary.doubleValue())));
		newHealVO.setAttributeValue("glbdef7", originValues.get("glbdef7"));
		newHealVO.setAttributeValue("insurancecompany", originValues.get("insurancecompany"));
		return newHealVO;

	}

	@SuppressWarnings("all")
	@Override
	public void renewRangeEx(String pk_org, String[] pk_psndocs, String[] pk_wa_class, UFDate cbaseDate,
			String avgMonCount, UFDate effectivedate) throws BusinessException {
		// 查詢本次加保组织的法人组织 Ares.Tank
		String[] pkLegalOrgs = { pk_org };
		pkLegalOrg = LegalOrgUtils.getLegalOrgByOrgs(pkLegalOrgs).get(pk_org);

		// ssx added on 2020-01-14
		// 增加HRWAWNC03取值，判斷平均薪資按日或月計算
		String avgRef = SysInitQuery.getParaString(pk_org, "TWHR16");
		// end

		InSQLCreator insql = new InSQLCreator();
		String waclassInSQL = insql.getInSQL(pk_wa_class);
		// 根据薪资方案筛选出公共薪资项目中勾选了平均薪资项的薪资薪资项
		List<Map<String, String>> walist = (List<Map<String, String>>) this.getBaseDao().executeQuery(
				"SELECT wa_item.itemkey as itemkey, iproperty from WA_ITEM INNER JOIN TWHR_WAITEM_30 on "
						+ "WA_ITEM.PK_WA_ITEM= TWHR_WAITEM_30.PK_WA_ITEM where WA_ITEM.PK_WA_ITEM IN ("
						+ "select distinct PK_WA_ITEM from WA_CLASSITEM where PK_WA_CLASS in (" + waclassInSQL + "))"
						+ "and AVGCALCSALFLAG='Y' and TWHR_WAITEM_30.isnhiitem_30 = 'Y' ", new MapListProcessor());
		if (walist.size() == 0) {
			throw new BusinessException("没有是否计算劳保健保的薪资项");
		}

		// ssx added on 2020-04-26
		// 详见#34655问题描述20200426-01.docx
		IBasedocPubQuery baseDocQry = NCLocator.getInstance().lookup(IBasedocPubQuery.class);
		BaseDocVO baseDocVO = baseDocQry.queryBaseDocByCode(pk_org, "TWSP0009");

		if (baseDocVO == null || baseDocVO.getNumbervalue() == null) {
			throw new BusinessException("未找到臺灣薪資及勞健保參數 [TWSP0009] 關於基本工資(月)的設定。");
		}
		//

		// 先获取基准日期，再根据基准日期和平均月数获取开始日期
		// 获取基准日期往前推的日期,返回的日期即为需要计算?目既掌?
		UFDate baseMonthDate = ComdateUtils.calendarclac(cbaseDate, avgMonCount);

		String cumn = "";
		for (Map<String, String> waitem : walist) {
			String neg = Integer.valueOf(String.valueOf(waitem.get("iproperty"))) == 1 ? "(-1)*" : "";
			cumn += "(" + neg + "SALARY_DECRYPT(isnull(" + waitem.get("itemkey") + ",0)))+";
		}

		String leaveType = SysInitQuery.getParaString(pk_org, "TWHR11");
		String backType = SysInitQuery.getParaString(pk_org, "TWHR12");

		// 取所有人的平均月薪
		if (pk_psndocs != null && pk_psndocs.length > 0) {
			// 取級距表
			this.getRangeTables(pk_org, effectivedate);
			// 校驗薪資期間有效性
			// avgmoncount, effectivedate);
			// 檢查是否存在生效日期以後的起始日期
			checkExistDateValid(pk_psndocs, effectivedate);

			for (String pk_psndoc : pk_psndocs) {
				// 在起始日期~基準日期間同時出現入職離職、停留複職時，取消同步
				if (isCanceledPsn(pk_psndoc, baseMonthDate, cbaseDate, leaveType, backType)) {
					continue;
				}

				UFLiteralDate enterDate = cbaseDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE);
				Collection<PsnOrgVO> psnOrgVOs = this.getBaseDao().retrieveByClause(
						PsnOrgVO.class,
						"'" + cbaseDate + "' between begindate and isnull(enddate, '9999-12-31') and pk_psndoc='"
								+ pk_psndoc + "'");
				if (psnOrgVOs != null && psnOrgVOs.size() > 0) {
					enterDate = psnOrgVOs.toArray(new PsnOrgVO[0])[0].getBegindate();
				}

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
				UFBoolean flag = isMonthlySalary(pk_psndoc, cbaseDate);
				// UFDouble avgSalary = UFDouble.ZERO_DBL;
				UFDouble salary = UFDouble.ZERO_DBL;
				boolean isMonthAvg = avgRef.equals("1");
				if (flag.booleanValue()) {
					salary = getAvgSalary(cbaseDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE),
							(baseMonthDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).before(enterDate) ? enterDate
									: baseMonthDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE)), waperiodlist,
							// ssx added on 2020-01-14
							isMonthAvg);

					// ssx added on 2020-04-26
					// 详见#34655问题描述20200426-01.docx
					if (salary.doubleValue() < baseDocVO.getNumbervalue().doubleValue()) {
						salary = baseDocVO.getNumbervalue();
					}
					//
				} else {
					// 非月薪
					UFDouble avgSalary = getAvgSalary(cbaseDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE),
							baseMonthDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE), waperiodlist,
							// ssx added on 2020-01-14
							isMonthAvg);
					UFDouble actavgSalary = getnoavgsalary(pk_psndoc, cbaseDate, baseMonthDate, waperiodlist);
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
					Logger.error(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
							"068J61035-0031", null, new String[] { vos[0].getCode() })
					/*
					 * * 員工 [ { 0 } ] 在指定的期間範圍內沒有薪資資料 ， 無法計算平均薪資 。
					 */
					);
					continue;
				}

				List<String> updateSQLs = new ArrayList<String>();

				// 處理劳保劳退
				// 同步投保級距異常 by George 20200306 缺陷Bug #33603
				// 查詢級距時，平均薪資無條件捨去，級距在整數位時是連續的，但在Double時是非連續的，0跟1之間無從判斷，導致NullPointerException
				List<PsndocDefVO> subInfoLines = dealLaborInfoset(effectivedate, pk_psndoc,
						new UFDouble(Math.floor(salary.toDouble())), updateSQLs);

				// 處理健保
				// 同步投保級距異常 by George 20200306 缺陷Bug #33603
				// 查詢級距時，平均薪資無條件捨去
				// 查詢級距時，平均薪資無條件捨去，級距在整數位時是連續的，但在Double時是非連續的，0跟1之間無從判斷，導致NullPointerException
				dealHealInfoset(effectivedate, pk_psndoc, new UFDouble(Math.floor(salary.toDouble())), updateSQLs,
						subInfoLines);

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

	private boolean isCanceledPsn(String pk_psndoc, UFDate baseMonthDate, UFDate cbaseDate, String leaveType,
			String backType) throws BusinessException {
		UFLiteralDate startDate = baseMonthDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE);
		UFLiteralDate endDate = cbaseDate.toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).getDateBefore(1);

		boolean rtn = false;
		Collection<PsnJobVO> psnJobVOs = this.getBaseDao().retrieveByClause(
				PsnJobVO.class,
				"begindate <= '" + endDate + "' and isnull(enddate, '9999-12-31') >= '" + startDate
						+ "' and pk_psndoc='" + pk_psndoc + "'");

		TrnstypeVO typeVO = (TrnstypeVO) this.getBaseDao()
				.retrieveByClause(TrnstypeVO.class, TrnstypeVO.TRNSTYPECODE + "='02'").toArray(new TrnstypeVO[0])[0];

		boolean isIn = false;
		boolean isOut = false;
		boolean isLeave = false;
		boolean isBack = false;
		if (psnJobVOs != null && psnJobVOs.size() > 0) {
			for (PsnJobVO psnJobVO : psnJobVOs) {
				if (psnJobVO.getTrnsevent() == 1
						&& (psnJobVO.getBegindate().isSameDate(startDate) || psnJobVO.getBegindate().after(startDate))) {
					isIn = true;
				} else if (psnJobVO.getTrnsevent() == 4) {
					isOut = true;
				} else if (psnJobVO.getTrnsevent() == 3) {
					if (!StringUtils.isEmpty(leaveType) && leaveType.equals(psnJobVO.getTrnstype())) {
						isLeave = true;
					} else if (!StringUtils.isEmpty(backType) && backType.equals(psnJobVO.getTrnstype())) {
						isBack = true;
					}
				} else if (psnJobVO.getTrnsevent() == 2) {
					if (typeVO.getPk_trnstype().equals(psnJobVO.getTrnstype())) {
						isIn = true;
					}
				}
			}

			if (isIn || isOut || isLeave || isBack) {
				rtn = true;
			}
		}

		return rtn;
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
		// 初始化健教生和外籍人员列表
		NhiCalcVO[] nhiFinalVOs = { new NhiCalcVO() };
		nhiFinalVOs[0].setPk_psndoc(psnJobVO.getPk_psndoc());
		initForeignAndTeachSet(nhiFinalVOs);
		// mod 回写逻辑修改 Ares.Tank 2018-10-2 10:06:23

		UFDouble nmoney = new UFDouble(
				String.valueOf(this
						.getBaseDao()
						.executeQuery(
								"SELECT SALARY_ENCRYPT(SUM(SALARY_DECRYPT(wd.NMONEY))) nmoney FROM "
										+ " hi_psndoc_wadoc wd INNER JOIN  wa_item wi ON  wd.pk_wa_item = wi.pk_wa_item INNER JOIN  twhr_waitem_30 tw ON  tw.pk_wa_item = wi.pk_wa_item WHERE "
										+ " wd.pk_psndoc = '"
										+ psnJobVO.getPk_psndoc()
										+ "' AND '"
										+ startDate.toString()
										+ "' BETWEEN wd.begindate AND NVL(wd.enddate, '9999-12-31') AND wd.lastflag = 'Y' AND tw.isnhiitem_30='Y' AND wi.isinhi = 'Y'",
								new ColumnProcessor())));

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
		// 是否生成了劳保记录
		boolean isLabor = false;
		// 是否生成了健保记录
		boolean isHealth = false;
		// 最新的投保结束日期
		UFLiteralDate lastBeginDate = null;
		// 记录最新日期的位置
		int lastLoc = 0;
		if (vos != null && vos.length > 0) {
			for (int i = 0; i < vos.length; i++) {
				if (null == lastBeginDate || lastBeginDate.after(vos[i].getBegindate())) {
					lastBeginDate = vos[i].getBegindate();
					lastLoc = i;
				}
				if (vos[i] == null || !vos[i].getLastflag().booleanValue()) {
					continue;// 寻找最新的投保记录
				}
				lastLaborPkList.add(vos[i].getPk_psndoc_sub());
				PsndocDefVO newLaborVO = (PsndocDefVO) vos[i].clone();
				// 是否劳保投保
				newLaborVO.setAttributeValue("glbdef10", "Y");
				// 是否劳退投保(健教生和外籍不投保劳退)
				newLaborVO
						.setAttributeValue("glbdef11", foreignAndTeachSet.contains(vos[i].getPk_psndoc()) ? "N" : "Y");

				newLaborVO.setBegindate(startDate);
				newLaborVO.setEnddate(new UFLiteralDate("9999-12-31"));

				// 劳退开始和结束时间
				newLaborVO.setAttributeValue("glbdef14", foreignAndTeachSet.contains(vos[i].getPk_psndoc()) ? null
						: startDate);
				newLaborVO.setAttributeValue("glbdef15", foreignAndTeachSet.contains(vos[i].getPk_psndoc()) ? null
						: new UFLiteralDate("9999-12-31"));

				// newLaborVO.setAttributeValue("legalpersonorg", pkLegalOrg);
				newLaborVO.setAttributeValue("insuranceform", ADD_FORM);

				newLaborVO.setAttributeValue("glbdef2", new UFDouble(nmoney));
				if (savedVOList.size() <= 0) {
					// 有可能出現臟數據,保證只有一條薪資
					isLabor = true;
					savedVOList.add(newLaborVO);
				}
			}
			// 出现脏数据的解决方案,采用开始时间最新的一条数据
			if (isLabor == false) {
				lastLaborPkList.add(vos[lastLoc].getPk_psndoc_sub());
				PsndocDefVO newLaborVO = (PsndocDefVO) vos[lastLoc].clone();
				// 是否劳保投保
				newLaborVO.setAttributeValue("glbdef10", "Y");
				// 是否劳退投保(健教生和外籍不投保劳退)
				newLaborVO.setAttributeValue("glbdef11", foreignAndTeachSet.contains(vos[lastLoc].getPk_psndoc()) ? "N"
						: "Y");

				newLaborVO.setBegindate(startDate);
				newLaborVO.setEnddate(new UFLiteralDate("9999-12-31"));

				// 劳退开始和结束时间
				newLaborVO.setAttributeValue("glbdef14",
						foreignAndTeachSet.contains(vos[lastLoc].getPk_psndoc()) ? null : startDate);
				newLaborVO.setAttributeValue("glbdef15",
						foreignAndTeachSet.contains(vos[lastLoc].getPk_psndoc()) ? null : new UFLiteralDate(
								"9999-12-31"));

				// newLaborVO.setAttributeValue("legalpersonorg", pkLegalOrg);
				newLaborVO.setAttributeValue("insuranceform", ADD_FORM);

				newLaborVO.setAttributeValue("glbdef2", new UFDouble(nmoney));
				if (savedVOList.size() <= 0) {
					// 有可能出現臟數據,保證只有一條薪資
					isLabor = true;
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
		String refTransType = SysInitQuery.getParaString(psnJobVO.getPk_hrorg(), "TWHR11").toString();
		if (refTransType != null) {

			UFLiteralDate healEnddate = NCLocator
					.getInstance()
					.lookup(IPsndocwadocManageService.class)
					.getTransTypeEndDate(psnJobVO.getPk_org(), psnJobVO.getBegindate(), refTransType,
							psnJobVO.getPk_psndoc());
			if (vos != null && vos.length > 0) {
				for (PsndocDefVO vo : vos) {
					// 健保資訊：留停復職或是留職停薪退保時，員工與眷屬應要一起加保一起退保
					/*
					 * if (vo == null || !vo.getLastflag().booleanValue()) {
					 * continue;// 寻找最新的投保记录 }
					 */
					if (vo != null && vo.getEnddate() != null && healEnddate.isSameDate(vo.getEnddate())) {
						lastHealthPkList.add(vo.getPk_psndoc_sub());
						PsndocDefVO newHealVO = (PsndocDefVO) vo.clone();
						// 是否健保投保
						newHealVO.setAttributeValue("glbdef14", "Y");
						newHealVO.setBegindate(startDate);
						newHealVO.setEnddate(new UFLiteralDate("9999-12-31"));

						// newLaborVO.setAttributeValue("legalpersonorg",
						// pkLegalOrg);
						newHealVO.setAttributeValue("insuranceform", ADD_FORM);
						newHealVO.setAttributeValue("glbdef17", glbdef17);

						newHealVO.setAttributeValue("glbdef6", new UFDouble(nmoney));

						isHealth = true;
						savedVOList.add(newHealVO);

					}

				}

			}
		}
		// 加保
		if (savedVOList != null && savedVOList.size() > 0) {
			for (PsndocDefVO vo : savedVOList) {
				vo.setPk_psndoc_sub(null);
				vo.setLastflag(UFBoolean.TRUE);
				service.insert(vo);
			}
		}
		// 回写最新標誌--保險起見,回寫以前的全部為n,只保留一條最新數據
		StringBuilder strSQLSB = new StringBuilder();
		if (isLabor) {
			for (String lastLaborPk : lastLaborPkList) {
				strSQLSB.delete(0, strSQLSB.length());
				strSQLSB.append("UPDATE ").append(PsndocDefTableUtil.getPsnLaborTablename())
						.append(" SET lastflag = 'N' ").append(" WHERE PK_PSNDOC_SUB = '").append(lastLaborPk)
						.append("'");
				this.getBaseDao().executeUpdate(strSQLSB.toString());
			}
		}

		if (isHealth) {
			for (String lastHealthPk : lastHealthPkList) {
				strSQLSB.delete(0, strSQLSB.length());
				strSQLSB.append("UPDATE ").append(PsndocDefTableUtil.getPsnHealthTablename())
						.append(" SET lastflag = 'N' ").append(" WHERE PK_PSNDOC_SUB = '").append(lastHealthPk)
						.append("'");
				this.getBaseDao().executeUpdate(strSQLSB.toString());
			}
		}

		// 健保資訊與勞保勞退資訊皆需要按時間排序
		List<Glbdef2VO> healthlist = (List<Glbdef2VO>) this.getBaseDao().retrieveByClause(Glbdef2VO.class,
				"pk_psndoc='" + psnJobVO.getPk_psndoc() + "'");
		ListSort(healthlist);
		int i = 0;
		for (Glbdef2VO vo : healthlist) {
			vo.setRecordnum(i);
			i++;
		}
		this.getBaseDao().updateVOArray(healthlist.toArray(new Glbdef2VO[0]));
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
	 * 删除 健保信息，劳保劳退信息，团保信息开始日期大于离职日期(或停薪日期)信息
	 * 
	 * @param pk_psndoc
	 * @param enddate
	 * 
	 * @throws BusinessException
	 */
	@Override
	public void deletePNI(String pk_psndoc, UFLiteralDate enddate) throws BusinessException {

		String strSQL = "delete  from " + PsndocDefTableUtil.getPsnLaborTablename() + "  "
				+ "   WHERE ISNULL(begindate, '9999-12-31') >= '" + enddate.toString() + "' AND dr=0 AND pk_psndoc = '"
				+ pk_psndoc + "'";
		this.getBaseDao().executeUpdate(strSQL);

		//
		strSQL = "delete  from " + PsndocDefTableUtil.getPsnHealthTablename() + " "
				+ "  WHERE ISNULL(begindate, '9999-12-31') >= '" + enddate.toString() + "' AND dr=0 AND pk_psndoc = '"
				+ pk_psndoc + "'";
		this.getBaseDao().executeUpdate(strSQL);

		String infosetCode = PsndocDefTableUtil.getGroupInsuranceTablename();
		if (StringUtils.isEmpty(infosetCode)) {
			throw new BusinessException("無法找到團保子集設置，請檢查自定義項(TWHR000)的設定內容。");
		}

		strSQL = "delete  from " + infosetCode + " " + " WHERE ISNULL(begindate, '9999-12-31') >= '"
				+ enddate.toString() + "' AND dr=0 AND pk_psndoc = '" + pk_psndoc + "'";
		this.getBaseDao().executeUpdate(strSQL);

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
		// 初始化健教生和外籍人员列表
		NhiCalcVO[] nhiFinalVOs = { new NhiCalcVO() };
		nhiFinalVOs[0].setPk_psndoc(pk_psndoc);
		initForeignAndTeachSet(nhiFinalVOs);
		// 修改的行数,如果没有更新则说明没有投保记录
		int resultRow = 0;

		// 回写结束日期和投保形态--劳保
		String strSQL = "UPDATE " + PsndocDefTableUtil.getPsnLaborTablename() + " SET enddate='" + endDate.toString()
				+ "' , insuranceform = "
				+ DEL_FORM
				// 調配記錄留職停薪時，不應清除勞保勞退與健保的生效標記 by George 20200304 缺陷Bug #33606
				// 員工 劳保劳退信息(hi_psndoc_glbdef1) 的 是否劳保投保(glbdef10) 的勾勾不應被勾銷
				+ " WHERE ISNULL(enddate, '9999-12-31') > '" + endDate.toString() + "' AND dr=0 AND pk_psndoc = '"
				+ pk_psndoc + "'";
		resultRow = this.getBaseDao().executeUpdate(strSQL);

		strSQL = "UPDATE " + PsndocDefTableUtil.getPsnLaborTablename() + " SET glbdef15='"
				+ endDate.toString()
				// 調配記錄留職停薪時，不應清除勞保勞退與健保的生效標記 by George 20200304 缺陷Bug #33606
				// 員工 劳保劳退信息(hi_psndoc_glbdef1) 的 是否劳退投保(glbdef11) 的勾勾不應被勾銷
				+ "' WHERE ISNULL(glbdef15, '9999-12-31') > '" + endDate.toString() + "' AND dr=0 AND pk_psndoc = '"
				+ pk_psndoc + "'";
		// 外籍和健教生不加劳退
		if (!foreignAndTeachSet.contains(pk_psndoc)) {
			resultRow += this.getBaseDao().executeUpdate(strSQL);
		}

		// : 補充新增健保欄位及回寫邏輯
		// ssx modified on 2018-03-19
		// for changes of psn leave
		// 離職自動退保時預製退保原因別為：2.轉出 ×

		// Ares.Tank mod on 2018-9-15 22:55:16
		// 新逻辑:投保狀態為退保、健保退保原因別為1.退保、健保退保原因說明別為1.離職。--Jessie
		// 在退保之前，修改数据，将结束日期为空或9999-12-31的数据更改是否最后的flag为Y
		String flagstr = "update " + PsndocDefTableUtil.getPsnHealthTablename() + " set lastflag='Y' where pk_psndoc='"
				+ pk_psndoc + "' and (enddate is null or enddate='9999-12-31' or enddate = '~')";
		this.getBaseDao().executeUpdate(flagstr);
		// 退保說明別為：1.離職
		Collection defvos = this.getBaseDao().retrieveByClause(DefdocVO.class,
				"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI005')");
		String glbdef18 = (defvos == null || defvos.size() == 0) ? "~" : ((DefdocVO) defvos.toArray()[0])
				.getPk_defdoc();
		defvos = this.getBaseDao().retrieveByClause(DefdocVO.class,
				"code='1' and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'NHI006')");
		String glbdef19 = (defvos == null || defvos.size() == 0) ? "~" : ((DefdocVO) defvos.toArray()[0])
				.getPk_defdoc();
		/*
		 * String updatesql = "update " +
		 * PsndocDefTableUtil.getPsnHealthTablename() +
		 * " set lastflag='Y' WHERE dr=0 AND " +
		 * "glbdef3=( SELECT ID FROM BD_PSNDOC " + "WHERE PK_PSNDOC='" +
		 * pk_psndoc + "') and " +
		 * "(enddate is null or enddate='9999-12-31' or enddate = '~')";
		 */
		// this.getBaseDao().executeUpdate(updatesql);

		/*
		 * String updatesqls = "update " +
		 * PsndocDefTableUtil.getPsnHealthTablename() +
		 * " set lastflag='N' WHERE dr=0 AND " +
		 * "glbdef3 !=( SELECT ID FROM BD_PSNDOC " + "WHERE PK_PSNDOC='" +
		 * pk_psndoc + "') and " +
		 * "(enddate is null or enddate='9999-12-31' or enddate = '~')";
		 * this.getBaseDao().executeUpdate(updatesqls);
		 */
		strSQL = "UPDATE "
				+ PsndocDefTableUtil.getPsnHealthTablename()
				+ " SET enddate='"
				+ endDate.toString()
				// 調配記錄留職停薪時，不應清除勞保勞退與健保的生效標記 by George 20200304 缺陷Bug #33606
				// 員工 健保信息(hi_psndoc_glbdef2) 的 是否投保(glbdef14) 的勾勾不應被勾銷
				+ "' , glbdef18='" + glbdef18 + "', glbdef19='" + glbdef19 + "' , insuranceform = " + DEL_FORM
				+ " WHERE ISNULL(enddate, '9999-12-31') > '" + endDate.toString() + "' AND dr=0 AND pk_psndoc = '"
				+ pk_psndoc + "'" + " and (enddate is null or enddate='9999-12-31' or enddate = '~') ";
		resultRow += this.getBaseDao().executeUpdate(strSQL);
		String clearsql = "update " + PsndocDefTableUtil.getPsnHealthTablename() + " set glbdef18='~', "
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
	public UFBoolean isMonthlySalary(String pk_psndoc, UFDate basedate) throws BusinessException {
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
				ismonthsalary = (UFBoolean) psnjob.getAttributeValue("ismonsalary");
			}
		}
		return ismonthsalary == null ? UFBoolean.FALSE : ismonthsalary;
	}

	/**
	 * 返回日薪(基准时间段--取左不取右，薪资期间段--既取左也取右)
	 * 
	 * @param basedateperiod
	 * @param waperiodlist
	 * @return
	 */
	private UFDouble getAvgSalary(UFLiteralDate basedate, UFLiteralDate basemonthdate,
			List<Map<String, Object>> waperiodlist, boolean basedOnMonth) {
		// 取薪资期间和基准日期段的时间交集
		int sumSalary = 0;
		int sumDays = 0;
		int sumMonths = 0;

		if (!basedate.isSameDate(basemonthdate)) {
			for (Map<String, Object> waperiod : waperiodlist) {
				String waperiodstartdate = (String) waperiod.get("cstartdate");
				String waperiodenddate = (String) waperiod.get("cenddate");
				String periodtime = ComdateUtils.getAlphalDate(basemonthdate.toString(), basedate.getDateBefore(1)
						.toString(), waperiodstartdate, waperiodenddate);

				// 日期交集的天数
				int days = 0;
				if (null != periodtime) {
					days = ComdateUtils.daysOfTwo(periodtime.split(":")[0], periodtime.split(":")[1]);
				}

				if (days == 0) {
					continue;
				}

				if (basedOnMonth) {
					sumMonths++;
					sumSalary += Integer.parseInt(waperiod.get("f_itemkey").toString());
					//
				} else {
					sumDays += days;
					// 获取薪资期间的天数
					int periodays = ComdateUtils.daysOfTwo(waperiodstartdate, waperiodenddate);
					sumSalary += (Integer.parseInt(waperiod.get("f_itemkey").toString()) * Math.min(periodays, days) / days);
				}
			}
		}

		if (sumMonths == 0 && sumDays == 0) {
			return UFDouble.ZERO_DBL;
		}

		if (basedOnMonth) {
			return new UFDouble(sumSalary).div(sumMonths);
		} else {
			return (new UFDouble(sumSalary + "").div(new UFDouble(sumDays + "")));
		}
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
					sumSalary = sumSalary.add(SalaryDecryptUtil.decrypt(psndocwa.getNmoney()));
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

	public static void ListSort(List<Glbdef2VO> list) {
		Collections.sort(list, new Comparator<Glbdef2VO>() {
			@Override
			public int compare(Glbdef2VO o1, Glbdef2VO o2) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date dt1 = format.parse(o1.getBegindate().toString());
					Date dt2 = format.parse(o2.getBegindate().toString());
					if (dt1.getTime() > dt2.getTime()) {
						return 1;
					} else if (dt1.getTime() < dt2.getTime()) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
	}

	@Override
	public void delGroupInsByType(UFDate enddate, String pk_defdoc) throws BusinessException {

		String sqlStr = " update " + PsndocDefTableUtil.getGroupInsuranceTablename() + " set glbdef7 = 'Y',enddate = '"
				+ enddate.toStdString() + "', modifiedtime='" + new UFDateTime().toString() + "', modifier='"
				+ InvocationInfoProxy.getInstance().getUserId() + "'  where glbdef5 = '" + pk_defdoc
				+ "' and isnull(enddate,'9999-12-31') = '9999-12-31'  ";
		getBaseDao().executeUpdate(sqlStr);

	}

	@Override
	public void generateGroupInsWithNewIns(String pk_org, String[] pk_psndocs, UFLiteralDate specificStartDate,
			String pk_group_ins, UFDate addDate) throws BusinessException {
		// 日期为加保日期
		UFLiteralDate earlist = new UFLiteralDate(addDate.toDate());
		// 新增险种逻辑 新增险种独立在定调资团保之外,需要对员工进行额外的加保动作.
		// 参数检查
		if (StringUtils.isEmpty(pk_org)) {
			throw new BusinessException("組織不能為空");
		}

		if (pk_psndocs == null || pk_psndocs.length == 0) {
			throw new BusinessException("未選取生成團保設定的員工");
		}

		// 过滤人员别为3 DL-OP 并且用工型式为 F 外籍人员
		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(pk_psndocs);
		String sqlStr = "select psn.* from bd_psndoc psn "
				+ "left join hi_psnjob job on job.pk_psndoc = psn.pk_psndoc and ismainjob='Y' and  lastflag = 'Y' and trnsevent<>4 "
				+ "where psn.pk_psndoc in (" + psndocsInSQL + ") and "
				+ "(job.jobglbdef8 <> (SELECT pk_defdoc FROM BD_DEFDOCLIST defdoclist , "
				+ "BD_DEFDOC defdoc WHERE defdoclist.pk_defdoclist= defdoc.pk_defdoclist "
				+ "and defdoclist.code='HR006_0xx' and defdoc.code='C'))";
		@SuppressWarnings("unchecked")
		List<PsndocVO> psnlist = (List<PsndocVO>) getBaseDao().executeQuery(sqlStr,
				new BeanListProcessor(PsndocVO.class));
		List<String> pk_psndoclist = new ArrayList<String>();
		if (psnlist.size() > 0) {
			for (PsndocVO psnvo : psnlist) {
				pk_psndoclist.add(psnvo.getPk_psndoc());
			}
		}
		// 根據加保日期和險种人員有沒有重複加保
		// (加保日期相同时才会认定是相同的加保信息,如果是不同的加保日期,则认定为是客户的误操作 --ryan)
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
		for (String pk_psndoc : pk_psndoclist.toArray(new String[0])) {
			PsndocDefVO[] vos = service.queryByCondition(
					PsndocDefUtil.getGroupInsuranceVO().getClass(),
					" pk_psndoc in ('" + pk_psndoc
							+ "') and begindate <= '9999-12-31' and isnull(enddate, '9999-12-31') >='"
							+ addDate.toStdString() + "'  and glbdef5 ='" + pk_group_ins + "' ");
			if (vos != null && vos.length > 0) {
				throw new BusinessException("員工:"
						+ (String) getBaseDao().executeQuery(
								"select code from bd_psndoc where pk_psndoc = '" + pk_psndoc + "'",
								new ColumnProcessor()) + "已存在新險种的團保投保設定");
			}
		}

		// 查询新增险种的信息
		// 取團保費率表
		@SuppressWarnings("unchecked")
		List<GroupInsuranceSettingVO> setList = (List<GroupInsuranceSettingVO>) getBaseDao().retrieveByClause(
				GroupInsuranceSettingVO.class,
				" id in (select id from twhr_groupinsurancesetting where cgrpinsid = '" + pk_group_ins + "') ");
		GroupInsuranceSettingVO[] settingAll = setList.toArray(new GroupInsuranceSettingVO[0]);
		if (null == settingAll) {
			throw new BusinessException("未找到改險种的團保費率表設置!");
		}
		// MOD 補充List<PsndocDefVO> savedVOs by Andy on 2019-01-24
		List<PsndocDefVO> savedVOs = new ArrayList<PsndocDefVO>();
		for (GroupInsuranceSettingVO set : settingAll) {
			// 判断新险种的適用人员
			if (set == null || set.getCgrpinsrelid() == null) {
				continue;
			}
			String sql = "select name from bd_defdoc where pk_defdoc = '" + set.getCgrpinsrelid() + "'";
			String name = (String) getBaseDao().executeQuery(sql, new ColumnProcessor());

			if (name != null && name.equals("本人")) {
				// 勾选入职自动加保
				// 團保職等對照，依選取人員職等做加保
				// 取團保職等對照
				IGroupgradeMaintain gradeSrv = NCLocator.getInstance().lookup(IGroupgradeMaintain.class);
				GroupInsuranceGradeVO[] grades = gradeSrv.queryOnDuty(pk_org);
				// 取人员入职日期
				IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
				// Psndoc
				PsndocVO[] psndocVOs = psnQry.queryPsndocByPks(pk_psndoclist.toArray(new String[0]));

				for (String pk_psndoc : pk_psndoclist.toArray(new String[0])) {
					String pk_jobrank = getJobRankByPsnDate(pk_psndoc, earlist);

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
					GroupInsuranceSettingVO[] setting = { set };

					String groupInsID = set.getId();
					Collection<?> cols = this.getBaseDao().retrieveByClause(GroupInsuranceGradeVO.class,
							GroupInsuranceGradeVO.PK_GROUPINSURANCE + "='" + groupInsID + "'");

					// 入職自動加保--參照職等
					if (set.getBautoreg() != null && set.getBautoreg().booleanValue() && cols != null
							&& cols.size() > 0) {
						// 新建團保資料
						// MOD 補充補充將getNewGroupInsVO方法改為 List<PsndocDefVO>型態
						// 因此可加到 savedVOs的List列表中by Andy on 2019-01-24
						savedVOs.addAll(getNewGroupInsVO(pk_psndoc, pk_org, grpSum, setting, grades, psnVO, pk_jobrank));

					} else {
						// 新建團保資料,不分職等
						// MOD 補充補充將getNewGroupInsVOAll方法改為 List<PsndocDefVO>型態
						// 因此可加到 savedVOs的List列表中by Andy on 2019-01-24
						savedVOs.addAll(getNewGroupInsVOAll(pk_psndoc, grpSum, setting, psnVO));
					}

				}
			} else {
				// 眷属
				for (String pk_psndoc : pk_psndoclist.toArray(new String[0])) {
					// 查詢改人員的所有眷屬,並且符合和該人員的關係
					@SuppressWarnings("unchecked")
					List<FamilyVO> familys = (List<FamilyVO>) getBaseDao().retrieveByClause(FamilyVO.class,
							" pk_psndoc = '" + pk_psndoc + "' and mem_relation = '" + set.getCgrpinsrelid() + "'");
					if (familys == null) {
						continue;
					}

					UFDouble grpSum = getGroupInsWadocBaseSalaryByPsnDate(pk_org, pk_psndoc, earlist);
					// 將其眷屬進行加保，如有一名以上子女，需都進行加保。
					// 新建團保資料,不分職等
					// MOD 補充將getNewGroupInsVO4Family方法改為 List<PsndocDefVO>型態
					// 因此可加到 savedVOs的List列表中by Andy on 2019-01-24
					savedVOs.addAll(getNewGroupInsVO4Family(pk_psndoc, grpSum, set, familys));
				}
			}
		}
		// MOD 補充 savedVOs型態改為List<PsndocDefVO> 因此可將生成新險種資料獨立於新建團保資料for迴圈 之外by
		// Andy on 2019-01-24
		if (savedVOs != null && savedVOs.size() > 0) {
			for (PsndocDefVO vo : savedVOs) {
				vo.setBegindate(earlist);
				vo.setEnddate(null);
				service.insert(vo);
			}
		}
	}

	/**
	 * 為眷屬進行團保加保
	 * 
	 * @param pk_psndoc
	 * @param pk_org
	 * @param psnSum
	 * @param setting
	 * @param grades
	 * @param psndocVO
	 * @param pk_jobrank
	 * @return
	 * @throws BusinessException
	 */
	private List<PsndocDefVO> getNewGroupInsVO4Family(String pk_psndoc, UFDouble psnSum, GroupInsuranceSettingVO setvo,
			List<FamilyVO> familys) throws BusinessException {
		List<PsndocDefVO> psnNhiVOs = new ArrayList<PsndocDefVO>();
		int i = 0;
		for (FamilyVO familyVO : familys) {
			PsndocDefVO newGrpInsVO = PsndocDefUtil.getGroupInsuranceVO();
			newGrpInsVO.setPk_psndoc(pk_psndoc);

			newGrpInsVO.setRecordnum(i++);
			newGrpInsVO.setLastflag(i == familys.size() ? UFBoolean.TRUE : UFBoolean.FALSE);
			newGrpInsVO.setAttributeValue("glbdef1", familyVO.getMem_name());
			Object idNum = familyVO.getAttributeValue("glbdef2");
			Object idNum2 = familyVO.getAttributeValue("idnumber");
			newGrpInsVO.setAttributeValue("glbdef2", idNum == null ? String.valueOf(idNum) : String.valueOf(idNum2));
			newGrpInsVO.setAttributeValue("glbdef3", familyVO.getMem_birthday());
			newGrpInsVO.setAttributeValue("glbdef4", familyVO.getMem_relation());
			newGrpInsVO.setAttributeValue("glbdef5", setvo.getCgrpinsid());
			// Ares.Tank start 2018-8-8 10:35:11 团保保薪基数加密
			newGrpInsVO.setAttributeValue("glbdef6", SalaryEncryptionUtil.encryption(psnSum.getDouble()));
			// Ares.Tank end 2018-8-8 10:35:37 团保保薪基数
			newGrpInsVO.setAttributeValue("glbdef7", "N");
			newGrpInsVO.setAttributeValue("insurancecompany", setvo.getInsurancecompany());
			psnNhiVOs.add(newGrpInsVO);
		}
		return psnNhiVOs;
	}

	/**
	 * 取最新一笔的定调资项目的开始日期
	 */
	@SuppressWarnings("unchecked")
	public UFLiteralDate getLastGroupInsWadocBase(String pk_org, String pk_psndoc) throws BusinessException {
		// 檢查勞健保參數是否有團保保薪基數加總項目
		List<BaseDocVO> grpWaitems = (List<BaseDocVO>) this.getBaseDao().retrieveByClause(BaseDocVO.class,
				"code like 'GRPSUM%' and pk_org='" + pk_org + "'", "code");
		if (grpWaitems == null || grpWaitems.size() == 0) {
			throw new BusinessException("請在參數設定中增加編碼為GRPSUMnn的薪資參數");
		}

		String waitems = "";
		for (BaseDocVO bdVO : grpWaitems) {
			// 在底層設定中,參數設定中有該薪資參數且參數值為空時,grpWaitems的Refvalue項將返回薪資項目表wa_item中該薪資項目的code值,用以提醒是該參數值為空
			// 若正常的話,參數設定中有該薪資參數且參數值不為空,Refvalue將返回薪資項目表wa_item中該薪資項目的pk_wa_item值
			// MOD 補充查詢wa_item中該薪資項目的code值 by Andy on 2019-01-30
			String codeSQL = "select  code from wa_item where  code ='" + bdVO.getRefvalue() + "'";
			String waitemcode = String.valueOf(getBaseDao().executeQuery(codeSQL, new ColumnProcessor()));
			// MOD 補充參數設定中有該薪資參數且參數值為空時的校驗 by Andy on 2019-01-30
			if (StringUtils.isEmpty(bdVO.getRefvalue()) || bdVO.getRefvalue().equals(waitemcode)) {
				throw new BusinessException("參數設定中編碼為 [" + bdVO.getCode() + "] 的參數值不能為空。");
			}
			if (StringUtils.isEmpty(waitems)) {
				waitems += "'" + bdVO.getRefvalue() + "'";
			} else {
				waitems += ",'" + bdVO.getRefvalue() + "'";
			}
		}

		String strSQL = "select top 1 begindate from hi_psndoc_wadoc where pk_wa_item in (" + waitems
				+ ")  and pk_org= '" + pk_org + "' and pk_psndoc = '" + pk_psndoc
				+ "' and waflag='Y' and lastflag='Y' order by begindate desc";
		Object value = this.getBaseDao().executeQuery(strSQL, new ColumnProcessor());
		UFLiteralDate beginDate = (value == null ? null : new UFLiteralDate(String.valueOf(value)));
		return beginDate;
	}

	/**
	 * 留停复职时的加保 (相对于入职加保)
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param specificStartDate
	 * @param UFLiteralDate
	 *            留停复职开始日期
	 * @throws BusinessException
	 */
	@Override
	public void generateGroupIns4Return(String pk_org, String[] pk_psndocs, UFLiteralDate specificStartDate)
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

		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(pk_psndocs);
		// #18826: 过滤人员别为3 DL-OP 并且用工型式为 F 外籍人员
		// #34337 不排除菲律賓，只需要排除派遣
		// MOD by ssx on 202-04-13
		String sqlStr = "select psn.* from bd_psndoc psn "
				+ "left join hi_psnjob job on job.pk_psndoc = psn.pk_psndoc and ismainjob='Y' and lastflag = 'Y' and trnsevent<>4 "
				+ "where psn.pk_psndoc in ("
				+ psndocsInSQL
				+ ") and ("
				// +
				// " not (job.PK_PSNCL = (select pk_psncl from bd_psncl where code='3') and"
				// +
				// " psn.country = (select pk_country from bd_countryzone where code='PH')) and "
				+ "job.jobglbdef8 <> (SELECT pk_defdoc FROM BD_DEFDOCLIST defdoclist , "
				+ "BD_DEFDOC defdoc WHERE defdoclist.pk_defdoclist= defdoc.pk_defdoclist "
				+ "and defdoclist.code='HR006_0xx' and defdoc.code='C'))";
		// end
		@SuppressWarnings("unchecked")
		List<PsndocVO> psnlist = (List<PsndocVO>) getBaseDao().executeQuery(sqlStr,
				new BeanListProcessor(PsndocVO.class));
		List<String> pk_psndoclist = new ArrayList<String>();
		if (psnlist.size() > 0) {
			for (PsndocVO psnvo : psnlist) {
				pk_psndoclist.add(psnvo.getPk_psndoc());
			}
		}
		// 取團保費率表
		IGroupinsuranceMaintain settingSrv = NCLocator.getInstance().lookup(IGroupinsuranceMaintain.class);
		GroupInsuranceSettingVO[] setting = settingSrv.queryOnDuty(pk_org);

		// 取團保職等對照
		IGroupgradeMaintain gradeSrv = NCLocator.getInstance().lookup(IGroupgradeMaintain.class);
		GroupInsuranceGradeVO[] grades = gradeSrv.queryOnDuty(pk_org);
		// 取人员信息
		IPsndocQueryService psnQry = NCLocator.getInstance().lookup(IPsndocQueryService.class);
		// Psndoc
		PsndocVO[] psndocVOs = psnQry.queryPsndocByPks(pk_psndoclist.toArray(new String[0]));

		for (String pk_psndoc : pk_psndoclist.toArray(new String[0])) {
			// 先進行退保
			IPsndocSubInfoService4JFS psnService = NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
			psnService.dismissPsnGroupIns(pk_org, pk_psndoc, specificStartDate.getDateBefore(1));
			// 根據人員查找進入日期
			UFLiteralDate earlist = getOrgEnterDateByPsndoc(specificStartDate, pk_psndoc);
			String pk_jobrank = getJobRankByPsnDate(pk_psndoc, earlist);

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
			List<PsndocDefVO> savedVOs = getNewGroupInsVO(pk_psndoc, pk_org, grpSum, setting, grades, psnVO, pk_jobrank);

			if (savedVOs != null && savedVOs.size() > 0) {
				SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
				for (PsndocDefVO vo : savedVOs) {
					vo.setBegindate(earlist);
					vo.setEnddate(null);
					service.insert(vo);
				}
			}
		}
	}

	/**
	 * 离职审核后回写劳健保及團保子集结束日期
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void finishInsurance(AggStapply[] aggvos) throws BusinessException {
		String pk_psndoc = null;
		UFLiteralDate effectdate = null;
		for (AggStapply applyvo : aggvos) {
			// ssx modified on 2020-06-12
			// 不能按批执行，因为可能同一批次可能有不同的离职生效日期
			// Redmine #35706
			if (null != applyvo.getParentVO().getAttributeValue("approve_state")) {
				if ((applyvo.getParentVO().getAttributeValue("approve_state").toString().equals("102") || applyvo
						.getParentVO().getAttributeValue("approve_state").toString().equals("1"))) {
					pk_psndoc = String.valueOf(applyvo.getParentVO().getAttributeValue("pk_psndoc"));
					effectdate = applyvo.getParentVO().getAttributeValue("effectdate") == null ? null
							: new UFLiteralDate(applyvo.getParentVO().getAttributeValue("effectdate").toString());
				}
			}

			if (!StringUtils.isEmpty(pk_psndoc) && null != effectdate) {
				// 查询出人员的劳健保子集信息
				// 劳保劳退
				List<Glbdef1VO> glbdef1vos = (List<Glbdef1VO>) this.getBaseDao().retrieveByClause(Glbdef1VO.class,
						" pk_psndoc ='" + pk_psndoc + "' and isnull(enddate, '9999-12-31')='9999-12-31'");
				for (Glbdef1VO vo : glbdef1vos) {
					vo.setAttributeValue("enddate", effectdate.getDateBefore(1));

					if (vo.getAttributeValue("glbdef14") != null) {
						vo.setAttributeValue("glbdef15", effectdate.getDateBefore(1));
					}
				}
				this.getBaseDao().updateVOList(glbdef1vos);

				// 健保
				List<Glbdef2VO> glbdef2vos = (List<Glbdef2VO>) this.getBaseDao().retrieveByClause(Glbdef2VO.class,
						" pk_psndoc ='" + pk_psndoc + "' and isnull(enddate, '9999-12-31')='9999-12-31'");
				for (Glbdef2VO vo : glbdef2vos) {
					vo.setAttributeValue("enddate", effectdate.getDateBefore(1));
				}
				this.getBaseDao().updateVOList(glbdef2vos);

				// 團保
				List<Glbdef6VO> glbdef6vos = (List<Glbdef6VO>) this.getBaseDao().retrieveByClause(Glbdef6VO.class,
						" pk_psndoc ='" + pk_psndoc + "' and isnull(enddate, '9999-12-31')='9999-12-31'");
				for (Glbdef6VO vo : glbdef6vos) {
					vo.setAttributeValue("enddate", effectdate.getDateBefore(1));
				}
				this.getBaseDao().updateVOList(glbdef6vos);
			}
		}
	}

	/**
	 * 计算投保级距 逻辑： 公式內新增取數來源為員工資訊維護的勞保勞退資訊中的「勞保級距」，取級距的來源是 同個期間開始日期最新的投保級距。
	 * 
	 * @param pk_psndoc
	 * @param cstartdate
	 * @param cenddate
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, Double> getGrand(List<String> pk_psndocs, String cstartdate, String cenddate)
			throws BusinessException {

		String sql = "select pk_psndoc, SALARY_DECRYPT(h.glbdef4) glbdef4  from hi_psndoc_glbdef1 h where  "
				+ "  ((h.begindate<='" + cstartdate + "'" + " and (case when h.enddate is null then '9999-12-31' "
				+ "else  h.enddate end)>='" + cstartdate + "'  )  " + " or (h.begindate>='" + cstartdate
				+ "' and h.begindate<='" + cenddate + "' ))  " + " and h.pk_psndoc in ("
				+ new InSQLCreator().getInSQL(pk_psndocs.toArray(new String[0])) + ")" + " order by h.begindate asc";
		List<Map> idmaps = (List<Map>) this.getBaseDao().executeQuery(sql, new MapListProcessor());
		Map<String, Double> rtn = new HashMap<String, Double>();
		if (idmaps != null && idmaps.size() > 0) {
			for (Map idmap : idmaps) {
				String pkpsndoc = (String) idmap.get("pk_psndoc");
				Double glbdef = null == idmap.get("glbdef4") ? 0 : Double.parseDouble(idmap.get("glbdef4").toString());
				rtn.put(pkpsndoc, glbdef);
			}
		}

		return rtn;
	}

	@Override
	public Map<String, Map<String, UFDouble>> recalculateGroupIns(String pk_org, String pk_wa_class, String cYear,
			String cPeriod) throws BusinessException {
		calculateGroupIns(pk_org, pk_wa_class, cYear, cPeriod, null, true);
		return getInsComStates();
	}

	@SuppressWarnings("unchecked")
	@Override
	public DataSet dsRecalculateGroupIns(SmartContext context) throws BusinessException {

		String pk_org = (String) context.getParameterValue("pk_org");
		String pk_wa_class = (String) context.getParameterValue("pk_wa_class");
		String cYear = (String) context.getParameterValue("cYear");
		String cPeriod = (String) context.getParameterValue("cPeriod");
		if (cYear == null || cYear.length() > 4) {
			String yearPeriod = (String) context.getParameterValue("sdate");
			if (!yearPeriod.isEmpty()) {
				cYear = yearPeriod.substring(0, 4);
				cPeriod = yearPeriod.substring(5, 7);
			}
		}

		DataSet result = new DataSet();

		Map<String, Map<String, UFDouble>> getInsComStates = new HashMap<String, Map<String, UFDouble>>();

		MetaData metaData = builderMetaData();
		result.setMetaData(metaData);

		if (pk_org == null || pk_wa_class == null || cYear == null || cPeriod == null) {
			return result;
		}

		calculateGroupIns(pk_org, pk_wa_class, cYear, cPeriod, null, true);
		getInsComStates = getInsComStates();
		if (getInsComStates == null || getInsComStates.size() == 0) {
			return result;
		}

		// ssx added on 2020-10-21
		// 取id-pk_psndoc對照Map
		String sql = "";

		sql = "select distinct ins.glbdef2, ins.pk_psndoc from hi_psndoc_glbdef6 ins,"
				+ " (select cstartdate, cenddate from wa_waclass wc inner join wa_period pd on wc.pk_periodscheme=pd.pk_periodscheme where wc.pk_wa_class = '"
				+ pk_wa_class + "' and pd.cyear='" + cYear + "' and pd.cperiod='" + cPeriod + "') psd " + " where "
				+ " ins.begindate <= psd.cenddate and NVL(ins.enddate,'9999-12-31') >= psd.cstartdate";
		List<Map<String, Object>> id_psndoc_MapList = (List<Map<String, Object>>) this.getBaseDao().executeQuery(sql,
				new MapListProcessor());
		Map<String, String> id_psndoc_Map = new HashMap<String, String>();
		if (id_psndoc_MapList != null && id_psndoc_MapList.size() > 0) {
			for (Map<String, Object> id_psndoc_map : id_psndoc_MapList) {
				String id = id_psndoc_map.values().toArray(new String[0])[1];
				String pk_psndoc = id_psndoc_map.values().toArray(new String[0])[0];
				id_psndoc_Map.put(id, pk_psndoc);
			}
		}
		// end

		// ssx added on 2020-10-21
		// 按pk_psndoc+保險公司彙總
		// Map<pk_psndoc, Map<保險公司PK, 保費合計>>
		Map<String, Map<String, UFDouble>> resultMap = new HashMap<String, Map<String, UFDouble>>();
		for (Entry<String, Map<String, UFDouble>> row : getInsComStates.entrySet()) {
			String id = row.getKey();
			String pk_psndoc = id_psndoc_Map.get(id);
			if (id == null || pk_psndoc == null) {
				continue;
			}
			for (Entry<String, UFDouble> rows : getInsComStates.get(row.getKey()).entrySet()) {
				String com_pk = rows.getKey();
				UFDouble amount = rows.getValue();

				if (!resultMap.containsKey(pk_psndoc)) {
					// 不存在該員工時，新增員工
					resultMap.put(pk_psndoc, new HashMap<String, UFDouble>());
				}

				if (!resultMap.get(pk_psndoc).containsKey(com_pk)) {
					// 不存在該員工的保險公司時，新增保險公司
					resultMap.get(pk_psndoc).put(com_pk, amount);
				} else {
					// 存在該員工、該保險公司時，累加金額
					resultMap.get(pk_psndoc).put(com_pk, amount.add(resultMap.get(pk_psndoc).get(com_pk)));
				}
			}
		}
		// end

		int com_size = 0;
		for (Entry<String, Map<String, UFDouble>> row : resultMap.entrySet()) {
			for (Entry<String, UFDouble> rows : resultMap.get(row.getKey()).entrySet()) {
				com_size++;
			}
		}

		Object[][] objs = new Object[com_size][3];
		int i = 0;

		// Map<身份證字號, Map<保險公司PK, 保費合計>>
		for (Entry<String, Map<String, UFDouble>> row : resultMap.entrySet()) {
			for (Entry<String, UFDouble> rows : resultMap.get(row.getKey()).entrySet()) {
				objs[i][0] = row.getKey();
				objs[i][1] = rows.getKey();
				objs[i][2] = rows.getValue();
				i++;
			}
		}
		result.setDatas(objs);
		return result;
	}

	// 設置元數據
	private MetaData builderMetaData() {
		MetaData metaData = new MetaData();

		Field fld = new Field();
		fld.setCaption("人員編號PK");
		fld.setFldname("pk_psndoc");
		fld.setDbColumnType(12);
		fld.setPrecision(100);
		metaData.addField(new Field[] { fld });

		fld = new Field();
		fld.setCaption("保險公司PK");
		fld.setFldname("pk_company");
		fld.setPrecision(100);
		fld.setDbColumnType(12);
		metaData.addField(new Field[] { fld });

		fld = new Field();
		fld.setCaption("保費合計");
		fld.setFldname("money_sum");
		fld.setPrecision(10);
		fld.setDbColumnType(4);
		fld.setScale(0);
		metaData.addField(new Field[] { fld });

		return metaData;
	}

}
