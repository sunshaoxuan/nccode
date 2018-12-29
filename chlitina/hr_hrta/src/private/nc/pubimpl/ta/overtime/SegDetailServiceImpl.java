package nc.pubimpl.ta.overtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.impl.ta.overtime.SegdetailMaintainImpl;
import nc.itf.hrwa.IWadaysalaryQueryService;
import nc.itf.ta.IPeriodQueryService;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hrwa.wadaysalary.DaySalaryEnum;
import nc.vo.ml.MultiLangUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.overtime.AggSegDetailVO;
import nc.vo.ta.overtime.AggSegRuleVO;
import nc.vo.ta.overtime.CalendarDateTypeEnum;
import nc.vo.ta.overtime.MonthStatOTCalcVO;
import nc.vo.ta.overtime.OTBalanceDetailVO;
import nc.vo.ta.overtime.OTLeaveBalanceVO;
import nc.vo.ta.overtime.OvertimeLimitScopeEnum;
import nc.vo.ta.overtime.OvertimeRegVO;
import nc.vo.ta.overtime.OvertimeSettleTypeEnum;
import nc.vo.ta.overtime.QueryValueTypeEnum;
import nc.vo.ta.overtime.SegDetailConsumeVO;
import nc.vo.ta.overtime.SegDetailVO;
import nc.vo.ta.overtime.SegRuleTermVO;
import nc.vo.ta.overtime.SegRuleVO;
import nc.vo.ta.overtime.SoureBillTypeEnum;
import nc.vo.ta.overtime.TaxFlagEnum;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.OverTimeTypeCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.lang.StringUtils;

public class SegDetailServiceImpl implements ISegDetailService {
    private Map<String, OTSChainNode> allNode = null;
    private BaseDAO baseDao = null;
    private IPeriodQueryService periodService = null;

    @Override
    public void regOvertimeSegDetail(OvertimeRegVO[] overtimeRegVOs) throws BusinessException {
	if (overtimeRegVOs != null && overtimeRegVOs.length > 0) {
	    this.getAllNode().clear();
	    for (OvertimeRegVO otRegVo : overtimeRegVOs) {
		UFBoolean isEnabled = new UFBoolean(SysInitQuery.getParaString(otRegVo.getPk_org(), "TBMOTSEG"));
		if (isEnabled == null || !isEnabled.booleanValue()) {
		    return;
		}

		String pk_psndoc = otRegVo.getPk_psndoc();

		// 取加班類別
		OverTimeTypeCopyVO otType = (OverTimeTypeCopyVO) this.getBaseDao().retrieveByPK(
			OverTimeTypeCopyVO.class, otRegVo.getPk_overtimetypecopy());

		if (otType.getPk_segrule() != null) {
		    // 根據加班類別取分段依據
		    AggSegRuleVO ruleAggVO = getSegRuleAggVO(otType.getPk_segrule());

		    // 取當前加班分段明細節點
		    OTSChainNode curOTSegNode = getOvertimeSegChainNodeByOTReg(otRegVo, ruleAggVO);

		    // 取當前人員全節點（物理鏈表）
		    if (!this.getAllNode().containsKey(pk_psndoc)) {
			OTSChainNode psnNode = OTSChainUtils.buildChainNodes(pk_psndoc, null, null, false, false,
				false, false, false);
			this.getAllNode().put(pk_psndoc, psnNode);
		    }

		    // 校驗加班日是否已存在不同的加班分段依據
		    OTSChainNode checkNode = this.getAllNode().get(pk_psndoc);
		    while (checkNode != null) {
			if (checkNode.getNodeData().getRegdate().isSameDate(curOTSegNode.getNodeData().getRegdate())) {
			    // 已存在節點與當前加班日為同一天
			    if (!otType.getPk_segrule().equals(checkNode.getNodeData().getPk_segrule())) {
				throw new BusinessException("系統中已存在使用不同類型的加班分段規則的加班登記單。 ");
			    }
			}
			checkNode = checkNode.getNextNode();
		    }

		    OTSChainNode combinedNodes = OTSChainUtils.combineNodes(this.getAllNode().get(pk_psndoc),
			    curOTSegNode);
		    this.getAllNode().put(pk_psndoc, combinedNodes);
		    OTSChainUtils.saveAll(this.getAllNode().get(pk_psndoc));
		}
	    }
	}
    }

    @Override
    public void updateOvertimeSegDetail(OvertimeRegVO[] overtimeRegVOs) throws BusinessException {
	if (overtimeRegVOs != null && overtimeRegVOs.length > 0) {
	    deleteOvertimeSegDetail(overtimeRegVOs);
	    regOvertimeSegDetail(overtimeRegVOs);
	}

    }

    @Override
    public void deleteOvertimeSegDetail(OvertimeRegVO[] overtimeRegVOs) throws BusinessException {
	if (overtimeRegVOs != null && overtimeRegVOs.length > 0) {
	    List<AggSegDetailVO> aggvos = new ArrayList<AggSegDetailVO>();

	    for (OvertimeRegVO vo : overtimeRegVOs) {
		OTSChainNode curNode = OTSChainUtils.buildChainNodes(vo.getPk_psndoc(), null, vo.getPk_overtimereg(),
			false, false, false, false, false);
		boolean canDel = true;
		while (curNode != null) {
		    if (curNode.getNodeData().getConsumedhours().doubleValue() > 0
			    || containsChild(curNode.getNodeData().getPk_segdetail())) {
			canDel = false;
			break;
		    }

		    AggSegDetailVO aggvo = new AggSegDetailVO();
		    aggvo.setParent(curNode.getNodeData());
		    aggvos.add(aggvo);

		    curNode = curNode.getNextNode();
		}

		if (!canDel) {
		    throw new BusinessException("加班登記單對應的分段明細已被消耗");
		}

	    }

	    if (aggvos.size() > 0) {
		new SegdetailMaintainImpl().delete(aggvos.toArray(new AggSegDetailVO[0]));
	    }
	}
    }

    private boolean containsChild(String pk_segdetail) throws BusinessException {
	Collection children = this.getBaseDao().retrieveByClause(SegDetailConsumeVO.class,
		"pk_segdetail='" + pk_segdetail + "'");
	return (children != null && children.size() > 0);
    }

    private OTSChainNode getOvertimeSegChainNodeByOTReg(OvertimeRegVO otRegVO, AggSegRuleVO ruleAggVO)
	    throws BusinessException {
	SegRuleVO rule = ruleAggVO.getParentVO();
	SegRuleTermVO[] terms = (SegRuleTermVO[]) ruleAggVO.getChildrenVO();

	UFDouble othours = otRegVO.getOvertimehour();
	UFDouble totalSegHours = UFDouble.ZERO_DBL; // 纍計生成分段時長
	OTSChainNode parentNode = null;
	UFLiteralDate realDate = getShiftRegDate(otRegVO); // 獲取實際加班日期（刷卡開始時間段所屬工作日）
	UFLiteralDate maxLeaveDate = getMaxLeaveDate(otRegVO, realDate); // 獲取最長可休日期

	OTSChainNode firstNode = OTSChainUtils.buildChainNodes(otRegVO.getPk_psndoc(), null, null, false, false, false,
		false, false);
	SegRuleTermVO beginTerm = getLastTermVO(firstNode, realDate, terms);

	for (SegRuleTermVO term : terms) {
	    if (othours.equals(UFDouble.ZERO_DBL)) {
		break;
	    }

	    if (beginTerm != null) {
		if (!term.equals(beginTerm)) {
		    continue;
		} else {
		    beginTerm = null;
		}
	    }

	    UFDouble start = term.getStartpoint();
	    UFDouble end = term.getEndpoint() == null ? new UFDouble(24) : term.getEndpoint();
	    UFDouble taxfreerate = term.getTaxfreeotrate();
	    UFDouble taxablerate = term.getTaxableotrate();
	    UFDouble seghours = end.sub(start); // 分段時長
	    UFDouble curSegTotalHours = UFDouble.ZERO_DBL; // 當前分段總時長
	    UFDouble curSegTaxfreeHours = UFDouble.ZERO_DBL; // 當前分段免稅時長
	    UFDouble curSegTaxableHours = UFDouble.ZERO_DBL; // 當前分段應稅時長
	    if (othours.doubleValue() >= seghours.doubleValue()) {
		// 加班時長大於分段長度，本次註冊時長取分段時長
		curSegTotalHours = seghours;
	    } else {
		// 加班時長小於分段長度，本次註冊時長取加班時長
		curSegTotalHours = othours;
	    }

	    othours = othours.sub(curSegTotalHours);

	    if (term.getIslimitscope() == null || !term.getIslimitscope().booleanValue()) {
		// 不計入加班上限統計，直接記為免稅時數
		curSegTaxfreeHours = curSegTotalHours;
		curSegTaxableHours = UFDouble.ZERO_DBL;
	    } else {
		// 先檢查分段依據上，該分段的應免稅設置
		if (term.getTaxflag().equals(TaxFlagEnum.TAXABLE.toIntValue())) {
		    curSegTaxfreeHours = UFDouble.ZERO_DBL;
		    curSegTaxableHours = curSegTotalHours;
		} else {
		    // 計入加班上限統計，檢查當日截止當日前一日的應稅加班時數
		    Map<String, UFDouble[]> psnSeghours = this.calculateTaxableByDate(otRegVO.getPk_org(),
			    new String[] { otRegVO.getPk_psndoc() }, realDate, realDate, curSegTotalHours);
		    curSegTaxfreeHours = psnSeghours.get(otRegVO.getPk_psndoc())[0];
		    curSegTaxableHours = psnSeghours.get(otRegVO.getPk_psndoc())[1];
		}
	    }

	    OTSChainNode curNode = new OTSChainNode();
	    SegDetailVO segvo = createNewSegDetail(otRegVO, realDate, rule, term, seghours, taxfreerate, taxablerate,
		    curSegTaxfreeHours, curSegTaxableHours, maxLeaveDate);

	    curNode.setNodeData(segvo);

	    // if (firstNode != null) {
	    // parentNode = OTSChainUtils.findParentNode(firstNode, curNode);
	    // } else {
	    // parentNode = OTSChainUtils.findParentNode(parentNode, curNode);
	    // }

	    if (parentNode == null) {
		curNode.setNextNode(null);
		curNode.setPriorNode(null);
		parentNode = curNode;
	    } else {
		OTSChainUtils.appendNode(parentNode, curNode);
		// parentNode = curNode;
	    }

	    totalSegHours = totalSegHours.add(curSegTotalHours);// 生成分段時長纍加
	}
	return OTSChainUtils.getFirstNode(parentNode);
    }

