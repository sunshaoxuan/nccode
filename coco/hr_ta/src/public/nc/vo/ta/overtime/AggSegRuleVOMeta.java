package nc.vo.ta.overtime;

import nc.vo.pubapp.pattern.model.meta.entity.bill.AbstractBillMeta;

public class AggSegRuleVOMeta extends AbstractBillMeta{
	
	public AggSegRuleVOMeta(){
		this.init();
	}
	
	private void init() {
		this.setParent(nc.vo.ta.overtime.SegRuleVO.class);
		this.addChildren(nc.vo.ta.overtime.SegRuleTermVO.class);
	}
}