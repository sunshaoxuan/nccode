package nc.ui.ta.leave.balance.view;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.hr.utils.ResHelper;
import nc.itf.ta.IPeriodQueryService;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.ta.leave.balance.model.LeaveBalanceAppModel;
import nc.ui.ta.leave.balance.model.LeaveBalanceModelDataManager;
import nc.ui.ta.pub.view.TAParamOrgPanel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.util.remotecallcombination.IRemoteCallCombinatorService;
import nc.vo.util.remotecallcombination.RemoteCallInfo;
import nc.vo.util.remotecallcombination.RemoteCallResult;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 假期计算 组织panel 含年度期间
 * @author yucheng
 *
 */
@SuppressWarnings("serial")
public class LeaveBalanceTopPanel extends TAParamOrgPanel implements ActionListener{


	//年度
	private UILabel yearLabel=null;
	private UIComboBox yearComboBox=null;

	//期间
	private UILabel periodLabel=null;
	private UIComboBox periodComboBox=null;
	
	//是否显示转移时长
	private UICheckBox showTransfer;

	private AbstractAppModel hierachicalModel;
	private Object []  periods;
	private  String  lastorg;

	/**
	 * 初始化界面
	 */
	@Override
	public void initUI(){
		setLayout(new FlowLayout(FlowLayout.LEFT,20,5));
//		if(super.isComponentDisplayable()){
			add(getLabel());
			add(getRefPane());
//			getLabel().setLabelFor(getRefPane().getUITextField());
//			getLabel().setDisplayedMnemonic('O');
//			if(getLabel().getText().indexOf("O")==-1)
//			{
//				getLabel().setText(getLabel().getText()+"(O)");
//			}
			initDefaultOrg();
//		}
		add(getYearLabel());
		add(getYearComboBox());

		add(getPeriodLabel());
		add(getPeriodComboBox());
		
		add(getShowTransfer());

		initYearComboBox();
		exceptionHandler = new DefaultExceptionHanler(this);
//		doYearChanged();
	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		super.valueChanged(event);
		String pk_org = getRefPane().getRefPK();
		if(event.getSource()==getRefPane()){
			getModel().getContext().setPk_org(pk_org);
			((LeaveBalanceModelDataManager)getDataManager()).initHierachicalModel();
			initYearComboBox();
		}
//		doYearChanged();
//		doMonthChanged();
	}

	public void doYearChanged(){
		LeaveBalanceAppModel model = ((LeaveBalanceAppModel)getModel());
		if(getYearComboBox().getSelectdItemValue()==null){
			model.setYear(null);
			model.setMonth(null);
			return;
		}
		String year = getYearComboBox().getSelectdItemValue().toString();
		((LeaveBalanceAppModel)getModel()).setYear(year);
		changedMonth(year);
	}

	public void doMonthChanged(){
		String month=null;
		if(getPeriodComboBox().getSelectdItemValue()!=null)
		{
			month = getPeriodComboBox().getSelectdItemValue().toString();
		}
		((LeaveBalanceAppModel)getModel()).setMonth(month);
		
	}

	/**
	 * 假期年度改变事件
	 */
	public void changedMonth(String strYear) {
		getPeriodComboBox().removeAllItems();
		getPeriodComboBox().addItems(getPeriodMap().get(strYear));
	}

	@Override
	public void handleEvent(AppEvent event) {

		if(AppEventConst.MODEL_INITIALIZED==event.getType())
		{
			getPeriodComboBox().setEnabled(!isYear());
			getYearComboBox().setEnabled(!isLactation());
			initYearComboBox();
		}

		if(AppEventConst.UISTATE_CHANGED==event.getType()){
			if(getModel().getUiState()==UIState.ADD||getModel().getUiState()==UIState.EDIT || getModel().getUiState() == UIState.DISABLE){
				getRefPane().setEnabled(false);
				getYearComboBox().setEnabled(false);
//				getPeriodComboBox().setEnabled(false);
			}else{
				getRefPane().setEnabled(true);
				getYearComboBox().setEnabled(true);
				getPeriodComboBox().setEnabled(isYear());
			}
			if(getModel().getUiState() == UIState.EDIT){
				getPeriodComboBox().setEnabled(false);
				getYearComboBox().setEnabled(false);
				getShowTransfer().setEnabled(false);
			}
		}
	}

