package nc.impl.ta.teamcalendar;

import java.util.Map;
import java.util.TimeZone;

import nc.impl.ta.calendar.TACalendarUtils;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.ta.algorithm.CompleteCheckTimeScopeUtils;
import nc.itf.ta.algorithm.ICompleteCheckTimeScope;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.TimeScopeUtils;
import nc.vo.bd.holiday.HolidayVO;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.bd.shift.WTVO;
import nc.vo.bd.team.team01.entity.TeamHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.teamcalendar.AggTeamCalendarVO;
import nc.vo.ta.teamcalendar.TeamCalHoliday;
import nc.vo.ta.teamcalendar.TeamCalendarVO;
import nc.vo.ta.teamcalendar.TeamWorkTimeVO;
import nc.vo.util.AuditInfoUtil;

import org.apache.commons.lang.ArrayUtils;

public class TeamCalendarUtils {
	
	/**
	 * ����һ�������ĳ��ļ����и�������˷�������ʱ��Ӧ�ñ�֤����ʱ���������н���
	 */
	public static AggTeamCalendarVO createHolidayCutAggPsnCalendarVO(
			TeamHeadVO teamVO,
			AggShiftVO aggShiftVO,
			HolidayVO[] holidayVOs,
			String date,
			TimeZone timeZone){
		AggTeamCalendarVO cutCalendar = TeamCalendarUtils.cutHoliday(aggShiftVO, holidayVOs, date, timeZone);
		cutCalendar.getTeamCalendarVO().setPk_team(teamVO.getCteamid());
		cutCalendar.getTeamCalendarVO().setPk_org_v(teamVO.getPk_org_v());
		return cutCalendar;
	}
	
