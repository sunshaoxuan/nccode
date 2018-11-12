package nc.ui.ta.teamcalendar.view.batchchangedaytype;


import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepException;
import nc.ui.ta.teamcalendar.view.batchchange.SelectTeamPanelForBatchChange;
import nc.vo.logging.Debug;

public class ShiftSetStepListenerForBatchChangeDayType implements IWizardStepListener {

	@Override
	public void stepActived(WizardStepEvent event) throws WizardStepException {
		Debug.debug("shift set step will be actived");
		ShiftSetStepForBatchChangeDayType currStep = (ShiftSetStepForBatchChangeDayType)event.getStep();
		HRWizardModel model = (HRWizardModel)currStep.getModel();
		if(!(model.getStepWhenAction() instanceof ConfirmTeamStepForBatchChangeDayType))
			return;
		SelectTeamStepForBatchChangeDayType selectTeamStep = (SelectTeamStepForBatchChangeDayType)model.getSteps().get(0);
		SelectTeamPanelForBatchChange selectTeamPanel = selectTeamStep.getTeamPanelForBatchChangeDayType();
		String pk_org = selectTeamPanel.getPK_BU();
		//设置班次参照的pk_org，使其只参照出第一步选择的业务单元的班次
		ShiftSetPanelForBatchChangeDayType panel = currStep.getShiftSetPanelForBatchChangeDayType();
		panel.setPK_BU(pk_org);
	}

	@Override
	public void stepDisactived(WizardStepEvent event)
			throws WizardStepException {
		// TODO Auto-generated method stub

	}

}
