package nc.ui.overtime.otleavebalance.action;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.ui.dcm.chnlrplstrct.maintain.action.MessageDialog;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModeDataManager;
import nc.ui.overtime.otleavebalance.model.OTLeaveBalanceModel;
import nc.ui.overtime.otleavebalance.view.OTLeaveBalanceOrgPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.editor.IBillListPanelView;
import nc.vo.pub.BusinessException;
import nc.vo.ta.overtime.OTLeaveBalanceVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;

import org.apache.commons.lang.StringUtils;

public class RebuildPsnSegDetailAction extends HrAction {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = 6101162527113648376L;
	private IBillListPanelView listView;
	private OTLeaveBalanceOrgPanel orgpanel;
	String error = null;

	public RebuildPsnSegDetailAction() {
		setCode("REBUILDPSNSEG");
		setBtnName("重建分段");
	}

	public Container getParentContainer() {
		return SwingUtilities.getWindowAncestor(getContext().getEntranceUI());
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		OTLeaveBalanceModel headModel = ((OTLeaveBalanceModel) this.getModel());
		OTLeaveBalanceVO otlbvo = (OTLeaveBalanceVO) headModel.getSelectedData();
		final String pk_psndoc = otlbvo.getPk_psndoc();
		BillItem billItem = this.getListView().getBillListPanel().getHeadItem("pk_psndoc");
		// 取員工Code
		UIRefPane pane = (UIRefPane) billItem.getComponent();
		Vector colval = pane.getRefModel().matchPkData(pk_psndoc);

		if (MessageDialog.showOkCancelDlg(this.getEntranceUI(), "確認", "是否重建員工 [" + ((Vector) colval.get(0)).get(0)
				+ "] 的未結算分段資料？") == MessageDialog.ID_OK) {
			// TODO 創建運行線程
			new SwingWorker<Boolean, Void>() {
				BannerTimerDialog dialog = new BannerTimerDialog(getParentContainer());

				@Override
				protected Boolean doInBackground() throws Exception {
					try {
						dialog.setStartText("正在重建分段資料...");
						dialog.start();

						IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
						String strSQL = "";
						// TODO 取人員重建未結算區間
						strSQL = "select pk_overtimereg from hrta_segdetail where pk_psndoc='" + pk_psndoc
								+ "' and nodecode = (select min(nodecode) from hrta_segdetail where pk_psndoc='"
								+ pk_psndoc + "' and issettled = 'N')";
						String pk_firstotreg = (String) query.executeQuery(strSQL, new ColumnProcessor());

						if (StringUtils.isEmpty(pk_firstotreg)) {

						}

						// TODO 取人員未結算區間加班單

						// TODO 取人員未結算區間休假單

						// TODO 清理未結算數據

						// TODO 重建已結算鏈表索引
						// select pk, pk_parent order by code

						// TODO 重建加班數據

						// TODO 重建休假數據
					} catch (Exception e) {
						error = e.getMessage();
					} finally {
						dialog.end();
					}
					return Boolean.TRUE;
				}
			}.execute();
		}
	}

	public IBillListPanelView getListView() {
		return listView;
	}

	public void setListView(IBillListPanelView listView) {
		this.listView = listView;
	}

	protected boolean isActionEnable() {
		try {
			String pk_otleavetype = SysInitQuery.getParaString(getContext().getPk_org(), "TWHRT08");

			LeaveTypeCopyVO leaveTypeVo = (LeaveTypeCopyVO) ((OTLeaveBalanceModeDataManager) getOrgpanel()
					.getDataManager()).getHierachicalModel().getSelectedData();

			if (leaveTypeVo != null && leaveTypeVo.getPk_timeitemcopy().equals(pk_otleavetype)) {
				OTLeaveBalanceModel headModel = ((OTLeaveBalanceModel) this.getModel());
				if ((OTLeaveBalanceVO) headModel.getSelectedData() != null) {
					return true;
				}
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}

		return false;
	}

	public OTLeaveBalanceOrgPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(OTLeaveBalanceOrgPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

}
