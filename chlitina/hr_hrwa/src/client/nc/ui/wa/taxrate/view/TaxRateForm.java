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
		// ȥ���ӱ�������
		getBillCardPanel().getBillTable().removeSortListener();
		//20151214 shenliangc NCdp205559130  н�ʹ�������һ�к����������ûس����У��ڶ����к�Ϊ�գ�����ʱ����
		//ȥ���س����С��ӱ������Ҽ���
		getBillCardPanel().getBodyPanel().setAutoAddLine(false);
		getBillCardPanel().getBodyPanel().setBBodyMenuShow(false);
	}

	@Override
	protected void onAdd() {
		// TODO Auto-generated method stub
		super.onAdd();
	
//		//����˰�ʱ����ͣ�������ʾʲô
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
		// 2015-11-3 zhousze ��д�÷�����֮ǰ�õ���setVisible�����Ʋ���ȫ��������UAP�ṩ�ķ�����ʹ��ͷ��󻯣������0����󻯣�����
		// ��ֵΪ0�ͻָ�Ĭ�� begin
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
        //  2015-11-9 zhousze ����������޸ĵȰ�ťʱ����Ӧ�¼�����Ƭ�༭̬��û�иı�˰�ʱ����ͣ�
        // ����Ҫִ�У��ɿ���ע������Ҫʱ�����ӡ�����ֻҪ��������ʱ��ʼ�����棬��������UAP����
        //  �����⣬�����޸�Ϊ��������ťʱ���ȡ��ǰѡ���е�itbltype������ֵ�������Ŀ�Ƭ���档begin
//       if (getModel().getUiState() == UIState.ADD )
//       {
//    	   changeTaxtableType();
//       }
       // end
    }
	
	@Override
	protected void onSelectionChanged() {
		synchronizeDataFromModel();
		
		//������й�����
		
		changeTaxtableType();
					
	} 
	
	@Override
	public void afterEdit(BillEditEvent evt) {
		super.afterEdit(evt);
		//������й�����
		
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
			//ֻչ�ֱ��롢���ơ�����
			showOtherCountry();
		}
	}
	
	
	private void	showOtherCountry(){
		//����˰�ʱ�
		setNdeductcritnVisible(false);
		setFixTableItemVisible(false);
		//����չ��
		setBodyItemVisible(true);
		setBodyTableVisible(true);
		
	}
	
	
	private void showCN(){
		Integer taxtabletype = (Integer)getHeadItemValue(TaxBaseVO.ITBLTYPE);
		if(taxtabletype.equals(TaxTableTypeEnum.WORKTAX.value())){
			//����˰�ʱ�
			setNdeductcritnVisible(false);
			setFixTableItemVisible(false);
			//����չ��
			setBodyItemVisible(true);
			setBodyTableVisible(true);
		}else if(taxtabletype.equals(TaxTableTypeEnum.FIXTAX.value())){
			//�̶�˰�ʱ�
			setNdeductcritnVisible(false);
			setFixTableItemVisible(true);
			//����չ��
			setBodyItemVisible(true);
			setBodyTableVisible(false);		
		}else if(taxtabletype.equals(TaxTableTypeEnum.FOREIGNWORKTAX.value())){//add by ward 2018-01-16 �⼮Ա����˰
			//����˰�ʱ�
			setNdeductcritnVisible(false);
			setFixTableItemVisible(false);
			//����չ��
			setBodyItemVisible(true);
			setBodyTableVisible(true);		
		}else {
			//�䶯˰�ʱ�			
			setNdeductcritnVisible(true);
			setFixTableItemVisible(false);
			//����չ��
			setBodyItemVisible(false);
			setBodyTableVisible(true);			
			
		}	
	}
   
   public GlobalLoginContext  getGContext(){
	 return   (GlobalLoginContext)getModel().getContext() ;
   }
	
	

}