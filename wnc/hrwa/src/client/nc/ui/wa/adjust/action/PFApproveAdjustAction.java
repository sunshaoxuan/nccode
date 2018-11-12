package nc.ui.wa.adjust.action;

import java.awt.event.ActionEvent;

import nc.itf.hr.wa.IHRWADataResCode;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.ui.hr.pf.action.PFApproveAction;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.editor.IEditor;
import nc.ui.wa.adjust.view.WaAdjustCardForm;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.adjust.AggPsnappaproveVO;
import nc.vo.wa.adjust.PsnappaproveBVO;

/**
 * 定调资 审批
 * 
 * @author: xuhw
 * @date: 2009-12-18 下午03:51:47
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class PFApproveAdjustAction extends PFApproveAction {
	/**
	 * @author xuhw on 2010-4-28
	 */
	private static final long serialVersionUID = 201004250808L;

	private IEditor editor;

	@Override
	// 审批流框架中没有对更新的审批状态持久化，这里重写了acticon
	public void doAction(ActionEvent evt) throws Exception {
		Object objValue = getModel().getSelectedData();

		if (objValue == null) {
			return;
		}
		checkDataPermission();

		AggPsnappaproveVO billvo = (AggPsnappaproveVO) objValue;
		PsnappaproveBVO[] psnappaproveBVOs = (PsnappaproveBVO[]) billvo
				.getChildrenVO();
		// 2016-12-08 zhousze 薪资加密：这里处理审批数据加密 begin
		for (PsnappaproveBVO psnappaproveBVO : psnappaproveBVOs) {
			psnappaproveBVO
					.setWa_old_money(psnappaproveBVO.getWa_old_money() == null ? null
							: new UFDouble(SalaryEncryptionUtil
									.encryption(psnappaproveBVO
											.getWa_old_money().toDouble())));
			psnappaproveBVO
					.setWa_cofm_money(psnappaproveBVO.getWa_cofm_money() == null ? null
							: new UFDouble(SalaryEncryptionUtil
									.encryption(psnappaproveBVO
											.getWa_cofm_money().toDouble())));
			psnappaproveBVO.setWa_apply_money(psnappaproveBVO
					.getWa_apply_money() == null ? null : new UFDouble(
					SalaryEncryptionUtil.encryption(psnappaproveBVO
							.getWa_apply_money().toDouble())));
			psnappaproveBVO.setWa_crt_apply_money(psnappaproveBVO
					.getWa_crt_apply_money() == null ? null : new UFDouble(
					SalaryEncryptionUtil.encryption(psnappaproveBVO
							.getWa_crt_apply_money().toDouble())));
			psnappaproveBVO.setWa_crt_cofm_money(psnappaproveBVO
					.getWa_crt_cofm_money() == null ? null : new UFDouble(
					SalaryEncryptionUtil.encryption(psnappaproveBVO
							.getWa_crt_cofm_money().toDouble())));
			psnappaproveBVO.setWa_crt_old_money(psnappaproveBVO
					.getWa_crt_old_money() == null ? null : new UFDouble(
					SalaryEncryptionUtil.encryption(psnappaproveBVO
							.getWa_crt_old_money().toDouble())));
		}
		// end
		int i = 0;
		for (PsnappaproveBVO psnappaproveBVO : psnappaproveBVOs) {
			psnappaproveBVO.setPsnname((((WaAdjustCardForm) this.getEditor())
					.getBillCardPanel().getBodyValueAt(i,
					"pk_psnjob.pk_psndoc.name")).toString());
			psnappaproveBVO
					.setPk_wa_item_showname((String) (((WaAdjustCardForm) this
							.getEditor()).getBillCardPanel().getBodyValueAt(
							i++, PsnappaproveBVO.PK_WA_ITEM_SHOWNAME)));
			// psnappaproveBVO.setPk_wa_item_showname("基本月工资");
		}

		billvo.setChildrenVO(psnappaproveBVOs);
		super.doAction(evt);
		putValue(HrAction.MESSAGE_AFTER_ACTION,
				IShowMsgConstant.getApproveSuccessInfo());
	}

	public PFApproveAdjustAction() {
		super();
		setResourceCode(IHRWADataResCode.ADJUST);
	}

	public IEditor getEditor() {
		return editor;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}

}
