package nc.impl.ta.leave;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ArrayHelper;
import nc.hr.utils.CommonUtils;
import nc.impl.ta.algorithm.BillProcessHelperAtServer;
import nc.impl.ta.algorithm.CalParam4OnePerson;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.algorithm.DateTimeUtils;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.TimeScopeUtils;
import nc.itf.ta.algorithm.impl.DefaultTimeScope;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveCommonVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * SplitLeaveBillUtils�Ĵ���helper�������㷨����SplitLeaveBillUtils���棬������һЩ����ţ�ĸ���ϸ�ڴ���
 * @author zengcheng
 *
 */
public class SplitLeaveBillHelper {
	/**
	 * �����ɸ��ݼ�ʱ�Σ������ݼ����ġ��������ڲ������ڿ�ʼ����____�족�������и���������֣���һ��������Υ���˲���Լ���ģ��ڶ�������������˲���Լ����
	 * ��У��ֻ�����뵥��Ч���������ڵǼǵ�����Ϊ��ȫ����˲���
	 * ���һ��ʱ�α��ֳ������Σ���ô�п������һ�������İ�α��ֳ����룬�������ʱ�������ʧ�档
	 * ����������Բ��ܣ���Ϊ�����ɵ�����д���Լ���������ɵ����⣬��ʧӦ�����Լ��е�
	 * @param <T>
	 * @param vo
	 * @param date
	 * @param timeZone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[][] splitTimeScopeByApplyDatePara(T[] vos ,UFLiteralDate applyDate,TimeZone timeZone,LeaveTypeCopyVO leaveTypeCopyVO){
		T[][] retArray = (T[][]) Array.newInstance(vos.getClass(), 2);
		if(!(vos[0] instanceof LeavebVO)||!leaveTypeCopyVO.isLeaveAppTimeLimit()){//����������뵥�������ݼ�������������������ʱ�ζ��ŵ��ڶ���Ԫ����
			retArray[1]=vos;
			return retArray;
		}
		TimeScopeUtils.sort(vos);
		//����һ���Ŀ�ʼ���ڶ���Υ���˹涨����ô�϶����еĶ���Υ��
		UFLiteralDate beginDate = DateTimeUtils.toLiteralDate(vos[0].getLeavebegintime(), timeZone);
		int limitDates = leaveTypeCopyVO.getLeaveAppTimeLimit();
		UFLiteralDate properBeginDate = applyDate.getDateBefore(limitDates);//��Υ���涨��������ݼٿ�ʼ���ڣ������Υ���涨��
		if(!beginDate.before(properBeginDate)){
			retArray[1]=vos;
			return retArray;
		}
		//ѭ��������Щ�ݼ�ʱ��
		List<T> breakList = new ArrayList<T>();//Υ���涨�ļ�¼����
		List<T> normalList = new ArrayList<T>();//��Υ���涨�ļ�¼����
		for(int i = 0;i<vos.length;i++){
			T vo = vos[i];
			//����ǰ���ʱ�β�ֳ�Υ���涨�ĺͲ�Υ���涨��
			T[] splitVOs = splitTimeScopeByDate(vo, properBeginDate, timeZone);
			if(splitVOs[0]!=null)//Υ���涨��
				breakList.add(splitVOs[0]);
			if(splitVOs[1]!=null)//��Υ����
				normalList.add(splitVOs[1]);
			if(splitVOs[0]==null){//�����һ��Ԫ��Ϊnull��˵����ǰ���������ʱ�β�Υ���涨������˵���������ʱ�ζ���Υ���涨
				for(int j=i+1;j<vos.length;j++){
					normalList.add(vos[j]);
				}
				break;
			}
		}
		retArray[0]=breakList.size()==0?null:breakList.toArray((T[]) Array.newInstance(vos.getClass().getComponentType(), breakList.size()));
		retArray[1]=normalList.size()==0?null:normalList.toArray((T[]) Array.newInstance(vos.getClass().getComponentType(), normalList.size()));
		return retArray;
	}
	
	/**
	 * �������������ȱ����ݼٵ�������Ҫ����ݼ�ʱ���Ƿ����ȣ�������ˣ�����Ҫ���
	 * ��ֵ�ʱ����Ҫ��֤һ�������İ�β������
	 * ���ô˷���ʱ�򣬲����϶��ǲ����������
	 * @param <T>
	 * @param vo
	 * @param timeZone
	 * @param leaveTypeCopyVO
	 * @return
	 * @throws BusinessException 
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[] splitTimeScopeByOverYearPara(T[] vos ,TimeZone timeZone,CalParam4OnePerson leaveLengthCalParam) throws BusinessException{
		List<T> retList = new ArrayList<T>();
		for(T vo:vos){
			//��������꣬�򲻹ܲ�����ʲô�������ô�����������ĳ����Ǿ�������������������жϣ�
			UFLiteralDate beginDate = DateTimeUtils.toLiteralDate(vo.getLeavebegintime(), timeZone);
			UFLiteralDate endDate = DateTimeUtils.toLiteralDate(vo.getLeaveendtime(), timeZone);
			int beginYearInt = beginDate.getYear();
			int endYearInt = endDate.getYear();
			String beginYear = Integer.toString(beginYearInt);
			String endYear = Integer.toString(endYearInt);
			boolean isEntimeJanFirstZeroOClock = vo.getLeaveendtime().toStdString(timeZone).endsWith("-01-01 00:00:00");
			//�����ʼ/���������ȣ����߽���ʱ���ǵڶ���һ��һ�յ���㣬���������
			if(beginYear.equals(endYear)||((endYearInt-beginYearInt==1)&&isEntimeJanFirstZeroOClock)){
				vo.setSimpleyear(beginYear);
				retList.add(vo);
				continue;
			}
			//���ݼ�ʱ�εĿ�ʼ/����������ȷȷʵʵ�ز���ͬһ�꣬����ҪС�Ĵ���һ�¡�
			//�������Լ򵥴���������������������Ӽ��ɣ��������Ļ����п��ܰ�һ����η�Ϊ���룬�������ʱ��ʧ�档�����ҪС�Ĵ�����
			//����������ر�����һ���ݼ�ʱ�����档�����п�������ݼ�ʱ��"��΢"�ؿ��꣬����������
			//������иʽ������ʱ�������ҵ����е���ݱ����(12.31��1.1)�������ҳ������ŵ��и�㡣���и�����ݼ�ʱ���֮�⣬˵������Ҫ�и�
			//ÿ�и�һ�Σ��Ͷ��һ��ʱ���
			//�����ʼ�ͽ�����ǰ�����꣬����һ�μ��ɣ����м仹�����꣬��ÿ���һ��Ͷദ��һ�Ρ�ע�⣬������ʱ����һ��һ����㣬�򵱳�ǰһ�괦��
			List<T> splitList = new ArrayList<T>(2);
			splitList.add(vo);
			for(int year=beginYearInt;year<endYearInt-(isEntimeJanFirstZeroOClock?1:0);year++){
				UFLiteralDate preDate = UFLiteralDate.getDate(year+"-12-31");
				UFLiteralDate latterDate = UFLiteralDate.getDate((year+1)+"-01-01");
				UFDateTime splitTime = findSplitTime(preDate, latterDate, leaveLengthCalParam);
				T toSplitVO = splitList.get(splitList.size()-1);//��Ҫ�����и����Զ�������һ����¼��֮ǰ�Ķ����Ѿ��и���˵�
				//���и�ʱ�䳬�����ݼ�ʱ��εķ�Χ�������и�
				if(!splitTime.after(toSplitVO.getLeavebegintime())){
					toSplitVO.setSimpleyear(Integer.toString(year+1));
					continue;
				}
				if(!splitTime.before(toSplitVO.getLeaveendtime())){
					toSplitVO.setSimpleyear(Integer.toString(year));
					continue;
				}
				T[] splitResult = splitTimeScopeByDateTime(toSplitVO, splitTime, timeZone);
				splitResult[0].setSimpleyear(Integer.toString(year));
				splitResult[1].setSimpleyear(Integer.toString(year+1));
				splitList.remove(splitList.size()-1);
				splitList.add(splitResult[0]);
				splitList.add(splitResult[1]);
			}
			retList.addAll(splitList);
		}
		return retList.toArray((T[])Array.newInstance(vos.getClass().getComponentType(), retList.size()));
	}
	
	/**
	 * ��ĳ������Ϊ�磬��ĳ���ݼ�ʱ�η�Ϊ�����֣���һ����ȫ����date֮ǰ��֮�󲿷�ȫ����date֮�����ָ�������һ�������İ�α��г����룬��ʱ������
	 * @param <T>
	 * @param vo
	 * @param date
	 * @param timeZone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[] splitTimeScopeByDate(T vo ,UFLiteralDate date,TimeZone timeZone){
		UFLiteralDate beginDate = DateTimeUtils.toLiteralDate( vo.getLeavebegintime(), timeZone);
		UFLiteralDate endDate = DateTimeUtils.toLiteralDate( vo.getLeaveendtime(), timeZone);
		T[] retArray = (T[]) Array.newInstance(vo.getClass(), 2);
		if(!beginDate.before(date)){
			retArray[1]=vo;
			return retArray;
		}
		if(endDate.before(date)||(endDate.equals(date)&&vo.getLeaveendtime().toStdString(timeZone).endsWith("00:00:00"))){
			retArray[0]=vo;
			return retArray;
		}
		UFDateTime splitTime = new UFDateTime(date+" 00:00:00", timeZone);
		T ret0 = (T) vo.clone();
		retArray[0]=ret0;
		ret0.setEnddate(date);
		ret0.setLeaveendtime(splitTime);
		T ret1 = (T) vo.clone();
		retArray[1]=ret1;
		ret1.setBegindate(date);
		ret1.setLeavebegintime(splitTime);
		return retArray;
	}
	
	/**
	 * ��һ��balancevo����Ч���ڷ�Χ��������β������Ű���Ϣ������һ��ʱ��Σ��������ݼ�ʱ�����������Ի�ô�balancevo����Ч�ڷ�Χ��֧�ֵ��ݼ�ʱ��
	 * �������Լ򵥵��õ�һ�յ�0������һ�յ�24����Ϊ�ָ�ʱ��㣬�������򵥵طָ��п��ܽ�һ�������İ�ε�ʱ��η��������ݼ�ʱ���У����ܻ����ʱ��ʧ�棬
	 * ��Ϊ�ݼ�ʱ���ļ�������һ���ݼ�ʱ��Ϊ�����ġ������Ҫ����һ�·ָ��㷨���Ծ����ر�֤��һ�������İ�α�����һ���ݼ�ʱ����
	 * @param balanceVO
	 * @param leaveLengthCalParam
	 * @return
	 * @throws BusinessException 
	 */
	private static ITimeScope toEffectiveTimeScope(LeaveBalanceVO balanceVO,CalParam4OnePerson leaveLengthCalParam) throws BusinessException{
		UFLiteralDate beginDate = balanceVO.getPeriodbegindate();//���/�ڼ�/��ְ��Ŀ�ʼ����
		UFLiteralDate beginDateBefore1 = beginDate.getDateBefore(1);//��ʼ���ڵ�ǰһ��
		
		UFDateTime beginTime = findSplitTime(beginDateBefore1, beginDate, leaveLengthCalParam);
		
		UFLiteralDate endDateAddExtend = balanceVO.getPeriodextendenddate();//���/�ڼ�/��ְ��Ľ������ڼ�����Ч���ӳ�����
		UFLiteralDate endDateAddExtendAfter1 = endDateAddExtend.getDateAfter(1);//��Ч�ڽ����յĺ�һ��
		
		UFDateTime endTime = findSplitTime(endDateAddExtend, endDateAddExtendAfter1, leaveLengthCalParam);
		
		return new DefaultTimeScope(beginTime, endTime, false);
	}
	
