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
 * SplitLeaveBillUtils的打杂helper，核心算法都在SplitLeaveBillUtils里面，此类做一些累死牛的各种细节处理
 * 
 * @author zengcheng
 * 
 */
public class SplitLeaveBillHelper {
	/**
	 * 将若干个休假时段，按照休假类别的“申请日期不得晚于开始日期____天”参数，切割成两个部分：第一个部分是违反此参数约束的，
	 * 第二个部分是满足此参数约束的 此校验只对申请单有效，即，对于登记单，视为完全满足此参数
	 * 如果一个时段被分成了两段，那么有可能造成一个完整的班次被分成两半，进而造成时长计算的失真。
	 * 这种情况可以不管，因为这是由单据填写人自己的延误造成的问题，损失应该由自己承担
	 * 
	 * @param <T>
	 * @param vo
	 * @param date
	 * @param timeZone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[][] splitTimeScopeByApplyDatePara(T[] vos, UFLiteralDate applyDate,
			TimeZone timeZone, LeaveTypeCopyVO leaveTypeCopyVO) {
		T[][] retArray = (T[][]) Array.newInstance(vos.getClass(), 2);
		if (!(vos[0] instanceof LeavebVO) || !leaveTypeCopyVO.isLeaveAppTimeLimit()) {// 如果不是申请单，或者休假类别不限制这个，则所有时段都放到第二个元素上
			retArray[1] = vos;
			return retArray;
		}
		TimeScopeUtils.sort(vos);
		// 若第一条的开始日期都不违反此规定，那么肯定所有的都不违反
		UFLiteralDate beginDate = DateTimeUtils.toLiteralDate(vos[0].getLeavebegintime(), timeZone);
		int limitDates = leaveTypeCopyVO.getLeaveAppTimeLimit();
		UFLiteralDate properBeginDate = applyDate.getDateBefore(limitDates);// 不违反规定的最早的休假开始日期，再早就违反规定了
		if (!beginDate.before(properBeginDate)) {
			retArray[1] = vos;
			return retArray;
		}
		// 循环处理这些休假时段
		List<T> breakList = new ArrayList<T>();// 违反规定的记录数组
		List<T> normalList = new ArrayList<T>();// 不违反规定的记录数组
		for (int i = 0; i < vos.length; i++) {
			T vo = vos[i];
			// 将当前这个时段拆分成违反规定的和不违反规定的
			T[] splitVOs = splitTimeScopeByDate(vo, properBeginDate, timeZone);
			if (splitVOs[0] != null)// 违反规定的
				breakList.add(splitVOs[0]);
			if (splitVOs[1] != null)// 不违反的
				normalList.add(splitVOs[1]);
			if (splitVOs[0] == null) {// 如果第一个元素为null，说明当前处理的这个时段不违反规定，进而说明其后续的时段都不违反规定
				for (int j = i + 1; j < vos.length; j++) {
					normalList.add(vos[j]);
				}
				break;
			}
		}
		retArray[0] = breakList.size() == 0 ? null : breakList.toArray((T[]) Array.newInstance(vos.getClass()
				.getComponentType(), breakList.size()));
		retArray[1] = normalList.size() == 0 ? null : normalList.toArray((T[]) Array.newInstance(vos.getClass()
				.getComponentType(), normalList.size()));
		return retArray;
	}

	/**
	 * 如果不允许跨年度保存休假单，则需要检查休假时段是否跨年度，如果跨了，则需要拆分 拆分的时候需要保证一个完整的班次不被拆分
	 * 调用此方的时候，参数肯定是不允许跨年的
	 * 
	 * @param <T>
	 * @param vo
	 * @param timeZone
	 * @param leaveTypeCopyVO
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[] splitTimeScopeByOverYearPara(T[] vos, TimeZone timeZone,
			CalParam4OnePerson leaveLengthCalParam) throws BusinessException {
		List<T> retList = new ArrayList<T>();
		for (T vo : vos) {
			// 如果不跨年，则不管参数是什么，都不用处理（不跨年的场景是绝大多数，因此先用这个判断）
			UFLiteralDate beginDate = DateTimeUtils.toLiteralDate(vo.getLeavebegintime(), timeZone);
			UFLiteralDate endDate = DateTimeUtils.toLiteralDate(vo.getLeaveendtime(), timeZone);
			int beginYearInt = beginDate.getYear();
			int endYearInt = endDate.getYear();
			String beginYear = Integer.toString(beginYearInt);
			String endYear = Integer.toString(endYearInt);
			boolean isEntimeJanFirstZeroOClock = vo.getLeaveendtime().toStdString(timeZone).endsWith("-01-01 00:00:00");
			// 如果开始/结束年份相等，或者结束时间是第二年一月一日的零点，都不算跨年
			if (beginYear.equals(endYear) || ((endYearInt - beginYearInt == 1) && isEntimeJanFirstZeroOClock)) {
				vo.setSimpleyear(beginYear);
				retList.add(vo);
				continue;
			}
			// 若休假时段的开始/结束日期又确确实实地不在同一年，则需要小心处理一下。
			// 本来可以简单处理，即从零点拆成两个单子即可，但这样的话，有可能把一个班次分为两半，造成最后的时长失真。因此需要小心处理，
			// 将班次完整地保存在一个休假时段里面。这样有可能造成休假时长"轻微"地跨年，这是允许的
			// 具体的切割方式，是在时间轴上找到所有的年份变更日(12.31和1.1)，依次找出其最优的切割点。若切割点在休假时间段之外，说明不需要切割
			// 每切割一次，就多出一个时间段
			// 如果开始和结束是前后两年，则处理一次即可；若中间还隔着年，则每多隔一年就多处理一次。注意，若结束时间是一月一日零点，则当成前一年处理
			List<T> splitList = new ArrayList<T>(2);
			splitList.add(vo);
			for (int year = beginYearInt; year < endYearInt - (isEntimeJanFirstZeroOClock ? 1 : 0); year++) {
				UFLiteralDate preDate = UFLiteralDate.getDate(year + "-12-31");
				UFLiteralDate latterDate = UFLiteralDate.getDate((year + 1) + "-01-01");
				UFDateTime splitTime = findSplitTime(preDate, latterDate, leaveLengthCalParam);
				T toSplitVO = splitList.get(splitList.size() - 1);// 需要进行切割的永远都是最后一条记录，之前的都是已经切割好了的
				// 若切割时间超出了休假时间段的范围，则不用切割
				if (!splitTime.after(toSplitVO.getLeavebegintime())) {
					toSplitVO.setSimpleyear(Integer.toString(year + 1));
					continue;
				}
				if (!splitTime.before(toSplitVO.getLeaveendtime())) {
					toSplitVO.setSimpleyear(Integer.toString(year));
					continue;
				}
				T[] splitResult = splitTimeScopeByDateTime(toSplitVO, splitTime, timeZone);
				splitResult[0].setSimpleyear(Integer.toString(year));
				splitResult[1].setSimpleyear(Integer.toString(year + 1));
				splitList.remove(splitList.size() - 1);
				splitList.add(splitResult[0]);
				splitList.add(splitResult[1]);
			}
			retList.addAll(splitList);
		}
		return retList.toArray((T[]) Array.newInstance(vos.getClass().getComponentType(), retList.size()));
	}

	/**
	 * 以某个日期为界，将某个休假时段分为两部分，第一部分全部在date之前，之后部分全部在date之后。若分割结果导致一个完整的班次被切成两半，
	 * 暂时不处理
	 * 
	 * @param <T>
	 * @param vo
	 * @param date
	 * @param timeZone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[] splitTimeScopeByDate(T vo, UFLiteralDate date, TimeZone timeZone) {
		UFLiteralDate beginDate = DateTimeUtils.toLiteralDate(vo.getLeavebegintime(), timeZone);
		UFLiteralDate endDate = DateTimeUtils.toLiteralDate(vo.getLeaveendtime(), timeZone);
		T[] retArray = (T[]) Array.newInstance(vo.getClass(), 2);
		if (!beginDate.before(date)) {
			retArray[1] = vo;
			return retArray;
		}
		if (endDate.before(date)
				|| (endDate.equals(date) && vo.getLeaveendtime().toStdString(timeZone).endsWith("00:00:00"))) {
			retArray[0] = vo;
			return retArray;
		}
		UFDateTime splitTime = new UFDateTime(date + " 00:00:00", timeZone);
		T ret0 = (T) vo.clone();
		retArray[0] = ret0;
		ret0.setEnddate(date);
		ret0.setLeaveendtime(splitTime);
		T ret1 = (T) vo.clone();
		retArray[1] = ret1;
		ret1.setBegindate(date);
		ret1.setLeavebegintime(splitTime);
		return retArray;
	}

	/**
	 * 将一个balancevo的有效日期范围，根据首尾四天的排班信息，生成一个时间段，用来与休假时段做交集，
	 * 以获得此balancevo的有效期范围能支持的休假时段
	 * 本来可以简单地用第一日的0点和最后一日的24点作为分割时间点，但这样简单地分隔有可能将一个完整的班次的时间段分在两个休假时段中
	 * ，可能会造成时长失真， 因为休假时长的计算是以一个休假时段为基础的。因此需要调整一下分割算法，以尽量地保证将一个完整的班次保留在一个休假时段中
	 * 
	 * @param balanceVO
	 * @param leaveLengthCalParam
	 * @return
	 * @throws BusinessException
	 */
	private static ITimeScope toEffectiveTimeScope(LeaveBalanceVO balanceVO, CalParam4OnePerson leaveLengthCalParam)
			throws BusinessException {
		UFLiteralDate beginDate = balanceVO.getPeriodbegindate();// 年度/期间/入职年的开始日期
		UFLiteralDate beginDateBefore1 = beginDate.getDateBefore(1);// 开始日期的前一天

		UFDateTime beginTime = findSplitTime(beginDateBefore1, beginDate, leaveLengthCalParam);

		UFLiteralDate endDateAddExtend = balanceVO.getPeriodextendenddate();// 年度/期间/入职年的结束日期加上有效期延长天数
		UFLiteralDate endDateAddExtendAfter1 = endDateAddExtend.getDateAfter(1);// 有效期结束日的后一天

		UFDateTime endTime = findSplitTime(endDateAddExtend, endDateAddExtendAfter1, leaveLengthCalParam);

		return new DefaultTimeScope(beginTime, endTime, false);
	}

