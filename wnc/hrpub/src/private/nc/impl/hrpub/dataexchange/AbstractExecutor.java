package nc.impl.hrpub.dataexchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.itf.hrpub.IMDExchangeLogService;
import nc.itf.hrpub.util.JsonUtil;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.md.innerservice.MDQueryService;
import nc.md.model.IBusinessEntity;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.hrpub.mdmapping.AggMDClassVO;
import nc.vo.hrpub.mdmapping.MDClassVO;
import nc.vo.hrpub.mdmapping.MDPropertyVO;
import nc.vo.ml.LanguageVO;
import nc.vo.ml.MultiLangContext;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;

import org.apache.commons.lang.StringUtils;

import uap.distribution.util.StringUtil;

public abstract class AbstractExecutor {
	private List<Map<String, Object>> ncValueObjects;
	private List<Map<String, Object>> jsonValueObjects;
	private String ncEntityName;
	private String pk_org;
	private String pk_group;
	private String pk_ioschema;
	private String classid;
	private String cuserid;
	private String sessionid;
	private String language;
	private AggMDClassVO mdmappingVO;
	private IBusinessEntity businessEntity;
	private String operationType;
	private BaseDAO baseDAO;
	private IMDExchangeLogService logSrv;
	private Map<String, String> bizKeyMap;
	private Map<String, String> bizCodeMap;
	private Map<String, String> bizTableMap;
	private Map<String, String> bizOrgMap;
	private Map<String, String> bizAuditInfoMap;
	private Map<String, String> multiLangMap;
	private String entityPK;
	private String entityOrg;
	private String entityCode;
	private boolean isEntityIBDObjectImpl;
	private boolean holdReservedProperties;
	private boolean skipNotExistMapping;

	public final String MD_CREATOR = "CREATOR";
	public final String MD_CREATEDTIME = "CREATEDTIME";
	public final String MD_MODIFIER = "MODIFIER";
	public final String MD_MODIFIEDTIME = "MODIFIEDTIME";
	private Map<String, String> errorMessages;

	public final String RESVSTR = "$RESERVED_PROPERTY$";

	public AbstractExecutor() throws BusinessException {
		this.init();
	}

	public String getReservedPropertyName(String propName) {
		return this.RESVSTR + propName;
	}

	private void init() throws BusinessException {
		this.getLogService().createLogTable();
	}

	public void execute() throws BusinessException {
		initEntityBizMap();
		initEntityBizAuditInfoMap();
		initPropertyBizMap();
		initMultiLangMap();
		this.Logging("SESSION:INIT");
	}

	private void initEntityBizAuditInfoMap() throws BusinessException {
		String strSQL = "select name from md_property where classid='" + this.getClassid()
				+ "' and name like 'creat%' and datatype='f6f9a473-56c0-432f-8bc7-fbf8fde54fee'";
		String propName = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		if (!StringUtils.isEmpty(propName)) {
			this.getBizAuditInfoMap().put(MD_CREATOR, propName);
		}
		strSQL = "select name from md_property where classid='" + this.getClassid()
				+ "' and name like 'creat%' and datatype='BS000010000100001034'";
		propName = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		if (!StringUtils.isEmpty(propName)) {
			this.getBizAuditInfoMap().put(MD_CREATEDTIME, propName);
		}

		strSQL = "select name from md_property where classid='" + this.getClassid()
				+ "' and name like 'modif%' and datatype='f6f9a473-56c0-432f-8bc7-fbf8fde54fee'";
		propName = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		if (!StringUtils.isEmpty(propName)) {
			this.getBizAuditInfoMap().put(MD_MODIFIER, propName);
		}

		strSQL = "select name from md_property where classid='" + this.getClassid()
				+ "' and name like 'modif%' and datatype='BS000010000100001034'";
		propName = (String) this.getBaseDAO().executeQuery(strSQL, new ColumnProcessor());
		if (!StringUtils.isEmpty(propName)) {
			this.getBizAuditInfoMap().put(MD_MODIFIEDTIME, propName);
		}
	}