	/**
	 * ������������İ�Σ��ҵ�һ������ʵķָ�ʱ��㣬ʹ��һ����α������ı�����һ��ʱ����У��������ֿ�
	 * @param preDate
	 * @param latterDate
	 * @param leaveLengthCalParam
	 * @return
	 * @throws BusinessException 
	 */
	private static UFDateTime findSplitTime(
			UFLiteralDate preDate,
			UFLiteralDate latterDate,
			CalParam4OnePerson leaveLengthCalParam) throws BusinessException{
		//��������Ĺ�������
		AggPsnCalendar preCalendar = leaveLengthCalParam.calendarMap==null?null:leaveLengthCalParam.calendarMap.get(preDate);
		AggPsnCalendar latterCalendar = leaveLengthCalParam.calendarMap==null?null:leaveLengthCalParam.calendarMap.get(latterDate);
		//��������İ��
		
		ShiftVO preShiftVO = preCalendar==null?null:ShiftServiceFacade.getShiftVOFromMap(leaveLengthCalParam.shiftMap, preCalendar.getPsnCalendarVO().getPk_shift());
		ShiftVO latterShiftVO = latterCalendar==null?null:ShiftServiceFacade.getShiftVOFromMap(leaveLengthCalParam.shiftMap, latterCalendar.getPsnCalendarVO().getPk_shift());
		//���������ʱ��
		TimeZone preTimeZone = leaveLengthCalParam.timeruleVO.getTimeZoneMap().get(leaveLengthCalParam.dateOrgMap.get(preDate));
		preTimeZone = preTimeZone == null?ICalendar.BASE_TIMEZONE:preTimeZone;
		TimeZone latterTimeZone = leaveLengthCalParam.timeruleVO.getTimeZoneMap().get(leaveLengthCalParam.dateOrgMap.get(latterDate));
		latterTimeZone = latterTimeZone == null?ICalendar.BASE_TIMEZONE:latterTimeZone;
		return findSplitTime(preDate, preCalendar, preShiftVO, preTimeZone, latterDate, latterCalendar, latterShiftVO, latterTimeZone);
	}
	
	private static UFDateTime findSplitTime(
			UFLiteralDate preDate,AggPsnCalendar preAggCalendar,ShiftVO preShiftVO,TimeZone preTimeZone,
			UFLiteralDate latterDate,AggPsnCalendar latterAggCalendar,ShiftVO latterShiftVO,TimeZone latterTimeZone){
		boolean isPreGX = preAggCalendar==null||preAggCalendar.getPsnCalendarVO().getPk_shift().equals(ShiftVO.PK_GX);
		boolean isLatterGX = latterAggCalendar==null||latterAggCalendar.getPsnCalendarVO().getPk_shift().equals(ShiftVO.PK_GX);
		//�������İ�ζ��ǹ��ݣ��򷵻���㼴�ɣ����ǰ�������ʱ����һ����ô�죿������ʱ�ú�һ���ʱ����
		if(isPreGX&&isLatterGX)
			return new UFDateTime(latterDate+" 00:00:00", latterTimeZone);
		if(isPreGX&&!isLatterGX)
			return latterShiftVO.toKqScope(latterDate.toString(), latterTimeZone).getScope_start_datetime();
		if(!isPreGX&&isLatterGX)
			return preShiftVO.toKqScope(preDate.toString(), preTimeZone).getScope_end_datetime();
		//���ǰ�����춼���ǹ��ݣ���ǰһ���ε��°�ʱ��ͺ�һ���ε��ϰ�ʱ���Ƿ�Խ�硣�����û��Խ�磬����0�����ָ�
		ITimeScope[] preWorkScopes = preAggCalendar.getPsnWorkTimeVO();
		//ǰһ�հ�ε��°�ʱ��
		UFDateTime preWorkEndTime = preWorkScopes[preWorkScopes.length-1].getScope_end_datetime();
		String preWorkEndTimeStr = preWorkEndTime.toStdString(preTimeZone);
		//ǰһ�յ��°�ʱ�䣬������ڴ��ڵ��գ�������Խ�磨��һ�յ���㲻��Խ�磬����һ�����Խ���ˣ�
		boolean isPreBeyond = preWorkEndTimeStr.substring(0, 10).compareTo(preDate.toString())>0&&!preWorkEndTimeStr.equals(latterDate+" 00:00:00");
		if(isPreBeyond){//���ǰһ����°�ʱ��Խ���ˣ���ֱ����ǰһ����°�ʱ����Ϊ�ָ�ʱ��
			return preWorkEndTime;
		}
		ITimeScope[] latterWorkScopes = latterAggCalendar.getPsnWorkTimeVO();
		//��һ�հ�ε��ϰ�ʱ��
		UFDateTime latterWorkBeginTime = latterWorkScopes[0].getScope_start_datetime();
		String latterWorkBeginTimeStr = latterWorkBeginTime.toStdString(latterTimeZone);
		//��һ�յ��ϰ�ʱ�䣬�������С�ڵ��գ�������Խ��
		boolean isLatterBeyond = latterWorkBeginTimeStr.substring(0, 10).compareTo(latterDate.toString())<0;
		if(isLatterBeyond){
			return latterWorkBeginTime;
		}
		//������춼û��Խ�磬��ʹ�����
		return new UFDateTime(latterDate+" 00:00:00", latterTimeZone);
	}
	
	static TimeZone queryTimeZoneByPkPsnjob(String pk_psnjob,Map<String, TimeZone> psnjobTimeZoneMap) throws BusinessException{
		TimeZone timeZone = psnjobTimeZoneMap.get(pk_psnjob);
		if(timeZone!=null)
			return timeZone;
		PsnJobVO psnJobVO = (PsnJobVO) new BaseDAO().retrieveByPK(PsnJobVO.class, pk_psnjob); 
		timeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(psnJobVO.getPk_org());
		psnjobTimeZoneMap.put(pk_psnjob, timeZone);
		return timeZone;
	}
	
	/**
	 * ��һ��ʱ�������vos�ĵ�һ��Ԫ���У���ȥһ��ʱ��Ρ��п��ܰѵ�һ��Ԫ����û�ˣ���ʱvos�ĳ��ȼ�1��Ҳ�п���ʣ��һ���֣���ʱvos���Ȳ���
	 * @param <T>
	 * @param vos
	 * @param voMinusLeft����һ��Ԫ����ʣ�µ�ʱ���
	 */
//	@SuppressWarnings("unchecked")
//	static <T extends LeaveCommonVO> T[] minusFromArrayTopByLeftTimeScope(T[] vos,T voMinusLeft){
//		if(voMinusLeft==null){//�����һ��Ԫ�ر���û�ˣ�������ĳ���Ҫ��һ
//			if(vos.length==1)
//				return null;
//			T[] retArray = (T[]) Array.newInstance(vos.getClass().getComponentType(), vos.length-1);
//			System.arraycopy(vos, 1, retArray, 0, vos.length-1);
//			return retArray;
//		}
//		vos[0]=voMinusLeft;
//		return vos;
//	}
	
