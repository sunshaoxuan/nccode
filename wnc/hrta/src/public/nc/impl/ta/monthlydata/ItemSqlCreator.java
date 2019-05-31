package nc.impl.ta.monthlydata;

import java.util.HashMap;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.hr.utils.SQLHelper;
import nc.itf.ta.PeriodServiceFacade;
import nc.vo.bd.shift.ShiftVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.ta.item.ItemVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.timeitem.LeaveTypeVO;
import nc.vo.ta.timeitem.TimeItemVO;

import org.apache.commons.lang.StringUtils;

/**
 * �ṩһЩ�±���Ŀ��Ϣ�Ĳ�ѯsqlƬ��
 * 
 * @author wangdca
 * 
 */
public class ItemSqlCreator {

	Map<String, PeriodVO> periodmap = new HashMap<String, PeriodVO>();
	Map<String, OrgVO> hrOrgMap = new HashMap<String, OrgVO>();

	/**
	 * ���ڽ���:ȡ����������ݣ��������ݼ���Ŀ��sqlƬ��
	 * 
	 * @param period
	 *            ��׼�����ڼ�
	 * @param pk_psndoc
	 *            ��Ա������Ϣ���������ֶ�(���� �� �ֶ�����)
	 * @param leaveType
	 *            �ݼ����VO
	 * @return ��Ӧ��Ա�ڻ�׼�����ڼ�ĸ��ݼ����Ľ���
	 * @throws BusinessException
	 */
	public String proYearLeave(LeaveTypeVO leaveType, String pk_psndoc, String taorg, String tayear, String tamonth,
			String tableName) throws BusinessException {
		StringBuilder sql = new StringBuilder();
		// MOD by ssx for efficiency optimization
		// on 2019-5-18
		// //
		// sql.append(" select tbm_leavebalance.restdayorhour from tbm_leavebalance, "
		// + tableName);;2013-11-19�޸�Ϊȡ����
		// sql.append(" select  sum(tbm_leavebalance.restdayorhour) from tbm_leavebalance, "
		// + tableName);
		// sql.append(" where tbm_leavebalance.pk_psndoc = " + pk_psndoc);
		// sql.append(" and tbm_leavebalance.pk_org = " + taorg);
		// // sql.append(" and tbm_leavebalance.curyear = " + tayear);
		// sql.append(" and tbm_leavebalance.salaryyear = " + tayear);
		// // ���������ݼ������Ҫ�����ڼ�
		// // sql.append(" and (tbm_leavebalance.curmonth = " + tamonth +
		// " or (select t.leavesetperiod from tbm_timeitemcopy t where t.pk_org="+taorg+" and t.pk_timeitem='"+leaveType.getPk_timeitem()+"')!="+TimeItemCopyVO.LEAVESETPERIOD_MONTH+" )");
		// sql.append(" and tbm_leavebalance.salarymonth = " + tamonth );
		// // ֻȡתн�ʵ����
		// sql.append(" and (select t.leavesettlement from tbm_timeitemcopy t where t.pk_org="+taorg+" and t.pk_timeitem='"+leaveType.getPk_timeitem()+"')="+TimeItemCopyVO.LEAVESETTLEMENT_MONEY);
		// sql.append(" and tbm_leavebalance.pk_timeitem= '" +
		// leaveType.getPk_timeitem() +
		// "' and wa_cacu_data.pk_wa_data = wa_data.pk_wa_data");

		// sql.append(" SELECT SUM ( tbm_leavebalance.restdayorhour ) ");
		// sql.append(" FROM tbm_leavebalance ");
		// sql.append(" INNER JOIN " + tableName + " ON ");
		// sql.append("     tbm_leavebalance.pk_psndoc = " + pk_psndoc);
		// sql.append(" AND tbm_leavebalance.pk_org = " + taorg);
		// sql.append(" AND tbm_leavebalance.salaryyear = " + tayear);
		// sql.append(" AND tbm_leavebalance.salarymonth = " + tamonth);
		// sql.append(" INNER JOIN tbm_timeitemcopy ON ");
		// sql.append("     tbm_timeitemcopy.pk_org = " + taorg);
		// sql.append(" AND tbm_leavebalance.pk_timeitem = tbm_timeitemcopy.pk_timeitem ");
		// sql.append(" WHERE  ");
		// sql.append("     tbm_timeitemcopy.pk_timeitem = '" +
		// leaveType.getPk_timeitem() + "' ");
		// sql.append(" AND tbm_timeitemcopy.leavesettlement = " +
		// String.valueOf(TimeItemCopyVO.LEAVESETTLEMENT_MONEY));
		// sql.append(" AND wa_cacu_data.pk_wa_data = wa_data.pk_wa_data ");
		// end

		// mod by Connie.ZH
		// 2019-05-25 started
		// wage calculation efficiency
		sql.append(" select  restdayorhour from (SELECT SUM ( tbm_leavebalance.restdayorhour ) restdayorhour,tbm_leavebalance.pk_psndoc  FROM tbm_leavebalance  ");
		sql.append(" inner join  tbm_timeitemcopy on tbm_timeitemcopy.pk_timeitem = tbm_leavebalance.PK_TIMEITEM");
		sql.append(" where");
		sql.append(" tbm_leavebalance.pk_timeitem = '" + leaveType.getPk_timeitem() + "' ");
		sql.append(" AND tbm_leavebalance.salaryyear =  '#salaryyear#'");
		sql.append(" AND tbm_leavebalance.salarymonth =  '#salarymonth#'");
		sql.append(" AND tbm_timeitemcopy.leavesettlement = 2");
		sql.append(" AND tbm_leavebalance.DR = 0");
		sql.append(" group by ");
		sql.append(" tbm_leavebalance.pk_psndoc)  tmp where tmp.pk_psndoc=wa_data.pk_psndoc ");
		// 2019-05-25 ended
		return sql.toString();
	}

