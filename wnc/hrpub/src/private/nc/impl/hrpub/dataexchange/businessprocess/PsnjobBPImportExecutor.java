package nc.impl.hrpub.dataexchange.businessprocess;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.framework.common.NCLocator;
import nc.impl.hrpub.MDExchangeServiceImpl;
import nc.impl.hrpub.dataexchange.DataImportExecutor;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hrpub.IDataExchangeExternalExecutor;
import nc.itf.trn.rds.IRdsManageService;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

public class PsnjobBPImportExecutor extends DataImportExecutor implements IDataExchangeExternalExecutor {

	private Map<String, PsndocAggVO> rowNCVO;
	private Map<String, String> rowPsnTypeMap;
	Map<String, List> upgJsonObjs = new HashMap<String, List>();

	public PsnjobBPImportExecutor() throws BusinessException {
		super();
	}

	@Override
	public Object getBizEntityID() throws BusinessException {
		// 必須賦值，否則不會加載本類型
		// 實體ID為md_class的ID欄位
		// md_class.name = hi_psnjob
		// 不定義返回類ID，BP模式不適用
		// return "7156d223-4531-4337-b192-492ab40098f1";
		return null;
	}

	@Override
	public void beforeUpdate() throws BusinessException {
		if (this.getNcValueObjects() != null && this.getNcValueObjects().size() > 0) {
			String rowNo = "";
			// 直属主管
			List<HashMap<String, Object>> deptlist = getPrincipal();
			for (Map<String, Object> rowNCMap : this.getNcValueObjects()) {
				try {
					rowNo = rowNCMap.keySet().toArray(new String[0])[0].split(":")[0];

					String pk_psndoc = (String) rowNCMap.get(rowNo + ":pk_psndoc");

					if (StringUtils.isEmpty(pk_psndoc)) {
						throw new BusinessException("员工编码未能匹配到有效的员工档案。");
					}

					PsndocAggVO aggVO = this.getMDQueryService().queryPsndocVOByPk(pk_psndoc);

					PsndocVO psndocVO = aggVO.getParentVO();

					if (null == psndocVO.getPsnJobVO().getPk_psnjob()) {
						throw new BusinessException("数据传输错误，请检查!");
					}

					PsnJobVO psnjobVO = new PsnJobVO();
					psnjobVO.setAssgid(psndocVO.getPsnJobVO().getAssgid());
					psnjobVO.setBegindate(new UFLiteralDate((String) rowNCMap.get(rowNo + ":begindate")));
					psnjobVO.setClerkcode(psndocVO.getCode());
					if (null == rowNCMap.get(rowNo + ":creationtime")) {
						psnjobVO.setCreationtime(psndocVO.getCreationtime());
					} else {
						psnjobVO.setCreationtime(new UFDateTime((String) rowNCMap.get(rowNo + ":creationtime")));
					}

					if (!StringUtils.isEmpty((String) rowNCMap.get(rowNo + ":creator"))) {
						psnjobVO.setCreator((String) rowNCMap.get(rowNo + ":creator"));
					} else {
						psnjobVO.setCreator(this.getCuserid());
					}
					psnjobVO.setDr(0);
					if (!StringUtils.isEmpty((String) rowNCMap.get(rowNo + ":enddate"))) {
						psnjobVO.setEnddate(new UFLiteralDate((String) rowNCMap.get(rowNo + ":enddate")));
					}
					psnjobVO.setEndflag(UFBoolean.FALSE);
					psnjobVO.setLastflag(UFBoolean.TRUE);
					psnjobVO.setIsmainjob(UFBoolean.TRUE);
					if (null == rowNCMap.get(rowNo + ":jobglbdef1")) {
						psnjobVO.setAttributeValue("jobglbdef1", psndocVO.getPsnJobVO().getAttributeValue("jobglbdef1"));
					} else {
						psnjobVO.setAttributeValue("jobglbdef1", rowNCMap.get(rowNo + ":jobglbdef1"));
					}

					if (null == rowNCMap.get(rowNo + ":jobglbdef4")) {
						psnjobVO.setAttributeValue("jobglbdef4", psndocVO.getPsnJobVO().getAttributeValue("jobglbdef4"));
					} else {
						psnjobVO.setAttributeValue("jobglbdef4", rowNCMap.get(rowNo + ":jobglbdef4"));
					}
					if (null == rowNCMap.get(rowNo + ":jobglbdef5")) {
						psnjobVO.setAttributeValue("jobglbdef5", psndocVO.getPsnJobVO().getAttributeValue("jobglbdef5"));
					} else {
						psnjobVO.setAttributeValue("jobglbdef5", rowNCMap.get(rowNo + ":jobglbdef5"));
					}
					if (null == rowNCMap.get(rowNo + ":jobglbdef7")) {
						psnjobVO.setAttributeValue("jobglbdef7", psndocVO.getPsnJobVO().getAttributeValue("jobglbdef7"));
					} else {
						psnjobVO.setAttributeValue("jobglbdef7", rowNCMap.get(rowNo + ":jobglbdef7"));
					}
					if (null == rowNCMap.get(rowNo + ":jobglbdef8")) {
						psnjobVO.setAttributeValue("jobglbdef8", psndocVO.getPsnJobVO().getAttributeValue("jobglbdef8"));
					} else {
						psnjobVO.setAttributeValue("jobglbdef8", rowNCMap.get(rowNo + ":jobglbdef8"));
					}

					if (!StringUtils.isEmpty((String) rowNCMap.get(rowNo + ":trnsevent"))) {
						psnjobVO.setTrnsevent(Integer.valueOf((String) rowNCMap.get(rowNo + ":trnsevent")));
					} else {
						throw new BusinessException("異動事件 [trnsevent] 不能為空");
					}
					if (null == rowNCMap.get(rowNo + ":memo")) {
						psnjobVO.setMemo(psndocVO.getPsnJobVO().getMemo());
					} else {
						psnjobVO.setMemo((String) rowNCMap.get(rowNo + ":memo"));
					}
					if (null == rowNCMap.get(rowNo + ":pk_dept")) {
						psnjobVO.setPk_dept(psndocVO.getPsnJobVO().getPk_dept());
					} else {
						psnjobVO.setPk_dept((String) rowNCMap.get(rowNo + ":pk_dept"));
					}

					psnjobVO.setPk_group(this.getPk_group());
					psnjobVO.setPk_hrgroup(this.getPk_group());
					psnjobVO.setPk_hrorg(psndocVO.getPsnJobVO().getPk_hrorg());
					psnjobVO.setPk_org(psndocVO.getPsnJobVO().getPk_org());
					if (null == rowNCMap.get(rowNo + ":pk_jobrank")) {
						psnjobVO.setPk_jobrank(psndocVO.getPsnJobVO().getPk_jobrank());
					} else {
						psnjobVO.setPk_jobrank((String) rowNCMap.get(rowNo + ":pk_jobrank"));
					}

					if (null == rowNCMap.get(rowNo + ":pk_psncl")) {
						psnjobVO.setPk_psncl(psndocVO.getPsnJobVO().getPk_psncl());
					} else {
						psnjobVO.setPk_psncl((String) rowNCMap.get(rowNo + ":pk_psncl"));
					}

					psnjobVO.setPk_psnorg(psndocVO.getPsnJobVO().getPk_psnorg());
					psnjobVO.setPoststat(UFBoolean.TRUE);
					psnjobVO.setRecordnum(0);
					psnjobVO.setShoworder(9999999);
					psnjobVO.setTrial_flag(UFBoolean.FALSE);
					// psnjobVO.setTs(new UFDateTime());
					psnjobVO.setPk_psndoc((String) rowNCMap.get(rowNo + ":pk_psndoc"));
					psnjobVO.setPsntype(0);
					psnjobVO.setAttributeValue("jobglbdef9",
							getPrincipal(deptlist, psnjobVO.getPk_dept(), psnjobVO.getPk_psndoc()));
					if (null == rowNCMap.get(rowNo + ":series")) {
						psnjobVO.setSeries(psndocVO.getPsnJobVO().getSeries());
					} else {
						psnjobVO.setSeries((String) rowNCMap.get(rowNo + ":series"));
					}
					psnjobVO.setTrnsreason((String) rowNCMap.get(rowNo + ":trnsreason"));

					if (null == rowNCMap.get(rowNo + ":trnstype")) {
						psnjobVO.setTrnstype(psndocVO.getPsnJobVO().getTrnstype());
					} else {
						psnjobVO.setTrnstype((String) rowNCMap.get(rowNo + ":trnstype"));
					}

					aggVO.setChildrenVO(null);
					aggVO.setTableVO("hi_psndoc_deptchg", new SuperVO[] { (SuperVO) psnjobVO });
					aggVO.setTableVO(PsnJobVO.getDefaultTableName(), aggVO.getTableVO("hi_psndoc_deptchg"));

					this.getRowNCVO().put(rowNo + ":" + psnjobVO.getBegindate(), aggVO);

				} catch (Exception e) {
					this.getErrorMessages().put(rowNo, e.getMessage());
				}
			}
		}
	}

