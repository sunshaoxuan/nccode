package nc.ui.wa.taxspecial_statistics.action;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Action;

import nc.funcnode.ui.action.INCAction;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.uif2.model.IAppModelDataManagerEx;
import nc.ui.wa.pub.WaOrgHeadPanel;
import nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsAppModel;
import nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsModelDataManager;
import nc.ui.wa.taxspecial_statistics.model.TaxSpecialStatisticsModelService;
import nc.ui.wa.taxspecial_statistics.view.TaxSpecialStatisticsListView;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 反锁定Action
 * 
 * @author: xuhw
 */
public class TaxSpecialStatisticsUnLockAction extends HrAction {

	private static final long serialVersionUID = -8050698726088941767L;
	private IAppModelDataManagerEx dataManager = null;

	private TaxSpecialStatisticsListView editor;
	private WaOrgHeadPanel orgpanel = null;

	public TaxSpecialStatisticsModelDataManager getDataManager() {
		return (TaxSpecialStatisticsModelDataManager) dataManager;
	}

	public void setDataManager(IAppModelDataManagerEx dataManager) {
		this.dataManager = dataManager;
	}

	public WaOrgHeadPanel getOrgpanel() {
		return orgpanel;
	}

	public void setOrgpanel(WaOrgHeadPanel orgpanel) {
		this.orgpanel = orgpanel;
	}

	public TaxSpecialStatisticsListView getEditor() {
		return editor;
	}

	public void setEditor(TaxSpecialStatisticsListView editor) {
		this.editor = editor;
	}

	public TaxSpecialStatisticsUnLockAction() {
		super();
		putValue(INCAction.CODE, "TaxSpecialStatisticsUnLockAction");
		setBtnName("解锁");
		putValue(Action.SHORT_DESCRIPTION, "解锁");
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		((TaxSpecialStatisticsModelService) this.getModel().getService()).unLockData((WaLoginContext) getContext(),
				getDataManager().getLastWhereSql(), null);
		getOrgpanel().refresh();
		getDataManager().refresh();
	}

	/**
	 * 解锁数据
	 * 界面上有未审核的薪资数据才可以解锁
	 * 解锁只能解锁未被审核的数据，被审核的不可以
	 */
	@Override
	protected boolean isActionEnable() {
		if (super.isActionEnable()) {
			List<Object> datas = getModel().getData();
			if (datas == null || datas.size() < 1) {
				return false;
			}
			boolean allCheck = true;
			for (Object obj : datas) {
				PayfileVO vo = (PayfileVO) obj;
				if (vo.getCheckflag().equals(UFBoolean.FALSE)) {
					allCheck = false;
				}
			}
			if (allCheck) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public TaxSpecialStatisticsAppModel getModel() {
		return (TaxSpecialStatisticsAppModel) super.getModel();
	}

}