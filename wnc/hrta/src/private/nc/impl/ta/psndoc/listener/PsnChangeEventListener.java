package nc.impl.ta.psndoc.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.businessevent.IEventType;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.psncalendar.PsnCalendarDAO;
import nc.impl.ta.psndoc.TBMPsndocChangeProcessor;
import nc.impl.ta.psndoc.TBMPsndocMaintainImpl;
import nc.itf.hi.IPsndocQryService;
import nc.itf.om.IAOSQueryService;
import nc.itf.ta.ILeaveBalanceManageMaintain;
import nc.itf.ta.ITimeRuleQueryService;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.pubitf.bd.team.team01.hr.ITeamQueryServiceForHR;
import nc.vo.bd.team.team01.entity.TeamItemVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.enumeration.TrnseventEnum;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hi.pub.HiBatchEventValueObject;
import nc.vo.hi.pub.HiEventValueObject;
import nc.vo.hi.pub.IHiEventType;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocCommonValue;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * ���ڵ���������Ա���ֱ䶯ʱ�ļ��� 1. �¼�Դ��ת����Ա���� ������ʽ�����Ӹ���Ա���ڵ�����¼�� 2. �¼�Դ��������¼������
 * ������ʽ������춯�¼�Ϊ��ְ�����������Ա�Ŀ��ڵ��� �������Ա����δ�����Ŀ��ڵ�����������ÿ��ڵ�����������һ�����ڵ�����¼�� 3.
 * �¼�Դ��������¼�޸ĺ� ������ʽ�� �������ְ��Ϣ�������κδ����� ����޸ĵĲ������µĹ�����¼�������κδ���
 * ����޸ĵ������µĹ�����¼�������޸�ǰ���޸ĺ���н������ڻ��߾��޽������ڣ������κδ�����
 * ����޸ĵ������µĹ�����¼�������޸�ǰ�޽������ڣ��޸ĺ��н������ڣ��������Ա�Ŀ��ڵ�����-begindate=enddate;
 * ��������޸ĸù�����¼�Ŀ��ڵ�����Ϣ�� 4. �¼�Դ��������¼ɾ��ǰ ������ʽ��ɾ�����ڵ�����Ϣ�� 5. �¼�Դ����ְ��¼�޸ĺ�
 * ������ʽ���빤����¼�޸Ĵ�����ʽ����3������ͬ�� 6. �¼�Դ����ְ��¼ɾ��ǰ ������ʽ������ü�ְ��¼���ڵ�������ɾ����ǰ��ְ�Ŀ��ڵ����� 7.
 * �¼�Դ����ְ�����
 * ������ʽ���������Ա���¿��ڵ�������ְ�ǹ�����¼�����Ǽ�ְ��¼���򲻽����κβ������������Ա���¿��ڵ�������ְ�Ǽ�ְ��¼��������ÿ��ڵ���
 * ��������һ�����ڵ�����¼��
 * 
 * 2011-2-17 9:39:44
 */
public class PsnChangeEventListener implements IBusinessListener {

	private TBMPsndocMaintainImpl manageMaintain;
	private ILeaveBalanceManageMaintain leaveBalanceMM;

	private ILeaveBalanceManageMaintain getLeaveBalanceMM() {
		if (leaveBalanceMM == null) {
			leaveBalanceMM = NCLocator.getInstance().lookup(ILeaveBalanceManageMaintain.class);
		}
		return leaveBalanceMM;
	}

	private TBMPsndocMaintainImpl getManageMaintain() {
		if (manageMaintain == null) {
			manageMaintain = new TBMPsndocMaintainImpl();
		}
		return manageMaintain;
	}

	@Override
	public void doAction(final IBusinessEvent event) throws BusinessException {

		// final String pk_group = PubEnv.getPk_group();
		// final String bizCenterCode =
		// InvocationInfoProxy.getInstance().getBizCenterCode();
		// final long bizDateTime =
		// InvocationInfoProxy.getInstance().getBizDateTime();
		// final String langCode =
		// InvocationInfoProxy.getInstance().getLangCode();
		// final String userDataSource =
		// InvocationInfoProxy.getInstance().getUserDataSource();
		// final String userId = InvocationInfoProxy.getInstance().getUserId();
		// final InvocationInfo invocationInfo =
		// BDDistTokenUtil.getInvocationInfo();
		// new Executor(new Runnable() {//�����̲߳�Ӱ����Ա����Ӧʱ��
		// @Override
		// public void run() {
		// �߳��л�����Ϣ�ᶪʧ������������һ��
		// InvocationInfoProxy.getInstance().setGroupId(pk_group);
		// InvocationInfoProxy.getInstance().setBizCenterCode(bizCenterCode);
		// InvocationInfoProxy.getInstance().setBizDateTime(bizDateTime);
		// InvocationInfoProxy.getInstance().setLangCode(langCode);
		// InvocationInfoProxy.getInstance().setUserDataSource(userDataSource);
		// InvocationInfoProxy.getInstance().setUserId(userId);
		// BDDistTokenUtil.setInvocationInfo(invocationInfo);
		handleChange(event);
		// }
		// }).start();
	}

