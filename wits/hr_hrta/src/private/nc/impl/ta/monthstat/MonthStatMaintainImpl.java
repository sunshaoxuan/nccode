package nc.impl.ta.monthstat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.bd.pub.distribution.util.BDDistTokenUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfo;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.execute.Executor;
import nc.bs.logging.Logger;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.hr.devitf.IDevItfQueryService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.hrss.IURLGenerator;
import nc.itf.hr.message.IHRMessageSend;
import nc.itf.om.IAOSQueryService;
import nc.itf.ta.ILateEarlyQueryService;
import nc.itf.ta.IMonthStatManageMaintain;
import nc.itf.ta.IMonthStatQueryMaintain;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.ITBMPsndocQueryMaintain;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeDataQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.IViewOrderQueryService;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.impl.DefaultDateScope;
import nc.itf.ta.customization.IMonthDataCreator;
import nc.itf.ta.monthlydata.IMonthlyRecordCreator;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.pubitf.rbac.IUserPubService;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hr.hrss.SSOInfo;
import nc.vo.hr.message.HRBusiMessageVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.ml.MultiLangUtil;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.sm.UserVO;
import nc.vo.ta.bill.TaMessageConst;
import nc.vo.ta.customization.MonthCalParam;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.item.ItemCopyVO;
import nc.vo.ta.item.ItemVO;
import nc.vo.ta.lateearly.LateEarlyVO;
import nc.vo.ta.log.TaBusilogUtil;
import nc.vo.ta.monthstat.DeptMonthStatVO;
import nc.vo.ta.monthstat.MonthStatVO;
import nc.vo.ta.monthstat.MonthStatbVO;
import nc.vo.ta.monthstat.MonthWorkVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.period.YearMonthComparator;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.timeitem.AwayTypeCopyVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.OverTimeTypeCopyVO;
import nc.vo.ta.timeitem.ShutDownTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.ta.vieworder.ViewOrderVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class MonthStatMaintainImpl implements IMonthStatManageMaintain,
		IMonthStatQueryMaintain {

	@Override
	public MonthStatVO[] approve(String pk_org,String year, String month, MonthStatVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
		PeriodVO currPeriodVO =	PeriodServiceFacade.checkCurPeriod(pk_org, year, month);

		MonthStatVO[] retvos = vos;
		List<String> pks = new ArrayList<String>();
		for(int i=0;i<retvos.length;i++){
			pks.add(retvos[i].getPk_monthstat());
			retvos[i].setIsapprove(UFBoolean.TRUE);
		}
		InSQLCreator isc = new InSQLCreator();
		try{
			String sql = "update tbm_monthstat set isapprove = 'Y',iseffective='Y',isuseful='Y' where pk_monthstat in("+isc.getInSQL(pks.toArray(new String[0]))+")";
			new BaseDAO().executeUpdate(sql);
			
			//MOD James
			sql="update tbm_leavereg set waperiod='"+year + month+"' where pk_org='"+pk_org+"' and ((approve_time>='"+
			currPeriodVO.getBegindate()+"' and approve_time<'"+currPeriodVO.getEnddate().getDateAfter(1)+
			"' and leavebegindate<'"+currPeriodVO.getEnddate().getDateAfter(1)+"') or ( leavebegindate<'"+currPeriodVO.getEnddate().getDateAfter(1)+
			"' and leavebegindate>='"+currPeriodVO.getBegindate()+"' and approve_time<'"+currPeriodVO.getEnddate().getDateAfter(1)+
					"' )) and dr=0 and pk_psndoc in(select pk_psndoc from tbm_monthstat  where pk_monthstat in("+isc.getInSQL(pks.toArray(new String[0]))+"))";
			new BaseDAO().executeUpdate(sql);
			
			sql="update tbm_overtimereg set waperiod='"+year + month+"' where pk_org='"+pk_org+"' and approve_time>='"+
					currPeriodVO.getBegindate()+"' and approve_time<'"+currPeriodVO.getEnddate().getDateAfter(1)+
					"' and overtimebegindate<'"+currPeriodVO.getEnddate().getDateAfter(1)+"' and dr=0 and pk_psndoc in(select pk_psndoc from tbm_monthstat  where pk_monthstat in("+isc.getInSQL(pks.toArray(new String[0]))+"))";
					new BaseDAO().executeUpdate(sql);
		}finally{
			isc.clear();
		}

		return retvos;
	}

	@Override
	public void generate(String pk_org, FromWhereSQL fromWhereSQL, String year, String month)
			throws BusinessException {
		PeriodVO curPeriod = PeriodServiceFacade.checkCurPeriod(pk_org, year, month);
		//容错处理：有时候难免考勤档案有数据但月报无数据，此时要做一下容错，将这些人的月报数据插入
		IMonthlyRecordCreator creator = new MonthStatRecordCreator();
		creator.createMonthlyRecord(curPeriod);
		//权限
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", "MonthStatGenerate", fromWhereSQL);
		//除去已审批的
		fromWhereSQL = ensureApprove(pk_org, year, month, fromWhereSQL);
		String[] pk_psndocs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryLatestPsndocsByCondition(pk_org, fromWhereSQL, curPeriod.getBegindate(), curPeriod.getEnddate());
		if(ArrayUtils.isEmpty(pk_psndocs))
			return;
		//计算月报
		generate0(pk_org, pk_psndocs, curPeriod);
		approve(pk_org, year, month, new MonthStatVO[0]);
	}

	/**
	 * 处理月报计算时的查询模板的条件，在里面增加条件，以便把审核已经通过的人员排除在外，因为审核通过的人员是不能再次计算的
	 * @param pk_org
	 * @param fromWhereSQL
	 * @return
	 * @throws BusinessException
	 */
	private FromWhereSQL ensureApprove(String pk_org, String year, String month, FromWhereSQL fromWhereSQL) throws BusinessException{
		TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		boolean needApprove = timeRuleVO!=null&&timeRuleVO.isMonthStatNeedApprove();
		if(!needApprove)//如果不需要审核，则不用排除任何数据
			return fromWhereSQL;
		//否则需要在条件中排除
		//首先保证tbm_psndoc被拼到sql中
		fromWhereSQL = TBMPsndocSqlPiecer.ensureTBMPsndocTable(fromWhereSQL);
		String where = fromWhereSQL.getWhere();
		String tbmpsndocAlias = fromWhereSQL.getTableAliasByAttrpath(".");
		where = SQLHelper.appendExtraCond(where, tbmpsndocAlias+"."+TBMPsndocVO.PK_PSNDOC+
				" not in (select monthstat.pk_psndoc from tbm_monthstat monthstat where monthstat.pk_org="+tbmpsndocAlias+"."+
				TBMPsndocVO.PK_ORG+" and monthstat.pk_psndoc="+TBMPsndocVO.PK_PSNDOC+" and monthstat."+MonthStatVO.TBMYEAR+
				"='"+year+"' and monthstat."+MonthStatVO.TBMMONTH+"='"+month+"' and isapprove in('y','Y'))");
		return new nc.ui.hr.pub.FromWhereSQL(fromWhereSQL.getFrom(), where, fromWhereSQL, null);
	}
	@Override
	public void generate(String pk_org, String[] pk_psndocs, String year,
			String month) throws BusinessException {
		if(ArrayUtils.isEmpty(pk_psndocs))
			return;
		FromWhereSQL fromWhereSQL =null;
		try{
			fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
			generate(pk_org, fromWhereSQL, year, month);
		}
		finally{
			if(fromWhereSQL!=null)
				TBMPsndocSqlPiecer.clearQuerySQL(fromWhereSQL);
		}

	}

	protected void generate0(String pk_org, String[] pk_psndocs, PeriodVO curPeriod) throws BusinessException {
		//Java类计算类型项目的计算参数
		MonthCalParam monthCalParam = MonthStatCalculationHelper.createPara(pk_org,curPeriod);
		InSQLCreator isc = new InSQLCreator();
		try{
			//准备计算参数
			CalSumParam calSumParam = prepareCalSumParam(pk_org, pk_psndocs, curPeriod,isc,monthCalParam);
			//计算单据时长
			processBills(calSumParam);
			//计算出勤时长
			processWorkDaysHours(calSumParam);
//			//计算月报项目
			try {
				processItems(calSumParam);
			} catch (DbException e) {
				Logger.error(e.getMessage(), e);
				throw new BusinessException(e.getMessage(), e);
			}

			//月报生成结束时对存在考勤异常的人员发送提醒消息,写日志
			afterGenerate(pk_org, pk_psndocs, curPeriod, monthCalParam,
					calSumParam);
		}
		finally{
			isc.clear();
		}

	}

	/** 月报生产效率优化，另起线程处理后续业务
	 * @param pk_org
	 * @param pk_psndocs
	 * @param curPeriod
	 * @param monthCalParam
	 * @param calSumParam
	 * @throws BusinessException
	 */
	private void afterGenerate(final String pk_org, final String[] pk_psndocs,
			final PeriodVO curPeriod, final MonthCalParam monthCalParam,
			final CalSumParam calSumParam) throws BusinessException {

		final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();
		new Executor(new Runnable() {
			@Override
			public void run() {
				BDDistTokenUtil.setInvocationInfo(invocationInfo);
				try {
					if(monthCalParam.timeruleVO.hasExceptionnoticeset()&&isHrssStarted()){
						sendMessage(pk_org, pk_psndocs, curPeriod, calSumParam);
					}
					TaBusilogUtil.writeMonthStatGenerate(pk_org, pk_psndocs, curPeriod.getBegindate(),curPeriod.getEnddate());
				} catch (BusinessException e) {
					Logger.error(" daystat generate error for sendMessge "+e.getMessage(), e);
				}
			}
		}).start();
	}

	private Boolean isHrssStarted() {
		Boolean isstart=false;
		  isstart= PubEnv.isModuleStarted(PubEnv.getPk_group(),PubEnv.MODULE_HRES);
		//修改说明： 应该使用上面的代码，但是修改了 ICommonConst.MODULECODE_HRSS的补丁不起作用，只好暂时使用下面的代码
		//注意v63要换回来使用上面的代码
		//判断自助模块是否启用
//			isstart= PubEnv.isModuleStarted(PubEnv.getPk_group(),"E202");
		return isstart;
		 }


	@SuppressWarnings("unchecked")
	private void processBills(CalSumParam calSumParam) throws DAOException{
		PeriodVO periodVO = calSumParam.periodVO;
		String year = periodVO.getTimeyear();
		String month = periodVO.getTimemonth();
		String pk_org = calSumParam.pk_org;
		String psndocInSQL = calSumParam.psndocInSQL;
		String sql = "select sum(hournum) as hournum,sum(toresthour) as toresthour,pk_timeitem,tbm_daystat.pk_psndoc ,type, " +
		"(select top 1 pk_monthstat from tbm_monthstat where tbm_monthstat.pk_psndoc=tbm_daystat.pk_psndoc " +
		"and tbm_monthstat.pk_org=tbm_daystat.pk_org and tbmyear='"+year+"' and tbmmonth='"+month+"')" +
		" as pk_monthstat,0 as dr from tbm_daystatb "+
		"inner join tbm_daystat on tbm_daystat.pk_daystat=tbm_daystatb.pk_daystat " +
		"where tbm_daystat.pk_org='"+pk_org+"' and tbm_daystat.pk_psndoc in("+
		psndocInSQL+") and  ((tbm_daystat.calendar between '"+periodVO.getBegindate()+"' and '"+periodVO.getEnddate()+"' "+
		"and isnull(tbm_daystatb.calcperiod,'1')='1') or tbm_daystatb.calcperiod='"+year+"'||'"+month+"')"+
		"group by tbm_daystat.pk_org,tbm_daystat.pk_psndoc,pk_timeitem,type ";

		String delSql = "delete from tbm_monthstatb where pk_monthstat in (select pk_monthstat from tbm_monthstat " +
		"where pk_org='"+pk_org+"' and pk_psndoc in("+psndocInSQL+") and tbmyear='"+year+"' and tbmmonth='"+month+"')";
		BaseDAO dao = new BaseDAO();
		//先删除子表记录
		dao.executeUpdate(delSql);
		MonthStatbVO[] vos = CommonUtils.toArray(MonthStatbVO.class, (Collection<MonthStatbVO>)dao.executeQuery(sql, new BeanListProcessor(MonthStatbVO.class)));
		if(ArrayUtils.isEmpty(vos))
			return;
		List<MonthStatbVO> insertList = new ArrayList<MonthStatbVO>();
		String pk_group = periodVO.getPk_group();
		Map<String, TimeItemCopyVO> allTypeMap = new HashMap<String, TimeItemCopyVO>();
		if(!ArrayUtils.isEmpty(calSumParam.leaveCopyVOs)){
			Map<String, LeaveTypeCopyVO> map = CommonUtils.toMap(TimeItemCopyVO.PK_TIMEITEM, calSumParam.leaveCopyVOs);
			allTypeMap.putAll(map);
		}
		if(!ArrayUtils.isEmpty(calSumParam.awayCopyVOs)){
			Map<String, AwayTypeCopyVO> map = CommonUtils.toMap(TimeItemCopyVO.PK_TIMEITEM, calSumParam.awayCopyVOs);
			allTypeMap.putAll(map);
		}
		if(!ArrayUtils.isEmpty(calSumParam.overCopyVOs)){
			Map<String, OverTimeTypeCopyVO> map = CommonUtils.toMap(TimeItemCopyVO.PK_TIMEITEM, calSumParam.overCopyVOs);
			allTypeMap.putAll(map);
		}
		if(!ArrayUtils.isEmpty(calSumParam.shutCopyVOs)){
			Map<String, ShutDownTypeCopyVO> map = CommonUtils.toMap(TimeItemCopyVO.PK_TIMEITEM, calSumParam.shutCopyVOs);
			allTypeMap.putAll(map);
		}
		for(MonthStatbVO vo:vos){
			if(StringUtils.isNotBlank(vo.getPk_monthstat()) &&
					((vo.getHournum()!=null&&vo.getHournum().doubleValue()>0) ||
					(vo.getToresthour()!=null&&vo.getToresthour().doubleValue()>0))){
				insertList.add(vo);
				vo.setPk_group(pk_group);
				vo.setPk_org(pk_org);
				TimeItemCopyVO typeVO = allTypeMap.get(vo.getPk_timeitem());
				vo.setTimeitemunit(typeVO!=null?typeVO.getTimeitemunit():TimeItemCopyVO.TIMEITEMUNIT_DAY);
			}
		}
		//再插入子表记录
		if(insertList.size()>0)
			dao.insertVOList(insertList);
	}

//	@SuppressWarnings("unchecked")
//	private void processWorkDaysHours(CalSumParam calSumParam) throws DAOException{
//		PeriodVO periodVO = calSumParam.periodVO;
//		String year = periodVO.getTimeyear();
//		String month = periodVO.getTimemonth();
//		String pk_org = calSumParam.pk_org;
//		String psndocInSQL = calSumParam.psndocInSQL;
//		int decimalDigits = calSumParam.timeRuleVO.getMreportdecimal().intValue();
//
//		String delSql = "delete from tbm_monthwork where pk_monthstat in (select pk_monthstat from tbm_monthstat " +
//		"where pk_org='"+pk_org+"' and pk_psndoc in("+psndocInSQL+") and tbmyear='"+year+"' and tbmmonth='"+month+"')";
//
//		//先构建一个临时表，临时表中是一人一天一条记录，字段分别记录了此人此天的班次主键，班次时长，工作日历时长，实际出勤时长等信息，然后在此临时表的基础上，再进一步汇总得到应出勤班数等信息
//		//此临时表只有机器考勤的天(机器考勤与手工考勤分开统计的原因是，两种情况下实际出勤时长的逻辑不一样：对于机器考勤，timedata表中已经算好了，
//		//而手工考勤还需要在这里现算)
//		String tempTableSQLMachine =
//			"select "+
////			" psncalendar.pk_org,"+//组织主键
//			" '"+pk_org+"' as pk_org, "+//组织主键
//			" psncalendar.pk_shift as pk_shift,"+//班次主键
//			" psncalendar.pk_psndoc,"+//人员主键
//			" shift.gzsj as shiftgzsj,"+//班次时长
//			" psncalendar.gzsj as calendargzsj,"+//工作日历时长
//			" timedata.worklength,"+//实际出勤时长
//			" psncalendar.calendar as calendar"+//日期
//			" from tbm_psncalendar psncalendar"+
//			" inner join bd_shift shift on shift.pk_shift=psncalendar.pk_shift "+
//			" left join tbm_timedata timedata on " +
//			//" timedata.pk_org=psncalendar.pk_org and " +这一段在V6.1中去掉，因为61中工作日历已不是HR组织级档案，改为业务单元级档案
//			" timedata.pk_psndoc=psncalendar.pk_psndoc  and timedata.calendar=psncalendar.calendar "+
//			" where " +
////			" psncalendar.pk_org='"+pk_org+"' and " +这一段在V6.1中去掉，因为61中工作日历已不是HR组织级档案，改为业务单元级档案
//			" psncalendar.pk_psndoc in("+calSumParam.psndocInSQL+") "+
//			" and psncalendar.pk_shift<>'"+ShiftVO.PK_GX+"' and psncalendar.calendar between '"+
//			periodVO.getBegindate()+"' and '"+periodVO.getEnddate()+"' "+
//			//下面这个exist的作用是将psncalendar中可能存在的垃圾数据排除掉（即psncalendar中有，但是考勤档案无效的天的数据），以免影响统计结果的正确性
//			" and exists (select 1 from tbm_psndoc psndoc where " +
////			" psndoc.pk_org=psncalendar.pk_org and " +这一段在V6.1中去掉，因为61中工作日历已不是HR组织级档案，改为业务单元级档案
//			" psndoc.pk_psndoc=psncalendar.pk_psndoc and psndoc.pk_org='"+pk_org+"' and psndoc.tbm_prop="+TBMPsndocVO.TBM_PROP_MACHINE+" and "+
//			" psncalendar.calendar between psndoc.begindate and psndoc.enddate)";
//		//再构建另外一个临时表，也是一人一天一条记录，结构与上面的临时表完全相同，只不过只统计手工考勤的天
//		String manualMinus = "";//手工考勤的实际工作时长的减项
//		Map<String, String> workLenMinusItemMap = TimeDataCalRuleConfig.parseMinusItem(calSumParam.timeRuleVO.getWorklenminusitems());
//		if(!MapUtils.isEmpty(workLenMinusItemMap)){
//			if(workLenMinusItemMap.containsKey(TimeDataCalRuleConfig.WORKLENMINUSITEM_LATE)){
//				manualMinus+="-isnull(lateearly.latelength,0)";
//			}
//			if(workLenMinusItemMap.containsKey(TimeDataCalRuleConfig.WORKLENMINUSITEM_EARLY)){
//				manualMinus+="-isnull(lateearly.earlylength,0)";
//			}
//			if(workLenMinusItemMap.containsKey(TimeDataCalRuleConfig.WORKLENMINUSITEM_ABSENT)){
//				manualMinus+="-isnull(lateearly.absenthour*60,0)";
//			}
//			String orihounumSQL = "-isnull((select sum(orihournumusehour)*60 from tbm_daystatb daystatb where daystatb.pk_daystat=(select pk_daystat from tbm_daystat daystat where daystat.pk_psndoc=psncalendar.pk_psndoc and daystat.calendar=psncalendar.calendar) and type={0}),0)";
//			if(workLenMinusItemMap.containsKey(TimeDataCalRuleConfig.WORKLENMINUSITEM_LEAVE)){
//				manualMinus+=MessageFormat.format(orihounumSQL, 0);
//			}
//			if(workLenMinusItemMap.containsKey(TimeDataCalRuleConfig.WORKLENMINUSITEM_AWAY)){
//				manualMinus+=MessageFormat.format(orihounumSQL, 2);
//			}
//			if(workLenMinusItemMap.containsKey(TimeDataCalRuleConfig.WORKLENMINUSITEM_SHUTDOWN)){
//				manualMinus+=MessageFormat.format(orihounumSQL, 3);
//			}
//			if(workLenMinusItemMap.containsKey(TimeDataCalRuleConfig.WORKLENMINUSITEM_LACTATION)){
//				manualMinus+="-isnull((select top 1 lactationhour*60 from tbm_leavereg leavereg where leavereg.pk_psndoc=psncalendar.pk_psndoc and psncalendar.calendar between leavereg.leavebegindate and leavereg.leaveenddate and islactation='Y'),0)";
//			}
//		}
//		String tempTableSQLManual =
//			"select "+
//			" '"+pk_org+"' as pk_org, "+//组织主键
//			" psncalendar.pk_shift as pk_shift,"+//班次主键
//			" psncalendar.pk_psndoc,"+//人员主键
//			" shift.gzsj as shiftgzsj,"+//班次时长
//			" psncalendar.gzsj as calendargzsj,"+//工作日历时长
//			" (psncalendar.gzsj*60"+manualMinus+")*60.0 as worklength,"+//实际出勤时长
//			" psncalendar.calendar as calendar"+//日期
//			" from tbm_psncalendar psncalendar"+
//			" inner join bd_shift shift on shift.pk_shift=psncalendar.pk_shift "+
//			" left join tbm_lateearly lateearly on " +
//			" lateearly.pk_psndoc=psncalendar.pk_psndoc  and lateearly.calendar=psncalendar.calendar "+
//			" where " +
//			" psncalendar.pk_psndoc in("+calSumParam.psndocInSQL+") "+
//			" and psncalendar.pk_shift<>'"+ShiftVO.PK_GX+"' and psncalendar.calendar between '"+
//			periodVO.getBegindate()+"' and '"+periodVO.getEnddate()+"' "+
//			//下面这个exist的作用是将psncalendar中可能存在的垃圾数据排除掉（即psncalendar中有，但是考勤档案无效的天的数据），以免影响统计结果的正确性
//			" and exists (select 1 from tbm_psndoc psndoc where " +
//			" psndoc.pk_psndoc=psncalendar.pk_psndoc and psndoc.pk_org='"+pk_org+"' and psndoc.tbm_prop="+TBMPsndocVO.TBM_PROP_MANUAL+" and "+
//			" psncalendar.calendar between psndoc.begindate and psndoc.enddate)";
//		//下面从临时明细表中查询出当月的出勤状况的统计信息
//		String sql =
//			"select round((sum(calendargzsj)*count(1)/sum(shiftgzsj)),"+decimalDigits+") as workdays,"+//应出勤班数workdays
//			"round(sum(calendargzsj),"+decimalDigits+") as workhours," +                              //应出勤工时workhours
//			"round(sum(case when worklength<0 then 0 else worklength end)/3600.0,"+decimalDigits+") as actualworkhours,"+                        //出勤工时actualworkhours
//			//"round((sum(worklength)*count(1)/sum(shiftgzsj))/86400.0,"+decimalDigits+") as actualworkdays,"+          //出勤班数actualworkdays
//			"round((sum(worklength)*count(1)/sum(shiftgzsj))/3600.0,"+decimalDigits+") as actualworkdays,"+          //出勤班数actualworkdays
//
//			"pk_shift,(select top 1 pk_monthstat from tbm_monthstat " +
//			"where tbm_monthstat.pk_org=t.pk_org and tbm_monthstat.pk_psndoc = t.pk_psndoc " +
//			"and tbmyear='"+year+"' and tbmmonth='"+month+"') as pk_monthstat,0 as dr,pk_org from("
//			+tempTableSQLMachine+" union "+tempTableSQLManual+")as t group by pk_org,pk_psndoc,pk_shift";
//
//		BaseDAO dao = new BaseDAO();
//		//先删除子表记录
//		dao.executeUpdate(delSql);
//		//然后插入
//		MonthWorkVO[] vos = CommonUtils.toArray(MonthWorkVO.class, (Collection<MonthWorkVO>)dao.executeQuery(sql, new BeanListProcessor(MonthWorkVO.class)));
//		if(ArrayUtils.isEmpty(vos))
//			return;
//		String pk_group= periodVO.getPk_group();
//		for(MonthWorkVO vo:vos){
//			vo.setPk_group(pk_group);
//			vo.setPk_org(pk_org);
//			if(vo.getActualworkdays()==null||vo.getActualworkdays().doubleValue()<0)
//				vo.setActualworkdays(UFDouble.ZERO_DBL);
//			if(vo.getActualworkhours()==null||vo.getActualworkhours().doubleValue()<0)
//				vo.setActualworkhours(UFDouble.ZERO_DBL);
//		}
//		dao.insertVOArray(vos);
//	}

	private void processWorkDaysHours(CalSumParam calSumParam) throws DAOException{
		PeriodVO periodVO = calSumParam.periodVO;
		String year = periodVO.getTimeyear();
		String month = periodVO.getTimemonth();
		String pk_org = calSumParam.pk_org;
		String psndocInSQL = calSumParam.psndocInSQL;

		BaseDAO dao = new BaseDAO();
		String delSql = "delete from tbm_monthwork where pk_monthstat in (select pk_monthstat from tbm_monthstat " +
		"where pk_org='"+pk_org+"' and pk_psndoc in("+psndocInSQL+") and tbmyear='"+year+"' and tbmmonth='"+month+"')";
		//先删除子表记录
		dao.executeUpdate(delSql);
		//然后插入
		GeneralVO[] genVOs = new MonthStatDAO().queryWorkDaysHours(pk_org, psndocInSQL, periodVO, calSumParam.timeRuleVO);
		if(ArrayUtils.isEmpty(genVOs))
			return;
		String pk_group= periodVO.getPk_group();
		List<MonthWorkVO> result = new ArrayList<MonthWorkVO>();
		for(GeneralVO genVO:genVOs){
			MonthWorkVO vo = new MonthWorkVO();
			vo.setPk_group(pk_group);
			vo.setPk_org(pk_org);
			UFDouble actualWorkDays = objectToUFDouble(genVO.getAttributeValue(MonthWorkVO.ACTUALWORKDAYS));
			UFDouble actualWorkHours = objectToUFDouble(genVO.getAttributeValue(MonthWorkVO.ACTUALWORKHOURS));
			vo.setActualworkdays(actualWorkDays.doubleValue()<0 ? UFDouble.ZERO_DBL:actualWorkDays);
			vo.setActualworkhours(actualWorkHours.doubleValue()<0 ? UFDouble.ZERO_DBL:actualWorkHours);
			vo.setWorkdays(objectToUFDouble(genVO.getAttributeValue(MonthWorkVO.WORKDAYS)));
			vo.setWorkhours(objectToUFDouble(genVO.getAttributeValue(MonthWorkVO.WORKHOURS)));
			vo.setPk_shift((String) genVO.getAttributeValue(MonthWorkVO.PK_SHIFT));
			vo.setPk_monthstat((String) genVO.getAttributeValue(MonthStatVO.PK_MONTHSTAT));
			vo.setDr(0);
			result.add(vo);
		}
		dao.insertVOArray(result.toArray(new MonthWorkVO[0]));
	}

	private UFDouble objectToUFDouble(Object obj){
		if(obj==null)
			return UFDouble.ZERO_DBL;
		return new UFDouble(String.valueOf(obj));
	}

	/**
	 * 准备参数，例如所有人的考勤项目等等
	 * @param pk_org
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	private CalSumParam prepareCalSumParam(String pk_org, String[] pk_psndocs,
			PeriodVO curPeriod,InSQLCreator isc,MonthCalParam monthCalParam) throws BusinessException{
		CalSumParam calSumParam = new CalSumParam();
		calSumParam.monthCalParam=monthCalParam;
		calSumParam.pk_org=pk_org;
		calSumParam.pk_psndocs=pk_psndocs;
		calSumParam.psndocInSQL=isc.getInSQL(pk_psndocs);
		calSumParam.periodVO=curPeriod;

		calSumParam.timeRuleVO = monthCalParam.timeruleVO;
		calSumParam.paramValues=monthCalParam.paramValues;
		if(monthCalParam.leaveItemMap!=null){
		calSumParam.leaveCopyVOs=monthCalParam.leaveItemMap.values().toArray(new LeaveTypeCopyVO[0]);
		}
		if(monthCalParam.awayItemMap!=null){
		calSumParam.awayCopyVOs=monthCalParam.awayItemMap.values().toArray(new AwayTypeCopyVO[0]);
		}
		if(monthCalParam.overtimeItemMap!=null){
		calSumParam.overCopyVOs=monthCalParam.overtimeItemMap.values().toArray(new OverTimeTypeCopyVO[0]);
		}
		if(monthCalParam.shutdownItemMap!=null){
		calSumParam.shutCopyVOs=monthCalParam.shutdownItemMap.values().toArray(new ShutDownTypeCopyVO[0]);
		}

		calSumParam.monthItemVOs=monthCalParam.itemVOs;
		// 考勤项目中是否存在java类的项目。如果不存在java类项目，则后面的一些操作可以不做，以提高计算效率
		boolean existsJavaItem=false;
		for (ItemCopyVO itemVO : calSumParam.monthItemVOs) {
			if (itemVO.getSrc_flag().intValue() == ItemCopyVO.SRC_FLAG_JAVA) {
				existsJavaItem=true;
				break;
			}
		}
		// 所有人员的日报的主表主键,第一个string是人员主键pk_psndoc,第二个string是日期，第三个string是pk_daystat。这个map的主要作用是为后面的daystatb子表生成做准备
		// Logger.error("查询日报主键map开始："+System.currentTimeMillis());
		long time = System.currentTimeMillis();
//		calSumParam.daystatPKMap = DayStatCalculationHelper.getDaystatPKMap(calSumParam.pk_org, calSumParam.psndocInSQL,
//				beginDate.toString(), endDate.toString());
//		Logger.debug("构造日报主键map耗时：" + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
//		// 所有人员的工作日历map,要求往前往后都推2天(即用户如果选择计算10号到11号的日报，那么此处应该查询8号到13号的工作日历),传进来的dateArray已经是比日报生成范围要往前往后两天了：
//		// 之所以往前后推这么多，是因为计算加班单的长度，在单子很长的时候，可能会用到很多天的工作日历，很有可能大大超过用户输入的日期范围
//		calSumParam.calendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).
//			queryCalendarVOByPsnInSQL(calSumParam.pk_org, beginDate.getDateBefore(1), endDate.getDateAfter(1), calSumParam.psndocInSQL);
		time = System.currentTimeMillis();
		// Logger.error("查询工作日历结束："+System.currentTimeMillis());
//		calSumParam.aggBclbMap=NCLocator.getInstance().lookup(IBclbQueryService.class).queryBclbAggVOMap(pk_org);
//		if(!MapUtils.isEmpty(calSumParam.aggBclbMap)){
//			calSumParam.bclbMap=new HashMap<String, BclbVO>();
//			Iterator<String> keyItrt = calSumParam.aggBclbMap.keySet().iterator();
//			while(keyItrt.hasNext()){
//				String key = keyItrt.next();
//				calSumParam.bclbMap.put(key, calSumParam.aggBclbMap.get(key).getBclbVO());
//			}
//		}
		time = System.currentTimeMillis();
		if (existsJavaItem) {
//			// 下面的这些map只在java项目计算时有用，因此只在存在java项目的时候初始化
//			// 所有人员的timedata数据,第一个string是人员主键，第二个UFLiteralDate是日期，value是timedatavo
//			calSumParam.timedataMap = NCLocator.getInstance().lookup(ITimeDataQueryService.class).
//				queryVOMapByPsndocInSQL(calSumParam.pk_org, beginDate,endDate,calSumParam.psndocInSQL);
//			// 所有人员的lateearly数据
//			calSumParam.lateearlyMap = NCLocator.getInstance().lookup(ILateEarlyQueryService.class).
//				queryVOMapByPsndocInSQL(calSumParam.pk_org, beginDate,endDate,calSumParam.psndocInSQL);
//			// 所有人员的考勤档案数据,key是人员主键，value是在这段时间内的考勤档案vo(一般情况下只有一条。如果有多条，那么已经按时间先后排好序)
//			calSumParam.tbmPsndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).
//				queryTBMPsndocMapByPsndocInSQL(calSumParam.pk_org,calSumParam.psndocInSQL,beginDate,endDate, true);
			//获取java类项目的计算类实例
			IDevItfQueryService service = NCLocator.getInstance().lookup(IDevItfQueryService.class);
			calSumParam.dataCreatorMap=new HashMap<String, IMonthDataCreator>();
			for(ItemCopyVO vo:calSumParam.monthItemVOs){
				if(vo.getSrc_flag()==ItemVO.SRC_FLAG_JAVA){
					IMonthDataCreator creator = (IMonthDataCreator) service.queryByCodeAndObj(ICommonConst.ITF_CODE_MONTH, vo.getPrimaryKey());
					calSumParam.dataCreatorMap.put(vo.getItem_code(), creator);
				}
			}
		}
		Logger.debug("查询timedata、lateearly、考勤档案耗时：" + (System.currentTimeMillis() - time));
		// 取得所有人员、所有日期（startDate的前两天至endDate的后两天）的单据数据
		// key是人员主键，value是人员的休假单子表数组，下面的加班单出差单停工单都一样
		time = System.currentTimeMillis();
		// 一个工作日的时长,在考勤规则中定义
		calSumParam.workDayLength = calSumParam.timeRuleVO == null ? 8 : calSumParam.timeRuleVO.getDaytohour().doubleValue();
		return calSumParam;
	}
	@SuppressWarnings("unchecked")
	@Override
	public MonthStatVO[] save(String pk_org,MonthStatVO[] vos) throws BusinessException {
		try {
			if(ArrayUtils.isEmpty(vos))
				return null;

			//封存期间的数据不允许修改
			IPeriodQueryService periodServer = NCLocator.getInstance().lookup(IPeriodQueryService.class);
			PeriodVO period = periodServer.queryByYearMonth(pk_org, vos[0].getTbmyear(),vos[0].getTbmmonth());
			periodServer.checkDateB4Modify(pk_org, CommonUtils.createDateArray(period.getBegindate(),period.getEnddate()));

			BaseDAO baseDAO = new BaseDAO();
			String tbmYear = vos[0].getTbmyear();
			String tbmMonth = vos[0].getTbmmonth();
			String checkCondition = " pk_org = '" + vos[0].getPk_org() + "' and timeyear = '" + tbmYear + "' and timemonth = '" + tbmMonth + "' and sealflag  = 'Y'";
			Collection periods = baseDAO.retrieveByClause(PeriodVO.class, checkCondition);
			if(CollectionUtils.isNotEmpty(periods)){
				throw new BusinessException(ResHelper.getString("6017hrta","06017hrta0103")
/*@res "考勤期间已封存，数据不能修改！"*/);
			}
			MonthStatVO[] oldvos = null;
			InSQLCreator isc = new InSQLCreator();
			String condition = MonthStatVO.PK_MONTHSTAT + " in (" + isc.getInSQL(vos,MonthStatVO.PK_MONTHSTAT) + ") ";
			Collection oldc = baseDAO.retrieveByClause(MonthStatVO.class, condition);
			if(CollectionUtils.isNotEmpty(oldc))
				oldvos = (MonthStatVO[]) oldc.toArray(new MonthStatVO[0]);
			new MonthStatDAO().save(pk_org, vos);
			//业务日志
			TaBusilogUtil.writeMonthStatEditBusiLog(vos,oldvos);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		return vos;
	}

	@Override
	public MonthStatVO[] unApprove(String pk_org,String year, String month, MonthStatVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
		PeriodVO currPeriodVO =	PeriodServiceFacade.checkCurPeriod(pk_org, year, month);
		MonthStatVO[] retvos = vos;
		List<String> pks = new ArrayList<String>();
		for(int i=0;i<retvos.length;i++){
			pks.add(retvos[i].getPk_monthstat());
			retvos[i].setIsapprove(UFBoolean.FALSE);
		}
		InSQLCreator isc = new InSQLCreator();
		try{
			String sql = "update tbm_monthstat set isapprove = 'N',isuseful='N' where pk_monthstat in("+isc.getInSQL(pks.toArray(new String[0]))+")";
			new BaseDAO().executeUpdate(sql);
			
			
			//MOD James
			sql="update tbm_leavereg set waperiod=null where pk_org='"+pk_org+"' and ((approve_time>='"+
			currPeriodVO.getBegindate()+"' and approve_time<'"+currPeriodVO.getEnddate().getDateAfter(1)+
			"' and leavebegindate<'"+currPeriodVO.getEnddate().getDateAfter(1)+"') or ( leavebegindate<'"+currPeriodVO.getEnddate().getDateAfter(1)+
			"' and leavebegindate>='"+currPeriodVO.getBegindate()+"' and approve_time<'"+currPeriodVO.getEnddate().getDateAfter(1)+
					"' )) and dr=0 and pk_psndoc in(select pk_psndoc from tbm_monthstat  where pk_monthstat in("+isc.getInSQL(pks.toArray(new String[0]))+"))";
			new BaseDAO().executeUpdate(sql);
			
			sql="update tbm_overtimereg set waperiod=null where pk_org='"+pk_org+"' and approve_time>='"+
					currPeriodVO.getBegindate()+"' and approve_time<'"+currPeriodVO.getEnddate().getDateAfter(1)+
					"' and overtimebegindate<'"+currPeriodVO.getEnddate().getDateAfter(1)+"' and dr=0 and pk_psndoc in(select pk_psndoc from tbm_monthstat  where pk_monthstat in("+isc.getInSQL(pks.toArray(new String[0]))+"))";
					new BaseDAO().executeUpdate(sql);
		}finally{
			isc.clear();
		}

		return retvos;
	}
	/**
	 * @param pk_org
	 * @param fromWhereSQL
	 * @param year
	 * @param month
	 * @param showNoDataRecord
	 * @param limitPsnByHROrg，是否根据hr组织过滤人员。对于业务节点的查询，要求根据HR组织来过滤，但是对于经理自助，目前要求不按HR组织过滤
	 * @return
	 * @throws BusinessException
	 */
	protected MonthStatVO[] queryByCondition(String pk_org,
			FromWhereSQL fromWhereSQL, String year, String month,
			boolean showNoDataRecord) throws BusinessException {
		//首先查询期间的起止日期
		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
		if(periodVO==null)
			throw new BusinessException(ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0090"
					 /*@res "期间{0}不存在!"*/, year+"-"+month));
