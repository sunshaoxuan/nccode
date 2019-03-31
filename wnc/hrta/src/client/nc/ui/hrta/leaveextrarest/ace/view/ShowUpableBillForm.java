package nc.ui.hrta.leaveextrarest.ace.view;

import nc.bs.framework.common.NCLocator;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.hrta.ILeaveextrarestMaintain;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.pub.bill.BillEditEvent;
import nc.vo.bd.psn.PsnjobVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.leaveextrarest.AggLeaveExtraRestVO;
import nc.vo.ta.leaveextrarest.LeaveExtraRestVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnCalendarVO;

public class ShowUpableBillForm extends nc.ui.pubapp.uif2app.view.ShowUpableBillForm {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 8910293530249474838L;
	IPsnCalendarQueryService service = null;

	@Override
	public void afterEdit(BillEditEvent evt) {

		super.afterEdit(evt);

		if (LeaveExtraRestVO.DATEBEFORECHANGE.equals(evt.getKey())) {
			// 变化前日期
			UFLiteralDate date = (UFLiteralDate) evt.getValue();
			if (date != null) {
				AggLeaveExtraRestVO aggvo = (AggLeaveExtraRestVO) this.getValue();
				String pk_psndoc = aggvo.getParentVO().getPk_psndoc();
				int type = getTypeOfDate(date, pk_psndoc);
				this.billCardPanel.setHeadItem(LeaveExtraRestVO.TYPEBEFORECHANGE, type);

				// 按年资起算日（比对变动前日期）
				try {
					UFLiteralDate maxLeaveDate = 
							NCLocator.getInstance().lookup(ILeaveextrarestMaintain.class)
					.calculateExpireDateByWorkAge(
							pk_psndoc,aggvo.getParentVO().getDatebeforechange(),aggvo.getParentVO().getBilldate());
					this.billCardPanel.setHeadItem(LeaveExtraRestVO.EXPIREDATE, maxLeaveDate);
				} catch (BusinessException e) {
					ExceptionUtils.wrappBusinessException(e.getMessage());
				}
			}
		} else if (LeaveExtraRestVO.DATEAFTERCHANGE.equals(evt.getKey())) {
			// 变化后日期
			UFLiteralDate date = (UFLiteralDate) evt.getValue();
			if (date != null) {
				AggLeaveExtraRestVO aggvo = (AggLeaveExtraRestVO) this.getValue();
				String pk_psndoc = aggvo.getParentVO().getPk_psndoc();
				int type = getTypeOfDate(date, pk_psndoc);
				this.billCardPanel.setHeadItem(LeaveExtraRestVO.TYPEAFTERCHANGE, type);
			}
		}

	}

	private int getTypeOfDate(UFLiteralDate date, String pk_psndoc) {
		try {
			AggPsnCalendar psncld = getService().queryByPsnDate(pk_psndoc, date);
			PsnCalendarVO cldvo = (PsnCalendarVO) psncld.getParentVO();
			if (cldvo != null) {
				return cldvo.getDate_daytype();
			}
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}

		return -1;
	}

	public IPsnCalendarQueryService getService() {
		if (service == null) {
			service = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		}
		return service;
	}
}
