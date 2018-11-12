package nc.impl.wa.datainterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.hr.dataexchange.export.DataFormatter;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.ResHelper;
import nc.itf.org.IOrgPubQryService;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.wa.datainterface.IReportExportService;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.para.SysInitQuery;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.hrwa.sumincometax.SumIncomeTaxVO;
import nc.vo.ml.MultiLangUtil;
import nc.vo.org.HROrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.category.WaClassVO;

import org.apache.commons.lang.StringUtils;

public class ReportExportServiceImpl implements IReportExportService {
	@SuppressWarnings("all")
	public String[] getIITXTextReport(String[] dataPKs, int iYear, String[] applyFormat, String applyCount,
			String applyReason, String vatNumber, String grantType, String comLinkMan, String comLinkTel,
			String comLinkEmail) throws BusinessException {
		Map<String, Object> refList = getRefListByVatNumber(vatNumber);
		//�����걨��ʽ�ֱ���
		List<String[]> declaretypelists = new ArrayList<String[]>();
		Map<String,String> maps = new HashMap<String,String>();
		Map<String,String> distinctmaps = new HashMap<String,String>();
		Map<String,List<String>> map = new HashMap<String,List<String>>();
		InSQLCreator creator = new InSQLCreator();
		BaseDAO basedao = new BaseDAO();
		List<Map<String,String>> lists = (List<Map<String,String>>) basedao.executeQuery("select  declaretype, pk_sumincometax from hrwa_sumincometax where pk_sumincometax in ("+creator.getInSQL(dataPKs)+");", new MapListProcessor());
		for(Map<String,String> list : lists){
			maps.put(list.get("pk_sumincometax"), list.get("declaretype"));
			distinctmaps.put(list.get("declaretype"), null);
		}
		for(String declaretype : distinctmaps.keySet()){
			List<String> datapks = new ArrayList<String>();
			for(String pk_sumincometax : maps.keySet()){
					if(maps.get(pk_sumincometax).equals(declaretype)){
						datapks.add(pk_sumincometax);
					}
			}
			map.put(declaretype, datapks);
		}
		for(String declaretype : map.keySet()){
			String[] arr = { declaretype };
			String[] applyFormats = getDeclaretype(arr);
			String tempTableName = creator.getInSQL(map.get(declaretype).toArray(new String[map.get(declaretype).size()]));
			DataFormatter formatter = new DataFormatter("IITR_FMT_TW_2018");
			formatter.setiYear(iYear);
			formatter.getRefsMap().putAll(refList);
			formatter.getRefsMap().put("APPLYCOUNT", applyCount);
			formatter.getRefsMap().put("APPLYREASON", applyReason == null ? "" : applyReason);
			formatter.getRefsMap().put("GRANTTYPE", grantType);
			formatter.getRefsMap().put("FORMAT", applyFormats[0]);
			formatter.getRefsMap().put("TEMPTABLENAME", tempTableName);
			formatter.getRefsMap().put("VATNUMBER", vatNumber);
			formatter.getRefsMap().put("COMLINKMAN", comLinkMan);
			formatter.getRefsMap().put("COMTELNO", comLinkTel);
			formatter.getRefsMap().put("COMEMAIL", comLinkEmail);
			formatter.setiYear(iYear);
			String[] rtn = formatter.getData();
			declaretypelists.add(rtn);
		}
		List<String> rtnlist = new ArrayList<String>();
		for(int i =0 ; i<declaretypelists.size(); i++){
			if(declaretypelists.get(i).length >1){
				if(i == 0){
					for(int j=0; j<declaretypelists.get(i).length-1; j++){
						rtnlist.add((declaretypelists.get(i))[j]);
					}
				}else {
					for(int j=1; j<declaretypelists.get(i).length-1; j++){
						rtnlist.add((declaretypelists.get(i))[j]);
					}	
				}
			}
		}
		return rtnlist.toArray(new String[0]);
	}
	
