package nc.ui.wa.adjust.wizard;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.ui.hr.frame.util.table.SelectableBillScrollPane;
import nc.ui.hr.global.Global;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.table.ColumnGroup;
import nc.ui.pub.beans.table.GroupableTableHeader;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepValidateException;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillModelCellEditableController;
import nc.ui.wa.adjust.model.BatchAdjustWizardModel;
import nc.ui.wa.ref.WAGradePrmSecDia;
import nc.ui.wa.salaryadjmgt.WASalaryadjmgtDelegator;
import nc.ui.wa.salaryadjmgt.WaSalaryadjmgtUtility;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.adjust.AdjustWadocVO;
import nc.vo.wa.adjust.BatchAdjustVO;
import nc.vo.wa.adjust.BatchAdjustVO.ADJUST_FROM;
import nc.vo.wa.adjust.IWaAdjustConstant;
import nc.vo.wa.adjust.PsnappaproveBVO;
import nc.vo.wa.adjust.WaAdjustParaTool;
import nc.vo.wa.grade.WaCriterionVO;
import nc.vo.wa.grade.WaGradeVO;
import nc.vo.wa.item.WaItemVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * н���յ��� �ڶ�����
 * 
 * @author xuhw
 * @date: 2010-9-6
 * @since: eHR V5.7
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class BatchAdjustTwoWizardStep extends WizardStep implements
		IWizardStepListener, BillEditListener, ValueChangedListener {

	private SelectableBillScrollPane psnBillScrollPane = null;
	private HashMap<String, Vector<String>> colNameMap = null;
	private AdjustWadocVO[] datas = null;
	private BatchAdjustVO batchAdjustVO = null;
	private BillCardPanel cpBill;
	private static String NODE_KEY = "adj_step2";
	private BatchAdjustWizardModel wizardModel;
	private BillModel billModel;
	/** �Ƿ��ѡ */
	boolean isMultsecCkeck = false;
	/** �Ƿ����н�� */
	boolean isRangeCkeck = false;
	private UFBoolean partflagShow = null;

	public BatchAdjustTwoWizardStep(BatchAdjustWizardModel wizardModel) {
		super();
		setWizardModel(wizardModel);
		setTitle(ResHelper.getString("60130adjapprove", "060130adjapprove0088")/*
																				 * @
																				 * res
																				 * "�յ����ȷ��"
																				 */);
		setDescription(ResHelper.getString("60130adjapprove",
				"060130adjapprove0088")/* @res "�յ����ȷ��" */);
		setComp(getPsnBillScrollPane());
		addListener(this);

	}

	public SelectableBillScrollPane getPsnBillScrollPane() {
		if (psnBillScrollPane == null) {
			psnBillScrollPane = new SelectableBillScrollPane();
			psnBillScrollPane.setName("adjustBillScrollPane");
			psnBillScrollPane.add(getBillCardPanel());
			billModel = new BillModel();
			BillItem[] billItems = getBillCardPanel().getBodyItems();
			/*
			 * UFBoolean partflagShow=
			 * WaAdjustParaTool.getPartjob_Adjmgt(getWizardModel
			 * ().getLoginContext().getPk_group());
			 * if(UFBoolean.TRUE.equals(partflagShow)){
			 * billItems[3].setShow(true); billItems[4].setShow(true); }
			 */
			billModel.setBodyItems(billItems);

			psnBillScrollPane.setTableModel(billModel);
			psnBillScrollPane.getTable().setSelectionMode(
					ListSelectionModel.SINGLE_SELECTION);
			psnBillScrollPane.setSelectRowCode("approved");
			psnBillScrollPane.setRowNOShow(true);
			if (!getPartflagShow().booleanValue()) {
				psnBillScrollPane.hideTableCol(PsnappaproveBVO.PARTFLAG);
				psnBillScrollPane.hideTableCol(PsnappaproveBVO.ASSGID);
			}
			initColumnGroup(getColumnGroupHash());
			psnBillScrollPane.setPreferredSize(new Dimension(780, 50));
			psnBillScrollPane.addEditListener(this);
			// addDecimalListener();
		}
		return psnBillScrollPane;
	}

	/**
	 * ����ģ�忨ƬPanel
	 */
	private BillCardPanel getBillCardPanel() {
		if (cpBill == null) {
			cpBill = new BillCardPanel();
			cpBill.setPreferredSize(new Dimension(780, 50));
			cpBill.loadTemplet(NODE_KEY, null, Global.getUserID(),
					Global.getGroupPK());
		}
		return cpBill;
	}

	/**
	 * У���Ƿ��Ѿ�ѡ������Ա
	 */
	@Override
	public void validate() throws WizardStepValidateException {
		super.validate();
		AdjustWadocVO[] selectVOs = getSelectedVO();
		String strErrorMessage = BatchAdjustValidator
				.validateBatchAdjustVOs(selectVOs);
		if (!StringUtils.isBlank(strErrorMessage)) {
			Logger.debug(strErrorMessage.toString());
			WizardStepValidateException e = new WizardStepValidateException();
			e.addMsg(ResHelper.getString("60130adjapprove",
					"060130adjapprove0089")/* @res "��ʾ:" */, strErrorMessage
					.toString());
			throw e;
		}
	}

	@Override
	public void stepActived(WizardStepEvent event) {
		// ���б�����ʾVO
		batchAdjustVO = (BatchAdjustVO) getWizardModel().getContext()
				.getAttribute(IWaAdjustConstant.STEP_ONE_DATA);
		datas = (AdjustWadocVO[]) getWizardModel().getContext().getAttribute(
				IWaAdjustConstant.STEP_TWO_DATA);
		if (!ArrayUtils.isEmpty(datas)) {
			getPsnBillScrollPane().selectAllRows();
			if (datas == null) {
				return;
			}
			for (AdjustWadocVO data : datas) {
				data.setApproved(UFBoolean.TRUE);
			}
		}

		BillItem[] billitems = getBillCardPanel().getBodyItems();
		for (BillItem billitem : billitems) {

			if (billitem.getKey().equals(AdjustWadocVO.MONEY_OLD)
					|| billitem.getKey().equals(AdjustWadocVO.GRADE_MONEY_OLD)
					|| billitem.getKey().equals(AdjustWadocVO.GRADE_MONEY_NEW)
					|| billitem.getKey().equals(AdjustWadocVO.MONEY_ADJUST)
					|| billitem.getKey().equals(
							AdjustWadocVO.GRADE_MONEY_ADJUST)
					|| billitem.getKey().equals(AdjustWadocVO.CHANGE_MONEY)) {
				billitem.setDecimalDigits((Integer) wizardModel.getContext()
						.getAttribute(WaItemVO.IFLDDECIMAL));
			}
		}
		ADJUST_FROM enumDiv = (ADJUST_FROM) getWizardModel().getContext()
				.getAttribute(IWaAdjustConstant.OPER_NODE_DIV);
		if (enumDiv == ADJUST_FROM.FROM_PSNDOCWADOC) {
			getPsnBillScrollPane().showTableCol(AdjustWadocVO.BEGINDATE);
		} else {
			getPsnBillScrollPane().hideTableCol(AdjustWadocVO.BEGINDATE);
		}
		// 2016-12-05 zhousze н�ʼ��ܣ����ﴦ��н���յ��������ݽ��� begin
		for (AdjustWadocVO vo : datas) {
			vo.setMoney_old(vo.getMoney_old() == null ? null : new UFDouble(
					SalaryDecryptUtil.decrypt(vo.getMoney_old().toDouble())));
			vo.setGrade_money_old(vo.getGrade_money_old() == null ? null
					: new UFDouble(SalaryDecryptUtil.decrypt(vo
							.getGrade_money_old().toDouble())));
			vo.setGrade_money_new(vo.getGrade_money_new() == null ? null
					: new UFDouble(SalaryDecryptUtil.decrypt(vo
							.getGrade_money_new().toDouble())));
			vo.setMoney_adjust(vo.getMoney_adjust() == null ? null
					: new UFDouble(SalaryDecryptUtil.decrypt(vo
							.getMoney_adjust().toDouble())));
			vo.setGrade_money_adjust(vo.getGrade_money_adjust() == null ? null
					: new UFDouble(SalaryDecryptUtil.decrypt(vo
							.getGrade_money_adjust().toDouble())));
			vo.setChange_money(vo.getChange_money() == null ? null
					: new UFDouble(vo.getMoney_adjust().toDouble()
							- vo.getMoney_old().toDouble()));
			vo.setCrt_min_value(vo.getCrt_min_value() == null ? null
					: new UFDouble(SalaryDecryptUtil.decrypt(vo
							.getCrt_min_value().toDouble())));
			vo.setCrt_max_value(vo.getCrt_max_value() == null ? null
					: new UFDouble(SalaryDecryptUtil.decrypt(vo
							.getCrt_max_value().toDouble())));
		}
		// end
		getPsnBillScrollPane().getTableModel().setBodyDataVO(datas);
		setCellEditableController();
	}

	/**
	 * ��ȡѡ�е�VO
	 */
	private AdjustWadocVO[] getSelectedVO() {
		return (AdjustWadocVO[]) getPsnBillScrollPane().getSelectedBodyVOs(
				AdjustWadocVO.class);
	}

	/**
	 * �õ��ϲ��Ķ��ͷ�ķ����ֶ�
	 * 
	 * @return
	 */
	private HashMap<String, Vector<String>> getColumnGroupHash() {
		if (colNameMap == null) {
			colNameMap = new HashMap<String, Vector<String>>();

			// ����ǰ
			String oldGroupColName = ResHelper.getString("60130adjapprove",
					"060130adjapprove0090")/* @res "����ǰ" */;
			Vector<String> oldKeys = new Vector<String>();
			oldKeys.add(AdjustWadocVO.CRT_NAME_OLD);
			// oldKeys.add("seclv_name_old");
			oldKeys.add(AdjustWadocVO.MONEY_OLD);
			oldKeys.add(AdjustWadocVO.GRADE_MONEY_OLD);
			colNameMap.put(oldGroupColName, oldKeys);

			// ������
			String applyGroupColName = ResHelper.getString("60130adjapprove",
					"060130adjapprove0091")/* @res "������" */;
			Vector<String> applyKeys = new Vector<String>();
			applyKeys.add(AdjustWadocVO.NEGOTIATION);
			applyKeys.add(AdjustWadocVO.CRT_NAME_ADJUST);
			// applyKeys.add("seclv_name_adjust");
			applyKeys.add(AdjustWadocVO.MONEY_ADJUST);
			applyKeys.add(AdjustWadocVO.GRADE_MONEY_ADJUST);
			colNameMap.put(applyGroupColName, applyKeys);
		}

		return colNameMap;
	}

	/**
	 * ���ö��ͷ
	 * 
	 * @param colNameMap
	 */
	public void initColumnGroup(HashMap<String, Vector<String>> colNameMap) {
		Iterator<String> iterator = colNameMap.keySet().iterator();
		while (iterator.hasNext()) {
			String groupColName = iterator.next();
			Vector<String> groupedColKeys = colNameMap.get(groupColName);
			// �����ͷ�ϲ�
			setColumnGroup(groupColName, groupedColKeys);
		}
	}

	/**
	 * �ϲ����ͷ
	 * 
	 * @param groupColName
	 * @param groupedColKeys
	 */
	private void setColumnGroup(String groupColName,
			Vector<String> groupedColKeys) {
		GroupableTableHeader header = (GroupableTableHeader) getPsnBillScrollPane()
				.getTable().getTableHeader();

		ColumnGroup colGroup = new ColumnGroup(groupColName);
		for (String colKey : groupedColKeys) {
			colGroup.add(tableColumn(colKey));
		}
		header.addColumnGroup(colGroup);

	}

	/**
	 * ������colKey�ҵ���Ӧ����
	 * 
	 * @param colKey
	 * @return
	 */
	private TableColumn tableColumn(String colKey) {

		int colIndex = getPsnBillScrollPane().getTableModel().getBodyColByKey(
				colKey);
		TableColumnModel cm = getPsnBillScrollPane().getTable()
				.getColumnModel();
		if (!getPartflagShow().booleanValue()) {
			colIndex = colIndex - 1;
		}
		return cm.getColumn(colIndex);
	}

	@Override
	public void stepDisactived(WizardStepEvent event) {
		// ������ѡ��VO
		getWizardModel().getContext().setAttribute(
				IWaAdjustConstant.SELECT_DATAS, getSelectedVO());
		// ���»���ȫ��VO�����Ժ��践��ʱ����ԭ����ѡ��״̬
		datas = (AdjustWadocVO[]) getPsnBillScrollPane().getTableModel()
				.getBodyValueVOs(AdjustWadocVO.class.getName());
		getWizardModel().getContext().setAttribute(
				IWaAdjustConstant.STEP_TWO_DATA, datas);
		datas = null;
	}

	@Override
	public void afterEdit(BillEditEvent e) {

		int rowIndex = psnBillScrollPane.getTable().getSelectedRow();
		// ̸�й�����Ŀ�����仯
		if (e.getKey().equalsIgnoreCase(AdjustWadocVO.NEGOTIATION)) {
			this.changeNegotiationWage(rowIndex);
		} else if (e.getKey().equalsIgnoreCase(AdjustWadocVO.MONEY_ADJUST)) {
			double dblMoneyOld = ((UFDouble) this.getBodyCellValue(rowIndex,
					AdjustWadocVO.MONEY_OLD)).doubleValue();
			double dblMoneyAdjust = ((UFDouble) this.getBodyCellValue(rowIndex,
					AdjustWadocVO.MONEY_ADJUST)).doubleValue();
			this.setBodyCellValue(dblMoneyAdjust - dblMoneyOld, rowIndex,
					AdjustWadocVO.CHANGE_MONEY);
		}
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
		int row = psnBillScrollPane.getTable().getSelectedRow();

		if (row > -1) {
			Object pk_wa_grd = getBodyCellValue(row, AdjustWadocVO.PK_WA_GRD);

			if (pk_wa_grd != null) {
				refreshCriterion(pk_wa_grd.toString(), row);
			}
		}
	}

	/**
	 * ���ʱ̸�й��ʣ� �򵵱𼶱�Ȳ��ɱ༭
	 * 
	 * @param rowIndex
	 */
	private void changeNegotiationWage(int rowIndex) {
		String[] neededClearKeys = {
				// ѡ��̸�й��ʺ�н������Ƿ�ҲӦ��������Ƿ����н��Ͳ����ڱ仯
				// PsnappaproveBVO.PK_WA_GRD_SHOWNAME,
				// PsnappaproveBVO.PK_WA_GRD,
				// ����н��
				AdjustWadocVO.CRT_NAME_ADJUST,
				AdjustWadocVO.GRADE_MONEY_ADJUST,
				AdjustWadocVO.PK_WA_PRMLV_ADJUST,
				AdjustWadocVO.PK_WA_SECLV_ADJUST };
		for (String strKey : neededClearKeys) {
			this.setBodyCellValue(null, rowIndex, strKey);
		}

		String[] neededClearKeys_cofom = { AdjustWadocVO.CRT_NAME_ADJUST,
				AdjustWadocVO.GRADE_MONEY_ADJUST,
				AdjustWadocVO.PK_WA_PRMLV_ADJUST,
				AdjustWadocVO.PK_WA_SECLV_ADJUST };
		for (String strKey : neededClearKeys_cofom) {
			this.setBodyCellValue(null, rowIndex, strKey);
		}
		this.setCellEditableController();
	}

	/**
	 * ���ı������
	 * 
	 * @param colKey
	 * @throws BusinessException
	 */
	private void refreshCriterion(final String strPkWaGrd, final int intRowLine) {
		String colKey = null;
		if (WaSalaryadjmgtUtility.adjustNull(strPkWaGrd)) {
			return;
		}
		colKey = AdjustWadocVO.CRT_NAME_ADJUST;

		int colKeyIndex = findColIndex(colKey);
		WaGradeVO wagradevo = null;
		try {
			wagradevo = WASalaryadjmgtDelegator.getAdjustQueryService()
					.queryWagradeVoByGradePk(strPkWaGrd);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			MessageDialog.showErrorDlg(null, null, e.getMessage());
			return;
		}
		// �Ƿ��ѡ
		isMultsecCkeck = wagradevo.getIsmultsec().booleanValue();
		// �Ƿ����н��
		isRangeCkeck = wagradevo.getIsrange().booleanValue();
		Boolean isNeg = (Boolean) getBodyCellValue(intRowLine,
				AdjustWadocVO.NEGOTIATION);
		if (isNeg != null && isNeg) {
			setBodyCellValue(UFBoolean.valueOf(false).toString(), intRowLine,
					AdjustWadocVO.IS_RANGE);
		} else {
			setBodyCellValue(UFBoolean.valueOf(isRangeCkeck).toString(),
					intRowLine, AdjustWadocVO.IS_RANGE);
		}

		UIRefPane pane = new UIRefPane() {
			private static final long serialVersionUID = -4261446741080782942L;

			@Override
			public void onButtonClicked() {
				if (strPkWaGrd != null) {
					WAGradePrmSecDia dlg = new WAGradePrmSecDia(null,
							strPkWaGrd, isMultsecCkeck, isRangeCkeck);
					dlg.showModal();
					if (dlg.getResult() == UIDialog.ID_OK) {
						changeWaCrt4Dia(intRowLine, dlg.getSelectVO());
						getBillCardPanel().stopEditing();
					}
				}
			}
		};
		pane.setEditable(true);
		pane.setEnabled(true);
		pane.addValueChangedListener(this);
		pane.setButtonFireEvent(true);
		colKeyIndex = psnBillScrollPane.getTable().convertColumnIndexToView(
				colKeyIndex);
		TableColumn tableColumn = psnBillScrollPane.getTable().getColumnModel()
				.getColumn(colKeyIndex);

		tableColumn.setCellEditor(new BillCellEditor(pane));
	}

	private int findColIndex(String keyName) {
		return billModel.getBodyColByKey(keyName);
	}

	/**
	 * н�ʱ�׼�仯
	 * 
	 * @author xuhw on 2009-12-21
	 * @param rowIndex
	 * @throws BusinessException
	 */
	private void changeWaCrt4Dia(int rowIndex, WaCriterionVO crtVO) {
		/** ���� */
		double dblMinValue = 0.0D;
		/** ���� */
		double dblMaxValue = 0.0D;
		// ���н�ʱ�׼��
		String[] clearRow = new String[] {
				// ����н��
				AdjustWadocVO.GRADE_MONEY_ADJUST, AdjustWadocVO.MONEY_ADJUST,
				AdjustWadocVO.PK_WA_SECLV_ADJUST,
				AdjustWadocVO.PK_WA_PRMLV_ADJUST,
				AdjustWadocVO.CRT_NAME_ADJUST, AdjustWadocVO.CRT_MIN_VALUE,
				AdjustWadocVO.CRT_MAX_VALUE };
		for (String strKey : clearRow) {
			setBodyCellValue(null, rowIndex, strKey);
		}

		if (crtVO == null) {
			return;
		}
		this.setBodyCellValue(crtVO.getPrmlvName(), rowIndex,
				AdjustWadocVO.CRT_NAME_ADJUST);
		this.setBodyCellValue(crtVO.getPk_wa_prmlv(), rowIndex,
				AdjustWadocVO.PK_WA_PRMLV_ADJUST);
		this.setBodyCellValue(crtVO.getPk_wa_seclv(), rowIndex,
				AdjustWadocVO.PK_WA_SECLV_ADJUST);
		this.setBodyCellValue(crtVO.getPk_wa_crt(), rowIndex,
				AdjustWadocVO.PK_WA_CRT_ADJUST);

		UFDouble strCriterionvalue = crtVO.getCriterionvalue();
		if (strCriterionvalue != null) {
			this.setBodyCellValue(strCriterionvalue.doubleValue(), rowIndex,
					AdjustWadocVO.MONEY_ADJUST);
			this.setBodyCellValue(strCriterionvalue.doubleValue(), rowIndex,
					AdjustWadocVO.GRADE_MONEY_ADJUST);

			if (batchAdjustVO.getIs_range().booleanValue()) {
				dblMinValue = crtVO.getMin_value().doubleValue();
				dblMaxValue = crtVO.getMax_value().doubleValue();
				setBodyCellValue(new UFDouble(dblMinValue), rowIndex,
						AdjustWadocVO.CRT_MIN_VALUE);
				setBodyCellValue(new UFDouble(dblMaxValue), rowIndex,
						AdjustWadocVO.CRT_MAX_VALUE);
			}
		}
		double dblMoneyOld = ((UFDouble) this.getBodyCellValue(rowIndex,
				AdjustWadocVO.MONEY_OLD)).doubleValue();
		double dblMoneyAdjust = ((UFDouble) this.getBodyCellValue(rowIndex,
				AdjustWadocVO.MONEY_ADJUST)).doubleValue();
		this.setBodyCellValue(dblMoneyAdjust - dblMoneyOld, rowIndex,
				AdjustWadocVO.CHANGE_MONEY);
	}

	/**
	 * ���õ�Ԫ���ܷ�༭
	 * 
	 * @author xuhw on 2009-12-23
	 */
	private void setCellEditableController() {
		billModel
				.setCellEditableController(new BillModelCellEditableController() {
					@Override
					public boolean isCellEditable(boolean blnIsEditable,
							int iRowIndex, String strItemKey) {
						return isBodyCellEditable(blnIsEditable, iRowIndex,
								strItemKey);
					}
				});
	}

	/**
	 * ���� ̸�й��������Ŀ�Ŀ�����
	 * 
	 * @author xuhw on 2009-12-23
	 * @param blIsEditable
	 * @param iRowIndex
	 * @param strItemKey
	 * @return
	 */
	private boolean isBodyCellEditable(boolean blIsEditable, int iRowIndex,
			String strItemKey) {
		Boolean blnNegotiation = false;
		Object object = billModel.getValueAt(iRowIndex,
				AdjustWadocVO.NEGOTIATION);
		if (object != null) {
			blnNegotiation = (Boolean) object;
		}
		if (AdjustWadocVO.CRT_NAME_ADJUST.equals(strItemKey)) {
			return !blnNegotiation;
		}
		if (AdjustWadocVO.MONEY_ADJUST.equals(strItemKey)) {
			if (!isRangeCkeck) {
				return blnNegotiation;
			}
		}
		return blIsEditable;
	}

	/**
	 * ���������ֶν��и�ֵ
	 * 
	 * @param aValue
	 * @param rowIndex
	 * @param strKey
	 */
	private void setBodyCellValue(Object aValue, int rowIndex, String strKey) {
		billModel.setValueAt(aValue, rowIndex,
				billModel.getBodyColByKey(strKey));
	}

	/**
	 * �õ������е��ֶ�ֵ
	 */
	private Object getBodyCellValue(int rowIndex, String strKey) {
		return billModel.getValueAt(rowIndex, strKey);
	}

	public BatchAdjustWizardModel getWizardModel() {
		return wizardModel;
	}

	public void setWizardModel(BatchAdjustWizardModel wizardModel) {
		this.wizardModel = wizardModel;
	}

	public BatchAdjustVO getBatchAdjustVO() {
		return batchAdjustVO;
	}

	public void setBatchAdjustVO(BatchAdjustVO batchAdjustVO) {
		this.batchAdjustVO = batchAdjustVO;
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {

	}

	private UFBoolean getPartflagShow() {
		if (partflagShow == null) {
			partflagShow = WaAdjustParaTool.getPartjob_Adjmgt(wizardModel
					.getLoginContext().getPk_group());
		}
		return partflagShow;
	}
}