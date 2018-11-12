package nc.bs.hrsms.ta.sss.lateearly.pagemodel;

import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrsms.ta.sss.lateearly.ShopLateEarlyConsts;
import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;

public class ShopLateEarlyPageModel extends AdvancePageModel{

	/**
	 * ���Ի�����
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
	}

	/**
	 * ���Panel
	 */
	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		//new MngDeptPanel()
		return new IPagePanel[] { new CanvasPanel(), new MngShopPanel(), new SimpleQueryPanel() };
	}

	@Override
	protected String getFunCode() {
		return ShopLateEarlyConsts.FUNC_CODE;
	}

	@Override
	protected String getQueryTempletKey() {
		return null;
	}

	@Override
	protected String getRightPage() {
		return null;
	}
}
