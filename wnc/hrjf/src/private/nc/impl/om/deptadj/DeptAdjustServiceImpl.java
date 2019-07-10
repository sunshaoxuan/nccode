package nc.impl.om.deptadj;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.itf.om.IDeptAdjustService;
import nc.itf.om.IDeptManageService;
import nc.itf.om.IDeptQueryService;
import nc.itf.org.IOrgVersionManageService;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.logging.Debug;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.DeptChangeType;
import nc.vo.om.hrdept.DeptHistoryVO;
import nc.vo.om.hrdept.HRDeptAdjustVO;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.om.pub.SQLHelper;
import nc.vo.om.pub.SuperVOHelper;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.MultiLangText;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;

public class DeptAdjustServiceImpl implements IDeptAdjustService {

	/** 部门DAO */
	// private DeptDao deptDao = null;

	/** 部门查询接口 **/
	private IDeptQueryService deptQueryService = null;

	/** 部門業務接口 **/
	private IDeptManageService deptManageService = null;

	/** baseDao **/
	private BaseDAO baseDAO = null;

	/**
	 * 为新版本部门
	 */
	private static final String PK_VID_FOR_DEPT_VER = "VIRTUAL_PK_DEPT_V";

	// 進行部門版本化后,需要進行修復pk_dept的表名(這些表的pk_dept和pk_dept_v同在,hi_stapply除外)
	private static String[] NEED_FIX_TABLE_NAME = { "hi_psnjob", "om_deptadj", "tbm_leaveplan", "hi_stapply" };
	// 部門pk_dept的字段名
	private static String[] NEED_FIX_TABLE_COLUMN = { "pk_dept", "pk_dept", "pk_dept", "newpk_dept" };

	/** 部门QService **/
	private IDeptQueryService getDeptQueryService() {
		if (deptQueryService == null) {
			deptQueryService = NCLocator.getInstance().lookup(IDeptQueryService.class);
		}
		return deptQueryService;
	}

	/** 部门MService **/
	private IDeptManageService getDeptManageService() {
		if (deptManageService == null) {
			deptManageService = NCLocator.getInstance().lookup(IDeptManageService.class);
		}
		return deptManageService;
	}

	private BaseDAO getBaseDAO() {
		if (null == baseDAO) {
			baseDAO = new BaseDAO();
		}
		return baseDAO;
	}

	/*
	 * private DeptDao getDeptDao() { if (deptDao == null) { deptDao = new
	 * DeptDao(); } return deptDao; }
	 */

