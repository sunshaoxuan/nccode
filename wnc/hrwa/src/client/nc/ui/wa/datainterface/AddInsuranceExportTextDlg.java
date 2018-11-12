package nc.ui.wa.datainterface;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import nc.bs.dao.DAOException;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;

@SuppressWarnings("unused")
public class AddInsuranceExportTextDlg extends UIDialog implements
		nc.ui.pub.bill.BillEditListener2, ActionListener {
	private BillCardPanel billListHeadPanel = null;
	private BillCardPanel billListBodyPanel = null;
	private static final long serialVersionUID = 1L;
	private UIPanel bnPanel = null;
	private UIButton okButton = null;
	private UIButton cancelButton = null;
	private Checkbox checkbox = null;
	private String dateTime = ""; // ����
	private String legalOrg = "";// ������֯
	private String hrOrg = "";// ������Դ��֯

	UIRefPane changeDateRefPane;// ������ڲ���

	public AddInsuranceExportTextDlg() throws DAOException {
		initialize();
	}

	// public AddInsuranceExportTextDlg(List<ReceiptsAndPaymentsDetail>
	// detailList) throws BusinessException {
	// initialize();
	// ReceiptsAndPaymentsDetail[] array= new
	// ReceiptsAndPaymentsDetail[detailList.size()];
	// BenefitsattsHVO[] array = new BenefitsattsHVO[list.size()];
	// for (int i = 0; i < list.size(); i++)
	// list.get(i).setVdef10("����");
	// getBillListBodyPanel().setBodyValueVO(detailList.toArray(array));
	// // getBillListBodyPanel().getBodyItem("download").setValue("����");
	// DecisionSuppItemSpitHyperlinkListener codeListener = new
	// DecisionSuppItemSpitHyperlinkListener(
	// array);
	// getBillListBodyPanel().getBodyItem("vdef10")
	// .addBillItemHyperlinkListener(codeListener);
	// getBillListBodyPanel().getBodyBillModel().execLoadFormula();
	// }

	private void initialize() {
		this.setTitle("Ո�x������");
		this.setLayout(new BorderLayout());
		this.setSize(new Dimension(300, 270));
		// this.add(getBillListBodyPanel(), BorderLayout.CENTER);
		this.add(getBillListHeadPanel(), BorderLayout.CENTER);
		this.add(getBnPanel(), BorderLayout.SOUTH);
	}

	private UIPanel getBnPanel() {
		if (this.bnPanel == null) {
			this.bnPanel = new UIPanel();
			this.bnPanel.setLayout(null);
			this.bnPanel.setPreferredSize(new Dimension(200, 30));
			this.bnPanel.add(getOkButton(), null);
			this.bnPanel.add(getCancelButton(), null);
		}
		return this.bnPanel;
	}

	private UIButton getOkButton() {
		if (this.okButton == null) {
			this.okButton = new UIButton();
			this.okButton.setBounds(new Rectangle(50, 5, 30, 20));
			this.okButton.setText(NCLangRes.getInstance().getStrByID("common",
					"UC001-0000044"));
			this.okButton.setPreferredSize(new Dimension(30, 22));
			this.okButton.addActionListener(this);
		}
		return this.okButton;
	}

	private UIButton getCancelButton() {
		if (this.cancelButton == null) {
			this.cancelButton = new UIButton();
			this.cancelButton.setBounds(new Rectangle(200, 5, 30, 20));
			this.cancelButton.setText(NCLangRes.getInstance().getStrByID(
					"common", "UC001-0000008"));
			this.cancelButton.setPreferredSize(new Dimension(60, 22));
			this.cancelButton.addActionListener(this);
		}
		return this.cancelButton;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(getOkButton())) {
			setResult(UIDialog.ID_OK);
			dateTime = getBillListHeadPanel().getHeadItem("dateTime")
					.getValue();
			legalOrg = getBillListHeadPanel().getHeadItem("legalOrg")
					.getValue();
			hrOrg = getBillListHeadPanel().getHeadItem("hrOrg").getValue();
			dispose();
		} else if (e.getSource().equals(getCancelButton())) {
			setResult(UIDialog.ID_CANCEL);
			dispose();
		}
	}

	/*
	 * ����ѡ����տ���ݣ����Ѿ���д�����ݽ���У�顣
	 */
	private String checkListData(String redate, String backdate) {

		return null;
	}

	private String checkCardData(String redate, String backdate) {
		String message = "";
		return message;
	}

	/*
	 * ��ȡģ��
	 */
	private BillCardPanel getBillListHeadPanel() {
		if (billListHeadPanel == null) {
			billListHeadPanel = new BillCardPanel();
			billListHeadPanel.loadTemplet("1001AA1000000001NOX8");
			billListHeadPanel.setVisible(true);
			billListHeadPanel.setEnabled(true);
			this.billListHeadPanel.addBodyEditListener2(this);
		}
		return billListHeadPanel;
	}

	/*
	 * ��ȡģ��
	 */
	private BillCardPanel getBillListBodyPanel() {
		if (billListBodyPanel == null) {
			billListBodyPanel = new BillCardPanel();
			billListBodyPanel.loadTemplet("1001AA1000000001NOX8");
			billListBodyPanel.setVisible(true);
			billListBodyPanel.setEnabled(true);
			this.billListBodyPanel.addBodyEditListener2(this);
		}
		return billListBodyPanel;
	}

	@Override
	public boolean beforeEdit(BillEditEvent paramBillEditEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getLegalOrg() {
		return legalOrg;
	}

	public void setLegalOrg(String legalOrg) {
		this.legalOrg = legalOrg;
	}

	public String getHrOrg() {
		return hrOrg;
	}

	public void setHrOrg(String hrOrg) {
		this.hrOrg = hrOrg;
	}

}