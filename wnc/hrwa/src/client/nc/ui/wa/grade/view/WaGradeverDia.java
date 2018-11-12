package nc.ui.wa.grade.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellEditor;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.ValidationFailure;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IWaGradeService;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.hr.util.TableColResize;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UISplitPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.wa.grade.model.WaGradeBillModel;
import nc.ui.wa.grade.validator.WaGradeCrtRangeValidator;
import nc.ui.wa.grade.validator.WaGradeCrtValueValidator;
import nc.ui.wa.salaryadjmgt.WASalaryadjmgtDelegator;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.grade.AggWaGradeVO;
import nc.vo.wa.grade.CrtVO;
import nc.vo.wa.grade.IWaGradeCommonDef;
import nc.vo.wa.grade.IWaGradeCommonDef.GradeSetType;
import nc.vo.wa.grade.IWaGradeCommonDef.OprationFlag;
import nc.vo.wa.grade.WaCriterionVO;
import nc.vo.wa.grade.WaGradeVO;
import nc.vo.wa.grade.WaGradeVerVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 薪资标准版本Panel
 * 
 * @author xuhw
 */
public class WaGradeverDia extends HrDialog implements BillEditListener {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -69933968263936095L;
	private UISplitPane splitPanel = null;
	private nc.ui.pub.bill.BillScrollPane ivjmainUpbsp = null;
	private WaGradeForm parentForm = null;
	private nc.ui.pub.bill.BillScrollPane ivjmainbsp = null;
	private WaGradeVerVO[] gradeVerVOs = null;
	private WaGradeBillModel billModel = null;

	protected UIButton btnAddVer = null;
	protected UIButton btnEditVer = null;
	protected UIButton btnDeleteVer = null;
	protected UIButton btnSaveVer = null;
	protected UIButton btnCancelVer = null;
	protected String btnAddStr = null;
	private final String ADDSTR = "Add";

	private UIPanel topButtonPanel;
	private OprationFlag oprationFlag = OprationFlag.No_Opration;

	public WaGradeverDia(WaGradeForm parentForm) {
		super(parentForm, ResHelper
				.getString("60130paystd", "060130paystd0192")/* @res "薪资标准版本维护" */);
		this.parentForm = parentForm;
		this.billModel = parentForm.getGradeModel();
		initSomething();
		setLayout(new BorderLayout());
		add(getTopButtonPanel(), BorderLayout.NORTH);
		add(getSplitPane(), BorderLayout.CENTER);
		setSize(450, 350);
	}

