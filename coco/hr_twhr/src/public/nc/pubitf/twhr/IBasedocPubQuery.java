package nc.pubitf.twhr;

import nc.vo.pub.BusinessException;
import nc.vo.twhr.basedoc.BaseDocVO;

public interface IBasedocPubQuery {
	/**
	 * 获取所有基本档案
	 * 
	 * @return 基本档案
	 * @throws BusinessException
	 */
	public BaseDocVO[] queryAllBaseDoc(String pk_org) throws BusinessException;

	/**
	 * 根据指定组织及编码取基本档案
	 * 
	 * @param pk_org
	 *            组织PK
	 * @param strCode
	 *            参数编码
	 * @return 参数值对象
	 * @throws BusinessException
	 */
	public BaseDocVO queryBaseDocByCode(String pk_org, String strCode)
			throws BusinessException;
	/**
	 * 根据指定编码取基本档案
	 * 
	 * 
	 * @param strCode
	 *            参数编码
	 * @return 参数值对象
	 * @throws BusinessException
	 */
	public BaseDocVO queryBaseDocByCode(String strCode)
			throws BusinessException;
}