	/**
	 * ��voToAdd�ӵ�vos�������ǰ�档���vosToAdd�����һ��Ԫ�غ�����ĵ�һ��Ԫ�ظպ��ܰ��ϣ�����Ҫ�ϲ�Ϊһ��Ԫ��
	 * ���ô˷����и�ǰ�᣺vosToAdd��ʱ��ȫ����vos��ǰ��
	 * @param <T>
	 * @param vos
	 * @param voToAdd
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[] reviveToArray(T[] vos,T[] vosToAdd){
		int originalLength = vos==null?0:vos.length;
		if(originalLength==0){//���vos�ǿյģ���ֱ�ӷ���vosToAdd
			return vosToAdd;
		}
		TimeScopeUtils.sort(vosToAdd);
		TimeScopeUtils.sort(vos);
		//���vos���գ���Ҫ��vos�ĵ�һ��Ԫ�غ�vosToAdd�����һ��Ԫ���Ƿ��ܽ��ϣ�����ܽ��ϣ�����Ҫ�����ߺϲ��ٷ���
		T lastAddVO = vosToAdd[vosToAdd.length-1];
		T firstVO = vos[0];
		if(lastAddVO.getScope_end_datetime().equals(firstVO.getScope_start_datetime())){
			T[] retArray = (T[]) Array.newInstance(vosToAdd.getClass().getComponentType(), originalLength+vosToAdd.length-1);
			if(vosToAdd.length>1)
				System.arraycopy(vosToAdd, 0, retArray, 0, vosToAdd.length-1);
			if(vos.length>1)
				System.arraycopy(vos, 1, retArray, vosToAdd.length, originalLength-1);
			T tempVO = (T)lastAddVO.clone();
			tempVO.setScope_end_datetime(firstVO.getScope_end_datetime());
			retArray[vosToAdd.length-1]=tempVO;
			return retArray;
				
		}
		//�ߵ����˵�����ܺ��κ�һ��ʱ��ν�������ֻ������ʵʵ�ش���һ��������
		T[] retArray = (T[]) Array.newInstance(vosToAdd.getClass().getComponentType(), originalLength+vosToAdd.length);
		System.arraycopy(vosToAdd, 0, retArray, 0, vosToAdd.length);
		System.arraycopy(vos, 0, retArray, vosToAdd.length, originalLength);
		TimeScopeUtils.sort(retArray);
		return retArray;
	}
	/**
	 * ������ĵ�һ��Ԫ��ȡ������װ��һ������Ϊ1�����鷵��
	 * @param <T>
	 * @param vos
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	static <T extends LeaveCommonVO> T[] fetchFirstElement(T[] vos){
//		if(vos.length==1)
//			return vos;
//		T[] retArray = (T[])Array.newInstance(vos.getClass().getComponentType(), 1);
//		retArray[0]=vos[0];
//		return retArray;
//	}
	
	/**
	 * ��vo��װ��һ������Ϊ1����������
	 * @param <T>
	 * @param vo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[] createOneElementArray(T vo){
		T[] retArray = (T[])Array.newInstance(vo.getClass(), 1);
		retArray[0]=vo;
		return retArray;
	}
	
	/**
	 * ��һ���ݼ�ʱ�δ�һ��ʱ���ֳ����롣���ʱ������ݼ�ʱ��֮�⣬��ֻ���س���Ϊ1������
	 * @param <T>
	 * @param vo
	 * @param time
	 * @param timeZone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T extends LeaveCommonVO> T[] splitTimeScopeByDateTime(T vo,UFDateTime time,TimeZone timeZone){
		if(!time.after(vo.getLeavebegintime())||!time.before(vo.getLeaveendtime()))
			return createOneElementArray(vo);
		T tempVO = (T)vo.clone();
		vo.setLeaveendtime(time);
		UFLiteralDate date = DateTimeUtils.toLiteralDate(time, timeZone);
		vo.setLeaveenddate(date);
		tempVO.setLeavebegintime(time);
		tempVO.setLeavebegindate(date);
		T[] retArray = (T[])Array.newInstance(vo.getClass(), 2);
		retArray[0]=vo;
		retArray[1]=tempVO;
		return retArray;
	}
	
	/**
	 * ��replaceVOs�滻��containerVOs�ĵ�һ��Ԫ����
	 * ����replaceVOs={a,b},containerVOs={a,c,d}���滻��containerVOs={a,b,c,d}
	 * @param <T>
	 * @param replaceVOs
	 * @param containerVOs
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	static <T extends LeaveCommonVO> T[] replaceFirstElementWithArray(T[] replaceVOs,T[] containerVOs){
//		if(containerVOs.length==1)
//			return replaceVOs;
//		if(replaceVOs.length==1){
//			containerVOs[0]=replaceVOs[0];
//			return containerVOs;
//		}
//		T[] retArray = (T[])Array.newInstance(containerVOs.getClass().getComponentType(), containerVOs.length+replaceVOs.length-1);
//		System.arraycopy(replaceVOs, 0, retArray, 0, replaceVOs.length);
//		System.arraycopy(containerVOs, 1, retArray, replaceVOs.length, containerVOs.length-1);
//		return retArray;
//	}
	
	/**
	 * ������ĵ�һ��Ԫ��Ĩȥ������ʣ�µ�
	 * @param <T>
	 * @param vos
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	static <T extends LeaveCommonVO> T[] removeTop(T[] vos){
//		if(ArrayUtils.isEmpty(vos)||vos.length==1)
//			return null;
//		T[] retArray  = (T[])Array.newInstance(vos.getClass().getComponentType(), vos.length-1);
//		System.arraycopy(vos, 1, retArray, 0, retArray.length);
//		return retArray;
//	}
	
	/**
	 * ��һ��Ԫ�ؼӵ�һ�������ĩβ
	 * @param <T>
	 * @param vos
	 * @param vo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[] addToBottom(T[] vos,T vo){
		if(ArrayUtils.isEmpty(vos)){
			return createOneElementArray(vo);
		}
		T[] retArray  = (T[])Array.newInstance(vos.getClass().getComponentType(), vos.length+1);
		System.arraycopy(vos, 0, retArray, 0, vos.length);
		retArray[retArray.length-1]=vo;
		return retArray;
	}
	
	static <T extends LeaveCommonVO> double sumLength(T[] vos){
		double length = 0;
		for(T vo:vos){
			if(vo.getLeavehour()!=null)
				length+=vo.getLeaveHourValue();
		}
		return length;
	}
	
	static <T extends LeaveCommonVO> double sumLengthExclude(T[] vos,int excludeIndex){
		if(excludeIndex<0||excludeIndex>=vos.length)
			return sumLength(vos);
		double length = 0;
		for(int i=0;i<vos.length;i++){
			if(i==excludeIndex)
				continue;
			T vo = vos[i];
			if(vo.getLeavehour()!=null)
				length+=vo.getLeaveHourValue();
		}
		return length;
	}
	
	/**
	 * balancevo������Чʱ�䷶Χ��map��
	 * @param balanceVOs
	 * @param leaveLengthCalParam
	 * @return
	 * @throws BusinessException 
	 */
	static Map<LeaveBalanceVO, ITimeScope> createBalanceVOEffectiveScopeMap(
			LeaveBalanceVO[] balanceVOs,
			CalParam4OnePerson leaveLengthCalParam) throws BusinessException{
		Map<LeaveBalanceVO, ITimeScope> retMap = new HashMap<LeaveBalanceVO, ITimeScope>();
		for(LeaveBalanceVO vo:balanceVOs){
			ITimeScope effectiveScope = toEffectiveTimeScope(vo, leaveLengthCalParam);
			retMap.put(vo, effectiveScope);
		}
		return retMap;
	}
	
	/**
	 * �ҳ�һ��������ڼ��ڵ�"��ռ��"����ʽ�ǣ���������ڼ����Чʱ�䷶Χ��ȥ������ĳЩ����ڼ��ʱ�䷶Χ
	 * ��ĳЩ�����Ӧ������������
	 * δ����
	 * ����ʱ�����ڵ����ݼ������С��λ������(������С��λ�������Ǳ�����Ҳ��ϸ���ƻ򲻿���ʱ��)
	 * @param <T>
	 * @param balanceVO
	 * @param temporarySplitResult
	 * @param onlyMinusUserInputType true��ֻ��ȥ�������û�¼������ʱ�Σ�false����ȥ����������������������ʱ�� 
	 * @return
	 */
	static <T extends LeaveCommonVO> ITimeScope[] getExclusionScopes(LeaveBalanceVO balanceVO,TemporarySplitResult<T> temporarySplitResult,boolean onlyMinusUserInputType){
		LeaveBalanceVO[] balanceVOs = temporarySplitResult.getBalanceVOs();
		Map<LeaveBalanceVO, ITimeScope> balanceVOEffectiveScopeMap = temporarySplitResult.getBalanceVOEffectiveScopeMap();
		String pk_orileavetype = temporarySplitResult.getOriginalLeaveTypeCopyVO().getPk_timeitem();
		int index = ArrayUtils.indexOf(balanceVOs, balanceVO);
		if(index<0)
			return null;
		ITimeScope effectiveScope = balanceVOEffectiveScopeMap.get(balanceVO);
		if(effectiveScope==null)
			return null;
		ITimeScope[] retScopes = new ITimeScope[]{effectiveScope};
		for(int i=index+1;i<balanceVOs.length;i++){
			if(onlyMinusUserInputType&&!balanceVOs[i].getPk_timeitem().equals(pk_orileavetype))
				continue;
			ITimeScope scope = getRealEffectiveScope(balanceVOs[i], temporarySplitResult);
			if(scope==null)
				continue;
			retScopes = TimeScopeUtils.minusTimeScopes(retScopes, new ITimeScope[]{scope});
			if(ArrayUtils.isEmpty(retScopes))
				return null;
		}
		return retScopes;
	}
	
