package nc.impl.wa.payfile;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import nc.bs.bd.baseservice.busilog.BDBusiLogUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.bs.uif2.validation.Validator;
import nc.hr.frame.persistence.IValidatorFactory;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.wa.log.WaBusilogUtil;
import nc.impl.wa.category.WaClassDAO;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.managescope.ManagescopeFacade;
import nc.itf.hr.trn.IhrtrnQBS;
import nc.itf.hr.wa.IClassItemManageService;
import nc.itf.hr.wa.IClassItemQueryService;
import nc.itf.hr.wa.IPayfileManageService;
import nc.itf.hr.wa.IPayfileQueryService;
import nc.itf.hr.wa.IWaClass;
import nc.itf.hr.wa.IWaPub;
import nc.itf.hr.wa.PayfileDspUtil;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.vo.hi.psndoc.Attribute;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.pub.CommonValue;
import nc.vo.hr.combinesort.SortVO;
import nc.vo.hr.combinesort.SortconVO;
import nc.vo.hr.comp.trn.PsnTrnVO;
import nc.vo.hr.managescope.ManagescopeBusiregionEnum;
import nc.vo.hr.pub.FormatVO;
import nc.vo.hr.tools.pub.Pair;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.logging.Debug;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.util.BDPKLockUtil;
import nc.vo.util.SqlWhereUtil;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.classpower.ClassPowerUtil;
import nc.vo.wa.func.WherePartUtil;
import nc.vo.wa.grade.WaPsnhiVO;
import nc.vo.wa.payfile.ItemsVO;
import nc.vo.wa.payfile.PayfileConstant;
import nc.vo.wa.payfile.PayfileVO;
import nc.vo.wa.payfile.PsnChangeHelper;
import nc.vo.wa.payfile.WaDataDspVO;
import nc.vo.wa.payfile.WaPayfileDspVO;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.pub.HRWAMetadataConstants;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaLoginVOHelper;
import nc.vo.wa.pub.WaState;
import nc.vo.wabm.util.HRWADateConvertor;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

/**
 * 薪资档案接口实现类
 * 
 * @author: zhoucx
 * @date: 2009-11-27 上午09:12:39
 * @since: eHR V6.0
 * @走查人:
 * @走查日期:
 * @修改人:
 * @修改日期:
 */
public class PayfileServiceImpl<T> implements IPayfileManageService<T>, IPayfileQueryService {

	private final String DOC_NAME = PayfileConstant.DOC_NAME;
	private final String orderCondtion = "order by clerkcode";// PayfileConstant.ORDER_CONDTION;
	private BaseDAO baseDao;

	private SimpleDocServiceTemplate serviceTemplate;

