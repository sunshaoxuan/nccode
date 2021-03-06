package nc.ui.wa.taxrate.view;

import nc.itf.org.IOrgConst;
import nc.ui.hr.uif2.model.IDefaultValueProvider;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.AppEvent;
import nc.vo.hr.globalapp.GlobalLoginContext;
import nc.vo.wa.taxrate.TaxBaseVO;
import nc.vo.wa.taxrate.TaxTableTypeEnum;
import nc.vo.wa.taxrate.TaxTableVO;

public class TaxRateForm extends HrBillFormEditor implements BillEditListener {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7090203937695314195L;

	private IDefaultValueProvider defaultValueProvider;
	
	
	public IDefaultValueProvider getDefaultValueProvider() {
		return defaultValueProvider;
	}

	public void setDefaultValueProvider(IDefaultValueProvider defaultValueProvider) {
		this.defaultValueProvider = defaultValueProvider;
	}

	public TaxRateForm() {
		super();
	}

	@Override
	public void initUI() {
		super.initUI();
		this.getBillCardPanel().addEditListener(this);
		// 去掉子表排序功能
		getBillCardPanel().getBillTable().removeSortListener();
		//20151214 shenliangc NCdp205559130  薪资规则，新增一行后，填入数据用回车换行，第二行行号为空，保存时报错
		//去掉回车增行、子表表体右键。
		getBillCardPanel().getBodyPanel().setAutoAddLine(false);
		getBillCardPanel().getBodyPanel().setBBodyMenuShow(false);
	}

	@Override
	protected void onAdd() {
		// TODO Auto-generated method stub
		super.onAdd();
	
//		//根据税率表类型，决定显示什么
//		Integer taxtabletype = (Integer)getHeadItemValue(TaxBaseVO.ITBLTYPE);
//		if(taxtabletype.equals(TaxTableTypeEnum.WORKTAX)){
//			setNdeductcritnVisible(false);
//		}
		
		setHeadItemValue(TaxBaseVO.PK_COUNTRY,getGContext().getPk_country() );
		
	}
	
	private void setNdeductcritnVisible(boolean visible){
		getHeadItem(TaxBaseVO.NDEBUCTAMOUNT).setShow(visible);
		getHeadItem(TaxBaseVO.MDEBUCTAMOUNT).setShow(visible);				
		getBillCardPanel().initPanelByPos(BillCardPanel.HEAD);
		
		
	}
	
	private void setFixTableItemVisible(boolean visible){
		getHeadItem(TaxBaseVO.NDEBUCTRATE).setShow(visible);
		getHeadItem(TaxBaseVO.NDEBUCTLOWEST).setShow(visible);
		getHeadItem(TaxBaseVO.NFIXRATE).setShow(visible);
		getHeadItem(TaxBaseVO.NDEDUCTCRITN).setShow(visible);
		getBillCardPanel().initPanelByPos(BillCardPanel.HEAD);
		
		
	}
	
	
	
	private void setBodyItemVisible(boolean visible){
		getBillCardPanel().getBodyItem(TaxTableVO.getDefaultTableName(),TaxTableVO.NDEBUCTRATE).setShow(visible);
		getBillCardPanel().getBodyItem(TaxTableVO.getDefaultTableName(),TaxTableVO.NDEBUCTAMOUNT).setShow(visible);
		getBillCardPanel().initPanelByPos(BillCardPanel.BODY);
	}
	
	private void setBodyTableVisible(boolean visible){
		// 2015-11-3 zhousze 重写该方法，之前用的是setVisible，控制不完全。现在用UAP提供的方法，使表头最大化，如果是0，最大化，继续
		// 赋值为0就恢复默认 begin
		int maxmizedPos = getBillCardPanel().getMaxmizedPos();
		if(visible && maxmizedPos == 0){
			getBillCardPanel().swichMaximizedView(0);
		}
		if(!visible && maxmizedPos == -1){
			getBillCardPanel().swichMaximizedView(0);
		}
		// end
//		getBillCardPanel().getBodyUIPanel().setVisible(visible);
	}
	
	BillItem getHeadItem(String itemkey){
		return this.getBillCardPanel().getHeadItem(itemkey);
		
	}
	
	 @Override
    public void handleEvent(AppEvent event)
    {
       super.handleEvent(event);
        //  2015-11-9 zhousze 点击新增、修改等按钮时，响应事件到卡片编辑态，没有改变税率表类型，
        // 不需要执行，可考虑注掉，需要时再添加。这里只要用于新增时初始化界面，现在适配UAP代码
        //  有问题，现在修改为点新增按钮时会获取当前选中行的itbltype，并赋值给新增的卡片界面。begin
//       if (getModel().getUiState() == UIState.ADD )
//       {
//    	   changeTaxtableType();
//       }
       // end
    }
	
	@Override
	protected void onSelectionChanged() {
		synchronizeDataFromModel();
		
		//如果是中国区域
		
		changeTaxtableType();
					
	} 
	
	@Override
	public void afterEdit(BillEditEvent evt) {
		super.afterEdit(evt);
		//如果是中国区域
		
		if(evt.getKey().equals(TaxBaseVO.ITBLTYPE)){
			
			changeTaxtableType();
		}
		
	}
	
	private void changeTaxtableType(){
		GlobalLoginContext contxt = getGContext();
		String pk_country = contxt.getPk_country();
		if(IOrgConst.DEFAULTCOUNTRYZONE.equals(pk_country)){
			showCN();
		}else{
			//只展现编码、名称、类型
			showOtherCountry();
		}
	}
	
	
	private void	showOtherCountry(){
		//劳务税率表
		setNdeductcritnVisible(false);
		setFixTableItemVisible(false);
		//表体展现
		setBodyItemVisible(true);
		setBodyTableVisible(true);
		
	}
	
	
	private void showCN(){
		Integer taxtabletype = (Integer)getHeadItemValue(TaxBaseVO.ITBLTYPE);
		if(taxtabletype.equals(TaxTableTypeEnum.WORKTAX.value())){
			//劳务税率表
			setNdeductcritnVisible(false);
			setFixTableItemVisible(false);
			//表体展现
			setBodyItemVisible(true);
			setBodyTableVisible(true);
		}else if(taxtabletype.equals(TaxTableTypeEnum.FIXTAX.value())){
			//固定税率表
			setNdeductcritnVisible(false);
			setFixTableItemVisible(true);
			//表体展现
			setBodyItemVisible(true);
			setBodyTableVisible(false);		
		}else if(taxtabletype.equals(TaxTableTypeEnum.FOREIGNWORKTAX.value())){//add by ward 2018-01-16 外籍员工报税
			//劳务税率表
			setNdeductcritnVisible(false);
			setFixTableItemVisible(false);
			//表体展现
			setBodyItemVisible(true);
			setBodyTableVisible(true);		
		}else {
			//变动税率表			
			setNdeductcritnVisible(true);
			setFixTableItemVisible(false);
			//表体展现
			setBodyItemVisible(false);
			setBodyTableVisible(true);			
			
		}	
	}
   
   public GlobalLoginContext  getGContext(){
	 return   (GlobalLoginContext)getModel().getContext() ;
   }
   public Object getValue2()
    {
    return getBillCardPanel().getBodyPanel().getTableModel().getBodyValueVOs(TaxTableVO.class.getName());
  }
	

}
