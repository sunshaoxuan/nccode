package nc.ui.twhr.nhicalc.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.itf.twhr.INhicalcMaintain;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pubapp.uif2app.view.BillListView;
import nc.ui.twhr.nhicalc.ace.view.NhiOrgHeadPanel;
import nc.ui.twhr.nhicalc.model.NhicalcAppModel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.apache.commons.lang.StringUtils;

public class UnAuditAction extends NCAction {
    /**
     * serial version id
     */
    private static final long serialVersionUID = -8157768193436986720L;

    private NhicalcAppModel model = null;
    private BillListView editor = null;

    private NhiOrgHeadPanel orgpanel = null;

    public NhicalcAppModel getModel() {
	return model;
    }

    public void setModel(NhicalcAppModel model) {
	model.addAppEventListener(this);
	this.model = model;
    }

    public BillListView getEditor() {
	return editor;
    }

    public void setEditor(BillListView editor) {
	this.editor = editor;
    }

    public UnAuditAction() {
	setBtnName("取消審核");
    }

    @Override
    public void doAction(ActionEvent arg0) throws Exception {
	if (MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(),
		NCLangRes.getInstance().getStrByID("68861705", "AuditAction-0000")/* 提示 */, NCLangRes.getInstance()
			.getStrByID("68861705", "UnAuditAction-0000")/*
								      * 弃审操作后将无法进行选定期间的薪资计算
								      * ，是否继续？
								      */) == UIDialog.ID_YES) {
	    INhicalcMaintain nhiSrv = NCLocator.getInstance().lookup(INhicalcMaintain.class);
	    nhiSrv.unAudit(getOrgpanel().getRefPane().getRefPK(), this.getOrgpanel().getPeriodRefModel()
		    .getRefNameValue().split("-")[0],
		    this.getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[1]);

	    getOrgpanel().getDataManager().initModelBySqlWhere(
		    "  pk_org='" + getOrgpanel().getRefPane().getRefPK() + "' and cyear='"
			    + this.getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[0]
			    + "' and cperiod='"
			    + this.getOrgpanel().getPeriodRefModel().getRefNameValue().split("-")[1] + "'");

	    // this.model = (BatchBillTableModel) getOrgpanel().getDataManager()
	    // .getModel();
	    this.getModel().setUiState(UIState.NOT_EDIT);
	}
    }

    public boolean isEnabled() {
	INhicalcMaintain nhiSrv = NCLocator.getInstance().lookup(INhicalcMaintain.class);
	boolean isaudit = false;
	try {
	    if (!StringUtils.isEmpty(getOrgpanel().getRefPane().getRefPK())
		    && !StringUtils.isEmpty(this.getOrgpanel().getPeriodRefModel().getRefNameValue())) {
		isaudit = nhiSrv.isAudit(getOrgpanel().getRefPane().getRefPK(), this.getOrgpanel().getPeriodRefModel()
			.getRefNameValue().split("-")[0], this.getOrgpanel().getPeriodRefModel().getRefNameValue()
			.split("-")[1]);
	    }
	} catch (BusinessException e) {
	    ExceptionUtils.wrappBusinessException(e.getMessage());
	}

	return (isaudit) && model.getUiState() == UIState.NOT_EDIT && !model.getData().isEmpty();
    }

    public NhiOrgHeadPanel getOrgpanel() {
	return orgpanel;
    }

    public void setOrgpanel(NhiOrgHeadPanel orgpanel) {
	this.orgpanel = orgpanel;
    }
}
