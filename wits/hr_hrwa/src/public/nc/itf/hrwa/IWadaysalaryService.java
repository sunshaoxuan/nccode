package nc.itf.hrwa;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

public interface IWadaysalaryService {
	/**
	 * 通過人力資源組織，計算日薪
	 * 
	 * @param pk_hrorg
	 * @param calculDate
	 * @return
	 * @throws BusinessException
	 */
	public void calculSalaryByHrorg(String pk_hrorg, UFLiteralDate calculDate) throws BusinessException;

	/**
	 * 重算部分人员的日薪
	 * 
	 * @param pk_psnorg
	 * @param calculDate
	 * @param pk_psndoc
	 * @param pk_wa_items
	 * @throws BusinessException
	 */
	public void calculSalaryByWaItem(String pk_hrorg, String pk_wa_class, UFLiteralDate calculDate, String pk_psndoc,
			String[] pk_wa_items) throws BusinessException;

	/**
	 * 刪除指定日期的日薪數據
	 * 
	 * @param pk_hrorg
	 * @param calculdate
	 * @param continueTime
	 * @throws BusinessException
	 */
	public void deleteDaySalary(String pk_hrorg, UFLiteralDate calculdate, int continueTime) throws BusinessException;

	/**
	 * 檢驗指定範圍內的日薪是否計算成功，如未計算，則重新計算
	 * 
	 * @param pk_hrorg
	 * @param calculdate
	 * @param checkrange
	 * @throws BusinessException
	 */
	public void checkDaySalaryAndCalculSalary(String pk_hrorg, UFLiteralDate calculdate, int checkrange)
			throws BusinessException;

	/**
	 * 檢驗指定範圍內的日薪是否計算成功，如未計算，則重新計算，如计算结果错误，也重新计算
	 * 
	 * @param pk_psndocs
	 * @param begindate
	 * @param enddate
	 * @throws BusinessException
	 */
	public void checkDaySalaryAndCalculSalary(String pk_wa_class, String[] pk_psndocs, UFLiteralDate begindate,
			UFLiteralDate enddate, String pk_wa_item) throws BusinessException;

	/**
	 * 薪資計算前檢驗指定範圍內的日薪是否計算成功，如未計算，則重新計算，如计算结果错误，也重新计算
	 * 
	 * @param pk_wa_class
	 * @param psnWhere
	 * @throws BusinessException
	 * @version 细化粒度,按照薪资项目进行计算
	 */
	public void checkDaySalaryAndCalculSalary(String pk_wa_class, String[] pk_psndocs, String cyear, String cperiod)
			throws BusinessException;

}
