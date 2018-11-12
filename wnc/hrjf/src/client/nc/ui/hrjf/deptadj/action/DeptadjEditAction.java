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
		// ֻ�����޸�ִ�б�־δ��,������Ч����С�ڵ�ǰ���ڵĵ���
		if (null != aggvo.getParentVO().getIseffective()
				&& (aggvo.getParentVO().getIseffective().booleanValue()
						|| aggvo.getParentVO().getEffectivedate().before(new UFLiteralDate(new Date())) || aggvo
						.getParentVO().getEffectivedate().isSameDate(new UFLiteralDate(new Date())))) {
			MessageDialog.showErrorDlg(context.getEntranceUI().getParent(), "���e��Ϣ", "�����S�޸Ĉ��И��I��,�K����Ч����С춮�ǰ���ڵĆΓ�");
			return false;
		} else {
			return true;
		}
	}

}