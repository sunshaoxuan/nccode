package nc.ui.hrwa.sumincometax.action;

import java.awt.event.ActionEvent;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pubapp.uif2app.view.ShowUpableBillForm;
import nc.ui.uif2.components.IAutoShowUpComponent;
import nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO;

/**
 * 
 * @author ward.wong
 * @date 20180126
 * @version v1.0
 * @功能描述 申报明细档汇总单据修改按钮
 *
 */
public class EditAction extends nc.ui.pubapp.uif2app.actions.EditAction {

	
	private static final long serialVersionUID = 8503495844116846886L;
	
	private IAutoShowUpComponent showUpComponent;
	
	private static String DF9A="1001ZZ1000000001NEGQ";
	private static String DF9B="1001ZZ1000000001NEGR";
	private static String DF92="1001ZZ1000000001NEGT";

	protected boolean isActionEnable() {
		boolean isEnable = super.isActionEnable();
		if ((isEnable) && (getModel().getSelectedData() != null)) {
			AggSumIncomeTaxVO aggvo = (AggSumIncomeTaxVO) getModel()
					.getSelectedData();
			if (!aggvo.getParentVO().getIsdeclare().booleanValue()) {
				return true;//只允许修改未被注记的单据
			}
		}
		return false;
	}
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
		getShowUpComponent().showMeUp();//进入卡片界面
		BillCardPanel panel = ((ShowUpableBillForm)getShowUpComponent()).getBillCardPanel();
		AggSumIncomeTaxVO aggvo = (AggSumIncomeTaxVO) getModel().getSelectedData();
		String declaretype = aggvo.getParentVO().getDeclaretype();
		if(DF9A.equals(declaretype)){
			panel.getHeadItem("businessno").setEnabled(true);//业务代号
			panel.getHeadItem("costno").setEnabled(false);//费用别代号
			panel.getHeadItem("projectno").setEnabled(false);//项目代号
		}else if(DF9B.equals(declaretype)){
			panel.getHeadItem("businessno").setEnabled(false);//业务代号
			panel.getHeadItem("costno").setEnabled(true);//费用别代号
			panel.getHeadItem("projectno").setEnabled(false);//项目代号
		}else if(DF92.equals(declaretype)){
			panel.getHeadItem("businessno").setEnabled(false);//业务代号
			panel.getHeadItem("costno").setEnabled(false);//费用别代号
			panel.getHeadItem("projectno").setEnabled(true);//项目代号
		}else{
			panel.getHeadItem("businessno").setEnabled(false);//业务代号
			panel.getHeadItem("costno").setEnabled(false);//费用别代号
			panel.getHeadItem("projectno").setEnabled(false);//项目代号
		}
	}
	public IAutoShowUpComponent getShowUpComponent() {
		return showUpComponent;
	}
	public void setShowUpComponent(IAutoShowUpComponent showUpComponent) {
		this.showUpComponent = showUpComponent;
	}
	
	
}
