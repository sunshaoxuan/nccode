package nc.ui.twhr.twhr.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.twhr.IDiffinsuranceaMaintain;
import nc.jdbc.framework.PersistenceManager;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.pubapp.uif2app.model.BatchBillTableModel;
import nc.ui.pubapp.uif2app.model.BatchModelDataManager;
import nc.ui.uif2.NCAction;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.twhr.nhicalc.BaoAccountVO;

public class ReconciliationAction extends NCAction {
	private BatchBillTableModel modeldiff = null;
	private BatchModelDataManager modelManager = null;
	private BatchBillTableModel modeltwhr = null;
	private NCAction batchRefreshAction;
	private boolean isDel = false;
	private boolean isBal = false;
	private String errorMessage = "";
	private String pk_org = "";
	private String pk_period = "";

	public BatchBillTableModel getModeldiff() {
		return modeldiff;
	}

	public NCAction getBatchRefreshAction() {
		return batchRefreshAction;
	}

	public void setBatchRefreshAction(NCAction batchRefreshAction) {
		this.batchRefreshAction = batchRefreshAction;
	}

	public void setModeldiff(BatchBillTableModel modeldiff) {
		this.modeldiff = modeldiff;
	}

	public BatchBillTableModel getModeltwhr() {
		return modeltwhr;
	}

	public void setModeltwhr(BatchBillTableModel modeltwhr) {
		this.modeltwhr = modeltwhr;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		BaoAccountVO bvo = (BaoAccountVO) modeltwhr.getSelectedData();
		if (bvo == null) {
			ExceptionUtils.wrappBusinessException("請檢查是否選擇組織或日期");
		} else if (bvo != null) {
			pk_org = bvo.getPk_org();
			pk_period = bvo.getPk_period();

			if (getService().ifexist(pk_org, pk_period)) {
				if (MessageDialog.showYesNoDlg(modeltwhr.getContext().getEntranceUI(), "是否覆蓋", "已存在差異分析數據是否覆蓋!") == UIDialog.ID_YES) {
					isDel = true;
					isBal = true;
				}
			} else {
				isDel = false;
				isBal = true;
			}

			new Thread(new Runnable() {
				@Override
				public void run() {
					IProgressMonitor progressMonitor = NCProgresses.createDialogProgressMonitor(getModeltwhr()
							.getContext().getEntranceUI());

					progressMonitor.beginTask("對賬", -1);
					progressMonitor.setProcessInfo("對賬中，請稍候...");
					try {
						if (isDel)
							getService().Delete(pk_org, pk_period);

						if (isBal)
							getService().Blanace(pk_org, pk_period);

						if (isDel || isBal) {
							getModelManager().initModel();
							getBatchRefreshAction().doAction(null);
						}
					} catch (Exception e) {
						Logger.error(e);
						MessageDialog.showErrorDlg(getModeltwhr().getContext().getEntranceUI(), null, e.getMessage());
					} finally {
						progressMonitor.done();
					}
				}
			}).start();

		}
	}

	private IDiffinsuranceaMaintain service;

	public IDiffinsuranceaMaintain getService() {
		if (null == service) {
			service = NCLocator.getInstance().lookup(IDiffinsuranceaMaintain.class);

		}
		return service;
	}

	@Override
	protected boolean isActionEnable() {
		// if(model.)
		return true;
	}

	public ReconciliationAction() {
		setBtnName("對賬");
		setCode("reconciliation");
	}

	public BatchModelDataManager getModelManager() {
		return modelManager;
	}

	public void setModelManager(BatchModelDataManager modelManager) {
		this.modelManager = modelManager;
	}

	public void setService(IDiffinsuranceaMaintain service) {
		this.service = service;
	}

	PersistenceManager sessionManager = null;

}
