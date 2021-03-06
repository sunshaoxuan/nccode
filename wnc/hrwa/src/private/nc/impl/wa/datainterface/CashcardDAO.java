package nc.impl.wa.datainterface;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.IWaPub;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.datainterface.BankEnterpriseVO;
import nc.vo.wa.datainterface.CashcardVO;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaState;

import org.apache.commons.lang.ArrayUtils;

/**
 * @author suihang
 * 
 */

public class CashcardDAO extends BaseDAOManager {

	public CashcardVO[] queryCashcardVOs(WaLoginVO waloginvo)
			throws DAOException {
		String year = waloginvo.getPeriodVO().getCyear();
		String period = waloginvo.getPeriodVO().getCperiod();
		String whereSql = "where pk_wa_class = '" + waloginvo.getPk_wa_class()
				+ "' and cyear = '" + year + "' and cperiod = '" + period + "'";
		/*
		 * if (waloginvo.getLeaveflag().booleanValue()) { whereSql =
		 * "where pk_wa_class in(select pk_childclass from wa_inludeclass where pk_parentclass = '"
		 * + waloginvo.getPk_prnt_class() + "' and cyear = '"+ year +
		 * "' and cperiod = '"+ period + "' and batch >100) and cyear = '"+ year
		 * + "' and cperiod = '" + period + "' "; }
		 */
		CashcardVO[] cashcardVOs = executeQueryVOs("select * from wa_cashcard "
				+ whereSql, CashcardVO.class);
		return cashcardVOs;
	}

	public void deleteCashcardVOs(WaLoginVO waloginvo) throws DAOException {
		String year = waloginvo.getPeriodVO().getCyear();
		String period = waloginvo.getPeriodVO().getCperiod();
		String whereSql = "where pk_wa_class = '" + waloginvo.getPk_wa_class()
				+ "' and cyear = '" + year + "' and cperiod = '" + period
				+ "' ";
		/*
		 * if (waloginvo.getLeaveflag().booleanValue()) { whereSql =
		 * "where pk_wa_class in(select pk_childclass from wa_inludeclass where pk_parentclass = '"
		 * + waloginvo.getPk_prnt_class() + "' and cyear = '"+ year +
		 * "' and cperiod = '"+ period + "' and batch >100) and cyear = '"+ year
		 * + "' and cperiod = '" + period + "' "; }
		 */
		getBaseDao().executeUpdate("delete from wa_cashcard " + whereSql);
	}

