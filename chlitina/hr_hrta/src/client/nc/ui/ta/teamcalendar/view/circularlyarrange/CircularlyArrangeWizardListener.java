package nc.ui.ta.teamcalendar.view.circularlyarrange;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.StringPiecer;
import nc.itf.ta.IPsnCalendarManageValidate;
import nc.itf.ta.ITeamCalendarManageMaintain;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.wizard.IWizardDialogListener;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.ui.ta.teamcalendar.model.TeamCalendarAppModel;
import nc.vo.bd.team.team01.entity.TeamHeadVO;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.teamcalendar.TeamInfoCalendarVO;

public class CircularlyArrangeWizardListener implements IWizardDialogListener {

	@Override
	public void wizardFinish(WizardEvent event) throws WizardActionException {
		HRWizardModel wizardModel = (HRWizardModel)event.getModel();
		//ȡ�����ڷ�Χ
		SelectTeamStep step1 = (SelectTeamStep)wizardModel.getSteps().get(0);
		SelectTeamPanel panel1 = step1.getTeamPanel();
		UFLiteralDate beginDate = panel1.getBeginDate();
		UFLiteralDate endDate = panel1.getEndDate();
		String pk_org = panel1.getPK_BU();
		//ȡ�����Ƿ񸲸����а�Ρ�
		boolean isOverride = panel1.isOverride();
		//ȡ����������
		ConfirmTeamStep step2 = (ConfirmTeamStep)wizardModel.getSteps().get(1);
		ConfirmTeamPanel panel2 = step2.getTeamPanel();
		TeamHeadVO[] teamVOs = panel2.getSelTeamVOs();
		String[] pk_teams = StringPiecer.getStrArray(teamVOs, TeamHeadVO.CTEAMID);
		//ȡ���������Ű�ȡ����־
		ShiftSetStep step3 = (ShiftSetStep)wizardModel.getSteps().get(2);
		ShiftSetPanel panel3 = step3.getShiftSetPanel();
		boolean isHolidayCancel = panel3.isHolidayCancel();
		//ȡ���������
		String[] pk_shifts = panel3.getCircularPks();
		ITeamCalendarManageMaintain manageMaintain = NCLocator.getInstance().lookup(ITeamCalendarManageMaintain.class);
		IPsnCalendarManageValidate manageValidate = NCLocator.getInstance().lookup(IPsnCalendarManageValidate.class);
		TeamInfoCalendarVO[] calendarVOs = null;
		//��ȡ������֯
		try {
			String[] orgs = { wizardModel.getModel().getContext().getPk_org() };
			String legal_pk_org = LegalOrgUtilsEX.getLegalOrgByOrgs(orgs)
					.get(wizardModel.getModel().getContext().getPk_org());
			//IHRHolidayQueryService
			//��ҳ����һ��������
			List<List<String>> strMessage = manageValidate.teamvalidate(wizardModel.getModel().getContext().getPk_org(),pk_teams, beginDate, endDate, pk_shifts);
			UFBoolean isStrcheck = SysInitQuery.getParaBoolean(legal_pk_org, "TWHRT03");
			if(null != strMessage ){
				for(List<String> strs : strMessage){
					if(isStrcheck.booleanValue()){
						//�ϸ�У�飨�д�����ȡ�����棩
						MessageDialog.showHintsDlg(wizardModel.getModel().getContext().getEntranceUI(), "У��", "����Ա��"+strs);
						return;
					} else {
						//���ϸ�У�飨ҳ�浯��һ���������û���Щ�˲�����һ��һ�ݣ�Ȼ�û��Լ�ѡ���Ƿ�������棩
						if( 2 == MessageDialog.showOkCancelDlg(wizardModel.getModel().getContext().getEntranceUI(), "У��", "����Ա��"+strs+"�Ƿ��������?")){
							return;
						}

					}
				}

			}

			calendarVOs = manageMaintain.circularArrange(pk_org, pk_teams, beginDate, endDate, pk_shifts,isHolidayCancel, isOverride,true);
		} catch (BusinessException e) {
			Debug.error(e.getMessage(), e);
			WizardActionException wae = new WizardActionException(e);
			wae.addMsg("1", e.getMessage());
			throw wae;
		}
		TeamCalendarAppModel appModel = (TeamCalendarAppModel)wizardModel.getModel();
		appModel.setBeginEndDate(beginDate, endDate);
		appModel.initModel(calendarVOs);
		event.getModel().gotoStepForwardNoValidate(0);
	}

	@Override
	public void wizardCancel(WizardEvent event) throws WizardActionException {}
	@Override
	public void wizardFinishAndContinue(WizardEvent event)throws WizardActionException {}

}