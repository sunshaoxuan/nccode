package nc.impl.ta.overtime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.zip.GZIPOutputStream;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.common.RuntimeEnv;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.hihk.hrta.vo.importovertime.ExportVO;
import nc.hihk.hrta.vo.importovertime.ImportVO;
import nc.hr.frame.persistence.HrBatchService;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.FromWhereSQLUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.am.common.InSqlManager;
import nc.impl.ta.algorithm.BillProcessHelperAtServer;
import nc.impl.ta.algorithm.BillValidatorAtServer;
import nc.impl.ta.overtime.validator.OvertimeRegDeleteValidator;
import nc.impl.ta.overtime.validator.OvertimeRegValidator;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.ta.CheckTimeServiceFacade;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.IOvertimeRegisterInfoDisplayer;
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
import nc.itf.ta.algorithm.impl.DefaultDateScope;
import nc.itf.ta.algorithm.impl.DefaultTimeScope;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.bill.IDateScopeBillBodyVO;
import nc.vo.ta.bill.ITimeScopeBillBodyVO;
import nc.vo.ta.dataprocess.TimeDataVO;
import nc.vo.ta.holiday.HRHolidayVO;
import nc.vo.ta.log.TaBusilogUtil;
import nc.vo.ta.overtime.OvertimeCommonVO;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.overtime.register.validator.SaveOvertimeRegValidator;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnWorkTimeVO;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.pub.ICommonConst;
import nc.vo.ta.pub.IMetaDataIDConst;
import nc.vo.ta.pub.PubPermissionUtils;
import nc.vo.ta.pub.TaBillRegQueryParams;
import nc.vo.ta.pub.TaNormalQueryUtils;
import nc.vo.ta.timeitem.OverTimeTypeCopyVO;
import nc.vo.ta.timeitem.OverTimeTypeVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class OvertimeRegisterMaintainImpl implements IOvertimeRegisterQueryMaintain, IOvertimeRegisterManageMaintain {

	private HrBatchService serviceTemplate;

	private HrBatchService getServiceTemplate() {
		if(serviceTemplate==null){
			serviceTemplate = new HrBatchService(IMetaDataIDConst.OVERTIME);
		}
		return serviceTemplate;
	}

	@Override
	public OvertimeRegVO queryByPk(String pk) throws BusinessException {
		return getServiceTemplate().queryByPk(OvertimeRegVO.class, pk);
	}

	@Override
	public OvertimeRegVO[] doCheck(OvertimeRegVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos)) {
			return null;
		}
