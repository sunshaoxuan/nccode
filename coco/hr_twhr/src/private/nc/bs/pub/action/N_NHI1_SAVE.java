package nc.bs.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.bs.pubapp.pf.action.AbstractPfAction;
import nc.bs.pubapp.pub.rule.CommitStatusCheckRule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.impl.pubapp.pattern.rule.processer.CompareAroundProcesser;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import nc.bs.twhr.twhr_declaration.plugin.bpplugin.Twhr_declarationPluginPoint;
import nc.vo.twhr.twhr_declaration.AggDeclarationVO;
import nc.itf.twhr.ITwhr_declarationMaintain;

public class N_NHI1_SAVE extends AbstractPfAction<AggDeclarationVO> {

	protected CompareAroundProcesser<AggDeclarationVO> getCompareAroundProcesserWithRules(
			Object userObj) {
		CompareAroundProcesser<AggDeclarationVO> processor = new CompareAroundProcesser<AggDeclarationVO>(
				Twhr_declarationPluginPoint.SEND_APPROVE);
		// TODO 在此处添加审核前后规则
		IRule<AggDeclarationVO> rule = new CommitStatusCheckRule();
		processor.addBeforeRule(rule);
		return processor;
	}

	@Override
	protected AggDeclarationVO[] processBP(Object userObj,
			AggDeclarationVO[] clientFullVOs, AggDeclarationVO[] originBills) {
		ITwhr_declarationMaintain operator = NCLocator.getInstance().lookup(
				ITwhr_declarationMaintain.class);
		AggDeclarationVO[] bills = null;
		try {
			bills = operator.save(clientFullVOs, originBills);
		} catch (BusinessException e) {
			ExceptionUtils.wrappBusinessException(e.getMessage());
		}
		return bills;
	}

}
