package nc.impl.wa.datainterface.export;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.hr.utils.ResHelper;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

public class DataFormatter {
	private Map<String, String> jointTables;
	private String formatterCode;
	private String pk_org;
	private String[] pk_orgs;
	private String startPeriod;
	private String endPeriod;
	private Integer iYear;
	private String classid;
	private String[] classIDs;
	private Map<String, Object> refsMap;

	public DataFormatter(String formatDocCode) throws BusinessException {
		if (StringUtil.isEmpty(formatDocCode)) {
			throw new BusinessException(ResHelper.getString(
					"twhr_datainterface", "DataInterface-00062"));// 文檔匯出格式編碼不能為空
		}

		formatterCode = formatDocCode;
	}

	public String[] getData() throws BusinessException {
		// 按編碼取格式定義
		String strSQL = "";
		Map<Integer, List<String>> lineTableMap = new HashMap<Integer, List<String>>();
		Map<Integer, List<String>> lineJoinMap = new HashMap<Integer, List<String>>();
		Map<Integer, List<String>> lineWhereMap = new HashMap<Integer, List<String>>();
		Map<Integer, Map<String, String>> lineFieldValueMap = new HashMap<Integer, Map<String, String>>();
		Map<Integer, Map<String, Integer>> lineFieldDataSourceMap = new HashMap<Integer, Map<String, Integer>>();
		Map<Integer, List<String>> lineGroupByMap = new HashMap<Integer, List<String>>();

		strSQL += " SELECT  item.linenumber , ";
		strSQL += "         item.posnumber , ";
		strSQL += "         item.itemcode , ";
		strSQL += "         item.itemname , ";
		strSQL += "         item.datatype , ";
		strSQL += "         item.byteLength , ";
		strSQL += "         item.fillmode , ";
		strSQL += "         item.fillstr , ";
		strSQL += "         item.prefix , ";
		strSQL += "         item.suffix , ";
		strSQL += "         item.splitter , ";
		strSQL += "         item.issum , ";
		strSQL += "         item.datasource , ";
		strSQL += "         item.datatable , ";
		strSQL += "         item.joinkey , ";
		strSQL += "         isnull(item.datacontext, '') as datacontext ";
		strSQL += " FROM    wa_expformat_item item ";
		strSQL += "         INNER JOIN wa_expformat_head head ON item.pk_formathead = head.pk_formathead ";
		strSQL += " WHERE   head.code = '" + this.getFormatterCode() + "' ";
		strSQL += "         AND head.dr = 0 ";
		strSQL += "         AND item.dr = 0 ";

		BaseDAO baseDao = new BaseDAO();
		List<Map> formats = (List<Map>) baseDao.executeQuery(strSQL,
				new MapListProcessor());

		for (Map format : formats) {
			int lineNo = (Integer) format.get("linenumber");
			// 行-表名
			appendLineTableMap(lineTableMap, format, lineNo);
			// 行-連接
			appendLineJoinMap(lineJoinMap, format, lineNo);
			// 行-字段-取值
			appendListFieldValueMap(lineFieldValueMap, lineGroupByMap, format,
					lineNo);
			// 行-字段-數據源
			appendListFieldDataSourceMap(lineFieldDataSourceMap, format, lineNo);
			// 行-條件鍵
			appendLineWhereMap(lineWhereMap, format, lineNo);
		}

		// 行-結果集
		Map<Integer, List<Map>> lineResults = new HashMap<Integer, List<Map>>();
		// 引用Key-值
		Map<String, Object> refs = new HashMap<String, Object>();
		for (int i = 1; i <= lineTableMap.keySet().size(); i++) {
			// 按格式定義生成行SQL
			StringBuilder sbSQL = new StringBuilder();
			sbSQL.append(getSelectPart(lineFieldValueMap,
					lineFieldDataSourceMap, i));
			sbSQL.append(getFromPart(lineTableMap, i));
			sbSQL.append(getJoinPart(lineJoinMap, i));
			sbSQL.append(getWherePart(lineWhereMap, i));
			sbSQL.append(getGroupByPart(lineFieldValueMap, lineGroupByMap, i));

			strSQL = replaceParams(lineFieldValueMap, lineFieldDataSourceMap,
					lineResults, i, sbSQL);
			// 替換已存在的引用
			for (Map.Entry<String, Object> entry : refs.entrySet()) {
				strSQL.replace(entry.getKey(), String.valueOf(entry.getValue()));
			}

			// 取值
			List<Map> lineResult = (List<Map>) baseDao.executeQuery(strSQL,
					new MapListProcessor());
			// 找到當前行引用，為下一行做準備
			refs = appendLineRefs(strSQL, lineResult, i);
			lineResults.put(i, lineResult);
		}

		for (Entry<Integer, List<Map>> rstEntry : lineResults.entrySet()) {
			for (Map line : rstEntry.getValue()) {
				Map<String, Object> realLine = (Map<String, Object>) line;
				for (Entry<String, Object> valentry : realLine.entrySet()) {
					valentry.setValue(replaceFunctions(lineResults,
							String.valueOf(valentry.getValue())));
				}
			}
		}

		List<String> resultList = new ArrayList<String>();
		// 取數格式化返回
		for (Map.Entry<Integer, List<Map>> entry : lineResults.entrySet()) {
			if (entry.getValue() == null || entry.getValue().size() == 0) {
				resultList.clear();
				break;
			}
			// 根據當前結果行找到行格式集
			List<Map<String, Object>> formatList = getLineFormat(formats,
					entry.getKey());
			for (Map<String, Object> lineRec : entry.getValue()) {
				String strProRes = "";
				for (int i = 0; i < formatList.size(); i++) {
					// 行格式集中定義的位置順序
					if (formatList.get(i).get("posnumber").equals(i + 1)) {
						// 找到當前位置格式行定義
						for (Map.Entry<String, Object> itemVal : lineRec
								.entrySet()) {
							if (itemVal.getKey().equals(
									String.valueOf(
											formatList.get(i).get("itemcode"))
											.toLowerCase())) {
								strProRes += getFormattedText(itemVal,
										formatList.get(i));
								break;
							}
						}
					}
				}
				resultList.add(strProRes);
			}
		}
		return resultList.toArray(new String[0]);
	}

