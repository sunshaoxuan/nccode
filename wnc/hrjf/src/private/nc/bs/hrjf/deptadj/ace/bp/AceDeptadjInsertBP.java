package nc.bs.hrjf.deptadj.ace.bp;

import nc.bs.framework.common.NCLocator;
import nc.bs.hrjf.deptadj.plugin.bpplugin.DeptadjPluginPoint;
import nc.impl.pubapp.pattern.data.bill.template.InsertBPTemplate;
import nc.impl.pubapp.pattern.rule.processer.AroundProcesser;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.itf.om.IDeptAdjustService;
import nc.vo.logging.Debug;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

/**
 * ��׼��������BP
 */
public class AceDeptadjInsertBP {

	public AggHRDeptAdjustVO[] insert(AggHRDeptAdjustVO[] bills) {

		InsertBPTemplate<AggHRDeptAdjustVO> bp = new InsertBPTemplate<AggHRDeptAdjustVO>(
				DeptadjPluginPoint.INSERT);
		this.addBeforeRule(bp.getAroundProcesser());
		this.addAfterRule(bp.getAroundProcesser());
		AggHRDeptAdjustVO[] results = bp.insert(bills);
		//�����Ч����Ϊ��ǰ����,��������Ч
		try {
			NCLocator.getInstance().lookup(IDeptAdjustService.class).executeDeptVersion(new UFLiteralDate());
		} catch (BusinessException e) {
			Debug.debug(e.getMessage());
			e.printStackTrace();
		}
		return results;
	}

	/**
	 * ���������
	 * 
	 * @param processor
	 */
	private void addAfterRule(AroundProcesser<AggHRDeptAdjustVO> processor) {
		// TODO ���������
		IRule<AggHRDeptAdjustVO> rule = null;
	}

	/**
	 * ����ǰ����
	 * 
	 * @param processor
	 */
	private void addBeforeRule(AroundProcesser<AggHRDeptAdjustVO> processer) {
		// TODO ����ǰ����
		IRule<AggHRDeptAdjustVO> rule = null;
		rule = new nc.bs.pubapp.pub.rule.FillInsertDataRule();
		processer.addBeforeRule(rule);
	}
}