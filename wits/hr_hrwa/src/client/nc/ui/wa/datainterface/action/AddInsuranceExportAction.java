package nc.ui.wa.datainterface.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.ui.hr.uif2.action.HrAction;
import nc.ui.pub.beans.UIDialog;
import nc.ui.wa.datainterface.AddInsuranceExportTextDlg;
import nc.ui.wa.util.LocalizationSysinitUtil;
import nc.vo.pub.BusinessException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

@SuppressWarnings("restriction")
public class AddInsuranceExportAction extends HrAction {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = -3937249333170798137L;

	String startPeriod = ""; // 日期

	public AddInsuranceExportAction() {
		super();
		super.setBtnName(ResHelper.getString("twhr_datainterface",
				"DataInterface-00079")); // 劳健退三合一加保
		super.setCode("AddInsuranceExportAction");
		super.putValue(Action.SHORT_DESCRIPTION, ResHelper.getString(
				"twhr_datainterface", "DataInterface-00079")); // 劳健退三合一加保
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		String pk_org = this.getModel().getContext().getPk_org();
		if (pk_org == null) {
			throw new BusinessException(ResHelper.getString(
					"twhr_datainterface", "DataInterface-00084"));// 请选择组织
		}
		AddInsuranceExportTextDlg dlg = new AddInsuranceExportTextDlg();
		int isGen = dlg.showModal();
		if (isGen == UIDialog.ID_OK) {
			String filepath = getExportFilePath("三合一加保.xls");
			if (null == filepath) {
				return;
			}
			if (dlg.getDateTime() == null && "".equals(dlg.getDateTime())) {
				throw new BusinessException(ResHelper.getString(
						"twhr_datainterface", "DataInterface-00082"));// 请选择日期
			}
			startPeriod = dlg.getDateTime().substring(0, 10);
			// startPeriod = "2018-01-30";
			IUAPQueryBS query = NCLocator.getInstance().lookup(
					IUAPQueryBS.class);
			String sql = "select 4 as move, hr."
					+ LocalizationSysinitUtil.getTwhrzzxx("TWHRZZXX01")
					+ " as insnum,"
					+ "hr."
					+ LocalizationSysinitUtil.getTwhrzzxx("TWHRZZXX02")
					+ " as checkcode, "
					+ "hr."
					+ LocalizationSysinitUtil.getTwhrzzxx("TWHRZZXX03")
					+ " as unitcode, "
					+ " case when g3.glbdef2='本人'then 2 else 3 end as inssort, "
					+ " case when g3.glbdef2='本人'then 1 else 2 end as insured, "
					+ " case doc.code  "
					+ " when 'LT06' then 'Y' "
					+ " when 'LT13' then '1'  "
					+ " when 'LT14' then '2' "
					+ " else '' end as insforeign, "
					+ " a.id as id,a.name as insname,a.birthdate as birthdate, "
					+ " g2.glbdef2 as wage,"
					+ " case when g3.glbdef16 is null "
					+ " then  "
					+ " (select top 1 h.glbdef16 from hi_psndoc_glbdef3 h left join bd_psndoc b on b.pk_psndoc=h.pk_psndoc "
					+ " where b.pk_psndoc=a.pk_psndoc and b.name2=a.name2) "
					+ " else g3.glbdef16 end  AS inssalary,"
					+ " doc4.code as special,"
					+ " case when doc2.code = 3 then '0' else '' end  as lfspecial , "
					+ " case when g3.glbdef2='本人' then '' "
					+ " when g3.glbdef2 is null then '' "
					+ " else g3.glbdef3 "
					+ " end  as jsid, "
					+ " case when g3.glbdef2='本人' then '' "
					+ " when g3.glbdef2 is null then '' "
					+ " else g3.glbdef1 "
					+ " end  as jsname, "
					+ " case when g3.glbdef2='本人' then '' "
					+ " when g3.glbdef2 is null then '' "
					+ " else substring(g3.glbdef4,0,11) "
					+ " end  as jsbirthdate, "
					+ " case g3.glbdef2  "
					+ " when '配偶' then 1  "
					+ " when '父母' then 2  "
					+ " when '子女' then 3  "
					+ " when '祖父母' then 4  "
					+ " when '孫子女' then 5  "
					+ " when '外祖父母' then 6  "
					+ " when '外孫子女' then 7  "
					+ " when '曾祖父母' then 8  "
					+ " when '外曾祖父母' then 9  "
					+ " end as title, "
					+ " d.code as business,doc3.code as reason ,g3.begindate as fsdate, "
					+ " case doc.code  "
					+ " when 'LT06' then case a.sex when 1 then 'M'when 2 then 'F'end  "
					+ " when 'LT13' then case a.sex when 1 then 'M'when 2 then 'F'end  "
					+ " when 'LT14' then case a.sex when 1 then 'M'when 2 then 'F'end  "
					+ " end as sex, "
					+ " CASE "
					+ " WHEN g3.glbdef2 = '本人' THEN doc2.code "
					+ " ELSE '' "
					+ " END   as tjid, "
					+ " case when g3.glbdef2='本人' then '6' else ''end  as gztjl, "
					+ " case when g3.glbdef2='本人' then "
					+ " case when g2.glbdef8 =0 then null "
					+ " else g2.glbdef8 end "
					+ "  else null end as grtjl,  "
					+ " case when g2.begindate = g2.glbdef14 then '' else g2.glbdef14 end as lttjdate "
					+ " from bd_psndoc a  "
					+ " left join org_orgs org on org.pk_org = a.pk_org "
					+ " left join org_hrorg hr on org.code = hr.code "
					+ " left join bd_defdoc d on d.pk_defdoc =  hr."
					+ LocalizationSysinitUtil.getTwhrzzxx("TWHRZZXX04")
					+ " "
					+ " left join hi_psndoc_glbdef3 g3 on g3.pk_psndoc = a.pk_psndoc "
					+ " left join bd_defdoc doc3 on g3.glbdef17= doc3.pk_defdoc "
					+ " left join hi_psndoc_glbdef2 g2 on g2.pk_psndoc = a.pk_psndoc  "
					+ " left join bd_defdoc doc on g2.glbdef1= doc.pk_defdoc "
					+ " left join bd_defdoc doc2 on g2.glbdef17= doc2.pk_defdoc "
					+ " left join bd_defdoc doc4 on g2.glbdef16 = doc4.pk_defdoc "
					// +" where a.code in('TP1006008','TP1712008')"
					+ " where g2.begindate = '"
					+ startPeriod
					+ "'"
					+ " and g2.recordnum=(select max(gl.recordnum) from bd_psndoc ps left join hi_psndoc_glbdef2 gl on gl.pk_psndoc = ps.pk_psndoc where ps.pk_psndoc=a.pk_psndoc) "
					+ " and g2.begindate = g3.begindate and g3.glbdef14='Y' and g2.glbdef10 = 'Y' and g2.glbdef11 = 'Y' and hr.pk_hrorg ='"
					+ pk_org
					+ "'"
					+ " order by a.name, (case when g3.glbdef2='本人'then 1 else 2 end)";
			List<Map<String, Object>> list = (List<Map<String, Object>>) query
					.executeQuery(sql, new MapListProcessor());
			InputStream instream = new FileInputStream(getPath()
					+ "coverage.xls");
			HSSFWorkbook wb = new HSSFWorkbook(instream);
			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFDataFormat format = wb.createDataFormat();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = list.get(i);
					HSSFRow row = sheet.createRow(i + 1);
					DecimalFormat df = new DecimalFormat("######0.0");
					DecimalFormat dfInt = new DecimalFormat("0");
					for (int j = 0; j < 26; j++) {
						HSSFCell cell = row.createCell(j);
						switch (j) {
						case 0:
							cell.setCellValue(map.get("move") == null ? ""
									: map.get("move").toString());
							break;
						case 1:
							cell.setCellValue(map.get("insnum") == null ? ""
									: map.get("insnum").toString());
							break;
						case 2:
							cell.setCellValue(map.get("checkcode") == null ? ""
									: map.get("checkcode").toString());
							break;
						case 3:
							cell.setCellValue(map.get("unitcode") == null ? ""
									: map.get("unitcode").toString());
							break;
						case 4:
							cell.setCellValue(map.get("inssort") == null ? ""
									: map.get("inssort").toString());
							break;
						case 5:
							cell.setCellValue(map.get("insured") == null ? ""
									: map.get("insured").toString());
							break;
						case 6:
							cell.setCellValue(map.get("insforeign") == null ? ""
									: map.get("insforeign").toString());
							break;
						case 7:
							cell.setCellValue(map.get("id") == null ? "" : map
									.get("id").toString());
							break;
						case 8:
							cell.setCellValue(map.get("insname") == null ? ""
									: map.get("insname").toString());
							break;
						case 9:
							cell.setCellValue(adYearToRepublicOfChina(map
									.get("birthdate")));
							break;
						case 10:
							cell.setCellValue((map.get("wage") == null ? dfInt
									.format(0) : dfInt.format(map.get("wage"))));
							break;
						case 11:
							cell.setCellValue((map.get("inssalary") == null ? dfInt
									.format(0) : dfInt.format(map
									.get("inssalary"))));
							break;
						case 12:
							cell.setCellValue(map.get("special") == null ? ""
									: map.get("special").toString());
							break;
						case 13:
							cell.setCellValue(map.get("lfspecial") == null ? ""
									: map.get("lfspecial").toString());
							break;
						case 14:
							cell.setCellValue(map.get("jsid") == null ? ""
									: map.get("jsid").toString());
							break;
						case 15:
							cell.setCellValue(map.get("jsname") == null ? ""
									: map.get("jsname").toString());
							break;
						case 16:
							cell.setCellValue(adYearToRepublicOfChina(map
									.get("jsbirthdate")));
							break;
						case 17:
							cell.setCellValue(map.get("title") == null ? ""
									: map.get("title").toString());
							break;
						case 18:
							cell.setCellValue(map.get("business") == null ? ""
									: map.get("business").toString());
							break;
						case 19:
							cell.setCellValue(map.get("reason") == null ? ""
									: map.get("reason").toString());
							break;
						case 20:
							cell.setCellValue(adYearToRepublicOfChina(map
									.get("fsdate")));
							break;
						case 21:
							cell.setCellValue(map.get("sex") == null ? "" : map
									.get("sex").toString());
							break;
						case 22:
							cell.setCellValue(map.get("tjid") == null ? ""
									: map.get("tjid").toString());
							break;
						case 23:
							cell.setCellValue(map.get("gztjl") == null ? ""
									: map.get("gztjl").toString());
							break;
						case 24:
							cell.setCellValue(map.get("grtjl") == null ? ""
									: df.format(map.get("grtjl")));
							break;
						case 25:
							cell.setCellValue(adYearToRepublicOfChina(map
									.get("lttjdate")));
							break;
						default:
							break;
						}
					}
				}
			}
			try {
				FileOutputStream outstream = new FileOutputStream(filepath);
				// 新建一个输出文件流
				wb.write(outstream);// 进行工作簿存储

				outstream.flush();
				outstream.close();
				Runtime CRuntime = Runtime.getRuntime();
				Process CProce = CRuntime.exec("cmd /c start " + filepath);
				// 关闭进程
				CProce.waitFor();
				CProce.destroy();
			} catch (Exception e) {
				Logger.error(e.getMessage());
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("leavebalance",
								"2leavebalance-000004")/* "發生錯誤，請檢查！" */);
			}
		} else if (isGen == UIDialog.ID_CANCEL) {
			this.putValue("message_after_action", ResHelper.getString(
					"twhr_datainterface", "DataInterface-00083")); // 綜所稅申報檔導出已取消
		}
	}

	@Override
	protected boolean isActionEnable() {
		return true;
	}

	/**
	 * 公元年转民国年 格式 ：2018-01-01 to 1070101
	 * 
	 * @param string
	 * @return
	 */
	public static String adYearToRepublicOfChina(Object string) {
		String date = "";
		if (string != null && !"".equals(string)) {
			String str = string.toString();
			String[] split = str.split("-");
			String year = String.valueOf(Integer.parseInt(split[0]) - 1911);
			if (year.length() == 1) {
				year = "00" + year;
			} else if (year.length() == 2) {
				year = "0" + year;
			}
			date = year + split[1] + split[2];
		}
		return date;
	}

	public static String getExportFilePath(String fileName) {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("*.xls",
				"xls");
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(filter);
		fc.setMultiSelectionEnabled(false);
		fc.setSelectedFile(new File(fileName));// 默认文件名
		int result = fc.showSaveDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			if (!file.getPath().endsWith(".xls")) {
				return file.getPath() + ".xls";
			}
			return file.getPath();
		}
		return null;
	}

	public String getPath() {
		return this.getClass().getResource("").getPath();
	}
}
