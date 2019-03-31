package nc.impl.hrwa;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.SQLHelper;
import nc.itf.hrwa.IWadaysalaryService;
import nc.itf.ta.ITBMPsndocQueryMaintain;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.uapbd.IWorkCalendarPubService;
import nc.pubitf.uapbd.WorkCalendarPubUtil;
import nc.vo.bd.workcalendar.CalendarDateType;
import nc.vo.bd.workcalendar.WorkCalendarDateVO;
import nc.vo.bd.workcalendar.WorkCalendarVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hrwa.wadaysalary.DaySalaryEnum;
import nc.vo.hrwa.wadaysalary.DaySalaryVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.util.BDValueCheckUtil;
import nc.vo.wa.category.WaClassVO;

import org.apache.commons.lang.StringUtils;

public class WadaysalaryServiceImpl implements IWadaysalaryService {

	@Override
	public void calculSalaryByHrorg(String pk_hrorg, UFLiteralDate calculDate) throws BusinessException {
		// ��ȡ��Ҫ�������Ա������
		String calculDateStr = calculDate.toStdString();
		String pk_psndocs[] = getPkPsndocs(pk_hrorg, calculDateStr);
		if (pk_psndocs == null || pk_psndocs.length < 1) {
			nc.bs.logging.Logger.warn(pk_hrorg + "������֯�²����ڿ�����Ա");
			return;
		}
		// ��ѯ��ǰ��֯����Ҫ�̶����ڼ�����н��н�ʷ���
		String[] pk_wa_classs = getWaClassByOrg(pk_hrorg);
		// ��ѯ���������Ķ�������Ŀ
		String inpsndocsql = new InSQLCreator().getInSQL(pk_psndocs, true);
		String qrySql = "SELECT\n" + "	wadoc.pk_psndoc,\n" + "	wadoc.pk_psnjob,\n" + "	wadoc.pk_psndoc_sub,\n"
				+ "	wadoc.ts as wadocts,\n" + "	wadoc.pk_wa_item,\n" + "	wadoc.nmoney\n" + "FROM\n"
				+ "	hi_psndoc_wadoc wadoc\n" + "LEFT JOIN wa_item waitem ON wadoc.pk_wa_item = waitem.pk_wa_item\n"
				+ "WHERE\n" + "	wadoc.pk_psndoc IN (" + inpsndocsql + ")\n"
				+ "AND wadoc.waflag = 'Y'\n"// ���ű�־ΪY
				+ "AND wadoc.begindate <= '" + calculDateStr + "'\n" + "AND (\n" + "	wadoc.enddate >= '"
				+ calculDateStr + "'\n" + "	OR wadoc.enddate IS NULL\n" + ")";
		HashMap<Object, List<GeneralVO>> psndocWadocMap = executeQuery(qrySql);
		if (psndocWadocMap == null) {
			nc.bs.logging.Logger.warn("��ѯ���Ϊ��");
			return;
		}
		// ��ѯ��������-��н��������ȡֵ��ʽ
		int daynumtype = getSysintValue(pk_hrorg, DaySalaryEnum.DAYSYSINT);
		// ��н������
		List<DaySalaryVO> listDaySalaryVOs = new ArrayList<DaySalaryVO>();
		// ��н�ʷ������б���
		for (int i = 0; i < pk_wa_classs.length; i++) {
			String pk_wa_class = pk_wa_classs[i];
			// ��ȡ��н��������
			double daysalarynum = getDaySalaryNum(pk_hrorg, pk_wa_class, calculDate, daynumtype);
			// ������Ա����
			for (Map.Entry<Object, List<GeneralVO>> e : psndocWadocMap.entrySet()) {
				String pk_psndoc = e.getKey().toString();
				List<GeneralVO> listGeneralVOs = e.getValue();
				// ��ÿ���˵Ķ�������Ŀ����
				for (int j = 0, size = listGeneralVOs.size(); j < size; j++) {
					DaySalaryVO salaryVO = new DaySalaryVO();
					GeneralVO generalVO = listGeneralVOs.get(j);
					salaryVO.setSalarydate(calculDate);
					salaryVO.setPk_wa_class(pk_wa_class);
					salaryVO.setCyear(calculDate.getYear());
					salaryVO.setCperiod(calculDate.getMonth());
					salaryVO.setPk_hrorg(pk_hrorg);
					salaryVO.setPk_psndoc(pk_psndoc);
					salaryVO.setPk_psndoc_sub(generalVO.getAttributeValue("pk_psndoc_sub").toString());
					salaryVO.setWadocts(new UFDateTime(generalVO.getAttributeValue("wadocts").toString()));
					salaryVO.setPk_psnjob(generalVO.getAttributeValue("pk_psnjob").toString());
					salaryVO.setPk_wa_item(generalVO.getAttributeValue("pk_wa_item").toString());
					UFDouble nmoney = generalVO.getAttributeValue("nmoney") != null ? new UFDouble(generalVO
							.getAttributeValue("nmoney").toString()) : UFDouble.ZERO_DBL;
					// ��������н��ʱн
					UFDouble daysalary = nmoney.div(daysalarynum);
					UFDouble hoursalary = daysalary.div(DaySalaryEnum.HOURSALARYNUM);
					if (daynumtype == DaySalaryEnum.DAYNUMTYPE1) {// н���ڼ��н������
						if (!checkWorkCalendar(calculDate, pk_hrorg)) {
							// ������������ǡ��ݡ�������нΪ0
							daysalary = UFDouble.ZERO_DBL;
							hoursalary = UFDouble.ZERO_DBL;
						}
					}
					salaryVO.setDaysalary(daysalary);
					salaryVO.setHoursalary(hoursalary);
					salaryVO.setInitcode(daynumtype);
					listDaySalaryVOs.add(salaryVO);
				}
			}
		}
		// �ڲ��딵��֮ǰ�������̎��
		getDao().deleteByClause(DaySalaryVO.class,
				"pk_hrorg='" + pk_hrorg + "' and salarydate='" + calculDate.toStdString() + "'");
		getDao().insertVOList(listDaySalaryVOs);
		// ����Ƿ�����Ҫ��������ݣ�������
		checkDaysalaryAndRecalculate(pk_hrorg, calculDate);

	}

