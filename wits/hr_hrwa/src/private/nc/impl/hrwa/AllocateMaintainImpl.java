package nc.impl.hrwa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.pub.ace.AceAllocatePubServiceImpl;
import nc.impl.pub.util.db.InSqlManager;
import nc.impl.wa.paydata.PaydataDAO;
import nc.itf.bd.shift.IShiftQueryService;
import nc.itf.hr.wa.IGlobalCountryQueryService;
import nc.itf.hrwa.IAllocateMaintain;
import nc.itf.ta.IHRHolidayQueryService;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.am.common.util.MapUtils;
import nc.vo.bd.countryzone.CountryZoneVO;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.holiday.HolidayVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.bm.bmclass.BmClassVO;
import nc.vo.bm.data.BmDataVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.org.OrgQueryUtil;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.resa.costcenter.CostCenterVO;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.wa.allocate.AggAllocateOutVO;
import nc.vo.wa.allocate.AllocateCsvVO;
import nc.vo.wa.allocate.AllocateOutHVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.projsalary.ProjSalaryHVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.util.WaConstant;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class AllocateMaintainImpl extends AceAllocatePubServiceImpl implements IAllocateMaintain {

	@Override
	public void delete(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills) throws BusinessException {
		super.pubdeleteBills(clientFullVOs, originBills);
	}

	@Override
	public AggAllocateOutVO[] insert(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException {
		return super.pubinsertBills(clientFullVOs, originBills);
	}

	@Override
	public AggAllocateOutVO[] update(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException {
		return super.pubupdateBills(clientFullVOs, originBills);
	}

	@Override
	public AggAllocateOutVO[] query(IQueryScheme queryScheme) throws BusinessException {
		return super.pubquerybills(queryScheme);
	}

	@Override
	public AggAllocateOutVO[] save(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException {
		return super.pubsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggAllocateOutVO[] unsave(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException {
		return super.pubunsendapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggAllocateOutVO[] approve(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException {
		return super.pubapprovebills(clientFullVOs, originBills);
	}

	@Override
	public AggAllocateOutVO[] unapprove(AggAllocateOutVO[] clientFullVOs, AggAllocateOutVO[] originBills)
			throws BusinessException {
		return super.pubunapprovebills(clientFullVOs, originBills);
	}

	private AppendBaseDAO appDao;

	public AppendBaseDAO getAppDao() {
		if (null == appDao) {
			appDao = new AppendBaseDAO();
		}
		return appDao;
	}

	@Override
	public void deleteByContext(WaLoginVO waLoginVO) throws BusinessException {
		if (null == waLoginVO) {
			return;
		}
		StringBuilder delSql = new StringBuilder();
		delSql.append(" delete from wa_allocateout where pk_org=? and pk_wa_calss=? and cperiod=? ");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_org());
		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear() + waLoginVO.getPeriodVO().getCperiod());
		getAppDao().getBaseDao().executeUpdate(delSql.toString(), parameter);
		
		//一次分摊删除
		getAppDao().getBaseDao()
			.executeUpdate("delete from wa_allocateout_once where pk_org=? and pk_wa_calss=? and cperiod=?", parameter);
		
		
	}

	@SuppressWarnings("unchecked")
	public AggAllocateOutVO[] queryBillsByCondition(WaLoginContext context, String condition, String orderSQL)
			throws BusinessException {
		AggAllocateOutVO[] bills = null;
		StringBuilder whereSQL = new StringBuilder();
		if (null != context) {
			if (StringUtils.isNotEmpty(context.getPk_org())) {
				whereSQL.append(" and pk_org='").append(context.getPk_org()).append("' ");
			}
			if (StringUtils.isNotEmpty(context.getPk_wa_class())) {
				whereSQL.append(" and pk_wa_calss='").append(context.getPk_wa_class()).append("' ");
			}
			if (StringUtils.isNotEmpty(context.getWaLoginVO().getPeriodVO().getCyear())
					|| StringUtils.isNotEmpty(context.getWaLoginVO().getPeriodVO().getCperiod())) {
				whereSQL.append(" and cperiod='").append(context.getWaLoginVO().getPeriodVO().getCyear())
						.append(context.getWaLoginVO().getPeriodVO().getCperiod()).append("' ");
			}
		}
		if (StringUtils.isNotEmpty(condition)) {
			whereSQL.append(" and ").append(condition).append(" ");
		}
		if (whereSQL.length() <= 0) {
			whereSQL.append(" and 1=2 ");
		} else {
			whereSQL.append(" and dr <> 1 ");
		}
		if (StringUtils.isEmpty(orderSQL)) {
			orderSQL = " pk_psndoc,pk_classitem,def1 ";
		}
		List<AllocateOutHVO> hvoList = (List<AllocateOutHVO>) getAppDao().getBaseDao().retrieveByClause(
				AllocateOutHVO.class, whereSQL.substring(4).toString(), orderSQL);
		List<AggAllocateOutVO> tempList = new ArrayList<AggAllocateOutVO>();
		if (null != hvoList && !hvoList.isEmpty()) {
			for (AllocateOutHVO hvo : hvoList) {
				AggAllocateOutVO aggVO = new AggAllocateOutVO();
				aggVO.setParentVO(hvo);
				tempList.add(aggVO);
			}
			bills = tempList.toArray(new AggAllocateOutVO[0]);
		}
		return bills;
	}

	@Override
	public AggAllocateOutVO[] genPjShareDetail(WaLoginContext context) throws BusinessException {
		AggAllocateOutVO[] returnBills = null;
		if (null == context) {
			return returnBills;
		}
		deleteByContext(context.getWaLoginVO());
		// WaLoginVO waLoginVO =
		// WaClassStateHelper.getWaclassVOWithState(context.getWaLoginVO());
		// context.setWaLoginVO(waLoginVO);

		// IPaydataQueryService paydataService =
		// NCLocator.getInstance().lookup(IPaydataQueryService.class);
		// 薪资项目
		WaClassItemVO[] classItemVOs = qryClassItems(context);

		if (ArrayUtils.isEmpty(classItemVOs)) {
			// 此方案期间没有薪资发放项目！
			ExceptionUtils.wrappBusinessException(ResHelper.getString("allocate", "0allcate-ui-000016"));
		}

		// 薪资档案数据.
		// DataVO[] payDatas = paydataService.queryDataVOsByCond(context, null,
		// null);
		StringBuilder selectStr = new StringBuilder();
		selectStr.append("wa_data.*");
		// if (classItemVOs != null && classItemVOs.length > 0) {
		// for (WaClassItemVO waitem : classItemVOs) {
		// selectStr.append(",wa_data." + waitem.getItemkey());
		// }
		// }

		DataVO[] payDatas = new PaydataDAO().queryByCondition(context, null, null, selectStr.toString());
		if (ArrayUtils.isEmpty(payDatas)) {
			// 此方案期间没有薪资发放数据！
			ExceptionUtils.wrappBusinessException(ResHelper.getString("allocate", "0allcate-ui-000017"));
		}
		// 分摊方案档案
		Map<String, DefdocVO> shareVOMap = queryBasicInfo(DefdocVO.class,
				getDefdocCondition(WaConstant.DEF_CODE_SHARE, null), new String[] { DefdocVO.PK_DEFDOC });
		// 员工专案代码分摊
		Map<String, List<ProjSalaryHVO>> psnProjSaVosMap = queryMapListInfo(ProjSalaryHVO.class,
				getProjCodeWhere(context), new String[] { "pk_psndoc" });
		// 考勤期间起止时间
		PeriodVO[] periodArr = getAppDao().retrieveByClause(PeriodVO.class, getPeriodWhere(context));
		if (ArrayUtils.isEmpty(periodArr)) {
			// 没有找到考勤期间
			ExceptionUtils.wrappBusinessException(ResHelper.getString("allocate", "0allcate-ui-000019"));
		}
		PeriodVO periodVO = periodArr[0];
		String[] psndocPks = StringPiecer.getStrArray(payDatas, DataVO.PK_PSNDOC);

		// 加班时数统计
		Map<String, List<OvertimeRegVO>> psnPjOtHoursMap = queryMapListInfo(OvertimeRegVO.class,
				getProjOverTimeWhere(context, periodVO), new String[] { OvertimeRegVO.PK_PSNDOC });
		// 员工工作日历工时数
		Map<String, UFDouble> psnMonthWkHourMap = qryWkHoursFromPsnCalendar(context, periodVO, psndocPks);
		// 考勤期间内的正常工时数(组织日历去除法定节假日与公休)，没有排班情况下引用
		Map<String, UFDouble> psnMonthWkHours = qryMonthWkHoursByOrgCalander(context, periodVO, psndocPks);
		// 员工休假期间休假信息
		Map<String, UFDouble> psnLeaveHoursMap = null; // qryPsnLeaveHours(context,periodVO);
		//
		Map<String,UFDouble> psnLeaveHohors4TwiceMap = qryPsnLeaveHours4Twice(context,periodVO);
		// 外部人员专案工时信息
		Map<String, Map<String, UFDouble>> psnPjWkHoursMap = qryPsnWkHoursOutersys(context, periodVO, psndocPks);
		// 人员工作记录->是否二次分摊
		Map<String,Boolean> job2isTwiceDevideMap = getisTwiceDevide(payDatas);
		// 基础信息分摊vo
		AllocateOutHVO baseHVO = createBaseHVO(context);
		boolean isTWArea = isTWOrg(context.getPk_org());

		List<AggAllocateOutVO> billList = new ArrayList<AggAllocateOutVO>();
		// 二次分摊数据 tank 2019年9月22日21:34:59 除全月工时分摊方案且人员类别中勾选'成本是否二次分摊'数据不同,其他都一样
		List<AggAllocateOutVO> billListtwice = new ArrayList<AggAllocateOutVO>();
		for (DataVO vo : payDatas) {
			String pk_psndoc = vo.getPk_psndoc();
			String pk_costcenter = vo.getPk_liabilityorg();
			String pk_psnjob = vo.getPk_psnjob();

			for (WaClassItemVO itemVO : classItemVOs) {
				String pk_classitem = itemVO.getPk_wa_classitem();
				String itemkey = itemVO.getItemkey();
				Object obj = vo.getAttributeValue(itemkey);
				UFDouble itemMny = UFDouble.ZERO_DBL;
				try {
					itemMny = (obj == null ? UFDouble.ZERO_DBL : new UFDouble(String.valueOf(obj)));
				} catch (java.lang.Exception e) {
					Logger.error("Error!!! classitem:" + itemkey + " value:" + String.valueOf(obj));
					continue;
				}
				DefdocVO shareVO = shareVOMap.get(itemVO.getDef1());
				if (null == shareVO || shareVO.getCode().toUpperCase().equals(WaConstant.SHARE_NOT_SYS__SHARED)
						|| itemMny.compareTo(UFDouble.ZERO_DBL) == 0) {
					continue;
				}

				if (shareVO.getCode().toUpperCase().equals(WaConstant.SHARE_MONTH_WORKHOURS)
						|| shareVO.getCode().toUpperCase().equals(WaConstant.SHARE_LEAVE_SHARED)) {
					// 全月工时分摊
					AllocateOutHVO pjbaseHVO = (AllocateOutHVO) baseHVO.clone();
					pjbaseHVO.setPk_psndoc(pk_psndoc);
					pjbaseHVO.setPk_classitem(pk_classitem);
					pjbaseHVO.setPk_costcenter(pk_costcenter);
					pjbaseHVO.setPk_psnjob(pk_psnjob);
					pjbaseHVO.setDef2(itemMny.toString());
					
					AllocateOutHVO pjbaseHVO4Twice = (AllocateOutHVO)pjbaseHVO.clone();
					List<AggAllocateOutVO> aggList = getMonthHoursShare(psnMonthWkHourMap, psnMonthWkHours,
							psnLeaveHoursMap, psnPjWkHoursMap, pjbaseHVO);
					if (!aggList.isEmpty()) {
						billList.addAll(aggList);
						
						if(job2isTwiceDevideMap.get(pk_psnjob)!=null
								&& job2isTwiceDevideMap.get(pk_psnjob).booleanValue()){
							//进行二次分摊:
							List<AggAllocateOutVO> aggListTwice = new ArrayList<>();
							if(shareVO.getCode().toUpperCase().equals(WaConstant.SHARE_LEAVE_SHARED)){
								//假况扣款
								aggListTwice = getLeaveShareTwice(psnPjWkHoursMap, pjbaseHVO4Twice);
							}else{
								//其他 (本薪等加项)
								aggListTwice = getMonthHoursShareTwice(psnMonthWkHourMap, psnMonthWkHours,
										psnLeaveHohors4TwiceMap, psnPjWkHoursMap, pjbaseHVO4Twice);
							}
							//如果二次分摊失败,则维持一次分摊结果
							if(aggListTwice==null || aggListTwice.size() <= 0){
								aggListTwice = aggList;
							}
							for(AggAllocateOutVO aggvo : aggListTwice){
								billListtwice.add((AggAllocateOutVO)aggvo.clone());
							}
						}else{
							for(AggAllocateOutVO aggvo : aggList){
								billListtwice.add((AggAllocateOutVO)aggvo.clone());
							}
						}
						
					}
					
				} else if (shareVO.getCode().toUpperCase().equals(WaConstant.SHARE_PROJ_CODE)) {
					// 专案代码分摊
					AllocateOutHVO pjCodeHVO = (AllocateOutHVO) baseHVO.clone();
					pjCodeHVO.setPk_psndoc(pk_psndoc);
					pjCodeHVO.setPk_classitem(pk_classitem);
					pjCodeHVO.setPk_costcenter(pk_costcenter);
					pjCodeHVO.setPk_psnjob(pk_psnjob);
					List<AggAllocateOutVO> aggList = getProjSalaryShare(psnProjSaVosMap, pjCodeHVO);
					if (!aggList.isEmpty()) {
						billList.addAll(aggList);
						for(AggAllocateOutVO aggvo : aggList){
							billListtwice.add((AggAllocateOutVO)aggvo.clone());
						}
					}
				} else if (shareVO.getCode().toUpperCase().equals(WaConstant.SHARE_PROJ_OVERHOURS)) {
					// 专案加班时数分摊
					AllocateOutHVO pjOtHVO = (AllocateOutHVO) baseHVO.clone();
					pjOtHVO.setPk_psndoc(pk_psndoc);
					pjOtHVO.setPk_classitem(pk_classitem);
					pjOtHVO.setPk_costcenter(pk_costcenter);
					pjOtHVO.setPk_psnjob(pk_psnjob);
					pjOtHVO.setDef2(itemMny.toString());
					List<AggAllocateOutVO> aggList = getProjOverTimeShare(psnPjOtHoursMap, pjOtHVO);
					if (!aggList.isEmpty()) {
						billList.addAll(aggList);
						for(AggAllocateOutVO aggvo : aggList){
							billListtwice.add((AggAllocateOutVO)aggvo.clone());
						}
					}

				} else if (shareVO.getCode().toUpperCase().equals(WaConstant.SHARE_FULL_COSTCENTER)) {
					// 全额摊至成本中心
					AllocateOutHVO fullCostCenterHVO = (AllocateOutHVO) baseHVO.clone();
					fullCostCenterHVO.setPk_psndoc(pk_psndoc);
					fullCostCenterHVO.setPk_classitem(pk_classitem);
					fullCostCenterHVO.setPk_costcenter(pk_costcenter);
					fullCostCenterHVO.setPk_psnjob(pk_psnjob);
					fullCostCenterHVO.setDef2(getRoundNumber(itemMny, isTWArea));
					AggAllocateOutVO billVO = new AggAllocateOutVO();
					billVO.setParentVO(fullCostCenterHVO);
					billList.add(billVO);
					billListtwice.add((AggAllocateOutVO)billVO.clone());
				} else if (shareVO.getCode().toUpperCase().equals(WaConstant.SHARE_NOT_SHARED)) {
					// 不分摊
					AllocateOutHVO notShareHVO = (AllocateOutHVO) baseHVO.clone();
					notShareHVO.setPk_psndoc(pk_psndoc);
					notShareHVO.setPk_classitem(pk_classitem);
					notShareHVO.setPk_costcenter(null); // pk_costcenter
					notShareHVO.setPk_psnjob(pk_psnjob);
					notShareHVO.setDef2(getRoundNumber(itemMny, isTWArea));
					AggAllocateOutVO billVO = new AggAllocateOutVO();
					billVO.setParentVO(notShareHVO);
					billList.add(billVO);
					billListtwice.add((AggAllocateOutVO)billVO.clone());
				} else {
					// 其他
				}
			}
		}

		if (!billList.isEmpty()) {
			// for (AggAllocateOutVO aggVO : billList) {
			// AllocateOutHVO hvo = aggVO.getParentVO();
			// UFDouble amt = (StringUtils.isEmpty(hvo.getDef2()) ?
			// UFDouble.ZERO_DBL : new UFDouble(hvo.getDef2()));
			// hvo.setDef2(getRoundNumber(amt, isTWArea));
			// }
			//插入第一次分摊的结果
			AggAllocateOutVO[] firstrs = insert(billList.toArray(new AggAllocateOutVO[0]), null);
			//把结果备份到第一次分摊表
			backupOneceDevide(context.getWaLoginVO());
			//删除第一次分摊结果
			//deleteOnceByContext(context.getWaLoginVO());
			delete(firstrs,firstrs);
			//插入二次分摊结果
			returnBills = insert(billListtwice.toArray(new AggAllocateOutVO[0]), null);
		}
		return returnBills;
	}

	private Map<String, Boolean> getisTwiceDevide(DataVO[] payDatas) throws BusinessException {
		Map<String, Boolean> rs = new HashMap<>();
		if(payDatas!=null && payDatas.length > 0){
			List<String> jobList = new ArrayList<>();
			for(DataVO vo : payDatas){
				jobList.add(vo.getPk_psnjob());
			}
			InSQLCreator insql = new InSQLCreator();
			String jobstr = insql.getInSQL(jobList.toArray(new String[0]));
			String sql = " select pk_psnjob,isnull(cl.istwicedevide,'N') istwicedevide from hi_psnjob job "
					+" left join bd_psncl cl on cl.pk_psncl = job.pk_psncl where job.pk_psnjob in ("+jobstr+")";
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> rsList = 
					(List<Map<String,Object>>)getAppDao().getBaseDao().executeQuery(sql, new MapListProcessor());
			if(rsList!=null && rsList.size() > 0){
				for(Map<String,Object> rsMap : rsList){
					if(rsMap!=null && rsMap.get("pk_psnjob")!=null){
						if("Y".equalsIgnoreCase(String.valueOf(rsMap.get("istwicedevide")))){
							rs.put(String.valueOf(rsMap.get("pk_psnjob")), true);
						}else{
							rs.put(String.valueOf(rsMap.get("pk_psnjob")), false);
						}
					}
				}
			}
		}

		return rs;
	}
	/**
	 * 获取请假时数
	 * @param context
	 * @param periodVO
	 * @return
	 * @throws BusinessException 
	 */
	private Map<String, UFDouble> qryPsnLeaveHours4Twice(
			WaLoginContext context, PeriodVO periodVO) throws BusinessException {
		String GPL01DbCol = (String) getAppDao().getBaseDao().executeQuery(
				"select item_db_code from tbm_item where item_code = 'GPL01' "
				+ " and pk_group = '"+context.getPk_group()+"' and pk_group = pk_org and dr = 0;", new ColumnProcessor());
		String GPL02DbCol = (String) getAppDao().getBaseDao().executeQuery(
				"select item_db_code from tbm_item where item_code = 'GPL02' "
				+ " and pk_group = '"+context.getPk_group()+"' and pk_group = pk_org and dr = 0;", new ColumnProcessor());
		if(null == GPL01DbCol || null == GPL02DbCol){
			throw new BusinessException("未找到請假時長/非當月審批時長日薪項目");
		}
		String sql = "SELECT tbm_daystat.pk_psndoc pk_psndoc,sum("+GPL01DbCol+")+sum("+GPL02DbCol+") leavehour "
				+" FROM tbm_daystatb INNER JOIN tbm_daystat ON tbm_daystat.pk_daystat=tbm_daystatb.pk_daystat "
				+" WHERE tbm_daystat.pk_org='"+context.getPk_org()+"' AND (( "
				+" tbm_daystat.calendar BETWEEN '"+periodVO.getBegindate().toStdString()
				+"' AND '"+periodVO.getEnddate().toStdString()+"' "
				+" AND ISNULL(tbm_daystatb.calcperiod,'1')='1') "
				+" OR  tbm_daystatb.calcperiod='"+periodVO.getYear()+"'+'"+periodVO.getMonth()+"') "
				+" group by tbm_daystat.pk_psndoc ";
		
		@SuppressWarnings("unchecked")
		Map<String, UFDouble> psnLeaveHoursMap = (Map<String, UFDouble>) getAppDao().getBaseDao().executeQuery(
				sql.toString(), new ResultSetProcessor() {
					/**
					 * 
					 */
					private static final long serialVersionUID = 5237174467741136799L;
					Map<String, UFDouble> map = new HashMap<String, UFDouble>();

					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {
						while (rs.next()) {
							map.put(rs.getString(1), new UFDouble(rs.getString(2)));
						}
						return map;
					}
				});
		return psnLeaveHoursMap;
	}

	private void backupOneceDevide(WaLoginVO waLoginVO) throws DAOException {

		String sql = "insert into wa_allocateout_once "
				+ " select * from wa_allocateout where pk_org='"+waLoginVO.getPk_org()
				+"' and pk_wa_calss='"+waLoginVO.getPk_wa_class()
				+"' and cperiod='"+waLoginVO.getPeriodVO().getCyear() + waLoginVO.getPeriodVO().getCperiod()+"' ";
		getAppDao().getBaseDao().executeUpdate(sql);
	}

	private List<AggAllocateOutVO> getLeaveShareTwice(Map<String, Map<String, UFDouble>> psnPjWkHoursMap, 
			AllocateOutHVO baseHVO ) throws BusinessException {
		List<AggAllocateOutVO> list = new ArrayList<AggAllocateOutVO>();
		String pk_psndoc = baseHVO.getPk_psndoc();
		UFDouble itemMny = (StringUtils.isEmpty(baseHVO.getDef2()) ? UFDouble.ZERO_DBL
				: new UFDouble(baseHVO.getDef2()));
		baseHVO.setDef2(null);
		boolean isTWArea = isTWOrg(baseHVO.getPk_org());
		Map<String, UFDouble> projWkHoursMap = psnPjWkHoursMap.get(pk_psndoc);
		if (MapUtils.isEmpty(projWkHoursMap)) {
			// 没有项目工时,不进行二次分摊
			return null;
		} else {
			UFDouble projMny = UFDouble.ZERO_DBL;
			UFDouble fullMonthWkHours = calculateSum(projWkHoursMap.values()
					.toArray(new UFDouble[0]));

			int len = projWkHoursMap.keySet().size();
			for (String projectPK : projWkHoursMap.keySet()) {
				UFDouble wkHours = projWkHoursMap.get(projectPK);
				if (null == wkHours) {
					wkHours = UFDouble.ZERO_DBL;
				}
				
				if(wkHours.compareTo(UFDouble.ZERO_DBL) <= 0){
					//项目工时为0 ,不分摊
					continue;
				}
				UFDouble shareMny = UFDouble.ZERO_DBL;
				if(null == fullMonthWkHours || fullMonthWkHours.equals(UFDouble.ZERO_DBL)
						|| Math.abs(fullMonthWkHours.doubleValue())< 0.000001){
					shareMny = UFDouble.ZERO_DBL;
				}else {
					shareMny = wkHours.multiply(itemMny).div(
							fullMonthWkHours);
				}
				if (len == 1) {
					shareMny = itemMny.sub(projMny);
				}
				shareMny = new UFDouble(getRoundNumber(shareMny, isTWArea));
				AggAllocateOutVO aggVO = new AggAllocateOutVO();
				AllocateOutHVO hvo = (AllocateOutHVO) baseHVO.clone();
				hvo.setPk_project(projectPK);
				hvo.setDef1(projectPK);
				hvo.setDef2(shareMny.toString());
				aggVO.setParentVO(hvo);
				list.add(aggVO);
				projMny = projMny.add(shareMny);
				len--;
			}

		}
		return list;

	}

	private List<AggAllocateOutVO> getMonthHoursShareTwice(
			Map<String, UFDouble> psnMonthWkHourMap,
			Map<String, UFDouble> psnMonthWkHours, Map<String, UFDouble> psnLeaveHohors4TwiceMap,
			Map<String, Map<String, UFDouble>> psnPjWkHoursMap, AllocateOutHVO baseHVO ) throws BusinessException {
		List<AggAllocateOutVO> list = new ArrayList<AggAllocateOutVO>();
		String pk_psndoc = baseHVO.getPk_psndoc();
		UFDouble itemMny = (StringUtils.isEmpty(baseHVO.getDef2()) ? UFDouble.ZERO_DBL
				: new UFDouble(baseHVO.getDef2()));
		baseHVO.setDef2(null);
		// 员工日历排班工时
		UFDouble psnCalMonthWkHours = psnMonthWkHourMap.get(pk_psndoc);
		if (null == psnCalMonthWkHours || psnCalMonthWkHours.doubleValue() <= 0) {
			// 组织日历工时
			psnCalMonthWkHours = psnMonthWkHours.get(pk_psndoc);
		}
		if (null == psnCalMonthWkHours) {
			psnCalMonthWkHours = UFDouble.ZERO_DBL;
		}
		boolean isTWArea = isTWOrg(baseHVO.getPk_org());
		if (null != psnLeaveHohors4TwiceMap) {
			if (psnCalMonthWkHours.doubleValue() <= 0) {
				Logger.error("员工[" + pk_psndoc + "]的全月工时为零，存在不正常数据.");
			}
			Map<String, UFDouble> projWkHoursMap = psnPjWkHoursMap.get(pk_psndoc);
			if (MapUtils.isEmpty(projWkHoursMap)) {
				AggAllocateOutVO aggVO = new AggAllocateOutVO();
				AllocateOutHVO hvo = (AllocateOutHVO) baseHVO.clone();
				hvo.setDef2(getRoundNumber(itemMny, isTWArea));
				aggVO.setParentVO(hvo);
				list.add(aggVO);
				Logger.error("员工[" + pk_psndoc + "]的期间项目导入工时无数据.");
			} else {
				UFDouble projWkHours = calculateSum(projWkHoursMap.values().toArray(new UFDouble[0]));
				
				UFDouble projMny = UFDouble.ZERO_DBL;
				UFDouble fullMonthWkHours = psnCalMonthWkHours;
				
				if (projWkHours.compareTo(psnCalMonthWkHours) < 0) {
					//请假时数:
					UFDouble leavehousr = psnLeaveHohors4TwiceMap.get(pk_psndoc)==null?UFDouble.ZERO_DBL:psnLeaveHohors4TwiceMap.get(pk_psndoc);
					//如果是請假時數+項目時數大於工作日曆的時數,則分母為(請假時數+項目時數)
					if(leavehousr.add(projWkHours).compareTo(psnCalMonthWkHours) > 0){
						fullMonthWkHours = leavehousr.add(projWkHours);
					}
					//请假成本
					UFDouble leaveCost = leavehousr.multiply(itemMny).div(fullMonthWkHours);
					
					for (String projectPK : projWkHoursMap.keySet()) {
						UFDouble wkHours = projWkHoursMap.get(projectPK);
						if (null == wkHours) {
							wkHours = UFDouble.ZERO_DBL;
						}
						UFDouble shareMny = wkHours.multiply(itemMny).div(fullMonthWkHours);
						//分摊请假成本
						if(projWkHours!=null && projWkHours.compareTo(UFDouble.ZERO_DBL)!=0){
							shareMny = shareMny.add(leaveCost.multiply(wkHours).div(projWkHours));
						}
						
						shareMny = new UFDouble(getRoundNumber(shareMny, isTWArea));
						AggAllocateOutVO aggVO = new AggAllocateOutVO();
						AllocateOutHVO hvo = (AllocateOutHVO) baseHVO.clone();
						hvo.setPk_project(projectPK);
						hvo.setDef1(projectPK);
						hvo.setDef2(shareMny.toString());
						aggVO.setParentVO(hvo);
						list.add(aggVO);
						projMny = projMny.add(shareMny);
					}
					UFDouble costcenterMny = itemMny.sub(projMny);
					costcenterMny = new UFDouble(getRoundNumber(costcenterMny, isTWArea));
					// 薪資發放項目 正負數都要顯示列出  by George 20190627 缺陷Bug #28125
					if (costcenterMny.compareTo(UFDouble.ZERO_DBL) != 0) {
						AggAllocateOutVO aggVO = new AggAllocateOutVO();
						AllocateOutHVO hvo = (AllocateOutHVO) baseHVO.clone();
						hvo.setDef2(costcenterMny.toString());
						aggVO.setParentVO(hvo);
						list.add(aggVO);
					}
				} else {
					//专案时长大于工作日历时长
					if (projWkHours.compareTo(psnCalMonthWkHours) > 0) {
						fullMonthWkHours = projWkHours;
					}
					
					int len = projWkHoursMap.keySet().size();
					for (String projectPK : projWkHoursMap.keySet()) {
						UFDouble wkHours = projWkHoursMap.get(projectPK);
						if (null == wkHours) {
							wkHours = UFDouble.ZERO_DBL;
						}
						if(wkHours.compareTo(UFDouble.ZERO_DBL) <= 0){
							//项目工时为0 ,不分摊
							continue;
						}
                        UFDouble shareMny = UFDouble.ZERO_DBL;
                        if(fullMonthWkHours!=null && fullMonthWkHours.compareTo(UFDouble.ZERO_DBL)!=0){
                             shareMny = wkHours.multiply(itemMny).div(fullMonthWkHours);
                        }else{
                            shareMny = UFDouble.ZERO_DBL;
                        }

						if (len == 1) {
							shareMny = itemMny.sub(projMny);
						}
						shareMny = new UFDouble(getRoundNumber(shareMny, isTWArea));
						AggAllocateOutVO aggVO = new AggAllocateOutVO();
						AllocateOutHVO hvo = (AllocateOutHVO) baseHVO.clone();
						hvo.setPk_project(projectPK);
						hvo.setDef1(projectPK);
						hvo.setDef2(shareMny.toString());
						aggVO.setParentVO(hvo);
						list.add(aggVO);
						projMny = projMny.add(shareMny);
						len--;
					}
				}
			}
			return list;
		}
		return null;
	}

	protected List<AggAllocateOutVO> getMonthHoursShare(Map<String, UFDouble> psnMonthWkHourMap,
			Map<String, UFDouble> psnMonthWkHours, Map<String, UFDouble> psnLeaveHoursMap,
			Map<String, Map<String, UFDouble>> psnPjWkHoursMap, AllocateOutHVO baseHVO) throws BusinessException {
		List<AggAllocateOutVO> list = new ArrayList<AggAllocateOutVO>();
		String pk_psndoc = baseHVO.getPk_psndoc();
		UFDouble itemMny = (StringUtils.isEmpty(baseHVO.getDef2()) ? UFDouble.ZERO_DBL
				: new UFDouble(baseHVO.getDef2()));
		baseHVO.setDef2(null);
		// 员工日历排班工时
		UFDouble psnCalMonthWkHours = psnMonthWkHourMap.get(pk_psndoc);
		if (null == psnCalMonthWkHours || psnCalMonthWkHours.doubleValue() <= 0) {
			// 组织日历工时
			psnCalMonthWkHours = psnMonthWkHours.get(pk_psndoc);
		}
		if (null == psnCalMonthWkHours) {
			psnCalMonthWkHours = UFDouble.ZERO_DBL;
		}
		boolean isTWArea = isTWOrg(baseHVO.getPk_org());
		if (null == psnLeaveHoursMap) {
			if (psnCalMonthWkHours.doubleValue() <= 0) {
				Logger.error("员工[" + pk_psndoc + "]的全月工时为零，存在不正常数据.");
			}
			Map<String, UFDouble> projWkHoursMap = psnPjWkHoursMap.get(pk_psndoc);
			if (MapUtils.isEmpty(projWkHoursMap)) {
				AggAllocateOutVO aggVO = new AggAllocateOutVO();
				AllocateOutHVO hvo = (AllocateOutHVO) baseHVO.clone();
				hvo.setDef2(getRoundNumber(itemMny, isTWArea));
				aggVO.setParentVO(hvo);
				list.add(aggVO);
				Logger.error("员工[" + pk_psndoc + "]的期间项目导入工时无数据.");
			} else {
				UFDouble projWkHours = calculateSum(projWkHoursMap.values().toArray(new UFDouble[0]));
				UFDouble projMny = UFDouble.ZERO_DBL;
				UFDouble fullMonthWkHours = psnCalMonthWkHours;
				if (projWkHours.compareTo(psnCalMonthWkHours) < 0) {
					for (String projectPK : projWkHoursMap.keySet()) {
						UFDouble wkHours = projWkHoursMap.get(projectPK);
						if (null == wkHours) {
							wkHours = UFDouble.ZERO_DBL;
						}
						UFDouble shareMny = wkHours.multiply(itemMny).div(fullMonthWkHours);
						shareMny = new UFDouble(getRoundNumber(shareMny, isTWArea));
						AggAllocateOutVO aggVO = new AggAllocateOutVO();
						AllocateOutHVO hvo = (AllocateOutHVO) baseHVO.clone();
						hvo.setPk_project(projectPK);
						hvo.setDef1(projectPK);
						hvo.setDef2(shareMny.toString());
						aggVO.setParentVO(hvo);
						list.add(aggVO);
						projMny = projMny.add(shareMny);
					}
					UFDouble costcenterMny = itemMny.sub(projMny);
					costcenterMny = new UFDouble(getRoundNumber(costcenterMny, isTWArea));
					// 薪資發放項目 正負數都要顯示列出  by George 20190627 缺陷Bug #28125
					if (costcenterMny.compareTo(UFDouble.ZERO_DBL) != 0) {
						AggAllocateOutVO aggVO = new AggAllocateOutVO();
						AllocateOutHVO hvo = (AllocateOutHVO) baseHVO.clone();
						hvo.setDef2(costcenterMny.toString());
						aggVO.setParentVO(hvo);
						list.add(aggVO);
					}
				} else {
					if (projWkHours.compareTo(psnCalMonthWkHours) > 0) {
						fullMonthWkHours = projWkHours;
					}
					int len = projWkHoursMap.keySet().size();
					for (String projectPK : projWkHoursMap.keySet()) {
						UFDouble wkHours = projWkHoursMap.get(projectPK);
						if (null == wkHours) {
							wkHours = UFDouble.ZERO_DBL;
						}
						UFDouble shareMny = wkHours.multiply(itemMny).div(fullMonthWkHours);
						if (len == 1) {
							shareMny = itemMny.sub(projMny);
						}
						shareMny = new UFDouble(getRoundNumber(shareMny, isTWArea));
						AggAllocateOutVO aggVO = new AggAllocateOutVO();
						AllocateOutHVO hvo = (AllocateOutHVO) baseHVO.clone();
						hvo.setPk_project(projectPK);
						hvo.setDef1(projectPK);
						hvo.setDef2(shareMny.toString());
						aggVO.setParentVO(hvo);
						list.add(aggVO);
						projMny = projMny.add(shareMny);
						len--;
					}
				}
			}
			return list;
		}
		// 审核当月请假工时
		UFDouble monthLeaveHours = psnLeaveHoursMap.get(pk_psndoc);
		if (null == monthLeaveHours) {
			monthLeaveHours = UFDouble.ZERO_DBL;
		}
		// 全月工时
		UFDouble fullMonthWkHours = psnCalMonthWkHours.sub(monthLeaveHours);

		Map<String, UFDouble> projWkHoursMap = psnPjWkHoursMap.get(pk_psndoc);
		if (null != projWkHoursMap && !projWkHoursMap.isEmpty()) {
			UFDouble projWkHours = calculateSum(projWkHoursMap.values().toArray(new UFDouble[0]));
			UFDouble projMny = UFDouble.ZERO_DBL;
			if (fullMonthWkHours.compareTo(projWkHours) > 0) {
				for (String projectPK : projWkHoursMap.keySet()) {
					UFDouble wkHours = projWkHoursMap.get(projectPK);
					if (null == wkHours) {
						wkHours = UFDouble.ZERO_DBL;
					}
					UFDouble shareMny = wkHours.multiply(itemMny).div(fullMonthWkHours);
					shareMny = new UFDouble(getRoundNumber(shareMny, isTWArea));
					AggAllocateOutVO aggVO = new AggAllocateOutVO();
					AllocateOutHVO hvo = (AllocateOutHVO) baseHVO.clone();
					hvo.setPk_project(projectPK);
					hvo.setDef1(projectPK);
					hvo.setDef2(shareMny.toString());
					aggVO.setParentVO(hvo);
					list.add(aggVO);
					projMny = projMny.add(shareMny);
				}
				UFDouble costcenterMny = itemMny.sub(projMny);
				costcenterMny = new UFDouble(getRoundNumber(costcenterMny, isTWArea));
				// 薪資發放項目 正負數都要顯示列出  by George 20190627 缺陷Bug #28125
				if (costcenterMny.compareTo(UFDouble.ZERO_DBL) != 0) {
					AggAllocateOutVO aggVO = new AggAllocateOutVO();
					AllocateOutHVO hvo = (AllocateOutHVO) baseHVO.clone();
					hvo.setDef2(costcenterMny.toString());
					aggVO.setParentVO(hvo);
					list.add(aggVO);
				}
			} else if (fullMonthWkHours.compareTo(projWkHours) == 0) {
				int len = projWkHoursMap.keySet().size();
				for (String projectPK : projWkHoursMap.keySet()) {
					UFDouble wkHours = projWkHoursMap.get(projectPK);
					if (null == wkHours) {
						wkHours = UFDouble.ZERO_DBL;
					}
					UFDouble shareMny = wkHours.multiply(itemMny).div(fullMonthWkHours);
					if (len == 1) {
						shareMny = itemMny.sub(projMny);
					}
					shareMny = new UFDouble(getRoundNumber(shareMny, isTWArea));
					AggAllocateOutVO aggVO = new AggAllocateOutVO();
					AllocateOutHVO hvo = (AllocateOutHVO) baseHVO.clone();
					hvo.setPk_project(projectPK);
					hvo.setDef1(projectPK);
					hvo.setDef2(shareMny.toString());
					aggVO.setParentVO(hvo);
					list.add(aggVO);
					projMny = projMny.add(shareMny);
					len--;
				}
			} else {
				try {
					PsndocVO psndocVO = getAppDao().retrieveByPK(PsndocVO.class, pk_psndoc);
					// 当前期间人员[{0}]项目工时[{1}]超出全月工时[{2}]!
					ExceptionUtils.wrappBusinessException(ResHelper.getString("allocate", "0allcate-ui-000018", null,
							new String[] { psndocVO.getCode(), projWkHours.toString(), psnMonthWkHours.toString() }));
				} catch (DAOException e) {
					Logger.error(e.getMessage());
				}
			}
		} else {
			AggAllocateOutVO aggVO = new AggAllocateOutVO();
			AllocateOutHVO hvo = (AllocateOutHVO) baseHVO.clone();
			hvo.setDef2(getRoundNumber(itemMny, isTWArea));
			aggVO.setParentVO(hvo);
			list.add(aggVO);
		}

		return list;
	}

	protected UFDouble calculateSum(UFDouble[] ufdArr) {
		UFDouble sum = UFDouble.ZERO_DBL;
		for (UFDouble ufd : ufdArr) {
			sum = sum.add(ufd == null ? UFDouble.ZERO_DBL : ufd);
		}
		return sum;
	}

	protected List<AggAllocateOutVO> getProjSalaryShare(Map<String, List<ProjSalaryHVO>> psnProjSaVosMap,
			AllocateOutHVO baseHVO) throws BusinessException {
		List<AggAllocateOutVO> list = new ArrayList<AggAllocateOutVO>();
		List<ProjSalaryHVO> tempList = psnProjSaVosMap.get(baseHVO.getPk_psndoc());
		if (null != tempList && !tempList.isEmpty()) {
			boolean isTWArea = isTWOrg(baseHVO.getPk_org());
			for (ProjSalaryHVO pjHVO : tempList) {
				if (!baseHVO.getPk_classitem().equals(pjHVO.getPk_classitem())) {
					continue;
				}
				UFDouble salaryAmt = pjHVO.getSalaryamt();
				if (null == salaryAmt) {
					salaryAmt = UFDouble.ZERO_DBL;
				}
				AggAllocateOutVO aggVO = new AggAllocateOutVO();
				AllocateOutHVO hvo = (AllocateOutHVO) baseHVO.clone();
				hvo.setPk_project(pjHVO.getDef1());
				hvo.setDef1(pjHVO.getDef1());
				hvo.setDef2(getRoundNumber(salaryAmt, isTWArea));
				aggVO.setParentVO(hvo);
				list.add(aggVO);
			}
		}
		return list;
	}

	protected List<AggAllocateOutVO> getProjOverTimeShare(Map<String, List<OvertimeRegVO>> psnPjOtHoursMap,
			AllocateOutHVO baseHVO) throws BusinessException {
		List<AggAllocateOutVO> list = new ArrayList<AggAllocateOutVO>();
		List<OvertimeRegVO> tempList = psnPjOtHoursMap.get(baseHVO.getPk_psndoc());
		if (null != tempList && !tempList.isEmpty()) {
			UFDouble itemMny = (StringUtils.isEmpty(baseHVO.getDef2()) ? UFDouble.ZERO_DBL : new UFDouble(
					baseHVO.getDef2()));
			baseHVO.setDef2(null);
			UFDouble totalOtHours = UFDouble.ZERO_DBL;
			Map<String, UFDouble> pjOtHoursMap = new HashMap<String, UFDouble>();
			for (OvertimeRegVO vo : tempList) {
				if (null == vo || StringUtils.isEmpty(vo.getProjectcode())) {
					continue;
				}
				UFDouble otHours = pjOtHoursMap.get(vo.getProjectcode());
				if (null == otHours) {
					otHours = UFDouble.ZERO_DBL;
				}
				UFDouble overHour = (null == vo.getOvertimehour() ? UFDouble.ZERO_DBL : vo.getOvertimehour());
				otHours = otHours.add(overHour);
				pjOtHoursMap.put(vo.getProjectcode(), otHours);
				totalOtHours = totalOtHours.add(overHour);
			}
			if (!pjOtHoursMap.isEmpty()) {
				boolean isTWArea = isTWOrg(baseHVO.getPk_org());
				int len = pjOtHoursMap.keySet().size();
				UFDouble subTotMny = UFDouble.ZERO_DBL;
				for (String project : pjOtHoursMap.keySet()) {
					UFDouble otHours = pjOtHoursMap.get(project);
					UFDouble shareMny = otHours.multiply(itemMny).div(totalOtHours);
					if (len == 1) {
						shareMny = itemMny.sub(subTotMny);
					}
					shareMny = new UFDouble(getRoundNumber(shareMny, isTWArea));
					AggAllocateOutVO aggVO = new AggAllocateOutVO();
					AllocateOutHVO hvo = (AllocateOutHVO) baseHVO.clone();
					hvo.setPk_project(project);
					hvo.setDef1(project);
					hvo.setDef2(shareMny.toString());
					aggVO.setParentVO(hvo);
					list.add(aggVO);
					subTotMny = subTotMny.add(shareMny);
					len--;
				}
			}
		}

		return list;
	}

	protected WaClassItemVO[] qryClassItems(WaLoginContext context) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_item.itemkey, wa_item.iitemtype, wa_item.ifldwidth,wa_item.category_id ");
		sqlBuffer.append(", wa_classitem.iflddecimal, wa_item.defaultflag, wa_item.iproperty, wa_classitem.* ");
		sqlBuffer.append(", 'Y' editflag from wa_classitem, wa_item ");
		sqlBuffer.append(" where wa_classitem.pk_wa_item = wa_item.pk_wa_item and wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append(" and wa_classitem.cyear = ?  and wa_classitem.cperiod = ?  ");
		sqlBuffer.append(" and wa_classitem.pk_org='").append(context.getPk_org()).append("' ");
		sqlBuffer.append(" and wa_item.mid = 'N' and wa_classitem.dr <> 1 ");
		sqlBuffer.append(" and wa_classitem.def1 in (select bd_defdoc.pk_defdoc from bd_defdoc ");
		sqlBuffer.append(" inner join bd_defdoclist on bd_defdoc.pk_defdoclist=bd_defdoclist.pk_defdoclist ");
		sqlBuffer.append(" and bd_defdoclist.code='WITSCS' and upper(bd_defdoc.code) in ('A','B','C','D','Z','E') )");
		sqlBuffer.append(" order by icomputeseq");

		SQLParameter parameter = WherePartUtil.getCommonParameter(context.getWaLoginVO());
		WaClassItemVO[] classItemVOs = ((WaClassItemVO[]) getAppDao().executeQueryVOs(sqlBuffer.toString(), parameter,
				WaClassItemVO.class));
		return classItemVOs;
	}

	protected String getDefdocCondition(String code, String condition) {
		StringBuilder where = new StringBuilder();
		where.append(" pk_defdoclist in (select pk_defdoclist from bd_defdoclist ");
		where.append(" where code='").append(code).append("') ");
		where.append(" and enablestate=2 and dr <> 1 ");
		if (StringUtils.isNotEmpty(condition)) {
			where.append(" and ").append(condition);
		}
		return where.toString();
	}

	protected <T extends SuperVO> Map<String, T> queryBasicInfo(Class<T> voClass, String condition, String[] fields)
			throws DAOException {
		T[] vos = getAppDao().retrieveByClause(voClass, condition);
		Map<String, T> resultMap = new HashMap<String, T>();
		if (!ArrayUtils.isEmpty(vos) && !ArrayUtils.isEmpty(fields)) {
			for (T vo : vos) {
				StringBuilder keyBuf = new StringBuilder();
				for (String attr : fields) {
					keyBuf.append(String.valueOf(vo.getAttributeValue(attr)));
				}
				resultMap.put(keyBuf.toString(), vo);
			}
		}

		return resultMap;
	}

	protected <T extends SuperVO> Map<String, List<T>> queryUnionVOList(Class<T> voClass, String condition,
			String[] fields) throws DAOException {
		T[] vos = getAppDao().retrieveByClause(voClass, condition);
		Map<String, List<T>> resultMap = new HashMap<String, List<T>>();
		if (!ArrayUtils.isEmpty(vos) && !ArrayUtils.isEmpty(fields)) {
			for (T vo : vos) {
				StringBuilder keyBuf = new StringBuilder();
				for (String attr : fields) {
					keyBuf.append(String.valueOf(vo.getAttributeValue(attr)));
				}
				List<T> voList = resultMap.get(keyBuf.toString());
				if (null == voList) {
					voList = new ArrayList<T>();
					resultMap.put(keyBuf.toString(), voList);
				}
				voList.add(vo);
			}
		}

		return resultMap;
	}

	protected String getPeriodWhere(WaLoginContext context) {
		StringBuilder periodBuf = new StringBuilder(" sealflag='N' and ");
		periodBuf.append(" pk_org='").append(context.getPk_org()).append("' ");
		periodBuf.append(" and timeyear='").append(context.getWaLoginVO().getPeriodVO().getCaccyear()).append("' ");
		periodBuf.append(" and timemonth='").append(context.getWaLoginVO().getPeriodVO().getCaccperiod()).append("' ");
		periodBuf.append(" and dr <> 1 ");

		return periodBuf.toString();
	}

	protected String getProjCodeWhere(WaLoginContext context) {
		StringBuilder where = new StringBuilder();
		where.append(" pk_org='").append(context.getPk_org()).append("' ");
		where.append(" and pk_wa_calss='").append(context.getClassPK()).append("' ");
		where.append(" and cperiod='").append(context.getWaLoginVO().getPeriodVO().getCyear())
				.append(context.getWaLoginVO().getPeriodVO().getCperiod()).append("' ");
		where.append(" and dr <> 1 ");

		return where.toString();
	}

	protected String getProjOverTimeWhere(WaLoginContext context, PeriodVO periodVO) {
		StringBuilder where = new StringBuilder();
		where.append(" pk_org='").append(context.getPk_org()).append("' ");
		where.append(" and approve_time>='").append(periodVO.getBegindate().toStdString()).append(" 00:00:00' ");
		where.append(" and approve_time<='").append(periodVO.getEnddate().toStdString()).append(" 23:59:59' ");
		// where.append(" and billsource=").append(ICommonConst.BILL_SOURCE_APPLY);
		where.append(" and dr <> 1 ");

		return where.toString();
	}

	protected Map<String, UFDouble> qryWkHoursFromPsnCalendar(WaLoginContext context, PeriodVO periodVO,
			String[] pk_psndocs) throws BusinessException {
		Map<String, UFDouble> psnWkHoursMap = new HashMap<String, UFDouble>();

		IShiftQueryService shiftService = NCLocator.getInstance().lookup(IShiftQueryService.class);
		// Map<String, ShiftVO> shiftPKMap =
		// shiftService.queryShiftVOMapByOrg(context.getPk_org());
		Map<String, ShiftVO> shiftPKMap = shiftService.queryShiftVOMapByHROrg(context.getPk_org());
		IPsnCalendarQueryService psnCalendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnDateCalendarMap = psnCalendarService
				.queryCalendarVOByPsnInSQLForProcess(context.getPk_org(), periodVO.getBegindate(),
						periodVO.getEnddate(), pk_psndocs);
		if (null != psnDateCalendarMap && !psnDateCalendarMap.isEmpty()) {
			for (String pk_psndoc : psnDateCalendarMap.keySet()) {
				UFDouble wkHours = UFDouble.ZERO_DBL;
				Map<UFLiteralDate, AggPsnCalendar> dateCalandarMap = psnDateCalendarMap.get(pk_psndoc);
				if (null != shiftPKMap && !shiftPKMap.isEmpty() && null != dateCalandarMap
						&& !dateCalandarMap.isEmpty()) {
					for (UFLiteralDate date : dateCalandarMap.keySet()) {
						AggPsnCalendar psnCalendar = dateCalandarMap.get(date);
						if (null == psnCalendar) {
							continue;
						}
						PsnCalendarVO hvo = psnCalendar.getPsnCalendarVO();
						if (null == hvo || StringUtils.isEmpty(hvo.getPk_shift())
								|| null == shiftPKMap.get(hvo.getPk_shift()) || hvo.getPk_shift().equals(ShiftVO.PK_GX)) {
							continue;
						}
						ShiftVO shiftVO = shiftPKMap.get(hvo.getPk_shift());
						UFDouble shiftHour = (null == shiftVO ? UFDouble.ZERO_DBL : shiftVO.getGzsj());
						wkHours = wkHours.add(shiftHour);
					}
				}
				psnWkHoursMap.put(pk_psndoc, wkHours);
			}
		}

		return psnWkHoursMap;
	}

	protected Map<String, UFDouble> qryMonthWkHoursByOrgCalander(WaLoginContext context, PeriodVO periodVO,
			String[] psndocPks) throws BusinessException {
		// UFDouble monthWkHours = UFDouble.ZERO_DBL;
		// HolidayVO[] holidayVOs =
		// getAppDao().retrieveByClause(HolidayVO.class,
		// " enablestate=2 and allflag='Y' and isdeftime='N' and holidayyear='"
		// + context.getCyear() + "' ");
		// List<Calendar> holidayList = new ArrayList<Calendar>();
		// for (HolidayVO vo : holidayVOs) {
		// Calendar calendar = Calendar.getInstance();
		// try {
		// calendar.setTime(WaConstant.DF.parse(vo.getStarttime()));
		// } catch (ParseException e) {
		// Logger.error(e.getMessage());
		// }
		// holidayList.add(calendar);
		// }
		//
		// int wkDays = 0;
		// String periodEndDateStr =
		// WaConstant.DF.format(periodVO.getEnddate().toDate());
		// Calendar ca = Calendar.getInstance();
		// ca.setTimeInMillis(periodVO.getBegindate().getMillis());
		// while (true) {
		// if (!checkHoliday(ca, holidayList)) {
		// wkDays++;
		// }
		// String calDateStr = WaConstant.DF.format(ca.getTime());
		// if (periodEndDateStr.equals(calDateStr)) {
		// break;
		// }
		// ca.add(Calendar.DAY_OF_MONTH, 1);
		//
		// }
		// monthWkHours = new UFDouble(8 * wkDays);
		// return monthWkHours;

		Map<String, UFDouble> psnWkHoursMap = new HashMap<String, UFDouble>();
		IHRHolidayQueryService holidayService = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		Map<String, Map<String, Integer>> psnWOrkDayTypeMap = holidayService.queryPsnWorkDayTypeInfo(
				context.getPk_org(), psndocPks, periodVO.getBegindate(), periodVO.getEnddate());
		if (null != psnWOrkDayTypeMap && !psnWOrkDayTypeMap.isEmpty()) {
			for (String pk_psndoc : psnWOrkDayTypeMap.keySet()) {
				int wkdays = 0;
				Map<String, Integer> dateWorkTypeMap = psnWOrkDayTypeMap.get(pk_psndoc);
				if (null != dateWorkTypeMap && !dateWorkTypeMap.isEmpty()) {
					for (String date : dateWorkTypeMap.keySet()) {
						Integer wkType = dateWorkTypeMap.get(date);
						if (null != wkType && wkType.intValue() == HolidayVO.DAY_TYPE_WORKDAY) {
							wkdays++;
						}
					}
				}
				psnWkHoursMap.put(pk_psndoc, new UFDouble(wkdays * 8));
			}
		}
		return psnWkHoursMap;
	}

	public boolean checkHoliday(Calendar calendar, List<Calendar> holidayList) {
		// 判断日期是否是周六周日
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
				|| calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
			return true;
		}
		// 判断日期是否是节假日
		for (Calendar ca : holidayList) {
			if (ca.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
					&& ca.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
					&& ca.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings({ "unchecked", "serial" })
	protected Map<String, UFDouble> qryPsnLeaveHours(WaLoginContext context, PeriodVO periodVO) throws DAOException {
		StringBuilder sql = new StringBuilder();
		sql.append(" select pk_psndoc,sum(isnull(leavehour,0)) as leavehour from tbm_leavereg");
		sql.append(" where pk_org='").append(context.getPk_org()).append("' ");
		sql.append(" and approve_time >='").append(periodVO.getBegindate().toStdString()).append(" 00:00:00' ");
		sql.append(" and approve_time <='").append(periodVO.getEnddate().toStdString()).append(" 23:59:59' ");
		sql.append(" and leavebegintime >='").append(periodVO.getBegindate().toStdString()).append(" 00:00:00' ");
		sql.append(" and leavebegintime <='").append(periodVO.getEnddate().toStdString()).append(" 23:59:59' ");
		sql.append(" and leaveendtime >='").append(periodVO.getBegindate().toStdString()).append(" 00:00:00' ");
		sql.append(" and leaveendtime <='").append(periodVO.getEnddate().toStdString()).append(" 23:59:59' ");
		sql.append(" and dr <> 1 ");
		sql.append(" group by pk_psndoc ");
		Map<String, UFDouble> psnLeaveHoursMap = (Map<String, UFDouble>) getAppDao().getBaseDao().executeQuery(
				sql.toString(), new ResultSetProcessor() {
					Map<String, UFDouble> map = new HashMap<String, UFDouble>();

					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {
						while (rs.next()) {
							map.put(rs.getString(1), new UFDouble(rs.getString(2)));
						}
						return map;
					}
				});
		return psnLeaveHoursMap;
	}

	@SuppressWarnings({ "serial", "unchecked" })
	protected Map<String, Map<String, UFDouble>> qryPsnWkHoursOutersys(WaLoginContext context, PeriodVO periodVO,
			String[] psndocPks) throws BusinessException {
		StringBuilder sql = new StringBuilder("select pk_psndoc,pk_project,sum(isnull(workhour,0)) as workhour from (");
		sql.append(" select replace(wits_io_hr_timecard.wdat,'/','-') as wdate,wits_io_hr_timecard.workhour");
		sql.append(" ,pyear,pmonth,org_orgs.pk_org,bd_psndoc.pk_psndoc,proj.pk_defdoc as pk_project");
		sql.append(" from wits_io_hr_timecard left join org_orgs on org_orgs.code=wits_io_hr_timecard.comno");
		sql.append(" left join bd_psndoc on bd_psndoc.code=wits_io_hr_timecard.empno");
		sql.append(" left join (select bd_defdoc.pk_defdoc,bd_defdoc.code from bd_defdoc");
		sql.append(" inner join bd_defdoclist on bd_defdoc.pk_defdoclist=bd_defdoclist.pk_defdoclist");
		sql.append(" where bd_defdoclist.code='ProjectCode') proj on proj.code=wits_io_hr_timecard.prono");
		sql.append(" ) t where pk_org=? and wdate>=? and wdate<=? ");
		sql.append(" and pk_psndoc in ").append(InSqlManager.getInSQLValue(psndocPks));
		sql.append(" group by pk_psndoc,pk_project ");
		SQLParameter param = new SQLParameter();
		param.addParam(context.getPk_org());
		param.addParam(periodVO.getBegindate().toStdString());
		param.addParam(periodVO.getEnddate().toStdString());
		Map<String, Map<String, UFDouble>> psnPjWkHoursMap = (Map<String, Map<String, UFDouble>>) getAppDao()
				.getBaseDao().executeQuery(sql.toString(), param, new ResultSetProcessor() {
					Map<String, Map<String, UFDouble>> map = new HashMap<String, Map<String, UFDouble>>();

					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {
						while (rs.next()) {
							String pk_psndoc = rs.getString(1);
							String pk_project = rs.getString(2);
							UFDouble workhour = (rs.getString(3) == null ? UFDouble.ZERO_DBL : new UFDouble(rs
									.getString(3)));
							Map<String, UFDouble> pjWkHourMap = map.get(pk_psndoc);
							if (null == pjWkHourMap) {
								pjWkHourMap = new HashMap<String, UFDouble>();
								map.put(pk_psndoc, pjWkHourMap);
							}
							pjWkHourMap.put(pk_project, workhour);
						}
						return map;
					}
				});
		return psnPjWkHoursMap;
	}

	protected <T extends SuperVO> Map<String, List<T>> queryMapListInfo(Class<T> voClass, String condition,
			String[] fields) throws DAOException {
		Map<String, List<T>> psnPjSaVOMap = new HashMap<String, List<T>>();
		T[] vos = getAppDao().retrieveByClause(voClass, condition);
		if (!ArrayUtils.isEmpty(vos)) {
			for (T vo : vos) {
				StringBuilder key = new StringBuilder();
				for (String attr : fields) {
					key.append(String.valueOf(vo.getAttributeValue(attr)));
				}
				if (StringUtils.isEmpty(key.toString())) {
					continue;
				}
				List<T> tempList = psnPjSaVOMap.get(key.toString());
				if (null == tempList) {
					tempList = new ArrayList<T>();
					psnPjSaVOMap.put(key.toString(), tempList);
				}
				tempList.add(vo);
			}
		}

		return psnPjSaVOMap;
	}

	private AllocateOutHVO createBaseHVO(WaLoginContext context) {
		AllocateOutHVO hvo = new AllocateOutHVO();
		hvo.setPk_allocate(null);
		hvo.setStatus(VOStatus.NEW);
		hvo.setPk_group(context.getPk_group());
		hvo.setPk_org(context.getPk_org());
		hvo.setPk_wa_calss(context.getClassPK());
		hvo.setCperiod(context.getWaLoginVO().getPeriodVO().getCyear()
				+ context.getWaLoginVO().getPeriodVO().getCperiod());
		hvo.setDbilldate(AppContext.getInstance().getBusiDate());
		hvo.setFstatusflag(BillStatusEnum.FREE.toIntValue());
		hvo.setPk_classitem(null);
		hvo.setPk_costcenter(null);
		hvo.setPk_psndoc(null);
		hvo.setPk_psnjob(null);
		hvo.setPk_project(null);
		hvo.setDef1(null);
		hvo.setDef2(null);
		return hvo;
	}

	@Override
	public List<AllocateCsvVO> transferToCsvInfo(Object[] datas) throws BusinessException {
		return transferToCsv(datas);
	}

	@Override
	public List<AllocateCsvVO> transferToCsvInfo(AggAllocateOutVO[] bills) throws BusinessException {
		return transferToCsv(bills);
	}

	/*
	 * 明细档生成Allocate的CSV文件内容构建.
	 */
	protected List<AllocateCsvVO> transferToCsv(Object[] datas) throws BusinessException {
		Map<String, AllocateCsvVO> unionIDMap = new HashMap<String, AllocateCsvVO>();
		if (!ArrayUtils.isEmpty(datas)) {
			Set<String> classItemPk = new HashSet<String>();
			Set<String> costCenterPk = new HashSet<String>();
			Set<String> psndocPk = new HashSet<String>();
			for (Object obj : datas) {
				if (obj instanceof AggAllocateOutVO) {
					AllocateOutHVO hvo = ((AggAllocateOutVO) obj).getParentVO();
					if (StringUtils.isNotEmpty(hvo.getPk_classitem())) {
						classItemPk.add(hvo.getPk_classitem());
					}
					if (StringUtils.isNotEmpty(hvo.getPk_costcenter())) {
						costCenterPk.add(hvo.getPk_costcenter());
					}
					if (StringUtils.isNotEmpty(hvo.getPk_psndoc())) {
						psndocPk.add(hvo.getPk_psndoc());
					}
				}
			}
			// 薪资项目
			StringBuilder classItemBuf = new StringBuilder();
			classItemBuf.append(" pk_wa_classitem in ").append(InSqlManager.getInSQLValue(classItemPk));
			Map<String, WaClassItemVO> itemVOMap = queryBasicInfo(WaClassItemVO.class, classItemBuf.toString(),
					new String[] { WaClassItemVO.PK_WA_CLASSITEM });
			StringBuilder filerItemWhere = new StringBuilder();
			filerItemWhere.append(" pk_wa_item in (select pk_wa_item  from wa_classitem where ");
			filerItemWhere.append(classItemBuf).append(") ");
			Map<String, WaItemVO> pkItemVOMap = queryBasicInfo(WaItemVO.class, filerItemWhere.toString(),
					new String[] { WaItemVO.PK_WA_ITEM });
			// 分摊方案档案
			Map<String, DefdocVO> shareVOMap = queryBasicInfo(DefdocVO.class,
					getDefdocCondition(WaConstant.DEF_CODE_SHARE, null), new String[] { DefdocVO.PK_DEFDOC });
			// 专案代码档案
			Map<String, DefdocVO> projVOMap = queryBasicInfo(DefdocVO.class,
					getDefdocCondition(WaConstant.DEF_CODE_PROJ, null), new String[] { DefdocVO.PK_DEFDOC });
			// 借方科目档案
			Map<String, DefdocVO> debitAccVOMap = queryBasicInfo(DefdocVO.class,
					getDefdocCondition(WaConstant.DEF_CODE_DEBIT_ACC, null), new String[] { DefdocVO.PK_DEFDOC });
			// 借方供应商档案
			Map<String, DefdocVO> debitVenVOMap = queryBasicInfo(DefdocVO.class,
					getDefdocCondition(WaConstant.DEF_CODE_DEBIT_VEN, null), new String[] { DefdocVO.PK_DEFDOC });
			// 贷方科目档案
			Map<String, DefdocVO> creditAccVOMap = queryBasicInfo(DefdocVO.class,
					getDefdocCondition(WaConstant.DEF_CODE_CREDIT_ACC, null), new String[] { DefdocVO.PK_DEFDOC });
			// 贷方供应商档案
			Map<String, DefdocVO> creditVenVOMap = queryBasicInfo(DefdocVO.class,
					getDefdocCondition(WaConstant.DEF_CODE_CREDIT_VEN, null), new String[] { DefdocVO.PK_DEFDOC });
			// 社保厂商档案
			Map<String, DefdocVO> ssmfVOMap = queryBasicInfo(DefdocVO.class,
					getDefdocCondition(WaConstant.DEF_CODE_SSMF, null), new String[] { DefdocVO.PK_DEFDOC });

			// 社保供應商代碼
			Map<String, DefdocVO> smvenVOMap = queryBasicInfo(DefdocVO.class,
					getDefdocCondition(WaConstant.DEF_CODE_BMVEN, null), new String[] { DefdocVO.PK_DEFDOC });

			// 员工薪资档案成本中心
			StringBuilder costcenterBuf = new StringBuilder();
			costcenterBuf.append(" pk_costcenter in ").append(InSqlManager.getInSQLValue(costCenterPk));
			Map<String, CostCenterVO> costCenterVOMap = queryBasicInfo(CostCenterVO.class, costcenterBuf.toString(),
					new String[] { CostCenterVO.PK_COSTCENTER });
			// 人员基本信息
			StringBuilder psnBuf = new StringBuilder();
			psnBuf.append(" pk_psndoc in ").append(InSqlManager.getInSQLValue(psndocPk));
			Map<String, PsndocVO> psnVOMap = queryBasicInfo(PsndocVO.class, psnBuf.toString(),
					new String[] { PsndocVO.PK_PSNDOC });
			// 社保信息
			AllocateOutHVO alloHvo = ((AggAllocateOutVO) datas[0]).getParentVO();
			StringBuilder bmdataBuf = new StringBuilder();
			bmdataBuf.append(" dr <> 1 and pk_org='").append(alloHvo.getPk_org()).append("' ");
			//Mod tank 直接用薪资期间去匹配
			bmdataBuf.append(" and cyear = '"+alloHvo.getCperiod().substring(0, 4)+"' and cperiod = '"+alloHvo.getCperiod().substring(4, 6)+"'  ");
/*			
			
			bmdataBuf.append(" and pk_psnjob in (select pk_psnjob from hi_psnjob where begindate <= '"
					+ getLastDayOfMonth(alloHvo.getCperiod().substring(0, 4), alloHvo.getCperiod().substring(4, 6))
							.toString().substring(0, 10)
					+ "' and isnull(enddate, '9999-12-31') >= '"
					+ getFirstDayOfMonth(alloHvo.getCperiod().substring(0, 4), alloHvo.getCperiod().substring(4, 6))
							.toString().substring(0, 10) + "') ");
			
			*/
			Map<String, List<BmDataVO>> bmdataVOMap = queryUnionVOList(BmDataVO.class, bmdataBuf.toString(),
					new String[] { BmDataVO.PK_PSNDOC});
			// 险种信息
			StringBuilder bmClassBuf = new StringBuilder(" pk_bm_class in (select pk_bm_class from bm_data where ");
			bmClassBuf.append(bmdataBuf).append(" ) ");
			Map<String, BmClassVO> bmClassVOMap = queryBasicInfo(BmClassVO.class, bmClassBuf.toString(),
					new String[] { BmClassVO.PK_BM_CLASS });

			for (Object obj : datas) {
				if (obj instanceof AggAllocateOutVO) {
					AllocateOutHVO hvo = ((AggAllocateOutVO) obj).getParentVO();
					WaClassItemVO itemVO = itemVOMap.get(hvo.getPk_classitem());
					WaItemVO waItemVO = (null != itemVO ? pkItemVOMap.get(itemVO.getPk_wa_item()) : null);
					if (itemVO == null || waItemVO == null) {
						continue;
					}
					DefdocVO shareDefVO = shareVOMap.get(itemVO.getDef1());
					String shareCode = (shareDefVO == null ? "" : shareDefVO.getCode());
					String projtext = hvo.getCperiod() + MultiLangHelper.getName(itemVO);
					UFDouble shareMny = (hvo.getDef2() == null ? UFDouble.ZERO_DBL : new UFDouble(hvo.getDef2()));
					CostCenterVO costCenVO = costCenterVOMap.get(hvo.getPk_costcenter());
					String costcenter = (costCenVO == null ? "" : costCenVO.getCccode());
					String orderCode = "";
					if (shareCode.equals(WaConstant.SHARE_MONTH_WORKHOURS)
							|| shareCode.equals(WaConstant.SHARE_PROJ_CODE)
							|| shareCode.equals(WaConstant.SHARE_PROJ_OVERHOURS)
							|| shareCode.equals(WaConstant.SHARE_LEAVE_SHARED)) {
						if (StringUtils.isNotEmpty(hvo.getDef1()) && null != projVOMap.get(hvo.getDef1())) {
							orderCode = projVOMap.get(hvo.getDef1()).getCode();
						}
					} else if (shareCode.equals(WaConstant.SHARE_FULL_COSTCENTER)) {
						orderCode = costcenter;
					}
					DefdocVO debitAccDefVO = debitAccVOMap.get(itemVO.getDef2());
					String debitAccCode = (debitAccDefVO == null ? "" : debitAccDefVO.getCode());
					DefdocVO debitVenDefVO = debitVenVOMap.get(itemVO.getDef3());
					String debitVenDefCode = (debitVenDefVO == null ? "" : debitVenDefVO.getCode());
					String debitVenCode = getVenderCode(debitVenDefCode, hvo, psnVOMap, ssmfVOMap, bmdataVOMap,
							waItemVO, smvenVOMap, bmClassVOMap);

					AllocateCsvVO debitCsvVO = new AllocateCsvVO(debitAccCode, shareMny, costcenter, orderCode, "",
							projtext, debitVenCode);
					setDirecMny(debitCsvVO, waItemVO, false);

					if (null != unionIDMap.get(debitCsvVO.getUnionID())) {
						AllocateCsvVO aCsvVO = unionIDMap.get(debitCsvVO.getUnionID());
						aCsvVO.setVouchmny(aCsvVO.getVouchmny().add(debitCsvVO.getVouchmny()));
					} else {
						unionIDMap.put(debitCsvVO.getUnionID(), debitCsvVO);
					}

					DefdocVO creditAccDefVO = creditAccVOMap.get(itemVO.getDef4());
					String creditAccCode = (creditAccDefVO == null ? "" : creditAccDefVO.getCode());
					DefdocVO creditVenDefVO = creditVenVOMap.get(itemVO.getDef5());
					String creditVenDefCode = (creditVenDefVO == null ? "" : creditVenDefVO.getCode());
					String creditVenCode = getVenderCode(creditVenDefCode, hvo, psnVOMap, ssmfVOMap, bmdataVOMap,
							waItemVO, smvenVOMap, bmClassVOMap);

					// AllocateCsvVO creditCsvVO = new
					// AllocateCsvVO(creditAccCode, shareMny.multiply(-1.0),
					// costcenter,
					// orderCode, "", projtext, creditVenCode);
					AllocateCsvVO creditCsvVO = new AllocateCsvVO(creditAccCode, shareMny, "", "", "", projtext,
							creditVenCode);
					setDirecMny(creditCsvVO, waItemVO, true);

					if (null != unionIDMap.get(creditCsvVO.getUnionID())) {
						AllocateCsvVO aCsvVO = unionIDMap.get(creditCsvVO.getUnionID());
						aCsvVO.setVouchmny(aCsvVO.getVouchmny().add(creditCsvVO.getVouchmny()));
					} else {
						unionIDMap.put(creditCsvVO.getUnionID(), creditCsvVO);
					}
				}
			}

		}
		return new ArrayList<AllocateCsvVO>(unionIDMap.values());
	}

	private UFDate getFirstDayOfMonth(String year, String month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(year), Integer.valueOf(month) - 1, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return new UFDate(calendar.getTime()).asBegin();
	}

	private UFDate getLastDayOfMonth(String year, String month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(year), Integer.valueOf(month) - 1, 1);
		int lastday = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH, lastday);
		return new UFDate(calendar.getTime()).asEnd();
	}

	public boolean isTWOrg(String pk_org) throws BusinessException {
		OrgVO[] orgVOs = OrgQueryUtil.queryOrgVOByPks(new String[] { pk_org });
		IGlobalCountryQueryService czQry = NCLocator.getInstance().lookup(IGlobalCountryQueryService.class);
		CountryZoneVO czVO = czQry.getCountryZoneByPK(orgVOs[0].getCountryzone());
		return czVO.getCode().equals("TW");
	}

	private void setDirecMny(AllocateCsvVO csvVO, WaItemVO waItemVO, boolean isCredit) {
		Integer iproperty = (null != waItemVO ? waItemVO.getIproperty() : null);
		if (null != iproperty) {
			UFDouble scvMny = csvVO.getVouchmny(); // .abs();
			if ((isCredit && (iproperty.intValue() == 0 || iproperty.intValue() == 2))
					|| (!isCredit && iproperty.intValue() == 1)) {
				scvMny = scvMny.multiply(-1);
			}
			csvVO.setVouchmny(scvMny);
		}
	}

	private String getVenderCode(String venCode, AllocateOutHVO hvo, Map<String, PsndocVO> psnVOMap,
			Map<String, DefdocVO> ssmfVOMap, Map<String, List<BmDataVO>> bmdataVOMap, WaItemVO waItemVO,
			Map<String, DefdocVO> smvenVOMap, Map<String, BmClassVO> bmClassVOMap) {
		String retCode = "";
		BmDataVO bmdataVO = null;
		List<BmDataVO> bmdataList = bmdataVOMap.get(hvo.getPk_psndoc());
		if (venCode.toUpperCase().equals(WaConstant.VENDER_SIFI)) {
			bmdataVO = (null != bmdataList && !bmdataList.isEmpty()) ? bmdataList.get(0) : null;
			if (null != bmdataVO && StringUtils.isNotEmpty(bmdataVO.getDef1())) {
				DefdocVO ssmfDefVO = ssmfVOMap.get(bmdataVO.getDef1());
				retCode = (ssmfDefVO == null ? "" : ssmfDefVO.getCode());
			}
		} else if (venCode.toUpperCase().equals(WaConstant.VENDER_SIF_COMP)) {
			bmdataVO = getDataVOByItem(bmdataList, waItemVO, bmClassVOMap);
			if (null != bmdataVO && StringUtils.isNotEmpty(bmdataVO.getDef2())) {
				DefdocVO smvenDefVO = smvenVOMap.get(bmdataVO.getDef2());
				retCode = (smvenDefVO == null ? "" : smvenDefVO.getCode());
			}
		} else if (venCode.toUpperCase().equals(WaConstant.VENDER_SIF_EMPL)) {
			bmdataVO = getDataVOByItem(bmdataList, waItemVO, bmClassVOMap);
			if (null != bmdataVO && StringUtils.isNotEmpty(bmdataVO.getDef3())) {
				DefdocVO smvenDefVO = smvenVOMap.get(bmdataVO.getDef3());
				retCode = (smvenDefVO == null ? "" : smvenDefVO.getCode());
			}
		} else if (venCode.toUpperCase().equals(WaConstant.VENDER_EMPL)) {
			PsndocVO psnVO = psnVOMap.get(hvo.getPk_psndoc());
			retCode = (psnVO == null ? "" : psnVO.getCode());
		} else {
			retCode = venCode;
		}

		return retCode;
	}

	private BmDataVO getDataVOByItem(List<BmDataVO> bmdataList, WaItemVO waItemVO, Map<String, BmClassVO> bmClassVOMap) {
		if ((null == bmdataList || bmdataList.isEmpty()) || null == waItemVO || MapUtils.isEmpty(bmClassVOMap)) {
			return null;
		}
		String itemName = waItemVO.getName();
		if (StringUtils.isNotBlank(itemName)) {
			for (BmDataVO dataVO : bmdataList) {
				if (null == dataVO || StringUtils.isEmpty(dataVO.getPk_bm_class())) {
					continue;
				}
				BmClassVO bmClass = bmClassVOMap.get(dataVO.getPk_bm_class());
				if (null == bmClass || StringUtils.isBlank(bmClass.getName())) {
					continue;
				}
				if (itemName.contains("大病")) {
					if (bmClass.getName().contains("大病")) {
						return dataVO;
					}
				} else {
					if (itemName.substring(0, 4).indexOf(bmClass.getName().trim().substring(0, 4)) != -1) {
						return dataVO;
					}
				}
			}
		}
		return null;
	}

	private String getRoundNumber(UFDouble amt, boolean isTWArea) throws BusinessException {
		String retStr = "";
		if (isTWArea) {
			retStr = Math.round(amt.doubleValue()) + "";
		} else {
			retStr = Math.round(amt.doubleValue() * 100) * 0.01d + "";
		}
		return retStr;
	}
}
