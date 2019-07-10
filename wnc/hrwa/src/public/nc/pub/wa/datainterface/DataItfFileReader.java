/**
 * @(#)DataItfFileReader.java 1.0 2018年1月30日
 *
 * Copyright (c) 2013, Yonyou. All rights reserved.
 * YONYOU PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package nc.pub.wa.datainterface;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.ResHelper;
import nc.itf.bd.bankacc.base.IBankAccBaseInfoQueryService;
import nc.itf.hr.datainterface.IDataIOManageService;
import nc.itf.hr.wa.IWaClass;
import nc.itf.om.IDeptQueryService;
import nc.itf.wa.datainterface.ICsvRowReader;
import nc.itf.wa.datainterface.IExcelRowReader;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.pub.SqlBuilder;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.datainterface.BonusOthBuckVO;
import nc.vo.wa.datainterface.DataItfFileVO;
import nc.vo.wa.datainterface.ImpParamVO;
import nc.vo.wa.datainterface.MappingFieldVO;
import nc.vo.wa.datainterface.SalaryOthBuckVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import uap.iweb.log.Logger;

/**
 * @author niehg
 * @since 6.3
 */
public class DataItfFileReader {
	public static boolean isCsvFile(String filePath) {
		return filePath.toLowerCase().trim().endsWith(DataItfConst.SUFFIX_CSV);
	}

	public static boolean isExcelFile(String filePath) {
		return filePath.toLowerCase().trim().endsWith(DataItfConst.SUFFIX_XLS)
				|| filePath.toLowerCase().trim().endsWith(DataItfConst.SUFFIX_XLSX);
	}

	public static DataVO[] readFileSD(String filePath, WaLoginContext waContext, ImpParamVO paraVO) throws Exception {
		if (StringUtils.isNotBlank(filePath)) {
			DataVO[] datas = null;
			if (isCsvFile(filePath)) {
				datas = readCsvSD(filePath, waContext, paraVO);
			} else if (isExcelFile(filePath)) {
				// datas = readExcelSD(filePath, waContext, paraVO);
				datas = readBigExcelSD(filePath, waContext, paraVO);
			}
			return datas;
		}
		return null;
	}

	public static SalaryOthBuckVO[] readFileSOD(String filePath, WaLoginContext waContext, ImpParamVO paraVO)
			throws Exception {
		if (StringUtils.isNotBlank(filePath)) {
			SalaryOthBuckVO[] datas = null;
			if (isCsvFile(filePath)) {
				datas = readCsvSOD(filePath, waContext, paraVO);
			} else if (isExcelFile(filePath)) {
				// datas = readExcelSOD(filePath, waContext, paraVO);
				datas = readBigExcelSOD(filePath, waContext, paraVO);
			}
			return datas;
		}
		return null;
	}

	private static SalaryOthBuckVO[] readBigExcelSOD(String filePath, final WaLoginContext waContext,
			final ImpParamVO paraVO) throws Exception {
		final List<SalaryOthBuckVO> voList = new ArrayList<SalaryOthBuckVO>();
		final StringBuilder errImpMsg = new StringBuilder();
		paraVO.countReset();
		final Map<String, HRDeptVO> codeDeptVOMap = paraVO.getCodeDeptVOMap();
		final Map<String, PsndocVO> codePsnVOMap = paraVO.getCodePsnVOMap();

		BigExcelReader reader = new BigExcelReader(filePath) {
			@Override
			protected void outputRow(String[] datas, int[] rowTypes, int rowIndex) {
				if (null != datas) {
					paraVO.countIncrement();
					if (paraVO.getCount() <= paraVO.getStartIndex()) {
						return;
					}

					String deptcode = datas[0]; // 部门编码
					String deptname = datas[1]; // 部门名称
					String psncode = datas[2]; // 人员编码
					String psnname = datas[3]; // 人员名称
					String pldecode = datas[4]; // 加扣代码-对应薪资发放项目
					String pldename = datas[5]; // 加扣名称
					String paydate = datas[6]; // 发放日期
					String yearmonth = datas[7]; // 发放期间
					String taxadd = datas[8]; // 应税加项
					String notaxadd = datas[9]; // 免税加项
					String taxsub = datas[10]; // 应税减项
					String notaxsub = datas[11]; // 免税减项
					String remark = datas[12]; // 备注
					Logger.info(String.valueOf(rowIndex) + "");
					SalaryOthBuckVO vo = new SalaryOthBuckVO();
					voList.add(vo);
					// if (voList.size() > paraVO.getLimitNum()) {
					// throw new Exception(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0045", null,
					// new String[] { paraVO.getLimitNum() + "" }));
					// }
					StringBuilder errMsg = new StringBuilder();
					String pk_dept = "";
					String pk_psndoc = "";
					if (StringUtils.isBlank(deptcode)) {
						// 部门编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0022"));
					} else {
						HRDeptVO deptVO = codeDeptVOMap.get(deptcode);
						if (null == deptVO) {
							// 部门编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0023"));
						} else {
							pk_dept = deptVO.getPk_dept();
						}
					}
					if (StringUtils.isBlank(psncode)) {
						// 人员编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0024"));
					} else {
						PsndocVO psnVO = codePsnVOMap.get(psncode);
						if (null == psnVO) {
							// 人员编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0025"));
						} else {
							pk_psndoc = psnVO.getPk_psndoc();
						}
					}
					if (StringUtils.isBlank(pldecode)) {
						// 加扣代号不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0032"));
					}
					// if (StringUtils.isBlank(paydate)) {
					// // 发放日期不能为空!
					// errMsg.append(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0033"));
					// } else {
					// try {
					// paydate =
					// DataItfConst.SDF_DATE.format(DataItfConst.SDF_STR_DATE.parse(paydate));
					// } catch (Exception e) {
					// // 发放日期[" + paydate + "]不能转换日期格式yyyy-MM-dd!
					// errMsg.append(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0034", null,
					// new String[] { paydate }));
					// }
					// }
					yearmonth = getWaYearMonth(yearmonth, errMsg);
					if (StringUtils.isBlank(taxadd)) {
						// 应税加项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0035"));
					}
					if (StringUtils.isBlank(notaxadd)) {
						// 免税加项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0036"));
					}
					if (StringUtils.isBlank(taxsub)) {
						// 应税减项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0037"));
					}
					if (StringUtils.isBlank(notaxsub)) {
						// 免税减项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0038"));
					}
					if (errMsg.length() > 0) {
						// 行[" + rowNo + "],
						errMsg.insert(
								0,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0031", null,
										new String[] { String.valueOf(rowIndex) + "" }));
						// throw new Exception(errMsg.toString());
						if (errImpMsg.length() > 0) {
							errImpMsg.append(System.getProperty("line.separator"));
						}
						errImpMsg.append(errMsg);
						return;
					}

					vo.setDeptcode(deptcode);
					vo.setDeptname(deptname);
					vo.setPsndoccode(psncode);
					vo.setPsndocname(psnname);
					vo.setPldecode(pldecode);
					vo.setPldename(pldename);
					vo.setPaydate(paydate);
					vo.setCyearperiod(yearmonth);
					vo.setTaxadd(getNumber(taxadd));
					vo.setTaxsub(getNumber(taxsub));
					vo.setNotaxadd(getNumber(notaxadd));
					vo.setNotaxsub(getNumber(notaxsub));
					vo.setRemark(remark);
					vo.setPk_dept(pk_dept);
					vo.setPk_psndoc(pk_psndoc);
					setDataByContext(vo, waContext);

					if (voList.size() == paraVO.getLimitNum()) {
						return;
					}
				}
			}
		};

		reader.parse();

