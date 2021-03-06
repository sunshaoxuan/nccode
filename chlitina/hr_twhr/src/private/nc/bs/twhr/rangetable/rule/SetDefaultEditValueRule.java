package nc.bs.twhr.rangetable.rule;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.pub.ISuperVO;
import nc.vo.pubapp.AppContext;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.pubapp.util.AuditInfoUtils;
import nc.vo.twhr.rangetable.RangeLineVO;
import nc.vo.twhr.rangetable.RangeTableAggVO;
import nc.vo.twhr.rangetable.RangeTableVO;

import org.apache.commons.lang.StringUtils;

public class SetDefaultEditValueRule implements IRule<RangeTableAggVO> {

	@Override
	public void process(RangeTableAggVO[] arg0) {
		for (RangeTableAggVO aggvo : arg0) {
			RangeTableVO table = aggvo.getParentVO();
			table.setStartdate(table.getStartdate().asBegin());
			table.setEnddate(table.getEnddate().asEnd());
			//start 改为全局节点,不再关联组织信息 同时抹除主表的组织信息 Ares.Tank 2018-7-25 10:31:17
			table.setPk_group("GLOBLE00000000000000");
			table.setPk_org("GLOBLE00000000000000");
			table.setPk_org_v("GLOBLE00000000000000");
			AuditInfoUtils.setUpdateAuditInfo((IBill[])arg0);
			//table.setAttributeValue("billmaker", AppContext.getInstance().getPkUser());
			/*if (!StringUtils.isEmpty(table.getPk_group())) {
				SetChildren(aggvo.getChildren(RangeLineVO.class), "pk_group",
						table.getPk_group());
			}

			if (!StringUtils.isEmpty(table.getPk_org())) {
				SetChildren(aggvo.getChildren(RangeLineVO.class), "pk_org",
						table.getPk_org());
			}

			if (!StringUtils.isEmpty(table.getPk_org_v())) {
				SetChildren(aggvo.getChildren(RangeLineVO.class), "pk_org_v",
						table.getPk_org_v());
			}*/
			//end 改为全局节点,不再关联组织信息 同时抹除主表的组织信息 Ares.Tank 2018-7-25 10:31:17
		}
	}

	private void SetChildren(ISuperVO[] children, String fieldName,
			String fieldValue) {
		for (ISuperVO child : children) {
			child.setAttributeValue(fieldName, fieldValue);
		}
	}

}
