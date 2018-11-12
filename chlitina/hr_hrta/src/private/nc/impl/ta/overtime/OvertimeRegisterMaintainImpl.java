package nc.impl.ta.overtime;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.hr.frame.persistence.HrBatchService;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.FromWhereSQLUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.algorithm.BillProcessHelperAtServer;
import nc.impl.ta.algorithm.BillValidatorAtServer;
import nc.impl.ta.overtime.validator.OvertimeRegDeleteValidator;
import nc.impl.ta.overtime.validator.OvertimeRegValidator;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.ta.CheckTimeServiceFacade;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.IOvertimeRegisterManageMaintain;
import nc.itf.ta.IOvertimeRegisterQueryMaintain;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeDataQueryService;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.algorithm.BillProcessHelper;
import nc.itf.ta.algorithm.CheckTimeUtils;
import nc.itf.ta.algorithm.CheckTimeUtilsWithCheckFlag;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.DateTimeUtils;
import nc.itf.ta.algorithm.ICheckTime;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.itf.ta.algorithm.TimeScopeUtils;
import nc.itf.ta.algorithm.impl.DefaultTimeScope;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.holiday.HRHolidayVO;
import nc.vo.ta.log.TaBusilogUtil;
import nc.vo.ta.overtime.OvertimeCommonVO;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.overtime.register.validator.SaveOvertimeRegValidator;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnWorkTimeVO;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.pub.IMetaDataIDConst;
import nc.vo.ta.pub.PubPermissionUtils;
import nc.vo.ta.pub.TaBillRegQueryParams;
import nc.vo.ta.pub.TaNormalQueryUtils;
import nc.vo.ta.timeitem.OverTimeTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class OvertimeRegisterMaintainImpl implements IOvertimeRegisterQueryMaintain, IOvertimeRegisterManageMaintain {

    private HrBatchService serviceTemplate;
    private ISegDetailService ovSegService;

    private HrBatchService getServiceTemplate() {
	if (serviceTemplate == null) {
	    serviceTemplate = new HrBatchService(IMetaDataIDConst.OVERTIME);
	}
	return serviceTemplate;
    }

    public ISegDetailService getOvSegService() {
	if (ovSegService == null) {
	    ovSegService = NCLocator.getInstance().lookup(ISegDetailService.class);
	}

	return ovSegService;
    }

    @Override
    public OvertimeRegVO queryByPk(String pk) throws BusinessException {
	return getServiceTemplate().queryByPk(OvertimeRegVO.class, pk);
    }

    @Override
    public OvertimeRegVO[] doCheck(OvertimeRegVO[] vos) throws BusinessException {
	if (ArrayUtils.isEmpty(vos)) {
	    return null;
	}
	// for(OvertimeRegVO vo:vos){
	// PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(),
	// vo.getBegindate(), vo.getEnddate());
	// }
	Map<String, OvertimeRegVO[]> psnRegVOMap = CommonUtils.group2ArrayByField(OvertimeCommonVO.PK_PSNDOC, vos);
	String[] pk_psndocs = psnRegVOMap.keySet().toArray(new String[0]);
	String pk_org = vos[0].getPk_org();
	IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(vos);
	PeriodServiceFacade.checkMonth(pk_org, pk_psndocs, maxDateScope.getBegindate(), maxDateScope.getEnddate());

	PeriodVO[] periods = NCLocator.getInstance().lookup(IPeriodQueryService.class)
		.queryPeriodsByDateScope(pk_org, maxDateScope.getBegindate(), maxDateScope.getEnddate());
	// 取班次信息和考勤规则
	Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
	Map<String, ShiftVO> shiftMap = CommonMethods.createShiftMapFromAggShiftMap(aggShiftMap);
	TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
	Map<String, TimeZone> timeZoneMap = timeRule.getTimeZoneMap();
	BillMethods.processBeginEndDatePkJobOrgTimeZone(vos, timeZoneMap);
	// 将需要校验的数据按人员分组
	Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = NCLocator.getInstance()
		.lookup(ITBMPsndocQueryService.class).queryDateJobOrgMapByPsndocInSQL(pk_psndocs, vos);
	// 取刷签卡信息
	Map<String, ICheckTime[]> psnCheckTimeMap = CheckTimeServiceFacade.queryCheckTimeMapByPsnsAndDateScope(pk_org,
		pk_psndocs, maxDateScope.getBegindate(), maxDateScope.getEnddate());
	String psndocInSql = new InSQLCreator().getInSQL(pk_psndocs);
	// 工作日历信息
	Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = NCLocator.getInstance()
		.lookup(IPsnCalendarQueryService.class).queryCalendarVOByPsnInSQL(pk_org, vos, 6, psndocInSql);
	// 考勤数据信息
	Map<String, Map<UFLiteralDate, TimeDataVO>> psnTimedataMap = NCLocator.getInstance()
		.lookup(ITimeDataQueryService.class)
		.queryVOMapByPsndocInSQL(pk_org, maxDateScope.getBegindate(), maxDateScope.getEnddate(), psndocInSql);
	// 宽松还是严格
	Integer strictType = timeRule.getOvertimecheckrule();
	boolean isStrict = strictType == null || strictType.intValue() == 0;// 默认是严格
	// V63添加，若是计算加班时长要考虑假日排班记为加班的情况,此时批量查询出需要的信息
	Map<String, HRHolidayVO[]> psnOverTimeHolidayScope = BillProcessHelperAtServer.getOverTimeHolidayScope(vos);

	List<OvertimeRegVO> result = new ArrayList<OvertimeRegVO>();
	// 存放旧数据的map
	Map<String, Integer> deductMap = new HashMap<String, Integer>();
	Map<String, UFDouble> hourMap = new HashMap<String, UFDouble>();
	for (String pk_psndoc : psnRegVOMap.keySet()) {
	    OvertimeRegVO[] regVOs = psnRegVOMap.get(pk_psndoc);
	    Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap) ? null : psnDateOrgMap
		    .get(pk_psndoc);
	    Map<UFLiteralDate, TimeZone> dateTimeZoneMap = CommonMethods.createDateTimeZoneMap(dateOrgMap, timeZoneMap);
	    Map<UFLiteralDate, TimeDataVO> timeDataMap = MapUtils.isEmpty(psnTimedataMap) ? null : psnTimedataMap
		    .get(pk_psndoc);
	    Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap) ? null : psnCalendarMap
		    .get(pk_psndoc);
	    ICheckTime[] checkTimes = MapUtils.isEmpty(psnCheckTimeMap) ? null : psnCheckTimeMap.get(pk_psndoc);
	    HRHolidayVO[] PsnEnjoyHrHolidayVOs = psnOverTimeHolidayScope.get(pk_psndoc);
	    // 依次校验此人的所有加班单
	    for (OvertimeRegVO vo : regVOs) {
		// 不需要校验或已校验或已转调休不需要校验
		if (!vo.getIsneedcheck().booleanValue() || vo.getIscheck().booleanValue()
			|| vo.getIstorest().booleanValue()) {
		    continue;
		}
		if (!BillMethods.isDateScopeInPeriod(periods, vo)) {
		    continue;
		}
		doCheck(isStrict, vo, aggShiftMap, timeDataMap, shiftMap, dateTimeZoneMap, calendarMap, checkTimes,
			timeRule, PsnEnjoyHrHolidayVOs);
		// 重新设置开始日期和结束日期
		CommonMethods.processBeginEndDate(vo, timeZoneMap);
		Integer tmpDiff = vo.getDiffhour() == null ? 0 : vo.getDiffhour().intValue();// 单位为分（假日加班异常时长）
		deductMap.put(vo.getPk_overtimereg(), vo.getDeduct());
		hourMap.put(vo.getPk_overtimereg(), vo.getOvertimehour());
		vo.setDeduct(vo.getDeduct() + tmpDiff);
		result.add(vo);
		// TimeItemCopyVO overtimeItem =
		// overtimeTypeMap.get(vo.getPk_overtimetype());
		// //1-天，2-小时,默认小时
		// int timeitemUnit =
		// overtimeItem.getTimeitemunit()==null?TimeItemCopyVO.TIMEITEMUNIT_HOUR:overtimeItem.getTimeitemunit().intValue();
		// UFDouble exceptionDiff = UFDouble.ZERO_DBL;
		// if(timeitemUnit==TimeItemCopyVO.TIMEITEMUNIT_HOUR){
		// exceptionDiff = new UFDouble(tmpDiff/60.00);
		// }else{
		// exceptionDiff = (new UFDouble(tmpDiff/60.00)).div(new
		// UFDouble(timeRule.getDaytohour2()));
		// }
	    }
	}
	if (CollectionUtils.isEmpty(result))
	    return null;
	OvertimeRegVO[] rsVOs = result.toArray(new OvertimeRegVO[0]);
	// 计算时长
	BillProcessHelperAtServer.calOvertimeLength(pk_org, rsVOs);
	// 给属性重新赋值
	for (OvertimeRegVO rsVO : rsVOs) {
	    rsVO.setOvertimehour(hourMap.get(rsVO.getPk_overtimereg()));
	    rsVO.setDiffhour(rsVO.getOvertimehour().sub(rsVO.getActhour()));
	    rsVO.setIscheck(UFBoolean.TRUE);
	    rsVO.setDeduct(deductMap.get(rsVO.getPk_overtimereg()));
	}
	rsVOs = getServiceTemplate().update(true, rsVOs);
	// 业务日志
	TaBusilogUtil.writeOvertimeRegCheckBusiLog(rsVOs);
	return rsVOs;
    }

    /**
     * 校验加班登记单据
     * 
     * @param isStrict
     *            是否严格校验
     * @param vo
     * @param aggShiftMap
     * @param shiftMap
     * @param timeRule
     * @param psnEnjoyHrHolidayVOs
     * @throws BusinessException
     */
    private void doCheck(boolean isStrict, OvertimeRegVO vo, Map<String, AggShiftVO> aggShiftMap,
	    Map<UFLiteralDate, TimeDataVO> timeDataMap, Map<String, ShiftVO> shiftMap,
	    Map<UFLiteralDate, TimeZone> dateTimeZoneMap, Map<UFLiteralDate, AggPsnCalendar> calendarMap,
	    ICheckTime[] allCheckTimes, TimeRuleVO timeRule, HRHolidayVO[] psnEnjoyHrHolidayVOs)
	    throws BusinessException {
	// 是否启用门禁
	boolean isCheckWithStatus = timeRule.getCheckinflag().booleanValue();
	// 获取单据归属日
	// UFLiteralDate belongDate = new OvertimeDAO().getBelongDate(vo,
	// shiftMap, dateTimeZoneMap);
	UFLiteralDate belongDate = new OvertimeDAO().getBelongDate(vo, shiftMap, calendarMap, dateTimeZoneMap);
	ITimeScope filterScope = CommonMethods.getKQScope4OvertimeCheckAndGen(belongDate, 5, shiftMap, calendarMap,
		dateTimeZoneMap);
	// 取刷签卡信息
	ICheckTime[] checkTimes = CheckTimeUtils.filterOrderedTimesYN(allCheckTimes,
		filterScope.getScope_start_datetime(), filterScope.getScope_end_datetime());
	if (ArrayUtils.isEmpty(checkTimes)) {
	    resetCheckValue(vo);
	    return;
	}
	Arrays.sort(checkTimes);
	// 取考勤数据处理信息
	// Map<String, Map<UFLiteralDate, TimeDataVO>> timeDataMap =
	// NCLocator.getInstance().lookup(ITimeDataQueryService.class).queryVOMapByPsndocInSQL(vo.getPk_org(),
	// belongDate, belongDate, "'"+vo.getPk_psndoc()+"'");
	// 没有刷签卡信息或没有设置工作日历或没有考勤数据处理数据直接重置
	if (MapUtils.isEmpty(calendarMap) || calendarMap.get(belongDate) == null
		|| calendarMap.get(belongDate).getPsnCalendarVO() == null
		|| StringUtils.isBlank(calendarMap.get(belongDate).getPsnCalendarVO().getPk_shift())
	// ||
	// timeDataMap==null || timeDataMap.get(vo.getPk_psndoc())==null ||
	// timeDataMap.get(vo.getPk_psndoc()).get(belongDate)==null
	) {
	    resetCheckValue(vo);
	    return;
	}
	String pk_shift = calendarMap.get(belongDate).getPsnCalendarVO().getPk_shift();
	// 公休日加班校验
	if (ShiftVO.PK_GX.equals(pk_shift)) {
	    // 公休日加班校验
	    if (isCheckWithStatus) {
		// 门禁公休日加班校验
		checkGXBillsWithFlag(isStrict, vo, checkTimes);
		return;
	    }
	    // 非门禁公休日加班校验
	    checkGXBills(isStrict, vo, checkTimes);
	    return;
	}
	// 工作日加班校验
	ShiftVO workShift = shiftMap.get(pk_shift);
	AggPsnCalendar aggCalendar = calendarMap.get(belongDate);
	// 由规定上班到规定下班组成的时间段。如果是弹性班，则需要固化
	ITimeScope workScope = aggCalendar.getPsnCalendarVO().isFlexibleFinal() ? OvertimeGenMaintainImpl
		.getSolidifyWorkTimeScope(vo.getPk_org(), vo.getPk_psndoc(), belongDate, timeRule, aggShiftMap,
			calendarMap, dateTimeZoneMap) : aggCalendar.toWorkScope();

	try {
	    // 单据开始时间在规定下班时间之后校验延时加班，否则校验提前加班
	    if (vo.getOvertimebegintime().compareTo(workScope.getScope_end_datetime()) >= 0) {
		// 校验工作日延时加班（门禁/非门禁）
		if (isCheckWithStatus) {
		    // 门禁工作日延时加班校验
		    checkDelayWorkBillsWithFlag(vo, workScope, workShift, checkTimes);
		    return;
		}
		// 非门禁工作日延时加班校验
		checkDelayWorkBills(isStrict, vo, workScope, workShift, checkTimes);
		return;
	    }
	    // 校验工作日提前加班（门禁/非门禁）
	    if (isCheckWithStatus) {
		// 门禁工作日提前加班校验
		checkBeforeWorkBillsWithFlag(vo, workScope, workShift, checkTimes);
		return;
	    }
	    // 非门禁工作日提前加班校验
	    checkBeforeWorkBills(isStrict, vo, workScope, workShift, checkTimes, psnEnjoyHrHolidayVOs);
	    return;
	} finally {
	    // 对假日排班的情况需要再校验一下假日排班记为加班的的时间段
	    checkHolidayOvertime(vo, timeDataMap, psnEnjoyHrHolidayVOs, isCheckWithStatus, checkTimes, workScope,
		    aggCalendar, workShift);
	}
    }

    /**
     * V63添加，校验假日排班记为加班的时间段
     * 假日时间段可能只是工作时间段的一部分，单据的加班时间有可能和假日时间有交集，也有可能包括非假日且非工作时间段的部分
     * 
     * @param vo
     * @param psnEnjoyHrHolidayVOs
     * @param isCheckWithStatus
     * @param checkTimes
     * @param workScope
     * @param aggCalendar
     * @param workShift
     * @throws BusinessException
     */
    private void checkHolidayOvertime(OvertimeRegVO vo, Map<UFLiteralDate, TimeDataVO> timeDataMap,
	    HRHolidayVO[] psnEnjoyHrHolidayVOs, boolean isCheckWithStatus, ICheckTime[] checkTimes,
	    ITimeScope workScope, AggPsnCalendar aggCalendar, ShiftVO workShift) throws BusinessException {
	if (checkTimes.length < 2) {
	    return;
	}
	// 假日排班记为加班，上面处理完成后加班时间需要加上假日时间段和上班时间段的交集
	ITimeScope[] holidayScopes = TimeScopeUtils.intersectionTimeScopes(workScope, psnEnjoyHrHolidayVOs);
	if (ArrayUtils.isEmpty(holidayScopes)) {
	    return;
	}
	// 再和实际的上下班签卡时间取交集
	// ITimeScope realscope = new DefaultTimeScope();
	// if(isCheckWithStatus){
	// //最早的in卡
	// ICheckTime earliestIn =
	// CheckTimeUtilsWithCheckFlag.findOrderedEarliest(checkTimes,
	// ICheckTime.CHECK_FLAG_IN);
	// //最晚的out卡
	// ICheckTime latestout =
	// CheckTimeUtilsWithCheckFlag.findOrderedLatest(checkTimes,
	// ICheckTime.CHECK_FLAG_OUT);
	// realscope.setScope_start_datetime(earliestIn.getDatetime());
	// realscope.setScope_end_datetime(latestout.getDatetime());
	// }else{
	// realscope.setScope_start_datetime(checkTimes[0].getDatetime());
	// realscope.setScope_end_datetime(checkTimes[checkTimes.length-1].getDatetime());
	// }
	// TimeDataVO[] timeDatavos =
	// NCLocator.getInstance().lookup(ITimeDataQueryMaintain.class).queryByPsn(vo.getPk_org(),
	// vo.getPk_psndoc(), vo.getBegindate(), vo.getEnddate());

	UFLiteralDate[] dates = CommonUtils.createDateArray(vo.getBegindate(), vo.getEnddate());
	// 取各时间段的实际的上下班时间
	PsnWorkTimeVO[] wtvos = aggCalendar.getPsnWorkTimeVO();
	if (ArrayUtils.isEmpty(wtvos))
	    return;
	TimeScopeUtils.sort(wtvos);
	int length = wtvos.length;
	List<TimeDataVO> timeDataList = new ArrayList<TimeDataVO>();
	List<ITimeScope> realScopesList = new ArrayList<ITimeScope>();
	// 20151210,判断是否进行考勤数据处理，未处理则取刷卡数据
	if (timeDataMap != null && timeDataMap.size() > 0) {
	    for (UFLiteralDate date : dates) {
		TimeDataVO timeData = timeDataMap.get(date);
		if (timeData == null)
		    continue;
		timeDataList.add(timeData);
	    }
	    if (!CollectionUtils.isEmpty(timeDataList)) {
		TimeDataVO timeDataVO = timeDataList.get(0);
		for (int i = 0; i < length; i++) {
		    ITimeScope realScope = new DefaultTimeScope();
		    if (timeDataVO.getBegintime(i) != null && timeDataVO.getEndtime(i) != null) {
			realScope.setScope_start_datetime(timeDataVO.getBegintime(i));
			realScope.setScope_end_datetime(timeDataVO.getEndtime(i));
			realScopesList.add(realScope);
		    }
		}
	    }
	}
	if (CollectionUtils.isEmpty(realScopesList)) {
	    ITimeScope realScope = new DefaultTimeScope();
	    realScope.setScope_start_datetime(checkTimes[0].getDatetime());
	    realScope.setScope_end_datetime(checkTimes[checkTimes.length - 1].getDatetime());
	    realScopesList.add(realScope);
	}

	ITimeScope[] realScopes = realScopesList.toArray(new ITimeScope[0]);

	holidayScopes = TimeScopeUtils.intersectionTimeScopes(realScopes, holidayScopes);

	// 记录原加班时间，处理后的时间不能超过登记填写的原始时间
	ITimeScope orignalScope = new DefaultTimeScope();
	orignalScope.setScope_start_datetime(vo.getBegintimebeforecheck());
	orignalScope.setScope_end_datetime(vo.getEndtimebeforecheck());
	holidayScopes = TimeScopeUtils.intersectionTimeScopes(orignalScope, holidayScopes);
	// 到此处holidayScopes为假日时间、工作时间、实际上下班（签卡）时间、单据申请时间四者的交集

	if (ArrayUtils.isEmpty(holidayScopes)) {
	    return;
	}
	// 单据的实际加班时间有肯能还包括非工作时间段、申请时间段、实际签卡时间段的交集。
	ITimeScope[] notworkScops = TimeScopeUtils.minusTimeScopes(new ITimeScope[] { orignalScope },
		new ITimeScope[] { workScope });
	// 此处非工作时间段中可能含有延时中不记为加班的时间段（比如从下班时间到开始记为下班延时加班的时间段），应该扣除掉
	// 上班延迟中不记为加班的部分
	List<ITimeScope> notInclude = new ArrayList<ITimeScope>();
	if (workShift.getUseontmrule() != null && workShift.getUseontmrule().booleanValue()) {
	    ITimeScope notIncludBeforScope = new DefaultTimeScope(workShift.getOntmend().multiply(60).longValue(),
		    workScope.getScope_start_datetime());
	    notInclude.add(notIncludBeforScope);
	}
	// 下班延迟中不记为加班的部分
	if (workShift.getUseovertmrule() != null && workShift.getUseovertmrule().booleanValue()) {
	    ITimeScope notIncludAfterScope = new DefaultTimeScope(workScope.getScope_end_datetime(), workShift
		    .getOvertmbegin().multiply(60).longValue());
	    notInclude.add(notIncludAfterScope);
	}
	// ITimeScope[] notIncludScopes = new
	// ITimeScope[]{notIncludBeforScope,notIncludAfterScope};
	if (CollectionUtils.isNotEmpty(notInclude)) {
	    notworkScops = TimeScopeUtils.minusTimeScopes(notworkScops, notInclude.toArray(new ITimeScope[0]));
	}

	notworkScops = TimeScopeUtils.intersectionTimeScopes(notworkScops, realScopes);// 实际签卡的非工作时间

	ITimeScope[] allScopes = TimeScopeUtils.mergeTimeScopes(holidayScopes, notworkScops);

	UFDateTime earliesStartTime = TimeScopeUtils.getEarliesStartTime(allScopes);
	// 若开始时间早于上班时间还要判断是否到了记为加班的开始时间
	if (earliesStartTime.before(workScope.getScope_start_datetime())) {
	    if (workShift.getUseontmrule() != null && workShift.getUseontmrule().booleanValue()) {
		if (earliesStartTime.after(DateTimeUtils.getDateTimeBeforeMills(workScope.getScope_start_datetime(),
			workShift.getOntmbeyond().multiply(60 * 1000).longValue())))
		    earliesStartTime = workScope.getScope_start_datetime();
	    } else {// 上班前不计加班
		earliesStartTime = workScope.getScope_start_datetime();
	    }
	}
	UFDateTime latestEndTime = TimeScopeUtils.getLatestEndTime(allScopes);
	// 若结束时间晚于下班时间，还要判断是否到了可以记为延时加班的时间
	if (latestEndTime.after(workScope.getScope_end_datetime())) {
	    if (workShift.getUseovertmrule() != null && workShift.getUseovertmrule().booleanValue()) {
		if (latestEndTime.before(DateTimeUtils.getDateTimeAfterMills(workScope.getScope_end_datetime(),
			workShift.getOvertmbeyond().multiply(60 * 1000).longValue())))
		    latestEndTime = workScope.getScope_end_datetime();
	    } else {// 下班后不计加班
		latestEndTime = workScope.getScope_end_datetime();
	    }
	}
	if (vo.getOvertimebegintime().compareTo(vo.getOvertimeendtime()) == 0) {
	    vo.setOvertimebegintime(earliesStartTime);
	    vo.setOvertimeendtime(latestEndTime);
	} else {
	    if (earliesStartTime.before(vo.getOvertimebegintime())) {
		vo.setOvertimebegintime(earliesStartTime);
	    }
	    if (latestEndTime.after(vo.getOvertimeendtime())) {
		vo.setOvertimeendtime(latestEndTime);
	    }
	}
	// 扣除时长
	Long diffLength = Long.valueOf(0);

	// 上班时可能产生缺勤、迟到、早退等时长，应扣除,记差异时长
	// 取应上下班时间段
	PsnWorkTimeVO[] psnWorkTimeVOs = aggCalendar.getPsnWorkTimeVO();
	// 扣除时长=（应上下班时间段-实际上下班时间段）∩假日时间段∩单据
	ITimeScope[] exceptionScops = TimeScopeUtils.minusTimeScopes(psnWorkTimeVOs, realScopes);
	exceptionScops = TimeScopeUtils.intersectionTimeScopes(exceptionScops, new ITimeScope[] { vo });
	exceptionScops = TimeScopeUtils.intersectionTimeScopes(exceptionScops, psnEnjoyHrHolidayVOs);
	if (!ArrayUtils.isEmpty(exceptionScops)) {
	    diffLength += TimeScopeUtils.getLength(exceptionScops) / 60;
	}

	// //延时加班中应扣除的时长,时长计算中已经扣除了，此处不需要重复扣除
	// ITimeScope[] minusScopes =
	// TimeScopeUtils.intersectionTimeScopes(notIncludScopes,new
	// ITimeScope[]{vo});
	// if(!ArrayUtils.isEmpty(minusScopes)){
	// diffLength += TimeScopeUtils.getLength(minusScopes)/60;
	// }
	// return;
	// long length2 = TimeScopeUtils.getLength(exceptionScops)/60;
	// vo.setDiffhour(new UFDouble(length2));
	vo.setDiffhour(new UFDouble(diffLength));
    }

    /**
     * 非门禁公休加班校验
     * 
     * @param isStrict
     * @param vo
     * @param kqScope
     * @param checkTimes
     */
    private void checkGXBills(boolean isStrict, OvertimeRegVO vo, ICheckTime[] checkTimes) {// 以单据开始时间和结束时间将刷签卡信息分组
	if (!isStrict) {// 如果是宽松校验，则很简单，用第一条刷签卡到最后一条刷签卡组成的时间段与vo取交集即可
	    if (ArrayUtils.getLength(checkTimes) < 2) {
		resetCheckValue(vo);
		return;
	    }
	    ITimeScope scope = new DefaultTimeScope(checkTimes[0].getDatetime(),
		    checkTimes[checkTimes.length - 1].getDatetime());
	    ITimeScope interScope = TimeScopeUtils.intersectionTimeScope(scope, vo);
	    if (interScope == null || interScope.getScope_end_datetime().equals(interScope.getScope_start_datetime())) {
		resetCheckValue(vo);
		return;
	    }
	    vo.setBegintimebeforecheck(vo.getOvertimebegintime());
	    vo.setEndtimebeforecheck(vo.getOvertimeendtime());
	    vo.setOvertimebegintime(interScope.getScope_start_datetime());
	    vo.setOvertimeendtime(interScope.getScope_end_datetime());
	    return;
	}
	ICheckTime[] preCheckTimes = CheckTimeUtils.filterOrderedTimesYN(checkTimes, checkTimes[0].getDatetime(),
		vo.getOvertimebegintime());
	ICheckTime[] curCheckTimes = CheckTimeUtils.filterOrderedTimesYY(checkTimes, vo.getOvertimebegintime(),
		vo.getOvertimeendtime());
	ICheckTime[] nxtCheckTimes = CheckTimeUtils.filterOrderedTimesNY(checkTimes, vo.getOvertimeendtime(),
		checkTimes[checkTimes.length - 1].getDatetime());

	int size = curCheckTimes.length;
	if (size >= 2) {
	    vo.setBegintimebeforecheck(vo.getOvertimebegintime());
	    vo.setEndtimebeforecheck(vo.getOvertimeendtime());
	    vo.setOvertimebegintime(curCheckTimes[size - 2].getDatetime());
	    vo.setOvertimeendtime(curCheckTimes[size - 1].getDatetime());
	    return;
	}
	if (size == 1) {
	    if (!ArrayUtils.isEmpty(nxtCheckTimes)) {
		vo.setBegintimebeforecheck(vo.getOvertimebegintime());
		vo.setEndtimebeforecheck(vo.getOvertimeendtime());
		vo.setOvertimebegintime(curCheckTimes[0].getDatetime());
		return;
	    }
	    if (!ArrayUtils.isEmpty(preCheckTimes)) {
		vo.setBegintimebeforecheck(vo.getOvertimebegintime());
		vo.setEndtimebeforecheck(vo.getOvertimeendtime());
		vo.setOvertimeendtime(curCheckTimes[0].getDatetime());
		return;
	    }
	    resetCheckValue(vo);
	    return;
	}
	if (ArrayUtils.isEmpty(preCheckTimes) || ArrayUtils.isEmpty(nxtCheckTimes)) {
	    resetCheckValue(vo);
	    return;
	}
	// 如果都存在刷签卡数据，单据时间不用改变
	vo.setBegintimebeforecheck(vo.getOvertimebegintime());
	vo.setEndtimebeforecheck(vo.getOvertimeendtime());
	return;
    }

    /**
     * 门禁公休日加班校验
     * 
     * @param isStrict
     *            是否严格校验。目前严格和宽松是一样的算法，因此此参数在这里无用
     * @param vo
     * @param kqScope
     * @param checkTimes
     */
    private void checkGXBillsWithFlag(boolean isStrict, OvertimeRegVO vo, ICheckTime[] checkTimes) {
	// 在连续公休的范围内，找出最早的in和最晚的out，然后与单据取交集
	ICheckTime earliestIn = CheckTimeUtilsWithCheckFlag.findOrderedEarliest(checkTimes, ICheckTime.CHECK_FLAG_IN);
	if (earliestIn == null) {
	    resetCheckValue(vo);
	    return;
	}
	ICheckTime lastOut = CheckTimeUtilsWithCheckFlag.findOrderedLatest(checkTimes, ICheckTime.CHECK_FLAG_OUT);
	if (lastOut == null || !lastOut.getDatetime().after(earliestIn.getDatetime())) {
	    resetCheckValue(vo);
	    return;
	}
	ITimeScope scope = TimeScopeUtils.intersectionTimeScope(vo, new DefaultTimeScope(earliestIn.getDatetime(),
		lastOut.getDatetime()));
	if (scope == null || scope.getScope_start_datetime().equals(scope.getScope_end_datetime())) {
	    resetCheckValue(vo);
	    return;
	}
	vo.setBegintimebeforecheck(vo.getOvertimebegintime());
	vo.setEndtimebeforecheck(vo.getOvertimeendtime());
	vo.setOvertimebegintime(scope.getScope_start_datetime());
	vo.setOvertimeendtime(scope.getScope_end_datetime());
    }

    /**
     * 非门禁平日延时加班校验
     * 
     * @param isStrict
     *            是否严格校验
     * @param vo
     * @param workScope
     * @param checkTimes
     * @param kqScope
     */
    private void checkDelayWorkBills(boolean isStrict, OvertimeRegVO vo, ITimeScope workScope, ShiftVO shiftVO,
	    ICheckTime[] checkTimes) {
	// 基本思想是，先构造两个时间段：延时加班的起点（班次定义中的参数）到下班后（含）的第一张卡是一个时间段（有可能没有），下班后（含）的第一张卡之后的所有卡第一张到最后一张是
	// 一个时间段（宽松），或者最后两条为一个时间段（严格）
	// 这两个时间段与填写的单据取交集，若交集结果大于两条，则取长的一条
	// 规定下班时间
	UFDateTime ruleOffDutyTime = workScope.getScope_end_datetime();
	// 过滤出从规定下班时间（含）之后的刷签卡数据
	ICheckTime[] filteredCheckTimes = CheckTimeUtils.filterOrderedTimesYY(checkTimes, ruleOffDutyTime,
		checkTimes[checkTimes.length - 1].getDatetime());
	if (ArrayUtils.isEmpty(filteredCheckTimes)) {
	    resetCheckValue(vo);
	    return;
	}
	ITimeScope overtimeRuleScope = shiftVO.getOvertimeRuleScope(ruleOffDutyTime, true);
	List<ITimeScope> scopes = new ArrayList<ITimeScope>(2);// 用来和加班单交集的时间段数组
	ICheckTime firstCheckTimeAfterOffDuty = filteredCheckTimes[0];// 下班后的第一张卡
	if (overtimeRuleScope != null
		&& !firstCheckTimeAfterOffDuty.getDatetime().before(overtimeRuleScope.getScope_end_datetime())) {
	    if (firstCheckTimeAfterOffDuty.getDatetime().after(overtimeRuleScope.getScope_start_datetime())) {
		scopes.add(new DefaultTimeScope(overtimeRuleScope.getScope_start_datetime(), firstCheckTimeAfterOffDuty
			.getDatetime()));
	    }
	}
	// 如果下班后（含）的刷签卡次数>=3次，则：
	// if 严格，最后两张凑成一对
	// if 宽松，第二张和最后一张凑成一对
	if (filteredCheckTimes.length >= 3) {
	    UFDateTime beginTime = isStrict ? filteredCheckTimes[filteredCheckTimes.length - 2].getDatetime()
		    : filteredCheckTimes[1].getDatetime();
	    UFDateTime endTime = filteredCheckTimes[filteredCheckTimes.length - 1].getDatetime();// 最后一张刷签卡
	    ITimeScope scope = new DefaultTimeScope(beginTime, endTime);
	    if (overtimeRuleScope == null) {
		scopes.add(scope);
	    } else {
		if (!endTime.before(overtimeRuleScope.getScope_end_datetime())) {
		    beginTime = DateTimeUtils.max(overtimeRuleScope.getScope_start_datetime(), beginTime);
		    if (scope.getScope_end_datetime().after(beginTime)) {
			scope.setScope_start_datetime(beginTime);
			scopes.add(scope);
		    }
		}
	    }
	}
	if (scopes.size() == 0) {
	    resetCheckValue(vo);
	    return;
	}
	ITimeScope[] interScopes = TimeScopeUtils.intersectionTimeScopes(vo, scopes.toArray(new ITimeScope[0]));
	if (ArrayUtils.isEmpty(interScopes)) {
	    resetCheckValue(vo);
	    return;
	}
	ITimeScope finalScope = findLongger(interScopes);// 最终交集的结果；如果交出了两个，则取长的为结果

	if (finalScope.getScope_start_datetime().equals(finalScope.getScope_end_datetime())) {
	    resetCheckValue(vo);
	    return;
	}
	vo.setBegintimebeforecheck(vo.getOvertimebegintime());
	vo.setEndtimebeforecheck(vo.getOvertimeendtime());
	vo.setOvertimebegintime(finalScope.getScope_start_datetime());
	vo.setOvertimeendtime(finalScope.getScope_end_datetime());
    }

    /**
     * 非门禁工作日提前加班校验
     * 
     * @param isStrict
     * @param vo
     * @param workScope
     * @param checkTimes
     * @param psnEnjoyHrHolidayVOs
     *            V63添加，加班时间包含假日且假日排班记为加班
     * @param kqScope
     */
    private void checkBeforeWorkBills(boolean isStrict, OvertimeRegVO vo, ITimeScope workScope, ShiftVO shiftVO,
	    ICheckTime[] checkTimes, HRHolidayVO[] psnEnjoyHrHolidayVOs) {

	// 基本思想是，先构造两个时间段：延时加班的起点（班次定义中的参数）到上班前（含）的第一张卡是一个时间段（有可能没有），上班前（含）的第一张卡之后的所有卡第一张到最前一张是
	// 一个时间段（宽松），或者最前两条为一个时间段（严格）
	// 这两个时间段与填写的单据取交集，若交集结果大于两条，则取长的一条
	// 规定上班时间
	UFDateTime ruleOnDutyTime = workScope.getScope_start_datetime();
	// 过滤出从规定上班时间（含）之前的刷签卡数据
	ICheckTime[] filteredCheckTimes = CheckTimeUtils.filterOrderedTimesYY(checkTimes, checkTimes[0].getDatetime(),
		ruleOnDutyTime);
	if (ArrayUtils.isEmpty(filteredCheckTimes)) {
	    resetCheckValue(vo);
	    return;
	}
	ITimeScope overtimeRuleScope = shiftVO.getOvertimeRuleScope(ruleOnDutyTime, false);
	List<ITimeScope> scopes = new ArrayList<ITimeScope>(2);// 用来和加班单交集的时间段数组
	ICheckTime firstCheckTimeBeforeOnDuty = filteredCheckTimes[filteredCheckTimes.length - 1];// 上班前的最后一张卡
	if (overtimeRuleScope != null
		&& !firstCheckTimeBeforeOnDuty.getDatetime().after(overtimeRuleScope.getScope_start_datetime())) {
	    if (firstCheckTimeBeforeOnDuty.getDatetime().before(overtimeRuleScope.getScope_end_datetime())) {
		scopes.add(new DefaultTimeScope(firstCheckTimeBeforeOnDuty.getDatetime(), overtimeRuleScope
			.getScope_end_datetime()));
	    }
	}
	// 如果上班班前（含）的刷签卡次数>=3次，则：
	// if 严格，最前面两张凑成一对
	// if 宽松，第二张和最前面一张凑成一对
	if (filteredCheckTimes.length >= 3) {
	    UFDateTime beginTime = filteredCheckTimes[0].getDatetime();// 最前一张刷签卡
	    UFDateTime endTime = isStrict ? filteredCheckTimes[1].getDatetime()
		    : filteredCheckTimes[filteredCheckTimes.length - 2].getDatetime();
	    ITimeScope scope = new DefaultTimeScope(beginTime, endTime);
	    if (overtimeRuleScope == null) {
		scopes.add(scope);
	    } else {// 否则还要与加班规则进行校验，保证开始时间在计为加班的时间之前（含），且结束时间在开始计加班的时间之前
		if (!beginTime.after(overtimeRuleScope.getScope_start_datetime())) {
		    endTime = DateTimeUtils.min(overtimeRuleScope.getScope_end_datetime(), endTime);
		    if (scope.getScope_start_datetime().before(endTime)) {
			scope.setScope_end_datetime(endTime);
			scopes.add(scope);
		    }
		}
	    }
	}
	if (scopes.size() == 0) {
	    resetCheckValue(vo);
	    return;
	}
	ITimeScope[] interScopes = TimeScopeUtils.intersectionTimeScopes(vo, scopes.toArray(new ITimeScope[0]));
	if (ArrayUtils.isEmpty(interScopes)) {
	    resetCheckValue(vo);
	    return;
	}
	ITimeScope finalScope = findLongger(interScopes);// 最终交集的结果；如果交出了两个，则取长的为结果
	if (finalScope.getScope_start_datetime().equals(finalScope.getScope_end_datetime())) {
	    resetCheckValue(vo);
	    return;
	}
	vo.setBegintimebeforecheck(vo.getOvertimebegintime());
	vo.setEndtimebeforecheck(vo.getOvertimeendtime());
	vo.setOvertimebegintime(finalScope.getScope_start_datetime());
	vo.setOvertimeendtime(finalScope.getScope_end_datetime());
    }

    /**
     * 在scopes中找出较长的一个。scopes的个数不超过2
     * 
     * @param scopes
     * @return
     */
    private ITimeScope findLongger(ITimeScope[] scopes) {
	if (ArrayUtils.isEmpty(scopes)) {
	    return null;
	}
	if (scopes.length == 1) {
	    return scopes[0];
	}
	long len1 = TimeScopeUtils.getLength(scopes[0]);
	long len2 = TimeScopeUtils.getLength(scopes[1]);
	return len1 < len2 ? scopes[1] : scopes[0];
    }

    /**
     * 门禁工作日延时加班校验
     * 
     * @param vo
     * @param workScope
     * @param shiftVO
     * @param psnCalendar
     * @param checkTimes
     * @param workScope
     * @param timeDataVO
     */
    private void checkDelayWorkBillsWithFlag(OvertimeRegVO vo, ITimeScope workScope, ShiftVO shiftVO,
	    ICheckTime[] checkTimes) {
	// 大致思路是：如果班次没有定义延时加班规则，则校验失败
	// 找出规定下班（含）之后的最晚out卡，如果无out卡或者out卡早于“超过计加班”参数，则校验失败
	// 否则制造一个时间段：开始时间是开始计加班的时间，结束时间是最晚的out卡，与用户填写的单子取交集
	// 规定下班时间
	UFDateTime ruleOffDutyTime = workScope.getScope_end_datetime();
	// 过滤出从规定下班时间（含）之后最晚的out卡
	ICheckTime lastOut = CheckTimeUtilsWithCheckFlag.findOrderedLatestYY(checkTimes, ruleOffDutyTime,
		checkTimes[checkTimes.length - 1].getDatetime(), ICheckTime.CHECK_FLAG_OUT);
	if (lastOut == null) {
	    resetCheckValue(vo);
	    return;
	}
	ITimeScope overtimeRuleScope = shiftVO.getOvertimeRuleScope(ruleOffDutyTime, true);
	// 如果没有定义延时加班规则，则校验失败。或者最晚的out在规定超过则计加班的时间之前，也校验失败
	if (overtimeRuleScope == null || lastOut.getDatetime().before(overtimeRuleScope.getScope_end_datetime())) {
	    resetCheckValue(vo);
	    return;
	}
	ITimeScope scope = new DefaultTimeScope(overtimeRuleScope.getScope_start_datetime(), lastOut.getDatetime());
	ITimeScope interScope = TimeScopeUtils.intersectionTimeScope(scope, vo);
	if (interScope == null || interScope.getScope_end_datetime().equals(interScope.getScope_start_datetime())) {
	    resetCheckValue(vo);
	    return;
	}
	// 处理单据时间
	vo.setBegintimebeforecheck(vo.getOvertimebegintime());
	vo.setEndtimebeforecheck(vo.getOvertimeendtime());
	vo.setOvertimebegintime(interScope.getScope_start_datetime());
	vo.setOvertimeendtime(interScope.getScope_end_datetime());
    }

    /**
     * 门禁工作日提前加班校验
     * 
     * @param vo
     * @param workScope
     * @param shiftVO
     * @param checkTimes
     * @param workScope
     * @param timeDataVO
     */
    private void checkBeforeWorkBillsWithFlag(OvertimeRegVO vo, ITimeScope workScope, ShiftVO shiftVO,
	    ICheckTime[] checkTimes) {
	// 大致思路是：如果班次没有定义提前加班规则，则校验失败
	// 找出规定上班（含）之前的最早in卡，如果无in卡或者in卡晚于“早于计加班”参数，则校验失败
	// 否则制造一个时间段：开始时间是最早的in卡，结束时间是开始计加班的时间，与用户填写的单子取交集
	// 规定上班时间
	UFDateTime ruleOnDutyTime = workScope.getScope_start_datetime();
	// 过滤出从规定上班时间（含）之前最早的in卡
	ICheckTime earliestIn = CheckTimeUtilsWithCheckFlag.findOrderedEarliestYY(checkTimes,
		checkTimes[0].getDatetime(), ruleOnDutyTime, ICheckTime.CHECK_FLAG_IN);
	if (earliestIn == null) {
	    resetCheckValue(vo);
	    return;
	}
	ITimeScope overtimeRuleScope = shiftVO.getOvertimeRuleScope(ruleOnDutyTime, false);
	// 如果没有定义提前加班规则，则校验失败。或者最早的in在规定早于则计加班的时间之后，也校验失败
	if (overtimeRuleScope == null || earliestIn.getDatetime().after(overtimeRuleScope.getScope_start_datetime())) {
	    resetCheckValue(vo);
	    return;
	}
	ITimeScope scope = new DefaultTimeScope(earliestIn.getDatetime(), overtimeRuleScope.getScope_end_datetime());
	ITimeScope interScope = TimeScopeUtils.intersectionTimeScope(scope, vo);
	if (interScope == null || interScope.getScope_end_datetime().equals(interScope.getScope_start_datetime())) {
	    resetCheckValue(vo);
	    return;
	}
	// 处理单据时间
	vo.setBegintimebeforecheck(vo.getOvertimebegintime());
	vo.setEndtimebeforecheck(vo.getOvertimeendtime());
	vo.setOvertimebegintime(interScope.getScope_start_datetime());
	vo.setOvertimeendtime(interScope.getScope_end_datetime());
    }

    /**
     * 校验不通过时，给登记单据赋值
     * 
     * @param vo
     */
    private void resetCheckValue(OvertimeRegVO vo) {
	vo.setActhour(UFDouble.ZERO_DBL);
	vo.setBegintimebeforecheck(vo.getOvertimebegintime());
	vo.setEndtimebeforecheck(vo.getOvertimeendtime());
	vo.setOvertimebegintime(vo.getOvertimeendtime());
    }

    @Override
    public OvertimeRegVO[] over2Rest(OvertimeRegVO[] vos, String toRestYear, String toRestMonth)
	    throws BusinessException {

	if (ArrayUtils.isEmpty(vos)) {
	    return null;
	}
	// for(OvertimeRegVO vo:vos){
	// PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(),
	// vo.getBegindate(), vo.getEnddate());
	// }
	String pk_org = vos[0].getPk_org();
	TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
	Map<String, TimeZone> timeZoneMap = timeRule.getTimeZoneMap();
	Map<String, ShiftVO> shiftMap = ShiftServiceFacade.queryShiftVOMapByHROrg(pk_org);
	// 取考勤期间内可转调休的最大时长
	// UFDouble maxHour = SysInitQuery.getParaDbl(pk_org,
	// OvertimeConst.OVERTIMETOREST_PARAM);
	UFDouble maxHour = timeRule.getTorestlongest();
	if (maxHour == null) {
	    maxHour = UFDouble.ZERO_DBL;
	}
	DecimalFormat dcmFmt = new DecimalFormat("0.00");
	maxHour = new UFDouble(dcmFmt.format(maxHour));

	Map<String, OverTimeTypeCopyVO> timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class)
		.queryOvertimeCopyTypeMapByOrg(pk_org);
	IPeriodQueryService periodService = NCLocator.getInstance().lookup(IPeriodQueryService.class);
	IDateScope[] scopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
	ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
	IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
	Map<UFLiteralDate, PeriodVO> periodVOMap = periodService.queryPeriodMapByDateScopes(pk_org, scopes);
	InSQLCreator isc = new InSQLCreator();
	String psndocInSQL = isc.getInSQL(vos, OvertimeCommonVO.PK_PSNDOC);
	Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = psndocService.queryDateJobOrgMapByPsndocInSQL(
		psndocInSQL, scopes);
	Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = calendarService.queryCalendarVOByPsnInSQL(
		pk_org, scopes, psndocInSQL);
	Map<OvertimeCommonVO, OvertimeCommonVO[]> overtimeMap = new OvertimeDAO().getOvertimeVOInPeriod(vos,
		timeZoneMap, shiftMap, psnDateOrgMap, psnCalendarMap, periodVOMap);

	// 按人循环处理
	Map<String, OvertimeRegVO[]> psnRegVOMap = CommonUtils.group2ArrayByField(OvertimeCommonVO.PK_PSNDOC, vos);
	List<OvertimeRegVO> returnVOs = new ArrayList<OvertimeRegVO>();
	Map<String, UFDouble> hourMap = new HashMap<String, UFDouble>();
	for (String pk_psndoc : psnRegVOMap.keySet()) {
	    OvertimeRegVO[] regVOs = psnRegVOMap.get(pk_psndoc);
	    Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap) ? null : psnDateOrgMap
		    .get(pk_psndoc);
	    Map<UFLiteralDate, TimeZone> dateTimeZoneMap = CommonMethods.createDateTimeZoneMap(dateOrgMap, timeZoneMap);
	    Map<String, UFDouble> periodMap = new HashMap<String, UFDouble>();
	    for (OvertimeRegVO vo : regVOs) {
		if (vo.getIstorest().booleanValue()) {
		    throw new BusinessException(ResHelper.getString("6017overtime", "06017overtime0033")/*
													 * @
													 * res
													 * "存在已转调休的记录！"
													 */);
		}
		// 加班单归属日 所在考勤期间是否已封存
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap) ? null
			: psnCalendarMap.get(pk_psndoc);
		UFLiteralDate belongDate = BillProcessHelper.findBelongtoDate(vo, calendarMap, shiftMap,
			dateTimeZoneMap);
		PeriodVO period = periodVOMap.get(belongDate);
		if (period == null || period.isSeal()) {
		    throw new BusinessException(ResHelper.getString("6017overtime", "06017overtime0034")/*
													 * @
													 * res
													 * "单据归属日所在考勤期间不存在或已封存！"
													 */);
		}
		UFDouble sumHour = getSumTorstHour(overtimeMap.get(vo));
		String periodKey = period.getTimeyear() + "-" + period.getTimemonth();
		// 转调休时长
		UFDouble torestHour = vo.getToresthour();
		// 取已转调休时长map中保存的时长信息
		UFDouble alreadyHour = periodMap.get(periodKey) == null ? UFDouble.ZERO_DBL : periodMap.get(periodKey);
		alreadyHour = alreadyHour.add(torestHour);
		if (maxHour.compareTo(sumHour.add(alreadyHour)) < 0) {
		    throw new BusinessException(ResHelper.getString("6017overtime", "06017overtime0035"/*
												        * @
												        * res
												        * "转调休时长超过考勤期间内转调休的最大时长： {0}小时！"
												        */,
			    maxHour.toString()));
		}
		periodMap.put(periodKey, alreadyHour);
		// 设置人员转调休时长
		UFDouble psnOrgHour = hourMap.get(vo.getPk_psnorg());
		hourMap.put(vo.getPk_psnorg(), psnOrgHour == null ? torestHour : psnOrgHour.add(torestHour));
		// 修改登记信息
		vo.setTorestyear(toRestYear);
		vo.setTorestmonth(toRestMonth);
		vo.setIstorest(UFBoolean.TRUE);
		// 取加班类别实体, 在前台将考勤单位为天的时长转换为小时，现在要转换回天
		OverTimeTypeCopyVO typeVO = timeitemMap.get(vo.getPk_overtimetype());
		if (TimeItemCopyVO.TIMEITEMUNIT_DAY == typeVO.getTimeitemunit().intValue()) {
		    vo.setActhour(vo.getActhour().div(timeRule.getDaytohour2()));
		}
		// // 设置差异时长
		// vo.setDiffhour(vo.getOvertimehour().sub(vo.getActhour()));
		// 更新加班登记单据
		returnVOs.add(vo);
	    }
	}
	// 将修改加班转调休结余数据和更新操作提到循环外，优化sql数量 2012-10-23
	NCLocator.getInstance().lookup(ILeaveBalanceManageService.class)
		.processBeforeRestOvertime(pk_org, hourMap, toRestYear, toRestMonth, true);
	// 业务日志
	TaBusilogUtil.writeOvertimeReg2RestBusiLog(returnVOs.toArray(new OvertimeRegVO[0]));
	return getServiceTemplate().update(true, returnVOs.toArray(new OvertimeRegVO[0]));
    }

    @Override
    public OvertimeRegVO[] unOver2Rest(OvertimeRegVO[] vos) throws BusinessException {
	if (ArrayUtils.isEmpty(vos)) {
	    return null;
	}
	// for(OvertimeRegVO vo:vos){
	// PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(),
	// vo.getBegindate(), vo.getEnddate());
	// }
	String pk_org = vos[0].getPk_org();
	TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
	Map<String, TimeZone> timeZoneMap = timeRule.getTimeZoneMap();
	Map<String, ShiftVO> shiftMap = ShiftServiceFacade.queryShiftVOMapByHROrg(pk_org);
	Map<String, OverTimeTypeCopyVO> timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class)
		.queryOvertimeCopyTypeMapByOrg(pk_org);
	IPeriodQueryService periodService = NCLocator.getInstance().lookup(IPeriodQueryService.class);
	IDateScope[] scopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
	ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
	IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
	Map<UFLiteralDate, PeriodVO> periodVOMap = periodService.queryPeriodMapByDateScopes(pk_org, scopes);
	InSQLCreator isc = new InSQLCreator();
	String psndocInSQL = isc.getInSQL(vos, OvertimeCommonVO.PK_PSNDOC);
	Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = psndocService.queryDateJobOrgMapByPsndocInSQL(
		psndocInSQL, scopes);
	Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = calendarService.queryCalendarVOByPsnInSQL(
		pk_org, scopes, psndocInSQL);

	// 按人循环处理
	Map<String, OvertimeRegVO[]> psnRegVOMap = CommonUtils.group2ArrayByField(OvertimeCommonVO.PK_PSNDOC, vos);
	List<OvertimeRegVO> returnVOs = new ArrayList<OvertimeRegVO>();
	// 构造<year+month, <pk_psnorg, toRestHour>>
	Map<String, Map<String, UFDouble>> restMap = new HashMap<String, Map<String, UFDouble>>();
	for (String pk_psndoc : psnRegVOMap.keySet()) {
	    OvertimeRegVO[] regVOs = psnRegVOMap.get(pk_psndoc);
	    Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap) ? null : psnDateOrgMap
		    .get(pk_psndoc);
	    Map<UFLiteralDate, TimeZone> dateTimeZoneMap = CommonMethods.createDateTimeZoneMap(dateOrgMap, timeZoneMap);
	    for (OvertimeRegVO vo : regVOs) {
		if (!vo.getIstorest().booleanValue()) {
		    throw new BusinessException(ResHelper.getString("6017overtime", "06017overtime0036")/*
													 * @
													 * res
													 * "存在未转调休的记录！"
													 */);
		}
		// 加班单归属日 所在考勤期间是否已封存
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap) ? null
			: psnCalendarMap.get(pk_psndoc);
		UFLiteralDate belongDate = BillProcessHelper.findBelongtoDate(vo, calendarMap, shiftMap,
			dateTimeZoneMap);
		PeriodVO period = periodVOMap.get(belongDate);
		if (period == null || period.isSeal()) {
		    throw new BusinessException(ResHelper.getString("6017overtime", "06017overtime0034")/*
													 * @
													 * res
													 * "单据归属日所在考勤期间不存在或已封存！"
													 */);
		}
		// 设置人员转调休时长
		String key = vo.getTorestyear() + (vo.getTorestmonth() == null ? "" : ("-" + vo.getTorestmonth()));
		Map<String, UFDouble> hourMap = restMap.get(key) != null ? restMap.get(key)
			: new HashMap<String, UFDouble>();
		UFDouble psnOrgHour = hourMap.get(vo.getPk_psnorg());
		hourMap.put(vo.getPk_psnorg(),
			psnOrgHour == null ? vo.getToresthour() : psnOrgHour.add(vo.getToresthour()));
		restMap.put(key, hourMap);
		// 将休假数据修改后再将转调休时长归0
		vo.setIstorest(UFBoolean.FALSE);
		vo.setTorestyear(null);
		vo.setTorestmonth(null);
		vo.setToresthour(UFDouble.ZERO_DBL);
		// 取加班类别实体, 在前台将考勤单位为天的时长转换为小时，现在要转换回天
		OverTimeTypeCopyVO typeVO = timeitemMap.get(vo.getPk_overtimetype());
		if (TimeItemCopyVO.TIMEITEMUNIT_DAY == typeVO.getTimeitemunit().intValue()) {
		    vo.setActhour(vo.getActhour().div(timeRule.getDaytohour2()));
		}
		// // 设置差异时长
		// vo.setDiffhour(vo.getOvertimehour().sub(vo.getActhour()));
		// 更新加班登记单据
		returnVOs.add(vo);
	    }
	}
	// 将修改加班转调休结余数据和更新操作提到循环外(可能转入年度期间不一致，还需要按期间循环一次)，优化sql数量 2012-10-23
	for (String key : restMap.keySet()) {
	    String[] yearAndMonths = key.split("-");
	    String year = yearAndMonths[0];
	    String month = ArrayUtils.getLength(yearAndMonths) > 1 ? yearAndMonths[1] : null;
	    NCLocator.getInstance().lookup(ILeaveBalanceManageService.class)
		    .processBeforeRestOvertime(pk_org, restMap.get(key), year, month, false);
	}
	// 业务日志
	TaBusilogUtil.writeOvertimeRegUn2RestBusiLog(returnVOs.toArray(new OvertimeRegVO[0]));
	return getServiceTemplate().update(true, returnVOs.toArray(new OvertimeRegVO[0]));
    }

    /**
     * 取归属日所在考勤期间已转调休单据的总时长
     * 
     * @param vo
     * @param shiftMap
     * @param timeZone
     * @return
     * @throws BusinessException
     */
    private UFDouble getSumTorstHour(OvertimeCommonVO[] belongVOs) throws BusinessException {
	if (ArrayUtils.isEmpty(belongVOs)) {
	    return UFDouble.ZERO_DBL;
	}
	UFDouble sumHour = UFDouble.ZERO_DBL;
	for (OvertimeCommonVO belongVO : belongVOs) {
	    if (!(belongVO instanceof OvertimeRegVO)) {
		continue;
	    }
	    OvertimeRegVO regVO = (OvertimeRegVO) belongVO;
	    if (!regVO.getIstorest().booleanValue()) {
		continue;
	    }
	    sumHour = sumHour.add(regVO.getToresthour());
	}
	return sumHour;
    }

    // private UFDouble getSumTorstHour(OvertimeRegVO vo, Map<String, ShiftVO>
    // shiftMap,
    // Map<UFLiteralDate, TimeZone> dateTimeZoneMap,
    // Map<UFLiteralDate, AggPsnCalendar> calendarMap,
    // Map<UFLiteralDate, PeriodVO> periodMap) throws BusinessException {
    // OvertimeCommonVO[] allVOs = new OvertimeDAO().getOvertimeVOInPeriod(vo,
    // shiftMap, dateTimeZoneMap,calendarMap,periodMap);
    // if(ArrayUtils.isEmpty(allVOs))
    // return UFDouble.ZERO_DBL;
    // UFDouble sumHour = UFDouble.ZERO_DBL;
    // for(OvertimeCommonVO allVO:allVOs){
    // if(!(allVO instanceof OvertimeRegVO))
    // continue;
    // OvertimeRegVO regVO = (OvertimeRegVO) allVO;
    // if(!regVO.getIstorest().booleanValue())
    // continue;
    // sumHour = sumHour.add(regVO.getToresthour());
    // }
    // return sumHour;
    // }

    @Override
    public OvertimeRegVO[] undoCheck(OvertimeRegVO[] vos) throws BusinessException {
	if (ArrayUtils.isEmpty(vos)) {
	    return null;
	}
	String pk_org = vos[0].getPk_org();
	// Map<String, TimeZone>
	// timeZoneMap=NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZoneMap(pk_org);
	List<OvertimeRegVO> result = new ArrayList<OvertimeRegVO>();
	for (OvertimeRegVO vo : vos) {
	    if (!vo.getIscheck().booleanValue()) {
		continue;
	    }
	    if (vo.getIstorest().booleanValue()) {
		throw new BusinessException(ResHelper.getString("6017overtime", "06017overtime0037")
		/* @res "存在已转调休单据，不允许反校验！" */);
	    }
	    vo.setOvertimebegintime(vo.getBegintimebeforecheck());
	    vo.setOvertimeendtime(vo.getEndtimebeforecheck());
	    // BillMethods.processBeginEndDate(vo, timeZoneMap);
	    vo.setIscheck(UFBoolean.FALSE);
	    result.add(vo);
	}
	if (CollectionUtils.isEmpty(result))
	    return null;
	vos = result.toArray(new OvertimeRegVO[0]);
	BillMethods.processBeginEndDatePkJobOrgTimeZone(vos);
	BillProcessHelperAtServer.calOvertimeLength(pk_org, vos);
	// 业务日志
	TaBusilogUtil.writeOvertimeRegUnCheckBusiLog(vos);
	return updateArrayData(vos);
    }

    @Override
    public void deleteArrayData(OvertimeRegVO[] vos) throws BusinessException {
	if (ArrayUtils.isEmpty(vos))
	    return;
	// for(OvertimeRegVO vo:vos){
	// PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(),
	// vo.getBegindate(), vo.getEnddate());
	// }
	IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(vos);
	PeriodServiceFacade.checkMonth(vos[0].getPk_org(),
		StringPiecer.getStrArrayDistinct(vos, OvertimeRegVO.PK_PSNDOC), maxDateScope.getBegindate(),
		maxDateScope.getEnddate());

	List<OvertimeRegVO> realDelList = new ArrayList<OvertimeRegVO>();
	for (OvertimeRegVO vo : vos) {
	    if (vo == null || ICommonConst.BILL_SOURCE_REG != vo.getBillsource().intValue()
		    || vo.getIstorest().booleanValue())
		continue;
	    realDelList.add(vo);
	}
	if (CollectionUtils.isEmpty(realDelList))
	    return;
	vos = realDelList.toArray(new OvertimeRegVO[0]);
	DefaultValidationService vService = new DefaultValidationService();
	vService.addValidator(new OvertimeRegDeleteValidator());
	vService.validate(vos);
	// 业务日志
	TaBusilogUtil.writeOvertimeRegDeleteBusiLog(vos);
	// ssx added for Taiwan new Law
	this.getOvSegService().deleteOvertimeSegDetail(vos);
	// end
	getServiceTemplate().delete(vos);
    }

    @Override
    public void deleteData(OvertimeRegVO vo) throws BusinessException {
	if (vo == null)
	    return;
	deleteArrayData(new OvertimeRegVO[] { vo });
    }

    @Override
    public OvertimeRegVO[] insertArrayData(OvertimeRegVO[] vos) throws BusinessException {
	return insertData(vos, true);
    }

    @Override
    public OvertimeRegVO[] insertData(OvertimeRegVO[] vos, boolean needCalAndValidate) throws BusinessException {
	if (ArrayUtils.isEmpty(vos)) {
	    return null;
	}
	BillMethods.processBeginEndDatePkJobOrgTimeZone(vos);
	if (needCalAndValidate) {
	    DefaultValidationService vService = new DefaultValidationService();
	    vService.addValidator(new SaveOvertimeRegValidator());
	    vService.addValidator(new OvertimeRegValidator());
	    vService.validate(vos);
	    String pk_org = vos[0].getPk_org();
	    // check(pk_org, vos);//此处的校验 在保存前提示性校验中已经校验过一次了，此处不再校验（影响效率）

	    // 是否跨业务单元单独校验单独校验
	    BillValidatorAtServer.checkCrossBU(pk_org, vos);

	} else {// 审批可能已经不再考勤档案范围内了，需要校验
	    DefaultValidationService vService = new DefaultValidationService();
	    vService.addValidator(new OvertimeRegValidator());
	    vService.validate(vos);
	}
	// add by fangbing 加班登记保存时，不需要修改“是否需要校验标识”，再此将其暂存下，待校验过后再还原。20141226
	Map<String, UFBoolean> checkMap = new HashMap<String, UFBoolean>();
	for (OvertimeRegVO vo : vos) {
	    checkMap.put(vo.getPk_psndoc() + vo.getOvertimebegintime(), vo.getIsneedcheck());
	}
	new OvertimeDAO().setAlreadyHourAndCheckFlag(false, vos);
	for (OvertimeRegVO vo : vos) {
	    // 设置差异时长
	    vo.setDiffhour(vo.getOvertimehour().sub(vo.getActhour()));
	    // add by fangbing 将之前暂存的标识按人+开始时间还原
	    vo.setIsneedcheck(checkMap.get(vo.getPk_psndoc() + vo.getOvertimebegintime()));
	  //MOD 设置审批时间为当前时间 by James
		vo.setApprove_time(new UFDateTime());
	}
	OvertimeRegVO[] retvos = getServiceTemplate().insert(vos);
	// 业务日志
	TaBusilogUtil.writeOvertimeRegAddBusiLog(retvos);
	// ssx added for Taiwan new law
	this.getOvSegService().regOvertimeSegDetail(retvos);
	// end
	return retvos;
    }

    // @Deprecated
    // public OvertimeRegVO[] insertData1(OvertimeRegVO[] vos,boolean
    // needCalAndValidate) throws BusinessException {
    // if(ArrayUtils.isEmpty(vos))
    // return null;
    // nc.impl.ta.timebill.CommonMethods.processBeginEndDatePkJobOrgTimeZone(vos);
    // if(needCalAndValidate){
    // DefaultValidationService vService = new DefaultValidationService();
    // vService.addValidator(new SaveOvertimeRegValidator());
    // vService.addValidator(new OvertimeRegValidator());
    // vService.validate(vos);
    // String pk_org = vos[0].getPk_org();
    // check(pk_org, vos);
    //
    // //是否跨业务单元单独校验单独校验
    // BillValidatorAtServer.checkCrossBU(pk_org, vos);
    //
    // }else{//审批可能已经不再考勤档案范围内了，需要校验
    // DefaultValidationService vService = new DefaultValidationService();
    // vService.addValidator(new OvertimeRegValidator());
    // vService.validate(vos);
    // }
    // // List<OvertimeRegVO> volist = new ArrayList<OvertimeRegVO>();
    // OvertimeApplyQueryMaintainImpl impl = new
    // OvertimeApplyQueryMaintainImpl();
    // OvertimeDAO dao = new OvertimeDAO();
    // String pk_org = vos[0].getPk_org();
    // Map<String, AggShiftVO> aggShiftMap =
    // ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
    // Map<String, ShiftVO> shiftMap =
    // CommonUtils.transferAggMap2HeadMap(aggShiftMap);
    // TimeRuleVO timeruleVO =
    // NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
    // Map<String, OverTimeTypeCopyVO> timeitemMap =
    // NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryOvertimeCopyTypeMapByOrg(pk_org);
    // IDateScope[] mergedScopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
    // IPeriodQueryService periodService =
    // NCLocator.getInstance().lookup(IPeriodQueryService.class);
    // Map<UFLiteralDate, PeriodVO> periodMap =
    // periodService.queryPeriodMapByDateScopes(pk_org, mergedScopes);
    // InSQLCreator isc = null;
    // try{
    // isc = new InSQLCreator();
    // String psndocInSQL = isc.getInSQL(vos, OvertimebVO.PK_PSNDOC);
    // ITBMPsndocQueryService psndocService =
    // NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
    // Map<String, Map<UFLiteralDate, String>> psnDateOrgMap =
    // psndocService.queryDateJobOrgMapByPsndocInSQL(psndocInSQL, mergedScopes);
    // IPsnCalendarQueryService calendarService =
    // NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
    // Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnAggCalendarMap =
    // calendarService.queryCalendarVOByPsnInSQL(pk_org, mergedScopes,
    // psndocInSQL);
    //
    // for(OvertimeRegVO vo:vos){
    // //如果选择了是否允许校验但不符合允许校验的规则，要强制转换为不允许校验
    // if(vo.getIsneedcheck().booleanValue()&&!impl.isCanCheck(vo))
    // vo.setIsneedcheck(UFBoolean.FALSE);
    // // 设置差异时长
    // vo.setDiffhour(vo.getOvertimehour().sub(vo.getActhour()));
    // String pk_psndoc = vo.getPk_psndoc();
    // Map<UFLiteralDate, String> dateOrgMap =
    // MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(pk_psndoc);
    // Map<UFLiteralDate, AggPsnCalendar> calendarMap =
    // MapUtils.isEmpty(psnAggCalendarMap)?null:psnAggCalendarMap.get(pk_psndoc);
    // Map<UFLiteralDate, TimeZone> dateTimeZoneMap =
    // CommonMethods.createDateTimeZoneMap(dateOrgMap,timeruleVO.getTimeZoneMap());
    // dao.setAlreadyHour(vo, shiftMap, dateTimeZoneMap, calendarMap,
    // timeitemMap, periodMap, timeruleVO);
    // //优化批量保存
    // // volist.add(getServiceTemplate().insert(vo));
    // }
    // getServiceTemplate().batchInsertDirect(vos);
    // // return volist.toArray(new OvertimeRegVO[0]);
    // return vos;
    // }
    // finally{
    // if(isc!=null)
    // isc.clear();
    // }
    // }

    @Override
    public OvertimeRegVO insertData(OvertimeRegVO vo) throws BusinessException {
	return insertData(new OvertimeRegVO[] { vo }, true)[0];
    }

    @Override
    public OvertimeRegVO[] updateArrayData(OvertimeRegVO[] vos) throws BusinessException {
	if (ArrayUtils.isEmpty(vos)) {
	    return null;
	}
	// for(OvertimeRegVO vo:vos){
	// PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(),
	// vo.getBegindate(), vo.getEnddate());
	// }
	IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(vos);
	PeriodServiceFacade.checkMonth(vos[0].getPk_org(),
		StringPiecer.getStrArrayDistinct(vos, OvertimeRegVO.PK_PSNDOC), maxDateScope.getBegindate(),
		maxDateScope.getEnddate());

	OvertimeRegVO[] oldvos = getServiceTemplate().queryByPks(OvertimeRegVO.class,
		StringPiecer.getStrArray(vos, OvertimeRegVO.PK_OVERTIMEREG));
	BillMethods.processBeginEndDatePkJobOrgTimeZone(vos);
	// 校验服务
	DefaultValidationService vService = new DefaultValidationService();
	vService.addValidator(new SaveOvertimeRegValidator());
	vService.addValidator(new OvertimeRegValidator());
	vService.validate(vos);

	// 按组织分组
	Map<String, OvertimeRegVO[]> vosMap = CommonUtils.group2ArrayByField(OvertimeCommonVO.PK_ORG, vos);
	for (String pk_org : vosMap.keySet()) {
	    OvertimeRegVO[] pk_vos = vosMap.get(pk_org);
	    // 单据冲突校验
	    check(pk_org, pk_vos);
	    List<OvertimeRegVO> regList = new ArrayList<OvertimeRegVO>();
	    for (OvertimeRegVO vo : pk_vos) {
		// 校验后有可能开始时间和结束时间一样，此时不做跨组织校验
		if (!vo.getOvertimebegintime().equals(vo.getOvertimeendtime())) {
		    regList.add(vo);
		}
		// 设置差异时长
		vo.setDiffhour(vo.getOvertimehour().sub(vo.getActhour()));
	    }
	    // 是否跨业务单元校验
	    if (!regList.isEmpty()) {
		BillValidatorAtServer.checkCrossBU(pk_org, regList.toArray(new OvertimeRegVO[0]));
	    }
	}

	OvertimeRegVO[] retvos = getServiceTemplate().update(true, vos);
	// 业务日志
	TaBusilogUtil.writeOvertimeRegEditBusiLog(retvos, oldvos);
	// ssx added for Taiwan new law
	this.getOvSegService().updateOvertimeSegDetail(retvos);
	// end
	return retvos;
    }

    @Override
    public OvertimeRegVO updateData(OvertimeRegVO vo) throws BusinessException {
	PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(), vo.getBegindate(), vo.getEnddate());
	OvertimeRegVO oldvo = getServiceTemplate().queryByPk(OvertimeRegVO.class, vo.getPk_overtimereg());
	BillMethods.processBeginEndDatePkJobOrgTimeZone(new OvertimeRegVO[] { vo });
	// 校验服务
	DefaultValidationService vService = new DefaultValidationService();
	vService.addValidator(new SaveOvertimeRegValidator());
	vService.addValidator(new OvertimeRegValidator());
	vService.validate(vo);
	// 单据冲突校验
	check(vo);

	// 是否跨业务单元单独校验
	OvertimeRegVO[] bills = new OvertimeRegVO[] { vo };
	String pk_org = vo.getPk_org();
	// 校验后有可能开始时间和结束时间一样，此时不做跨组织校验
	if (!vo.getOvertimebegintime().equals(vo.getOvertimeendtime())) {
	    BillValidatorAtServer.checkCrossBU(pk_org, bills);
	}
	// 设置差异时长
	vo.setDiffhour(vo.getOvertimehour().sub(vo.getActhour()));
	// 业务日志
	TaBusilogUtil.writeOvertimeRegEditBusiLog(new OvertimeRegVO[] { vo }, new OvertimeRegVO[] { oldvo });
	// ssx added for Taiwan new law
	this.getOvSegService().updateOvertimeSegDetail(new OvertimeRegVO[] { vo });
	// end
	return getServiceTemplate().update(true, vo)[0];

    }

    @SuppressWarnings("unchecked")
    @Override
    public OvertimeRegVO[] queryByCond(LoginContext context, FromWhereSQL fromWhereSQL, Object etraConds)
	    throws BusinessException {
	// 增加权限sql，使用工作记录资源实体下的“时间管理通用引用”元数据操作
	fromWhereSQL = PubPermissionUtils.addPubQueryPermission2FromWhereSQL(fromWhereSQL,
		OvertimeRegVO.getDefaultTableName(), OvertimeRegVO.getDefaultTableName(), OvertimeCommonVO.PK_PSNJOB);
	// 取主表名
	String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, OvertimeRegVO.getDefaultTableName());
	// 拼组织的sql
	String etraCondsNext = alias + "." + OvertimeCommonVO.PK_ORG
		+ (context.getPk_org() == null ? " is null" : " = '" + context.getPk_org() + "' ");
	// 按期间查询
	String periodSql = TaNormalQueryUtils.getPeriodSql(context, OvertimeRegVO.class, alias,
		(TaBillRegQueryParams) etraConds);
	if (periodSql != null) {
	    etraCondsNext += " and " + periodSql;
	}
	String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, OvertimeRegVO.getDefaultTableName(),
		etraCondsNext, null);
	Collection<OvertimeRegVO> aggvos = (Collection<OvertimeRegVO>) new BaseDAO().executeQuery(sql,
		new BeanListProcessor(OvertimeRegVO.class));
	if (CollectionUtils.isEmpty(aggvos)) {
	    return null;
	}
	return aggvos.toArray(new OvertimeRegVO[0]);
    }

    @Override
    public Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> check(OvertimeRegVO vo) throws BusinessException {
	NCLocator
		.getInstance()
		.lookup(ITBMPsndocQueryService.class)
		.checkTBMPsndocDate(vo.getPk_org(), new String[] { vo.getPk_psndoc() }, new OvertimeRegVO[] { vo },
			true);
	BillMethods.processBeginEndDatePkJobOrgTimeZone(new OvertimeRegVO[] { vo });
	return BillValidatorAtServer.checkOvertime(vo);
    }

    @Override
    public Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> check(String pk_org, OvertimeRegVO[] vos)
	    throws BusinessException {
	BillMethods.processBeginEndDatePkJobOrgTimeZone(vos);
	return BillValidatorAtServer.checkOvertime(pk_org, vos);
    }

    @Override
    public OvertimeRegVO[] doBeforeCheck(OvertimeRegVO[] vos) throws BusinessException {
	if (ArrayUtils.isEmpty(vos)) {
	    return null;
	}
	String pk_org = vos[0].getPk_org();
	IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(vos);
	PeriodVO[] periods = NCLocator.getInstance().lookup(IPeriodQueryService.class)
		.queryPeriodsByDateScope(pk_org, maxDateScope.getBegindate(), maxDateScope.getEnddate());
	List<OvertimeRegVO> returnList = new ArrayList<OvertimeRegVO>();
	for (OvertimeRegVO vo : vos) {
	    // 已转调休的需要提示
	    if (vo.getIstorest().booleanValue()) {
		returnList.add(vo);
		continue;
	    }
	    // 如果期间已封存或不归属任何期间，需要提示
	    if (BillMethods.isDateScopeInPeriod(periods, vo))
		continue;
	    returnList.add(vo);
	}
	return CollectionUtils.isEmpty(returnList) ? null : returnList.toArray(new OvertimeRegVO[0]);
    }

    @Override
    public GeneralVO over2RestFirst(OvertimeRegVO[] vos, String toRestYear, String toRestMonth)
	    throws BusinessException {
	if (ArrayUtils.isEmpty(vos)) {
	    return null;
	}
	// for(OvertimeRegVO vo:vos){循环校验效率太慢
	// PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(),
	// vo.getBegindate(), vo.getEnddate());
	// }
	String pk_org = vos[0].getPk_org();
	TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
	// 若是严格控制则不需要提示信息
	if (timeRule.getIstorestctrlot() != null && timeRule.getIstorestctrlot().booleanValue()) {
	    OvertimeRegVO[] retvos = over2Rest(vos, toRestYear, toRestMonth);
	    GeneralVO retgvo = new GeneralVO();
	    retgvo.setAttributeValue("overtimeRegVO", retvos);
	    return retgvo;
	}
	// 超出最大时长的人员记录
	List<String> overPsn = new ArrayList<String>();

	Map<String, TimeZone> timeZoneMap = timeRule.getTimeZoneMap();
	Map<String, ShiftVO> shiftMap = ShiftServiceFacade.queryShiftVOMapByHROrg(pk_org);
	// 取考勤期间内可转调休的最大时长
	// UFDouble maxHour = SysInitQuery.getParaDbl(pk_org,
	// OvertimeConst.OVERTIMETOREST_PARAM);
	UFDouble maxHour = timeRule.getTorestlongest();
	if (maxHour == null) {
	    maxHour = UFDouble.ZERO_DBL;
	}
	Map<String, OverTimeTypeCopyVO> timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class)
		.queryOvertimeCopyTypeMapByOrg(pk_org);
	IPeriodQueryService periodService = NCLocator.getInstance().lookup(IPeriodQueryService.class);
	IDateScope[] scopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
	ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
	IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
	Map<UFLiteralDate, PeriodVO> periodVOMap = periodService.queryPeriodMapByDateScopes(pk_org, scopes);
	InSQLCreator isc = new InSQLCreator();
	String psndocInSQL = isc.getInSQL(vos, OvertimeCommonVO.PK_PSNDOC);
	Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = psndocService.queryDateJobOrgMapByPsndocInSQL(
		psndocInSQL, scopes);
	Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = calendarService.queryCalendarVOByPsnInSQL(
		pk_org, scopes, psndocInSQL);
	Map<OvertimeCommonVO, OvertimeCommonVO[]> overtimeMap = new OvertimeDAO().getOvertimeVOInPeriod(vos,
		timeZoneMap, shiftMap, psnDateOrgMap, psnCalendarMap, periodVOMap);
	// 按人循环处理
	Map<String, OvertimeRegVO[]> psnRegVOMap = CommonUtils.group2ArrayByField(OvertimeCommonVO.PK_PSNDOC, vos);
	List<OvertimeRegVO> returnVOs = new ArrayList<OvertimeRegVO>();
	Map<String, UFDouble> hourMap = new HashMap<String, UFDouble>();
	for (String pk_psndoc : psnRegVOMap.keySet()) {
	    OvertimeRegVO[] regVOs = psnRegVOMap.get(pk_psndoc);
	    Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap) ? null : psnDateOrgMap
		    .get(pk_psndoc);
	    Map<UFLiteralDate, TimeZone> dateTimeZoneMap = CommonMethods.createDateTimeZoneMap(dateOrgMap, timeZoneMap);
	    Map<String, UFDouble> periodMap = new HashMap<String, UFDouble>();
	    for (OvertimeRegVO vo : regVOs) {
		if (vo.getIstorest().booleanValue()) {
		    throw new BusinessException(ResHelper.getString("6017overtime", "06017overtime0033")/*
													 * @
													 * res
													 * "存在已转调休的记录！"
													 */);
		}
		// 加班单归属日 所在考勤期间是否已封存
		Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap) ? null
			: psnCalendarMap.get(pk_psndoc);
		UFLiteralDate belongDate = BillProcessHelper.findBelongtoDate(vo, calendarMap, shiftMap,
			dateTimeZoneMap);
		PeriodVO period = periodVOMap.get(belongDate);
		if (period == null || period.isSeal()) {
		    throw new BusinessException(ResHelper.getString("6017overtime", "06017overtime0034")/*
													 * @
													 * res
													 * "单据归属日所在考勤期间不存在或已封存！"
													 */);
		}
		UFDouble sumHour = getSumTorstHour(overtimeMap.get(vo));
		String periodKey = period.getTimeyear() + "-" + period.getTimemonth();
		// 转调休时长
		UFDouble torestHour = vo.getToresthour();
		// 取已转调休时长map中保存的时长信息
		UFDouble alreadyHour = periodMap.get(periodKey) == null ? UFDouble.ZERO_DBL : periodMap.get(periodKey);
		alreadyHour = alreadyHour.add(torestHour);
		if (maxHour.doubleValue() >= 0 && maxHour.compareTo(sumHour.add(alreadyHour)) < 0) {
		    overPsn.add(vo.getPk_psndoc());
		    // throw new
		    // BusinessException(ResHelper.getString("6017overtime","06017overtime0035"/*@res
		    // "转调休时长超过考勤期间内转调休的最大时长： {0}小时！"*/, maxHour.toString()));
		}
		periodMap.put(periodKey, alreadyHour);
		// 设置人员转调休时长
		UFDouble psnOrgHour = hourMap.get(vo.getPk_psnorg());
		hourMap.put(vo.getPk_psnorg(), psnOrgHour == null ? torestHour : psnOrgHour.add(torestHour));
		// 修改登记信息
		vo.setTorestyear(toRestYear);
		vo.setTorestmonth(toRestMonth);
		vo.setIstorest(UFBoolean.TRUE);
		// 取加班类别实体, 在前台将考勤单位为天的时长转换为小时，现在要转换回天
		OverTimeTypeCopyVO typeVO = timeitemMap.get(vo.getPk_overtimetype());
		if (TimeItemCopyVO.TIMEITEMUNIT_DAY == typeVO.getTimeitemunit().intValue()) {
		    vo.setActhour(vo.getActhour().div(timeRule.getDaytohour2()));
		}
		// // 设置差异时长
		// vo.setDiffhour(vo.getOvertimehour().sub(vo.getActhour()));
		// 更新加班登记单据
		returnVOs.add(vo);
	    }
	}

	OvertimeRegVO[] retvos = returnVOs.toArray(new OvertimeRegVO[0]);
	GeneralVO retgvo = new GeneralVO();
	// 不需要提示消息直接更新数据库
	if (overPsn.isEmpty()) {
	    // 将修改加班转调休结余数据和更新操作提到循环外，优化sql数量 2012-10-23
	    NCLocator.getInstance().lookup(ILeaveBalanceManageService.class)
		    .processBeforeRestOvertime(pk_org, hourMap, toRestYear, toRestMonth, true);
	    retvos = getServiceTemplate().update(true, retvos);
	    retgvo.setAttributeValue("overtimeRegVO", retvos);
	    // 业务日志
	    TaBusilogUtil.writeOvertimeReg2RestBusiLog(retvos);
	    return retgvo;
	} else {// 提示加班转调休超时
	    String[] pk_psndocs = overPsn.toArray(new String[0]);
	    String psnNames = CommonUtils.getPsnNames(pk_psndocs);
	    String warningMsg = ResHelper.getString("6017overtime", "06017overtime0048"
	    /* @res "下列人员{0}的转调休时长超过考勤期间内规定的最大时长"; */, psnNames);
	    retgvo.setAttributeValue("warningMsg", warningMsg);
	    retgvo.setAttributeValue("leaveHour", hourMap);
	    retgvo.setAttributeValue("overtimeRegVO", retvos);
	    return retgvo;
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    public OvertimeRegVO[] over2RestSecond(GeneralVO gvo, String toRestYear, String toRestMonth)
	    throws BusinessException {
	if (gvo.getAttributeValue("overtimeRegVO") == null) {
	    return null;
	}
	OvertimeRegVO[] vos = (OvertimeRegVO[]) gvo.getAttributeValue("overtimeRegVO");
	if (ArrayUtils.isEmpty(vos)) {
	    return null;
	}
	String pk_org = vos[0].getPk_org();
	Map<String, UFDouble> hourMap = (Map<String, UFDouble>) gvo.getAttributeValue("leaveHour");
	NCLocator.getInstance().lookup(ILeaveBalanceManageService.class)
		.processBeforeRestOvertime(pk_org, hourMap, toRestYear, toRestMonth, true);
	// 业务日志
	TaBusilogUtil.writeOvertimeReg2RestBusiLog(vos);
	return getServiceTemplate().update(true, vos);
    }

    @Override
    public String[] queryPKsByFromWhereSQL(LoginContext context, FromWhereSQL fromWhereSQL, Object etraConds)
	    throws BusinessException {
	String cond = getSQLCondByFromWhereSQL(context, fromWhereSQL, etraConds);
	String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, OvertimeRegVO.getDefaultTableName());
	List<String> result = excuteQueryPksBycond(cond, alias);
	return CollectionUtils.isEmpty(result) ? null : (String[]) result.toArray(new String[0]);
    }

    public String getSQLCondByFromWhereSQL(LoginContext context, FromWhereSQL fromWhereSQL, Object etraConds)
	    throws BusinessException {
	// 增加权限sql，使用工作记录资源实体下的“时间管理通用引用”元数据操作
	fromWhereSQL = PubPermissionUtils.addPubQueryPermission2FromWhereSQL(fromWhereSQL,
		OvertimeRegVO.getDefaultTableName(), OvertimeRegVO.getDefaultTableName(), OvertimeCommonVO.PK_PSNJOB);
	// 取主表名
	String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, OvertimeRegVO.getDefaultTableName());
	// 拼组织的sql
	String etraCondsNext = alias + "." + OvertimeCommonVO.PK_ORG
		+ (context.getPk_org() == null ? " is null" : " = '" + context.getPk_org() + "' ");
	// 按期间查询
	String periodSql = TaNormalQueryUtils.getPeriodSql(context, OvertimeRegVO.class, alias,
		(TaBillRegQueryParams) etraConds);
	if (periodSql != null) {
	    etraCondsNext += " and " + periodSql;
	}
	String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, OvertimeRegVO.getDefaultTableName(),
		new String[] { OvertimeRegVO.PK_OVERTIMEREG }, null, null, null, null);
	etraCondsNext += " and " + OvertimeRegVO.PK_OVERTIMEREG + " in ( " + sql + " ) ";
	return etraCondsNext;
    }

    private List<String> excuteQueryPksBycond(String cond, String alias) throws BusinessException {
	String sql = "select " + (StringUtils.isEmpty(alias) ? "" : alias + ".") + OvertimeRegVO.PK_OVERTIMEREG
		+ " from " + OvertimeRegVO.getDefaultTableName() + " " + (StringUtils.isEmpty(alias) ? "" : alias);
	if (!StringUtils.isEmpty(cond))
	    sql = sql + " where " + cond;
	List<String> result = (List) new BaseDAO().executeQuery(sql, new ColumnListProcessor());
	return result;
    }

    @Override
    public OvertimeRegVO[] queryByPks(String[] pks) throws BusinessException {
	if (ArrayUtils.isEmpty(pks))
	    return null;
	return getServiceTemplate().queryByPks(OvertimeRegVO.class, pks);
    }
}