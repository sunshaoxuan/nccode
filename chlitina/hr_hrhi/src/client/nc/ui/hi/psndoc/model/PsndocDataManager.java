package nc.ui.hi.psndoc.model;

import java.util.HashMap;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hi.IPsndocService;
import nc.itf.hr.managescope.ManagescopeFacade;
import nc.md.MDBaseQueryFacade;
import nc.md.data.access.NCObject;
import nc.md.model.IAttribute;
import nc.md.model.IBean;
import nc.pub.tools.HiSQLHelper;
import nc.pub.tools.KeyPsnGroupSqlUtils;
import nc.ui.hr.comp.combinesort.Attribute;
import nc.ui.hr.uif2.model.IQueryInfo;
import nc.ui.hr.uif2.model.ITypeSupportModelDataManager;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.pagination.BillManagePaginationDelegator;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.components.pagination.PaginationBar;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.DefaultAppModelDataManager;
import nc.ui.uif2.model.IAppModelDataManagerEx;
import nc.ui.uif2.model.RowOperationInfo;
import nc.ui.uif2.model.RowSelectionOperationInfo;
import nc.vo.bd.psn.PsnClVO;
import nc.vo.hi.psndoc.CapaVO;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.KeyPsnGrpVO;
import nc.vo.hi.psndoc.KeyPsnVO;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.QulifyVO;
import nc.vo.hi.psndoc.TrainVO;
import nc.vo.hi.psndoc.WainfoVO;
import nc.vo.hr.managescope.ManagescopeBusiregionEnum;
import nc.vo.om.aos.AOSSQLHelper;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class PsndocDataManager extends DefaultAppModelDataManager implements AppEventListener, IAppModelDataManagerEx,
	ITypeSupportModelDataManager, IPaginationModelListener, IQueryInfo {
    private boolean blExecuteQuery = false;
    private BillManagePaginationDelegator paginationDelegator;
    private PaginationModel paginationModel;
    private PaginationBar paginationBar = null;

    private IPsndocQryService queryService;

    public PsndocModel getModel() {
	return (PsndocModel) super.getModel();
    }

    protected String getOrderby(Vector<Attribute> vectSortField) {
	if ((vectSortField == null) || (vectSortField.size() == 0)) {
	    return "";
	}
	String strOrderBy = "";
	for (Attribute attr : vectSortField) {
	    String strFullCode = (String) attr.getAttribute().getValue();
	    String strTableName = "";
	    String strCode = strFullCode;
	    int iDotIndex = strFullCode.indexOf(".");
	    if (iDotIndex > 0) {
		strTableName = strFullCode.substring(0, iDotIndex);
		strCode = strFullCode.substring(iDotIndex);
	    }
	    strFullCode = getTableAlias(strTableName) + strCode;
	    strOrderBy = strOrderBy + "," + strFullCode + (attr.isAscend() ? "" : " desc");
	}
	return strOrderBy.length() > 0 ? strOrderBy.substring(1) : "";
    }

    public BillManagePaginationDelegator getPaginationDelegator() {
	if (paginationDelegator == null) {
	    paginationDelegator = new BillManagePaginationDelegator(getModel(), getPaginationModel());
	}
	return paginationDelegator;
    }

    public PaginationModel getPaginationModel() {
	return paginationModel;
    }

    public int getQueryDataCount() {
	return getPaginationModel().getObjectPks().size();
    }

    public IPsndocQryService getQueryService() {
	if (queryService == null) {
	    queryService = ((IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class));
	}
	return queryService;
    }

    protected String getSQL4IncludeChildren() {
	SuperVO superVO = getModel().getCurrTypeOrgVO();
	if ((superVO == null) || (((superVO instanceof OrgVO)) && ("other_node".equals(superVO.getPrimaryKey())))) {

	    return "";
	}
	String strSQL = "";
	String strPsnJobTableAlias = getTableAlias(PsnJobVO.getDefaultTableName());
	if (getModel().isIncludeChildOrgs()) {
	    strSQL = strSQL + strPsnJobTableAlias + "." + "pk_org"
		    + " in (select pk_org from org_orgs where pk_org in(select pk_org ))";
	}
	if (getModel().isIncludeChildDepts()) {
	    strSQL = strSQL + "";
	}
	return "";
    }

    protected String getSQL4ManageScope() throws Exception {
	return HiSQLHelper.getManagescopeSQL(getContext().getPk_org());
    }

    protected String getSQL4Tree() throws Exception {
	String strSQL = "1=1";
	SuperVO superVO = getModel().getCurrTypeOrgVO();
	String tableAlias = getTableAlias(PsnJobVO.getDefaultTableName());
	if ((superVO instanceof OrgVO)) {
	    OrgVO orgVO = (OrgVO) superVO;
	    if ("other_node_msaos".equals(orgVO.getPrimaryKey())) {
		String strOtherPsnjobByHrorg = ManagescopeFacade.queryOtherPsnjobByHrorg4MsAOS(
			getContext().getPk_org(), ManagescopeBusiregionEnum.psndoc);

		strSQL = " " + tableAlias + ".pk_psnjob in" + strOtherPsnjobByHrorg;

	    } else if (getModel().isIncludeChildOrgs()) {

		UFBoolean isIncludeChildHR = UFBoolean.TRUE;

		String sql = "";
		if (("60070psninfo".equals(getModel().getContext().getNodeCode())) && (isIncludeChildHR != null)
			&& (isIncludeChildHR.booleanValue())) {

		    String gkSql = ((IPsndocService) NCLocator.getInstance().lookup(IPsndocService.class))
			    .queryControlSql("@@@@Z710000000006M2K", getModel().getContext().getPk_org(), true);

		    sql = AOSSQLHelper.getAllBUInSQLByHROrgPK(orgVO.getPrimaryKey());
		    if (!StringUtils.isEmpty(gkSql)) {
			sql = sql + " and pk_adminorg in (" + gkSql + ")";
		    } else {
			sql = AOSSQLHelper.getChildrenBUInSQLByHROrgPK(orgVO.getPrimaryKey());
		    }
		} else {
		    sql = AOSSQLHelper.getChildrenBUInSQLByHROrgPK(orgVO.getPrimaryKey());
		}
		strSQL = " " + tableAlias + ".pk_org in(" + sql + ")";

	    } else {

		strSQL = " " + tableAlias + ".pk_org='" + orgVO.getPrimaryKey() + "'";
	    }

	} else if ((superVO instanceof DeptVO)) {
	    DeptVO deptVO = (DeptVO) superVO;
	    if (getModel().isIncludeChildDepts()) {

		strSQL = " " + tableAlias + ".pk_dept in("
			+ AOSSQLHelper.getAllDeptInSQLByHROrgPK(deptVO.getPrimaryKey(), true) + ")";
	    } else {
		strSQL = " " + tableAlias + ".pk_dept='" + deptVO.getPrimaryKey() + "'";
	    }
	} else if ((superVO instanceof PsnClVO)) {
	    PsnClVO psnClVO = (PsnClVO) superVO;

	    if (getModel().isBlIncludeChildPsncl()) {

		String innercode = psnClVO.getInnercode();
		StringBuilder inSQL = new StringBuilder();

		inSQL.append("select pk_psncl from bd_psncl where ");
		inSQL.append("innercode like '" + innercode + "'||'%' ");
		inSQL.append("and enablestate=2 ");
		strSQL = " " + tableAlias + ".pk_psncl in(" + inSQL + ")";

	    } else {
		strSQL = " " + tableAlias + ".pk_psncl='" + psnClVO.getPrimaryKey() + "'";
	    }
	    if ("60070psninfo".equals(getModel().getContext().getNodeCode())) {

		String orgPart = " " + tableAlias + ".pk_org in ("
			+ AOSSQLHelper.getAllBUInSQLByHROrgPK(getContext().getPk_org()) + ") ";
		strSQL = strSQL + " and " + orgPart + " ";
	    } else if ("60070poi".equals(getModel().getContext().getNodeCode())) {

		strSQL = strSQL + " and " + tableAlias + ".pk_hrorg = '" + getContext().getPk_org() + "' ";
	    }
	} else if ((superVO instanceof KeyPsnGrpVO)) {

	    KeyPsnGrpVO grp = (KeyPsnGrpVO) superVO;
	    String inSql = " select pk_psnorg from hi_psndoc_keypsn where pk_keypsn_grp = '" + grp.getPk_keypsn_group()
		    + "' ";
	    if (!getModel().isShowHisKeyPsn()) {

		inSql = inSql + " and (endflag ='N' or isnull(endflag,'~') = '~') ";
	    }

	    String orgAlias = getTableAlias(PsnOrgVO.getDefaultTableName());
	    strSQL = strSQL + " and " + orgAlias + ".pk_psnorg in ( " + inSql + " ) ";

	} else if ("60070psninfo".equals(getModel().getContext().getNodeCode())) {

	    UFBoolean isIncludeChildHR = UFBoolean.TRUE;
	    String sql = "";
	    if ((isIncludeChildHR != null) && (isIncludeChildHR.booleanValue())) {

		sql = AOSSQLHelper.getChildrenBUInSQLByHROrgPK(getContext().getPk_org());

	    } else {
		sql = AOSSQLHelper.getAllBUInSQLByHROrgPK(getContext().getPk_org());
	    }
	    strSQL = strSQL + " and " + tableAlias + ".pk_org in (" + sql + ") ";
	} else if ("60070poi".equals(getModel().getContext().getNodeCode())) {
	    strSQL = strSQL + " and " + tableAlias + ".pk_hrorg = '" + getContext().getPk_org() + "' ";
	} else if ("60070keypsn".equals(getModel().getContext().getNodeCode())) {

	    String grpInSql = " select pk_keypsn_group from hi_keypsn_group where pk_org = '"
		    + getContext().getPk_org() + "' ";
	    if (!getModel().isShowHisGroup()) {

		grpInSql = grpInSql + " and enablestate = " + 2;
	    }

	    grpInSql = grpInSql + " and "
		    + KeyPsnGroupSqlUtils.getKeyPsnGroupPowerSql(KeyPsnGrpVO.getDefaultTableName());

	    String inSql = " select pk_psnorg from hi_psndoc_keypsn where pk_keypsn_grp in ( " + grpInSql + " ) ";
	    if (!getModel().isShowHisKeyPsn()) {

		inSql = inSql + " and (endflag ='N' or isnull(endflag,'~') = '~') ";
	    }

	    String orgAlias = getTableAlias(PsnOrgVO.getDefaultTableName());
	    strSQL = strSQL + " and " + orgAlias + ".pk_psnorg in ( " + inSql + " ) ";
	}

	return strSQL;
    }

    public String getSQLAll() throws Exception {
	String strSQL = "(" + getSQLExtWhere() + ")";

	String strLastWhereSqlQueryDialog = getModel().getLastWhereSqlQueryDialog();
	if (StringUtils.isNotBlank(strLastWhereSqlQueryDialog)) {
	    strSQL = strSQL + " and (" + strLastWhereSqlQueryDialog + ")";
	}

	String strCurrTreeSQL = getSQL4Tree();
	if (StringUtils.isNotBlank(strCurrTreeSQL)) {
	    strSQL = strSQL + " and (" + strCurrTreeSQL + ")";
	}

	if ((!"60070poi".equals(getModel().getContext().getNodeCode()))
		&& (!"60070psninfo".equals(getModel().getContext().getNodeCode()))) {

	    String strPsnJobTableName = getTableAlias(PsnJobVO.getDefaultTableName());

	    strSQL = strSQL + " and ( " + strPsnJobTableName + "." + "pk_hrorg" + " = '" + getContext().getPk_org()
		    + "' and " + strPsnJobTableName + "." + "lastflag" + " = 'Y' and " + strPsnJobTableName + "."
		    + "ismainjob" + " = 'Y' ) ";

	    if (getModel().isBlShowOnJobPsn()) {
		strSQL = strSQL + " and ( " + strPsnJobTableName + "." + "endflag" + " = 'N' ) ";
	    } else {
		strSQL = strSQL + " and ( " + strPsnJobTableName + "." + "endflag" + " = 'Y' ) ";
	    }
	}

	if ("60070psninfo".equals(getModel().getContext().getNodeCode())) {
	    String strPsnJobTableName = getTableAlias(PsnJobVO.getDefaultTableName());
	    if (getModel().isBlShowAllInfo()) {

		String inSql = " select pk_psnjob from hi_psnjob inner join ( select min(recordnum) over(partition by pk_psnorg, pk_org) recordmin, recordnum , pk_psnorg , pk_org  from hi_psnjob where ismainjob='Y' ) temp on hi_psnjob.recordnum = temp.recordnum  and hi_psnjob.pk_psnorg = temp.pk_psnorg and temp.recordmin = temp.recordnum and hi_psnjob.ismainjob = 'Y' ";

		strSQL = strSQL + " and (  " + strPsnJobTableName + ".pk_psnjob in ( " + inSql
			+ "  union select pk_psnjob from hi_psnjob where ismainjob ='N' and endflag ='N' ) ) ";

	    } else {

		strSQL = strSQL
			+ " and (  "
			+ strPsnJobTableName
			+ ".pk_psnjob in ( select pk_psnjob from hi_psnjob where lastflag = 'Y' and (ismainjob = 'Y' or (hi_psnjob.ismainjob = 'N' and hi_psnjob.endflag ='N')))) ";
	    }

	    if (getModel().isBlShowOnJobPsn()) {
		strSQL = strSQL + " and ( " + strPsnJobTableName + "." + "endflag" + " = 'N' ) ";
	    } else {
		strSQL = strSQL + " and ( " + strPsnJobTableName + "." + "endflag" + " = 'Y' ) ";
	    }
	}

	if ("60070employee".equals(getModel().getContext().getNodeCode())) {

	    String strPsnJobTableName = getTableAlias(PsnJobVO.getDefaultTableName());
	    strSQL = strSQL + " and ( " + strPsnJobTableName + ".pk_psnorg  not in ( "
		    + KeyPsnGroupSqlUtils.getKeyPsnSql(getModel().getContext().getPk_org()) + " ) ) ";
	}

	String strPsnJobTableName = getTableAlias(PsnJobVO.getDefaultTableName());
	strSQL = strSQL + " and " + strPsnJobTableName + ".pk_org in (select pk_adminorg from org_admin_enable)";

	return strSQL;
    }

    protected String getSQLExtWhere() {
	String psnorg = getTableAlias(PsnOrgVO.getDefaultTableName());
	String psnjob = getTableAlias(PsnJobVO.getDefaultTableName());
	String strSQL = psnorg + "." + "indocflag" + "='" + getModel().getIndocFlag() + "' and " + psnorg + "."
		+ "psntype" + "=" + getModel().getPsnType();

	if (!"60070psninfo".equals(getModel().getContext().getNodeCode())) {

	    strSQL = strSQL + "  and " + psnjob + "." + "lastflag" + "='" + UFBoolean.TRUE + "' ";
	}

	if (ArrayUtils.contains(new String[] { "60070register" }, getModel().getContext().getNodeCode())) {
	    strSQL = strSQL + " and " + psnjob + "." + "endflag" + "='" + UFBoolean.FALSE + "'";
	}
	return strSQL;
    }

    public String getTableAlias(String strTableName) {
	return getModel().getUsedTablesOfQuery().containsKey(strTableName) ? (String) getModel().getUsedTablesOfQuery()
		.get(strTableName) : strTableName;
    }

    public void handleEvent(AppEvent event) {
	if ("orgchanged".equals(event.getType())) {

	    setExecuteQuery(false);
	    try {
		getPaginationModel().setObjectPks(null);
	    } catch (BusinessException e) {
		Logger.error(e.getMessage(), e);
	    }
	}

	if ("event_adjustsort".equals(event.getType())) {

	    Boolean blShow = (Boolean) event.getContextObject();
	    getPaginationBar().setVisible(!blShow.booleanValue());
	}

	if (ArrayUtils.contains(new String[] { "Data_Deleted", "Data_Inserted", "Selected_Data_Changed",
		"Multi_Selection_Changed" }, event.getType())) {

	    RowOperationInfo info = (RowOperationInfo) event.getContextObject();
	    Object[] objs = info.getRowDatas();
	    Object[] newObjs = new Object[objs.length];
	    for (int i = 0; i < objs.length; i++) {
		newObjs[i] = ((PsndocAggVO) objs[i]).getParentVO().getPsnJobVO();
	    }

	    RowOperationInfo newInfo = new RowOperationInfo(info.getRowIndexes(), newObjs);
	    if ((info instanceof RowSelectionOperationInfo)) {
		newInfo = new RowSelectionOperationInfo();
		newInfo.setRowIndexes(info.getRowIndexes());
		newInfo.setRowDatas(newObjs);
		((RowSelectionOperationInfo) newInfo).setSelectionState(((RowSelectionOperationInfo) info)
			.getSelectionState());
	    }

	    AppEvent newEvent = new AppEvent(event.getType(), event.getSource(), newInfo);
	    getPaginationDelegator().handleEvent(newEvent);

	    if (ArrayUtils.contains(new String[] { "Data_Inserted", "Selected_Data_Changed" }, event.getType())) {
		for (int i = 0; i < objs.length; i++) {
		    PsndocAggVO agg = (PsndocAggVO) objs[i];
		    getPaginationModel().update(agg.getParentVO().getPsnJobVO().getPk_psnjob(), agg);
		}

	    }
	} else {
	    getPaginationDelegator().handleEvent(event);
	}
    }

    public void initModel() {
	initModelBySqlWhere(null);
    }

    public void initModelBySqlWhere(String strSqlWhere) {
	refresh();
    }

    public void initModelByType(NCObject typeData) {
	if (typeData == null) {
	    getModel().setCurrTypeOrgVO(null);
	    initModelBySqlWhere(null);
	    return;
	}

	Object obj = typeData.getContainmentObject();
	SuperVO superVO = null;
	if ((obj instanceof AggregatedValueObject)) {
	    superVO = (SuperVO) ((AggregatedValueObject) obj).getParentVO();
	} else if ((obj instanceof SuperVO)) {
	    superVO = (SuperVO) obj;
	}
	getModel().setCurrTypeOrgVO(superVO);
	if (superVO == null) {
	    return;
	}
	initModelBySqlWhere(null);
    }

    public boolean isExecuteQuery() {
	return blExecuteQuery;
    }

    public void onDataReady() {
	getPaginationDelegator().onDataReady();
    }

    public void onStructChanged() {
    }

    public void queryData() throws Exception {
	if (!isExecuteQuery()) {

	    getPaginationModel().setObjectPks(null);
	    return;
	}
	String strWhere = getSQLAll();

	String strFrom = "bd_psndoc";

	String strPsnOrgTableName = getTableAlias(PsnOrgVO.getDefaultTableName());
	strFrom = strFrom + " inner join hi_psnorg " + strPsnOrgTableName + " on bd_psndoc.pk_psndoc = "
		+ strPsnOrgTableName + ".pk_psndoc ";

	String strPsnJobTableName = getTableAlias(PsnJobVO.getDefaultTableName());
	strFrom = strFrom + " inner join hi_psnjob " + strPsnJobTableName + " on " + strPsnOrgTableName
		+ ".pk_psnorg = " + strPsnJobTableName + ".pk_psnorg";

	HashMap<String, String> hashUsedTablesOfQuery = getModel().getUsedTablesOfQuery();
	for (String tabName : hashUsedTablesOfQuery.keySet()) {
	    if (!ArrayUtils.contains(new String[] { PsndocVO.getDefaultTableName(), PsnOrgVO.getDefaultTableName(),
		    PsnJobVO.getDefaultTableName() }, tabName)) {

		String alias = getTableAlias(tabName);
		if (PsndocAggVO.hashBusinessInfoSet.contains(tabName)) {

		    strFrom = strFrom + " left outer join " + tabName + " " + alias + " on " + strPsnOrgTableName
			    + ".pk_psnorg = " + alias + ".pk_psnorg ";

		} else {
		    strFrom = strFrom + " left outer join " + tabName + " " + alias + " on bd_psndoc.pk_psndoc = "
			    + alias + ".pk_psndoc ";
		}
	    }
	}

	strFrom = strFrom + " left outer join org_orgs org_orgs  on " + strPsnJobTableName
		+ ".pk_org = org_orgs.pk_org ";

	strFrom = strFrom + " left outer join org_dept org_dept  on " + strPsnJobTableName
		+ ".pk_dept = org_dept.pk_dept ";

	String[] strPk_psndocs = getQueryService()
		.queryPsndocPks(getContext(),
			new String[] { getTableAlias(PsnJobVO.getDefaultTableName()) + "." + "pk_psnjob" }, strFrom,
			strWhere, getOrderby(getModel().getSortFields()), getModel().getUsedTablesOfQuery(),
			getModel().getResourceCode());

	getPaginationModel().setObjectPks(strPk_psndocs);
    }

    public SuperVO[] querySubVO(String strTabCode, String strAddtionalWhere) throws BusinessException {
	if ((getModel().getCurrentPkPsndoc() == null) || (getModel().getCurrentPkPsndoc().trim().length() == 0)) {
	    return null;
	}
	if (strAddtionalWhere == null) {
	    strAddtionalWhere = "";
	}
	if (PsnJobVO.getDefaultTableName().equals(strTabCode)) {
	    strAddtionalWhere = strAddtionalWhere + " hi_psnjob.ismainjob='Y'";
	} else if (PartTimeVO.getDefaultTableName().equals(strTabCode)) {
	    strAddtionalWhere = strAddtionalWhere + " hi_psnjob.ismainjob='N'";
	} else if (CtrtVO.getDefaultTableName().equals(strTabCode)) {

	    strAddtionalWhere = strAddtionalWhere + " hi_psndoc_ctrt.isrefer = 'Y' ";
	} else if (KeyPsnVO.getDefaultTableName().equals(strTabCode)) {

	    if ((getModel().getCurrTypeOrgVO() != null) && ((getModel().getCurrTypeOrgVO() instanceof KeyPsnGrpVO))) {
		strAddtionalWhere = strAddtionalWhere + " hi_psndoc_keypsn.pk_keypsn_grp = '"
			+ ((KeyPsnGrpVO) getModel().getCurrTypeOrgVO()).getPk_keypsn_group() + "' ";

	    } else {
		strAddtionalWhere = strAddtionalWhere
			+ " hi_psndoc_keypsn.pk_keypsn_grp in ( select pk_keypsn_group from hi_keypsn_group where "
			+ KeyPsnGroupSqlUtils.getKeyPsnGroupPowerSql(KeyPsnGrpVO.getDefaultTableName()) + " ) ";
	    }

	} else if (CapaVO.getDefaultTableName().equals(strTabCode)) {
	    strAddtionalWhere = strAddtionalWhere + " hi_psndoc_capa.lastflag = 'Y' ";
	} else if (TrainVO.getDefaultTableName().equals(strTabCode)) {
	    PsndocAggVO psndocAggVO = (PsndocAggVO) getModel().getSelectedData();
	    if ((psndocAggVO != null) && (psndocAggVO.getParentVO().getPsnJobVO() != null)) {
		String pk_psnjob = psndocAggVO.getParentVO().getPsnJobVO().getPk_psnjob();
		strAddtionalWhere = strAddtionalWhere + " hi_psndoc_train.pk_psnjob = '" + pk_psnjob + "' ";
	    }
	}

	String strWhere = " pk_psndoc='" + getModel().getCurrentPkPsndoc() + "'";
	PsndocAggVO psndocAggVO = (PsndocAggVO) getModel().getSelectedData();

	if (((getModel().getBusinessInfoSet().contains(strTabCode)) || (WainfoVO.getDefaultTableName()
		.equals(strTabCode))) && (psndocAggVO != null)) {

	    if ("60070psninfo".equals(getContext().getNodeCode())) {

		String pk_org = psndocAggVO.getParentVO().getPsnJobVO().getPk_org();
		if (PsnJobVO.getDefaultTableName().equals(strTabCode)) {

		    strWhere = strWhere + " and pk_org = '" + pk_org + "' and pk_psnorg = '"
			    + psndocAggVO.getParentVO().getPsnOrgVO().getPrimaryKey() + "'";

		} else if (PsndocAggVO.hashPsnJobInfoSet.contains(strTabCode)) {

		    strWhere = strWhere + " and pk_psnjob in (select pk_psnjob from hi_psnjob where pk_org = '"
			    + pk_org + "' and pk_psnorg = '" + psndocAggVO.getParentVO().getPsnOrgVO().getPrimaryKey()
			    + "') ";

		} else if (!strTabCode.equals(PsndocDefTableUtil.getPsnNHIDetailTablename())
			&& !strTabCode.equals(PsndocDefTableUtil.getPsnNHISumTablename())
			&& !strTabCode.equals(PsndocDefTableUtil.getPsnHealthInsExTablename())
			&& !strTabCode.equals("hi_psndoc_groupinsrecord")
			&& !strTabCode.equals(PsndocDefTableUtil.getGroupInsuranceTablename())) {

		    // 只显示当前组织关系的数据
		    strWhere = strWhere + " and pk_psnorg = '"
			    + psndocAggVO.getParentVO().getPsnOrgVO().getPrimaryKey() + "'";
		}

		UFBoolean ismainjob = psndocAggVO.getParentVO().getPsnJobVO().getIsmainjob();
		if ((ismainjob == null) || (!ismainjob.booleanValue())) {

		    if (WainfoVO.getDefaultTableName().equals(strTabCode)) {
			strWhere = strWhere + " and pk_psnjob in (select pk_psnjob from hi_psnjob where pk_org = '"
				+ pk_org + "' and pk_psnorg = '"
				+ psndocAggVO.getParentVO().getPsnOrgVO().getPrimaryKey() + "' and ismainjob = 'N') ";

		    } else if (PartTimeVO.getDefaultTableName().equals(strTabCode)) {

			strWhere = strWhere + " and pk_org = '" + pk_org + "' ";
		    } else if (TrainVO.getDefaultTableName().equals(strTabCode)) {
			strWhere = strWhere + " and 1 = 1 ";
		    } else {
			strWhere = strWhere + " and 1 = 2 ";
		    }

		}

	    } else {
		// ssx added on 2017-05-16
		// upgrade to V65, from JD code
		if ((UIState.ADD == getModel().getUiState() || UIState.EDIT == getModel().getUiState())
			&& (!strTabCode.equals(PsndocDefTableUtil.getPsnNHIDetailTablename())
				&& !strTabCode.equals(PsndocDefTableUtil.getPsnNHISumTablename())
				&& !strTabCode.equals(PsndocDefTableUtil.getPsnHealthInsExTablename())
				&& !strTabCode.equals("hi_psndoc_groupinsrecord") && !strTabCode
				    .equals(PsndocDefTableUtil.getGroupInsuranceTablename()))) {
		    // 编辑态还是只显示当前组织关系的数据
		    strWhere += " and pk_psnorg = '" + psndocAggVO.getParentVO().getPsnOrgVO().getPrimaryKey() + "'";
		} else if (!strTabCode.equals(PsndocDefTableUtil.getPsnNHIDetailTablename())
			&& !strTabCode.equals(PsndocDefTableUtil.getPsnNHISumTablename())
			&& !strTabCode.equals(PsndocDefTableUtil.getPsnHealthInsExTablename())
			&& !strTabCode.equals("hi_psndoc_groupinsrecord")
			&& !strTabCode.equals(PsndocDefTableUtil.getGroupInsuranceTablename())) {
		    // 编辑态还是只显示当前组织关系的数据
		    strWhere += " and pk_psnorg = '" + psndocAggVO.getParentVO().getPsnOrgVO().getPrimaryKey() + "'";
		    // 显示所有信息时只显示转档的数据
		    // strWhere +=
		    // " and pk_psnorg in ( select pk_psnorg from hi_psnorg where pk_psndoc='"
		    // + getModel().getCurrentPkPsndoc()
		    // + "' and indocflag = 'Y' and orgrelaid <= " +
		    // psndocAggVO.getParentVO().getPsnOrgVO().getOrgrelaid() +
		    // " ) ";
		}
	    }
	}

	if (strAddtionalWhere.length() > 0) {
	    strWhere = strWhere + " and " + strAddtionalWhere;
	}

	IBean bean = MDBaseQueryFacade.getInstance().getBeanByFullName("hrhi." + strTabCode);
	IAttribute attribute = MDBaseQueryFacade.getInstance().getAttributeByFullName(
		"hrhi." + strTabCode + ".recordnum");
	String strOrder = attribute != null ? " recordnum desc" : null;

	if ((getModel().getBusinessInfoSet().contains(strTabCode))
		&& ("60070psninfo".equals(getContext().getNodeCode()))) {
	    if (QulifyVO.getDefaultTableName().equals(strTabCode)) {
		strOrder = " authendate ";
	    } else {
		strOrder = " begindate ";
	    }
	}

	Class clazzVO = null;
	try {
	    clazzVO = Class.forName(bean.getFullClassName());
	} catch (ClassNotFoundException ex) {
	    Logger.error(ex);
	}
	SuperVO[] subVOs = getQueryService().querySubVO(clazzVO, strWhere, strOrder);
	return subVOs;
    }

    public void refresh() {
	if (getModel().getContext().getPk_org() == null) {
	    return;
	}
	try {
	    queryData();
	} catch (Exception e) {
	    Logger.error(e.getMessage(), e);
	}
    }

    public void setExecuteQuery(boolean executeQuery) {
	blExecuteQuery = executeQuery;
    }

    public void setModel(PsndocModel model) {
	super.setModel(model);
	model.addAppEventListener(this);
    }

    public void setPaginationDelegator(BillManagePaginationDelegator paginationDelegator) {
	this.paginationDelegator = paginationDelegator;
    }

    public void setPaginationModel(PaginationModel paginationModel) {
	this.paginationModel = paginationModel;
	paginationModel.addPaginationModelListener(this);
	paginationModel.setPageSize(getModel().getPaginationSize());
	paginationModel.setMaxPageSize(15000);
	paginationModel.init();
    }

    public void setShowSealDataFlag(boolean showSealDataFlag) {
    }

    public void setPaginationBar(PaginationBar paginationBar) {
	this.paginationBar = paginationBar;
    }

    public PaginationBar getPaginationBar() {
	return paginationBar;
    }

    public boolean getValueOFParaMaintainCadre() throws Exception {
	return getQueryService().getValueOFParaMaintainCadre(getContext()).booleanValue();
    }
}