	@Override
	public void calculSalaryByWaItem(String pk_hrorg, String pk_wa_class, UFLiteralDate calculDate, String pk_psndoc,
			String[] pk_wa_items) throws BusinessException {
		// ��ȡ��Ҫ�������Ա������
		String calculDateStr = calculDate.toStdString();
		int daynumtype = getSysintValue(pk_hrorg, DaySalaryEnum.DAYSYSINT);
		double daysalarynum = getDaySalaryNum(pk_hrorg, pk_wa_class, calculDate, daynumtype);
		// ��ѯ���������Ķ�������Ŀ
		String inwaitemsql = new InSQLCreator().getInSQL(pk_wa_items, true);
		String qrySql = "SELECT\n" + "	wadoc.pk_psndoc,\n" + "	wadoc.pk_psnjob,\n" + "	wadoc.pk_psndoc_sub,\n"
				+ "	wadoc.ts as wadocts,\n" + "	wadoc.pk_wa_item,\n" + "	wadoc.nmoney\n" + "FROM\n"
				+ "	hi_psndoc_wadoc wadoc\n" + "LEFT JOIN wa_item waitem ON wadoc.pk_wa_item = waitem.pk_wa_item\n"
				+ "WHERE\n" + "	wadoc.pk_psndoc = '" + pk_psndoc
				+ "'\n"
				+ "AND wadoc.waflag = 'Y'\n"// ���ű�־ΪY
				+ "AND wadoc.begindate <= '" + calculDateStr + "'\n" + "AND (\n" + "	wadoc.enddate >= '"
				+ calculDateStr + "'\n" + "	OR wadoc.enddate IS NULL\n" + ")\n" + "AND wadoc.pk_wa_item in ("
				+ inwaitemsql + ")";
		HashMap<Object, List<GeneralVO>> psndocWadocMap = executeQuery(qrySql);
		if (psndocWadocMap == null) {
			nc.bs.logging.Logger.warn("��ѯ���Ϊ��");
			return;
		}
		List<DaySalaryVO> listDaySalaryVOs = new ArrayList<DaySalaryVO>();
		for (Map.Entry<Object, List<GeneralVO>> e : psndocWadocMap.entrySet()) {
			List<GeneralVO> listGeneralVOs = e.getValue();
			for (int i = 0, size = listGeneralVOs.size(); i < size; i++) {
				GeneralVO generalVO = listGeneralVOs.get(i);
				DaySalaryVO salaryVO = new DaySalaryVO();
				salaryVO.setSalarydate(calculDate);
				salaryVO.setPk_wa_class(pk_wa_class);
				salaryVO.setCyear(calculDate.getYear());
				salaryVO.setCperiod(calculDate.getMonth());
				salaryVO.setPk_hrorg(pk_hrorg);
				salaryVO.setPk_psndoc(pk_psndoc);
				salaryVO.setPk_psndoc_sub(generalVO.getAttributeValue("pk_psndoc_sub").toString());
				salaryVO.setWadocts(new UFDateTime(generalVO.getAttributeValue("wadocts").toString()));
				salaryVO.setPk_psnjob(generalVO.getAttributeValue("pk_psnjob").toString());
				salaryVO.setPk_wa_item(generalVO.getAttributeValue("pk_wa_item").toString());
				salaryVO.setInitcode(daynumtype);
				UFDouble nmoney = generalVO.getAttributeValue("nmoney") != null ? new UFDouble(generalVO
						.getAttributeValue("nmoney").toString()) : UFDouble.ZERO_DBL;
				// ��������н��ʱн
				UFDouble daysalary = nmoney.div(daysalarynum);
				UFDouble hoursalary = daysalary.div(DaySalaryEnum.HOURSALARYNUM);
				if (daynumtype == DaySalaryEnum.DAYNUMTYPE1) {// н���ڼ��н������
					if (!checkWorkCalendar(calculDate, pk_hrorg)) {
						// ������������ǡ��ݡ�������нΪ0
						daysalary = UFDouble.ZERO_DBL;
						hoursalary = UFDouble.ZERO_DBL;
					}
				}
				salaryVO.setDaysalary(daysalary);
				salaryVO.setHoursalary(hoursalary);
				listDaySalaryVOs.add(salaryVO);
			}
		}
		// �ڲ��딵��֮ǰ�������̎��
		getDao().deleteByClause(
				DaySalaryVO.class,
				"pk_hrorg='" + pk_hrorg + "' and pk_wa_class='" + pk_wa_class + "' and pk_psndoc = '" + pk_psndoc
						+ "' and salarydate='" + calculDate.toStdString() + "' and pk_wa_item in (" + inwaitemsql + ")");
		getDao().insertVOList(listDaySalaryVOs);

	}