	private Object getJoinPart(Map<Integer, List<String>> lineJoinMap,
			int lineNo) {
		StringBuilder joinPart = new StringBuilder();
		for (Map.Entry<Integer, List<String>> entry : lineJoinMap.entrySet()) {
			if (entry.getKey() == lineNo) {
				for (String joinStr : entry.getValue()) {
					joinPart.append(joinStr);
				}
			}
		}
		return joinPart.toString();
	}

	private void appendLineJoinMap(Map<Integer, List<String>> lineJoinMap,
			Map format, int lineNo) {
		if (!lineJoinMap.containsKey(lineNo)) {
			lineJoinMap.put(lineNo, new ArrayList<String>());
		}

		if (((Integer) format.get("datasource")) == 2) { // 0:固定值，1:手工輸入，2:公式
			String joinStr = (String) format.get("joinkey");
			String tableStr = (String) format.get("datatable");
			if (!StringUtil.isEmpty(joinStr)
					&& (!StringUtil.isEmpty(tableStr) && tableStr.contains("%"))) {
				lineJoinMap.get(lineNo).add(
						tableStr.replace("%LEFT%", " LEFT JOIN ")
								.replace("%RIGHT%", " RIGHT JOIN")
								.replace("%INNER%", " INNER JOIN ")
								+ " ON " + joinStr);
			}
		}
	}

	private String getGroupByPart(
			Map<Integer, Map<String, String>> lineFieldValueMap,
			Map<Integer, List<String>> lineGroupByMap, int lineNo) {
		boolean hasSum = false;
		for (Map.Entry<String, String> entry : lineFieldValueMap.get(lineNo)
				.entrySet()) {
			if (entry.getValue().contains("SUM(")) {
				hasSum = true;
				break;
			}
		}

		String groupByPart = "";
		if (hasSum) {
			for (String groupBy : lineGroupByMap.get(lineNo)) {
				if (!StringUtil.isEmpty(groupByPart)) {
					groupByPart += ",";
				}
				groupByPart += groupBy;
			}

			return " group by " + groupByPart;
		} else {
			return "";
		}
	}