//		UFLiteralDate beginDate = periodVO.getBegindate();
//		UFLiteralDate endDate = periodVO.getEnddate();
		//查询权限
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
//		//首先，查询考勤档案pk_org就是当前pk_org的人员
//		TBMPsndocVO[] psndocVOsInOrg = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).
//			queryLatestByCondition(pk_org, fromWhereSQL, beginDate,endDate);
		//然后，查询考勤档案pk_adminorg就是当前pk_org的人员，即考勤档案不在本组织，但管理组织在本组织的人员
//		TBMPsndocVO[] psndocVOsInAdminOrg = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).
//			queryLatestByCondition2(pk_org, fromWhereSQL, beginDate,endDate);
//		if(ArrayUtils.isEmpty(psndocVOsInOrg)&&ArrayUtils.isEmpty(psndocVOsInAdminOrg))
//			return null;
		//查询这些人员已有的月报记录
//		InSQLCreator isc = new InSQLCreator();
//		try{
//			SQLParameter para = new SQLParameter();
//			para.addParam(pk_org);
//			para.addParam(year);
//			para.addParam(month);
////			MonthStatVO[] dbVOsInOrg = null;
//			MonthStatVO[] dbVOsInAdminOrg = null;


//			if(!ArrayUtils.isEmpty(psndocVOsInOrg)){
//				String cond = MonthStatVO.PK_ORG+"=? and "+MonthStatVO.PK_PSNDOC+" in("+isc.getInSQL(psndocVOsInOrg, MonthStatVO.PK_PSNDOC)+") and "+
//					MonthStatVO.TBMYEAR+"=? and "+MonthStatVO.TBMMONTH+"=? ";
//				dbVOsInOrg = new MonthStatDAO().query(pk_org, MonthStatVO.class,
//						new String[]{MonthStatVO.PK_PSNDOC,MonthStatVO.TBMYEAR,MonthStatVO.TBMMONTH
//					,MonthStatVO.PK_GROUP,MonthStatVO.PK_ORG,MonthStatVO.ISAPPROVE},
//						cond, null,para);
//			}