    private SegRuleTermVO getLastTermVO(OTSChainNode firstNode, UFLiteralDate realDate, SegRuleTermVO[] terms) {
	SegRuleTermVO retTerm = null;
	if (firstNode != null) {
	    OTSChainNode curNode = firstNode;
	    String pk_lastterm = null;
	    UFDouble lastHours = UFDouble.ZERO_DBL;
	    boolean fouldTerm = false;
	    while (curNode != null) {
		if (curNode.getNodeData() != null) {
		    if (curNode.getNodeData().getRegdate().equals(realDate)) {
			pk_lastterm = curNode.getNodeData().getPk_segruleterm();
			lastHours = curNode.getNodeData().getHours();
			for (SegRuleTermVO term : terms) {
			    if (term.getPk_segruleterm().equals(pk_lastterm)) {
				UFDouble ruleHours = term.getEndpoint() == null ? new UFDouble(24).sub(term
					.getStartpoint()) : term.getEndpoint().sub(term.getStartpoint());
				if (lastHours.doubleValue() < ruleHours.doubleValue()) {
				    retTerm = term;
				    retTerm.setStartpoint(retTerm.getStartpoint().add(lastHours));
				    fouldTerm = true;
				    break;
				}
			    }
			}
			if (fouldTerm) {
			    break;
			}
		    }
		}
		curNode = curNode.getNextNode();
	    }
	}
	return retTerm;
    }

    @SuppressWarnings("unchecked")
    private UFLiteralDate getMaxLeaveDate(OvertimeRegVO otRegVO, UFLiteralDate otRealDate) throws BusinessException {
	int settleType = SysInitQuery.getParaInt(otRegVO.getPk_org(), "TWHRT09");
	Collection<TimeRuleVO> rules = this.getBaseDao().retrieveByClause(TimeRuleVO.class,
		"pk_org='" + otRegVO.getPk_org() + "'");
	TimeRuleVO timeRule = null;
	if (rules != null && rules.size() > 0) {
	    timeRule = rules.toArray(new TimeRuleVO[0])[0];
	}

	if (timeRule == null) {
	    throw new BusinessException("当前组织未定义有效的考勤规则，请检查后重试。");
	}

	UFLiteralDate maxLeaveDate = null;

	if (settleType == 0) {
	    // 按加班日期往后N个月
	    UFDouble monthAfter = timeRule.getMonthafterotdate();
	    maxLeaveDate = otRealDate.getDateAfter(monthAfter.multiply(30).intValue());
	} else if (settleType == 1) {
	    // 按审批日期往后N个月
	    UFDouble monthAfter = timeRule.getMonthafterapproved();
	    if (otRegVO.getApprove_time() != null) {
		maxLeaveDate = new UFLiteralDate(otRegVO.getApprove_time()
			.getDateTimeAfter(monthAfter.multiply(30).sub(1).intValue()).toString().substring(0, 10));
	    } else {
		throw new BusinessException("加班可休日期按审核日期计算错误，请检查加班登记单审核日期。");
	    }
	} else if (settleType == 2) {
	    // 按固定周期（比对加班日期）
	    String startYearMonth = timeRule.getStartcycleyearmonth();
	    if (!StringUtils.isEmpty(startYearMonth) && startYearMonth.length() == 6) {
		String cyear = startYearMonth.substring(0, 4);
		String cmonth = startYearMonth.substring(4, 6);
		UFLiteralDate startDate = new UFLiteralDate(cyear + "-" + cmonth + "-01"); // 周期起始日
		maxLeaveDate = getCycleDate(timeRule.getMonthofcycle(), startDate);
	    } else {
		throw new BusinessException("加班可休日期按固定周期计算错误，请检查考勤规则起算年月设定（格式：YYYYMM）。");
	    }
	} else if (settleType == 3) {
	    // 按年资起算日（比对加班日期）
	    PsnOrgVO psnOrgVO = (PsnOrgVO) this.getBaseDao().retrieveByPK(PsnOrgVO.class, otRegVO.getPk_psnorg());
	    UFLiteralDate workAgeStartDate = (UFLiteralDate) psnOrgVO.getAttributeValue("workagestartdate"); // 年资起算日
	    if (workAgeStartDate != null) {
		maxLeaveDate = getCycleDate(new UFDouble(12), workAgeStartDate);
	    } else {
		throw new BusinessException("加班可休日期按年资起算日计算错误，请检查员工组织关系年资起算日设定。");
	    }
	} else {
	    throw new BusinessException("加班可休日期计算错误，请检查组织参数（TWHRT09）的设定。");
	}
	return maxLeaveDate;
    }

    private UFLiteralDate getCycleDate(UFDouble monthOfCycle, UFLiteralDate startDate) {
	UFLiteralDate maxLeaveDate;
	int wholeDays = new UFLiteralDate().getDaysAfter(startDate); // 周期起始日到加班当日天数
	UFDouble daysInOneCycle = monthOfCycle.multiply(30); // 单周期日数
	int passedCycles = wholeDays / daysInOneCycle.intValue(); // 下一周期所在周期数
	startDate = startDate.getDateAfter(daysInOneCycle.multiply(passedCycles).intValue()); // 下一周期的起始日
	maxLeaveDate = startDate.getDateBefore(startDate.getDay() - 1).getDateBefore(1);
	return maxLeaveDate;
    }

    private SegDetailVO createNewSegDetail(OvertimeRegVO vo, UFLiteralDate realRegDate, SegRuleVO rule,
	    SegRuleTermVO term, UFDouble seghours, UFDouble taxfreerate, UFDouble taxablerate, UFDouble hourstaxfree,
	    UFDouble hourstaxable, UFLiteralDate maxLeaveDate) throws BusinessException {
	if (vo.getApprove_time() == null) {
	    throw new BusinessException("加班登記審核日期錯誤，請檢查加班登記。");
	}
	SegDetailVO segvo = new SegDetailVO();
	segvo.setPk_group(vo.getPk_group());
	segvo.setPk_org(vo.getPk_org());
	segvo.setPk_org_v(vo.getPk_org_v());
	segvo.setPk_overtimereg(vo.getPk_overtimereg());
	segvo.setPk_psndoc(vo.getPk_psndoc());
	segvo.setPk_segrule(rule.getPk_segrule());
	segvo.setPk_segruleterm(term.getPk_segruleterm());
	segvo.setConsumedhours(UFDouble.ZERO_DBL);
	segvo.setConsumedhourstaxfree(UFDouble.ZERO_DBL);
	segvo.setConsumedhourstaxable(UFDouble.ZERO_DBL);
	segvo.setFrozenhours(UFDouble.ZERO_DBL);
	segvo.setFrozenhourstaxfree(UFDouble.ZERO_DBL);
	segvo.setFrozenhourstaxable(UFDouble.ZERO_DBL);
	UFDouble hourpay = getPsnHourPay(vo.getPk_psndoc(), vo.getOvertimebegindate(), DaySalaryEnum.TBMHOURSALARY);
	segvo.setHourlypay(hourpay == null ? UFDouble.ZERO_DBL : hourpay);
	segvo.setHourstaxfree(hourstaxfree);
	segvo.setHourstaxable(hourstaxable);
	segvo.setHours(segvo.getHourstaxfree().add(segvo.getHourstaxable()));
	segvo.setIscanceled(UFBoolean.FALSE);
	segvo.setIscompensation(vo.getIstorest());
	segvo.setHourstorest(vo.getToresthour());
	segvo.setIsconsumed(UFBoolean.FALSE);
	segvo.setIssettled(UFBoolean.FALSE);
	segvo.setMaketime(new UFDate());
	// mod Ares.Tank 2018-10-15 12:40:19 增加审批时间
	segvo.setApproveddate(new UFLiteralDate(vo.getApprove_time() == null ? null : vo.getApprove_time().toString()));

	// end
	PsndocVO psnVo = (PsndocVO) this.getBaseDao().retrieveByPK(PsndocVO.class, vo.getPk_psndoc());
	segvo.setNodecode(psnVo.getCode() + OTSChainUtils.SPLT + realRegDate.toString() + OTSChainUtils.SPLT
		+ rule.getCode() + OTSChainUtils.SPLT + String.valueOf(String.format("%02d", term.getSegno())));
	segvo.setNodename(MultiLangUtil.getSuperVONameOfCurrentLang(psnVo, PsndocVO.NAME, psnVo.getName())
		+ OTSChainUtils.SPLT + realRegDate.toString().replace("-", "") + OTSChainUtils.SPLT
		+ MultiLangUtil.getSuperVONameOfCurrentLang(rule, SegRuleVO.NAME, rule.getName()) + OTSChainUtils.SPLT
		+ String.valueOf(term.getSegno()));
	segvo.setTaxfreerate(taxfreerate == null ? UFDouble.ZERO_DBL : taxfreerate);
	segvo.setTaxablerate(taxablerate == null ? UFDouble.ZERO_DBL : taxablerate);
	segvo.setRegdate(realRegDate);
	segvo.setExpirydate(maxLeaveDate);
	segvo.setRemainhours(segvo.getHours());
	segvo.setRemainhourstaxfree(hourstaxfree);
	segvo.setRemainhourstaxable(hourstaxable);
	segvo.setRemainamounttaxfree(getOTAmount(segvo.getTaxfreerate(), segvo.getHourlypay(), segvo.getTaxfreerate(),
		null, -1));
	segvo.setRemainamounttaxable(getOTAmount(segvo.getTaxablerate(), segvo.getHourlypay(), segvo.getTaxablerate(),
		null, -1));
	segvo.setRemainamount(segvo.getRemainamounttaxfree().add(segvo.getRemainamounttaxable())); // 剩餘金額=剩餘金額（免稅）+剩餘金額（應稅）
	segvo.setRulehours(seghours);
	return segvo;
    }

