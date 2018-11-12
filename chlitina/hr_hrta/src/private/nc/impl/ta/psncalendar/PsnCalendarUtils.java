package nc.impl.ta.psncalendar;

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
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnCalHoliday;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.ta.psncalendar.PsnWorkTimeVO;
import nc.vo.util.AuditInfoUtil;

import org.apache.commons.lang.ArrayUtils;

public class PsnCalendarUtils {
	
	private static UFDouble zero = new UFDouble(0);
	/**
	 * �����������ݷ��ذ���������������һ�����壬����Ĭ�ϰ��������������ĩ�����ع�������
	 * @param date
	 * @return
	 */
//	public static  String getPkShiftByDate(String date,AggShiftVO defaultAggShiftVO){
//		int weekDay = UFLiteralDate.getDate(date).getWeek();
//		return (weekDay==0||weekDay==6)?ShiftVO.PK_GX:(defaultAggShiftVO==null?ShiftVO.PK_GX:defaultAggShiftVO.getShiftVO().getPrimaryKey());
//	}
	
	/**
	 * ���ݰ����������AggPsnCalendarVO
	 * @param pk_group
	 * @param pk_org
	 * @param pk_shift
	 * @param bclbVOMap
	 * @param isHolidayCancel
	 * @return
	 */
//	@Deprecated
//	public static AggPsnCalendar createAggVOByBclbVO(String pk_psndoc,String pk_group,String pk_org,String date,
//			String pk_shift,Map<String, AggBclbDefVO> bclbVOMap,boolean isHolidayCancel){
//		PsnCalendarVO calendarVO = new PsnCalendarVO();
//		calendarVO.setPk_psndoc(pk_psndoc);
//		calendarVO.setCalendar(UFLiteralDate.getDate(date));
//		if(BclbVO.PK_GX.equals(pk_shift)){
//			setGX(calendarVO);
//		}
//		else{
//			setNonGX(calendarVO, bclbVOMap.get(pk_shift).getBclbVO());
//		}
//		AggPsnCalendar aggVO = new AggPsnCalendar();
//		aggVO.setParentVO(calendarVO);
//		setGroupOrgPk2AggVO(pk_group, pk_org, aggVO, isHolidayCancel);
//		return aggVO;
//	}
	
	/**
	 * ���ݰ����������AggPsnCalendarVO
	 * @param pk_group
	 * @param pk_org
	 * @param pk_shift
	 * @param bclbVOMap
	 * @param isHolidayCancel
	 * @return
	 * @throws BusinessException 
	 */
	public static AggPsnCalendar createAggVOByShiftVO(String pk_psndoc,String pk_group,String pk_org,String date,
			String pk_shift,Map<String, AggShiftVO> shiftVOMap,boolean isHolidayCancel) throws BusinessException{
		PsnCalendarVO calendarVO = new PsnCalendarVO();
		calendarVO.setPk_psndoc(pk_psndoc);
		calendarVO.setCalendar(UFLiteralDate.getDate(date));
		if(ShiftVO.PK_GX.equals(pk_shift)){
			setGX(calendarVO);
		}
		else{
			if(!shiftVOMap.containsKey(pk_shift))
				shiftVOMap.put(pk_shift, ShiftServiceFacade.queryShiftAggVOByPk(pk_shift));
			setNonGX(calendarVO, ShiftServiceFacade.getAggShiftVOFromMap(shiftVOMap, pk_shift).getShiftVO());
		}
		AggPsnCalendar aggVO = new AggPsnCalendar();
		aggVO.setParentVO(calendarVO);
		setGroupOrgPk2AggVO(pk_group, pk_org, aggVO, isHolidayCancel);
		return aggVO;
	}
	
