package nc.impl.ta.psndoc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.om.pub.MapProcessor;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.FromWhereSQLUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.bd.shift.ShiftServiceFacade;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.om.aos.AOSSQLHelper;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocCommonValue;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.vorg.AdminOrgVersionVO;
import nc.vo.vorg.DeptVersionVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class TBMPsndocDAO {
	private BaseDAO baseDAOManager = new BaseDAO();

	// public boolean checkTACardID(TBMPsndocVO psndocVO, String cardID) throws
	// DAOException {
	// return checkTACardID(psndocVO.getPk_org(), cardID,
	// psndocVO.getBegindate(), psndocVO.getPk_psndoc());
	// }
	// public boolean checkTACardID(String pk_hrorg, String cardID,
	// UFLiteralDate begindate, String pk_psndoc)
	// throws DAOException {
	// String strSQL =
	// "select pk_psndoc from tbm_psndoc where pk_org = ?  and (timecardid =  ? or secondcardid = ? ) and (enddate like '9999%' or enddate >  ? ) and pk_psndoc <> ? ";
	// SQLParameter parameter = new SQLParameter();
	// parameter.addParam(pk_hrorg);
	// parameter.addParam(cardID);
	// parameter.addParam(cardID);
	// parameter.addParam(begindate.getDateBefore(1).toString());
	// parameter.addParam(pk_psndoc);
	// String pkpsndoc = (String) baseDAOManager.executeQuery(strSQL, parameter,
	// new ColumnProcessor());
	// return pkpsndoc != null && pkpsndoc.length() > 0;
	// }
	// @SuppressWarnings("unchecked")
	// public boolean checkTACardIDForImport(String pk_hrorg, String cardID,
	// UFLiteralDate begindate, String pk_psndoc)
	// throws DAOException {
	// // 考勤档案开始日期校验
	// String sql =
	// "select 1 from tbm_psndoc where pk_org = ? and ((timecardid = ? and pk_psndoc <> ?) or secondcardid = ?) and (enddate like '9999%' or enddate >= ?)";
	// SQLParameter parameter = null;
	// parameter = new SQLParameter();
	// parameter.addParam(pk_hrorg);
	// parameter.addParam(cardID);
	// parameter.addParam(pk_psndoc);
	// parameter.addParam(cardID);
	// parameter.addParam(begindate);
	// Object obj = baseDAOManager.executeQuery(sql, parameter, new
	// ColumnListProcessor());
	// ArrayList<String> alist = (ArrayList<String>) obj;
	// return alist != null && alist.size() > 0;
	// }

	@SuppressWarnings("unchecked")
	public String checkAddTBMPsndocDate(String pk_hrorg, TBMPsndocVO[] vos) throws BusinessException {
		StringBuilder chkMsg = new StringBuilder();
		InSQLCreator isc = new InSQLCreator();
		try {
			String psndocInSQL = isc.getInSQL(vos, TBMPsndocVO.PK_PSNDOC);
			// 查询人员的已有考勤档案的最晚结束日期
			String sql = "select tbm_psndoc.pk_psndoc, max(enddate) enddate from tbm_psndoc where tbm_psndoc.pk_psndoc "
					+ "in (" + psndocInSQL + ") group by tbm_psndoc.pk_psndoc";
			Map<String, String> maxEndDate = (Map<String, String>) baseDAOManager.executeQuery(sql, new MapProcessor(
					"pk_psndoc", "enddate"));
			PsndocVO[] psndocVOs = (PsndocVO[]) CommonUtils.toArray(
					PsndocVO.class,
					baseDAOManager.retrieveByClause(PsndocVO.class, "pk_psndoc in(" + psndocInSQL + ")", new String[] {
							PsndocVO.PK_PSNDOC, PsndocVO.NAME, PsndocVO.NAME2, PsndocVO.NAME3, PsndocVO.NAME4,
							PsndocVO.NAME5, PsndocVO.NAME6 }));
			Map<String, PsndocVO> psndocMap = CommonUtils.toMap(PsndocVO.PK_PSNDOC, psndocVOs);
			if (MapUtils.isEmpty(maxEndDate)) // 没有考勤档案
				return null;
			StringBuffer notEndPsn = new StringBuffer();
			StringBuffer earlyPsn = new StringBuffer();
			for (TBMPsndocVO vo : vos) {
				String pk_psndoc = vo.getPk_psndoc();
				// 取最晚结束日期
				String maxEnddate = maxEndDate.get(pk_psndoc);
				if (StringUtils.isEmpty(maxEnddate))
					continue;
				// 取人名
				String psnName = psndocMap == null ? null : (psndocMap.get(pk_psndoc) == null ? null : psndocMap.get(
						pk_psndoc).getMultiLangName());
				if (StringUtils.isEmpty(psnName))
					continue;
				if (maxEnddate.startsWith(TBMPsndocCommonValue.END_DATA_PRE)) { // 如果已有未结束的考勤档案
					notEndPsn.append(psnName).append(",");
					continue;
				}
				UFLiteralDate begindate = vo.getBegindate();
				if (begindate == null)
					continue;
				if (!begindate.after(UFLiteralDate.getDate(maxEnddate))) // 如果开始日期晚于最晚结束日期
					earlyPsn.append(ResHelper.getString("6017psndoc", "06017psndoc0120"
					/* @res "{0}的考勤档案开始时间应该晚于{1}" */, psnName, maxEnddate)).append("\r\n");
			}
			if (notEndPsn.length() != 0) {
				notEndPsn.deleteCharAt(notEndPsn.length() - 1);
				chkMsg.append(ResHelper.getString("6017psndoc", "06017psndoc0119"
				/* @res "{0}已经有未结束的考勤档案！" */, notEndPsn.toString())).append("\r\n");
			}
			chkMsg.append(earlyPsn.toString());
			return chkMsg.length() == 0 ? null : chkMsg.toString();
		} finally {
			isc.clear();
		}
	}

	// @SuppressWarnings("unchecked")
	// public String checkAddTBMPsndocDate(String pk_hrorg, TBMPsndocVO[] vos)
	// throws BusinessException {
	// StringBuilder chkMsg = new StringBuilder();
	// // 考勤档案开始日期校验
	// // String sql0 =
	// "select bd_psndoc.name  from tbm_psndoc inner join bd_psndoc  on tbm_psndoc.pk_psndoc = bd_psndoc.pk_psndoc where  tbm_psndoc.enddate like '9999%' and tbm_psndoc.pk_psndoc = ? group by  tbm_psndoc.pk_psndoc,bd_psndoc.name";
	// // String sql0 =
	// "select tbm_psndoc.pk_psndoc from tbm_psndoc  where  tbm_psndoc.enddate like '9999%' and tbm_psndoc.pk_psndoc = ? group by  tbm_psndoc.pk_psndoc";
	// InSQLCreator isc = null;
	// try{
	// isc = new InSQLCreator();
	// String psndocInSQL = isc.getInSQL(vos, TBMPsndocVO.PK_PSNDOC);
	// //查询考勤档案未结束的人员
	// String sql0 =
	// "select tbm_psndoc.pk_psndoc from tbm_psndoc  where  tbm_psndoc.enddate like '9999%' and tbm_psndoc.pk_psndoc in("+
	// psndocInSQL+") group by  tbm_psndoc.pk_psndoc";
	// List<String> pkList = (List<String>) baseDAOManager.executeQuery(sql0,
	// new ColumnListProcessor());
	// //查询人员的最大结束日期
	// String sql1 =
	// "select tbm_psndoc.pk_psndoc, max(enddate) enddate from tbm_psndoc where  tbm_psndoc.enddate not like '9999%' "
	// +
	// " and tbm_psndoc.pk_psndoc in (" + psndocInSQL +
	// ") group by tbm_psndoc.pk_org,tbm_psndoc.pk_psndoc";
	// if(CollectionUtils.isNotEmpty(pkList)){
	// String notEndInSQL = isc.getInSQL(pkList.toArray(new String[0]));
	// PsndocVO[] psndocVOs = (PsndocVO[]) CommonUtils.toArray(PsndocVO.class,
	// baseDAOManager.retrieveByClause(PsndocVO.class,
	// "pk_psndoc in("+notEndInSQL+")", new
	// String[]{PsndocVO.NAME,PsndocVO.NAME2,PsndocVO.NAME3,PsndocVO.NAME4,PsndocVO.NAME5,PsndocVO.NAME6}));
	// StringBuilder nameSB = new StringBuilder();
	// for(PsndocVO psnVO : psndocVOs){
	// nameSB.append(psnVO.getMultiLangName()).append(",");
	// }
	// nameSB.deleteCharAt(nameSB.length()-1);
	// chkMsg.append(ResHelper.getString("6017psndoc","06017psndoc0119"
	// /*@res "{0}已经有未结束的考勤档案！"*/, nameSB.toString())).append("\r\n");
	//
	// //查询人员的最大结束日期，最好也去掉上面校验过,否则有可能会造成一个人提示多条时间重复
	// sql1 =
	// "select tbm_psndoc.pk_psndoc, max(enddate) enddate from tbm_psndoc where  tbm_psndoc.enddate not like '9999%' "
	// +
	// " and tbm_psndoc.pk_psndoc in (" + psndocInSQL +
	// ") and tbm_psndoc.pk_psndoc not in (" + notEndInSQL + ")" +
	// " group by tbm_psndoc.pk_org,tbm_psndoc.pk_psndoc";
	// }
	// // 考勤档案开始日期校验
	// // String sql1 =
	// "select bd_psndoc.name,max(enddate) from tbm_psndoc inner join bd_psndoc  on tbm_psndoc.pk_psndoc = bd_psndoc.pk_psndoc where tbm_psndoc.enddate >= ? and tbm_psndoc.enddate not like '9999%' and tbm_psndoc.pk_psndoc = ? group by tbm_psndoc.pk_org,tbm_psndoc.pk_psndoc,bd_psndoc.name";
	// // 优化掉 String sql1 =
	// "select tbm_psndoc.pk_psndoc, max(enddate) from tbm_psndoc where tbm_psndoc.enddate >= ? and tbm_psndoc.enddate not like '9999%' and tbm_psndoc.pk_psndoc = ? group by tbm_psndoc.pk_org,tbm_psndoc.pk_psndoc";
	//
	// // SQLParameter parameter = new SQLParameter();
	// Map<String,String> maxEndDate = (Map<String, String>)
	// baseDAOManager.executeQuery(sql1, new
	// nc.bs.om.pub.MapProcessor("pk_psndoc","enddate"));
	// //记录应该提示"{0}的考勤档案开始时间应该晚于{1}"的map，<pk_psndoc,date>
	// Map<String, String> earlyPsnMap = new HashMap<String, String>();
	// if(MapUtils.isNotEmpty(maxEndDate)){
	// for (TBMPsndocVO vo : vos) {
	// UFLiteralDate begindate = vo.getBegindate();
	// if (begindate == null)
	// continue;
	// // parameter.clearParams();
	// // parameter.addParam(vo.getBegindate().toString());
	// // parameter.addParam(vo.getPk_psndoc());
	// // Object[] results = (Object[]) baseDAOManager.executeQuery(sql1,
	// parameter, new ArrayProcessor());
	// //
	// // if (results == null)
	// // continue;
	// // String pk_psndoc = (String) results[0];
	// // String date = (String) results[1];
	// String maxEnddate = maxEndDate.get(vo.getPk_psndoc());
	// if(StringUtils.isEmpty(maxEnddate))
	// continue;
	// UFLiteralDate maxenddate = new UFLiteralDate(maxEnddate);
	// if(begindate.before(maxenddate)||begindate.equals(maxenddate))
	// earlyPsnMap.put(vo.getPk_psndoc(), maxEnddate);
	// }
	// }
	// if(earlyPsnMap.size()==0){
	// return chkMsg.toString();
	// }
	// String[] psndocs = earlyPsnMap.keySet().toArray(new String[0]);
	// psndocInSQL = isc.getInSQL(psndocs);
	// PsndocVO[] psndocVOs = (PsndocVO[]) CommonUtils.toArray(PsndocVO.class,
	// baseDAOManager.retrieveByClause(PsndocVO.class,
	// "pk_psndoc in("+psndocInSQL+")", new
	// String[]{PsndocVO.PK_PSNDOC,PsndocVO.NAME,PsndocVO.NAME2,PsndocVO.NAME3,PsndocVO.NAME4,PsndocVO.NAME5,PsndocVO.NAME6}));
	// for(PsndocVO psnVO:psndocVOs){
	// String pk_psndoc =psnVO.getPk_psndoc();
	// String name = psnVO.getMultiLangName();
	// String date = earlyPsnMap.get(pk_psndoc);
	// String msg = ResHelper.getString("6017psndoc","06017psndoc0120"
	// /*@res "{0}的考勤档案开始时间应该晚于{1}"*/,name,date);
	// chkMsg.append(msg).append("\r\n");
	// }
	// }
	// finally{
	// if(isc!=null)
	// isc.clear();
	// }
	// return chkMsg.length()==0?null:chkMsg.toString();
	// }

	/**
	 * 更新时校验考勤档案日期 1. 结束日期为空时：需要判断是否已经存在未结束的考勤档案、是否有结束日期大于该考勤档案开始日期的考勤档案 2.
	 * 结束日期不为空时：该档案结束日期不能大于未结束考勤档案的开始日期、该档案的开始/结束日期不能在本人其他档案时间范围内
	 * 
	 * @param pk_hrorg
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public String checkUpdateTBMPsndocDate(String pk_hrorg, TBMPsndocVO[] vos) throws BusinessException {
		// 下面几个set和map都用于异常信息的提示
		Set<String> effectivePsndocSet = new HashSet<String>();// 已有生效考勤档案的人员的pk
		Map<String, String> shouldLater = new HashMap<String, String>();// {0}的考勤档案开始时间应该晚于{1}<pk_psndoc,date>
		Map<String, String> shouldNotLater = new HashMap<String, String>();// {0}的考勤档案结束时间不能晚于{1}<pk_psndoc,date>
		Map<String, String[]> beginTimeShouldNotBetween = new HashMap<String, String[]>();// {0}的考勤档案开始时间不能在{1}到{2}之间!<pk_psndoc,date[]>
		Map<String, String[]> endTimeShouldNotBetween = new HashMap<String, String[]>();// {0}的考勤档案结束时间不能在{1}到{2}之间!<pk_psndoc,date[]>
		// 查询人员已有考勤档案
		Map<String, List<TBMPsndocVO>> tbmPsndocMap = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class)
				.queryTBMPsndocMapByPsndocs(pk_hrorg, StringPiecer.getStrArray(vos, TBMPsndocVO.PK_PSNDOC));
		for (TBMPsndocVO vo : vos) {
			String pk_psndoc = vo.getPk_psndoc();
			List<TBMPsndocVO> psndocList = tbmPsndocMap == null ? null : tbmPsndocMap.get(pk_psndoc);
			if (CollectionUtils.isEmpty(psndocList)) // 数据库中无已存在考勤档案，不需要校验
				continue;
			psndocList = (List<TBMPsndocVO>) ((ArrayList<TBMPsndocVO>) psndocList).clone(); // 复制一份
			psndocList.remove(vo); // 不需要校验自身
			if (CollectionUtils.isEmpty(psndocList)) // 移除自身后无其它档案也不校验
				continue;
			TBMPsndocVO tmpPsndoc = psndocList.get(psndocList.size() - 1); // 取最新的考勤档案
			UFLiteralDate lastEndDate = tmpPsndoc.getEnddate();
			boolean isNotEndPsndoc = lastEndDate.toString().startsWith(TBMPsndocCommonValue.END_DATA_PRE); // 最新考勤档案是否未结束的考勤档案
			if (vo.getEnddate() == null)
				vo.setEnddate(UFLiteralDate.getDate(TBMPsndocCommonValue.END_DATA));
			if (vo.getEnddate().toString().startsWith(TBMPsndocCommonValue.END_DATA_PRE)) { // 结束日期为空
				if (isNotEndPsndoc) // 存在未结束的考勤档案
					effectivePsndocSet.add(pk_psndoc);
				if (vo.getBegindate().before(lastEndDate)) // 存在结束日期大于该档案开始日期的考勤档案
					shouldLater.put(pk_psndoc, lastEndDate.toString());
				continue;
			}
			// 结束日期不为空
			if (isNotEndPsndoc && !tmpPsndoc.getBegindate().after(vo.getEnddate())) // 该档案结束日期大于未结束的考勤档案的开始日期
				shouldNotLater.put(pk_psndoc, tmpPsndoc.getBegindate().toString());
			if (isNotEndPsndoc) // 如果最新考勤档案是未结束的，移除掉再做后续操作
				psndocList.remove(tmpPsndoc);
			TBMPsndocVO bgPsndoc = TBMPsndocVO.findIntersectionVO(psndocList, vo.getBegindate().toString());
			TBMPsndocVO edPsndoc = TBMPsndocVO.findIntersectionVO(psndocList, vo.getEnddate().toString());
			if (bgPsndoc != null) // 该档案开始日期在本人其他档案时间范围内
				beginTimeShouldNotBetween.put(pk_psndoc, new String[] { bgPsndoc.getBegindate().toString(),
						bgPsndoc.getEnddate().toString() });
			if (edPsndoc != null) // 该档案结束日期在本人其他档案时间范围内
				endTimeShouldNotBetween.put(pk_psndoc, new String[] { edPsndoc.getBegindate().toString(),
						edPsndoc.getEnddate().toString() });
		}
		// 需要查询很多人的姓名：
		Set<String> querySet = new HashSet<String>();
		querySet.addAll(effectivePsndocSet);
		querySet.addAll(shouldLater.keySet());
		querySet.addAll(shouldNotLater.keySet());
		querySet.addAll(beginTimeShouldNotBetween.keySet());
		querySet.addAll(endTimeShouldNotBetween.keySet());
		if (querySet.size() == 0)
			return null;
		StringBuilder chkMsg = new StringBuilder();
		InSQLCreator isc = new InSQLCreator();
		try {
			String psndocInSQL = isc.getInSQL(querySet.toArray(new String[0]));
			PsndocVO[] psndocVOs = (PsndocVO[]) CommonUtils.toArray(
					PsndocVO.class,
					baseDAOManager.retrieveByClause(PsndocVO.class, "pk_psndoc in(" + psndocInSQL + ")", new String[] {
							PsndocVO.PK_PSNDOC, PsndocVO.NAME, PsndocVO.NAME2, PsndocVO.NAME3, PsndocVO.NAME4,
							PsndocVO.NAME5, PsndocVO.NAME6 }));
			Map<String, PsndocVO> psndocMap = CommonUtils.toMap(nc.vo.bd.psn.PsndocVO.PK_PSNDOC, psndocVOs);
			// 开始拼接具体的字符串
			// 已有生效的考勤档案
			for (String pk_psndoc : effectivePsndocSet) {
				chkMsg.append(ResHelper.getString("6017psndoc", "06017psndoc0121"
				/* @res "{0}已经有生效的考勤档案！" */, psndocMap.get(pk_psndoc).getMultiLangName())).append("\r\n");
			}
			// 开始时间应该晚于
			for (String pk_psndoc : shouldLater.keySet()) {
				String date = shouldLater.get(pk_psndoc);
				chkMsg.append(ResHelper.getString("6017psndoc", "06017psndoc0120"
				/* @res "{0}的考勤档案开始时间应该晚于{1}" */, psndocMap.get(pk_psndoc).getMultiLangName(), date)).append("\r\n");
			}
			// 结束时间不能晚于
			for (String pk_psndoc : shouldNotLater.keySet()) {
				String date = shouldNotLater.get(pk_psndoc);
				chkMsg.append(ResHelper.getString("6017psndoc", "06017psndoc0122"
				/* @res "{0}的考勤档案结束时间不能晚于{1}" */, psndocMap.get(pk_psndoc).getMultiLangName(), date)).append("\r\n");
			}
			// 开始时间不能在xx之间
			for (String pk_psndoc : beginTimeShouldNotBetween.keySet()) {
				String[] dates = beginTimeShouldNotBetween.get(pk_psndoc);
				chkMsg.append(
						ResHelper.getString("6017psndoc", "06017psndoc0123"
						/* @res "{0}的考勤档案开始时间不能在{1}到{2}之间!" */, psndocMap.get(pk_psndoc).getMultiLangName(), dates[0],
								dates[1])).append("\r\n");
			}
			// 结束时间不能在xx之间
			for (String pk_psndoc : endTimeShouldNotBetween.keySet()) {
				String[] dates = endTimeShouldNotBetween.get(pk_psndoc);
				chkMsg.append(
						ResHelper.getString("6017psndoc", "06017psndoc0124"
						/* @res "{0}的考勤档案结束时间不能在{1}到{2}之间!" */, psndocMap.get(pk_psndoc).getMultiLangName(), dates[0],
								dates[1])).append("\r\n");
			}
		} finally {
			isc.clear();
		}
		return chkMsg.length() > 0 ? chkMsg.toString() : null;

	}

	// @SuppressWarnings("unchecked")
	// public String checkUpdateTBMPsndocDate1(String pk_hrorg, TBMPsndocVO[]
	// vos) throws BusinessException {
	// StringBuilder chkMsg = new StringBuilder();
	// // String nameField =
	// "name"+MultiLangUtil.getCurrentLangSeqSuffix();//人员名称字段，需要考虑多语
	// // 如果考勤档案结束日期为空，判断是否已经存在未结束的考勤档案
	// // String sql0 =
	// "select "+nameField+" from tbm_psndoc inner join bd_psndoc on tbm_psndoc.pk_psndoc = bd_psndoc.pk_psndoc where  tbm_psndoc.enddate like '9999%' and tbm_psndoc.pk_psndoc = ? and tbm_psndoc.pk_tbm_psndoc <>?";
	// String sql0 =
	// "select 1 from tbm_psndoc  where  tbm_psndoc.enddate like '9999%' and tbm_psndoc.pk_psndoc = ? and tbm_psndoc.pk_tbm_psndoc <>?";
	// // 结束日期为空，判断是否有结束日期大于该档案开始日期的记录 --参数2为begindate
	// // String sql1 =
	// "select bd_psndoc."+nameField+",max(enddate) from tbm_psndoc inner join bd_psndoc on tbm_psndoc.pk_psndoc = bd_psndoc.pk_psndoc   where tbm_psndoc.enddate >= ? and tbm_psndoc.pk_psndoc = ? and tbm_psndoc.pk_tbm_psndoc <> ? group by tbm_psndoc.pk_org,bd_psndoc.name";
	// String sql1 =
	// "select max(enddate) from tbm_psndoc where tbm_psndoc.enddate >= ? and tbm_psndoc.pk_psndoc = ? and tbm_psndoc.pk_tbm_psndoc <> ? group by tbm_psndoc.pk_org,tbm_psndoc.pk_psndoc";
	// // 结束日期不为空，判断该档案的结束日期不能大于未结束的档案开始日期--参数2为enddate
	// // String sql2 =
	// "select bd_psndoc."+nameField+",begindate from tbm_psndoc inner join bd_psndoc on tbm_psndoc.pk_psndoc = bd_psndoc.pk_psndoc where  tbm_psndoc.begindate <= ? and tbm_psndoc.enddate like '9999%' and tbm_psndoc.pk_psndoc = ? and tbm_psndoc.pk_tbm_psndoc <> ? ";//
	// 考勤档案开始日期校验
	// String sql2 =
	// "select begindate from tbm_psndoc where  tbm_psndoc.begindate <= ? and tbm_psndoc.enddate like '9999%' and tbm_psndoc.pk_psndoc = ? and tbm_psndoc.pk_tbm_psndoc <> ? ";//
	// 考勤档案开始日期校验
	// // 结束日期不为空，判断该档案的开始日期不能在本人其他档案时间范围内。--传开始日期
	// // 结束日期不为空，判断该档案的结束日期不能在本人其他档案时间范围内。--传入结束日期
	// // String sql3 =
	// "select bd_psndoc."+nameField+",begindate,enddate from tbm_psndoc inner join bd_psndoc on tbm_psndoc.pk_psndoc = bd_psndoc.pk_psndoc where  tbm_psndoc.enddate not like '9999%' and tbm_psndoc.pk_psndoc = ? and tbm_psndoc.pk_tbm_psndoc <> ? and tbm_psndoc.begindate <= ? and tbm_psndoc.enddate >=?";
	// String sql3 =
	// "select begindate,enddate from tbm_psndoc where  tbm_psndoc.enddate not like '9999%' and tbm_psndoc.pk_psndoc = ? and tbm_psndoc.pk_tbm_psndoc <> ? and tbm_psndoc.begindate <= ? and tbm_psndoc.enddate >=?";
	//
	// SQLParameter parameter = new SQLParameter();
	// Object[] results = null;
	// //下面几个set和map都用于异常信息的提示
	// Set<String> effectivePsndocSet = new HashSet<String>();//已有生效考勤档案的人员的pk
	// Map<String, String> shouldLater = new HashMap<String,
	// String>();//{0}的考勤档案开始时间应该晚于{1}<pk_psndoc,date>
	// Map<String, String> shouldNotLater = new HashMap<String,
	// String>();//{0}的考勤档案结束时间不能晚于{1}<pk_psndoc,date>
	// Map<String, String[]> beginTimeShouldNotBetween = new HashMap<String,
	// String[]>();//{0}的考勤档案开始时间不能在{1}到{2}之间!<pk_psndoc,date[]>
	// Map<String, String[]> endTimeShouldNotBetween = new HashMap<String,
	// String[]>();//{0}的考勤档案结束时间不能在{1}到{2}之间!<pk_psndoc,date[]>
	//
	// for (TBMPsndocVO vo : vos) {
	// String sql = null;
	// String pk_psndoc = vo.getPk_psndoc();
	// if (vo.getEnddate() == null) {
	// vo.setEnddate(UFLiteralDate.getDate(TBMPsndocCommonValue.END_DATA));
	// }
	// if
	// (vo.getEnddate().toString().startsWith(TBMPsndocCommonValue.END_DATA_PRE))
	// {// 为空
	// sql = sql0;
	// parameter.clearParams();
	// parameter.addParam(vo.getPk_psndoc());
	// parameter.addParam(vo.getPk_tbm_psndoc());
	// Object reslt = baseDAOManager.executeQuery(sql, parameter, new
	// ColumnProcessor());
	// if (reslt != null) {
	// effectivePsndocSet.add(vo.getPk_psndoc());
	// }
	// sql = sql1;
	// parameter.clearParams();
	// parameter.addParam(vo.getBegindate().toString());
	// parameter.addParam(pk_psndoc);
	// parameter.addParam(vo.getPk_tbm_psndoc());
	// results = (Object[]) baseDAOManager.executeQuery(sql, parameter, new
	// ArrayProcessor());
	// if (reslt != null){
	// shouldLater.put(pk_psndoc, (String) results[0]);
	// }
	// continue;
	// }
	// sql = sql2;
	// parameter.clearParams();
	// parameter.addParam(vo.getEnddate().toString());
	// parameter.addParam(pk_psndoc);
	// parameter.addParam(vo.getPk_tbm_psndoc());
	// results = (Object[]) baseDAOManager.executeQuery(sql, parameter, new
	// ArrayProcessor());
	// if (results != null){
	// shouldNotLater.put(pk_psndoc, (String) results[0]);
	// }
	// sql = sql3;
	// parameter.clearParams();
	// parameter.addParam(pk_psndoc);
	// parameter.addParam(vo.getPk_tbm_psndoc());
	// parameter.addParam(vo.getBegindate().toString());
	// parameter.addParam(vo.getBegindate().toString());
	// results = (Object[]) baseDAOManager.executeQuery(sql, parameter, new
	// ArrayProcessor());
	// if (results != null){
	// beginTimeShouldNotBetween.put(pk_psndoc, new String[]{(String)
	// results[0],(String) results[1]});
	// }
	//
	// sql = sql3;
	// parameter.clearParams();
	// parameter.addParam(pk_psndoc);
	// parameter.addParam(vo.getPk_tbm_psndoc());
	// parameter.addParam(vo.getEnddate().toString());
	// parameter.addParam(vo.getEnddate().toString());
	// results = (Object[]) baseDAOManager.executeQuery(sql, parameter, new
	// ArrayProcessor());
	// if (results == null)
	// continue;
	// endTimeShouldNotBetween.put(pk_psndoc, new String[]{(String)
	// results[0],(String) results[1]});
	// }
	// //需要查询很多人的姓名：
	// Set<String> querySet = new HashSet<String>();
	// querySet.addAll(effectivePsndocSet);
	// querySet.addAll(shouldLater.keySet());
	// querySet.addAll(shouldNotLater.keySet());
	// querySet.addAll(beginTimeShouldNotBetween.keySet());
	// querySet.addAll(endTimeShouldNotBetween.keySet());
	// if(querySet.size()==0)
	// return null;
	// InSQLCreator isc = null;
	// try{
	// isc = new InSQLCreator();
	// String psndocInSQL = isc.getInSQL(querySet.toArray(new String[0]));
	// PsndocVO[] psndocVOs = (PsndocVO[]) CommonUtils.toArray(PsndocVO.class,
	// baseDAOManager.retrieveByClause(PsndocVO.class,
	// "pk_psndoc in("+psndocInSQL+")", new
	// String[]{PsndocVO.PK_PSNDOC,PsndocVO.NAME,PsndocVO.NAME2,PsndocVO.NAME3,PsndocVO.NAME4,PsndocVO.NAME5,PsndocVO.NAME6}));
	// Map<String, PsndocVO> psndocMap =
	// CommonUtils.toMap(nc.vo.bd.psn.PsndocVO.PK_PSNDOC, psndocVOs);
	// //开始拼接具体的字符串
	// //已有生效的考勤档案
	// for(String pk_psndoc:effectivePsndocSet){
	// chkMsg.append(ResHelper.getString("6017psndoc","06017psndoc0121"
	// /*@res "{0}已经有生效的考勤档案！"*/,
	// psndocMap.get(pk_psndoc).getMultiLangName())).append("\r\n");
	// }
	// //开始时间应该晚于
	// for(String pk_psndoc:shouldLater.keySet()){
	// String date = shouldLater.get(pk_psndoc);
	// chkMsg.append(ResHelper.getString("6017psndoc","06017psndoc0120"
	// /*@res "{0}的考勤档案开始时间应该晚于{1}"*/,
	// psndocMap.get(pk_psndoc).getMultiLangName(),date)).append("\r\n");
	// }
	// //结束时间不能晚于
	// for(String pk_psndoc:shouldNotLater.keySet()){
	// String date = shouldNotLater.get(pk_psndoc);
	// chkMsg.append(ResHelper.getString("6017psndoc","06017psndoc0122"
	// /*@res "{0}的考勤档案结束时间不能晚于{1}"*/,
	// psndocMap.get(pk_psndoc).getMultiLangName(),date)).append("\r\n");
	// }
	// //开始时间不能在xx之间
	// for(String pk_psndoc:beginTimeShouldNotBetween.keySet()){
	// String[] dates = beginTimeShouldNotBetween.get(pk_psndoc);
	// chkMsg.append(ResHelper.getString("6017psndoc","06017psndoc0123"
	// /*@res "{0}的考勤档案开始时间不能在{1}到{2}之间!"*/,
	// psndocMap.get(pk_psndoc).getMultiLangName(),dates[0],dates[1])).append("\r\n");
	// }
	// //结束时间不能在xx之间
	// for(String pk_psndoc:endTimeShouldNotBetween.keySet()){
	// String[] dates = endTimeShouldNotBetween.get(pk_psndoc);
	// chkMsg.append(ResHelper.getString("6017psndoc","06017psndoc0124"
	// /*@res "{0}的考勤档案结束时间不能在{1}到{2}之间!"*/,
	// psndocMap.get(pk_psndoc).getMultiLangName(),dates[0],dates[1])).append("\r\n");
	// }
	//
	// }
	// finally{
	// if(isc!=null)
	// isc.clear();
	// }
	// if (chkMsg.length()>0) {
	// return chkMsg.toString();
	// }
	// return null;
	// }

	public TBMPsndocVO[] queryByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL) throws BusinessException {
		return queryByCondition(pk_hrorg, fromWhereSQL, null, null);
	}

	public TBMPsndocVO[] queryByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, String extraCond)
			throws BusinessException {
		return queryByCondition(pk_hrorg, fromWhereSQL, extraCond, null);
	}

	public TBMPsndocVO[] queryByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, String extraCond,
			SQLParameter extraPara) throws BusinessException {
		return queryByCondition(pk_hrorg, fromWhereSQL, extraCond, extraPara, false, false);
	}

	public TBMPsndocVO[] queryByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, String extraCond,
			SQLParameter extraPara, boolean queryPkJobOrg, boolean queryPkDept) throws BusinessException {
		return queryByCondition(pk_hrorg, fromWhereSQL, extraCond, extraPara, null, queryPkJobOrg, queryPkDept, false);
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-30 14:50:55<br>
	 * 
	 * @param strWhere
	 * @return ArrayList<GeneralVO>
	 * @throws BusinessException
	 * @author
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	public ArrayList<GeneralVO> queryTaPsninfoByCondition(String strWhere) throws BusinessException {
		String sql = " select hi_psnjob.clerkcode as code,bd_psndoc.name as psnname,bd_psndoc.name2 as psnname2,bd_psndoc.name3 as psnname3,c.name as orgname,c.name2 as orgname2,c.name3 as orgname3 ,d.name as deptname,d.name2 as deptname2,d.name3 as deptname3,e.postname,e.postname2,e.postname3,f.jobname,f.jobname2,f.jobname3,bd_psncl.name as psnclname,bd_psncl.name2 as psnclname2,bd_psncl.name3 as psnclname3,tbm_psndoc.pk_psnjob,tbm_psndoc.begindate,tbm_psndoc.enddate,tbm_psndoc.timecardid  "
				+ "  from tbm_psndoc  inner join hi_psnjob  on hi_psnjob.pk_psnjob = tbm_psndoc.pk_psnjob"
				+ " inner join bd_psndoc  on hi_psnjob.pk_psndoc = bd_psndoc.pk_psndoc  "
				+ " inner join hi_psnorg on hi_psnjob.pk_psnorg = hi_psnorg.pk_psnorg "
				+ " inner join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl "
				+ " left outer join org_orgs c on hi_psnjob.pk_org = c.pk_org "
				+ " left outer join org_dept d on hi_psnjob.pk_dept = d.pk_dept "
				+ " left outer join om_post e on hi_psnjob.pk_post =e.pk_post  "
				+ " left outer join om_job f on hi_psnjob.pk_job = f.pk_job where " + strWhere;
		return (ArrayList<GeneralVO>) baseDAOManager.executeQuery(sql, new BeanListProcessor(GeneralVO.class));
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-30 14:50:55<br>
	 * 
	 * @param pks
	 * @return ArrayList<GeneralVO>
	 * @throws BusinessException
	 * @author
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	public ArrayList<GeneralVO> queryTaPsninfoByPks(String[] pks) throws BusinessException {
		String sql = " select hi_psnjob.pk_psndoc,hi_psnjob.clerkcode as code,bd_psndoc.name as psnname,bd_psndoc.name2 as psnname2,bd_psndoc.name3 as psnname3,c.name as orgname,c.name2 as orgname2,c.name3 as orgname3 ,d.name as deptname,d.name2 as deptname2,d.name3 as deptname3,e.postname,e.postname2,e.postname3,f.jobname,f.jobname2,f.jobname3,bd_psncl.name as psnclname,bd_psncl.name2 as psnclname2,bd_psncl.name3 as psnclname3,tbm_psndoc.pk_psnjob,tbm_psndoc.pk_tbm_psndoc,tbm_psndoc.begindate,tbm_psndoc.enddate,tbm_psndoc.timecardid  "
				+ "  from tbm_psndoc  inner join hi_psnjob  on hi_psnjob.pk_psnjob = tbm_psndoc.pk_psnjob"
				+ " inner join bd_psndoc  on hi_psnjob.pk_psndoc = bd_psndoc.pk_psndoc  "
				+ " inner join hi_psnorg on hi_psnjob.pk_psnorg = hi_psnorg.pk_psnorg "
				+ " inner join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl "
				+ " left outer join org_orgs c on hi_psnjob.pk_org = c.pk_org "
				+ " left outer join org_dept d on hi_psnjob.pk_dept= d.pk_dept "
				+ " left outer join om_post e on hi_psnjob.pk_post=e.pk_post  "
				+ " left outer join om_job f on hi_psnjob.pk_job=f.pk_job  ";
		String where = " where tbm_psndoc.pk_tbm_psndoc in (";
		for (String pk : pks) {
			where += "'" + pk + "',";
		}
		where = where.substring(0, where.length() - 1) + ")";
		return (ArrayList<GeneralVO>) baseDAOManager.executeQuery(sql + where, new BeanListProcessor(GeneralVO.class));
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-30 14:50:55<br>
	 * 
	 * @param pk_hrorg
	 * @param strWhere
	 * @return ArrayList<GeneralVO>
	 * @throws BusinessException
	 * @author
	 **************************************************************************/
	@SuppressWarnings("unchecked")
	public ArrayList<GeneralVO> queryTaPsninfoForImport(String pk_hrorg, String strWhere) throws BusinessException {
		// 03-06-06 导入要按权限进行过滤，需要查询pk_psnjob
		String sql = " select tbm_psndoc.pk_psnjob,tbm_psndoc.begindate,tbm_psndoc.pk_psndoc,pk_tbm_psndoc,timecardid,bd_psndoc.code psncode, bd_psndoc.name psnname from tbm_psndoc inner join bd_psndoc on tbm_psndoc.pk_psndoc = bd_psndoc.pk_psndoc where tbm_psndoc.pk_org = ? and  "
				+ strWhere;
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_hrorg);
		return (ArrayList<GeneralVO>) baseDAOManager.executeQuery(sql, parameter,
				new BeanListProcessor(GeneralVO.class));
	}

	public TBMPsndocVO[] queryByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, String extraCond,
			SQLParameter extraPara, String[] order) throws BusinessException {
		return queryByCondition(pk_hrorg, fromWhereSQL, extraCond, extraPara, order, false, false, false);
	}

	@SuppressWarnings("unchecked")
	public TBMPsndocVO[] queryByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, String extraCond,
			SQLParameter extraPara, String[] order, boolean queryPkJobOrg, boolean queryDept, boolean isHRAdminOrg)
			throws BusinessException {
		// 看到这段代码的开发人员请注意，使用TBMPsndocSqlPiecer进行代码拼接时，凡是涉及到tbm_psndoc表的前缀表达，都用TBMPsndocSqlPiecer.TBM_PSNDOC_NAME来代替，StringPiecer会自动处理替换为实际的表名前缀
		// 支持pk_org为空时的查询。若pk_org为空，则查询结果跨HR组织
		String mainTableCond = null;
		if (StringUtils.isNotEmpty(pk_hrorg)) {
			if (isHRAdminOrg)
				mainTableCond = TBMPsndocSqlPiecer.TBM_PSNDOC_NAME + ".pk_adminorg=? and "
						+ TBMPsndocSqlPiecer.TBM_PSNDOC_NAME + ".pk_adminorg<>" + TBMPsndocSqlPiecer.TBM_PSNDOC_NAME
						+ ".pk_org";
			else
				mainTableCond = TBMPsndocSqlPiecer.TBM_PSNDOC_NAME + ".pk_org=? ";
		}
		mainTableCond = nc.hr.utils.SQLHelper.appendExtraCond(mainTableCond, extraCond);
		if (queryPkJobOrg || queryDept)// 如果要查任职组织或者部门，都需要关联任职表
			fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinPsnjobTable(fromWhereSQL);
		// 添加排序条件
		fromWhereSQL = TBMPsndocSqlPiecer.addDefaultOrderBy(fromWhereSQL);
		String sql = null;
		List<String> extraSelFieldList = new ArrayList<String>();
		// 如果fromwheresql里面关联了组织和部门版本，则需要查询版本表的vid
		String orgversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob." + PsnJobVO.PK_ORG_V
				+ FromWhereSQLUtils.getAttPathPostFix());
		if (StringUtils.isNotEmpty(orgversionAlias))
			extraSelFieldList.add(orgversionAlias + "." + AdminOrgVersionVO.PK_VID + " as " + TBMPsndocVO.PK_ORG_V);
		String deptversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob." + PsnJobVO.PK_DEPT_V
				+ FromWhereSQLUtils.getAttPathPostFix());
		if (StringUtils.isNotEmpty(deptversionAlias))
			extraSelFieldList.add(deptversionAlias + "." + DeptVersionVO.PK_VID + " as " + TBMPsndocVO.PK_DEPT_V);
		if (queryPkJobOrg) {
			extraSelFieldList.add(fromWhereSQL.getTableAliasByAttrpath("pk_psnjob"
					+ FromWhereSQLUtils.getAttPathPostFix())
					+ "." + PsnJobVO.PK_ORG + " as " + TBMPsndocVO.PK_JOBORG);
		}
		if (queryDept) {
			extraSelFieldList.add(fromWhereSQL.getTableAliasByAttrpath("pk_psnjob"
					+ FromWhereSQLUtils.getAttPathPostFix())
					+ "." + PsnJobVO.PK_DEPT);
		}
		if (extraSelFieldList.size() > 0)
			sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, TBMPsndocVO.TABLE_NAME, null,
					extraSelFieldList.toArray(new String[0]), null, mainTableCond, order);
		else
			sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, TBMPsndocVO.TABLE_NAME, mainTableCond, order);
		SQLParameter para = extraPara == null ? new SQLParameter() : extraPara;
		if (StringUtils.isNotEmpty(pk_hrorg))
			para.getParameters().add(0, pk_hrorg);
		BaseDAO dao = new BaseDAO();

		// ssx added on 2020-07-25
		// 啟碁啟用產線權限子集控制領班查詢權限（部門及人員查詢範圍）
		int hasGlbdef8 = -1;
		hasGlbdef8 = (int) dao.executeQuery(
				"select count(glbdef1) from HI_PSNDOC_GLBDEF8 where pk_psndoc = (select pk_psndoc from sm_user where cuserid = '"
						+ InvocationInfoProxy.getInstance().getUserId() + "')", new ColumnProcessor());

		if (hasGlbdef8 > 0) {
			String deptWherePart = "#DEPT_PK# in (select glbdef1 from HI_PSNDOC_GLBDEF8 where pk_psndoc = (select pk_psndoc from sm_user where cuserid = '"
					+ InvocationInfoProxy.getInstance().getUserId()
					+ "') and '"
					+ new UFLiteralDate().toString()
					+ "' between BEGINDATE and nvl(ENDDATE, '9999-12-31')) and (select count(pk_dept) from org_dept where pk_dept=#DEPT_PK# and isnull(HRCANCELED, 'N')='N') > 0";
			sql = sql.replace(
					"order by",
					" and "
							+ (sql.contains(" T1 ") ? deptWherePart.replace("#DEPT_PK#", "T1.pk_dept") : deptWherePart
									.replace("#DEPT_PK#", "psnjob.pk_dept")) + " order by ");
		}
		//

		Collection<TBMPsndocVO> c = (Collection<TBMPsndocVO>) dao.executeQuery(sql, para, new BeanListProcessor(
				TBMPsndocVO.class));
		if (CollectionUtils.isEmpty(c))
			return null;
		TBMPsndocVO[] retArray = c.toArray(new TBMPsndocVO[0]);
		if (queryPkJobOrg) {// 买一送一，查询任职组织的话，顺带查询任职组织的时区
			new TBMPsndocServiceImpl().processTimeZone(pk_hrorg, retArray);
		}
		return c.toArray(new TBMPsndocVO[0]);
	}

	@SuppressWarnings("unchecked")
	public String[] queryPsndocPksByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, String extraCond,
			SQLParameter extraPara) throws BusinessException {
		// 看到这段代码的开发人员请注意，使用TBMPsndocSqlPiecer进行代码拼接时，凡是涉及到tbm_psndoc表的前缀表达，都用TBMPsndocSqlPiecer.TBM_PSNDOC_NAME来代替，StringPiecer会自动处理替换为实际的表名前缀
		// 支持pk_org为空时的查询。若pk_org为空，则查询结果跨HR组织
		String mainTableCond = null;
		mainTableCond = TBMPsndocSqlPiecer.TBM_PSNDOC_NAME + ".pk_org=? ";
		mainTableCond = nc.hr.utils.SQLHelper.appendExtraCond(mainTableCond, extraCond);
		// 添加排序条件
		fromWhereSQL = TBMPsndocSqlPiecer.addDefaultOrderBy(fromWhereSQL);
		// String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL,
		// TBMPsndocVO.TABLE_NAME, mainTableCond, null);
		SQLParameter para = extraPara == null ? new SQLParameter() : extraPara;
		para.getParameters().add(0, pk_hrorg);
		BaseDAO dao = new BaseDAO();
		// Collection<TBMPsndocVO> c = (Collection<TBMPsndocVO>)
		// dao.executeQuery(sql, para, new BeanListProcessor(
		// TBMPsndocVO.class));
		String sql2 = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, TBMPsndocVO.TABLE_NAME,
				new String[] { TBMPsndocVO.PK_PSNDOC }, null, null, mainTableCond, null);
		List<String> psndocs = (List<String>) dao.executeQuery(sql2, para, new ColumnListProcessor());
		if (CollectionUtils.isEmpty(psndocs))
			return null;
		return psndocs.toArray(new String[0]);
	}

	@SuppressWarnings("unchecked")
	public TBMPsndocVO[] queryByPsnForBatchAdd(String pk_hrorg, FromWhereSQL fromWhereSQL) throws DAOException {
		SQLParameter param = new SQLParameter();
		String psnjob = PsnJobVO.getDefaultTableName();
		String psnjobDot = psnjob + ".";
		String exCond = psnjobDot
				+ PsnJobVO.LASTFLAG
				+ " = 'Y'  and hi_psnjob.endflag <> 'Y'  "// 批量新增时候应该过滤掉离职人员以及相关人员的
				+ " and " + psnjobDot + PsnJobVO.PK_ORG + " in (" + AOSSQLHelper.getChildrenBUInSQLByHROrgPK(pk_hrorg)
				+ ")" + " and " + psnjobDot + PsnJobVO.PK_PSNDOC
				+ " not in (select tbm_psndoc.pk_psndoc from tbm_psndoc where " + " tbm_psndoc.pk_psndoc = "
				+ PsnJobVO.getDefaultTableName() + "." + PsnJobVO.PK_PSNDOC
				+ " and enddate like '9999%' and tbm_psndoc.pk_org = ?)"
				// 考勤档案批量新增需要 过滤掉没有转入人员档案的人员信息
				+ " and " + psnjobDot + PsnJobVO.PK_PSNDOC
				+ " in (select pk_psndoc from HI_PSNORG where indocflag = 'Y' and hi_psnorg.psntype <>1 ) ";
		param.addParam(pk_hrorg);
		String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, PsnJobVO.getDefaultTableName(), exCond, null);
		Collection<PsnJobVO> c = (Collection<PsnJobVO>) new BaseDAO().executeQuery(sql, param, new BeanListProcessor(
				PsnJobVO.class));
		if (CollectionUtils.isEmpty(c))
			return null;
		PsnJobVO[] afterSortVo = c.toArray(new PsnJobVO[0]);
		// 按照人员倒序排列，因为要留最后一个任职的
		// 再按照是否主职排列，因为如果有最新主职，则要主职，如果没有主职，则要最新的兼职
		// 其他的全部去掉
		nc.vo.trade.voutils.VOUtil.descSort(afterSortVo, new String[] { PsnJobVO.PK_PSNDOC, PsnJobVO.ISMAINJOB });
		for (int i = 0; i < afterSortVo.length; i++) {
			// 第一项不用比较
			if (i == 0)
				continue;
			// 如果相同，则去掉
			if (afterSortVo[i].getPk_psndoc().equals(afterSortVo[i - 1].getPk_psndoc())) {
				c.remove(afterSortVo[i]);
			}
		}
		PsnJobVO[] afterQuiSame = c.toArray(new PsnJobVO[0]);
		TBMPsndocVO[] vos = new TBMPsndocVO[afterQuiSame.length];
		for (int i = 0; i < afterQuiSame.length; i++) {
			vos[i] = new TBMPsndocVO();
			vos[i].setPk_psndoc(afterQuiSame[i].getPk_psndoc());
			vos[i].setPk_psnjob(afterQuiSame[i].getPk_psnjob());
			vos[i].setPk_psnorg(afterQuiSame[i].getPk_psnorg());// 组织关系
		}
		return vos;
	}

	public TBMPsndocVO[] queryByPsndocInSQL(String pk_hrorg, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		return queryByPsndocInSQL(pk_hrorg, psndocInSQL, beginDate, endDate, false);
	}

	@SuppressWarnings("unchecked")
	public TBMPsndocVO[] queryByPsndocInSQL(String pk_hrorg, String psndocInSQL, UFLiteralDate beginDate,
			UFLiteralDate endDate, boolean queryPkJobOrg) throws BusinessException {
		SQLParameter para = new SQLParameter();
		para.addParam(pk_hrorg);
		para.addParam(endDate.toString());
		para.addParam(beginDate.toString());
		if (!queryPkJobOrg) {
			String cond = TBMPsndocVO.PK_ORG + "=? and " + TBMPsndocVO.PK_PSNDOC + " in(" + psndocInSQL + ") and "
					+ TBMPsndocVO.BEGINDATE + "<=? and " + TBMPsndocVO.ENDDATE + ">=?";
			return (TBMPsndocVO[]) CommonUtils.toArray(TBMPsndocVO.class,
					new BaseDAO().retrieveByClause(TBMPsndocVO.class, cond, TBMPsndocVO.ENDDATE, para));
		}
		String sql = "select tbmpsndoc.*,psnjob.pk_org as " + TBMPsndocVO.PK_JOBORG + " from "
				+ TBMPsndocVO.getDefaultTableName() + " tbmpsndoc " + "inner join " + PsnJobVO.getDefaultTableName()
				+ " psnjob on tbmpsndoc.pk_psnjob=psnjob.pk_psnjob "
				+ "where tbmpsndoc.pk_org=? and tbmpsndoc.pk_psndoc in(" + psndocInSQL
				+ ") and tbmpsndoc.begindate<=? and tbmpsndoc.enddate>=? ";
		List<TBMPsndocVO> list = (List<TBMPsndocVO>) new BaseDAO().executeQuery(sql, para, new BeanListProcessor(
				TBMPsndocVO.class));
		TBMPsndocVO[] retArray = org.springframework.util.CollectionUtils.isEmpty(list) ? null : list
				.toArray(new TBMPsndocVO[0]);
		// 买一送一，查询任职组织的话，顺带查询任职组织的时区
		new TBMPsndocServiceImpl().processTimeZone(pk_hrorg, retArray);
		return retArray;
	}

	public TBMPsndocVO queryLatestByPsnorgDate(String pk_hrorg, String pk_psnorg, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		return queryLatestByPsnorgdocDate(pk_hrorg, TBMPsndocVO.PK_PSNORG, pk_psnorg, beginDate, endDate);
	}

	public TBMPsndocVO queryLatestByPsndocDate(String pk_hrorg, String pk_psndoc, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		return queryLatestByPsnorgdocDate(pk_hrorg, TBMPsndocVO.PK_PSNDOC, pk_psndoc, beginDate, endDate);
	}

	@SuppressWarnings("unchecked")
	private TBMPsndocVO queryLatestByPsnorgdocDate(String pk_hrorg, String psnorgdocField, String pk_psnorgdoc,
			UFLiteralDate beginDate, UFLiteralDate endDate) throws BusinessException {
		boolean hasPkHrOrg = !StringUtils.isEmpty(pk_hrorg);
		String orgCond = hasPkHrOrg ? "pk_org = ? and " : " ";
		String sql = "select top 1 * from tbm_psndoc where " + orgCond + psnorgdocField
				+ "=? and begindate<=? and enddate>=? order by begindate desc";
		SQLParameter para = new SQLParameter();
		if (hasPkHrOrg)
			para.addParam(pk_hrorg);
		para.addParam(pk_psnorgdoc);
		para.addParam(endDate.toString());
		para.addParam(beginDate.toString());
		TBMPsndocVO[] vos = CommonUtils.toArray(TBMPsndocVO.class, (Collection<TBMPsndocVO>) new BaseDAO()
				.executeQuery(sql, para, new BeanListProcessor(TBMPsndocVO.class)));
		return vos == null ? null : vos[0];
	}

	/**
	 * 简单地取传入的UFDateTime的前10位作为日期，然后往前推一天，往后推一天，然后查询此pk_psndoc在这三天范围内有效的考勤档案记录。
	 * 如果没有，返回null；有的话，返回最新的一条
	 * 
	 * @param pk_psndoc
	 * @param dateTime
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public TBMPsndocVO queryByPsndocAndDateTime(String pk_psndoc, UFDateTime dateTime) throws BusinessException {
		// 前一天的00:00:00 2012-03-20修改 使用ufdatetime在DB2数据库中报错
		// UFDateTime beginDateTime = new
		// UFDateTime(dateTime.getBeginDate().getDateBefore(1).toDate());
		UFLiteralDate beginDateTime = new UFLiteralDate(dateTime.getBeginDate().getDateBefore(1).toDate());
		// 后一天的23:59:59
		// UFDateTime endDateTime = new
		// UFDateTime(dateTime.getEndDate().getDateAfter(1).toDate());
		UFLiteralDate endDateTime = new UFLiteralDate(dateTime.getEndDate().getDateAfter(1).toDate());
		String sql = "select * from " + TBMPsndocVO.getDefaultTableName() + " where " + TBMPsndocVO.PK_PSNDOC
				+ " = ? and " + TBMPsndocVO.ENDDATE + " >= ? and " + TBMPsndocVO.BEGINDATE + " <= ? order by "
				+ TBMPsndocVO.BEGINDATE + " desc ";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_psndoc);
		param.addParam(beginDateTime);
		param.addParam(endDateTime);
		TBMPsndocVO[] vos = CommonUtils.toArray(TBMPsndocVO.class, (Collection<TBMPsndocVO>) new BaseDAO()
				.executeQuery(sql, param, new BeanListProcessor(TBMPsndocVO.class)));
		return vos == null ? null : vos[0];
	}

	public String[] queryAllBUPksByHROrg(String pk_hrorg, UFLiteralDate beginDate, UFLiteralDate endDate,
			boolean mergeAosOrg) throws BusinessException {
		OrgVO[] orgVOs = queryAllBUsByHROrg(pk_hrorg, beginDate, endDate, mergeAosOrg);
		return StringPiecer.getStrArray(orgVOs, OrgVO.PK_ORG);
	}

	@SuppressWarnings("unchecked")
	public OrgVO[] queryAllBUsByHROrg(String pk_hrorg, UFLiteralDate beginDate, UFLiteralDate endDate,
			boolean mergeAosOrg) throws BusinessException {
		String cond = "pk_org in(select distinct psnjob.pk_org from " + PsnJobVO.getDefaultTableName()
				+ " psnjob where " + "exists(select 1 from " + TBMPsndocVO.getDefaultTableName()
				+ " tbmpsndoc where psnjob.pk_psnjob=tbmpsndoc.pk_psnjob and "
				+ " tbmpsndoc.pk_org=? and tbmpsndoc.begindate<=? and tbmpsndoc.enddate>=?))";
		SQLParameter para = new SQLParameter();
		para.addParam(pk_hrorg);
		para.addParam(endDate.toString());
		para.addParam(beginDate.toString());
		OrgVO[] psndocOrgVOs = (OrgVO[]) CommonUtils.toArray(OrgVO.class,
				new BaseDAO().retrieveByClause(OrgVO.class, cond, para));
		if (!mergeAosOrg)
			return psndocOrgVOs;
		// OrgVO[] aosOrgVOs =
		// NCLocator.getInstance().lookup(IAOSQueryService.class).queryAOSMembersByHROrgPK(pk_hrorg,
		// false, true);
		OrgVO[] aosOrgVOs = ShiftServiceFacade.queryOrgsByHROrg(pk_hrorg);
		if (ArrayUtils.isEmpty(aosOrgVOs))
			return psndocOrgVOs;
		if (ArrayUtils.isEmpty(psndocOrgVOs))
			return aosOrgVOs;
		Set<String> psndocOrgPkSet = new HashSet<String>();
		for (OrgVO psndocOrg : psndocOrgVOs) {
			psndocOrgPkSet.add(psndocOrg.getPk_org());
		}
		List<OrgVO> retList = new ArrayList<OrgVO>(Arrays.asList(psndocOrgVOs));
		for (OrgVO aosOrgVO : aosOrgVOs) {
			if (!psndocOrgPkSet.contains(aosOrgVO.getPk_org()))
				retList.add(aosOrgVO);
		}
		return retList.size() == psndocOrgVOs.length ? psndocOrgVOs : retList.toArray(new OrgVO[0]);
	}

	@SuppressWarnings("unchecked")
	public OrgVO[] queryAllBUsByPsndocs(String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate)
			throws BusinessException {
		InSQLCreator isc = null;
		String psndocInSQL = null;
		try {
			isc = new InSQLCreator();
			psndocInSQL = isc.getInSQL(pk_psndocs);
			String cond = "pk_org in(select distinct psnjob.pk_org from " + PsnJobVO.getDefaultTableName()
					+ " psnjob where " + "exists(select 1 from " + TBMPsndocVO.getDefaultTableName()
					+ " tbmpsndoc where psnjob.pk_psnjob=tbmpsndoc.pk_psnjob "
					+ " and tbmpsndoc.begindate<=? and tbmpsndoc.enddate>=?) and pk_psndoc in(" + psndocInSQL + "))";
			SQLParameter para = new SQLParameter();
			para.addParam(endDate.toString());
			para.addParam(beginDate.toString());
			OrgVO[] psndocOrgVOs = (OrgVO[]) CommonUtils.toArray(OrgVO.class,
					new BaseDAO().retrieveByClause(OrgVO.class, cond, para));
			return psndocOrgVOs;
		} finally {
			if (isc != null)
				isc.clear();
		}
	}

	/**
	 * 校验考勤档案时间范围内是否已有单据和刷卡信息
	 * 
	 * @param pk_tbm_psndocs
	 * @throws BusinessException
	 *             有单据和刷卡信息时抛异常
	 */
	@SuppressWarnings("unchecked")
	public void checkTBMPsndocUsed4Bill(String[] pk_tbm_psndocs) throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" select pk_psndoc from TBM_PSNDOC ");
			sql.append(" where tbm_psndoc.pk_tbm_psndoc in (" + isc.getInSQL(pk_tbm_psndocs) + ") ");
			sql.append("    and (exists (select 1 from tbm_awayh left join tbm_awayb on tbm_awayh.pk_awayh=tbm_awayb.pk_awayh ");
			sql.append("                where tbm_awayh.pk_psndoc=tbm_psndoc.pk_psndoc");
			sql.append("                and (tbm_awayb.awaybegindate between tbm_psndoc.begindate and tbm_psndoc.enddate");
			sql.append("                or tbm_awayb.awayenddate between tbm_psndoc.begindate and tbm_psndoc.enddate))");
			sql.append("         or exists (select 1 from tbm_overtimeh left join tbm_overtimeb on tbm_overtimeh.pk_overtimeh=tbm_overtimeb.pk_overtimeh ");
			sql.append("                where tbm_overtimeh.pk_psndoc=tbm_psndoc.pk_psndoc");
			sql.append("                and (tbm_overtimeb.overtimebegindate between tbm_psndoc.begindate and tbm_psndoc.enddate");
			sql.append("                or tbm_overtimeb.overtimeenddate between tbm_psndoc.begindate and tbm_psndoc.enddate))");
			sql.append("         or exists (select 1 from tbm_changeshifth left join tbm_changeshiftb on tbm_changeshifth.pk_changeshifth=tbm_changeshiftb.pk_changeshifth ");
			sql.append("                where tbm_changeshifth.pk_psndoc=tbm_psndoc.pk_psndoc");
			sql.append("                and (tbm_changeshiftb.changeshiftbegindate between tbm_psndoc.begindate and tbm_psndoc.enddate");
			sql.append("                or tbm_changeshiftb.changeshiftenddate between tbm_psndoc.begindate and tbm_psndoc.enddate))");
			sql.append("         or exists (select 1 from tbm_signh left join tbm_signb on tbm_signh.pk_signh=tbm_signb.pk_signh ");
			sql.append("                where tbm_signh.pk_psndoc=tbm_psndoc.pk_psndoc");
			sql.append("                and tbm_signb.signtime between tbm_psndoc.begindate||' 00:00:00' and tbm_psndoc.enddate||' 23:59:59')");
			sql.append("         or exists (select 1 from tbm_leaveh left join tbm_leaveb on tbm_leaveh.pk_leaveh=tbm_leaveb.pk_leaveh ");
			sql.append("                where tbm_leaveh.pk_psndoc=tbm_psndoc.pk_psndoc");
			sql.append("                and (tbm_leaveb.leavebegindate between tbm_psndoc.begindate and tbm_psndoc.enddate");
			sql.append("                or tbm_leaveb.leaveenddate between tbm_psndoc.begindate and tbm_psndoc.enddate))");
			sql.append("         or exists (select 1 from tbm_importdata where tbm_importdata.pk_psndoc=tbm_psndoc.pk_psndoc");
			sql.append("                and tbm_importdata.calendardate between tbm_psndoc.begindate and tbm_psndoc.enddate)");
			sql.append("         or exists (select 1 from tbm_awayreg where tbm_awayreg.pk_psndoc=tbm_psndoc.pk_psndoc");
			sql.append("                and (tbm_awayreg.awaybegindate between tbm_psndoc.begindate and tbm_psndoc.enddate");
			sql.append("                or tbm_awayreg.awayenddate between tbm_psndoc.begindate and tbm_psndoc.enddate))");
			sql.append("         or exists (select 1 from tbm_overtimereg where tbm_overtimereg.pk_psndoc=tbm_psndoc.pk_psndoc");
			sql.append("                and (tbm_overtimereg.overtimebegindate between tbm_psndoc.begindate and tbm_psndoc.enddate");
			sql.append("                or tbm_overtimereg.overtimeenddate between tbm_psndoc.begindate and tbm_psndoc.enddate))");
			sql.append("         or exists (select 1 from tbm_leavereg where tbm_leavereg.pk_psndoc=tbm_psndoc.pk_psndoc");
			sql.append("                and (tbm_leavereg.leavebegindate between tbm_psndoc.begindate and tbm_psndoc.enddate");
			sql.append("                or tbm_leavereg.leaveenddate between tbm_psndoc.begindate and tbm_psndoc.enddate))");
			sql.append("         or exists (select 1 from tbm_changeshiftreg where tbm_changeshiftreg.pk_psndoc=tbm_psndoc.pk_psndoc");
			sql.append("                and (tbm_changeshiftreg.changeshiftbegindate between tbm_psndoc.begindate and tbm_psndoc.enddate");
			sql.append("                or tbm_changeshiftreg.changeshiftenddate between tbm_psndoc.begindate and tbm_psndoc.enddate))");
			sql.append("         or exists (select 1 from tbm_signreg where tbm_signreg.pk_psndoc=tbm_psndoc.pk_psndoc");
			sql.append("                and tbm_signreg.signdate between tbm_psndoc.begindate and tbm_psndoc.enddate)");
			sql.append("         or exists (select 1 from tbm_shutdownreg where tbm_shutdownreg.pk_psndoc=tbm_psndoc.pk_psndoc");
			sql.append("                and (tbm_shutdownreg.begindate between tbm_psndoc.begindate and tbm_psndoc.enddate");
			sql.append("                or tbm_shutdownreg.enddate between tbm_psndoc.begindate and tbm_psndoc.enddate)))");
			List<String> existPks = (List<String>) new BaseDAO()
					.executeQuery(sql.toString(), new ColumnListProcessor());
			if (CollectionUtils.isEmpty(existPks))
				return;
			throw new BusinessException(ResHelper.getString("6017psndoc", "06017psndoc0146"
			/* @res "{0}在考勤档案时间范围内已经有业务记录，不能删除考勤档案！" */, CommonUtils.getPsnNames(existPks.toArray(new String[0]))));
		} finally {
			isc.clear();
		}
	}

	/**
	 * 修改考勤档案的结束日期，则结束日期之后不能有有单据信息，业务单据继续处理会发生异常，否则不能修改
	 * 
	 * @param pk_tbm_psndocs
	 * @throws BusinessException
	 *             有单据和刷卡信息时抛异常
	 */
	@SuppressWarnings("unchecked")
	public void checkTBMPsndocEndDateUsed4Bill(TBMPsndocVO vo) throws BusinessException {
		// 只对修改结束日期有效
		if (VOStatus.UPDATED != vo.getStatus())
			return;
		// 结束日为空则不用校验
		if (TBMPsndocCommonValue.END_DATA.equals(vo.getEnddate().toString()))
			return;
		StringBuffer sql = new StringBuffer();
		sql.append(" select pk_psndoc from TBM_PSNDOC ");
		sql.append(" where tbm_psndoc.pk_tbm_psndoc = '" + vo.getPk_tbm_psndoc() + "' ");
		sql.append("    and (exists (select 1 from tbm_awayh left join tbm_awayb on tbm_awayh.pk_awayh=tbm_awayb.pk_awayh ");
		sql.append("                where tbm_awayh.pk_psndoc=tbm_psndoc.pk_psndoc");
		sql.append("                and tbm_awayb.awayenddate >= '" + vo.getEnddate() + "' )");
		sql.append("         or exists (select 1 from tbm_overtimeh left join tbm_overtimeb on tbm_overtimeh.pk_overtimeh=tbm_overtimeb.pk_overtimeh ");
		sql.append("                where tbm_overtimeh.pk_psndoc=tbm_psndoc.pk_psndoc");
		sql.append("                and tbm_overtimeb.overtimeenddate >= '" + vo.getEnddate() + "' )");
		sql.append("         or exists (select 1 from tbm_changeshifth left join tbm_changeshiftb on tbm_changeshifth.pk_changeshifth=tbm_changeshiftb.pk_changeshifth ");
		sql.append("                where tbm_changeshifth.pk_psndoc=tbm_psndoc.pk_psndoc");
		sql.append("                and tbm_changeshiftb.changeshiftenddate >= '" + vo.getEnddate() + "' )");
		sql.append("         or exists (select 1 from tbm_signh left join tbm_signb on tbm_signh.pk_signh=tbm_signb.pk_signh ");
		sql.append("                where tbm_signh.pk_psndoc=tbm_psndoc.pk_psndoc");
		sql.append("                and tbm_signb.signtime >= '" + vo.getEnddate() + "' )");
		sql.append("         or exists (select 1 from tbm_leaveh left join tbm_leaveb on tbm_leaveh.pk_leaveh=tbm_leaveb.pk_leaveh ");
		sql.append("                where tbm_leaveh.pk_psndoc=tbm_psndoc.pk_psndoc");
		sql.append("                and tbm_leaveb.leaveenddate >= '" + vo.getEnddate() + "' )");
		sql.append("         or exists (select 1 from tbm_importdata where tbm_importdata.pk_psndoc=tbm_psndoc.pk_psndoc");
		sql.append("                and tbm_importdata.calendardate  >= '" + vo.getEnddate() + "' )");
		sql.append("         or exists (select 1 from tbm_awayreg where tbm_awayreg.pk_psndoc=tbm_psndoc.pk_psndoc");
		sql.append("                and tbm_awayreg.awayenddate >= '" + vo.getEnddate() + "' )");
		sql.append("         or exists (select 1 from tbm_overtimereg where tbm_overtimereg.pk_psndoc=tbm_psndoc.pk_psndoc");
		sql.append("                and tbm_overtimereg.overtimeenddate >= '" + vo.getEnddate() + "' )");
		sql.append("         or exists (select 1 from tbm_leavereg where tbm_leavereg.pk_psndoc=tbm_psndoc.pk_psndoc");
		sql.append("                and tbm_leavereg.leaveenddate >= '" + vo.getEnddate() + "' )");
		sql.append("         or exists (select 1 from tbm_changeshiftreg where tbm_changeshiftreg.pk_psndoc=tbm_psndoc.pk_psndoc");
		sql.append("                and tbm_changeshiftreg.changeshiftenddate  >= '" + vo.getEnddate() + "' )");
		sql.append("         or exists (select 1 from tbm_signreg where tbm_signreg.pk_psndoc=tbm_psndoc.pk_psndoc");
		sql.append("                and tbm_signreg.signdate  >= '" + vo.getEnddate() + "' )");
		sql.append("         or exists (select 1 from tbm_shutdownreg where tbm_shutdownreg.pk_psndoc=tbm_psndoc.pk_psndoc");
		sql.append("                and tbm_shutdownreg.enddate  >= '" + vo.getEnddate() + "' ))");
		List<String> existPks = (List<String>) new BaseDAO().executeQuery(sql.toString(), new ColumnListProcessor());
		if (CollectionUtils.isEmpty(existPks))
			return;
		throw new BusinessException(ResHelper.getString("6017psndoc", "06017psndoc0151"
		/* @res "结束日期错误！{0}在考勤档案结束日期之后已经有业务记录！" */, CommonUtils.getPsnNames(existPks.toArray(new String[0]))));
	}

	public void checkTBMPsndocEndDateSealed4HROrg(String pk_hrorg) throws BusinessException {
		// 所有考勤档案的结束日期，都应该属于一个已经封存的考勤期间，否则就需要抛异常
		// String sql =
		// "select top 1 1 from tbm_psndoc psndoc where psndoc.pk_org=? and not exists("
		// +
		// "select 1 from "+PeriodVO.getDefaultTableName()+" period " +
		// "where period."+PeriodVO.PK_ORG+"=psndoc.pk_org and period."+PeriodVO.SEALFLAG+"='N' and psndoc.enddate between period."+PeriodVO.BEGINDATE+" and period."+PeriodVO.ENDDATE+")";
	}

	public void checkTBMPsndocEndDateSealed4BU(String pk_org) throws BusinessException {

	}

	@SuppressWarnings("unchecked")
	public TBMPsndocVO queryByPsndocAndDate(String pk_psndoc, UFLiteralDate date) throws BusinessException {
		String sql = "select * from " + TBMPsndocVO.getDefaultTableName() + " where " + TBMPsndocVO.PK_PSNDOC
				+ " = ? and " + TBMPsndocVO.ENDDATE + " >= ? and " + TBMPsndocVO.BEGINDATE + " <= ? order by "
				+ TBMPsndocVO.BEGINDATE + " desc ";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_psndoc);
		param.addParam(date);
		param.addParam(date);
		TBMPsndocVO[] vos = CommonUtils.toArray(TBMPsndocVO.class, (Collection<TBMPsndocVO>) new BaseDAO()
				.executeQuery(sql, param, new BeanListProcessor(TBMPsndocVO.class)));
		return vos == null ? null : vos[0];
	}

	/**
	 * 查询默认班次主键
	 */
	public String queryDefshift(String pk_org) throws BusinessException {
		String sql = "select pk_shift from bd_shift where defaultflag = 'Y' and pk_org='" + pk_org + "'";
		String result = (String) new BaseDAO().executeQuery(sql, new ColumnProcessor());
		return result;
	}
}