package nc.impl.pub;

import nc.bs.dao.BaseDAO;
import nc.pubimpl.ta.overtime.SegDetailServiceImpl;
import nc.pubitf.para.SysInitQuery;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.overtime.OTLeaveBalanceVO;

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
     * @param beginDate
     *            起始日期
     * @param endDate
     *            截止日期
     * @return
     * @throws BusinessException
     */
    public OTLeaveBalanceVO[] queryOTLeaveAggvos(String pk_org, String[] pk_psndocs, String[] pk_depts,
	    String pk_leavetypecopy, String maxdate, String beginDate, String endDate) throws BusinessException {
	OTLeaveBalanceVO[] ret = null;
	String initleavetype = SysInitQuery.getParaString(pk_org, "TWHRT08");
	if (pk_leavetypecopy.equals(initleavetype)) {
	    // 加载加班單
	    // 按日期范围查找加班分段明细
	    // 日期范围限制的不是来源单据的日期，而是产生的假期最长可休日期在该区间内
	    // ISegDetailService service =
	    // NCLocator.getInstance().lookup(ISegDetailService.class);
	    ret = new SegDetailServiceImpl().getOvertimeToRestHoursByType(pk_org, pk_psndocs, new UFLiteralDate(
		    beginDate), new UFLiteralDate(endDate), null);
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