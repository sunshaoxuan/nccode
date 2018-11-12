package nc.ui.wa.ref;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.ui.hr.frame.dialog.HrDialog;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITable;
import nc.ui.pub.beans.table.ColumnGroup;
import nc.ui.pub.beans.table.GroupableTableHeader;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillMouseEnent;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.bill.BillTableMouseListener;
import nc.ui.pub.bill.IBillItem;
import nc.ui.wa.grade.model.WaGradeBillModel;
import nc.ui.wa.salaryadjmgt.WASalaryadjmgtDelegator;
import nc.vo.om.pub.SuperVOHelper;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.grade.AggWaGradeVO;
import nc.vo.wa.grade.CrtVO;
import nc.vo.wa.grade.IWaGradeCommonDef;
import nc.vo.wa.grade.IWaGradeCommonDef.GradeSetType;
import nc.vo.wa.grade.WaCriterionVO;
import nc.vo.wa.grade.WaGradeVerVO;
import nc.vo.wa.grade.WaPrmlvVO;
import nc.vo.wa.grade.WaSeclvVO;
import nc.vo.wa.item.WaItemVO;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author: xuhw
 * @date: 2010-8-10 下午02:36:30
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WAGradePrmSecDia extends HrDialog {
	/** 是否多档 */
	private boolean isMultsecCkeck = false;
	/** 是否宽带薪酬 */
	private boolean isRangeCkeck = false;
	GradeSetType gradeSetType = null;
	/** 薪资档别VO数组 */
	private WaSeclvVO[] seclvVos = null;

	/** 薪资级别VO数组 */
	private WaPrmlvVO[] prmlvVos = null;

	private WaCriterionVO[] crtVos = null;

	private Map<String, WaPrmlvVO> prmlvCache = null;
	private Map<String, WaSeclvVO> seclvCache = null;
	private AggWaGradeVO aggvo = null;
	/** 项目类型 */
	private Integer[] saClsBodyColType = null;
	/** 项目名称的设定 */
	private final List<String> lisColName = new ArrayList<String>();
	/** 项目key的设定 */
	private final List<String> lisKeyName = new ArrayList<String>();
	/** 项目类型设定 */
	private final List<Integer> lisColType = new ArrayList<Integer>();
	/** 项目名字 */
	private String[] saClsBodyColName = null;
	/** 项目key */
	private String[] saClsBodyColKeyName = null;
	private final HashMap<String, List<String>> colNameMap = new HashMap<String, List<String>>();
	private BillScrollPane cardPanel;
	/** 薪资标准设置表主表PK */
	private String strPkWaGrd = null;

	private WaCriterionVO selectVO;

	/**
	 * @author xuhw on 2010-8-10
	 * @param parent
	 */
	public WAGradePrmSecDia(Container parent, String strPkWaGrd,
			boolean isMultsecCkeck, boolean isRangeCkeck) {
		super(parent, ResHelper.getString("6013salaryadjmgt",
				"06013salaryadjmgt0128")/* @res "薪资标准表" */);
		this.isMultsecCkeck = isMultsecCkeck;
		this.isRangeCkeck = isRangeCkeck;
		this.strPkWaGrd = strPkWaGrd;
		initGradeType();
		createWaCrtTable();
	}

	/**
	 * @author xuhw on 2010-8-10
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @author xuhw on 2010-8-10
	 * @see nc.ui.hr.frame.dialog.HrDialog#createCenterPanel()
	 */
	@Override
	protected JComponent createCenterPanel() {
		return getBillCardPanel();
	}

	/**
	 * 生成薪资标准值页签<BR>
	 * 四种类型<BR>
	 * 1)多级 非多档 非宽带薪酬<BR>
	 * 2)多级 非多档 宽带薪酬<BR>
	 * 3)多级 多档 宽带薪酬<BR>
	 * 4)多级 多档 宽带薪酬<BR>
	 * 
	 * @author xuhw on 2009-11-24
	 * @see nc.ui.uif2.NCAction#doAction(java.awt.event.ActionEvent)
	 */
	public void createWaCrtTable() {
		try {
			// 多级 非多档 非宽带薪酬
			if (gradeSetType == GradeSetType.SingleSeclv) {
				initPrmlvTable();
			}
			// 多级 非多档 宽带薪酬
			else if (gradeSetType == GradeSetType.SingleSeclvRange) {
				initPrmlvRangeTable();
			}
			// 多级 多档 非宽带薪酬
			else if (gradeSetType == GradeSetType.MultSeclv) {
				initPrmlvReclvTable();
			}
			// 多级 多档 宽带薪酬
			else {
				initPrmlvReclvRangeTable();
			}
		} catch (Exception ex) {
			Logger.error("生成薪资类别标准值页签时候出现异常", ex);
			return;
		}
	}

	/**
	 * 四种类型<BR>
	 * 1)多级 非多档 非宽带薪酬<BR>
	 * 2)多级 非多档 宽带薪酬<BR>
	 * 3)多级 多档 宽带薪酬<BR>
	 * 4)多级 多档 宽带薪酬<BR>
	 * 
	 */
	private void initGradeType() {
		// 多级 非多档 非宽带薪酬
		if (!isMultsecCkeck && !isRangeCkeck) {
			gradeSetType = GradeSetType.SingleSeclv;
		}
		// 多级 非多档 宽带薪酬
		else if (!isMultsecCkeck && isRangeCkeck) {
			gradeSetType = GradeSetType.SingleSeclvRange;
		}
		// 多级 多档 非宽带薪酬
		else if (isMultsecCkeck && !isRangeCkeck) {
			gradeSetType = GradeSetType.MultSeclv;
		}
		// 多级 多档 宽带薪酬
		else {
			gradeSetType = GradeSetType.MultSeclvRange;
		}

		// 薪资档别VO数组
		try {
			aggvo = new WaGradeBillModel().queryAggWagradeByGrdPK(strPkWaGrd);
			seclvVos = (WaSeclvVO[]) aggvo
					.getTableVO(IWaGradeCommonDef.WA_SECLV);
			prmlvVos = (WaPrmlvVO[]) aggvo
					.getTableVO(IWaGradeCommonDef.WA_PRMLV);
			crtVos = (WaCriterionVO[]) aggvo
					.getTableVO(IWaGradeCommonDef.WA_CRT);

			// 2016-12-2 zhousze 薪资加密：这里处理薪资标准表弹出框金额数据解密 beigin
			for (WaCriterionVO vo : crtVos) {
				vo.setCriterionvalue(new UFDouble(
						SalaryDecryptUtil.decrypt(new UFDouble(vo
								.getCriterionvalue() == null ? new UFDouble(0)
								: vo.getCriterionvalue()).toDouble())));
				vo.setMax_value(new UFDouble(SalaryDecryptUtil
						.decrypt(new UFDouble(
								vo.getMax_value() == null ? new UFDouble(0)
										: vo.getMax_value()).toDouble())));
				vo.setMin_value(new UFDouble(SalaryDecryptUtil
						.decrypt(new UFDouble(
								vo.getMin_value() == null ? new UFDouble(0)
										: vo.getMin_value()).toDouble())));
			}
			// end

			prmlvCache = SuperVOHelper.buildAttributeToVOMap(
					WaPrmlvVO.LEVELNAME, prmlvVos);

			seclvCache = SuperVOHelper.buildAttributeToVOMap(
					WaSeclvVO.PK_WA_SECLV, seclvVos);
		} catch (BusinessException e) {
		}
	}

	/**
	 * 创建Table(多级 非多档 非宽带)
	 * 
	 * @throws BusinessException
	 */
	private void initPrmlvTable() throws BusinessException {

		if (seclvVos == null || seclvVos.length < 1) {
			// 档别的名称
			lisColName.add(ResHelper.getString("6013salaryadjmgt",
					"06013salaryadjmgt0129")/* @res "级别" */);
			// 档别key
			lisKeyName.add(WaCriterionVO.PRMLVNAME);
			// 档别类型 币值型
			lisColType.add(BillItem.STRING);
			// 档别的名称
			lisColName.add(ResHelper.getString("common", "UC000-0004112")/*
																		 * @res
																		 * "金额"
																		 */);
			// 档别key
			lisKeyName.add("criterionvalue");
			// 档别类型 币值型
			lisColType.add(BillItem.DECIMAL);

		}

		// 创建项目
		createCrtItems(lisColName, lisKeyName, lisColType);
	}

	/**
	 * 创建Table(多级 宽带 非多档)
	 * 
	 * @throws BusinessException
	 */
	private void initPrmlvRangeTable() throws BusinessException {
		if (seclvVos == null || seclvVos.length < 1) {

			// 档别的名称
			lisColName.add(ResHelper.getString("6013salaryadjmgt",
					"06013salaryadjmgt0129")/* @res "级别" */);
			// 档别key
			lisKeyName.add(WaCriterionVO.PRMLVNAME);
			// 档别类型 币值型
			lisColType.add(BillItem.STRING);
			// 名称的设定
			lisColName.add(ResHelper.getString("6013salaryadjmgt",
					"06013salaryadjmgt0130")/* @res "下限" */);
			lisColName.add(ResHelper.getString("6013salaryadjmgt",
					"06013salaryadjmgt0131")/* @res "基准值" */);
			lisColName.add(ResHelper.getString("6013salaryadjmgt",
					"06013salaryadjmgt0132")/* @res "上限" */);

			// key的设定
			// 档别的金额下限
			lisKeyName.add("pran_down_criterionvalue");
			// 档别的金额基准值
			lisKeyName.add("pran_basic_criterionvalue");
			// 档别的金额上限
			lisKeyName.add("pran_up_criterionvalue");

			// 类型的设定——币值型
			lisColType.add(BillItem.DECIMAL);
			lisColType.add(BillItem.DECIMAL);
			lisColType.add(BillItem.DECIMAL);
		}

		// 创建项目
		createCrtItems(lisColName, lisKeyName, lisColType);

		initColumnGroup(getColumnGroupHash(ResHelper.getString(
				"6013salaryadjmgt", "06013salaryadjmgt0133")/* @res "一档" */,
				"pran_down_criterionvalue", "pran_basic_criterionvalue",
				"pran_up_criterionvalue"));
	}

	/**
	 * 创建Table(多级 多档 非宽带)
	 * 
	 * @throws BusinessException
	 */
	private void initPrmlvReclvTable() throws BusinessException {
		if (seclvVos == null) {
			initPrmlvTable();
			return;
		}

		// 档别的名称
		lisColName.add(ResHelper.getString("6013salaryadjmgt",
				"06013salaryadjmgt0134")/* @res "档别" */);
		// 档别key
		lisKeyName.add(WaCriterionVO.PRMLVNAME);
		// 档别类型 币值型
		lisColType.add(BillItem.STRING);

		for (int i = 0; i < seclvVos.length; i++) {
			// 档别的name设定
			lisColName.add(seclvVos[i].getLevelname());
			// 档别的金额基准值key
			lisKeyName.add("prec_" + seclvVos[i].getPk_wa_seclv());
			// 档别类型的设定
			lisColType.add(BillItem.DECIMAL);
		}

		// 创建项目
		createCrtItems(lisColName, lisKeyName, lisColType);
	}

	/**
	 * 创建Table(多级 多档 宽带)
	 * 
	 * @throws BusinessException
	 */
	private void initPrmlvReclvRangeTable() throws BusinessException {
		if (seclvVos == null) {
			initPrmlvRangeTable();
			return;
		}

		// 档别的名称
		lisColName.add(ResHelper.getString("6013salaryadjmgt",
				"06013salaryadjmgt0129")/* @res "级别" */);
		// 档别key
		lisKeyName.add(WaCriterionVO.PRMLVNAME);
		// 档别类型 币值型
		lisColType.add(BillItem.STRING);
		for (int i = 0; i < seclvVos.length; i++) {
			// 名称的设定
			lisColName.add(ResHelper.getString("6013salaryadjmgt",
					"06013salaryadjmgt0130")/* @res "下限" */);
			lisColName.add(ResHelper.getString("6013salaryadjmgt",
					"06013salaryadjmgt0131")/* @res "基准值" */);
			lisColName.add(ResHelper.getString("6013salaryadjmgt",
					"06013salaryadjmgt0132")/* @res "上限" */);
			// key的设定
			// 档别的金额下限
			lisKeyName.add("precran_down_" + seclvVos[i].getPk_wa_seclv());
			// 档别的金额基准值
			lisKeyName.add("precran_basic_" + seclvVos[i].getPk_wa_seclv());
			// 档别的金额上限
			lisKeyName.add("precran_up_" + seclvVos[i].getPk_wa_seclv());

			// 类型的设定——币值型
			lisColType.add(BillItem.DECIMAL);
			lisColType.add(BillItem.DECIMAL);
			lisColType.add(BillItem.DECIMAL);
		}

		// 创建项目
		createCrtItems(lisColName, lisKeyName, lisColType);
		for (int i = 0; i < seclvVos.length; i++) {
			initColumnGroup(getColumnGroupHash(seclvVos[i].getLevelname(),
					"precran_down_" + seclvVos[i].getPk_wa_seclv(),
					"precran_basic_" + seclvVos[i].getPk_wa_seclv(),
					"precran_up_" + seclvVos[i].getPk_wa_seclv()));
		}
	}

	/**
	 * 创建薪资标准类别值页签
	 * 
	 * @author xuhw on 2009-11-26
	 * @throws BusinessException
	 */
	private void createCrtItems(List<String> lisColName,
			List<String> lisKeyName, List<Integer> lisColType)
			throws BusinessException {
		saClsBodyColName = new String[lisColName.size()];
		saClsBodyColKeyName = new String[lisKeyName.size()];
		saClsBodyColType = new Integer[lisColType.size()];
		lisColName.toArray(saClsBodyColName);
		lisKeyName.toArray(saClsBodyColKeyName);
		lisColType.toArray(saClsBodyColType);
		// liang 取薪资项目的小数位数,设置标准表的小数位数
		int iflddecimal = IBillItem.DEFAULT_DECIMAL_DIGITS;
		if (!StringUtils.isBlank(strPkWaGrd)) {
			WaItemVO itemvo = WASalaryadjmgtDelegator.getGradeQueryService()
					.getItemVOByGrdPk(strPkWaGrd);
			iflddecimal = itemvo.getIflddecimal();
		}
		// end
		BillItem[] biaBody = new BillItem[saClsBodyColName.length];
		for (int i = 0; i < saClsBodyColName.length; i++) {
			biaBody[i] = new BillItem();
			biaBody[i].setName(saClsBodyColName[i]);
			biaBody[i].setKey(saClsBodyColKeyName[i]);
			biaBody[i].setWidth(100);
			biaBody[i].setEnabled(false);
			biaBody[i].setEdit(false);
			biaBody[i].setNull(false);
			if (saClsBodyColType[i] == BillItem.STRING) {
				// 字符型
				biaBody[i].setDataType(BillItem.STRING);
				biaBody[i].setEdit(false);
				biaBody[i].setEnabled(false);
			} else if (saClsBodyColType[i] == BillItem.DECIMAL) {
				// 币值型
				biaBody[i].setDataType(BillItem.DECIMAL);
				biaBody[i].setDecimalDigits(iflddecimal);
				biaBody[i].setLength(27);
				((UIRefPane) biaBody[i].getComponent()).getUITextField()
						.setMinValue(0);
			}
		}
		// 薪资标准值model
		BillModel bmCrt = new BillModel();
		bmCrt.setBodyItems(biaBody);
		getBillCardPanel().setTableModel(bmCrt);
		getBillCardPanel().addMouseListener(new BillTableMouseListener() {
			@Override
			public void mouse_doubleclick(BillMouseEnent evt) {
				okButtonActionPerformed();
			}
		});
		UITable table = getBillCardPanel().getTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.removeSortListener();
		initCrtTableData(isMultsecCkeck, strPkWaGrd);
		// 自动对列的宽度进行调整的话，如果列的数量太多的时候就不会有滚动条，而且特别拥挤显得
		// table.setAutoResizeMode(table.AUTO_RESIZE_LAST_COLUMN);
	}

	private BillScrollPane getBillCardPanel() {
		if (cardPanel == null) {
			cardPanel = new BillScrollPane();
		}
		return cardPanel;
	}

	/**
	 * 初始化表体的数据
	 */
	private void initCrtTableData(boolean isMultsec, String strPkWaGrd)
			throws BusinessException {
		WaGradeVerVO gradever = new WaGradeBillModel().queryCriterionByClassid(
				isMultsec, strPkWaGrd);
		// 2017-10-11 zhousze 薪资加密：这里处理薪资标准表界面数据解密 begin
		CrtVO[] crtVOs = gradever.getCrtVOs();
		for (CrtVO vo : crtVOs) {
			ArrayList<Object> list = (ArrayList<Object>) vo.getVecData();
			for (Object waCriVO : list) {
				WaCriterionVO vo1 = (WaCriterionVO) waCriVO;
				vo1.setCriterionvalue(new UFDouble(
						SalaryDecryptUtil
								.decrypt((vo1.getCriterionvalue() == null ? new UFDouble(
										0) : vo1.getCriterionvalue())
										.toDouble())));
				vo1.setMax_value(new UFDouble(SalaryDecryptUtil.decrypt((vo1
						.getMax_value() == null ? new UFDouble(0) : vo1
						.getMax_value()).toDouble())));
				vo1.setMin_value(new UFDouble(SalaryDecryptUtil.decrypt((vo1
						.getMin_value() == null ? new UFDouble(0) : vo1
						.getMin_value()).toDouble())));
			}
		}
		// end
		getBillModel().setBodyDataVO(crtVOs);
		// getBillCardPanel().getTableModel().setSortEnabled(false);
		// getGradeModel().setCrtvos(crtvos);
	}

	/**
	 * 取得billmodel<BR>
	 * <BR>
	 * 
	 * @author xuhw on 2010-3-31
	 * 
	 * @return
	 */
	private BillModel getBillModel() {
		return getBillCardPanel().getTableModel();
	}

	/**
	 * 得到合并的多表头的分组字段
	 * 
	 * @return
	 */
	private HashMap<String, List<String>> getColumnGroupHash(
			String strHeadName, String strUp, String strBasic, String strDown) {
		// 原薪资信息
		String oldGroupColName = strHeadName;
		List<String> oldKeys = new ArrayList<String>();
		oldKeys.add(strUp);
		oldKeys.add(strBasic);
		oldKeys.add(strDown);
		colNameMap.put(oldGroupColName, oldKeys);
		return colNameMap;
	}

	/**
	 * 设置多表头
	 * 
	 * @param colNameMap
	 */
	private void initColumnGroup(HashMap<String, List<String>> colNameMap) {
		Iterator<String> iterator = colNameMap.keySet().iterator();
		while (iterator.hasNext()) {
			String groupColName = iterator.next();
			List<String> groupedColKeys = colNameMap.get(groupColName);
			// 将多表头合并
			setColumnGroup(groupColName, groupedColKeys);
		}
	}

	/**
	 * 合并多表头
	 * 
	 * @param groupColName
	 * @param groupedColKeys
	 */
	private void setColumnGroup(String groupColName, List<String> groupedColKeys) {
		GroupableTableHeader header = (GroupableTableHeader) getBillCardPanel()
				.getTable().getTableHeader();

		ColumnGroup colGroup = new ColumnGroup(groupColName);
		for (String colKey : groupedColKeys) {
			colGroup.add(tableColumn(colKey));
		}
		header.addColumnGroup(colGroup);

		getBillCardPanel().getTable().setTableHeader(header);
		getBillModel();
	}

	/**
	 * 根据列colKey找到对应的列
	 * 
	 * @param colKey
	 * @return
	 */
	private TableColumn tableColumn(String colKey) {
		int colIndex = getBillModel().getBodyColByKey(colKey);
		TableColumnModel cm = getBillCardPanel().getTable().getColumnModel();
		return cm.getColumn(colIndex);
	}

	private void okButtonActionPerformed() {
		int colIndex = getBillCardPanel().getTable().getSelectedColumn();
		int rowIndex = getBillCardPanel().getTable().getSelectedRow();
		if (getBillCardPanel().getLockCol() >= 0 && colIndex >= 0) {
			colIndex = colIndex + getBillCardPanel().getLockCol() + 1;
		}

		if (colIndex <= 0 || rowIndex < 0) {
			return;
		}
		// 档别
		String pk_wa_seclv = getBillCardPanel().getTableModel().getBodyItems()[colIndex]
				.getKey();
		// String sec_name =
		// getBillCardPanel().getTableModel().getBodyItems()[colIndex].getName();
		// 级别
		String pk_wa_prmlv = getBillCardPanel().getTableModel()
				.getValueAt(rowIndex, WaCriterionVO.PRMLVNAME).toString();
		String prm_name = getBillCardPanel().getTableModel()
				.getValueAt(rowIndex, WaCriterionVO.PRMLVNAME).toString();

		if (crtVos == null) {
			MessageDialog.showErrorDlg(this, null, ResHelper.getString(
					"6013salaryadjmgt", "06013salaryadjmgt0142"));// "请先设置薪资标准表版本信息！"
			return;

		}
		for (WaCriterionVO criterionVO : crtVos) {
			if (criterionVO.getPk_wa_prmlv().equals(
					prmlvCache.get(pk_wa_prmlv).getPk_wa_prmlv())
					&& (criterionVO.getPk_wa_seclv() == null || "~"
							.equals(criterionVO.getPk_wa_seclv()))) {
				criterionVO.setPrmlvName(prm_name);
				setSelectVO(criterionVO);
				break;
			} else if (criterionVO.getPk_wa_prmlv().equals(
					prmlvCache.get(pk_wa_prmlv).getPk_wa_prmlv())
					&& pk_wa_seclv.indexOf(criterionVO.getPk_wa_seclv()) != -1) {
				criterionVO.setPrmlvName(prm_name
						+ "/"
						+ seclvCache.get(criterionVO.getPk_wa_seclv())
								.getLevelname());
				setSelectVO(criterionVO);
				break;
			}
		}

		setResult(UIDialog.ID_OK);
		this.dispose();
	}

	public WaCriterionVO getSelectVO() {
		return selectVO;
	}

	public void setSelectVO(WaCriterionVO selectVO) {
		this.selectVO = selectVO;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == btnCancel) {
			closeCancel();
		} else if (evt.getSource() == btnOk) {
			// closeOK();
			okButtonActionPerformed();
		}
	}

}