//		for(OvertimeRegVO vo:vos){
//			PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(), vo.getBegindate(), vo.getEnddate());
//		}
		Map<String, OvertimeRegVO[]> psnRegVOMap = CommonUtils.group2ArrayByField(OvertimeCommonVO.PK_PSNDOC, vos);
		String[] pk_psndocs = psnRegVOMap.keySet().toArray(new String[0]);
		String pk_org = vos[0].getPk_org();
		IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(vos);
		PeriodServiceFacade.checkMonth(pk_org, pk_psndocs, maxDateScope.getBegindate(), maxDateScope.getEnddate());
		
		PeriodVO[] periods = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryPeriodsByDateScope(pk_org, maxDateScope.getBegindate(), maxDateScope.getEnddate());
		// ȡ�����Ϣ�Ϳ��ڹ���
		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		Map<String, ShiftVO> shiftMap = CommonMethods.createShiftMapFromAggShiftMap(aggShiftMap);
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		Map<String, TimeZone> timeZoneMap = timeRule.getTimeZoneMap();
		BillMethods.processBeginEndDatePkJobOrgTimeZone(vos, timeZoneMap);
		//����ҪУ������ݰ���Ա����
		Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryDateJobOrgMapByPsndocInSQL(pk_psndocs, vos);
		// ȡˢǩ����Ϣ
		Map<String, ICheckTime[]> psnCheckTimeMap = CheckTimeServiceFacade.queryCheckTimeMapByPsnsAndDateScope(pk_org, pk_psndocs, maxDateScope.getBegindate(), maxDateScope.getEnddate());
		String psndocInSql = new InSQLCreator().getInSQL(pk_psndocs);
		// ����������Ϣ
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class).queryCalendarVOByPsnInSQL(pk_org, vos, 6, psndocInSql);
		// ����������Ϣ
		Map<String, Map<UFLiteralDate, TimeDataVO>> psnTimedataMap = NCLocator.getInstance().lookup(ITimeDataQueryService.class).queryVOMapByPsndocInSQL(pk_org, maxDateScope.getBegindate(), maxDateScope.getEnddate(), psndocInSql);
		//���ɻ����ϸ�
		Integer strictType = timeRule.getOvertimecheckrule();
		boolean isStrict = strictType==null || strictType.intValue()==0;//Ĭ�����ϸ�
		//V63���ӣ����Ǽ���Ӱ�ʱ��Ҫ���Ǽ����Ű��Ϊ�Ӱ�����,��ʱ������ѯ����Ҫ����Ϣ
		Map<String, HRHolidayVO[]> psnOverTimeHolidayScope =BillProcessHelperAtServer.getOverTimeHolidayScope(vos);
		
		List<OvertimeRegVO> result = new ArrayList<OvertimeRegVO>();
		// ��ž����ݵ�map
		Map<String, Integer> deductMap = new HashMap<String, Integer>();
		Map<String, UFDouble> hourMap = new HashMap<String, UFDouble>();
		for(String pk_psndoc: psnRegVOMap.keySet()){
			OvertimeRegVO[] regVOs = psnRegVOMap.get(pk_psndoc);
			Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(pk_psndoc);
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap = CommonMethods.createDateTimeZoneMap(dateOrgMap, timeZoneMap);
			Map<UFLiteralDate, TimeDataVO> timeDataMap = MapUtils.isEmpty(psnTimedataMap)?null:psnTimedataMap.get(pk_psndoc);
			Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(pk_psndoc);
			ICheckTime[] checkTimes = MapUtils.isEmpty(psnCheckTimeMap)?null:psnCheckTimeMap.get(pk_psndoc);
			HRHolidayVO[] PsnEnjoyHrHolidayVOs = psnOverTimeHolidayScope.get(pk_psndoc);
			//����У����˵����мӰ൥
			for(OvertimeRegVO vo:regVOs){
				// ����ҪУ�����У�����ת���ݲ���ҪУ��
				if(!vo.getIsneedcheck().booleanValue() || vo.getIscheck().booleanValue() || vo.getIstorest().booleanValue()) {
					continue;
				}
				if(!BillMethods.isDateScopeInPeriod(periods, vo)) {
					continue;
				}
				doCheck(isStrict, vo, aggShiftMap, timeDataMap, shiftMap, dateTimeZoneMap, calendarMap, checkTimes, timeRule,PsnEnjoyHrHolidayVOs);
				// �������ÿ�ʼ���ںͽ�������
				CommonMethods.processBeginEndDate(vo, timeZoneMap);
				Integer tmpDiff = vo.getDiffhour()==null?0:vo.getDiffhour().intValue();//��λΪ�֣����ռӰ��쳣ʱ����
				deductMap.put(vo.getPk_overtimereg(), vo.getDeduct());
				hourMap.put(vo.getPk_overtimereg(), vo.getOvertimehour());
				vo.setDeduct(vo.getDeduct()+tmpDiff);
				result.add(vo);
//				TimeItemCopyVO overtimeItem = overtimeTypeMap.get(vo.getPk_overtimetype());
//				//1-�죬2-Сʱ,Ĭ��Сʱ
//				int timeitemUnit = overtimeItem.getTimeitemunit()==null?TimeItemCopyVO.TIMEITEMUNIT_HOUR:overtimeItem.getTimeitemunit().intValue();
//				UFDouble exceptionDiff = UFDouble.ZERO_DBL;
//				if(timeitemUnit==TimeItemCopyVO.TIMEITEMUNIT_HOUR){
//					exceptionDiff = new UFDouble(tmpDiff/60.00);
//				}else{
//					exceptionDiff = (new UFDouble(tmpDiff/60.00)).div(new UFDouble(timeRule.getDaytohour2()));
//				}
			}
		}
		if(CollectionUtils.isEmpty(result))
			return null;
		OvertimeRegVO[] rsVOs = result.toArray(new OvertimeRegVO[0]);
		// ����ʱ��
		BillProcessHelperAtServer.calOvertimeLength(pk_org, rsVOs);
		// ���������¸�ֵ
		for(OvertimeRegVO rsVO:rsVOs){
			rsVO.setOvertimehour(hourMap.get(rsVO.getPk_overtimereg()));
			rsVO.setDiffhour(rsVO.getOvertimehour().sub(rsVO.getActhour()));
			rsVO.setIscheck(UFBoolean.TRUE);
			rsVO.setDeduct(deductMap.get(rsVO.getPk_overtimereg()));
		}
		rsVOs = getServiceTemplate().update(true, rsVOs);
		//ҵ����־
		TaBusilogUtil.writeOvertimeRegCheckBusiLog(rsVOs);
		return rsVOs;
	}

	/**
	 * У��Ӱ�Ǽǵ���
	 * @param isStrict �Ƿ��ϸ�У��
	 * @param vo
	 * @param aggShiftMap
	 * @param shiftMap
	 * @param timeRule
	 * @param psnEnjoyHrHolidayVOs 
	 * @throws BusinessException
	 */
	private void doCheck(boolean isStrict, OvertimeRegVO vo,
			Map<String, AggShiftVO> aggShiftMap, Map<UFLiteralDate, TimeDataVO> timeDataMap,
			Map<String, ShiftVO> shiftMap, Map<UFLiteralDate, TimeZone> dateTimeZoneMap, 
			Map<UFLiteralDate, AggPsnCalendar> calendarMap, ICheckTime[] allCheckTimes,
			TimeRuleVO timeRule, HRHolidayVO[] psnEnjoyHrHolidayVOs)  throws BusinessException {
		// �Ƿ������Ž�
		boolean isCheckWithStatus = timeRule.getCheckinflag().booleanValue();
		// ��ȡ���ݹ�����
//		UFLiteralDate belongDate = new OvertimeDAO().getBelongDate(vo, shiftMap, dateTimeZoneMap);
		UFLiteralDate belongDate = new OvertimeDAO().getBelongDate(vo, shiftMap,calendarMap, dateTimeZoneMap);
		ITimeScope filterScope = CommonMethods.getKQScope4OvertimeCheckAndGen(belongDate, 5, shiftMap, calendarMap, dateTimeZoneMap);
		// ȡˢǩ����Ϣ
		ICheckTime[] checkTimes = CheckTimeUtils.filterOrderedTimesYN(allCheckTimes, filterScope.getScope_start_datetime(), filterScope.getScope_end_datetime());
		if(ArrayUtils.isEmpty(checkTimes)){
			resetCheckValue(vo);
			return;
		}
		Arrays.sort(checkTimes);
		// ȡ�������ݴ�����Ϣ
//		Map<String, Map<UFLiteralDate, TimeDataVO>> timeDataMap = NCLocator.getInstance().lookup(ITimeDataQueryService.class).queryVOMapByPsndocInSQL(vo.getPk_org(), belongDate, belongDate, "'"+vo.getPk_psndoc()+"'");
		// û��ˢǩ����Ϣ��û�����ù���������û�п������ݴ�������ֱ������
		if(MapUtils.isEmpty(calendarMap) ||
				calendarMap.get(belongDate)==null ||
				calendarMap.get(belongDate).getPsnCalendarVO()==null ||
				StringUtils.isBlank(calendarMap.get(belongDate).getPsnCalendarVO().getPk_shift()) 
//				||
//				timeDataMap==null || timeDataMap.get(vo.getPk_psndoc())==null || timeDataMap.get(vo.getPk_psndoc()).get(belongDate)==null
		){
			resetCheckValue(vo);
			return;
		}
		String pk_shift = calendarMap.get(belongDate).getPsnCalendarVO().getPk_shift();
		// �����ռӰ�У��
		if(ShiftVO.PK_GX.equals(pk_shift)){
			// �����ռӰ�У��
			if(isCheckWithStatus){
				// �Ž������ռӰ�У��
				checkGXBillsWithFlag(isStrict, vo, checkTimes);
				return;
			}
			// ���Ž������ռӰ�У��
			checkGXBills(isStrict, vo, checkTimes);
			return;
		}
		// �����ռӰ�У��
		ShiftVO workShift = shiftMap.get(pk_shift);
		AggPsnCalendar aggCalendar = calendarMap.get(belongDate);
		//�ɹ涨�ϰൽ�涨�°���ɵ�ʱ��Ρ�����ǵ��԰࣬����Ҫ�̻�
		ITimeScope workScope = aggCalendar.getPsnCalendarVO().isFlexibleFinal()?
				OvertimeGenMaintainImpl.getSolidifyWorkTimeScope(vo.getPk_org(), vo.getPk_psndoc(), belongDate, timeRule, aggShiftMap, calendarMap, dateTimeZoneMap):
					aggCalendar.toWorkScope();
		
		try{
			// ���ݿ�ʼʱ���ڹ涨�°�ʱ��֮��У����ʱ�Ӱ࣬����У����ǰ�Ӱ�
			if(vo.getOvertimebegintime().compareTo(workScope.getScope_end_datetime())>=0){
				// У�鹤������ʱ�Ӱࣨ�Ž�/���Ž���
				if(isCheckWithStatus){
					// �Ž���������ʱ�Ӱ�У��
					checkDelayWorkBillsWithFlag(vo, workScope, workShift, checkTimes);
					return;
				}
				// ���Ž���������ʱ�Ӱ�У��
				checkDelayWorkBills(isStrict, vo, workScope,workShift,  checkTimes);
				return;
			}
			// У�鹤������ǰ�Ӱࣨ�Ž�/���Ž���
			if(isCheckWithStatus){
				// �Ž���������ǰ�Ӱ�У��
				checkBeforeWorkBillsWithFlag(vo, workScope, workShift,  checkTimes);
				return;
			}
			// ���Ž���������ǰ�Ӱ�У��
			checkBeforeWorkBills(isStrict, vo, workScope,workShift, checkTimes,psnEnjoyHrHolidayVOs);
			return;
		}finally{
			//�Լ����Ű�������Ҫ��У��һ�¼����Ű��Ϊ�Ӱ�ĵ�ʱ���
			checkHolidayOvertime(vo, timeDataMap, psnEnjoyHrHolidayVOs, isCheckWithStatus,checkTimes, workScope,aggCalendar,workShift);
		}
	}

	/**
	 * V63���ӣ�У������Ű��Ϊ�Ӱ��ʱ���
	 * ����ʱ��ο���ֻ�ǹ���ʱ��ε�һ���֣����ݵļӰ�ʱ���п��ܺͼ���ʱ���н�����Ҳ�п��ܰ����Ǽ����ҷǹ���ʱ��εĲ���
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
			HRHolidayVO[] psnEnjoyHrHolidayVOs, boolean isCheckWithStatus,
			ICheckTime[] checkTimes, ITimeScope workScope, AggPsnCalendar aggCalendar, ShiftVO workShift) throws BusinessException {
		if(checkTimes.length<2) {
			return;
		}
		//�����Ű��Ϊ�Ӱ࣬���洦����ɺ�Ӱ�ʱ����Ҫ���ϼ���ʱ��κ��ϰ�ʱ��εĽ���
		ITimeScope[] holidayScopes = TimeScopeUtils.intersectionTimeScopes(workScope, psnEnjoyHrHolidayVOs);
		if(ArrayUtils.isEmpty(holidayScopes)) {
			return;
		}
		//�ٺ�ʵ�ʵ����°�ǩ��ʱ��ȡ����
//		ITimeScope realscope = new DefaultTimeScope();
//		if(isCheckWithStatus){
//			//�����in��
//			ICheckTime earliestIn = CheckTimeUtilsWithCheckFlag.findOrderedEarliest(checkTimes, ICheckTime.CHECK_FLAG_IN);
//			//������out��
//			ICheckTime latestout = CheckTimeUtilsWithCheckFlag.findOrderedLatest(checkTimes, ICheckTime.CHECK_FLAG_OUT);
//			realscope.setScope_start_datetime(earliestIn.getDatetime());
//			realscope.setScope_end_datetime(latestout.getDatetime());
//		}else{
//			realscope.setScope_start_datetime(checkTimes[0].getDatetime());
//			realscope.setScope_end_datetime(checkTimes[checkTimes.length-1].getDatetime());
//		}
//		TimeDataVO[] timeDatavos = NCLocator.getInstance().lookup(ITimeDataQueryMaintain.class).queryByPsn(vo.getPk_org(), vo.getPk_psndoc(), vo.getBegindate(), vo.getEnddate());

		UFLiteralDate[] dates = CommonUtils.createDateArray(vo.getBegindate(), vo.getEnddate());
		//ȡ��ʱ��ε�ʵ�ʵ����°�ʱ��
		PsnWorkTimeVO[] wtvos = aggCalendar.getPsnWorkTimeVO();
		if(ArrayUtils.isEmpty(wtvos))
			return;
		TimeScopeUtils.sort(wtvos);
		int length = wtvos.length;
		List<TimeDataVO> timeDataList = new ArrayList<TimeDataVO>();
		List<ITimeScope> realScopesList = new ArrayList<ITimeScope>();
		//20151210,�ж��Ƿ���п������ݴ�����δ������ȡˢ������
		if(timeDataMap!=null&&timeDataMap.size()>0){
			for(UFLiteralDate date:dates){
				TimeDataVO timeData = timeDataMap.get(date);
				if(timeData==null)
					continue;
				timeDataList.add(timeData);
			}
			if(!CollectionUtils.isEmpty(timeDataList)){
				TimeDataVO timeDataVO = timeDataList.get(0);
				for(int i=0;i<length;i++){
					ITimeScope realScope = new DefaultTimeScope();
					if(timeDataVO.getBegintime(i)!=null&&timeDataVO.getEndtime(i)!=null){
						realScope.setScope_start_datetime(timeDataVO.getBegintime(i));
						realScope.setScope_end_datetime(timeDataVO.getEndtime(i));
						realScopesList.add(realScope);
					}
				}
			}
		}
		if(CollectionUtils.isEmpty(realScopesList)){
			ITimeScope realScope = new DefaultTimeScope();
			realScope.setScope_start_datetime(checkTimes[0].getDatetime());
			realScope.setScope_end_datetime(checkTimes[checkTimes.length-1].getDatetime());
			realScopesList.add(realScope);
		}
		
		ITimeScope[] realScopes = realScopesList.toArray(new ITimeScope[0]);
		
		
		holidayScopes = TimeScopeUtils.intersectionTimeScopes(realScopes, holidayScopes);
		
		//��¼ԭ�Ӱ�ʱ�䣬�������ʱ�䲻�ܳ����Ǽ���д��ԭʼʱ��
		ITimeScope orignalScope = new DefaultTimeScope();
		orignalScope.setScope_start_datetime(vo.getBegintimebeforecheck());
		orignalScope.setScope_end_datetime(vo.getEndtimebeforecheck());
		holidayScopes = TimeScopeUtils.intersectionTimeScopes(orignalScope, holidayScopes);
		//���˴�holidayScopesΪ����ʱ�䡢����ʱ�䡢ʵ�����°ࣨǩ����ʱ�䡢��������ʱ�����ߵĽ���
		
		if(ArrayUtils.isEmpty(holidayScopes)) {
			return;
		}
		//���ݵ�ʵ�ʼӰ�ʱ���п��ܻ������ǹ���ʱ��Ρ�����ʱ��Ρ�ʵ��ǩ��ʱ��εĽ�����
		ITimeScope[] notworkScops = TimeScopeUtils.minusTimeScopes(new ITimeScope[]{orignalScope},new ITimeScope[]{ workScope});
		//�˴��ǹ���ʱ����п��ܺ�����ʱ�в���Ϊ�Ӱ��ʱ��Σ�������°�ʱ�䵽��ʼ��Ϊ�°���ʱ�Ӱ��ʱ��Σ���Ӧ�ÿ۳���
		//�ϰ��ӳ��в���Ϊ�Ӱ�Ĳ���
		List<ITimeScope> notInclude = new ArrayList<ITimeScope>();
		if(workShift.getUseontmrule()!=null&&workShift.getUseontmrule().booleanValue()){
			ITimeScope notIncludBeforScope = new DefaultTimeScope(workShift.getOntmend().multiply(60).longValue(),workScope.getScope_start_datetime());
			notInclude.add(notIncludBeforScope);
		}
		//�°��ӳ��в���Ϊ�Ӱ�Ĳ���
		if(workShift.getUseovertmrule()!=null&&workShift.getUseovertmrule().booleanValue()){
			ITimeScope notIncludAfterScope = new DefaultTimeScope(workScope.getScope_end_datetime(),workShift.getOvertmbegin().multiply(60).longValue());
			notInclude.add(notIncludAfterScope);
		}
//		ITimeScope[] notIncludScopes = new ITimeScope[]{notIncludBeforScope,notIncludAfterScope};
		if(CollectionUtils.isNotEmpty(notInclude)){
			notworkScops = TimeScopeUtils.minusTimeScopes(notworkScops, notInclude.toArray(new ITimeScope[0]));
		}
		
		notworkScops = TimeScopeUtils.intersectionTimeScopes(notworkScops, realScopes);//ʵ��ǩ���ķǹ���ʱ��
		
		ITimeScope[] allScopes =  TimeScopeUtils.mergeTimeScopes(holidayScopes, notworkScops);
		
		UFDateTime earliesStartTime = TimeScopeUtils.getEarliesStartTime(allScopes);
		//����ʼʱ�������ϰ�ʱ�仹Ҫ�ж��Ƿ��˼�Ϊ�Ӱ�Ŀ�ʼʱ��
		if(earliesStartTime.before(workScope.getScope_start_datetime())){
			if(workShift.getUseontmrule()!=null&&workShift.getUseontmrule().booleanValue()){
				if(earliesStartTime.after(DateTimeUtils.getDateTimeBeforeMills(workScope.getScope_start_datetime(), workShift.getOntmbeyond().multiply(60*1000).longValue())))
					earliesStartTime = workScope.getScope_start_datetime();
			}else{//�ϰ�ǰ���ƼӰ�
				earliesStartTime = workScope.getScope_start_datetime();
			}
		}
		UFDateTime latestEndTime = TimeScopeUtils.getLatestEndTime(allScopes);
		//������ʱ�������°�ʱ�䣬��Ҫ�ж��Ƿ��˿��Լ�Ϊ��ʱ�Ӱ��ʱ��
		if(latestEndTime.after(workScope.getScope_end_datetime())){
			if(workShift.getUseovertmrule()!=null&&workShift.getUseovertmrule().booleanValue()){
				if(latestEndTime.before(DateTimeUtils.getDateTimeAfterMills(workScope.getScope_end_datetime(),workShift.getOvertmbeyond().multiply(60*1000).longValue())))
					latestEndTime = workScope.getScope_end_datetime();
			}else{//�°�󲻼ƼӰ�
				latestEndTime = workScope.getScope_end_datetime();
			}
		}
		if(vo.getOvertimebegintime().compareTo(vo.getOvertimeendtime())==0){
			vo.setOvertimebegintime(earliesStartTime);
			vo.setOvertimeendtime(latestEndTime);
		}else{
			if(earliesStartTime.before(vo.getOvertimebegintime())){
				vo.setOvertimebegintime(earliesStartTime);
			}
			if(latestEndTime.after(vo.getOvertimeendtime())){
				vo.setOvertimeendtime(latestEndTime);
			}
		}
		//�۳�ʱ��
		Long diffLength = Long.valueOf(0);
		
		//�ϰ�ʱ���ܲ���ȱ�ڡ��ٵ������˵�ʱ����Ӧ�۳�,�ǲ���ʱ��
		//ȡӦ���°�ʱ���
		PsnWorkTimeVO[] psnWorkTimeVOs = aggCalendar.getPsnWorkTimeVO();
		//�۳�ʱ��=��Ӧ���°�ʱ���-ʵ�����°�ʱ��Σ��ɼ���ʱ��Ρɵ���
		ITimeScope[] exceptionScops = TimeScopeUtils.minusTimeScopes(psnWorkTimeVOs, realScopes);
		exceptionScops = TimeScopeUtils.intersectionTimeScopes(exceptionScops, new ITimeScope[]{vo});
		exceptionScops = TimeScopeUtils.intersectionTimeScopes(exceptionScops, psnEnjoyHrHolidayVOs);
		if(!ArrayUtils.isEmpty(exceptionScops)){
			diffLength += TimeScopeUtils.getLength(exceptionScops)/60;
		}
		
//		//��ʱ�Ӱ���Ӧ�۳���ʱ��,ʱ���������Ѿ��۳��ˣ��˴�����Ҫ�ظ��۳�
//		ITimeScope[] minusScopes = TimeScopeUtils.intersectionTimeScopes(notIncludScopes,new ITimeScope[]{vo});
//		if(!ArrayUtils.isEmpty(minusScopes)){
//			diffLength += TimeScopeUtils.getLength(minusScopes)/60;
//		}
//			return;
//		long length2 = TimeScopeUtils.getLength(exceptionScops)/60;
//		vo.setDiffhour(new UFDouble(length2));
		vo.setDiffhour(new UFDouble(diffLength));
	}
	

	/**
	 * ���Ž����ݼӰ�У��
	 * @param isStrict
	 * @param vo
	 * @param kqScope
	 * @param checkTimes
	 */
	private void checkGXBills(boolean isStrict, OvertimeRegVO vo, ICheckTime[] checkTimes){// �Ե��ݿ�ʼʱ��ͽ���ʱ�佫ˢǩ����Ϣ����
		if(!isStrict){//����ǿ���У�飬��ܼ򵥣��õ�һ��ˢǩ�������һ��ˢǩ����ɵ�ʱ�����voȡ��������
			if(ArrayUtils.getLength(checkTimes)<2){
				resetCheckValue(vo);
				return;
			}
			ITimeScope scope = new DefaultTimeScope(checkTimes[0].getDatetime(),checkTimes[checkTimes.length-1].getDatetime());
			ITimeScope interScope = TimeScopeUtils.intersectionTimeScope(scope, vo);
			if(interScope==null||interScope.getScope_end_datetime().equals(interScope.getScope_start_datetime())){
				resetCheckValue(vo);
				return;
			}
			vo.setBegintimebeforecheck(vo.getOvertimebegintime());
			vo.setEndtimebeforecheck(vo.getOvertimeendtime());
			vo.setOvertimebegintime(interScope.getScope_start_datetime());
			vo.setOvertimeendtime(interScope.getScope_end_datetime());
			return;
		}
		ICheckTime[] preCheckTimes = CheckTimeUtils.filterOrderedTimesYN(checkTimes, checkTimes[0].getDatetime(), vo.getOvertimebegintime());
		ICheckTime[] curCheckTimes = CheckTimeUtils.filterOrderedTimesYY(checkTimes, vo.getOvertimebegintime(), vo.getOvertimeendtime());
		ICheckTime[] nxtCheckTimes = CheckTimeUtils.filterOrderedTimesNY(checkTimes, vo.getOvertimeendtime(), checkTimes[checkTimes.length-1].getDatetime());

		int size = curCheckTimes.length;
		if(size>=2){
			vo.setBegintimebeforecheck(vo.getOvertimebegintime());
			vo.setEndtimebeforecheck(vo.getOvertimeendtime());
			vo.setOvertimebegintime(curCheckTimes[size-2].getDatetime());
			vo.setOvertimeendtime(curCheckTimes[size-1].getDatetime());
			return;
		}
		if(size==1){
			if(!ArrayUtils.isEmpty(nxtCheckTimes)){
				vo.setBegintimebeforecheck(vo.getOvertimebegintime());
				vo.setEndtimebeforecheck(vo.getOvertimeendtime());
				vo.setOvertimebegintime(curCheckTimes[0].getDatetime());
				return;
			}
			if(!ArrayUtils.isEmpty(preCheckTimes)){
				vo.setBegintimebeforecheck(vo.getOvertimebegintime());
				vo.setEndtimebeforecheck(vo.getOvertimeendtime());
				vo.setOvertimeendtime(curCheckTimes[0].getDatetime());
				return;
			}
			resetCheckValue(vo);
			return;
		}
		if(ArrayUtils.isEmpty(preCheckTimes)||ArrayUtils.isEmpty(nxtCheckTimes)){
			resetCheckValue(vo);
			return;
		}
		// ���������ˢǩ�����ݣ�����ʱ�䲻�øı�
		vo.setBegintimebeforecheck(vo.getOvertimebegintime());
		vo.setEndtimebeforecheck(vo.getOvertimeendtime());
		return;
	}

	/**
	 * �Ž������ռӰ�У��
	 * @param isStrict �Ƿ��ϸ�У�顣Ŀǰ�ϸ�Ϳ�����һ�����㷨����˴˲�������������
	 * @param vo
	 * @param kqScope
	 * @param checkTimes
	 */
	private void checkGXBillsWithFlag(boolean isStrict, OvertimeRegVO vo, ICheckTime[] checkTimes){
		// ���������ݵķ�Χ�ڣ��ҳ������in��������out��Ȼ���뵥��ȡ����
		ICheckTime earliestIn = CheckTimeUtilsWithCheckFlag.findOrderedEarliest(checkTimes, ICheckTime.CHECK_FLAG_IN);
		if(earliestIn==null){
			resetCheckValue(vo);
			return;
		}
		ICheckTime lastOut = CheckTimeUtilsWithCheckFlag.findOrderedLatest(checkTimes, ICheckTime.CHECK_FLAG_OUT);
		if(lastOut==null||!lastOut.getDatetime().after(earliestIn.getDatetime())){
			resetCheckValue(vo);
			return;
		}
		ITimeScope scope = TimeScopeUtils.intersectionTimeScope(vo, new DefaultTimeScope(earliestIn.getDatetime(), lastOut.getDatetime()));
		if(scope==null||scope.getScope_start_datetime().equals(scope.getScope_end_datetime())){
			resetCheckValue(vo);
			return;
		}
		vo.setBegintimebeforecheck(vo.getOvertimebegintime());
		vo.setEndtimebeforecheck(vo.getOvertimeendtime());
		vo.setOvertimebegintime(scope.getScope_start_datetime());
		vo.setOvertimeendtime(scope.getScope_end_datetime());
	}

	/**
	 * ���Ž�ƽ����ʱ�Ӱ�У��
	 * @param isStrict �Ƿ��ϸ�У��
	 * @param vo
	 * @param workScope
	 * @param checkTimes
	 * @param kqScope
	 */
	private void checkDelayWorkBills(boolean isStrict, OvertimeRegVO vo,ITimeScope workScope,ShiftVO shiftVO,ICheckTime[] checkTimes){
		//����˼���ǣ��ȹ�������ʱ��Σ���ʱ�Ӱ����㣨��ζ����еĲ��������°�󣨺����ĵ�һ�ſ���һ��ʱ��Σ��п���û�У����°�󣨺����ĵ�һ�ſ�֮������п���һ�ŵ����һ����
		//һ��ʱ��Σ����ɣ��������������Ϊһ��ʱ��Σ��ϸ�
		//������ʱ�������д�ĵ���ȡ���������������������������ȡ����һ��
		//�涨�°�ʱ��
		UFDateTime ruleOffDutyTime = workScope.getScope_end_datetime();
		//���˳��ӹ涨�°�ʱ�䣨����֮���ˢǩ������
		ICheckTime[] filteredCheckTimes = CheckTimeUtils.filterOrderedTimesYY(checkTimes, ruleOffDutyTime, checkTimes[checkTimes.length-1].getDatetime());
		if(ArrayUtils.isEmpty(filteredCheckTimes)){
			resetCheckValue(vo);
			return;
		}
		ITimeScope overtimeRuleScope = shiftVO.getOvertimeRuleScope(ruleOffDutyTime, true);
		List<ITimeScope> scopes = new ArrayList<ITimeScope>(2);//�����ͼӰ൥������ʱ�������
		ICheckTime firstCheckTimeAfterOffDuty = filteredCheckTimes[0];//�°��ĵ�һ�ſ�
		if(overtimeRuleScope!=null&&!firstCheckTimeAfterOffDuty.getDatetime().before(overtimeRuleScope.getScope_end_datetime())){
			if(firstCheckTimeAfterOffDuty.getDatetime().after(overtimeRuleScope.getScope_start_datetime())) {
				scopes.add(new DefaultTimeScope(overtimeRuleScope.getScope_start_datetime(),firstCheckTimeAfterOffDuty.getDatetime()));
			}
		}
		//����°�󣨺�����ˢǩ������>=3�Σ���
		//if �ϸ�������Ŵճ�һ��
		//if ���ɣ��ڶ��ź����һ�Ŵճ�һ��
		if(filteredCheckTimes.length>=3){
			UFDateTime beginTime = isStrict?filteredCheckTimes[filteredCheckTimes.length-2].getDatetime():filteredCheckTimes[1].getDatetime();
			UFDateTime endTime = filteredCheckTimes[filteredCheckTimes.length-1].getDatetime();//���һ��ˢǩ��
			ITimeScope scope = new DefaultTimeScope(beginTime,endTime);
			if(overtimeRuleScope==null) {
				scopes.add(scope);
			} else{
				if(!endTime.before(overtimeRuleScope.getScope_end_datetime())){
					beginTime = DateTimeUtils.max(overtimeRuleScope.getScope_start_datetime(), beginTime);
					if(scope.getScope_end_datetime().after(beginTime)){
						scope.setScope_start_datetime(beginTime);
						scopes.add(scope);
					}
				}
			}
		}
		if(scopes.size()==0){
			resetCheckValue(vo);
			return;
		}
		ITimeScope[] interScopes = TimeScopeUtils.intersectionTimeScopes(vo, scopes.toArray(new ITimeScope[0]));
		if(ArrayUtils.isEmpty(interScopes)){
			resetCheckValue(vo);
			return;
		}
		ITimeScope finalScope = findLongger(interScopes);//���ս����Ľ���������������������ȡ����Ϊ���
		
		if(finalScope.getScope_start_datetime().equals(finalScope.getScope_end_datetime())){
			resetCheckValue(vo);
			return;
		}
		vo.setBegintimebeforecheck(vo.getOvertimebegintime());
		vo.setEndtimebeforecheck(vo.getOvertimeendtime());
		vo.setOvertimebegintime(finalScope.getScope_start_datetime());
		vo.setOvertimeendtime(finalScope.getScope_end_datetime());
	}

	/**
	 * ���Ž���������ǰ�Ӱ�У��
	 * @param isStrict
	 * @param vo
	 * @param workScope
	 * @param checkTimes
	 * @param psnEnjoyHrHolidayVOs V63���ӣ��Ӱ�ʱ����������Ҽ����Ű��Ϊ�Ӱ�
	 * @param kqScope
	 */
	private void checkBeforeWorkBills(boolean isStrict, OvertimeRegVO vo,ITimeScope workScope,ShiftVO shiftVO, ICheckTime[] checkTimes, HRHolidayVO[] psnEnjoyHrHolidayVOs){

		//����˼���ǣ��ȹ�������ʱ��Σ���ʱ�Ӱ����㣨��ζ����еĲ��������ϰ�ǰ�������ĵ�һ�ſ���һ��ʱ��Σ��п���û�У����ϰ�ǰ�������ĵ�һ�ſ�֮������п���һ�ŵ���ǰһ����
		//һ��ʱ��Σ����ɣ���������ǰ����Ϊһ��ʱ��Σ��ϸ�
		//������ʱ�������д�ĵ���ȡ���������������������������ȡ����һ��
		//�涨�ϰ�ʱ��
		UFDateTime ruleOnDutyTime = workScope.getScope_start_datetime();
		//���˳��ӹ涨�ϰ�ʱ�䣨����֮ǰ��ˢǩ������
		ICheckTime[] filteredCheckTimes = CheckTimeUtils.filterOrderedTimesYY(checkTimes,  checkTimes[0].getDatetime(),ruleOnDutyTime);
		if(ArrayUtils.isEmpty(filteredCheckTimes)){
			resetCheckValue(vo);
			return;
		}
		ITimeScope overtimeRuleScope = shiftVO.getOvertimeRuleScope(ruleOnDutyTime, false);
		List<ITimeScope> scopes = new ArrayList<ITimeScope>(2);//�����ͼӰ൥������ʱ�������
		ICheckTime firstCheckTimeBeforeOnDuty = filteredCheckTimes[filteredCheckTimes.length-1];//�ϰ�ǰ�����һ�ſ�
		if(overtimeRuleScope!=null&&!firstCheckTimeBeforeOnDuty.getDatetime().after(overtimeRuleScope.getScope_start_datetime())){
			if(firstCheckTimeBeforeOnDuty.getDatetime().before(overtimeRuleScope.getScope_end_datetime())) {
				scopes.add(new DefaultTimeScope(firstCheckTimeBeforeOnDuty.getDatetime(),overtimeRuleScope.getScope_end_datetime()));
			}
		}
		//����ϰ��ǰ��������ˢǩ������>=3�Σ���
		//if �ϸ���ǰ�����Ŵճ�һ��
		//if ���ɣ��ڶ��ź���ǰ��һ�Ŵճ�һ��
		if(filteredCheckTimes.length>=3){
			UFDateTime beginTime = filteredCheckTimes[0].getDatetime();//��ǰһ��ˢǩ��
			UFDateTime endTime = isStrict?filteredCheckTimes[1].getDatetime():filteredCheckTimes[filteredCheckTimes.length-2].getDatetime();
			ITimeScope scope = new DefaultTimeScope(beginTime,endTime);
			if(overtimeRuleScope==null) {
				scopes.add(scope);
			} else{//����Ҫ��Ӱ�������У�飬��֤��ʼʱ���ڼ�Ϊ�Ӱ��ʱ��֮ǰ���������ҽ���ʱ���ڿ�ʼ�ƼӰ��ʱ��֮ǰ
				if(!beginTime.after(overtimeRuleScope.getScope_start_datetime())){
					endTime = DateTimeUtils.min(overtimeRuleScope.getScope_end_datetime(), endTime);
					if(scope.getScope_start_datetime().before(endTime)){
						scope.setScope_end_datetime(endTime);
						scopes.add(scope);
					}
				}
			}
		}
		if(scopes.size()==0){
			resetCheckValue(vo);
			return;
		}
		ITimeScope[] interScopes = TimeScopeUtils.intersectionTimeScopes(vo, scopes.toArray(new ITimeScope[0]));
		if(ArrayUtils.isEmpty(interScopes)){
			resetCheckValue(vo);
			return;
		}
		ITimeScope finalScope = findLongger(interScopes);//���ս����Ľ���������������������ȡ����Ϊ���
		if(finalScope.getScope_start_datetime().equals(finalScope.getScope_end_datetime())){
			resetCheckValue(vo);
			return;
		}
		vo.setBegintimebeforecheck(vo.getOvertimebegintime());
		vo.setEndtimebeforecheck(vo.getOvertimeendtime());
		vo.setOvertimebegintime(finalScope.getScope_start_datetime());
		vo.setOvertimeendtime(finalScope.getScope_end_datetime());
	}
	
	/**
	 * ��scopes���ҳ��ϳ���һ����scopes�ĸ���������2
	 * @param scopes
	 * @return
	 */
	private ITimeScope findLongger(ITimeScope[] scopes){
		if(ArrayUtils.isEmpty(scopes)) {
			return null;
		}
		if(scopes.length==1) {
			return scopes[0];
		}
		long len1 = TimeScopeUtils.getLength(scopes[0]);
		long len2 = TimeScopeUtils.getLength(scopes[1]);
		return len1<len2?scopes[1]:scopes[0];
	}

	/**
	 * �Ž���������ʱ�Ӱ�У��
	 * @param vo
	 * @param workScope
	 * @param shiftVO
	 * @param psnCalendar
	 * @param checkTimes
	 * @param workScope
	 * @param timeDataVO
	 */
	private void checkDelayWorkBillsWithFlag(OvertimeRegVO vo, ITimeScope workScope,ShiftVO shiftVO, ICheckTime[] checkTimes){
		//����˼·�ǣ�������û�ж�����ʱ�Ӱ������У��ʧ��
		//�ҳ��涨�°ࣨ����֮�������out���������out������out�����ڡ������ƼӰࡱ��������У��ʧ��
		//��������һ��ʱ��Σ���ʼʱ���ǿ�ʼ�ƼӰ��ʱ�䣬����ʱ����������out�������û���д�ĵ���ȡ����
		//�涨�°�ʱ��
		UFDateTime ruleOffDutyTime = workScope.getScope_end_datetime();
		//���˳��ӹ涨�°�ʱ�䣨����֮��������out��
		ICheckTime lastOut = CheckTimeUtilsWithCheckFlag.findOrderedLatestYY(checkTimes, ruleOffDutyTime, checkTimes[checkTimes.length-1].getDatetime(), ICheckTime.CHECK_FLAG_OUT);
		if(lastOut==null){
			resetCheckValue(vo);
			return;
		}
		ITimeScope overtimeRuleScope = shiftVO.getOvertimeRuleScope(ruleOffDutyTime, true);
		//���û�ж�����ʱ�Ӱ������У��ʧ�ܡ�����������out�ڹ涨������ƼӰ��ʱ��֮ǰ��ҲУ��ʧ��
		if(overtimeRuleScope==null||lastOut.getDatetime().before(overtimeRuleScope.getScope_end_datetime())){
			resetCheckValue(vo);
			return;
		}
		ITimeScope scope = new DefaultTimeScope(overtimeRuleScope.getScope_start_datetime(),lastOut.getDatetime());
		ITimeScope interScope = TimeScopeUtils.intersectionTimeScope(scope, vo);
		if(interScope==null||interScope.getScope_end_datetime().equals(interScope.getScope_start_datetime())){
			resetCheckValue(vo);
			return;
		}
		// ��������ʱ��
		vo.setBegintimebeforecheck(vo.getOvertimebegintime());
		vo.setEndtimebeforecheck(vo.getOvertimeendtime());
		vo.setOvertimebegintime(interScope.getScope_start_datetime());
		vo.setOvertimeendtime(interScope.getScope_end_datetime());
	}

	/**
	 * �Ž���������ǰ�Ӱ�У��
	 * @param vo
	 * @param workScope
	 * @param shiftVO
	 * @param checkTimes
	 * @param workScope
	 * @param timeDataVO
	 */
	private void checkBeforeWorkBillsWithFlag(OvertimeRegVO vo, ITimeScope workScope,ShiftVO shiftVO,ICheckTime[] checkTimes){
		//����˼·�ǣ�������û�ж�����ǰ�Ӱ������У��ʧ��
		//�ҳ��涨�ϰࣨ����֮ǰ������in���������in������in�����ڡ����ڼƼӰࡱ��������У��ʧ��
		//��������һ��ʱ��Σ���ʼʱ���������in��������ʱ���ǿ�ʼ�ƼӰ��ʱ�䣬���û���д�ĵ���ȡ����
		//�涨�ϰ�ʱ��
		UFDateTime ruleOnDutyTime = workScope.getScope_start_datetime();
		//���˳��ӹ涨�ϰ�ʱ�䣨����֮ǰ�����in��
		ICheckTime earliestIn = CheckTimeUtilsWithCheckFlag.findOrderedEarliestYY(checkTimes, checkTimes[0].getDatetime(), ruleOnDutyTime, ICheckTime.CHECK_FLAG_IN);
		if(earliestIn==null){
			resetCheckValue(vo);
			return;
		}
		ITimeScope overtimeRuleScope = shiftVO.getOvertimeRuleScope(ruleOnDutyTime, false);
		//���û�ж�����ǰ�Ӱ������У��ʧ�ܡ����������in�ڹ涨������ƼӰ��ʱ��֮��ҲУ��ʧ��
		if(overtimeRuleScope==null||earliestIn.getDatetime().after(overtimeRuleScope.getScope_start_datetime())){
			resetCheckValue(vo);
			return;
		}
		ITimeScope scope = new DefaultTimeScope(earliestIn.getDatetime(),overtimeRuleScope.getScope_end_datetime());
		ITimeScope interScope = TimeScopeUtils.intersectionTimeScope(scope, vo);
		if(interScope==null||interScope.getScope_end_datetime().equals(interScope.getScope_start_datetime())){
			resetCheckValue(vo);
			return;
		}
		// ��������ʱ��
		vo.setBegintimebeforecheck(vo.getOvertimebegintime());
		vo.setEndtimebeforecheck(vo.getOvertimeendtime());
		vo.setOvertimebegintime(interScope.getScope_start_datetime());
		vo.setOvertimeendtime(interScope.getScope_end_datetime());
	}

	/**
	 * У�鲻ͨ��ʱ�����Ǽǵ��ݸ�ֵ
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

		if(ArrayUtils.isEmpty(vos)) {
			return null;
		}
//		for(OvertimeRegVO vo:vos){
//			PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(), vo.getBegindate(), vo.getEnddate());
//		}
		String pk_org = vos[0].getPk_org();
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		Map<String, TimeZone> timeZoneMap = timeRule.getTimeZoneMap();
		Map<String, ShiftVO> shiftMap = ShiftServiceFacade.queryShiftVOMapByHROrg(pk_org);
		// ȡ�����ڼ��ڿ�ת���ݵ����ʱ��
//		UFDouble maxHour = SysInitQuery.getParaDbl(pk_org, OvertimeConst.OVERTIMETOREST_PARAM);
		UFDouble maxHour = timeRule.getTorestlongest();
		if(maxHour==null) {
			maxHour = UFDouble.ZERO_DBL;
		}
		DecimalFormat dcmFmt = new DecimalFormat("0.00");
		maxHour = new UFDouble(dcmFmt.format(maxHour));
		
		Map<String, OverTimeTypeCopyVO> timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryOvertimeCopyTypeMapByOrg(pk_org);
		IPeriodQueryService periodService = NCLocator.getInstance().lookup(IPeriodQueryService.class);
		IDateScope[] scopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		Map<UFLiteralDate, PeriodVO> periodVOMap = periodService.queryPeriodMapByDateScopes(pk_org, scopes);
		InSQLCreator isc = new InSQLCreator();
			String psndocInSQL = isc.getInSQL(vos, OvertimeCommonVO.PK_PSNDOC);
			Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = psndocService.queryDateJobOrgMapByPsndocInSQL(psndocInSQL, scopes);
			Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = calendarService.queryCalendarVOByPsnInSQL(pk_org, scopes, psndocInSQL);
			Map<OvertimeCommonVO, OvertimeCommonVO[]> overtimeMap = new OvertimeDAO().getOvertimeVOInPeriod(vos, timeZoneMap, shiftMap, psnDateOrgMap, psnCalendarMap, periodVOMap);

			//����ѭ������
			Map<String, OvertimeRegVO[]> psnRegVOMap = CommonUtils.group2ArrayByField(OvertimeCommonVO.PK_PSNDOC, vos);
			List<OvertimeRegVO> returnVOs = new ArrayList<OvertimeRegVO>();
			Map<String, UFDouble> hourMap = new HashMap<String, UFDouble>();
			for(String pk_psndoc:psnRegVOMap.keySet()){
				OvertimeRegVO[] regVOs = psnRegVOMap.get(pk_psndoc);
				Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(pk_psndoc);
				Map<UFLiteralDate, TimeZone> dateTimeZoneMap= CommonMethods.createDateTimeZoneMap(dateOrgMap, timeZoneMap);
				Map<String, UFDouble> periodMap = new HashMap<String, UFDouble>();
				for(OvertimeRegVO vo:regVOs){
					if(vo.getIstorest().booleanValue()) {
						throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0033")/*@res "������ת���ݵļ�¼��"*/);
					}
					// �Ӱ൥������ ���ڿ����ڼ��Ƿ��ѷ��
					Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(pk_psndoc);
					UFLiteralDate belongDate = BillProcessHelper.findBelongtoDate(vo, calendarMap, shiftMap, dateTimeZoneMap);
					PeriodVO period = periodVOMap.get(belongDate);
