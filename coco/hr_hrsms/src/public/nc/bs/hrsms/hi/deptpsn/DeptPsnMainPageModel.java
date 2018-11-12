package nc.bs.hrsms.hi.deptpsn;

import nc.bs.hrss.pub.advpanel.AdvancePageModel;
import nc.bs.hrss.pub.advpanel.IPagePanel;
import nc.bs.hrss.pub.advpanel.mngdept.MngDeptPanel;
import nc.bs.hrss.pub.advpanel.panels.CanvasPanel;
import nc.bs.hrss.pub.advpanel.panels.SimpleQueryPanel;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;

public class DeptPsnMainPageModel extends AdvancePageModel {

	protected String getFunCode() {
		return "E20400101";
	}

	@Override
	protected void initPageMetaStruct() {
		super.initPageMetaStruct();
	}

	@Override
	protected String getQueryTempletKey() {
		return null;
	}

	@Override
	protected String getRightPage() {
		return null;
	}

	@Override
	protected IPagePanel[] getLeftComponents(LfwWindow pm, UIMeta um) {
		return new IPagePanel[]{new CanvasPanel(), new MngDeptPanel(), new SimpleQueryPanel()};
	}
}
