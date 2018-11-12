package nc.ui.wa.classitem.view;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashMap;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IClassItemQueryService;
import nc.itf.hr.wa.IItemQueryService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapProcessor;
import nc.md.model.impl.MDEnum;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefRecentRecordsUtil;
import nc.ui.bd.ref.model.AccountRefModelWithSystem;
import nc.ui.hr.itemsource.view.AbstractBillItemEditor;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.wa.classitem.model.ClassItemAppModel;
import nc.ui.wa.item.model.HRWADefdocGridRefModel;
import nc.ui.wa.item.model.ItemDefaultValueProvider;
import nc.ui.wa.item.util.ItemUtils;
import nc.ui.wa.item.view.ItemBillFormEditor;
import nc.ui.wa.item.view.custom.CheckedTextField;
import nc.ui.wa.ref.ClassItemTransferRefModel;
import nc.ui.wa.ref.WaItemRefForClassItem;
import nc.vo.bd.accsystem.AccSystemVO;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.wa.classitem.RoundTypeEnum;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.item.PropertyEnumVO;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author: wh
 * @date: 2009-12-8 下午01:01:25
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
@SuppressWarnings("serial")
public class ClassItemFormEditor extends ItemBillFormEditor {

	private WaItemRefForClassItem waItemRefModel;
	private ClassItemDataSourcePanel dataSourcePanel;
	private CheckedTextItemEditor transferEditor;
	private final String defaultAccsystem = "0001Z0100ACCSYSTEM01"; //默認值-nc自帶的基礎科目體系


