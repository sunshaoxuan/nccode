package nc.ui.om.hrdept.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFDate;
import nc.vo.vorg.DeptVersionVO;

import org.apache.commons.lang.StringUtils;

public class CheckVersionErrorAction extends HrAction {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 8767320447684882946L;

	public CheckVersionErrorAction() {
		this.setBtnName("版本檢查");
		this.setCode("CheckVersionErrorAction");
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		new SwingWorker() {

			BannerTimerDialog dialog = new BannerTimerDialog(SwingUtilities.getWindowAncestor(getModel().getContext()
					.getEntranceUI()));
			String error = null;

			@SuppressWarnings("unchecked")
			protected Boolean doInBackground() throws Exception {
				try {
					dialog.setStartText("正在進行部門版本資料檢查");
					dialog.start();

					Map<String, String> errorDept = new HashMap<String, String>();
					List<String> rightDept = new ArrayList<String>();
					IUAPQueryBS svc = NCLocator.getInstance().lookup(IUAPQueryBS.class);
					Collection<DeptVersionVO> vos = svc
							.retrieveByClause(DeptVersionVO.class,
									"pk_dept in (select pk_dept from org_dept where pk_org = '"
											+ getModel().getContext().getPk_org()
											+ "' and isnull(dr, 0)=0) and isnull(dr,0)=0");
					if (vos != null && vos.size() > 0) {
						for (DeptVersionVO vo : vos) {
							if (!errorDept.containsKey(vo.getPk_dept()) && !rightDept.contains(vo.getPk_dept())) {
								List<DeptVersionVO> vlist = getDeptVersionList(vo.getPk_dept(), vos);
								if (checkVList(vlist, errorDept)) {
									rightDept.add(vo.getPk_dept());
								}
							}
						}

						StringBuilder sbRst = new StringBuilder();
						if (errorDept.size() > 0) {
							sbRst.append("以下部門版本期間發生重疊\r\n");
						}

						for (String pk_dept : errorDept.keySet()) {
							DeptVO vo = (DeptVO) svc.retrieveByPK(DeptVO.class, pk_dept);
							sbRst.append("部門 [" + vo.getCode() + "] : " + errorDept.get(vo.getPk_dept()) + "\r\n");
						}

						error = sbRst.toString();
					}

				} catch (LockFailedException le) {
					error = le.getMessage();
				} catch (VersionConflictException le) {
					throw new BusinessException(le.getBusiObject().toString(), le);
				} catch (Exception e) {
					error = e.getMessage();
				} finally {
					dialog.end();
				}
				return Boolean.TRUE;
			}

			protected void done() {
				if (!StringUtils.isEmpty(error)) {
					nc.ui.pub.beans.MessageDialog.showErrorDlg(getEntranceUI(), "部門版本檢查錯誤", error);
				} else {
					ShowStatusBarMsgUtil.showStatusBarMsg("部門版本檢查通過。", getModel().getContext());
				}
			}
		}.execute();
	}

	private boolean checkVList(List<DeptVersionVO> vlist, Map<String, String> errorDept) {
		boolean notCoverted = true;
		if (vlist != null && vlist.size() > 0) {
			for (int i = 0; i < vlist.size() - 1; i++) {
				for (int j = i + 1; j < vlist.size(); j++) {
					if (timeCovered(vlist.get(i), vlist.get(j))) {
						errorDept.put(vlist.get(i).getPk_dept(), "版本 [" + vlist.get(i).getVno() + "] - 版本 ["
								+ vlist.get(j).getVno() + "]");
						notCoverted = false;
					}
				}
			}
		}
		return notCoverted;
	}

	private boolean timeCovered(DeptVersionVO dv1, DeptVersionVO dv2) {
		if (dv1.getVenddate() == null) {
			dv1.setVenddate(new UFDate("9999-12-31 00:00:00"));
		}

		if (dv2.getVenddate() == null) {
			dv2.setVenddate(new UFDate("9999-12-31 00:00:00"));
		}

		if ((dv1.getVstartdate().toUFLiteralDate(ICalendar.BASE_TIMEZONE)
				.before(dv2.getVenddate().toUFLiteralDate(ICalendar.BASE_TIMEZONE)) && dv1.getVenddate()
				.toUFLiteralDate(ICalendar.BASE_TIMEZONE)
				.after(dv2.getVstartdate().toUFLiteralDate(ICalendar.BASE_TIMEZONE)))
				|| (dv2.getVstartdate().toUFLiteralDate(ICalendar.BASE_TIMEZONE)
						.before(dv1.getVenddate().toUFLiteralDate(ICalendar.BASE_TIMEZONE)) && dv2.getVenddate()
						.toUFLiteralDate(ICalendar.BASE_TIMEZONE)
						.after(dv1.getVstartdate().toUFLiteralDate(ICalendar.BASE_TIMEZONE)))) {
			return true;
		}

		return false;
	}

	private List<DeptVersionVO> getDeptVersionList(String pk_dept, Collection<DeptVersionVO> vos) {
		List<DeptVersionVO> vlist = new ArrayList<DeptVersionVO>();
		for (DeptVersionVO vo : vos) {
			if (vo.getPk_dept().equals(pk_dept)) {
				vlist.add(vo);
			}
		}
		return vlist;
	}

	protected boolean isActionEnable() {
		NODE_TYPE nodeType = getModel().getContext().getNodeType();

		if (nodeType != NODE_TYPE.ORG_NODE) {
			return (getModel().getUiState() == UIState.INIT) || (getModel().getUiState() == UIState.NOT_EDIT);
		}

		return StringUtils.isNotBlank(getModel().getContext().getPk_org());
	}
}
