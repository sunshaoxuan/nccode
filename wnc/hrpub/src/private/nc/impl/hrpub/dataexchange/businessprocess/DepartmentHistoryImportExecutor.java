package nc.impl.hrpub.dataexchange.businessprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.uap.oid.OidGenerator;
import nc.impl.hrpub.dataexchange.DataImportExecutor;
import nc.itf.hrpub.IDataExchangeExternalExecutor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.om.hrdept.DeptChangeType;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.lang.StringUtils;

public class DepartmentHistoryImportExecutor extends DataImportExecutor implements IDataExchangeExternalExecutor {

	private List<String> notNullProperties = new ArrayList<String>();

	public DepartmentHistoryImportExecutor() throws BusinessException {
		super();

		this.notNullProperties.add("pk_dept");
		this.setActionOnDataExists(ExecuteActionEnum.UPDATE);
	}

	/**
	 * 保存前事件
	 * 
	 * @throws BusinessException
	 */
	public void beforeUpdate() throws BusinessException {
		// 增加额外的唯一性检查条件，基類在插入數據之前，增加該條件的檢查，以確保業務主鍵不重複
		this.setUniqueCheckExtraCondition("effectdate='$effectdate$' and changetype != 1");
		// 增加排他的唯一性检查条件，基類在插入數據之前，用此條件替換檢查條件，不再以基類的按Code轉PK加額外條件檢查
		this.setUniqueCheckExclusiveCondition(" code='$code$' and " + this.getUniqueCheckExtraCondition());

		if (this.getNcValueObjects() != null && this.getNcValueObjects().size() > 0) {
			String rowNo = "";
			for (Map<String, Object> rowNCMap : this.getNcValueObjects()) {
				try {
					rowNo = rowNCMap.keySet().toArray(new String[0])[0].split(":")[0];

					// 本次操作部門Code
					String newDeptCode = (String) rowNCMap.get(rowNo + ":code");
					// 变化历史部门Code
					String oldDeptCode = (String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("ODEPNO"));

					// 历史部门创建日期
					String oldDeptCreateDate = getDateString((String) rowNCMap.get(rowNo + ":"
							+ this.getReservedPropertyName("OEFFDATE")));

					// 日期格式整理
					rowNCMap.put(rowNo + ":effectdate", getDateString((String) rowNCMap.get(rowNo + ":effectdate")));

					// 本次操作部门生效日期
					String newDeptEffectDate = getDateString((String) rowNCMap.get(rowNo + ":effectdate"));

					// 本次变更类型
					String changeType = getChangeType(rowNCMap, rowNo);

					// 本次要移動的變更記錄
					List<String> depts_move = new ArrayList<String>();
					// 本次要作廢的創建記錄
					List<String> depts_cancel = new ArrayList<String>();

					// 先取部門ID
					String pk_dept = getDeptByCode(newDeptCode, newDeptEffectDate);

					// 容錯處理：出現髒數據（部門為空時）跳過後續處理
					if (StringUtils.isEmpty(pk_dept) || "~".equals(pk_dept)) {
						continue;
					}

					// 補充新增變更主體記錄資料
					dealMainDeptHistory(rowNo, rowNCMap, pk_dept, changeType);

					if (!this.getExtendSQLs().containsKey(rowNo)) {
						this.getExtendSQLs().put(rowNo, new ArrayList<String>());
					}

					// 生成“更新部門創建記錄編碼及生效時間”SQL
					this.getExtendSQLs().get(rowNo).addAll(dealCreateInfo(pk_dept, oldDeptCode, oldDeptCreateDate));

					// 变更负责人只创建变更记录，不创建新版本
					if (!DeptChangeType.CHANGEPRINCIPAL.equals(changeType)) {
						dealRelatedTables(rowNo, rowNCMap, newDeptCode, oldDeptCreateDate, depts_move, depts_cancel,
								pk_dept);
					}

				} catch (Exception e) {
					this.getErrorMessages().put(rowNo, e.getMessage());
				}
			}
		}
	}

	private String getDeptByCode(String newDeptCode, String newDeptEffectDate) throws BusinessException {
		// PK_DEPT = 歷史記錄(歷史記錄.Code=新Code and 歷史記錄.生效日期=生效日期).PK_DEPT
		// IF ISNULL(PK_DEPT) THEN
		// //找不到歷史記錄
		// PK_DEPT = 部門主檔(Code=新Code)
		// IF ISNULL(PK_DEPT) THEN
		// //報錯：無法找到宿主部門
		// END
		// END
		String strSQL = "select pk_dept from om_depthistory where code=" + getStringValue(newDeptCode)
				+ " and effectdate=" + getStringValue(newDeptEffectDate) + " and changetype=1";
		String pk_dept = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		if (StringUtils.isEmpty(pk_dept)) {
			strSQL = "select pk_dept from org_dept where code=" + getStringValue(newDeptCode);
			pk_dept = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
			if (StringUtils.isEmpty(pk_dept)) {
				throw new BusinessException("无法找到宿主部门 [" + newDeptCode + "]");
			}
		}
		return pk_dept;
	}

