package nc.ui.ta.psncalendar.view.batchchangecalendarday;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.ta.IPsnCalendarManageMaintain;
import nc.itf.ta.IPsnCalendarManageValidate;
import nc.itf.ta.IPsnCalendarQueryMaintain;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.hr.uif2.model.HRWizardModel;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.wizard.IWizardDialogListener;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.ui.ta.psncalendar.model.PsnCalendarAppModel;
import nc.ui.ta.psncalendar.view.batchchange.SelPsnPanelForBatchChange;
import nc.vo.logging.Debug;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;

public class WizardListenerForBatchChangeCalendarDay implements IWizardDialogListener {
	private String pk_org = null;
	private String[] pk_psndocs = null;
	private WizardEvent event = null;

	@Override
	public void wizardCancel(WizardEvent event) throws WizardActionException {

	}

	@Override
	public void wizardFinish(WizardEvent event) throws WizardActionException {
		this.event = event;
		HRWizardModel wizardModel = (HRWizardModel) event.getModel();

		SelPsnStepForBatchChangeCalendarDay step1 = (SelPsnStepForBatchChangeCalendarDay) wizardModel.getSteps().get(0);
		SelPsnPanelForBatchChange panel1 = step1.getSelPsnPanelForBatchChangeCalendarDay();
		this.pk_org = panel1.getPK_BU();
		// ȡ����Ա����
		ConfirmPsnStepForBatchChangeCalendarDay step2 = (ConfirmPsnStepForBatchChangeCalendarDay) wizardModel
				.getSteps().get(1);
		ConfirmPsnPanelForBatchChangeCalendarDay panel2 = step2.getConfirmPsnPanelForBatchChangeCalendarDay();
		this.pk_psndocs = panel2.getSelPkPsndocs();

		// ȡ���Ƿ�����������־
		ShiftSetStepForBatchChangeCalendarDay step3 = (ShiftSetStepForBatchChangeCalendarDay) wizardModel.getSteps()
				.get(2);
		ShiftSetPanelForBatchChangeCalendarDay panel3 = step3.getShiftSetPanelForBatchChangeCalendarDay();
		boolean changeDayTypeCheckBox = panel3.getChangeDayTypeCheckBox().isSelected();

		// ��ȡ������֯

		String[] orgs = { wizardModel.getModel().getContext().getPk_org() };
		String legal_pk_org = LegalOrgUtilsEX.getLegalOrgByOrgs(orgs).get(
				wizardModel.getModel().getContext().getPk_org());

		if (changeDayTypeCheckBox) {
			// �����߼�
			// ��ȡ���������߼�����������������������
			UFLiteralDate firstDate = panel3.getFirstDate();
			UFLiteralDate secondDate = panel3.getSecondDate();
			IPsnCalendarManageValidate manageValidate = NCLocator.getInstance()
					.lookup(IPsnCalendarManageValidate.class);
			if (null == firstDate || null != secondDate) {
				// ���Ƚ��п��ڵ�����У��
				try {
					// ITBMPsndocQueryService tbmPsndocQS =
					// NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
					// ������1�Ŀ��ڵ���//������Ա��key����Ա����
					/*
					 * Map<String, List<TBMPsndocVO>> firstTbmpsndocVOMap =
					 * tbmPsndocQS.queryTBMPsndocMapByPsndocs(pk_org,
					 * pk_psndocs, firstDate, firstDate, true, true, null);
					 */
					// �ڶ���Ŀ��ڵ���
					/*
					 * Map<String, List<TBMPsndocVO>> secondTbmpsndocVOMap =
					 * tbmPsndocQS.queryTBMPsndocMapByPsndocs(pk_org,
					 * pk_psndocs, secondDate, secondDate, true, true, null);
					 */
					/*
					 * if (MapUtils.isEmpty(firstTbmpsndocVOMap)) {
					 * WizardActionException wae = new WizardActionException(new
					 * BusinessException( firstDate +
					 * ResHelper.getString("twhr_psncalendar",
					 * "psncalendar-0015")
					 * 
					 * @res "��Ա���ڵ���Ϊ��,��������κβ���" )); wae.addMsg("1", firstDate +
					 * ResHelper.getString("twhr_psncalendar",
					 * "psncalendar-0015")
					 * 
					 * @res "��Ա���ڵ���Ϊ��,��������κβ���" ); throw wae; } if
					 * (MapUtils.isEmpty(secondTbmpsndocVOMap)) {
					 * WizardActionException wae = new WizardActionException(new
					 * BusinessException( secondDate +
					 * ResHelper.getString("twhr_psncalendar",
					 * "psncalendar-0015")
					 * 
					 * @res "��Ա���ڵ���Ϊ��,��������κβ���" )); wae.addMsg("1", secondDate +
					 * ResHelper.getString("twhr_psncalendar",
					 * "psncalendar-0015")
					 * 
					 * @res "��Ա���ڵ���Ϊ��,��������κβ���" ); throw wae; }
					 */

					// �����������ڶ��п��ڵ�������Ա
					// Set<String> psndocListHasBothDate = new HashSet<>();//
					// ����ͷ��û����set����,����set�İ���Ҫ�Լ���
					// ��Щ�ǿ��ڵ���ȱʧ����Ա <date,List<pk_psndoc>>
					// Map<UFLiteralDate, HashSet<String>>
					// psndocListHasnotBothDate = new HashMap<>();//
					// ����ͷ,HashMapҲû����
					// psndocListHasnotBothDate.put(firstDate, new
					// HashSet<String>());
					// psndocListHasnotBothDate.put(secondDate, new
					// HashSet<String>());
					// ����������Ա,���Ƿ��ڵ����Ƿ񶼴���
					// int signal = 0;
					/*
					 * for (String pk_psndoc : pk_psndocs) { signal = 0; if
					 * (null == firstTbmpsndocVOMap.get(pk_psndoc) ||
					 * firstTbmpsndocVOMap.get(pk_psndoc).isEmpty()) {
					 * psndocListHasnotBothDate.get(firstDate).add(pk_psndoc);
					 * signal++; } if (null ==
					 * secondTbmpsndocVOMap.get(pk_psndoc) ||
					 * secondTbmpsndocVOMap.get(pk_psndoc).isEmpty()) {
					 * psndocListHasnotBothDate.get(secondDate).add(pk_psndoc);
					 * signal++; } if (0 == signal) {// �������ڵ��������
					 * psndocListHasBothDate.add(pk_psndoc); } }
					 * 
					 * if (!psndocListHasnotBothDate.get(firstDate).isEmpty() ||
					 * !psndocListHasnotBothDate.get(secondDate).isEmpty()) {
					 * 
					 * WorkCalendarHolidayMemoDialog memoDialog = new
					 * WorkCalendarHolidayMemoDialog( this,
					 * nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID
					 * ("10140wcb", "010140wcb0010") @res "�ڼ���˵��" ); String memo
					 * = " "; if (memoDialog.showModal() == UIDialog.ID_OK) {
					 * memo = memoDialog.getHolidayMemo(); }
					 * 
					 * 
					 * //TODO: �����ˆT���ڙn�������ڵ��ˆTExcel,�������@ʾ����ʾ���� int isForce =
					 * MessageDialog.showOkCancelDlg(null,
					 * ResHelper.getString("twhr_psncalendar",
					 * "psncalendar-0016"),
					 * ResHelper.getString("twhr_psncalendar",
					 * "psncalendar-0016")); if (1 != isForce) { // ��ͬ��,ʲôҲ����
					 * return; } }
					 */
					// ������ʱ��,�����п��ڵ�У��
					// psndocListHasBothDate.addAll(Arrays.asList(pk_psndocs));
					// ����֮ǰ��һ��һ�ݵ�У��
					// IHRHolidayQueryService
					// ��ҳ����һ��������
					List<String> strMessage = manageValidate.updateValidate(wizardModel.getModel().getContext()
							.getPk_org(), pk_psndocs, firstDate, secondDate);
					UFBoolean isStrcheck = SysInitQuery.getParaBoolean(legal_pk_org, "TWHRT03");
					if (strMessage.size() > 0) {
						for (String strs : strMessage) {
							if (isStrcheck.booleanValue()) {
								// �ϸ�У�飨�д�����ȡ�����棩
								MessageDialog.showHintsDlg(wizardModel.getModel().getContext().getEntranceUI(), "У��",
										"����Ա��" + strs);
								return;
							} else {
								// ���ϸ�У�飨ҳ�浯��һ���������û���Щ�˲�����һ��һ�ݣ�Ȼ�û��Լ�ѡ���Ƿ�������棩
								if (2 == MessageDialog.showOkCancelDlg(wizardModel.getModel().getContext()
										.getEntranceUI(), "У��", "����Ա��" + strs + "�Ƿ��������?")) {
									return;
								}

							}
						}

					}

					// �����ߵ���,˵��ȫ���˶��п��ڵ���,������̨ȥ���������������

					IPsnCalendarManageMaintain queryMaintain = NCLocator.getInstance().lookup(
							IPsnCalendarManageMaintain.class);

					// MOD �ź� {21997} ��ȡ����ʱ�� 2018/9/30
					String changedayorhourStr = panel3.getSupplementDayNum();

					queryMaintain.batchChangeDateType(pk_org, pk_psndocs, firstDate, secondDate, changedayorhourStr);

				} catch (BusinessException e) {
					Debug.error(e.getMessage(), e);
					WizardActionException wae = new WizardActionException(e);
					wae.addMsg("1", e.getMessage());
					throw wae;
				}

			} else {
				WizardActionException wae = new WizardActionException(new BusinessException(ResHelper.getString(
						"twhr_psncalendar", "psncalendar-0013")
				/* @res "�������ڲ���Ϊ��" */));
				Debug.error(wae.getMessage(), wae);
				// throw wae;
				// wae.addMsg("1", e.getMessage());
				throw wae;
			}

			refreshData(firstDate, secondDate);
		} else {

			// ��������߼�
			// ��ȡһ����������
			UFLiteralDate changeDate = panel3.getChangeDate();

			IPsnCalendarManageValidate manageValidate = NCLocator.getInstance()
					.lookup(IPsnCalendarManageValidate.class);
			if (null != changeDate) {
				// ���Ƚ��п��ڵ�����У��
				// ITBMPsndocQueryService tbmPsndocQS =
				// NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
				// ������1�Ŀ��ڵ���//������Ա��key����Ա����
				try {
					/*
					 * Map<String, List<TBMPsndocVO>> changeTbmpsndocVOMap =
					 * tbmPsndocQS.queryTBMPsndocMapByPsndocs(pk_org,
					 * pk_psndocs, changeDate, changeDate, true, true, null);
					 */
					/*
					 * if (MapUtils.isEmpty(changeTbmpsndocVOMap)) {
					 * WizardActionException wae = new WizardActionException(new
					 * BusinessException( changeDate +
					 * ResHelper.getString("twhr_psncalendar",
					 * "psncalendar-0015")
					 * 
					 * @res "��Ա���ڵ���Ϊ��,��������κβ���" )); wae.addMsg("1", changeDate +
					 * ResHelper.getString("twhr_psncalendar",
					 * "psncalendar-0015")
					 * 
					 * @res "��Ա���ڵ���Ϊ��,��������κβ���" ); throw wae; }
					 */
					// Set<String> psndocListHasDate = new HashSet<>();
					Set<String> psndocListWithNoDate = new HashSet<>();
					/*
					 * // �����п��ڵ�������Ա
					 * 
					 * // ����������Ա,���Ƿ��ڵ����Ƿ񶼴��� for (String pk_psndoc :
					 * pk_psndocs) { if (null ==
					 * changeTbmpsndocVOMap.get(pk_psndoc) ||
					 * changeTbmpsndocVOMap.get(pk_psndoc).isEmpty()) {
					 * psndocListWithNoDate.add(pk_psndoc); } else{// �������ڵ��������
					 * psndocListHasDate.add(pk_psndoc); } }
					 */
					// psndocListHasDate.addAll(Arrays.asList(pk_psndocs));
					/*
					 * if (!psndocListWithNoDate.isEmpty()) {
					 * 
					 * WorkCalendarHolidayMemoDialog memoDialog = new
					 * WorkCalendarHolidayMemoDialog( this,
					 * nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID
					 * ("10140wcb", "010140wcb0010") @res "�ڼ���˵��" ); String memo
					 * = " "; if (memoDialog.showModal() == UIDialog.ID_OK) {
					 * memo = memoDialog.getHolidayMemo(); }
					 * 
					 * //StringBuilder msgBd = new StringBuilder(); //TODO:
					 * �����ˆT���ڙn���������ˆT��Excel(��锵���^��),�������@ʾ����ʾ���� int isForce =
					 * MessageDialog.showOkCancelDlg(null,
					 * ResHelper.getString("twhr_psncalendar",
					 * "psncalendar-0016"),
					 * ResHelper.getString("twhr_psncalendar",
					 * "psncalendar-0016")); if (1 != isForce) { // ��ͬ��,ʲôҲ����
					 * return; } }
					 */
					// ����֮ǰ��һ��һ�ݵ�У��
					// IHRHolidayQueryService
					// ��ҳ����һ��������
					List<String> strMessage = manageValidate.updatedayValidate(wizardModel.getModel().getContext()
							.getPk_org(), psndocListWithNoDate.toArray(new String[0]), changeDate,
							panel3.getafterChangeDateType());
					UFBoolean isStrcheck = SysInitQuery.getParaBoolean(legal_pk_org, "TWHRT03");
					if (null != strMessage) {
						for (String strs : strMessage) {
							if (isStrcheck.booleanValue()) {
								// �ϸ�У�飨�д�����ȡ�����棩
								MessageDialog.showHintsDlg(wizardModel.getModel().getContext().getEntranceUI(), "У��",
										"����Ա��" + strs);
								return;
							} else {
								// ���ϸ�У�飨ҳ�浯��һ���������û���Щ�˲�����һ��һ�ݣ�Ȼ�û��Լ�ѡ���Ƿ�������棩
								if (2 == MessageDialog.showOkCancelDlg(wizardModel.getModel().getContext()
										.getEntranceUI(), "У��", "����Ա��" + strs + "�Ƿ��������?")) {
									return;
								}

							}
						}

					}
					// �����ߵ���,˵��ȫ���˶��п��ڵ���,������̨ȥ���������������

					IPsnCalendarManageMaintain queryMaintain = NCLocator.getInstance().lookup(
							IPsnCalendarManageMaintain.class);

					queryMaintain.batchChangeDateType4OneDay(pk_org, pk_psndocs, changeDate,
							panel3.getafterChangeDateType());

				} catch (BusinessException e) {
					Debug.debug(e.getMessage(), e);
					WizardActionException wae = new WizardActionException(e);
					wae.addMsg("1", e.getMessage());
					throw wae;
				}
			}

			refreshData(changeDate, changeDate);
		}

		/*
		 * HRWizardModel wizardModel = (HRWizardModel)event.getModel(); //ȡ�����ڷ�Χ
		 * SelPsnStepForBatchChange step1 =
		 * (SelPsnStepForBatchChange)wizardModel.getSteps().get(0);
		 * SelPsnPanelForBatchChange panel1 =
		 * step1.getSelPsnPanelForBatchChange(); FromWhereSQL fromWhereSQL =
		 * panel1.getQuerySQL(); UFLiteralDate beginDate =
		 * panel1.getBeginDate(); UFLiteralDate endDate = panel1.getEndDate();
		 * String pk_org = panel1.getPK_BU(); //ȡ����Ա����
		 * ConfirmPsnStepForBatchChange step2 =
		 * (ConfirmPsnStepForBatchChange)wizardModel.getSteps().get(1);
		 * ConfirmPsnPanelForBatchChange panel2 =
		 * step2.getConfirmPsnPanelForBatchChange(); String[] pk_psndocs =
		 * panel2.getSelPkPsndocs(); //ȡ���������Ű�ȡ����־ ShiftSetStepForBatchChange
		 * step3 = (ShiftSetStepForBatchChange)wizardModel.getSteps().get(2);
		 * ShiftSetPanelForBatchChange panel3 =
		 * step3.getShiftSetPanelForBatchChange(); // boolean isHolidayCancel =
		 * panel3.isHolidayCancel(); //ȡ��������� String old_Pk_shift =
		 * panel3.getOldShiftPk();//�ϵİ������ String new_Pk_shift =
		 * panel3.getNewShiftPk();//�µİ������
		 * 
		 * Boolean
		 * withOldShift=panel3.getOldShiftCheckBox().isSelected();//ԭ����Ƿ�ѡ��
		 * IPsnCalendarManageMaintain manageMaintain =
		 * NCLocator.getInstance().lookup(IPsnCalendarManageMaintain.class);
		 * IPsnCalendarQueryMaintain queryMaintain =
		 * NCLocator.getInstance().lookup(IPsnCalendarQueryMaintain.class);
		 * PsnJobCalendarVO[] calendarVOs = null; try {
		 * manageMaintain.batchChangeShiftNew(pk_org, pk_psndocs, beginDate,
		 * endDate,withOldShift, old_Pk_shift,new_Pk_shift); String pk_hrorg =
		 * NCLocator.getInstance().lookup(IAOSQueryService.class).
		 * queryHROrgByOrgPK(pk_org).getPk_org(); //calendarVOs=
		 * queryMaintain.queryCalendarVOByPsndocs(pk_org, pk_psndocs, beginDate,
		 * endDate); calendarVOs=
		 * queryMaintain.queryCalendarVOByPsndocs(pk_hrorg, pk_psndocs,
		 * beginDate, endDate);
		 * 
		 * 
		 * } catch (BusinessException e) { Debug.error(e.getMessage(), e);
		 * WizardActionException wae = new WizardActionException(e);
		 * wae.addMsg("1", e.getMessage()); throw wae; } PsnCalendarAppModel
		 * appModel = (PsnCalendarAppModel)wizardModel.getModel();
		 * appModel.setBeginEndDate(beginDate, endDate);
		 * appModel.initModel(calendarVOs);
		 * event.getModel().gotoStepForwardNoValidate(0);
		 */

		System.out.println("ȷ���߼���ʼ..............");
	}

