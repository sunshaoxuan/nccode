package nc.pubimpl.ta.leaveextrarest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.hr.utils.InSQLCreator;
import nc.impl.pub.OTLeaveBalanceUtils;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.ta.leaveextrarest.ILeaveExtraRestService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leaveextrarest.LeaveExtraRestVO;
import nc.vo.ta.overtime.OTBalanceDetailVO;
import nc.vo.ta.overtime.OTLeaveBalanceVO;
import nc.vo.ta.overtime.SoureBillTypeEnum;

import org.apache.commons.lang.StringUtils;

public class LeaveExtraRestServiceImpl implements ILeaveExtraRestService {

	private BaseDAO baseDao = null;

	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

	@Override
	public void settledByExpiryDate(String pk_org, String[] pk_psndocs, UFLiteralDate settleDate, Boolean isForce)
			throws BusinessException {
		String strCondition = " dr=0 and isnull(issettled,'N')='N' ";

		if (isForce) {
			// ����鏊�ƽY�㣬���ָ���ˆT�б��������S�����M�����ƽY��
			if (pk_psndocs == null || pk_psndocs.length == 0) {
				throw new BusinessException("���ƽY���e�`��δָ���Y���ˆT��");
			}
		} else {
			// �Ǐ��ƽY��r���Y�����ڲ��ܞ��
			if (settleDate == null) {
				throw new BusinessException("�Y���e�`���Y�����ڲ��ܞ�ա�");
			}

			strCondition += " and expiredate <='" + settleDate.toString() + "' ";
		}

		// ���˞��ȣ�ָ���ˆT�ģ���ָ���ˆT�M�нY�㣬��t�������M���M�нY��
		if (pk_psndocs == null || pk_psndocs.length == 0) {
			if (StringUtils.isEmpty(pk_org)) {
				throw new BusinessException("�Y���e�`���Y��M���ͽY���ˆT����ͬ�r��ա�");
			}

			strCondition += " and pk_org = '" + pk_org + "'";
		} else {
			InSQLCreator insql = new InSQLCreator();
			strCondition += " and pk_psndoc in (" + insql.getInSQL(pk_psndocs) + ")";
		}

		this.getBaseDao().executeUpdate(
				"update " + LeaveExtraRestVO.getDefaultTableName() + " set " + LeaveExtraRestVO.ISSETTLED + "='Y', "
						+ LeaveExtraRestVO.SETTLEDATE + "='" + settleDate.toString() + "' where " + strCondition);

	}