	@Override
	public void afterUpdate() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void afterConvert() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeConvert() throws BusinessException {
		if (this.getJsonValueObjects() != null && this.getJsonValueObjects().size() > 0) {
			for (Map<String, Object> jsonobj : this.getJsonValueObjects()) {
				String rowNo = "";
				String psntype = "";
				List<Map<String, Object>> upgObj = new ArrayList<Map<String, Object>>();
				for (Entry<String, Object> entry : jsonobj.entrySet()) {
					if (entry.getKey().contains("ROWNO")) {
						rowNo = (String) entry.getValue();
					} else if (entry.getKey().contains("NPEONO")) {
						psntype = (String) entry.getValue();
					} else if (entry.getKey().contains("ISPROMOTE")) {
						// 含有晉陞資料
						if (entry.getValue() != null && entry.getValue().toString().toUpperCase().equals("Y")
								&& !entry.getValue().toString().toUpperCase().equals("1")) {
							upgObj.add(jsonobj);
						}
					}
				}

				if (upgObj.size() > 0) {
					upgJsonObjs.put(rowNo, upgObj);
				}

				this.getRowPsnTypeMap().put(rowNo, psntype);
			}
		}
	}

	@Override
	public void beforeQuery() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void afterQuery() throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeInsertOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void beforeUpdateOperation(Map<String, Object> rowMap) throws BusinessException {
		// TODO 自动生成的方法存根

	}