	/**
	 * 根据相邻两天的班次，找到一个最合适的分割时间点，使得一个班次被完整的保留在一个时间段中，而不被分开
	 * 
	 * @param preDate
	 * @param latterDate
	 * @param leaveLengthCalParam
	 * @return
	 * @throws BusinessException
	 */
	private static UFDateTime findSplitTime(UFLiteralDate preDate, UFLiteralDate latterDate,
			CalParam4OnePerson leaveLengthCalParam) throws BusinessException {
		// 相邻两天的工作日历
		AggPsnCalendar preCalendar = leaveLengthCalParam.calendarMap == null ? null : leaveLengthCalParam.calendarMap
				.get(preDate);
		AggPsnCalendar latterCalendar = leaveLengthCalParam.calendarMap == null ? null
				: leaveLengthCalParam.calendarMap.get(latterDate);
		// 相邻两天的班次

		ShiftVO preShiftVO = preCalendar == null ? null : ShiftServiceFacade.getShiftVOFromMap(
				leaveLengthCalParam.shiftMap, preCalendar.getPsnCalendarVO().getPk_shift());
		ShiftVO latterShiftVO = latterCalendar == null ? null : ShiftServiceFacade.getShiftVOFromMap(
				leaveLengthCalParam.shiftMap, latterCalendar.getPsnCalendarVO().getPk_shift());
		// 相邻两天的时区
		TimeZone preTimeZone = leaveLengthCalParam.timeruleVO.getTimeZoneMap().get(
				leaveLengthCalParam.dateOrgMap.get(preDate));
		preTimeZone = preTimeZone == null ? ICalendar.BASE_TIMEZONE : preTimeZone;
		TimeZone latterTimeZone = leaveLengthCalParam.timeruleVO.getTimeZoneMap().get(
				leaveLengthCalParam.dateOrgMap.get(latterDate));
		latterTimeZone = latterTimeZone == null ? ICalendar.BASE_TIMEZONE : latterTimeZone;
		return findSplitTime(preDate, preCalendar, preShiftVO, preTimeZone, latterDate, latterCalendar, latterShiftVO,
				latterTimeZone);
	}

	private static UFDateTime findSplitTime(UFLiteralDate preDate, AggPsnCalendar preAggCalendar, ShiftVO preShiftVO,
			TimeZone preTimeZone, UFLiteralDate latterDate, AggPsnCalendar latterAggCalendar, ShiftVO latterShiftVO,
			TimeZone latterTimeZone) {
		boolean isPreGX = preAggCalendar == null
				|| preAggCalendar.getPsnCalendarVO().getPk_shift().equals(ShiftVO.PK_GX);
		boolean isLatterGX = latterAggCalendar == null
				|| latterAggCalendar.getPsnCalendarVO().getPk_shift().equals(ShiftVO.PK_GX);
		// 如果两天的班次都是公休，则返回零点即可（如果前后两天的时区不一致怎么办？现在暂时用后一天的时区）
		if (isPreGX && isLatterGX)
			return new UFDateTime(latterDate + " 00:00:00", latterTimeZone);
		if (isPreGX && !isLatterGX)
			return latterShiftVO.toKqScope(latterDate.toString(), latterTimeZone).getScope_start_datetime();
		if (!isPreGX && isLatterGX)
			return preShiftVO.toKqScope(preDate.toString(), preTimeZone).getScope_end_datetime();
		// 如果前后两天都不是公休，则看前一天班次的下班时间和后一天班次的上班时间是否越界。如果都没有越界，则用0点做分割
		ITimeScope[] preWorkScopes = preAggCalendar.getPsnWorkTimeVO();
		// 前一日班次的下班时间
		UFDateTime preWorkEndTime = preWorkScopes[preWorkScopes.length - 1].getScope_end_datetime();
		String preWorkEndTimeStr = preWorkEndTime.toStdString(preTimeZone);
		// 前一日的下班时间，如果日期大于当日，则属于越界（下一日的零点不算越界，过了一秒就算越界了）
		boolean isPreBeyond = preWorkEndTimeStr.substring(0, 10).compareTo(preDate.toString()) > 0
				&& !preWorkEndTimeStr.equals(latterDate + " 00:00:00");
		if (isPreBeyond) {// 如果前一天的下班时间越界了，则直接用前一天的下班时间作为分割时间
			return preWorkEndTime;
		}
		ITimeScope[] latterWorkScopes = latterAggCalendar.getPsnWorkTimeVO();
		// 后一日班次的上班时间
		UFDateTime latterWorkBeginTime = latterWorkScopes[0].getScope_start_datetime();
		String latterWorkBeginTimeStr = latterWorkBeginTime.toStdString(latterTimeZone);
		// 后一日的上班时间，如果日期小于当日，则属于越界
		boolean isLatterBeyond = latterWorkBeginTimeStr.substring(0, 10).compareTo(latterDate.toString()) < 0;
		if (isLatterBeyond) {
			return latterWorkBeginTime;
		}
		// 如果两天都没有越界，则使用零点
		return new UFDateTime(latterDate + " 00:00:00", latterTimeZone);
	}

	static TimeZone queryTimeZoneByPkPsnjob(String pk_psnjob, Map<String, TimeZone> psnjobTimeZoneMap)
			throws BusinessException {
		TimeZone timeZone = psnjobTimeZoneMap.get(pk_psnjob);
		if (timeZone != null)
			return timeZone;
		PsnJobVO psnJobVO = (PsnJobVO) new BaseDAO().retrieveByPK(PsnJobVO.class, pk_psnjob);
		timeZone = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZone(psnJobVO.getPk_org());
		psnjobTimeZoneMap.put(pk_psnjob, timeZone);
		return timeZone;
	}

	/**
	 * 从一个时间段数组vos的第一个元素中，挖去一个时间段。有可能把第一个元素挖没了，此时vos的长度减1，也有可能剩下一部分，此时vos长度不变
	 * 
	 * @param <T>
	 * @param vos
	 * @param voMinusLeft
	 *            ，第一个元素挖剩下的时间段
	 */
	// @SuppressWarnings("unchecked")
	// static <T extends LeaveCommonVO> T[] minusFromArrayTopByLeftTimeScope(T[]
	// vos,T voMinusLeft){
	// if(voMinusLeft==null){//如果第一个元素被挖没了，则数组的长度要减一
	// if(vos.length==1)
	// return null;
	// T[] retArray = (T[]) Array.newInstance(vos.getClass().getComponentType(),
	// vos.length-1);
	// System.arraycopy(vos, 1, retArray, 0, vos.length-1);
	// return retArray;
	// }
	// vos[0]=voMinusLeft;
	// return vos;
	// }

