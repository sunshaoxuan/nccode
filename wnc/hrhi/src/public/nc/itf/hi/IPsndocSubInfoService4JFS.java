package nc.itf.hi;

import java.util.Map;
import java.util.Set;

import nc.itf.hr.hi.InsuranceTypeEnum;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.trn.transmng.AggStapply;

public interface IPsndocSubInfoService4JFS {
	/**
	 * 同步勞健保投保薪資和級距
	 * 
	 * @param pk_org
	 *            組織
	 * @param cyear
	 *            年
	 * @param cperiod
	 *            期間
	 * @throws BusinessException
	 */
	public void renewRange(String pk_org, String cyear, String cperiod) throws BusinessException;

	/**
	 * 按指定期間平均薪資同步勞健保投保薪資和級距
	 * 
	 * @param pk_org
	 *            組織
	 * @param pk_psndocs
	 *            人員列表
	 * @param pk_wa_class
	 *            基準薪資方案
	 * @param startperiod
	 *            起始期間
	 * @param endperiod
	 *            截止期間
	 * @param effectivedate
	 *            生效日期
	 * @throws BusinessException
	 */
	/*
	 * public void renewRangeEx(String pk_org, String[] pk_psndocs, String
	 * pk_wa_class, String startperiod, String endperiod, UFDate effectivedate)
	 * throws BusinessException;
	 */

	/**
	 * 按指定期間平均薪資同步勞健保投保薪資和級距
	 * 
	 * @param pk_org
	 *            組織
	 * @param pk_psndocs
	 *            人員列表
	 * @param cbasedate
	 *            基准日期
	 * @param avgmoncount
	 *            平均月数
	 * @param effectivedate
	 *            生效日期
	 * @throws BusinessException
	 */
	public void renewRangeEx(String pk_org, String[] pk_psndocs, String[] pk_wa_class, UFDate cbaseDate,
			String avgmoncount, UFDate effectivedate) throws BusinessException;

	/**
	 * 按指定期間平均薪資同步團保保薪基數
	 * 
	 * @param pk_org
	 *            組織
	 * @param pk_psndocs
	 *            人員列表
	 * @param pk_wa_class
	 *            基準薪資方案
	 * @param startperiod
	 *            起始期間
	 * @param endperiod
	 *            截止期間
	 * @param effectivedate
	 *            生效日期
	 * @throws BusinessException
	 */
	/*
	 * public void renewGroupIns(String pk_org, String[] pk_psndocs, String
	 * pk_wa_class, String startperiod, String endperiod, UFDate effectivedate)
	 * throws BusinessException;
	 */
	/**
	 * 按指定期間平均薪資同步團保保薪基數
	 * 
	 * @param pk_org
	 *            組織
	 * @param pk_psndocs
	 *            人員列表
	 * @param cbasedate
	 *            基准日期
	 * @param avgmoncount
	 *            平均月数
	 * @param effectivedate
	 *            生效日期
	 * @throws BusinessException
	 */
	public void renewGroupIns(String pk_org, String[] pk_psndocs, UFDate effectivedate) throws BusinessException;

	/**
	 * 離職終止勞健保設定
	 * 
	 * @param pk_org
	 *            組織
	 * @param pk_psndoc
	 *            人員pk
	 * @param enddate
	 *            結束日期
	 * @throws BusinessException
	 */
	public void dismissPsnNHI(String pk_org, String pk_psndoc, UFLiteralDate enddate) throws BusinessException;

	/**
	 * 離職終止團保設定
	 * 
	 * @param pk_org
	 *            組織
	 * @param pk_psndoc
	 *            人員pk
	 * @param enddate
	 *            結束日期
	 * @throws BusinessException
	 */
	public void dismissPsnGroupIns(String pk_org, String pk_psndoc, UFLiteralDate enddate) throws BusinessException;