	/**
	 * ���ĳ����֯�������н�����Ƿ���Ҫ���㣬������
	 * 
	 * @param pk_org
	 * @param calculdate
	 * @throws BusinessException
	 */
	public void checkDaysalaryAndRecalculate(String pk_org, UFLiteralDate calculdate) throws BusinessException {
		String checkSql = "SELECT  \n" + "	daysalary.salarydate,\n" + "	daysalary.pk_wa_class,\n"
				+ "	daysalary.pk_psndoc,\n" + "	daysalary.pk_wa_item\n" + "FROM\n" + "	wa_daysalary daysalary\n"
				+ "LEFT JOIN hi_psndoc_wadoc wadoc ON (\n" + "	daysalary.pk_psndoc = wadoc.pk_psndoc\n"
				+ "	AND daysalary.pk_wa_item = wadoc.pk_wa_item\n" + "	AND daysalary.salarydate >= wadoc.begindate\n"
				+ "	AND (\n" + "		daysalary.salarydate <= wadoc.enddate\n" + "		OR wadoc.enddate IS NULL\n" + "	)\n"
				+ ")\n" + "  left join pub_sysinit sysinit on (tbmpsndoc.pk_org = '"
				+ pk_org
				+ "' and sysinit.dr = 0 and sysinit.initcode = 'HRWA021')  "
				+ "WHERE\n"
				+ "	daysalary.salarydate < '"
				+ calculdate.toStdString()
				+ "'\n"
				+ "AND (daysalary.wadocts <> wadoc.ts OR salary.wadocts IS NULL or salary.initcode <> sysinit.value or salary.initcode  IS NULL)\n"
				+ "AND daysalary.pk_hrorg = '"
				+ pk_org
				+ "'\n"
				+ "UNION ALL\n"
				+ "	SELECT\n"
				+ "		daysalary.salarydate,\n"
				+ "		daysalary.pk_wa_class,\n"
				+ "		daysalary.pk_psndoc,\n"
				+ "		daysalary.pk_wa_item\n"
				+ "	FROM\n"
				+ "		wa_daysalary daysalary\n"
				+ "	LEFT JOIN hi_psndoc_wadoc wadoc ON daysalary.pk_psndoc_sub = wadoc.pk_psndoc_sub\n"
				+ "	WHERE\n"
				+ "		daysalary.pk_hrorg = '"
				+ pk_org
				+ "'\n"
				+ "	AND daysalary.salarydate < '"
				+ calculdate.toStdString() + "'\n" + "	AND wadoc.pk_psndoc_sub IS NULL";
		HashMap<String, HashMap<String, HashMap<String, List<String>>>> checkresultHashMap = executeQuery2(checkSql);
		if (checkresultHashMap.size() < 1) {
			return;
		}
		for (Map.Entry<String, HashMap<String, HashMap<String, List<String>>>> e : checkresultHashMap.entrySet()) {
			String key = e.getKey();
			UFLiteralDate reCalculdate = new UFLiteralDate(key);
			HashMap<String, HashMap<String, List<String>>> reWaClassMap = e.getValue();
			for (Map.Entry<String, HashMap<String, List<String>>> e2 : reWaClassMap.entrySet()) {
				String pk_wa_class = e2.getKey();
				HashMap<String, List<String>> reRsndocAndWaitems = e2.getValue();
				for (Map.Entry<String, List<String>> e3 : reRsndocAndWaitems.entrySet()) {
					String pk_psndoc = e3.getKey();
					String[] waitems = e3.getValue().toArray(new String[0]);
					calculSalaryByWaItem(pk_org, pk_wa_class, reCalculdate, pk_psndoc, waitems);
				}
			}
		}
	}