	/**
	 * ȡ��Ӧ��Ա��ĳһ����׼�ڼ俼���ڼ�� ��Ŀ sqlƬ��
	 * 
	 * @param accyear
	 *            ������
	 * @param accmonth
	 *            ����·�
	 * @param pk_psndoc
	 *            ��Ա������Ϣ���������ֶ�(���� �� �ֶ�����)
	 * @param item
	 *            ��Ŀdbcode �� item_db_code ������ item_code
	 * @return
	 * @throws BusinessException
	 */
	public String getSql(String accyear, String accmonth, String pk_psndoc, String item_db_code, String pk_org)
			throws BusinessException {
		PeriodVO period = getPeriod(accyear, accmonth, pk_org);
		String sql = "select " + item_db_code + " from tbm_monthstat where tbm_monthstat.pk_psndoc = " + pk_psndoc
				+ " and tbm_monthstat.tbmyear = '" + period.getTimeyear() + "' and tbm_monthstat.tbmmonth = '"
				+ period.getTimemonth() + "' and tbm_monthstat.pk_org = '" + pk_org + "' ";
		return sql;
	}

	/**
	 * ȡ��֯������hr��֯��pk������sqlƬ�� orcal���
	 * 
	 * @param workorg
	 *            ��ְ��֯(���� �� �ֶ�����)
	 * @return
	 * @throws DAOException
	 */
	public String getHrOrgPK(String workorg, String tableName) throws DAOException {
		StringBuilder querySql = new StringBuilder();
		querySql.append("select hr_orgs1.pk_hrorg  pk_hrorg                                                                                                           ");
		querySql.append("from (select hr.pk_hrorg,                                                                                                                   ");
		querySql.append("       a.innercode aos_innercode                                                                                                     ");
		querySql.append("      from org_adminorg a                                                                                                            ");
		querySql.append("       inner join org_hrorg hr                                                                                                       ");
		querySql.append("       on a.pk_adminorg = hr.pk_hrorg                                                                                                ");
		querySql.append("       inner join org_adminorg b                                                                                                     ");
		querySql.append("       on (a.innercode=substr(b.innercode, 1, length(a.innercode))) inner join " + tableName
				+ " on b.pk_adminorg = " + workorg + "							  ");
		querySql.append("      where hr.enablestate =2                                                                                                        ");
		querySql.append(" )                                                                                                                                   ");
		querySql.append(" hr_orgs1                                                                                                                            ");
		querySql.append("where length(hr_orgs1.aos_innercode) = (select max(length(hr_orgs2.innercode))                                                             ");
		querySql.append("                                     from (select a.innercode                                                                        ");
		querySql.append("                                           from org_adminorg a                                                                       ");
		querySql.append("                                            inner join org_hrorg hr                                                                  ");
		querySql.append("                                            on a.pk_adminorg = hr.pk_hrorg                                                           ");
		querySql.append("                                            inner join org_adminorg b                                                                ");
		querySql.append("                                            on a.innercode=substr(b.innercode, 1, length(a.innercode)) inner join "
				+ tableName + " on b.pk_adminorg = (" + workorg + ")");
		querySql.append("                                           where hr.enablestate =2                                                                   ");
		querySql.append("                                      )                                                                                              ");
		querySql.append("                                      hr_orgs2 )                                                                                      ");
		return querySql.toString();
	}

