package nc.ui.twhr.twhr.action;

import java.awt.event.ActionEvent;

import nc.ui.pubapp.uif2app.model.BatchModelDataManager;
import nc.ui.twhr.twhr.ace.view.AccountOrgHeadPanel;
import nc.ui.uif2.model.BatchBillTableModel;

public class BatchRefreshAction extends nc.ui.uif2.actions.batch.BatchRefreshAction {
	private static final long serialVersionUID = -6596206455524975067L;
	private BatchModelDataManager batchModelModelDataManagerAna;

	private BatchModelDataManager batchDataManager;

	public BatchModelDataManager getBatchDataManager() {
		return batchDataManager;
	}

	public void setBatchDataManager(BatchModelDataManager batchDataManager) {
		this.batchDataManager = batchDataManager;
	}

	private AccountOrgHeadPanel orgpanel;

	public AccountOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(AccountOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

	public BatchModelDataManager getBatchModelModelDataManagerAna() {
		return batchModelModelDataManagerAna;
	}

	public void setBatchModelModelDataManagerAna(BatchModelDataManager batchModelModelDataManagerAna) {
		this.batchModelModelDataManagerAna = batchModelModelDataManagerAna;
	}

	public BatchRefreshAction() {
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		this.getBatchModelModelDataManagerAna().initModelBySqlWhere(
				" and pk_org='" + orgpanel.getRefPane().getRefPK() + "' and pk_period='"
						+ orgpanel.getWaPeriodRefPane().getRefModel().getRefNameValue() + "' and dr=0 ");
		this.getBatchDataManager().initModelBySqlWhere(
				" and pk_org='" + orgpanel.getRefPane().getRefPK() + "' and pk_period='"
						+ orgpanel.getWaPeriodRefPane().getRefModel().getRefNameValue() + "' and dr=0 ");

	}

	public void setModel(BatchBillTableModel model) {
		super.setModel(model);
		model.addAppEventListener(this);
	}
}