	/**
	 * 根据部门查询当前部门的版本PK
	 * 
	 * @param pk_dept
	 * @return 当前部门的版本PK 业务逻辑:查询当前部门的版本PK
	 */
	public String queryLastDeptByPk(String pk_dept) {
		String pk_dept_v = null;
		try {
			// ssx MOD 取當前日期生效部門版本 改為 取主檔上指定的最新版本
			// on 2019-06-28
			pk_dept_v = ((HRDeptVO) this.getBaseDAO().retrieveByPK(HRDeptVO.class, pk_dept)).getPk_vid();
			// end
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return pk_dept_v;
	}

	@Override
	public String executeDeptVersion(HRDeptAdjustVO[] results, UFLiteralDate date) throws BusinessException {
		StringBuilder errMsg = new StringBuilder();

		if (results == null || results.length == 0) {
			return errMsg.toString();
		}

		for (HRDeptAdjustVO vo : results) {
			if (vo == null) {
				continue;
			}
			String sqlStr = "";
			try {
				AggHRDeptVO saveVO = new AggHRDeptVO();
				HRDeptVO deptVO = HRDeptAdjust2HRDeptVO(vo);
				// 獲取當前的部門信息
				sqlStr = "select * from org_dept where pk_dept = '" + deptVO.getPk_dept() + "' ";
				HRDeptVO curVO = (HRDeptVO) getBaseDAO().executeQuery(sqlStr, new BeanProcessor(HRDeptVO.class));

				// MOD (PM25602：增加部門當前版本開始日期晚於等於生效日期時，不進行版本化)
				// added on 2019-03-28 by ssx
				if (curVO.getVstartdate() != null) {
					if (curVO.getVstartdate().toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE).isSameDate(date)
							|| curVO.getVstartdate().toUFLiteralDate(UFLiteralDate.BASE_TIMEZONE)
									.after(vo.getEffectivedate())) {
						errMsg.append("部門 [" + curVO.getCode() + "] 已存在易於申請單生效日期的版本，不能進行版本化處理。");
						continue;
					}
				}

				if (date.after(vo.getEffectivedate())) {
					date = vo.getEffectivedate();
				}
				// MOD END

				if (deptVO.getPk_vid() == null || deptVO.getPk_vid().equals("~") || deptVO.getPk_vid().equals("null")) {
					deptVO.setPk_vid(curVO.getPk_vid());
				}
				String oldPkDept = deptVO.getPk_dept();

				String newPkdeptV = curVO.getPk_vid();
				// 設置啟用
				deptVO.setEnablestate(2);
				deptVO.setIslastversion(new UFBoolean(true));
				saveVO.setParentVO(deptVO);

				// 处理撤销标志
				if (curVO != null && curVO.getHrcanceled() != null && vo.getHrcanceled() != null) {
					// 原来是撤销状态,且现在为非撤销状态,先取消撤銷
					if (curVO.getHrcanceled().booleanValue() && !vo.getHrcanceled().booleanValue()) {
						DeptHistoryVO historyVO = buildDeptHistoryVO4UnCancel(deptVO);
						// 反撤銷
						AggHRDeptVO[] uncanceledDepts = getDeptManageService().uncancel(saveVO, historyVO, false,
								false, true);
						// 部门版本化操作完善 王永文 20190502 begin
						if (uncanceledDepts != null && uncanceledDepts.length > 0) {
							saveVO = uncanceledDepts[0];
						}
						HRDeptVO parentVO = (HRDeptVO) saveVO.getParentVO();
						IOrgVersionManageService orgManageService = NCLocator.getInstance().lookup(
								IOrgVersionManageService.class);
						String vno = queryNextVersionNO("org_dept_v", "pk_dept", deptVO.getPk_dept(),
								String.valueOf(date.getYear()));
						DeptVO pvo = (DeptVO) getBaseDAO().retrieveByPK(DeptVO.class, parentVO.getPk_dept());
						if (pvo != null) {
							pvo.setOrgtype13(UFBoolean.TRUE);
							pvo.setOrgtype17(UFBoolean.FALSE);
						}
						/*
						 * 调用标准产品的版本化服务，但是标准产品要求版本化时必须修改名称，
						 * 而我们的需求是支持不修改名称的，解决的方案是先把名称修改，再把名称update回来
						 */
						pvo.setName(pvo.getName() + "@@@@TWNC");
						if (pvo.getName2() != null)
							pvo.setName2(pvo.getName2() + "@@@@TWNC");
						if (pvo.getName3() != null)
							pvo.setName3(pvo.getName3() + "@@@@TWNC");
						// 调用标准产品的标准版本化操作
						DeptVO hrDptNewVerVO = (DeptVO) orgManageService.createNewVersionVO("nc.vo.org.DeptVO", pvo,
								"版本更新" + date.getYear() + vno, date.getYear() + vno, new UFDate(date.toString()
										+ " 00:00:00"), new UFDate(date.getDateBefore(1).toString() + " 23:59:59"));
						if (hrDptNewVerVO == null) {
							continue;
						}
						// 把名称再给update回来,同时把部门表和部门版本表的负责人、副主管、所在地点更新了
						updateOrgName(vo, hrDptNewVerVO.getPk_vid());
						/*
						 * AggHRDeptVO[] newVOs =
						 * getDeptManageService().createDeptVersion
						 * (uncanceledDepts, new UFDate(date.toDate()));
						 */
						// HR部门更新
						if (hrDptNewVerVO != null) {
							AggHRDeptVO newVO = new AggHRDeptVO();
							HRDeptVO hrdeptvo = (HRDeptVO) getBaseDAO().retrieveByPK(HRDeptVO.class,
									hrDptNewVerVO.getPk_dept());
							hrdeptvo.setFatherDeptChanged(parentVO.isFatherDeptChanged());
							hrdeptvo.setApprovedept(parentVO.getApprovedept());
							hrdeptvo.setApprovenum(parentVO.getApprovenum());
							hrdeptvo.setDeptduty(parentVO.getDeptduty());
							hrdeptvo.setManagescope(parentVO.isManagescope());
							newVO.setParentVO(hrdeptvo);
							saveVO = getDeptManageService().update(newVO, false);
						}
						// 部门版本化操作完善 王永文 20190502 end
						// 回写标志:
						sqlStr = "update om_deptadj set iseffective = 'Y' where pk_deptadj = '" + vo.getPk_deptadj()
								+ "'";
						getBaseDAO().executeUpdate(sqlStr);
						newPkdeptV = ((HRDeptVO) saveVO.getParentVO()).getPk_vid();
						sqlStr = "update  hi_psnjob set pk_dept_v = '" + newPkdeptV + "'  where begindate <= '"
								+ date.toStdString() + "' and isnull(enddate,'9999-12-31') > '" + date.toStdString()
								+ "' and  pk_dept = '" + oldPkDept + "'";
						getBaseDAO().executeUpdate(sqlStr);
						continue;
					}
				}

				if (vo.getPk_dept_v() != null && vo.getPk_dept_v().equals(PK_VID_FOR_DEPT_VER)) {
					// 如果是新增,那麼先刪除主表上的檔案信息
					getBaseDAO().deleteVO(deptVO);
					// 執行新增標準平台新增邏輯
					AggHRDeptVO rtnVO = getDeptManageService().insert(saveVO);
					String pk_dept = null;
					if (rtnVO.getParentVO() != null) {
						pk_dept = rtnVO.getParentVO().getPrimaryKey();
					}
					// 替換所有原先引用的值
					dataFix(oldPkDept, pk_dept);
				} else {
					DeptHistoryVO historyVO = buildDeptHistoryVO4Update(deptVO, date);
					// 部门版本化操作完善 王永文 20190502 begin
					HRDeptVO parentVO = (HRDeptVO) saveVO.getParentVO();
					IOrgVersionManageService orgManageService = NCLocator.getInstance().lookup(
							IOrgVersionManageService.class);
					String vno = queryNextVersionNO("org_dept_v", "pk_dept", deptVO.getPk_dept(),
							String.valueOf(date.getYear()));
					DeptVO pvo = (DeptVO) new BaseDAO().retrieveByPK(DeptVO.class, parentVO.getPk_dept());
					/*
					 * 调用标准产品的版本化服务，但是标准产品要求版本化时必须修改名称，
					 * 而我们的需求是支持不修改名称的，解决的方案是先把名称修改，再把名称update回来
					 */
					pvo.setName(pvo.getName() + "@@@@TWNC");
					if (pvo.getName2() != null)
						pvo.setName2(pvo.getName2() + "@@@@TWNC");
					if (pvo.getName3() != null)
						pvo.setName3(pvo.getName3() + "@@@@TWNC");
					// 调用标准产品的标准版本化操作
					DeptVO hrDptNewVerVO = (DeptVO) orgManageService.createNewVersionVO("nc.vo.org.DeptVO", pvo, "版本更新"
							+ date.getYear() + vno, date.getYear() + vno, new UFDate(date.toString() + " 00:00:00"),
							new UFDate(date.getDateBefore(1).toString() + " 23:59:59"));
					if (hrDptNewVerVO == null) {
						continue;
					}
					// 把名称再给update回来,同时把部门表和部门版本表的负责人、副主管、所在地点更新了
					updateOrgName(vo, hrDptNewVerVO.getPk_vid());
					AggHRDeptVO newVO = new AggHRDeptVO();
					parentVO = (HRDeptVO) new BaseDAO().retrieveByPK(HRDeptVO.class, hrDptNewVerVO.getPk_dept());
					newVO.setParentVO(parentVO);
					// 如果是修改,直接修改所有信息
					getBaseDAO().updateVO((HRDeptVO) newVO.getParentVO());
					newPkdeptV = ((HRDeptVO) newVO.getParentVO()).getPk_vid();
					newVO.setParentVO((HRDeptVO) getBaseDAO().retrieveByPK(HRDeptVO.class,
							newVO.getParentVO().getPrimaryKey()));
					// 插入部门变更历史数据
					getBaseDAO().insertVO(historyVO);
					// 如果部門代碼變更或部門名稱變更,則新增人員任職記錄，
					int renameAndPrincipalChangeFlag = 0;
					if (vo.getCode() != null && vo.getName() != null) {
						if (!vo.getCode().equals(curVO.getCode()) || !vo.getName().equals(curVO.getName())) {
							// 變更了名稱
							renameAndPrincipalChangeFlag = 1;
						}
					}
					if ((vo.getPrincipal() == null && curVO.getCode() != null)
							|| (vo.getPrincipal() != null && curVO.getPrincipal() == null)
							|| (vo.getPrincipal() != null && curVO.getPrincipal() != null && !vo.getPrincipal().equals(
									curVO.getPrincipal()))) {
						// 變更了負責人
						if (0 == renameAndPrincipalChangeFlag) {
							historyVO.setChangetype(DeptChangeType.CHANGEPRINCIPAL);
							renameAndPrincipalChangeFlag = 2;
						} else {
							renameAndPrincipalChangeFlag = 3;
						}
					}
					// if (renameAndPrincipalChangeFlag != 0) {
					/*
					 * getDeptManageService().renameAndPrincipalChange(newVO,
					 * historyVO, true, renameAndPrincipalChangeFlag);
					 */
					newPkdeptV = ((HRDeptVO) newVO.getParentVO()).getPk_vid();
					// }
					// 部门版本化操作完善 王永文 20190502 end
				}
				// 回写标志:
				sqlStr = "update om_deptadj set iseffective = 'Y' where pk_deptadj = '" + vo.getPk_deptadj() + "'";
				getBaseDAO().executeUpdate(sqlStr);
				sqlStr = "update  hi_psnjob set pk_dept_v = '" + newPkdeptV + "'  where begindate <= '"
						+ date.toStdString() + "' and isnull(enddate,'9999-12-31') > '" + date.toStdString()
						+ "' and  pk_dept = '" + oldPkDept + "'";
				getBaseDAO().executeUpdate(sqlStr);
			} catch (Exception e) {
				errMsg.append("部門版本化發生錯誤: " + e.getMessage());
				continue;
			}
		}

		return errMsg.toString();
	}