	/**
	 * ��ȡ�����ڼ����º�������Դ��֯����(orcal ��䣩
	 * 
	 * @param accyear
	 * @param accmonth
	 * @param workorg
	 * @return ������
	 * @throws BusinessException
	 */
	public String getHrAndPeriodOracl(String accyear, String accmonth, String workorg, String tablename)
			throws BusinessException {
		// String sql =
		// " select period.timeyear timeyear,period.timemonth timemonth,hrorg.pk_hrorg from tbm_period period, ("
		// + getHrOrgPK(workorg,tableName) + ") hrorg " +
		// " where period.pk_org = hrorg.pk_hrorg and period.accyear = '" +
		// accyear + "' and period.accmonth = '" + accmonth + "'";
		// return sql;
		StringBuilder sql = new StringBuilder();

		// MOD by ssx for efficiency optimization
		// on 2019-5-18
		sql.append(" select /*+leading( period)*/ distinct period.timeyear timeyear,period.timemonth timemonth,hrorg.pk_hrorg pk_hrorg, hrorg.workorg workorg ");
		sql.append(" from tbm_period period, ");
		// ssx add distinct
		sql.append("    (select distinct hr_orgs1.pk_hrorg pk_hrorg ,hr_orgs1.workorg  ");
		sql.append(" 	from ( ");
		// ssx add distinct
		sql.append("         select distinct hr.pk_hrorg, a.innercode aos_innercode , wa_cacu_data.workorg workorg");
		sql.append(" 		 from org_adminorg a inner join org_hrorg hr on a.pk_adminorg = hr.pk_hrorg ");
		sql.append(" 			 inner join org_adminorg b on (a.innercode =substr(b.innercode, 1,length(a.innercode)))");
		sql.append(" 			 inner join " + tablename + " on b.pk_adminorg = " + workorg);
		sql.append(" 		 where hr.enablestate = 2 )hr_orgs1, ");
		sql.append("         (select max(length(hr_orgs2.innercode)) leng,workorg");
		sql.append(" 		  from(	");
		// ssx add distinct
		sql.append("          select distinct a.innercode ,wa_cacu_data.workorg ");
		sql.append(" 		  from org_adminorg a inner join org_hrorg hr on a.pk_adminorg = hr.pk_hrorg ");
		sql.append(" 				inner join org_adminorg b on a.innercode=substr(b.innercode, 1, length(a.innercode)) ");
		sql.append(" 				inner join " + tablename + " on b.pk_adminorg =  " + workorg);
		sql.append("          where hr.enablestate =2 )hr_orgs2 group by workorg )hrorg3 ");
		sql.append("			where length( hr_orgs1.aos_innercode) =hrorg3.leng and hr_orgs1.workorg = hrorg3.workorg ) hrorg ");
		sql.append(" where period.pk_org = hrorg.pk_hrorg and period.accyear = '" + accyear
				+ "' and  period.accmonth = '" + accmonth + "'");
		// end

		return sql.toString();

		// StringBuilder sql = new StringBuilder();
		//
		// sql.append(" select /*+leading( period)*/ distinct period.timeyear timeyear,period.timemonth timemonth,hrorg.pk_hrorg pk_hrorg, hrorg.workorg workorg ");
		// sql.append(" from tbm_period period, ");
		// sql.append("    (select hr_orgs1.pk_hrorg pk_hrorg ,hr_orgs1.workorg  ");
		// sql.append("  from ( ");
		// sql.append("         select hr.pk_hrorg, a.innercode aos_innercode , wa_cacu_data.workorg workorg");
		// sql.append("    from org_adminorg a inner join org_hrorg hr on a.pk_adminorg = hr.pk_hrorg ");
		// sql.append("     inner join org_adminorg b on (a.innercode =substr(b.innercode, 1,length(a.innercode)))");
		// sql.append("    where hr.enablestate = 2 and exists(select 1 from " +
		// tablename + " where b.pk_adminorg = " + workorg + "))hr_orgs1, ");
		// sql.append("         (select max(length(hr_orgs2.innercode)) leng,workorg");
		// sql.append("     from( ");
		// sql.append("          select a.innercode ,wa_cacu_data.workorg ");
		// sql.append("     from org_adminorg a inner join org_hrorg hr on a.pk_adminorg = hr.pk_hrorg ");
		// sql.append("     inner join org_adminorg b on a.innercode=substr(b.innercode, 1, length(a.innercode)) ");
		// sql.append("          where hr.enablestate =2 and exists(select 1 from "
		// + tablename + " where b.pk_adminorg = " + workorg +
		// "))hr_orgs2 group by workorg )hrorg3 ");
		// sql.append("   where length( hr_orgs1.aos_innercode) =hrorg3.leng and hr_orgs1.workorg = hrorg3.workorg ) hrorg ");
		// sql.append(" where period.pk_org = hrorg.pk_hrorg and period.accyear = '"
		// + accyear
		// + "' and  period.accmonth = '" + accmonth +"'");
		//
		// return sql.toString();

	}