	/**
	 * ���ü��������ǡ�ƽ/�ݡ�
	 * 
	 * @param calculDate
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public boolean checkWorkCalendar(UFLiteralDate calculDate, String pk_org) throws BusinessException {
		WorkCalendarVO calendarVO = WorkCalendarPubUtil.getInstance().getWorkCalendarVOByPkOrg(pk_org);
		if ((calendarVO == null) || (BDValueCheckUtil.isNullORZeroLength(calendarVO.getCalendardates()))) {
			Logger.error("δ��ѯ����Ч�Ĺ�������");
			return false;
		}
		for (WorkCalendarDateVO dataVO : calendarVO.getCalendardates()) {
			if (dataVO.getCalendardate().isSameDate(calculDate)
					&& (CalendarDateType.WEEKENDDAY.ordinal() != dataVO.getDatetype().intValue())) {

				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * ��ȡ��Ҫ������н����Ա�����ݿ��ڵ������в�ѯ
	 * 
	 * @param pk_hrorg
	 * @param calculDate
	 * @return
	 * @throws BusinessException
	 */
	public String[] getPkPsndocs(String pk_hrorg, String calculDate) throws BusinessException {
		String condition = "pk_org ='" + pk_hrorg + "' and begindate<='" + calculDate
				+ "' and isnull(enddate, '9999-12-31')>='" + calculDate + "' and isnull(dr,0)=0";
		ITBMPsndocQueryMaintain service = NCLocator.getInstance().lookup(ITBMPsndocQueryMaintain.class);
		TBMPsndocVO[] tbmPsndocVOs = service.queryByCondition(condition);
		String[] psndocpks = SQLHelper.getStrArray(tbmPsndocVOs, TBMPsndocVO.PK_PSNDOC);
		return psndocpks;
	}

	/**
	 * ������֯����ѯ����Ҫ������н��н�ʷ���
	 * 
	 * @param pk_hrorg
	 * @return
	 * @throws BusinessException
	 */
	public String[] getWaClassByOrg(String pk_hrorg) throws BusinessException {
		String qrySql = "SELECT\n" + "	pk_wa_class\n" + "FROM\n" + "	wa_waclass\n" + "WHERE\n" + "	pk_org = '"
				+ pk_hrorg + "'\n" + "AND stopflag = 'N'\n" + "AND isdaysalary = 'Y'\n" + "AND isnull(dr, 0) = 0";
		@SuppressWarnings("unchecked")
		List<String> waClassList = (List<String>) getDao().executeQuery(qrySql, new ColumnListProcessor());
		if (waClassList == null || waClassList.size() < 1) {
			throw new BusinessException("�M����" + pk_hrorg + ",δ�O����Ҫ�̶��L��Ӌ����н��н�Y����");
		}
		return waClassList.toArray(new String[0]);
	}

	public Map<String, UFLiteralDate> getPeriodDate(String pk_hrorg, String pk_wa_class, UFLiteralDate calculdate)
			throws BusinessException {
		String qrySql = "SELECT\n" + "	period.cstartdate,\n" + "	period.cenddate\n" + "FROM\n"
				+ "	wa_waclass waclass\n"
				+ "LEFT JOIN wa_period period ON period.pk_periodscheme = waclass.pk_periodscheme\n" + "WHERE\n"
				+ "	waclass.pk_wa_class = '" + pk_wa_class + "'\n" + "AND period.cstartdate <= '"
				+ calculdate.toStdString() + "'\n" + "AND period.cenddate >= '" + calculdate.toStdString() + "'";
		@SuppressWarnings("unchecked")
		List<HashMap<String, Object>> listMaptemp = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(qrySql,
				new MapListProcessor());
		if (listMaptemp != null && listMaptemp.size() > 0) {
			HashMap<String, Object> hashMap = listMaptemp.get(0);
			String begindate = hashMap.get("cstartdate").toString();
			String enddate = hashMap.get("cenddate").toString();
			Map<String, UFLiteralDate> map = new HashMap<String, UFLiteralDate>();
			map.put("begindate", new UFLiteralDate(begindate));
			map.put("enddate", new UFLiteralDate(enddate));
			return map;
		} else {
			StringBuffer message = new StringBuffer();
			message.append("�M����" + pk_hrorg + "\n");
			message.append("н�ʷ�����" + pk_wa_class + "\n");
			message.append("Ӌ�����ڣ�" + calculdate.toStdString() + "\n");
			message.append("��S�oн���ڼ�");
			throw new BusinessException(message.toString());
		}

	}

	/**
	 * ��ѯ����ֵ
	 * 
	 * @param pk_org
	 * @param initcode
	 * @return
	 * @throws DAOException
	 */
	public int getSysintValue(String pk_org, String initcode) throws DAOException {
		String qrySql = "select value from pub_sysinit where initcode='" + initcode + "' and pk_org ='" + pk_org
				+ "' and isnull(dr,0)=0";
		Object object = getDao().executeQuery(qrySql, new ColumnProcessor());
		int sysValue = 0;
		try {
			sysValue = Integer.valueOf(object.toString());
		} catch (Exception e) {

			nc.bs.logging.Logger.error("sql:" + qrySql + "\n result:" + object + "\n initcode:" + initcode
					+ "\n ����ʱн����������н��������");
		}
		return sysValue;
	}

