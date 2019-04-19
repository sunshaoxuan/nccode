package nc.ui.wa.payfile.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.IActionCode;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.query.tools.ImageIconAccessor;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.wa.payfile.model.PayfileAppModel;
import nc.ui.wa.payfile.refmodel.PersonType;
import nc.ui.wa.payfile.view.PayfileFormEditor;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.payfile.PayfileVO;

/**
 * ������ʽ��ԱAction
 *
 * @author: zhoucx
 * @date: 2009-11-27 ����09:12:39
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class AddPayfileAction extends PayfileBaseAction {

	private static final long serialVersionUID = 3560637576039585517L;
	
	private static String DF9A="1001ZZ1000000001NEGQ";//ҵ�����
	private static String DF9B="1001ZZ1000000001NEGR";//���ñ����
	private static String DF92="1001ZZ1000000001NEGT";//��Ŀ����

	public AddPayfileAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.ADD);
		putValue(AbstractAction.SMALL_ICON, ImageIconAccessor.getIcon("toolbar/icon2/addline.gif"));
		setBtnName(ResHelper.getString("common","UC001-0000108")/*@res "����"*/);
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("common","UC001-0000108")/*@res "����"*/+"(Ctrl+N)");
	}
	

	/*
	 * (non-Javadoc)
	 *
	 * @see nc.ui.uif2.actions.AddAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {
		((PayfileAppModel) getModel()).setPType(PersonType.NORMAL);
		getModel().setUiState(UIState.ADD);
		IUAPQueryBS query = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		//WaClassVO vo = (WaClassVO)query.retrieveByPK("", arg1)
		PayfileVO pf = (PayfileVO)getModel().getSelectedData();
		String wa_class = pf.getPk_wa_class();//getModel().getSelectedData();
		WaClassVO vo = (WaClassVO) query.retrieveByPK(WaClassVO.class, wa_class);
		BillCardPanel panel = ((PayfileFormEditor)getEditor()).getBillCardPanel();
		String declaretype = vo==null?"":vo.getDeclareform();
		if(DF9A.equals(declaretype)){
			panel.getHeadItem("biztype").setEnabled(true);//ҵ�����
			panel.getHeadItem("feetype").setEnabled(false);//���ñ����
			panel.getHeadItem("projectcode").setEnabled(false);//��Ŀ����
		}else if(DF9B.equals(declaretype)){
			panel.getHeadItem("biztype").setEnabled(false);//ҵ�����
			panel.getHeadItem("feetype").setEnabled(true);//���ñ����
			panel.getHeadItem("projectcode").setEnabled(false);//��Ŀ����
		}else if(DF92.equals(declaretype)){
			panel.getHeadItem("biztype").setEnabled(false);//ҵ�����
			panel.getHeadItem("feetype").setEnabled(false);//���ñ����
			panel.getHeadItem("projectcode").setEnabled(true);//��Ŀ����
		}else{
			panel.getHeadItem("biztype").setEnabled(false);//ҵ�����
			panel.getHeadItem("feetype").setEnabled(false);//���ñ����
			panel.getHeadItem("projectcode").setEnabled(false);//��Ŀ����
		}
	}

	@Override
	protected boolean isActionEnable() {
		boolean enabled = UIState.NOT_EDIT == getModel().getUiState() ;
		if(getWaLoginVO().getBatch()!=null&&getWaLoginVO().getBatch()>100){
			return false;
		}
		return enabled && super.isActionEnable();
	}


}