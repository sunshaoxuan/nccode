package nc.itf.om;

import java.util.List;

import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.HRDeptAdjustVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;

public interface IDeptAdjustService {
	
	/**
	 * ���ŵ�pk_dept_v����Ϊ��,�˱�Ǳ�ʾ�˲���Ϊ����,�ް汾�Ĳ���
	 * @return
	 */
	String getNewDeptVerPK();
	
	/**
	 * ���ݲ��Ų�ѯ��ǰ���ŵİ汾PK
	 * @param pk_dept 
	 * @return ��ǰ���ŵİ汾PK
	 * ҵ���߼�:��ѯ��ǰ���ŵİ汾PK
	 */
	String queryLastDeptByPk(String pk_dept);
	
	/**
	 * ִ�в��Ű汾������̨�΄գ����������޸ģ�
	 * @param date
	 * @throws BusinessException
	 * ҵ���߼�:ִ����Ч����Ϊ�����ڵ����в��Ű汾������,
	 * 1.�������ڵĵ���vo�ϴ洢���ֶ���Ϣ,�洢��org_dept,org_dept_v,org_orgs,org_orgs_v,org_reportorg.���Ҹ��¹�����ʮ���ű�(�ο��������޸��߼�).
	 * 2.���ű���Ͳ������Ƹ���ʱ, �����ˆT��ӛ�( �ѽ��Y������ӛ䛲�����)((�ο��������޸��߼�))
	 * 3.��Ա������¼��Ҫ����(�ο��������޸��߼�)4.islastversion�������±�־,�ò���������¼��Ϊfalse(�ο��������޸��߼�) 
	 * 4.iseffective����ִ�б��.iseffectiveΪfalse�Ĳ�ִ��.
	 * (5.�����ο��������޸��߼�)
	 */
	void executeDeptVersion (UFLiteralDate date) throws BusinessException;
	
	/*
	 * ��ѯ�Ƿ��Ѵ���ָ�����Tδ��Ч���{����Ո��					
	* pk_dept					
	* ����UFBoolean True���ڣ�False������
	* ҵ���߼�:��ѯ�Ƿ��Ѵ���ָ�����Tδ��Ч���{����Ո�μ�������Ч���ڴ��ڵ�ǰ���ڵĴ˲��ŵĵ�����.	
	*/
	UFBoolean isExistDeptAdj(String pk_dept,String pk_adjdept)  throws BusinessException ;					
	
	/**
	 * ����У�����1--���ŵ���Ψһ��(nc.impl.pubapp.pattern.rule.IRule<BatchOperateVO>)
	 * @param vo
	 * @throws BusinessException
	 * ҵ���߼�:
	 * 1. ͬһ���Tֻ����һ����Ч���ڴ��ڵ�ǰ���چΓ�(��̨����)
	 */
	UFBoolean validateDept(HRDeptAdjustVO vo) throws BusinessException;

	/**
	 * ������Ϣ������д
	 * @param deptVO
	 * @throws BusinessException
	 * ҵ���߼�:��������ʱ,����������ڴ��ڵ�ǰ����,
	 * ��ô��дһ�ʵ��ݵ����ڵ�,
	 * ��deptVO��ֵ��HRDeptAdjustVO�ϵ��ֶ�,��������д��ǰ�û�,
	 * ����������д��ǰ����,��Ч����Ϊ���ŵĳ�������,��������Ϊvo�ϵĲ���.
	 * ����ò���pk�Ѿ�����,��ô�����ڽ��л�д,ͬʱ�ڱ��沿�ŵ�ʱ���׳��쳣.
	 * 
	 */
	AggHRDeptVO writeBack4DeptAdd(AggHRDeptVO deptVO)  throws BusinessException ;
	/**
	 * ������Ϣȡ��������д
	 * @param deptVO
	 * @throws BusinessException
	 * ҵ���߼�:����ȡ������ʱ,����������ڴ��ڵ�ǰ����,
	 * ��ô��дһ�ʵ��ݵ����ڵ�,
	 * ��deptVO��ֵ��HRDeptAdjustVO�ϵ��ֶ�,��������д��ǰ�û�,
	 * ����������д��ǰ����,��Ч����Ϊ���ŵĳ�������,��������Ϊvo�ϵĲ���.
	 */
	AggHRDeptVO writeBack4DeptUnCancel(AggHRDeptVO deptVO,UFLiteralDate effective)  throws BusinessException ;
	
	/**
	 * pk_org ������Դ��֯pk
	 * @param pk_org
	 * @wheresql ǰ�˲�ԃ�l��
	 * @returnList<HRDeptVO>
	 * @throws BusinessException
	 * 
	 * �ڲ�����Ϣ�ڵ�,��Ҫ���˳���δ��Ч�Ĳ���,�ڴ˽ڵ��ѯ����������Դ��֯��,
	 * ������Ч�����ڵ�ǰ����֮�����������δ��Ч�Ĳ���,,
	 * ��װ��HRDeptVO��list����,��������Ϣ�ڵ��ѯδ��Ч����ʹ��
	 */
	List<HRDeptVO> queryOFutureDept(String pk_org,String whereSql)  throws BusinessException ;
	/**
	 * 
	 * @param HRDeptVO
	 * @return
	 * @throws BusinessException
	 * ҵ���߼�:�ڲ��Žڵ��ѯδ��Ч�Ĳ���,�Լ��ڽ��к�̨�����дʱ
	 * ,��Ҫ������VOת���ɲ���VO
	 */
	HRDeptVO HRDeptAdjust2HRDeptVO(HRDeptAdjustVO hRDeptAdjustVO)  throws BusinessException;
	/**
	 * ���У�����2,���ܽ���ɾ��,,ɾ��ʱ����ɾ��������Ϣ������
	 * @param date
	 * @throws BusinessException
	 */
	void executeDeptCancel(UFLiteralDate date) throws BusinessException;
	/**
	 * void validatePsn(HRDeptAdjustVO vo) throws BusinessException;
	 * @param vo
	 * @throws BusinessException
	 * ҵ���߼�:
	 * 2. У����Ա����������Ч�����Ƿ��ڲ�����Ч����֮��
	 */
	UFBoolean validatePsn(HRDeptAdjustVO vo) throws BusinessException;
	/**
	 * ֻ����ɾ��ִ�б�־δ�򹴵�,������Ч�����ڵ�ǰ����֮��ĵ���.
	 * ɾ��ʱ,'��������'�д���������δ���������ŵļ�¼ʱ��ɾ������������ʱ����;
	 * �Ѿ����¼�����ʱ,ɾ������
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	UFBoolean validateDel(HRDeptAdjustVO vo) throws BusinessException;

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