    /**
     * 根據加班核定開始日期查詢加班實際歸屬班次的所屬日期
     * 
     * @param vo
     *            加班登記單
     * @return
     * @throws BusinessException
     */
    @SuppressWarnings("unchecked")
    private UFLiteralDate getShiftRegDate(OvertimeRegVO vo) throws BusinessException {
	UFLiteralDate rtnDate = vo.getBegindate();
	Collection<PsnCalendarVO> psncals = this.getBaseDao().retrieveByClause(
		PsnCalendarVO.class,
		"pk_psndoc='" + vo.getPk_psndoc() + "' and calendar between '"
			+ vo.getOvertimebegindate().getDateBefore(3) + "' and '"
			+ vo.getOvertimebegindate().getDateAfter(3) + "'");
	if (psncals != null && psncals.size() > 0) {
	    for (PsnCalendarVO psncal : psncals) {
		if (psncal.getPk_shift() != null) {
		    ShiftVO shiftvo = (ShiftVO) this.getBaseDao().retrieveByPK(ShiftVO.class, psncal.getPk_shift());
		    if (shiftvo != null) {
			UFDateTime startDT = null;
			if (shiftvo.getTimebeginday() == 0) {
			    startDT = new UFDateTime(psncal.getCalendar().toString() + " " + shiftvo.getBegintime());
			} else {
			    startDT = new UFDateTime(psncal.getCalendar().getDateAfter(1).toString() + " "
				    + shiftvo.getBegintime());
			}

			UFDateTime endDT = null;
			if (shiftvo.getTimeendday() == 0) {
			    endDT = new UFDateTime(psncal.getCalendar().toString() + " " + shiftvo.getEndtime());
			} else {
			    endDT = new UFDateTime(psncal.getCalendar().getDateAfter(1).toString() + " "
				    + shiftvo.getEndtime());
			}

			if (vo.getOvertimebegintime().before(endDT) && vo.getOvertimebegintime().after(startDT)) {
			    rtnDate = psncal.getCalendar();
			}
		    }
		}
	    }
	}
	return rtnDate;
    }

    private UFDouble getOTAmount(UFDouble otRate, UFDouble hourlypay, UFDouble hours, SegDetailVO detailVO,
	    int daySalType) throws BusinessException {
	otRate = otRate == null ? UFDouble.ZERO_DBL : otRate;
	hourlypay = hourlypay == null ? UFDouble.ZERO_DBL : hourlypay;
	hours = hours == null ? UFDouble.ZERO_DBL : hours;
	if (detailVO != null) {
	    hourlypay = getPsnHourPay(detailVO.getPk_psndoc(), detailVO.getRegdate(), daySalType);
	    detailVO.setHourlypay(hourlypay);
	}
	UFDouble amount = hourlypay.multiply(otRate).multiply(hours);
	return amount;
    }

    private UFDouble getPsnHourPay(String pk_psndoc, UFLiteralDate overtimebegindate, int daySalType)
	    throws BusinessException {
	IWadaysalaryQueryService dayPaySvc = NCLocator.getInstance().lookup(IWadaysalaryQueryService.class);

	Map<String, HashMap<UFLiteralDate, UFDouble>> dayPayMap = dayPaySvc.getTotalTbmDaySalaryMap(
		new String[] { pk_psndoc }, overtimebegindate, overtimebegindate, daySalType);
	if (dayPayMap == null || dayPayMap.size() == 0 || dayPayMap.get(pk_psndoc) == null
		|| dayPayMap.get(pk_psndoc).get(overtimebegindate) == null) {
	    // throw new BusinessException("人員時薪取值錯誤：人員時薪為空");
	    return UFDouble.ZERO_DBL; // 未取到日薪的，暫時返回0
	}
	return dayPayMap.get(pk_psndoc).get(overtimebegindate);
    }

    @SuppressWarnings("unchecked")
    private AggSegRuleVO getSegRuleAggVO(String pk_segrule) throws BusinessException {
	SegRuleVO ruleVO = (SegRuleVO) this.getBaseDao().retrieveByPK(SegRuleVO.class, pk_segrule);

	if (ruleVO == null) {
	    throw new BusinessException("數據錯誤：加班分段規則已被刪除");
	}

	Collection<SegRuleTermVO> segTerms = this.getBaseDao().retrieveByClause(SegRuleTermVO.class,
		"pk_segrule='" + pk_segrule + "' and dr=0", "segno");
	if (segTerms == null || segTerms.size() == 0) {
	    throw new BusinessException("數據錯誤：加班分段規則明細已被刪除");
	}

	AggSegRuleVO aggVo = new AggSegRuleVO();
	aggVo.setParent(ruleVO);
	aggVo.setChildrenVO(segTerms.toArray(new SegRuleTermVO[0]));

	return aggVo;
    }

    @Override
    public void regOvertimeSegDetailConsume(LeaveRegVO[] leaveRegVOs) throws BusinessException {
	if (leaveRegVOs != null && leaveRegVOs.length > 0) {
	    for (LeaveRegVO vo : leaveRegVOs) {
		UFBoolean isEnabled = SysInitQuery.getParaBoolean(vo.getPk_org(), "TBMOTSEG");
		if (isEnabled == null || !isEnabled.booleanValue()) {
		    return;
		}

		// 取業務參數定義的加班分段休假類別（加班轉補休）
		LeaveTypeCopyVO lvTypeVO = getLeaveTypeVOs(vo.getPk_org());

		if (lvTypeVO.getPk_timeitemcopy().equals(vo.getPk_leavetypecopy())) {
		    // 取已註冊未消耗的加班分段明細
		    // 取當前人員過濾節點（邏輯鏈表：轉補休，未作廢，未核消完畢，未結算）
		    OTSChainNode psnNode = OTSChainUtils.buildChainNodes(vo.getPk_psndoc(), null, null, true, false,
			    true, true, true);
		    SegDetailVO[] segDetailBeConsumed = OTSChainUtils.getAllNodeData(psnNode);

		    if (segDetailBeConsumed == null || segDetailBeConsumed.length == 0) {
			throw new BusinessException("消耗加班分段明細失敗：未找到可用的加班分段明細。");
		    }

		    consumeSegDetailHours(segDetailBeConsumed, vo);
		}
	    }
	}
    }

