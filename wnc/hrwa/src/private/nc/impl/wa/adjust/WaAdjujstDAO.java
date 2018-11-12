package nc.impl.wa.adjust;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import nc.bs.bd.cache.CacheProxy;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.StringPiecer;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hr.managescope.ManagescopeFacade;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hi.wadoc.PsndocWadocVO;
import nc.vo.hr.frame.persistence.BooleanProcessor;
import nc.vo.hr.managescope.ManagescopeBusiregionEnum;
import nc.vo.hr.pub.FormatVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.uif2.LoginContext;
import nc.vo.util.AuditInfoUtil;
import nc.vo.wa.adjust.AggPsnappaproveVO;
import nc.vo.wa.adjust.PsnappaproveBVO;
import nc.vo.wa.adjust.PsnappaproveVO;
import nc.vo.wa.adjust.WaAdjustParaTool;
import nc.vo.wa.grade.WaCriterionVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 定调资Dao<BR>
 * <BR>
 * 
 * @author: xuhw
 * @date: 2009-11-26 上午09:27:04
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class WaAdjujstDAO extends BaseDAOManager {
	/**
	 * 根据条件查询定调资信息
	 * 
	 * @author xuhw on 2010-7-1
	 * @param context
	 * @param strCondition
	 * @return
	 * @throws BusinessException
	 */
	private PsnappaproveBVO[] queryPsnappaproveByCondition(
			LoginContext context, String strCondition) throws BusinessException {
		StringBuffer sqlquery = new StringBuffer();

		InSQLCreator inSQLCreator = new InSQLCreator();

		try {
			sqlquery.append(queryPsnAppSql(inSQLCreator, context));
			if (!StringUtils.isEmpty(strCondition)) {
				// zhoumxc 20140821 取最新的定调资维护记录
				if (!strCondition.contains("hi_psnjob.pk_psnjob")) {
					if (strCondition.contains("hi_psndoc_wadoc.nmoney")
							|| strCondition
									.contains("hi_psndoc_wadoc.pk_wa_prmlv")
							|| strCondition
									.contains("hi_psndoc_wadoc.pk_wa_seclv")) {
						sqlquery.append(" and hi_psndoc_wadoc.lastflag = 'Y' ");
					}
				}
				sqlquery.append(strCondition);
			}
			PsnappaproveBVO[] vos = setApprovedISTrue(executeQueryVOs(
					sqlquery.toString(), PsnappaproveBVO.class));

			return vos;
		} finally {
			inSQLCreator.clear();
		}
	}

	public PsnappaproveBVO[] queryPsnappaproveByHeaderPK(String pk)
			throws DAOException {
		// 薪资项目
		StringBuffer sbf = new StringBuffer();
		sbf.append(" select wa_psnappaprove_b.*, ( isnull(oldprml.levelname,'')|| ( case when oldseclv.levelname is null then '' else '/'||oldseclv.levelname  end))  as pk_wa_crt_old_showname, ( isnull(applyprml.levelname,'')||( case when applyseclv.levelname is null then '' else '/'||applyseclv.levelname  end))   as pk_wa_crt_apply_showname, ");
		sbf.append(" ( isnull(approveprml.levelname,'')|| ( case when approveseclv.levelname is null then '' else '/'||approveseclv.levelname  end) )  as pk_wa_crt_cofm_showname,wa_grade.name as pk_wa_grd_showname ");
		sbf.append(" from wa_psnappaprove_b ");
		sbf.append(" left outer join wa_prmlv oldprml on wa_psnappaprove_b.pk_wa_prmlv_old = oldprml.pk_wa_prmlv ");
		sbf.append(" left outer join wa_prmlv applyprml on wa_psnappaprove_b.pk_wa_prmlv_apply = applyprml.pk_wa_prmlv ");
		sbf.append(" left outer join wa_prmlv approveprml on wa_psnappaprove_b.pk_wa_prmlv_cofm = approveprml.pk_wa_prmlv ");
		sbf.append(" left outer join wa_seclv  oldseclv  on wa_psnappaprove_b.pk_wa_seclv_old = oldseclv.pk_wa_seclv ");
		sbf.append(" left outer join wa_seclv  applyseclv  on wa_psnappaprove_b.pk_wa_seclv_apply = applyseclv.pk_wa_seclv ");
		sbf.append(" left outer join wa_seclv approveseclv  on wa_psnappaprove_b.pk_wa_seclv_cofm = approveseclv.pk_wa_seclv ");
		sbf.append(" left outer join wa_grade on wa_psnappaprove_b.pk_wa_grd = wa_grade.pk_wa_grd ");

		sbf.append(" where wa_psnappaprove_b.pk_psnapp= '" + pk + "' ");

		//
		return executeQueryVOs(sbf.toString(), PsnappaproveBVO.class);
	}

	/**
	 * 批量增加根据部门PK查询 第一种方式
	 * 
	 * @param strPKdeptdocs
	 * @return
	 * @throws BusinessException
	 * 
	 */
	public PsnappaproveBVO[] queryPsnappaproveByDepts(String[] strPKdeptdocs,
			LoginContext context) throws BusinessException {
		String strInWhere = FormatVO.formatArrayToString(strPKdeptdocs, "'");
		StringBuffer sqlquery = new StringBuffer();
		sqlquery.append("  and hi_psnjob.pk_dept in (" + strInWhere + ")");
		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "hi_psnjob");
		if (!StringUtils.isEmpty(powerSql)) {
			sqlquery.append(" and " + powerSql);
		}
		sqlquery.append(" order by  "
				+ SQLHelper.getMultiLangNameColumn("org_dept.name")
				+ " ,bd_psndoc.code");
		return queryPsnappaproveByCondition(context, sqlquery.toString());
	}

	/**
	 * 批量增加 第二种方式 人员 查询
	 * 
	 * @param conditions
	 * @return
	 * @throws BusinessException
	 */
	public PsnappaproveBVO[] queryPsnappaproveBVOForadd(String conditions,
			LoginContext context) throws BusinessException {
		StringBuffer sqlquery = new StringBuffer();
		if (!StringUtils.isEmpty(conditions)) {
			sqlquery.append(conditions);
		}
		String powerSql = WaPowerSqlHelper.getWaPowerSql(context.getPk_group(),
				HICommonValue.RESOUCECODE_6007PSNJOB,
				IHRWADataResCode.WADEFAULT, "hi_psnjob");
		if (!StringUtils.isEmpty(powerSql)) {
			sqlquery.append(" and " + powerSql);
		}
		// 过滤未启用组织职能的人员
		sqlquery.append(" and hi_psnjob.pk_org in (select pk_adminorg from org_admin_enable) ");
		sqlquery.append(" order by  "
				+ SQLHelper.getMultiLangNameColumn("org_dept.name")
				+ " ,bd_psndoc.code");

		return queryPsnappaproveByCondition(context, sqlquery.toString());
	}

	/**
	 * 批量增加 多选人员查询 第三种方式
	 * 
	 * @param strPKdeptdocs
	 * @return
	 * @throws BusinessException
	 * 
	 */
	public PsnappaproveBVO[] queryPsnappaproveByPsnPks(String[] strPKpsndoc,
			LoginContext context) throws BusinessException {
		String strInWhere = FormatVO.formatArrayToString(strPKpsndoc, "'");
		StringBuffer sqlquery = new StringBuffer();
		sqlquery.append(" and bd_psndoc.pk_psndoc in (" + strInWhere + ")");
		sqlquery.append(" order by  "
				+ SQLHelper.getMultiLangNameColumn("org_dept.name")
				+ " ,bd_psndoc.code");
		return queryPsnappaproveByCondition(context, sqlquery.toString());
	}

	public PsnappaproveBVO[] queryPsnappaproveByPKpsnjob(String[] strPKPsnjob,
			LoginContext context) throws BusinessException {
		String strInWhere = FormatVO.formatArrayToString(strPKPsnjob, "'");
		StringBuffer sqlquery = new StringBuffer();
		sqlquery.append(" and hi_psnjob.pk_psnjob in (" + strInWhere + ")");
		sqlquery.append(" order by  "
				+ SQLHelper.getMultiLangNameColumn("org_dept.name")
				+ " ,bd_psndoc.code");
		return queryPsnappaproveByCondition(context, sqlquery.toString());
	}

	/**
	 * 设置默认选中
	 * 
	 * @author xuhw on 2010-1-18
	 * @param psnappaproveBVOs
	 */
	private PsnappaproveBVO[] setApprovedISTrue(
			PsnappaproveBVO[] psnappaproveBVOs) {
		if (psnappaproveBVOs == null) {
			return null;
		}
		for (PsnappaproveBVO psnappaproveBVO : psnappaproveBVOs) {
			psnappaproveBVO.setApproved(UFBoolean.valueOf(true));
		}

		return psnappaproveBVOs;
	}

	/**
	 * 批量增加人员
	 * 
	 * @return
	 */
	private String queryPsnAppSql(InSQLCreator inSQLCreator,
			LoginContext context) throws DAOException {
		StringBuffer sqlquery = new StringBuffer();
		sqlquery.append(" select distinct bd_psndoc.pk_psndoc, ");
		sqlquery.append("         "
				+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name")
				+ "  as psnname , ");
		sqlquery.append("        bd_psndoc.code as psncode , ");
		sqlquery.append("         "
				+ SQLHelper.getMultiLangNameColumn("org_dept.name")
				+ "  as deptname, ");

		sqlquery.append("        om_job.jobname as jobname, ");
		sqlquery.append("        om_postseries.postseriesname as postseriesname, ");
		sqlquery.append("         "
				+ SQLHelper.getMultiLangNameColumn("bd_psncl.name")
				+ "  as psnclname, ");
		sqlquery.append("        om_post.postname as postname, ");

		sqlquery.append("        hi_psnjob.clerkcode as clerkcode, ");
		sqlquery.append("        hi_psnjob.pk_psnjob as pk_psnjob, ");
		sqlquery.append("       (case when  hi_psnjob.ismainjob='Y' then 'N' else 'Y' end) as partflag, ");
		sqlquery.append("        hi_psnjob.assgid ");
		sqlquery.append("   from hi_psnjob  ");
		sqlquery.append("  inner join bd_psndoc on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc ");
		sqlquery.append("  inner join hi_psnorg on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg ");
		sqlquery.append("  left outer join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept ");
		sqlquery.append("  left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl  ");
		sqlquery.append("  left outer join om_job on om_job.pk_job = hi_psnjob.pk_job ");
		sqlquery.append("  left outer join om_postseries on om_postseries.pk_postseries = hi_psnjob.pk_postseries ");
		sqlquery.append("  left outer join om_post on om_post.pk_post = hi_psnjob.pk_post ");
		// zhoumxc 20140819 添加查询条件，与查询模板的条件匹配
		sqlquery.append("  left outer join hi_psndoc_wadoc  on HI_PSNDOC_WADOC.pk_psndoc = hi_psnjob.pk_psndoc ");
		try {
			ManagescopeBusiregionEnum busiregionEnum = ManagescopeBusiregionEnum.salary;
			Integer allowed = WaAdjustParaTool.getWaOrg(context.getPk_group());
			if (allowed.equals(1)) {
				busiregionEnum = ManagescopeBusiregionEnum.psndoc;
			}
			/*
			 * OrgVO[] orgVOs = NCLocator.getInstance().lookup(
			 * IAOSQueryService.class).queryOrgByHROrgPK(context.getPk_org(),
			 * null, null, OrgQueryMode.Independent);
			 * 
			 * String[] pkOrgArray = new String[orgVOs.length]; for (int i = 0;
			 * i < orgVOs.length; i++) { pkOrgArray[i] = orgVOs[i].getPk_org();
			 * }
			 * 
			 * String pkOrgs = inSQLCreator.getInSQL(pkOrgArray);
			 */
			//
			sqlquery.append(" where ( hi_psnjob.pk_psnjob in "
					+ ManagescopeFacade.queryPsnjobPksSQLByHrorgAndBusiregion(
							context.getPk_org(), busiregionEnum, true));
			// sqlquery.append("   or ( hi_psnjob.pk_org in ( " + pkOrgs +
			// " )  and  hi_psnjob.lastflag = 'Y' and  hi_psnjob.psntype =  0 ))");
		} catch (BusinessException e) {
			throw new DAOException(e);
		}
		// sqlquery.append("    hi_psnjob.pk_group = '" + context.getPk_group()
		// + "' and hi_psnjob.pk_org = '" + context.getPk_org() + "'");
		sqlquery.append("    and hi_psnorg.pk_psndoc = bd_psndoc.pk_psndoc ");
		// 人员组织关系是否限定
		sqlquery.append("    and hi_psnorg.indocflag = 'Y' ");
		// sqlquery.append("    and hi_psnjob.lastflag = 'Y' ");
		// sqlquery.append("    and hi_psnjob.endflag = 'N' ");
		// sqlquery.append("    and hi_psnorg.endflag = 'N' ");
		sqlquery.append("    and hi_psnorg.lastflag = 'Y' ");

		// 兼职记录也要查出来
		UFBoolean partflagShow = WaAdjustParaTool.getPartjob_Adjmgt(context
				.getPk_group());
		if (!partflagShow.booleanValue()) {
			sqlquery.append("    and hi_psnjob.ismainjob = 'Y' ");
		}
		// sqlquery.append("  and hi_psnorg.psntype in (0,1) and bd_psndoc.enablestate =2");
		sqlquery.append(" )");
		return sqlquery.toString();
	}

	/*
	 * public PsnappaproveBVO getDefultApply(PsnappaproveBVO psnappaproveBVO)
	 * throws BusinessException { String pk_wa_item =
	 * psnappaproveBVO.getPk_wa_item(); String pk_wa_grd =
	 * psnappaproveBVO.getPk_wa_grd(); String pk_psndoc =
	 * psnappaproveBVO.getPk_psndoc(); if (pk_psndoc != null && pk_wa_grd !=
	 * null && pk_wa_item != null) { String sql = new
	 * WaPsnHiDAO().getCriterion(pk_wa_grd, pk_psndoc); if (sql != null) {
	 * PsnappaproveBVO bvo = executeQueryVO(sql, PsnappaproveBVO.class); if (bvo
	 * != null) { psnappaproveBVO.setIsMultiSec(UFBoolean.valueOf(false)); if
	 * (sql.indexOf("wa_seclv") != -1) {
	 * psnappaproveBVO.setIsMultiSec(UFBoolean.valueOf(true)); }
	 * psnappaproveBVO.setPk_wa_crt(bvo.getPk_wa_crt());
	 * psnappaproveBVO.setPk_wa_prmlv_apply(bvo.getPk_wa_prmlv_apply());
	 * psnappaproveBVO.setPk_wa_seclv_apply(bvo.getPk_wa_seclv_apply()); // 默认值
	 * psnappaproveBVO.setNegotiation(UFBoolean.valueOf(false));
	 * psnappaproveBVO.setWa_apply_money(bvo.getWa_apply_money());
	 * psnappaproveBVO.setWa_crt_apply_money(bvo.getWa_apply_money());
	 * psnappaproveBVO
	 * .setPk_wa_crt_apply_showname(getCrtName(bvo.getPk_wa_prmlv_apply
	 * (),bvo.getPk_wa_seclv_apply(),
	 * psnappaproveBVO.getIsMultiSec().booleanValue()));
	 * psnappaproveBVO.setCrt_max_value(bvo.getCrt_max_value());
	 * psnappaproveBVO.setCrt_min_value(bvo.getCrt_min_value()); } } } return
	 * psnappaproveBVO; }
	 */
	// 20151208 xiejie3 NCdp205554161 入职推调薪，单据没有根据级别档别属性值自动带出
	// 推单时要求支持每个人多个业务流配置，所以修改逻辑：查询每个人的业务流配置，进行推单默认值设置。
	public PsnappaproveBVO[] getDefultApplys(PsnappaproveBVO[] psnappaproveBVOs)
			throws BusinessException {
		// 20151229 NCdp205566669 xiejie3
		// 入职推定调资申请单，当人员符合薪资标准级别属性时，推单生成单据中人员的薪资标准没有自动带出
		// xiejie3 由于批量处理。效率较低，所以此处暂时采取循环处理sql。
		// List<String> pk_psndocList=new ArrayList<String>();
		// List<String> pk_wa_grdList=new ArrayList<String>();
		// for (PsnappaproveBVO psnappaproveBVO : psnappaproveBVOs) {
		// String pk_wa_item = psnappaproveBVO.getPk_wa_item();
		// String pk_wa_grd = psnappaproveBVO.getPk_wa_grd();
		// String pk_psndoc=psnappaproveBVO.getPk_psndoc();
		// if (!StringUtils.isEmpty(pk_psndoc) && pk_wa_grd != null
		// && pk_wa_item != null) {
		// pk_psndocList.add(pk_psndoc);
		// pk_wa_grdList.add(pk_wa_grd);
		// }
		// }
		// String sql = new WaPsnHiDAO().getCriterions(pk_wa_grdList.toArray(new
		// String[0]), pk_psndocList.toArray(new String[0]));
		// String[] sqlArray=new String[psnappaproveBVOs.length];
		List<String> sqlArrayList = new ArrayList<String>();
		// 2016-1-9 NCdp205570961 zhousze sonar问题，for循环中new对象 begin
		WaPsnHiDAO waPsnHiDao = new WaPsnHiDAO();
		// end
		// int i=0;
		for (PsnappaproveBVO psnappaproveBVO : psnappaproveBVOs) {
			String pk_wa_item = psnappaproveBVO.getPk_wa_item();
			String pk_wa_grd = psnappaproveBVO.getPk_wa_grd();
			String pk_psndoc = psnappaproveBVO.getPk_psndoc();
			if (!StringUtils.isEmpty(pk_psndoc) && pk_wa_grd != null
					&& pk_wa_item != null) {
				// 得到查询适合的薪资标准表的sql
				// sqlArray[i] = new
				// WaPsnHiDAO().getCriterion(pk_wa_grd,pk_psndoc);
				sqlArrayList.add(waPsnHiDao.getCriterion(pk_wa_grd, pk_psndoc));
			}
			// i++;
		}
		String[] sqlArray = sqlArrayList.toArray(new String[0]);
		// if (sql != null) {
		// PsnappaproveBVO[] bvos = executeQueryVOs(sql,
		// PsnappaproveBVO.class);
		PsnappaproveBVO bvo = new PsnappaproveBVO();

		if (!ArrayUtils.isEmpty(sqlArray)) {
			//
			HashMap<String, String> sqlMap = new HashMap<String, String>();
			HashMap<String, PsnappaproveBVO> psnappaproveBVOMap = new HashMap<String, PsnappaproveBVO>();
			// 分别查询出适合的薪资标准表值，放入数组。
			for (int j = 0; j < sqlArray.length; j++) {
				List<PsnappaproveBVO> bvoList = new ArrayList<PsnappaproveBVO>();
				// 20150105 xiejie3 NCdp205568628
				// 定调资档案新增定调资记录，薪资项目标准工资（多级无对应级别属性），选择薪资标准类别后报错
				// 原因：sqlArray[j]没有判空
				if (!StringUtils.isEmpty(sqlArray[j])) {
					bvo = executeQueryVO(sqlArray[j], PsnappaproveBVO.class);
					if (null != bvo) {
						for (PsnappaproveBVO psnappaproveBVO : psnappaproveBVOs) {
							if (psnappaproveBVO.getPk_psndoc().equals(
									bvo.getPk_psndoc())
									&& (Integer.valueOf(0).equals(
											bvo.getAssgid()) || psnappaproveBVO
											.getAssgid()
											.equals(bvo.getAssgid()))
									&& psnappaproveBVO.getPk_wa_grd().equals(
											bvo.getPk_wa_grd())) {
								// 20151208 xiejie3
								// 为区分不同的bvo，用人员主键+任职id+薪资标准类别主键 作为key。
								psnappaproveBVOMap.put(
										psnappaproveBVO.getPk_psndoc()
												+ psnappaproveBVO.getAssgid()
												+ psnappaproveBVO
														.getPk_wa_grd(), bvo);
								sqlMap.put(psnappaproveBVO.getPk_psndoc()
										+ psnappaproveBVO.getAssgid()
										+ psnappaproveBVO.getPk_wa_grd(),
										sqlArray[j]);
								bvoList.add(bvo);
								break;
							}
						}
					}
					// PsnappaproveBVO[] bvos = getBaseDao().(sql,
					// PsnappaproveBVO.class);

					if (null == bvoList || bvoList.isEmpty()) {
						continue;
					}
					// for (PsnappaproveBVO psnappaproveBVO : psnappaproveBVOs)
					// {
					// for (PsnappaproveBVO bvo : bvos) {
					// if (psnappaproveBVO.getPk_psndoc().equals(
					// bvo.getPk_psndoc())
					// && (Integer.valueOf(0).equals(bvo.getAssgid()) ||
					// psnappaproveBVO
					// .getAssgid().equals(bvo.getAssgid()))
					// &&psnappaproveBVO.getPk_wa_grd().equals(bvo.getPk_wa_grd()))
					// {
					// // 20151208 xiejie3 为区分不同的bvo，用人员主键+任职id+薪资标准类别主键 作为key。
					// psnappaproveBVOMap.put(
					// psnappaproveBVO.getPk_psndoc()
					// +
					// psnappaproveBVO.getAssgid()+psnappaproveBVO.getPk_wa_grd(),
					// bvo);
					// break;
					// }
					// }
					// }
					HashMap<String, String> namemap = null;
					// namemap = getCrtName(psnappaproveBVOs,null);
					for (PsnappaproveBVO psnappaproveBVO : psnappaproveBVOs) {
						PsnappaproveBVO apply_psnappaproveBVO = psnappaproveBVOMap
								.get(psnappaproveBVO.getPk_psndoc()
										+ psnappaproveBVO.getAssgid()
										+ psnappaproveBVO.getPk_wa_grd());
						String sql = sqlMap.get(psnappaproveBVO.getPk_psndoc()
								+ psnappaproveBVO.getAssgid()
								+ psnappaproveBVO.getPk_wa_grd());
						if (apply_psnappaproveBVO == null) {
							continue;
						}
						psnappaproveBVO.setIsMultiSec(UFBoolean.valueOf(false));
						//
						if (sql.indexOf("wa_seclv") != -1) {
							psnappaproveBVO.setIsMultiSec(UFBoolean
									.valueOf(true));
						}
						psnappaproveBVO.setPk_wa_crt(apply_psnappaproveBVO
								.getPk_wa_crt());
						psnappaproveBVO
								.setPk_wa_prmlv_apply(apply_psnappaproveBVO
										.getPk_wa_prmlv_apply());
						psnappaproveBVO
								.setPk_wa_seclv_apply(apply_psnappaproveBVO
										.getPk_wa_seclv_apply());
						// 默认值
						psnappaproveBVO
								.setNegotiation(UFBoolean.valueOf(false));
						psnappaproveBVO.setWa_apply_money(apply_psnappaproveBVO
								.getWa_apply_money());
						psnappaproveBVO
								.setWa_crt_apply_money(apply_psnappaproveBVO
										.getWa_apply_money());
						psnappaproveBVO
								.setPk_wa_crt_apply_showname(apply_psnappaproveBVO
										.getPk_wa_crt_apply_showname());
						psnappaproveBVO.setCrt_max_value(apply_psnappaproveBVO
								.getCrt_max_value());
						psnappaproveBVO.setCrt_min_value(apply_psnappaproveBVO
								.getCrt_min_value());
					}
					psnappaproveBVOMap.clear();
					sqlMap.clear();
				}
			}
		}
		return psnappaproveBVOs;
	}

	// end

	// 20151208 xiejie3 为支持人员入职推单支持多业务流程配置，修改为上面的代码。
	/*
	 * public PsnappaproveBVO[] getDefultApplys(PsnappaproveBVO[]
	 * psnappaproveBVOs) throws BusinessException { String pk_wa_item =
	 * psnappaproveBVOs[0].getPk_wa_item(); String pk_wa_grd =
	 * psnappaproveBVOs[0].getPk_wa_grd(); String[] pk_psndocs = new
	 * String[psnappaproveBVOs.length];
	 * 
	 * int i = 0; for (PsnappaproveBVO psnappaproveBVO : psnappaproveBVOs) {
	 * pk_psndocs[i++] = psnappaproveBVO.getPk_psndoc(); }
	 * 
	 * if (!ArrayUtils.isEmpty(pk_psndocs) && pk_wa_grd != null && pk_wa_item !=
	 * null) { String sql = new WaPsnHiDAO().getCriterion(pk_wa_grd,
	 * pk_psndocs); if (sql != null) { PsnappaproveBVO[] bvos =
	 * executeQueryVOs(sql, PsnappaproveBVO.class); HashMap<String,
	 * PsnappaproveBVO> psnappaproveBVOMap = new HashMap<String,
	 * PsnappaproveBVO>(); if (ArrayUtils.isEmpty(bvos)) { return
	 * psnappaproveBVOs; } for (PsnappaproveBVO psnappaproveBVO :
	 * psnappaproveBVOs) { for (PsnappaproveBVO bvo : bvos) { if
	 * (psnappaproveBVO.getPk_psndoc().equals( bvo.getPk_psndoc()) &&
	 * (Integer.valueOf(0).equals(bvo.getAssgid()) || psnappaproveBVO
	 * .getAssgid().equals(bvo.getAssgid()))) { psnappaproveBVOMap.put(
	 * psnappaproveBVO.getPk_psndoc() + psnappaproveBVO.getAssgid(), bvo);
	 * break; } } } HashMap<String, String> namemap = null; // namemap =
	 * getCrtName(psnappaproveBVOs,null); for (PsnappaproveBVO psnappaproveBVO :
	 * psnappaproveBVOs) { PsnappaproveBVO apply_psnappaproveBVO =
	 * psnappaproveBVOMap .get(psnappaproveBVO.getPk_psndoc() +
	 * psnappaproveBVO.getAssgid()); if (apply_psnappaproveBVO == null) {
	 * continue; } psnappaproveBVO.setIsMultiSec(UFBoolean.valueOf(false)); //
	 * if (sql.indexOf("wa_seclv") != -1) {
	 * psnappaproveBVO.setIsMultiSec(UFBoolean.valueOf(true)); }
	 * psnappaproveBVO.setPk_wa_crt(apply_psnappaproveBVO .getPk_wa_crt());
	 * psnappaproveBVO.setPk_wa_prmlv_apply(apply_psnappaproveBVO
	 * .getPk_wa_prmlv_apply());
	 * psnappaproveBVO.setPk_wa_seclv_apply(apply_psnappaproveBVO
	 * .getPk_wa_seclv_apply()); // 默认值
	 * psnappaproveBVO.setNegotiation(UFBoolean.valueOf(false));
	 * psnappaproveBVO.setWa_apply_money(apply_psnappaproveBVO
	 * .getWa_apply_money());
	 * psnappaproveBVO.setWa_crt_apply_money(apply_psnappaproveBVO
	 * .getWa_apply_money()); psnappaproveBVO
	 * .setPk_wa_crt_apply_showname(apply_psnappaproveBVO
	 * .getPk_wa_crt_apply_showname()); // String showname = null; //
	 * if(psnappaproveBVO.getIsMultiSec().booleanValue()){ // showname = //
	 * namemap.get(psnappaproveBVO.getPk_wa_prmlv_apply()+psnappaproveBVO.
	 * getPk_wa_seclv_apply()); // }else{ // showname = //
	 * namemap.get(psnappaproveBVO.getPk_wa_prmlv_apply()); // } //
	 * psnappaproveBVO.setPk_wa_crt_apply_showname(showname); //
	 * psnappaproveBVO.
	 * setPk_wa_crt_apply_showname(getCrtName(apply_psnappaproveBVO
	 * .getPk_wa_prmlv_apply(),apply_psnappaproveBVO.getPk_wa_seclv_apply(), //
	 * psnappaproveBVO.getIsMultiSec().booleanValue()));
	 * psnappaproveBVO.setCrt_max_value(apply_psnappaproveBVO
	 * .getCrt_max_value());
	 * psnappaproveBVO.setCrt_min_value(apply_psnappaproveBVO
	 * .getCrt_min_value()); } psnappaproveBVOMap.clear(); } } return
	 * psnappaproveBVOs; }
	 */

	/*
	 * public PsnappaproveBVO[] getDefultApplys4Psn(PsnappaproveBVO[]
	 * psnappaproveBVOs) throws BusinessException {
	 * 
	 * for(PsnappaproveBVO psnappaproveBVO:psnappaproveBVOs){ String pk_wa_grd =
	 * psnappaproveBVO.getPk_wa_grd(); String pk_psndoc =
	 * psnappaproveBVO.getPk_psndoc(); String sql = new
	 * WaPsnHiDAO().getCriterion(pk_wa_grd, pk_psndoc); PsnappaproveBVO bvo =
	 * executeQueryVO(sql, PsnappaproveBVO.class); if(bvo==null){ continue; }
	 * psnappaproveBVO.setIsMultiSec(UFBoolean.valueOf(false)); if
	 * (sql.indexOf("wa_seclv") != -1) {
	 * psnappaproveBVO.setIsMultiSec(UFBoolean.valueOf(true)); }
	 * psnappaproveBVO.setPk_wa_crt(bvo.getPk_wa_crt());
	 * psnappaproveBVO.setPk_wa_prmlv_apply(bvo.getPk_wa_prmlv_apply());
	 * psnappaproveBVO.setPk_wa_seclv_apply(bvo.getPk_wa_seclv_apply()); // 默认值
	 * psnappaproveBVO.setNegotiation(UFBoolean.valueOf(false));
	 * psnappaproveBVO.setWa_apply_money(bvo.getWa_apply_money());
	 * psnappaproveBVO.setWa_crt_apply_money(bvo.getWa_apply_money());
	 * psnappaproveBVO
	 * .setPk_wa_crt_apply_showname(getCrtName(bvo.getPk_wa_prmlv_apply
	 * (),bvo.getPk_wa_seclv_apply(),
	 * psnappaproveBVO.getIsMultiSec().booleanValue()));
	 * psnappaproveBVO.setCrt_max_value(bvo.getCrt_max_value());
	 * psnappaproveBVO.setCrt_min_value(bvo.getCrt_min_value()); } return
	 * psnappaproveBVOs; }
	 */
	/**
	 * 根据薪资标准类别值主键 查询内容
	 * 
	 * @author xuhw on 2009-12-7
	 * @param strCrtkey
	 * @return
	 * @throws DAOException
	 */
	public WaCriterionVO queryCriterionVOByCrtKey(String strCrtkey)
			throws DAOException {

		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" select  wc.pk_wa_crt, wc.criterionvalue,wc.min_value, wc.max_value ");
		sbSql.append(" from wa_criterion wc");
		sbSql.append(" where wc.pk_wa_crt = ?");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strCrtkey);

		return executeQueryVO(sbSql.toString(), parameter, WaCriterionVO.class);
	}

	/**
	 * 更新员工个人定调级申请单子表
	 * 
	 * @param billPk
	 */
	public void updatePsnAppBByPkForUnapprove(String strPKPsnapp)
			throws DAOException {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update wa_psnappaprove_b ");
		sqlBuffer.append("   set wa_psnappaprove_b.wa_cofm_money = null , ");
		sqlBuffer.append("       wa_psnappaprove_b.pk_wa_crt_cofm = null ");
		sqlBuffer.append(" where pk_psnapp = ? ");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strPKPsnapp);
		this.getBaseDao().executeUpdate(sqlBuffer.toString(), parameter);
	}

	/**
	 * 查询员工个人定调级申请单
	 * 
	 * @author xuhw on 2009-12-24
	 * @param strPKpsnapp
	 * @return
	 * @throws DAOException
	 */
	public boolean isPsnAppBByPkForUnapprove(String strPKPsnapp)
			throws DAOException {
		String sql = " select 1 from wa_psnappaprove where wa_psnappaprove.confirmstate = -1 and wa_psnappaprove.pk_psnapp = ? ";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strPKPsnapp);
		return isValueExist(sql, parameter);
	}

	/**
	 * 删除掉审批的信息
	 * 
	 * @author xuhw on 2009-12-24
	 * @param pk_psnapp
	 * @throws DAOException
	 */
	public void updatePsnAppAprove(String strPKPsnapp) throws DAOException {
		// 如果返回到编写中则将把审批的信息也删除掉
		String sqlDate = "update wa_psnappaprove set confirmdate = null  where pk_psnapp = ?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strPKPsnapp);
		this.getBaseDao().executeUpdate(sqlDate, parameter);
	}

	/**
	 * 获得对应的期间 0--year;1-->period
	 * 
	 * @return java.lang.String[]
	 * @param usedate
	 *            java.lang.String
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public String[] getPeriod(String usedate) throws DAOException {
		String sql = " select distinct apm.accperiodmth,periodyear from bd_accperiodmonth apm,"
				+ "bd_accperiod ap where apm.pk_accperiod=ap.pk_accperiod  "
				+ " and apm.begindate <= ? and apm.enddate >= ? ";
		String[] re = new String[2];
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(new UFDate(usedate).toString());
		parameter.addParam(new UFDate(usedate).toString());
		re = (String[]) this.getBaseDao().executeQuery(sql, parameter,
				new ResultSetProcessor() {
					public Object handleResultSet(ResultSet rs)
							throws SQLException {
						String[] re = new String[2];
						if (rs.next()) {
							re[0] = rs.getString(1);
							re[1] = rs.getString(2);
						}
						return re;
					}
				});

		return re;
	}

	/**
	 * 向数据库中插入一批VO对象。
	 * 
	 * @throws SQLException
	 */
	public String[] insertArray(PsndocWadocVO[] psndocWadocs)
			throws DAOException {
		if (psndocWadocs == null || psndocWadocs.length == 0) {
			return null;
		}
		Hashtable<String, String> ht = new Hashtable<String, String>();
		PsndocWadocVO[] wadocs = null;
		// 20150825优化效率查询提到循环外
		String psnjobInSql = null;
		try {
			psnjobInSql = new InSQLCreator()
					.getInSQL(StringPiecer.getStrArrayDistinct(psndocWadocs,
							PsndocWadocVO.PK_PSNJOB));
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
		}
		PsnJobVO[] psnjobVOs = CommonUtils.retrieveByClause(PsnJobVO.class,
				"pk_psnjob in (" + psnjobInSql + ")");
		Map<String, String> psndocMap = new HashMap<String, String>();
		for (int i = 0, j = ArrayUtils.getLength(psnjobVOs); i < j; i++) {
			psndocMap.put(psnjobVOs[i].getPk_psnjob(),
					psnjobVOs[i].getPk_psndoc());
		}
		for (int i = 0; i < psndocWadocs.length; i++) {
			if (psndocWadocs[i].getRecordnum() == null) {
				psndocWadocs[i].setRecordnum(Integer.valueOf(0));
			}
			if (psndocWadocs[i].getLastflag() == null) {
				psndocWadocs[i].setLastflag(UFBoolean.TRUE);
			}
			// 20140806 shenliangc 定调资申请直批报空指针错。
			// 直接原因是psndocWadocs[i].getPk_psnjob()返回null，这个是必然的，因为传过来的psndocWadocs里面pk_psndoc就是null。
			// 同样的原因还导致定调资申请保存重复性校验出错。根本原因不清楚，这里的修改只是解决这个问题。
			if (!psndocWadocs[i].getPk_wa_item().equals(
					ht.get(psndocWadocs[i].getPk_psnjob()))) {// 保证同一批插入的数据，同一人同一项目的记录号只更新一次
				// updatePrePsnWadocFlag(psndocWadocs[i].getPk_psndoc(),
				// psndocWadocs[i].getPk_wa_item(),
				// "insertarray",
				// psndocWadocs[i].getRecordnum(),
				// new
				// Boolean(true));
				// updatePreEnddate(psndocWadocs[i]);
				// zhoumxc
				// 2014.08.16
				// 因为pk_psndoc为空，但pk_psnjob不为空，所以要根据pk_psnjob取得pk_pscdoc，否则，在维护那里查不到数据
				String pk_psndoc = "";
				if (psndocWadocs[i].getPk_psndoc() == null) {
					// String pk_psndoc = "";
					// String sql =
					// "select pk_psndoc from hi_psnjob where pk_psnjob = '"+
					// psndocWadocs[i].getPk_psnjob() + "'";
					// SQLParameter parameter = null;
					// pk_psndoc = (String)
					// this.getBaseDao().executeQuery(sql,parameter, new
					// ColumnProcessor(1));
					pk_psndoc = psndocMap.get(psndocWadocs[i].getPk_psnjob()); // 启用此行后，可去掉上面3行查询内容
					if (!StringUtils.isEmpty(pk_psndoc)) {
						psndocWadocs[i].setPk_psndoc(pk_psndoc);
						psndocWadocs[i].setAssgid(1);
					}
				}

				wadocs = (PsndocWadocVO[]) ArrayUtils.add(wadocs,
						psndocWadocs[i]);
				ht.put(psndocWadocs[i].getPk_psnjob(),
						psndocWadocs[i].getPk_wa_item());
			}
		}
		updatePrePsnWadocFlag(wadocs);
		updatePreEnddate(wadocs);
		String[] keys = this.getBaseDao().insertVOArray(psndocWadocs);
		return keys;
	}

	/**
	 * 更新旧的薪资变动档案的标志。 创建日期：(2004-6-7 19:26:30)
	 * 
	 * @param psnpk
	 *            java.lang.String
	 * @param itempk
	 *            java.lang.String
	 * @param grdpk
	 *            java.lang.String
	 * @exception java.sql.SQLException
	 *                异常说明。
	 * @throws DAOException
	 */
	private void updatePrePsnWadocFlag(PsndocWadocVO[] psndocWadocs)
			throws DAOException {
		String sql = "update hi_psndoc_wadoc set recordnum = recordnum+1, lastflag='N' where pk_psndoc = ? and pk_wa_item=?  and recordnum >= 0 and assgid =?";
		SQLParameter[] parameters = null;
		String[] sqls = null;
		for (PsndocWadocVO psndocWadocVO : psndocWadocs) {
			sqls = (String[]) ArrayUtils.add(sqls, sql);
			SQLParameter parameter = new SQLParameter();
			parameter.addParam(psndocWadocVO.getPk_psndoc());
			parameter.addParam(psndocWadocVO.getPk_wa_item());
			parameter.addParam(psndocWadocVO.getAssgid());
			parameters = (SQLParameter[]) ArrayUtils.add(parameters, parameter);
		}
		try {
			NCLocator.getInstance().lookup(IPersistenceUpdate.class)
					.executeSQLs(sqls, parameters);
			CacheProxy.fireDataUpdated("");
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			throw new DAOException(e.getMessage());
		}
	}

	/**
	 * 检查相同的组织和定调级类别下 有无重复的申请编码
	 * 
	 * @param context
	 * @param billcode
	 * @return
	 * @throws DAOException
	 */
	public boolean checkBillCodeUseable(String billcode, String strPKOrg)
			throws DAOException {

		// 编码不能重复（相同的公司和定调级类别下）
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select 1 "); // 1
		sqlB.append("  from wa_psnappaprove ");
		sqlB.append(" where wa_psnappaprove.billcode = ? ");
		sqlB.append("   and wa_psnappaprove.pk_org = ? ");

		// 存在重复的
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(billcode);
		parameter.addParam(strPKOrg);
		return isValueExist(sqlB.toString(), parameter);
	}

	/**
	 * 检查相同的组织和定调级类别下 有无重复的申请编码 修改保存时调用，与自己重复不算重复。
	 * 
	 * @param context
	 * @param billcode
	 * @return
	 * @throws DAOException
	 */
	public boolean checkBillCodeUseable(String billpk, String billcode,
			String strPKOrg) throws DAOException {

		// 编码不能重复（相同的公司和定调级类别下）
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("select 1 "); // 1
		sqlB.append("  from wa_psnappaprove ");
		sqlB.append(" where wa_psnappaprove.billcode = ? ");
		sqlB.append("   and wa_psnappaprove.pk_org = ? ");
		sqlB.append("   and wa_psnappaprove.pk_psnapp <> ? ");

		// 存在重复的
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(billcode);
		parameter.addParam(strPKOrg);
		parameter.addParam(billpk);
		return isValueExist(sqlB.toString(), parameter);
	}

	/**
	 * 用一个VO对象的属性更新数据库中的值。
	 */
	public void updateApproveDate(PsnappaproveVO psnappaprovervo)
			throws DAOException {
		String sql = "update wa_psnappaprove set confirmdate = ?, confirmstate = ?, approve_note = ?, approver = ? , modifier =?, modifiedtime=? where pk_psnapp = ?";
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(psnappaprovervo.getConfirmdate());
		parameter.addParam(psnappaprovervo.getConfirmstate());
		parameter.addParam(psnappaprovervo.getApprove_note());
		parameter.addParam(psnappaprovervo.getApprover());
		// 设置审计信息
		parameter.addParam(AuditInfoUtil.getCurrentUser());
		parameter.addParam(AuditInfoUtil.getCurrentTime());
		parameter.addParam(psnappaprovervo.getPk_psnapp());

		this.getBaseDao().executeUpdate(sql, parameter);
	}

	/**
	 * 根据是否多档和薪资标准值PK 得到标准名称
	 * 
	 * @author xuhw on 2010-1-29
	 * @param strPkCrt
	 * @param ismultsec
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, String> getCrtName(
			PsnappaproveBVO[] psnappaproveBVOs, PsndocWadocVO[] psndocWadocVOs)
			throws DAOException {
		boolean flag = psndocWadocVOs != null;
		HashMap<String, PsndocWadocVO> psndocWadocVOMap = null;
		if (flag) {
			psndocWadocVOMap = new HashMap<String, PsndocWadocVO>();
			for (PsnappaproveBVO psnappaproveBVO : psnappaproveBVOs) {
				for (PsndocWadocVO psndocWadocVO : psndocWadocVOs) {
					if (psnappaproveBVO.getPk_psndoc().equals(
							psndocWadocVO.getPk_psndoc())
							&& psnappaproveBVO.getAssgid().equals(
									psndocWadocVO.getAssgid())) {
						psndocWadocVOMap.put(psnappaproveBVO.getPk_psndoc()
								+ psnappaproveBVO.getAssgid(), psndocWadocVO);
					}
				}
			}
		}
		HashMap<String, String> namemap = null;
		String[] prmlvpks = null;
		String[] prmlvpks2 = null;
		// wc.pk_wa_prmlv = '" + strPrmlvPk + "' and wc.pk_wa_seclv = '" +
		// strSeclvPK+ "'
		for (PsnappaproveBVO psnappaproveBVO : psnappaproveBVOs) {
			String pk_wa_prmlv = null;
			String pk_wa_seclv = null;
			boolean bismultsec = false;
			if (flag) {
				PsndocWadocVO psndocWadocVO = psndocWadocVOMap
						.get(psnappaproveBVO.getPk_psndoc()
								+ psnappaproveBVO.getAssgid());
				if (psndocWadocVO == null) {
					continue;
				}
				pk_wa_prmlv = psndocWadocVO.getPk_wa_prmlv();
				pk_wa_seclv = psndocWadocVO.getPk_wa_seclv();
				bismultsec = psndocWadocVO.getIsmultsec().booleanValue();
			} else {
				pk_wa_prmlv = psnappaproveBVO.getPk_wa_prmlv_apply();
				pk_wa_seclv = psnappaproveBVO.getPk_wa_seclv_apply();
				bismultsec = psnappaproveBVO.getIsMultiSec() == null ? false
						: psnappaproveBVO.getIsMultiSec().booleanValue();
			}
			if (bismultsec) {
				// "wc.pk_wa_prmlv = '" + psndocWadocVO.getPk_wa_prmlv() +
				// "' and wc.pk_wa_seclv = '" + psndocWadocVO.getPk_wa_seclv()+
				// "'";
				if (!ArrayUtils.contains(prmlvpks, pk_wa_prmlv + pk_wa_seclv)) {
					prmlvpks = (String[]) ArrayUtils.add(prmlvpks, pk_wa_prmlv
							+ pk_wa_seclv);
				}

			} else {
				if (!ArrayUtils.contains(prmlvpks2, pk_wa_prmlv)) {
					Object[] objs = ArrayUtils.add(prmlvpks2, pk_wa_prmlv);
					if (!ArrayUtils.isEmpty(objs)) {
						prmlvpks2 = new String[objs.length];
						for (int i = 0; i < objs.length; i++) {
							prmlvpks2[i] = (String) objs[i];
						}
					}
				}
			}
		}

		StringBuffer sql = new StringBuffer();
		StringBuffer notmultsecsql = new StringBuffer();
		InSQLCreator inSQLCreator = new InSQLCreator();
		HashMap<String, String> hashResult = new HashMap<String, String>();

		try {
			sql.append(" select wp.levelname || '/' || ws.levelname as name,wc.pk_wa_prmlv||wc.pk_wa_seclv as pk");
			sql.append(" from wa_criterion wc , wa_prmlv wp , wa_seclv ws ");
			sql.append(" where wc.pk_wa_prmlv = wp.pk_wa_prmlv and wc.pk_wa_seclv = ws.pk_wa_seclv and wc.pk_wa_prmlv||wc.pk_wa_seclv in("
					+ inSQLCreator.getInSQL(prmlvpks) + ")");

			sql.append(" union select wp.levelname as name ,wc.pk_wa_prmlv as pk");
			sql.append(" from  wa_criterion wc ,  wa_prmlv wp  ");
			sql.append(" where wc.pk_wa_prmlv = wp.pk_wa_prmlv and wc.pk_wa_prmlv in("
					+ inSQLCreator.getInSQL(prmlvpks2) + ")");

			hashResult = (HashMap<String, String>) getBaseDao().executeQuery(
					sql.toString(), new ResultSetProcessor() {
						public Object handleResultSet(ResultSet rs)
								throws SQLException {
							HashMap<String, String> hash = new HashMap<String, String>();
							while (rs.next()) {
								hash.put(rs.getString(2), rs.getString(1));
							}
							return hash;
						}
					});
		} catch (BusinessException e) {
			Logger.error(e);
		}

		return hashResult;
	}

	/**
	 * 根据是否多档和薪资标准值PK 得到标准名称
	 * 
	 * @author xuhw on 2010-1-29
	 * @param strPkCrt
	 * @param ismultsec
	 * @return
	 * @throws DAOException
	 */
	// public String getCrtName(String strPrmlvPk,String strSeclvPK, boolean
	// ismultsec) throws DAOException
	// {
	// StringBuffer sql = new StringBuffer();
	//
	// if (ismultsec)
	// {
	// sql.append(" select wp.levelname || '/' || ws.levelname ");
	// sql.append(" from wa_criterion wc , wa_prmlv wp , wa_seclv ws ");
	// sql.append(" where wc.pk_wa_prmlv = wp.pk_wa_prmlv and wc.pk_wa_seclv = ws.pk_wa_seclv and wc.pk_wa_prmlv = '"
	// + strPrmlvPk + "' and wc.pk_wa_seclv = '" + strSeclvPK+ "'");
	// }
	// else
	// {
	// sql.append(" select wp.levelname  ");
	// sql.append(" from  wa_criterion wc ,  wa_prmlv wp  ");
	// sql.append(" where wc.pk_wa_prmlv = wp.pk_wa_prmlv and wc.pk_wa_prmlv = '"
	// + strPrmlvPk + "' ");
	// }
	// return (String) getBaseDao().executeQuery(sql.toString(), new
	// ResultSetProcessor()
	// {
	// public Object handleResultSet(ResultSet rs) throws SQLException
	// {
	// String result = null;
	// if (rs.next())
	// {
	// result = rs.getString(1);
	// }
	// return result;
	// }
	// });
	//
	// }

	/**
	 * 此处插入方法描述。
	 * 
	 * @param strWhere
	 *            java.lang.String
	 * @param strTable
	 *            java.lang.String
	 * @throws DAOException
	 */
	public PsnappaproveVO[] queryByCon(String strWhere, String strTable)
			throws DAOException {
		String sql = " select distinct wa_psnappaprove.pk_psnapp,billcode,billname,"
				+ "applydate,confirmdate,usedate,wa_psnappaprove.confirmstate,"
				+ "applypsn,givencount,plancount,factcount,approvetype ,"
				+ "pk_changecause,vbasefile, pk_busitype, wa_psnappaprove.pk_org ";
		sql += strTable + strWhere;
		return executeQueryVOs(sql, PsnappaproveVO.class);
	}

	/**
	 * 根据crtpk查询 是否多档，是否宽带，以及标准值<BR>
	 * 查询old原来的薪资信息时候使用<BR>
	 */
	public String[] queryVOByCrtPK(String strCrtPK) throws DAOException {
		StringBuffer sqlB = new StringBuffer();
		sqlB.append("    select crt.criterionvalue , ");
		sqlB.append("         grd.ismultsec  ");
		sqlB.append("  from wa_criterion crt ,wa_grade grd ");
		sqlB.append("  where   ");
		sqlB.append("  crt.pk_wa_grd = grd.pk_wa_grd ");
		sqlB.append("  and crt.pk_wa_crt = ? ");

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(strCrtPK);
		return (String[]) this.getBaseDao().executeQuery(sqlB.toString(),
				parameter, new ResultSetProcessor() {
					public Object handleResultSet(ResultSet rs)
							throws SQLException {
						String[] re = new String[2];
						if (rs.next()) {
							re[0] = rs.getString(1);
							re[1] = rs.getString(2);
						}
						return re;
					}
				});
	}

	/**
	 * 当岗位和部门发生合并和撤销时触发的时间，判断定调资信息中是否有相关人员
	 * 
	 * @author xuhw on 2010-7-2
	 * @param strCondition
	 * @return
	 * @throws DAOException
	 */
	public SuperVO[] queryAdjustInfoWhenDeptOrPostChange(String strCondition)
			throws DAOException {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" select wa_psnappaprove_b.* from wa_psnappaprove_b inner join wa_psnappaprove on wa_psnappaprove_b.pk_psnapp = wa_psnappaprove.pk_psnapp ");
		sbSql.append(" where ");
		if (StringUtils.isEmpty(strCondition)) {
			return null;
		}
		sbSql.append(strCondition);

		return executeQueryVOs(sbSql.toString(), PsnappaproveBVO.class);
	}

	/**
	 * 审批金额和申请金额的汇总
	 * 
	 * @param psnapparovervos
	 * @return
	 * @throws DAOException
	 */
	public AggPsnappaproveVO[] querySumApplyAndSumConfimMoney(
			AggPsnappaproveVO[] aggPsnappaproveVOs) throws DAOException {

		if (ArrayUtils.isEmpty(aggPsnappaproveVOs)) {
			return new AggPsnappaproveVO[0];
		}

		// 2016-12-07 zhousze 薪资加密：这里处理定调资申请界面数据解密 begin
		StringBuffer sql = new StringBuffer();
		sql.append("select * from wa_psnappaprove_b a inner join wa_psnappaprove b on a.pk_psnapp = b.pk_psnapp ");
		sql.append("where a.approved = 'Y'");
		PsnappaproveBVO[] bvos = new BaseDAOManager().executeQueryVOs(
				sql.toString(), PsnappaproveBVO.class);
		for (PsnappaproveBVO vo : bvos) {
			vo.setWa_apply_money(vo.getWa_apply_money() == null ? null
					: new UFDouble(SalaryDecryptUtil.decrypt(vo
							.getWa_apply_money().toDouble())));
			vo.setWa_cofm_money(vo.getWa_cofm_money() == null ? null
					: new UFDouble(SalaryDecryptUtil.decrypt(vo
							.getWa_cofm_money().toDouble())));
		}
		getBaseDao().updateVOArray(bvos);
		// end

		UFDouble defaultValue = new UFDouble(0);
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" select");
		sbSql.append(" sum(wa_psnappaprove_b.wa_apply_money) as sum_apply_money_dbl , sum(wa_psnappaprove_b.wa_cofm_money) as sum_confim_money_dbl , ");
		sbSql.append(" wa_psnappaprove.pk_psnapp ");
		sbSql.append("  from");
		sbSql.append("   wa_psnappaprove_b ");
		sbSql.append("      inner join wa_psnappaprove ");
		sbSql.append("     on wa_psnappaprove.pk_psnapp = wa_psnappaprove_b.pk_psnapp ");
		sbSql.append(" where wa_psnappaprove_b.approved = 'Y' ");
		sbSql.append("  group by");
		sbSql.append("      wa_psnappaprove.pk_psnapp");

		PsnappaproveVO[] queryPsnappaproveVOs = new BaseDAOManager()
				.executeQueryVOs(sbSql.toString(), PsnappaproveVO.class);

		for (AggPsnappaproveVO aggPsnappaproveVO : aggPsnappaproveVOs) {
			PsnappaproveVO psnapparovervo = (PsnappaproveVO) aggPsnappaproveVO
					.getParentVO();
			psnapparovervo.setSum_confim_money(defaultValue);
			if (!ArrayUtils.isEmpty(queryPsnappaproveVOs)) {
				for (PsnappaproveVO queryPsnappaproveVO : queryPsnappaproveVOs) {
					if (psnapparovervo.getPk_psnapp().equals(
							queryPsnappaproveVO.getPk_psnapp())) {
						UFDouble ufdSumconfim = queryPsnappaproveVO
								.getSum_confim_money_dbl();
						// 2015-11-20 zhousze 现在修改为不管是否审批通过，都显示汇总审批金额 begin
						// 只有审批通过的单据，显示审批金额
						// if (psnapparovervo.getConfirmstate() ==
						// IBillStatus.CHECKPASS) {
						psnapparovervo
								.setSum_confim_money(ufdSumconfim == null ? defaultValue
										: ufdSumconfim);
						// } else {
						// psnapparovervo.setSum_confim_money(defaultValue);
						// }
						// end

						break;
					}
				}
			}
		}

		sbSql = new StringBuffer();
		sbSql.append(" select");
		sbSql.append(" sum(wa_psnappaprove_b.wa_apply_money) as sum_apply_money_dbl , sum(wa_psnappaprove_b.wa_cofm_money) as sum_confim_money_dbl , ");
		sbSql.append(" wa_psnappaprove.pk_psnapp ");
		sbSql.append("  from");
		sbSql.append("   wa_psnappaprove_b ");
		sbSql.append("      inner join wa_psnappaprove ");
		sbSql.append("     on wa_psnappaprove.pk_psnapp = wa_psnappaprove_b.pk_psnapp ");

		sbSql.append("  group by");
		sbSql.append("      wa_psnappaprove.pk_psnapp");

		queryPsnappaproveVOs = new BaseDAOManager().executeQueryVOs(
				sbSql.toString(), PsnappaproveVO.class);
		for (AggPsnappaproveVO aggPsnappaproveVO : aggPsnappaproveVOs) {
			PsnappaproveVO psnapparovervo = (PsnappaproveVO) aggPsnappaproveVO
					.getParentVO();
			psnapparovervo.setSum_apply_money(defaultValue);
			if (!ArrayUtils.isEmpty(queryPsnappaproveVOs)) {
				for (PsnappaproveVO queryPsnappaproveVO : queryPsnappaproveVOs) {
					if (psnapparovervo.getPk_psnapp().equals(
							queryPsnappaproveVO.getPk_psnapp())) {
						UFDouble ufdSumApply = queryPsnappaproveVO
								.getSum_apply_money_dbl();
						psnapparovervo
								.setSum_apply_money(ufdSumApply == null ? defaultValue
										: ufdSumApply);
						break;
					}
				}
			}
		}

		// 2016-12-07 zhousze 薪资加密：这里处理解密后数据加密到数据库中 begin
		for (PsnappaproveBVO vo : bvos) {
			vo.setWa_apply_money(vo.getWa_apply_money() == null ? null
					: new UFDouble(SalaryEncryptionUtil.encryption(vo
							.getWa_apply_money().toDouble())));
			vo.setWa_cofm_money(vo.getWa_cofm_money() == null ? null
					: new UFDouble(SalaryEncryptionUtil.encryption(vo
							.getWa_cofm_money().toDouble())));
		}
		getBaseDao().updateVOArray(bvos);
		// end

		// 2016-12-07 zhousze 薪资加密：这里处理查询出来的子表数据，数据解密 begin
		for (AggPsnappaproveVO aggVO : aggPsnappaproveVOs) {
			PsnappaproveBVO[] bVOs = (PsnappaproveBVO[]) aggVO.getChildrenVO();
			for (PsnappaproveBVO bVO : bVOs) {
				bVO.setWa_old_money(bVO.getWa_old_money() == null ? null
						: new UFDouble(SalaryDecryptUtil.decrypt(bVO
								.getWa_old_money().toDouble())));
				bVO.setWa_cofm_money(bVO.getWa_cofm_money() == null ? null
						: new UFDouble(SalaryDecryptUtil.decrypt(bVO
								.getWa_cofm_money().toDouble())));
				bVO.setWa_apply_money(bVO.getWa_apply_money() == null ? null
						: new UFDouble(SalaryDecryptUtil.decrypt(bVO
								.getWa_apply_money().toDouble())));
				bVO.setWa_crt_apply_money(bVO.getWa_crt_apply_money() == null ? null
						: new UFDouble(SalaryDecryptUtil.decrypt(bVO
								.getWa_crt_apply_money().toDouble())));
				bVO.setWa_crt_cofm_money(bVO.getWa_crt_cofm_money() == null ? null
						: new UFDouble(SalaryDecryptUtil.decrypt(bVO
								.getWa_crt_cofm_money().toDouble())));
				bVO.setWa_crt_old_money(bVO.getWa_crt_old_money() == null ? null
						: new UFDouble(SalaryDecryptUtil.decrypt(bVO
								.getWa_crt_old_money().toDouble())));
			}
		}
		// end

		return aggPsnappaproveVOs;
	}

	/**
	 * 上条定调资信息截止日期为空,定调资信息的开始日期前一天回填给上条定调资信息截止日期
	 * 
	 * @param psnpk
	 * @param itempk
	 * @param grdpk
	 * @throws DAOException
	 */
	private void updatePreEnddate(PsndocWadocVO[] psndocWadocs)
			throws DAOException {
		String sql = "update  hi_psndoc_wadoc set enddate = ? where pk_psndoc = ? and pk_wa_item=? and  recordnum = 1 and  isnull(enddate,'~')='~' and assgid =? ";
		SQLParameter[] parameters = null;
		String[] sqls = null;
		for (PsndocWadocVO psndocWadocVO : psndocWadocs) {
			sqls = (String[]) ArrayUtils.add(sqls, sql);
			SQLParameter parameter = new SQLParameter();
			UFLiteralDate begindate = psndocWadocVO.getBegindate();
			UFLiteralDate enddate = begindate.getDateBefore(1);
			parameter.addParam(enddate.toString());
			parameter.addParam(psndocWadocVO.getPk_psndoc());
			parameter.addParam(psndocWadocVO.getPk_wa_item());
			parameter.addParam(psndocWadocVO.getAssgid());
			parameters = (SQLParameter[]) ArrayUtils.add(parameters, parameter);
		}
		try {
			NCLocator.getInstance().lookup(IPersistenceUpdate.class)
					.executeSQLs(sqls, parameters);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			throw new DAOException(e.getMessage());
		}
	}

	/**
	 * 校验新增定调资信息的起始日期：新的起始日期晚于前一条的截止日期
	 * 
	 * @param psnpk
	 * @param itempk
	 * @param grdpk
	 * @throws DAOException
	 */
	public Boolean validateUsedate(PsnappaproveBVO psnappaproveBVO)
			throws DAOException {
		String psnpk = psnappaproveBVO.getPk_psndoc();
		Integer assgid = psnappaproveBVO.getAssgid();
		String itempk = psnappaproveBVO.getPk_wa_item();
		UFLiteralDate usedate = psnappaproveBVO.getUsedate();
		String sql = "";

		sql = "select 1 from hi_psndoc_wadoc  where pk_psndoc = ? and assgid = ? and pk_wa_item=? and  (recordnum = 0 and (enddate>='"
				+ usedate.toString()
				+ "' or begindate>='"
				+ usedate.toString()
				+ "'))";

		SQLParameter parameter = new SQLParameter();
		parameter.addParam(psnpk);
		parameter.addParam(assgid);
		parameter.addParam(itempk);
		return (Boolean) this.getBaseDao().executeQuery(sql, parameter,
				new BooleanProcessor());

	}

	/**
	 * 处理数值型的科学计数法
	 * 
	 * @author xuhw on 2010-8-3
	 * @param strDigt
	 * @return
	 */
	private String parseDouble(String strDigt) {
		if (strDigt.indexOf("E") == -1) {
			return strDigt;
		}
		int pos = strDigt.indexOf("E");
		int dotPos = strDigt.indexOf(".");
		String betweenValue = strDigt.substring(dotPos + 1, pos);
		String temp = "";
		int power = Integer.parseInt(strDigt.substring(pos + 1));
		if (power > 0) {
			StringBuilder sbd = new StringBuilder();
			sbd.append(strDigt.substring(0, dotPos));
			for (int index = 0; index < power; index++) {
				if (index < betweenValue.length()) {
					sbd.append(betweenValue.charAt(index));
				} else {
					sbd.append("0");
				}
			}

			if (power < betweenValue.length()) {
				temp = betweenValue.substring(power);
				sbd.append((new StringBuilder()).append(".").append(temp)
						.toString());
			}
			return sbd.toString();
		} else {
			return strDigt;
		}
	}
}