	@Override
	public OTLeaveBalanceVO[] getLeaveExtHoursByType(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate, String pk_leavetypecopy, boolean isSettled) throws BusinessException {

		List<OTLeaveBalanceVO> headvos = new ArrayList<OTLeaveBalanceVO>();

		pk_psndocs = OTLeaveBalanceUtils.getPsnListByDateScope(pk_org, pk_psndocs, beginDate, endDate);

		Map<String, UFLiteralDate> psnWorkStartDate = OTLeaveBalanceUtils.getPsnOrgMapByPsnBeginDate(pk_psndocs,
				beginDate, endDate);

		UFDouble dayHoursRate = getDayHourRate(pk_org, pk_leavetypecopy);

		if (pk_psndocs != null && pk_psndocs.length > 0) {
			for (String pk_psndoc : pk_psndocs) {
				// �������g�_ʼ�r�g��λ�T�����Y�������g
				UFLiteralDate psnBeginDate = OTLeaveBalanceUtils.getBeginDateByWorkAgeStartDate(
						String.valueOf(beginDate.getYear()), psnWorkStartDate.get(pk_psndoc));
				UFLiteralDate psnEndDate = OTLeaveBalanceUtils.getEndDateByWorkAgeStartDate(
						String.valueOf(beginDate.getYear()), psnWorkStartDate.get(pk_psndoc));
				if (psnBeginDate.after(beginDate)) {
					psnBeginDate = OTLeaveBalanceUtils.getBeginDateByWorkAgeStartDate(
							String.valueOf(beginDate.getYear() - 1), psnWorkStartDate.get(pk_psndoc));
					psnEndDate = OTLeaveBalanceUtils.getEndDateByWorkAgeStartDate(
							String.valueOf(beginDate.getYear() - 1), psnWorkStartDate.get(pk_psndoc));
				}

				//

				UFDouble totalAmount = UFDouble.ZERO_DBL; // ����
				UFDouble spentAmount = UFDouble.ZERO_DBL;// ����
				UFDouble remainAmount = UFDouble.ZERO_DBL;// ʣ��
				UFDouble frozenAmount = UFDouble.ZERO_DBL;// ����
				UFDouble useableAmount = UFDouble.ZERO_DBL; // ����
				UFDouble unusableAmount = UFDouble.ZERO_DBL;// ������

				UFLiteralDate settleddate = null;

				LeaveExtraRestVO[] extvos = getLeaveExtraRestVOsByPsnDate(pk_org, pk_psndoc, psnBeginDate, psnEndDate,
						isSettled);
				LeaveRegVO[] leaveRegVOs = OTLeaveBalanceUtils.getLeaveRegByPsnDate(pk_org, pk_psndoc, psnBeginDate,
						psnEndDate, pk_leavetypecopy);
				// ����
				for (LeaveExtraRestVO vo : extvos) {
					UFDouble curAmount = OTLeaveBalanceUtils.getUFDouble(vo.getChangedayorhour())
							.multiply(dayHoursRate);
					totalAmount = totalAmount.add(curAmount);

					if (UFBoolean.TRUE.equals(vo.getIssettled())) {
						unusableAmount = unusableAmount.add(curAmount);
						settleddate = vo.getSettledate();
					}
				}

				if (!totalAmount.equals(UFDouble.ZERO_DBL)) {
					// ����
					if (leaveRegVOs != null && leaveRegVOs.length > 0) {
						for (LeaveRegVO lrvo : leaveRegVOs) {
							spentAmount = spentAmount.add(OTLeaveBalanceUtils.getUFDouble(lrvo.getLeavehour())
									.multiply(dayHoursRate));
						}
					}

					if (unusableAmount.doubleValue() > 0) {
						unusableAmount = unusableAmount.sub(spentAmount);
					}
					// ʣ�N
					remainAmount = remainAmount.add(totalAmount.sub(spentAmount));
					// ����
					useableAmount = useableAmount.add(totalAmount.sub(spentAmount).sub(frozenAmount)).sub(
							unusableAmount);

					if (!totalAmount.equals(UFDouble.ZERO_DBL) || !spentAmount.equals(UFDouble.ZERO_DBL)
							|| !remainAmount.equals(UFDouble.ZERO_DBL) || !frozenAmount.equals(UFDouble.ZERO_DBL)
							|| !useableAmount.equals(UFDouble.ZERO_DBL)) {
						// ������ͷ
						OTLeaveBalanceVO headVo = OTLeaveBalanceUtils.createNewHeadVO(pk_org, pk_psndoc, totalAmount,
								spentAmount, remainAmount, frozenAmount, useableAmount);
						headVo.setQstartdate(psnBeginDate);
						headVo.setQenddate(psnEndDate);
						headVo.setSettleddate(settleddate);
						headvos.add(headVo);
					}
				}
			}
		}

		return headvos.toArray(new OTLeaveBalanceVO[0]);
	}

	@SuppressWarnings("unchecked")
	private LeaveExtraRestVO[] getLeaveExtraRestVOsByPsnDate(String pk_org, String pk_psndoc, UFLiteralDate beginDate,
			UFLiteralDate endDate, boolean isSettled) throws BusinessException {
		String strSQL = "pk_org='" + pk_org + "' and pk_psndoc='" + pk_psndoc + "' and expiredate between '"
				+ beginDate.toString() + "' and '" + endDate.toString() + "' and dr=0 "
				+ (isSettled ? " and isnull(issettled,'~')='Y' " : "");
		Collection<LeaveExtraRestVO> vos = this.getBaseDao().retrieveByClause(LeaveExtraRestVO.class, strSQL);

		if (vos != null && vos.size() > 0) {
			return vos.toArray(new LeaveExtraRestVO[0]);
		}

		return new LeaveExtraRestVO[0];
	}

