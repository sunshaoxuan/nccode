package nc.ui.wa.paydata.action;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.LockFailedException;
import nc.bs.uif2.VersionConflictException;
import nc.funcnode.ui.action.INCAction;
import nc.hr.utils.ResHelper;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.itf.hrwa.IWadaysalaryService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.ui.hr.caculate.view.BannerTimerDialog;
import nc.ui.hr.caculate.view.RecacuTypeChooseDialog;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.hr.caculate.CaculateTypeVO;
import nc.vo.pfxx.util.ArrayUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.pub.WaState;

import org.apache.commons.lang.StringUtils;

/**
 * ����
 * 
 * @author: zhangg
 * @date: 2009-12-1 ����08:39:35
 * @since: eHR V6.0
 * @�߲���:
 * @�߲�����:
 * @�޸���:
 * @�޸�����:
 */
public class CaculateAction extends PayDataBaseAction {

	private static final long serialVersionUID = 1L;
	private static final String WA = "wa";

	public CaculateAction() {
		super();
		putValue(INCAction.CODE, IHRWAActionCode.CACULATEACTION);
		setBtnName(ResHelper.getString("60130paydata", "060130paydata0331")/*
																			 * @res
																			 * "����"
																			 */);
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("60130paydata", "060130paydata0331")/*
																									 * @
																									 * res
																									 * "����"
																									 */+ "(Ctrl+A)");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK));
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception{
		/**
		 * ������ȨУ��
		 */
		int isgetlicense = NCLocator.getInstance().lookup(nc.itf.uap.cil.ICilService.class)
									.getProductLicense("HRWATAX20191");	//		HRWATAX20190122
		if(isgetlicense<0){
			MessageDialog.showErrorDlg(getEntranceUI(), null, "��������˰��ǿ��δ��Ȩ��");
			return ;
		}
		//end 
		super.doAction(e);
		
	}

	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {
		// ʹ���µ���н�����߼�
		/*
		 * if (!getPaydataModel().getIsCompute()) { if
		 * (showYesNoMessage(ResHelper
		 * .getString("60130paydata","060130paydata0332")@res
		 * "��Ա����н��Ӧ�Ƚ��м���,�Ƿ����?") != MessageDialog.ID_YES) { return; } }
		 */
		final RecacuTypeChooseDialog chooseDialog = new RecacuTypeChooseDialog(getParentContainer(), WA);
		chooseDialog.showModal();
		if (chooseDialog.getResult() == UIDialog.ID_OK) {
			new SwingWorker<Boolean, Void>() {
				BannerTimerDialog dialog = new BannerTimerDialog(getParentContainer());
				String error = null;

				@Override
				protected Boolean doInBackground() throws Exception {
					try {
						dialog.setStartText(ResHelper.getString("60130paydata", "060130paydata0333")/*
																									 * @
																									 * res
																									 * "н�ʼ�������У����Ե�..."
																									 */);
						dialog.start();
						CaculateTypeVO caculateTypeVO = chooseDialog.getValue();
						List<SuperVO> payfileVos = getPaydataModel().getData();

						// MOD(������н�������Ľϴ󣬲�����н�ʼ�������н�����н����)
						// ����н�����ᵽн�ʼ���ǰͳһ���У���ʽ���Ѹ�Ϊֻȡ���������
						// by ssx on 20190224
						dialog.setStartText("�A̎����н������Ո�Ե�...");
						long start = System.currentTimeMillis();
						List<String> pk_psndoclist = getCalculatePsnList(caculateTypeVO, payfileVos);
						IWadaysalaryService daySalCalService = NCLocator.getInstance()
								.lookup(IWadaysalaryService.class);
						daySalCalService.checkDaySalaryAndCalculSalary(getWaContext().getPk_wa_class(), pk_psndoclist
								.toArray(new String[0]), getWaContext().getCyear(), getWaContext().getCperiod());
						long end = System.currentTimeMillis();
						// end

						UFDouble spent = new UFDouble(end - start).div(1000.0D);
						ShowStatusBarMsgUtil.showStatusBarMsg("�A̎����н�����ĕr��"
								+ spent.setScale(3, UFDouble.ROUND_HALF_UP).toString() + "��", getContext());

						dialog.setStartText(ResHelper.getString("60130paydata", "060130paydata0333")/*
																									 * @
																									 * res
																									 * "н�ʼ�������У����Ե�..."
																									 */);
						// ���·����г���refresh�����п�������߳����⡣
						// ���˼·��refresh�õ����߳�ִ����Ϻ�
						getPaydataManager().onCaculate(caculateTypeVO, payfileVos.toArray(new SuperVO[0]));
					} catch (LockFailedException le) {
						error = ResHelper.getString("60130paydata", "060130paydata0334")/*
																						 * @
																						 * res
																						 * "��������������������޸ģ�"
																						 */;
					} catch (VersionConflictException le) {
						throw new BusinessException(le.getBusiObject().toString(), le);
					} catch (Exception e) {
						error = e.getMessage();
					} finally {
						dialog.end();
					}
					return Boolean.TRUE;
				}

				@SuppressWarnings("unchecked")
				private List<String> getCalculatePsnList(CaculateTypeVO caculateTypeVO, List<SuperVO> payfileVos) {
					List<String> pk_psndoclist = new ArrayList<String>();
					String where = null;
					// ���㷶Χ
					boolean all = caculateTypeVO.getRange().booleanValue();
					if (where == null || all) {
						where = null;
					}

					// ���㷽ʽ
					boolean type = caculateTypeVO.getType().booleanValue();
					if (type) {
						String addWhere = "  wa_data.checkflag = 'N'  ";
						where = where == null ? addWhere : (where + " and   " + addWhere);
					} else {
						String addWhere = "  wa_data.checkflag = 'N' and wa_data.caculateflag = 'N'   ";
						where = where == null ? addWhere : (where + " and   " + addWhere);
					}
					if (StringUtils.isEmpty(where)) {
						where = WherePartUtil.getCommonWhereCondtion4Data(getWaContext().getWaLoginVO());
					} else {
						where = where + " and "
								+ WherePartUtil.getCommonWhereCondtion4Data(getWaContext().getWaLoginVO());
					}

					if (caculateTypeVO.getRange().booleanValue()) {
						String sql = "select pk_psndoc from wa_data where " + where;
						IUAPQueryBS queryPsn = NCLocator.getInstance().lookup(IUAPQueryBS.class);
						try {
							List<String> psns = (List<String>) queryPsn.executeQuery(sql, new ColumnListProcessor());
							if (psns != null && psns.size() > 0) {
								pk_psndoclist.addAll(psns);
							}
						} catch (BusinessException e) {
							ExceptionUtils.wrappBusinessException(e.getMessage());
						}
					} else {
						if (!ArrayUtils.isEmpty(payfileVos.toArray(new SuperVO[0]))) {
							for (int i = 0; i < payfileVos.size(); i++) {
								pk_psndoclist.add(((DataVO) payfileVos.get(i)).getPk_psndoc());
							}
						}
					}
					return pk_psndoclist;
				}

				/**
				 * @author zhangg on 2010-7-7
				 * @see javax.swing.SwingWorker#done()
				 */
				@Override
				protected void done() {
					if (error != null) {
						ShowStatusBarMsgUtil.showErrorMsg(ResHelper.getString("60130paydata", "060130paydata0335")/*
																												 * @
																												 * res
																												 * "������̴��ڴ���"
																												 */,
								error, getContext());
						// MessageDialog.showErrorDlg(getParentContainer(),
						// null, error);

					} else {
						ShowStatusBarMsgUtil.showStatusBarMsg(ResHelper.getString("60130paydata", "060130paydata0336")/*
																													 * @
																													 * res
																													 * "��"
																													 */
								+ dialog.getSecond() + ResHelper.getString("60130paydata", "060130paydata0337")/*
																												 * @
																												 * res
																												 * "���ڼ������."
																												 */,
								getContext());
					}
				}
			}.execute();
			// н����ĿԤ��
			String keyName = ResHelper.getString("60130paydata", "060130paydata0331")/*
																					 * @
																					 * res
																					 * "����"
																					 */;
			String[] files = getPaydataManager().getAlterFiles(keyName);
			showAlertInfo(files);
		}
	}

	/**
	 * ��ť���õ�״̬
	 * 
	 * @author zhangg on 2009-12-1
	 * @see nc.ui.wa.paydata.action.PayDataBaseAction#getEnableStateSet()
	 */
	@Override
	public Set<WaState> getEnableStateSet() {
		if (waStateSet == null) {
			waStateSet = new HashSet<WaState>();
			waStateSet.add(WaState.CLASS_WITHOUT_RECACULATED);
			waStateSet.add(WaState.CLASS_RECACULATED_WITHOUT_CHECK);
			waStateSet.add(WaState.CLASS_PART_CHECKED);

		}
		return waStateSet;
	}

	@Override
	protected boolean isActionEnable() {

		if (super.isActionEnable()) {
			List<SuperVO> payfileVos = getPaydataModel().getData();
			if (payfileVos != null && payfileVos.size() > 0) {
				for (int i = 0; i < payfileVos.size(); i++) {
					if (!((DataVO) (payfileVos.get(i))).getCheckflag().booleanValue()) {
						return true;
					}
				}
				return false;
			} else {
				return false;
			}
		}
		return false;
	}

}