	private void dealRelatedTables(String rowNo, Map<String, Object> rowNCMap, String deptCode, String effectDate,
			List<String> depts_move, List<String> depts_cancel, String pk_dept) throws BusinessException {
		String strVNO = getDateString((String) rowNCMap.get(rowNo + ":effectdate")).replace("-", "").substring(0, 6);
		String pk_dept_v = getDeptVIDByVNO(pk_dept, strVNO, effectDate);
		String innerCode = (isPropertyChanged(rowNCMap, rowNo, this.getReservedPropertyName("OPARENT"),
				this.getReservedPropertyName("NPARENT")) ? getStringValue(getInnerCode((String) rowNCMap.get(rowNo
				+ ":" + this.getReservedPropertyName("OPARENT")))) : "INNERCODE");
		String pk_fatherorg = getOldFatherDeptID((String) rowNCMap.get(rowNo + ":"
				+ this.getReservedPropertyName("OPARENT")));
		String pk_principal = getPrincipalIDByCode((String) rowNCMap.get(rowNo + ":"
				+ this.getReservedPropertyName("OEMPNO1")));

		String pk_vid = StringUtils.isEmpty(pk_dept_v) ? OidGenerator.getInstance().nextOid() : null;

		// 生成“新增部門版本”SQL
		this.getExtendSQLs()
				.get(rowNo)
				.addAll(createDeptVersion(rowNCMap, rowNo, pk_dept, pk_dept_v, innerCode, pk_fatherorg, pk_principal,
						strVNO, pk_vid));

		// 生成“新增組織版本”SQL
		this.getExtendSQLs()
				.get(rowNo)
				.addAll(createOrgVersion(rowNCMap, rowNo, pk_dept, pk_dept_v, innerCode, pk_fatherorg, pk_principal,
						strVNO, pk_vid));

		if (depts_cancel.size() > 0) {
			// 生成“作廢創建部門PK所屬部門”SQL
			this.getExtendSQLs().get(rowNo).addAll(cancelDept(deptCode, depts_cancel));
		}

		if (depts_move.size() > 0) {
			depts_move.addAll(depts_cancel);

			// 生成“移動作廢部門變更記錄到變更主體”SQL
			this.getExtendSQLs().get(rowNo).addAll(dealMoveDeptHistory(deptCode, depts_move, pk_dept));
			// 生成“作廢部門版本到變更主體”SQL
			this.getExtendSQLs().get(rowNo).addAll(cancelMoveDeptVersion(depts_move));
			// 生成“作廢組織”SQL
			this.getExtendSQLs().get(rowNo).addAll(cancelMoveOrg(depts_move));
			// 生成“作廢組織版本”SQL
			this.getExtendSQLs().get(rowNo).addAll(cancelMoveOrgVersion(depts_move));
		}
	}

	private List<String> cancelMoveOrgVersion(List<String> depts_move) {
		List<String> sqls = new ArrayList<String>();
		for (String pk_dept : depts_move) {
			sqls.add("UPDATE ORG_ORGS_V SET ENABLESTATE=3 WHERE PK_ORG='" + pk_dept + "';");
		}
		return sqls;
	}

	private List<String> cancelMoveOrg(List<String> depts_move) {
		List<String> sqls = new ArrayList<String>();
		for (String pk_dept : depts_move) {
			sqls.add("UPDATE ORG_ORGS SET ENABLESTATE=3 WHERE PK_ORG='" + pk_dept + "';");
		}
		return sqls;
	}

	private List<String> cancelMoveDeptVersion(List<String> depts_move) {
		List<String> sqls = new ArrayList<String>();
		for (String pk_dept : depts_move) {
			sqls.add("UPDATE ORG_DEPT_V SET ENABLESTATE=3 WHERE PK_DEPT='" + pk_dept + "';");
		}
		return sqls;
	}

	private List<String> dealMoveDeptHistory(String deptCode, List<String> depts_move, String pk_dept_new) {
		List<String> sqls = new ArrayList<String>();
		for (String pk_dept_old : depts_move) {
			sqls.add("UPDATE OM_DEPTHISTORY SET PK_DEPT='" + pk_dept_new + "' WHERE PK_DEPT='" + pk_dept_old
					+ "' AND CODE='" + deptCode + "' AND CHANGETYPE!=1;");
		}
		return sqls;
	}

	private List<String> cancelDept(String deptCode, List<String> depts_cancel) {
		List<String> sqls = new ArrayList<String>();
		for (String pk_dept : depts_cancel) {
			sqls.add("UPDATE ORG_DEPT SET ENABLESTATE=3 WHERE PK_DEPT='" + pk_dept + "';");
		}
		return sqls;
	}