//					if(period==null||period.isSeal()) {
//						throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0034")/*@res "���ݹ��������ڿ����ڼ䲻���ڻ��ѷ�棡"*/);
//					}
					UFDouble sumHour = getSumTorstHour(overtimeMap.get(vo));
					String periodKey = period.getTimeyear()+"-"+period.getTimemonth();
					// ת����ʱ��
					UFDouble torestHour = vo.getToresthour();
					// ȡ��ת����ʱ��map�б����ʱ����Ϣ
					UFDouble alreadyHour = periodMap.get(periodKey)==null?UFDouble.ZERO_DBL:periodMap.get(periodKey);
					alreadyHour = alreadyHour.add(torestHour);
					if(maxHour.compareTo(sumHour.add(alreadyHour))<0) {
						throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0035"/*@res "ת����ʱ�����������ڼ���ת���ݵ����ʱ���� {0}Сʱ��"*/,  maxHour.toString()));
					}
					periodMap.put(periodKey, alreadyHour);
					// ������Աת����ʱ��
					UFDouble psnOrgHour = hourMap.get(vo.getPk_psnorg());
					hourMap.put(vo.getPk_psnorg(), psnOrgHour==null?torestHour:psnOrgHour.add(torestHour));
					// �޸ĵǼ���Ϣ
					vo.setTorestyear(toRestYear);
					vo.setTorestmonth(toRestMonth);
					vo.setIstorest(UFBoolean.TRUE);
					// ȡ�Ӱ����ʵ��, ��ǰ̨�����ڵ�λΪ���ʱ��ת��ΪСʱ������Ҫת������
					OverTimeTypeCopyVO typeVO = timeitemMap.get(vo.getPk_overtimetype());
					if(TimeItemCopyVO.TIMEITEMUNIT_DAY==typeVO.getTimeitemunit().intValue()){
						vo.setActhour(vo.getActhour().div(timeRule.getDaytohour2()));
					}
