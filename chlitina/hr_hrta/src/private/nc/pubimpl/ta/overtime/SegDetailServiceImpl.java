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

		// ȡ�Ӱ�e
		OverTimeTypeCopyVO otType = (OverTimeTypeCopyVO) this.getBaseDao().retrieveByPK(
			OverTimeTypeCopyVO.class, otRegVo.getPk_overtimetypecopy());

		if (otType.getPk_segrule() != null) {
		    // �����Ӱ�eȡ�ֶ�����
		    AggSegRuleVO ruleAggVO = getSegRuleAggVO(otType.getPk_segrule());

		    // ȡ��ǰ�Ӱ�ֶ��������c
		    OTSChainNode curOTSegNode = getOvertimeSegChainNodeByOTReg(otRegVo, ruleAggVO);

		    // ȡ��ǰ�ˆTȫ���c������朱���
		    if (!this.getAllNode().containsKey(pk_psndoc)) {
			OTSChainNode psnNode = OTSChainUtils.buildChainNodes(pk_psndoc, null, null, false, false,
				false, false, false);
			this.getAllNode().put(pk_psndoc, psnNode);
		    }

		    // У�Ӱ����Ƿ��Ѵ��ڲ�ͬ�ļӰ�ֶ�����
		    OTSChainNode checkNode = this.getAllNode().get(pk_psndoc);
		    while (checkNode != null) {
			if (checkNode.getNodeData().getRegdate().isSameDate(curOTSegNode.getNodeData().getRegdate())) {
			    // �Ѵ��ڹ��c�c��ǰ�Ӱ��՞�ͬһ��
			    if (!otType.getPk_segrule().equals(checkNode.getNodeData().getPk_segrule())) {
				throw new BusinessException("ϵ�y���Ѵ���ʹ�ò�ͬ��͵ļӰ�ֶ�Ҏ�t�ļӰ��ӛ�Ρ� ");
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
		    throw new BusinessException("�Ӱ��ӛ�Ό����ķֶ������ѱ�����");
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
	UFDouble totalSegHours = UFDouble.ZERO_DBL; // �nӋ���ɷֶΕr�L
	OTSChainNode parentNode = null;
	UFLiteralDate realDate = getShiftRegDate(otRegVO); // �@ȡ���H�Ӱ����ڣ�ˢ���_ʼ�r�g�����ٹ����գ�
	UFLiteralDate maxLeaveDate = getMaxLeaveDate(otRegVO, realDate); // �@ȡ���L��������

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
	    UFDouble seghours = end.sub(start); // �ֶΕr�L
	    UFDouble curSegTotalHours = UFDouble.ZERO_DBL; // ��ǰ�ֶο��r�L
	    UFDouble curSegTaxfreeHours = UFDouble.ZERO_DBL; // ��ǰ�ֶ��ⶐ�r�L
	    UFDouble curSegTaxableHours = UFDouble.ZERO_DBL; // ��ǰ�ֶΑ����r�L
	    if (othours.doubleValue() >= seghours.doubleValue()) {
		// �Ӱ��r�L��춷ֶ��L�ȣ������]�ԕr�Lȡ�ֶΕr�L
		curSegTotalHours = seghours;
	    } else {
		// �Ӱ��r�LС춷ֶ��L�ȣ������]�ԕr�Lȡ�Ӱ��r�L
		curSegTotalHours = othours;
	    }

	    othours = othours.sub(curSegTotalHours);

	    if (term.getIslimitscope() == null || !term.getIslimitscope().booleanValue()) {
		// ��Ӌ��Ӱ����޽yӋ��ֱ��ӛ���ⶐ�r��
		curSegTaxfreeHours = curSegTotalHours;
		curSegTaxableHours = UFDouble.ZERO_DBL;
	    } else {
		// �șz��ֶ������ϣ�ԓ�ֶεđ��ⶐ�O��
		if (term.getTaxflag().equals(TaxFlagEnum.TAXABLE.toIntValue())) {
		    curSegTaxfreeHours = UFDouble.ZERO_DBL;
		    curSegTaxableHours = curSegTotalHours;
		} else {
		    // Ӌ��Ӱ����޽yӋ���z�鮔�ս�ֹ����ǰһ�յđ����Ӱ��r��
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

	    totalSegHours = totalSegHours.add(curSegTotalHours);// ���ɷֶΕr�L�n��
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
	    throw new BusinessException("��ǰ��֯δ������Ч�Ŀ��ڹ�����������ԡ�");
	}

	UFLiteralDate maxLeaveDate = null;

	if (settleType == 0) {
	    // ���Ӱ���������N����
	    UFDouble monthAfter = timeRule.getMonthafterotdate();
	    maxLeaveDate = otRealDate.getDateAfter(monthAfter.multiply(30).intValue());
	} else if (settleType == 1) {
	    // ��������������N����
	    UFDouble monthAfter = timeRule.getMonthafterapproved();
	    if (otRegVO.getApprove_time() != null) {
		maxLeaveDate = new UFLiteralDate(otRegVO.getApprove_time()
			.getDateTimeAfter(monthAfter.multiply(30).sub(1).intValue()).toString().substring(0, 10));
	    } else {
		throw new BusinessException("�Ӱ�������ڰ�������ڼ����������Ӱ�Ǽǵ�������ڡ�");
	    }
	} else if (settleType == 2) {
	    // ���̶����ڣ��ȶԼӰ����ڣ�
	    String startYearMonth = timeRule.getStartcycleyearmonth();
	    if (!StringUtils.isEmpty(startYearMonth) && startYearMonth.length() == 6) {
		String cyear = startYearMonth.substring(0, 4);
		String cmonth = startYearMonth.substring(4, 6);
		UFLiteralDate startDate = new UFLiteralDate(cyear + "-" + cmonth + "-01"); // ������ʼ��
		maxLeaveDate = getCycleDate(timeRule.getMonthofcycle(), startDate);
	    } else {
		throw new BusinessException("�Ӱ�������ڰ��̶����ڼ���������鿼�ڹ������������趨����ʽ��YYYYMM����");
	    }
	} else if (settleType == 3) {
	    // �����������գ��ȶԼӰ����ڣ�
	    PsnOrgVO psnOrgVO = (PsnOrgVO) this.getBaseDao().retrieveByPK(PsnOrgVO.class, otRegVO.getPk_psnorg());
	    UFLiteralDate workAgeStartDate = (UFLiteralDate) psnOrgVO.getAttributeValue("workagestartdate"); // ����������
	    if (workAgeStartDate != null) {
		maxLeaveDate = getCycleDate(new UFDouble(12), workAgeStartDate);
	    } else {
		throw new BusinessException("�Ӱ�������ڰ����������ռ����������Ա����֯��ϵ�����������趨��");
	    }
	} else {
	    throw new BusinessException("�Ӱ�������ڼ������������֯������TWHRT09�����趨��");
	}
	return maxLeaveDate;
    }

    private UFLiteralDate getCycleDate(UFDouble monthOfCycle, UFLiteralDate startDate) {
	UFLiteralDate maxLeaveDate;
	int wholeDays = new UFLiteralDate().getDaysAfter(startDate); // ������ʼ�յ��Ӱ൱������
	UFDouble daysInOneCycle = monthOfCycle.multiply(30); // ����������
	int passedCycles = wholeDays / daysInOneCycle.intValue(); // ��һ��������������
	startDate = startDate.getDateAfter(daysInOneCycle.multiply(passedCycles).intValue()); // ��һ���ڵ���ʼ��
	maxLeaveDate = startDate.getDateBefore(startDate.getDay() - 1).getDateBefore(1);
	return maxLeaveDate;
    }

    private SegDetailVO createNewSegDetail(OvertimeRegVO vo, UFLiteralDate realRegDate, SegRuleVO rule,
	    SegRuleTermVO term, UFDouble seghours, UFDouble taxfreerate, UFDouble taxablerate, UFDouble hourstaxfree,
	    UFDouble hourstaxable, UFLiteralDate maxLeaveDate) throws BusinessException {
	if (vo.getApprove_time() == null) {
	    throw new BusinessException("�Ӱ��ӛ���������e�`��Ո�z��Ӱ��ӛ��");
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
	// mod Ares.Tank 2018-10-15 12:40:19 ��������ʱ��
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
	segvo.setRemainamount(segvo.getRemainamounttaxfree().add(segvo.getRemainamounttaxable())); // ʣ�N���~=ʣ�N���~���ⶐ��+ʣ�N���~��������
	segvo.setRulehours(seghours);
	return segvo;
    }

    /**
     * �����Ӱ�˶��_ʼ���ڲ�ԃ�Ӱ����H�w�ٰ�ε���������
     * 
     * @param vo
     *            �Ӱ��ӛ��
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
	    // throw new BusinessException("�ˆT�rнȡֵ�e�`���ˆT�rн���");
	    return UFDouble.ZERO_DBL; // δȡ����н�ģ����r����0
	}
	return dayPayMap.get(pk_psndoc).get(overtimebegindate);
    }

    @SuppressWarnings("unchecked")
    private AggSegRuleVO getSegRuleAggVO(String pk_segrule) throws BusinessException {
	SegRuleVO ruleVO = (SegRuleVO) this.getBaseDao().retrieveByPK(SegRuleVO.class, pk_segrule);

	if (ruleVO == null) {
	    throw new BusinessException("�����e�`���Ӱ�ֶ�Ҏ�t�ѱ��h��");
	}

	Collection<SegRuleTermVO> segTerms = this.getBaseDao().retrieveByClause(SegRuleTermVO.class,
		"pk_segrule='" + pk_segrule + "' and dr=0", "segno");
	if (segTerms == null || segTerms.size() == 0) {
	    throw new BusinessException("�����e�`���Ӱ�ֶ�Ҏ�t�����ѱ��h��");
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

		// ȡ�I�Յ������x�ļӰ�ֶ��ݼ�e���Ӱ��D�a�ݣ�
		LeaveTypeCopyVO lvTypeVO = getLeaveTypeVOs(vo.getPk_org());

		if (lvTypeVO.getPk_timeitemcopy().equals(vo.getPk_leavetypecopy())) {
		    // ȡ���]��δ���ĵļӰ�ֶ�����
		    // ȡ��ǰ�ˆT�^�V���c��߉݋朱����D�a�ݣ�δ���U��δ�����ꮅ��δ�Y�㣩
		    OTSChainNode psnNode = OTSChainUtils.buildChainNodes(vo.getPk_psndoc(), null, null, true, false,
			    true, true, true);
		    SegDetailVO[] segDetailBeConsumed = OTSChainUtils.getAllNodeData(psnNode);

		    if (segDetailBeConsumed == null || segDetailBeConsumed.length == 0) {
			throw new BusinessException("���ļӰ�ֶ�����ʧ����δ�ҵ����õļӰ�ֶ�������");
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
	    // ���M�ȳ�ƥ��
	    if (!unConsumedLeaveHours.equals(UFDouble.ZERO_DBL)) {
		// ���P���N�r��
		UFDouble curConsumedHours = UFDouble.ZERO_DBL;
		if (segDetail.getRemainhours().doubleValue() >= unConsumedLeaveHours.doubleValue()) {
		    // ʣ�NС�r����춵�춱��κ��N�r��=���κ��Nȫ���ڱ��l�������
		    curConsumedHours = unConsumedLeaveHours;
		    unConsumedLeaveHours = UFDouble.ZERO_DBL;
		} else {
		    // ʣ�NС�r��С춱��κ��N�r��=���κ��N�ڱ��l������ɲ���
		    curConsumedHours = segDetail.getRemainhours();
		    // �N�²����^�m������N
		    unConsumedLeaveHours = vo.getLeavehour().sub(segDetail.getRemainhours());
		}

		consumeSegDetailHours(curConsumedHours, segDetail); // ̎�����P���N
		SegDetailConsumeVO consumeVO = getNewConsumeVO(vo, segDetail); // ���ɺ��N����
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

    // �������ⶐ���֣������đ�������
    private void consumeSegDetailHours(UFDouble unConsumedHours, SegDetailVO segDetail) throws BusinessException {
	SegRuleTermVO term = (SegRuleTermVO) this.getBaseDao().retrieveByPK(SegRuleTermVO.class,
		segDetail.getPk_segruleterm());

	// �ⶐ����
	if (segDetail.getRemainhourstaxfree().doubleValue() > 0) {
	    if (unConsumedHours.doubleValue() <= segDetail.getRemainhourstaxfree().doubleValue()) {
		// ֻ�����ⶐ����
		segDetail.setRemainhourstaxfree(segDetail.getRemainhourstaxfree().sub(unConsumedHours));
		segDetail.setConsumedhourstaxfree(segDetail.getConsumedhourstaxfree().add(unConsumedHours));
		segDetail.setRemainhours(segDetail.getRemainhours().sub(unConsumedHours));
		segDetail.setConsumedhours(segDetail.getConsumedhours().add(unConsumedHours));
	    } else {
		// �����ⶐȫ���ᣬʣ�N���đ�������
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
	    // ֻ���đ�������
	    segDetail.setRemainhourstaxable(segDetail.getRemainhourstaxable().sub(unConsumedHours));
	    segDetail.setConsumedhourstaxable(segDetail.getConsumedhourstaxable().add(unConsumedHours));
	    segDetail.setRemainhours(segDetail.getRemainhours().sub(unConsumedHours));
	    segDetail.setConsumedhours(segDetail.getConsumedhours().add(unConsumedHours));
	}

	// Ӌ��Ӱ��M
	segDetail.setRemainamounttaxfree(getOTAmount(term.getTaxfreeotrate(), segDetail.getHourlypay(),
		segDetail.getRemainhourstaxfree(), segDetail, DaySalaryEnum.TAXFREEHOURSALARY));
	segDetail.setRemainamounttaxable(getOTAmount(term.getTaxableotrate(), segDetail.getHourlypay(),
		segDetail.getRemainhourstaxable(), segDetail, DaySalaryEnum.TAXABLEHOURSALARY));
	segDetail.setRemainamount(segDetail.getRemainamounttaxfree().add(segDetail.getRemainamounttaxable()));

	unConsumedHours = UFDouble.ZERO_DBL;
	if (segDetail.getRemainhours().doubleValue() == 0) {
	    // �O���������ꮅ
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
		throw new BusinessException("����ȡֵʧ����Ո�ژI�Յ����O��-�M�����O�����M�� [�Ӱ��a��ָ���لe] ������");
	    }
	    LeaveTypeCopyVO vo = (LeaveTypeCopyVO) this.getBaseDao().retrieveByPK(LeaveTypeCopyVO.class,
		    pk_leavetypecopy);
	    leaveTypeVOMap.put(pk_org, vo);
	}

	return leaveTypeVOMap.get(pk_org);
    }

    @SuppressWarnings("unchecked")
    private TimeRuleVO getTimeRule(String pk_org) throws BusinessException {
	// ȡԓ�T������Ҏ�t
	Collection<TimeRuleVO> timerule = this.getBaseDao().retrieveByClause(TimeRuleVO.class,
		"pk_org='" + pk_org + "' and dr=0");
	if (timerule == null) {
	    throw new BusinessException("ȡ����Ҏ�t�e�`��ָ���M����δ���x����Ҏ�t");
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
		// ����Ҏ�t
		TimeRuleVO timerule = getTimeRule(pk_org);

		// ȡԓ�T���Y���L��
		OvertimeLimitScopeEnum curStatScope = getPsnStatScope(pk_org, pk_psndoc, curNode.getNodeData()
			.getRegdate());

		// һ���L�ڃȼӰ಻�ܳ��^�ĕr��
		UFDouble taxFreeLimitHours = timerule.getCtrlothours();
		// һ�������L�ڃȼӰ಻�ܳ��^�Ŀ��r��
		UFDouble totalTaxFreeLimitHours = taxFreeLimitHours;

		PeriodVO[] threePeriods = null;
		PeriodVO periodCurrent = getPeriodService().queryByDate(pk_org, startDate);
		if (periodCurrent == null) {
		    throw new BusinessException("ȡ��ǰ���g�e�`");
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

		// �Ƿ��ⶐ
		boolean isTaxFree = true;
		if (curNode != null) {
		    while (curNode != null) {
			// ��ǰ���c�r������գ���ǰ���c������յģ���������һ���M��
			if (curNode.getNodeData() == null && curNodeHours != null) {
			    if (!isTaxFree) {
				// ���M�둪������
				totalTaxFreeAmount = UFDouble.ZERO_DBL;
				totalTaxableAmount = curNodeHours;
			    } else {
				if (totalHours.add(curNodeHours).doubleValue() <= totalTaxFreeLimitHours.doubleValue()) {
				    // �ӿ��������ⶐ�r��������
				    totalTaxFreeAmount = curNodeHours;
				    totalTaxableAmount = UFDouble.ZERO_DBL;
				} else {
				    // ���ó��^�ⶐ�r��
				    totalTaxableAmount = totalHours.add(curNodeHours).sub(totalTaxFreeLimitHours);
				    totalTaxFreeAmount = curNodeHours.sub(totalTaxableAmount);
				}
			    }
			    break;
			}

			if (curNode.getNodeData().getPk_org().equals(pk_org)) {
			    // �����ڹ�����
			    if (curNode.getNodeData().getRegdate().isSameDate(sumStartDate)
				    || curNode.getNodeData().getRegdate().after(sumStartDate)
				    && (curNode.getNodeData().getRegdate().isSameDate(sumEndDate) || curNode
					    .getNodeData().getRegdate().before(sumEndDate))) {
				UFDouble curHours = getHoursInScope(curNode.getNodeData());

				// �n�ӼӰ��r��
				totalHours = totalHours.add(curHours);
				if (isTaxFree) {
				    if (totalHours.doubleValue() <= totalTaxFreeLimitHours.doubleValue()) {
					// ���ⶐ�r�������ȵģ��nӋ���ⶐ�Ӱ��M
					totalTaxFreeAmount = totalTaxFreeAmount.add(getOTAmount(curNode.getNodeData()
						.getTaxfreerate(), curNode.getNodeData().getHourlypay(), curNode
						.getNodeData().getHourstaxfree(), curNode.getNodeData(),
						DaySalaryEnum.TBMHOURSALARY));
				    } else {
					// ���ó��^�ⶐ�r��
					// ���^�r���� * �rн * �����Ӱ��M�� = ��һ�P�����Ӱ��M
					totalTaxableAmount = getOTAmount(curNode.getNodeData().getTaxablerate(),
						curNode.getNodeData().getHourlypay(),
						totalHours.sub(totalTaxFreeLimitHours), curNode.getNodeData(),
						DaySalaryEnum.TBMHOURSALARY);
					// ���Pδ���ļӰ��M - ��һ�P�����Ӱ��M = ����һ�P�ⶐ�Ӱ��M
					totalTaxFreeAmount = totalTaxFreeAmount.add(getOTAmount(curNode.getNodeData()
						.getTaxfreerate(), curNode.getNodeData().getHourlypay(), curHours
						.sub(totalHours.sub(totalTaxFreeLimitHours)), curNode.getNodeData(),
						DaySalaryEnum.TBMHOURSALARY));
					isTaxFree = false;
				    }
				} else {
				    // ���^�ⶐ�r�������ģ��nӋ�������Ӱ��M
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
			    // ���M�둪������
			    totalTaxFreeAmount = UFDouble.ZERO_DBL;
			    totalTaxableAmount = curNodeHours;
			} else {
			    if (totalHours.add(curNodeHours).doubleValue() <= totalTaxFreeLimitHours.doubleValue()) {
				// �ӿ��������ⶐ�r��������
				totalTaxFreeAmount = curNodeHours;
				totalTaxableAmount = UFDouble.ZERO_DBL;
			    } else {
				// ���ó��^�ⶐ�r��
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
	// �������g
	try {
	    startPeriod = SysInitQuery.getParaString(pk_org, "TWHRT07"); // �Ӱ�У���ʼ����
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
	    throw new BusinessException("�o���_���Ӱ����޽yӋ�������g��");
	}

	int startMonth = Integer.valueOf(periodStart.toArray(new PeriodVO[0])[0].getTimemonth());
	int currentMonth = startDate.getMonth();

	threePeriods = new PeriodVO[3];
	if (startMonth % 3 == currentMonth % 3) {
	    // 0:0 1:1 2:2 ���a2��
	    // ��ǰ���g����ʼ���g�����a�ɂ����g
	    threePeriods[0] = periodCurrent;
	    threePeriods[1] = this.getPeriodService().queryNextPeriod(pk_org, threePeriods[0].getBegindate());
	    threePeriods[2] = this.getPeriodService().queryNextPeriod(pk_org, threePeriods[1].getBegindate());
	} else if (((currentMonth % 3) - (startMonth % 3) == 1) || (currentMonth % 3 + 3) - (startMonth % 3) == 1) {
	    // 0:1 1:2 2:0 ǰ���1
	    // ��ǰ���g�����g���g��ǰ����aһ�����g
	    threePeriods[1] = periodCurrent;
	    threePeriods[0] = this.getPeriodService().queryPreviousPeriod(pk_org, threePeriods[1].getBegindate());
	    threePeriods[2] = this.getPeriodService().queryNextPeriod(pk_org, threePeriods[1].getBegindate());
	} else {
	    // 0:2 1:0 2:1 ǰ�a2��
	    // ��ǰ���g������һ�����g��ǰ�a�ɂ����g
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
	    // Ӌ��Ӱ����޽yӋ
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

	throw new BusinessException("�o���ҵ��T���ļӰ����޽yӋ����");
    }

    @Override
    public Map<String, UFDouble[]> settleByFixSalary(Map<String, UFDouble> psnFixSalary, UFLiteralDate startDate,
	    UFLiteralDate endDate) throws BusinessException {
	// TODO �ԄӮa���ķ��� Stub
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
			    // ������
			    holidayHours = holidayHours.add(curNode.getNodeData().getRemainhours());
			} else if (CalendarDateTypeEnum.OFFDAY.toIntValue() == rule.getDatetype()) {
			    // ��Ϣ��
			    offdayHours = offdayHours.add(curNode.getNodeData().getRemainhours());
			} else if (CalendarDateTypeEnum.NORMAL.toIntValue() == rule.getDatetype()) {
			    // ƽ��
			    normalHours = normalHours.add(curNode.getNodeData().getRemainhours());
			} else if (CalendarDateTypeEnum.NATIONALDAY.toIntValue() == rule.getDatetype()) {
			    // ��������
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
			// ��ǰ���c�r������գ���ǰ���c������յģ���������һ���M��
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
			// ��ǰ���c����գ���ǰ���c������յģ���������һ���M��
			if (curNode.getNodeData() != null) {
			    if ((curNode.getNodeData().getRegdate().isSameDate(overtimeDate) || curNode.getNodeData()
				    .getRegdate().after(overtimeDate))
				    && curNode.getNodeData().getRegdate().isSameDate(endDate)
				    || curNode.getNodeData().getRegdate().before(endDate)) {
				if (!ret.containsKey(pk_psndoc)) {
				    ret.put(pk_psndoc, UFDouble.ZERO_DBL);
				}

				// ����ȡ���ڕrн
				UFDouble hourpay = getPsnHourPay(pk_psndoc, curNode.getNodeData().getRegdate(),
					DaySalaryEnum.TBMHOURSALARY);
				// �c��ӛ䛿��ڕrн��ͬ�r����Ӌ��ʣ��Ӱ��M���~
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

	Map<OvertimeSettleTypeEnum, UFDouble> workDayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>(); // ƽ��
	Map<OvertimeSettleTypeEnum, UFDouble> holidayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>(); // ������
	Map<OvertimeSettleTypeEnum, UFDouble> offdayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// ��Ϣ��
	Map<OvertimeSettleTypeEnum, UFDouble> nationalDayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// ����
	Map<OvertimeSettleTypeEnum, UFDouble> totalWorkAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// ƽ�պϼ�
	Map<OvertimeSettleTypeEnum, UFDouble> totalHolidayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// �����պϼ�
	Map<OvertimeSettleTypeEnum, UFDouble> totalOffdayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// ��Ϣ�պϼ�
	Map<OvertimeSettleTypeEnum, UFDouble> totalNationalDayAmount = new HashMap<OvertimeSettleTypeEnum, UFDouble>();// ���ٺϼ�

	if (curNode != null) {
	    Map<String, SegRuleVO> ruleMap = new HashMap<String, SegRuleVO>();
	    while (curNode != null) {
		// ��ǰ���c����գ���ǰ���c������յģ���������һ���M��
		if (curNode.getNodeData() != null) {
		    String pk_segrule = curNode.getNodeData().getPk_segrule();
		    // �Ӱ��������ڼ���
		    if (!curNode.getNodeData().getRegdate().after(endDate)
			    && !curNode.getNodeData().getRegdate().before(beginDate)) {
			if (!ruleMap.containsKey(pk_segrule)) {
			    // ����Ӱ�ֶ�����
			    ruleMap.put(pk_segrule,
				    (SegRuleVO) this.getBaseDao().retrieveByPK(SegRuleVO.class, pk_segrule));
			}

			if (dateType != null) {
			    if (CalendarDateTypeEnum.NORMAL.equals(dateType)) {
				// ͳ��ƽ��
				addInPeriodAmount(dateType, curNode, workDayAmount, ruleMap, pk_segrule);
			    } else if (CalendarDateTypeEnum.HOLIDAY.equals(dateType)) {
				// ͳ��������
				addInPeriodAmount(dateType, curNode, holidayAmount, ruleMap, pk_segrule);
			    } else if (CalendarDateTypeEnum.OFFDAY.equals(dateType)) {
				// ͳ����Ϣ��
				addInPeriodAmount(dateType, curNode, offdayAmount, ruleMap, pk_segrule);
			    } else if (CalendarDateTypeEnum.NATIONALDAY.equals(dateType)) {
				// ͳ�ƹ���
				addInPeriodAmount(dateType, curNode, nationalDayAmount, ruleMap, pk_segrule);
			    }
			} else {
			    // ͳ��ƽ��
			    addInPeriodAmount(CalendarDateTypeEnum.NORMAL, curNode, workDayAmount, ruleMap, pk_segrule);
			    // ͳ��������
			    addInPeriodAmount(CalendarDateTypeEnum.HOLIDAY, curNode, holidayAmount, ruleMap, pk_segrule);
			    // ͳ����Ϣ��
			    addInPeriodAmount(CalendarDateTypeEnum.OFFDAY, curNode, offdayAmount, ruleMap, pk_segrule);
			    // ͳ�ƹ���
			    addInPeriodAmount(CalendarDateTypeEnum.NATIONALDAY, curNode, nationalDayAmount, ruleMap,
				    pk_segrule);
			}
		    }
		    // ��������ڷ�Χ��
		    else if (!curNode.getNodeData().getApproveddate().after(endDate)
			    && !curNode.getNodeData().getApproveddate().before(beginDate)) {
			if (dateType != null && !curNode.getNodeData().getIscompensation().booleanValue()) {
			    if (CalendarDateTypeEnum.NORMAL.equals(dateType)) {
				// ͳ��ƽ��
				addInMap(workDayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
					.getRemainhours());
			    } else if (CalendarDateTypeEnum.HOLIDAY.equals(dateType)) {
				// ͳ��������
				addInMap(holidayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
					.getRemainhours());
			    } else if (CalendarDateTypeEnum.OFFDAY.equals(dateType)) {
				// ͳ����Ϣ��
				addInMap(offdayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
					.getRemainhours());
			    } else if (CalendarDateTypeEnum.NATIONALDAY.equals(dateType)) {
				// ͳ�ƹ���
				addInMap(nationalDayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode
					.getNodeData().getRemainhours());
			    }
			} else {
			    if (!curNode.getNodeData().getIscompensation().booleanValue()) {
				// ͳ��ƽ��
				addInMap(workDayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
					.getRemainhours());
				// ͳ��������
				addInMap(holidayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
					.getRemainhours());
				// ͳ����Ϣ��
				addInMap(offdayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode.getNodeData()
					.getRemainhours());
				// ͳ�ƹ���
				addInMap(nationalDayAmount, OvertimeSettleTypeEnum.OTHER_TOSALARY, curNode
					.getNodeData().getRemainhours());
			    }
			}
		    } else {
			// ������ֹʱ�伴Ϊ����
			break;
		    }
		}
		curNode = curNode.getNextNode();
	    }

	    // �����ϼ�
	    // ƽ��
	    addInMap(totalWorkAmount, OvertimeSettleTypeEnum.TOTAL,
		    workDayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOSALARY));
	    addInMap(totalWorkAmount, OvertimeSettleTypeEnum.TOTAL,
		    workDayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOREST));
	    addInMap(totalWorkAmount, OvertimeSettleTypeEnum.TOTAL,
		    workDayAmount.get(OvertimeSettleTypeEnum.OTHER_TOSALARY));
	    // ������
	    addInMap(totalHolidayAmount, OvertimeSettleTypeEnum.TOTAL,
		    holidayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOSALARY));
	    addInMap(totalHolidayAmount, OvertimeSettleTypeEnum.TOTAL,
		    holidayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOREST));
	    addInMap(totalHolidayAmount, OvertimeSettleTypeEnum.TOTAL,
		    holidayAmount.get(OvertimeSettleTypeEnum.OTHER_TOSALARY));
	    // ��Ϣ��
	    addInMap(totalOffdayAmount, OvertimeSettleTypeEnum.TOTAL,
		    offdayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOSALARY));
	    addInMap(totalOffdayAmount, OvertimeSettleTypeEnum.TOTAL,
		    offdayAmount.get(OvertimeSettleTypeEnum.PERIOD_TOREST));
	    addInMap(totalOffdayAmount, OvertimeSettleTypeEnum.TOTAL,
		    offdayAmount.get(OvertimeSettleTypeEnum.OTHER_TOSALARY));
	    // ����
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
		// ����תн
		addInMap(workDayAmount, OvertimeSettleTypeEnum.PERIOD_TOSALARY, curNode.getNodeData().getRemainhours());
	    } else {
		// ����ת��
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
		UFDouble totalAmount = UFDouble.ZERO_DBL; // ����
		UFDouble spentAmount = UFDouble.ZERO_DBL;// ����
		UFDouble remainAmount = UFDouble.ZERO_DBL;// ʣ��
		UFDouble frozenAmount = UFDouble.ZERO_DBL;// ����
		UFDouble useableAmount = UFDouble.ZERO_DBL; // ����
		Map<String, UFLiteralDate> otDateMap = new HashMap<String, UFLiteralDate>();// �Ӱ�����
		Map<String, UFDouble> otTotalHoursMap = new HashMap<String, UFDouble>(); // �Ӱ�����
		Map<String, UFDouble> otSpentHoursMap = new HashMap<String, UFDouble>();// �Ӱ�����
		Map<String, UFDouble> otFrozenHoursMap = new HashMap<String, UFDouble>();// �Ӱඳ��
		Map<String, UFBoolean> otClosedMap = new HashMap<String, UFBoolean>();// �Ƿ����
		List<String> otList = new ArrayList<String>();
		OTSChainNode curNode = OTSChainUtils.buildChainNodes(pk_psndoc, null, null, true, false, true, false,
			false); // ����Ա���ؼӰ�ת���ݷֶ���ϸ

		OTLeaveBalanceVO headVo = null;
		if (curNode == null) {
		    headVo = createNewHeadVO(pk_org, pk_psndoc, totalAmount, spentAmount, remainAmount, frozenAmount,
			    useableAmount);
		    continue;
		}
		curNode = OTSChainUtils.getFirstNode(curNode); // ȡ��һ���ڵ�
		while (curNode != null) {
		    if (curNode.getNodeData() != null) {
			UFLiteralDate expireDate = curNode.getNodeData().getExpirydate();
			String pk_overtimereg = curNode.getNodeData().getPk_overtimereg();
			if (expireDate != null) {
			    if ((expireDate.isSameDate(beginDate) || expireDate.after(beginDate))
				    && (expireDate.isSameDate(endDate) || expireDate.before(endDate))) {
				totalAmount = totalAmount.add(getUFDouble(curNode.getNodeData().getHours()));// ����
				spentAmount = spentAmount.add(getUFDouble(curNode.getNodeData().getConsumedhours()));// ����
				remainAmount = remainAmount.add(getUFDouble(curNode.getNodeData().getRemainhours()));// ʣ��
				frozenAmount = frozenAmount.add(getUFDouble(curNode.getNodeData().getFrozenhours()));// ����
				collectOvertimeData(otDateMap, otTotalHoursMap, otSpentHoursMap, otFrozenHoursMap,
					otClosedMap, otList, curNode, pk_overtimereg); // ͳ�ƼӰ�����
			    }
			}
		    }
		    curNode = curNode.getNextNode();
		}
		useableAmount = remainAmount.sub(frozenAmount);

		// ������ͷ
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
	// �Ӱ�����
	if (!otDateMap.containsKey(pk_overtimereg)) {
	    otDateMap.put(pk_overtimereg, curNode.getNodeData().getRegdate());
	}

	// �Ӱ�����
	if (!otTotalHoursMap.containsKey(pk_overtimereg)) {
	    otTotalHoursMap.put(pk_overtimereg, getUFDouble(curNode.getNodeData().getHours()));
	} else {
	    otTotalHoursMap
		    .put(pk_overtimereg,
			    getUFDouble(otTotalHoursMap.get(pk_overtimereg).add(
				    getUFDouble(curNode.getNodeData().getHours()))));
	}

	// �Ӱ�����
	if (!otSpentHoursMap.containsKey(pk_overtimereg)) {
	    otSpentHoursMap.put(pk_overtimereg, getUFDouble(curNode.getNodeData().getConsumedhours()));
	} else {
	    otSpentHoursMap.put(
		    pk_overtimereg,
		    getUFDouble(otSpentHoursMap.get(pk_overtimereg).add(
			    getUFDouble(curNode.getNodeData().getConsumedhours()))));
	}

	// �Ӱඳ��
	if (!otFrozenHoursMap.containsKey(pk_overtimereg)) {
	    otFrozenHoursMap.put(pk_overtimereg, getUFDouble(curNode.getNodeData().getFrozenhours()));
	} else {
	    otFrozenHoursMap.put(
		    pk_overtimereg,
		    getUFDouble(otFrozenHoursMap.get(pk_overtimereg).add(
			    getUFDouble(curNode.getNodeData().getFrozenhours()))));
	}

	// �Ƿ����
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
	    Map<String, UFLiteralDate> otDateMap = new HashMap<String, UFLiteralDate>();// �Ӱ�����
	    Map<String, UFDouble> otTotalHoursMap = new HashMap<String, UFDouble>(); // �Ӱ�����
	    Map<String, UFDouble> otSpentHoursMap = new HashMap<String, UFDouble>();// �Ӱ�����
	    Map<String, UFDouble> otFrozenHoursMap = new HashMap<String, UFDouble>();// �Ӱඳ��
	    Map<String, UFBoolean> otClosedMap = new HashMap<String, UFBoolean>();// �Ƿ����
	    List<String> otList = new ArrayList<String>();
	    OTSChainNode curNode = OTSChainUtils
		    .buildChainNodes(pk_psndoc, null, null, true, false, true, false, false); // ����Ա���ؼӰ�ת���ݷֶ���ϸ

	    curNode = OTSChainUtils.getFirstNode(curNode); // ȡ��һ���ڵ�
	    while (curNode != null) {
		if (curNode.getNodeData() != null) {
		    UFLiteralDate expireDate = curNode.getNodeData().getExpirydate();
		    String pk_overtimereg = curNode.getNodeData().getPk_overtimereg();
		    if (expireDate != null) {
			if ((expireDate.isSameDate(beginDate) || expireDate.after(beginDate))
				&& (expireDate.isSameDate(endDate) || expireDate.before(endDate))) {
			    collectOvertimeData(otDateMap, otTotalHoursMap, otSpentHoursMap, otFrozenHoursMap,
				    otClosedMap, otList, curNode, pk_overtimereg); // ͳ�ƼӰ�����
			}
		    }
		}
		curNode = curNode.getNextNode();
	    }

	    // ��������
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