	/**
	 * ���¿����ڼ����º�������Դ��֯����(sqlserver ��䣩
	 * 
	 * @param accyear
	 * @param accmonth
	 * @param workorg
	 * @return ������
	 * @throws BusinessException
	 */
	public String updateTaItemDataInOrcal(String accyear, String accmonth, String workorg, String tablename)
			throws BusinessException {
		StringBuilder sql = new StringBuilder();
		sql.append(" update " + tablename
				+ " set (tayear, taperiod,pk_ta_org) = (select tadata.timeyear,tadata.timemonth,tadata.pk_hrorg from (");
		sql.append(getHrAndPeriodOracl(accyear, accmonth, workorg, tablename));
		sql.append(" ) tadata  where tadata.workorg = " + workorg + ") ");
		return sql.toString();

	}

	/**
	 * ���¿����ڼ����º�������Դ��֯����(sqlserver ��䣩
	 * 
	 * @param accyear
	 * @param accmonth
	 * @param workorg
	 * @return ������
	 * @throws BusinessException
	 */
	public String updateTaItemDataInSQL(String accyear, String accmonth, String workorg, String tablename)
			throws BusinessException {
		StringBuilder sql = new StringBuilder();
		sql.append(" update " + tablename
				+ " set tayear  = tadata.timeyear, taperiod = tadata.timemonth,pk_ta_org=tadata.pk_hrorg from (");
		sql.append(getPeriodAndHrOrgSql(accyear, accmonth, workorg, tablename));
		sql.append(" ) tadata ," + tablename + " where tadata.workorg = " + workorg);
		return sql.toString();
	}