	/**
	 * 将voToAdd加到vos数组的最前面。如果vosToAdd的最后一个元素和数组的第一个元素刚好能挨上，则需要合并为一个元素
	 * 调用此方法有个前提：vosToAdd的时间全部在vos的前面
	 * 
	 * @param <T>
	 * @param vos
	 * @param voToAdd
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[] reviveToArray(T[] vos, T[] vosToAdd) {
		int originalLength = vos == null ? 0 : vos.length;
		if (originalLength == 0) {// 如果vos是空的，则直接返回vosToAdd
			return vosToAdd;
		}
		TimeScopeUtils.sort(vosToAdd);
		TimeScopeUtils.sort(vos);
		// 如果vos不空，则还要看vos的第一个元素和vosToAdd的最后一个元素是否能接上，如果能接上，则需要将二者合并再返回
		T lastAddVO = vosToAdd[vosToAdd.length - 1];
		T firstVO = vos[0];
		if (lastAddVO.getScope_end_datetime().equals(firstVO.getScope_start_datetime())) {
			T[] retArray = (T[]) Array.newInstance(vosToAdd.getClass().getComponentType(), originalLength
					+ vosToAdd.length - 1);
			if (vosToAdd.length > 1)
				System.arraycopy(vosToAdd, 0, retArray, 0, vosToAdd.length - 1);
			if (vos.length > 1)
				System.arraycopy(vos, 1, retArray, vosToAdd.length, originalLength - 1);
			T tempVO = (T) lastAddVO.clone();
			tempVO.setScope_end_datetime(firstVO.getScope_end_datetime());
			retArray[vosToAdd.length - 1] = tempVO;
			return retArray;

		}
		// 走到这里，说明不能和任何一个时间段接起来，只有老老实实地创建一个新数组
		T[] retArray = (T[]) Array
				.newInstance(vosToAdd.getClass().getComponentType(), originalLength + vosToAdd.length);
		System.arraycopy(vosToAdd, 0, retArray, 0, vosToAdd.length);
		System.arraycopy(vos, 0, retArray, vosToAdd.length, originalLength);
		TimeScopeUtils.sort(retArray);
		return retArray;
	}

	/**
	 * 将数组的第一个元素取出，包装成一个长度为1的数组返回
	 * 
	 * @param <T>
	 * @param vos
	 * @return
	 */
	// @SuppressWarnings("unchecked")
	// static <T extends LeaveCommonVO> T[] fetchFirstElement(T[] vos){
	// if(vos.length==1)
	// return vos;
	// T[] retArray = (T[])Array.newInstance(vos.getClass().getComponentType(),
	// 1);
	// retArray[0]=vos[0];
	// return retArray;
	// }

	/**
	 * 将vo包装在一个长度为1的数组里面
	 * 
	 * @param <T>
	 * @param vo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[] createOneElementArray(T vo) {
		T[] retArray = (T[]) Array.newInstance(vo.getClass(), 1);
		retArray[0] = vo;
		return retArray;
	}

	/**
	 * 将一个休假时段从一个时间点分成两半。如果时间点在休假时段之外，则只返回长度为1的数组
	 * 
	 * @param <T>
	 * @param vo
	 * @param time
	 * @param timeZone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T extends LeaveCommonVO> T[] splitTimeScopeByDateTime(T vo, UFDateTime time, TimeZone timeZone) {
		if (!time.after(vo.getLeavebegintime()) || !time.before(vo.getLeaveendtime()))
			return createOneElementArray(vo);
		T tempVO = (T) vo.clone();
		vo.setLeaveendtime(time);
		UFLiteralDate date = DateTimeUtils.toLiteralDate(time, timeZone);
		vo.setLeaveenddate(date);
		tempVO.setLeavebegintime(time);
		tempVO.setLeavebegindate(date);
		T[] retArray = (T[]) Array.newInstance(vo.getClass(), 2);
		retArray[0] = vo;
		retArray[1] = tempVO;
		return retArray;
	}

	/**
	 * 将replaceVOs替换到containerVOs的第一个元素中
	 * 例如replaceVOs={a,b},containerVOs={a,c,d}，替换后containerVOs={a,b,c,d}
	 * 
	 * @param <T>
	 * @param replaceVOs
	 * @param containerVOs
	 * @return
	 */
	// @SuppressWarnings("unchecked")
	// static <T extends LeaveCommonVO> T[] replaceFirstElementWithArray(T[]
	// replaceVOs,T[] containerVOs){
	// if(containerVOs.length==1)
	// return replaceVOs;
	// if(replaceVOs.length==1){
	// containerVOs[0]=replaceVOs[0];
	// return containerVOs;
	// }
	// T[] retArray =
	// (T[])Array.newInstance(containerVOs.getClass().getComponentType(),
	// containerVOs.length+replaceVOs.length-1);
	// System.arraycopy(replaceVOs, 0, retArray, 0, replaceVOs.length);
	// System.arraycopy(containerVOs, 1, retArray, replaceVOs.length,
	// containerVOs.length-1);
	// return retArray;
	// }

	/**
	 * 将数组的第一个元素抹去，返回剩下的
	 * 
	 * @param <T>
	 * @param vos
	 * @return
	 */
	// @SuppressWarnings("unchecked")
	// static <T extends LeaveCommonVO> T[] removeTop(T[] vos){
	// if(ArrayUtils.isEmpty(vos)||vos.length==1)
	// return null;
	// T[] retArray = (T[])Array.newInstance(vos.getClass().getComponentType(),
	// vos.length-1);
	// System.arraycopy(vos, 1, retArray, 0, retArray.length);
	// return retArray;
	// }

	/**
	 * 将一个元素加到一个数组的末尾
	 * 
	 * @param <T>
	 * @param vos
	 * @param vo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[] addToBottom(T[] vos, T vo) {
		if (ArrayUtils.isEmpty(vos)) {
			return createOneElementArray(vo);
		}
		T[] retArray = (T[]) Array.newInstance(vos.getClass().getComponentType(), vos.length + 1);
		System.arraycopy(vos, 0, retArray, 0, vos.length);
		retArray[retArray.length - 1] = vo;
		return retArray;
	}

	static <T extends LeaveCommonVO> double sumLength(T[] vos) {
		double length = 0;
		for (T vo : vos) {
			if (vo.getLeavehour() != null)
				length += vo.getLeaveHourValue();
		}
		return length;
	}

	static <T extends LeaveCommonVO> double sumLengthExclude(T[] vos, int excludeIndex) {
		if (excludeIndex < 0 || excludeIndex >= vos.length)
			return sumLength(vos);
		double length = 0;
		for (int i = 0; i < vos.length; i++) {
			if (i == excludeIndex)
				continue;
			T vo = vos[i];
			if (vo.getLeavehour() != null)
				length += vo.getLeaveHourValue();
		}
		return length;
	}

	/**
	 * balancevo与其有效时间范围的map。
	 * 
	 * @param balanceVOs
	 * @param leaveLengthCalParam
	 * @return
	 * @throws BusinessException
	 */
	static Map<LeaveBalanceVO, ITimeScope> createBalanceVOEffectiveScopeMap(LeaveBalanceVO[] balanceVOs,
			CalParam4OnePerson leaveLengthCalParam) throws BusinessException {
		Map<LeaveBalanceVO, ITimeScope> retMap = new HashMap<LeaveBalanceVO, ITimeScope>();
		for (LeaveBalanceVO vo : balanceVOs) {
			ITimeScope effectiveScope = toEffectiveTimeScope(vo, leaveLengthCalParam);
			retMap.put(vo, effectiveScope);
		}
		return retMap;
	}

	/**
	 * 找出一个类别在期间内的"独占区"，方式是，将此类别期间的有效时间范围减去后续的某些类别期间的时间范围 “某些”类别应该满足条件： 未结算
	 * 结余时长大于等于休假类别最小单位，或者(不够最小单位，但是是本类别，且不严格控制或不控制时长)
	 * 
	 * @param <T>
	 * @param balanceVO
	 * @param temporarySplitResult
	 * @param onlyMinusUserInputType
	 *            true，只减去后续的用户录入类别的时段，false，见去后续所有满足条件的类别的时段
	 * @return
	 */
	static <T extends LeaveCommonVO> ITimeScope[] getExclusionScopes(LeaveBalanceVO balanceVO,
			TemporarySplitResult<T> temporarySplitResult, boolean onlyMinusUserInputType) {
		LeaveBalanceVO[] balanceVOs = temporarySplitResult.getBalanceVOs();
		Map<LeaveBalanceVO, ITimeScope> balanceVOEffectiveScopeMap = temporarySplitResult
				.getBalanceVOEffectiveScopeMap();
		String pk_orileavetype = temporarySplitResult.getOriginalLeaveTypeCopyVO().getPk_timeitem();
		int index = ArrayUtils.indexOf(balanceVOs, balanceVO);
		if (index < 0)
			return null;
		ITimeScope effectiveScope = balanceVOEffectiveScopeMap.get(balanceVO);
		if (effectiveScope == null)
			return null;
		ITimeScope[] retScopes = new ITimeScope[] { effectiveScope };
		for (int i = index + 1; i < balanceVOs.length; i++) {
			if (onlyMinusUserInputType && !balanceVOs[i].getPk_timeitem().equals(pk_orileavetype))
				continue;
			ITimeScope scope = getRealEffectiveScope(balanceVOs[i], temporarySplitResult);
			if (scope == null)
				continue;
			retScopes = TimeScopeUtils.minusTimeScopes(retScopes, new ITimeScope[] { scope });
			if (ArrayUtils.isEmpty(retScopes))
				return null;
		}
		return retScopes;
	}

