package nc.impl.wa.func;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import nc.impl.wa.paydata.caculate.AbstractFormulaExecutor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.wa.pub.WaLoginContext;

import org.apache.commons.lang3.StringUtils;

public class CalAccumulatePaidFormulaExecutor extends AbstractFormulaExecutor {

	private static final String Cur_Time_Pay_Tax = "f_4"; // 本次扣税基数

	@Override
	public void excute(Object arg0, WaLoginContext context) throws BusinessException {
		String pk_wa_class = context.getPk_wa_class();
		String pk_org = context.getPk_org();
		// 查询薪资期间
		HashMap<String, String> periodMap = (HashMap<String, String>) getBaseDao().executeQuery(
				" select code,textvalue from twhr_basedoc where code in ( 'TWSP0016','TWSP0017') and dr=0 and pk_org = '"
						+ pk_org + "'", new ResultSetProcessor() {

					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {
						HashMap<String, String> periodMap = new HashMap<String, String>();
						while (rs.next()) {
							String key = "TWSP0016".equals(rs.getString(1)) ? "Start" : "End";
							periodMap.put(key, rs.getString(2));
						}
						return periodMap;
					}
				});
		if (periodMap.isEmpty()) {
			return;
		} else if (StringUtils.isEmpty(periodMap.get("Start")) || periodMap.get("Start").length() < 6) {
			return;
		} else if (StringUtils.isEmpty(periodMap.get("End")) || periodMap.get("End").length() < 6) {
			return;
		}
		String start = periodMap.get("Start");
		String end = periodMap.get("End");
		// 按人统计期间内的[福利薪资方案]的本次扣税基数
		// SALARY_DECRYPT() is for WNC only!
		String calSql = "select sum(SALARY_DECRYPT ("
				+ Cur_Time_Pay_Tax
				+ ")) cal_sum,wa.pk_psndoc  from wa_data wa inner join wa_waclass waclass on waclass.pk_wa_class=wa.pk_wa_class  where  wa.pk_wa_class in ( select PK_WA_CLASS from wa_periodstate where (PK_WA_PERIOD in (select pk_wa_period from wa_period where CONCAT(CYEAR,CPERIOD) >= '"
				+ start
				+ "' and CONCAT(CYEAR,CPERIOD) < '"
				+ end
				+ "' ) and PAYOFFFLAG = 'Y') or pk_wa_class = '"
				+ pk_wa_class
				+ "') and waclass.isferry = 'Y' and wa.pk_psndoc in (select distinct pk_psndoc from wa_cacu_data where PK_WA_CLASS='"
				+ pk_wa_class + "') group by wa.pk_psndoc";
		String updateSql = "update wa_cacu_data set wa_cacu_data.cacu_value =( select t.cal_sum from  (" + calSql
				+ " )  t  where t.pk_psndoc=wa_cacu_data.PK_PSNDOC)";
		getBaseDao().executeUpdate(updateSql);

	}

}
