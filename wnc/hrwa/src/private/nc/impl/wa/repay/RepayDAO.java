package nc.impl.wa.repay;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nc.bs.bd.util.DBAUtil;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.impl.wa.common.WaCommonImpl;
import nc.impl.wa.pub.WapubDAO;
import nc.itf.hr.wa.IClassItemQueryService;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.jdbc.framework.JdbcPersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.generator.IdGenerator;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.vo.dataitem.pub.DataVOUtils;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.hr.pub.FormatVO;
import nc.vo.hr.tools.dbtool.util.db.DBUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.util.BDPKLockUtil;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.paydata.ICommonAlterName;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wa.repay.ReDataVO;
import nc.vo.wa.repay.RepaydataVO;

import org.apache.commons.lang.ArrayUtils;

public class RepayDAO extends AppendBaseDAO implements ICommonAlterName {

	/**
	 * 修改
	 * 
	 * @author liangxr on 2010-6-10
	 * @param object
	 * @param waLoginVO
	 * @throws BusinessException
	 */
	public void update(Object object, WaLoginVO waLoginVO)
			throws BusinessException {
		if (!object.getClass().isArray()) {
			ReDataVO datavo = (ReDataVO) object;
			// shenliangc 20140815 解决薪资补发删除后修改保存报错“更新了一个不存在的数据”
			if (datavo.getStatus() != 3) {
				// 2016-12-12 zhousze 薪资加密：这里处理薪资补发薪资项目修改数据加密 begin
				HashMap<String, Object> map = datavo.appValueHashMap;
				Object[] pks = map.keySet().toArray();
				for (int i = 0; i < pks.length; i++) {
					if (pks[i].toString().startsWith("f_")) {
						UFDouble oldValue = (UFDouble) map.get(pks[i]);
						oldValue = new UFDouble(
								SalaryEncryptionUtil.encryption(oldValue
										.toDouble()));
						datavo.setAttributeValue(pks[i].toString(), oldValue);
					}
				}
				// end
				singleUpdate(datavo);
			}
		} else {
			Object[] objs = (Object[]) object;
			for (Object vo : objs) {
				// shenliangc 20140815 解决薪资补发删除后修改保存报错“更新了一个不存在的数据”
				if (((ReDataVO) vo).getStatus() != 3) {
					ReDataVO datavo = (ReDataVO) vo;

					// 2016-12-12 zhousze 薪资加密：这里处理薪资补发薪资项目修改数据加密 begin
					HashMap<String, Object> map = datavo.appValueHashMap;
					Object[] pks = map.keySet().toArray();
					for (int i = 0; i < pks.length; i++) {
						if (pks[i].toString().startsWith("f_")) {
							UFDouble oldValue = (UFDouble) map.get(pks[i]);
							oldValue = new UFDouble(
									SalaryEncryptionUtil.encryption(oldValue
											.toDouble()));
							datavo.setAttributeValue(pks[i].toString(),
									oldValue);
						}
					}
					// end

					singleUpdate(datavo);
				}
			}
		}

		// 更新期间计算状态
		updatePeriodState(waLoginVO, "caculateflag", UFBoolean.FALSE);

	}

	/**
	 * 单个修改
	 * 
	 * @author liangxr on 2010-6-10
	 * @param datavo
	 * @throws BusinessException
	 */
	private void singleUpdate(ReDataVO datavo) throws BusinessException {
		// 加锁
		BDPKLockUtil.lockSuperVO(datavo);

		datavo.setCaculateflag(UFBoolean.FALSE);

		String[] attributeNames = datavo.getAttributeNames();
		List<String> needUpdateNamesList = new LinkedList<String>();
		for (String attributeName : attributeNames) {
			if (DataVOUtils.isAppendAttribute(attributeName)) {
				needUpdateNamesList.add(attributeName);
			}
		}
		needUpdateNamesList.add(ReDataVO.CACULATEFLAG);
		JdbcPersistenceManager.clearColumnTypes(ReDataVO.getDefaultTableName());
		getBaseDao().updateVO(datavo,
				needUpdateNamesList.toArray(new String[0]));
	}

	/**
	 * 删除
	 * 
	 * @author liangxr on 2010-6-10
	 * @param object
	 * @param waLoginVO
	 * @throws BusinessException
	 */
	public void delete(Object object, WaLoginVO waLoginVO)
			throws BusinessException {

		if (!object.getClass().isArray()) {
			ReDataVO datavo = (ReDataVO) object;
			singleDelete(datavo);
		} else {
			Object[] objs = (Object[]) object;
			for (Object vo : objs) {
				ReDataVO datavo = (ReDataVO) vo;
				singleDelete(datavo);
			}
		}

		// 更新期间计算状态
		updatePeriodState(waLoginVO, PeriodStateVO.CACULATEFLAG,
				UFBoolean.FALSE);
	}

	/**
	 * 单个删除
	 * 
	 * @author liangxr on 2010-6-10
	 * @param datavo
	 * @throws BusinessException
	 */
	private void singleDelete(ReDataVO datavo) throws BusinessException {
		// 加锁
		BDPKLockUtil.lockSuperVO(datavo);

		datavo.setCaculateflag(UFBoolean.FALSE);

		String[] attributeNames = datavo.getAttributeNames();
		List<String> needUpdateNamesList = new LinkedList<String>();
		for (String attributeName : attributeNames) {
			if (DataVOUtils.isAppendAttribute(attributeName)) {
				needUpdateNamesList.add(attributeName);
			}
		}
		needUpdateNamesList.add(ReDataVO.CACULATEFLAG);
		getBaseDao().deleteVO(datavo);
	}

	/**
	 * 替换
	 * 
	 * @author liangxr on 2010-6-10
	 * @param waLoginVO
	 * @param whereCondition
	 * @param replaceItem
	 * @param formula
	 * @throws BusinessException
	 */