//			if(!ArrayUtils.isEmpty(psndocVOsInAdminOrg)){
//				String cond = MonthStatVO.PK_ORG+"<>? and "+MonthStatVO.PK_PSNDOC+" in("+isc.getInSQL(psndocVOsInAdminOrg, MonthStatVO.PK_PSNDOC)+") and "+
//				MonthStatVO.TBMYEAR+"=? and "+MonthStatVO.TBMMONTH+"=? ";
//				dbVOsInAdminOrg = new MonthStatDAO().query(pk_org, MonthStatVO.class,
//						new String[]{MonthStatVO.PK_PSNDOC,MonthStatVO.TBMYEAR,MonthStatVO.TBMMONTH
//					,MonthStatVO.PK_GROUP,MonthStatVO.PK_ORG,MonthStatVO.ISAPPROVE},
//						cond, null,para);
//			}
//			//本组织的月报记录
//			MonthStatVO[] vos1 =  processDBVOs(psndocVOsInOrg, dbVOsInOrg, year,month, showNoDataRecord,endDate);
//			//管理组织在本组织的月报记录
//			MonthStatVO[] vos2 =  processDBVOs(psndocVOsInAdminOrg, dbVOsInAdminOrg, year,month, showNoDataRecord,endDate);
//			return (MonthStatVO[])ArrayUtils.addAll(vos1, vos2);
//		} catch (DbException e) {
//			Logger.error(e.getMessage(), e);
//			throw new BusinessRuntimeException(e.getMessage(), e);
//		} catch (ClassNotFoundException e) {
//			Logger.error(e.getMessage(), e);
//			throw new BusinessRuntimeException(e.getMessage(), e);
//		}
		//本组织的月报记录
		MonthStatVO[] vos1 =  queryByConditionAndOrg(pk_org, periodVO, fromWhereSQL, showNoDataRecord);
		//管理组织在本组织的月报记录
		MonthStatVO[] vos2= queryByConditionAndAndminorg(pk_org, periodVO, fromWhereSQL, showNoDataRecord);
		return (MonthStatVO[])ArrayUtils.addAll(vos1, vos2);
	}

	/**
	 * 查询本组织的考勤月报
	 * @param pk_org
	 * @param periodVO
	 * @param fromWhereSQL
	 * @param showNoDataRecord
	 * @return
	 * @throws BusinessException
	 */
	private MonthStatVO[] queryByConditionAndOrg(String pk_org,PeriodVO periodVO,FromWhereSQL fromWhereSQL,boolean showNoDataRecord)throws BusinessException {
		UFLiteralDate beginDate = periodVO.getBegindate();
		UFLiteralDate endDate = periodVO.getEnddate();
		TBMPsndocVO[] psndocVOsInOrg = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).
		queryLatestByCondition(pk_org, fromWhereSQL, beginDate,endDate);
		if(ArrayUtils.isEmpty(psndocVOsInOrg))
			return null;
		//查询这些人员已有的月报记录
		InSQLCreator isc = new InSQLCreator();
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(periodVO.getTimeyear());
		para.addParam(periodVO.getTimemonth());
		MonthStatVO[] dbVOsInOrg = null;
		String cond = MonthStatVO.PK_ORG+"=? and "+MonthStatVO.PK_PSNDOC+" in("+isc.getInSQL(psndocVOsInOrg, MonthStatVO.PK_PSNDOC)+") and "+
		MonthStatVO.TBMYEAR+"=? and "+MonthStatVO.TBMMONTH+"=? ";
		try {
			dbVOsInOrg = new MonthStatDAO().query(pk_org, MonthStatVO.class,
					new String[]{MonthStatVO.PK_PSNDOC,MonthStatVO.TBMYEAR,MonthStatVO.TBMMONTH
				,MonthStatVO.PK_GROUP,MonthStatVO.PK_ORG,MonthStatVO.ISAPPROVE},
				cond, null,para);
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
		return processDBVOs(psndocVOsInOrg, dbVOsInOrg, periodVO.getTimeyear(),periodVO.getTimemonth(), showNoDataRecord,endDate);
	}

	/**
	 * 查询管理组织是本组织的考勤月报
	 * @param pk_org
	 * @param periodVO
	 * @param fromWhereSQL
	 * @param showNoDataRecord
	 * @return
	 * @throws BusinessException
	 */
	private MonthStatVO[] queryByConditionAndAndminorg(String pk_org,PeriodVO periodVO,FromWhereSQL fromWhereSQL,boolean showNoDataRecord)throws BusinessException {
		UFLiteralDate beginDate = periodVO.getBegindate();
		UFLiteralDate endDate = periodVO.getEnddate();
		TBMPsndocVO[] psndocVOsInAdminOrg = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).
						queryLatestByCondition2(pk_org, fromWhereSQL, beginDate,endDate);
		if(ArrayUtils.isEmpty(psndocVOsInAdminOrg))
			return null;
		//查询这些人员已有的月报记录
		InSQLCreator isc = new InSQLCreator();
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(periodVO.getTimeyear());
		para.addParam(periodVO.getTimemonth());
		String cond = MonthStatVO.PK_ORG+"<>? and "+MonthStatVO.PK_PSNDOC+" in("+isc.getInSQL(psndocVOsInAdminOrg, MonthStatVO.PK_PSNDOC)+") and "+
		MonthStatVO.TBMYEAR+"=? and "+MonthStatVO.TBMMONTH+"=? ";
		MonthStatVO[] dbVOsInAdminOrg=null;
		try {
			dbVOsInAdminOrg = new MonthStatDAO().query(pk_org, MonthStatVO.class,
					new String[]{MonthStatVO.PK_PSNDOC,MonthStatVO.TBMYEAR,MonthStatVO.TBMMONTH
				,MonthStatVO.PK_GROUP,MonthStatVO.PK_ORG,MonthStatVO.ISAPPROVE},
					cond, null,para);
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
		//管理组织在本组织的月报记录
		return processDBVOs(psndocVOsInAdminOrg, dbVOsInAdminOrg, periodVO.getTimeyear(),periodVO.getTimemonth(), showNoDataRecord,endDate);
	}


	@Override
	public MonthStatVO[] queryByCondition(LoginContext context,
			FromWhereSQL fromWhereSQL, String year, String month,
			boolean showNoDataRecord) throws BusinessException {
		return queryByCondition(context.getPk_org(), fromWhereSQL, year, month, showNoDataRecord);
	}

	/**
	 * 处理月报项目的计算，包括公式、java自定义
	 * @param calSumParam
	 * @throws DAOException
	 * @throws DbException
	 */
	private void processItems(CalSumParam calSumParam) throws DAOException, DbException{
		// 存储日报项目值的map，因为java类计算的项目如果依赖于其他项目的值的话，需要从内存中取。月报项目可能依赖于日报项目
		// 第一个string是itemcode，第二个string是pk_psndoc,第三个string是date，Object是项目的值
//		Map<String, Map<String, Map<String, Object>>> dayItemValueMap = new HashMap<String, Map<String, Map<String, Object>>>();
		// 存放月报项目值的map，，因为java类计算的项目如果依赖于其他项目的值的话，需要从内存中取。
		// 第一个String是itemcode，第二个string是pk_psndoc,Object是项目的值
//		Map<String, Map<String, Object>> monthItemValueMap = new HashMap<String, Map<String, Object>>();
		ItemCopyVO[] itemVOs = calSumParam.monthItemVOs;
		String pk_org = calSumParam.pk_org;
		String psndocInSQL = calSumParam.psndocInSQL;
		PeriodVO periodVO = calSumParam.periodVO;
		BaseDAO dao = new BaseDAO();
//		String[] pk_psndocs = calSumParam.pk_psndocs;
		String updateEffective = "update "+MonthStatVO.getDefaultTableName()+" set "+MonthStatVO.ISEFFECTIVE+"='Y' ";
		String where = " where pk_org='" + pk_org + "' and pk_psndoc in (" + psndocInSQL + ") " +
		"and tbmyear='" + periodVO.getTimeyear() + "' and tbmmonth='" + periodVO.getTimemonth() + "'";
		updateEffective+=where;
		dao.executeUpdate(updateEffective);
//		int psnCount = pk_psndocs.length;
//		MonthCalParam calPara = calSumParam.monthCalParam;
		// 按项目循环计算
		for (int itemIndex = 0; itemIndex < itemVOs.length; itemIndex++) {
			ItemCopyVO itemVO = itemVOs[itemIndex];
			// 如果是手工录入类型，则不用处理，直接continue
			if (itemVO.getSrc_flag().intValue() == ItemVO.SRC_FLAG_MANUAL) {
				continue;
			}
			// 如果是公式类型，则需要执行公式
			if (itemVO.getSrc_flag().intValue() == ItemVO.SRC_FLAG_FORMULA) {
				String updateSql = itemVO.getParsedFormula() + where;
				try {
					dao.executeUpdate(updateSql);
				} catch (Exception e) {
					throw new DAOException(ResHelper.getString("6017hrta","06017hrta0104")
/*@res "月报项目："*/ + itemVO.getMultilangName() + ResHelper.getString("6017hrta","06017hrta0105")
/*@res " 计算报错，请检查公式！"*/);
				}
				continue;
			}
			// 如果是java类计算，则要执行java类的计算
			// 从数据库中查询出此月报项目所依赖的月报项目和日报项目的值，放到dayItemValueMap和monthItemValueMap中
//			try {
//				calHelper.queryItemValuesForMonthJavaItem(pkCorp, psnInsql, year, month, beginDate, endDate, itemVO, dayItemValueMap, monthItemValueMap, calPara.code2DayItemVOMap, calPara.code2ItemVOMap);
//			} catch (SQLException e) {
//				Logger.error(e.getMessage(), e);
//				throw new BusinessException(e.getMessage(), e);
//			}
//			IMonthDataCreator creator = (IMonthDataCreator) itemVO.getImplObj();
//			ItemVO[] referencedDayItems = creator.getDependentDayItems(pkCorp, itemVO);
//			ItemVO[] referencedMonthItems = creator.getDependentItems(pkCorp, itemVO);
//			// 在itemvalue的map中给自己找一个位置：自己算完后就可以马上放回去
//			// key是人的主键，object是参数值
//			Map<String, Object> valueMap = null;
//			if (!monthItemValueMap.containsKey(itemVO.getItem_code())) {
//				valueMap = new HashMap<String, Object>();
//				monthItemValueMap.put(itemVO.getItem_code(), valueMap);
//			}
//			calPara.itemCode = itemVO.getItem_code();
//			// 按人进行循环计算
//			for (int psnIndex = 0; psnIndex < psnCount; psnIndex++) {
//				// 人员主键
//				String pk_psndoc = pk_psndocs[psnIndex];
//				calPara.pk_psndoc = pk_psndoc;
//				// 看是否需要其他月报项目的值，如果需要的话，需要从monthItemValueMap里面去取
//				if (referencedMonthItems != null && referencedMonthItems.length > 0) {
//					for (ItemVO referencedItemVO : referencedMonthItems) {
//						calPara.itemValues.put(referencedItemVO.getItem_code(), monthItemValueMap.get(referencedItemVO.getItem_code()).get(pk_psndoc));
//					}
//				}
//				// 看是否需要日报项目的值，如果需要的话，需要从里面去取
//				if (referencedDayItems != null && referencedDayItems.length > 0) {
//					for (ItemVO referencedItemVO : referencedDayItems) {
//						calPara.dayItemValueMap.put(referencedItemVO.getItem_code(), dayItemValueMap.get(referencedItemVO.getItem_code()).get(pk_psndoc));
//					}
//				}
//				// 真正的计算，由IMonthDataCreator接口的实现完成
//				Object result = creator.process(calPara);
//				// 吃水不忘挖井人，刚才从map中取了其他项目的值，现在自己的值算出来了，就要回报map，将自己的值放回map中
//				valueMap.put(pk_psndoc, TBMItemUtils.processNullValue(itemVO.getData_type().intValue(), result));
//			}
//			// 按人循环算完这个月报项目后，要更新到数据库
//			try {
//				calHelper.persistMonthItemValueTODB(pkCorp, itemIndex, itemVOs, monthItemValueMap, psnpks, year, month);
//			} catch (SQLException e) {
//				Logger.error(e.getMessage(), e);
//				throw new BusinessException(e.getMessage(), e);
//			}
		}
	}

	/**
	 * 将dbVOs数组处理后返回。dbVOs数组是数据库中已有的数据
	 * 如果showNoDataRecord为true，则保证一人一条记录，不管数据库中是否有（没有就new一个daystatvo）
	 * 如果showNoDataRecord为false，则要将dbVOs中所有字段为空的记录去除掉
	 * @param psndocVOs
	 * @param aggVOs
	 * @param date
	 * @param showNoDataRecord
	 * @param endDate
	 * @return
	 */
	private MonthStatVO[] processDBVOs(TBMPsndocVO[] psndocVOs,MonthStatVO[] dbVOs, String year, String month,boolean showNoDataRecord, UFLiteralDate endDate) throws BusinessException {
		if(ArrayUtils.isEmpty(dbVOs)&&!showNoDataRecord)
			return null;
		if(ArrayUtils.isEmpty(psndocVOs))
			return null;
		//dbVO的map，key是pk_psndoc
		Map<String, MonthStatVO> dbVOMap = CommonUtils.toMap(MonthStatVO.PK_PSNDOC, dbVOs);
		if(dbVOMap==null) dbVOMap = new HashMap<String, MonthStatVO>();
		List<MonthStatVO> retList = new ArrayList<MonthStatVO>();
		//如果不含空记录
		if(!showNoDataRecord){
			for(int i=0;i<psndocVOs.length;i++){
				TBMPsndocVO psndocVO = psndocVOs[i];
				String pk_psndoc = psndocVO.getPk_psndoc();
				MonthStatVO dbVO = dbVOMap.get(pk_psndoc);
				if(dbVO==null||dbVO.isNoDataRecord())
					continue;
				retList.add(dbVO);
				//设置任职主键(日报表中并没有存储，元数据上有)
				dbVO.setPk_psnjob(psndocVO.getPk_psnjob());
				dbVO.setPk_org_v(psndocVO.getPk_org_v());
				dbVO.setPk_dept_v(psndocVO.getPk_dept_v());
				dbVO.setPk_dept(psndocVO.getPk_dept());
			}
			return retList.size()==0?null:retList.toArray(new MonthStatVO[0]);
		}
		//如果含空记录，则要求每人都要有vo，没有的话，需要new一个

		MonthStatVO[] retArray = new MonthStatVO[psndocVOs.length];
		for(int i=0;i<psndocVOs.length;i++){
			TBMPsndocVO psndocVO = psndocVOs[i];
			MonthStatVO monthstatVO = dbVOMap.get(psndocVO.getPk_psndoc());
			if(monthstatVO != null){
				retArray[i]=monthstatVO;
				monthstatVO.setPk_psnjob(psndocVO.getPk_psnjob());
				monthstatVO.setPk_org_v(psndocVO.getPk_org_v());
				monthstatVO.setPk_dept_v(psndocVO.getPk_dept_v());
				monthstatVO.setPk_dept(psndocVO.getPk_dept());
				continue;
			}
			//如果没有则new一个
			monthstatVO = new MonthStatVO();
			retArray[i]=monthstatVO;
			monthstatVO.setTbmyear(year);
			monthstatVO.setTbmmonth(month);
			monthstatVO.setPk_psndoc(psndocVO.getPk_psndoc());
			monthstatVO.setPk_psnjob(psndocVO.getPk_psnjob());
			monthstatVO.setPk_group(psndocVO.getPk_group());
			monthstatVO.setPk_org(psndocVO.getPk_org());
			monthstatVO.setPk_org_v(psndocVO.getPk_org_v());
			monthstatVO.setPk_dept_v(psndocVO.getPk_dept_v());
			monthstatVO.setPk_dept(psndocVO.getPk_dept());
//			OrgVersionVO orgv = orgvs.get(monthstatVO.getPk_psndoc());
//			if(orgv!= null){
//				monthstatVO.setPk_org_v(orgv.getPk_vid());
//			}
//			DeptVersionVO deptv = deptvs.get(monthstatVO.getPk_psndoc());
//			if(orgv!= null){
//				monthstatVO.setPk_dept_v(deptv.getPk_vid());
//			}
		}
		return retArray;
	}



	@Override
	public TBMPsndocVO[] queryUnGenerateByCondition(LoginContext context,
			FromWhereSQL fromWhereSQL, String year, String month)
			throws BusinessException {
		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(context.getPk_org(), year, month);
		UFLiteralDate periodBeginDate = periodVO.getBegindate();
		UFLiteralDate periodEndDate = periodVO.getEnddate();
		fromWhereSQL = TBMPsndocSqlPiecer.ensureTBMPsndocTable(fromWhereSQL);
		//增加权限处理
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		//使用维护权限
//		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocPermissionSQL2QuerySQL("60170psndoc", IActionCode.EDIT, fromWhereSQL);
		String alias = fromWhereSQL.getTableAliasByAttrpath(".");
		String extraCond = alias+"."+TBMPsndocVO.PK_PSNDOC+" not in("+
		"select "+MonthStatVO.PK_PSNDOC+" from "+MonthStatVO.getDefaultTableName()+" where "+
		MonthStatVO.PK_ORG+"='"+context.getPk_org()+"' and "+MonthStatVO.TBMYEAR+"='"+year+"' and "+
		MonthStatVO.TBMMONTH+"='"+month+"' and "+MonthStatVO.ISEFFECTIVE+"='Y')";
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocCond2QuerySQL(extraCond, fromWhereSQL);
		ITBMPsndocQueryService queryService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		TBMPsndocVO[] vos = queryService.queryLatestByCondition(context.getPk_org(), fromWhereSQL, periodBeginDate, periodEndDate);
		TaBusilogUtil.writeMonthStatUngenBusiLog(vos, periodVO.getBegindate(), periodVO.getEnddate());
		return vos;
	}

	/**
	 * 记录所有汇总参数的类，即所有的类别、项目等
	 * @author zengcheng
	 *
	 */
	private static class CalSumParam{

		MonthCalParam monthCalParam;
		String pk_org;
		TimeRuleVO timeRuleVO;
		String[] pk_psndocs;
		String psndocInSQL;//人员主键的in sql。注意，psndocInSQL和pk_psndocs实际上是等同的，只不过，如果只传pk_psndocs，不存psndocInSQL的话，很多地方都要根据pk_psndocs再造临时表，比较浪费
		PeriodVO periodVO;


		Map<String,Object> paramValues;//各个参数的值，key是参数的编码

		Map<String, IMonthDataCreator> dataCreatorMap;//java实现类的map，key是日报项目的code（只有java类的项目put进去），value是实现类

		ItemCopyVO[] monthItemVOs;//月报项目，要求按计算顺序排列
		Map<String, AggShiftVO> aggShiftMap;//所有班次的aggvo的map
		Map<String, ShiftVO> shiftMap;//所有班次的map

		LeaveTypeCopyVO[] leaveCopyVOs;
		AwayTypeCopyVO[] awayCopyVOs;
		OverTimeTypeCopyVO[] overCopyVOs;
		ShutDownTypeCopyVO[] shutCopyVOs;

//		// 所有人员的排班
//		Map<String, Map<UFLiteralDate, AggPsnCalendar>> calendarMap;
//		// 所有人员的timedata数据,第一个string是人员主键，第二个UFLiteralDate是日期，value是timedatavo
//		Map<String, Map<UFLiteralDate, TimeDataVO>> timedataMap=null;
//		// 所有人员的lateearly数据
//		Map<String, Map<UFLiteralDate, LateEarlyVO>> lateearlyMap=null;
//		// 所有人员的考勤档案数据,key是人员主键，value是在这段时间内的考勤档案vo(一般情况下只有一条。如果有多条，那么已经按时间先后排好序)
//		Map<String, List<TBMPsndocVO>> tbmPsndocMap = null;
//		// 所有人员的日报的主表主键,第一个string是人员主键pk_psndoc,第二个string是日期，第三个string是pk_daystat。这个map的主要作用是为后面的daystatb子表生成做准备
//		Map<String, Map<String, String>> daystatPKMap;
		double workDayLength;
	}

	@Override
	public MonthStatVO[] generate(String pk_org, FromWhereSQL fromWhereSQL, String year,
			String month, boolean showNoDataRecord) throws BusinessException {
		generate(pk_org, fromWhereSQL, year, month);
		return queryByCondition(pk_org, fromWhereSQL, year, month, showNoDataRecord);

	}

	@Override
	public MonthStatVO[] queryByConditionAndDept(String pk_dept,
			boolean containsSubDepts, FromWhereSQL fromWhereSQL, String year,
			String month, boolean showNoDataRecord) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		DeptVO deptVO = (DeptVO) dao.retrieveByPK(DeptVO.class, pk_dept);
		OrgVO orgVO = NCLocator.getInstance().lookup(IAOSQueryService.class). queryHROrgByOrgPK(deptVO.getPk_org());
		return queryByCondition(orgVO.getPk_org(), TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL),
				year, month, showNoDataRecord);
	}

	@Override
	public TBMPsndocVO[] queryUnGenerateByConditionAndDept(String pk_dept,
			boolean containsSubDepts, FromWhereSQL fromWhereSQL, String year,
			String month) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		DeptVO deptVO = (DeptVO) dao.retrieveByPK(DeptVO.class, pk_dept);
		OrgVO orgVO = NCLocator.getInstance().lookup(IAOSQueryService.class). queryHROrgByOrgPK(deptVO.getPk_org());
		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(orgVO.getPk_org(), year, month);
		UFLiteralDate periodBeginDate = periodVO.getBegindate();
		UFLiteralDate periodEndDate = periodVO.getEnddate();
		fromWhereSQL = TBMPsndocSqlPiecer.ensureTBMPsndocTable(fromWhereSQL);
		String alias = fromWhereSQL.getTableAliasByAttrpath(".");
		String extraCond = alias+"."+TBMPsndocVO.PK_PSNDOC+" not in("+
		"select "+MonthStatVO.PK_PSNDOC+" from "+MonthStatVO.getDefaultTableName()+" where "+
		MonthStatVO.TBMYEAR+"='"+year+"' and "+MonthStatVO.TBMMONTH+"='"+month+"' and "+MonthStatVO.ISEFFECTIVE+"='Y')";
		fromWhereSQL = TBMPsndocSqlPiecer.addTBMPsndocCond2QuerySQL(extraCond, fromWhereSQL);
		fromWhereSQL = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL);
		ITBMPsndocQueryService queryService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		return queryService.queryLatestByCondition(null, fromWhereSQL, periodBeginDate, periodEndDate);
	}

	@Override
	public MonthStatVO queryByPsn(String pk_psndoc, String year, String month)
			throws BusinessException {
		return queryByPsn(pk_psndoc, null, year, month);
	}

	@Override
	public MonthStatVO[] queryByPsn(String pk_psndoc, String year)
			throws BusinessException {
		String cond = MonthStatVO.PK_PSNDOC+"=? and "+MonthStatVO.TBMYEAR+"=? ";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_psndoc);
		para.addParam(year);
		String orderby = MonthStatVO.TBMYEAR+","+MonthStatVO.TBMMONTH;
		return queryByPsn(pk_psndoc, cond, orderby, para);
	}

	protected MonthStatVO[] queryByPsn(String pk_psndoc, String cond,String orderby,SQLParameter para)
	throws BusinessException {

		//最新的一条考勤档案记录
		TBMPsndocVO latestPsndocVO = NCLocator.getInstance().lookup(ITBMPsndocQueryMaintain.class).queryByPsndocAndDateTime(pk_psndoc, new UFDateTime());
		String pk_org = latestPsndocVO.getPk_org();
		MonthStatDAO dao = new MonthStatDAO();
		MonthStatVO[] vos =null;

		try {
			vos = dao.query(pk_org, MonthStatVO.class,
					new String[]{MonthStatVO.PK_PSNDOC,MonthStatVO.TBMYEAR,MonthStatVO.TBMMONTH
				,MonthStatVO.PK_GROUP,MonthStatVO.PK_ORG,MonthStatVO.ISAPPROVE},
				cond,orderby, para);
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		if(ArrayUtils.isEmpty(vos))
			return null;
		processPsnjob4Psn(pk_psndoc, vos);
		return vos;
	}

	/**
	 * 为员工自助的月报记录处理pk_psnjob
	 * 因为员工自助的月报都是直接从月报表中查询的，没有关联考勤档案，因此pk_psnjob字段无值，导致自助
	 * 的界面上无法显示任职的相关信息
	 * @param pk_psndoc
	 * @param monthstatVOs
	 * @throws BusinessException
	 */
	private void processPsnjob4Psn(String pk_psndoc,MonthStatVO[] monthstatVOs) throws BusinessException{
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		for(MonthStatVO monthstatVO:monthstatVOs){
			String pk_org = monthstatVO.getPk_org();
			String year = monthstatVO.getTbmyear();
			String month = monthstatVO.getTbmmonth();
			PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
			if(periodVO==null)
				continue;
			TBMPsndocVO psndocVO = psndocService.queryLatestByPsndocDate(pk_org, pk_psndoc, periodVO.getBegindate(), periodVO.getEnddate());
			if(psndocVO==null)
				continue;
			monthstatVO.setPk_psnjob(psndocVO.getPk_psnjob());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public MonthWorkVO[] queryMonthWorkVOsByPsn(String pk_org, String pk_psndoc,
			String year, String month) throws BusinessException {
		String cond = MonthWorkVO.PK_MONTHSTAT+" in (select "+MonthStatVO.PK_MONTHSTAT+" from "+
			MonthStatVO.getDefaultTableName()+" where "+MonthStatVO.PK_ORG+"=? and "+
			MonthStatVO.PK_PSNDOC+"=? and "+MonthStatVO.TBMYEAR+"=? and "+MonthStatVO.TBMMONTH+"=?)";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(pk_psndoc);
		para.addParam(year);
		para.addParam(month);
		return (MonthWorkVO[]) CommonUtils.toArray(MonthWorkVO.class, new BaseDAO().retrieveByClause(MonthWorkVO.class, cond, para));
	}

	/**
	 * 月报生成结束时对存在考勤异常的人员发送提醒消息
	 * @param pk_org
	 * @param pk_psndocs
	 * @param curPeriod
	 * @throws BusinessException
	 */


//	private  Hashtable<String, String[]> createTransferValues(PsndocVO vo,String url) throws BusinessException  {
//		Hashtable<String, String[]> hm = new Hashtable<String, String[]>();
//		String[] fieldCode = TimeitemConst.FIELDCODE;
//		for (int i = 0; i < fieldCode.length; i++) {
//			String value = "";
//			if ("name".equals(fieldCode[i])) {
//				// 姓名
//				value = vo.getName();
//			}
//			else if ("url".equals(fieldCode[i])) {
//				// 链接地址
//				value = "testtest";
//			}
//			else {
//				value = "";
//			}
//			hm.put(fieldCode[i], new String[]{value});
//		}
//		return hm;
//	}
//	private void sendMessage(String pk_org, String[] pk_psndocs, PeriodVO curPeriod,CalSumParam calSumParam )throws BusinessException {
//
//		Set<String> noticePsnPkset = this.getExceptionPsns(pk_org, pk_psndocs, curPeriod, calSumParam);//异常人员主键集合
//		if(noticePsnPkset.size()>0){
//		INotice secvice = NCLocator.getInstance().lookup(INotice.class);
//		NoticeTempletVO[] nt = secvice.queryDistributedTemplates(TimeitemConst.PK_NOTICE_SORT, PubEnv.getPk_group(), pk_org, true);
//		Iterator<String> it = noticePsnPkset.iterator();
//		String old="";
//		while(it.hasNext()){
//			String psnPK=it.next();//异常人员主键
//			IPersistenceRetrieve perstRetrieve = NCLocator.getInstance().lookup( IPersistenceRetrieve.class );
//			PsndocVO psndocVO = (PsndocVO) perstRetrieve.retrieveByPk(null, PsndocVO.class, psnPK);//通过主键得到异常人员的VO
//			if(psndocVO==null){
//				continue;
//			}
//			String[] mail=new String[]{psndocVO.getEmail()};//得到邮箱账户
//			if(null==mail[0]||mail.length<=0){
//				continue;
//			}
//			// 替换通知模板的变量
//			String noticeContent = nt[0].getContent();// 通知模板的内容
//			if(noticeContent.indexOf("<#name#>")<0 && !StringUtils.isEmpty(old)){
//				noticeContent=noticeContent.replace(old, "<#name#>");//不替换 name就一直是第一个接收人的name， url也得替换
//			}
//			StringOperator strSys = new StringOperator(noticeContent);
//			new StringOperator(strSys);
//			String psnName=psndocVO.getName();//员工姓名
//			old=psnName;//如果给多人发送邮件时候 psnname总是第一个人的 为了防止这种情况就把上一次的人员取出来然后再替换掉
//			strSys.replaceAllString("<#name#>",psnName);// 员工姓名
//			strSys.replaceAllString("<#url#>", "");//链接地址
//			if (nt != null && nt.length > 0) {
//				// 将属性值放到通知模板上
//				nt[0].setReceiverEmails(mail);//接收者邮件地址
//				nt[0].setContent(strSys.toString());//发送内容
//				if (StringUtils.isBlank( nt[0].getCurrentUserPk() ) || nt[0].getCurrentUserPk().length() != 20) {
//				// 如果模板的当前用户为空，则附上当前用户，或NC系统用户
//				nt[0].setCurrentUserPk( PubEnv.getPk_user() != null && PubEnv.getPk_user().length() == 20 ? PubEnv.getPk_user()
//						: INCSystemUserConst.NC_USER_PK );
//				}
//			//发送通知
//			secvice.sendNotice_RequiresNew(nt[0], pk_org, false);
//
//			}
//		}
//		}
//	}

//	private void sendMessage(String pk_org, String[] pk_psndocs, PeriodVO curPeriod,CalSumParam calSumParam )throws BusinessException {
//		Set<String> noticePsnPkset = getExceptionPsns(pk_org, pk_psndocs, curPeriod, calSumParam);//异常人员主键集合
//		if(noticePsnPkset.size()<=0){
//			return;
//		}
//		String[] noticePsnPks = (String[]) noticePsnPkset.toArray(new String[0]);
//		String insql=StringPiecer.getDefaultPiecesTogether(noticePsnPks);
//		String cond="pk_psndoc in (" + insql +" )";
//		//查询出所有的psndocvo
//		PsndocVO[] psndocvos = (PsndocVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, PsndocVO.class, cond);
//		if(ArrayUtils.isEmpty(psndocvos))
//			return;
//		INotice service = NCLocator.getInstance().lookup(INotice.class);
//		IURLGenerator IurlDirect =  NCLocator.getInstance().lookup(IURLGenerator.class);
//		NoticeTempletVO[] nt = service.queryDistributedTemplates(TimeitemConst.PK_NOTICE_SORT, PubEnv.getPk_group(), pk_org, true);
//		HashMap<String, UserVO[]> userMap = NCLocator.getInstance().lookup(IUserPubService.class).batchQueryUserVOsByPsnDocID(pk_psndocs, null);
////		String old="";
//		for(PsndocVO psndocVO: psndocvos){
//			//每次使用前都用原始数据重新复制一份，否则容易带有上次发送消息的内容
//			NoticeTempletVO noticeTempletVO = (NoticeTempletVO) nt[0].clone();
//
//			// 替换通知模板的变量
//			String noticeContent = noticeTempletVO.getContent();// 通知模板的内容
////			if(noticeContent.indexOf("<#name#>")<0 && !StringUtils.isEmpty(old)){
////				noticeContent=noticeContent.replace(old, "<#name#>");//不替换 name就一直是第一个接收人的name， url也得替换
////			}
//			StringOperator strSys = new StringOperator(noticeContent);
//			new StringOperator(strSys);
//			String psnName=psndocVO.getName();//员工姓名
////			old=psnName;//如果给多人发送邮件时候 psnname总是第一个人的 为了防止这种情况就把上一次的人员取出来然后再替换掉
//			strSys.replaceAllString("<#name#>",psnName);// 员工姓名
//			UserVO[] users = userMap.get(psndocVO.getPk_psndoc());
//			noticeTempletVO.setReceiverPkUsers(StringPiecer.getStrArrayDistinct(users, UserVO.CUSERID));//设置接收人
//			SSOInfo ssinfo = new SSOInfo();
//			if(!ArrayUtils.isEmpty(users)){
//			   // ssinfo.setUserPassword(users[0].getUser_password());
//				ssinfo.setUserPK(users[0].getCuserid());
//			}
//			ssinfo.setTtl(PubEnv.getServerTime().getDateTimeAfter(30));
//			ssinfo.setFuncode("E20200910");//E20200910 考勤月报的补考勤的功能节点号
//			String urlTitle=IurlDirect.buildHTML(ssinfo, ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0088")/*@res "员工自助补考勤!"*/);
////			String urlTitle=IurlDirect.buildURLString(ssinfo);
//			strSys.replaceAllString("<#url#>",urlTitle);
//			if (nt != null && nt.length > 0) {
//				// 将属性值放到通知模板上
////				nt[0].setReceiverEmails(mail);//接收者邮件地址（设置了接收人就不用再设置email了）
//				noticeTempletVO.setContent(strSys.toString());//发送内容
//				if (StringUtils.isBlank( noticeTempletVO.getCurrentUserPk() ) || noticeTempletVO.getCurrentUserPk().length() != 20) {
//				// 如果模板的当前用户为空，则附上当前用户，或NC系统用户
//				noticeTempletVO.setCurrentUserPk( PubEnv.getPk_user() != null && PubEnv.getPk_user().length() == 20 ? PubEnv.getPk_user()
//						: INCSystemUserConst.NC_USER_PK );
//				}
//				//发送通知
//				service.sendNotice_RequiresNew(noticeTempletVO, pk_org, false);
//			}
//		}
//	}
	//V63以后使用平台的消息通知发送方式
	private void sendMessage(String pk_org, String[] pk_psndocs, PeriodVO curPeriod,CalSumParam calSumParam )throws BusinessException {
		Set<String> noticePsnPkset = getExceptionPsns(pk_org, pk_psndocs, curPeriod, calSumParam);//异常人员主键集合
		if(noticePsnPkset.size()<=0){
			return;
		}
		String[] noticePsnPks = (String[]) noticePsnPkset.toArray(new String[0]);
		InSQLCreator isc = new InSQLCreator();
		String insql=isc.getInSQL(noticePsnPks);
		String cond="pk_psndoc in (" + insql +" )";
		//查询出所有的psndocvo
		PsndocVO[] psndocvos = (PsndocVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByClause(null, PsndocVO.class, cond);
		if(ArrayUtils.isEmpty(psndocvos))
			return;
		//查询组织
		OrgVO org = (OrgVO) NCLocator.getInstance().lookup(IPersistenceRetrieve.class).retrieveByPk(null, OrgVO.class, pk_org);
		//查询月报
		FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(noticePsnPks);
		MonthStatVO[] monthStatVOs = queryByCondition(pk_org, fromWhereSQL, curPeriod.getTimeyear(), curPeriod.getTimemonth(),true);
		Map<String, MonthStatVO> monthMap = CommonUtils.toMap(MonthStatVO.PK_PSNDOC, monthStatVOs);

		HashMap<String, UserVO[]> userMap = NCLocator.getInstance().lookup(IUserPubService.class).batchQueryUserVOsByPsnDocID(pk_psndocs, null);
		IURLGenerator IurlDirect =  NCLocator.getInstance().lookup(IURLGenerator.class);
		IHRMessageSend messageSendServer = NCLocator.getInstance().lookup(IHRMessageSend.class);
		//按人员循环发送消息
		for(PsndocVO psndocVO: psndocvos){
			HRBusiMessageVO messageVO = new HRBusiMessageVO();
			messageVO.setBillVO(monthMap.get(psndocVO.getPk_psndoc()));//元数据属性解析
			messageVO.setMsgrescode(TaMessageConst.MONTHSTATEXCPMSG);
			//业务变量
			Hashtable<String, Object> busiVarValues = new Hashtable<String, Object>();
			UserVO[] users = userMap.get(psndocVO.getPk_psndoc());
			SSOInfo ssinfo = new SSOInfo();
			if(!ArrayUtils.isEmpty(users)){
			   // ssinfo.setUserPassword(users[0].getUser_password());
				ssinfo.setUserPK(users[0].getCuserid());
			}
			ssinfo.setTtl(PubEnv.getServerTime().getDateTimeAfter(30));
			ssinfo.setFuncode("E20200910");//E20200910 考勤月报的补考勤的功能节点号

			String urlTitle= IurlDirect.buildURLString(ssinfo);
//				IurlDirect.buildHTML(ssinfo, ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0088")/*@res "员工自助补考勤!"*/);
			busiVarValues.put("url", urlTitle);
			busiVarValues.put("CURRUSERNAME", MultiLangHelper.getName(psndocVO));
			busiVarValues.put("CURRCORPNAME", MultiLangHelper.getName(org));
			messageVO.setBusiVarValues(busiVarValues);
			messageVO.setPkorgs(new String[]{pk_org});
			messageVO.setReceiverPkUsers(ArrayUtils.isEmpty(users)?null:new String[]{users[0].getPrimaryKey()});

			messageSendServer. sendBuziMessage_RequiresNew(messageVO);
		}
	}



	//得到月报异常人员
	private Set<String> getExceptionPsns(String pk_org, String[] pk_psndocs, PeriodVO curPeriod,CalSumParam calSumParam) throws BusinessException{

		Set<String> exceptionPsnPkSet = new HashSet<String>();
		ITBMPsndocQueryService tbmPsnQueryS = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);

		//查询考勤档案  找到对应的考勤方式（机器考勤or手工考勤）
		Map<String, List<TBMPsndocVO>> tbmPsnMap = tbmPsnQueryS.queryTBMPsndocMapByPsndocs(pk_org, pk_psndocs, curPeriod.getBegindate(), curPeriod.getEnddate(), true);
		if(MapUtils.isEmpty(tbmPsnMap)){
			return null;
		}
		ITimeDataQueryService timDataQuery = NCLocator.getInstance().lookup(ITimeDataQueryService.class);
		//机器考勤数据
		Map<String, Map<UFLiteralDate, TimeDataVO>>   machTimeDateMap = timDataQuery.queryVOMapByPsndocInSQLForMonth(pk_org, curPeriod.getBegindate(), curPeriod.getEnddate(), calSumParam.psndocInSQL);
		//循环查找机器考勤数据 的异常情况，若含有和月报异常发送邮件设置相匹配的 则把 该人员的pk加入的发送邮件的集合内
		if(MapUtils.isNotEmpty(machTimeDateMap)){
			for(String pk_psndoc :machTimeDateMap.keySet()){
				Map<UFLiteralDate, TimeDataVO> dataMap = machTimeDateMap.get(pk_psndoc);
				for(UFLiteralDate date :dataMap.keySet()){
					TimeDataVO timeDataVO = dataMap.get(date);
					if(!timeDataVO.isNormal()){
						if(calSumParam.timeRuleVO.isAbsentNotice()){
							if(this.isAbsent(timeDataVO)){
								exceptionPsnPkSet.add(pk_psndoc);
							}
						} if(calSumParam.timeRuleVO.isLateNotice()){
							if(this.isLate(timeDataVO)){
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}  if(calSumParam.timeRuleVO.isEarlyNotice()){
							if(this.isEarly(timeDataVO)){
								exceptionPsnPkSet.add(pk_psndoc);
							}
						} if(calSumParam.timeRuleVO.isMidOutNotice()){
							if(timeDataVO.getIsMidOut()){
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
					}
				}
			}
		}

		ILateEarlyQueryService handTimeDataQuery = NCLocator.getInstance().lookup(ILateEarlyQueryService.class);
		//手工考勤数据
		Map<String, Map<UFLiteralDate, LateEarlyVO>> handTimeDateMap = handTimeDataQuery.queryVOMapByPsndocInSQLForMonth(pk_org, curPeriod.getBegindate(),curPeriod.getEnddate(),calSumParam.psndocInSQL);
		if(MapUtils.isNotEmpty(handTimeDateMap)){
			for(String pk_psndoc:handTimeDateMap.keySet()){
				Map<UFLiteralDate, LateEarlyVO> dataMap = handTimeDateMap.get(pk_psndoc);
				for(UFLiteralDate date:dataMap.keySet()){
					LateEarlyVO handDataVO = dataMap.get(date);
					if(!handDataVO.isNormal()){
						if(calSumParam.timeRuleVO.isAbsentNotice()){
							if(this.isAbsent(handDataVO)){
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}  if(calSumParam.timeRuleVO.isLateNotice()){
							if(this.isLate(handDataVO)){
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}  if(calSumParam.timeRuleVO.isEarlyNotice()){
							if(this.isEarly(handDataVO)){
								exceptionPsnPkSet.add(pk_psndoc);
							}
						}
						//手工考勤没有中途外出这一项,所以这一段不再需要
//						if(calSumParam.timeRuleVO.isMidOutNotice()){
//							if(handDataVO.getAbsenthour().toDouble()>0){
//								exceptionPsnPkSet.add(pk_psndoc);
//							}
//						}

					}
				}
			}

		}
		return exceptionPsnPkSet;
	}


	/**
	 * //判断一条考勤是否 有迟到现象 true 为有迟到现象
	 */
	private boolean isLate(Object timeDataVO){
		if(timeDataVO instanceof TimeDataVO){
			TimeDataVO timedata = (TimeDataVO) timeDataVO;
			for(int i=0;i<4;i++){
				if(timedata.getIslate(i)==1)
					return true;
			}
		}
		if(timeDataVO instanceof LateEarlyVO){
			LateEarlyVO timedata = (LateEarlyVO) timeDataVO;
			if(timedata.getLatecount()>0 && timedata.getLatelength().toDouble() >= 0){
				return true;
			}
		}
		return false;
	}


	/**
	 * //判断一条考勤是否 有早退现象      true 为有早退现象
	 */
	private boolean isEarly(Object timeDataVO){
		if(timeDataVO instanceof TimeDataVO){
			TimeDataVO timedata = (TimeDataVO) timeDataVO;
			for(int i=0;i<4;i++){
				if(timedata.getIsearly(i)==1)
					return true;
			}
		}
		if(timeDataVO instanceof LateEarlyVO){
			LateEarlyVO timedata = (LateEarlyVO) timeDataVO;
			if(timedata.getEarlycount()>0 && timedata.getEarlylength().toDouble() >= 0){
				return true;
			}
		}
		return false;
	}


	/**
	 * //判断一条考勤是否 有矿工现象      true 为包含矿工
	 */
	private boolean isAbsent(Object timeDataVO){
		if(timeDataVO instanceof TimeDataVO){
			TimeDataVO timedata = (TimeDataVO) timeDataVO;
			for(int i=0;i<4;i++){
				if(timedata.getIsabsent(i) == 1 || timedata.getIsearlyabsent(i)==1 ||timedata.getIslateabsent(i)==1)
					return true;
			}
		}
		if(timeDataVO instanceof LateEarlyVO){
			LateEarlyVO timedata = (LateEarlyVO) timeDataVO;
			if(timedata.getAbsenthour().toDouble() > 0){
				return true;
			}
		}
		return false;
	}

	@Override
	public MonthStatVO[] queryByPsnAndNatualYearMonth(String pk_psndoc, String beginYear,
			String beginMonth, String endYear, String endMonth)
			throws BusinessException {
		beginMonth = ensureMonth(beginMonth);
		endMonth = ensureMonth(endMonth);
		//如果开始=结束，则直接按年月查
		if(beginYear.equals(endYear)&&beginMonth.equals(endMonth)){
			MonthStatVO[] vos =  queryByPsnAndNatualYearMonth(pk_psndoc, beginYear, beginMonth);
			processExtraInfoForSelf(vos);
			return vos;
		}
		//如果begin>end，则交换一下,增强系统的鲁棒性
		String beginYearMonth = beginYear+beginMonth;
		String endYearMonth = endYear+endMonth;
		if(beginYearMonth.compareTo(endYearMonth)>0){
			beginYear = endYearMonth.substring(0, 4);
			beginMonth = endYearMonth.substring(4, 6);
			endYear = beginYearMonth.substring(0, 4);
			endMonth = beginYearMonth.substring(4, 6);
		}
		//先简单地按照每个月都查询一次
		MonthStatVO[] vos = simplyQueryPsnMonthstat(pk_psndoc, beginYear, beginMonth, endYear, endMonth);
		if(ArrayUtils.isEmpty(vos))
			return null;
		//然后进行处理，包括：去掉重复的（有可能同一个组织内，两个自然月查出的月报都是相同的），中间有空档，查出空档
		vos = postProcessPsnMonthStat(vos);
		processExtraInfoForSelf(vos);
		return vos;
	}

	private MonthStatVO[] simplyQueryPsnMonthstat(String pk_psndoc, String beginYear,
			String beginMonth, String endYear, String endMonth) throws NumberFormatException, BusinessException{
		//依次查询从begin到end之间的每一个月
		//如果不垮年，则从开始月查询到结束月
		List<MonthStatVO> retList = new ArrayList<MonthStatVO>();
		int beginMonthInt = getMonth(beginMonth);
		int endMonthInt = getMonth(endMonth);
		if(beginYear.equals(endYear)){//不垮年
			queryMonths(pk_psndoc, Integer.parseInt(beginYear), beginMonthInt, endMonthInt, retList);
		}
		//如果跨年，则按三种情况处理：第一年，中间年，最后一年，
		else{
			int beginYearInt = Integer.parseInt(beginYear);
			int endYearInt = Integer.parseInt(endYear);
			for(int year = beginYearInt;year<=endYearInt;year++){
				int month1 = year==beginYearInt?beginMonthInt:1;
				int month2 = year==endYearInt?endMonthInt:12;
				queryMonths(pk_psndoc, year, month1, month2, retList);
			}
		}
		return retList.toArray(new MonthStatVO[0]);
	}
	protected  MonthStatVO queryByPsn(String pk_psndoc, String pk_org ,String year, String month)
	throws BusinessException {
		String cond = MonthStatVO.PK_PSNDOC+"=? and "+MonthStatVO.TBMYEAR+"=? and "+MonthStatVO.TBMMONTH+"=?";
		if(StringUtils.isNotEmpty(pk_org))
			cond+=" and "+MonthStatVO.PK_ORG+"=?";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_psndoc);
		para.addParam(year);
		para.addParam(month);
		if(StringUtils.isNotEmpty(pk_org))
			para.addParam(pk_org);
		MonthStatVO[] vos =  queryByPsn(pk_psndoc, cond, null, para);
		if(ArrayUtils.isEmpty(vos))
			return null;
		processPsnjob4Psn(pk_psndoc, vos);
		return vos[0];
	}
	private MonthStatVO[] postProcessPsnMonthStat(MonthStatVO[] vos) throws BusinessException{
		if(vos.length==1)
			return vos;
		String pk_psndoc = vos[0].getPk_psndoc();
		//按hr组织分组
		Map<String, MonthStatVO[]> orgMap = CommonUtils.group2ArrayByField(MonthStatVO.PK_ORG, vos);
		YearMonthComparator comparator = new YearMonthComparator();
		for(String pk_org:orgMap.keySet()){
			MonthStatVO[] orgMonthVOs = orgMap.get(pk_org);
			if(vos.length==1)
				continue;
			//首先按年度期间进行排序
			Arrays.sort(orgMonthVOs, comparator);
			//除去年度相同的记录（有可能会发生按不同的自然月查，返回相同月报记录的情况，例如，一个期间很长，从3.20到4.30，那么查自然月3月和4月，都会查出这条月报记录，需要干掉其中一条，不然用户会看到两条一模一样的月报）
			List<MonthStatVO> list = new ArrayList<MonthStatVO>(orgMonthVOs.length);
			Set<String> yearMonthSet = new HashSet<String>();
			for(MonthStatVO vo:orgMonthVOs){
				String yearMonth = vo.getTbmyear()+vo.getTbmmonth();
				if(yearMonthSet.contains(yearMonth))
					continue;
				yearMonthSet.add(yearMonth);
				list.add(vo);
			}
			//走到这里，已经没有重复的月报记录了,需要查询第一个月报到最后一个月报之间空着的记录
			//如果不垮考勤年，则很好判断是否有空着的记录
			int firstYearInt = Integer.parseInt(orgMonthVOs[0].getTbmyear());
			int lastYearInt = Integer.parseInt(orgMonthVOs[orgMonthVOs.length-1].getTbmyear());
			int firstMonthInt = getMonth(orgMonthVOs[0].getTbmmonth());
			int lastMonthInt = getMonth(orgMonthVOs[orgMonthVOs.length-1].getTbmmonth());
			if(firstYearInt==lastYearInt){
				//如果两个月是同一月，或者紧挨着，没必要处理(不跳出循环，需要加到orgMap中)
//				if(lastMonthInt-firstMonthInt<=1)
//					continue;
				if(lastMonthInt-firstMonthInt>1) {
					for(int month = firstMonthInt+1;month<lastMonthInt;month++){
						String strMonth = getMonth(month);
						if(yearMonthSet.contains(firstYearInt+strMonth))
							continue;
						//还没有这个月的月报，则需要查询
						MonthStatVO statvo = queryByPsn(pk_psndoc, pk_org, Integer.toString(firstYearInt), strMonth);
						if(statvo!=null)
							list.add(statvo);
					}
				}
				Collections.sort(list, comparator);
			}
			else{//如果跨年，则比较麻烦一点，因为考勤期间不一定只有12个，可能有13，14个，因此需要查询考勤期间表
				//查询这些考勤年的所有期间
				Map<String, PeriodVO[]> periodMap = PeriodServiceFacade.queryByYear(pk_org, Integer.toString(firstYearInt),
						Integer.toString(lastYearInt));
				for(int year = firstYearInt;year<=lastYearInt;year++){
					PeriodVO[] periodVOs = periodMap.get(Integer.toString(year));
					if(ArrayUtils.isEmpty(periodVOs))
						continue;
					Arrays.sort(periodVOs, comparator);
					int beginMonthInt = year == firstYearInt?firstMonthInt:1;
					int endMonthInt = year==lastYearInt?getMonth(periodVOs[periodVOs.length-1].getTimemonth()):lastMonthInt;
					for(int month = beginMonthInt;month<=endMonthInt;month++){
						String strMonth = getMonth(month);
						if(yearMonthSet.contains(firstYearInt+strMonth))
							continue;
						//还没有这个月的月报，则需要查询
						MonthStatVO statvo = queryByPsn(pk_psndoc, pk_org, Integer.toString(firstYearInt), strMonth);
						if(statvo!=null)
							list.add(statvo);
					}
				}
			}
			orgMap.put(pk_org, list.toArray(new MonthStatVO[0]));
		}
		Collection<MonthStatVO[]> c = orgMap.values();
		List<MonthStatVO> list = new ArrayList<MonthStatVO>();
		for(MonthStatVO[] array:c){
			list.addAll(Arrays.asList(array));
		}
		Collections.sort(list, comparator);
		return list.toArray(new MonthStatVO[0]);
	}

	private void queryMonths(String pk_psndoc,int year,int beginMonth,int endMonth,List<MonthStatVO> retList) throws BusinessException{
		for(int month=beginMonth;month<=endMonth;month++){
			MonthStatVO[] vos = queryByPsnAndNatualYearMonth(pk_psndoc, Integer.toString(year), getMonth(month));
			if(ArrayUtils.isEmpty(vos))
				continue;
			retList.addAll(Arrays.asList(vos));
		}
	}

	private String ensureMonth(String month){
		if(month.length()==1)
			month= "0"+month;
		if(month.compareTo("12")>0)
			month = "12";
		return month;
	}

	private int getMonth(String month){
		if(month.length()==0)
			return Integer.parseInt(month);
		if(month.startsWith("0"))
			return Integer.parseInt(month.substring(1));
		return Integer.parseInt(month);
	}

	private String getMonth(int month){
		if(month>=10)
			return Integer.toString(month);
		return "0"+month;
	}

	protected MonthStatVO[] queryByPsnAndNatualYearMonth(String pk_psndoc, String year,
			String month)throws BusinessException {
		//首先确定自然年月的日期范围
		IDateScope monthDateScope = getNatualMonthBeginEndDate(year, month);
		UFLiteralDate beginDate = monthDateScope.getBegindate();
		UFLiteralDate endDate = monthDateScope.getEnddate();
		//查询此人在此日期范围内所有的考勤档案
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		//增强鲁棒性，扩大查询的日期范围，防止有些月报因为自然月与考勤档案无交集而永远查不出来
		TBMPsndocVO[] psndocVOs = psndocService.queryTBMPsndocVOsByPsndocDate(pk_psndoc, beginDate.getDateBefore(30), endDate.getDateAfter(30));
		if(ArrayUtils.isEmpty(psndocVOs))
			return null;
		//按HR组织分组
		Map<String, TBMPsndocVO[]> orgMap = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_ORG, psndocVOs);
//		String cond = MonthStatVO.PK_PSNDOC+"=? and "+MonthStatVO.TBMYEAR+"=? and "+MonthStatVO.TBMMONTH+"=?";
		String cond = MonthStatVO.PK_ORG+"=? and " + MonthStatVO.PK_PSNDOC+"=? and "+MonthStatVO.TBMYEAR+"=? and "+MonthStatVO.TBMMONTH+"=?";
		SQLParameter para = new SQLParameter();
		MonthStatDAO dao = new MonthStatDAO();
		List<MonthStatVO> monthVOList = new ArrayList<MonthStatVO>();
		//按组织循环处理
		for(String pk_org:orgMap.keySet()){
			PeriodVO[] periodVOs = PeriodServiceFacade.queryPeriodsByDateScope(pk_org, beginDate, endDate);
			if(ArrayUtils.isEmpty(periodVOs))
				continue;
			// 可递归找存在的记录
			for(int i = ArrayUtils.getLength(periodVOs)-1;i >= 0;i--) {
				PeriodVO latestPeriodVO = periodVOs[i];
				TBMPsndocVO[] psnVOs = orgMap.get(pk_org);
				if(!DateScopeUtils.isCross(psnVOs, latestPeriodVO))
					continue;
				//查询此人此月的月报
				para.clearParams();
				para.addParam(pk_org);
				para.addParam(pk_psndoc);
				para.addParam(latestPeriodVO.getTimeyear());
				para.addParam(latestPeriodVO.getTimemonth());
				MonthStatVO[] vos =null;
				try {

					vos = dao.query(pk_org, MonthStatVO.class,
							new String[]{MonthStatVO.PK_PSNDOC,MonthStatVO.TBMYEAR,MonthStatVO.TBMMONTH
						,MonthStatVO.PK_GROUP,MonthStatVO.PK_ORG,MonthStatVO.ISAPPROVE},
						cond,null, para);
				} catch (DbException e) {
					Logger.error(e.getMessage(), e);
					throw new BusinessException(e.getMessage(), e);
				} catch (ClassNotFoundException e) {
					Logger.error(e.getMessage(), e);
					throw new BusinessException(e.getMessage(), e);
				}
				if(ArrayUtils.isEmpty(vos))
					continue;
				DateScopeUtils.sort(psnVOs);
				//因为查询考勤档案时，扩大了查询的日期范围，因此psnVOs的最后一条不一定是period范围内的，这里要特殊处理一下
				for(int j=psnVOs.length-1;j>=0;j--){
					if(DateScopeUtils.isCross(psnVOs[j], latestPeriodVO))
						vos[0].setPk_psnjob(psnVOs[j].getPk_psnjob());
				}
				monthVOList.add(vos[0]);
				break;
			}
		}

		return monthVOList.toArray(new MonthStatVO[0]);
	}

	/**
	 * 自助需要处理也写额外的信息项，例如姓名，公司名，部门名，以及考勤项目+休假出差加班类别
	 * @param vos
	 * @throws BusinessException
	 */
	private void processExtraInfoForSelf(MonthStatVO[] vos) throws BusinessException{
		if(ArrayUtils.isEmpty(vos))
			return;
		//为了减少查询次数，建立四个map
		//<pk_psnjob,PsnJobVO>
		Map<String, PsnJobVO> psnjobMap = null;
		//<pk_org,OrgVO>
		Map<String, OrgVO> orgMap = null;
		//<pk_dept,HRDeptVO>
		Map<String, HRDeptVO> deptMap = null;
		//<pk_hrorg,ViewOrderVO[]>
		Map<String, ViewOrderVO[]> viewOrderMap = new HashMap<String, ViewOrderVO[]>();
		BaseDAO dao = new BaseDAO();
		IViewOrderQueryService viewOrderService = NCLocator.getInstance().lookup(IViewOrderQueryService.class);
		nc.vo.hi.psndoc.PsndocVO psndocVO = (nc.vo.hi.psndoc.PsndocVO) dao.retrieveByPK(nc.vo.hi.psndoc.PsndocVO.class, vos[0].getPk_psndoc());
		String psnName = psndocVO.getMultiLangName();
		InSQLCreator isc = null;
		try{
			isc = new InSQLCreator();
			//查询psnjob
			String psnjobInSQL = isc.getInSQL(StringPiecer.getStrArrayDistinct(vos, MonthStatVO.PK_PSNJOB));
			String cond = PsnJobVO.PK_PSNJOB+" in("+psnjobInSQL+")";
			PsnJobVO[] psnjobVOs = CommonUtils.retrieveByClause(PsnJobVO.class, dao, cond);
			psnjobMap = CommonUtils.toMap(PsnJobVO.PK_PSNJOB, psnjobVOs);
			//查询orgvo
			String orgInSQL = isc.getInSQL(StringPiecer.getStrArrayDistinct(psnjobVOs, PsnJobVO.PK_ORG));
			cond = OrgVO.PK_ORG+" in("+orgInSQL+")";
			OrgVO[] orgVOs = CommonUtils.retrieveByClause(OrgVO.class, dao, cond);
			orgMap = CommonUtils.toMap(OrgVO.PK_ORG, orgVOs);
			//查询deptvo
			String deptInSQL = isc.getInSQL(StringPiecer.getStrArrayDistinct(psnjobVOs, PsnJobVO.PK_DEPT));
			cond = HRDeptVO.PK_DEPT+" in("+deptInSQL+")";
			HRDeptVO[] deptVOs = CommonUtils.retrieveByClause(HRDeptVO.class, dao, cond);
			deptMap = CommonUtils.toMap(HRDeptVO.PK_DEPT, deptVOs);

		}
		finally{
			if(isc!=null)
				isc.clear();
		}
		for(MonthStatVO vo:vos){
			vo.setPsnName(psnName);

			String pk_psnjob = vo.getPk_psnjob();
			PsnJobVO psnjobVO = psnjobMap.get(pk_psnjob);

			String pk_org = psnjobVO.getPk_org();
			OrgVO orgVO = orgMap.get(pk_org);
			vo.setOrgName(MultiLangUtil.getSuperVONameOfCurrentLang(orgVO, OrgVO.NAME, null));

			String pk_dept = psnjobVO.getPk_dept();
			HRDeptVO deptVO = deptMap.get(pk_dept);
			vo.setDeptName(deptVO.getMultilangName());

			String pk_hrorg = vo.getPk_org();
			ViewOrderVO[] viewOrderVOs = null;
			if(viewOrderMap.containsKey(pk_hrorg)){
				viewOrderVOs  = viewOrderMap.get(pk_hrorg);
			}
			else{
				viewOrderVOs = viewOrderService.queryViewOrder(pk_hrorg, ViewOrderVO.FUN_TYPE_MONTH, ViewOrderVO.REPORT_TYPE_PSN, false);
				viewOrderMap.put(pk_hrorg, viewOrderVOs);
			}
			vo.setViewOrderVOs(viewOrderVOs);
		}
	}

	/**
	 * 获取一个自然月的第一天和最后一天
	 * @param year
	 * @param month
	 * @return
	 */
	private IDateScope getNatualMonthBeginEndDate(String year,
			String month){

		String strMonth = month.length()==1?("0"+month):month;
		UFLiteralDate beginDate = UFLiteralDate.getDate(year+"-"+strMonth+"-01");
		int[] dayCount = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
		int intMonth = getMonth(month)-1;
		int daysCount = dayCount[intMonth];
		if(intMonth==1){
			int yearInt =Integer.parseInt(year);
			if(yearInt%400==0||(yearInt%4==0&&yearInt%100!=0))
				daysCount=29;
		}
		UFLiteralDate endDate = UFLiteralDate.getDate(year+"-"+strMonth+"-"+daysCount);
		return new DefaultDateScope(beginDate, endDate);
	}

	@Override
	public DeptMonthStatVO[] queryDMSVOByCondition(LoginContext context,String[] pk_depts,
			FromWhereSQL fromWhereSQL, String year, String month,
			boolean showNoDataRecord) throws BusinessException {
		if(!ArrayUtils.isEmpty(pk_depts)){
			//将部门条件加到客户端的条件中去
			fromWhereSQL = TBMPsndocSqlPiecer.addDeptPkArrayQuerySQL(pk_depts, fromWhereSQL);
		}
		MonthStatVO[] monthStatVOs = queryByCondition(context, fromWhereSQL, year, month, showNoDataRecord);
		if(ArrayUtils.isEmpty(monthStatVOs))
			return null;
		//将人员数据按部门分组，并且顺序不能改变。即，返回的DeptMonthStatVO[]中vo的顺序，要与MonthStatVO中部门的顺序一致
		List<DeptMonthStatVO> retList = new ArrayList<DeptMonthStatVO>();//list的作用是保证部门vo的顺序
		Map<String, DeptMonthStatVO> statMap = new HashMap<String, DeptMonthStatVO>();//map的作用是取vo方便
		for(MonthStatVO monthStatVO:monthStatVOs){
			String pk_dept = monthStatVO.getPk_dept();
			DeptMonthStatVO deptVO = statMap.get(pk_dept);
			if(deptVO==null){
				deptVO = new DeptMonthStatVO();
				deptVO.setPk_dept(pk_dept);

				//设置版本信息
				deptVO.setPk_dept_v(monthStatVO.getPk_dept_v());
				deptVO.setPk_org_v(monthStatVO.getPk_org_v());

				deptVO.setTbmyear(monthStatVO.getTbmyear());
				deptVO.setTbmmonth(monthStatVO.getTbmmonth());

//				deptVO.setMonthworkVOs(monthStatVO.getMonthworkVOs());

				statMap.put(pk_dept, deptVO);
				retList.add(deptVO);
			}
			//把个人月报数据合并为部门月报数据
			deptVO.mergePsnMonthStatVO(monthStatVO);
		}
		return retList.toArray(new DeptMonthStatVO[0]);
	}

	@Override
	public MonthStatVO[] queryByDeptPeriod(LoginContext context, String pk_dept,String year, String month) throws BusinessException {
		if(context == null)
			return null;
		FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, null);
//		String pk_org = context.getPk_org();
//		//首先查询期间的起止日期
//		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
//		if(periodVO==null)
//			throw new BusinessException(ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0090")
//					 /*@res "期间{0}不存在!"*/, year+"-"+month);
//		UFLiteralDate beginDate = periodVO.getBegindate();
//		UFLiteralDate endDate = periodVO.getEnddate();
//		//查询期间内在部门工作过的考勤档案
//		String tbmpsnSql = " select tbm_psndoc.pk_tbm_psndoc from tbm_psndoc inner join bd_psnjob on tbm_psndoc.pk_psnjob = bd_psnjob.pk_psnjob " +
//				"where bd_psnjob.pk_dept = '" + pk_dept + "' and tbm_psndoc.pk_org = '" + pk_org +
//				"' and (tbm_psndoc.begindate <= '" + endDate + "' and tbm_psndoc.enddate >='" + beginDate + "') ";
		return queryByCondition(context, fromWhereSQL, year, month, true);
	}

	/**
	 */
	@Override
	public MonthStatVO[] queryByCondition(LoginContext context,
			String[] pks, String year, String month,
			boolean showNoDataRecord) throws BusinessException {

		//pks = pk_psndoc+pk_psnorg
		if(ArrayUtils.isEmpty(pks))
			return null;
		String pk_org = context.getPk_org();
		//首先查询期间的起止日期
		PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
		if(periodVO==null)
			throw new BusinessException(ResHelper.getString("6017dayandmonthstat","06017dayandmonthstat0090")
					 /*@res "期间{0}不存在!"*/, year+"-"+month);
		List<String> psnInOrg = new ArrayList<String>();//本组织中的人员
		List<String> psnInAdminorg = new ArrayList<String>();//管理组织在本组织的人员
		for(String pk:pks){
			String[] split = pk.split(",");
			if(context.getPk_org().equals(split[1])){
				psnInOrg.add(split[0]);
			}else{
				psnInAdminorg.add(split[0]);
			}
		}
		//本组织的月报记录
		MonthStatVO[] vos1 = null;
		if(CollectionUtils.isNotEmpty(psnInOrg)){
			FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(psnInOrg.toArray(new String[0]));
			vos1 = queryByConditionAndOrg(pk_org, periodVO, fromWhereSQL, showNoDataRecord);
		}
		//管理组织在本组织的月报记录
		MonthStatVO[] vos2= null;
		if(CollectionUtils.isNotEmpty(psnInAdminorg)){
			FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(psnInAdminorg.toArray(new String[0]));
			vos2= queryByConditionAndAndminorg(pk_org, periodVO, fromWhereSQL, showNoDataRecord);
		}
		return (MonthStatVO[])ArrayUtils.addAll(vos1, vos2);
	}

}