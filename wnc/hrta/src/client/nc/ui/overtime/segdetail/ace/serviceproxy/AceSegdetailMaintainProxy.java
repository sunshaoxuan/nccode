package nc.ui.overtime.segdetail.ace.serviceproxy;

import nc.bs.framework.common.NCLocator;
import nc.ui.pubapp.pub.task.ISingleBillService;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.pubapp.uif2app.actions.IDataOperationService;
import nc.ui.pubapp.uif2app.query2.model.IQueryService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.overtime.AggSegDetailVO;
import nc.itf.ta.overtime.ISegdetailMaintain;

/**
 * 示例单据的操作代理
 * 
 * @author author
 * @version tempProject version
 */
public class AceSegdetailMaintainProxy implements IDataOperationService,
		IQueryService ,ISingleBillService<AggSegDetailVO>{
	@Override
	public IBill[] insert(IBill[] value) throws BusinessException {
		ISegdetailMaintain operator = NCLocator.getInstance().lookup(
				ISegdetailMaintain.class);
		AggSegDetailVO[] vos = operator.insert((AggSegDetailVO[]) value);
		return vos;
	}

	@Override
	public IBill[] update(IBill[] value) throws BusinessException {
		ISegdetailMaintain operator = NCLocator.getInstance().lookup(
				ISegdetailMaintain.class);
		AggSegDetailVO[] vos = operator.update((AggSegDetailVO[]) value);
		return vos;
	}

	@Override
	public IBill[] delete(IBill[] value) throws BusinessException {
		// 目前的删除并不是走这个方法，由于pubapp不支持从这个服务中执行删除操作
		// 单据的删除实际上使用的是：ISingleBillService<AggSingleBill>的operateBill
		ISegdetailMaintain operator = NCLocator.getInstance().lookup(
				ISegdetailMaintain.class);
		operator.delete((AggSegDetailVO[]) value);
		return value;
	}
	
	@Override
	public AggSegDetailVO operateBill(AggSegDetailVO bill) throws Exception {
		ISegdetailMaintain operator = NCLocator.getInstance().lookup(
				ISegdetailMaintain.class);
		operator.delete(new AggSegDetailVO[] { bill });
		return bill;
	}

	@Override
	public Object[] queryByQueryScheme(IQueryScheme queryScheme)
			throws Exception {
		ISegdetailMaintain query = NCLocator.getInstance().lookup(
				ISegdetailMaintain.class);
		return query.query(queryScheme);
	}

}
