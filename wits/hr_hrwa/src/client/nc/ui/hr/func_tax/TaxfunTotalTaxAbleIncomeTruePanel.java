package nc.ui.hr.func_tax;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.ui.hr.func.IRefPanel;
import nc.ui.hr.itemsource.view.IParaPanel;
import nc.ui.pub.beans.UIAsteriskPanelWrapper;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.wa.ctymgt.CtymgtDelegator;
import nc.ui.wa.ref.WaItemRefModel;
import nc.vo.hr.formula.IFormulaConst;
import nc.vo.hr.func.FunctableItemVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.WaLoginContext;

/**
 * �Ľ���� �ۼ�Ӧ��˰���ö� 
 * 
 * @author harvey
 */
public class TaxfunTotalTaxAbleIncomeTruePanel extends UIPanel implements ItemListener,
		IParaPanel, IRefPanel, IFormulaConst {
	private static final long serialVersionUID = -5415459347134279156L;

	private UILabel ItemText = null;
	private UIComboBox ivjcmbItem = null;// н�ʷ�����Ŀ
	private WaClassItemVO[] itemdata = null;// н����Ŀ

	private UILabel ItemText2 = null;
	private UIComboBox ivjcmbItem2 = null;// н�ʷ�����Ŀ
	private WaClassItemVO[] itemdata2 = null;// н����Ŀ

	private int datatype = 1;// ������Ҫ���ص��������ͣ�Ĭ��������
	private UIComboBox cmdDaystype = null;
	public int parasLen = 0;

	private WaLoginContext context = null;

	public void setContext(WaLoginContext context) {
		this.context = context;
	}

	public WaLoginContext getContext() {
		return this.context;
	}

	public int getDatatype() {
		return this.datatype;
	}

	/**
	 * WaParaPanel ������ע�⡣
	 */
	public TaxfunTotalTaxAbleIncomeTruePanel() {
		super();
		initialize();
	}

	/**
	 * н����Ŀ
	 * 
	 * @return
	 */
	private UILabel getUILabel1() {
		if (ItemText == null) {
			ItemText = new UILabel();
			ItemText.setName("ItemText");
			ItemText.setText("���ο�˰����");
			ItemText.setBounds(10, 15, 80, 22);

		}
		return ItemText;
	}

	/**
	 * н����Ŀ
	 * 
	 * @return
	 */
	private UILabel getUILabel2() {
		if (ItemText2 == null) {
			ItemText2 = new UILabel();
			ItemText2.setName("ItemText2");
			ItemText2.setText("�ۼ�ר��Ӧ��");
			ItemText2.setBounds(10, 70, 80, 22);

		}
		return ItemText2;
	}

	private UIAsteriskPanelWrapper cmbItemUI = null;

	private UIAsteriskPanelWrapper getcmbItemUI() {

		if (cmbItemUI == null) {
			cmbItemUI = new UIAsteriskPanelWrapper(getcmbItem());
			cmbItemUI.setBounds(93, 15, 147, 22);
			cmbItemUI.setMustInputItem(true);
		}

		return cmbItemUI;

	}

	private nc.ui.pub.beans.UIComboBox getcmbItem() {
		if (ivjcmbItem == null) {
			ivjcmbItem = new nc.ui.pub.beans.UIComboBox();
			ivjcmbItem.setName("cmbItem");
			ivjcmbItem.setBounds(100, 6, 140, 22);

			ivjcmbItem.setTranslate(true);
		}
		return ivjcmbItem;
	}

	private UIAsteriskPanelWrapper cmbItemUI2 = null;

	private UIAsteriskPanelWrapper getcmbItemUI2() {

		if (cmbItemUI2 == null) {
			cmbItemUI2 = new UIAsteriskPanelWrapper(getcmbItem2());
			cmbItemUI2.setBounds(93, 70, 147, 22);
			cmbItemUI2.setMustInputItem(true);
		}

		return cmbItemUI2;

	}

	private nc.ui.pub.beans.UIComboBox getcmbItem2() {
		if (ivjcmbItem2 == null) {
			ivjcmbItem2 = new nc.ui.pub.beans.UIComboBox();
			ivjcmbItem2.setName("cmbItem2");
			ivjcmbItem2.setBounds(100, 70, 140, 22);

			ivjcmbItem2.setTranslate(true);
		}
		return ivjcmbItem2;
	}

	/**
	 * ���������Ϸ���У��
	 */
	public void checkPara(int dataType) throws Exception {
		try {
			// �жϷǿ�
			String nullstr = "";
			if (getcmbItem().isVisible()
					&& getcmbItem().getSelectedIndex() <= 0) {
				if (nullstr.length() > 0) {
					nullstr += "���ο�˰����,";
				}
				nullstr += "";
			}
			if (getcmbItem2().isVisible()
					&& getcmbItem2().getSelectedIndex() <= 0) {
				if (nullstr.length() > 0) {
					nullstr += "�ۼ�ר��Ӧ��,";
				}
				nullstr += "";
			}

		} catch (Exception e) {
			handleException(e);
			throw e;
		}
	}

	public void clearDis() {
	}

	/**
	 * ���غ����Ĳ�������
	 */
	public String[] getPara() throws Exception {
		String[] paras = new String[2];

		// ��Ŀ //�ж�н����Ŀ��������
		WaItemVO item = CtymgtDelegator.getWaItem().queryWaItemVOByPk(
				itemdata[getcmbItem().getSelectedIndex() - 1].getPk_wa_item());
		WaItemVO item2 = CtymgtDelegator.getWaItem().queryWaItemVOByPk(
				itemdata2[getcmbItem2().getSelectedIndex() - 1].getPk_wa_item());

		paras[0] = item.getItemkey().toString().trim();
		paras[1] = item2.getItemkey().toString().trim();
		return paras;

	}

	/**
	 * ÿ�������׳��쳣ʱ������
	 * 
	 * @param exception
	 *            java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		Logger.error(exception.getMessage(), exception);
	}

	public void initData() {

		getcmbItem().removeAllItems();
		getcmbItem().addItem(" ");
		getcmbItem2().removeAllItems();
		getcmbItem2().addItem(" ");
		try {
			itemdata = CtymgtDelegator.getClassItemQueryService()
					.queryAllClassItemsForFormular(context.getClassPK());
			itemdata2 = CtymgtDelegator.getClassItemQueryService()
					.queryAllClassItemsForFormular(context.getClassPK());
			if (itemdata != null && itemdata.length > 0)
				for (int i = 0; i < itemdata.length; i++) {
					getcmbItem().addItem(itemdata[i].getMultilangName());
					getcmbItem2().addItem(itemdata[i].getMultilangName());
				}

		} catch (Exception ex) {
			handleException(ex);
		}
		// Ĭ��ѡ�п���
		getcmbItem().setSelectedIndex(0);
		getcmbItem2().setSelectedIndex(0);
	}

	/**
	 * ��ʼ���ࡣ
	 */
	/* ���棺�˷������������ɡ� */
	private void initialize() {
		setName("WaParaPanel");
		setLayout(null);
		setSize(240, 200);
		add(getUILabel1(), getUILabel1().getName());
		add(getcmbItemUI(), getcmbItem().getName());
		add(getUILabel2(), getUILabel2().getName());
		add(getcmbItemUI2(), getcmbItem2().getName());
	}

	public void setDatatype(int newDatatype) {
		datatype = newDatatype;
	}

	/**
	 * ����ѡ��ĺ������²������ý���
	 * 
	 * @param paras
	 *            nc.vo.wa.func.FunctableItemVO[]
	 */
	public void updateDis(FunctableItemVO[] paras) {

		getcmbItem().removeAllItems();
		getcmbItem().addItem(" ");
		getcmbItem2().removeAllItems();
		getcmbItem2().addItem(" ");
		try {
			itemdata = CtymgtDelegator.getClassItemQueryService()
					.queryAllClassItemsForFormular(context.getClassPK());
			itemdata2 = CtymgtDelegator.getClassItemQueryService()
					.queryAllClassItemsForFormular(context.getClassPK());
			if (itemdata != null && itemdata.length > 0)
				for (int i = 0; i < itemdata.length; i++){
					getcmbItem().addItem(itemdata[i].getMultilangName());
					getcmbItem2().addItem(itemdata[i].getMultilangName());
				}
					
		} catch (Exception ex) {
			handleException(ex);
		}
		// Ĭ��ѡ�п���
		getcmbItem().setSelectedIndex(0);
		getcmbItem2().setSelectedIndex(0);
	}

	public void setParasLen(int newParasLen) {
		parasLen = newParasLen;
	}

	public void updateDis(int index) {
		initData();
	}

	public void updateDis(String funcname) {

	}

	String currentItemKey = null;

	@Override
	public void setCurrentItemKey(String itemKey) {
		currentItemKey = itemKey;

	}

	@Override
	public void itemStateChanged(ItemEvent e) {

	}

}