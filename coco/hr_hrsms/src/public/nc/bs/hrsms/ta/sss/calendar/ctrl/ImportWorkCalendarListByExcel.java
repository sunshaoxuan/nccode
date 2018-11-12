package nc.bs.hrsms.ta.sss.calendar.ctrl;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.hrsms.ta.sss.calendar.WorkCalendarConsts;
import nc.bs.hrss.pub.HrssConsts;
import nc.bs.hrss.pub.cmd.PlugoutSimpleQueryCmd;
import nc.bs.hrss.pub.tool.CommonUtil;
import nc.bs.hrss.pub.tool.SessionUtil;
import nc.bs.hrss.ta.calendar.CalendarUtils;
import nc.bs.logging.Logger;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.ResHelper;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.bd.shift.IShiftManageMaintain;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.hi.IPsndocQryService;
import nc.itf.ta.IPeriodQueryService;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.IPsncalendarMng;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.uap.ctrl.excel.UifExcelImportCmd;
import nc.uap.lfw.core.AppInteractionUtil;
import nc.uap.lfw.core.ContextResourceUtil;
import nc.uap.lfw.core.cmd.CmdInvoker;
import nc.uap.lfw.core.ctx.AppLifeCycleContext;
import nc.uap.lfw.core.event.MouseEvent;
import nc.uap.lfw.core.event.ScriptEvent;
import nc.ui.querytemplate.querytree.FromWhereSQLImpl;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.shift.AggShiftVO;
import nc.vo.bd.shift.CapRTVO;
import nc.vo.bd.shift.RTVO;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hrss.ta.calendar.QryConditionVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.lang.UFTime;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnCalendarCommonValue;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;

public class ImportWorkCalendarListByExcel {

	public static String PSNCL = "psncl"; // 人员类别

	public static String CAL_RES = "coco_ta"; // 多语文件夹

	public static String GX = ResHelper.getString("6017psncalendar", "06017psncalendar0092")/*
																							 * @
																							 * res
																							 * "公休"
																							 */;

	public static String KB = ResHelper.getString(CAL_RES, "coco_cal_res011");/*
																			 * @res
																			 * "空班"
																			 */

	public static String GDJ = ResHelper.getString(CAL_RES, "coco_cal_res005");/*
																				 * @
																				 * res
																				 * 国定假
																				 */

	public static String LJ = ResHelper.getString(CAL_RES, "coco_cal_res002");/*
																			 * @res
																			 * 例假
																			 */

	public static String XXR = ResHelper.getString(CAL_RES, "coco_cal_res003");/*
																				 * @
																				 * res
																				 * 休息日
																				 */

	// 设置文件选择对话框
	private JFileChooser fileChooser = null;

	private FileNameExtensionFilter xlsFilter = new FileNameExtensionFilter("MS Office Excel(*.xls)", "xls");

	private int beDay = 0; // 要锁定两周前数据不能修改，beDay导入需要往后多少天才开始导入

	private List restName = Arrays.asList(new String[] { GX, KB, GDJ, LJ, XXR }); // 非工作日
																					// 列表

	private HashMap<String, String> shiftPkNameMap = new HashMap<String, String>(); // key：班次pk；value为班次名

	private HashMap<String, String> shiftWorktimeName = new HashMap<String, String>();

	private String defaultShiftName; // 默认班次名称

	private ArrayList<ShiftVO> shiftVOs = new ArrayList<ShiftVO>(); // 班次

	private Sheet sheet; // 获取的excel页签

	private HashMap<String, Integer> conWorkDaysMap = new HashMap<String, Integer>();// 每个人已连续加班天数；key:psncode+psnname;value:conWorkDays

	private HashMap<String, HashMap<String, Integer>> weekLjXxrMap = new HashMap<String, HashMap<String, Integer>>();// 周一到周日内有且只有一个例假日一个休息日;map<人员,<假期,天数>>

	private HashMap<String, String> psnPkMap = new HashMap<String, String>();

	private HashMap<String, HashMap<String, String>> psnDateShiftHolidayTypeMap = new HashMap<String, HashMap<String, String>>();

	private HashMap<String, String> psnPsnclMap = new HashMap<String, String>();

	private HashMap<String, String> psnHrorgMap = new HashMap<String, String>();

	private HashMap<String, String> psnOrgMap = new HashMap<String, String>();

	private HashMap<String, ArrayList<String>> shortCalMap = new HashMap<String, ArrayList<String>>();

	private String extramsg = "";

	/**
	 * 日历导入
	 * 
	 * @param mouseEvent
	 */
	@SuppressWarnings("rawtypes")
	public void onImportExcel(MouseEvent mouseEvent) {
		UifExcelImportCmd cmd = new UifExcelImportCmd(this.getClass().getName(), HrssConsts.PAGE_MAIN_WIDGET);
		CmdInvoker.invoke(cmd);

	}

	// 确认对话框的Id
	public static String DIALOG_IMPORT = "dlg_import";

	public void onUploadedExcelFile(ScriptEvent e) throws BusinessException {

		/*
		 * if
		 * (!CommonUtil.showConfirmDialog(ResHelper.getString("6017psncalendar"
		 * ,"06017psncalendar0034")
		 * 
		 * @ res "导入工作日历数据时，若已有日历数据，原数据将会被覆盖，确认继续吗？")) { return; }
		 */
		Boolean resultOne = AppInteractionUtil.getConfirmDialogResult(DIALOG_IMPORT);
		if (resultOne != null && Boolean.FALSE.equals(resultOne)) {
			return;
		}
		/*
		 * String originalFilename =
		 * AppLifeCycleContext.current().getParameter("originalFilename");
		 * String errorFilePath = "importfiles/" + originalFilename.substring(0,
		 * originalFilename.lastIndexOf(".xls")) + "_error.xls";
		 */

		if (resultOne == null) {

			String relativePath = AppLifeCycleContext.current().getParameter(UifExcelImportCmd.EXCEL_PATH);
			String appPath = ContextResourceUtil.getCurrentAppPath();
			String fullPath = appPath + "/" + relativePath;

			sheet = openWorkbook(fullPath);
			
			// BEGIN 张恒 获取门市信息 2018/10/29
			// 获取第三行第七列的值 门市的内容
			Cell cell = sheet.getRow(2)[7];
			String psnjobOrgName = cell == null ? "" : cell.getContents()
					.trim().toString();
			if ("".equals(psnjobOrgName)) {
				throw new BusinessException("excel門市資訊不能為空");
			}
			IPsncalendarMng iPsncalendarMng = NCLocator.getInstance().lookup(IPsncalendarMng.class);
			String psnjoborg = iPsncalendarMng.queryPsnJobOrgByName(psnjobOrgName);
			if ("".equals(psnjoborg)) {
				throw new BusinessException("excel門市資訊名稱有誤");
			}
			// END 张恒 获取门店信息 2018/10/29

			HashMap<String, ArrayList<GeneralVO>> ImportvosMap = getPsnCalendarVOs();

			if (ImportvosMap == null || ImportvosMap.isEmpty())
				return;

			List<String>[] exceptVectors = iPsncalendarMng
					.importDatasHdType(ImportvosMap, false, psnDateShiftHolidayTypeMap, psnjoborg);

			if (!ArrayUtils.isEmpty(exceptVectors)) {

				CommonUtil.showErrorDialog(getImportErrorMsg(exceptVectors));

			}

			CmdInvoker.invoke(new PlugoutSimpleQueryCmd());

			if (shortCalMap != null && shortCalMap.size() > 0) {
				String msg = extramsg;
				msg += getCoCoRes("coco_cal_res027") + " 	";

				for (String code : shortCalMap.keySet()) {

					msg += getCoCoRes("coco_cal_res020") + "[" + code + "]";

					for (String date : shortCalMap.get(code)) {

						msg += " Date:" + date;
					}

					msg += "	";
				}

				CommonUtil.showMessageDialog(getCoCoRes("coco_cal_res034"), msg);// 短班
			} else if (extramsg != null) {
				CommonUtil.showMessageDialog(getCoCoRes("coco_cal_res034"), extramsg);
			} else {
				CommonUtil.showMessageDialog(getCoCoRes("coco_cal_res034"), "旧Θ");
			}
		}/*
		 * else {
		 * 
		 * AppLifeCycleContext.current().getWindowContext().addExecScript(
		 * "sysDownloadFile('" + LfwRuntimeEnvironment.getRootPath() + "/" +
		 * errorFilePath + "');"); }
		 */

	}

