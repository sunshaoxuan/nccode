package nc.impl.wa.paydata.precacu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.impl.wa.func.AbstractPreExcutorFormulaParse;
import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.pubitf.ta.overtime.ISegDetailService;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.pub.WaLoginContext;

/**
 * 應稅加班薪資函数处理
 * 
 * @author: xqy
 * @date: 2018-4-28 下午01:12:26
 * @since: eHR V6.0
 */
public class TaxableOvertimePaysPreExcutor extends AbstractPreExcutorFormulaParse {

	int convMode = 0;

	public void excute(Object pk_wa_wageform, WaLoginContext context) throws BusinessException {
		// set to zero
		String sql = "update wa_cacu_data set cacu_value = 0 where  " + "pk_wa_class = '" + context.getPk_wa_class()
				+ "' and creator = '" + context.getPk_loginUser() + "'";
		getBaseDao().executeUpdate(sql);

		sql = "select DISTINCT pk_psndoc from wa_cacu_data where pk_wa_class= '" + context.getPk_wa_class()
				+ "' and creator = '" + context.getPk_loginUser() + "'";

		ArrayList<String> pk_psndocs = (ArrayList<String>) getBaseDao().executeQuery(sql, new ColumnListProcessor());
		String cyear = context.getCyear(), cperiod = context.getCperiod();
		String[] psndocs = (String[]) pk_psndocs.toArray(new String[pk_psndocs.size()]);
		
		String[] arguments = getArguments(pk_wa_wageform.toString());
		String pk_group_item = String.valueOf(arguments[0]).replaceAll("\'", "");

		Map<String, UFDouble> calculateTaxFreeAmountByPeriod = NCLocator.getInstance().lookup(ISegDetailService.class)
				.calculateTaxableAmountByPeriod(context.getPk_org(), psndocs, cyear, cperiod,pk_group_item);

		// 测试代码
		/*
		 * Map<String, UFDouble> calculateTaxFreeAmountByPeriod = new
		 * HashMap<String, UFDouble>();
		 * 
		 * for (int i = 0; i < psndocs.length; i++) {
		 * calculateTaxFreeAmountByPeriod.put(psndocs[i],
		 * UFDouble.ONE_DBL.add(i)); }
		 */

		if (calculateTaxFreeAmountByPeriod == null || calculateTaxFreeAmountByPeriod.size() == 0) {

			return;
		}

		PersistenceManager sessionManager = null;
		try {
			sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			Iterator<Map.Entry<String, UFDouble>> iterator = calculateTaxFreeAmountByPeriod.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, UFDouble> entry = iterator.next();
				sql = "update wa_cacu_data set cacu_value = " + entry.getValue() + " where  " + "pk_wa_class = '"
						+ context.getPk_wa_class() + "' and creator = '" + context.getPk_loginUser()
						+ "'  and pk_psndoc = '" + entry.getKey() + "'";

				session.addBatch(sql);
			}
			session.executeBatch();
		} catch (DbException e) {
			throw new DAOException(e.getMessage());
		} finally {
			if (sessionManager != null) {
				sessionManager.release();
			}
		}
	}
	private BaseDAO baseDao;
	
	public BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}
}