	/**
	 * ���ù��ݰ�ε�psncalendar��Ϣ
	 * @param calendarVO
	 */
	public static void setGX(PsnCalendarVO calendarVO){
		calendarVO.setPk_shift(ShiftVO.PK_GX);
		calendarVO.setGzsj(zero);
		calendarVO.setIf_rest(UFBoolean.TRUE);
		calendarVO.setIsflexiblefinal(UFBoolean.FALSE);
		calendarVO.setIswtrecreate(UFBoolean.FALSE);
		calendarVO.setStatus(VOStatus.NEW);
	}
	
//	/**
//	 * ���÷ǹ��ݰ�ε�psncalendar��Ϣ
//	 * @param calendarVO
//	 * @param bclbVO
//	 */
//	@Deprecated
//	public static void setNonGX(PsnCalendarVO calendarVO,BclbVO bclbVO){
//		calendarVO.setPk_shift(bclbVO.getPk_bclb());
//		calendarVO.setGzsj(bclbVO.getGzsj());
//		calendarVO.setIf_rest(UFBoolean.FALSE);
//		calendarVO.setIsflexiblefinal(bclbVO.getIsflexiblefinal());
//		calendarVO.setIswtrecreate(UFBoolean.FALSE);
//		calendarVO.setStatus(VOStatus.NEW);
//	}
	
	/**
	 * ���÷ǹ��ݰ�ε�psncalendar��Ϣ
	 * @param calendarVO
	 * @param bclbVO
	 */
	public static void setNonGX(PsnCalendarVO calendarVO,ShiftVO shiftVO){
		calendarVO.setPk_shift(shiftVO.getPrimaryKey());
		calendarVO.setGzsj(shiftVO.getGzsj());
		calendarVO.setIf_rest(UFBoolean.FALSE);
		calendarVO.setIsflexiblefinal(shiftVO.getIsflexiblefinal());
		calendarVO.setIswtrecreate(UFBoolean.FALSE);
		calendarVO.setStatus(VOStatus.NEW);
	}
	
	/**
	 * ��������������Ϣ���õ�AggVO����
	 * @param pk_group
	 * @param pk_org
	 * @param aggVO
	 * @param isHolidayCancel
	 */
	public static void setGroupOrgPk2AggVO(String pk_group,String pk_org,AggPsnCalendar aggVO,boolean isHolidayCancel){
		if(aggVO==null)
			return;
		PsnCalendarVO calVO = aggVO.getPsnCalendarVO();
		calVO.setCancelflag(UFBoolean.valueOf(isHolidayCancel));
		calVO.setPk_group(pk_group);
		calVO.setPk_org(pk_org);
		calVO.setStatus(VOStatus.NEW);
		AuditInfoUtil.addData(calVO);
		PsnWorkTimeVO[] wtVOs = aggVO.getPsnWorkTimeVO();
		if(!ArrayUtils.isEmpty(wtVOs))
			for(PsnWorkTimeVO wtVO:wtVOs){
				wtVO.setPk_group(pk_group);
				wtVO.setPk_org(pk_org);
				wtVO.setStatus(VOStatus.NEW);
			}
		PsnCalHoliday[] holVOs = aggVO.getPsnCalHolidayVO();
		if(!ArrayUtils.isEmpty(holVOs))
			for(PsnCalHoliday holVO:holVOs){
				holVO.setPk_group(pk_group);
				holVO.setPk_org(pk_org);
				holVO.setStatus(VOStatus.NEW);
			}
	}
	
	/**
	 * ����ĳһ����Ű࣬�������и�������������null�����ʾ���и�������߼�����psncalendar���ݼ���
	 * */
	public static AggPsnCalendar createAggPsnCalendarByShiftAndHolidayNullWhenNotCut(
			AggShiftVO aggShiftVO,String date,TimeZone timeZone,
			HolidayVO[] holidayVOs,
			Map<String, Boolean> enjoyHolidayMap){
		HolidayVO[] crossedHolidayVOs = TACalendarUtils.findCrossedHolidayVOs(aggShiftVO, holidayVOs, date, timeZone, enjoyHolidayMap);
		if(ArrayUtils.isEmpty(crossedHolidayVOs))
			return null;
		return cutHoliday(aggShiftVO, crossedHolidayVOs, date, timeZone);
		
	}
	