	private List<String> createOrgVersion(Map<String, Object> rowNCMap, String rowno, String pk_dept, String pk_dept_v,
			String innerCode, String pk_fatherorg, String pk_principal, String strVNO, String pk_vid) {
		List<String> sqls = new ArrayList<String>();

		if (StringUtils.isEmpty(pk_dept_v)) {
			sqls.add("INSERT INTO ORG_ORGS_V " + "(ADDRESS, CODE, COUNTRYZONE, CREATIONTIME, CREATOR, DATAORIGINFLAG, "
					+ "DEF1, DEF10, DEF11, DEF12, DEF13, DEF14, DEF15, DEF16, DEF17, DEF18, DEF19, "
					+ "DEF2, DEF20, DEF3, DEF4, DEF5, DEF6, DEF7, DEF8, DEF9, DR, ENABLESTATE, "
					+ "INNERCODE, ISBUSINESSUNIT, ISLASTVERSION, MEMO, MNECODE, MODIFIEDTIME, "
					+ "MODIFIER, NAME, NAME2, NAME3, NAME4, NAME5, NAME6, NCINDUSTRY, "
					+ "ORGANIZATIONCODE, ORGTYPE1, ORGTYPE10, ORGTYPE11, ORGTYPE12, ORGTYPE13, "
					+ "ORGTYPE14, ORGTYPE15, ORGTYPE16, ORGTYPE17, ORGTYPE18, ORGTYPE19, "
					+ "ORGTYPE2, ORGTYPE20, ORGTYPE21, ORGTYPE22, ORGTYPE23, ORGTYPE24, "
					+ "ORGTYPE25, ORGTYPE26, ORGTYPE27, ORGTYPE28, ORGTYPE29, ORGTYPE3, "
					+ "ORGTYPE30, ORGTYPE31, ORGTYPE32, ORGTYPE33, ORGTYPE34, ORGTYPE35, "
					+ "ORGTYPE36, ORGTYPE37, ORGTYPE38, ORGTYPE39, ORGTYPE4, ORGTYPE40, "
					+ "ORGTYPE41, ORGTYPE42, ORGTYPE43, ORGTYPE44, ORGTYPE45, ORGTYPE46, "
					+ "ORGTYPE47, ORGTYPE48, ORGTYPE49, ORGTYPE5, ORGTYPE50, ORGTYPE6, "
					+ "ORGTYPE7, ORGTYPE8, ORGTYPE9, PK_FATHERORG, PK_FORMAT, PK_GROUP, "
					+ "PK_ORG, PK_OWNORG, PK_TIMEZONE, PK_VID, PRINCIPAL, SHORTNAME, SHORTNAME2, "
					+ "SHORTNAME3, SHORTNAME4, SHORTNAME5, SHORTNAME6, TEL, VENDDATE, "
					+ "VNAME, VNAME2, VNAME3, VNAME4, VNAME5, VNAME6, VNO, VSTARTDATE, "
					+ "CHARGELEADER, ENTITYTYPE, ISBALANCEUNIT, ISRETAIL, PK_ACCPERIODSCHEME, "
					+ "PK_CONTROLAREA, PK_CORP, PK_CURRTYPE, PK_EXRATESCHEME, REPORTCONFIRM, " + "WORKCALENDAR) "
					+ "SELECT " + "ADDRESS, "
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("ODEPNO"), "code") ? getStringValue((String) rowNCMap
							.get(rowno + ":" + this.getReservedPropertyName("ODEPNO"))) : "CODE")
					+ ", COUNTRYZONE, CREATIONTIME, CREATOR, DATAORIGINFLAG, "
					+ "DEF1, DEF10, DEF11, DEF12, DEF13, DEF14, DEF15, DEF16, DEF17, DEF18, DEF19, "
					+ "DEF2, DEF20, DEF3, DEF4, DEF5, DEF6, DEF7, DEF8, DEF9, DR, ENABLESTATE, "
					+ innerCode
					+ ", ISBUSINESSUNIT, ISLASTVERSION, MEMO, MNECODE, MODIFIEDTIME, "
					+ "MODIFIER, "
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("ODEPNM"), "name") ? getStringValue(rowNCMap
							.get(rowno + ":" + this.getReservedPropertyName("ODEPNM"))) : "NAME")
					+ ", "
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("ODEPNM"), "name") ? getStringValue(rowNCMap
							.get(rowno + ":" + this.getReservedPropertyName("ODEPNM"))) : "NAME2")
					+ ", "
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OEDEPNM"),
							this.getReservedPropertyName("NEDEPNM")) ? getStringValue(rowNCMap.get(rowno + ":"
							+ this.getReservedPropertyName("OEDEPNM"))) : "NAME3")
					+ ", NAME4, NAME5, NAME6, NCINDUSTRY, "
					+ "ORGANIZATIONCODE, ORGTYPE1, ORGTYPE10, ORGTYPE11, ORGTYPE12, ORGTYPE13, "
					+ "ORGTYPE14, ORGTYPE15, ORGTYPE16, ORGTYPE17, ORGTYPE18, ORGTYPE19, "
					+ "ORGTYPE2, ORGTYPE20, ORGTYPE21, ORGTYPE22, ORGTYPE23, ORGTYPE24, "
					+ "ORGTYPE25, ORGTYPE26, ORGTYPE27, ORGTYPE28, ORGTYPE29, ORGTYPE3, "
					+ "ORGTYPE30, ORGTYPE31, ORGTYPE32, ORGTYPE33, ORGTYPE34, ORGTYPE35, "
					+ "ORGTYPE36, ORGTYPE37, ORGTYPE38, ORGTYPE39, ORGTYPE4, ORGTYPE40, "
					+ "ORGTYPE41, ORGTYPE42, ORGTYPE43, ORGTYPE44, ORGTYPE45, ORGTYPE46, "
					+ "ORGTYPE47, ORGTYPE48, ORGTYPE49, ORGTYPE5, ORGTYPE50, ORGTYPE6, "
					+ "ORGTYPE7, ORGTYPE8, ORGTYPE9, "
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OPARENT"),
							this.getReservedPropertyName("NPARENT")) ? getStringValue(pk_fatherorg) : "PK_FATHERORG")
					+ ", PK_FORMAT, PK_GROUP, "
					+ "PK_ORG, PK_OWNORG, PK_TIMEZONE, "
					+ getStringValue(pk_vid)
					+ ", "
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OEMPNO1"), "principal") ? getStringValue(pk_principal)
							: "PRINCIPAL")
					+ ", SHORTNAME, SHORTNAME2, "
					+ "SHORTNAME3, SHORTNAME4, SHORTNAME5, SHORTNAME6, TEL, VENDDATE, "
					+ ""
					+ getStringValue(strVNO)
					+ ", "
					+ getStringValue(strVNO)
					+ ", "
					+ getStringValue(strVNO)
					+ ", VNAME4, VNAME5, VNAME6, "
					+ getStringValue(strVNO)
					+ ", VSTARTDATE, "
					+ "CHARGELEADER, ENTITYTYPE, ISBALANCEUNIT, ISRETAIL, PK_ACCPERIODSCHEME, "
					+ "PK_CONTROLAREA, PK_CORP, PK_CURRTYPE, PK_EXRATESCHEME, REPORTCONFIRM, "
					+ "WORKCALENDAR "
					+ "FROM ORG_ORGS_V  " + "WHERE PK_ORG = '" + pk_dept + "' AND ROWNUM=1 ORDER BY VENDDATE ");
		} else {
			sqls.add("UPDATE ORG_ORGS_V SET CODE="
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("ODEPNO"), "code") ? getStringValue(rowNCMap
							.get(rowno + ":" + this.getReservedPropertyName("ODEPNO"))) : "CODE")
					+ ", NAME="
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("ODEPNM"), "name") ? getStringValue(rowNCMap
							.get(rowno + ":" + this.getReservedPropertyName("ODEPNM"))) : "NAME")
					+ ", NAME2="
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("ODEPNM"), "name") ? getStringValue(rowNCMap
							.get(rowno + ":" + this.getReservedPropertyName("ODEPNM"))) : "NAME2")
					+ ", NAME3="
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OEDEPNM"),
							this.getReservedPropertyName("NEDEPNM")) ? getStringValue(rowNCMap.get(rowno + ":"
							+ this.getReservedPropertyName("OEDEPNM"))) : "NAME3")
					+ ", PK_FATHERORG="
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OPARENT"),
							this.getReservedPropertyName("NPARENT")) ? getStringValue(pk_fatherorg) : "PK_FATHERORG")
					+ ", PRINCIPAL="
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OEMPNO1"), "principal") ? getStringValue(pk_principal)
							: "PRINCIPAL") + " WHERE PK_VID=" + getStringValue(pk_dept_v) + ";");
		}

		return sqls;
	}

	private void dealMainDeptHistory(String rowNo, Map<String, Object> rowNCMap, String pk_dept, String changeType)
			throws BusinessException {
		// 舊部門編碼也使用當前變更主體PK
		if (!this.deptCodePKMap
				.containsKey((String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("ODEPNO")))) {
			this.setDeptCodePKMap((String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("ODEPNO")), pk_dept);
		}
		rowNCMap.put(rowNo + ":pk_dept", pk_dept);

		// 日期格式整理
		rowNCMap.put(rowNo + ":effectdate", getDateString((String) rowNCMap.get(rowNo + ":effectdate")));

		// 处理变更记录
		// 找当前变更部门是否存在相同的生效日期
		// 逻辑：在部门版本中查找本次传新Code及生效日期相同的记录，关联其pk_dept
		// 理由：如果Code发生过变化，一直在org_dept表中找不到历史版本的code，造成一直新增无法更新
		String strSQL = "select pk_depthistory from om_depthistory hst inner join org_dept_v dpt on hst.pk_dept = dpt.pk_dept where dpt.code = '"
				+ (String) rowNCMap.get(rowNo + ":code")
				+ "' and hst.changetype != 1 and hst.effectdate='"
				+ getDateString((String) rowNCMap.get(rowNo + ":effectdate")) + "'";
		String pk_depthistory = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());

		if (StringUtils.isEmpty(pk_depthistory)) {
			// 如果为空，则新增，不为空则更新
			// 新增的条件是pk_depthistory在数据库中找不到，默认逻辑已支持

			// 新增时需要补充的属性
			rowNCMap.put(rowNo + ":changenum", OidGenerator.getInstance().nextOid());

			// pk_org
			rowNCMap.put(rowNo + ":pk_org",
					getPk_org(((String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("NCOMNO"))).trim()));

			// pk_org_v
			rowNCMap.put(rowNo + ":pk_org_v",
					getPk_org_v(((String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("NCOMNO"))).trim()));
		}

		// 异动类型
		rowNCMap.put(rowNo + ":changetype", changeType);

		// Name2暂同步为Name
		rowNCMap.put(rowNo + ":name2", ((String) rowNCMap.get(rowNo + ":name")).trim());

		// Name3暂同步为新英文名
		rowNCMap.put(rowNo + ":name3",
				((String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("NEDEPNM"))).trim());

		rowNCMap.put(rowNo + ":isreceived", "N");

		// 部门级别同步为新部门级别
		rowNCMap.put(rowNo + ":deptlevel",
				getDeptLevel(((String) rowNCMap.get(rowNo + ":" + this.getReservedPropertyName("NEXP_LEVEL"))).trim()));
	}

	private List<String> dealCreateInfo(String pk_dept, String oldDeptCode, String oldDeptCreateDate) {
		List<String> sqls = new ArrayList<String>();

		oldDeptCreateDate = getDateString(oldDeptCreateDate);
		// 更新變更歷史，使創立部門日期及編號變為舊的資料
		sqls.add("update om_depthistory set code=" + getStringValue(oldDeptCode)
				+ (StringUtils.isEmpty(oldDeptCreateDate) ? "" : ", effectdate=" + getStringValue(oldDeptCreateDate))
				+ " where pk_dept=" + getStringValue(pk_dept) + " and changetype=1;");

		if (!StringUtils.isEmpty(oldDeptCreateDate)) {
			// 更新部門主檔，使創立部門日期變為舊的資料
			sqls.add("update org_dept set createdate="
					+ getStringValue(oldDeptCreateDate)
					+ (StringUtils.isEmpty(oldDeptCreateDate) ? "" : ", vstartdate="
							+ getStringValue(getDateString(oldDeptCreateDate + " 00:00:00"))) + " where pk_dept="
					+ getStringValue(pk_dept) + ";");
			sqls.add("update org_dept_v set createdate=" + getStringValue(oldDeptCreateDate) + ", vstartdate="
					+ getStringValue(getDateString(oldDeptCreateDate + " 00:00:00")) + ", vno="
					+ getStringValue(oldDeptCreateDate.replace("-", "").substring(0, 4) + "00") + " where pk_dept="
					+ getStringValue(pk_dept) + " and vname='初始版本';");
			sqls.add("update org_orgs set vstartdate=" + getStringValue(getDateString(oldDeptCreateDate + " 00:00:00"))
					+ " where pk_org=" + getStringValue(pk_dept) + ";");
			sqls.add("update org_orgs_v set vstartdate="
					+ getStringValue(getDateString(oldDeptCreateDate + " 00:00:00")) + ",vno="
					+ getStringValue(oldDeptCreateDate.replace("-", "").substring(0, 4) + "00") + " where pk_org="
					+ getStringValue(pk_dept) + " and vname='初始版本';");
		}

		return sqls;
	}

	private Map<String, String> orgMap = new HashMap<String, String>();

	private String getPk_org(String orgCode) throws BusinessException {
		if (!orgMap.containsKey(orgCode)) {
			String strSQL = "select pk_org from org_orgs where islastversion='Y' and code = '" + orgCode + "'";
			String pk_org = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
			orgMap.put(orgCode, pk_org);
		}
		return orgMap.get(orgCode);
	}

	private Map<String, String> orgvMap = new HashMap<String, String>();

	private String getPk_org_v(String orgCode) throws BusinessException {
		if (!orgvMap.containsKey(orgCode)) {
			String strSQL = "select pk_vid from org_orgs where islastversion='Y' and code =" + getStringValue(orgCode);
			String pk_org = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
			orgvMap.put(orgCode, pk_org);
		}
		return orgvMap.get(orgCode);
	}

	private Map<String, String> deptLevelMap = new HashMap<String, String>();

	protected String getDeptLevel(String deptLevel) throws BusinessException {
		if (!deptLevelMap.containsKey(deptLevel)) {
			String strSQL = "select pk_defdoc from bd_defdoc where pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'ORG002_0xx') and code = '"
					+ deptLevel + "'";
			String pk_deptlevel = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
			deptLevelMap.put(deptLevel, pk_deptlevel);
		}
		return deptLevelMap.get(deptLevel);
	}

	protected String getChangeType(Map<String, Object> rowNCMap, String rowno) throws BusinessException {
		String cancelDate = (String) rowNCMap.get(rowno + ":" + this.getReservedPropertyName("CANCELED"));
		String oldDeptCode = (String) rowNCMap.get(rowno + ":" + this.getReservedPropertyName("ODEPNO"));
		if (!StringUtils.isEmpty(cancelDate.trim())) {
			// 失效日期不为空，则为撤消
			return DeptChangeType.HRCANCELED;
		} else if (cancelDate == null || StringUtils.isEmpty(cancelDate.trim())) {
			// 失效日期为空，则需要看部门档案是否已撤消
			String pk_dept = getDeptByCode(oldDeptCode, new ArrayList<String>(), new ArrayList<String>());
			HRDeptVO deptvo = (HRDeptVO) this.getBaseDAO().retrieveByPK(HRDeptVO.class, pk_dept);
			if (deptvo.getHrcanceled() != null && deptvo.getHrcanceled().booleanValue()) {
				return DeptChangeType.HRUNCANCELED;
			}
		}
		if (this.isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OPARENT"),
				this.getReservedPropertyName("NPARENT"))) {
			// 單元內轉移
			return DeptChangeType.SHIFT;
		} else if (this.isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("ODEPNM"), "name")
				|| this.isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("ODEPNO"), "code")) {
			// 更名
			return DeptChangeType.RENAME;
		} else if (this.isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OEMPNO1"), "principal")) {
			// 負責人變更
			return DeptChangeType.CHANGEPRINCIPAL;
		}

		throw new BusinessException("未找到支持的部門變更類型");
	}

	private List<String> createDeptVersion(Map<String, Object> rowNCMap, String rowno, String pk_dept,
			String pk_dept_v, String innerCode, String pk_fatherorg, String pk_principal, String strVNO, String pk_vid)
			throws BusinessException {
		List<String> sqls = new ArrayList<String>();

		// 建新部门历史版本
		// org_dept_v
		if (StringUtils.isEmpty(pk_dept_v)) {
			sqls.add("INSERT INTO ORG_DEPT_V " + "(ADDRESS, CODE, CREATEDATE, CREATIONTIME, CREATOR, "
					+ "DATAORIGINFLAG, DEF1, DEF10, DEF11, DEF12, DEF13, DEF14, DEF15, DEF16, DEF17, "
					+ "DEF18, DEF19, DEF2, DEF20, DEF3, DEF4, DEF5, DEF6, DEF7, DEF8, DEF9, "
					+ "DEPTCANCELDATE, DEPTTYPE, DISPLAYORDER, DR, ENABLESTATE, HRCANCELED, "
					+ "INNERCODE, ISLASTVERSION, ISRETAIL, MEMO, MNECODE, MODIFIEDTIME, MODIFIER, "
					+ "NAME, NAME2, NAME3, NAME4, NAME5, NAME6, PK_DEPT, PK_FATHERORG, PK_GROUP, "
					+ "PK_ORG, PK_VID, PRINCIPAL, RESPOSITION, SHORTNAME, SHORTNAME2, SHORTNAME3, "
					+ "SHORTNAME4, SHORTNAME5, SHORTNAME6, TEL, VENDDATE, VNAME, VNAME2, VNAME3, "
					+ "VNAME4, VNAME5, VNAME6, VNO, VSTARTDATE, CHARGELEADER, DEPTLEVEL, ORGTYPE13, "
					+ "ORGTYPE17, DEPTDUTY) " + "SELECT ADDRESS, "
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("ODEPNO"), "code") ? getStringValue(rowNCMap
							.get(rowno + ":" + this.getReservedPropertyName("ODEPNO"))) : "CODE")
					+ ", CREATEDATE, CREATIONTIME, CREATOR, "
					+ "DATAORIGINFLAG, DEF1, DEF10, DEF11, DEF12, DEF13, DEF14, DEF15, DEF16, DEF17, "
					+ "DEF18, DEF19, DEF2, DEF20, DEF3, DEF4, DEF5, DEF6, DEF7, DEF8, DEF9, "
					+ "NULL, DEPTTYPE, DISPLAYORDER, DR, ENABLESTATE, 'N', "
					+ innerCode
					+ ", 'N', ISRETAIL, MEMO, MNECODE, MODIFIEDTIME, MODIFIER, "
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("ODEPNM"), "name") ? getStringValue(rowNCMap
							.get(rowno + ":" + this.getReservedPropertyName("ODEPNM"))) : "NAME")
					+ ", "
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("ODEPNM"), "name") ? getStringValue(rowNCMap
							.get(rowno + ":" + this.getReservedPropertyName("ODEPNM"))) : "NAME2")
					+ ", "
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OEDEPNM"),
							this.getReservedPropertyName("NEDEPNM")) ? getStringValue(rowNCMap.get(rowno + ":"
							+ this.getReservedPropertyName("OEDEPNM"))) : "NAME3")
					+ ", NAME4, NAME5, NAME6, PK_DEPT, "
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OPARENT"),
							this.getReservedPropertyName("NPARENT")) ? getStringValue(pk_fatherorg) : "PK_FATHERORG")
					+ ", PK_GROUP, "
					+ "PK_ORG, "
					+ getStringValue(pk_vid)
					+ ", "
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OEMPNO1"), "principal") ? getStringValue(pk_principal)
							: "PRINCIPAL")
					+ ", RESPOSITION, SHORTNAME, SHORTNAME2, SHORTNAME3, "
					+ "SHORTNAME4, SHORTNAME5, SHORTNAME6, TEL, VENDDATE, "
					+ getStringValue(strVNO)
					+ ", "
					+ getStringValue(strVNO)
					+ ", "
					+ getStringValue(strVNO)
					+ ", "
					+ "VNAME4, VNAME5, VNAME6, "
					+ getStringValue(strVNO)
					+ ", VSTARTDATE, CHARGELEADER, "
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OEXP_LEVEL"),
							this.getReservedPropertyName("NEXP_LEVEL")) ? getStringValue(rowNCMap.get(rowno + ":"
							+ this.getReservedPropertyName("OEXP_LEVEL"))) : "DEPTLEVEL")
					+ ", ORGTYPE13, "
					+ "ORGTYPE17, DEPTDUTY "
					+ "FROM ORG_DEPT_V WHERE PK_DEPT = "
					+ getStringValue(pk_dept)
					+ " AND ROWNUM=1 ORDER BY VENDDATE");
		} else {
			sqls.add("UPDATE ORG_DEPT_V SET CODE="
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("ODEPNO"), "code") ? getStringValue(rowNCMap
							.get(rowno + ":" + this.getReservedPropertyName("ODEPNO"))) : "CODE")
					+ ", NAME="
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("ODEPNM"), "name") ? getStringValue(rowNCMap
							.get(rowno + ":" + this.getReservedPropertyName("ODEPNM"))) : "NAME")
					+ ", NAME2="
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("ODEPNM"), "name") ? getStringValue(rowNCMap
							.get(rowno + ":" + this.getReservedPropertyName("ODEPNM"))) : "NAME2")
					+ ", NAME3="
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OEDEPNM"),
							this.getReservedPropertyName("NEDEPNM")) ? getStringValue(rowNCMap.get(rowno + ":"
							+ this.getReservedPropertyName("OEDEPNM"))) : "NAME3")
					+ ", PK_FATHERORG="
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OPARENT"),
							this.getReservedPropertyName("NPARENT")) ? getStringValue(pk_fatherorg) : "PK_FATHERORG")
					+ ", PRINCIPAL="
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OEMPNO1"), "principal") ? getStringValue(pk_principal)
							: "PRINCIPAL")
					+ ", DEPTLEVEL="
					+ (isPropertyChanged(rowNCMap, rowno, this.getReservedPropertyName("OEXP_LEVEL"),
							this.getReservedPropertyName("NEXP_LEVEL")) ? getStringValue(rowNCMap.get(rowno + ":"
							+ this.getReservedPropertyName("OEXP_LEVEL"))) : "DEPTLEVEL") + "  WHERE PK_VID="
					+ getStringValue(pk_dept_v) + ";");
		}

		return sqls;

	}

	protected String getPrincipalIDByCode(String psnCode) throws BusinessException {
		String strSQL = "select pk_psndoc from bd_psndoc where code = '" + psnCode + "'";
		String pk_psndoc = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		return pk_psndoc;
	}

	// 查找父部門ID
	// 邏輯要點：尋找當前數據庫中父部門的最早版本
	private String getOldFatherDeptID(String oldFatherOrgCode) throws BusinessException {
		String strSQL = "select pk_dept from org_dept where code = '" + oldFatherOrgCode
				+ "' and rownum=1 order by VENDDATE";
		String pk_fatherorg = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		return pk_fatherorg;
	}

	private String getDeptVIDByVNO(String pk_dept, String VNO, String effectDate) throws BusinessException {
		String strSQL = "select pk_vid from org_dept_v where VNO =" + getStringValue(VNO) + " and pk_dept="
				+ getStringValue(pk_dept) + " and createdate=" + getStringValue(getDateString(effectDate)) + ";";
		String pk_vid = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());

		return pk_vid;
	}

	private Map<String, String> deptCodePKMap = new HashMap<String, String>();

	private void setDeptCodePKMap(String code, String pk) {
		if (!deptCodePKMap.containsKey(code)) {
			deptCodePKMap.put(code, pk);
		}
	}

	protected String getDeptByCode(String deptCode, List<String> pk_depts_move, List<String> pk_depts_cancel)
			throws BusinessException {
		String pk_dept = null;

		if (deptCodePKMap.containsKey(deptCode)) {
			pk_dept = deptCodePKMap.get(deptCode);
		} else {
			String strSQL = "select pk_dept, changetype from om_depthistory where code = '" + deptCode
					+ "' order by effectdate desc;";
			List<Map> deptHistories = (List<Map>) this.getBaseDAO().executeQuery(strSQL, new MapListProcessor());

			if (deptHistories == null || deptHistories.size() == 0) {
				throw new BusinessException("無法找到部門 [" + deptCode + "]");
			} else {
				String pk_dept_create = "";
				List<String> pk_depts_otherChange = new ArrayList<String>();
				for (Map<String, Object> deptHistory : deptHistories) {
					pk_dept = (String) deptHistory.get("pk_dept");
					for (Entry<String, Object> historyDetail : deptHistory.entrySet()) {
						if (historyDetail.getKey().equals("changetype")) {
							if (DeptChangeType.ESTABLISH.equals(historyDetail.getValue())) {
								pk_dept_create = pk_dept;
							} else {
								if (!pk_depts_otherChange.contains(pk_dept)) {
									pk_depts_otherChange.add(pk_dept);
								}
							}
						}
					}
				}

				if (pk_depts_otherChange.size() == 0) {
					// 只有創建記錄，取創建部門PK，作為部門變更主體PK
					pk_dept = pk_dept_create;
				} else {
					// 變更記錄部門PK=創建部門PK，是則確定部門變更主體PK
					pk_dept = pk_depts_otherChange.get(0);
					if (pk_depts_otherChange.size() > 1) {
						for (String pk_dept_change : pk_depts_otherChange) {
							if (!pk_dept.equals(pk_dept_change)) {
								pk_depts_move.add(pk_dept_change);
							}
						}
					}
					pk_depts_cancel.add(pk_dept_create);
				}

				setDeptCodePKMap(deptCode, pk_dept);
			}
		}

		return pk_dept;
	}

	private String getInnerCode(String oldFatherDeptCode) throws BusinessException {
		String innercode = "";

		// 取父部门InnerCode
		if (!StringUtils.isEmpty(oldFatherDeptCode)) {
			innercode = (String) this.getBaseDAO().executeQuery(
					"select innercode from org_dept where code='" + oldFatherDeptCode + "'", new ColumnProcessor());
		}

		innercode += getRandomString(4);

		String strSQL = "select count(*) from org_dept where innercode = '" + innercode + "'";
		int count = (int) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());

		// 如果存在InnerCode，则递归生成
		if (count > 0) {
			innercode = getInnerCode(oldFatherDeptCode);
		}
		return innercode;
	}

	/**
	 * 保存后事件
	 * 
	 * @throws BusinessException
	 */
	public void afterUpdate() throws BusinessException {
		List<String> sqls = new ArrayList<String>();

		if (this.getNcValueObjects() != null && this.getNcValueObjects().size() > 0) {
			String rowNo = "";
			for (Map<String, Object> rowNCMap : this.getNcValueObjects()) {
				rowNo = rowNCMap.keySet().toArray(new String[0])[0].split(":")[0];
				String pk_dept = (String) rowNCMap.get(rowNo + ":pk_dept");

				String strSQL = "SELECT createdate, deptcanceldate, vno, vname, vstartdate, pk_dept, pk_vid FROM org_dept_v WHERE pk_dept="
						+ getStringValue(pk_dept) + " ORDER BY createdate;";
				List<Map> verList = (List<Map>) this.getBaseDAO().executeQuery(strSQL, new MapListProcessor());

				if (verList != null && verList.size() > 0) {
					for (int i = 0; i < verList.size(); i++) {
						Map<String, Object> valueMap = (Map<String, Object>) verList.get(i);
						String enddate = "";
						if (!StringUtils.isEmpty((String) valueMap.get("deptcanceldate"))) {
							enddate = (String) valueMap.get("deptcanceldate");
						} else {
							if (i != verList.size() - 1) {
								UFDate endD = new UFDate(getDateString((String) verList.get(i + 1).get("createdate")));
								enddate = getDateString(String.valueOf(endD.getYear())
										+ "-"
										+ (endD.getMonth() < 10 ? ("0" + String.valueOf(endD.getMonth())) : String
												.valueOf(endD.getMonth()))
										+ "-"
										+ (endD.getDay() < 10 ? ("0" + String.valueOf(endD.getDay())) : String
												.valueOf(endD.getDay())) + " 23:59:59");
							} else {
								enddate = "9999-12-31 23:59:59";
							}
						}
						strSQL = "UPDATE ORG_DEPT_V SET VSTARTDATE="
								+ getStringValue(getDateString((String) valueMap.get("createdate") + " 00:00:00"))
								+ ", VENDDATE="
								+ getStringValue(getDateString(enddate + " 23:59:59"))
								// + ", VNO="
								// + getStringValue(getDateString(
								// (String) valueMap.get("createdate"))
								// .replace("-", "").substring(0, 6))
								+ " WHERE pk_dept=" + getStringValue(valueMap.get("pk_dept")) + " AND pk_vid="
								+ getStringValue(valueMap.get("pk_vid")) + ";";

						sqls.add(strSQL);
					}
				}
			}
		}

		if (sqls.size() > 0) {
			this.getSaveService().executeQueryWithNoCMT(sqls.toArray(new String[0]));
		}
	}

	@Override
	public void beforeConvert() throws BusinessException {
		// 数据预处理：去掉值的首尾空格
		if (this.getJsonValueObjects() != null && this.getJsonValueObjects().size() > 0) {
			for (Map<String, Object> jsonobj : this.getJsonValueObjects()) {
				for (Entry<String, Object> entry : jsonobj.entrySet()) {
					jsonobj.put(entry.getKey(), ((String) entry.getValue()).trim());
				}
			}
		}
	}

	/**
	 * 转换后事件
	 * 
	 * @throws BusinessException
	 */
	public void afterConvert() throws BusinessException {
	}

	@Override
	public String getBizEntityID() {
		// 必須賦值，否則不會加載本類型
		// 實體ID為md_class的ID欄位
		return "46cb59ad-dae5-40b4-a9fe-3e9663fa95ac";
	}

	@Override
	public void beforeQuery() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void afterQuery() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void doUpdateByBP() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeInsertOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeUpdateOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void doQueryByBP() throws BusinessException {
		// TODO 自動產生的方法 Stub

	}

}