	@Override
	public OTBalanceDetailVO[] getLeaveExtByType(String pk_org, String pk_psndoc, String queryYear,
			UFLiteralDate beginDate, UFLiteralDate endDate, String pk_leavetypecopy, boolean isSettled)
			throws BusinessException {
		List<OTBalanceDetailVO> detailVOs = new ArrayList<OTBalanceDetailVO>();

		if (!StringUtils.isEmpty(pk_psndoc)) {
			LeaveExtraRestVO[] extvos = getLeaveExtraRestVOsByPsnDate(pk_org, pk_psndoc, beginDate, endDate, isSettled);
			LeaveRegVO[] leaveRegVOs = OTLeaveBalanceUtils.getLeaveRegByPsnDate(pk_org, pk_psndoc, beginDate, endDate,
					pk_leavetypecopy);
			String pk_otleavetype = SysInitQuery.getParaString(pk_org, "TWHRT10");
			// ����
			UFDouble spentAmount = UFDouble.ZERO_DBL;
			if (pk_otleavetype != null && pk_otleavetype.equals(pk_leavetypecopy)) {
				if (leaveRegVOs != null && leaveRegVOs.length > 0) {
					for (LeaveRegVO lrvo : leaveRegVOs) {
						spentAmount = spentAmount.add(lrvo.getLeavehour());
					}
				}
			}

			// ��������
			for (LeaveExtraRestVO vo : extvos) {
				OTBalanceDetailVO detailVo = new OTBalanceDetailVO();
				detailVo.setPk_otleavebalance(pk_psndoc);
				detailVo.setSourcetype(SoureBillTypeEnum.EXTRALEAVE.toIntValue());
				detailVo.setPk_sourcebill(vo.getPk_extrarest());
				detailVo.setCalendar(vo.getBilldate());
				detailVo.setBillhours(vo.getChangedayorhour());
				if (spentAmount.doubleValue() >= vo.getChangedayorhour().doubleValue()) {
					detailVo.setConsumedhours(vo.getChangedayorhour());
					spentAmount.sub(vo.getChangedayorhour());
				} else {
					detailVo.setConsumedhours(spentAmount);
					spentAmount = UFDouble.ZERO_DBL;
				}

				detailVo.setFrozenhours(UFDouble.ZERO_DBL);
				detailVo.setCloseflag(new UFBoolean(detailVo.getBillhours().doubleValue() == detailVo
						.getConsumedhours().doubleValue()));
				detailVo.setPk_balancedetail(vo.getPk_extrarest());
				detailVOs.add(detailVo);
			}
		}
		return detailVOs.toArray(new OTBalanceDetailVO[0]);
	}

	@Override
	public void unSettleByPsn(String pk_psndoc) throws BusinessException {
		if (StringUtils.isEmpty(pk_psndoc)) {
			throw new BusinessException("���Y���e�`��δָ���M�з��Y����ˆT��");
		}

		// ���Y������
		String lastSettledDate = (String) this.getBaseDao().executeQuery(
				"select max(" + LeaveExtraRestVO.EXPIREDATE + ") from " + LeaveExtraRestVO.getDefaultTableName()
						+ " where " + LeaveExtraRestVO.ISSETTLED + "='Y' and " + LeaveExtraRestVO.PK_PSNDOC + "='"
						+ pk_psndoc + "'", new ColumnProcessor());
		if (StringUtils.isEmpty(lastSettledDate)) {
			throw new BusinessException("���Y���e�`��δ�ҵ�ָ���ˆT�Ŀɷ��Y������a��ӛ䛡�");
		}

		// ���Y��
		this.getBaseDao().executeUpdate(
				"update " + LeaveExtraRestVO.getDefaultTableName() + " set " + LeaveExtraRestVO.ISSETTLED + "='N', "
						+ LeaveExtraRestVO.SETTLEDATE + "=null where  " + LeaveExtraRestVO.PK_PSNDOC + "='" + pk_psndoc
						+ "' and " + LeaveExtraRestVO.EXPIREDATE + "= '" + lastSettledDate + "'");
	}