	/**
	 * ��ȡ�����ڼ����º�������Դ��֯����(sqlserver ��䣩
	 * 
	 * @param accyear
	 *            �����
	 * @param accmonth
	 *            �����
	 * @param workorg
	 *            �м����ְ��֯�ֶΣ��м����.�ֶ�����
	 * @param tablename
	 *            �м����
	 * @return
	 * @throws BusinessException
	 */
	public String getPeriodAndHrOrgSql(String accyear, String accmonth, String workorg, String tablename)
			throws BusinessException {
		StringBuilder sql = new StringBuilder();

		sql.append(" select distinct period.timeyear timeyear,period.timemonth timemonth,hrorg.pk_hrorg pk_hrorg, hrorg.workorg workorg ");
		sql.append(" from tbm_period period, ");
		sql.append("    (select hr_orgs1.pk_hrorg pk_hrorg ,hr_orgs1.workorg  ");
		sql.append(" 	from ( ");
		sql.append("         select hr.pk_hrorg, a.innercode aos_innercode , wa_cacu_data.workorg workorg");
		sql.append(" 		 from org_adminorg a inner join org_hrorg hr on a.pk_adminorg = hr.pk_hrorg ");
		sql.append(" 			 inner join org_adminorg b on (a.innercode =substring(b.innercode, 1,len(a.innercode)))");
		sql.append(" 			 inner join " + tablename + " on b.pk_adminorg = " + workorg);
		sql.append(" 		 where hr.enablestate = 2 )hr_orgs1, ");
		sql.append("         (select max(len(hr_orgs2.innercode)) leng,workorg");
		sql.append(" 		  from(	");
		sql.append("          select a.innercode ,wa_cacu_data.workorg ");
		sql.append(" 		  from org_adminorg a inner join org_hrorg hr on a.pk_adminorg = hr.pk_hrorg ");
		sql.append(" 				inner join org_adminorg b on a.innercode=substring(b.innercode, 1, len(a.innercode)) ");
		sql.append(" 				inner join " + tablename + " on b.pk_adminorg =  " + workorg);
		sql.append("          where hr.enablestate =2 )hr_orgs2 group by workorg )hrorg3 ");
		sql.append("			where len( hr_orgs1.aos_innercode) =hrorg3.leng and hr_orgs1.workorg = hrorg3.workorg ) hrorg ");
		sql.append(" where period.pk_org = hrorg.pk_hrorg and period.accyear = '" + accyear
				+ "' and  period.accmonth = '" + accmonth + "'");

		return sql.toString();
	}

