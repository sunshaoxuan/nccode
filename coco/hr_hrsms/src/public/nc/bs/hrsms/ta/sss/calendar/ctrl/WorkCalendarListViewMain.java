package nc.bs.hrsms.ta.sss.calendar.ctrl;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dbcache.intf.IDBCacheBS;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.common.ctrl.BURefController;
import nc.bs.hrsms.ta.sss.calendar.WorkCalendarConsts;
import nc.bs.hrsms.ta.sss.calendar.common.ExcelExportUtils;
import nc.bs.hrsms.ta.sss.calendar.common.WorkCalendarCommonValue;
import nc.bs.hrsms.ta.sss.calendar.pagemodel.BatchChangeShiftPageModel;
import nc.bs.hrsms.ta.sss.calendar.pagemodel.WorkCalendarForPsnPageModel;
import nc.bs.hrsms.ta.sss.common.ShopTAUtil;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.ServiceLocator;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.exception.HrssException;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.DatasetUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.pub.tool.ViewUtil;
import nc.bs.hrss.pub.tool.qry.QueryUtil;
import nc.bs.hrss.ta.calendar.CalendarUtils;
import nc.bs.hrss.ta.common.ctrl.BaseController;
import nc.bs.hrss.ta.utils.TBMPeriodUtil;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.ta.IPsnCalendarManageMaintain;
import nc.itf.ta.IPsnCalendarQueryMaintain;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.md.model.type.IType;
import nc.uap.ctrl.tpl.qry.FromWhereSQLImpl;
import nc.uap.lfw.core.LfwRuntimeEnvironment;
import nc.uap.lfw.core.bm.ButtonStateManager;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.common.DataTypeTranslator;
import nc.uap.lfw.core.common.EditorTypeConst;
import nc.uap.lfw.core.common.StringDataTypeConst;
import nc.uap.lfw.core.comp.FormComp;
import nc.uap.lfw.core.comp.GridColumn;
import nc.uap.lfw.core.comp.GridColumnGroup;
import nc.uap.lfw.core.comp.GridComp;
import nc.uap.lfw.core.comp.IGridColumn;
import nc.uap.lfw.core.comp.MenuItem;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.ctx.ApplicationContext;
import nc.uap.lfw.core.ctx.ViewContext;
import nc.uap.lfw.core.data.Dataset;
import nc.uap.lfw.core.data.Field;
import nc.uap.lfw.core.data.FieldRelation;
import nc.uap.lfw.core.data.MatchField;
import nc.uap.lfw.core.data.PaginationInfo;
import nc.uap.lfw.core.data.Row;
import nc.uap.lfw.core.data.RowData;
import nc.uap.lfw.core.data.WhereField;
import nc.uap.lfw.core.event.DataLoadEvent;
import nc.uap.lfw.core.event.DatasetCellEvent;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.uap.lfw.core.model.plug.TranslatedRow;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.refnode.NCRefNode;
import nc.uap.lfw.core.refnode.RefNodeGenerator;
import nc.ui.ta.calendar.pub.CalendarColorUtils;
import nc.ui.ta.pub.IColorConst;
import nc.vo.bd.holiday.HolidayVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hrss.pub.SessionBean;
import nc.vo.hrss.ta.calendar.QryConditionVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class WorkCalendarListViewMain extends BaseController {

	private static final int MAX_QUERY_DAYS = 60;

	// 后缀 - 参照的显示字段
	private static final String REF_SUFFIX = "_name";

	// 前缀-数据集字段
	private static final String COLOR_PREFIX = "color_";
	// 前缀-数据集字段
	private static final String EDITABLE_PREFIX = "edit_";

	// 前缀-表格多表头
	private static final String CLOUM_GROUP_PREFIX = "group_";
	// 前缀 - 表格
	private static final String CLOUM_PREFIX = "col_";
	// 引用数据集-班次
	private static final String REFDS_PK_SHIFT_NAME = "$refds_uap_shift";
	// 参数 - 人员的PsnJobCalendarVO数组
	public static final String SESSION_PSNJOB_CALENDARVOS = "ci_PsnJobCalendarVOs";
	// 周数组
	private static String[] WEEKARRAY = new String[] { "0c_ta-res0131"/*
																	 * @ res
																	 * "周日"
																	 */, "0c_ta-res0132"/*
																						 * @
																						 * res
																						 * "周一"
																						 */, "0c_ta-res0133"/*
																											 * @
																											 * res
																											 * "周二"
																											 */,
			"0c_ta-res0134"/* @ res "周三" */, "0c_ta-res0135"/* @ res "周四" */, "0c_ta-res0136"/*
																							 * @
																							 * res
																							 * "周五"
																							 */, "0c_ta-res0137"/*
																												 * @
																												 * res
																												 * "周六"
																												 */};

	/**
	 * 管理部门变更
	 * 
	 * @param keys
	 * @throws BusinessException
	 */
	public void pluginDeptChange(Map<String, Object> keys) throws BusinessException {
		LfwView simpQryView = getLifeCycleContext().getWindowContext()
				.getViewContext(HrssConsts.PAGE_SIMPLE_QUERY_WIDGET).getView();
		Dataset dsSearch = simpQryView.getViewModels().getDataset("mainds");
		if (dsSearch != null) {
			Row selRow = dsSearch.getSelectedRow();
			// 管理部门所在的HR组织
			String pk_hr_org = SessionUtil.getHROrg();
			UFLiteralDate[] dates = TBMPeriodUtil.getDefaultBeginEndDateByPkOrg(pk_hr_org);
			if (dsSearch.nameToIndex(WorkCalendarConsts.FD_BEGINDATE) > -1) {
				// 开始日期
				selRow.setValue(dsSearch.nameToIndex(WorkCalendarConsts.FD_BEGINDATE), String.valueOf(dates[0]));
			}
			if (dsSearch.nameToIndex(WorkCalendarConsts.FD_ENDDATE) > -1) {
				// 结束日期
				if (dates[1].after(dates[0].getDateAfter(60))) {
					dates[1] = dates[0].getDateAfter(60);
				}
				selRow.setValue(dsSearch.nameToIndex(WorkCalendarConsts.FD_ENDDATE), String.valueOf(dates[1]));
			}
			if (dsSearch.nameToIndex(WorkCalendarConsts.FD_ENDDATE) > -1) {
				// 排班条件
				selRow.setValue(dsSearch.nameToIndex(WorkCalendarConsts.FD_ARRANGEFLAG),
						String.valueOf(WorkCalendarConsts.QUERYSCOPE_ALL));
			}
			SessionUtil.getSessionBean().setExtendAttribute(WorkCalendarConsts.SESSION_QRY_CONDITIONS, null);
			// 清空数据集
			Dataset dsCalendar = getCurrentView().getViewModels().getDataset(
					WorkCalendarForPsnPageModel.DATASET_CALENDAR);
			DatasetUtil.clearData(dsCalendar);
			// 执行左侧快捷查询
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		}
	}

	/**
	 * 批量排班 关闭窗口刷新
	 * 
	 * @param keys
	 */
	public void plugininid_soci(Map<String, Object> keys) {
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * 循环排班 关闭窗口刷新
	 * 
	 * @param keys
	 */
	public void pluginCircleArrangeShift_inId(Map<String, Object> keys) {
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * 分类切换事件
	 * 
	 * @param keys
	 */
	public static void pluginCatagory(Map<String, Object> keys) {
		if (keys == null || keys.size() == 0) {
			return;
		}
		TranslatedRow row = (TranslatedRow) keys.get("key2");
		if (row == null) {
			return;
		}
		String pk_node = (String) row.getValue("pk_node");
		if (StringUtils.isEmpty(pk_node)) {
			return;
		}
		ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
		String url = LfwRuntimeEnvironment.getRootPath() + "/app/" + pk_node + "?"
				+ WorkCalendarConsts.SESSION_CATAGORY_ACCESS + "=1";
		if (WorkCalendarConsts.WORKCALENDARAPP_TIME.equals(pk_node)) {// 店员工作日历按时段查询
			url += "&nodecode=" + WorkCalendarConsts.FUNC_CODE;
		} else if (WorkCalendarConsts.WORKCALENDARAPP_CAL.equals(pk_node)) {
			url += "&nodecode=E20600933";
		}// E20600933
		appCtx.sendRedirect(url);
	}

	/**
	 * 日历数据集的加载事件,触发查询操作<br/>
	 * 页面初始化加载操作<br/>
	 * 
	 * @param dataLoadEvent
	 * @throws BusinessException
	 */
	public void onDataLoad_dsCalendar(DataLoadEvent dataLoadEvent) throws BusinessException {
		Dataset dsCalendar = dataLoadEvent.getSource();

		PaginationInfo pg = dsCalendar.getCurrentRowSet().getPaginationInfo();
		if (pg.getRecordsCount() > 0) { // 分页操作
			QryConditionVO vo = (QryConditionVO) SessionUtil.getSessionBean().getExtendAttributeValue(
					WorkCalendarConsts.SESSION_QRY_CONDITIONS);
			// 显示查询结果数据
			initData(getCurrentView(), dsCalendar, SessionUtil.getPk_mng_dept(), SessionUtil.isIncludeSubDept(),
					vo.getBeginDate(), vo.getEndDate(), vo.getArrangeflag(), vo.getFromWhereSQL());
		} else {
			// 执行左侧快捷查询
			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
		}
	}

	/**
	 * 点击搜索后,页面重新加载事件
	 * 
	 * @param keys
	 * @throws BusinessException
	 */
	public void pluginSearch(Map<String, Object> keys) throws BusinessException {
		if (keys == null || keys.size() == 0) {
			return;
		}

		LfwView viewMain = getCurrentView();
		Dataset dsCalendar = viewMain.getViewModels().getDataset(WorkCalendarForPsnPageModel.DATASET_CALENDAR);
		// 管理部门
		String pk_mng_dept = SessionUtil.getPk_mng_dept();
		if (StringUtils.isEmpty(pk_mng_dept)) {
			return;
		}
		// 是否包含下级部门
		boolean isContainSub = SessionUtil.isIncludeSubDept();
		QryConditionVO vo = getConditions(keys);
		// 根据开始日期和结束日期,构造数据集和表格
		initDsAndGrid(viewMain, dsCalendar, vo.getBeginDate(), vo.getEndDate());
		// 清空数据集
		DatasetUtil.clearData(dsCalendar);
		dsCalendar.setCurrentKey(Dataset.MASTER_KEY);
		dsCalendar.getCurrentRowSet().getPaginationInfo().setPageIndex(0);
		Dataset dsModifiedClass = viewMain.getViewModels().getDataset("dsModifiedClass");
		DatasetUtil.clearData(dsModifiedClass);
		// 显示查询结果数据
		initData(viewMain, dsCalendar, pk_mng_dept, isContainSub, vo.getBeginDate(), vo.getEndDate(),
				vo.getArrangeflag(), vo.getFromWhereSQL());
	}

	/**
	 * 获得查询条件.
	 * 
	 * @param keys
	 * @return
	 */
	public static QryConditionVO getConditions(Map<String, Object> keys) {
		ViewContext leftView = AppLifeCycleContext.current().getWindowContext().getViewContext("pubview_simplequery");
		FormComp searchForm = null;
		if (leftView != null && leftView.getView() != null) {
			searchForm = (FormComp) leftView.getView().getViewComponents().getComponent("mainform");
		}
		SessionBean sess = SessionUtil.getSessionBean();
		QryConditionVO vo = (QryConditionVO) sess.getExtendAttributeValue(WorkCalendarConsts.SESSION_QRY_CONDITIONS);
		ApplicationContext appCtx = AppLifeCycleContext.current().getApplicationContext();
		if (!StringUtils.isEmpty((String) appCtx.getAppAttribute(WorkCalendarConsts.SESSION_CATAGORY_ACCESS))
				&& vo != null) {// 按时段/日历查看进入
			// 只使用一次,需删除
			appCtx.addAppAttribute(WorkCalendarConsts.SESSION_CATAGORY_ACCESS, null);

		} else {// 通过菜单进入/通过查询按钮进入
			vo = new QryConditionVO();
			FromWhereSQLImpl whereSql = (nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get(HrssConsts.PO_SEARCH_WHERESQL);
			nc.ui.querytemplate.querytree.FromWhereSQLImpl fromWhereSQL = (nc.ui.querytemplate.querytree.FromWhereSQLImpl) CommonUtil
					.getUAPFromWhereSQL((nc.uap.ctrl.tpl.qry.FromWhereSQLImpl) keys.get(HrssConsts.PO_SEARCH_WHERESQL));
			// 获得自定义条件
			Map<String, String> selfDefMap = whereSql.getFieldAndSqlMap();
			// 查询条件-排班条件
			String arrangeflag = selfDefMap.get(WorkCalendarConsts.FD_ARRANGEFLAG);
			// 查询条件-姓名
			String name = selfDefMap.get("pk_psndoc_name");
			// 查询条件-开始日期
			String beginDate = selfDefMap.get(WorkCalendarConsts.FD_BEGINDATE);
			String endDate = selfDefMap.get(WorkCalendarConsts.FD_ENDDATE);
			if (StringUtils.isEmpty(beginDate)) {
				CommonUtil.showCompErrorDialog(searchForm,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0017")/*
																										 * @
																										 * res
																										 * "开始日期不能为空！"
																										 */);

			}
			// 查询条件-结束日期
			if (StringUtils.isEmpty(endDate)) {
				CommonUtil.showCompErrorDialog(searchForm,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0018")/*
																										 * @
																										 * res
																										 * "结束日期不能为空，请输入！"
																										 */);
			}
			if (new UFLiteralDate(beginDate).afterDate(new UFLiteralDate(endDate))) {
				CommonUtil.showCompErrorDialog(searchForm,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0019")/*
																										 * @
																										 * res
																										 * "开始日期不能晚于结束日期，请重新输入！"
																										 */);

			}
			if (new UFLiteralDate(endDate).after(new UFLiteralDate(beginDate).getDateAfter(MAX_QUERY_DAYS))) {
				CommonUtil.showCompErrorDialog(searchForm,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0139")/*
																										 * @
																										 * res
																										 * 开始日期和结束日期的间隔天数大于60天
																										 * ，
																										 * 请重新输入
																										 * ！
																										 */);

			}
			// 查询条件-排班条件
			if (StringUtils.isEmpty(arrangeflag)) {
				CommonUtil.showCompErrorDialog(searchForm,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0020")/*
																										 * @
																										 * res
																										 * "排班条件不能为空，请选择！"
																										 */);
			}
			vo.setBeginDate(new UFLiteralDate(beginDate));
			vo.setEndDate(new UFLiteralDate(endDate));
			vo.setArrangeflag(arrangeflag);
			String psnScopeSqlPart = null;
			try {
				psnScopeSqlPart = QueryUtil.getDeptPsnCondition();
			} catch (BusinessException e) {
				// new HrssException(e).deal();
			}
			String sql = " 1=1";
			if (null != name) {
				sql = " tbm_psndoc.pk_psndoc in ( select bd_psndoc.pk_psndoc from bd_psndoc where name like '%" + name
						+ "%') ";
			}
			if (fromWhereSQL != null && !StringUtils.isEmpty(psnScopeSqlPart)) {
				(fromWhereSQL).setWhere(sql + " and tbm_psndoc.pk_psndoc in (" + psnScopeSqlPart + ") ");
			}

			vo.setFromWhereSQL(fromWhereSQL);
			// 记录本次查询条件
			sess.setExtendAttribute(WorkCalendarConsts.SESSION_QRY_CONDITIONS, vo);
		}
		return vo;
	}

	/**
	 * 根据开始日期和结束日期,构造数据集和日期表格
	 * 
	 * @param beginDate
	 * @param endDate
	 */
	private void initDsAndGrid(LfwView viewMain, Dataset dsCalendar, UFLiteralDate beginDate, UFLiteralDate endDate) {

		// 查询条件开始日期和结束日期没有发生变化,不用重构造DS/Grid！
		UFBoolean dateChangeFlag = (UFBoolean) getApplicationContext().getAppAttribute(
				WorkCalendarConsts.SESSION_DATE_CHANGE);
		getApplicationContext().addAppAttribute(WorkCalendarConsts.SESSION_DATE_CHANGE, UFBoolean.FALSE);// 重置状态
		if (dateChangeFlag != null && !dateChangeFlag.booleanValue()) {
			return;
		}
		// 删除原有Dataset的Fields
		Field[] fields = dsCalendar.getFieldSet().getFields();
		for (Field field : fields) {
			String filedId = field.getId();
			// begin edit shaochj 2015-04-29
			// 原代 码 if (StringUtils.isEmpty(filedId) ||
			// filedId.startsWith("pk_psnjob")) {// 排除非日历相关字段
			if (StringUtils.isEmpty(filedId) || filedId.startsWith("pk_psnjob") || filedId.equals("totaltimes")) {// 排除非日历相关字段
				// end edit shaochj 2015-04-29
				continue;
			}
			dsCalendar.getFieldSet().removeField(field);
		}
		// 删除原有Grid的ColumnS
		GridComp grid = (GridComp) viewMain.getViewComponents().getComponent(WorkCalendarConsts.PAGE_MAIN_GRID);
		List<IGridColumn> groupList = new ArrayList<IGridColumn>();
		IGridColumn col = null;
		for (Iterator<IGridColumn> it = grid.getColumnList().iterator(); it.hasNext();) {
			col = it.next();
			if (col instanceof GridColumnGroup) {
				groupList.add(col);
			}
		}
		grid.removeColumns(groupList, true);

		// 新增新的COLUMNS
		UFLiteralDate tmpDate = null;
		List<IGridColumn> newGroupList = new ArrayList<IGridColumn>();
		int days = UFLiteralDate.getDaysBetween(beginDate, endDate) + 1;// 获得间隔天数
		for (int i = 0; i < days; i++) {
			// 日期
			tmpDate = beginDate.getDateAfter(i);
			// 创建列表数据集字段
			col = buildDatasetField(viewMain, dsCalendar, tmpDate);
			newGroupList.add(col);
		}
		grid.addColumns(newGroupList, true);
	}

	/**
	 * 创建Field字段
	 * 
	 * @param viewMain
	 * @param dsCalendar
	 * @param dateAfter
	 */
	private GridColumnGroup buildDatasetField(LfwView widget, Dataset ds, UFLiteralDate curDate) {

		/** 生成 班次主键字段 ,格式:20120105 */
		Field keyField = buildKeyField(curDate);
		ds.getFieldSet().addField(keyField);

		/** 生成 班次名称字段,格式:20120105_name */
		Field nameField = buildNameField(keyField);
		ds.getFieldSet().addField(nameField);

		/** 生成 班次的FiledRelation */
		String keyFieldId = keyField.getId();
		String nameFieldId = nameField.getId();
		FieldRelation fr = buildFieldRelation(keyFieldId, nameFieldId);
		ds.getFieldRelations().addFieldRelation(fr);
		/** 生成班次的RefNode */
		NCRefNode rfnode = buildRefNode(ds, keyFieldId, nameFieldId);
		rfnode.setDataListener(BURefController.class.getName());
		widget.getViewModels().addRefNode(rfnode);

		/** 生成单元格颜色字段 */
		Field colorField = buildColorField(nameFieldId);
		ds.getFieldSet().addField(colorField);
		/** 生成单元格是否可编辑字段 */
		Field isEditableField = buildIsEditableField(nameFieldId);
		ds.getFieldSet().addField(isEditableField);

		/** 生成班次的GridColumn */
		return buildGridGroup(ds, keyFieldId, nameField, curDate, rfnode.getId());
	}

	/**
	 * 生成 班次主键字段
	 * 
	 * @param curDate
	 * @return
	 */
	private Field buildKeyField(UFLiteralDate curDate) {
		String strDate = curDate.toPersisted();
		// 班级字段
		Field keyField = new Field();
		// 设置字段ID
		keyField.setId(strDate.replace("-", ""));// 格式:20120702
		// 列名
		keyField.setText(strDate);// 格式:2012-07-02
		// 设置数据类型
		keyField.setDataType(DataTypeTranslator.translateInt2String(IType.REF));
		// 与VO对应字段(属性)
		keyField.setField(null);
		return keyField;
	}

	/**
	 * 生成 班次名称字段
	 * 
	 * @return
	 */
	private Field buildNameField(Field keyField) {
		// 生成写入字段ID
		String nameFieldId = keyField.getId() + REF_SUFFIX; // 格式：20120701_name
		Field nameField = new Field();
		// 设置字段ID
		nameField.setId(nameFieldId);
		// 列名
		nameField.setText(keyField.getText());// 格式：2012-07-01
		// 设置数据类型
		nameField.setDataType(StringDataTypeConst.STRING);
		// 与VO对应字段(属性)
		nameField.setField(null);
		return nameField;
	}

	/**
	 * 记录单元格显示颜色的字段
	 * 
	 * @param nameFieldId
	 * @return
	 */
	private Field buildColorField(String nameFieldId) {
		Field colorField = new Field();
		// 设置字段ID
		colorField.setId(COLOR_PREFIX + nameFieldId);// 格式：color_20120701_name
		// 设置数据类型
		colorField.setDataType(StringDataTypeConst.STRING);
		// 与VO对应字段(属性)
		colorField.setField(null);
		return colorField;
	}

	/**
	 * 记录是否可编辑的字段
	 * 
	 * @param keyField
	 * @return
	 */
	private Field buildIsEditableField(String nameFieldId) {
		Field isEditableField = new Field();
		// 设置字段ID
		isEditableField.setId(EDITABLE_PREFIX + nameFieldId);// 格式：edit_20120701_name
		// 设置数据类型
		isEditableField.setDataType(StringDataTypeConst.STRING);
		// 与VO对应字段(属性)
		isEditableField.setField(null);
		return isEditableField;
	}

	/**
	 * 生成 班次的FiledRelation
	 * 
	 * @param ds
	 * @param keyFieldId
	 * @param nameFieldId
	 */
	private FieldRelation buildFieldRelation(String keyFieldId, String nameFieldId) {
		// 生成FieldRelation
		MatchField mf = new MatchField();
		// 参照数据集中的字段名
		mf.setReadField("name");
		// 当前数据集中的Id名
		mf.setWriteField(nameFieldId);

		WhereField whereField = new WhereField();
		// 参照数据集中的字段名
		whereField.setKey("pk_shift");
		// 当前数据集中的Id名
		whereField.setValue(keyFieldId);

		FieldRelation fr = new FieldRelation();
		fr.setId(keyFieldId + "_rel");
		// 参照数据集
		fr.setRefDataset(REFDS_PK_SHIFT_NAME);
		// 待查询字段
		fr.addMatchField(mf);
		// 条件字段
		fr.setWhereField(whereField);
		return fr;
	}

	/**
	 * 创建班次参照
	 * 
	 * @param widget
	 * @param ds
	 * @param keyFieldId
	 * @param nameFieldId
	 * @return
	 */
	private NCRefNode buildRefNode(Dataset ds, String keyFieldId, String nameFieldId) {
		String displayName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0021")/*
																												 * @
																												 * res
																												 * "班次(业务单元)"
																												 */;
		String refCode = "班次(业务单元含本门店)"/* -=notranslate=- */;
		String readFields = "pk_shift,name";
		String writeFields = keyFieldId + "," + nameFieldId;
		NCRefNode rfnode = new RefNodeGenerator().createRefNode(ds, true, nameFieldId, displayName, refCode,
				readFields, writeFields, false, null, null);
		rfnode.setText(displayName);
		return rfnode;
	}

	/**
	 * 创建表格项
	 * 
	 * @param ds
	 * @param keyFieldId
	 * @param nameField
	 * @param curDate
	 * @param refnodeId
	 * @return
	 */
	public static GridColumnGroup buildGridGroup(Dataset ds, String keyFieldId, Field nameField, UFLiteralDate curDate,
			String refnodeId) {
		GridColumn column = new GridColumn();
		// 设置id
		column.setId(CLOUM_PREFIX + keyFieldId); // 格式：col_20120702
		// 设置显示标题
		column.setI18nName(WEEKARRAY[curDate.getWeek()]); // 格式：周一
		column.setLangDir("c_ta-res");
		// 设置关联Dataset的Field
		column.setField(nameField.getId());
		// 设置列宽度,按实际宽度
		column.setWidth(80);
		// 设置参照引用
		column.setRefNode(refnodeId);
		// 设置渲染器
		column.setRenderType("CalendarRender");
		// 设置编辑类型
		column.setEditorType(EditorTypeConst.REFERENCE);
		// "能否编辑",如果是被参照出来的列,设置不可编辑
		column.setEditable(true);

		// 创建Grid多表头
		GridColumnGroup group = new GridColumnGroup();
		group.setId(CLOUM_GROUP_PREFIX + keyFieldId);// 格式：group_20120702
		group.setText(nameField.getText()); // 格式：2012-07-02
		group.addColumn(column);
		return group;
	}

	/**
	 * 显示查询结果
	 * 
	 * @param pk_mng_dept
	 * @param isContainSub
	 * @param beginDate
	 * @param endDate
	 * @param arrangeflag
	 * @param object
	 * @throws BusinessException
	 */
	private void initData(LfwView widget, Dataset ds, String pk_mng_dept, boolean isContainSub,
			UFLiteralDate beginDate, UFLiteralDate endDate, String arrangeflag,
			nc.ui.querytemplate.querytree.FromWhereSQL fromWhereSQL) throws BusinessException {

		PsnJobCalendarVO[] psnvos = CalendarUtils.getDeptPsnCalendar(pk_mng_dept, isContainSub, beginDate, endDate,
				arrangeflag, fromWhereSQL);
		ApplicationContext appCxt = getLifeCycleContext().getApplicationContext();
		appCxt.addAppAttribute(SESSION_PSNJOB_CALENDARVOS, psnvos);

		// tangcht
		psnvos = dealDayType(psnvos, beginDate, endDate);

		// 批量调班时使用参数 */
		String[] psnjobPks = StringPiecer.getStrArray(psnvos, "pk_psnjob");
		appCxt.addAppAttribute(BatchChangeShiftPageModel.WSES_PSN_KEYS, psnjobPks);
		appCxt.addAppAttribute(BatchChangeShiftPageModel.FLD_BEGIN, beginDate);
		appCxt.addAppAttribute(BatchChangeShiftPageModel.FLD_END, endDate);
		initDatasetValue(beginDate, endDate, ds, psnvos);
	}

	/**
	 * 例假、休息日、国定假 设为红色
	 * 
	 * @param psnvos
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 * @author tangcht
	 */
	private PsnJobCalendarVO[] dealDayType(PsnJobCalendarVO[] psnvos, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {

		String[] psndocPks = StringPiecer.getStrArray(psnvos, "pk_psndoc");

		Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnCalMap = NCLocator.getInstance()
				.lookup(IPsnCalendarQueryService.class)
				.queryCalendarVOByPsnInSQLForProcess(null, beginDate, endDate, psndocPks);

		if (psnvos != null) {

			for (PsnJobCalendarVO vo : psnvos) {

				HashMap<String, Integer> dayTypeMap = (HashMap<String, Integer>) vo.getDayTypeMap();

				for (String day : dayTypeMap.keySet()) {

					if (psnCalMap != null
							&& psnCalMap.get(vo.getPk_psndoc()) != null
							&& psnCalMap.get(vo.getPk_psndoc()).get(
									new UFLiteralDate(day)) != null
							&& psnCalMap.get(vo.getPk_psndoc())
									.get(new UFLiteralDate(day))
									.getPsnCalendarVO() != null) {
						// ssx remarked for TWLC
						// on 2018-10-11
						String pk_shiftholidaytype = psnCalMap
								.get(vo.getPk_psndoc())
								.get(new UFLiteralDate(day)).getPsnCalendarVO()
								.getPk_shiftholidaytype();
						String pk_shift = psnCalMap.get(vo.getPk_psndoc())
								.get(new UFLiteralDate(day)).getPsnCalendarVO()
								.getPk_shift();
						// modify by ward 20180428 休息日为绿色，国假日例假日为红色 begin
						if (pk_shiftholidaytype != null
								&& !"~".equals(pk_shiftholidaytype)) {
							if ("0001Z7000000000000OD"
									.equals(pk_shiftholidaytype)) {
								dayTypeMap.put(day,
										HolidayVO.DAY_TYPE_NONWORKDAY);
							} else {
								dayTypeMap.put(day, HolidayVO.DAY_TYPE_HOLIDAY);
							}
						} else if ("0001Z7000000000000OD".equals(pk_shift)) {
							dayTypeMap.put(day, HolidayVO.DAY_TYPE_NONWORKDAY);
						} else if ("0001Z7000000000000HD".equals(pk_shift)) {
							//mod Ares.Tank 增加了例假日类型 为深红色
							dayTypeMap.put(day, HolidayVO.DAY_TYPE_OFFICAL_HOLIDAY);
						} else if ("0001Z7000000000000NH".equals(pk_shift)) {
							dayTypeMap.put(day, HolidayVO.DAY_TYPE_HOLIDAY);
						}
						// //modify by ward 20180428 休息日为绿色，国假日例假日为红色 end
						// ssx end
					}
				}
			}
		}

		return psnvos;

	}

	/**
	 * 初始化数据集的值
	 * 
	 * @param beginDate
	 * @param endDate
	 * @param ds
	 * @param psnvos
	 */
	private void initDatasetValue(UFLiteralDate beginDate, UFLiteralDate endDate, Dataset ds, PsnJobCalendarVO[] psnvos) {
		if (StringUtils.isEmpty(ds.getCurrentKey())) {
			ds.setCurrentKey(Dataset.MASTER_KEY);
		}
		PaginationInfo pinfo = ds.getCurrentRowSet().getPaginationInfo();
		if (psnvos == null || psnvos.length == 0) {
			DatasetUtil.clearData(ds);
			pinfo.setRecordsCount(0);
			// 更新菜单状态
			ds.setEnabled(false);
			ButtonStateManager.updateButtons();
			return;
		}
		SuperVO[] result = DatasetUtil.paginationMethod(ds, psnvos);
		// 获得间隔天数
		int days = UFLiteralDate.getDaysBetween(beginDate, endDate) + 1;
		// 当前日期
		for (int i = 0; i < result.length; i++) { // 按人员循环
			PsnJobCalendarVO calendarVO = (PsnJobCalendarVO) result[i];
			Row row = ds.getEmptyRow();
			// 设置人员主键
			String pk_psndoc = calendarVO.getPk_psndoc();
			// 设置人员工作主键
			String pk_psnjob = calendarVO.getPk_psnjob();
			// 存储班别的map,key是日期，value是班别主键
			Map<String, String> calendarMap = calendarVO.getCalendarMap();
			Set<String> effectiveDateSet = calendarVO.getPsndocEffectiveDateSetInHROrgAndDept();

			row.setValue(ds.nameToIndex("pk_psnjob_pk_psndoc"), pk_psndoc);
			row.setValue(ds.nameToIndex(PsnJobVO.PK_PSNJOB), pk_psnjob);
			double totaltimes = 0;
			DecimalFormat df = new DecimalFormat("0.00");
			String pk_shift = null;
			for (int j = 0; j < days; j++) {// 按日期循环
				String date = beginDate.getDateAfter(j).toPersisted();
				String keyFieldId = date.replace("-", "");
				String nameFieldId = keyFieldId + REF_SUFFIX;
				// 计算总工时
				pk_shift += ";" + calendarMap.get(date);
				// totaltimes += this.getGzsjByPk_shift(calendarMap.get(date));
				if (calendarMap.containsKey(date)) {
					if (SessionUtil.getPk_mng_org().equals(calendarVO.getOrgMap().get(date))) {
						if (ds.nameToIndex(nameFieldId) > -1) {
							row.setValue(ds.nameToIndex(keyFieldId), calendarMap.get(date));// 单元格班次主键的值
						}
					}
				}
				if (ds.nameToIndex(COLOR_PREFIX + nameFieldId) > -1) {
					Color c = null;
					// 判断是考勤档案是否在当前组织
					if (SessionUtil.getPk_mng_org().equals(calendarVO.getOrgMap().get(date))) {
						c = CalendarColorUtils.getDateColor(date, calendarVO);
					} else {
						c = IColorConst.COLOR_NONTBMPSNDOC;
					}
					if (c != null) {
						row.setValue(ds.nameToIndex(COLOR_PREFIX + nameFieldId), ShopTAUtil.getHexDesc(c));// 单元格班次颜色
					}
				}
				if (ds.nameToIndex(EDITABLE_PREFIX + nameFieldId) > -1) {
					row.setValue(ds.nameToIndex(EDITABLE_PREFIX + nameFieldId), effectiveDateSet.contains(date));// 单元格班次是否可编辑
				}
			}
			totaltimes = getGzsjByPk_shifts(pk_shift);
			totaltimes = Double.parseDouble(df.format(totaltimes).toString());
			row.setValue(ds.nameToIndex("totaltimes"), totaltimes
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res", "0c_ta-res0078")); // leo
																											// edit
																											// {18112-7}
			ds.addRow(row);
		}
		// 更新菜单状态
		ds.setEnabled(false);
		ButtonStateManager.updateButtons();
	}

	/**
	 * 修改班次参照后的处理方法
	 */
	public void onAfterDataChange_dsCalendar(DatasetCellEvent datasetCellEvent) {
		Dataset dsCalendar = datasetCellEvent.getSource();
		Row selRow = dsCalendar.getSelectedRow();
		if (selRow == null) {
			return;
		}
		int rowIndex = datasetCellEvent.getRowIndex();
		int colIndex = datasetCellEvent.getColIndex();
		Field field = dsCalendar.getFieldSet().getField(colIndex);
		if (field != null && !StringUtils.isEmpty(field.getId()) && field.getId().endsWith("_name")) {
			return;
		}

		// 行的唯一标记
		String rowId = rowIndex + "_" + colIndex;
		LfwView viewMain = getLifeCycleContext().getViewContext().getView();
		Dataset dsModifiedClass = viewMain.getViewModels().getDataset("dsModifiedClass");
		String currntKey = dsModifiedClass.getCurrentKey();
		Row newRow = null;
		if (StringUtils.isEmpty(currntKey)) {
			dsModifiedClass.setCurrentKey(Dataset.MASTER_KEY);
		} else {
			newRow = dsModifiedClass.getRowById(rowId);
		}
		if (newRow == null) {
			newRow = dsModifiedClass.getEmptyRow();
			dsModifiedClass.addRow(newRow);
			newRow.setRowId(rowId);
		}
		// 设置DSModifiedClass中的行记录的值
		String date = field.getText(); // 调班日期
		newRow.setValue(dsModifiedClass.nameToIndex("pk_psndoc"),
				selRow.getString(dsCalendar.nameToIndex("pk_psnjob_pk_psndoc")));
		newRow.setValue(dsModifiedClass.nameToIndex("pk_psnjob"), selRow.getString(dsCalendar.nameToIndex("pk_psnjob")));
		newRow.setValue(dsModifiedClass.nameToIndex("date"), date);
		newRow.setValue(dsModifiedClass.nameToIndex("pk_shift"), datasetCellEvent.getNewValue());
	}

	/**
	 * 调班
	 * 
	 * @param mouseEvent
	 */
	public void doChangeClasses(MouseEvent<MenuItem> mouseEvent) {
		Dataset dsCalendar = getCurrentView().getViewModels().getDataset(WorkCalendarForPsnPageModel.DATASET_CALENDAR);
		dsCalendar.setEnabled(true);
		// 更新菜单状态
		ButtonStateManager.updateButtons();
	}

	/**
	 * 保存操作
	 * 
	 * @param mouseEvent
	 */
	public void doSave(MouseEvent<?> mouseEvent) {

		LfwView viewMain = getCurrentView();
		// 已修改的班次数组
		Dataset dsModifiedClass = viewMain.getViewModels().getDataset("dsModifiedClass");
		Dataset dsCalendar = getCurrentView().getViewModels().getDataset(WorkCalendarForPsnPageModel.DATASET_CALENDAR);
		dsCalendar.setEnabled(false);
		RowData rowData = dsModifiedClass.getCurrentRowData();
		if (rowData == null || rowData.getRows() == null || rowData.getRows().length == 0) {
			// 更新菜单状态
			ButtonStateManager.updateButtons();
			return;
		}

		PsnJobCalendarVO[] psnCalVOs = (PsnJobCalendarVO[]) getApplicationContext().getAppAttribute(
				SESSION_PSNJOB_CALENDARVOS);
		Map<String, PsnJobCalendarVO> psnCalVOMap = new HashMap<String, PsnJobCalendarVO>();
		if (!ArrayUtils.isEmpty(psnCalVOs)) {
			for (PsnJobCalendarVO psnCalVO : psnCalVOs) {
				psnCalVOMap.put(psnCalVO.getPk_psnjob(), psnCalVO);
			}
		}
		for (Row row : rowData.getRows()) {
			String pk_psndoc = row.getString(dsModifiedClass.nameToIndex("pk_psndoc"));
			String pk_psnjob = row.getString(dsModifiedClass.nameToIndex("pk_psnjob"));
			String date = row.getString(dsModifiedClass.nameToIndex("date"));
			String pk_shift = row.getString(dsModifiedClass.nameToIndex("pk_shift"));
			Map<String, Map<String, String>> tmpMap = new HashMap<String, Map<String, String>>();

			// Map-Key格式:人员主键&工作主键
			// 作用：屏蔽多次修改同一日期的班次,以最后一次修改为准.
			String tmpKey = pk_psndoc + HrssConsts.FLAG_PARAMS_SPLIT + pk_psnjob;
			if (tmpMap.containsKey(tmpKey)) {
				Map<String, String> modifiedCalendarMap = tmpMap.get(tmpKey);
				modifiedCalendarMap.put(date, pk_shift);
			} else {
				Map<String, String> modifiedCalendarMap = new HashMap<String, String>();
				modifiedCalendarMap.put(date, pk_shift);
				tmpMap.put(tmpKey, modifiedCalendarMap);
			}

			// 组合修改的工作日历数组
			List<PsnJobCalendarVO> calendarList = new ArrayList<PsnJobCalendarVO>();
			for (Iterator<String> itr = tmpMap.keySet().iterator(); itr.hasNext();) {
				String key = itr.next();
				String[] keys = key.split(HrssConsts.FLAG_PARAMS_SPLIT);
				PsnJobCalendarVO calendarVO = psnCalVOMap.get(keys[1]);
				calendarVO.setPk_psndoc(keys[0]);
				calendarVO.setPk_psnjob(keys[1]);
				calendarVO.getModifiedCalendarMap().putAll(tmpMap.get(key));
				calendarList.add(calendarVO);
			}
			// 保存修改的工作日历数组
			saveCalendarsByEdit(calendarList.toArray(new PsnJobCalendarVO[0]));
		}
		// 更新菜单状态
		ButtonStateManager.updateButtons();
	}

	/**
	 * 保存修改的工作日历数组
	 * 
	 * @param psnvos
	 */
	private void saveCalendarsByEdit(PsnJobCalendarVO[] psnvos) {
		try {
			IPsnCalendarManageMaintain service = ServiceLocator.lookup(IPsnCalendarManageMaintain.class);
			service.save4Mgr(SessionUtil.getPk_mng_dept(), psnvos);
		} catch (HrssException e) {
			new HrssException(e).alert();
		} catch (BusinessException e) {
			new HrssException(e).deal();
		}
	}

	/**
	 * 操作
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void doRefresh(MouseEvent mouseEvent) {
		// 执行左侧快捷查询
		CmdInvoker.invoke(new PlugoutSimpleQueryCmd());
	}

	/**
	 * 批量调班操作
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void doBatchChange(MouseEvent mouseEvent) {
		BatchChangeShiftViewMain.doBatchChange();
	}

	/**
	 * 循环排班操作
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onCircleArrangeShift(MouseEvent mouseEvent) {
		BatchArrangeShiftViewMain.doCircleArrangeShift(WorkCalendarConsts.FUNC_CODE);
	}

	/**
	 * 导出
	 * 
	 * @param mouseEvent
	 */
	public void doExportExcel(MouseEvent<MenuItem> mouseEvent) {
		Dataset ds = ViewUtil.getDataset(getCurrentView(), WorkCalendarForPsnPageModel.DATASET_CALENDAR);
		Row[] row = ds.getAllRow();
		if (row.length == 0) {
			CommonUtil.showErrorDialog("导出失败", "没有可导出的数据！");
		} else {

			// 查询条件
			QryConditionVO vo = (QryConditionVO) SessionUtil.getSessionBean().getExtendAttributeValue(
					WorkCalendarConsts.SESSION_QRY_CONDITIONS);
			// 开始日期
			UFLiteralDate beginDate = vo.getBeginDate();
			// 结束日期
			UFLiteralDate endDate = vo.getEndDate();
			// 工作日历数据
			PsnJobCalendarVO[] psnCalVOs = (PsnJobCalendarVO[]) getApplicationContext().getAppAttribute(
					SESSION_PSNJOB_CALENDARVOS);
			if (psnCalVOs == null) {
				psnCalVOs = CalendarUtils.getDeptPsnCalendar(SessionUtil.getPk_mng_dept(),
						SessionUtil.isIncludeSubDept(), beginDate, endDate, vo.getArrangeflag(), vo.getFromWhereSQL());
			}
			/** 将人员姓名、编码、班次名称等信息加入到工作日历数据中 */
			GeneralVO[] vos = null;
			try {
				IPsnCalendarQueryMaintain service = ServiceLocator.lookup(IPsnCalendarQueryMaintain.class);
				vos = service.getExportDatas(SessionUtil.getHROrg(), psnCalVOs, beginDate, endDate);
			} catch (BusinessException e) {
				new HrssException(e).deal();
			} catch (HrssException e) {
				e.alert();
			}

			String[] fields = WorkCalendarCommonValue.createExportFields(beginDate, endDate);
			GeneralVO[] psndocVOs = new GeneralVO[vos.length];
			UFLiteralDate[] dates = CommonUtils.createDateArray(beginDate, endDate);
			String[][] workCalendars = new String[vos.length][fields.length - 4];

			for (int i = 0; i < vos.length; i++) {
				psndocVOs[i] = new GeneralVO();
				String totals = (String) row[i].getValue(ds.nameToIndex("totaltimes"));
				for (int j = 0; j < fields.length; j++) {
					String value = (String) vos[i].getAttributeValue(fields[j]);
					switch (j) {
					case 0:
						psndocVOs[i].setAttributeValue(WorkCalendarCommonValue.LISTCODE_CLERKCODE, value);
						break;
					case 1:
						psndocVOs[i].setAttributeValue(WorkCalendarCommonValue.LISTCODE_PSNCODE, value);
						break;
					case 2:
						psndocVOs[i].setAttributeValue(WorkCalendarCommonValue.LISTCODE_PSNNAME, value);
						break;
					case 3:
						psndocVOs[i].setAttributeValue(WorkCalendarCommonValue.LISTCODE_TOTALTIMES, totals);
						break;
					default:
						workCalendars[i][j - 4] = value;
						break;
					}
				}
			}
			try {
				/* 写入文件 */
				String filename = "psncalendar_" + System.currentTimeMillis() + ".xls";
				String deptName = getDeptName();
				ExcelExportUtils excelExport = new ExcelExportUtils();
				String path = excelExport.exportCalendarExcelFile(deptName, filename, psndocVOs, dates, workCalendars);
				if (!StringUtils.isEmpty(path)) {
					try {
						AppLifeCycleContext
								.current()
								.getWindowContext()
								.addExecScript(
										"sysDownloadFile('" + LfwRuntimeEnvironment.getRootPath() + "/" + path + "');");
					} catch (Exception e) {
						new HrssException(e).deal();
					}
				}
			} catch (Exception e) {
				new HrssException(e).deal();
			}
		}

	}

	public String getDeptName() {
		HRDeptVO hrDeptVO = SessionUtil.getMngDept();
		if (hrDeptVO != null) {
			return MultiLangHelper.getName(hrDeptVO, HRDeptVO.NAME);
		}
		return null;
	}

	/**
	 * 根据班次PK获取工作时长
	 * 
	 * @param pk_shift
	 * @return
	 */
	public double getGzsjByPk_shift(String pk_shift) {
		double gzsj = 0;
		/*
		 * try { AggShiftVO aggVO =
		 * NCLocator.getInstance().lookup(IStoreShiftQueryMaintain
		 * .class).queryByPk(pk_shift); ShiftVO vo = (ShiftVO)
		 * aggVO.getParentVO(); UFDouble gzsc = vo.getGzsj(); gzsj =
		 * gzsc.doubleValue(); } catch (BusinessException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		try {
			String sql = "select gzsj  from bd_shift " + "where  pk_shift = '" + pk_shift + "' ";
			IDBCacheBS idbc = (IDBCacheBS) NCLocator.getInstance().lookup(IDBCacheBS.class.getName());
			ArrayList<?> result = (ArrayList<?>) idbc.runSQLQuery(sql, new ArrayListProcessor());
			if (result != null && result.size() > 0) {
				Object[] obj = (Object[]) result.get(0);
				if (obj != null && obj[0] != null) {
					gzsj = Double.parseDouble(obj[0].toString());
				}
			}
		} catch (Exception e) {
			new HrssException(e).deal();
		}
		return gzsj;
	}

	/**
	 * 根据班次PK获取工作时长
	 * 
	 * @param pk_shift
	 * @return
	 */
	public double getGzsjByPk_shifts(String pk_shifts) {
		double gzsj = 0;
		/*
		 * try { AggShiftVO aggVO =
		 * NCLocator.getInstance().lookup(IStoreShiftQueryMaintain
		 * .class).queryByPk(pk_shift); ShiftVO vo = (ShiftVO)
		 * aggVO.getParentVO(); UFDouble gzsc = vo.getGzsj(); gzsj =
		 * gzsc.doubleValue(); } catch (BusinessException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		String[] pk_shift = pk_shifts.split(";");

		// {18112}
		// String updateSql="update bd_shift set gzsj=0 where code='DEFAULT'";
		// BaseDAO dao = new BaseDAO();
		// try {
		// dao.executeUpdate(updateSql.toString());
		// } catch (DAOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		for (int i = 0; i < pk_shift.length; i++) {
			try {

				String sql = "		select bd_shift.gzsj, tbm_psncalendar.pk_shiftholidaytype,bd_shift.worklen from bd_shift "
						+ "inner join  tbm_psncalendar on bd_shift.pk_shift= tbm_psncalendar.pk_shift "
						+ "where bd_shift.pk_shift = '" + pk_shift[i] + "' ";
				IDBCacheBS idbc = (IDBCacheBS) NCLocator.getInstance().lookup(IDBCacheBS.class.getName());
				ArrayList<?> result = (ArrayList<?>) idbc.runSQLQuery(sql, new ArrayListProcessor());
				if (result != null && result.size() > 0) {

					// Added by leo begin {18112} totaltime cal
					Object[] obj = (Object[]) result.get(0);
					String holidaytype = obj[1] == null ? "" : obj[1].toString(); // pk_shiftholidaytype
					int workLen = (Integer) obj[2]; // worklen

					if (holidaytype.equals("0001Z7000000000000NH")) // guo ding
																	// jia
					{
						if (workLen > 0 && (workLen / 3600.0) < 8) {
							gzsj += 8;
						} else {
							if (obj != null && obj[0] != null) {
								gzsj += Double.parseDouble(obj[0].toString());
							}
						}
					} else if (holidaytype.equals("0001Z7000000000000HD")) // xiu
																			// xi
																			// ri
					{
						if (workLen > 0 && (workLen / 3600.0) < 4) {
							gzsj += 4;
						} else if (Double.parseDouble(obj[0].toString()) > 0
								&& Double.parseDouble(obj[0].toString()) <= 4) // 0<gzsj<=4;
						{
							gzsj += 4;
						} else if (Double.parseDouble(obj[0].toString()) > 4
								&& Double.parseDouble(obj[0].toString()) <= 8) // 4<gzsj<=8;
						{
							gzsj += 8;
						} else if (Double.parseDouble(obj[0].toString()) > 8) // gzsj>8;
						{
							gzsj += 12;
						}
					} else {
						if (obj != null && obj[0] != null) {
							gzsj += Double.parseDouble(obj[0].toString());
						}
					}

					// end leo
				}
			} catch (Exception e) {
				new HrssException(e).deal();
			}
		}

		return gzsj;
	}

	/**
	 * 店员工作日历导入
	 * 
	 * @author tangcht
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public void doImportExcel(MouseEvent<MenuItem> mouseEvent) throws BusinessException {

		new ImportWorkCalendarListByExcel().onImportExcel(mouseEvent);

	}

	public void onUploadedExcelFile(ScriptEvent e) throws BusinessException {

		new ImportWorkCalendarListByExcel().onUploadedExcelFile(e);
	}

}