	private void handleChange(IBusinessEvent event) throws BusinessException {
		BusinessEvent be = (BusinessEvent) event;
		Object obj = be.getObject();
		if (obj == null)
			return;
		PsnJobVO[] psnJobBefores = null;
		PsnJobVO[] psnJobAfters = null;
		// ����ҵ��仯��Ĺ�����¼�������ת����Ա��������仯ǰ�ͱ仯����ͬ
		if (obj instanceof HiBatchEventValueObject) {
			HiBatchEventValueObject value = (HiBatchEventValueObject) obj;
			psnJobBefores = value.getPsnjobs_before();
			psnJobAfters = value.getPsnjobs_after();
		} else {
			HiEventValueObject value = (HiEventValueObject) obj;
			if (value.getPsnjob_before() != null)
				psnJobBefores = new PsnJobVO[] { value.getPsnjob_before() };
			if (value.getPsnjob_after() != null)
				psnJobAfters = new PsnJobVO[] { value.getPsnjob_after() };
		}
		String sourceID = event.getSourceID();
		try { // 2013-04-01 ��Ա������ʱ��ļ���Ӱ����Աҵ��Ĳ�������˰��쳣catch��
				// ������¼�仯����
			if (HICommonValue.PSNJOBSOURCEID.equals(sourceID)) {
				handlePsnjobChange(event.getEventType(), psnJobBefores, psnJobAfters);
			}
			// ��ְ��¼�仯����
			if (HICommonValue.PARTSOURCEID.equals(sourceID)) {
				handlePsnPartjobChange(event.getEventType(), psnJobBefores, psnJobAfters);
			}
		} catch (Exception e) {
			Logger.error("======== PsnChangeEventListener error " + e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}

	/**
	 * ��������������¼�仯
	 * 
	 * @param eventType
	 * @param psnJobBefores
	 * @param psnJobAfters
	 * @throws BusinessException
	 */
	private void handlePsnjobChange(String eventType, PsnJobVO[] psnJobBefores, PsnJobVO[] psnJobAfters)
			throws BusinessException {
		// ��������������¼��
		if (IEventType.TYPE_INSERT_AFTER.equals(eventType)) {
			handleAddPsnJobChange(psnJobBefores, psnJobAfters);
			getLeaveBalanceMM().translateLeaves(psnJobBefores, psnJobAfters);
			return;
		}
		// �޸Ĺ�����¼��
		if (IEventType.TYPE_UPDATE_AFTER.equals(eventType)) {
			handleUpdateJobOrPartChg(psnJobBefores, psnJobAfters);
			return;
		}
		// ɾ��������¼ǰ
		if (IEventType.TYPE_DELETE_BEFORE.equals(eventType)) {
			deleteTBMPsndocByPsnjob(psnJobBefores, false);
			return;
		}
	}

	/**
	 * ������ְ��¼��Ϣ�仯
	 * 
	 * @param event
	 * @throws BusinessException
	 */
	private void handlePsnPartjobChange(String eventType, PsnJobVO[] psnJobBefores, PsnJobVO[] psnJobAfters)
			throws BusinessException {
		// �޸ļ�ְ��¼��
		if (IEventType.TYPE_UPDATE_AFTER.equals(eventType)) {
			handleUpdateJobOrPartChg(psnJobBefores, psnJobAfters);
			return;
		}
		// ɾ����ְ��¼ǰ
		if (IEventType.TYPE_DELETE_BEFORE.equals(eventType)) {
			deleteTBMPsndocByPsnjob(psnJobBefores, true);
			return;
		}
		// ��ְ��¼��������������Ч���ڵ�����Ӧ�Ĺ�����¼�������κδ���������ǣ������֮ǰ�Ŀ��ڵ�����������һ�����ڵ���
		if (IHiEventType.PARTCHGAFTER.equals(eventType)) {
			handlePartChange(psnJobBefores, psnJobAfters);
			getLeaveBalanceMM().translateLeaves(psnJobBefores, psnJobAfters);
		}
	}

	/**
	 * ������ְ��¼���
	 * 
	 * @param oldPsnjobVOs
	 * @param psnjobVOs
	 * @throws BusinessException
	 */
	private void handlePartChange(PsnJobVO[] oldPsnjobVOs, PsnJobVO[] psnjobVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(psnjobVOs))
			return;
		String pk_org = psnjobVOs[0].getPk_org(); // ��hrjf��ҵ�񣬴���Ĺ�����¼����ҵ��ԪӦ�ö���һ���ģ���������֯ҵ��仯���˴�Ӧͬ���޸ģ�
		OrgVO hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org);
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class)
				.queryByOrgWithoutException(hrorg.getPk_org());
		boolean isNotIntoTBM = timeRule == null || timeRule.getTotbmpsntype() == null
				|| 0 == timeRule.getTotbmpsntype().intValue(); // ������Ա�Ƿ�ת�뿼�ڵ���
		Map<String, TBMPsndocVO> psndocMap = getManageMaintain().queryUnFinishPsnDoc(
				StringPiecer.getStrArray(psnjobVOs, PsnJobVO.PK_PSNDOC));
		if (MapUtils.isEmpty(psndocMap))
			return;
		TBMPsndocVO[] psndocs = psndocMap.values().toArray(new TBMPsndocVO[0]);
		PsnJobVO[] latestPsnjobVOs = NCLocator.getInstance().lookup(IPsndocQryService.class)
				.queryPsnjobByPKs(StringPiecer.getStrArrayDistinct(psndocs, TBMPsndocVO.PK_PSNJOB));
		Map<String, PsnJobVO> psnjobMap = CommonUtils.toMap(PsnJobVO.PK_PSNJOB, latestPsnjobVOs);
		if (MapUtils.isEmpty(psnjobMap))
			return;
		List<TBMPsndocVO> insertList = new ArrayList<TBMPsndocVO>();
		List<TBMPsndocVO> updateList = new ArrayList<TBMPsndocVO>();
		List<TBMPsndocVO> deleteList = new ArrayList<TBMPsndocVO>();
		for (int i = 0; i < psnjobVOs.length; i++) {
			PsnJobVO psnjobVO = psnjobVOs[i];
			TBMPsndocVO unFinishPsndoc = psndocMap.get(oldPsnjobVOs[i].getPk_psnjob());
			// �������Աû����Ч�ؿ��ڵ���,������
			if (unFinishPsndoc == null)
				continue;
			// �������Ա���¿��ڵ�������ְ�ǹ�����¼�����Ǽ�ְ��¼���򲻽����κβ���
			PsnJobVO latestPsnjob = psnjobMap.get(unFinishPsndoc.getPk_psnjob());
			if (latestPsnjob == null || latestPsnjob.getIsmainjob().booleanValue())
				continue;
			// ����֮ǰ�Ŀ��ڵ���
			// ������ڵ�����ʼ�����ڹ�����¼��������֮����ֱ��ɾ���������ڵ�����¼
			if (psnjobVO.getEnddate() != null && unFinishPsndoc.getBegindate().after(psnjobVO.getEnddate())) {
				deleteList.add(unFinishPsndoc);
			} else { // ���������½�������
				unFinishPsndoc.setEnddate(psnjobVO.getBegindate().getDateBefore(1));
				updateList.add(unFinishPsndoc);
			}
			if (isNotIntoTBM && (psndocMap.size() == 0 || psndocMap == null))// 2014-08-27��������(psndocMap.size()==0||psndocMap==null)��������δ�����Ŀ��ڵ��������Զ����뿼�ڴ���
				continue;
			// ��������һ�����ڵ���
			TBMPsndocVO psndocVO = new TBMPsndocVO();
			psndocVO.setPk_psndoc(psnjobVO.getPk_psndoc());
			psndocVO.setPk_psnjob(psnjobVO.getPk_psnjob());
			psndocVO.setPk_group(psnjobVO.getPk_group());
			psndocVO.setPk_org(hrorg.getPk_org());
			psndocVO.setPk_adminorg(hrorg.getPk_org());
			psndocVO.setPk_psnorg(psnjobVO.getPk_psnorg());
			psndocVO.setBegindate(psnjobVO.getBegindate());
			psndocVO.setEnddate(psnjobVO.getEnddate() == null ? UFLiteralDate.getDate(TBMPsndocCommonValue.END_DATA)
					: psnjobVO.getEnddate());
			if (timeRule.getTotbmpsntype() != null) {
				psndocVO.setTbm_prop(timeRule.getTotbmpsntype().intValue() == 1 ? TBMPsndocCommonValue.PROP_MANUAL
						: TBMPsndocCommonValue.PROP_MACHINE);
			} else {
				psndocVO.setTbm_prop(TBMPsndocCommonValue.PROP_MACHINE);
			}
			if (unFinishPsndoc != null && unFinishPsndoc.getPk_org().equalsIgnoreCase(psndocVO.getPk_org())) {// ������֯û�����ͱ仯��ѿ��ڿ�����Ϣ����
				psndocVO.setTimecardid(unFinishPsndoc.getTimecardid());
				psndocVO.setSecondcardid(unFinishPsndoc.getSecondcardid());
				psndocVO.setTbm_prop(unFinishPsndoc.getTbm_prop());
				psndocVO.setPk_place(unFinishPsndoc.getPk_place());
			}
			insertList.add(psndocVO);
			// v63add,�������������ԭ����֯�Ŀ��ڵ���������������֯�Ŀ��ڵ���������Ҫ�������ڽ���ת��
			// getLeaveBalanceMM().translateLeave(oldPsnjobVOs[i], psnjobVO);
		}
		// ����
		getManageMaintain().delete(deleteList.toArray(new TBMPsndocVO[0]));
		getManageMaintain().update(updateList.toArray(new TBMPsndocVO[0]), true);
		getManageMaintain().insert(insertList.toArray(new TBMPsndocVO[0]), true);
	}

	/**
	 * ��������������¼
	 * 
	 * @param psnJobVO
	 * @throws BusinessException
	 */
	private void handleAddPsnJobChange(PsnJobVO[] oldPsnjobVOs, PsnJobVO[] psnjobVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(psnjobVOs))
			return;
		String pk_org = psnjobVOs[0].getPk_org(); // ��hrjf��ҵ�񣬴���Ĺ�����¼����ҵ��ԪӦ�ö���һ���ģ���������֯ҵ��仯���˴�Ӧͬ���޸ģ�
		OrgVO hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org);
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class)
				.queryByOrgWithoutException(hrorg.getPk_org());
		Map<String, TBMPsndocVO> psndocMap = getManageMaintain().queryUnFinishPsnDoc(
				StringPiecer.getStrArray(psnjobVOs, PsnJobVO.PK_PSNDOC));
		boolean isNotIntoTBM = timeRule == null || timeRule.getTotbmpsntype() == null
				|| 0 == timeRule.getTotbmpsntype().intValue(); // ������Ա�Ƿ�ת�뿼�ڵ���
		List<TBMPsndocVO> insertList = new ArrayList<TBMPsndocVO>();
		List<TBMPsndocVO> insertNotArrangeList = new ArrayList<TBMPsndocVO>();// 633�޸�����֯û�з����仯���������Ű�
		List<TBMPsndocVO> updateList = new ArrayList<TBMPsndocVO>();
		List<TBMPsndocVO> updateNotArrangeList = new ArrayList<TBMPsndocVO>();// 65�޸�����֯û�з����仯���������Ű�
		List<TBMPsndocVO> deleteList = new ArrayList<TBMPsndocVO>();

		List<TeamItemVO> insertTeamItemVOs = new ArrayList<TeamItemVO>();// 65�޸���Ҫ�����İ�����Ա(��Ա����֯�����š���λδ�仯�����ܽ������飬��Ҫ����һ��)
		for (int i = 0; i < psnjobVOs.length; i++) {
			PsnJobVO psnjobVO = psnjobVOs[i];
			// ������ӵ�����ʷ������¼���������µģ��򲻴���
			if (psnjobVO.getLastflag() == null || !psnjobVO.getLastflag().booleanValue())
				continue;
			PsnJobVO oldPsnJobvo = oldPsnjobVOs[i];
			TBMPsndocVO unFinishPsndoc = psndocMap == null ? null : psndocMap.get(oldPsnJobvo.getPk_psnjob());
			// ��ѯ��֯��ϵ
			BaseDAO dao = new BaseDAO();
			List<String> flag = (List<String>) dao.executeQuery("select endflag from " + PsnOrgVO.getDefaultTableName()
					+ " where " + PsnOrgVO.PK_PSNORG + " = '" + psnjobVO.getPk_psnorg() + "' ",
					new ColumnListProcessor());
			// �������ְ��Ա���������ǰ���ڵ���
			if (psnjobVO.getTrnsevent().intValue() == TrnseventEnum.DISMISSION.toIntValue() || "Y".equals(flag.get(0))) {
				if (psndocMap == null || psndocMap.size() == 0)
					continue;
				if (unFinishPsndoc != null) {// �������ڵ���������ְ�����
					// ������ڵ�����ʼ�����ڹ�����¼��������֮����ֱ��ɾ���������ڵ�����¼
					if (unFinishPsndoc.getBegindate().after(psnjobVO.getBegindate())) {
						deleteList.add(unFinishPsndoc);
						continue;
					}
					// ���������½�������
					unFinishPsndoc.setEnddate(psnjobVO.getBegindate().getDateBefore(1));
					updateList.add(unFinishPsndoc);
					continue;
				} else {// �������ڵ������Ǽ�ְ�����,��Ա��ְ��ְͬʱɾ�����ڵ����м�ְ��¼20140901
					for (String str : psndocMap.keySet()) {
						if (psndocMap.get(str).getBegindate().after(psnjobVO.getBegindate())) {
							deleteList.add(psndocMap.get(str));
							continue;
						}
						psndocMap.get(str).setEnddate(psnjobVO.getBegindate().getDateBefore(1));
						updateList.add(psndocMap.get(str));
						continue;
					}
				}
			}
			// ���û��δ�����Ŀ��ڵ���
			if (psndocMap == null || psndocMap.size() == 0) {
				if (isNotIntoTBM || timeRule == null)// �������Ҫ���뿼�ڵ�����������֯û�п��ڹ�������Ҫ�ٴ���
					continue;
				// ��������һ�����ڵ���
				TBMPsndocVO psndocVO = new TBMPsndocVO();
				psndocVO.setPk_psndoc(psnjobVO.getPk_psndoc());
				psndocVO.setPk_psnjob(psnjobVO.getPk_psnjob());
				psndocVO.setPk_group(psnjobVO.getPk_group());
				psndocVO.setPk_org(hrorg.getPk_org());
				psndocVO.setPk_adminorg(hrorg.getPk_org());
				psndocVO.setPk_psnorg(psnjobVO.getPk_psnorg());
				psndocVO.setBegindate(psnjobVO.getBegindate());
				psndocVO.setEnddate(psnjobVO.getEnddate() == null ? UFLiteralDate
						.getDate(TBMPsndocCommonValue.END_DATA) : psnjobVO.getEnddate());
				psndocVO.setTbm_prop(timeRule.getTotbmpsntype().intValue() == 1 ? TBMPsndocCommonValue.PROP_MANUAL
						: TBMPsndocCommonValue.PROP_MACHINE);
				insertList.add(psndocVO);
				continue;
			}
			if (unFinishPsndoc == null) // ֻ�������ڵ�������ְ��¼��������ְ
				continue;
			// ������п��ڵ�����ʼ��������һ������¼��������֮��Ϊ���⿼�ڵ������������ڿ�ʼ����֮ǰ��ֻ�轫�˿��ڵ���pk_psnjob���ɵ�ǰ������¼��pk_psnjob����
			if (unFinishPsndoc.getBegindate().after(psnjobVO.getBegindate().getDateBefore(1))) {
				TBMPsndocVO oldUnFinishPsndoc = (TBMPsndocVO) unFinishPsndoc.clone();
				unFinishPsndoc.setPk_psnjob(psnjobVO.getPk_psnjob());
				// ��֯�͹�����֯�ı�,����֯û�з����仯�������֯Ҳ�������仯
				if (!unFinishPsndoc.getPk_org().equals(hrorg.getPk_org())) {
					unFinishPsndoc.setPk_adminorg(hrorg.getPk_org());
				}
				unFinishPsndoc.setPk_org(hrorg.getPk_org());
				unFinishPsndoc.setPk_team(null);// ��֯�л���Ҫ���������
				// һ���˵Ŀ��ڵ�����A��֯ƽ�Ƶ�B��֯��Ҫ������A��֯�°����ڶ�Ӧ�ĸ���Ա����Ϣ��������
				// ���������Ա���ڶ��뿼�ڵ������ڶ���ȣ���ֱ��ɾ��������Ա��¼���ɣ�
				// ������ڶβ���ȣ����޸İ�����Ա��������Ϊ���ڵ�����ʼ����ǰһ��
				new TBMPsndocChangeProcessor().processAfterTBMPsndocDelete(oldUnFinishPsndoc);
				updateList.add(unFinishPsndoc);
				continue;
			}
			// ԭ�еĿ��ڵ�������һ��������¼�Ľ�������֮�������������õ�����������һ��
			unFinishPsndoc.setEnddate(psnjobVO.getBegindate().getDateBefore(1));
			// v63 ��������ȷ�ϣ������п��ڵ����ˣ����Զ����뿼�ڵ�������
			// if(isNotIntoTBM) //������Զ����뿼�ڵ�������ֻ����ԭ�еĲ�������
			// continue;
			TBMPsndocVO psndocVO = new TBMPsndocVO();
			psndocVO.setPk_psndoc(psnjobVO.getPk_psndoc());
			psndocVO.setPk_psnjob(psnjobVO.getPk_psnjob());
			psndocVO.setPk_group(psnjobVO.getPk_group());
			psndocVO.setPk_org(hrorg.getPk_org());
			// ����֯û�з����仯�������֯Ҳ�������仯,�ҿ��ڿ��ſ��ڵص�Ҳ���ֲ���,633�޸�����֯û�з����仯���������Ű�
			if (unFinishPsndoc.getPk_org().equals(hrorg.getPk_org())) {
				insertNotArrangeList.add(psndocVO);
				updateNotArrangeList.add(unFinishPsndoc);
				psndocVO.setPk_adminorg(unFinishPsndoc.getPk_adminorg());
				psndocVO.setTimecardid(unFinishPsndoc.getTimecardid());
				psndocVO.setSecondcardid(unFinishPsndoc.getSecondcardid());
				psndocVO.setPk_place(unFinishPsndoc.getPk_place());
				psndocVO.setTbm_prop(unFinishPsndoc.getTbm_prop());
			} else {// ��֯�䶯�˱���Ҫ�����Ű���
				updateList.add(unFinishPsndoc);
				if (timeRule != null) {// �µ������֯û�����ÿ��ڹ�����������֯������뿼��
					insertList.add(psndocVO);
					psndocVO.setPk_adminorg(hrorg.getPk_org());
					psndocVO.setTbm_prop(timeRule.getTotbmpsntype().intValue() == 1 ? TBMPsndocCommonValue.PROP_MANUAL
							: TBMPsndocCommonValue.PROP_MACHINE);
				}
			}
			psndocVO.setPk_psnorg(psnjobVO.getPk_psnorg());
			psndocVO.setBegindate(psnjobVO.getBegindate());
			psndocVO.setEnddate(psnjobVO.getEnddate() == null ? UFLiteralDate.getDate(TBMPsndocCommonValue.END_DATA)
					: psnjobVO.getEnddate());
			// psndocVO.setTbm_prop(timeRule.getTotbmpsntype().intValue()==1 ?
			// TBMPsndocCommonValue.PROP_MANUAL:TBMPsndocCommonValue.PROP_MACHINE);
			// insertList.add(psndocVO);
			// v63,������ԭ����֯�Ŀ��ڵ���������������֯�Ŀ��ڵ���������Ҫ�������ڽ���ת��
			// getLeaveBalanceMM().translateLeave(oldPsnjobVOs[i], psnjobVO);

			// V65����������֯�����š���λ��û�б仯�����Ҳ���仯������ԭ���ģ��������µģ�//post ��Ϊ���ж� tangcht

			if (StringUtils.isNotBlank(unFinishPsndoc.getPk_team())
					&& psnjobVO.getPk_org().equalsIgnoreCase(oldPsnJobvo.getPk_org())
					&& psnjobVO.getPk_dept().equalsIgnoreCase(oldPsnJobvo.getPk_dept())
					&& ((psnjobVO.getPk_post() == null && oldPsnJobvo.getPk_post() == null) || (psnjobVO.getPk_post() != null && psnjobVO
							.getPk_post().equalsIgnoreCase(oldPsnJobvo.getPk_post())))) {
				TeamItemVO teamPsn = NCLocator
						.getInstance()
						.lookup(ITeamQueryServiceForHR.class)
						.queryByPsnandDate(unFinishPsndoc.getPk_team(), unFinishPsndoc.getPk_psndoc(),
								unFinishPsndoc.getBegindate(), unFinishPsndoc.getEnddate())[0];
				if (null != teamPsn) {
					TeamItemVO newTeamPsn = (TeamItemVO) teamPsn.clone();
					newTeamPsn.setDstartdate(psnjobVO.getBegindate());
					newTeamPsn.setPk_psnjob(psnjobVO.getPk_psnjob());
					newTeamPsn.setDr(0);
					insertTeamItemVOs.add(newTeamPsn);
				}
				psndocVO.setPk_team(unFinishPsndoc.getPk_team());
			}
		}
		// ����
		getManageMaintain().delete(deleteList.toArray(new TBMPsndocVO[0]));
		// updateListy��updateNotArrangeList�ϲ������������Ű�,����ɵĹ��������Ĺ�����insert�����
		// getManageMaintain().update(updateList.toArray(new
		// TBMPsndocVO[0]),false);
		updateNotArrangeList.addAll(updateList);
		getManageMaintain().update(updateNotArrangeList.toArray(new TBMPsndocVO[0]), false);
		// ���µĿ��ڵ����Ű��ʱ�������ʱ��εĹ�����������������
		getManageMaintain().insert(insertList.toArray(new TBMPsndocVO[0]), true);
		getManageMaintain().insert(insertNotArrangeList.toArray(new TBMPsndocVO[0]), false);

		if (CollectionUtils.isNotEmpty(insertTeamItemVOs)) {
			new BaseDAO().insertVOList(insertTeamItemVOs);
		}
	}

	/**
	 * �����޸Ĺ�����¼���߼�ְ��¼
	 * 
	 * @param psnJobBefore
	 * @param psnJobAfter
	 * @throws BusinessException
	 */
	private void handleUpdateJobOrPartChg(PsnJobVO[] oldPsnjobVOs, PsnJobVO[] psnjobVOs) throws BusinessException {
		if (ArrayUtils.isEmpty(psnjobVOs))
			return;
		String pk_org = psnjobVOs[0].getPk_org();
		OrgVO hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org);
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class)
				.queryByOrgWithoutException(hrorg.getPk_org());
		Map<String, TBMPsndocVO> psndocMap = getManageMaintain().queryUnFinishPsnDoc(
				StringPiecer.getStrArray(psnjobVOs, PsnJobVO.PK_PSNDOC));
		// ��û�п��ڹ����û��δ��ɵĿ��ڵ��������ô���
		if (timeRule == null || MapUtils.isEmpty(psndocMap))
			return;
		// boolean isNotIntoTBM =
		// timeRule.getTotbmpsntype()==null||0==timeRule.getTotbmpsntype().intValue();
		// //������Ա�Ƿ�ת�뿼�ڵ���
		List<TBMPsndocVO> insertList = new ArrayList<TBMPsndocVO>();
		List<TBMPsndocVO> updateList = new ArrayList<TBMPsndocVO>();
		List<TBMPsndocVO> deleteList = new ArrayList<TBMPsndocVO>();
		List<TBMPsndocVO> deleteCalendarList = new ArrayList<TBMPsndocVO>();

		for (int i = 0; i < psnjobVOs.length; i++) {

			PsnJobVO psnjobVO = psnjobVOs[i];

			// �޸ĵ����ݺͿ��ڵ���û�й�ϵ���ô���,����������֯����ʼ���ڡ�����������Щֵ��û�б仯
			PsnJobVO oldVO = oldPsnjobVOs[i];
			if (psnjobVO.getPk_psnjob().equalsIgnoreCase(oldVO.getPk_psnjob())
					&& psnjobVO.getBegindate().isSameDate(oldVO.getBegindate())
					&& psnjobVO.getPk_org().equals(oldVO.getPk_org())) {
				UFLiteralDate enddate = psnjobVO.getEnddate();
				UFLiteralDate oldenddate = oldVO.getEnddate();
				if (null == enddate && null == oldenddate)
					continue;
				if (null != enddate && null != oldenddate && enddate.isSameDate(oldenddate))
					continue;
			}

			// ��ְ��¼������
			if (oldPsnjobVOs[i].getTrnsevent() != null
					&& oldPsnjobVOs[i].getTrnsevent().intValue() == TrnseventEnum.DISMISSION.toIntValue())
				continue;
			TBMPsndocVO unFinishPsndoc = psndocMap.get(psnjobVOs[i].getPk_psnjob());
			// ����޸ĵĲ�����Ч�ؿ��ڵ�����Ӧ�Ĺ�����¼�������κδ���
			if (unFinishPsndoc == null || !oldPsnjobVOs[i].getPk_psnjob().equals(unFinishPsndoc.getPk_psnjob()))
				continue;
			if (psnjobVO.getPk_org().equals(oldPsnjobVOs[i].getPk_org())) {// �����֯û��
				// ����޸ĵ������µĹ�����¼�������޸�ǰ�޽������ڣ��޸ĺ��н������ڣ��������Ա�Ŀ��ڵ���
				if (oldPsnjobVOs[i].getEnddate() == null && psnjobVO.getEnddate() != null) {
					// ������ڵ�����ʼ�����ڹ�����¼��������֮����ֱ��ɾ���������ڵ�����¼
					if (unFinishPsndoc.getBegindate().after(psnjobVO.getEnddate())) {
						deleteList.add(unFinishPsndoc);
						continue;
					}
					// ���������½�������
					unFinishPsndoc.setEnddate(psnjobVO.getEnddate());
					updateList.add(unFinishPsndoc);
					continue;
				} else {
					unFinishPsndoc.setPk_psndoc(psnjobVO.getPk_psndoc());
					unFinishPsndoc.setPk_psnjob(psnjobVO.getPk_psnjob());
					unFinishPsndoc.setPk_group(psnjobVO.getPk_group());
					unFinishPsndoc.setPk_org(hrorg.getPk_org());
					unFinishPsndoc.setPk_adminorg(hrorg.getPk_org());
					unFinishPsndoc.setPk_psnorg(psnjobVO.getPk_psnorg());
					unFinishPsndoc.setEnddate(psnjobVO.getEnddate() == null ? UFLiteralDate
							.getDate(TBMPsndocCommonValue.END_DATA) : psnjobVO.getEnddate());
					updateList.add(unFinishPsndoc);
				}
				continue;
			}
			// 2013-08-12
			// �޸�������δ�����Ŀ��ڵ����ˣ����Զ����뿼�ڵ������������ǳ�����a��bҵ��Ԫ��ͬһ��hr��֯�£�������ְ��¼a->b��ʹhr��֯�Ŀ��ڹ����Զ����뿼�ڵ�����Ҳ��Ҫɾ��
			// if(isNotIntoT BM){//�������֯�Զ�ת�뿼�ڵ�������ֱ�ӽ�����֯�ϸ���Ա�Ŀ��ڵ���ɾ��
			// deleteList.add(unFinishPsndoc);
			// }else{//�������֯��������ֱ�ӽ��ϵĿ��ڵ������µ�����֯��
			// ����ֱ���޸Ļ����ԭҵ��Ԫ�µĹ�������ɾ����������ҵ��Ԫ�µ����������ϵ�����
			TBMPsndocVO clearCalendarPsn = (TBMPsndocVO) unFinishPsndoc.clone();
			deleteCalendarList.add(clearCalendarPsn);
			unFinishPsndoc.setPk_psndoc(psnjobVO.getPk_psndoc());
			unFinishPsndoc.setPk_psnjob(psnjobVO.getPk_psnjob());
			unFinishPsndoc.setPk_group(psnjobVO.getPk_group());
			// ��������Դ��֯�����仯�˲Ÿ��¹�����֯
			if (!unFinishPsndoc.getPk_org().equals(hrorg.getPk_org()))
				unFinishPsndoc.setPk_adminorg(hrorg.getPk_org());
			unFinishPsndoc.setPk_org(hrorg.getPk_org());
			unFinishPsndoc.setPk_psnorg(psnjobVO.getPk_psnorg());
			unFinishPsndoc.setEnddate(psnjobVO.getEnddate() == null ? UFLiteralDate
					.getDate(TBMPsndocCommonValue.END_DATA) : psnjobVO.getEnddate());
			// ��֯�仯�˰���Ӧ�������
			unFinishPsndoc.setPk_team(null);
			updateList.add(unFinishPsndoc);
			// unFinishPsndoc.setBegindate(psnjobVO.getBegindate());// ʹ��ԭ���Ŀ�ʼ����
			// unFinishPsndoc.setTbm_prop(timeRule.getTotbmpsntype().intValue()==1
			// ?
			// TBMPsndocCommonValue.PROP_MANUAL:TBMPsndocCommonValue.PROP_MACHINE);
			// }
		}
		// ����
		// ҵ��Ԫ�仯ֱ���޸Ŀ��ڵ����ģ�������ϵ�����
		new PsnCalendarDAO().deleteByTBMPsndocVOs(deleteCalendarList.toArray(new TBMPsndocVO[0]));
		getManageMaintain().delete(deleteList.toArray(new TBMPsndocVO[0]));
		getManageMaintain().update(updateList.toArray(new TBMPsndocVO[0]), true);
		getManageMaintain().insert(insertList.toArray(new TBMPsndocVO[0]), true);
	}

	/**
	 * ɾ�����ڵ���
	 * 
	 * @param psnJobVO
	 * @throws BusinessException
	 */
	private void deleteTBMPsndocByPsnjob(PsnJobVO[] psnjobVOs, boolean isPartJob) throws BusinessException {
		if (ArrayUtils.isEmpty(psnjobVOs))
			return;
		String condition = TBMPsndocVO.PK_PSNJOB + " in("
				+ new InSQLCreator().getInSQL(StringPiecer.getStrArray(psnjobVOs, PsnJobVO.PK_PSNJOB)) + ") order by "
				+ TBMPsndocVO.ENDDATE + " desc ";
		TBMPsndocVO[] psndocVOs = getManageMaintain().queryByCondition(condition);
		if (ArrayUtils.isEmpty(psndocVOs))
			return;
		Map<String, TBMPsndocVO[]> psndocMap = CommonUtils.group2ArrayByField(TBMPsndocVO.PK_PSNJOB, psndocVOs);
		Map<String, TBMPsndocVO> unFinishMap = getManageMaintain().queryUnFinishPsnDoc(
				StringPiecer.getStrArray(psnjobVOs, PsnJobVO.PK_PSNDOC));
		List<TBMPsndocVO> deleteList = new ArrayList<TBMPsndocVO>();
		for (PsnJobVO psnjobVO : psnjobVOs) {
			TBMPsndocVO[] psndocs = psndocMap.get(psnjobVO.getPk_psnjob());
			// û�ж�Ӧ�Ŀ��ڵ���
			if (ArrayUtils.isEmpty(psndocs))
				continue;
			String unFinishPsnjob = MapUtils.isEmpty(unFinishMap) ? null
					: (unFinishMap.get(psnjobVO.getPk_psnjob()) == null ? null : psnjobVO.getPk_psnjob());
			// if(isPartJob &&
			// !psnjobVO.getPk_psnjob().equals(unFinishPsnjob))//����Ǽ�ְ���޸ĵĲ�����Ч�ؿ��ڵ�����Ӧ�Ĺ�����¼�������κδ���
			if (unFinishPsnjob == null)
				continue;
			deleteList.add(psndocs[0]);// ɾ����һ���������ļ�¼
		}
		// ����
		getManageMaintain().delete(deleteList.toArray(new TBMPsndocVO[0]));
	}

}