	/**
	 * �õ�һ��leavebalancevo������֧�ֵ�ʱ��
	 * ���Ѿ����㣬����ʱ������������ζ��������֧�ֵ�ʱ��Ϊ��
	 * @param <T>
	 * @param balanceVO
	 * @param temporarySplitResult
	 * @return
	 */
	private static <T extends LeaveCommonVO> ITimeScope getRealEffectiveScope(LeaveBalanceVO balanceVO,TemporarySplitResult<T> temporarySplitResult){
		if(balanceVO.isSettlement())
			return null;
		String pk_timeitem = balanceVO.getPk_timeitem();
		Map<String, LeaveTypeCopyVO> leaveTypeVOMap=temporarySplitResult.getLeaveTypeVOMap();
		LeaveTypeCopyVO  leaveTypeCopyVO = leaveTypeVOMap.get(pk_timeitem);
		String pk_orileavetype = temporarySplitResult.getOriginalLeaveTypeCopyVO().getPk_timeitem();
		int timeitemUnit = leaveTypeCopyVO.getTimeItemUnit();//ʱ����λ���컹��Сʱ��
		boolean isDayUnit = timeitemUnit==TimeItemCopyVO.TIMEITEMUNIT_DAY;
		double timeUnit0 = leaveTypeCopyVO.getTimeUnit();//��Сʱ��������30���ӣ�0.5�졣���Ϊ0����˵������Сʱ��Ҫ��ʱ��������Ƕ��پ��Ƕ��٣�����ȡ��
		double timeUnit = isDayUnit?timeUnit0:timeUnit0/60;//�������ӼƵ���Сʱ��ת��Ϊ��Сʱ��
		double usefulRestLen = balanceVO.getUsefulRestDayOrHour();//�������ڼ�Ŀ��ý���ʱ�����ɽ���ʱ����ȥ����ʱ��
		boolean lessThanMinUnit = usefulRestLen<timeUnit;
		boolean isUserInputType = pk_timeitem.equals(pk_orileavetype);
		if(lessThanMinUnit&&!isUserInputType)//���ʱ��������һ����С��λ��������ǰ�ü٣������ṩ�κ�֧��
			return null;
		LeaveTypeCopyVO oriType = leaveTypeVOMap.get(pk_orileavetype);
		if(lessThanMinUnit&&isUserInputType&&oriType.isLeaveLimit()&&oriType.isRestrictLimit())//���ʱ������һ����С��λ���������û�¼���ԭʼ��𣬲����ϸ����ʱ������Ҳ�����ṩ�κ�֧��
			return null;
		return temporarySplitResult.getBalanceVOEffectiveScopeMap().get(balanceVO);
	}
	
