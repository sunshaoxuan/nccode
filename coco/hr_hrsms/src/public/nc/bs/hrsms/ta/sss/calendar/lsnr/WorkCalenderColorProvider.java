package nc.bs.hrsms.ta.sss.calendar.lsnr;

import nc.bs.hrsms.ta.sss.common.ShopTAUtil;
import nc.uap.lfw.core.comp.IWebPartContentFetcher;
import nc.uap.lfw.core.page.LfwView;
import nc.uap.lfw.core.page.LfwWindow;
import nc.uap.lfw.jsp.uimeta.UIMeta;
import nc.ui.ta.pub.IColorConst;
import nc.vo.ml.MultiLangContext;

public class WorkCalenderColorProvider implements IWebPartContentFetcher {
	
	/**
	 * 日历显示的DIV
	 */
	@Override
	public String fetchHtml(UIMeta um, LfwWindow pm, LfwView view) {
		/* 日历颜色说明 */
		/*Dataset ds = view.getViewModels().getDataset(CalendarForPsnPageModel.DATASET_CALENDAR);
		Row[] row = ds.getAllRow();
		AppLifeCycleContext.current().getParameter("ci_pk_psndoc");
		AppLifeCycleContext.current().getApplicationContext().getAppAttribute("ci_PsnJobCalendarVO");
		Row[] changedRows = DatasetUtil.getUpdatedRows(ds);*/
		return getColorBrief();
	}

	@Override
	public String fetchBodyScript(UIMeta um, LfwWindow pm, LfwView view) {
		return "";
	}
	
	/**
	 * 设置工作日历的颜色说明html
	 *
	 * @return
	 * @author haoy 2011-8-23
	 */
	private String getColorBrief() {
		String[][] cMap = new String[][] { 
			{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0036")/*@res "工作日"*/, ShopTAUtil.getHexDesc(nc.ui.bd.holiday.IColorConst.COLOR_WORKDAY) }, 
			{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_psncalendar", "psncalendar-0019")/*@res "非工作日-休息日"*/, ShopTAUtil.getHexDesc(nc.ui.bd.holiday.IColorConst.COLOR_NONWORKDAY) },
			{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_psncalendar", "psncalendar-0020")/*@res "国定假日"*/, ShopTAUtil.getHexDesc(nc.ui.bd.holiday.IColorConst.COLOR_HOLIDAY) },
			{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("twhr_psncalendar", "psncalendar-0021")/*@res "例假日"*/, ShopTAUtil.getHexDesc(nc.ui.bd.holiday.IColorConst.COLOR_OFFICALHOLIDAY) },
			{ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0037")/*@res "无考勤档案"*/, ShopTAUtil.getHexDesc(IColorConst.COLOR_NONTBMPSNDOC) } };
		
		String[] borders = new String[] { 
				ShopTAUtil.getHexDesc(nc.ui.bd.holiday.IColorConst.COLOR_WORKDAY_BORDER)/* 工作日 */,
				ShopTAUtil.getHexDesc(nc.ui.bd.holiday.IColorConst.COLOR_NONWORKDAY_BORDER)/* 非工作日-休息日 */,
				ShopTAUtil.getHexDesc(nc.ui.bd.holiday.IColorConst.COLOR_HOLIDAY_BORDER)/* 国定假日 */,
				ShopTAUtil.getHexDesc(nc.ui.bd.holiday.IColorConst.COLOR_OFFICALHOLIDAY_BORDER)/* 例假日 */,
				ShopTAUtil.getHexDesc(IColorConst.COLOR_NONTBMPSNDOC_BORDER) /* 无考勤档案 */};
		
		StringBuffer buf = new StringBuffer();
		buf.append("<table width=\"94%\" height=\"40px\" cellpadding =\"0\" cellspacing=\"0\" align=\"left\" style=\"margin-left:10px;\">");

		buf.append("<tr>");
		buf.append("<td>");
		/**如果语种不为简体中文这不显示农历选择*/
		//获取当前多语编号
		String currentLanguageCode = String.valueOf(MultiLangContext.getInstance().getCurrentLangVO().getLangcode());
		if("simpchn".equals(currentLanguageCode)) {
			buf.append("<input type=\"checkbox\" id=\"chkLunar\" checked=\"true\" onclick=\"showOrHideLunar(this.checked);\" >");
			buf.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("c_ta-res","0c_ta-res0138")/*@res "显示农历"*/);
			buf.append("</checkbox>");
		}		
		/**如果语种不为简体中文这不显示农历选择*/
		buf.append("</td>");
		for (int i = 0; i < cMap.length; i++) {
			buf.append("<td ><span style=\"width:20px;height:20px;margin-left:20px;border:1px solid ").append(borders[i]).append(";background-color:");
			buf.append(cMap[i][1]);
			buf.append("\">&nbsp;&nbsp;</span>");
			buf.append("<span style=\"margin-left:5px;\">");
			buf.append(cMap[i][0]);
			buf.append("</span></td>");
		}
		buf.append("<td id=\"totaltimes\" width=\"55%\">");
		buf.append("</td>");
		buf.append("</tr>");
		
		buf.append("</table>");
		return buf.toString();
	}

}