	/**
	 * 得到一个leavebalancevo真正能支持的时段 若已经结算，或者时长不够，都意味着真正能支持的时段为空
	 * 
	 * @param <T>
	 * @param balanceVO
	 * @param temporarySplitResult
	 * @return
	 */
	private static <T extends LeaveCommonVO> ITimeScope getRealEffectiveScope(LeaveBalanceVO balanceVO,
			TemporarySplitResult<T> temporarySplitResult) {
		if (balanceVO.isSettlement())
			return null;
		String pk_timeitem = balanceVO.getPk_timeitem();
		Map<String, LeaveTypeCopyVO> leaveTypeVOMap = temporarySplitResult.getLeaveTypeVOMap();
		LeaveTypeCopyVO leaveTypeCopyVO = leaveTypeVOMap.get(pk_timeitem);
		String pk_orileavetype = temporarySplitResult.getOriginalLeaveTypeCopyVO().getPk_timeitem();
		int timeitemUnit = leaveTypeCopyVO.getTimeItemUnit();// 时长单位，天还是小时。
		boolean isDayUnit = timeitemUnit == TimeItemCopyVO.TIMEITEMUNIT_DAY;
		double timeUnit0 = leaveTypeCopyVO.getTimeUnit();// 最小时长，例如30分钟，0.5天。如果为0，则说明无最小时长要求，时长算出来是多少就是多少，不用取整
		double timeUnit = isDayUnit ? timeUnit0 : timeUnit0 / 60;// 将按分钟计的最小时长转换为按小时计
		double usefulRestLen = balanceVO.getUsefulRestDayOrHour();// 此类别此期间的可用结余时长，由结余时长减去冻结时长
		boolean lessThanMinUnit = usefulRestLen < timeUnit;
		boolean isUserInputType = pk_timeitem.equals(pk_orileavetype);
		if (lessThanMinUnit && !isUserInputType)// 如果时长还不够一个最小单位，并且是前置假，则不能提供任何支持
			return null;
		LeaveTypeCopyVO oriType = leaveTypeVOMap.get(pk_orileavetype);
		if (lessThanMinUnit && isUserInputType && oriType.isLeaveLimit() && oriType.isRestrictLimit())// 如果时长不够一个最小单位，并且是用户录入的原始类别，并且严格控制时长，则也不能提供任何支持
			return null;
		return temporarySplitResult.getBalanceVOEffectiveScopeMap().get(balanceVO);
	}

