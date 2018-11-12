package nc.pubitf.twhr.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.InSQLCreator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.md.model.MetaDataException;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.logging.Debug;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 *
 * ������֯������,���𷽷��漰��������InSQLCreateֻ�ܺ�̨ʹ��,����ǰ���ͨ��.
 *
 * @author Ares.Tank
 * @date 2018-9-18 10:47:47
 */
public class LegalOrgUtilsEX {
	// ��֯��Ϣ   ���߳��Ż� TODO
	private static Map<String, OrgDTO> orgMap;
	//н��ί�й�ϵpk
	private final static String SALARY_RELATION_KEY  = "SALARY00000000000000";


	/**
	 * !!!!!!!!!!�˷���ֻ�����ں�̨!!!!!!!!!!!!
	 * ��ȡ��Щ���Ƕ��ķ�����֯
	 *
	 * @param psndoc
	 * @return Map<psndoc,pk_org>
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getLegalOrgByPsndoc(String[] psndocs) {
		if (null == psndocs || psndocs.length <= 0) {
			return new HashMap<>();
		}
		InSQLCreator isc = new InSQLCreator();
		String psndocSql;
		try {
			psndocSql = isc.getInSQL(psndocs);
		} catch (BusinessException e1) {
			Debug.debug(e1.getMessage());
			return new HashMap<>();
		}
		String sqlStr = "select bd_psndoc.pk_psndoc,bd_psndoc.pk_org " + "from bd_psndoc " + "WHERE pk_psndoc in ("
				+ psndocSql + ") ";

		List<Map<String, String>> pkOrgMapList;
		try {
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());

			pkOrgMapList = (List<Map<String, String>>) iUAPQueryBS.executeQuery(sqlStr, new MapListProcessor());
		} catch (BusinessException e) {
			Debug.debug(e.getMessage());
			return new HashMap<>();
		}
		if (pkOrgMapList != null && pkOrgMapList.size() > 0) {
			// ���е�Ҫ��ѯ����֯
			Set<String> orgSet = new HashSet<>();
			// map<psndoc,pk_org>
			Map<String, String> resultMap = new HashMap<>();

			for (Map<String, String> psnOrgMap : pkOrgMapList) {
				if (psnOrgMap != null && psnOrgMap.get("pk_psndoc") != null && psnOrgMap.get("pk_org") != null) {
					resultMap.put(psnOrgMap.get("pk_psndoc"), psnOrgMap.get("pk_org"));
					orgSet.add(psnOrgMap.get("pk_org"));
				}
			}
			// ��ѯ��֯��Ӧ�ķ�����֯ map<��֯,������֯>
			Map<String, String> orgToLegalOrgMap = getLegalOrgByOrgs(orgSet.toArray(new String[0]));
			if (orgToLegalOrgMap != null && orgToLegalOrgMap.size() > 0) {
				for (Map.Entry<String, String> entry : resultMap.entrySet()) {
					// ��������֯����Ա��Ӧ
					resultMap.put(entry.getKey(), orgToLegalOrgMap.get(entry.getValue()));
				}
			}
			return resultMap;
		}
		return new HashMap<>();

	}

	/**
	 * ��ȡ����֯�ķ�����֯,��������Ƿ�����֯,��ô�ͷ��ر���, �������,��ô�����ϼ�,���ع�ϵ����ķ�����֯
	 *
	 * @param pkOrg
	 * @return<��֯pk,������֯pk>
	 */

	public static Map<String, String> getLegalOrgByOrgs(String[] pkOrgs) {
		Map<String, String> resultMap = new HashMap<>();
		if (null == pkOrgs || pkOrgs.length <= 0) {
			return resultMap;
		}

		// ��ȥ��,��������ûʲô����
		Set<String> pkOrgsSet = new HashSet<>(Arrays.asList(pkOrgs));
		// ��ʼ����֯��Ϣ
		initOrgInfo();
		// ������֯�ķ�����֯
		for (String pkOrg : pkOrgsSet) {
			resultMap.put(pkOrg, findLegalOrg(pkOrg));
		}

		return resultMap;
	}