	private CheckedTextItemEditor getTransferEditor() {
		return transferEditor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initUI() {
		super.initUI();
		UIRefPane refPane = (UIRefPane) billCardPanel.getHeadItem(WaClassItemVO.PK_WA_ITEM).getComponent();
		refPane.setButtonFireEvent(true);
		waItemRefModel = (WaItemRefForClassItem)refPane.getRefModel();
		waItemRefModel.setContext((WaLoginContext) getModel().getContext());
		//重置 项目分类的参照！从公共薪资项目卡片界面拷贝过来的，将发放项目卡片界面
		//“项目分类”的参照重设为公共薪资项目的项目分类参照。目的是将该参照对话框中“新增”按钮屏蔽。
		//需求问题编码：NCdp204895901。2014.01.07
		BillItem categoryID = getClassItemCategory();
		UIRefPane ref = (UIRefPane) categoryID.getComponent();
		HRWADefdocGridRefModel hrWADefdocGridRefModel = new HRWADefdocGridRefModel(
				ItemDefaultValueProvider.getDefdocVO().getPk_defdoclist());
		hrWADefdocGridRefModel.setPara1("1002Z710000000004RC2");
		ref.setRefModel(hrWADefdocGridRefModel);
		
		//start Ares.Tank 2018-8-29 18:38:47 讀取台灣薪資科目體系,編碼固定為0004
		String sqlStr = "select BD_ACCSYSTEM."+AccSystemVO.PK_ACCSYSTEM+" from BD_ACCSYSTEM  where code ='0004'";
		
		IUAPQueryBS iUAPQueryBS = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());   
		String pkAccsystem = null;
		try {
			Map<String,String> result = (HashMap<String, String>)iUAPQueryBS. executeQuery(sqlStr,new MapProcessor ());
			if(null==result||result.size()<=0){
				pkAccsystem = defaultAccsystem;//设置默认的科目体系
			}else{
				pkAccsystem = result.get(AccSystemVO.PK_ACCSYSTEM);
			}
			
		} catch (Exception e) {
			
			pkAccsystem = defaultAccsystem;
			e.printStackTrace();
		}  
		//end Ares.Tank 2018-8-29 18:38:47 讀取台灣薪資科目體系,編碼固定為0004
		
		//start Ares.Tank 2018-8-7 18:18:46 设置会计科目的科目表
		UIRefPane debitentryRefPane = (UIRefPane) billCardPanel.getHeadItem(WaClassItemVO.DEBITENTRY).getComponent();
		UIRefPane debitentryBRefPane = (UIRefPane) billCardPanel.getHeadItem(WaClassItemVO.DEBITENTRY_B).getComponent();
		UIRefPane creditRefPane = (UIRefPane) billCardPanel.getHeadItem(WaClassItemVO.CREDITSUBJECT).getComponent();
		UIRefPane creditSubRefPane = (UIRefPane) billCardPanel.getHeadItem(WaClassItemVO.CREDISUBJECT_B).getComponent();
		
		AccountRefModelWithSystem accountRefModelWithSystem = new AccountRefModelWithSystem();
		accountRefModelWithSystem.setPk_accsystem(pkAccsystem);
		accountRefModelWithSystem.reset();
		
		AccountRefModelWithSystem accountRefModelWithSystem1 = new AccountRefModelWithSystem();
		accountRefModelWithSystem1.setPk_accsystem(pkAccsystem);
		accountRefModelWithSystem1.reset();
		
		AccountRefModelWithSystem accountRefModelWithSystem2 = new AccountRefModelWithSystem();
		accountRefModelWithSystem2.setPk_accsystem(pkAccsystem);
		accountRefModelWithSystem2.reset();
		
		AccountRefModelWithSystem accountRefModelWithSystem3 = new AccountRefModelWithSystem();
		accountRefModelWithSystem3.setPk_accsystem(pkAccsystem);
		accountRefModelWithSystem3.reset();
		
		
		
		debitentryRefPane.setRefModel(accountRefModelWithSystem);
		debitentryBRefPane.setRefModel(accountRefModelWithSystem1);
		creditRefPane.setRefModel(accountRefModelWithSystem2);
		creditSubRefPane.setRefModel(accountRefModelWithSystem3);
		
		//end Ares.Tank 2018-8-7 18:22:03 设置会计科目表
		
        // guoqt取消参照缓存的下拉快捷提示，数据权限有问题
		refPane.getUITextField().addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusGained(FocusEvent evt)
            {
                RefRecentRecordsUtil.clear(waItemRefModel);
            }
        });
	}
	
	/**
	 * 取得发放项目卡片界面“项目分类”界面元素
	 */
	protected BillItem  getClassItemCategory(){
		return getHeadItem(WaItemVO.CATEGORY_ID);
	}


	@Override
	protected void onEdit() {
		super.onEdit();

		updateUIOnEdit(getTypeEnumValue());
		updteWaItemFileState(false);

	}

	@Override
	protected  BillItem getMidBillItem(){
		return getHeadItem(WaItemVO.MID );
	}

	/**
	 * 设置结转设置和预警条件设置是否可以编辑
	 * @param isEnable
	 */
	private void setEnable(boolean isEnable){
		
		//当数据类型不是”数值型“时，预警条件不可用。by：xiejie
		TypeEnumVO typeEnum = getTypeEnumValue();
		if(!typeEnum.equals(TypeEnumVO.FLOATTYPE)){
			isEnable = false;
		}
		//end by:xiejie
		
		billCardPanel.getHeadItem("transfer").setEnabled(isEnable);
		billCardPanel.getHeadItem("sumceil").setEnabled(isEnable);
		billCardPanel.getHeadItem("sumfloor").setEnabled(isEnable);
		billCardPanel.getHeadItem("psnceil").setEnabled(isEnable);
		billCardPanel.getHeadItem("psnfloor").setEnabled(isEnable);
		
	}

	@Override
	public void handleEvent(AppEvent event)
	{
		super.handleEvent(event);
		
		if (AppEventConst.UISTATE_CHANGED == event.getType()) {
			
			if (getModel().getUiState() == UIState.ADD) {
			} else if (getModel().getUiState() == UIState.EDIT) {
			} else {
				if (((ClassItemAppModel)getModel()).getData() != null && 
						((ClassItemAppModel)getModel()).getData().size() > 0 && 
							((ClassItemAppModel)getModel()).getSelectedRow() == -1 ) {
							if (((ClassItemAppModel)getModel()).getSelectedOperaRows().length == 0) {
								((BillManageModel)getModel()).setSelectedRow(0);
							}else{
								((BillManageModel)getModel()).setSelectedRow(((ClassItemAppModel)getModel()).getSelectedOperaRows()[0]);
							}
						} 
			} 
		}

		if (getModel().getUiState() == UIState.NOT_EDIT)
		{
			setEnable(false);
		}else if (getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT){
			setEnable(true);
		}
	}

	private CheckedTextItemEditor createTransferEditor(){
		transferEditor = new CheckedTextItemEditor("transfer","istransfer","destitempk","destitemcol");
		AbstractRefModel refmodel = new ClassItemTransferRefModel(this);
		transferEditor.setRefmodel(refmodel);

		BillItem item = billCardPanel.getHeadItem(transferEditor.getKeyOfBillItem());
		transferEditor.setItem(item);
		//		transferEditor.initialize();
		//初始化为不可用
		transferEditor.setContentEnabled(true);
		item.setItemEditor(transferEditor);
		return transferEditor;
	}

	/**
	 * @author xuanlt on 2010-1-21
	 * @see nc.ui.wa.item.view.ItemBillFormEditor#createCustomEditors()
	 */
	@Override
	protected AbstractBillItemEditor[] createCustomEditors() {
		// TODO Auto-generated method stub
		AbstractBillItemEditor[] editors = new AbstractBillItemEditor[]{

				createTransferEditor(),
				getItemDataSourcePanel(),
				new CheckedTextField("sumceil","sumceilflag","nsumceil"),
				new CheckedTextField("sumfloor","sumfloorflag","nsumfloor"),
				new CheckedTextField("psnceil","psnceilflag","npsnceil"),
				new CheckedTextField("psnfloor","psnfloorflag","npsnfloor"),
		};
		return editors;
	}



	/**
	 * 修改时，设定界面的可用性
	 * @author xuanlt on 2010-1-18
	 * @return  void
	 */
	protected void updateUIOnEdit(TypeEnumVO typeName) {

		WaClassItemVO classItem = (WaClassItemVO) getModel().getSelectedData();
		boolean isGroupItem = false;
		try {
			//检查是否是集团项目，是的话值能修改名称
			isGroupItem = NCLocator.getInstance().lookup(
					IClassItemQueryService.class).checkGroupItem(classItem);
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}
		setRoundTypeEnable();
		String itemkey = classItem.getItemkey();
		if(ItemUtils.isSystemItemKey(itemkey)){
			//单独处理系统项目
			for(BillItem item : billCardPanel.getHeadItems()){
				//NAME可以修改
				if(item.getKey().equals(WaClassItemVO.NAME)||
						item.getKey().equals(WaClassItemVO.ROUND_TYPE)	||
						item.getKey().equals(WaClassItemVO.BANKACCOUNT)||
						item.getKey().equals(WaClassItemVO.INAPPROVEDITEM)){
					item.setEnabled(true);
				}else if(item.getKey().equals(DATA_SOURCE_PANEL)){
					//数据来源 ： 已扣税  、已扣税基数 、补发扣税 、 补发扣税基数不能修改
					if(ItemUtils.isSpecialSystemItem(itemkey)){
						item.setEnabled(false);
					}else{
						item.setEnabled(true);
					}
				}else if(isAlertItem(item.getKey())){
					//预警条件可以编辑
					item.setEnabled(true);
				}else{
					//其他不可以编辑
					item.setEnabled(false);
				}
			}
		} else if (isGroupItem) {
			//单独处理系统项目
			for (BillItem item : billCardPanel.getHeadItems()) {
				// NAME可以修改
				if (item.getKey().equals(WaClassItemVO.NAME)) {
					item.setEnabled(true);
				} else {
					// 其他不可以编辑
					item.setEnabled(false);
				}
			}
		} else {
			// 根据数据类型 决定 舍位方式是否可用
			setHeadItemEnable(WaClassItemVO.PK_WA_ITEM, false);
		}

	}

	private void setRoundTypeEnable() {
		Integer itemType = (Integer) getHeadItemValue(WaClassItemVO.IITEMTYPE);
		if (TypeEnumVO.valueOf(TypeEnumVO.class, itemType).equals(
				TypeEnumVO.FLOATTYPE)) {
			setHeadItemEnable(WaClassItemVO.ROUND_TYPE, true);
		} else {
			setHeadItemEnable(WaClassItemVO.ROUND_TYPE, false);
		}

	}

	private boolean isAlertItem(String item){
		if(item.equals("sumceil")||
				item.equals("sumfloor")
				||item.equals("psnceil")
				||item.equals("psnfloor")){
			return true;

		}

		return false;
	}

	@Override
	protected void onAdd() {
		waItemRefModel.clearData();
		super.onAdd();
		setRoundTypeEnable();

	}

	@Override
	public WaClassItemVO getValue() {
		WaClassItemVO vo = (WaClassItemVO) super.getValue();

		if(!isAddPrivateItem()){
			//数据类型总是不对，需要重新设置一下
			vo.setIitemtype((Integer)getHeadItemValue(WaItemVO.PK_WA_ITEM+"." + WaItemVO.IITEMTYPE));
		}


		getTransferEditor().collectData(vo);

		/**
		 * 应对uap中value ==null 则不赋予值的问题
		 */
		if(vo.getCategory_id()==null){
			vo.setCategory_id("");
		}


		return vo;
	}

	private boolean isAddPrivateItem(){
		String pkwaitem = (String)	getHeadItemValue(WaClassItemVO.PK_WA_ITEM);
		return StringUtils.isBlank(pkwaitem);
	}

	/**
	 * @author xuanlt on 2010-1-21
	 * @see nc.ui.wa.item.view.ItemBillFormEditor#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object object) {
		// TODO Auto-generated method stub
		super.setValue(object);

		//设定 结转
		//getTransferEditor().setDataToEditor((WaClassItemVO)object);

	}




	@Override
	protected BillItem getInHiBillItem(){
		return getHeadItem(WaItemVO.PK_WA_ITEM+"."+WaItemVO.ISINHI );
	}

	@Override
	protected BillItem getTaxFlagBillItem(){
		return getHeadItem(WaClassItemVO.TAXFLAG );
	}



	@Override
	protected  BillItem getIpropertyBillItem(){
		return getHeadItem(WaClassItemVO.IPROPERTY );
	}


	@Override
	protected  BillItem  getFldWidthBillItem(){
		return getHeadItem(WaClassItemVO.IFLDWIDTH );
	}

	@Override
	protected  BillItem  getFldDecimalBillItem(){
		return getHeadItem(WaClassItemVO.IFLDDECIMAL );
	}


	//	"pk_wa_item.iitemtype"
	@Override
	public Integer getItemDataType(){
		Integer itemType = (Integer)getHeadItem(WaItemVO.PK_WA_ITEM+"."+ WaClassItemVO.IITEMTYPE).getValueObject();
		if(itemType == null){
			itemType = 0;
		}

		return  itemType;
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		super.afterEdit(e);
		if(WaClassItemVO.PK_WA_ITEM.equals(e.getKey())){
			String pk_wa_item = (String) getHeadItemValue(WaClassItemVO.PK_WA_ITEM);
			try {
				WaItemVO item  = new WaItemVO();
				if(!StringUtils.isBlank(pk_wa_item)){
					item = NCLocator.getInstance().lookup(IItemQueryService.class).queryWaItemVOByPk(pk_wa_item);
				}

				WaClassItemVO classItemVO = getValue();
				classItemVO.merge(item);
				classItemVO.setCreator(null);
				classItemVO.setCreationtime(null);
				classItemVO.setModifier(null);
				classItemVO.setModifiedtime(null);
				setValue(classItemVO);

				//获取项目的长度和精度并更改预算精度  by wangqim
				resetPrecision();
				
				//设置公共项目字段的可编辑性
				if(StringUtils.isBlank(pk_wa_item)){
					updteWaItemFileState(true);

				}else{
					updteWaItemFileState(false);
					
				//当“增减属性”为“其他项“时，设置”中间项“为可编辑状态，	by：xiejie
					PropertyEnumVO propEnum = MDEnum.valueOf(PropertyEnumVO.class, (Integer)(getHeadItem(WaItemVO.IPROPERTY).getValueObject()));
					getMidBillItem().setEnabled(propEnum == PropertyEnumVO.OTHER);
				////end by:xiejie
					
				}
				//设定自定义编辑器的状态
				updateCustomEditorState(getTypeEnumValue());

				updateTaxFlag(getTypeEnumValue());

				updateRoundType(getTypeEnumValue());


			} catch (BusinessException e1) {
				throw new BusinessRuntimeException(ResHelper.getString("60130payitem","060130payitem0213")/*@res "查询薪资项目时出现异常！"*/,e1);
			}
		}if(WaClassItemVO.IITEMTYPE.equals(e.getKey())){
			updateRoundType(getTypeEnumValue());
		}
	}

