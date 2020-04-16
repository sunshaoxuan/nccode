package nc.impl.hrpub.dataexchange;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nc.bs.logging.Logger;
import nc.itf.hrpub.IDataExchangeExternalExecutor;
import nc.itf.hrpub.util.JsonUtil;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.md.model.IAttribute;
import nc.md.model.type.IType;
import nc.vo.hrpub.mdmapping.AggMDClassVO;
import nc.vo.hrpub.mdmapping.MDPropertyVO;
import nc.vo.jcom.security.Base64;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

public class DataExportExecutor extends AbstractExecutor {
	private IDataExchangeExternalExecutor extendBizClass = null;
	private String dataRange;
	private UFDateTime lastTime;
	private String condition;
	private Map<String, String> loadKVMap;
	private boolean isQueryByBP;
	private boolean extendClassLoaded = false;
	private List<Map<String, Object>> resultRows;
	private Map<String, Object> bpQueryConditions;

	public DataExportExecutor() throws BusinessException {
		super();
	}

	public String getDataRange() {
		if (this.getJsonValueObjects() != null && this.getJsonValueObjects().size() > 0) {
			dataRange = (String) ((Map<String, Object>) this.getJsonValueObjects().get(0).get("EXPORTDATA"))
					.get("DATARANGE");
		}
		return dataRange;
	}

	public void setDataRange(String dataRange) {
		this.dataRange = dataRange;
	}

	public UFDateTime getLastTime() {
		if (this.getJsonValueObjects() != null && this.getJsonValueObjects().size() > 0) {
			lastTime = new UFDateTime((String) this.getJsonValueObjects().get(0).get("LASTTIME"));
		}
		return lastTime;
	}

	public void setLastTime(UFDateTime lastTime) {
		this.lastTime = lastTime;
	}

