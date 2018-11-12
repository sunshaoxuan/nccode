package nc.ui.overtime.otleavebalance.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.itf.ta.overtime.IOTLeaveBalanceMaintain;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.ta.overtime.AggOTLeaveBalanceVO;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class OTLeaveBalanceMaintainProxy implements IDataOperationService, IQueryService,
	ISingleBillService<AggOTLeaveBalanceVO> {
    @Override
    public IBill[] insert(IBill[] value) throws BusinessException {
	IOTLeaveBalanceMaintain operator = NCLocator.getInstance().lookup(IOTLeaveBalanceMaintain.class);
	AggOTLeaveBalanceVO[] vos = operator.insert((AggOTLeaveBalanceVO[]) value);
	return vos;
    }

    @Override
    public IBill[] update(IBill[] value) throws BusinessException {
	IOTLeaveBalanceMaintain operator = NCLocator.getInstance().lookup(IOTLeaveBalanceMaintain.class);
	AggOTLeaveBalanceVO[] vos = operator.update((AggOTLeaveBalanceVO[]) value);
	return vos;
    }

    @Override
    public IBill[] delete(IBill[] value) throws BusinessException {
	// 目前的删除并不是走这个方法，由于pubapp不支持从这个服务中执行删除操作
	// 单据的删除实际上使用的是：ISingleBillService<AggSingleBill>的operateBill
	IOTLeaveBalanceMaintain operator = NCLocator.getInstance().lookup(IOTLeaveBalanceMaintain.class);
	operator.delete((AggOTLeaveBalanceVO[]) value);
	return value;
    }

    @Override
    public AggOTLeaveBalanceVO operateBill(AggOTLeaveBalanceVO bill) throws Exception {
	IOTLeaveBalanceMaintain operator = NCLocator.getInstance().lookup(IOTLeaveBalanceMaintain.class);
	operator.delete(new AggOTLeaveBalanceVO[] { bill });
	return bill;
    }

    @Override
    public Object[] queryByQueryScheme(IQueryScheme queryScheme) throws Exception {
	IOTLeaveBalanceMaintain query = NCLocator.getInstance().lookup(IOTLeaveBalanceMaintain.class);
	return query.query(queryScheme);
    }

}
