package nc.impl.ta.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.impl.ta.common.utils.CorporateOrg;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.impl.DefaultDateScope;
import nc.pubitf.para.SysInitQuery;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnCalendarVO;

import org.apache.commons.lang.StringUtils;

public abstract class AbstractTWHolidayOffdayValidator implements ITWHolidayOffdayValidate {
    private IPsnCalendarQueryService calendarService;
    private BaseDAO baseDAO;
    private CorporateOrg org = new CorporateOrg();

    public String validate(String pk_org, String pk_psndoc, UFLiteralDate checkDate, List<AggPsnCalendar> volists)
	    throws BusinessException {
	String strMessage = "";
	PsndocVO psnvo = (PsndocVO) this.getBaseDAO().retrieveByPK(PsndocVO.class, pk_psndoc);
	IDateScope[] checkScope = this.getDateScopeGroupContainsCheckDate(pk_org, checkDate);
	Map<UFLiteralDate, Integer> allDayTypeMap = new HashMap<UFLiteralDate, Integer>();
	List<AggPsnCalendar> allCalendarVOs = new ArrayList<AggPsnCalendar>();

	checkScope = this.reGroupDateScope(checkScope, this.getMinHolidayCheckWeeks());
	for (IDateScope scope : checkScope) {
	    Map<UFLiteralDate, Integer> dayTypeMap = new HashMap<UFLiteralDate, Integer>();

	    AggPsnCalendar[] vos = this.getCalendarService().queryCalendarVOsByPsnDates(pk_org, pk_psndoc,
		    scope.getBegindate(), scope.getEnddate());
	    // ��ȡ����ͬ���ڵ�vo
	    List<AggPsnCalendar> list = new ArrayList<AggPsnCalendar>();
	    for (AggPsnCalendar hvo : volists) {
		if (((PsnCalendarVO) hvo.getParentVO()).getPk_psndoc().equals(pk_psndoc)
			&& (((PsnCalendarVO) hvo.getParentVO()).getCalendar().isSameDate(scope.getBegindate()) || ((PsnCalendarVO) hvo
				.getParentVO()).getCalendar().after(scope.getBegindate()))
			&& (((PsnCalendarVO) hvo.getParentVO()).getCalendar().isSameDate(scope.getEnddate()) || ((PsnCalendarVO) hvo
				.getParentVO()).getCalendar().before(scope.getEnddate()))) {
		    list.add(hvo);
		}
	    }
	    List<AggPsnCalendar> volist = getPsnCalendarVOs(vos, list);
	    for (AggPsnCalendar vo : volist) {
		int dayType = -1;
		if (((PsnCalendarVO) vo.getParentVO()).getDate_daytype() == null) {
		    String pk_shift = ((PsnCalendarVO) vo.getParentVO()).getPk_shift();
		    if (pk_shift == null) {
			return null;
		    } else {
			if (pk_shift.equals(ShiftVO.PK_GX)) {
			    dayType = 1;
			} else {
			    dayType = 0;
			}
		    }
		} else {
		    dayType = ((PsnCalendarVO) vo.getParentVO()).getDate_daytype();
		}
		dayTypeMap.put(vo.getDate(), dayType);
		allDayTypeMap.put(vo.getDate(), dayType);
	    }

	    // �Ű���С춙z���L��Ҫ���գ�����δ����Ű࣬���M��У�
	    if (dayTypeMap.size() < this.getMinHolidayCheckWeeks() * 7) {
		return null;
	    }

	    try {
		// 7��1֮������У�
		strMessage = this.checkHoliday(dayTypeMap, new int[] { 4 }, this.getHolidayCountInMinCheckWeeks());
		if (!StringUtils.isEmpty(strMessage)) {
		    return psnvo.getCode() + ":" + strMessage;
		}
	    } catch (BusinessException e) {
		throw new BusinessException(e.getMessage() + "\r\n�T����" + psnvo.getCode() + "\r\n�L�ڣ�["
			+ scope.getBegindate().toString() + "] ~ [" + scope.getEnddate().toString() + "]\r\n");
	    }

	    try {
		// С�L����������r��У�
		strMessage = this.checkMaxDailyWorkHoursInMinWeek(volist, this.getMaxDailyWorkHoursInMinWeeks());
		allCalendarVOs.addAll(volist);
		if (!StringUtils.isEmpty(strMessage)) {
		    return psnvo.getCode() + ":" + strMessage;
		}
	    } catch (BusinessException e) {
		throw new BusinessException(e.getMessage() + "\r\n�T����" + psnvo.getCode() + "\r\n�L�ڣ�["
			+ scope.getBegindate().toString() + "] ~ [" + scope.getEnddate().toString() + "]\r\n");
	    }
	}

	// �Ű���С춙z���L��Ҫ���գ�����δ����Ű࣬���M��У�
	if (allDayTypeMap.size() < getCheckedWeeks() * 7) {
	    return null;
	}
	try {
	    // �����ռ��ݼ��տ�����У�
	    strMessage = this.checkHoliday(allDayTypeMap, new int[] { 1, 4 }, this.getTotalHolidaysAndOffdaysCount());
	    if (!StringUtils.isEmpty(strMessage)) {
		return psnvo.getCode() + "=" + strMessage;
	    }
	} catch (BusinessException e) {
	    throw new BusinessException(e.getMessage() + "\r\n�T����" + psnvo.getCode() + "\r\n�L�ڣ�["
		    + checkScope[0].getBegindate().toString() + "] ~ ["
		    + checkScope[checkScope.length - 1].getEnddate().toString() + "]\r\n");
	}

	try {
	    // С�L����������r��У�
	    strMessage = this.checkTotalWorkHoursInMinWeek(allCalendarVOs, this.getTotalMaxWorkHoursInMinWeeks());
	    if (!StringUtils.isEmpty(strMessage)) {
		return psnvo.getCode() + ":" + strMessage;
	    }
	} catch (BusinessException e) {
	    throw new BusinessException(e.getMessage() + "\r\n�T����" + psnvo.getCode() + "\r\n�L�ڣ�["
		    + checkScope[0].getBegindate().toString() + "] ~ ["
		    + checkScope[checkScope.length - 1].getEnddate().toString() + "]\r\n");
	}

	try {
	    // ���L����������r��У�
	    strMessage = this.checkTotalWorkHoursInMaxWeek(allCalendarVOs, this.getTotalMaxWorkHoursInMaxWeeks());
	    if (!StringUtils.isEmpty(strMessage)) {
		return psnvo.getCode() + ":" + strMessage;
	    }
	} catch (BusinessException e) {
	    throw new BusinessException(e.getMessage() + "\r\n�T����" + psnvo.getCode() + "\r\n�L�ڣ�["
		    + checkScope[0].getBegindate().toString() + "] ~ ["
		    + checkScope[checkScope.length - 1].getEnddate().toString() + "]\r\n");
	}

	return null;
    }