		if (errImpMsg.length() > 0) {
			throw new Exception(errImpMsg.toString());
		}
		return voList.toArray(new SalaryOthBuckVO[0]);
	}

	public static DataVO[] readFileBD(String filePath, WaLoginContext waContext, ImpParamVO paraVO) throws Exception {
		if (StringUtils.isNotBlank(filePath)) {
			DataVO[] datas = null;
			if (isCsvFile(filePath)) {
				datas = readCsvBD(filePath, waContext, paraVO);
			} else if (isExcelFile(filePath)) {
				// datas = readExcelBD(filePath, waContext, paraVO);
				datas = readBigExcelBD(filePath, waContext, paraVO);
			}
			return datas;
		}
		return null;
	}

	public static BonusOthBuckVO[] readFileBOD(String filePath, WaLoginContext waContext, ImpParamVO paraVO)
			throws Exception {
		if (StringUtils.isNotBlank(filePath)) {
			BonusOthBuckVO[] datas = null;
			if (isCsvFile(filePath)) {
				datas = readCsvBOD(filePath, waContext, paraVO);
			} else if (isExcelFile(filePath)) {
				// datas = readExcelBOD(filePath, waContext, paraVO);
				datas = readBigExcelBOD(filePath, waContext, paraVO);
			}
			return datas;
		}
		return null;
	}

	public static DataVO[] readCsvSD(String filePath, final WaLoginContext waContext, final ImpParamVO paraVO)
			throws Exception {
		final List<DataVO> voList = new ArrayList<DataVO>();
		final StringBuilder errImpMsg = new StringBuilder();
		paraVO.countReset();
		final Map<String, HRDeptVO> codeDeptVOMap = paraVO.getCodeDeptVOMap();
		final Map<String, PsndocVO> codePsnVOMap = paraVO.getCodePsnVOMap();
		final Map<String, PsndocVO> codeDeptPsnVOMap = paraVO.getCodeDeptPsnVOMap();
		final Map<String, BankAccbasVO> codeBankVOMap = paraVO.getCodeBankVOMap();
		final Map<String, MappingFieldVO> indexItemKeyMap = paraVO.getIndexItemKeyMap();
		if (null == indexItemKeyMap || indexItemKeyMap.isEmpty()) {
			// Mapping表(wa_imp_fieldmapping)中没有找到类型[{0}]的薪资项目对应字段信息!
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0041", null,
					new String[] { String.valueOf(MappingFieldVO.TYPE_SD) }));
		}
		FileUtils.readCsv(filePath, DataItfConst.GLOAL_CHARSET, new ICsvRowReader() {

			@Override
			public boolean readRow(int rowNo, String content) throws Exception {
				if (rowNo > 0 && StringUtils.isNotBlank(content)) {
					paraVO.countIncrement();
					if (paraVO.getCount() <= paraVO.getStartIndex()) {
						return true;
					}

					DataVO dvo = new DataVO();
					voList.add(dvo);
					// if (voList.size() > paraVO.getLimitNum()) {
					// throw new
					// Exception(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0045", null,
					// new String[] { paraVO.getLimitNum() + "" }));
					// }

					String[] ary = content.split(DataItfConst.CSV_SP);

					String deptCode = getStr(ary[0]); // 部门编码
					String deptName = getStr(ary[1]); // 部门名称
					String psnCode = getStr(ary[2]); // 人员编码
					String psnName = getStr(ary[3]); // 人员名称
					String yearmonth = getStr(ary[4]); // 薪资期间（年月）
					String bankAcc = getStr(ary[32]); // 账号
					String pk_psndoc = "";
					String pk_psnjob = "";
					String pk_psnorg = "";
					String pk_workdept = "";
					String pk_workorg = "";
					String pk_banktype = "";
					// String pk_bankacc = "";
					Logger.info(rowNo + "");
					StringBuilder errMsg = new StringBuilder();
					if (StringUtils.isBlank(deptCode)) {
						// 部门编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0022"));
					} else {
						HRDeptVO vo = codeDeptVOMap.get(deptCode);
						if (null == vo) {
							// 部门编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0023"));
						} else {
							pk_workdept = vo.getPk_dept();
							deptName = MultiLangHelper.getName(vo);
						}
					}
					if (StringUtils.isBlank(psnCode)) {
						// 人员编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0024"));
					} else {
						PsndocVO vo = codePsnVOMap.get(psnCode);
						if (null == vo) {
							// 人员编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0025"));
						} else if (StringUtils.isNotBlank(deptCode)) {
							vo = codeDeptPsnVOMap.get(psnCode + deptCode);
							pk_psndoc = vo.getPk_psndoc();
							psnName = MultiLangHelper.getName(vo);
							PsnJobVO jobvo = vo.getPsnJobVO();
							if (null == jobvo) {
								// 人员[" + psnCode + "]
								// 在部门[" + deptCode + "]下的任职记录在系统中找不到!
								errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0026", null,
										new String[] { psnCode, deptCode }));
							} else {
								pk_psnjob = jobvo.getPk_psnjob();
								pk_psnorg = jobvo.getPk_psnorg();
								pk_workdept = jobvo.getPk_dept();
								pk_workorg = jobvo.getPk_org();
							}
						}
					}
					yearmonth = getWaYearMonth(yearmonth, errMsg);
					if (StringUtils.isBlank(bankAcc)) {
						// 账号不能为空!
						// errMsg.append(ResHelper.getString("6013dataitf_01",
						// "dataitf-01-0029"));
					} else {
						BankAccbasVO vo = codeBankVOMap.get(bankAcc);
						if (null == vo) {
							// 账号在系统中找不到!
							// errMsg.append(ResHelper.getString("6013dataitf_01",
							// "dataitf-01-0030"));
						} else {
							pk_banktype = vo.getPk_banktype();
							// pk_bankacc = vo.getPk_bankaccbas();
						}
					}
					if (errMsg.length() > 0) {
						// 行[" + rowNo + "],
						errMsg.insert(
								0,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0031", null, new String[] { rowNo
										+ "" }));
						// throw new Exception(errMsg.toString());
						if (errImpMsg.length() > 0) {
							errImpMsg.append(System.getProperty("line.separator"));
						}
						errImpMsg.append(errMsg);
						return true;
					}

					setMnyFieldValueZero(dvo);
					dvo.setCyearperiod(yearmonth);
					if (yearmonth.length() == 6) {
						dvo.setCyear(yearmonth.substring(0, 4));
						dvo.setCperiod(yearmonth.substring(4, 6));
					}
					dvo.setPk_psndoc(pk_psndoc);
					dvo.setPk_psnjob(pk_psnjob);
					dvo.setPk_psnorg(pk_psnorg);
					dvo.setWorkdept(pk_workdept);
					dvo.setWorkorg(pk_workorg);
					dvo.setPk_bankaccbas1(bankAcc);
					dvo.setClerkcode(psnCode);
					dvo.setDeptname(deptName);
					dvo.setPsnname(psnName);
					dvo.setPk_bankaccbas1(bankAcc);
					dvo.setPk_banktype1(pk_banktype);

					for (int i = 5; i < ary.length; i++) {
						if (i == 32) {
							continue;
						}
						MappingFieldVO mappVO = indexItemKeyMap.get(String.valueOf(i));
						if (null == mappVO) {
							continue;
						}
						String itemKey = mappVO.getItemkey();
						if (StringUtils.isNotBlank(itemKey)) {
							dvo.setAttributeValue(itemKey, getNumber(ary[i]));
						}
					}
					dvo.setPk_org(waContext.getPk_org());
					dvo.setPk_group(waContext.getPk_group());
					dvo.setPk_wa_class(waContext.getClassPK());

					if (voList.size() == paraVO.getLimitNum()) {
						return false;
					}
				}
				return true;
			}
		});
		if (errImpMsg.length() > 0) {
			throw new Exception(errImpMsg.toString());
		}
		return voList.toArray(new DataVO[0]);
	}

	public static SalaryOthBuckVO[] readCsvSOD(String filePath, final WaLoginContext waContext, final ImpParamVO paraVO)
			throws Exception {
		final List<SalaryOthBuckVO> voList = new ArrayList<SalaryOthBuckVO>();
		final StringBuilder errImpMsg = new StringBuilder();
		paraVO.countReset();
		final Map<String, HRDeptVO> codeDeptVOMap = paraVO.getCodeDeptVOMap();
		final Map<String, PsndocVO> codePsnVOMap = paraVO.getCodePsnVOMap();
		FileUtils.readCsv(filePath, DataItfConst.GLOAL_CHARSET, new ICsvRowReader() {

			@Override
			public boolean readRow(int rowNo, String content) throws Exception {
				if (rowNo > 0 && StringUtils.isNotBlank(content)) {
					paraVO.countIncrement();
					if (paraVO.getCount() <= paraVO.getStartIndex()) {
						return true;
					}

					String[] ary = content.split(DataItfConst.CSV_SP);
					SalaryOthBuckVO vo = new SalaryOthBuckVO();
					voList.add(vo);
					// if (voList.size() > paraVO.getLimitNum()) {
					// throw new
					// Exception(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0045", null,
					// new String[] { paraVO.getLimitNum() + "" }));
					// }

					String deptcode = getStr(ary[0]); // 部门编码
					String deptname = getStr(ary[1]); // 部门名称
					String psncode = getStr(ary[2]); // 人员编码
					String psnname = getStr(ary[3]); // 人员名称
					String pldecode = getStr(ary[4]); // 加扣代码-对应薪资发放项目
					String pldename = getStr(ary[5]); // 加扣名称
					String paydate = getStr(ary[6]); // 发放日期
					String yearmonth = getStr(ary[7]); // 发放期间
					String taxadd = getStr(ary[8]); // 应税加项
					String notaxadd = getStr(ary[9]); // 免税加项
					String taxsub = getStr(ary[10]); // 应税减项
					String notaxsub = getStr(ary[11]); // 免税减项
					String remark = getStr(ary[12]); // 备注
					Logger.info(rowNo + "");
					StringBuilder errMsg = new StringBuilder();
					String pk_dept = "";
					String pk_psndoc = "";
					if (StringUtils.isBlank(deptcode)) {
						// 部门编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0022"));
					} else {
						HRDeptVO deptVO = codeDeptVOMap.get(deptcode);
						if (null == deptVO) {
							// 部门编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0023"));
						} else {
							pk_dept = deptVO.getPk_dept();
						}
					}
					if (StringUtils.isBlank(psncode)) {
						// 人员编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0024"));
					} else {
						PsndocVO psnVO = codePsnVOMap.get(psncode);
						if (null == psnVO) {
							// 人员编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0025"));
						} else {
							pk_psndoc = psnVO.getPk_psndoc();
						}
					}
					if (StringUtils.isBlank(pldecode)) {
						// 加扣代号不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0032"));
					}
					yearmonth = getWaYearMonth(yearmonth, errMsg);
					if (StringUtils.isBlank(taxadd)) {
						// 应税加项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0035"));
					}
					if (StringUtils.isBlank(notaxadd)) {
						// 免税加项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0036"));
					}
					if (StringUtils.isBlank(taxsub)) {
						// 应税减项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0037"));
					}
					if (StringUtils.isBlank(notaxsub)) {
						// 免税减项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0038"));
					}
					if (errMsg.length() > 0) {
						// 行[" + rowNo + "],
						errMsg.insert(
								0,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0031", null, new String[] { rowNo
										+ "" }));
						// throw new Exception(errMsg.toString());
						if (errImpMsg.length() > 0) {
							errImpMsg.append(System.getProperty("line.separator"));
						}
						errImpMsg.append(errMsg);
						return true;
					}

					vo.setDeptcode(deptcode);
					vo.setDeptname(deptname);
					vo.setPsndoccode(psncode);
					vo.setPsndocname(psnname);
					vo.setPldecode(pldecode);
					vo.setPldename(pldename);
					vo.setPaydate(paydate);
					vo.setCyearperiod(yearmonth);
					vo.setTaxadd(getNumber(taxadd));
					vo.setTaxsub(getNumber(taxsub));
					vo.setNotaxadd(getNumber(notaxadd));
					vo.setNotaxsub(getNumber(notaxsub));
					vo.setRemark(remark);
					vo.setPk_dept(pk_dept);
					vo.setPk_psndoc(pk_psndoc);
					setDataByContext(vo, waContext);

					if (voList.size() == paraVO.getLimitNum()) {
						return false;
					}
				}

				return true;
			}
		});
		if (errImpMsg.length() > 0) {
			throw new Exception(errImpMsg.toString());
		}
		return voList.toArray(new SalaryOthBuckVO[0]);
	}

	public static DataVO[] readCsvBD(String filePath, final WaLoginContext waContext, final ImpParamVO paraVO)
			throws Exception {
		final List<DataVO> voList = new ArrayList<DataVO>();
		final StringBuilder errImpMsg = new StringBuilder();
		paraVO.countReset();
		final Map<String, HRDeptVO> codeDeptVOMap = paraVO.getCodeDeptVOMap();
		final Map<String, PsndocVO> codePsnVOMap = paraVO.getCodePsnVOMap();
		final Map<String, PsndocVO> codeDeptPsnVOMap = paraVO.getCodeDeptPsnVOMap();
		final Map<String, MappingFieldVO> indexItemKeyMap = paraVO.getIndexItemKeyMap();
		if (null == indexItemKeyMap || indexItemKeyMap.isEmpty()) {
			// Mapping表(wa_imp_fieldmapping)中没有找到类型[{0}]的薪资项目对应字段信息!
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0041", null,
					new String[] { String.valueOf(MappingFieldVO.TYPE_BD) }));
		}

		final Map<String, String> itemKeyIndexMap = new HashMap<String, String>();

		FileUtils.readCsv(filePath, DataItfConst.GLOAL_CHARSET, new ICsvRowReader() {

			@Override
			public boolean readRow(int rowNo, String content) throws Exception {
				if (rowNo > 0 && StringUtils.isNotBlank(content)) {
					paraVO.countIncrement();
					if (paraVO.getCount() <= paraVO.getStartIndex()) {
						return true;
					}
					String[] ary = content.split(DataItfConst.CSV_SP);
					DataVO dvo = new DataVO();
					voList.add(dvo);
					// if (voList.size() > paraVO.getLimitNum()) {
					// throw new
					// Exception(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0045", null,
					// new String[] { paraVO.getLimitNum() + "" }));
					// }

					String deptCode = getStr(ary[0]); // 部门编码
					String deptName = getStr(ary[1]); // 部门名称
					String psnCode = getStr(ary[2]); // 人员编码
					String psnName = getStr(ary[3]); // 人员名称
					String yearmonth = getStr(ary[4]); // 薪资期间（年月）
					// String schemename = getStr(ary[5]); // 节金名称

					Logger.info(rowNo + "");
					String pk_psndoc = "";
					String pk_psnjob = "";
					String pk_psnorg = "";
					String pk_workdept = "";
					String pk_workorg = "";
					StringBuilder errMsg = new StringBuilder();
					if (StringUtils.isBlank(deptCode)) {
						// 部门编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0022"));
					} else {
						HRDeptVO vo = codeDeptVOMap.get(deptCode);
						if (null == vo) {
							// 部门编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0023"));
						} else {
							pk_workdept = vo.getPk_dept();
							deptName = MultiLangHelper.getName(vo);
						}
					}
					if (StringUtils.isBlank(psnCode)) {
						// 人员编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0024"));
					} else {
						PsndocVO vo = codePsnVOMap.get(psnCode);
						if (null == vo) {
							// 人员编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0025"));
						} else if (StringUtils.isNotBlank(deptCode)) {
							vo = codeDeptPsnVOMap.get(psnCode + deptCode);
							if (vo == null) {
								// 人员[" + psnCode + "]
								// 在部门[" + deptCode + "]下的任职记录在系统中找不到!
								errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0026", null,
										new String[] { psnCode, deptCode }));
							} else {
								pk_psndoc = vo.getPk_psndoc();
								psnName = MultiLangHelper.getName(vo);
								PsnJobVO jobvo = vo.getPsnJobVO();
								if (null == jobvo) {
									// 人员[" + psnCode + "]
									// 在部门[" + deptCode + "]下的任职记录在系统中找不到!
									errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0026", null,
											new String[] { psnCode, deptCode }));
								} else {
									pk_psnjob = jobvo.getPk_psnjob();
									pk_psnorg = jobvo.getPk_psnorg();
									pk_workdept = jobvo.getPk_dept();
									pk_workorg = jobvo.getPk_org();
								}
							}
						}
					}
					yearmonth = getWaYearMonth(yearmonth, errMsg);
					if (errMsg.length() > 0) {
						// 行[" + rowNo + "],
						errMsg.insert(
								0,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0031", null, new String[] { rowNo
										+ "" }));
						// throw new Exception(errMsg.toString());
						if (errImpMsg.length() > 0) {
							errImpMsg.append(System.getProperty("line.separator"));
						}
						errImpMsg.append(errMsg);
						return true;
					}

					setMnyFieldValueZero(dvo);
					dvo.setCyearperiod(yearmonth);
					if (yearmonth.length() == 6) {
						dvo.setCyear(yearmonth.substring(0, 4));
						dvo.setCperiod(yearmonth.substring(4, 6));
					}
					dvo.setPk_psndoc(pk_psndoc);
					dvo.setPk_psnjob(pk_psnjob);
					dvo.setPk_psnorg(pk_psnorg);
					dvo.setWorkdept(pk_workdept);
					dvo.setWorkorg(pk_workorg);
					dvo.setClerkcode(psnCode);
					dvo.setDeptname(deptName);
					dvo.setPsnname(psnName);

					// for (int i = 6; i < ary.length; i++) {
					// MappingFieldVO mappVO = indexItemKeyMap
					// .get(String.valueOf(i));
					// if (null == mappVO) {
					// continue;
					// }
					// String itemKey = mappVO.getItemkey();
					// if (StringUtils.isNotBlank(itemKey)) {
					// dvo.setAttributeValue(itemKey,
					// getNumber(ary[i]));
					// }
					// }
					dvo.setPk_org(waContext.getPk_org());
					dvo.setPk_group(waContext.getPk_group());
					dvo.setPk_wa_class(waContext.getClassPK());

					// MOD zhangheng 插入薪资项 2018/12/24
					if (!MapUtils.isEmpty(itemKeyIndexMap) && itemKeyIndexMap.size() > 0) {
						for (Map.Entry<String, String> entry : itemKeyIndexMap.entrySet()) {
							String key = entry.getKey();
							String valueIndex = entry.getValue();
							for (int i = 5; i < ary.length; i++) {
								String value = ary[Integer.valueOf(valueIndex)];
								dvo.setAttributeValue(key, value);
							}

						}
					}

					if (voList.size() == paraVO.getLimitNum()) {
						return false;
					}
				} else {
					paraVO.countIncrement();
					if (paraVO.getCount() <= paraVO.getStartIndex()) {
						return true;
					}
					String[] ary = content.split(DataItfConst.CSV_SP);
					if (ary.length > 5) {
						for (int i = 5; i < ary.length; i++) {
							String itemKey = ary[i];
							itemKeyIndexMap.put(itemKey, String.valueOf(i));
						}
					}

				}
				return true;
			}
		});
		if (errImpMsg.length() > 0) {
			throw new Exception(errImpMsg.toString());
		}
		return voList.toArray(new DataVO[0]);
	}

	public static BonusOthBuckVO[] readCsvBOD(String filePath, final WaLoginContext waContext, final ImpParamVO paraVO)
			throws Exception {
		final List<BonusOthBuckVO> voList = new ArrayList<BonusOthBuckVO>();
		final StringBuilder errImpMsg = new StringBuilder();
		paraVO.countReset();
		final Map<String, HRDeptVO> codeDeptVOMap = paraVO.getCodeDeptVOMap();
		final Map<String, PsndocVO> codePsnVOMap = paraVO.getCodePsnVOMap();
		final Map<String, WaClassVO> codeWaclassVOMap = paraVO.getCodeWaclassVOMap();
		FileUtils.readCsv(filePath, DataItfConst.GLOAL_CHARSET, new ICsvRowReader() {

			@Override
			public boolean readRow(int rowNo, String content) throws Exception {
				if (rowNo > 0 && StringUtils.isNotBlank(content)) {
					paraVO.countIncrement();
					if (paraVO.getCount() <= paraVO.getStartIndex()) {
						return true;
					}

					String[] ary = content.split(DataItfConst.CSV_SP);
					BonusOthBuckVO vo = new BonusOthBuckVO();
					voList.add(vo);
					// if (voList.size() > paraVO.getLimitNum()) {
					// throw new
					// Exception(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0045", null,
					// new String[] { paraVO.getLimitNum() + "" }));
					// }

					String deptcode = getStr(ary[0]); // 部门编码
					String psncode = getStr(ary[1]); // 人员编码
					String psnname = getStr(ary[2]); // 人员名称
					String paydate = getStr(ary[3]); // 发放日期
					String yearmonth = getStr(ary[4]); // 发放期间
					String schemecode = getStr(ary[5]); // 方案编码
					String schemename = getStr(ary[6]); // 方案名称
					String taxadd = getStr(ary[7]); // 应税加项
					String notaxadd = getStr(ary[8]); // 免税加项
					String taxsub = getStr(ary[9]); // 应税减项
					String notaxsub = getStr(ary[10]); // 免税减项
					String pldecode = getStr(ary[11]); // 加扣代码-对应薪资发放项目
					String remark = getStr(ary[12]); // 备注
					Logger.info(rowNo + "");
					StringBuilder errMsg = new StringBuilder();
					String pk_dept = "";
					String pk_psndoc = "";
					if (StringUtils.isBlank(deptcode)) {
						// 部门编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0022"));
					} else {
						HRDeptVO deptVO = codeDeptVOMap.get(deptcode);
						if (null == deptVO) {
							// 部门编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0023"));
						} else {
							pk_dept = deptVO.getPk_dept();
						}
					}
					if (StringUtils.isBlank(psncode)) {
						// 人员编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0024"));
					} else {
						PsndocVO psnVO = codePsnVOMap.get(psncode);
						if (null == psnVO) {
							// 人员编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0025"));
						} else {
							pk_psndoc = psnVO.getPk_psndoc();
						}
					}
					if (StringUtils.isBlank(paydate)) {
						// 发放日期不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0033"));
					} else {
						try {
							paydate = DataItfConst.SDF_DATE.format(DataItfConst.SDF_STR_DATE.parse(paydate));
						} catch (Exception e) {
							// 发放日期[" + paydate + "]不能转换日期格式yyyy-MM-dd!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0034", null,
									new String[] { paydate }));
						}
					}
					yearmonth = getWaYearMonth(yearmonth, errMsg);
					if (StringUtils.isBlank(schemecode)) {
						// 节金代号不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0039"));
					} else {
						WaClassVO classVO = codeWaclassVOMap.get(schemecode);
						if (null == classVO) {
							// 节金代号在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0040"));
						}
					}
					if (StringUtils.isBlank(taxadd)) {
						// 应税加项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0035"));
					}
					if (StringUtils.isBlank(notaxadd)) {
						// 免税加项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0036"));
					}
					if (StringUtils.isBlank(taxsub)) {
						// 应税减项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0037"));
					}
					if (StringUtils.isBlank(notaxsub)) {
						// 免税减项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0038"));
					}
					if (StringUtils.isBlank(pldecode)) {
						// 加扣代号不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0032"));
					}
					if (errMsg.length() > 0) {
						// 行[" + rowNo + "],
						errMsg.insert(
								0,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0031", null, new String[] { rowNo
										+ "" }));
						// throw new Exception(errMsg.toString());
						if (errImpMsg.length() > 0) {
							errImpMsg.append(System.getProperty("line.separator"));
						}
						errImpMsg.append(errMsg);
						return true;
					}

					vo.setDeptcode(deptcode);
					vo.setPsndoccode(psncode);
					vo.setPsndocname(psnname);
					vo.setPaydate(paydate);
					vo.setCyearperiod(yearmonth);
					vo.setSchemecode(schemecode);
					vo.setSchemename(schemename);
					vo.setTaxadd(getNumber(taxadd));
					vo.setTaxsub(getNumber(taxsub));
					vo.setNotaxadd(getNumber(notaxadd));
					vo.setNotaxsub(getNumber(notaxsub));
					vo.setPldecode(pldecode);
					vo.setRemark(remark);
					vo.setPk_dept(pk_dept);
					vo.setPk_psndoc(pk_psndoc);
					setDataByContext(vo, waContext);

					if (voList.size() == paraVO.getLimitNum()) {
						return false;
					}
				}

				return true;
			}
		});
		if (errImpMsg.length() > 0) {
			throw new Exception(errImpMsg.toString());
		}
		return voList.toArray(new BonusOthBuckVO[0]);
	}

	public static DataVO[] readExcelSD(String filePath, final WaLoginContext waContext, final ImpParamVO paraVO)
			throws Exception {
		final List<DataVO> voList = new ArrayList<DataVO>();
		final StringBuilder errImpMsg = new StringBuilder();
		paraVO.countReset();
		final Map<String, HRDeptVO> codeDeptVOMap = paraVO.getCodeDeptVOMap();
		final Map<String, PsndocVO> codePsnVOMap = paraVO.getCodePsnVOMap();
		final Map<String, PsndocVO> codeDeptPsnVOMap = paraVO.getCodeDeptPsnVOMap();
		final Map<String, BankAccbasVO> codeBankVOMap = paraVO.getCodeBankVOMap();
		final Map<String, MappingFieldVO> indexItemKeyMap = paraVO.getIndexItemKeyMap();
		if (null == indexItemKeyMap || indexItemKeyMap.isEmpty()) {
			// Mapping表(wa_imp_fieldmapping)中没有找到类型[{0}]的薪资项目对应字段信息!
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0041", null,
					new String[] { String.valueOf(MappingFieldVO.TYPE_SD) }));
		}
		FileUtils.readExcel(filePath, new IExcelRowReader() {

			@Override
			public boolean readRow(int sheetNo, int rowNo, Row row) throws Exception {
				if (null != row) {
					paraVO.countIncrement();
					if (paraVO.getCount() <= paraVO.getStartIndex()) {
						return true;
					}

					String deptCode = getCellValue(row.getCell(0)); // 部门编码
					String deptName = getCellValue(row.getCell(1)); // 部门名称
					String psnCode = getCellValue(row.getCell(2)); // 人员编码
					String psnName = getCellValue(row.getCell(3)); // 人员名称
					String yearmonth = getCellValue(row.getCell(4)); // 薪资期间（年月）
					String bankAcc = getCellValue(row.getCell(32)); // 账号
					String pk_psndoc = "";
					String pk_psnjob = "";
					String pk_psnorg = "";
					String pk_workdept = "";
					String pk_workorg = "";
					String pk_banktype = "";
					// String pk_bankacc = "";
					Logger.info(rowNo + "");

					DataVO dvo = new DataVO();
					voList.add(dvo);
					// if (voList.size() > paraVO.getLimitNum()) {
					// throw new Exception(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0045", null,
					// new String[] { paraVO.getLimitNum() + "" }));
					// }
					StringBuilder errMsg = new StringBuilder();
					if (StringUtils.isBlank(deptCode)) {
						// 部门编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0022"));
					} else {
						HRDeptVO vo = codeDeptVOMap.get(deptCode);
						if (null == vo) {
							// 部门编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0023"));
						} else {
							pk_workdept = vo.getPk_dept();
							deptName = MultiLangHelper.getName(vo);
						}
					}
					if (StringUtils.isBlank(psnCode)) {
						// 人员编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0024"));
					} else {
						PsndocVO vo = codePsnVOMap.get(psnCode);
						if (null == vo) {
							// 人员编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0025"));
						} else if (StringUtils.isNotBlank(deptCode)) {
							vo = codeDeptPsnVOMap.get(psnCode + deptCode);
							pk_psndoc = vo.getPk_psndoc();
							psnName = MultiLangHelper.getName(vo);
							PsnJobVO jobvo = vo.getPsnJobVO();
							if (null == jobvo) {
								// 人员[" + psnCode + "]
								// 在部门[" + deptCode + "]下的任职记录在系统中找不到!
								errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0026", null,
										new String[] { psnCode, deptCode }));
							} else {
								pk_psnjob = jobvo.getPk_psnjob();
								pk_psnorg = jobvo.getPk_psnorg();
								pk_workdept = jobvo.getPk_dept();
								pk_workorg = jobvo.getPk_org();
							}
						}
					}
					yearmonth = getWaYearMonth(yearmonth, errMsg);
					if (StringUtils.isBlank(bankAcc)) {
						// 账号不能为空!
						// errMsg.append(ResHelper.getString("6013dataitf_01",
						// "dataitf-01-0029"));
					} else {
						BankAccbasVO vo = codeBankVOMap.get(bankAcc);
						if (null == vo) {
							// 账号在系统中找不到!
							// errMsg.append(ResHelper.getString("6013dataitf_01",
							// "dataitf-01-0030"));
						} else {
							pk_banktype = vo.getPk_banktype();
							// pk_bankacc = vo.getPk_bankaccbas();
						}
					}
					if (errMsg.length() > 0) {
						// 行[" + rowNo + "],
						errMsg.insert(
								0,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0031", null, new String[] { rowNo
										+ "" }));
						// throw new Exception(errMsg.toString());
						if (errImpMsg.length() > 0) {
							errImpMsg.append(System.getProperty("line.separator"));
						}
						errImpMsg.append(errMsg);
						return true;
					}

					setMnyFieldValueZero(dvo);
					dvo.setCyearperiod(yearmonth);
					if (yearmonth.length() == 6) {
						dvo.setCyear(yearmonth.substring(0, 4));
						dvo.setCperiod(yearmonth.substring(4, 6));
					}
					dvo.setPk_psndoc(pk_psndoc);
					dvo.setPk_psnjob(pk_psnjob);
					dvo.setPk_psnorg(pk_psnorg);
					dvo.setWorkdept(pk_workdept);
					dvo.setWorkorg(pk_workorg);
					dvo.setPk_bankaccbas1(bankAcc);
					dvo.setClerkcode(psnCode);
					dvo.setDeptname(deptName);
					dvo.setPsnname(psnName);
					dvo.setPk_bankaccbas1(bankAcc);
					dvo.setPk_banktype1(pk_banktype);

					// 获得当前行的列数
					int lastCellNum = row.getPhysicalNumberOfCells();
					for (int i = 5; i < lastCellNum; i++) {
						if (i == 32) {
							continue;
						}
						MappingFieldVO mappVO = indexItemKeyMap.get(String.valueOf(i));
						if (null == mappVO) {
							continue;
						}
						String itemKey = mappVO.getItemkey();
						if (StringUtils.isNotBlank(itemKey)) {
							dvo.setAttributeValue(itemKey, getCellValue(row.getCell(i)));
						}
					}
					dvo.setPk_org(waContext.getPk_org());
					dvo.setPk_group(waContext.getPk_group());
					dvo.setPk_wa_class(waContext.getClassPK());

					if (voList.size() == paraVO.getLimitNum()) {
						return false;
					}
				}
				return true;

			}
		});
		if (errImpMsg.length() > 0) {
			throw new Exception(errImpMsg.toString());
		}
		return voList.toArray(new DataVO[0]);
	}

	public static DataVO[] readBigExcelSD(String filePath, final WaLoginContext waContext, final ImpParamVO paraVO)
			throws Exception {
		final List<DataVO> voList = new ArrayList<DataVO>();
		final StringBuilder errImpMsg = new StringBuilder();
		paraVO.countReset();
		final Map<String, HRDeptVO> codeDeptVOMap = paraVO.getCodeDeptVOMap();
		final Map<String, PsndocVO> codePsnVOMap = paraVO.getCodePsnVOMap();
		final Map<String, PsndocVO> codeDeptPsnVOMap = paraVO.getCodeDeptPsnVOMap();
		final Map<String, BankAccbasVO> codeBankVOMap = paraVO.getCodeBankVOMap();
		final Map<String, MappingFieldVO> indexItemKeyMap = paraVO.getIndexItemKeyMap();
		if (null == indexItemKeyMap || indexItemKeyMap.isEmpty()) {
			// Mapping表(wa_imp_fieldmapping)中没有找到类型[{0}]的薪资项目对应字段信息!
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0041", null,
					new String[] { String.valueOf(MappingFieldVO.TYPE_SD) }));
		}
		BigExcelReader reader = new BigExcelReader(filePath) {

			@Override
			protected void outputRow(String[] datas, int[] rowTypes, int rowIndex) {
				if (null != datas && rowIndex > 0) {
					paraVO.countIncrement();
					if (paraVO.getCount() <= paraVO.getStartIndex()) {
						return;
					}

					String deptCode = datas[0]; // 部门编码
					String deptName = datas[1]; // 部门名称
					String psnCode = datas[2]; // 人员编码
					String psnName = datas[3]; // 人员名称
					String yearmonth = datas[4]; // 薪资期间（年月）
					String bankAcc = datas[32]; // 账号
					String pk_psndoc = "";
					String pk_psnjob = "";
					String pk_psnorg = "";
					String pk_workdept = "";
					String pk_workorg = "";
					String pk_banktype = "";
					// String pk_bankacc = "";
					Logger.info(String.valueOf(rowIndex) + "");

					DataVO dvo = new DataVO();
					voList.add(dvo);
					// if (voList.size() > paraVO.getLimitNum()) {
					// throw new Exception(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0045", null,
					// new String[] { paraVO.getLimitNum() + "" }));
					// }
					StringBuilder errMsg = new StringBuilder();
					if (StringUtils.isBlank(deptCode)) {
						// 部门编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0022"));
					} else {
						HRDeptVO vo = codeDeptVOMap.get(deptCode);
						if (null == vo) {
							// 部门编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0023"));
						} else {
							pk_workdept = vo.getPk_dept();
							deptName = MultiLangHelper.getName(vo);
						}
					}
					if (StringUtils.isBlank(psnCode)) {
						// 人员编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0024"));
					} else {
						PsndocVO vo = codePsnVOMap.get(psnCode);
						if (null == vo) {
							// 人员编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0025"));
						} else if (StringUtils.isNotBlank(deptCode)) {
							vo = codeDeptPsnVOMap.get(psnCode + deptCode);
							if (vo == null) {
								// 人员[" + psnCode + "]
								// 在部门[" + deptCode + "]下的任职记录在系统中找不到!
								errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0026", null,
										new String[] { psnCode, deptCode }));
							} else {
								pk_psndoc = vo.getPk_psndoc();
								psnName = MultiLangHelper.getName(vo);
								PsnJobVO jobvo = vo.getPsnJobVO();
								if (null == jobvo) {
									// 人员[" + psnCode + "]
									// 在部门[" + deptCode + "]下的任职记录在系统中找不到!
									errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0026", null,
											new String[] { psnCode, deptCode }));
								} else {
									pk_psnjob = jobvo.getPk_psnjob();
									pk_psnorg = jobvo.getPk_psnorg();
									pk_workdept = jobvo.getPk_dept();
									pk_workorg = jobvo.getPk_org();
								}
							}
						}
					}
					yearmonth = getWaYearMonth(yearmonth, errMsg);
					if (StringUtils.isBlank(bankAcc)) {
						// 账号不能为空!
						// errMsg.append(ResHelper.getString("6013dataitf_01",
						// "dataitf-01-0029"));
					} else {
						BankAccbasVO vo = codeBankVOMap.get(bankAcc);
						if (null == vo) {
							// 账号在系统中找不到!
							// errMsg.append(ResHelper.getString("6013dataitf_01",
							// "dataitf-01-0030"));
						} else {
							pk_banktype = vo.getPk_banktype();
							// pk_bankacc = vo.getPk_bankaccbas();
						}
					}
					if (errMsg.length() > 0) {
						// 行[" + rowNo + "],
						errMsg.insert(
								0,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0031", null,
										new String[] { String.valueOf(rowIndex) + "" }));
						// throw new Exception(errMsg.toString());
						if (errImpMsg.length() > 0) {
							errImpMsg.append(System.getProperty("line.separator"));
						}
						errImpMsg.append(errMsg);
						return;
					}

					setMnyFieldValueZero(dvo);
					dvo.setCyearperiod(yearmonth);
					if (yearmonth.length() == 6) {
						dvo.setCyear(yearmonth.substring(0, 4));
						dvo.setCperiod(yearmonth.substring(4, 6));
					}
					dvo.setPk_psndoc(pk_psndoc);
					dvo.setPk_psnjob(pk_psnjob);
					dvo.setPk_psnorg(pk_psnorg);
					dvo.setWorkdept(pk_workdept);
					dvo.setWorkorg(pk_workorg);
					dvo.setPk_bankaccbas1(bankAcc);
					dvo.setClerkcode(psnCode);
					dvo.setDeptname(deptName);
					dvo.setPsnname(psnName);
					dvo.setPk_bankaccbas1(bankAcc);
					dvo.setPk_banktype1(pk_banktype);

					// 获得当前行的列数
					int lastCellNum = datas.length;
					for (int i = 5; i < lastCellNum; i++) {
						if (i == 32) {
							continue;
						}
						MappingFieldVO mappVO = indexItemKeyMap.get(String.valueOf(i));
						if (null == mappVO) {
							continue;
						}
						String itemKey = mappVO.getItemkey();
						if (StringUtils.isNotBlank(itemKey)) {
							dvo.setAttributeValue(itemKey, datas[i]);
						}
					}
					dvo.setPk_org(waContext.getPk_org());
					dvo.setPk_group(waContext.getPk_group());
					dvo.setPk_wa_class(waContext.getClassPK());

					if (voList.size() == paraVO.getLimitNum()) {
						return;
					}
				}
			}
		};

		reader.parse();

		if (errImpMsg.length() > 0) {
			throw new Exception(errImpMsg.toString());
		}
		return voList.toArray(new DataVO[0]);
	}

	public static SalaryOthBuckVO[] readExcelSOD(String filePath, final WaLoginContext waContext,
			final ImpParamVO paraVO) throws Exception {
		final List<SalaryOthBuckVO> voList = new ArrayList<SalaryOthBuckVO>();
		final StringBuilder errImpMsg = new StringBuilder();
		paraVO.countReset();
		final Map<String, HRDeptVO> codeDeptVOMap = paraVO.getCodeDeptVOMap();
		final Map<String, PsndocVO> codePsnVOMap = paraVO.getCodePsnVOMap();
		FileUtils.readExcel(filePath, new IExcelRowReader() {

			@Override
			public boolean readRow(int sheetNo, int rowNo, Row row) throws Exception {
				if (null != row) {
					paraVO.countIncrement();
					if (paraVO.getCount() <= paraVO.getStartIndex()) {
						return true;
					}

					String deptcode = getCellValue(row.getCell(0)); // 部门编码
					String deptname = getCellValue(row.getCell(1)); // 部门名称
					String psncode = getCellValue(row.getCell(2)); // 人员编码
					String psnname = getCellValue(row.getCell(3)); // 人员名称
					String pldecode = getCellValue(row.getCell(4)); // 加扣代码-对应薪资发放项目
					String pldename = getCellValue(row.getCell(5)); // 加扣名称
					String paydate = getCellValue(row.getCell(6)); // 发放日期
					String yearmonth = getCellValue(row.getCell(7)); // 发放期间
					String taxadd = getCellValue(row.getCell(8)); // 应税加项
					String notaxadd = getCellValue(row.getCell(9)); // 免税加项
					String taxsub = getCellValue(row.getCell(10)); // 应税减项
					String notaxsub = getCellValue(row.getCell(11)); // 免税减项
					String remark = getCellValue(row.getCell(12)); // 备注
					Logger.info(rowNo + "");
					SalaryOthBuckVO vo = new SalaryOthBuckVO();
					voList.add(vo);
					// if (voList.size() > paraVO.getLimitNum()) {
					// throw new Exception(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0045", null,
					// new String[] { paraVO.getLimitNum() + "" }));
					// }
					StringBuilder errMsg = new StringBuilder();
					String pk_dept = "";
					String pk_psndoc = "";
					if (StringUtils.isBlank(deptcode)) {
						// 部门编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0022"));
					} else {
						HRDeptVO deptVO = codeDeptVOMap.get(deptcode);
						if (null == deptVO) {
							// 部门编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0023"));
						} else {
							pk_dept = deptVO.getPk_dept();
						}
					}
					if (StringUtils.isBlank(psncode)) {
						// 人员编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0024"));
					} else {
						PsndocVO psnVO = codePsnVOMap.get(psncode);
						if (null == psnVO) {
							// 人员编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0025"));
						} else {
							pk_psndoc = psnVO.getPk_psndoc();
						}
					}
					if (StringUtils.isBlank(pldecode)) {
						// 加扣代号不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0032"));
					}
					yearmonth = getWaYearMonth(yearmonth, errMsg);
					if (StringUtils.isBlank(taxadd)) {
						// 应税加项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0035"));
					}
					if (StringUtils.isBlank(notaxadd)) {
						// 免税加项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0036"));
					}
					if (StringUtils.isBlank(taxsub)) {
						// 应税减项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0037"));
					}
					if (StringUtils.isBlank(notaxsub)) {
						// 免税减项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0038"));
					}
					if (errMsg.length() > 0) {
						// 行[" + rowNo + "],
						errMsg.insert(
								0,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0031", null, new String[] { rowNo
										+ "" }));
						// throw new Exception(errMsg.toString());
						if (errImpMsg.length() > 0) {
							errImpMsg.append(System.getProperty("line.separator"));
						}
						errImpMsg.append(errMsg);
						return true;
					}

					vo.setDeptcode(deptcode);
					vo.setDeptname(deptname);
					vo.setPsndoccode(psncode);
					vo.setPsndocname(psnname);
					vo.setPldecode(pldecode);
					vo.setPldename(pldename);
					vo.setPaydate(paydate);
					vo.setCyearperiod(yearmonth);
					vo.setTaxadd(getNumber(taxadd));
					vo.setTaxsub(getNumber(taxsub));
					vo.setNotaxadd(getNumber(notaxadd));
					vo.setNotaxsub(getNumber(notaxsub));
					vo.setRemark(remark);
					vo.setPk_dept(pk_dept);
					vo.setPk_psndoc(pk_psndoc);
					setDataByContext(vo, waContext);

					if (voList.size() == paraVO.getLimitNum()) {
						return false;
					}
				}

				return true;
			}
		});
		if (errImpMsg.length() > 0) {
			throw new Exception(errImpMsg.toString());
		}
		return voList.toArray(new SalaryOthBuckVO[0]);
	}

	public static DataVO[] readExcelBD(String filePath, final WaLoginContext waContext, final ImpParamVO paraVO)
			throws Exception {
		final List<DataVO> voList = new ArrayList<DataVO>();
		final StringBuilder errImpMsg = new StringBuilder();
		paraVO.countReset();
		final Map<String, HRDeptVO> codeDeptVOMap = paraVO.getCodeDeptVOMap();
		final Map<String, PsndocVO> codePsnVOMap = paraVO.getCodePsnVOMap();
		final Map<String, PsndocVO> codeDeptPsnVOMap = paraVO.getCodeDeptPsnVOMap();
		final Map<String, MappingFieldVO> indexItemKeyMap = paraVO.getIndexItemKeyMap();
		if (null == indexItemKeyMap || indexItemKeyMap.isEmpty()) {
			// Mapping表(wa_imp_fieldmapping)中没有找到类型[{0}]的薪资项目对应字段信息!
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0041", null,
					new String[] { String.valueOf(MappingFieldVO.TYPE_BD) }));
		}
		FileUtils.readExcel(filePath, new IExcelRowReader() {

			@Override
			public boolean readRow(int sheetNo, int rowNo, Row row) throws Exception {
				if (null != row) {
					paraVO.countIncrement();
					if (paraVO.getCount() <= paraVO.getStartIndex()) {
						return true;
					}

					String deptCode = getCellValue(row.getCell(0)); // 部门编码
					String deptName = getCellValue(row.getCell(1)); // 部门名称
					String psnCode = getCellValue(row.getCell(2)); // 人员编码
					String psnName = getCellValue(row.getCell(3)); // 人员名称
					String yearmonth = getCellValue(row.getCell(4)); // 薪资期间（年月）
					// String schemename = getCellValue(row.getCell(5)); // 节金名称
					String pk_psndoc = "";
					String pk_psnjob = "";
					String pk_psnorg = "";
					String pk_workdept = "";
					String pk_workorg = "";
					Logger.info(rowNo + "");

					DataVO dvo = new DataVO();
					voList.add(dvo);
					// if (voList.size() > paraVO.getLimitNum()) {
					// throw new Exception(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0045", null,
					// new String[] { paraVO.getLimitNum() + "" }));
					// }
					StringBuilder errMsg = new StringBuilder();
					if (StringUtils.isBlank(deptCode)) {
						// 部门编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0022"));
					} else {
						HRDeptVO vo = codeDeptVOMap.get(deptCode);
						if (null == vo) {
							// 部门编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0023"));
						} else {
							pk_workdept = vo.getPk_dept();
							deptName = MultiLangHelper.getName(vo);
						}
					}
					if (StringUtils.isBlank(psnCode)) {
						// 人员编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0024"));
					} else {
						PsndocVO vo = codePsnVOMap.get(psnCode);
						if (null == vo) {
							// 人员编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0025"));
						} else if (StringUtils.isNotBlank(deptCode)) {
							vo = codeDeptPsnVOMap.get(psnCode + deptCode);
							pk_psndoc = vo.getPk_psndoc();
							psnName = MultiLangHelper.getName(vo);
							PsnJobVO jobvo = vo.getPsnJobVO();
							if (null == jobvo) {
								// 人员[" + psnCode + "]
								// 在部门[" + deptCode + "]下的任职记录在系统中找不到!
								errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0026", null,
										new String[] { psnCode, deptCode }));
							} else {
								pk_psnjob = jobvo.getPk_psnjob();
								pk_psnorg = jobvo.getPk_psnorg();
								pk_workdept = jobvo.getPk_dept();
								pk_workorg = jobvo.getPk_org();
							}
						}
					}
					yearmonth = getWaYearMonth(yearmonth, errMsg);
					if (errMsg.length() > 0) {
						// 行[" + rowNo + "],
						errMsg.insert(
								0,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0031", null, new String[] { rowNo
										+ "" }));
						// throw new Exception(errMsg.toString());
						if (errImpMsg.length() > 0) {
							errImpMsg.append(System.getProperty("line.separator"));
						}
						errImpMsg.append(errMsg);
						return true;
					}

					setMnyFieldValueZero(dvo);
					dvo.setCyearperiod(yearmonth);
					if (yearmonth.length() == 6) {
						dvo.setCyear(yearmonth.substring(0, 4));
						dvo.setCperiod(yearmonth.substring(4, 6));
					}
					dvo.setPk_psndoc(pk_psndoc);
					dvo.setPk_psnjob(pk_psnjob);
					dvo.setPk_psnorg(pk_psnorg);
					dvo.setWorkdept(pk_workdept);
					dvo.setWorkorg(pk_workorg);
					dvo.setClerkcode(psnCode);
					dvo.setDeptname(deptName);
					dvo.setPsnname(psnName);

					// 获得当前行的列数
					int lastCellNum = row.getPhysicalNumberOfCells();
					for (int i = 6; i < lastCellNum; i++) {
						MappingFieldVO mappVO = indexItemKeyMap.get(Integer.valueOf(i));
						if (null == mappVO) {
							continue;
						}
						String itemKey = mappVO.getItemkey();
						if (StringUtils.isNotBlank(itemKey)) {
							dvo.setAttributeValue(itemKey, getCellValue(row.getCell(i)));
						}
					}
					dvo.setPk_org(waContext.getPk_org());
					dvo.setPk_group(waContext.getPk_group());
					dvo.setPk_wa_class(waContext.getClassPK());

					if (voList.size() == paraVO.getLimitNum()) {
						return false;
					}
				}
				return true;
			}
		});
		if (errImpMsg.length() > 0) {
			throw new Exception(errImpMsg.toString());
		}
		return voList.toArray(new DataVO[0]);
	}

	public static DataVO[] readBigExcelBD(String filePath, final WaLoginContext waContext, final ImpParamVO paraVO)
			throws Exception {
		final List<DataVO> voList = new ArrayList<DataVO>();
		final StringBuilder errImpMsg = new StringBuilder();
		paraVO.countReset();
		final Map<String, HRDeptVO> codeDeptVOMap = paraVO.getCodeDeptVOMap();
		final Map<String, PsndocVO> codePsnVOMap = paraVO.getCodePsnVOMap();
		final Map<String, PsndocVO> codeDeptPsnVOMap = paraVO.getCodeDeptPsnVOMap();
		final Map<String, MappingFieldVO> indexItemKeyMap = paraVO.getIndexItemKeyMap();
		if (null == indexItemKeyMap || indexItemKeyMap.isEmpty()) {
			// Mapping表(wa_imp_fieldmapping)中没有找到类型[{0}]的薪资项目对应字段信息!
			throw new Exception(ResHelper.getString("6013dataitf_01", "dataitf-01-0041", null,
					new String[] { String.valueOf(MappingFieldVO.TYPE_BD) }));
		}

		BigExcelReader reader = new BigExcelReader(filePath) {

			@Override
			protected void outputRow(String[] datas, int[] rowTypes, int rowIndex) {
				if (null != datas) {
					paraVO.countIncrement();
					if (paraVO.getCount() <= paraVO.getStartIndex()) {
						return;
					}

					String deptCode = datas[0]; // 部门编码
					String deptName = datas[1]; // 部门名称
					String psnCode = datas[2]; // 人员编码
					String psnName = datas[3]; // 人员名称
					String yearmonth = datas[4]; // 薪资期间（年月）
					// String schemename = getCellValue(row.getCell(5)); // 节金名称
					String pk_psndoc = "";
					String pk_psnjob = "";
					String pk_psnorg = "";
					String pk_workdept = "";
					String pk_workorg = "";
					Logger.info(String.valueOf(rowIndex) + "");

					DataVO dvo = new DataVO();
					voList.add(dvo);
					// if (voList.size() > paraVO.getLimitNum()) {
					// throw new Exception(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0045", null,
					// new String[] { paraVO.getLimitNum() + "" }));
					// }
					StringBuilder errMsg = new StringBuilder();
					if (StringUtils.isBlank(deptCode)) {
						// 部门编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0022"));
					} else {
						HRDeptVO vo = codeDeptVOMap.get(deptCode);
						if (null == vo) {
							// 部门编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0023"));
						} else {
							pk_workdept = vo.getPk_dept();
							deptName = MultiLangHelper.getName(vo);
						}
					}
					if (StringUtils.isBlank(psnCode)) {
						// 人员编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0024"));
					} else {
						PsndocVO vo = codePsnVOMap.get(psnCode);
						if (null == vo) {
							// 人员编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0025"));
						} else if (StringUtils.isNotBlank(deptCode)) {
							vo = codeDeptPsnVOMap.get(psnCode + deptCode);
							pk_psndoc = vo.getPk_psndoc();
							psnName = MultiLangHelper.getName(vo);
							PsnJobVO jobvo = vo.getPsnJobVO();
							if (null == jobvo) {
								// 人员[" + psnCode + "]
								// 在部门[" + deptCode + "]下的任职记录在系统中找不到!
								errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0026", null,
										new String[] { psnCode, deptCode }));
							} else {
								pk_psnjob = jobvo.getPk_psnjob();
								pk_psnorg = jobvo.getPk_psnorg();
								pk_workdept = jobvo.getPk_dept();
								pk_workorg = jobvo.getPk_org();
							}
						}
					}
					yearmonth = getWaYearMonth(yearmonth, errMsg);
					if (errMsg.length() > 0) {
						// 行[" + rowNo + "],
						errMsg.insert(
								0,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0031", null,
										new String[] { String.valueOf(rowIndex) + "" }));
						// throw new Exception(errMsg.toString());
						if (errImpMsg.length() > 0) {
							errImpMsg.append(System.getProperty("line.separator"));
						}
						errImpMsg.append(errMsg);
						return;
					}

					setMnyFieldValueZero(dvo);
					dvo.setCyearperiod(yearmonth);
					if (yearmonth.length() == 6) {
						dvo.setCyear(yearmonth.substring(0, 4));
						dvo.setCperiod(yearmonth.substring(4, 6));
					}
					dvo.setPk_psndoc(pk_psndoc);
					dvo.setPk_psnjob(pk_psnjob);
					dvo.setPk_psnorg(pk_psnorg);
					dvo.setWorkdept(pk_workdept);
					dvo.setWorkorg(pk_workorg);
					dvo.setClerkcode(psnCode);
					dvo.setDeptname(deptName);
					dvo.setPsnname(psnName);

					// 获得当前行的列数
					int lastCellNum = datas.length;
					for (int i = 6; i < lastCellNum; i++) {
						MappingFieldVO mappVO = indexItemKeyMap.get(Integer.valueOf(i));
						if (null == mappVO) {
							continue;
						}
						String itemKey = mappVO.getItemkey();
						if (StringUtils.isNotBlank(itemKey)) {
							dvo.setAttributeValue(itemKey, datas[i]);
						}
					}
					dvo.setPk_org(waContext.getPk_org());
					dvo.setPk_group(waContext.getPk_group());
					dvo.setPk_wa_class(waContext.getClassPK());

					if (voList.size() == paraVO.getLimitNum()) {
						return;
					}
				}
			}
		};

		reader.parse();

		if (errImpMsg.length() > 0) {
			throw new Exception(errImpMsg.toString());
		}
		return voList.toArray(new DataVO[0]);
	}

	public static BonusOthBuckVO[] readExcelBOD(String filePath, final WaLoginContext waContext, final ImpParamVO paraVO)
			throws Exception {
		final List<BonusOthBuckVO> voList = new ArrayList<BonusOthBuckVO>();
		final StringBuilder errImpMsg = new StringBuilder();
		paraVO.countReset();
		final Map<String, HRDeptVO> codeDeptVOMap = paraVO.getCodeDeptVOMap();
		final Map<String, PsndocVO> codePsnVOMap = paraVO.getCodePsnVOMap();
		final Map<String, WaClassVO> codeWaclassVOMap = paraVO.getCodeWaclassVOMap();
		FileUtils.readExcel(filePath, new IExcelRowReader() {

			@Override
			public boolean readRow(int sheetNo, int rowNo, Row row) throws Exception {
				if (null != row) {
					paraVO.countIncrement();
					if (paraVO.getCount() <= paraVO.getStartIndex()) {
						return true;
					}

					String deptcode = getCellValue(row.getCell(0)); // 部门编码
					String psncode = getCellValue(row.getCell(1)); // 人员编码
					String psnname = getCellValue(row.getCell(2)); // 人员名称
					String paydate = getCellValue(row.getCell(3)); // 发放日期
					String yearmonth = getCellValue(row.getCell(4)); // 发放期间
					String schemecode = getCellValue(row.getCell(5)); // 方案编码
					String schemename = getCellValue(row.getCell(6)); // 方案名称
					String taxadd = getCellValue(row.getCell(7)); // 应税加项
					String notaxadd = getCellValue(row.getCell(8)); // 免税加项
					String taxsub = getCellValue(row.getCell(9)); // 应税减项
					String notaxsub = getCellValue(row.getCell(10)); // 免税减项
					String pldecode = getCellValue(row.getCell(11)); // 加扣代码-对应薪资发放项目
					String remark = getCellValue(row.getCell(12)); // 备注
					Logger.info(rowNo + "");

					BonusOthBuckVO vo = new BonusOthBuckVO();
					voList.add(vo);
					// if (voList.size() > paraVO.getLimitNum()) {
					// throw new Exception(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0045", null,
					// new String[] { paraVO.getLimitNum() + "" }));
					// }
					StringBuilder errMsg = new StringBuilder();
					String pk_dept = "";
					String pk_psndoc = "";
					if (StringUtils.isBlank(deptcode)) {
						// 部门编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0022"));
					} else {
						HRDeptVO deptVO = codeDeptVOMap.get(deptcode);
						if (null == deptVO) {
							// 部门编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0023"));
						} else {
							pk_dept = deptVO.getPk_dept();
						}
					}
					if (StringUtils.isBlank(psncode)) {
						// 人员编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0024"));
					} else {
						PsndocVO psnVO = codePsnVOMap.get(psncode);
						if (null == psnVO) {
							// 人员编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0025"));
						} else {
							pk_psndoc = psnVO.getPk_psndoc();
						}
					}
					if (StringUtils.isBlank(paydate)) {
						// 发放日期不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0033"));
					} else {
						try {
							paydate = DataItfConst.SDF_DATE.format(DataItfConst.SDF_STR_DATE.parse(paydate));
						} catch (Exception e) {
							// 发放日期[" + paydate + "]不能转换日期格式yyyy-MM-dd!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0034", null,
									new String[] { paydate }));
						}
					}
					yearmonth = getWaYearMonth(yearmonth, errMsg);
					if (StringUtils.isBlank(schemecode)) {
						// 节金代号不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0039"));
					} else {
						WaClassVO classVO = codeWaclassVOMap.get(schemecode);
						if (null == classVO) {
							// 节金代号在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0040"));
						}
					}
					if (StringUtils.isBlank(taxadd)) {
						// 应税加项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0035"));
					}
					if (StringUtils.isBlank(notaxadd)) {
						// 免税加项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0036"));
					}
					if (StringUtils.isBlank(taxsub)) {
						// 应税减项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0037"));
					}
					if (StringUtils.isBlank(notaxsub)) {
						// 免税减项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0038"));
					}
					if (StringUtils.isBlank(pldecode)) {
						// 加扣代号不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0032"));
					}
					if (errMsg.length() > 0) {
						// 行[" + rowNo + "],
						errMsg.insert(
								0,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0031", null, new String[] { rowNo
										+ "" }));
						// throw new Exception(errMsg.toString());
						if (errImpMsg.length() > 0) {
							errImpMsg.append(System.getProperty("line.separator"));
						}
						errImpMsg.append(errMsg);
						return true;
					}

					vo.setDeptcode(deptcode);
					vo.setPsndoccode(psncode);
					vo.setPsndocname(psnname);
					vo.setPaydate(paydate);
					vo.setCyearperiod(yearmonth);
					vo.setSchemecode(schemecode);
					vo.setSchemename(schemename);
					vo.setTaxadd(getNumber(taxadd));
					vo.setTaxsub(getNumber(taxsub));
					vo.setNotaxadd(getNumber(notaxadd));
					vo.setNotaxsub(getNumber(notaxsub));
					vo.setPldecode(pldecode);
					vo.setRemark(remark);
					vo.setPk_dept(pk_dept);
					vo.setPk_psndoc(pk_psndoc);
					setDataByContext(vo, waContext);

					if (voList.size() == paraVO.getLimitNum()) {
						return false;
					}
				}

				return true;
			}
		});
		if (errImpMsg.length() > 0) {
			throw new Exception(errImpMsg.toString());
		}
		return voList.toArray(new BonusOthBuckVO[0]);
	}

	public static BonusOthBuckVO[] readBigExcelBOD(String filePath, final WaLoginContext waContext,
			final ImpParamVO paraVO) throws Exception {
		final List<BonusOthBuckVO> voList = new ArrayList<BonusOthBuckVO>();
		final StringBuilder errImpMsg = new StringBuilder();
		paraVO.countReset();
		final Map<String, HRDeptVO> codeDeptVOMap = paraVO.getCodeDeptVOMap();
		final Map<String, PsndocVO> codePsnVOMap = paraVO.getCodePsnVOMap();
		final Map<String, WaClassVO> codeWaclassVOMap = paraVO.getCodeWaclassVOMap();
		BigExcelReader reader = new BigExcelReader(filePath) {

			@Override
			protected void outputRow(String[] datas, int[] rowTypes, int rowIndex) {
				if (null != datas) {
					paraVO.countIncrement();
					if (paraVO.getCount() <= paraVO.getStartIndex()) {
						return;
					}

					String deptcode = datas[0]; // 部门编码
					String psncode = datas[1]; // 人员编码
					String psnname = datas[2]; // 人员名称
					String paydate = datas[3]; // 发放日期
					String yearmonth = datas[4]; // 发放期间
					String schemecode = datas[5]; // 方案编码
					String schemename = datas[6]; // 方案名称
					String taxadd = datas[7]; // 应税加项
					String notaxadd = datas[8]; // 免税加项
					String taxsub = datas[9]; // 应税减项
					String notaxsub = datas[10]; // 免税减项
					String pldecode = datas[11]; // 加扣代码-对应薪资发放项目
					String remark = datas[12]; // 备注
					Logger.info(String.valueOf(rowIndex) + "");

					BonusOthBuckVO vo = new BonusOthBuckVO();
					voList.add(vo);
					// if (voList.size() > paraVO.getLimitNum()) {
					// throw new Exception(ResHelper.getString("6013dataitf_01",
					// "dataitf-01-0045", null,
					// new String[] { paraVO.getLimitNum() + "" }));
					// }
					StringBuilder errMsg = new StringBuilder();
					String pk_dept = "";
					String pk_psndoc = "";
					if (StringUtils.isBlank(deptcode)) {
						// 部门编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0022"));
					} else {
						HRDeptVO deptVO = codeDeptVOMap.get(deptcode);
						if (null == deptVO) {
							// 部门编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0023"));
						} else {
							pk_dept = deptVO.getPk_dept();
						}
					}
					if (StringUtils.isBlank(psncode)) {
						// 人员编码不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0024"));
					} else {
						PsndocVO psnVO = codePsnVOMap.get(psncode);
						if (null == psnVO) {
							// 人员编码在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0025"));
						} else {
							pk_psndoc = psnVO.getPk_psndoc();
						}
					}
					if (StringUtils.isBlank(paydate)) {
						// 发放日期不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0033"));
					} else {
						try {
							paydate = DataItfConst.SDF_DATE.format(DataItfConst.SDF_STR_DATE.parse(paydate));
						} catch (Exception e) {
							// 发放日期[" + paydate + "]不能转换日期格式yyyy-MM-dd!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0034", null,
									new String[] { paydate }));
						}
					}
					if (StringUtils.isBlank(yearmonth)) {
						// 发放年月不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0027"));
					} else {
						try {
							yearmonth = DataItfConst.SDF_PERIOD.format(DataItfConst.SDF_STR_DATE.parse(yearmonth));
						} catch (Exception e) {
							// 发放年月[" + yearmonth + "]不能转换日期格式yyyyMM!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0028", null,
									new String[] { yearmonth }));
						}
					}
					if (StringUtils.isBlank(schemecode)) {
						// 节金代号不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0039"));
					} else {
						WaClassVO classVO = codeWaclassVOMap.get(schemecode);
						if (null == classVO) {
							// 节金代号在系统中找不到!
							errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0040"));
						}
					}
					if (StringUtils.isBlank(taxadd)) {
						// 应税加项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0035"));
					}
					if (StringUtils.isBlank(notaxadd)) {
						// 免税加项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0036"));
					}
					if (StringUtils.isBlank(taxsub)) {
						// 应税减项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0037"));
					}
					if (StringUtils.isBlank(notaxsub)) {
						// 免税减项不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0038"));
					}
					if (StringUtils.isBlank(pldecode)) {
						// 加扣代号不能为空!
						errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0032"));
					}
					if (errMsg.length() > 0) {
						// 行[" + rowNo + "],
						errMsg.insert(
								0,
								ResHelper.getString("6013dataitf_01", "dataitf-01-0031", null,
										new String[] { String.valueOf(rowIndex) + "" }));
						// throw new Exception(errMsg.toString());
						if (errImpMsg.length() > 0) {
							errImpMsg.append(System.getProperty("line.separator"));
						}
						errImpMsg.append(errMsg);
						return;
					}

					vo.setDeptcode(deptcode);
					vo.setPsndoccode(psncode);
					vo.setPsndocname(psnname);
					vo.setPaydate(paydate);
					vo.setCyearperiod(yearmonth);
					vo.setSchemecode(schemecode);
					vo.setSchemename(schemename);
					vo.setTaxadd(getNumber(taxadd));
					vo.setTaxsub(getNumber(taxsub));
					vo.setNotaxadd(getNumber(notaxadd));
					vo.setNotaxsub(getNumber(notaxsub));
					vo.setPldecode(pldecode);
					vo.setRemark(remark);
					vo.setPk_dept(pk_dept);
					vo.setPk_psndoc(pk_psndoc);
					setDataByContext(vo, waContext);

					if (voList.size() == paraVO.getLimitNum()) {
						return;
					}
				}

			}
		};

		reader.parse();

		if (errImpMsg.length() > 0) {
			throw new Exception(errImpMsg.toString());
		}
		return voList.toArray(new BonusOthBuckVO[0]);
	}

	public static void setDataByContext(DataItfFileVO vo, WaLoginContext waContext) {
		if (null != vo && null != waContext) {
			vo.setStatus(VOStatus.NEW);
			vo.setDataid(null);
			vo.setPk_group(waContext.getPk_group());
			vo.setPk_org(waContext.getPk_org());
			vo.setPk_wa_class(waContext.getPk_wa_class());
		}
	}

	public static String getCellValue(Cell cell) throws Exception {
		String cellValue = "";
		if (cell == null) {
			return cellValue;
		}
		// if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
		// cell.setCellType(Cell.CELL_TYPE_STRING);
		// }
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC: // 数字
			if (DateUtil.isCellDateFormatted(cell)) {
				cellValue = DataItfConst.SDF_STR_DATE.format(cell.getDateCellValue());
			} else {
				DecimalFormat df = new DecimalFormat("0");
				cellValue = df.format(cell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_STRING: // 字符串
			cellValue = String.valueOf(cell.getStringCellValue());
			break;
		case Cell.CELL_TYPE_BOOLEAN: // Boolean
			cellValue = String.valueOf(cell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA: // 公式
			cellValue = String.valueOf(cell.getCellFormula());
			break;
		case Cell.CELL_TYPE_BLANK: // 空值
			cellValue = "";
			break;
		default:
			throw new Exception("Excel Data cell type is err! can not transter context!");
		}
		return cellValue;
	}

	public static String getStr(String str) {
		String s = StringUtils.isBlank(str) ? "" : str.trim();
		return s;
	}

	public static UFDouble getNumber(String strNum) {
		UFDouble ufd = StringUtils.isBlank(strNum) ? UFDouble.ZERO_DBL : new UFDouble(strNum);
		ufd.setScale(-2, UFDouble.ROUND_UP);
		return ufd;
	}

	public static Map<String, HRDeptVO> getCodeDeptInfo(WaLoginContext waContext) throws Exception {
		HRDeptVO[] deptVos = NCLocator.getInstance().lookup(IDeptQueryService.class)
				.queryDeptVOsByOrgPK(waContext.getPk_org(), true);
		Map<String, HRDeptVO> map = CommonUtils.toMap(HRDeptVO.CODE, deptVos);
		if (null == map) {
			map = new HashMap<String, HRDeptVO>();
		}
		return map;
	}

	// <psnCode, psnVO>
	public static Map<String, PsndocVO> getCodePsnInfo(WaLoginContext waContext) throws Exception {
		Map<String, PsndocVO> map = NCLocator.getInstance().lookup(IDataIOManageService.class)
				.queryPsnByOrgConditionn(waContext.getPk_org(), null, false);
		if (null == map) {
			map = new HashMap<String, PsndocVO>();
		}
		return map;
	}

	// <psnCode + deptCode, psnVO> psnVO include psnjobVO and psnorgVO
	public static Map<String, PsndocVO> getCodeDeptPsnInfo(WaLoginContext waContext) throws Exception {
		Map<String, PsndocVO> map = NCLocator.getInstance().lookup(IDataIOManageService.class)
				.queryPsnByOrgConditionn(waContext.getPk_org(), null, true);
		if (null == map) {
			map = new HashMap<String, PsndocVO>();
		}
		return map;
	}

	public static Map<String, WaClassVO> getCodeWaClassInfo(WaLoginContext waContext) throws Exception {
		WaClassVO vo = NCLocator.getInstance().lookup(IWaClass.class).queryWaClassByPK(waContext.getClassPK());
		Map<String, WaClassVO> map = CommonUtils.toMap(WaClassVO.CODE, new WaClassVO[] { vo });
		if (null == map) {
			map = new HashMap<String, WaClassVO>();
		}
		return map;
	}

	public static Map<String, BankAccbasVO> getCodeBankAccInfo(WaLoginContext waContext) throws Exception {
		SqlBuilder condition = new SqlBuilder();
		condition.append(BankAccbasVO.PK_GROUP, waContext.getPk_group());
		condition.append(" and ");
		condition.append(BankAccbasVO.PK_ORG, waContext.getPk_org());
		BankAccbasVO[] vos = NCLocator.getInstance().lookup(IBankAccBaseInfoQueryService.class)
				.queryBankAccBasVO(condition.toString(), false);
		Map<String, BankAccbasVO> map = CommonUtils.toMap(BankAccbasVO.ACCNUM, vos);
		if (null == map) {
			map = new HashMap<String, BankAccbasVO>();
		}
		return map;
	}

	public static Map<String, MappingFieldVO> getMappingByType(Integer type) throws BusinessException {
		SqlBuilder condition = new SqlBuilder();
		condition.append(MappingFieldVO.IMPTYPE, type);
		Map<Integer, MappingFieldVO[]> typeVOMap = NCLocator.getInstance().lookup(IDataIOManageService.class)
				.qryImpFieldMappingVO(condition.toString());
		if (!MapUtils.isEmpty(typeVOMap)) {
			MappingFieldVO[] mappVOs = typeVOMap.get(type);
			if (!ArrayUtils.isEmpty(mappVOs)) {
				if (type == MappingFieldVO.TYPE_SD || type == MappingFieldVO.TYPE_BD) {
					Map<String, MappingFieldVO> map = new HashMap<String, MappingFieldVO>();
					for (MappingFieldVO vo : mappVOs) {
						Object obj = vo.getAttributeValue(MappingFieldVO.COLINDEX);
						map.put(String.valueOf(obj), vo);
					}
					return map;
				} else if (type == MappingFieldVO.TYPE_SOD || type == MappingFieldVO.TYPE_BOD) {
					return CommonUtils.toMap(MappingFieldVO.CODE, mappVOs);
				} else if (type == MappingFieldVO.TYPE_BATCH) {
					return CommonUtils.toMap(MappingFieldVO.CODE, mappVOs);
				}
			}
		}
		return null;
	}

	public static int getBatchNumLimit() throws BusinessException {
		Map<String, MappingFieldVO> batchMap = getMappingByType(MappingFieldVO.TYPE_BATCH);
		if (!MapUtils.isEmpty(batchMap)) {
			MappingFieldVO vo = batchMap.get(MappingFieldVO.BATCH_CODE);
			if (null != vo && null != vo.getColindex()) {
				return vo.getColindex().intValue();
			}
		}
		return DataItfConst.BATCH_NUM;
	}

	public static void setMnyFieldValueZero(DataVO vo) {
		// String[] fields = vo.getAttributeNames();
		// if (!ArrayUtils.isEmpty(fields)) {
		// for (String fd : fields) {
		// if (fd.toLowerCase().startsWith("f_")) {
		// vo.setAttributeValue(fd, 0);
		// }
		// }
		// }

		for (int i = 1; i <= 280; i++) {
			vo.setAttributeValue("f_" + i, 0);
		}
	}

	public static String getWaYearMonth(String yearmonth, StringBuilder errMsg) {
		if (StringUtils.isBlank(yearmonth)) {
			// 发放年月不能为空!
			errMsg.append(ResHelper.getString("6013dataitf_01", "dataitf-01-0027"));
		} else {
			try {
				yearmonth = DataItfConst.SDF_PERIOD.format(DataItfConst.SDF_PERIOD.parse(yearmonth));
			} catch (Exception e) {
				// 发放年月[" + yearmonth + "]不能转换日期格式yyyyMM!
				errMsg.append(ResHelper
						.getString("6013dataitf_01", "dataitf-01-0028", null, new String[] { yearmonth }));
			}
		}
		return yearmonth;
	}
}
