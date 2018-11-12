package nc.ui.wa.psndocwadoc.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.hr.utils.ResHelper;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.hr.uif2.validator.ErrorWizardExceptionHandler;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.wizard.WizardDialog;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel;
import nc.ui.wa.psndocwadoc.view.labourjoin.ConfirmPsnStepForAddJoin;
import nc.ui.wa.psndocwadoc.view.labourjoin.JoinSetStepForAddJoin;
import nc.ui.wa.psndocwadoc.view.labourjoin.SelPsnStepForAddJoin;
import nc.ui.wa.psndocwadoc.view.labourjoin.WizardListenerForAddJoin;
import nc.vo.pub.BusinessException;
import nc.vo.ta.PublicLangRes;
import nc.ui.hr.uif2.action.HrAction;


public class AddJoinAction extends HrAction {
	
	/**
	 *
	 */
	private static final long serialVersionUID = -6671656249456854266L;

	WizardDialog dialog = null;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		if(UIDialog.ID_OK!=getDialog().showModal()){
			putValue(MESSAGE_AFTER_ACTION, PublicLangRes.CANCELED());
			return;
		}
	}

	public AddJoinAction() {
		super();
//		String name = ResHelper.getString("common","UC001-0000085")
		String name = "加保"
/*@res "批改"*/;
		setBtnName(name);
		setCode("Addjoin");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('I', Event.CTRL_MASK));
		putValue(Action.SHORT_DESCRIPTION, name+"(Ctrl+I)");
	}

	private WizardDialog getDialog() throws BusinessException{
		if(dialog==null){
			List<WizardStep> stepList = new ArrayList<WizardStep>();

			SelPsnStepForAddJoin step1 = new SelPsnStepForAddJoin();
			step1.setAppModel((PsndocwadocAppModel)getModel());
			step1.init();
			stepList.add(step1);

			ConfirmPsnStepForAddJoin step2 = new ConfirmPsnStepForAddJoin();
			step2.init();
			stepList.add(step2);

			JoinSetStepForAddJoin step3 = new JoinSetStepForAddJoin();
			step3.setAppModel(getModel());
			step3.init();
			stepList.add(step3);

			HRWizardModel wizardModel = new HRWizardModel(stepList);
			wizardModel.setModel(getModel());
			dialog = new WizardDialog(getContext().getEntranceUI(), wizardModel, stepList, null){

				/**
				 *
				 */
				private static final long serialVersionUID = 2834883786608019372L;

				/*
				 * 打开对话框后，重新加载第一个界面的数据（如果是第一次打开对话框，则已经加载过数据了，不需要重复加载）
				 * (non-Javadoc)
				 * @see nc.ui.pub.beans.UIDialog#showModal()
				 */
				@Override
				public int showModal() {
					return super.showModal();
				};
			};
			dialog.setWizardDialogListener(new WizardListenerForAddJoin());
			dialog.setSize(900, 600);
			dialog.setResizable(false);
			dialog.setWizardExceptionHandler(new ErrorWizardExceptionHandler(dialog));
		}

		return dialog;
	}

	
	
	
	
	
	
	
	/*public AddJoinDialog getDialog() {
		if(dialog==null){
			dialog = new AddJoinDialog(getContext().getEntranceUI());
			dialog.setModel((PsnCalendarAppModel)getModel());
			dialog.initUI();
		}
		return dialog;
	}
*/
	@Override
	protected boolean isActionEnable() {
		return super.isActionEnable();
//		UIState state = getModel().getUiState();
//		return (state==UIState.INIT||state==UIState.NOT_EDIT)&&!StringUtils.isEmpty(getModel().getContext().getPk_org());
	}

}