	private String queryNextVersionNO(String table, String pkField, String pkValue, String cyear)
			throws BusinessException {
		StringBuilder querySQL = new StringBuilder();
		querySQL.append("select ");
		querySQL.append("max(right(vno, 2)) ");
		querySQL.append("from ");
		querySQL.append(table);
		querySQL.append(" where ");
		querySQL.append(SQLHelper.genSQLEqualClause(pkField, pkValue));
		querySQL.append(" and ");

		querySQL.append(SQLHelper.genSQLEqualClause("LEFT(vno, 4)", cyear));

		String maxNO = null;

		ColumnProcessor processor = new ColumnProcessor();
		Object object = getBaseDAO().executeQuery(querySQL.toString(), processor);
		if (object != null)
			maxNO = object.toString();
		if (maxNO == null)
			maxNO = "00";
		String nextNO = String.valueOf(Integer.parseInt(maxNO) + 1);
		while (nextNO.length() < 2) {
			nextNO = "0" + nextNO;
		}
		return nextNO;
	}

	/**
	 * 执行部门版本化（後台任務：先新增再修改）
	 * 
	 * @param date
	 * @throws BusinessException
	 *             业务逻辑:执行生效日期为此日期的所有部门版本化单据,
	 *             1.将此日期的单据vo上存储的字段信息,存储到org_dept,org_dept_v
	 *             ,org_orgs,org_orgs_v,org_reportorg.并且更新关联的十几张表(参考新增和修改逻辑).
	 *             2.部门编码和部门名称更改时, 新增人員任職記錄( 已經結束的任職記錄不更新)((参考新增和修改逻辑))
	 *             3.人员履历记录都要增加
	 *             (参考新增和修改逻辑)4.islastversion打上最新标志,该部门其它记录改为false(参考新增和修改逻辑)
	 *             4.iseffective打上执行标记.iseffective为false的不执行. (5.其它参考新增和修改逻辑)
	 *             6.修改有動撤銷標記時,調用撤銷/取消撤銷邏輯
	 */
	@SuppressWarnings("unchecked")
	public String executeDeptVersion(UFLiteralDate date) throws BusinessException {

		// 查询该生效日期的所有单据
		// MOD(PM25602：修改為早於等於當前日期的所有未執行單據)
		// added on 2019-03-28 by ssx
		String sqlStr = "select * from om_deptadj where effectivedate <= '" + date.toStdString()
				+ "' and isnull(iseffective, 'N') = 'N' and dr = 0 order by effectivedate";
		// MOD END
		List<HRDeptAdjustVO> needExecuteVOs = (List<HRDeptAdjustVO>) getBaseDAO().executeQuery(sqlStr,
				new BeanListProcessor(HRDeptAdjustVO.class));
		return executeDeptVersion(needExecuteVOs.toArray(new HRDeptAdjustVO[0]), date);
	}