	private void initEntityBizMap() throws BusinessException {
		AggMDClassVO mapping = this.getMdmappingVO();
		MDClassVO entity = mapping.getParentVO();

		String sql = "";
		sql = "select bmap.intattrid, prop.name, cls.defaulttablename tablename from md_bizItfMap bmap "
				+ "inner join md_property prop on bmap.classattrid = prop.id "
				+ "inner join md_class cls on prop.classid = cls.id " + "where bmap.classid='" + entity.getPk_class()
				+ "' and bmap.bizinterfaceid='6c8722b9-911a-489b-8d0d-18bd3734fcf6'";
		List<Map<String, Object>> bizIfcList = (List<Map<String, Object>>) this.getBaseDAO().executeQuery(sql,
				new MapListProcessor());
		if (bizIfcList != null || bizIfcList.size() > 0) {
			for (Map<String, Object> bizIfc : bizIfcList) {
				if (bizIfc != null && bizIfc.size() > 0) {
					if ("5dd3c721-22ad-42b1-9c10-4351c236bc77".equals(String.valueOf(bizIfc.get("intattrid"))
							.toLowerCase())) {
						// PK
						this.setEntityPK((String) bizIfc.get("name"));
					} else if ("d32cc17b-f415-415a-923f-0764443eb102".equals(String.valueOf(bizIfc.get("intattrid"))
							.toLowerCase())) {
						// Code
						this.setEntityCode((String) bizIfc.get("name"));
					} else if ("ecf1b76a-6e44-42e2-a55e-87596504775b".equals(String.valueOf(bizIfc.get("intattrid"))
							.toLowerCase())) {
						// pk_org
						this.setEntityOrg((String) bizIfc.get("name"));
					}
				}

				this.setEntityIBDObjectImpl(true);
			}
		} else {
			this.setEntityCode(StringUtil.EMPTY);
			this.setEntityOrg(StringUtil.EMPTY);
			this.setEntityPK(StringUtil.EMPTY);
			this.setEntityIBDObjectImpl(false);
		}
	}

	private void initMultiLangMap() {
		LanguageVO[] enableLangVOs = MultiLangContext.getInstance().getEnableLangVOs();
		for (LanguageVO vo : enableLangVOs) {
			if (vo.getLangseq() == 1) {
				this.getMultiLangMap().put(vo.getLangcode().toLowerCase(), "");
			} else {
				this.getMultiLangMap().put(vo.getLangcode().toLowerCase(), String.valueOf(vo.getLangseq()));
			}
		}
	}

	private void initPropertyBizMap() throws BusinessException {
		AggMDClassVO mapping = this.getMdmappingVO();
		MDPropertyVO[] props = (MDPropertyVO[]) mapping.getChildren(MDPropertyVO.class);

		String sql = "";
		for (MDPropertyVO prop : props) {
			if (!StringUtils.isEmpty(prop.getMapfieldname())) {
				// 根據IBDObject接口取得業務主鍵對照
				sql = "select bmap.intattrid, clm.name, cls.defaulttablename tablename from md_bizItfMap bmap "
						+ "inner join md_property prop on bmap.classattrid = prop.id "
						+ "inner join md_class cls on prop.classid = cls.id "
						+ "inner join md_ormap orm on orm.attributeid = bmap.classattrid "
						+ "inner join md_column clm on clm.id = orm.columnid "
						+ "where bmap.classid=(select datatype from md_property where id='" + prop.getPk_property()
						+ "') and bmap.bizinterfaceid='6c8722b9-911a-489b-8d0d-18bd3734fcf6'";
				List<Map<String, Object>> bizIfcList = (List<Map<String, Object>>) this.getBaseDAO().executeQuery(sql,
						new MapListProcessor());
				if (bizIfcList == null || bizIfcList.size() == 0) {
					// 未實現IBDObject的屬性按一般類型處理
					continue;
				} else {
					for (Map<String, Object> bizIfc : bizIfcList) {
						if (bizIfc != null && bizIfc.size() > 0) {
							if ("5dd3c721-22ad-42b1-9c10-4351c236bc77".equals(String.valueOf(bizIfc.get("intattrid"))
									.toLowerCase())) {
								// PK
								this.getBizKeyMap().put(prop.getPk_property(), (String) bizIfc.get("name"));
							} else if ("d32cc17b-f415-415a-923f-0764443eb102".equals(String.valueOf(
									bizIfc.get("intattrid")).toLowerCase())) {
								// Code
								this.getBizCodeMap().put(prop.getPk_property(), (String) bizIfc.get("name"));
							} else if ("ecf1b76a-6e44-42e2-a55e-87596504775b".equals(String.valueOf(
									bizIfc.get("intattrid")).toLowerCase())) {
								// pk_org
								this.getBizOrgMap().put(prop.getPk_property(), (String) bizIfc.get("name"));
							}

							if (this.getBizCodeMap().containsKey(prop.getPk_property())
									&& this.getBizKeyMap().containsKey(prop.getPk_property())
									&& !this.getBizTableMap().containsKey(prop.getPk_property())) {
								this.getBizTableMap().put(prop.getPk_property(), (String) bizIfc.get("tablename"));
							}
						}
					}
				}
			}
		}
	}