	private boolean isYear()
	{
		boolean isYear =false;
		Object data = getHierachicalModel().getSelectedData();
		if (data != null) {
			LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO) data;
			Integer leavesetperiod = typeVO.getLeavesetperiod();
			// isYear =
			// leavesetperiod!=null&&(Integer)SettlementPeriodEnum.YEAR.value()==leavesetperiod.intValue();
			isYear = leavesetperiod != null
					&& (TimeItemCopyVO.LEAVESETPERIOD_YEAR == leavesetperiod
							|| TimeItemCopyVO.LEAVESETPERIOD_DATE == leavesetperiod
					// ssx added on 2018-03-16
					// for changes of start date of company age
					|| TimeItemCopyVO.LEAVESETPERIOD_STARTDATE == leavesetperiod
					//
					);
		}
		return isYear;
	}

	private boolean isLactation()
	{
		boolean isLactation =false;
		Object data = getHierachicalModel().getSelectedData();
		if(data!=null)
		{
			LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO)data;
			isLactation = typeVO.getIslactation()==null?false:typeVO.getIslactation().booleanValue();
		}
		return isLactation;
	}

	@Override
	public boolean isComponentDisplayable() {
		return true;
	}

	protected UILabel getYearLabel() {
		if(yearLabel==null){
			yearLabel=new UILabel();
			yearLabel.setText(ResHelper.getString("common","UC000-0001802")
/*@res "年度"*/);
		}
		return yearLabel;
	}

	public UIComboBox getYearComboBox() {
		if (yearComboBox == null) {
			yearComboBox = new nc.ui.pub.beans.UIComboBox();
			yearComboBox.setName("yearComboBox");
			yearComboBox.setLocation(80, 6);
			yearComboBox.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent e) {
					doYearChanged();
				}

			});
		}
		return yearComboBox;
	}

	@SuppressWarnings("unchecked")
	public void initYearComboBox(){
		try {
			//当前选择的年度，切换类别时，尽量保持原来的年度
			String selectedYear = (String)getYearComboBox().getSelectedItem();
			String selectedMonth = (String)getPeriodComboBox().getSelectedItem();
			Object[] objs=getPeriodInfos();
			Map<String, String[]> periodMap = (Map<String, String[]>) objs[0];
			PeriodVO periodVO = (PeriodVO) objs[1];
			this.setPeriodMap(periodMap);
			String[] years=null;
			if(periodMap!=null)
			{
				years = periodMap.keySet().toArray(new String[0]);
				Arrays.sort(years);
			}

			getYearComboBox().removeAllItems();
			Object data = getHierachicalModel().getSelectedData();
			boolean isDate = data != null
					&& (data instanceof LeaveTypeCopyVO)
					&& (((LeaveTypeCopyVO) data).getLeavesetperiod().intValue() == LeaveTypeCopyVO.LEAVESETPERIOD_DATE
					// ssx added on 2018-03-16
					// for changes of start date of company age
					|| ((LeaveTypeCopyVO) data).getLeavesetperiod().intValue() == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE);
			//
			//考虑到按入职日结算的，需要往前再推一年
			if(!ArrayUtils.isEmpty(years)&&isDate){
				String preYear = Integer.toString(Integer.parseInt(years[0])-1);
				getYearComboBox().addItem(preYear);
			}
			getYearComboBox().addItems(years);
			if(ArrayUtils.isEmpty(years)){
				getPeriodComboBox().removeAllItems();
				return;
			}
			// 如果没有当前期间
			if(periodVO==null){
				doYearChanged();
				changedMonth(selectedYear!=null?selectedYear:years[0]);
				doMonthChanged();
				return;
			}
			// 设置当前考勤年
			getYearComboBox().setSelectedItem(selectedYear!=null?selectedYear:periodVO.getTimeyear());
			doYearChanged();
			// 设置当前考勤期间
			getPeriodComboBox().setSelectedItem(selectedMonth!=null?selectedMonth:periodVO.getTimemonth());
			doMonthChanged();
		} catch (BusinessException e) {
			Logger.debug("初始化假期年度，期间失败!");
		}
	}

