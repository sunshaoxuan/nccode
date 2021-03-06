package nc.ui.ta.leave.balance.action;

import java.awt.event.ActionEvent;

import nc.ui.hr.uif2.action.QueryAction;
import nc.ui.querytemplate.queryarea.IQueryExecutor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.ta.leave.balance.model.LeaveBalanceAppModel;
import nc.ui.ta.leave.balance.model.LeaveBalanceModelDataManager;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;

import org.apache.commons.lang.StringUtils;


/**
 * @author 
 *
 */
@SuppressWarnings("serial")
public class QueryLeaveBalanceAction extends QueryAction {

	private AbstractAppModel hierachicalModel;
	private IQueryExecutor queryExecutor;
	
	protected void executeQuery(FromWhereSQL sqlWhere) {
		((LeaveBalanceModelDataManager)getDataManager()).initModelByFromWhere(sqlWhere);
		queryExcuted=true;
	}
	

	public class LeaveQueryExecutor implements IQueryExecutor {
		@Override
		public void doQuery(IQueryScheme queryScheme) {
			FromWhereSQL sqlWhere = queryScheme.getTableJoinFromWhereSQL();
			executeQuery(sqlWhere);
		}
	}
	
	@Override
	public void doAction(ActionEvent evt) throws Exception {
		try{
			getQueryDelegator().doQuery(getQueryExecutor());
		} finally {
			setStatusBarMsg();
			queryExcuted = false;
		}
	}

	public IQueryExecutor getQueryExecutor() {
		if(queryExecutor == null) {
			queryExecutor = new LeaveQueryExecutor();
		}
		return queryExecutor;
	}

	@Override
	protected boolean isActionEnable() {
		boolean enable =  super.isActionEnable();
		if(enable)
		{
			enable = StringUtils.isNotBlank(getModel().getContext().getPk_org());
		}
		if(enable&&getHierachicalModel()!=null)
		{
			Object obj = getHierachicalModel().getSelectedData();
			enable = obj==null?false:true;
			if(enable)
			{
				LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO)obj;
				Integer leavesetperiod = typeVO.getLeavesetperiod();
//				boolean isYear = leavesetperiod!=null&&(Integer)SettlementPeriodEnum.YEAR.value()==leavesetperiod.intValue();
				boolean isYear = leavesetperiod != null
						&& (TimeItemCopyVO.LEAVESETPERIOD_YEAR == leavesetperiod
								|| TimeItemCopyVO.LEAVESETPERIOD_DATE == leavesetperiod
						// ssx added on 2018-03-16
						// for changes of start date of company age
						|| TimeItemCopyVO.LEAVESETPERIOD_STARTDATE == leavesetperiod
						//
						);
				if(!isYear)
				{
					String month = ((LeaveBalanceAppModel)getModel()).getMonth();
					enable = (month==null||month.equals(""))?false:true;
				}
			}
		}
		return enable;
	}

	public AbstractAppModel getHierachicalModel() {
		return hierachicalModel;
	}

	public void setHierachicalModel(AbstractAppModel hierachicalModel) {
		this.hierachicalModel = hierachicalModel;
	}
	
}
