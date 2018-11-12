package nc.impl.ta.teamcalendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PsndocFromWhereSQLPiecer;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.calendar.CalendarShiftMutexChecker;
import nc.impl.ta.calendar.TACalendarUtils;
import nc.impl.ta.psncalendar.PsnCalendarDAO;
import nc.itf.bd.holiday.IHolidayQueryService;
import nc.itf.bd.shift.IShiftQueryService;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.bd.timezone.TimezoneUtil;
import nc.itf.om.IAOSQueryService;
import nc.itf.om.IDeptQueryService;
import nc.itf.ta.IHRHolidayQueryService;
import nc.itf.ta.IPsnCalendarManageService;
import nc.itf.ta.ITeamCalendarManageMaintain;
import nc.itf.ta.ITeamCalendarQueryMaintain;
import nc.itf.ta.ITimeRuleQueryService;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.bd.team.team01.hr.ITeamQueryServiceForHR;
import nc.ui.hr.pub.InSQLFromWhereSQL;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.holiday.HolidayInfo;
import nc.vo.bd.holiday.HolidayVO;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.bd.team.team01.entity.TeamHeadVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.logging.Debug;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.holiday.HRHolidayVO;
import nc.vo.ta.log.TaBusilogUtil;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.ta.teamcalendar.AggTeamCalendarVO;
import nc.vo.ta.teamcalendar.QueryScopeEnum;
import nc.vo.ta.teamcalendar.TeamCalHoliday;
import nc.vo.ta.teamcalendar.TeamCalendarCommonValue;
import nc.vo.ta.teamcalendar.TeamCalendarVO;
import nc.vo.ta.teamcalendar.TeamInfoCalendarVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class TeamCalendarMaintainImpl implements ITeamCalendarManageMaintain, ITeamCalendarQueryMaintain {

	private SimpleDocServiceTemplate serviceTemplate;
	public SimpleDocServiceTemplate getServiceTemplate() {
		if(serviceTemplate==null)
			serviceTemplate = new SimpleDocServiceTemplate("");
		return serviceTemplate;
	}

	@Override
	public void batchChangeShift(String pk_org, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			boolean withOldShift, String oldShift, String newShift)
			throws BusinessException {
		if(!withOldShift){//如果不考虑原班次
			TeamHeadVO[] teamVOs = queryTeamVOsByOrgs(new String[]{pk_org}, fromWhereSQL);
			String[] pk_teams = SQLHelper.getStrArray(teamVOs, TeamHeadVO.CTEAMID);
			if(ArrayUtils.isEmpty(pk_teams))
				return;
			if(StringUtils.isEmpty(newShift)){
				new TeamCalendarDAO().deleteByTeamsAndDateArea(pk_teams, beginDate, endDate);
				return;
			}
			//如果新班次不空，则可以理解是排班周期是一天的循环排班，且覆盖原有班次，且遇假日照旧
			circularArrange(pk_org, pk_teams, beginDate, endDate, new String[]{newShift}, false, true,false);
			return;
		}
//		if(StringUtils.equals(oldShift, newShift))
//			return;
		TeamInfoCalendarVO[] calendarVOs = queryCalendarVOByCondition(pk_org, fromWhereSQL, beginDate, endDate);
		if(ArrayUtils.isEmpty(calendarVOs))
			return;
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		for(TeamInfoCalendarVO calendarVO:calendarVOs){
			Map<String, String> calendarMap = calendarVO.getCalendarMap();
//			if(MapUtils.isEmpty(calendarMap))
//				continue;
			for(UFLiteralDate date:allDates){
				// 如果班组当天工作日历不是HR控制则不处理
				if(calendarVO.getCtrlMap()!=null && calendarVO.getCtrlMap().get(date.toString())!=null && calendarVO.getCtrlMap().get(date.toString()).booleanValue())
					continue;
				//如果date日已排班，且班次主键等于oldShift，或者date日未排班，且oldShift为空，则都视为满足oldShift的匹配条件
				if(StringUtils.equals(oldShift, (calendarMap==null?null:calendarMap.get(date.toString()))))
					calendarVO.getModifiedCalendarMap().put(date.toString(), newShift);
			}
		}
		String pk_hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		save(pk_hrorg, calendarVOs,false);
	}
	
	@Override
	public void batchChangeShiftNew(String pk_org, String[] bz_pks,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			boolean withOldShift, String oldShift, String newShift)
			throws BusinessException {
		//业务日志
		TaBusilogUtil.writeBatchEditTeamCalendarBusiLog(pk_org, bz_pks, beginDate, endDate, oldShift, newShift);
		if(!withOldShift){//如果不考虑原班次
			//TeamHeadVO[] teamVOs = queryTeamVOsByBzPks(bz_pks);
			//String[] pk_teams = SQLHelper.getStrArray(teamVOs, TeamHeadVO.CTEAMID);
			if(ArrayUtils.isEmpty(bz_pks))
				return;
			if(StringUtils.isEmpty(newShift)){
				new TeamCalendarDAO().deleteByTeamsAndDateArea(bz_pks, beginDate, endDate);
				return;
			}
			//如果新班次不空，则可以理解是排班周期是一天的循环排班，且覆盖原有班次，且遇假日照旧
			circularArrange(pk_org, bz_pks, beginDate, endDate, new String[]{newShift}, false, true,false);
			return;
		}
//		if(StringUtils.equals(oldShift, newShift))
//			return;
		//TeamInfoCalendarVO[] calendarVOs = queryCalendarVOByCondition(pk_org, fromWhereSQL, beginDate, endDate);
		TeamInfoCalendarVO[] calendarVOs = queryCalendarVOByConditionNew(pk_org, bz_pks, beginDate, endDate);
		if(ArrayUtils.isEmpty(calendarVOs))
			return;
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		for(TeamInfoCalendarVO calendarVO:calendarVOs){
			Map<String, String> calendarMap = calendarVO.getCalendarMap();
//			if(MapUtils.isEmpty(calendarMap))
//				continue;
			for(UFLiteralDate date:allDates){
				// 如果班组当天工作日历不是HR控制则不处理
				if(calendarVO.getCtrlMap()!=null && calendarVO.getCtrlMap().get(date.toString())!=null && calendarVO.getCtrlMap().get(date.toString()).booleanValue())
					continue;
				//如果date日已排班，且班次主键等于oldShift，或者date日未排班，且oldShift为空，则都视为满足oldShift的匹配条件
				if(StringUtils.equals(oldShift, (calendarMap==null?null:calendarMap.get(date.toString()))))
					calendarVO.getModifiedCalendarMap().put(date.toString(), newShift);
			}
		}
		String pk_hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		save(pk_hrorg, calendarVOs,false);
	}

	@Override
	public TeamInfoCalendarVO[] circularArrange(String pk_org,
			String[] pk_teams, UFLiteralDate beginDate, UFLiteralDate endDate,
			String[] calendarPks, boolean isHolidayCancel,
			boolean overrideExistCalendar, boolean needLog) throws BusinessException {

		if(needLog)
			TaBusilogUtil.writeCircularlyArrangeteamCalendarBusiLog(pk_org, pk_teams, beginDate, endDate);
		
		//首先简单校验一下循环排班的班次是否冲突。
		new CalendarShiftMutexChecker().simpleCheckCircularArrange(pk_org, beginDate, endDate, calendarPks, true);
		String pk_group = PubEnv.getPk_group();
		if(isHolidayCancel){
			HolidayInfo<HRHolidayVO> holidayInfo = NCLocator.getInstance().lookup(IHRHolidayQueryService.class).queryHolidayInfo(pk_org, beginDate, endDate);
			if(holidayInfo!=null)
				// 遇假日取消，且有假日
				return circularArrangeWithHoliday(pk_group, pk_org, pk_teams, beginDate, endDate, calendarPks, overrideExistCalendar, holidayInfo);
		}
		// 遇假日照旧 或遇假日取消但无假日
		return circularArrangeIgnoreHoliday(pk_group, pk_org, pk_teams, beginDate, endDate, calendarPks, isHolidayCancel, overrideExistCalendar);
	}

	/**
	 * 循环排班，不考虑假日
	 * @param pk_group
	 * @param pk_org
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @param calendarPks
	 * @param isHolidayCancel
	 * @param overrideExistCalendar
	 * @return
	 * @throws BusinessException
	 */
	private TeamInfoCalendarVO[] circularArrangeIgnoreHoliday(String pk_group,String pk_org, String[] pk_teams,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			String[] calendarPks,boolean isHolidayCancel,boolean overrideExistCalendar) throws BusinessException{
		String pk_hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		//将循环的班次按日期范围全部展开
		Map<String, String> pkMap = TACalendarUtils.expandCalendar2MapByDateArea(beginDate, endDate, calendarPks);
		//构造所有人的排班map，校验方法需要
//		Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String,String>>();
//		for(String pk_team:pk_teams){
//			TeamInfoCalendarVO oldCalender = oldTeamCalendarMap.get(pk_team);
//			Map<String, String> modifiedPKMap = new HashMap<String, String>();
//			for(UFLiteralDate date:allDates){
//				if(oldCalender.getCtrlMap().get(date.toString())){
//				}
//			}
//			modifiedCalendarMap.put(pk_team, pkMap);
//		}
//		new CalendarShiftMutexChecker().checkCalendar(pk_org, modifiedCalendarMap, overrideExistCalendar,true, true);
		TeamCalendarDAO dao = new TeamCalendarDAO();
		//组织内所有班次
		Map<String, AggShiftVO> shiftMap = NCLocator.getInstance().lookup(IShiftQueryService.class).queryShiftAggVOMapByOrg(pk_org);
		//已有工作日历
		Map<String, TeamInfoCalendarVO> existTeamCalendarMap = CommonUtils.toMap(TeamHeadVO.CTEAMID, queryCalendarVOByPKTeams(pk_hrorg, pk_teams, beginDate, endDate));
		List<AggTeamCalendarVO> insertList = new ArrayList<AggTeamCalendarVO>();
		// 最终工作日历
		Map<String, Map<String, String>> finalCalendarMap = new HashMap<String, Map<String,String>>();

		Map<String, TeamHeadVO> teamMap = CommonUtils.toMap(TeamHeadVO.CTEAMID, NCLocator.getInstance().lookup(ITeamQueryServiceForHR.class).queryBZbyPK(pk_teams));
		for(String pk_team:pk_teams){
			//最终班次信息
			Map<String, String> finalShiftMap = new HashMap<String, String>();
			finalCalendarMap.put(pk_team, finalShiftMap);
			for(UFLiteralDate date:allDates){
				//如果用户选择不覆盖，且当日已有工作日历，则也不insert
				if(!overrideExistCalendar){
					if(existTeamCalendarMap.get(pk_team).getCalendarMap()!=null&&existTeamCalendarMap.get(pk_team).getCalendarMap().containsKey(date.toString())){
						finalShiftMap.put(date.toString(), existTeamCalendarMap.get(pk_team).getCalendarMap().get(date.toString()));
						continue;
					}
				}
				// 如果此班组当前天不是由HR控制，则不insert
				if(existTeamCalendarMap.get(pk_team).getCtrlMap()!=null && existTeamCalendarMap.get(pk_team).getCtrlMap().containsKey(date.toString()) 
						&& existTeamCalendarMap.get(pk_team).getCtrlMap().get(date.toString())){
					finalShiftMap.put(date.toString(), existTeamCalendarMap.get(pk_team).getCalendarMap().get(date.toString()));
					continue;
				}
				finalShiftMap.put(date.toString(), pkMap.get(date.toString()));
				AggTeamCalendarVO calendarVO = TeamCalendarUtils.createAggVOByShiftVO(teamMap.get(pk_team),pk_group, pk_org, date.toString(),pkMap.get(date.toString()), shiftMap, isHolidayCancel);
				insertList.add(calendarVO);
			}
		}
		if(insertList.size()==0)//如果直接return null会造成界面什么都没显示，
			return queryCalendarVOByPKTeams(pk_hrorg, pk_teams, beginDate, endDate);
			//return null;
		//进行校验：用最终的班次信息校验，所以要覆盖原有班次
		new CalendarShiftMutexChecker().checkCalendar(pk_org, finalCalendarMap, true,true, true);
		if(overrideExistCalendar)//如果用户选择覆盖已有排班，则需要将日期范围内的工作日历删除掉
			dao.deleteByTeamsAndDateArea(pk_teams, beginDate, endDate);
		AggTeamCalendarVO[] aggvos = insertList.toArray(new AggTeamCalendarVO[0]);
		//将日历天类型set到Aggvo Ares.Tank 2018-9-7 18:28:21
		aggvos = dealDateType4TeamCalendar(aggvos,pk_teams,pk_hrorg,beginDate,endDate);
		dao.insert(aggvos);
		//业务日志
//		TaBusilogUtil.writeCircularlyArrangeteamCalendarBusiLog(pk_hrorg, aggvos);
		// 同步人员工作日历
		NCLocator.getInstance().lookup(IPsnCalendarManageService.class).sync2TeamCalendarAfterCircularlyArrange(pk_org, finalCalendarMap, beginDate, endDate, false);
		return queryCalendarVOByPKTeams(pk_hrorg, pk_teams, beginDate, endDate);
	}
	/**
	 * 排班的时候使用
	 * 把dateType set 到 班组工作日历,
	 * @param aggvos
	 * @param pk_teams
	 * @param pk_hrorg
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	private AggTeamCalendarVO[] dealDateType4TeamCalendar(AggTeamCalendarVO[] aggvos, String[] pk_teams,
			String pk_hrorg, UFLiteralDate beginDate, UFLiteralDate endDate) {
		if(null==aggvos||aggvos.length<=0){
			return aggvos;
		}
		//查询默认的日历天类型
		IHRHolidayQueryService holidayService = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		Map<String, Integer> orgDateIntegerMap = new HashMap<>();//此组织默认的日历天类型<日期,日历天类型>
		try {
			orgDateIntegerMap = 
					holidayService.queryTeamWorkDayTypeInfo(pk_hrorg, beginDate, endDate);
		} catch (BusinessException e) {
			orgDateIntegerMap = new HashMap<>();
			Debug.debug(e);
			e.printStackTrace();
		}
		for(AggTeamCalendarVO aggVO : aggvos){//set日历天
			if(null!=aggVO&&null!=aggVO.getTeamCalendarVO()){
				Integer dateType = orgDateIntegerMap.get(aggVO.getTeamCalendarVO().getCalendar().toString());
				if(null==dateType){
					dateType = 0;
				}
				aggVO.getTeamCalendarVO().setDate_daytype(dateType);
			}
			
		}
		return aggvos;
	}

	/**
	 * 考虑假日的循环排班
	 * @param pk_org,业务单元主键
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @param calendarPks
	 * @param overrideExistCalendar
	 * @return
	 * @throws BusinessException 
	 */
	private TeamInfoCalendarVO[] circularArrangeWithHoliday(String pk_group,String pk_org, String[] pk_teams,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			String[] calendarPks,boolean overrideExistCalendar,HolidayInfo<HRHolidayVO> holidayInfo) throws BusinessException{
		//存储要设置的班次的map，key是人员主键，value的key是date，value是班次主键.考虑到有可能产生班次的对调，因此最终modifiedCalendarMap里的日期范围可能会超出begindate和enddate的范围
		Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String,String>>();
		//将循环的班次按日期范围全部展开，key是日期，value是班次主键
		Map<String, String> originalExpandedDatePkShiftMap = TACalendarUtils.expandCalendar2MapByDateArea(beginDate, endDate, calendarPks);
		UFLiteralDate[] dateArea = CommonUtils.createDateArray(beginDate, endDate);
		//默认班
		AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		//业务单元内所有班次
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByOrg(pk_org);
		//与假日切割的班次的map，key是人员，value的key是日期，value是psncalendar的聚合vo.只有假日对排班产生了影响，例如切割了，或者导致弹性班固化了，才存到此map里去，正常的天，或者被完全切割成公休的不会存
		Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap = new HashMap<String, Map<String,AggTeamCalendarVO>>();
		//记录对调前班次的map，key是人员，value的key是日期，value是对调前的班次
		Map<String, Map<String, String>> psnBeforeExgPkShiftMap = new HashMap<String, Map<String,String>>();
		//业务单元的时区
		TimeZone timeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		//人员已经排好的班次,范围是min(beginDate,对调日,假日).getDateBefore(1)到max(endDate,对调日，假日).getDateAfter(1)
		Set<String> holidayDateSet = new HashSet<String>();//存储假日、对调日的map，key是日期，用于日期排序，以确定查询工作日历的范围
		holidayDateSet.addAll(holidayInfo.getHolidayMap().keySet());
		holidayDateSet.addAll(holidayInfo.getSwitchMap().keySet());
		String[] allDates = holidayDateSet.toArray(new String[0]);//对假日、对调日排序
		Arrays.sort(allDates);
		//查询工作日历的最大日期范围
		UFLiteralDate calendarQueryBeginDate = allDates[0].compareTo(beginDate.toString())<0?UFLiteralDate.getDate(allDates[0]).getDateBefore(1):beginDate.getDateBefore(1);
		UFLiteralDate calendarQueryEndDate = allDates[allDates.length-1].compareTo(endDate.toString())>0?UFLiteralDate.getDate(allDates[allDates.length-1]).getDateAfter(1):endDate.getDateAfter(1);
		Map<String, Map<String, TeamCalendarVO>> psnExistsCalendarMap = new TeamCalendarDAO().queryCalendarVOMapByTeams(pk_teams, calendarQueryBeginDate, calendarQueryEndDate);
		Map<String, TeamHeadVO> teamMap = CommonUtils.toMap(TeamHeadVO.CTEAMID, NCLocator.getInstance().lookup(ITeamQueryServiceForHR.class).queryBZbyPK(pk_teams));
		//按班组循环处理
		for(int i=0;i<pk_teams.length;i++){
			Map<String, String> cloneDatePkShiftMap = new HashMap<String, String>(originalExpandedDatePkShiftMap);//每个人单独复制一份，因为虽然是循环排班，但是最后排班的情况可能每个人都不同
			Map<String, String> beforeExgPkShiftMap = new HashMap<String, String>();
			String pk_team = pk_teams[i];
			modifiedCalendarMap.put(pk_team, cloneDatePkShiftMap);
			psnBeforeExgPkShiftMap.put(pk_team, beforeExgPkShiftMap);
			//以及处理过的天(因为涉及到对调，可能有些后面的天在前面就处理过了)
			Set<String> processedDateSet = new HashSet<String>();
			//循环处理用户设置的循环班次
			for(int dateIndex=0;dateIndex<dateArea.length;dateIndex++){
				String date = dateArea[dateIndex].toString();//日期
				if(processedDateSet.contains(date))//处理过则不再处理
					continue;
				processedDateSet.add(date);
				String pk_shift = originalExpandedDatePkShiftMap.get(date);//班次，因为是循环排班，因此不可能为空
				//如果是公休
				if(ShiftVO.PK_GX.equals(pk_shift)){//直接调用公休的排班方法
					circularArrangeWithHolidayGX(teamMap.get(pk_team), beginDate, endDate, 
							cloneDatePkShiftMap, originalExpandedDatePkShiftMap,beforeExgPkShiftMap,
							psnExistsCalendarMap==null?null:psnExistsCalendarMap.get(pk_team),shiftMap, 
							holidayCutMap, processedDateSet, holidayInfo, defaultShift,date, timeZone);
					continue;
				}
				//如果不是公休，直接调用非公休的排班方法
				circularArrangeWithHolidayNonGX(teamMap.get(pk_team), calendarQueryBeginDate, 
						calendarQueryEndDate, cloneDatePkShiftMap, originalExpandedDatePkShiftMap, beforeExgPkShiftMap,
						psnExistsCalendarMap==null?null:psnExistsCalendarMap.get(pk_team), 
						shiftMap, holidayCutMap, processedDateSet, holidayInfo, defaultShift, date, timeZone);
			}
		}
		//至此，所有的班都排完，相关的排班信息存入了：
		//1.modifiedCalendarMap：用户要设置的班次的map；
		//2.holidayCutMap：如果排班的过程中因为假日导致psncalendarvo要特殊处理，则放入此map
		//进行校验：
//		new CalendarShiftMutexChecker().checkCalendar(pk_org, modifiedCalendarMap, overrideExistCalendar,true,true);
		//校验通过，持久化到数据库
		return circularArrangeWithHolidayPersistence(pk_group, pk_org, shiftMap, teamMap.values().toArray(new TeamHeadVO[0]), 
					beginDate, endDate, originalExpandedDatePkShiftMap, 
					modifiedCalendarMap, psnBeforeExgPkShiftMap, holidayCutMap, psnExistsCalendarMap,overrideExistCalendar);
	}
	
	/**
	 * 考虑假日的循环排班，当天的班次用户排为了非公休
	 * @throws BusinessException 
	 */
	private void circularArrangeWithHolidayNonGX(
			TeamHeadVO teamVO,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			Map<String, String> cloneDatePkShiftMap,
			Map<String, String> originalExpandedDatePkShiftMap,//用户排的原始的班次map，key是日期，value是班次主键，已经按照日期范围展开
			Map<String, String> beforeExgPkShiftMap,//如果产生了对调，此map记录对调前的班次，key是date
			Map<String, TeamCalendarVO> existsCalendarMap,//数据库中已经排的工作日历
			Map<String, AggShiftVO> shiftMap,//业务单元内所有的班次
			Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap,
			Set<String> processedDateSet,//已经处理过的日期set
			HolidayInfo<HRHolidayVO> holidayInfo,
			AggShiftVO defaultAggShiftVO,
			String date,
			TimeZone timeZone
	) throws BusinessException{

		String pk_shift = originalExpandedDatePkShiftMap.get(date);
		//如果此天不是对调日，或者是对调日但此人不享受假日，则按用户设置排班，不用特殊处理
		if(!holidayInfo.getSwitchMap().containsKey(date)){
			processHolidayCut(teamVO, cloneDatePkShiftMap, shiftMap.get(pk_shift), holidayInfo.getHolidayVOs(), holidayCutMap, date, timeZone);
			return;
		}
		String switchDate = holidayInfo.getSwitchMap().get(date);//对调日
		processedDateSet.add(switchDate);//对调日打上处理标志
		//看对调日是否在日期范围内,如果在范围内，那么对调是肯定要发生的，
		if(switchDate.compareTo(beginDate.toString())>=0&&switchDate.compareTo(endDate.toString())<=0){	
			exchangeDateInDateAreaNonGX(teamVO, cloneDatePkShiftMap, pk_shift, originalExpandedDatePkShiftMap.get(switchDate), beforeExgPkShiftMap,shiftMap, holidayCutMap, holidayInfo, date, switchDate, timeZone);
			return;
		}
		//如果对调日在此次设置班次的日期范围之外，则需要看对调日期的排班状况，如果
		//1.有排班，且排班为遇假日取消，则跟上面对调的逻辑一样
		//2.有排班，且排班为与假日照旧，则不对调
		//3.无排班，则自动对调日排上一个默认班（周一到周五），或者公休（周六日），然后执行对调，对调的结果是两天都是遇假日取消
		//注意：如果对调日属于考勤档案无效的日期，则等同于情况3
		//1,3可以合并处理，3可以看作1的一种特例
		//下面从已排的班次中取出对调日的班
		TeamCalendarVO switchCalendar = existsCalendarMap==null?null:existsCalendarMap.get(switchDate);
		exchangeDateOutDateAreaNonGX(teamVO, cloneDatePkShiftMap,pk_shift, switchCalendar, beforeExgPkShiftMap,
				shiftMap, holidayCutMap, holidayInfo, 
				defaultAggShiftVO, date, switchDate, timeZone);
	}
	
	
	/**
	 * 处理对调，对调日在循环排班的日期范围之外，且用户给当日排了非公休
	 * 与exchangeDateInDateAreaNonGX方法是一对，一个处理对调日在范围内，一个处理对调日在范围外
	 * @throws BusinessException 
	 */
	private void exchangeDateOutDateAreaNonGX(
			TeamHeadVO teamVO,
			Map<String, String> cloneDatePkShiftMap,
			String originalPkShift,
			TeamCalendarVO switchCalendar,
			Map<String, String> beforeExgPkShiftMap,//如果产生了对调，此map记录对调前的班次，key是date
			Map<String, AggShiftVO> shiftMap,
			Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap,//如果PsnCalendarVO需要特殊处理，则将处理结果放入这个map
			HolidayInfo<HRHolidayVO> holidayInfo,
			AggShiftVO defaultAggShiftVO,
			String date,
			String switchDate,
			TimeZone timeZone
			) throws BusinessException{
		//如果对调日排班了，且对调日属于有效的考勤档案，且对调日遇假日照旧，这种情况不对调
		if(switchCalendar!=null&&!switchCalendar.isHolidayCancel()){
			processHolidayCut(teamVO, cloneDatePkShiftMap, ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,originalPkShift), holidayInfo.getHolidayVOs(), holidayCutMap, date, timeZone);
			return;
		}
		beforeExgPkShiftMap.put(date, originalPkShift);//当日对调前的班次
		//如果没排班或者对调日遇假日取消
		//对调日的班肯定排当日的班,即两个班对调
		processHolidayCut(teamVO, cloneDatePkShiftMap, ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,originalPkShift), holidayInfo.getHolidayVOs(), holidayCutMap, switchDate, timeZone);
		String switchPkShift=switchCalendar==null?TACalendarUtils.getPkShiftByDate(switchDate, defaultAggShiftVO):switchCalendar.getPk_shift();
		beforeExgPkShiftMap.put(switchDate, switchPkShift);//对调日对调前的班次
		if(ShiftVO.PK_GX.equals(switchPkShift)){
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);//对调日的班次是公休，那么当日也肯定是公休了
			return;
		}
		//对调日班次非公休，则要看对调日的班次的工作时间段（对于弹性班，是最大工作时间段）与假日是否有交集（对于弹性班，最大工作时间段与假日有交集会导致固化，但不一定会导致工作时间段被切割）
		AggShiftVO switchShiftAggVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,switchPkShift);
		processHolidayCut(teamVO, cloneDatePkShiftMap, switchShiftAggVO, holidayInfo.getHolidayVOs(), holidayCutMap, switchDate, timeZone);
	}
	
	/**
	 * 处理对调，对调日在循环排班的日期范围内，且用户给当日排了非公休
	 * 与exchangeDateOutDateAreaNonGX方法是一对，一个处理对调日在范围内，一个处理对调日在范围外
	 * @throws BusinessException 
	 */
	private void exchangeDateInDateAreaNonGX(
			TeamHeadVO teamVO,
			Map<String, String> cloneDatePkShiftMap,
			String originalPkShift,//当日用户排的原始的班次
			String originalSwitchDayPkShift,//对调日用户排的原始的班次
			Map<String, String> beforeExgPkShiftMap,//如果产生了对调，此map记录对调前的班次，key是date
			Map<String, AggShiftVO> shiftMap,
			Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap,
			HolidayInfo<HRHolidayVO> holidayInfo,
			String date,
			String switchDate,
			TimeZone timeZone
			) throws BusinessException{
		beforeExgPkShiftMap.put(date, originalPkShift);//记录当日对调前的班次
		beforeExgPkShiftMap.put(switchDate, originalSwitchDayPkShift);//记录对调日对调前的班次
		//如果对调日的班次是公休，那么此天也排公休，
		if(ShiftVO.PK_GX.equals(originalSwitchDayPkShift)){
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);
		}
		else{//对调日不是公休，那么将对调日的班次掉到当日，然后处理当日班次与假日的交集即可
			processHolidayCut(teamVO, cloneDatePkShiftMap, ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,originalSwitchDayPkShift), holidayInfo.getHolidayVOs(), holidayCutMap, date, timeZone);
		}
		//对调日的班次也要处理
		processHolidayCut(teamVO, cloneDatePkShiftMap, ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,originalPkShift), holidayInfo.getHolidayVOs(), holidayCutMap, switchDate, timeZone);
		return;
	}
	
	/**
	 * 考虑假日的循环排班，当天的班次用户排为了公休
	 * @throws BusinessException 
	 */
	private void circularArrangeWithHolidayGX(
			TeamHeadVO teamVO,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			Map<String, String> cloneDatePkShiftMap,//方法往里放值
			Map<String, String> originalExpandedDatePkShiftMap,//用户排的原始的班次map，key是日期，value是班次主键，已经按照日期范围展开，方法从里取值
			Map<String, String> beforeExgPkShiftMap,//如果产生了对调，此map记录对调前的班次，key是date，方法往里放值
			Map<String, TeamCalendarVO> existsCalendarMap,//数据库中已经排的工作日历，方法从里取值
			Map<String, AggShiftVO> shiftMap,//组织内所有的班次，方法从里取值
			Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap,//方法往里放值
			Set<String> processedDateSet,//已经处理过的日期set，方法取值，也放值
			HolidayInfo<HRHolidayVO> holidayInfo,
			AggShiftVO defaultShift,
			String date,
			TimeZone timeZone
	) throws BusinessException{

		//如果此天不涉及到对调，或者涉及对调但此人不享受此假日，则公休依然是公休,不受影响
		if(!holidayInfo.getSwitchMap().containsKey(date))//此天不涉及对调
			return;
		//如果涉及到对调，则要看对调日的班次：
		String switchDate = holidayInfo.getSwitchMap().get(date);
		processedDateSet.add(switchDate);
		//如果对调日在此次设置班次的日期范围内，则：当日排对调日的班（如果非公休且与假日相切，还要减去假日），对调日排公休
		if(switchDate.compareTo(beginDate.toString())>=0&&switchDate.compareTo(endDate.toString())<=0){
			exchangeDateInDateAreaGX(teamVO, cloneDatePkShiftMap, 
					originalExpandedDatePkShiftMap.get(switchDate),beforeExgPkShiftMap, shiftMap, 
					holidayCutMap, holidayInfo, date, switchDate, timeZone);
			return;
		}
		//如果对调日在此次设置班次的日期范围之外，则需要看对调日期的排班状况，如果
		//1.有排班，且排班为遇假日取消，则跟上面对调的逻辑一样
		//2.有排班，且排班为与假日照旧，则不对调
		//3.无排班，则自动对调日排上一个默认班（周一到周五），或者公休（周六日），然后执行对调，对调的结果是两天都是遇假日取消
		//注意：如果对调日属于考勤档案无效的日期，则等同于情况3
		//1,3可以合并处理，3可以看作1的一种特例
		//下面从已排的班次中取出对调日的班
		TeamCalendarVO switchCalendar = existsCalendarMap==null?null:existsCalendarMap.get(switchDate);
		exchangeDateOutDateAreaGX(teamVO, cloneDatePkShiftMap, switchCalendar, beforeExgPkShiftMap,
				shiftMap, holidayCutMap, holidayInfo, 
				defaultShift, date, switchDate, timeZone);
	}
	
	/**
	 * 处理对调，对调日在循环排班的日期范围内，且用户给当日排了公休
	 * 与exchangeDateOutDateAreaGX方法是一对，一个处理对调日在范围内，一个处理对调日在范围外
	 * @throws BusinessException 
	 */
	private void exchangeDateInDateAreaGX(
			TeamHeadVO teamVO,
			Map<String, String> cloneDatePkShiftMap,
			String originalSwitchDayPkShift,//对调日用户排的原始的班次
			Map<String, String> beforeExgPkShiftMap,//如果产生了对调，此map记录对调前的班次，key是date
			Map<String, AggShiftVO> shiftMap,
			Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap,
			HolidayInfo<HRHolidayVO> holidayInfo,
			String date,
			String switchDate,
			TimeZone timeZone
			) throws BusinessException{
		beforeExgPkShiftMap.put(date, ShiftVO.PK_GX);//记录当日对调前的班次
		beforeExgPkShiftMap.put(switchDate, originalSwitchDayPkShift);//记录对调日对调前的班次
		cloneDatePkShiftMap.put(switchDate, ShiftVO.PK_GX);//对调日肯定是公休了
		if(ShiftVO.PK_GX.equals(originalSwitchDayPkShift)){
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);//对调日的班次是公休，那么当日也肯定是公休了
			return;
		}
		//对调日班次非公休，则要看对调日的班次的工作时间段（对于弹性班，是最大工作时间段）与假日是否有交集（对于弹性班，最大工作时间段与假日有交集会导致固化，但不一定会导致工作时间段被切割）
		AggShiftVO switchShiftAggVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,originalSwitchDayPkShift);
		processHolidayCut(teamVO, cloneDatePkShiftMap, switchShiftAggVO, 
				holidayInfo.getHolidayVOs(), holidayCutMap, 
				date, timeZone);
	}
	
	/**
	 * 处理对调，对调日在循环排班的日期范围之外，且用户给当日排了公休
	 * 与exchangeDateInDateAreaGX方法是一对，一个处理对调日在范围内，一个处理对调日在范围外
	 * @throws BusinessException 
	 */
	private void exchangeDateOutDateAreaGX(
			TeamHeadVO teamVO,
			Map<String, String> cloneDatePkShiftMap,
			TeamCalendarVO switchCalendar,
			Map<String, String> beforeExgPkShiftMap,//如果产生了对调，此map记录对调前的班次，key是date
			Map<String, AggShiftVO> shiftMap,
			Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap,//如果PsnCalendarVO需要特殊处理，则将处理结果放入这个map
			HolidayInfo<HRHolidayVO> holidayInfo,
			AggShiftVO defaultAggShiftVO,
			String date,
			String switchDate,
			TimeZone timeZone
			) throws BusinessException{

		//如果对调日排班了，且对调日遇假日照旧，这种情况不对调
		if(switchCalendar!=null&&!switchCalendar.isHolidayCancel()){
			return;
		}
		//如果没排班或者日期不属于有效的考勤档案日期或者对调日遇假日取消，这种情况下要对调
		beforeExgPkShiftMap.put(date, ShiftVO.PK_GX);//记录当日对调前的班次
		//对调日的班肯定是公休
		cloneDatePkShiftMap.put(switchDate, ShiftVO.PK_GX);//对调日肯定是公休了
		String switchPkShift=null;
		switchPkShift=switchCalendar==null?TACalendarUtils.getPkShiftByDate(switchDate, defaultAggShiftVO):switchCalendar.getPk_shift();
		beforeExgPkShiftMap.put(switchDate, switchPkShift);//记录对调日对调前的班次
		if(ShiftVO.PK_GX.equals(switchPkShift)){
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);//对调日的班次是公休，那么当日也肯定是公休了
			return;
		}
		//对调日班次非公休，则要看对调日的班次的工作时间段（对于弹性班，是最大工作时间段）与假日是否有交集（对于弹性班，最大工作时间段与假日有交集会导致固化，但不一定会导致工作时间段被切割）
		AggShiftVO switchShiftAggVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,switchPkShift);
		processHolidayCut(teamVO, cloneDatePkShiftMap, switchShiftAggVO, holidayInfo.getHolidayVOs(), holidayCutMap, switchDate, timeZone);
	}
	
	/**
	 * 处理一个班次在某天的假日切割情况
	 * 有可能班次与假日完全不搭，这个时候psncalendarvo不用特殊处理。有可能psncalendarvo又需要特殊处理，例如假日切割了工作段，又例如假日导致弹性班固化了
	 * 如果psncalendarvo不用特殊处理，则返回null，否则返回AggPsnCalendar
	 * @param aggShiftVO
	 * @param holidayVOs
	 * @param enjoyHolidayMap
	 * @param date
	 * @param timeZone
	 */
	private void processHolidayCut(TeamHeadVO teamVO,
			Map<String, String> cloneDatePkShiftMap,
			AggShiftVO aggShiftVO,HolidayVO[] holidayVOs,
			Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap,
			String date,TimeZone timeZone){
		// 构造假日全享有
		Map<String, Boolean> holidayEnjoyMap = new HashMap<String,Boolean>();
		for(HolidayVO holidayVO:holidayVOs)
			holidayEnjoyMap.put(holidayVO.getPk_holiday(), true);
		//找出与工作时间有交集的假日
		HolidayVO[] crossedHolidayVOs = TACalendarUtils.findCrossedHolidayVOs(aggShiftVO, holidayVOs, date, timeZone, holidayEnjoyMap);
		//如果假日无交集，则正常处理
		if(ArrayUtils.isEmpty(crossedHolidayVOs)){
			cloneDatePkShiftMap.put(date, aggShiftVO==null?null:aggShiftVO.getShiftVO().getPrimaryKey());
			return;
		}
		Map<String, AggTeamCalendarVO> cutMap = holidayCutMap.get(teamVO.getCteamid());
		if(cutMap==null){
			cutMap = new HashMap<String, AggTeamCalendarVO>();
			holidayCutMap.put(teamVO.getCteamid(),cutMap);
		}
		processHolidayCut2(teamVO, cloneDatePkShiftMap, aggShiftVO, crossedHolidayVOs, cutMap, date, timeZone);
	}
	
	/**
	 * 处理一个班次在某天的假日切割情况。此方法调用时，应该保证工作时间段与假日有交集
	 */
	private void processHolidayCut2(TeamHeadVO teamVO,
			Map<String, String> cloneDatePkShiftMap,
			AggShiftVO shiftVO,HolidayVO[] holidayVOs,
			Map<String, AggTeamCalendarVO> cutMap,
			String date,TimeZone timeZone){
		AggTeamCalendarVO cutCalendar = createHolidayCutAggPsnCalendarVO(teamVO, cloneDatePkShiftMap, shiftVO, holidayVOs, date, timeZone);
		cutMap.put(date, cutCalendar);
	}
	
	/**
	 * 处理一个班次在某天的假日切割情况。此方法调用时，应该保证工作时间段与假日有交集
	 */
	private AggTeamCalendarVO createHolidayCutAggPsnCalendarVO(TeamHeadVO teamVO,
			Map<String, String> cloneDatePkShiftMap,
			AggShiftVO shiftVO,HolidayVO[] holidayVOs,
			String date,TimeZone timeZone){
		AggTeamCalendarVO cutCalendar = TeamCalendarUtils.createHolidayCutAggPsnCalendarVO(teamVO, shiftVO, holidayVOs, date, timeZone);
		cloneDatePkShiftMap.put(date, cutCalendar.getTeamCalendarVO().getPk_shift());
		return cutCalendar;
	}
	
	/**
	 * 将循环排班的结果持久化到数据库，循环排班的时候考虑假日了
	 * 应该将循环排班的结果中已经在数据库中存在的数据删除，再insert循环排班的结果
	 * @param pk_org
	 * @param pk_psndocs
	 * @param modifiedCalendarMap
	 * @param holidayCutMap
	 * @return
	 * @throws BusinessException 
	 */
	private TeamInfoCalendarVO[] circularArrangeWithHolidayPersistence(
			String pk_group,
			String pk_org,
			Map<String, AggShiftVO> shiftMap,//所有的班次
			TeamHeadVO[] teamVOs,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			Map<String, String> originalExpandedDatePkShiftMap,//用户排的原始的班次map，key是日期，value是班次主键，已经按照日期范围展开
			Map<String, Map<String, String>> modifiedCalendarMap,//根据用户的设置生成的排班数据，key是pk_psndoc,value的key是date，value是班次主键
			Map<String, Map<String, String>> psnBeforeExgPkShiftMap,//记录对调前班次的map，key是人员主键，value的key是date，value是班次主键
			Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap,//如果假日产生了作用导致不能用默认规则生成psncalendarvo，则要用这个map里面的psncalendarvo
			Map<String, Map<String, TeamCalendarVO>> psnExistsCalendarMap,//数据库里面已经排好的班次
			boolean isOverrideExistsCalendar
	) throws BusinessException{
		if(ArrayUtils.isEmpty(teamVOs))
			return null;
		List<String> toDelPsnCalendarPk = new ArrayList<String>();//需要在数据库中删除的工作日历
		List<AggTeamCalendarVO> toInsertPsnCalendarVOList = new ArrayList<AggTeamCalendarVO>();//需要insert的工作日历
		Map<String, Map<String, String>> finalCalendarMap = new HashMap<String, Map<String, String>>();//最终的班组工作日历
		//按班组处理
		Set<String> pkTeamSet = new HashSet<>();
		for(TeamHeadVO teamVO:teamVOs){
			Map<String, String> finalShiftMap = new HashMap<String, String>(); //此班组最终的排班结果
			String pk_team = teamVO.getCteamid();
			pkTeamSet.add(pk_team);
			finalCalendarMap.put(pk_team, finalShiftMap);
			Map<String, String> mdfdCalendarMap = modifiedCalendarMap.get(pk_team);//此班组的排班结果
			Map<String, TeamCalendarVO> existsCalendarMap = psnExistsCalendarMap==null?null:psnExistsCalendarMap.get(pk_team);
			Map<String, String> beforeExgPkShiftMap = psnBeforeExgPkShiftMap.get(pk_team);
			Map<String, AggTeamCalendarVO> hldCutMap = holidayCutMap==null?null:holidayCutMap.get(pk_team);
			if(MapUtils.isEmpty(mdfdCalendarMap))
				continue;
			//循环处理每一天
			String[] dates = mdfdCalendarMap.keySet().toArray(new String[0]);
			for(String date:dates){
				if(existsCalendarMap!=null&&existsCalendarMap.get(date)!=null){//如果这一天已经有排班，则要看用户是选择覆盖还是不覆盖
					if(!isOverrideExistsCalendar){//如果不覆盖，则要从mdfdCalendarMap中remove掉这一天的班
						mdfdCalendarMap.remove(date);
						finalShiftMap.put(date, existsCalendarMap.get(date).getPk_shift());//此班次为当天最终班次
						continue;
					}
					// 如果此班组当前天不是由HR控制，则不覆盖
					if(existsCalendarMap.get(date).isManuCtrl()){
						mdfdCalendarMap.remove(date);
						finalShiftMap.put(date, existsCalendarMap.get(date).getPk_shift());//此班次为当天最终班次
						continue;
					}
					//如果覆盖，则需要在数据库中删除掉这一天的排班
					TeamCalendarVO existsCalendar = existsCalendarMap.get(date);
					toDelPsnCalendarPk.add(existsCalendar.getPk_teamcalendar());
				}
				finalShiftMap.put(date, mdfdCalendarMap.get(date));
				//下面构建插入数据库的vo
				AggTeamCalendarVO aggVO = hldCutMap==null?null:hldCutMap.get(date);
				if(aggVO!=null){
					TeamCalendarUtils.setGroupOrgPk2AggVO(pk_group, pk_org, aggVO);
					aggVO.getTeamCalendarVO().setCancelflag(UFBoolean.TRUE);
					toInsertPsnCalendarVOList.add(aggVO);
					continue;
				}
				//代码走到这里，工作日历的两个子表肯定是没数据的
				aggVO=new AggTeamCalendarVO();
				TeamCalendarVO calendarVO=new TeamCalendarVO();
				aggVO.setParentVO(calendarVO);
				calendarVO.setPk_team(pk_team);
				calendarVO.setPk_org_v(teamVO.getPk_org_v());
				calendarVO.setCalendar(UFLiteralDate.getDate(date));
				calendarVO.setPk_shift(mdfdCalendarMap.get(date));
				calendarVO.setOriginal_shift_b4exg(beforeExgPkShiftMap.get(date));
				calendarVO.setCancelflag(UFBoolean.TRUE);
				if(ShiftVO.PK_GX.equals(calendarVO.getPk_shift())||StringUtils.isEmpty(calendarVO.getPk_shift())){
					TeamCalendarUtils.setGX(calendarVO);
				}
				else{
					TeamCalendarUtils.setNonGX(calendarVO, ShiftServiceFacade.getAggShiftVOFromMap(shiftMap, calendarVO.getPk_shift()).getShiftVO());
				}
				TeamCalendarUtils.setGroupOrgPk2AggVO(pk_group, pk_org, aggVO);
				toInsertPsnCalendarVOList.add(aggVO);
			}
		}
		//进行校验：用最终的班次信息校验，所以要覆盖原有班次
		new CalendarShiftMutexChecker().checkCalendar(pk_org, finalCalendarMap, true,true,true);
		try {
			TeamCalendarDAO dao = new TeamCalendarDAO();
			if(toDelPsnCalendarPk.size()>0)
				dao.deleteByPkArray(toDelPsnCalendarPk.toArray(new String[0]));
			if(toInsertPsnCalendarVOList.size()>0){
				AggTeamCalendarVO[] aggvos = toInsertPsnCalendarVOList.toArray(new AggTeamCalendarVO[0]);
				//set日历天类型信息
				aggvos = dealDateType4TeamCalendar(aggvos,pkTeamSet.toArray(new String[0]),pk_org,beginDate,endDate);
				
				dao.insert(aggvos);
				//业务日志
//				TaBusilogUtil.writeCircularlyArrangeteamCalendarBusiLog(pk_org, aggvos);
			}
		} catch (MetaDataException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		// 同步人员工作日历
		NCLocator.getInstance().lookup(IPsnCalendarManageService.class).sync2TeamCalendarAfterCircularlyArrange(pk_org, finalCalendarMap, beginDate, endDate, true);
		String pk_hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		return queryCalendarVOByTeams(pk_hrorg, teamVOs, beginDate, endDate);
	}
	
	/**
	 * 保存时的校验
	 * 此方法在人员工作日历节点保存时调用，由于界面选择的是HR组织，因此有可能同时修改多个业务单元的班次
	 * 此方法取出所有的业务单元，按业务单元进行处理
	 * @param pk_hrorg
	 * @param vos
	 * @throws BusinessException
	 */
	private void checkCalendarWhenSave(String pk_hrorg, TeamInfoCalendarVO[] vos) throws BusinessException{
		//取出vos中所有的业务单元
		Set<String> orgSet = new HashSet<String>();
		for(TeamInfoCalendarVO vo:vos){
			orgSet.add(vo.getPk_org());
		}
		CalendarShiftMutexChecker checker = new CalendarShiftMutexChecker();
		for(String pk_org:orgSet){
			Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String,String>>();
			for(TeamInfoCalendarVO vo:vos){
				if(!pk_org.equals(vo.getPk_org()))
					continue;
				//此人在界面上被修改的工作日历
				Map<String, String> modifiedMap = vo.getModifiedCalendarMap();
				if(MapUtils.isEmpty(modifiedMap))
					continue;
				modifiedCalendarMap.put(vo.getCteamid(), modifiedMap);
			}
			checker.checkCalendar(pk_org, modifiedCalendarMap, true,true, true);//进行校验
		}
	}
	
	@Override
	public TeamInfoCalendarVO[] save(String pk_hrorg, TeamInfoCalendarVO[] vos,boolean busilog)
			throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return vos;
		checkCalendarWhenSave(pk_hrorg, vos);//校验
		TeamCalendarDAO dao = new TeamCalendarDAO();
		try {
			dao.deleteExistsCalendarWhenSave(vos);//删除已有记录
		} catch (DbException e) {
			Logger.error(e.getMessage(),e);
			throw new BusinessException(e.getMessage(),e);
		}
		String pk_group = PubEnv.getPk_group();
		
		// 人员工作日历同步
		NCLocator.getInstance().lookup(IPsnCalendarManageService.class).sync2TeamCalendarAfterSave(pk_hrorg, vos);
		List<AggTeamCalendarVO> insertList = new ArrayList<AggTeamCalendarVO>();
		Map<String, TeamHeadVO> teamMap = CommonUtils.toMap(TeamHeadVO.CTEAMID, NCLocator.getInstance().lookup(ITeamQueryServiceForHR.class).queryBZbyPK(StringPiecer.getStrArray(vos, TeamHeadVO.CTEAMID)));
		//HR组织内所有班次
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_hrorg);
		for(TeamInfoCalendarVO vo:vos){
			if(vo.getModifiedCalendarMap()==null||vo.getModifiedCalendarMap().size()==0)
				continue;
			for(String date:vo.getModifiedCalendarMap().keySet()){
				String pk_shift = vo.getModifiedCalendarMap().get(date);
				vo.getCalendarMap().put(date, pk_shift);
				if(StringUtils.isEmpty(pk_shift))
					continue;
				AggTeamCalendarVO calendarVO = TeamCalendarUtils.createAggVOByShiftVO(teamMap.get(vo.getCteamid()), pk_group, vo.getPk_org(), date,pk_shift, shiftMap, false);
				insertList.add(calendarVO);
			}
			vo.getModifiedCalendarMap().clear();//清空存储修改数据的map
		}
		if(insertList.size()>0){
			AggTeamCalendarVO[] aggvos = insertList.toArray(new AggTeamCalendarVO[0]);
			TeamCalendarVO[] teamcalvos = new TeamCalendarVO[aggvos.length];
			for(int i = 0;i<aggvos.length;i++){
				teamcalvos[i] = aggvos[i].getTeamCalendarVO();
			}
//			InSQLCreator isc = new InSQLCreator();
//			String condition = TeamCalendarVO.PK_TEAMCALENDAR + " in (" + isc.getInSQL(teamcalvos,TeamCalendarVO.PK_TEAMCALENDAR) + ") ";
//			Collection oldc = new BaseDAO().retrieveByClause(TeamCalendarVO.class, condition);
//			if(CollectionUtils.isNotEmpty(oldc))
//				oldvos = (TeamCalendarVO[]) oldc.toArray(new TeamCalendarVO[0]);
			
			dao.insert(aggvos);
			//业务日志
//			TaBusilogUtil.writeEditTeamCalendarBusiLog(pk_hrorg, aggvos,oldaggvos);
			if(busilog)
				TaBusilogUtil.writeEditTeamCalendarBusiLog(teamcalvos, null);
		}
		return vos;
	}

	@Override
	public TeamInfoCalendarVO save(String pk_org, TeamInfoCalendarVO vo)
			throws BusinessException {
		return save(pk_org, new TeamInfoCalendarVO[]{vo},true)[0];
	}

	@Override
	public GeneralVO[] getExportDatas(String pk_hrorg, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		TeamInfoCalendarVO[] calendarVOs = queryCalendarVOByCondition(pk_hrorg, fromWhereSQL, beginDate, endDate);
		if(ArrayUtils.isEmpty(calendarVOs))
			return null;
		return getExportDatas(pk_hrorg, calendarVOs, beginDate, endDate);
	}

	@Override
	public AggTeamCalendarVO queryByTeamDate(String pk_team, UFLiteralDate date)
			throws BusinessException {
		String cond = " pk_team ='"+pk_team+"' and calendar='"+date.toString()+"' ";
		AggTeamCalendarVO returnVO = getServiceTemplate().queryByCondition(AggTeamCalendarVO.class, cond)[0];
		String pk_org = returnVO.getTeamCalendarVO().getPk_org();
		TeamCalHoliday[] holidays = returnVO.getTeamCalHolidayVO();
		if(ArrayUtils.isEmpty(holidays))
			return returnVO;
		
		//查询这一天的时区
		OrgVO orgVO = (OrgVO) new BaseDAO().retrieveByPK(OrgVO.class, pk_org);
		TimeZone timeZone = StringUtils.isEmpty(orgVO.getPk_timezone())?ICalendar.BASE_TIMEZONE:TimezoneUtil.getTimeZone(orgVO.getPk_timezone());
		IHolidayQueryService holidayService = NCLocator.getInstance().lookup(IHolidayQueryService.class);
		for(int i = 0;i<holidays.length;i++){
			HolidayVO holidayVO = holidayService.queryByPk(holidays[i].getPk_holiday());
			if(holidayVO==null)
				continue;
			holidays[i].setBeginTime(new UFDateTime(holidayVO.getStarttime(),timeZone));
			holidays[i].setEndTime(new UFDateTime(holidayVO.getEndtime(),timeZone));
			holidays[i].setHolidayName(holidayVO.getHolidayMultiLangName());
		}
		return returnVO;
	}
	
	/**
	 * 查询人力资源组织下所有班组信息
	 * @param pk_hrorg
	 * @param fromWhereSQL
	 * @return
	 * @throws BusinessException
	 */
	protected TeamHeadVO[] queryTeamVOsByHROrg(String pk_hrorg,FromWhereSQL fromWhereSQL) throws BusinessException {
		if(StringUtils.isEmpty(pk_hrorg))
			return null;
//		OrgVO[] orgVOs = NCLocator.getInstance().lookup(IAOSQueryService.class).queryAOSMembersByHROrgPK(pk_hrorg, false, false);
		OrgVO[] orgVOs =  ShiftServiceFacade.queryOrgsByHROrg(pk_hrorg);
		if(ArrayUtils.isEmpty(orgVOs))
			return null;
		String[] pk_orgs = StringPiecer.getStrArray(orgVOs, OrgVO.PK_ORG);
		return queryTeamVOsByOrgs(pk_orgs, fromWhereSQL);
	}
	
	/**
	 * 查询业务单元下所有班组信息
	 * @param pk_org
	 * @param fromWhereSQL
	 * @return
	 * @throws BusinessException
	 */
	protected TeamHeadVO[] queryTeamVOsByOrgs(String[] pk_orgs, FromWhereSQL fromWhereSQL) throws BusinessException {
		if(ArrayUtils.isEmpty(pk_orgs))
			return null;
		return NCLocator.getInstance().lookup(ITeamQueryServiceForHR.class).queryBZbyWhereStr(fromWhereSQL, pk_orgs);
	}
	
	protected TeamHeadVO[] queryTeamVOsByBzPks(String[] pk_bzs) throws BusinessException {
		if(ArrayUtils.isEmpty(pk_bzs))
			return null;
		return NCLocator.getInstance().lookup(ITeamQueryServiceForHR.class).queryBZbyPK(pk_bzs);
	}

	/**
	 * 查询班组工作日历
	 * @param pk_hrorg 人力资源组织主键
	 */
	@Override
	public TeamInfoCalendarVO[] queryCalendarVOByCondition(String pk_hrorg,
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		TeamHeadVO[] teamVOs = queryTeamVOsByHROrg(pk_hrorg, fromWhereSQL);
		return queryCalendarVOByTeams(pk_hrorg, teamVOs, beginDate, endDate);
	}
	
	//班组工作日历批改时候通过班组主键查询其工作日历
	public TeamInfoCalendarVO[] queryCalendarVOByConditionNew(String pk_hrorg,
			String[] bz_pks, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		TeamHeadVO[] teamVOs = queryTeamVOsByBzPks(bz_pks);
		return queryCalendarVOByTeams(pk_hrorg, teamVOs, beginDate, endDate);
	}
	
	@Override
	public TeamInfoCalendarVO[] queryCalendarVOByPKTeams(String pk_hrorg,
			String[] pk_teams, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(ArrayUtils.isEmpty(pk_teams))
			return null;
		TeamHeadVO[] teamVOs = NCLocator.getInstance().lookup(ITeamQueryServiceForHR.class).queryBZbyPK(pk_teams);
		return queryCalendarVOByTeams(pk_hrorg, teamVOs, beginDate, endDate);
	}
	
	public TeamInfoCalendarVO[] queryCalendarVOByTeams(String pk_hrorg,
			TeamHeadVO[] teamVOs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(ArrayUtils.isEmpty(teamVOs))
			return null;
		// 组装工作日类型Map
		String[] pk_orgs = StringPiecer.getStrArray(teamVOs, TeamHeadVO.PK_ORG);
		IHRHolidayQueryService holidayService = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		Map<String, Map<String, Integer>> dayTypeMap = new HashMap<String, Map<String, Integer>>();
//		for(String pk_org:pk_orgs){
//			// 如果此业务单元已有表示已查询过，就不再查询
//			if(dayTypeMap.get(pk_org)!=null)
//				continue;
//			// 工作日类型
//			Map<String, Integer> dayType = holidayService.queryTeamWorkDayTypeInfo(pk_org, beginDate, endDate);
//			dayTypeMap.put(pk_org, dayType);
//		}
		//得到要修改的pk值
		Set<String> pkTeamVO = new HashSet<>();
		for(TeamHeadVO bzdyHeadVO:teamVOs){
			if(null!=bzdyHeadVO&&null!=bzdyHeadVO.getCteamid()){
				pkTeamVO.add(bzdyHeadVO.getCteamid());
			}
			
		}
		//优化改为批量查询
		//Map<班组pk，<日期，工作日类型>>
		dayTypeMap = holidayService.queryTeamWorkDayTypeInfos4View(pkTeamVO.toArray(new String[0]),pk_orgs, beginDate, endDate);
		// 班组的工作日历
		Map<String, List<TeamCalendarVO>> calendarMap = new TeamCalendarDAO().queryTeamCalendarListMapByPKTeams(SQLHelper.getStrArray(teamVOs, TeamHeadVO.CTEAMID), beginDate, endDate);
		List<TeamInfoCalendarVO> returnList = new ArrayList<TeamInfoCalendarVO>();
		for(TeamHeadVO bzdyHeadVO:teamVOs){
			String pk_team = bzdyHeadVO.getCteamid();
			TeamInfoCalendarVO vo = new TeamInfoCalendarVO();
			vo.setCteamid(pk_team);
			vo.setVteamcode(bzdyHeadVO.getVteamcode());
			vo.setVteamname(bzdyHeadVO.getVteamname());
			vo.setVteamname2(bzdyHeadVO.getVteamname2());
			vo.setVteamname3(bzdyHeadVO.getVteamname3());
			vo.setVteamname4(bzdyHeadVO.getVteamname4());
			vo.setVteamname5(bzdyHeadVO.getVteamname5());
			vo.setVteamname6(bzdyHeadVO.getVteamname6());
			vo.setPk_group(bzdyHeadVO.getPk_group());
			vo.setPk_org(bzdyHeadVO.getPk_org());
			vo.setCdeptid(bzdyHeadVO.getCdeptid());
			vo.setCdeptvid(bzdyHeadVO.getCdeptvid());
			vo.setEnablestate(bzdyHeadVO.getEnablestate());
			// 装入班次和是否HR控制
			if(calendarMap!=null && !CollectionUtils.isEmpty(calendarMap.get(pk_team))) {
				List<TeamCalendarVO> calendarList = calendarMap.get(pk_team);
				for(TeamCalendarVO calendarVO:calendarList){
					vo.getCalendarMap().put(calendarVO.getCalendar().toString(), calendarVO.getPk_shift());
					vo.getCtrlMap().put(calendarVO.getCalendar().toString(), calendarVO.isManuCtrl());
				}
			}
			// 装入工作日类型
			if(dayTypeMap.get(pk_team)!= null)
				vo.getDayTypeMap().putAll(dayTypeMap.get(pk_team));
			returnList.add(vo);
		}
		
		return returnList.toArray(new TeamInfoCalendarVO[0]);
	}

	@Override
	public TeamInfoCalendarVO[] queryCalendarVOByCondition(String pk_hrorg,
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate, QueryScopeEnum queryScope)
			throws BusinessException {
		TeamInfoCalendarVO[] vos = queryCalendarVOByCondition(pk_hrorg, fromWhereSQL, beginDate, endDate);
		//如果是查询所有，则直接返回
		if(vos==null||queryScope==QueryScopeEnum.all)
			return vos;
		List<TeamInfoCalendarVO> returnList = new ArrayList<TeamInfoCalendarVO>();
		//为了效率，分成三个for循环；如果写成一个for循环，则判断次数大增
		//如果是查询部分排班人员，则要判断排班天数是否小于考勤档案有效天数且排班天数大于0
		if(queryScope==QueryScopeEnum.part){
			for(TeamInfoCalendarVO vo:vos){
				if(vo.getCalendarMap().size()<UFLiteralDate.getDaysBetween(beginDate, endDate)+1&&vo.getCalendarMap().size()>0)
					returnList.add(vo);
			}
			return returnList.toArray(new TeamInfoCalendarVO[0]);
		}
		//如果是查询尚未排班人员，则要判断排班天数是否等于0
		if(queryScope==QueryScopeEnum.not){
			for(TeamInfoCalendarVO vo:vos){
				if(vo.getCalendarMap().size()==0)
					returnList.add(vo);
			}
			return returnList.toArray(new TeamInfoCalendarVO[0]);
		}
		//走到这里肯定是查询完全排班人员，只需判断排班天数是否与考勤档案有效天数相等即可
		for(TeamInfoCalendarVO vo:vos){
			if(vo.getCalendarMap().size()==(UFLiteralDate.getDaysBetween(beginDate, endDate)+1))
				returnList.add(vo);
		}
		return returnList.toArray(new TeamInfoCalendarVO[0]);
	}

	@Override
	public TeamHeadVO[] queryTeamVOsByConditionAndOverride(String pk_org,
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate, boolean isOverrideExistCalendar)
			throws BusinessException {
		TeamHeadVO[] teamVOs = queryTeamVOsByOrgs(new String[]{pk_org}, fromWhereSQL);
		//如果是覆盖已有的工作日历，则很简单，查询出最新的班组信息即可
		if(isOverrideExistCalendar)
			return teamVOs;
		// 查询班组工作日历
		Map<String, Map<String, TeamCalendarVO>> calendarMap = new TeamCalendarDAO().queryCalendarVOMapByTeams(SQLHelper.getStrArray(teamVOs, TeamHeadVO.CTEAMID), beginDate, endDate);
		// 工作日历日期数比开始日期到结束日期的日期数小，表示工作日历不完整
		int dateLength = UFLiteralDate.getDaysBetween(beginDate, endDate)+1;
		List<TeamHeadVO> result = new ArrayList<TeamHeadVO>();
		for(TeamHeadVO bzdyHeadVO:teamVOs){
			if(!MapUtils.isEmpty(calendarMap==null?null:calendarMap.get(bzdyHeadVO.getCteamid())) && calendarMap.get(bzdyHeadVO.getCteamid()).keySet().size()==dateLength)
				continue;
			result.add(bzdyHeadVO);
		}
		return CollectionUtils.isEmpty(result)?null:result.toArray(new TeamHeadVO[0]);
	}
	
	//查询用到某个班次的班组工作日历
	public TeamInfoCalendarVO[]  queryByShiftPK(String shiftpk,boolean allField) throws BusinessException {
		String sql = " select top 1 * from BD_TEAMCALENDAR where pk_shift = ? "; 
		if(allField)
			sql+="or original_shift_b4cut = ? or original_shift_b4exg = ? ";
		SQLParameter para = new SQLParameter();
		para.addParam(shiftpk);
		if(allField){
			para.addParam(shiftpk);
			para.addParam(shiftpk);
		}
		@SuppressWarnings("unchecked")
		Collection<TeamInfoCalendarVO> result=(Collection<TeamInfoCalendarVO>)new BaseDAO().executeQuery(sql, para,new BeanListProcessor(TeamInfoCalendarVO.class));
		return CollectionUtils.isEmpty(result)?null:result.toArray(new TeamInfoCalendarVO[0]);
	}

	@Override
	public TeamInfoCalendarVO[] queryCalendarVOByDeptPk(String pk_dept,
			boolean containsSubDept, FromWhereSQL fromWhereSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			QueryScopeEnum queryScope) throws BusinessException {
		fromWhereSQL = PsndocFromWhereSQLPiecer.ensureMainTable(fromWhereSQL, TeamHeadVO.getDefaultTableName(), TeamHeadVO.getDefaultTableName());
		String where = fromWhereSQL.getWhere();
		String deptCond = null;
		if(containsSubDept) {
			DeptVO deptVO = (DeptVO) new BaseDAO().retrieveByPK(DeptVO.class, pk_dept);
			deptCond = TeamHeadVO.CDEPTID + " in (select pk_dept from org_dept where innercode like '" + deptVO.getInnercode() + "%') ";
		}
		else {
			deptCond = TeamHeadVO.CDEPTID + " = '" + pk_dept + "' ";
		}
		where = (StringUtils.isEmpty(where) ? "":(where + " and ")) + deptCond;
		fromWhereSQL = fromWhereSQL instanceof InSQLFromWhereSQL ? new InSQLFromWhereSQL(fromWhereSQL.getFrom(), where, fromWhereSQL, null)
    		: new nc.ui.hr.pub.FromWhereSQL(fromWhereSQL.getFrom(), where, fromWhereSQL, null);
		// 查询部门所属HR组织
		String pk_hrorg = NCLocator.getInstance().lookup(IDeptQueryService.class).queryHrPkOrgByPkDept(pk_dept);
		return queryCalendarVOByCondition(pk_hrorg, fromWhereSQL, beginDate, endDate, queryScope);
	}

	@Override
	public GeneralVO[] getExportDatas(String pk_hrorg,
			TeamInfoCalendarVO[] calendarVOs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if(ArrayUtils.isEmpty(calendarVOs))
			return null;
		
		String[] fields = TeamCalendarCommonValue.createExportFields(beginDate, endDate);
//		OrgVO[] orgVOs = NCLocator.getInstance().lookup(IAOSQueryService.class).queryAOSMembersByHROrgPK(pk_hrorg, false, false);
		OrgVO[] orgVOs =  ShiftServiceFacade.queryOrgsByHROrg(pk_hrorg);
		if(ArrayUtils.isEmpty(orgVOs))
			return null;
//		String[] pk_orgs = StringPiecer.getStrArray(orgVOs, OrgVO.PK_ORG);
		// 查询HR组织下所有业务单元的班组和班组分类信息
		TeamHeadVO[] teamVOs = NCLocator.getInstance().lookup(ITeamQueryServiceForHR.class).queryBZbyPK(StringPiecer.getStrArray(calendarVOs, TeamInfoCalendarVO.CTEAMID));
		Map<String, TeamHeadVO> teamMap = CommonUtils.toMap(TeamHeadVO.CTEAMID, teamVOs);
		
		// 获取当前组织所有班次信息，并用map存储,key是班次主键,value是班次名称
		AggShiftVO[] shiftVOs = ShiftServiceFacade.queryAllByHROrg(pk_hrorg);
		Map<String, String> shiftMap = new HashMap<String,String>();
		shiftMap.put(ShiftVO.PK_GX, ResHelper.getString("6017psncalendar","06017psncalendar0092")/*@res "公休"*/);
		if(!ArrayUtils.isEmpty(shiftVOs)){
			for(AggShiftVO shiftVO:shiftVOs){
				shiftMap.put(shiftVO.getShiftVO().getPk_shift(),shiftVO.getShiftVO().getMultiLangName().toString());
			}
		}
		
		List<GeneralVO> returnVOs = new ArrayList<GeneralVO>();
		for(int i = 0; i < calendarVOs.length;i++){
			GeneralVO exportVO = new GeneralVO();
			// 设置人员信息
			TeamHeadVO bzdyHeadVO = teamMap.get(calendarVOs[i].getCteamid());
			exportVO.setAttributeValue(TeamCalendarCommonValue.LISTCODE_TEAMCODE, bzdyHeadVO.getVteamcode());
			exportVO.setAttributeValue(TeamCalendarCommonValue.LISTCODE_TEAMNAME, MultiLangHelper.getName(bzdyHeadVO));
			for(int j = 2;j < fields.length;j++){
				String pk_shift = calendarVOs[i].getCalendarMap().get(fields[j]);
				if(StringUtils.isEmpty(pk_shift))
					continue;
				exportVO.setAttributeValue(fields[j], shiftMap.get(pk_shift));
			}
			returnVOs.add(exportVO);
		}
		
		return returnVOs.toArray(new GeneralVO[0]);
	}

	@Override
	public TeamHeadVO[] queryTeamVOsByConditionAndOverrideWithOutDate(String pk_org, FromWhereSQL fromWhereSQL) 
			throws BusinessException {
		return queryTeamVOsByOrgs(new String[]{pk_org}, fromWhereSQL);
		
	}
	/**
	 * 批量调换日历天和排班
	 * @param pkTeams 班组信息
	 * @param firstDate 调换日期1
	 * @param secondDate 调换日期2
	 * @author Ares.Tank 2018-9-6 15:15:10
	 */
	@Override
	public void batchChangeDateType(String pk_hrorg, String[] pkTeams, UFLiteralDate firstDate,
			UFLiteralDate secondDate) throws BusinessException {

		if (null==firstDate||null==secondDate||firstDate.equals(secondDate)) {// 相等的话,没什么变的
			return;
		}else if(firstDate.after(secondDate)){
			UFLiteralDate temp = firstDate;
			firstDate = secondDate; 
			secondDate = temp;
		}
		
		TeamCalendarDAO teamCalendarDao = new TeamCalendarDAO();
		
		List<AggTeamCalendarVO> resultList = new ArrayList<>();
		// List<String> resultPkList = new ArrayList<>();//pk存一下,用来删除的
		// 查出这些人的工作日历信息
		Map<String, Map<String, TeamCalendarVO>> forChangeMap = teamCalendarDao.queryCalendarVOMapByTeams(
				pkTeams, firstDate,secondDate);
		
		for (String pkTeamStr : pkTeams) {
			if (null != forChangeMap && null != forChangeMap.get(pkTeamStr)
					&& null != forChangeMap.get(pkTeamStr).get(firstDate.toString())
					&& null != forChangeMap.get(pkTeamStr).get(secondDate.toString())) {
				AggTeamCalendarVO temp = new AggTeamCalendarVO();
				// resultPkList.add(forChangeMap.get(psndocStr).get(firstDate.toString()).getPk_psncalendar());
				forChangeMap.get(pkTeamStr).get(firstDate.toString()).setCalendar(secondDate);
				// forChangeMap.get(psndocStr).get(firstDate.toString()).setPk_psncalendar(null);
				// forChangeMap.get(psndocStr).get(firstDate.toString()).setTs(null);
				temp.setParentVO(forChangeMap.get(pkTeamStr).get(firstDate.toString()));
				resultList.add(temp);

				temp = new AggTeamCalendarVO ();
				// resultPkList.add(forChangeMap.get(psndocStr).get(secondDate.toString()).getPk_psncalendar());
				forChangeMap.get(pkTeamStr).get(secondDate.toString()).setCalendar(firstDate);
				// forChangeMap.get(psndocStr).get(secondDate.toString()).setPk_psncalendar(null);
				// forChangeMap.get(psndocStr).get(secondDate.toString()).setTs(null);

				temp.setParentVO(forChangeMap.get(pkTeamStr).get(secondDate.toString()));
				resultList.add(temp);

			}
		}
		/*
		 * if (resultList.size() > 0){
		 * psnCalendarDAO.deleteByPkArray(resultPkList.toArray(new String[0]));
		 * }
		 */
		//BaseDAO bsDao = new BaseDAO();
		// 由於需要批量交換數據,而且涉及到索引列,先drop索引
		/*try {
			bsDao.executeUpdate("drop INDEX i_psncalendar ON tbm_psncalendar");
		} catch (Exception e) {
			Debug.debug(e.getMessage(), e);

		}*/

		IMDPersistenceService service = MDPersistenceService.lookupPersistenceService();
		String[] attrs = { TeamCalendarVO.CALENDAR };
		service.updateBillWithAttrs(resultList.toArray(new AggTeamCalendarVO[0]), attrs);
		/*// 重啟啟用索引
		String sqlStr = "CREATE UNIQUE INDEX " + "i_psncalendar " + "ON " + "tbm_psncalendar " + "( " + "pk_psndoc, "
				+ "calendar, " + "pk_org " + ") ";
		try {
			bsDao.executeUpdate(sqlStr);
		} catch (Exception e) {
			Debug.debug(e.getMessage(), e);

		}*/
		// psnCalendarDAO.insert(resultList.toArray(new AggPsnCalendar[0]));
		// int i = new BaseDAO().updateVOArray(resultList.toArray(new
		// PsnCalendarVO[0]));
		// Debug.print(i);
	}
	/**
	 * 批量调换日历天和排班（有返回值）
	 * @param pkTeams 班组信息
	 * @param firstDate 调换日期1
	 * @param secondDate 调换日期2
	 * @author he 2018-9-6 15:15:10
	 */
	@Override
	public List<AggTeamCalendarVO> changeDateType(String pk_hrorg, String[] pkTeams, UFLiteralDate firstDate,
			UFLiteralDate secondDate) throws BusinessException {

		if (null==firstDate||null==secondDate||firstDate.equals(secondDate)) {// 相等的话,没什么变的
			return null;
		}else if(firstDate.after(secondDate)){
			UFLiteralDate temp = firstDate;
			firstDate = secondDate; 
			secondDate = temp;
		}
		
		TeamCalendarDAO teamCalendarDao = new TeamCalendarDAO();
		
		List<AggTeamCalendarVO> resultList = new ArrayList<>();
		// List<String> resultPkList = new ArrayList<>();//pk存一下,用来删除的
		// 查出这些人的工作日历信息
		Map<String, Map<String, TeamCalendarVO>> forChangeMap = teamCalendarDao.queryCalendarVOMapByTeams(
				pkTeams, firstDate,secondDate);
		
		for (String pkTeamStr : pkTeams) {
			if (null != forChangeMap && null != forChangeMap.get(pkTeamStr)
					&& null != forChangeMap.get(pkTeamStr).get(firstDate.toString())
					&& null != forChangeMap.get(pkTeamStr).get(secondDate.toString())) {
				AggTeamCalendarVO temp = new AggTeamCalendarVO();
				// resultPkList.add(forChangeMap.get(psndocStr).get(firstDate.toString()).getPk_psncalendar());
				forChangeMap.get(pkTeamStr).get(firstDate.toString()).setCalendar(secondDate);
				// forChangeMap.get(psndocStr).get(firstDate.toString()).setPk_psncalendar(null);
				// forChangeMap.get(psndocStr).get(firstDate.toString()).setTs(null);
				temp.setParentVO(forChangeMap.get(pkTeamStr).get(firstDate.toString()));
				resultList.add(temp);

				temp = new AggTeamCalendarVO ();
				// resultPkList.add(forChangeMap.get(psndocStr).get(secondDate.toString()).getPk_psncalendar());
				forChangeMap.get(pkTeamStr).get(secondDate.toString()).setCalendar(firstDate);
				// forChangeMap.get(psndocStr).get(secondDate.toString()).setPk_psncalendar(null);
				// forChangeMap.get(psndocStr).get(secondDate.toString()).setTs(null);

				temp.setParentVO(forChangeMap.get(pkTeamStr).get(secondDate.toString()));
				resultList.add(temp);

			}
		}
		return resultList;
	}
	/**
	 * 批量变更日历天和排班
	 * @param pkTeams 班组信息
	 * @param date 需要变更的日期
	 * @param 日历天类型,@see HolidayVo 
	 * @author Ares.Tank 2018-9-6 15:15:10
	 */
	@Override
	public void batchChangeDateType4OneDay(String pk_hrorg, String[] pkTeams, UFLiteralDate changeDate,
			Integer dateType) throws BusinessException {
		if (null == pkTeams || null == changeDate) {//
			return;

		}
		TeamCalendarDAO teamCalendarDao = new TeamCalendarDAO();
		//UFLiteralDate[] ufLDates = { changeDate };
		List<AggTeamCalendarVO> resultList = new ArrayList<>();
		// List<String> resultPkList = new ArrayList<>();//pk存一下,用来删除的
		// 查出这些人的工作日历信息
		Map<String, Map<String, TeamCalendarVO>> forChangeMap = teamCalendarDao.queryCalendarVOMapByTeams(
				pkTeams, changeDate,changeDate);
		for (String pkTeamStr : pkTeams) {
			if (null != forChangeMap && null != forChangeMap.get(pkTeamStr)
					&& null != forChangeMap.get(pkTeamStr).get(changeDate.toString())) {
				AggTeamCalendarVO  temp = new AggTeamCalendarVO ();
				forChangeMap.get(pkTeamStr).get(changeDate.toString()).setDate_daytype(dateType);
				temp.setParentVO(forChangeMap.get(pkTeamStr).get(changeDate.toString()));
				resultList.add(temp);
				// resultPkList.add(forChangeMap.get(psndocStr).get(changeDate).getPk_psncalendar());
			}
		}
		/*
		 * if (resultList.size() > 0){
		 * psnCalendarDAO.deleteByPkArray(resultPkList.toArray(new String[0]));
		 * }
		 */

		IMDPersistenceService service = MDPersistenceService.lookupPersistenceService();
		String[] attrs = { TeamCalendarVO.DATE_DAYTYPE };
		service.updateBillWithAttrs(resultList.toArray(new AggTeamCalendarVO[0]), attrs);

		// psnCalendarDAO.insert(resultList.toArray(new AggPsnCalendar[0]));

	}

}