	private String getFormattedText(Entry<String, Object> itemVal, Map format)
			throws BusinessException {
		String result = "";
		int datatype = Integer.valueOf(ObjectUtils.toString(format
				.get("datatype")));
		int fillmode = Integer.valueOf(ObjectUtils.toString(format
				.get("fillmode")));
		int byteLen = Integer.valueOf(ObjectUtils.toString(format
				.get("bytelength")));
		String prefix = (String) format.get("prefix");
		int prefixLen = 0;

		String suffix = (String) format.get("suffix");
		int suffixLen = 0;

		try {
			if (!StringUtils.isEmpty(prefix)) {
				prefixLen = prefix.getBytes("BIG5").length;
			}

			if (!StringUtils.isEmpty(suffix)) {
				suffixLen = suffix.getBytes("BIG5").length;
			}
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}

		String value = null;
		if (datatype == 1) {
			value = new UFDouble(String.valueOf(itemVal.getValue())).toString();
		} else {
			value = ObjectUtils.toString(itemVal.getValue());
		}

		if (datatype == 3) {
			result = ExportHelper4TW.getLengthString(value, byteLen - prefixLen
					- suffixLen,
					ObjectUtils.toString(format.get("fillstr") == null ? ""
							: format.get("fillstr")), (fillmode == 1 ? "L"
							: (fillmode == 2 ? "R" : "")), true);
		} else {
			result = ExportHelper4TW.getLengthString(value, byteLen - prefixLen
					- suffixLen,
					ObjectUtils.toString(format.get("fillstr") == null ? ""
							: format.get("fillstr")), (fillmode == 1 ? "L"
							: (fillmode == 2 ? "R" : "")), false);
		}

		if (!StringUtils.isEmpty(prefix)) {
			result = prefix + result;
		}

		if (!StringUtils.isEmpty(suffix)) {
			result = result + suffix;
		}

		return result;
	}