    @Override
    public void updateOvertimeSegDetailConsume(LeaveRegVO[] leaveRegVOs) throws BusinessException {
	if (leaveRegVOs != null && leaveRegVOs.length > 0) {
	    deleteOvertimeSegDetailConsume(leaveRegVOs);
	    regOvertimeSegDetailConsume(leaveRegVOs);
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deleteOvertimeSegDetailConsume(LeaveRegVO[] leaveRegVOs) throws BusinessException {
	if (leaveRegVOs != null && leaveRegVOs.length > 0) {
	    for (LeaveRegVO vo : leaveRegVOs) {
		Collection<SegDetailConsumeVO> consumeVOs = this.getBaseDao().retrieveByClause(
			SegDetailConsumeVO.class, "pk_leavereg='" + vo.getPk_leavereg() + "'");
		for (SegDetailConsumeVO consumeVO : consumeVOs) {
		    SegDetailVO detailVO = (SegDetailVO) this.getBaseDao().retrieveByPK(SegDetailVO.class,
			    consumeVO.getPk_segdetail());
		    Map<String, UFDouble[]> taxhourssplit = calculateTaxableByDate(vo.getPk_org(),
			    new String[] { vo.getPk_psndoc() }, detailVO.getRegdate(), detailVO.getRegdate()
				    .getDateBefore(1), vo.getLeavehour());
		    UFDouble[] values = taxhourssplit.get(vo.getPk_psndoc());
		    detailVO.setRemainhourstaxfree(detailVO.getRemainhourstaxfree().add(values[0]));
		    detailVO.setRemainhourstaxable(detailVO.getRemainhourstaxable().add(values[1]));
		    detailVO.setRemainhours(detailVO.getRemainhourstaxfree().add(detailVO.getRemainhourstaxable()));

		    detailVO.setRemainamounttaxfree(getOTAmount(detailVO.getTaxfreerate(), detailVO.getHourlypay(),
			    detailVO.getRemainhourstaxfree(), detailVO, DaySalaryEnum.TAXFREEHOURSALARY));
		    detailVO.setRemainamounttaxable(getOTAmount(detailVO.getTaxablerate(), detailVO.getHourlypay(),
			    detailVO.getRemainhourstaxable(), detailVO, DaySalaryEnum.TAXABLEHOURSALARY));
		    detailVO.setRemainamount(detailVO.getRemainamounttaxfree().add(detailVO.getRemainamounttaxable()));

		    detailVO.setConsumedhourstaxfree(detailVO.getConsumedhourstaxfree().sub(values[0]));
		    detailVO.setConsumedhourstaxable(detailVO.getConsumedhourstaxable().sub(values[1]));
		    detailVO.setConsumedhours(detailVO.getConsumedhourstaxfree()
			    .add(detailVO.getConsumedhourstaxable()));

		    this.getBaseDao().updateVO(detailVO);
		    this.getBaseDao().deleteVO(consumeVO);
		}
	    }
	}
    }

    private void consumeSegDetailHours(SegDetailVO[] segDetailVOs, LeaveRegVO vo) throws BusinessException {
	List<AggSegDetailVO> aggvos = new ArrayList<AggSegDetailVO>();
	UFDouble unConsumedLeaveHours = vo.getLeavehour();
	for (SegDetailVO segDetail : segDetailVOs) {
	    // 先進先出匹配
	    if (!unConsumedLeaveHours.equals(UFDouble.ZERO_DBL)) {
		// 本筆核銷時數
		UFDouble curConsumedHours = UFDouble.ZERO_DBL;
		if (segDetail.getRemainhours().doubleValue() >= unConsumedLeaveHours.doubleValue()) {
		    // 剩餘小時數大於等於本次核銷時數=本次核銷全部在本條明細完成
		    curConsumedHours = unConsumedLeaveHours;
		    unConsumedLeaveHours = UFDouble.ZERO_DBL;
		} else {
		    // 剩餘小時數小於本次核銷時數=本次核銷在本條明細完成部分
		    curConsumedHours = segDetail.getRemainhours();
		    // 餘下部分繼續向後核銷
		    unConsumedLeaveHours = vo.getLeavehour().sub(segDetail.getRemainhours());
		}

		consumeSegDetailHours(curConsumedHours, segDetail); // 處理本筆核銷
		SegDetailConsumeVO consumeVO = getNewConsumeVO(vo, segDetail); // 生成核銷明細
		AggSegDetailVO aggvo = new AggSegDetailVO();
		segDetail.setStatus(VOStatus.UPDATED);
		aggvo.setParent(segDetail);
		consumeVO.setStatus(VOStatus.NEW);
		aggvo.setChildren(SegDetailConsumeVO.class, new SegDetailConsumeVO[] { consumeVO });
		aggvos.add(aggvo);
		// unConsumedLeaveHours =
		// unConsumedLeaveHours.sub(curConsumedHours);
	    }
	}

	if (aggvos.size() > 0) {
	    new SegdetailMaintainImpl().update(aggvos.toArray(new AggSegDetailVO[0]));
	}
    }

    private SegDetailConsumeVO getNewConsumeVO(LeaveRegVO vo, SegDetailVO segDetail) {
	SegDetailConsumeVO consumeVO = new SegDetailConsumeVO();
	consumeVO.setPk_group(segDetail.getPk_group());
	consumeVO.setPk_org(segDetail.getPk_org());
	consumeVO.setPk_org_v(segDetail.getPk_org_v());
	consumeVO.setPk_segdetail(segDetail.getPk_segdetail());
	consumeVO.setPk_leavereg(vo.getPk_leavereg());
	consumeVO.setBizdate(vo.getLeavebegindate());
	consumeVO.setBiztype(vo.getBillType());
	return consumeVO;
    }

    // 先消耗免稅部分，再消耗應稅部分
    private void consumeSegDetailHours(UFDouble unConsumedHours, SegDetailVO segDetail) throws BusinessException {
	SegRuleTermVO term = (SegRuleTermVO) this.getBaseDao().retrieveByPK(SegRuleTermVO.class,
		segDetail.getPk_segruleterm());

	// 免稅部分
	if (segDetail.getRemainhourstaxfree().doubleValue() > 0) {
	    if (unConsumedHours.doubleValue() <= segDetail.getRemainhourstaxfree().doubleValue()) {
		// 只消耗免稅部分
		segDetail.setRemainhourstaxfree(segDetail.getRemainhourstaxfree().sub(unConsumedHours));
		segDetail.setConsumedhourstaxfree(segDetail.getConsumedhourstaxfree().add(unConsumedHours));
		segDetail.setRemainhours(segDetail.getRemainhours().sub(unConsumedHours));
		segDetail.setConsumedhours(segDetail.getConsumedhours().add(unConsumedHours));
	    } else {
		// 消耗免稅全部後，剩餘消耗應稅部分
		UFDouble tmpHours = unConsumedHours.sub(segDetail.getRemainamounttaxfree());
		segDetail.setRemainhourstaxfree(segDetail.getRemainhourstaxfree().sub(tmpHours));
		segDetail.setConsumedhourstaxfree(segDetail.getConsumedhourstaxfree().add(tmpHours));
		segDetail.setRemainhours(segDetail.getRemainhours().sub(tmpHours));
		segDetail.setConsumedhours(segDetail.getConsumedhours().add(tmpHours));

		tmpHours = unConsumedHours.sub(tmpHours);
		segDetail.setRemainhourstaxable(segDetail.getRemainhourstaxable().sub(tmpHours));
		segDetail.setConsumedhourstaxable(segDetail.getConsumedhourstaxable().add(tmpHours));
		segDetail.setRemainhours(segDetail.getRemainhours().sub(tmpHours));
		segDetail.setConsumedhours(segDetail.getConsumedhours().add(tmpHours));
	    }
	} else {
	    // 只消耗應稅部分
	    segDetail.setRemainhourstaxable(segDetail.getRemainhourstaxable().sub(unConsumedHours));
	    segDetail.setConsumedhourstaxable(segDetail.getConsumedhourstaxable().add(unConsumedHours));
	    segDetail.setRemainhours(segDetail.getRemainhours().sub(unConsumedHours));
	    segDetail.setConsumedhours(segDetail.getConsumedhours().add(unConsumedHours));
	}

	// 計算加班費
	segDetail.setRemainamounttaxfree(getOTAmount(term.getTaxfreeotrate(), segDetail.getHourlypay(),
		segDetail.getRemainhourstaxfree(), segDetail, DaySalaryEnum.TAXFREEHOURSALARY));
	segDetail.setRemainamounttaxable(getOTAmount(term.getTaxableotrate(), segDetail.getHourlypay(),
		segDetail.getRemainhourstaxable(), segDetail, DaySalaryEnum.TAXABLEHOURSALARY));
	segDetail.setRemainamount(segDetail.getRemainamounttaxfree().add(segDetail.getRemainamounttaxable()));

	unConsumedHours = UFDouble.ZERO_DBL;
	if (segDetail.getRemainhours().doubleValue() == 0) {
	    // 設置已消耗完畢
	    segDetail.setIsconsumed(UFBoolean.TRUE);
	}
    }

    Map<String, LeaveTypeCopyVO> leaveTypeVOMap = null;

    private LeaveTypeCopyVO getLeaveTypeVOs(String pk_org) throws BusinessException {
	if (leaveTypeVOMap == null) {
	    leaveTypeVOMap = new HashMap<String, LeaveTypeCopyVO>();
	}

	if (!leaveTypeVOMap.containsKey(pk_org)) {
	    String pk_leavetypecopy = SysInitQuery.getParaString(pk_org, "TWHRT08");

	    if (StringUtils.isEmpty(pk_leavetypecopy)) {
		throw new BusinessException("參數取值失敗：請在業務參數設置-組織中設定本組織 [加班補休指定假別] 參數。");
	    }
	    LeaveTypeCopyVO vo = (LeaveTypeCopyVO) this.getBaseDao().retrieveByPK(LeaveTypeCopyVO.class,
		    pk_leavetypecopy);
	    leaveTypeVOMap.put(pk_org, vo);
	}

	return leaveTypeVOMap.get(pk_org);
    }

    @SuppressWarnings("unchecked")
    private TimeRuleVO getTimeRule(String pk_org) throws BusinessException {
	// 取該員工考勤規則
	Collection<TimeRuleVO> timerule = this.getBaseDao().retrieveByClause(TimeRuleVO.class,
		"pk_org='" + pk_org + "' and dr=0");
	if (timerule == null) {
	    throw new BusinessException("取考勤規則錯誤：指定組織下未定義考勤規則");
	}

	return timerule.toArray(new TimeRuleVO[0])[0];
    }

    Map<String, Integer> sysManageScope = new HashMap<String, Integer>();
    Map<String, TBMPsndocVO> psndocMap = new HashMap<String, TBMPsndocVO>();

    @Override
    public Map<String, UFDouble[]> calculateTaxableByDate(String pk_org, String[] pk_psndocs, UFLiteralDate startDate,
	    UFLiteralDate endDate, UFDouble curNodeHours) throws BusinessException {
	Map<String, UFDouble[]> ret = new HashMap<String, UFDouble[]>();
	if (pk_psndocs != null && pk_psndocs.length > 0) {
	    for (String pk_psndoc : pk_psndocs) {
		UFDouble[] retValues = new UFDouble[2];
		retValues[0] = UFDouble.ZERO_DBL;
		retValues[1] = UFDouble.ZERO_DBL;
		UFDouble totalTaxFreeAmount = UFDouble.ZERO_DBL;
		UFDouble totalTaxableAmount = UFDouble.ZERO_DBL;
		UFDouble totalHours = UFDouble.ZERO_DBL;

		OTSChainNode curNode = OTSChainUtils.buildChainNodes(pk_psndoc, null, null, false, true, true, true,
			true);
		// 考勤規則
		TimeRuleVO timerule = getTimeRule(pk_org);

				UFLiteralDate regDate = null;
				if (curNode == null || curNode.getNodeData() == null) {
					regDate = startDate;
				} else {
					regDate = curNode.getNodeData().getRegdate();
				}
				// 取該員工結算週期
				OvertimeLimitScopeEnum curStatScope = getPsnStatScope(pk_org, pk_psndoc, regDate);

		// 一個週期內加班不能超過的時數
		UFDouble taxFreeLimitHours = timerule.getCtrlothours();
		// 一或三個週期內加班不能超過的總時數
		UFDouble totalTaxFreeLimitHours = taxFreeLimitHours;

		PeriodVO[] threePeriods = null;
		PeriodVO periodCurrent = getPeriodService().queryByDate(pk_org, startDate);
		if (periodCurrent == null) {
		    throw new BusinessException("取當前期間錯誤");
		}

		if (curStatScope.equals(OvertimeLimitScopeEnum.THREEMONTH)) {
		    taxFreeLimitHours = timerule.getCtrlothours3();
		    totalTaxFreeLimitHours = timerule.getCtrlothours3();
		    threePeriods = getThreePeriodVOs(pk_org, startDate, pk_psndoc, periodCurrent);
		} else {
		    threePeriods = new PeriodVO[1];
		    threePeriods[0] = periodCurrent;
		}

		UFLiteralDate sumStartDate = threePeriods[0].getBegindate();
		UFLiteralDate sumEndDate = threePeriods.length == 3 ? threePeriods[2].getEnddate() : threePeriods[0]
			.getEnddate();

		if (endDate != null) {
		    sumEndDate = sumEndDate.after(endDate) ? endDate : sumEndDate;
		}

		// 是否免稅
		boolean isTaxFree = true;
		if (curNode != null) {
		    while (curNode != null) {
			// 當前節點時數不為空，當前節點明細為空的，即為最後一次進入
			if (curNode.getNodeData() == null && curNodeHours != null) {
			    if (!isTaxFree) {
				// 已進入應稅範圍
				totalTaxFreeAmount = UFDouble.ZERO_DBL;
				totalTaxableAmount = curNodeHours;
			    } else {
				if (totalHours.add(curNodeHours).doubleValue() <= totalTaxFreeLimitHours.doubleValue()) {
				    // 加總後仍在免稅時數範圍內
				    totalTaxFreeAmount = curNodeHours;
				    totalTaxableAmount = UFDouble.ZERO_DBL;
				} else {
				    // 正好超過免稅時數
				    totalTaxableAmount = totalHours.add(curNodeHours).sub(totalTaxFreeLimitHours);
				    totalTaxFreeAmount = curNodeHours.sub(totalTaxableAmount);
				}
			    }
			    break;
			}

			if (curNode.getNodeData().getPk_org().equals(pk_org)) {
			    // 在日期範圍內
			    if (curNode.getNodeData().getRegdate().isSameDate(sumStartDate)
				    || curNode.getNodeData().getRegdate().after(sumStartDate)
				    && (curNode.getNodeData().getRegdate().isSameDate(sumEndDate) || curNode
					    .getNodeData().getRegdate().before(sumEndDate))) {
				UFDouble curHours = getHoursInScope(curNode.getNodeData());

				// 纍加加班時數
				totalHours = totalHours.add(curHours);
				if (isTaxFree) {
				    if (totalHours.doubleValue() <= totalTaxFreeLimitHours.doubleValue()) {
					// 在免稅時數範圍內的，纍計到免稅加班費
					totalTaxFreeAmount = totalTaxFreeAmount.add(getOTAmount(curNode.getNodeData()
						.getTaxfreerate(), curNode.getNodeData().getHourlypay(), curNode
						.getNodeData().getHourstaxfree(), curNode.getNodeData(),
						DaySalaryEnum.TBMHOURSALARY));
				    } else {
					// 正好超過免稅時數
					// 超過時數差 * 時薪 * 應稅加班費率 = 第一筆應稅加班費
					totalTaxableAmount = getOTAmount(curNode.getNodeData().getTaxablerate(),
						curNode.getNodeData().getHourlypay(),
						totalHours.sub(totalTaxFreeLimitHours), curNode.getNodeData(),
						DaySalaryEnum.TBMHOURSALARY);
					// 本筆未消耗加班費 - 第一筆應稅加班費 = 最後一筆免稅加班費
					totalTaxFreeAmount = totalTaxFreeAmount.add(getOTAmount(curNode.getNodeData()
						.getTaxfreerate(), curNode.getNodeData().getHourlypay(), curHours
						.sub(totalHours.sub(totalTaxFreeLimitHours)), curNode.getNodeData(),
						DaySalaryEnum.TBMHOURSALARY));
					isTaxFree = false;
				    }
				} else {
				    // 超過免稅時數範圍的，纍計到應稅加班費
				    totalTaxableAmount = totalTaxableAmount.add(getOTAmount(curNode.getNodeData()
					    .getTaxablerate(), curNode.getNodeData().getHourlypay(), curNode
					    .getNodeData().getHourlypay(), curNode.getNodeData(),
					    DaySalaryEnum.TBMHOURSALARY));
				}
			    }
			    curNode = curNode.getNextNode();

			    if (curNodeHours != null && curNode == null) {
				curNode = new OTSChainNode();
			    }
			}
		    }
		    retValues[0] = totalTaxFreeAmount;
		    retValues[1] = totalTaxableAmount;
		} else {
		    if (curNodeHours != null) {
			if (!isTaxFree) {
			    // 已進入應稅範圍
			    totalTaxFreeAmount = UFDouble.ZERO_DBL;
			    totalTaxableAmount = curNodeHours;
			} else {
			    if (totalHours.add(curNodeHours).doubleValue() <= totalTaxFreeLimitHours.doubleValue()) {
				// 加總後仍在免稅時數範圍內
				totalTaxFreeAmount = curNodeHours;
				totalTaxableAmount = UFDouble.ZERO_DBL;
			    } else {
				// 正好超過免稅時數
				totalTaxableAmount = totalHours.add(curNodeHours).sub(totalTaxFreeLimitHours);
				totalTaxFreeAmount = curNodeHours.sub(totalTaxableAmount);
			    }
			}
			retValues[0] = totalTaxFreeAmount;
			retValues[1] = totalTaxableAmount;
		    }
		}
		ret.put(pk_psndoc, retValues);
	    }
	}
	return ret;
    }

    private PeriodVO[] getThreePeriodVOs(String pk_org, UFLiteralDate startDate, String pk_psndoc,
	    PeriodVO periodCurrent) throws BusinessException {
	PeriodVO[] threePeriods;
	TBMPsndocVO psndoc = psndocMap.get(pk_psndoc);
	String startPeriod = "";
	// 起算期間
	try {
	    startPeriod = SysInitQuery.getParaString(pk_org, "TWHRT07"); // 加班校驗起始年月
	} catch (BusinessException e) {
	    Logger.error(e.getMessage());
	}

	String strWhere = "pk_org='" + pk_org + "' ";

	if (!StringUtils.isEmpty(startPeriod)) {
	    strWhere += " and timeyear='" + startPeriod.substring(0, 4) + "' and timemonth='"
		    + startPeriod.substring(4, 6) + "'";
	} else {
	    strWhere += " and '" + startDate.toString() + "' between begindate and enddate ";
	}
	Collection<PeriodVO> periodStart = this.getBaseDao().retrieveByClause(PeriodVO.class, strWhere);
	if (periodStart == null || periodStart.size() == 0) {
	    throw new BusinessException("無法確定加班上限統計起算期間。");
	}

	int startMonth = Integer.valueOf(periodStart.toArray(new PeriodVO[0])[0].getTimemonth());
	int currentMonth = startDate.getMonth();

	threePeriods = new PeriodVO[3];
	if (startMonth % 3 == currentMonth % 3) {
	    // 0:0 1:1 2:2 後補2個
	    // 當前期間為起始期間，後補兩個期間
	    threePeriods[0] = periodCurrent;
	    threePeriods[1] = this.getPeriodService().queryNextPeriod(pk_org, threePeriods[0].getBegindate());
	    threePeriods[2] = this.getPeriodService().queryNextPeriod(pk_org, threePeriods[1].getBegindate());
	} else if (((currentMonth % 3) - (startMonth % 3) == 1) || (currentMonth % 3 + 3) - (startMonth % 3) == 1) {
	    // 0:1 1:2 2:0 前後各1
	    // 當前期間為中間期間，前後各補一個期間
	    threePeriods[1] = periodCurrent;
	    threePeriods[0] = this.getPeriodService().queryPreviousPeriod(pk_org, threePeriods[1].getBegindate());
	    threePeriods[2] = this.getPeriodService().queryNextPeriod(pk_org, threePeriods[1].getBegindate());
	} else {
	    // 0:2 1:0 2:1 前補2個
	    // 當前期間為最後一個期間，前補兩個期間
	    threePeriods[2] = periodCurrent;
	    threePeriods[1] = this.getPeriodService().queryPreviousPeriod(pk_org, threePeriods[2].getBegindate());
	    threePeriods[0] = this.getPeriodService().queryPreviousPeriod(pk_org, threePeriods[1].getBegindate());
	}
	return threePeriods;
    }

    Map<String, SegRuleTermVO> mapSegTerm = new HashMap<String, SegRuleTermVO>();

    private UFDouble getHoursInScope(SegDetailVO segdetail) throws BusinessException {
	SegRuleTermVO term = null;
	if (!mapSegTerm.containsKey(segdetail.getPk_segruleterm())) {
	    term = (SegRuleTermVO) this.getBaseDao().retrieveByPK(SegRuleTermVO.class, segdetail.getPk_segruleterm());
	} else {
	    term = mapSegTerm.get(segdetail.getPk_segruleterm());
	}

	UFDouble rtn = UFDouble.ZERO_DBL;
	if (term.getIslimitscope() != null && term.getIslimitscope().booleanValue()) {
	    // 計入加班上限統計
	    rtn = segdetail.getHours();
	}
	return rtn;
    }

    @SuppressWarnings("unchecked")
    private OvertimeLimitScopeEnum getPsnStatScope(String pk_org, String pk_psndoc, UFLiteralDate regDate)
	    throws BusinessException {
	int chkScope = -1;

	if (!psndocMap.containsKey(pk_psndoc)) {
	    Collection<TBMPsndocVO> psndoc = this.getBaseDao().retrieveByClause(
		    TBMPsndocVO.class,
		    " pk_org='" + pk_org + "' and pk_psndoc='" + pk_psndoc + "' and '" + regDate
			    + "' between begindate and enddate ");
	    if (psndoc != null && psndoc.size() > 0) {
		psndocMap.put(pk_psndoc, psndoc.toArray(new TBMPsndocVO[0])[0]);
	    }
	}

	if (psndocMap.containsKey(pk_psndoc)) {
	    chkScope = psndocMap.get(pk_psndoc).getOvertimecontrol();
	}

	if (chkScope == 1) {
	    return OvertimeLimitScopeEnum.ONEMONTH;
	} else if (chkScope == 2) {
	    return OvertimeLimitScopeEnum.THREEMONTH;
	}

	throw new BusinessException("無法找到員工的加班上限統計範圍");
    }

    @Override
    public Map<String, UFDouble[]> settleByFixSalary(Map<String, UFDouble> psnFixSalary, UFLiteralDate startDate,
	    UFLiteralDate endDate) throws BusinessException {
	// TODO 自動產生的方法 Stub
	return null;
    }

    @Override
    public Map<String, UFDouble> calculateTaxFreeAmountByPeriod(String pk_org, String[] pk_psndocs, String cyear,
	    String cperiod) throws BusinessException {
	PeriodVO period = getPeriodService().queryByYearMonth(pk_org, cyear, cperiod);

	Map<String, UFDouble[]> taxAmounts = calculateTaxableByDate(pk_org, pk_psndocs, period.getBegindate(),
		period.getEnddate(), null);

	Map<String, UFDouble> ret = new HashMap<String, UFDouble>();
	if (taxAmounts != null && taxAmounts.size() > 0) {
	    for (Entry<String, UFDouble[]> amount : taxAmounts.entrySet()) {
		ret.put(amount.getKey(), amount.getValue()[0]);
	    }
	}
	return ret;
    }

    @Override
    public Map<String, UFDouble> calculateTaxableAmountByPeriod(String pk_org, String[] pk_psndocs, String cyear,
	    String cperiod) throws BusinessException {
	PeriodVO period = getPeriodService().queryByYearMonth(pk_org, cyear, cperiod);

	Map<String, UFDouble[]> taxAmounts = calculateTaxableByDate(pk_org, pk_psndocs, period.getBegindate(),
		period.getEnddate(), null);

	Map<String, UFDouble> ret = new HashMap<String, UFDouble>();
	if (taxAmounts != null && taxAmounts.size() > 0) {
	    for (Entry<String, UFDouble[]> amount : taxAmounts.entrySet()) {
		ret.put(amount.getKey(), amount.getValue()[1]);
	    }
	}
	return ret;
    }

    @Override
    public Map<String, UFDouble> getHoursToRestByScope(UFLiteralDate startDate, UFLiteralDate endDate,
	    String[] pk_psndocs) throws BusinessException {
	return getSegDetailSummary(pk_psndocs, startDate, endDate, SegDetailVO.HOURSTOREST);
    }

    @Override
    public Map<String, UFDouble> getHoursToRestByPeriod(String pk_org, String cyear, String cperiod, String[] pk_psndocs)
	    throws BusinessException {
	Map<String, UFDouble> ret = new HashMap<String, UFDouble>();
	if (pk_psndocs != null && pk_psndocs.length > 0) {
	    PeriodVO period = getPeriodService().queryByYearMonth(pk_org, cyear, cperiod);

	    if (period != null) {
		ret = getHoursToRestByScope(period.getBegindate(), period.getEnddate(), pk_psndocs);
	    }
	}
	return ret;
    }

    @Override
    public Map<String, Map<QueryValueTypeEnum, UFDouble>> getOvertimeHours(String pk_org, String[] pk_psndocs,
	    UFLiteralDate overtimeDate) throws BusinessException {
	Map<String, Map<QueryValueTypeEnum, UFDouble>> ret = new HashMap<String, Map<QueryValueTypeEnum, UFDouble>>();

	if (pk_psndocs != null) {
	    for (String pk_psndoc : pk_psndocs) {
		OTSChainNode curNode = OTSChainUtils.buildChainNodes(pk_psndoc, overtimeDate, null, false, true, true,
			false, true);

		UFDouble normalHours = UFDouble.ZERO_DBL;
		UFDouble offdayHours = UFDouble.ZERO_DBL;
		UFDouble holidayHours = UFDouble.ZERO_DBL;
		UFDouble nationalHours = UFDouble.ZERO_DBL;
		UFDouble totalFee = UFDouble.ZERO_DBL;

		while (curNode != null) {
		    if (curNode.getNodeData().getPk_org().equals(pk_org)) {
			SegRuleVO rule = (SegRuleVO) this.getBaseDao().retrieveByPK(SegRuleVO.class,
				curNode.getNodeData().getPk_segrule());
			if (CalendarDateTypeEnum.HOLIDAY.toIntValue() == rule.getDatetype()) {
			    // 例假日
			    holidayHours = holidayHours.add(curNode.getNodeData().getRemainhours());
			} else if (CalendarDateTypeEnum.OFFDAY.toIntValue() == rule.getDatetype()) {
			    // 休息日
			    offdayHours = offdayHours.add(curNode.getNodeData().getRemainhours());
			} else if (CalendarDateTypeEnum.NORMAL.toIntValue() == rule.getDatetype()) {
			    // 平日
			    normalHours = normalHours.add(curNode.getNodeData().getRemainhours());
			} else if (CalendarDateTypeEnum.NATIONALDAY.toIntValue() == rule.getDatetype()) {
			    // 國定假日
			    nationalHours = nationalHours.add(curNode.getNodeData().getRemainhours());
			}
			totalFee = totalFee.add(curNode.getNodeData().getRemainamount());
		    }
		    curNode = curNode.getNextNode();
		}

		Map<QueryValueTypeEnum, UFDouble> hoursMap = new HashMap<QueryValueTypeEnum, UFDouble>();
		hoursMap.put(QueryValueTypeEnum.HOLIDAY, holidayHours);
		hoursMap.put(QueryValueTypeEnum.OFFDAY, offdayHours);
		hoursMap.put(QueryValueTypeEnum.NATIONALDAY, nationalHours);
		hoursMap.put(QueryValueTypeEnum.NORMAL, normalHours);
		hoursMap.put(QueryValueTypeEnum.ALL, holidayHours.add(offdayHours).add(nationalHours).add(normalHours));
		hoursMap.put(QueryValueTypeEnum.TOTALFEE, totalFee);
		ret.put(pk_psndoc, hoursMap);
	    }
	}
	return ret;
    }

    public BaseDAO getBaseDao() {
	if (baseDao == null) {
	    baseDao = new BaseDAO();
	}
	return baseDao;
    }

    public IPeriodQueryService getPeriodService() {
	if (periodService == null) {
	    periodService = (IPeriodQueryService) NCLocator.getInstance().lookup(IPeriodQueryService.class);
	}
	return periodService;
    }

    public Map<String, OTSChainNode> getAllNode() {
	if (allNode == null) {
	    allNode = new HashMap<String, OTSChainNode>();
	}
	return allNode;
    }

    @Override
    public Map<String, UFDouble> getOvertimeHoursByType(String pk_org, String[] pk_psndocs, UFLiteralDate overtimeDate,
	    String pk_overtimetype) throws BusinessException {
	return getOvertimeHours(pk_org, pk_psndocs, overtimeDate, pk_overtimetype, false);
    }

    @Override
    public Map<String, UFDouble> getOvertimeToRestHoursByType(String pk_org, String[] pk_psndocs,
	    UFLiteralDate overtimeDate, String pk_overtimetype) throws BusinessException {
	return getOvertimeHours(pk_org, pk_psndocs, overtimeDate, pk_overtimetype, true);
    }

    private Map<String, UFDouble> getOvertimeHours(String pk_org, String[] pk_psndocs, UFLiteralDate overtimeDate,
	    String pk_overtimetype, boolean isToRest) throws BusinessException {
	Map<String, UFDouble> ret = new HashMap<String, UFDouble>();
	if (pk_psndocs != null && pk_psndocs.length > 0) {
	    for (String pk_psndoc : pk_psndocs) {
		OTSChainNode curNode = OTSChainUtils.buildChainNodes(pk_psndoc, overtimeDate, null, isToRest, false,
			true, true, true);
		UFDouble otHours = UFDouble.ZERO_DBL;
		Map<String, String> pkTypes = new HashMap<String, String>();
		if (curNode != null) {
		    while (curNode != null) {
			// 當前節點時數不為空，當前節點明細為空的，即為最後一次進入
			if (curNode.getNodeData() != null) {
			    if (!pkTypes.containsKey(curNode.getNodeData().getPk_overtimereg())) {
				OvertimeRegVO otvo = (OvertimeRegVO) this.getBaseDao().retrieveByPK(
					OvertimeRegVO.class, curNode.getNodeData().getPk_overtimereg());
				if (otvo != null) {
				    pkTypes.put(otvo.getPk_overtimereg(), otvo.getPk_overtimetype());
				}
			    }

			    if (pkTypes.containsKey(curNode.getNodeData().getPk_overtimereg())) {
				if (pk_overtimetype.equals(pkTypes.get(curNode.getNodeData().getPk_overtimereg()))) {
				    otHours = otHours.add(curNode.getNodeData().getHours());
				}
			    }
			}
			curNode = curNode.getNextNode();
		    }
		}
		ret.put(pk_psndoc, otHours);
	    }
	}
	return ret;
    }

    @Override
    public Map<String, UFDouble> getOvertimeTaxfreeAmount(String[] pk_psndocs, UFLiteralDate startDate,
	    UFLiteralDate endDate) throws BusinessException {
	return getSegDetailSummary(pk_psndocs, startDate, endDate, SegDetailVO.REMAINAMOUNTTAXFREE);
    }

    @Override
    public Map<String, UFDouble> getOvertimeTaxableAmount(String[] pk_psndocs, UFLiteralDate startDate,
	    UFLiteralDate endDate) throws BusinessException {
	return getSegDetailSummary(pk_psndocs, startDate, endDate, SegDetailVO.REMAINAMOUNTTAXABLE);
    }

    private Map<String, UFDouble> getSegDetailSummary(String[] pk_psndocs, UFLiteralDate startDate,
	    UFLiteralDate endDate, String digitPropName) throws BusinessException {
	Map<String, UFDouble> ret = new HashMap<String, UFDouble>();
	if (pk_psndocs != null && pk_psndocs.length > 0) {
	    for (String pk_psndoc : pk_psndocs) {
		UFLiteralDate overtimeDate = startDate;
		OTSChainNode curNode = OTSChainUtils.buildChainNodes(pk_psndoc, overtimeDate, null, false, false, true,
			true, true);
		if (curNode != null) {
		    while (curNode != null) {
			// 當前節點不為空，當前節點明細為空的，即為最後一次進入
			if (curNode.getNodeData() != null) {
			    if ((curNode.getNodeData().getRegdate().isSameDate(overtimeDate) || curNode.getNodeData()
				    .getRegdate().after(overtimeDate))
				    && curNode.getNodeData().getRegdate().isSameDate(endDate)
				    || curNode.getNodeData().getRegdate().before(endDate)) {
				if (!ret.containsKey(pk_psndoc)) {
				    ret.put(pk_psndoc, UFDouble.ZERO_DBL);
				}

				// 重新取考勤時薪
				UFDouble hourpay = getPsnHourPay(pk_psndoc, curNode.getNodeData().getRegdate(),
					DaySalaryEnum.TBMHOURSALARY);
				// 與已記錄考勤時薪不同時重新計算剩余加班費金額
				if (!hourpay.equals(curNode.getNodeData().getHourlypay())) {
				    curNode.getNodeData().setRemainamounttaxfree(
					    this.getOTAmount(curNode.getNodeData().getTaxfreerate(), hourpay, curNode
						    .getNodeData().getRemainhourstaxfree(), null, -1));
				    curNode.getNodeData().setRemainamounttaxable(
					    this.getOTAmount(curNode.getNodeData().getTaxablerate(), hourpay, curNode
						    .getNodeData().getRemainhourstaxable(), null, -1));
				    curNode.getNodeData().setRemainamount(
					    curNode.getNodeData().getRemainamounttaxfree()
						    .add(curNode.getNodeData().getRemainamounttaxable()));
				    OTSChainUtils.save(curNode);
				}

				ret.get(pk_psndoc)
					.add(curNode.getNodeData().getAttributeValue(digitPropName) == null ? UFDouble.ZERO_DBL
						: (UFDouble) curNode.getNodeData().getAttributeValue(digitPropName));
			    }
			}
			curNode = curNode.getNextNode();
		    }
		}
	    }
	}
	return ret;
    }

    @Override
    public MonthStatOTCalcVO[] getOvertimeSalaryHoursByTBMPeriodSource(String pk_org, String[] pk_psndocs,
	    String cyear, String cperiod, CalendarDateTypeEnum dateType, OvertimeSettleTypeEnum settleType)
	    throws BusinessException {
	List<MonthStatOTCalcVO> ret = new ArrayList<MonthStatOTCalcVO>();
	if (pk_psndocs != null && pk_psndocs.length > 0) {
	    PeriodVO period = getPeriodService().queryByYearMonth(pk_org, cyear, cperiod);
	    for (String pk_psndoc : pk_psndocs) {
		List<MonthStatOTCalcVO> vos = getCurrentMonthSalaryHoursByDate(pk_psndoc, period.getBegindate(),
			period.getEnddate(), dateType, settleType);
		for (MonthStatOTCalcVO vo : vos) {
		    vo.setPk_org(pk_org);
		    vo.setCyear(cyear);
		    vo.setCperiod(cperiod);
		    vo.setPk_psndoc(pk_psndoc);
		}
		ret.addAll(vos);
	    }
	}
	return ret.toArray(new MonthStatOTCalcVO[0]);
    }

    private List<MonthStatOTCalcVO> getCurrentMonthSalaryHoursByDate(String pk_psndoc, UFLiteralDate beginDate,
	    UFLiteralDate endDate, CalendarDateTypeEnum dateType, OvertimeSettleTypeEnum settleType)
	    throws BusinessException {
	List<MonthStatOTCalcVO> vos = new ArrayList<MonthStatOTCalcVO>();

	OTSChainNode curNode = OTSChainUtils.buildChainNodes(pk_psndoc, null, null, false, false, true, true, true);

	Map<OvertimeSettleTypeEnum, UFDouble> workDayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>(); // 平日
	Map<OvertimeSettleTypeEnum, UFDouble> holidayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>(); // 例假日
	Map<OvertimeSettleTypeEnum, UFDouble> offdayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// 休息日
	Map<OvertimeSettleTypeEnum, UFDouble> nationalDayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// 国假
	Map<OvertimeSettleTypeEnum, UFDouble> totalWorkAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// 平日合计
	Map<OvertimeSettleTypeEnum, UFDouble> totalHolidayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// 例假日合计
	Map<OvertimeSettleTypeEnum, UFDouble> totalOffdayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// 休息日合计
	Map<OvertimeSettleTypeEnum, UFDouble> totalNationalDayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// 国假合计

	if (curNode != null) {
	    Map<String, SegRuleVO> ruleMap = new HashMap<String, SegRuleVO>();
	    while (curNode != null) {
		// 當前節點不為空，當前節點明細為空的，即為最後一次進入
		if (curNode.getNodeData() != null) {
		    String pk_segrule = curNode.getNodeData().getPk_segrule();
		    // 加班日期在期间内
		    if (!curNode.getNodeData().getRegdate().after(endDate)
			    && !curNode.getNodeData().getRegdate().before(beginDate)) {
			if (!ruleMap.containsKey(pk_segrule)) {
			    // 缓存加班分段依据
			    ruleMap.put(pk_segrule,
				    (SegRuleVO) this.getBaseDao().retrieveByPK(SegRuleVO.class, pk_segrule));
			}

			if (dateType != null) {
			    if (CalendarDateTypeEnum.NORMAL.equals(dateType)) {
				// 统计平日
				addInPeriodAmount(dateType, curNode, workDayAmount, ruleMap, pk_segrule);
			    } else if (CalendarDateTypeEnum.HOLIDAY.equals(dateType)) {
				// 统计例假日
				addInPeriodAmount(dateType, curNode, holidayAmount, ruleMap, pk_segrule);
			    } else if (CalendarDateTypeEnum.OFFDAY.equals(dateType)) {
				// 统计休息日
				addInPeriodAmount(dateType, curNode, offdayAmount, ruleMap, pk_segrule);
			    } else if (CalendarDateTypeEnum.NATIONALDAY.equals(dateType)) {
				// 统计国假
				addInPeriodAmount(dateType, curNode, nationalDayAmount, ruleMap, pk_segrule);
			    }
			} else {
			    // 统计平日
			    addInPeriodAmount(CalendarDateTypeEnum.NORMAL, curNode, workDayAmount, ruleMap, pk_segrule);
			    // 统计例假日
			    addInPeriodAmount(CalendarDateTypeEnum.HOLIDAY, curNode, holidayAmount, ruleMap, pk_segrule);
			    // 统计休息日
			    addInPeriodAmount(CalendarDateTypeEnum.OFFDAY, curNode, offdayAmount, ruleMap, pk_segrule);
			    // 统计国假
			    addInPeriodAmount(CalendarDateTypeEnum.NATIONALDAY, curNode, nationalDayAmount, ruleMap,
				    pk_segrule);
			}
		    }
		    // 审核日期在范围内
		    else if (!curNode.getNodeData().getApproveddate().after(endDate)
			    && !curNode.getNodeData().getApproveddate().before(beginDate)) {
			if (dateType != null && !curNode.getNodeData().getIscompensation().booleanValue()) {
			    if (CalendarDateTypeEnum.NORMAL.equals(dateType)) {
				// 统计平日
				addInMap(workDayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
					.getRemainhours());
			    } else if (CalendarDateTypeEnum.HOLIDAY.equals(dateType)) {
				// 统计例假日
				addInMap(holidayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
					.getRemainhours());
			    } else if (CalendarDateTypeEnum.OFFDAY.equals(dateType)) {
				// 统计休息日
				addInMap(offdayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
					.getRemainhours());
			    } else if (CalendarDateTypeEnum.NATIONALDAY.equals(dateType)) {
				// 统计国假
				addInMap(nationalDayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode
					.getNodeData().getRemainhours());
			    }
			} else {
			    if (!curNode.getNodeData().getIscompensation().booleanValue()) {
				// 统计平日
				addInMap(workDayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
					.getRemainhours());
				// 统计例假日
				addInMap(holidayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
					.getRemainhours());
				// 统计休息日
				addInMap(offdayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
					.getRemainhours());
				// 统计国假
				addInMap(nationalDayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode
					.getNodeData().getRemainhours());
			    }
			}
		    } else {
			// 超过截止时间即为结束
			break;
		    }
		}
		curNode = curNode.getNextNode();
	    }

	    // 处理合计
	    // 平日
	    addInMap(totalWorkAmount, OvertimeSettleTypeEnum.TOTAL,
		    workDayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOSALARY));
	    addInMap(totalWorkAmount, OvertimeSettleTypeEnum.TOTAL,
		    workDayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOREST));
	    addInMap(totalWorkAmount, OvertimeSettleTypeEnum.TOTAL,
		    workDayAmount.get(OvertimeSettleTypeEnum.OTHER_TOSALARY));
	    // 例假日
	    addInMap(totalHolidayAmount, OvertimeSettleTypeEnum.TOTAL,
		    holidayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOSALARY));
	    addInMap(totalHolidayAmount, OvertimeSettleTypeEnum.TOTAL,
		    holidayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOREST));
	    addInMap(totalHolidayAmount, OvertimeSettleTypeEnum.TOTAL,
		    holidayAmount.get(OvertimeSettleTypeEnum.OTHER_TOSALARY));
	    // 休息日
	    addInMap(totalOffdayAmount, OvertimeSettleTypeEnum.TOTAL,
		    offdayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOSALARY));
	    addInMap(totalOffdayAmount, OvertimeSettleTypeEnum.TOTAL,
		    offdayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOREST));
	    addInMap(totalOffdayAmount, OvertimeSettleTypeEnum.TOTAL,
		    offdayAmount.get(OvertimeSettleTypeEnum.OTHER_TOSALARY));
	    // 国假
	    addInMap(totalNationalDayAmount, OvertimeSettleTypeEnum.TOTAL,
		    nationalDayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOSALARY));
	    addInMap(totalNationalDayAmount, OvertimeSettleTypeEnum.TOTAL,
		    nationalDayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOREST));
	    addInMap(totalNationalDayAmount, OvertimeSettleTypeEnum.TOTAL,
		    nationalDayAmount.get(OvertimeSettleTypeEnum.OTHER_TOSALARY));
	}

	convertToVO(vos, CalendarDateTypeEnum.NORMAL, workDayAmount);
	convertToVO(vos, CalendarDateTypeEnum.HOLIDAY, holidayAmount);
	convertToVO(vos, CalendarDateTypeEnum.OFFDAY, offdayAmount);
	convertToVO(vos, CalendarDateTypeEnum.NATIONALDAY, nationalDayAmount);

	convertToVO(vos, CalendarDateTypeEnum.NORMAL, totalWorkAmount);
	convertToVO(vos, CalendarDateTypeEnum.HOLIDAY, totalHolidayAmount);
	convertToVO(vos, CalendarDateTypeEnum.OFFDAY, totalOffdayAmount);
	convertToVO(vos, CalendarDateTypeEnum.NATIONALDAY, totalNationalDayAmount);

	return vos;
    }

    private void convertToVO(List<MonthStatOTCalcVO> vos, CalendarDateTypeEnum dateType,
	    Map<OvertimeSettleTypeEnum, UFDouble> amountMap) {
	for (Entry<OvertimeSettleTypeEnum, UFDouble> amount : amountMap.entrySet()) {
	    MonthStatOTCalcVO vo = new MonthStatOTCalcVO();
	    vo.setSettleType(amount.getKey());
	    vo.setHours(amount.getValue());
	    vo.setDateType(dateType);
	    vos.add(vo);
	}
    }

    private void addInPeriodAmount(CalendarDateTypeEnum dateType, OTSChainNode curNode,
	    Map<OvertimeSettleTypeEnum, UFDouble> workDayAmount, Map<String, SegRuleVO> ruleMap, String pk_segrule) {
	if (ruleMap.get(pk_segrule).getDatetype() == dateType.toIntValue()) {
	    if (!curNode.getNodeData().getIscompensation().booleanValue()) {
		// 本期转薪
		addInMap(workDayAmount, OvertimeSettleTypeEnum.PERIOD_TOSALARY, curNode.getNodeData().getRemainhours());
	    } else {
		// 本期转休
		addInMap(workDayAmount, OvertimeSettleTypeEnum.PERIOD_TOSALARY, curNode.getNodeData().getRemainhours());
	    }
	}
    }

    private void addInMap(Map<OvertimeSettleTypeEnum, UFDouble> dataMap, OvertimeSettleTypeEnum key, UFDouble value) {
	if (!dataMap.containsKey(key)) {
	    dataMap.put(key, UFDouble.ZERO_DBL);
	}

	dataMap.put(key, dataMap.get(key).add(value == null ? UFDouble.ZERO_DBL : value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public OTLeaveBalanceVO[] getOvertimeToRestHoursByType(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
	    UFLiteralDate endDate, String pk_overtimetype) throws BusinessException {
	List<OTLeaveBalanceVO> headvos = new ArrayList<OTLeaveBalanceVO>();

	if (pk_psndocs == null || pk_psndocs.length == 0) {
	    List<String> psnList = (List<String>) this.getBaseDao().executeQuery(
		    "select distinct pk_psndoc from tbm_psndoc where pk_org = '" + pk_org
			    + "' and isnull(dr,0)=0 and '" + beginDate.toString() + "' <= enddate and '"
			    + endDate.toString() + "' >= begindate", new ColumnListProcessor());
	    if (psnList != null && psnList.size() > 0) {
		pk_psndocs = psnList.toArray(new String[0]);
	    }
	}

	if (pk_psndocs != null && pk_psndocs.length > 0) {
	    for (String pk_psndoc : pk_psndocs) {
		UFDouble totalAmount = UFDouble.ZERO_DBL; // 享有
		UFDouble spentAmount = UFDouble.ZERO_DBL;// 已休
		UFDouble remainAmount = UFDouble.ZERO_DBL;// 剩余
		UFDouble frozenAmount = UFDouble.ZERO_DBL;// 冻结
		UFDouble useableAmount = UFDouble.ZERO_DBL; // 可用
		Map<String, UFLiteralDate> otDateMap = new HashMap<String, UFLiteralDate>();// 加班日期
		Map<String, UFDouble> otTotalHoursMap = new HashMap<String, UFDouble>(); // 加班享有
		Map<String, UFDouble> otSpentHoursMap = new HashMap<String, UFDouble>();// 加班已休
		Map<String, UFDouble> otFrozenHoursMap = new HashMap<String, UFDouble>();// 加班冻结
		Map<String, UFBoolean> otClosedMap = new HashMap<String, UFBoolean>();// 是否结束
		List<String> otList = new ArrayList<String>();
		OTSChainNode curNode = OTSChainUtils.buildChainNodes(pk_psndoc, null, null, true, false, true, false,
			false); // 按人员加载加班转调休分段明细

		OTLeaveBalanceVO headVo = null;
		if (curNode == null) {
		    headVo = createNewHeadVO(pk_org, pk_psndoc, totalAmount, spentAmount, remainAmount, frozenAmount,
			    useableAmount);
		    continue;
		}
		curNode = OTSChainUtils.getFirstNode(curNode); // 取第一个节点
		while (curNode != null) {
		    if (curNode.getNodeData() != null) {
			UFLiteralDate expireDate = curNode.getNodeData().getExpirydate();
			String pk_overtimereg = curNode.getNodeData().getPk_overtimereg();
			if (expireDate != null) {
			    if ((expireDate.isSameDate(beginDate) || expireDate.after(beginDate))
				    && (expireDate.isSameDate(endDate) || expireDate.before(endDate))) {
				totalAmount = totalAmount.add(getUFDouble(curNode.getNodeData().getHours()));// 享有
				spentAmount = spentAmount.add(getUFDouble(curNode.getNodeData().getConsumedhours()));// 已休
				remainAmount = remainAmount.add(getUFDouble(curNode.getNodeData().getRemainhours()));// 剩余
				frozenAmount = frozenAmount.add(getUFDouble(curNode.getNodeData().getFrozenhours()));// 冻结
				collectOvertimeData(otDateMap, otTotalHoursMap, otSpentHoursMap, otFrozenHoursMap,
					otClosedMap, otList, curNode, pk_overtimereg); // 统计加班数据
			    }
			}
		    }
		    curNode = curNode.getNextNode();
		}
		useableAmount = remainAmount.sub(frozenAmount);

		// 创建表头
		headVo = createNewHeadVO(pk_org, pk_psndoc, totalAmount, spentAmount, remainAmount, frozenAmount,
			useableAmount);
		headvos.add(headVo);
	    }
	}

	return headvos.toArray(new OTLeaveBalanceVO[0]);
    }

    private OTLeaveBalanceVO createNewHeadVO(String pk_org, String pk_psndoc, UFDouble totalAmount,
	    UFDouble spentAmount, UFDouble remainAmount, UFDouble frozenAmount, UFDouble useableAmount) {
	OTLeaveBalanceVO newHeadVo = new OTLeaveBalanceVO();
	newHeadVo.setPk_psndoc(pk_psndoc);
	newHeadVo.setPk_otleavebalance(pk_psndoc);
	newHeadVo.setPk_org(pk_org);
	newHeadVo.setPk_group(InvocationInfoProxy.getInstance().getGroupId());
	newHeadVo.setTotalhours(totalAmount);
	newHeadVo.setConsumedhours(spentAmount);
	newHeadVo.setRemainhours(remainAmount);
	newHeadVo.setFrozenhours(frozenAmount);
	newHeadVo.setFreehours(useableAmount);
	return newHeadVo;
    }

    private void collectOvertimeData(Map<String, UFLiteralDate> otDateMap, Map<String, UFDouble> otTotalHoursMap,
	    Map<String, UFDouble> otSpentHoursMap, Map<String, UFDouble> otFrozenHoursMap,
	    Map<String, UFBoolean> otIsClosedMap, List<String> otList, OTSChainNode curNode, String pk_overtimereg) {
	if (!otList.contains(pk_overtimereg)) {
	    otList.add(pk_overtimereg);
	}
	// 加班日期
	if (!otDateMap.containsKey(pk_overtimereg)) {
	    otDateMap.put(pk_overtimereg, curNode.getNodeData().getRegdate());
	}

	// 加班享有
	if (!otTotalHoursMap.containsKey(pk_overtimereg)) {
	    otTotalHoursMap.put(pk_overtimereg, getUFDouble(curNode.getNodeData().getHours()));
	} else {
	    otTotalHoursMap
		    .put(pk_overtimereg,
			    getUFDouble(otTotalHoursMap.get(pk_overtimereg).add(
				    getUFDouble(curNode.getNodeData().getHours()))));
	}

	// 加班已休
	if (!otSpentHoursMap.containsKey(pk_overtimereg)) {
	    otSpentHoursMap.put(pk_overtimereg, getUFDouble(curNode.getNodeData().getConsumedhours()));
	} else {
	    otSpentHoursMap.put(
		    pk_overtimereg,
		    getUFDouble(otSpentHoursMap.get(pk_overtimereg).add(
			    getUFDouble(curNode.getNodeData().getConsumedhours()))));
	}

	// 加班冻结
	if (!otFrozenHoursMap.containsKey(pk_overtimereg)) {
	    otFrozenHoursMap.put(pk_overtimereg, getUFDouble(curNode.getNodeData().getFrozenhours()));
	} else {
	    otFrozenHoursMap.put(
		    pk_overtimereg,
		    getUFDouble(otFrozenHoursMap.get(pk_overtimereg).add(
			    getUFDouble(curNode.getNodeData().getFrozenhours()))));
	}

	// 是否结束
	if (!otIsClosedMap.containsKey(pk_overtimereg)) {
	    otIsClosedMap.put(pk_overtimereg,
		    new UFBoolean(UFDouble.ZERO_DBL.equals(curNode.getNodeData().getRemainhours())));
	}
    }

    UFDouble getUFDouble(Object value) {
	if (value == null) {
	    return UFDouble.ZERO_DBL;
	} else {
	    return new UFDouble(String.valueOf(value));
	}
    }

    @SuppressWarnings("unchecked")
    @Override
    public OTBalanceDetailVO[] getOvertimeToRestByType(String pk_org, String pk_psndoc, UFLiteralDate beginDate,
	    UFLiteralDate endDate, String pk_overtimetype) throws BusinessException {
	List<OTBalanceDetailVO> detailVOs = new ArrayList<OTBalanceDetailVO>();

	if (!StringUtils.isEmpty(pk_psndoc)) {
	    Map<String, UFLiteralDate> otDateMap = new HashMap<String, UFLiteralDate>();// 加班日期
	    Map<String, UFDouble> otTotalHoursMap = new HashMap<String, UFDouble>(); // 加班享有
	    Map<String, UFDouble> otSpentHoursMap = new HashMap<String, UFDouble>();// 加班已休
	    Map<String, UFDouble> otFrozenHoursMap = new HashMap<String, UFDouble>();// 加班冻结
	    Map<String, UFBoolean> otClosedMap = new HashMap<String, UFBoolean>();// 是否结束
	    List<String> otList = new ArrayList<String>();
	    OTSChainNode curNode = OTSChainUtils
		    .buildChainNodes(pk_psndoc, null, null, true, false, true, false, false); // 按人员加载加班转调休分段明细

	    curNode = OTSChainUtils.getFirstNode(curNode); // 取第一个节点
	    while (curNode != null) {
		if (curNode.getNodeData() != null) {
		    UFLiteralDate expireDate = curNode.getNodeData().getExpirydate();
		    String pk_overtimereg = curNode.getNodeData().getPk_overtimereg();
		    if (expireDate != null) {
			if ((expireDate.isSameDate(beginDate) || expireDate.after(beginDate))
				&& (expireDate.isSameDate(endDate) || expireDate.before(endDate))) {
			    collectOvertimeData(otDateMap, otTotalHoursMap, otSpentHoursMap, otFrozenHoursMap,
				    otClosedMap, otList, curNode, pk_overtimereg); // 统计加班数据
			}
		    }
		}
		curNode = curNode.getNextNode();
	    }

	    // 创建表行
	    for (String pk_overtimereg : otList) {
		OTBalanceDetailVO detailVo = new OTBalanceDetailVO();
		detailVo.setPk_otleavebalance(pk_psndoc);
		detailVo.setSourcetype(SoureBillTypeEnum.OVERTIMEREG.toIntValue());
		detailVo.setPk_sourcebill(pk_overtimereg);
		detailVo.setCalendar(otDateMap.get(pk_overtimereg));
		detailVo.setBillhours(otTotalHoursMap.get(pk_overtimereg));
		detailVo.setConsumedhours(otSpentHoursMap.get(pk_overtimereg));
		detailVo.setFrozenhours(otFrozenHoursMap.get(pk_overtimereg));
		detailVo.setCloseflag(otClosedMap.get(pk_overtimereg));
		detailVo.setPk_balancedetail(pk_overtimereg);
		detailVOs.add(detailVo);
	    }
	}

	return detailVOs.toArray(new OTBalanceDetailVO[0]);
    }
}