	/**
	 * ��ȡ��Ӧ��Աĳ��������Ŀ���±����ݵ�SqlƬ��
	 * 
	 * @param item_db_code
	 *            itemvo��Ŀdbcode �� item_db_code ������ item_code
	 * @param pk_psndoc
	 *            ��Ա������Ϣ���������ֶ�(���� �� �ֶ�����)
	 * @param taorg
	 *            ������֯���������ֶΣ�
	 * @param tayear
	 *            ������ȣ��������ֶΣ�
	 * @param tamonth
	 *            �����¶ȣ��������ֶΣ�
	 * @param tableName
	 *            ����--�м��
	 * @return
	 */
	public String getItemDataSql(String item_db_code, String pk_psndoc, String taorg, String tayear, String tamonth,
			String tableName) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select " + item_db_code);
		sql.append(" from tbm_monthstat, " + tableName);
		sql.append(" where tbm_monthstat.pk_psndoc = " + pk_psndoc);
		sql.append("       and tbm_monthstat.pk_org = " + taorg);
		sql.append("       and tbm_monthstat.tbmyear = " + tayear);
		sql.append("       and tbm_monthstat.tbmmonth = " + tamonth
				+ " and  wa_cacu_data.pk_wa_data = wa_data.pk_wa_data");
		return sql.toString();
	}

	/**
	 * ȡ��Ӧ��Ա��ĳһ����׼�ڼ俼���ڼ�ĳ���ͣ�������Ŀ���ݵ� sqlƬ��
	 * 
	 * @param timeItem
	 *            ������ݼ١��Ӱࡢͣ�����ϣ���� TimeItemVO
	 * @param pk_psndoc
	 *            ��Ա������Ϣ���������ֶ�(���� �� �ֶ�����)
	 * @param taorg
	 *            ������֯���������ֶΣ�
	 * @param tayear
	 *            ������ȣ��������ֶΣ�
	 * @param tamonth
	 *            �����¶ȣ��������ֶΣ�
	 * @param tableName
	 *            ����--�м��
	 * @return
	 */
	public String getTimeItemSql(TimeItemVO timeItem, String pk_psndoc, String taorg, String tayear, String tamonth,
			String tableName) {
		if (timeItem == null) {
			return null;
		}
		StringBuilder sql = new StringBuilder();
		// sql.append(" select hournum from tbm_monthstatb , " + tableName);
		sql.append(" select hournum from tbm_monthstatb  ");
		// sql.append(" where pk_monthstat in (select pk_monthstat from tbm_monthstat ,"
		// + tableName);
		sql.append(" where pk_monthstat in (select pk_monthstat from tbm_monthstat ");
		sql.append(" 	where tbm_monthstat.tbmyear = " + tayear);
		sql.append(" 	and tbm_monthstat.tbmmonth = " + tamonth);
		sql.append(" 	and tbm_monthstat.pk_org = " + taorg);
		sql.append(" 	and tbm_monthstat.pk_psndoc = " + pk_psndoc
				+ " ) and wa_cacu_data.pk_wa_data = wa_data.pk_wa_data");
		sql.append(" and tbm_monthstatb.pk_timeitem = '" + timeItem.getPk_timeitem() + "' ");
		sql.append(" and tbm_monthstatb.pk_org = " + taorg);
		return sql.toString();
	}

	/**
	 * ���ݻ���ڼ���һ�׼�����ڼ�
	 * 
	 * @param accyear
	 *            ������
	 * @param accmonth
	 *            ����·�
	 * @param pk_org
	 * @return
	 */
	public PeriodVO getPeriod(String accyear, String accmonth, String pk_org) throws BusinessException {
		String key = accyear + accmonth + pk_org;
		PeriodVO vo = periodmap.get(key);
		if (vo == null) {
			vo = PeriodServiceFacade.queryByAccYearMonth(pk_org, accyear, accmonth);
			periodmap.put(key, vo);
		}
		return vo;
	}

	/**
	 * ���˼�н����������:ȡ����ָ����Χ�ļ�н����������������ʼ���ڣ���ֹ���ڡ���Ա������Ϣ���������ֶΣ���sqlƬ��
	 * 
	 * @param beginDate
	 *            ��ʼ����
	 * @param endDate
	 *            ��ֹ����
	 * @param pk_psndoc
	 *            ��Ա������Ϣ���������ֶ�
	 * @return ���նԵ����и�ǰ�ķǹ��ݵĻ���
	 */
	public String valueOfTBMPsn(String beginDate, String endDate, String pk_psndoc) {
		// �����Ű��Ĺ����������� �����������ǹ��� + ������Ϊ��н����
		// String sql =
		// "select sum(case when tbm_psncalendar.pk_shift != '0001Z7000000000000GX' or bd_holiday.issalary = 'Y' then 1 else 0 end) "
		// +
		// "from tbm_psncalendar left join tbm_psncalholiday on tbm_psncalholiday.pk_psncalendar = tbm_psncalendar.pk_psncalendar left join bd_holiday on bd_holiday.pk_holiday = tbm_psncalholiday.pk_holiday "
		// +
		// " where tbm_psncalendar.calendar between '" + beginDate + "' and '" +
		// endDate +
		// "'  and tbm_psncalendar.pk_psndoc = '" + pk_psndoc + "' ";
		// ��������
		// String cond = " where tbm_psncalendar.calendar between '" + beginDate
		// + "' and '" + endDate +
		String cond = " where tbm_psncalendar.calendar >= " + beginDate + " and tbm_psncalendar.calendar <= " + endDate
				+ " and tbm_psncalendar.pk_psndoc = " + pk_psndoc;
		// ���ü����и�ǰ���Ե�����㷨 �����������İ�ηǹ��� + ���������İ��Ϊ�������и�ǰ�а�Σ��ǹ��ݣ�
		// ��֮Ϊ�����նԵ����и�ǰ�ķǹ��ݵĻ���
		// ��н����
		cond += " and ( pk_shift <> '" + ShiftVO.PK_GX + "' or (pk_shift = '" + ShiftVO.PK_GX
				+ "' and original_shift_b4cut not in ('null','~','" + ShiftVO.PK_GX + "'))) ";
		String sql = " select count(calendar) from tbm_psncalendar " + cond;
		return sql;
	}

	/**
	 * ���ݴ�������ȡ�����ڼ俪ʼ/�������ڵ�sqlƬ��
	 * 
	 * @param pk_org
	 * @param hiredate
	 *            ��ְ����
	 * @param leaveDate
	 *            ��ְ����
	 * @param isGetBegin
	 *            �Ƿ�ȡ��ʼ����
	 * @return
	 */
	public String valueOfPeriodDate(String pk_org, String hireDate, String leaveDate, boolean isGetBegin) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select (case when "
				+ (isGetBegin ? ("tbm_period.begindate>" + hireDate) : (SQLHelper.getNullSql(leaveDate)
						+ " or tbm_period.enddate<" + leaveDate)));
		sql.append(" then " + (isGetBegin ? "tbm_period.begindate" : "tbm_period.enddate"));
		sql.append(" else " + (isGetBegin ? hireDate : leaveDate) + " end) ");
		sql.append(" from tbm_period where tbm_period.pk_org = " + pk_org);
		sql.append(" and " + (isGetBegin ? leaveDate : hireDate)
				+ " between tbm_period.begindate and tbm_period.enddate");
		return sql.toString();
	}

	/**
	 * ���˿���ͳ�ƺ���:ȡ����ָ����Χ�Ŀ��ڻ������ݣ�������������Ŀ����ֹ�����ڼ䣬���㷽ʽ����sqlƬ��
	 * 
	 * @param beginDate
	 *            ���ڿ�ʼʱ��
	 * @param endDate
	 *            ���ڽ���ʱ��
	 * @param item
	 *            ������Ŀvo�������ձ��ĳٵ����������˴�����ʱ���ȣ� ItemVO
	 * @param calculateType
	 *            ���㷽ʽ 0-ȡ�ͣ�1-��ֵ��2-��Сֵ��3-���ֵ
	 * @param pk_psndoc
	 *            ��Ա������Ϣ���������ֶ�
	 * @return
	 */
	public String valueOfTBMPsn(String beginDate, String endDate, ItemVO item, int calculateType, String pk_psndoc,
			String pk_org) {
		if (StringUtils.isEmpty(beginDate) || StringUtils.isEmpty(endDate) || StringUtils.isEmpty(pk_psndoc)) {
			return null;
		}
		String type = null;
		switch (calculateType) {
		case 0:
			type = "sum";
			break;
		case 1:
			type = "avg";
			break;
		case 2:
			type = "min";
			break;
		case 3:
			type = "max";
			break;
		}
		if (StringUtils.isEmpty(type)) {
			return null;
		}
		String sql = " select " + type + "(" + item.getItem_db_code() + ")" + " from tbm_daystat "
				+ "where  tbm_daystat.pk_psndoc = " + pk_psndoc + " and  tbm_daystat.pk_org = '" + pk_org
				+ "' and tbm_daystat.calendar >= " + beginDate + " and tbm_daystat.calendar <= " + endDate;
		return sql;
	}

	/**
	 * ����ĳ������һ��ʱ��ĳ��������� sqlƬ�� �÷�����ʱ����ȷ �д�����
	 * 
	 * @param beginDate
	 * @param endDate
	 * @param pk_psndoc
	 * @return
	 */
	public String workDaysOfTime(String beginDate, String endDate, String pk_psndoc, String pk_org) {
		if (StringUtils.isEmpty(beginDate) || StringUtils.isEmpty(endDate) || StringUtils.isEmpty(pk_psndoc)) {
			return null;
		}
		String sql = "select  sum((worklength/3600)/(gzsj+0.000000001)) from tbm_timedata inner join tbm_psncalendar  on tbm_timedata.calendar = tbm_psncalendar.calendar  where tbm_timedata.pk_psndoc = "
				+ pk_psndoc
				+ " and tbm_psncalendar.pk_psndoc = "
				+ pk_psndoc
				+ " and tbm_timedata.calendar >= "
				+ beginDate + " and tbm_timedata.calendar <= " + endDate;

		return sql;
	}

	/**
	 * ĳ���ڻ�׼�ڼ䣨����ڼ䣩�ļ�н������ sqlƬ��
	 * 
	 * @param accyear
	 * @param accmonth
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	public String salaryDays(String accyear, String accmonth, String pk_psndoc, String pk_org) throws BusinessException {
		if (StringUtils.isEmpty(accyear) || StringUtils.isEmpty(accmonth) || StringUtils.isEmpty(pk_psndoc)) {
			return null;
		}
		PeriodVO period = getPeriod(accyear, accmonth, pk_org);
		if (period != null) {
			String beginDate = "'" + period.getBegindate() + "' ";
			String endDate = " '" + period.getEnddate() + "' ";
			return valueOfTBMPsn(beginDate, endDate, pk_psndoc);
		}
		return null;
	}

}