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
		if(!withOldShift){//���������ԭ���
			TeamHeadVO[] teamVOs = queryTeamVOsByOrgs(new String[]{pk_org}, fromWhereSQL);
			String[] pk_teams = SQLHelper.getStrArray(teamVOs, TeamHeadVO.CTEAMID);
			if(ArrayUtils.isEmpty(pk_teams))
				return;
			if(StringUtils.isEmpty(newShift)){
				new TeamCalendarDAO().deleteByTeamsAndDateArea(pk_teams, beginDate, endDate);
				return;
			}
			//����°�β��գ�������������Ű�������һ���ѭ���Ű࣬�Ҹ���ԭ�а�Σ����������վ�
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
				// ������鵱�칤����������HR�����򲻴���
				if(calendarVO.getCtrlMap()!=null && calendarVO.getCtrlMap().get(date.toString())!=null && calendarVO.getCtrlMap().get(date.toString()).booleanValue())
					continue;
				//���date�����Ű࣬�Ұ����������oldShift������date��δ�Ű࣬��oldShiftΪ�գ�����Ϊ����oldShift��ƥ������
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
		//ҵ����־
		TaBusilogUtil.writeBatchEditTeamCalendarBusiLog(pk_org, bz_pks, beginDate, endDate, oldShift, newShift);
		if(!withOldShift){//���������ԭ���
			//TeamHeadVO[] teamVOs = queryTeamVOsByBzPks(bz_pks);
			//String[] pk_teams = SQLHelper.getStrArray(teamVOs, TeamHeadVO.CTEAMID);
			if(ArrayUtils.isEmpty(bz_pks))
				return;
			if(StringUtils.isEmpty(newShift)){
				new TeamCalendarDAO().deleteByTeamsAndDateArea(bz_pks, beginDate, endDate);
				return;
			}
			//����°�β��գ�������������Ű�������һ���ѭ���Ű࣬�Ҹ���ԭ�а�Σ����������վ�
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
				// ������鵱�칤����������HR�����򲻴���
				if(calendarVO.getCtrlMap()!=null && calendarVO.getCtrlMap().get(date.toString())!=null && calendarVO.getCtrlMap().get(date.toString()).booleanValue())
					continue;
				//���date�����Ű࣬�Ұ����������oldShift������date��δ�Ű࣬��oldShiftΪ�գ�����Ϊ����oldShift��ƥ������
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
		
		//���ȼ�У��һ��ѭ���Ű�İ���Ƿ��ͻ��
		new CalendarShiftMutexChecker().simpleCheckCircularArrange(pk_org, beginDate, endDate, calendarPks, true);
		String pk_group = PubEnv.getPk_group();
		if(isHolidayCancel){
			HolidayInfo<HRHolidayVO> holidayInfo = NCLocator.getInstance().lookup(IHRHolidayQueryService.class).queryHolidayInfo(pk_org, beginDate, endDate);
			if(holidayInfo!=null)
				// ������ȡ�������м���
				return circularArrangeWithHoliday(pk_group, pk_org, pk_teams, beginDate, endDate, calendarPks, overrideExistCalendar, holidayInfo);
		}
		// �������վ� ��������ȡ�����޼���
		return circularArrangeIgnoreHoliday(pk_group, pk_org, pk_teams, beginDate, endDate, calendarPks, isHolidayCancel, overrideExistCalendar);
	}

	/**
	 * ѭ���Ű࣬�����Ǽ���
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
		//��ѭ���İ�ΰ����ڷ�Χȫ��չ��
		Map<String, String> pkMap = TACalendarUtils.expandCalendar2MapByDateArea(beginDate, endDate, calendarPks);
		//���������˵��Ű�map��У�鷽����Ҫ
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
		//��֯�����а��
		Map<String, AggShiftVO> shiftMap = NCLocator.getInstance().lookup(IShiftQueryService.class).queryShiftAggVOMapByOrg(pk_org);
		//���й�������
		Map<String, TeamInfoCalendarVO> existTeamCalendarMap = CommonUtils.toMap(TeamHeadVO.CTEAMID, queryCalendarVOByPKTeams(pk_hrorg, pk_teams, beginDate, endDate));
		List<AggTeamCalendarVO> insertList = new ArrayList<AggTeamCalendarVO>();
		// ���չ�������
		Map<String, Map<String, String>> finalCalendarMap = new HashMap<String, Map<String,String>>();

		Map<String, TeamHeadVO> teamMap = CommonUtils.toMap(TeamHeadVO.CTEAMID, NCLocator.getInstance().lookup(ITeamQueryServiceForHR.class).queryBZbyPK(pk_teams));
		for(String pk_team:pk_teams){
			//���հ����Ϣ
			Map<String, String> finalShiftMap = new HashMap<String, String>();
			finalCalendarMap.put(pk_team, finalShiftMap);
			for(UFLiteralDate date:allDates){
				//����û�ѡ�񲻸��ǣ��ҵ������й�����������Ҳ��insert
				if(!overrideExistCalendar){
					if(existTeamCalendarMap.get(pk_team).getCalendarMap()!=null&&existTeamCalendarMap.get(pk_team).getCalendarMap().containsKey(date.toString())){
						finalShiftMap.put(date.toString(), existTeamCalendarMap.get(pk_team).getCalendarMap().get(date.toString()));
						continue;
					}
				}
				// ����˰��鵱ǰ�첻����HR���ƣ���insert
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
		if(insertList.size()==0)//���ֱ��return null����ɽ���ʲô��û��ʾ��
			return queryCalendarVOByPKTeams(pk_hrorg, pk_teams, beginDate, endDate);
			//return null;
		//����У�飺�����յİ����ϢУ�飬����Ҫ����ԭ�а��
		new CalendarShiftMutexChecker().checkCalendar(pk_org, finalCalendarMap, true,true, true);
		if(overrideExistCalendar)//����û�ѡ�񸲸������Ű࣬����Ҫ�����ڷ�Χ�ڵĹ�������ɾ����
			dao.deleteByTeamsAndDateArea(pk_teams, beginDate, endDate);
		AggTeamCalendarVO[] aggvos = insertList.toArray(new AggTeamCalendarVO[0]);
		//������������set��Aggvo Ares.Tank 2018-9-7 18:28:21
		aggvos = dealDateType4TeamCalendar(aggvos,pk_teams,pk_hrorg,beginDate,endDate);
		dao.insert(aggvos);
		//ҵ����־
//		TaBusilogUtil.writeCircularlyArrangeteamCalendarBusiLog(pk_hrorg, aggvos);
		// ͬ����Ա��������
		NCLocator.getInstance().lookup(IPsnCalendarManageService.class).sync2TeamCalendarAfterCircularlyArrange(pk_org, finalCalendarMap, beginDate, endDate, false);
		return queryCalendarVOByPKTeams(pk_hrorg, pk_teams, beginDate, endDate);
	}
	/**
	 * �Ű��ʱ��ʹ��
	 * ��dateType set �� ���鹤������,
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
		//��ѯĬ�ϵ�����������
		IHRHolidayQueryService holidayService = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		Map<String, Integer> orgDateIntegerMap = new HashMap<>();//����֯Ĭ�ϵ�����������<����,����������>
		try {
			orgDateIntegerMap = 
					holidayService.queryTeamWorkDayTypeInfo(pk_hrorg, beginDate, endDate);
		} catch (BusinessException e) {
			orgDateIntegerMap = new HashMap<>();
			Debug.debug(e);
			e.printStackTrace();
		}
		for(AggTeamCalendarVO aggVO : aggvos){//set������
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
	 * ���Ǽ��յ�ѭ���Ű�
	 * @param pk_org,ҵ��Ԫ����
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
		//�洢Ҫ���õİ�ε�map��key����Ա������value��key��date��value�ǰ������.���ǵ��п��ܲ�����εĶԵ����������modifiedCalendarMap������ڷ�Χ���ܻᳬ��begindate��enddate�ķ�Χ
		Map<String, Map<String, String>> modifiedCalendarMap = new HashMap<String, Map<String,String>>();
		//��ѭ���İ�ΰ����ڷ�Χȫ��չ����key�����ڣ�value�ǰ������
		Map<String, String> originalExpandedDatePkShiftMap = TACalendarUtils.expandCalendar2MapByDateArea(beginDate, endDate, calendarPks);
		UFLiteralDate[] dateArea = CommonUtils.createDateArray(beginDate, endDate);
		//Ĭ�ϰ�
		AggShiftVO defaultShift = ShiftServiceFacade.queryDefaultShiftAggVOByOrg(pk_org);
		//ҵ��Ԫ�����а��
		Map<String, AggShiftVO> shiftMap = ShiftServiceFacade.queryShiftAggVOMapByOrg(pk_org);
		//������и�İ�ε�map��key����Ա��value��key�����ڣ�value��psncalendar�ľۺ�vo.ֻ�м��ն��Ű������Ӱ�죬�����и��ˣ����ߵ��µ��԰�̻��ˣ��Ŵ浽��map��ȥ���������죬���߱���ȫ�и�ɹ��ݵĲ����
		Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap = new HashMap<String, Map<String,AggTeamCalendarVO>>();
		//��¼�Ե�ǰ��ε�map��key����Ա��value��key�����ڣ�value�ǶԵ�ǰ�İ��
		Map<String, Map<String, String>> psnBeforeExgPkShiftMap = new HashMap<String, Map<String,String>>();
		//ҵ��Ԫ��ʱ��
		TimeZone timeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(pk_org);
		//��Ա�Ѿ��źõİ��,��Χ��min(beginDate,�Ե���,����).getDateBefore(1)��max(endDate,�Ե��գ�����).getDateAfter(1)
		Set<String> holidayDateSet = new HashSet<String>();//�洢���ա��Ե��յ�map��key�����ڣ���������������ȷ����ѯ���������ķ�Χ
		holidayDateSet.addAll(holidayInfo.getHolidayMap().keySet());
		holidayDateSet.addAll(holidayInfo.getSwitchMap().keySet());
		String[] allDates = holidayDateSet.toArray(new String[0]);//�Լ��ա��Ե�������
		Arrays.sort(allDates);
		//��ѯ����������������ڷ�Χ
		UFLiteralDate calendarQueryBeginDate = allDates[0].compareTo(beginDate.toString())<0?UFLiteralDate.getDate(allDates[0]).getDateBefore(1):beginDate.getDateBefore(1);
		UFLiteralDate calendarQueryEndDate = allDates[allDates.length-1].compareTo(endDate.toString())>0?UFLiteralDate.getDate(allDates[allDates.length-1]).getDateAfter(1):endDate.getDateAfter(1);
		Map<String, Map<String, TeamCalendarVO>> psnExistsCalendarMap = new TeamCalendarDAO().queryCalendarVOMapByTeams(pk_teams, calendarQueryBeginDate, calendarQueryEndDate);
		Map<String, TeamHeadVO> teamMap = CommonUtils.toMap(TeamHeadVO.CTEAMID, NCLocator.getInstance().lookup(ITeamQueryServiceForHR.class).queryBZbyPK(pk_teams));
		//������ѭ������
		for(int i=0;i<pk_teams.length;i++){
			Map<String, String> cloneDatePkShiftMap = new HashMap<String, String>(originalExpandedDatePkShiftMap);//ÿ���˵�������һ�ݣ���Ϊ��Ȼ��ѭ���Ű࣬��������Ű���������ÿ���˶���ͬ
			Map<String, String> beforeExgPkShiftMap = new HashMap<String, String>();
			String pk_team = pk_teams[i];
			modifiedCalendarMap.put(pk_team, cloneDatePkShiftMap);
			psnBeforeExgPkShiftMap.put(pk_team, beforeExgPkShiftMap);
			//�Լ�����������(��Ϊ�漰���Ե���������Щ���������ǰ��ʹ�������)
			Set<String> processedDateSet = new HashSet<String>();
			//ѭ�������û����õ�ѭ�����
			for(int dateIndex=0;dateIndex<dateArea.length;dateIndex++){
				String date = dateArea[dateIndex].toString();//����
				if(processedDateSet.contains(date))//���������ٴ���
					continue;
				processedDateSet.add(date);
				String pk_shift = originalExpandedDatePkShiftMap.get(date);//��Σ���Ϊ��ѭ���Ű࣬��˲�����Ϊ��
				//����ǹ���
				if(ShiftVO.PK_GX.equals(pk_shift)){//ֱ�ӵ��ù��ݵ��Ű෽��
					circularArrangeWithHolidayGX(teamMap.get(pk_team), beginDate, endDate, 
							cloneDatePkShiftMap, originalExpandedDatePkShiftMap,beforeExgPkShiftMap,
							psnExistsCalendarMap==null?null:psnExistsCalendarMap.get(pk_team),shiftMap, 
							holidayCutMap, processedDateSet, holidayInfo, defaultShift,date, timeZone);
					continue;
				}
				//������ǹ��ݣ�ֱ�ӵ��÷ǹ��ݵ��Ű෽��
				circularArrangeWithHolidayNonGX(teamMap.get(pk_team), calendarQueryBeginDate, 
						calendarQueryEndDate, cloneDatePkShiftMap, originalExpandedDatePkShiftMap, beforeExgPkShiftMap,
						psnExistsCalendarMap==null?null:psnExistsCalendarMap.get(pk_team), 
						shiftMap, holidayCutMap, processedDateSet, holidayInfo, defaultShift, date, timeZone);
			}
		}
		//���ˣ����еİ඼���꣬��ص��Ű���Ϣ�����ˣ�
		//1.modifiedCalendarMap���û�Ҫ���õİ�ε�map��
		//2.holidayCutMap������Ű�Ĺ�������Ϊ���յ���psncalendarvoҪ���⴦����������map
		//����У�飺
//		new CalendarShiftMutexChecker().checkCalendar(pk_org, modifiedCalendarMap, overrideExistCalendar,true,true);
		//У��ͨ�����־û������ݿ�
		return circularArrangeWithHolidayPersistence(pk_group, pk_org, shiftMap, teamMap.values().toArray(new TeamHeadVO[0]), 
					beginDate, endDate, originalExpandedDatePkShiftMap, 
					modifiedCalendarMap, psnBeforeExgPkShiftMap, holidayCutMap, psnExistsCalendarMap,overrideExistCalendar);
	}
	
	/**
	 * ���Ǽ��յ�ѭ���Ű࣬����İ���û���Ϊ�˷ǹ���
	 * @throws BusinessException 
	 */
	private void circularArrangeWithHolidayNonGX(
			TeamHeadVO teamVO,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			Map<String, String> cloneDatePkShiftMap,
			Map<String, String> originalExpandedDatePkShiftMap,//�û��ŵ�ԭʼ�İ��map��key�����ڣ�value�ǰ���������Ѿ��������ڷ�Χչ��
			Map<String, String> beforeExgPkShiftMap,//��������˶Ե�����map��¼�Ե�ǰ�İ�Σ�key��date
			Map<String, TeamCalendarVO> existsCalendarMap,//���ݿ����Ѿ��ŵĹ�������
			Map<String, AggShiftVO> shiftMap,//ҵ��Ԫ�����еİ��
			Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap,
			Set<String> processedDateSet,//�Ѿ�������������set
			HolidayInfo<HRHolidayVO> holidayInfo,
			AggShiftVO defaultAggShiftVO,
			String date,
			TimeZone timeZone
	) throws BusinessException{

		String pk_shift = originalExpandedDatePkShiftMap.get(date);
		//������첻�ǶԵ��գ������ǶԵ��յ����˲����ܼ��գ����û������Ű࣬�������⴦��
		if(!holidayInfo.getSwitchMap().containsKey(date)){
			processHolidayCut(teamVO, cloneDatePkShiftMap, shiftMap.get(pk_shift), holidayInfo.getHolidayVOs(), holidayCutMap, date, timeZone);
			return;
		}
		String switchDate = holidayInfo.getSwitchMap().get(date);//�Ե���
		processedDateSet.add(switchDate);//�Ե��մ��ϴ�����־
		//���Ե����Ƿ������ڷ�Χ��,����ڷ�Χ�ڣ���ô�Ե��ǿ϶�Ҫ�����ģ�
		if(switchDate.compareTo(beginDate.toString())>=0&&switchDate.compareTo(endDate.toString())<=0){	
			exchangeDateInDateAreaNonGX(teamVO, cloneDatePkShiftMap, pk_shift, originalExpandedDatePkShiftMap.get(switchDate), beforeExgPkShiftMap,shiftMap, holidayCutMap, holidayInfo, date, switchDate, timeZone);
			return;
		}
		//����Ե����ڴ˴����ð�ε����ڷ�Χ֮�⣬����Ҫ���Ե����ڵ��Ű�״�������
		//1.���Ű࣬���Ű�Ϊ������ȡ�����������Ե����߼�һ��
		//2.���Ű࣬���Ű�Ϊ������վɣ��򲻶Ե�
		//3.���Ű࣬���Զ��Ե�������һ��Ĭ�ϰࣨ��һ�����壩�����߹��ݣ������գ���Ȼ��ִ�жԵ����Ե��Ľ�������춼��������ȡ��
		//ע�⣺����Ե������ڿ��ڵ�����Ч�����ڣ����ͬ�����3
		//1,3���Ժϲ�������3���Կ���1��һ������
		//��������ŵİ����ȡ���Ե��յİ�
		TeamCalendarVO switchCalendar = existsCalendarMap==null?null:existsCalendarMap.get(switchDate);
		exchangeDateOutDateAreaNonGX(teamVO, cloneDatePkShiftMap,pk_shift, switchCalendar, beforeExgPkShiftMap,
				shiftMap, holidayCutMap, holidayInfo, 
				defaultAggShiftVO, date, switchDate, timeZone);
	}
	
	
	/**
	 * �����Ե����Ե�����ѭ���Ű�����ڷ�Χ֮�⣬���û����������˷ǹ���
	 * ��exchangeDateInDateAreaNonGX������һ�ԣ�һ�������Ե����ڷ�Χ�ڣ�һ�������Ե����ڷ�Χ��
	 * @throws BusinessException 
	 */
	private void exchangeDateOutDateAreaNonGX(
			TeamHeadVO teamVO,
			Map<String, String> cloneDatePkShiftMap,
			String originalPkShift,
			TeamCalendarVO switchCalendar,
			Map<String, String> beforeExgPkShiftMap,//��������˶Ե�����map��¼�Ե�ǰ�İ�Σ�key��date
			Map<String, AggShiftVO> shiftMap,
			Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap,//���PsnCalendarVO��Ҫ���⴦�����򽫴�������������map
			HolidayInfo<HRHolidayVO> holidayInfo,
			AggShiftVO defaultAggShiftVO,
			String date,
			String switchDate,
			TimeZone timeZone
			) throws BusinessException{
		//����Ե����Ű��ˣ��ҶԵ���������Ч�Ŀ��ڵ������ҶԵ����������վɣ�����������Ե�
		if(switchCalendar!=null&&!switchCalendar.isHolidayCancel()){
			processHolidayCut(teamVO, cloneDatePkShiftMap, ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,originalPkShift), holidayInfo.getHolidayVOs(), holidayCutMap, date, timeZone);
			return;
		}
		beforeExgPkShiftMap.put(date, originalPkShift);//���նԵ�ǰ�İ��
		//���û�Ű���߶Ե���������ȡ��
		//�Ե��յİ�϶��ŵ��յİ�,��������Ե�
		processHolidayCut(teamVO, cloneDatePkShiftMap, ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,originalPkShift), holidayInfo.getHolidayVOs(), holidayCutMap, switchDate, timeZone);
		String switchPkShift=switchCalendar==null?TACalendarUtils.getPkShiftByDate(switchDate, defaultAggShiftVO):switchCalendar.getPk_shift();
		beforeExgPkShiftMap.put(switchDate, switchPkShift);//�Ե��նԵ�ǰ�İ��
		if(ShiftVO.PK_GX.equals(switchPkShift)){
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);//�Ե��յİ���ǹ��ݣ���ô����Ҳ�϶��ǹ�����
			return;
		}
		//�Ե��հ�ηǹ��ݣ���Ҫ���Ե��յİ�εĹ���ʱ��Σ����ڵ��԰࣬�������ʱ��Σ�������Ƿ��н��������ڵ��԰࣬�����ʱ���������н����ᵼ�¹̻�������һ���ᵼ�¹���ʱ��α��и
		AggShiftVO switchShiftAggVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,switchPkShift);
		processHolidayCut(teamVO, cloneDatePkShiftMap, switchShiftAggVO, holidayInfo.getHolidayVOs(), holidayCutMap, switchDate, timeZone);
	}
	
	/**
	 * �����Ե����Ե�����ѭ���Ű�����ڷ�Χ�ڣ����û����������˷ǹ���
	 * ��exchangeDateOutDateAreaNonGX������һ�ԣ�һ�������Ե����ڷ�Χ�ڣ�һ�������Ե����ڷ�Χ��
	 * @throws BusinessException 
	 */
	private void exchangeDateInDateAreaNonGX(
			TeamHeadVO teamVO,
			Map<String, String> cloneDatePkShiftMap,
			String originalPkShift,//�����û��ŵ�ԭʼ�İ��
			String originalSwitchDayPkShift,//�Ե����û��ŵ�ԭʼ�İ��
			Map<String, String> beforeExgPkShiftMap,//��������˶Ե�����map��¼�Ե�ǰ�İ�Σ�key��date
			Map<String, AggShiftVO> shiftMap,
			Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap,
			HolidayInfo<HRHolidayVO> holidayInfo,
			String date,
			String switchDate,
			TimeZone timeZone
			) throws BusinessException{
		beforeExgPkShiftMap.put(date, originalPkShift);//��¼���նԵ�ǰ�İ��
		beforeExgPkShiftMap.put(switchDate, originalSwitchDayPkShift);//��¼�Ե��նԵ�ǰ�İ��
		//����Ե��յİ���ǹ��ݣ���ô����Ҳ�Ź��ݣ�
		if(ShiftVO.PK_GX.equals(originalSwitchDayPkShift)){
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);
		}
		else{//�Ե��ղ��ǹ��ݣ���ô���Ե��յİ�ε������գ�Ȼ�������հ������յĽ�������
			processHolidayCut(teamVO, cloneDatePkShiftMap, ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,originalSwitchDayPkShift), holidayInfo.getHolidayVOs(), holidayCutMap, date, timeZone);
		}
		//�Ե��յİ��ҲҪ����
		processHolidayCut(teamVO, cloneDatePkShiftMap, ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,originalPkShift), holidayInfo.getHolidayVOs(), holidayCutMap, switchDate, timeZone);
		return;
	}
	
	/**
	 * ���Ǽ��յ�ѭ���Ű࣬����İ���û���Ϊ�˹���
	 * @throws BusinessException 
	 */
	private void circularArrangeWithHolidayGX(
			TeamHeadVO teamVO,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			Map<String, String> cloneDatePkShiftMap,//���������ֵ
			Map<String, String> originalExpandedDatePkShiftMap,//�û��ŵ�ԭʼ�İ��map��key�����ڣ�value�ǰ���������Ѿ��������ڷ�Χչ������������ȡֵ
			Map<String, String> beforeExgPkShiftMap,//��������˶Ե�����map��¼�Ե�ǰ�İ�Σ�key��date�����������ֵ
			Map<String, TeamCalendarVO> existsCalendarMap,//���ݿ����Ѿ��ŵĹ�����������������ȡֵ
			Map<String, AggShiftVO> shiftMap,//��֯�����еİ�Σ���������ȡֵ
			Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap,//���������ֵ
			Set<String> processedDateSet,//�Ѿ�������������set������ȡֵ��Ҳ��ֵ
			HolidayInfo<HRHolidayVO> holidayInfo,
			AggShiftVO defaultShift,
			String date,
			TimeZone timeZone
	) throws BusinessException{

		//������첻�漰���Ե��������漰�Ե������˲����ܴ˼��գ�������Ȼ�ǹ���,����Ӱ��
		if(!holidayInfo.getSwitchMap().containsKey(date))//���첻�漰�Ե�
			return;
		//����漰���Ե�����Ҫ���Ե��յİ�Σ�
		String switchDate = holidayInfo.getSwitchMap().get(date);
		processedDateSet.add(switchDate);
		//����Ե����ڴ˴����ð�ε����ڷ�Χ�ڣ��򣺵����ŶԵ��յİࣨ����ǹ�������������У���Ҫ��ȥ���գ����Ե����Ź���
		if(switchDate.compareTo(beginDate.toString())>=0&&switchDate.compareTo(endDate.toString())<=0){
			exchangeDateInDateAreaGX(teamVO, cloneDatePkShiftMap, 
					originalExpandedDatePkShiftMap.get(switchDate),beforeExgPkShiftMap, shiftMap, 
					holidayCutMap, holidayInfo, date, switchDate, timeZone);
			return;
		}
		//����Ե����ڴ˴����ð�ε����ڷ�Χ֮�⣬����Ҫ���Ե����ڵ��Ű�״�������
		//1.���Ű࣬���Ű�Ϊ������ȡ�����������Ե����߼�һ��
		//2.���Ű࣬���Ű�Ϊ������վɣ��򲻶Ե�
		//3.���Ű࣬���Զ��Ե�������һ��Ĭ�ϰࣨ��һ�����壩�����߹��ݣ������գ���Ȼ��ִ�жԵ����Ե��Ľ�������춼��������ȡ��
		//ע�⣺����Ե������ڿ��ڵ�����Ч�����ڣ����ͬ�����3
		//1,3���Ժϲ�������3���Կ���1��һ������
		//��������ŵİ����ȡ���Ե��յİ�
		TeamCalendarVO switchCalendar = existsCalendarMap==null?null:existsCalendarMap.get(switchDate);
		exchangeDateOutDateAreaGX(teamVO, cloneDatePkShiftMap, switchCalendar, beforeExgPkShiftMap,
				shiftMap, holidayCutMap, holidayInfo, 
				defaultShift, date, switchDate, timeZone);
	}
	
	/**
	 * �����Ե����Ե�����ѭ���Ű�����ڷ�Χ�ڣ����û����������˹���
	 * ��exchangeDateOutDateAreaGX������һ�ԣ�һ�������Ե����ڷ�Χ�ڣ�һ�������Ե����ڷ�Χ��
	 * @throws BusinessException 
	 */
	private void exchangeDateInDateAreaGX(
			TeamHeadVO teamVO,
			Map<String, String> cloneDatePkShiftMap,
			String originalSwitchDayPkShift,//�Ե����û��ŵ�ԭʼ�İ��
			Map<String, String> beforeExgPkShiftMap,//��������˶Ե�����map��¼�Ե�ǰ�İ�Σ�key��date
			Map<String, AggShiftVO> shiftMap,
			Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap,
			HolidayInfo<HRHolidayVO> holidayInfo,
			String date,
			String switchDate,
			TimeZone timeZone
			) throws BusinessException{
		beforeExgPkShiftMap.put(date, ShiftVO.PK_GX);//��¼���նԵ�ǰ�İ��
		beforeExgPkShiftMap.put(switchDate, originalSwitchDayPkShift);//��¼�Ե��նԵ�ǰ�İ��
		cloneDatePkShiftMap.put(switchDate, ShiftVO.PK_GX);//�Ե��տ϶��ǹ�����
		if(ShiftVO.PK_GX.equals(originalSwitchDayPkShift)){
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);//�Ե��յİ���ǹ��ݣ���ô����Ҳ�϶��ǹ�����
			return;
		}
		//�Ե��հ�ηǹ��ݣ���Ҫ���Ե��յİ�εĹ���ʱ��Σ����ڵ��԰࣬�������ʱ��Σ�������Ƿ��н��������ڵ��԰࣬�����ʱ���������н����ᵼ�¹̻�������һ���ᵼ�¹���ʱ��α��и
		AggShiftVO switchShiftAggVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,originalSwitchDayPkShift);
		processHolidayCut(teamVO, cloneDatePkShiftMap, switchShiftAggVO, 
				holidayInfo.getHolidayVOs(), holidayCutMap, 
				date, timeZone);
	}
	
	/**
	 * �����Ե����Ե�����ѭ���Ű�����ڷ�Χ֮�⣬���û����������˹���
	 * ��exchangeDateInDateAreaGX������һ�ԣ�һ�������Ե����ڷ�Χ�ڣ�һ�������Ե����ڷ�Χ��
	 * @throws BusinessException 
	 */
	private void exchangeDateOutDateAreaGX(
			TeamHeadVO teamVO,
			Map<String, String> cloneDatePkShiftMap,
			TeamCalendarVO switchCalendar,
			Map<String, String> beforeExgPkShiftMap,//��������˶Ե�����map��¼�Ե�ǰ�İ�Σ�key��date
			Map<String, AggShiftVO> shiftMap,
			Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap,//���PsnCalendarVO��Ҫ���⴦�����򽫴�������������map
			HolidayInfo<HRHolidayVO> holidayInfo,
			AggShiftVO defaultAggShiftVO,
			String date,
			String switchDate,
			TimeZone timeZone
			) throws BusinessException{

		//����Ե����Ű��ˣ��ҶԵ����������վɣ�����������Ե�
		if(switchCalendar!=null&&!switchCalendar.isHolidayCancel()){
			return;
		}
		//���û�Ű�������ڲ�������Ч�Ŀ��ڵ������ڻ��߶Ե���������ȡ�������������Ҫ�Ե�
		beforeExgPkShiftMap.put(date, ShiftVO.PK_GX);//��¼���նԵ�ǰ�İ��
		//�Ե��յİ�϶��ǹ���
		cloneDatePkShiftMap.put(switchDate, ShiftVO.PK_GX);//�Ե��տ϶��ǹ�����
		String switchPkShift=null;
		switchPkShift=switchCalendar==null?TACalendarUtils.getPkShiftByDate(switchDate, defaultAggShiftVO):switchCalendar.getPk_shift();
		beforeExgPkShiftMap.put(switchDate, switchPkShift);//��¼�Ե��նԵ�ǰ�İ��
		if(ShiftVO.PK_GX.equals(switchPkShift)){
			cloneDatePkShiftMap.put(date, ShiftVO.PK_GX);//�Ե��յİ���ǹ��ݣ���ô����Ҳ�϶��ǹ�����
			return;
		}
		//�Ե��հ�ηǹ��ݣ���Ҫ���Ե��յİ�εĹ���ʱ��Σ����ڵ��԰࣬�������ʱ��Σ�������Ƿ��н��������ڵ��԰࣬�����ʱ���������н����ᵼ�¹̻�������һ���ᵼ�¹���ʱ��α��и
		AggShiftVO switchShiftAggVO = ShiftServiceFacade.getAggShiftVOFromMap(shiftMap,switchPkShift);
		processHolidayCut(teamVO, cloneDatePkShiftMap, switchShiftAggVO, holidayInfo.getHolidayVOs(), holidayCutMap, switchDate, timeZone);
	}
	
	/**
	 * ����һ�������ĳ��ļ����и����
	 * �п��ܰ���������ȫ������ʱ��psncalendarvo�������⴦�����п���psncalendarvo����Ҫ���⴦������������и��˹����Σ���������յ��µ��԰�̻���
	 * ���psncalendarvo�������⴦�����򷵻�null�����򷵻�AggPsnCalendar
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
		// �������ȫ����
		Map<String, Boolean> holidayEnjoyMap = new HashMap<String,Boolean>();
		for(HolidayVO holidayVO:holidayVOs)
			holidayEnjoyMap.put(holidayVO.getPk_holiday(), true);
		//�ҳ��빤��ʱ���н����ļ���
		HolidayVO[] crossedHolidayVOs = TACalendarUtils.findCrossedHolidayVOs(aggShiftVO, holidayVOs, date, timeZone, holidayEnjoyMap);
		//��������޽���������������
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
	 * ����һ�������ĳ��ļ����и�������˷�������ʱ��Ӧ�ñ�֤����ʱ���������н���
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
	 * ����һ�������ĳ��ļ����и�������˷�������ʱ��Ӧ�ñ�֤����ʱ���������н���
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
	 * ��ѭ���Ű�Ľ���־û������ݿ⣬ѭ���Ű��ʱ���Ǽ�����
	 * Ӧ�ý�ѭ���Ű�Ľ�����Ѿ������ݿ��д��ڵ�����ɾ������insertѭ���Ű�Ľ��
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
			Map<String, AggShiftVO> shiftMap,//���еİ��
			TeamHeadVO[] teamVOs,
			UFLiteralDate beginDate, UFLiteralDate endDate,
			Map<String, String> originalExpandedDatePkShiftMap,//�û��ŵ�ԭʼ�İ��map��key�����ڣ�value�ǰ���������Ѿ��������ڷ�Χչ��
			Map<String, Map<String, String>> modifiedCalendarMap,//�����û����������ɵ��Ű����ݣ�key��pk_psndoc,value��key��date��value�ǰ������
			Map<String, Map<String, String>> psnBeforeExgPkShiftMap,//��¼�Ե�ǰ��ε�map��key����Ա������value��key��date��value�ǰ������
			Map<String, Map<String, AggTeamCalendarVO>> holidayCutMap,//������ղ��������õ��²�����Ĭ�Ϲ�������psncalendarvo����Ҫ�����map�����psncalendarvo
			Map<String, Map<String, TeamCalendarVO>> psnExistsCalendarMap,//���ݿ������Ѿ��źõİ��
			boolean isOverrideExistsCalendar
	) throws BusinessException{
		if(ArrayUtils.isEmpty(teamVOs))
			return null;
		List<String> toDelPsnCalendarPk = new ArrayList<String>();//��Ҫ�����ݿ���ɾ���Ĺ�������
		List<AggTeamCalendarVO> toInsertPsnCalendarVOList = new ArrayList<AggTeamCalendarVO>();//��Ҫinsert�Ĺ�������
		Map<String, Map<String, String>> finalCalendarMap = new HashMap<String, Map<String, String>>();//���յİ��鹤������
		//�����鴦��
		Set<String> pkTeamSet = new HashSet<>();
		for(TeamHeadVO teamVO:teamVOs){
			Map<String, String> finalShiftMap = new HashMap<String, String>(); //�˰������յ��Ű���
			String pk_team = teamVO.getCteamid();
			pkTeamSet.add(pk_team);
			finalCalendarMap.put(pk_team, finalShiftMap);
			Map<String, String> mdfdCalendarMap = modifiedCalendarMap.get(pk_team);//�˰�����Ű���
			Map<String, TeamCalendarVO> existsCalendarMap = psnExistsCalendarMap==null?null:psnExistsCalendarMap.get(pk_team);
			Map<String, String> beforeExgPkShiftMap = psnBeforeExgPkShiftMap.get(pk_team);
			Map<String, AggTeamCalendarVO> hldCutMap = holidayCutMap==null?null:holidayCutMap.get(pk_team);
			if(MapUtils.isEmpty(mdfdCalendarMap))
				continue;
			//ѭ������ÿһ��
			String[] dates = mdfdCalendarMap.keySet().toArray(new String[0]);
			for(String date:dates){
				if(existsCalendarMap!=null&&existsCalendarMap.get(date)!=null){//�����һ���Ѿ����Ű࣬��Ҫ���û���ѡ�񸲸ǻ��ǲ�����
					if(!isOverrideExistsCalendar){//��������ǣ���Ҫ��mdfdCalendarMap��remove����һ��İ�
						mdfdCalendarMap.remove(date);
						finalShiftMap.put(date, existsCalendarMap.get(date).getPk_shift());//�˰��Ϊ�������հ��
						continue;
					}
					// ����˰��鵱ǰ�첻����HR���ƣ��򲻸���
					if(existsCalendarMap.get(date).isManuCtrl()){
						mdfdCalendarMap.remove(date);
						finalShiftMap.put(date, existsCalendarMap.get(date).getPk_shift());//�˰��Ϊ�������հ��
						continue;
					}
					//������ǣ�����Ҫ�����ݿ���ɾ������һ����Ű�
					TeamCalendarVO existsCalendar = existsCalendarMap.get(date);
					toDelPsnCalendarPk.add(existsCalendar.getPk_teamcalendar());
				}
				finalShiftMap.put(date, mdfdCalendarMap.get(date));
				//���湹���������ݿ��vo
				AggTeamCalendarVO aggVO = hldCutMap==null?null:hldCutMap.get(date);
				if(aggVO!=null){
					TeamCalendarUtils.setGroupOrgPk2AggVO(pk_group, pk_org, aggVO);
					aggVO.getTeamCalendarVO().setCancelflag(UFBoolean.TRUE);
					toInsertPsnCalendarVOList.add(aggVO);
					continue;
				}
				//�����ߵ�������������������ӱ��϶���û���ݵ�
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
		//����У�飺�����յİ����ϢУ�飬����Ҫ����ԭ�а��
		new CalendarShiftMutexChecker().checkCalendar(pk_org, finalCalendarMap, true,true,true);
		try {
			TeamCalendarDAO dao = new TeamCalendarDAO();
			if(toDelPsnCalendarPk.size()>0)
				dao.deleteByPkArray(toDelPsnCalendarPk.toArray(new String[0]));
			if(toInsertPsnCalendarVOList.size()>0){
				AggTeamCalendarVO[] aggvos = toInsertPsnCalendarVOList.toArray(new AggTeamCalendarVO[0]);
				//set������������Ϣ
				aggvos = dealDateType4TeamCalendar(aggvos,pkTeamSet.toArray(new String[0]),pk_org,beginDate,endDate);
				
				dao.insert(aggvos);
				//ҵ����־
//				TaBusilogUtil.writeCircularlyArrangeteamCalendarBusiLog(pk_org, aggvos);
			}
		} catch (MetaDataException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		// ͬ����Ա��������
		NCLocator.getInstance().lookup(IPsnCalendarManageService.class).sync2TeamCalendarAfterCircularlyArrange(pk_org, finalCalendarMap, beginDate, endDate, true);
		String pk_hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		return queryCalendarVOByTeams(pk_hrorg, teamVOs, beginDate, endDate);
	}
	
	/**
	 * ����ʱ��У��
	 * �˷�������Ա���������ڵ㱣��ʱ���ã����ڽ���ѡ�����HR��֯������п���ͬʱ�޸Ķ��ҵ��Ԫ�İ��
	 * �˷���ȡ�����е�ҵ��Ԫ����ҵ��Ԫ���д���
	 * @param pk_hrorg
	 * @param vos
	 * @throws BusinessException
	 */
	private void checkCalendarWhenSave(String pk_hrorg, TeamInfoCalendarVO[] vos) throws BusinessException{
		//ȡ��vos�����е�ҵ��Ԫ
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
				//�����ڽ����ϱ��޸ĵĹ�������
				Map<String, String> modifiedMap = vo.getModifiedCalendarMap();
				if(MapUtils.isEmpty(modifiedMap))
					continue;
				modifiedCalendarMap.put(vo.getCteamid(), modifiedMap);
			}
			checker.checkCalendar(pk_org, modifiedCalendarMap, true,true, true);//����У��
		}
	}
	
	@Override
	public TeamInfoCalendarVO[] save(String pk_hrorg, TeamInfoCalendarVO[] vos,boolean busilog)
			throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return vos;
		checkCalendarWhenSave(pk_hrorg, vos);//У��
		TeamCalendarDAO dao = new TeamCalendarDAO();
		try {
			dao.deleteExistsCalendarWhenSave(vos);//ɾ�����м�¼
		} catch (DbException e) {
			Logger.error(e.getMessage(),e);
			throw new BusinessException(e.getMessage(),e);
		}
		String pk_group = PubEnv.getPk_group();
		
		// ��Ա��������ͬ��
		NCLocator.getInstance().lookup(IPsnCalendarManageService.class).sync2TeamCalendarAfterSave(pk_hrorg, vos);
		List<AggTeamCalendarVO> insertList = new ArrayList<AggTeamCalendarVO>();
		Map<String, TeamHeadVO> teamMap = CommonUtils.toMap(TeamHeadVO.CTEAMID, NCLocator.getInstance().lookup(ITeamQueryServiceForHR.class).queryBZbyPK(StringPiecer.getStrArray(vos, TeamHeadVO.CTEAMID)));
		//HR��֯�����а��
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
			vo.getModifiedCalendarMap().clear();//��մ洢�޸����ݵ�map
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
			//ҵ����־
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
		
		//��ѯ��һ���ʱ��
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
	 * ��ѯ������Դ��֯�����а�����Ϣ
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
	 * ��ѯҵ��Ԫ�����а�����Ϣ
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
	 * ��ѯ���鹤������
	 * @param pk_hrorg ������Դ��֯����
	 */
	@Override
	public TeamInfoCalendarVO[] queryCalendarVOByCondition(String pk_hrorg,
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		TeamHeadVO[] teamVOs = queryTeamVOsByHROrg(pk_hrorg, fromWhereSQL);
		return queryCalendarVOByTeams(pk_hrorg, teamVOs, beginDate, endDate);
	}
	
	//���鹤����������ʱ��ͨ������������ѯ�乤������
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
		// ��װ����������Map
		String[] pk_orgs = StringPiecer.getStrArray(teamVOs, TeamHeadVO.PK_ORG);
		IHRHolidayQueryService holidayService = NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		Map<String, Map<String, Integer>> dayTypeMap = new HashMap<String, Map<String, Integer>>();
