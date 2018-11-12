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
 * @date: 2018-08-29 上午09:22:28
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class AvgSalaryPanel extends UIPanel implements ItemListener, IParaPanel, IRefPanel, IFormulaConst {


	private static final long serialVersionUID = -5415459347134279156L;

	private UIComboBox cbasedate = null;

	private UIComboBox avgmoncount = null;

	private UILabel basedateUILabel = null;

	private UILabel avgmonthUILabel = null;

	/*private WaClassVO[] classdata = null; //薪资方案

	private WaClassItemVO[] itemdata = null; //薪资项目
*/
	private int datatype = 0; //函数需要返回的数据类型，默认数字型

	//平均月数参照
	private UIRefPane refAvgMonCount = null;
	


	public int parasLen = 0;

	/* 函数序号 */
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
	 * WaParaPanel 构造子注解。
	 */
	public AvgSalaryPanel() {
		super();
		initialize();
	}

	/**
	 *  函数参数合法性校验
	 */
	public void checkPara(int dataType) throws Exception {
		try {
			//判断非空
			if (getcmbClass().isVisible() && getcmbClass().getSelectedIndex() <= 0){
				throw new Exception(ResHelper.getString("6013salaryctymgt","06013salaryctymgt0280")/*@res "基准日期不能为空！"*/);
			}
		
			
			String nullstr = "";
			if (getcmbClass().isVisible() && getcmbClass().getSelectedIndex() <= 0) {
				nullstr += ResHelper.getString("6013salaryctymgt","06013salaryctymgt0281")/*@res "平均月数方案"*/;
			}
			
			if (nullstr.length() > 0) {
				throw new Exception(nullstr + ResHelper.getString("6013commonbasic","06013commonbasic0021")/*@res "不能为空！"*/);
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		
			
			
	}

	

	public void clearDis() {
	}



	

	/**
	 * 得到该类别的所有薪资项目
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
	 * 得到该类别指定期间下的薪资项目
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
	 * 返回 cmbClass 特性值。
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	/* 警告：此方法将重新生成。 */
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
				ml[0] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0286")/*@res "薪资开始日期"*/,"UPPnc_hr_wa_pub1-000016");/* -=notranslate=- */
				ml[1] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0287")/*@res "薪资结束日期"*/,"UPPnc_hr_wa_pub1-000017");/* -=notranslate=- */
				ml[2] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0288")/*@res "资遣日期"*/,"UPPnc_hr_wa_pub1-000018");/* -=notranslate=- */
				ml[3] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0289")/*@res "退休日期"*/,"UPPnc_hr_wa_pub1-000019");/* -=notranslate=- */
				ml[4] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0290")/*@res "特休日期"*/,"UPPnc_hr_wa_pub1-000020");/* -=notranslate=- */

				String[] mlDefault = new String[]{ResHelper.getString("6013salaryctymgt","06013salaryctymgt0286")/*@res "薪资开始日期"*/,ResHelper.getString("6013salaryctymgt","06013salaryctymgt0287")/*@res "薪资结束日期 "*/,ResHelper.getString("6013salaryctymgt","06013salaryctymgt0288")/*@res "资遣日期"*/,ResHelper.getString("6013salaryctymgt","06013salaryctymgt0289")/*@res "退休日期"*/,ResHelper.getString("6013salaryctymgt","06013salaryctymgt0290")/*@res "特休日期"*/};/* -=notranslate=- */
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
	 * 返回 cmbItem 特性值。
	 * @return nc.ui.pub.beans.UIComboBox
	 */
	/* 警告：此方法将重新生成。 */
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
				ml[0] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0282")@res "3月份","UPPnc_hr_wa_pub1-000016"); -=notranslate=- 
				ml[1] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0283")@res "6月份","UPPnc_hr_wa_pub1-000017"); -=notranslate=- 
				ml[2] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0284")@res "9月份","UPPnc_hr_wa_pub1-000018"); -=notranslate=- 
				ml[3] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0285")@res "12月份","UPPnc_hr_wa_pub1-000019"); -=notranslate=- 
				ml[4] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0291")@res "1月份","UPPnc_hr_wa_pub1-000020"); -=notranslate=- 
				ml[5] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0292")@res "2月份","UPPnc_hr_wa_pub1-000021"); -=notranslate=- 
				ml[6] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0293")@res "4月份","UPPnc_hr_wa_pub1-000022"); -=notranslate=- 
				ml[7] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0294")@res "5月份","UPPnc_hr_wa_pub1-000023"); -=notranslate=- 
				ml[8] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0295")@res "7月份","UPPnc_hr_wa_pub1-000024"); -=notranslate=- 
				ml[9] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0296")@res "8月份","UPPnc_hr_wa_pub1-000025"); -=notranslate=- 
				ml[10] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0297")@res "10月份","UPPnc_hr_wa_pub1-000026"); -=notranslate=- 
				ml[11] = nc.ui.ml.NCLangRes.getInstance().getString("nc_hr_wa_pub1",ResHelper.getString("6013salaryctymgt","06013salaryctymgt0298")@res "11月份","UPPnc_hr_wa_pub1-000027"); -=notranslate=- 

				String[] mlDefault = new String[]{ResHelper.getString("6013salaryctymgt","06013salaryctymgt0291")@res "1月",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0292")@res "2月",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0282")@res "3月",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0293")@res "4月份",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0294")@res "5月份",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0283")@res "6月份",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0295")@res "7月份",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0296")@res "8月份",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0284")@res "9月份",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0297")@res "10月份",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0298")@res "11月份",
						ResHelper.getString("6013salaryctymgt","06013salaryctymgt0285"),@res "12月份"}; -=notranslate=- 
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
	 * 返回函数的参数内容
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
	 *  * 返回函数的参数内容
	 *  * @param		参数说明
	 *  * @return		返回值
	 *  * @exception 异常描述
	 *  * @see		需要参见的其它内容
	 *  * @since		从类的那一个版本，此方法被添加进来。（可选）
	 *  * @deprecated该方法从类的那一个版本后，已经被其它方法替换。（可选）
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
	 * 返回 UILabel1 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabel1() {
		if (basedateUILabel == null) {
			try {
				basedateUILabel = new nc.ui.pub.beans.UILabel();
				basedateUILabel.setName("UILabel1");
				basedateUILabel.setText(ResHelper.getString("6013commonbasic","06013commonbasic0266")/*@res "基准日期："*/);
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
	 * 返回 UILabel2 特性值。
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* 警告：此方法将重新生成。 */
	private nc.ui.pub.beans.UILabel getUILabel2() {
		if (avgmonthUILabel == null) {
			try {
				avgmonthUILabel = new nc.ui.pub.beans.UILabel();
				avgmonthUILabel.setName("UILabel2");
				avgmonthUILabel.setText(ResHelper.getString("6013commonbasic","06013commonbasic0267")/*@res "平均月数："*/);
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
	 * 每当部件抛出异常时被调用
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {

		Logger.error(exception.getMessage(),exception);
	}
	/**
	 * /**
	 *  * 初始化界面显示
	 *  * @param		参数说明
	 *  * @return		返回值
	 *  * @exception 异常描述
	 *  * @see		需要参见的其它内容
	 *  * @since		从类的那一个版本，此方法被添加进来。（可选）
	 *  * @deprecated该方法从类的那一个版本后，已经被其它方法替换。（可选）
	 * *-/
	 *
	 */
	public void initData() {
		try {
			//主要是填写类别列表
			getcmbClass().removeItemListener(this);
			
			getcmbClass().addItemListener(this);

		} catch (Exception e) {
			handleException(e);
		}
	}
	/**
	 * 初始化类。
	 */
	/* 警告：此方法将重新生成。 */
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
	 *  * 切换类别时刷新项目列表
	 *  * @param		参数说明
	 *  * @return		返回值
	 *  * @exception 异常描述
	 *  * @see		需要参见的其它内容
	 *  * @since		从类的那一个版本，此方法被添加进来。（可选）
	 *  * @deprecated该方法从类的那一个版本后，已经被其它方法替换。（可选）
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
	 *  * 设置数据类型
	 *  * @param		参数说明
	 *  * @return		返回值
	 *  * @exception 异常描述
	 *  * @see		需要参见的其它内容
	 *  * @since		从类的那一个版本，此方法被添加进来。（可选）
	 *  * @deprecated该方法从类的那一个版本后，已经被其它方法替换。（可选）
	 * *-/
	 *
	 * @param newDatatype int
	 */
	public void setDatatype(int newDatatype) {
		datatype = newDatatype;
	}

	/**
	 * 根据选择的函数更新参数设置界面
	 * @param paras nc.vo.wa.func.FunctableItemVO[]
	 */
	public void updateDis(FunctableItemVO[] paras) {

		
		if(paras==null){
			return ;
		}
		
		
		
	}

	

	
	

	

	

	


	

	

	/**
	 * 此处插入方法描述。 创建日期：(2003-11-10 10:16:25)
	 *
	 * @return int
	 */
	public int getParasLen() {
		return parasLen;
	}



	
	/**
	 * 此处插入方法描述。
	 * 创建日期：(2003-11-10 10:16:25)
	 * @param newParasLen int
	 */
	public void setParasLen(int newParasLen) {
		parasLen = newParasLen;
	}

	/**
	 * 更新参数设置界面显示，根据选中的不同函数
	 */
	public void updateDis(int index) {
		this.funcIndex = index;

		/**
		 *应该分开 。
		 */

		initData();



	}

	/**
	 * /**
	 *  * 更新参数设置界面显示，根据选中的不同函数
	 *  * @param		参数说明
	 *  * @return		返回值
	 *  * @exception 异常描述
	 *  * @see		需要参见的其它内容
	 *  * @since		从类的那一个版本，此方法被添加进来。（可选）
	 *  * @deprecated该方法从类的那一个版本后，已经被其它方法替换。（可选）
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