	/**
	 * ָ��һ����κ����ڣ��Լ����յ�������Ϣ���ҳ����������й���ʱ���ཻ�����м���
	 * @param blcbVO
	 * @param holidayVOs
	 * @param timeZone
	 * @param holidayEnjoyMap
	 * @return
	 */
//	public static HolidayVO[] findCrossedHolidayVOs(AggShiftVO aggShiftVO,HolidayVO[] holidayVOs,String date,TimeZone timeZone,Map<String, Boolean> holidayEnjoyMap){
//		if(ArrayUtils.isEmpty(holidayVOs))
//			return null;
//		if(aggShiftVO==null)
//			return null;
//		List<HolidayVO> retList = new ArrayList<HolidayVO>();
//		for(HolidayVO holidayVO:holidayVOs){
//			if(!holidayVO.isAllEnjoy()&&(holidayEnjoyMap==null||!holidayEnjoyMap.get(holidayVO.getPk_holiday())))//���������������գ���continue
//				continue;
//			ITimeScope[] workTimeScopes = aggShiftVO.toMaxWorkTimeScope(date, timeZone);
//			ITimeScope holidayTimeScope = holidayVO.toTimeScope(timeZone);
//			if(!TimeScopeUtils.isCross(workTimeScopes, holidayTimeScope))//�������ʱ�������ʱ����ȫ���ཻ����Ҳ���ù�
//				continue;
//			//����ཻ������˵Ӧ�÷��ش˼��գ�������һ���ཻ������������ۺ�������������Ǿ��Ǽ���ʱ�δ�һ������ʱ����м䴩��������ʱ����Ϊ���룬�������Ҳ������,��Ϊ������������̫����
//			boolean isTrueContains = false;
//			for(ITimeScope workTimeScope:workTimeScopes){
//				if(TimeScopeUtils.trueContains(workTimeScope, holidayTimeScope)){
//					isTrueContains=true;
//					break;
//				}
//			}
//			if(!isTrueContains)
//				retList.add(holidayVO);
//			else if(aggShiftVO.getShiftVO().isOtFlexibleFinal()){
//				//����������������˼��գ�����������°൯�Եģ���Ҫ��һ�����ǣ�
//				//��ʱ���Զκܳ������������ϰ�6�㣬�����ϰ�13�㣬����8Сʱ�����������������6-21�㣬
//				//�����׾Ͱ�һ�����հ�����ȥ�ˣ������м��̻��Ļ����ǲ��ܰ������յģ���������£����ܰ���
//				//����Ĺ���������
//				//��������£���Ҫ���м��̻��Ĺ������ٱȽ�һ�Σ��������trueContains���򲻴���
//				WTVO[] wtVOs = aggShiftVO.getWTVOs();//�Ѿ��ǰ��м��̻����˵�
//				ITimeScope[] midWorkScopes =  TimeScopeUtils.toTimeScope(wtVOs, date, timeZone);
//				isTrueContains = false;
//				for(ITimeScope workTimeScope:midWorkScopes){
//					if(TimeScopeUtils.trueContains(workTimeScope, holidayTimeScope)){
//						isTrueContains=true;
//						break;
//					}
//				}
//				if(!isTrueContains)
//					retList.add(holidayVO);
//			}
//		}
//		return retList.toArray(new HolidayVO[0]);
//	}
	
