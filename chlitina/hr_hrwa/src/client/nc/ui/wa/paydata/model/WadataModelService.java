package nc.ui.wa.paydata.model;

import nc.bs.framework.common.NCLocator;
import nc.itf.hr.wa.IPaydataManageService;
import nc.itf.hr.wa.IPaydataQueryService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.hr.caculate.CaculateTypeVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.paydata.AggPayDataVO;
import nc.vo.wa.paydata.DataSVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.paydata.WaClassItemShowInfVO;
import nc.vo.wa.paydata.WaPaydataDspVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;

/**
 * 薪资发放数据服务类
 * 
 * @author: zhangg
 * @date: 2009-11-23 下午01:21:09
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WadataModelService implements IAppModelService, IPaginationQueryService{

	private IPaydataManageService manageService;

	private IPaydataQueryService queryService;

	protected IPaydataManageService getManageService() {
		if (manageService == null) {
			manageService = NCLocator.getInstance().lookup(IPaydataManageService.class);
		}
		return manageService;
	}

	protected IPaydataQueryService getQueryService() {
		if (queryService == null) {
			queryService = NCLocator.getInstance().lookup(IPaydataQueryService.class);
		}
		return queryService;
	}

	public Object update(Object object, WaLoginVO waLoginVO) throws BusinessException {
		getManageService().update(object, waLoginVO);
		return object;
	}

	@Override
	public Object insert(Object object) throws BusinessException {
		return null;
	}

	public void onCheck(WaLoginVO waLoginVO, String whereCondition, Boolean isRangeAll)
			throws nc.vo.pub.BusinessException {
		getManageService().onCheck(waLoginVO, whereCondition, isRangeAll);
	}

	public void onUnCheck(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll)
			throws nc.vo.pub.BusinessException {
		getManageService().onUnCheck(waLoginVO, whereCondition, isRangeAll);
	}

	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context) throws BusinessException {
		return null;
	}

	public WaClassItemVO[] getUserShowClassItemVOs(WaLoginContext loginContext) throws BusinessException {
		if (loginContext.isContextNotNull()) {
			return getQueryService().getUserShowClassItemVOs(loginContext);
		}
		return null;
	}
	
	
	public WaClassItemShowInfVO getWaClassItemShowInfVO(WaLoginContext loginContext) throws BusinessException {
		if (loginContext.isContextNotNull()) {
			return getQueryService().getWaClassItemShowInfVO(loginContext);
		}
		WaClassItemShowInfVO info =new WaClassItemShowInfVO();
		info.setWaClassItemVO(new WaClassItemVO[0]);
		info.setWaPaydataDspVO(new WaPaydataDspVO[0]);
		return info;
	}

	public AggPayDataVO queryAggPayDataVOByCondition(WaLoginContext loginContext, String condition,
			String orderCondtion) throws BusinessException {
		return getQueryService().queryAggPayDataVOByCondition(loginContext, condition, orderCondtion);
	}

	public int updateClassItemVOsDisplayFlg(WaClassItemVO[] classItemVOs) throws BusinessException {
		return getManageService().updateClassItemVOsDisplayFlg(classItemVOs);
	}

	public void onPay(WaLoginContext loginContext) throws BusinessException {
		getManageService().onPay(loginContext);
	}

	public void onUnPay(WaLoginVO waLoginVO) throws BusinessException {
		getManageService().onUnPay(waLoginVO);
	}

	public void onCaculate(WaLoginContext loginContext, CaculateTypeVO caculateTypeVO, String condition,SuperVO... superVOs)
			throws BusinessException {
		getManageService().onCaculate(loginContext, caculateTypeVO, condition,superVOs);
	}

	public void onRepace(WaLoginVO waLoginVO, String whereCondition, WaClassItemVO replaceItem, String formula,SuperVO... superVOs)
			throws BusinessException {
		getManageService().onReplace(waLoginVO, whereCondition, replaceItem, formula,superVOs);
	}

	public void onSaveDataSVOs(WaLoginVO waLoginVO, DataSVO[] dataSVOs) throws BusinessException {
		getManageService().onSaveDataSVOs(waLoginVO, dataSVOs);
	}



	public DataVO getDataVOByPk(String pk_wa_data) throws BusinessException {
		return getQueryService().getDataVOByPk(pk_wa_data);
	}

	public DataSVO[] getDataSVOs(WaLoginContext loginContext) throws BusinessException {
		return getQueryService().getDataSVOs(loginContext);
	}

	@Override
	public void delete(Object object) throws Exception {

	}

	@Override
	public Object update(Object object) throws Exception {
		return null;
	}

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		return getQueryService().queryDataVOByPks(pks);
	}
	/**
	 * 回写到扣款明细子集 by he
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	public void backdeductdetails(WaLoginVO waLoginVO, String[] pk_psndocs) throws nc.vo.pub.BusinessException {
		getManageService().backdeductdetails(waLoginVO, pk_psndocs);
		
	}

}