	private SimpleDocServiceTemplate getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new SimpleDocServiceTemplate(DOC_NAME);
			serviceTemplate.setValidatorFactory(new PayfileValidatorFactory());
		}
		return serviceTemplate;
	}

	private BaseDAO getBaseDao() {
		if (baseDao == null) {
			baseDao = new BaseDAO();
		}
		return baseDao;
	}

	/**
	 * 根据条件查询档案VO
	 * 
	 * @author liangxr on 2010-4-22
	 * @see nc.itf.hr.wa.IPayfileQueryService#queryPayfileVOByCondition(nc.vo.wa.pub.WaLoginContext,
	 *      java.lang.String)
	 */
	@Override
	public PayfileVO[] queryPayfileVOByCondition(WaLoginContext context, String condition, String orderSQL)
			throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		if (orderSQL != null && !"".equals(orderSQL)) {
			orderSQL = " order by " + orderSQL;
		} else {
			orderSQL = "order by org_dept.code,hi_psnjob.clerkcode";
		}

		return dao.queryTransferPayfileVO(context, condition, orderSQL);

	}

	/**
	 * 专门为分页提供的
	 */
	@Override
	public PayfileVO[] queryPayfileVOByPKS(String[] pks) throws BusinessException {
		if (ArrayUtils.isEmpty(pks)) {
			return new PayfileVO[0];
		}
		InSQLCreator inSQLCreator = new InSQLCreator();
		try {
			PayfileDAO dao = new PayfileDAO();
			String insql = inSQLCreator.getInSQL(pks);
			PayfileVO[] payfileVOArrays = null;
			String conditon = " wa_data.pk_wa_data in (" + insql + ") ";

			if (pks.length < inSQLCreator.getCount()) {
				payfileVOArrays = dao.queryPayfileVOByPKS(conditon);
			} else {
				// String tableNmae =insql.substring(insql.indexOf("from ")-5);
				String condition = "  inner join(" + insql + " ) tmp_table on wa_data.pk_wa_data=tmp_table.in_pk ";
				StringBuffer sqlBuffer = new StringBuffer(dao.getPayfileSelectField()[0] + condition
						+ dao.getPayfileSelectField()[1]);
				// sqlBuffer.append(WherePartUtil.addWhereKeyWord2Condition(condition));
				payfileVOArrays = dao.executeQueryVOs(sqlBuffer.toString(), PayfileVO.class);
			}

			// autor erl 2011-8-19 09:09 按传进来的主键的顺序排序
			List<PayfileVO> payfileVOList = new ArrayList<PayfileVO>();
			Map<String, PayfileVO> payfileVOMap = new HashMap<String, PayfileVO>();
			for (PayfileVO payfileVO : payfileVOArrays) {
				payfileVOMap.put(payfileVO.getPk_wa_data(), payfileVO);
			}
			for (String strPK : pks) {
				payfileVOList.add(payfileVOMap.get(strPK));
			}

			return payfileVOList.toArray(new PayfileVO[0]);
		} finally {
			inSQLCreator.clear();
		}

	}

	@Override
	public String[] queryPKSByCondition(WaLoginContext context, String condition, String orderSQL)
			throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		if (!StringUtils.isBlank(orderSQL)) {
			orderSQL = " order by " + orderSQL;
		} else {
			// 如果没有排序字段 先到数据库中查询有没有当前用户的排序设置 by wangqim
			SortVO sortVOs[] = null;
			SortconVO sortconVOs[] = null;
			String strCondition = " func_code='" + context.getNodeCode() + "'"
					+ " and group_code= 'TableCode' and ((pk_corp='" + PubEnv.getPk_group() + "' and pk_user='"
					+ PubEnv.getPk_user() + "') or pk_corp ='@@@@') order by pk_corp";

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
				orderSQL = getOrderby(vectSortField);
			}
			orderSQL = StringUtils.isBlank(orderSQL) ? "order by org_dept.code,hi_psnjob.clerkcode" : " order by "
					+ orderSQL;
			;
		}
		WaBusilogUtil.writePayfileQueryBusiLog(context);
		return dao.queryPKSByCondition(context, condition, orderSQL);

	}

	// guoqt
	public static String getOrderby(Vector<Attribute> vectSortField) {
		if (vectSortField == null || vectSortField.size() == 0) {
			return "";
		}
		String strOrderBy = "";
		for (Attribute attr : vectSortField) {
			String strFullCode = attr.getAttribute().getValue();
			strOrderBy = strOrderBy + "," + strFullCode + (attr.isAscend() ? "" : " desc");
		}
		return strOrderBy.length() > 0 ? strOrderBy.substring(1) : "";
	}

	/**
	 * 查询当前薪资档案包含的未审核人员（包含人员姓名，部门名称等信息）
	 * 
	 * @author liangxr on 2010-1-14
	 * @see nc.itf.hr.wa.IPayfileQueryService#queryTransferPayfileVO(nc.vo.wa.pub.WaLoginContext,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public PayfileVO[] queryTransferPayfileVO(WaLoginContext loginContext, String condition) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		if (condition == null) {
			condition = " checkflag='N'";
		} else {
			condition += " and checkflag='N'";
		}
		return dao.queryTransferPayfileVO(loginContext, condition, orderCondtion);
	}

	/**
	 * 批量修改查询时，同时查询不能修改的记录（在其他子类别中存在）
	 * 
	 * @param loginContext
	 * @param condition
	 * @param orderCondtion
	 * @return
	 * @throws DAOException
	 */
	@Override
	public PayfileVO[] queryCannotEditPsn(WaLoginContext loginContext, String condition) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		return dao.queryCannotEditPsn(loginContext, condition + " and checkflag='N'");
	}

	@Override
	public void delete(PayfileVO vo) throws BusinessException {
		BDPKLockUtil.lockString(vo.getPk_wa_class());
		// 删除前先删除相关表信息wa_redata,wa_psntax,wa_datas
		PayfileDAO dao = new PayfileDAO();
		dao.deleteRelationTable(vo);

		getServiceTemplate().delete(vo);

		new BDBusiLogUtil(HRWAMetadataConstants.WA_FILE_MDID).writeBusiLog(HRWAMetadataConstants.DELETE, "", vo);
		// 如果方案为离职方案且删除后没有人员记录，则将离职方案同时删除
		if (dao.isLeaveClass(vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod())
				&& !dao.existPsnInClass(vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod())) {
			// 删除期间状态
			deletePeriodState(vo.getPk_wa_class());

			// 删除相关项目
			// deleteClassitem(vo.getPk_wa_class());
			// 20160113 shenliangc NCdp205572656
			// 离职结薪节点删除所有人员数据后，发放项目汇总下还有被删除的离职结薪次数下的新增项目
			IClassItemQueryService classitemQueryService = NCLocator.getInstance().lookup(IClassItemQueryService.class);
			WaClassItemVO[] vos = classitemQueryService.queryAllClassItems(vo.getPk_wa_class());

			IClassItemManageService classitemManageService = NCLocator.getInstance().lookup(
					IClassItemManageService.class);
			classitemManageService.deleteWaClassItemVOs(vos);

			// 删除关系表
			deleteRelation(vo.getPk_wa_class());
			// 删除发放次数对应的薪资类别
			deleteTimesClass(vo.getPk_wa_class());
			// 如果所有子方案都发放完，则修改父方案状态
			WaClassDAO waclassdao = new WaClassDAO();
			WaClassVO waclassvo = new WaClassVO();
			waclassvo.setPk_wa_class(vo.getPk_prnt_class());
			waclassvo.setCyear(vo.getCyear());
			waclassvo.setCperiod(vo.getCperiod());
			if (waclassdao.isChildPayoff(waclassvo))
				dao.updateParentClassPayOff(vo.getPk_prnt_class(), vo.getCyear(), vo.getCperiod(), "Y");
			dao.updateLeaveFlag(vo.getPk_prnt_class(), vo.getCyear(), vo.getCperiod(), false);
		} else {
			// 判断如果删除后所有记录都已计算的话，那更新计算状态
			updatePeriodstateCalflag(vo);
			// 判断如果删除后所有记录都已审核，则更甚新审核状态
			updatePeriodstateCheckflag(vo);
		}

		// 如果是多次发放。判断其他子方案中是否有该人，如果没有，则从汇总方案中删除
		if (!vo.getPk_wa_class().equals(vo.getPk_prnt_class()) && !existInSubClass(vo)) {// 多次发放且其他
																							// 子方案不存在该人员
			dao.delFromPrntClass(vo);
		}
	}

	/**
	 * 删除期间状态
	 * 
	 * @param pk_wa_class
	 * @throws BusinessException
	 */
	private void deletePeriodState(String pk_wa_class) throws BusinessException {
		// 先删除原有的，再插入新的。因为类别主键在整个数据库中是唯一的。所以直接按照薪资方案主键进行删除
		try {
			PersistenceManager sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate(" delete from  wa_periodstate   where pk_wa_class= '" + pk_wa_class + "'");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass", "060130waclass0080")/*
																								 * @
																								 * res
																								 * "薪资方案的期间状态删除失败"
																								 */);
		}

	}

	/**
	 * 删除发放项目
	 * 
	 * @param pk_wa_class
	 * @throws BusinessException
	 */
	private void deleteClassitem(String pk_wa_class) throws BusinessException {
		// 先删除原有的，再插入新的。因为类别主键在整个数据库中是唯一的。所以直接按照薪资方案主键进行删除
		try {
			PersistenceManager sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate(" delete from  wa_classitem   where pk_wa_class= '" + pk_wa_class + "'");
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass", "060130waclass0083")/*
																								 * @
																								 * res
																								 * "薪资方案的发放项目删除失败"
																								 */);
		}

	}

	/**
	 * 删除关系表
	 * 
	 * @param pk_childclass
	 * @throws BusinessException
	 */
	private void deleteRelation(String pk_childclass) throws BusinessException {
		try {
			PersistenceManager sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate("  delete from wa_inludeclass where pk_childclass = '" + pk_childclass + "'");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass", "060130waclass0072")/*
																								 * @
																								 * res
																								 * "删除父子方案关系表失败"
																								 */);
		}
	}

	/**
	 * 删除方案
	 * 
	 * @param pk_childclass
	 * @throws BusinessException
	 */
	private void deleteTimesClass(String pk_childclass) throws BusinessException {
		try {
			PersistenceManager sessionManager = PersistenceManager.getInstance();
			JdbcSession session = sessionManager.getJdbcSession();
			session.executeUpdate(" delete from wa_waclass where pk_wa_class = '" + pk_childclass + "' ");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(ResHelper.getString("60130waclass", "060130waclass0077")/*
																								 * @
																								 * res
																								 * "删除发放次数对应的薪资类别失败"
																								 */);
		}
	}

	@Override
	public PayfileVO insert(PayfileVO vo) throws BusinessException {
		BDPKLockUtil.lockString(vo.getPk_wa_class());
		/*
		 * 在员工信息维护节点删除了薪资档案中所选的工作 记录后，在插入保存时应该加是否删除校验 缺陷ID：NCdp205490803
		 * 2013-11-05
		 */
		String pk_psnjob = vo.getPk_psnjob();
		// 根据pk_psnjob查询工作记录

		PsnJobVO[] psnjobVOs = NCLocator.getInstance().lookup(IPsndocQryService.class)
				.queryPsnjobByPKs(new String[] { pk_psnjob });
		if (ArrayUtils.isEmpty(psnjobVOs)) {
			throw new BusinessException(ResHelper.getString("60130payfile", "060130payfile0362")/*
																								 * @
																								 * res
																								 * "员工工作记录已被删除，保存失败！"
																								 */);
		}

		// ssx added on 2020-10-30
		// 設置補充保費計算方式默認值
		if (vo.getExnhitype() == null || vo.getExnhitype() == 0) {
			vo.setExnhitype(PayfileDspUtil.getDefaultExNhiType(vo.getPk_org(), vo.getPk_wa_class(), vo.getPk_psnorg()));
		}
		//

		PayfileVO resultvo = getServiceTemplate().insert(vo);

		// 如果新增人员是停发状态，则不更新该方案的计算状态
		if (UFBoolean.FALSE.equals(vo.getStopflag())) {
			updatePeriodstate(vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod(), UFBoolean.FALSE);
		}
		PayfileDAO dao = new PayfileDAO();
		PayfileVO[] updateVos = new PayfileVO[1];

		// 如果是多次发放。判断汇总方案中是否有该人，如果没有，则添加到汇总方案
		if (vo.getPk_prnt_class() != null && !vo.getPk_wa_class().equals(vo.getPk_prnt_class())
				&& !(new PayfileDAO()).existInPrntClass(vo)) {// 多次发放
			vo.setPk_wa_class(vo.getPk_prnt_class());
			vo.setPk_wa_data(null);

			updateVos[0] = getServiceTemplate().insert(vo);
			dao.updateAccount(updateVos);
		}
		updateVos[0] = resultvo;
		dao.updateAccount(updateVos);
		return resultvo;
	}

	/**
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	private PayfileVO singleUpdate(PayfileVO vo) throws BusinessException {
		vo.setCaculateflag(UFBoolean.FALSE);
		PayfileDAO dao = new PayfileDAO();
		// 多次发放，其他子方案中不存在该人时，更新父方案记录
		if (vo.getPk_prnt_class() != null && !vo.getPk_wa_class().equals(vo.getPk_prnt_class()) && !existInSubClass(vo)) {
			PayfileVO prntvo = dao.getPsnInPrntClass(vo.getPk_prnt_class(), vo.getCyear(), vo.getCperiod(),
					vo.getPk_psnjob());
			prntvo.setIsndebuct(vo.getIsndebuct());// 费用扣除额
			prntvo.setTaxtableid(vo.getTaxtableid());// 税率表
			prntvo.setIsderate(vo.getIsderate());// 是否减免税
			prntvo.setDerateptg(vo.getDerateptg());// 减税比例
			prntvo.setTaxtype(vo.getTaxtype());// 扣税方式
			prntvo.setWorkorg(vo.getWorkorg());
			prntvo.setWorkorgvid(vo.getWorkorgvid());
			prntvo.setWorkdept(vo.getWorkdept());
			prntvo.setWorkdeptvid(vo.getWorkdeptvid());
			prntvo.setPk_financeorg(vo.getPk_financeorg());
			prntvo.setPk_financedept(vo.getPk_financedept());
			prntvo.setFiporgvid(vo.getFiporgvid());
			prntvo.setFipdeptvid(vo.getFipdeptvid());
			prntvo.setPk_liabilityorg(vo.getPk_liabilityorg());
			prntvo.setPk_liabilitydept(vo.getPk_liabilitydept());

			prntvo.setLibdeptvid(vo.getLibdeptvid());
			prntvo.setFipendflag(vo.getFipendflag());
			getServiceTemplate().update(prntvo, true);
		}
		// guoqt
		if (vo.getPk_prnt_class() != null && !vo.getPk_wa_class().equals(vo.getPk_prnt_class())
				&& !existInSubClassStopFlag(vo)) {
			PayfileVO prntvo = dao.getPsnInPrntClassAll(vo.getPk_prnt_class(), vo.getCyear(), vo.getCperiod(),
					vo.getPk_psnjob());
			prntvo.setStopflag(vo.getStopflag());
			getServiceTemplate().update(prntvo, true);
		}
		return getServiceTemplate().update(vo, true);
	}

	/**
	 * // 批改
	 */
	@Override
	public void update(Object obj, PayfileVO[] addTotal) throws BusinessException {
		PayfileVO[] objs = (PayfileVO[]) obj;
		getBaseDao().updateVOArray(objs);
		// 多次发放批改，对于其他子方案中不存在的记录，一并修改汇总方案
		if (addTotal != null) {
			PayfileDAO dao = new PayfileDAO();
			String[] pk_psndocs = new String[addTotal.length];
			for (int i = 0; i < addTotal.length; i++) {
				pk_psndocs[i] = addTotal[i].getPk_psndoc();
			}
			dao.updateTaxset2Total(null, addTotal[0].getPk_prnt_class(), addTotal[0].getPk_wa_class(),
					addTotal[0].getCyear(), addTotal[0].getCperiod(), addTotal[0].getStopflag(), pk_psndocs);
		}
		// 判断如果修改后所有记录都已计算的话，那更新计算状态（只更新了停发状态的情况下可能存在）
		updatePeriodstateCalflag(objs[0]);
		// 判断如果修改后所有记录都已审核的话，更新审核状态
		updatePeriodstateCheckflag(objs[0]);
	}

	/**
	 * 修改
	 */
	@Override
	public PayfileVO update(Object obj) throws BusinessException {
		PayfileVO vo = (PayfileVO) obj;
		BDPKLockUtil.lockString(vo.getPk_wa_class());
		PayfileVO resultvo = singleUpdate(vo);
		// 判断如果修改后所有记录都已计算的话，那更新计算状态（只更新了停发状态的情况下可能存在）
		updatePeriodstateCalflag(vo);
		// 判断如果修改后所有记录都已审核的话，更新审核状态
		updatePeriodstateCheckflag(vo);
		return resultvo;

	}

	/**
	 * 判断如果修改后所有记录都已计算的话，那更新计算状态 （增删改都存在该情况 ——停发人员相关）
	 * 
	 * @author liangxr on 2010-7-6
	 * @param vo
	 * @throws BusinessException
	 * @throws
	 */
	private void updatePeriodstateCalflag(PayfileVO vo) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		if (dao.isAllCaculated(vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod(), null)) {
			updatePeriodstate(vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod(), UFBoolean.TRUE);
		} else {
			updatePeriodstate(vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod(), UFBoolean.FALSE);
		}
	}

	/**
	 * //判断如果修改后所有记录都已审核的话，更新审核状态
	 * 
	 * @author liangxr on 2010-7-6
	 * @param vo
	 * @throws BusinessException
	 */
	private void updatePeriodstateCheckflag(PayfileVO vo) throws BusinessException {
		if (allDataHasChecked(vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod())) {
			updatePeriodCheck(vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod());
		}
	}

	@Override
	public PayfileVO[] queryBatchAddPayfileVO(WaLoginContext loginContext, String condition, int type)
			throws BusinessException {
		// 获取业务委托sql
		String msSql = ManagescopeFacade.queryPsnjobPksSQLByHrorgAndBusiregion(loginContext.getPk_org(),
				ManagescopeBusiregionEnum.salary, true);
		PayfileDAO dao = new PayfileDAO();
		PayfileVO[] payfileVOs = dao.queryBatchAddPayfileVO(loginContext, condition, orderCondtion, msSql, type);
		return payfileVOs;
		// return dao.getPkFinanceOrg(payfileVOs);
	}

	/**
	 * 转档第四步，查询转档时两个类别的薪资项目
	 * 
	 * @author liangxr on 2010-1-13
	 * @see nc.itf.hr.wa.IPayfileQueryService#queryByPKclass(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public ItemsVO[] queryByPKclass(WaLoginContext context, WaClassVO classIn) throws BusinessException {
		// 转出的薪资类别
		WaClassItemVO[] classitemsout = null;
		// 转入薪资类别
		WaClassItemVO[] classitemsin = null;
		// 转档项目对照VO
		ItemsVO[] itemsvo = null;

		String pk_org = context.getPk_org();
		String PKOut = context.getPk_wa_class();
		String waYear = context.getWaYear();
		String waPeriod = context.getWaPeriod();

		IClassItemQueryService queryItem = NCLocator.getInstance().lookup(IClassItemQueryService.class);
		classitemsout = queryItem.queryItemInfoVO(pk_org, PKOut, waYear, waPeriod);
		classitemsin = queryItem.queryItemInfoVO(pk_org, classIn.getPk_wa_class(), classIn.getCyear(),
				classIn.getCperiod());
		if (classitemsout != null && classitemsout.length > 0) {
			itemsvo = new ItemsVO[classitemsout.length];
			// 如果公共项目ID相同，则转入和转出的薪资项目相互对照
			for (int i = 0; i < classitemsout.length; i++) {
				itemsvo[i] = new ItemsVO();
				itemsvo[i].setCode(classitemsout[i].getItemkey());
				itemsvo[i].setName(classitemsout[i].getMultilangName());
				itemsvo[i].setType(classitemsout[i].getIitemtype());
				if (classitemsin != null && classitemsin.length > 0) {
					for (WaClassItemVO element : classitemsin) {
						if (itemsvo[i].getCode().equals(element.getItemkey())) {
							itemsvo[i].setNcode(element.getItemkey());
							itemsvo[i].setNname(element.getMultilangName());
							itemsvo[i].setNtype(element.getIitemtype());
						}
					}
				}
			}
		}

		return itemsvo;
	}

	/**
	 * 是否有未审核的人员
	 * 
	 * @author liangxr on 2010-1-14
	 * @see nc.itf.hr.wa.IPayfileQueryService#allDataHasChecked(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public boolean allDataHasChecked(String classid, String cyear, String cperiod) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		return !dao.havePsnNotCheck(classid, cyear, cperiod);
	}

	/**
	 * @author liangxr on 2010-1-18
	 * @see nc.itf.hr.wa.IPayfileManageService#updateCheckFlagAtPeriod(java.lang.String,
	 *      java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public void updateCheckFlagAtPeriod(WaLoginVO loginVO) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		dao.updateTableByColKey("wa_periodstate", PeriodStateVO.CHECKFLAG, UFBoolean.FALSE, getLoginWhereSql(loginVO)
				+ " and caculateflag = 'Y'");
	}

	private String getLoginWhereSql(WaLoginVO loginVO) {
		return WherePartUtil.getCommonWhereCondtion4PeriodState(loginVO);
	}

	/**
	 * 给人员转档
	 * 
	 * @author liangxr on 2010-5-6
	 * @see nc.itf.hr.wa.IPayfileManageService#alterPsn(nc.vo.wa.payfile.PayfileVO[],
	 *      nc.vo.wa.payfile.ItemsVO[], java.lang.String,
	 *      nc.vo.wa.pub.WaLoginContext, nc.vo.wa.category.WaClassVO, boolean)
	 */

	@Override
	public void alterPsn(PayfileVO[] psns, ItemsVO[] itemvos, String userId, WaLoginContext context, WaClassVO classin,
			boolean isCover) throws BusinessException {

		String classout = context.getPk_wa_class();
		String waYearOut = context.getWaYear();
		String waPeriodOut = context.getWaPeriod();

		String waYearIn = classin.getCyear();
		String waPeriodIn = classin.getCperiod();

		WaLoginVO checkState = getcheckstate(classin.getPk_wa_class(), waYearIn, waPeriodIn);
		if (isCheckedOrAprroved(checkState)) {
			throw new BusinessException(ResHelper.getString("60130payfile", "060130payfile0325")/*
																								 * @
																								 * res
																								 * "转入的薪资方案已审核！"
																								 */);
		}
		if (psns.length > 500) {
			PayfileVO[] addPsnChild = null;
			int end = 0;
			int len = psns.length / 500 + 1;
			for (int i = 0; i < len; i++) {
				end = 500 * (i + 1);
				addPsnChild = Arrays.copyOfRange(psns, 500 * i, end >= psns.length ? psns.length : end);
				// 插入wa_data
				alterPsnByGroup(context, addPsnChild, classin, itemvos, isCover);
			}
			addPsnChild = null;
		} else {
			alterPsnByGroup(context, psns, classin, itemvos, isCover);
		}
		PayfileDAO dao = new PayfileDAO();

		// 核查剩余的人员是否全部审核,如果是,则修改期间状态的审核标识
		if (allDataHasChecked(classout, waYearOut, waPeriodOut)) {
			updatePeriodCheck(classout, waYearOut, waPeriodOut);
		}
		// 判断如果删除后所有记录都已计算的话，那更新计算状态
		if (dao.isAllCaculated(classout, waYearOut, waPeriodOut, null)) {
			updatePeriodstate(classout, waYearOut, waPeriodOut, UFBoolean.TRUE);
		}
	}

	private void alterPsnByGroup(WaLoginContext context, PayfileVO[] psns, WaClassVO classin, ItemsVO[] itemvos,
			boolean isCover) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		String waYearIn = classin.getCyear();
		String waPeriodIn = classin.getCperiod();
		String psnjobs = null;
		String psndocs = null;
		// 处理有重复人员情况
		PayfileVO[] existPsns = dao.queryExistPsns(psns, classin);
		if (!ArrayUtils.isEmpty(existPsns)) {
			if (isCover) {
				dao.delDataByClass(existPsns, classin.getPk_wa_class(), waYearIn, waPeriodIn);
				insertDataArray(psns, classin.getPk_wa_class(), waYearIn, waPeriodIn);
			} else {
				PayfileVO[] notexistVOs = getNotExistPsns(psns, existPsns);
				insertDataArray(notexistVOs, classin.getPk_wa_class(), waYearIn, waPeriodIn);
			}
		} else {
			insertDataArray(psns, classin.getPk_wa_class(), waYearIn, waPeriodIn);
		}

		// 如果转入方案是多次发放，
		if (WaLoginVOHelper.isSubClass(classin)
		// 20160113 shenliangc 客户发现历版都存在薪资档案人员转档到多次方案时，汇总档案中不会自动添加这些档案人员。
				|| (classin.getPk_prnt_class() != null && !classin.getPk_prnt_class().equals(classin.getPk_wa_class()))) {
			// 其他子方案已存在的记录，扣税设置不变
			psnjobs = FormatVO.formatArrayToString(psns, PayfileVO.PK_PSNJOB);
			dao.updateTaxsetByClass("  and a.pk_psnjob in (" + psnjobs + ") ", classin.getPk_prnt_class(),
					classin.getPk_wa_class(), waYearIn, waPeriodIn);

			// 将父方案中不存在的人员添加到父方案中
			PayfileVO[] psns2 = dao.getPsnNotInPrntClass(classin.getPk_prnt_class(), classin.getPk_wa_class(),
					waYearIn, waPeriodIn);
			if (psns2 != null) {
				for (PayfileVO vo : psns2) {
					vo.setPk_wa_class(classin.getPk_prnt_class());
				}
				getBaseDao().insertVOArray(psns2);
			}
		}

		// 修改薪资结帐表信息
		updatePeriodstate(classin.getPk_wa_class(), waYearIn, waPeriodIn, UFBoolean.FALSE);

		// 复制薪资项目数据
		dao.transSelData(psns, itemvos, context, classin);

		// 从转出类别中删除
		dao.delDataByClass(psns, context.getPk_wa_class(), context.getWaYear(), context.getWaPeriod());

		// 如果转出方案 是多次发放。其他子方案不存在的人员，删除时连汇总方案中一起删除
		if (context.getPk_prnt_class() != null && !context.getPk_prnt_class().equals(context.getPk_wa_class())) {
			psndocs = FormatVO.formatArrayToString(psns, PayfileVO.PK_PSNDOC);
			dao.deleteFromTotal(psndocs, context.getPk_prnt_class(), context.getPk_wa_class(), context.getWaYear(),
					context.getWaPeriod());
		}

	}

	/**
	 * 判断一个薪资方案是否已经在审核状态以后
	 * 
	 * @author xuanlt on 2010-4-29
	 * @return
	 * @return boolean
	 */
	private boolean isCheckedOrAprroved(WaLoginVO waloginvo) {

		return (waloginvo.getState() == WaState.CLASS_IS_APPROVED
				|| waloginvo.getState() == WaState.CLASS_IN_APPROVEING
				|| waloginvo.getState() == WaState.CLASS_CHECKED_WITHOUT_APPROVE
				|| waloginvo.getState() == WaState.CLASS_ALL_PAY || waloginvo.getState() == WaState.CLASS_CHECKED_WITHOUT_PAY);
	}

	/**
	 * 将薪资档案插入到转入类别中
	 * 
	 * @author liangxr on 2010-4-26
	 * @param psns
	 * @param classin
	 * @param waYear
	 * @param waPeriod
	 * @throws BusinessException
	 */
	private void insertDataArray(PayfileVO[] psns, String classin, String waYear, String waPeriod)
			throws BusinessException {

		for (int i = 0; i < psns.length; i++) {
			psns[i].setPk_wa_class(classin);
			psns[i].setCyear(waYear);
			psns[i].setCperiod(waPeriod);
			psns[i].setCyearperiod(waYear + waPeriod);
			psns[i].setPk_wa_data(null);
			insert(psns[i]);
		}
	}

	private PayfileVO[] getNotExistPsns(PayfileVO[] psns, PayfileVO[] existVos) {
		Set<String> set = new HashSet<String>();
		Vector<PayfileVO> v = new Vector<PayfileVO>();
		for (PayfileVO existvo : existVos) {
			set.add(existvo.getPk_psndoc());
		}
		for (PayfileVO vo : psns) {
			if (!set.contains(vo.getPk_psndoc())) {
				v.add(vo);
			}
		}
		PayfileVO[] result = new PayfileVO[v.size()];
		v.copyInto(result);
		return result;
	}

	/**
	 * 从数据库中删除一批vO：转档
	 * 
	 * @author liangxr on 2010-1-18
	 * @param voArray
	 * @param userId
	 * @param aRecacuVO
	 * @throws nc.vo.pub.BusinessException
	 */
	public void deletePsnArrayForAlter(PayfileVO[] voArray) throws BusinessException {

		for (int i = 0; i < voArray.length; i++) {
			// 删除转出表的信息
			delete(voArray[i]);
		}
		// 修改薪资结帐表信息
		// 判断如果修改后所有记录都已计算的话，那更新计算状态
		updatePeriodstateCalflag(voArray[0]);

	}

	/**
	 * 更新薪资期间计算状态状态
	 * 
	 * @author liangxr on 2010-1-18
	 * @param pk_wa_class
	 * @param cyear
	 * @param cperiod
	 * @throws BusinessException
	 */
	private void updatePeriodstate(String pk_wa_class, String waYear, String waPeriod, UFBoolean isCacu)
			throws BusinessException {
		PayfileDAO dao = new PayfileDAO();

		PeriodVO periodvo = dao.queryPeriodByWaClass(pk_wa_class, waYear, waPeriod);
		StringBuffer whereSql = new StringBuffer();
		whereSql.append(" pk_wa_class = '");
		whereSql.append(pk_wa_class);
		whereSql.append("' and pk_wa_period = '");
		whereSql.append(periodvo.getPk_wa_period());
		whereSql.append("'");

		// 修改薪资结帐表信息

		String[] strCols = new String[] { "checkflag", "caculateflag", "daccdate" };
		Object[] strKeys = new Object[] { UFBoolean.FALSE, isCacu, new UFDate() };
		dao.updateTableByColKey("wa_periodstate", strCols, strKeys, whereSql.toString());
	}

	/**
	 * 更新审核状态为已审核
	 * 
	 * @author liangxr on 2010-7-6
	 * @param pk_wa_class
	 * @param waYear
	 * @param waPeriod
	 * @param isCacu
	 * @throws BusinessException
	 */
	private void updatePeriodCheck(String pk_wa_class, String waYear, String waPeriod) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();

		PeriodVO periodvo = dao.queryPeriodByWaClass(pk_wa_class, waYear, waPeriod);
		StringBuffer whereSql = new StringBuffer();
		whereSql.append(" caculateflag = 'Y' and pk_wa_class = '");
		whereSql.append(pk_wa_class);
		whereSql.append("' and pk_wa_period = '");
		whereSql.append(periodvo.getPk_wa_period());
		whereSql.append("'");

		// 修改薪资结帐表信息

		String[] strCols = new String[] { "checkflag", "daccdate" };
		Object[] strKeys = new Object[] { UFBoolean.TRUE, new UFDate() };
		dao.updateTableByColKey("wa_periodstate", strCols, strKeys, whereSql.toString());
	}

	/**
	 * 获取指定期间和指定方案的数据处理状态
	 * 
	 * @author liangxr on 2010-5-6
	 * @param inWaLoginVO
	 * @return
	 * @throws BusinessException
	 */
	private WaLoginVO getcheckstate(String pk_wa_class, String waYear, String waPeriod) throws BusinessException {
		WaLoginVO inWaLoginVO = new WaLoginVO();
		inWaLoginVO.setPk_wa_class(pk_wa_class);
		PeriodStateVO periodstatevo = new PeriodStateVO();
		periodstatevo.setCyear(waYear);
		periodstatevo.setCperiod(waPeriod);
		inWaLoginVO.setPeriodVO(periodstatevo);

		IWaPub wapub = NCLocator.getInstance().lookup(IWaPub.class);
		return wapub.getWaclassVOWithState(inWaLoginVO);
	}

	/**
	 * 薪资档案批量增加
	 * 
	 * @author liangxr on 2010-1-20
	 * @return
	 * @see nc.itf.hr.wa.IPayfileQueryService#addPsnVOs(nc.vo.wa.payfile.PayfileVO[])
	 */
	@Override
	public PayfileVO[] addPsnVOs(PayfileVO[] addPsn) throws BusinessException {
		// ssx added on 2020-10-30
		// 設置補充保費計算方式默認值
		for (PayfileVO vo : addPsn) {
			if (vo.getExnhitype() == null || vo.getExnhitype() == 0) {
				vo.setExnhitype(PayfileDspUtil.getDefaultExNhiType(vo.getPk_org(), vo.getPk_wa_class(),
						vo.getPk_psnorg()));
			}
		}
		//
		BDPKLockUtil.lockString(addPsn[0].getPk_wa_class());
		validateVO(addPsn[0], IValidatorFactory.INSERT);
		// 更新银行帐号信息
		PayfileDAO dao = new PayfileDAO();
		String psnjobs = null;
		String[] pk_wa_datas = new String[addPsn.length];
		if (addPsn.length > 500) {
			pk_wa_datas = getBaseDao().insertVOArray(addPsn);
			dao.updateAccount(addPsn);
			// 如果是多次发放方案，其他子方案已存在的记录，扣税设置不变
			if (addPsn[0].getPk_prnt_class() != null) {
				dao.updateTaxsetByClass(" and a.checkflag = 'N'  ", addPsn[0].getPk_prnt_class(),
						addPsn[0].getPk_wa_class(), addPsn[0].getCyear(), addPsn[0].getCperiod());
			}
		} else {
			// 插入wa_data
			pk_wa_datas = getBaseDao().insertVOArray(addPsn);
			// 更新银行帐号信息
			psnjobs = FormatVO.formatArrayToString(addPsn, PayfileVO.PK_PSNJOB);
			dao.updateAccount(addPsn);
			// 如果是多次发放方案，其他子方案已存在的记录，扣税设置不变
			if (addPsn[0].getPk_prnt_class() != null) {
				dao.updateTaxsetByClass(" and a.pk_psnjob in (" + psnjobs + ")  ", addPsn[0].getPk_prnt_class(),
						addPsn[0].getPk_wa_class(), addPsn[0].getCyear(), addPsn[0].getCperiod());
			}
		}

		// 同步默认的财务组织，成本中心。
		dao.synFiAndCostOrg(addPsn);

		// 修改期间结帐表状态
		updatePeriodstateCalflag(addPsn[0]);
		// 如果是多次发放，将父方案中不存在的人员添加到父方案中
		if (addPsn[0].getPk_prnt_class() != null && !addPsn[0].getPk_wa_class().equals(addPsn[0].getPk_prnt_class())) {
			PayfileVO[] psns = dao.getPsnNotInPrntClass(addPsn[0].getPk_prnt_class(), addPsn[0].getPk_wa_class(),
					addPsn[0].getCyear(), addPsn[0].getCperiod());
			if (psns != null) {
				for (PayfileVO vo : psns) {
					vo.setPk_wa_class(addPsn[0].getPk_prnt_class());
				}
				getBaseDao().insertVOArray(psns);
			}
		}
		return queryPayfileVOByPKS(pk_wa_datas);
	}

	/**
	 * 薪资档案批量增加
	 * 
	 * @author liangxr on 2010-1-20
	 * @return
	 * @see nc.itf.hr.wa.IPayfileQueryService#addPsnVOs(nc.vo.wa.payfile.PayfileVO[])
	 */

	public void addPsnVOsToOtherClass(PayfileVO[] addPsn) throws BusinessException {
		BDPKLockUtil.lockString(addPsn[0].getPk_wa_class());
		validateVO(addPsn[0], IValidatorFactory.INSERT);
		PayfileDAO dao = new PayfileDAO();
		String psnjobs = null;
		if (addPsn.length > 500) {
			getBaseDao().insertVOArray(addPsn);
			// 如果是多次发放方案，其他子方案已存在的记录，扣税设置不变
			if (addPsn[0].getPk_prnt_class() != null) {
				dao.updateTaxsetByClass(" and a.checkflag = 'N'  ", addPsn[0].getPk_prnt_class(),
						addPsn[0].getPk_wa_class(), addPsn[0].getCyear(), addPsn[0].getCperiod());
			}
		} else {
			// 插入wa_data
			getBaseDao().insertVOArray(addPsn);
			// 更新银行帐号信息
			psnjobs = FormatVO.formatArrayToString(addPsn, PayfileVO.PK_PSNJOB);
			// 如果是多次发放方案，其他子方案已存在的记录，扣税设置不变
			if (addPsn[0].getPk_prnt_class() != null) {
				dao.updateTaxsetByClass(" and a.pk_psnjob in (" + psnjobs + ")  ", addPsn[0].getPk_prnt_class(),
						addPsn[0].getPk_wa_class(), addPsn[0].getCyear(), addPsn[0].getCperiod());
			}
		}

		// 修改期间结帐表状态
		updatePeriodstateCalflag(addPsn[0]);
		// 如果是多次发放，将父方案中不存在的人员添加到父方案中
		if (addPsn[0].getPk_prnt_class() != null && !addPsn[0].getPk_wa_class().equals(addPsn[0].getPk_prnt_class())) {
			PayfileVO[] psns = dao.getPsnNotInPrntClass(addPsn[0].getPk_prnt_class(), addPsn[0].getPk_wa_class(),
					addPsn[0].getCyear(), addPsn[0].getCperiod());
			if (psns != null) {
				for (PayfileVO vo : psns) {
					vo.setPk_wa_class(addPsn[0].getPk_prnt_class());
				}
				getBaseDao().insertVOArray(psns);
			}
		}
	}

	/**
	 * 批量新增，修改校验器
	 * 
	 * @author liangxr on 2010-4-16
	 * @param type
	 * @throws BusinessException
	 */
	private void validateVO(PayfileVO vo, String type) throws BusinessException {
		DefaultValidationService validationService = SimpleDocServiceTemplate.createValidationService();
		if (getServiceTemplate().getValidatorFactory() != null) {
			Validator[] validators = getServiceTemplate().getValidatorFactory().createValidator(type);
			if (validators != null && validators.length > 0) {
				for (Validator validator : validators) {
					validationService.addValidator(validator);
				}
			}
		}
		validationService.validate(vo);
	}

	/**
	 * @author liangxr on 2010-1-21
	 * @see nc.itf.hr.wa.IPayfileManageService#delPsnVOs(nc.vo.wa.payfile.PayfileVO[])
	 */
	@Override
	public void delPsnVOs(PayfileVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
		BDPKLockUtil.lockString(vos[0].getPk_wa_class());
		PayfileDAO dao = new PayfileDAO();
		// 分组删除相关表内容（处理大数据量）
		String psndocs = null;
		if (vos.length > 500) {
			PayfileVO[] delPsnChild = null;
			int end = 0;
			int len = vos.length / 500 + 1;
			for (int i = 0; i < len; i++) {
				end = 500 * (i + 1);
				delPsnChild = Arrays.copyOfRange(vos, 500 * i, end >= vos.length ? vos.length : end);
				if (delPsnChild.length > 0) {
					dao.deleteRelationTableMClass(delPsnChild);
					// getBaseDao().deleteVOArray(delPsnChild);
					// 如果 是多次发放。其他子方案不存在的人员，删除时连汇总方案中一起删除
					if (vos[0].getPk_prnt_class() != null && !vos[0].getPk_wa_class().equals(vos[0].getPk_prnt_class())) {
						psndocs = FormatVO.formatArrayToString(delPsnChild, PayfileVO.PK_PSNDOC);
						dao.deleteFromTotal(psndocs, vos[0].getPk_prnt_class(), vos[0].getPk_wa_class(),
								vos[0].getCyear(), vos[0].getCperiod());
					}
				}
			}
			delPsnChild = null;
		} else {
			dao.deleteRelationTableMClass(vos);
			// getBaseDao().deleteVOArray(vos);
			if (vos[0].getPk_prnt_class() != null && !vos[0].getPk_wa_class().equals(vos[0].getPk_prnt_class())) {
				// 同步父方案里面的薪资档案
				psndocs = FormatVO.formatArrayToString(vos, PayfileVO.PK_PSNDOC);
				dao.deleteFromTotal(psndocs, vos[0].getPk_prnt_class(), vos[0].getPk_wa_class(), vos[0].getCyear(),
						vos[0].getCperiod());
			}
		}
		PayfileVO vo = vos[0];
		if (dao.isLeaveClass(vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod())
				&& !dao.existPsnInClass(vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod())) {
			// 删除期间状态
			deletePeriodState(vo.getPk_wa_class());
			// 删除相关项目
			deleteClassitem(vo.getPk_wa_class());
			// 删除关系表
			deleteRelation(vo.getPk_wa_class());
			// 删除发放次数对应的薪资类别
			deleteTimesClass(vo.getPk_wa_class());
			// 如果所有子方案都发放完，则修改父方案状态
			WaClassDAO waclassdao = new WaClassDAO();
			WaClassVO waclassvo = new WaClassVO();
			waclassvo.setPk_wa_class(vo.getPk_prnt_class());
			waclassvo.setCyear(vo.getCyear());
			waclassvo.setCperiod(vo.getCperiod());
			if (waclassdao.isChildPayoff(waclassvo))
				dao.updateParentClassPayOff(vo.getPk_prnt_class(), vo.getCyear(), vo.getCperiod(), "Y");
			dao.updateLeaveFlag(vo.getPk_prnt_class(), vo.getCyear(), vo.getCperiod(), false);
		} else {
			// 判断如果删除后所有记录都已计算的话，那更新计算状态
			updatePeriodstateCalflag(vo);
			// 判断如果删除后所有记录都已审核，则更甚新审核状态
			updatePeriodstateCheckflag(vo);
		}

		new BDBusiLogUtil(HRWAMetadataConstants.WA_FILE_MDID).writeBusiLog(HRWAMetadataConstants.DELETE, "", vos);
	}

	/**
	 * @author liangxr on 2010-1-25
	 * @see nc.itf.hr.wa.IPayfileQueryService#hasRepeatPsn(nc.vo.wa.pub.WaLoginVO,
	 *      nc.vo.wa.pub.WaLoginVO)
	 */
	@Override
	public boolean hasRepeatPsn(WaLoginVO fromWaClass, WaLoginVO toWaClass) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		return dao.hasRepeatPsn(fromWaClass, toWaClass);
	}

	/**
	 * 复制薪资档案
	 * 
	 * @author liangxr on 2010-1-25
	 * @param fromWaClass
	 * @param aRecaVO
	 * @param reWrite
	 */
	@Override
	public void copyWaPsn(WaLoginVO fromWaClass, WaLoginVO waLoginVO, boolean reWrite) throws BusinessException {
		copyWaPsn(fromWaClass, waLoginVO, reWrite, true);
	}

	/**
	 * 复制薪资档案
	 * 
	 * @author liangxr on 2010-1-25
	 * @param fromWaClass
	 * @param aRecaVO
	 * @param reWrite
	 * @Param addToSumClass 方案增加次数，不需要校验子方案是否包含该人员，一定会包括
	 */
	@Override
	public void copyWaPsn(WaLoginVO fromWaClass, WaLoginVO waLoginVO, boolean reWrite, boolean addToSumClass)
			throws BusinessException {

		PayfileVO[] unrepeatedPsnVOs = getCopyedPsn(fromWaClass, waLoginVO, false);
		if (reWrite) {
			PayfileVO[] repeatedPsnVOs = getCopyedPsn(fromWaClass, waLoginVO, true);

			if (repeatedPsnVOs != null) {
				delOverlapPsnForCopy(fromWaClass, waLoginVO);
				doCopyPsnVOs(replaceWaClass(repeatedPsnVOs, waLoginVO));
				List<PayfileVO> repeatedsingle = new ArrayList<PayfileVO>();
				List<PayfileVO> existlist = getExistInSubClassList(fromWaClass.getPk_wa_class(),
						waLoginVO.getPk_prnt_class(), waLoginVO.getPk_wa_class(), waLoginVO.getPeriodVO().getCyear(),
						waLoginVO.getPeriodVO().getCyear(), repeatedPsnVOs);
				for (PayfileVO repeatedvo : repeatedPsnVOs) {
					if (!existlist.contains(repeatedvo)) {
						repeatedsingle.add(repeatedvo);
					}
				}
				// guopeng 修改NCdp205509519 效率问题
				// for(int i = 0; i < repeatedPsnVOs.length; i++){
				// if(!existInSubClass(repeatedPsnVOs[i])){
				// repeatedsingle.add(repeatedPsnVOs[i]);
				// }
				// }
				if (repeatedsingle.size() != 0) {
					PayfileDAO dao = new PayfileDAO();
					dao.delDataByClass(repeatedsingle.toArray(new PayfileVO[0]), waLoginVO.getPk_prnt_class(),
							waLoginVO.getCyear(), waLoginVO.getCperiod());
					doCopyPsnVOs(replaceWaClassPrnt(repeatedsingle.toArray(new PayfileVO[0]), waLoginVO));
				}
			}
			repeatedPsnVOs = null;
		}
		if (unrepeatedPsnVOs != null) {
			doCopyPsnVOs(replaceWaClass(unrepeatedPsnVOs, waLoginVO));
			List<PayfileVO> unrepeatedsingle = new ArrayList<PayfileVO>();
			// 增加发放次数，不需要同步汇总方案，原发放次数已保证汇总方案人员存在
			if (addToSumClass) {
				List<PayfileVO> existlist = getExistInSubClassList(fromWaClass.getPk_wa_class(),
						waLoginVO.getPk_prnt_class(), waLoginVO.getPk_wa_class(), waLoginVO.getPeriodVO().getCyear(),
						waLoginVO.getPeriodVO().getCperiod(), unrepeatedPsnVOs);
				// for(int i = 0; i < unrepeatedPsnVOs.length; i++){
				// if(!existInSubClass(unrepeatedPsnVOs[i])){
				// unrepeatedsingle.add(unrepeatedPsnVOs[i]);
				// }
				for (PayfileVO unrepeatedvo : unrepeatedPsnVOs) {
					if (!existlist.contains(unrepeatedvo)) {
						unrepeatedsingle.add(unrepeatedvo);
					}
				}

				// }
				// guoqt薪资档案复制报错
				PayfileVO[] unrepeatedPsnCoVOs = unrepeatedsingle.toArray(new PayfileVO[0]);
				// 同步汇总方案
				if (unrepeatedsingle.size() != 0
						&& !unrepeatedPsnCoVOs[0].getPk_wa_class().equals(unrepeatedPsnCoVOs[0].getPk_prnt_class())) {
					doCopyPsnVOs(replaceWaClassPrnt(unrepeatedPsnCoVOs, waLoginVO));
				}
			}
		}
		unrepeatedPsnVOs = null;

	}

	/**
	 * 薪资档案批复制
	 * 
	 * @param doCopyPsnVOs
	 * @throws BusinessException
	 */
	private void doCopyPsnVOs(PayfileVO[] addPsn) throws BusinessException {
		BDPKLockUtil.lockString(addPsn[0].getPk_wa_class());
		validateVO(addPsn[0], IValidatorFactory.INSERT);
		// 更新银行帐号信息
		PayfileDAO dao = new PayfileDAO();
		// 插入wa_data
		getBaseDao().insertVOArray(addPsn);
		dao.updateAccount(addPsn);

	}

	/**
	 * 替换档案的薪资类别
	 * 
	 * @author liangxr on 2010-1-25
	 * @param psns
	 * @param context
	 * @return
	 */
	private PayfileVO[] replaceWaClass(PayfileVO[] psns, WaLoginVO waLoginVO) {
		for (int i = 0; i < psns.length; i++) {
			psns[i].setPk_wa_data(null);
			psns[i].setPk_wa_class(waLoginVO.getPk_wa_class());
			psns[i].setCyear(waLoginVO.getPeriodVO().getCyear());
			psns[i].setCperiod(waLoginVO.getPeriodVO().getCperiod());
			psns[i].setCyearperiod(waLoginVO.getPeriodVO().getCyear() + waLoginVO.getPeriodVO().getCperiod());
			psns[i].setCaculateflag(UFBoolean.FALSE);
			psns[i].setCheckflag(UFBoolean.FALSE);
			psns[i].setPk_prnt_class(waLoginVO.getPk_prnt_class());
		}
		return psns;
	}

	/**
	 * 替换档案的薪资类别（同步汇总方案）
	 * 
	 * @author guoqt on 2014-01-14
	 * @param psns
	 * @param context
	 * @return
	 */
	private PayfileVO[] replaceWaClassPrnt(PayfileVO[] psns, WaLoginVO waLoginVO) {
		for (int i = 0; i < psns.length; i++) {
			psns[i].setPk_wa_data(null);
			psns[i].setPk_wa_class(waLoginVO.getPk_prnt_class());
			psns[i].setCyear(waLoginVO.getPeriodVO().getCyear());
			psns[i].setCperiod(waLoginVO.getPeriodVO().getCperiod());
			psns[i].setCyearperiod(waLoginVO.getPeriodVO().getCyear() + waLoginVO.getPeriodVO().getCperiod());
			psns[i].setCaculateflag(UFBoolean.FALSE);
			psns[i].setCheckflag(UFBoolean.FALSE);
			// guoqt薪资档案复制报错
			psns[i].setPk_prnt_class(waLoginVO.getPk_prnt_class());
		}
		return psns;
	}

	/**
	 * 复制时删除重复人员
	 * 
	 * @author liangxr on 2010-4-19
	 * @throws BusinessException
	 */
	private void delOverlapPsnForCopy(WaLoginVO fromWaClass, WaLoginVO waLoginVO) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		dao.delOverlapPsnForCopy(fromWaClass, waLoginVO);

	}

	/**
	 * 查询可以复制的人员
	 * 
	 * @author liangxr on 2010-1-25
	 * @param fromWaClass
	 *            源薪资类别
	 * @param context
	 *            目标薪资档案信息
	 * @param isrepeat
	 *            是否包含重复
	 * @return
	 * @throws BusinessException
	 */

	private PayfileVO[] getCopyedPsn(WaLoginVO fromWaClass, WaLoginVO waLoginVO, boolean isrepeat)
			throws BusinessException {
		// 获取需要复制的人员
		SqlWhereUtil squ = new SqlWhereUtil();

		squ.s("		  pk_org='" + waLoginVO.getPk_org() + "'");
		squ.and().s(" pk_wa_class='" + fromWaClass.getPk_wa_class() + "'");
		squ.and().s(" cyear='" + fromWaClass.getCyear() + "'");
		squ.and().s(" cperiod='" + fromWaClass.getCperiod() + "'");
		// squ.and().s(" stopflag='N' ");
		squ.and().s(" pk_psnjob not in(select pk_psnjob from hi_psnjob where endflag = 'Y' and ismainjob ='N') ");
		squ.and().s(" pk_psndoc " + (isrepeat ? " in " : " not in "));
		squ.s("     ( select pk_psndoc from wa_data ");
		squ.s("       where pk_wa_class = '" + waLoginVO.getPk_wa_class() + "' ");
		squ.and().s(" cyear = '" + waLoginVO.getPeriodVO().getCyear() + "' ");
		squ.and().s(" cperiod = '" + waLoginVO.getPeriodVO().getCperiod() + "') ");

		// 两个方案中，加入到离职结薪的人员，是不需要复制的
		String fromPk_wa_prntclass = fromWaClass.getPk_prnt_class();
		String fromcyear = fromWaClass.getCyear();
		String fromcperiod = fromWaClass.getCperiod();

		String destPk_wa_prntclass = waLoginVO.getPk_prnt_class();
		String destcyear = waLoginVO.getPeriodVO().getCyear();
		String destcperiod = waLoginVO.getPeriodVO().getCperiod();

		// 源方案与目标方案已经加入到离职结薪的需要去掉
		squ.and()
				.s("  pk_psndoc not in   ( select wa_data.pk_psndoc from wa_data where ( pk_wa_class in ( select wa_inludeclass.pk_childclass from wa_inludeclass where pk_parentclass = '"
						+ fromPk_wa_prntclass
						+ "' and cyear = '"
						+ fromcyear
						+ "' and cperiod = '"
						+ fromcperiod
						+ "' and batch > 100 ) and cyear = '"
						+ fromcyear
						+ "' and cperiod = '"
						+ fromcperiod
						+ "' ) or ( pk_wa_class in ( select wa_inludeclass.pk_childclass from wa_inludeclass where pk_parentclass = '"
						+ destPk_wa_prntclass
						+ "' and cyear = '"
						+ destcyear
						+ "' and cperiod = '"
						+ destcperiod
						+ "' and batch > 100 ) and cyear = '"
						+ destcyear
						+ "' and cperiod = '"
						+ destcperiod
						+ "' )  )");

		return getServiceTemplate().queryByCondition(PayfileVO.class, squ.toString());
	}

	@Override
	public String getAddWhere(WaLoginContext context, int trnType) throws BusinessException {
		return this.getAddWhere(context, trnType, true);
	}

	/***************************************************************************
	 * <br>
	 * Created on 2012-7-17 下午8:10:48<br>
	 * 
	 * @param context
	 * @param trnType
	 * @param addRule
	 *            是否调用计薪规则
	 * @return
	 * @throws BusinessException
	 * @author daicy
	 ***************************************************************************/
	public String getAddWhere(WaLoginContext context, int trnType, boolean addRule) throws BusinessException {
		StringBuffer sqlB = new StringBuffer();
		if (PsnChangeHelper.isExists(trnType)) {// 在薪资档案的人员,且为同一个组织关系

			sqlB.append(" and exists ");
			sqlB.append(" (select 1 from wa_data ");
			sqlB.append(" where wa_data.pk_psndoc = psnjob.pk_psndoc and wa_data.assgid = psnjob.assgid and wa_data.pk_psnorg = psnjob.pk_psnorg and wa_data.checkflag='N' ");
			sqlB.append("and wa_data.pk_wa_class = '").append(context.getPk_wa_class()).append("' ");
			sqlB.append("and wa_data.cyear = '").append(context.getWaYear()).append("' ");
			sqlB.append("and wa_data.cperiod = '").append(context.getWaPeriod()).append("') ");

		} else {// 没有加入到薪资档案的人员
			// 此处从薪资方案接口获取计薪规则sql
			String ruleSql = "";

			if (addRule) {
				ruleSql = (NCLocator.getInstance().lookup(IWaClass.class)).queryWaRangeRule(context.getPk_wa_class());
			}

			if (!StringUtil.isEmpty(ruleSql)) {
				sqlB.append("and ").append(ruleSql.replaceAll("hi_psnjob", "psnjob").replaceAll("bd_psndoc", "psndoc"));
			}
			// 获取业务委托sql
			String msSql = ManagescopeFacade.queryPsnjobPksSQLByHrorgAndBusiregion(context.getPk_org(),
					ManagescopeBusiregionEnum.salary, true);

			if (!StringUtil.isEmpty(msSql)) {
				sqlB.append(" and psnjob.pk_psnjob in " + msSql);
			}

			sqlB.append("  and not exists ");
			sqlB.append(" (select 1   from wa_data ");
			sqlB.append("  where wa_data.pk_psndoc = psnjob.pk_psndoc  ");
			sqlB.append(" and wa_data.pk_wa_class = '").append(context.getPk_wa_class()).append("' ");
			sqlB.append(" and wa_data.cyear = '").append(context.getWaYear()).append("' ");
			sqlB.append(" and wa_data.cperiod = '").append(context.getWaPeriod()).append("') ");

		}
		return sqlB.toString();
	}

	public String getBoolNullSql(String strFieldCode) {
		return MessageFormat.format(" isnull({0},''{1}'')=''{1}'' ", strFieldCode, UFBoolean.TRUE);
	}

	public Map<String, Object> getAboutRuleForPayfile(WaLoginContext context, String addWhere) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		Map<String, Object> map = new HashMap<String, Object>(3);

		// 获取业务委托sql
		String wtCond = null;
		try {
			wtCond = ManagescopeFacade.queryPsnjobPksSQLByHrorgAndBusiregion(context.getPk_org(),
					ManagescopeBusiregionEnum.salary, true);
		} catch (BusinessException e) {
			Debug.error(e.getMessage(), e);
		}

		IWaClass waclassQuery = NCLocator.getInstance().lookup(IWaClass.class);
		// 获取计薪规则影响列
		WaPsnhiVO[] rulevo = waclassQuery.queryRangRuleByClass(context.getPk_wa_class());

		if (ArrayUtils.isEmpty(rulevo)) {// 如果计薪规则为空，则只判断业务委托范围

			if (wtCond != null) {
				// wtCond =
				// " and (wa_data.isrulehint = 'Y' or wa_data.isrulehint is null) and wa_data.pk_psnjob not in "
				// 去掉最新记录条件，薪资可以给旧的任职记录发工资
				wtCond = wtCond.replaceAll("and hi_psnjob.lastflag = 'Y'", "");
				wtCond = " and (wa_data.isrulehint = 'Y' or " + getBoolNullSql("wa_data.isrulehint")
						+ " ) and hi_psnjob.psntype = 0 and wa_data.pk_psnjob not in " + wtCond;
				// 档案中已存在的不在业务委托人员
				PsnTrnVO[] vos = dao.queryNotInRule(context, wtCond, orderCondtion);
				map.put(PayfileConstant.NOT_IN_RULE, vos);
			}
			return map;
		}
		map.put(PayfileConstant.RULE_COL, rulevo);
		// 获取计薪规则
		String condition = "";
		String ruleSql = waclassQuery.queryWaRangeRule(context.getPk_wa_class());
		if (StringUtil.isEmpty(ruleSql)) {
			map.put(PayfileConstant.NOT_IN_RULE, null);
		} else {
			wtCond = wtCond.replaceAll("and hi_psnjob.lastflag = 'Y'", "");
			// condition =
			// " and ((wa_data.isrulehint = 'Y' or wa_data.isrulehint is null) and ( wa_data.pk_psnjob not in(select pk_psnjob from hi_psnjob where "
			condition = " and (((wa_data.isrulehint = 'Y' or " + getBoolNullSql("wa_data.isrulehint")
					+ ") and ( wa_data.pk_psnjob not in(select pk_psnjob from hi_psnjob where "

					+ ruleSql + ") or wa_data.pk_psnjob not in " + wtCond + ")) and hi_psnjob.psntype = 0) ";
			// 档案中已存在的不符合计薪规则人员
			PsnTrnVO[] vos = dao.queryNotInRule(context, condition, orderCondtion);
			map.put(PayfileConstant.NOT_IN_RULE, vos);
		}
		String pk_wa_class = context.getPk_wa_class();
		String cyear = context.getWaYear();
		String cperiod = context.getWaPeriod();
		// 获取最近两个期间的起止日期
		UFDate[] beginAndEndDate = queryDateByPeriod(pk_wa_class, cyear, cperiod);
		UFDate beginDate = beginAndEndDate[0];
		UFDate endDate = beginAndEndDate[1];

		// 查询新进人员或兼职开始人员
		IhrtrnQBS hrtrnQBS = NCLocator.getInstance().lookup(IhrtrnQBS.class);
		UFLiteralDate bgDate = HRWADateConvertor.toUFLiteralDate(beginDate);
		UFLiteralDate edDate = HRWADateConvertor.toUFLiteralDate(endDate);
		PsnTrnVO[] hrAddVOs = hrtrnQBS.queryTRNPsnInf(context.getPk_org(), bgDate, edDate, CommonValue.TRN_ALL_ADD,
				addWhere);
		map.put(PayfileConstant.NOT_IN_WADATA, hrAddVOs);

		return map;
	}

	@Override
	public Map<String, Object> getAboutRuleForPayfile(WaLoginContext context, int trnType) throws BusinessException {
		String addWhere = getAddWhere(context, trnType);

		return getAboutRuleForPayfile(context, addWhere);
	}

	@Override
	public void updatePsnHintFlag(WaLoginContext context, String whereSql, UFBoolean isHint) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		dao.updatePsnHintFlag(context, whereSql, isHint);

	}

	@Override
	public boolean isExistPsns(PayfileVO[] psns, WaClassVO toWaClass) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		PayfileVO[] existvos = dao.queryExistPsns(psns, toWaClass);
		return !ArrayUtils.isEmpty(existvos);
	}

	@Override
	public PeriodVO queryStartEndDate(PeriodVO periodVO) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		return dao.queryStartEndDate(periodVO);
	}

	@Override
	public UFDate[] queryDateByPeriod(String pk_wa_class, String waYear, String waPeriod) throws BusinessException {
		if (pk_wa_class == null || waYear == null || waPeriod == null)
			return null;
		PayfileDAO dao = new PayfileDAO();
		UFDate[] dates = new UFDate[2];
		PeriodVO vo = dao.queryPeriodByWaClass(pk_wa_class, waYear, waPeriod);
		PeriodVO lastVo = getPeriodByendDate(new UFDate(vo.getCstartdate().getDateBefore(1).getMillis()).asEnd());
		if (lastVo == null)
			dates[0] = new UFDate(vo.getCstartdate().getMillis());
		else
			dates[0] = new UFDate(lastVo.getCstartdate().getMillis());
		dates[1] = new UFDate(vo.getCenddate().getMillis());
		return dates;
	}

	/**
	 * 根据结束日期查询期间
	 * 
	 * @param pk_wa_period
	 * @return
	 */
	private PeriodVO getPeriodByendDate(UFDate enddate) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		return dao.getPeriodByendDate(enddate);
	}

	@Override
	public void delPsnbyWaClass(String pk_wa_class) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		dao.delPsnbyWaClass(pk_wa_class);
	}

	/**
	 * 取当前人在父方案中的记录
	 * 
	 * @param loginContext
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public PayfileVO getPsnInPrntClass(WaLoginContext loginContext, String pk_psnjob) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		return dao.getPsnInPrntClass(loginContext.getPk_prnt_class(), loginContext.getWaYear(),
				loginContext.getWaPeriod(), pk_psnjob);
	}

	/**
	 * 判断一个人在其他子方案中是否存在
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	private boolean existInSubClass(PayfileVO vo) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		return dao.existInSubClass(vo.getPk_prnt_class(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod(),
				vo.getPk_psndoc());
	}

	/*
	 * 获取已存在的薪资档案vo，保证传入和传出是一个实例 使用Pk_psndoc判断是否存在
	 */
	private List<PayfileVO> getExistInSubClassList(String fromclass, String toparentclass, String toclass,
			String toyear, String toperiod, PayfileVO[] oldvos) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		List<PayfileVO> existsvos = new ArrayList<PayfileVO>();
		Map<String, PayfileVO> existmap = dao.existInSubClass(toparentclass, toclass, toyear, toperiod, oldvos);
		for (PayfileVO vo : oldvos) {
			if (existmap.containsKey(vo.getPk_psndoc()))
				existsvos.add(vo);
		}

		return existsvos;
	}

	/**
	 * guoqt 判断一个人在其他子方案中是否存在（其他子方案为不停发）
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	private boolean existInSubClassStopFlag(PayfileVO vo) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		return dao.existInSubClassStopFlag(vo.getPk_prnt_class(), vo.getPk_wa_class(), vo.getCyear(), vo.getCperiod(),
				vo.getPk_psndoc());
	}

	/**
	 * 判断一个人在其他子方案中是否存在
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public boolean existInSubClass(WaLoginContext loginContext, String pk_psndoc) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		return dao.existInSubClass(loginContext.getPk_prnt_class(), loginContext.getPk_wa_class(),
				loginContext.getWaYear(), loginContext.getWaPeriod(), pk_psndoc);
	}

	@Override
	public List<WaPayfileDspVO> queryPayfileDisplayInfo(WaLoginContext context) throws BusinessException {
		// 查找个人显示设置
		List<WaPayfileDspVO> dspList = this.queryPayfilePersonalDsp(context);
		if (dspList == null || dspList.isEmpty()) {
			// 没有配置个人显示设置，那么按通用显示
			dspList = this.queryPayfileCommonDsp(context);
		}

		if (dspList == null || dspList.isEmpty()) {
			// 既没有个人显示设置，也没有通用显示设置，就按默认显示
			dspList = PayfileDspUtil.getDefaultDsp();
		} else {
			// 设置item_name
			PayfileDspUtil.setPayfileDisplayName(dspList);
		}
		return dspList;
	}

	/**
	 * 查找通用显示设置
	 * 
	 * @param context
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private List<WaPayfileDspVO> queryPayfileCommonDsp(WaLoginContext context) throws DAOException {
		StringBuffer condtion = new StringBuffer();
		condtion.append(" pk_wa_class='").append(context.getPk_prnt_class()).append("' and type='")
				.append(PayfileDspUtil.commonDsp).append("' ");
		return (List<WaPayfileDspVO>) getBaseDao().retrieveByClause(WaPayfileDspVO.class, condtion.toString(),
				"displayseq");
	}

	/**
	 * 查找分人显示设置
	 * 
	 * @param context
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private List<WaPayfileDspVO> queryPayfilePersonalDsp(WaLoginContext context) throws DAOException {
		StringBuffer condtion = new StringBuffer();
		condtion.append(" pk_user = '").append(context.getPk_loginUser()).append("' and pk_wa_class='")
				.append(context.getPk_prnt_class()).append("' and type='").append(PayfileDspUtil.personalDsp)
				.append("' ");
		return (List<WaPayfileDspVO>) getBaseDao().retrieveByClause(WaPayfileDspVO.class, condtion.toString(),
				"displayseq");
	}

	private void deleteDisplayInfo(WaLoginContext context, String type, Object clascObj) throws BusinessException {
		StringBuffer whereStr = new StringBuffer();
		whereStr.append(" pk_wa_class='").append(context.getPk_prnt_class()).append("' and type='").append(type)
				.append("'");
		if (PayfileDspUtil.personalDsp.equals(type)) {
			whereStr.append(" and pk_user='").append(context.getPk_loginUser()).append("'");
		}
		getBaseDao().deleteByClause(clascObj.getClass(), whereStr.toString());
	}

	@Override
	public void saveDisplayInfo(WaLoginContext context, String type, List<T> yleftList, List<T> yrightList)
			throws BusinessException {
		List<WaDataDspVO> leftList = null;
		Object classObj = new WaPayfileDspVO();
		if (null != yleftList) {
			leftList = (List<WaDataDspVO>) yleftList;
			if (leftList.size() > 0)
				classObj = leftList.get(0);
		}

		List<WaDataDspVO> rightList = null;
		if (null != yrightList) {
			rightList = (List<WaDataDspVO>) yrightList;
			if (rightList.size() > 0)
				classObj = rightList.get(0);
		}
		// 保存之前先删除旧配置
		deleteDisplayInfo(context, type, classObj);
		// 设置已选择的项目
		for (int i = 0; i < rightList.size(); i++) {
			WaDataDspVO vo = rightList.get(i);
			vo.setPk_wa_class(context.getPk_prnt_class());
			vo.setType(type);
			vo.setBshow(UFBoolean.TRUE);
			vo.setDisplayseq(i);
			// 如果type为通用，pk_user的意义仅仅是记录谁创建此记录而已
			vo.setPk_user(context.getPk_loginUser());
			rightList.set(i, vo);
		}
		// 设置待选择的项目
		for (int i = 0; i < leftList.size(); i++) {
			WaDataDspVO vo = leftList.get(i);
			vo.setPk_wa_class(context.getPk_prnt_class());
			vo.setType(type);
			vo.setBshow(UFBoolean.FALSE);
			// 如果type为通用，pk_user的意义仅仅是记录谁创建此记录而已
			vo.setPk_user(context.getPk_loginUser());
			leftList.set(i, vo);
		}
		// 已选择和待选择的合并在同一个List中保存
		rightList.addAll(leftList);
		getBaseDao().insertVOList(rightList);
	}

	@Override
	public List<WaLoginVO> querySynWaClass(WaLoginContext context) throws BusinessException {

		List<WaLoginVO> reList = new ArrayList<WaLoginVO>();
		IWaClass classService = NCLocator.getInstance().lookup(IWaClass.class);
		WaClassVO parentClass = classService.queryParentClass(context.getPk_wa_class(), context.getWaYear(),
				context.getWaPeriod());
		String pk_wa_class = context.getPk_wa_class();
		if (parentClass != null) {
			pk_wa_class = parentClass.getPk_wa_class();
		}
		// 获得有权限的薪资方案
		String condition = " pk_group='" + context.getPk_group() + "' and pk_org='" + context.getPk_org()
				+ "' and stopflag = 'N' and pk_wa_class <> '" + pk_wa_class + "' and pk_wa_class in("
				+ ClassPowerUtil.getClassower(context) + ") ";
		List<WaLoginVO> classList = (List<WaLoginVO>) getBaseDao().retrieveByClause(WaLoginVO.class, condition);

		IWaPub wapub = NCLocator.getInstance().lookup(IWaPub.class);
		if (classList != null && !classList.isEmpty()) {
			for (int i = 0; i < classList.size(); i++) {
				try {
					WaLoginVO vo = classList.get(i);
					PeriodStateVO periodstatevo = new PeriodStateVO();
					periodstatevo.setCyear(vo.getCyear());
					periodstatevo.setCperiod(vo.getCperiod());
					vo.setPeriodVO(periodstatevo);
					vo = wapub.getWaclassVOWithState(vo);
					if (vo.getState() == WaState.NO_WA_DATA_FOUND || vo.getState() == WaState.CLASS_WITHOUT_RECACULATED
							|| vo.getState() == WaState.CLASS_RECACULATED_WITHOUT_CHECK
							|| vo.getState() == WaState.CLASS_PART_CHECKED) {
						reList.add(classList.get(i));
					}
				} catch (BusinessException e) {
					Logger.error(e.getMessage());
				}

			}
		}
		return reList;
	}

	@Override
	public void synToOtherClass(PayfileVO value, List<WaClassVO> classList) throws BusinessException {
		PayfileVO[] listvo = null;
		PayfileDAO dao = new PayfileDAO();
		for (WaClassVO vo : classList) {
			PayfileVO fileVO = new PayfileVO();
			BeanUtils.copyProperties(value, fileVO);
			fileVO.setPk_wa_data(null);

			/*
			 * IWaClass iWaClass =
			 * NCLocator.getInstance().lookup(IWaClass.class); WaClassVO
			 * cureentChildClass =
			 * iWaClass.queryCurrentInludeClass(vo.getPk_wa_class(),
			 * vo.getCyear(), vo.getCperiod()); //多次发放方案，加到当前使用的子方案中，并同时加到父方案中。
			 * if(cureentChildClass != null){
			 */

			fileVO.setPk_wa_class(vo.getPk_wa_class());
			fileVO.setPk_prnt_class(vo.getPk_prnt_class());
			/*
			 * }else{ fileVO.setPk_wa_class(vo.getPk_wa_class()); }
			 */
			fileVO.setCyear(vo.getCyear());
			fileVO.setCperiod(vo.getCperiod());
			fileVO.setCyearperiod(vo.getCyear() + vo.getCperiod());
			listvo = (PayfileVO[]) ArrayUtils.add(listvo, fileVO);
			// if(!new PayfileDAO().existInCurrentClass(fileVO)){
			// this.insert(fileVO);
			// }
		}
		HashMap<String, PayfileVO> map = dao.existInCurrentClass(listvo);
		ArrayList<PayfileVO> insertVOs = new ArrayList<PayfileVO>();
		for (PayfileVO vo : listvo) {
			StringBuffer key = new StringBuffer();
			key.append(vo.getPk_wa_class());
			key.append(vo.getPk_psndoc());
			key.append(vo.getCyearperiod());
			if (!map.containsKey(key.toString())) {
				insertVOs.add(vo);
			}
		}
		if (!insertVOs.isEmpty()) {
			addPsnVOsToOtherClass(insertVOs.toArray(new PayfileVO[0]));
		}
	}

	@Override
	public void batchSynToOtherClass(PayfileVO[] payfileVOs, List<WaClassVO> classList) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		for (WaClassVO vo : classList) {
			List<PayfileVO> payfileList = new ArrayList<PayfileVO>();
			PayfileVO[] listvo = null;
			/*
			 * IWaClass iWaClass =
			 * NCLocator.getInstance().lookup(IWaClass.class); WaClassVO
			 * cureentChildClass =
			 * iWaClass.queryCurrentInludeClass(vo.getPk_wa_class(),
			 * vo.getCyear(), vo.getCperiod());
			 */

			for (PayfileVO value : payfileVOs) {
				PayfileVO fileVO = new PayfileVO();
				BeanUtils.copyProperties(value, fileVO);
				fileVO.setPk_wa_data(null);
				// 多次发放方案，加到当前使用的子方案中，并同时加到父方案中。

				fileVO.setPk_wa_class(vo.getPk_wa_class());
				fileVO.setPk_prnt_class(vo.getPk_prnt_class());

				fileVO.setCyear(vo.getCyear());
				fileVO.setCperiod(vo.getCperiod());
				fileVO.setCyearperiod(vo.getCyear() + vo.getCperiod());
				listvo = (PayfileVO[]) ArrayUtils.add(listvo, fileVO);
				// //验证是否已经存在
				// if(!new PayfileDAO().existInCurrentClass(fileVO)){
				// payfileList.add(fileVO);
				// }
			}
			HashMap<String, PayfileVO> map = dao.existInCurrentClass(listvo);
			ArrayList<PayfileVO> insertVOs = new ArrayList<PayfileVO>();
			for (PayfileVO vo2 : listvo) {
				StringBuffer key = new StringBuffer();
				key.append(vo2.getPk_wa_class());
				key.append(vo2.getPk_psndoc());
				key.append(vo2.getCyearperiod());
				if (!map.containsKey(key.toString())) {
					insertVOs.add(vo2);
				}
			}
			if (!insertVOs.isEmpty()) {
				addPsnVOsToOtherClass(insertVOs.toArray(new PayfileVO[0]));
			}
			// //批量保存
			// if(!payfileList.isEmpty()){
			// this.addPsnVOs(payfileList.toArray(new PayfileVO[0]));
			// }
		}
	}

	@Override
	public void synDelete(PayfileVO[] vos, List<WaClassVO> classList) throws BusinessException {
		if (!ArrayUtils.isEmpty(vos) && classList != null && !classList.isEmpty()) {
			StringBuffer condition = new StringBuffer();
			WaClassVO classVO = null;
			for (int i = 0; i < vos.length; i++) {
				condition.append(" (pk_psndoc='").append(vos[i].getPk_psndoc()).append("' and (");
				for (int j = 0; j < classList.size(); j++) {
					classVO = classList.get(j);
					if (j != 0) {
						condition.append(" or ");
					}
					condition.append(" (pk_wa_class='").append(classVO.getPk_wa_class()).append("' and cyear='")
							.append(classVO.getCyear()).append("' and cperiod='").append(classVO.getCperiod())
							.append("') ");
				}
				condition.append(")) or");
			}
			PayfileVO[] delVOs = getServiceTemplate().queryByCondition(PayfileVO.class,
					condition.substring(0, condition.length() - 2));
			if (delVOs != null) {
				for (PayfileVO delVO : delVOs) {
					delVO.setPk_prnt_class(classVO.getPk_prnt_class());
				}
			}

			this.delPsnVOs(delVOs);
		}

	}

	/**
	 * 取得版本信息
	 * 
	 * @param vo
	 * @return
	 * @throws DAOException
	 */
	@Override
	public PayfileVO getPayfileVersionInfo(PayfileVO vo) throws DAOException {
		if (vo.getFipedit().booleanValue()) {
			if (!StringUtils.isEmpty(vo.getPk_financeorg())) {
				OrgVO orgVO = (OrgVO) getBaseDao().retrieveByPK(OrgVO.class, vo.getPk_financeorg());
				vo.setFiporgvid(orgVO.getPk_vid());
			}
			if (!StringUtils.isEmpty(vo.getPk_financedept())) {
				DeptVO deptVO = (DeptVO) getBaseDao().retrieveByPK(DeptVO.class, vo.getPk_financedept());
				vo.setFipdeptvid(deptVO.getPk_vid());
			}
		}

		// 陈本部门已经是版本pk了，不需要重新查询版本，否则会出现错误！
		if (vo.getLibedit().booleanValue()) {
			if (!StringUtils.isEmpty(vo.getPk_liabilitydept())) {
				vo.setLibdeptvid(vo.getPk_liabilitydept());
			}
		}
		return vo;
	}

	@Override
	public boolean existPsnInClass(WaLoginVO waloginvo) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		return dao.existPsnInClass(waloginvo.getPk_wa_class(), waloginvo.getCyear(), waloginvo.getCperiod());
	}

	@Override
	public boolean queryIsTaxTableMust(String pk_wa_class) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		return dao.queryIsTaxTableMust(pk_wa_class);
	}

	@Override
	public void deleteFromTotal(WaClassVO vo) throws BusinessException {
		PayfileDAO dao = new PayfileDAO();
		dao.deleteFromTotal(vo);

	}

}