	/**
	 * 計算員工團保費用
	 * 
	 * @param pk_org
	 *            組織
	 * @param pk_wa_class
	 *            薪資方案
	 * @param cYear
	 *            期间年
	 * @param cPeriod
	 *            期间号
	 * @param pk_psndocs
	 *            指定人員PK數組
	 * @param onlyCalculate
	 *            只計算不回寫
	 * @throws BusinessException
	 */
	public void calculateGroupIns(String pk_org, String pk_wa_class, String cYear, String cPeriod, String[] pk_psndocs,
			boolean onlyCalculate) throws BusinessException;

	/**
	 * 重新計算員工團保（按保險公司統計）
	 * 
	 * @param pk_org
	 *            組織PK
	 * @param pk_wa_class
	 *            薪資方案PK
	 * @param cYear
	 *            期間年
	 * @param cPeriod
	 *            期間號
	 * @return
	 */
	public Map<String, Map<String, UFDouble>> recalculateGroupIns(String pk_org, String pk_wa_class, String cYear,
			String cPeriod) throws BusinessException;

	/**
	 * 重新計算員工團保（按保險公司統計）
	 * 
	 * @param pk_org
	 *            組織PK
	 * @param pk_wa_class
	 *            薪資方案PK
	 * @param cYear
	 *            期間年
	 * @param cPeriod
	 *            期間號
	 * @return
	 */

	public DataSet dsRecalculateGroupIns(SmartContext context) throws BusinessException;

	/**
	 * 指定员工设定是否存在团保计算结果
	 * 
	 * @param pk_psndoc
	 *            员工ID
	 * @param pk_psndoc_sub
	 * @return
	 * @throws BusinessException
	 */
	public boolean isExistsGroupInsCalculateResults(String pk_psndoc, String pk_psndoc_sub) throws BusinessException;

	/**
	 * 生成勞健保設定
	 * 
	 * @param pk_org
	 *            組織
	 * @param pk_psndocs
	 *            人員列表
	 * @throws BusinessException
	 */
	public void generatePsnNHI(String pk_org, String[] pk_psndocs, UFLiteralDate specificStartDate, boolean isGenLabor,
			boolean isGenRetire, boolean isGenHeal) throws BusinessException;

	/**
	 * 生成勞健保設定
	 * 
	 * @param pk_org
	 *            組織
	 * @param <pk_psndoc,Set<加保类型>> psndocsAndInsuranceTypeMap 人員列表
	 * @param NHIInsMap
	 *            加保信息 map key 参照:nc.itf.hr.hi.BatchInsuranceFieldDeclaration
	 * @throws BusinessException
	 */
	public void generatePsnNHI(String pk_org, Map<String, Set<InsuranceTypeEnum>> psndocsAndInsuranceTypeMap,
			Map<String, String> NHIInsMap) throws BusinessException;

	/**
	 * 生成團保設定
	 * 
	 * @param pk_org
	 *            組織
	 * @param pk_psndocs
	 *            人員列表
	 * @param specificStartDate
	 *            指定開始日期
	 * @throws BusinessException
	 */
	public void generateGroupIns(String pk_org, String[] pk_psndocs, UFLiteralDate specificStartDate)
			throws BusinessException;

	/**
	 * 生成團保設定
	 * 
	 * @param pk_org
	 *            組織
	 * @param pk_psndocs
	 *            人員列表
	 * @param groupInsMap
	 *            加保信息 map key 参照:nc.itf.hr.hi.BatchInsuranceFieldDeclaration
	 * @throws BusinessException
	 */
	public void generateGroupIns(String pk_org, String[] pk_psndocs, Map<String, String> groupInsMap)
			throws BusinessException;

	/**
	 * 根据人员日期取团保保薪基数
	 * 
	 * @param pk_org
	 *            组织
	 * @param pk_psndoc
	 *            人员
	 * @param salaryDate
	 *            薪资日期
	 * @return
	 * @throws BusinessException
	 */
	public UFDouble getGroupInsWadocBaseSalaryByPsnDate(String pk_org, String pk_psndoc, UFLiteralDate salaryDate)
			throws BusinessException;