	/**
	 * ����һ���������յ��и���������ڵ��԰࣬�п��ܴ����Ľ���ǹ̻��ˣ�����û���и���̻���Ĺ�����
	 * @param aggShiftVO
	 * @param holidayVOs
	 * @param date
	 * @param timeZone
	 * @param pk_psndoc
	 * @return
	 */
	public static AggTeamCalendarVO cutHoliday(AggShiftVO aggShiftVO,HolidayVO[] holidayVOs,String date,TimeZone timeZone){
		TeamCalendarVO mainVO = new TeamCalendarVO();
		mainVO.setCalendar(UFLiteralDate.getDate(date));
		mainVO.setCancelflag(UFBoolean.TRUE);//����������и�Ŀ϶���������ȡ��
		AggTeamCalendarVO aggVO = new AggTeamCalendarVO();
		aggVO.setParentVO(mainVO);
		
		TeamCalHoliday[] calHolVOs = new TeamCalHoliday[holidayVOs.length];
		for(int i=0;i<calHolVOs.length;i++){
			calHolVOs[i]=new TeamCalHoliday();
			calHolVOs[i].setPk_holiday(holidayVOs[i].getPk_holiday());
		}
		aggVO.setTeamCalHolidayVO(calHolVOs);
		ITimeScope[] holidayScopes = HolidayVO.toTimeScopes(holidayVOs, timeZone);//����ʱ���
		ITimeScope[] remainsWorkScopes = TimeScopeUtils.minusTimeScopes(aggShiftVO.toMaxWorkTimeScope(date, timeZone), holidayScopes);//����ʱ��μ�ȥ����ʱ���
		if(ArrayUtils.isEmpty(remainsWorkScopes)){//��������ɶ����ʣ�ˣ���ô���տ϶���Ϊ����
			TeamCalendarUtils.setGX(mainVO);
			mainVO.setOriginal_shift_b4cut(aggShiftVO.getShiftVO().getPk_shift());//�и�ǰ�İ��
			return aggVO;
		}
		//�������󣬻�ʣһ�㣬����Ҫ�����������
		//����ǹ̶��࣬��ܼ򵥣�ʣ�µľ������չ���ʱ��Σ�
		//����ǵ��԰࣬��Ҫ������ʱ���Ƿ��ܸ���ĳһ�����ܵ���������ʱ��Σ�ע�ⲻ�����Ĺ���ʱ��Σ����п��ܼ��ղ��ܸ������Ĺ���ʱ��Σ����ܸ���ĳһ�����ܵ���������ʱ��Σ�������ܸ��ǣ��������Ȼ����Ϊ���ݣ�������ܸ�������һ�����ܵĹ���ʱ��Σ���Ҫ���м��̻���ʱ�乤��ʱ��μ�ȥ����ʱ�Σ�ʣ�µĲ������չ���ʱ���
		if(!aggShiftVO.getShiftVO().isFlexibleFinal()){//����ǹ̶���
			mainVO.setPk_shift(aggShiftVO.getShiftVO().getPrimaryKey());
			TeamWorkTimeVO[] workTimeVOs = transfer2WorkTimeVOs(remainsWorkScopes,aggShiftVO,date,timeZone);
			aggVO.setTeamWorkTimeVO(workTimeVOs);
			mainVO.setGzsj(new UFDouble(TimeScopeUtils.getLength(workTimeVOs)/3600.0));
			mainVO.setIf_rest(UFBoolean.FALSE);
			mainVO.setIswtrecreate(UFBoolean.TRUE);
			mainVO.setIsflexiblefinal(UFBoolean.FALSE);
			return aggVO;
		}
		//�����ǵ��԰�
		if(TACalendarUtils.isHolidayCanOverrideACompleteShift(aggShiftVO, holidayScopes, date, timeZone)){//�������ʱ��ο��Ը���һ�������Ĺ���ʱ��Σ�������Ϊ����
			TeamCalendarUtils.setGX(mainVO);
			mainVO.setOriginal_shift_b4cut(aggShiftVO.getShiftVO().getPk_shift());//�и�ǰ�İ��
			return aggVO;
		}
		//������ܸ��ǣ����Ȱ��м�ʱ���̻�����ʱ��Σ���ζ��屣���ʱ�򣬶��ڵ��԰�Ĺ���ʱ����Ѿ������м仯����������ֱ���ã���Ȼ�󿴹̻����ʱ�����
		//�����Ƿ��н���������޽���������һ����Ű಻��Ӱ�졣����н�������Ҫ�����и�
		//ע�⣬�����ߵ�������԰��Ѿ��϶�Ҫ���м��̻��ˣ�ֻ�����̻���Ĺ���ʱ����Ƿ�ᱻ�����и��ȷ������Ҫ��һ���ж�
		WTVO[] wtVOs = aggShiftVO.getWTVOs();//�Ѿ��ǰ��м��̻����˵�
		mainVO.setPk_shift(aggShiftVO.getShiftVO().getPrimaryKey());
		ITimeScope[] midWorkScopes =  TimeScopeUtils.toTimeScope(wtVOs, date, timeZone);
		//����������м��̻��Ĺ������޽���
		if(!TimeScopeUtils.isCross(holidayScopes,midWorkScopes)){
			mainVO.setIswtrecreate(UFBoolean.FALSE);
			mainVO.setGzsj(aggShiftVO.getShiftVO().getGzsj());
			mainVO.setIf_rest(UFBoolean.FALSE);
			mainVO.setIsflexiblefinal(UFBoolean.FALSE);
			return aggVO;
		}
		//����н��������м�㹤���μ�ȥ���գ���Ϊ���յĹ���ʱ���
		remainsWorkScopes = TimeScopeUtils.minusTimeScopes(midWorkScopes, holidayScopes);
		TeamWorkTimeVO[] workTimeVOs = transfer2WorkTimeVOs(remainsWorkScopes,aggShiftVO,date,timeZone);
		aggVO.setTeamWorkTimeVO(workTimeVOs);
		mainVO.setGzsj(new UFDouble(TimeScopeUtils.getLength(workTimeVOs)/3600.0));
		mainVO.setIf_rest(UFBoolean.FALSE);
		mainVO.setIswtrecreate(UFBoolean.TRUE);
		mainVO.setIsflexiblefinal(UFBoolean.FALSE);
		return aggVO;
		
	}
	
	/**
	 * ����ĳһ����Ű࣬�������и�������������null�����ʾ���и�������߼�����teamcalendar���ݼ���
	 * */
	public static AggTeamCalendarVO createAggTeamCalendarByShiftAndHolidayNullWhenNotCut(
			AggShiftVO aggShiftVO,String date,TimeZone timeZone,
			HolidayVO[] holidayVOs,
			Map<String, Boolean> enjoyHolidayMap){
		HolidayVO[] crossedHolidayVOs = TACalendarUtils.findCrossedHolidayVOs(aggShiftVO, holidayVOs, date, timeZone, enjoyHolidayMap);
		if(ArrayUtils.isEmpty(crossedHolidayVOs))
			return null;
		return cutHoliday(aggShiftVO, crossedHolidayVOs, date, timeZone);
		
	}
	
