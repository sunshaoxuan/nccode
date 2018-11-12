package nc.itf.hi;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;

public interface IPsndocSubInfoService4JFS {
	/**
	 * ͬ���ڽ���Ͷ��н�Y�ͼ���
	 * 
	 * @param pk_org
	 *            �M��
	 * @param cyear
	 *            ��
	 * @param cperiod
	 *            ���g
	 * @throws BusinessException
	 */
	public void renewRange(String pk_org, String cyear, String cperiod)
			throws BusinessException;

	/**
	 * ��ָ�����gƽ��н�Yͬ���ڽ���Ͷ��н�Y�ͼ���
	 * 
	 * @param pk_org
	 *            �M��
	 * @param pk_psndocs
	 *            �ˆT�б�
	 * @param pk_wa_class
	 *            ����н�Y����
	 * @param startperiod
	 *            ��ʼ���g
	 * @param endperiod
	 *            ��ֹ���g
	 * @param effectivedate
	 *            ��Ч����
	 * @throws BusinessException
	 */
	public void renewRangeEx(String pk_org, String[] pk_psndocs,
			String pk_wa_class, String startperiod, String endperiod,
			UFDate effectivedate) throws BusinessException;

	/**
	 * ��ָ�����gƽ��н�Yͬ���F����н����
	 * 
	 * @param pk_org
	 *            �M��
	 * @param pk_psndocs
	 *            �ˆT�б�
	 * @param pk_wa_class
	 *            ����н�Y����
	 * @param startperiod
	 *            ��ʼ���g
	 * @param endperiod
	 *            ��ֹ���g
	 * @param effectivedate
	 *            ��Ч����
	 * @throws BusinessException
	 */
	public void renewGroupIns(String pk_org, String[] pk_psndocs,
			String pk_wa_class, String startperiod, String endperiod,
			UFDate effectivedate) throws BusinessException;

	/**
	 * �x�Kֹ�ڽ����O��
	 * 
	 * @param pk_org
	 *            �M��
	 * @param pk_psndoc
	 *            �ˆTpk
	 * @param enddate
	 *            �Y������
	 * @throws BusinessException
	 */
	public void dismissPsnNHI(String pk_org, String pk_psndoc,
			UFLiteralDate enddate) throws BusinessException;

	/**
	 * �x�Kֹ�F���O��
	 * 
	 * @param pk_org
	 *            �M��
	 * @param pk_psndoc
	 *            �ˆTpk
	 * @param enddate
	 *            �Y������
	 * @throws BusinessException
	 */
	public void dismissPsnGroupIns(String pk_org, String pk_psndoc,
			UFLiteralDate enddate) throws BusinessException;

	/**
	 * Ӌ��T���F���M��
	 * 
	 * @param pk_org
	 *            �M��
	 * @param pk_wa_class
	 *            н�Y����
	 * @param cYear
	 *            �ڼ���
	 * @param cPeriod
	 *            �ڼ��
	 * @throws BusinessException
	 */
	public void calculateGroupIns(String pk_org, String pk_wa_class,
			String cYear, String cPeriod) throws BusinessException;

	/**
	 * ָ��Ա���趨�Ƿ�����ű�������
	 * 
	 * @param pk_psndoc
	 *            Ա��ID
	 * @param pk_psndoc_sub
	 * @return
	 * @throws BusinessException
	 */
	public boolean isExistsGroupInsCalculateResults(String pk_psndoc,
			String pk_psndoc_sub) throws BusinessException;

	/**
	 * ���Ʉڽ����O��
	 * 
	 * @param pk_org
	 *            �M��
	 * @param pk_psndocs
	 *            �ˆT�б�
	 * @throws BusinessException
	 */
	public void generatePsnNHI(String pk_org, String[] pk_psndocs)
			throws BusinessException;

	/**
	 * ���ɈF���O��
	 * 
	 * @param pk_org
	 *            �M��
	 * @param pk_psndocs
	 *            �ˆT�б�
	 * @throws BusinessException
	 */
	public void generateGroupIns(String pk_org, String[] pk_psndocs)
			throws BusinessException;

	/**
	 * ������Ա����ȡ�ű���н����
	 * 
	 * @param pk_org
	 *            ��֯
	 * @param pk_psndoc
	 *            ��Ա
	 * @param salaryDate
	 *            н������
	 * @return
	 * @throws BusinessException
	 */
	public UFDouble getGroupInsWadocBaseSalaryByPsnDate(String pk_org, String pk_psndoc,
			UFLiteralDate salaryDate) throws BusinessException;
}