	public List<Map<String, Object>> convertToNCValueObjects() throws BusinessException {
		return null;
	};

	public List<Map<String, Object>> convertToJsonValueObjects() throws BusinessException {
		return null;
	};

	public String convertJsonValueObjectsToString() throws BusinessException {
		return JsonUtil.map2JSON(this.getJsonValueObjects());
	}

	public abstract void doConvert() throws BusinessException;

	public List<Map<String, Object>> getNcValueObjects() {
		if (ncValueObjects == null) {
			ncValueObjects = new ArrayList<Map<String, Object>>();
		}
		return ncValueObjects;
	}

	public void setNcValueObjects(List<Map<String, Object>> ncValueObjects) {
		this.ncValueObjects = ncValueObjects;
	}

	public List<Map<String, Object>> getJsonValueObjects() {
		if (jsonValueObjects == null) {
			jsonValueObjects = new ArrayList<Map<String, Object>>();
		}
		return jsonValueObjects;
	}

	public void setJsonValueObjects(List<Map<String, Object>> jsonValueObjects) {
		this.jsonValueObjects = jsonValueObjects;
	}

	public String getNcEntityName() throws BusinessException {
		return ncEntityName;
	}

	public void setNcEntityName(String entityName) throws BusinessException {
		this.ncEntityName = entityName;
	}

	public AggMDClassVO getMdmappingVO() throws BusinessException {
		if (StringUtils.isEmpty(this.getNcEntityName())) {
			throw new BusinessException("操作失败：未指定转换实体");
		}

		if (mdmappingVO == null) {
			Collection objs = MDPersistenceService
					.lookupPersistenceQueryService()
					.queryBillOfVOByCond(
							AggMDClassVO.class,
							"pk_mdclass=(select pk_mdclass from hrpub_mdclass mapmd inner join md_class md on mapmd.pk_class = md.id where md.name = '"
									+ this.getNcEntityName()
									+ "' and pk_ioschema='"
									+ this.getPk_ioschema()
									+ "' and pk_org='"
									+ this.getPk_org()
									+ "' and isenabled='Y' and isnull(mapmd.dr, 0)=0)", true, false);
			if (objs != null && objs.size() > 0) {
				mdmappingVO = (AggMDClassVO) objs.toArray()[0];
			} else {
				throw new BusinessException("操作失败：未找到指定的语义元数据映射关系");
			}
		}
		return mdmappingVO;
	}