	/**
	 * ���������и���ʱ���ת��ΪPsnWortTimeVO[]���飬��Ҫ�������е����°��Ƿ���Ҫˢ�����ϰ�ˢ���Ρ��°�ˢ���ο�ʼ����ʱ�����Ϣ
	 * @param timeScopes
	 * @param aggShiftVO
	 * @return
	 */
	private static TeamWorkTimeVO[] transfer2WorkTimeVOs(ITimeScope[] timeScopes,AggShiftVO aggShiftVO,String date,TimeZone timeZone){
		TeamWorkTimeVO[] workTimeVOs = new TeamWorkTimeVO[timeScopes.length];
		WTVO[] wtVOs = aggShiftVO.getWTVOs();
		ITimeScope[] wtScopes = TimeScopeUtils.toTimeScope(wtVOs, date, timeZone);
		//TODO:��������ʱ���ӱ�
		for(int i=0;i<workTimeVOs.length;i++){
			workTimeVOs[i]=new TeamWorkTimeVO(timeScopes[i]);
			workTimeVOs[i].setTimeid(i);
			//���漸���ֶζ��ô������ؼ��Ǻ����ksfromtime��kstotime��jsfromtime��jstotime��checkinflag��checkoutflag
			//checkinflag��checkoutflag�Ĵ����߼����£�������ѭ����
			//��һ��ѭ��ʱ�򵥴�����PsnWortTimeVO��checkinflag��checkoutflag������������ԭʼ�Ĺ���ʱ���
			//�ڶ���ѭ��ʱ������У�飺�����һ���ε��ϰ಻ˢ������ҪУ����ˢ����������һ�ε��°಻ˢ������ҪУ����ˢ��
			//���������ε�ǰ���°ࡢ����ϰ಻ƥ�䣬������Ϊˢ��
			for(int j=0;j<wtVOs.length;j++){
				if(TimeScopeUtils.contains(wtScopes[j], workTimeVOs[i])){
					workTimeVOs[i].setCheckinflag(wtVOs[j].getCheckinflag());//ˢ����־��������ԭʼ����ʱ���
					workTimeVOs[i].setCheckoutflag(wtVOs[j].getCheckoutflag());
					break;
				}
			}
		}
		if(workTimeVOs[0].getCheckinflag()==null||!workTimeVOs[0].getCheckinflag().booleanValue())//�����һ��ѭ���Ľ���ǵ�һ�ε��ϰ಻ˢ������ҪУ������
			workTimeVOs[0].setCheckinflag(UFBoolean.TRUE);
		if(workTimeVOs[workTimeVOs.length-1].getCheckoutflag()==null||!workTimeVOs[workTimeVOs.length-1].getCheckoutflag().booleanValue())//�����һ��ѭ���Ľ�������һ�ε��°಻ˢ������ҪУ������
			workTimeVOs[workTimeVOs.length-1].setCheckoutflag(UFBoolean.TRUE);
		//�ڶ���ѭ�������������ν���У�飬����ǰ��ƥ������
		if(workTimeVOs.length>1)
			for(int i=0;i<workTimeVOs.length-1;i++){
				boolean lastCheckoutflag=workTimeVOs[i].getCheckoutflag()!=null&&workTimeVOs[i].getCheckoutflag().booleanValue();
				boolean nextCheckinflag=workTimeVOs[i+1].getCheckinflag()!=null&&workTimeVOs[i+1].getCheckinflag().booleanValue();
				if(lastCheckoutflag!=nextCheckinflag){//���ǰһ���°�ͺ�һ���ϰ��ˢ����־��һ�£��򶼵���Ϊ����ˢ��
					workTimeVOs[i].setCheckoutflag(UFBoolean.TRUE);
					workTimeVOs[i+1].setCheckinflag(UFBoolean.TRUE);
				}
			}
		//���ˣ��Ƿ���Ҫˢ���ı�־������ϣ����濪ʼ�����ϰ�ˢ���εĿ�ʼ������ʱ����°�ˢ���εĿ�ʼ����ʱ��
		ITimeScope kqScope = aggShiftVO.getShiftVO().toKqScope(date, timeZone);
		//���������ʱ��κϲ���ˢ���Σ�������ˢ���ε��ϰ�ˢ���Ρ��°�ˢ���εĿ�ʼ����ʱ��
		ICompleteCheckTimeScope[] checkTimeScopes = CompleteCheckTimeScopeUtils
		.mergeWorkTime(kqScope.getScope_start_datetime(), kqScope.getScope_end_datetime(), workTimeVOs);
		for(ICompleteCheckTimeScope checkTimeScope:checkTimeScopes){
			workTimeVOs[checkTimeScope.getCheckinScopeTimeID()].setKsfromtime(checkTimeScope.getKsfromtime());
			workTimeVOs[checkTimeScope.getCheckinScopeTimeID()].setKstotime(checkTimeScope.getKstotime());
			workTimeVOs[checkTimeScope.getCheckoutScopeTimeID()].setJsfromtime(checkTimeScope.getKsfromtime());
			workTimeVOs[checkTimeScope.getCheckoutScopeTimeID()].setJstotime(checkTimeScope.getJstotime());
		}
		
		return workTimeVOs;
	}
	
