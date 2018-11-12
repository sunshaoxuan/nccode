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
 * �򿪽ڵ����
 * @author ward
 *
 */
public class WaDaysalaryFuncNodeInitDataListener extends DefaultFuncNodeInitDataListener{
	@Override
	public void initData(FuncletInitData data) {
		// ���һ���ڵ��Ѿ��򿪲������ڱ༭����, Ĭ�ϲ��ٴ�����������
		if (UIState.EDIT.equals(this.getModel().getUiState())
				|| UIState.ADD.equals(this.getModel().getUiState())) {
			return;
		}
		//��ʼ��ʱ������ǰ������������
		String condition=" wa_daysalary.pk_hrorg='"+((WaDayLoginContext)getContext()).getPk_hrorg()+"' and wa_daysalary.salarydate='"+((WaDayLoginContext)getContext()).getCalculdate()+"'";
		IWadaysalaryQueryService wadaysalaryQueryService=NCLocator.getInstance().lookup(IWadaysalaryQueryService.class);
		AggDaySalaryVO[] aggDaySalaryVOs = null;
		//Ares.Tank 2018��10��19��16:48:24 ���ػ�����
		/*try {
			aggDaySalaryVOs = wadaysalaryQueryService.queryByCondition(condition);
		} catch (BusinessException e) {
			Logger.error(e);
		}*/
		getModel().initModel(aggDaySalaryVOs);
	}
}