package nc.itf.hr.wa;

import java.util.List;
import java.util.Map;

import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.wadoc.BatchGroupInsuranceExitVO;
import nc.vo.pub.BusinessException;

public interface IPsndocwadocLabourService {

	// MOD 张恒 人员部门信息 2018/9/15 mod Ares.Tank 部门多选,组织可查询人力资源的下的所有法人组织
	public PsnJobVO[] queryPsnJobVOsByConditionAndOverrideOrg(List<String> pk_psndocs, String pk_org, String[] pk_dept)
			throws BusinessException;

	// MOD 张恒 查询劳保退保健保 2018/9/15
	public Map<String, String[]> queryLabour(List<String> pk_psndocs, String laoJiJu, String tuiJiJu, String jianJiJu)
			throws BusinessException;

	// MOD 张恒 根据code找到自定义档案参照的name值 2018/9/17
	public String queryRefNameByCode(String code) throws BusinessException;

	/**
	 * 團保批量退保
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public BatchGroupInsuranceExitVO[] batchGroupInsuranceExit(List<BatchGroupInsuranceExitVO> dataVO)
			throws BusinessException;

}