    /**
     * ���L����������r��У�
     * 
     * @param allCalendarVOs
     * @param totalMaxWorkHoursInMaxWeeks
     * @return
     */
    private String checkTotalWorkHoursInMaxWeek(List<AggPsnCalendar> allCalendarVOs, int totalMaxWorkHoursInMaxWeeks)
	    throws BusinessException {
	// TODO �ԄӮa���ķ��� Stub
	return null;
    }

    /**
     * С�L����������r��У�
     * 
     * @param allCalendarVOs
     * @param totalMaxWorkHoursInMinWeeks
     * @return
     */
    private String checkTotalWorkHoursInMinWeek(List<AggPsnCalendar> allCalendarVOs, int totalMaxWorkHoursInMinWeeks)
	    throws BusinessException {
	// TODO �ԄӮa���ķ��� Stub
	return null;
    }

    /**
     * С�L����������r��У�
     * 
     * @param volist
     * @return
     * @throws BusinessException
     */
    private String checkMaxDailyWorkHoursInMinWeek(List<AggPsnCalendar> volist, int dailyWorkLimit)
	    throws BusinessException {
	// TODO �ԄӮa���ķ��� Stub

	return null;
    }

    private List<AggPsnCalendar> getPsnCalendarVOs(AggPsnCalendar[] vos, List<AggPsnCalendar> volists) {
	List<AggPsnCalendar> voslist = new ArrayList<AggPsnCalendar>();
	Map<UFLiteralDate, AggPsnCalendar> mapvos = new HashMap<UFLiteralDate, AggPsnCalendar>();
	if (null == vos || vos.length == 0) {
	    voslist.addAll(volists);
	} else {
	    for (AggPsnCalendar vo : vos) {
		mapvos.put(((PsnCalendarVO) vo.getParentVO()).getCalendar(), vo);
	    }
	    for (AggPsnCalendar volist : volists) {
		mapvos.put(((PsnCalendarVO) volist.getParentVO()).getCalendar(), volist);
	    }
	    for (UFLiteralDate date : mapvos.keySet()) {
		voslist.add(mapvos.get(date));
	    }
	}

	return voslist;
    }

    /**
     * �z���L��
     * 
     * @return
     * @throws BusinessException
     */
    public abstract int getCheckedWeeks() throws BusinessException;

    /**
     * ��С���ٙz���L��
     * 
     * @return
     * @throws BusinessException
     */
    public abstract int getMinHolidayCheckWeeks() throws BusinessException;

    /**
     * ��С���ٙz���L�������������յ��씵
     * 
     * @return
     * @throws BusinessException
     */
    public abstract int getHolidayCountInMinCheckWeeks() throws BusinessException;

    /**
     * ���L�ڃ���Ϣ�ռ������տ��씵
     * 
     * @return
     * @throws BusinessException
     */
    public abstract int getTotalHolidaysAndOffdaysCount() throws BusinessException;

    /**
     * С�L����������r��
     * 
     * @return
     * @throws BusinessException
     */
    public abstract int getMaxDailyWorkHoursInMinWeeks() throws BusinessException;

    /**
     * С�L����������r��
     * 
     * @return
     * @throws BusinessException
     */
    public abstract int getTotalMaxWorkHoursInMinWeeks() throws BusinessException;

