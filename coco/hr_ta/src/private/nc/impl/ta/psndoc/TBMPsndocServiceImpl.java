package nc.impl.ta.psndoc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.FromWhereSQLUtils;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.ITimeScope;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.itf.ta.algorithm.TimeScopeUtils;
import nc.itf.ta.algorithm.impl.DefaultDateScope;
import nc.itf.ta.algorithm.impl.DefaultTimeScope;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hr.frame.persistence.BooleanProcessor;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.bill.ITimeScopeBillBodyVO;
import nc.vo.ta.psndoc.TBMPsndocSqlPiecer;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.psndoc.TbmPropEnum;
import nc.vo.ta.pub.PubPermissionUtils;
import nc.vo.ta.pub.SQLParamWrapper;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;







public class TBMPsndocServiceImpl
  implements ITBMPsndocQueryService
{
  private final String DOC_NAME = "tbmpsndoc";
  



  private SimpleDocServiceTemplate serviceTemplate;
  



  public boolean checkTBMPsndocDate(String pk_hrorg, String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    try
    {
      String condition = " pk_org = '" + pk_hrorg + "' and pk_psndoc = '" + pk_psndoc + "' ";
      
      DefaultDateScope dateScope = new DefaultDateScope();
      dateScope.setBegindate(beginDate);
      dateScope.setEnddate(endDate);
      return DateScopeUtils.mergeContains((IDateScope[])getServiceTemplate().queryByCondition(TBMPsndocVO.class, condition), dateScope);
    }
    catch (Exception e) {
      Logger.error(e.getMessage(), e);
      throw new BusinessException(e.getMessage());
    }
  }
  
  private SimpleDocServiceTemplate getServiceTemplate() {
    if (serviceTemplate == null)
    {
      serviceTemplate = new SimpleDocServiceTemplate("tbmpsndoc");
    }
    return serviceTemplate;
  }
  
  public TBMPsndocVO queryByPsnDateTime(String pk_hrorg, String pk_psndoc, UFDateTime time)
    throws BusinessException
  {
    return null;
  }
  

  public TBMPsndocVO[] queryLatestByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate begindate, UFLiteralDate enddate)
    throws BusinessException
  {
    return queryLatestByCondition(pk_hrorg, fromWhereSQL, begindate, enddate, false);
  }
  
  private TBMPsndocVO[] queryLatestByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate begindate, UFLiteralDate enddate, boolean isHRAdminOrg)
    throws BusinessException
  {
    fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, enddate.toStdString());
    SQLParamWrapper w = TBMPsndocSqlPiecer.getLatestPsndocWhereCond(begindate.toString(), enddate.toString(), StringUtils.isNotEmpty(pk_hrorg));
    


    return new TBMPsndocDAO().queryByCondition(pk_hrorg, fromWhereSQL, w.getSql(), w.getParam(), null, true, true, isHRAdminOrg);
  }
  

  public String[] queryLatestPsndocPksByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate begindate, UFLiteralDate enddate)
    throws BusinessException
  {
    SQLParamWrapper w = TBMPsndocSqlPiecer.getLatestPsndocWhereCond(begindate.toString(), enddate.toString(), StringUtils.isNotEmpty(pk_hrorg));
    

    return new TBMPsndocDAO().queryPsndocPksByCondition(pk_hrorg, fromWhereSQL, w.getSql(), w.getParam());
  }
  


  public TBMPsndocVO[] queryLatestByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, String[] pk_psndocs, UFLiteralDate begindate, UFLiteralDate enddate)
    throws BusinessException
  {
    fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, enddate.toStdString());
    fromWhereSQL = TBMPsndocSqlPiecer.addPsnjobPermission2PsnjobQuerySQL(fromWhereSQL);
    SQLParamWrapper w = TBMPsndocSqlPiecer.getLatestPsndocWhereCond(begindate.toString(), enddate.toString());
    String where = w.getSql();
    if (ArrayUtils.isEmpty(pk_psndocs)) {
      return new TBMPsndocDAO().queryByCondition(pk_hrorg, fromWhereSQL, where, w.getParam(), true, false);
    }
    InSQLCreator isc = new InSQLCreator();
    String inSQL = isc.getInSQL(pk_psndocs);
    where = where + (w.getSql() == null ? "" : " and ") + TBMPsndocVO.getDefaultTableName() + "." + "pk_psndoc" + " in (" + inSQL + ") ";
    return new TBMPsndocDAO().queryByCondition(pk_hrorg, fromWhereSQL, where, w.getParam(), true, false);
  }
  





  public TBMPsndocVO[] queryLatestMachineDocByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate begindate, UFLiteralDate enddate)
    throws BusinessException
  {
    fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, enddate.toStdString());
    fromWhereSQL = PubPermissionUtils.addPubQueryPermission2FromWhereSQL(fromWhereSQL, TBMPsndocVO.getDefaultTableName(), TBMPsndocVO.getDefaultTableName(), "pk_psnjob");
    String extraCond = "{0}.tbm_prop=2";
    SQLParamWrapper w = TBMPsndocSqlPiecer.getLatestPsndocWhereCond("{0}", begindate.toString(), enddate.toString(), extraCond, StringUtils.isNotEmpty(pk_hrorg));
    TBMPsndocVO[] tbmpsndocs = new TBMPsndocDAO().queryByCondition(pk_hrorg, fromWhereSQL, w.getSql(), w.getParam());
    return tbmpsndocs;
  }
  



  public String[] queryMachineDocByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate begindate, UFLiteralDate enddate)
    throws BusinessException
  {
    fromWhereSQL = PubPermissionUtils.addPubQueryPermission2FromWhereSQL(fromWhereSQL, TBMPsndocVO.getDefaultTableName(), TBMPsndocVO.getDefaultTableName(), "pk_psnjob");
    String extraCond = "{0}.tbm_prop=2";
    SQLParamWrapper w = TBMPsndocSqlPiecer.getLatestPsndocWhereCond("{0}", begindate.toString(), enddate.toString(), extraCond, StringUtils.isNotEmpty(pk_hrorg));
    
    String[] psndocs = new TBMPsndocDAO().queryPsndocPksByCondition(pk_hrorg, fromWhereSQL, w.getSql(), w.getParam());
    return psndocs;
  }
  

  public TBMPsndocVO[] queryLatestManualDocByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate begindate, UFLiteralDate enddate)
    throws BusinessException
  {
    fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, enddate.toStdString());
    String extraCond = "{0}.tbm_prop=1";
    SQLParamWrapper w = TBMPsndocSqlPiecer.getLatestPsndocWhereCond("{0}", begindate.toString(), enddate.toString(), extraCond, StringUtils.isNotEmpty(pk_hrorg));
    TBMPsndocVO[] tbmpsndocs = new TBMPsndocDAO().queryByCondition(pk_hrorg, fromWhereSQL, w.getSql(), w.getParam());
    return tbmpsndocs;
  }
  

  public ArrayList<GeneralVO> queryTaPsninfoByCondition(String strWhere)
    throws BusinessException
  {
    return new TBMPsndocDAO().queryTaPsninfoByCondition(strWhere);
  }
  
  public ArrayList<GeneralVO> queryTaPsninfoByPks(String[] pks) throws BusinessException
  {
    return new TBMPsndocDAO().queryTaPsninfoByPks(pks);
  }
  

  public Map<String, List<TBMPsndocVO>> queryTBMPsndocMapByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, boolean processIntersection)
    throws BusinessException
  {
    return queryTBMPsndocMapByCondition(pk_hrorg, fromWhereSQL, beginDate, endDate, processIntersection, null);
  }
  


























  public Map<String, List<TBMPsndocVO>> queryTBMPsndocMapByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, boolean processIntersection, TbmPropEnum tbm_prop)
    throws BusinessException
  {
    return queryTBMPsndocMapByCondition(pk_hrorg, fromWhereSQL, beginDate, endDate, processIntersection, false, tbm_prop);
  }
  


  public Map<String, List<TBMPsndocVO>> queryTBMPsndocMapByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, boolean processIntersection, boolean queryJobOrg, TbmPropEnum tbm_prop)
    throws BusinessException
  {
    TBMPsndocVO[] psndocVOs = null;
    if (tbm_prop == null) {
      psndocVOs = queryLatestByCondition(pk_hrorg, fromWhereSQL, beginDate, endDate);
    } else if (TbmPropEnum.MACHINE_CHECK.equals(tbm_prop)) {
      psndocVOs = queryLatestMachineDocByCondition(pk_hrorg, fromWhereSQL, beginDate, endDate);
    } else {
      psndocVOs = queryLatestManualDocByCondition(pk_hrorg, fromWhereSQL, beginDate, endDate);
    }
    if (ArrayUtils.isEmpty(psndocVOs)) {
      return null;
    }
    return queryTBMPsndocMapByPsndocs0(pk_hrorg, SQLHelper.getStrArray(psndocVOs, "pk_psndoc"), beginDate, endDate, processIntersection, queryJobOrg, tbm_prop);
  }
  




  public Map<String, List<TBMPsndocVO>> queryTBMPsndocMapByConditionForTeam(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate beginDate, UFLiteralDate endDate, boolean processIntersection, boolean queryJobOrg, TbmPropEnum tbm_prop)
    throws BusinessException
  {
    String extraCond = null;
    if ((tbm_prop != null) && (!tbm_prop.equals(TbmPropEnum.NOT_CHECK))) {
      extraCond = "{0}.tbm_prop=" + tbm_prop.value();
    }
    String dateCond = "({0}.begindate<=? and {0}.enddate>=?)  ";//leo   //and psnjob.lastflag='Y' and psnjob.endflag='N'
//    String dateCond = "{0}.begindate<=? and {0}.enddate>=? ";
    if (StringUtils.isEmpty(extraCond)) {
      extraCond = dateCond;
    } else {
      extraCond = SQLHelper.appendExtraCond(extraCond, dateCond);
    }
    SQLParameter para = new SQLParameter();
    para.addParam(endDate.toString());
    para.addParam(beginDate.toString());
    fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, endDate.toStdString());
    TBMPsndocVO[] vos = new TBMPsndocDAO().queryByCondition(pk_hrorg, fromWhereSQL, extraCond, para, new String[] { "{0}.begindate" }, queryJobOrg, false, false);
    
    return createPsnMap(vos, beginDate, endDate, processIntersection);
  }
  

  private Map<String, List<TBMPsndocVO>> queryTBMPsndocMapByPsndocs0(String pk_hrorg, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate, boolean processIntersection, boolean queryJobOrg, TbmPropEnum tbm_prop)
    throws BusinessException
  {
    FromWhereSQL fromWhereSQL = null;
    try {
      if (!ArrayUtils.isEmpty(pk_psndocs)) {
        fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
      }
      String extraCond = null;
      if ((tbm_prop != null) && (!tbm_prop.equals(TbmPropEnum.NOT_CHECK))) {
        extraCond = "{0}.tbm_prop=" + tbm_prop.value();
      }
      String dateCond = "{0}.begindate<=? and {0}.enddate>=? ";
      if (StringUtils.isEmpty(extraCond)) {
        extraCond = dateCond;
      } else {
        extraCond = SQLHelper.appendExtraCond(extraCond, dateCond);
      }
      SQLParameter para = new SQLParameter();
      para.addParam(endDate.toString());
      para.addParam(beginDate.toString());
      fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, endDate.toStdString());
      TBMPsndocVO[] vos = new TBMPsndocDAO().queryByCondition(pk_hrorg, fromWhereSQL, extraCond, para, new String[] { "{0}.begindate" }, queryJobOrg, false, false);
      
      return createPsnMap(vos, beginDate, endDate, processIntersection);
    }
    finally
    {
      TBMPsndocSqlPiecer.clearQuerySQL(fromWhereSQL);
    }
  }
  

  public Map<String, List<TBMPsndocVO>> queryTBMPsndocMapByPsndocs(String pk_hrorg, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate, boolean processIntersection)
    throws BusinessException
  {
    return queryTBMPsndocMapByPsndocs(pk_hrorg, pk_psndocs, beginDate, endDate, processIntersection, null);
  }
  


  public Map<String, List<TBMPsndocVO>> queryTBMPsndocMapByPsndocs(String pk_hrorg, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate, boolean processIntersection, TbmPropEnum tbm_prop)
    throws BusinessException
  {
    return queryTBMPsndocMapByPsndocs(pk_hrorg, pk_psndocs, beginDate, endDate, processIntersection, false, tbm_prop);
  }
  


  public Map<String, List<TBMPsndocVO>> queryTBMPsndocMapByPsndocs(String pk_hrorg, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate, boolean processIntersection, boolean queryJobOrg, TbmPropEnum tbm_prop)
    throws BusinessException
  {
    return queryTBMPsndocMapByPsndocs0(pk_hrorg, pk_psndocs, beginDate, endDate, processIntersection, queryJobOrg, tbm_prop);
  }
  

















  private Map<String, List<TBMPsndocVO>> createPsnMap(TBMPsndocVO[] vos, UFLiteralDate beginDate, UFLiteralDate endDate, boolean processIntersection)
  {
    if (ArrayUtils.isEmpty(vos)) {
      return null;
    }
    
    boolean existsMutiRcdPsn = false;
    Map<String, UFLiteralDate> newestDateMap = new HashMap();
    for (TBMPsndocVO vo : vos) {
      if (!newestDateMap.containsKey(vo.getPk_psndoc())) {
        newestDateMap.put(vo.getPk_psndoc(), vo.getBegindate());
      }
      else {
        existsMutiRcdPsn = true;
        if (vo.getBegindate().after((UFLiteralDate)newestDateMap.get(vo.getPk_psndoc()))) {
          newestDateMap.put(vo.getPk_psndoc(), vo.getBegindate());
        }
      }
    }
    if (!existsMutiRcdPsn) {
      Map<String, List<TBMPsndocVO>> map = new LinkedHashMap();
      for (TBMPsndocVO vo : vos) {
        List<TBMPsndocVO> list = new ArrayList(1);
        if (processIntersection) {
          if (beginDate.after(vo.getBegindate())) {
            vo.setBegindate(beginDate);
          }
          if (endDate.before(vo.getEnddate())) {
            vo.setEnddate(endDate);
          }
        }
        list.add(vo);
        map.put(vo.getPk_psndoc(), list);
      }
      return map;
    }
    
    Map<String, List<TBMPsndocVO>> map = new HashMap();
    Map<String, List<TBMPsndocVO>> returnMap = new LinkedHashMap();
    for (TBMPsndocVO vo : vos) {
      List<TBMPsndocVO> list = (List)map.get(vo.getPk_psndoc());
      if (list == null) {
        list = new ArrayList();
        map.put(vo.getPk_psndoc(), list);
      }
      list.add(vo);
      
      if (vo.getBegindate().equals(newestDateMap.get(vo.getPk_psndoc()))) {
        returnMap.put(vo.getPk_psndoc(), list);
      }
      if (processIntersection) {
        if (beginDate.after(vo.getBegindate())) {
          vo.setBegindate(beginDate);
        }
        if (endDate.before(vo.getEnddate())) {
          vo.setEnddate(endDate);
        }
      }
    }
    
    if (MapUtils.isNotEmpty(map)) {
      for (String key : map.keySet()) {
        List<TBMPsndocVO> list = (List)map.get(key);
        if (list.size() != 1)
        {

          Collections.sort(list); }
      }
    }
    return returnMap;
  }
  


  public String[] queryLatestPsndocsByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate begindate, UFLiteralDate enddate)
    throws BusinessException
  {
    TBMPsndocVO[] psndocVOs = queryLatestByCondition(pk_hrorg, fromWhereSQL, begindate, enddate);
    if (ArrayUtils.isEmpty(psndocVOs)) {
      return null;
    }
    String[] retArray = new String[psndocVOs.length];
    for (int i = 0; i < retArray.length; i++) {
      retArray[i] = psndocVOs[i].getPk_psndoc();
    }
    return retArray;
  }
  
  public Map<String, List<TBMPsndocVO>> queryTBMPsndocMapByPsndocs(String pk_hrorg, String[] pk_psndocs)
    throws BusinessException
  {
    return queryTBMPsndocMapByPsndocs(pk_hrorg, pk_psndocs, false);
  }
  

  public Map<String, List<TBMPsndocVO>> queryTBMPsndocMapByPsndocs(String pk_hrorg, String[] pk_psndocs, boolean queryJobOrg)
    throws BusinessException
  {
    return CommonUtils.group2ListByField("pk_psndoc", queryTBMPsndocVOsByPsndocs(pk_hrorg, pk_psndocs, queryJobOrg));
  }
  
  public TBMPsndocVO[] queryTBMPsndocVOsByPsndocs(String pk_hrorg, String[] pk_psndocs, boolean queryJobOrg)
    throws BusinessException
  {
    if (ArrayUtils.isEmpty(pk_psndocs)) {
      return null;
    }
    if (!queryJobOrg) {
      InSQLCreator isc = new InSQLCreator();
      String inSql = isc.getInSQL(pk_psndocs);
      Collection<TBMPsndocVO> col = new BaseDAO().retrieveByClause(TBMPsndocVO.class, "pk_org='" + pk_hrorg + "' and " + "pk_psndoc" + " in(" + inSql + ")", "begindate");
      if (org.apache.commons.collections.CollectionUtils.isEmpty(col))
        return null;
      return (TBMPsndocVO[])col.toArray(new TBMPsndocVO[0]);
    }
    FromWhereSQL fromWhereSQL = null;
    try {
      fromWhereSQL = TBMPsndocSqlPiecer.createPsndocArrayQuerySQL(pk_psndocs);
      fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgTable(fromWhereSQL);
      String tbmpsndocAlias = fromWhereSQL.getTableAliasByAttrpath(".");
      fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, tbmpsndocAlias + "." + "enddate");
      String orgversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob.pk_org_v" + FromWhereSQLUtils.getAttPathPostFix());
      String deptversionAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob.pk_dept_v" + FromWhereSQLUtils.getAttPathPostFix());
      String orgAlias = fromWhereSQL.getTableAliasByAttrpath("pk_psnjob.pk_org" + FromWhereSQLUtils.getAttPathPostFix());
      String mainTableCond = null;
      if (StringUtils.isNotEmpty(pk_hrorg)) {
        mainTableCond = "{0}.pk_org='" + pk_hrorg + "' ";
      }
      String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, TBMPsndocVO.getDefaultTableName(), null, new String[] { orgAlias + "." + "pk_adminorg" + " as " + "pk_joborg", orgversionAlias + "." + "pk_vid" + " as " + "pk_org_v", deptversionAlias + "." + "pk_vid" + " as " + "pk_dept_v" }, null, mainTableCond, null);
      





      TBMPsndocVO[] retArray = (TBMPsndocVO[])CommonUtils.toArray(TBMPsndocVO.class, (List)new BaseDAO().executeQuery(sql + " order by " + tbmpsndocAlias + "." + "begindate", new BeanListProcessor(TBMPsndocVO.class)));
      processTimeZone(pk_hrorg, retArray);
      return retArray;
    }
    finally {
      TBMPsndocSqlPiecer.clearQuerySQL(fromWhereSQL);
    }
  }
  
  protected void processTimeZone(String pk_hrorg, TBMPsndocVO[] vos) throws BusinessException {
    if (ArrayUtils.isEmpty(vos)) {
      return;
    }
    Map<String, TimeZone> timeZoneMap = ((ITimeRuleQueryService)NCLocator.getInstance().lookup(ITimeRuleQueryService.class)).queryTimeZoneMap(pk_hrorg);
    for (TBMPsndocVO vo : vos) {
      vo.setTimezone((TimeZone)timeZoneMap.get(vo.getPk_joborg()));
    }
  }
  

  public List<TBMPsndocVO> queryTBMPsndocListByPsndoc(String pk_hrorg, String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate, boolean processIntersection)
    throws BusinessException
  {
    return queryTBMPsndocListByPsndoc(pk_hrorg, pk_psndoc, beginDate, endDate, processIntersection, null);
  }
  
  public List<TBMPsndocVO> queryTBMPsndocListByPsndoc(String pk_hrorg, String pk_psndoc) throws BusinessException
  {
    Map<String, List<TBMPsndocVO>> map = queryTBMPsndocMapByPsndocs(pk_hrorg, new String[] { pk_psndoc });
    if ((map == null) || (!map.containsKey(pk_psndoc))) {
      return null;
    }
    return (List)map.get(pk_psndoc);
  }
  

  public List<TBMPsndocVO> queryTBMPsndocListByPsndoc(String pk_hrorg, String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate, boolean processIntersection, TbmPropEnum tbmProp)
    throws BusinessException
  {
    Map<String, List<TBMPsndocVO>> map = queryTBMPsndocMapByPsndocs(pk_hrorg, new String[] { pk_psndoc }, beginDate, endDate, processIntersection, tbmProp);
    if ((map == null) || (!map.containsKey(pk_psndoc))) {
      return null;
    }
    return (List)map.get(pk_psndoc);
  }
  
  public TBMPsndocVO[] queryByCondition(String pk_org, FromWhereSQL fromWhereSQL, UFLiteralDate date)
    throws BusinessException
  {
    return queryByCondition(pk_org, fromWhereSQL, date, false, false);
  }
  

  public TBMPsndocVO[] queryByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate date, boolean queryPkJobOrg, boolean queryPkDept)
    throws BusinessException
  {
    return queryByCondition(pk_hrorg, fromWhereSQL, date, queryPkJobOrg, queryPkDept, false);
  }
  
  private TBMPsndocVO[] queryByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate date, boolean queryPkJobOrg, boolean queryPkDept, boolean isHRAdminOrg)
    throws BusinessException
  {
    fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, date.toStdString());
    String extraCond = "? between " + TBMPsndocSqlPiecer.TBM_PSNDOC_BEGINDATE_FIELD + " and " + TBMPsndocSqlPiecer.TBM_PSNDOC_ENDDATE_FIELD;
    SQLParameter para = new SQLParameter();
    para.addParam(date.toString());
    return new TBMPsndocDAO().queryByCondition(pk_hrorg, fromWhereSQL, extraCond, para, null, queryPkJobOrg, queryPkDept, isHRAdminOrg);
  }
  


  public Map<String, List<TBMPsndocVO>> queryTBMPsndocMapByPsndocInSQL(String pk_hrorg, String psndocInSQL, UFLiteralDate beginDate, UFLiteralDate endDate, boolean processIntersection)
    throws BusinessException
  {
    return queryTBMPsndocMapByPsndocInSQL(pk_hrorg, psndocInSQL, beginDate, endDate, processIntersection, false);
  }
  


  public Map<String, List<TBMPsndocVO>> queryTBMPsndocMapByPsndocInSQL(String pk_hrorg, String psndocInSQL, UFLiteralDate beginDate, UFLiteralDate endDate, boolean processIntersection, boolean queryPkJobOrg)
    throws BusinessException
  {
    return createPsnMap(new TBMPsndocDAO().queryByPsndocInSQL(pk_hrorg, psndocInSQL, beginDate, endDate, queryPkJobOrg), beginDate, endDate, processIntersection);
  }
  
  public void checkTBMPsndocTime(String pk_hrorg, String pk_psndoc, UFDateTime beginTime, UFDateTime endTime)
    throws BusinessException
  {
    String condition = " pk_org = '" + pk_hrorg + "' and pk_psndoc = '" + pk_psndoc + "' ";
    checkTBMPsndocTime(pk_hrorg, pk_psndoc, beginTime, endTime, condition);
  }
  
  private void checkTBMPsndocTime(String pk_hrorg, String pk_psndoc, UFDateTime beginTime, UFDateTime endTime, String condition)
    throws BusinessException
  {
    List<ITimeScope> psnTimeScopes = new ArrayList();
    TBMPsndocVO[] psndocvos = (TBMPsndocVO[])getServiceTemplate().queryByCondition(TBMPsndocVO.class, condition);
    if (ArrayUtils.isEmpty(psndocvos)) {
      throw new BusinessException(ResHelper.getString("6017psndoc", "06017psndoc0129"));
    }
    


    for (TBMPsndocVO psndocvo : psndocvos) {
      DefaultTimeScope psnTimeScope = new DefaultTimeScope();
      
      psnTimeScope.setScopeStartDateTime(new UFDateTime(psndocvo.getBegindate().getDateBefore(1) + " 00:00:00"));
      
      psnTimeScope.setScopeEndDateTime(new UFDateTime(psndocvo.getEnddate().getDateAfter(1) + " 23:59:59"));
      psnTimeScopes.add(psnTimeScope);
    }
    
    ITimeScope[] scopes = TimeScopeUtils.mergeTimeScopes((ITimeScope[])psnTimeScopes.toArray(new DefaultTimeScope[0]));
    if (ArrayUtils.isEmpty(scopes)) {
      throw new BusinessException(ResHelper.getString("6017psndoc", "06017psndoc0130"));
    }
    



    DefaultTimeScope paramTimeScope = new DefaultTimeScope(beginTime, endTime);
    
    if (!TimeScopeUtils.contains(scopes, paramTimeScope)) {
      throw new BusinessException(ResHelper.getString("6017psndoc", "06017psndoc0131", new String[] { getPsnName(pk_psndoc) }));
    }
  }
  


  public void checkTBMPsndocTimeForMachinePsn(String pk_hrorg, String pk_psndoc, UFDateTime beginTime, UFDateTime endTime)
    throws BusinessException
  {
    String condition = " pk_org = '" + pk_hrorg + "' and pk_psndoc = '" + pk_psndoc + "' and tbm_prop=" + 2;
    try {
      checkTBMPsndocTime(pk_hrorg, pk_psndoc, beginTime, endTime, condition);
    } catch (BusinessException e) {
      IPersistenceRetrieve perstRetrieve = (IPersistenceRetrieve)NCLocator.getInstance().lookup(IPersistenceRetrieve.class);
      PsndocVO psndocvo = (PsndocVO)perstRetrieve.retrieveByPk(null, PsndocVO.class, pk_psndoc);
      if (psndocvo != null) {
        String psnname = psndocvo.getName();
        throw new BusinessException(psnname + ResHelper.getString("6017psndoc", "06017psndoc0132"));
      }
      
      throw new BusinessException(ResHelper.getString("6017psndoc", "06017psndoc0133"));
    }
  }
  






  public String getPsnName(String pk_psndoc)
    throws BusinessException
  {
    PsndocVO psndocvo = queryPsnDocVOByPk(pk_psndoc);
    return MultiLangHelper.getName(psndocvo);
  }
  






  private PsndocVO queryPsnDocVOByPk(String pk_psndoc)
    throws BusinessException
  {
    return (PsndocVO)new BaseDAO().retrieveByPK(PsndocVO.class, pk_psndoc);
  }
  
  public TBMPsndocVO queryTBMPsndocByPsndoc(String pk_hrorg, String pk_psndoc, UFLiteralDate date)
    throws BusinessException
  {
    boolean hasPkOrg = StringUtils.isNotEmpty(pk_hrorg);
    String cond = "";
    if (hasPkOrg) {
      cond = cond + "pk_org=? and ";
    }
    cond = cond + "pk_psndoc=? and ? between begindate and enddate";
    SQLParameter params = new SQLParameter();
    if (hasPkOrg) {
      params.addParam(pk_hrorg);
    }
    params.addParam(pk_psndoc);
    params.addParam(date.toString());
    Collection<TBMPsndocVO> col = new BaseDAO().retrieveByClause(TBMPsndocVO.class, cond, params);
    if (org.springframework.util.CollectionUtils.isEmpty(col)) {
      return null;
    }
    return ((TBMPsndocVO[])col.toArray(new TBMPsndocVO[0]))[0];
  }
  








  public List<GeneralVO> queryTBMPsndocGeneralVOList(String pk_hrorg, UFLiteralDate beginDate, UFLiteralDate endDate, String condStr)
    throws BusinessException
  {
    String sql = " select tbm_psndoc.begindate,tbm_psndoc.pk_psndoc,tbm_psndoc.pk_psnjob,pk_tbm_psndoc,timecardid,bd_psndoc.code psncode, bd_psndoc.name psnname from tbm_psndoc inner join bd_psndoc on tbm_psndoc.pk_psndoc = bd_psndoc.pk_psndoc  where tbm_psndoc.pk_org = ?  and (tbm_psndoc.begindate <=? and tbm_psndoc.enddate >= ?) ";
    

    if (!StringUtils.isEmpty(condStr)) {
      sql = sql + " and " + condStr;
    }
    SQLParameter parameter = new SQLParameter();
    parameter.addParam(pk_hrorg);
    parameter.addParam(endDate.toString());
    parameter.addParam(beginDate.toString());
    
    return (ArrayList)new BaseDAO().executeQuery(sql, parameter, new BeanListProcessor(GeneralVO.class));
  }
  

  public TBMPsndocVO[] queryTBMPsndocExcSeveral(String pk_hrorg, String pk_sndoc, UFLiteralDate beginDate, UFLiteralDate endDate, String[] excludePk_tbmpsndocs)
    throws BusinessException
  {
    String cond = "pk_org=? and begindate<=? and enddate>=? and pk_psndoc = ? ";
    
    SQLParameter para = new SQLParameter();
    para.addParam(pk_hrorg);
    para.addParam(endDate.toString());
    para.addParam(beginDate.toString());
    para.addParam(pk_sndoc);
    if (!ArrayUtils.isEmpty(excludePk_tbmpsndocs)) {
      if (excludePk_tbmpsndocs.length == 1) {
        cond = cond + " and pk_tbm_psndoc<>?";
        para.addParam(excludePk_tbmpsndocs[0]);
      } else {
        cond = cond + " and pk_tbm_psndoc not in(" + StringPiecer.getDefaultPiecesTogether(excludePk_tbmpsndocs) + ")";
      }
    }
    return (TBMPsndocVO[])CommonUtils.toArray(TBMPsndocVO.class, new BaseDAO().retrieveByClause(TBMPsndocVO.class, cond, para));
  }
  
  public void checkTBMPsndocTime(String pk_hrorg, String pk_psndoc, ITimeScope timeScope) throws BusinessException
  {
    checkTBMPsndocTime(pk_hrorg, pk_psndoc, timeScope.getScope_start_datetime(), timeScope.getScope_end_datetime());
  }
  
  public void checkTBMPsndocTime(String pk_hrorg, String pk_psndoc, ITimeScope[] timeScopes) throws BusinessException
  {
    if (ArrayUtils.isEmpty(timeScopes)) {
      return;
    }
    for (ITimeScope timeScope : timeScopes) {
      checkTBMPsndocTime(pk_hrorg, pk_psndoc, timeScope);
    }
  }
  
  public void checkTBMPsndocDate(String pk_hrorg, String pk_psndoc, IDateScope dateScope) throws BusinessException
  {
    checkTBMPsndocDate(pk_hrorg, new String[] { pk_psndoc }, new IDateScope[] { dateScope });
  }
  









  public void checkTBMPsndocDate(String pk_hrorg, String pk_psndoc, IDateScope[] dateScopes)
    throws BusinessException
  {
    if (ArrayUtils.isEmpty(dateScopes)) {
      return;
    }
    for (IDateScope dateScope : dateScopes) {
      checkTBMPsndocDate(pk_hrorg, pk_psndoc, dateScope);
    }
  }
  


  public void checkTBMPsndocDate(String pk_hrorg, String[] pk_psndocs, IDateScope[] dateScopes)
    throws BusinessException
  {
    checkTBMPsndocDate(pk_hrorg, pk_psndocs, dateScopes, false);
  }
  






  public void checkTBMPsndocDate(String pk_hrorg, String[] pk_psndocs, IDateScope[] dateScopes, boolean extendDate)
    throws BusinessException
  {
    if (!ArrayUtils.isSameLength(pk_psndocs, dateScopes)) {
      throw new IllegalArgumentException("the lenth of pk_psndocs must equal to the length of datescopes!");
    }
    int length = ArrayUtils.getLength(pk_psndocs);
    if (length == 0) {
      return;
    }
    
    if ((dateScopes[0] instanceof ITimeScopeWithBillInfo)) {
      BillMethods.processBeginEndDateExistsTimeZone((ITimeScopeBillBodyVO[])dateScopes);
    }
    IDateScope maxScope = DateScopeUtils.getMaxRangeDateScope(DateScopeUtils.extendScopes(dateScopes, extendDate ? 1 : 0));
    
    Map<String, List<TBMPsndocVO>> psndocMap = queryTBMPsndocMapByPsndocs(pk_hrorg, pk_psndocs, maxScope.getBegindate(), maxScope.getEnddate(), false);
    for (int i = 0; i < length; i++) {
      List<TBMPsndocVO> psndocList = psndocMap == null ? null : (List)psndocMap.get(pk_psndocs[i]);
      if (org.apache.commons.collections.CollectionUtils.isEmpty(psndocList)) {
        throw new BusinessException(ResHelper.getString("6017psndoc", "06017psndoc0131", new String[] { getPsnName(pk_psndocs[i]) }));
      }
      
      IDateScope[] mergeScopes = DateScopeUtils.mergeAndExtendScopes((IDateScope[])psndocList.toArray(new TBMPsndocVO[0]), extendDate ? 1 : 0);
      IDateScope scope = new DefaultDateScope();
      scope.setBegindate(dateScopes[i].getBegindate());
      scope.setEnddate(dateScopes[i].getEnddate());
      if ((dateScopes[i] instanceof ITimeScopeWithBillInfo)) {
        ITimeScopeWithBillInfo billInfo = (ITimeScopeWithBillInfo)dateScopes[i];
        String endTime = billInfo.getScope_end_datetime().toStdString(billInfo.getTimezone());
        if (endTime.endsWith("00:00:00"))
        {
          scope.setEnddate(dateScopes[i].getEnddate().getDateBefore(1));
        }
      }
      
      if (!DateScopeUtils.contains(mergeScopes, scope))
      {

        throw new BusinessException(ResHelper.getString("6017psndoc", "06017psndoc0131", new String[] { getPsnName(pk_psndocs[i]) }));
      }
    }
  }
  














































































































































  public boolean existsTBMPsndoc(String pk_hrorg, String pk_psnorg, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    String sql = "select top 1 1 from tbm_psndoc where pk_org=? and pk_psnorg=? and begindate<=? and enddate>=?";
    SQLParameter para = new SQLParameter();
    para.addParam(pk_hrorg);
    para.addParam(pk_psnorg);
    para.addParam(endDate.toString());
    para.addParam(beginDate.toString());
    Boolean hasDoc = (Boolean)new BaseDAO().executeQuery(sql, para, new BooleanProcessor());
    return hasDoc.booleanValue();
  }
  







  public String[] existsTBMPsndocs(String pk_hrorg, String[] pk_psnorgs, IDateScope[] scopes)
    throws BusinessException
  {
    if (!ArrayUtils.isSameLength(pk_psnorgs, scopes)) {
      throw new BusinessException("pk_psnorgs must have same length to scopes!");
    }
    int length = ArrayUtils.getLength(pk_psnorgs);
    if (length == 0) {
      return null;
    }
    InSQLCreator isc = new InSQLCreator();
    List<TBMPsndocVO> tmpList = new ArrayList();
    for (int i = 0; i < length; i++) {
      TBMPsndocVO tmpVO = new TBMPsndocVO();
      tmpVO.setPk_org(pk_hrorg);
      tmpVO.setPk_psnorg(pk_psnorgs[i]);
      tmpVO.setBegindate(scopes[i].getBegindate());
      tmpVO.setEnddate(scopes[i].getEnddate());
      tmpList.add(tmpVO);
    }
    String[] fields = { "pk_org", "pk_psnorg", "begindate", "enddate" };
    String tableName = isc.insertValues("tbm_psndoctmp", fields, fields, (SuperVO[])tmpList.toArray(new TBMPsndocVO[0]));
    
    String sql = "select tmptable.pk_psnorg from " + tableName + " tmptable where not exists (select 1 from tbm_psndoc where tbm_psndoc.pk_org=tmptable.pk_org" + " and tbm_psndoc.pk_psnorg=tmptable.pk_psnorg and tbm_psndoc.begindate<=tmptable.enddate and tbm_psndoc.enddate>=tmptable.begindate)";
    
    List notExistDoc = (List)new BaseDAO().executeQuery(sql, new ColumnListProcessor());
    return (String[])(org.apache.commons.collections.CollectionUtils.isEmpty(notExistDoc) ? null : notExistDoc.toArray(new String[0]));
  }
  

  public TBMPsndocVO queryLatestByPsnorgDate(String pk_hrorg, String pk_psnorg, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    return new TBMPsndocDAO().queryLatestByPsnorgDate(pk_hrorg, pk_psnorg, beginDate, endDate);
  }
  

  public TBMPsndocVO queryLatestByPsndocDate(String pk_hrorg, String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    return new TBMPsndocDAO().queryLatestByPsndocDate(pk_hrorg, pk_psndoc, beginDate, endDate);
  }
  
  public TBMPsndocVO[] queryPsnorgLatestByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate begindate, UFLiteralDate enddate)
    throws BusinessException
  {
    fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, enddate.toStdString());
    SQLParamWrapper w = TBMPsndocSqlPiecer.getLatestPsnOrgPsndocWhereCond(begindate.toString(), enddate.toString());
    return new TBMPsndocDAO().queryByCondition(pk_hrorg, fromWhereSQL, w.getSql(), w.getParam());
  }
  

  public TBMPsndocVO[] queryLatestByConditionAndDept(FromWhereSQL fromWhereSQL, UFLiteralDate begindate, UFLiteralDate enddate, String pk_dept, boolean containsSubDepts)
    throws BusinessException
  {
    return queryLatestByCondition(null, TBMPsndocSqlPiecer.addDeptPk2QuerySQL(pk_dept, containsSubDepts, fromWhereSQL), begindate, enddate);
  }
  

  public TBMPsndocVO[] queryByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL)
    throws BusinessException
  {
    return new TBMPsndocDAO().queryByCondition(pk_hrorg, fromWhereSQL);
  }
  

  public Map<String, List<TBMPsndocVO>> queryTBMPsndocMapByPsndocsBU(String pk_org, String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate, boolean processIntersection)
    throws BusinessException
  {
    Map<String, List<TBMPsndocVO>> retMap = queryTBMPsndocMapByPsndocs(null, pk_psndocs, beginDate, endDate, processIntersection, true, null);
    
    return filterByOrg(pk_org, retMap);
  }
  





  private Map<String, List<TBMPsndocVO>> filterByOrg(String pk_org, Map<String, List<TBMPsndocVO>> map)
  {
    if (MapUtils.isEmpty(map)) {
      return map;
    }
    for (String pk_psndoc : map.keySet()) {
      List<TBMPsndocVO> psndocVOList = (List)map.get(pk_psndoc);
      for (int i = psndocVOList.size() - 1; i >= 0; i--) {
        if (!pk_org.equals(((TBMPsndocVO)psndocVOList.get(i)).getPk_joborg())) {
          psndocVOList.remove(i);
        }
      }
    }
    return map;
  }
  

  public Map<UFLiteralDate, String> queryDateJobOrgMap(String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    Map<String, Map<UFLiteralDate, String>> map = queryDateJobOrgMapByPsndocInSQL("'" + pk_psndoc + "'", beginDate, endDate);
    if (MapUtils.isEmpty(map)) {
      return null;
    }
    return (Map)map.get(pk_psndoc);
  }
  

  public Map<UFLiteralDate, String> queryDateJobOrgMap(String pk_psndoc, IDateScope[] dateScopes)
    throws BusinessException
  {
    IDateScope[] mergedScopes = DateScopeUtils.mergeDateScopes(dateScopes);
    if (mergedScopes.length == 1) {
      return queryDateJobOrgMap(pk_psndoc, mergedScopes[0].getBegindate(), mergedScopes[0].getEnddate());
    }
    Map<UFLiteralDate, String> map = new HashMap();
    for (IDateScope dateScope : mergedScopes) {
      Map<UFLiteralDate, String> tempMap = queryDateJobOrgMap(pk_psndoc, dateScope.getBegindate(), dateScope.getEnddate());
      if (!MapUtils.isEmpty(tempMap))
      {

        map.putAll(tempMap); }
    }
    return map;
  }
  

  public Map<String, Map<UFLiteralDate, String>> queryDateJobOrgMapByPsndocInSQL(String psndocInSQL, IDateScope[] dateScopes)
    throws BusinessException
  {
    if (ArrayUtils.isEmpty(dateScopes)) {
      return null;
    }
    IDateScope[] mergedScopes = DateScopeUtils.mergeDateScopes(dateScopes);
    Map<String, Map<UFLiteralDate, String>> map = null;
    for (IDateScope mergedScope : mergedScopes) {
      Map<String, Map<UFLiteralDate, String>> tempMap = queryDateJobOrgMapByPsndocInSQL(psndocInSQL, mergedScope.getBegindate(), mergedScope.getEnddate());
      map = CommonUtils.putAll(map, tempMap);
    }
    return map;
  }
  

  public Map<String, Map<UFLiteralDate, String>> queryDateJobOrgMapByPsndocInSQL(String psndocInSQL, IDateScope[] dateScopes, int extendDates)
    throws BusinessException
  {
    return queryDateJobOrgMapByPsndocInSQL(psndocInSQL, DateScopeUtils.extendScopes(dateScopes, extendDates));
  }
  



  public Map<String, Map<UFLiteralDate, String>> queryDateJobOrgMapByPsndocInSQL(String psndocInSQL, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    String cond = "pk_psndoc in(" + psndocInSQL + ") and " + "begindate" + "<=? " + "and " + "enddate" + ">=?";
    SQLParameter para = new SQLParameter();
    para.addParam(endDate.toString());
    para.addParam(beginDate.toString());
    BaseDAO dao = new BaseDAO();
    TBMPsndocVO[] psndocVOs = (TBMPsndocVO[])CommonUtils.toArray(TBMPsndocVO.class, dao.retrieveByClause(TBMPsndocVO.class, cond, para));
    if (ArrayUtils.isEmpty(psndocVOs)) {
      return null;
    }
    

    Map<String, String> jobOrgMap = new HashMap();
    InSQLCreator isc = new InSQLCreator();
    String psnjobInSQL = isc.getInSQL(psndocVOs, "pk_psnjob");
    String psnjobCond = "pk_psnjob in(" + psnjobInSQL + ")";
    PsnJobVO[] psnjobVOs = (PsnJobVO[])CommonUtils.toArray(PsnJobVO.class, dao.retrieveByClause(PsnJobVO.class, psnjobCond));
    for (PsnJobVO psnjobVO : psnjobVOs) {
      jobOrgMap.put(psnjobVO.getPk_psnjob(), psnjobVO.getPk_org());
    }
    
    UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
    Map<String, TBMPsndocVO[]> psndocMap = CommonUtils.group2ArrayByField("pk_psndoc", psndocVOs);
    Map<String, Map<UFLiteralDate, String>> retMap = new HashMap();
    for (String pk_psndoc : psndocMap.keySet()) {
      Map<UFLiteralDate, String> dateOrgMap = new HashMap();
      retMap.put(pk_psndoc, dateOrgMap);
      TBMPsndocVO[] tempPsndocVOs = (TBMPsndocVO[])psndocMap.get(pk_psndoc);
      for (UFLiteralDate date : allDates) {
        TBMPsndocVO vo = TBMPsndocVO.findIntersectionVO(tempPsndocVOs, date.toString());
        if (vo != null)
        {

          dateOrgMap.put(date, jobOrgMap.get(vo.getPk_psnjob())); }
      }
    }
    return retMap;
  }
  
  public String[] queryAllBUPksByHROrg(String pk_hrorg, UFLiteralDate beginDate, UFLiteralDate endDate, boolean mergeAosOrg)
    throws BusinessException
  {
    return new TBMPsndocDAO().queryAllBUPksByHROrg(pk_hrorg, beginDate, endDate, mergeAosOrg);
  }
  
  public OrgVO[] queryAllBUsByHROrg(String pk_hrorg, UFLiteralDate beginDate, UFLiteralDate endDate, boolean mergeAosOrg) throws BusinessException
  {
    return new TBMPsndocDAO().queryAllBUsByHROrg(pk_hrorg, beginDate, endDate, mergeAosOrg);
  }
  
  public TBMPsndocVO[] queryTBMPsndocVOsByPsndoc(String pk_psndoc) throws BusinessException {
    FromWhereSQL fws = TBMPsndocSqlPiecer.createPsndocQuerySQL(pk_psndoc);
    TBMPsndocVO[] vos = new TBMPsndocDAO().queryByCondition(null, fws, null, null, new String[] { "{0}.begindate" }, true, false, false);
    return vos;
  }
  
  public TBMPsndocVO[] queryTBMPsndocVOsByPsndocDate(String pk_psndoc, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    String extraCond = "{0}.begindate<=? and {0}.enddate>=?";
    SQLParameter para = new SQLParameter();
    para.addParam(endDate);
    para.addParam(beginDate);
    FromWhereSQL fws = TBMPsndocSqlPiecer.createPsndocQuerySQL(pk_psndoc);
    fws = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fws, endDate.toStdString());
    return new TBMPsndocDAO().queryByCondition(null, fws, extraCond, para, new String[] { "{0}.begindate" }, true, false, false);
  }
  
  public void checkTBMPsndocEndDateSealed4HROrg(String pk_hrorg) throws BusinessException
  {
    new TBMPsndocDAO().checkTBMPsndocEndDateSealed4HROrg(pk_hrorg);
  }
  
  public void checkTBMPsndocEndDateSealed4BU(String pk_org)
    throws BusinessException
  {
    new TBMPsndocDAO().checkTBMPsndocEndDateSealed4BU(pk_org);
  }
  
  public String[] queryAllBUPksByPsndocs(String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    return StringPiecer.getStrArray(queryAllBUsByPsndocs(pk_psndocs, beginDate, endDate), "pk_org");
  }
  
  public OrgVO[] queryAllBUsByPsndocs(String[] pk_psndocs, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    return new TBMPsndocDAO().queryAllBUsByPsndocs(pk_psndocs, beginDate, endDate);
  }
  
  public TBMPsndocVO[] queryLatestByCondition2(String pk_hradminorg, FromWhereSQL fromWhereSQL, UFLiteralDate begindate, UFLiteralDate enddate)
    throws BusinessException
  {
    return queryLatestByCondition(pk_hradminorg, fromWhereSQL, begindate, enddate, true);
  }
  

  public TBMPsndocVO[] queryByCondition2(String pk_hradminorg, FromWhereSQL fromWhereSQL, UFLiteralDate date, boolean queryPkJobOrg, boolean queryPkDept)
    throws BusinessException
  {
    return queryByCondition(pk_hradminorg, fromWhereSQL, date, queryPkJobOrg, queryPkDept, true);
  }
  
  public Map<String, Map<UFLiteralDate, String>> queryDateJobOrgMapByPsndocInSQL(String[] pkPsndocs, IDateScope[] dateScopes)
    throws BusinessException
  {
    if ((ArrayUtils.isEmpty(pkPsndocs)) || (ArrayUtils.isEmpty(dateScopes))) {
      return null;
    }
    InSQLCreator isc = new InSQLCreator();
    String psnInSql = isc.getInSQL(pkPsndocs);
    return queryDateJobOrgMapByPsndocInSQL(psnInSql, dateScopes);
  }
  

  public TBMPsndocVO[] queryTBMPsndocByPsnorgs(String pk_hrorg, String[] pk_psnorgs, UFLiteralDate beginDate, UFLiteralDate endDate)
    throws BusinessException
  {
    if ((StringUtils.isEmpty(pk_hrorg)) || (ArrayUtils.isEmpty(pk_psnorgs))) {
      return null;
    }
    InSQLCreator isc = new InSQLCreator();
    String condition = " pk_org='" + pk_hrorg + "' and pk_psnorg in (" + isc.getInSQL(pk_psnorgs) + ") and begindate<='" + endDate + "' and enddate>= '" + beginDate + "' ";
    
    return (TBMPsndocVO[])CommonUtils.retrieveByClause(TBMPsndocVO.class, condition);
  }
  
  public TBMPsndocVO[] queryLatestByConditionForTeam(String pk_hrorg, String pk_team, UFLiteralDate begindate, UFLiteralDate enddate)
    throws BusinessException
  {
    String condition = " pk_org = '" + pk_hrorg + "' and pk_team = '" + pk_team + "' and begindate<='" + enddate + "' and enddate>= '" + begindate + "' ";
    return (TBMPsndocVO[])CommonUtils.retrieveByClause(TBMPsndocVO.class, condition);
  }
  
  public String[] queryPsndocsByCondition(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate begindate, UFLiteralDate enddate)
    throws BusinessException
  {
    String tbmpsndocCondSQL = " and tbm_psndoc.pk_org = '" + pk_hrorg + "' and tbm_psndoc.begindate<='" + enddate + "' and tbm_psndoc.enddate>= '" + begindate + "' ";
    
    String QrySql = " select distinct(pk_psndoc) " + fromWhereSQL.getFrom() + fromWhereSQL.getWhere() + tbmpsndocCondSQL;
    
    Object c = new BaseDAO().executeQuery(QrySql, new ArrayListProcessor());
    
    return null;
  }
  

  public TBMPsndocVO[] queryLatestByConditionWithUnit(String pk_hrorg, FromWhereSQL fromWhereSQL, UFLiteralDate begindate, UFLiteralDate enddate, boolean businessUnitFlag)
    throws BusinessException
  {
    fromWhereSQL = TBMPsndocSqlPiecer.ensureJoinOrgDeptVersionTable(fromWhereSQL, enddate.toStdString());
    SQLParamWrapper w = TBMPsndocSqlPiecer.getLatestPsndocWhereCondWithUnitFlag("{0}", begindate.toString(), enddate.toString(), null, StringUtils.isNotEmpty(pk_hrorg), businessUnitFlag);
    
    return new TBMPsndocDAO().queryByCondition(pk_hrorg, fromWhereSQL, w.getSql(), w.getParam(), null, true, true, false);
  }
}

/* Location:           C:\ncProjects\6.5\COCO-PRO-20180122\nchome\home\modules\hrta\META-INF\lib\hrta_hrtapsndoc.jar
 * Qualified Name:     nc.impl.ta.psndoc.TBMPsndocServiceImpl
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */