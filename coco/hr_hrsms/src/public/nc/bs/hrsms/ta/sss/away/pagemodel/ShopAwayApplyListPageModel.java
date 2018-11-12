package nc.bs.hrsms.ta.sss.away.pagemodel;

import nc.bs.hrsms.pub.advpanel.mngdept.MngShopPanel;
import nc.bs.hrsms.ta.sss.common.ShopTaListBasePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.vo.ta.away.AwaybVO;
import nc.vo.ta.away.AwayhVO;

public class ShopAwayApplyListPageModel extends ShopTaListBasePageModel{

	@Override
	protected String getFunCode() {
		return "E20600925";
	}
	
	/**
	 * ��ʼ������
	 */
	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
	}
	
	/**
	 * ���ÿ������ݵ�Сʱλ��<br/>
	 * String[]�����õĿ��������ֶ�����<br/>
	 * 
	 * @return
	 */
	protected String[] getTimeDataFields() {
		return new String[] { AwayhVO.SUMHOUR, AwaybVO.AWAYHOUR };
	}
	
	@Override
	protected String getQueryTempletKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		// TODO Auto-generated method stub
		return new IPagePanel[] { new CanvasPanel(), new MngShopPanel(), new SimpleQueryPanel() };
	}

	@Override
	protected String getRightPage() {
		// TODO Auto-generated method stub
		return null;
	}

}