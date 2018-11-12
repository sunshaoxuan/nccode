package nc.itf.ta;

import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;
import nc.vo.ta.psncalendar.QueryScopeEnum;

public interface IPsnCalendarQueryMaintain {

	/**
	 * ���ݿ�ʼ�������ڣ���Ա������ѯ��������
	 * @param pk_hrorg,HR��֯����
	 * @param beginDate,��ʼ����
	 * @param endDate,��������
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	PsnJobCalendarVO[] 
	queryCalendarVOByCondition(String pk_hrorg,FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate) throws BusinessException;
	

	/**
	 * ���ݿ�ʼ�������ڡ���Ա���������ѯ��������
	 * @param pk_hrorg
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	PsnJobCalendarVO[] queryCalendarVOByPsndocs(String pk_hrorg,String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException ;
	
	/**
	 * �������ڷ�Χ����Ա�������Լ���ѯ��Χ��ѯ��Ա��������
	 * @param pk_hrorg
	 * @param beginDate
	 * @param endDate
	 * @param condition
	 * @param queryScope����ѯ��Χ��������Ա����δ�Ű���Ա���Ű������Ա�������Ű���Ա
	 * @return
	 * @throws BusinessException
	 */
	PsnJobCalendarVO[]
	queryCalendarVOByCondition(String pk_hrorg,
			FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate,QueryScopeEnum queryScope) throws BusinessException;
	
	
	/**
	 * �������ڷ�Χ����Ա�������Լ���ѯ��Χ��ѯ��Ա��������
	 * ��������ʹ��
	 * @param pk_dept ��������
	 * @param containsSubDepts �Ƿ�����¼�����
	 * @param beginDate
	 * @param endDate
	 * @param condition
	 * @param queryScope����ѯ��Χ��������Ա����δ�Ű���Ա���Ű������Ա�������Ű���Ա
	 * @return
	 * @throws BusinessException
	 */
	PsnJobCalendarVO[]
	queryCalendarVOByConditionAndDept(String pk_dept,boolean containsSubDepts,
			FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate,QueryScopeEnum queryScope) throws BusinessException;
	
	/**
	 * ���ݲ��š���Ա���������ڷ�Χ��ѯ��Ա��������
	 * ��������ʹ��
	 * @param pk_dept
	 * @param pk_psndoc
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	PsnJobCalendarVO
	                    	queryCalendarVOByConditionAndDept(String pk_dept,String pk_psndoc,UFLiteralDate beginDate,UFLiteralDate endDate) throws BusinessException;
	
	/**
	 * �������ڷ�Χ����Ա�������Ƿ񸲸����й���������ѯ��Ա������ѭ���Ű��Ĭ���Ű�ѡ�����������Ա�б�ȷ��
	 * @param pk_org��isHROrgΪtrue����HR��֯��Ϊfalse����ҵ��Ԫ
	 * @param qs
	 * @param beginDate
	 * @param endDate
	 * @param isOverrideExistCalendar��Ϊtrue����ʾ��Ҫ�������й����������������з�����������Ա��Ϊfalse��
	 * ��ʾ���������й�����������ֻ������ڷ�Χ�ڹ�����������������Ա����������Ա�������
	 * @param isHROrg,pk_org��HR��֯����ҵ��Ԫ
	 * @return��������������Ա������¼��ֻ����ְ��Ϣ��û�п�����Ϣ�͹���������Ϣ
	 * @throws BusinessException
	 */
	PsnJobVO[] queryPsnJobVOsByConditionAndOverrideOrg(String pk_org,
			FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate,boolean isOverrideExistCalendar,boolean isHROrg) throws BusinessException;
	/**
	 * ������Ա�����������������������ղ�ѯ��������
	 * @param pk_org
	 * @param pk_psndoc
	 * @param date
	 * @return
	 * @throws BusinessException
	 */
	AggPsnCalendar queryByPsnDate(String pk_psndoc,UFLiteralDate date) throws BusinessException;
	
	/**
	 * ��ȡ��������
	 * @param pk_hrorg
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	GeneralVO[] getExportDatas(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate,QueryScopeEnum queryScope) throws BusinessException;
	
	/**
	 * ��ȡ��������
	 * @param pk_hrorg
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	GeneralVO[] getExportDatas(String pk_org,PsnJobCalendarVO[] calendarVOs, UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException;
	
	/**
	 * ��ѯָ�����š����������ڷ�Χ�ڵ���Ա
	 * ���ھ�������
	 * @param pk_dept
	 * @param containsSunDepts
	 * @param fromWhereSQL
	 * @param beginDate
	 * @param endDate
	 * @param queryScope
	 * @return
	 * @throws BusinessException
	 */
	PsndocVO[] queryPsndocVOsByConditionAndDept(String pk_dept,boolean containsSubDepts,FromWhereSQL fromWhereSQL,UFLiteralDate beginDate,UFLiteralDate endDate,QueryScopeEnum queryScope)throws BusinessException;
	
	/**
	 * ������Ա�������ڷ�Χ��ѯ��������
	 * ����Ա������
	 * @param pk_psndoc
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	PsnJobCalendarVO queryByPsnDates(String pk_psndoc,UFLiteralDate beginDate,UFLiteralDate endDate)throws BusinessException;
		
	/**
	 * �������ڷ�Χ����Ա�������Ƿ񸲸����й���������ѯ��Ա������ѭ���Ű��Ĭ���Ű�ѡ�����������Ա�б�ȷ��
	 * @param pk_org��isHROrgΪtrue����HR��֯��Ϊfalse����ҵ��Ԫ
	 * @param qs
	 * @param beginDate
	 * @param endDate
	 * @param isOverrideExistCalendar��Ϊtrue����ʾ��Ҫ�������й����������������з�����������Ա��Ϊfalse��
	 * ��ʾ���������й�����������ֻ������ڷ�Χ�ڹ�����������������Ա����������Ա�������
	 * @param isHROrg,pk_org��HR��֯����ҵ��Ԫ
	 * @param businessUnitFlag �Ƿ��޶�ҵ��Ԫ
	 * @return��������������Ա������¼��ֻ����ְ��Ϣ��û�п�����Ϣ�͹���������Ϣ
	 * @throws BusinessException
	 */
	PsnJobVO[] queryPsnJobVOsByConditionAndOverrideOrgWithUnit(String pk_org,
			FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate, boolean override, boolean isHROrg,
			boolean businessUnitFlag)throws BusinessException;
}