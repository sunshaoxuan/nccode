package nc.ui.wa.payfile.action;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.IActionCode;
import nc.bs.wa.util.LocalizationSysinitUtil;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.wizard.IWizardDialogListener;
import nc.ui.pub.beans.wizard.WizardActionException;
import nc.ui.pub.beans.wizard.WizardDialog;
import nc.ui.pub.beans.wizard.WizardEvent;
import nc.ui.pub.beans.wizard.WizardStep;
import nc.ui.pub.query.tools.ImageIconAccessor;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.wa.payfile.model.PayfileAppModel;
import nc.ui.wa.payfile.model.PayfileModelDataManager;
import nc.ui.wa.payfile.model.PayfileWizardModel;
import nc.ui.wa.payfile.wizard.BatchEditWizardThirdStep;
import nc.ui.wa.payfile.wizard.SearchPsnWizardFirstStep;
import nc.ui.wa.payfile.wizard.SearchPsnWizardSecondStep;
import nc.ui.wa.pub.WADelegator;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.payfile.Taxtype;
import nc.vo.wa.payfile.WizardActionType;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wa.taxrate.TaxBaseVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * 薪资档案批量修改Action
 * 
 * @author: zhoucx
 * @date: 2009-11-30 下午05:02:17
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class BatchEditPayfileAction extends PayfileBaseAction implements IWizardDialogListener {

	private static final long serialVersionUID = 3762858219065755582L;

	private final String actionName = ResHelper.getString("60130payfile", "060130payfile0331")/*
																							 * @
																							 * res
																							 * "批量修改"
																							 */;

	private PayfileWizardModel wizardModel = null;

	private Map<String, String> psnCodes = null;

	public BatchEditPayfileAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.BROW);
		putValue(AbstractAction.SMALL_ICON, ImageIconAccessor.getIcon("uapde/edit.gif"));
		setBtnName(ResHelper.getString("60130payfile", "060130payfile0331")/*
																			 * @res
																			 * "批量修改"
																			 */);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK + Event.SHIFT_MASK));
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("60130payfile", "060130payfile0331")/*
																									 * @
																									 * res
																									 * "批量修改"
																									 */
				+ "(Ctrl+Shift+E)");
	}

	public PayfileAppModel getPayfileModel() {
		return (PayfileAppModel) super.getModel();
	}

	/**
	 * @author liangxr on 2009-12-25
	 * @see nc.ui.uif2.actions.AddAction#doAction(java.awt.event.ActionEvent)
	 */
	@Override
	public void doActionForExtend(ActionEvent e) throws Exception {

		// 批量修改向导对话框
		WizardDialog wizardDialog = new WizardDialog(getModel().getContext().getEntranceUI(), newWizardModel(),
				getSteps(), null);
		wizardDialog.setWizardDialogListener(this);
		wizardDialog.setResizable(true);
		wizardDialog.setSize(new Dimension(800, 620));
		wizardDialog.showModal();
	}

	private List<WizardStep> getSteps() {
		PayfileModelDataManager dataManager = (PayfileModelDataManager) super.getDataManager();

		// 第一步 查询人员
		WizardStep step1 = new SearchPsnWizardFirstStep(getPayfileModel(), dataManager, WizardActionType.UPDATE,
				actionName);
		// 第二步 选择人员
		WizardStep step2 = new SearchPsnWizardSecondStep(getPayfileModel(), dataManager, actionName);
		// 第三步 修改项目
		WizardStep step3 = new BatchEditWizardThirdStep(getPayfileModel(), dataManager);
		// 放到List中
		List<WizardStep> steps = Arrays.asList(new WizardStep[] { step1, step2, step3 });
		return steps;
	}

	public void setWizardModel(PayfileWizardModel wizardModel) {
		this.wizardModel = wizardModel;
	}

	public PayfileWizardModel getWizardModel() {
		if (wizardModel == null) {
			wizardModel = newWizardModel();
		}
		return wizardModel;
	}

	public PayfileWizardModel newWizardModel() {
		wizardModel = new PayfileWizardModel();
		wizardModel.setSteps(getSteps());
		return wizardModel;
	}

	/**
	 * @author liangxr on 2010-1-20
	 * @see nc.ui.pub.beans.wizard.IWizardDialogListener#wizardFinish(nc.ui.pub.beans.wizard.WizardEvent)
	 */
	@Override
	public void wizardFinish(WizardEvent event) throws WizardActionException {
		// 从model中取数据，
		PayfileVO[] addPsntemp = getWizardModel().getSelectVOs();
		Vector<String> v_cannotEdit = new Vector<String>();
		Vector<PayfileVO> v_add2Prnt = new Vector<PayfileVO>();

		PayfileVO[] cannotEdits = getWizardModel().getCannotEditVOs();
		// 如果存在不能修改的记录。（多次发放其他子方案已存在的记录）
		if (!ArrayUtils.isEmpty(cannotEdits)) {
			for (PayfileVO vo : cannotEdits) {
				v_cannotEdit.add(vo.getPk_psndoc());
			}
		}

		if (addPsntemp != null && addPsntemp.length > 0) {
			PayfileVO batchvo = getWizardModel().getBatchitemvo();
			// try {
			// batchvo =
			// WADelegator.getPayfileQuery().getPayfileVersionInfo(batchvo);
			// } catch (BusinessException e1) {
			// Logger.error(e1.getMessage(), e1);
			// throw new WizardActionException(e1);
			// }
			// add by ward 20180112
			Map<String, String> isForeginMap = null;
			TaxBaseVO taxBaseVO = new TaxBaseVO();
			if (null != batchvo.getTaxtableid() && !"".equals(batchvo.getTaxtableid())) {
				try {
					taxBaseVO = (TaxBaseVO) getUAPQueryBS().retrieveByPK(TaxBaseVO.class, batchvo.getTaxtableid());
				} catch (BusinessException e2) {
					Logger.error(e2.getMessage(), e2);
				}
			}
			if (Integer.valueOf(3).equals(taxBaseVO.getItbltype())) {
				try {
					isForeginMap = getIsForegin(addPsntemp);
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
					WizardActionException e1 = new WizardActionException(e);
					e1.addMsg(ResHelper.getString("60130payfile", "060130payfile0245")/*
																					 * @
																					 * res
																					 * "提示"
																					 */, e.getMessage());
					throw e1;
				}
			}
			List<PayfileVO> listPayfileVOs = new ArrayList<PayfileVO>();// 能够保存的数据
			List<PayfileVO> errorList = new ArrayList<PayfileVO>();// 存在问题的数据
			for (PayfileVO element : addPsntemp) {
				element.setPk_prnt_class(getWaLoginVO().getPk_prnt_class());
				if (!v_cannotEdit.contains(element.getPk_psndoc())) {
					// 界面的填写数据。
					if (batchvo.getTaxedit().booleanValue()) {
						element.setTaxtype(batchvo.getTaxtype());
						element.setTaxtableid(batchvo.getTaxtableid());
						element.setIsndebuct(batchvo.getIsndebuct());
					}
					if (batchvo.getTaxfreeedit().booleanValue()) {
						element.setIsderate(batchvo.getIsderate());
						element.setDerateptg(batchvo.getDerateptg());
					}
					if (batchvo.getFipedit().booleanValue()) {
						element.setPk_financeorg(batchvo.getPk_financeorg());
						element.setFiporgvid(batchvo.getFiporgvid());
						element.setPk_financedept(batchvo.getPk_financedept());
						element.setFipdeptvid(batchvo.getFipdeptvid());
					}
					if (batchvo.getLibedit().booleanValue()) {
						element.setPk_liabilityorg(batchvo.getPk_liabilityorg());
						element.setPk_liabilitydept(batchvo.getPk_liabilitydept());
						element.setLibdeptvid(batchvo.getLibdeptvid());
					}

					// ssx added on 2020-10-22
					// 設置補充保費計算方式
					element.setExnhitype(batchvo.getExnhitype());
					//

					// 如果vo中有“不扣税”,但批改时只修改了减免税，未修改扣税方式可能引起数据的不一致，重置这部分数据
					if (Taxtype.TAXFREE.equalsValue(element.getTaxtype()) && element.getIsderate().booleanValue()) {
						element.setIsderate(UFBoolean.valueOf(false));
						element.setDerateptg(null);
					}
					// 多次发放中，其他子方案已经存在的数据（其他子方案中为停发），只有停发标志是可以修改的
					if (batchvo.getStopedit().booleanValue()) {
						element.setStopflag(batchvo.getStopflag());
					}
					if (WaLoginVOHelper.isSubClass(getWaLoginVO())) {// 多次发放
						v_add2Prnt.add(element);
					}
				}
				// 多次发放中，其他子方案已经存在的数据，只有停发标志是可以修改的
				if (batchvo.getStopedit().booleanValue()) {
					element.setStopflag(batchvo.getStopflag());
				}
				if (Integer.valueOf(3).equals(taxBaseVO.getItbltype())) {
					if ("N".equals(isForeginMap.get(element.getPk_psndoc()))) {
						errorList.add(element);
					} else {
						listPayfileVOs.add(element);
					}
				} else {
					listPayfileVOs.add(element);
				}

			}
			PayfileVO[] add2total = null;
			if (v_add2Prnt.size() > 0) {
				add2total = new PayfileVO[v_add2Prnt.size()];
				v_add2Prnt.copyInto(add2total);
			}
			// 批量修改。
			try {
				// WADelegator.getPayfile().update(addPsntemp,add2total);
				if (listPayfileVOs != null && listPayfileVOs.size() > 0)
					WADelegator.getPayfile().update(listPayfileVOs.toArray(new PayfileVO[0]), add2total);
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
				throw new WizardActionException(e);
			}
			getWizardModel().setPayfileVOs(null);
			addPsntemp = null;
			getWizardModel().setSelectVOs(null);
			// 刷新页面
			try {
				((PayfileModelDataManager) getDataManager()).getPaginationModel().setObjectPks(
						((PayfileModelDataManager) getDataManager()).getPks());
			} catch (BusinessException e) {
				Logger.error(e.getMessage());
			}
			if (errorList != null && errorList.size() > 0) {
				String msg = ResHelper.getString("notice", "2notice-tw-000002")/* 人员编码为 */;
				for (int i = 0; i < errorList.size(); i++) {
					String code = psnCodes.get(errorList.get(i).getPk_psndoc());
					if (i == errorList.size() - 1)
						msg += code;
					else
						msg += code + "、";
				}
				msg += ResHelper.getString("notice", "2notice-tw-000003")/*
																		 * 的员工不是外籍员工
																		 * ，
																		 * 请确认员工咨询维护中是否正确维护是否外籍员工
																		 * 。
																		 */;
				MessageDialog.showErrorDlg(null, ResHelper.getString("60130payfile", "060130payfile0245"), msg);
			} else {
				putValue(HrAction.MESSAGE_AFTER_ACTION, ResHelper.getString("60130payfile", "060130payfile0351")/*
																												 * @
																												 * res
																												 * "批量修改操作成功。"
																												 */);
			}
		} else {
			WizardActionException e = new WizardActionException(new BusinessException());
			e.addMsg(ResHelper.getString("60130payfile", "060130payfile0245")/*
																			 * @res
																			 * "提示"
																			 */,
					ResHelper.getString("60130payfile", "060130payfile0281")/*
																			 * @res
																			 * "请选择人员!"
																			 */);
			throw e;
		}
	}

	/**
	 * @author liangxr on 2010-1-20
	 * @see nc.ui.pub.beans.wizard.IWizardDialogListener#wizardFinishAndContinue(nc.ui.pub.beans.wizard.WizardEvent)
	 */
	@Override
	public void wizardFinishAndContinue(WizardEvent event) throws WizardActionException {
	}

	/**
	 * @author xuanlt on 2010-3-24
	 * @see nc.ui.pub.beans.wizard.IWizardDialogListener#wizardCancel(nc.ui.pub.beans.wizard.WizardEvent)
	 */
	@Override
	public void wizardCancel(WizardEvent event) throws WizardActionException {
		// TODO Auto-generated method stub

	}

	/**
	 * 根据要插入的薪资档案VO，查询所有员工是否为外籍员工
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, String> getIsForegin(PayfileVO[] vos) throws BusinessException {
		String[] pks = SQLHelper.getStrArray(vos, PayfileVO.PK_PSNDOC);
		InSQLCreator isc = new InSQLCreator();
		String inPsndocSql = isc.getInSQL(pks);
		String where = PayfileVO.PK_PSNDOC + " in(" + inPsndocSql + ")";
		IUAPQueryBS queryBS = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		@SuppressWarnings("unchecked")
		List<PsndocVO> list = (ArrayList<PsndocVO>) queryBS.retrieveByClause(PsndocVO.class, where);
		Map<String, String> SLOMap = new HashMap<String, String>();
		psnCodes = new HashMap<String, String>();
		for (PsndocVO psndocVO : list) {
			SLOMap.put(psndocVO.getPk_psndoc(),
					psndocVO.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01")) != null ? psndocVO
							.getAttributeValue(LocalizationSysinitUtil.getTwhrlPsn("TWHRLPSN01")).toString() : "N");
			psnCodes.put(psndocVO.getPk_psndoc(), psndocVO.getCode());
		}
		return SLOMap;
	}

	private IUAPQueryBS queryBS = null;

	public IUAPQueryBS getUAPQueryBS() {
		if (queryBS == null) {
			queryBS = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		}
		return queryBS;
	}

}