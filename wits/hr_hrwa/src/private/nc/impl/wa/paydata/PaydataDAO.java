package nc.impl.wa.paydata;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.hr.frame.persistence.AppendBaseDAO;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.wa.log.WaBusilogUtil;
import nc.impl.wa.common.WaCommonImpl;
import nc.impl.wa.end.MonthEndDAO;
import nc.impl.wa.pub.WapubDAO;
import nc.itf.hr.frame.IHRDataPermissionPubService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.wa.IAmoSchemeQuery;
import nc.itf.hr.wa.IHRWAActionCode;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.IPayfileManageService;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.jdbc.framework.JdbcPersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.BeanProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.jdbc.framework.type.SQLParamType;
import nc.jdbc.framework.type.SQLTypeFactory;
import nc.vo.dataitem.pub.DataVOUtils;
import nc.vo.hi.psndoc.Attribute;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hr.append.AppendableVO;
import nc.vo.hr.combinesort.SortVO;
import nc.vo.hr.combinesort.SortconVO;
import nc.vo.hr.itemsource.TypeEnumVO;
import nc.vo.hr.pub.FormatVO;
import nc.vo.hr.tools.dbtool.util.db.DBUtil;
import nc.vo.hr.tools.pub.HRConstEnum;
import nc.vo.hr.tools.pub.Pair;
import nc.vo.hrp.budgetitem.BudgetItemVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.BDVersionValidationUtil;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.classitempower.ItemPowerUtil;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.paydata.AggPayDataVO;
import nc.vo.wa.paydata.DataSVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.paydata.ICommonAlterName;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.SalaryPmtCommonValue;
import nc.vo.wa.pub.WaClassStateHelper;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaState;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class PaydataDAO extends AppendBaseDAO implements ICommonAlterName {

	/**
	 * �����û���Ϣ��ø��û���Ȩ�޵ĸ��ڼ��н����Ŀ
	 * 
	 * @author zhangg on 2009-11-26
	 * @param loginVO
	 * @return
	 * @throws BusinessException
	 */
	public WaClassItemVO[] getUserClassItemVOs(WaLoginContext loginVO) throws BusinessException {
		WaClassItemVO[] classitems = testgetRoleClassItemVOs(loginVO.getWaLoginVO(), null);

		return classitems;
	}

	/**
	 * ��ѯ�û�������ʾ����Ŀ
	 * 
	 * wa_classitemdsp �ñ��Ѿ�������
	 * 
	 * @author liangxr on 2010-7-2
	 * @param loginVO
	 * @return
	 * @throws BusinessException
	 */
	public WaClassItemVO[] getUserShowClassItemVOs(WaLoginContext loginVO) throws BusinessException {
		String condition = " wa_classitem.pk_wa_classitem not in (select pk_wa_classitem from wa_classitemdsp "
				+ "where pk_wa_class = '" + loginVO.getPk_wa_class() + "' and cyear = '" + loginVO.getWaYear()
				+ "' and cperiod = '" + loginVO.getWaPeriod() + "' and pk_user = '" + PubEnv.getPk_user()
				+ "' and bshow = 'N' )";
		WaClassItemVO[] classitems = testgetRoleClassItemVOs(loginVO.getWaLoginVO(), condition);
		return classitems;
	}

	public WaClassItemVO[] getRepayUserShowClassItemVOs(WaLoginContext waLoginContext) throws BusinessException {

		WaLoginVO waLoginVO = waLoginContext.getWaLoginVO();
		String condition = "";
		if ((!StringUtils.isEmpty(waLoginVO.getReperiod())) && (!waLoginVO.getReperiod().equals("-1"))) {
			condition = " wa_classitem.pk_wa_item in (select pk_wa_item from wa_classitem where pk_wa_class = '"
					+ waLoginVO.getPk_prnt_class() + "' and cyear = '" + waLoginVO.getReyear() + "' and cperiod = '"
					+ waLoginVO.getReperiod() + "' ) ";
		}

		WaClassItemVO[] classitems = testgetRoleClassItemVOs(waLoginVO, condition);
		return classitems;
	}

	/**
	 * ��ѯ����������н����Ŀ
	 * 
	 * @author liangxr on 2010-7-2
	 * @param loginVO
	 * @return
	 * @throws BusinessException
	 */
	public WaClassItemVO[] getApprovedClassItemVOs(WaLoginContext loginVO) throws BusinessException {
		String condition = " wa_classitem.inapproveditem = 'Y'";
		WaClassItemVO[] classitems = null;
		if ("60130payslipaly".equals(loginVO.getNodeCode())) {
			classitems = testgetRoleClassItemVOs(loginVO.getWaLoginVO(), condition);
		} else {
			classitems = getRoleClassItemVOsNoPower(loginVO.getWaLoginVO(), condition);
		}
		return classitems;
	}

	/**
	 * �ж�һ�������������ǵ��λ��Ƕ�Σ����Ƕ���е�һ�Σ��Ƿ��Ѿ����Ź���һ�Σ�
	 * 
	 * @param pk_wa_data
	 * @return
	 * @throws BusinessException
	 */
	public boolean isAnyTimesPayed(String pk_wa_class, String cyear, String cperiod) throws BusinessException {
		StringBuilder sbd = new StringBuilder();
		sbd.append("  select top 1 wa_periodstate.pk_wa_period from wa_periodstate,wa_period ");
		sbd.append("  	where wa_periodstate.pk_wa_period = wa_period.pk_wa_period  ");
		sbd.append("  	 and wa_period.cyear = ?  ");
		sbd.append("  	and wa_period.cperiod=? and wa_periodstate.payoffflag='Y' ");
		sbd.append("  and ( wa_periodstate.pk_wa_class  in ( ");
		sbd.append("  select pk_childclass  from wa_inludeclass where pk_parentclass = ? and wa_inludeclass.cyear = ? and wa_inludeclass.cperiod = ?  ");
		sbd.append("   ) or  wa_periodstate.pk_wa_class  = ? ) ");

		SQLParameter para = new SQLParameter();
		para.addParam(cyear);
		para.addParam(cperiod);
		para.addParam(pk_wa_class);
		para.addParam(cyear);
		para.addParam(cperiod);
		para.addParam(pk_wa_class);

		return isValueExist(sbd.toString(), para);
	}

	/**
	 * н�����
	 * 
	 * @author zhangg on 2009-12-2
	 * @param waLoginVO
	 * @param whereCondition
	 * @param isRangeAll
	 * @throws nc.vo.pub.BusinessException
	 */
	public boolean onCheck(WaLoginVO waLoginVO, String whereCondition, Boolean isRangeAll)
			throws nc.vo.pub.BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("   wa_data.checkflag = 'N' "); // 3
		sqlBuffer.append("   and wa_data.stopflag = 'N' and wa_data.caculateflag='Y' "); // 5
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(getCommonWhereCondtion4Data(waLoginVO)));

		if (!isRangeAll) {
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(whereCondition));
		}
		// ������wa_data����˱�־
		updateTableByColKey("wa_data", PayfileVO.CHECKFLAG, UFBoolean.TRUE, sqlBuffer.toString());

		// �鿴н�������Ƿ�ȫ�����
		sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_data.pk_wa_data "); // 1
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" where wa_data.checkflag = 'N' ");
		sqlBuffer.append("   and wa_data.stopflag = 'N' ");
		// ����Ȩ��guoqt����ʹ��Ȩ
		// sqlBuffer.append(WherePartUtil.formatAddtionalWhere(getCommonWhereCondtion4Data(waLoginVO)));
		sqlBuffer.append("   and wa_data.pk_wa_class ='" + waLoginVO.getPk_wa_class() + "' ");
		sqlBuffer.append("   and wa_data.cyear ='" + waLoginVO.getCyear() + "' ");
		sqlBuffer.append("   and wa_data.cperiod ='" + waLoginVO.getCperiod() + "' ");

		boolean isAllChecked = !isValueExist(sqlBuffer.toString());
		if (isAllChecked) {
			// �����ڼ����˱�־
			updatePeriodState(PeriodStateVO.CHECKFLAG, UFBoolean.TRUE, waLoginVO);
			return true;
		}
		return false;
	}

	public void onUnCheck(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll)
			throws nc.vo.pub.BusinessException {
		// �������������������������еĵ�������ȡ����//�粻��Ҫ�������Ѿ����ӵ����������еģ�����ȡ�����
		PeriodStateVO period = waLoginVO.getPeriodVO();
		if (period != null) {
			if (period.getIsapproved().booleanValue()) {
				throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0447")/*
																									 * @
																									 * res
																									 * "��н�ʷ����Ѿ�����ͨ�����޷�ȡ����ˣ�"
																									 */);// "��н������Ѿ�����ͨ�����޷�ȡ����ˣ�"

			}
			Boolean isInApproveing = new WapubDAO().isInApproveing(waLoginVO);
			if (isInApproveing) {
				throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0448")/*
																									 * @
																									 * res
																									 * "��н�ʷ����Ѿ��ӵ����������У��޷�ȡ����ˣ�"
																									 */);// "��н������Ѿ��ӵ����������У��޷�ȡ����ˣ�"
			}
		}
		// ͬһ������ڼ��·����Ƿ�����, У��ϲ���˰
		String caccyear = waLoginVO.getPeriodVO().getCaccyear();
		String caccperiod = waLoginVO.getPeriodVO().getCaccperiod();
		// String sqlWhereAccPeriod = " cpreclassid = '" +
		// waLoginVO.getPk_wa_class()
		// + "' and checkflag = 'Y'";
		// sqlWhereAccPeriod += " and caccyear='" + caccyear +
		// "' and caccperiod='" + caccperiod + "'";
		// StringBuffer sqlBuffer = new
		// StringBuffer(WapubDAO.getPeriodViewTable());
		// sqlBuffer.append(WherePartUtil.addWhereKeyWord2Condition(sqlWhereAccPeriod));
		// if (isValueExist(sqlBuffer.toString())) {
		// throw new nc.vo.pub.BusinessException(MessageFormat.format(
		// ResHelper.getString("60130paydata","060130paydata0469")
		// /*@res "{0}���ñ����������ݽ����˼��㣬����ˣ�����������ȡ����ˣ�"*/,
		// getRelateClassName(waLoginVO)));
		// }
		checkTaxRelate(waLoginVO, whereCondition, isRangeAll);

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("   wa_data.stopflag = 'N' "); // 5
		sqlBuffer.append("   and wa_data.checkflag = 'Y' "); // 6
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(getCommonWhereCondtion4Data(waLoginVO)));
		if (!isRangeAll) {
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(whereCondition));
		}
		dealInPayLeavePsn(waLoginVO, whereCondition, isRangeAll);

		// ������wa_data��prewadata
		updateTableByColKey("wa_data", "prewadata", SQLTypeFactory.getNullType(Types.CHAR), sqlBuffer.toString());

		// ������wa_data����˱�־
		updateTableByColKey("wa_data", PayfileVO.CHECKFLAG, UFBoolean.FALSE, sqlBuffer.toString());

		// �������ڼ����˱�־
		updatePeriodState(PeriodStateVO.CHECKFLAG, UFBoolean.FALSE, waLoginVO);

		// ���úϲ���˰���÷����ļ���״̬
		String sqlWhereAccPeriod = " cpreclassid = '" + waLoginVO.getPk_wa_class() + "'";
		sqlWhereAccPeriod += " and pk_wa_period in(select pk_wa_period from wa_period where caccyear='" + caccyear
				+ "' and caccperiod='" + caccperiod + "')";
		updateTableByColKey("wa_periodstate", "caculateflag", UFBoolean.FALSE,
				WherePartUtil.addWhereKeyWord2Condition(sqlWhereAccPeriod));

		// ȡ�����ʱ��wa_data�� prewadata ���
	}

	private void checkTaxRelate(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll)
			throws BusinessException {

		String addSql = getCommonWhereCondtion4Data(waLoginVO);
		if (!isRangeAll && whereCondition != null && whereCondition.length() > 0) {
			addSql += " and wa_data.pk_wa_data in(select pk_wa_data from wa_data where " + whereCondition + ")";
		}

		String sql = "select "
				+ SQLHelper.getMultiLangNameColumn("b1.name")
				+ " as psnname,"
				+ SQLHelper.getMultiLangNameColumn("w1.name")
				+ " as classname,w2.batch "
				+ "from  wa_data d2 "
				+ "INNER JOIN wa_waclass w1 ON d2.pk_wa_class = w1.pk_wa_class "
				+ "LEFT OUTER JOIN wa_inludeclass w2 	ON w2.pk_childclass = d2.pk_wa_class AND w2.cyear = d2.cyear AND w2.cperiod = d2.cperiod  "
				+ "INNER JOIN bd_psndoc b1  ON b1.pk_psndoc = d2.pk_psndoc WHERE "
				+ " d2.prewadata in(select pk_wa_data from wa_data where " + addSql + ") AND d2.checkflag = 'Y'  ";

		List result = (List) getBaseDao().executeQuery(sql, new MapListProcessor());
		if (result == null || result.size() == 0) {
			return;
		}
		Map map = null;
		Integer batch = 0;
		StringBuffer msg = new StringBuffer();
		// 2015-10-26 zhousze н�ʷ��źϲ���˰����ȡ�������ʾ���� begin
		msg.append(ResHelper.getString("60130paydata", "060130paydata0473"))/*
																			 * @res
																			 * "������Ա�������ѱ����ã��޷�ȡ����ˣ�\r\n"
																			 */;
		// end
		for (int i = 0; i < result.size(); i++) {
			map = (Map) result.get(i);
			batch = ((Integer) map.get("batch"));
			msg.append("[" + map.get("psnname"));
			msg.append(":" + map.get("classname"));
			if (batch == null || batch == 0) {
				// do nothing
			} else if (batch > 100) {
				msg.append(ResHelper.getString("60130paydata", "060130paydata0470"))/*
																					 * @
																					 * res
																					 * "-��ְ��н"
																					 */;
			} else {
				msg.append(MessageFormat.format(ResHelper.getString("60130paydata", "060130paydata0471")/*
																										 * @
																										 * res
																										 * "{0}�η�н"
																										 */,
						nc.vo.format.FormatGenerator.getIndexFormat().format(batch)));
			}
			msg.append("]");
		}
		throw new BusinessException(msg.toString());
	}

	/**
	 * ɾ����ְ��н�е���Ա
	 * 
	 * @param waLoginVO
	 * @param whereCondition
	 * @param isRangeAll
	 * @throws BusinessException
	 */
	private void dealInPayLeavePsn(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll)
			throws BusinessException {
		String sql = "select * FROM wa_data " + "WHERE wa_data.pk_wa_class IN( SELECT w2.pk_childclass "
				+ "								   FROM wa_inludeclass w2 " + "								  WHERE w2.pk_parentclass = ? "
				+ "									AND w2.cyear = ? " + "									AND w2.cperiod = ? )" + "	AND wa_data.cyear = ? "
				+ "	AND wa_data.cperiod =? " + "	AND wa_data.checkflag = 'N' "
				+ "	AND wa_data.pk_psndoc IN( SELECT pk_psndoc " + "								FROM wa_data w1 "
				+ "							   WHERE w1.pk_wa_class = ? " + "								 AND w1.cyear = ? " + "								 AND w1.cperiod = ? "
				+ "								 AND w1.checkflag = 'Y')";
		if (!isRangeAll) {
			sql += WherePartUtil.formatAddtionalWhere(whereCondition);
		}
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_prnt_class());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());
		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());
		List<PayfileVO> delVos = (List<PayfileVO>) getBaseDao().executeQuery(sql, parameter,
				new BeanListProcessor(PayfileVO.class));

		NCLocator.getInstance().lookup(IPayfileManageService.class).delPsnVOs(delVos.toArray(new PayfileVO[0]));

	}

	/**
	 * ȡ�����ʱУ����ְ��н
	 * 
	 * @param waLoginVO
	 * @param whereCondition
	 * @param isRangeAll
	 * @return
	 * @throws DAOException
	 */
	public PayfileVO[] getInPayLeavePsn(WaLoginVO waLoginVO, String whereCondition, boolean isRangeAll)
			throws DAOException {
		String sql = "select bd_psndoc.code as psncode," + SQLHelper.getMultiLangNameColumn("bd_psndoc.name")
				+ " as psnname " + "from wa_data ,bd_psndoc " + "where wa_data.pk_psndoc = bd_psndoc.pk_psndoc "
				+ " and wa_data.pk_wa_class = ? " + "	and wa_data.cyear = ? " + "	and wa_data.cperiod = ? "
				+ " and wa_data.checkflag = 'Y' " + "	and wa_data.pk_psndoc in(select w1.pk_psndoc "
				+ "						from wa_data w1 ,wa_inludeclass w2 " + "						where w1.pk_wa_class = w2.pk_childclass "
				+ "							and w1.cyear = w2.cyear " + "							and w1.cperiod = w2.cperiod "
				+ "							and w2.batch >100 " + "							and w1.checkflag = 'N' " + "							and w2.pk_parentclass = ? "
				+ "							and w1.cyear = ? " + "							and w1.cperiod = ? " + "	)";
		if (!isRangeAll && whereCondition != null && whereCondition.length() > 0) {
			sql += " and wa_data.pk_wa_data in(select pk_wa_data from wa_data where " + whereCondition + ")";
		}
		SQLParameter parameter = getCommonParameter(waLoginVO);
		parameter.addParam(waLoginVO.getPk_prnt_class());
		parameter.addParam(waLoginVO.getCyear());
		parameter.addParam(waLoginVO.getCperiod());
		PayfileVO[] vos = executeQueryVOs(sql, parameter, PayfileVO.class);
		return vos;
	}

	/**
	 * ȡ��������������
	 * 
	 * @param waLoginVO
	 * @return
	 * @throws DAOException
	 */
	public String getRelateClassName(WaLoginVO waLoginVO) throws DAOException {
		String sql = "select w1.name,w2.batch from wa_periodstate p1,wa_waclass w1,wa_inludeclass w2 "
				+ "where  p1.pk_wa_class = w1.pk_wa_class " + "and w1.pk_wa_class = w2.pk_childclass "
				+ "and p1.cpreclassid = ? " + "and w2.cyear = ? " + "and w2.cperiod = ? ";
		SQLParameter parameter = getCommonParameter(waLoginVO);
		Map map = (Map) getBaseDao().executeQuery(sql, parameter, new MapProcessor());
		String name = map.get("name").toString();
		int batch = Integer.parseInt(map.get("batch").toString());
		if (batch > 100) {
			return name + ResHelper.getString("60130paydata", "060130paydata0470")
			/* @res "-��ְ��н" */;
		}

		return name + MessageFormat.format(ResHelper.getString("60130paydata", "060130paydata0471")
		/* @res "{0}�η�н" */, nc.vo.format.FormatGenerator.getIndexFormat().format(batch));

	}

	/**
	 * �����������Ƿ����ύ̬
	 * 
	 * @author liangxr on 2010-6-1
	 * @param waLoginVO
	 * @return
	 * @throws DAOException
	 */
	public boolean isPayrollSubmit(WaLoginVO waLoginVO) throws DAOException {
		String classid = waLoginVO.getPk_wa_class();
		String cyear = waLoginVO.getCyear();
		String cperiod = waLoginVO.getCperiod();

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select 1 from wa_periodstate ");
		sqlBuffer.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
		sqlBuffer.append("  wa_period.pk_wa_period and wa_periodstate.enableflag = 'Y') ");
		sqlBuffer.append("where wa_periodstate.isapproved != 'Y' ");
		sqlBuffer.append("and exists (select 1 from wa_payroll  ");
		sqlBuffer.append("where wa_payroll.pk_wa_class = wa_periodstate.pk_wa_class ");
		sqlBuffer.append(" and wa_payroll.billstate='").append(IPfRetCheckInfo.COMMIT);
		sqlBuffer.append("' and wa_payroll.pk_wa_class = '").append(classid);
		sqlBuffer.append("' and wa_payroll.cyear = '").append(cyear);
		sqlBuffer.append("' and wa_payroll.cperiod = '").append(cperiod);
		sqlBuffer.append("') and wa_periodstate.pk_wa_class = '").append(classid);
		sqlBuffer.append("' and wa_period.cyear = '").append(cyear);
		sqlBuffer.append("' and wa_period.cperiod = '").append(cperiod).append("' ");

		return isValueExist(sqlBuffer.toString());
	}

	/**
	 * �ж������������Ƿ�������̬
	 * 
	 * @author liangxr on 2010-6-1
	 * @param waLoginVO
	 * @return
	 * @throws DAOException
	 */
	public boolean isPayrollFree(WaLoginVO waLoginVO) throws DAOException {
		String classid = waLoginVO.getPk_wa_class();
		String cyear = waLoginVO.getCyear();
		String cperiod = waLoginVO.getCperiod();

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select 1 from wa_periodstate ");
		sqlBuffer.append(" inner join wa_period on (wa_periodstate.pk_wa_period = ");
		sqlBuffer.append(" wa_period.pk_wa_period and wa_periodstate.enableflag = 'Y') ");
		sqlBuffer.append("where wa_periodstate.isapproved != 'Y' ");
		sqlBuffer.append("and exists (select 1 from wa_payroll  ");
		sqlBuffer.append("where wa_payroll.pk_wa_class = wa_periodstate.pk_wa_class ");
		sqlBuffer.append(" and wa_payroll.billstate='").append(IPfRetCheckInfo.NOSTATE);
		sqlBuffer.append("' and wa_payroll.pk_wa_class = '").append(classid);
		sqlBuffer.append("' and wa_payroll.cyear = '").append(cyear);
		sqlBuffer.append("' and wa_payroll.cperiod = '").append(cperiod);
		sqlBuffer.append("') and wa_periodstate.pk_wa_class = '").append(classid);
		sqlBuffer.append("' and wa_period.cyear = '").append(cyear);
		sqlBuffer.append("' and wa_period.cperiod = '").append(cperiod).append("' ");

		return isValueExist(sqlBuffer.toString());
	}

	/**
	 * ��ȡн�ʷ�����Ŀ
	 * 
	 * @author liangxr on 2010-5-13
	 * @param waLoginVO
	 * @return
	 * @throws BusinessException
	 */
	private WaClassItemVO[] testgetRoleClassItemVOs(WaLoginVO waLoginVO, String condition) throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select wa_item.itemkey, wa_item.iitemtype,wa_item.defaultflag, ");
		sqlBuffer.append(" wa_item.ifldwidth,wa_item.category_id, ");
		sqlBuffer.append(" wa_classitem.*, 'Y' editflag,");
		sqlBuffer.append(" 'Y' as showflag,");
		sqlBuffer.append(" wa_classitem.idisplayseq as idisplayseq,  ");
		sqlBuffer.append("itempower.editflag ");
		sqlBuffer.append("from wa_classitem , wa_item,");
		sqlBuffer.append("(SELECT pk_wa_item,MAX(editflag) as editflag");
		sqlBuffer.append("   FROM wa_itempower ");
		// �ж��Ƿ��Ƕ�η���
		// WaClassVO parentvo = NCLocator.getInstance().lookup(IWaClass.class)
		// .queryParentClass(waLoginVO.getPk_wa_class(),
		// waLoginVO.getCyear(), waLoginVO.getCperiod());

		sqlBuffer.append("  WHERE pk_wa_class = '" + waLoginVO.getPk_prnt_class() + "'");
		sqlBuffer.append("    AND pk_group ='" + waLoginVO.getPk_group() + "'");
		sqlBuffer.append("    AND pk_org = '" + waLoginVO.getPk_org() + "'");
		sqlBuffer.append("    AND ( pk_subject IN(SELECT pk_role ");
		sqlBuffer.append("				       FROM sm_user_role ");
		sqlBuffer.append("				      WHERE cuserid = '" + PubEnv.getPk_user() + "'");
		sqlBuffer.append("                   ) or pk_subject = '" + PubEnv.getPk_user() + "') ");
		sqlBuffer.append("  GROUP BY pk_wa_item ) as itempower");
		sqlBuffer.append(" where wa_classitem.pk_wa_item = wa_item.pk_wa_item ");
		sqlBuffer.append(" and wa_classitem.pk_wa_item = itempower.pk_wa_item ");
		sqlBuffer.append(" and wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append(" and wa_classitem.cyear = ?  and wa_classitem.cperiod = ? ");
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition));
		sqlBuffer.append(" order by wa_classitem.idisplayseq");

		SQLParameter parameter = getCommonParameter(waLoginVO);
		return executeQueryVOs(sqlBuffer.toString(), parameter, WaClassItemVO.class);
	}

	/**
	 * ��ȡн�ʷ�����Ŀ
	 * 
	 * @author liangxr on 2010-5-13
	 * @param waLoginVO
	 * @return
	 * @throws BusinessException
	 */
	private WaClassItemVO[] getRoleClassItemVOsNoPower(WaLoginVO waLoginVO, String condition) throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select wa_item.itemkey, wa_item.iitemtype,wa_item.defaultflag, ");
		sqlBuffer.append(" wa_item.ifldwidth,wa_item.category_id, ");
		sqlBuffer.append(" wa_classitem.*, 'Y' editflag,");
		sqlBuffer.append("isnull(wa_classitemdsp.bshow,'Y') as showflag,");
		sqlBuffer.append("isnull(wa_classitemdsp.displayseq,wa_classitem.idisplayseq) as idisplayseq ");
		sqlBuffer.append("from wa_classitem LEFT OUTER JOIN wa_classitemdsp  ");
		sqlBuffer.append(" ON wa_classitem.pk_wa_class = wa_classitemdsp.pk_wa_class  ");
		sqlBuffer.append(" AND wa_classitem.cyear = wa_classitemdsp.cyear  ");
		sqlBuffer.append(" AND wa_classitem.cperiod = wa_classitemdsp.cperiod  ");
		sqlBuffer
				.append(" AND wa_classitem.pk_wa_classitem = wa_classitemdsp.pk_wa_classitem and wa_classitemdsp.pk_user = '"
						+ PubEnv.getPk_user() + "' , wa_item ");
		sqlBuffer.append(" where wa_classitem.pk_wa_item = wa_item.pk_wa_item ");
		sqlBuffer.append(" and wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append(" and wa_classitem.cyear = ?  and wa_classitem.cperiod = ? ");
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition));
		sqlBuffer.append(" order by wa_classitem.idisplayseq");

		SQLParameter parameter = getCommonParameter(waLoginVO);
		return executeQueryVOs(sqlBuffer.toString(), parameter, WaClassItemVO.class);
	}

	public DataSVO[] getDataSVOs(WaLoginContext loginContext) throws BusinessException {
		// TODO ������ԱȨ��
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_datas.pk_wa_data, "); // 1
		sqlBuffer.append("       wa_datas.pk_wa_datas, "); // 2
		sqlBuffer.append("       wa_datas.pk_wa_classitem, "); // 3
		sqlBuffer.append("       wa_datas.ts, "); // 4
		sqlBuffer.append("       wa_datas.value, "); // 5
		sqlBuffer.append("       wa_datas.to_next, "); // 5
		sqlBuffer.append("       wa_datas.caculatevalue, "); // 6
		sqlBuffer.append("       wa_datas.notes, "); // 7
		sqlBuffer.append("       wa_item.itemkey itemkey, "); // 8
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("wa_classitem.name") + "  itemname, "); // 8
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + PSNNAME + ", "); // 9
		sqlBuffer.append("       wa_classitem.iflddecimal, "); // 10
		sqlBuffer.append("       bd_psndoc.code " + PSNCODE + ", "); // 11
		sqlBuffer.append("       hi_psnjob.clerkcode, "); // 3
		sqlBuffer.append("       wa_data.checkflag "); // 12
		sqlBuffer.append("  from wa_datas ");
		sqlBuffer.append(" inner join wa_data on wa_datas.pk_wa_data = wa_data.pk_wa_data ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join wa_classitem on wa_datas.pk_wa_classitem = wa_classitem.pk_wa_classitem ");
		sqlBuffer.append(" inner join wa_item on wa_classitem.pk_wa_item = wa_item.pk_wa_item ");

		sqlBuffer.append(WherePartUtil.addWhereKeyWord2Condition(getCommonWhereCondtion4Data(loginContext
				.getWaLoginVO())));
		String powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(), IHRWADataResCode.WADATA,
				IHRWAActionCode.SpecialPsnAction, "hi_psnjob");
		if (!StringUtils.isBlank(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		sqlBuffer.append("  and  wa_item.pk_wa_item in (" + ItemPowerUtil.getItemPower(loginContext) + ")");
		sqlBuffer.append(" order by wa_datas.pk_wa_data, wa_datas.pk_wa_classitem");
		return executeQueryVOs(sqlBuffer.toString(), DataSVO.class);
	}

	public DataVO[] queryByCondition(WaLoginContext context, String condition, String orderCondtion, String selectString)
			throws BusinessException {
		DataVO[] dataContainer = queryWaDataByCondition(context, condition, orderCondtion, selectString);
		DataVO[] dataAppends = queryAppendDataByCondition(context, condition, orderCondtion);
		return combineDataParts(dataContainer, dataAppends);
	}

	public DataVO[] queryByCondition(WaLoginContext context, String condition, String orderCondtion)
			throws BusinessException {
		DataVO[] dataContainer = queryWaDataByCondition(context, condition, orderCondtion, "wa_data.*");
		DataVO[] dataAppends = queryAppendDataByCondition(context, condition, orderCondtion);
		return combineDataParts(dataContainer, dataAppends);

	}

	private DataVO[] combineDataParts(DataVO[] dataContainer, DataVO[] dataAppends) {
		List<DataVO> data = new ArrayList<DataVO>();
		List<String> fieldsInDataAppends = Arrays.asList(new String[] { "psnname", "psncode", "idtype", "clerkcode",
				"id", "deptname", "orgname", "plsname", "financeorg", "financedept", "liabilityorg", "liabilitydept",
				"jobname", "postname" });
		if (dataContainer != null && dataContainer.length > 0) {
			for (DataVO vo : dataContainer) {
				DataVO newData = vo;
				String pk_wa_data = vo.getPk_wa_data();
				if (dataAppends != null && dataAppends.length > 0) {
					for (DataVO appendvo : dataAppends) {
						if (pk_wa_data.equals(appendvo.getPk_wa_data())) {
							for (String attname : appendvo.getAttributeNames()) {
								if (fieldsInDataAppends.contains(attname)) {
									newData.setAttributeValue(attname, appendvo.getAttributeValue(attname));
								}
							}
							break;
						}
					}
				}
				data.add(newData);
			}
		}
		return data.toArray(new DataVO[0]);
	}

	private DataVO[] queryAppendDataByCondition(WaLoginContext context, String condition, String orderCondtion)
			throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + PSNNAME + ", "); // 1
		// shenliangc 20140702 ���б������ӻ���
		sqlBuffer.append("       bd_psndoc.code " + PSNCODE + ",  "); // 2
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psnidtype.name") + "  as idtype, "); // 4
		sqlBuffer.append("       hi_psnjob.clerkcode, bd_psndoc.id as id, "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + DEPTNAME + ", "); // 4
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  " + ORGNAME + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  " + PLSNAME + ", "); // 5
		// shenliangc 20140702 ���б������ӻ���
		// guoqt�׽�������
		// 2015-07-30 zhosuze NCdp205099799 н�ʷ��Ž������Ӳ�����֯������ begin
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("financeorg.name") + "  " + FINANCEORG + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("financedept.name") + "  " + FINANCEDEPT + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("liabilityorg.name") + "  " + LIABILITYORG
				+ ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("liabilitydept.name") + "  " + LIABILITYDEPT
				+ ", "); // 3
		// end

		sqlBuffer.append("       om_job.jobname, "); // 6
		// guoqt��λ
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  " + POSTNAME + ", "); // 6
		sqlBuffer.append("       wa_data.pk_wa_data "); // 7
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" left join org_orgs_v on org_orgs_v.pk_vid = wa_data.workorgvid ");
		sqlBuffer.append(" left join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid ");
		sqlBuffer.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		// shenliangc 20140702 ���б������ӻ���
		// 2015-07-30 zhosuze NCdp205099799 н�ʷ��Ž������Ӳ�����֯������ begin
		sqlBuffer.append("  left join org_orgs financeorg on wa_data.pk_financeorg = financeorg.pk_org ");
		sqlBuffer.append("  left join org_dept financedept on wa_data.pk_financedept = financedept.pk_dept ");
		sqlBuffer.append("  left join org_orgs liabilityorg on wa_data.pk_liabilityorg = liabilityorg.pk_org ");
		sqlBuffer.append("  left join org_dept liabilitydept on wa_data.pk_liabilitydept = liabilitydept.pk_dept ");
		// end

		sqlBuffer.append("  left outer join bd_psnidtype on bd_psndoc.idtype = bd_psnidtype.pk_identitype ");
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		// TODO ���Ʋ�ѯ
		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");
			sqlBuffer.append(sqlpart);
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition)).append(")");
		}
		// guoqt н�ʷ��ŵ�������Ȩ�޿���
		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		} else {
			sqlBuffer.append(" order by org_dept_v.code,hi_psnjob.clerkcode");
		}
		return executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);
	}

	private DataVO[] queryWaDataByCondition(WaLoginContext context, String condition, String orderCondtion,
			String selectString) throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  ");
		sqlBuffer.append(selectString); // 7
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" left join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid ");
		sqlBuffer.append("  where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		// TODO ���Ʋ�ѯ
		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");
			sqlBuffer.append(sqlpart);
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition)).append(")");
		}
		// guoqt н�ʷ��ŵ�������Ȩ�޿���
		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		// if (!StringUtil.isEmpty(orderCondtion)) {
		// sqlBuffer.append(" order by ").append(orderCondtion);
		// } else {
		// sqlBuffer.append(" order by org_dept_v.code,hi_psnjob.clerkcode");
		// }
		return executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);
	}

	public DataVO[] queryByConditionWithItem(WaLoginContext context, String orderCondtion, WaClassItemVO[] classItemVOs)
			throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + PSNNAME + ", "); // 1
		sqlBuffer.append("       bd_psndoc.code " + PSNCODE + ", "); // 2
		sqlBuffer.append("       hi_psnjob.clerkcode, "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + DEPTNAME + ", "); // 4
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  " + ORGNAME + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  " + PLSNAME + ", "); // 5
		sqlBuffer.append("       om_job.jobname, "); // 6
		sqlBuffer.append("       om_post.postname, "); // 6
		sqlBuffer.append("       wa_data.f_1,wa_data.f_3 "); // 7
		if (!ArrayUtils.isEmpty(classItemVOs)) {
			for (WaClassItemVO classItemVO : classItemVOs) {
				sqlBuffer.append("       ,wa_data." + classItemVO.getItemkey()); // 7
			}
		}
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" left join org_orgs_v on org_orgs_v.pk_vid = wa_data.workorgvid ");
		sqlBuffer.append(" left join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid ");
		sqlBuffer.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		// // TODO ���Ʋ�ѯ
		// if (!StringUtil.isEmpty(condition)) {
		// sqlBuffer
		// .append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");
		// sqlBuffer.append(sqlpart);
		// sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition))
		// .append(")");
		// }
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		} else {
			sqlBuffer.append(" order by org_dept_v.code,hi_psnjob.clerkcode");
		}
		return executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);

	}

	public DataVO[] querySumDataByCondition(WaLoginContext context, String orderCondtion, WaClassItemVO[] classItemVOs)
			throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  "); // 1
		sqlBuffer.append("  org_orgs_v.code orgcode ,"); // 2
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  " + ORGNAME + ", "); // 3
		sqlBuffer.append("  org_dept_v.code deptcode ,"); // 2
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + DEPTNAME + ", "); // 4
		sqlBuffer.append("   count(wa_data.pk_psnjob ) psnnum"); // 7
		if (!ArrayUtils.isEmpty(classItemVOs)) {
			for (WaClassItemVO classItemVO : classItemVOs) {
				if (classItemVO.getIitemtype() != null
						&& classItemVO.getIitemtype().intValue() == TypeEnumVO.FLOATTYPE.value().intValue()) {
					sqlBuffer.append("       ,sum(" + classItemVO.getItemkey() + ") " + classItemVO.getItemkey()); // 7
				}
			}
		}

		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" left join org_orgs_v on org_orgs_v.pk_vid = wa_data.workorgvid ");
		sqlBuffer.append(" left join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		// // TODO ���Ʋ�ѯ
		// if (!StringUtil.isEmpty(condition)) {
		// sqlBuffer
		// .append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");
		// sqlBuffer.append(sqlpart);
		// sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition))
		// .append(")");
		// }
		sqlBuffer.append(" group by org_orgs_v.code," + SQLHelper.getMultiLangNameColumn("org_orgs_v.name")
				+ ",org_dept_v.code," + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + " ");
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		} else {
			sqlBuffer.append(" order by org_dept_v.code");
		}
		return executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);

	}

	public DataVO[] queryByPKSCondition(String condition, String orderCondtion) throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + PSNNAME + ", "); // 1
		sqlBuffer.append("       bd_psndoc.code " + PSNCODE + ", "); // 2
		sqlBuffer.append("       hi_psnjob.clerkcode, "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + DEPTNAME + ", "); // 4
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  " + ORGNAME + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  " + PLSNAME + ", "); // 5
		// shenliangc 20140702 ���б������ӻ���
		// 2015-07-30 zhosuze NCdp205099799 н�ʷ��Ž������Ӳ�����֯������ begin
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("financeorg.name") + "  " + FINANCEORG + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("financedept.name") + "  " + FINANCEDEPT + ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("liabilityorg.name") + "  " + LIABILITYORG
				+ ", "); // 3
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("liabilitydept.name") + "  " + LIABILITYDEPT
				+ ", "); // 3
		// end

		sqlBuffer.append("       om_job.jobname, "); // 6
		// guoqt��λ
		sqlBuffer.append("        " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  " + POSTNAME + ", "); // 6
		sqlBuffer.append("       wa_data.*,datapower.operateflag "); // 7
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" left join org_orgs_v on org_orgs_v.pk_vid = wa_data.workorgvid ");
		sqlBuffer.append(" left join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid ");
		sqlBuffer.append(" left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");
		// shenliangc 20140702 ���б������ӻ���
		// 2015-07-30 zhosuze NCdp205099799 н�ʷ��Ž������Ӳ�����֯������ begin
		sqlBuffer.append("  left join org_orgs financeorg on wa_data.pk_financeorg = financeorg.pk_org ");
		sqlBuffer.append("  left join org_dept financedept on wa_data.pk_financedept = financedept.pk_dept ");
		sqlBuffer.append("  left join org_orgs liabilityorg on wa_data.pk_liabilityorg = liabilityorg.pk_org ");
		sqlBuffer.append("  left join org_dept liabilitydept on wa_data.pk_liabilitydept = liabilitydept.pk_dept ");
		// end

		// String operateConditon = NCLocator
		// .getInstance()
		// .lookup(IDataPermissionPubService.class)
		// .getDataPermissionSQLWherePartByMetaDataOperation(
		// PubEnv.getPk_user(), IHRWADataResCode.WADATA,
		// IHRWAActionCode.EDIT, PubEnv.getPk_group());

		// 20150703 xiejie3 ȡ��ά��Ȩ�ޣ����operateConditonΪ�գ���˵���������е�Ȩ�ޡ�begin
		String operateConditon = NCLocator
				.getInstance()
				.lookup(IHRDataPermissionPubService.class)
				.getDataRefSQLWherePartByMDOperationCode(PubEnv.getPk_user(), PubEnv.getPk_group(),
						IHRWADataResCode.WADATA, IHRWAActionCode.EDIT, "wa_data");
		if (StringUtils.isEmpty(operateConditon)) {
			operateConditon = " 1 = 1 ";
		}
		// end

		sqlBuffer.append(" left outer join (select 'Y' as operateflag,pk_wa_data from wa_data  where ");
		sqlBuffer.append(operateConditon);
		sqlBuffer.append(") datapower on wa_data.pk_wa_data = datapower.pk_wa_data ");
		// TODO ���Ʋ�ѯ
		if (!StringUtil.isEmpty(condition)) {

			sqlBuffer.append(" where  wa_data.pk_wa_data in (" + condition + ")");

		}
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		}

		return executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);

	}

	public String[] queryPKSByCondition(WaLoginContext context, String condition, String orderCondtion)
			throws BusinessException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select ");
		sqlBuffer.append("       wa_data.pk_wa_data "); // 7
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" left join org_orgs_v on wa_data.WORKORGVID = org_orgs_v.PK_VID ");
		sqlBuffer.append(" LEFT OUTER JOIN org_dept_v ON wa_data.WORKDEPTVID = org_dept_v.PK_VID  ");
		sqlBuffer.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		// TODO ���Ʋ�ѯ
		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");
			sqlBuffer.append(sqlpart);
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition)).append(")");
		}
		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		if (!StringUtil.isEmpty(orderCondtion)) {
			sqlBuffer.append(" order by ").append(orderCondtion);
		}
		DataVO[] vos = executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);

		String[] pks = new String[0];
		if (vos != null) {
			pks = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				pks[i] = vos[i].getPk_wa_data();
			}

		}
		return pks;

	}

	/**
	 * ��ѯн�ʷ�������
	 * 
	 * @author zhangg on 2009-12-10
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggPayDataVO queryAggPayDataVOByCondition(WaLoginContext loginContext, String condition, String orderCondtion)
			throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();

		// н�������Ϣ
		WaLoginVO waLoginVO = WaClassStateHelper.getWaclassVOWithState(loginContext.getWaLoginVO());
		aggPayDataVO.setLoginVO(waLoginVO);

		// ��Ȩ�޵�н����Ŀ
		WaClassItemVO[] classItemVOs = getUserClassItemVOs(loginContext);
		aggPayDataVO.setClassItemVOs(classItemVOs);
		// н�ʷ������ݡ� Ӧ�ò���Ҫ��
		// DataVO[] dataVOs = queryByCondition(loginContext, condition,
		// orderCondtion);
		// aggPayDataVO.setDataVOs(dataVOs);

		String[] pks = queryPKSByCondition(loginContext, condition, orderCondtion);
		aggPayDataVO.setDataPKs(pks);
		aggPayDataVO.setSumData(querySumData(loginContext, condition, classItemVOs));
		DataSVO[] dsvos = getDataSVOs(loginContext);

		aggPayDataVO.setDataSmallVO(dsvos);

		return aggPayDataVO;
	}

	public DataVO querySumData(WaLoginContext context, String condition, WaClassItemVO[] classItemVOs)
			throws DAOException {
		if (ArrayUtils.isEmpty(classItemVOs)) {
			return new DataVO();
		}
		StringBuffer sumSql = new StringBuffer();

		for (int i = 0; i < classItemVOs.length; i++) {
			if (classItemVOs[i].getIitemtype() == 0) {
				sumSql.append("sum(wa_data." + classItemVOs[i].getItemkey() + ") as " + classItemVOs[i].getItemkey()
						+ ",");
			}
		}
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select ");
		sqlBuffer.append(sumSql); // 7
		sqlBuffer.append(" '1' as pk_wa_data from wa_data ");
		sqlBuffer.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlBuffer.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlBuffer.append(" left join org_orgs_v on wa_data.WORKORGVID = org_orgs_v.PK_VID ");
		sqlBuffer.append(" LEFT OUTER JOIN org_dept_v ON wa_data.WORKDEPTVID = org_dept_v.PK_VID  ");
		sqlBuffer.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlBuffer.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		sqlBuffer.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl where ");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);
		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");
			sqlBuffer.append(sqlpart);
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition)).append(")");
		}
		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtil.isEmptyWithTrim(powerSql)) {
			sqlBuffer.append(" and " + powerSql);
		}
		DataVO vo = executeQueryAppendableVO(sqlBuffer.toString(), DataVO.class);
		return vo;
	}

	/**
	 * �÷������ڸ���н�ʷ������ڼ䷽������ȡ��ڼ����ڼ�״̬���в�ѯ������TS���ڼӰ汾У�� 2015-11-5
	 * 
	 * @author zhousze
	 * @param pk_wa_class
	 * @param pk_periodscheme
	 * @param cperiod
	 * @param cyear
	 * @return
	 * @throws DAOException
	 */
	public PeriodStateVO queryPeriodStateVOByPk(String pk_wa_class, String pk_periodscheme, String cperiod, String cyear)
			throws DAOException {
		PeriodStateVO periodstateVO = new PeriodStateVO();
		StringBuffer sqlA = new StringBuffer();
		sqlA.append("select pk_periodstate from wa_periodstate where pk_wa_period = "
				+ "(select pk_wa_period from wa_period where pk_periodscheme = '" + pk_periodscheme + "' and cyear = '"
				+ cyear + "' and cperiod  = '" + cperiod + "') " + " and pk_wa_class = '" + pk_wa_class + "' ");
		String pk_periodstate = (String) this.getBaseDao().executeQuery(sqlA.toString(), new ResultSetProcessor() {

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				String result = null;
				if (rs.next()) {
					result = rs.getString(1);
				}
				return result;
			}
		});
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select ts from wa_periodstate where pk_wa_period = "
				+ "(select pk_wa_period from wa_period where pk_periodscheme = '" + pk_periodscheme + "' and cyear = '"
				+ cyear + "' and cperiod  = '" + cperiod + "') " + " and pk_wa_class = '" + pk_wa_class + "' ");
		UFDateTime ts = (UFDateTime) this.getBaseDao().executeQuery(sqlB.toString(),
				new BeanProcessor(UFDateTime.class));
		periodstateVO.setPk_periodstate(pk_periodstate);
		periodstateVO.setTs(ts);
		return periodstateVO;
	}

	public AggPayDataVO queryAggPayDataVOs(WaLoginContext loginContext, String condition, String orderCondtion)
			throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();

		// н�������Ϣ
		WaLoginVO waLoginVO = WaClassStateHelper.getWaclassVOWithState(loginContext.getWaLoginVO());
		aggPayDataVO.setLoginVO(waLoginVO);

		// ��Ȩ�޵�н����Ŀ
		WaClassItemVO[] classItemVOs = getUserClassItemVOs(loginContext);
		aggPayDataVO.setClassItemVOs(classItemVOs);

		// н�ʷ������ݡ� Ӧ�ò���Ҫ��
		DataVO[] dataVOs = queryByCondition(loginContext, condition, orderCondtion);
		aggPayDataVO.setDataVOs(dataVOs);

		// String[] pks = queryPKSByCondition(loginContext, condition,
		// orderCondtion);
		// aggPayDataVO.setDataPKs(pks);

		// DataSVO. ����ϸ��������Ŀ
		DataSVO[] dsvos = getDataSVOs(loginContext);

		aggPayDataVO.setDataSmallVO(dsvos);
		return aggPayDataVO;
	}

	/**
	 * ��ѯ���뵥��������ϸ����
	 * 
	 * @author zhangg on 2009-12-10
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggPayDataVO queryAggPayDataVOForroll(WaLoginContext loginContext) throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();

		// ������н����Ŀ
		WaClassItemVO[] classItemVOs = getApprovedClassItemVOs(loginContext);
		aggPayDataVO.setClassItemVOs(classItemVOs);

		// // н�ʷ�����Ŀ
		// String powerSql =
		// WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(),
		// HICommonValue.RESOUCECODE_6007PSNJOB,
		// IHRWADataResCode.WADEFAULT, "wa_data");

		// 2015-7-27 xiejie3 NCdp205377513
		// �����ϲ������������롿�������뵥ʱ�����뵥����Ա˳���롾н�ʷ��š�����Ա˳�򱣳�һ��begin
		// ���û�������ֶ� �ȵ����ݿ��в�ѯ��û�е�ǰ�û����������� by wangqim
		String orderCondtion = "";
		SortVO sortVOs[] = null;
		SortconVO sortconVOs[] = null;
		String strCondition = " func_code='" + "60130paydata" + "'" + " and group_code= 'TableCode' and ((pk_corp='"
				+ PubEnv.getPk_group() + "' and pk_user='" + PubEnv.getPk_user()
				+ "') or pk_corp ='@@@@') order by pk_corp";

		sortVOs = (SortVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, SortVO.class, strCondition);
		Vector<Attribute> vectSortField = new Vector<Attribute>();
		if (sortVOs != null && sortVOs.length > 0) {
			strCondition = "pk_hr_sort='" + sortVOs[0].getPrimaryKey() + "' order by field_seq ";
			sortconVOs = (SortconVO[]) NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
					.retrieveByClause(null, SortconVO.class, strCondition);
			for (int i = 0; sortconVOs != null && i < sortconVOs.length; i++) {
				Pair<String> field = new Pair<String>(sortconVOs[i].getField_name(), sortconVOs[i].getField_code());
				Attribute attribute = new Attribute(field, sortconVOs[i].getAscend_flag().booleanValue());
				vectSortField.addElement(attribute);
			}
			orderCondtion = getOrderby(vectSortField);
		}
		if (StringUtils.isBlank(orderCondtion)) {
			orderCondtion = " org_dept_v.code , hi_psnjob.clerkcode ";
		}
		// end

		// DataVO[] dataVOs = queryByConditionWithItem(loginContext,
		// null, classItemVOs);
		DataVO[] dataVOs = queryByConditionWithItem(loginContext, orderCondtion, classItemVOs);

		aggPayDataVO.setDataVOs(dataVOs);
		return aggPayDataVO;
	}

	// 2015-7-27 xiejie3 NCdp205377513
	// �����ϲ������������롿�������뵥ʱ�����뵥����Ա˳���롾н�ʷ��š�����Ա˳�򱣳�һ��begin
	public static String getOrderby(Vector<Attribute> vectSortField) {
		if (vectSortField == null || vectSortField.size() == 0) {
			return "";
		}
		String strOrderBy = "";
		String strFullCode = null;
		for (Attribute attr : vectSortField) {
			strFullCode = attr.getAttribute().getValue();
			// if(!(strFullCode.equalsIgnoreCase("bd_psndoc.name")
			// ||strFullCode.equalsIgnoreCase("org_dept_v.name")
			// ||strFullCode.equalsIgnoreCase("queryByConditionWithItem")
			// ||strFullCode.equalsIgnoreCase("bd_psncl.name")
			// ||strFullCode.equalsIgnoreCase("om_post.postname"))){
			// continue;
			// }
			if (strFullCode == null || strFullCode.isEmpty()) {
				continue;
			}
			strOrderBy = strOrderBy + "," + strFullCode + (attr.isAscend() ? "" : " desc");
		}
		return strOrderBy.length() > 0 ? strOrderBy.substring(1) : "";
	}

	// end

	/**
	 * ��ѯ���뵥��������ϸ����
	 * 
	 * @author zhangg on 2009-12-10
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggPayDataVO querySumDataVOForroll(WaLoginContext loginContext) throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();

		// ������н����Ŀ
		WaClassItemVO[] classItemVOs = getApprovedClassItemVOs(loginContext);
		// // н�ʷ�����Ŀ
		// String powerSql = WaPowerSqlHelper.getWaPowerSql(
		// loginContext.getPk_group(),
		// HICommonValue.RESOUCECODE_6007PSNJOB,
		// IHRWADataResCode.WADEFAULT, "wa_data");
		DataVO[] dataVOs = querySumDataByCondition(loginContext, null, classItemVOs);

		aggPayDataVO.setDataVOs(dataVOs);
		return aggPayDataVO;
	}

	// guoqt н�ʷ������뼰�������Ӻϼ���
	public DataVO[] querySumDataByConditionAll(WaLoginContext context, String condition, String orderCondtion,
			WaClassItemVO[] classItemVOs) throws BusinessException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select  COUNT(wa_data.pk_psnjob ) psnnum "); // 1
		if (!ArrayUtils.isEmpty(classItemVOs)) {
			for (WaClassItemVO classItemVO : classItemVOs) {
				// �ж��Ƿ�����ֵ����Ŀ
				if (classItemVO.getIitemtype() != null
						&& classItemVO.getIitemtype().intValue() == TypeEnumVO.FLOATTYPE.value().intValue()) {
					sqlBuffer.append("       ,sum(" + classItemVO.getItemkey() + ") " + classItemVO.getItemkey()); // 7
				}
			}
		}

		sqlBuffer.append("  from wa_data where");

		String sqlpart = getCommonWhereCondtion4Data(context.getWaLoginVO());
		sqlBuffer.append(sqlpart);

		// TODO ���Ʋ�ѯ
		if (!StringUtil.isEmpty(condition)) {
			sqlBuffer.append(" and wa_data.pk_wa_data in (select pk_wa_data from wa_data where ");
			sqlBuffer.append(sqlpart);
			sqlBuffer.append(WherePartUtil.formatAddtionalWhere(condition)).append(")");
		}
		return executeQueryAppendableVOs(sqlBuffer.toString(), DataVO.class);

	}

	/**
	 * ��ѯ���뵥��������ϸ���ݣ��ϼ��У�
	 * 
	 * @author guoqt
	 * @param loginContext
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggPayDataVO querySumDataVOForrollAll(WaLoginContext loginContext) throws BusinessException {
		AggPayDataVO aggPayDataVO = new AggPayDataVO();

		// ������н����Ŀ
		WaClassItemVO[] classItemVOs = getApprovedClassItemVOs(loginContext);
		// н�ʷ�����Ŀ
		String powerSql = WaPowerSqlHelper.getWaPowerSql(loginContext.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB, IHRWADataResCode.WADEFAULT, "wa_data");
		DataVO[] dataVOs = querySumDataByConditionAll(loginContext, powerSql, null, classItemVOs);

		aggPayDataVO.setDataVOs(dataVOs);
		return aggPayDataVO;
	}

	private void updatePeriodState(String[] colKeys, Object[] colValues, WaLoginVO waLoginVO) throws BusinessException {
		updateTableByColKey("wa_periodstate", colKeys, colValues, getCommonWhereCondtion4PeriodState(waLoginVO));
	}

	public void updatePeriodState(String colKey, Object colValue, WaLoginVO waLoginVO) throws BusinessException {
		updatePeriodState(new String[] { colKey }, new Object[] { colValue }, waLoginVO);
	}

	// shenliangc 20140830 �ϲ���˰�����������ֻ��������ϲ�ѯ��������Ա���ݣ���Ҫ�������������
	public void reCaculate(WaLoginContext loginContext, String whereCondition) throws nc.vo.pub.BusinessException {
		// �ϲ���˰���㼰�����������
		TaxBindCaculateService caculateService = new TaxBindCaculateService(loginContext, whereCondition);
		caculateService.doCaculate();
	}

	/**
	 * н�ʷ���
	 * 
	 * @author zhangg on 2009-12-4
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	public void onPay(WaLoginContext loginContext) throws nc.vo.pub.BusinessException {
		WaLoginVO waLoginVO = loginContext.getWaLoginVO();
		// �ϲ���˰���㼰�����������
		// boolean isInTaxgroup =
		// wapubDAO.isInTaxGroup(waLoginVO.getPk_wa_class());
		//
		// if (isInTaxgroup) {
		// TaxBindCaculateService caculateService = new
		// TaxBindCaculateService(loginContext);
		// caculateService.doCaculate();
		// }

		String[] colKeys = new String[] { PeriodStateVO.PAYOFFFLAG, PeriodStateVO.VPAYCOMMENT, PeriodStateVO.CPAYDATE };
		String comment = waLoginVO.getPeriodVO().getVpaycomment();
		SQLParamType nullValue = SQLTypeFactory.getNullType(Types.VARCHAR);
		UFDate paydate = waLoginVO.getPeriodVO().getCpaydate();

		Object[] colValues = new Object[] { UFBoolean.TRUE, comment == null ? nullValue : comment,
				paydate == null ? nullValue : paydate };
		updatePeriodState(colKeys, colValues, waLoginVO);

		// ����ӷ���ȫ�����ţ����¸�����״̬
		if (!isChildPayoff(loginContext)) {
			colKeys = new String[] { PeriodStateVO.PAYOFFFLAG };
			colValues = new Object[] { UFBoolean.TRUE };
			String cond = getPeriodstateCond(waLoginVO.getPk_prnt_class(), waLoginVO.getCyear(), waLoginVO.getCperiod());
			updateTableByColKey("wa_periodstate", colKeys, colValues, cond);
		}
		colKeys = new String[] { PeriodStateVO.VPAYCOMMENT, PeriodStateVO.CPAYDATE };
		colValues = new Object[] { comment == null ? nullValue : comment, paydate == null ? nullValue : paydate };
		updateTableByColKey("wa_data", colKeys, colValues, WherePartUtil.getCommonWhereCondtion4ChildData(waLoginVO)
				+ " ");
		// guoqt NCdp205075407���·������ڸ�����ԭ������ԭ���Ƿ��з������ڼ�����ԭ��
		// + " and wa_data.cpaydate is null and wa_data.vpaycomment is null");
		// ���µ���״̬Ϊִ��̬
		String updatePayrollSql = "update wa_payroll set billstate = '" + HRConstEnum.EXECUTED
				+ "' where billstate = '" + IPfRetCheckInfo.PASSING + "' and pk_wa_class = '"
				+ waLoginVO.getPk_wa_class() + "'";
		getBaseDao().executeUpdate(updatePayrollSql);
	}

	public boolean isChildPayoff(WaLoginContext loginContext) throws DAOException {
		String sql = "SELECT wa_inludeclass.pk_childclass " + "FROM wa_inludeclass,wa_periodstate,wa_period "
				+ "WHERE wa_inludeclass.pk_childclass = wa_periodstate.pk_wa_class "
				+ "	AND wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
				+ "	AND wa_inludeclass.cyear = wa_period.cyear " + "	AND wa_inludeclass.cperiod = wa_period.cperiod "
				+ "	AND wa_inludeclass.pk_parentclass = ? " + "	AND wa_inludeclass.cyear = ? "
				+ "	AND wa_inludeclass.cperiod = ? " + "	AND wa_periodstate.payoffflag = 'N' ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(loginContext.getPk_prnt_class());
		parameter.addParam(loginContext.getWaYear());
		parameter.addParam(loginContext.getWaPeriod());
		return isValueExist(sql, parameter);
	}

	private String getPeriodstateCond(String pk_wa_class, String cyear, String cperiod) {
		String cond = " pk_wa_class = '" + pk_wa_class + "' and exists "
				+ "(select wa_period.pk_wa_period  from wa_period  "
				+ "where wa_period.pk_wa_period = wa_periodstate.pk_wa_period" + " and wa_period.cyear =  '" + cyear
				+ "' and wa_period.cperiod =  '" + cperiod + "' and  " + "wa_periodstate.pk_wa_class =  '"
				+ pk_wa_class + "')";
		return cond;
	}

	public void update(Object object, WaLoginVO waLoginVO) throws BusinessException {
		if (!object.getClass().isArray()) {
			DataVO datavo = (DataVO) object;
			singleUpdate(waLoginVO.getPk_wa_class(), datavo);

		} else {
			Object[] objs = (Object[]) object;
			List<Object> list = Arrays.asList(objs);
			DataVO[] dataVos = list.toArray(new DataVO[objs.length]);
			batchUpdate(waLoginVO.getPk_wa_class(), dataVos);
			// for (Object vo : objs) {
			// DataVO datavo = (DataVO) vo;
			// singleUpdate(datavo);
			// }
		}
		// �����ڼ����״̬
		updatePeriodState("caculateflag", UFBoolean.FALSE, waLoginVO);
	}

	/**
	 * �������� <br>
	 * ע�⣺���е�datavo �����Ա���ͳһ�� Created on 2012-10-8 15:32:57<br>
	 * 
	 * @param objs
	 * @throws BusinessException
	 * @author daicy
	 */
	private void batchUpdate(String pk_wa_class, DataVO... objs) throws BusinessException {
		// ����
		BDPKLockUtil.lockSuperVO(objs);
		// �汾У�飨ʱ���У�飩
		BDVersionValidationUtil.validateSuperVO(objs);
		InSQLCreator inSQLCreator = new InSQLCreator();
		try {
			String sql = "select wa_data.*,bd_psndoc.name psnname from wa_data inner join bd_psndoc on wa_data.PK_PSNDOC = bd_psndoc.pk_psndoc where "
					+ DataVO.PK_WA_DATA + " in (" + inSQLCreator.getInSQL(objs, DataVO.PK_WA_DATA) + ")";
			// BmDataVO[] dbdatavos = retrieveByClause(BmDataVO.class,
			// condition);
			DataVO[] dbdatavos = executeQueryVOs(sql, DataVO.class);

			if (dbdatavos == null || dbdatavos.length == 0) {
				return;
			}

			// ������ݲ���Objs��ȷ�� ��Ҫ���µ�attribute�����ܸ���dbdatavos ��ȷ����
			// ע�⣺���е�datavo �����Ա���ͳһ��
			List<String> needUpdateNamesList = getNeedUpdateNamesList(objs[0]);
			List<DataVO> list_update = new ArrayList<DataVO>();
			for (int i = 0; i < objs.length; i++) {

				DataVO temp_datavo = objs[i];
				temp_datavo.setCaculateflag(UFBoolean.FALSE);
				// ��δ��˵ĲŸ���
				if (!temp_datavo.getCheckflag().booleanValue()) {
					list_update.add(temp_datavo);
				}
			}
			JdbcPersistenceManager.clearColumnTypes(DataVO.getDefaultTableName());
			DataVO[] updateVOs = list_update.toArray(new DataVO[list_update.size()]);
			getBaseDao().updateVOArray(updateVOs, needUpdateNamesList.toArray(new String[needUpdateNamesList.size()]));

			if (list_update.size() > 0) {
				// for (int i = 0; i < list_update.size(); i++){
				WaBusilogUtil.writeEditLog(pk_wa_class, updateVOs);
				// }
			}
		} finally {
			inSQLCreator.clear();
		}
	}

	private List<String> getNeedUpdateNamesList(DataVO datavo) {

		String[] attributeNames = datavo.getAttributeNames();
		List<String> needUpdateNamesList = new LinkedList<String>();
		for (String attributeName : attributeNames) {
			if (DataVOUtils.isAppendAttribute(attributeName)) {
				needUpdateNamesList.add(attributeName);
			}
		}
		// NCdp205555043 н�ʷ����������б������޸ĺ󣬼����־û��ȥ��
		needUpdateNamesList.add(DataVO.CACULATEFLAG);
		needUpdateNamesList.add(DataVO.CPAYDATE);
		needUpdateNamesList.add(DataVO.VPAYCOMMENT);

		return needUpdateNamesList;

	}

	private DataVO singleUpdate(String pk_wa_class, DataVO datavo) throws BusinessException {
		// ����
		BDPKLockUtil.lockSuperVO(datavo);

		// �汾У�飨ʱ���У�飩
		BDVersionValidationUtil.validateSuperVO(datavo);

		DataVO dbdatavo = retrieveByPK(DataVO.class, datavo.getPk_wa_data());
		if (dbdatavo == null) {
			throw new BusinessException(datavo.getAttributeValue("psnname")
					+ ResHelper.getString("60130paydata", "060130paydata0449")/*
																			 * @res
																			 * "�ڸ÷�����н�ʵ������Ѿ���ɾ��!"
																			 */);
		}
		if (dbdatavo.getCheckflag().booleanValue()) {
			throw new BusinessException(datavo.getAttributeValue("psnname")
					+ ResHelper.getString("60130paydata", "060130paydata0450")/*
																			 * @res
																			 * "�ڸ÷������������Ѿ������!"
																			 */);
		}

		datavo.setCaculateflag(UFBoolean.FALSE);
		String[] attributeNames = datavo.getAttributeNames();
		List<String> needUpdateNamesList = new LinkedList<String>();
		for (String attributeName : attributeNames) {
			if (DataVOUtils.isAppendAttribute(attributeName)) {
				needUpdateNamesList.add(attributeName);
			}
		}
		needUpdateNamesList.add(DataVO.CACULATEFLAG);
		needUpdateNamesList.add(DataVO.CPAYDATE);
		needUpdateNamesList.add(DataVO.VPAYCOMMENT);

		// ��¼��־��
		JdbcPersistenceManager.clearColumnTypes(DataVO.getDefaultTableName());
		getBaseDao().updateVO(datavo, needUpdateNamesList.toArray(new String[0]));

		WaBusilogUtil.writeEditLog(pk_wa_class, new DataVO[] { datavo });

		return datavo;
	}

	/**
	 * н��ȡ������
	 * 
	 * @author zhangg on 2009-12-4
	 * @param waLoginVO
	 * @throws nc.vo.pub.BusinessException
	 */
	public void onUnPay(WaLoginVO waLoginVO) throws nc.vo.pub.BusinessException {

		String checkSql = "SELECT batch " + "FROM wa_inludeclass "
				+ "WHERE pk_parentclass=( select distinct pk_parentclass from wa_inludeclass where pk_childclass =  '"
				+ waLoginVO.getPk_wa_class() + "') and cyear = '" + waLoginVO.getCyear() + "' and cperiod = '"
				+ waLoginVO.getCperiod() + "' and batch > " + waLoginVO.getBatch()
				+ " and batch<100 and pk_childclass <>'" + waLoginVO.getPk_wa_class() + "'";
		if (isValueExist(checkSql)) {
			throw new nc.vo.pub.BusinessException(ResHelper.getString("60130paydata", "060130paydata0451")/*
																										 * @
																										 * res
																										 * "�Ѿ��������µ��ӷ������÷�������ȡ�����ţ�"
																										 */);
		}

		checkSql = "SELECT 1 " + "FROM wa_data " + "WHERE fipendflag = 'Y' " + "and pk_wa_class = ? "
				+ "and cyear = ? " + "and cperiod = ?";

		if (isValueExist(checkSql, getCommonParameter(waLoginVO))) {
			throw new nc.vo.pub.BusinessException(ResHelper.getString("60130paydata", "060130paydata0534")/*
																										 * @
																										 * res
																										 * "�÷����Ѵ��ڲ���������ݣ�����ȡ�����ţ�"
																										 */);
		}
		// �÷����Ƿ��Ƶ�
		// if(waLoginVO.isMultiClass()){
		boolean isbill = NCLocator.getInstance().lookup(IAmoSchemeQuery.class).isApportion(waLoginVO);
		if (isbill == true) {
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0452")/*
																								 * @
																								 * res
																								 * "��н�ʷ����Ѿ��Ƶ�������ȡ�����ţ�"
																								 */);// "�ù�������Ѿ��Ƶ�������ȡ�����ţ�"
		}
		// }
		// �ϲ���˰���㼰�����������
		// ͬһ������ڼ��·����Ƿ�����, Ӧ��ֻ�кϲ���˰��������
		// String caccyear = waLoginVO.getPeriodVO().getCaccyear();
		// String caccperiod = waLoginVO.getPeriodVO().getCaccperiod();
		// String sqlWhereAccPeriod = " cpreclassid = '" +
		// waLoginVO.getPk_wa_class()
		// + "' and checkflag = 'Y'";
		// sqlWhereAccPeriod += " and caccyear='" + caccyear +
		// "' and caccperiod='" + caccperiod + "'";
		// sqlBuffer = new StringBuffer(WapubDAO.getPeriodViewTable());
		// sqlBuffer.append(WherePartUtil.addWhereKeyWord2Condition(sqlWhereAccPeriod));
		// if (isValueExist(sqlBuffer.toString())) {
		// throw new
		// nc.vo.pub.BusinessException(ResHelper.getString("60130paydata","060130paydata0453")/*@res
		// "��н�ʷ������ź����б�ķ������ø÷��������ݽ����˼��㣬����ˣ��÷�������ȡ�����ţ�"*/);//
		// "�ù�����𷢷ź����б��������ø��������ݽ����˼��㣬����ˣ��������ȡ����ˣ�"
		// }

		// ����״̬����wa_periodstate��isapproved�ֶ�ֵ
		// updatePeriodState("isapproved", UFBoolean.FALSE, waLoginVO);
		// ɾ����������

		// ���·��ű�־
		// String[] colKeys = new String[] { "payoffflag", "vpaycomment",
		// "cpaydate", "cpreclassid" };//�ϲ���˰���㼰�����������
		String[] colKeys = new String[] { "payoffflag", "vpaycomment", "cpaydate" };
		SQLParamType value = SQLTypeFactory.getNullType(Types.VARCHAR);
		// Object[] colValues = new Object[] { UFBoolean.FALSE, value, value,
		// value};//�ϲ���˰���㼰�����������
		Object[] colValues = new Object[] { UFBoolean.FALSE, value, value };
		updatePeriodState(colKeys, colValues, waLoginVO);
		// //ȡ������ʱ���������ںͷ���˵���ÿ�
		// SQLParamType nullValue = SQLTypeFactory.getNullType(Types.VARCHAR);
		// colKeys = new String[] {
		// PeriodStateVO.VPAYCOMMENT,PeriodStateVO.CPAYDATE };
		// colValues = new Object[] {nullValue ,nullValue};
		// updateTableByColKey("wa_data", colKeys,
		// colValues,WherePartUtil.getCommonWhereCondtion4ChildData(waLoginVO));
		// // ����״̬ //�ϲ���˰���㼰�����������
		// //sqlWhereAccPeriod = " cpreclassid like '%" +
		// waLoginVO.getPk_wa_class() + "%'";
		// String sqlWhereAccPeriod = " cpreclassid = '" +
		// waLoginVO.getPk_wa_class() + "'";
		// sqlWhereAccPeriod +=
		// " and pk_wa_period in(select pk_wa_period from wa_period where caccyear='"
		// + caccyear + "' and caccperiod='" + caccperiod + "')";
		// updateTableByColKey("wa_periodstate", "caculateflag",
		// UFBoolean.FALSE, WherePartUtil
		// .addWhereKeyWord2Condition(sqlWhereAccPeriod));

		// boolean isInTaxgroup =
		// wapubDAO.isInTaxGroup(waLoginVO.getPk_wa_class());
		//
		// if (isInTaxgroup) {
		// // FIXME ����
		// // caculateFormuItem_afterTax_zero(aRecaVO);
		// }

		// ����Ƕ�η��ţ����¸�����״̬
		// if(waLoginVO.isMultiClass()){
		colKeys = new String[] { PeriodStateVO.PAYOFFFLAG };
		colValues = new Object[] { UFBoolean.FALSE };
		String cond = getPeriodstateCond(waLoginVO.getPk_prnt_class(), waLoginVO.getCyear(), waLoginVO.getCperiod());
		updateTableByColKey("wa_periodstate", colKeys, colValues, cond);
		// }
		// ���µ���״̬Ϊִ��̬
		String updatePayrollSql = "update wa_payroll set billstate = '" + IPfRetCheckInfo.PASSING
				+ "' where billstate = '" + HRConstEnum.EXECUTED + "' and pk_wa_class = '" + waLoginVO.getPk_wa_class()
				+ "' and cyear = '" + waLoginVO.getCyear() + "' and cperiod = '" + waLoginVO.getCperiod() + "' ";
		getBaseDao().executeUpdate(updatePayrollSql);

	}

	/**
	 * �滻
	 * 
	 * @author zhangg on 2009-12-8
	 * @param waLoginVO
	 * @param whereCondition
	 * @param replaceItem
	 * @param formula
	 * @throws BusinessException
	 */

	public void onReplace(WaLoginVO waLoginVO, String whereCondition, WaClassItemVO replaceItem, String formula)
			throws BusinessException {

		String sql = "update wa_data set ";

		if (TypeEnumVO.FLOATTYPE.value().equals(replaceItem.getIitemtype())) {// ������
			sql += replaceItem.getItemkey()
					+ "= "
					+ WaCommonImpl.getRoundSql(getBaseDao().getDBType(), formula, replaceItem.getIflddecimal(),
							replaceItem.getRound_type());
		} else {// �ַ���
			sql += replaceItem.getItemkey() + "=" + formula + ", ";
		}

		sql += "caculateflag= 'N'  ";
		sql += " where checkflag='N' and pk_wa_class = ?  and  cyear = ?  and cperiod = ? ";
		sql += " and dr=0  and stopflag = 'N' ";

		sql += WherePartUtil.formatAddtionalWhere(whereCondition);

		getBaseDao().executeUpdate(sql, getCommonParameter(waLoginVO));

		// дҵ����־
		WaBusilogUtil.writePaydataReplaceBusiLog(waLoginVO, replaceItem, formula);

		// �������״̬
		updatePeriodState("caculateflag", UFBoolean.FALSE, waLoginVO);
	}

	/**
	 * ���а汾���ơ� ʹ�����н��У��Ӷ���ȥ����Ҫ�ļ��
	 * 
	 * @param waLoginVO
	 * @param dataSVOs
	 * @throws BusinessException
	 */
	public void onSaveDataSVOs(WaLoginVO waLoginVO, DataSVO[] dataSVOs) throws BusinessException {
		if (dataSVOs == null) {
			return;
		}
		DataVO[] dataVOs = new DataVO[dataSVOs.length];
		for (int i = 0; i < dataVOs.length; i++) {
			dataVOs[i] = new DataVO();
			dataVOs[i].setPk_wa_data(dataSVOs[i].getPk_wa_data());
			dataVOs[i].setCaculateflag(UFBoolean.FALSE);
		}
		// ����
		BDPKLockUtil.lockSuperVO(dataVOs);

		// У��&����
		List<DataSVO> deleteList = new LinkedList<DataSVO>();
		List<DataSVO> updateList = new LinkedList<DataSVO>();
		List<DataSVO> insertList = new LinkedList<DataSVO>();

		InSQLCreator isc = new InSQLCreator();
		String insql = isc.getInSQL(dataVOs, DataVO.PK_WA_DATA);
		DataVO[] newdataVOs = retrieveByClause(DataVO.class, " pk_wa_data in (" + insql + ")");
		if (newdataVOs == null) {
			return;
		}
		HashMap<String, DataVO> map = new HashMap<String, DataVO>();
		for (DataVO dvo : newdataVOs) {
			map.put(dvo.getPk_wa_data(), dvo);
		}
		for (DataSVO vo : dataSVOs) {
			if (map.get(vo.getPk_wa_data()) == null) {
				String psnname = ""; // zhoumxc �޸Ĵ�����ʾ����ȡ��������б��е���Ա���� 2014.08.09
				if (map.get(vo.getPk_wa_data()) != null) {
					psnname = vo.getPsnname();
				}
				throw new BusinessException(psnname + ResHelper.getString("60130paydata", "060130paydata0449")/*
																											 * @
																											 * res
																											 * "�ڸ÷�����н�ʵ������Ѿ���ɾ��!"
																											 */);
			}
			if (map.get(vo.getPk_wa_data()).getCheckflag().booleanValue()) {
				String psnname = ""; // zhoumxc �޸Ĵ�����ʾ����ȡ��������б��е���Ա���� 2014.08.09
				if (map.get(vo.getPk_wa_data()) != null) {
					psnname = vo.getPsnname();
				}
				throw new BusinessException(psnname + ResHelper.getString("60130paydata", "060130paydata0450")/*
																											 * @
																											 * res
																											 * "�ڸ÷������������Ѿ������!"
																											 */);
			}
			if (vo.getStatus() == VOStatus.NEW) {
				insertList.add(vo);
			} else if (vo.getStatus() == VOStatus.DELETED) {
				deleteList.add(vo);
			} else if (vo.getStatus() == VOStatus.UPDATED) {
				updateList.add(vo);
			}
		}

		// ��ɾ��
		getBaseDao().deleteVOArray(deleteList.toArray(new DataSVO[0]));
		// ���޸�
		StringBuffer sqlB = new StringBuffer();

		// �����Ա��Ŀ�Ƿ��ظ�
		DataSVO[] updatesvo = updateList.toArray(new DataSVO[updateList.size()]);
		String[] field = HRWACommonConstants.DATASCOLUMN;
		String tableName = isc.insertValues(HRWACommonConstants.WA_TEMP_DATAS, field, field, updatesvo);
		if (tableName != null) {
			sqlB.append("select wa_datas.pk_wa_datas, ");
			sqlB.append(SQLHelper.getMultiLangNameColumn("bd_psndoc.name"));
			sqlB.append(" psnname ,");
			sqlB.append(SQLHelper.getMultiLangNameColumn("wa_classitem.name"));
			sqlB.append(" itemname");
			sqlB.append(" from wa_datas inner join wa_data on wa_datas.pk_wa_data = wa_data.pk_wa_data");
			sqlB.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
			sqlB.append(" inner join wa_classitem on wa_classitem.pk_wa_classitem = wa_datas.pk_wa_classitem ");
			sqlB.append(" inner join ");
			sqlB.append(tableName);
			sqlB.append(" on (wa_datas.pk_wa_data = ");
			sqlB.append(tableName);
			sqlB.append(".pk_wa_data ");
			sqlB.append(" and wa_datas.pk_wa_classitem = ");
			sqlB.append(tableName);
			sqlB.append(".pk_wa_classitem ");
			sqlB.append(" and wa_datas.pk_wa_datas != ");
			sqlB.append(tableName);
			sqlB.append(".pk_wa_datas )");

			DataSVO[] resultvos = executeQueryVOs(sqlB.toString(), DataSVO.class);
			if (resultvos != null && resultvos.length > 0) {
				StringBuffer eMsg = new StringBuffer();
				for (DataSVO resultvo : resultvos) {
					eMsg.append(ResHelper.getString("60130paydata", "060130paydata0454", resultvo.getPsnname(),
							resultvo.getItemname())
					/* @res "��Ա����Ϊ{0}��{1}������Ŀ�Ѿ����ڣ�" */);
					eMsg.append("/r/n");
				}
				throw new BusinessException(eMsg.toString());
			}
			getBaseDao().updateVOArray(updatesvo);
		}

		// ������

		// �����Ա��Ŀ�Ƿ��ظ�
		DataSVO[] insertsvo = insertList.toArray(new DataSVO[insertList.size()]);
		String[] field2 = HRWACommonConstants.DATAS2COLUMN;
		String tableName2 = isc.insertValues(HRWACommonConstants.WA_TEMP_DATAS2, field2, field2, insertsvo);
		if (tableName2 != null) {
			sqlB = new StringBuffer();
			sqlB.append("select ");
			sqlB.append(SQLHelper.getMultiLangNameColumn("bd_psndoc.name"));
			sqlB.append(" psnname ,");
			sqlB.append(SQLHelper.getMultiLangNameColumn("wa_classitem.name"));
			sqlB.append(" itemname");
			sqlB.append(" from wa_datas inner join wa_data on wa_datas.pk_wa_data = wa_data.pk_wa_data");
			sqlB.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
			sqlB.append(" inner join wa_classitem on wa_classitem.pk_wa_classitem = wa_datas.pk_wa_classitem ");
			sqlB.append(" inner join ");
			sqlB.append(tableName2);
			sqlB.append(" on (wa_datas.pk_wa_data = ");
			sqlB.append(tableName2);
			sqlB.append(".pk_wa_data ");
			sqlB.append(" and wa_datas.pk_wa_classitem = ");
			sqlB.append(tableName2);
			sqlB.append(".pk_wa_classitem )");

			DataSVO[] iresultvos = executeQueryVOs(sqlB.toString(), DataSVO.class);
			if (iresultvos != null && iresultvos.length > 0) {
				StringBuffer eMsg = new StringBuffer();
				for (DataSVO iresultvo : iresultvos) {
					eMsg.append(ResHelper.getString("60130paydata", "060130paydata0454", iresultvo.getPsnname(),
							iresultvo.getItemname())
					/* @res "��Ա����Ϊ{0}��{1}������Ŀ�Ѿ����ڣ�" */);
				}
				throw new BusinessException(eMsg.toString());
			}
			// al = new ArrayList<DataSVO>();
			// // �����Ա��Ŀ�Ƿ��ظ�
			// for (DataSVO dataSVO : insertList) {
			// DataSVO condtionVO = new DataSVO();
			// condtionVO.setPk_wa_data(dataSVO.getPk_wa_data());
			// condtionVO.setPk_wa_classitem(dataSVO.getPk_wa_classitem());
			// if (isVoExist(condtionVO)) {
			// throw new BusinessException(ResHelper.getString("60130paydata",
			// "060130paydata0454", dataSVO.getPsnname(), dataSVO
			// .getItemname())
			// /* @res "��Ա����Ϊ{0}��{1}������Ŀ�Ѿ����ڣ�" */);
			// }
			// al.add(dataSVO);
			// }
			getBaseDao().insertVOArray(insertsvo);
		}

		// ����н�ʼ���״̬
		getBaseDao().updateVOArray(dataVOs, new String[] { DataVO.CACULATEFLAG });
	}

	/**
	 * pk_wa_class year, period
	 * 
	 * @author zhangg on 2009-12-2
	 * @param waLoginVO
	 * @return
	 */
	public static SQLParameter getCommonParameter(WaLoginVO waLoginVO) {
		return WherePartUtil.getCommonParameter(waLoginVO);
	}

	public static SQLParameter getCommonParameterTwice(WaLoginVO waLoginVO) {
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());
		return parameter;
	}

	/**
	 * 
	 * @author zhangg on 2009-12-4
	 * @param waLoginVO
	 * @return
	 */
	public static String getCommonWhereCondtion4Data(WaLoginVO waLoginVO) {
		return WherePartUtil.getCommonWhereCondtion4Data(waLoginVO);
	}

	/**
	 * 
	 * @author zhangg on 2009-12-4
	 * @param waLoginVO
	 * @return
	 */
	public static String getCommonWhereCondtion4PeriodState(WaLoginVO waLoginVO) {
		return WherePartUtil.getCommonWhereCondtion4PeriodState(waLoginVO);
	}

	/**
	 * �ж�״̬�Ƿ��� �仯
	 * 
	 * @author zhangg on 2009-8-12
	 * @param waLoginVO
	 * @param whereCondition
	 * @return
	 * @throws BusinessException
	 */
	public void checkWaClassStateChange(WaLoginVO waLoginVO, String whereCondition) throws BusinessException {
		WaState oldStates = waLoginVO.getState();
		WaState newStates = WaClassStateHelper.getWaclassVOWithState(waLoginVO).getState();

		if (!(oldStates == newStates)) {
			throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0457")/*
																								 * @
																								 * res
																								 * "ѡ���н�ʷ���״̬�����仯, ��ˢ�º����ԣ�"
																								 */);// ѡ���н�����״̬�����仯,
			// ��ˢ�º����ԣ�
		}
	}

	/**
	 * �õ�״̬�仯�˵�VO
	 * 
	 * @author zhangg on 2009-8-13
	 * @param waLoginVO
	 * @param wa_data_where
	 * @return
	 * @throws BusinessException
	 */
	public WaLoginVO getNewWaclassVOWithState(WaLoginVO waLoginVO) throws BusinessException {
		return WaClassStateHelper.getWaclassVOWithState(waLoginVO);
	}

	/**
	 * �Ա���ϸ
	 * 
	 * @author zhangg on 2010-1-4
	 * @param context
	 * @param whereCondition
	 * @return
	 * @throws BusinessException
	 */
	public DataVO[] getContractDataVOs(WaLoginContext context, String whereCondition, String orderCondition)
			throws BusinessException {
		String pk_wa_class = context.getPk_wa_class();
		WaLoginVO waLoginVO = context.getWaLoginVO();

		String waYear = waLoginVO.getPeriodVO().getCyear();
		String waPeriod = waLoginVO.getPeriodVO().getCperiod();
		UFBoolean checkFlag = waLoginVO.getPeriodVO().getCheckflag();
		if (!pk_wa_class.equals(waLoginVO.getPk_prnt_class())) {
			pk_wa_class = waLoginVO.getPk_prnt_class();
			if (checkFlag != null && !checkFlag.booleanValue()) {
				throw new BusinessException(ResHelper.getString("60130paydata", "060130paydata0548")/*
																									 * @
																									 * res
																									 * "���ڱ��η�������δ��ˣ��������ܺ��ٽ�����ϸ�Աȣ�"
																									 */);
			}
		}

		// �˴�����ֱ�Ӵ�waloginvo�������ǰ�ڼ��ѽ��ʣ�����waloginvo���������ڼ䣬�����ǵ�ǰ�ڼ�
		WaClassVO classvo = new WaClassVO();
		classvo.setPk_wa_class(pk_wa_class);
		classvo.setCyear(waYear);
		classvo.setCperiod(waPeriod);
		PeriodStateVO periodStateVO = new MonthEndDAO().getSubclassPrePeriodVO(classvo);

		if (periodStateVO == null) {
			return null;
		}
		String preYear = periodStateVO.getCyear();
		String prePeriod = periodStateVO.getCperiod();

		WaClassItemVO[] classItemVOs = getUserShowClassItemVOs(context);
		// �����������ڼ䲻ͬ�Ĳ���
		StringBuffer tempsql = new StringBuffer();
		tempsql.append(" select a.pk_psndoc from wa_data a, wa_data b where a.pk_wa_class = b.pk_wa_class and a.pk_psndoc = b.pk_psndoc ");
		if (classItemVOs != null && classItemVOs.length > 0) {
			tempsql.append(" and ( ");
			for (int i = 0; i < classItemVOs.length - 1; i++) {
				tempsql.append(" a.");
				tempsql.append(classItemVOs[i].getItemkey());
				tempsql.append(" <> b.");
				tempsql.append(classItemVOs[i].getItemkey()).append(" or ");
			}
			tempsql.append(" a.");
			tempsql.append(classItemVOs[classItemVOs.length - 1].getItemkey());
			tempsql.append(" <> b.");
			tempsql.append(classItemVOs[classItemVOs.length - 1].getItemkey());
			tempsql.append(" ) ");
		}

		tempsql.append(" and a.pk_wa_class = '");
		tempsql.append(pk_wa_class);
		tempsql.append("' and a.cyear = '");
		tempsql.append(waYear);
		tempsql.append("' and a.cperiod = '");
		tempsql.append(waPeriod);
		tempsql.append("' and b.pk_wa_class = '");
		tempsql.append(pk_wa_class);
		tempsql.append("' and b.cyear = '");
		tempsql.append(preYear);
		tempsql.append("' and b.cperiod = '");
		tempsql.append(prePeriod);
		tempsql.append("'  ");

		// ���ӻ���ٵĲ���
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_data.pk_psndoc "); // 1
		sqlBuffer.append("  from wa_data ");
		sqlBuffer.append(" where wa_data.pk_psndoc in ");
		sqlBuffer.append("       (select wa_data.pk_psndoc ");
		sqlBuffer.append("          from wa_data ");
		sqlBuffer.append("         where ((wa_data.pk_wa_class = '" + pk_wa_class + "' and wa_data.cyear = '" + waYear
				+ "' and wa_data.cperiod = '" + waPeriod + "') or (wa_data.pk_wa_class = '" + pk_wa_class
				+ "' and wa_data.cyear = '" + preYear + "' and wa_data.cperiod = '" + prePeriod + "')) ");
		sqlBuffer.append("   ) ");
		sqlBuffer.append("   and wa_data.pk_psndoc not in ");
		sqlBuffer.append("       (select a.pk_psndoc ");
		sqlBuffer.append("          from wa_data a, wa_data b ");
		sqlBuffer.append("         where a.pk_wa_class = b.pk_wa_class ");
		sqlBuffer.append("           and a.pk_psndoc = b.pk_psndoc ");
		sqlBuffer.append("           and a.pk_wa_class = '" + pk_wa_class + "' ");
		sqlBuffer.append("           and a.cyear = '" + waYear + "' ");
		sqlBuffer.append("           and a.cperiod = '" + waPeriod + "' ");
		sqlBuffer.append("           and b.pk_wa_class = '" + pk_wa_class + "' ");
		sqlBuffer.append("           and b.cyear = '" + preYear + "' ");
		sqlBuffer.append("           and b.cperiod = '" + prePeriod + "' ");
		sqlBuffer.append(") ");

		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select  " + SQLHelper.getMultiLangNameColumn("bd_psndoc.name") + "  " + PSNNAME + ", "); // 1
		sqlB.append("       bd_psndoc.code " + PSNCODE + ", "); // 2
		sqlB.append("       hi_psnjob.clerkcode, "); // 3
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("org_dept_v.name") + "  " + DEPTNAME + ", "); // 4
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("bd_psncl.name") + "  " + PLSNAME + ", "); // 5
		// 2015-09-20 xiejie3
		// 2015-07-30 zhosuze NCdp205099799 н�ʷ��Ž������Ӳ�����֯������ begin
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("financeorg.name") + "  " + FINANCEORG + ", "); // 3
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("financedept.name") + "  " + FINANCEDEPT + ", "); // 3
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("liabilityorg.name") + "  " + LIABILITYORG + ", "); // 3
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("liabilitydept.name") + "  " + LIABILITYDEPT + ", "); // 3
		// end

		sqlB.append("       om_job.jobname, "); // 6
		// guoqt��λ����
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("om_post.postname") + "  " + POSTNAME + ", "); // 6
		sqlB.append("        " + SQLHelper.getMultiLangNameColumn("org_orgs_v.name") + "  orgname, "); // 6
		sqlB.append("       wa_data.* "); // 7
		sqlB.append("  from wa_data ");
		sqlB.append(" inner join bd_psndoc on wa_data.pk_psndoc = bd_psndoc.pk_psndoc ");
		sqlB.append(" inner join hi_psnjob on wa_data.pk_psnjob = hi_psnjob.pk_psnjob ");
		sqlB.append(" inner join org_orgs_v on wa_data.WORKORGVID = org_orgs_v.PK_VID ");
		sqlB.append("  inner join org_dept_v ON wa_data.WORKDEPTVID = org_dept_v.PK_VID  ");
		sqlB.append("  left outer join om_job on hi_psnjob.pk_job = om_job.pk_job ");
		sqlB.append("  left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ");
		// 2015-09-20 xiejie3
		// 2015-07-30 zhosuze NCdp205099799 н�ʷ��Ž������Ӳ�����֯������ begin
		sqlB.append("  left join org_orgs financeorg on wa_data.pk_financeorg = financeorg.pk_org ");
		sqlB.append("  left join org_dept financedept on wa_data.pk_financedept = financedept.pk_dept ");
		sqlB.append("  left join org_orgs liabilityorg on wa_data.pk_liabilityorg = liabilityorg.pk_org ");
		sqlB.append("  left join org_dept liabilitydept on wa_data.pk_liabilitydept = liabilitydept.pk_dept ");
		// end

		sqlB.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl ");

		String condtion = " where wa_data.pk_wa_class = '" + pk_wa_class + "'  ";
		condtion += " and ((wa_data.cyear = '" + waYear + "' and wa_data.cperiod = '" + waPeriod
				+ "') or (wa_data.cyear = '" + preYear + "' and wa_data.cperiod = '" + prePeriod + "')) ";

		// 20140728 shenliangc ���ڼ�ͣ����Ա����ϸ�Ա��в��۷���������û�б仯����ֻ��ʾ��һ�ڼ�ķ������ݡ�
		condtion += " and stopflag = 'N' ";

		condtion += " and (wa_data.pk_psndoc in ( " + tempsql.toString() + ")or wa_data.pk_psndoc in ("
				+ sqlBuffer.toString() + ")) ";
		if (whereCondition != null && whereCondition.startsWith("pk_"))
			whereCondition = " hi_psnjob." + whereCondition;
		condtion += WherePartUtil.formatAddtionalWhere(whereCondition);

		sqlB.append(condtion);
		// ����Ȩ��guoqt��ϸ�Ա�
		String powerSql = WaPowerSqlHelper.getWaPowerSql(waLoginVO.getPk_group(), HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "wa_data");
		if (!StringUtils.isBlank(powerSql)) {
			sqlB.append(" and " + powerSql);
		}
		if (StringUtil.isEmpty(orderCondition)) {
			orderCondition = " org_dept_v.code , hi_psnjob.clerkcode";
		}
		sqlB.append(" order by ").append(orderCondition + ",wa_data.cyear,wa_data.cperiod");

		return executeQueryAppendableVOs(sqlB.toString(), DataVO.class);
	}

	/**
	 * �õ���Ҫ���ܵ�н����Ŀ ���� �ڻ�������У� �����ڱ����ܵ�����е���ֵ����Ŀ�ֹ��������Ŀ
	 * 
	 * @author liangxr on 2010-5-26
	 * @param aRecaVO
	 * @return
	 * @throws DAOException
	 */
	public WaItemVO[] getUnitDigitItem(WaLoginVO waLoginVO) throws DAOException {

		// �õ���Ҫ���ܵ�н����Ŀ
		// ���� �ڻ�������У� �����ڱ����ܵ�����е���ֵ����Ŀ�ֹ��������Ŀ
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_item.itemkey, wa_item.iproperty "); // 1
		sqlBuffer.append("  from wa_item ");
		sqlBuffer.append(" where wa_item.pk_wa_item in ");
		sqlBuffer.append("       (select wa_classitem.pk_wa_item ");
		sqlBuffer.append("          from wa_classitem ");
		sqlBuffer.append("         where wa_classitem.pk_wa_class in ");
		sqlBuffer.append("               (select wa_unitctg.classedid from wa_unitctg,wa_waclass ");
		sqlBuffer.append("				where wa_waclass.pk_wa_class = wa_unitctg.classedid ");
		sqlBuffer.append("				and wa_unitctg.pk_wa_class = ? and wa_waclass.stopflag='N') ");
		sqlBuffer.append("           and wa_classitem.cyear = ? ");
		sqlBuffer.append("           and wa_classitem.cperiod = ?) ");
		sqlBuffer.append("   and wa_item.pk_wa_item in ");
		sqlBuffer.append("       (select wa_classitem.pk_wa_item ");
		sqlBuffer.append("          from wa_classitem ");
		sqlBuffer.append("         where wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append("           and wa_classitem.cyear = ? ");
		sqlBuffer.append("           and wa_classitem.cperiod = ?) ");
		sqlBuffer.append("   and wa_item.iitemtype = 0 ");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_prnt_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());
		return executeQueryVOs(sqlBuffer.toString(), parameter, WaItemVO.class);

	}

	public WaItemVO[] getParentClassDigitItem(WaLoginVO waLoginVO) throws DAOException {

		// �õ���Ҫ���ܵ�н����Ŀ
		// ���� �ڻ�������У� �����ڱ����ܵ�����е���ֵ����Ŀ�ֹ��������Ŀ
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select wa_item.itemkey, wa_item.iproperty "); // 1
		sqlBuffer.append("  from wa_item ");
		sqlBuffer.append("   where  wa_item.pk_wa_item in ");
		sqlBuffer.append("       (select wa_classitem.pk_wa_item ");
		sqlBuffer.append("          from wa_classitem ");
		sqlBuffer.append("         where wa_classitem.pk_wa_class = ? ");
		sqlBuffer.append("           and wa_classitem.cyear = ? ");
		sqlBuffer.append("           and wa_classitem.cperiod = ?) ");
		sqlBuffer.append("   and wa_item.iitemtype = 0 ");

		return executeQueryVOs(sqlBuffer.toString(), getCommonParameter(waLoginVO), WaItemVO.class);

	}

	/**
	 * ɾ������������Ա������Ϣ
	 * 
	 * @author liangxr on 2010-5-26
	 * @param waLoginVO
	 * @throws DAOException
	 */
	public void deleteUnitClassPsn(WaLoginVO waLoginVO) throws DAOException {

		SQLParameter parameter = getCommonParameter(waLoginVO);
		String sql = "delete from wa_data where pk_wa_class = ? and cyear= ? and cperiod = ? ";
		// StringBuffer sqlBuffer = new StringBuffer();
		// sqlBuffer
		// .append("delete from wa_data where pk_wa_class = ? and cyear= ? and cperiod = ? and pk_psnjob not in");
		// sqlBuffer.append("          (select wa_data.pk_psnjob ");
		// sqlBuffer.append("             from wa_data ");
		// sqlBuffer.append("            where wa_data.pk_wa_class in ");
		// sqlBuffer.append("               (select wa_unitctg.classedid from wa_unitctg,wa_waclass ");
		// sqlBuffer.append("				where wa_waclass.pk_wa_class = wa_unitctg.classedid ");
		// sqlBuffer.append("				and wa_unitctg.pk_wa_class = ? and wa_waclass.stopflag='N') ");
		// sqlBuffer.append("              and wa_data.cyear = ? ");
		// sqlBuffer.append("              and wa_data.cperiod = ? ");
		// sqlBuffer.append("              and wa_data.stopflag = 'N' )");

		getBaseDao().executeUpdate(sql, parameter);

	}

	/**
	 * ���»���ʱ������ر�
	 * 
	 * @author liangxr on 2010-7-2
	 * @param waLoginVO
	 * @throws DAOException
	 */
	public void deleteUnitRelation(WaLoginVO waLoginVO) throws DAOException {
		SQLParameter parameter = getCommonParameterTwice(waLoginVO);

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer
				.append("delete from wa_redata where pk_wa_class = ? and cyear= ? and cperiod = ? and pk_psnjob not in");
		sqlBuffer.append("          (select wa_data.pk_psnjob ");
		sqlBuffer.append("             from wa_data ");
		sqlBuffer.append("            where wa_data.pk_wa_class  = ? ");
		sqlBuffer.append("              and wa_data.cyear = ? ");
		sqlBuffer.append("              and wa_data.cperiod = ? )");
		getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);
		// sqlBuffer = new StringBuffer();
		// sqlBuffer
		// .append("delete from wa_dataz where pk_wa_class = ? and cyear= ? and cperiod = ? and pk_psnjob not in");
		// sqlBuffer.append("          (select wa_data.pk_psnjob ");
		// sqlBuffer.append("             from wa_data ");
		// sqlBuffer.append("            where wa_data.pk_wa_class  = ? ");
		// sqlBuffer.append("              and wa_data.cyear = ? ");
		// sqlBuffer.append("              and wa_data.cperiod = ? )");
		// getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);
		//
		// sqlBuffer = new StringBuffer();
		// sqlBuffer
		// .append("delete from wa_dataf where pk_wa_class = ? and cyear= ? and cperiod = ? and pk_psnjob not in");
		// sqlBuffer.append("          (select wa_data.pk_psnjob ");
		// sqlBuffer.append("             from wa_data ");
		// sqlBuffer.append("            where wa_data.pk_wa_class  = ? ");
		// sqlBuffer.append("              and wa_data.cyear = ? ");
		// sqlBuffer.append("              and wa_data.cperiod = ? )");
		// getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);

		// sqlBuffer = new StringBuffer();
		// sqlBuffer
		// .append("delete from wa_tax where pk_wa_class = ? and vcalyear= ? and vcalmonth = ?  and pk_psnjob not in");
		// sqlBuffer.append("          (select wa_data.pk_psnjob ");
		// sqlBuffer.append("             from wa_data ");
		// sqlBuffer.append("            where wa_data.pk_wa_class  = ? ");
		// sqlBuffer.append("              and wa_data.cyear = ? ");
		// sqlBuffer.append("              and wa_data.cperiod = ? )");
		// getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);

	}

	/**
	 * ��Ҫ���ӵĻ��ܵ���Ա ���򣺻�������Ѿ������ݵı����������������Ϣ�� ��������𣬿�˰��־��˰�ʱ�Ĭ�ϻ������
	 * 
	 * @author zhangg on 2009-3-17
	 * @param aRecaVO
	 * @return
	 * @throws DAOException
	 */
	public PayfileVO[] getUnitPsnVOs(WaLoginVO waLoginVO) throws DAOException {

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select pk_psnjob," + "pk_psndoc," + "pk_psnorg," + "taxtype," + "taxtableid," + "isderate,"
				+ "isndebuct," + "derateptg," + "pk_group," + "pk_org," + "pk_bankaccbas1," + "pk_bankaccbas2,"
				+ "pk_bankaccbas3," + "partflag," + "stopflag, wa_data.* from wa_data ");
		sqlBuffer.append("     where wa_data.pk_wa_class in  ");
		sqlBuffer.append("               (select wa_unitctg.classedid from wa_unitctg,wa_waclass ");
		sqlBuffer.append("				where wa_waclass.pk_wa_class = wa_unitctg.classedid ");
		sqlBuffer.append("				and wa_unitctg.pk_wa_class = ? and wa_waclass.stopflag='N') ");
		sqlBuffer.append("      and wa_data.cyear = ? ");
		sqlBuffer.append("      and wa_data.cperiod = ? ");
		sqlBuffer.append("      and wa_data.stopflag = 'N' ");
		sqlBuffer.append("      and wa_data.pk_psndoc not in ");
		sqlBuffer.append("       (select pk_psndoc ");
		sqlBuffer.append("       		from wa_data ");
		sqlBuffer.append("          	where wa_data.pk_wa_class = ? ");
		sqlBuffer.append("            	and wa_data.cyear = ? ");
		sqlBuffer.append("              and wa_data.cperiod = ?)");

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(waLoginVO.getPk_prnt_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());
		return executeQueryVOs(sqlBuffer.toString(), parameter, PayfileVO.class);

	}

	public void updateData(WaLoginVO waLoginVO, WaItemVO[] itemVOs) throws DAOException {
		if (itemVOs == null) {
			return;
		}
		// �����ݿ���и���
		if (getBaseDao().getDBType() == DBUtil.SQLSERVER) {
			updateDataSQLDbs(itemVOs, waLoginVO);
		} else {
			updateDataOracleDbs(itemVOs, waLoginVO);
		}

	}

	/**
	 * ���»����ֶ�ֵ��sqlserver��
	 * 
	 * @author liangxr on 2010-5-27
	 * @param itemVOs
	 * @param waLoginVO
	 * @throws DAOException
	 */
	private void updateDataSQLDbs(WaItemVO[] itemVOs, WaLoginVO waLoginVO) throws DAOException {

		String tableName = getDataTableName(waLoginVO);

		// ��Ҫ���µ��ֶ�
		List<String> list = new LinkedList<String>();
		for (WaItemVO itemVO : itemVOs) {
			list.add(tableName + "." + itemVO.getItemkey() + " = sum_data." + itemVO.getItemkey() + "");
		}

		String colNames = FormatVO.formatListToString(list, "");

		// SUM���ֶ�
		List<String> sumList = new LinkedList<String>();
		for (WaItemVO itemVO : itemVOs) {
			sumList.add("sum(" + tableName + "." + itemVO.getItemkey() + ") " + itemVO.getItemkey());
		}

		String sumColNames = FormatVO.formatListToString(sumList, "");
		String extraConditon = "";

		// // ֻ�ϼƷ�ͣ����Ա
		// if (tableName.equalsIgnoreCase("wa_dataz")) {
		// extraConditon = " and exists (select 1"; // 1
		// extraConditon += "          from wa_data";
		// extraConditon += "         where wa_dataz.classid = wa_data.classid";
		// extraConditon += "           and wa_dataz.cyear = wa_data.cyear";
		// extraConditon += "           and wa_dataz.cperiod = wa_data.cperiod";
		// extraConditon += "           and wa_dataz.psnid = wa_data.psnid";
		// extraConditon += "           and wa_data.istopflag = 'N') ";
		//
		// } else {
		extraConditon = " and wa_data.stopflag = 'N' ";
		// }

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update " + tableName + " "); // 1
		sqlBuffer.append("   set " + colNames + "  "); // 2
		sqlBuffer.append("   from (select " + sumColNames + ", " + tableName + ".pk_psndoc ");
		sqlBuffer.append("     from " + tableName + " ");
		sqlBuffer.append("    where " + tableName + ".pk_wa_class in ");
		sqlBuffer.append("               (select wa_waclass.pk_wa_class ");
		sqlBuffer.append("				from wa_unitctg,wa_waclass  ");
		sqlBuffer.append("                where  wa_waclass.pk_wa_class = wa_unitctg.classedid  ");
		sqlBuffer.append("                and wa_unitctg.pk_wa_class =  ? ");
		sqlBuffer.append("				and wa_waclass.stopflag='N' )");

		sqlBuffer.append("      and " + tableName + ".cyear = ? ");
		sqlBuffer.append("      and " + tableName + ".cperiod = ? ");
		sqlBuffer.append(extraConditon);

		// ����һ���ǰ���pk_psndoc����
		sqlBuffer.append("    group by " + tableName + ".pk_psndoc) sum_data ");
		sqlBuffer.append(" where " + tableName + ".pk_wa_class = ? ");
		sqlBuffer.append(" and " + tableName + ".cyear = ?  ");
		sqlBuffer.append(" and " + tableName + ".cperiod = ?  ");
		sqlBuffer.append(" and " + tableName + ".pk_psndoc = sum_data.pk_psndoc ");
		SQLParameter parameter = new SQLParameter();

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());
		getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);

	}

	/**
	 * ���»����ֶ�ֵ��oracle��
	 * 
	 * @author liangxr on 2010-5-27
	 * @param itemVOs
	 * @param waLoginVO
	 * @throws DAOException
	 */
	private void updateDataOracleDbs(WaItemVO[] itemVOs, WaLoginVO waLoginVO) throws DAOException {

		String tableName = getDataTableName(waLoginVO);

		// ��Ҫ���µ��ֶ�
		List<String> list = new LinkedList<String>();
		for (WaItemVO itemVO : itemVOs) {
			list.add("unit." + itemVO.getItemkey());
		}

		String colNames = FormatVO.formatListToString(list, "");

		// SUM���ֶ�
		List<String> sumList = new LinkedList<String>();
		for (WaItemVO itemVO : itemVOs) {
			sumList.add(" nvl(sum(" + tableName + "." + itemVO.getItemkey() + "),0)");
		}

		String sumColNames = FormatVO.formatListToString(sumList, "");

		String extraConditon = "";
		// ֻ�ϼƷ�ͣ����Ա
		// if (tableName.equalsIgnoreCase("wa_dataz")) {
		// extraConditon = " and exists (select 1"; // 1
		// extraConditon += "          from wa_data";
		// extraConditon += "         where wa_dataz.classid = wa_data.classid";
		// extraConditon += "           and wa_dataz.cyear = wa_data.cyear";
		// extraConditon += "           and wa_dataz.cperiod = wa_data.cperiod";
		// extraConditon += "           and wa_dataz.psnid = wa_data.psnid";
		// extraConditon += "           and wa_data.istopflag = 0) ";
		//
		// } else {
		extraConditon = " and wa_data.stopflag = 'N' ";
		// }

		// ��֯SQL���
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update " + tableName + " unit "); // 1
		sqlBuffer.append("   set (" + colNames + ") = (select  " + sumColNames + " "); // 2
		sqlBuffer.append("                                  from " + tableName + " where " + tableName
				+ ".pk_wa_class in ");
		sqlBuffer.append("               (select wa_waclass.pk_wa_class ");
		sqlBuffer.append("				from wa_unitctg,wa_waclass  ");
		sqlBuffer.append("                where  wa_waclass.pk_wa_class = wa_unitctg.classedid  ");
		sqlBuffer.append("                and wa_unitctg.pk_wa_class =  ? ");
		sqlBuffer.append("				and wa_waclass.stopflag='N' )");

		sqlBuffer.append("                                       and " + tableName + ".cyear = ?  ");
		sqlBuffer.append("                                       and " + tableName + ".cperiod = ?  ");
		sqlBuffer.append(extraConditon);
		sqlBuffer.append("                                       and " + tableName
				+ ".pk_psndoc = unit.pk_psndoc group by " + tableName + ".pk_psndoc) ");
		sqlBuffer.append(" where unit.pk_wa_class = ? ");
		sqlBuffer.append("   and unit.cyear = ? ");
		sqlBuffer.append("   and unit.cperiod = ? ");

		// ���������ӷ������е���Ա
		sqlBuffer
				.append(" and unit.pk_psndoc in  (select wa_data.pk_psndoc    "
						+ "from wa_data  where wa_data.pk_wa_class in "
						+ "(select wa_waclass.pk_wa_class from wa_unitctg, wa_waclass  where wa_waclass.pk_wa_class = wa_unitctg.classedid and wa_unitctg.pk_wa_class = ? and wa_waclass.stopflag = 'N'  ) and wa_data.cyear = ? and wa_data.cperiod = ? and wa_data.stopflag = 'N') ");
		SQLParameter parameter = new SQLParameter();

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		parameter.addParam(waLoginVO.getPk_wa_class());
		parameter.addParam(waLoginVO.getPeriodVO().getCyear());
		parameter.addParam(waLoginVO.getPeriodVO().getCperiod());

		getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);
	}

	/**
	 * ���ܺ���±�־
	 * 
	 * @author liangxr on 2010-6-1
	 * @throws BusinessException
	 */
	public void updateStateforTotal(WaLoginVO waLoginVO) throws BusinessException {
		// �ؼ����־
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("   wa_data.stopflag = 'N' ");
		sqlBuffer.append(WherePartUtil.formatAddtionalWhere(getCommonWhereCondtion4Data(waLoginVO)));
		updateTableByColKey("wa_data", new String[] { PayfileVO.CACULATEFLAG, PayfileVO.CHECKFLAG }, new Object[] {
				UFBoolean.TRUE, UFBoolean.FALSE }, sqlBuffer.toString());

		// ������Ա���Ѽ�����ϣ����ܣ�
		updatePeriodState("caculateflag", UFBoolean.TRUE, waLoginVO);
	}

	/**
	 * �õ���Ҫ���ܵı�
	 * 
	 * @author zhangg on 2009-3-25
	 * @param aRecaVO
	 * @return
	 */
	public String getDataTableName(WaLoginVO waLoginVO) {
		String tableName = "wa_data";
		// boolean isZhuBi = waLoginVO.getCurrentBO().isZhuBi();
		//
		// // ��������ң�˵�����в�����ܵ���������һ�����ֲ�ͬ
		// if (isZhuBi) {
		// // �������һ�������
		// tableName = "wa_dataz";
		// }
		// // ����������ң�˵�����в�����ܵĸ���������ͬ���Ҳ�������
		// else {
		// // ����ԭ�һ�������
		// tableName = "wa_data";
		// }
		return tableName;

	}

	/**
	 * wa_data����״̬�޸�Ϊȫ��δ���� wa_data���״̬��Ϊȫ��δ��� wa_periodstate �޸�Ϊδ����,δ���
	 * 
	 * @author liangxr on 2010-7-7
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @throws DAOException
	 */
	public void updatePaydataFlag(String pk_wa_class, String cyear, String cperiod) throws DAOException {
		String sql = "update wa_data set checkflag ='N', caculateflag='N' where pk_wa_class =? and cyear=? and cperiod=?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_wa_class);
		parameter.addParam(cyear);
		parameter.addParam(cperiod);
		getBaseDao().executeUpdate(sql, parameter);

		sql = "update wa_periodstate  set checkflag= 'N',caculateflag = 'N' " + "where pk_wa_class = ? and exists "
				+ "(select wa_period.pk_wa_period  from wa_period  "
				+ "where wa_period.pk_wa_period = wa_periodstate.pk_wa_period"
				+ " and wa_period.cyear = ? and wa_period.cperiod = ? and  " + "wa_periodstate.pk_wa_class = ?)";
		parameter = new SQLParameter();
		parameter.addParam(pk_wa_class);
		parameter.addParam(cyear);
		parameter.addParam(cperiod);
		parameter.addParam(pk_wa_class);
		getBaseDao().executeUpdate(sql, parameter);
	}

	/**
	 * ���Wa_data��ĳ����Ŀ��ֵ��
	 * 
	 * @author liangxr on 2010-7-8
	 * @param vo
	 * @throws DAOException
	 */
	public void clearClassItemData(WaClassItemVO vo) throws DAOException {
		String sql = " update wa_data set " + vo.getItemkey() + " = ? where pk_wa_class=? and cyear=? and cperiod=?";

		SQLParameter parameter = new SQLParameter();
		if (vo.getItemkey().startsWith("f_")) {
			parameter.addParam(0);
		} else {
			SQLParamType value = SQLTypeFactory.getNullType(Types.VARCHAR);
			parameter.addParam(value);
		}
		parameter.addParam(vo.getPk_wa_class());
		parameter.addParam(vo.getCyear());
		parameter.addParam(vo.getCperiod());
		getBaseDao().executeUpdate(sql, parameter);

	}

	/**
	 * ���㵱ǰѡ��û����˵���Ա���� --getCorpTmSelected
	 * 
	 * @author liangxr on 2010-7-12
	 * @param cacuItem
	 * @param whereStr
	 * @return
	 * @throws DAOException
	 */
	public BigDecimal getOrgTmSelected(String cacuItem, String whereStr) throws DAOException {

		StringBuffer strSql = new StringBuffer();
		strSql.append("SELECT sum(");
		strSql.append(cacuItem);
		strSql.append(")  FROM wa_data,wa_waclass where ");
		strSql.append(whereStr);

		return new BigDecimal(getBaseDao().executeQuery(strSql.toString(), new ColumnProcessor()).toString());

	}

	/**
	 * ����֯������ֵ��������
	 * 
	 * @author liangxr on 2010-7-12
	 * @param cacuItem
	 * @param pk_org
	 * @param accYear
	 * @param accPeriod
	 * @param pk_wa_class
	 * @param sumtype
	 *            �ܶ����� 1:���ڼ䣬2�������ۼƵ����ڼ䣬3�����
	 * @return
	 * @throws DAOException
	 */
	public BigDecimal getOrgTm(String cacuItem, String pk_org, String accYear, String accPeriod, String pk_wa_class,
			int sumtype) throws DAOException {

		StringBuffer strSql = new StringBuffer();
		strSql.append("SELECT isnull(sum(");
		strSql.append(cacuItem);
		strSql.append("),0) FROM wa_data,wa_periodstate,wa_period,wa_waclass WHERE ");
		strSql.append(" wa_data.workorg = '" + pk_org + "' ");
		strSql.append(" and wa_data.pk_wa_class=wa_waclass.pk_wa_class and wa_waclass.collectflag='N' and wa_waclass.mutipleflag = 'N' "
				+ " and wa_data.pk_wa_class= wa_periodstate.pk_wa_class"
				+ " and wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
				+ " and wa_data.cyear= wa_period.cyear"
				+ " and wa_data.cperiod=wa_period.cperiod and wa_period.caccyear='");
		strSql.append(accYear);
		if (sumtype == SalaryPmtCommonValue.CURRPERIOD) {
			strSql.append("' and wa_period.caccperiod='");
			strSql.append(accPeriod);
		} else if (sumtype == SalaryPmtCommonValue.ACUMULATE_TO_CURRPERIOD) {
			strSql.append("' and wa_period.caccperiod<='");
			strSql.append(accPeriod);
		}
		strSql.append("' and wa_data.checkflag = 'Y' ");
		// strSql.append("' and wa_data.checkflag = 'Y' and wa_data.pk_wa_class='");
		// strSql.append(pk_wa_class).append("'");

		return new BigDecimal(getBaseDao().executeQuery(strSql.toString(), new ColumnProcessor()).toString());
	}

	/**
	 * ����֯����ʵ�ʷ���ֵ
	 * 
	 * @throws BusinessException
	 */
	public AppendableVO[] getOrgRealTm(String[] pk_orgs, AppendableVO[] items, String accYear, String accPeriod,
			int sumtype) throws BusinessException {
		InSQLCreator inSQLCreator = new InSQLCreator();
		HashMap<String, String> itemmap = new HashMap<String, String>();
		String[] strItems = null;
		for (AppendableVO item : items) {
			String pk_budgetItem = item.getAttributeValue(BudgetItemVO.PK_BUDGET_ITEM).toString();
			String computerule = item.getAttributeValue("computerule").toString();
			itemmap.put(pk_budgetItem, computerule);
			strItems = (String[]) ArrayUtils.add(strItems, pk_budgetItem);
		}
		try {
			StringBuffer strSql = new StringBuffer();
			strSql.append("SELECT wa_data.workorg pk_org");
			for (String strItem : strItems) {
				strSql.append(",isnull(sum(");
				strSql.append(itemmap.get(strItem));
				strSql.append("),0) realvalue");
				strSql.append(strItem);
			}
			strSql.append(" FROM wa_data,wa_periodstate,wa_period,wa_waclass WHERE  ");
			strSql.append(" wa_data.workorg in (" + inSQLCreator.getInSQL(pk_orgs) + ") ");
			strSql.append("  and wa_data.pk_wa_class=wa_waclass.pk_wa_class and wa_waclass.collectflag='N' and wa_waclass.mutipleflag = 'N' "
					+ " and wa_data.pk_wa_class= wa_periodstate.pk_wa_class"
					+ " and wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
					+ " and wa_data.cyear= wa_period.cyear"
					+ " and wa_data.cperiod=wa_period.cperiod and wa_period.caccyear='");
			strSql.append(accYear);
			if (sumtype == SalaryPmtCommonValue.CURRPERIOD) {
				strSql.append("' and wa_period.caccperiod='");
				strSql.append(accPeriod);
			} else if (sumtype == SalaryPmtCommonValue.ACUMULATE_TO_CURRPERIOD) {
				strSql.append("' and wa_period.caccperiod<='");
				strSql.append(accPeriod);
			}
			strSql.append("' and wa_data.checkflag = 'Y' ");

			strSql.append(" group by wa_data.workorg ");

			AppendableVO[] vos = executeQueryAppendableVOs(strSql.toString(), AppendableVO.class);
			AppendableVO[] itemvos = null;
			if (ArrayUtils.isEmpty(vos)) {
				return null;
			}
			for (AppendableVO vo : vos) {
				String pk_org = vo.getAttributeValue("pk_org").toString();
				for (String strItem : strItems) {

					AppendableVO tvo = new AppendableVO();
					tvo.setAttributeValue("pk_org", pk_org);
					tvo.setAttributeValue("realvalue", vo.getAttributeValue("realvalue" + strItem));
					tvo.setAttributeValue("pk_budgetItem", strItem);
					itemvos = (AppendableVO[]) ArrayUtils.add(itemvos, tvo);
				}
			}
			return itemvos;

		} finally {

		}

	}

	/**
	 * ������ ������ֵ��������
	 * 
	 * @author liangxr on 2010-7-12
	 * @param cacuItem
	 * @param pk_org
	 * @param accYear
	 * @param accPeriod
	 * @param pk_wa_class
	 * @param sumtype
	 *            �ܶ����� 1:���ڼ䣬2�������ۼƵ����ڼ䣬3�����
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, BigDecimal> getDeptTm(String cacuItem, String pk_org, String accYear, String accPeriod,
			String pk_wa_class, int sumtype) throws DAOException {

		StringBuffer strSql = new StringBuffer();
		strSql.append("SELECT wa_data.workdept pk_dept,sum(");
		strSql.append(cacuItem);
		strSql.append(") FROM wa_data,wa_periodstate,wa_period,wa_waclass WHERE  ");
		strSql.append(" wa_data.workorg = '" + pk_org + "' ");
		strSql.append(" and wa_data.pk_wa_class=wa_waclass.pk_wa_class and wa_waclass.collectflag='N' and wa_waclass.mutipleflag = 'N'"
				+ " and wa_data.pk_wa_class= wa_periodstate.pk_wa_class"
				+ " and wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
				+ " and wa_data.cyear= wa_period.cyear"
				+ " and wa_data.cperiod=wa_period.cperiod and wa_period.caccyear='");
		strSql.append(accYear);
		if (sumtype == SalaryPmtCommonValue.CURRPERIOD) {
			strSql.append("' and wa_period.caccperiod='");
			strSql.append(accPeriod);
		} else if (sumtype == SalaryPmtCommonValue.ACUMULATE_TO_CURRPERIOD) {
			strSql.append("' and wa_period.caccperiod<='");
			strSql.append(accPeriod);
		}
		strSql.append("' and wa_data.checkflag = 'Y' ");
		// strSql.append("' and wa_data.checkflag = 'Y' and wa_data.pk_wa_class='");
		// strSql.append(pk_wa_class).append("'");
		strSql.append(" group by wa_data.workdept");

		List<Object[]> list = (List<Object[]>) getBaseDao().executeQuery(strSql.toString(), new ArrayListProcessor());
		Map<String, BigDecimal> resultMap = new HashMap<String, BigDecimal>();
		for (Object[] obj : list) {
			BigDecimal bigdecimal = new BigDecimal(0);
			if (obj[1] != null) {
				bigdecimal = new BigDecimal(obj[1].toString());
			}
			resultMap.put((String) obj[0], bigdecimal);
		}
		return resultMap;
	}

	/**
	 * ȡ�ò���ʵ�ʷ���ֵ
	 * 
	 * @param pk_depts
	 * @param cacuItem
	 * @param accYear
	 * @param accPeriod
	 * @param pk_wa_class
	 * @param sumtype
	 * @return
	 * @throws BusinessException
	 */
	public AppendableVO[] getRealDeptTm(String pk_org, String[] pk_depts, AppendableVO[] items, String accYear,
			String accPeriod, int sumtype) throws BusinessException {

		InSQLCreator inSQLCreator = new InSQLCreator();
		HashMap<String, String> itemmap = new HashMap<String, String>();
		String[] strItems = null;
		for (AppendableVO item : items) {
			String pk_budgetItem = item.getAttributeValue(BudgetItemVO.PK_BUDGET_ITEM).toString();
			String computerule = item.getAttributeValue("computerule").toString();
			itemmap.put(computerule, pk_budgetItem);
			// Logger.debug("pk_budgetItem=:"+pk_budgetItem);
			// Logger.debug("computerule=:"+computerule);
			// Logger.debug("mapget="+itemmap.get(computerule));
			strItems = (String[]) ArrayUtils.add(strItems, computerule);
		}
		try {

			StringBuffer strSql = new StringBuffer();
			strSql.append("SELECT wa_data.workdept pk_dept");
			for (AppendableVO item : items) {
				strSql.append(",isnull(sum(");
				strSql.append((String) item.getAttributeValue("computerule"));
				strSql.append("),0) realvalue");
				strSql.append((String) item.getAttributeValue(BudgetItemVO.PK_BUDGET_ITEM));
			}
			strSql.append(" FROM wa_data,wa_periodstate,wa_period,wa_waclass WHERE  ");
			strSql.append(" wa_data.workorg = '" + pk_org + "' ");
			strSql.append(" and wa_data.workdept in (" + inSQLCreator.getInSQL(pk_depts) + ") ");

			strSql.append(" and wa_data.pk_wa_class=wa_waclass.pk_wa_class and wa_waclass.collectflag='N' and wa_waclass.mutipleflag = 'N' "
					+ " and wa_data.pk_wa_class= wa_periodstate.pk_wa_class"
					+ " and wa_periodstate.pk_wa_period = wa_period.pk_wa_period "
					+ " and wa_data.cyear= wa_period.cyear"
					+ " and wa_data.cperiod=wa_period.cperiod and wa_period.caccyear='");
			strSql.append(accYear);
			if (sumtype == SalaryPmtCommonValue.CURRPERIOD) {
				strSql.append("' and wa_period.caccperiod='");
				strSql.append(accPeriod);
			} else if (sumtype == SalaryPmtCommonValue.ACUMULATE_TO_CURRPERIOD) {
				strSql.append("' and wa_period.caccperiod<='");
				strSql.append(accPeriod);
			}
			strSql.append("' and wa_data.checkflag = 'Y' ");

			strSql.append(" group by wa_data.workdept ");

			AppendableVO[] vos = executeQueryAppendableVOs(strSql.toString(), AppendableVO.class);
			if (vos == null) {
				return null;
			}
			AppendableVO[] itemvos = null;
			for (AppendableVO vo : vos) {
				// String pk_dept = vo.getAttributeValue("pk_dept").toString();
				for (AppendableVO item : items) {
					String pk_budgetItem = (String) item.getAttributeValue(BudgetItemVO.PK_BUDGET_ITEM);
					AppendableVO tvo = new AppendableVO();
					tvo.setAttributeValue("pk_dept", vo.getAttributeValue("pk_dept").toString());
					tvo.setAttributeValue("realvalue", vo.getAttributeValue("realvalue" + pk_budgetItem));
					tvo.setAttributeValue("pk_budgetItem", item.getAttributeValue(BudgetItemVO.PK_BUDGET_ITEM));
					itemvos = (AppendableVO[]) ArrayUtils.add(itemvos, tvo);
				}
			}
			return itemvos;

		} finally {

		}

	}

	/**
	 * ���㵱ǰѡ��û����˵���Ա����
	 * 
	 * @author liangxr on 2010-7-23
	 * @param cacuItem
	 * @param whereStr
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, BigDecimal> getDeptTmSelected(String cacuItem, String whereStr) throws DAOException {

		StringBuffer strSql = new StringBuffer();
		strSql.append("SELECT sum(");
		strSql.append(cacuItem);
		strSql.append("),wa_data.workdept  FROM wa_data,wa_waclass where ");
		strSql.append(whereStr);
		strSql.append(" group by wa_data.workdept");
		List<Object[]> list = (List<Object[]>) getBaseDao().executeQuery(strSql.toString(), new ArrayListProcessor());
		Map<String, BigDecimal> resultMap = new HashMap<String, BigDecimal>();
		for (Object[] obj : list) {
			BigDecimal bigdecimal = new BigDecimal(0);
			if (obj[0] != null) {
				bigdecimal = new BigDecimal(obj[0].toString());
			}

			resultMap.put((String) obj[1], bigdecimal);
		}
		return resultMap;
	}

	// shenliangc 20140826 ʱ��н�ʼ��㱣��֮������������Ӧ��Ա�ķ������ݼ����־
	public void updateCalFlag4OnTime(String pk_wa_class, String cyear, String cperiod, String[] pk_psndocs)
			throws BusinessException {
		// ������㲢����ʱ��н����Ա��н�ʷ������ݼ����־
		String updateWaDataSql = "update wa_data set wa_data.caculateflag = 'N' where " + " wa_data.pk_wa_class = '"
				+ pk_wa_class + "' and " + " wa_data.cyear = '" + cyear + "' and " + " wa_data.cperiod = '" + cperiod
				+ "' and " + " wa_data.pk_psndoc in (" + SQLHelper.joinToInSql(pk_psndocs, -1) + ") and "
				+ " wa_data.checkflag = 'N' ";

		this.executeSQLs(updateWaDataSql);

		// ����н�ʷ����ڼ�״̬�����־
		String updatePeriodStateSql = "update wa_periodstate set wa_periodstate.caculateflag = 'N' where "
				+ " wa_periodstate.pk_wa_class = '" + pk_wa_class + "' and " + " wa_periodstate.pk_wa_period in "
				+ " ( " + "	select pk_wa_period from wa_period where cyear = '" + cyear + "' and cperiod = '" + cperiod
				+ "' and "
				+ " 		pk_periodscheme in ( select pk_periodscheme from wa_waclass where wa_waclass.pk_wa_class = '"
				+ pk_wa_class + "')" + " ) " + " and " + " wa_periodstate.checkflag = 'N' ";

		this.executeSQLs(updatePeriodStateSql);
	}

	// 20151031 shenliangc н�ʷ�����Ŀ�޸�������Դ�����ͬʱ���·�����ĿȨ�����ݡ�
	// �ֹ����롪�����ֹ����룬���ֹ����롪�������ֹ����룬��ĿȨ�����ݱ��ֲ��䣻
	// ���ֹ����롪�����ֹ����룬�ֹ����롪�������ֹ����룬����޸ĺ�Ϊ�ֹ����룬��ɱ༭Ȩ����ΪY���������ΪN��
	// ���ֹ����롪�����ֹ����룬��Ҫ��wa_data�е���ϸ���ݸ���Ϊ��ʼֵ��
	public void clearPaydataByClassitem(WaClassItemVO vo) throws DAOException {
		Integer itemType = vo.getIitemtype();
		String value = "null";
		if (itemType.equals(TypeEnumVO.FLOATTYPE.value())) {
			value = "0";
		}
		// ����ֻ��������ͨ��������������ܻ���Ҫ�������ŷ�����Ŀ�޸ġ���η���������Ŀ�޸ĵ������
		String updateSql = "update wa_data set wa_data." + vo.getItemkey() + " = " + value + " where "
				+ " (wa_data.pk_wa_class = '" + vo.getPk_wa_class() + "' or "
				+ " wa_data.pk_wa_class in (select pk_parentclass from wa_inludeclass where pk_childclass = '"
				+ vo.getPk_wa_class() + "') or "
				+ " wa_data.pk_wa_class in (select pk_childclass from wa_inludeclass where pk_parentclass = '"
				+ vo.getPk_wa_class() + "')) and " + " wa_data.cyear = '" + vo.getCyear() + "' and "
				+ " wa_data.cperiod = '" + vo.getCperiod() + "' and " +
				// 20151121 shenliangc NCdp205546984 н�ʷ��Ŷ�η��ź󣬵�һ�ε�н����Ŀ��Ϊ��0ֵ
				" wa_data.checkflag = 'N' ";

		this.getBaseDao().executeUpdate(updateSql);
	}

}