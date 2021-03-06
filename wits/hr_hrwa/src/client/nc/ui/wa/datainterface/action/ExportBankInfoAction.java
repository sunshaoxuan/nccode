package nc.ui.wa.datainterface.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.wa.datainterface.IReportExportService;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.UIFileChooser;
import nc.ui.wa.datainterface.TextFileFilter4TW;
import nc.vo.pub.BusinessException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class ExportBankInfoAction extends HrAction {
	/**
	 * serial no
	 */
	private static final long serialVersionUID = 5099164079859001851L;

	public ExportBankInfoAction() {
		super();
		super.setBtnName("匯出銀行媒體檔");
		super.setCode("ExportBankInfoAction");
		super.putValue(Action.SHORT_DESCRIPTION, "匯出銀行媒體檔");
	}

	private IReportExportService service = null;
	private String year = "";
	private String period = "";
	private String pk_wa_class = "";
	private String pk_org = "";

	private IReportExportService getService() {
		if (service == null) {
			service = (IReportExportService) NCLocator.getInstance().lookup(
					IReportExportService.class);
		}
		return service;
	}

	public String getYear() {
		return ((nc.ui.wa.datainterface.model.DataIOAppModel) this.getModel())
				.getWaLoginContext().getWaYear();
	}

	public String getPeriod() {
		return ((nc.ui.wa.datainterface.model.DataIOAppModel) this.getModel())
				.getWaLoginContext().getWaPeriod();
	}

	public String getPk_wa_class() {
		if (StringUtils.isEmpty(pk_wa_class)) {
			pk_wa_class = ((nc.ui.wa.datainterface.model.DataIOAppModel) this
					.getModel()).getWaLoginContext().getPk_wa_class();
		}
		return pk_wa_class;
	}

	public String getPk_org() {
		if (StringUtils.isEmpty(pk_org)) {
			pk_org = getModel().getContext().getPk_org();
		}
		return pk_org;
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		JComponent parentUi = getModel().getContext().getEntranceUI();

		String[] textArr = getService().getBankReportText(getPk_org(),
				getYear() + getPeriod(), getPk_wa_class());
        // 銀行媒體檔產出問題  20190801 George 缺陷Bug #29229 
		// 當產出人員只有一筆時,無法產出媒體檔
		if (textArr != null && textArr.length >= 2) {
			UIFileChooser fileChooser = new UIFileChooser();
			fileChooser.setDialogTitle("請指定要匯出的文檔名稱");
			TextFileFilter4TW filter = new TextFileFilter4TW();
			filter.setFilterString("*.TXT");
			filter.setDescription("匯出銀行媒體檔");
			fileChooser.addChoosableFileFilter(filter);
			fileChooser.setSelectedFile(new File(fileChooser
					.getCurrentDirectory().getAbsolutePath()
					+ "\\BANK_RPT_"
					+ getYear() + getPeriod() + ".TXT"));
			int userSelection = fileChooser.showSaveDialog(parentUi);

			String filename = "";
			File fileToSave = null;
			if (userSelection == JFileChooser.APPROVE_OPTION) {
				if (!fileChooser.getSelectedFile().getAbsoluteFile().toString()
						.toUpperCase().endsWith(".TXT")) {
					filename = fileChooser.getSelectedFile().getAbsolutePath()
							+ ".TXT";
				} else {
					filename = fileChooser.getSelectedFile().getAbsolutePath();
				}
				fileToSave = new File(filename);
			} else {
				this.putValue("message_after_action", "匯出已取消");
				return;
			}

			StringBuilder sb = new StringBuilder();
			for (String txt : textArr) {
				sb.append(txt + "\r\n");
			}

			if (fileToSave != null) {
				// 讀取報表文本
				FileUtils.writeStringToFile(fileToSave, sb.toString(), "Big5");
			}

			this.putValue("message_after_action", "");
		} else {
			this.putValue("message_after_action", "選定的期間內沒找到可匯出的資料。");
		}

	}

	@Override
	protected boolean isActionEnable() {
		try {
			if (getService().checkPeriodWaDataExists(
					new String[] { this.getPk_wa_class() },
					this.getYear() + this.getPeriod(),
					this.getYear() + this.getPeriod()) == 0) {
				return true;
			}
		} catch (BusinessException e) {
			Logger.error("检查期间数据时发生错误：" + e.getMessage());
		}

		return false;
	}
}