//

	
	

	private void updteWaItemFileState(boolean flag){
		getHeadItem(WaClassItemVO.IITEMTYPE).setEnabled(flag);
		getHeadItem(WaClassItemVO.IFLDWIDTH).setEnabled(flag);
		getHeadItem(WaClassItemVO.IPROPERTY).setEnabled(flag);
		getHeadItem(WaClassItemVO.MID).setEnabled(flag);
		updteWaItemDecimalState();
	}

	private void updteWaItemDecimalState(){
		TypeEnumVO typeEnum = getTypeEnumValue();
		if(getModel().getUiState().equals(UIState.ADD)|| getModel().getUiState().equals(UIState.EDIT)){
			if(TypeEnumVO.FLOATTYPE.equals(typeEnum)){
				getHeadItem(WaClassItemVO.IFLDDECIMAL).setEnabled(true);
			}else{
				getHeadItem(WaClassItemVO.IFLDDECIMAL).setEnabled(false);
			}
		}else{
			getHeadItem(WaClassItemVO.IFLDDECIMAL).setEnabled(false);
		}


	}


	private void updateTaxFlag(TypeEnumVO typeName){
		BillItem taxItem = getHeadItem(WaClassItemVO.TAXFLAG);
		if(typeName.equals(TypeEnumVO.FLOATTYPE)){
			taxItem.setEnabled(true);
		}else{
			taxItem.setEnabled(false);
		}

	}


	private void updateRoundType(TypeEnumVO typeName){
		BillItem roundTypeItem = getHeadItem(WaClassItemVO.ROUND_TYPE);
		if(typeName.equals(TypeEnumVO.FLOATTYPE)){
			//四舍五入
			((UIComboBox)roundTypeItem.getComponent()).setSelectedItem(RoundTypeEnum.ROUND.value());
			roundTypeItem.setEnabled(true);
		}else{
			//不选
			((UIComboBox)roundTypeItem.getComponent()).setSelectedIndex(0);
			roundTypeItem.setEnabled(false);
		}

	}

	@Override
	protected ClassItemDataSourcePanel createItemDataSourcePanel(){
		if(dataSourcePanel == null){
			dataSourcePanel = new ClassItemDataSourcePanel(DATA_SOURCE_PANEL,getModel(),this);
		}
		return  dataSourcePanel;
	}

	@Override
	protected BillItem  getItemCategory(){
		return getHeadItem(WaItemVO.PK_WA_ITEM+"."+WaItemVO.CATEGORY_ID);
	}

}