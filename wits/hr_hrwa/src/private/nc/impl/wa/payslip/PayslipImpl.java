package nc.impl.wa.payslip;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import nc.bs.bd.baseservice.md.VOArrayUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pub.pf.PfMailAndSMSUtil;
import nc.hr.frame.persistence.BaseDAOManager;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.itf.bd.currtype.ICurrtypeQuery;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hr.wa.IClassItemQueryService;
import nc.itf.hr.wa.IHRWADataResCode;
import nc.itf.hr.wa.IItemQueryService;
import nc.itf.hr.wa.IPaydataQueryService;
import nc.itf.hr.wa.IPayrollManageService;
import nc.itf.hr.wa.IPayslipService;
import nc.itf.hr.wa.IPayslipService4Mobile;
import nc.itf.hr.wa.IWaClass;
import nc.itf.hr.wa.IWaPub;
import nc.itf.hr.wa.SalaryreportUtil;
import nc.itf.hr.wa.SendResultVO;
import nc.itf.hr.wa.WaPowerSqlHelper;
import nc.itf.hrss.pub.profile.IProfileService;
import nc.itf.org.IOrgUnitQryService;
import nc.itf.uap.busibean.ISysInitQry;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.itf.uap.sf.ICreateCorpQueryService;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.md.data.access.NCObject;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceService;
import nc.md.persist.framework.MDPersistenceService;
import nc.message.util.MessageSender;
import nc.vo.bd.currtype.CurrtypeVO;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.para.SysInitVO;
import nc.vo.pub.print.PrintTempletmanageItemVO;
import nc.vo.sm.UserVO;
import nc.vo.uif2.LoginContext;
import nc.vo.wa.category.WaClassVO;
import nc.vo.wa.category.WaInludeclassVO;
import nc.vo.wa.classitem.WaClassItemVO;
import nc.vo.wa.classitempower.ItemPowerUtil;
import nc.vo.wa.classpower.ClassPowerUtil;
import nc.vo.wa.classprint.WaClassPrintVO;
import nc.vo.wa.item.WaItemConstant;
import nc.vo.wa.item.WaItemVO;
import nc.vo.wa.paydata.DataVO;
import nc.vo.wa.payroll.PayrollVO;
import nc.vo.wa.payslip.AggPayslipVO;
import nc.vo.wa.payslip.MyPayslipVO;
import nc.vo.wa.payslip.PaySlipItemValueVO;
import nc.vo.wa.payslip.PayslipHtmlGenerator;
import nc.vo.wa.payslip.PayslipItemVO;
import nc.vo.wa.payslip.PayslipTemplate;
import nc.vo.wa.payslip.PayslipVO;
import nc.vo.wa.payslip.SelfEmpPayslipVO;
import nc.vo.wa.payslip.SendTypeEnum;
import nc.vo.wa.period.PeriodVO;
import nc.vo.wa.pub.HRWACommonConstants;
import nc.vo.wa.pub.ParaConstant;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaLoginContext;
import nc.vo.wa.pub.WaLoginVO;
import nc.vo.wa.pub.WaState;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

/**
 * Created on 2008-12-11
 *
 * @author zhangg
 */
public class PayslipImpl implements IPayslipService,IPayslipService4Mobile
{
	private static final String ITEM= "item";
	private PayslipDAO payslipDao;
	private IMDPersistenceService mdService = null;


	private BaseDAO dao;

	//参数查询接口
	private static ISysInitQry sysInitQry;

	//查询用户VO接口
	private static IUserManageQuery userQryServ;

	// 参数代码
	private String PARAM_INITCODE = "HRWA012";

	private final String TABLE_START = "<table border=1 cellspacing=0 cellpadding=3 frame=box>";
	private final String TABLE_END = "</table>";
	private final String TR_START = "<tr>";
	private final String TR_START_COLOR = "<tr style='background-color:#B7C2CA;'>";
	private final String TR_END = "</tr>";
	private final String TD_START = "<td nowrap=nowrap height=40>";
	private final String TD_START_RIGHT = "<td nowrap=nowrap height=40 align='right'>";
	private final String TD_END = "</td>";

	public final String TABLE_WACLASSITEM = "wa_classitem";
	public final String TABLE_WADATA = "wa_data";

	public final String ITEM_TAB_C_EMAIL = "email";
	public final String ITEM_TAB_C_MOBILE = "mobile";
	public final String ITEM_TAB_C_NAME = "name";


	private IMDPersistenceService getMdService()
	{
		if (mdService == null)
		{
			mdService = NCLocator.getInstance().lookup(IMDPersistenceService.class);
		}
		return mdService;
	}

	public PayslipDAO getPayslipDao() {
		if(payslipDao==null){
			payslipDao = new PayslipDAO();
		}
		return payslipDao;
	}

	public void setPayslipDao(PayslipDAO payslipDao) {
		this.payslipDao = payslipDao;
	}

	/**
	 * 查询聚合薪资条数据
	 */
	@Override
	public AggPayslipVO[] queryAggPayslipByCond(String condition, LoginContext context) throws BusinessException
	{
		IPersistenceRetrieve persistence = NCLocator.getInstance().lookup(IPersistenceRetrieve.class);

		PayslipVO[] parentVOs = queryLastPeriodPaylipVos(context);
		if (parentVOs == null)
		{
			return new AggPayslipVO[0];
		}
		SalaryreportUtil util = new SalaryreportUtil();
		AggPayslipVO[] vos = new AggPayslipVO[parentVOs.length];
		//guopeng 优化薪资条效率，多个薪资条，查当前组织下所有
		HashMap<String, String> itemmap = util.getColId2FullColNameMap(context, null);
		for (int i = 0; i < parentVOs.length; i++)
		{
			// 当薪资条为多方案薪资条时，拼接所有的薪资方案名称一并显示
			String joinName = "";

			parentVOs[i].setTitle(util.parseCode2Text(parentVOs[i].getTitle(), context, itemmap));
			parentVOs[i].setTail(util.parseCode2Text(parentVOs[i].getTail(), context, itemmap));
//			20150730 xiejie3 补丁合并，NCdp205425993薪资条管理节点未按薪资方案权限控制begin
			//shenliangc 20140626  解决薪资条按当前用户关联角色的薪资方案权限过滤问题。
			//同时将薪资条中发放项目按照当前用户关联角色的发放项目权限进行过滤。
			WaLoginContext waLoginContext = (WaLoginContext) context;
			waLoginContext.getWaLoginVO().setPk_wa_class(parentVOs[i].getPk_wa_class());
			String classItemPowerSql = " and ((item_table <> 'wa_data') or (item_table = 'wa_data' and slip_item in " 
										+ " ( select itemkey from wa_item where pk_wa_item in " 
										+		" ( " + getItemPower(waLoginContext) + " )"
										+ 	" ) "
										//20160115 shenliangc NCdp205573919 设置薪资条后，查询界面中少了薪资项目下的字段【年度】【期间】
										+ " ) or (item_table = 'wa_data' and slip_item in('cyear', 'cperiod')))";
//			end

			PayslipItemVO[] payslipItemVOs = (PayslipItemVO[]) persistence
					.retrieveByClause(
							null,
							PayslipItemVO.class,
//							20150730 xiejie3，补丁合并，NCdp205425993薪资条管理节点未按薪资方案权限控制，begin
							PayslipItemVO.PK_PAYSLIP + "='" + parentVOs[i].getPk_payslip() + "' "
							//shenliangc 20140626  解决薪资条按当前用户关联角色的薪资方案权限过滤问题。
							//同时将薪资条中发放项目按照当前用户关联角色的发放项目权限进行过滤。
							+ classItemPowerSql
							+ " order by showorder asc");
//							end
//							PayslipItemVO.PK_PAYSLIP + "='"
//									+ parentVOs[i].getPk_payslip()
//									+ "'  order by showorder asc");
			for (PayslipItemVO payslipItemVO : payslipItemVOs)
			{
				if (payslipItemVO != null)
				{
					joinName += joinName.equals("") ? payslipItemVO.getItem_displayname() : "," + payslipItemVO.getItem_displayname();
				}
			}
			parentVOs[i].setJoinname(joinName);
			vos[i] = new AggPayslipVO();
			vos[i].setParentVO(parentVOs[i]);
			vos[i].setTableVO(PayslipImpl.ITEM, payslipItemVOs);
		}
		return vos;
	}
	//20150730 xiejie3, 补丁合并，NCdp205425993薪资条管理节点未按薪资方案权限控制begin
	//shenliangc 20140626  解决薪资条按当前用户关联角色的薪资方案权限过滤问题。
	//同时将薪资条中发放项目按照当前用户关联角色的发放项目权限进行过滤。
	public static String getItemPower(WaLoginContext context){
		return "select distinct  pk_wa_item from wa_itempower where pk_wa_class = '"+context.getWaLoginVO().getPk_wa_class()
				+"' and (pk_subject in ( select pk_role from sm_user_role where cuserid = '"+context.getPk_loginUser()+"' ) " 
				+" or pk_subject = '"+context.getPk_loginUser()+"' ) " 
				+" and pk_group = '"+context.getPk_group()+"' and pk_org = '"+context.getPk_org()+"'";
	}
	//end
	@Override
	public AggPayslipVO queryAggPayslipVOByCon(String condition, LoginContext context)
			throws BusinessException {
		IPersistenceRetrieve persistence = NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
		PayslipVO[] parentVOs = ((List<PayslipVO>)getDao().retrieveByClause(PayslipVO.class, condition)).toArray(new PayslipVO[0]);
		if (parentVOs != null && parentVOs.length > 0) {
			PayslipVO vo = parentVOs[0];
			AggPayslipVO aggVO = new AggPayslipVO();
			SalaryreportUtil util = new SalaryreportUtil();
			// 当薪资条为多方案薪资条时，拼接所有的薪资方案名称一并显示
			String joinName = "";
			//guopeng 优化薪资条效率
			HashMap<String, String> itemmap = util.getColId2FullColNameMap(context, vo);
			vo.setTitle(util.parseCode2Text(vo.getTitle(), context, itemmap));
			vo.setTail(util.parseCode2Text(vo.getTail(), context, itemmap));

			PayslipItemVO[] payslipItemVOs = (PayslipItemVO[]) persistence
					.retrieveByClause(
							null,
							PayslipItemVO.class,
							PayslipItemVO.PK_PAYSLIP + "='"
									+ vo.getPk_payslip()
									+ "' order by showorder asc");
			for (PayslipItemVO payslipItemVO : payslipItemVOs)
			{
				if (payslipItemVO != null)
				{
					joinName += joinName.equals("") ? payslipItemVO.getItem_displayname() : "," + payslipItemVO.getItem_displayname();
				}
			}
			vo.setJoinname(joinName);
			aggVO.setParentVO(vo);
			aggVO.setTableVO(PayslipImpl.ITEM, payslipItemVOs);
			return aggVO;
		}

		return null;
	}

	/**
	 * 查询聚合薪资条数据根据薪资方案,薪资期间,发放批次
	 */
	@Override
	public AggPayslipVO queryAggPayslipByCond(String strPK_wa_class,String strYear, String strMonth, Integer intType, LoginContext context) throws BusinessException
	{
		IPersistenceRetrieve persistence = NCLocator.getInstance().lookup(IPersistenceRetrieve.class);

		StringBuffer sbSql  = new StringBuffer();
		sbSql.append("select accmonth, accyear, dr, pk_group, pk_org, pk_payslip, pk_wa_class, title,tail, ts, type   from wa_payslip where ");
		sbSql.append(PayslipVO.ACCYEAR).append(" = '").append(strYear).append("' and ");
		sbSql.append(PayslipVO.ACCMONTH).append(" = '").append(strMonth).append("' and ");
		sbSql.append(PayslipVO.PK_WA_CLASS).append(" = '").append(strPK_wa_class).append("' and ");
		sbSql.append(PayslipVO.TYPE).append(" = ").append(intType).append(" and ");
		sbSql.append(PayslipVO.PK_GROUP).append(" = '").append(context.getPk_group()).append("' and ");
		sbSql.append(PayslipVO.PK_ORG).append(" = '").append(context.getPk_org()).append("' ");

		PayslipVO payslipvo =  new BaseDAOManager().executeQueryVO(sbSql.toString(), PayslipVO.class);
		//		if (payslipvo == null)
		//		{
		//			//如果工资条为空，查询以前的工资条
		//			StringBuffer sbSql2  = new StringBuffer();
		//			sbSql2.append("select accmonth, accyear, dr, pk_group, pk_org, pk_payslip, pk_wa_class, title,tail, ts, type   from wa_payslip where ");
		//			sbSql2.append(" accyear||accmonth = (select max(accyear||accmonth )from wa_payslip where  pk_wa_class = '"+strPK_wa_class+"' and type = "+intType+" and pk_group = '"+context.getPk_group()+"' and pk_org = '"+
		//					context.getPk_org()+"' ").append(") and ");
		//			sbSql2.append(PayslipVO.PK_WA_CLASS).append(" = '").append(strPK_wa_class).append("' and ");
		//			sbSql2.append(PayslipVO.TYPE).append(" = ").append(intType).append(" and ");
		//			sbSql2.append(PayslipVO.PK_GROUP).append(" = '").append(context.getPk_group()).append("' and ");
		//			sbSql2.append(PayslipVO.PK_ORG).append(" = '").append(context.getPk_org()).append("' ");
		//			payslipvo =  new BaseDAOManager().executeQueryVO(sbSql2.toString(), PayslipVO.class);;
		//		}
		if(payslipvo == null){
			return null;
		}

		SalaryreportUtil util = new SalaryreportUtil();
		AggPayslipVO vo = new AggPayslipVO();
		{
			// 当薪资条为多方案薪资条时，拼接所有的薪资方案名称一并显示
			String joinName = "";
			//guopeng 优化薪资条效率
			HashMap<String, String> itemmap = util.getColId2FullColNameMap(context, payslipvo);
			payslipvo.setTitle(util.parseCode2Text(payslipvo.getTitle(), context, itemmap));
			payslipvo.setTail(util.parseCode2Text(payslipvo.getTail(), context, itemmap));
			//20150730 xiejie3，补丁合并，NCdp205425993薪资条管理节点未按薪资方案权限控制begin
			//shenliangc 20140626  解决薪资条按当前用户关联角色的薪资方案权限过滤问题。
			//同时将薪资条中发放项目按照当前用户关联角色的发放项目权限进行过滤。
			WaLoginContext waLoginContext = (WaLoginContext) context;
			waLoginContext.getWaLoginVO().setPk_wa_class(payslipvo.getPk_wa_class());
			String classItemPowerSql = " and ((item_table <> 'wa_data') or (item_table = 'wa_data' and slip_item in " 
										+ " ( select itemkey from wa_item where pk_wa_item in " 
										+		" ( " + getItemPower(waLoginContext) + " )"
										+ 	" ) "
										//20160115 shenliangc NCdp205573919 设置薪资条后，查询界面中少了薪资项目下的字段【年度】【期间】
										+ " ) or (item_table = 'wa_data' and slip_item in('cyear', 'cperiod')))";
			//end
			PayslipItemVO[] payslipItemVOs = (PayslipItemVO[]) persistence
					.retrieveByClause(
							null,
							PayslipItemVO.class,
							//20150730 xiejie3，补丁合并，NCdp205425993薪资条管理节点未按薪资方案权限控制begin							
							PayslipItemVO.PK_PAYSLIP + "='" + payslipvo.getPk_payslip() + "' "
							//shenliangc 20140626  解决薪资条按当前用户关联角色的薪资方案权限过滤问题。
							//同时将薪资条中发放项目按照当前用户关联角色的发放项目权限进行过滤。
							+ classItemPowerSql
							+ " order by showorder asc");
							//end
//							PayslipItemVO.PK_PAYSLIP + "='"
//									+ payslipvo.getPk_payslip()
//									+ "' order by showorder asc");
			//			IClassItemQueryService service = NCLocator.getInstance().lookup(IClassItemQueryService.class);
			//			IWaClass waClass = NCLocator.getInstance().lookup(IWaClass.class);
			//			WaClassVO waclassVo = waClass.queryWaClassByPK(strPK_wa_class);
			//
			//			if (waclassVo.getMutipleflag() != null && waclassVo.getMutipleflag().booleanValue())
			//			{
			//				StringBuffer sbItemSql = new StringBuffer();
			//				sbItemSql.append(" pk_wa_parentclass =  '").append(strPK_wa_class).append("' ");
			//				sbItemSql.append(" and cyear =  '").append(strYear).append("' ");
			//				sbItemSql.append(" and  cperiod =  '").append(strMonth).append("' ");
			//				sbItemSql.append(" and  batch =  ").append(intType).append(" ");
			//				WaInludeclassVO classVo = (WaInludeclassVO)new BaseDAO().retrieveByClause(WaInludeclassVO.class, sbItemSql.toString());
			//				if (classVo != null)
			//				{
			//					strPK_wa_class = classVo.getPk_childclass();
			//				}
			//			}
			//
			//			String classitem_cond = " pk_wa_class = '" + strPK_wa_class + "' and cyear ='" + strYear + "' and cperiod='" + strMonth + "'";
			//			WaClassItemVO[] itemVOs = service.queryByCondition(context, classitem_cond);
			//			List<String> list= new ArrayList<String>();

			for (PayslipItemVO payslipItemVO : payslipItemVOs)
			{
				if (payslipItemVO != null)
				{
					joinName += joinName.equals("") ? payslipItemVO.getItem_displayname() : "," + payslipItemVO.getItem_displayname();
				}
			}
			payslipvo.setJoinname(joinName);
			vo = new AggPayslipVO();
			vo.setParentVO(payslipvo);
			vo.setTableVO(PayslipImpl.ITEM, payslipItemVOs);
		}
		return vo;
	}