	private String getImportErrorMsg(List<String>[] exceptVectors) {

		String[] errMsg = new String[] { getCoCoRes("coco_cal_res028"), getCoCoRes("coco_cal_res029"),
				getCoCoRes("coco_cal_res030"), getCoCoRes("coco_cal_res031"), getCoCoRes("coco_cal_res032"),
				getCoCoRes("coco_cal_res033") };

		String msg = "";

		for (int i = 0; i < exceptVectors.length; i++) {

			if (exceptVectors[i].isEmpty()) {
			}
			msg += errMsg[i] + ";";
		}

		return msg;
	}

	/**
	 * 获取人员工作日历数据
	 * 
	 * @param sheet
	 * @return
	 * @return
	 * @throws BusinessException
	 */
	private HashMap<String, ArrayList<GeneralVO>> getPsnCalendarVOs() throws BusinessException {

		int dateOnRow = 3; // 日期所在行
		int naHolidayRow = 5; // 国定假所在行
		int naHolidayColumnPsn = 44; // AS Column for national holiday
		int personStartRow = 9; // 员工开始行号
		int nextPersonRow = 9; // 每个人8行数据 //customer add one more line to excel;

		int dateStartColumn = 5; // 日期开始列

		// 之后调用的importDatas前3列原先分别对应 员工号,人员编码和姓名,之后保存对应日期的工作日历
		// 自己构建的时候前3列要留出来不能保存日历信息

		int nowRow = personStartRow; // 当前处理行
		String psnname = ""; // 姓名
		String psncode = ""; // 人员编码
		String psncl = ""; // 人员类别 （是否雇主）
		String psn = "";

		String title = getCoCoRes("coco_cal_res012");
		/** 店员工作日历导入 */
		String errorMsg = "";

		// 加载班次信息
		loadShift();

		// 校验并获取表头信息,只取有用的信息，和excel列没有一一对应

		String[] fieldNames = getHeadMessage(dateStartColumn, dateOnRow);
		
		// BEGIN 张恒{{23045}} 获取excel导入的开始、结束日期 2018/10/27
		String excelBegindateStr = "";
		String excelEnddateStr = "";
		UFLiteralDate excelBegindate = null;
		UFLiteralDate excelEnddate = null;
		if (!ArrayUtils.isEmpty(fieldNames)) {

			// 获取excel的开始日期
			excelBegindateStr = fieldNames[0];
			excelBegindate = new UFLiteralDate(excelBegindateStr);

			// 获取excel的结束日期
			excelEnddateStr = fieldNames[fieldNames.length - 1];
			excelEnddate = new UFLiteralDate(excelEnddateStr);
		}
		// END 张恒{{23045}} 获取excel导入的开始、结束日期 2018/10/27
		

		// 校验日期段，要求日期段全部落在未封存的期间内，如果有日期不属于任何期间，或者属于已封存的期间，则抛异常
		NCLocator
				.getInstance()
				.lookup(IPeriodQueryService.class)
				.checkDateScope(SessionUtil.getPk_org(), UFLiteralDate.getDate(fieldNames[0]),
						UFLiteralDate.getDate(fieldNames[fieldNames.length - 1]));

		HashMap<String, ArrayList<GeneralVO>> hrorgImportvosMap = new HashMap<String, ArrayList<GeneralVO>>();

		ArrayList ls = new ArrayList();

		for (int i = 0;; i++) {

			nowRow = 9 + i * nextPersonRow; // edited

			psnname = sheet.getCell(1, nowRow).getContents().trim();

			if (psnname == null || "".equals(psnname)) {
				break;
			}

			psncode = sheet.getCell(1, nowRow + 3).getContents().trim();

			if (psncode == null || "".equals(psncode)) {
				break;
			}

			psn = psncode + psnname;

			psncl = sheet.getCell(0, nowRow).getContents().trim();

			if (psnOrgMap.get(psn) != null) {

				errorMsg = checkWorkTime(psncl, nowRow);

				if (!"".equals(errorMsg)) {

					CommonUtil.showErrorDialog(errorMsg + "," + getCoCoRes("coco_cal_res020") + "[" + psn + "]");// leo
																													// edit
																													// from
																													// psncode
																													// to
																													// psn;
				}

				GeneralVO vo = new GeneralVO();

				vo.setAttributeValue(PSNCL, psncl);

				vo.setAttributeValue(PsnCalendarCommonValue.LISTCODE_PSNNAME, psnname);
				vo.setAttributeValue(PsnCalendarCommonValue.LISTCODE_PSNCODE, psncode);

				// special psn:new join psn; resign psn checkLJXXR

				// MOD LEO {18765-2 check LJ XXR by Month} 2018-02-26 BEGIN
				Integer totalLJdaysthisMonth = 0;
				Integer totalXXRdaysthisMonth = 0;
				UFLiteralDate indutydate = getStartDayByPsn(psncode);
				/*
				 * // one LJ one XXR per week case--not use any more
				 * UFLiteralDate
				 * nextMondayAfterIndutydate=indutydate.getWeek()==
				 * 0?indutydate.getDateAfter
				 * (1):indutydate.getDateAfter(8-indutydate.getWeek()); int
				 * addstartdaynumber=0; if (indutydate.getWeek()==1){
				 * if(indutydate!=null &&
				 * UFLiteralDate.getDate(fieldNames[0]).before(indutydate) &&
				 * UFLiteralDate
				 * .getDate(fieldNames[fieldNames.length-1]).after(indutydate)){
				 * addstartdaynumber
				 * =Integer.valueOf(indutydate.toString().substring(8))-1; }
				 * }else{ if(indutydate!=null &&
				 * UFLiteralDate.getDate(fieldNames
				 * [0]).before(nextMondayAfterIndutydate) &&
				 * UFLiteralDate.getDate
				 * (fieldNames[fieldNames.length-1]).after(
				 * nextMondayAfterIndutydate )){
				 * addstartdaynumber=Integer.valueOf(nextMondayAfterIndutydate
				 * .toString().substring(8))-1; } }
				 */
				UFLiteralDate enddate = getEndDayByPsn(psncode);
				/*
				 * // one LJ one XXR per week case--not use any more int
				 * enddaynumber
				 * =Integer.valueOf(fieldNames[fieldNames.length-1].substring
				 * (8)); if (enddate!=null &&
				 * UFLiteralDate.getDate(fieldNames[fieldNames
				 * .length-1]).after(enddate) &&
				 * UFLiteralDate.getDate(fieldNames[0]).before(enddate)){
				 * enddaynumber
				 * =Integer.valueOf(enddate.toString().substring(8))-1; }
				 */
				// MOD change LJ and XXR logic James
				
				// BEGIN 张恒{{23045}} 将人员开始日期与excel开始日期进行比对 如果人员开始日期在excel开始日期之前
				// 则应该取excel开始日期为开始日期 2018/10/27
				UFLiteralDate startDate = null;
				if (indutydate.beforeDate(excelBegindate)
						&& ((null != enddate && !excelBegindate
								.afterDate(enddate)) || null == enddate)) {
					startDate = excelBegindate;
				} else {
					startDate = indutydate;
				}
				// END 张恒{{23045}} 将人员开始日期与excel开始日期进行比对 如果人员开始日期在excel开始日期之前
				
				// BEGIN 张恒{23045} 将人员结束日期与excel结束日期进行比对 如果人员结束日期在excel结束日期之后
				// 则应该取excel结束日期为结束日期 2018/10/27
				UFLiteralDate overDate = null;
				if ((null == enddate)
						|| (enddate.afterDate(excelEnddate) && !excelEnddate
								.beforeDate(indutydate))) {
					overDate = excelEnddate;
				} else {
					overDate = enddate;
				}
				// END 张恒{{23045}} 将人员结束日期与excel结束日期进行比对 如果人员结束日期在excel结束日期之后
				
				if (indutydate != null && UFLiteralDate.getDate(fieldNames[0]).before(indutydate)
						&& UFLiteralDate.getDate(fieldNames[fieldNames.length - 1]).after(indutydate)) {
					// BEGIN 张恒{23045} 获取员工满足excel导入的天数 2018/10/27

					// int endday=30;
					// if(enddate!=null)
					// {
					// endday=Integer.valueOf(enddate.toString().substring(8));
					// }else{
					// endday=indutydate.getDaysMonth();
					// }
					//
					// int indutynumber= endday -
					// Integer.valueOf(indutydate.toString().substring(8))+1;
					int indutynumber = overDate.getDaysAfter(startDate) + 1;

					// END 张恒{23045} 获取员工满足excel导入的天数 2018/10/27

					totalLJdaysthisMonth = indutynumber / 7;
					if (indutynumber % 7 > 5) {
						totalLJdaysthisMonth = totalLJdaysthisMonth + 1;
					}

					totalXXRdaysthisMonth = indutynumber / 7;
					// for(int j=indutynumber-1;j<fieldNames.length;j++){
					// if (UFLiteralDate.getDate(fieldNames[j]).getWeek()==0)
					// {
					// totalLJdaysthisMonth=totalLJdaysthisMonth+1;
					// }
					// if (UFLiteralDate.getDate(fieldNames[j]).getWeek()==6)
					// {
					// totalXXRdaysthisMonth=totalXXRdaysthisMonth+1;
					// }
					// }
					// if (null!=indutydate &&indutydate.getWeek()==6
					// ||indutydate.getWeek()==3 ||indutydate.getWeek()==4
					// ||indutydate.getWeek()==5 ||indutydate.getWeek()==0){
					// totalXXRdaysthisMonth=totalXXRdaysthisMonth-1;
					// }
				} else if (enddate != null && UFLiteralDate.getDate(fieldNames[fieldNames.length - 1]).after(enddate)
						&& UFLiteralDate.getDate(fieldNames[0]).before(enddate)) {

					// BEGIN 张恒{23045} 获取员工满足excel导入的天数 2018/10/27
					// int startday=1;
					// if(indutydate!=null)
					// {
					// startday=Integer.valueOf(indutydate.toString().substring(8));
					// }
					// int
					// enddaynumber=Integer.valueOf(enddate.toString().substring(8))-startday+1;
					int enddaynumber = overDate.getDaysAfter(startDate) + 1;
					// END 张恒{23045} 获取员工满足excel导入的天数 2018/10/27
					totalLJdaysthisMonth = enddaynumber / 7;
					if (enddaynumber % 7 > 5) {
						totalLJdaysthisMonth = totalLJdaysthisMonth + 1;
					}
					// if (UFLiteralDate.getDate(fieldNames[j]).getWeek()==6)
					// {
					// totalXXRdaysthisMonth=totalXXRdaysthisMonth+1;
					// }

					totalXXRdaysthisMonth = enddaynumber / 7;

					// if (UFLiteralDate.getDate(fieldNames[j]).getWeek()==0)
					// {
					// totalLJdaysthisMonth=totalLJdaysthisMonth+1;
					// }
					// if (UFLiteralDate.getDate(fieldNames[j]).getWeek()==6)
					// {
					// totalXXRdaysthisMonth=totalXXRdaysthisMonth+1;
					// }

					// for(int j=0;j<enddaynumber;j++){
					// totalLJdaysthisMonth=enddaynumber/7;
					// if(enddaynumber%7>5)
					// {
					// totalLJdaysthisMonth=totalLJdaysthisMonth+1;
					// }
					// // if (UFLiteralDate.getDate(fieldNames[j]).getWeek()==0)
					// // {
					// // totalLJdaysthisMonth=totalLJdaysthisMonth+1;
					// // }
					// if (UFLiteralDate.getDate(fieldNames[j]).getWeek()==6)
					// {
					// totalXXRdaysthisMonth=totalXXRdaysthisMonth+1;
					// }
					// }
					// if (null!=enddate && (enddate5.getWeek()==2
					// ||enddate.getWeek()==3 ||enddate.getWeek()==4
					// ||enddate.getWeek()==5 ||enddate.getWeek()==6)){
					// totalLJdaysthisMonth=totalLJdaysthisMonth+1;
					// }
				} else {
					for (int j = 0; j < fieldNames.length; j++) {
						if (UFLiteralDate.getDate(fieldNames[j]).getWeek() == 0) {
							totalLJdaysthisMonth = totalLJdaysthisMonth + 1;
						}
						if (UFLiteralDate.getDate(fieldNames[j]).getWeek() == 6) {
							totalXXRdaysthisMonth = totalXXRdaysthisMonth + 1;
						}
					}
				}
				if (totalXXRdaysthisMonth < 0) {
					totalXXRdaysthisMonth = 0;
				}

				if (totalLJdaysthisMonth < 0) {
					totalLJdaysthisMonth = 0;
				}
				Integer currLJdays = 0;
				Integer currXXRdays = 0;

				// get 2-26,2-27,2-28 LJXXR days
				Integer LJdaysinFeb2018 = 0;
				Integer XXRdaysinFeb2018 = 0;

				if (fieldNames[0].toString().substring(0, 7).equals("2018-03")) {
					LJdaysinFeb2018 = getLJdaysinFeb2018(psncode);
					XXRdaysinFeb2018 = getXXRdaysinFeb2018(psncode);
					currLJdays = LJdaysinFeb2018;
					currXXRdays = XXRdaysinFeb2018;
				}

				// LEO End
				int endday = 30;
				if (enddate != null && UFLiteralDate.getDate(fieldNames[fieldNames.length - 1]).after(enddate)
						&& UFLiteralDate.getDate(fieldNames[0]).before(enddate)) {
					endday = Integer.valueOf(enddate.toString().substring(8));
				} else {

					endday = Integer.valueOf(fieldNames[fieldNames.length - 1].substring(8));
				}
				// endday=UFLiteralDate.getDate(fieldNames[fieldNames.length-1]).after(getEndDayByPsn(psncode))==true?(Integer.valueOf(getStartDayByPsn(psncode).toString().substring(8))):0;
				//
				// int endday=30;
				// if (enddate!=null &&
				// UFLiteralDate.getDate(fieldNames[fieldNames.length-1]).after(enddate)
				// && UFLiteralDate.getDate(fieldNames[0]).before(enddate))
				// {
				// endday=Integer.valueOf(enddate.toString().substring(8));
				// }else{
				// endday=indutydate.getDaysMonth();
				// }
				int startday = 1;
				if (indutydate != null && UFLiteralDate.getDate(fieldNames[0]).before(indutydate)
						&& UFLiteralDate.getDate(fieldNames[fieldNames.length - 1]).after(indutydate)) {
					startday = Integer.valueOf(indutydate.toString().substring(8));
				}
				for (int j = startday - 1; j < endday; j++) {
					String naHoliday = "";
					// 取excel上的 假别，本店上班1，本店上班2
					if (sheet.getCell(naHolidayColumnPsn, nowRow).getContents().trim().toString() != null
							&& sheet.getCell(naHolidayColumnPsn, nowRow).getContents().trim().toString().equals("X")) {
						naHoliday = sheet.getCell(j + beDay + 5, nowRow).getContents().trim().toString();
					} else {
						naHoliday = sheet.getCell(j + beDay + 5, naHolidayRow).getContents().trim().toString();
					}
					// String naHoliday = sheet.getCell(j+beDay+5,
					// naHolidayRow).getContents().trim().toString();
					String leaveType = sheet.getCell(j + beDay + 5, nowRow).getContents().trim().toString();
					String beginTime = sheet.getCell(j + beDay + 5, nowRow + 1).getContents().trim().toString();
					String endTime = sheet.getCell(j + beDay + 5, nowRow + 2).getContents().trim().toString();

					// 校验排班数据是否合规，不合规抛错，不执行之后操作
					if (!"V".equals(psncl))
						errorMsg = checkDayTime(psncode, fieldNames[j], j + beDay + 5, nowRow);

					if (!"".equals(errorMsg)) {

						CommonUtil.showErrorDialog(errorMsg + "," + getCoCoRes("coco_cal_res020") + "[" + psn + "]");//
					}

					// 通过上下班时间获取对应班次名称
					String shiftName = getShiftName(psn, naHoliday, leaveType, beginTime, endTime, fieldNames[j]);

					// 连输上班天数校验
					if (!"V".equals(psncl)) {
						errorMsg = checkConWorkDays(psn, shiftName);
					}
					if (!"".equals(errorMsg)) {

						CommonUtil.showErrorDialog(errorMsg + "," + getCoCoRes("coco_cal_res020") + "[" + psn + "]");
					}

					// check LJXXR
					// one LJ one XXR case- not use any more
					// if (j>=addstartdaynumber && j<=enddaynumber &&
					// !"V".equals(psncl)){//new join staff, resign
					// errorMsg = checkWeekLjXxr(psn,fieldNames[j],shiftName);
					//
					// if(!"".equals(errorMsg)){
					//
					// CommonUtil.showErrorDialog(errorMsg+","+getCoCoRes("coco_cal_res020")+"["+psn+"]");//一周内必须要有一个例假日一个休息日
					// }
					// }

					if (LJ.equals(leaveType)) {
						currLJdays = currLJdays + 1;
					} else if (XXR.equals(leaveType)) {
						currXXRdays = currXXRdays + 1;
					}

					vo.setAttributeValue(fieldNames[j], shiftName);
				}

				// check LJXXR by month
				if (!"V".equals(psncl)) {
					if (totalLJdaysthisMonth != currLJdays) {
						CommonUtil.showErrorDialog("[" + psn + " 當月例假日" + totalLJdaysthisMonth + " 當前" + currLJdays
								+ " 請檢查]");
					}
					if (totalXXRdaysthisMonth != currXXRdays) {
						CommonUtil.showErrorDialog("[" + psn + " 當月休息日" + totalXXRdaysthisMonth + " 當前" + currXXRdays
								+ " 請檢查]");
					}
				}

				// 之后会用到
				vo.setAttributeValue(PsnCalendarCommonValue.LISTCODE_ISNULLROW, new UFBoolean(false));

				if (hrorgImportvosMap.get(psnHrorgMap.get(psn)) == null) {

					ArrayList list = new ArrayList();
					list.add(vo);
					hrorgImportvosMap.put(psnHrorgMap.get(psn), list);
				} else {

					hrorgImportvosMap.get(psnHrorgMap.get(psn)).add(vo);
				}
			} else {
				extramsg += "獶セ┍ " + psn + "     ";
			}
		}
		return hrorgImportvosMap;
	}

