package nc.ui.wa.adjust.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hr.pf.IHrPf;
import nc.itf.hr.wa.ISalaryadjmgtConstant;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pubitf.para.SysInitQuery;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.IRefConst;
import nc.ui.bd.ref.UFRefGridTreeUI;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.hr.util.TableColResize;
import nc.ui.pub.beans.IBeforeRefDlgShow;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.table.ColumnGroup;
import nc.ui.pub.beans.table.GroupableTableHeader;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillModelCellEditableController;
import nc.ui.pub.bill.BillTableCellRenderer;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.ui.pub.style.Style;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.ITabbedPaneAwareComponent;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.wa.adjust.model.WaAdjustAppModel;
import nc.ui.wa.ref.WAGradePrmSecDia;
import nc.ui.wa.ref.WaGradeRefModel;
import nc.ui.wa.ref.WaPrmSecRefModel;
import nc.ui.wa.salaryadjmgt.WASalaryadjmgtDelegator;
import nc.ui.wa.salaryadjmgt.WaSalaryadjmgtUtility;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.wa.adjust.AdjustWadocVO;
import nc.vo.wa.adjust.BatchAdjustVO;
import nc.vo.wa.adjust.PsnappaproveBVO;
import nc.vo.wa.adjust.PsnappaproveVO;
import nc.vo.wa.adjust.WaAdjustParaTool;
import nc.vo.wa.grade.WaCriterionVO;
import nc.vo.wa.grade.WaGradeVO;
import nc.vo.wa.item.WaItemVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * ������ ��Ƭ����
 * 
 * @author: xuhw
 * @date: 2009-12-17 ����12:56:56
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
@SuppressWarnings("serial")
public class WaAdjustCardForm extends HrBillFormEditor implements
		ITabbedPaneAwareComponent, BillEditListener, ValueChangedListener,
		BillCardBeforeEditListener, BillEditListener2 {
	/** ������ model */
	private WaAdjustAppModel appmodel;
	/** �������������ť */
	private List<NCAction> approveTabActions;
	private BillModel billModel;
	private UIRefPane refpane = null;
	/** ���� */
	double dblMaxValue = 0.0D;
	/** ���� */
	double dblMinValue = 0.0D;
	private List<String[]> headlist = null;
	/** �Ƿ�������ڵ� */
	private boolean isInputPnl = true;
	/** �Ƿ��ѡ */
	boolean isMultsecCkeck = false;
	/** �Ƿ����н�� */
	boolean isRangeCkeck = false;

	/** н����ĿPK */
	private String itempk;
	String strCrt = null;
	String strMoney = null;
	String strPkWaGrdShowname = null;
	private WaCriterionVO selectVO;
	private UICheckBox partflagBox;

	private UFBoolean partflagShow = null;

	private String applyShowName = null;
	private String confirmShowName = null;
	private String pk_wa_item = null;
	private String pk_wa_grd = null;

	/**
	 * ����BO
	 * 
	 * @param psnappaproveBVOs
	 */
	public void addBodyVos(PsnappaproveBVO[] psnappaproveBVOs) {
		if (psnappaproveBVOs == null) {
			return;
		}
		for (PsnappaproveBVO psnappaproveBVO : psnappaproveBVOs) {
			this.getBillCardPanel().getBodyPanel().getTableModel().addLine();
			int numberOfRow = this.billModel.getRowCount();
			this.billModel.setBodyRowVO(psnappaproveBVO, numberOfRow - 1);
			UIRefPane refpane = (UIRefPane) getBillCardPanel().getBodyItem(
					AdjustWadocVO.PK_WA_ITEM_SHOWNAME).getComponent();
			refpane.setPK(psnappaproveBVO.getPk_wa_item());
			billModel.setValueAt(psnappaproveBVO.getClerkcode(),
					numberOfRow - 1, PsnappaproveBVO.PK_PSNJOB);
			// setBodyCellValue(psnappaproveBVO.getClerkcode(), numberOfRow - 1,
			// PsnappaproveBVO.PK_PSNJOB);
			setBodyCellValue(psnappaproveBVO.getPsnname(), numberOfRow - 1,
					"pk_psnjob.pk_psndoc.name");
			setBodyCellValue(psnappaproveBVO.getDeptName(), numberOfRow - 1,
					"pk_psnjob.pk_dept.name");
			setBodyCellValue(psnappaproveBVO.getPsnclname(), numberOfRow - 1,
					"pk_psnjob.pk_psncl.name");
			setBodyCellValue(psnappaproveBVO.getPostname(), numberOfRow - 1,
					"pk_psnjob.pk_post.postname");
			setBodyCellValue(psnappaproveBVO.getPostseriesname(),
					numberOfRow - 1, "pk_psnjob.pk_postseries.postseriesname");
			setBodyCellValue(psnappaproveBVO.getJobname(), numberOfRow - 1,
					"pk_psnjob.pk_job.jobname");
			if (psnappaproveBVO.getUsedate() == null) {
				setBodyCellValue(
						this.getBillCardPanel()
								.getHeadItem(PsnappaproveVO.USEDATE)
								.getValueObject(), numberOfRow - 1,
						PsnappaproveBVO.USEDATE);
			} else {
				setBodyCellValue(psnappaproveBVO.getUsedate(), numberOfRow - 1,
						PsnappaproveBVO.USEDATE);
			}
		}
		TableColResize.reSizeTable(this.getBillCardPanel().getBodyPanel());
	}

	/**
	 * ����С��λ������ ʹÿ�е�С��λ����н����Ŀ����һ��
	 */
	private void addDecimalListener() {
		String[] billitems = new String[] { PsnappaproveBVO.WA_CRT_OLD_MONEY,
				PsnappaproveBVO.WA_OLD_MONEY,
				PsnappaproveBVO.WA_CRT_APPLY_MONEY,
				PsnappaproveBVO.WA_APPLY_MONEY,
				PsnappaproveBVO.WA_CRT_COFM_MONEY,
				PsnappaproveBVO.WA_COFM_MONEY };
		String pk_group = getModel().getContext().getPk_group();
		IBillModelDecimalListener2 bmd = new WaItemDecimalAdapter(
				WaItemVO.PK_WA_ITEM, billitems, pk_group);
		getBillCardPanel().getBillModel().addDecimalListener(bmd);

	}

	/**
	 * н���յ���
	 * 
	 * @param adjustVO
	 * @param batchAdjustVO
	 * @throws BusinessException
	 */
	public void adjustBatchProcesser(AdjustWadocVO[] adjustVOs,
			BatchAdjustVO batchAdjustVO, PsnappaproveBVO[] psnappaproveBVOs)
			throws BusinessException {
		if (ArrayUtils.isEmpty(adjustVOs)) {
			return;
		}
		String strPKPsndoc = null;
		Integer assgid = null;
		psnappaproveBVOs = filterAdjustPsns(psnappaproveBVOs, adjustVOs);

		int numberOfRow = psnappaproveBVOs.length;
		if (numberOfRow > 0) {

			Map<String, AdjustWadocVO> hashMap = new HashMap<String, AdjustWadocVO>();
			for (AdjustWadocVO adjustVO : adjustVOs) {

				strPKPsndoc = adjustVO.getPk_psndoc();
				assgid = adjustVO.getAssgid() == null ? Integer.valueOf(1)
						: adjustVO.getAssgid();
				hashMap.put(strPKPsndoc + assgid.toString(), adjustVO);
			}

			for (PsnappaproveBVO psnappaproveBVO2 : psnappaproveBVOs) {
				Integer assgid2 = psnappaproveBVO2.getAssgid() == null ? Integer
						.valueOf(1) : psnappaproveBVO2.getAssgid();
				String key = psnappaproveBVO2.getPk_psndoc()
						+ assgid2.toString();
				if (hashMap.get(key) == null) {
					continue;
				}

				// ������ wa_apply_money
				psnappaproveBVO2.setWa_apply_money(hashMap.get(key)
						.getMoney_adjust());
				// // ���뼶������ pk_wa_crt_apply
				// psnappaproveBVO2.setPk_wa_crt_apply(adjustVO.getPk_wa_crt_adjust());
				// pk_wa_prmlv_apply
				psnappaproveBVO2.setPk_wa_prmlv_apply(hashMap.get(key)
						.getPk_wa_prmlv());
				// pk_wa_seclv_apply
				psnappaproveBVO2.setPk_wa_seclv_apply(hashMap.get(key)
						.getPk_wa_seclv());
				// wa_seclv_apply
				psnappaproveBVO2.setPk_wa_crt_apply_showname(hashMap.get(key)
						.getCrt_name_adjust());
				// wa_crt_apply_money
				psnappaproveBVO2.setWa_crt_apply_money(hashMap.get(key)
						.getGrade_money_adjust());
				psnappaproveBVO2.setCrt_min_value(hashMap.get(key)
						.getCrt_min_value());
				psnappaproveBVO2.setCrt_max_value(hashMap.get(key)
						.getCrt_max_value());
				psnappaproveBVO2.setIs_range(hashMap.get(key).getIs_range()
						.toString());
				psnappaproveBVO2.setIsMultiSec(hashMap.get(key)
						.getIs_multsecckeck());
				psnappaproveBVO2.setNegotiation(hashMap.get(key)
						.getNegotiation());
				psnappaproveBVO2.setPk_wa_crt(hashMap.get(key).getPk_wa_crt());
			}
			getBillCardPanel().getBillModel().setBodyDataVO(psnappaproveBVOs);
			getBillCardPanel().getBillModel().loadLoadRelationItemValue();
			setHeadCellValue(batchAdjustVO.getStart_date(),
					PsnappaproveVO.USEDATE);
			// setHeadCellValue(batchAdjustVO.getPk_changecause(),
			// PsnappaproveVO.PK_CHANGECAUSE);
		}
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		int rowIndex = this.getBillCardPanel().getBodyPanel().getTable()
				.getSelectedRow();

		// ̸�й�����Ŀ�����仯
		if (e.getKey().equalsIgnoreCase(PsnappaproveBVO.NEGOTIATION)) {
			this.changeNegotiationWage(rowIndex);
			Object isNegotiation = getBillCardPanel().getBodyPanel()
					.getTableModel()
					.getValueAt(rowIndex, PsnappaproveBVO.NEGOTIATION);
			if (isNegotiation == null || !(Boolean) isNegotiation) {
				return;
			}
			try {
				Object objPsn = getBillCardPanel().getBodyPanel()
						.getTableModel()
						.getValueAt(rowIndex, PsnappaproveBVO.PK_PSNDOC);
				Object objItem = getBillCardPanel().getBodyPanel()
						.getTableModel()
						.getValueAt(rowIndex, PsnappaproveBVO.PK_WA_ITEM);
				if (objPsn != null && objItem != null) {
					PsndocWadocVO vo = WASalaryadjmgtDelegator
							.getPsndocWadocQueryService().queryLastNMoney(
									(String) objPsn, (String) objItem);
					if (vo != null) {
						getBillCardPanel()
								.getBodyPanel()
								.getTableModel()
								.setValueAt(vo.getNmoney(), rowIndex,
										PsnappaproveBVO.WA_OLD_MONEY);
					}
				}
			} catch (Exception e2) {
				Logger.error(e2.getMessage(), e2);
			}
		}

		// ����н�ʱ�׼�����仯
		else if (e.getKey().equalsIgnoreCase(
				PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME)) {
			this.refpane = (UIRefPane) ((BillCellEditor) e.getSource())
					.getComponent();
			this.refpane.setAutoCheck(false);
			this.setBodyCellValue(this.applyShowName, rowIndex,
					PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME);
			this.changeCrtApply(rowIndex);
		}
		// ȷ��н�ʱ�׼�����仯
		else if (e.getKey().equalsIgnoreCase(
				PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME)) {
			this.refpane = (UIRefPane) ((BillCellEditor) e.getSource())
					.getComponent();
			this.setBodyCellValue(this.confirmShowName, rowIndex,
					PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME);
			this.changeCrtApprove(rowIndex);
		}
		// н����Ŀ�����仯
		else if (e.getKey().equalsIgnoreCase(
				PsnappaproveBVO.PK_WA_ITEM_SHOWNAME)) {
			this.refpane = (UIRefPane) ((BillCellEditor) e.getSource())
					.getComponent();
			this.changeWaItem(rowIndex);
		}
		// н��������仯
		else if (e.getKey()
				.equalsIgnoreCase(PsnappaproveBVO.PK_WA_GRD_SHOWNAME)) {
			this.refpane = (UIRefPane) ((BillCellEditor) e.getSource())
					.getComponent();
			this.changeWaGrd(rowIndex);

			// 20140728 shenliangc Ϊ����������������뵥����һ�У�ѡ��н�ʱ�׼���֮��
			// н�ʱ�׼���մ򲻿������⣬��bodyRowChange()�����帴�ƹ������߼���
			// н����Ŀ
			Object objPkWaItem = this.billModel.getValueAt(rowIndex,
					PsnappaproveBVO.PK_WA_ITEM);
			if (objPkWaItem != null) {
				this.itempk = objPkWaItem.toString();
				// н�ʱ�׼
				this.refreshGradeRef(PsnappaproveBVO.PK_WA_GRD_SHOWNAME,
						new WaGradeRefModel(itempk, this.getModel()
								.getContext().getPk_org()));

				Object o_pk_wa_grd = this.billModel.getValueAt(rowIndex,
						PsnappaproveBVO.PK_WA_GRD);
				if (!WaSalaryadjmgtUtility.adjustNull(o_pk_wa_grd)) {
					String pk_wa_grd = o_pk_wa_grd.toString();
					this.refreshCriterion(pk_wa_grd, rowIndex);
				}
			}

		}
		// ��Ա���뷢���仯
		else if (e.getKey().equalsIgnoreCase(PsnappaproveBVO.PK_PSNJOB)) {
			this.refpane = (UIRefPane) ((BillCellEditor) e.getSource())
					.getComponent();

			this.changePsncode(rowIndex);
		} else if (e.getKey().equalsIgnoreCase(PsnappaproveVO.TRANSTYPEID)) {
			// �趨��������PK
			setHeadItemValue(PsnappaproveVO.TRANSTYPEID,
					((UIRefPane) e.getSource()).getRefPK());

			// �趨�������� code��code�������ǡ�6301�� ��Ҫ���ݽ������͸ı�
			setHeadItemValue(PsnappaproveVO.TRANSTYPE,
					((UIRefPane) e.getSource()).getRefCode());
		}
	}

	@Override
	public boolean beforeEdit(BillEditEvent e) {
		int rowIndex = this.getBillCardPanel().getBodyPanel().getTable()
				.getSelectedRow();
		if ((DefaultConstEnum) getBillCardPanel().getBillTable().getModel()
				.getValueAt(rowIndex, 17) != null) {
			this.applyShowName = (String) ((DefaultConstEnum) getBillCardPanel()
					.getBillTable().getModel().getValueAt(rowIndex, 17))
					.getValue();
		}
		if ((DefaultConstEnum) getBillCardPanel().getBillTable().getModel()
				.getValueAt(rowIndex, 21) != null) {
			this.confirmShowName = (String) ((DefaultConstEnum) getBillCardPanel()
					.getBillTable().getModel().getValueAt(rowIndex, 21))
					.getValue();
		}

		if ((DefaultConstEnum) getBillCardPanel().getBillTable().getModel()
				.getValueAt(rowIndex, 28) != null) {
			this.pk_wa_item = (String) ((DefaultConstEnum) getBillCardPanel()
					.getBillTable().getModel().getValueAt(rowIndex, 28))
					.getValue();
		}
		if ((DefaultConstEnum) getBillCardPanel().getBillTable().getModel()
				.getValueAt(rowIndex, 40) != null) {
			this.pk_wa_grd = (String) ((DefaultConstEnum) getBillCardPanel()
					.getBillTable().getModel().getValueAt(rowIndex, 40))
					.getValue();
		}

		if (PsnappaproveBVO.PK_PSNJOB.equals(e.getKey())) {
			// ������
			UIRefPane refPane = (UIRefPane) getBillCardPanel().getBodyItem(
					PsnappaproveBVO.PK_PSNJOB).getComponent();
			// refPane.add(getChkSealedDataShow() );
			/*
			 * refPane.setDisabledDataButtonShow(true);
			 * refPane.setSealedDataButtonShow(true);
			 * refPane.setIncludeSubShow(true); refPane.repaint();
			 */
			if (!getPartflagShow().booleanValue()) {
				refPane.getRefModel().setWherePart(
						"  hi_psnjob.ismainjob = 'Y'", true);
			} else {
				refPane.setIBeforeRefDlgShow(new IBeforeRefDlgShow() {

					@Override
					public AbstractButton[] addButtons(UIDialog dlg) {

						return new AbstractButton[] { getPartflagBox() };
					}

				});

				refPane.getRefModel().setWherePart(
						"  hi_psnjob.ismainjob = '"
								+ (getPartflagBox().isSelected() ? 'N' : 'Y')
								+ "'", true);
			}
		} else if (PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME.equals(e.getKey())) {
			// 2015-11-27 NCdp205550499 zhousze ���û��ѡ��н�ʱ�׼�����ôн�ʱ�׼�Ͳ��ɱ༭ begin
			String itempkNow = (String) getBodyCellValue(rowIndex,
					PsnappaproveBVO.PK_WA_GRD);
			if (itempkNow != null) {
				this.setBodyCellValue(this.applyShowName, rowIndex,
						PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME);
			} else {
				getBillCardPanel().getBodyTabbedPane().setEnabledAt(rowIndex,
						false);
				// end
			}
		} else if (PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME.equals(e.getKey())) {
			this.setBodyCellValue(this.confirmShowName, rowIndex,
					PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME);
		} else if (PsnappaproveBVO.PK_WA_ITEM_SHOWNAME.equals(e.getKey())) {
			UIRefPane refPane = (UIRefPane) getBillCardPanel().getBodyItem(
					PsnappaproveBVO.PK_WA_ITEM_SHOWNAME).getComponent();
			refPane.setPK(this.pk_wa_item);
		} else if (PsnappaproveBVO.PK_WA_GRD_SHOWNAME.equals(e.getKey())) {
			// 2015-11-27 NCdp205550499 zhousze ���û��ѡ��н����Ŀ����ôн�ʱ�׼���Ͳ��ɱ༭ begin
			String itempkNow = (String) getBodyCellValue(rowIndex,
					PsnappaproveBVO.PK_WA_ITEM);
			if (itempkNow != null) {
				UIRefPane refPane = (UIRefPane) getBillCardPanel().getBodyItem(
						PsnappaproveBVO.PK_WA_GRD_SHOWNAME).getComponent();
				refPane.setPK(this.pk_wa_grd);
			} else {
				getBillCardPanel().getBodyTabbedPane().setEnabledAt(rowIndex,
						false);
			}
		}
		return true;
	}

	@Override
	public boolean beforeEdit(BillItemEvent e) {
		if (PsnappaproveVO.TRANSTYPEID.equals(e.getItem().getKey())) {
			// ������
			((UIRefPane) e.getItem().getComponent()).getRefModel()
					.addWherePart(
							" and ( pk_billtypecode = '"
									+ ISalaryadjmgtConstant.BillTYPE_ENTRY
									+ "' or ( parentbilltype = '"
									+ ISalaryadjmgtConstant.BillTYPE_ENTRY
									+ "' and pk_group = '"
									+ getModel().getContext().getPk_group()
									+ "' )) ");
			((UIRefPane) e.getItem().getComponent()).getRefModel().reloadData();
		}

		return true;
	}

	@Override
	/*
	 * * �������ʱ��Ҫ���²��պͼ��ɱ༭����
	 */
	public void bodyRowChange(BillEditEvent billEditEvent) {
		int rowIndex = billEditEvent.getRow();
		// н����Ŀ
		Object objPkWaItem = this.billModel.getValueAt(rowIndex,
				PsnappaproveBVO.PK_WA_ITEM);
		if (objPkWaItem != null) {
			this.itempk = objPkWaItem.toString();
			// н�ʱ�׼
			this.refreshGradeRef(PsnappaproveBVO.PK_WA_GRD_SHOWNAME,
					new WaGradeRefModel(itempk, this.getModel().getContext()
							.getPk_org()));

			Object o_pk_wa_grd = this.billModel.getValueAt(rowIndex,
					PsnappaproveBVO.PK_WA_GRD);
			if (!WaSalaryadjmgtUtility.adjustNull(o_pk_wa_grd)) {
				String pk_wa_grd = o_pk_wa_grd.toString();
				this.refreshCriterion(pk_wa_grd, rowIndex);
			}
		}
	}

	@Override
	public boolean canBeHidden() {
		if (getModel().getUiState() == UIState.ADD
				|| getModel().getUiState() == UIState.EDIT) {
			return false;
		}
		return super.canBeHidden();
	}

	/**
	 * �����޸ı���VO
	 * 
	 * @param psnappaproveBVO
	 * @throws BusinessException
	 */
	public void changeBodyVo() throws BusinessException {
		int intNumberOfRow = this.billModel.getRowCount();
		if (intNumberOfRow <= 0) {
			return;
		}

		getBillCardPanel().stopEditing();
		PsnappaproveBVO[] psnappaproveBVOs = (PsnappaproveBVO[]) this.billModel
				.getBodyValueVOs(PsnappaproveBVO.class.getName());

		this.appmodel.batchEditProcesser(psnappaproveBVOs);

		String[] allAttributeNames = PsnappaproveBVO.getAllUpdateAttrNames();
		// ���Ƶ�����
		for (int i = 0; i < intNumberOfRow; i++) {
			for (String name : allAttributeNames) {
				this.setBodyCellValue(
						psnappaproveBVOs[i].getAttributeValue(name), i,
						billModel.getBodyColByKey(name));
			}
			int rowState = this.billModel.getRowState(i);
			if (rowState != BillModel.ADD) {
				this.billModel.getRowAttribute(i).setRowState(
						BillModel.MODIFICATION);
			}
		}
	}

	/**
	 * �����޸ı���VO
	 * 
	 * @param psnappaproveBVO
	 * @throws BusinessException
	 */
	public void changeBodyVo1() throws BusinessException {
		int intNumberOfRow = this.billModel.getRowCount();
		if (intNumberOfRow <= 0) {
			return;
		}

		getBillCardPanel().stopEditing();
		PsnappaproveBVO[] psnappaproveBVOs = (PsnappaproveBVO[]) this.billModel
				.getBodyValueVOs(PsnappaproveBVO.class.getName());

		this.appmodel.batchEditProcesser1(psnappaproveBVOs);

		String[] allAttributeNames = PsnappaproveBVO.getAllUpdateAttrNames();
		// ���Ƶ�����
		for (int i = 0; i < intNumberOfRow; i++) {
			for (String name : allAttributeNames) {
				this.setBodyCellValue(
						psnappaproveBVOs[i].getAttributeValue(name), i, name);
			}
			int rowState = this.billModel.getRowState(i);
			if (rowState != BillModel.ADD) {
				this.billModel.getRowAttribute(i).setRowState(
						BillModel.MODIFICATION);
			}
		}
	}

	/**
	 * н�ʱ�׼�仯
	 * 
	 * @author xuhw on 2009-12-21
	 * @param rowIndex
	 * @throws BusinessException
	 */
	private void changeCrtApply(int rowIndex) {
		// // ���н�ʱ�׼��
		// String[] clearRow = new String[]
		// {
		// // ����н��
		// PsnappaproveBVO.WA_APPLY_MONEY, PsnappaproveBVO.WA_CRT_APPLY_MONEY,
		// PsnappaproveBVO.PK_WA_CRT_APPLY,
		// // PsnappaproveBVO.IS_RANGE,
		// PsnappaproveBVO.CRT_MIN_VALUE, PsnappaproveBVO.CRT_MAX_VALUE,
		// PsnappaproveBVO.VNOTE };
		// for (String strKey : clearRow)
		// {
		// this.setBodyCellValue(null, rowIndex, strKey);
		// }
		//
		// // UIRefPane applyCrtRef = (UIRefPane)
		// // this.billCellEditor.getComponent();
		// this.setBodyCellValue(refpane.getRefPK(), rowIndex,
		// PsnappaproveBVO.PK_WA_CRT_APPLY);
		// UFDouble strCriterionvalue = (UFDouble)
		// refpane.getRefModel().getValue(WaCriterionVO.CRITERIONVALUE);
		// if (strCriterionvalue != null)
		// {
		// this.setBodyCellValue(strCriterionvalue.doubleValue(), rowIndex,
		// PsnappaproveBVO.WA_APPLY_MONEY);
		// this.setBodyCellValue(strCriterionvalue.doubleValue(), rowIndex,
		// PsnappaproveBVO.WA_CRT_APPLY_MONEY);
		//
		// if (this.isRangeCkeck)
		// {
		// this.dblMinValue = ((UFDouble)
		// refpane.getRefModel().getValue(WaCriterionVO.MIN_VALUE)).doubleValue();
		// this.dblMaxValue = ((UFDouble)
		// refpane.getRefModel().getValue(WaCriterionVO.MAX_VALUE)).doubleValue();
		// this.setBodyCellValue(new UFDouble(this.dblMinValue), rowIndex,
		// PsnappaproveBVO.CRT_MIN_VALUE);
		// this.setBodyCellValue(new UFDouble(this.dblMaxValue), rowIndex,
		// PsnappaproveBVO.CRT_MAX_VALUE);
		// }
		// }

		if (selectVO != null) {
			changeCrtApply4Dia(rowIndex, selectVO);
			selectVO = null;
		}
	}

	/**
	 * н�ʱ�׼�仯
	 * 
	 * @author xuhw on 2009-12-21
	 * @param rowIndex
	 * @throws BusinessException
	 */
	private void changeCrtApply4Dia(int rowIndex, WaCriterionVO crtVO) {
		// ���н�ʱ�׼��
		String[] clearRow = new String[] {
				// ����н��
				PsnappaproveBVO.WA_APPLY_MONEY,
				PsnappaproveBVO.WA_CRT_APPLY_MONEY,
				PsnappaproveBVO.PK_WA_PRMLV_APPLY,
				PsnappaproveBVO.PK_WA_SECLV_APPLY,
				// PsnappaproveBVO.IS_RANGE,
				PsnappaproveBVO.CRT_MIN_VALUE, PsnappaproveBVO.CRT_MAX_VALUE,
				PsnappaproveBVO.VNOTE, PsnappaproveBVO.PK_WA_CRT };
		for (String strKey : clearRow) {
			this.setBodyCellValue(null, rowIndex, strKey);
		}

		if (crtVO == null) {
			return;
		}
		// UIRefPane applyCrtRef = (UIRefPane)
		// this.billCellEditor.getComponent();
		this.setBodyCellValue(crtVO.getPk_wa_crt(), rowIndex,
				PsnappaproveBVO.PK_WA_CRT);
		this.setBodyCellValue(crtVO.getPk_wa_prmlv(), rowIndex,
				PsnappaproveBVO.PK_WA_PRMLV_APPLY);
		this.setBodyCellValue(crtVO.getPk_wa_seclv(), rowIndex,
				PsnappaproveBVO.PK_WA_SECLV_APPLY);
		this.setBodyCellValue(crtVO.getPrmlvName(), rowIndex,
				PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME);
		UFDouble strCriterionvalue = crtVO.getCriterionvalue();
		if (strCriterionvalue != null) {
			this.setBodyCellValue(strCriterionvalue.doubleValue(), rowIndex,
					PsnappaproveBVO.WA_APPLY_MONEY);
			this.setBodyCellValue(strCriterionvalue.doubleValue(), rowIndex,
					PsnappaproveBVO.WA_CRT_APPLY_MONEY);

			if (this.isRangeCkeck) {
				this.dblMinValue = crtVO.getMin_value().doubleValue();
				this.dblMaxValue = crtVO.getMax_value().doubleValue();
				this.setBodyCellValue(new UFDouble(this.dblMinValue), rowIndex,
						PsnappaproveBVO.CRT_MIN_VALUE);
				this.setBodyCellValue(new UFDouble(this.dblMaxValue), rowIndex,
						PsnappaproveBVO.CRT_MAX_VALUE);
			}
		}

		getBillCardPanel().getBillModel().getValueAt(0, 42);
	}

	/**
	 * н�ʱ�׼�仯 ����
	 * 
	 * @author xuhw on 2009-12-21
	 * @param rowIndex
	 * @throws BusinessException
	 */
	private void changeCrtApprove(int rowIndex) {
		// // ���н�ʱ�׼��
		// String[] clearRow = new String[]
		// {
		// // ȷ��н��
		// PsnappaproveBVO.WA_COFM_MONEY, PsnappaproveBVO.COFM_GRADE_MONEY,
		// PsnappaproveBVO.PK_WA_CRT_COFM,
		// PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME,
		// // PsnappaproveBVO.IS_RANGE,
		// PsnappaproveBVO.CRT_MIN_VALUE, PsnappaproveBVO.CRT_MAX_VALUE, };
		// for (String strKey : clearRow)
		// {
		// this.setBodyCellValue(null, rowIndex, strKey);
		// }
		//
		// // UIRefPane applyCrtRef = (UIRefPane)
		// // this.billCellEditor.getComponent();
		// this.setBodyCellValue(refpane.getRefName(), rowIndex,
		// PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME);
		// this.setBodyCellValue(refpane.getRefPK(), rowIndex,
		// PsnappaproveBVO.PK_WA_CRT_COFM);
		// UFDouble strCriterionvalue = (UFDouble)
		// refpane.getRefModel().getValue(WaCriterionVO.CRITERIONVALUE);
		// if (strCriterionvalue != null)
		// {
		// this.setBodyCellValue(strCriterionvalue.doubleValue(), rowIndex,
		// PsnappaproveBVO.WA_COFM_MONEY);
		// this.setBodyCellValue(strCriterionvalue.doubleValue(), rowIndex,
		// PsnappaproveBVO.COFM_GRADE_MONEY);
		//
		// if (this.isRangeCkeck)
		// {
		// this.dblMinValue = ((UFDouble)
		// refpane.getRefModel().getValue(WaCriterionVO.MIN_VALUE)).doubleValue();
		// this.dblMaxValue = ((UFDouble)
		// refpane.getRefModel().getValue(WaCriterionVO.MAX_VALUE)).doubleValue();
		// this.setBodyCellValue(new UFDouble(this.dblMinValue), rowIndex,
		// PsnappaproveBVO.CRT_MIN_VALUE);
		// this.setBodyCellValue(new UFDouble(this.dblMaxValue), rowIndex,
		// PsnappaproveBVO.CRT_MAX_VALUE);
		// }
		// }

		if (selectVO == null) {
			return;
		}
		changeCrtApprove4Dia(rowIndex, selectVO);
	}

	/**
	 * н�ʱ�׼�仯 ����
	 * 
	 * @author xuhw on 2009-12-21
	 * @param rowIndex
	 * @throws BusinessException
	 */
	private void changeCrtApprove4Dia(int rowIndex, WaCriterionVO crtVO) {
		// ���н�ʱ�׼��
		String[] clearRow = new String[] {
				// ȷ��н��
				PsnappaproveBVO.WA_COFM_MONEY,
				PsnappaproveBVO.WA_CRT_COFM_MONEY,
				PsnappaproveBVO.PK_WA_PRMLV_COFM,
				PsnappaproveBVO.PK_WA_SECLV_COFM,
				PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME,
				// PsnappaproveBVO.IS_RANGE,
				PsnappaproveBVO.CRT_MIN_VALUE, PsnappaproveBVO.CRT_MAX_VALUE,
				PsnappaproveBVO.PK_WA_CRT };
		for (String strKey : clearRow) {
			this.setBodyCellValue(null, rowIndex, strKey);
		}

		if (crtVO == null) {
			return;
		}
		// UIRefPane applyCrtRef = (UIRefPane)
		// this.billCellEditor.getComponent();
		this.setBodyCellValue(crtVO.getPrmlvName(), rowIndex,
				PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME);
		this.setBodyCellValue(crtVO.getPk_wa_crt(), rowIndex,
				PsnappaproveBVO.PK_WA_CRT);
		setBodyCellValue(crtVO.getPk_wa_prmlv(), rowIndex,
				PsnappaproveBVO.PK_WA_PRMLV_COFM);
		setBodyCellValue(crtVO.getPk_wa_seclv(), rowIndex,
				PsnappaproveBVO.PK_WA_SECLV_COFM);
		UFDouble strCriterionvalue = crtVO.getCriterionvalue();
		if (strCriterionvalue != null) {
			this.setBodyCellValue(strCriterionvalue.doubleValue(), rowIndex,
					PsnappaproveBVO.WA_COFM_MONEY);
			this.setBodyCellValue(strCriterionvalue.doubleValue(), rowIndex,
					PsnappaproveBVO.WA_CRT_COFM_MONEY);

			if (this.isRangeCkeck) {
				this.dblMinValue = crtVO.getMin_value().doubleValue();
				this.dblMaxValue = crtVO.getMax_value().doubleValue();
				this.setBodyCellValue(new UFDouble(this.dblMinValue), rowIndex,
						PsnappaproveBVO.CRT_MIN_VALUE);
				this.setBodyCellValue(new UFDouble(this.dblMaxValue), rowIndex,
						PsnappaproveBVO.CRT_MAX_VALUE);
			}
		}
	}

	/**
	 * ���ʱ̸�й��ʣ� �򵵱𼶱�Ȳ��ɱ༭
	 * 
	 * @param rowIndex
	 */
	private void changeNegotiationWage(int rowIndex) {
		/* ���н�ʱ�׼�����ʾ���ƺ�н�ʱ�׼���pk */
		String[] neededClear = { PsnappaproveBVO.PK_WA_GRD,
				PsnappaproveBVO.PK_WA_GRD_SHOWNAME };
		for (String strKey : neededClear) {
			this.setBodyCellValue(null, rowIndex, strKey);
		}

		String[] neededClearKeys = {
				// ѡ��̸�й��ʺ�н������Ƿ�ҲӦ��������Ƿ����н��Ͳ����ڱ仯
				// PsnappaproveBVO.PK_WA_GRD_SHOWNAME,
				// PsnappaproveBVO.PK_WA_GRD,
				// ����н��
				PsnappaproveBVO.PK_WA_CRT_OLD_SHOWNAME,
				PsnappaproveBVO.WA_CRT_OLD_MONEY, PsnappaproveBVO.WA_OLD_MONEY,
				PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME,
				PsnappaproveBVO.WA_CRT_APPLY_MONEY,
				PsnappaproveBVO.WA_APPLY_MONEY,
				PsnappaproveBVO.PK_WA_PRMLV_APPLY,
				PsnappaproveBVO.PK_WA_SECLV_APPLY };
		for (String strKey : neededClearKeys) {
			this.setBodyCellValue(null, rowIndex, strKey);
		}

		/**
		 * ���������뵥����ԱtestA���ü��𡢵����ύ���ݺ������ڵ��޸ı��浥�ݣ�
		 * ����н��Ĭ�ϴ���������ļ��𡢵����Ȼ��������ڵ��ջص��ݣ��޸�����н��Ϊ̸�й��ʣ�
		 * �ٴ��ύ���ݡ������ڵ������н������ʾδ�޸�ǰ�Ĵ����ļ��𡢵����Ҳ����޸ģ��õ�������ͨ����
		 * �ڶ�������Ϣά���ڵ��ѯʱ����¼̸�й���ΪY�����𡢵���Ҳ��ʾ�����ݡ��絥�ݣ�XC0711070002
		 */
		String[] neededClearKeys_cofom =
		// TODO Ϊʲô�������ʱ�����ȷ�ϵĽ��
		{ PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME,
				PsnappaproveBVO.WA_CRT_COFM_MONEY,
				PsnappaproveBVO.PK_WA_SECLV_COFM,
				PsnappaproveBVO.PK_WA_PRMLV_COFM };
		for (String strKey : neededClearKeys_cofom) {
			this.setBodyCellValue(null, rowIndex, strKey);
		}
		this.setCellEditableController();
	}

	/**
	 * ��Ա�����仯ʱ
	 * 
	 * @param rowIndex
	 */
	private void changePsncode(int rowIndex) {
		/*
		 * String clerkcode = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.CLERKCODE); String
		 * psncode = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.PSN_CODE); // TODO
		 * �����Ժ����Ҫ��Ϊͨ�õģ������ValuecodeҲҪ�� String psnname = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.PSN_NAME); // �趨���� String
		 * deptname = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.DEPT_NAME); // �趨��λ
		 * String psotname = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.POST_NAME); // �趨��Ա���
		 * String psnpcl = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.PK_PSNCL); // �趨��λ����
		 * String jobName = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.JOB_NAME); // �趨ְ�� String
		 * postseries = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.POSTSERIES);
		 * 
		 * String pk_psndoc = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.PK_PSNDOC); String
		 * pk_psnjob = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.PK_PSNJOB);
		 * 
		 * setBodyCellValue(clerkcode, rowIndex, PsnappaproveBVO.CLERKCODE);
		 * setBodyCellValue(psncode, rowIndex, PsnappaproveBVO.PSNCODE);
		 * setBodyCellValue(psnname, rowIndex, PsnappaproveBVO.PSNNAME);
		 * setBodyCellValue(deptname, rowIndex, PsnappaproveBVO.DEPTNAME);
		 * setBodyCellValue(psotname, rowIndex, PsnappaproveBVO.POSTNAME);
		 * setBodyCellValue(pk_psnjob, rowIndex, PsnappaproveBVO.PK_PSNJOB);
		 * setBodyCellValue(pk_psndoc, rowIndex, PsnappaproveBVO.PK_PSNDOC);
		 * setBodyCellValue(psnpcl, rowIndex, "pk_psnjob.pk_psncl.name");
		 * setBodyCellValue(jobName, rowIndex, "pk_psnjob.pk_job.jobname");
		 * setBodyCellValue(postseries, rowIndex,
		 * "pk_psnjob.pk_postseries.postseriesname");
		 */

		// shenliangc 20140825 ��δ����Ǻϲ�������ְ�����ƶ�����ʾ���󡱣�
		// �������PsndocRefModelWithPower��û��om_job.jobname�ֶΣ�ȡֵ�����쳣��
		// ���º������벻ִ�У�������ȱ���ݣ�����һϵ�к������⣬��ע����������跨�����
		/*
		 * String jobName = (String)
		 * refpane.getRefValue("om_job.jobname"+MultiLangUtil
		 * .getCurrentLangSeqSuffix()); // �趨ְ�� String if
		 * (StringUtils.isEmpty(jobName)) { jobName = (String)
		 * refpane.getRefValue("om_job.jobname"); // �趨ְ�� String }
		 * setBodyCellValue(jobName, rowIndex, "pk_psnjob.pk_job.jobname");
		 */
		String ismainjob = (String) refpane
				.getRefValue(PsndocRefModelWithPower.ISMAINJOB);
		Integer assgid = (Integer) refpane
				.getRefValue(PsndocRefModelWithPower.ASSGID);
		String pk_psndoc = (String) refpane
				.getRefValue(PsndocRefModelWithPower.PK_PSNDOC);
		setBodyCellValue(ismainjob.equals("Y") ? "N" : "Y", rowIndex,
				PsnappaproveBVO.PARTFLAG);
		setBodyCellValue(assgid, rowIndex, PsnappaproveBVO.ASSGID);
		setBodyCellValue(pk_psndoc, rowIndex, PsnappaproveBVO.PK_PSNDOC);
		setBodyCellValue(new Boolean(true), rowIndex, PsnappaproveBVO.APPROVED);
		// ���н�ʱ�׼��
		String[] clearRow = new String[] {
				PsnappaproveBVO.PK_WA_ITEM_SHOWNAME,
				PsnappaproveBVO.PK_WA_ITEM,
				PsnappaproveBVO.PK_WA_GRD_SHOWNAME,
				PsnappaproveBVO.PK_WA_GRD,
				// ԭн��
				PsnappaproveBVO.PK_WA_CRT_OLD_SHOWNAME,
				PsnappaproveBVO.WA_OLD_MONEY,
				PsnappaproveBVO.WA_CRT_OLD_MONEY,
				PsnappaproveBVO.PK_WA_PRMLV_OLD,
				PsnappaproveBVO.PK_WA_SECLV_OLD,
				// ����н��
				PsnappaproveBVO.NEGOTIATION,
				PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME,
				PsnappaproveBVO.WA_APPLY_MONEY,
				PsnappaproveBVO.WA_CRT_APPLY_MONEY,
				PsnappaproveBVO.PK_WA_PRMLV_APPLY,
				PsnappaproveBVO.PK_WA_SECLV_APPLY,

				PsnappaproveBVO.IS_RANGE, PsnappaproveBVO.CRT_MIN_VALUE,
				PsnappaproveBVO.CRT_MAX_VALUE, PsnappaproveBVO.VNOTE };
		for (String strKey : clearRow) {
			setBodyCellValue(null, rowIndex, strKey);
		}

	}

	/**
	 * @author xuhw on 2009-12-21
	 * @param rowIndex
	 * @throws BusinessException
	 */
	private void changeWaGrd(int rowIndex) {
		String StrGrdPK = refpane.getRefModel().getPkValue();
		String grdpkNow = (String) getBodyCellValue(rowIndex,
				PsnappaproveBVO.PK_WA_GRD);
		if (!StrGrdPK.equals(grdpkNow)) {
			// ���н�ʱ�׼��
			String[] clearRow = new String[] {
					// ԭн��
					PsnappaproveBVO.PK_WA_CRT_OLD_SHOWNAME,
					PsnappaproveBVO.WA_OLD_MONEY,
					PsnappaproveBVO.WA_CRT_OLD_MONEY,
					PsnappaproveBVO.PK_WA_PRMLV_OLD,
					PsnappaproveBVO.PK_WA_SECLV_OLD,
					// ����н��
					PsnappaproveBVO.NEGOTIATION,
					PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME,
					PsnappaproveBVO.WA_APPLY_MONEY,
					PsnappaproveBVO.WA_CRT_APPLY_MONEY,
					PsnappaproveBVO.PK_WA_PRMLV_APPLY,
					PsnappaproveBVO.PK_WA_SECLV_APPLY,

					PsnappaproveBVO.IS_RANGE, PsnappaproveBVO.CRT_MIN_VALUE,
					PsnappaproveBVO.CRT_MAX_VALUE, PsnappaproveBVO.VNOTE,
					PsnappaproveBVO.PK_WA_CRT };
			for (String strKey : clearRow) {
				setBodyCellValue(null, rowIndex, strKey);
			}

			// Object StrGrdPK2 = billModel.getValueAt(rowIndex,
			// PsnappaproveBVO.PK_WA_GRD);

			if (StrGrdPK == null) {
				return;
			}
			setBodyCellValue(StrGrdPK, rowIndex, PsnappaproveBVO.PK_WA_GRD);

			Object strPkWaItem = billModel.getValueAt(rowIndex,
					PsnappaproveBVO.PK_WA_ITEM);
			Object PK_PSNJOB = ((UIRefPane) billModel.getBodyItems()[1]
					.getComponent()).getRefPK();
			Object[] result = (Object[]) ((WaAdjustAppModel) getModel())
					.queryPsnPK(String.valueOf(PK_PSNJOB));
			Object strPkPsndoc = result[0];
			Object assgid = result[1];
			if (strPkWaItem != null && strPkPsndoc != null && StrGrdPK != null) {
				PsnappaproveBVO psnappaproveBVO = null;
				try {
					psnappaproveBVO = WaSalaryadjmgtUtility.getExtraInfo(
							strPkPsndoc.toString(), (Integer) assgid,
							strPkWaItem.toString(), StrGrdPK.toString(), true);

					// 2016-12-2 zhosuze н�ʼ��ܣ�����Զ���������ڵ������Ա�����Ķ����������ӱ����ݽ��� begin
					psnappaproveBVO
							.setWa_crt_old_money(new UFDouble(
									SalaryDecryptUtil.decrypt((psnappaproveBVO
											.getWa_crt_old_money() == null ? new UFDouble(
											0) : psnappaproveBVO
											.getWa_crt_old_money()).toDouble())));
					psnappaproveBVO
							.setWa_crt_apply_money(new UFDouble(
									SalaryDecryptUtil.decrypt((psnappaproveBVO
											.getWa_crt_apply_money() == null ? new UFDouble(
											0) : psnappaproveBVO
											.getWa_crt_apply_money())
											.toDouble())));
					psnappaproveBVO.setWa_cofm_money(new UFDouble(
							SalaryDecryptUtil.decrypt((psnappaproveBVO
									.getWa_cofm_money() == null ? new UFDouble(
									0) : psnappaproveBVO.getWa_cofm_money())
									.toDouble())));
					psnappaproveBVO
							.setWa_old_money(new UFDouble(
									SalaryDecryptUtil.decrypt((psnappaproveBVO
											.getWa_old_money() == null ? new UFDouble(
											0) : psnappaproveBVO
											.getWa_old_money()).toDouble())));
					psnappaproveBVO
							.setWa_apply_money(new UFDouble(
									SalaryDecryptUtil.decrypt((psnappaproveBVO
											.getWa_apply_money() == null ? new UFDouble(
											0) : psnappaproveBVO
											.getWa_apply_money()).toDouble())));
					psnappaproveBVO
							.setWa_crt_cofm_money(new UFDouble(
									SalaryDecryptUtil.decrypt((psnappaproveBVO
											.getWa_crt_cofm_money() == null ? new UFDouble(
											0) : psnappaproveBVO
											.getWa_crt_cofm_money()).toDouble())));
					// end

					for (String codeName : clearRow) {
						setBodyCellValue(
								psnappaproveBVO.getAttributeValue(codeName),
								rowIndex, codeName);
					}
					// ���ı���Ĳ���
					refreshCriterion(StrGrdPK, rowIndex);
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
					MessageDialog.showErrorDlg(getModel().getContext()
							.getEntranceUI(), null, ResHelper.getString(
							"60130adjapprove", "060130adjapprove0073")/*
																	 * @res
																	 * "��ѯ��ԭʼ�Ľ��ʧ��,��鿴н�ʱ�׼��Ա�����趨�Ƿ���ȷ��"
																	 */);
					return;
				}
			}

			getBillCardPanel().getBillModel().getValueAt(0, 42);
		}
	}

	/**
	 * н����Ŀ�����仯
	 * 
	 * @param rowIndex
	 */
	private void changeWaItem(int rowIndex) {
		itempk = refpane.getRefModel().getPkValue();
		String itempkNow = (String) getBodyCellValue(rowIndex,
				PsnappaproveBVO.PK_WA_ITEM);
		if (!itempk.equals(itempkNow)) {
			// ���н�ʱ�׼��
			String[] clearRow = new String[] {
					PsnappaproveBVO.PK_WA_GRD_SHOWNAME,
					PsnappaproveBVO.PK_WA_GRD,
					// ԭн��
					PsnappaproveBVO.PK_WA_CRT_OLD_SHOWNAME,
					PsnappaproveBVO.WA_OLD_MONEY,
					PsnappaproveBVO.WA_CRT_OLD_MONEY,
					PsnappaproveBVO.PK_WA_PRMLV_OLD,
					PsnappaproveBVO.PK_WA_SECLV_OLD,
					// ����н��
					PsnappaproveBVO.NEGOTIATION,
					PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME,
					PsnappaproveBVO.WA_APPLY_MONEY,
					PsnappaproveBVO.WA_CRT_APPLY_MONEY,
					PsnappaproveBVO.PK_WA_PRMLV_APPLY,
					PsnappaproveBVO.PK_WA_SECLV_APPLY,

					PsnappaproveBVO.IS_RANGE, PsnappaproveBVO.CRT_MIN_VALUE,
					PsnappaproveBVO.CRT_MAX_VALUE, PsnappaproveBVO.VNOTE };
			for (String strKey : clearRow) {
				setBodyCellValue(null, rowIndex, strKey);
			}
			// ��סн����Ŀ����
			setBodyCellValue(itempk, rowIndex, PsnappaproveBVO.PK_WA_ITEM);
			// itempk = (String) billModel.getValueAt(rowIndex,
			// PsnappaproveBVO.PK_WA_ITEM);
			// ��ʼ��н�ʱ�׼����
			refreshGradeRef(PsnappaproveBVO.PK_WA_GRD_SHOWNAME,
					new WaGradeRefModel(itempk, getModel().getContext()
							.getPk_org()));
		}
	}

	private PsnappaproveBVO[] filterAdjustPsns(
			PsnappaproveBVO[] psnappaproveBVOs, AdjustWadocVO[] adjustVOs) {
		List<String> lisAdjustPsnPKs = new ArrayList<String>();
		List<PsnappaproveBVO> lisAdjustPsns = new ArrayList<PsnappaproveBVO>();
		for (AdjustWadocVO adjustVO : adjustVOs) {
			lisAdjustPsnPKs.add(adjustVO.getPk_psndoc());
		}

		for (PsnappaproveBVO psnappaproveBVO : psnappaproveBVOs) {
			if (lisAdjustPsnPKs.contains(psnappaproveBVO.getPk_psndoc())) {
				lisAdjustPsns.add(psnappaproveBVO);
			}
		}

		return lisAdjustPsns.toArray(new PsnappaproveBVO[0]);
	}

	/**
	 * �����е��ֶ��ҵ���Ӧ���е�INDEX
	 * 
	 * @param colKey
	 * @return
	 */
	private int findColIndex(String colKey) {
		return getBillCardPanel().getBillModel().getItemIndex(colKey);
	}

	public List<NCAction> getApproveTabActions() {
		return approveTabActions;
	}

	/**
	 * �õ�������ֶ���Ϣ
	 * 
	 * @param strKey
	 * @return
	 */
	public Object getBodyCellObject(String strKey) {
		return getBillCardPanel().getBodyItem(strKey).getComponent();
	}

	/**
	 * �õ������е��ֶ�ֵ
	 */
	public Object getBodyCellValue(int rowIndex, String strKey) {
		return getBillCardPanel().getBodyPanel().getTableModel()
				.getValueAt(rowIndex, strKey);
	}

	/**
	 * �Ա�ͷ���з���
	 * 
	 * @param proKey
	 * @param headerName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private ColumnGroup getColumnGroup(String[] headerName) {
		ColumnGroup columnGroup = new ColumnGroup(headerName[0]);
		TableColumnModel cm = getBillCardPanel().getBillTable()
				.getColumnModel();
		// ���ж��ͷ���趨
		for (int i = 0; i < cm.getColumnCount(); i++) {
			// ѭ����Ҫ���õı�ͷ
			for (int n = 1; n < headerName.length; n++) {
				if (getBillCardPanel().getBillModel().getBodyKeyByCol(i)
						.equalsIgnoreCase(headerName[n])) {
					columnGroup.add(cm.getColumn(i));
					break;
				}
			}
		}
		return columnGroup;
	}

	/**
	 * �õ���ͷ���ֶ���Ϣ
	 * 
	 * @param strKey
	 * @return
	 */
	public Object getHeadCellValue(String strKey) {
		return getBillCardPanel().getHeadItem(strKey).getValueObject();
	}

	private UICheckBox getPartflagBox() {
		if (partflagBox == null) {
			partflagBox = new UICheckBox();
			partflagBox.setBackground(Style.getDlgBgColor());
			partflagBox.setName("partflagBox");
			partflagBox.setText(ResHelper.getString("60130adjapprove",
					"060130adjapprove0140")/* @res "��ְ" */);
			partflagBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent arg0) {
					UIRefPane psnRefpane = (UIRefPane) getBillCardPanel()
							.getBodyItem(PsnappaproveBVO.PK_PSNJOB)
							.getComponent();
					psnRefpane.getRefModel().setWherePart(
							"  hi_psnjob.ismainjob = '"
									+ (getPartflagBox().isSelected() ? 'N'
											: 'Y') + "'", true);

					UFRefGridTreeUI ui = (UFRefGridTreeUI) psnRefpane
							.getRefUI();
					ui.onRefresh();
					/*
					 * TreePath i = ui.getUITree1().getSelectionPath();
					 * ui.showModal(); ui.getUITree1().
					 * ui.getUITree1().setSelectionPath(i);
					 */

				}
			});
			partflagBox.setSelected(false);
			partflagBox.setVisible(true);
			partflagBox.setOpaque(false);
		}
		return partflagBox;
	}

	public WaCriterionVO getSelectVO() {
		return selectVO;
	}

	/**
	 * �ѷ�����ֶηŵ�һ��map��
	 * 
	 * @return
	 */
	private List<String[]> getTableHeadInfo() {
		if (headlist == null) {
			headlist = new ArrayList<String[]>();
			// ԭн�ʵı�ͷ
			String strOlds[] = new String[] {
					ResHelper.getString("60130adjapprove",
							"060130adjapprove0074")/* @res "ԭн��" */,
					PsnappaproveBVO.PK_WA_CRT_OLD_SHOWNAME,
					PsnappaproveBVO.WA_OLD_MONEY,
					PsnappaproveBVO.WA_CRT_OLD_MONEY };
			// ����н��
			String strApplys[] = new String[] {
					ResHelper.getString("60130adjapprove",
							"060130adjapprove0075")/* @res "����н��" */,
					PsnappaproveBVO.NEGOTIATION,
					PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME,
					PsnappaproveBVO.WA_APPLY_MONEY,
					PsnappaproveBVO.WA_CRT_APPLY_MONEY,
					PsnappaproveBVO.PK_CHANGECAUSE
			/* PsnappaproveBVO.APPLYREASON */};
			// ����н��
			String strCofm[] = new String[] {
					ResHelper.getString("60130adjapprove",
							"060130adjapprove0076")/* @res "����н��" */,
					PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME,
					PsnappaproveBVO.WA_COFM_MONEY,
					PsnappaproveBVO.WA_CRT_COFM_MONEY };
			headlist.add(strOlds);
			headlist.add(strApplys);
			headlist.add(strCofm);
		}
		return headlist;
	}

	@Override
	public void handleEvent(AppEvent event) {
		/**
		 * Ϊ�˼��ٲ�ѯ��������������model������isAutoExecLoadFormula����ʶ�Ƿ��Զ����ع�ʽ
		 * ���磺�ڲ�ѯʱ�����б����沢û���漰���ӱ��еĹ�ʽ�ֶΣ����Կ��Խ�isAutoExecLoadFormula���ó�false
		 */
		// 2015-12-3 NCdp205551130 zhousze
		// ��������������ڵ㣬�״ν���ֱ���޸ġ�н����Ŀ���ȹ�ʽ�ֶβ���ʾ���⡣����������
		// ��ʼ����������ʱ���Ѿ����Զ����ع�ʽ��������model�д�����ݾ�û�м��س���������޸ĵ�ʱ��ȡ�����ݾͲ����ˡ����ڽ�
		// �ò���ע��ȷ������û���⡣ begin
		// super.setAutoExecLoadFormula(((WaAdjustAppModel)getModel()).isAutoExecLoadFormula());
		// end
		super.handleEvent(event);
		// }
		// end
		// end
		// 2015-12-3 NCdp205551130 zhousze
		// ��������������ڵ㣬�״ν���ֱ���޸ġ�н����Ŀ���ȹ�ʽ�ֶβ���ʾ���⡣����������
		// ��ʼ����������ʱ���Ѿ����Զ����ع�ʽ��������model�д�����ݾ�û�м��س���������޸ĵ�ʱ��ȡ�����ݾͲ����ˡ����ڽ�
		// �ò���ע��ȷ������û���⡣ begin
		// Object obj = getModel().getSelectedData();
		// if(AppEventConst.SELECTION_CHANGED.equalsIgnoreCase(event.getType())
		// && obj !=null &&
		// !((WaAdjustAppModel)getModel()).isAutoExecLoadFormula()){
		// ((WaAdjustAppModel)getModel()).setAutoExecLoadFormula(true);
		// }
		// end
		UIState state = getModel().getUiState();
		if (AppEventConst.MODEL_INITIALIZED.equalsIgnoreCase(event.getType())) {
			// ��������Ĵ���ʹ��ʼ����ͷʧЧ�������������ʼ�����ͷ
			// ��ʼ��ҳ���ϵĶ��ͷ��Ϣ
			initPageInfo();
			UIRefPane changeCauseRefpane = (UIRefPane) getBillCardPanel()
					.getBodyItem(PsnappaproveVO.PK_CHANGECAUSE).getComponent();
			changeCauseRefpane.getRefModel().setPk_org(
					getModel().getContext().getPk_org());
			changeCauseRefpane.getRefModel().addWherePart(
					" and pk_defdoclist = '1002Z710000000004MO1' ", true);
		}

		else if (ISalaryadjmgtConstant.COPY_DO.equals(event.getType())) {
			setCopyStates();
		}

		else if (state == UIState.EDIT) {
			if (isInputPnl && appmodel.getAutoGenerateBillCode()) {
				if (!appmodel.isAutoCodeEdit()) {
					// �Զ����룬 ���ݱ��벻�ɱ༭
					setHeadItemEnabled(PsnappaproveVO.BILLCODE, false);
				}
			}
			// ���������
			// �Զ���д���е�����
			else if (!isInputPnl) {
				// ��������
				setTailCellValue(PubEnv.getServerTime(),
						PsnappaproveVO.CONFIRMDATE);
				int rc = getBillCardPanel().getBodyPanel().getTable()
						.getRowCount();
				if (rc > 0) {
					for (int i = 0; i < rc; i++) {
						updateApproveData(i);
					}
				}
				setApproveCellEnable(false);
			}
		}
		if (isInputPnl) {
			// �Ƿ�ͨ��checkbox����ʾ����
			Integer strConfirmState = (Integer) getHeadCellValue(PsnappaproveBVO.CONFIRMSTATE);
			if (state != UIState.NOT_EDIT) {
				// 2015-08-12 zhousze �������͸��ݵ�ǰ������ʽ�Ƿ�ɼ�
				if (getApproveType() != null
						&& getApproveType().equals(
								HRConstEnum.APPROVE_TYPE_FORCE_WORK_FLOW)) {
					getHeadItem(PsnappaproveVO.TRANSTYPEID).setEnabled(true);
				} else if (getApproveType() != null
						&& getApproveType().equals(
								HRConstEnum.APPROVE_TYPE_FORCE_DIRECT)) {
					getHeadItem(PsnappaproveVO.TRANSTYPEID).setEnabled(false);
				}
			}

			// ����δͨ�� ȷ�������� �ύ̬�ĵ���checkbox�ɼ�
			if (strConfirmState == null
					|| IPfRetCheckInfo.PASSING != strConfirmState) {
				getBillCardPanel().hideBodyTableCol(PsnappaproveBVO.APPROVED);
			} else {
				getBillCardPanel().showBodyTableCol(PsnappaproveBVO.APPROVED);
			}
		} else {
			getBillCardPanel().showBodyTableCol(PsnappaproveBVO.APPROVED);
		}
		setCellEditableController();

	}

	/**
	 * ���������ʽ 3�ύ������/2ֱ��/1������/0ϵͳ�ж� Created on 2015-08-12
	 * 
	 * @return Integer
	 * @throws BusinessException
	 * @author zhousze
	 */
	protected Integer getApproveType() {
		try {
			return SysInitQuery.getParaInt(getModel().getContext().getPk_org(),
					IHrPf.hashBillTypePara.get(((WaAdjustAppModel) getModel())
							.getBillType()));
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * ��ȡ��Ŀ����Ҫ�����ڶ��������͸���������ʽ�����Ƿ�ɱ༭ Created on 2015-08-12
	 * 
	 * @param strKey
	 * @return
	 * @author zhousze
	 */
	private BillItem getHeadItem(String strKey) {
		return getBillCardPanel().getHeadItem(strKey);
	}

	/**
	 * ��ʼ��ҳ����Ϣ�����ҳ���ϵĶ��ͷ����ʽ
	 */
	private void initPageInfo() {
		// �趨���ͷ
		GroupableTableHeader header = (GroupableTableHeader) getBillCardPanel()
				.getBillTable().getTableHeader();
		for (int i = 0; i < getTableHeadInfo().size(); i++) {
			String[] headname = getTableHeadInfo().get(i);
			header.addColumnGroup(getColumnGroup(headname));
		}
	}

	private void initRefModel() {

		UIRefPane refPane = ((UIRefPane) getBillCardPanel().getHeadItem(
				PsnappaproveVO.PK_BUSITYPE).getComponent());
		refPane.getRefModel().addWherePart(" and busiprop= 6031 ", true);

		// ��ʼ����ͷҵ�����Ͳ���
		// nc.ui.bd.ref.model.PsndocDefaultRefModel
		/*
		 * PsndocRefModelWithPower psnrefmodel = new PsndocRefModelWithPower();
		 * psnrefmodel.setPk_group(getModel().getContext().getPk_group());
		 * psnrefmodel.setPk_org(getModel().getContext().getPk_org());
		 * setBodyRefModel(PsnappaproveBVO.CLERKCODE, psnrefmodel);
		 */

		UIRefPane psnRefpane = (UIRefPane) getBillCardPanel().getBodyItem(
				PsnappaproveBVO.PK_PSNJOB).getComponent();
		psnRefpane.getRefModel().addWherePart(" and hi_psnorg.indocflag = 'Y'");
		psnRefpane.setRefType(IRefConst.GRIDTREE);
		psnRefpane.addValueChangedListener(this);
		psnRefpane.setButtonFireEvent(true);

		UIRefPane grdRefpane = (UIRefPane) getBillCardPanel().getBodyItem(
				PsnappaproveBVO.PK_WA_GRD_SHOWNAME).getComponent();
		grdRefpane.addValueChangedListener(this);
		grdRefpane.setButtonFireEvent(true);

		UIRefPane itemRefpane = (UIRefPane) getBillCardPanel().getBodyItem(
				PsnappaproveBVO.PK_WA_ITEM_SHOWNAME).getComponent();
		itemRefpane.addValueChangedListener(this);
		itemRefpane.setButtonFireEvent(true);

		UIRefPane applyRefpane = (UIRefPane) getBillCardPanel().getBodyItem(
				PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME).getComponent();
		applyRefpane.addValueChangedListener(this);
		applyRefpane.setButtonFireEvent(true);

		UIRefPane confirmRefpane = (UIRefPane) getBillCardPanel().getBodyItem(
				PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME).getComponent();
		confirmRefpane.addValueChangedListener(this);
		confirmRefpane.setButtonFireEvent(true);

	}

	/** ��ʼ��UI */
	@Override
	public void initUI() {
		super.initUI();

		/*
		 * ��Ԫ���Ƿ����
		 */
		BillTableCellRenderer cellRenderer = new BillTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);
				BillModel billModel = billCardPanel.getBillModel();
				Object object = billModel.getValueAt(row,
						PsnappaproveBVO.NEGOTIATION);
				// if(object==null)
				// return c;
				boolean isNegotiation = object == null ? false
						: (Boolean) object;
				if (value == null && !isNegotiation) {
					if (getModel().getUiState() != UIState.EDIT) {
						return c;
					}
					super.setForeground(Color.RED);
					setText("*");
				}
				return c;
			}
		};
		getBillCardPanel().getBodyPanel()
				.getShowCol(PsnappaproveBVO.PK_WA_GRD_SHOWNAME)
				.setCellRenderer(cellRenderer);
		if (getPartflagShow() != null && !getPartflagShow().booleanValue()) {
			getBillCardPanel().hideBodyTableCol(PsnappaproveBVO.PARTFLAG);
			getBillCardPanel().hideBodyTableCol(PsnappaproveBVO.ASSGID);
		}

		// /*ƽ̨��ʽ�༭��û����������������⴦��һ��*/
		// String loadFormula =
		// (getBillCardPanel().getBodyItem(PsnappaproveBVO.PK_WA_ITEM_SHOWNAME).getLoadFormula())[0];
		// String langName = ",name" + MultiLangUtil.getCurrentLangSeqSuffix();
		// loadFormula = loadFormula.replaceAll(",name", langName);
		// getBillCardPanel().getBodyItem(PsnappaproveBVO.PK_WA_ITEM_SHOWNAME).setLoadFormula(new
		// String[]{loadFormula});

		getBillCardPanel().addEditListener(this);
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
		getBillCardPanel().getBodyPanel().addEditListener2(this);
		// ����δת����Ա
		addDecimalListener();
		appmodel = (WaAdjustAppModel) getModel();
		billModel = getBillCardPanel().getBillModel();
		isInputPnl = true;

		// billCardPanel.addTabAction(IBillItem.BODY, null);
		if (appmodel.isApproveSite()) {
			isInputPnl = false;
			setApproveCellEnable(false);
		}
		initRefModel();

		// ((UIRefPane)
		// billCardPanel.getBillModel().getItemByKey("pk_psnjob").getComponent()).getRefModel().addWherePart(" and hi_psnorg.indocflag = 'Y'");

		initValues();
		// 2015-12-3 NCdp205551130 zhousze
		// ��������������ڵ㣬�״ν���ֱ���޸ġ�н����Ŀ���ȹ�ʽ�ֶβ���ʾ���⡣����������
		// ��ʼ����������ʱ���Ѿ����Զ����ع�ʽ��������model�д�����ݾ�û�м��س���������޸ĵ�ʱ��ȡ�����ݾͲ����ˡ����ڽ�
		// �ò���ע��ȷ������û���⡣ begin
		// �����ڵ����ڿ��ܴ������ݻ��Զ����ع�ʽ����������ʱ��ʾ����Ϊ���Զ�����
		// if(((PFAppModel)getModel()).isApproveSite()){
		// ((WaAdjustAppModel)getModel()).setAutoExecLoadFormula(false);
		// }
		// end
		// 2015-11-26 NCdp205550467 zhousze ȥ�����������뿨Ƭ�����ֱ��Ҽ��˵�����ʾ������ begin
		getBillCardPanel().getBodyPanel().setBBodyMenuShow(false);
		// 20151214 shenliangc NCdp205559130 н�ʹ�������һ�к����������ûس����У��ڶ����к�Ϊ�գ�����ʱ����
		// ȥ���س����С��ӱ������Ҽ���
		getBillCardPanel().getBodyPanel().setAutoAddLine(false);
		// end
	}

	/**
	 * @author xuhw on 2009-12-29
	 */
	private void initValues() {
		strPkWaGrdShowname = PsnappaproveBVO.PK_WA_GRD_SHOWNAME;
		if (isInputPnl) {
			strCrt = PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME;
			strMoney = PsnappaproveBVO.WA_APPLY_MONEY;
		} else {
			strCrt = PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME;
			strMoney = PsnappaproveBVO.WA_COFM_MONEY;
		}
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
		if (getModel().getUiState() != UIState.ADD
				&& getModel().getUiState() != UIState.EDIT) {
			return blIsEditable;
		}
		Boolean blnNegotiation = false;
		Object object = billModel.getValueAt(iRowIndex,
				PsnappaproveBVO.NEGOTIATION);
		if (object != null) {
			blnNegotiation = (Boolean) object;
		}
		if (strCrt.equals(strItemKey)) {
			return !blnNegotiation;
		}
		if (strPkWaGrdShowname.equals(strItemKey)) {
			if (appmodel.isApproveSite()) {
				return false;
			} else {
				return !blnNegotiation;
			}
		}
		if (strMoney.equals(strItemKey)) {
			if (!isRangeCkeck) {
				return blnNegotiation;
			}
		}
		return blIsEditable;
	}

	/**
	 * ���ı������
	 * 
	 * @param colKey
	 * @throws BusinessException
	 */
	private void refreshCriterion(final String strPkWaGrd, final int intRowLine) {
		String colKey = null;
		if (WaSalaryadjmgtUtility.isNull(strPkWaGrd)) {
			return;
		}
		if (isInputPnl) {
			colKey = PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME;
		} else {
			colKey = PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME;
		}

		int colKeyIndex = findColIndex(colKey);

		WaGradeVO wagradevo = null;
		try {
			wagradevo = appmodel.queryWagradeVoByGradePk(strPkWaGrd);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(),
					null, e.getMessage());
			return;
		}
		// �Ƿ��ѡ
		isMultsecCkeck = false;
		isRangeCkeck = false;

		if (wagradevo.getIsmultsec() != null) {
			isMultsecCkeck = wagradevo.getIsmultsec().booleanValue();
		}

		// �Ƿ����н��

		if (wagradevo.getIsrange() != null) {
			isRangeCkeck = wagradevo.getIsrange().booleanValue();
		}

		Boolean isNeg = (Boolean) getBodyCellValue(intRowLine,
				PsnappaproveBVO.NEGOTIATION);
		if (isNeg == null) {
			isNeg = false;
		}

		if (isNeg) {
			setBodyCellValue(UFBoolean.valueOf(false).toString(), intRowLine,
					PsnappaproveBVO.IS_RANGE);
		} else {
			setBodyCellValue(UFBoolean.valueOf(isRangeCkeck).toString(),
					intRowLine, PsnappaproveBVO.IS_RANGE);
		}

		UIRefPane pane = new UIRefPane() {
			private static final long serialVersionUID = -4261446741080782942L;

			@Override
			public void onButtonClicked() {
				if (strPkWaGrd != null) {
					WAGradePrmSecDia dlg = new WAGradePrmSecDia(getModel()
							.getContext().getEntranceUI(), strPkWaGrd,
							isMultsecCkeck, isRangeCkeck);
					dlg.showModal();
					if (dlg.getResult() == UIDialog.ID_OK) {
						selectVO = dlg.getSelectVO();
						if (appmodel.isApproveSite()) {
							changeCrtApprove4Dia(intRowLine, selectVO);
						} else {
							changeCrtApply4Dia(intRowLine, selectVO);
						}

						getBillCardPanel().stopEditing();
					}
				}
			}
		};

		pane.setRefModel(new WaPrmSecRefModel(strPkWaGrd, isMultsecCkeck,
				isRangeCkeck));

		NumberFormat formater = NumberFormat.getInstance();
		formater.setMaximumFractionDigits(3);
		formater.setMinimumFractionDigits(3);
		pane.setEditable(true);
		pane.setButtonFireEvent(true);
		pane.addValueChangedListener(this);
		pane.setEnabled(true);
		colKeyIndex = getBillCardPanel().getBodyPanel().getTable()
				.convertColumnIndexToView(colKeyIndex);
		TableColumn tableColumn = getBillCardPanel().getBodyPanel().getTable()
				.getColumnModel().getColumn(colKeyIndex);

		tableColumn.setCellEditor(new BillCellEditor(pane));
	}

	/**
	 * ˢ�±�׼������
	 * 
	 * @author xuhw
	 * @param colKey
	 * @param refModel
	 */
	private void refreshGradeRef(String strColKey, AbstractRefModel refModel) {
		// �����е��ֶ��ҵ���Ӧ���е�INDEX
		int intColKeyIndex = getBillCardPanel().getBillModel().getItemIndex(
				strColKey);
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getBillModel()
				.getBodyItems()[intColKeyIndex].getComponent();
		refModel.setPk_org(getModel().getContext().getPk_org());
		refPane.setRefModel(refModel);
		refPane.setButtonFireEvent(true);
		refPane.addValueChangedListener(this);
		refPane.getRefModel().reloadData();
	}

	/**
	 * �����ڵ�ʱ ���н�����Ŀ���������
	 */
	private void setApproveCellEnable(boolean isEditaAble) {
		setBodyItemEnabled(PsnappaproveBVO.APPROVED, !isEditaAble);
		setBodyItemEnabled(PsnappaproveBVO.PK_PSNJOB, isEditaAble);
		setBodyItemEnabled(PsnappaproveBVO.PK_WA_ITEM_SHOWNAME, isEditaAble);
		setBodyItemEnabled(PsnappaproveBVO.PK_WA_GRD_SHOWNAME, isEditaAble);
		setBodyItemEnabled(PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME,
				isEditaAble);
		setBodyItemEnabled(PsnappaproveBVO.WA_APPLY_MONEY, isEditaAble);
		// setBodyItemEnabled(PsnappaproveBVO.APPLYREASON, isEditaAble);
		setBodyItemEnabled(PsnappaproveBVO.PK_CHANGECAUSE, isEditaAble);
		setBodyItemEnabled(PsnappaproveBVO.NEGOTIATION, isEditaAble);
		setBodyItemEnabled(PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME,
				!isEditaAble);
		setBodyItemEnabled(PsnappaproveBVO.WA_COFM_MONEY, !isEditaAble);

		// ���ݱ���
		setHeadItemEnabled(PsnappaproveVO.BILLCODE, isEditaAble);
		// ��������
		setHeadItemEnabled(PsnappaproveVO.BILLNAME, isEditaAble);
		// ����״̬
		setHeadItemEnabled(PsnappaproveVO.CONFIRMSTATE, isEditaAble);
		// ҵ������
		setHeadItemEnabled(PsnappaproveVO.PK_BUSITYPE, isEditaAble);
		// ��������
		setHeadItemEnabled(PsnappaproveVO.TRANSTYPEID, isEditaAble);
		// ��������
		getBillCardPanel().getTailItem(PsnappaproveVO.CONFIRMDATE).setEdit(
				isEditaAble);
		// // �䶯ԭ��
		// setHeadItemEnabled(PsnappaproveVO.PK_CHANGECAUSE, isEditaAble);
		// ������
		setHeadItemEnabled(PsnappaproveVO.OPERATOR, isEditaAble);
		// �����ļ�
		setHeadItemEnabled(PsnappaproveVO.VBASEFILE, isEditaAble);

		// �����ļ�
		getBillCardPanel().getBodyItem(PsnappaproveBVO.WA_COFM_MONEY).setNull(
				isEditaAble);

		getBillCardPanel().getBodyItem(PsnappaproveBVO.WA_COFM_MONEY).setNull(
				true);
		// ��������
		setHeadItemEnabled(PsnappaproveVO.APPLYDATE, isEditaAble);
	}

	public void setApproveTabActions(List<NCAction> approveTabActions) {
		this.approveTabActions = approveTabActions;
	}

	/**
	 * ���������ֶν��и�ֵ
	 * 
	 * @param aValue
	 * @param rowIndex
	 * @param strKey
	 */
	private void setBodyCellValue(Object aValue, int rowIndex, int conIndex) {
		if (null != aValue) {
			billModel.setValueAt(null, rowIndex, conIndex);
		}

		billModel.setValueAt(aValue, rowIndex, conIndex);
	}

	/**
	 * ���������ֶν��и�ֵ
	 * 
	 * @param aValue
	 * @param rowIndex
	 * @param strKey
	 */
	private void setBodyCellValue(Object aValue, int rowIndex, String strKey) {
		if (null != aValue) {
			billModel.setValueAt(null, rowIndex, strKey);
		}
		billModel.setValueAt(aValue, rowIndex, strKey);
	}

	/**
	 * �����Ƿ���Ա༭
	 * 
	 * @param colKey
	 * @param edit
	 */
	private void setBodyItemEnabled(String colKey, boolean edit) {
		getBillCardPanel().getBodyItem(colKey).setEnabled(edit);
	}

	/**
	 * ��ʼ������
	 * 
	 * @author xuhw on 2010-4-15
	 * @param strItemKey
	 * @param refModel
	 */
	private void setBodyRefModel(String strItemKey, AbstractRefModel refModel) {

		UIRefPane refPane = ((UIRefPane) getBillCardPanel().getBodyItem(
				strItemKey).getComponent());
		refPane.setRefModel(refModel);
		refPane.setButtonFireEvent(true);
		refPane.addValueChangedListener(this);
		getBillCardPanel().setBillData(getBillCardPanel().getBillData());
	}

	/**
	 * ���õ�Ԫ���ܷ�༭
	 * 
	 * @author xuhw on 2009-12-23
	 */
	private void setCellEditableController() {
		billModel
				.setCellEditableController(new BillModelCellEditableController() {
					public boolean isCellEditable(boolean blnIsEditable,
							int iRowIndex, String strItemKey) {
						return isBodyCellEditable(blnIsEditable, iRowIndex,
								strItemKey);
					}
				});
	}

	/**
	 * ����׼������
	 * 
	 * @author xuhw on 2010-4-28
	 * @throws BusinessException
	 */
	private void setCopyStates() {

		UIRefPane transtypeRefpane = (UIRefPane) getBillCardPanel()
				.getHeadItem(PsnappaproveVO.TRANSTYPEID).getComponent();

		// �趨�������� code��code�������ǡ�6301�� ��Ҫ���ݽ������͸ı�
		setHeadItemValue(PsnappaproveVO.TRANSTYPE,
				transtypeRefpane.getRefCode());

		getBillCardPanel().getHeadItem(PsnappaproveVO.CONFIRMSTATE).setValue(
				IPfRetCheckInfo.NOSTATE);
		// ����״̬ ��Ϊ����̬
		if (((WaAdjustAppModel) getModel()).getAutoGenerateBillCode()) {// ���ݱ���
			try {
				getBillCardPanel()
						.getHeadItem(PsnappaproveVO.BILLCODE)
						.setValue(((WaAdjustAppModel) getModel()).getBillCode());
				getBillCardPanel().getHeadItem(PsnappaproveVO.BILLNAME)
						.setValue(
								getBillCardPanel().getHeadItem(
										PsnappaproveVO.BILLCODE).getValue());
			} catch (BusinessException e) {
				Logger.error(e.getMessage());
			}
			if (!appmodel.isAutoCodeEdit()) {
				// �Զ����룬 ���ݱ��벻�ɱ༭
				setHeadItemEnabled(PsnappaproveVO.BILLCODE, false);
			}
		} else {
			getBillCardPanel().getHeadItem(PsnappaproveVO.BILLCODE).setValue(
					null);
		}
		getBillCardPanel().getHeadItem(PsnappaproveVO.OPERATOR).setValue(
				PubEnv.getPk_user());// ������
		getBillCardPanel().getHeadItem(PsnappaproveVO.APPLYDATE).setValue(
				PubEnv.getServerDate());// ��������
		getBillCardPanel().getHeadItem(PsnappaproveVO.USEDATE).setValue(null);// ��Ч����
		getBillCardPanel().getHeadItem(PsnappaproveVO.APPROVE_NOTE).setValue(
				null);// �������
		getBillCardPanel().getTailItem(PsnappaproveVO.CONFIRMDATE).setValue(
				null);// ȷ������
		getBillCardPanel().getHeadItem(PsnappaproveVO.SUM_CONFIM_MONEY)
				.setValue(null);// �������
		getBillCardPanel().getTailItem(PsnappaproveVO.APPROVER).setValue(null);// ��Ч����

		// ������
		getBillCardPanel().getTailItem(PsnappaproveVO.CREATOR).setValue("");
		// ����ʱ��
		getBillCardPanel().getTailItem(PsnappaproveVO.CREATIONTIME)
				.setValue("");

		// �޸���
		getBillCardPanel().getTailItem(PsnappaproveVO.MODIFIER).setValue("");
		// �޸�ʱ��
		getBillCardPanel().getTailItem(PsnappaproveVO.MODIFIEDTIME)
				.setValue("");

		// ��ѯ��ԭʼ�Ľ��
		BillModel billModel = getBillCardPanel().getBillModel();
		try {
			billModel.setBodyDataVO(WaSalaryadjmgtUtility.getExtraInfo(
					(PsnappaproveBVO[]) billModel
							.getBodyValueVOs(PsnappaproveBVO.class.getName()),
					false));
			billModel.loadLoadRelationItemValue();
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(),
					null, ResHelper.getString("60130adjapprove",
							"060130adjapprove0073")/*
													 * @res
													 * "��ѯ��ԭʼ�Ľ��ʧ��,��鿴н�ʱ�׼��Ա�����趨�Ƿ���ȷ��"
													 */);
			return;
		}
		for (int i = 0; i < billModel.getRowCount(); i++) {
			// ȷ����Ϣ
			billModel.setValueAt(null, i,
					billModel.getBodyColByKey(PsnappaproveBVO.WA_COFM_MONEY));
			billModel.setValueAt(null, i, billModel
					.getBodyColByKey(PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME));
			billModel.setValueAt(null, i, billModel
					.getBodyColByKey(PsnappaproveBVO.WA_CRT_COFM_MONEY));
			billModel
					.setValueAt(null, i, billModel
							.getBodyColByKey(PsnappaproveBVO.PK_WA_SECLV_COFM));
			billModel
					.setValueAt(null, i, billModel
							.getBodyColByKey(PsnappaproveBVO.PK_WA_PRMLV_COFM));
			billModel.setValueAt(UFBoolean.valueOf(true), i,
					billModel.getBodyColByKey(PsnappaproveBVO.APPROVED));

			billModel.setRowState(i, BillModel.ADD);
		}
	}

	@Override
	protected void setDefaultValue() {
		super.setDefaultValue();
		if (isInputPnl) {
			if (appmodel.getAutoGenerateBillCode()) {
				if (!appmodel.isAutoCodeEdit()) {
					// �Զ����룬 ���ݱ��벻�ɱ༭
					setHeadItemEnabled(PsnappaproveVO.BILLCODE, false);
				}
				// �Զ����룬 ��������Ĭ��ͬ����
				Object billCodeObject = getHeadCellValue(PsnappaproveVO.BILLCODE);
				setHeadCellValue(billCodeObject, PsnappaproveVO.BILLNAME);
			}
			// ������������
			setHeadCellValue(PubEnv.getServerDate(), PsnappaproveVO.APPLYDATE);
			setHeadCellValue(ISalaryadjmgtConstant.BillTYPE_ENTRY,
					PsnappaproveVO.TRANSTYPE);
			setHeadCellValue(((WaAdjustAppModel) getModel()).getBillType(),
					PsnappaproveVO.BILLTYPE);
		}
	}

	/**
	 * ���ñ�ͷ���յ�ֵ
	 * 
	 * @param refpk
	 * @param strKey
	 */
	public void setHeadCellRefPk(String refpk, String strKey) {
		JComponent component = getBillCardPanel().getHeadItem(strKey)
				.getComponent();
		if (component instanceof UIRefPane) {
			UIRefPane refPane = (UIRefPane) component;
			refPane.setPK(refpk);
		}
	}

	/**
	 * ���ñ�ͷ��Ŀֵ
	 * 
	 * @param value
	 * @param strKey
	 */
	public void setHeadCellValue(Object value, String strKey) {
		getBillCardPanel().getHeadItem(strKey).setValue(value);
	}

	/**
	 * ��ͷ�Ƿ���Ա༭
	 * 
	 * @param colKey
	 * @param edit
	 */
	public void setHeadItemEnabled(String colKey, boolean edit) {
		getBillCardPanel().getHeadItem(colKey).setEnabled(edit);
	}

	public void setSelectVO(WaCriterionVO selectVO) {
		this.selectVO = selectVO;
	}

	/**
	 * ���ñ�ͷ��Ŀֵ
	 * 
	 * @param value
	 * @param strKey
	 */
	public void setTailCellValue(Object value, String strKey) {
		getBillCardPanel().getTailItem(strKey).setValue(value);
	}

	/**
	 * �����������Ŀ��������������
	 * 
	 * @param rowIndex
	 */
	private void updateApproveData(int rowIndex) {
		if (billModel.getValueAt(rowIndex, PsnappaproveBVO.WA_COFM_MONEY) == null
				|| billModel
						.getValueAt(rowIndex, PsnappaproveBVO.WA_COFM_MONEY)
						.toString().length() == 0) {
			// Ĭ������ͨ��
			setBodyCellValue(UFBoolean.valueOf(true), rowIndex,
					PsnappaproveBVO.APPROVED);
			setBodyCellValue(billModel.getValueAt(rowIndex,
					PsnappaproveBVO.PK_WA_PRMLV_APPLY), rowIndex,
					PsnappaproveBVO.PK_WA_PRMLV_COFM);
			setBodyCellValue(billModel.getValueAt(rowIndex,
					PsnappaproveBVO.PK_WA_SECLV_APPLY), rowIndex,
					PsnappaproveBVO.PK_WA_SECLV_COFM);
			setBodyCellValue(billModel.getValueAt(rowIndex,
					PsnappaproveBVO.WA_APPLY_MONEY), rowIndex,
					PsnappaproveBVO.WA_COFM_MONEY);
			setBodyCellValue(billModel.getValueAt(rowIndex,
					PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME), rowIndex,
					PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME);
			setBodyCellValue(billModel.getValueAt(rowIndex,
					PsnappaproveBVO.WA_CRT_APPLY_MONEY), rowIndex,
					PsnappaproveBVO.WA_CRT_COFM_MONEY);
			billModel.getRowAttribute(rowIndex).setRowState(
					BillModel.MODIFICATION);
		}
	}

	/**
	 * @author xuhw on 2010-5-10
	 * @see nc.ui.pub.beans.ValueChangedListener#valueChanged(nc.ui.pub.beans.ValueChangedEvent)
	 */
	@Override
	public void valueChanged(ValueChangedEvent event) {
		getBillCardPanel().stopEditing();
	}

	private UFBoolean getPartflagShow() {
		if (partflagShow == null) {
			partflagShow = WaAdjustParaTool.getPartjob_Adjmgt(getModel()
					.getContext().getPk_group());
		}
		return partflagShow;
	}

}