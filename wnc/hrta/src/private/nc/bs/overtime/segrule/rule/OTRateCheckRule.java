package nc.bs.overtime.segrule.rule;

import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.overtime.AggSegRuleVO;
import nc.vo.ta.overtime.SegRuleTermVO;

public class OTRateCheckRule implements IRule<AggSegRuleVO> {

    @Override
    public void process(AggSegRuleVO[] aggvos) {
	for (AggSegRuleVO aggvo : aggvos) {
	    SegRuleTermVO[] terms = (SegRuleTermVO[]) aggvo.getChildren(SegRuleTermVO.class);
	    for (SegRuleTermVO term : terms) {
		if (term.getTaxfreeotrate() == null) {
		    ExceptionUtils.wrappBusinessException("分段 [" + term.getSegno() + "] 未指定有效的加班費率。");
		}
	    }
	}
    }

}
