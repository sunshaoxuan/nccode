package nc.ui.hrjf.deptadj.action;

import java.util.Date;

import nc.ui.pub.beans.MessageDialog;
import nc.ui.pubapp.uif2app.actions.EditAction;
import nc.vo.om.hrdept.AggHRDeptAdjustVO;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uif2.LoginContext;

public class DeptadjEditAction extends EditAction {

	@Override
	protected boolean checkDataPermission() {
		LoginContext context = getModel().getContext();
		Object data = getModel().getSelectedData();
		AggHRDeptAdjustVO aggvo = (AggHRDeptAdjustVO) data;
		// 只允许修改执行标志未打勾,并且生效日期小于当前日期的单据
		if (null != aggvo.getParentVO().getIseffective()
				&& (aggvo.getParentVO().getIseffective().booleanValue() && (aggvo.getParentVO().getEffectivedate()
						.before(new UFLiteralDate(new Date())) || aggvo.getParentVO().getEffectivedate()
						.isSameDate(new UFLiteralDate(new Date()))))) {
			MessageDialog.showErrorDlg(context.getEntranceUI().getParent(), "報錯信息", "不允許修改已執行、並且生效日期小於當前日期的單據");
			return false;
		} else {
			return true;
		}
	}

}