	// /**
	// * ȡ����ʱн����ȡֵ��ʽʱ��
	// * @param pk_hrorg
	// * @param calculDate
	// * @param tbmnumtype
	// * @return
	// * @throws BusinessException
	// */
	// public double getTbmSalaryNum(String pk_hrorg, UFLiteralDate calculDate,
	// int tbmnumtype) throws BusinessException {
	// if (tbmnumtype == DaySalaryEnum.TBMNUMTYPE1) {
	// return DaySalaryEnum.TBMSALARYNUM01;////�̶�ֵ30��
	// }
	// if (tbmnumtype == DaySalaryEnum.TBMNUMTYPE2) {
	// return DaySalaryEnum.TBMSALARYNUM02;//�̶�21.75��
	// }
	// if (tbmnumtype == DaySalaryEnum.TBMNUMTYPE3) {
	// // ��ѯ�����ڼ�
	// String sqlsys = "SELECT\n" +
	// "	begindate,\n" +
	// "	enddate\n" +
	// "FROM\n" +
	// "	tbm_period\n" +
	// "WHERE\n" +
	// "	begindate <= '"+calculDate+"'\n" +
	// "AND enddate >= '"+calculDate+"'\n" +
	// "AND pk_org = '"+pk_hrorg+"'\n" +
	// "AND isnull(dr, 0) = 0";
	// @SuppressWarnings("unchecked")
	// List<HashMap<String, Object>> listMaptemp = (ArrayList<HashMap<String,
	// Object>>) getDao()
	// .executeQuery(sqlsys.toString(), new MapListProcessor());
	// String begindate = null;
	// String enddate = null;
	// if (listMaptemp != null && listMaptemp.size() > 0) {
	// HashMap<String, Object> hashMap = listMaptemp.get(0);
	// begindate = hashMap.get("begindate").toString();
	// enddate = hashMap.get("enddate").toString();
	// } else {
	// StringBuffer message=new StringBuffer();
	// message.append("�M����"+pk_hrorg+"\n");
	// message.append("Ӌ�����ڣ�"+calculDate.toStdString()+"\n");
	// message.append("��S�o�������g");
	// throw new BusinessException(message.toString());
	// }
	// return UFLiteralDate.getDaysBetween(new UFLiteralDate(begindate),
	// new UFLiteralDate(enddate)) + 1;
	// }
	// return DaySalaryEnum.TBMSALARYNUM01;//�̶�ֵ30��;
	// }

	/**
	 * ȡ��н��������ȡֵ
	 * 
	 * @param pk_hrorg
	 *            ��֯
	 * @param pk_wa_class
	 *            н�ʷ���
	 * @param calculDate
	 *            ��������
	 * @param daysalarynumtype
	 *            ���Ʋ���
	 * @return
	 * @throws BusinessException
	 */
	public double getDaySalaryNum(String pk_hrorg, String pk_wa_class, UFLiteralDate calculDate, int daysalarynumtype)
			throws BusinessException {
		// н���ڼ��н������������+ƽ�գ�
		if (daysalarynumtype == DaySalaryEnum.DAYNUMTYPE1) {
			Map<String, UFLiteralDate> periodMap = getPeriodDate(pk_hrorg, pk_wa_class, calculDate);
			UFDouble temp = getCalendarPubService().getWorkCalndPsnWageDays(pk_hrorg, periodMap.get("begindate"),
					periodMap.get("enddate"));
			return temp.toDouble();
		}
		// н���ڼ�����
		if (daysalarynumtype == DaySalaryEnum.DAYNUMTYPE2) {
			Map<String, UFLiteralDate> periodMap = getPeriodDate(pk_hrorg, pk_wa_class, calculDate);
			return UFLiteralDate.getDaysBetween(periodMap.get("begindate"), periodMap.get("enddate")) + 1;
		}
		// �̶�30��
		if (daysalarynumtype == DaySalaryEnum.DAYNUMTYPE3) {
			return DaySalaryEnum.DAYSAYSALARYNUM03;
		}
		return DaySalaryEnum.DAYSAYSALARYNUM03;
	}

	private BaseDAO dao;

	public BaseDAO getDao() {
		if (null == dao) {
			dao = new BaseDAO();
		}
		return dao;
	}

	private IWorkCalendarPubService calendarPubService;

	private IWorkCalendarPubService getCalendarPubService() {
		if (null == calendarPubService) {
			calendarPubService = NCLocator.getInstance().lookup(IWorkCalendarPubService.class);
		}
		return calendarPubService;
	}

	@SuppressWarnings({ "unchecked" })
	private HashMap<Object, List<GeneralVO>> executeQuery(String qrysql) throws DAOException {

		HashMap<Object, List<GeneralVO>> param1ParamsMap = (HashMap<Object, List<GeneralVO>>) getDao().executeQuery(
				qrysql, new ResultSetProcessor() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -6223698366816831149L;

					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {
						HashMap<Object, List<GeneralVO>> retMap = new HashMap<Object, List<GeneralVO>>();

						while (rs.next()) {

							List<GeneralVO> listGeneralVOs = retMap.get(rs.getObject(1));

							if (listGeneralVOs == null) {

								listGeneralVOs = new ArrayList<GeneralVO>();

								listGeneralVOs.add(processorToGeneralVO(rs));
								retMap.put(rs.getObject(1), listGeneralVOs);
							} else {

								listGeneralVOs.add(processorToGeneralVO(rs));
							}

						}

						return retMap;
					}
				});