	/**
	 * 用于薪资薪资条和修改薪资条
	 */
	public AggPayslipVO queryAggPayslipByCond4AddOrEdit(String strPK_wa_class,String strYear, String strMonth, Integer intType, LoginContext context) throws BusinessException
	{
		IPersistenceRetrieve persistence = NCLocator.getInstance().lookup(IPersistenceRetrieve.class);

		StringBuffer sbSql  = new StringBuffer();
		sbSql.append("select accmonth, accyear, dr, pk_group, pk_org, pk_payslip, pk_wa_class, title,tail, ts, type   from wa_payslip where ");
		sbSql.append(PayslipVO.ACCYEAR).append(" = '").append(strYear).append("' and ");
		sbSql.append(PayslipVO.ACCMONTH).append(" = '").append(strMonth).append("' and ");
		sbSql.append(PayslipVO.PK_WA_CLASS).append(" = '").append(strPK_wa_class).append("' and ");
		sbSql.append(PayslipVO.TYPE).append(" = ").append(intType).append(" and ");
		sbSql.append(PayslipVO.PK_GROUP).append(" = '").append(context.getPk_group()).append("' and ");
		sbSql.append(PayslipVO.PK_ORG).append(" = '").append(context.getPk_org()).append("' ");

		PayslipVO payslipvo =  new BaseDAOManager().executeQueryVO(sbSql.toString(), PayslipVO.class);
		//		if (payslipvo == null)
		//		{
		//			//如果工资条为空，查询以前的工资条
		//			StringBuffer sbSql2  = new StringBuffer();
		//			sbSql2.append("select accmonth, accyear, dr, pk_group, pk_org, pk_payslip, pk_wa_class, title,tail, ts, type   from wa_payslip where ");
		//			sbSql2.append(" accyear||accmonth = (select max(accyear||accmonth )from wa_payslip where  pk_wa_class = '"+strPK_wa_class+"' and type = "+intType+" and pk_group = '"+context.getPk_group()+"' and pk_org = '"+
		//					context.getPk_org()+"' ").append(") and ");
		//			sbSql2.append(PayslipVO.PK_WA_CLASS).append(" = '").append(strPK_wa_class).append("' and ");
		//			sbSql2.append(PayslipVO.TYPE).append(" = ").append(intType).append(" and ");
		//			sbSql2.append(PayslipVO.PK_GROUP).append(" = '").append(context.getPk_group()).append("' and ");
		//			sbSql2.append(PayslipVO.PK_ORG).append(" = '").append(context.getPk_org()).append("' ");
		//			payslipvo =  new BaseDAOManager().executeQueryVO(sbSql2.toString(), PayslipVO.class);;
		//		}
		if(payslipvo == null){
			return null;
		}

		SalaryreportUtil util = new SalaryreportUtil();
		AggPayslipVO vo = new AggPayslipVO();
		{
			// 当薪资条为多方案薪资条时，拼接所有的薪资方案名称一并显示
			String joinName = "";
			//guopeng 优化薪资条效率
			HashMap<String, String> itemmap = util.getColId2FullColNameMap(context, payslipvo);
			payslipvo.setTitle(util.parseCode2Text(payslipvo.getTitle(), context, itemmap));
			payslipvo.setTail(util.parseCode2Text(payslipvo.getTail(), context, itemmap));
			PayslipItemVO[] payslipItemVOs = (PayslipItemVO[]) persistence
					.retrieveByClause(
							null,
							PayslipItemVO.class,
							PayslipItemVO.PK_PAYSLIP + "='"
									+ payslipvo.getPk_payslip()
									+ "' order by showorder asc");
			for (PayslipItemVO payslipItemVO : payslipItemVOs)
			{
				if (payslipItemVO != null)
				{
					joinName += joinName.equals("") ? payslipItemVO.getItem_displayname() : "," + payslipItemVO.getItem_displayname();
				}
			}
			payslipvo.setJoinname(joinName);
			vo = new AggPayslipVO();
			vo.setParentVO(payslipvo);
			vo.setTableVO(PayslipImpl.ITEM, payslipItemVOs);
		}
		return vo;
	}


	/**
	 * 查询已经创建的最新的薪资条
	 * @param condition
	 * @param context
	 * @return
	 * @throws BusinessException
	 */
	private PayslipVO[] queryLastPeriodPaylipVos(LoginContext context) throws BusinessException
	{
		StringBuffer sbbuf = new StringBuffer();
		sbbuf.append(" select ");
		sbbuf.append(" 	wa_payslip.*  ");
		sbbuf.append(" from ");
		sbbuf.append(" 	wa_payslip  ");
		sbbuf.append(" 		inner join (select ");
		sbbuf.append(" 						max( accyear || accmonth) as monthday, ");
		sbbuf.append(" 						pk_wa_class,TYPE  ");
		sbbuf.append(" 					from ");
		sbbuf.append(" 						wa_payslip slip  ");
		sbbuf.append(" 					where ");
		sbbuf.append(" 						slip.pk_group=? and ");
		sbbuf.append(" 						slip.pk_org=?  ");
		sbbuf.append(" 					group by ");
		sbbuf.append(" 					slip.pk_wa_class,slip.TYPE ");
		sbbuf.append(" 	) ");
		sbbuf.append(" 	as tt  ");
		sbbuf.append(" 	on tt.pk_wa_class = wa_payslip.pk_wa_class  and tt.type = wa_payslip.type  ");
		sbbuf.append(" where ");
		sbbuf.append(" 	wa_payslip.pk_group=? and ");
		sbbuf.append(" wa_payslip.pk_org=? and ");
		sbbuf.append(" (wa_payslip.ACCYEAR||wa_payslip.accmonth) = tt.monthday ");
		//20150730 xiejie3 补丁合并，NCdp205425993薪资条管理节点未按薪资方案权限控制，begin
		//shenliangc 20140624 解决薪资条按当前用户关联角色的薪资方案权限过滤问题。
		sbbuf.append(" and wa_payslip.pk_wa_class in (" + ClassPowerUtil.getClassower(context) + ")");
		//end
		SQLParameter para = new SQLParameter();
		para.addParam(context.getPk_group());
		para.addParam(context.getPk_org());
		para.addParam(context.getPk_group());
		para.addParam(context.getPk_org());
		return new BaseDAOManager().executeQueryVOs(sbbuf.toString(), para, PayslipVO.class);
	}
	@Override
	public void delete(AggPayslipVO vo) throws BusinessException
	{
		getMdService().deleteBillFromDB(vo);
		// getServiceTemplate().delete(vo);
	}