	public String getCondition() throws BusinessException {
		if (StringUtils.isEmpty(condition)) {
			if (this.getJsonValueObjects() != null && this.getJsonValueObjects().size() > 0) {
				condition = (String) ((Map<String, Object>) this.getJsonValueObjects().get(0).get("EXPORTDATA"))
						.get("CONDITION");
			}
		}
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Map<String, String> getLoadKVMap() {
		return loadKVMap;
	}

	public void setLoadKVMap(Map<String, String> loadKVMap) {
		this.loadKVMap = loadKVMap;
	}

	@Override
	public void execute() throws BusinessException {
		super.execute();

		if (!this.isQueryByBP() && findLoadedExtendClass()) {
			extendClassLoaded = true;
		} else {
			if (this instanceof IDataExchangeExternalExecutor) {
				this.extendBizClass = (IDataExchangeExternalExecutor) this;
				extendClassLoaded = true;
			}
		}

		Logger.error("--- Data Exchange Log ---");
		Logger.error("Biz handler Class found: " + String.valueOf(extendClassLoaded));

		dealCondition();

		if (extendClassLoaded) {
			Logger.error("Biz handler Class: syn public 2 handler.");
			synClassProperties(this, this.extendBizClass);
			Logger.error("Biz handler Class: beforeQuery");
			extendBizClass.beforeQuery();
			Logger.error("Biz handler Class: syn handler 2 public.");
			synClassProperties(this.extendBizClass, this);
		}

		if (isQueryByBP()) {
			if (extendClassLoaded) {
				Logger.error("Biz handler Class: doUpdateByBP");
				this.Logging("IMPORT:DOBPCONVERT");
				extendBizClass.doQueryByBP();
			}
		} else {
			this.Logging("IMPORT:DOQUERY");
			doQuery();
		}

		if (extendClassLoaded) {
			Logger.error("Biz handler Class: afterQuery");
			extendBizClass.afterQuery();
		}

		if (extendClassLoaded) {
			Logger.error("Biz handler Class: syn public 2 handler.");
			synClassProperties(this, this.extendBizClass);
			Logger.error("Biz handler Class: beforeConvert");
			extendBizClass.beforeConvert();
			Logger.error("Biz handler Class: syn handler 2 public.");
			synClassProperties(this.extendBizClass, this);
		}
		this.Logging("IMPORT:DOCONVERT");
		doConvert();
		if (extendClassLoaded) {
			Logger.error("Biz handler Class: afterConvert");
			extendBizClass.afterConvert();
		}

		this.Logging("EXPORT:FINISH");
	}

	/**
	 * 查询前事件
	 * 
	 * @throws BusinessException
	 */
	public void beforeQuery() throws BusinessException {
	}

	/**
	 * 查询后事件
	 * 
	 * @throws BusinessException
	 */
	public void afterQuery() throws BusinessException {
	}

	/**
	 * 转换前事件
	 * 
	 * @throws BusinessException
	 */
	public void beforeConvert() throws BusinessException {
	}

	/**
	 * 转换后事件
	 * 
	 * @throws BusinessException
	 */
	public void afterConvert() throws BusinessException {
	}

	/**
	 * 查找當前類已加載的業務子類
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private boolean findLoadedExtendClass() throws BusinessException {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
		provider.addIncludeFilter(new AssignableTypeFilter(DataExportExecutor.class));
		Set<BeanDefinition> components = provider.findCandidateComponents("nc/impl/hrpub/dataexchange/businessprocess");
		Logger.error("--- Data Export External Class Check Log ---");
		Logger.error("Components found: " + (components == null ? "0" : String.valueOf(components.size())));
		try {
			for (BeanDefinition component : components) {
				Logger.error("Compnents bean class name: " + component.getBeanClassName());
				Class loadedClass = Class.forName(component.getBeanClassName());
				IDataExchangeExternalExecutor classImpl = (IDataExchangeExternalExecutor) loadedClass.newInstance();

				// 加載類實現實體ID = 當前類處理實體ID
				if (classImpl.getBizEntityID() instanceof List) {
					if (((List<String>) classImpl.getBizEntityID()).contains(this.getBusinessEntity().getID())) {
						this.extendBizClass = classImpl;
						return true;
					}
				} else if (classImpl.getBizEntityID() instanceof String) {
					if (classImpl.getBizEntityID().equals(this.getBusinessEntity().getID())) {
						this.extendBizClass = classImpl;
						return true;
					}
				}
			}
		} catch (Exception e) {
			throw new BusinessException(e);
		}

		return false;
	}

	private void synClassProperties(DataExportExecutor source, IDataExchangeExternalExecutor target) {
		if (target != null) {
			Method[] methods = target.getClass().getMethods();
			for (Method setMethod : methods) {
				if (setMethod.getName().indexOf("set") == 0) {
					String propName = setMethod.getName().substring(setMethod.getName().indexOf("set") + 3);
					try {
						Method getMethod = source.getClass().getMethod("get" + propName, null);
						if (getMethod == null) {
							getMethod = source.getClass().getMethod("is" + propName, null);
						}

						if (getMethod != null) {
							setMethod.invoke(target, getMethod.invoke(source, null));
						}
					} catch (Exception e) {
						Logger.error(e.getMessage());
					}
				}
			}
		}
	}

	private void synClassProperties(IDataExchangeExternalExecutor source, DataExportExecutor target) {
		if (target != null) {
			Method[] methods = target.getClass().getMethods();
			for (Method setMethod : methods) {
				if (setMethod.getName().indexOf("set") == 0) {
					String propName = setMethod.getName().substring(setMethod.getName().indexOf("set") + 3);
					try {
						Method getMethod = source.getClass().getMethod("get" + propName, null);
						if (getMethod == null) {
							getMethod = source.getClass().getMethod("is" + propName, null);
						}

						if (getMethod != null) {
							setMethod.invoke(target, getMethod.invoke(source, null));
						}
					} catch (Exception e) {
						Logger.error(e.getMessage());
					}
				}
			}
		}
	}

	private void dealCondition() throws BusinessException {
		if (this.isQueryByBP()) {
			this.setBpQueryConditions(JsonUtil.json2Map(this.getCondition().replace("\\", "")));

			String strTablename = this.getBusinessEntity().getTable().getName();
			getSelectFields(strTablename);
		} else {
			if (this.getDataRange().toUpperCase().equals("ALL")) {
				this.setCondition("1=1");
			} else if (this.getDataRange().toUpperCase().equals("LAST")) {
				this.setCondition("ts >= '" + this.getLastTime().toString() + "'");
			} else {
				this.setCondition(convertRefValues(this.getCondition()));
			}

			if (this.getBusinessEntity().getName().toUpperCase().startsWith("DEFDOC-")) {
				String defDocCode = this.getBusinessEntity().getName().substring(7);
				this.setCondition(this.getCondition()
						+ " and pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = '" + defDocCode
						+ "')");
			}
		}
	}

	private String convertRefValues(String strCond) {
		return strCond;
	}

	private void doQuery() throws BusinessException {
		StringBuffer sql = new StringBuffer();
		String strTablename = this.getBusinessEntity().getTable().getName();
		String strSelect = getSelectFields(strTablename);

		if (!StringUtils.isEmpty(strSelect)) {
			sql.append("select ");
			sql.append(strSelect);
			sql.append(", " + this.getBusinessEntity().getPrimaryKey().getPKColumn().getName() + " ROWNO");
			sql.append(" from ");
			sql.append(strTablename);
			sql.append(" where ");
			sql.append(this.getCondition());

			if (this.getBusinessEntity().getAttributeByName("pk_org") != null) {
				sql.append(" and (pk_org='" + this.getPk_org() + "' or pk_org in (select pk_group from org_group) "
						+ " or pk_org in (select pk_org from org_orgs where code='global'))");
			}

			this.setResultRows((List<Map<String, Object>>) this.getBaseDAO().executeQuery(sql.toString(),
					new MapListProcessor()));
		}
		this.Logging("EXPORT:DOQUERY");
	}

	private String getSelectFields(String tablename) throws BusinessException {
		AggMDClassVO mapping = this.getMdmappingVO();
		MDPropertyVO[] props = (MDPropertyVO[]) mapping.getChildren(MDPropertyVO.class);
		StringBuffer strSelect = new StringBuffer();
		if (props != null && props.length > 0) {
			for (MDPropertyVO prop : props) {
				if (!StringUtils.isEmpty(prop.getMapfieldname())) {
					if (strSelect.length() > 0) {
						strSelect.append(",");
					}

					String propName = findPropertyName(prop.getPk_property());
					String propMultiName = findPropertyMultiName(propName);

					if (!this.getBizCodeMap().containsKey(prop.getPk_property())) {
						strSelect.append(propMultiName + " " + propName);
					} else {
						strSelect.append(this.getCodeFromPKSQL(prop.getPk_property(), tablename + "." + propMultiName));
						strSelect.append(" " + propName);
					}
				}
			}
		}
		return strSelect.toString();
	}

	private String findPropertyMultiName(String propName) throws BusinessException {
		IAttribute att = this.getBusinessEntity().getAttributeByName(propName);
		if (att.getDataType().getTypeType() == IType.MULTILANGUAGE) {
			return propName + this.getMultiLangMap().get(this.getLanguage());
		}
		return propName;
	}

	Map<String, String> propertyIDNameMap = new HashMap<String, String>();

	private String findPropertyName(String propertyid) throws BusinessException {
		List<IAttribute> attribs = this.getBusinessEntity().getAttributes();

		for (IAttribute att : attribs) {
			if (att.getID().toLowerCase().equals(propertyid.toLowerCase())) {
				propertyIDNameMap.put(att.getName(), propertyid);
				return att.getName();
			}
		}

		throw new BusinessException("操作失败：获取实体属性名称时发生未知错误");
	}

	@Override
	public void doConvert() throws BusinessException {
		// Export.doConvert: NCObject -> JsonObject，結果轉換
		if (this.getNcValueObjects() != null && this.getNcValueObjects().size() > 0) {
			this.setJsonValueObjects(new ArrayList<Map<String, Object>>());
			Map<String, Object> jsonMap = new HashMap<String, Object>();
			jsonMap.put("METADATA", this.getBusinessEntity().getName());
			jsonMap.put("RECORD", new ArrayList<Map<String, Object>>());
			for (Map<String, Object> ncobj : this.getNcValueObjects()) {
				Map<String, Object> record = new HashMap<String, Object>();
				if (ncobj != null && ncobj.size() > 0) {
					for (Entry<String, Object> kv : ncobj.entrySet()) {
						String key = getMappingName(kv.getKey());
						if (!StringUtils.isEmpty(key)) {
							record.put(key.toUpperCase(), convertBLOBValue(kv.getValue()));
						}
					}
				}
				((List) jsonMap.get("RECORD")).add(record);
			}
			this.getJsonValueObjects().add(jsonMap);
		}
		this.Logging("EXPORT:DOCONVERT");
	}

	private String getPropIDMappingName(String mapName) throws BusinessException {
		AggMDClassVO mapping = this.getMdmappingVO();

		if (mapping != null) {
			MDPropertyVO[] props = (MDPropertyVO[]) mapping.getChildren(MDPropertyVO.class);
			if (props != null && props.length > 0) {
				for (MDPropertyVO prop : props) {
					// ssx modified on 2019-01-08
					if (mapName.toLowerCase().equals(
							prop.getMapfieldname() == null ? null : prop.getMapfieldname().toLowerCase())) // 与元数据对比一律使用小写做匹配
					{
						return prop.getPk_property();
					}
				}
			}
		}

		return "";
	}

	private Object convertBLOBValue(Object value) {
		if (value instanceof byte[]) {
			StringBuffer retStr = new StringBuffer();
			byte[] retvalue = Base64.encode((byte[]) value);
			if (retvalue != null && retvalue.length > 0) {
				for (byte val : retvalue) {
					retStr.append((char) val);
				}
			}
			return retStr.toString();
		} else {
			return value;
		}
	}

	private String getMappingName(String key) throws BusinessException {
		AggMDClassVO mapping = this.getMdmappingVO();
		MDPropertyVO[] props = (MDPropertyVO[]) mapping.getChildren(MDPropertyVO.class);
		if (props != null && props.length > 0) {
			for (MDPropertyVO prop : props) {
				if (!StringUtils.isEmpty(prop.getMapfieldname())) {
					if (prop.getPk_property().equals(propertyIDNameMap.get(key))) {
						return prop.getMapfieldname();
					}
				}
			}
		}

		return "";
	}

	public boolean isQueryByBP() {
		return isQueryByBP;
	}

	public void setQueryByBP(boolean isQueryByBP) {
		this.isQueryByBP = isQueryByBP;
	}

	public List<Map<String, Object>> getResultRows() {
		return resultRows;
	}

	public void setResultRows(List<Map<String, Object>> resultRows) {
		if (resultRows != null && resultRows.size() > 0) {
			for (Map<String, Object> valueRow : resultRows) {
				if (valueRow != null && valueRow.size() > 0) {
					this.getNcValueObjects().add(valueRow);
				}
			}
		}
	}

	public Map<String, Object> getBpQueryConditions() {
		return bpQueryConditions;
	}

	public void setBpQueryConditions(Map<String, Object> bpQueryConditions) {
		this.bpQueryConditions = bpQueryConditions;
	}

}