//					// ���ò���ʱ��
//					vo.setDiffhour(vo.getOvertimehour().sub(vo.getActhour()));
					//���¼Ӱ�Ǽǵ���
					returnVOs.add(vo);
				}
			}
			// ���޸ļӰ�ת���ݽ������ݺ͸��²����ᵽѭ���⣬�Ż�sql���� 2012-10-23
			NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).processBeforeRestOvertime(pk_org, hourMap, toRestYear, toRestMonth, true);
			//ҵ����־
			TaBusilogUtil.writeOvertimeReg2RestBusiLog(returnVOs.toArray(new OvertimeRegVO[0]));
			return getServiceTemplate().update(true, returnVOs.toArray(new OvertimeRegVO[0]));
	}

	@Override
	public OvertimeRegVO[] unOver2Rest(OvertimeRegVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos)) {
			return null;
		}
//		for(OvertimeRegVO vo:vos){
//			PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(), vo.getBegindate(), vo.getEnddate());
//		}
		String pk_org = vos[0].getPk_org();
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		Map<String, TimeZone> timeZoneMap = timeRule.getTimeZoneMap();
		Map<String, ShiftVO> shiftMap = ShiftServiceFacade.queryShiftVOMapByHROrg(pk_org);
		Map<String, OverTimeTypeCopyVO> timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryOvertimeCopyTypeMapByOrg(pk_org);
		IPeriodQueryService periodService = NCLocator.getInstance().lookup(IPeriodQueryService.class);
		IDateScope[] scopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		Map<UFLiteralDate, PeriodVO> periodVOMap = periodService.queryPeriodMapByDateScopes(pk_org, scopes);
		InSQLCreator isc = new InSQLCreator();
			String psndocInSQL = isc.getInSQL(vos, OvertimeCommonVO.PK_PSNDOC);
			Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = psndocService.queryDateJobOrgMapByPsndocInSQL(psndocInSQL, scopes);
			Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = calendarService.queryCalendarVOByPsnInSQL(pk_org, scopes, psndocInSQL);

			//����ѭ������
			Map<String, OvertimeRegVO[]> psnRegVOMap = CommonUtils.group2ArrayByField(OvertimeCommonVO.PK_PSNDOC, vos);
			List<OvertimeRegVO> returnVOs = new ArrayList<OvertimeRegVO>();
			// ����<year+month, <pk_psnorg, toRestHour>>
			Map<String, Map<String, UFDouble>> restMap = new HashMap<String, Map<String, UFDouble>>();
			for(String pk_psndoc:psnRegVOMap.keySet()){
				OvertimeRegVO[] regVOs = psnRegVOMap.get(pk_psndoc);
				Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(pk_psndoc);
				Map<UFLiteralDate, TimeZone> dateTimeZoneMap= CommonMethods.createDateTimeZoneMap(dateOrgMap, timeZoneMap);
				for(OvertimeRegVO vo:regVOs){
					if(!vo.getIstorest().booleanValue()) {
						throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0036")/*@res "����δת���ݵļ�¼��"*/);
					}
					// �Ӱ൥������ ���ڿ����ڼ��Ƿ��ѷ��
					Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(pk_psndoc);
					UFLiteralDate belongDate = BillProcessHelper.findBelongtoDate(vo, calendarMap, shiftMap, dateTimeZoneMap);
					PeriodVO period = periodVOMap.get(belongDate);
					if(period==null||period.isSeal()) {
						throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0034")/*@res "���ݹ��������ڿ����ڼ䲻���ڻ��ѷ�棡"*/);
					}
					// ������Աת����ʱ��
					String key = vo.getTorestyear() + (vo.getTorestmonth()==null?"":("-"+vo.getTorestmonth()));
					Map<String, UFDouble> hourMap = restMap.get(key)!=null?restMap.get(key):new HashMap<String, UFDouble>();
					UFDouble psnOrgHour = hourMap.get(vo.getPk_psnorg());
					hourMap.put(vo.getPk_psnorg(), psnOrgHour==null?vo.getToresthour():psnOrgHour.add(vo.getToresthour()));
					restMap.put(key, hourMap);
					// ���ݼ������޸ĺ��ٽ�ת����ʱ����0
					vo.setIstorest(UFBoolean.FALSE);
					vo.setTorestyear(null);
					vo.setTorestmonth(null);
					vo.setToresthour(UFDouble.ZERO_DBL);
					// ȡ�Ӱ����ʵ��, ��ǰ̨�����ڵ�λΪ���ʱ��ת��ΪСʱ������Ҫת������
					OverTimeTypeCopyVO typeVO = timeitemMap.get(vo.getPk_overtimetype());
					if(TimeItemCopyVO.TIMEITEMUNIT_DAY==typeVO.getTimeitemunit().intValue()){
						vo.setActhour(vo.getActhour().div(timeRule.getDaytohour2()));
					}
//					// ���ò���ʱ��
//					vo.setDiffhour(vo.getOvertimehour().sub(vo.getActhour()));
					//���¼Ӱ�Ǽǵ���
					returnVOs.add(vo);
				}
			}
			// ���޸ļӰ�ת���ݽ������ݺ͸��²����ᵽѭ����(����ת������ڼ䲻һ�£�����Ҫ���ڼ�ѭ��һ��)���Ż�sql���� 2012-10-23
			for(String key:restMap.keySet()){
				String[] yearAndMonths = key.split("-");
				String year = yearAndMonths[0];
				String month = ArrayUtils.getLength(yearAndMonths)>1?yearAndMonths[1]:null;
				NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).processBeforeRestOvertime(pk_org, restMap.get(key), year, month, false);
			}
			//ҵ����־
			TaBusilogUtil.writeOvertimeRegUn2RestBusiLog(returnVOs.toArray(new OvertimeRegVO[0]));
			return getServiceTemplate().update(true, returnVOs.toArray(new OvertimeRegVO[0]));
	}

	/**
	 * ȡ���������ڿ����ڼ���ת���ݵ��ݵ���ʱ��
	 * @param vo
	 * @param shiftMap
	 * @param timeZone
	 * @return
	 * @throws BusinessException
	 */
	private UFDouble getSumTorstHour(OvertimeCommonVO[] belongVOs) throws BusinessException {
		if(ArrayUtils.isEmpty(belongVOs)) {
			return UFDouble.ZERO_DBL;
		}
		UFDouble sumHour = UFDouble.ZERO_DBL;
		for(OvertimeCommonVO belongVO:belongVOs){
			if(!(belongVO instanceof OvertimeRegVO)) {
				continue;
			}
			OvertimeRegVO regVO = (OvertimeRegVO) belongVO;
			if(!regVO.getIstorest().booleanValue()) {
				continue;
			}
			sumHour = sumHour.add(regVO.getToresthour());
		}
		return sumHour;
	}
	
//	private UFDouble getSumTorstHour(OvertimeRegVO vo, Map<String, ShiftVO> shiftMap,
//			Map<UFLiteralDate, TimeZone> dateTimeZoneMap,
//			Map<UFLiteralDate, AggPsnCalendar> calendarMap,
//			Map<UFLiteralDate, PeriodVO> periodMap) throws BusinessException {
//		OvertimeCommonVO[] allVOs = new OvertimeDAO().getOvertimeVOInPeriod(vo, shiftMap, dateTimeZoneMap,calendarMap,periodMap);
//		if(ArrayUtils.isEmpty(allVOs))
//			return UFDouble.ZERO_DBL;
//		UFDouble sumHour = UFDouble.ZERO_DBL;
//		for(OvertimeCommonVO allVO:allVOs){
//			if(!(allVO instanceof OvertimeRegVO))
//				continue;
//			OvertimeRegVO regVO = (OvertimeRegVO) allVO;
//			if(!regVO.getIstorest().booleanValue())
//				continue;
//			sumHour = sumHour.add(regVO.getToresthour());
//		}
//		return sumHour;
//	}

	@Override
	public OvertimeRegVO[] undoCheck(OvertimeRegVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos)) {
			return null;
		}
		String pk_org = vos[0].getPk_org();