	public void onReplace(WaLoginVO waLoginVO, String whereCondition,
			WaClassItemVO replaceItem, String formula) throws BusinessException {

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("update wa_redata set ");

		if (TypeEnumVO.FLOATTYPE.value().equals(replaceItem.getIitemtype())) {// 数字型
			sqlB.append(replaceItem.getItemkey()).append("= ");
			sqlB.append(WaCommonImpl.getRoundSql(getBaseDao().getDBType(),
					formula, replaceItem.getIflddecimal(),
					replaceItem.getRound_type()));
		} else {// 字符型
			sqlB.append(replaceItem.getItemkey()).append("=").append(formula)
					.append(", ");
		}
		sqlB.append("caculateflag= 'N' ");
		sqlB.append(" where pk_wa_class_z = ? ");
		sqlB.append(" and  cyear = ?  and cperiod = ? ");
		sqlB.append(" and  pk_wa_class = ?  ");
		sqlB.append(" and creyear = ? and creperiod = ?");

		sqlB.append(WherePartUtil.formatAddtionalWhere(whereCondition));

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(WaLoginVOHelper.getChildClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(WaLoginVOHelper.getParentClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getReyear());
		parameter.addParam(waLoginVO.getReperiod());

		// 跟新补发数据
		// 2016-1-27 NCdp205578930 zhousze 薪资补发替换项目不规范时，会抛出DAOException，封装成业务异常
		// begin
		try {
			getBaseDao().executeUpdate(sqlB.toString(), parameter);
		} catch (DAOException e) {
			throw new BusinessException(ResHelper.getString("60130paydata",
					"060130paydata0490")/* @res "公式设置错误" */);
		}
		// end
		// 更新当前薪资类别状态
		updatePeriodState(waLoginVO, PeriodStateVO.CACULATEFLAG,
				UFBoolean.FALSE);
	}

	/**
	 * 复制到下一期间
	 * 
	 * @author liangxr on 2010-6-12
	 * @param waLoginVO
	 * @param nextyear
	 * @param nextperiod
	 * @param waItems
	 * @throws DAOException
	 */
	public void copy(WaLoginVO waLoginVO, String nextyear, String nextperiod,
			WaClassItemVO[] waItems) throws DAOException {

		String pk_wa_class_z = WaLoginVOHelper.getChildClassPK(waLoginVO);
		String pk_wa_class = WaLoginVOHelper.getParentClassPK(waLoginVO);
		String cyear = waLoginVO.getPeriodVO().getCyear();
		String cperiod = waLoginVO.getPeriodVO().getCperiod();
		String reyear = waLoginVO.getReyear();
		String reperiod = waLoginVO.getReperiod();
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("update next set ");

		for (int i = 0; i < waItems.length; i++) {
			sqlB.append(" next.").append(waItems[i].getItemkey());
			sqlB.append("=pre.").append(waItems[i].getItemkey());
			if (i < waItems.length - 1) {
				sqlB.append(",");
			}
		}

		sqlB.append(" from wa_redata pre, wa_redata  next ");

		sqlB.append(" where pre.pk_wa_class_z= '").append(pk_wa_class_z);
		sqlB.append("' and pre.cyear= '").append(cyear);
		sqlB.append("' and pre.cperiod= '").append(cperiod);

		sqlB.append("' and pre.pk_wa_class= '").append(pk_wa_class);
		sqlB.append("' and pre.creyear= '").append(reyear);
		sqlB.append("' and pre.creperiod= '").append(reperiod);

		sqlB.append("' and next.pk_wa_class_z= '").append(pk_wa_class_z);
		sqlB.append("' and next.cyear= '").append(cyear);
		sqlB.append("' and next.cperiod= '").append(cperiod);

		sqlB.append("' and next.pk_wa_class= '").append(pk_wa_class);
		sqlB.append("' and next.creyear= '").append(nextyear);
		sqlB.append("' and next.creperiod= '").append(nextperiod);
		sqlB.append("' and pre.pk_psnjob=next.pk_psnjob ");

		getBaseDao().executeUpdate(sqlB.toString());
	}

	/**
	 * 重新汇总,目前只汇总数字型薪资项目
	 * 
	 * @author liangxr on 2010-6-11
	 * @param pk_wa_class
	 * @param waYear
	 * @param waPeriod
	 * @param psnVO
	 * @param waDigitItem
	 * @throws BusinessException
	 */
	public void reTotal(String pk_wa_class, String pk_wa_class_z,
			String waYear, String waPeriod, RepaydataVO[] psnVO,
			WaClassItemVO[] waDigitItem) throws BusinessException {
		// 拼sql insert部分
		StringBuffer sql1 = new StringBuffer();
		sql1.append("insert into wa_redata( pk_wa_redata, pk_psnjob, pk_psndoc,pk_psnorg, pk_group, pk_org,workorgvid,workdeptvid, ");
		sql1.append("pk_wa_class,pk_wa_class_z,cyear,cperiod,caculateflag,stopflag,creyear, creperiod, reflag ");
		for (int i = 0; i < waDigitItem.length; i++) {
			sql1.append(", ");
			sql1.append(waDigitItem[i].getItemkey());
		}
		sql1.append(") ");
		sql1.append(" select  ");

		// 拼sql insert-select后面部分
		StringBuffer sql2 = new StringBuffer();
		sql2.append("'Y','N','-1','-1','N'");

		for (int i = 0; i < waDigitItem.length; i++) {
			sql2.append(", sum(");
			sql2.append(waDigitItem[i].getItemkey());
			sql2.append(")");
		}
		sql2.append(" from wa_redata where pk_wa_class_z = '");
		sql2.append(pk_wa_class_z);
		sql2.append("' and cyear= '");
		sql2.append(waYear);
		sql2.append("' and cperiod= '");
		sql2.append(waPeriod);
		sql2.append("' and creyear <> -1 and creperiod <> -1  and pk_psndoc= ");

		// 最后组装sql
		IdGenerator idGenerator = DBAUtil.getIdGenerator();
		String[] keys = idGenerator.generate(psnVO.length);
		StringBuffer[] updateSqls = new StringBuffer[psnVO.length];
		for (int i = 0; i < psnVO.length; i++) {
			updateSqls[i] = new StringBuffer(100);
			updateSqls[i].append(sql1).append("'").append(keys[i]).append("',");
			updateSqls[i].append("'").append(psnVO[i].getPk_psnjob())
					.append("',");
			updateSqls[i].append("'").append(psnVO[i].getPk_psndoc())
					.append("',");
			updateSqls[i].append("'").append(psnVO[i].getPk_psnorg())
					.append("',");
			updateSqls[i].append("'").append(psnVO[i].getPk_group())
					.append("',");
			updateSqls[i].append("'").append(psnVO[i].getPk_org()).append("',");
			// 多版本
			updateSqls[i].append("'").append(psnVO[i].getWorkorgvid())
					.append("',");
			updateSqls[i].append("'").append(psnVO[i].getWorkdeptvid())
					.append("',");

			updateSqls[i].append("'").append(pk_wa_class).append("',");
			updateSqls[i].append("'").append(pk_wa_class_z).append("',");
			updateSqls[i].append("'").append(waYear).append("',");
			updateSqls[i].append("'").append(waPeriod).append("',")
					.append(sql2);
			updateSqls[i].append("'").append(psnVO[i].getPk_psndoc())
					.append("'");
		}
		executeSQLs(updateSqls);
	}

	/***************************************************************************
	 * 查询补发数据 <br>
	 * Created on 2012-7-11 上午10:03:53<br>
	 * 
	 * @param waLoginVO
	 * @param reYear
	 * @param rePeriod
	 * @param sqlWhere
	 * @return
	 * @throws DAOException
	 * @author daicy
	 ***************************************************************************/
	public ReDataVO[] queryAllAt(WaLoginVO waLoginVO, String reYear,
			String rePeriod, String sqlWhere) throws DAOException {

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(WaLoginVOHelper.getChildClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  "
				+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  "
				+ PSNNAME + ", "); // 1
		sqlBuffer.append("       bd_psndoc.code " + PSNCODE + ", "); // 2
		sqlBuffer.append("       hi_psnjob.clerkcode, "); // 3
		sqlBuffer.append("        "
				+ SQLHelper.getMultiLangNameColumn("org_dept.name") + "  "
				+ DEPTNAME + ", "); // 4
		sqlBuffer.append("        "
				+ SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  "
				+ PLSNAME + ", "); // 5
		sqlBuffer.append("       om_job.jobname, "); // 6
		sqlBuffer.append("       wa_redata.* "); // 7
		sqlBuffer.append("  from wa_redata ");
		sqlBuffer
				.append(" inner join bd_psndoc on wa_redata.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer
				.append(" inner join hi_psnjob on wa_redata.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer
				.append(" left outer join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept ");
		sqlBuffer
				.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer
				.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");
		sqlBuffer.append(" where wa_redata.pk_wa_class_z = ? ");
		sqlBuffer.append(" and wa_redata.cyear=? ");
		sqlBuffer.append(" and wa_redata.cperiod=? ");
		sqlBuffer.append(" and wa_redata.stopflag= 'N' ");
		if (!StringUtil.isEmpty(reYear) && !StringUtil.isEmpty(rePeriod)) {
			sqlBuffer.append(" and wa_redata.pk_wa_class=? ");
			sqlBuffer.append(" and rtrim(wa_redata.creyear) = ?");
			sqlBuffer.append(" and rtrim(wa_redata.creperiod) = ? ");
			parameter.addParam(WaLoginVOHelper.getParentClassPK(waLoginVO));
			parameter.addParam(reYear);
			parameter.addParam(rePeriod);
		}
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(sqlWhere));

		return executeQueryVOs(sqlBuffer.toString(), parameter, ReDataVO.class);
	}

	/**
	 * 查询该期间补发的所有VO
	 * 
	 * @author liangxr on 2010-6-12
	 * @param waLoginVO
	 * @param deptpower
	 * @return
	 * @throws DAOException
	 */
	public RepaydataVO[] getAllUnitPsn(WaLoginVO waLoginVO, String deptpower)
			throws DAOException {

		StringBuffer sqlB = new StringBuffer();
		sqlB.append(" select distinct pk_psnjob, pk_psndoc, pk_psnorg,pk_group,pk_org,workorgvid,workdeptvid ");
		sqlB.append(" from wa_redata  where pk_wa_class_z= ? and cyear= ? and cperiod= ? ");
		sqlB.append(" and creyear <> '-1' and creperiod <> '-1' and stopflag ='N' ");
		sqlB.append(WherePartUtil.formatAddtionalWhere(deptpower));

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(WaLoginVOHelper.getChildClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		return executeQueryVOs(sqlB.toString(), parameter, RepaydataVO.class);

	}

	/***************************************************************************
	 * 查询薪资方案的已结帐的所有薪资期间 <br>
	 * Created on 2012-7-11 上午10:03:26<br>
	 * 
	 * @param pk_wa_class
	 * @return
	 * @throws DAOException
	 * @author daicy
	 ***************************************************************************/
	public PeriodStateVO[] getAccountPeriods(String pk_wa_class)
			throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(WapubDAO.getPeriodViewTable());
		sqlBuffer.append(" where wa_periodstate.pk_wa_class = ? ");
		sqlBuffer.append(" and accountmark = 'Y' ");
		sqlBuffer.append(" order by wa_period.cyear, wa_period.cperiod");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_wa_class);

		return executeQueryVOs(sqlBuffer.toString(), parameter,
				PeriodStateVO.class);
	}

	/***************************************************************************
	 * 查询已初始化的补发期间 <br>
	 * Created on 2012-7-11 上午10:02:30<br>
	 * 
	 * @param waLoginVO
	 * @param deptpower
	 * @return
	 * @throws DAOException
	 * @author daicy
	 ***************************************************************************/
	public RepaydataVO[] getRepayPeriods(WaLoginVO waLoginVO, String deptpower)
			throws DAOException {
		if (null != waLoginVO && null != waLoginVO.getPeriodVO()
				&& null != waLoginVO.getPeriodVO().getCperiod()) {

			StringBuffer sqlB = new StringBuffer();
			sqlB.append(" select distinct creyear, creperiod from wa_redata  where pk_wa_class_z= ? and cyear= ? and cperiod = ?  and dr=0 ");
			sqlB.append(WherePartUtil.formatAddtionalWhere(deptpower));
			sqlB.append(" order by creyear, creperiod ");

			SQLParameter parameter = new SQLParameter();
			parameter.addParam(WaLoginVOHelper.getChildClassPK(waLoginVO));
			parameter.addParam(waLoginVO.getPeriodVO().getCyear());
			parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

			return executeQueryVOs(sqlB.toString(), parameter,
					RepaydataVO.class);
		}
		return null;
	}

	/***************************************************************************
	 * 删除期间的所有补发数据 <br>
	 * Created on 2012-7-11 上午10:03:08<br>
	 * 
	 * @param waLoginVO
	 * @param deptpower
	 * @throws DAOException
	 * @author daicy
	 ***************************************************************************/
	public void deleteAll(WaLoginVO waLoginVO, String deptpower)
			throws DAOException {

		StringBuffer sqlB = new StringBuffer(
				"delete from wa_redata  where pk_wa_class_z= ? and cyear = ? and cperiod = ?  and dr=0 ");
		sqlB.append(WherePartUtil.formatAddtionalWhere(deptpower));

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(WaLoginVOHelper.getChildClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		getBaseDao().executeUpdate(sqlB.toString(), parameter);
	}

	/**
	 * 重置最新期间的补发数据
	 * 
	 * @author liangxr on 2010-6-8
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @param deptpower
	 * @throws DAOException
	 */
	public void resetWaData(WaLoginVO waLoginVO, String deptpower)
			throws DAOException {

		deptpower = StringUtil.replaceAllString(deptpower, "wa_redata",
				"wa_data");

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("update wa_data set f_8=0, f_9=0, caculateflag= 'N',checkflag ='N' where ");
		sqlB.append(WherePartUtil.getCommonWhereCondtion4ChildData(waLoginVO));
		sqlB.append(WherePartUtil.formatAddtionalWhere(deptpower));

		getBaseDao().executeUpdate(sqlB.toString());

	}

	/**
	 * 从wa_redata表中删除汇总数据
	 * 
	 * @author liangxr on 2010-6-8
	 * @param pk_wa_class
	 * @param waYear
	 * @param waPeriod
	 * @param deptpower
	 * @throws DAOException
	 */

	public void deleteTotalData(WaLoginVO waLoginVO, String deptpower)
			throws DAOException {

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("delete from wa_redata  where pk_wa_class_z = ?  and cyear=?  and  cperiod=? ");
		sqlB.append(" and rtrim(creyear)= '-1' and rtrim(creperiod) = '-1' ");
		sqlB.append(WherePartUtil.formatAddtionalWhere(deptpower));

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(WaLoginVOHelper.getChildClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		getBaseDao().executeUpdate(sqlB.toString(), parameter);

	}

	/**
	 * 更新wa_periodState字段
	 * 
	 * @author liangxr on 2010-6-8
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @param colKey
	 * @param colValue
	 * @throws BusinessException
	 */
	public void updatePeriodState(WaLoginVO waLoginvo, String colKey,
			Object colValue) throws BusinessException {
		String where = WherePartUtil
				.getCommonWhereCondtion4PeriodState(waLoginvo);
		updateTableByColKey("wa_periodstate", new String[] { colKey },
				new Object[] { colValue }, where);
	}

	/**
	 * 根据查询条件查询PK
	 * 
	 * @author zhousze
	 * @since 2015-11-25
	 * @param context
	 * @param condition
	 * @param orderCondtion
	 * @return
	 * @throws DAOException
	 */
	public String[] queryPKSByCondition(WaLoginContext context,
			String condition, String orderCondtion) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(getSelectPart4PK());
		sqlBuffer.append(" where wa_redata.pk_wa_class_z =? and ");
		sqlBuffer.append(" wa_redata.cyear = ? and ");
		sqlBuffer.append(" wa_redata.cperiod = ? and ");
		sqlBuffer.append(" wa_redata.pk_wa_class = ? and ");
		sqlBuffer.append(" ltrim(rtrim(wa_redata.creyear)) = ? and ");
		sqlBuffer.append(" ltrim(rtrim(wa_redata.creperiod)) = ? ");

		// TODO 完善查询
		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer
					.append(" and wa_redata.pk_wa_redata in (select pk_wa_redata from wa_redata where ")
					.append(condition).append(")");
		}

		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, null);
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		// 20150924 shenliangc 设置人员数据权限后薪资补发刷新查询报错。原因是错将order by拼接到了数据权限子句前面。
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		} else {
			sqlBuffer.append(" order by org_dept_v.code,hi_psnjob.clerkcode");
		}

