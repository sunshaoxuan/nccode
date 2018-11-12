package nc.itf.twhr;

import java.util.List;
import java.util.Map;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * Calculate Taiwan Labor & Health Insurance
 * 
 * @author Parker Sun
 * 
 */
public interface ICalculateTWNHI {
	public void calculate(String pk_org, String acc_year, String acc_month)
			throws Exception;

	/**
	 * Get NHI class map
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public List<Map> getNHIClassMap() throws BusinessException;

	/**
	 * Update extend NHI infoset with wadata on PayAction been clicked.
	 * 
	 * @param pk_group
	 *            集團
	 * @param pk_org
	 *            組織
	 * @param pk_wa_class
	 *            薪資方案
	 * @param pk_periodscheme
	 *            期間方案
	 * @param pk_wa_period
	 *            薪資期間
	 * @param payDate
	 *            發放日期
	 * @throws BusinessException
	 */
	public void updateExtendNHIInfo(String pk_group, String pk_org,
			String pk_wa_class, String pk_periodscheme, String pk_wa_period,
			UFDate payDate) throws BusinessException;

	/**
	 * Delete extend NHI infoset record on UnPayAction been clicked.
	 * 
	 * @param pk_group
	 *            集團
	 * @param pk_org
	 *            組織
	 * @param pk_wa_class
	 *            薪資方案
	 * @param pk_periodscheme
	 *            期間方案
	 * @param pk_wa_period
	 *            薪資期間
	 * @param payDate
	 *            發放日期
	 * @throws BusinessException
	 */
	public void deleteExtendNHIInfo(String pk_group, String pk_org,
			String pk_wa_class, String pk_periodscheme, String pk_wa_period,
			UFDate payDate) throws BusinessException;
}
