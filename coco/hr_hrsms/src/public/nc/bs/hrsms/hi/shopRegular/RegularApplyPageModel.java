package nc.bs.hrsms.hi.shopRegular;

import java.util.HashMap;
import java.util.Map;

import nc.bs.hrss.pub.pf.ctrl.TransTypeRefCtrl;
import nc.bs.hrss.trn.PsnApplyConsts;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.refnode.IRefNode;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.bs.hrsms.hi.HiApplyBasePageModel;
import nc.bs.hrsms.hi.employ.lsnr.ShopPsnDocController;
import nc.bs.hrsms.hi.employ.lsnr.ShopPsnDocRegController;

public class RegularApplyPageModel extends HiApplyBasePageModel{
	
	@Override
	protected String getFunCode() {
		return PsnApplyConsts.REGULAR_FUNC_CODE;
	}

	@Override
	protected String getBillType() {
		return PsnApplyConsts.REGULAR_BILLTYPE_CODE;
	}

	@Override
	protected String getBillInfoForm() {
		return PsnApplyConsts.REGULAR_FORM_BILLINFO;
	}
	
	/**
	 * 特殊待设置参照集合
	 */
	@Override
	protected Map<String, String> getSpecialRefnodeMap() {
		Map<String, String> specialRefMap = new HashMap<String, String>();
		String transTypeRefId = "refnode_hi_regapply_transtypeid_billtypename";
		specialRefMap.put(transTypeRefId, TransTypeRefCtrl.class.getName());
		return specialRefMap;
	}
	
	@Override
	protected void initPageMetaStruct() {
		
		super.initPageMetaStruct();
		//得到卡片界面的view，对流程类型参照做控制
		LfwView widget = this.getPageMeta().getView("main");
		
		IRefNode region = widget.getViewModels().getRefNode("refnode_hi_regapply_pk_psndoc_name");
		NCRefNode region1 = (NCRefNode) region;
		region1.setDataListener(ShopPsnDocRegController.class.getName());
		
		IRefNode region2 = widget.getViewModels().getRefNode("refnode_hi_stapply_pk_psndoc_name");
		NCRefNode region3 = (NCRefNode) region2;
		region3.setDataListener(ShopPsnDocController.class.getName());
	}

}