	/**
	 * 将休假时段分为三类(注意，是分类，不是拆分)
	 * 第一类是完全包含在exclusionScopes里面的，第二类是部分包含在exclusionScopes里面的
	 * ，第三类是完全在exclusionScopes之外的
	 * 
	 * @param <T>
	 * @param vos
	 * @param exclusionScopes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[][] groupScopesByExclusionScopes(T[] vos, ITimeScope[] exclusionScopes) {
		T[][] retArray = (T[][]) Array.newInstance(vos.getClass(), 3);
		if (ArrayUtils.isEmpty(exclusionScopes)) {
			retArray[2] = vos;
			return retArray;
		}
		if (TimeScopeUtils.contains(exclusionScopes, vos)) {
			retArray[0] = vos;
			return retArray;
		}
		List<T> group1 = new ArrayList<T>();
		List<T> group2 = new ArrayList<T>();
		List<T> group3 = new ArrayList<T>();
		for (T vo : vos) {
			if (TimeScopeUtils.contains(exclusionScopes, vo)) {
				group1.add(vo);
				continue;
			}
			ITimeScope[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes(vo, exclusionScopes);
			if (ArrayUtils.isEmpty(intersectionScopes)) {
				group3.add(vo);
				continue;
			}
			group2.add(vo);
		}

		if (group1.size() > 0)
			retArray[0] = group1.toArray((T[]) Array.newInstance(vos.getClass().getComponentType(), group1.size()));
		if (group2.size() > 0)
			retArray[1] = group2.toArray((T[]) Array.newInstance(vos.getClass().getComponentType(), group2.size()));
		if (group3.size() > 0)
			retArray[2] = group3.toArray((T[]) Array.newInstance(vos.getClass().getComponentType(), group3.size()));
		return retArray;
	}

	/**
	 * 将vos切割为两个部分：第一个部分是在exclusionScopes里面的，第二个部分是exclusionScopes外面的
	 * 
	 * @param <T>
	 * @param vos
	 * @param exclusionScopes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[][] splitScopesByExclusionScopes(T[] vos, ITimeScope[] exclusionScopes,
			TimeZone timeZone) {
		ITimeScope[] intersectionScopes = TimeScopeUtils.intersectionTimeScopes(vos, exclusionScopes);
		ITimeScope[] outsideScopes = TimeScopeUtils.minusTimeScopes(vos, intersectionScopes);
		ITimeScope[][] scopeArrays = new ITimeScope[][] { intersectionScopes, outsideScopes };
		for (int m = 0; m < 2; m++) {
			ITimeScope[] scopeArray = scopeArrays[m];
			if (ArrayUtils.isEmpty(scopeArray))
				continue;
			for (int i = 0; i < scopeArray.length; i++) {
				ITimeScope scope = scopeArray[i];
				for (T vo : vos) {
					if (TimeScopeUtils.contains(vo, scope)) {
						T clone = (T) vo.clone();
						scopeArray[i] = clone;
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
		retArray[0] = (T[]) ArrayHelper.cast(intersectionScopes, vos.getClass().getComponentType());
		retArray[1] = (T[]) ArrayHelper.cast(outsideScopes, vos.getClass().getComponentType());
		return retArray;
	}

	/**
	 * 将两个休假时段数组进行合并。 vos1可能有记在多个leavebalanceVO上，vos2只记在传入的balanceVO上
	 * 要求：只有记在balanceVO上的记录你呢个合并，且合并后的时段不违反“跨年度休假单是否能保存”参数
	 * 也就是说，传入的vos1中，很有可能有很多时段是打酱油的，因为是记录在其他leavebalancevo上的，不会被处理
	 * 
	 * @param <T>
	 * @param vos
	 * @param canOverYear
	 * @param balanceVO
	 *            。结算vo，结余时长已经支持了vos1里面记在它身上的时长
	 * @param timeZone
	 * @return 合并之后的数组
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> T[] mergeScopesByBalanceVO(T[] vos1, T[] vos2, boolean canOverYear,
			LeaveBalanceVO balanceVO, LeaveTypeCopyVO typeVO, CalParam4OnePerson leaveLengthCalParam, TimeZone timeZone)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos1))
			return vos2;
		if (ArrayUtils.isEmpty(vos2))
			return vos1;
		// 把vos1里面，计在balanceVO上的和不记在balancevo上的记录分成两组
		List<T> filteredVOList1 = new ArrayList<T>();// 记在balanceVO上的记录
		List<T> reverseFilteredVOList1 = new ArrayList<T>();// 不记在balanceVO上的记录
		String pk_leavetype = balanceVO.getPk_timeitem();
		String period = balanceVO.getCuryear() + (typeVO.isSetPeriodYearORDate() ? "" : balanceVO.getCurmonth());
		for (T vo : vos1) {
			if (!vo.getPk_timeitem().equals(pk_leavetype)) {
				reverseFilteredVOList1.add(vo);
				continue;
			}
			String curPeriod = vo.getLeaveyear() + (typeVO.isSetPeriodYearORDate() ? "" : vo.getLeavemonth());
			if (curPeriod.equals(period)) {
				filteredVOList1.add(vo);
				continue;
			}
			reverseFilteredVOList1.add(vo);
		}
		if (filteredVOList1.size() == 0) {// 说明vos1里面，没有记在balancevo上的记录，则直接把两个数组合并即可
			balanceVO.minusRestdayorhour(sumLength(vos2));
			return (T[]) ArrayHelper.addAll(vos1, vos2, vos1.getClass().getComponentType());
		}
		// 如果vos1里面有记在balanceVO上的记录，则要试图将能接起来的时间段拼起来
		T[] mergedVOs = filteredVOList1.toArray((T[]) Array.newInstance(vos1.getClass().getComponentType(), 0));
		double lenBeforeMerge = sumLength(mergedVOs);// 合并前，此leavebalancevo支持了多长的休假时段
		for (T vo2 : vos2) {
			MergeResultDescriptor<T> mergeResult = mergeScopeToScopes(mergedVOs, vo2, canOverYear, timeZone);
			mergedVOs = mergeResult.getVos();
		}
		// 如果合并之后的长度没变，则说明并没有发生时间段合并，则直接把两个数组合并起来即可
		if (mergedVOs.length == filteredVOList1.size() + vos2.length) {
			balanceVO.minusRestdayorhour(sumLength(vos2));
			return (T[]) ArrayHelper.addAll(vos1, vos2, vos1.getClass().getComponentType());
		}
		// 长度变了，则说明发生合并了，合并之后的休假时长需要重新计算
		BillProcessHelperAtServer.calLeaveLength4OnePerson(balanceVO.getPk_org(), balanceVO.getPk_psndoc(), mergedVOs,
				typeVO, leaveLengthCalParam);
		double lenAfterMerge = sumLength(mergedVOs);// 合并后，记在此leavebalancevo上的休假时段的长度
		balanceVO.minusRestdayorhour(lenAfterMerge - lenBeforeMerge);// 此次合并导致的新增的休假时长需要leavebalancevo来负担
		return (T[]) ArrayHelper.addAll(
				reverseFilteredVOList1.toArray((T[]) Array.newInstance(vos1.getClass().getComponentType(), 0)),
				mergedVOs, vos1.getClass().getComponentType());
	}

	/**
	 * 将一个数组和一个元素合并，形成一个新的数组 如果此元素可以和数组中的某个（或者某两个元素）连起来，则将它们合并为一个元素
	 * 若不允许跨年度的单据保存，则两个不同年度的单子就算能接在一起，也不接起来
	 * 
	 * @param <T>
	 * @param vos
	 * @param vo
	 * @param canOverYear
	 *            ,跨年度的单据是否允许保存
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T extends LeaveCommonVO> MergeResultDescriptor<T> mergeScopeToScopes(T[] vos, T vo, boolean canOverYear,
			TimeZone timeZone) {
		TimeScopeUtils.sort(vos);
		int leftJoinIndex = -1;// vo的左端可以和vos里面哪个元素的右端接起来
		int rightJoinIndex = -1;// vo的右端可以和vos里面的哪个元素的左端接起来
		for (int i = 0; i < vos.length; i++) {
			T curVO = vos[i];
			if (curVO.getLeaveendtime().equals(vo.getLeavebegintime())) {
				if (!canOverYear && !curVO.getSimpleyear().equals(vo.getSimpleyear()))// 如果跨年度单据不允许保存，且两个单子年度不一样，则不拼接
					continue;
				leftJoinIndex = i;
				continue;
			}
			if (curVO.getLeavebegintime().equals(vo.getLeaveendtime())) {
				if (!canOverYear && !curVO.getSimpleyear().equals(vo.getSimpleyear()))// 如果跨年度单据不允许保存，且两个单子年度不一样，则不拼接
					continue;
				rightJoinIndex = i;
				continue;
			}
		}
		MergeResultDescriptor<T> result = new MergeResultDescriptor<T>();
		result.setLeftJoinIndex(leftJoinIndex);
		result.setRightJoinIndex(rightJoinIndex);
		// 如果vo和vos里面的数组一个都接不上，则老老实实地接在最后面
		if (leftJoinIndex < 0 && rightJoinIndex < 0) {
			T[] retArray = (T[]) Array.newInstance(vos.getClass().getComponentType(), vos.length + 1);
			System.arraycopy(vos, 0, retArray, 0, vos.length);
			retArray[vos.length] = vo;
			result.setVos(retArray);
			return result;
		}
		// 如果左边能接上右边不能接上，或者左边不能接上右边能接上，则返回数组长度不变
		if (leftJoinIndex >= 0 && rightJoinIndex < 0) {
			T[] retArray = (T[]) Array.newInstance(vos.getClass().getComponentType(), vos.length);
			System.arraycopy(vos, 0, retArray, 0, retArray.length);
			retArray[leftJoinIndex] = (T) retArray[leftJoinIndex].clone();
			retArray[leftJoinIndex].setLeaveendtime(vo.getLeaveendtime());
			retArray[leftJoinIndex].setLeaveenddate(DateTimeUtils.toLiteralDate(vo.getLeaveendtime(), timeZone));
			result.setVos(retArray);
			return result;
		}
		if (leftJoinIndex < 0 && rightJoinIndex >= 0) {
			T[] retArray = (T[]) Array.newInstance(vos.getClass().getComponentType(), vos.length);
			System.arraycopy(vos, 0, retArray, 0, retArray.length);
			retArray[rightJoinIndex] = (T) vos[rightJoinIndex].clone();
			retArray[rightJoinIndex].setLeavebegintime(vo.getLeavebegintime());
			retArray[rightJoinIndex].setLeavebegindate(DateTimeUtils.toLiteralDate(vo.getLeavebegintime(), timeZone));
			result.setVos(retArray);
			return result;
		}
		// 如果vo的首尾两端都能和vos里的元素接上，则vos的长度要-1
		T[] retArray = (T[]) Array.newInstance(vos.getClass().getComponentType(), vos.length - 1);
		if (leftJoinIndex >= 1)
			System.arraycopy(vos, 0, retArray, 0, leftJoinIndex);
		if (rightJoinIndex <= vos.length - 2)
			System.arraycopy(vos, rightJoinIndex + 1, retArray, rightJoinIndex, vos.length - rightJoinIndex - 1);
		retArray[leftJoinIndex] = (T) retArray[leftJoinIndex].clone();
		retArray[leftJoinIndex].setLeaveendtime(vos[rightJoinIndex].getLeaveendtime());
		retArray[leftJoinIndex].setLeaveenddate(DateTimeUtils.toLiteralDate(retArray[leftJoinIndex].getLeaveendtime(),
				timeZone));
		result.setVos(retArray);
		return result;
	}

	/**
	 * 描述一个vo和vos数组合并的结果的class
	 * 
	 * @author zengcheng
	 * 
	 * @param <T>
	 */
	static final class MergeResultDescriptor<T extends LeaveCommonVO> {
		int leftJoinIndex = -1;// vo的左端可以和vos里面哪个元素的右端接起来
		int rightJoinIndex = -1;// vo的右端可以和vos里面的哪个元素的左端接起来
		T[] vos;// 合并后的数组

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
	 * 反推休假结束/开始时间，要求leaveFromTime到反推出的结束/开始时间这段时段与workScopes的交集的长度=seconds
	 * 此方法的前提是，leaveFromTime到workScopes最后一秒/第一秒之间的时段的长度超过了seconds 返回的时间点不包含最后一秒
	 * 
	 * @param leaveFromTime
	 *            ,fromLeft2Right为true，为开始时间，从左往右推，fromLeft2Right为false，为结束时间，
	 *            从右往左推
	 * @param shiftWorkScopes
	 * @param seconds
	 * @return
	 */
	static UFDateTime spread(UFDateTime leaveFromTime, ITimeScope[] shiftWorkScopes, long seconds,
			boolean fromLeft2Right) {
		ITimeScope firstWorkScope = shiftWorkScopes[0];
		ITimeScope lastWorkScope = shiftWorkScopes[shiftWorkScopes.length - 1];
		UFDateTime shiftBeginTime = firstWorkScope.getScope_start_datetime();// 班次的开始时间
		UFDateTime shiftEndTime = lastWorkScope.getScope_end_datetime();// 班次的结束时间
		UFDateTime fromTime = fromLeft2Right ? DateTimeUtils.max(shiftBeginTime, leaveFromTime) : DateTimeUtils.min(
				shiftEndTime, leaveFromTime);
		long sumSeconds = 0;
		for (int i = fromLeft2Right ? 0 : shiftWorkScopes.length - 1; fromLeft2Right ? i < shiftWorkScopes.length
				: i >= 0; i = i + (fromLeft2Right ? 1 : -1)) {
			ITimeScope workScope = shiftWorkScopes[i];
			// 下面的if和else，主要是判断，如果当前工作段不可能与休假段有交集，则continue
			// 如果fromLeft2Right=true,且fromTime在此工作段之后，则continue(对于不包含结束时间的工作段，两个时间相等也视为没有交集)
			if (fromLeft2Right) {
				if (fromTime.after(workScope.getScope_end_datetime())
						|| (!workScope.isContainsLastSecond() && fromTime.equals(workScope.getScope_end_datetime())))
					continue;
			}
			// 如果fromLeft2Right=false，且fromTime在此工作段之前(或者等于此工作段开始时间，也视为无交集，因为此时fromTime是休假结束时间，休假结束时间是不包含最后一秒的)，则continue
			else {
				if (!fromTime.after(workScope.getScope_start_datetime()))
					continue;
			}
			// 如果fromLeft2Right为true，取later(休假开始时间，工作段开始时间)到此工作段结束时间之间的工作段的长度
			// 否则，取earlier(休假结束时间，工作段结束时间)到此工作段开始时间之间的工作段长度
			long workScopeLength = fromLeft2Right ? (!fromTime.after(workScope.getScope_start_datetime()) ? TimeScopeUtils
					.getLength(workScope) : TimeScopeUtils.getLength(new DefaultTimeScope(fromTime, workScope
					.getScope_end_datetime(), workScope.isContainsLastSecond())))
					: (!fromTime.before(workScope.getScope_end_datetime()) ? TimeScopeUtils.getLength(workScope)
							: TimeScopeUtils.getLength(new DefaultTimeScope(workScope.getScope_start_datetime(),
									fromTime, false)));
			long tempSumSeconds = sumSeconds + workScopeLength;// 加上此工作段的长度后，看长度超了没有
			// 如果此工作段的长度加上后，刚好等于时长，则如果fromLeft2Right=true返回工作段的最后一秒（若工作段包含最后一秒，则要将结束时间往后推一秒，因为此方法返回的是不包含最后一秒的结束时间）
			// fromLeft2Right=false返回工作段的第一秒
			if (tempSumSeconds == seconds) {
				if (fromLeft2Right)
					return workScope.isContainsLastSecond() ? DateTimeUtils.getDateTimeAfterMills(
							workScope.getScope_end_datetime(), 1000) : workScope.getScope_end_datetime();
				return workScope.getScope_start_datetime();
			}
			// 如果此工作段的长度加上后，还小于seconds，则还可以到下一个工作段继续走
			if (tempSumSeconds < seconds) {
				sumSeconds = tempSumSeconds;
				continue;
			}
			// 如果此工作段的长度加上后，超过了seconds，则需要在工作段中找一个时间点，使得leaveBeginTime到此时间点之间的长度刚好等于seconds
			if (fromLeft2Right) {
				return DateTimeUtils
						.getDateTimeAfterMills(DateTimeUtils.max(fromTime, workScope.getScope_start_datetime()),
								(seconds - sumSeconds) * 1000);
			}
			return DateTimeUtils.getDateTimeBeforeMills(DateTimeUtils.min(fromTime, workScope.getScope_end_datetime()),
					(seconds - sumSeconds) * 1000);

		}
		throw new IllegalArgumentException("it is a big error!The programm can not walk this way in a normal path!");
	}