	/**
	 * ��ȡ������Դ��֯�µ����з�����֯
	 *
	 * @param pkOrgs ������Դ��֯
	 * @param pkGoup
	 *            ��ǰ��¼�û��������ļ��� ����Ȩ�޵�У��
	 * @return <pk_hrorg>
	 */
	@SuppressWarnings("unchecked")
	public static Set<String> getOrgsByLegal(String pkOrg, String pkGroup) {
		//������֤����������hr��֯,���Ƿ������еķ�����֯
		return getOrgsByLegal(pkOrg);
	}
	/**
	 * ��ȡ������Դ��֯�µ����з�����֯
	 *
	 * @param pkOrgs ������Դ��֯

	 * @return <pk_hrorg>
	 */
	@SuppressWarnings("unchecked")
	public static Set<String> getOrgsByLegal(String pkOrg) {
		Set<String> resultSet = new HashSet<>();
		if (null == pkOrg) {
			return resultSet;
		}
		// �Ȳ�ѯ��ǰ�û����в鿴��֯��Ȩ��
		List<Map<String, String>> mapList;


		// ��ѯ������Դ��֯�µ����з�����֯��Ϣ
		String sqlStr = "select pk_org from org_orgs " + " where innercode like (select innercode from org_orgs "
				+ " where org_orgs.pk_org = '" + pkOrg + "') || '%' " + " and dr = 0 and org_orgs.orgtype2 = 'Y' ";

		try {
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
			mapList = (List<Map<String, String>>) iUAPQueryBS.executeQuery(sqlStr, new MapListProcessor());
		} catch (BusinessException e) {
			Debug.debug(e.getMessage());
			mapList = new ArrayList<>();
		}
		if (null != mapList && mapList.size() > 0) {
			//
			orgMap = new HashMap<>();

			for (Map<String, String> map : mapList) {
				if (null != map.get("pk_org")) {
					resultSet.add(map.get("pk_org"));
				}
			}
		}
		return resultSet;
	}
	/**
	 * ��ѯ��֯��Ϣ
	 * @param pkOrg
	 * @return
	 * @throws MetaDataException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,OrgVO> getOrgInfo (String[] pkOrg) throws MetaDataException{
		Map<String,OrgVO> resultMap  = new HashMap<>();
		IMDPersistenceQueryService service = MDPersistenceService.lookupPersistenceQueryService();
		List<OrgVO> restList  = (List<OrgVO>)service.queryBillOfVOByPKs(OrgVO.class, pkOrg, false);
		for(OrgVO ov :restList){
			resultMap.put(ov.getPk_org(),ov);
		}
		return resultMap;
	}
	/**
	 * ��ʼ����֯��Ϣ,���ڲ��������֯
	 */
	@SuppressWarnings("unchecked")
	private static void initOrgInfo() {
		String sqlStr = "select org_orgs.pk_org,org_orgs.pk_fatherorg,orgtype2 " + "from org_orgs where dr=0 ";
		List<Map<String, Object>> mapList;
		try {
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
			mapList = (List<Map<String, Object>>) iUAPQueryBS.executeQuery(sqlStr, new MapListProcessor());
		} catch (BusinessException e) {
			Debug.debug(e.getMessage());
			mapList = new ArrayList<>();
		}
		if (null != mapList && mapList.size() > 0) {
			// ��װһ�����ݽ��,������ѯ��Ϣ,<pk_org,orgdto>
			orgMap = new HashMap<>();

			for (Map<String, Object> map : mapList) {
				if (null != map.get("pk_org")) {
					OrgDTO temp = new OrgDTO();
					temp.setPkOrg((String) map.get("pk_org"));
					temp.setPkFatherOrg((String) map.get("pk_fatherorg"));
					temp.setLegalOrg(new UFBoolean((String) map.get("orgtype2")).booleanValue());

					orgMap.put(temp.getPkOrg(), temp);
				}
			}
		}
	}