	/**
	 * ���ݰ����������AggTeamCalendarVO
	 * @param pk_psndoc
	 * @param pk_group
	 * @param pk_org ҵ��Ԫ����
	 * @param date
	 * @param pk_shift
	 * @param shiftVOMap
	 * @param isHolidayCancel
	 * @return
	 * @throws BusinessException
	 */
	public static AggTeamCalendarVO createAggVOByShiftVO(TeamHeadVO teamVO,String pk_group,String pk_org,String date,
			String pk_shift,Map<String, AggShiftVO> shiftVOMap,boolean isHolidayCancel) throws BusinessException{
		TeamCalendarVO calendarVO = new TeamCalendarVO();
		calendarVO.setPk_team(teamVO.getPrimaryKey());
		calendarVO.setPk_org_v(teamVO.getPk_org_v());
		calendarVO.setVnote(teamVO.getVbnote());
		calendarVO.setCwkid(teamVO.getCwkid());
		calendarVO.setCalendar(UFLiteralDate.getDate(date));
		calendarVO.setCancelflag(UFBoolean.valueOf(isHolidayCancel));
		if(ShiftVO.PK_GX.equals(pk_shift)){
			setGX(calendarVO);
		}else if ("0001Z7000000000012GX".equals(pk_shift)) {
			setLJ(calendarVO);
		}
		else{
			setNonGX(calendarVO, ShiftServiceFacade.getAggShiftVOFromMap(shiftVOMap,pk_shift).getShiftVO());
		}
		
		AggTeamCalendarVO aggVO = new AggTeamCalendarVO();
		aggVO.setParentVO(calendarVO);
		setGroupOrgPk2AggVO(pk_group, pk_org, aggVO);
		return aggVO;
	}
	//�O��������
	public static void setLJ(TeamCalendarVO calendarVO) {
		calendarVO.setPk_shift("0001Z7000000000012GX");
		calendarVO.setGzsj(UFDouble.ZERO_DBL);
		calendarVO.setDate_daytype(4);
		calendarVO.setIf_rest(UFBoolean.TRUE);
		calendarVO.setIsflexiblefinal(UFBoolean.FALSE);
		calendarVO.setIswtrecreate(UFBoolean.FALSE);
		calendarVO.setStatus(VOStatus.NEW);
		
	}
	
	/**
	 * ���ù��ݰ�ε�TeamCalendarVO��Ϣ
	 * @param calendarVO
	 */
	public static void setGX(TeamCalendarVO calendarVO){
		calendarVO.setPk_shift(ShiftVO.PK_GX);
		calendarVO.setGzsj(UFDouble.ZERO_DBL);
		calendarVO.setDate_daytype(1);
		calendarVO.setIf_rest(UFBoolean.TRUE);
		calendarVO.setIsflexiblefinal(UFBoolean.FALSE);
		calendarVO.setIswtrecreate(UFBoolean.FALSE);
		calendarVO.setStatus(VOStatus.NEW);
	}
	
	/**
	 * ���÷ǹ��ݰ�ε�TeamCalendarVO��Ϣ
	 * @param calendarVO
	 * @param shiftVO
	 */
	public static void setNonGX(TeamCalendarVO calendarVO,ShiftVO shiftVO){
		calendarVO.setPk_shift(shiftVO.getPrimaryKey());
		calendarVO.setGzsj(shiftVO.getGzsj());
		calendarVO.setDate_daytype(0);
		calendarVO.setIf_rest(UFBoolean.FALSE);
		calendarVO.setIsflexiblefinal(shiftVO.getIsflexiblefinal());
		calendarVO.setIswtrecreate(UFBoolean.FALSE);
		calendarVO.setStatus(VOStatus.NEW);
	}
	
	/**
	 * ��������������Ϣ���õ�AggVO����
	 * @param pk_group
	 * @param pk_org ҵ��Ԫ����
	 * @param aggVO
	 * @param isHolidayCancel
	 */
	public static void setGroupOrgPk2AggVO(String pk_group,String pk_org,AggTeamCalendarVO aggVO){
		if(aggVO==null)
			return;
		TeamCalendarVO calVO = aggVO.getTeamCalendarVO();
		calVO.setPk_group(pk_group);
		calVO.setPk_org(pk_org);
		calVO.setStatus(VOStatus.NEW);
		AuditInfoUtil.addData(calVO);
		TeamWorkTimeVO[] wtVOs = aggVO.getTeamWorkTimeVO();
		if(!ArrayUtils.isEmpty(wtVOs))
			for(TeamWorkTimeVO wtVO:wtVOs){
				wtVO.setPk_group(pk_group);
				wtVO.setPk_org(pk_org);
				wtVO.setStatus(VOStatus.NEW);
			}
		TeamCalHoliday[] holVOs = aggVO.getTeamCalHolidayVO();
		if(!ArrayUtils.isEmpty(holVOs))
			for(TeamCalHoliday holVO:holVOs){
				holVO.setPk_group(pk_group);
				holVO.setPk_org(pk_org);
				holVO.setStatus(VOStatus.NEW);
			}
	}
}