	@Override
	public void doUpdateByBP() throws BusinessException {
		if (rowNCVO != null && rowNCVO.size() > 0) {
			List<String> arraylist = new ArrayList<String>();
			for (Entry<String, PsndocAggVO> rowData : rowNCVO.entrySet()) {
				arraylist.add(rowData.getKey());
			}
			// 通过日期进行排序
			ListSort(arraylist);
			for (String s : arraylist) {
				try {
					PsndocAggVO aggVO = this.getMDQueryService().queryPsndocVOByPk(
							rowNCVO.get(s).getParentVO().getPk_psndoc());
					PsndocVO psndocVO = aggVO.getParentVO();
					rowNCVO.get(s).setParentVO(psndocVO);
					this.getVOSaveService().addPsnjob(rowNCVO.get(s), "hi_psndoc_deptchg", true, this.getPk_org());

					if (upgJsonObjs != null && upgJsonObjs.size() > 0) {
						String classid = (new MDExchangeServiceImpl()).checkExchangeRights(this.getOperationType(),
								this.getPk_org(), this.getPk_ioschema(), this.getCuserid(), "hi_psndoc_glbdef9");

						// 含有晉陞資料
						PsnInfosetImportExecutor infosetImporter = new PsnInfosetImportExecutor();
						infosetImporter.setNcEntityName("hi_psndoc_glbdef9");
						infosetImporter.setJsonValueObjects(upgJsonObjs.get(s.split(":")[0]));
						infosetImporter.setPk_ioschema(this.getPk_ioschema());
						infosetImporter.setPk_org(this.getPk_org());
						infosetImporter.setPk_group(this.getPk_group());
						infosetImporter.setCuserid(this.getCuserid());
						infosetImporter.setClassid(classid);
						infosetImporter.setOperationType(this.getOperationType());
						infosetImporter.setSessionid(this.getSessionid() + (s.split(":")[0]));
						infosetImporter.setLanguage(this.getLanguage());
						infosetImporter.setHoldReservedProperties(false);
						infosetImporter.setSkipNotExistMapping(true);
						infosetImporter.execute();

						if (infosetImporter.getErrorMessages() != null && infosetImporter.getErrorMessages().size() > 0) {
							this.getErrorMessages().putAll(infosetImporter.getErrorMessages());
						}
					}
				} catch (BusinessException e) {
					this.getErrorMessages().put(s.split(":")[0], e.getMessage());
				}
			}
		}
	}