		SQLParameter parameter = new SQLParameter();

		WaLoginVO waLoginVO = context.getWaLoginVO();
		parameter.addParam(WaLoginVOHelper.getChildClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(WaLoginVOHelper.getParentClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getReyear());
		parameter.addParam(waLoginVO.getReperiod());

		ReDataVO[] vos = executeQueryAppendableVOs(sqlBuffer.toString(),
				parameter, ReDataVO.class);

		String[] pks = new String[0];
		if (vos != null) {
			pks = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				pks[i] = vos[i].getPk_wa_redata();
			}

		}

		return pks;
	}

	/**
	 * 用于查询PKS时的select部分
	 * 
	 * @author zhousze
	 * @since 2015-11-25
	 * @return
	 */
	private String getSelectPart4PK() {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_redata.pk_wa_redata "); // 1
		sqlBuffer.append("  from wa_redata ");
		sqlBuffer
				.append(" inner join bd_psndoc on wa_redata.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer
				.append(" inner join hi_psnjob on wa_redata.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer
				.append(" inner join org_orgs_v on org_orgs_v.pk_vid = wa_redata.workorgvid ");
		sqlBuffer
				.append(" left outer join org_dept_v on org_dept_v.pk_vid = wa_redata.workdeptvid ");
		sqlBuffer
				.append(" left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");

		sqlBuffer
				.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");
		return sqlBuffer.toString();
	}

	/**
	 * 按条件查询补发数据
	 * 
	 * @author liangxr on 2010-6-11
	 * @param context
	 * @param condition
	 * @param orderCondtion
	 * @return
	 * @throws DAOException
	 */
	public ReDataVO[] queryByCondition(WaLoginContext context,
			String condition, String orderCondtion) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(getSelectPart());
		sqlBuffer.append(" where wa_redata.pk_wa_class_z =? and ");
		sqlBuffer.append(" wa_redata.cyear = ? and ");
		sqlBuffer.append(" wa_redata.cperiod = ? and ");
		sqlBuffer.append(" wa_redata.pk_wa_class = ? and ");
		sqlBuffer.append(" ltrim(rtrim(wa_redata.creyear)) = ? and ");
		sqlBuffer.append(" ltrim(rtrim(wa_redata.creperiod)) = ? ");

		// TODO 完善查询
		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer
					.append(" and wa_redata.pk_wa_redata in (select pk_wa_redata from wa_redata where ")
					.append(condition).append(")");
		}

		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, null);
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		// 20150924 shenliangc 设置人员数据权限后薪资补发刷新查询报错。原因是错将order by拼接到了数据权限子句前面。
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		} else {
			sqlBuffer.append(" order by org_dept_v.code,hi_psnjob.clerkcode");
		}

		SQLParameter parameter = new SQLParameter();

		WaLoginVO waLoginVO = context.getWaLoginVO();
		parameter.addParam(WaLoginVOHelper.getChildClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(WaLoginVOHelper.getParentClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getReyear());
		parameter.addParam(waLoginVO.getReperiod());

		return executeQueryAppendableVOs(sqlBuffer.toString(), parameter,
				ReDataVO.class);
	}

	private String getSelectPart() {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select bd_psndoc.pk_psndoc, "
				+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  "
				+ PSNNAME + ", "); // 1
		sqlBuffer.append("       bd_psndoc.code " + PSNCODE + ", "); // 2
		sqlBuffer.append("       hi_psnjob.clerkcode, "); // 3
		sqlBuffer.append("        "
				+ SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  "
				+ ORGNAME + ", "); // 3
		sqlBuffer.append("        "
				+ SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  "
				+ DEPTNAME + ", "); // 4
		sqlBuffer.append("        "
				+ SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  "
				+ PLSNAME + ", "); // 5
		sqlBuffer.append("       om_job.jobname, "); // 6
		sqlBuffer.append("       wa_redata.* "); // 7
		sqlBuffer.append("  from wa_redata ");
		sqlBuffer
				.append(" inner join bd_psndoc on wa_redata.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer
				.append(" inner join hi_psnjob on wa_redata.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer
				.append(" inner join org_orgs_v on org_orgs_v.pk_vid = wa_redata.workorgvid ");
		sqlBuffer
				.append(" left outer join org_dept_v on org_dept_v.pk_vid = wa_redata.workdeptvid ");
		sqlBuffer
				.append(" left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");

		sqlBuffer
				.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");
		return sqlBuffer.toString();
	}

	/**
	 * 按人员某期间查询补发数据,不包含汇总数据
	 * 
	 * x-y = z
	 * 
	 * 查询 z 的数据
	 * 
	 * @author liangxr on 2010-6-11
	 * @param context
	 * @param condition
	 * @param orderCondtion
	 * @return
	 * @throws DAOException
	 */
	private ReDataVO[] queryPsnRepayVO(WaLoginVO waLoginVO, String pk_psnjob)
			throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(getSelectPart());
		sqlBuffer.append("where wa_redata.pk_wa_class_z =? and ");
		sqlBuffer.append("wa_redata.cyear = ? and ");
		sqlBuffer
				.append("wa_redata.cperiod = ?  and  wa_redata.pk_psndoc = (select pk_psndoc from hi_psnjob where pk_psnjob =  ?)  and creyear<>'-1' and creperiod<>'-1'");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(WaLoginVOHelper.getChildClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(pk_psnjob);

		return executeQueryAppendableVOs(sqlBuffer.toString(), parameter,
				ReDataVO.class);
	}

	/**
	 * 总额补发
	 * 
	 * @author liangxr on 2010-6-12
	 * @param datavo
	 * @param yearAndPeriod
	 * @param itemcodesEidtDbl
	 * @param waLoginVO
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public ReDataVO[] updateTotalRe(ReDataVO datavo, WaClassItemVO[] editItems,
			WaLoginVO waLoginVO) throws BusinessException {
		// 查出所有补发记录
		ReDataVO[] psnDataVos = queryPsnRepayVO(waLoginVO,
				datavo.getPk_psnjob());
		if (ArrayUtils.isEmpty(psnDataVos)) {
			return null;
		}

		// 查询所有补发期间。
		RepaydataVO[] vos = getRepayPeriods(waLoginVO, null);
		String timessql = getItemsApperTimesSQL(vos, psnDataVos[0]);
		StringBuffer sbf = new StringBuffer();
		InSQLCreator c = new InSQLCreator();
		sbf.append(" select itemkey ,count (1)  from wa_classitem where pk_wa_class  = '"
				+ psnDataVos[0].getPk_wa_class()
				+ "' and itemkey in ("
				+ c.getInSQL(editItems, "itemkey") + ")  ");
		sbf.append("  and (" + timessql + ")  group by itemkey ");

		Map<String, Integer> timesmap = (Map<String, Integer>) getBaseDao()
				.executeQuery(sbf.toString(), new BaseProcessor() {
					@Override
					public Object processResultSet(ResultSet rs)
							throws SQLException {
						Map<String, Integer> result = new HashMap<String, Integer>();
						while (rs.next()) {
							result.put(rs.getString(1), rs.getInt(2));
						}
						return result;
					}
				});
		// 针对每一个补发记录处理
		for (ReDataVO psnDataVo : psnDataVos) {
			psnDataVo.setCaculateflag(UFBoolean.FALSE);
			psnDataVo.setReflag(UFBoolean.FALSE);
			for (WaClassItemVO classitem : editItems) {
				String key = classitem.getItemkey();
				Integer times = timesmap.get(key);
				UFDouble d = UFDouble.ZERO_DBL;
				if (times == null || times == 0) {
					d = UFDouble.ZERO_DBL;
				} else {
					Object o = datavo.getAttributeValue(key);
					if (o == null) {
						o = 0;
					}
					d = (new UFDouble(
							new BigDecimal(o.toString()).doubleValue()
									/ times.intValue())).setScale(-6,
							UFDouble.ROUND_HALF_UP);
				}
				// 确定该期间是否存在 key
				if (canPayedTheItem(psnDataVo, key)) {
					// 确定人员、项目、应该补发多少钱
					psnDataVo.setAttributeValue(key, d);
				} else {
					psnDataVo.setAttributeValue(key, UFDouble.ZERO_DBL);
				}
			}
		}

		update(psnDataVos, waLoginVO);

		return psnDataVos;
	}

	// private Map<String, Integer> getItemsApperTimes(WaClassItemVO[]
	// editItems, WaLoginVO waLoginVO) throws BusinessException{
	// //查询出所有的补发期间
	//
	// //委托范围呢？
	// RepaydataVO[] vos = getRepayPeriods(waLoginVO, null);
	// Map<String, Integer> map= new HashMap<String, Integer>();
	// if(ArrayUtils.isEmpty(vos)){
	// throw new BusinessException(" find no append period");
	//
	// }
	// StringBuilder sbd = new StringBuilder();
	//
	// for (int i = 0; i < vos.length; i++) {
	// sbd.append(" (cyear= '"+vos[i].getCreyear()+"' and  cperiod = '"+vos[i].getCreperiod()+"') ");
	// if(i < vos.length-1){
	// sbd.append(" or ");
	// }
	// }
	//
	// String sbd2 = "( "+ sbd.toString() +" )";
	//
	// //循环确定每个项目出现的次数。项目不是很多，不出效率问题
	// for (int i = 0; i < editItems.length; i++) {
	// WaClassItemVO waClassItemVO = editItems[i];
	// String temp = " select count(*) from wa_classitem where pk_wa_class = '"
	// +WaLoginVOHelper.getParentClassPK(waLoginVO) + "' and  "+ sbd2.toString()
	// +" and itemkey = '"+ waClassItemVO.getItemkey()+"' ";
	// Integer times = (Integer) getBaseDao().executeQuery(temp, new
	// ColumnProcessor());
	// map.put( waClassItemVO.getItemkey(), times);
	// }
	//
	// return map;
	//
	// }

	private String getItemsApperTimesSQL(RepaydataVO[] vos, ReDataVO psndata)
			throws BusinessException {

		if (ArrayUtils.isEmpty(vos)) {
			throw new BusinessException(" find no append period");

		}
		InSQLCreator c = new InSQLCreator();
		StringBuilder sbd = new StringBuilder();

		String pk_wa_class = psndata.getPk_wa_class();
		for (int i = 0; i < vos.length; i++) {
			sbd.append(" (cyear= '" + vos[i].getCreyear()
					+ "' and  cperiod = '" + vos[i].getCreperiod() + "' ");
			sbd.append("   and exists (select 1 from wa_data where pk_wa_class  = '"
					+ pk_wa_class
					+ "' and pk_psndoc = '"
					+ psndata.getPk_psndoc()
					+ "' and stopflag = 'N' and cyear = '"
					+ vos[i].getCreyear()
					+ "' and cperiod = '"
					+ vos[i].getCreperiod() + "'  ) )");

			if (i < vos.length - 1) {
				sbd.append(" or ");
			}
		}

		return sbd.toString();

	}

	private boolean canPayedTheItem(ReDataVO psnDataVo, String itemkey)
			throws DAOException {
		// 被补发期间是否有该补发项目,并且人员没有停发

		String temp = " select itemkey from wa_classitem  where pk_wa_class = '"
				+ psnDataVo.getPk_wa_class()
				+ "' and cyear = '"
				+ psnDataVo.getCreyear()
				+ "' and cperiod = '"
				+ psnDataVo.getCreperiod()
				+ "' and itemkey = '"
				+ itemkey
				+ "' ";
		StringBuffer sbf = new StringBuffer();
		sbf.append(" select count (pk_wa_data)  from wa_data where pk_wa_class  = '"
				+ psnDataVo.getPk_wa_class() + "' and stopflag = 'N'  ");
		// datavo 中不含有pk_psnjob
		sbf.append("  and  wa_data.pk_psndoc = '" + psnDataVo.getPk_psndoc()
				+ "' and cyear = '' and cperiod = '' and exists(" + temp
				+ ")  ");

		return isValueExist(temp);
	}

	/**
	 * 判断各补发期间是否已全部计算
	 * 
	 * @author liangxr on 2010-6-21
	 * @param waLoginVO
	 * @return
	 * @throws DAOException
	 */
	public boolean isAllCaculated(WaLoginVO waLoginVO) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer
				.append("select 1 from wa_redata where pk_wa_class_z = ? and cyear = ? and cperiod = ? ");
		sqlBuffer
				.append(" and creyear <>'-1' and creperiod<>'-1' and stopflag='N' and caculateflag='N'");

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(WaLoginVOHelper.getChildClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		return !isValueExist(sqlBuffer.toString(), parameter)
				&& !ArrayUtils.isEmpty(getRepayPeriods(waLoginVO, null));
	}

	/**
	 * 修改汇总标记
	 * 
	 * @author liangxr on 2010-6-22
	 * @param waLoginVO
	 * @param deptpower
	 * @throws DAOException
	 * @throws Exception
	 */
	public void updateReFlag(WaLoginVO waLoginVO, String deptpower)
			throws DAOException {

		String sql = " update wa_redata set reflag= 'Y' "
				+ " where wa_redata.pk_wa_class_z= ? and wa_redata.cyear= ? and wa_redata.cperiod= ? "
				+ " and rtrim(wa_redata.creyear)= '-1' and wa_redata.creperiod= '-1' ";
		sql += WherePartUtil.formatAddtionalWhere(deptpower);

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(WaLoginVOHelper.getChildClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		getBaseDao().executeUpdate(sql, parameter);

	}

	/**
	 * 累加到薪资发放
	 * 
	 * @author liangxr on 2010-6-21
	 * @param waLoginVO
	 * @throws BusinessException
	 */
	public void toChange(WaLoginVO waLoginVO, String tableName,
			String deptpower, String spart) throws BusinessException {

		StringBuffer sqlB = new StringBuffer();

		String pk_wa_class_z = waLoginVO.getPk_wa_class();
		String cyear_z = waLoginVO.getPeriodVO().getCyear();
		String cperiod_z = waLoginVO.getPeriodVO().getCperiod();

		// String pk_wa_class_x = WaLoginVOHelper.getParentClassPK(waLoginVO);
		// String cyear_x = waLoginVO.getPeriodVO().getCyear();
		// String cperiod_x = waLoginVO.getPeriodVO().getCperiod();
		sqlB.append(" update wa_data set ");
		if (getBaseDao().getDBType() == DBUtil.SQLSERVER) {
			if (tableName.equalsIgnoreCase("wa_data")) {
				// 代扣税: 补发金额=实发合计+本次扣税
				// 补发扣税 = 本次扣税
				// 代缴税：补发金额=实发合计
				// 补发扣税 = 本次扣税

				sqlB.append(" wa_data.f_8=redata.f_3+redata.f_5");// 补发金额=
																	// 所有的增项－所有的减项
				// = 实发合计+本次扣税

				sqlB.append(" , wa_data.f_9=redata.f_5, wa_data.caculateflag= 'N' ,wa_data.checkflag= 'N'");

			} else {
				// 从未走到这个地方？61 删除
				sqlB.append(" wa_data.f_9=redata.f_5 ");
			}
			sqlB.append(", redtotal = (" + spart + ") ");
			sqlB.append(" from ").append(tableName)
					.append(" , wa_redata  redata ");
			sqlB.append(" where wa_data.pk_wa_class= '" + pk_wa_class_z
					+ "' and wa_data.cyear= '" + cyear_z
					+ "' and wa_data.cperiod= '" + cperiod_z + "'");
			sqlB.append(" and redata.pk_wa_class_z= '"
					+ pk_wa_class_z
					+ "' and redata.cyear= wa_data.cyear and redata.cperiod= wa_data.cperiod ");
			// 如果发生人员调配，增加了任职记录。
			// sqlB.append(" and wa_data.pk_psnjob=redata.pk_psnjob ");
			sqlB.append(" and wa_data.pk_psndoc=redata.pk_psndoc ");
			sqlB.append(" and redata.creyear= '-1' and redata.creperiod= '-1' ");
			if (tableName.equalsIgnoreCase("wa_data")) {
				sqlB.append(" and wa_data.stopflag = 'N' ");
			}
			sqlB.append(WherePartUtil.formatAddtionalWhere(deptpower));
			getBaseDao().executeUpdate(sqlB.toString());

		} else {
			String sqlstrORA1 = "";
			String sqlstrORA2 = "";
			if (tableName.equalsIgnoreCase("wa_data")) {
				sqlstrORA1 += " wa_data.f_8, wa_data.f_9,wa_data.caculateflag,wa_data.checkflag,redtotal ";
				sqlstrORA2 += " nvl(redata.f_3+redata.f_5,0),nvl(redata.f_5,0),'N','N',("
						+ spart + ") ";

			} else {
				sqlstrORA1 += " wa_data.f_9,redtotal ";
				sqlstrORA2 += " nvl(redata.f_5,0),(" + spart + ") ";
			}
			sqlB.append(" (" + sqlstrORA1 + ")=(select " + sqlstrORA2);
			sqlB.append(" from  wa_redata  redata ");

			sqlB.append(" where redata.pk_wa_class_z='" + pk_wa_class_z + "' ");
			sqlB.append(" and redata.cyear= wa_data.cyear and redata.cperiod= wa_data.cperiod ");
			// 按照基本人员来添加人员档案
			// sqlB.append(" and wa_data.pk_psnjob=redata.pk_psnjob ");
			sqlB.append(" and wa_data.pk_psndoc=redata.pk_psndoc ");
			sqlB.append(" and redata.creyear= '-1' and redata.creperiod= '-1') ");
			sqlB.append(" where wa_data.pk_wa_class=  '" + pk_wa_class_z
					+ "'  and wa_data.cyear= '" + cyear_z
					+ "' and wa_data.cperiod= '" + cperiod_z + "' ");
			if (tableName.equalsIgnoreCase("wa_data")) {
				sqlB.append(" and wa_data.stopflag = 'N' ");
			}
			sqlB.append(" and wa_data.pk_psndoc in (select pk_psndoc from wa_redata where pk_wa_class_z = '"
					+ pk_wa_class_z
					+ "'"
					+ " and cyear = '"
					+ cyear_z
					+ "' and cperiod = '"
					+ cperiod_z
					+ "' and creyear='-1' and creperiod = '-1')");
			sqlB.append(WherePartUtil.formatAddtionalWhere(deptpower));
			getBaseDao().executeUpdate(sqlB.toString());
		}

		// 对于代缴税。更新 wa_data.f_8 = wa_data.f_8 - wa_data.f_9
		adjustF8(waLoginVO, tableName, deptpower, spart);

		// 调整 wa_data.f_8, wa_data.f_9 的小数位数
		updateF8F9Digits(waLoginVO, deptpower);

	}

	private void updateF8F9Digits(WaLoginVO waLoginVO, String deptpower)
			throws BusinessException {
		// 查询F8F9项目，并调整小数位数
		String pk_wa_class_z = waLoginVO.getPk_wa_class();
		String cyear_z = waLoginVO.getPeriodVO().getCyear();
		String cperiod_z = waLoginVO.getPeriodVO().getCperiod();

		IClassItemQueryService classitemquery = NCLocator.getInstance().lookup(
				IClassItemQueryService.class);
		WaClassItemVO[] ClassItemVOs = classitemquery.queryItemInfoVO(
				waLoginVO.getPk_org(), pk_wa_class_z, cyear_z, cperiod_z,
				" wa_classitem.itemkey in ('f_8','f_9')");

		StringBuffer whereCondition = new StringBuffer();
		whereCondition.append(" where wa_data.pk_wa_class= '" + pk_wa_class_z
				+ "' and wa_data.cyear= '" + cyear_z
				+ "' and wa_data.cperiod= '" + cperiod_z + "'");
		whereCondition.append(WherePartUtil.formatAddtionalWhere(deptpower));

		ArrayList<String> list = new ArrayList<String>();
		for (WaClassItemVO itemVO : ClassItemVOs) {

			int digits = itemVO.getIflddecimal();
			if (itemVO.getRound_type() == null
					|| itemVO.getRound_type().intValue() == 0) {// 四舍五入

				String sql = "update wa_data set wa_data."
						+ itemVO.getItemkey() + " = round((wa_data."
						+ itemVO.getItemkey() + "), " + digits + " )"
						+ whereCondition.toString();

				list.add(sql);
			} else {
				UFDouble f = UFDouble.ZERO_DBL;
				if (itemVO.getRound_type().intValue() == 1) {// 进位
					f = new UFDouble(0.4f);
				} else if (itemVO.getRound_type().intValue() == 2) {// 舍位操作
					f = new UFDouble(-0.5f);
				} else {// 默认四舍五入
					f = UFDouble.ZERO_DBL;
				}

				f = f.multiply(Math.pow(0.1, digits));
				String where = whereCondition.toString() + " and wa_data."
						+ itemVO.getItemkey() + " > 0";
				String sql = "update wa_data set wa_data."
						+ itemVO.getItemkey() + " = round(((wa_data."
						+ itemVO.getItemkey() + ") + " + f + "), " + digits
						+ " )" + where;

				String where1 = whereCondition + " and wa_data."
						+ itemVO.getItemkey() + " < 0";
				f = f.multiply(-1);
				String sql1 = "update wa_data set wa_data."
						+ itemVO.getItemkey() + " = round(((wa_data."
						+ itemVO.getItemkey() + ") + " + f + "), " + digits
						+ " )" + where1;

				list.add(sql);
				list.add(sql1);
			}
		}
		if (!list.isEmpty()) {
			executeSQLs(list.toArray(new String[0]));
		}

	}

	/**
	 * 对于代缴税，进行特殊调整
	 * 
	 * @param waLoginVO
	 * @param tableName
	 * @param deptpower
	 * @param spart
	 * @throws DAOException
	 */
	private void adjustF8(WaLoginVO waLoginVO, String tableName,
			String deptpower, String spart) throws DAOException {
		String pk_wa_class_z = waLoginVO.getPk_wa_class();
		String cyear_z = waLoginVO.getPeriodVO().getCyear();
		String cperiod_z = waLoginVO.getPeriodVO().getCperiod();

		// String pk_wa_class_x = WaLoginVOHelper.getParentClassPK(waLoginVO);
		// String cyear_x = waLoginVO.getPeriodVO().getCyear();
		// String cperiod_x = waLoginVO.getPeriodVO().getCperiod();
		StringBuffer sbf = new StringBuffer();

		StringBuilder whereconditon = new StringBuilder();

		whereconditon.append(" where wa_data.pk_wa_class=  '" + pk_wa_class_z
				+ "'  and wa_data.cyear= '" + cyear_z
				+ "' and wa_data.cperiod= '" + cperiod_z
				// wa_data.taxtype 代缴税 重要
				+ "'  and wa_data.taxtype = 1 ");
		if (tableName.equalsIgnoreCase("wa_data")) {
			whereconditon.append(" and wa_data.stopflag = 'N' ");
		}
		whereconditon
				.append(" and wa_data.pk_psndoc in (select pk_psndoc from wa_redata where pk_wa_class_z = '"
						+ pk_wa_class_z
						+ "'"
						+ " and cyear = '"
						+ cyear_z
						+ "' and cperiod = '"
						+ cperiod_z
						+ "' and creyear='-1' and creperiod = '-1')");
		whereconditon.append(WherePartUtil.formatAddtionalWhere(deptpower));

		sbf.append(" update wa_data set f_8 = f_8 - f_9 ");
		sbf.append(whereconditon);
		getBaseDao().executeUpdate(sbf.toString());

	}

	/**
	 * 自动补发
	 * 
	 * x-y-->Z
	 * 
	 * @author liangxr on 2010-6-22
	 * @param waLoginVO
	 * @param items
	 * @param inputyear
	 * @param inputperiod
	 * @param whereSql
	 * @throws DAOException
	 */
	public void autoRepay(WaLoginVO waLoginVO, WaClassItemVO[] items,
			String inputyear, String inputperiod, String whereSql)
			throws DAOException {

		String sqlItems = FormatVO.formatArrayToString(items, "itemkey", "");
		// 基准期间(inputyear,inputperiod)
		String sqla = "select pk_psndoc," + sqlItems
				+ " from wa_data where pk_wa_class=? and cyear=? and cperiod=?";

		// 补发期间基准值(reyear,reperiod)
		String sqlb = "select pk_psndoc,"
				+ sqlItems
				+ " from wa_data where pk_wa_class =? and cyear=? and cperiod=?";

		SQLParameter param = new SQLParameter();

		/**
		 * pk_wa_class: inputyear x.cyear inputperiod x.cperiod
		 * waLoginVO.getReyear() 补发年 y.cyear waLoginVO.getReperiod() 补发期间
		 * y.cperiod
		 * 
		 * waLoginVO.getPeriodVO().getCyear() : z.cyear
		 * waLoginVO.getPeriodVO().getCyear() : z.cperiod
		 * 
		 */

		// x
		String xpk_wa_class = getXpk_wa_class(waLoginVO);
		param.addParam(xpk_wa_class);
		param.addParam(inputyear);
		param.addParam(inputperiod);

		// y
		String ypk_wa_class = getYpk_wa_class(waLoginVO);
		param.addParam(ypk_wa_class);
		param.addParam(waLoginVO.getReyear());
		param.addParam(waLoginVO.getReperiod());

		// z
		String zpk_wa_class = getZpk_wa_class(waLoginVO);
		param.addParam(zpk_wa_class);
		param.addParam(waLoginVO.getPeriodVO().getCyear());
		param.addParam(waLoginVO.getPeriodVO().getCperiod());

		param.addParam(ypk_wa_class);
		param.addParam(waLoginVO.getReyear());
		param.addParam(waLoginVO.getReperiod());

		String sql = getUpdateSqlPart(xpk_wa_class, inputyear, inputperiod,
				items, sqla, sqlb, whereSql);

		this.getBaseDao().executeUpdate(sql, param);
	}

	private String getXpk_wa_class(WaLoginVO waLoginVO) {

		return waLoginVO.getPk_prnt_class();

	}

	private String getYpk_wa_class(WaLoginVO waLoginVO) {

		return waLoginVO.getPk_prnt_class();
	}

	private String getZpk_wa_class(WaLoginVO waLoginVO) {
		return waLoginVO.getPk_wa_class();
	}

	/**
	 * 不同数据库更新 语句不同
	 * 
	 * @author liangxr on 2010-7-26
	 * @param items
	 * @param sqla
	 * @param sqlb
	 * @param whereSql
	 * @return
	 */
	private String getUpdateSqlPart(String xpk_wa_class, String xyear,
			String xperiod, WaClassItemVO[] items, String sqla, String sqlb,
			String whereSql) {
		String sql = "update wa_redata set ";
		if (getBaseDao().getDBType() == DBUtil.SQLSERVER) {

			for (int i = 0; i < items.length; i++) {
				sql += "wa_redata." + items[i].getItemkey() + "= isnull(a."
						+ items[i].getItemkey() + "-b." + items[i].getItemkey()
						+ ",0)";

				if (i < items.length - 1) {
					sql += ",";
				}
			}

			sql += " from wa_redata ,("
					+ sqla
					+ ") a,("
					+ sqlb
					+ ") b "
					+ "where a.pk_psndoc = b.pk_psndoc and wa_redata.pk_psndoc=a.pk_psndoc "
					+ "and  wa_redata.pk_wa_class_z = ?  and wa_redata.cyear = ? and wa_redata.cperiod = ? "
					+ "and  wa_redata.pk_wa_class = ? and  wa_redata.creyear = ? and wa_redata.creperiod = ? ";
			sql += WherePartUtil.formatAddtionalWhere(whereSql);

		} else {
			String sqlstrORA1 = "";
			String sqlstrORA2 = "";

			for (int i = 0; i < items.length; i++) {
				sqlstrORA1 += "wa_redata." + items[i].getItemkey();
				sqlstrORA2 += " isnull(a." + items[i].getItemkey() + "-b."
						+ items[i].getItemkey() + ",0) ";
				if (i < items.length - 1) {
					sqlstrORA1 += ",";
					sqlstrORA2 += ",";
				}
			}

			sql += " (" + sqlstrORA1 + ")=(select " + sqlstrORA2;
			sql += " from ("
					+ sqla
					+ ") a,("
					+ sqlb
					+ ") b "
					+ "where a.pk_psndoc = b.pk_psndoc and wa_redata.pk_psndoc=a.pk_psndoc )"
					+ "where wa_redata.pk_wa_class_z = ? and wa_redata.cyear = ? and wa_redata.cperiod = ? "
					+ "  and wa_redata.pk_wa_class = ? and   wa_redata.creyear = ? and wa_redata.creperiod = ? "
					+ "  and  exists ( select pk_psndoc	from wa_data where	pk_wa_class = '"
					+ xpk_wa_class + "' and	cyear = '" + xyear
					+ "' and	cperiod = '" + xperiod
					+ "' and   wa_data.pk_psndoc = wa_redata.pk_psndoc ) ";
			sql += WherePartUtil.formatAddtionalWhere(whereSql);
		}
		return sql;
	}

	public boolean isValueExist(String pk_wa_class, String inputyear,
			String inputperiod) throws DAOException {
		String existSql = "select 1 from wa_data where pk_wa_class = ? and cyear=? and cperiod = ?";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_wa_class);
		param.addParam(inputyear);
		param.addParam(inputperiod);
		return isValueExist(existSql, param);
	}

	/**
	 * 更新补发计算状态
	 * 
	 * @author liangxr on 2010-6-28
	 * @throws DAOException
	 */
	public void updateCacuflag(WaLoginVO waLoginVO) throws DAOException {
		String sql = "update wa_redata set  wa_redata.caculateflag = 'Y' "
				+ "where pk_wa_class_z=? and cyear = ? and cperiod = ? and pk_wa_class=? and creyear=? and creperiod = ?";

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(WaLoginVOHelper.getChildClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(WaLoginVOHelper.getParentClassPK(waLoginVO));
		parameter.addParam(waLoginVO.getReyear());
		parameter.addParam(waLoginVO.getReperiod());

		getBaseDao().executeUpdate(sql, parameter);
	}

	public PeriodStateVO getRepayLastPeriod(WaLoginVO waLoginVo)
			throws BusinessException {

		String pk_wa_class = WaLoginVOHelper.getParentClassPK(waLoginVo);
		String reyear = waLoginVo.getReyear();
		String reperiod = waLoginVo.getReperiod();

		String pk_wa_class_z = WaLoginVOHelper.getChildClassPK(waLoginVo);
		String cyear = waLoginVo.getCyear();
		String cperiod = waLoginVo.getCperiod();

		StringBuilder sbd = new StringBuilder();
		// 20160116 xiejie3 NCdp205574655 多次发放补发，考虑补发历史
		// sbd.append(" select pk_wa_class_z as pk_wa_class, cyear,cperiod from 	wa_redata where	  pk_wa_class = ?  and wa_redata.creyear =? 	and creperiod = ?	"
		// );
		// sbd.append("  	and  	not exists   ( select 1 from   wa_redata itself where itself.pk_wa_class_z = ? AND 	itself.cyear = ? AND 	itself.cperiod = ? and itself.pk_wa_class = ? and itself.creyear = ? and itself.creperiod = ? and itself.pk_wa_redata = wa_redata.pk_wa_redata ) "
		// +
		// " and creyear <>'-1' 	and creperiod <> '-1'  order by cyear desc ,cperiod desc "
		// );
		sbd.append(" select pk_wa_class_z as pk_wa_class, wa_redata.cyear as cyear,wa_redata.cperiod  as cperiod from 	wa_redata    left  join wa_inludeclass  on  wa_inludeclass.PK_CHILDCLASS=  wa_redata.pk_wa_class_z  where	  pk_wa_class = ?  and wa_redata.creyear =? 	and creperiod = ?	");
		sbd.append("  	and  	not exists   ( select 1 from   wa_redata itself where itself.pk_wa_class_z = ? AND 	itself.cyear = ? AND 	itself.cperiod = ? and itself.pk_wa_class = ? and itself.creyear = ? and itself.creperiod = ? and itself.pk_wa_redata = wa_redata.pk_wa_redata ) "
				+ " and creyear <>'-1' 	and creperiod <> '-1'  order by cyear desc ,cperiod desc ,BATCH desc");
		// end NCdp205574655
		SQLParameter param = new SQLParameter();
		param.addParam(pk_wa_class);
		param.addParam(reyear);
		param.addParam(reperiod);

		param.addParam(pk_wa_class_z);
		param.addParam(cyear);
		param.addParam(cperiod);

		param.addParam(pk_wa_class);
		param.addParam(reyear);
		param.addParam(reperiod);

		return new BaseDAOManager().executeQueryVO(sbd.toString(), param,
				PeriodStateVO.class);

	}

}
