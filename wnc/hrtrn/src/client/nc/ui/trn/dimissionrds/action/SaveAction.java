package nc.ui.trn.dimissionrds.action;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.IActionCode;
import nc.hr.utils.ResHelper;
import nc.itf.hi.IPersonRecordService;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.itf.trn.rds.IRdsManageService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillModel;
import nc.ui.trn.dimissionrds.view.DateLarborDlg;
import nc.ui.trn.rds.action.RdsBaseAction;
import nc.ui.trn.rds.model.RdsPsninfoModel;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.TrialVO;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.trn.pub.TRNConst;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

public class SaveAction extends RdsBaseAction {

	private boolean isdisablepsn = Boolean.FALSE;

	private String refTransType; // 留停異動類型
	private String refReturnType;// 複職異動類型
	private String refChangeType = "1002Z710000000008GSN";// 转调

	public SaveAction() {

		super();
		ActionInitializer.initializeAction(this, IActionCode.SAVE);
		putValue(Action.SHORT_DESCRIPTION, ResHelper.getString("6009tran", "06009tran0042")/*
																							 * @
																							 * res
																							 * "对界面数据进行保存(Ctrl+S)"
																							 */);
	}

	/**
	 * 校验开始日期合法型
	 * 
	 * @param saveData
	 * @param curRow
	 * @return boolean
	 */
	public boolean checkBegindate(int saveType, SuperVO saveData, int currow) throws BusinessException {

		// 当前行的开始日期和终止日期
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		UFLiteralDate endDate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		// 前一条记录的开始日期
		UFLiteralDate preRowBegindate = null;
		// 前一条记录的结束日期
		UFLiteralDate preRowEnddate = null;
		// 后一条记录的开始日期
		UFLiteralDate nextRowBegindate = null;
		// 总行数
		int iRowCount = getCurBillModel().getRowCount() - 1;
		if (currow != iRowCount) {
			nextRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow + 1, "begindate");
		}
		if (currow != 0) {
			if (getCurBillModel().getValueAt(currow - 1, "enddate") != null) {
				preRowEnddate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "enddate");
			} else {
				preRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "begindate");
				preRowEnddate = beginDate.getDateBefore(1);
				if (preRowBegindate != null && preRowBegindate.afterDate(preRowEnddate)) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0043")/*
																								 * @
																								 * res
																								 * "开始日期不能早于等于上一记录的开始日期！"
																								 */);
				} else if (preRowBegindate == null) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0044")/*
																								 * @
																								 * res
																								 * "前一条记录的开始日期不能为空！"
																								 */);
				}
			}
		}
		if (preRowEnddate != null && (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0045")/*
																						 * @
																						 * res
																						 * "开始日期不能早于等于上一记录的结束日期！"
																						 */);
		}
		if (endDate != null && nextRowBegindate != null
				&& (nextRowBegindate.compareTo(endDate) == 0 || nextRowBegindate.beforeDate(endDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0046")/*
																						 * @
																						 * res
																						 * "结束日期不能晚于或等于下一记录的开始日期！"
																						 */);
		}
		// 该公司第一条任职记录 任职日期和此员工在其他公司的任职日期作比较，校验是否有交叠
		if (currow == 0 && TRNConst.Table_NAME_DEPTCHG.equals(curTabCode)) {
			NCLocator
					.getInstance()
					.lookup(IPersonRecordService.class)
					.checkBeginDate(selData.getParentVO().getPsnJobVO(),
							(UFLiteralDate) saveData.getAttributeValue(PsnJobVO.BEGINDATE), null);
		}
		return true;
	}

	/**
	 * 检验记录间的开始结束日期
	 * 
	 * @param saveType
	 * @param strTabCode
	 * @throws BusinessException
	 */
	public void checkDataForTableType(int saveType, String strTabCode, SuperVO saveData) throws BusinessException {

		UFLiteralDate begindate = null;
		UFLiteralDate enddate = null;
		String tabCode = getListView().getCurrentTabCode();
		begindate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		enddate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		if (begindate == null) {
			if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0047")/*
																							 * @
																							 * res
																							 * "进入日期不能为空！"
																							 */);
			}
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0048")/*
																						 * @
																						 * res
																						 * "开始日期不能为空！"
																						 */);
		}
		if (saveData.getAttributeValue("recordnum") != null
				&& ((Integer) saveData.getAttributeValue("recordnum")).intValue() != 0 && enddate == null) {
			if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0049")/*
																							 * @
																							 * res
																							 * "离开日期不能为空！"
																							 */);
			}
			if (!TRNConst.Table_NAME_TRIAL.equals(strTabCode)) {
				// 试用不判断结束日期
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0050")/*
																							 * @
																							 * res
																							 * "结束日期不能为空！"
																							 */);
			}
		}
		if (saveData.getAttributeValue("endflag") != null) {
			UFBoolean endflag = (UFBoolean) saveData.getAttributeValue("endflag");
			if (endflag.booleanValue() && enddate == null) {
				if (!TRNConst.Table_NAME_DIMISSION.equals(tabCode)) {
					if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
						throw new BusinessException(ResHelper.getString("6009tran", "06009tran0049")/*
																									 * @
																									 * res
																									 * "离开日期不能为空！"
																									 */);
					}
					if (!TRNConst.Table_NAME_TRIAL.equals(strTabCode)) {
						// 试用不判断结束日期
						throw new BusinessException(ResHelper.getString("6009tran", "06009tran0050")/*
																									 * @
																									 * res
																									 * "结束日期不能为空！"
																									 */);
					}
				}
			}
		}
		if (enddate != null && begindate.afterDate(enddate)) {
			if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0051")/*
																							 * @
																							 * res
																							 * "进入日期不能晚于离开日期！"
																							 */);
			}
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0052")/*
																						 * @
																						 * res
																						 * "开始日期不能晚于结束日期！"
																						 */);
		}
		if (TRNConst.Table_NAME_TRIAL.equals(tabCode)) {
			// 试用
			UFLiteralDate regulardate = (UFLiteralDate) saveData.getAttributeValue("regulardate");
			if (regulardate != null && begindate.afterDate(regulardate)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0053")/*
																							 * @
																							 * res
																							 * "开始日期不能晚于转正日期！"
																							 */);
			}
		}
		BillModel curBillModel = getListView().getBillListPanel().getBodyBillModel(getListView().getCurrentTabCode());
		int editRow = curBillModel.getEditRow();
		if (TRNConst.Table_NAME_PART.equals(strTabCode)) {
			// 兼职不校验记记录间的时间关系,只在兼职变更时校验两条记录的事件关系
			if (getModel().getEditType() == RdsPsninfoModel.PARTCHG) {
				checkPartchg(saveData, editRow);
			} else if (getModel().getEditType() == RdsPsninfoModel.UPDATE) {
				// 修改兼职记录,特殊处理
				checkPartEdit(saveData, editRow);
			}
			return;
		}
		if (TRNConst.Table_NAME_TRIAL.equals(strTabCode)) {
			// 试用的字段名不一样单独校验
			checkTrial(saveData, editRow);
			return;
		}
		if (TRNConst.Table_NAME_PSNCHG.equals(strTabCode)) {
			// 流动情况的字段名不一样单独校验
			checkPsnchg(saveData, editRow);
			return;
		}
		// WNC客开,插入调配记录的情况
		if (TRNConst.Table_NAME_DEPTCHG.equals(strTabCode) && saveType == RdsPsninfoModel.INSERT) {
			checkBegindateForInsertChg(saveType, saveData, editRow);
		} else {
			checkBegindate(saveType, saveData, editRow);
		}

	}

	private boolean checkBegindateForInsertChg(int saveType, SuperVO saveData, int currow) throws BusinessException {
		// 当前行的开始日期和终止日期
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		UFLiteralDate endDate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		// 前一条记录的开始日期
		UFLiteralDate preRowBegindate = null;
		// 前一条记录的结束日期
		UFLiteralDate preRowEnddate = null;
		// 后一条记录的开始日期
		UFLiteralDate nextRowBegindate = null;
		// 总行数
		int iRowCount = getCurBillModel().getRowCount() - 1;
		if (currow != iRowCount) {
			nextRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow + 1, "begindate");
		}
		if (currow != 0) {
			preRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "begindate");
			preRowEnddate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "enddate");
			if (preRowBegindate == null) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0044")/*
																							 * @
																							 * res
																							 * "前一条记录的开始日期不能为空！"
																							 */);
			}
			if (preRowEnddate == null) {
				// 為空則說明是新增而不是插入
				throw new BusinessException("前一條記錄的結束日期不能為空!");
			}
			// 插入數據的校驗
			if (preRowBegindate != null && (preRowBegindate.isSameDate(beginDate)) || preRowBegindate.after(beginDate)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0043")/*
																							 * @
																							 * res
																							 * "开始日期不能早于等于上一记录的开始日期！"
																							 */);
			}
			if (nextRowBegindate != null
					&& (!nextRowBegindate.after(preRowBegindate) || !nextRowBegindate.after(preRowEnddate))) {
				throw new BusinessException("資料錯誤,後一條的開始日期不能小於等於前一條記錄的開始或結束日期!");
			}
			if (nextRowBegindate == null) {
				throw new BusinessException("後一條記錄的開始日期不能為空!");
			}
			if (nextRowBegindate != null && (nextRowBegindate.isSameDate(beginDate))
					|| nextRowBegindate.before(beginDate)) {
				throw new BusinessException("開始日期不能晚於等於下一記錄的開始日期！");
			}
			if (nextRowBegindate != null && (!nextRowBegindate.getDateBefore(1).isSameDate(endDate))) {
				throw new BusinessException("結束日期須為下一記錄開始日期的前一天！");
			}
			return true;
		} else {
			throw new BusinessException("不能在入職前插入資料!");
		}

	}

	private void checkPartchg(SuperVO saveData, int currow) throws BusinessException {

		// 当前行的开始日期和终止日期
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		// 前一条记录的开始日期
		UFLiteralDate preRowBegindate = null;
		// 前一条记录的结束日期
		UFLiteralDate preRowEnddate = null;
		// 总行数
		if (currow != 0) {
			if (getCurBillModel().getValueAt(currow - 1, "enddate") != null) {
				preRowEnddate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "enddate");
			} else {
				preRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "begindate");
				preRowEnddate = beginDate.getDateBefore(1);
				if (preRowBegindate != null && preRowBegindate.afterDate(preRowEnddate)) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0054")/*
																								 * @
																								 * res
																								 * "开始日期不能早于或等于上一记录的开始日期！"
																								 */);
				} else if (preRowBegindate == null) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0044")/*
																								 * @
																								 * res
																								 * "前一条记录的开始日期不能为空！"
																								 */);
				}
			}
		}
		if (preRowEnddate != null && (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0055")/*
																						 * @
																						 * res
																						 * "开始日期不能早于或等于上一记录的结束日期！"
																						 */);
		}
	}

	private void checkPartEdit(SuperVO saveData, int currow) throws BusinessException {

		// 当前行的开始日期和终止日期
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		UFLiteralDate endDate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		// 前一条记录的开始日期
		UFLiteralDate preRowBegindate = null;
		// 前一条记录的结束日期
		UFLiteralDate preRowEnddate = null;
		// 后一条记录的开始日期
		UFLiteralDate nextRowBegindate = null;
		// 总行数
		int iRowCount = getCurBillModel().getRowCount() - 1;
		if (currow != iRowCount && saveData.getAttributeValue("lastflag") != null
				&& !((UFBoolean) saveData.getAttributeValue("lastflag")).booleanValue()) {
			// 修改兼职变更的历史记录时修改校验下一条的开始时间
			nextRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow + 1, "begindate");
			if (endDate != null && nextRowBegindate != null
					&& (nextRowBegindate.compareTo(endDate) == 0 || nextRowBegindate.beforeDate(endDate))) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0046")/*
																							 * @
																							 * res
																							 * "结束日期不能晚于或等于下一记录的开始日期！"
																							 */);
			}
		}
		if (currow != 0) {
			// 得到上一条数据,如果上一条的lastflag='Y'则不与上一条的时间比较
			CircularlyAccessibleValueObject preVO = getCurBillModel().getBodyValueRowVO(currow - 1,
					getListView().getCurClassName());
			if (preVO != null && preVO.getAttributeValue("lastflag") != null
					&& !((UFBoolean) saveData.getAttributeValue("lastflag")).booleanValue()) {
				if (preVO.getAttributeValue("enddate") != null) {
					preRowEnddate = (UFLiteralDate) preVO.getAttributeValue("enddate");
				} else {
					preRowBegindate = (UFLiteralDate) preVO.getAttributeValue("begindate");
					preRowEnddate = beginDate.getDateBefore(1);
					if (preRowBegindate != null && preRowBegindate.afterDate(preRowEnddate)) {
						throw new BusinessException(ResHelper.getString("6009tran", "06009tran0043")/*
																									 * @
																									 * res
																									 * "开始日期不能早于等于上一记录的开始日期！"
																									 */);
					} else if (preRowBegindate == null) {
						throw new BusinessException(ResHelper.getString("6009tran", "06009tran0044")/*
																									 * @
																									 * res
																									 * "前一条记录的开始日期不能为空！"
																									 */);
					}
				}
				if (preRowEnddate != null
						&& (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0045")/*
																								 * @
																								 * res
																								 * "开始日期不能早于等于上一记录的结束日期！"
																								 */);
				}
			}
		}
	}

	private void checkPsnchg(SuperVO saveData, int currow) throws BusinessException {

		// 当前行的开始日期和终止日期
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue("begindate");
		UFLiteralDate endDate = (UFLiteralDate) saveData.getAttributeValue("enddate");
		;
		// 前一条记录的开始日期
		UFLiteralDate preRowBegindate = null;
		// 前一条记录的结束日期
		UFLiteralDate preRowEnddate = null;
		// 后一条记录的开始日期
		UFLiteralDate nextRowBegindate = null;
		// 总行数
		int iRowCount = getCurBillModel().getRowCount() - 1;
		if (currow != iRowCount) {
			nextRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow + 1, "begindate");
		}
		if (currow != 0) {
			if (getCurBillModel().getValueAt(currow - 1, "enddate") != null) {
				preRowEnddate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "enddate");
			} else {
				preRowBegindate = (UFLiteralDate) getCurBillModel().getValueAt(currow - 1, "begindate");
				preRowEnddate = beginDate.getDateBefore(1);
				if (preRowBegindate != null && preRowBegindate.afterDate(preRowEnddate)) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0056")/*
																								 * @
																								 * res
																								 * "进入日期不能早于等于上一记录的进入日期！"
																								 */);
				} else if (preRowBegindate == null) {
					throw new BusinessException(ResHelper.getString("6009tran", "06009tran0057")/*
																								 * @
																								 * res
																								 * "前一条记录的进入日期不能为空！"
																								 */);
				}
			}
		}
		if (preRowEnddate != null && (preRowEnddate.compareTo(beginDate) == 0 || preRowEnddate.afterDate(beginDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0058")/*
																						 * @
																						 * res
																						 * "进入日期不能早于等于上一记录的离开日期！"
																						 */);
		}
		if (endDate != null && nextRowBegindate != null
				&& (nextRowBegindate.compareTo(endDate) == 0 || nextRowBegindate.beforeDate(endDate))) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0059")/*
																						 * @
																						 * res
																						 * "离开日期不能晚于或等于下一记录的进入日期！"
																						 */);
		}
	}

	private void checkTrial(SuperVO saveData, int currow) throws BusinessException {

		// 当前行的开始日期和终止日期
		UFLiteralDate beginDate = (UFLiteralDate) saveData.getAttributeValue(TrialVO.BEGINDATE);
		UFLiteralDate regulardate = (UFLiteralDate) saveData.getAttributeValue(TrialVO.REGULARDATE);
		TrialVO trial = (TrialVO) saveData;
		if (trial.getEndflag() != null && trial.getEndflag().booleanValue() && regulardate == null) {
			if (trial.getTrialresult() != null && trial.getTrialresult() == TRNConst.TRIALRESULT_PASS) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0060")/*
																							 * @
																							 * res
																							 * "转正日期不能为空！"
																							 */);
			}
		}
		if (regulardate != null && beginDate.afterDate(regulardate)) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0053")/*
																						 * @
																						 * res
																						 * "开始日期不能晚于转正日期！"
																						 */);
		}
		if (getModel().getEditType() == RdsPsninfoModel.INSERT && trial.getTrialresult() == null) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0061")/*
																						 * @
																						 * res
																						 * "试用结果不能为空！"
																						 */);
		}
		// 最新工作记录的开始日期
		UFLiteralDate jobBeginDate = selData.getParentVO().getPsnJobVO().getBegindate();
		if (trial.getEndflag() != null && trial.getEndflag().booleanValue() && jobBeginDate != null
				&& jobBeginDate.afterDate(beginDate)) {
			if (jobBeginDate != null && jobBeginDate.beforeDate(regulardate)) {
				throw new BusinessException(ResHelper.getString("6009tran", "06009tran0208")/*
																							 * @
																							 * res
																							 * "历史转正日期不能晚于最新工作记录的开始日期"
																							 */);
			}
		} else if (jobBeginDate != null && jobBeginDate.afterDate(beginDate)) {
			throw new BusinessException(ResHelper.getString("6009tran", "06009tran0062")/*
																						 * @
																						 * res
																						 * "开始日期不能早于最新工作记录的开始日期"
																						 */);
		}
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		super.doAction(e);
		getListView().tableStopCellEditing(curTabCode);
		// @jiazhtb 执行验证公式，显式调用
		boolean validateResult = getListView().getBillListPanel().getBodyBillModel(curTabCode)
				.execValidateForumlas(null, null, null);
		if (!validateResult) {
			return;
		}

		// 非空校验
		getListView().dataNotNullValidate();
		// 开始结束时间的校验
		int editRow = getCurBillModel().getEditRow();
		SuperVO vo = (SuperVO) getListView().getBillListPanel().getBodyBillModel(curTabCode)
				.getBodyValueRowVO(editRow, getListView().getCurClassName());
		checkDataForTableType(getModel().getEditType(), curTabCode, vo);

		// end
		// 不同子集的特殊校验
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) && getModel().getEditType() != RdsPsninfoModel.INSERT) {
			// 插入操作不进行编制校验,被保存的数据的lastflag不是Y也不进行编制校验
			// 编制校验
			if (((PsnJobVO) vo).getLastflag().booleanValue()) {
				if (!validateBudget(getContext(), new PsnJobVO[] { selData.getParentVO().getPsnJobVO() },
						new PsnJobVO[] { (PsnJobVO) vo })) {
					return;
				}
			}
		}
		// end
		selData.setTableVO(curTabCode, new SuperVO[] { vo });

		if (getModel().getEditType() == RdsPsninfoModel.ADD) {
			// fengwei 2012-12-24 如果是离职记录节点，
			// 且是“人员任职记录”页签，则提示“是否同时停用当前人员”，只有新增是提示
			if (TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {

				int result = MessageDialog.showYesNoDlg(getEntranceUI(),
						ResHelper.getString("6009tran", "06009tran0206")/*
																		 * @ res
																		 * "确认停用"
																		 */,
						ResHelper.getString("6009tran", "06009tran0185")/*
																		 * @res
																		 * "是否同时停用当前人员？"
																		 */);
				if (result == MessageDialog.ID_YES) {
					isdisablepsn = Boolean.TRUE;
				} else {
					isdisablepsn = Boolean.FALSE;
				}

			}

			// 子表增加后保存
			if (!saveAddData(selData, e)) {
				return;
			}
		} else if (getModel().getEditType() == RdsPsninfoModel.UPDATE) {
			// 子表修改后保存
			if (!saveUpdateData(selData, e)) {
				return;
			}
		} else if (getModel().getEditType() == RdsPsninfoModel.INSERT) {
			// 子表插入后保存
			if (!saveInsertData(selData)) {
				return;
			}
		} else if (getModel().getEditType() == RdsPsninfoModel.PARTCHG) {
			// 兼职变更保存
			if (!savePartchg(selData)) {
				return;
			}
		}
		getListView().setMainPanelEnabled(false);
		getListView().getBillListPanel().setEnabled(false);
		getModel().setEditType(RdsPsninfoModel.UNCHANGE);
		// 万能的repaint()
		getListView().repaint();
		getModel().setUiState(UIState.NOT_EDIT);
	}

	private BillModel getCurBillModel() {
		return getListView().getBillListPanel().getBodyBillModel(curTabCode);
	}

	private IRdsManageService getIRdsService() {
		return NCLocator.getInstance().lookup(IRdsManageService.class);
	}

	@Override
	protected boolean isActionEnable() {

		if (getContext().getPk_org() == null) {
			return false;
		}
		return getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT;
	}

	private boolean saveAddData(PsndocAggVO aggVO, ActionEvent event) throws Exception {
		// 查询最新和以前的工作记录的法人组织
		boolean isNewLegalOrg = false;
		// 新记录pk
		String oldPkOrg = selData.getParentVO().getPk_org();
		String newPkOrg = null;
		// 原纪录:
		SuperVO[] saveData = aggVO.getTableVO(curTabCode);

		for (SuperVO pjv : saveData) {
			if (pjv instanceof PsnJobVO) {
				PsnJobVO temp = (PsnJobVO) pjv;
				if (temp.getLastflag().booleanValue()) {
					newPkOrg = temp.getPk_org();
				}
			} else if (pjv instanceof TrialVO) {
				TrialVO temp = (TrialVO) pjv;
				if (temp.getLastflag().booleanValue()) {
					newPkOrg = temp.getPk_org();
				}
			}
		}
		if (null == newPkOrg) {
			isNewLegalOrg = false;
		} else {
			// 查询两个的组织是否为同一法人组织

			String[] orgs = { oldPkOrg, newPkOrg };
			Map<String, String> resultMap = LegalOrgUtilsEX.getLegalOrgByOrgs(orgs);
			if (null != resultMap && null != resultMap.get(oldPkOrg) && null != resultMap.get(newPkOrg)
					&& !resultMap.get(newPkOrg).equals(resultMap.get(oldPkOrg))) {
				// 不同的法人组织
				isNewLegalOrg = true;
			}

		}

		boolean isSynWork = (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_PART
				.equals(curTabCode))
				&& UIDialog.ID_YES == MessageDialog.showYesNoDlg(getEntranceUI(),
						ResHelper.getString("6009tran", "06009tran0207")/*
																		 * @ res
																		 * "确认同步"
																		 */,
						ResHelper.getString("6009tran", "06009tran0063")/*
																		 * @res
																		 * "是否同步履历?"
																		 */);

		if (TRNConst.Table_NAME_TRIAL.equals(curTabCode)) {
			TrialVO trail = (TrialVO) aggVO.getTableVO(curTabCode)[0];
			if (trail.getEndflag() != null && trail.getEndflag().booleanValue() && trail.getTrialresult() != null
					&& trail.getTrialresult() == TRNConst.TRIALRESULT_PASS) {
				isSynWork = UIDialog.ID_YES == MessageDialog.showYesNoDlg(getEntranceUI(),
						ResHelper.getString("6009tran", "06009tran0207")/*
																		 * @ res
																		 * "确认同步"
																		 */,
						ResHelper.getString("6009tran", "06009tran0063")/*
																		 * @res
																		 * "是否同步履历?"
																		 */);
			}
		}
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
			// 任职离职新增时要交验委托关系
			validateManageScope((PsnJobVO) aggVO.getTableVO(curTabCode)[0]);
			aggVO.setTableVO(PsnJobVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		} else if (TRNConst.Table_NAME_PART.equals(curTabCode)) {
			aggVO.setTableVO(PartTimeVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		}
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode)) {

			// 留停異動類型
			refTransType = SysInitQuery.getParaString(selData.getParentVO().getPk_org(), "TWHR11").toString();
			// 複職異動類型
			refReturnType = SysInitQuery.getParaString(selData.getParentVO().getPk_org(), "TWHR12").toString();

			if (refTransType == null || refTransType.equals("~")) {
				throw new BusinessException("系統參數 [TWHR11] 未指定用於留停的異動類型。");
			}

			if (refReturnType == null || refReturnType.equals("~")) {
				throw new BusinessException("系統參數 [TWHR12] 未指定用於留停複職的異動類型。");
			}
			// 新增工作记录
			// 是否同步履历
			PsndocAggVO retVO = getIRdsService().addPsnjob_RequiresNew(aggVO, curTabCode, isSynWork,
					getContext().getPk_org());

			// WNC 客開 插入 調配記錄 tank 2020年3月26日11:08:27
			if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) && getModel().getEditType() == RdsPsninfoModel.ADD) {
				if (retVO != null && retVO.getTableVO(curTabCode) != null && retVO.getTableVO(curTabCode).length > 0) {
					// 插入數據的起止日期
					UFLiteralDate begindate = ((PsnJobVO) aggVO.getTableVO(curTabCode)[0]).getBegindate();
					UFLiteralDate enddate = ((PsnJobVO) aggVO.getTableVO(curTabCode)[0]).getEnddate();
					// 返回值
					SuperVO[] rtnVOs = retVO.getTableVO(curTabCode);
					// 找到插入數據
					PsnJobVO psnjob = findInsertRecord(rtnVOs, begindate, enddate, 0);
					// 找到插入的前一條數據
					PsnJobVO prejob = findInsertRecord(rtnVOs, begindate, enddate, -1);

					getIRdsService().doAfterInsertPsnjob(prejob, psnjob);
				}

			}
			// end WNC 客開 插入 調配記錄 tank

			// 劳健保处理--法人组织不同的时候进入
			//
			// isNewLegalOrg = true;

			if (retVO.getParentVO().getPsnJobVO().getTrnstype() != null
					&& retVO.getParentVO().getPsnJobVO().getTrnstype().equals(refTransType)) {
				// 留職停薪時
				dealTransPNI();
			} else if (retVO.getParentVO().getPsnJobVO().getTrnstype() != null
					&& retVO.getParentVO().getPsnJobVO().getTrnstype().equals(refReturnType)) {
				// 留停複職
				dealReturnPNI();
			} else if (retVO.getParentVO().getPsnJobVO().getTrnstype() != null
					&& retVO.getParentVO().getPsnJobVO().getTrnstype().equals(refChangeType)) {
				// 转调
				if (isNewLegalOrg) {
					dealChangePNI();
				}
			}

			Object obj = getModel().getTreeObject();
			String pkTreeObj = "";
			String pkPsn = "";
			if (obj != null && obj instanceof OrgVO) {
				pkTreeObj = ((OrgVO) obj).getPk_org();
				pkPsn = retVO.getParentVO().getPsnJobVO().getPk_org();
			} else if (obj != null && obj instanceof DeptVO) {
				pkTreeObj = ((DeptVO) obj).getPk_dept();
				pkPsn = retVO.getParentVO().getPsnJobVO().getPk_dept();
			}
			if (obj == null || pkPsn.equals(pkTreeObj)) {
				// 根节点或是相同的树节点
				setRetData(retVO, curTabCode);
			} else {
				// 增加工作记录后组织或部门换了
				getModel().directlyDelete(aggVO);
			}
			// 增加工作记录后要刷新界面
		} else if (TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
			// 退保日期确认
			// DateLarborDlg dlg = new DateLarborDlg(getEntranceUI(), "退保日期確認",
			// "退保日期");

			// dlg.initUI();
			// dlg.showModal();
			// UFDate delLarbolDate = dlg.getdEffectiveDate();
			UFLiteralDate delLarbolDate = null;
			for (SuperVO pjv : saveData) {
				if (pjv instanceof PsnJobVO) {
					PsnJobVO temp = (PsnJobVO) pjv;
					if (temp.getLastflag().booleanValue()) {
						newPkOrg = temp.getPk_org();
						delLarbolDate = temp.getBegindate().getDateBefore(1);
					}
				} else if (pjv instanceof TrialVO) {
					TrialVO temp = (TrialVO) pjv;
					if (temp.getLastflag().booleanValue()) {
						newPkOrg = temp.getPk_org();
					}
				}
			}

			UFLiteralDate endDate;
			if (null == delLarbolDate) {
				endDate = null;
			} else {
				endDate = new UFLiteralDate(delLarbolDate.toDate());
			}

			getIRdsService().addPsnjobDimissionWithDate(aggVO, curTabCode, getContext().getPk_org(), isdisablepsn,
					endDate);
			// TODO 查詢該人員是否有有效的投保記錄,如果沒有則提示錯誤
			String sqlStr = "select pk_psndoc from " + PsndocDefTableUtil.getPsnLaborTablename()
					+ " WHERE ISNULL(glbdef15, '9999-12-31') > '" + endDate.toString() + "' AND dr=0 AND pk_psndoc = '"
					+ aggVO.getParentVO().getPk_psndoc() + "'" + " and lastflag = 'Y'";
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
			Object obj = iUAPQueryBS.executeQuery(sqlStr, new ColumnProcessor());

			sqlStr = "select pk_psndoc from " + PsndocDefTableUtil.getPsnHealthTablename()
					+ " WHERE ISNULL(glbdef15, '9999-12-31') > '" + endDate.toString() + "' AND dr=0 AND pk_psndoc = '"
					+ aggVO.getParentVO().getPk_psndoc() + "'" + " and lastflag = 'Y'";

			Object obj2 = iUAPQueryBS.executeQuery(sqlStr, new ColumnProcessor());

			if (null == obj && null == obj2) {
				MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(), "提示", "此員工沒有投保紀錄");
			}

			// 增加离职记录后要刷新界面
			getModel().directlyDelete(aggVO);
		} else {
			PsndocAggVO retVO = getIRdsService().addSubRecord(aggVO, curTabCode, isSynWork, getContext().getPk_org());
			setRetData(retVO, curTabCode);
		}
		return true;
	}

	/**
	 * 留停複職
	 * 
	 * @throws BusinessException
	 */
	private void dealReturnPNI() throws BusinessException {

		/*
		 * boolean is2AddPNI = UIDialog.ID_YES ==
		 * MessageDialog.showYesNoDlg(getEntranceUI(),
		 * ResHelper.getString("6009tran", "06009tran0207")
		 * 
		 * @ res "确认同步" , "留停復職是否加保"); if (is2AddPNI) { // 加保日期确认 DateLarborDlg
		 * dlg = new DateLarborDlg(getEntranceUI(), "加保日期確認", "加保日期");
		 * 
		 * dlg.initUI();
		 * 
		 * dlg.showModal(); UFDate delLarbolDate = dlg.getdEffectiveDate(); if
		 * (null == delLarbolDate) { return; } //
		 * selData.getParentVO().getPsnJobVO().getTrnstype(); try {
		 * IPsndocSubInfoService4JFS nhiService =
		 * NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
		 * nhiService.returnPsnNHI(new UFLiteralDate(delLarbolDate.toDate()),
		 * selData.getParentVO().getPsnJobVO());
		 * 
		 * } catch (BusinessException e) {
		 * MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(),
		 * "提示", "此員工沒有投保紀錄"); } }
		 */
		try {
			PsndocAggVO aggvo = selData;
			String oldPkOrg = selData.getParentVO().getPk_org();
			// 原纪录:
			SuperVO[] saveData = aggvo.getTableVO(curTabCode);
			PsnJobVO newtemp = null;
			for (SuperVO pjv : saveData) {
				PsnJobVO temp = (PsnJobVO) pjv;
				if (temp.getLastflag().booleanValue()) {
					newtemp = temp;
				}
			}
			UFLiteralDate beginDate = (UFLiteralDate) newtemp.getAttributeValue("begindate");
			// 启基客开，默认加保，加保日期为当天
			IPsndocSubInfoService4JFS nhiService = NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
			nhiService.returnPsnNHI(newtemp.getBegindate(), selData.getParentVO().getPsnJobVO());
			// 处理团保
			String[] psnArray = { selData.getParentVO().getPk_psndoc() };
			try {
				nhiService.generateGroupIns4Return(selData.getParentVO().getPk_org(), psnArray, newtemp.getBegindate());
			} catch (BusinessException e) {
				MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), "團保加保失敗!", e.getMessage()
						+ " 請手工加團保!");
			}

		} catch (BusinessException e) {
			MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(), "提示", "此員工沒有投保紀錄");
		}
	}

	/**
	 * 留职停薪
	 * 
	 * @throws BusinessException
	 */
	private void dealTransPNI() throws BusinessException {
		/*
		 * boolean is2DelPNI = UIDialog.ID_YES ==
		 * MessageDialog.showYesNoDlg(getEntranceUI(),
		 * ResHelper.getString("6009tran", "06009tran0207")
		 * 
		 * @ res "确认同步" , "留職停薪是否退保"); if (is2DelPNI) { // 退保日期确认 DateLarborDlg
		 * dlg = new DateLarborDlg(getEntranceUI(), "退保日期確認", "退保日期");
		 * 
		 * dlg.initUI();
		 * 
		 * dlg.showModal(); UFDate delLarbolDate = dlg.getdEffectiveDate(); if
		 * (null == delLarbolDate) { return; } try { //
		 * selData.getParentVO().getPsnJobVO().getTrnstype();
		 * IPsndocSubInfoService4JFS nhiService =
		 * NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
		 * nhiService.transPsnNHI(new UFLiteralDate(delLarbolDate.toDate()),
		 * selData.getParentVO().getPsnJobVO());
		 * 
		 * } catch (BusinessException e) {
		 * MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(),
		 * "提示", e.getMessage()); }
		 * 
		 * }
		 */
		try {
			// 启基客开,默认退保，退保日期为当天的前一天
			// selData.getParentVO().getPsnJobVO().getTrnstype();
			PsndocAggVO aggvo = selData;
			String oldPkOrg = selData.getParentVO().getPk_org();
			// 原纪录:
			SuperVO[] saveData = aggvo.getTableVO(curTabCode);
			PsnJobVO newtemp = null;
			for (SuperVO pjv : saveData) {
				PsnJobVO temp = (PsnJobVO) pjv;
				if (temp.getLastflag().booleanValue()) {
					newtemp = temp;
				}

			}
			UFLiteralDate beginDate = (UFLiteralDate) newtemp.getAttributeValue("begindate");
			IPsndocSubInfoService4JFS nhiService = NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
			// 处理劳健保
			nhiService.transPsnNHI(beginDate.getDateBefore(1), selData.getParentVO().getPsnJobVO());
			// 处理团保
			nhiService.dismissPsnGroupIns(selData.getParentVO().getPk_org(), selData.getParentVO().getPk_psndoc(),
					beginDate.getDateBefore(1));
		} catch (BusinessException e) {
			MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(), "提示", e.getMessage());
		}

	}

	private void dealChangePNI() throws BusinessException {
		boolean is2LegalOrg = UIDialog.ID_YES == MessageDialog.showYesNoDlg(getEntranceUI(),
				ResHelper.getString("6009tran", "06009tran0207")/*
																 * @ res "确认同步"
																 */, "是否同步投保記錄至新法人組織");
		// 退保日期确认
		DateLarborDlg dlg = new DateLarborDlg(getEntranceUI(), "退保日期確認", "退保日期");

		dlg.initUI();

		dlg.showModal();
		UFDate delLarbolDate = dlg.getdEffectiveDate();
		if (null == delLarbolDate) {
			return;
		}
		// selData.getParentVO().getPsnJobVO().getTrnstype();
		try {

			IPsndocSubInfoService4JFS nhiService = NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
			nhiService.redeployPsnNHI(new UFLiteralDate(delLarbolDate.toDate()), selData.getParentVO().getPsnJobVO(),
					is2LegalOrg);
		} catch (BusinessException e) {
			MessageDialog.showHintDlg(getModel().getContext().getEntranceUI(), "提示", e.getMessage());
		}

		/*
		 * // ssx added for Taiwan NHI on 2015-10-15 // add NHI Info //
		 * 2017-05-16 upgrade to V65, from JD code IPsndocSubInfoService4JFS
		 * nhiService =
		 * NCLocator.getInstance().lookup(IPsndocSubInfoService4JFS.class);
		 * nhiService.dismissPsnNHI(dimission.getPk_org(),
		 * dimission.getPk_psndoc(), dimission.getBegindate().getDateBefore(1));
		 * // ssx added on 2017-12-19 // 回寫團保結束日期
		 * nhiService.dismissPsnGroupIns(dimission.getPk_org(),
		 * dimission.getPk_psndoc(), dimission.getBegindate().getDateBefore(1));
		 */

	}

	private boolean saveInsertData(PsndocAggVO aggVO) throws Exception {

		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
			aggVO.setTableVO(PsnJobVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		} else if (TRNConst.Table_NAME_PART.equals(curTabCode)) {
			aggVO.setTableVO(PartTimeVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		}
		PsndocAggVO retVO = getIRdsService().insertSubRecord(aggVO, curTabCode);
		// WNC 客開 插入 調配記錄 tank 2020年3月26日11:08:27
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) && getModel().getEditType() == RdsPsninfoModel.INSERT) {
			if (retVO != null && retVO.getTableVO(curTabCode) != null && retVO.getTableVO(curTabCode).length > 0) {
				// 插入數據的起止日期
				UFLiteralDate begindate = ((PsnJobVO) aggVO.getTableVO(curTabCode)[0]).getBegindate();
				UFLiteralDate enddate = ((PsnJobVO) aggVO.getTableVO(curTabCode)[0]).getEnddate();
				// 返回值
				SuperVO[] rtnVOs = retVO.getTableVO(curTabCode);
				// 找到插入數據
				PsnJobVO psnjob = findInsertRecord(rtnVOs, begindate, enddate, 0);
				// 找到插入的前一條數據
				PsnJobVO prejob = findInsertRecord(rtnVOs, begindate, enddate, -1);

				getIRdsService().doAfterInsertPsnjob(prejob, psnjob);
			}

		}
		// end WNC 客開 插入 調配記錄 tank
		setRetData(retVO, curTabCode);
		return true;
	}

	/**
	 * 找到需要的工作記錄
	 * 
	 * @param rtnVOs
	 * @param begindate
	 * @param enddate
	 * @param flag
	 * @return
	 */
	private PsnJobVO findInsertRecord(SuperVO[] rtnVOs, UFLiteralDate begindate, UFLiteralDate enddate, int flag) {
		PsnJobVO rtn = null;
		int rtIndex = -1;
		if (rtnVOs != null) {
			for (int i = 0; i < rtnVOs.length; i++) {
				if (rtnVOs[i] instanceof PsnJobVO) {
					PsnJobVO jobvo = (PsnJobVO) rtnVOs[i];
					UFLiteralDate tpBeginDate = jobvo.getBegindate();
					UFLiteralDate tpEndDate = jobvo.getEnddate();
					if (tpBeginDate != null
							&& tpBeginDate.isSameDate(begindate)
							&& (tpEndDate != null && tpEndDate.isSameDate(enddate) || (tpEndDate == null && enddate == null))) {
						rtIndex = i;
						break;
					}
				}
			}
			if (rtIndex >= 0 && (rtIndex + flag) >= 0 && (rtIndex + flag) < rtnVOs.length) {
				rtn = (PsnJobVO) rtnVOs[(rtIndex + flag)];
			}
		}
		return rtn;
	}

	private boolean savePartchg(PsndocAggVO aggVO) throws BusinessException {

		boolean isSynWork = UIDialog.ID_YES == MessageDialog
				.showYesNoDlg(getEntranceUI(), ResHelper.getString("6009tran", "06009tran0207")/*
																								 * @
																								 * res
																								 * "确认同步"
																								 */,
						ResHelper.getString("6009tran", "06009tran0063")/*
																		 * @res
																		 * "是否同步履历?"
																		 */);
		aggVO.setTableVO(PartTimeVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		PsndocAggVO retVO = getIRdsService().savePartchg(aggVO, curTabCode, isSynWork, getContext().getPk_org());
		setRetData(retVO, curTabCode);
		return true;
	}

	private boolean saveUpdateData(PsndocAggVO aggVO, ActionEvent event) throws Exception {

		boolean isSynWork = (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_PART
				.equals(curTabCode))
				&& UIDialog.ID_YES == MessageDialog.showYesNoDlg(getEntranceUI(),
						ResHelper.getString("6009tran", "06009tran0207")/*
																		 * @ res
																		 * "确认同步"
																		 */,
						ResHelper.getString("6009tran", "06009tran0063")/*
																		 * @res
																		 * "是否同步履历?"
																		 */);
		if (TRNConst.Table_NAME_TRIAL.equals(curTabCode)) {
			TrialVO trail = (TrialVO) aggVO.getTableVO(curTabCode)[0];
			if (trail.getEndflag() != null && trail.getEndflag().booleanValue() && trail.getTrialresult() != null
					&& trail.getTrialresult() == TRNConst.TRIALRESULT_PASS) {
				isSynWork = UIDialog.ID_YES == MessageDialog.showYesNoDlg(getEntranceUI(),
						ResHelper.getString("6009tran", "06009tran0207")/*
																		 * @ res
																		 * "确认同步"
																		 */,
						ResHelper.getString("6009tran", "06009tran0063")/*
																		 * @res
																		 * "是否同步履历?"
																		 */);
			}
		}
		if (TRNConst.Table_NAME_DEPTCHG.equals(curTabCode) || TRNConst.Table_NAME_DIMISSION.equals(curTabCode)) {
			// 任职离职修改时要交验委托关系,只有是保存最新一条的时候
			PsnJobVO vo = (PsnJobVO) aggVO.getTableVO(curTabCode)[0];
			if (vo.getLastflag().booleanValue()) {
				validateManageScope(vo);
			}
			aggVO.setTableVO(PsnJobVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		} else if (TRNConst.Table_NAME_PART.equals(curTabCode)) {
			aggVO.setTableVO(PartTimeVO.getDefaultTableName(), aggVO.getTableVO(curTabCode));
		}
		PsndocAggVO retVO = getIRdsService().updateSubRecord(aggVO, curTabCode, isSynWork, getContext().getPk_org());
		if (TRNConst.Table_NAME_DIMISSION.equals(curTabCode) || TRNConst.Table_NAME_DEPTCHG.equals(curTabCode)) {
			Object obj = getModel().getTreeObject();
			String pkTreeObj = "";
			String pkPsn = "";
			if (obj != null && obj instanceof OrgVO) {
				pkTreeObj = ((OrgVO) obj).getPk_org();
				pkPsn = retVO.getParentVO().getPsnJobVO().getPk_org();
			} else if (obj != null && obj instanceof DeptVO) {
				pkTreeObj = ((DeptVO) obj).getPk_dept();
				pkPsn = retVO.getParentVO().getPsnJobVO().getPk_dept();
			}
			if (obj == null || pkPsn.equals(pkTreeObj)) {
				// 根节点或是相同的树节点
				setRetData(retVO, curTabCode);
			} else {
				// 增加工作记录后组织或部门换了
				getModel().directlyDelete(aggVO);
			}
		} else {
			setRetData(retVO, curTabCode);
		}
		return true;
	}

}