	private void refreshData(UFLiteralDate firstDate, UFLiteralDate secondDate) {
		HRWizardModel wizardModel = (HRWizardModel) event.getModel();
		PsnCalendarAppModel appModel = (PsnCalendarAppModel) wizardModel.getModel();
		IPsnCalendarQueryMaintain queryMaintain = NCLocator.getInstance().lookup(IPsnCalendarQueryMaintain.class);
		PsnJobCalendarVO[] calendarVOs = new PsnJobCalendarVO[0];
		if (firstDate.before(secondDate)) {

			appModel.setBeginEndDate(firstDate, secondDate);
			try {
				calendarVOs = queryMaintain.queryCalendarVOByPsndocs(pk_org, pk_psndocs, firstDate, secondDate);
			} catch (BusinessException e) {
				Debug.error(e.getMessage(), e);
				e.printStackTrace();
			}

		} else {
			appModel.setBeginEndDate(secondDate, firstDate);
			try {
				calendarVOs = queryMaintain.queryCalendarVOByPsndocs(pk_org, pk_psndocs, secondDate, firstDate);
			} catch (BusinessException e) {
				Debug.error(e.getMessage(), e);
				e.printStackTrace();
			}
		}
		appModel.initModel(calendarVOs);

		event.getModel().gotoStepForwardNoValidate(0);
	}

	@Override
	public void wizardFinishAndContinue(WizardEvent event) throws WizardActionException {

	}

}