	private List<Map<String, Object>> getLineFormat(List<Map> formats,
			Integer lineno) {
		List<Map<String, Object>> formatList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> format : formats) {
			int lineNo = (Integer) format.get("linenumber");
			if (lineNo == lineno) {
				formatList.add(format);
			}
		}
		return formatList;
	}

	private Map<String, Object> appendLineRefs(String strSQL,
			List<Map> lineResult, int lineNo) throws BusinessException {
		Map<String, Object> refs = new HashMap<String, Object>();

		appendRefsByName("VALUE", strSQL, lineResult, refs);
		for (Map line : lineResult) {
			for (Map.Entry fieldValue : ((Map<String, Object>) line).entrySet()) {
				String key = "%VALUE:LINE:" + String.valueOf(lineNo) + ":"
						+ fieldValue.getKey() + "%";

				if (refs.containsKey(key)) {
					refs.put(key, fieldValue.getValue());
				}
			}
		}

		appendRefsByName("SUM", strSQL, lineResult, refs);
		for (Map line : lineResult) {
			for (Map.Entry fieldValue : ((Map<String, Object>) line).entrySet()) {
				String key = "%SUM:LINE:" + String.valueOf(lineNo) + ":"
						+ fieldValue.getKey() + "%";

				if (refs.containsKey(key)) {
					if (refs.containsKey(key)) {
						if (refs.get(key) == null) {
							refs.put(key, new UFDouble(0));
						}

						refs.put(key, ((UFDouble) refs.get(key))
								.add(new UFDouble(String.valueOf(fieldValue
										.getValue()))));
					}
				}
			}
		}

		appendRefsByName("COUNT", strSQL, lineResult, refs);
		for (Map line : lineResult) {
			for (Map.Entry fieldValue : ((Map<String, Object>) line).entrySet()) {
				String key = "%COUNT:LINE:" + String.valueOf(lineNo) + ":*%";

				if (refs.containsKey(key)) {
					if (refs.containsKey(key)) {
						if (refs.get(key) == null) {
							refs.put(key, new UFDouble(0));
						}

						refs.put(key, ((UFDouble) refs.get(key)).add(1));
					}
				}
			}
		}

		return refs;
	}

	private void appendRefsByName(String tagName, String strSQL,
			List<Map> lineResult, Map<String, Object> refs)
			throws BusinessException {
		int startIdx = 0;
		while (startIdx < strSQL.length()) {
			String key = "%" + tagName + ":";
			int beginIdx = strSQL.indexOf(key, startIdx);
			if (beginIdx >= 0) {
				int endIdx = strSQL.indexOf("%", beginIdx + 1);
				if (endIdx < 0) {
					throw new BusinessException("Wrong format of " + tagName
							+ " REFs");
				} else {
					key = strSQL.substring(beginIdx, endIdx + 1); // 含%整段
					if (!refs.containsKey(key)) {
						refs.put(key, null);
					}
				}

				startIdx = beginIdx + endIdx + 1;
			} else {
				startIdx = strSQL.length() + 1;
			}
		}
	}

	/**
	 * 替換參數
	 * 
	 * @param lineFieldValueMap
	 * @param lineFieldDataSourceMap
	 * @param lineResultsMap
	 * @param i
	 * @param sbSQL
	 * @return
	 */
	private String replaceParams(
			Map<Integer, Map<String, String>> lineFieldValueMap,
			Map<Integer, Map<String, Integer>> lineFieldDataSourceMap,
			Map<Integer, List<Map>> lineResultsMap, int i, StringBuilder sbSQL)
			throws BusinessException {
		String strSQL = sbSQL.toString();
		// 組織參數
		if (!StringUtils.isEmpty(this.getPk_org())) {
			strSQL = strSQL.replace("%PKORG%", "'" + this.getPk_org() + "'");
		} else {
			strSQL = strSQL.replace("%PKORG%", "''");
		}

		String orgsInString = "";
		if (this.getPk_orgs() != null && this.getPk_orgs().length > 0) {
			for (String org : this.getPk_orgs()) {
				if (orgsInString != "") {
					orgsInString += ",'" + org + "'";
				} else {
					orgsInString = "'" + org + "'";
				}
			}
			strSQL = strSQL.replace("%PKORGS%", orgsInString);
		} else {
			if (StringUtils.isEmpty(this.getPk_org())) {
				strSQL = strSQL.replace("%PKORGS%", "''");
			} else {
				strSQL = strSQL.replace("%PKORGS%", "'" + this.getPk_org()
						+ "'");
			}
		}
		// 行號
		strSQL = strSQL.replace("%ROWNO%",
				getRowNoOrderBy(i, lineFieldValueMap, lineFieldDataSourceMap));
		// 臺灣年
		strSQL = strSQL.replace("%REFTWYEAR%",
				String.valueOf(this.getiYear() - 1911));
		// 臺灣日期
		SimpleDateFormat format = new SimpleDateFormat("MMdd");
		strSQL = strSQL
				.replace(
						"%TWDATE%",
						String.valueOf(Calendar.getInstance()
								.get(Calendar.YEAR) - 1911)
								+ format.format(new Date()));
		// 參數年
		strSQL = strSQL.replace("%REFYEAR%",
				"'" + String.valueOf(this.getiYear()) + "'");
		// 參數薪資方案
		if (this.getClassid() != null) {
			strSQL = strSQL
					.replace("%REFCLASS%", "'" + this.getClassid() + "'");
		}
		// 參數薪資方案列表
		if (this.getClassIDs() != null && this.getClassIDs().length > 0) {
			String classids = "";
			for (String classid : this.getClassIDs()) {
				if (!StringUtil.isEmpty(classids)) {
					classids += ",";
				}
				classids += "'" + classid + "'";
			}
			strSQL = strSQL.replace("%REFCLASSES%", classids);
		}
		// 起始期間
		if (!StringUtil.isEmpty(this.getStartPeriod())) {
			// 一般格式: YYYYMM
			strSQL = strSQL.replace("%STARTPERIOD%",
					"'" + this.getStartPeriod() + "'");

			// 臺灣格式: YYYMM
			strSQL = strSQL
					.replace(
							"%STARTPERIOD_TW%",
							"'"
									+ String.valueOf(Integer.valueOf(this
											.getStartPeriod().substring(
													0,
													this.getStartPeriod()
															.length() - 2)) - 1911)
									+ this.getStartPeriod().substring(
											this.getStartPeriod().length() - 2)
									+ "'");
			// 參數期間
			strSQL = strSQL.replace("%REFPERIOD%", "'"
					+ this.getStartPeriod().substring(4) + "'");
		}
		// 截止期間
		if (!StringUtil.isEmpty(this.getEndPeriod())) {
			// 一般格式：YYYYMM
			strSQL = strSQL.replace("%ENDPERIOD%", "'" + this.getEndPeriod()
					+ "'");

			// 臺灣格式：YYYMM
			strSQL = strSQL
					.replace(
							"%ENDPERIOD_TW%",
							String.valueOf(Integer.valueOf(this.getEndPeriod()
									.substring(0,
											this.getEndPeriod().length() - 2)) - 1911)
									+ this.getEndPeriod().substring(
											this.getEndPeriod().length() - 2));
		}

		while (strSQL.contains("%ROWNO:")) {
			String strRef = strSQL.substring(strSQL.indexOf("%ROWNO:"),
					strSQL.indexOf("%", strSQL.indexOf("%ROWNO:") + 1) + 1);

			String[] refs = strRef.replace("%", "").split(":");
			if (!StringUtils.isEmpty(refs[1])) {
				strSQL = strSQL.replace(strRef, "row_number() over (order by "
						+ refs[1] + ")");

			} else {
				throw new BusinessException(ResHelper.getString(
						"twhr_datainterface", "DataInterface-00069")
						+ "ROWNO:"
						+ refs[1]);// 未找到傳入參數：
			}
		}

		while (strSQL.contains("%REF:")) {
			String strRef = strSQL.substring(strSQL.indexOf("%REF:"),
					strSQL.indexOf("%", strSQL.indexOf("%REF:") + 1) + 1);

			String[] refs = strRef.replace("%", "").split(":");
			String refName = refs[1].toUpperCase();
			if (this.getRefsMap().containsKey(refName)) {
				if (this.getRefsMap().get(refName) instanceof String) {
					strSQL = strSQL.replace(strRef, (String) this.getRefsMap()
							.get(refName));
				} else {
					strSQL = strSQL.replace(strRef,
							String.valueOf(this.getRefsMap().get(refName)));
				}

			} else {
				throw new BusinessException(ResHelper.getString(
						"twhr_datainterface", "DataInterface-00069") + refName);// 未找到傳入參數：
			}
		}

		strSQL = replaceFunctions(lineResultsMap, strSQL);

		return strSQL;
	}

	private String replaceFunctions(Map<Integer, List<Map>> lineResultsMap,
			String strSQL) throws BusinessException {
		// VALUE:LINE
		while (strSQL.contains("%VALUE:LINE")) {
			String strRef = strSQL.substring(strSQL.indexOf("%VALUE:LINE"),
					strSQL.indexOf("%", strSQL.indexOf("%VALUE:LINE") + 1) + 1);

			String[] refs = strRef.replace("%", "").split(":");
			int tarLine = Integer.valueOf(refs[2]);
			String tarField = refs[3].toLowerCase();
			if (lineResultsMap.get(tarLine) != null
					&& lineResultsMap.get(tarLine).size() > 0) {
				if (lineResultsMap.get(tarLine).get(0).get(tarField) == null) {
					strSQL = strSQL.replace(strRef, "");
				} else {
					strSQL = strSQL.replace(
							strRef,
							String.valueOf(lineResultsMap.get(tarLine).get(0)
									.get(tarField)));
				}
			} else {
				throw new BusinessException(ResHelper.getString(
						"twhr_datainterface", "DataInterface-00070"));// 參數VALUE:LINE替換時發生未知錯誤
			}
		}
		// COUNT:LINE
		while (strSQL.contains("%COUNT:LINE")) {
			String strRef = strSQL.substring(strSQL.indexOf("%COUNT:LINE"),
					strSQL.indexOf("%", strSQL.indexOf("%COUNT:LINE") + 1) + 1);

			String[] refs = strRef.replace("%", "").split(":");
			int tarLine = Integer.valueOf(refs[2]);
			String tarField = refs[3].toLowerCase();

			if (tarField.equals("*")) {
				strSQL = strSQL.replace(strRef,
						String.valueOf(lineResultsMap.get(tarLine).size()));
			} else {
				int icount = 0;
				if (lineResultsMap.get(tarField) != null
						&& lineResultsMap.get(tarField).size() > 0) {
					for (int j = 0; j < lineResultsMap.get(tarLine).size(); j++) {
						if (lineResultsMap.get(tarLine).get(j).get(tarField) != null) {
							icount++;
						}
					}
				}
				strSQL = strSQL.replace(strRef, String.valueOf(icount));
			}
		}
		// SUM:LINE
		Map<String, String> sumRef = new HashMap<String, String>();
		while (strSQL.contains("%SUM:LINE")) {
			String strRef = strSQL.substring(strSQL.indexOf("%SUM:LINE"),
					strSQL.indexOf("%", strSQL.indexOf("%SUM:LINE") + 1) + 1);

			String[] refs = strRef.replace("%", "").split(":");
			int tarLine = Integer.valueOf(refs[2]);
			String tarField = refs[3].toLowerCase();

			Double isum = 0.0;
			if (lineResultsMap.get(tarLine) != null
					&& lineResultsMap.get(tarLine).size() > 0) {
				for (int j = 0; j < lineResultsMap.get(tarLine).size(); j++) {
					if (lineResultsMap.get(tarLine).get(j).get(tarField) != null) {
						if (lineResultsMap.get(tarLine).get(j).get(tarField) instanceof Integer) {
							isum += (Integer) lineResultsMap.get(tarLine)
									.get(j).get(tarField);
						} else if (lineResultsMap.get(tarLine).get(j)
								.get(tarField) instanceof BigDecimal)
							isum += ((BigDecimal) lineResultsMap.get(tarLine)
									.get(j).get(tarField)).doubleValue();
					}
				}
			} else {
				strSQL = strSQL.replace(strRef, strRef.replace("%", "&"));
				sumRef.put(strRef.replace("%", "&"), strRef);
				continue;
			}
			strSQL = strSQL.replace(strRef, String.valueOf(isum.intValue()));
		}
		for (Entry<String, String> sumEntry : sumRef.entrySet()) {
			strSQL = strSQL.replace(sumEntry.getKey(),
					"'" + sumEntry.getValue() + "'");
		}

		// MIN:LINE
		while (strSQL.contains("%MIN:LINE")) {
			String strRef = strSQL.substring(strSQL.indexOf("%MIN:LINE"),
					strSQL.indexOf("%", strSQL.indexOf("%MIN:LINE") + 1) + 1);

			String[] refs = strRef.replace("%", "").split(":");
			int tarLine = Integer.valueOf(refs[2]);
			String tarField = refs[3].toLowerCase();

			Double minvalue = Double.MAX_VALUE;
			if (lineResultsMap.get(tarLine) != null
					&& lineResultsMap.get(tarLine).size() > 0) {
				for (int j = 0; j < lineResultsMap.get(tarLine).size(); j++) {
					if (lineResultsMap.get(tarLine).get(j).get(tarField) != null) {
						double value = 0;
						if (lineResultsMap.get(tarLine).get(j).get(tarField) instanceof Long) {
							value = ((Long) lineResultsMap.get(tarLine).get(j)
									.get(tarField)).doubleValue();
						} else if (lineResultsMap.get(tarLine).get(j)
								.get(tarField) instanceof BigDecimal) {
							value = ((BigDecimal) lineResultsMap.get(tarLine)
									.get(j).get(tarField)).doubleValue();
						}
						if (value < minvalue) {
							minvalue = value;
						}
					}
				}
			}
			strSQL = strSQL
					.replace(strRef, String.valueOf(minvalue.intValue()));
		}

		// MAX:LINE
		while (strSQL.contains("%MAX:LINE")) {
			String strRef = strSQL.substring(strSQL.indexOf("%MAX:LINE"),
					strSQL.indexOf("%", strSQL.indexOf("%MAX:LINE") + 1) + 1);

			String[] refs = strRef.replace("%", "").split(":");
			int tarLine = Integer.valueOf(refs[2]);
			String tarField = refs[3].toLowerCase();

			Double maxvalue = Double.MIN_VALUE;
			if (lineResultsMap.get(tarLine) != null
					&& lineResultsMap.get(tarLine).size() > 0) {
				for (int j = 0; j < lineResultsMap.get(tarLine).size(); j++) {
					if (lineResultsMap.get(tarLine).get(j).get(tarField) != null) {
						double value = 0;
						if (lineResultsMap.get(tarLine).get(j).get(tarField) instanceof Long) {
							value = ((Long) lineResultsMap.get(tarLine).get(j)
									.get(tarField)).doubleValue();
						} else if (lineResultsMap.get(tarLine).get(j)
								.get(tarField) instanceof BigDecimal) {
							value = ((BigDecimal) lineResultsMap.get(tarLine)
									.get(j).get(tarField)).doubleValue();
						}
						if (value > maxvalue) {
							maxvalue = value;
						}
					}
				}
			}
			strSQL = strSQL
					.replace(strRef, String.valueOf(maxvalue.intValue()));
		}
		return strSQL;
	}

	/**
	 * 取當前行第一個數據庫中字段作為排序字段
	 * 
	 * @param i
	 *            當前行
	 * @param lineFieldValueMap
	 *            行-字段-取值
	 * @param lineFieldDataSourceMap
	 *            行-字段-數據源
	 * @return
	 */
	private String getRowNoOrderBy(int i,
			Map<Integer, Map<String, String>> lineFieldValueMap,
			Map<Integer, Map<String, Integer>> lineFieldDataSourceMap) {
		for (Map.Entry<Integer, Map<String, Integer>> entry : lineFieldDataSourceMap
				.entrySet()) {
			if (entry.getKey() == i) {
				for (Map.Entry<String, Integer> dsEntry : entry.getValue()
						.entrySet()) {
					if (dsEntry.getValue() == 2) {
						return "row_number() over (order by "
								+ lineFieldValueMap.get(i)
										.get(dsEntry.getKey()) + ")";
					}
				}
			}
		}
		return "";
	}

	private void appendListFieldDataSourceMap(
			Map<Integer, Map<String, Integer>> lineFieldDataSourceMap,
			Map format, int lineNo) {
		if (!lineFieldDataSourceMap.containsKey(lineNo)) {
			lineFieldDataSourceMap.put(lineNo, new HashMap<String, Integer>());
		}
		String itemCode = (String) format.get("itemcode");
		Integer value = (Integer) format.get("datasource");
		if (!lineFieldDataSourceMap.get(lineNo).containsKey(itemCode)) {
			lineFieldDataSourceMap.get(lineNo).put(itemCode, value);
		}

	}

	private String getWherePart(Map<Integer, List<String>> lineJoinKeyMap,
			int lineNo) {
		StringBuilder wherePart = new StringBuilder();
		for (Map.Entry<Integer, List<String>> entry : lineJoinKeyMap.entrySet()) {
			if (entry.getKey() == lineNo) {
				for (String key : entry.getValue()) {
					if (!StringUtil.isEmpty(wherePart.toString())) {
						wherePart.append(" and ");
					}
					wherePart.append(key);
				}
			}
		}

		if (wherePart.toString().trim().equals("")) {
			return "";
		} else {
			return " where " + wherePart.toString();
		}
	}

	private String getFromPart(Map<Integer, List<String>> lineTableMap,
			int lineNo) {
		StringBuilder fromStr = new StringBuilder();
		for (Map.Entry<Integer, List<String>> entry : lineTableMap.entrySet()) {
			if (entry.getKey() == lineNo) {
				for (String table : entry.getValue()) {
					if (!StringUtil.isEmpty(fromStr.toString())) {
						fromStr.append(",");
					}
					fromStr.append(table);
				}
			}
		}

		if (fromStr.toString().trim().equals("")) {
			return "";
		} else {
			return " from " + fromStr.toString();
		}
	}

	private String getSelectPart(
			Map<Integer, Map<String, String>> lineFieldValueMap,
			Map<Integer, Map<String, Integer>> lineFieldDateSourceMap,
			int lineNo) {
		StringBuilder selectStr = new StringBuilder();
		for (Map.Entry<Integer, Map<String, String>> entry : lineFieldValueMap
				.entrySet()) {
			if (entry.getKey() == lineNo) {
				for (Map.Entry<String, String> codeNameEntry : entry.getValue()
						.entrySet()) {
					if (!StringUtil.isEmpty(selectStr.toString())) {
						selectStr.append(",");
					}
					getValueNamePair(lineFieldDateSourceMap, lineNo, selectStr,
							codeNameEntry);
				}
			}
		}

		return "select " + selectStr.toString();
	}

	private void getValueNamePair(
			Map<Integer, Map<String, Integer>> lineFieldDataSourceMap,
			int lineNo, StringBuilder selectStr,
			Map.Entry<String, String> codeNameEntry) {
		String value = codeNameEntry.getValue();
		if (lineFieldDataSourceMap.get(lineNo).get(codeNameEntry.getKey()) == 0) {
			value = "'" + value + "'";
		}
		selectStr.append(value);
		selectStr.append(" as ");
		selectStr.append(codeNameEntry.getKey());
	}

	private void appendListFieldValueMap(
			Map<Integer, Map<String, String>> lineFieldValueMap,
			Map<Integer, List<String>> lineGroupByMap, Map format, int lineNo)
			throws BusinessException {
		if (!lineFieldValueMap.containsKey(lineNo)) {
			lineFieldValueMap.put(lineNo, new HashMap<String, String>());
		}

		if (!lineGroupByMap.containsKey(lineNo)) {
			lineGroupByMap.put(lineNo, new ArrayList<String>());
		}

		String itemCode = (String) format.get("itemcode");
		String valueStr = (String) format.get("datacontext");

		if (format.get("issum").equals("Y")) {
			valueStr = "SUM(" + valueStr + ")";
		} else {
			if ((!valueStr.contains("%ROWNO%"))
					&& ((Integer) format.get("datasource") == 2)
					&& !StringUtil.isEmpty(valueStr.trim())) {
				lineGroupByMap.get(lineNo).add(valueStr);
			}
		}

		if (!StringUtil.isEmpty(valueStr)) {
			if (!lineFieldValueMap.get(lineNo).containsKey(itemCode)) {
				lineFieldValueMap.get(lineNo).put(itemCode, valueStr);
			} else {
				throw new BusinessException(ResHelper.getString(
						"twhr_datainterface", "DataInterface-00063",
						String.valueOf(lineNo), itemCode));
			}
		} else {
			if (!lineFieldValueMap.get(lineNo).containsKey(itemCode)) {
				lineFieldValueMap.get(lineNo).put(itemCode, "");
			} else {
				throw new BusinessException(ResHelper.getString(
						"twhr_datainterface", "DataInterface-00063",
						String.valueOf(lineNo), itemCode));
			}
		}
	}

	private void appendLineWhereMap(Map<Integer, List<String>> lineWhereMap,
			Map format, int lineNo) {
		if (!lineWhereMap.containsKey(lineNo)) {
			lineWhereMap.put(lineNo, new ArrayList<String>());
		}

		if (((Integer) format.get("datasource")) == 2) { // 0:固定值，1:手工輸入，2:公式
			String joinStr = (String) format.get("joinkey");
			String tableStr = (String) format.get("datatable");
			if (!StringUtil.isEmpty(joinStr)
					&& (StringUtil.isEmpty(tableStr) || !tableStr.contains("%"))) {
				String joins[] = joinStr.split(",");
				if (!lineWhereMap.get(lineNo).contains(joinStr)) {
					lineWhereMap.get(lineNo).add(" (" + joinStr + ") ");
				}
			}
		}

	}

	private void appendLineTableMap(Map<Integer, List<String>> lineTableMap,
			Map format, int lineNo) {
		if (!lineTableMap.containsKey(lineNo)) {
			lineTableMap.put(lineNo, new ArrayList<String>());
		}

		if (((Integer) format.get("datasource")) == 2) { // 0:固定值，1:手工輸入，2:公式
			String tableStr = (String) format.get("datatable");
			if (!StringUtil.isEmpty(tableStr) && !tableStr.contains("%")) {
				String tables[] = tableStr.split(",");
				for (String table : tables) {
					String tableName = table.toLowerCase().replace(" as ", " ")
							.trim();// 整理格式：去掉多餘空格
					while (tableName.contains("  ")) {
						tableName = tableName.replace("  ", " ");
					}
					if (!lineTableMap.get(lineNo).contains(tableName)) {
						lineTableMap.get(lineNo).add(tableName);
					}
				}
			}
		}
	}

	public Map<String, String> getJointTables() {
		if (jointTables == null) {
			jointTables = new HashMap<String, String>();
		}

		return jointTables;
	}

	public String getFormatterCode() {
		return formatterCode;
	}

	public void setFormatterCode(String formatterCode) {
		this.formatterCode = formatterCode;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getStartPeriod() {
		return startPeriod;
	}

	public void setStartPeriod(String startPeriod) {
		this.startPeriod = startPeriod;
	}

	public String getEndPeriod() {
		return endPeriod;
	}

	public void setEndPeriod(String endPeriod) {
		this.endPeriod = endPeriod;
	}

	public Integer getiYear() {
		return iYear;
	}

	public void setiYear(Integer iYear) {
		this.iYear = iYear;
	}

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public String[] getClassIDs() {
		return classIDs;
	}

	public void setClassIDs(String[] classIDs) {
		this.classIDs = classIDs;
	}

	public Map<String, Object> getRefsMap() {
		if (refsMap == null) {
			refsMap = new HashMap<String, Object>();
		}
		return refsMap;
	}

	public void setRefsMap(Map<String, Object> refsMap) {
		this.refsMap = refsMap;
	}

	public String[] getPk_orgs() {
		return pk_orgs;
	}

	public void setPk_orgs(String[] pk_orgs) {
		if (pk_orgs != null && pk_orgs.length == 1) {
			this.pk_org = pk_orgs[0];
			this.pk_orgs = null;
		} else if (pk_orgs != null && pk_orgs.length > 1) {
			this.pk_org = "";
			this.pk_orgs = pk_orgs;
		}
	}

}
