package nc.impl.om.deptadj;

import java.util.ArrayList;
import java.util.List;

import nc.ui.pub.beans.UIMultiLangCombox;
import nc.vo.logging.Debug;
import nc.vo.om.hrdept.AggHRDeptVO;
import nc.vo.om.hrdept.DeptChangeType;
import nc.vo.om.hrdept.DeptHistoryVO;
import nc.vo.om.hrdept.HRDeptAdjustVO;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.itf.om.IDeptAdjustService;
import nc.itf.om.IDeptManageService;
import nc.itf.om.IDeptQueryService;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.om.pub.SuperVOHelper;
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
	private static String[] NEED_FIX_TABLE_NAME = { "hi_psnjob", "om_deptadj",
			"tbm_leaveplan", "hi_stapply" };
	// 部門pk_dept的字段名
	private static String[] NEED_FIX_TABLE_COLUMN = { "pk_dept", "pk_dept",
			"pk_dept", "newpk_dept" };

	/** 部门QService **/
	private IDeptQueryService getDeptQueryService() {
		if (deptQueryService == null) {
			deptQueryService = NCLocator.getInstance().lookup(
					IDeptQueryService.class);
		}
		return deptQueryService;
	}

	/** 部门MService **/
	private IDeptManageService getDeptManageService() {
		if (deptManageService == null) {
			deptManageService = NCLocator.getInstance().lookup(
					IDeptManageService.class);
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
			pk_dept_v = getDeptQueryService().getDeptVid(pk_dept, new UFDate());
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return pk_dept_v;
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
	public void executeDeptVersion(UFLiteralDate date) throws BusinessException {

		// 查询该生效日期的所有单据
		String sqlStr = "select * from om_deptadj where effectivedate = '"
				+ date.toStdString() + "' and iseffective = 'N' and dr = 0";
		List<HRDeptAdjustVO> needExecuteVOs = (List<HRDeptAdjustVO>) getBaseDAO()
				.executeQuery(sqlStr,
						new BeanListProcessor(HRDeptAdjustVO.class));
		if (null != needExecuteVOs && needExecuteVOs.size() > 0) {
			// 执行版本化
			for (HRDeptAdjustVO vo : needExecuteVOs) {
				try {
					if (null == vo) {
						continue;
					}
					AggHRDeptVO saveVO = new AggHRDeptVO();
					HRDeptVO deptVO = HRDeptAdjust2HRDeptVO(vo);
					// 獲取當前的部門信息
					sqlStr = "select * from org_dept where pk_dept = '"
							+ deptVO.getPk_dept() + "' ";
					HRDeptVO curVO = (HRDeptVO) getBaseDAO().executeQuery(
							sqlStr, new BeanProcessor(HRDeptVO.class));
					if(deptVO.getPk_vid()==null ||deptVO.getPk_vid().equals("~")||deptVO.getPk_vid().equals("null")){
						deptVO.setPk_vid(curVO.getPk_vid());
					}
					String oldPkDept = deptVO.getPk_dept();
					// 設置啟用
					deptVO.setEnablestate(2);
					deptVO.setIslastversion(new UFBoolean(true));
					saveVO.setParentVO(deptVO);
					//处理撤销标志 TODO
					if(curVO != null && curVO.getHrcanceled() != null && vo.getHrcanceled() != null){
						//原来是撤销状态,且现在为非撤销状态,先取消撤銷
						if(curVO.getHrcanceled().booleanValue() && !vo.getHrcanceled().booleanValue()){
							DeptHistoryVO historyVO = buildDeptHistoryVO4UnCancel(deptVO);
							//反撤銷
							AggHRDeptVO[] uncanceledDepts = getDeptManageService().uncancel(saveVO, historyVO, false,
									false, true);
							// 创建部门新版本
							AggHRDeptVO[] newVOs = getDeptManageService().createDeptVersion(uncanceledDepts);
							if(newVOs != null && newVOs.length >= 1 && newVOs[0] != null){
								saveVO = getDeptManageService().update(newVOs[0], false);
								deptVO = HRDeptAdjust2HRDeptVO(vo);
								// 獲取當前的部門信息
								sqlStr = "select * from org_dept where pk_dept = '"
										+ deptVO.getPk_dept() + "' ";
								curVO = (HRDeptVO) getBaseDAO().executeQuery(
										sqlStr, new BeanProcessor(HRDeptVO.class));
							}
							
						}
					}
					
					if (vo.getPk_dept_v() != null
							&& vo.getPk_dept_v().equals(PK_VID_FOR_DEPT_VER)) {
						// 如果是新增,那麼先刪除主表上的檔案信息
						getBaseDAO().deleteVO(deptVO);
						// 執行新增標準平台新增邏輯
						AggHRDeptVO rtnVO = getDeptManageService().insert(
								saveVO);
						String pk_dept = null;
						if (rtnVO.getParentVO() != null) {
							pk_dept = rtnVO.getParentVO().getPrimaryKey();
						}
						// 替換所有原先引用的值
						dataFix(oldPkDept, pk_dept);
					} else {
						DeptHistoryVO historyVO = buildDeptHistoryVO4Update(deptVO);
						
						
						AggHRDeptVO newVO = getDeptManageService()
								.createDeptVersion(saveVO);
						if (null == newVO) {
							continue;
						}
						// 如果是修改,直接修改所有信息
						getBaseDAO().updateVO((HRDeptVO) newVO.getParentVO());
						// 如果部門代碼變更或部門名稱變更,則新增人員任職記錄，
						if (vo.getCode() != null && vo.getName() != null) {
							if (!vo.getCode().equals(curVO.getCode())
									|| !vo.getName().equals(curVO.getName())) {
								getDeptManageService().rename(newVO, historyVO,
										true);
							}
						}
					}
					// 回写标志:
					sqlStr = "update om_deptadj set iseffective = 'Y' where pk_deptadj = '"
							+ vo.getPk_deptadj() + "'";
					getBaseDAO().executeUpdate(sqlStr);
				} catch (Exception e) {
					Debug.debug(e.getMessage());
					continue;
				}
			
				
				
				
			}
		}
	}

	/**
	 * 主鍵修復,替換原有主鍵到新的主鍵
	 * 
	 * @param pk_dept
	 * @param pk_dept2
	 * @throws BusinessException
	 */
	private void dataFix(String old_pk_dept, String new_pk_dept)
			throws BusinessException {
		if (null == old_pk_dept || null == new_pk_dept || new_pk_dept.equals(old_pk_dept)) {
			return;
		}
		// 將新PK替換舊PK
		for (int i = 0; i < NEED_FIX_TABLE_NAME.length; i++) {
			String sqlStr = "update " + NEED_FIX_TABLE_NAME[i] + " set "
					+ NEED_FIX_TABLE_COLUMN[i] + " = '" + new_pk_dept + "' "
					+ " where " + NEED_FIX_TABLE_COLUMN[i] + " = '"
					+ old_pk_dept + "' and dr = 0";
			getBaseDAO().executeUpdate(sqlStr);
		}
	}

	/*
	 * 查询是否已存在指定部門未生效的調整申請單 pk_dept 返回UFBoolean True存在，False不存在
	 * 业务逻辑:查询是否已存在指定部門未生效的調整申請單即存在生效日期大于当前日期的此部门的调整单.
	 *  本身除外
	 */
	public UFBoolean isExistDeptAdj(String pk_dept,String pk_deptadj) throws BusinessException {
		String sqlStr = null;
		if(pk_deptadj != null){
			sqlStr = "select count(*) from om_deptadj where pk_dept ='"
					+ pk_dept + "' and effectivedate > '"
					+ new UFDate().toStdString() + "' and pk_deptadj <> '"+pk_deptadj
					+"' and dr = 0 and isnull(iseffective,'N') = 'N'";
		}else{
			sqlStr = "select count(*) from om_deptadj where pk_dept ='"
					+ pk_dept + "' and effectivedate > '"
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
		if (!isExistDeptAdj(vo.getPk_dept(),vo.getPk_deptadj()).booleanValue()) {
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
	public AggHRDeptVO writeBack4DeptAdd(AggHRDeptVO aggDeptVO)
			throws BusinessException {
		if (aggDeptVO == null || aggDeptVO.getParentVO() == null) {
			return null;
		}
		HRDeptVO deptVO = (HRDeptVO) aggDeptVO.getParentVO();
		// 設置狀態為未啟用
		deptVO.setEnablestate(1);
		// 处理日期大于当前日期才可以回写
		if (null != deptVO && deptVO.getCreatedate() != null
				&& isAfterToday(deptVO.getCreatedate())) {
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
	public AggHRDeptVO writeBack4DeptUnCancel(AggHRDeptVO aggDeptVO,UFLiteralDate effective)
			throws BusinessException {
		if (aggDeptVO == null || aggDeptVO.getParentVO() == null ) {
			return null;
		}
		if(effective == null){
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
			//取消撤銷標誌
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
	public List<HRDeptVO> queryOFutureDept(String pk_org, String whereSql)
			throws BusinessException {
		if (whereSql == null) {
			whereSql = " 1 = 1 ";
		}
		String sqlStr = " select * from org_dept where pk_org = '" + pk_org
				+ "' and  createdate > '" + (new UFDate()).toStdString()
				+ "' and " + whereSql;

		List<HRDeptVO> result = (List<HRDeptVO>) getBaseDAO().executeQuery(
				sqlStr, new BeanListProcessor(HRDeptVO.class));
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
	public HRDeptVO HRDeptAdjust2HRDeptVO(HRDeptAdjustVO vo)
			throws BusinessException {
		// 先查找当前的部门信息
		String sqlStr = "select * from org_dept where pk_dept = '"
				+ vo.getPk_dept() + "'";
		HRDeptVO resultVO = (HRDeptVO) getBaseDAO().executeQuery(sqlStr,
				new BeanProcessor(HRDeptVO.class));
		if (null == resultVO) {
			resultVO = new HRDeptVO();
		}
		// 将修改信息覆盖到当前信息里面
		if (null != vo) {
			resultVO.setPk_vid(vo.getPk_dept_v());
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
			resultVO.setIslastversion(vo.getIslastversion());
			resultVO.setCreator(vo.getCreator());
			resultVO.setCreationtime(vo.getCreationtime());
			resultVO.setModifiedtime(vo.getModifiedtime());
			resultVO.setModifier(vo.getModifier());
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
	public HRDeptAdjustVO HRDeptVO2HRDeptAdjust(HRDeptVO vo)
			throws BusinessException {
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
			//获取组织版本
			String sqlStr = "select pk_vid from org_orgs where pk_org = '"+vo.getPk_org()+"' ";
			Object pkvid = getSingleDataBySQL(sqlStr);
			if(null != pkvid){
				resultVO.setPk_org_v(String.valueOf(pkvid));
			}
		}
		return resultVO;
	}

	/**
	 * 自动任务--部门撤销 检查校验规则2,才能进行删除,删除时联动删除部门信息的主档 部门撤销和取消撤销暂时用标准功能
	 * 撤销部门暂时不进行版本化操作
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
		String sqlStr = "select count(*) from hi_stapply where  newpk_dept = '"
		+vo.getPk_dept()+"'and  EFFECTDATE > '"+vo.getEffectivedate().toStdString()+"' ";
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
		if (isAfterToday(vo.getEffectivedate()) && vo.getIseffective() != null
				&& !vo.getIseffective().booleanValue()) {
			// 查詢調配記錄中,是否有有引用此部門的單據
			String sqlStr = "select count(*) from hi_stapply where newpk_dept ='"
					+ vo.getPk_dept() + "' and dr = 0";
			Integer num = getIntegerDataBySQL(sqlStr);
			if (num != null && num > 0) {
				throw new BusinessException("該部門還有未處理的人員調配申請,請取消調配后再刪除!");
			}
			// 已经有下级部门时,删除报错
			sqlStr = "select count(*) from om_deptadj where pk_fatherorg = '"
					+ vo.getPk_dept() + "' and dr = 0";
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
	 * @return
	 */
	private DeptHistoryVO buildDeptHistoryVO4Update(HRDeptVO vo) {
		DeptHistoryVO deptHistoryVO = new DeptHistoryVO();
		// 批准单位
		deptHistoryVO.setApprovedept(null);
		// 批准文号
		deptHistoryVO.setApprovenum(null);
		UIMultiLangCombox lang = new UIMultiLangCombox();
		MultiLangText multiLangText = new MultiLangText();
		multiLangText.setText(vo.getName());
		multiLangText.setText2(vo.getName());
		lang.setMultiLangText(multiLangText);
		// 新部门名
		MultiLangText mutiLangText = lang.getMultiLangText();
		SuperVOHelper.copyMultiLangAttribute(mutiLangText, deptHistoryVO);
		// 编码
		deptHistoryVO.setCode(vo.getCode());
		// 部门级别
		deptHistoryVO.setDeptlevel(vo.getDeptlevel());
		// 负责人
		deptHistoryVO.setPrincipal(vo.getPrincipal());
		// 生效日期--默認是今天
		deptHistoryVO.setEffectdate(new UFLiteralDate());
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

}