	static <T extends LeaveCommonVO> T[] minus(T[] vos1, T[] vos2, TimeZone timeZone) {
		T[] result = TimeScopeUtils.minusTimeScopesRemainsOriType(vos1, vos2);
		if (ArrayUtils.isEmpty(result))
			return result;
		for (T res : result) {
			res.setLeavebegindate(DateTimeUtils.toLiteralDate(res.getLeavebegintime(), timeZone));
			res.setLeaveenddate(DateTimeUtils.toLiteralDate(res.getLeaveendtime(), timeZone));
		}
		return result;
	}

	/**
	 * 将拆单结果合并为N个申请单 注意，此方法没有处理单据号
	 * 大致思想是，相同类别，相同年度/期间的休假时段放在一个申请单中。如果跨年度休假不允许保存，则还要将不同年度的放在不同的申请单中
	 * 
	 * @param hVO
	 * @param bVOs
	 * @return
	 */
	static AggLeaveVO[] groupLeavebVOs(LeavehVO hVO, LeavebVO[] bVOs, Map<String, LeaveTypeCopyVO> leaveTypeVOMap,
			boolean canOverYear) {
		if (bVOs.length == 1) {// 如果只有一条子表记录，则很好处理，将子表上面的信息项set到主表上去即可
			return new AggLeaveVO[] { syncBodyToHeadAndCreateAggVO(bVOs, hVO) };
		}
		List<AggLeaveVO> retList = new ArrayList<AggLeaveVO>();
		// 如果有多条子表记录，则按照类别+期间分组，且如果不允许跨年度保存，那么不同年度的时间段不该保存在一个申请单中
		// 首先按照任职组织、类别、年度分组（不能跨任职组织）
		Map<String, Map<String, Map<String, LeavebVO[]>>> orgTypeYearMap = CommonUtils.group2ArrayByFields(
				LeavebVO.PK_ORG_V, LeavebVO.PK_LEAVETYPE, LeavebVO.LEAVEYEAR, bVOs);
		// 按业务单元依次处理
		for (String pk_org_v : orgTypeYearMap.keySet()) {
			Map<String, Map<String, LeavebVO[]>> typeYearMap = orgTypeYearMap.get(pk_org_v);
			// 按类别依次处理
			for (String pk_leavetype : typeYearMap.keySet()) {
				Map<String, LeavebVO[]> yearMap = typeYearMap.get(pk_leavetype);
				// 是否是按期间结算的类别
				boolean isMonthPeriod = !leaveTypeVOMap.get(pk_leavetype).isSetPeriodYearORDate();
				for (String year : yearMap.keySet()) {// 按年度循环
					LeavebVO[] yearVOs = yearMap.get(year);
					LeavehVO cloneVO = (LeavehVO) hVO.clone();
					cloneVO.setPk_psnjob(yearVOs[0].getPk_psnjob());
					cloneVO.setPk_org_v(yearVOs[0].getPk_org_v());
					cloneVO.setPk_dept_v(yearVOs[0].getPk_dept_v());
					if (isMonthPeriod) {
						processMonthAggVOForOneYear(cloneVO, yearVOs, retList, canOverYear);
						continue;
					}
					processYearDateAggVOForOneYear(cloneVO, yearVOs, retList, canOverYear);
				}
			}
		}
		// 最终需要把aggvo数组排一下序（上面按类别、年度循环可能已经打乱了顺序），以满足拆单的顺序
		AggLeaveVO[] aggVOs = retList.toArray(new AggLeaveVO[0]);
		Arrays.sort(aggVOs, new AggLeaveVOComparator());
		String billCode = hVO.getBill_code();
		// 因为billcode都是从一个LeavehVO中clone出来的，因此只能有一个被保留（保留在最后一个leavehvo中），其他的都set为null
		if (aggVOs.length > 1 && !StringUtils.isEmpty(billCode)) {
			for (int i = 0; i < aggVOs.length - 1; i++) {
				aggVOs[i].getHeadVO().setBill_code(null);
			}
		}
		return aggVOs;
	}

	/**
	 * 简单地比较休假申请单的顺序，子表开始时间在前的排在前面
	 * 
	 * @author zengcheng
	 * 
	 */
	private static class AggLeaveVOComparator implements Comparator<AggLeaveVO> {

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
	 * yearVOs已经是某按年/入职日结算的类别的某一考勤年下的所有休假时段，需要将它们包装成aggvo数组
	 * 
	 * @param hVO
	 * @param yearVOs
	 * @param retList
	 * @param canOverYear
	 */
	private static void processYearDateAggVOForOneYear(LeavehVO hVO, LeavebVO[] yearVOs, List<AggLeaveVO> retList,
			boolean canOverYear) {
		processByNatualYear(hVO, yearVOs, retList, canOverYear);
	}

	/**
	 * yearVOs已经是某按期间结算的类别的某一考勤年下的所有休假时段，需要将它们包装成aggvo数组
	 * 
	 * @param hVO
	 * @param yearVOs
	 * @param retList
	 * @param canOverYear
	 */
	private static void processMonthAggVOForOneYear(LeavehVO hVO, LeavebVO[] yearVOs, List<AggLeaveVO> retList,
			boolean canOverYear) {
		Map<String, LeavebVO[]> monthMap = CommonUtils.group2ArrayByField(LeavebVO.LEAVEMONTH, yearVOs);
		for (String month : monthMap.keySet()) {
			LeavebVO[] monthVOs = monthMap.get(month);
			processByNatualYear(hVO, monthVOs, retList, canOverYear);
		}
	}

