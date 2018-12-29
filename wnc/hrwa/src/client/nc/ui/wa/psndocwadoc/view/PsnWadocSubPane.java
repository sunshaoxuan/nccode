package nc.ui.wa.psndocwadoc.view;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.hr.uif2.view.HrBillFormEditor;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillModelCellEditableController;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.BillTableCellRenderer;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.wa.adjust.view.WaItemDecimalAdapter;
import nc.ui.wa.psndocwadoc.action.CancelPsndocwadocAction;
import nc.ui.wa.psndocwadoc.action.SavePsndocwadocAction;
import nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel;
import nc.ui.wa.ref.WAGradePrmSecDia;
import nc.ui.wa.ref.WaGradeRefModel;
import nc.ui.wa.ref.WaItemRefModelForGrade;
import nc.ui.wa.salaryadjmgt.WASalaryadjmgtDelegator;
import nc.ui.wa.salaryadjmgt.WaBillTableEnterKeyControler;
import nc.ui.wa.salaryadjmgt.WaSalaryadjmgtUtility;
import nc.vo.hi.wadoc.PsndocWadocMainVO;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.hi.wadoc.PsndocwadocCommonDef;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.adjust.PsnappaproveBVO;
import nc.vo.wa.grade.WaCriterionVO;
import nc.vo.wa.grade.WaGradeVO;
import nc.vo.wa.item.WaItemVO;

