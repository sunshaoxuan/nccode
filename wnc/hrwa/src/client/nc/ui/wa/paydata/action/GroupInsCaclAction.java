package nc.ui.wa.paydata.action;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.wa.paydata.DataVO;

import org.apache.commons.lang.StringUtils;

public class GroupInsCaclAction extends PayDataBaseAction {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = -6116710789493297603L;
	private String errorMessage = "";

	public GroupInsCaclAction() {
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt", "068J61035-0001")/*
																													 * @
																													 * res
																													 * 团保费计算
																													 */);
		this.setCode("CalculatePTAction");
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		JComponent parentUi = getWaContext().getEntranceUI();

		if (showYesNoMessage("團保計算已合併至薪資計算過程中，是否仍然單獨計算團保費？") != 4) {
			this.putValue("message_after_action", "團保計算已取消。");
			return;
		}

		new SwingWorker() {

			BannerTimerDialog dialog = new BannerTimerDialog(SwingUtilities.getWindowAncestor(getModel().getContext()
					.getEntranceUI()));
			String error = null;

			protected Boolean doInBackground() throws Exception {
				try {
					dialog.setStartText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt",
							"068J61035-0002")/* @res 正在计算员工团保费用 */);
					dialog.start();

					// 计算服务
					IPsndocSubInfoService4JFS service = NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
					service.calculateGroupIns(getWaContext().getPk_org(), getWaContext().getPk_wa_class(),
							getWaContext().getCyear(), getWaContext().getCperiod(), null, false);

				} catch (LockFailedException le) {
					errorMessage = le.getMessage();
				} catch (VersionConflictException le) {
					throw new BusinessException(le.getBusiObject().toString(), le);
				} catch (Exception e) {
					errorMessage = e.getMessage();
				} finally {
					dialog.end();
				}
				return Boolean.TRUE;
			}

			protected void done() {
				if (!StringUtils.isEmpty(errorMessage)) {
					ShowStatusBarMsgUtil.showErrorMsg(
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt", "068J61035-0003")/*
																														 * @
																														 * res
																														 * 正在计算员工团保费用发生错误
																														 * ：
																														 */
							, errorMessage, getContext());
				} else {
					ShowStatusBarMsgUtil.showStatusBarMsg(
							nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt", "068J61035-0004")/*
																														 * @
																														 * res
																														 * 正在计算员工团保费用成功
																														 * 。
																														 */
							, getContext());
				}
			}
		}.execute();

		this.putValue("message_after_action",
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_personalmgt", "068J61035-0002")/*
																											 * @
																											 * res
																											 * 正在计算员工团保费用
																											 */);
	}

	protected boolean isActionEnable() {
		if (super.isActionEnable()) {
			List<SuperVO> payfileVos = getPaydataModel().getData();
			if ((payfileVos != null) && (payfileVos.size() > 0)) {
				for (int i = 0; i < payfileVos.size(); i++) {
					if (!((DataVO) payfileVos.get(i)).getCheckflag().booleanValue()) {
						return true;
					}
				}
				return false;
			}
			return false;
		}

		return false;
	}
}
