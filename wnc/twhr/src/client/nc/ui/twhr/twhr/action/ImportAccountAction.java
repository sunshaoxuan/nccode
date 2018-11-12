package nc.ui.twhr.twhr.action;

/**
 * @(#)ImportPayDataAction.java 1.0 2018��1��29��
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.twhr.DataItfFileReaderAccount;
import nc.itf.twhr.ITwhrMaintain;
import nc.pub.wa.datainterface.DataItfConst;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.twhr.twhr.ace.view.AccountOrgHeadPanel;
import nc.ui.uif2.NCAction;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.twhr.nhicalc.BaoAccountVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author niehg
 * @since 6.3
 */
@SuppressWarnings({ "restriction", "serial" })
public class ImportAccountAction extends HrAction {
	private NCAction batchRefreshAction;

	private AccountOrgHeadPanel orgpanel;

	public AccountOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(AccountOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

	public ImportAccountAction() {
		setCode("importPayData");
		// setBtnName(ResHelper.getString("6013dataitf_01", "dataitf-01-0001"));
		// // �����ڼ䵼��
	}

	@Override
	public void doAction(ActionEvent evt) throws Exception {
		AccoutImportDlg dlg = new AccoutImportDlg(getContext());
		if (1 == dlg.showModal()) {
			putValue(HrAction.MESSAGE_AFTER_ACTION, "");
			final String filePath = dlg.getFilePathPane().getText();
			final Integer dataType = (Integer) dlg.getUiCbxDataType().getSelectdItemValue();
			final LoginContext waContext = (LoginContext) getContext();
			final String waperiod = AccountOrgHeadPanel.getWaperiod();
			new Thread(new Runnable() {
				@Override
				public void run() {
					IProgressMonitor progressMonitor = NCProgresses
							.createDialogProgressMonitor(ImportAccountAction.this.getEntranceUI());

					progressMonitor.beginTask(ResHelper.getString("6013dataitf_01", "dataitf-01-0042"), -1); // ��������...
					progressMonitor.setProcessInfo(ResHelper.getString("6013dataitf_01", "dataitf-01-0043")); // ���ݵ�����,���Ժ�......
					try {
						ImportAccountAction.this.payDataImport(filePath, waperiod, waContext, dataType);
						getBatchRefreshAction().doAction(null);
						MessageDialog.showHintDlg(ImportAccountAction.this.getEntranceUI(), null,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0044")); // ���ݵ���ɹ���
					} catch (Exception e) {
						Logger.error(e);
						MessageDialog.showErrorDlg(ImportAccountAction.this.getEntranceUI(), null, e.getMessage());
					} finally {

						progressMonitor.done();

					}
				}
			}).start();

		} else {
			putValue(HrAction.MESSAGE_AFTER_ACTION, "");
		}
	}

	@Override
	protected void checkDataPermission() throws BusinessException {
		// super.checkDataPermission();
	}

	@Override
	protected boolean isActionEnable() {
		boolean result = super.isActionEnable();
		LoginContext context = (LoginContext) getContext();
		// context.getInitData().
		return true;

	}

	protected void payDataImport(String filePath, String waperiod, LoginContext waContext, Integer dataType)
			throws Exception {
		if (null == dataType) {
			Logger.error("import data type is null!");
			// "������������Ϊ��!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0019"));
		}
		if (StringUtils.isBlank(filePath)) {
			Logger.error("import filepath is bank!");
			// "���������ļ�Ϊ��!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0020"));
		} else if (!DataItfFileReaderAccount.isTxtFile(filePath)) {
			// "ֻ�ܵ���txt���������ļ�!"
			throw new Exception(ResHelper.getString("twhr_laborhealthimport", "import-00001"));
		}
		switch (dataType.intValue()) {
		case DataItfConst.VALUE_SALARY_DETAIL:
			importDataSD(filePath, waperiod, waContext);
			break;

		default:
			Logger.error("import data type is out of type arry combobox!");
			// "������������ͳ�����������б�!"
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0021"));
		}
	}

	protected void importDataSD(String filePath, String waperiod, LoginContext waContext) throws Exception {

		BaoAccountVO[] vos = null;
		int count = 0;

		try {
			String pk_org = this.getOrgpanel().getRefPane().getRefPK();
			if (!StringUtils.isEmpty(pk_org)) {
				vos = DataItfFileReaderAccount.readTxtFileSD(pk_org, waperiod, filePath, waContext);
				if (!ArrayUtils.isEmpty(vos)) {
					NCLocator.getInstance().lookup(ITwhrMaintain.class).insertupdatelabor(vos);
					count += vos.length;
				}
			}
		} catch (Exception e) {
			// "�ɹ�����[{0}]����¼!"
			StringBuffer errormsg = new StringBuffer();

			errormsg.append(e.getMessage());
			throw new Exception(errormsg.toString());
		}
	}

	protected String getFilterCondition(LoginContext waContext) {
		SqlBuilder condition = new SqlBuilder();
		String pk_org = this.getOrgpanel().getRefPane().getRefPK();
		condition.append("pk_group", waContext.getPk_group());
		condition.append("and pk_org", pk_org);

		return condition.toString();
	}

	public void importPayDataSD(BaoAccountVO[] SDVos) throws BusinessException {

		if (ArrayUtils.isEmpty(SDVos)) {
			return;
		}

	}

	public NCAction getBatchRefreshAction() {
		return batchRefreshAction;
	}

	public void setBatchRefreshAction(NCAction batchRefreshAction) {
		this.batchRefreshAction = batchRefreshAction;
	}

}