/**
 * 定调资信息维护 表体
 * 
 * @author: xuhw
 * @date: 2009-12-26 上午09:27:04
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class PsnWadocSubPane extends HrBillFormEditor implements BillEditListener, BillEditListener2, AppEventListener,
		ListSelectionListener, ValueChangedListener {
	private static final long serialVersionUID = 1691612280204595966L;

	// ssx added on 2018-11-14
	// for multi-selection for wadoc
	private boolean isHeadBatch = false;

	public boolean isHeadBatch() {
		return ((PsndocwadocAppModel) this.getModel()).isBatch();
	}

	//

	/** 表体项目能否编辑的状态 */
	private final static Map<String, Boolean> NEGOTIATION_WAGEADD_STATE = new HashMap<String, Boolean>();
	private final static Map<String, Boolean> NEGOTIATION_WAGEINSERT_STATE = new HashMap<String, Boolean>();
	private final static Map<String, Boolean> NEGOTIATION_WAGEMODIFY_STATE = new HashMap<String, Boolean>();
	private final static Map<String, Boolean> WAGEADD_STATE = new HashMap<String, Boolean>();
	private final static Map<String, Boolean> WAGEINSERT_STATE = new HashMap<String, Boolean>();
	private final static Map<String, Boolean> WAGEMODIFY_STATE = new HashMap<String, Boolean>();
	private BillScrollPane billPane = null;
	private UIRefPane refpane = null;
	/** 薪资项目PK */
	private String pkWaItem;
	/** 是否谈判工资标识 */
	boolean negotiation_wage = false;
	private int last_select_row_number = 0;

	private BillModel billModel;
	private PsndocWadocVO[] subVOs = null;
	private PsndocWadocMainVO mainVO = null;
	/** 是否多选 */
	boolean isMultsecCkeck = false;
	/** 是否宽带薪酬 */
	boolean isRangeCkeck = false;

	/** 薪资项目所在列 */
	private final int PK_WA_ITEM_INDEX = 0;
	/** 薪资类别所在列 */
	private final int PK_WA_GRD_INDEX = 1;

	private WaCriterionVO crtVO;

	static {
		// 谈判工资 增加
		NEGOTIATION_WAGEADD_STATE.put(PsndocWadocVO.PK_WA_ITEM_SHOWNAME, new Boolean(true));
		NEGOTIATION_WAGEADD_STATE.put(PsndocWadocVO.PK_WA_GRD_SHOWNAME, new Boolean(true));
		NEGOTIATION_WAGEADD_STATE.put(PsndocWadocVO.BEGINDATE, new Boolean(true));
		NEGOTIATION_WAGEADD_STATE.put(PsndocWadocVO.ENDDATE, new Boolean(true));
		NEGOTIATION_WAGEADD_STATE.put(PsndocWadocVO.CHANGEDATE, new Boolean(true));
		NEGOTIATION_WAGEADD_STATE.put(PsndocWadocVO.NEGOTIATION_WAGE, new Boolean(true));
		NEGOTIATION_WAGEADD_STATE.put(PsndocWadocVO.PK_WA_CRT_SHOWNAME, new Boolean(false));
		NEGOTIATION_WAGEADD_STATE.put(PsndocWadocVO.CRITERIONVALUE, new Boolean(false));
		NEGOTIATION_WAGEADD_STATE.put(PsndocWadocVO.NMONEY, new Boolean(true));
		NEGOTIATION_WAGEADD_STATE.put(PsndocWadocVO.WAFLAG, new Boolean(true));
		NEGOTIATION_WAGEADD_STATE.put(PsndocWadocVO.LASTFLAG, new Boolean(false));
		NEGOTIATION_WAGEADD_STATE.put(PsndocWadocVO.DOCNAME, new Boolean(true));
		NEGOTIATION_WAGEADD_STATE.put(PsndocWadocVO.PK_CHANGECAUSE, new Boolean(true));
		NEGOTIATION_WAGEADD_STATE.put(PsndocWadocVO.VBASEFILE, new Boolean(true));
		// 谈判工资 插入
		NEGOTIATION_WAGEINSERT_STATE.put(PsndocWadocVO.PK_WA_ITEM_SHOWNAME, new Boolean(false));
		NEGOTIATION_WAGEINSERT_STATE.put(PsndocWadocVO.PK_WA_GRD_SHOWNAME, new Boolean(true));
		NEGOTIATION_WAGEINSERT_STATE.put(PsndocWadocVO.BEGINDATE, new Boolean(true));
		NEGOTIATION_WAGEINSERT_STATE.put(PsndocWadocVO.ENDDATE, new Boolean(true));
		NEGOTIATION_WAGEINSERT_STATE.put(PsndocWadocVO.CHANGEDATE, new Boolean(true));
		NEGOTIATION_WAGEINSERT_STATE.put(PsndocWadocVO.NEGOTIATION_WAGE, new Boolean(true));
		NEGOTIATION_WAGEINSERT_STATE.put(PsndocWadocVO.PK_WA_CRT_SHOWNAME, new Boolean(false));
		NEGOTIATION_WAGEINSERT_STATE.put(PsndocWadocVO.CRITERIONVALUE, new Boolean(false));
		NEGOTIATION_WAGEINSERT_STATE.put(PsndocWadocVO.NMONEY, new Boolean(true));
		NEGOTIATION_WAGEINSERT_STATE.put(PsndocWadocVO.WAFLAG, new Boolean(true));
		NEGOTIATION_WAGEINSERT_STATE.put(PsndocWadocVO.LASTFLAG, new Boolean(false));
		NEGOTIATION_WAGEINSERT_STATE.put(PsndocWadocVO.DOCNAME, new Boolean(true));
		NEGOTIATION_WAGEINSERT_STATE.put(PsndocWadocVO.PK_CHANGECAUSE, new Boolean(true));
		NEGOTIATION_WAGEINSERT_STATE.put(PsndocWadocVO.VBASEFILE, new Boolean(true));
		// 谈判工资 修改
		NEGOTIATION_WAGEMODIFY_STATE.put(PsndocWadocVO.PK_WA_ITEM_SHOWNAME, new Boolean(false));
		NEGOTIATION_WAGEMODIFY_STATE.put(PsndocWadocVO.PK_WA_GRD_SHOWNAME, new Boolean(true));
		NEGOTIATION_WAGEMODIFY_STATE.put(PsndocWadocVO.BEGINDATE, new Boolean(true));
		NEGOTIATION_WAGEMODIFY_STATE.put(PsndocWadocVO.ENDDATE, new Boolean(true));
		NEGOTIATION_WAGEMODIFY_STATE.put(PsndocWadocVO.CHANGEDATE, new Boolean(true));
		NEGOTIATION_WAGEMODIFY_STATE.put(PsndocWadocVO.NEGOTIATION_WAGE, new Boolean(true));
		NEGOTIATION_WAGEMODIFY_STATE.put(PsndocWadocVO.PK_WA_CRT_SHOWNAME, new Boolean(false));
		NEGOTIATION_WAGEMODIFY_STATE.put(PsndocWadocVO.CRITERIONVALUE, new Boolean(false));
		NEGOTIATION_WAGEMODIFY_STATE.put(PsndocWadocVO.NMONEY, new Boolean(true));
		NEGOTIATION_WAGEMODIFY_STATE.put(PsndocWadocVO.WAFLAG, new Boolean(true));
		NEGOTIATION_WAGEMODIFY_STATE.put(PsndocWadocVO.LASTFLAG, new Boolean(false));
		NEGOTIATION_WAGEMODIFY_STATE.put(PsndocWadocVO.DOCNAME, new Boolean(true));
		NEGOTIATION_WAGEMODIFY_STATE.put(PsndocWadocVO.PK_CHANGECAUSE, new Boolean(true));
		NEGOTIATION_WAGEMODIFY_STATE.put(PsndocWadocVO.VBASEFILE, new Boolean(true));
		// 非谈判工资 新增
		WAGEADD_STATE.put(PsndocWadocVO.PK_WA_ITEM_SHOWNAME, new Boolean(true));
		WAGEADD_STATE.put(PsndocWadocVO.PK_WA_GRD_SHOWNAME, new Boolean(true));
		WAGEADD_STATE.put(PsndocWadocVO.BEGINDATE, new Boolean(true));
		WAGEADD_STATE.put(PsndocWadocVO.ENDDATE, new Boolean(true));
		WAGEADD_STATE.put(PsndocWadocVO.CHANGEDATE, new Boolean(true));
		WAGEADD_STATE.put(PsndocWadocVO.NEGOTIATION_WAGE, new Boolean(true));
		WAGEADD_STATE.put(PsndocWadocVO.PK_WA_CRT_SHOWNAME, new Boolean(true));
		WAGEADD_STATE.put(PsndocWadocVO.CRITERIONVALUE, new Boolean(false));
		WAGEADD_STATE.put(PsndocWadocVO.NMONEY, new Boolean(true));
		WAGEADD_STATE.put(PsndocWadocVO.WAFLAG, new Boolean(true));
		WAGEADD_STATE.put(PsndocWadocVO.LASTFLAG, new Boolean(false));
		WAGEADD_STATE.put(PsndocWadocVO.DOCNAME, new Boolean(true));
		WAGEADD_STATE.put(PsndocWadocVO.PK_CHANGECAUSE, new Boolean(true));
		WAGEADD_STATE.put(PsndocWadocVO.VBASEFILE, new Boolean(true));
		// 非谈判工资 插入
		WAGEINSERT_STATE.put(PsndocWadocVO.PK_WA_ITEM_SHOWNAME, new Boolean(false));
		WAGEINSERT_STATE.put(PsndocWadocVO.PK_WA_GRD_SHOWNAME, new Boolean(true));
		WAGEINSERT_STATE.put(PsndocWadocVO.BEGINDATE, new Boolean(true));
		WAGEINSERT_STATE.put(PsndocWadocVO.ENDDATE, new Boolean(true));
		WAGEINSERT_STATE.put(PsndocWadocVO.CHANGEDATE, new Boolean(true));
		WAGEINSERT_STATE.put(PsndocWadocVO.NEGOTIATION_WAGE, new Boolean(true));
		WAGEINSERT_STATE.put(PsndocWadocVO.PK_WA_CRT_SHOWNAME, new Boolean(true));
		WAGEINSERT_STATE.put(PsndocWadocVO.CRITERIONVALUE, new Boolean(false));
		WAGEINSERT_STATE.put(PsndocWadocVO.NMONEY, new Boolean(true));
		WAGEINSERT_STATE.put(PsndocWadocVO.WAFLAG, new Boolean(true));
		WAGEINSERT_STATE.put(PsndocWadocVO.LASTFLAG, new Boolean(false));
		WAGEINSERT_STATE.put(PsndocWadocVO.DOCNAME, new Boolean(true));
		WAGEINSERT_STATE.put(PsndocWadocVO.PK_CHANGECAUSE, new Boolean(true));
		WAGEINSERT_STATE.put(PsndocWadocVO.VBASEFILE, new Boolean(true));
		// 非谈判工资 修改
		WAGEMODIFY_STATE.put(PsndocWadocVO.PK_WA_ITEM_SHOWNAME, new Boolean(false));
		WAGEMODIFY_STATE.put(PsndocWadocVO.PK_WA_GRD_SHOWNAME, new Boolean(true));
		WAGEMODIFY_STATE.put(PsndocWadocVO.BEGINDATE, new Boolean(true));
		WAGEMODIFY_STATE.put(PsndocWadocVO.ENDDATE, new Boolean(true));
		WAGEMODIFY_STATE.put(PsndocWadocVO.CHANGEDATE, new Boolean(true));
		WAGEMODIFY_STATE.put(PsndocWadocVO.NEGOTIATION_WAGE, new Boolean(true));
		WAGEMODIFY_STATE.put(PsndocWadocVO.PK_WA_CRT_SHOWNAME, new Boolean(true));
		WAGEMODIFY_STATE.put(PsndocWadocVO.CRITERIONVALUE, new Boolean(false));
		WAGEMODIFY_STATE.put(PsndocWadocVO.NMONEY, new Boolean(true));
		WAGEMODIFY_STATE.put(PsndocWadocVO.WAFLAG, new Boolean(true));
		WAGEMODIFY_STATE.put(PsndocWadocVO.LASTFLAG, new Boolean(false));
		WAGEMODIFY_STATE.put(PsndocWadocVO.DOCNAME, new Boolean(true));
		WAGEMODIFY_STATE.put(PsndocWadocVO.PK_CHANGECAUSE, new Boolean(true));
		WAGEMODIFY_STATE.put(PsndocWadocVO.VBASEFILE, new Boolean(true));
	}

	@Override
	public void initUI() {
		super.initUI();
		billModel = getBillCardPanel().getBillModel();
		initTableStyle();
		// liangxr 设置小数位数监听
		addDecimalListener();

		UIRefPane grdRefpane = (UIRefPane) this.getBillCardPanel().getBodyItem(PsndocWadocVO.PK_WA_GRD_SHOWNAME)
				.getComponent();
		grdRefpane.addValueChangedListener(this);
		grdRefpane.setButtonFireEvent(true);

		/*
		 * 单元格是否必输
		 */
		BillTableCellRenderer cellRenderer = new BillTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				BillModel billModel = billCardPanel.getBillModel();
				Object object = billModel.getValueAt(row, PsndocWadocVO.NEGOTIATION_WAGE);
				// if(object==null)
				// return c;
				boolean isNegotiation = object == null ? false : (Boolean) object;
				if (value == null && !isNegotiation) {
					setForeground(Color.RED);
					setText("*");
				}
				return c;
			}
		};
		getBillCardPanel().getBodyPanel().getShowCol(PsndocWadocVO.PK_WA_GRD_SHOWNAME).setCellRenderer(cellRenderer);

		UIRefPane itemRefpane = (UIRefPane) this.getBillCardPanel().getBodyItem(PsndocWadocVO.PK_WA_ITEM_SHOWNAME)
				.getComponent();
		itemRefpane.addValueChangedListener(this);
		itemRefpane.setButtonFireEvent(true);

		UIRefPane applyRefpane = (UIRefPane) this.getBillCardPanel().getBodyItem(PsndocWadocVO.PK_WA_CRT_SHOWNAME)
				.getComponent();
		applyRefpane.addValueChangedListener(this);
		applyRefpane.setButtonFireEvent(true);

		UIRefPane docRefpane = (UIRefPane) this.getBillCardPanel().getBodyItem(PsndocWadocVO.DOCNAME).getComponent();
		docRefpane.addValueChangedListener(this);
		docRefpane.setButtonFireEvent(true);

		getBillCardPanel().setBillTableEnterKeyControler("PsndocWadocVO", new WaBillTableEnterKeyControler());
		getBillCardPanel().getBodyPanel().addEditListener2(this);

		// 20151214 shenliangc NCdp205559130 薪资规则，新增一行后，填入数据用回车换行，第二行行号为空，保存时报错
		// 去掉回车增行、子表表体右键。
		getBillCardPanel().getBodyPanel().setAutoAddLine(false);
		getBillCardPanel().getBodyPanel().setBBodyMenuShow(false);

	}

	/**
	 * 返回 BillPane 特性值。
	 */
	private BillScrollPane getBillPane() {
		if (billPane == null) {
			billPane = billCardPanel.getBodyPanel();
		}
		return billPane;
	}

	/** 初始化主表。 */
	public void initTableStyle() {
		getBillPane().getTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getBillPane().getTable().getSelectionModel().addListSelectionListener(this);
		getBillPane().getTable().setSortEnabled(false);
		getBillPane().getTable().setColumnSelectionAllowed(false);
		getBillPane().getTable().setRowSelectionAllowed(true);

		// 显示行号
		getBillPane().setRowNOShow(true);
	}

	/**
	 * liangxr 设置小数位数监听 使子表每行的小数位数和薪资项目保持一致
	 */
	private void addDecimalListener() {
		String[] billitems = new String[] { PsndocWadocVO.CRITERIONVALUE, PsndocWadocVO.NMONEY };
		IBillModelDecimalListener2 bmd = new WaItemDecimalAdapter(WaItemVO.PK_WA_ITEM, billitems,
				(PsndocwadocAppModel) getModel());
		getBillCardPanel().getBillModel().addDecimalListener(bmd);

	}

	/**
	 * 显示选中人员的所有薪资变动情况。
	 */
	public void setSubVOs(PsndocWadocMainVO vo) {
		mainVO = vo;
		try {
			PsndocWadocVO[] subvos = WASalaryadjmgtDelegator.getPsndocWadocQueryService().queryAllVOsByPsnPKForHI(
					vo.getPk_psndoc(), vo.getAssgid(), false);
			if (subvos == null) {
				billModel.setBodyDataVO(subvos);
				return;
			}
			for (PsndocWadocVO subvo : subvos) {
				subvo.setDeptCode(vo.getDeptcode());
				subvo.setDeptName(vo.getDeptname());
				subvo.setPsnCode(vo.getPsncode());
				subvo.setPsnName(vo.getPsnname());
				subvo.setPostName(vo.getPostname());
			}
			setWadocData(subvos);
		} catch (BusinessException ex) {
			Logger.error(ex.getMessage(), ex);
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), null,
					ResHelper.getString("60130adjmtc", "060130adjmtc0181")/*
																		 * @res
																		 * "显示人员薪资变动情况详细信息出错！"
																		 */);
			return;
		}
	}

	/**
	 * 单元格是否可编辑
	 * 
	 * @author xuhw on 2010-1-5
	 * @param row
	 * @param col
	 * @return
	 */
	private boolean isEditable(boolean blnIsEditable, int row, String keyName) {
		// ssx added on 2018-11-14
		// for multi-selection for wadoc
		if (this.isHeadBatch()) {
			return false;
		}
		//

		boolean isEditable = false;
		if (this.getModel().getUiState() != UIState.ADD && this.getModel().getUiState() != UIState.EDIT) {
			return isEditable;
		}

		// 如果是增加行， 并且选择的行是最后一行， 则整行可编辑
		int col = findColIndex(keyName);
		if (getBillPane().isLockCol()) {
			col = col - getBillPane().getLockCol();
		}
		Boolean workflowFlag = (Boolean) getBodyCellValue(row, PsndocWadocVO.WORKFLOWFLAG);
		if (row == last_select_row_number) {
			if (PsndocwadocCommonDef.MODIFY_STATE.equals(getModelState()) && workflowFlag) {
				// 只能修改最新记录的发放标志
				int index = billModel.getBodyColByKey(PsndocWadocVO.WAFLAG);
				int index2 = billModel.getBodyColByKey(PsndocWadocVO.ENDDATE);
				int index3 = billModel.getBodyColByKey(PsndocWadocVO.CHANGEDATE);
				if (col == index || col == index2 || col == index3) {
					isEditable = true;
				}
			} else if (!PsndocwadocCommonDef.UNKNOWN_STATE.equals(getModelState())) {
				Boolean boolean1 = getStateHash(getModelState(), negotiation_wage).get(keyName);
				isEditable = boolean1 == null ? false : boolean1.booleanValue();

				Boolean blnNegotiation = false;
				Object object = billModel.getValueAt(row, PsndocWadocVO.NEGOTIATION_WAGE);
				if (object != null) {
					blnNegotiation = (Boolean) object;
				}
				if (PsndocWadocVO.PK_WA_CRT_SHOWNAME.equals(keyName)
						|| PsndocWadocVO.PK_WA_GRD_SHOWNAME.equals(keyName)) {
					return !blnNegotiation;
				}
				if (PsndocWadocVO.NMONEY.equals(keyName)) {
					if (!this.isRangeCkeck) {
						return blnNegotiation;
					}
				}
			}
		}
		Logger.debug("index=" + keyName + "col=" + col);
		return isEditable;
	}

	/**
	 * 执行动作
	 * 
	 * @author xuhw on 2010-1-6
	 * @param strAction
	 * @throws BusinessException
	 */
	public void doAction() throws BusinessException {
		if (PsndocwadocCommonDef.ADD_STATE.equals(getModelState())) {
			onAdd();
		} else if (PsndocwadocCommonDef.INSERT_STATE.equals(getModelState())) {
			onInsert();
		} else if (PsndocwadocCommonDef.MODIFY_STATE.equals(getModelState())) {
			onModify();
		}

		// 设置选中行
		getBillPane().getTable().setRowSelectionInterval(last_select_row_number, last_select_row_number);
	}

	/** 增加 */
	@Override
	public void onAdd() {
		billModel.addLine();
		last_select_row_number = billModel.getRowCount() - 1;
		refreshItemRef();
		// 发放记录， 最新标志全部选择
		setBodyCellValue(UFBoolean.valueOf(true), last_select_row_number, PsndocWadocVO.WAFLAG);
		setBodyCellValue(UFBoolean.valueOf(true), last_select_row_number, PsndocWadocVO.LASTFLAG);
		setBodyCellValue("0", last_select_row_number, PsndocWadocVO.RECORDNUM);
		setBodyCellValue(0, last_select_row_number, PsndocWadocVO.WORKFLOWFLAG);
		setBodyCellValue(mainVO.getDeptname(), last_select_row_number, "");
		setBodyCellValue(mainVO.getPsnname(), last_select_row_number, "");
		setBodyCellValue(mainVO.getPartflag(), last_select_row_number, PsndocWadocVO.PARTFLAG);
		setBodyCellValue(mainVO.getAssgid(), last_select_row_number, PsndocWadocVO.ASSGID);
		setBodyCellValue(mainVO.getPk_psndoc(), last_select_row_number, PsndocWadocVO.PK_PSNDOC);
		setBodyCellValue(mainVO.getPk_psnjob(), last_select_row_number, PsndocWadocVO.PK_PSNJOB);
	}

	/** 插入 */
	private void onInsert() throws BusinessException {
		int selectRow = getBillPane().getTable().getSelectedRow();
		if (selectRow == -1) {
			last_select_row_number = 0;
			Logger.debug("请选择插入的位置！");
			throw new BusinessException(ResHelper.getString("60130adjmtc", "060130adjmtc0217")/*
																							 * @
																							 * res
																							 * "请选择插入的位置！"
																							 */);
		}
		String name = getBodyCellValue(selectRow, PsndocWadocVO.PK_WA_ITEM_SHOWNAME).toString();
		String pk_wa_item = getBodyCellValue(selectRow, PsndocWadocVO.PK_WA_ITEM).toString();
		String recordnum = getBodyCellValue(selectRow, PsndocWadocVO.RECORDNUM).toString();
		billModel.insertRow(selectRow);
		setBodyCellValue(name, selectRow, PsndocWadocVO.PK_WA_ITEM_SHOWNAME);
		setBodyCellValue(pk_wa_item, selectRow, PsndocWadocVO.PK_WA_ITEM);
		int num = Integer.parseInt(recordnum) + 1;
		setBodyCellValue(num, selectRow, PsndocWadocVO.RECORDNUM);
		setBodyCellValue(UFBoolean.valueOf(true), selectRow, PsndocWadocVO.WAFLAG);
		setBodyCellValue(0, selectRow, PsndocWadocVO.WORKFLOWFLAG);
		setBodyCellValue(UFBoolean.valueOf(false), selectRow, PsndocWadocVO.LASTFLAG);
		setBodyCellValue(mainVO.getPartflag(), selectRow, PsndocWadocVO.PARTFLAG);
		setBodyCellValue(mainVO.getAssgid(), selectRow, PsndocWadocVO.ASSGID);
		setBodyCellValue(mainVO.getPk_psndoc(), last_select_row_number, PsndocWadocVO.PK_PSNDOC);
		setBodyCellValue(mainVO.getPk_psnjob(), last_select_row_number, PsndocWadocVO.PK_PSNJOB);

		// 初始化薪资标准参照
		refreshGradeRef(PsndocWadocVO.PK_WA_GRD_SHOWNAME, new WaGradeRefModel(pk_wa_item, this.getModel().getContext()
				.getPk_org()));

		last_select_row_number = selectRow;
	}

	/** 修改表体 */
	private void onModify() throws BusinessException {
		int selectRow = getBillPane().getTable().getSelectedRow();
		last_select_row_number = selectRow;
		String pk_wa_item = getBodyCellValue(selectRow, PsndocWadocVO.PK_WA_ITEM).toString();
		refreshGradeRef(PsndocWadocVO.PK_WA_GRD_SHOWNAME, new WaGradeRefModel(pk_wa_item, this.getModel().getContext()
				.getPk_org()));
		BillItem[] billitems = getBillCardPanel().getBodyItems();
		for (BillItem billItem : billitems) {
			billItem.setEnabled(true);

		}
	}

	/**
	 * 取得选中的表体VO
	 * 
	 * @author xuhw on 2010-1-6
	 * @return
	 */
	public PsndocWadocVO getSelectVO(Integer selectrow) throws BusinessException {
		PsndocWadocVO selectVO = new PsndocWadocVO();
		getBillCardPanel().stopEditing();
		int selectRow = selectrow == null ? last_select_row_number : selectrow;
		selectVO = (PsndocWadocVO) billModel.getBodyValueRowVO(selectRow, PsndocWadocVO.class.getName());
		selectVO.setPk_psndoc(mainVO.getPk_psndoc());
		selectVO.setPk_psnjob(mainVO.getPk_psnjob());
		selectVO.setPk_group(getModel().getContext().getPk_group());
		selectVO.setPk_org(getModel().getContext().getPk_org());
		return selectVO;
	}

	/**
	 * 设置表体VO
	 * 
	 * @author xuhw on 2010-1-6
	 * @param subVOs
	 */
	public void setWadocData(PsndocWadocVO[] subVOs) {
		// 2016-12-2 zhousze 薪资加密：这里处理定调资信息维护表体数据，进行数据解密 begin
		if (subVOs != null) {
			for (PsndocWadocVO vo : subVOs) {
				vo.setCriterionvalue(new UFDouble(
						SalaryDecryptUtil.decrypt((vo.getCriterionvalue() == null ? new UFDouble(0) : vo
								.getCriterionvalue()).toDouble())));
				vo.setNmoney(new UFDouble(SalaryDecryptUtil.decrypt((vo.getNmoney() == null ? new UFDouble(0) : vo
						.getNmoney()).toDouble())));
			}
		}
		// end
		this.subVOs = subVOs;
		billModel.setBodyDataVO(subVOs);
		getBillPane().getTable().getSelectionModel().addListSelectionListener(this);
		if (subVOs != null && subVOs.length != 0) {
			getBillPane().getTable().setRowSelectionInterval(0, 0);
		}
		// TableColResize.reSizeTable(getBillPane()); //连续刷新100次，会报错！！
	}

	/**
	 * 更改表体参照
	 * 
	 * @param colKey
	 * @throws BusinessException
	 */
	private void refreshCriterion(final String strPkWaGrd, final int intRowLine) {
		String colKey = null;
		if (WaSalaryadjmgtUtility.adjustNull(strPkWaGrd)) {
			return;
		}
		colKey = PsndocWadocVO.PK_WA_CRT_SHOWNAME;

		int colKeyIndex = findColIndex(colKey);
		WaGradeVO wagradevo = null;
		try {
			wagradevo = WASalaryadjmgtDelegator.getAdjustQueryService().queryWagradeVoByGradePk(strPkWaGrd);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), null, e.getMessage());
			return;
		}
		// 是否多选
		isMultsecCkeck = wagradevo.getIsmultsec().booleanValue();
		// 是否宽带薪酬
		isRangeCkeck = wagradevo.getIsrange().booleanValue();
		Boolean isNeg = (Boolean) getBodyCellValue(intRowLine, PsndocWadocVO.NEGOTIATION_WAGE);
		if (isNeg != null && isNeg) {
			setBodyCellValue(UFBoolean.valueOf(false).toString(), intRowLine, PsndocWadocVO.ISRANGE);
		} else {
			setBodyCellValue(UFBoolean.valueOf(isRangeCkeck).toString(), intRowLine, PsndocWadocVO.ISRANGE);
		}

		UIRefPane pane = new UIRefPane() {
			private static final long serialVersionUID = -4261446741080782942L;

			@Override
			public void onButtonClicked() {
				if (strPkWaGrd != null) {
					// TODO Auto-generated method stub
					WAGradePrmSecDia dlg = new WAGradePrmSecDia(getModel().getContext().getEntranceUI(), strPkWaGrd,
							isMultsecCkeck, isRangeCkeck);
					dlg.showModal();
					if (dlg.getResult() == UIDialog.ID_OK) {
						changeWaCrt4Dia(intRowLine, dlg.getSelectVO());
						getBillCardPanel().stopEditing();
					}
				}
			}
		};

		// NumberFormat formater = DecimalFormat.getInstance();
		// formater.setMaximumFractionDigits(3);
		// formater.setMinimumFractionDigits(3);
		pane.setEditable(true);
		pane.setEnabled(true);
		pane.addValueChangedListener(this);
		pane.setButtonFireEvent(true);
		colKeyIndex = getBillPane().getTable().convertColumnIndexToView(colKeyIndex);
		TableColumn tableColumn = getBillPane().getTable().getColumnModel().getColumn(colKeyIndex);

		tableColumn.setCellEditor(new BillCellEditor(pane));
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		int rowIndex = getBillPane().getTable().getSelectedRow();
		getBillCardPanel().stopEditing();

		// 薪资项目发生变化
		if (PsndocWadocVO.PK_WA_ITEM_SHOWNAME.equalsIgnoreCase(e.getKey())) {
			changeWaItem(rowIndex);
		}
		// 薪资标准类别发生变化
		else if (PsndocWadocVO.PK_WA_GRD_SHOWNAME.equalsIgnoreCase(e.getKey())) {
			changeWaGrd(rowIndex);
		}
		// 谈判工资发生变化
		else if (PsndocWadocVO.NEGOTIATION_WAGE.equalsIgnoreCase(e.getKey())) {
			changeNegotiationWage(rowIndex);
		}
		// 薪资标准发生变化
		else if (e.getKey().equalsIgnoreCase(PsndocWadocVO.PK_WA_CRT_SHOWNAME)) {
			refpane = (UIRefPane) ((BillCellEditor) e.getSource()).getComponent();
			changeWaCrt(rowIndex);
		}
	}

	/**
	 * 薪资项目发生变化
	 * 
	 * @param rowIndex
	 */
	private void changeWaItem(int rowIndex) {
		// 清空薪资标准等
		String[] clearRow = new String[] { PsndocWadocVO.PK_WA_GRD_SHOWNAME, PsndocWadocVO.PK_WA_GRD,
				PsndocWadocVO.NEGOTIATION_WAGE, PsndocWadocVO.PK_WA_CRT_SHOWNAME, PsndocWadocVO.NMONEY,
				PsndocWadocVO.CRITERIONVALUE,

				PsndocWadocVO.ISRANGE, PsndocWadocVO.CRT_MIN_VALUE, PsndocWadocVO.CRT_MAX_VALUE, };
		for (String strKey : clearRow) {
			setBodyCellValue(null, rowIndex, strKey);
		}
		// 记住薪资项目主键
		pkWaItem = ((UIRefPane) billModel.getBodyItems()[PK_WA_ITEM_INDEX].getComponent()).getRefPK();

		// 初始化薪资标准参照
		refreshGradeRef(PsndocWadocVO.PK_WA_GRD_SHOWNAME, new WaGradeRefModel(pkWaItem, this.getModel().getContext()
				.getPk_org()));

		setBodyCellValue(pkWaItem, rowIndex, PsndocWadocVO.PK_WA_ITEM);
	}

	/**
	 * 得到界面上显示的数据 给打印使用,保证打印的小数位数正确
	 */

	public PsndocWadocVO[] getUIData() {
		return (PsndocWadocVO[]) getBillCardPanel().getBillModel().getBodyValueVOs(PsndocWadocVO.class.getName());
	}

	/**
	 * 薪资类别发生变化
	 * 
	 * @author xuhw on 2009-12-21
	 * @param rowIndex
	 * @throws BusinessException
	 */
	private void changeWaGrd(int rowIndex) {
		// 清空薪资标准等
		String[] clearRow = new String[] { PsndocWadocVO.NEGOTIATION_WAGE, PsndocWadocVO.PK_WA_CRT_SHOWNAME,
				PsndocWadocVO.NMONEY, PsndocWadocVO.CRITERIONVALUE,/*
																	 * PsndocWadocVO
																	 * .
																	 * PK_WA_CRT
																	 * ,
																	 */
				PsndocWadocVO.PK_WA_SECLV, PsndocWadocVO.PK_WA_PRMLV,

				PsndocWadocVO.ISRANGE, PsndocWadocVO.CRT_MIN_VALUE, PsndocWadocVO.CRT_MAX_VALUE,
				PsndocWadocVO.PK_WA_CRT };

		// 根据选择的人员相关属性，需要自动带出的项目
		String[] psnappaproveItems = new String[] { PsnappaproveBVO.NEGOTIATION,
				PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME, PsnappaproveBVO.WA_CRT_APPLY_MONEY,
				PsnappaproveBVO.WA_APPLY_MONEY, PsnappaproveBVO.PK_WA_PRMLV_APPLY, PsnappaproveBVO.PK_WA_SECLV_APPLY };
		for (String strKey : clearRow) {
			setBodyCellValue(null, rowIndex, strKey);
		}

		// 记住薪资项目主键
		Object StrGrdPK = ((UIRefPane) billModel.getBodyItems()[PK_WA_GRD_INDEX].getComponent()).getRefPK();
		setBodyCellValue(StrGrdPK, rowIndex, PsndocWadocVO.PK_WA_GRD);
		if (StrGrdPK == null) {
			return;
		}

		Object strPkWaItem = billModel.getValueAt(rowIndex, PsndocWadocVO.PK_WA_ITEM);
		Object assgid = billModel.getValueAt(rowIndex, PsndocWadocVO.ASSGID);

		if (strPkWaItem != null && mainVO.getPk_psndoc() != null && StrGrdPK != null) {
			PsnappaproveBVO psnappaproveBVO = null;
			try {
				psnappaproveBVO = WaSalaryadjmgtUtility.getExtraInfo(mainVO.getPk_psndoc(), (Integer) assgid,
						strPkWaItem.toString(), StrGrdPK.toString(), true);
				// 2017-10-11 zhousze 薪资加密：定调资根据级别属性自动带出薪资解密处理 begin
				psnappaproveBVO.setWa_crt_apply_money(new UFDouble(SalaryDecryptUtil.decrypt((psnappaproveBVO
						.getWa_crt_apply_money() == null ? new UFDouble(0) : psnappaproveBVO.getWa_crt_apply_money())
						.toDouble())));
				psnappaproveBVO.setWa_apply_money(new UFDouble(SalaryDecryptUtil.decrypt((psnappaproveBVO
						.getWa_apply_money() == null ? new UFDouble(0) : psnappaproveBVO.getWa_apply_money())
						.toDouble())));
				// end
				setBodyCellValue(psnappaproveBVO.getAttributeValue(PsnappaproveBVO.NEGOTIATION), rowIndex,
						PsndocWadocVO.NEGOTIATION_WAGE);
				setBodyCellValue(psnappaproveBVO.getAttributeValue(PsnappaproveBVO.PK_WA_CRT_APPLY_SHOWNAME), rowIndex,
						PsndocWadocVO.PK_WA_CRT_SHOWNAME);
				setBodyCellValue(psnappaproveBVO.getAttributeValue(PsnappaproveBVO.WA_CRT_APPLY_MONEY), rowIndex,
						PsndocWadocVO.NMONEY);
				setBodyCellValue(psnappaproveBVO.getAttributeValue(PsnappaproveBVO.WA_APPLY_MONEY), rowIndex,
						PsndocWadocVO.CRITERIONVALUE);
				setBodyCellValue(psnappaproveBVO.getAttributeValue(PsnappaproveBVO.PK_WA_CRT), rowIndex,
						PsndocWadocVO.PK_WA_CRT);
				setBodyCellValue(psnappaproveBVO.getAttributeValue(PsnappaproveBVO.PK_WA_PRMLV_APPLY), rowIndex,
						PsndocWadocVO.PK_WA_PRMLV);
				setBodyCellValue(psnappaproveBVO.getAttributeValue(PsnappaproveBVO.PK_WA_SECLV_APPLY), rowIndex,
						PsndocWadocVO.PK_WA_SECLV);
				// 更改表体的参照
				refreshCriterion(StrGrdPK.toString(), rowIndex);
			} catch (BusinessException e) {
				Logger.error(e.getMessage(), e);
				MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(), null, e.getMessage());
				return;
			}
		}
	}

	/**
	 * 薪资标准变化
	 * 
	 * @author xuhw on 2009-12-21
	 * @param rowIndex
	 * @throws BusinessException
	 */
	private void changeWaCrt(int rowIndex) {
		changeWaCrt4Dia(rowIndex, crtVO);
	}

	/**
	 * 薪资标准变化
	 * 
	 * @author xuhw on 2009-12-21
	 * @param rowIndex
	 * @throws BusinessException
	 */
	private void changeWaCrt4Dia(int rowIndex, WaCriterionVO crtVO) {
		/** 下限 */
		double dblMinValue = 0.0D;
		/** 上限 */
		double dblMaxValue = 0.0D;
		// 清空薪资标准等
		String[] clearRow = new String[] {
				// 申请薪资
				PsndocWadocVO.CRITERIONVALUE, PsndocWadocVO.NMONEY, PsndocWadocVO.PK_WA_SECLV,
				PsndocWadocVO.PK_WA_PRMLV, PsndocWadocVO.PK_WA_CRT_SHOWNAME, PsndocWadocVO.CRT_MIN_VALUE,
				PsndocWadocVO.CRT_MAX_VALUE, PsndocWadocVO.PK_WA_CRT };
		for (String strKey : clearRow) {
			setBodyCellValue(null, rowIndex, strKey);
		}

		if (crtVO == null) {
			return;
		}
		this.setBodyCellValue(crtVO.getPrmlvName(), rowIndex, PsndocWadocVO.PK_WA_CRT_SHOWNAME);
		this.setBodyCellValue(crtVO.getPk_wa_crt(), rowIndex, PsndocWadocVO.PK_WA_CRT);
		this.setBodyCellValue(crtVO.getPk_wa_prmlv(), rowIndex, PsndocWadocVO.PK_WA_PRMLV);
		this.setBodyCellValue(crtVO.getPk_wa_seclv(), rowIndex, PsndocWadocVO.PK_WA_SECLV);
		UFDouble strCriterionvalue = crtVO.getCriterionvalue();
		if (strCriterionvalue != null) {
			this.setBodyCellValue(strCriterionvalue.doubleValue(), rowIndex, PsndocWadocVO.NMONEY);
			this.setBodyCellValue(strCriterionvalue.doubleValue(), rowIndex, PsndocWadocVO.CRITERIONVALUE);

			if (this.isRangeCkeck) {
				dblMinValue = crtVO.getMin_value().doubleValue();
				dblMaxValue = crtVO.getMax_value().doubleValue();
				setBodyCellValue(new UFDouble(dblMinValue), rowIndex, PsndocWadocVO.CRT_MIN_VALUE);
				setBodyCellValue(new UFDouble(dblMaxValue), rowIndex, PsndocWadocVO.CRT_MAX_VALUE);
			}
		}
	}

	/**
	 * 如果时谈判工资， 则档别级别等不可编辑
	 * 
	 * @param rowIndex
	 */
	private void changeNegotiationWage(int rowIndex) {
		String[] neededClearKeys = {
				// 薪资标准类别
				PsndocWadocVO.PK_WA_GRD, PsndocWadocVO.PK_WA_GRD_SHOWNAME,
				// 申请薪资
				PsndocWadocVO.PK_WA_PRMLV, PsndocWadocVO.PK_WA_SECLV, PsndocWadocVO.CRITERIONVALUE,
				PsndocWadocVO.PK_WA_CRT_SHOWNAME };
		for (String strKey : neededClearKeys) {
			setBodyCellValue(null, rowIndex, strKey);
		}

		Object object = getBodyCellValue(rowIndex, PsndocWadocVO.NEGOTIATION_WAGE);
		if (object != null) {
			negotiation_wage = (Boolean) object;
		}
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
		// if (PsndocwadocCommonDef.ADD_STATE.equals(getModelState()) ||
		// PsndocwadocCommonDef.INSERT_STATE.equals(getModelState()) ||
		// PsndocwadocCommonDef.MODIFY_STATE.equals(getModelState()))
		// {
		// getBillPane().getTable().setRowSelectionInterval(last_select_row_number,
		// last_select_row_number);
		// return;
		// }
		int row = getBillPane().getTable().getSelectedRow();

		if (row > -1) {
			if (PsndocwadocCommonDef.UNKNOWN_STATE.equals(getModelState())
					|| PsndocwadocCommonDef.DELETE_STATE.equals(getModelState())) {
				((PsndocwadocAppModel) getModel()).setUiState(UIState.NOT_EDIT);
			} else if (PsndocwadocCommonDef.ADD_STATE.equals(getModelState())
					|| PsndocwadocCommonDef.INSERT_STATE.equals(getModelState())
					|| PsndocwadocCommonDef.MODIFY_STATE.equals(getModelState())) {
				((PsndocwadocAppModel) getModel()).setUiState(UIState.EDIT);
			}

			Boolean object = (Boolean) getBodyCellValue(row, PsndocWadocVO.NEGOTIATION_WAGE);
			negotiation_wage = false;
			if (object != null && object.booleanValue()) {
				negotiation_wage = true;
			}

			Object pk_wa_grd = getBodyCellValue(row, PsndocWadocVO.PK_WA_GRD);

			// 薪资项目
			pkWaItem = (String) getBodyCellValue(row, PsndocWadocVO.PK_WA_ITEM);

			if (pk_wa_grd != null) {
				refreshGradeRef(PsndocWadocVO.PK_WA_GRD_SHOWNAME, new WaGradeRefModel(pkWaItem, this.getModel()
						.getContext().getPk_org()));
				refreshCriterion(pk_wa_grd.toString(), row);
			}
		}
	}

	/**
	 * 刷新标准类别参照
	 * 
	 * @author xuhw
	 * @param colKey
	 * @param refModel
	 */
	private void refreshGradeRef(String strColKey, AbstractRefModel refModel) {
		// 根据列的字段找到对应的列的INDEX
		int intColKeyIndex = billModel.getItemIndex(strColKey);
		UIRefPane refPane = (UIRefPane) billModel.getBodyItems()[intColKeyIndex].getComponent();
		refModel.setPk_org(getModel().getContext().getPk_org());
		refPane.setRefModel(refModel);
		refPane.addValueChangedListener(this);
		refPane.setButtonFireEvent(true);
		// refPane.getRefModel().addWherePart(" and wa_grade.pk_wa_item = '" +
		// pkWaItem + "'", true);
		// refPane.getRefModel().reloadData();
	}

	/**
	 * 刷新标准项目参照
	 * 
	 * @author xuhw
	 * @param colKey
	 * @param refModel
	 */
	private void refreshItemRef() {
		// 根据列的字段找到对应的列的INDEX
		UIRefPane refpane = (UIRefPane) billModel.getBodyItems()[PK_WA_ITEM_INDEX].getComponent();
		WaItemRefModelForGrade refmodel = new WaItemRefModelForGrade();
		refmodel.setPkOrg(this.getModel().getContext().getPk_org());
		refmodel.setPk_org(getModel().getContext().getPk_org());
		refpane.setRefModel(refmodel);
		refpane.addValueChangedListener(this);
		refpane.setButtonFireEvent(true);
		refpane.setText(null);
		refpane.setReturnCode(true);
		refpane.setRefInputType(0);
		refpane.setReturnCode(false);
	}

	/**
	 * 根据状态取得表体单元格可否编辑Map
	 * 
	 * @author xuhw on 2010-1-6
	 * @param state
	 * @param negotiation_wage
	 * @return
	 */
	private Map<String, Boolean> getStateHash(String state, boolean negotiation_wage) {
		if (negotiation_wage) {
			if (PsndocwadocCommonDef.ADD_STATE.equals(state)) {
				return NEGOTIATION_WAGEADD_STATE;

			} else if (PsndocwadocCommonDef.INSERT_STATE.equals(state)) {
				return NEGOTIATION_WAGEINSERT_STATE;
			} else if (PsndocwadocCommonDef.MODIFY_STATE.equals(state)) {
				return NEGOTIATION_WAGEMODIFY_STATE;
			}
		} else {
			if (PsndocwadocCommonDef.ADD_STATE.equals(state)) {
				return WAGEADD_STATE;
			} else if (PsndocwadocCommonDef.INSERT_STATE.equals(state)) {
				return WAGEINSERT_STATE;
			} else if (PsndocwadocCommonDef.MODIFY_STATE.equals(state)) {
				return WAGEMODIFY_STATE;
			}
		}
		return null;
	}

	/**
	 * 设置单元格能否编辑
	 * 
	 * @author xuhw on 2009-12-23
	 */
	private void setCellEditableController() {
		getBillCardPanel().getBillModel().setCellEditableController(new BillModelCellEditableController() {
			public boolean isCellEditable(boolean blnIsEditable, int iRowIndex, String strItemKey) {
				return isEditable(blnIsEditable, iRowIndex, strItemKey);
			}
		});
	}

	/**
	 * 得到主表中的字段值
	 */
	private Object getBodyCellValue(int rowIndex, String strKey) {
		return billModel.getValueAt(rowIndex, strKey);
	}

	/**
	 * 对主表的字段进行赋值
	 * 
	 * @param aValue
	 * @param rowIndex
	 * @param strKey
	 */
	private void setBodyCellValue(Object aValue, int rowIndex, String strKey) {
		billModel.setValueAt(aValue, rowIndex, billModel.getBodyColByKey(strKey));
	}

	/** 取得自定义状态 */
	private String getModelState() {
		return ((PsndocwadocAppModel) getModel()).getState();
	}

	public BillScrollPane getBillScrollPane() {
		return getBillPane();
	}

	public PsndocWadocVO[] getWadocData() {
		return subVOs;
	}

	private int findColIndex(String keyName) {
		return billModel.getBodyColByKey(keyName);
	}

	@Override
	public void handleEvent(AppEvent event) {
		// ssx added on 2018-11-14
		// for multi-selection for wadoc
		if (this.isHeadBatch()) {
			for (AbstractAction action : getTabActions()) {
				if (action instanceof CancelPsndocwadocAction || action instanceof SavePsndocwadocAction) {
					action.setEnabled(false);
				}
			}
			return;
		} else {
			for (AbstractAction action : getTabActions()) {
				if (action instanceof CancelPsndocwadocAction || action instanceof SavePsndocwadocAction) {
					action.setEnabled((PsndocwadocCommonDef.ADD_STATE.equals(getModelState())
							|| PsndocwadocCommonDef.INSERT_STATE.equals(getModelState()) || PsndocwadocCommonDef.MODIFY_STATE
							.equals(getModelState())));
				}
			}
		}
		//

		if (AppEventConst.MODEL_INITIALIZED.equalsIgnoreCase(event.getType())) {
			// 由于上面的代码使初始化表头失效，所以在这里初始化多表头
			// 初始化页面上的多表头信息
			UIRefPane changeCauseRefpane = (UIRefPane) getBillCardPanel().getBodyItem(PsndocWadocVO.DOCNAME)
					.getComponent();
			changeCauseRefpane.getRefModel().setPk_org(getModel().getContext().getPk_org());
		}

		else if (PsndocwadocCommonDef.ADD_STATE.equals(getModelState())
				|| PsndocwadocCommonDef.INSERT_STATE.equals(getModelState())
				|| PsndocwadocCommonDef.MODIFY_STATE.equals(getModelState())) {
			setEditable(true);
		} else {
			setEditable(false);
		}

		setCellEditableController();
	}

	/**
	 * @author xuhw on 2010-5-10
	 * @see nc.ui.pub.beans.ValueChangedListener#valueChanged(nc.ui.pub.beans.ValueChangedEvent)
	 */
	@Override
	public void valueChanged(ValueChangedEvent event) {
		getBillCardPanel().stopEditing();
	}

	/**
	 * 得到表体的字段信息
	 * 
	 * @param strKey
	 * @return
	 */
	public Object getBodyCellObject(String strKey) {
		return this.getBillCardPanel().getBodyItem(strKey).getComponent();
	}

	/**
	 * @author xuhw on 2010-5-31
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

	}

	public WaCriterionVO getCrtVO() {
		return crtVO;
	}

	public void setCrtVO(WaCriterionVO crtVO) {
		this.crtVO = crtVO;
	}

	@Override
	public boolean beforeEdit(BillEditEvent e) {
		// TODO Auto-generated method stub
		if (((BillItem) e.getSource()).getKey().equals("docname")) {
			UIRefPane changeCauseRefpane = (UIRefPane) ((BillItem) e.getSource()).getComponent();
			changeCauseRefpane.getRefModel().setPk_org(getModel().getContext().getPk_org());
			changeCauseRefpane.getRefModel().addWherePart(" and pk_defdoclist = '1002Z710000000004MO1' ", true);
		}
		return true;
	}
}