	IRdsManageService voSaveService = null;

	private IRdsManageService getVOSaveService() {
		if (voSaveService == null) {
			voSaveService = NCLocator.getInstance().lookup(IRdsManageService.class);
		}
		return voSaveService;
	}

	IPsndocQryService qryService = null;

	private IPsndocQryService getMDQueryService() {
		if (qryService == null) {
			qryService = (IPsndocQryService) NCLocator.getInstance().lookup(IPsndocQryService.class);
		}

		return qryService;
	}

	public Map<String, PsndocAggVO> getRowNCVO() {
		if (rowNCVO == null) {
			rowNCVO = new HashMap<String, PsndocAggVO>();
		}
		return rowNCVO;
	}

	public void setRowNCVO(Map<String, PsndocAggVO> rowNCVO) {
		this.rowNCVO = rowNCVO;
	}

	public Map<String, String> getRowPsnTypeMap() {
		if (rowPsnTypeMap == null) {
			rowPsnTypeMap = new HashMap<String, String>();
		}
		return rowPsnTypeMap;
	}

	public void setRowPsnTypeMap(Map<String, String> rowPsnTypeMap) {
		this.rowPsnTypeMap = rowPsnTypeMap;
	}

	public static void ListSort(List<String> list) {
		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date dt1 = format.parse(o1.split(":")[1]);
					Date dt2 = format.parse(o2.split(":")[1]);
					if (dt1.getTime() > dt2.getTime()) {
						return 1;
					} else if (dt1.getTime() < dt2.getTime()) {
						return -1;
					} else {
						return 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
	}

	private List<HashMap<String, Object>> getPrincipal() {
		String queryClusterSql = "SELECT PK_FATHERORG,pk_dept, principal FROM org_dept WHERE  dr=0 ";
		IUAPQueryBS impl = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());

		List<HashMap<String, Object>> result = null;

		try {
			result = (ArrayList<HashMap<String, Object>>) impl.executeQuery(queryClusterSql, new MapListProcessor());
		} catch (BusinessException e1) {
			result = null;
			return result;
		}
		return result;
	}

	private String getPrincipal(List<HashMap<String, Object>> list, String pk_dept, String pk_psndoc) {
		if (null == pk_psndoc) {
			return null;
		}
		if (null == pk_dept) {
			return null;
		}
		if (list.size() > 0) {
			for (HashMap<String, Object> map : list) {
				if (null != map.get("pk_dept") && map.get("pk_dept").equals(pk_dept)) {
					if (null != map.get("principal") && map.get("principal").equals(pk_psndoc)) {
						return getPrincipal(list,
								null == map.get("fatherorg") ? null : String.valueOf(map.get("fatherorg")), pk_psndoc);
					} else if (null != map.get("principal") && !map.get("principal").equals(pk_psndoc)) {
						return String.valueOf(map.get("principal"));
					}
				}
			}
		}
		return null;
	}
}
