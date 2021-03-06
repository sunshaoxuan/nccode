package nc.itf.hr.wa;

import nc.vo.hr.caculate.CaculateTypeVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.paydata.DataSVO;
import nc.vo.wa.paydata.PsndocWaVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;

/**
 * 薪资发放接口类
 * 
 * @author: zhangg
 * @date: 2009-11-23 下午01:19:34
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public interface IPaydataManageService {
	/**
	 * 薪資解密臨時表：已解密wa_data行
	 */
	public static String DECRYPTEDPKTABLENAME = "wa_cacu_decryptedpk";

	public void update(Object vo, WaLoginVO waLoginVO) throws BusinessException;

	/**
	 * 保存显示设置
	 * 
	 * @author liangxr on 2010-5-26
	 * @param classItemVOs
	 * @return
	 * @throws BusinessException
	 */
	public int updateClassItemVOsDisplayFlg(WaClassItemVO[] classItemVOs) throws BusinessException;

	/**
	 * 审核
	 * 
	 * @author liangxr on 2010-6-18
	 * @param waLoginVO
	 * @param whereCondition
	 * @param isRangeAll
	 * @throws nc.vo.pub.BusinessException
	 */
	public void onCheck(WaLoginVO waLoginVO, String whereCondition, Boolean isRangeAll)
			throws nc.vo.pub.BusinessException;

	/**
	 * 取消审核
	 * 
	 * @author liangxr on 2010-6-18
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	public void onUnCheck(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll)
			throws nc.vo.pub.BusinessException;

	/**
	 * 发放
	 */
	public void onPay(WaLoginContext loginContext) throws nc.vo.pub.BusinessException;

	/**
	 * 薪资发放次数数据汇总 本次计税基数等于每个人员的最后一次发放数据中本次计税基数的值； 已扣税基数和已扣税等于0；
	 * 其他数值型项目等于所有发放次数中该项目的合计值； 字符型和日期型项目等于每个人员最后一次发放数据中该项目的值。
	 * 
	 * @param waLoginVO
	 * @throws BusinessException
	 */
	public void collectWaTimesData(WaLoginVO waLoginVO) throws BusinessException;

	/**
	 * 取消发放
	 * 
	 * @author liangxr on 2010-5-26
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	public void onUnPay(WaLoginVO waLoginVO) throws nc.vo.pub.BusinessException;

	public void onSaveDataSVOs(WaLoginVO waLoginVO, DataSVO[] dataSVOs) throws BusinessException;

	/**
	 * 替换
	 * 
	 * @author liangxr on 2010-5-26
	 * @param waLoginVO
	 * @param whereCondition
	 * @param replaceItem
	 * @param formula
	 * @throws BusinessException
	 */
	public void onReplace(WaLoginVO waLoginVO, String whereCondition, WaClassItemVO replaceItem, String formula,
			SuperVO... superVOs) throws BusinessException;

	/**
	 * 汇总
	 * 
	 * @author liangxr on 2010-5-26
	 * @param aRecaVO
	 * @throws nc.vo.pub.BusinessException
	 */
	void reTotal(WaLoginVO waLoginVO) throws nc.vo.pub.BusinessException;

	/**
	 * 計算前檢查
	 * 
	 * @author sunsx
	 * @since 2019-05-05 V6.5
	 * 
	 * @param loginContext
	 * @param caculateTypeVO
	 * @param condition
	 * @param superVOs
	 * @throws BusinessException
	 */
	public void onCalculateCheck(WaLoginContext loginContext, CaculateTypeVO caculateTypeVO, String condition,
			SuperVO... superVOs) throws BusinessException;

	/**
	 * 计算
	 * 
	 * @author liangxr on 2010-7-7
	 * @param loginContext
	 * @param caculateTypeVO
	 * @param conditiupdateCalFlag4OnTimeon
	 * @throws BusinessException
	 */
	public void onCaculate(WaLoginContext loginContext, CaculateTypeVO caculateTypeVO, String condition,
			SuperVO... superVOs) throws BusinessException;

	/**
	 * 重新计算跟二代健保有关的薪资项目
	 * 
	 * @author Ares.Tank 2018-10-23 17:06:49
	 * @param loginContext
	 * @param caculateTypeVO
	 * @param conditiupdateCalFlag4OnTimeon
	 * @throws BusinessException
	 */
	public void reCalculateRelationWaItem(WaLoginContext loginContext, CaculateTypeVO caculateTypeVO, String condition,
			SuperVO... superVOs) throws BusinessException;

	/**
	 * wa_data计算状态修改为全部未计算 wa_data审核状态改为全部未审核 wa_periodstate 修改为未计算,未审核
	 * 
	 * @author liangxr on 2010-7-7
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	void updatePaydataFlag(String pk_wa_class, String cyear, String cperiod) throws nc.vo.pub.BusinessException;

	/**
	 * （根据薪资方案 、业务期间） 清空某个项目在 wadata中的数据
	 */
	void clearClassItemData(WaClassItemVO vo) throws nc.vo.pub.BusinessException;

	/**
	 * 
	 * 删除显示设置
	 * 
	 * @param context
	 * @param type
	 *            显示设置类型：通用设置、个人设置
	 * @throws BusinessException
	 */
	public void deleteDisplayInfo(WaLoginContext context, String type) throws BusinessException;

	/**
	 * 重新计算合并计税
	 * 
	 * @param loginContext
	 *            WaLoginContext
	 * @throws BusinessException
	 */
	// shenliangc 20140830 合并计税方案部分审核只计算界面上查询出来的人员数据，需要传入过滤条件。
	public void reCaculate(WaLoginContext loginContext, String whereCondition) throws BusinessException;

	/**
	 * shenliangc 20140826 时点薪资计算保存之后清除主界面对应人员的发放数据计算标志
	 * 
	 * @param psndocWaVOs
	 * @throws BusinessException
	 */
	public void updateCalFlag4OnTime(PsndocWaVO[] psndocWaVOs) throws BusinessException;

	/**
	 * 異常時加密已解密的數據
	 * 
	 * @param loginContext
	 *            WaLoginContext
	 * @throws BusinessException
	 */
	public void doEncryptEx(WaLoginContext loginContext) throws BusinessException;

	/**
	 * 通過Email發送加密PDF薪資單
	 * 
	 * @param payFileVOs
	 *            薪資檔案VOs
	 * @param pk_itemgroup
	 *            薪資項目分組
	 * @param showZeroItems
	 *            是否顯示金額為0的項目
	 * @return
	 * @throws BusinessException
	 */
	public boolean sendPayslipByEmail(SuperVO[] payFileVOs, String pk_itemgroup, boolean showZeroItems)
			throws BusinessException;
}