//		Map<String, TimeZone> timeZoneMap=NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZoneMap(pk_org);
		List<OvertimeRegVO> result = new ArrayList<OvertimeRegVO>();
		for(OvertimeRegVO vo:vos){
			if(!vo.getIscheck().booleanValue()) {
				continue;
			}
			if(vo.getIstorest().booleanValue()) {
				throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0037")
/*@res "������ת���ݵ��ݣ���������У�飡"*/);
			}
			vo.setOvertimebegintime(vo.getBegintimebeforecheck());
			vo.setOvertimeendtime(vo.getEndtimebeforecheck());
//			BillMethods.processBeginEndDate(vo, timeZoneMap);
			vo.setIscheck(UFBoolean.FALSE);
			result.add(vo);
		}
		if(CollectionUtils.isEmpty(result))
			return null;
		vos = result.toArray(new OvertimeRegVO[0]);
		BillMethods.processBeginEndDatePkJobOrgTimeZone(vos);
		BillProcessHelperAtServer.calOvertimeLength(pk_org, vos);
		//ҵ����־
		TaBusilogUtil.writeOvertimeRegUnCheckBusiLog(vos);
		return updateArrayData(vos);
	}

	@Override
	public void deleteArrayData(OvertimeRegVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return;
//		for(OvertimeRegVO vo:vos){
//			PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(), vo.getBegindate(), vo.getEnddate());
//		}
		IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(vos);
		PeriodServiceFacade.checkMonth(vos[0].getPk_org(),StringPiecer.getStrArrayDistinct(vos, OvertimeRegVO.PK_PSNDOC), maxDateScope.getBegindate(), maxDateScope.getEnddate());
		
		List<OvertimeRegVO> realDelList = new ArrayList<OvertimeRegVO>();
		for(OvertimeRegVO vo:vos) {
			if(vo == null || ICommonConst.BILL_SOURCE_REG != vo.getBillsource().intValue() || vo.getIstorest().booleanValue())
				continue;
			realDelList.add(vo);
		}
		if(CollectionUtils.isEmpty(realDelList))
			return;
		vos = realDelList.toArray(new OvertimeRegVO[0]);
		DefaultValidationService vService = new DefaultValidationService();
        vService.addValidator(new OvertimeRegDeleteValidator());
		vService.validate(vos);
		//ҵ����־
		TaBusilogUtil.writeOvertimeRegDeleteBusiLog(vos);
		getServiceTemplate().delete(vos);
	}

	@Override
	public void deleteData(OvertimeRegVO vo) throws BusinessException {
		if(vo==null)
			return;
		deleteArrayData(new OvertimeRegVO[]{vo});
	}

	@Override
	public OvertimeRegVO[] insertArrayData(OvertimeRegVO[] vos) throws BusinessException {
		return insertData(vos, true);
	}
	
	@Override
	public OvertimeRegVO[] insertData(OvertimeRegVO[] vos,boolean needCalAndValidate) throws BusinessException {
		if(ArrayUtils.isEmpty(vos)) {
			return null;
		}
		BillMethods.processBeginEndDatePkJobOrgTimeZone(vos);
		if(needCalAndValidate){
			DefaultValidationService vService = new DefaultValidationService();
	        vService.addValidator(new SaveOvertimeRegValidator());
	        vService.addValidator(new OvertimeRegValidator());
			vService.validate(vos);
			String pk_org = vos[0].getPk_org();
//			check(pk_org, vos);//�˴���У�� �ڱ���ǰ��ʾ��У�����Ѿ�У���һ���ˣ��˴�����У�飨Ӱ��Ч�ʣ�
			
			//�Ƿ��ҵ��Ԫ����У�鵥��У��
			BillValidatorAtServer.checkCrossBU(pk_org, vos);
			
		}else{//���������Ѿ����ٿ��ڵ�����Χ���ˣ���ҪУ��
			DefaultValidationService vService = new DefaultValidationService();
			vService.addValidator(new OvertimeRegValidator());
			vService.validate(vos);
		}
		//add by fangbing �Ӱ�ǼǱ���ʱ������Ҫ�޸ġ��Ƿ���ҪУ���ʶ�����ٴ˽����ݴ��£���У������ٻ�ԭ��20141226
		Map<String,UFBoolean> checkMap = new HashMap<String, UFBoolean>();
		for(OvertimeRegVO vo:vos){
			checkMap.put(vo.getPk_psndoc() + vo.getOvertimebegintime(), vo.getIsneedcheck());
		}
		new OvertimeDAO().setAlreadyHourAndCheckFlag(false,vos);
		for(OvertimeRegVO vo:vos){
			// ���ò���ʱ��
			vo.setDiffhour(vo.getOvertimehour().sub(vo.getActhour()));
			//add by fangbing ��֮ǰ�ݴ�ı�ʶ����+��ʼʱ�仹ԭ
			vo.setIsneedcheck(checkMap.get(vo.getPk_psndoc()+vo.getOvertimebegintime()));
			
			//MOD ��������ʱ��Ϊ��Ȼʱ�� by James
			vo.setApprove_time(new UFDateTime());
		}
		OvertimeRegVO[] retvos = getServiceTemplate().insert(vos);
		//ҵ����־
		TaBusilogUtil.writeOvertimeRegAddBusiLog(retvos);
		return retvos;
	}
	
//	@Deprecated
//	public OvertimeRegVO[] insertData1(OvertimeRegVO[] vos,boolean needCalAndValidate) throws BusinessException {
//		if(ArrayUtils.isEmpty(vos))
//			return null;
//		nc.impl.ta.timebill.CommonMethods.processBeginEndDatePkJobOrgTimeZone(vos);
//		if(needCalAndValidate){
//			DefaultValidationService vService = new DefaultValidationService();
//	        vService.addValidator(new SaveOvertimeRegValidator());
//	        vService.addValidator(new OvertimeRegValidator());
//			vService.validate(vos);
//			String pk_org = vos[0].getPk_org();
//			check(pk_org, vos);
//			
//			//�Ƿ��ҵ��Ԫ����У�鵥��У��
//			BillValidatorAtServer.checkCrossBU(pk_org, vos);
//			
//		}else{//���������Ѿ����ٿ��ڵ�����Χ���ˣ���ҪУ��
//			DefaultValidationService vService = new DefaultValidationService();
//			vService.addValidator(new OvertimeRegValidator());
//			vService.validate(vos);
//		}
////		List<OvertimeRegVO> volist = new ArrayList<OvertimeRegVO>();
//		OvertimeApplyQueryMaintainImpl impl = new OvertimeApplyQueryMaintainImpl();
//		OvertimeDAO dao = new OvertimeDAO();
//		String pk_org = vos[0].getPk_org();
//		Map<String, AggShiftVO> aggShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
//		Map<String, ShiftVO> shiftMap = CommonUtils.transferAggMap2HeadMap(aggShiftMap);
//		TimeRuleVO timeruleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
//		Map<String, OverTimeTypeCopyVO> timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryOvertimeCopyTypeMapByOrg(pk_org);
//		IDateScope[] mergedScopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
//		IPeriodQueryService periodService = NCLocator.getInstance().lookup(IPeriodQueryService.class);
//		Map<UFLiteralDate, PeriodVO> periodMap = periodService.queryPeriodMapByDateScopes(pk_org, mergedScopes);
//		InSQLCreator isc = null;
//		try{
//			isc = new InSQLCreator();
//			String psndocInSQL = isc.getInSQL(vos, OvertimebVO.PK_PSNDOC);
//			ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
//			Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = psndocService.queryDateJobOrgMapByPsndocInSQL(psndocInSQL, mergedScopes);
//			IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
//			Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnAggCalendarMap = calendarService.queryCalendarVOByPsnInSQL(pk_org, mergedScopes, psndocInSQL);
//
//			for(OvertimeRegVO vo:vos){
//				//���ѡ�����Ƿ�����У�鵫����������У��Ĺ���Ҫǿ��ת��Ϊ������У��
//				if(vo.getIsneedcheck().booleanValue()&&!impl.isCanCheck(vo))
//					vo.setIsneedcheck(UFBoolean.FALSE);
//				// ���ò���ʱ��
//				vo.setDiffhour(vo.getOvertimehour().sub(vo.getActhour()));
//				String pk_psndoc = vo.getPk_psndoc();
//				Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(pk_psndoc);
//				Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnAggCalendarMap)?null:psnAggCalendarMap.get(pk_psndoc);
//				Map<UFLiteralDate, TimeZone> dateTimeZoneMap = CommonMethods.createDateTimeZoneMap(dateOrgMap,timeruleVO.getTimeZoneMap());
//				dao.setAlreadyHour(vo, shiftMap, dateTimeZoneMap, calendarMap, timeitemMap, periodMap, timeruleVO);
//				//�Ż���������
////				volist.add(getServiceTemplate().insert(vo));
//			}
//			getServiceTemplate().batchInsertDirect(vos);
////			return volist.toArray(new OvertimeRegVO[0]);
//			return vos;
//		}
//		finally{
//			if(isc!=null)
//				isc.clear();
//		}
//	}

	@Override
	public OvertimeRegVO insertData(OvertimeRegVO vo) throws BusinessException {
		return insertData(new OvertimeRegVO[]{vo}, true)[0];
	}

	@Override
	public OvertimeRegVO[] updateArrayData(OvertimeRegVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos)) {
			return null;
		}