	@Override
	public String insertAggPayslipVO(AggPayslipVO aggVO) throws BusinessException
	{
		try
		{
			IPersistenceUpdate persistence = NCLocator.getInstance().lookup(IPersistenceUpdate.class);
			// 插入wa_payslip表，薪资条主表，包括主题等信息
			StringBuffer sbSql = new StringBuffer();
			sbSql.append(" select 1 from  wa_payslip where ");
			sbSql.append(" pk_wa_class = ? and ");
			sbSql.append("  accyear = ? and ");
			sbSql.append("  accmonth = ? and ");
			sbSql.append("  type = ? and ");
			sbSql.append("  pk_group = ? and ");
			sbSql.append("  pk_org = ?");
			PayslipVO vo = (PayslipVO) aggVO.getParentVO();
			SQLParameter para = new SQLParameter();
			para.addParam(vo.getPk_wa_class());
			para.addParam(vo.getAccyear());
			para.addParam(vo.getAccmonth());
			para.addParam(vo.getType());
			para.addParam(vo.getPk_group());
			para.addParam(vo.getPk_org());
			if (new BaseDAOManager().isValueExist(sbSql.toString(), para))
			{
				Logger.debug("同一薪资类别同一薪资期间同一发送方式只能制定一个薪资条!");
				throw new BusinessException(ResHelper.getString("6013payslp","06013payslp0171")/*@res "同一薪资类别同一薪资期间同一发送方式只能制定一个薪资条!"*/);
			}

			//设置工资条的期间是否是最新期间，若不是则该工资条需要同步到后续期间。
			IWaClass waclass = NCLocator.getInstance().lookup(IWaClass.class);
			WaClassVO classVO = waclass.queryWaClassByPK(vo.getPk_wa_class());
			if(!classVO.getCyear().equals(vo.getAccyear()) || !classVO.getCperiod().equals(vo.getAccmonth())){
				syncAggPayslipVO(aggVO,classVO.getCyear(),classVO.getCperiod());
			}

			String pk_payslip = persistence.insertVO(null, (PayslipVO) aggVO.getParentVO(), null);

			PayslipItemVO[] itemVOs = (PayslipItemVO[]) aggVO.getTableVO(PayslipImpl.ITEM);
			if (itemVOs != null)
			{
				for (int i = 0; i < itemVOs.length; i++)
				{
					itemVOs[i].setPk_payslip(pk_payslip);
				}
				// 插入wa_payslip_detail，薪资条项目表，包括薪资项目（所选薪资方案涉及的公共薪资项目）和系统预置的项目
				persistence.insertVOArray(null, itemVOs, null);
			}
			return pk_payslip;
		}
		catch (Exception e)
		{
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
	}
	
	@Override
	public String saveAggPayslipVO(AggPayslipVO aggVO, LoginContext context, boolean blnUpdateFlag) throws BusinessException
	{
		String pk_payslip;
		PayslipVO vo = (PayslipVO) aggVO.getParentVO();
		pk_payslip = vo.getPk_payslip();
		//guopeng 优化薪资条效率，多个薪资条，查当前组织下所有
		SalaryreportUtil util = new SalaryreportUtil();
		HashMap<String, String> itemmap = util.getColId2FullColNameMap(context, vo);
		vo.setTitle(util.parseCode2Text(vo.getTitle(), context, itemmap));
		vo.setTail(util.parseCode2Text(vo.getTail(), context, itemmap));

		if (pk_payslip == null)
		{
			pk_payslip = insertAggPayslipVO(aggVO);
		}
		else
		{
			updateAggPayslipVOAfterPoier(aggVO,blnUpdateFlag);
		}
		return pk_payslip;
	}

	private void updateAggPayslipVO(AggPayslipVO aggVO) throws BusinessException
	{
		try
		{
			IPersistenceUpdate persistence = NCLocator.getInstance().lookup(IPersistenceUpdate.class);
			PayslipVO vo = (PayslipVO) aggVO.getParentVO();
			// 更新wa_payslip表，薪资条主表，包括主题等信息
			persistence.updateVO(null, vo, null, null);

			// 先清除在插入子表信息
			persistence.executeSQLs(new String[]
					{ "delete from wa_payslip_class where pk_payslip='" + vo.getPk_payslip() + "'" });
			persistence.executeSQLs(new String[]
					{ "delete from wa_payslip_item where pk_payslip='" + vo.getPk_payslip() + "'" });

			PayslipItemVO[] itemVOs = (PayslipItemVO[]) aggVO.getTableVO(PayslipImpl.ITEM);
			if (itemVOs != null)
			{
				for (int i = 0; i < itemVOs.length; i++)
				{
					itemVOs[i].setPk_payslip(vo.getPk_payslip());
				}
				// 插入wa_payslip_detail，薪资条项目表，包括薪资项目（所选薪资方案涉及的公共薪资项目）和系统预置的项目
				persistence.insertVOArray(null, itemVOs, null);
			}

		}
		catch (Exception e)
		{
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * 设置工资条的期间早于最新业务期间，设置的该工资条要同步到后续期间。
	 * @param aggVO
	 * @param lastCYear  最新期间年
	 * @param lastCPeriod  最新期间
	 * @throws BusinessException
	 */
	private void syncAggPayslipVO(AggPayslipVO aggPayslipVO,String lastCYear,String lastCPeriod) throws BusinessException {
		// 为解决当前设置的期间和最新期间中间，已有薪资条的情况.这时，只同步到最近的薪资条的期间为止。
		AggPayslipVO nextVO = queryNextAggPayslipVO(aggPayslipVO);
		if(nextVO != null){
			lastCYear = ((PayslipVO)nextVO.getParentVO()).getAccyear();
			lastCPeriod = ((PayslipVO)nextVO.getParentVO()).getAccmonth();
		}

		//update wa_payslip set title = '' ,tail = '' where pk_wa_class = '' and type =  and  期间限制

		//同步项目

		PeriodVO[] periodVOs = getPayslipDao().queryPeriodVOs(aggPayslipVO, lastCYear, lastCPeriod);

		IPersistenceUpdate persistence = NCLocator.getInstance().lookup(IPersistenceUpdate.class);
		if(!ArrayUtils.isEmpty(periodVOs)){

			//薪资条
			PayslipVO[] payslipVOs = new PayslipVO[periodVOs.length];

			//薪资条项目
			PayslipItemVO[] itemVOs = (PayslipItemVO[]) aggPayslipVO.getTableVO(PayslipImpl.ITEM);
			List<PayslipItemVO> list = new ArrayList<PayslipItemVO>();

			//组装薪资条数据
			for(int index = 0; index < periodVOs.length; index++){
				PayslipVO payslipVO = new PayslipVO();
				BeanUtils.copyProperties(aggPayslipVO.getParentVO(),payslipVO);
				payslipVO.setAccyear(periodVOs[index].getCyear());
				payslipVO.setAccmonth(periodVOs[index].getCperiod());
				payslipVOs[index] = payslipVO;
			}
			//插入数据并返回薪资条主键
			String[] pk_payslips = persistence.insertVOArray(null,payslipVOs,null);
			//组装薪资条项目数据
			for(int index = 0; index < pk_payslips.length; index++){
				if (itemVOs != null)
				{
					for (int i = 0; i < itemVOs.length; i++){
						//新建对象复制属性那个，不能直接引用
						PayslipItemVO itemVO = new PayslipItemVO();
						BeanUtils.copyProperties(itemVOs[i],itemVO);
						itemVO.setPk_payslip(pk_payslips[index]);
						list.add(itemVO);
					}
				}
			}
			// 插入wa_payslip_detail，薪资条项目表，包括薪资项目（所选薪资方案涉及的公共薪资项目）和系统预置的项目
			persistence.insertVOArray(null, list.toArray(new PayslipItemVO[0]), null);
		}
	}

	@SuppressWarnings("unchecked")
	private AggPayslipVO queryNextAggPayslipVO(AggPayslipVO aggVO) throws MetaDataException{
		PayslipVO slipVO = (PayslipVO) aggVO.getParentVO();

		StringBuffer sbSlipWhere = new StringBuffer("pk_wa_class='").append(
				slipVO.getPk_wa_class()).append("' and (accyear>'").append(
						slipVO.getAccyear()).append("' or (accyear='").append(
								slipVO.getAccyear()).append("' and accmonth>'").append(
										slipVO.getAccmonth()).append("')) ");

		List<AggPayslipVO> aggVOList = (List<AggPayslipVO>) MDPersistenceService
				.lookupPersistenceQueryService().queryBillOfVOByCondWithOrder(
						AggPayslipVO.class, sbSlipWhere.toString(), true,
						false, new String[] { "accyear", "accmonth" });
		if (aggVOList != null && !aggVOList.isEmpty() && aggVOList.size() > 0) {
			// 当前工资条的期间，后续期间中最近的薪资条
			return aggVOList.get(aggVOList.size() - 1);
		}
		return null;
	}

	/**
	 * 更改历史期间薪资条的处理
	 */
	private void updateAggPayslipVOAfterPoier(AggPayslipVO aggVO, boolean blnUpdateFlag) throws BusinessException
	{
		try {
			IPersistenceUpdate persistence = NCLocator.getInstance().lookup(
					IPersistenceUpdate.class);
			PayslipVO vo = (PayslipVO) aggVO.getParentVO();
			StringBuffer sbSql = new StringBuffer();
			sbSql.append(" select * from  wa_payslip where ");
			sbSql.append(" pk_wa_class = ? and ");
			sbSql.append("  accyear = ? and ");
			sbSql.append("  accmonth = ? and ");
			sbSql.append("  type = ? and ");
			sbSql.append("  pk_group = ? and ");
			sbSql.append("  pk_org = ?");

			SQLParameter para = new SQLParameter();
			para.addParam(vo.getPk_wa_class());
			para.addParam(vo.getAccyear());
			para.addParam(vo.getAccmonth());
			para.addParam(vo.getType());
			para.addParam(vo.getPk_group());
			para.addParam(vo.getPk_org());
			PayslipVO paySlipvo = new BaseDAOManager().executeQueryVO(
					sbSql.toString(), para, PayslipVO.class);
			String strPk = null;
			if (paySlipvo == null) {
				vo.setPk_payslip(null);
				strPk = persistence.insertVO(null, vo, null);
			} else {
				paySlipvo.setAccyear(vo.getAccyear());
				paySlipvo.setAccmonth(vo.getAccmonth());
				paySlipvo.setTitle(vo.getTitle());
				paySlipvo.setTail(vo.getTail());
				paySlipvo.setStatus(VOStatus.UPDATED);
				// 更新wa_payslip表，薪资条主表，包括主题等信息
				persistence.updateVO(null, paySlipvo, null, null);
				strPk = paySlipvo.getPk_payslip();
				persistence
				.executeSQLs(new String[] { "delete from wa_payslip_item where pk_payslip='"
						+ strPk + "'" });
			}
			PayslipItemVO[] itemVOs = (PayslipItemVO[]) aggVO
					.getTableVO(PayslipImpl.ITEM);
			if (itemVOs != null) {
				for (int j = 0; j < itemVOs.length; j++) {
					itemVOs[j].setPk_payslip(strPk);
				}
				// 插入wa_payslip_detail，薪资条项目表，包括薪资项目（所选薪资方案涉及的公共薪资项目）和系统预置的项目
				persistence.insertVOArray(null, itemVOs, null);
			}

			if (!blnUpdateFlag) {
				return;
			}


			PeriodVO[] periodVOs = getPayslipDao().queryPeriodVOs4Edit(aggVO);
			if (!ArrayUtils.isEmpty(periodVOs)) {
				StringBuilder sbd1 = new StringBuilder(
						"delete from wa_payslip_item where pk_payslip in (select pk_payslip from wa_payslip where pk_wa_class = '"+vo.getPk_wa_class()+"' and (accyear > '"+vo.getAccyear()+"' or (accyear='"+vo.getAccyear()+"' and accmonth>'"+vo.getAccmonth()+"')) and type = "+vo.getType()+")");
				StringBuilder sbd2 = new StringBuilder(
						"delete from wa_payslip where pk_wa_class = '"+vo.getPk_wa_class()+"' and (accyear > '"+vo.getAccyear()+"' or (accyear='"+vo.getAccyear()+"' and accmonth>'"+vo.getAccmonth()+"')) and type = "+vo.getType());
				persistence.executeSQLs(
						new String[] { sbd1.toString(), sbd2.toString() });
				PeriodVO temp = null;
				//薪资条
				List<PayslipVO> payslipList = new ArrayList<PayslipVO>();
				//薪资条项目
				PayslipItemVO[] items = (PayslipItemVO[]) aggVO.getTableVO(PayslipImpl.ITEM);
				List<PayslipItemVO> list = new ArrayList<PayslipItemVO>();
				for (int i = 0; i < periodVOs.length; i++) {
					temp = periodVOs[i];
					int cyear = Integer.parseInt(temp.getCyear());
					int cperiod = Integer.parseInt(temp.getCperiod());
					int accyear = Integer.parseInt(vo.getAccyear());
					int accperiod = Integer.parseInt(vo.getAccmonth());
					if ((cyear>accyear) || (cyear==accyear && cperiod>accperiod)) {
						PayslipVO payslipVO = new PayslipVO();
						BeanUtils.copyProperties(aggVO.getParentVO(),payslipVO);
						payslipVO.setAccyear(temp.getCyear());
						payslipVO.setAccmonth(temp.getCperiod());
						payslipList.add(payslipVO);
					}
				}
				//插入数据并返回薪资条主键
				String[] pk_payslips = persistence.insertVOArray(null,payslipList.toArray(new PayslipVO[0]),null);
				//组装薪资条项目数据
				for(int index = 0; index < pk_payslips.length; index++){
					if (items != null)
					{
						for (int i = 0; i < items.length; i++){
							//新建对象复制属性那个，不能直接引用
							PayslipItemVO itemVO = new PayslipItemVO();
							BeanUtils.copyProperties(items[i],itemVO);
							itemVO.setPk_payslip(pk_payslips[index]);
							list.add(itemVO);
						}
					}
				}
				// 插入wa_payslip_detail，薪资条项目表，包括薪资项目（所选薪资方案涉及的公共薪资项目）和系统预置的项目
				persistence.insertVOArray(null, list.toArray(new PayslipItemVO[0]), null);
			}

		}

		catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
	}
	

	/***************************************************************************
	 * 创建，临时表，
	 * <br>Created on 2012-9-28 上午10:27:01<br>
	 * @return
	 * @author daicy
	 * @param tempTable 
	 * @throws BusinessException
	 * @throws SQLException
	 * @throws NamingException
	 ***************************************************************************/
	private String createTempTable(String tempTable) throws BusinessException  {

		//创建临时表
		String tableName = HRWACommonConstants.WA_TEMP_QUERYPAYSLIP;

        //pk_wa_class,cyear,cperiod,cstartdate,cenddate 
		//创建临时表
		String  columns= "  pk_wa_class char(20) , cyear char(4), cperiod char(2) , cstartdate char(10), cenddate char(10) " ;
		InSQLCreator inSQLCreator = new InSQLCreator();

		try
		{

			//此表是临时表，事务结束后会自动清空！！

			tableName = inSQLCreator.createTempTable(tableName, columns,null);

			String  columns2= "  pk_wa_class , cyear , cperiod  , cstartdate, cenddate   ";


			StringBuilder sbd = new StringBuilder(" insert into  "+tableName+"   ("+columns2+") " +
					"select distinct wa_waclass.pk_wa_class,wa_period.cyear,wa_period.cperiod," +
					"wa_period.cstartdate,wa_period.cenddate  from wa_waclass " +
					" inner join wa_periodscheme on wa_periodscheme.pk_periodscheme=wa_waclass.pk_periodscheme " +
					"inner join wa_period on wa_period.pk_periodscheme=wa_periodscheme.pk_periodscheme " +
					"inner join wa_periodstate on wa_periodstate.pk_wa_period=wa_period.pk_wa_period ");
					//20160105 shenliangc 薪资条查询功能查询一个基础数据表导致效率问题，经查sql查询关联条件缺失导致sql查询结果集大量重复执行效率低下，建议如下修改：
					//20150106 shenliangc NCdp205569297 改了之后导致多次方案设置薪资条后，查询第一次、第二次数据都为空。代码恢复原状。
//					" and wa_waclass.pk_wa_class= wa_periodstate.pk_wa_class ");
			//guoqt庞大薪资条管理查询效率优化，因为庞大组织太多
			sbd.append(" where  ");
			sbd.append(tempTable);

			new BaseDAO().executeUpdate(sbd.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new BusinessException(e.getMessage());
		}finally{
			inSQLCreator.clear();
		}


		return tableName;
	}


	/**
	 * 查询指定薪资条的薪资发放数据
	 */
	@Override
	public ArrayList<HashMap<String, Object>> queryPayslipDataByCond(LoginContext context, AggPayslipVO aggVO, String cyear, String cperiod,Integer intTimes, String condition,String orderBy) throws BusinessException
	{

		IClassItemQueryService service = NCLocator.getInstance().lookup(IClassItemQueryService.class);
		// 拼接薪资项目,有可能有动态扩展
		PayslipVO payslipVo = (PayslipVO)aggVO.getParentVO();
		String parentPK = payslipVo.getPk_wa_class();
		String pk = parentPK;

		if ( intTimes != 0)
		{
			StringBuffer sbSql = new StringBuffer();
			sbSql.append(" select * from wa_inludeclass where ");
			sbSql.append(" pk_parentclass =  '").append(pk).append("' ");
			sbSql.append(" and cyear =  '").append(cyear).append("' ");
			sbSql.append(" and  cperiod =  '").append(cperiod).append("' ");
			sbSql.append(" and  batch = abs(").append(intTimes).append(") ");
			sbSql.append(" and  pk_group =  '").append(context.getPk_group()).append("' ");
			sbSql.append(" and  pk_org =  '").append(context.getPk_org()).append("' ");
			WaInludeclassVO classVo =  new BaseDAOManager().executeQueryVO(sbSql.toString(),WaInludeclassVO.class);
			if (classVo != null)
			{
				pk = classVo.getPk_childclass();
			}
		}

		/*查看方案数据是否已经发放*/
		IWaPub wapub = NCLocator.getInstance().lookup(IWaPub.class);
		WaLoginVO waLoginVO =new WaLoginVO();
		waLoginVO.setPk_wa_class(pk);
		waLoginVO.setPk_prnt_class(parentPK);
		PeriodStateVO periodStateVO = new PeriodStateVO();
		periodStateVO.setCyear(cyear);
		periodStateVO.setCperiod(cperiod);
		waLoginVO.setPeriodVO(periodStateVO);
		//校验状态－ 是否已经取消发放。
		WaLoginVO waloginvo = wapub.getWaclassVOWithState(waLoginVO);
		if(waloginvo!=null&&!waloginvo.getState().equals(WaState.CLASS_ALL_PAY)&&!waloginvo.getState().equals(WaState.CLASS_MONTH_END))
		{
			Logger.debug("请先发放该期间单据!");
			throw new BusinessException(ResHelper.getString("6013payslp","06013payslp0172")/*@res "请先发放该期间单据!"*/);
		}

		// condition中获得会计期间
		String classitem_cond = " pk_wa_class = '" + pk + "' and cyear ='" + cyear + "' and cperiod='" + cperiod + "'";
		//20150730 xiejie3 补丁合并，NCdp205425993薪资条管理节点未按薪资方案权限控制，begin
		//shenliangc 20140626  解决薪资条按当前用户关联角色的薪资方案权限过滤问题。
		//同时将薪资条中发放项目按照当前用户关联角色的发放项目权限进行过滤。
		WaLoginContext waLoginContext = (WaLoginContext)context;
		waLoginContext.setWaLoginVO(waloginvo);
		classitem_cond += " and wa_classitem.pk_wa_item in (" +ItemPowerUtil.getItemPower(waLoginContext) + ")";
		//end

		WaClassItemVO[] itemVOs = service.queryClassItemByCondition(context, classitem_cond);

		String sql_wa_cols = "";
		HashMap<String, String> map = new HashMap<String, String>();
		ArrayList<WaClassItemVO> list = new ArrayList<WaClassItemVO>();
		for (int i = 0; itemVOs != null && i < itemVOs.length; i++)
		{
			if (map.get(itemVOs[i].getItemkey()) != null)
			{
				continue;
			}
			map.put(itemVOs[i].getItemkey(), "Y");
			list.add(itemVOs[i]);
			String tab_col = " " + TABLE_WADATA + "." + itemVOs[i].getItemkey() + " ";

			String col_as = TABLE_WADATA + itemVOs[i].getItemkey();
			String tab_col_as = tab_col + col_as;
			sql_wa_cols += sql_wa_cols.equals("") ? tab_col_as : "," + tab_col_as;
		}
		//guoqt庞大薪资条管理查询效率优化，因为庞大组织太多
		//20160105 shenliangc 薪资条查询功能查询一个基础数据表导致效率问题，经查sql查询关联条件缺失导致sql查询结果集大量重复执行效率低下，建议如下修改：
		//20150106 shenliangc NCdp205569297 改了之后导致多次方案设置薪资条后，查询第一次、第二次数据都为空。代码恢复原状。
		String tempTable = " wa_waclass.pk_org = '" + payslipVo.getPk_org() + "'" ;
		String period_view = createTempTable(tempTable);
				//" ( select distinct wa_waclass.pk_wa_class,wa_period.pk_wa_period,wa_period.cyear,wa_period.cperiod,wa_period.cstartdate,wa_period.cenddate " + " ,wa_periodstate.payoffflag " + " from wa_waclass " + " inner join wa_periodscheme on wa_periodscheme.pk_periodscheme=wa_waclass.pk_periodscheme " + " inner join wa_period on wa_period.pk_periodscheme=wa_periodscheme.pk_periodscheme " + " inner join wa_periodstate on wa_periodstate.pk_wa_period=wa_period.pk_wa_period ) t ";

		ArrayList<HashMap<String, Object>> results  = null;
		try {

			// 暂时先将所有字段查询出来，优化时可以仅将薪资条中用户选择的项目列拼接出来即可
			StringBuilder sqlbuBuilder = new StringBuilder() ;

			sqlbuBuilder
			.append(" select distinct "
					+ (sql_wa_cols.equals("") ? "" : sql_wa_cols + ",")
					+ " wa_data.pk_wa_data,wa_data.cyear as wa_datacyear,wa_data.cperiod as wa_datacperiod,wa_data.pk_bankaccbas1 as otherbankaccbasaccnum1,wa_data.pk_bankaccbas2 as otherbankaccbasaccnum2,wa_data.pk_bankaccbas3 as otherbankaccbasaccnum3, "
					+ " bd_psndoc.pk_psndoc as bd_psndocpk_psndoc,bd_psndoc.id as bd_psndocid,bd_psndoc.code as bd_psndoccode, "
					+ SQLHelper
					.getMultiLangNameColumn("bd_psndoc.name")
					+ "  as bd_psndocname , isnull(bd_psndoc.secret_email,bd_psndoc.email) as bd_psndocemail,  bd_psndoc.mobile as bd_psndocmobile"
					+ " ,bd_psncl.code as bd_psnclcode  , "
					+ SQLHelper.getMultiLangNameColumn("bd_psncl.name")
					+ "  as bd_psnclname "
					+ " ,org_orgs.code as org_orgscode , "
					+ SQLHelper.getMultiLangNameColumn("org_orgs.name")
					+ "  as org_orgsname "
					+ " ,org_dept_v.code as org_deptcode , "
					+ SQLHelper
					.getMultiLangNameColumn("org_dept_v.name")
					+ "  as org_deptname "
					+ " ,om_post.postcode as om_postpostcode , "
					+ SQLHelper
					.getMultiLangNameColumn("om_post.postname")
					+ "  as om_postpostname "
					+ " ,convert(char(10),t.cstartdate) as othercstartdate, convert(char(10), t.cenddate) as othercenddate , "
					+ "bt1.mnecode  as otherbankdoccode1, bt2.mnecode as otherbankdoccode2, bt3.mnecode  as otherbankdoccode3,"
					+ SQLHelper.getMultiLangNameColumn("bt1.name")
					+ "  as otherbankdocname1, "
					+ SQLHelper.getMultiLangNameColumn("bt2.name")
					+ "  as otherbankdocname2, "
					+ SQLHelper.getMultiLangNameColumn("bt3.name")
					+ "  as otherbankdocname3 ");
			sqlbuBuilder.append( " from wa_data " + " inner join wa_waclass on wa_data.pk_wa_class=wa_waclass.pk_wa_class " + " inner join bd_psndoc on wa_data.pk_psndoc=bd_psndoc.pk_psndoc " + " inner join hi_psnjob on wa_data.pk_psnjob=hi_psnjob.pk_psnjob " + " inner join bd_psncl on hi_psnjob.pk_psncl=bd_psncl.pk_psncl "
					+ "  left outer join org_dept_v on org_dept_v.pk_vid = wa_data.workdeptvid  inner JOIN org_orgs ON org_orgs.pk_org = hi_psnjob.pk_org left outer join om_post on hi_psnjob.pk_post = om_post.pk_post left outer join bd_banktype bt1 on wa_data.pk_banktype1=bt1.pk_banktype  left outer join bd_banktype bt2 on wa_data.pk_banktype2=bt2.pk_banktype  left outer join bd_banktype bt3 on wa_data.pk_banktype3=bt3.pk_banktype" );
			sqlbuBuilder.append( " inner join " + period_view + " t on (t.pk_wa_class=wa_data.pk_wa_class and t.cyear=wa_data.cyear and t.cperiod=wa_data.cperiod) ");

			String whereSql = "";
			if (intTimes < 100) {
				whereSql = " where wa_data.pk_org='" + context.getPk_org()
						+ "' and wa_data.pk_wa_class ='" + pk
						+ "' and wa_data.cyear = '" + cyear
						+ "' and wa_data.cperiod = '" + cperiod + "'";
			} else {
				//离职发薪
				whereSql = " where wa_data.pk_org='"
						+ context.getPk_org()
						+ "' and wa_data.pk_wa_class in (select pk_childclass from wa_inludeclass where pk_parentclass = '"
						+ payslipVo.getPk_wa_class() + "' and cyear = '" + cyear + "' and cperiod = '" + cperiod + "' and batch > 100)"
						+ " and wa_data.cyear = '" + cyear
						+ "' and wa_data.cperiod = '" + cperiod + "'";
			}
			sqlbuBuilder.append(whereSql) ;

			if (condition != null && !condition.trim().equals(""))
			{
				sqlbuBuilder.append( " and wa_data.pk_wa_data in (select pk_wa_data from wa_data where " + condition + ") ");
			}
			sqlbuBuilder.append( " and wa_data.stopflag='N'");
//			20151012 xiejie3  补丁合并 NCdp205449569
			//20150724 shenliangc 薪资条查询添加人员任职部门数据权限控制 begin
			String powerSql = WaPowerSqlHelper.getWaPowerSql(PubEnv.getPk_group(),
					HICommonValue.RESOUCECODE_6007PSNJOB,
					IHRWADataResCode.WADEFAULT, "wa_data");
			if (!StringUtil.isEmptyWithTrim(powerSql)) {
				sqlbuBuilder.append(" and " + powerSql);
			}
			//20150724 shenliangc 薪资条查询添加人员任职部门数据权限控制 end
			
			if(!StringUtil.isEmptyWithTrim(orderBy)){
				sqlbuBuilder.append(" order by "+orderBy);
			}else{
				sqlbuBuilder.append(" order by org_dept_v.code,bd_psndoc.code ");
			}

			BaseDAO dao = new BaseDAO();
			results = (ArrayList<HashMap<String, Object>>) dao.executeQuery(sqlbuBuilder.toString(), new MapListProcessor());

			// 目前的结果中存在同一个人有多条记录的情况，比如前后在两个部门中任职但处于同一方案中。需要将其合并为一条。(部门用逗号分隔)
			// HashMap<String, Integer> recordMap = new HashMap<String,
			// Integer>();
			// int dynCount = results == null ? 0 : results.size();
			// for (int i = 0; i < dynCount; i++)
			// {
			// HashMap<String, Object> valueMap = results.get(i);
			// String key = (String) valueMap.get("bd_psndocpk_psndoc");
			// if (recordMap.get(key) == null)
			// {
			// recordMap.put(key, Integer.valueOf(i));
			// }
			// else
			// {
			// // 存在重复的人，将部门信息拼接，薪资项目值累计
			// results.remove(i);
			// dynCount--;
			// int index = recordMap.get(key);
			// String colNameKey = "org_deptcode";
			// String oldValue = results.get(index).get(colNameKey) == null ? ""
			// : (String) results.get(index).get(colNameKey);
			// String newValue = valueMap.get(colNameKey) == null ? "" :
			// (String) valueMap.get(colNameKey);
			// if (!oldValue.equals(newValue))
			// {
			// newValue = oldValue + "," + newValue;
			// }
			// results.get(index).put(colNameKey, newValue);
			//
			// colNameKey = "org_deptname";
			// oldValue = results.get(index).get(colNameKey) == null ? "" :
			// (String) results.get(index).get(colNameKey);
			// newValue = valueMap.get(colNameKey) == null ? "" : (String)
			// valueMap.get(colNameKey);
			// if (!oldValue.equals(newValue))
			// {
			// newValue = oldValue + "," + newValue;
			// }
			// results.get(index).put(colNameKey, newValue);
			//
			// colNameKey = "bd_psnclcode";
			// oldValue = results.get(index).get(colNameKey) == null ? "" :
			// (String) results.get(index).get(colNameKey);
			// newValue = valueMap.get(colNameKey) == null ? "" : (String)
			// valueMap.get(colNameKey);
			// if (!oldValue.equals(newValue))
			// {
			// newValue = oldValue + "," + newValue;
			// }
			// results.get(index).put(colNameKey, newValue);
			//
			// colNameKey = "bd_psnclname";
			// oldValue = results.get(index).get(colNameKey) == null ? "" :
			// (String) results.get(index).get(colNameKey);
			// newValue = valueMap.get(colNameKey) == null ? "" : (String)
			// valueMap.get(colNameKey);
			// if (!oldValue.equals(newValue))
			// {
			// newValue = oldValue + "," + newValue;
			// }
			// results.get(index).put(colNameKey, newValue);
			//
			// // 银行账户先略掉
			// colNameKey = "bankname1";
			// oldValue = results.get(index).get(colNameKey) == null ? "" :
			// (String) results.get(index).get(colNameKey);
			// newValue = valueMap.get(colNameKey) == null ? "" : (String)
			// valueMap.get(colNameKey);
			// if (!oldValue.equals(newValue))
			// {
			// newValue = oldValue + "," + newValue;
			// }
			// results.get(index).put(colNameKey, newValue);
			//
			// colNameKey = "bankname2";
			// oldValue = results.get(index).get(colNameKey) == null ? "" :
			// (String) results.get(index).get(colNameKey);
			// newValue = valueMap.get(colNameKey) == null ? "" : (String)
			// valueMap.get(colNameKey);
			// if (!oldValue.equals(newValue))
			// {
			// newValue = oldValue + "," + newValue;
			// }
			// results.get(index).put(colNameKey, newValue);
			//
			// colNameKey = "bankname3";
			// oldValue = results.get(index).get(colNameKey) == null ? "" :
			// (String) results.get(index).get(colNameKey);
			// newValue = valueMap.get(colNameKey) == null ? "" : (String)
			// valueMap.get(colNameKey);
			// if (!oldValue.equals(newValue))
			// {
			// newValue = oldValue + "," + newValue;
			// }
			// results.get(index).put(colNameKey, newValue);
			//
			// // 薪资项目
			// for (int j = 0; list != null && j < list.size(); j++)
			// {
			// WaClassItemVO existItemVO = list.get(j);
			// if
			// (itemVOs[j].getIitemtype().equals(TypeEnumVO.FLOATTYPE.value()))
			// {
			// String waItemcolNameKey = "wa_data" + existItemVO.getItemkey();
			// BigDecimal oldBigValue = results.get(index).get(waItemcolNameKey)
			// == null ? (new BigDecimal(0)) : new
			// BigDecimal(results.get(index).get(waItemcolNameKey).toString());
			// BigDecimal newBigValue = valueMap.get(waItemcolNameKey) == null ?
			// (new BigDecimal(0)) : new
			// BigDecimal(valueMap.get(waItemcolNameKey).toString());
			// newBigValue = oldBigValue.add(newBigValue);
			// results.get(index).put(waItemcolNameKey, newBigValue);
			// }
			// }
			//
			// }
			// }

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			WaClassItemVO vo = (WaClassItemVO) context.getInitData();
			throw new BusinessException(ResHelper.getString("60130paydata","060130paydata0444")/*@res "薪资项目:"*/ + vo.getMultilangName() + ResHelper.getString("60130paydata","060130paydata0445")/*@res "公式设置错误。 请检查。"*/);

		}
		return results;
	}



	/**
	 * 不仅仅需要工资条主键，还需要用户id。因为需要用户权限限制
	 */
	public PayslipVO[] queryPayslipByCond(String condition, LoginContext context) throws BusinessException
	{
		IPersistenceRetrieve persistence = NCLocator.getInstance().lookup(IPersistenceRetrieve.class);

		PayslipVO[] vos = (PayslipVO[]) persistence.retrieveByClause(null, PayslipVO.class, condition);
		//		SalaryreportUtil util = new SalaryreportUtil();
		for (int i = 0; vos != null && i < vos.length; i++)
		{
			//			PayslipClassVO[] payslipClsVOs = (PayslipClassVO[]) persistence.retrieveByClause(null, PayslipClassVO.class, PayslipClassVO.PK_PAYSLIP + "='" + vos[i].getPk_payslip() + "'");
			//			String joinName = "";
			//			for (int j = 0; payslipClsVOs != null && j < payslipClsVOs.length; j++)
			//			{
			//				WaClassVO classVO = (WaClassVO) persistence.retrieveByPk(null, WaClassVO.class, payslipClsVOs[j].getPk_wa_class());
			//				if (classVO != null)
			//				{
			//					joinName += joinName.equals("") ? classVO.getName() : "," + classVO.getName();
			//				}
			//			}
			//			vos[i].setJoinname(joinName);
			//			vos[i].setTitle(util.parseCode2Text(vos[i].getTitle(), context));
		}

		return vos;
	}
	//
	//	@Override
	//	public SendResultVO[] sendPayslip(AggPayslipVO aggVO, ArrayList<HashMap<String, Object>> datas, WaLoginContext context) throws BusinessException
	//	{
	//		List<SendResultVO> list= null;
	//		if (aggVO == null || datas == null || datas.size() < 1)
	//		{
	//			return null;
	//		}else{
	//			list = new ArrayList<SendResultVO>();
	//		}
	//		PayslipVO vo = (PayslipVO) aggVO.getParentVO();
	//		PayslipItemVO[] itemVOs = (PayslipItemVO[]) aggVO.getTableVO(PayslipImpl.ITEM);
	//
	//		CurrtypeVO currVO = null;
	//		CurrtypeVO taxCurrVO = null;
	//
	//		   WaLoginVO waLoginVO =new WaLoginVO();
	//		   waLoginVO.setPk_wa_class(vo.getPk_wa_class());
	//		   PeriodStateVO periodStateVO = new PeriodStateVO();
	//			periodStateVO.setCyear(vo.getAccyear());
	//			periodStateVO.setCperiod(vo.getAccmonth());
	//			waLoginVO.setPeriodVO(periodStateVO);
	//			IWaPub wapub = NCLocator.getInstance().lookup(IWaPub.class);
	//			WaLoginVO tempvo = wapub.getWaclassVOWithState(waLoginVO);
	//			context.setWaLoginVO(tempvo);
	//
	//			ICurrtypeQuery service = NCLocator.getInstance().lookup(ICurrtypeQuery.class);
	//			WaClassVO classVO = context.getWaLoginVO();//waClassService.queryWaClassByPK(vo.getPk_wa_class());
	//
	//			if (classVO != null)
	//			{
	//				currVO = service.findCurrtypeVOByPK(classVO.getCurrid());
	//				taxCurrVO = service.findCurrtypeVOByPK(classVO.getTaxcurrid());
	//			}
	//
	//
	//		UFBoolean zeroPara = UFBoolean.TRUE;
	//		try {
	//			zeroPara = SysinitAccessor.getInstance().getParaBoolean(context.getPk_org(), ParaConstant.ZERO_SEND);
	//		} catch (BusinessException e) {
	//			Logger.error(e.getMessage());
	//		}
	//
	//		//查询组织orgvo
	//		IPFMessage pfMessage = (IPFMessage) NCLocator.getInstance().lookup(IPFMessage.class.getName());
	//		int count = 0;
	//		if (vo.getType().equals(SendTypeEnum.MAIL.value()))
	//		{
	//			SendResultVO sendVO = null;
	//			for (int i = 0; i < datas.size(); i++)
	//			{
	//				PayslipTemplate psTemplate = PayslipTemplate.getInstance();
	//				String content = "";
	//				//读取私密邮箱
	//
	//				//读取默认邮箱
	//				String email = (String) datas.get(i).get(SalaryreportUtil.TABLE_PSNDOC + ITEM_TAB_C_EMAIL);
	//				String name = (String) datas.get(i).get(SalaryreportUtil.TABLE_PSNDOC + ITEM_TAB_C_NAME);
	//				sendVO = new SendResultVO();
	//				sendVO.setPsnname(name);
	//				sendVO.setMborem(email);
	//				if (!StringUtils.isEmpty(email)){
	//					String[] emails = new String[]{ email };
	//					if(!StringUtils.isEmpty(psTemplate.getDefaultTemplateContents())){
	//						content = PayslipHtmlGenerator.generateHtmlPayslipContent(psTemplate, itemVOs, datas.get(i),context);
	//					}else{
	//						content = getPayslipHTMLContent(aggVO, itemVOs, datas.get(i), currVO, taxCurrVO,zeroPara,replaceVar(vo.getTail(), datas.get(i), context));
	//					}
	//					try{
	//						PfMailAndSMSUtil.sendHtmlEmail(replaceVar(vo.getTitle(), datas.get(i), context), emails, content, null);
	//					}catch(Exception e){
	//						//如果捕获异常,标示发送失败
	//						Logger.error(e.getMessage());
	//						sendVO.setResult(ResHelper.getString("6013payslp", "06013payslp0201")/*@res "否"*/ );
	//						sendVO.setReason(ResHelper.getString("6013payslp", "06013payslp0202")/*@res "发送失败，可能原因：1.服务器异常;2.邮箱已满;"*/ );
	//						list.add(sendVO);
	//					}
	//
	//				}else{
	//					sendVO.setResult(ResHelper.getString("6013payslp", "06013payslp0201")/*@res "否"*/ );
	//					sendVO.setReason(ResHelper.getString("6013payslp", "06013payslp0203")/*@res "没有配置邮箱信息"*/ );
	//					list.add(sendVO);
	//				}
	//			}
	//		}
	//		else
	//		{
	//			SendResultVO sendVO = null;
	//			for (int i = 0; i < datas.size(); i++)
	//			{
	//				MobileMsg mobileVO = new MobileMsg();
	//				String content = getPayslipMsgContent(vo, itemVOs, datas.get(i),zeroPara);
	//				mobileVO.setMsg(content);
	//				String mobile = (String) datas.get(i).get(SalaryreportUtil.TABLE_PSNDOC + ITEM_TAB_C_MOBILE);
	//				String name = (String) datas.get(i).get("bd_psndocname");
	//				sendVO = new SendResultVO();
	//				sendVO.setPsnname(name);
	//				sendVO.setMborem(mobile);
	//				if (!StringUtils.isEmpty(mobile))
	//				{
	//					String[] mobiles = new String[]
	//					{ mobile };
	//					mobileVO.setTargetPhones(mobiles);
	//					try {
	//						pfMessage.sendMobileMessage(mobileVO);
	//					} catch (Exception e) {
	//						//如果捕获异常,则提示
	//						Logger.error(e.getMessage());
	//						sendVO.setResult(ResHelper.getString("6013payslp", "06013payslp0201")/*@res "否"*/ );
	//						sendVO.setReason(ResHelper.getString("6013payslp", "06013payslp0207")/*@res "发送失败，可能原因：1.服务器异常;2.网络异常;"*/ );
	//						list.add(sendVO);
	//					}
	//
	//
	//				}else{
	//					sendVO.setResult(ResHelper.getString("6013payslp", "06013payslp0201")/*@res "否"*/ );
	//					sendVO.setReason(ResHelper.getString("6013payslp", "06013payslp0204")/*@res "没有配置手机信息"*/ );
	//					list.add(sendVO);
	//				}
	//			}
	//		}
	//		if (list.size() > 0) {
	//			return list.toArray(new SendResultVO[0]);
	//		}else{
	//			return null;
	//		}
	//	}
	//



	@Override
	public SendResultVO[] sendPayslip(AggPayslipVO aggVO,
			ArrayList<HashMap<String, Object>> datas, WaLoginContext context)
					throws BusinessException {
		Logger.error("\n*******************************发送工资条开始*********************************************" + "\n* 时间："
				+ PubEnv.getServerTime() + "\n* 用户：" + PubEnv.getPk_user() + "\n* 地址：" + InvocationInfoProxy.getInstance().getClientHost()
				+ "\n 人员个数："+datas.size()+"*******************************************************************************************");

		List<SendResultVO> list = null;
		if (aggVO == null || datas == null || datas.size() < 1) {
			return null;
		} else {
			list = new ArrayList<SendResultVO>();
		}


		PayslipVO vo = (PayslipVO) aggVO.getParentVO();
		PayslipItemVO[] itemVOs = (PayslipItemVO[]) aggVO.getTableVO(PayslipImpl.ITEM);
		CurrtypeVO currVO = null;
		CurrtypeVO taxCurrVO = null;
		WaLoginVO waLoginVO = new WaLoginVO();
		waLoginVO.setPk_wa_class(vo.getPk_wa_class());
		PeriodStateVO periodStateVO = new PeriodStateVO();
		periodStateVO.setCyear(vo.getAccyear());
		periodStateVO.setCperiod(vo.getAccmonth());
		waLoginVO.setPeriodVO(periodStateVO);
		IWaPub wapub = NCLocator.getInstance().lookup(IWaPub.class);
		WaLoginVO tempvo = wapub.getWaclassVOWithState(waLoginVO);
		context.setWaLoginVO(tempvo);

		WaClassVO classVO = context.getWaLoginVO();// waClassService.queryWaClassByPK(vo.getPk_wa_class());

		ICurrtypeQuery service = NCLocator.getInstance().lookup(ICurrtypeQuery.class);
		currVO = service.findCurrtypeVOByPK(classVO.getCurrid());
		taxCurrVO = service.findCurrtypeVOByPK(classVO.getTaxcurrid());


		//是否展现0值
		UFBoolean zeroPara = UFBoolean.TRUE;
		try {
			zeroPara = SysinitAccessor.getInstance().getParaBoolean(
					context.getPk_org(), ParaConstant.ZERO_SEND);
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
		}

		// 查询组织orgvo
		if (vo.getType().equals(SendTypeEnum.MAIL.value())) {
			SendResultVO sendVO = null;
			Map<String, String> map = SalaryreportUtil.getColName2ColIdMap(context, null);

			/* 帮助context 查询pk_org对应的orgvo */
			OrgVO orgvo = NCLocator.getInstance().lookup(IOrgUnitQryService.class).getOrg(context.getPk_org());
			List<OrgVO> ll = new ArrayList<OrgVO>();
			ll.add(orgvo);
			context.setOrgvos(ll);
			PayslipTemplate psTemplate = PayslipTemplate.getInstance();
			//guopeng 优化薪资条效率
			HashMap<String, String> itemmap = new SalaryreportUtil().getColId2FullColNameMap(context, vo);
			for (int i = 0; i < datas.size(); i++) {
				String content = "";

				// 读取邮箱
				String email = (String) datas.get(i).get(SalaryreportUtil.TABLE_PSNDOC + ITEM_TAB_C_EMAIL);
				String name = (String) datas.get(i).get(SalaryreportUtil.TABLE_PSNDOC + ITEM_TAB_C_NAME);

				sendVO = new SendResultVO();
				sendVO.setPsnname(name);
				sendVO.setMborem(email);
				
				//2015-07-30 zhousze NCdp205296968 工资条发送失败添加人员编码与人员类别 begin
				String code = (String) datas.get(i).get(SalaryreportUtil.TABLE_PSNDOC + "code");
				sendVO.setPsncode(code);
				
				String type = (String) datas.get(i).get(SalaryreportUtil.TABLE_PSNCL + "name");
				sendVO.setPsntype(type);
				//end
				
				if (StringUtils.isNotBlank(email)){

					//得到内容
					if (!StringUtils.isEmpty(psTemplate.getDefaultTemplateContents())) {
						content = PayslipHtmlGenerator.generateHtmlPayslipContent(psTemplate, itemVOs, datas.get(i), context);
					} else {
						// 2015-11-18 zhousze 这里修改解析表尾的方法，之前的replaceVar与下面的不一致，解析不出数据 begin
						content = getPayslipHTMLContent(aggVO, itemVOs, datas.get(i), currVO, taxCurrVO,zeroPara,replaceVar(vo.getTail(), datas.get(i),context, map));
						// end
					}

					//得到title
					String subject = replaceVar(vo.getTitle(), datas.get(i),context, map);

					try{
						Logger.error("\n*******************************(PfMailAndSMSUtil.sendHtmlEmail) 开始调用********" +
								"\n *序号："+i+"*************************************" + "\n* 时间："
								+ PubEnv.getServerTime() + "\n* 用户：" + PubEnv.getPk_user() + "\n* 地址：" + InvocationInfoProxy.getInstance().getClientHost()
								+ "\n*******************************************************************************************");

						PfMailAndSMSUtil.sendHtmlEmail(subject, new String[]{email}, content, null);
						Logger.error("\n*******************************(PfMailAndSMSUtil.sendHtmlEmail) 调用结束*********************************************" + "\n* 时间："
								+ PubEnv.getServerTime() + "\n* 用户：" + PubEnv.getPk_user() + "\n* 地址：" + InvocationInfoProxy.getInstance().getClientHost()
								+ "\n*******************************************************************************************");


					}catch(Exception e){
						//如果捕获异常,标示发送失败
						Logger.error(e.getMessage());
						sendVO.setResult(ResHelper.getString("6013payslp", "06013payslp0201"));
						sendVO.setReason(ResHelper.getString("6013payslp", "06013payslp0202"));
						list.add(sendVO);

						Logger.error("\n*******************************(PfMailAndSMSUtil.sendHtmlEmail) 抛出异常后终止，异常信息是： " + e.getMessage()+"*********************************************" + "\n* 时间："
								+ PubEnv.getServerTime() + "\n* 用户：" + PubEnv.getPk_user() + "\n* 地址：" + InvocationInfoProxy.getInstance().getClientHost()
								+ "\n*******************************************************************************************");

					}
				}else{
					sendVO.setResult(ResHelper.getString("6013payslp", "06013payslp0201"));
					sendVO.setReason(ResHelper.getString("6013payslp", "06013payslp0203"));/*没有配置邮箱信息*/
					list.add(sendVO);
				}

			}

		} else {
//			IPFMessage pfMessage = (IPFMessage) NCLocator.getInstance().lookup(IPFMessage.class.getName());
			SendResultVO sendVO = null;
			MessageSender sender = new MessageSender();
			for (int i = 0; i < datas.size(); i++) {
//				MobileMsg mobileVO = new MobileMsg();
				String content = getPayslipMsgContent(vo,
						itemVOs, datas.get(i), zeroPara);
//				mobileVO.setMsg(content);
				//20151022 shenliangc NCdp205496236 工资条发送手机失败添加人员编码与人员类别 begin
				String code = (String) datas.get(i).get(SalaryreportUtil.TABLE_PSNDOC + "code");
				String type = (String) datas.get(i).get(SalaryreportUtil.TABLE_PSNCL + "name");
				//end
				String mobile = (String) datas.get(i).get(SalaryreportUtil.TABLE_PSNDOC + ITEM_TAB_C_MOBILE);
				String name = (String) datas.get(i).get("bd_psndocname");
				sendVO = new SendResultVO();
				sendVO.setPsncode(code);
				sendVO.setPsnname(name);
				sendVO.setPsntype(type);
				sendVO.setMborem(mobile);
				if (!StringUtils.isEmpty(mobile)) {
					String[] mobiles = new String[] { mobile };
//					mobileVO.setTargetPhones(mobiles);
					try {
//						pfMessage.sendMobileMessage(mobileVO);
						sender.sendSMS(mobiles, content);
					} catch (Exception e) {
						// 如果捕获异常,则提示
						Logger.error(e.getMessage());
						sendVO.setResult(ResHelper.getString("6013payslp",
								"06013payslp0201"));
						sendVO.setReason(ResHelper.getString("6013payslp",
								"06013payslp0207"));
						list.add(sendVO);
					}

				} else {
					sendVO.setResult(ResHelper.getString("6013payslp",
							"06013payslp0201"));
					sendVO.setReason(ResHelper.getString("6013payslp",
							"06013payslp0204"));/*没有配置手机信息*/
					list.add(sendVO);
				}
			}
		}
		if (list.size() > 0) {
			Logger.error("\n*******************************>> sendPayslip 结束, 邮件发送过程中出失败*********************************************" + "\n* 时间："
					+ PubEnv.getServerTime() + "\n* 用户：" + PubEnv.getPk_user() + "\n* 地址：" + InvocationInfoProxy.getInstance().getClientHost()
					+ "\n*******************************************************************************************");

			return list.toArray(new SendResultVO[0]);
		} else {
			Logger.error("\n*******************************>> sendPayslip 结束, 邮件全部发送成功 *********************************************" + "\n* 时间："
					+ PubEnv.getServerTime() + "\n* 用户：" + PubEnv.getPk_user() + "\n* 地址：" + InvocationInfoProxy.getInstance().getClientHost()
					+ "\n*******************************************************************************************");

			return null;
		}
	}

	// 系统变量的通用模式
	public static String commonPattern = "<@.*?@>";

	/**
	 * 负责解析文本为系统编码
	 *
	 * @param text
	 * @return
	 * @throws BusinessException
	 */
	public  String parseText2Code(Map<String, String> 	map,String text, LoginContext context)
	{
		if (text == null)
		{
			return "";
		}
		Pattern pattern = Pattern.compile(commonPattern);
		Matcher matcher = pattern.matcher(text);


		while (matcher.find())
		{
			String name = matcher.group();
			name = name.substring(2, name.length() - 2);
			String code = map.get(name);
			if (code != null)
			{
				text = text.replace(name, code);
			}
		}
		return text;
	}


	public  String  replaceVar(String title, HashMap<String, Object> data, LoginContext context,Map<String,String> map) throws BusinessException{
		String returnCode = parseText2Code(map,title, context);
		int beginIndex = returnCode.indexOf("<@");
		while (beginIndex >= 0)
		{
			int endIndex = returnCode.indexOf("@>", beginIndex + 2);
			if (endIndex > 0)
			{
				String regex = returnCode.substring(beginIndex, endIndex + 2);
				String key = returnCode.substring(beginIndex + 2, endIndex);
				key = key.replace(".", "");
				if ((String) data.get(key) != null)
				{
					returnCode = returnCode.replaceAll(regex, (String) data.get(key));
				}
				else
				{
					returnCode = returnCode.replaceAll(regex, "");
				}
				beginIndex = returnCode.indexOf("<@", beginIndex + 2);
			}
			beginIndex = returnCode.indexOf("<@", beginIndex);
		}
		return returnCode;
	}

	private String getPayslipMsgContent(PayslipVO vo, PayslipItemVO[] items, HashMap<String, Object> data,UFBoolean zeroPara) throws BusinessException
	{
		String content = "";
		for (int i = 0; i < items.length; i++)
		{
			Object value = data.get(items[i].getItem_table() + items[i].getSlip_item());
			if(!(zeroPara != null && !zeroPara.booleanValue() && items[i].getSlip_item().contains("f_")&& value != null && Double.valueOf(value.toString()) == 0.0D)){
				String itemContent = items[i].getItem_displayname() + ":" + (value == null ? "" : value.toString());
				content += content.equals("") ? itemContent : "," + itemContent;
			}
		}
		return content;
	}


	private String getPayslipHTMLContent(AggPayslipVO aggVO, PayslipItemVO[] items, HashMap<String, Object> data, CurrtypeVO currVO, CurrtypeVO taxCurrVO,UFBoolean zeroPara,String tail) throws BusinessException
	{
		StringBuffer itemColName = new StringBuffer();
		StringBuffer itemColValue = new StringBuffer();
		PayslipVO vo = (PayslipVO) aggVO.getParentVO();

		String title = /* modify by xuhw vo.getName() +*/ " (" + (currVO == null ? "" : ResHelper.getString("6013report","06013report0059")/*@res "币种:"*/ + currVO.getCode()) + (taxCurrVO == null ? "" : ResHelper.getString("6013report","06013report0060")/*@res " 计税币种:"*/ + taxCurrVO.getCode()) + ")";
		StringBuffer titleCol = new StringBuffer(TR_START + "<th colspan=" + items.length + " height=40>" + title + "</th>" + TR_END);
		for (int i = 0; i < items.length; i++)
		{
			Object value = data.get(items[i].getItem_table() + items[i].getSlip_item());
			if(!(zeroPara != null && !zeroPara.booleanValue() && items[i].getSlip_item().contains("f_")&& value != null && Double.valueOf(value.toString()) == 0.0D)){
				itemColName.append(TD_START + items[i].getItem_displayname() + TD_END);
				boolean b = value == null || value.toString() == null;
				itemColValue.append(TD_START_RIGHT + (b ? "&nbsp;" : value) + TD_END);
			}
		}
		// 2015-08-14 zhousze NCdp205414754 邮件发送薪资条表头不再显示“币种”和“计税币种”
		String content = TABLE_START /*+ titleCol.toString() */ + TR_START_COLOR + itemColName.toString() + TR_END + TR_START + itemColValue.toString() + TR_END ;
		if (tail != null && !"".equals(tail)) {
			content += TR_START + "<td colspan = '" + items.length + "' align = 'right'>" + tail + "</td>" + TR_END ;
		}
		content += TABLE_END;
		return content;

	}

	private String replaceVar(HashMap<String, String> itemmap, String title, HashMap<String, Object> data, LoginContext context) throws BusinessException
	{
		String returnCode = SalaryreportUtil.parseText2Code(title, context, itemmap);
		int beginIndex = returnCode.indexOf("<@");
		while (beginIndex >= 0)
		{
			int endIndex = returnCode.indexOf("@>", beginIndex + 2);
			if (endIndex > 0)
			{
				String regex = returnCode.substring(beginIndex, endIndex + 2);
				String key = returnCode.substring(beginIndex + 2, endIndex);
				key = key.replace(".", "");
				if ((String) data.get(key) != null)
				{
					returnCode = returnCode.replaceAll(regex, (String) data.get(key));
				}
				else
				{
					returnCode = returnCode.replaceAll(regex, "");
				}
				beginIndex = returnCode.indexOf("<@", beginIndex + 2);
			}
			beginIndex = returnCode.indexOf("<@", beginIndex);
		}
		return returnCode;
	}

	/**
	 * 
	 */
	public SendResultVO[] sendPayslip(String pk_wa_class,String accyear,String accmonth,Integer senetype , ArrayList<HashMap<String, Object>> datas,WaLoginContext context) throws BusinessException{

		StringBuffer con = new StringBuffer();
		con.append(" pk_wa_class = '" + pk_wa_class + "' and accmonth= '"+accmonth+"' and accyear = '" + accyear+ "' and type = " + senetype.intValue());

		AggPayslipVO aggVO = queryAggPayslipVOByCon(con.toString(), context);
		return sendPayslip(aggVO, datas, context);


	}



	/**
	 * 自助/移动应用   个人薪资查询
	 */
	@Override
	public List<MyPayslipVO> querySelfAggPayslipVOs(String beginTime, String endTime, String psndocID, Integer type) throws BusinessException{
		List<MyPayslipVO> selfVOList = new ArrayList<MyPayslipVO>();
		//获得期间，开始时间和结束时间也许有多个期间
		PeriodVO[] periodVOs = getPayslipDao().queryPeriodVOs(beginTime, endTime);
		if(ArrayUtils.isEmpty(periodVOs)){
			return null;
		}

		for(int periodIndex=0;periodIndex<periodVOs.length;periodIndex++){
			PeriodVO periodVO = periodVOs[periodIndex];
			//获得此人在此期间下有关联的方案
			List<String> waClassPKs = getPayslipDao().queryWaClassPk(periodVO, psndocID,beginTime, endTime);
			if(waClassPKs.isEmpty()){//没有任何方案和此人有关
				continue;
			}


			Map<String,WaInludeclassVO> vomap = new LinkedHashMap<String, WaInludeclassVO>();

			WaInludeclassVO[] includeClassVos = this.getPayslipDao().queryWaInludeclassVOArrayByChildClassPKS(waClassPKs.toArray(new String[0]));
			if(waClassPKs!=null&&waClassPKs.size()>0){
				for (int i = 0; i < waClassPKs.size(); i++) {
					vomap.put(waClassPKs.get(i), null);
					if (!ArrayUtils.isEmpty(includeClassVos)) {
						for (int j = 0; j < includeClassVos.length; j++) {
							if(includeClassVos[j].getPk_childclass().equals(waClassPKs.get(i))){
								vomap.put(waClassPKs.get(i), includeClassVos[j]);
								break;
							}
						}
					}
				}
			}
			Set<Entry<String,WaInludeclassVO>> set = vomap.entrySet();
			Iterator<Entry<String,WaInludeclassVO>> iter = set.iterator();
			while(iter.hasNext()){
				Entry<String,WaInludeclassVO> entry = iter.next();
				WaInludeclassVO includeClassVO = entry.getValue();
				//判断方案是否多次发放的子方案，如果是，则不需要查询。因为查询父方案时候会把子方案一起查询，并有排序。
				if (includeClassVO == null) {
					List<MyPayslipVO> list = queryPayslipInfo(entry.getKey(), periodVO.getCyear(), periodVO.getCperiod(), psndocID, type);
					if(list != null && !list.isEmpty()){
						selfVOList.addAll(list);
					}
				}

			}

		}

		return selfVOList;
	}


	/**
	 * 自助  员工薪资查询
	 * beginTime  开始日期
	 * endTime    结束日期
	 * deptID     部门ID
	 * isIncludeChildDepts  是否包含子部门
	 * waClassID  薪资方案id ，可空
	 * 
	 * return  该部门下，该期间内所有的薪资记录
	 * 
	 */
	@Override
	public List<SelfEmpPayslipVO> querySelfEmpPayslipVOs(String beginDate,String endDate,String deptInnercode,Boolean isIncludeChildDepts,String waclasspk, String psndocArr) throws BusinessException{

		String sqlStr = buildSql(beginDate, endDate, deptInnercode, isIncludeChildDepts, waclasspk, psndocArr);
		ArrayList<HashMap<String,Object>> result = new ArrayList<HashMap<String,Object>>();
		result.addAll((ArrayList<HashMap<String,Object>>)getPayslipDao().getBaseDao().executeQuery(sqlStr, new MapListProcessor()));

		//封装数据并返回
		return convertSelfEmpPayslipVO(result);
	}




	/**
	 * 自助 查找某人某方案某期间的薪资条。
	 * 自助  员工薪资的明细  和  个人薪资查询中使用到
	 */
	@Override
	public List<MyPayslipVO> queryPayslipInfo(String waClassID, String cyear,String cperiod, String psndocID, Integer type) throws BusinessException{
		PayslipVO payslipVO = getPayslipDao().queryPayslipVO(waClassID, cyear, cperiod, type);
		if(payslipVO == null){
			return null;//不存在对应工资条
		}

		List<MyPayslipVO> selfVOList = new ArrayList<MyPayslipVO>();
		IWaClass waClass = NCLocator.getInstance().lookup(IWaClass.class);
		IItemQueryService itemService = NCLocator.getInstance().lookup(IItemQueryService.class);
		IPayrollManageService manageService = NCLocator.getInstance().lookup(IPayrollManageService.class);

		//根据id找到薪资方案
		WaClassVO classVO = waClass.queryWaClassByPK(waClassID);
		if(!isPayed(waClassID, cyear, cperiod)){
			return null;//方案未发放
		}

		List<String> waClassPKs = new ArrayList<String>();
		WaInludeclassVO[] includeClassVO = null;

		/**
		 * 确定共有多少个pk_wa_class
		 */
		//		//是否多次发放，若是，则每次发放都需要查询
		//		if(classVO.getMutipleflag().booleanValue()){
		//多次发薪 只添加子方案(不添加父方案)
		includeClassVO = waClass.queryIncludeClasses(waClassID, cyear, cperiod);
		List<WaInludeclassVO> list = new ArrayList<WaInludeclassVO>();
		if(!ArrayUtils.isEmpty(includeClassVO)){
			PayrollVO rollvo = new PayrollVO();
			rollvo.setCyear(cyear);
			rollvo.setCperiod(cperiod);

			for(int i=0;i<includeClassVO.length;i++){
				rollvo.setPk_wa_class(includeClassVO[i].getPk_childclass());
				if(manageService.isPayed(rollvo)){ //判断子方案是否发放
					waClassPKs.add(includeClassVO[i].getPk_childclass());
					list.add(includeClassVO[i]);
				}
			}
		}else{
			//不是多次发薪,直接添加父方案
			waClassPKs.add(waClassID);
		}



		//为自助方便显示，需要封装的数据
		String orgName = getPayslipDao().queryOrgName(payslipVO.getPk_org());
		String currName = getPayslipDao().queryCurrName(classVO.getCurrid());

		//20150717 shenliangc 集中查出发放项目精度信息 begin
		HashMap<String, WaClassItemVO> itemDecimalMap = new HashMap<String, WaClassItemVO>();
		itemDecimalMap = this.getPayslipDao().queryAllItemDecimal(waClassPKs, payslipVO.getPk_org(), cyear, cperiod);
		//20150717 shenliangc 集中查出发放项目精度信息 end
		
		// 查找薪资条的项目 20150729 xiejie3 此方法没必要放到for循环中。begin
		PayslipItemVO[] payslipItemVOs = getPayslipDao().queryPayslipItemVO(payslipVO.getPk_payslip());
		//end
		
		for (int waClassPKIndex = 0; waClassPKIndex < waClassPKs.size(); waClassPKIndex++) {
			// 查找薪资条的项目
//			PayslipItemVO[] payslipItemVOs = getPayslipDao().queryPayslipItemVO(payslipVO.getPk_payslip());
			AggPayslipVO aggVO = new AggPayslipVO();
			aggVO.setParentVO(payslipVO);
			aggVO.setTableVO(PayslipImpl.ITEM, payslipItemVOs);

			//查询薪资条数据
			String whereCondition = " and wa_data.pk_psndoc='"+ psndocID + "' and wa_data.pk_wa_class ='"
					+ waClassPKs.get(waClassPKIndex) + "' and wa_data.cyear = '" + cyear
					+ "' and wa_data.cperiod = '" + cperiod + "'";
			ArrayList<HashMap<String, Object>> results = getPayslipDao().queryPayslipData(aggVO, whereCondition);

			//为自助展示方便，封装数据
			if(!results.isEmpty()&&results.get(0)!=null){
				Map map = results.get(0);
				PaySlipItemValueVO[] paySlipVOs = new PaySlipItemValueVO[payslipItemVOs.length];
				for(int i=0;i<payslipItemVOs.length;i++){
					PaySlipItemValueVO psivVO = new PaySlipItemValueVO();
					psivVO.setName(payslipItemVOs[i].getItem_displayname());
					Object ovalue = map.get(payslipItemVOs[i].getItem_table()+payslipItemVOs[i].getSlip_item());
					//小数位设置
//					WaItemVO waItemVO = itemService.queryByClassItemkeyAndPkorg(payslipVO.getPk_org(), payslipItemVOs[i].getSlip_item());
					
					//20150717 shenliangc 组织级发放项目精度数据从map中取 begin
					WaClassItemVO waClassItemVO = itemDecimalMap.get(payslipVO.getPk_org() + waClassPKs.get(waClassPKIndex) + payslipItemVOs[i].getSlip_item());
					//20150717 shenliangc 组织级发放项目精度数据从map中取 end

					
					//如果组织下不存在该公共薪资项目，则在集团下查找
					if(waClassItemVO == null){
//						waItemVO = itemService.queryByClassItemkeyAndPkorg(payslipVO.getPk_group(), payslipItemVOs[i].getSlip_item());
						
						//20150717 shenliangc 集团级发放项目精度数据从map中取 begin
						waClassItemVO = itemDecimalMap.get(payslipVO.getPk_org() + waClassPKs.get(waClassPKIndex) + payslipItemVOs[i].getSlip_item());
						//20150717 shenliangc 集团级发放项目精度数据从map中取 end

					}
					if(waClassItemVO != null && ovalue != null && payslipItemVOs[i].getData_type().intValue() == 2){
						String strValue = ovalue.toString();
						if(!StringUtils.isEmpty(strValue)){
							//guoqt取薪资条的薪资发放项目的精度，需要测试多期间合计的情况
							ovalue = getFormatUFDouble(new UFDouble(strValue), payslipItemVOs[i].getIflddecimal());
						}
					}else if(waClassItemVO == null && ovalue != null && payslipItemVOs[i].getData_type().intValue() == 2){
						//20160112 shenliangc NCdp205571711  多次发薪自助薪资条问题 
						//多次方案第二次发放增加的项目，第一次的薪资条中该项目精度显示8位。修改为凡是找不到精度的数值型项目都算两位小数。
						String strValue = ovalue.toString();
						if(!StringUtils.isEmpty(strValue)){
							ovalue = getFormatUFDouble(new UFDouble(strValue), 2);
						}
					}
					psivVO.setValue(ovalue);
					psivVO.setCode(payslipItemVOs[i].getItem_table()+payslipItemVOs[i].getSlip_item());
					if(type.intValue() == 3){
						psivVO.setIsGroupItem(isGroupItem(waClassItemVO));
						psivVO.setIsCountPro(payslipItemVOs[i].getIsCountPro());
						psivVO.setIsEmpPro(payslipItemVOs[i].getIsEmpPro());
						psivVO.setIsMngPro(payslipItemVOs[i].getIsMngPro());
					}
					psivVO.setDataType(payslipItemVOs[i].getData_type());
					paySlipVOs[i] = psivVO;
				}
				int batch = 0;
				boolean isMultiParentClass = classVO.getMutipleflag().booleanValue();
				//方案为多次发放的子方案，要获得次数
//				if(includeClassVO != null && waClassPKIndex<includeClassVO.length){
//					batch = includeClassVO[waClassPKIndex].getBatch();
//					isMultiParentClass = false;
//				}
				if(includeClassVO != null && waClassPKIndex<list.size()){
					batch = list.get(waClassPKIndex).getBatch();
					isMultiParentClass = false;
				}
				MyPayslipVO myPayslipVO = new MyPayslipVO(payslipVO.getPk_org(),orgName, currName, null,
						classVO.getMultilangName(), cyear, cperiod,batch,isMultiParentClass);
				myPayslipVO.setPk_wa_data((String)map.get("pk_wa_data"));
				myPayslipVO.setPaySlipVOs(paySlipVOs);
				selfVOList.add(myPayslipVO);
			}
		}
		return selfVOList;
	}




	private String buildSql(String beginDate,String endDate,String deptInnercode,Boolean isIncludeChildDepts,String waclasspk, String psndocArr){

		int innercodeLen = deptInnercode.length();
		StringBuffer sqlSB = new StringBuffer("select bd_psndoc.CODE code,"+SQLHelper.getMultiLangNameColumn("bd_psndoc.NAME"))
		.append(" name,"+SQLHelper.getMultiLangNameColumn("bd_psncl.NAME"))
		.append(" psnclname,"+SQLHelper.getMultiLangNameColumn("org_orgs_v.NAME"))
		.append(" orgname,"+SQLHelper.getMultiLangNameColumn("org_dept_v.NAME"))
		.append(" deptname,"+SQLHelper.getMultiLangNameColumn("om_post.POSTNAME"))
		.append(" postname, "+SQLHelper.getMultiLangNameColumn("wa_waclass.NAME"))
		.append(" classname,wa_data.F_1 totalpayable,wa_data.F_2 totaldeduction,wa_data.F_3 paidtotal,wa_data.CYEAR cyear,wa_data.CPERIOD cperiod,wa_waclass.PK_WA_CLASS classid,bd_psndoc.PK_PSNDOC psndocid ")
		.append(",wa_period.cstartdate cstartdate,wa_period.cenddate cenddate,hi_psnjob.clerkcode clerkcode ")
		.append(" from wa_data inner join wa_waclass on wa_data.pk_wa_class = wa_waclass.pk_wa_class ")
		.append(" and wa_waclass.showflag = 'Y'   ")
		.append(" inner join wa_periodstate on wa_waclass.pk_wa_class = wa_periodstate.pk_wa_class ")
		.append(" inner join wa_period   on wa_periodstate.pk_wa_period = wa_period.pk_wa_period and wa_data.cyear = wa_period.cyear and wa_data.cperiod = wa_period.cperiod ")
		.append(" left   join org_dept  on wa_data.workdept = org_dept.pk_dept ")
		.append(" left   join org_dept_v  on wa_data.workdeptvid = org_dept_v.pk_vid  ")

		.append(" inner join bd_psndoc on wa_data.pk_psndoc=bd_psndoc.pk_psndoc ")
		.append(" inner join hi_psnjob on wa_data.pk_psnjob=hi_psnjob.pk_psnjob ")
		.append("  inner join org_orgs on hi_psnjob.pk_org=org_orgs.pk_org ")
		.append(" left outer join org_orgs_v  on wa_data.workorgvid = org_orgs_v.pk_vid  ")
		.append(" left outer join om_post on hi_psnjob.pk_post = om_post.pk_post ")
		.append(" left outer join bd_psncl on hi_psnjob.pk_psncl=bd_psncl.pk_psncl ")
		.append(" inner join wa_payslip on wa_payslip.accyear = wa_period.cyear and wa_payslip.ACCMONTH = wa_period.cperiod  ")
		.append(" where  wa_data.stopflag = 'N'  and wa_periodstate.enableflag = 'Y' ")//尚未听停发的,已经发放的
		//添加时间限制
		.append(" and   ( wa_period.cenddate >='"+beginDate+"' ")
		.append("   and wa_period.cstartdate  <= '"+endDate+"') ");

		//添加部门限制
		if(isIncludeChildDepts){
			sqlSB.append(" and org_dept.pk_dept in ( select pk_dept from org_dept where  substring(innercode,1,"+innercodeLen +") = '"+ deptInnercode +"') ");
		}else{
			sqlSB.append(" and org_dept.innercode = '"+deptInnercode+"' ");
		}

		////添加方案限制
		if(!StringUtils.isEmpty(waclasspk)){
			sqlSB.append("	and   wa_data.pk_wa_class ='"+waclasspk+"'");
			sqlSB.append(" and   wa_payslip.type = 3 ");
			sqlSB.append(" and   wa_payslip.pk_wa_class ='"+waclasspk+"'");
			//添加是否发放校验
			sqlSB.append(" and (");
			sqlSB.append(" wa_periodstate.PAYOFFFLAG = 'Y' ");
			sqlSB.append(" or");
			//sqlSB.append(" (wa_periodstate.PAYOFFFLAG = 'N' and wa_periodstate.classtype <> 0 and wa_data.pk_wa_class in ");
			//移动应用补丁合并guoqt
			sqlSB.append(" (wa_periodstate.PAYOFFFLAG = 'N' and wa_data.pk_wa_class in ");
			sqlSB.append(" (SELECT pk_parentclass FROM wa_inludeclass WHERE pk_childclass IN (select pk_wa_class from wa_periodstate inner join wa_period ON wa_periodstate.pk_wa_period = wa_period.pk_wa_period ");
			sqlSB.append(" and wa_period.cenddate >='"+beginDate+"' AND wa_period.cstartdate <= '"+endDate+"' and wa_periodstate.PAYOFFFLAG = 'Y' and pk_wa_class in ");
			sqlSB.append(" (select pk_childclass from wa_inludeclass where pk_parentclass = '"+waclasspk+"') )" +
					" and cyear=wa_data.cyear and cperiod=wa_data.cperiod ))");
			sqlSB.append(" ) ");
		}

		if (!StringUtils.isEmpty(psndocArr)) {
			sqlSB.append(" and bd_psndoc.pk_psndoc in (" + psndocArr + ")");
		}

		return sqlSB.toString();
	}



	private UFBoolean isGroupItem(WaItemVO itemVO){
		if(itemVO != null && (HRWACommonConstants.GROUP_ID.equals(itemVO.getPk_org()) || PubEnv.getPk_group().equals(itemVO.getPk_org()))){
			return UFBoolean.TRUE;
		}
		return UFBoolean.FALSE;
	}

	/**
	 * 按照自助员工薪资查询显示方式封装数据
	 * @param result
	 * @return
	 * @throws BusinessException
	 */
	private List<SelfEmpPayslipVO> convertSelfEmpPayslipVO(ArrayList<HashMap<String,Object>> result) throws BusinessException{
		List<SelfEmpPayslipVO> selfVOList = new ArrayList<SelfEmpPayslipVO>();
		IItemQueryService itemService = NCLocator.getInstance().lookup(IItemQueryService.class);
		//查找公共薪资项目：应发合计、扣款合计、实发合计  用于小数位的截取
		WaItemVO totalpayableVO = itemService.queryByItemkey(WaItemConstant.SysItemKey[0]);
		WaItemVO totaldeductionVO = itemService.queryByItemkey(WaItemConstant.SysItemKey[1]);
		WaItemVO paidtotalVO = itemService.queryByItemkey(WaItemConstant.SysItemKey[2]);
		if(result != null && !result.isEmpty()){
			for(int i=0;i<result.size();i++){
				SelfEmpPayslipVO empPayslipVO = new SelfEmpPayslipVO();
				empPayslipVO.setCode((String)result.get(i).get("code"));
				empPayslipVO.setName((String)result.get(i).get("name"));
				empPayslipVO.setPsnclName((String)result.get(i).get("psnclname"));
				empPayslipVO.setOrgName((String)result.get(i).get("orgname"));
				empPayslipVO.setDeptName((String)result.get(i).get("deptname"));
				empPayslipVO.setPostName((String)result.get(i).get("postname"));
				empPayslipVO.setClassName((String)result.get(i).get("classname"));
				BigDecimal totalpayable = (BigDecimal)result.get(i).get("totalpayable");
				//处理小数位
				if(totalpayableVO!= null && totalpayable != null){
					if(!StringUtils.isEmpty(totalpayable.toString())){
						empPayslipVO.setTotalPayable(getFormatUFDouble(new UFDouble(totalpayable.toString()), totalpayableVO.getIflddecimal()).toString());;
					}
				}
				BigDecimal totaldeduction = (BigDecimal)result.get(i).get("totaldeduction");
				if(totaldeductionVO!= null && totaldeduction != null){
					if(!StringUtils.isEmpty(totaldeduction.toString())){
						empPayslipVO.setTotalDeduction(getFormatUFDouble(new UFDouble(totaldeduction.toString()), totaldeductionVO.getIflddecimal()).toString());;
					}
				}
				BigDecimal paidtotal = (BigDecimal)result.get(i).get("paidtotal");
				if(paidtotalVO!= null && paidtotal != null){
					if(!StringUtils.isEmpty(paidtotal.toString())){
						empPayslipVO.setPaidTotal(getFormatUFDouble(new UFDouble(paidtotal.toString()), paidtotalVO.getIflddecimal()).toString());;
					}
				}
				empPayslipVO.setCyear((String)result.get(i).get("cyear"));
				empPayslipVO.setCperiod((String)result.get(i).get("cperiod"));
				empPayslipVO.setClassID((String)result.get(i).get("classid"));
				empPayslipVO.setPsndocID((String)result.get(i).get("psndocid"));
				empPayslipVO.setCStartDate((String)result.get(i).get("cstartdate"));
				empPayslipVO.setCEndDate((String)result.get(i).get("cenddate"));
				empPayslipVO.setClerkcode((String)result.get(i).get("clerkcode"));
				selfVOList.add(empPayslipVO);
			}
		}
		return selfVOList;
	}


	/**
	 * 判断方案是否已发放
	 * 业务意义上的单次薪资方案， 根据期间状态判断就可以了。
	 * 多次薪资方案，只要有一个子方案发放就认为父方案已发放
	 * @param waClassVO  //薪资方案，如果是多次发放，传的是父方案
	 * @param cyear
	 * @param cperiod
	 * @return
	 * @throws BusinessException
	 */
	private boolean isPayed(String classid,String cyear,String cperiod) throws BusinessException{
		return  NCLocator.getInstance().lookup(IPaydataQueryService.class).isAnyTimesPayed(classid, cyear, cperiod);
	}

	private WaInludeclassVO[] queryWaInludeclassVO(String cyear,String cperiod,String waClassPK) throws DAOException{
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" pk_parentclass =  '").append(waClassPK).append("' ");
		sbSql.append(" and cyear =  '").append(cyear).append("' ");
		sbSql.append(" and  cperiod =  '").append(cperiod).append("' ");
		return new BaseDAOManager().retrieveByClause(WaInludeclassVO.class, sbSql.toString());
	}


	/**
	 * 根据开始日期、结束日期、部门pk、是否包含子部门 。 查看用户定义的的薪资方案（showflag = ‘Y’）
	 */
	@Override
	public List<WaClassVO> queryWaClassVOs(String beginTime, String endTime,
			String deptID, Boolean isIncludeChildDepts)
					throws BusinessException {
		PeriodVO[] periodVOs = getPayslipDao().queryPeriodVOs(beginTime, endTime,null);
		if(periodVOs == null || periodVOs.length == 0){
			return null;
		}

		List<String> waClassPks = getPayslipDao().queryWaClassVOs(periodVOs, deptID, isIncludeChildDepts,beginTime,endTime);
		IWaClass waClass = NCLocator.getInstance().lookup(IWaClass.class);
		// 因为多次发放的方案，从薪资档案中查询出来的是子方案，所以对于多次发放的子方案，要找到父方案。父方案可能重复，所以要去掉重复
		//Map中方薪资方案的ID，只是用来判断是否重复，没有其它意义
		Map<String,String> map = new HashMap<String,String>();
		List<WaClassVO> classVOList = new ArrayList<WaClassVO>();
		Map<String,WaInludeclassVO> vomap = new LinkedHashMap<String, WaInludeclassVO>();
		WaInludeclassVO[] includeClassVos = this.getPayslipDao().queryWaInludeclassVOArrayByChildClassPKS(waClassPks == null ? null : waClassPks.toArray(new String[0]));
		if(waClassPks!=null&&waClassPks.size()>0){
			for (int i = 0; i < waClassPks.size(); i++) {
				vomap.put(waClassPks.get(i), null);
				if (includeClassVos != null && includeClassVos.length > 0) {
					for (int j = 0; j < includeClassVos.length; j++) {
						if(includeClassVos[j].getPk_childclass().equals(waClassPks.get(i))){
							vomap.put(waClassPks.get(i), includeClassVos[j]);
							break;
						}
					}
				}
			}
		}
		Set<Entry<String,WaInludeclassVO>> set = vomap.entrySet();
		Iterator<Entry<String,WaInludeclassVO>> iter = set.iterator();
		while(iter.hasNext()){
			Entry<String,WaInludeclassVO> entry = iter.next();
			String pk = entry.getKey();
			WaInludeclassVO includeClassVO = entry.getValue();
			if(includeClassVO != null){
				//父方案ID
				pk = includeClassVO.getPk_parentclass();
			}
			if(map.get(pk) == null){
				WaClassVO classVO = waClass.queryWaClassByPK(pk);
				classVOList.add(classVO);
				map.put(pk, pk);
			}
		}



		return classVOList;
	}

	@Override
	public PrintTempletmanageItemVO[] queryPrintTempletmanageItemVOs(String waClassPK) throws BusinessException {
		WaClassItemVO[] itemVOs = getPayslipDao().queryWaClassItemVOs(waClassPK);
		if(ArrayUtils.isEmpty(itemVOs)){
			return null;
		}
		PrintTempletmanageItemVO[] vos = new PrintTempletmanageItemVO[itemVOs.length];
		for(int i=0;i<itemVOs.length;i++){
			PrintTempletmanageItemVO ptItemVO = new PrintTempletmanageItemVO();
			ptItemVO.setVvarexpress(itemVOs[i].getItemkey());
			ptItemVO.setVvarname(itemVOs[i].getMultilangName());
			vos[i] = ptItemVO;
		}
		return vos;
	}

	@Override
	public WaClassPrintVO queryWaClassPrintVO(String waClassPK) throws DAOException {
		return getPayslipDao().queryWaClassPrintVO(waClassPK);
	}

	@Override
	public void insertWaClassPrintVO(WaClassPrintVO vo) throws BusinessException {
		IPersistenceUpdate persistence = NCLocator.getInstance().lookup(IPersistenceUpdate.class);
		persistence.insertVO(null, vo, null);

	}

	@Override
	public void deleteWaClassPrintVO(WaClassPrintVO vo) throws BusinessException {
		getPayslipDao().getBaseDao().deleteByClause(WaClassPrintVO.class, " pk_print_template_id='"+vo.getPk_print_template_id()+"'");
	}

	@Override
	public DataVO[] queryDataVO(String[] pkArray) throws BusinessException {
		NCObject[] objs = MDPersistenceService.lookupPersistenceQueryService().queryBillOfNCObjectByPKs(DataVO.class, pkArray, null, false);
		return VOArrayUtil.convertToVOArray(DataVO.class,objs);
	}

	private String createItemQueryCondition(LoginContext context, String waClassPk,String cyear,String cperiod){
		StringBuffer sbWhereCond = new StringBuffer(" and ");
		sbWhereCond.append(TABLE_WACLASSITEM+"."+WaClassItemVO.PK_WA_CLASS).append(" = '").append(waClassPk).append("' and ");
		sbWhereCond.append(TABLE_WACLASSITEM+"."+WaClassItemVO.CYEAR).append(" = '").append(cyear).append("' and ");
		sbWhereCond.append(TABLE_WACLASSITEM+"."+WaClassItemVO.CPERIOD).append(" = '").append(cperiod).append("' ");
		sbWhereCond.append(" and wa_classitem.itemkey in("+"select distinct wa_item.itemkey from wa_itempower ,wa_item where  wa_itempower.pk_wa_item = wa_item.pk_wa_item and  wa_itempower.pk_wa_class = '"+waClassPk+"' and (wa_itempower.pk_subject in ( select pk_role from sm_user_role where cuserid = '"+context.getPk_loginUser()+"' ) or wa_itempower.pk_subject = '"+context.getPk_loginUser()+"' ) and wa_itempower.pk_group = '"+context.getPk_group()+"' and wa_itempower.pk_org = '"+context.getPk_org()+"'"+")");
		return sbWhereCond.toString();
	}

	@Override
	public PayslipItemVO[] queryPowerPayslipItemVO(String strPK_wa_class,
			String strYear, String strMonth, Integer intType,
			LoginContext context) throws BusinessException {
		IClassItemQueryService ciService = NCLocator.getInstance().lookup(IClassItemQueryService.class);
		//查找薪资条
		AggPayslipVO billVO = queryAggPayslipByCond4AddOrEdit(strPK_wa_class, strYear, strMonth, intType, context);
		if(billVO == null){
			return null;
		}

		//查找方案期间下的所有薪资发放项目
		StringBuffer sbWhereCond = new StringBuffer(" and ");
		sbWhereCond.append(TABLE_WACLASSITEM+"."
				+WaClassItemVO.PK_WA_CLASS).append(" = '").append(strPK_wa_class).append("' and ");
		sbWhereCond.append(TABLE_WACLASSITEM+"."
				+WaClassItemVO.CYEAR).append(" = '").append(strYear).append("' and ");
		sbWhereCond.append(TABLE_WACLASSITEM+"."
				+WaClassItemVO.CPERIOD).append(" = '").append(strMonth).append("' ");
		WaClassItemVO[] allVOs = ciService.queryByCondition(context, null, sbWhereCond.toString(), null);
		if(ArrayUtils.isEmpty(allVOs)){
			return null;
		}

		//查找方案期间下的有权限的薪资发放项目
		String condition = createItemQueryCondition(context, strPK_wa_class, strYear, strMonth);
		WaClassItemVO[] powerVOs = NCLocator.getInstance().lookup(IClassItemQueryService.class).queryByCondition(context, "", condition, null);

		if(ArrayUtils.isEmpty(powerVOs)){
			return null;
		}

		//把所有的信息发放项目的itemkey放到map中，用来判断是否薪资发放项目
		Map<String,String> allVOMap = new HashMap<String, String>();
		for(int i=0;i<allVOs.length;i++){
			allVOMap.put(allVOs[i].getItemkey(), allVOs[i].getItemkey());
		}

		//把有权限的薪资发放项目的itemkey放到map中,用来判断是否有权限
		Map<String,String> powerVOMap = new HashMap<String, String>();
		for(int i=0;i<powerVOs.length;i++){
			powerVOMap.put(powerVOs[i].getItemkey(), powerVOs[i].getItemkey());
		}

		List<PayslipItemVO> itemVOList = new ArrayList<PayslipItemVO>();
		PayslipItemVO[] itemVOArray = (PayslipItemVO[])billVO.getTableVO(AggPayslipVO.ITEM);
		for(int i=0;i<itemVOArray.length;i++){
			if (TABLE_WADATA.equals(itemVOArray[i]
					.getItem_table())
					&& allVOMap.get(itemVOArray[i].getSlip_item()) != null
					&& powerVOMap.get(itemVOArray[i].getSlip_item()) == null) {
				//首先判断是否薪资发放档案(wa_data)的属性，是则继续。如果不是，则肯定不是薪资发放项目，更无关项目权限。如：薪资条中bd_psndoc、org_dept、other等
				//其次判断是否薪资发放项目，是则继续。因为wa_data中不一定就是薪资发放项目。如：薪资条中会计年度、会计期间
				//之后判断才是否有权限,上面两个条件满足而没权限，则该薪资条的项目不应该显示
				continue;
			}else{
				itemVOList.add(itemVOArray[i]);
			}
		}
		return itemVOList.toArray(new PayslipItemVO[0]);
	}
	//	public void a(){
	//		BillUtil.getFormatUFDouble(new UFDouble(0.99), 2);
	//	}

	public static UFDouble getFormatUFDouble(UFDouble d, int digits) {
		UFDouble o = null;
		if (d != null) {
			int power = d.getPower();

			if (power != -digits) {
				o = d.setScale(digits, UFDouble.ROUND_HALF_UP);
			} else {
				o = d;
			}
		}
		return o;
	}
	@Override
	public List queryClassItem(String pk_group, String pk_org)
			throws BusinessException {
		String sql = "select itemkey,name from wa_item where pk_org= '"+pk_group+"' or pk_org='"+pk_org+"' or pk_org='GLOBLE00000000000000'";

		List list = (List) new BaseDAO().executeQuery(sql, new MapListProcessor());
		List<PrintTempletmanageItemVO> itemlist = new ArrayList<PrintTempletmanageItemVO>();
		if(list!=null&&list.size()>0){
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);

				PrintTempletmanageItemVO item = new PrintTempletmanageItemVO();
				item.setVvarname(map.get("name")+"");
				item.setVvarexpress(map.get("itemkey")+"");
				itemlist.add(item);
			}
		}

		//人员编码
		PrintTempletmanageItemVO codeitem = new PrintTempletmanageItemVO();
		codeitem.setVvarname(ResHelper.getString("common", "UC000-0000147")/**人员编码 */);
		codeitem.setVvarexpress("bd_psndoccode");
		itemlist.add(codeitem);

		//姓名
		PrintTempletmanageItemVO nameitem = new PrintTempletmanageItemVO();
		nameitem.setVvarname(ResHelper.getString("common", "UC000-0001403")/**姓名*/);
		nameitem.setVvarexpress("bd_psndocname");
		itemlist.add(nameitem);

		//证件号码
		PrintTempletmanageItemVO iditem = new PrintTempletmanageItemVO();
		iditem.setVvarname(ResHelper.getString("60130paydata", "060130paydata0475")/**证件号码*/);
		iditem.setVvarexpress("bd_psndocid");
		itemlist.add(iditem);

		//电子邮件
		PrintTempletmanageItemVO emailitem = new PrintTempletmanageItemVO();
		emailitem.setVvarname(ResHelper.getString("common", "UC000-0002980")/**电子邮件*/);
		emailitem.setVvarexpress("bd_psndocemail");
		itemlist.add(emailitem);

		// 2015-11-4 zhousze 给手机字段是as的bd_psndocmobile，而不是bd_psndocphone，所以取不到值 begin
		//手机
		PrintTempletmanageItemVO phoneitem = new PrintTempletmanageItemVO();
		phoneitem.setVvarname(ResHelper.getString("common", "UC000-0001988")/**手机*/);
		phoneitem.setVvarexpress("bd_psndocmobile");
		itemlist.add(phoneitem);
		// end

		//人员分类编码
		PrintTempletmanageItemVO tcodeitem = new PrintTempletmanageItemVO();
		tcodeitem.setVvarname(ResHelper.getString("60130paydata", "060130paydata0476")/**人员分类编码*/);
		tcodeitem.setVvarexpress("bd_psnclcode");
		itemlist.add(tcodeitem);

		//人员分类名称
		PrintTempletmanageItemVO tnameitem = new PrintTempletmanageItemVO();
		tnameitem.setVvarname(ResHelper.getString("60130paydata", "060130paydata0477")/**人员分类名称*/);
		tnameitem.setVvarexpress("bd_psnclname");
		itemlist.add(tnameitem);

		//部门编码
		PrintTempletmanageItemVO orgcodeitem = new PrintTempletmanageItemVO();
		orgcodeitem.setVvarname(ResHelper.getString("common", "UC000-0004073")/**部门编码*/);
		orgcodeitem.setVvarexpress("org_deptcode");
		itemlist.add(orgcodeitem);

		//部门名称
		PrintTempletmanageItemVO orgnameitem = new PrintTempletmanageItemVO();
		orgnameitem.setVvarname(ResHelper.getString("common", "UC000-0004069")/**部门名称*/);
		orgnameitem.setVvarexpress("org_deptname");
		itemlist.add(orgnameitem);

		//薪资项目-年度
		PrintTempletmanageItemVO cyearitem = new PrintTempletmanageItemVO();
		cyearitem.setVvarname(ResHelper.getString("common", "UC000-0001802")/**年度*/);
		cyearitem.setVvarexpress("wa_datacyear");
		itemlist.add(cyearitem);

		//薪资项目-期间
		PrintTempletmanageItemVO cperioditem = new PrintTempletmanageItemVO();
		cperioditem.setVvarname(ResHelper.getString("common", "UC000-0002560")/**期间*/);
		cperioditem.setVvarexpress("wa_datacperiod");
		itemlist.add(cperioditem);
		
		// 2015-11-4 zhousze 解决“薪资条管理”三层保密工资单打印预览取不到手机等字段信息的问题。这里主要是因为
		// 在WaPrintItemListener中薪资发放 60130paydata、离职结薪 60130payleave、薪资补发 60130repaydata、
		// 发放申请 60130payslipaly、发放审批 60130payslipapv、数据接口 60130dataitf、工资条 60130slipmgt这些节
		// 点的公式一致，而在薪资条管理中并不是那些字段，所以现在分出来，独立给薪资条增加数据源变量的设置 begin
		PrintTempletmanageItemVO bankaccbas1 = new PrintTempletmanageItemVO();	
		bankaccbas1.setVvarname(ResHelper.getString("60130payfile", "160130payfile0006")/**主卡帐号 */);
		bankaccbas1.setVvarexpress("otherbankaccbasaccnum1");
		itemlist.add(bankaccbas1);
		
		PrintTempletmanageItemVO bankaccbas2 = new PrintTempletmanageItemVO();	
		bankaccbas2.setVvarname(ResHelper.getString("60130payfile", "160130payfile0008")/**副卡1帐号 */);
		bankaccbas2.setVvarexpress("otherbankaccbasaccnum2");
		itemlist.add(bankaccbas2);
		
		PrintTempletmanageItemVO bankaccbas3 = new PrintTempletmanageItemVO();	
		bankaccbas3.setVvarname(ResHelper.getString("60130payfile", "160130payfile0007")/**副卡2帐号 */);
		bankaccbas3.setVvarexpress("otherbankaccbasaccnum3");
		itemlist.add(bankaccbas3);
		
		PrintTempletmanageItemVO bankname1 = new PrintTempletmanageItemVO();	
		bankname1.setVvarname(ResHelper.getString("60130payfile", "160130payfile0011")/**主卡银行名称 */);
		bankname1.setVvarexpress("otherbankdocname1");
		itemlist.add(bankname1);
		
		PrintTempletmanageItemVO bankname2 = new PrintTempletmanageItemVO();	
		bankname2.setVvarname(ResHelper.getString("60130payfile", "160130payfile0012")/**副卡1银行名称 */);
		bankname2.setVvarexpress("otherbankdocname2");
		itemlist.add(bankname2);
		
		PrintTempletmanageItemVO bankname3 = new PrintTempletmanageItemVO();	
		bankname3.setVvarname(ResHelper.getString("60130payfile", "160130payfile0010")/**副卡2银行名称 */);
		bankname3.setVvarexpress("otherbankdocname2");
		itemlist.add(bankname3);
		// end

		return itemlist;
	}
	/**
	 * 查找所有的公共薪资项目(薪资发放)
	 * guoqt
	 * @return
	 * @throws BusinessException
	 */
	public List queryClassItemPaydata(String pk_group, String pk_org)
	throws BusinessException {
		String sql = "select itemkey,name from wa_item where pk_org= '"+pk_group+"' or pk_org='"+pk_org+"' or pk_org='GLOBLE00000000000000'";

		List list = (List) new BaseDAO().executeQuery(sql, new MapListProcessor());
		List<PrintTempletmanageItemVO> itemlist = new ArrayList<PrintTempletmanageItemVO>();
		if(list!=null&&list.size()>0){
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);

				PrintTempletmanageItemVO item = new PrintTempletmanageItemVO();
				item.setVvarname(map.get("name")+"");
				item.setVvarexpress(map.get("itemkey")+"");
				itemlist.add(item);
			}
		}

