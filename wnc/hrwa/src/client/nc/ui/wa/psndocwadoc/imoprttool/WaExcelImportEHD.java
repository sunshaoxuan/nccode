package nc.ui.wa.psndocwadoc.imoprttool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import nc.bs.uif2.validation.IValidationService;
import nc.hr.utils.ResHelper;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.ui.hr.util.TableColResize;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.wa.psndocwadoc.model.PsndocwadocAppModel;
import nc.ui.wa.psndocwadoc.view.DataImportDia;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.ArrayUtils;

/**
 * 定调资信息数据导入工具事件处理类
 * 
 * @author: xuhw
 * @date: 2010-4-8 上午11:30:25
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WaExcelImportEHD {
	/** 薪资普调model */
	private final PsndocwadocAppModel appmodel;

	private PsndocwadocExcelImporter importer;
	/** 关联的数据导入UI */
	private final DataImportDia ui;
	private IValidationService validationService = null;

	private int errorNum = 0;

	public int getErrorNum() {
		return errorNum;
	}

	public void setErrorNum(int errorNum) {
		this.errorNum = errorNum;
	}

	/**
	 * @param ui
	 *            关联的数据导入UI
	 */
	public WaExcelImportEHD(DataImportDia ui) {
		this.ui = ui;
		this.appmodel = ui.getAppmodel();
	}

	/**
	 * 导出 事件处理
	 */
	public void doExport() throws Exception {

		PsndocWadocVO[] exprotvos = this.appmodel.exportData2Excel();
		if (exprotvos == null) {
			doexportCSVTempate();
		} else {
			// 2016-12-06 zhousze 薪资加密：这里处理定调资信息维护导出模板数据解密处理 begin
			for (PsndocWadocVO vo : exprotvos) {
				vo.setCriterionvalue(vo.getCriterionvalue() == null ? null
						: new UFDouble(SalaryDecryptUtil.decrypt(vo
								.getCriterionvalue().toDouble())));
				vo.setNmoney(vo.getNmoney() == null ? null : new UFDouble(
						SalaryDecryptUtil.decrypt(vo.getNmoney().toDouble())));
			}
			// end
			int importAftLen = 0;
			this.ui.setBodyVOs(exprotvos);
			importAftLen = exprotvos.length;
			StringBuffer sbMessage = new StringBuffer();
			sbMessage.append(ResHelper.getString("60130adjmtc",
					"060130adjmtc0175")/* @res "成功导出条数：" */+ importAftLen);
			getImporter().setDetailMessage(sbMessage.toString());
			ui.setResultMessage(sbMessage.toString());
			doexportCSVTempate();
			// ui.showModal();
		}
	}

	/**
	 * 从画面导出
	 * 
	 * @author xuhw on 2010-5-21
	 */
	public void doexportCSVTempate() throws Exception {
		PsndocwadocExcelImporter importer = getImporter();
		importer.exportCSVTempate(getUI().getInputItems());
	}

	/**
	 * 从画面导出
	 * 
	 * @author xuhw on 2010-5-21
	 */
	public void doExportFromPanel() throws Exception {
		getImporter().setImportFileName(ui.getFileName());
		getImporter().exportCSVFromPanel(getUI().getInputItems());
	}

	/**
	 * 导入数据库事件处理
	 */
	public void doImport2DB() throws Exception {
		setErrorNum(0);
		PsndocWadocVO[] psndocwadocvos = this.ui.getBodyVOs();
		int importBefLen = 0;
		int importAftLen = 0;
		if (ArrayUtils.isEmpty(psndocwadocvos)) {
			MessageDialog.showErrorDlg(this.ui, null,
					ResHelper.getString("60130adjmtc", "060130adjmtc0176")/*
																		 * @res
																		 * "请先组织数据！"
																		 */);
			return;
		}

		// 如果是谈判工资，数据库不保存薪资标准类别，薪资标准，级别，档别
		for (int i = 0; i < psndocwadocvos.length; i++) {
			if (psndocwadocvos[i].getNegotiation_wage().booleanValue()) {
				psndocwadocvos[i].setPk_wa_grd(null);
				psndocwadocvos[i].setPk_wa_grd_showname(null);
				psndocwadocvos[i].setPk_wa_crt(null);
				psndocwadocvos[i].setPk_wa_crt_showname(null);
				psndocwadocvos[i].setPk_wa_prmlv(null);
				psndocwadocvos[i].setPk_wa_seclv(null);
			}
			if (psndocwadocvos[i].getPartflag() == null
					|| psndocwadocvos[i].getAssgid() == null) {
				psndocwadocvos[i].setPartflag(UFBoolean.FALSE);
				psndocwadocvos[i].setAssgid(Integer.valueOf(1));
			}
		}

		importBefLen = psndocwadocvos.length;
		Arrays.sort(psndocwadocvos);
		PsndocWadocVO[] failtureReports = this.appmodel.importData2DB(
				psndocwadocvos, this.appmodel.getContext());
		importAftLen = failtureReports.length;
		StringBuffer sbMessage = new StringBuffer();
		sbMessage.append(ResHelper.getString("60130adjmtc", "060130adjmtc0177",
				String.valueOf((importBefLen - importAftLen)),
				String.valueOf(importAftLen))/* @res "成功导入条数：{0}，导入失败条数：{1}。" */
		);
		sbMessage.append("\n");
		if (importAftLen > 0) {
			sbMessage.append(ResHelper.getString("60130adjmtc",
					"060130adjmtc0180")/*
										 * @res
										 * "失败原因请参考最后的【失败原因】一列。\n 错误数据已经导出，请修改后重试！"
										 */);
		}
		setErrorNum(importAftLen);
		Arrays.sort(failtureReports);
		this.ui.setBodyVOs(failtureReports);
		TableColResize.reSizeTable(this.ui.getBillCardPanel());
		// MessageDialog.showHintDlg(this.ui, null, sbMessage.toString());
		getImporter().setDetailMessage(sbMessage.toString());
		// doExportFromPanel();
		// 根据psncode[] 查psndocwadoc[]
		Set<String> set = new HashSet<String>();
		for (PsndocWadocVO wadocVO : psndocwadocvos) {
			set.add(wadocVO.getPsnCode());
		}
		PsndocWadocVO[] reVos = this.appmodel.queryPsndocWadocsByPsncode(set
				.toArray(new String[set.size()]));
		// 同步hi数据。。。by wangqim
		// patch to v636 wangqim NCdp205253327
		this.appmodel.psndocWadocSaveToWainfoVO(reVos);
	}

	/**
	 * 导入系统事件处理
	 */
	public void doImport2Panel() throws Exception {
		getImporter().importFromCls(getUI().getInputItems());
	}

	public PsndocwadocExcelImporter getImporter() {
		if (importer == null) {
			importer = new PsndocwadocExcelImporter(getUI());
		}
		return importer;
	}

	/**
	 * @return 关联的数据导入UI
	 */
	private DataImportDia getUI() {
		return ui;
	}

	public IValidationService getValidationService() {
		return validationService;
	}

	public void setValidationService(IValidationService validationService) {
		this.validationService = validationService;
	}
}