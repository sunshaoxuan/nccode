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
 * 定调资 卡片界面
 * 
 * @author: xuhw
 * @date: 2009-12-17 下午12:56:56
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("serial")
public class WaAdjustCardForm extends HrBillFormEditor implements
		ITabbedPaneAwareComponent, BillEditListener, ValueChangedListener,
		BillCardBeforeEditListener, BillEditListener2 {
	/** 定调资 model */
	private WaAdjustAppModel appmodel;
	/** 定调资审批肩膀按钮 */
	private List<NCAction> approveTabActions;
	private BillModel billModel;
	private UIRefPane refpane = null;
	/** 上限 */
	double dblMaxValue = 0.0D;
	/** 下限 */
	double dblMinValue = 0.0D;
	private List<String[]> headlist = null;
	/** 是否是申请节点 */
	private boolean isInputPnl = true;
	/** 是否多选 */
	boolean isMultsecCkeck = false;
	/** 是否宽带薪酬 */
	boolean isRangeCkeck = false;

	/** 薪资项目PK */
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
	 * 增加BO
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
	 * 设置小数位数监听 使每行的小数位数和薪资项目保持一致
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
	 * 薪资普调、
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

				// 申请金额 wa_apply_money
				psnappaproveBVO2.setWa_apply_money(hashMap.get(key)
						.getMoney_adjust());
				// // 申请级别主键 pk_wa_crt_apply
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

		// 谈判工资项目发生变化
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

		// 申请薪资标准发生变化
		else if (e.getKey().equalsIgnoreCase(
				PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME)) {
			this.refpane = (UIRefPane) ((BillCellEditor) e.getSource())
					.getComponent();
			this.refpane.setAutoCheck(false);
			this.setBodyCellValue(this.applyShowName, rowIndex,
					PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME);
			this.changeCrtApply(rowIndex);
		}
		// 确认薪资标准发生变化
		else if (e.getKey().equalsIgnoreCase(
				PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME)) {
			this.refpane = (UIRefPane) ((BillCellEditor) e.getSource())
					.getComponent();
			this.setBodyCellValue(this.confirmShowName, rowIndex,
					PsnappaproveBVO.PK_WA_CRT_COFM_SHOWNAME);
			this.changeCrtApprove(rowIndex);
		}
		// 薪资项目发生变化
		else if (e.getKey().equalsIgnoreCase(
				PsnappaproveBVO.PK_WA_ITEM_SHOWNAME)) {
			this.refpane = (UIRefPane) ((BillCellEditor) e.getSource())
					.getComponent();
			this.changeWaItem(rowIndex);
		}
		// 薪资类别发生变化
		else if (e.getKey()
				.equalsIgnoreCase(PsnappaproveBVO.PK_WA_GRD_SHOWNAME)) {
			this.refpane = (UIRefPane) ((BillCellEditor) e.getSource())
					.getComponent();
			this.changeWaGrd(rowIndex);

			// 20140728 shenliangc 为解决定新增调资申请单增加一行，选择薪资标准类别之后，
			// 薪资标准参照打不开的问题，从bodyRowChange()中整体复制过来的逻辑。
			// 薪资项目
			Object objPkWaItem = this.billModel.getValueAt(rowIndex,
					PsnappaproveBVO.PK_WA_ITEM);
			if (objPkWaItem != null) {
				this.itempk = objPkWaItem.toString();
				// 薪资标准
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
		// 人员编码发生变化
		else if (e.getKey().equalsIgnoreCase(PsnappaproveBVO.PK_PSNJOB)) {
			this.refpane = (UIRefPane) ((BillCellEditor) e.getSource())
					.getComponent();

			this.changePsncode(rowIndex);
		} else if (e.getKey().equalsIgnoreCase(PsnappaproveVO.TRANSTYPEID)) {
			// 设定交易类型PK
			setHeadItemValue(PsnappaproveVO.TRANSTYPEID,
					((UIRefPane) e.getSource()).getRefPK());

			// 设定交易类型 code。code不能总是“6301” ，要根据交易类型改变
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
			// 审批流
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
			// 2015-11-27 NCdp205550499 zhousze 如果没有选择薪资标准类别，那么薪资标准就不可编辑 begin
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
			// 2015-11-27 NCdp205550499 zhousze 如果没有选择薪资项目，那么薪资标准类别就不可编辑 begin
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
			// 审批流
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
	 * * 当便会列时需要更新参照和检查可编辑的列
	 */
	public void bodyRowChange(BillEditEvent billEditEvent) {
		int rowIndex = billEditEvent.getRow();
		// 薪资项目
		Object objPkWaItem = this.billModel.getValueAt(rowIndex,
				PsnappaproveBVO.PK_WA_ITEM);
		if (objPkWaItem != null) {
			this.itempk = objPkWaItem.toString();
			// 薪资标准
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
	 * 批量修改表体VO
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
		// 复制到表中
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
	 * 批量修改表体VO
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
		// 复制到表中
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
	 * 薪资标准变化
	 * 
	 * @author xuhw on 2009-12-21
	 * @param rowIndex
	 * @throws BusinessException
	 */
	private void changeCrtApply(int rowIndex) {
		// // 清空薪资标准等
		// String[] clearRow = new String[]
		// {
		// // 申请薪资
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
	 * 薪资标准变化
	 * 
	 * @author xuhw on 2009-12-21
	 * @param rowIndex
	 * @throws BusinessException
	 */
	private void changeCrtApply4Dia(int rowIndex, WaCriterionVO crtVO) {
		// 清空薪资标准等
		String[] clearRow = new String[] {
				// 申请薪资
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
	 * 薪资标准变化 审批
	 * 
	 * @author xuhw on 2009-12-21
	 * @param rowIndex
	 * @throws BusinessException
	 */
	private void changeCrtApprove(int rowIndex) {
		// // 清空薪资标准等
		// String[] clearRow = new String[]
		// {
		// // 确认薪资
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
	 * 薪资标准变化 审批
	 * 
	 * @author xuhw on 2009-12-21
	 * @param rowIndex
	 * @throws BusinessException
	 */
	private void changeCrtApprove4Dia(int rowIndex, WaCriterionVO crtVO) {
		// 清空薪资标准等
		String[] clearRow = new String[] {
				// 确认薪资
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
	 * 如果时谈判工资， 则档别级别等不可编辑
	 * 
	 * @param rowIndex
	 */
	private void changeNegotiationWage(int rowIndex) {
		/* 清除薪资标准类别显示名称和薪资标准类别pk */
		String[] neededClear = { PsnappaproveBVO.PK_WA_GRD,
				PsnappaproveBVO.PK_WA_GRD_SHOWNAME };
		for (String strKey : neededClear) {
			this.setBodyCellValue(null, rowIndex, strKey);
		}

		String[] neededClearKeys = {
				// 选择谈判工资后，薪资类别是否也应该清除，是否宽待薪酬就不会在变化
				// PsnappaproveBVO.PK_WA_GRD_SHOWNAME,
				// PsnappaproveBVO.PK_WA_GRD,
				// 申请薪资
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
		 * 定调资申请单上人员testA设置级别、档别，提交单据后，审批节点修改保存单据，
		 * 审批薪资默认带出了申请的级别、档别金额。然后在申请节点收回单据，修改申请薪资为谈判工资，
		 * 再次提交单据。审批节点的审批薪资仍显示未修改前的带出的级别、档别，且不能修改，该单据审批通过后，
		 * 在定调资信息维护节点查询时，记录谈判工资为Y，级别、档别也显示了内容。如单据：XC0711070002
		 */
		String[] neededClearKeys_cofom =
		// TODO 为什么在申请的时候清空确认的金额
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
	 * 人员发生变化时
	 * 
	 * @param rowIndex
	 */
	private void changePsncode(int rowIndex) {
		/*
		 * String clerkcode = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.CLERKCODE); String
		 * psncode = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.PSN_CODE); // TODO
		 * 参照以后可能要换为通用的，这里的Valuecode也要换 String psnname = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.PSN_NAME); // 设定部门 String
		 * deptname = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.DEPT_NAME); // 设定岗位
		 * String psotname = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.POST_NAME); // 设定人员类别
		 * String psnpcl = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.PK_PSNCL); // 设定岗位序列
		 * String jobName = (String)
		 * refpane.getRefValue(PsndocRefModelWithPower.JOB_NAME); // 设定职务 String
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

		// shenliangc 20140825 这段代码是合并补丁“职务名称多语显示错误”，
		// 结果参照PsndocRefModelWithPower中没有om_job.jobname字段，取值发生异常，
		// 导致后续代码不执行，产生残缺数据，引发一系列后续问题，先注掉，随后再设法解决。
		/*
		 * String jobName = (String)
		 * refpane.getRefValue("om_job.jobname"+MultiLangUtil
		 * .getCurrentLangSeqSuffix()); // 设定职务 String if
		 * (StringUtils.isEmpty(jobName)) { jobName = (String)
		 * refpane.getRefValue("om_job.jobname"); // 设定职务 String }
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
		// 清空薪资标准等
		String[] clearRow = new String[] {
				PsnappaproveBVO.PK_WA_ITEM_SHOWNAME,
				PsnappaproveBVO.PK_WA_ITEM,
				PsnappaproveBVO.PK_WA_GRD_SHOWNAME,
				PsnappaproveBVO.PK_WA_GRD,
				// 原薪资
				PsnappaproveBVO.PK_WA_CRT_OLD_SHOWNAME,
				PsnappaproveBVO.WA_OLD_MONEY,
				PsnappaproveBVO.WA_CRT_OLD_MONEY,
				PsnappaproveBVO.PK_WA_PRMLV_OLD,
				PsnappaproveBVO.PK_WA_SECLV_OLD,
				// 申请薪资
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
			// 清空薪资标准等
			String[] clearRow = new String[] {
					// 原薪资
					PsnappaproveBVO.PK_WA_CRT_OLD_SHOWNAME,
					PsnappaproveBVO.WA_OLD_MONEY,
					PsnappaproveBVO.WA_CRT_OLD_MONEY,
					PsnappaproveBVO.PK_WA_PRMLV_OLD,
					PsnappaproveBVO.PK_WA_SECLV_OLD,
					// 申请薪资
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

					// 2016-12-2 zhosuze 薪资加密：这里对定调资申请节点根据人员带出的定调资申请子表数据解密 begin
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
					// 更改表体的参照
					refreshCriterion(StrGrdPK, rowIndex);
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
					MessageDialog.showErrorDlg(getModel().getContext()
							.getEntranceUI(), null, ResHelper.getString(
							"60130adjapprove", "060130adjapprove0073")/*
																	 * @res
																	 * "查询到原始的金额失败,请查看薪资标准人员属性设定是否正确！"
																	 */);
					return;
				}
			}

			getBillCardPanel().getBillModel().getValueAt(0, 42);
		}
	}

	/**
	 * 薪资项目发生变化
	 * 
	 * @param rowIndex
	 */
	private void changeWaItem(int rowIndex) {
		itempk = refpane.getRefModel().getPkValue();
		String itempkNow = (String) getBodyCellValue(rowIndex,
				PsnappaproveBVO.PK_WA_ITEM);
		if (!itempk.equals(itempkNow)) {
			// 清空薪资标准等
			String[] clearRow = new String[] {
					PsnappaproveBVO.PK_WA_GRD_SHOWNAME,
					PsnappaproveBVO.PK_WA_GRD,
					// 原薪资
					PsnappaproveBVO.PK_WA_CRT_OLD_SHOWNAME,
					PsnappaproveBVO.WA_OLD_MONEY,
					PsnappaproveBVO.WA_CRT_OLD_MONEY,
					PsnappaproveBVO.PK_WA_PRMLV_OLD,
					PsnappaproveBVO.PK_WA_SECLV_OLD,
					// 申请薪资
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
			// 记住薪资项目主键
			setBodyCellValue(itempk, rowIndex, PsnappaproveBVO.PK_WA_ITEM);
			// itempk = (String) billModel.getValueAt(rowIndex,
			// PsnappaproveBVO.PK_WA_ITEM);
			// 初始化薪资标准参照
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
	 * 根据列的字段找到对应的列的INDEX
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
	 * 得到表体的字段信息
	 * 
	 * @param strKey
	 * @return
	 */
	public Object getBodyCellObject(String strKey) {
		return getBillCardPanel().getBodyItem(strKey).getComponent();
	}

	/**
	 * 得到主表中的字段值
	 */
	public Object getBodyCellValue(int rowIndex, String strKey) {
		return getBillCardPanel().getBodyPanel().getTableModel()
				.getValueAt(rowIndex, strKey);
	}

	/**
	 * 对表头进行分组
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
		// 进行多表头的设定
		for (int i = 0; i < cm.getColumnCount(); i++) {
			// 循环需要设置的表头
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
	 * 得到表头的字段信息
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
					"060130adjapprove0140")/* @res "兼职" */);
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
	 * 把分组的字段放到一个map中
	 * 
	 * @return
	 */
	private List<String[]> getTableHeadInfo() {
		if (headlist == null) {
			headlist = new ArrayList<String[]>();
			// 原薪资的表头
			String strOlds[] = new String[] {
					ResHelper.getString("60130adjapprove",
							"060130adjapprove0074")/* @res "原薪资" */,
					PsnappaproveBVO.PK_WA_CRT_OLD_SHOWNAME,
					PsnappaproveBVO.WA_OLD_MONEY,
					PsnappaproveBVO.WA_CRT_OLD_MONEY };
			// 申请薪资
			String strApplys[] = new String[] {
					ResHelper.getString("60130adjapprove",
							"060130adjapprove0075")/* @res "申请薪资" */,
					PsnappaproveBVO.NEGOTIATION,
					PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME,
					PsnappaproveBVO.WA_APPLY_MONEY,
					PsnappaproveBVO.WA_CRT_APPLY_MONEY,
					PsnappaproveBVO.PK_CHANGECAUSE
			/* PsnappaproveBVO.APPLYREASON */};
			// 审批薪资
			String strCofm[] = new String[] {
					ResHelper.getString("60130adjapprove",
							"060130adjapprove0076")/* @res "审批薪资" */,
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
		 * 为了减少查询流量连接数，在model中增加isAutoExecLoadFormula来标识是否自动加载公式
		 * 例如：在查询时由于列表界面并没有涉及到子表中的公式字段，所以可以将isAutoExecLoadFormula设置成false
		 */
		// 2015-12-3 NCdp205551130 zhousze
		// 解决定调资审批节点，首次进入直接修改“薪资项目”等公式字段不显示问题。该问题由于
		// 初始化界面数据时，已经不自动加载公式，所以在model中存的数据就没有加载出来，点击修改的时候取的数据就不对了。现在将
		// 该部分注掉确保流程没问题。 begin
		// super.setAutoExecLoadFormula(((WaAdjustAppModel)getModel()).isAutoExecLoadFormula());
		// end
		super.handleEvent(event);
		// }
		// end
		// end
		// 2015-12-3 NCdp205551130 zhousze
		// 解决定调资审批节点，首次进入直接修改“薪资项目”等公式字段不显示问题。该问题由于
		// 初始化界面数据时，已经不自动加载公式，所以在model中存的数据就没有加载出来，点击修改的时候取的数据就不对了。现在将
		// 该部分注掉确保流程没问题。 begin
		// Object obj = getModel().getSelectedData();
		// if(AppEventConst.SELECTION_CHANGED.equalsIgnoreCase(event.getType())
		// && obj !=null &&
		// !((WaAdjustAppModel)getModel()).isAutoExecLoadFormula()){
		// ((WaAdjustAppModel)getModel()).setAutoExecLoadFormula(true);
		// }
		// end
		UIState state = getModel().getUiState();
		if (AppEventConst.MODEL_INITIALIZED.equalsIgnoreCase(event.getType())) {
			// 由于上面的代码使初始化表头失效，所以在这里初始化多表头
			// 初始化页面上的多表头信息
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
					// 自动编码， 单据编码不可编辑
					setHeadItemEnabled(PsnappaproveVO.BILLCODE, false);
				}
			}
			// 如果是审批
			// 自动填写表中的数据
			else if (!isInputPnl) {
				// 批复日期
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
			// 是否通过checkbox的显示控制
			Integer strConfirmState = (Integer) getHeadCellValue(PsnappaproveBVO.CONFIRMSTATE);
			if (state != UIState.NOT_EDIT) {
				// 2015-08-12 zhousze 流程类型根据当前审批方式是否可见
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

			// 审批未通过 确认中自由 提交态的单据checkbox可见
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
	 * 获得审批方式 3提交即审批/2直批/1审批流/0系统判断 Created on 2015-08-12
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
	 * 获取项目，主要是用于对流程类型根据审批方式设置是否可编辑 Created on 2015-08-12
	 * 
	 * @param strKey
	 * @return
	 * @author zhousze
	 */
	private BillItem getHeadItem(String strKey) {
		return getBillCardPanel().getHeadItem(strKey);
	}

	/**
	 * 初始化页面信息，针对页面上的多表头的形式
	 */
	private void initPageInfo() {
		// 设定多表头
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

		// 初始化表头业务类型参照
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

	/** 初始化UI */
	@Override
	public void initUI() {
		super.initUI();

		/*
		 * 单元格是否必输
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

		// /*平台公式编辑器没有做到多语，这里特殊处理一下*/
		// String loadFormula =
		// (getBillCardPanel().getBodyItem(PsnappaproveBVO.PK_WA_ITEM_SHOWNAME).getLoadFormula())[0];
		// String langName = ",name" + MultiLangUtil.getCurrentLangSeqSuffix();
		// loadFormula = loadFormula.replaceAll(",name", langName);
		// getBillCardPanel().getBodyItem(PsnappaproveBVO.PK_WA_ITEM_SHOWNAME).setLoadFormula(new
		// String[]{loadFormula});

		getBillCardPanel().addEditListener(this);
		getBillCardPanel().setBillBeforeEditListenerHeadTail(this);
		getBillCardPanel().getBodyPanel().addEditListener2(this);
		// 过滤未转档人员
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
		// 解决定调资审批节点，首次进入直接修改“薪资项目”等公式字段不显示问题。该问题由于
		// 初始化界面数据时，已经不自动加载公式，所以在model中存的数据就没有加载出来，点击修改的时候取的数据就不对了。现在将
		// 该部分注掉确保流程没问题。 begin
		// 审批节点由于可能存在数据会自动加载公式，在这里暂时显示设置为不自动加载
		// if(((PFAppModel)getModel()).isApproveSite()){
		// ((WaAdjustAppModel)getModel()).setAutoExecLoadFormula(false);
		// }
		// end
		// 2015-11-26 NCdp205550467 zhousze 去掉定调资申请卡片界面字表右键菜单不显示的问题 begin
		getBillCardPanel().getBodyPanel().setBBodyMenuShow(false);
		// 20151214 shenliangc NCdp205559130 薪资规则，新增一行后，填入数据用回车换行，第二行行号为空，保存时报错
		// 去掉回车增行、子表表体右键。
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
	 * 设置 谈判工资相关项目的可用性
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
	 * 更改表体参照
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
		// 是否多选
		isMultsecCkeck = false;
		isRangeCkeck = false;

		if (wagradevo.getIsmultsec() != null) {
			isMultsecCkeck = wagradevo.getIsmultsec().booleanValue();
		}

		// 是否宽带薪酬

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
	 * 刷新标准类别参照
	 * 
	 * @author xuhw
	 * @param colKey
	 * @param refModel
	 */
	private void refreshGradeRef(String strColKey, AbstractRefModel refModel) {
		// 根据列的字段找到对应的列的INDEX
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
	 * 审批节点时 进行界面项目的相关设置
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

		// 单据编码
		setHeadItemEnabled(PsnappaproveVO.BILLCODE, isEditaAble);
		// 单据名称
		setHeadItemEnabled(PsnappaproveVO.BILLNAME, isEditaAble);
		// 审批状态
		setHeadItemEnabled(PsnappaproveVO.CONFIRMSTATE, isEditaAble);
		// 业务类型
		setHeadItemEnabled(PsnappaproveVO.PK_BUSITYPE, isEditaAble);
		// 流程类型
		setHeadItemEnabled(PsnappaproveVO.TRANSTYPEID, isEditaAble);
		// 批复日期
		getBillCardPanel().getTailItem(PsnappaproveVO.CONFIRMDATE).setEdit(
				isEditaAble);
		// // 变动原因
		// setHeadItemEnabled(PsnappaproveVO.PK_CHANGECAUSE, isEditaAble);
		// 申请人
		setHeadItemEnabled(PsnappaproveVO.OPERATOR, isEditaAble);
		// 依据文件
		setHeadItemEnabled(PsnappaproveVO.VBASEFILE, isEditaAble);

		// 依据文件
		getBillCardPanel().getBodyItem(PsnappaproveBVO.WA_COFM_MONEY).setNull(
				isEditaAble);

		getBillCardPanel().getBodyItem(PsnappaproveBVO.WA_COFM_MONEY).setNull(
				true);
		// 申请日期
		setHeadItemEnabled(PsnappaproveVO.APPLYDATE, isEditaAble);
	}

	public void setApproveTabActions(List<NCAction> approveTabActions) {
		this.approveTabActions = approveTabActions;
	}

	/**
	 * 对主表的字段进行赋值
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
	 * 对主表的字段进行赋值
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
	 * 表体是否可以编辑
	 * 
	 * @param colKey
	 * @param edit
	 */
	private void setBodyItemEnabled(String colKey, boolean edit) {
		getBillCardPanel().getBodyItem(colKey).setEnabled(edit);
	}

	/**
	 * 初始化参照
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
	 * 设置单元格能否编辑
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
	 * 复制准备数据
	 * 
	 * @author xuhw on 2010-4-28
	 * @throws BusinessException
	 */
	private void setCopyStates() {

		UIRefPane transtypeRefpane = (UIRefPane) getBillCardPanel()
				.getHeadItem(PsnappaproveVO.TRANSTYPEID).getComponent();

		// 设定交易类型 code。code不能总是“6301” ，要根据交易类型改变
		setHeadItemValue(PsnappaproveVO.TRANSTYPE,
				transtypeRefpane.getRefCode());

		getBillCardPanel().getHeadItem(PsnappaproveVO.CONFIRMSTATE).setValue(
				IPfRetCheckInfo.NOSTATE);
		// 审批状态 设为自由态
		if (((WaAdjustAppModel) getModel()).getAutoGenerateBillCode()) {// 单据编码
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
				// 自动编码， 单据编码不可编辑
				setHeadItemEnabled(PsnappaproveVO.BILLCODE, false);
			}
		} else {
			getBillCardPanel().getHeadItem(PsnappaproveVO.BILLCODE).setValue(
					null);
		}
		getBillCardPanel().getHeadItem(PsnappaproveVO.OPERATOR).setValue(
				PubEnv.getPk_user());// 申请人
		getBillCardPanel().getHeadItem(PsnappaproveVO.APPLYDATE).setValue(
				PubEnv.getServerDate());// 申请日期
		getBillCardPanel().getHeadItem(PsnappaproveVO.USEDATE).setValue(null);// 生效日期
		getBillCardPanel().getHeadItem(PsnappaproveVO.APPROVE_NOTE).setValue(
				null);// 审批意见
		getBillCardPanel().getTailItem(PsnappaproveVO.CONFIRMDATE).setValue(
				null);// 确认日期
		getBillCardPanel().getHeadItem(PsnappaproveVO.SUM_CONFIM_MONEY)
				.setValue(null);// 审批金额
		getBillCardPanel().getTailItem(PsnappaproveVO.APPROVER).setValue(null);// 生效日期

		// 创建人
		getBillCardPanel().getTailItem(PsnappaproveVO.CREATOR).setValue("");
		// 创建时间
		getBillCardPanel().getTailItem(PsnappaproveVO.CREATIONTIME)
				.setValue("");

		// 修改人
		getBillCardPanel().getTailItem(PsnappaproveVO.MODIFIER).setValue("");
		// 修改时间
		getBillCardPanel().getTailItem(PsnappaproveVO.MODIFIEDTIME)
				.setValue("");

		// 查询到原始的金额
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
													 * "查询到原始的金额失败,请查看薪资标准人员属性设定是否正确！"
													 */);
			return;
		}
		for (int i = 0; i < billModel.getRowCount(); i++) {
			// 确认信息
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
					// 自动编码， 单据编码不可编辑
					setHeadItemEnabled(PsnappaproveVO.BILLCODE, false);
				}
				// 自动编码， 单据名称默认同编码
				Object billCodeObject = getHeadCellValue(PsnappaproveVO.BILLCODE);
				setHeadCellValue(billCodeObject, PsnappaproveVO.BILLNAME);
			}
			// 加上申请日期
			setHeadCellValue(PubEnv.getServerDate(), PsnappaproveVO.APPLYDATE);
			setHeadCellValue(ISalaryadjmgtConstant.BillTYPE_ENTRY,
					PsnappaproveVO.TRANSTYPE);
			setHeadCellValue(((WaAdjustAppModel) getModel()).getBillType(),
					PsnappaproveVO.BILLTYPE);
		}
	}

	/**
	 * 设置表头参照的值
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
	 * 设置表头项目值
	 * 
	 * @param value
	 * @param strKey
	 */
	public void setHeadCellValue(Object value, String strKey) {
		getBillCardPanel().getHeadItem(strKey).setValue(value);
	}

	/**
	 * 表头是否可以编辑
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
	 * 设置表头项目值
	 * 
	 * @param value
	 * @param strKey
	 */
	public void setTailCellValue(Object value, String strKey) {
		getBillCardPanel().getTailItem(strKey).setValue(value);
	}

	/**
	 * 根据申请的项目更新审批的数据
	 * 
	 * @param rowIndex
	 */
	private void updateApproveData(int rowIndex) {
		if (billModel.getValueAt(rowIndex, PsnappaproveBVO.WA_COFM_MONEY) == null
				|| billModel
						.getValueAt(rowIndex, PsnappaproveBVO.WA_COFM_MONEY)
						.toString().length() == 0) {
			// 默认申请通过
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