	public BankEnterpriseVO[] queryBankEnterpriseVOs(WaLoginVO waLoginVO,
			int type) throws BusinessException {

		String year = waLoginVO.getPeriodVO().getCyear();
		String period = waLoginVO.getPeriodVO().getCperiod();

		BaseDAOManager baseDAOManager = new BaseDAOManager();
		IWaPub wapub = NCLocator.getInstance().lookup(IWaPub.class);

		// 发放后方案才能进行分摊
		WaLoginVO waloginvo = wapub.getWaclassVOWithState(waLoginVO);

		if (waloginvo != null
				&& !waloginvo.getState().equals(WaState.CLASS_ALL_PAY)
				&& !waloginvo.getState().equals(WaState.CLASS_MONTH_END)) {
			String exceptionMessage = "";
			if (type == 1) {
				exceptionMessage = ResHelper.getString("60130bankitf",
						"060130bankitf0030")
				/* @res "薪资方案没有发放,不能导出报盘文件" */;
			} else {
				exceptionMessage = ResHelper.getString("60130bankitf",
						"060130bankitf0023")
				/* @res "薪资方案没有发放,不能进行财务结算" */;
			}

			throw new BusinessException(exceptionMessage);
		}
		StringBuffer sqlBuffer2 = new StringBuffer();
		sqlBuffer2
				.append("select wa_classitem.itemkey,wa_classitem.bankaccount "
						+ "from wa_classitem inner join wa_item on wa_item.pk_wa_item = wa_classitem.pk_wa_item  "
						+ "where   isnull(wa_classitem.bankaccount ,0)!=0 and wa_classitem.pk_wa_class = ? "
						+ "and wa_classitem.cyear = ? and wa_classitem.cperiod = ? and wa_item.iitemtype  = 0");
		SQLParameter parameter2 = new SQLParameter();
		parameter2.addParam(waLoginVO.getPk_wa_class());
		parameter2.addParam(year);
		parameter2.addParam(period);
		List<Object[]> itemAccountList = (List<Object[]>) getBaseDao()
				.executeQuery(sqlBuffer2.toString(), parameter2,
						new ArrayListProcessor());
		if (ArrayUtils.isEmpty(itemAccountList.toArray())) {
			return null;
		}
		HashMap<String, String> itemAccountMap = new HashMap<String, String>();
		for (Object[] itemAccount : itemAccountList) {
			itemAccountMap.put(itemAccount[0].toString(),
					itemAccount[1].toString());
		}

		StringBuffer sqlBuffer = new StringBuffer();
		Iterator<String> iterator = itemAccountMap.keySet().iterator();
		String whereSql = "where wa_data.pk_wa_class = '"
				+ waloginvo.getPk_wa_class() + "' and wa_data.cyear = '" + year
				+ "' and wa_data.cperiod = '" + period
				+ "'  and wa_data.stopflag='N' ";
		/*
		 * if (waloginvo.getLeaveflag().booleanValue()) { whereSql =
		 * "where wa_data.pk_wa_class in(select pk_childclass from wa_inludeclass where pk_parentclass = '"
		 * + waloginvo.getPk_prnt_class() + "' and cyear = '"+ year +
		 * "' and cperiod = '"+ period +
		 * "' and batch >100) and wa_data.cyear = '"+ year +
		 * "' and wa_data.cperiod = '" + period + "' "; }
		 */

		// 数据权限
		String powerSql = WaPowerSqlHelper.getWaPowerSql(
				waLoginVO.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			whereSql = whereSql + " and " + powerSql;
		}

		while (iterator.hasNext()) {
			String itemkey = iterator.next();

			sqlBuffer.append("select bd_psndoc.pk_psndoc, "); // 1
			sqlBuffer.append("       bd_psndoc.code psncode, "); // 2
			sqlBuffer.append(SQLHelper.getMultiLangNameColumn("bd_psndoc.name")
					+ "   psnname, "); // 3
			sqlBuffer
					.append(SQLHelper
							.getMultiLangNameColumn("bd_psnidtype.name")
							+ "  idtype, "); // 3
			sqlBuffer.append("       bd_psndoc.id id, "); // 3
			sqlBuffer.append("       wa_data.pk_psnjob pk_psnjob, "); // 3
			sqlBuffer
					.append("       wa_data.cyear || wa_data.cperiod yearperiod, "); // 4
			sqlBuffer.append(SQLHelper
					.getMultiLangNameColumn("org_orgs_v.name")
					+ "      org_name, "); // 5
			sqlBuffer.append(SQLHelper
					.getMultiLangNameColumn("org_dept_v.name")
					+ "      dept_name, "); // 5

			sqlBuffer.append("       wa_data.pk_financeorg pk_financeorg, "); // 5
			sqlBuffer.append(SQLHelper.getMultiLangNameColumn("org.name")
					+ "        financeorg_name, "); // 5
			sqlBuffer.append(SQLHelper.getMultiLangNameColumn("dept.name")
					+ "        financedept_name, "); // 5
			sqlBuffer.append(SQLHelper
					.getMultiLangNameColumn("bd_bankdoc.name")
					+ "     bank_name, "); // 6
			sqlBuffer.append(" bd_bankaccbas.accname    accname, "); // 7
			sqlBuffer.append("       bd_bankaccbas.accnum accnum, "); // 7
			sqlBuffer.append(SQLHelper
					.getMultiLangNameColumn("bd_banktype.name")
					+ "    banktypename, "); // 8
			sqlBuffer.append("       bd_banktype.code banktypecode, "); // 9
			sqlBuffer.append("       bd_banktype.pk_banktype, "); // 10
			sqlBuffer.append(SQLHelper
					.getMultiLangNameColumn("bd_currtype.name")
					+ "    currtypename, "); // 11
			sqlBuffer.append("       bd_currtype.pk_currtype, "); // 12
			sqlBuffer.append("       wa_data." + itemkey + " value, "); // 13
			sqlBuffer.append("       wa_data.fipendflag fipendflag, "); // 13
			sqlBuffer.append("       '" + itemAccountMap.get(itemkey)
					+ "' as cardtype, ");
			// sqlBuffer.append("       wa_dataz."+itemkey+" value_zb "); // 14
			sqlBuffer.append("       '" + itemkey + "' as itemkey ");
			sqlBuffer.append("  from wa_data ");
			sqlBuffer
					.append(" inner join wa_waclass on wa_data.pk_wa_class = wa_waclass.pk_wa_class ");
			sqlBuffer
					.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
			sqlBuffer
					.append("  left outer join bd_psnidtype on bd_psndoc.idtype =  bd_psnidtype.pk_identitype ");
			sqlBuffer
					.append("  left outer join bd_banktype on wa_data.pk_banktype"
							+ itemAccountMap.get(itemkey)
							+ " =  bd_banktype.pk_banktype ");
			sqlBuffer
					.append("  left outer join bd_bankaccbas on wa_data.pk_bankaccbas"
							+ itemAccountMap.get(itemkey)
							+ " =  bd_bankaccbas.accnum "
							+ "   and  bd_bankaccbas.accclass = 0 and bd_bankaccbas.enablestate=2 and  bd_bankaccbas.pk_banktype = bd_banktype.pk_banktype");
			sqlBuffer
					.append("  left outer join bd_bankdoc on bd_bankdoc.pk_bankdoc =  bd_bankaccbas.pk_bankdoc ");
			sqlBuffer
					.append("  left outer join bd_currtype on wa_waclass.currid = bd_currtype.pk_currtype ");
			// sqlBuffer.append("  left outer join wa_dataz on wa_dataz.pk_wa_data = wa_data.pk_wa_data ");
			sqlBuffer
					.append(" left outer join org_orgs_v on org_orgs_v.pk_vid = wa_data.workorgvid ");
			sqlBuffer
					.append(" left outer join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid ");

			sqlBuffer
					.append("  left outer join ORG_FINANCEORG org on wa_data.pk_financeorg = org.pk_financeorg ");
			sqlBuffer
					.append("  left outer join org_dept dept on wa_data.PK_FINANCEDEPT = dept.pk_dept ");
			sqlBuffer.append(whereSql);
			sqlBuffer.append(" and wa_data." + itemkey + " > 0 ");
			if (iterator.hasNext()) {
				sqlBuffer.append(" union ");
			}
		}

		if (sqlBuffer.length() > 50) {
			sqlBuffer.insert(0, "select * from (");
			sqlBuffer.append(" ) as pp order by psncode  ");
		}

		BankEnterpriseVO[] vos = baseDAOManager.executeQueryVOs(
				sqlBuffer.toString(), BankEnterpriseVO.class);

		// 2016-12-22 zhousze 薪资加密：这里处理银行报盘根据方案、项目查询对应的项目值，数据解密 begin
		for (BankEnterpriseVO vo : vos) {
			vo.setValue(new UFDouble(vo.getValue() == null ? 0
					: SalaryDecryptUtil.decrypt(vo.getValue().toDouble())));
		}
		// end

		return vos;
	}