//		for(String pk_org:pk_orgs){
//			// �����ҵ��Ԫ���б�ʾ�Ѳ�ѯ�����Ͳ��ٲ�ѯ
//			if(dayTypeMap.get(pk_org)!=null)
//				continue;
//			// ����������
//			Map<String, Integer> dayType = holidayService.queryTeamWorkDayTypeInfo(pk_org, beginDate, endDate);
//			dayTypeMap.put(pk_org, dayType);
//		}
		//�õ�Ҫ�޸ĵ�pkֵ
		Set<String> pkTeamVO = new HashSet<>();
		for(TeamHeadVO bzdyHeadVO:teamVOs){
			if(null!=bzdyHeadVO&&null!=bzdyHeadVO.getCteamid()){
				pkTeamVO.add(bzdyHeadVO.getCteamid());
			}
			
		}
		//�Ż���Ϊ������ѯ
		//Map<����pk��<���ڣ�����������>>
		dayTypeMap = holidayService.queryTeamWorkDayTypeInfos4View(pkTeamVO.toArray(new String[0]),pk_orgs, beginDate, endDate);
		// ����Ĺ�������
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
			// װ���κ��Ƿ�HR����
			if(calendarMap!=null && !CollectionUtils.isEmpty(calendarMap.get(pk_team))) {
				List<TeamCalendarVO> calendarList = calendarMap.get(pk_team);
				for(TeamCalendarVO calendarVO:calendarList){
					vo.getCalendarMap().put(calendarVO.getCalendar().toString(), calendarVO.getPk_shift());
					vo.getCtrlMap().put(calendarVO.getCalendar().toString(), calendarVO.isManuCtrl());
				}
			}
			// װ�빤��������
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
		//����ǲ�ѯ���У���ֱ�ӷ���
		if(vos==null||queryScope==QueryScopeEnum.all)
			return vos;
		List<TeamInfoCalendarVO> returnList = new ArrayList<TeamInfoCalendarVO>();
		//Ϊ��Ч�ʣ��ֳ�����forѭ�������д��һ��forѭ�������жϴ�������
		//����ǲ�ѯ�����Ű���Ա����Ҫ�ж��Ű������Ƿ�С�ڿ��ڵ�����Ч�������Ű���������0
		if(queryScope==QueryScopeEnum.part){
			for(TeamInfoCalendarVO vo:vos){
				if(vo.getCalendarMap().size()<UFLiteralDate.getDaysBetween(beginDate, endDate)+1&&vo.getCalendarMap().size()>0)
					returnList.add(vo);
			}
			return returnList.toArray(new TeamInfoCalendarVO[0]);
		}
		//����ǲ�ѯ��δ�Ű���Ա����Ҫ�ж��Ű������Ƿ����0
		if(queryScope==QueryScopeEnum.not){
			for(TeamInfoCalendarVO vo:vos){
				if(vo.getCalendarMap().size()==0)
					returnList.add(vo);
			}
			return returnList.toArray(new TeamInfoCalendarVO[0]);
		}
		//�ߵ�����϶��ǲ�ѯ��ȫ�Ű���Ա��ֻ���ж��Ű������Ƿ��뿼�ڵ�����Ч������ȼ���
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
		//����Ǹ������еĹ�����������ܼ򵥣���ѯ�����µİ�����Ϣ����
		if(isOverrideExistCalendar)
			return teamVOs;
		// ��ѯ���鹤������
		Map<String, Map<String, TeamCalendarVO>> calendarMap = new TeamCalendarDAO().queryCalendarVOMapByTeams(SQLHelper.getStrArray(teamVOs, TeamHeadVO.CTEAMID), beginDate, endDate);
		// ���������������ȿ�ʼ���ڵ��������ڵ�������С����ʾ��������������
		int dateLength = UFLiteralDate.getDaysBetween(beginDate, endDate)+1;
		List<TeamHeadVO> result = new ArrayList<TeamHeadVO>();
		for(TeamHeadVO bzdyHeadVO:teamVOs){
			if(!MapUtils.isEmpty(calendarMap==null?null:calendarMap.get(bzdyHeadVO.getCteamid())) && calendarMap.get(bzdyHeadVO.getCteamid()).keySet().size()==dateLength)
				continue;
			result.add(bzdyHeadVO);
		}
		return CollectionUtils.isEmpty(result)?null:result.toArray(new TeamHeadVO[0]);
	}
	
	//��ѯ�õ�ĳ����εİ��鹤������
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
		// ��ѯ��������HR��֯
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
		// ��ѯHR��֯������ҵ��Ԫ�İ���Ͱ��������Ϣ
		TeamHeadVO[] teamVOs = NCLocator.getInstance().lookup(ITeamQueryServiceForHR.class).queryBZbyPK(StringPiecer.getStrArray(calendarVOs, TeamInfoCalendarVO.CTEAMID));
		Map<String, TeamHeadVO> teamMap = CommonUtils.toMap(TeamHeadVO.CTEAMID, teamVOs);
		
		// ��ȡ��ǰ��֯���а����Ϣ������map�洢,key�ǰ������,value�ǰ������
		AggShiftVO[] shiftVOs = ShiftServiceFacade.queryAllByHROrg(pk_hrorg);
		Map<String, String> shiftMap = new HashMap<String,String>();
		shiftMap.put(ShiftVO.PK_GX, ResHelper.getString("6017psncalendar","06017psncalendar0092")/*@res "����"*/);
		if(!ArrayUtils.isEmpty(shiftVOs)){
			for(AggShiftVO shiftVO:shiftVOs){
				shiftMap.put(shiftVO.getShiftVO().getPk_shift(),shiftVO.getShiftVO().getMultiLangName().toString());
			}
		}
		
		List<GeneralVO> returnVOs = new ArrayList<GeneralVO>();
		for(int i = 0; i < calendarVOs.length;i++){
			GeneralVO exportVO = new GeneralVO();
			// ������Ա��Ϣ
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
	 * ����������������Ű�
	 * @param pkTeams ������Ϣ
	 * @param firstDate ��������1
	 * @param secondDate ��������2
	 * @author Ares.Tank 2018-9-6 15:15:10
	 */
	@Override
	public void batchChangeDateType(String pk_hrorg, String[] pkTeams, UFLiteralDate firstDate,
			UFLiteralDate secondDate) throws BusinessException {

		if (null==firstDate||null==secondDate||firstDate.equals(secondDate)) {// ��ȵĻ�,ûʲô���
			return;
		}else if(firstDate.after(secondDate)){
			UFLiteralDate temp = firstDate;
			firstDate = secondDate; 
			secondDate = temp;
		}
		
		TeamCalendarDAO teamCalendarDao = new TeamCalendarDAO();
		
		List<AggTeamCalendarVO> resultList = new ArrayList<>();
		// List<String> resultPkList = new ArrayList<>();//pk��һ��,����ɾ����
		// �����Щ�˵Ĺ���������Ϣ
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
		// �����Ҫ�������Q����,�����漰��������,��drop����
		/*try {
			bsDao.executeUpdate("drop INDEX i_psncalendar ON tbm_psncalendar");
		} catch (Exception e) {
			Debug.debug(e.getMessage(), e);

		}*/

		IMDPersistenceService service = MDPersistenceService.lookupPersistenceService();
		String[] attrs = { TeamCalendarVO.CALENDAR };
		service.updateBillWithAttrs(resultList.toArray(new AggTeamCalendarVO[0]), attrs);
		/*// �؆���������
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
	 * ����������������Űࣨ�з���ֵ��
	 * @param pkTeams ������Ϣ
	 * @param firstDate ��������1
	 * @param secondDate ��������2
	 * @author he 2018-9-6 15:15:10
	 */
	@Override
	public List<AggTeamCalendarVO> changeDateType(String pk_hrorg, String[] pkTeams, UFLiteralDate firstDate,
			UFLiteralDate secondDate) throws BusinessException {

		if (null==firstDate||null==secondDate||firstDate.equals(secondDate)) {// ��ȵĻ�,ûʲô���
			return null;
		}else if(firstDate.after(secondDate)){
			UFLiteralDate temp = firstDate;
			firstDate = secondDate; 
			secondDate = temp;
		}
		
		TeamCalendarDAO teamCalendarDao = new TeamCalendarDAO();
		
		List<AggTeamCalendarVO> resultList = new ArrayList<>();
		// List<String> resultPkList = new ArrayList<>();//pk��һ��,����ɾ����
		// �����Щ�˵Ĺ���������Ϣ
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
	 * ���������������Ű�
	 * @param pkTeams ������Ϣ
	 * @param date ��Ҫ���������
	 * @param ����������,@see HolidayVo 
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
		// List<String> resultPkList = new ArrayList<>();//pk��һ��,����ɾ����
		// �����Щ�˵Ĺ���������Ϣ
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