	@Override
	public OTLeaveBalanceVO[] getLeaveExtHoursByType(String pk_org, String[] pk_psndocs, String queryYear,
			String pk_leavetypecopy, boolean isSettled) throws BusinessException {
		List<OTLeaveBalanceVO> headvos = new ArrayList<OTLeaveBalanceVO>();

		Map<String, UFLiteralDate> psnWorkStartDate = OTLeaveBalanceUtils.getPsnWorkStartDateMap(pk_org, pk_psndocs,
				queryYear, pk_leavetypecopy);

		UFDouble dayHoursRate = getDayHourRate(pk_org, pk_leavetypecopy);

		if (psnWorkStartDate != null && psnWorkStartDate.keySet().size() > 0) {
			for (String pk_psndoc : psnWorkStartDate.keySet()) {
				if (psnWorkStartDate.get(pk_psndoc) != null) {

					UFLiteralDate beginDate = null;
					UFLiteralDate endDate = null;
					UFLiteralDate settleddate = null;

					beginDate = OTLeaveBalanceUtils.getBeginDateByWorkAgeStartDate(queryYear, psnWorkStartDate,
							pk_psndoc);
					endDate = OTLeaveBalanceUtils.getEndDateByWorkAgeStartDate(queryYear, psnWorkStartDate, pk_psndoc);

					UFDouble totalAmount = UFDouble.ZERO_DBL; // ����
					UFDouble spentAmount = UFDouble.ZERO_DBL;// ����
					UFDouble remainAmount = UFDouble.ZERO_DBL;// ʣ��
					UFDouble frozenAmount = UFDouble.ZERO_DBL;// ����
					UFDouble useableAmount = UFDouble.ZERO_DBL; // ����
					UFDouble unusableAmount = UFDouble.ZERO_DBL;// ������

					LeaveExtraRestVO[] extvos = getLeaveExtraRestVOsByPsnDate(pk_org, pk_psndoc, beginDate, endDate,
							isSettled);
					LeaveRegVO[] leaveRegVOs = OTLeaveBalanceUtils.getLeaveRegByPsnYear(pk_org, pk_psndoc, queryYear,
							pk_leavetypecopy);
					// ����
					for (LeaveExtraRestVO vo : extvos) {
						UFDouble curAmount = OTLeaveBalanceUtils.getUFDouble(vo.getChangedayorhour()).multiply(
								dayHoursRate);
						totalAmount = totalAmount.add(curAmount);

						if (UFBoolean.TRUE.equals(vo.getIssettled())) {
							unusableAmount = unusableAmount.add(curAmount);
							settleddate = vo.getSettledate();
						}
					}

					if (!totalAmount.equals(UFDouble.ZERO_DBL)) {
						// ����
						if (leaveRegVOs != null && leaveRegVOs.length > 0) {
							for (LeaveRegVO lrvo : leaveRegVOs) {
								spentAmount = spentAmount.add(OTLeaveBalanceUtils.getUFDouble(lrvo.getLeavehour())
										.multiply(dayHoursRate));
							}
						}

						if (unusableAmount.doubleValue() > 0) {
							unusableAmount = unusableAmount.sub(spentAmount);
						}

						// ʣ�N
						remainAmount = remainAmount.add(totalAmount.sub(spentAmount));
						// ����
						useableAmount = useableAmount.add(totalAmount.sub(spentAmount).sub(frozenAmount)).sub(
								unusableAmount);

						if (!totalAmount.equals(UFDouble.ZERO_DBL) || !spentAmount.equals(UFDouble.ZERO_DBL)
								|| !remainAmount.equals(UFDouble.ZERO_DBL) || !frozenAmount.equals(UFDouble.ZERO_DBL)
								|| !useableAmount.equals(UFDouble.ZERO_DBL)) {
							// ������ͷ
							OTLeaveBalanceVO headVo = OTLeaveBalanceUtils.createNewHeadVO(pk_org, pk_psndoc,
									totalAmount, spentAmount, remainAmount, frozenAmount, useableAmount);
							headVo.setQstartdate(beginDate);
							headVo.setQenddate(endDate);
							headVo.setSettleddate(settleddate);
							headvos.add(headVo);
						}
					}
				}
			}
		}

		return headvos.toArray(new OTLeaveBalanceVO[0]);
	}

	@SuppressWarnings("unchecked")
	private UFDouble getDayHourRate(String pk_org, String pk_leavetypecopy) throws DAOException {
		// ���ݼ���Ͷ��x�Ć�λ�Q�����ջ�С�rӋ��
		// Collection<TimeRuleVO> timerule =
		// this.getBaseDao().retrieveByClause(TimeRuleVO.class,
		// "pk_org='" + pk_org + "'");
		UFDouble dayHoursRate = new UFDouble(1);
		// if (timerule != null && timerule.size() > 0) {
		// LeaveTypeCopyVO leavetype = (LeaveTypeCopyVO)
		// this.getBaseDao().retrieveByPK(LeaveTypeCopyVO.class,
		// pk_leavetypecopy);
		// dayHoursRate = leavetype.getTimeitemunit() == 1 ?
		// timerule.toArray(new TimeRuleVO[0]).clone()[0]
		// .getDaytohour() : new UFDouble(1);
		// }
		return dayHoursRate;
	}
}