	/**
	 * ���ݼ�ʱ�η�Ϊ����(ע�⣬�Ƿ��࣬���ǲ��)
	 * ��һ������ȫ������exclusionScopes����ģ��ڶ����ǲ��ְ�����exclusionScopes����ģ�����������ȫ��exclusionScopes֮���
	 * @param <T>
	 * @param vos
	 * @param exclusionScopes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[][] groupScopesByExclusionScopes(T[] vos,ITimeScope[] exclusionScopes){
		T[][] retArray = (T[][]) Array.newInstance(vos.getClass(), 3);
		if(ArrayUtils.isEmpty(exclusionScopes)){
			retArray[2]=vos;
			return retArray;
		}
		if(TimeScopeUtils.contains(exclusionScopes, vos)){
			retArray[0]=vos;
			return retArray;
		}
		List<T> group1 = new ArrayList<T>();
		List<T> group2 = new ArrayList<T>();
		List<T> group3 = new ArrayList<T>();
		for(T vo:vos){
			if(TimeScopeUtils.contains(exclusionScopes, vo)){
				group1.add(vo);
				continue;
			}
			ITimeScope[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes(vo, exclusionScopes);
			if(ArrayUtils.isEmpty(intersectionScopes)){
				group3.add(vo);
				continue;
			}
			group2.add(vo);
		}
		
		if(group1.size()>0)
			retArray[0]=group1.toArray((T[])Array.newInstance(vos.getClass().getComponentType(), group1.size()));
		if(group2.size()>0)
			retArray[1]=group2.toArray((T[])Array.newInstance(vos.getClass().getComponentType(), group2.size()));
		if(group3.size()>0)
			retArray[2]=group3.toArray((T[])Array.newInstance(vos.getClass().getComponentType(), group3.size()));
		return retArray;
	}
	
	/**
	 * ��vos�и�Ϊ�������֣���һ����������exclusionScopes����ģ��ڶ���������exclusionScopes�����
	 * @param <T>
	 * @param vos
	 * @param exclusionScopes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[][] splitScopesByExclusionScopes(T[] vos,ITimeScope[] exclusionScopes,TimeZone timeZone){
		ITimeScope[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes(vos, exclusionScopes);
		ITimeScope[] outsideScopes = TimeScopeUtils.minusTimeScopes(vos, intersectionScopes);
		ITimeScope[][] scopeArrays= new ITimeScope[][]{intersectionScopes,outsideScopes};
		for(int m=0;m<2;m++){
			ITimeScope[] scopeArray = scopeArrays[m];
			if(ArrayUtils.isEmpty(scopeArray))
				continue;
			for(int i=0;i<scopeArray.length;i++){
				ITimeScope scope = scopeArray[i];
				for(T vo:vos){
					if(TimeScopeUtils.contains(vo, scope)){
						T clone = (T)vo.clone();
						scopeArray[i]=clone;
						clone.setLeavebegintime(scope.getScope_start_datetime());
						clone.setLeavebegindate(DateTimeUtils.toLiteralDate(clone.getLeavebegintime(), timeZone));
						clone.setLeaveendtime(scope.getScope_end_datetime());
						clone.setLeaveenddate(DateTimeUtils.toLiteralDate(clone.getLeaveendtime(), timeZone));
						break;
					}
				}
			}
		}
		T[][] retArray = (T[][]) Array.newInstance(vos.getClass(), 2);
		retArray[0]=(T[]) ArrayHelper.cast(intersectionScopes, vos.getClass().getComponentType());
		retArray[1]=(T[]) ArrayHelper.cast(outsideScopes, vos.getClass().getComponentType());
		return retArray;
	}
	/**
	 * �������ݼ�ʱ��������кϲ���
	 * vos1�����м��ڶ��leavebalanceVO�ϣ�vos2ֻ���ڴ����balanceVO��
	 * Ҫ��ֻ�м���balanceVO�ϵļ�¼���ظ��ϲ����Һϲ����ʱ�β�Υ����������ݼٵ��Ƿ��ܱ��桱����
	 * Ҳ����˵�������vos1�У����п����кܶ�ʱ���Ǵ��͵ģ���Ϊ�Ǽ�¼������leavebalancevo�ϵģ����ᱻ����
	 * @param <T>
	 * @param vos
	 * @param canOverYear
	 * @param balanceVO������vo������ʱ���Ѿ�֧����vos1������������ϵ�ʱ��
	 * @param timeZone
	 * @return �ϲ�֮�������
	 * @throws BusinessException 
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[] mergeScopesByBalanceVO(T[] vos1,T[] vos2,boolean canOverYear,
			LeaveBalanceVO balanceVO,LeaveTypeCopyVO typeVO,CalParam4OnePerson leaveLengthCalParam,TimeZone timeZone) throws BusinessException{
		if(ArrayUtils.isEmpty(vos1))
			return vos2;
		if(ArrayUtils.isEmpty(vos2))
			return vos1;
		//��vos1���棬����balanceVO�ϵĺͲ�����balancevo�ϵļ�¼�ֳ�����
		List<T> filteredVOList1 = new ArrayList<T>();//����balanceVO�ϵļ�¼
		List<T> reverseFilteredVOList1 = new ArrayList<T>();//������balanceVO�ϵļ�¼
		String pk_leavetype = balanceVO.getPk_timeitem();
		String period = balanceVO.getCuryear()+(typeVO.isSetPeriodYearORDate()?"":balanceVO.getCurmonth());
		for(T vo:vos1){
			if(!vo.getPk_timeitem().equals(pk_leavetype)){
				reverseFilteredVOList1.add(vo);
				continue;
			}
			String curPeriod = vo.getLeaveyear()+(typeVO.isSetPeriodYearORDate()?"":vo.getLeavemonth());
			if(curPeriod.equals(period)){
				filteredVOList1.add(vo);
				continue;
			}
			reverseFilteredVOList1.add(vo);
		}
		if(filteredVOList1.size()==0){//˵��vos1���棬û�м���balancevo�ϵļ�¼����ֱ�Ӱ���������ϲ�����
			balanceVO.minusRestdayorhour(sumLength(vos2));
			return (T[]) ArrayHelper.addAll(vos1, vos2, vos1.getClass().getComponentType());
		}
		//���vos1�����м���balanceVO�ϵļ�¼����Ҫ��ͼ���ܽ�������ʱ���ƴ����
		T[] mergedVOs = filteredVOList1.toArray((T[]) Array.newInstance(vos1.getClass().getComponentType(), 0));
		double lenBeforeMerge = sumLength(mergedVOs);//�ϲ�ǰ����leavebalancevo֧���˶೤���ݼ�ʱ��
		for(T vo2:vos2){
			MergeResultDescriptor<T> mergeResult = mergeScopeToScopes(mergedVOs, vo2, canOverYear, timeZone);
			mergedVOs = mergeResult.getVos();
		}
		//����ϲ�֮��ĳ���û�䣬��˵����û�з���ʱ��κϲ�����ֱ�Ӱ���������ϲ���������
		if(mergedVOs.length==filteredVOList1.size()+vos2.length){
			balanceVO.minusRestdayorhour(sumLength(vos2));
			return (T[]) ArrayHelper.addAll(vos1, vos2, vos1.getClass().getComponentType());
		}
		//���ȱ��ˣ���˵�������ϲ��ˣ��ϲ�֮����ݼ�ʱ����Ҫ���¼���
		BillProcessHelperAtServer.calLeaveLength4OnePerson(balanceVO.getPk_org(), balanceVO.getPk_psndoc(), mergedVOs, typeVO, leaveLengthCalParam);
		double lenAfterMerge = sumLength(mergedVOs);//�ϲ��󣬼��ڴ�leavebalancevo�ϵ��ݼ�ʱ�εĳ���
		balanceVO.minusRestdayorhour(lenAfterMerge-lenBeforeMerge);//�˴κϲ����µ��������ݼ�ʱ����Ҫleavebalancevo������
		return (T[]) ArrayHelper.addAll(reverseFilteredVOList1.toArray((T[]) Array.newInstance(vos1.getClass().getComponentType(), 0)), mergedVOs, vos1.getClass().getComponentType());
	}
	/**
	 * ��һ�������һ��Ԫ�غϲ����γ�һ���µ�����
	 * �����Ԫ�ؿ��Ժ������е�ĳ��������ĳ����Ԫ�أ��������������Ǻϲ�Ϊһ��Ԫ��
	 * ������������ȵĵ��ݱ��棬��������ͬ��ȵĵ��Ӿ����ܽ���һ��Ҳ��������
	 * @param <T>
	 * @param vos
	 * @param vo
	 * @param canOverYear,����ȵĵ����Ƿ���������
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> MergeResultDescriptor<T> mergeScopeToScopes(T[] vos,T vo,boolean canOverYear,TimeZone timeZone){
		TimeScopeUtils.sort(vos);
		int leftJoinIndex = -1;//vo����˿��Ժ�vos�����ĸ�Ԫ�ص��Ҷ˽�����
		int rightJoinIndex = -1;//vo���Ҷ˿��Ժ�vos������ĸ�Ԫ�ص���˽�����
		for(int i=0;i<vos.length;i++){
			T curVO = vos[i];
			if(curVO.getLeaveendtime().equals(vo.getLeavebegintime())){
				if(!canOverYear&&!curVO.getSimpleyear().equals(vo.getSimpleyear()))//�������ȵ��ݲ��������棬������������Ȳ�һ������ƴ��
					continue;
				leftJoinIndex=i;
				continue;
			}
			if(curVO.getLeavebegintime().equals(vo.getLeaveendtime())){
				if(!canOverYear&&!curVO.getSimpleyear().equals(vo.getSimpleyear()))//�������ȵ��ݲ��������棬������������Ȳ�һ������ƴ��
					continue;
				rightJoinIndex=i;
				continue;
			}
		}
		MergeResultDescriptor<T> result = new MergeResultDescriptor<T>();
		result.setLeftJoinIndex(leftJoinIndex);
		result.setRightJoinIndex(rightJoinIndex);
		//���vo��vos���������һ�����Ӳ��ϣ�������ʵʵ�ؽ��������
		if(leftJoinIndex<0&&rightJoinIndex<0){
			T[] retArray = (T[]) Array.newInstance(vos.getClass().getComponentType(), vos.length+1);
			System.arraycopy(vos, 0, retArray, 0, vos.length);
			retArray[vos.length]=vo;
			result.setVos(retArray);
			return result;
		}
		//�������ܽ����ұ߲��ܽ��ϣ�������߲��ܽ����ұ��ܽ��ϣ��򷵻����鳤�Ȳ���
		if(leftJoinIndex>=0&&rightJoinIndex<0){
			T[] retArray = (T[]) Array.newInstance(vos.getClass().getComponentType(), vos.length);
			System.arraycopy(vos, 0, retArray, 0, retArray.length);
			retArray[leftJoinIndex]=(T) retArray[leftJoinIndex].clone();
			retArray[leftJoinIndex].setLeaveendtime(vo.getLeaveendtime());
			retArray[leftJoinIndex].setLeaveenddate(DateTimeUtils.toLiteralDate(vo.getLeaveendtime(), timeZone));
			result.setVos(retArray);
			return result;
		}
		if(leftJoinIndex<0&&rightJoinIndex>=0){
			T[] retArray = (T[]) Array.newInstance(vos.getClass().getComponentType(), vos.length);
			System.arraycopy(vos, 0, retArray, 0, retArray.length);
			retArray[rightJoinIndex]=(T) vos[rightJoinIndex].clone();
			retArray[rightJoinIndex].setLeavebegintime(vo.getLeavebegintime());
			retArray[rightJoinIndex].setLeavebegindate(DateTimeUtils.toLiteralDate(vo.getLeavebegintime(), timeZone));
			result.setVos(retArray);
			return result;
		}
		//���vo����β���˶��ܺ�vos���Ԫ�ؽ��ϣ���vos�ĳ���Ҫ-1
		T[] retArray = (T[]) Array.newInstance(vos.getClass().getComponentType(), vos.length-1);
		if(leftJoinIndex>=1)
			System.arraycopy(vos, 0, retArray, 0, leftJoinIndex);
		if(rightJoinIndex<=vos.length-2)
			System.arraycopy(vos, rightJoinIndex+1, retArray, rightJoinIndex, vos.length-rightJoinIndex-1);
		retArray[leftJoinIndex]=(T) retArray[leftJoinIndex].clone();
		retArray[leftJoinIndex].setLeaveendtime(vos[rightJoinIndex].getLeaveendtime());
		retArray[leftJoinIndex].setLeaveenddate(DateTimeUtils.toLiteralDate(retArray[leftJoinIndex].getLeaveendtime(), timeZone));
		result.setVos(retArray);
		return result;
	}
	
	/**
	 * ����һ��vo��vos����ϲ��Ľ����class
	 * @author zengcheng
	 *
	 * @param <T>
	 */
	static final class MergeResultDescriptor<T extends LeaveCommonVO>{
		int leftJoinIndex = -1;//vo����˿��Ժ�vos�����ĸ�Ԫ�ص��Ҷ˽�����
		int rightJoinIndex = -1;//vo���Ҷ˿��Ժ�vos������ĸ�Ԫ�ص���˽�����
		T[] vos;//�ϲ��������
		public int getLeftJoinIndex() {
			return leftJoinIndex;
		}
		public void setLeftJoinIndex(int leftJoinIndex) {
			this.leftJoinIndex = leftJoinIndex;
		}
		public int getRightJoinIndex() {
			return rightJoinIndex;
		}
		public void setRightJoinIndex(int rightJoinIndex) {
			this.rightJoinIndex = rightJoinIndex;
		}
		public T[] getVos() {
			return vos;
		}
		public void setVos(T[] vos) {
			this.vos = vos;
		}
	}
	