//		for(OvertimeRegVO vo:vos){
//			PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(), vo.getBegindate(), vo.getEnddate());
//		}
		IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(vos);
		PeriodServiceFacade.checkMonth(vos[0].getPk_org(),StringPiecer.getStrArrayDistinct(vos, OvertimeRegVO.PK_PSNDOC), maxDateScope.getBegindate(), maxDateScope.getEnddate());
		
		OvertimeRegVO[] oldvos = getServiceTemplate().queryByPks(OvertimeRegVO.class,StringPiecer.getStrArray(vos,OvertimeRegVO.PK_OVERTIMEREG));
		BillMethods.processBeginEndDatePkJobOrgTimeZone(vos);
		//У�����
        DefaultValidationService vService = new DefaultValidationService();
        vService.addValidator(new SaveOvertimeRegValidator());
        vService.addValidator(new OvertimeRegValidator());
		vService.validate(vos);
		
		// ����֯����
		Map<String, OvertimeRegVO[]> vosMap = CommonUtils.group2ArrayByField(OvertimeCommonVO.PK_ORG, vos);
		for(String pk_org:vosMap.keySet()){
			OvertimeRegVO[] pk_vos = vosMap.get(pk_org);
			//���ݳ�ͻУ��
			check(pk_org, pk_vos);
			List<OvertimeRegVO> regList = new ArrayList<OvertimeRegVO>();
			for(OvertimeRegVO vo:pk_vos){
				// У����п��ܿ�ʼʱ��ͽ���ʱ��һ������ʱ��������֯У��
				if(!vo.getOvertimebegintime().equals(vo.getOvertimeendtime())){
					regList.add(vo);
				}
				// ���ò���ʱ��
				vo.setDiffhour(vo.getOvertimehour().sub(vo.getActhour()));
			}
			//�Ƿ��ҵ��ԪУ��
			if(!regList.isEmpty()) {
				BillValidatorAtServer.checkCrossBU(pk_org, regList.toArray(new OvertimeRegVO[0]));
			}
		}
		
		OvertimeRegVO[] retvos = getServiceTemplate().update(true, vos);
		//ҵ����־
		TaBusilogUtil.writeOvertimeRegEditBusiLog(retvos,oldvos);
		return retvos;
	}

	@Override
	public OvertimeRegVO updateData(OvertimeRegVO vo) throws BusinessException {
		PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(), vo.getBegindate(), vo.getEnddate());
		OvertimeRegVO oldvo = getServiceTemplate().queryByPk(OvertimeRegVO.class,vo.getPk_overtimereg());
		BillMethods.processBeginEndDatePkJobOrgTimeZone(new OvertimeRegVO[]{vo});
		//У�����
        DefaultValidationService vService = new DefaultValidationService();
        vService.addValidator(new SaveOvertimeRegValidator());
        vService.addValidator(new OvertimeRegValidator());
		vService.validate(vo);
		//���ݳ�ͻУ��
		check(vo);
		
		//�Ƿ��ҵ��Ԫ����У��
		OvertimeRegVO[] bills = new OvertimeRegVO[]{vo};
		String pk_org = vo.getPk_org();
		// У����п��ܿ�ʼʱ��ͽ���ʱ��һ������ʱ��������֯У��
		if(!vo.getOvertimebegintime().equals(vo.getOvertimeendtime())) {
			BillValidatorAtServer.checkCrossBU(pk_org, bills);
		}
		// ���ò���ʱ��
		vo.setDiffhour(vo.getOvertimehour().sub(vo.getActhour()));
		//ҵ����־
		TaBusilogUtil.writeOvertimeRegEditBusiLog(new OvertimeRegVO[]{vo},new OvertimeRegVO[]{oldvo});
		return getServiceTemplate().update(true, vo)[0];
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public OvertimeRegVO[] queryByCond(LoginContext context, FromWhereSQL fromWhereSQL, Object etraConds)
			throws BusinessException {
		//����Ȩ��sql��ʹ�ù�����¼��Դʵ���µġ�ʱ�����ͨ�����á�Ԫ���ݲ���
		fromWhereSQL = PubPermissionUtils.addPubQueryPermission2FromWhereSQL
		(fromWhereSQL, OvertimeRegVO.getDefaultTableName(), OvertimeRegVO.getDefaultTableName(), OvertimeCommonVO.PK_PSNJOB);
		//ȡ������
		String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, OvertimeRegVO.getDefaultTableName());
		//ƴ��֯��sql
		String etraCondsNext = alias + "." + OvertimeCommonVO.PK_ORG +
					(context.getPk_org()==null?" is null": " = '" + context.getPk_org() + "' ");
		//���ڼ��ѯ
		String periodSql = TaNormalQueryUtils.getPeriodSql(context, OvertimeRegVO.class, alias,(TaBillRegQueryParams)etraConds);
		if(periodSql != null) {
			etraCondsNext += " and " + periodSql;
		}
		String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, OvertimeRegVO.getDefaultTableName(), etraCondsNext, null);
		Collection<OvertimeRegVO> aggvos = (Collection<OvertimeRegVO>)new BaseDAO().executeQuery(sql, new BeanListProcessor(
				OvertimeRegVO.class));
		if(CollectionUtils.isEmpty(aggvos)) {
			return null;
		}
		return aggvos.toArray(new OvertimeRegVO[0]);
	}

	@Override
	public Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> check(
			OvertimeRegVO vo) throws BusinessException{
		NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).checkTBMPsndocDate(vo.getPk_org(),new String[]{vo.getPk_psndoc()}, new OvertimeRegVO[]{vo},true);
		BillMethods.processBeginEndDatePkJobOrgTimeZone(new OvertimeRegVO[]{vo});
		return BillValidatorAtServer.checkOvertime(vo);
	}

	@Override
	public Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> check(
			String pk_org, OvertimeRegVO[] vos) throws BusinessException{
		BillMethods.processBeginEndDatePkJobOrgTimeZone(vos);
		return BillValidatorAtServer.checkOvertime(pk_org, vos);
	}

	@Override
	public OvertimeRegVO[] doBeforeCheck(OvertimeRegVO[] vos)
			throws BusinessException {
		if(ArrayUtils.isEmpty(vos)) {
			return null;
		}
		String pk_org = vos[0].getPk_org();
		IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(vos);
		PeriodVO[] periods = NCLocator.getInstance().lookup(IPeriodQueryService.class).queryPeriodsByDateScope(pk_org, maxDateScope.getBegindate(), maxDateScope.getEnddate());
		List<OvertimeRegVO> returnList = new ArrayList<OvertimeRegVO>();
		for(OvertimeRegVO vo:vos){
			// ��ת���ݵ���Ҫ��ʾ
			if(vo.getIstorest().booleanValue()){
				returnList.add(vo);
				continue;
			}
			// ����ڼ��ѷ��򲻹����κ��ڼ䣬��Ҫ��ʾ
			if(BillMethods.isDateScopeInPeriod(periods, vo))
				continue;
			returnList.add(vo);
		}
		return CollectionUtils.isEmpty(returnList)? null:returnList.toArray(new OvertimeRegVO[0]);
	}

	@Override
	public GeneralVO over2RestFirst(OvertimeRegVO[] vos, String toRestYear,
			String toRestMonth) throws BusinessException {
		if(ArrayUtils.isEmpty(vos)) {
			return null;
		}
//		for(OvertimeRegVO vo:vos){ѭ��У��Ч��̫��
//			PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(), vo.getBegindate(), vo.getEnddate());
//		}
		String pk_org = vos[0].getPk_org();
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		//�����ϸ��������Ҫ��ʾ��Ϣ
		if(timeRule.getIstorestctrlot()!=null&&timeRule.getIstorestctrlot().booleanValue()) {
			OvertimeRegVO[] retvos = over2Rest(vos, toRestYear, toRestMonth);
			GeneralVO retgvo = new GeneralVO();
			retgvo.setAttributeValue("overtimeRegVO", retvos);
			return retgvo;
		}
		//�������ʱ������Ա��¼
		List<String> overPsn = new ArrayList<String>();
		
		Map<String, TimeZone> timeZoneMap = timeRule.getTimeZoneMap();
		Map<String, ShiftVO> shiftMap = ShiftServiceFacade.queryShiftVOMapByHROrg(pk_org);
		// ȡ�����ڼ��ڿ�ת���ݵ����ʱ��
//		UFDouble maxHour = SysInitQuery.getParaDbl(pk_org, OvertimeConst.OVERTIMETOREST_PARAM);
		UFDouble maxHour = timeRule.getTorestlongest();
		if(maxHour==null) {
			maxHour = UFDouble.ZERO_DBL;
		}
		Map<String, OverTimeTypeCopyVO> timeitemMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryOvertimeCopyTypeMapByOrg(pk_org);
		IPeriodQueryService periodService = NCLocator.getInstance().lookup(IPeriodQueryService.class);
		IDateScope[] scopes = DateScopeUtils.mergeAndExtendScopes(vos, 3);
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		Map<UFLiteralDate, PeriodVO> periodVOMap = periodService.queryPeriodMapByDateScopes(pk_org, scopes);
		InSQLCreator isc = new InSQLCreator();
		String psndocInSQL = isc.getInSQL(vos, OvertimeCommonVO.PK_PSNDOC);
		Map<String, Map<UFLiteralDate, String>> psnDateOrgMap = psndocService.queryDateJobOrgMapByPsndocInSQL(psndocInSQL, scopes);
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalendarMap = calendarService.queryCalendarVOByPsnInSQL(pk_org, scopes, psndocInSQL);
		Map<OvertimeCommonVO, OvertimeCommonVO[]> overtimeMap = new OvertimeDAO().getOvertimeVOInPeriod(vos, timeZoneMap, shiftMap, psnDateOrgMap, psnCalendarMap, periodVOMap);
		//����ѭ������
		Map<String, OvertimeRegVO[]> psnRegVOMap = CommonUtils.group2ArrayByField(OvertimeCommonVO.PK_PSNDOC, vos);
		List<OvertimeRegVO> returnVOs = new ArrayList<OvertimeRegVO>();
		Map<String, UFDouble> hourMap = new HashMap<String, UFDouble>();
		for(String pk_psndoc:psnRegVOMap.keySet()){
			OvertimeRegVO[] regVOs = psnRegVOMap.get(pk_psndoc);
			Map<UFLiteralDate, String> dateOrgMap = MapUtils.isEmpty(psnDateOrgMap)?null:psnDateOrgMap.get(pk_psndoc);
			Map<UFLiteralDate, TimeZone> dateTimeZoneMap= CommonMethods.createDateTimeZoneMap(dateOrgMap, timeZoneMap);
			Map<String, UFDouble> periodMap = new HashMap<String, UFDouble>();
			for(OvertimeRegVO vo:regVOs){
				if(vo.getIstorest().booleanValue()) {
					throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0033")/*@res "������ת���ݵļ�¼��"*/);
				}
				// �Ӱ൥������ ���ڿ����ڼ��Ƿ��ѷ��
				Map<UFLiteralDate, AggPsnCalendar> calendarMap = MapUtils.isEmpty(psnCalendarMap)?null:psnCalendarMap.get(pk_psndoc);
				UFLiteralDate belongDate = BillProcessHelper.findBelongtoDate(vo, calendarMap, shiftMap, dateTimeZoneMap);
				PeriodVO period = periodVOMap.get(belongDate);
				if(period==null||period.isSeal()) {
					throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0034")/*@res "���ݹ��������ڿ����ڼ䲻���ڻ��ѷ�棡"*/);
				}
				UFDouble sumHour = getSumTorstHour(overtimeMap.get(vo));
				String periodKey = period.getTimeyear()+"-"+period.getTimemonth();
				// ת����ʱ��
				UFDouble torestHour = vo.getToresthour();
				// ȡ��ת����ʱ��map�б����ʱ����Ϣ
				UFDouble alreadyHour = periodMap.get(periodKey)==null?UFDouble.ZERO_DBL:periodMap.get(periodKey);
				alreadyHour = alreadyHour.add(torestHour);
				if(maxHour.doubleValue()>=0&&maxHour.compareTo(sumHour.add(alreadyHour))<0) {
						overPsn.add(vo.getPk_psndoc());
//						throw new BusinessException(ResHelper.getString("6017overtime","06017overtime0035"/*@res "ת����ʱ�����������ڼ���ת���ݵ����ʱ���� {0}Сʱ��"*/,  maxHour.toString()));
					}
				periodMap.put(periodKey, alreadyHour);
				// ������Աת����ʱ��
				UFDouble psnOrgHour = hourMap.get(vo.getPk_psnorg());
				hourMap.put(vo.getPk_psnorg(), psnOrgHour==null?torestHour:psnOrgHour.add(torestHour));
				// �޸ĵǼ���Ϣ
				vo.setTorestyear(toRestYear);
				vo.setTorestmonth(toRestMonth);
				vo.setIstorest(UFBoolean.TRUE);
				// ȡ�Ӱ����ʵ��, ��ǰ̨�����ڵ�λΪ���ʱ��ת��ΪСʱ������Ҫת������
				OverTimeTypeCopyVO typeVO = timeitemMap.get(vo.getPk_overtimetype());
				if(TimeItemCopyVO.TIMEITEMUNIT_DAY==typeVO.getTimeitemunit().intValue()){
					vo.setActhour(vo.getActhour().div(timeRule.getDaytohour2()));
				}
//				// ���ò���ʱ��
//				vo.setDiffhour(vo.getOvertimehour().sub(vo.getActhour()));
				//���¼Ӱ�Ǽǵ���
				returnVOs.add(vo);
			}
		}
		
		OvertimeRegVO[] retvos = returnVOs.toArray(new OvertimeRegVO[0]);
		GeneralVO retgvo = new GeneralVO();
		//����Ҫ��ʾ��Ϣֱ�Ӹ������ݿ�
		if(overPsn.isEmpty()){
			// ���޸ļӰ�ת���ݽ������ݺ͸��²����ᵽѭ���⣬�Ż�sql���� 2012-10-23
			NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).processBeforeRestOvertime(pk_org, hourMap, toRestYear, toRestMonth, true);
			retvos = getServiceTemplate().update(true, retvos);
			retgvo.setAttributeValue("overtimeRegVO", retvos);
			//ҵ����־
			TaBusilogUtil.writeOvertimeReg2RestBusiLog(retvos);
			return retgvo;
		}else{//��ʾ�Ӱ�ת���ݳ�ʱ
			String[] pk_psndocs = overPsn.toArray(new String[0]);
			String psnNames = CommonUtils.getPsnNames(pk_psndocs);
			String warningMsg = ResHelper.getString("6017overtime","06017overtime0048"
					/*@res "������Ա{0}��ת����ʱ�����������ڼ��ڹ涨�����ʱ��";*/,psnNames);
			retgvo.setAttributeValue("warningMsg", warningMsg);
			retgvo.setAttributeValue("leaveHour", hourMap);
			retgvo.setAttributeValue("overtimeRegVO", retvos);
			return retgvo;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public OvertimeRegVO[] over2RestSecond( GeneralVO gvo, String toRestYear,
			String toRestMonth) throws BusinessException {
		if( gvo.getAttributeValue("overtimeRegVO") == null) {
			return null;
		}
		OvertimeRegVO[] vos = (OvertimeRegVO[]) gvo.getAttributeValue("overtimeRegVO");
		if(ArrayUtils.isEmpty(vos)) {
			return null;
		}
		String pk_org = vos[0].getPk_org();
		Map<String, UFDouble> hourMap = (Map<String, UFDouble>) gvo.getAttributeValue("leaveHour");
		NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).processBeforeRestOvertime(pk_org, hourMap, toRestYear, toRestMonth, true);
		//ҵ����־
		TaBusilogUtil.writeOvertimeReg2RestBusiLog(vos);
		return getServiceTemplate().update(true, vos);
	}

	@Override
	public String[] queryPKsByFromWhereSQL(LoginContext context,
			FromWhereSQL fromWhereSQL, Object etraConds)throws BusinessException {
		String cond=getSQLCondByFromWhereSQL(context, fromWhereSQL, etraConds);
		String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, OvertimeRegVO.getDefaultTableName());
		List<String> result = excuteQueryPksBycond(cond, alias);
		return CollectionUtils.isEmpty(result) ? null : (String[])result.toArray(new String[0]);
	}
	
	public String getSQLCondByFromWhereSQL(LoginContext context,FromWhereSQL fromWhereSQL,Object etraConds) throws BusinessException{
		//����Ȩ��sql��ʹ�ù�����¼��Դʵ���µġ�ʱ�����ͨ�����á�Ԫ���ݲ���
		fromWhereSQL = PubPermissionUtils.addPubQueryPermission2FromWhereSQL
		(fromWhereSQL, OvertimeRegVO.getDefaultTableName(), OvertimeRegVO.getDefaultTableName(), OvertimeCommonVO.PK_PSNJOB);
		//ȡ������
		String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, OvertimeRegVO.getDefaultTableName());
		//ƴ��֯��sql
		String etraCondsNext = alias + "." + OvertimeCommonVO.PK_ORG +
					(context.getPk_org()==null?" is null": " = '" + context.getPk_org() + "' ");
		//���ڼ��ѯ
		String periodSql = TaNormalQueryUtils.getPeriodSql(context, OvertimeRegVO.class, alias,(TaBillRegQueryParams)etraConds);
		if(periodSql != null) {
			etraCondsNext += " and " + periodSql;
		}
		String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, OvertimeRegVO.getDefaultTableName(),  
				new String[]{OvertimeRegVO.PK_OVERTIMEREG}, null, null, null, null);
		etraCondsNext+=" and "+OvertimeRegVO.PK_OVERTIMEREG+" in ( "+sql+" ) ";
		return etraCondsNext;
	}
	
	private List<String> excuteQueryPksBycond(String cond, String alias)throws BusinessException{
		String sql = "select "+(StringUtils.isEmpty(alias)?"":alias+".")+OvertimeRegVO.PK_OVERTIMEREG+ 
				" from " + OvertimeRegVO.getDefaultTableName() + " " + (StringUtils.isEmpty(alias) ? "" : alias);
		if (!StringUtils.isEmpty(cond))
			sql = sql + " where " + cond;
		List<String> result = (List)new BaseDAO().executeQuery(sql, new ColumnListProcessor());
		return result;
	}

	@Override
	public OvertimeRegVO[] queryByPks(String[] pks) throws BusinessException {
		if(ArrayUtils.isEmpty(pks))
			return null;
		return getServiceTemplate().queryByPks(OvertimeRegVO.class, pks);
	}

	@Override
	public ImportVO[] importdata(ImportVO[] vos) throws BusinessException {
		//��һ��������Excel�е�����
		getRefInfo(vos);
		validateTimeScope(vos);
		//���϶��ǶԵ�����Ϣ�Ĵ���У�飬���д��󷵻�ǰ����ʾ�û�
		if(isFail(vos)) 
			return vos;
		//�ڶ�������ȡת����ļӰ�Ǽ�VO
		OvertimeRegVO[] regvos = turnOvertimeVO(vos);
		if(isFail(vos)) 
			return vos;
		checdatabasektimescope(regvos, vos);
		if(isFail(vos)) 
			return vos;
		//�����������浽����
		OvertimeRegVO[] returnregvos= batchSave(regvos);
		//���ڵ�һ��ǰ̨Excelvo��
		vos[0].setReturnTimeRegVOs(returnregvos);
		return vos;
	}
	/**
	 * У�����ݿ����Ƿ����ʱ���ͻ����
	 * @param regvos
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	private boolean checdatabasektimescope(OvertimeRegVO[] regvos,ImportVO[] vos) throws BusinessException{
		for(int i = 0; i < regvos.length; i++){
			try{
				Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> result = BillValidatorAtServer.checkOvertime(regvos[i]);
			}catch(BusinessException e){
				vos[i].setErrmsg(e.getMessage().replaceAll("\r\n", ""));
			}
		}
		return false;
	}
	private boolean isFail(ImportVO[] vos){
		for(ImportVO vo : vos){
			if(vo.isFail()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �������浼�봦����ļӰ�Ǽ�����
	 * @param regvos
	 * @return
	 * @throws BusinessException 
	 */
	private OvertimeRegVO[] batchSave(OvertimeRegVO[] regvos) throws BusinessException{
		for(OvertimeRegVO regvo:regvos){
			regvo.setStatus(VOStatus.NEW);
			regvo.setIscheck(UFBoolean.FALSE);
//			regvo.setIstorest(UFBoolean.FALSE);
			regvo.setToresthour(UFDouble.ZERO_DBL);
			regvo.setBillsource(ICommonConst.BILL_SOURCE_REG);
			//MOD reset isneedcheck  James
			regvo.setIsneedcheck(UFBoolean.FALSE);
		}
		regvos = insertArrayData(regvos);
		return regvos;
	}
	
	/**
	 * ת��ʵ�ʵļӰ�Ǽ�vo
	 * @param vos
	 * @return
	 * @throws BusinessException 
	 */
	private OvertimeRegVO[] turnOvertimeVO(ImportVO[] ivos) throws BusinessException{
		OvertimeRegVO []tvos = new OvertimeRegVO[ivos.length];
		Set<String> pk_psndocSet = new HashSet<String>();
		for(int i =0; i < ivos.length;i++){
			pk_psndocSet.add(ivos[i].getPk_psndoc());
			tvos[i] = new OvertimeRegVO();
			tvos[i].setOvertimebegintime(ivos[i].getOvertimebegintime());
			tvos[i].setScope_start_datetime(ivos[i].getOvertimebegintime());
			tvos[i].setOvertimeendtime(ivos[i].getOvertimeendtime());
			tvos[i].setScope_end_datetime(ivos[i].getOvertimeendtime());
			tvos[i].setPk_psndoc(ivos[i].getPk_psndoc());
			tvos[i].setBillsource(2);
			tvos[i].setPk_overtimetype(ivos[i].getPk_overtimetype());
			tvos[i].setPk_overtimetypecopy(ivos[i].getPk_overtimetypecopy());
			tvos[i].setPk_timeitem(ivos[i].getPk_overtimetype());
			tvos[i].setIstorest(ivos[i].getIstorest());
			tvos[i].setDeduct(0);
			//MOD ����ר������ James
			tvos[i].setProjectcode(ivos[i].getPk_project());
//			vo.setTranstypeid(getBillTypeRefPk());
//			vo.setTranstype(getRefBillType().getRefCode());
//			vo.setIsneedcheck(UFBoolean.valueOf(topPanel.isNeedCheck()));
			
		}
		
		String pk_org = ivos[0].getPk_org();
		FromWhereSQL fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocSet.toArray(new String[]{}));
		Map<String, TimeZone> timeZoneMap = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryTimeZoneMap(pk_org);
		//����Ȩ�޴���
		fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermissionSQL2QuerySQL(fromWhereSQL);
		CommonMethods.processBeginEndDate(tvos, timeZoneMap);
		IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(tvos);
		//��ѯ���ڵ���
		Map<String, List<TBMPsndocVO>> psndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryTBMPsndocMapByCondition(pk_org, fromWhereSQL, maxDateScope.getBegindate(), maxDateScope.getEnddate(), false);
		if(MapUtils.isEmpty(psndocMap)){
			for(ImportVO ivo : ivos){
				ivo.setErrmsg("δ�ҵ�<"+ivo.getPsnzhname()+"><"+ivo.getVoerdate()+">�Ŀ��ڵ���");
			}
			return null;
		}
		for(int i = 0; i < tvos.length; i++){
			String pk_psndoc = tvos[i].getPk_psndoc();
			// ��ͨ�����������ҿ��ڵ���
			TBMPsndocVO psndocVO = TBMPsndocVO.findIntersectionVO(psndocMap.get(pk_psndoc), tvos[i].getEnddate().toStdString());
			if(psndocVO == null){
				// �����������û�ҵ������Ŀ��ڵ�������ͨ����ʼ��������һ��
				psndocVO = TBMPsndocVO.findIntersectionVO(psndocMap.get(pk_psndoc), tvos[i].getBegindate().toStdString());
				if(psndocVO == null){
					ivos[i].setErrmsg("δ�ҵ�<"+ivos[i].getPsnzhname()+"><"+ivos[i].getVoerdate()+">�Ŀ��ڵ���");
					continue;
				}
			}
			tvos[i].setPk_psndoc(psndocVO.getPk_psndoc());
			tvos[i].setPk_psnjob(psndocVO.getPk_psnjob());
			tvos[i].setPk_psnorg(psndocVO.getPk_psnorg());
			tvos[i].setPk_org_v(psndocVO.getPk_org_v());
			tvos[i].setPk_dept_v(psndocVO.getPk_dept_v());
			tvos[i].setPk_group(psndocVO.getPk_group());
			tvos[i].setPk_org(pk_org);
		}	
		IOvertimeRegisterInfoDisplayer regInfoDisplayer = NCLocator.getInstance().lookup(IOvertimeRegisterInfoDisplayer.class);
		for(int i = 0; i < tvos.length;i++){
			tvos[i] = (OvertimeRegVO) regInfoDisplayer.calculate(tvos[i], ivos[i].getClientTimeZone());//����ʱ��
			if(tvos[i].getOvertimehour() == null || tvos[i].getOvertimehour().compareTo(UFDouble.ZERO_DBL)==0 ){
				ivos[i].setErrmsg("<"+ivos[i].getPsnzhname()+"><"+ivos[i].getVoerdate()+" "
			+ivos[i].getVoertimebegin()+"~"+ivos[i].getVoertimeend()+">����ʱ��Ϊ0��");
			}
		}
		checkCalendarCompleteForOvertime(tvos, ivos);
		return tvos;
	}
	/**
	 * У��Ӱ൥�Ĺ��������Ƿ�����
	 * ����BilValidatorAtServ.checkCalendarCompleteForOvertime
	 * @param bills
	 * @param ivos
	 * @return
	 * @throws BusinessException 
	 */
	private boolean checkCalendarCompleteForOvertime(OvertimeRegVO []bills,ImportVO[] ivos) throws BusinessException{
		if (bills == null || bills.length == 0)
			return false;
		String pk_org = ivos[0].getPk_org();
		Map<OvertimeCommonVO, Map<UFLiteralDate, AggPsnCalendar>> calendarMap = new HashMap<OvertimeCommonVO, Map<UFLiteralDate, AggPsnCalendar>>();
		//������е��ݿ����漰���Ĺ�������������ÿ�ŵ��ݵ�ʱ�䶼���ܲ�һ�������ÿ�ŵ��ݵ�������һ�β�ѯ�������ܰ���һ��ͳһ��ʱ���ѯ
		IPsnCalendarQueryService calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);

		TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		Map<String, TimeZone> timeZoneMap = timeRuleVO.getTimeZoneMap();
		Map<String, AggShiftVO> allShiftMap = ShiftServiceFacade.queryShiftAggVOMapByHROrg(pk_org);
		Map<ITimeScopeBillBodyVO, Map<UFLiteralDate, AggPsnCalendar>> billCalendarVOsMap = calendarService.queryCalendarVOsByPsnsDates(null, bills,timeZoneMap, allShiftMap);
		for(ITimeScopeBillBodyVO bill:billCalendarVOsMap.keySet()){
			calendarMap.put((OvertimeCommonVO) bill, billCalendarVOsMap.get(bill));
		}
		Map<String, ShiftVO> shiftMap = new HashMap<String, ShiftVO>();
		for(String pk_shift:allShiftMap.keySet()){
			shiftMap.put(pk_shift, allShiftMap.get(pk_shift).getShiftVO());
		}
		checkCalendarCompleteForOvertime(ivos,bills, calendarMap, shiftMap,
				CommonMethods.createPsnDateTimeZoneMap(queryPsnDateOrgMap(bills),timeZoneMap));
		return false;
		
	}
	
	private void checkCalendarCompleteForOvertime(ImportVO[] ivos,
			OvertimeCommonVO[] bills,
			Map<OvertimeCommonVO,Map<UFLiteralDate, AggPsnCalendar>> calendarMap,
			Map<String, ShiftVO> shiftMap,
			Map<String,Map<UFLiteralDate, TimeZone>> psnDateTimeZoneMap) throws BusinessException{
		if(bills==null||bills.length==0)
			return;
		for(int i = 0; i < bills.length ; i++){
			OvertimeCommonVO bill = bills[i];
			Map<UFLiteralDate, AggPsnCalendar> psnCalendarMap = calendarMap.get(bill);
			//��������Ϊ�գ���϶��Ҳ��������գ���������������
			if(psnCalendarMap==null||psnCalendarMap.size()==0){
				ivos[i].setErrmsg("<"+ivos[i].getPsnzhname()+"><"+ivos[i].getVoerdate()+">��������������");
				continue;
			}
			UFLiteralDate belongToDate = BillProcessHelper.findBelongtoDate(bill, psnCalendarMap, shiftMap,
					psnDateTimeZoneMap.get(bill.getPk_psndoc()));
			//û���ҵ������գ���������������
			if(belongToDate==null || psnCalendarMap.get(belongToDate)==null){
				ivos[i].setErrmsg("<"+ivos[i].getPsnzhname()+"><"+ivos[i].getVoerdate()+">��������������");
				continue;
			}
		}
	}
	private Map<String, Map<UFLiteralDate, String>> queryPsnDateOrgMap(IDateScopeBillBodyVO[] vos) throws BusinessException{
		if(ArrayUtils.isEmpty(vos))
			return null;
		String[] pk_psndocs = StringPiecer.getStrArrayDistinct((SuperVO[])vos, "pk_psndoc");
		// ȡ����VO�е����翪ʼ���ں������������ڣ�������SQL�������������ڴ棩
		IDateScope scope = new DefaultDateScope(DateScopeUtils.findEarliestBeginDate(vos).getDateBefore(2), DateScopeUtils.findLatestEndDate(vos).getDateAfter(2));
		return NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).queryDateJobOrgMapByPsndocInSQL(pk_psndocs, new IDateScope[]{scope});
	}
	/**
	 * У��excel���Ƿ����ʱ�佻������
	 * @param vos
	 * @return
	 */
	private boolean validateTimeScope(ImportVO[] vos){
		int size = vos.length;
		for(int i = 0; i < size -1; i++) {
			UFDateTime curBeginTime = vos[i].getOvertimebegintime();
			UFDateTime curEndTime = vos[i].getOvertimeendtime();
			if(curBeginTime == null || curEndTime==null){
				continue;
			}
			//У���ͻ
			ITimeScope voScope = new DefaultTimeScope(curBeginTime,curEndTime);
			if(isNotNull(vos[i].getPk_psndoc())){//��Ա��Ϣ������ݲ�У��ʱ���Ƿ񽻲�
				for(int j = i+1; j< size; j++) {
					if(vos[i].getPk_psndoc().equals(vos[j].getPk_psndoc())){//ͬһ��ԱУ��ʱ���Ƿ񽻼�
						UFDateTime compBeginTime = vos[j].getOvertimebegintime();
						UFDateTime compEndTime = vos[j].getOvertimeendtime();
						if(compBeginTime == null || compEndTime == null){
							continue;
						}
						ITimeScope thisScope = new DefaultTimeScope(compBeginTime,compEndTime);
						if(TimeScopeUtils.isCross(voScope, thisScope)) {
							vos[i].setErrmsg("���"+vos[j].getRow()+"������ʱ��<"+vos[j].getVoerdate()
									+" "+vos[j].getVoertimebegin()+"-"+vos[j].getVoertimeend()+">���ڽ���");
						}
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * ת�������������в��յĶ�Ӧ������У����Ա���������Ƿ�ƥ��
	 * @param ivos
	 * @throws BusinessException 
	 */
	private void getRefInfo(ImportVO[] ivos) throws BusinessException{
		Set<String>  psncodeSet = new HashSet<String>();//��Ա����
		Set<String>  projectSet = new HashSet<String>();//ר��
		Set<String> overtimetypeSet = new HashSet<String>();
		for(ImportVO vo : ivos){
			if(isNotNull(vo.getPsncode())){
				psncodeSet.add(vo.getPsncode());
			}
			if(isNotNull(vo.getProjectname())){
				projectSet.add(vo.getProjectname());
			}
			if(isNotNull(vo.getOvertimetype())){
				overtimetypeSet.add(vo.getOvertimetype());
			}
		}
		psntbminfo(ivos, psncodeSet);
		typeinfo_refmodel(ivos, overtimetypeSet);
//		typeinfo(ivos, overtimetypeSet);
		projectInfo(ivos, projectSet);
	}
	/**
	 * ת��ר����Ϣ
	 * @param ivos
	 * @param projectSet
	 * @return
	 * @throws BusinessException 
	 */
	private boolean projectInfo(ImportVO[] ivos,Set<String>  projectSet) throws BusinessException{
		BaseDAO dao = new BaseDAO();
		//MOD ר��������Ҫ������֯ James
		String pk_org = ivos[0].getPk_org();		
		String sql="select * from bd_defdoc where enablestate=2 and dr = 0 and pk_defdoclist='1001A110000000007PSD' and pk_org='"+pk_org+"' and code in " 
		+InSqlManager.getInSQLValue(projectSet.toArray(new String[]{}));
		ArrayList<DefdocVO> defdocvoList=(ArrayList<DefdocVO>) dao.executeQuery(sql, new BeanListProcessor(DefdocVO.class));
		Map<String,DefdocVO> proMap = new HashMap<String,DefdocVO>();
		if (defdocvoList != null && defdocvoList.size() > 0) {
			for(DefdocVO projectvo : defdocvoList){
				proMap.put(projectvo.getCode(), projectvo);
			}
			for(ImportVO ivo :ivos){
				if(isNotNull(ivo.getProjectname()) && proMap.get(ivo.getProjectname()) != null){
					ivo.setPk_project(proMap.get(ivo.getProjectname()).getPk_defdoc());
				}else {
					if(isNotNull(ivo.getProjectname())){
						ivo.setErrmsg("δ�ҵ�ר��:"+ivo.getProjectname());
					}
				}	
			}
		}else{
			for(ImportVO ivo :ivos){
				if(isNotNull(ivo.getProjectname()))
					ivo.setErrmsg("δ�ҵ�ר��:"+ivo.getProjectname());
			}
		}
		
		return true;
	}
	
	/**
	 * �ԼӰ������յ�sql��ȡ��Ӧ�ļӰ������Ϣ
	 * @param ivos
	 * @param overtimetypeSet
	 * @return
	 * @throws BusinessException
	 */
	private boolean typeinfo_refmodel(ImportVO[] ivos,Set<String>  overtimetypeSet) throws BusinessException{
		BaseDAO dao = new BaseDAO();
		String pk_org = ivos[0].getPk_org();
		String queryallsql = "select  tbm_timeitem.timeitemcode,timeitemname,tbm_timeitem.pk_timeitem,tbm_timeitem.itemtype,tbm_timeitemcopy.pk_timeitemcopy," +
				"tbm_timeitemcopy.pk_org,tbm_timeitemcopy.timeitemunit,tbm_timeitemcopy.leavesetperiod" +
				"  from  tbm_timeitem inner join tbm_timeitemcopy on tbm_timeitem.pk_timeitem=tbm_timeitemcopy.pk_timeitem " +
				"  and tbm_timeitemcopy.pk_org='"+pk_org+"'   where tbm_timeitem.itemtype=1 and tbm_timeitemcopy.enablestate=2" +
				"  and timeitemname  in"+InSqlManager.getInSQLValue(overtimetypeSet.toArray(new String[]{}));
		
		@SuppressWarnings("unchecked")
		Map<String,Map<String,String>> typeinfoMap = (Map<String, Map<String, String>>) dao.executeQuery(queryallsql, new ResultSetProcessor() {
			@Override
			public Map<String,Map<String,String>> handleResultSet(ResultSet rs) throws SQLException {
				Map<String,Map<String,String>> typeinfoMap = new HashMap<String,Map<String,String>>();
				Map<String,String> typeMap = new HashMap<String,String>();
				Map<String,String> typecopyMap = new HashMap<String,String>();
				while(rs.next()){
					typeMap.put(rs.getString("timeitemname"), rs.getString("pk_timeitem"));
					typecopyMap.put(rs.getString("timeitemname"), rs.getString("pk_timeitemcopy"));
				}
				typeinfoMap.put("type", typeMap);
				typeinfoMap.put("typecopy", typecopyMap);
				return typeinfoMap;
			}
		});
		Map<String,String> typeMap = new HashMap<String,String>();
		Map<String,String> typecopyMap = new HashMap<String,String>();
		if(typeinfoMap != null && typeinfoMap.size() > 0){
			typeMap = typeinfoMap.get("type");
			typecopyMap = typeinfoMap.get("typecopy");
		}
		for(ImportVO ivo : ivos){
			if(typeMap.size() == 0 || !typeMap.containsKey(ivo.getOvertimetype())){
				if(isNotNull(ivo.getOvertimetype())){
					ivo.setErrmsg("δ�ҵ��Ӱ����"+ivo.getOvertimetype());
				}
			}else {
				ivo.setPk_overtimetype(typeMap.get(ivo.getOvertimetype()));
				ivo.setPk_overtimetypecopy(typecopyMap.get(ivo.getOvertimetype()));
			}
		}
		return true;
	}
	/**
	 * ת���Ӱ������Ϣ
	 * @param ivos
	 * @param overtimetypeSet
	 * @return
	 * @throws BusinessException 
	 */
	private boolean typeinfo(ImportVO[] ivos,Set<String>  overtimetypeSet) throws BusinessException{
		if(overtimetypeSet == null || overtimetypeSet.size() == 0){
			return false;
		}
		BaseDAO dao = new BaseDAO();
		String pk_org = ivos[0].getPk_org();
		String typesqlCondition = " enablestate=2 and pk_org ='"+pk_org+"' and  itemtype=1 " +
				"and timeitemname  in"+InSqlManager.getInSQLValue(overtimetypeSet.toArray(new String[]{}));//��ѯ�Ӱ����
		String typecopysqlCondition = " enablestate=2 and  pk_org ='"+pk_org+"' and pk_timeitem " 
		+"in( select pk_timeitem from tbm_timeitem  where "+typesqlCondition+")";//��ѯ�Ӱ���𿽱�
		@SuppressWarnings("unchecked")
		Collection<OverTimeTypeCopyVO> typecopyvos = dao.retrieveByClause(OverTimeTypeCopyVO.class, typecopysqlCondition);
		Collection<OverTimeTypeVO> typevos = dao.retrieveByClause(OverTimeTypeVO.class, typesqlCondition);
		Map<String,List<OverTimeTypeVO>> typevoMap = new HashMap<String,List<OverTimeTypeVO>>();
		Map<String,OverTimeTypeCopyVO> typecopyvoMap = new HashMap<String,OverTimeTypeCopyVO>();
		if(typevos != null && typevos.size() > 0){
			for(OverTimeTypeVO v : typevos){
				if(!typevoMap.containsKey(v.getTimeitemname())){
					List<OverTimeTypeVO> list = new ArrayList<OverTimeTypeVO>();
					typevoMap.put(v.getTimeitemname(), list);
				}
				typevoMap.get(v.getTimeitemname()).add(v);
			}
		}
		if(typecopyvos != null && typecopyvos.size() > 0){
			for(OverTimeTypeCopyVO cpvo : typecopyvos){
				typecopyvoMap.put(cpvo.getPk_timeitem(), cpvo);
			}
		}
		for(ImportVO ivo : ivos){
			if(typevoMap.size() == 0){
				ivo.setErrmsg("δ�ҵ��Ӱ����"+ivo.getOvertimetype());
			}else {
				List<OverTimeTypeVO> otlist = typevoMap.get(ivo.getOvertimetype());
				boolean isexist = false;
				if(otlist != null && otlist.size() > 0){
					for(OverTimeTypeVO ot : otlist){
						if("GLOBLE00000000000000".equals(ot.getPk_org())){//ȫ������
							isexist = true;
//							if(ivo.getPk_overtimetype() == null){
//								ivo.setPk_overtimetype(ot.getPk_timeitem());
//								if(typecopyvoMap.containsKey(ot.getPk_timeitem())){
//									ivo.setPk_overtimetypecopy(typecopyvoMap.get(ot.getPk_timeitem()).getPk_timeitemcopy());
//								}
//							}else if(ot.getPk_group().equals(ivo.getPk_group())){//ȫ�����ݵ�ǰ��������
//								ivo.setPk_overtimetype(ot.getPk_timeitem());
//								if(typecopyvoMap.containsKey(ot.getPk_timeitem())){
//									ivo.setPk_overtimetypecopy(typecopyvoMap.get(ot.getPk_timeitem()).getPk_timeitemcopy());
//								}
//							}
							ivo.setPk_overtimetype(ot.getPk_timeitem());
							if(typecopyvoMap.containsKey(ot.getPk_timeitem())){
								ivo.setPk_overtimetypecopy(typecopyvoMap.get(ot.getPk_timeitem()).getPk_timeitemcopy());
							}
							
						}
						if(ot.getPk_group().equals(ivo.getPk_group())){//��ǰ���ŵ�����
							if(ot.getPk_group().equals(ot.getPk_org())){//��������
								isexist = true;
								ivo.setPk_overtimetype(ot.getPk_timeitem());
								if(typecopyvoMap.containsKey(ot.getPk_timeitem())){
									ivo.setPk_overtimetypecopy(typecopyvoMap.get(ot.getPk_timeitem()).getPk_timeitemcopy());
								}
							}
							if(ot.getPk_org().equals(ivo.getPk_org())){//��ǰ��֯������
								isexist = true;
								ivo.setPk_overtimetype(ot.getPk_timeitem());
								if(typecopyvoMap.containsKey(ot.getPk_timeitem())){
									ivo.setPk_overtimetypecopy(typecopyvoMap.get(ot.getPk_timeitem()).getPk_timeitemcopy());
								}
							}
						}
					}
				}
				if(!isexist){
					ivo.setErrmsg("δ�ҵ��Ӱ����"+ivo.getOvertimetype());
				}
				
			}
		}
		return true;
	}
	
	/**
	 * ת����Ա������Ϣ
	 * @param ivos
	 * @param psncodeSet
	 * @return
	 * @throws BusinessException
	 */
	private boolean psntbminfo(ImportVO[] ivos,Set<String>  psncodeSet) throws BusinessException{
		BaseDAO dao = new BaseDAO();
		String pk_org = ivos[0].getPk_org();
		String psnsql = "select  bd_psndoc.code,bd_psndoc.name,clerkcode,tbm_psndoc.timecardid,tbm_psndoc.begindate,tbm_psndoc.enddate,org_adminorg.name," +
				"org_dept.name,bd_psndoc.pk_group,bd_psndoc.pk_org,bd_psndoc.pk_psndoc,hi_psnjob.pk_psnorg,hi_psnjob.pk_psnjob,hi_psnjob.pk_psncl," +
				"hi_psnjob.pk_org," +"hi_psnjob.pk_dept,hi_psnjob.pk_job,hi_psnjob.pk_post,org_adminorg.PK_VID as pk_org_v,org_dept.pk_vid as pk_dept_v "
				+" from   tbm_psndoc inner join hi_psnjob on tbm_psndoc.pk_psnjob = hi_psnjob.pk_psnjob  " +
				"inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg " +
				"inner join bd_psndoc on hi_psnorg.pk_psndoc = bd_psndoc.pk_psndoc " +
				"left outer join org_adminorg on org_adminorg.pk_adminorg = hi_psnjob.pk_org " +
				"left outer join org_dept on org_dept.pk_dept = hi_psnjob.pk_dept " +
				"left outer join om_post on om_post.pk_post = hi_psnjob.pk_post  " +
				"where (tbm_psndoc.pk_tbm_psndoc in " +
				"   (select pk_tbm_psndoc from tbm_psndoc where   (tbm_psndoc.enddate =(select max(enddate) from tbm_psndoc psndoc2 where psndoc2.pk_org = '"+pk_org+"' and psndoc2.pk_psndoc=tbm_psndoc.pk_psndoc))) " +
				") and bd_psndoc.code in "+InSqlManager.getInSQLValue(psncodeSet.toArray(new String[]{}));
		@SuppressWarnings("unchecked")
		Map<String,Map<String,String>> psninfoMap = (Map<String, Map<String,String>>) dao.executeQuery(psnsql, new ResultSetProcessor() {
		
			private static final long serialVersionUID = 1L;

			@Override
			public Map<String,Map<String,String>> handleResultSet(ResultSet rs) throws SQLException {
				Map<String,Map<String,String>> map = new HashMap<String,Map<String,String>>();
				while(rs.next()){
					Map<String,String> pknameMap = new HashMap<String,String>();
					pknameMap.put("psnzhname",rs.getString("name"));
					pknameMap.put("pk_psndoc",rs.getString("pk_psndoc"));
					pknameMap.put("pk_job",rs.getString("pk_job"));
					pknameMap.put("pk_org_v", rs.getString("pk_org_v"));
					map.put(rs.getString("code"), pknameMap);
				}
				return map;
			}
		});
		if(psninfoMap != null && psninfoMap.size() > 0){
			for(ImportVO vo : ivos){
				if(isNotNull(vo.getPsncode())){
					Map<String,String> pknameinfomap = psninfoMap.get(vo.getPsncode());
					if(pknameinfomap == null){
						vo.setErrmsg("��Ա����<"+vo.getPsncode()+">�����ڻ��ڵ�ǰ��֯��");
					}else{
						String zhname = pknameinfomap.get("psnzhname");
						String pk_psndoc = pknameinfomap.get("pk_psndoc");
						vo.setPk_psndoc(pk_psndoc);
						if(isNotNull(vo.getPsnzhname())){
							if(!vo.getPsnzhname().equals(zhname)){
								vo.setErrmsg("���������������["+zhname+"]��һ��");
							}
						}
					}
				}
			}
		}else{
			for(ImportVO vo : ivos){
				vo.setErrmsg("��Ա����<"+vo.getPsncode()+">�����ڻ��ڵ�ǰ��֯��");
			}
		}
		return true;
	}
	/**
	 * У��string�Ƿ�Ϊ��
	 * @param str
	 * @return
	 */
	private boolean isNotNull(String str){
		if(str == null || "".equals(str) || "".equals(str.trim())){
			return false;
		}
		return true;
	}

	@Override
	public ExportVO exportdata() throws BusinessException {
		String filepath = RuntimeEnv.getInstance().getNCHome() +File.separator +"resources"
					+ File.separator + "overtimetemplet" +File.separator+"�Ӱ�Ǽ�����.xlsx";
		try {
			byte[] databytes = getFileBytes(filepath);
			ExportVO evo = new ExportVO();
			evo.setDatabytes(databytes);
			return evo;
		} catch (Exception e) {
			throw new BusinessException("������ģ�嶪ʧ�����ڲ�ȫNCHOME/resources/overtimetemplet/�Ӱ�Ǽ�����.xlsx");
		}
	}
	
	/**
     * ���ļ������ɶ�����
     * @author xiepch
     * @param fileName
     * @return
     * @throws Exception
     */
    public static byte[] getFileBytes(String fileName) throws Exception
    {
        ByteArrayOutputStream bos = null;
        InputStream fis = null;
        GZIPOutputStream gzip = null;
        
        byte ba[] = new byte[1024];
        
        try
        {
            bos = new ByteArrayOutputStream();
            fis = new FileInputStream(fileName);
            gzip = new GZIPOutputStream(bos);
            
            do
            {
                int len = fis.read(ba, 0, ba.length);
                if (len == -1)
                {
                    break;
                }
                
                gzip.write(ba, 0, len);
            }
            while (true);
            
            gzip.finish();
            gzip.flush();
            ba = bos.toByteArray();
        }
        catch (Exception ex)
        {
            Logger.error(ex);
        }
        finally
        {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(gzip);
        }
        
        return ba;
    }
}