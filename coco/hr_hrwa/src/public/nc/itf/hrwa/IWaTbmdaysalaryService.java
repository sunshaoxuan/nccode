package nc.itf.hrwa;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

public interface IWaTbmdaysalaryService {
	
	/**
	 * ͨ�^�����YԴ�M����Ӌ�㿼����н
	 * @param pk_hrorg 
	 * @param calculDate
	 * @return
	 * @throws BusinessException
	 */
	public void calculTbmSalaryByHrorg(String pk_hrorg,UFLiteralDate calculDate) throws BusinessException;
	/**
	 * ���㲿����Ա�Ŀ�����н
	 * @param pk_psnorg
	 * @param calculDate
	 * @param pk_psndoc
	 * @param pk_wa_items
	 * @throws BusinessException
	 */
	public void calculTbmSalaryByWaItem(String pk_hrorg,UFLiteralDate calculDate,String pk_psndoc,String[] pk_wa_items) throws BusinessException;
	/**
	 * �h��ָ�����ڵ���н����
	 * @param pk_hrorg
	 * @param calculdate
	 * @param continueTime
	 * @throws BusinessException
	 */
	public void deleteTbmDaySalary(String pk_hrorg,UFLiteralDate calculdate,int continueTime)throws BusinessException;
	/**
	 * �z�ָ�������ȵ���н�Ƿ�Ӌ��ɹ�����δӋ�㣬�t����Ӌ��
	 * @param pk_hrorg
	 * @param calculdate
	 * @param checkrange
	 * @throws BusinessException
	 */
	public void checkTbmDaySalaryAndCalculSalary(String pk_hrorg,UFLiteralDate calculdate,int checkrange) throws BusinessException;
	/**
	 * �z�ָ�������ȵĿ���н���Ƿ�Ӌ��ɹ�����δӋ�㣬�t����Ӌ�㣬�����������Ҳ���¼���
	 * @param pk_psndocs
	 * @param begindate
	 * @param enddate
	 * @throws BusinessException
	 */
	public void checkTbmDaysalaryAndRecalculate(String[] pk_psndocs,UFLiteralDate begindate,UFLiteralDate enddate) throws BusinessException;

	public int getSysintValue(String pk_org, String initcode)
			throws BusinessException;
	
	public double getTbmSalaryNum(String pk_hrorg, UFLiteralDate calculDate, int tbmnumtype) throws BusinessException;
}