	/**
	 * �����ݼٽ���/��ʼʱ�䣬Ҫ��leaveFromTime�����Ƴ��Ľ���/��ʼʱ�����ʱ����workScopes�Ľ����ĳ���=seconds
	 * �˷�����ǰ���ǣ�leaveFromTime��workScopes���һ��/��һ��֮���ʱ�εĳ��ȳ�����seconds
	 * ���ص�ʱ��㲻�������һ��
	 * @param leaveFromTime,fromLeft2RightΪtrue��Ϊ��ʼʱ�䣬���������ƣ�fromLeft2RightΪfalse��Ϊ����ʱ�䣬����������
	 * @param shiftWorkScopes
	 * @param seconds
	 * @return
	 */
	static UFDateTime spread(UFDateTime leaveFromTime,ITimeScope[] shiftWorkScopes,long seconds,boolean fromLeft2Right){
		ITimeScope firstWorkScope = shiftWorkScopes[0];
		ITimeScope lastWorkScope = shiftWorkScopes[shiftWorkScopes.length-1];
		UFDateTime shiftBeginTime = firstWorkScope.getScope_start_datetime();//��εĿ�ʼʱ��
		UFDateTime shiftEndTime = lastWorkScope.getScope_end_datetime();//��εĽ���ʱ��
		UFDateTime fromTime = fromLeft2Right?
					DateTimeUtils.max(shiftBeginTime, leaveFromTime):
					DateTimeUtils.min(shiftEndTime, leaveFromTime);
		long sumSeconds = 0;
		for(int i=fromLeft2Right?0:shiftWorkScopes.length-1;fromLeft2Right?i<shiftWorkScopes.length:i>=0;i=i+(fromLeft2Right?1:-1)){
			ITimeScope workScope = shiftWorkScopes[i];
			//�����if��else����Ҫ���жϣ������ǰ�����β��������ݼٶ��н�������continue
			//���fromLeft2Right=true,��fromTime�ڴ˹�����֮����continue(���ڲ���������ʱ��Ĺ����Σ�����ʱ�����Ҳ��Ϊû�н���)
			if(fromLeft2Right){
				if(fromTime.after(workScope.getScope_end_datetime())||(!workScope.isContainsLastSecond()&&fromTime.equals(workScope.getScope_end_datetime())))
					continue;
			}
			//���fromLeft2Right=false����fromTime�ڴ˹�����֮ǰ(���ߵ��ڴ˹����ο�ʼʱ�䣬Ҳ��Ϊ�޽�������Ϊ��ʱfromTime���ݼٽ���ʱ�䣬�ݼٽ���ʱ���ǲ��������һ���)����continue
			else{
				if(!fromTime.after(workScope.getScope_start_datetime()))
					continue;
			}
			//���fromLeft2RightΪtrue��ȡlater(�ݼٿ�ʼʱ�䣬�����ο�ʼʱ��)���˹����ν���ʱ��֮��Ĺ����εĳ���
			//����ȡearlier(�ݼٽ���ʱ�䣬�����ν���ʱ��)���˹����ο�ʼʱ��֮��Ĺ����γ���
			long workScopeLength = fromLeft2Right?
					(!fromTime.after(workScope.getScope_start_datetime())?TimeScopeUtils.getLength(workScope):
				TimeScopeUtils.getLength(new DefaultTimeScope(fromTime, workScope.getScope_end_datetime(), workScope.isContainsLastSecond()))):
					(!fromTime.before(workScope.getScope_end_datetime())?TimeScopeUtils.getLength(workScope):
						TimeScopeUtils.getLength(new DefaultTimeScope(workScope.getScope_start_datetime(),fromTime,false)));
			long tempSumSeconds = sumSeconds+workScopeLength;//���ϴ˹����εĳ��Ⱥ󣬿����ȳ���û��
			//����˹����εĳ��ȼ��Ϻ󣬸պõ���ʱ���������fromLeft2Right=true���ع����ε����һ�루�������ΰ������һ�룬��Ҫ������ʱ��������һ�룬��Ϊ�˷������ص��ǲ��������һ��Ľ���ʱ�䣩
			//fromLeft2Right=false���ع����εĵ�һ��
			if(tempSumSeconds==seconds){
				if(fromLeft2Right)
					return workScope.isContainsLastSecond()?DateTimeUtils.getDateTimeAfterMills(workScope.getScope_end_datetime(), 1000):workScope.getScope_end_datetime();
				return workScope.getScope_start_datetime();
			}
			//����˹����εĳ��ȼ��Ϻ󣬻�С��seconds���򻹿��Ե���һ�������μ�����
			if(tempSumSeconds<seconds){
				sumSeconds=tempSumSeconds;
				continue;
			}
			//����˹����εĳ��ȼ��Ϻ󣬳�����seconds������Ҫ�ڹ���������һ��ʱ��㣬ʹ��leaveBeginTime����ʱ���֮��ĳ��ȸպõ���seconds
			if(fromLeft2Right){
				return DateTimeUtils.getDateTimeAfterMills(DateTimeUtils.max(fromTime, workScope.getScope_start_datetime()), (seconds-sumSeconds)*1000);
			}
			return DateTimeUtils.getDateTimeBeforeMills(DateTimeUtils.min(fromTime, workScope.getScope_end_datetime()), (seconds-sumSeconds)*1000);
			
		}
		throw new IllegalArgumentException("it is a big error!The programm can not walk this way in a normal path!");
	}
	
	static <T extends LeaveCommonVO> T[] minus(T[] vos1,T[] vos2,TimeZone timeZone){
		T[] result = TimeScopeUtils.minusTimeScopesRemainsOriType(vos1, vos2);
		if(ArrayUtils.isEmpty(result))
			return result;
		for(T res:result){
			res.setLeavebegindate(DateTimeUtils.toLiteralDate(res.getLeavebegintime(), timeZone));
			res.setLeaveenddate(DateTimeUtils.toLiteralDate(res.getLeaveendtime(), timeZone));
		}
		return result;
	}
	
	/**
	 * ���𵥽���ϲ�ΪN�����뵥
	 * ע�⣬�˷���û�д������ݺ�
	 * ����˼���ǣ���ͬ�����ͬ���/�ڼ���ݼ�ʱ�η���һ�����뵥�С����������ݼٲ��������棬��Ҫ����ͬ��ȵķ��ڲ�ͬ�����뵥��
	 * @param hVO
	 * @param bVOs
	 * @return
	 */
	static AggLeaveVO[] groupLeavebVOs(LeavehVO hVO,LeavebVO[] bVOs,Map<String, LeaveTypeCopyVO> leaveTypeVOMap,boolean canOverYear){
		if(bVOs.length==1){//���ֻ��һ���ӱ���¼����ܺô��������ӱ��������Ϣ��set��������ȥ����
			return new AggLeaveVO[]{syncBodyToHeadAndCreateAggVO(bVOs, hVO)};
		}
		List<AggLeaveVO> retList = new ArrayList<AggLeaveVO>();
		//����ж����ӱ���¼���������+�ڼ���飬���������������ȱ��棬��ô��ͬ��ȵ�ʱ��β��ñ�����һ�����뵥��
		//���Ȱ�����ְ��֯�������ȷ��飨���ܿ���ְ��֯��
		Map<String,Map<String,Map<String,LeavebVO[]>>> orgTypeYearMap= CommonUtils.group2ArrayByFields(LeavebVO.PK_ORG_V,LeavebVO.PK_LEAVETYPE, LeavebVO.LEAVEYEAR, bVOs);
		//��ҵ��Ԫ���δ���
		for(String pk_org_v:orgTypeYearMap.keySet()){
			Map<String,Map<String,LeavebVO[]>> typeYearMap=orgTypeYearMap.get(pk_org_v);
			//��������δ���
			for(String pk_leavetype: typeYearMap.keySet()){
				Map<String,LeavebVO[]> yearMap = typeYearMap.get(pk_leavetype);
				//�Ƿ��ǰ��ڼ��������
				boolean isMonthPeriod = !leaveTypeVOMap.get(pk_leavetype).isSetPeriodYearORDate();
				for(String year:yearMap.keySet()){//�����ѭ��
					LeavebVO[] yearVOs = yearMap.get(year);
					LeavehVO cloneVO = (LeavehVO)hVO.clone();
					cloneVO.setPk_psnjob(yearVOs[0].getPk_psnjob());
					cloneVO.setPk_org_v(yearVOs[0].getPk_org_v());
					cloneVO.setPk_dept_v(yearVOs[0].getPk_dept_v());
					if(isMonthPeriod){
						processMonthAggVOForOneYear(cloneVO, yearVOs, retList, canOverYear);
						continue;
					}
					processYearDateAggVOForOneYear(cloneVO, yearVOs, retList, canOverYear);
				}
			}
		}
		//������Ҫ��aggvo������һ�������水������ѭ�������Ѿ�������˳�򣩣�������𵥵�˳��
		AggLeaveVO[] aggVOs = retList.toArray(new AggLeaveVO[0]);
		Arrays.sort(aggVOs, new AggLeaveVOComparator());
		String billCode = hVO.getBill_code();
		//��Ϊbillcode���Ǵ�һ��LeavehVO��clone�����ģ����ֻ����һ�������������������һ��leavehvo�У��������Ķ�setΪnull
		if(aggVOs.length>1&&!StringUtils.isEmpty(billCode)){
			for(int i=0;i<aggVOs.length-1;i++){
				aggVOs[i].getHeadVO().setBill_code(null);
			}
		}
		return aggVOs;
	}
	
	/**
	 * �򵥵رȽ��ݼ����뵥��˳���ӱ���ʼʱ����ǰ������ǰ��
	 * @author zengcheng
	 *
	 */
	private static class AggLeaveVOComparator implements Comparator<AggLeaveVO>{

		@Override
		public int compare(AggLeaveVO o1, AggLeaveVO o2) {
			LeavebVO[] bvos1 = o1.getBodyVOs();
			TimeScopeUtils.sort(bvos1);
			LeavebVO[] bvos2 = o2.getBodyVOs();
			TimeScopeUtils.sort(bvos2);
			return bvos1[0].getLeavebegintime().compareTo(bvos2[0].getLeavebegintime());
		}
		
	}
	
	/**
	 * yearVOs�Ѿ���ĳ����/��ְ�ս��������ĳһ�������µ������ݼ�ʱ�Σ���Ҫ�����ǰ�װ��aggvo����
	 * @param hVO
	 * @param yearVOs
	 * @param retList
	 * @param canOverYear
	 */
	private static void processYearDateAggVOForOneYear(LeavehVO hVO,LeavebVO[] yearVOs,List<AggLeaveVO> retList,boolean canOverYear){
		processByNatualYear(hVO, yearVOs, retList, canOverYear);
	}
	
	/**
	 * yearVOs�Ѿ���ĳ���ڼ���������ĳһ�������µ������ݼ�ʱ�Σ���Ҫ�����ǰ�װ��aggvo����
	 * @param hVO
	 * @param yearVOs
	 * @param retList
	 * @param canOverYear
	 */
	private static void processMonthAggVOForOneYear(LeavehVO hVO,LeavebVO[] yearVOs,List<AggLeaveVO> retList,boolean canOverYear){
		Map<String,LeavebVO[]> monthMap = CommonUtils.group2ArrayByField(LeavebVO.LEAVEMONTH, yearVOs);
		for(String month :monthMap.keySet()){
			LeavebVO[] monthVOs = monthMap.get(month);
			processByNatualYear(hVO, monthVOs, retList, canOverYear);
		}
	}
	
