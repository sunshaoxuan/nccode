package nc.ui.wa.category.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IWaClass;
import nc.ref.hr.globalapp.HRGlobalCountryRefModel;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.hr.frame.util.BillPanelUtils;
import nc.ui.hr.uif2.view.HrAppEventConst;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.IFunNodeClosingListener;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.AutoShowUpEventSource;
import nc.ui.uif2.components.IAutoShowUpComponent;
import nc.ui.uif2.components.IAutoShowUpEventListener;
import nc.ui.uif2.components.IComponentWithActions;
import nc.ui.uif2.components.ITabbedPaneAwareComponent;
import nc.ui.uif2.components.ITabbedPaneAwareComponentListener;
import nc.ui.uif2.components.TabbedPaneAwareCompnonetDelegate;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.wa.category.model.WaClassModel;
import nc.ui.wa.ref.WaClassRangeRef;
import nc.ui.wa.ref.WaPeriodChemeRefModel;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.category.WaFiorgVO;
import nc.vo.wa.grade.WaPsnhiBVO;
import nc.vo.wa.grade.WaPsnhiVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.pub.WaLoginVOHelper;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author: xuanlt
 * @date: 2009-11-18 上午10:50:09
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WaClassForm extends BillForm implements BillEditListener,
ITabbedPaneAwareComponent, IAutoShowUpComponent, IComponentWithActions {

	private static final long serialVersionUID = -3230896117403084273L;
	private final IAutoShowUpComponent autoShowUpComponent;
	// 列表卡片切换时对外通知控件。
	private final ITabbedPaneAwareComponent tabbedPaneAwareComponent;
	private IFunNodeClosingListener closingListener;
	// 子表按钮
	private List<Action> tabActions;

	private List<Action> actions;

	private boolean isCopy = false;

	public WaClassForm() {
		super();
		autoShowUpComponent = new AutoShowUpEventSource(this);
		tabbedPaneAwareComponent = new TabbedPaneAwareCompnonetDelegate();
	}

	@Override
	protected void setDefaultValue() {
		LoginContext context = getModel().getContext();

		billCardPanel.getHeadItem(WaClassVO.PK_GROUP).setValue(
				context.getPk_group());
		billCardPanel.getHeadItem(WaClassVO.PK_ORG).setValue(
				context.getPk_org());
		billCardPanel.getHeadItem(WaClassVO.CURRID).setValue(
				getWaClassModel().getCurrType());
		billCardPanel.getHeadItem(WaClassVO.TAXCURRID).setValue(
				getWaClassModel().getCurrType());

		billCardPanel.getHeadItem(WaClassVO.STARTYEARPERIOD).setValue(
				getWaClassModel().getCurrentYearMonth());

		billCardPanel.getHeadItem(WaClassVO.SHOWFLAG).setValue(UFBoolean.TRUE);
		billCardPanel.getHeadItem(WaClassVO.ISAPPORVE).setValue(UFBoolean.TRUE);


		//设置国家地区参照默认值
		UIRefPane refPane = (UIRefPane)getBillCardPanel().getHeadItem("pk_country").getComponent();
		HRGlobalCountryRefModel refModel = (HRGlobalCountryRefModel)refPane.getRefModel();
		Vector data = refModel.getData();
		if(null!=data&&data.size()==1){//如果仅有一个值则设为默认值
			refModel.setSelectedData(data);//matchData(field, value)matchPkData(refModel.getPkValue());
			getBillCardPanel().getHeadItem("pk_country").setValue(refModel.getPkValue());
		}


	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nc.ui.uif2.editor.BillForm#handleEvent(nc.ui.uif2.AppEvent)
	 */
	@Override
	public void handleEvent(AppEvent event) {
		if (AppEventConst.MODEL_INITIALIZED.equalsIgnoreCase(event.getType())) {
			BillPanelUtils.setPkorgToRefModel(this
					.getBillCardPanel(), this.getModel().getContext()
					.getPk_org());
		}

		super.handleEvent(event);
		if (HrAppEventConst.SHOW_FORM == event.getType()) {
			synchronizeDataFromModel();
			this.showMeUp();
		}

		updateStatus();

	}

	private void updateStatus() {

		getBillCardPanel().getBodyPanel().getTable().setEnabled(isPanelEnable());
	}

	private boolean isPanelEnable(){

		if(	getModel().getUiState() != UIState.EDIT && getModel().getUiState() != UIState.ADD){
			return false;
		}

		return true;

	}

	@Override
	protected void onAdd() {
		super.onAdd();
		showMeUp();
		setUIEnable(UFBoolean.TRUE);

		if (((WaClassModel) getModel()).isCopying()) {
			setCopyValue();
		}else{
			// 新增状态

			BillItem billItem2 = getBillCardPanel().getHeadItem(
					WaClassVO.STARTYEARPERIOD);
			BillPanelUtils.initComboBox(billItem2,  new String[0],
					Boolean.FALSE);
			waRangeSetBillItems(new BillItem[0]);
		}

		BillItem billItem2 = getBillCardPanel().getHeadItem(
				WaClassVO.IDISPLAYORDER);
		if(getModel().getContext().getNodeType() ==NODE_TYPE.GROUP_NODE ){
			billItem2.setValue(0);

		}else{
			billItem2.setValue(1);
		}
	}

	@Override
	protected void onEdit() {
		super.onEdit();
		showMeUp();
		resetWaRange();
		// 设置界面控件起始年月与封存年月不可编辑
		setUIEnable(UFBoolean.FALSE);
		//根据方案状态设置 界面期间方案设否可以编辑：起始年度有已经审核的数据，则不允许修改期间方案
		WaClassVO selectedData = getSelectedWaclass();
		if(selectedData!=null){

			if(hasBusinessData(selectedData)){
				this.getBillCardPanel().getHeadItem(WaClassVO.PK_PERIODSCHEME).setEnabled(false);
				this.getBillCardPanel().getHeadItem(WaClassVO.STARTYEARPERIOD).setEnabled(false);
				this.getBillCardPanel().getHeadItem(WaClassVO.NAME).setEnabled(false);
			}else{
				this.getBillCardPanel().getHeadItem(WaClassVO.PK_PERIODSCHEME).setEnabled(true);
				this.getBillCardPanel().getHeadItem(WaClassVO.STARTYEARPERIOD).setEnabled(true);
				this.getBillCardPanel().getHeadItem(WaClassVO.NAME).setEnabled(true);
			}
		}

		//币种一旦确定就不能修改
		String 	currid = billCardPanel.getHeadItem(WaClassVO.CURRID).getValue();
		String  taxcurrid = billCardPanel.getHeadItem(WaClassVO.TAXCURRID).getValue();
		if(StringUtils.isBlank(currid)){
			this.getBillCardPanel().getHeadItem(WaClassVO.CURRID).setEnabled(true);

		}else{
			this.getBillCardPanel().getHeadItem(WaClassVO.CURRID).setEnabled(false);
		}

		if(StringUtils.isBlank(taxcurrid)){
			this.getBillCardPanel().getHeadItem(WaClassVO.TAXCURRID).setEnabled(true);

		}else{
			this.getBillCardPanel().getHeadItem(WaClassVO.TAXCURRID).setEnabled(false);
		}



		if (!isGroupNode())
		{
			if(selectedData.getPk_group().equals(selectedData.getPk_org())){
				// 组织修改集团分配方案时只能修改计薪规则
				this.getBillCardPanel().getHeadItem(WaClassVO.PK_PERIODSCHEME).setEnabled(false);
				this.getBillCardPanel().getHeadItem(WaClassVO.STARTYEARPERIOD).setEnabled(false);
				this.getBillCardPanel().getHeadItem(WaClassVO.NAME).setEnabled(false);
				this.getBillCardPanel().getHeadItem(WaClassVO.CODE).setEnabled(false);
				this.getBillCardPanel().getHeadItem(WaClassVO.TAXCURRID).setEnabled(false);
				this.getBillCardPanel().getHeadItem(WaClassVO.CURRID).setEnabled(false);
				this.getBillCardPanel().getHeadItem(WaClassVO.VOUCHERFLAG).setEnabled(false);
				this.getBillCardPanel().getHeadItem(WaClassVO.SHOWFLAG).setEnabled(false);
				this.getBillCardPanel().getHeadItem(WaClassVO.DISPLAYTAXITEM).setEnabled(false);
				this.getBillCardPanel().getHeadItem("taxmode").setEnabled(false);
			}
			//组织下修改汇总时，一些字段是不可以修改的
			selectedData = getSelectedWaclass();
			if(WaLoginVOHelper.isCollectClass(selectedData))
			{
				BillItem billItem = this.getBillCardPanel().getHeadItem(
						WaClassVO.CODE);
				billItem.setEnabled(false);
				BillItem nameBillItem = this.getBillCardPanel().getHeadItem(
						WaClassVO.NAME);
				nameBillItem.setEnabled(false);
				// BillItem vouBillItem = this.getBillCardPanel().getHeadItem(
				// WaClassVO.VOUCHERFLAG);
				// vouBillItem.setEnabled(false);
				billItem = this.getBillCardPanel().getHeadItem(
						WaClassVO.PK_PERIODSCHEME);
				billItem.setEnabled(true);
				billItem = this.getBillCardPanel().getHeadItem(
						WaClassVO.STARTYEARPERIOD);
				billItem.setEnabled(true);
			}
		}
	}


	private boolean hasBusinessData(WaClassVO vo){
		try {
			//业务期间与起始期间不一样，则已经有业务数据。对于集团的则看是否已经分配 。已经分配，则不允许修改方案

			if (vo.getCyear() == null || vo.getCperiod() == null) {
				return false;
			} else if (!vo.getCyear().equals(vo.getStartyear())
					|| !vo.getCperiod().equals(vo.getStartperiod())) {
				return true;
			}else{
				//到数据库中核查是否有业务数据
				if(isGroupNode()){
					//集团登录，已经分配则认为有业务数据
					IWaClass waclass = NCLocator.getInstance().lookup(IWaClass.class);
					return 	waclass.groupClsHasAssigned(vo);
				}else{
					//组织节点，查看起始期间是否有业务数据
					IWaClass waclass = NCLocator.getInstance().lookup(IWaClass.class);
					return waclass.WaClsHasBusinessData(vo);
				}

			}
		} catch (BusinessException be) {
			Logger.error(be.getMessage(), be);
			return true;
		}catch (Exception e) {

			Logger.error(e.getMessage(), e);
			return true;
		}



	}

	/**
	 * 设置 不允许修改的项目：
	 * 起始期间、多次发放标识
	 * @param b
	 */
	private void setUIEnable(UFBoolean b) {
		getBillCardPanel().getHeadItem(WaClassVO.STARTYEARPERIOD).setEnabled(
				b.booleanValue());
// {MOD:新个税补丁}
// begin
		//20160127 shenliangc 薪资方案编辑态不准修改“多次发放”标志！
		if(getBillCardPanel().getHeadItem(WaClassVO.MUTIPLEFLAG) != null){
			getBillCardPanel().getHeadItem(WaClassVO.MUTIPLEFLAG).setEnabled(
					b.booleanValue());
		}
// end
		if (isGroupNode()) {
			BillItem billItem = this.getBillCardPanel().getHeadItem(
					WaClassVO.PK_ORG);
			billItem.setEnabled(false);
		}




	}


	/**
	 * 设置copy过来的数据
	 *
	 * @author xuanlt on 2010-1-22
	 * @return void
	 */
	private void setCopyValue() {

		WaClassVO selectedData = (WaClassVO) getSelectedWaclass().clone();
		if (selectedData != null) {
			selectedData.setPk_wa_class(null);
			selectedData.setCode(null);
			selectedData.setPk_org(getModel().getContext().getPk_org());
			selectedData.setPk_group(getModel().getContext().getPk_group());
			selectedData.setLeaveflag(UFBoolean.FALSE);
			selectedData.setStopflag(UFBoolean.FALSE);
			// if(!WaLoginVOHelper.isParentClass(selectedData)){
			selectedData.setMutipleflag(UFBoolean.FALSE);
			// }
			//复制时将起始期间赋值为最新期间
			selectedData.setStartyear(selectedData.getCyear());
			selectedData.setStartperiod(selectedData.getCperiod());
			selectedData.setCreator(null);
			selectedData.setCreationtime(null);
			selectedData.setModifier(null);
			selectedData.setModifiedtime(null);
		}
		setValue(selectedData);
	}

	@Override
	public Object getValue() {
		Object obj = super.getValue();
		WaClassVO waClassVO = ((WaClassVO) obj);
		if (waClassVO != null) {
			// 设置计薪规则
			String[] rangeRule = ((UIRefPane) getBillCardPanel().getHeadItem(
					WaClassVO.RANGERULE).getComponent()).getRefPKs();
			waClassVO.setWaPsnhiVOs(constructRangeRule(rangeRule));

			// 设置计薪新范围
			waClassVO.setWaPsnhiBVOs(getWaPsnhiBVOs(waClassVO));

			//设置财务组织-总账关账使用
			String[] wafiorgpks = ((UIRefPane) getBillCardPanel().getHeadItem(
					WaClassVO.WAFIORG).getComponent()).getRefPKs();
			waClassVO.setWaclassFiorgvo(constructFiOrgvo(wafiorgpks));

		}
		return obj;
	}

	/**
	 * 需要创新
	 *
	 * @author xuanlt on 2010-4-14
	 * @return
	 * @return WaPsnhiBVO[]
	 */
	private WaPsnhiBVO[] getWaPsnhiBVOs(WaClassVO vo) {


		WaPsnhiVO[] psnhivos = vo.getWaPsnhiVOs();
		BillModel billmodel = getBillCardPanel().getBodyPanel().getTableModel();
		CircularlyAccessibleValueObject[] vos = billmodel.getBodyValueVOs(GeneralVO.class.getName());

		ArrayList<WaPsnhiBVO> list   =new ArrayList<WaPsnhiBVO>();
		for (int i = 0; i < vos.length; i++) {
			//拆分成 wapsnhibvo
			for (int j = 0; j < psnhivos.length; j++) {
				WaPsnhiBVO waPsnhiBVO = new WaPsnhiBVO();
				waPsnhiBVO.setStatus(VOStatus.NEW);
				waPsnhiBVO.setSortgroup(String.valueOf(i));
				waPsnhiBVO.setPk_wa_psnhi(psnhivos[j].getPk_flddict());

				if (vos[i].getAttributeValue(psnhivos[j].getPk_flddict()) != null) {
					waPsnhiBVO.setVfldvalue(vos[i].getAttributeValue(psnhivos[j].getPk_flddict()).toString());
				}

				list.add(waPsnhiBVO);
			}
		}
		WaPsnhiBVO [] tempvos =  new WaPsnhiBVO [list.size()];
		return list.toArray(tempvos );


	}



	private WaPsnhiVO[] constructRangeRule(String[] rangeRule) {
		if (ArrayUtils.isEmpty(rangeRule)) {
			return new WaPsnhiVO[0];
		}
		WaPsnhiVO[] vos = new WaPsnhiVO[rangeRule.length];
		for (int index = 0; index < rangeRule.length; index++) {
			vos[index] = new WaPsnhiVO();
			vos[index].setPk_flddict(rangeRule[index]);
			vos[index].setShoworder(index);

		}

		return vos;

	}

	private WaFiorgVO[] constructFiOrgvo(String[] fiorgpks) {
		if (ArrayUtils.isEmpty(fiorgpks)) {
			return new WaFiorgVO[0];
		}
		WaFiorgVO[] vos = new WaFiorgVO[fiorgpks.length];
		for (int index = 0; index < fiorgpks.length; index++) {
			vos[index] = new WaFiorgVO();
			vos[index].setPk_financeorg(fiorgpks[index]);


		}

		return vos;

	}



	public WaClassVO getSelectedWaclass() {
		Object selectedData = getModel().getSelectedData();
		return ((WaClassVO) selectedData);
	}

	public List<Action> getTabActions() {
		return tabActions;
	}

	public void setTabActions(List<Action> tabActions) {
		this.tabActions = tabActions;
	}

	@Override
	public void initUI() {
		super.initUI();

		UIRefPane fiOrgPane = (UIRefPane)this.getBillCardPanel().getHeadItem(WaClassVO.WAFIORG).getComponent();
		fiOrgPane.setMultiSelectedEnabled(true);

		//设置薪资期间方案参照
		UIRefPane schemeRefPane = (UIRefPane)this.getBillCardPanel().getHeadItem(WaClassVO.PK_PERIODSCHEME).getComponent();
		WaPeriodChemeRefModel schemeRefModel = (WaPeriodChemeRefModel)schemeRefPane.getRefModel();
		schemeRefModel.setContext(getModel().getContext());

		// 设置计薪人员规则多选
		WaClassRangeRef refmodel = new WaClassRangeRef();
		UIRefPane refPane = (UIRefPane) this.getBillCardPanel().getHeadItem(
				WaClassVO.RANGERULE).getComponent();
		refPane.setRefModel(refmodel);
		refPane.setMultiSelectedEnabled(true);

		// 展现计薪范围表结构
		this.getBillCardPanel().addTabAction(IBillItem.BODY, tabActions);
		this.getBillCardPanel().addEditListener(this);

		waRangeSetBillItems(new BillItem[0]);

		//集团薪资方案不显示计薪规则
		if(isGroupNode()){
			this.getBillCardPanel().getHeadItem(WaClassVO.RANGERULE).setShow(false);
			this.getBillCardPanel().getHeadItem(WaClassVO.COLLECTFLAG).setShow(false);
			this.getBillCardPanel().getHeadItem(WaClassVO.MUTIPLEFLAG).setShow(false);
			this.getBillCardPanel().getHeadItem(WaClassVO.WAFIORG).setShow(false);
			this.getBillCardPanel().getHeadItem(WaClassVO.ISAPPORVE).setShow(false);
// {MOD:新个税补丁}
// begin

			this.getBillCardPanel().getHeadItem(WaClassVO.YEARBONUSFLAG).setShow(false);
// end
		}else{
			//组织
			this.getBillCardPanel().getHeadItem(WaClassVO.ADDFLAG).setShow(false);
		}
		//重新绘制！防止出现界面不美观
		this.getBillCardPanel().setBillData(this.getBillCardPanel().getBillData());


	}

	private boolean isGroupNode() {
		return getModel().getContext().getNodeType() == NODE_TYPE.GROUP_NODE;

	}

	public void setCopy(boolean isCopy) {
		this.isCopy = isCopy;
	}

	public boolean isCopy() {
		return isCopy;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void addTabbedPaneAwareComponentListener(
			ITabbedPaneAwareComponentListener l) {

		tabbedPaneAwareComponent.addTabbedPaneAwareComponentListener(l);
	}

	@Override
	public boolean canBeHidden() {
		//		if (closingListener != null) {
		//			return closingListener.canBeClosed();
		//		}
		//		return tabbedPaneAwareComponent.canBeHidden();

		if (this.getModel().getUiState() == UIState.ADD || this.getModel().getUiState() == UIState.EDIT) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean isComponentVisible() {
		return tabbedPaneAwareComponent.isComponentVisible();
	}

	@Override
	public void setComponentVisible(boolean visible) {
		tabbedPaneAwareComponent.setComponentVisible(visible);
	}

	@Override
	public void setAutoShowUpEventListener(IAutoShowUpEventListener l) {
		autoShowUpComponent.setAutoShowUpEventListener(l);
	}

	public void setClosingListener(IFunNodeClosingListener closingListener) {
		this.closingListener = closingListener;
	}

	@Override
	public void showMeUp() {
		autoShowUpComponent.showMeUp();
	}

	@Override
	protected void onNotEdit() {
		// TODO Auto-generated method stub
		super.onNotEdit();
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		//
		// 重新设置薪资年月的下拉选项
		if (e.getKey().equals(WaClassVO.PK_PERIODSCHEME)) {
			BillItem billItem = getBillCardPanel().getHeadItem(
					WaClassVO.PK_PERIODSCHEME);
			BillItem billItem2 = getBillCardPanel().getHeadItem(
					WaClassVO.STARTYEARPERIOD);
			PeriodVO[] vos = new PeriodVO[0];
			if (billItem.getValueObject() != null) {
				vos = getWaClassModel().getWaPeiords(
						billItem.getValueObject().toString());
			}

			BillPanelUtils.initComboBox(billItem2, getAllPeriod(vos),
					Boolean.FALSE);
			// 根据服务器日期设置默认年月 （起始年、起始月、业务年、业务月）
			setStartYandM();
		} else if (e.getKey().equals(WaClassVO.STARTYEARPERIOD)) {
			setWaYearMonth();
		}

		else if (e.getKey().equals(WaClassVO.CURRID)) {
			// 计税币种默认同方案币种
			setTaxCurr();
		} else if (WaClassVO.RANGERULE.equals(e.getKey())) {

			// 重新设置计薪范围
			if (UIDialog.ID_OK != MessageDialog.showOkCancelDlg(getModel()
					.getContext().getEntranceUI(), null,
					ResHelper.getString("60130waclass","060130waclass0057")/*@res "需要重置计薪范围,会导致原有的计薪范围丢失，确定要继续吗？"*/)) {
				BillItem billItem = getBillCardPanel().getHeadItem(
						WaClassVO.RANGERULE);
				billItem.setValue(e.getOldValue());
				return;
			}

			resetWaRange();
			getBillCardPanel().getBillData().setBodyValueVO(null);
		}

	}

	/**
	 *
	 * @author xuanlt on 2010-4-15
	 * @param vos
	 * @return void
	 */
	private void seWaRange(WaClassVO vo ) {
		/**
		 * 不能使用billitem ，因为 有参照
		 */
		WaPsnhiBVO[] vos = vo.getWaPsnhiBVOs();
		if (ArrayUtils.isEmpty(vos)) {
			return;
		}

		int columnnum =0;
		WaPsnhiVO[] psnhivos = vo.getWaPsnhiVOs();
		if(!ArrayUtils.isEmpty(psnhivos)){
			columnnum = psnhivos.length;
		}
		//得到最大的sortgroup
		int num = 0;
		for (int index = 0; index < vos.length; index++) {
			int sortgroup = Integer.parseInt(vos[index].getSortgroup());
			if(sortgroup>num){
				num = sortgroup;
			}
		}

		GeneralVO[] gvos = new GeneralVO[num+1];
		/**
		 * vos 必须按照 sortgroup排序 sortGroup 应该是整数型
		 */
		for (int i = 0; i < vos.length; i++) {
			int rownum = Integer.parseInt(vos[i].getSortgroup());
			if (gvos[rownum] == null) {
				gvos[rownum] = new GeneralVO();
			}

			gvos[rownum].setAttributeValue(vos[i].getPk_wa_psnhi(),vos[i].getVfldvalue());
		}

		getBillCardPanel().getBillData().setBodyValueVO(gvos);
		BillPanelUtils.dealWithRefShowNameByPk(getBillCardPanel());

	}


	private void resetWaRange() {
		BillItem item2 = this.getBillCardPanel().getHeadItem(
				WaClassVO.RANGERULE);
		Vector<Vector> v = ((UIRefPane) item2.getComponent()).getRefModel()
				.getSelectedData();
		resetModel(v);
	}

	private void resetModel(Vector<Vector> vec) {

		BillItem[] biaBody = null;
		if (vec==null || vec.isEmpty()) {
			biaBody = new BillItem[0];

		}else{
			biaBody = new BillItem[vec.size()];

			for (int index = 0; index < vec.size(); index++) {
				Vector v = vec.get(index);
				biaBody[index] =  getDefaultBillItem();

				// FIX ME
				biaBody[index].setName(v.get(0).toString());

				//利用 pk_wa_psnhi做 key
				biaBody[index].setKey(v.get(3).toString());

				if(v.get(2)!=null){
					UIRefPane ref  = (UIRefPane)biaBody[index].getComponent();
					ref.setButtonFireEvent(true);
					ref.setReturnCode(false);
					biaBody[index].setComponent(ref);
					AbstractRefModel refmodel = getRefModel(v.get(2).toString());
					refmodel.setPk_org(getModel().getContext().getPk_org());
					ref.setRefModel(refmodel);
				}else{
					biaBody[index].setDataType(BillItem.STRING);
				}



			}
		}
		waRangeSetBillItems(biaBody);

	}

	private void setModel(WaPsnhiVO[] vos) {
		BillItem[] biaBody =null;

		if (ArrayUtils.isEmpty(vos)) {
			biaBody  = new BillItem[0];

		}else{
			biaBody = new BillItem[vos.length];

			for (int index = 0; index < vos.length; index++) {
				WaPsnhiVO v = vos[index];
				biaBody[index] =  getDefaultBillItem();

				// FIX ME
				biaBody[index].setName(v.getVfldname());
				biaBody[index].setKey(v.getPk_flddict());


				AbstractRefModel model = getRefModel(v);
				if(model==null){
					biaBody[index].setDataType(BillItem.STRING);
				}else{
					UIRefPane ref  = (UIRefPane)biaBody[index].getComponent();
					ref.setRefModel(getRefModel(v));
					ref.setButtonFireEvent(true);
					ref.setReturnCode(false);
					biaBody[index].setComponent(ref);
				}


			}
		}
		waRangeSetBillItems(biaBody);

	}

	/**
	 * 为计薪范围表设置 billitems
	 *
	 * @author xuanlt on 2010-4-20
	 * @param items
	 * @return  void
	 */
	private void waRangeSetBillItems(BillItem[] items ){

		getBillCardPanel().getBillData().setBodyItems(items);
		//getBillCardPanel().getBillData().setBodyValueVO(null);
		BillPanelUtils.dealWithRefField(getBillCardPanel());
		//清空数据
		//20151223 shenliangc NCdp205564067 薪资未屏蔽右键菜单和表格中回车加行的功能的节点
		BillPanelUtils.disabledRightMenuAndAutoAddLine(getBillCardPanel().getBodyPanel());
	}

	private BillItem getDefaultBillItem(){
		BillItem item = new BillItem();
		item.setWidth(100);
		item.setEnabled(true);
		item.setEdit(true);
		item.setDataType(BillItem.UFREF);
		item.setTatol(false);
		item.setNull(true);
		return item;
	}


	private AbstractRefModel getRefModel (WaPsnhiVO vo){

		return getRefModel(vo.getRefmodel());


	}

	private AbstractRefModel getRefModel (String  refmodelName){
		AbstractRefModel model = null;
		try {
			if(!StringUtils.isBlank(refmodelName)){
				//FIXME nc.ui.bd.ref.model.PsnclDefaultRefModel  没有默认构造函数,需要UAP协商
				model = (AbstractRefModel)Class.forName(refmodelName.replace("<", "").replace(">", "")).newInstance();
			}

		} catch (Exception e) {
			Logger.error(e.getMessage(),e);

		}

		return model;

	}


	/**
	 *
	 * @author xuanlt on 2010-4-19
	 * @return  void
	 */

	private void setStartYandM() {
		BillItem billItem2 = getBillCardPanel().getHeadItem(
				WaClassVO.STARTYEARPERIOD);
		billItem2.setValue(getCorespondingPriod());
		setWaYearMonth();
	}

	private void setWaYearMonth() {
		// 根据起始年月设置 起始年、起始月 、 业务年 、 业务月
		BillItem startyearperiod = getBillCardPanel().getHeadItem(
				WaClassVO.STARTYEARPERIOD);
		BillItem cyearItem = getBillCardPanel().getHeadItem(WaClassVO.CYEAR);
		BillItem cperiodItem = getBillCardPanel().getHeadItem(WaClassVO.CPERIOD);
		BillItem startyearItem = getBillCardPanel().getHeadItem(WaClassVO.STARTYEAR);
		BillItem startperiodItem = getBillCardPanel().getHeadItem(WaClassVO.STARTPERIOD);
		if (startyearperiod.getValueObject() != null) {
			String startYandM = startyearperiod.getValueObject().toString();

			startyearItem.setValue(startYandM.substring(0, 4));
			startperiodItem.setValue(startYandM.substring(4));
			cyearItem.setValue(startYandM.substring(0, 4));
			cperiodItem.setValue(startYandM.substring(4));

		}else{
			startyearItem.setValue(null);
			startperiodItem.setValue(null);
			cyearItem.setValue(null);
			cperiodItem.setValue(null);
		}
	}

	private void setTaxCurr() {
		BillItem taxcurrid = getBillCardPanel()
				.getHeadItem(WaClassVO.TAXCURRID);
		BillItem currid = getBillCardPanel().getHeadItem(WaClassVO.CURRID);
		taxcurrid.setValue(currid.getValueObject());

	}

	private String getCurrentDate() {
		// 获得当前选择的登陆日期 。undo
		UFDate serverDate = PubEnv.getServerDate();
		String year  = String.valueOf(serverDate.getYear());
		String monthe = String.valueOf(serverDate.getMonth());
		if(monthe.length()==1){
			monthe = "0" + monthe;
		}

		return year+ monthe;
	}

	/**
	 * 得到当前日期所
	 * @return
	 */
	private String getCorespondingPriod(){

		UFLiteralDate serverDate = PubEnv.getServerLiteralDate();
		BillItem billItem = getBillCardPanel().getHeadItem(
				WaClassVO.PK_PERIODSCHEME);

		PeriodVO[]  vos = new PeriodVO[0];
		if(billItem.getValue()!=null){
			vos = getWaClassModel().getWaPeiords(billItem.getValue());
		}

		for (int i = 0; i < vos.length; i++) {
			if(vos[i].getCstartdate().compareTo(serverDate)<=0 && vos[i].getCenddate().compareTo(serverDate)>=0){
				return vos[i].getCyear()+ vos[i].getCperiod();
			}
		}

		return getCurrentDate();
	}


	@Override
	public void setValue(Object object) {

		super.setValue(object);

		if (object != null) {
			// 根据薪资期间表，更改薪资期间的下拉选项
			BillItem billItem = getBillCardPanel().getHeadItem(
					WaClassVO.STARTYEARPERIOD);
			BillPanelUtils
			.initComboBox(billItem, getAllPeriod(), Boolean.FALSE);
			String yearAndPriod = ((WaClassVO) object).getStartyearperiod();
			billItem.setValue(yearAndPriod);

			// 设置 计薪规则与计薪范围
			WaClassVO vo = (WaClassVO) object;

			if (ArrayUtils.isEmpty(vo.getWaPsnhiVOs())) {
				getBillCardPanel().getHeadItem(WaClassVO.RANGERULE).setValue(
						null);

			} else {
				((UIRefPane) getBillCardPanel()
						.getHeadItem(WaClassVO.RANGERULE).getComponent())
						.setPKs(vo.getRangeRulePK());
			}

			// 初始化计薪范围表结构
			setModel(vo.getWaPsnhiVOs());
			// 初始化计薪范围数据
			seWaRange(vo);

			//设置财务组织
			if (ArrayUtils.isEmpty(vo.getWaclassFiorgvo())) {
				getBillCardPanel().getHeadItem(WaClassVO.WAFIORG).setValue(
						null);

			} else {
				((UIRefPane) getBillCardPanel()
						.getHeadItem(WaClassVO.WAFIORG).getComponent())
						.setPKs(vo.getWaFiorgPKs());
			}

		}
	}

	public String[] getAllPeriod() {
		BillItem billItem = getBillCardPanel().getHeadItem(
				WaClassVO.PK_PERIODSCHEME);
		PeriodVO[]  vos = new PeriodVO[0];
		if(billItem.getValue()!=null){
			vos = getWaClassModel().getWaPeiords(billItem.getValue());
		}

		return getAllPeriod(vos);
	}

	public String[] getAllPeriod(PeriodVO[] vos) {
		if (vos == null) {
			return new String[0];
		}
		String[] objValues = new String[vos.length];
		for (int i = 0; i < vos.length; i++) {
			objValues[i] = vos[i].getCyear() + vos[i].getCperiod();
		}
		return objValues;
	}

	private WaClassModel getWaClassModel() {
		return (WaClassModel) super.getModel();
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
		// 实现 BillEditListener 。但是不做任何事情

	}
}