	public void setMdmappingVO(AggMDClassVO mdmappingVO) {
		this.mdmappingVO = mdmappingVO;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getPk_group() {
		return pk_group;
	}

	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	public String getPk_ioschema() {
		return pk_ioschema;
	}

	public void setPk_ioschema(String pk_ioschema) {
		this.pk_ioschema = pk_ioschema;
	}

	public String getCuserid() {
		return cuserid;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) throws BusinessException {
		if (this.getLogService().isSessionExists(sessionid)) {
			throw new BusinessException("[SESSIONKEY] 已存在，请使用新的进程ID");
		}
		this.sessionid = sessionid;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public IMDExchangeLogService getLogService() {
		if (logSrv == null) {
			logSrv = NCLocator.getInstance().lookup(IMDExchangeLogService.class);
		}
		return logSrv;
	}

	public String getEntityPK() {
		return entityPK;
	}

	public void setEntityPK(String entityPK) {
		this.entityPK = entityPK;
	}

	public String getEntityOrg() {
		return entityOrg;
	}

	public void setEntityOrg(String entityOrg) {
		this.entityOrg = entityOrg;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public boolean isEntityIBDObjectImpl() {
		return isEntityIBDObjectImpl;
	}

	public boolean isHoldReservedProperties() {
		return holdReservedProperties;
	}

	public void setHoldReservedProperties(boolean holdReservedProperties) {
		this.holdReservedProperties = holdReservedProperties;
	}

	public boolean isSkipNotExistMapping() {
		return skipNotExistMapping;
	}

	public void setSkipNotExistMapping(boolean skipNotExistMapping) {
		this.skipNotExistMapping = skipNotExistMapping;
	}

	public void setEntityIBDObjectImpl(boolean isEntityIBDObjectImpl) {
		this.isEntityIBDObjectImpl = isEntityIBDObjectImpl;
	}

	/**
	 * OBDObject字段映射
	 * 
	 * key:md_property.id , value:md_property.datatype 对应表 PK 字段名
	 * 
	 * @return
	 */
	public Map<String, String> getBizKeyMap() {
		if (bizKeyMap == null) {
			bizKeyMap = new HashMap<String, String>();
		}
		return bizKeyMap;
	}

	public void setBizKeyMap(Map<String, String> bizKeyMap) {
		this.bizKeyMap = bizKeyMap;
	}

	/**
	 * OBDObject字段映射
	 * 
	 * key:md_property.id, value:md_property.datatype 对应表 Code 字段名
	 * 
	 * @return
	 */
	public Map<String, String> getBizCodeMap() {
		if (bizCodeMap == null) {
			bizCodeMap = new HashMap<String, String>();
		}
		return bizCodeMap;
	}

	public void setBizCodeMap(Map<String, String> bizCodeMap) {
		this.bizCodeMap = bizCodeMap;
	}

	/**
	 * OBDObject字段映射
	 * 
	 * key:md_property.id, value:md_property.datatype 对应表名称
	 * 
	 * @return
	 */
	public Map<String, String> getBizTableMap() {
		if (bizTableMap == null) {
			bizTableMap = new HashMap<String, String>();
		}
		return bizTableMap;
	}

	public void setBizTableMap(Map<String, String> bizTableMap) {
		this.bizTableMap = bizTableMap;
	}

	public Map<String, String> getBizOrgMap() {
		if (bizOrgMap == null) {
			bizOrgMap = new HashMap<String, String>();
		}
		return bizOrgMap;
	}

	public void setBizOrgMap(Map<String, String> bizOrgMap) {
		this.bizOrgMap = bizOrgMap;
	}

	public Map<String, String> getBizAuditInfoMap() {
		if (bizAuditInfoMap == null) {
			bizAuditInfoMap = new HashMap<String, String>();
		}
		return bizAuditInfoMap;
	}

	public void setBizAuditInfoMap(Map<String, String> bizAuditInfoMap) {
		this.bizAuditInfoMap = bizAuditInfoMap;
	}

	public Map<String, String> getMultiLangMap() {
		if (multiLangMap == null) {
			multiLangMap = new HashMap<String, String>();
		}
		return multiLangMap;
	}

	public void setMultiLangMap(Map<String, String> multiLangMap) {
		this.multiLangMap = multiLangMap;
	}

	public BaseDAO getBaseDAO() {
		if (baseDAO == null) {
			baseDAO = new BaseDAO();
		}
		return baseDAO;
	}

	public IBusinessEntity getBusinessEntity() throws BusinessException {
		if (businessEntity == null) {
			businessEntity = MDQueryService.lookupMDInnerQueryService().getBusinessEntityByID(this.getClassid());
		}
		return businessEntity;
	}

	protected void Logging(String status) throws BusinessException {
		if (status.equals("SESSION:INIT")) {
			if (this.getLogService().isSessionExists(this.getSessionid())) {
				throw new BusinessException("[SESSIONKEY]已经存在，请使用新的进程ID重试");
			}
		}
		this.getLogService().logging(this.getSessionid(), this.getClassid(), this.getOperationType(), status,
				new UFDateTime());
	}

	protected String getCodeFromPKSQL(String pk_property, String pkStr) {
		String subTable = this.getBizTableMap().get(pk_property);
		String subAlias = subTable + "_" + String.valueOf((int) (Math.random() * (10001)));
		String transPart = "(select " + subAlias + "." + this.getBizCodeMap().get(pk_property) + " from " + subTable
				+ " " + subAlias + " where " + subAlias + "." + this.getBizKeyMap().get(pk_property) + " = " + pkStr
				+ ")";
		return transPart;
	}

	public Map<String, String> getErrorMessages() {
		if (errorMessages == null) {
			errorMessages = new HashMap<String, String>();
		}
		return errorMessages;
	}

	public void setErrorMessages(Map<String, String> errorMessages) {
		this.errorMessages = errorMessages;
	}

}