	/**
	 * vos�Ѿ������/�ڼ���鵽��������ĩ���������������ǰ�����㣬���Ѿ�����������ˣ�����ǰ��ڼ���㣬���Ѿ����ڼ�������ˣ�
	 * �ڴ�ǰ���£����ǡ������Ƿ���Ա��桱���������������������Ҫ������������Ȼ�����
	 * @param hVO
	 * @param vos
	 * @param retList
	 * @param canOverYear
	 */
	private static void processByNatualYear(LeavehVO hVO,LeavebVO[] vos,List<AggLeaveVO> retList,boolean canOverYear){
		if(vos.length==1||canOverYear){//�������ֻ��һ����¼�������������굥�ݱ��棬��ܼ򵥣����ü���������
			retList.add(syncBodyToHeadAndCreateAggVO(vos, (LeavehVO)hVO.clone()));
			return;
		}
		Map<String, LeavebVO[]> natualYearMap = CommonUtils.group2ArrayByField(LeavebVO.SIMPLEYEAR, vos);
		if(natualYearMap.size()==1){//���ֻ��һ�飬��һ�δ���������ѭ��
			retList.add(syncBodyToHeadAndCreateAggVO(vos, (LeavehVO)hVO.clone()));
			return;
		}
		for(String natualYear:natualYearMap.keySet()){
			retList.add(syncBodyToHeadAndCreateAggVO(natualYearMap.get(natualYear), (LeavehVO)hVO.clone()));
		}
	}
	
	/**
	 * 
	 * @param bVOs
	 * @param hVO
	 * @return
	 */
	static AggLeaveVO syncBodyToHeadAndCreateAggVO(LeavebVO[] bVOs,LeavehVO hVO){
		hVO.setPk_leavetype(bVOs[0].getPk_leavetype());
		hVO.setPk_leavetypecopy(bVOs[0].getPk_leavetypecopy());
		hVO.setLeaveyear(bVOs[0].getLeaveyear());
		hVO.setLeavemonth(bVOs[0].getLeavemonth());
		hVO.setLeaveindex(bVOs[0].getLeaveindex());
		if(bVOs.length==1)
			hVO.setLength(bVOs[0].getLeavehour());
		else
			hVO.setLength(new UFDouble(sumLength(bVOs)));
		AggLeaveVO retVO = new AggLeaveVO();
		retVO.setParentVO(hVO);
		retVO.setChildrenVO(bVOs);
		return retVO;
	}
	
//	/**
//	 * �޸�δ�ύ�����뵥ʱ���ô˷����������ǣ������޸ĵ����뵥��ʱ���Ӷ���ʱ���п۳������������д���
//	 * @param aggVO
//	 * @param balanceVOs
//	 * @throws BusinessException 
//	 */
//	static void processLeaveBalanceVOWhenUpdateAggVO(AggLeaveVO aggVO,LeaveBalanceVO[] balanceVOs) throws BusinessException{
//		String pk_leaveh = aggVO.getHeadVO().getPrimaryKey();
//		//ֻ���޸�ʱ����Ҫ��ô����,����ʱ����Ҫ
//		if(StringUtils.isEmpty(pk_leaveh))
//			return;
//		LeaveBalanceVO balanceVO = search(aggVO.getHeadVO(), balanceVOs);
////		balanceVO.setFreezedayorhour(balanceVO.getFreezedayorhour()==null?);
//	}
//	
//	/**
//	 * ��balanceVOs�У��ҳ�leavehvo��Ӧ�������ڼ�ļ�¼����û�У��򷵻�null
//	 * @param hvo
//	 * @param balanceVOs
//	 * @param leaveTypeVOMap
//	 * @return
//	 */
//	private static LeaveBalanceVO search(LeavehVO hvo,LeaveBalanceVO[] balanceVOs){
//		for(LeaveBalanceVO balanceVO:balanceVOs){
//			//������ͬ��������Ȳ�ͬ������leaveindex��ͬ����϶�����
//			if(!balanceVO.getPk_timeitem().equals(hvo.getPk_timeitem())||!balanceVO.getCuryear().equals(hvo.getLeaveyear())||balanceVO.getLeaveindex().intValue()!=hvo.getLeaveindex().intValue())
//				continue;
//			//�ߵ���������ȣ�leaveindex����ͬ
//			boolean isYearDatePeriod = balanceVO.isYearDateSetPeriod();
//			if(isYearDatePeriod)
//				return balanceVO;
//			if(balanceVO.getCurmonth().equals(hvo.getLeavemonth()))
//				return balanceVO;
//		}
//		return null;
//	}
	
	/**
	 * ������������
	 * ��������ʱ�Ĳ����ν��еģ�һ��ֻ��һ���ݼٵ�������ǰ��ĵ�֮�󣬽���ʱ��������Ӧ�ñ��ٵģ��������û��⣬���
	 * �����ݼٵ���ǣ�����Ľ����¼��Ӧ�ô�ǰ�����ĵ����ù��Ľ����¼��ȡ
	 * @param newestBalanceVOMap
	 * @param balanceVOs
	 */
	static void syncFromNewestBalanceVO(Map<String,LeaveBalanceVO> newestBalanceVOMap,LeaveBalanceVO[] balanceVOs){
		if(ArrayUtils.isEmpty(balanceVOs))
			return;
		for(int i=0;i<balanceVOs.length;i++){
			LeaveBalanceVO balanceVO = balanceVOs[i];
			String key = balanceVO.getPk_timeitem()+balanceVO.getPeriodStr()+balanceVO.getLeaveindex();
			if(!newestBalanceVOMap.containsKey(key)){
				newestBalanceVOMap.put(key, balanceVO);
				continue;
			}
			balanceVOs[i]=newestBalanceVOMap.get(key);
		}
	}
	
	static void processVOStatus(AggLeaveVO oriVO,AggLeaveVO[] splitVOs){
		String oriPk = oriVO.getHeadVO().getPrimaryKey();
		if(StringUtils.isEmpty(oriPk)){//�������������ܼ򵥣����еĶ�Ū��new
			for(AggLeaveVO aggVO:splitVOs){
				LeavehVO hvo = aggVO.getLeavehVO();
				hvo.setStatus(VOStatus.NEW);
				LeavebVO[] bVOs = aggVO.getBodyVOs();
				for(LeavebVO bvo:bVOs){
					bvo.setStatus(VOStatus.NEW);
				}
			}
			return;
		}
		//������޸ģ���Ƚ��鷳���������������ۣ�head���棬ֻ��һ����update�������Ķ���new
		//body���棬headΪnew����ôbody�϶�����new��head��update��body�п�����new��update����delete
		//��������ʽ�򵥻���splitVOs�ĵ�һ��aggvo��head��Ϊupdate,�������е�aggVO����insert
		//Ȼ��ԭʼ��������������ӱ���¼����Ϊdelete��¼�ŵ���һ��aggvo�У���һ��aggvo�������ӱ���¼����Ϊinsert
		AggLeaveVO firstAggVO = splitVOs[0];
		firstAggVO.getHeadVO().setStatus(VOStatus.UPDATED);
		firstAggVO.getHeadVO().setPrimaryKey(oriPk);//��������
		firstAggVO.getHeadVO().setBill_code(oriVO.getHeadVO().getBill_code());//���õ��ݱ���
		firstAggVO.getHeadVO().setCreator(oriVO.getHeadVO().getCreator());
		firstAggVO.getHeadVO().setCreationtime(oriVO.getHeadVO().getCreationtime());
		LeavebVO[] oriBVOs = oriVO.getBodyVOs();
		//ԭʼ���ݵ��ӱ�voҪ��¡����ܷ���firstAggVO����Ϊ���ܸı�ԭʼ���ݵ��κ����ݺ�״̬
		List<LeavebVO> oriBVOsClone = new ArrayList<LeavebVO>(oriBVOs.length);
		for(LeavebVO bvo:oriBVOs){
			LeavebVO clone = (LeavebVO)bvo.clone();
			clone.setStatus(VOStatus.DELETED);
			oriBVOsClone.add(clone);
		}
		LeavebVO[] firstBVOs = firstAggVO.getBodyVOs();
		for(LeavebVO bvo:firstBVOs){
			bvo.setStatus(VOStatus.NEW);
			bvo.setPk_leaveh(oriPk);
			bvo.setPrimaryKey(null);
		}
		firstAggVO.setChildrenVO((LeavebVO[])ArrayHelper.addAll(oriBVOsClone.toArray(new LeavebVO[0]), firstBVOs, LeavebVO.class));
		for(int i=1;i<splitVOs.length;i++){//�ӵڶ�����ʼ�����еĶ�����Ϊnew
			AggLeaveVO aggVO=splitVOs[i];
			LeavehVO hvo = aggVO.getLeavehVO();
			hvo.setPrimaryKey(null);//��������Ϊnull
			hvo.setBill_code(null);//���ݱ�������Ϊnull
			hvo.setStatus(VOStatus.NEW);
			LeavebVO[] bVOs = aggVO.getBodyVOs();
			for(LeavebVO bvo:bVOs){
				bvo.setStatus(VOStatus.NEW);
				bvo.setPrimaryKey(null);
				bvo.setPk_leaveh(null);
			}
		}
	}
	