		return param1ParamsMap;
	}

	/**
	 * HashMap<String,HashMap<String, List<String>>>
	 * HashMap<����ʱ��,HashMap<pk_wa_class,HashMap<pk_psndoc, List<pk_wa_item>>>>
	 * 
	 * @param qrysql
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, HashMap<String, HashMap<String, List<String>>>> executeQuery2(String qrysql)
			throws DAOException {

		HashMap<String, HashMap<String, HashMap<String, List<String>>>> param1ParamsMap = (HashMap<String, HashMap<String, HashMap<String, List<String>>>>) getDao()
				.executeQuery(qrysql, new ResultSetProcessor() {

					/**
			 * 
			 */
					private static final long serialVersionUID = -4700240988126059729L;

					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {
						HashMap<String, HashMap<String, HashMap<String, List<String>>>> retMap = new HashMap<String, HashMap<String, HashMap<String, List<String>>>>();
						while (rs.next()) {
							HashMap<String, HashMap<String, List<String>>> param2VoMap = retMap.get(rs.getObject(1));
							if (param2VoMap == null) {
								param2VoMap = new HashMap<String, HashMap<String, List<String>>>();
								HashMap<String, List<String>> param3VoMap = new HashMap<String, List<String>>();
								param2VoMap.put(rs.getObject(2).toString(), param3VoMap);
								List<String> list = new ArrayList<>();
								list.add(rs.getObject(4).toString());
								param3VoMap.put(rs.getObject(3).toString(), list);
								retMap.put(rs.getObject(1).toString(), param2VoMap);
							} else {
								HashMap<String, List<String>> param3VoMap = param2VoMap.get(rs.getObject(2));
								if (param3VoMap == null) {
									param3VoMap = new HashMap<String, List<String>>();
									List<String> list = new ArrayList<>();
									list.add(rs.getObject(4).toString());
									param3VoMap.put(rs.getObject(3).toString(), list);
								} else {
									List<String> list = param3VoMap.get(rs.getObject(3).toString());
									if (list == null) {
										list = new ArrayList<>();
										list.add(rs.getObject(4).toString());
										param3VoMap.put(rs.getObject(3).toString(), list);
									} else {
										list.add(rs.getObject(4).toString());
										param3VoMap.put(rs.getObject(3).toString(), list);
									}
								}
							}

						}
						return retMap;
					}
				});

		return param1ParamsMap;
	}

	protected GeneralVO processorToGeneralVO(ResultSet rs) throws SQLException {

		ResultSetMetaData meta = rs.getMetaData();
		int cols = meta.getColumnCount();
		GeneralVO result = new GeneralVO();

		for (int i = 1; i <= cols; i++) {

			String strRealName = StringUtils.isNotEmpty(meta.getColumnLabel(i)) ? meta.getColumnLabel(i) : meta
					.getColumnName(i);
			result.setAttributeValue(strRealName.toLowerCase(), rs.getObject(i));
		}

		return result;
	}

	@Override
	public void deleteDaySalary(String pk_hrorg, UFLiteralDate calculdate, int continueTime) throws BusinessException {
		UFLiteralDate continuedate = calculdate.getDateBefore(continueTime);
		String deleteSql = "delete from wa_daysalary where pk_hrorg='" + pk_hrorg + "' and salarydate<'"
				+ continuedate.toStdString() + "'";
		getDao().executeUpdate(deleteSql);
	}

	@Override
	public void checkDaySalaryAndCalculSalary(String pk_hrorg, UFLiteralDate calculdate, int checkrange)
			throws BusinessException {
		for (int i = 1; i <= checkrange; i++) {
			UFLiteralDate checkDate = calculdate.getDateBefore(i);
			// ��ѯ��������-��н��������ȡֵ��ʽ
			int daynumtype = getSysintValue(pk_hrorg, DaySalaryEnum.DAYSYSINT);
			String checkSql = "select count(*) from wa_daysalary where pk_hrorg='" + pk_hrorg + "' and salarydate='"
					+ checkDate.toStdString() + "' and isnull(dr,0)=0 and initcode = '" + daynumtype + "'";
			int count = (int) getDao().executeQuery(checkSql, new ColumnProcessor());
			if (count == 0) {
				calculSalaryByHrorg(pk_hrorg, checkDate);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void checkDaySalaryAndCalculSalary(String pk_wa_class, String[] pk_psndocs, String cyear, String cperiod)
			throws BusinessException {
		// ��ѯн�ʷ�����Ӧ��н���ڼ�Ŀ�ʼ�������������
		String qrySql = "SELECT\n" + "	period.cstartdate,\n" + "	period.cenddate\n" + "FROM\n"
				+ "	wa_waclass waclass\n"
				+ "LEFT JOIN wa_period period ON period.pk_periodscheme = waclass.pk_periodscheme\n" + "WHERE\n"
				+ "	waclass.pk_wa_class = '" + pk_wa_class + "'\n" + "AND period.caccyear = '" + cyear + "'\n"
				+ "AND period.caccperiod = '" + cperiod + "'";
		List<HashMap<String, Object>> listMaptemp = (ArrayList<HashMap<String, Object>>) getDao().executeQuery(qrySql,
				new MapListProcessor());
		if (listMaptemp.size() < 1) {
			return;
		}
		HashMap<String, Object> hashMap = listMaptemp.get(0);
		String begindate = hashMap.get("cstartdate").toString();
		String enddate = hashMap.get("cenddate").toString();

		qrySql = "select distinct pk_wa_item from hi_psndoc_wadoc where begindate <= '" + enddate.toString()
				+ "' and isnull(enddate, '9999-12-31') >= '" + begindate.toString() + "'";
		List<String> pk_wa_items = (List<String>) getDao().executeQuery(qrySql, new ColumnListProcessor());

		if (pk_wa_items != null && pk_wa_items.size() > 0) {
			for (String pk_wa_item : pk_wa_items) {
				checkDaySalaryAndCalculSalary(pk_wa_class, pk_psndocs, (new UFLiteralDate(begindate)),
						new UFLiteralDate(enddate), pk_wa_item);
			}
		}

	}

	@Override
	public void checkDaySalaryAndCalculSalary(String pk_wa_class, String[] pk_psndocs, UFLiteralDate begindate,
			UFLiteralDate enddate, String pk_wa_item) throws BusinessException {
		WaClassVO waclassvo = (WaClassVO) getDao().retrieveByPK(WaClassVO.class, pk_wa_class);
		OrgVO orgvo = (OrgVO) getDao().retrieveByPK(OrgVO.class, waclassvo.getPk_org());
		String inpsndocsql = new InSQLCreator().getInSQL(pk_psndocs, true);

		String strSQL = "SELECT   '"
				+ waclassvo.getPk_org()
				+ "' pk_org, " //
				+ " calendar.calendardate, " //
				+ " wadoc.pk_psndoc, " //
				+ " wadoc.pk_psnjob, " //
				+ " wadoc.pk_psndoc_sub, " //
				+ " wadoc.ts AS wadocts, "
				+ " wadoc.pk_wa_item, "
				+ " wadoc.nmoney " //
				+ " FROM " //
				+ " hi_psndoc_wadoc wadoc " //
				+ " LEFT JOIN " //
				+ " bd_workcalendardate calendar "
				+ " ON "
				+ " calendar.calendardate >= wadoc.begindate "
				+ " AND ( "
				+ "         calendar.calendardate <= wadoc.enddate " //
				+ " OR  wadoc.enddate IS NULL ) " //
				+ " WHERE " //
				+ " calendar.calendardate <= '" + enddate.toStdString() + "' " + " AND calendar.calendardate >= '"
				+ begindate.toStdString()
				+ "' " //
				+ " AND calendar.pk_workcalendar='" + orgvo.getWorkcalendar() + "' " + " AND wadoc.pk_wa_item='"
				+ pk_wa_item + "' " + " AND wadoc.waflag='Y' " + " AND wadoc.pk_psndoc in (" + inpsndocsql + ") ";

		// ��ѯ�������ʴ����������޸ĵļ�¼�Լ���¼Ϊ�յ�����
		// ��н�ʷ�������֯���й���,��ֹ��ְ��Ա����
		// String checkSql =
		// "SELECT  waclass.pk_org,  calendar.calendardate,  wadoc.pk_psndoc,  wadoc.pk_psnjob,  wadoc.pk_psndoc_sub, "
		// +
		// " wadoc.ts AS wadocts,  wadoc.pk_wa_item, wadoc.nmoney, salary.pk_daysalary "
		// + " FROM  hi_psndoc_wadoc wadoc " +
		// " LEFT JOIN  wa_waclass waclass ON  waclass.pk_wa_class = '"
		// + pk_wa_class
		// + "' "
		// // + " LEFT JOIN  org_orgs org ON org.pk_org = waclass.pk_org "
		// +
		// " LEFT JOIN bd_workcalendardate calendar ON calendar.calendardate >= wadoc.begindate "
		// +
		// " AND ( calendar.calendardate <= wadoc.enddate OR  wadoc.enddate IS NULL ) "
		// // + " AND org.workcalendar = calendar.pk_workcalendar "
		// + " LEFT JOIN wa_daysalary salary ON ( salary.pk_wa_class = '"
		// + pk_wa_class
		// + "' "
		// +
		// " AND salary.pk_psndoc = wadoc.pk_psndoc  AND wadoc.pk_wa_item = salary.pk_wa_item "
		// + " AND calendar.calendardate = salary.salarydate ) "
		// // +
		// //
		// " LEFT JOIN  pub_sysinit sysinit ON (  waclass.pk_org = sysinit.pk_org "
		// // + " AND sysinit.dr = 0  AND sysinit.initcode = '"
		// // + DaySalaryEnum.DAYSYSINT
		// // + "') "
		// + " WHERE  wadoc.waflag = 'Y' AND calendar.calendardate <= '"
		// + enddate.toStdString()
		// + "' "
		// + " AND calendar.calendardate >= '"
		// + begindate.toStdString()
		// + "'"
		// + " AND wadoc.pk_wa_item='"
		// + pk_wa_item
		// + "' "
		// + " AND calendar.pk_workcalendar='"
		// + pk_workcalendar
		// + "'"
		// // + " AND wadoc.pk_org = waclass.pk_org "
		// + " AND wadoc.pk_psndoc in ("
		// + inpsndocsql
		// + ") "
		// + " AND ( wadoc.ts <> salary.wadocts "
		// + " OR  salary.wadocts IS NULL "
		// // + " OR  salary.initcode <> sysinit.value " +
		// // " OR  salary.initcode IS NULL "
		// + ") ";

		List<Map<String, Object>> psndocWadocMapList = (List<Map<String, Object>>) getDao().executeQuery(strSQL,
				new MapListProcessor());// executeQuery(strSQL);
		if (psndocWadocMapList == null || psndocWadocMapList.size() < 1) {
			return;
		} else {
			this.getDao().executeUpdate(
					"delete from wa_daysalary where pk_hrorg='" + orgvo.getPk_org() + "' and pk_wa_class='"
							+ waclassvo.getPk_wa_class() + "'  and pk_wa_item='" + pk_wa_item
							+ "' and salarydate between '" + begindate.toStdString() + "' and '"
							+ enddate.toStdString() + "' and pk_psndoc in (" + inpsndocsql + ")");
		}

		// ��Ҫɾ������н��¼
		// List<String> deletePks = new ArrayList<String>();
		// ������н������
		List<DaySalaryVO> listTbmDaySalaryVOs = new ArrayList<DaySalaryVO>();
		for (Map<String, Object> psndocWadocMap : psndocWadocMapList) {
			String pk_org = (String) psndocWadocMap.get("pk_org");
			// ��ѯ��������-������н��������ȡֵ��ʽ
			int daynumtype = getSysintValue(pk_org, DaySalaryEnum.DAYSYSINT);
			// List<GeneralVO> listGeneralVOs = e.getValue();
			// ��ÿ���˵Ķ�������Ŀ����
			// for (int j = 0, size = listGeneralVOs.size(); j < size; j++) {
			DaySalaryVO salaryVO = new DaySalaryVO();
			// GeneralVO generalVO = listGeneralVOs.get(j);
			UFLiteralDate calculdate = new UFLiteralDate((String) psndocWadocMap.get("calendardate"));

			salaryVO.setSalarydate(calculdate);
			salaryVO.setCyear(calculdate.getYear());
			salaryVO.setCperiod(calculdate.getMonth());
			salaryVO.setPk_hrorg(pk_org);
			salaryVO.setPk_psndoc((String) psndocWadocMap.get("pk_psndoc"));
			salaryVO.setPk_psndoc_sub((String) psndocWadocMap.get("pk_psndoc_sub"));
			salaryVO.setWadocts(new UFDateTime((String) psndocWadocMap.get("wadocts")));
			salaryVO.setPk_psnjob((String) psndocWadocMap.get("pk_psnjob"));
			salaryVO.setPk_wa_item((String) psndocWadocMap.get("pk_wa_item"));
			salaryVO.setPk_wa_class(pk_wa_class);
			UFDouble nmoney = psndocWadocMap.get("nmoney") != null ? new UFDouble(String.valueOf(psndocWadocMap
					.get("nmoney"))) : UFDouble.ZERO_DBL;
			double daysalarynum = getDaySalaryNum(pk_org, pk_wa_class, calculdate, daynumtype);
			UFDouble daysalary = nmoney.div(daysalarynum);
			UFDouble hoursalary = daysalary.div(DaySalaryEnum.HOURSALARYNUM);
			if (daynumtype == DaySalaryEnum.DAYNUMTYPE1) {// н���ڼ��н������
				if (!checkWorkCalendar(calculdate, pk_org)) {
					// ������������ǡ��ݡ�������нΪ0
					daysalary = UFDouble.ZERO_DBL;
					hoursalary = UFDouble.ZERO_DBL;
				}
			}
			salaryVO.setDaysalary(daysalary);
			salaryVO.setHoursalary(hoursalary);
			salaryVO.setInitcode(daynumtype);
			listTbmDaySalaryVOs.add(salaryVO);
			// String pk_salary = null;
			// if (StringUtils.isNotBlank(pk_salary)) {
			// deletePks.add(pk_salary);
			// }
			// }
		}
		// ɾ���Ѿ������ڵĶ����ʼ�¼
		String deletesql = "delete from wa_daysalary where pk_psndoc_sub not in (select pk_psndoc_sub from hi_psndoc_wadoc)";
		getDao().executeUpdate(deletesql);
		// if (deletePks.size() > 1) {
		// getDao().deleteByPKs(DaySalaryVO.class, deletePks.toArray(new
		// String[0]));
		// }
		getDao().insertVOList(listTbmDaySalaryVOs);
	}
}