    /**
     * ���L����������r��
     * 
     * @return
     * @throws BusinessException
     */
    public abstract int getTotalMaxWorkHoursInMaxWeeks() throws BusinessException;

    /**
     * �����M���͙z������ȡ�z���L��
     * 
     * @param pk_org
     *            �M��PK
     * @param checkDate
     *            �z������
     * @return
     * @throws BusinessException
     */
    public IDateScope[] getDateScopeGroupContainsCheckDate(String pk_org, UFLiteralDate checkDate)
	    throws BusinessException {
	// �����M������ȡһ��һ�ݙz���_ʼ����
	UFLiteralDate startDate = getOrgStartDate(pk_org);

	// �z�����c��ʼ������씵
	int days = UFLiteralDate.getDaysBetween(startDate, checkDate);
	// �z�����c��ʼ������L�ڔ�
	int passedWeeks = (int) Math.floor(Double.valueOf(days) / Double.valueOf(getCheckedWeeks() * 7));

	UFLiteralDate startCheckDate = startDate.getDateAfter(passedWeeks * getCheckedWeeks() * 7);

	List<DefaultDateScope> scopes = new ArrayList<DefaultDateScope>();
	for (int i = 0; i < getCheckedWeeks(); i++) {
	    DefaultDateScope scope = new DefaultDateScope();
	    scope.setBegindate(startCheckDate.getDateAfter(i * 7));
	    scope.setEnddate(scope.getBegindate().getDateAfter(6));
	    scopes.add(scope);
	}
	return scopes.toArray(new IDateScope[0]);
    }

    /**
     * ������С�z���L�����L�������·ֽM
     * 
     * @param oneWeekScopes
     *            ���L�������M
     * @param groupWeeks
     *            �ֽM�L��
     * @return
     * @throws BusinessException
     */
    public IDateScope[] reGroupDateScope(IDateScope[] oneWeekScopes, int groupWeeks) throws BusinessException {
	if (groupWeeks == 1) {
	    return oneWeekScopes;
	} else {
	    int i = 1;
	    DefaultDateScope newScope = null;
	    List<DefaultDateScope> reGroupedScope = new ArrayList<DefaultDateScope>();
	    for (IDateScope scope : oneWeekScopes) {
		if (i == 1) {
		    newScope = new DefaultDateScope();
		    newScope.setBegindate(scope.getBegindate());
		} else if (i == this.getMinHolidayCheckWeeks()) {
		    newScope.setEnddate(scope.getEnddate());
		    reGroupedScope.add(newScope);
		    i = 0;
		}
		i++;
	    }
	    return reGroupedScope.toArray(new IDateScope[0]);
	}
    }

    private UFLiteralDate getOrgStartDate(String pk_org) throws BusinessException {
	// ��ȡ������֯
	String legal_pk_org = org.legorg(pk_org);
	String strStart = SysInitQuery.getParaString(legal_pk_org, "TWHRT01");
	UFLiteralDate startDate = null;
	if (!StringUtils.isEmpty(strStart)) {
	    startDate = new UFLiteralDate(strStart);
	}

	if (startDate == null) {
	    throw new BusinessException("һ��һ��У�ʧ����δָ��һ��һ����ʼ׃���գ�TWHRT01��");
	}

	// �����O�����_ʼ���������ڎ�
	int weekDayOfStartDate = startDate.getWeek();
	// �����O�����������������ڎ�
	int startWeekDay = SysInitQuery.getParaInt(legal_pk_org, "TWHRT02");

	// ����_ʼ������ԓ�L�����L������Ҏ�������ڎף�Ҫ������һ�����ڎ�����z����L��������
	if (weekDayOfStartDate != startWeekDay) {
	    startDate = startDate.getDateAfter(weekDayOfStartDate < startWeekDay ? (startWeekDay - weekDayOfStartDate)
		    : (7 + startWeekDay - weekDayOfStartDate));
	}
	return startDate;
    }

    public String checkHoliday(Map<UFLiteralDate, Integer> dayTypeMap, int[] checkDateTypes, int minCount)
	    throws BusinessException {
	int matched = 0;
	for (Entry<UFLiteralDate, Integer> dayType : dayTypeMap.entrySet()) {
	    for (int checkType : checkDateTypes) {
		if (checkType == dayType.getValue()) {
		    matched++;
		}
	    }
	}
	String strMessage = "";
	if (checkDateTypes.length == 1) {
	    if (matched < minCount) {
		strMessage = "�������씵��춄ڻ���Ҏ����";
	    }
	} else {
	    if (matched < minCount) {
		strMessage = "�����ռ���Ϣ���씵��춄ڻ���Ҏ����";
	    }
	}
	return strMessage;
    }

    public IPsnCalendarQueryService getCalendarService() {
	if (calendarService == null) {
	    calendarService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
	}
	return calendarService;
    }

    public BaseDAO getBaseDAO() {
	if (baseDAO == null) {
	    baseDAO = new BaseDAO();
	}

	return baseDAO;
    }
}