	static void processVOStatus(LeaveRegVO oriVO,LeaveRegVO[] splitVOs){
		String oriPk = oriVO.getPrimaryKey();
		if(StringUtils.isEmpty(oriPk)){//�������������ܼ򵥣����еĶ�Ū��new
			for(LeaveRegVO regVO:splitVOs){
				regVO.setStatus(VOStatus.NEW);
			}
			return;
		}
		//������޸ģ��򽫵�һ����Ϊupdate������Ķ���Ϊinsert
		LeaveRegVO firstRegVO = splitVOs[0];
		firstRegVO.setStatus(VOStatus.UPDATED);
		firstRegVO.setPrimaryKey(oriPk);//��������
		firstRegVO.setCreator(oriVO.getCreator());
		firstRegVO.setCreationtime(oriVO.getCreationtime());
		for(int i=1;i<splitVOs.length;i++){//�ӵڶ�����ʼ�����еĶ�����Ϊnew
			LeaveRegVO regVO=splitVOs[i];
			regVO.setPrimaryKey(null);//��������Ϊnull
			regVO.setStatus(VOStatus.NEW);
		}
	}
	
	/**
	 * �����뵥�ӱ��е��������޸ĵ�������������ɾ�����ӵ�
	 * @param bvos
	 * @return
	 */
	static LeavebVO[] filterNewAndUpdate(LeavebVO[] bvos){
		List<LeavebVO> retList = new ArrayList<LeavebVO>();
		for(LeavebVO bvo:bvos){
			if(bvo.getStatus()==VOStatus.DELETED)
				continue;
			retList.add(bvo);
		}
		return retList.size()==bvos.length?bvos:retList.toArray(new LeavebVO[0]);
	}
	
	@SuppressWarnings("unchecked")
	static <T extends SuperVO> T[] clone(T[] vos){
		if(ArrayUtils.isEmpty(vos))
			return vos;
		T[] cloneVOs = (T[]) Array.newInstance(vos.getClass().getComponentType(), vos.length);
		for(int i=0;i<vos.length;i++){
			cloneVOs[i]=(T) vos[i].clone();
		}
		return cloneVOs;
	}
	
	/**
	 * ��balancevo�еĽ���ʱ������Ϣͬ�����ݼٵ�vo�У�����ͬʱ����ʱ�����޵���Ϣ��������뵥��ʱ��������balancevo�Ŀ���ʱ��,������-���ᣬ����Ϊʱ�����ޣ�
	 * @param aggVOs
	 * @param balanceVOs
	 * @param typeMap
	 */
	static void syncRestLength(AggLeaveVO[] aggVOs,LeaveBalanceVO[] balanceVOs,Map<String, LeaveTypeCopyVO> typeMap){
		//<pk_timeitem+�ڼ��ֶΣ�balancevo>
		Map<String, LeaveBalanceVO> balanceVOMap = new HashMap<String, LeaveBalanceVO>();
		for(LeaveBalanceVO balanceVO:balanceVOs){
			balanceVOMap.put(balanceVO.getPk_timeitem()+balanceVO.getPeriodStr(), balanceVO);
		}
		for(AggLeaveVO aggVO:aggVOs){
			LeavehVO hvo =aggVO.getLeavehVO();
			String pk_leavetype =hvo.getPk_leavetype();
			LeaveTypeCopyVO typeVO = typeMap.get(pk_leavetype);
			String scopePeriodStr = typeVO.isSetPeriodYearORDate()?hvo.getLeaveyear():(hvo.getLeaveyear()+hvo.getLeavemonth());
			String key = pk_leavetype+scopePeriodStr;
			LeaveBalanceVO balanceVO = balanceVOMap.get(key);
			UFDouble usefuldayorhour = balanceVO.getUsefulrestdayorhour();
			UFBoolean exceedLimit = UFBoolean.valueOf(typeVO.isLeaveLimit()&&aggVO.getHeadVO().getSumhour().compareTo(usefuldayorhour)>0);
			hvo.setRealdayorhour(balanceVO.getRealdayorhour());
			hvo.setRestdayorhour(balanceVO.getRestdayorhour());
			hvo.setResteddayorhour(balanceVO.getYidayorhour());
			hvo.setFreezedayorhour(balanceVO.getFreezedayorhour());
			hvo.setUsefuldayorhour(usefuldayorhour);
			
			//MOD ���ӵ���ʱ�� James
			hvo.setChangelength(balanceVO.getChangelength());
			
			LeavebVO[] bvos = aggVO.getBodyVOs();
			for(LeavebVO bvo:bvos){
				bvo.setRealdayorhour(balanceVO.getRealdayorhour());
				bvo.setRestdayorhour(balanceVO.getRestdayorhour());
				bvo.setResteddayorhour(balanceVO.getYidayorhour());
				bvo.setFreezedayorhour(balanceVO.getFreezedayorhour());
				bvo.setUsefuldayorhour(usefuldayorhour);
				bvo.setExceedlimit(exceedLimit);
			}
			//�������뵥�����Ҫ����ͬ������ʱ���������Ҫ����balanceVO�Ķ���ʱ���ֶ�
			balanceVO.setFreezedayorhour(balanceVO.getFreezedayorhour()==null?aggVO.getHeadVO().getSumhour():balanceVO.getFreezedayorhour().add(aggVO.getHeadVO().getSumhour()));
		}
	}
	
	/**
	 * ��balancevo�Ľ���ʱ������Ϣͬ����vo�У�������ʾʱ��������Ϣ
	 * �����ڵǼǵ��ĵ����𵥣�������/�Ǽǵ���������ʱ�Ĳ�
	 * @param regVOs
	 * @param balanceVOs
	 * @param typeMap
	 */
	static void syncRestLength(LeaveCommonVO[] vos,LeaveBalanceVO[] balanceVOs,Map<String, LeaveTypeCopyVO> typeMap){
		Map<String, LeaveBalanceVO> balanceVOMap = new HashMap<String, LeaveBalanceVO>();
		for(LeaveBalanceVO balanceVO:balanceVOs){
			balanceVOMap.put(balanceVO.getPk_timeitem()+balanceVO.getPeriodStr(), balanceVO);
		}
		for(LeaveCommonVO vo:vos){
			String pk_leavetype =vo.getPk_leavetype();
			LeaveTypeCopyVO typeVO = typeMap.get(pk_leavetype);
			String scopePeriodStr = typeVO.isSetPeriodYearORDate()?vo.getLeaveyear():(vo.getLeaveyear()+vo.getLeavemonth());
			LeaveBalanceVO balanceVO = balanceVOMap.get(pk_leavetype+scopePeriodStr);
			UFDouble usefuldayorhour = balanceVO.getUsefulrestdayorhour();
			vo.setRealdayorhour(balanceVO.getRealdayorhour());
			vo.setRestdayorhour(balanceVO.getRestdayorhour());
			vo.setResteddayorhour(balanceVO.getYidayorhour());
			vo.setFreezedayorhour(balanceVO.getFreezedayorhour());
			vo.setUsefuldayorhour(usefuldayorhour);
			
			//MOD ���ӵ���ʱ�� James
			vo.setChangelength(balanceVO.getChangelength());
			
			vo.setExceedlimit(UFBoolean.valueOf(typeVO.isLeaveLimit()&&vo.getLeavehour().compareTo(usefuldayorhour)>0));
			//���ڵǼǵ��Ǽ�ʱ��Ч�����ǰ��ĵǼǵ��ᵼ�º�������ݡ�����ʱ���ı仯
			if(vo instanceof LeaveRegVO){
				balanceVO.setYidayorhour(balanceVO.getYidayorhour()==null?vo.getLeavehour():balanceVO.getYidayorhour().add(vo.getLeavehour()));
				balanceVO.setRestdayorhour(balanceVO.getRestdayorhour()==null?vo.getLeavehour().multiply(-1):balanceVO.getRestdayorhour().sub(vo.getLeavehour()));
				continue;
			}
			//�������뵥�����Ҫ����ͬ������ʱ���������Ҫ����balanceVO�Ķ���ʱ���ֶ�
			balanceVO.setFreezedayorhour(balanceVO.getFreezedayorhour()==null?vo.getLeavehour():balanceVO.getFreezedayorhour().add(vo.getLeavehour()));
		}
	}
	
	/**
	 * ����������������Ľ���ʱ������Ϣ��������ʾʱ��������Ϣ
	 * �˷������������Ľ����ȫ��������֮��һ�ε��ã��Ǵ�������һ���ݼ�ʱ���͵���һ��
	 * ����ǵǼǵ�����ֱ�ӵ���syncRestLength����
	 * ��������뵥����Ƚϸ��ӣ������ϵġ��ϲ����ݡ�ѡ����߲������ᵼ�½����һ�¡�Ϊ�˼������ʹ�úϲ�����ΪN�Ĵ����߼�
	 * ��һ�����뵥����
	 * @param <T>
	 * @param vos
	 * @param balanceVOs
	 * @param typeMap
	 */
	static <T extends LeaveCommonVO> void syncRestLengthForBatch(T[] vos,LeaveBalanceVO[] balanceVOs,Map<String, LeaveTypeCopyVO> typeMap){
		syncRestLength(vos, balanceVOs, typeMap);
	}
}