	/**
	 * 员工轉調时劳健保处理
	 * 
	 * @param ufLiteralDate
	 *            退保日期
	 * @param psnJobVO
	 *            新工作
	 * @param is2LegalOrg
	 *            是否加保(同步投保记录到新组织)
	 * @author Ares.Tank 2018-9-16 11:20:53
	 * @throws BusinessException
	 */
	public void redeployPsnNHI(UFLiteralDate endDate, PsnJobVO psnJobVO, boolean is2LegalOrg) throws BusinessException;

	/**
	 * 员工留停複職时劳健保处理
	 * 
	 * @param ufLiteralDate
	 *            加保日期
	 * @param psnJobVO
	 *            新工作
	 * @author Ares.Tank 2018-9-16 11:20:53
	 * @throws BusinessException
	 */
	public void returnPsnNHI(UFLiteralDate startDate, PsnJobVO psnJobVO) throws BusinessException;

	/**
	 * 员工留職停薪时劳健保处理
	 * 
	 * @param ufLiteralDate
	 *            退保日期
	 * @param psnJobVO
	 *            新工作
	 * @author Ares.Tank 2018-9-16 11:20:53
	 * @throws BusinessException
	 */
	public void transPsnNHI(UFLiteralDate endDate, PsnJobVO psnJobVO) throws BusinessException;

	/**
	 * 劳健保批量退保
	 * 
	 * @param pk_org
	 * @param @param <pk_psndoc,Set<加保类型>> psndocsAndInsuranceTypeMap
	 * @param outMap
	 *            退保信息 map key 参照:nc.itf.hr.hi.BatchInsuranceFieldDeclaration
	 * @throws BusinessException
	 */
	public void delPsnNHI(String pk_org, Map<String, Set<InsuranceTypeEnum>> psndocsAndInsuranceTypeMap,
			Map<String, String> outMap) throws BusinessException;

	/**
	 * 团报批量退保
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param outMap
	 *            退保信息 mapKey map key
	 *            参照:nc.itf.hr.hi.BatchInsuranceFieldDeclaration
	 * @throws BusinessException
	 */
	public void delGroupIns(String pk_org, String[] pk_psndocs, Map<String, String> outMap) throws BusinessException;

	/**
	 * 停用團保時進行退保,
	 * 
	 * @param enddate
	 *            退保日期
	 * @param pk_defdoc
	 *            團保險种,自定義檔案主鍵
	 */
	public void delGroupInsByType(UFDate enddate, String pk_defdoc) throws BusinessException;

	/**
	 * 生成團保設定和新增险种
	 * 
	 * @param pk_org
	 *            組織
	 * @param pk_psndocs
	 *            人員列表
	 * @param specificStartDate
	 *            指定開始日期
	 * @param pk_group_ins
	 *            新增险种pk
	 * @param addDate
	 *            新增险种加保日期
	 * @throws BusinessException
	 */
	public void generateGroupInsWithNewIns(String pk_org, String[] pk_psndocs, UFLiteralDate specificStartDate,
			String pk_group_ins, UFDate addDate) throws BusinessException;

	/**
	 * 留停复职时的加保(相对于入职加保)
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param specificStartDate
	 *            留停复职日期
	 * @throws BusinessException
	 */
	void generateGroupIns4Return(String pk_org, String[] pk_psndocs, UFLiteralDate specificStartDate)
			throws BusinessException;

	/**
	 * 离职审核后回写劳健保子集结束日期
	 */
	void finishInsurance(AggStapply[] aggvos) throws BusinessException;

	/**
	 * 删除 健保信息，劳保劳退信息，团保信息开始日期大于离职日期(或停薪日期)信息
	 * 
	 * @param pk_psndoc
	 * @param enddate
	 * 
	 * @throws BusinessException
	 */
	void deletePNI(String pk_psndoc, UFLiteralDate enddate) throws BusinessException;

}
