package nc.ui.ta.psncalendar.view.batchchangecalendarday;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.ta.IHRHolidayQueryService;
import nc.itf.ta.IPsnCalendarManageMaintain;
import nc.itf.ta.IPsnCalendarManageValidate;
import nc.itf.ta.IPsnCalendarQueryMaintain;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.uap.IUAPQueryBS;
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
import nc.vo.ta.psndoc.TBMPsndocVO;

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
		// 取出人员主键
		ConfirmPsnStepForBatchChangeCalendarDay step2 = (ConfirmPsnStepForBatchChangeCalendarDay) wizardModel.getSteps()
				.get(1);
		ConfirmPsnPanelForBatchChangeCalendarDay panel2 = step2.getConfirmPsnPanelForBatchChangeCalendarDay();
		this.pk_psndocs = panel2.getSelPkPsndocs();

		// 取出是否调换日历天标志
		ShiftSetStepForBatchChangeCalendarDay step3 = (ShiftSetStepForBatchChangeCalendarDay) wizardModel.getSteps()
				.get(2);
		ShiftSetPanelForBatchChangeCalendarDay panel3 = step3.getShiftSetPanelForBatchChangeCalendarDay();
		boolean changeDayTypeCheckBox = panel3.getChangeDayTypeCheckBox().isSelected();

		//获取法人组织

		 String[] orgs ={wizardModel.getModel().getContext().getPk_org()};
		 String legal_pk_org = LegalOrgUtilsEX.getLegalOrgByOrgs(orgs).get(wizardModel.getModel().getContext().getPk_org());


		if (changeDayTypeCheckBox) {
			// 调换逻辑
			// 读取两个调换逻辑和两个调换的日历天类型
			UFLiteralDate firstDate = panel3.getFirstDate();
			UFLiteralDate secondDate = panel3.getSecondDate();
			IPsnCalendarManageValidate manageValidate = NCLocator.getInstance().lookup(IPsnCalendarManageValidate.class);
			if (null == firstDate || null != secondDate) {
				// 首先进行考勤档案的校验
				try {
					ITBMPsndocQueryService tbmPsndocQS = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
					// 调换天1的考勤档案//所有人员，key是人员主键
					Map<String, List<TBMPsndocVO>> firstTbmpsndocVOMap = tbmPsndocQS.queryTBMPsndocMapByPsndocs(pk_org,
							pk_psndocs, firstDate, firstDate, true, true, null);
					// 第二天的考勤档案
					Map<String, List<TBMPsndocVO>> secondTbmpsndocVOMap = tbmPsndocQS.queryTBMPsndocMapByPsndocs(pk_org,
							pk_psndocs, secondDate, secondDate, true, true, null);
					if (MapUtils.isEmpty(firstTbmpsndocVOMap)) {
						WizardActionException wae = new WizardActionException(new BusinessException(
								firstDate + ResHelper.getString("twhr_psncalendar", "psncalendar-0015")
						/* @res "人员考勤档案为空,不会进行任何操作" */));
						wae.addMsg("1", firstDate + ResHelper.getString("twhr_psncalendar",
								"psncalendar-0015")/*
													 * @res "人员考勤档案为空,不会进行任何操作"
													 */);
						throw wae;
					}
					if (MapUtils.isEmpty(secondTbmpsndocVOMap)) {
						WizardActionException wae = new WizardActionException(new BusinessException(
								secondDate + ResHelper.getString("twhr_psncalendar", "psncalendar-0015")
						/* @res "人员考勤档案为空,不会进行任何操作" */));
						wae.addMsg("1", secondDate + ResHelper.getString("twhr_psncalendar",
								"psncalendar-0015")/*
													 * @res "人员考勤档案为空,不会进行任何操作"
													 */);
						throw wae;
					}

					// 保存两个日期都有考勤档案的人员
					Set<String> psndocListHasBothDate = new HashSet<>();// 这年头都没人用set的吗,连个set的包都要自己引
					// 这些是考勤档案缺失的人员 <date,List<pk_psndoc>>
					Map<UFLiteralDate, HashSet<String>> psndocListHasnotBothDate = new HashMap<>();// 这年头,HashMap也没人用
					psndocListHasnotBothDate.put(firstDate, new HashSet<String>());
					psndocListHasnotBothDate.put(secondDate, new HashSet<String>());
					// 遍历各个人员,看是否考勤档案是否都存在
					int signal = 0;
					for (String pk_psndoc : pk_psndocs) {
						signal = 0;
						if (null == firstTbmpsndocVOMap.get(pk_psndoc)
								|| firstTbmpsndocVOMap.get(pk_psndoc).isEmpty()) {
							psndocListHasnotBothDate.get(firstDate).add(pk_psndoc);
							signal++;
						}
						if (null == secondTbmpsndocVOMap.get(pk_psndoc)
								|| secondTbmpsndocVOMap.get(pk_psndoc).isEmpty()) {
							psndocListHasnotBothDate.get(secondDate).add(pk_psndoc);
							signal++;
						}
						if (0 == signal) {// 两个考勤档案都存的
							psndocListHasBothDate.add(pk_psndoc);
						}
					}

					if (!psndocListHasnotBothDate.get(firstDate).isEmpty()
							|| !psndocListHasnotBothDate.get(secondDate).isEmpty()) {
						//TODO: 導出人員考勤檔案不存在的人員Excel,而不是顯示在提示框上
						int isForce = MessageDialog.showOkCancelDlg(null,
								ResHelper.getString("twhr_psncalendar", "psncalendar-0016"),
								ResHelper.getString("twhr_psncalendar", "psncalendar-0016"));
						if (1 != isForce) {
							// 不同意,什么也不改
							return;
						}
					}
					//不符合
					//校验日历天类型,不为同一日历天类型,则提示不进行更新
					psndocListHasBothDate = checkDateType4ChangeDate(wizardModel,pk_org,
							psndocListHasBothDate,firstDate,secondDate,
							panel3.getFirstDateType(),
							panel3.getSecondDateType());
					
					
					//更新之前做一例一休的校验
					//IHRHolidayQueryService
					//在页面做一个弹出框
					List<List<String>> strMessage = manageValidate.updateValidate(wizardModel.getModel().getContext().getPk_org(),psndocListHasBothDate.toArray(new String[0]), firstDate, secondDate);
					UFBoolean isStrcheck = SysInitQuery.getParaBoolean(legal_pk_org, "TWHRT03");
					if(strMessage.size()>0 ){
						for(List<String> strs : strMessage){
							if(isStrcheck.booleanValue()){
								//严格校验（有错误则取消保存）
								MessageDialog.showHintsDlg(wizardModel.getModel().getContext().getEntranceUI(), "校验", "以下员工"+strs);
								return;
							} else {
								//非严格校验（页面弹出一个框提醒用户哪些人不符合一例一休，然用户自己选择是否继续保存）
								if( 2 == MessageDialog.showOkCancelDlg(wizardModel.getModel().getContext().getEntranceUI(), "校验", "以下员工"+strs+"是否继续保存?")){
									return;
								}

							}
						}

					}

					// 代码走到这,说明全部人都有考勤档案,丢到后台去更改日历天就行了

					IPsnCalendarManageMaintain queryMaintain = NCLocator.getInstance()
							.lookup(IPsnCalendarManageMaintain.class);

					//MOD 张恒 {21997} 获取调整时长  2018/9/30
					String changedayorhourStr = panel3.getSupplementDayNum();

					queryMaintain.batchChangeDateType(pk_org, psndocListHasBothDate.toArray(new String[0]), firstDate,
							secondDate, changedayorhourStr);

				} catch (BusinessException e) {
					Debug.error(e.getMessage(), e);
					WizardActionException wae = new WizardActionException(e);
					wae.addMsg("1", e.getMessage());
					throw wae;
				}

			} else {
				WizardActionException wae = new WizardActionException(
						new BusinessException(ResHelper.getString("twhr_psncalendar", "psncalendar-0013")
						/* @res "调换日期不能为空" */));
				Debug.error(wae.getMessage(), wae);
				throw wae;
			}


			refreshData(firstDate, secondDate);
		} else {

			// 日历变更逻辑
			// 读取一个日历即可
			UFLiteralDate changeDate = panel3.getChangeDate();

			IPsnCalendarManageValidate manageValidate = NCLocator.getInstance().lookup(IPsnCalendarManageValidate.class);
			if (null != changeDate) {
				// 首先进行考勤档案的校验
				ITBMPsndocQueryService tbmPsndocQS = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
				// 调换天1的考勤档案//所有人员，key是人员主键
				try {
					Map<String, List<TBMPsndocVO>> changeTbmpsndocVOMap = tbmPsndocQS.queryTBMPsndocMapByPsndocs(pk_org,
							pk_psndocs, changeDate, changeDate, true, true, null);
					if (MapUtils.isEmpty(changeTbmpsndocVOMap)) {
						WizardActionException wae = new WizardActionException(new BusinessException(
								changeDate + ResHelper.getString("twhr_psncalendar", "psncalendar-0015")
						/* @res "人员考勤档案为空,不会进行任何操作" */));
						wae.addMsg("1", changeDate + ResHelper.getString("twhr_psncalendar",
								"psncalendar-0015")/*
													 * @res "人员考勤档案为空,不会进行任何操作"
													 */);
						throw wae;
					}
					// 保存有考勤档案的人员
					Set<String> psndocListHasDate = new HashSet<>();
					Set<String> psndocListWithNoDate = new HashSet<>();
					// 遍历各个人员,看是否考勤档案是否都存在
					for (String pk_psndoc : pk_psndocs) {
						if (null == changeTbmpsndocVOMap.get(pk_psndoc)
								|| changeTbmpsndocVOMap.get(pk_psndoc).isEmpty()) {
							psndocListWithNoDate.add(pk_psndoc);
						}
						else{// 两个考勤档案都存的
							psndocListHasDate.add(pk_psndoc);
						}
					}
					if (!psndocListWithNoDate.isEmpty()) {
						//TODO: 導出人員考勤檔案不存在人員的Excel(因為數量較多),而不是顯示在提示框上
						int isForce = MessageDialog.showOkCancelDlg(null,
								ResHelper.getString("twhr_psncalendar", "psncalendar-0016"),
								ResHelper.getString("twhr_psncalendar", "psncalendar-0016"));
						if (1 != isForce) {
							// 不同意,什么也不改
							return;
						}
					}
					//校验日历天类型,不为同一日历天类型,则提示不进行更新
					psndocListWithNoDate = checkDateType4OneDate(wizardModel,pk_org,
							psndocListHasDate,changeDate,
							panel3.getBeforeChangeDateType());
					
					
					//更新之前做一例一休的校验
					//IHRHolidayQueryService
					//在页面做一个弹出框
					List<List<String>> strMessage = manageValidate.updatedayValidate(wizardModel.getModel().getContext().getPk_org(),psndocListHasDate.toArray(new String[0]), changeDate, panel3.getafterChangeDateType());
					UFBoolean isStrcheck = SysInitQuery.getParaBoolean(legal_pk_org, "TWHRT03");
					if(null != strMessage ){
						for(List<String> strs : strMessage){
							if(isStrcheck.booleanValue()){
								//严格校验（有错误则取消保存）
								MessageDialog.showHintsDlg(wizardModel.getModel().getContext().getEntranceUI(), "校验", "以下员工"+strs);
								return;
							} else {
								//非严格校验（页面弹出一个框提醒用户哪些人不符合一例一休，然用户自己选择是否继续保存）
								if( 2 == MessageDialog.showOkCancelDlg(wizardModel.getModel().getContext().getEntranceUI(), "校验", "以下员工"+strs+"是否继续保存?")){
									return;
								}

							}
						}

					}
					// 代码走到这,说明全部人都有考勤档案,丢到后台去更改日历天就行了

					IPsnCalendarManageMaintain queryMaintain = NCLocator.getInstance()
							.lookup(IPsnCalendarManageMaintain.class);
					String changedayorhourStr = panel3.getSupplementDayNum();
					queryMaintain.batchChangeDateType4OneDay(pk_org, psndocListHasDate.toArray(new String[0]),
							changeDate, panel3.getafterChangeDateType(), changedayorhourStr);

				} catch (BusinessException e) {
					Debug.debug(e.getMessage(), e);
					WizardActionException wae = new WizardActionException(e);
					wae.addMsg("1", e.getMessage());
					throw wae;
				}
			}

			refreshData(changeDate, changeDate);
		}

		System.out.println("确定逻辑开始..............");
	}
	/**
	 * 日历调换时候,进行日历天的校验,如果不是选定的日历天,那么跳过这个人
	 * @param psndocListHasBothDate 人员信息
	 * @param secondDate 
	 * @param firstDate 
	 * @param firstDateType
	 * @param secondDateType
	 * @return 
	 * @throws BusinessException 
	 */
	private Set<String> checkDateType4ChangeDate(HRWizardModel wizardModel,String pk_org,
			Set<String> psndocListHasBothDate, UFLiteralDate firstDate, UFLiteralDate secondDate, 
			int firstDateType, int secondDateType) throws BusinessException {
		Set<String> resultSet = new HashSet<>();
		Set<String> notMatchSet = new HashSet<>();
		//查询這些員工的日曆天類型
		IHRHolidayQueryService holidaySerevice = 
				NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		// Map<pk_psndoc,<date,工作日类型>>
		Map<String, Map<String, Integer>> firstDateTypeMap = 
				holidaySerevice.queryPsnWorkDayTypeInfo(
				pk_org,psndocListHasBothDate.toArray(new String[0]),firstDate,firstDate);
		Map<String, Map<String, Integer>> secondDateTypeMap = 
				holidaySerevice.queryPsnWorkDayTypeInfo(
				pk_org,psndocListHasBothDate.toArray(new String[0]),secondDate,secondDate);
		//匹配第一天的這些員工的日曆天類型
		for(String pk_psndoc : firstDateTypeMap.keySet()){
			if(firstDateTypeMap.get(pk_psndoc)!=null 
					&& firstDateTypeMap.get(pk_psndoc).get(firstDate.toString())!=null
					&& switchDateType(firstDateTypeMap.get(pk_psndoc).get(firstDate.toString()))== firstDateType){
				resultSet.add(pk_psndoc);
				continue;
			}
			notMatchSet.add(pk_psndoc);
		}
		//匹配第二天的日曆天類型
		for(String pk_psndoc : secondDateTypeMap.keySet()){
			if(secondDateTypeMap.get(pk_psndoc)!=null 
					&& secondDateTypeMap.get(pk_psndoc).get(secondDate.toString())!=null
					&& switchDateType(secondDateTypeMap.get(pk_psndoc).get(secondDate.toString()))== secondDateType){
				resultSet.add(pk_psndoc);
				continue;
			}
			notMatchSet.add(pk_psndoc);
		}
		//提示不符合的人员,
		if(notMatchSet!=null&& notMatchSet.size() > 0){
			MessageDialog.showHintsDlg(null,
					"提示", "員工["+getPsnCodeByPK(notMatchSet)+"],日曆天類型不符合,將被跳過.");
		}
		//返回符合的人员
		return resultSet;
	}
	
	/**
	 * 日历变更时,进行日历天的校验,如果不是选定的日历天,那么跳过这个人
	 * @param psndocListHasDate 人员信息
	 * @param changeDateType 變更前日曆天
	 * @param changeDate 
	 * @return 
	 * @throws BusinessException 
	 */
	private Set<String> checkDateType4OneDate(HRWizardModel wizardModel, String pk_org,
			Set<String> psndocListHasDate, UFLiteralDate changeDate, int changeDateType) throws BusinessException {
		Set<String> resultSet = new HashSet<>();
		Set<String> notMatchSet = new HashSet<>();
		//查询這些員工的日曆天類型
		IHRHolidayQueryService holidaySerevice = 
				NCLocator.getInstance().lookup(IHRHolidayQueryService.class);
		// Map<pk_psndoc,<date,工作日类型>>
		Map<String, Map<String, Integer>> firstDateTypeMap = 
				holidaySerevice.queryPsnWorkDayTypeInfo(
				pk_org,psndocListHasDate.toArray(new String[0]),changeDate,changeDate);
		
		//匹配交換的這些員工的日曆天類型
		for(String pk_psndoc : firstDateTypeMap.keySet()){
			if(firstDateTypeMap.get(pk_psndoc)!=null 
					&& firstDateTypeMap.get(pk_psndoc).get(changeDate.toString())!=null
					&& switchDateType(firstDateTypeMap.get(pk_psndoc).get(changeDate.toString()))== changeDateType){
				resultSet.add(pk_psndoc);
				continue;
			}
			notMatchSet.add(pk_psndoc);
		}
		
		//提示不符合的人员,
		if(notMatchSet!=null&& notMatchSet.size() > 0){
			MessageDialog.showHintsDlg(wizardModel.getModel().getContext().getEntranceUI(),
					"提示", "員工["+getPsnCodeByPK(notMatchSet)+"],日曆天類型不符合,將被跳過.");
		}
		//返回符合的人员
		return resultSet;
	}
	/**
	 * 獲取員工code 並拼成字符串
	 * @param pk_psndocSet
	 * @return
	 * @throws BusinessException 
	 */
	public String getPsnCodeByPK (Set<String> pk_psndocSet) throws BusinessException{
		IPsnCalendarQueryMaintain service = NCLocator.getInstance().lookup(IPsnCalendarQueryMaintain.class);   
		return service.getPsnCodeByPk(pk_psndocSet);
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
	/**
	 * 返回日历天类型的枚举值 员工工作日历 工作日历全局 工作日 -> 工作日 0 非工作日 -> 休息日 1 国定假日 -> 节假日 2 例假日 ->
	 * 例假日 4
	 *  为什么要进行转换呢?这是个很长的故事
	 * @return
	 */
	public Integer switchDateType(int dayType) {
			switch (dayType) {
			case 0:
				return 0;
			case 1:
				return 99;// 公休日暂未定义
			case 2:
				return 2;
			case 3:
				return 4;
			case 4:
				return 1;
			default:
				return 0;
			}
	}

}
