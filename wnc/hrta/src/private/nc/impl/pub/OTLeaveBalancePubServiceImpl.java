package nc.impl.pub;

import nc.bs.dao.BaseDAO;
import nc.pubimpl.ta.leaveextrarest.LeaveExtraRestServiceImpl;
import nc.pubimpl.ta.overtime.SegDetailServiceImpl;
import nc.pubitf.para.SysInitQuery;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.OTLeaveBalanceVO;

import org.apache.commons.lang.StringUtils;

public class OTLeaveBalancePubServiceImpl {
	private BaseDAO baseDAO = null;

	/**
	 * 根据条件计算补休假数据
	 * 
	 * @param pk_org
	 *            人力资源组织PK
	 * @param pk_psndocs
	 *            人员PK数组
	 * @param pk_depts
	 *            部门数组
	 * @param pk_leavetypecopy
	 *            休假类别PK
	 * @param maxdate
	 *            最晚可休假日期
	 * @param queryYear
	 *            年度
	 * @param beginDate
	 *            起始日期
	 * @param endDate
	 *            截止日期
	 * @return
	 * @throws BusinessException
	 */
	public OTLeaveBalanceVO[] queryOTLeaveAggvos(String pk_org, String[] pk_psndocs, String[] pk_depts,
			String pk_leavetypecopy, String maxdate, String queryYear, String beginDate, String endDate)
			throws BusinessException {
		OTLeaveBalanceVO[] ret = null;
		String initotleavetype = SysInitQuery.getParaString(pk_org, "TWHRT08");
		String initexleavetype = SysInitQuery.getParaString(pk_org, "TWHRT10");
		if (pk_leavetypecopy.equals(initotleavetype)) {
			if (StringUtils.isEmpty(queryYear)) {
				// 加载加班單
				// 按日期范围查找加班分段明细
				// 日期范围限制的不是来源单据的日期，而是产生的假期最长可休日期在该区间内
				ret = new SegDetailServiceImpl().getOvertimeToRestHoursByType(pk_org, pk_psndocs, new UFLiteralDate(
						beginDate), new UFLiteralDate(endDate), null);
			} else {
				ret = new SegDetailServiceImpl().getOvertimeToRestHoursByType(pk_org, pk_psndocs, queryYear,
						pk_leavetypecopy);
			}
		} else if (pk_leavetypecopy.equals(initexleavetype)) {
			if (StringUtils.isEmpty(queryYear)) {
				ret = new LeaveExtraRestServiceImpl().getLeaveExtHoursByType(pk_org, pk_psndocs, new UFLiteralDate(
						beginDate), new UFLiteralDate(endDate), pk_leavetypecopy, false);
			} else {
				ret = new LeaveExtraRestServiceImpl().getLeaveExtHoursByType(pk_org, pk_psndocs, queryYear,
						pk_leavetypecopy, false);
			}
		}
		return ret;
	}

	public BaseDAO getBaseDAO() {
		if (baseDAO == null) {
			baseDAO = new BaseDAO();
		}
		return baseDAO;
	}

}