	/**
	 * vos已经按年度/期间分组到了类别的最末级（即，如果类别是按年结算，则已经按年分完组了，如果是按期间结算，则已经按期间分完组了）
	 * 在此前提下，考虑“跨年是否可以保存”参数，如果不允许，则需要继续按所属自然年分组
	 * 
	 * @param hVO
	 * @param vos
	 * @param retList
	 * @param canOverYear
	 */
	private static void processByNatualYear(LeavehVO hVO, LeavebVO[] vos, List<AggLeaveVO> retList, boolean canOverYear) {
		if (vos.length == 1 || canOverYear) {// 如果本月只有一条记录，或者允许跨年单据保存，则很简单，不用继续分组了
			retList.add(syncBodyToHeadAndCreateAggVO(vos, (LeavehVO) hVO.clone()));
			return;
		}
		Map<String, LeavebVO[]> natualYearMap = CommonUtils.group2ArrayByField(LeavebVO.SIMPLEYEAR, vos);
		if (natualYearMap.size() == 1) {// 如果只有一组，则一次处理，不用循环
			retList.add(syncBodyToHeadAndCreateAggVO(vos, (LeavehVO) hVO.clone()));
			return;
		}
		for (String natualYear : natualYearMap.keySet()) {
			retList.add(syncBodyToHeadAndCreateAggVO(natualYearMap.get(natualYear), (LeavehVO) hVO.clone()));
		}
	}

	/**
	 * 
	 * @param bVOs
	 * @param hVO
	 * @return
	 */
	static AggLeaveVO syncBodyToHeadAndCreateAggVO(LeavebVO[] bVOs, LeavehVO hVO) {
		hVO.setPk_leavetype(bVOs[0].getPk_leavetype());
		hVO.setPk_leavetypecopy(bVOs[0].getPk_leavetypecopy());
		hVO.setLeaveyear(bVOs[0].getLeaveyear());
		hVO.setLeavemonth(bVOs[0].getLeavemonth());
		hVO.setLeaveindex(bVOs[0].getLeaveindex());
		if (bVOs.length == 1)
			hVO.setLength(bVOs[0].getLeavehour());
		else
			hVO.setLength(new UFDouble(sumLength(bVOs)));
		AggLeaveVO retVO = new AggLeaveVO();
		retVO.setParentVO(hVO);
		retVO.setChildrenVO(bVOs);
		return retVO;
	}

	// /**
	// * 修改未提交的申请单时调用此方法，作用是，将被修改的申请单的时长从冻结时长中扣除，否则计算会有错误
	// * @param aggVO
	// * @param balanceVOs
	// * @throws BusinessException
	// */
	// static void processLeaveBalanceVOWhenUpdateAggVO(AggLeaveVO
	// aggVO,LeaveBalanceVO[] balanceVOs) throws BusinessException{
	// String pk_leaveh = aggVO.getHeadVO().getPrimaryKey();
	// //只有修改时才需要这么处理,新增时不需要
	// if(StringUtils.isEmpty(pk_leaveh))
	// return;
	// LeaveBalanceVO balanceVO = search(aggVO.getHeadVO(), balanceVOs);
	// // balanceVO.setFreezedayorhour(balanceVO.getFreezedayorhour()==null?);
	// }
	//
	// /**
	// * 从balanceVOs中，找出leavehvo对应的类别和期间的记录，如没有，则返回null
	// * @param hvo
	// * @param balanceVOs
	// * @param leaveTypeVOMap
	// * @return
	// */
	// private static LeaveBalanceVO search(LeavehVO hvo,LeaveBalanceVO[]
	// balanceVOs){
	// for(LeaveBalanceVO balanceVO:balanceVOs){
	// //如果类别不同，或者年度不同，或者leaveindex不同，则肯定不是
	// if(!balanceVO.getPk_timeitem().equals(hvo.getPk_timeitem())||!balanceVO.getCuryear().equals(hvo.getLeaveyear())||balanceVO.getLeaveindex().intValue()!=hvo.getLeaveindex().intValue())
	// continue;
	// //走到这里，类别，年度，leaveindex都相同
	// boolean isYearDatePeriod = balanceVO.isYearDateSetPeriod();
	// if(isYearDatePeriod)
	// return balanceVO;
	// if(balanceVO.getCurmonth().equals(hvo.getLeavemonth()))
	// return balanceVO;
	// }
	// return null;
	// }

	/**
	 * 用于批量新增 由于批增时的拆单依次进行的，一次只拆一个休假单，拆完前面的单之后，结余时长按道理应该变少的，但结果还没入库，因此
	 * 后拆的休假单所牵扯到的结算记录，应该从前面拆过的单子用过的结算记录中取
	 * 
	 * @param newestBalanceVOMap
	 * @param balanceVOs
	 */
	static void syncFromNewestBalanceVO(Map<String, LeaveBalanceVO> newestBalanceVOMap, LeaveBalanceVO[] balanceVOs) {
		if (ArrayUtils.isEmpty(balanceVOs))
			return;
		for (int i = 0; i < balanceVOs.length; i++) {
			LeaveBalanceVO balanceVO = balanceVOs[i];
			String key = balanceVO.getPk_timeitem() + balanceVO.getPeriodStr() + balanceVO.getLeaveindex();
			if (!newestBalanceVOMap.containsKey(key)) {
				newestBalanceVOMap.put(key, balanceVO);
				continue;
			}
			balanceVOs[i] = newestBalanceVOMap.get(key);
		}
	}

	static void processVOStatus(AggLeaveVO oriVO, AggLeaveVO[] splitVOs) {
		String oriPk = oriVO.getHeadVO().getPrimaryKey();
		if (StringUtils.isEmpty(oriPk)) {// 如果是新增，则很简单，所有的都弄成new
			for (AggLeaveVO aggVO : splitVOs) {
				LeavehVO hvo = aggVO.getLeavehVO();
				hvo.setStatus(VOStatus.NEW);
				LeavebVO[] bVOs = aggVO.getBodyVOs();
				for (LeavebVO bvo : bVOs) {
					bvo.setStatus(VOStatus.NEW);
				}
			}
			return;
		}
		// 如果是修改，则比较麻烦，分两个层面讨论：head层面，只有一个是update，其他的都是new
		// body层面，head为new，那么body肯定都是new，head是update，body有可能有new，update，和delete
		// 将处理方式简单化，splitVOs的第一个aggvo的head作为update,后面所有的aggVO都是insert
		// 然后原始单据里面的所有子表记录都作为delete记录放到第一个aggvo中，第一个aggvo的所有子表记录都作为insert
		AggLeaveVO firstAggVO = splitVOs[0];
		firstAggVO.getHeadVO().setStatus(VOStatus.UPDATED);
		firstAggVO.getHeadVO().setPrimaryKey(oriPk);// 设置主键
		firstAggVO.getHeadVO().setBill_code(oriVO.getHeadVO().getBill_code());// 设置单据编码
		firstAggVO.getHeadVO().setCreator(oriVO.getHeadVO().getCreator());
		firstAggVO.getHeadVO().setCreationtime(oriVO.getHeadVO().getCreationtime());
		LeavebVO[] oriBVOs = oriVO.getBodyVOs();
		// 原始单据的子表vo要克隆后才能放入firstAggVO，因为不能改变原始单据的任何数据和状态
		List<LeavebVO> oriBVOsClone = new ArrayList<LeavebVO>(oriBVOs.length);
		for (LeavebVO bvo : oriBVOs) {
			LeavebVO clone = (LeavebVO) bvo.clone();
			clone.setStatus(VOStatus.DELETED);
			oriBVOsClone.add(clone);
		}
		LeavebVO[] firstBVOs = firstAggVO.getBodyVOs();
		for (LeavebVO bvo : firstBVOs) {
			bvo.setStatus(VOStatus.NEW);
			bvo.setPk_leaveh(oriPk);
			bvo.setPrimaryKey(null);
		}
		firstAggVO.setChildrenVO((LeavebVO[]) ArrayHelper.addAll(oriBVOsClone.toArray(new LeavebVO[0]), firstBVOs,
				LeavebVO.class));
		for (int i = 1; i < splitVOs.length; i++) {// 从第二个开始，所有的都设置为new
			AggLeaveVO aggVO = splitVOs[i];
			LeavehVO hvo = aggVO.getLeavehVO();
			hvo.setPrimaryKey(null);// 主键设置为null
			hvo.setBill_code(null);// 单据编码设置为null
			hvo.setStatus(VOStatus.NEW);
			LeavebVO[] bVOs = aggVO.getBodyVOs();
			for (LeavebVO bvo : bVOs) {
				bvo.setStatus(VOStatus.NEW);
				bvo.setPrimaryKey(null);
				bvo.setPk_leaveh(null);
			}
		}
	}

