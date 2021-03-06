package nc.ui.wa.taxupgrade_tool.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.PubEnv;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.uif2.UIState;
import nc.ui.wa.taxupgrade_tool.model.Taxupgrade_toolAppModel;
import nc.ui.wa.taxupgrade_tool.model.Taxupgrade_toolModelService;
import nc.ui.wa.taxupgrade_tool.view.Taxupgrade_toolForm;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa_tax.TaxupgradeDef;

/**
 * 升级专项附加扣除
 *
 * @author: xuhw
 */
public class Taxupgrade_toolDeductionUpgradeAction extends HrAction {

	private static final long serialVersionUID = -8050698726088941767L;
	private static final String type = "0";
	private Taxupgrade_toolForm editor;

	public Taxupgrade_toolForm getEditor() {
		return editor;
	}

	public void setEditor(Taxupgrade_toolForm editor) {
		this.editor = editor;
	}

	public Taxupgrade_toolDeductionUpgradeAction() {
		super();
		putValue(INCAction.CODE, "Taxupgrade_toolDeductionUpgradeAction");
		setBtnName("升级专项附加扣除-累计方式");
		putValue(Action.SHORT_DESCRIPTION, "升级专项附加扣除-累计方式");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		GeneralVO[] vos = getEditor().getSelectedClassVOs();
		WaItemVO[] itemvos = getEditor().getTaxItems();
		itemvos = getEditor().getTaxItems();
		((Taxupgrade_toolModelService)getModel().getService()).taxUpgradeToSelectClass(PubEnv.getPk_group(), itemvos, vos, TaxupgradeDef.OPTYPE_UPGRADE_SPECIALDEDUCTION);
		getEditor().initUI();
	}

	@Override
	protected boolean isActionEnable() {
		return super.isActionEnable() && getModel().getContext().getPk_org() != null;
	}

	public Taxupgrade_toolAppModel getModel(){
		return (Taxupgrade_toolAppModel)super.getModel();
	}

}