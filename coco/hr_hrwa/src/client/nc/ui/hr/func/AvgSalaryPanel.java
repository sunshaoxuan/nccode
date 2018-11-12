package nc.ui.hr.func;

import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IWaClass;
import nc.ui.hr.global.Global;
import nc.ui.hr.itemsource.view.IParaPanel;
import nc.ui.pub.beans.UIAsteriskPanelWrapper;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.wa.ctymgt.CtymgtDelegator;
import nc.vo.hr.formula.IFormulaConst;
import nc.vo.hr.func.FunctableItemVO;
import nc.vo.hrwa.wadaysalary.AvageMonthEnum;
import nc.vo.pub.BusinessException;
import nc.vo.ta.overtime.CalendarDateTypeEnum;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.WaLoginContext;

/**
 *
 * @author: he
 * @date: 2018-08-29 ����09:22:28
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class AvgSalaryPanel extends UIPanel implements ItemListener, IParaPanel, IRefPanel, IFormulaConst {


	private static final long serialVersionUID = -5415459347134279156L;

	private UIComboBox cbasedate = null;

	private UIComboBox avgmoncount = null;

	private UILabel basedateUILabel = null;

	private UILabel avgmonthUILabel = null;

	/*private WaClassVO[] classdata = null; //н�ʷ���

	private WaClassItemVO[] itemdata = null; //н����Ŀ
*/
	private int datatype = 0; //������Ҫ���ص��������ͣ�Ĭ��������

	//ƽ����������
	private UIRefPane refAvgMonCount = null;
	


	public int parasLen = 0;

	/* ������� */
	private int funcIndex = 0;

	private WaLoginContext context = null;
	//	private HrFormula formular = null;
	private WaClassUtil waClassUtil  =  WaClassUtil.getInstance();

	public void setContext(WaLoginContext context) {
		this.context = context;
	}


	public WaLoginContext getContext() {
		return this.context;
	}


	/**
	 * @author 
	 * @return the datatype
	 */
	public int getDatatype() {
		return this.datatype;
	}
	/**
	 * WaParaPanel ������ע�⡣
	 */
	public AvgSalaryPanel() {
		super();
		initialize();
	}

	/**
	 *  ���������Ϸ���У��
	 */
	public void checkPara(int dataType) throws Exception {
		try {
			//�жϷǿ�
			if (getcmbClass().isVisible() && getcmbClass().getSelectedIndex() <= 0){
				throw new Exception(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0280")/*@res "��׼���ڲ���Ϊ�գ�"*/);
			}
		
			
			String nullstr = "";
			if (getcmbClass().isVisible() && getcmbClass().getSelectedIndex() <= 0) {
				nullstr += ResHelper.getString("6013salaryctymgt","06013salaryctymgt0281")/*@res "ƽ����������"*/;
			}
			
			if (nullstr.length() > 0) {
				throw new Exception(nullstr + ResHelper.getString("6013commonbasic","06013commonbasic0021")/*@res "����Ϊ�գ�"*/);
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		
			
			
	}

	

	public void clearDis() {
	}



	

	/**
	 * �õ�����������н����Ŀ
	 * @author xuanlt on 2010-2-24
	 * @param waclassPK
	 * @return
	 * @return  String[]
	 */
	private WaClassItemVO[] getWaCalssitems(String waclassPK){
		try {
			waClassUtil.setWaClassPK(waclassPK);
			return waClassUtil.getWaClassItemVOs();
		} catch (Exception e) {
			Logger.error(e.getMessage(),e);
			return new WaClassItemVO[0];
		}

	}

	/**
	 * �õ������ָ���ڼ��µ�н����Ŀ
	 * @author xuanlt on 2010-2-24
	 * @param waclassPK
	 * @return
	 * @return  String[]
	 */
	private WaClassItemVO[] getWaCalssitems(String waclassPK,String cyear,String period){
		WaClassItemVO[] vos = getWaCalssitems(waclassPK);
		List<WaClassItemVO> temp = new ArrayList<WaClassItemVO>();
		for (int index = 0; index < vos.length; index++) {
			if(vos[index].getCyear().equals(cyear)&& vos[index].getCperiod().equals(period)){
				temp.add(vos[index]);
			}
		}
		return temp.toArray(new WaClassItemVO[temp.size()]);

	}
	/**
	 * ���� cmbClass ����ֵ��
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UIComboBox getcmbClass() {
		if (cbasedate == null) {
			try {
				cbasedate = new nc.ui.pub.beans.UIComboBox();
				cbasedate.setName("cmbClass");
				cbasedate.setBounds(100, 0, 140, 22);
				cbasedate.setVisible(true);
				// user code begin {1}
				cbasedate.setTranslate(true);
				// user code end
				String[] ml = new String[5];
				ml[0] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0286")/*@res "н�ʿ�ʼ����"*/,"UPPnc_hr_wa_pub1-000016");/* -=notranslate=- */
				ml[1] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0287")/*@res "н�ʽ�������"*/,"UPPnc_hr_wa_pub1-000017");/* -=notranslate=- */
				ml[2] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0288")/*@res "��ǲ����"*/,"UPPnc_hr_wa_pub1-000018");/* -=notranslate=- */
				ml[3] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0289")/*@res "��������"*/,"UPPnc_hr_wa_pub1-000019");/* -=notranslate=- */
				ml[4] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0290")/*@res "��������"*/,"UPPnc_hr_wa_pub1-000020");/* -=notranslate=- */

				String[] mlDefault = new String[]{ResHelper.getString("6013salaryctymgt","06013salaryctymgt0286")/*@res "н�ʿ�ʼ����"*/,ResHelper.getString("6013salaryctymgt","06013salaryctymgt0287")/*@res "н�ʽ������� "*/,ResHelper.getString("6013salaryctymgt","06013salaryctymgt0288")/*@res "��ǲ����"*/,ResHelper.getString("6013salaryctymgt","06013salaryctymgt0289")/*@res "��������"*/,ResHelper.getString("6013salaryctymgt","06013salaryctymgt0290")/*@res "��������"*/};/* -=notranslate=- */
				nc.hr.utils.PairFactory mPairFactory=new nc.hr.utils.PairFactory(ml, mlDefault );

				cbasedate.addItems(mPairFactory.getAllConstEnums());
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return cbasedate;
	}

	/**
	 * ���� cmbItem ����ֵ��
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UIComboBox getcmbItem() {
		if (avgmoncount == null) {
			try {
				avgmoncount = new nc.ui.pub.beans.UIComboBox();
				avgmoncount.setName("cmbItem");
				avgmoncount.setBounds(100, 70, 140, 22);
				// user code begin {1}
				avgmoncount.setTranslate(true);
				// user code end
				/*String[] ml = new String[12];
				ml[0] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0282")@res "3�·�","UPPnc_hr_wa_pub1-000016"); -=notranslate=- 
				ml[1] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0283")@res "6�·�","UPPnc_hr_wa_pub1-000017"); -=notranslate=- 
				ml[2] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0284")@res "9�·�","UPPnc_hr_wa_pub1-000018"); -=notranslate=- 
				ml[3] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0285")@res "12�·�","UPPnc_hr_wa_pub1-000019"); -=notranslate=- 
				ml[4] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0291")@res "1�·�","UPPnc_hr_wa_pub1-000020"); -=notranslate=- 
				ml[5] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0292")@res "2�·�","UPPnc_hr_wa_pub1-000021"); -=notranslate=- 
				ml[6] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0293")@res "4�·�","UPPnc_hr_wa_pub1-000022"); -=notranslate=- 
				ml[7] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0294")@res "5�·�","UPPnc_hr_wa_pub1-000023"); -=notranslate=- 
				ml[8] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0295")@res "7�·�","UPPnc_hr_wa_pub1-000024"); -=notranslate=- 
				ml[9] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0296")@res "8�·�","UPPnc_hr_wa_pub1-000025"); -=notranslate=- 
				ml[10] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0297")@res "10�·�","UPPnc_hr_wa_pub1-000026"); -=notranslate=- 
				ml[11] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0298")@res "11�·�","UPPnc_hr_wa_pub1-000027"); -=notranslate=- 

				String[] mlDefault = new String[]{ResHelper.getString("6013salaryctymgt","06013salaryctymgt0291")@res "1��",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0292")@res "2��",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0282")@res "3��",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0293")@res "4�·�",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0294")@res "5�·�",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0283")@res "6�·�",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0295")@res "7�·�",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0296")@res "8�·�",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0284")@res "9�·�",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0297")@res "10�·�",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0298")@res "11�·�",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0285"),@res "12�·�"}; -=notranslate=- 
				nc.hr.utils.PairFactory mPairFactory=new nc.hr.utils.PairFactory(ml, mlDefault );
*/					
				avgmoncount.addItems(nc.md.model.impl.MDEnum.valueOfConstEnum(AvageMonthEnum.class));
				//avgmoncount.addItems(mPairFactory.getAllConstEnums());
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return avgmoncount;
	}




	/**
	 * ���غ����Ĳ�������
	 */
	public String[] getPara() throws Exception {
		if( getcmbClass().isVisible()==false  && getcmbItem().isVisible()==false){
			return null;
		}
		return getParaAvgSalary();

	}


	private WaClassItemVO getSelectedItem() {

		return (WaClassItemVO) getcmbItem().getSelectedItem();

	}

	/**
	 * /**
	 *  * ���غ����Ĳ�������
	 *  * @param		����˵��
	 *  * @return		����ֵ
	 *  * @exception �쳣����
	 *  * @see		��Ҫ�μ�����������
	 *  * @since		�������һ���汾���˷��������ӽ���������ѡ��
	 *  * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ��
	 * *-/
	 *
	 * @return java.lang.String
	 */
	public String[] getParaAvgSalary() throws Exception {
		String[] paras = new String[2];

		int baseindex = getcmbClass().getSelectedIndex();
		if (baseindex == 0) {
			paras[0] = "salarybegindate";
		} else
			if (baseindex == 1) {
				paras[0] = "salaryenddate";
			} else
				if (baseindex == 2) {
					paras[0] = "sepaydate";
				} else 
					if(baseindex == 3){
					paras[0] = "retirementdate";
				} else {
					paras[0] = "specialdate";
				}
					
		int avgindex = getcmbItem().getSelectedIndex();
		for(int x=0; x<12; x++){
			if(avgindex == x){
				paras[1] = ""+(x+1)+"";
			}
		}
		/*if(avgindex == 0){
			paras[1] = "3";
		}else if (avgindex == 1){
			paras[1] = "6";
		}else if (avgindex == 2){
			paras[1] = "9";
		}else if(avgindex == 3){
			paras[1] = "12";
		}else if(avgindex == 4){
			paras[1] = "1";
		}else if(avgindex == 5){
			paras[1] = "2";
		}else if(avgindex == 6){
			paras[1] = "4";
		}else if(avgindex == 7){
			paras[1] = "5";
		}else if(avgindex == 8){
			paras[1] = "7";
		}else if(avgindex == 9){
			paras[1] = "8";
		}else if(avgindex == 10){
			paras[1] = "10";
		}else{
			paras[1] = "11";
		}*/
		

		return paras;
	}


	/**
	 * ���� UILabel1 ����ֵ��
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UILabel getUILabel1() {
		if (basedateUILabel == null) {
			try {
				basedateUILabel = new nc.ui.pub.beans.UILabel();
				basedateUILabel.setName("UILabel1");
				basedateUILabel.setText(ResHelper.getString("6013commonbasic","06013commonbasic0266")/*@res "��׼���ڣ�"*/);
				basedateUILabel.setBounds(10, 0, 80, 22);
				basedateUILabel.setVisible(true);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return basedateUILabel;
	}

	/**
	 * ���� UILabel2 ����ֵ��
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private nc.ui.pub.beans.UILabel getUILabel2() {
		if (avgmonthUILabel == null) {
			try {
				avgmonthUILabel = new nc.ui.pub.beans.UILabel();
				avgmonthUILabel.setName("UILabel2");
				avgmonthUILabel.setText(ResHelper.getString("6013commonbasic","06013commonbasic0267")/*@res "ƽ��������"*/);
				avgmonthUILabel.setBounds(10, 70, 80, 22);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return avgmonthUILabel;
	}


	
	/**
	 * ÿ�������׳��쳣ʱ������
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		Logger.error(exception.getMessage(),exception);
	}
	/**
	 * /**
	 *  * ��ʼ��������ʾ
	 *  * @param		����˵��
	 *  * @return		����ֵ
	 *  * @exception �쳣����
	 *  * @see		��Ҫ�μ�����������
	 *  * @since		�������һ���汾���˷��������ӽ���������ѡ��
	 *  * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ��
	 * *-/
	 *
	 */
	public void initData() {
		try {
			//��Ҫ����д����б�
			getcmbClass().removeItemListener(this);
			
			getcmbClass().addItemListener(this);

		} catch (Exception e) {
			handleException(e);
		}
	}
	/**
	 * ��ʼ���ࡣ
	 */
	/* ���棺�˷������������ɡ� */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("WaParaPanel");
			setLayout(null);
			setSize(240, 200);
			add(getUILabel1(), getUILabel1().getName());
			add(getcmbClassUI(), getcmbClass().getName());
			add(getUILabel2(), getUILabel2().getName());
			add(getcmbItemUI(), getcmbItem().getName());
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		getcmbClass().addItemListener(this);

	}


	UIAsteriskPanelWrapper  cmbClassUI  = null;
	private UIAsteriskPanelWrapper getcmbClassUI(){
		if(cmbClassUI == null){
			cmbClassUI = new UIAsteriskPanelWrapper(getcmbClass());
			cmbClassUI.setBounds(93, 0, 147, 22);
			cmbClassUI.setMustInputItem(true);
		}
		return cmbClassUI;
	}





	private UIAsteriskPanelWrapper  cmbItemUI = null;
	private UIAsteriskPanelWrapper getcmbItemUI() {

		if(cmbItemUI==null){
			cmbItemUI = new UIAsteriskPanelWrapper(getcmbItem());
			cmbItemUI.setBounds(93, 70, 147, 22);
			cmbItemUI.setMustInputItem(true);
		}

		return cmbItemUI;

	}



	

	/**
	 * /**
	 *  * �л����ʱˢ����Ŀ�б�
	 *  * @param		����˵��
	 *  * @return		����ֵ
	 *  * @exception �쳣����
	 *  * @see		��Ҫ�μ�����������
	 *  * @since		�������һ���汾���˷��������ӽ���������ѡ��
	 *  * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ��
	 * *-/
	 *
	 * @return java.lang.String
	 */
	public void itemStateChanged(java.awt.event.ItemEvent e) {
		if (e.getStateChange() != 2) {
			return;
		}
		
		//	initCmbArea();
	}

	/**
	 * /**
	 *  * ������������
	 *  * @param		����˵��
	 *  * @return		����ֵ
	 *  * @exception �쳣����
	 *  * @see		��Ҫ�μ�����������
	 *  * @since		�������һ���汾���˷��������ӽ���������ѡ��
	 *  * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ��
	 * *-/
	 *
	 * @param newDatatype int
	 */
	public void setDatatype(int newDatatype) {
		datatype = newDatatype;
	}

	/**
	 * ����ѡ��ĺ������²������ý���
	 * @param paras nc.vo.wa.func.FunctableItemVO[]
	 */
	public void updateDis(FunctableItemVO[] paras) {

		
		if(paras==null){
			return ;
		}
		
		
		
	}

	

	
	

	

	

	


	

	

	/**
	 * �˴����뷽�������� �������ڣ�(2003-11-10 10:16:25)
	 *
	 * @return int
	 */
	public int getParasLen() {
		return parasLen;
	}



	
	/**
	 * �˴����뷽��������
	 * �������ڣ�(2003-11-10 10:16:25)
	 * @param newParasLen int
	 */
	public void setParasLen(int newParasLen) {
		parasLen = newParasLen;
	}

	/**
	 * ���²������ý�����ʾ������ѡ�еĲ�ͬ����
	 */
	public void updateDis(int index) {
		this.funcIndex = index;

		/**
		 *Ӧ�÷ֿ� ��
		 */

		initData();



	}

	/**
	 * /**
	 *  * ���²������ý�����ʾ������ѡ�еĲ�ͬ����
	 *  * @param		����˵��
	 *  * @return		����ֵ
	 *  * @exception �쳣����
	 *  * @see		��Ҫ�μ�����������
	 *  * @since		�������һ���汾���˷��������ӽ���������ѡ��
	 *  * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ��
	 * *-/
	 *
	 * @param paras nc.vo.wa.func.FunctableItemVO[]
	 */
	public void updateDis(String funcname) {

	}


	String currentItemKey = null;
	/**
	 * @author zhangg on 2010-6-3
	 * @see nc.ui.hr.itemsource.view.IParaPanel#setCurrentItemKey(java.lang.String)
	 */
	@Override
	public void setCurrentItemKey(String itemKey) {
		currentItemKey = itemKey;

	}

}