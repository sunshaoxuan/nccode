package nc.ui.hrwa.incometax.view;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.cmp.contract.component.CMPLabel;
import nc.ui.cmp.contract.component.LabbedCom;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.beans.progress.NCProgresses;
import nc.ui.wa.ref.WaPeriodRefTreeModel;
import nc.ui.wa.util.LocalizationSysinitUtil;
import nc.vo.pub.BusinessException;

/**
 * 
 * @author ward.wong
 * @date 20180126
 * @version v1.0
 * @date 20180227 添加關於福委和申報憑單格式的過濾條件
 * @功能描述 申报明细档单据生成参数设置界面
 * 
 */
public class AddIncomeTaxView extends UIDialog implements ActionListener,
		ValueChangedListener, ItemListener {

	public AddIncomeTaxView() {
		super();
		initialize();
	}

	public AddIncomeTaxView(Container arg1, String arg2) {
		super(arg1, arg2);
	}

	public AddIncomeTaxView(Frame arg1) {
		super(arg1);
	}

	public AddIncomeTaxView(Frame arg1, String arg2) {
		super(arg1, arg2);
	}

	public AddIncomeTaxView(Container arg1) {
		super(arg1);
		initialize();
	}

	private void initialize() {
		try {
			setName("AddIncomeTaxView");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(LabbedCom.MaxSize.width * 4, 350);
			setContentPane(getUIDialogContentPane());
		} catch (Throwable ivjExc) {
			handleException(ivjExc);
		}

	}

	private JPanel getUIDialogContentPane() {
		if (ivjUIDialogContentPane == null) {
			try {
				ivjUIDialogContentPane = new javax.swing.JPanel();
				ivjUIDialogContentPane.setName("UIDialogContentPane");
				ivjUIDialogContentPane.setLayout(new java.awt.BorderLayout());
				getUIDialogContentPane().add(getUIPanel1());//, "Center"
				//getUIDialogContentPane().add(getUIPanel2(), "South");
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIDialogContentPane;
	}

	private UIPanel getUIPanel1() {
		if (ivjUIPanel1 == null) {
			try {
				ivjUIPanel1 = new UIPanel();
				ivjUIPanel1.setName("UIPanel1");
				ivjUIPanel1.setLayout(null);
				// 是否外籍员工逐月申报
				ivjUIPanel1.add(getisForeignMonthDec());
				ivjUIPanel1.add(getisPtCost());
				// 统一编号
				ivjUIPanel1.add(getUnifiednumberLabel());
				ivjUIPanel1.add(getUnifiednumberRefPane());
				// 申报格式
				ivjUIPanel1.add(getDeclaretypeLabel());
				ivjUIPanel1.add(getDeclaretypeRefPane());
				// 薪资方案
				ivjUIPanel1.add(getWaclassLabel());
				ivjUIPanel1.add(getWaclassRefPane());
				// 薪资年度
				ivjUIPanel1.add(getWayearLabel());
				ivjUIPanel1.add(getWayearRefPane());
				// 开始时间
				ivjUIPanel1.add(getWabeginmonthLabel());
				ivjUIPanel1.add(getWabeginmonthRefPane());
				// 结束时间
				ivjUIPanel1.add(getWaendmonthLabel());
				ivjUIPanel1.add(getWaendmonthRefPane());

				ivjUIPanel1.add(getBtnOK(), getBtnOK().getName());
				ivjUIPanel1.add(getBtnCancel(), getBtnCancel().getName());
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIPanel1;
	}

	private nc.ui.pub.beans.UIPanel getUIPanel2() {
		if (ivjUIPanel2 == null) {
			try {
				ivjUIPanel2 = new nc.ui.pub.beans.UIPanel();
				ivjUIPanel2.setName("UIPanel2");
				// ivjUIPanel2.setBackground(new java.awt.Color(204, 204, 204));
				getUIPanel2().add(getBtnOK(), getBtnOK().getName());
				getUIPanel2().add(getBtnCancel(), getBtnCancel().getName());
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIPanel2;
	}

	private JCheckBox getisForeignMonthDec() {
		if (isForeignMonthDec == null) {
			isForeignMonthDec = new JCheckBox(ResHelper.getString("incometax",
					"2incometax-n-000013")/* "是否外籍员工逐月申报" */);
			isForeignMonthDec.setBounds(230, 10, 200, 30);
			isForeignMonthDec.addItemListener(this);
		}
		return isForeignMonthDec;
	}
	
	private JCheckBox getisPtCost() {
		if (isPtCost == null) {
			isPtCost = new JCheckBox(ResHelper.getString("incometax",
					"2incometax-n-000052")/* "是否外籍员工逐月申报" */);
			isPtCost.setBounds(150, 280, 200, 30);
			isPtCost.addItemListener(this);
		}
		return isPtCost;
	}

	private CMPLabel getUnifiednumberLabel() {
		if (unifiednumberLabel == null) {
			try {
				unifiednumberLabel = new CMPLabel();
				unifiednumberLabel.setName("unifiednumberLabel");
				unifiednumberLabel.setText(ResHelper.getString("incometax",
						"2incometax-n-000014")/* "统一编号" */);
				unifiednumberLabel.setBounds(0, 50, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return unifiednumberLabel;
	}

	private CMPLabel getDeclaretypeLabel() {
		if (declaretypeLabel == null) {
			try {
				declaretypeLabel = new CMPLabel();
				declaretypeLabel.setName("declaretypeLabel");
				declaretypeLabel.setText(ResHelper.getString("incometax",
						"2incometax-n-000015")/* "申报凭单格式" */);
				declaretypeLabel.setBounds(0, 90, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return declaretypeLabel;
	}

	private CMPLabel getWaclassLabel() {
		if (waclassLabel == null) {
			try {
				waclassLabel = new CMPLabel();
				waclassLabel.setName("waclassLabel");
				waclassLabel.setText(ResHelper.getString("incometax",
						"2incometax-n-000016")/* "薪资方案" */);
				waclassLabel.setBounds(0, 130, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return waclassLabel;
	}

	private CMPLabel getWayearLabel() {
		if (wayearLabel == null) {
			try {
				wayearLabel = new CMPLabel();
				wayearLabel.setName("wayearLabel");
				wayearLabel.setText(ResHelper.getString("incometax",
						"2incometax-n-000017")/* "薪资年度" */);
				wayearLabel.setBounds(0, 170, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return wayearLabel;
	}

	private CMPLabel getWabeginmonthLabel() {
		if (wabeginmonthLabel == null) {
			try {
				wabeginmonthLabel = new CMPLabel();
				wabeginmonthLabel.setName("wabeginmonthLabel");
				wabeginmonthLabel.setText(ResHelper.getString("incometax",
						"2incometax-n-000018")/* "开始时间" */);
				wabeginmonthLabel.setBounds(0, 210, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return wabeginmonthLabel;
	}

	private CMPLabel getWaendmonthLabel() {
		if (waendmonthLabel == null) {
			try {
				waendmonthLabel = new CMPLabel();
				waendmonthLabel.setName("waendmonthLabel");
				waendmonthLabel.setText(ResHelper.getString("incometax",
						"2incometax-n-000019")/* "结束时间" */);
				waendmonthLabel.setBounds(0, 250, 100, 30);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return waendmonthLabel;
	}

	/**
	 * 统一编号
	 * 
	 * @return
	 */
	private UIRefPane getUnifiednumberRefPane() {
		if (unifiednumberRefPane == null) {
			try {
				unifiednumberRefPane = new UIRefPane();
				unifiednumberRefPane.setName("unifiednumberRefPane");
				unifiednumberRefPane.setRefNodeName("統一編號(自定义档案)");
				unifiednumberRefPane.setBounds(160, 50, 150, 50);
				unifiednumberRefPane.addValueChangedListener(this);
				unifiednumberRefPane.setButtonFireEvent(true);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return unifiednumberRefPane;
	}

	/**
	 * 申报格式
	 * 
	 * @return
	 */
	private UIRefPane getDeclaretypeRefPane() {
		if (declaretypeRefPane == null) {
			try {
				declaretypeRefPane = new UIRefPane();
				declaretypeRefPane.setName("declaretypeRefPane");
				declaretypeRefPane.setRefNodeName("申報格式(自定义档案)");
				declaretypeRefPane.addValueChangedListener(this);
				declaretypeRefPane.setBounds(160, 90, 150, 50);
				declaretypeRefPane.setMultiSelectedEnabled(true);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return declaretypeRefPane;
	}

	/**
	 * 薪资方案
	 * 
	 * @return
	 */
	private UIRefPane getWaclassRefPane() {
		if (this.waClassRefPane == null) {
			this.waClassRefPane = new UIRefPane();
			this.waClassRefPane.setVisible(this.isShow);
			waClassRefPane.setBounds(160, 130, 150, 50);
			waClassRefPane.addValueChangedListener(this);
			waClassRefPane.setButtonFireEvent(true);
			WaClassRefModel refmodel = getClassRefModel();
			this.waClassRefPane.setRefModel(refmodel);
			this.waClassRefPane.setMultiSelectedEnabled(true);
		}
		return this.waClassRefPane;
	}

	public WaClassRefModel getClassRefModel() {
		if (this.classRefModel == null) {
			this.classRefModel = new nc.ui.hrwa.incometax.view.WaClassRefModel();
		}

		return this.classRefModel;
	}

	/**
	 * 薪资年度
	 * 
	 * @return
	 */
	private UIRefPane getWayearRefPane() {
		if (this.wayearRefPane == null) {
			try {
				wayearRefPane = new UIRefPane();
				wayearRefPane.setName("wayearRefPane");
				wayearRefPane.setRefNodeName("年");
				wayearRefPane.setBounds(160, 170, 150, 50);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}

		}
		return this.wayearRefPane;
	}

	/**
	 * 开始时间
	 * 
	 * @return
	 */
	private UIRefPane getWabeginmonthRefPane() {
		if (this.wabeginmonthRefPane == null) {
			try {
				wabeginmonthRefPane = new UIRefPane();
				wabeginmonthRefPane.setName("wabeginmonthRefPane");
				wabeginmonthRefPane.setRefNodeName("会计期间档案");
				wabeginmonthRefPane.setBounds(160, 210, 150, 50);
				wabeginmonthRefPane.addValueChangedListener(this);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}

		}
		return this.wabeginmonthRefPane;
	}

	/**
	 * 结束时间
	 * 
	 * @return
	 */
	private UIRefPane getWaendmonthRefPane() {
		if (this.waendmonthRefPane == null) {
			try {
				waendmonthRefPane = new UIRefPane();
				waendmonthRefPane.setName("waendmonthRefPane");
				waendmonthRefPane.setRefNodeName("会计期间档案");
				waendmonthRefPane.setBounds(160, 250, 150, 50);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}

		}
		return this.waendmonthRefPane;
	}

	/**
	 * 确定操作
	 * 
	 * @throws Exception
	 */
	private void onButtonOKClicked() {
		this.isForeignMonth = getisForeignMonthDec().isSelected();
		this.isPtCostSelect = getisPtCost().isSelected();
		this.unifiednumber = getUnifiednumberRefPane().getRefPK();
		if (null == this.unifiednumber || "".equals(this.unifiednumber)) {
			MessageDialog
					.showWarningDlg(this, ResHelper.getString("incometax",
							"2incometax-n-000004"), ResHelper.getString(
							"incometax", "2incometax-n-000020") /* "统一编号不能为空" */);
			return;
		}
		this.declaretype = getDeclaretypeRefPane().getRefPKs();
		if (null == this.declaretype || "".equals(this.declaretype)) {
			MessageDialog
					.showWarningDlg(this, ResHelper.getString("incometax",
							"2incometax-n-000004"), ResHelper.getString(
							"incometax", "2incometax-n-000021") /* "申报凭单格式不能为空" */);
			return;
		}
		this.waclass = getWaclassRefPane().getRefPKs();
		if(!isPtCostSelect){
			if (null == this.waclass || this.waclass.length < 1) {
				MessageDialog
						.showWarningDlg(this, ResHelper.getString("incometax",
								"2incometax-n-000004"), ResHelper.getString(
								"incometax", "2incometax-n-000022")/* "薪资方案不能为空" */);
				return;
			}
		}
		
		this.year = getWayearRefPane().getRefPK();
		if (null == this.year || "".equals(this.year)) {
			MessageDialog
					.showWarningDlg(this, ResHelper.getString("incometax",
							"2incometax-n-000004"), ResHelper.getString(
							"incometax", "2incometax-n-000023")/* "薪资年度不能为空" */);
			return;
		}
		this.beginMonth = getWabeginmonthRefPane().getRefCode();
		if (null == this.beginMonth || "".equals(this.beginMonth)) {
			MessageDialog
					.showWarningDlg(this, ResHelper.getString("incometax",
							"2incometax-n-000004"), ResHelper.getString(
							"incometax", "2incometax-n-000024")/* "起始时间不能为空" */);
			return;
		}
		this.endMonth = getWaendmonthRefPane().getRefCode();
		if (this.isForeignMonth) {
			this.endMonth = this.beginMonth;
		} else if (null == this.endMonth || "".equals(this.endMonth)) {
			MessageDialog
					.showWarningDlg(this, ResHelper.getString("incometax",
							"2incometax-n-000004"), ResHelper.getString(
							"incometax", "2incometax-n-000025")/* "结束时间不能为空" */);
			return;
		} else if (Integer.valueOf(this.beginMonth.replace("-", "")) > Integer
				.valueOf(this.endMonth.replace("-", ""))) {
			MessageDialog
					.showWarningDlg(this, ResHelper.getString("incometax",
							"2incometax-n-000004"), ResHelper.getString(
							"incometax", "2incometax-n-000026") /* "开始时间不能小于结束时间" */);
			return;
		}
		this.closeOK();

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == this.getBtnOK()) {
			new Thread() {
				@Override
				public void run() {

					IProgressMonitor progressMonitor = NCProgresses
							.createDialogProgressMonitor(getParent());

					try {
						progressMonitor.beginTask("匯出中...",
								IProgressMonitor.UNKNOWN_REMAIN_TIME);
						progressMonitor.setProcessInfo("匯出中，請稍候.....");
						onButtonOKClicked();
					} finally {

						progressMonitor.done(); // 进度任务结束
					}
				}
			}.start();
		}
		if (e.getSource() == this.getBtnCancel()) {
			this.closeCancel();
		}

	}

	public void valueChanged(ValueChangedEvent e) {
		if (e.getSource() == getUnifiednumberRefPane()) {
			// 根据统一编号获取该编号下对应的组织，并查詢該統一
			String unifiednumber = getUnifiednumberRefPane().getRefPK();
			if (null != unifiednumber && !"".equals(unifiednumber)) {
				String qrySql = "select pk_hrorg from org_hrorg where "
						+ LocalizationSysinitUtil.getTwhrlOrg("TWHRLORG03")
						+ " = '" + unifiednumber + "' or "
						+ LocalizationSysinitUtil.getTwhrlOrg("TWHRLORG15")
						+ " = '" + unifiednumber + "'";
				String qryPid = "select pid from bd_defdoc where pk_defdoc='"
						+ unifiednumber + "'";
				try {
					@SuppressWarnings("unchecked")
					List<String> orgs = (ArrayList<String>) LocalizationSysinitUtil
							.getUAPQueryBS().executeQuery(qrySql,
									new ColumnListProcessor());
					((WaClassRefModel) getWaclassRefPane().getRefModel())
							.setPk_orgs(orgs.toArray(new String[0]));
					Object pid = LocalizationSysinitUtil.getUAPQueryBS()
							.executeQuery(qryPid, new ColumnProcessor());
					if ("1001ZZ1000000001PX6Q".equals(String.valueOf(pid))) {// 普通
						((WaClassRefModel) getWaclassRefPane().getRefModel())
								.setIsferry("N");
					} else if ("1001ZZ1000000001PX6S".equals(String
							.valueOf(pid))) {// 福委
						((WaClassRefModel) getWaclassRefPane().getRefModel())
								.setIsferry("Y");
					}
				} catch (BusinessException e1) {
					handleException(e1);
				}
				getWaclassRefPane().setPK(null);
			}
		}
		if (e.getSource() == getDeclaretypeRefPane()) {
			((WaClassRefModel) getWaclassRefPane().getRefModel())
					.setDeclareform(getDeclaretypeRefPane().getRefPKs());
		}

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// 获取改变的复选按键
		Object source = e.getItemSelectable();
		if (source == getisForeignMonthDec()) {
			// 当勾选是否外籍员工逐月申报时，薪资方案单选；结束时间不允许编辑
			if (getisForeignMonthDec().isSelected()) {
				getWaclassRefPane().setMultiSelectedEnabled(false);
				getWaendmonthRefPane().setEnabled(false);
				// {MOD:个税申报}
				getisPtCost().setEnabled(false);
				// end
			} else {
				getWaclassRefPane().setMultiSelectedEnabled(true);
				getWaendmonthRefPane().setEnabled(true);
				// {MOD:个税申报}
				getisPtCost().setEnabled(true);
				// end
			}
			getWaclassRefPane().setPK(null);
			getWaendmonthRefPane().setPK(null);
		}
	}

	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	protected void finalize() throws Throwable {
		super.finalize();
	}

	protected LayoutManager getLayoutManager() {
		return new GridLayout(2, 2);
	}

	/**
	 * 生成接收器的散列码。 此方法主要由散列表 支持，如 java.util 中提供的那些散列表。@return 接收器的整数散列码
	 */
	public int hashCode() {
		return super.hashCode();
	}

	private UIButton getBtnOK() {
		if (btnOK == null) {
			try {
				btnOK = new UIButton();
				btnOK.setName("btnOK");
				btnOK.setText(ResHelper.getString("incometax",
						"2incometax-n-000027")/* "确定(Y)" */);
				btnOK.addActionListener(this);
				btnOK.registerKeyboardAction(this, KeyStroke.getKeyStroke(
						KeyEvent.VK_Y, InputEvent.ALT_MASK),
						JComponent.WHEN_IN_FOCUSED_WINDOW);
				btnOK.setBounds(135, 310, 55, 25);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnOK;
	}

	private UIButton getBtnCancel() {
		if (btnCancel == null) {
			try {
				btnCancel = new UIButton();
				btnCancel.setName("btnCancel");
				btnCancel.setText(ResHelper.getString("incometax",
						"2incometax-n-000028")/* "取消(C)" */);
				btnCancel.addActionListener(this);
				btnCancel.registerKeyboardAction(this, KeyStroke.getKeyStroke(
						KeyEvent.VK_C, InputEvent.ALT_MASK),
						JComponent.WHEN_IN_FOCUSED_WINDOW);
				btnCancel.setBounds(210, 310, 55, 25);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnCancel;
	}

	private void handleException(Throwable exception) {
		Logger.error("--------- 未捕捉到的异常 ---------");
		MessageDialog.showErrorDlg(this, null, exception.getMessage());
		exception.printStackTrace();
	}

	private UIButton btnOK = null;// 确定按钮
	private UIButton btnCancel = null;// 取消按钮
	private JPanel ivjUIDialogContentPane = null;
	private UIPanel ivjUIPanel1 = null;
	private UIPanel ivjUIPanel2 = null;
	private JCheckBox isForeignMonthDec;// 是否外籍员工逐月申报
	private JCheckBox isPtCost;// 是否生成勞務費用頁籤資料
	private CMPLabel unifiednumberLabel;// 统一编号
	private CMPLabel declaretypeLabel = null;// 申报凭单格式
	private CMPLabel waclassLabel = null;// 薪资方案
	private CMPLabel wayearLabel = null;// 薪资年度
	private CMPLabel wabeginmonthLabel = null;// 开始时间
	private CMPLabel waendmonthLabel = null;// 截止时间

	private UIRefPane unifiednumberRefPane = null;// 统一编号
	private UIRefPane declaretypeRefPane = null;// 申报凭单格式
	private UIRefPane waClassRefPane = null;// 薪资方案
	private UIRefPane wayearRefPane = null;// 薪资年度
	private UIRefPane wabeginmonthRefPane = null;// 开始时间
	private UIRefPane waendmonthRefPane = null;// 结束时间
	private WaClassRefModel classRefModel = null;
	private WaPeriodRefTreeModel periodRefModel = null;
	protected boolean isShow = true;

	private boolean isForeignMonth = false;
	private boolean isPtCostSelect = false;
	private String unifiednumber;
	private String[] declaretype;
	private String[] waclass;
	private String year;
	private String beginMonth;
	private String endMonth;

	public boolean isForeignMonth() {
		return isForeignMonth;
	}

	public void setForeignMonth(boolean isForeignMonth) {
		this.isForeignMonth = isForeignMonth;
	}

	public boolean isPtCostSelect() {
		return isPtCostSelect;
	}

	public void setPtCostSelect(boolean isPtCostSelect) {
		this.isPtCostSelect = isPtCostSelect;
	}

	public String getUnifiednumber() {
		return unifiednumber;
	}

	public void setUnifiednumber(String unifiednumber) {
		this.unifiednumber = unifiednumber;
	}

	public String[] getDeclaretype() {
		return declaretype;
	}

	public void setDeclaretype(String[] declaretype) {
		this.declaretype = declaretype;
	}

	public String[] getWaclass() {
		return waclass;
	}

	public void setWaclass(String[] waclass) {
		this.waclass = waclass;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getBeginMonth() {
		return beginMonth;
	}

	public void setBeginMonth(String beginMonth) {
		this.beginMonth = beginMonth;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

}