	static void processVOStatus(LeaveRegVO oriVO, LeaveRegVO[] splitVOs) {
		String oriPk = oriVO.getPrimaryKey();
		if (StringUtils.isEmpty(oriPk)) {// 如果是新增，则很简单，所有的都弄成new
			for (LeaveRegVO regVO : splitVOs) {
				regVO.setStatus(VOStatus.NEW);
			}
			return;
		}
		// 如果是修改，则将第一个作为update，后面的都作为insert
		LeaveRegVO firstRegVO = splitVOs[0];
		firstRegVO.setStatus(VOStatus.UPDATED);
		firstRegVO.setPrimaryKey(oriPk);// 设置主键
		firstRegVO.setCreator(oriVO.getCreator());
		firstRegVO.setCreationtime(oriVO.getCreationtime());
		for (int i = 1; i < splitVOs.length; i++) {// 从第二个开始，所有的都设置为new
			LeaveRegVO regVO = splitVOs[i];
			regVO.setPrimaryKey(null);// 主键设置为null
			regVO.setStatus(VOStatus.NEW);
		}
	}

	/**
	 * 将申请单子表中的新增和修改的数据挑出来，删除的扔掉
	 * 
	 * @param bvos
	 * @return
	 */
	static LeavebVO[] filterNewAndUpdate(LeavebVO[] bvos) {
		List<LeavebVO> retList = new ArrayList<LeavebVO>();
		for (LeavebVO bvo : bvos) {
			if (bvo.getStatus() == VOStatus.DELETED)
				continue;
			retList.add(bvo);
		}
		return retList.size() == bvos.length ? bvos : retList.toArray(new LeavebVO[0]);
	}

	@SuppressWarnings("unchecked")
	static <T extends SuperVO> T[] clone(T[] vos) {
		if (ArrayUtils.isEmpty(vos))
			return vos;
		T[] cloneVOs = (T[]) Array.newInstance(vos.getClass().getComponentType(), vos.length);
		for (int i = 0; i < vos.length; i++) {
			cloneVOs[i] = (T) vos[i].clone();
		}
		return cloneVOs;
	}

	/**
	 * 将balancevo中的结余时长等信息同步到休假的vo中，并且同时设置时长超限的信息（如果申请单的时长超过了balancevo的可用时长,即结余-
	 * 冻结，则视为时长超限）
	 * 
	 * @param aggVOs
	 * @param balanceVOs
	 * @param typeMap
	 */
	static void syncRestLength(AggLeaveVO[] aggVOs, LeaveBalanceVO[] balanceVOs, Map<String, LeaveTypeCopyVO> typeMap) {
		// <pk_timeitem+期间字段，balancevo>
		Map<String, LeaveBalanceVO> balanceVOMap = new HashMap<String, LeaveBalanceVO>();
		for (LeaveBalanceVO balanceVO : balanceVOs) {
			balanceVOMap.put(balanceVO.getPk_timeitem() + balanceVO.getPeriodStr(), balanceVO);
		}
		for (AggLeaveVO aggVO : aggVOs) {
			LeavehVO hvo = aggVO.getLeavehVO();
			String pk_leavetype = hvo.getPk_leavetype();
			LeaveTypeCopyVO typeVO = typeMap.get(pk_leavetype);
			String scopePeriodStr = typeVO.isSetPeriodYearORDate() ? hvo.getLeaveyear() : (hvo.getLeaveyear() + hvo
					.getLeavemonth());
			String key = pk_leavetype + scopePeriodStr;
			LeaveBalanceVO balanceVO = balanceVOMap.get(key);
			UFDouble usefuldayorhour = balanceVO.getUsefulrestdayorhour();
			UFBoolean exceedLimit = UFBoolean.valueOf(typeVO.isLeaveLimit()
					&& aggVO.getHeadVO().getSumhour().compareTo(usefuldayorhour) > 0);
			hvo.setRealdayorhour(balanceVO.getRealdayorhour());
			hvo.setRestdayorhour(balanceVO.getRestdayorhour());
			hvo.setResteddayorhour(balanceVO.getYidayorhour());
			hvo.setFreezedayorhour(balanceVO.getFreezedayorhour());
			hvo.setUsefuldayorhour(usefuldayorhour);

			LeavebVO[] bvos = aggVO.getBodyVOs();
			for (LeavebVO bvo : bvos) {
				bvo.setRealdayorhour(balanceVO.getRealdayorhour());
				bvo.setRestdayorhour(balanceVO.getRestdayorhour());
				bvo.setResteddayorhour(balanceVO.getYidayorhour());
				bvo.setFreezedayorhour(balanceVO.getFreezedayorhour());
				bvo.setUsefuldayorhour(usefuldayorhour);
				bvo.setExceedlimit(exceedLimit);
			}
			// 由于申请单保存后要立即同步冻结时长，因此需要处理balanceVO的冻结时长字段
			balanceVO.setFreezedayorhour(balanceVO.getFreezedayorhour() == null ? aggVO.getHeadVO().getSumhour()
					: balanceVO.getFreezedayorhour().add(aggVO.getHeadVO().getSumhour()));
		}
	}

	/**
	 * 将balancevo的结余时长等信息同步到vo中，并且提示时长超限信息 适用于登记单的单个拆单，与申请/登记的批量新增时的拆单
	 * 
	 * @param regVOs
	 * @param balanceVOs
	 * @param typeMap
	 */
	static void syncRestLength(LeaveCommonVO[] vos, LeaveBalanceVO[] balanceVOs, Map<String, LeaveTypeCopyVO> typeMap) {
		Map<String, LeaveBalanceVO> balanceVOMap = new HashMap<String, LeaveBalanceVO>();
		for (LeaveBalanceVO balanceVO : balanceVOs) {
			balanceVOMap.put(balanceVO.getPk_timeitem() + balanceVO.getPeriodStr(), balanceVO);
		}
		for (LeaveCommonVO vo : vos) {
			String pk_leavetype = vo.getPk_leavetype();
			LeaveTypeCopyVO typeVO = typeMap.get(pk_leavetype);
			String scopePeriodStr = typeVO.isSetPeriodYearORDate() ? vo.getLeaveyear() : (vo.getLeaveyear() + vo
					.getLeavemonth());
			LeaveBalanceVO balanceVO = balanceVOMap.get(pk_leavetype + scopePeriodStr);
			UFDouble usefuldayorhour = balanceVO.getUsefulrestdayorhour();
			vo.setRealdayorhour(balanceVO.getRealdayorhour());
			vo.setRestdayorhour(balanceVO.getRestdayorhour());
			vo.setResteddayorhour(balanceVO.getYidayorhour());
			vo.setFreezedayorhour(balanceVO.getFreezedayorhour());
			vo.setUsefuldayorhour(usefuldayorhour);

			vo.setExceedlimit(UFBoolean.valueOf(typeVO.isLeaveLimit()
					&& vo.getLeavehour().compareTo(usefuldayorhour) > 0));
			// 由于登记单是即时生效，因此前面的登记单会导致后面的已休、结余时长的变化
			if (vo instanceof LeaveRegVO) {
				balanceVO.setYidayorhour(balanceVO.getYidayorhour() == null ? vo.getLeavehour() : balanceVO
						.getYidayorhour().add(vo.getLeavehour()));
				balanceVO.setRestdayorhour(balanceVO.getRestdayorhour() == null ? vo.getLeavehour().multiply(-1)
						: balanceVO.getRestdayorhour().sub(vo.getLeavehour()));
				continue;
			}
			// 由于申请单保存后要立即同步冻结时长，因此需要处理balanceVO的冻结时长字段
			balanceVO.setFreezedayorhour(balanceVO.getFreezedayorhour() == null ? vo.getLeavehour() : balanceVO
					.getFreezedayorhour().add(vo.getLeavehour()));
		}
	}

	/**
	 * 处理批量新增结果的结余时长等信息，并且提示时长超限信息 此方法不是批量的结果完全处理完了之后一次调用，是处理完了一个休假时长就调用一次
	 * 如果是登记单，则直接调用syncRestLength即可
	 * 如果是申请单，则比较复杂：界面上的“合并单据”选项勾或者不勾，会导致结果不一致。为了简单起见，使用合并单据为N的处理逻辑 在一张申请单里面
	 * 
	 * @param <T>
	 * @param vos
	 * @param balanceVOs
	 * @param typeMap
	 */
	static <T extends LeaveCommonVO> void syncRestLengthForBatch(T[] vos, LeaveBalanceVO[] balanceVOs,
			Map<String, LeaveTypeCopyVO> typeMap) {
		syncRestLength(vos, balanceVOs, typeMap);
	}
}