	private void initSomething() {
		getmainbsp().setTableModel(
				parentForm.getBillCardPanel().getBillModel(
						IWaGradeCommonDef.WA_CRT));
		initMainUpTable();
		initMainUpTableData();
		if (parentForm.getGradeSetType() == GradeSetType.SingleSeclvRange) {
			WaGradeColumnGroupTool groupTool = new WaGradeColumnGroupTool(
					getmainbsp().getTable(), getmainbsp().getTableModel());
			groupTool.initColumnGroup(
					ResHelper.getString("60130paystd", "060130paystd0187")/*
																		 * @res
																		 * "一档"
																		 */,
					"pran_down_criterionvalue", "pran_basic_criterionvalue",
					"pran_up_criterionvalue");
		} else if (parentForm.getGradeSetType() == GradeSetType.MultSeclvRange
				&& null != parentForm.getSeclvVos()) {
			WaGradeColumnGroupTool groupTool = new WaGradeColumnGroupTool(
					getmainbsp().getTable(), getmainbsp().getTableModel());
			for (int i = 0; i < parentForm.getSeclvVos().length; i++) {
				groupTool.initColumnGroup(
						parentForm.getSeclvVos()[i].getLevelname(),
						"precran_down_"
								+ parentForm.getSeclvVos()[i].getPk_wa_seclv(),
						"precran_basic_"
								+ parentForm.getSeclvVos()[i].getPk_wa_seclv(),
						"precran_up_"
								+ parentForm.getSeclvVos()[i].getPk_wa_seclv());
			}
		}

		initVerButton();
	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			onBtnCloseClick();

		}// processActionEvent(new ActionEvent(this,
			// ActionEvent.ACTION_PERFORMED, CANCEL_COMMAND));
		else if (e.getID() == WindowEvent.WINDOW_OPENED) {
			// simulate tab from last to wrap around to first & properly set
			// initial focus
			if (getComponentCount() > 0)
				getComponent(getComponentCount() - 1).transferFocus();
		}
		super.processWindowEvent(e);
	}

	private BillScrollPane getmainUpbsp() {
		if (ivjmainUpbsp == null) {
			ivjmainUpbsp = new nc.ui.pub.bill.BillScrollPane();
			ivjmainUpbsp.setName("mainupbsp");
			ivjmainUpbsp.setBounds(230, 400, 564, 200);
			ivjmainUpbsp.setTableName("11111111");
			ivjmainUpbsp.setAutoAddLine(false);
		}
		return ivjmainUpbsp;
	}

	private void initMainUpTable() {
		String[] colName = new String[] {
				ResHelper.getString("common", "UC000-0002905")/* @res "版本号" */,
				ResHelper.getString("60130paystd", "160130paystd0014")/*
																	 * @res
																	 * "版本名称"
																	 */,
				ResHelper.getString("60130paystd", "060130paystd0193")/*
																	 * @res
																	 * "版本建立日期"
																	 */,
				ResHelper.getString("60130paystd", "060130paystd0194")/*
																	 * @res
																	 * "生效标记"
																	 */,
				"pk_wa_grd", "pk_wa_gradever" };
		String[] colKey = new String[] { WaGradeVerVO.GRADEVER_NUM,
				WaGradeVerVO.GRADEVER_NAME, WaGradeVerVO.VER_CREATE_DATE,
				WaGradeVerVO.EFFECT_FLAG, WaGradeVerVO.PK_WA_GRD,
				WaGradeVerVO.PK_WA_GRADEVER };
		BillItem[] abillBody = new BillItem[colName.length];
		for (int i = 0; i < colName.length; i++) {
			abillBody[i] = new BillItem();
			if (i == 3) {
				abillBody[i].setDataType(BillItem.BOOLEAN);
			} else {
				abillBody[i].setDataType(BillItem.STRING);
			}
			abillBody[i].setName(colName[i]);
			abillBody[i].setKey(colKey[i]);
			abillBody[i].setWidth(60);
			abillBody[i].setEnabled(true);
			abillBody[i].setEdit(true);

			if (i == 0) {
				abillBody[i].setDataType(BillItem.DECIMAL);
				abillBody[i].setDecimalDigits(1);
				abillBody[i].setEnabled(false);
				abillBody[i].setEdit(false);
				abillBody[i].setNull(true);
			}
			if (i == 1) {
				abillBody[i].setLength(500);
				abillBody[i].setNull(true);
			}
			if (i == 2) {
				abillBody[i].setDataType(BillItem.DATE);
				abillBody[i].setWidth(120);
				abillBody[i].setNull(true);
			}

			if (i == 3) {
				abillBody[i].setDataType(BillItem.BOOLEAN);
				abillBody[i].setWidth(80);
				abillBody[i].setNull(false);
				abillBody[i].setComponent(new UICheckBox());
				((UICheckBox) abillBody[i].getComponent())
						.setHorizontalAlignment(UICheckBox.CENTER);
			}

			if (i > 3) {
				abillBody[i].setShow(false);
				abillBody[i].setNull(false);
			}

		}
		BillModel billModel = new BillModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int col) {
				if (oprationFlag == OprationFlag.No_Opration) {
					return false;
				} else {
					return isEditable(row, col);
				}
			}
		};
		billModel.setBodyItems(abillBody);
		getmainUpbsp().setTableModel(billModel);
		getmainUpbsp().getTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		getmainUpbsp().getTable().setSelectionMode(
				javax.swing.ListSelectionModel.SINGLE_SELECTION);
		getmainUpbsp().setRowNOShow(true);
		getmainUpbsp().addEditListener(this);
	}

	/**
	 * 单元格可编辑性
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	private boolean isEditable(int row, int col) {
		boolean isEditable = false;
		// 如果是增加行， 并且选择的行是最后一行， 则整行可编辑
		if (row == last_select_row_number) {
			if (oprationFlag == OprationFlag.GradeverAdd
					|| oprationFlag == OprationFlag.GradeverEdit) {
				return true;
			}
		}

		return isEditable;
	}

	private nc.ui.pub.bill.BillScrollPane getmainbsp() {
		if (ivjmainbsp == null) {
			ivjmainbsp = new nc.ui.pub.bill.BillScrollPane();
			ivjmainbsp.setName("mainbsp");
			ivjmainbsp.setBounds(230, 500, 564, 217);
			ivjmainbsp.setAutoAddLine(false);
		}
		return ivjmainbsp;
	}

	protected UISplitPane getSplitPane() {
		if (splitPanel == null) {
			splitPanel = new UISplitPane(JSplitPane.VERTICAL_SPLIT,
					getmainUpbsp(), getmainbsp());
			splitPanel.setOneTouchExpandable(true);
			splitPanel.setPreferredSize(new Dimension(298, 469));
			splitPanel.setDividerLocation(175);
			// 20151028 shenliangc 原来条柄尺寸太小，显示不完整。
			splitPanel.setDividerSize(10);// 增加了条柄的大小，方便拖拽
		}
		return splitPanel;
	}

	private void initMainUpTableData() {
		try {
			// 初始化数据
			/*
			 * billModel.directlyUpdate(billModel.queryAggWagradeByGrdPK(parentForm
			 * .getHeadItem(WaGradeVO.PK_WA_GRD).getValueObject() .toString()));
			 */
			gradeVerVOs = billModel.queryGradeVerByGradePK(parentForm
					.getHeadItem(WaGradeVO.PK_WA_GRD).getValueObject()
					.toString());
			getmainUpbsp().getTableModel().setBodyDataVO(gradeVerVOs);
			getmainUpbsp().getTable().setSortEnabled(false);
			TableColResize.reSizeTable(getmainUpbsp());
			if (gradeVerVOs != null && gradeVerVOs.length > 0)
				getmainUpbsp().getTable().setRowSelectionInterval(0, 0);
			else
				parentForm.resetCrtTableData();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 返回放置按钮的TOppanel
	 */
	protected UIPanel getTopButtonPanel() {
		if (topButtonPanel == null) {
			topButtonPanel = new UIPanel();
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			topButtonPanel.setLayout(flowLayout);
		}
		return topButtonPanel;
	}

	/**
	 * 为对话框增加按钮 <br>
	 */
	protected void initVerButton() {
		getTopButtonPanel().removeAll();
		btnAddVer = new UIButton(ResHelper.getString("60130paystd",
				"060130paystd0135")/* @res "增加版本" */);
		btnAddVer.addActionListener(this);
		btnAddVer.setToolTipText(ResHelper.getString("60130paystd",
				"060130paystd0195")/* @res "增加版本(Alt+N)" */);

		btnEditVer = new UIButton(ResHelper.getString("60130paystd",
				"060130paystd0144")/* @res "修改版本" */);
		btnEditVer.addActionListener(this);
		btnEditVer.setToolTipText(ResHelper.getString("60130paystd",
				"060130paystd0196")/* @res "修改版本 (Alt+E)" */);

		btnDeleteVer = new UIButton(ResHelper.getString("60130paystd",
				"060130paystd0197")/* @res "刪除版本" */);
		btnDeleteVer.addActionListener(this);
		btnDeleteVer.setToolTipText(ResHelper.getString("60130paystd",
				"060130paystd0198")/* @res "删除版本 (Alt+Del)" */);

		btnSaveVer = new UIButton(ResHelper.getString("60130paystd",
				"060130paystd0146")/* @res "保存版本" */);
		btnSaveVer.addActionListener(this);
		btnSaveVer.setToolTipText(ResHelper.getString("60130paystd",
				"060130paystd0199")/* @res "保存版本 (Alt+S)" */);

		btnCancelVer = new UIButton(ResHelper.getString("common",
				"UC001-0000008")/* @res "取消" */);
		btnCancelVer.addActionListener(this);
		btnCancelVer.setToolTipText(ResHelper.getString("60130paystd",
				"060130paystd0200")/* @res "取消(Alt+Y)" */);

		getTopButtonPanel().add(btnAddVer);
		getTopButtonPanel().add(btnEditVer);
		getTopButtonPanel().add(btnDeleteVer);
		getTopButtonPanel().add(btnSaveVer);
		getTopButtonPanel().add(btnCancelVer);
		setBtnEnabled(true);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		btnAddStr = "";
		if (evt.getSource() == btnCancel) {
			onBtnCloseClick();
		} else if (evt.getSource() == btnSaveVer) {
			onBtnSaveClick();
		} else if (evt.getSource() == btnAddVer) {
			onAddClick();
			btnAddStr = ADDSTR;
		} else if (evt.getSource() == btnEditVer) {
			onEditClick();
		} else if (evt.getSource() == btnCancelVer) {
			oncancleClick();
		} else if (evt.getSource() == btnDeleteVer) {
			onDeleteClick();
		}
	}

	private void onAddClick() {
		oprationFlag = OprationFlag.GradeverAdd;
		setSelectedRow(getmainUpbsp(), getmainUpbsp().getTable()
				.getSelectedRow());
		addLine(getmainUpbsp());
		setBtnEnabled(false);
		getmainbsp().getTableModel().setEnabled(true);
		try {
			WaGradeVerVO vervo = WASalaryadjmgtDelegator.getGradeQueryService()
					.queryEffectGradeVerVO(
							parentForm.getHeadItem(WaGradeVO.PK_WA_GRD)
									.getValueObject().toString(),
							null,
							(Boolean) parentForm.getHeadItem(
									WaGradeVO.ISMULTSEC).getValueObject());
			// 2017-10-11 zhousze 薪资加密：这里处理薪资标准表界面数据解密 begin
			CrtVO[] crtVOs = vervo.getCrtVOs();
			for (CrtVO vo : crtVOs) {
				ArrayList<Object> list = (ArrayList<Object>) vo.getVecData();
				for (Object waCriVO : list) {
					WaCriterionVO vo1 = (WaCriterionVO) waCriVO;
					vo1.setCriterionvalue(new UFDouble(
							SalaryDecryptUtil
									.decrypt((vo1.getCriterionvalue() == null ? new UFDouble(
											0) : vo1.getCriterionvalue())
											.toDouble())));
					vo1.setMax_value(new UFDouble(
							SalaryDecryptUtil
									.decrypt((vo1.getMax_value() == null ? new UFDouble(
											0) : vo1.getMax_value()).toDouble())));
					vo1.setMin_value(new UFDouble(
							SalaryDecryptUtil
									.decrypt((vo1.getMin_value() == null ? new UFDouble(
											0) : vo1.getMin_value()).toDouble())));
				}
			}
			// end
			billModel.setCrtvos(crtVOs);
			getmainbsp().getTableModel().setBodyDataVO(crtVOs);
			getmainbsp().getTable().setSortEnabled(false);
			TableColResize.reSizeTable(getmainbsp());
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			Logger.error(e.getMessage(), e);
		}
	}

	private void onEditClick() {
		// 修改当前状态
		oprationFlag = OprationFlag.GradeverEdit;
		// 设置选中行
		setSelectedRow(getmainUpbsp(), getmainUpbsp().getTable()
				.getSelectedRow());
		getmainUpbsp().getTable().setRowSelectionInterval(
				last_select_row_number, last_select_row_number);
		setBtnEnabled(false);
		controlTableEnable();
	}

	private void oncancleClick() {
		initSomething();
		setSelectedRow(getmainUpbsp(), last_select_row_number);
		initSomething();
	}

	/**
	 * 删除版本
	 */
	private void onDeleteClick() {
		WaGradeVerVO sleleVerVO = null;

		if (getmainUpbsp().getTable().getSelectedRow() != -1) {
			sleleVerVO = (WaGradeVerVO) getmainUpbsp().getTableModel()
					.getBodyValueRowVO(
							getmainUpbsp().getTable().getSelectedRow(),
							WaGradeVerVO.class.getName());
		}
		if (sleleVerVO == null
				|| StringUtils.isBlank(sleleVerVO.getPk_wa_gradever())) {
			resetCrtTableData();
			getmainbsp().getTable().setSortEnabled(false);
			return;
		}

		if (sleleVerVO.getEffect_flag() != null
				&& sleleVerVO.getEffect_flag().booleanValue()
				&& getmainUpbsp().getTable().getRowCount() > 0) {
			MessageDialog.showHintDlg(this, null,
					ResHelper.getString("60130paystd", "060130paystd0141")/*
																		 * @res
																		 * "生效的薪资标准不允许删除！"
																		 */);
			return;
		}

		if (UIDialog.ID_OK != MessageDialog.showOkCancelDlg(this, null,
				ResHelper.getString("60130paystd", "060130paystd0142")/*
																	 * @res
																	 * "您确定要删除所选数据吗？"
																	 */)) {
			return;
		}

		try {
			billModel.deleteGradeVerByVO(sleleVerVO);
			// parentForm.onRefresh();
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			MessageDialog.showErrorDlg(null, null, e.getMessage());
		}

		initSomething();
		if (last_select_row_number - 1 >= 0)
			setSelectedRow(getmainUpbsp(), last_select_row_number - 1);
	}

	private void onBtnCloseClick() {
		if (oprationFlag == OprationFlag.GradeverAdd
				|| oprationFlag == OprationFlag.GradeverEdit) {
			int intResult = MessageDialog.showYesNoCancelDlg(null, null,
					ResHelper.getString("60130paystd", "060130paystd0201")/*
																		 * @res
																		 * "正处于编辑态,是否保存?"
																		 */);
			if (intResult == MessageDialog.ID_YES) {
				onBtnSaveClick();
			} else if (intResult == MessageDialog.ID_CANCEL) {
				return;
			}
		}
		this.closeCancel();
	}

	/**
	 * 保存薪资标准表<BR>
	 * 增加了对金额排序的校验<BR>
	 * 
	 * @return
	 * @throws BusinessException
	 * @throws BusinessException
	 */
	private boolean onBtnSaveClick() {
		try {
			TableCellEditor editor = getmainbsp().getTable().getCellEditor();
			if (editor != null) {
				editor.stopCellEditing();
			}
			TableCellEditor vereditor = getmainUpbsp().getTable()
					.getCellEditor();
			if (vereditor != null) {
				vereditor.stopCellEditing();
			}
			if (oprationFlag == OprationFlag.GradeverAdd
					|| oprationFlag == OprationFlag.GradeverEdit) {

				// 校验生效标记
				validateEffectFlag((WaGradeVerVO[]) getmainUpbsp()
						.getTableModel().getBodyValueVOs(
								WaGradeVerVO.class.getName()));
				// 取得选中的生效标记及其对应的薪资标准表
				WaGradeVerVO sleleVerVO = null;

				if (getmainUpbsp().getTable().getSelectedRow() != -1) {
					sleleVerVO = (WaGradeVerVO) getmainUpbsp().getTableModel()
							.getBodyValueRowVO(last_select_row_number,
									WaGradeVerVO.class.getName());
				}
				// 2016-11-29 zhousze 薪资加密：薪资标准表新增、修改保存时，对数据加密 begin
				CrtVO[] crtVOs = getParentForm().disToCrtVO(
						getmainbsp().getTableModel(), getmainbsp().getTable());
				for (CrtVO vo : crtVOs) {
					ArrayList<Object> list = (ArrayList<Object>) vo
							.getVecData();
					for (Object waCriVO : list) {
						WaCriterionVO vo1 = (WaCriterionVO) waCriVO;
						vo1.setCriterionvalue(new UFDouble(
								SalaryEncryptionUtil.encryption((vo1
										.getCriterionvalue() == null ? new UFDouble(
										0) : vo1.getCriterionvalue())
										.toDouble())));
						vo1.setMax_value(new UFDouble(
								SalaryEncryptionUtil.encryption((vo1
										.getMax_value() == null ? new UFDouble(
										0) : vo1.getMax_value()).toDouble())));
						vo1.setMin_value(new UFDouble(
								SalaryEncryptionUtil.encryption((vo1
										.getMin_value() == null ? new UFDouble(
										0) : vo1.getMin_value()).toDouble())));
					}
				}
				sleleVerVO.setCrtVOs(crtVOs);
				// end
				// sleleVerVO.setCrtVOs(getParentForm().disToCrtVO(getmainbsp().getTableModel(),
				// getmainbsp().getTable()));

				if (oprationFlag == OprationFlag.GradeverAdd) {
					sleleVerVO.setStatus(VOStatus.NEW);
				} else {
					sleleVerVO.setStatus(VOStatus.UPDATED);
				}
				WaGradeVO wagradevo = (WaGradeVO) ((AggWaGradeVO) billModel
						.getSelectedData()).getParentVO();
				sleleVerVO.setPk_wa_grd(wagradevo.getPk_wa_grd());
				// 为了校验使用
				billModel.setWaGradeVO(wagradevo);
				WaGradeCrtRangeValidator crtRangeValidator = new WaGradeCrtRangeValidator();
				crtRangeValidator.setModel(billModel);
				ValidationFailure failure = crtRangeValidator
						.validate(sleleVerVO);
				if (failure != null
						&& !StringUtils.isBlank(failure.getMessage())) {
					Logger.error(failure.getMessage());
					throw new BusinessException(failure.getMessage());
				}
				// 跟据薪资标准表的排序方式校验金额是否合法
				WaGradeCrtValueValidator crtgValidator = new WaGradeCrtValueValidator();
				crtgValidator.setModel(billModel);
				failure = crtgValidator.validate(sleleVerVO);
				if (failure != null
						&& !StringUtils.isBlank(failure.getMessage())) {
					Logger.error(failure.getMessage());
					MessageDialog.showHintDlg(this, null, failure.getMessage());
					// throw new BusinessException(failure.getMessage());
				}
				billModel.processCriterionArray(wagradevo, sleleVerVO);
				billModel.directlyUpdate(billModel
						.queryAggWagradeByGrdPK(parentForm
								.getHeadItem(WaGradeVO.PK_WA_GRD)
								.getValueObject().toString()));
				initMainUpTableData();
				getmainUpbsp().getTable().setRowSelectionInterval(
						last_select_row_number, last_select_row_number);
				initCrtTableData();
			}
			getmainbsp().setEnabled(false);
			getmainUpbsp().setEnabled(false);
			// parentForm.onRefresh();
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			MessageDialog.showHintDlg(this, null, e.getMessage());
			return false;
		}

		setBtnEnabled(true);
		initSomething();
		return true;
	}

	/**
	 * 增加版本
	 * 
	 * @param pane
	 */
	private void addLine(BillScrollPane pane) {
		pane.getTableModel().addLine();
		// 版本记录数
		int intNum = pane.getTable().getRowCount();

		int intMaxNum = 0;
		UFDouble lineNumber = null;
		try {
			lineNumber = getParentForm().getGradeModel().getMaxVerNum(
					parentForm.getHeadItem(WaGradeVO.PK_WA_GRD)
							.getValueObject().toString());
		} catch (BusinessException e) {
		}
		if (lineNumber != null) {
			intMaxNum = (int) lineNumber.doubleValue();
		}
		// 版本记录数
		pane.getTable().setValueAt(intMaxNum + 1, intNum - 1, 0);
		pane.getTable().setValueAt(PubEnv.getServerDate(), intNum - 1, 2);
		setSelectedRow(pane, intNum - 1);

	}

	private int last_select_row_number = 0;

	/**
	 * 指定的BillScrollPane选中行，如果selRow不在范围内，则选中最后一行
	 */
	private void setSelectedRow(BillScrollPane pane, int selRow) {
		if (!isOutOfRange(pane.getTableModel(), selRow)) {
			last_select_row_number = selRow;
			pane.getTable().setRowSelectionInterval(selRow, selRow);

		} else {// 选中最后一行
			if (pane.getTableModel().getRowCount() > 0) {
				int tmep = pane.getTableModel().getRowCount() - 1;
				last_select_row_number = tmep;
				pane.getTable().setRowSelectionInterval(tmep, tmep);

			}
		}
	}

	private boolean isOutOfRange(BillModel model, int toBeSelrow) {
		if (toBeSelrow > model.getRowCount() - 1 || toBeSelrow < 0) {
			return true;
		}
		return false;
	}

	// /*********************************************************************************************************
	// * 为对话框增加按钮 <br>
	// * Created on 2004-10-14 13:34:59</br>
	// ********************************************************************************************************/
	// @Override
	// protected void initButton(){
	// btnCancel = new
	// UIButton(ResHelper.getString("60130paystd","060130paystd0202")/*@res
	// "关闭"*/);
	// btnCancel.addActionListener(this);
	// btnCancel.setToolTipText(ResHelper.getString("60130paystd","060130paystd0203")/*@res
	// "关闭(Alt+C)"*/);
	// // getButtonPanel().add(btnCancel);
	// }

	@Override
	public void afterEdit(BillEditEvent e) {
		if (e.getKey().equals(WaGradeVerVO.EFFECT_FLAG)) {
			Object object = getmainUpbsp().getTableModel().getValueAt(
					e.getRow(), WaGradeVerVO.EFFECT_FLAG);
			Boolean blnEffectFlag = false;
			if (object != null) {
				blnEffectFlag = (Boolean) object;
			}

			int rowCnt = getmainUpbsp().getTable().getRowCount();
			if (blnEffectFlag) {
				for (int i = 0; i < rowCnt; i++) {

					if (i == e.getRow()) {
						continue;
					}
					getmainUpbsp().getTableModel().setValueAt(
							UFBoolean.valueOf(false), i,
							WaGradeVerVO.EFFECT_FLAG);
				}
			} else {
				for (int i = 0; i < rowCnt; i++) {

					object = getmainUpbsp().getTableModel().getValueAt(i,
							WaGradeVerVO.EFFECT_FLAG);
					if (object != null) {
						blnEffectFlag = (Boolean) object;
					}
					if (blnEffectFlag) {
						break;
					}
				}
			}

			if (!blnEffectFlag) {
				IWaGradeService service = NCLocator.getInstance().lookup(
						IWaGradeService.class);
				String msg = null;
				try {
					msg = service
							.validateGradeHaveReferenceByBusiness(parentForm
									.getHeadItem(WaGradeVO.PK_WA_GRD)
									.getValueObject().toString());
					if (msg != null && !"".equals(msg)) {
						MessageDialog.showHintDlg(this, null, msg);
					}
				} catch (BusinessException ex) {
					Logger.error(ex.getMessage(), ex);
				}
			}
			if (!btnAddStr.equals(ADDSTR)) {
				if (blnEffectFlag) {
					getmainbsp().getTableModel().setEnabled(false);
				} else {
					getmainbsp().getTableModel().setEnabled(true);
				}
			}
		}
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
		int rowCount = getmainUpbsp().getTable().getRowCount();
		if (oprationFlag != OprationFlag.No_Opration
				&& last_select_row_number != getmainUpbsp().getTable()
						.getSelectedRow()) {
			initCrtTableData();// zhoumxc 2014.08.13 修改薪资标准表，版本信息维护控制问题
			return;
		}
		setSelectedRow(getmainUpbsp(), e.getRow());
		if (rowCount <= 0) {
			// 总行数小于等于一，不可移动
			return;
		} else if (rowCount > 0) {
			initCrtTableData();
		}
	}

	/**
	 * 初始薪资标准表数据
	 */
	private void initCrtTableData() {
		try {

			WaGradeVerVO sleleVerVO = null;

			if (getmainUpbsp().getTable().getSelectedRow() != -1) {
				sleleVerVO = (WaGradeVerVO) getmainUpbsp().getTableModel()
						.getBodyValueRowVO(
								getmainUpbsp().getTable().getSelectedRow(),
								WaGradeVerVO.class.getName());
			}
			if (sleleVerVO == null
					|| StringUtils.isBlank(sleleVerVO.getPk_wa_gradever())) {
				resetCrtTableData();
				getmainbsp().getTable().setSortEnabled(false);
				// return;//zhoumxc 2014.08.13 薪资标准表，版本信息维护控制问题
			}
			// 初始化数据
			CrtVO[] crtVOs = billModel.queryCriterionByClassid(getParentForm()
					.getStrPkWaGrd(), sleleVerVO.getPk_wa_gradever(),
					getParentForm().isMultsecCkeck());
			// 2016-11-29 zhousze 薪资加密：薪资标准表版本操作是，查询出的数据进行解密 begin
			for (CrtVO vo : crtVOs) {
				ArrayList<Object> list = (ArrayList<Object>) vo.getVecData();
				for (Object waCriVO : list) {
					WaCriterionVO vo1 = (WaCriterionVO) waCriVO;
					vo1.setCriterionvalue(new UFDouble(
							SalaryDecryptUtil
									.decrypt((vo1.getCriterionvalue() == null ? new UFDouble(
											0) : vo1.getCriterionvalue())
											.toDouble())));
					vo1.setMax_value(new UFDouble(
							SalaryDecryptUtil
									.decrypt((vo1.getMax_value() == null ? new UFDouble(
											0) : vo1.getMax_value()).toDouble())));
					vo1.setMin_value(new UFDouble(
							SalaryDecryptUtil
									.decrypt((vo1.getMin_value() == null ? new UFDouble(
											0) : vo1.getMin_value()).toDouble())));
				}
			}
			// end
			billModel.setCrtvos(crtVOs);
			getmainbsp().getTableModel().setBodyDataVO(crtVOs);
			getmainbsp().getTable().setSortEnabled(false);
			TableColResize.reSizeTable(getmainbsp());
		} catch (Exception e) {
			MessageDialog.showErrorDlg(this, null, e.getMessage());
			return;
		}
	}

	/**
	 * 设置按钮组的可用性
	 * 
	 * @param blnIsEnabled
	 */
	private void setBtnEnabled(boolean blnIsBrowese) {
		btnAddVer.setVisible(blnIsBrowese);
		if (getmainUpbsp().getTable().getSelectedRow() == -1) {
			btnEditVer.setEnabled(false);
			btnDeleteVer.setEnabled(false);
		} else {
			btnEditVer.setEnabled(blnIsBrowese);
			btnDeleteVer.setEnabled(blnIsBrowese);
			btnEditVer.setVisible(blnIsBrowese);
			btnDeleteVer.setVisible(blnIsBrowese);
		}

		btnSaveVer.setVisible(!blnIsBrowese);
		btnCancelVer.setVisible(!blnIsBrowese);
		if (blnIsBrowese)
			oprationFlag = OprationFlag.No_Opration;
		getmainbsp().getTableModel().setEnabled(!blnIsBrowese);
	}

	/**
	 * 薪资标准版本的生效日期的校验
	 * 
	 * @param vervos
	 * @throws BusinessException
	 */
	private void validateEffectFlag(WaGradeVerVO[] vervos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vervos)) {
			return;
		}
		int intErrorFlag = 0;
		for (WaGradeVerVO vervo : vervos) {
			if (vervo.getEffect_flag() != null
					&& vervo.getEffect_flag().booleanValue()) {
				intErrorFlag++;
			}
		}

		if (intErrorFlag == 1) {
			return;
		}
		String strErrorMessage = null;
		if (intErrorFlag == 0) {
			IWaGradeService service = NCLocator.getInstance().lookup(
					IWaGradeService.class);
			String msg = null;
			try {
				msg = service.validateGradeHaveReferenceByBusiness(parentForm
						.getHeadItem(WaGradeVO.PK_WA_GRD).getValueObject()
						.toString());
				if (msg != null && !"".equals(msg)) {
					strErrorMessage = msg;
				}
			} catch (BusinessException ex) {
				Logger.error(ex.getMessage(), ex);
			}
		} else if (intErrorFlag > 1) {
			strErrorMessage = ResHelper.getString("60130paystd",
					"060130paystd0206")/* @res "一个薪资标准中不能有多个生效标记被选中！" */;
		}

		if (!StringUtils.isBlank(strErrorMessage)) {
			Logger.debug("strErrorMessage");
			throw new BusinessException(strErrorMessage);
		}
	}

	/**
	 * 重置薪资标准表数据
	 */
	private void resetCrtTableData() {
		int cols = getmainbsp().getTableModel().getColumnCount();
		int rows = getmainbsp().getTableModel().getRowCount();
		for (int i = 0; i < rows; i++) {
			// 列的统计
			for (int j = 1; j < cols; j++) {
				getmainbsp().getTableModel().setValueAt(0, i, j);
			}
		}

	}

	@Override
	protected void hotKeyPressed(KeyStroke hotKey, KeyEvent evt) {

		int iModifiers = hotKey.getModifiers();

		// Single hot key:
		if (iModifiers == 0) {
			return;
		}

		boolean blAlt = false;
		boolean blCrtl = false;

		if ((iModifiers & Event.ALT_MASK) != 0) {
			blAlt = true;
		}

		if ((iModifiers & Event.CTRL_MASK) != 0) {
			blCrtl = true;
		}

		// 处理Alt+Y
		if (!blCrtl && blAlt && hotKey.getKeyCode() == KeyEvent.VK_Q) {
			oncancleClick();
		}
		// Ctrl+Alt+N
		// 处理Alt+Y
		if (!blCrtl && blAlt && hotKey.getKeyCode() == KeyEvent.VK_N
				&& oprationFlag == OprationFlag.No_Opration) {
			onAddClick();
		} else if (!blCrtl && blAlt && hotKey.getKeyCode() == KeyEvent.VK_S) {
			onBtnSaveClick();
		} else if (!blCrtl && blAlt && hotKey.getKeyCode() == KeyEvent.VK_E
				&& oprationFlag == OprationFlag.No_Opration) {
			onEditClick();
		} else if (!blCrtl && blAlt
				&& hotKey.getKeyCode() == KeyEvent.VK_DELETE
				&& oprationFlag == OprationFlag.No_Opration) {
			onDeleteClick();
		}
		super.hotKeyPressed(hotKey, evt);
	}

	public WaGradeForm getParentForm() {
		return parentForm;
	}

	public void setParentForm(WaGradeForm parentForm) {
		this.parentForm = parentForm;
	}

	@Override
	protected JComponent createCenterPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	private void controlTableEnable() {
		WaGradeVerVO sleleVerVO = null;
		if (getmainUpbsp().getTable().getSelectedRow() != -1) {
			sleleVerVO = (WaGradeVerVO) getmainUpbsp().getTableModel()
					.getBodyValueRowVO(last_select_row_number,
							WaGradeVerVO.class.getName());
			if (sleleVerVO.getEffect_flag().booleanValue()) {
				getmainbsp().getTableModel().setEnabled(false);
			}
		}
	}
}