	public String[] getIITXTextReport(String[] dataPKs, int iYear, String applyFormat, String applyCount,
			String applyReason, String vatNumber, String grantType, String comLinkMan, String comLinkTel,
			String comLinkEmail) throws BusinessException {
		Map<String, Object> refList = getRefListByVatNumber(vatNumber);
		InSQLCreator creator = new InSQLCreator();
		String tempTableName = creator.getInSQL(dataPKs);
		DataFormatter formatter = new DataFormatter("IITR_FMT_TW_2018");
		formatter.setiYear(iYear);
		formatter.getRefsMap().putAll(refList);
		formatter.getRefsMap().put("APPLYCOUNT", applyCount);
		formatter.getRefsMap().put("APPLYREASON", applyReason == null ? "" : applyReason);
		formatter.getRefsMap().put("GRANTTYPE", grantType);
		formatter.getRefsMap().put("FORMAT", applyFormat);
		formatter.getRefsMap().put("TEMPTABLENAME", tempTableName);
		formatter.getRefsMap().put("VATNUMBER", vatNumber);
		formatter.getRefsMap().put("COMLINKMAN", comLinkMan);
		formatter.getRefsMap().put("COMTELNO", comLinkTel);
		formatter.getRefsMap().put("COMEMAIL", comLinkEmail);
		formatter.setiYear(iYear);
		String[] rtn = formatter.getData();
		return rtn;
	}
	