	public int changeFipEndFlag(String[] psnjobs, WaLoginVO waloginvo,
			UFBoolean fipendflag) throws BusinessException {

		InSQLCreator inC = null;
		try {
			inC = new InSQLCreator();
			// String psnSql="";
			String sql = "";
			String year = waloginvo.getPeriodVO().getCyear();
			String whereSql = "where pk_wa_class = '"
					+ waloginvo.getPk_wa_class() + "' and cyear = '" + year
					+ "' and cperiod = '"
					+ waloginvo.getPeriodVO().getCperiod() + "' ";
			/*
			 * if (waloginvo.getLeaveflag().booleanValue()) { whereSql =
			 * "where pk_wa_class in(select pk_childclass from wa_inludeclass where pk_parentclass = '"
			 * + waloginvo.getPk_prnt_class() + "' and cyear = '"+ year +
			 * "' and cperiod = '"+ period + "' and batch >100) and cyear = '"+
			 * year + "' and cperiod = '" + period+ "' "; }
			 */
			if (psnjobs == null) {
				sql = "update wa_data set fipendflag=? " + whereSql;

			} else {
				// for(int i=0;i<psnjobs.length;i++){
				// psnSql += ", '" +psnjobs[i]+"' ";
				// }
				// psnSql = psnSql.substring(1);
				sql = "update wa_data set fipendflag=? " + whereSql
						+ " and pk_psnjob in (" + inC.getInSQL(psnjobs) + ") ";

			}
			SQLParameter parameter = new SQLParameter();
			parameter.addParam(fipendflag);
			return getBaseDao().executeUpdate(sql, parameter);
		} finally {
			inC.clear();
		}

	}

	/***************************************************************************
	 * XXX <br>
	 * Created on 2012-7-31 下午6:17:54<br>
	 * 
	 * @param waclassids
	 * @param cyear
	 * @param cperiod
	 * @return
	 * @throws BusinessException
	 * @author daicy
	 ***************************************************************************/
	public String[] getFipEndClass(String[] waclassids, String cyear,
			String cperiod) throws BusinessException {

		InSQLCreator inSQLCreator = new InSQLCreator();

		try {
			String[] pk_wa_class = null;
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer
					.append(" select distinct pk_wa_class from wa_data where ");
			sqlBuffer.append(" wa_data.pk_wa_class in( "
					+ inSQLCreator.getInSQL(waclassids) + ") ");
			sqlBuffer.append(" and wa_data.cyear = '" + cyear + "'");
			sqlBuffer.append(" and wa_data.cperiod = '" + cperiod + "'");
			sqlBuffer.append(" and wa_data.fipendflag = 'Y'");
			PayfileVO[] vos = this.executeQueryVOs(sqlBuffer.toString(),
					PayfileVO.class);

			if (vos != null) {
				pk_wa_class = new String[vos.length];
				for (int i = 0; i < vos.length; i++) {
					pk_wa_class[i] = vos[i].getPk_wa_class();
				}
			}
			return pk_wa_class;
		} finally {
			inSQLCreator.clear();
		}

	}
}