	private UFLiteralDate getStartDayByPsn(String psncode) {
		String pk_psnjob = "";
		UFLiteralDate indutydate = null;
		// ssx remarked for TWLC
		// on 2018-10-11
		try {
			indutydate = NCLocator.getInstance().lookup(IPsndocQryService.class).queryFirstIndutyDate(psncode);
		} catch (BusinessException e) {
			e.printStackTrace();
		}

		return indutydate;
	}

	private UFLiteralDate getEndDayByPsn(String psncode) {
		String pk_psnjob = "";
		UFLiteralDate enddate = null;
		// ssx remarked for TWLC
		// on 2018-10-11
		try {
			enddate = NCLocator.getInstance().lookup(IPsndocQryService.class).queryEndDate(psncode);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return enddate;
	}

	private Integer getLJdaysinFeb2018(String psncode) {
		Integer LJdays = 0;
		// ssx remarked for TWLC
		// on 2018-10-11
		try {

			LJdays = NCLocator.getInstance().lookup(IPsndocQryService.class).queryLJdaysinFeb2018(psncode);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return LJdays;

	}

	private Integer getXXRdaysinFeb2018(String psncode) {
		Integer XXRdays = 0;
		// ssx remarked for TWLC
		// on 2018-10-11
		try {
			XXRdays = NCLocator.getInstance().lookup(IPsndocQryService.class).queryXXRdaysinFeb2018(psncode);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return XXRdays;

	}

	/**
	 * 一周内有且只有一个例假和休息日
	 * 
	 * @param psn
	 * @param shiftName
	 * @param shiftName2
	 * @return
	 */
	private String checkWeekLjXxr(String psn, String date, String shiftName) {

		String errorMsg = "";

		String pk_shift = psnDateShiftHolidayTypeMap.get(psnPkMap.get(psn)).get(date);

		shiftName = pk_shift == null ? shiftName : shiftPkNameMap.get(pk_shift);

		if (weekLjXxrMap.get(psn) == null) {

			weekLjXxrMap.put(psn, new HashMap());
		}

		HashMap restMap = weekLjXxrMap.get(psn);

		if (LJ.equals(shiftName) || XXR.equals(shiftName)) {

			Integer days = (Integer) restMap.get(shiftName);

			if (days != null && days == 1) {

				errorMsg = getCoCoRes("coco_cal_res024") + shiftName + "-" + date; /*
																					 * @
																					 * res
																					 * 一周内不能有一个以上
																					 */
			} else {

				restMap.put(shiftName, 1);
			}
		}

		// 如果是周天，重置
		if (UFLiteralDate.getDate(date).getWeek() == 0) {

			if ((Integer.valueOf(0).equals(restMap.get(LJ)) || Integer.valueOf(0).equals(restMap.get(XXR)))) {

				errorMsg = getCoCoRes("coco_cal_res025") + "-" + date; /* 一周内必须要有一个例假日一个休息日 */
			}

			restMap.put(LJ, 0);
			restMap.put(XXR, 0);
		}

		return errorMsg;
	}

	/**
	 * 连续上班天数校验
	 * 
	 * @param shiftName
	 * @param shiftName2
	 */
	private String checkConWorkDays(String psn, String shiftName) {

		String errorMsg = "";

		Integer conWorkDays = conWorkDaysMap.get(psn);

		if (conWorkDays == null) {

			// conWorkDays = 0;
			errorMsg = getCoCoRes("coco_cal_res023");/* @res 该人员不属于这个组织或没有对应的考勤档案 */
		} else {

			if (restName.contains(shiftName)) {

				conWorkDays = 0;
			} else {

				conWorkDays++;
			}

			if (conWorkDays > 12) { // {18765-1} 2018-02-23 7days change to
									// <13days

				errorMsg = getCoCoRes("coco_cal_res022");/* @RES 本月或上月已连续上班7天 */
			} else {

				conWorkDaysMap.put(psn, conWorkDays);
			}
		}

		return errorMsg;
	}

	/**
	 * 加载当前组织下所有人员在date前已连续加班天数
	 * 
	 * @param date
	 * @throws BusinessException
	 */
	private void loadConWorkDays(String date) throws BusinessException {

		// 模仿nc端查询，所以强制设计fromWhereSql；可以自己拼sql查询，只要有对应的排班以及人员编码以及姓名
		/*
		 * FromWhereSQLImpl fromWhereSql = new FromWhereSQLImpl();
		 * 
		 * String fromSql =
		 * "tbm_psndoc tbm_psndoc left outer join hi_psnjob T1 ON T1.pk_psnjob = tbm_psndoc.pk_psnjob"
		 * ;
		 * 
		 * InSQLCreator inSqlCreator = new InSQLCreator();
		 * 
		 * String inSql = inSqlCreator.getInSQL(getAllOrgPksOfPsn());
		 * 
		 * String whereSql = "T1.pk_org in (" +inSql + ")";
		 * 
		 * HashMap attrpath_alias_map = new HashMap();
		 * 
		 * attrpath_alias_map.put(".", "tbm_psndoc");
		 * 
		 * attrpath_alias_map.put("pk_psnjob", "T1");
		 * 
		 * fromWhereSql.setFrom(fromSql);
		 * 
		 * fromWhereSql.setWhere(whereSql);
		 * 
		 * fromWhereSql.setAttrpath_alias_map(attrpath_alias_map);
		 */
		// UFLiteralDate UFdate=UFLiteralDate.getDate(date);

		// edit before 2018-04-26
		// UFLiteralDate endDate = UFLiteralDate.getDate(date).getDateBefore(1);
		// UFLiteralDate beginDate = endDate.getDateBefore(6);
		// edit on 2018-04-26
		UFLiteralDate endDate = new UFLiteralDate(date.substring(0, 8)
				+ String.valueOf(UFLiteralDate.getDate(date).getDaysMonth()));
		UFLiteralDate beginDate = UFLiteralDate.getDate(date);

		// PsnJobCalendarVO[] psnCalVOs =
		// NCLocator.getInstance().lookup(IPsnCalendarQueryMaintain.class).queryCalendarVOByCondition(SessionUtil.getPk_org(),fromWhereSql,beginDate,endDate);

		QryConditionVO qryCondVo = (QryConditionVO) SessionUtil.getSessionBean().getExtendAttributeValue(
				WorkCalendarConsts.SESSION_QRY_CONDITIONS);

		((FromWhereSQLImpl) qryCondVo.getFromWhereSQL()).setWhere("1=1");

		List<PsnJobCalendarVO> psnCalVOs = new ArrayList<PsnJobCalendarVO>();

		for (String pk_dept : getAllMngDeptPks()) {
			PsnJobCalendarVO[] calvos = CalendarUtils.getDeptPsnCalendar(pk_dept, SessionUtil.isIncludeSubDept(),
					beginDate, endDate, qryCondVo.getArrangeflag(), qryCondVo.getFromWhereSQL());

			if (calvos != null && !ArrayUtils.isEmpty(calvos))
				psnCalVOs.addAll(Arrays.asList(calvos));
		}

		ArrayList<String> psnPks = new ArrayList<String>();

		ArrayList<String> psnjobPks = new ArrayList<String>();

		HashMap<String, String> psnCalMap = new HashMap<String, String>();

		HashMap<String, Integer> pkConWorkDaysMap = new HashMap<String, Integer>();

		HashMap<String, HashMap<String, Integer>> pkWeekLjXxrMap = new HashMap<String, HashMap<String, Integer>>();

		if (psnCalVOs == null)
			return;

		for (PsnJobCalendarVO vo : psnCalVOs) {

			psnPks.add(vo.getPk_psndoc());

			psnjobPks.add(vo.getPk_psnjob());

		}

		Map<String, Map<UFLiteralDate, AggPsnCalendar>> psncalendarVOs = NCLocator.getInstance()
				.lookup(IPsnCalendarQueryService.class)
				.queryCalendarVOByPsnInSQLForProcess(null, beginDate, endDate, psnPks.toArray(new String[0]));

		for (PsnJobCalendarVO vo : psnCalVOs) {

			Integer conWorkDays = 0;

			HashMap dateShiftMap = (HashMap) vo.getCalendarMap();
			HashMap psncalendarVOsMap = null;
			if (null != psncalendarVOs) { // leo
				psncalendarVOsMap = (HashMap) psncalendarVOs.get(vo.getPk_psndoc());
			}

			HashMap<String, Integer> LjXxrMap = new HashMap<String, Integer>();

			LjXxrMap.put(LJ, 0);

			LjXxrMap.put(XXR, 0);

			for (int i = 0; i < 7; i++) {

				UFLiteralDate ufDate = beginDate.getDateAfter(i);

				String pk_shiftholidaytype = null;

				if (psncalendarVOsMap != null && psncalendarVOsMap.get(ufDate) != null) {
					// ssx remarked for TWLC
					// on 2018-10-11
					pk_shiftholidaytype = ((AggPsnCalendar) psncalendarVOsMap.get(ufDate)).getPsnCalendarVO()
							.getPk_shiftholidaytype();
				}

				// 例假休息日排班怎么处理，待处理
				String shiftName = shiftPkNameMap.get(dateShiftMap.get(ufDate.toString()));
				String shiftholidaytype = shiftPkNameMap.get(pk_shiftholidaytype);

				if (shiftName == null || restName.contains(shiftName)) {

					conWorkDays = 0;
				} else {

					conWorkDays++;
				}

				if (ufDate.getWeek() == 0) {

					LjXxrMap.put(LJ, 0);
					LjXxrMap.put(XXR, 0);
				}// tong
				else if (shiftName == null && shiftholidaytype == null) // check
																		// if
																		// this
																		// is
																		// first
																		// time
																		// to
																		// import
																		// shift;
																		// only
																		// 0
																		// value
																		// will
																		// return
																		// error;ㄒヰ
				{
					LjXxrMap.put(shiftName, -1);
					LjXxrMap.put(shiftholidaytype, -1);
				} else if ("默认班次".equals(shiftName)) // check if this 羉砰s first
														// time to import shift;
														// only 0 value will
														// return error;ㄒヰ
				{
					LjXxrMap.put(shiftName, -2);
					LjXxrMap.put(shiftholidaytype, -2);
				} else if (LJ.equals(shiftName) || XXR.equals(shiftName)) {

					LjXxrMap.put(shiftName, 1);
				} else if (LJ.equals(shiftholidaytype) || XXR.equals(shiftholidaytype)) {

					LjXxrMap.put(shiftholidaytype, 1);
				}
			}

			pkWeekLjXxrMap.put(vo.getPk_psndoc(), LjXxrMap);

			pkConWorkDaysMap.put(vo.getPk_psndoc(), conWorkDays);
		}

		PsnJobVO[] psnjobVOs = NCLocator.getInstance().lookup(IPsndocQryService.class)
				.queryPsnjobByPKs(psnjobPks.toArray(new String[0]));

		HashMap<String, String> pkPsnclMap = new HashMap<String, String>();

		HashMap<String, String> pkHrorgMap = new HashMap<String, String>();

		HashMap<String, String> pkOrgMap = new HashMap<String, String>();

		for (PsnJobVO jobVO : psnjobVOs) {

			pkPsnclMap.put(jobVO.getPk_psndoc(), jobVO.getPk_psncl());

			pkHrorgMap.put(jobVO.getPk_psndoc(), jobVO.getPk_hrorg());

			pkOrgMap.put(jobVO.getPk_psndoc(), jobVO.getPk_org());
		}
		// // String[] psnPksNew=;
		// psnPks.add("0001A11000000002LZ1C");
		PsndocVO[] psnVOs = NCLocator.getInstance().lookup(IPsndocQueryService.class)
				.queryPsndocByPks(psnPks.toArray(new String[psnPks.size()]));

		for (PsndocVO vo : psnVOs) {

			conWorkDaysMap.put(vo.getCode() + MultiLangHelper.getName(vo), pkConWorkDaysMap.get(vo.getPk_psndoc()));

			weekLjXxrMap.put(vo.getCode() + MultiLangHelper.getName(vo), pkWeekLjXxrMap.get(vo.getPk_psndoc()));

			psnPkMap.put(vo.getCode() + MultiLangHelper.getName(vo), vo.getPk_psndoc());

			psnHrorgMap.put(vo.getCode() + MultiLangHelper.getName(vo), pkHrorgMap.get(vo.getPk_psndoc()));

			psnOrgMap.put(vo.getCode() + MultiLangHelper.getName(vo), pkOrgMap.get(vo.getPk_psndoc()));
		}

	}

	private List<String> getAllMngDeptPks() {

		List DeptPks = new ArrayList();

		for (HRDeptVO deptvo : SessionUtil.getHRDeptVO()) {

			DeptPks.add(deptvo.getPk_dept());
		}

		return DeptPks;
	}

	private String[] getAllOrgPksOfPsn() {

		List orgPks = new ArrayList();

		for (HRDeptVO deptvo : SessionUtil.getHRDeptVO()) {

			orgPks.add(deptvo.getPk_org());
		}

		orgPks.add(SessionUtil.getPk_org());

		return (String[]) orgPks.toArray(new String[orgPks.size()]);
	}

	/**
	 * 排班時數低於每月最低工時，不予導入; 加班超過46小時; 每月2号后不能再导入前月数据;
	 * 
	 * @param psncl
	 * */
	private String checkWorkTime(String psncl, int nowRow) {

		String errorMsg = "";

		int overtimeColumn = 39;
		int absencetimeColumn = 40;

		float overtime = Float.parseFloat(sheet.getCell(overtimeColumn, nowRow).getContents().toString().trim());
		float absencetime = Float.parseFloat(sheet.getCell(absencetimeColumn, nowRow).getContents().toString().trim());

		/*
		 * if (getOffDate().before(new Date())) {
		 * 
		 * // errorMsg = getCoCoRes("coco_cal_res021");//每月2号后不能再导入前月数据 }
		 */

		// 2018-01-25 customer say this check do not need any more;
		// if(overtime < absencetime){
		//
		// errorMsg = getCoCoRes("coco_cal_res013");//导入班表时排班时数低于每月最低工时，不予导入
		// }// 雇主不校验其他规则
		/* else */if (!"V".equals(psncl)) {

			if (overtime > 46) {

				errorMsg = getCoCoRes("coco_cal_res014");// 加班超過46小時
			}
		}

		return errorMsg;
	}

	/**
	 * 获取不可导入日期
	 * 
	 * @return
	 */
	private Date getOffDate() {

		Integer year = Integer.parseInt(sheet.getCell(18, 0).getContents().trim().toString());
		Integer month = Integer.parseInt(sheet.getCell(20, 0).getContents().trim().toString());

		month = (month - 1) < 0 ? 12 : month - 1;

		Calendar cal = Calendar.getInstance();

		cal.set(year, month, 2); // 每月2号后不能再导入前月数据

		cal.add(Calendar.MONTH, 1);

		return cal.getTime();
	}

	/**
	 * 校验每天上下班时间
	 * 
	 * @param date
	 * @param psncode
	 * @param column
	 * @param nowRow
	 * @return
	 */
	private String checkDayTime(String psncode, String date, int column, int nowRow) {

		String errorMsg = "";

		float workLen = Float.parseFloat(sheet.getCell(column, nowRow + 8).getContents().toString().trim());// customer
																											// add
																											// one
																											// more
																											// line
																											// to
																											// excel;

		String leaveType = sheet.getCell(column, nowRow).getContents().trim().toString();

		String beginTime = sheet.getCell(column, nowRow + 1).getContents().trim().toString();

		String endTime = sheet.getCell(column, nowRow + 2).getContents().trim().toString();

		String psnname = sheet.getCell(1, nowRow).getContents().trim().toString();
		String psn = psncode + psnname;
		// 每工作日工作時長不能超過12小時
		if (workLen > 12) {

			errorMsg = getCoCoRes("coco_cal_res015");// 每个工作日工作时长不能超过12小时
		}// 出現例假日排班時，不予導入班表
		else if (LJ.equals(leaveType)
				&& !(beginTime == null || endTime == null || "".equals(beginTime) || "".equals(endTime))) {

			errorMsg = getCoCoRes("coco_cal_res016");// 例假日排班，不予导入
		}

		if (workLen < 8 && !(beginTime == null || endTime == null || "".equals(beginTime) || "".equals(endTime))) {

			ArrayList ls = shortCalMap.get(psn); // leo fix:duan ban only show
													// one;

			if (ls == null)
				ls = new ArrayList();

			ls.add(date.substring(8));

			shortCalMap.put(psn, ls);
		}
		// CommonUtil.showMessageDialog(getCoCoRes("coco_cal_res027"));//短班

		return errorMsg;
	}

	/**
	 * 加载班次信息
	 * 
	 * @throws BusinessException
	 */
	private void loadShift() throws BusinessException {

		List<AggShiftVO> aggShiftVOs = getShiftVOs();

		if (aggShiftVOs != null && !aggShiftVOs.isEmpty()) {

			for (AggShiftVO aggShiftVO : aggShiftVOs) {

				ShiftVO shiftVO = aggShiftVO.getShiftVO();

				// 此处使用的name应为多语环境下当前语种的name，不能直接使用getname否则不匹配，不同的业务单元下有重复的名称，导致匹配失败

				// shiftNameMap.put(shiftVO.getPk_org() + "-" +
				// shiftVO.getDefaultflag() , MultiLangHelper.getName(shiftVO));

				// shiftMap.put(new
				// String[]{shiftVO.getBegintime(),shiftVO.getEndtime()},MultiLangHelper.getName(shiftVO));

				// 此处直接获取默认班次名，之后可以直接取不用再查
				if (shiftVO.getDefaultflag().booleanValue() && shiftVO.getPk_org().equals(SessionUtil.getPk_org())) {

					defaultShiftName = MultiLangHelper.getName(shiftVO);
				}

				shiftVOs.add(shiftVO);
				shiftWorktimeName.put(shiftVO.getBegintime() + "-" + shiftVO.getEndtime() + "-" + shiftVO.getPk_org(),
						MultiLangHelper.getName(shiftVO));

				shiftPkNameMap.put(shiftVO.getPk_shift(), MultiLangHelper.getName(shiftVO));
			}
		}

		shiftPkNameMap.put(WorkCalendarConsts.SHIFTPK_GX, GX); // 公休
		shiftPkNameMap.put(WorkCalendarConsts.SHIFTPK_LJ, LJ); // 例假
		shiftPkNameMap.put(WorkCalendarConsts.SHIFTPK_GDJ, GDJ); // 国定假
		shiftPkNameMap.put(WorkCalendarConsts.SHIFTPK_XXR, XXR); // 休息日
		shiftPkNameMap.put(WorkCalendarConsts.SHIFTPK_KB, KB); // 空班

	}

	/**
	 * 获取拥有部门管理权限对应组织下的班次
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private List<AggShiftVO> getShiftVOs() throws BusinessException {

		Map<String, AggShiftVO> map = ShiftServiceFacade.queryShiftAggVOMapByOrgs(getAllOrgPksOfPsn());

		if (map == null)
			return null;

		List ls = new ArrayList();

		Iterator it = map.entrySet().iterator();

		while (it.hasNext()) {

			Map.Entry entry = (Entry) it.next();

			ls.add(entry.getValue());
		}

		return ls;
	}

	/**
	 * 获取班次名称
	 * 
	 * @param psncl
	 * @param naHoliday
	 * @param leaveType
	 * @param beginTime
	 * @param endTime
	 * @param fieldName
	 * @return
	 * @throws BusinessException
	 */
	private String getShiftName(String psn, String naHoliday, String leaveType, String beginTime, String endTime,
			String date) throws BusinessException {

		List leaveTypes = Arrays.asList(new String[] { GDJ, LJ, XXR });

		String shiftname = "";
		String ht = leaveType;
		// 以下班次没有打卡时间，排入默认排班
		List defShiftLeaTps = Arrays.asList(new String[] { getCoCoRes("coco_cal_res004")/* 特休 */,
				getCoCoRes("coco_cal_res006")/* 产假 */, getCoCoRes("coco_cal_res007")/* 其它假 */,
				getCoCoRes("coco_cal_res008")/* 事假 */, getCoCoRes("coco_cal_res009")/* 会议 */,
				getCoCoRes("coco_cal_res010")/* 休息日会议 */, getCoCoRes("coco_cal_res026") /* X */});

		if (beginTime != null && endTime != null && !"".equals(beginTime) && !"".equals(endTime)) {

			String bTime = dealTimeForm(beginTime);
			String eTime = dealTimeForm(endTime);
			if (Integer.valueOf(eTime.substring(0, 2)) >= 24) {
				String neweTime = "0" + String.valueOf(Integer.valueOf(eTime.substring(0, 2)) - 24) + ":"
						+ eTime.substring(3, 5) + ":00";
				shiftname = shiftWorktimeName.get(bTime + "-" + neweTime + "-" + psnOrgMap.get(psn)) == null ? ""
						: shiftWorktimeName.get(bTime + "-" + neweTime + "-" + psnOrgMap.get(psn));
			} else {
				shiftname = shiftWorktimeName.get(bTime + "-" + eTime + "-" + psnOrgMap.get(psn)) == null ? ""
						: shiftWorktimeName.get(bTime + "-" + eTime + "-" + psnOrgMap.get(psn));
			}

			if ("".equals(shiftname)) {

				ShiftVO shiftvo = createNewShift(beginTime, endTime, psnOrgMap.get(psn));

				shiftVOs.add(shiftvo);

				shiftPkNameMap.put(shiftvo.getPk_shift(), MultiLangHelper.getName(shiftvo));

				if (Integer.valueOf(eTime.substring(0, 2)) >= 24) {
					String neweTime = "0" + String.valueOf(Integer.valueOf(eTime.substring(0, 2)) - 24) + ":"
							+ eTime.substring(3, 5) + ":00";
					shiftWorktimeName.put(bTime + "-" + neweTime + "-" + psnOrgMap.get(psn),
							MultiLangHelper.getName(shiftvo));
				} else {
					shiftWorktimeName.put(bTime + "-" + eTime + "-" + psnOrgMap.get(psn),
							MultiLangHelper.getName(shiftvo));
				}

				shiftname = MultiLangHelper.getName(shiftvo);
			}
		} else {

			if (GDJ.equals(naHoliday)) {

				shiftname = naHoliday;
			} else if (leaveTypes.contains(leaveType)) {

				shiftname = leaveType;
			} else if (defShiftLeaTps.contains(leaveType)) {

				shiftname = defaultShiftName;
			} else {

				shiftname = KB;
			}
		}

		String hdType = null;

		if (GDJ.equals(naHoliday) || GDJ.equals(leaveType)) {

			hdType = WorkCalendarConsts.SHIFTPK_GDJ;
		} else if (LJ.equals(leaveType)) {

			hdType = WorkCalendarConsts.SHIFTPK_LJ; // edited on 12/27
		} else if (XXR.equals(leaveType)) {

			hdType = WorkCalendarConsts.SHIFTPK_XXR;
		}

		HashMap<String, String> dateHdMap = psnDateShiftHolidayTypeMap.get(psnPkMap.get(psn));

		if (dateHdMap == null) {

			dateHdMap = new HashMap<String, String>();
			dateHdMap.put(date, hdType);
		} else {

			dateHdMap.put(date, hdType);
		}

		psnDateShiftHolidayTypeMap.put(psnPkMap.get(psn), dateHdMap);

		return shiftname;
	}

	private ShiftVO createNewShift(String beginTime, String endTime, String pk_org) throws BusinessException {

		String bTime = dealTimeForm(beginTime);
		String eTime = dealTimeForm(endTime);
		int workLength = 0;
		if (Integer.valueOf(eTime.substring(0, 2)) >= 24) {
			workLength = (int) ((new UFTime("23:59:59").getMillis() - new UFTime(bTime).getMillis() + 1000 + new UFTime(
					(Integer.valueOf(eTime.substring(0, 2)) - 24 + 8) >= 10 ? String.valueOf(Integer.valueOf(eTime
							.substring(0, 2)) - 24 + 8) + ":" + eTime.substring(3, 5) + ":00" : "0"
							+ String.valueOf(Integer.valueOf(eTime.substring(0, 2)) - 24 + 8) + ":"
							+ eTime.substring(3, 5) + ":00").getMillis()) / 60000L);
		} else {
			workLength = (int) ((new UFTime(eTime).getMillis() - new UFTime(bTime).getMillis()) / 60000L); // 工作时长：分钟
		}

		//

		RTVO rtvo = null;

		CapRTVO caprtvo = null;

		// 工作时长小于4小时，不排休息时间
		if (workLength > (4 * 60)) {

			// 休息时段
			rtvo = buildRtvo(bTime, eTime, pk_org);

			// 产能休息时段
			caprtvo = buildCapRtvo(bTime, eTime, pk_org);
		}

		ShiftVO shiftvo = buildShiftVO(beginTime, endTime, pk_org, rtvo);

		AggShiftVO aggShiftVO = new AggShiftVO();

		aggShiftVO.setParentVO(shiftvo);

		if (rtvo != null)
			aggShiftVO.setRTVOs(new RTVO[] { rtvo });

		if (caprtvo != null)
			aggShiftVO.setCAPRTVOs(new CapRTVO[] { caprtvo });

		aggShiftVO = ((IShiftManageMaintain) NCLocator.getInstance().lookup(IShiftManageMaintain.class))
				.insertShiftVO(aggShiftVO);

		return shiftvo;
	}

	private CapRTVO buildCapRtvo(String bTime, String eTime, String pk_org) {

		List<String> ls = getRtTime(bTime, eTime);

		String rtBtime = ls.get(0);
		String rtEtime = ls.get(1);

		CapRTVO caprtvo = new CapRTVO();

		if (Integer.valueOf(eTime.substring(0, 2)) >= 24) {
			caprtvo.setEndday(1);
		} else {
			caprtvo.setEndday(0);
		}

		// m_isdirty valueindex找不到
		caprtvo.setBeginday(new Integer(0));
		caprtvo.setBegintime(rtBtime);
		caprtvo.setCapresttime(new UFDouble(30.00000000));
		caprtvo.setDr(0);

		caprtvo.setEndtime(rtEtime);
		caprtvo.setPk_group(SessionUtil.getPk_group());
		caprtvo.setPk_org(SessionUtil.getHROrg());// 待处理
		caprtvo.setStatus(2);
		caprtvo.setTimeid(0);

		return caprtvo;
	}

	private RTVO buildRtvo(String bTime, String eTime, String pk_org) {

		List<String> ls = getRtTime(bTime, eTime);

		String rtBtime = ls.get(0);
		String rtEtime = ls.get(1);

		RTVO rtvo = new RTVO();

		if (Integer.valueOf(eTime.substring(0, 2)) >= 24) {
			rtvo.setEarliestendday(1);
			rtvo.setEndday(1);
		} else {
			rtvo.setEarliestendday(0);
			rtvo.setEndday(0);
		}
		// m_isdiry,valueindex 找不到
		rtvo.setBeginday(0);
		rtvo.setBegintime(rtBtime);
		rtvo.setCheckflag(new UFBoolean("N"));
		rtvo.setDr(0);

		rtvo.setEarliestendtime(rtEtime);
		rtvo.setEndtime(rtEtime);
		rtvo.setIsflexible(new UFBoolean("N"));
		rtvo.setLatestbeginday(0);
		rtvo.setLatestbegintime(rtEtime);
		rtvo.setPk_group(SessionUtil.getPk_group());
		rtvo.setPk_org(pk_org);// 待处理
		rtvo.setResttime(new UFDouble(30.00));// 待处理
		rtvo.setStatus(2);
		rtvo.setTimeid(0);
		rtvo.setVirBegintime(new UFTime(rtBtime));
		rtvo.setVirEndtime(new UFTime(rtEtime));

		return rtvo;
	}

	private List getRtTime(String rtBtime, String rtEtime) {
		List ls = new ArrayList();

		String processedrtEtime = rtEtime;
		String rtEtimeHour = rtEtime.substring(0, rtEtime.indexOf(":")).trim();
		if (Integer.valueOf(rtEtimeHour) >= 24) {
			ls.add("23:30:00");
			ls.add("00:00:00");
		} else {
			UFTime bt = new UFTime(rtBtime);
			UFTime et = new UFTime(rtEtime);

			long millis = (bt.getMillis() + et.getMillis()) / 2L;

			UFTime ut = new UFTime(millis);

			rtBtime = new UFTime(millis - 15 * 60000L).toString();
			rtEtime = new UFTime(millis + 15 * 60000L).toString();

			ls.add(rtBtime);
			ls.add(rtEtime);
		}

		return ls;
	}

	private ShiftVO buildShiftVO(String beginTime, String endTime, String pk_org, RTVO rtvo) {

		String bTime = dealTimeForm(beginTime);

		String eTime = dealTimeForm(endTime);

		ShiftVO shiftvo = new ShiftVO();

		// Integer workLen = (int) ((new UFTime(eTime).getMillis()-new
		// UFTime(bTime).getMillis())/1000L); //工作时长：秒 tong

		// leo >24
		Integer workLen = 0;
		if (Integer.valueOf(eTime.substring(0, 2)) >= 24) {
			workLen = (int) ((new UFTime("23:59:59").getMillis() - new UFTime(bTime).getMillis() + 1000 + new UFTime(
					(Integer.valueOf(eTime.substring(0, 2)) - 24 + 8) >= 10 ? String.valueOf(Integer.valueOf(eTime
							.substring(0, 2)) - 24 + 8) + ":" + eTime.substring(3, 5) + ":00" : "0"
							+ String.valueOf(Integer.valueOf(eTime.substring(0, 2)) - 24 + 8) + ":"
							+ eTime.substring(3, 5) + ":00").getMillis()) / 1000L);
		} else {
			workLen = (int) ((new UFTime(eTime).getMillis() - new UFTime(bTime).getMillis()) / 1000L);
		}

		Double rtLen = rtvo == null ? 0 : rtvo.getResttime().toDouble() * 60;

		UFDouble gzsj = new UFDouble((workLen - rtLen) / 3600.0);

		shiftvo.setGzsj(gzsj);
		shiftvo.setCapgzsj(gzsj);
		shiftvo.setCode(beginTime + "-" + endTime);
		shiftvo.setName(beginTime + "-" + endTime);
		shiftvo.setName2(beginTime + "-" + endTime);
		shiftvo.setName3(beginTime + "-" + endTime);
		shiftvo.setName4(beginTime + "-" + endTime);
		shiftvo.setName5(beginTime + "-" + endTime);
		shiftvo.setName6(beginTime + "-" + endTime);
		shiftvo.setWorklen(workLen);
		shiftvo.setAllowearly(new UFDouble(0.00000000));
		shiftvo.setAllowlate(new UFDouble(0.00000000));
		shiftvo.setBeginday(new Integer(0));
		shiftvo.setBegintime(bTime);
		shiftvo.setCapbeginday(new Integer(0));
		shiftvo.setCapbegintime(bTime);

		shiftvo.setDefaultflag(new UFBoolean("N"));
		shiftvo.setDr(new Integer(0));

		shiftvo.setEnablestate(new Integer(2));

		shiftvo.setIncludenightshift(new UFBoolean("N"));
		shiftvo.setIsallowout(new UFBoolean("N"));
		shiftvo.setIsautokg(new UFBoolean("Y"));
		shiftvo.setIscapedited(new UFBoolean("N"));
		shiftvo.setIsflexiblefinal(new UFBoolean("N"));
		shiftvo.setIshredited(new UFBoolean("N"));
		shiftvo.setIsotflexible(new UFBoolean("N"));
		shiftvo.setIsotflexiblefinal(new UFBoolean("N"));
		shiftvo.setIsrttimeflexible(new UFBoolean("N"));
		shiftvo.setIsrttimeflexiblefinal(new UFBoolean("N"));
		shiftvo.setIssinglecard(new UFBoolean("N"));
		shiftvo.setIsturn(new UFBoolean("N"));
		shiftvo.setLargeearly(new UFDouble(60.00000000));// #17682 2018-04-11
		shiftvo.setLargelate(new UFDouble(60.00000000));// #17682 2018-04-11
		shiftvo.setLatestbeginday(new Integer(0));
		shiftvo.setLatestbegintime(bTime);
		shiftvo.setNightbeginday(new Integer(0));
		shiftvo.setNightendday(new Integer(0));
		shiftvo.setPk_group(SessionUtil.getPk_group());
		shiftvo.setPk_org(pk_org);
		shiftvo.setStatus(0);
		shiftvo.setTimebeginday(new Integer(0));
		shiftvo.setTimebegintime(timeaddcalculator(bTime, -30));

		shiftvo.setUseontmrule(new UFBoolean("N"));
		shiftvo.setUseovertmrule(new UFBoolean("N"));

		// >24
		if (Integer.valueOf(eTime.substring(0, 2)) >= 24) {
			String neweTime = "0" + String.valueOf(Integer.valueOf(eTime.substring(0, 2)) - 24) + ":"
					+ eTime.substring(3, 5) + ":00";
			shiftvo.setEndday(new Integer(1));
			shiftvo.setEndtime(neweTime);
			shiftvo.setCapendday(new Integer(1));
			shiftvo.setCapendtime(neweTime);
			shiftvo.setTimeendday(new Integer(1));
			shiftvo.setTimeendtime(timeaddcalculator(neweTime, 31));
			shiftvo.setEarliestendday(new Integer(1));
			shiftvo.setEarliestendtime(neweTime);
		} else {
			if (eTime.equals("23:30:00")) {
				shiftvo.setTimeendday(new Integer(1));
				shiftvo.setTimeendtime("00:00:01");
			} else {
				shiftvo.setTimeendday(new Integer(0));
				shiftvo.setTimeendtime(timeaddcalculator(eTime, 31));
			}
			shiftvo.setEndday(new Integer(0));
			shiftvo.setEndtime(eTime);
			shiftvo.setCapendday(new Integer(0));
			shiftvo.setCapendtime(eTime);

			shiftvo.setEarliestendday(new Integer(0));
			shiftvo.setEarliestendtime(eTime);
		}
		// shiftvo.toRelativeKqScope();
		// shiftvo.toRelativeWorkScope();
		// shiftvo.toRelativeCapScope();
		// shiftvo.toRelativeFlexWorkScope();
		/*
		 * shiftvo.setCardtype(null); shiftvo.setCreationtime(null);
		 * shiftvo.setCreator(null); shiftvo.setDataoriginflag(null);
		 * shiftvo.setKghours(null); shiftvo.setTs(null);
		 * shiftvo.setPk_shift(null); shiftvo.setPk_shifttype(null);
		 * shiftvo.setPk_sort(null); shiftvo.setNightendtime(null);
		 * shiftvo.setNightgzsj(null); shiftvo.setOntmbeyond(null);
		 * shiftvo.setOntmend(null); shiftvo.setOvertmbegin(null);
		 * shiftvo.setOvertmbeyond(null); shiftvo.setNightbegintime(null);
		 * shiftvo.setMemo(null); shiftvo.setModifiedtime(null);
		 * shiftvo.setModifier(null);
		 */

		return shiftvo;
	}

	/**
	 * 整理打卡时间格式
	 * 
	 * @param time
	 * @return
	 */
	private String dealTimeForm(String time) {

		if (time == null) {
			CommonUtil.showErrorDialog(getCoCoRes("coco_cal_res038"));
		}

		// 时间只精确到分钟
		String h = "00";
		String m = "00";

		if (time.contains(".")) {

			h = time.substring(0, time.indexOf(".")).trim();
			h = h.length() == 1 ? "0" + h : h;
			m = time.substring(time.indexOf(".") + 1).trim();
			if (m.length() > 1) {
				CommonUtil.showErrorDialog(getCoCoRes("coco_cal_res039"));
			}
			m = String.valueOf(((Integer.parseInt(m)) * 6));
			m = m.length() == 1 ? "0" + m : m;

		} else {

			h = time.length() < 2 ? "0" + time : time;
		}

		return h + ":" + m + ":00";
	}

	private String timeaddcalculator(String myTime, int addminutes) {
		// String myTime = "14:10";
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		Date d = null;
		try {
			d = df.parse(myTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.MINUTE, addminutes);
		String newTime = df.format(cal.getTime());
		return newTime;
	}

	/**
	 * 获取要用到的表头信息
	 * 
	 * @param dateStartColumn
	 * @param dateOnRow
	 * @param sheet
	 * @return
	 * @throws BusinessException
	 */
	private String[] getHeadMessage(int dateStartColumn, int dateOnRow) throws BusinessException {

		Integer year = Integer.parseInt(sheet.getCell(18, 0).getContents().trim().toString());
		Integer month = Integer.parseInt(sheet.getCell(20, 0).getContents().trim().toString());

		// 获取每月天数
		int days = getMonthHasDays(year, month);

		beDay = getStartDate(year, month);
		beDay = 0; // do not lock?

		ArrayList namelist = new ArrayList();

		String dateValue = year.toString() + "-"
				+ (month.toString().length() == 1 ? "0" + month.toString() : month.toString());

		for (int i = beDay; i < days; i++) {

			String value = sheet.getCell(dateStartColumn + i, dateOnRow).getContents().trim().toString();

			UFLiteralDate dateTmp = UFLiteralDate.getDate(dateValue.toString() + "-"
					+ (value.toString().length() == 1 ? "0" + value.toString() : value.toString()));

			namelist.add(dateTmp.toString().trim());
		}

		String[] fieldNames = (String[]) namelist.toArray(new String[namelist.size()]);

		loadConWorkDays(fieldNames[0]);

		return fieldNames;
	}

	/**
	 * 两周前数据不能修改
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private int getStartDate(Integer year, Integer month) {

		String dateValue = year.toString() + "-"
				+ (month.toString().length() == 1 ? "0" + month.toString() : month.toString());

		UFLiteralDate dateTmp = UFLiteralDate.getDate(dateValue.toString() + "-01");

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.WEDNESDAY, -4);

		cal.getTime();

		int a = UFLiteralDate.getDaysBetween(dateTmp, new UFLiteralDate(cal.getTime()));

		a = a < 0 ? 0 : a;

		return a;
	}

	/**
	 * 获取对应月份包含天数
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private int getMonthHasDays(Integer year, Integer month) {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DATE, 1);
		cal.roll(Calendar.DATE, -1);
		int maxDate = cal.get(Calendar.DATE);
		return maxDate;
	}

	/**
	 * 打开excel
	 * 
	 * @param fullPath
	 * @return
	 */
	private Sheet openWorkbook(String fullPath) {

		Workbook workbook = null;
		InputStream is = null;
		String DEFAULT_PASSWORD = "8";
		try {
			is = new FileInputStream(fullPath);
			workbook = Workbook.getWorkbook(is);
		} catch (Exception ex) {

			Logger.error(ex.getMessage(), ex);
		} finally {
			IOUtils.closeQuietly(is);
		}

		if (workbook == null) {
			CommonUtil.showErrorDialog(getCoCoRes("coco_cal_res035"));
		}

		Sheet sheet = workbook.getSheet(getCoCoRes("coco_cal_res036"));

		if (sheet == null) {
			CommonUtil.showErrorDialog(getCoCoRes("coco_cal_res037"));
		}

		return sheet;

	}

	private String getCoCoRes(String string) {

		return ResHelper.getString(CAL_RES, string);
	}

	/**
	 * 导入文件选择对话框
	 */
	public boolean chooseFile() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser("UFIDA"/*
												 * System.getProperty("user.name"
												 * )
												 */);

			fileChooser.setFileFilter(xlsFilter);
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setAcceptAllFileFilterUsed(false);
		}

		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
			return false;
		}

		return true;
	}

}