		return itemlist;
	}
	

	@Override
	public PeriodVO queryDefaultPeriod(String pk_psndoc,Integer type)
			throws BusinessException {
		String sqlMul = "SELECT MAX(cstartdate) AS cstartdate ,MAX(cenddate) AS cenddate "
				+ "FROM wa_period,wa_periodstate,wa_payslip,wa_inludeclass,wa_data "
				+ "WHERE wa_period.pk_wa_period = wa_periodstate.pk_wa_period "
				+ "	AND wa_period.cyear = wa_payslip.accyear "
				+ "	AND wa_period.cperiod = wa_payslip.accmonth "
				+ "	AND wa_payslip.type = "+type+" "
				+ "	AND wa_periodstate.pk_wa_class = wa_inludeclass.pk_childclass "
				+ "	AND wa_inludeclass.pk_parentclass = wa_payslip.pk_wa_class "
				+ "	AND wa_inludeclass.cyear = wa_period.cyear "
				+ "	AND wa_inludeclass.cperiod = wa_period.cperiod "
				+ "	AND wa_periodstate.payoffflag = 'Y' "
				+ "	AND wa_data.pk_wa_class = wa_inludeclass.pk_childclass "
				+ "	AND wa_data.cyear = wa_inludeclass.cyear "
				+ "	AND wa_data.cperiod = wa_inludeclass.cperiod "
				+ "	AND wa_data.pk_psndoc = '"+pk_psndoc+"'"
				//guoqt查询未停发的薪资数据（为自助提供）
				+ "	and wa_data.stopflag='N' ";
		PeriodVO voMul = getPayslipDao().executeQueryVO(sqlMul, PeriodVO.class);

		String sqlSin = "SELECT MAX(cstartdate) AS cstartdate ,MAX(cenddate) AS cenddate "
				+ "FROM wa_period,wa_periodstate,wa_payslip,wa_data "
				+ "WHERE wa_period.pk_wa_period = wa_periodstate.pk_wa_period "
				+ "	AND wa_period.cyear = wa_payslip.accyear "
				+ "	AND wa_period.cperiod = wa_payslip.accmonth "
				+ "	AND wa_payslip.type = "+type+" "
				+ "	AND wa_periodstate.pk_wa_class = wa_data.pk_wa_class "
				+ "	AND wa_periodstate.payoffflag = 'Y' "
				+ "	AND wa_data.pk_wa_class = wa_payslip.pk_wa_class "
				+ "	AND wa_data.pk_psndoc = '"+pk_psndoc+"'"
				//guoqt原有关联表查询出现重复垃圾数据
				+ "	and wa_data.cyear=wa_period.cyear and wa_data.cperiod=wa_period.cperiod "
				//guoqt查询未停发的薪资数据（为自助提供）
				+ "	and wa_data.stopflag='N' ";

		PeriodVO voSin = getPayslipDao().executeQueryVO(sqlSin, PeriodVO.class);

		PeriodVO vo = new PeriodVO();

		if (voMul.getCstartdate() == null && voSin.getCstartdate() != null) {
			return voSin;
		} else if (voMul.getCstartdate() != null && voSin.getCstartdate() == null) {
			return voMul;
		} else if (voMul.getCstartdate() != null && voSin.getCstartdate() != null){

			if (voSin.getCstartdate().afterDate(voMul.getCstartdate())) {
				vo.setCstartdate(voSin.getCstartdate());
			}else{
				vo.setCstartdate(voMul.getCstartdate());
			}

			if (voSin.getCenddate().afterDate(voMul.getCenddate())) {
				vo.setCenddate(voSin.getCenddate());
			}else{
				vo.setCenddate(voMul.getCenddate());
			}

			return vo;
		} else{
			return null;
		}
	}

	@Override
	public List<Map<String, String>> getSalaryPswdFlag(String groupid){
		//返回结果集合
		List<Map<String, String>> resultList = new ArrayList<Map<String,String>>();
		Map<String, String> map = new HashMap<String, String>();
		//返回标志
		String flag = null;
		//返回信息
		String des = null;
		//返回参数值
		String param = "N";

		SysInitVO sysInitVO = null;
		try {
//			boolean isRight = WaCacheUtils.isModuleStarted(HRWACommonConstants.MODULEID_HRSS);
			boolean isRight = NCLocator.getInstance().lookup(ICreateCorpQueryService.class).isEnabled(groupid, HRWACommonConstants.MODULEID_HRSS);
			if (isRight) {
				sysInitVO = getISysInitQry().queryByParaCode(groupid, PARAM_INITCODE);
				if (sysInitVO != null
						&& !StringUtil.isEmptyWithTrim(sysInitVO.getValue())) {
					flag = "0";
					des = ResHelper.getString("6013payslp","06013payslp0185")/*@res "操作成功！"*/;
					param = sysInitVO.getValue();
					map.put("flag", flag);
					map.put("des", des);
					map.put("param", param);
					resultList.add(map);
					return resultList;
				}
			}else{
				flag = "0";
				des = ResHelper.getString("6013payslp","06013payslp0186")/*@res "没有安装自助系统，不需要查询密码。"*/;
				param = "N";
				map.put("flag", flag);
				map.put("des", des);
				map.put("param", param);
				resultList.add(map);
				return resultList;
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
			flag = "1";
			des = ResHelper.getString("6013payslp","06013payslp0183")/*@res "查询过程出现错误！"*/;
			map.put("flag", flag);
			map.put("des", des);
			map.put("param", param);
			resultList.add(map);
			return resultList;
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getSalary(String groupid, String userid,
			String salarypswd, String year, String period) {
		//返回结果集合
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		//返回薪资条Map
		Map<String,Object> map = new HashMap<String, Object>();;
		//年份
		String syear = null;
		//月度
		String smonth = null;
		//返回标志
		String flag = "1";
		//返回信息
		String des = null;
		//薪资条装载集合
		List<Map<String,Object>> salarystructlist = null;

		//薪资条VO数据
		List<MyPayslipVO> list = null;

	/*	map.put("flag", flag);
		map.put("des", des);
		map.put("salarystructlist", salarystructlist);
		resultList.add(map);*/

		//通过userid查询UserVO
		String pk_psndoc = null;  //对应业务员信息
		UserVO userVO = null;
		try {
			userVO = getUserQryServ().findUserByIDs( new String[]{userid})[0];
			if (userVO != null && userVO.getPk_base_doc() != null) {
				pk_psndoc = userVO.getPk_base_doc();
				if (NCLocator.getInstance().lookup(ICreateCorpQueryService.class).isEnabled(groupid, HRWACommonConstants.MODULEID_HRSS)) {
					SysInitVO sysInitVO = getISysInitQry().queryByParaCode(groupid, PARAM_INITCODE);
					if (salarypswd != null && sysInitVO != null && sysInitVO.getValue()!= null && "Y".equals(sysInitVO.getValue())) {
						String sql = "select wagepwd from hrss_profile where pk_profile = '" + pk_psndoc + "'";
						HashMap resultmap = (HashMap)getDao().executeQuery(sql, new MapProcessor());
						IProfileService service = NCLocator.getInstance().lookup(IProfileService.class);
						if (!service.existWaPwd(pk_psndoc)) {
							des = ResHelper.getString("6013payslp","06013payslp0192")/*@res "薪资查询密码不存在，请重置密码！"*/;
							map.put("des", des);
							return resultList;
						}else if(!service.checkWaPwd(pk_psndoc, salarypswd)){
							des = ResHelper.getString("6013payslp","06013payslp0180")/*@res "查询密码不正确！"*/;
							map.put("des", des);
							return resultList;
						}
					}else if (sysInitVO != null && sysInitVO.getValue()!= null && "Y".equals(sysInitVO.getValue())) {
						des = ResHelper.getString("6013payslp","06013payslp0193")/*@res "薪资查询密码不能为空！"*/;
						map.put("des", des);
						return resultList;
					}
				}
				//如果年份和月份同时为空，则查找最新发薪月份
				if (year == null || period == null || StringUtil.isEmptyWithTrim(year) || StringUtil.isEmptyWithTrim(period)) {
					PeriodVO periodVO = null;
					periodVO = this.queryDefaultPeriod(pk_psndoc, SendTypeEnum.MOBILE.toIntValue());
					if (periodVO != null && periodVO.getCenddate() != null) {
						UFLiteralDate busLiteralDate = periodVO.getCenddate();
						//按照最新期间查询数据
						list = this.querySelfAggPayslipVOs(
								getBusiBeginEndDate(busLiteralDate)[0],
								getBusiBeginEndDate(busLiteralDate)[1],
								pk_psndoc, SendTypeEnum.MOBILE.toIntValue());
						syear = "" + busLiteralDate.getYear();
						if (busLiteralDate.getMonth() < 10) {
							smonth = "0" + busLiteralDate.getMonth();
						}else{
							smonth = "" + busLiteralDate.getMonth();
						}
					}else{
						flag = "1";
						des = ResHelper.getString("6013payslp","06013payslp0182")/*@res "无最新发放的期间数据！"*/;
					}
				}else{
					//如果年度和月份有数据直接查询
					if (period.length() == 1 && Integer.parseInt(period) < 10) {
						period = "0" + period ;
					}
					syear = year ;
					smonth = period;
					UFLiteralDate busLiteralDate = new UFLiteralDate(year + "-" + period + "-15");
					list = this.querySelfAggPayslipVOs(
							getBusiBeginEndDate(busLiteralDate)[0],
							getBusiBeginEndDate(busLiteralDate)[1],
							pk_psndoc, SendTypeEnum.MOBILE.toIntValue());
				}
				/*加载数据过程*/
				if (list != null && list.size() > 0) {

					/*处理0值是否显示问题*/
					Map<String,UFBoolean> map4Org = new HashMap<String, UFBoolean>();
					try {
						for (int i = 0; i < list.size(); i++) {
							if (map4Org.containsKey(list.get(i).getPk_org())) {
								continue;
							}
							map4Org.put(list.get(i).getPk_org(), SysinitAccessor.getInstance().getParaBoolean(
									list.get(i).getPk_org(), ParaConstant.ZERO_SEND));
						}
					} catch (Exception e) {
						Logger.error(e.getMessage(), e);
					}
					//查询成功
					flag = "0";
					des = ResHelper.getString("6013payslp","06013payslp0185")/*@res "操作成功！"*/;
					salarystructlist = new ArrayList<Map<String,Object>>();
					Map<String,Object> salarystruct = null;
					for (int i = 0; i < list.size(); i++) {
						salarystruct = new HashMap<String, Object>();
						String moneyType = list.get(i).getMoneyType();
						String pk_org = list.get(i).getPk_org();
						UFBoolean isShowZero = map4Org.get(pk_org);
						if (list.get(i).getBatch() != 0) {
							salarystruct.put("splanname", list.get(i).getWaClassName() + "-" + MessageFormat.format(ResHelper.getString("6013payslp","06013payslp0188")/*@res "第{0}次发放"*/,list.get(i).getBatch()) + "（" + moneyType + "）" );/* -=notranslate=- */
						}else{
							salarystruct.put("splanname", list.get(i).getWaClassName() + "（" + moneyType + "）");/* -=notranslate=- */
						}
						PaySlipItemValueVO[] paySlipVOs = list.get(i).getPaySlipVOs();
						//薪资条项目装载集合
						List<Map<String,String>> salarydetaillist = null;
						if (paySlipVOs != null && paySlipVOs.length > 0) {
							salarydetaillist = new ArrayList<Map<String,String>>();
							for (int j = 0; j < paySlipVOs.length; j++) {
								//薪资参数-0值显示处理
								if( isShowZero != null && !isShowZero.booleanValue() && paySlipVOs[j].getDataType().intValue() == 2 && paySlipVOs[j].getValue() != null && Double.valueOf(paySlipVOs[j].getValue().toString()) == 0.0D){
									continue;
								}
								if (paySlipVOs[j].getValue() != null && !"".equals(paySlipVOs[j].getValue().toString().trim())) {
									Map<String,String> itemMap = new HashMap<String, String>();
									itemMap.put("showtitle", paySlipVOs[j].getName());
									itemMap.put("showcontent", paySlipVOs[j].getValue().toString());
									salarydetaillist.add(itemMap);
								}
							}
							salarystruct.put("salarydetaillist", salarydetaillist);
						}
						salarystructlist.add(salarystruct);
					}
					map.put("salarystructlist", salarystructlist);
				}else{
					flag = "2";
					des = ResHelper.getString("6013payslp","06013payslp0187")/*@res "查询期间无薪资发放数据！"*/;
					salarystructlist = null;
					map.put("flag", flag);
					map.put("des", des);
					map.put("salarystructlist", salarystructlist);
					resultList.add(map);
					return resultList;
				}
			}else{
				flag = "1";
				des = ResHelper.getString("6013payslp","06013payslp0181")/*@res "关联人员信息不正确！"*/;
				salarystructlist = null;
				map.put("flag", flag);
				map.put("des", des);
				map.put("salarystructlist", salarystructlist);
				resultList.add(map);
				return resultList;
			}
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
			flag = "1";
			des = ResHelper.getString("6013payslp","06013payslp0183")/*@res "查询过程出现错误！"*/;
			salarystructlist = null;
			map.put("flag", flag);
			map.put("des", des);
			map.put("salarystructlist", salarystructlist);
			resultList.add(map);
			return resultList;
		}
		map.put("syear", syear);
		map.put("smonth", smonth);
		map.put("flag", flag);
		map.put("des", des);
		map.put("salarystructlist", salarystructlist);
		resultList.add(map);
		return resultList;
	}

	/**
	 * 是否需要输入密码参数查询服务
	 */

	private static ISysInitQry getISysInitQry() {
		if(sysInitQry == null){
			sysInitQry = NCLocator.getInstance().lookup(ISysInitQry.class);
		}
		return sysInitQry;
	}

	/**
	 * 用户VO查询服务
	 */
	private static IUserManageQuery getUserQryServ() {
		if(userQryServ == null){
			userQryServ = NCLocator.getInstance().lookup(IUserManageQuery.class);
		}
		return userQryServ;
	}

	/**
	 * 根据当前业务日期获得起始日期和截至日期
	 *
	 * @return
	 */
	private String[] getBusiBeginEndDate(UFLiteralDate busLiteralDate) {
		// 业务日期所属月的第一天
		String beginDate = busLiteralDate.toString().substring(0, 7) + "-01";
		// 业务日期所属月的最后一天
		String endDate = busLiteralDate.toString().substring(0, 7) + "-"
				+ UFLiteralDate.getDaysMonth(busLiteralDate.getYear(), busLiteralDate.getMonth());
		return new String[] { beginDate, endDate };
	}

	/**
	 * 获取BaseDao
	 * @return
	 */
	public BaseDAO getDao()
	{
		if (dao == null)
		{
			dao = new BaseDAO();
		}
		return dao;
	}
	
	@Override
	public List<Map<String, Object>> getSalary4Mobile(String groupid,
			String userid, String salarypswd, String year, String period) {
		return getSalary(groupid, userid, salarypswd, year, period);
	}
	
	@Override
	public List<Map<String, String>> getSalaryPswdFlag4Mobile(String groupid) {
		// TODO Auto-generated method stub
		return getSalaryPswdFlag(groupid);
	}
}