	/**
	 * ���ϲ��Ҵ���֯�ķ�����֯
	 *
	 * @param pkOrg
	 * @param orgMap
	 * @return ������֯pk
	 */
	private static String findLegalOrg(String pkOrg) {

		if (null == orgMap || null == orgMap.get(pkOrg)) {
			return null;
		}
		// ��ǰ��֯Ϊ������֯�򷵻�
		if (orgMap.get(pkOrg).isLegalOrg()) {
			return pkOrg;
		}
		// �����ǰ��֯û�и���֯,���Ҳ��Ƿ�����֯,˵��û�������ķ�����֯
		if (null == orgMap.get(pkOrg).getPkFatherOrg()) {
			return null;
		}
		// ��������ϼ���֯
		return findLegalOrg(orgMap.get(pkOrg).getPkFatherOrg());
	}
	/**
	 * ����н��ί�и���������Դ��֯����֯,�����ֲ㼶ί������,���᷵�����е��¼�����֯
	 * @param pkOrg
	 * @return
	 * @author Ares.Tank
	 * @date 2018��10��1�� ����7:43:44
	 * @description
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getRelationOrgWithSalary(String pk_hrorg){
		Set<String> result = new HashSet<>();
		//��ѯ���е���֯��ϵ
		String sqlStr = " select hro.pk_org as sub_org ,hro.pk_hrorg as fa_hrorg from hr_relation_org hro "
				+ " where hro.business_type='" + SALARY_RELATION_KEY
				+ "' and dr = 0 ";
		List<Map<String,String>> dataMapList;
		try {
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
			dataMapList = (List<Map<String,String>>) iUAPQueryBS.executeQuery(sqlStr, new MapListProcessor());
		} catch (BusinessException e) {
			Debug.debug(e.getMessage());
			dataMapList = new ArrayList<>();
		}
		//Map<pk_hrOrg,Set<org>> <�ϼ�ί����֯,Set<��ί�е���֯>>
		Map<String,Set<String>> orgMap4Loop = new HashMap<>();
		if (null != dataMapList && dataMapList.size() > 0) {
			//Map<pk_hrOrg,set<pk_org>>
			for(Map<String,String> tempMap : dataMapList){
				if(null != tempMap && null != tempMap.get("sub_org") && null != tempMap.get("fa_hrorg")){
					if(!orgMap4Loop.containsKey(tempMap.get("fa_hrorg"))){
						//�����ھ�����set������
						Set<String> tempSet  = new HashSet<>();
						tempSet.add(tempMap.get("sub_org"));
						orgMap4Loop.put(tempMap.get("fa_hrorg"), tempSet);
					}else{
						//�Ѿ�������ֱ������
						orgMap4Loop.get(tempMap.get("fa_hrorg")).add(tempMap.get("sub_org"));
					}

				}
			}
			//���еݹ��ѯ��֯��н��ί�й�ϵ
			recursionSearchRelationOrg(orgMap4Loop,result,pk_hrorg);
		}
		//���뱾��
		result.add(pk_hrorg);
		return new ArrayList<>(result);
	}
	/**
	 * !!!! ֻ�����ں�̨����  !!!!
	 * ����н��ί�и���������Դ��֯����֯;
	 * �����ֲ㼶ί������,���᷵�����е��¼�����֯;
	 * ���¼���������֯ͳһ��һ��������֯����,���ѷ�����֯һͬ���� --�����ͽ����ļ���
	 * @param pkOrg hr��֯
	 * @return
	 * @author Ares.Tank
	 * @date 2018��10��16��15:29:13
	 * @description
	 */
	public static List<String> getRelationOrgWithSalary4NHI(String pk_hrorg){
		//��ȡί�еĸ���������Դ����֯
		List<String> relationOrgList = getRelationOrgWithSalary(pk_hrorg);

		if(null == relationOrgList || relationOrgList.size() <= 0){
			return relationOrgList;
		}

		//������Щ��֯,�ҳ���Щ��֯�����з�����֯ 
		Set<String> allOrgSet = new HashSet<>(relationOrgList);
		String orgInSQL = null;
		try {
			InSQLCreator insql = new InSQLCreator();
			orgInSQL = insql.getInSQL(relationOrgList.toArray(new String[0]));
			if(null == orgInSQL){
				return relationOrgList;
			}
			String sqlStr = "select pk_corp from org_orgs where pk_org in ("+orgInSQL+")";
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
			List<Map<String,String>> qryData = (List<Map<String,String>>) iUAPQueryBS.executeQuery(sqlStr, new MapListProcessor());
			for(Map<String,String> rowMap : qryData){
				if(null != rowMap && rowMap.size() > 0 && null != rowMap.get("pk_corp")){
					allOrgSet.add(rowMap.get("pk_corp"));
				}
			}
			
		} catch (BusinessException e) {
			Debug.debug(e.getMessage());
			e.printStackTrace();
		}
		return new ArrayList<>(allOrgSet);
	}
	/**
	 * ���еݹ��ѯ��֯��н��ί�й�ϵ
	 * @param orgMap4Loop <pk_hrOrg,Set<org>> <�ϼ�ί����֯,Set<��ί�е���֯>>
	 * @param result <�¼���н��ί����֯>
	 * @param ��Ҫ���ҵ�������Դ��֯
	 * @author Ares.Tank
	 * @date 2018��10��1�� ����9:22:18
	 * @description
	 */
	private static void recursionSearchRelationOrg(Map<String, Set<String>> orgMap4Loop, Set<String> result,
			String pk_hrorg) {
		//���¼��ǲŽ��в���(������Դ��֯�����¼�)
		if(orgMap4Loop.containsKey(pk_hrorg)){
			for(String pkOrg : orgMap4Loop.get(pk_hrorg)){
				//���Ա������еݹ�
				if(pkOrg.equals(pk_hrorg)){
					continue;
				}
				//������֯
				result.add(pkOrg);
				//�����¼���û�п����ӵ�
				recursionSearchRelationOrg(orgMap4Loop,result,pkOrg);
			}
		}
	}
}

// ��ʱ��װһ�µݹ���������ݽṹ
class OrgDTO {
	// ��֯pk
	private String pkOrg;
	// ����֯��pk
	private String pkFatherOrg;
	// �Ƿ��Ƿ�����֯
	private boolean isLegalOrg;

	public String getPkOrg() {
		return pkOrg;
	}

	public void setPkOrg(String pkOrg) {
		this.pkOrg = pkOrg;
	}

	public String getPkFatherOrg() {
		return pkFatherOrg;
	}

	public void setPkFatherOrg(String pkFatherOrg) {
		this.pkFatherOrg = pkFatherOrg;
	}

	public boolean isLegalOrg() {
		return isLegalOrg;
	}

	public void setLegalOrg(boolean isLegalOrg) {
		this.isLegalOrg = isLegalOrg;
	}

}