	/**
	 * 更新组织名称、报表组织名称、部门名称，及对应的版本名称 增加更新负责人、副主管、所在地点
	 * 
	 * @param vo
	 * @throws DAOException
	 */
	private void updateOrgName(HRDeptAdjustVO vo, String vidPK) throws DAOException {
		String name = vo.getName();
		String name2 = vo.getName2();
		String name3 = vo.getName3();
		String name4 = vo.getName4();
		String name5 = vo.getName5();
		String name6 = vo.getName6();
		// 更新部门表,更新负责人、副主管、所在地点
		String sql_dept = "update org_dept set code='" + vo.getCode() + "',name = " + getNmF(name) + ", name2 = "
				+ getNmF(name2) + ",name3=" + getNmF(name3) + ",name4=" + getNmF(name4) + ",name5=" + getNmF(name5)
				+ ",name6=" + getNmF(name6) + ",PRINCIPAL = '" + vo.getPrincipal() + "',GLBDEF11 = '"
				+ vo.getGlbdef11() + "', GLBDEF3 = '" + vo.getGlbdef3() + "' where pk_dept = '" + vo.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_dept);
		// 更新部门版本表,更新负责人、副主管、所在地点
		String sql_dept_v = "update org_dept_v set code='" + vo.getCode() + "', name=" + getNmF(name) + ", name2 = "
				+ getNmF(name2) + ",name3=" + getNmF(name3) + ",name4=" + getNmF(name4) + ",name5=" + getNmF(name5)
				+ ",name6=" + getNmF(name6) + ",PRINCIPAL = '" + vo.getPrincipal() + "',GLBDEF11 = '"
				+ vo.getGlbdef11() + "', GLBDEF3 = '" + vo.getGlbdef3() + "' where pk_vid = '" + vidPK + "'";
		getBaseDAO().executeUpdate(sql_dept_v);
		// 更新报表组织表
		String sql_report = "update org_reportorg set name=" + getNmF(name) + ", name2 = " + getNmF(name2) + ",name3="
				+ getNmF(name3) + ",name4=" + getNmF(name4) + ",name5=" + getNmF(name5) + ",name6=" + getNmF(name6)
				+ " where pk_reportorg = '" + vo.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_report);
		// 更新报表组织版本表
		String sql_report_v = "update org_reportorg_v set name=" + getNmF(name) + ", name2 = " + getNmF(name2)
				+ ",name3=" + getNmF(name3) + ",name4=" + getNmF(name4) + ",name5=" + getNmF(name5) + ",name6="
				+ getNmF(name6) + " where pk_vid = '" + vidPK + "'";
		getBaseDAO().executeUpdate(sql_report_v);
		// 更新组织表
		String sql_org = "update org_orgs set code='" + vo.getCode() + "', name=" + getNmF(name) + ", name2 = "
				+ getNmF(name2) + ",name3=" + getNmF(name3) + ",name4=" + getNmF(name4) + ",name5=" + getNmF(name5)
				+ ",name6=" + getNmF(name6) + " where pk_org = '" + vo.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_org);
	}

	private String getNmF(String name) {
		if (name == null || name.trim().length() == 0) {
			return null;
		}
		return "'" + name + "'";
	}

	/**
	 * 主鍵修復,替換原有主鍵到新的主鍵
	 * 
	 * @param pk_dept
	 * @param pk_dept2
	 * @throws BusinessException
	 */
	private void dataFix(String old_pk_dept, String new_pk_dept) throws BusinessException {
		if (null == old_pk_dept || null == new_pk_dept || new_pk_dept.equals(old_pk_dept)) {
			return;
		}
		// 將新PK替換舊PK
		for (int i = 0; i < NEED_FIX_TABLE_NAME.length; i++) {
			String sqlStr = "update " + NEED_FIX_TABLE_NAME[i] + " set " + NEED_FIX_TABLE_COLUMN[i] + " = '"
					+ new_pk_dept + "' " + " where " + NEED_FIX_TABLE_COLUMN[i] + " = '" + old_pk_dept + "' and dr = 0";
			getBaseDAO().executeUpdate(sqlStr);
		}
	}

	/*
	 * 查询是否已存在指定部門未生效的調整申請單 pk_dept 返回UFBoolean True存在，False不存在
	 * 业务逻辑:查询是否已存在指定部門未生效的調整申請單即存在生效日期大于当前日期的此部门的调整单. 本身除外
	 */
	public UFBoolean isExistDeptAdj(String pk_dept, String pk_deptadj) throws BusinessException {
		String sqlStr = null;
		if (pk_deptadj != null) {
			sqlStr = "select count(*) from om_deptadj where pk_dept ='" + pk_dept + "' and effectivedate > '"
					+ new UFDate().toStdString() + "' and pk_deptadj <> '" + pk_deptadj
					+ "' and dr = 0 and isnull(iseffective,'N') = 'N'";
		} else {
			sqlStr = "select count(*) from om_deptadj where pk_dept ='" + pk_dept + "' and effectivedate > '"
					+ new UFDate().toStdString() + "' and dr = 0 and isnull(iseffective,'N') = 'N'";
		}

		Integer result = getIntegerDataBySQL(sqlStr);
		if (null != result && result > 0) {
			return new UFBoolean(false);
		}
		return new UFBoolean(true);
	}

	/**
	 * 单据校验服務1--部门单据唯一性(nc.impl.pubapp.pattern.rule.IRule<BatchOperateVO>)
	 * 
	 * @param vo
	 * @throws BusinessException
	 *             业务逻辑: 1. 同一部門只能有一張生效日期大于当前日期單據(后台服务)
	 */
	public UFBoolean validateDept(HRDeptAdjustVO vo) throws BusinessException {
		if (null == vo) {
			throw new BusinessException("無效記錄!");
		}
		if (null == vo.getPk_dept()) {
			throw new BusinessException("部門主鍵不得為空!");
		}
		if (null == vo.getEffectivedate()) {
			throw new BusinessException("生效日期不得為空");
		}
		if (!isExistDeptAdj(vo.getPk_dept(), vo.getPk_deptadj()).booleanValue()) {
			throw new BusinessException("該部門已經存在未到生效日期的申請單");
		}
		return new UFBoolean(true);
	}

	/**
	 * 部门信息新增回写
	 * 
	 * @param deptVO
	 * @throws BusinessException
	 *             业务逻辑:部门新增时,如果成立日期大于当前日期, 那么回写一笔单据到本节点,
	 *             将deptVO赋值到HRDeptAdjustVO上的字段,申请人填写当前用户,
	 *             申请日期填写当前日期,生效日期为部门的成立日期,调整部门为VO上的部门.
	 *             如果该部门PK而且生效日期已经存在,那么不能在进行回写,同时在保存部门的时候抛出异常.
	 */
	public AggHRDeptVO writeBack4DeptAdd(AggHRDeptVO aggDeptVO) throws BusinessException {
		if (aggDeptVO == null || aggDeptVO.getParentVO() == null) {
			return null;
		}
		HRDeptVO deptVO = (HRDeptVO) aggDeptVO.getParentVO();
		// 設置狀態為未啟用
		deptVO.setEnablestate(1);
		// 处理日期大于当前日期才可以回写
		if (null != deptVO && deptVO.getCreatedate() != null && isAfterToday(deptVO.getCreatedate())) {
			// 设置PK_VID的值不为null才能存储
			deptVO.setPk_vid(PK_VID_FOR_DEPT_VER);
			deptVO.setCreationtime(new UFDateTime());
			deptVO.setDr(0);
			// 在org_dept上新增一條主檔,其他什麼都不幹
			getBaseDAO().insertVO(deptVO);
			// 在新節點上新增記錄
			HRDeptAdjustVO saveVO = HRDeptVO2HRDeptAdjust(deptVO);
			saveVO.setEffectivedate(deptVO.getCreatedate());
			saveVO.setBilldate(new UFDate());
			saveVO.setDr(0);
			saveVO.setDisplayorder(999999);
			saveVO.setIseffective(new UFBoolean(false));
			saveVO.setCreationtime(new UFDateTime());
			saveVO.setAdj_code(String.valueOf(System.currentTimeMillis()));
			saveVO.setPk_dept_v(PK_VID_FOR_DEPT_VER);
			validateDept(saveVO);
			getBaseDAO().insertVO(saveVO);
		}
		aggDeptVO.setParentVO(deptVO);
		return aggDeptVO;
	}

	/**
	 * 部门信息新增回写
	 * 
	 * @param deptVO
	 * @throws BusinessException
	 *             业务逻辑:部门新增时,如果成立日期大于当前日期, 那么回写一笔单据到本节点,
	 *             将deptVO赋值到HRDeptAdjustVO上的字段,申请人填写当前用户,
	 *             申请日期填写当前日期,生效日期为部门的成立日期,调整部门为VO上的部门.
	 *             如果该部门PK而且生效日期已经存在,那么不能在进行回写,同时在保存部门的时候抛出异常.
	 */
	public AggHRDeptVO writeBack4DeptUnCancel(AggHRDeptVO aggDeptVO, UFLiteralDate effective) throws BusinessException {
		if (aggDeptVO == null || aggDeptVO.getParentVO() == null) {
			return null;
		}
		if (effective == null) {
			throw new BusinessException("請填入生效日期!");
		}

		HRDeptVO deptVO = (HRDeptVO) aggDeptVO.getParentVO();
		// 設置狀態為未啟用
		deptVO.setEnablestate(1);
		// 处理日期大于当前日期才可以回写
		if (effective != null && isAfterToday(effective)) {
			// 在新節點上新增記錄
			HRDeptAdjustVO saveVO = HRDeptVO2HRDeptAdjust(deptVO);
			saveVO.setEffectivedate(effective);
			saveVO.setBilldate(new UFDate());
			saveVO.setDr(0);
			saveVO.setDisplayorder(deptVO.getDisplayorder());
			saveVO.setIseffective(new UFBoolean(false));
			saveVO.setCreationtime(new UFDateTime());
			saveVO.setAdj_code(String.valueOf(System.currentTimeMillis()));
			saveVO.setPk_dept_v(deptVO.getPk_vid());
			// 取消撤銷標誌
			saveVO.setHrcanceled(UFBoolean.FALSE);
			validateDept(saveVO);
			getBaseDAO().insertVO(saveVO);
		}
		aggDeptVO.setParentVO(deptVO);
		return aggDeptVO;
	}

	/**
	 * pk_org 人力资源组织PK
	 * 
	 * @param pk_org
	 * @returnList<HRDeptVO>
	 * @throws BusinessException
	 * 
	 *             在部门信息节点,需要过滤出还未生效的部门,在此节点查询出此人力资源组织下,
	 *             所有生效日期在当前日期之后的新增并且未生效的部门,,
	 *             封装成HRDeptVO的list返回,供部门信息节点查询未生效部门使用
	 */
	@SuppressWarnings("unchecked")
	public List<HRDeptVO> queryOFutureDept(String pk_org, String whereSql) throws BusinessException {
		if (whereSql == null) {
			whereSql = " 1 = 1 ";
		}
		String sqlStr = " select * from org_dept where pk_org = '" + pk_org + "' and  createdate > '"
				+ (new UFDate()).toStdString() + "' and " + whereSql;

		List<HRDeptVO> result = (List<HRDeptVO>) getBaseDAO().executeQuery(sqlStr,
				new BeanListProcessor(HRDeptVO.class));
		if (result == null) {
			result = new ArrayList<>();
		}
		return result;
	}

	/**
	 * 
	 * @param HRDeptVO
	 * @return
	 * @throws BusinessException
	 *             业务逻辑:在部门节点查询未生效的部门,以及在进行后台任务回写时 ,需要将部门VO转换成单据VO
	 */
	public HRDeptVO HRDeptAdjust2HRDeptVO(HRDeptAdjustVO vo) throws BusinessException {
		// 先查找当前的部门信息
		String sqlStr = "select * from org_dept where pk_dept = '" + vo.getPk_dept() + "'";
		HRDeptVO resultVO = (HRDeptVO) getBaseDAO().executeQuery(sqlStr, new BeanProcessor(HRDeptVO.class));
		if (null == resultVO) {
			resultVO = new HRDeptVO();
		}
		// 将修改信息覆盖到当前信息里面
		if (null != vo) {
			resultVO.setPk_vid(vo.getPk_dept_v());
			resultVO.setPk_dept(vo.getPk_dept());
			resultVO.setCode(vo.getCode());
			resultVO.setName(vo.getName());
			// ssx added on 2019-05-02
			// for MultiLangText
			resultVO.setName2(vo.getName2());
			resultVO.setName3(vo.getName3());
			resultVO.setName4(vo.getName4());
			resultVO.setName5(vo.getName5());
			resultVO.setName6(vo.getName6());
			// end
			resultVO.setInnercode(vo.getInnercode());
			resultVO.setPk_fatherorg(vo.getPk_fatherorg());
			resultVO.setPk_group(vo.getPk_group());
			resultVO.setPk_org(vo.getPk_org());
			resultVO.setDepttype(vo.getDepttype());
			resultVO.setDeptlevel(vo.getDeptlevel());
			resultVO.setDeptduty(vo.getDeptduty());
			resultVO.setCreatedate(vo.getCreatedate());
			resultVO.setShortname(vo.getShortname());
			// ssx added on 2019-05-02
			// for MultiLangText
			resultVO.setShortname2(vo.getShortname2());
			resultVO.setShortname3(vo.getShortname3());
			resultVO.setShortname4(vo.getShortname4());
			resultVO.setShortname5(vo.getShortname5());
			resultVO.setShortname6(vo.getShortname6());
			// end
			resultVO.setMnecode(vo.getMnecode());
			resultVO.setHrcanceled(vo.getHrcanceled());
			resultVO.setDeptcanceldate(vo.getDeptcanceldate());
			resultVO.setEnablestate(vo.getEnablestate());
			resultVO.setDisplayorder(vo.getDisplayorder());
			resultVO.setDataoriginflag(vo.getDataoriginflag());
			resultVO.setOrgtype13(vo.getOrgtype13());
			resultVO.setOrgtype17(vo.getOrgtype17());
			resultVO.setPrincipal(vo.getPrincipal());
			resultVO.setTel(vo.getTel());
			resultVO.setAddress(vo.getAddress());
			resultVO.setMemo(vo.getMemo());
			resultVO.setIslastversion(vo.getIslastversion());
			resultVO.setCreator(vo.getCreator());
			resultVO.setCreationtime(vo.getCreationtime());
			resultVO.setModifiedtime(vo.getModifiedtime());
			resultVO.setModifier(vo.getModifier());
			// 增加副主管和工作地点 王永文 20190503 begin
			resultVO.setAttributeValue("glbdef3", vo.getGlbdef3());
			resultVO.setAttributeValue("glbdef11", vo.getGlbdef11());
			// 增加副主管和工作地点 王永文 20190503 end
		}
		return resultVO;
	}

	/**
	 * 
	 * @param HRDeptVO
	 * @return
	 * @throws BusinessException
	 *             业务逻辑:在部门节点查询未生效的部门,以及在进行后台任务回写时 ,需要将部门VO转换成单据VO
	 */
	public HRDeptAdjustVO HRDeptVO2HRDeptAdjust(HRDeptVO vo) throws BusinessException {
		HRDeptAdjustVO resultVO = new HRDeptAdjustVO();
		if (null != vo) {
			resultVO.setPk_dept_v(null);
			resultVO.setPk_dept(vo.getPk_dept());
			resultVO.setCode(vo.getCode());
			resultVO.setName(vo.getName());
			resultVO.setInnercode(vo.getInnercode());
			resultVO.setPk_fatherorg(vo.getPk_fatherorg());
			resultVO.setPk_group(vo.getPk_group());
			resultVO.setPk_org(vo.getPk_org());
			resultVO.setDepttype(vo.getDepttype());
			resultVO.setDeptlevel(vo.getDeptlevel());
			resultVO.setDeptduty(vo.getDeptduty());
			resultVO.setCreatedate(vo.getCreatedate());
			resultVO.setShortname(vo.getShortname());
			resultVO.setMnecode(vo.getMnecode());
			resultVO.setHrcanceled(vo.getHrcanceled());
			resultVO.setDeptcanceldate(vo.getDeptcanceldate());
			resultVO.setEnablestate(vo.getEnablestate());
			resultVO.setDisplayorder(vo.getDisplayorder());
			resultVO.setDataoriginflag(vo.getDataoriginflag());
			resultVO.setOrgtype13(vo.getOrgtype13());
			resultVO.setOrgtype17(vo.getOrgtype17());
			resultVO.setPrincipal(vo.getPrincipal());
			resultVO.setTel(vo.getTel());
			resultVO.setAddress(vo.getAddress());
			resultVO.setMemo(vo.getMemo());
			resultVO.setIslastversion(new UFBoolean(true));
			resultVO.setCreator(vo.getCreator());
			resultVO.setCreationtime(vo.getCreationtime());
			resultVO.setModifiedtime(vo.getModifiedtime());
			resultVO.setModifier(vo.getModifier());
			// 获取组织版本
			String sqlStr = "select pk_vid from org_orgs where pk_org = '" + vo.getPk_org() + "' ";
			Object pkvid = getSingleDataBySQL(sqlStr);
			if (null != pkvid) {
				resultVO.setPk_org_v(String.valueOf(pkvid));
			}
		}
		return resultVO;
	}

	/**
	 * 自动任务--部门撤销 检查校验规则2,才能进行删除,删除时联动删除部门信息的主档 部门撤销和取消撤销暂时用标准功能 撤销部门暂时不进行版本化操作
	 * 
	 * @param date
	 * @throws BusinessException
	 */
	public void executeDeptCancel(UFLiteralDate date) throws BusinessException {
		return;
	}

	/**
	 * void validatePsn(HRDeptAdjustVO vo) throws BusinessException;
	 * 
	 * @param vo
	 * @throws BusinessException
	 *             业务逻辑: 2. 校验人员调配申请生效日期是否在部门生效日期之后
	 */
	public UFBoolean validatePsn(HRDeptAdjustVO vo) throws BusinessException {
		if (null == vo) {
			throw new BusinessException("無效記錄!");
		}
		if (null == vo.getPk_dept()) {
			throw new BusinessException("部門主鍵不得為空!");
		}
		if (null == vo.getEffectivedate()) {
			throw new BusinessException("生效日期不得為空");
		}
		// 搜索是否存在在部門成立之前的人员调用记录
		String sqlStr = "select count(*) from hi_stapply where  newpk_dept = '" + vo.getPk_dept()
				+ "'and  EFFECTDATE > '" + vo.getEffectivedate().toStdString() + "' ";
		Integer rtn = getIntegerDataBySQL(sqlStr);
		if (null != rtn && rtn > 0) {
			throw new BusinessException("無效日期!該日期之前已經存在人員調配申請!請選擇稍早的日期.");
		}
		return new UFBoolean(true);
	}

	/**
	 * 从数据库获取一个值
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unused")
	private Object getSingleDataBySQL(String sql) throws DAOException {
		return (Object) getBaseDAO().executeQuery(sql, new ColumnProcessor());
	}

	/**
	 * 从数据库获取一个整数
	 * 
	 * @return 如果为null 则返回 null
	 * @throws DAOException
	 */
	private Integer getIntegerDataBySQL(String sql) throws DAOException {
		Object result = getBaseDAO().executeQuery(sql, new ColumnProcessor());
		Integer rtn = null;
		try {
			rtn = Integer.parseInt(String.valueOf(result));
		} catch (Exception e) {
			Debug.debug(e.getMessage());
		}
		return rtn;
	}

	/**
	 * 只允许删除执行标志未打勾的,并且生效日期在当前日期之后的单据. 删除时,'调配申请'中存在已引用未来新增部门的记录时，删除该新增部门时报错;
	 * 已经有下级部门时,删除报错
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public UFBoolean validateDel(HRDeptAdjustVO vo) throws BusinessException {
		if (null == vo) {
			throw new BusinessException("單據異常!無法判斷當前狀態!");
		}
		// 未打勾的,并且生效日期在当前日期之后的
		if (isAfterToday(vo.getEffectivedate()) && vo.getIseffective() != null && !vo.getIseffective().booleanValue()) {
			// 查詢調配記錄中,是否有有引用此部門的單據
			String sqlStr = "select count(*) from hi_stapply where newpk_dept ='" + vo.getPk_dept() + "' and dr = 0";
			Integer num = getIntegerDataBySQL(sqlStr);
			if (num != null && num > 0) {
				throw new BusinessException("該部門還有未處理的人員調配申請,請取消調配后再刪除!");
			}
			// 已经有下级部门时,删除报错
			sqlStr = "select count(*) from om_deptadj where pk_fatherorg = '" + vo.getPk_dept() + "' and dr = 0";
			num = getIntegerDataBySQL(sqlStr);
			if (num != null && num > 0) {
				throw new BusinessException("該部門已存在下級部門,無法刪除!");
			}
		} else {
			throw new BusinessException("單據已經被執行或者已過當前日期,不能刪除!");
		}
		return new UFBoolean(false);
	}

	/**
	 * 判斷日期是否在當前日期之後
	 * 
	 * @param date
	 * @return
	 */
	private boolean isAfterToday(UFLiteralDate date) {
		// 获取明天日期的简便方法:Thread.sleep(24*60*60*1000); Date date = new Date();
		if (null != date) {
			// UFLiteralDate tomorrow = new UFLiteralDate().getDateAfter(1);
			return date.after(new UFLiteralDate());
		}
		return false;
	}

	/**
	 * 获得部门变更历史<br>
	 * 更新用
	 * 
	 * @param effDate
	 * 
	 * @return
	 */
	private DeptHistoryVO buildDeptHistoryVO4Update(HRDeptVO vo, UFLiteralDate effDate) {
		DeptHistoryVO deptHistoryVO = new DeptHistoryVO();
		// 批准单位
		deptHistoryVO.setApprovedept(null);
		// 批准文号
		deptHistoryVO.setApprovenum(null);
		// 新部门名
		MultiLangText multiLangText = new MultiLangText();
		multiLangText.setText(vo.getName());
		multiLangText.setText2(vo.getName2());
		multiLangText.setText3(vo.getName3());
		multiLangText.setText4(vo.getName4());
		multiLangText.setText5(vo.getName5());
		multiLangText.setText6(vo.getName6());
		SuperVOHelper.copyMultiLangAttribute(multiLangText, deptHistoryVO);
		// 编码
		deptHistoryVO.setCode(vo.getCode());
		// 部门级别
		deptHistoryVO.setDeptlevel(vo.getDeptlevel());
		// 负责人
		deptHistoryVO.setPrincipal(vo.getPrincipal());
		// 生效日期--默認是今天
		// ssx modified on 2019-05-02
		// 由於可以進行前置日期的單據錄入，所以默認不能是今天，而是發生變更的日期
		deptHistoryVO.setEffectdate(effDate);
		// --end
		// 备注
		deptHistoryVO.setMemo("自動版本化");
		// 变更类型-更名
		deptHistoryVO.setChangetype(DeptChangeType.RENAME);
		// 部门主键
		deptHistoryVO.setPk_dept(vo.getPk_dept());
		// 组织主键
		deptHistoryVO.setPk_org(vo.getPk_org());

		return deptHistoryVO;
	}

	/**
	 * 获得部门变更历史<br>
	 * 取消撤銷用
	 * 
	 * @return
	 */
	private DeptHistoryVO buildDeptHistoryVO4UnCancel(HRDeptVO vo) {
		DeptHistoryVO deptHistoryVO = new DeptHistoryVO();
		// 批准单位
		deptHistoryVO.setApprovedept(null);
		// 批准文号
		deptHistoryVO.setApprovenum(null);
		// 编码
		deptHistoryVO.setCode(vo.getCode());
		// 部门名
		SuperVOHelper.copyMultiLangAttribute(vo, deptHistoryVO);
		// 部门级别
		deptHistoryVO.setDeptlevel(vo.getDeptlevel());
		// 负责人
		deptHistoryVO.setPrincipal(vo.getPrincipal());
		// 生效日期
		deptHistoryVO.setEffectdate(new UFLiteralDate());
		// 备注
		deptHistoryVO.setMemo(null);
		// 变更类型-反撤销
		deptHistoryVO.setChangetype(DeptChangeType.HRUNCANCELED);
		// 部门主键
		deptHistoryVO.setPk_dept(vo.getPk_dept());
		// 组织主键
		deptHistoryVO.setPk_org(vo.getPk_org());
		// 接收部门
		deptHistoryVO.setIsreceived(UFBoolean.FALSE);

		return deptHistoryVO;
	}

	@Override
	public String getNewDeptVerPK() {
		return PK_VID_FOR_DEPT_VER;
	}

	@Override
	public void executeDeptVersion(AggHRDeptVO saveVO, UFLiteralDate date) throws BusinessException {
		if (null == saveVO) {
			return;
		}
		if (null == saveVO.getParentVO()) {
			throw new BusinessException("部門主檔不能為空!");
		}
		HRDeptVO deptVO = (HRDeptVO) saveVO.getParentVO();
		deptVO.setName(deptVO.getName() == null ? null : deptVO.getName().replaceAll("'", ""));
		deptVO.setName2(deptVO.getName2() == null ? null : deptVO.getName2().replaceAll("'", ""));
		deptVO.setName3(deptVO.getName3() == null ? null : deptVO.getName3().replaceAll("'", ""));
		// 獲取當前的部門信息
		String sqlStr = "select * from org_dept where pk_dept = '" + deptVO.getPk_dept() + "' ";
		HRDeptVO curVO = (HRDeptVO) getBaseDAO().executeQuery(sqlStr, new BeanProcessor(HRDeptVO.class));
		String newPkdeptV = curVO.getPk_vid();

		if (deptVO.getPk_vid() == null || deptVO.getPk_vid().equals("~") || deptVO.getPk_vid().equals("null")) {
			deptVO.setPk_vid(curVO.getPk_vid());
		}
		// 設置啟用
		deptVO.setEnablestate(2);
		deptVO.setIslastversion(new UFBoolean(true));
		saveVO.setParentVO(deptVO);
		// 处理撤销标志
		if (curVO != null && curVO.getHrcanceled() != null && deptVO.getHrcanceled() != null) {
			// 原来是撤销状态,且现在为非撤销状态,先取消撤銷
			if (curVO.getHrcanceled().booleanValue() && !deptVO.getHrcanceled().booleanValue()) {
				DeptHistoryVO historyVO = buildDeptHistoryVO4ImportUnCancel(deptVO, date);
				// 反撤銷
				AggHRDeptVO[] uncanceledDepts = getDeptManageService().uncancel(saveVO, historyVO, false, false, true);
				// /部门版本化操作完善 王永文 20190502 begin
				if (uncanceledDepts != null && uncanceledDepts.length > 0) {
					saveVO = uncanceledDepts[0];
				}
				HRDeptVO parentVO = (HRDeptVO) saveVO.getParentVO();
				IOrgVersionManageService orgManageService = NCLocator.getInstance().lookup(
						IOrgVersionManageService.class);
				String vno = queryNextVersionNO("org_dept_v", "pk_dept", deptVO.getPk_dept(),
						String.valueOf(date.getYear()));
				DeptVO pvo = (DeptVO) getBaseDAO().retrieveByPK(DeptVO.class, parentVO.getPk_dept());
				String name = pvo.getName();
				String name2 = pvo.getName2();
				String name3 = pvo.getName3();
				/*
				 * 调用标准产品的版本化服务，但是标准产品要求版本化时必须修改名称，
				 * 而我们的需求是支持不修改名称的，解决的方案是先把名称修改，再把名称update回来
				 */
				pvo.setName(pvo.getName() + "@@@@TWNC");
				if (pvo.getName2() != null)
					pvo.setName2(pvo.getName2() + "@@@@TWNC");
				if (pvo.getName3() != null)
					pvo.setName3(pvo.getName3() + "@@@@TWNC");
				// 调用标准产品的标准版本化操作
				DeptVO hrDptNewVerVO = (DeptVO) orgManageService.createNewVersionVO("nc.vo.org.DeptVO", pvo, "版本更新"
						+ date.getYear() + vno, date.getYear() + vno, new UFDate(date.toString() + " 00:00:00"),
						new UFDate(date.getDateBefore(1).toString() + " 23:59:59"));
				if (hrDptNewVerVO == null) {
					return;
				}
				// 把名称再给update回来,同时把部门表和部门版本表的负责人、副主管、所在地点更新了
				updateOrgName(name, name2, name3, hrDptNewVerVO);
				/*
				 * AggHRDeptVO[] newVOs =
				 * getDeptManageService().createDeptVersion(uncanceledDepts, new
				 * UFDate(date.toDate()));
				 */
				// 更新HR部门表
				AggHRDeptVO newVO = new AggHRDeptVO();
				HRDeptVO hrdeptvo = (HRDeptVO) getBaseDAO().retrieveByPK(HRDeptVO.class, hrDptNewVerVO.getPk_dept());
				hrdeptvo.setFatherDeptChanged(parentVO.isFatherDeptChanged());
				hrdeptvo.setApprovedept(parentVO.getApprovedept());
				hrdeptvo.setApprovenum(parentVO.getApprovenum());
				hrdeptvo.setDeptduty(parentVO.getDeptduty());
				hrdeptvo.setManagescope(parentVO.isManagescope());
				newVO.setParentVO(hrdeptvo);
				getDeptManageService().update(newVO, false);
				newPkdeptV = ((HRDeptVO) newVO.getParentVO()).getPk_vid();
				// 回写工作记录
				sqlStr = "update  hi_psnjob set pk_dept_v = '" + newPkdeptV + "'  where begindate <= '"
						+ date.toStdString() + "' and isnull(enddate,'9999-12-31') > '" + date.toStdString()
						+ "' and  pk_dept = '" + deptVO.getPk_dept() + "'";
				getBaseDAO().executeUpdate(sqlStr);
				return;
			}
		}
		// 修改邏輯

		DeptHistoryVO historyVO = buildDeptHistoryVO4ImportUpdate(deptVO, date);
		// 部门版本化操作完善 王永文 20190502 begin
		HRDeptVO parentVO = (HRDeptVO) saveVO.getParentVO();
		IOrgVersionManageService orgManageService = NCLocator.getInstance().lookup(IOrgVersionManageService.class);
		String vno = queryNextVersionNO("org_dept_v", "pk_dept", deptVO.getPk_dept(), String.valueOf(date.getYear()));
		DeptVO pvo = (DeptVO) new BaseDAO().retrieveByPK(DeptVO.class, parentVO.getPk_dept());
		String name = pvo.getName();
		String name2 = pvo.getName2();
		String name3 = pvo.getName3();
		/*
		 * 调用标准产品的版本化服务，但是标准产品要求版本化时必须修改名称，
		 * 而我们的需求是支持不修改名称的，解决的方案是先把名称修改，再把名称update回来
		 */
		pvo.setName(pvo.getName() + "@@@@TWNC");
		if (pvo.getName2() != null)
			pvo.setName2(pvo.getName2() + "@@@@TWNC");
		if (pvo.getName3() != null)
			pvo.setName3(pvo.getName3() + "@@@@TWNC");
		// 调用标准产品的标准版本化操作
		DeptVO hrDptNewVerVO = (DeptVO) orgManageService.createNewVersionVO("nc.vo.org.DeptVO", pvo,
				"版本更新" + date.getYear() + vno, date.getYear() + vno, new UFDate(date.toString() + " 00:00:00"),
				new UFDate(date.getDateBefore(1).toString() + " 23:59:59"));
		if (hrDptNewVerVO == null) {
			return;
		}
		// 把名称再给update回来,同时把部门表和部门版本表的负责人、副主管、所在地点更新了
		updateOrgName(name, name2, name3, hrDptNewVerVO);
		// 更新HR部门表
		AggHRDeptVO newVO = new AggHRDeptVO();
		HRDeptVO hrdeptvo = (HRDeptVO) getBaseDAO().retrieveByPK(HRDeptVO.class, hrDptNewVerVO.getPk_dept());
		hrdeptvo.setFatherDeptChanged(parentVO.isFatherDeptChanged());
		hrdeptvo.setApprovedept(parentVO.getApprovedept());
		hrdeptvo.setApprovenum(parentVO.getApprovenum());
		hrdeptvo.setDeptduty(parentVO.getDeptduty());
		hrdeptvo.setManagescope(parentVO.isManagescope());
		newVO.setParentVO(hrdeptvo);
		getDeptManageService().update(newVO, false);

		// AggHRDeptVO newVO = saveVO;
		// 如果部門代碼變更或部門名稱變更,則新增人員任職記錄，
		int renameAndPrincipalChangeFlag = 0;
		if (deptVO.getCode() != null && curVO.getName() != null) {
			if (!deptVO.getCode().equals(curVO.getCode()) || !deptVO.getName().equals(curVO.getName())) {
				// 變更了名稱
				renameAndPrincipalChangeFlag = 1;
			}
		}
		if ((deptVO.getPrincipal() == null && curVO.getCode() != null)
				|| (deptVO.getPrincipal() != null && curVO.getPrincipal() == null)
				|| (deptVO.getPrincipal() != null && curVO.getPrincipal() != null && !deptVO.getPrincipal().equals(
						curVO.getPrincipal()))) {
			// 變更了負責人
			if (0 == renameAndPrincipalChangeFlag) {
				historyVO.setChangetype(DeptChangeType.CHANGEPRINCIPAL);
				renameAndPrincipalChangeFlag = 2;
			} else {
				renameAndPrincipalChangeFlag = 3;
			}
		}
		if (renameAndPrincipalChangeFlag != 0) {
			// getDeptManageService().renameAndPrincipalChange(newVO, historyVO,
			// true, renameAndPrincipalChangeFlag);
			// 插入历史数据
			getBaseDAO().insertVO(historyVO);
			newPkdeptV = ((HRDeptVO) newVO.getParentVO()).getPk_vid();
		}
		// 回写工作记录表
		sqlStr = "update  hi_psnjob set pk_dept_v = '" + newPkdeptV + "'  where begindate <= '" + date.toStdString()
				+ "' and isnull(enddate,'9999-12-31') > '" + date.toStdString() + "' and  pk_dept = '"
				+ deptVO.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sqlStr);

	}

	private void updateOrgName(String name, String name2, String name3, DeptVO vo) throws DAOException {
		// 更新部门表,更新负责人、副主管、所在地点
		String sql_dept = "update org_dept set name = " + getNmF(name) + ", name2 = " + getNmF(name2) + ",name3="
				+ getNmF(name3) + " where pk_dept = '" + vo.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_dept);
		// 更新部门版本表,更新负责人、副主管、所在地点
		String sql_dept_v = "update org_dept_v set name = " + getNmF(name) + ", name2 = " + getNmF(name2) + ",name3="
				+ getNmF(name3) + " where pk_vid = '" + vo.getPk_vid() + "'";
		getBaseDAO().executeUpdate(sql_dept_v);
		// 更新报表组织表
		String sql_report = "update org_reportorg set name = " + getNmF(name) + ", name2 = " + getNmF(name2)
				+ ",name3=" + getNmF(name3) + " where pk_reportorg = '" + vo.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_report);
		// 更新报表组织版本表
		String sql_report_v = "update org_reportorg_v set name = " + getNmF(name) + ", name2 = " + getNmF(name2)
				+ ",name3=" + getNmF(name3) + " where pk_vid = '" + vo.getPk_vid() + "'";
		getBaseDAO().executeUpdate(sql_report_v);
		// 更新组织表
		String sql_org = "update org_orgs set name = " + getNmF(name) + ", name2 = " + getNmF(name2) + ",name3="
				+ getNmF(name3) + " where pk_org = '" + vo.getPk_dept() + "'";
		getBaseDAO().executeUpdate(sql_org);

	}

	private DeptHistoryVO buildDeptHistoryVO4ImportUpdate(HRDeptVO vo, UFLiteralDate date) {
		DeptHistoryVO deptHistoryVO = new DeptHistoryVO();
		// 批准单位
		deptHistoryVO.setApprovedept(null);
		// 批准文号
		deptHistoryVO.setApprovenum(null);
		// 新部门名
		MultiLangText multiLangText = new MultiLangText();
		multiLangText.setText(vo.getName());
		multiLangText.setText2(vo.getName2());
		multiLangText.setText3(vo.getName3());
		multiLangText.setText4(vo.getName4());
		multiLangText.setText5(vo.getName5());
		multiLangText.setText6(vo.getName6());
		SuperVOHelper.copyMultiLangAttribute(multiLangText, deptHistoryVO);
		// 编码
		deptHistoryVO.setCode(vo.getCode());
		// 部门级别
		deptHistoryVO.setDeptlevel(vo.getDeptlevel());
		// 负责人
		deptHistoryVO.setPrincipal(vo.getPrincipal());
		// 生效日期--默認是今天
		deptHistoryVO.setEffectdate(date);
		// 备注
		deptHistoryVO.setMemo("自動版本化");
		// 变更类型-更名
		deptHistoryVO.setChangetype(DeptChangeType.RENAME);
		// 部门主键
		deptHistoryVO.setPk_dept(vo.getPk_dept());
		// 组织主键
		deptHistoryVO.setPk_org(vo.getPk_org());

		return deptHistoryVO;
	}

	private DeptHistoryVO buildDeptHistoryVO4ImportUnCancel(HRDeptVO vo, UFLiteralDate date) {
		DeptHistoryVO deptHistoryVO = new DeptHistoryVO();
		// 批准单位
		deptHistoryVO.setApprovedept(null);
		// 批准文号
		deptHistoryVO.setApprovenum(null);
		// 编码
		deptHistoryVO.setCode(vo.getCode());
		// 部门名
		SuperVOHelper.copyMultiLangAttribute(vo, deptHistoryVO);
		// 部门级别
		deptHistoryVO.setDeptlevel(vo.getDeptlevel());
		// 负责人
		deptHistoryVO.setPrincipal(vo.getPrincipal());
		// 生效日期
		deptHistoryVO.setEffectdate(date);
		// 备注
		deptHistoryVO.setMemo(null);
		// 变更类型-反撤销
		deptHistoryVO.setChangetype(DeptChangeType.HRUNCANCELED);
		// 部门主键
		deptHistoryVO.setPk_dept(vo.getPk_dept());
		// 组织主键
		deptHistoryVO.setPk_org(vo.getPk_org());
		// 接收部门
		deptHistoryVO.setIsreceived(UFBoolean.FALSE);

		return deptHistoryVO;
	}
	
	
	private char[] ran = {'A','B','C','D','E','F','G','H','I','J','K','L',
			'M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
			'0','1','2','3','4','5','6','7','8','9'};
	
	/**
	 * 获得随机四个字符的字符串
	 * @return
	 */
	private String get4RandomCode(){
		Random rom = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i <4; i++) {
			char r1 = ran[rom.nextInt(35)];
			sb.append(r1);
		}
		return sb.toString();
	}

	/**
	 * 更新当前部门的innercode
	 * @param innercode 上级部门的innercode
	 * @param pk_dept 更新的部门
	 * @return
	 * @throws DAOException
	 */
	private String updateCode(String innercode,String pk_dept) throws DAOException {
		StringBuffer sql = new StringBuffer();
		String s = get4RandomCode();
		//更新部门表
		sql.append("update org_dept set innercode = '")
			.append(innercode).append(s)
			.append("' where pk_dept = '")
			.append(pk_dept)
			.append("'");
		this.getBaseDAO().executeUpdate(sql.toString());
		//更新部门版本表
		sql = new StringBuffer();
		sql.append("update org_dept_v v set v.innercode = '")
			.append(innercode).append(s)
			.append("' where v.pk_vid =(select d.pk_vid from org_dept d where d.pk_dept ='")
			.append(pk_dept)
			.append("' )");
		this.getBaseDAO().executeUpdate(sql.toString());
		return innercode+s;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void updateDeptInnercode(String innercode,String pk_fatherorg) throws DAOException{
		String sql = "select innercode,pk_dept,pk_fatherorg from org_dept where dr=0 and pk_fatherorg = '"+pk_fatherorg+"'";
		List<Map<String,String>> list = (List<Map<String, String>>) 
				this.getBaseDAO().executeQuery(sql,  new MapListProcessor());
		if(list.size()==0){
			return ;
		}
		for (Map<String, String> map : list) {
			String s = updateCode(innercode,map.get("pk_dept"));
			updateDeptInnercode(s,map.get("pk_dept"));
		}
	}
}