//	private Object[] getPeriodInfos() throws BusinessException{
//		return new Object[]{null,null};
//	}
	
	private Object[] getPeriodInfos() throws BusinessException{
		String pk_org=getModel().getContext().getPk_org();
		if(StringUtils.isEmpty(pk_org)){//如果组织为空则肯定没有年度和期间所一直直接返回null
			return new Object[]{null,null};
			}else{
				if(!ObjectUtils.equals(pk_org,lastorg)){//切换组织时候才查
					//查询期间
					List<RemoteCallInfo> remoteList = new ArrayList<RemoteCallInfo>();
					RemoteCallInfo queryPeriodRemote = new RemoteCallInfo();
					queryPeriodRemote.setClassName(IPeriodQueryService.class.getName());
					queryPeriodRemote.setMethodName("queryPeriodYearAndMonthByOrg");
					queryPeriodRemote.setParamTypes(new Class[]{String.class});
					queryPeriodRemote.setParams(new Object[]{getModel().getContext().getPk_org()});
					remoteList.add(queryPeriodRemote);
					
					RemoteCallInfo queryByDateRemote = new RemoteCallInfo();
					queryByDateRemote.setClassName(IPeriodQueryService.class.getName());
					queryByDateRemote.setMethodName("queryByDate");
					queryByDateRemote.setParamTypes(new Class[]{String.class, UFLiteralDate.class});
					queryByDateRemote.setParams(new Object[]{getModel().getContext().getPk_org(), new UFLiteralDate(WorkbenchEnvironment.getInstance().getBusiDate().toDate())});
					remoteList.add(queryByDateRemote);
					
//					String[] itfName = new String[]{IPeriodQueryService.class.getName(),IPeriodQueryService.class.getName()};
//					String[] methodNames = new String[]{"queryPeriodYearAndMonthByOrg","queryByDate"};
//					
//					Class[][] types = new Class[2][];
//					types[0] = new Class[]{String.class};
//					types[1] = new Class[]{String.class, UFLiteralDate.class};
//					Object[][] param = new Object[2][];
//					param[0] = new Object[]{getModel().getContext().getPk_org()};
//					param[1] = new Object[]{getModel().getContext().getPk_org(), new UFLiteralDate(WorkbenchEnvironment.getInstance().getBusiDate().toDate())};
//					periods=NCLocator.getInstance().lookup(IServiceHome.class).execute(itfName, methodNames, types, param);
					//打包执行
					List<RemoteCallResult> returnList = NCLocator.getInstance().lookup(IRemoteCallCombinatorService.class).doRemoteCall(remoteList);
					if(returnList.isEmpty())
						return null;
					RemoteCallResult[] returns = returnList.toArray(new RemoteCallResult[0]);
					periods = new Object[2];
					periods[0] = returns[0].getResult();
					periods[1] = returns[1].getResult();
					lastorg=pk_org;
				}
			}
		return periods;
	}
	

	private Map<String, String[]> periodMap;

	public Map<String, String[]> getPeriodMap() {
		return periodMap;
	}


	public void setPeriodMap(Map<String, String[]> periodMap) {
		this.periodMap = periodMap;
	}

	/**
	 * 取得会计年度
	 *
	 * @return：
	 */
	public String[] getAccYear() {
//		try {
//			AccountCalendar calendar = AccountCalendar.getInstance();
//			calendar.setDate(WorkbenchEnvironment.getInstance().getBusiDate());
//			AccperiodVO[] vos = calendar.getYearVOsOfCurrentScheme();
//			if(vos == null || vos.length < 1){
//				return null;
//			}
//			List<String> yearList = new ArrayList<String>();
//			for(int i = 0; i < vos.length; i++) {
//				yearList.add(vos[i].getPeriodyear());
//			}
//
//			return yearList.toArray(new String[0]);
//
//		} catch (Exception e) {
//            MessageDialog.showErrorDlg(this, null, "当前会计年度为空，可能是还未设置，请确认!");
//		}
		return null;
	}

	public UILabel getPeriodLabel() {
		if(periodLabel==null){
			periodLabel=new UILabel();
			periodLabel.setText(ResHelper.getString("common","UC000-0002560")
/*@res "期间"*/);
		}
		return periodLabel;
	}

	public UIComboBox getPeriodComboBox() {
		if (periodComboBox == null) {
			periodComboBox = new nc.ui.pub.beans.UIComboBox();
			periodComboBox.setName("periodComboBox");
			periodComboBox.setLocation(80, 6);
			periodComboBox.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent e) {
					doMonthChanged();
					//下面的工作是重新选择期间后进行刷新
					String year = ((LeaveBalanceAppModel)getModel()).getYear();
					String month = ((LeaveBalanceAppModel)getModel()).getMonth();
					if(StringUtils.isBlank(year)||StringUtils.isBlank(month))
						return;
					String curPeriod = year + month;
					LeaveBalanceVO selectedData = (LeaveBalanceVO) getModel().getSelectedData();
					if(selectedData==null)//为空时没法刷新，暂时没想到解决方案，若不判空则有可能进入死循环
						return;
					String selPeriod = selectedData.getCuryear() + selectedData.getCurmonth();
					if(selPeriod.compareTo(curPeriod)!=0){
						((LeaveBalanceModelDataManager)getDataManager()).refresh();
						return;
					}
				}

			});
		}
		return periodComboBox;
	}

	public AbstractAppModel getHierachicalModel() {
		return hierachicalModel;
	}

	public void setHierachicalModel(AbstractAppModel hierachicalModel) {
		this.hierachicalModel = hierachicalModel;
	}
	
	protected UICheckBox getShowTransfer() {
		if(showTransfer==null){
			showTransfer = new UICheckBox(ResHelper.getString("6017leave","06017leave0256")
					/*@res"显示转移时长"*/);
			showTransfer.addActionListener(this);
		}
		return showTransfer;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==getShowTransfer()){
			((LeaveBalanceAppModel)getModel()).setShowTransfer(getShowTransfer().isSelected());
		}
	}

}