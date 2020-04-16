package nc.impl.ta.psncalendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.bd.baseservice.IBaseServiceConst;
import nc.bs.bd.pub.distribution.util.BDDistTokenUtil;
import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.businessevent.IEventType;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfo;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.execute.Executor;
import nc.bs.logging.Logger;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.itf.hrta.ILeaveextrarestMaintain;
import nc.itf.om.IAOSQueryService;
import nc.itf.ta.IPsnCalendarQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.md.data.access.NCObject;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.para.SysInitQuery;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.bd.workcalendrule.WorkCalendarRuleVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.changeshift.ChangeShiftCommonVO;
import nc.vo.ta.leaveextrarest.AggLeaveExtraRestVO;
import nc.vo.ta.leaveextrarest.LeaveExtraRestVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psncalendar.AggPsnCalendar;
import nc.vo.ta.psncalendar.PsnCalHoliday;
import nc.vo.ta.psncalendar.PsnCalendarVO;
import nc.vo.ta.psncalendar.PsnJobCalendarVO;
import nc.vo.ta.psncalendar.PsnWorkTimeVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.IMetaDataIDConst;
import nc.vo.ta.pub.PsnInSQLDateScope;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class PsnCalendarDAO {
	/**
	 * 返回人员在指定日期内的班次的map，key是人员基本档案主键，value是排班情况数组
	 * 按标准的数据，人员不在考勤档案范围内的日期是没有排班数据的，因此此方法做了容错处理：只返回考勤档案有效日期范围内的工作日历
	 * 2011.9.22已完成工作日历业务单元化的修改
	 * 由于V6.1将人员工作日历改为业务单元级的档案，即tbm_psncalendar记录的是业务单元主键，不是HR组织主键，
	 * 因此不能将HR组织主键pk_org作为工作日历表的过滤条件
	 * ，只能作为考勤档案的过滤条件。也不能将考勤档案的pk_org字段与工作日历的pk_org字段做关联
	 * 
	 * @param pk_hrorg
	 *            ,人力资源组织。如果为空，则查询结果穿透HR组织
	 * @param inSQL
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws MetaDataException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	Map<String, List<PsnCalendarVO>> queryCalendarVOListMapByPsndocInSQL(String pk_hrorg, String inSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws DAOException {
		// IMDPersistenceQueryService queryService =
		// MDPersistenceService.lookupPersistenceQueryService();
		// boolean hasPkOrg =
		// org.apache.commons.lang.StringUtils.isNotEmpty(pk_hrorg);
		SQLParameter para = new SQLParameter();
		// if(hasPkOrg)
		// para.addParam(pk_org);
		para.addParam(beginDate.toString());
		para.addParam(endDate.toString());
		// if(hasPkOrg)
		// para.addParam(pk_hrorg);
		String cond = "";
		// V6.1开始，工作日历以业务单元为主组织，因此此处不能用pk_org过滤，因为pk_org是HR组织
		// if(hasPkOrg)
		// cond+="pk_org=? and ";
		cond += "tbm_psncalendar.pk_psndoc in(" + inSQL
				+ ") and tbm_psncalendar.calendar between ? and ? and exists(select 1 from tbm_psndoc t where ";
		// if(hasPkOrg)
		// // cond+=" t.pk_org=pk_org and ";
		// cond+=" t.pk_org=? and ";
		cond += " t.pk_psndoc=tbm_psncalendar.pk_psndoc and tbm_psncalendar.calendar between t.begindate and t.enddate)";
		BaseDAO dao = new BaseDAO();
		dao.setMaxRows(500000);// 500人180天的数据超过了十万条，basedao默认只查询十万条，需要重新设置一下。
		Collection<PsnCalendarVO> c = dao.retrieveByClause(PsnCalendarVO.class, cond, para);
		if (CollectionUtils.isEmpty(c))
			return null;
		Map<String, List<PsnCalendarVO>> returnMap = new HashMap<String, List<PsnCalendarVO>>();
		for (PsnCalendarVO calendar : c) {
			if (returnMap.containsKey(calendar.getPk_psndoc())) {
				returnMap.get(calendar.getPk_psndoc()).add(calendar);
				continue;
			}
			List<PsnCalendarVO> list = new ArrayList<PsnCalendarVO>();
			list.add(calendar);
			returnMap.put(calendar.getPk_psndoc(), list);
		}
		return returnMap;
	}

	@SuppressWarnings("unchecked")
	public Map<String, List<PsnCalendarVO>> queryCalendarVOListMapByPsndocInSQL(String pk_hrorg, String inSQL,
			String dateInSql) throws DAOException {
		SQLParameter para = new SQLParameter();
		String cond = "";
		cond += "tbm_psncalendar.pk_psndoc in(" + inSQL + ") and tbm_psncalendar.calendar in(" + dateInSql
				+ ") and exists(select 1 from tbm_psndoc t where ";
		cond += " t.pk_psndoc=tbm_psncalendar.pk_psndoc and tbm_psncalendar.calendar between t.begindate and t.enddate)";
		Collection<PsnCalendarVO> c = new BaseDAO().retrieveByClause(PsnCalendarVO.class, cond, para);
		if (CollectionUtils.isEmpty(c))
			return null;
		Map<String, List<PsnCalendarVO>> returnMap = new HashMap<String, List<PsnCalendarVO>>();
		for (PsnCalendarVO calendar : c) {
			if (returnMap.containsKey(calendar.getPk_psndoc())) {
				returnMap.get(calendar.getPk_psndoc()).add(calendar);
				continue;
			}
			List<PsnCalendarVO> list = new ArrayList<PsnCalendarVO>();
			list.add(calendar);
			returnMap.put(calendar.getPk_psndoc(), list);
		}
		return returnMap;
	}

	/**
	 * key是pk_psndoc,value是这段日期内的所有工作日历list
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<String, List<PsnCalendarVO>> queryCalendarVOListMapByPsndocs(String pk_hrorg, String[] pk_psndocs,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs) || beginDate.afterDate(endDate))
			return null;
		// InSQLCreator isc = new InSQLCreator();
		// try{
		// String inSql = isc.getInSQL(pk_psndocs);
		// return queryCalendarVOListMapByPsndocInSQL(pk_hrorg, inSql,
		// beginDate, endDate);
		// } finally{
		// isc.clear();
		// }
		// 2014-07-30修改为批量执行，有的客户的数据量非常大，超过了数据库一次查询数据的限制，导致查询不不出来
		int length = pk_psndocs.length;
		InSQLCreator isc = new InSQLCreator();
		if (length < 1000) {
			String inSql = isc.getInSQL(pk_psndocs);
			return queryCalendarVOListMapByPsndocInSQL(pk_hrorg, inSql, beginDate, endDate);
		}
		Map<String, List<PsnCalendarVO>> retMap = new HashMap<String, List<PsnCalendarVO>>();
		int count = 0;
		List<String> psnList = new ArrayList<String>();
		for (int i = 0; i < length; i++) {
			count++;
			psnList.add(pk_psndocs[i]);
			if (count >= 999) {
				String inSql = isc.getInSQL(psnList.toArray(new String[0]));
				Map<String, List<PsnCalendarVO>> vos = queryCalendarVOListMapByPsndocInSQL(pk_hrorg, inSql, beginDate,
						endDate);
				if (MapUtils.isNotEmpty(vos)) {
					retMap.putAll(vos);
				}
				count = 0;
				psnList.clear();
			}
		}
		String inSql = isc.getInSQL(psnList.toArray(new String[0]));
		Map<String, List<PsnCalendarVO>> vos = queryCalendarVOListMapByPsndocInSQL(pk_hrorg, inSql, beginDate, endDate);
		if (MapUtils.isNotEmpty(vos)) {
			retMap.putAll(vos);
		}
		return retMap;
	}

	Map<String, Map<String, PsnCalendarVO>> queryCalendarVOMapByPsndocs(String pk_hrorg, String[] pk_psndocs,
			UFLiteralDate[] allDates) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs) || ArrayUtils.isEmpty(allDates))
			return null;
		InSQLCreator isc = new InSQLCreator();
		String inSql = isc.getInSQL(pk_psndocs);
		String[] dates = new String[allDates.length];
		for (int i = 0; i < allDates.length; i++) {
			dates[i] = allDates[i].toString();
		}
		InSQLCreator isc2 = new InSQLCreator();
		String datesInSql = isc2.getInSQL(dates);
		return queryCalendarVOMapByPsndocs(pk_hrorg, inSql, datesInSql);
	}

	/**
	 * key是pk_psndoc,value的key是日期，value是日历vo
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<String, Map<String, PsnCalendarVO>> queryCalendarVOMapByPsndocs(String pk_hrorg, String[] pk_psndocs,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs))
			return null;
		InSQLCreator isc = new InSQLCreator();
		try {
			String inSql = isc.getInSQL(pk_psndocs);
			return queryCalendarVOMapByPsndocs(pk_hrorg, inSql, beginDate, endDate);
		} finally {
			isc.clear();
		}
	}

	/**
	 * key是pk_psndoc,value的key是日期，value是日历vo
	 * 
	 * @param pk_org
	 * @param inSQL
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<String, Map<String, PsnCalendarVO>> queryCalendarVOMapByPsndocs(String pk_hrorg, String inSQL, String datesInSql)
			throws BusinessException {
		Map<String, List<PsnCalendarVO>> voMap = queryCalendarVOListMapByPsndocInSQL(pk_hrorg, inSQL, datesInSql);
		if (MapUtils.isEmpty(voMap))
			return null;
		Map<String, Map<String, PsnCalendarVO>> retMap = new HashMap<String, Map<String, PsnCalendarVO>>();
		for (String pk_psndoc : voMap.keySet()) {
			List<PsnCalendarVO> voList = voMap.get(pk_psndoc);
			if (CollectionUtils.isEmpty(voList))
				continue;
			Map<String, PsnCalendarVO> calendarMap = new HashMap<String, PsnCalendarVO>();
			retMap.put(pk_psndoc, calendarMap);
			for (PsnCalendarVO vo : voList) {
				calendarMap.put(vo.getCalendar().toString(), vo);
			}
		}
		return retMap;
	}

	/**
	 * key是pk_psndoc,value的key是日期，value是日历vo
	 * 
	 * @param pk_org
	 * @param inSQL
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	Map<String, Map<String, PsnCalendarVO>> queryCalendarVOMapByPsndocs(String pk_hrorg, String inSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		Map<String, List<PsnCalendarVO>> voMap = queryCalendarVOListMapByPsndocInSQL(pk_hrorg, inSQL, beginDate,
				endDate);
		if (MapUtils.isEmpty(voMap))
			return null;
		Map<String, Map<String, PsnCalendarVO>> retMap = new HashMap<String, Map<String, PsnCalendarVO>>();
		for (String pk_psndoc : voMap.keySet()) {
			List<PsnCalendarVO> voList = voMap.get(pk_psndoc);
			if (CollectionUtils.isEmpty(voList))
				continue;
			Map<String, PsnCalendarVO> calendarMap = new HashMap<String, PsnCalendarVO>();
			retMap.put(pk_psndoc, calendarMap);
			for (PsnCalendarVO vo : voList) {
				calendarMap.put(vo.getCalendar().toString(), vo);
			}
		}
		return retMap;
	}

	/**
	 * 返回人员在指定日期内的班次主键的map，key是人员基本档案主键，value是排班情况数组
	 * 按标准的数据，人员不在考勤档案范围内的日期是没有排班数据的
	 * 
	 * @param pk_org
	 * @param inSQL
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws DAOException
	 */
	Map<String, Map<String, String>> queryPkShiftMapByPsndocInSQL(String pk_hrorg, String inSQL,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws DAOException {
		Map<String, List<PsnCalendarVO>> voMap = queryCalendarVOListMapByPsndocInSQL(pk_hrorg, inSQL, beginDate,
				endDate);
		if (voMap == null || voMap.size() == 0)
			return null;
		Map<String, Map<String, String>> retMap = new HashMap<String, Map<String, String>>();
		for (String pk_psndoc : voMap.keySet()) {
			List<PsnCalendarVO> voList = voMap.get(pk_psndoc);
			if (CollectionUtils.isEmpty(voList))
				continue;
			Map<String, String> pkMap = new HashMap<String, String>();
			for (PsnCalendarVO vo : voList) {
				pkMap.put(vo.getCalendar().toString(), vo.getPk_shift());
			}
			retMap.put(pk_psndoc, pkMap);
		}
		return retMap;
	}

	/**
	 * key是pk_psndoc,value的key是日期，value是班次主键
	 * 
	 * @param pk_org
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, Map<String, String>> queryPkShiftMapByPsndocs(String pk_hrorg, String[] pk_psndocs,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs))
			return null;
		InSQLCreator isc = new InSQLCreator();
		try {
			String inSql = isc.getInSQL(pk_psndocs);
			return queryPkShiftMapByPsndocInSQL(pk_hrorg, inSql, beginDate, endDate);
		} finally {
			isc.clear();
		}
	}

	/**
	 * 根据主键数组删除工作日历
	 * 
	 * @param pk_psncalendars
	 * @throws BusinessException
	 */
	@SuppressWarnings("rawtypes")
	public void deleteByPkArray(String[] pk_psncalendars) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psncalendars))
			return;
		Collection col = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(AggPsnCalendar.class,
				pk_psncalendars, false);
		IMDPersistenceService service = MDPersistenceService.lookupPersistenceService();
		service.deleteBillByPK(AggPsnCalendar.class, pk_psncalendars);
		afterDelEvent(col);
	}

	// 删除后事件
	@SuppressWarnings("rawtypes")
	private void afterDelEvent(final Collection col) throws BusinessException {
		if (CollectionUtils.isNotEmpty(col)) {

			final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();
			new Executor(new Runnable() {
				@SuppressWarnings("unchecked")
				@Override
				public void run() {
					// 线程中环境信息会丢失，主动的设置一下
					BDDistTokenUtil.setInvocationInfo(invocationInfo);
					try {
						EventDispatcher.fireEvent(new BusinessEvent(IMetaDataIDConst.PSNCALENDAR,
								IEventType.TYPE_DELETE_AFTER, col.toArray(new AggPsnCalendar[0])));
					} catch (BusinessException e) {
						Logger.error(e.getMessage(), e);
					}
				}
			}).start();
		}
	}

	/**
	 * 保存工作日历时，对于用户设置的日期的班次，删除已有的记录
	 * 
	 * @param pk_org
	 * @param vos
	 * @throws BusinessException
	 */
	public void deleteExistsCalendarWhenSave(PsnJobCalendarVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;
		String psncalendarWhereSql = " where pk_psndoc = ? and calendar = ? ";
		String psncalendarInSql = "pk_psncalendar in (select pk_psncalendar from tbm_psncalendar "
				+ psncalendarWhereSql + ")";
		String deletePsnWorkSql = "delete from tbm_psncalwt where " + psncalendarInSql;
		String deltePsnCalHolSql = "delete from tbm_psncalholiday where " + psncalendarInSql;
		String deleteMainSql = "delete from tbm_psncalendar " + psncalendarWhereSql;

		JdbcSession session = null;
		try {
			session = new JdbcSession();
			List<SQLParameter> paraList = new ArrayList<SQLParameter>();
			for (PsnJobCalendarVO vo : vos) {
				Map<String, String> modifiedCalendarMap = vo.getModifiedCalendarMap();
				if (MapUtils.isEmpty(modifiedCalendarMap))
					continue;
				for (String date : modifiedCalendarMap.keySet()) {
					SQLParameter para = new SQLParameter();
					paraList.add(para);
					para.addParam(vo.getPk_psndoc());
					para.addParam(date);
				}
			}
			if (CollectionUtils.isEmpty(paraList))
				return;
			SQLParameter[] paras = paraList.toArray(new SQLParameter[0]);
			session.addBatch(deletePsnWorkSql, paras);
			session.addBatch(deltePsnCalHolSql, paras);
			session.addBatch(deleteMainSql, paras);
			session.executeBatch();
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			if (session != null)
				session.closeAll();
		}
		// 删除后事件
		afterDelVos(vos);
	}

	private void afterDelVos(final PsnJobCalendarVO[] vos) throws BusinessException {
		final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();
		new Executor(new Runnable() {
			@Override
			public void run() {
				// 线程中环境信息会丢失，主动的设置一下
				BDDistTokenUtil.setInvocationInfo(invocationInfo);
				try {
					EventDispatcher.fireEvent(new BusinessEvent(IMetaDataIDConst.PSNCALENDAR,
							IEventType.TYPE_DELETE_AFTER, vos));
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
				}
			}
		}).start();
	}

	/**
	 * 根据人员主键数组、日期范围删除工作日历
	 * 
	 * @param pk_org
	 *            ,业务单元主键
	 * @param pk_psndocs
	 * @param beginDate
	 * @param endDate
	 * @throws BusinessException
	 */
	public void deleteByPsndocsAndDateArea(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs))
			return;
		InSQLCreator isc = new InSQLCreator();
		String inSql = isc.getInSQL(pk_psndocs);
		PsnInSQLDateScope psnInSql = new PsnInSQLDateScope();
		psnInSql.setPk_org(pk_org);
		psnInSql.setPsndocInSQL(inSql);
		psnInSql.setBeginDate(beginDate);
		psnInSql.setEndDate(endDate);
		String psncalendarWhereSql = " where pk_org=? and calendar between ? and ? and pk_psndoc in(" + inSql + ") ";
		String psncalendarInSql = "pk_psncalendar in(select pk_psncalendar from tbm_psncalendar " + psncalendarWhereSql
				+ ")";
		String deletePsnWorkSql = "delete from tbm_psncalwt where pk_org=? and " + psncalendarInSql;
		String deltePsnCalHolSql = "delete from tbm_psncalholiday where pk_org=? and " + psncalendarInSql;
		String deleteMainSql = "delete from tbm_psncalendar " + psncalendarWhereSql;
		SQLParameter para = new SQLParameter();
		para.addParam(pk_org);
		para.addParam(pk_org);
		para.addParam(beginDate.toString());
		para.addParam(endDate.toString());
		BaseDAO dao = new BaseDAO();
		dao.executeUpdate(deletePsnWorkSql, para);// 删除工作时间段表
		dao.executeUpdate(deltePsnCalHolSql, para);// 删除假日相交表
		para.clearParams();
		para.addParam(pk_org);
		para.addParam(beginDate.toString());
		para.addParam(endDate.toString());
		dao.executeUpdate(deleteMainSql, para);// 删除主表
		// 删除后事件
		fireDelEvent(pk_org, pk_psndocs, beginDate, endDate);
	}

	private void fireDelEvent(final String pk_org, final String[] pk_psndocs, final UFLiteralDate beginDate,
			final UFLiteralDate endDate) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_psndocs))
			return;

		final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();
		new Executor(new Runnable() {
			@Override
			public void run() {
				// 线程中环境信息会丢失，主动的设置一下
				BDDistTokenUtil.setInvocationInfo(invocationInfo);
				try {
					InSQLCreator isc = new InSQLCreator();
					String inSql = isc.getInSQL(pk_psndocs);
					PsnInSQLDateScope psnInSql = new PsnInSQLDateScope();
					psnInSql.setPk_org(pk_org);
					psnInSql.setPsndocInSQL(inSql);
					psnInSql.setBeginDate(beginDate);
					psnInSql.setEndDate(endDate);
					EventDispatcher.fireEvent(new BusinessEvent(IMetaDataIDConst.PSNCALENDAR,
							IEventType.TYPE_DELETE_AFTER, psnInSql));
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
				}
			}
		}).start();

	}

	public void deleteByFromWhereAndDateArea(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		String pk_hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org).getPk_org();
		String[] pk_psndocs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryLatestPsndocsByCondition(pk_hrorg, fromWhereSQL, beginDate, endDate);
		if (ArrayUtils.isEmpty(pk_psndocs))
			return;
		deleteByPsndocsAndDateArea(pk_org, pk_psndocs, beginDate, endDate);
	}

	public String[] insert(AggPsnCalendar[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		// 校验7天一休
		// 排班校验7天要有一休
		checkShitAvailable(vos);
		IMDPersistenceService service = MDPersistenceService.lookupPersistenceService();
		NCObject[] ncObjs = new NCObject[vos.length];
		for (int i = 0; i < vos.length; i++) {
			ncObjs[i] = NCObject.newInstance(vos[i]);
		}
		String[] result = service.saveBill(ncObjs);
		// 新增后事件
		afterInsertEvent(vos);
		return result;
	}

	// 新增后事件
	private void afterInsertEvent(final AggPsnCalendar[] vos) throws BusinessException {

		final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();
		new Executor(new Runnable() {
			@Override
			public void run() {
				// 线程中环境信息会丢失，主动的设置一下
				BDDistTokenUtil.setInvocationInfo(invocationInfo);
				try {
					EventDispatcher.fireEvent(new BusinessEvent(IMetaDataIDConst.PSNCALENDAR,
							IEventType.TYPE_INSERT_AFTER, vos));
				} catch (BusinessException e) {
					Logger.error(e.getMessage(), e);
				}
			}
		}).start();

	}

	public void deleteByTBMPsndocVOs(TBMPsndocVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return;
		String psncalendarWhereSql = " where pk_psndoc = ? and calendar between ? and ? ";
		String psncalendarInSql = "pk_psncalendar in(select pk_psncalendar from tbm_psncalendar " + psncalendarWhereSql
				+ ")";
		String deletePsnWorkSql = "delete from tbm_psncalwt where  " + psncalendarInSql;
		String deltePsnCalHolSql = "delete from tbm_psncalholiday where " + psncalendarInSql;
		String deleteMainSql = "delete from tbm_psncalendar " + psncalendarWhereSql;
		JdbcSession session = null;
		try {
			session = new JdbcSession();
			List<SQLParameter> paraList = new ArrayList<SQLParameter>();
			for (TBMPsndocVO vo : vos) {
				SQLParameter para = new SQLParameter();
				paraList.add(para);
				para.addParam(vo.getPk_psndoc());
				para.addParam(vo.getBegindate().toString());
				para.addParam(vo.getEnddate().toString());
			}
			if (CollectionUtils.isEmpty(paraList))
				return;
			SQLParameter[] paras = paraList.toArray(new SQLParameter[0]);
			session.addBatch(deletePsnWorkSql, paras);// 删除工作时间段表
			session.addBatch(deltePsnCalHolSql, paras);// 删除假日相交表
			session.addBatch(deleteMainSql, paras);// 删除主表
			session.executeBatch();
			// 删除后事件
			EventDispatcher
					.fireEvent(new BusinessEvent(IMetaDataIDConst.PSNCALENDAR, IEventType.TYPE_DELETE_AFTER, vos));
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			if (session != null)
				session.closeAll();
		}
	}

	/**
	 * 根据人员日期范围map删除工作日历，key是人员主键，value是日期段数组
	 * 
	 * @param pk_org
	 * @param deleteCalendarMap
	 * @throws BusinessException
	 */
	public void deleteByDateScopeMap(String pk_org, Map<String, IDateScope[]> deleteCalendarMap)
			throws BusinessException {
		if (MapUtils.isEmpty(deleteCalendarMap))
			return;
		List<TBMPsndocVO> deleteVOList = new ArrayList<TBMPsndocVO>();
		for (String pk_psndoc : deleteCalendarMap.keySet()) {
			IDateScope[] dateScopes = deleteCalendarMap.get(pk_psndoc);
			if (ArrayUtils.isEmpty(dateScopes))
				continue;
			for (IDateScope dateScope : dateScopes) {
				TBMPsndocVO vo = new TBMPsndocVO();
				vo.setPk_psndoc(pk_psndoc);
				vo.setBegindate(dateScope.getBegindate());
				vo.setEnddate(dateScope.getEnddate());
				deleteVOList.add(vo);
			}
		}
		deleteByTBMPsndocVOs(deleteVOList.toArray(new TBMPsndocVO[0]));
	}

	@SuppressWarnings("unchecked")
	protected boolean checkShiftRef(String pk_shift) throws DAOException {
		BaseDAO dao = new BaseDAO();
		ShiftVO shiftVO = (ShiftVO) dao.retrieveByPK(ShiftVO.class, pk_shift);
		if (shiftVO == null || shiftVO.getPk_group().equals(shiftVO.getPk_org()))// 集团定义的班次模板不可能会被引用
			return false;
		String sql = "select top 1 1 from " + PsnCalendarVO.getDefaultTableName() + " where " + PsnCalendarVO.PK_SHIFT
				+ " = ? and " + IBaseServiceConst.PK_ORG_FIELD + "=?";
		SQLParameter para = new SQLParameter();
		para.addParam(shiftVO.getPrimaryKey());
		para.addParam(shiftVO.getPk_org());
		List<PsnCalendarVO> result = (List<PsnCalendarVO>) dao.executeQuery(sql, para, new BeanListProcessor(
				PsnCalendarVO.class));
		return !org.springframework.util.CollectionUtils.isEmpty(result);
	}

	/**
	 * 校验使用此班次的工作日历所在的期间是否已经封存
	 * 
	 * @param pk_shift
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	protected boolean checkPeriodSealed(String pk_shift) throws DAOException {
		BaseDAO dao = new BaseDAO();
		ShiftVO shiftVO = (ShiftVO) dao.retrieveByPK(ShiftVO.class, pk_shift);
		if (shiftVO == null || shiftVO.getPk_group().equals(shiftVO.getPk_org()))// 集团定义的班次模板不可能会被引用
			return false;
		String sql = "select top 1 1 from " + PsnCalendarVO.getDefaultTableName() + " where exists(select 1 from "
				+ PsnCalendarVO.getDefaultTableName() + " where " + PsnCalendarVO.PK_SHIFT
				+ " = ? and exists(select 1 from " + PeriodVO.getDefaultTableName() + " period where period."
				+ IBaseServiceConst.PK_ORG_FIELD + " =? and " + PsnCalendarVO.CALENDAR + " between "
				+ PeriodVO.BEGINDATE + " and " + PeriodVO.ENDDATE + " and period." + PeriodVO.SEALFLAG + "='Y'))";
		SQLParameter para = new SQLParameter();
		para.addParam(shiftVO.getPrimaryKey());
		para.addParam(shiftVO.getPk_org());
		List<PsnCalendarVO> result = (List<PsnCalendarVO>) dao.executeQuery(sql, para, new BeanListProcessor(
				PsnCalendarVO.class));
		return !org.springframework.util.CollectionUtils.isEmpty(result);
	}

	protected void deleteBeforeDeleteShift(String pk_shift) throws BusinessException {
		// 删除前事件
		EventDispatcher.fireEvent(new BusinessEvent(IMetaDataIDConst.PSNCALENDAR, IEventType.TYPE_DELETE_BEFORE,
				pk_shift));
		String inSQL = "select " + PsnCalendarVO.PK_PSNCALENDAR + " from " + PsnCalendarVO.getDefaultTableName()
				+ " where " + PsnCalendarVO.PK_SHIFT + "=?";
		String deleteSub1 = "delete from " + PsnWorkTimeVO.getDefaultTableName() + " where "
				+ PsnWorkTimeVO.PK_PSNCALENDAR + " in (" + inSQL + ")";
		String deleteSub2 = "delete from " + PsnCalHoliday.getDefaultTableName() + " where "
				+ PsnCalHoliday.PK_PSNCALENDAR + " in (" + inSQL + ")";
		String deleteMain = "delete from " + PsnCalendarVO.getDefaultTableName() + " where " + PsnCalendarVO.PK_SHIFT
				+ "=?";
		BaseDAO dao = new BaseDAO();
		SQLParameter para = new SQLParameter();
		para.addParam(pk_shift);
		dao.executeUpdate(deleteSub1, para);
		dao.executeUpdate(deleteSub2, para);
		dao.executeUpdate(deleteMain, para);
	}

	protected boolean existsCalendar(String pk_tbmpsndoc) throws BusinessException {
		if (org.apache.commons.lang.StringUtils.isBlank(pk_tbmpsndoc))
			return false;
		return existsCalendar(new String[] { pk_tbmpsndoc })[0];
	}

	@SuppressWarnings("unchecked")
	protected boolean[] existsCalendar(String[] pk_tbmpsndocs) throws BusinessException {
		if (ArrayUtils.isEmpty(pk_tbmpsndocs))
			return null;
		boolean[] retArray = new boolean[pk_tbmpsndocs.length];
		BaseDAO dao = new BaseDAO();
		InSQLCreator isc = null;
		try {
			isc = new InSQLCreator();
			String inSQL = isc.getInSQL(pk_tbmpsndocs);
			String sql0 = "select "
					+ TBMPsndocVO.PK_TBM_PSNDOC
					+ " from "
					+ TBMPsndocVO.getDefaultTableName()
					+ " psndoc where "
					+ TBMPsndocVO.PK_TBM_PSNDOC
					+ " in("
					+ inSQL
					+ ") and exists(select top 1 1 from "
					+ PsnCalendarVO.getDefaultTableName()
					+ " psncalendar "
					+ " where psncalendar.pk_psndoc=psndoc.pk_psndoc and calendar between psndoc.begindate and psndoc.enddate) ";
			List<String> result = (List<String>) dao.executeQuery(sql0, new ColumnListProcessor());
			if (CollectionUtils.isEmpty(result)) {
				for (int i = 0; i < retArray.length; i++) {
					retArray[i] = false;
				}
				return retArray;
			}
			Set<String> psndocSet = new HashSet<String>(result);
			for (int i = 0; i < retArray.length; i++) {
				retArray[i] = psndocSet.contains(pk_tbmpsndocs[i]);
			}
			return retArray;
		} finally {
			if (isc != null)
				isc.clear();
		}
	}

	/**
	 * 查询人员每一天的班次
	 * 
	 * @param vos
	 * @return <pk_psndoc,<date,pk_shift>>
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Map<UFLiteralDate, String>> queryPsnPkShift(ChangeShiftCommonVO[] vos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos))
			return null;
		BaseDAO dao = new BaseDAO();
		Map<String, Map<UFLiteralDate, String>> retMap = new HashMap<String, Map<UFLiteralDate, String>>();

		// 优化处理，一般批量调班的日期相同的可能性很大，这中情况可以优化sql查询次数
		IDateScope[] dateScopes = DateScopeUtils.mergeDateScopes(vos);
		// 选择人员没有选择日期前dateScopes=null
		if (ArrayUtils.isEmpty(dateScopes))
			return retMap;
		if (dateScopes.length == 1) {
			InSQLCreator isc = new InSQLCreator();
			String psnInSql = isc.getInSQL(vos, ChangeShiftCommonVO.PK_PSNDOC);
			String sql = "select " + PsnCalendarVO.PK_PSNDOC + "," + PsnCalendarVO.PK_SHIFT + ","
					+ PsnCalendarVO.CALENDAR + " from " + PsnCalendarVO.getDefaultTableName() + " where "
					+ PsnCalendarVO.PK_PSNDOC + " in (" + psnInSql + ") and " + PsnCalendarVO.CALENDAR + " between '"
					+ dateScopes[0].getBegindate() + "' and '" + dateScopes[0].getEnddate() + "'";
			List result = (List) dao.executeQuery(sql, new ArrayListProcessor());
			if (org.springframework.util.CollectionUtils.isEmpty(result))
				return retMap;
			for (Object o : result) {
				Object[] array = (Object[]) o;
				String pk_psndoc = (String) array[0];
				String pk_shift = (String) array[1];
				UFLiteralDate date = UFLiteralDate.getDate((String) array[2]);
				if (retMap.get(pk_psndoc) != null) {
					retMap.get(pk_psndoc).put(date, pk_shift);
				} else {
					Map<UFLiteralDate, String> tempMap = new HashMap<UFLiteralDate, String>();
					tempMap.put(date, pk_shift);
					retMap.put(pk_psndoc, tempMap);
				}
			}
			return retMap;
		}

		// 按人员分组
		Map<String, ChangeShiftCommonVO[]> psnGrpMap = CommonUtils.group2ArrayByField(ChangeShiftCommonVO.PK_PSNDOC,
				vos);
		String sql = "select " + PsnCalendarVO.PK_SHIFT + "," + PsnCalendarVO.CALENDAR + " from "
				+ PsnCalendarVO.getDefaultTableName() + " where " + PsnCalendarVO.PK_PSNDOC + "=? and "
				+ PsnCalendarVO.CALENDAR + " between ? and ?";
		SQLParameter para = new SQLParameter();
		for (String pk_psndoc : psnGrpMap.keySet()) {
			ChangeShiftCommonVO[] psnVOs = psnGrpMap.get(pk_psndoc);
			IDateScope[] mergeScopes = DateScopeUtils.mergeDateScopes(psnVOs);
			Map<UFLiteralDate, String> tempMap = new HashMap<UFLiteralDate, String>();
			retMap.put(pk_psndoc, tempMap);
			for (IDateScope mergeScope : mergeScopes) {
				para.clearParams();
				para.addParam(pk_psndoc);
				para.addParam(mergeScope.getBegindate());
				para.addParam(mergeScope.getEnddate());
				List result = (List) dao.executeQuery(sql, para, new ArrayListProcessor());
				if (org.springframework.util.CollectionUtils.isEmpty(result))
					continue;
				for (Object o : result) {
					Object[] array = (Object[]) o;
					tempMap.put(UFLiteralDate.getDate((String) array[1]), (String) array[0]);
				}
			}
		}
		return retMap;
	}

	// 默认排班方式修改
	public WorkCalendarRuleVO queryDefaultWorkCalendar() throws BusinessException {
		String sql1 = "select pk_workcalendrule from bd_workcalendar where isdefaultcalendar = 'Y'";
		String result = (String) new BaseDAO().executeQuery(sql1, new ColumnProcessor());

		if (result != null && !result.equals("")) {
			if (result.equals("1001A110000000000LQL")) {
				WorkCalendarRuleVO ruleVO = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPK(
						WorkCalendarRuleVO.class, result, false);
				return ruleVO;
			}
		}
		return null;
	}

	/**
	 * 排班校驗： 1. 排班時如是直接點選默認排班按鈕參照標準日曆進行排班，則不進行校驗 2.
	 * 如非點選默認排班時，則每一保存的操作皆校驗每七天需有1個公休。
	 * 
	 * @param values
	 */
	private void checkShitAvailable(AggPsnCalendar[] values) throws BusinessException {
		if (null == values || values.length <= 0) {
			return;
		}
		// 遍历数据得出,每个人的每天的日历天类型,以及此次排班的开始和结束时间 map<pk_psndoc,map<date,shift>>
		Map<String, Map<UFLiteralDate, String>> psnDateShiftMap = new HashMap<>();
		UFLiteralDate maxDate = new UFLiteralDate("1970-01-01 00:00:00");
		UFLiteralDate minDate = new UFLiteralDate("9999-12-31 23:59:59");
		String pk_org = null;
		Set<String> psnSet = new HashSet<>();
		for (AggPsnCalendar value : values) {
			PsnCalendarVO hVO = (PsnCalendarVO) value.getParentVO();
			if (hVO.getPk_psndoc() != null && hVO.getCalendar() != null) {
				if (hVO.getPk_org() != null) {
					pk_org = hVO.getPk_org();
				}

				// MOD by ssx for Force check switch on Taiwan Shift Checkings
				// on 2019-06-14
				UFBoolean isStrcheck = SysInitQuery.getParaBoolean(pk_org, "TWHRT03");
				if (isStrcheck != null && !isStrcheck.booleanValue()) {
					continue;
				}
				// end

				// 得出此次的日期范围
				psnSet.add(hVO.getPk_psndoc());
				if (hVO.getCalendar().after(maxDate)) {
					maxDate = hVO.getCalendar();
				}
				if (hVO.getCalendar().before(minDate)) {
					minDate = hVO.getCalendar();
				}

				// 封装此次的班次数据
				if (psnDateShiftMap.get(hVO.getPk_psndoc()) == null) {
					Map<UFLiteralDate, String> tempDateShiftMap = new HashMap<>();
					tempDateShiftMap.put(hVO.getCalendar(), hVO.getPk_shift());
					psnDateShiftMap.put(hVO.getPk_psndoc(), tempDateShiftMap);
				} else {
					Map<UFLiteralDate, String> tempDateShiftMap = psnDateShiftMap.get(hVO.getPk_psndoc());
					tempDateShiftMap.put(hVO.getCalendar(), hVO.getPk_shift());
					psnDateShiftMap.put(hVO.getPk_psndoc(), tempDateShiftMap);
				}

			}
		}
		if (psnSet.size() <= 0) {
			return;
		}
		// 查询开始结束时间前后7天的排班信息
		IPsnCalendarQueryService psnService = NCLocator.getInstance().lookup(IPsnCalendarQueryService.class);
		InSQLCreator insql = new InSQLCreator();
		String psndocsInSQL = insql.getInSQL(psnSet.toArray(new String[0]));

		Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnAfterMaxDateMap = psnService.queryCalendarVOByCondition(
				pk_org, maxDate, maxDate.getDateAfter(6), psndocsInSQL);
		Map<String, Map<UFLiteralDate, AggPsnCalendar>> psnBeforeMinDateMap = psnService.queryCalendarVOByCondition(
				pk_org, minDate.getDateBefore(6), minDate, psndocsInSQL);
		// 合并这些日期
		for (String pk_psndoc : psnSet) {
			if (psnAfterMaxDateMap != null && psnAfterMaxDateMap.get(pk_psndoc) != null) {
				Map<UFLiteralDate, AggPsnCalendar> afterMaxDateMap = psnAfterMaxDateMap.get(pk_psndoc);

				for (int i = 1; i < 7; i++) {
					if (afterMaxDateMap.get(maxDate.getDateAfter(i)) != null) {
						PsnCalendarVO hVO = (PsnCalendarVO) (afterMaxDateMap.get(maxDate.getDateAfter(i)))
								.getParentVO();
						if (null == hVO) {
							continue;
						}
						// 封装此次的班次数据
						if (psnDateShiftMap.get(hVO.getPk_psndoc()) == null) {
							Map<UFLiteralDate, String> tempDateShiftMap = new HashMap<>();
							tempDateShiftMap.put(hVO.getCalendar(), hVO.getPk_shift());
							psnDateShiftMap.put(hVO.getPk_psndoc(), tempDateShiftMap);
						} else {
							Map<UFLiteralDate, String> tempDateShiftMap = psnDateShiftMap.get(hVO.getPk_psndoc());
							tempDateShiftMap.put(hVO.getCalendar(), hVO.getPk_shift());
							psnDateShiftMap.put(hVO.getPk_psndoc(), tempDateShiftMap);
						}
					}
				}
			}
			if (psnBeforeMinDateMap != null && psnBeforeMinDateMap.get(pk_psndoc) != null) {
				Map<UFLiteralDate, AggPsnCalendar> beforeMaxDateMap = psnBeforeMinDateMap.get(pk_psndoc);

				for (int i = 1; i < 7; i++) {
					if (beforeMaxDateMap.get(minDate.getDateBefore(i)) != null) {
						PsnCalendarVO hVO = (PsnCalendarVO) (beforeMaxDateMap.get(minDate.getDateBefore(i)))
								.getParentVO();
						if (null == hVO) {
							continue;
						}
						// 封装此次的班次数据
						if (psnDateShiftMap.get(hVO.getPk_psndoc()) == null) {
							Map<UFLiteralDate, String> tempDateShiftMap = new HashMap<>();
							tempDateShiftMap.put(hVO.getCalendar(), hVO.getPk_shift());
							psnDateShiftMap.put(hVO.getPk_psndoc(), tempDateShiftMap);
						} else {
							Map<UFLiteralDate, String> tempDateShiftMap = psnDateShiftMap.get(hVO.getPk_psndoc());
							tempDateShiftMap.put(hVO.getCalendar(), hVO.getPk_shift());
							psnDateShiftMap.put(hVO.getPk_psndoc(), tempDateShiftMap);
						}
					}
				}
			}
		}
		// 校验每个人,每7天是否有一个公休日 map<pk_psndoc,map<date,shift>>
		for (String pk_psndoc : psnDateShiftMap.keySet()) {
			Map<UFLiteralDate, String> dateShift = psnDateShiftMap.get(pk_psndoc);
			// 当前区间
			UFLiteralDate beginDate = minDate.getDateBefore(6);
			UFLiteralDate endDate = beginDate.getDateAfter(6);

			// indexDate 当前遍历日期
			for (UFLiteralDate indexDate = (UFLiteralDate) beginDate.clone(); indexDate.compareTo(maxDate
					.getDateAfter(6)) <= 0; indexDate = indexDate.getDateAfter(1)) {
				// 找到公休日或未排班,则從下一天开始循环
				if (dateShift.get(indexDate) == null || ShiftVO.PK_GX.equals(dateShift.get(indexDate))) {
					beginDate = indexDate.getDateAfter(1);
					endDate = beginDate.getDateAfter(6);
					continue;
				}
				// 已经遍历到结尾了,还没有找到公休日,则报错
				if (indexDate.compareTo(endDate) == 0) {
					BaseDAO dao = new BaseDAO();
					throw new BusinessException("員工["
							+ dao.executeQuery("select code from bd_psndoc where pk_psndoc = '" + pk_psndoc + "'",
									new ColumnProcessor()) + "]排班,在期間[" + beginDate.toStdString() + "-"
							+ endDate.toStdString() + "],不滿足七天一休!");
				}
			}
		}
	}

	public static void generateExtraLeaves(String pk_hrorg, String[] psndocs, UFLiteralDate changeDate,
			Integer dateType, String changedayorhourStr) throws BusinessException {
		// 生成外加補休單據
		List<AggLeaveExtraRestVO> extraRestList = new ArrayList<AggLeaveExtraRestVO>();
		UFDouble changedayorhour = new UFDouble(0.00);
		if (!StringUtils.isEmpty(changedayorhourStr) && new Double(changedayorhourStr) > 0) {
			changedayorhour = new UFDouble(changedayorhourStr);
		}
		if (changedayorhour.compareTo(UFDouble.ZERO_DBL) >= 0) {
			for (String psndocStr : psndocs) {
				if (null != changeDate) {
					extraRestList.add(getExtraAggvo(pk_hrorg, new UFLiteralDate(), changeDate, changeDate,
							changedayorhour, psndocStr));
				}
			}
		}

		NCLocator.getInstance().lookup(ILeaveextrarestMaintain.class)
				.insert(extraRestList.toArray(new AggLeaveExtraRestVO[0]));
	}

	public static AggLeaveExtraRestVO getExtraAggvo(String pk_hrorg, UFLiteralDate billDate, UFLiteralDate firstDate,
			UFLiteralDate secondDate, UFDouble changedayorhour, String psndocStr) throws BusinessException {
		AggLeaveExtraRestVO saveVO = new AggLeaveExtraRestVO();
		LeaveExtraRestVO extraRestVO = new LeaveExtraRestVO();
		extraRestVO.setPk_psndoc(psndocStr);
		extraRestVO.setPk_org(pk_hrorg);
		Map<String, String> baseInfo = getPsnBaseInfo(psndocStr, pk_hrorg, firstDate);
		extraRestVO.setPk_org_v(baseInfo.get("pk_org_v"));
		extraRestVO.setPk_dept_v(baseInfo.get("pk_dept_v"));
		extraRestVO.setPk_group(baseInfo.get("pk_group"));
		extraRestVO.setBilldate(billDate);
		extraRestVO.setDatebeforechange(firstDate);
		extraRestVO.setTypebeforechange(getDateTypeByPsnDate(pk_hrorg, psndocStr, firstDate));
		extraRestVO.setDateafterchange(secondDate);
		extraRestVO.setTypeafterchange(getDateTypeByPsnDate(pk_hrorg, psndocStr, secondDate));
		extraRestVO.setChangetype(extraRestVO.getTypebeforechange());
		extraRestVO.setChangedayorhour(changedayorhour);
		extraRestVO.setCreationtime(new UFDateTime());
		extraRestVO.setExpiredate(getExpiredate(psndocStr, firstDate, extraRestVO.getBilldate()));
		saveVO.setParent(extraRestVO);
		return saveVO;
	}

	/**
	 * 查詢一下基本的信息
	 * 
	 * @param psndocStr
	 * @param pk_hrorg
	 * @return pk_org_v pk_dept_v pk_group
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, String> getPsnBaseInfo(String pk_psndoc, String pk_hrorg, UFLiteralDate checkDate)
			throws BusinessException {
		Map<String, String> resultMap = new HashMap<>();
		Collection<PsnJobVO> psnjobvos = new BaseDAO().retrieveByClause(PsnJobVO.class, "pk_psndoc='" + pk_psndoc
				+ "' and '" + checkDate + "' between begindate and isnull(enddate, '9999-12-31') and trnsevent<>4");
		if (psnjobvos != null && psnjobvos.size() > 0) {
			String pk_dept = psnjobvos.toArray(new PsnJobVO[0])[0].getPk_dept();
			String sqlStr = "select dept.pk_vid pk_dept_v, orgs.pk_vid pk_org_v ,orgs.pk_group pk_group"
					+ " from org_dept dept " + " left join org_orgs orgs on orgs.pk_org = '" + pk_hrorg
					+ "' where dept.pk_dept = '" + pk_dept + "'";
			resultMap = (Map<String, String>) new BaseDAO().executeQuery(sqlStr, new MapProcessor());
		}
		return resultMap == null ? new HashMap<String, String>() : resultMap;
	}

	private static Integer getDateTypeByPsnDate(String pk_hrorg, String pk_psndoc, UFLiteralDate checkDate)
			throws BusinessException {
		if (checkDate == null) {
			return null;
		}

		// 查出人员工作日历信息
		PsnCalendarDAO psnCalendarDAO = new PsnCalendarDAO();
		Map<String, Map<String, PsnCalendarVO>> dateTypeMap = psnCalendarDAO.queryCalendarVOMapByPsndocs(pk_hrorg,
				new String[] { pk_psndoc }, new UFLiteralDate[] { checkDate });
		if (dateTypeMap == null) {
			PsndocVO psndocvo = (PsndocVO) new BaseDAO().retrieveByPK(PsndocVO.class, pk_psndoc);
			throw new BusinessException("员工 [" + psndocvo.getCode() + "] 在 [" + checkDate.toString() + "] 未找到有效排班。");
		}
		PsnCalendarVO psnVo = dateTypeMap.get(pk_psndoc).get(checkDate.toString());
		return psnVo.getDate_daytype();
	}

	private static UFLiteralDate getExpiredate(String psndocStr, UFLiteralDate firstDate, UFLiteralDate billDate)
			throws BusinessException {
		UFLiteralDate maxLeaveDate = NCLocator.getInstance().lookup(ILeaveextrarestMaintain.class)
				.calculateExpireDateByWorkAge(psndocStr, firstDate, billDate);
		return maxLeaveDate;
	}
}
