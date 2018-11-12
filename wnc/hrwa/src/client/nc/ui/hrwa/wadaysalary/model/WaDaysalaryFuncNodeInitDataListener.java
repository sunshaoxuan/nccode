package nc.ui.hrwa.wadaysalary.model;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.funcnode.ui.FuncletInitData;
import nc.itf.hrwa.IWadaysalaryQueryService;
import nc.ui.pubapp.uif2app.model.DefaultFuncNodeInitDataListener;
import nc.ui.uif2.UIState;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.AggDaySalaryVO;
import nc.vo.wa.pub.WaDayLoginContext;
/**
 * 打开节点监听
 * @author ward
 *
 */
public class WaDaysalaryFuncNodeInitDataListener extends DefaultFuncNodeInitDataListener{
	@Override
	public void initData(FuncletInitData data) {
		// 如果一个节点已经打开并且正在编辑数据, 默认不再打开其他的数据
		if (UIState.EDIT.equals(this.getModel().getUiState())
				|| UIState.ADD.equals(this.getModel().getUiState())) {
			return;
		}
		//初始化时带出当前满足条件数据
		String condition=" wa_daysalary.pk_hrorg='"+((WaDayLoginContext)getContext()).getPk_hrorg()+"' and wa_daysalary.salarydate='"+((WaDayLoginContext)getContext()).getCalculdate()+"'";
		IWadaysalaryQueryService wadaysalaryQueryService=NCLocator.getInstance().lookup(IWadaysalaryQueryService.class);
		AggDaySalaryVO[] aggDaySalaryVOs = null;
		//Ares.Tank 2018年10月19日16:48:24 本地化适配
		/*try {
			aggDaySalaryVOs = wadaysalaryQueryService.queryByCondition(condition);
		} catch (BusinessException e) {
			Logger.error(e);
		}*/
		getModel().initModel(aggDaySalaryVOs);
	}
}
