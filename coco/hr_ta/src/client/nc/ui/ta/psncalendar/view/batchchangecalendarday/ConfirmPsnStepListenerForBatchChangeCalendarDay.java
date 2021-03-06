package nc.ui.ta.psncalendar.view.batchchangecalendarday;


import nc.bs.framework.common.NCLocator;
import nc.itf.ta.IPsnCalendarQueryMaintain;
import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.pub.beans.wizard.IWizardStepListener;
import nc.ui.pub.beans.wizard.WizardStepEvent;
import nc.ui.pub.beans.wizard.WizardStepException;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.ta.psncalendar.view.batchchange.SelPsnPanelForBatchChange;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFLiteralDate;

public class ConfirmPsnStepListenerForBatchChangeCalendarDay implements IWizardStepListener {

	@Override
	public void stepActived(WizardStepEvent event) throws WizardStepException {
		Debug.debug("multiselect step will be actived");
		ConfirmPsnStepForBatchChangeCalendarDay currStep = (ConfirmPsnStepForBatchChangeCalendarDay)event.getStep();
		HRWizardModel model = (HRWizardModel)currStep.getModel();
		if(!(model.getStepWhenAction() instanceof SelPsnStepForBatchChangeCalendarDay))
			return;
		SelPsnStepForBatchChangeCalendarDay selPsnStep = (SelPsnStepForBatchChangeCalendarDay)model.getSteps().get(0);
		SelPsnPanelForBatchChange selPsnPanel = selPsnStep.getSelPsnPanelForBatchChangeCalendarDay();
		FromWhereSQL fromWhereSQL =  selPsnPanel.getQuerySQL();
		
		UFLiteralDate beginDate = selPsnPanel.getBeginDate();
		UFLiteralDate endDate = selPsnPanel.getEndDate();
		IPsnCalendarQueryMaintain queryService = NCLocator.getInstance().lookup(IPsnCalendarQueryMaintain.class);
		PsnJobVO[] vos = null;
		try {
			boolean isHROrg = !selPsnPanel.isNeedBURef();
			String pk_org=isHROrg?model.getModel().getContext().getPk_org():selPsnPanel.getPK_BU();
			vos = queryService.queryPsnJobVOsByConditionAndOverrideOrg(pk_org, fromWhereSQL, beginDate, endDate,true,isHROrg);
			//vos = queryService.queryPsnJob(pk_org, fromWhereSQL, true,isHROrg);
		} catch (BusinessException e) {
			Debug.error(e.getMessage(), e);
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
		currStep.getConfirmPsnPanelForBatchChangeCalendarDay().setFormVOs(vos);
	}

	@Override
	public void stepDisactived(WizardStepEvent event)
			throws WizardStepException {

	}

}