	/**
	 * ����һ���������յ��и���������ڵ��԰࣬�п��ܴ����Ľ���ǹ̻��ˣ�����û���и���̻���Ĺ�����
	 * @param aggShiftVO
	 * @param holidayVOs
	 * @param date
	 * @param timeZone
	 * @param pk_psndoc
	 * @return
	 */
	public static AggPsnCalendar cutHoliday(AggShiftVO aggShiftVO,HolidayVO[] holidayVOs,String date,TimeZone timeZone){
		PsnCalendarVO mainVO = new PsnCalendarVO();
		mainVO.setCalendar(UFLiteralDate.getDate(date));
		mainVO.setCancelflag(UFBoolean.TRUE);//����������и�Ŀ϶���������ȡ��
		AggPsnCalendar aggVO = new AggPsnCalendar();
		aggVO.setParentVO(mainVO);
		
		PsnCalHoliday[] calHolVOs = new PsnCalHoliday[holidayVOs.length];
		for(int i=0;i<calHolVOs.length;i++){
			calHolVOs[i]=new PsnCalHoliday();
			calHolVOs[i].setPk_holiday(holidayVOs[i].getPk_holiday());
		}
		aggVO.setPsnCalHolidayVO(calHolVOs);
		ITimeScope[] holidayScopes = HolidayVO.toTimeScopes(holidayVOs, timeZone);//����ʱ���
		ITimeScope[] remainsWorkScopes = TimeScopeUtils.minusTimeScopes(aggShiftVO.toMaxWorkTimeScope(date, timeZone), holidayScopes);//����ʱ��μ�ȥ����ʱ���
		if(ArrayUtils.isEmpty(remainsWorkScopes)){//��������ɶ����ʣ�ˣ���ô���տ϶���Ϊ����
			PsnCalendarUtils.setGX(mainVO);
			mainVO.setOriginal_shift_b4cut(aggShiftVO.getShiftVO().getPk_shift());//�и�ǰ�İ��
			return aggVO;
		}
		//�������󣬻�ʣһ�㣬����Ҫ�����������
		//����ǹ̶��࣬��ܼ򵥣�ʣ�µľ������չ���ʱ��Σ�
		//����ǵ��԰࣬��Ҫ������ʱ���Ƿ��ܸ���ĳһ�����ܵ���������ʱ��Σ�ע�ⲻ�����Ĺ���ʱ��Σ����п��ܼ��ղ��ܸ������Ĺ���ʱ��Σ����ܸ���ĳһ�����ܵ���������ʱ��Σ�������ܸ��ǣ��������Ȼ����Ϊ���ݣ�������ܸ�������һ�����ܵĹ���ʱ��Σ���Ҫ���м��̻���ʱ�乤��ʱ��μ�ȥ����ʱ�Σ�ʣ�µĲ������չ���ʱ���
		if(!aggShiftVO.getShiftVO().isFlexibleFinal()){//����ǹ̶���
			mainVO.setPk_shift(aggShiftVO.getShiftVO().getPrimaryKey());
			PsnWorkTimeVO[] workTimeVOs = transfer2WorkTimeVOs(remainsWorkScopes,aggShiftVO,date,timeZone);
			aggVO.setPsnWorkTimeVO(workTimeVOs);
			mainVO.setGzsj(new UFDouble(TimeScopeUtils.getLength(workTimeVOs)/3600.0));
			mainVO.setIf_rest(UFBoolean.FALSE);
			mainVO.setIswtrecreate(UFBoolean.TRUE);
			mainVO.setIsflexiblefinal(UFBoolean.FALSE);
			return aggVO;
		}
		//�����ǵ��԰�
		if(TACalendarUtils.isHolidayCanOverrideACompleteShift(aggShiftVO, holidayScopes, date, timeZone)){//�������ʱ��ο��Ը���һ�������Ĺ���ʱ��Σ�������Ϊ����
			PsnCalendarUtils.setGX(mainVO);
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
		PsnWorkTimeVO[] workTimeVOs = transfer2WorkTimeVOs(remainsWorkScopes,aggShiftVO,date,timeZone);
		aggVO.setPsnWorkTimeVO(workTimeVOs);
		mainVO.setGzsj(new UFDouble(TimeScopeUtils.getLength(workTimeVOs)/3600.0));
		mainVO.setIf_rest(UFBoolean.FALSE);
		mainVO.setIswtrecreate(UFBoolean.TRUE);
		mainVO.setIsflexiblefinal(UFBoolean.FALSE);
		return aggVO;
		
	}
	
	/**
	 * ���������и���ʱ���ת��ΪPsnWortTimeVO[]���飬��Ҫ�������е����°��Ƿ���Ҫˢ�����ϰ�ˢ���Ρ��°�ˢ���ο�ʼ����ʱ�����Ϣ
	 * @param timeScopes
	 * @param aggShiftVO
	 * @return
	 */
	private static PsnWorkTimeVO[] transfer2WorkTimeVOs(ITimeScope[] timeScopes,AggShiftVO aggShiftVO,String date,TimeZone timeZone){
		PsnWorkTimeVO[] workTimeVOs = new PsnWorkTimeVO[timeScopes.length];
		WTVO[] wtVOs = aggShiftVO.getWTVOs();
		ITimeScope[] wtScopes = TimeScopeUtils.toTimeScope(wtVOs, date, timeZone);
		//:��������ʱ���ӱ�
		for(int i=0;i<workTimeVOs.length;i++){
			workTimeVOs[i]=new PsnWorkTimeVO(timeScopes[i]);
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
	 * ������ʱ���Ƿ���Ը��ǵ��԰�ε�ĳһ�����ܵ������Ĺ���ʱ��Ρ���Ϊ���԰�����°����Ϣ�ζ��п����ǵ��Եģ�����߼���ǳ����ӣ��˴����򵥴�����
	 * ������°൯�ԣ��򲻿����м�ʱ��Σ�ֻҪ�ܸ���һ�����ܵ��ϰ�㵽�°��
	 * ������°಻���ԣ���Ҫ���ܸ����ϰ�ʱ�䵽�°�ʱ����ι̶�ʱ��Σ�Ҳ�������м�ʱ��Σ�
	 * ���磬���԰�Ϊ8-10�ϰ࣬16-18�°࣬��ô����ʱ��Ϊ8-16,���ܷ���true�����÷ǵ�8-18Ϊ���ղŷ���true
	 * �˷����������ǣ����Ϊtrue�����ʾ������Ȼ���ܸ������Ĺ���ʱ��Σ����������ܹ��ųɹ���
	 * @param aggShiftVO
	 * @param holidayScopes
	 * @param date
	 * @param timeZone
	 * @return
	 */
//	private static  boolean isHolidayCanOverrideACompleteShift(AggShiftVO aggShiftVO,ITimeScope[] holidayScopes,String date,TimeZone timeZone){
//		//���ȿ�����ʱ���Ƿ���ڵ��ڰ�εĹ���ʱ�������Ϊn������޿��ܸ���һ�����ܵ������Ĺ�����
//		if(TimeScopeUtils.getLength(holidayScopes)<aggShiftVO.getShiftVO().getGzsj().doubleValue()*3600)
//			return false;
//		if(!aggShiftVO.getShiftVO().isOtFlexibleFinal()){//����������°�̶��������������ʱ��μ�ȥ����ʱ�Σ�������Ƿ�Ϊ��,Ϊ���������Ϊ�ܸ���һ�������Ĺ���ʱ���
//			return ArrayUtils.isEmpty(TimeScopeUtils.minusTimeScopes(aggShiftVO.toMaxWorkTimeScope(date, timeZone), holidayScopes));
//		}
//		//����������°൯�ԣ����߼��Ƚϸ��ӣ�
//		//ѭ��ÿһ������ʱ�Σ�holidayScopes��ʱ���Ѿ�������merge���������ֱ��ü���ʱ�εĿ�ʼʱ��ͽ���ʱ�����̻���εĹ���ʱ��Σ�����̻�֮��Ĺ���ʱ����ܱ�����ʱ����ȫ�������򷵻�true�����򷵻�false
//		for(ITimeScope holidayScope:holidayScopes){
//			UFDateTime holidayBeginTime = holidayScope.getScope_start_datetime();
//			ITimeScope[] solidifiedWorkScopes = aggShiftVO.solidify(date, timeZone, holidayBeginTime, true);//�����ڿ�ʼʱ����Ϊ�ϰ�ʱ���̻��Ĺ���ʱ���
//			if(TimeScopeUtils.contains(holidayScopes, solidifiedWorkScopes))
//				return true;
//			UFDateTime holidayEndTime = holidayScope.getScope_end_datetime();
//			solidifiedWorkScopes = aggShiftVO.solidify(date, timeZone, holidayEndTime, false);//�����ڽ���ʱ����Ϊ�°�ʱ���̻��Ĺ���ʱ���
//			if(TimeScopeUtils.contains(holidayScopes, solidifiedWorkScopes))
//				return true;
//		}
//		return false;
//	}
	
	/**
	 * ����һ�������ĳ��ļ����и�������˷�������ʱ��Ӧ�ñ�֤����ʱ���������н���
	 */
	public static AggPsnCalendar createHolidayCutAggPsnCalendarVO(
			String pk_psndoc,
			AggShiftVO aggShiftVO,
			HolidayVO[] holidayVOs,
			String date,
			TimeZone timeZone){
		AggPsnCalendar cutCalendar = PsnCalendarUtils.cutHoliday(aggShiftVO, holidayVOs, date, timeZone);
		cutCalendar.getPsnCalendarVO().setPk_psndoc(pk_psndoc);
		return cutCalendar;
	}
	
	/**
	 * ��ѭ���İ�ΰ����ڷ�Χȫ��չ����map��key��date��value�ǰ������
	 * @param beginDate
	 * @param endDate
	 * @param calendarPks
	 * @return
	 */
//	public static Map<String, String> expandCalendar2MapByDateArea(UFLiteralDate beginDate, UFLiteralDate endDate,
//			String[] calendarPks){
//		Map<String, String> returnMap = new HashMap<String, String>();
//		int daysCount = endDate.getDaysAfter(beginDate)+1;
//		for(int i=0;i<daysCount;i++){
//			returnMap.put(beginDate.getDateAfter(i).toString(), calendarPks[i%calendarPks.length]);
//		}
//		return returnMap;
//	}
	
//	@SuppressWarnings("unchecked")
//	@Deprecated
//	protected static  Set<BclbMutexVO>[] splitMutexVOSet(BclbMutexVO[] vos){
//		//�����ų�ͻ��
//		Set<BclbMutexVO> mutexSet0 = new HashSet<BclbMutexVO>();
//		//�����ŵߵ���
//		Set<BclbMutexVO> reverseSet0 = new HashSet<BclbMutexVO>();
//		//��һ���ͻ��
//		Set<BclbMutexVO> mutexSet1 = new HashSet<BclbMutexVO>();
//		//��һ��ߵ���
//		Set<BclbMutexVO> reverseSet1 = new HashSet<BclbMutexVO>();
//		if(ArrayUtils.isEmpty(vos))
//			return new Set[]{mutexSet0,reverseSet0,mutexSet1,reverseSet1};
//		for(BclbMutexVO vo:vos){
//			if(vo.getSepday()==0&&vo.getMutextype().intValue()==BclbMutexVO.NEXTMUTEX){
//				mutexSet0.add(vo);
//				continue;
//			}
//			if(vo.getSepday()==1&&vo.getMutextype().intValue()==BclbMutexVO.NEXTMUTEX){
//				mutexSet1.add(vo);
//				continue;
//			}
//			if(vo.getSepday()==0&&vo.getMutextype().intValue()==BclbMutexVO.EXGHGMUTEX){
//				reverseSet0.add(vo);
//				continue;
//			}
//			if(vo.getSepday()==1&&vo.getMutextype().intValue()==BclbMutexVO.EXGHGMUTEX){
//				reverseSet1.add(vo);
//				continue;
//			}
//		}
//		return new Set[]{mutexSet0,reverseSet0,mutexSet1,reverseSet1};
//	}
	
//	@SuppressWarnings("unchecked")
//	protected static  Set<ShiftMutexBUVO>[] splitMutexVOSet(ShiftMutexBUVO[] vos){
//		//�����ų�ͻ��
//		Set<ShiftMutexBUVO> mutexSet0 = new HashSet<ShiftMutexBUVO>();
//		//�����ŵߵ���
//		Set<ShiftMutexBUVO> reverseSet0 = new HashSet<ShiftMutexBUVO>();
//		//��һ���ͻ��
//		Set<ShiftMutexBUVO> mutexSet1 = new HashSet<ShiftMutexBUVO>();
//		//��һ��ߵ���
//		Set<ShiftMutexBUVO> reverseSet1 = new HashSet<ShiftMutexBUVO>();
//		if(ArrayUtils.isEmpty(vos))
//			return new Set[]{mutexSet0,reverseSet0,mutexSet1,reverseSet1};
//		for(ShiftMutexBUVO vo:vos){
//			if(vo.getSepday()==0&&vo.getMutextype().intValue()==ShiftMutexBUVO.NEXTMUTEX){
//				mutexSet0.add(vo);
//				continue;
//			}
//			if(vo.getSepday()==1&&vo.getMutextype().intValue()==ShiftMutexBUVO.NEXTMUTEX){
//				mutexSet1.add(vo);
//				continue;
//			}
//			if(vo.getSepday()==0&&vo.getMutextype().intValue()==ShiftMutexBUVO.EXGHGMUTEX){
//				reverseSet0.add(vo);
//				continue;
//			}
//			if(vo.getSepday()==1&&vo.getMutextype().intValue()==ShiftMutexBUVO.EXGHGMUTEX){
//				reverseSet1.add(vo);
//				continue;
//			}
//		}
//		return new Set[]{mutexSet0,reverseSet0,mutexSet1,reverseSet1};
//	}
}