	// �����y��ȡ����ֵ������ֵӛ䛽M���Զ��x�Ŀ���a����ͬ�M�����ܶ��x��ͬ��
	// COUNTYDS �h�Єe
	// DEPTDS �C�P�e
	// VATNUMBERDS ���yһ��̖
	// COMNAMEDS ����λ���Q
	// COMADDRESSDS ����λ��ַ
	// COMPRINCIPALDS �۶��x�������Q
	// COMTAXNODS ����λ������̖
	// COMISHQDS ����֧�C���]ӛ
	// COMINSTOCKDS ���й�˾�]ӛ
	// COMISBANKDS ���ڙC���]ӛ
	// COMISAGENTDS ���������]ӛ
	// COMHOUSETAXNODS ���ݶ�����̖
	private Map<String, Object> getRefListByVatNumber(String vatNumber) throws BusinessException {
		Map<String, Object> ret = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(vatNumber)) {
			BaseDAO baseDao = new BaseDAO();
			List<DefdocVO> vo = (List<DefdocVO>) baseDao
					.retrieveByClause(
							DefdocVO.class,
							"pk_defdoc = (select bd_defdoc.pid from bd_defdoc inner join bd_defdoclist on bd_defdoc.pk_defdoclist=bd_defdoclist.pk_defdoclist where bd_defdoc.code = '"
									+ vatNumber + "' and bd_defdoclist.code='TWHR014')");

			if (vo.get(0).getCode().contains("(FUWEI)")) {
				// ��ί
				// TWHRLORG13 �h�Єe(��ί)
				ret.put("COUNTYDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG13"));
				// TWHRLORG14 �C�P�e(��ί)
				ret.put("DEPTDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG14"));
				// TWHRLORG15 ���yһ��̖(��ί)
				ret.put("VATNUMBERDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG15"));
				// TWHRLORG16 ����λ���Q(��ί)
				ret.put("COMNAMEDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG16"));
				// TWHRLORG17 ����λ��ַ(��ί)
				ret.put("COMADDRESSDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG17"));
				// TWHRLORG18 �۶��x�������Q(��ί)
				ret.put("COMPRINCIPALDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG18"));
				// TWHRLORG19 ����λ������̖(��ί)
				ret.put("COMTAXNODS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG19"));
				// TWHRLORG20 ����֧�C���]ӛ(��ί)
				ret.put("COMISHQDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG20"));
				// TWHRLORG21 ���й�˾�]ӛ(��ί)
				ret.put("COMINSTOCKDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG21"));
				// TWHRLORG22 ���ڙC���]ӛ(��ί)
				ret.put("COMISBANKDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG22"));
				// TWHRLORG23 ���������]ӛ(��ί)
				ret.put("COMISAGENTDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG23"));
				// TWHRLORG24 ���ݶ�����̖(��ί)
				ret.put("COMHOUSETAXNODS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG24"));
			} else {
				// ��˾
				// TWHRLORG01 �h�Єe
				ret.put("COUNTYDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG01"));
				// TWHRLORG02 �C�P�e
				ret.put("DEPTDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG02"));
				// TWHRLORG03 ���yһ��̖
				ret.put("VATNUMBERDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG03"));
				// TWHRLORG04 ����λ���Q
				ret.put("COMNAMEDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG04"));
				// TWHRLORG05 ����λ��ַ
				ret.put("COMADDRESSDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG05"));
				// TWHRLORG06 �۶��x�������Q
				ret.put("COMPRINCIPALDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG06"));
				// TWHRLORG07 ����λ������̖
				ret.put("COMTAXNODS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG07"));
				// TWHRLORG08 ����֧�C���]ӛ
				ret.put("COMISHQDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG08"));
				// TWHRLORG09 ���й�˾�]ӛ
				ret.put("COMINSTOCKDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG09"));
				// TWHRLORG10 ���ڙC���]ӛ
				ret.put("COMISBANKDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG10"));
				// TWHRLORG11 ���������]ӛ
				ret.put("COMISAGENTDS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG11"));
				// TWHRLORG12 ���ݶ�����̖
				ret.put("COMHOUSETAXNODS", SysInitQuery.getParaString("GLOBLE00000000000000", "TWHRLORG12"));
			}
		}
		return ret;
	}

	@Override
	public void writeBackFlags(String[] dataPKs) throws BusinessException {
		InSQLCreator creator = new InSQLCreator();
		String tempTableName = creator.getInSQL(dataPKs);

		BaseDAO baseDao = new BaseDAO();
		String strSQL = "update hrwa_sumincometax set isdeclare='Y', dr=0 where pk_sumincometax in (" + tempTableName
				+ ");";
		baseDao.executeUpdate(strSQL);
		strSQL = "update hrwa_incometaxdetail set isdeclare='Y', dr=0 where pk_sumincometax in (" + tempTableName
				+ ");";
		baseDao.executeUpdate(strSQL);
	}

	public String[] getBankReportText(String pk_org, String offerPeriod, String pk_wa_class) throws BusinessException {
		DataFormatter formatter = new DataFormatter("FUBON_FMT_TW_2017");
		formatter.setPk_org(pk_org);
		formatter.setiYear(Integer.valueOf(offerPeriod.substring(0, 4)));
		formatter.setStartPeriod(offerPeriod);
		formatter.setClassid(pk_wa_class);
		return formatter.getData();
	}

	@Override
	public String getOrgVATNumber(String pk_org) throws BusinessException {
		String strSQL = "SELECT def.code FROM org_hrorg org inner join bd_defdoc def on org.glbdef30=def.pk_defdoc WHERE org.pk_hrorg='"
				+ pk_org + "'";

		BaseDAO dao = new BaseDAO();
		String vatNo = (String) dao.executeQuery(strSQL, new ColumnProcessor());

		if (vatNo != null) {
			return vatNo;
		} else {
			return "";
		}
	}

	@Override
	public String[] getAllOrgVATNumber() throws BusinessException {
		String strSQL = "SELECT DISTINCT glbdef30 FROM org_hrorg WHERE glbdef30 IS NOT NULL ORDER BY glbdef30";

		BaseDAO dao = new BaseDAO();
		List vatNos = (List) dao.executeQuery(strSQL, new ColumnListProcessor());

		if (vatNos != null && vatNos.size() > 0) {
			List<String> retVals = new ArrayList<String>();
			for (int i = 0; i < vatNos.size(); i++) {
				retVals.add((String) vatNos.get(i));
			}
			return retVals.toArray(new String[0]);
		} else {
			return null;
		}
	}

	@Override
	public String[] getAllOrgByVATNumber(String strVATNumber) throws BusinessException {
		String strSQL = "SELECT DISTINCT pk_hrorg FROM org_hrorg org inner join bd_defdoc def on org.glbdef30 = def.pk_defdoc WHERE def.code='"
				+ strVATNumber + "'";

		BaseDAO dao = new BaseDAO();
		List vatNos = (List) dao.executeQuery(strSQL, new ColumnListProcessor());

		if (vatNos != null && vatNos.size() > 0) {
			List<String> retVals = new ArrayList<String>();
			for (int i = 0; i < vatNos.size(); i++) {
				retVals.add((String) vatNos.get(i));
			}
			return retVals.toArray(new String[0]);
		} else {
			return null;
		}
	}

	@Override
	public int checkPeriodWaDataExists(String pk_org, String[] wa_classes, String startPeriod, String endPeriod)
			throws BusinessException {
		int foundCount = 0;
		if (wa_classes != null && wa_classes.length > 0) {
			String clsString = "";
			for (String wa_class : wa_classes) {
				if (clsString.equals("")) {
					clsString = "'" + wa_class + "'";
				} else {
					clsString += ", '" + wa_class + "'";
				}
			}

			String refvalue = SysInitQuery.getParaString(pk_org, "TWHR07");

			String checkStr = "";

			if ("�l������".equals(refvalue)) {
				checkStr = "LEFT(stat.cpaydate, 4) + RIGHT(LEFT(stat.cpaydate, 7), 2)";
			} else if ("н�Y���g".equals(refvalue)) {
				checkStr = "data.cyearperiod";
			} else {
				return 0;
			}

			String strSQL = " SELECT DISTINCT ";
			strSQL += "        data.pk_org , ";
			strSQL += "        data.pk_wa_class ";
			strSQL += "FROM    wa_data data ";
			strSQL += "        INNER JOIN wa_periodstate stat ON data.pk_wa_class = stat.pk_wa_class ";
			strSQL += "WHERE   data.pk_wa_class IN ( " + clsString + " ) ";
			strSQL += "        AND stat.isapproved = 'Y' ";
			strSQL += "        AND (" + checkStr + ") BETWEEN '" + startPeriod + "' ";
			strSQL += "                    AND     '" + endPeriod + "' ";

			BaseDAO basDAO = new BaseDAO();
			List<Map> results = (List<Map>) basDAO.executeQuery(strSQL, new MapListProcessor());

			for (Map result : results) {
				for (int i = 0; i < wa_classes.length; i++) {
					if (wa_classes[i].equals(result.get("pk_wa_class"))) {
						foundCount++;
						wa_classes[i] = "";
						break;
					}
				}
			}

			clsString = "";
			// ��ԃн�Y���������c����н�Y����������Ŀ���ȕr��ĳн�Y�����o����
			// ���췽�����e��
			if (foundCount != 0 && foundCount != wa_classes.length) {
				for (String wa_class : wa_classes) {
					if (!StringUtils.isEmpty(wa_class)) {
						if (clsString.equals("")) {
							clsString = "'" + wa_class + "'";
						} else {
							clsString += ", '" + wa_class + "'";
						}
					}
				}

				strSQL = "SELECT hrorg.pk_hrorg, hrorg.code AS orgcode, cls.pk_wa_class, cls.code AS classcode "
						+ " FROM wa_waclass cls " + " INNER JOIN org_hrorg hrorg ON hrorg.pk_hrorg = cls.pk_org "
						+ " WHERE cls.pk_wa_class IN (" + clsString + " )";

				results = (List<Map>) basDAO.executeQuery(strSQL, new MapListProcessor());
				List<String> pk_hrorgs = new ArrayList<String>();
				String strMessage = "";
				for (Map result : results) {
					pk_hrorgs.add((String) result.get("pk_hrorg"));

					// ȡн�Y����VO
					WaClassVO classVO = (WaClassVO) basDAO.retrieveByPK(WaClassVO.class,
							(String) result.get("pk_wa_class"));
					// ȡн�Y�������Z���Q
					String strName = MultiLangUtil.getSuperVONameOfCurrentLang(classVO, "name", classVO.getName());
					strMessage += (String) result.get("pk_hrorg") + " [" + (String) result.get("orgcode") + "]: "
							+ strName + " [" + result.get("classcode") + "]\r\n";
				}

				// ȡ�����YԴ�M��VO
				IOrgPubQryService orgsrv = NCLocator.getInstance().lookup(IOrgPubQryService.class);
				List<HROrgVO> orgVOs = orgsrv.getVOListByClause(HROrgVO.class, "pk_hrorg", pk_hrorgs, null);

				if (orgVOs != null && orgVOs.size() > 0) {
					for (HROrgVO orgvo : orgVOs) {
						// ȡ�����YԴ�M�����Z���Q
						String strName = MultiLangUtil.getSuperVONameOfCurrentLang(orgvo, "name", orgvo.getName());
						strMessage = strMessage.replace(orgvo.getPk_hrorg(), strName);
					}

					throw new BusinessException(strMessage);
				}
			}
		}

		return foundCount;
	}

	@Override
	public void checkExportOrg(String[] pk_orgs) throws BusinessException {
		String orgString = "";
		for (String pk_org : pk_orgs) {
			if (orgString.equals("")) {
				orgString = "'" + pk_org + "'";
			} else {
				orgString += ", '" + pk_org + "'";
			}
		}

		BaseDAO baseDAO = new BaseDAO();
		List<Map> results = (List<Map>) baseDAO.executeQuery("SELECT * FROM org_hrorg WHERE pk_hrorg IN (" + orgString
				+ ")", new MapListProcessor());

		if (results != null) {
			for (Map result : results) {
				if (result.get("glbdef27") == null) {
					throw new BusinessException(ResHelper.getString("twhr_datainterface", "DataInterface-00076")
							+ result.get("name") + "[" + result.get("code") + "] "
							+ ResHelper.getString("twhr_datainterface", "DataInterface-00077"));
				}
			}
		} else {
			throw new BusinessException(ResHelper.getString("twhr_datainterface", "DataInterface-00077"));
		}
	}

	@Override
	public String[] getIITXTInfo(String vatNumber,  String grantType, String declareReason)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String[] rtn = new String[3];

		// ����vatNumberȡ�y��
		if (!StringUtils.isEmpty(vatNumber)) {
			DefdocVO vo = (DefdocVO) dao.retrieveByPK(DefdocVO.class, vatNumber);
			if (vo != null) {
				rtn[0] = vo.getCode();
			}
		}
		
		// ����grantTypeȡ�����l��;�̖
		if (!StringUtils.isEmpty(grantType)) {
			DefdocVO vo = (DefdocVO) dao.retrieveByPK(DefdocVO.class, grantType);
			if (vo != null) {
				rtn[1] = vo.getCode();
			}
		}

		// ����declareReasonȡ���}���ԭ��̖
		if (!StringUtils.isEmpty(declareReason)) {
			DefdocVO vo = (DefdocVO) dao.retrieveByPK(DefdocVO.class, declareReason);
			if (vo != null) {
				rtn[2] = vo.getCode();
			} else {
				rtn[2] = "";
			}
		}

		// [�yһ��̖��������Code����l���Code�����}���ԭ��Code]
		return rtn;
	}

	@Override
	public String[] getDeclaretype(String[] declaretypes) 
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String[] declaretypepks = new String[declaretypes.length];
		// ����declareTypeȡ�����;�̖
		if (declaretypes.length>0) {
			int i = 0;
			for(String declareType : declaretypes){
					DefdocVO vo = (DefdocVO) dao.retrieveByPK(DefdocVO.class, declareType);
					if (vo != null) {
						declaretypepks[i] = vo.getCode();
						i++;
					}
				}
			}
		return declaretypepks;
	}

	
}