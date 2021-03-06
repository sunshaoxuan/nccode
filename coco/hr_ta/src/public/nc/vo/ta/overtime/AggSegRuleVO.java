package nc.vo.ta.overtime;

import nc.vo.pubapp.pattern.model.entity.bill.AbstractBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;

@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.ta.overtime.SegRuleVO")

public class AggSegRuleVO extends AbstractBill {
	
	  @Override
	  public IBillMeta getMetaData() {
	  	IBillMeta billMeta =BillMetaFactory.getInstance().getBillMeta(AggSegRuleVOMeta.class);
	  	return billMeta;
	  }
	    
	  @Override
	  public SegRuleVO getParentVO(){
	  	return (SegRuleVO)this.getParent();
	  }
	  
}