package nc.impl.hi.psndoc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.naming.NamingException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import nc.bs.businessevent.BusinessEvent;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.businessevent.bd.BDCommonEventUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.sec.esapi.NCESAPI;
import nc.bs.uap.oid.OidGenerator;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.hr.frame.persistence.IValidatorFactory;
import nc.hr.frame.persistence.PersistenceDAO;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.IDValidateUtil;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.hr.utils.TimerLogger;
import nc.impl.pubapp.plugin.PluginExecutor;
import nc.itf.bd.pub.IBDMetaDataIDConst;
import nc.itf.hi.IPersonRecordService;
import nc.itf.hi.IPsndocQryService;
import nc.itf.hr.frame.IHrBillCode;
import nc.itf.hr.frame.IPersistenceHome;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.hr.frame.PersistenceDbException;
import nc.itf.org.IOrgConst;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.jdbc.framework.DataSourceCenter;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.md.data.access.NCObject;
import nc.md.model.MetaDataException;
import nc.md.persist.designer.vo.ClassVO;
import nc.md.persist.framework.MDPersistenceService;
import nc.plugin.hi.IPsndocIntoDoc;
import nc.plugin.hi.PsnIntoDocPluginExecDelegate;
import nc.pub.billcode.itf.IBillcodeManage;
import nc.pub.billcode.vo.BillCodeContext;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.pub.tools.HiCacheUtils;
import nc.pub.tools.HiSQLHelper;
import nc.pub.tools.VOUtils;
import nc.pub.xml.utils.XmlMatchUtils;
import nc.ui.hr.pub.FromWhereSQL;
import nc.ui.pub.bill.IBillItem;
import nc.vo.bd.psnid.PsnIdtypeVO;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.bd.ref.RefInfoVO;
import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.KeyPsnGrpVO;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnBudgetVO;
import nc.vo.hi.psndoc.PsnChgVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.WorkVO;
import nc.vo.hi.psndoc.enumeration.PsnType;
import nc.vo.hi.psndoc.enumeration.TrnseventEnum;
import nc.vo.hi.pub.BillCodeRepeatBusinessException;
import nc.vo.hi.pub.HICommonValue;
import nc.vo.hi.pub.HiBatchEventValueObject;
import nc.vo.hi.pub.HiEventValueObject;
import nc.vo.hi.pub.IHiEventType;
import nc.vo.hr.formulaset.FormuaDateHelper;
import nc.vo.hr.infoset.InfoItemMapVO;
import nc.vo.hr.infoset.InfoItemVO;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hr.tools.pub.GeneralVOProcessor;
import nc.vo.om.hrdept.HRDeptVO;
import nc.vo.om.orginfo.HROrgVO;
import nc.vo.om.pub.JFCommonValue;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.org.PostVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pub.lang.UFTime;
import nc.vo.sm.UserVO;
import nc.vo.uap.rbac.constant.INCSystemUserConst;
import nc.vo.uif2.LoginContext;
import nc.vo.util.AuditInfoUtil;
import nc.vo.util.BDReferenceChecker;
import nc.vo.util.BDVersionValidationUtil;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/***************************************************************************
 * @author dusx, Rocex Wang
 **************************************************************************/
public class PsndocDAO extends SimpleDocServiceTemplate {

	private final BaseDAO baseDAOManager = new BaseDAO();

	private final Stack<String> stackOid = new Stack<String>();

	private IPersistenceRetrieve retrieve = null;

	private IPersonRecordService psnrdsService = null;

	/***************************************************************************
	 * Created on 2010-1-30 14:56:50<br>
	 * 
	 * @author Rocex Wang
	 ***************************************************************************/
	public PsndocDAO() {
		super("PsndocDAO");
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-4-13 10:53:00<br>
	 * 
	 * @param superVO
	 * @param strFieldCode
	 * @param strPk_psnjob
	 * @param aggs
	 * @author Rocex Wang
	 * @return PsndocAggVO[]
	 * @throws BusinessException
	 ***************************************************************************/
	public PsndocAggVO[] batchUpdatePsndoc(SuperVO superVO,
			String strFieldCode, String[] strPk_psnjob, String[] strPk_psnorgs,
			String[] strPk_psndocs, String[] pk_sub) throws BusinessException {
		// ǰ�¼�����
		EventDispatcher.fireEvent(new BusinessEvent(HICommonValue.MD_ID_PSNDOC,
				IHiEventType.BATCH_UPDATE_EMPLOYEE_BEFORE, strPk_psndocs));

		String tableName = superVO.getTableName();
		InSQLCreator ttu = null;
		try {
			ttu = new InSQLCreator();
			String inSql = ttu.getInSQL(pk_sub);

			String updateSQL = " update " + tableName + " set " + strFieldCode
					+ " = ? where " + superVO.getPKFieldName() + " in ( "
					+ inSql + " ) ";
			SQLParameter para = new SQLParameter();
			Object objAttrValue = superVO.getAttributeValue(strFieldCode);
			if (objAttrValue == null) {
				para.addNullParam(Types.NULL);
			} else {
				para.addParam(objAttrValue);
			}
			baseDAOManager.executeUpdate(updateSQL, para);

			// �ӱ�ͬ������
			batchSubSynPsndoc(strPk_psndocs, tableName, strFieldCode, superVO);

			// ͬ������,�����޸�ʱֻ���޸�PsndocVO
			HiCacheUtils.synCache(PsndocVO.getDefaultTableName(),
					PsnJobVO.getDefaultTableName(),
					PsnOrgVO.getDefaultTableName());

			PsndocAggVO[] psndocAggVOs = queryPsndocVOByPks(true, strPk_psnjob);
			// ���¼�����
			EventDispatcher.fireEvent(new BusinessEvent(
					HICommonValue.MD_ID_PSNDOC,
					IHiEventType.BATCH_UPDATE_EMPLOYEE_AFTER, strPk_psndocs));
			HiEventValueObject
					.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
			return psndocAggVOs;
		} finally {
			if (ttu != null) {
				ttu.clear();
			}
		}
	}

	public PsndocAggVO[] batchUpdatePsndocMain(SuperVO superVO,
			String strFieldCode, String[] strPk_psnjob, String[] strPk_psndocs)
			throws BusinessException {
		// ǰ�¼�����
		EventDispatcher.fireEvent(new BusinessEvent(HICommonValue.MD_ID_PSNDOC,
				IHiEventType.BATCH_UPDATE_EMPLOYEE_BEFORE, strPk_psndocs));

		InSQLCreator ttu = null;
		try {
			ttu = new InSQLCreator();
			String inSql = ttu.getInSQL(strPk_psndocs);

			String updateSQL = " update bd_psndoc set " + strFieldCode
					+ " = ? where pk_psndoc in ( " + inSql + " ) ";
			SQLParameter para = new SQLParameter();
			Object objAttrValue = superVO.getAttributeValue(strFieldCode);
			if (objAttrValue == null) {
				para.addNullParam(Types.NULL);
			} else {
				para.addParam(objAttrValue);
			}
			baseDAOManager.executeUpdate(updateSQL, para);

			// ͬ������,�����޸�ʱֻ���޸�PsndocVO
			HiCacheUtils.synCache(PsndocVO.getDefaultTableName(),
					PsnJobVO.getDefaultTableName(),
					PsnOrgVO.getDefaultTableName());

			PsndocAggVO[] psndocAggVOs = queryPsndocVOByPks(true, strPk_psnjob);
			// ���¼�����
			EventDispatcher.fireEvent(new BusinessEvent(
					HICommonValue.MD_ID_PSNDOC,
					IHiEventType.BATCH_UPDATE_EMPLOYEE_AFTER, strPk_psndocs));
			HiEventValueObject
					.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
			return psndocAggVOs;
		} finally {
			if (ttu != null) {
				ttu.clear();
			}
		}
	}

	/**
	 * @param strPkPsndocs
	 * @param subtablename
	 * @param fieldCode
	 * @param voClass
	 * @throws BusinessException
	 */
	private void batchSubSynPsndoc(String[] strPkPsndocs, String subtablename,
			String fieldCode, SuperVO voClass) throws BusinessException {
		// ҵ���Ӽ���tabCode
		final String[] busiSubset = PsndocAggVO.hashBusinessInfoSet
				.toArray(new String[0]);

		InfoItemVO[] items = (InfoItemVO[]) getRetrieve().retrieveByClause(
				null,
				InfoItemVO.class,
				" pk_infoset in ( select pk_infoset from hr_infoset where table_code = '"
						+ subtablename + "' and pk_infoset_sort = '"
						+ HICommonValue.PSNDOC_INFOSET_SORT_PK
						+ "' ) and item_code = '" + fieldCode
						+ "' and pk_main_item <> '~' ");

		if (items == null || items.length == 0
				|| StringUtils.isBlank(items[0].getPk_main_item())) {
			// ��Ŀ�����ڻ�����Ŀû�ж�Ӧ������Ϣ��,ֱ�ӷ���
			return;
		}

		// ֻ����һ���ֶ� �Ǿ�ֻ����һ�������ֶ�
		InfoItemVO mainitem = (InfoItemVO) getRetrieve().retrieveByPk(null,
				InfoItemVO.class, items[0].getPk_main_item());
		if (mainitem == null) {
			// �������Ӧ������Ϣ��
			return;
		}

		String mainField = mainitem.getItem_code();
		String subField = items[0].getItem_code();

		InSQLCreator isc = null;
		try {
			isc = new InSQLCreator();
			String insql = isc.getInSQL(strPkPsndocs);

			String subWhere = "";

			if (ArrayUtils.contains(busiSubset, subtablename)) {
				subWhere = " pk_psnorg in ( select pk_psnorg from hi_psnorg where pk_psndoc in ("
						+ insql + ") ";
			} else {
				subWhere = " pk_psndoc in (" + insql + ") ";
			}

			if (!StringUtils.isBlank(items[0].getSub_formula_sql())) {
				subWhere = subWhere + " and " + items[0].getSub_formula_sql();
			} else {
				// ���û�ж����ӱ���Ϣ�ʽ,���ѯ����һ��������ֵ
				if (PartTimeVO.getDefaultTableName().equals(subtablename)) {
					// ��ְ
					subWhere = subWhere
							+ " and assgid > 1 and recordnum = 0 order by assgid desc ";
				} else if (PsnJobVO.getDefaultTableName().equals(subtablename)) {
					// ������¼
					subWhere = subWhere + " and assgid = 1 and recordnum = 0 ";
				} else if (ArrayUtils.contains(busiSubset, subtablename)) {
					// ��ְ��ְ���ҵ���Ӽ�
					subWhere = subWhere + " and recordnum = 0 ";
				} else {
					// ��ҵ���Ӽ�
					subWhere = subWhere + "  and recordnum = 0 ";
				}
			}

			GeneralVO[] subValue = (GeneralVO[]) new BaseDAO().executeQuery(
					" select " + subField + ",pk_psndoc from "
							+ voClass.getTableName() + " where  " + subWhere,
					new GeneralVOProcessor<GeneralVO>(GeneralVO.class));

			if (subValue == null) {
				return;
			}

			HashMap<String, Object> hm = new HashMap<String, Object>();
			for (GeneralVO vo : subValue) {
				hm.put((String) vo.getAttributeValue("pk_psndoc"),
						vo.getAttributeValue(subField));
			}

			PsndocAggVO[] agg = queryByCondition_LazyLoad(PsndocAggVO.class,
					" pk_psndoc in (" + insql + ") ");
			ArrayList<PsndocVO> al = new ArrayList<PsndocVO>();
			for (int i = 0; agg != null && i < agg.length; i++) {
				agg[i].getParentVO().setAttributeValue(mainField,
						hm.get(agg[i].getParentVO().getPk_psndoc()));
				al.add(agg[i].getParentVO());
			}
			baseDAOManager.updateVOArray(al.toArray(new PsndocVO[0]),
					new String[] { mainField });
		} finally {
			if (isc != null) {
				isc.clear();
			}
		}

	}

	/***************************************************************************
	 * �����Ա����ȫ��Ψһ<br>
	 * Created on 2010-6-18 13:14:53<br>
	 * 
	 * @param psndocVO
	 * @author Rocex Wang
	 * @throws BusinessException
	 ***************************************************************************/
	public void checkPsnCodeUnique(PsndocVO psndocVO) throws BusinessException {
		String strSQL = PsndocVO.CODE + "='"
				+ NCESAPI.sqlEncode(psndocVO.getCode()) + "'";
		if ((VOStatus.UPDATED == psndocVO.getStatus() || psndocVO
				.getPk_psndoc() != null)) {
			strSQL += " and " + PsndocVO.PK_PSNDOC + "<>'"
					+ psndocVO.getPk_psndoc() + "'";
		}
		int iCount = getRetrieve().getCountByCondition(
				PsndocVO.getDefaultTableName(), strSQL);
		if (iCount > 0) {
			BillCodeRepeatBusinessException ex = new BillCodeRepeatBusinessException(
					ResHelper.getString("6007psn", "06007psn0228")/*
																 * @res
																 * "ȫ���Ѿ�������ͬ��Ա�������Ա��"
																 */,
					"coderepeat");

			// ��ְ�Ǽǽڵ��£����������Ϊǰ�����Ҳ��ɱ༭�������������ظ�ʱ���������½����ϵı��� add by yanglt
			// 20140807
			BillCodeContext billcodeContext = NCLocator
					.getInstance()
					.lookup(IBillcodeManage.class)
					.getBillCodeContext(HICommonValue.NBCR_PSNDOC_CODE,
							PubEnv.getPk_group(), psndocVO.getPk_org());
			if (billcodeContext != null && billcodeContext.isPrecode()
					&& !billcodeContext.isEditable()
					&& StringUtils.isNotBlank(psndocVO.getCode())) {
				String strPsnCode = null;
				NCLocator
						.getInstance()
						.lookup(IBillcodeManage.class)
						.AbandonBillCode_RequiresNew(
								HICommonValue.NBCR_PSNDOC_CODE,
								PubEnv.getPk_group(), psndocVO.getPk_org(),
								psndocVO.getCode());
				strPsnCode = NCLocator
						.getInstance()
						.lookup(IHrBillCode.class)
						.getBillCode(HICommonValue.NBCR_PSNDOC_CODE,
								PubEnv.getPk_group(), psndocVO.getPk_org());
				ex.setNbcrcode(HICommonValue.NBCR_PSNDOC_CODE);
				ex.setNewCode(strPsnCode);
			}

			throw ex;
		}
	}

	/***************************************************************************
	 * ����ʱУ����ԱΨһ��Ϣ
	 * 
	 * @param psndocVO
	 * @throws BusinessException
	 **************************************************************************/
	public void checkPsnUnique(PsndocVO psndocVO) throws BusinessException {
		if (VOStatus.UNCHANGED == psndocVO.getStatus()) {
			return;
		}
		String[] strUniqueFields = (String[]) getPsndocUniqueRule().keySet()
				.toArray(new String[0]);
		// strUniqueFields = (String[]) ArrayUtils.addAll(strUniqueFields, new
		// String[] { PsndocVO.NAME, PsndocVO.IDTYPE, PsndocVO.ID });
		if (!ArrayUtils.contains(strUniqueFields, PsndocVO.IDTYPE)) {
			strUniqueFields = (String[]) ArrayUtils.add(strUniqueFields,
					PsndocVO.IDTYPE);
		}

		if (!ArrayUtils.contains(strUniqueFields, PsndocVO.ID)) {
			strUniqueFields = (String[]) ArrayUtils.add(strUniqueFields,
					PsndocVO.ID);
		}

		if (!ArrayUtils.contains(strUniqueFields, PsndocVO.NAME)) {
			strUniqueFields = (String[]) ArrayUtils.addAll(strUniqueFields,
					new String[] { PsndocVO.NAME, PsndocVO.NAME2,
							PsndocVO.NAME3, PsndocVO.NAME4, PsndocVO.NAME5,
							PsndocVO.NAME6 });
		} else {
			strUniqueFields = (String[]) ArrayUtils.addAll(strUniqueFields,
					new String[] { PsndocVO.NAME2, PsndocVO.NAME3,
							PsndocVO.NAME4, PsndocVO.NAME5, PsndocVO.NAME6 });
		}

		if (strUniqueFields == null || strUniqueFields.length == 0) {
			return;
		}
		PsndocVO newPsndoc = new PsndocVO();
		for (String strField : strUniqueFields) {
			newPsndoc.setAttributeValue(strField,
					psndocVO.getAttributeValue(strField));
		}
		if (psndocVO.getPk_psndoc() != null) {
			newPsndoc.setPk_psndoc(psndocVO.getPk_psndoc());
		}
		PsndocAggVO psndocAggVO = getPsndocVOByUniqueVO(newPsndoc);
		if (psndocAggVO != null && psndocAggVO.getParentVO() != null) {
			PsndocVO head = psndocAggVO.getParentVO();
			Map mapUniqueField = getPsndocUniqueRule();
			if (!mapUniqueField.containsKey(PsndocVO.IDTYPE)) {
				mapUniqueField.put(PsndocVO.IDTYPE,
						ResHelper.getString("6007psn", "06007psn0229")/*
																	 * @ res
																	 * "֤������"
																	 */);
			}
			if (!mapUniqueField.containsKey(PsndocVO.ID)) {
				mapUniqueField.put(PsndocVO.ID,
						ResHelper.getString("6007psn", "06007psn0230")/*
																	 * @ res
																	 * "֤������"
																	 */);
			}
			if (!mapUniqueField.containsKey(PsndocVO.NAME)) {
				mapUniqueField.put(PsndocVO.NAME,
						ResHelper.getString("common", "UC000-0001403")/*
																	 * @ res
																	 * "����"
																	 */);
			}
			String strMsg = "";
			PsnIdtypeVO psnIdtypeVO = (PsnIdtypeVO) getDao().retrieveByPK(
					PsnIdtypeVO.class,
					(String) psndocVO.getAttributeValue(CertVO.IDTYPE));
			for (String strField : strUniqueFields) {
				if (mapUniqueField.get(strField) == null) {
					continue;
				}
				if (CertVO.IDTYPE.equalsIgnoreCase(strField)) {
					strMsg += mapUniqueField.get(strField) + ":"
							+ VOUtils.getNameByVO(psnIdtypeVO) + ",";
				} else if (PsndocVO.NAME.equalsIgnoreCase(strField)) {
					strMsg += mapUniqueField.get(strField) + ":"
							+ VOUtils.getNameByVO(psndocVO) + ",";
				} else {
					String fieldValue = (String) psndocVO
							.getAttributeValue(strField);
					// ������ʾ�е�nullֵ
					fieldValue = StringUtils.isBlank(fieldValue) ? ResHelper
							.getString("6007psn", "06007psn0148")/*
																 * @ res "��"
																 */
					: fieldValue;
					strMsg += mapUniqueField.get(strField) + ":" + fieldValue
							+ ",";
				}
			}
			if (strMsg.length() > 0) {
				strMsg = strMsg.substring(0, strMsg.length() - 1);
			}

			String jobName = VOUtils.getDocName(PostVO.class, head
					.getPsnJobVO().getPk_post());
			jobName = StringUtils.isBlank(jobName) ? ResHelper.getString(
					"6007psn", "06007psn0148")/*
											 * @ res "��"
											 */: jobName;
			String deptName = VOUtils.getDocName(DeptVO.class, head
					.getPsnJobVO().getPk_dept());
			deptName = StringUtils.isBlank(deptName) ? ResHelper.getString(
					"6007psn", "06007psn0148")/*
											 * @ res "��"
											 */: deptName;
			String orgName = VOUtils.getDocName(OrgVO.class, head.getPsnJobVO()
					.getPk_org());
			orgName = StringUtils.isBlank(orgName) ? ResHelper.getString(
					"6007psn", "06007psn0148")/*
											 * @ res "��"
											 */: orgName;
			strMsg = ResHelper.getString("6007psn", "06007psn0231"/*
																 * @res
																 * "�����Ѵ���  {0} ����; ������Ϣ����  ������֯��{1}  ���ڲ��ţ�{2}  ���ڸ�λ: {3}"
																 */, strMsg,
					orgName, deptName, jobName);
			throw new BusinessException(strMsg);
		}
	}

	/****************************************************************************
	 * {@inheritDoc}<br>
	 * Created on 2010-7-20 14:04:48<br>
	 * 
	 * @see nc.hr.frame.persistence.SimpleDocServiceTemplate#delete(java.lang.Object)
	 * @author Rocex Wang
	 ****************************************************************************/
	@Override
	public void delete(Object obj) throws BusinessException {
		final PsndocVO psndocVO = (PsndocVO) getMainVO(obj);
		getLocker().lock(DELETE, psndocVO); // �Ӽ�����������������֯ҳǩ����������
		BDVersionValidationUtil.validateSuperVO(psndocVO);// �汾У�飨ʱ���У�飩
		InfoSetVO[] set = (InfoSetVO[]) NCLocator
				.getInstance()
				.lookup(IPersistenceRetrieve.class)
				.retrieveByClause(
						null,
						InfoSetVO.class,
						" pk_infoset_sort = '"
								+ HICommonValue.PSNDOC_INFOSET_SORT_PK + "' ");
		// NCLocator.getInstance().lookup(IInfoSetQry.class).queryInfoSetBySortPk(HICommonValue.PSNDOC_INFOSET_SORT_PK);
		HashMap<String, String> hm = null;
		if (set != null && set.length > 0) {
			hm = new HashMap<String, String>();
			for (int i = 0; i < set.length; i++) {
				if (hm.get(set[i].getTable_code()) == null) {
					hm.put(set[i].getTable_code(), set[i].getTable_code());
				}
			}
		}

		String[] excludeTables = null;
		if (hm != null) {
			excludeTables = hm.keySet().toArray(new String[0]);
			excludeTables = (String[]) ArrayUtils.add(excludeTables,
					"hr_relation_psn");
		}

		if (isDispatchEvent()) {
			// ɾ��ǰ�¼�
			HiEventValueObject
					.fireEvent(
							psndocVO.getPsnJobVO(),
							null,
							psndocVO.getPsnJobVO().getPk_hrorg(),
							HICommonValue.MD_ID_PSNDOC,
							ObjectUtils.equals(psndocVO.getPsnOrgVO()
									.getPsntype(), PsnType.EMPLOYEE.value()) ? IHiEventType.DELETE_EMPLOYEE_BEFORE
									: IHiEventType.DELETE_POI_BEFORE);
		}

		PsndocAggVO agg = (PsndocAggVO) obj;
		int count = getRetrieve().getCountByCondition(
				PsnOrgVO.getDefaultTableName(),
				" pk_psndoc = '" + agg.getParentVO().getPk_psndoc() + "' ");
		if (count == 1) {

			nc.vo.bd.psn.PsndocVO uapVO = getMDQueryService()
					.queryBillOfVOByPK(nc.vo.bd.psn.PsndocVO.class,
							agg.getParentVO().getPk_psndoc(), false);

			// ɾ��ǰ�¼�
			new BDCommonEventUtil(IBDMetaDataIDConst.PSNDOC)
					.dispatchDeleteBeforeEvent(uapVO);

			// ��ǰ��Աֻ��һ����֯��ϵ,ֱ��ɾ��
			setBdReferenceChecker(BDReferenceChecker.getInstance(excludeTables));
			DefaultValidationService validationService = createValidationService(getBDReferenceChecker(obj));
			createCustomValidators(validationService, IValidatorFactory.DELETE); // �Զ���У����
			validationService.validate(obj);
			// �����Ա������Ϣ,ɾ��
			PsndocAggVO dbAgg = queryByPk(PsndocAggVO.class, agg.getParentVO()
					.getPk_psndoc(), false);
			getMDPersistenceService().deleteBillFromDB(dbAgg); // �h��

			// ɾ�����¼�
			new BDCommonEventUtil(IBDMetaDataIDConst.PSNDOC)
					.dispatchDeleteAfterEvent(uapVO);
		} else if (count > 1) {
			BDReferenceChecker checker = BDReferenceChecker
					.getInstance(excludeTables);
			setBdReferenceChecker(checker);
			DefaultValidationService validationService = createValidationService(checker);
			createCustomValidators(validationService, IValidatorFactory.DELETE); // �Զ���У����

			// ��ǰ��Ա�ֶ�νM���P�S,�h����ǰ�M���P�S�µ����ИI���Ӽ�����;
			PsndocAggVO aggvo = queryByPk(PsndocAggVO.class, agg.getParentVO()
					.getPk_psndoc());
			ArrayList<SuperVO> al = new ArrayList<SuperVO>();
			for (SuperVO vo : aggvo.getAllChildrenVO()) {
				if (PsndocAggVO.listBusinessInfoSetClass
						.contains(vo.getClass())
						&& agg.getParentVO().getPsnOrgVO().getPk_psnorg()
								.equals(vo.getAttributeValue("pk_psnorg"))) {
					al.add(vo);
				}
			}
			// ���Ӽ�����ɾ��
			if (al.size() > 0) {
				SuperVO[] vos = al.toArray(new SuperVO[0]);
				validationService.validate(vos);
				ArrayList<String> table_name = new ArrayList<String>();
				for (int i = 0; i < vos.length; i++) {
					if (!table_name.contains(vos[i].getTableName())) {
						table_name.add(vos[i].getTableName());
					}
				}
				String[] sqls = new String[table_name.size()];
				for (int i = 0; i < sqls.length; i++) {
					sqls[i] = " delete from " + table_name.get(i)
							+ " where pk_psnorg = '"
							+ agg.getParentVO().getPsnOrgVO().getPk_psnorg()
							+ "'";
				}
				NCLocator.getInstance().lookup(IPersistenceUpdate.class)
						.executeSQLs(sqls);
			}
			// ɾ��֮�������֯��ϵ��lastflag
			PsnOrgVO[] orgVO = queryByCondition(PsnOrgVO.class,
					" pk_psndoc = '" + agg.getParentVO().getPk_psndoc()
							+ "' order by orgrelaid desc ");
			for (int i = 0; orgVO != null && i < orgVO.length; i++) {
				if (i == 0) {
					orgVO[i].setAttributeValue("lastflag", UFBoolean.TRUE);
				} else {
					orgVO[i].setAttributeValue("lastflag", UFBoolean.FALSE);
				}
			}
			NCLocator
					.getInstance()
					.lookup(IPersistenceUpdate.class)
					.updateVOArray(null, orgVO, new String[] { "lastflag" },
							null);

			// ͬ������
			for (String tabName : PsndocAggVO.hashBusinessInfoSet) {
				updateDataAfterSubDataChanged(tabName, agg.getParentVO()
						.getPk_psndoc());
			}

			// ͬ��������ְ��֯
			// �õ���Ա����Ա�����͵���֯��ϵ�µ����¹�����¼����
			String pkPsnjob = getPsnrdsService().getEmpPsnjobByPsndoc(
					agg.getParentVO().getPk_psndoc());
			PsnJobVO job = queryByPk(PsnJobVO.class, pkPsnjob);
			if (job != null) {
				getPsnrdsService().synPkorgOfPsndoc(job.getPk_org(),
						agg.getParentVO().getPk_psndoc());
			}
		} else {
			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0232")/*
									 * @ res "��ɾ������Աû����֯��ϵ��¼"
									 */);
		}
		// ͬ������
		HiCacheUtils.synCache(PsndocVO.getDefaultTableName(),
				PsnJobVO.getDefaultTableName(), PsnOrgVO.getDefaultTableName());
		if (isDispatchEvent()) {
			// ɾ�����¼�
			HiEventValueObject
					.fireEvent(
							psndocVO.getPsnJobVO(),
							null,
							psndocVO.getPsnJobVO().getPk_hrorg(),
							HICommonValue.MD_ID_PSNDOC,
							ObjectUtils.equals(psndocVO.getPsnOrgVO()
									.getPsntype(), PsnType.EMPLOYEE.value()) ? IHiEventType.DELETE_EMPLOYEE_AFTER
									: IHiEventType.DELETE_POI_AFTER);
		}
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-5-13 11:25:20<br>
	 * 
	 * @param psndocVO
	 * @throws BusinessException
	 * @author Rocex Wang
	 ***************************************************************************/
	private void fillOrgJobVO(PsndocVO psndocVO) throws BusinessException {
		String strSubCondition = "";
		if (psndocVO.getPsnOrgVO().getPrimaryKey() == null) {
			strSubCondition = "{0}=''{1}'' and {2}=''{3}'' and {4}=''{5}'' order by {6}";
			strSubCondition = MessageFormat.format(strSubCondition,
					PsnOrgVO.PK_PSNDOC, psndocVO.getPrimaryKey(),
					PsnOrgVO.LASTFLAG, UFBoolean.TRUE, PsnOrgVO.ENDFLAG,
					UFBoolean.FALSE, PsnOrgVO.ORGRELAID);
			PsnOrgVO[] psnOrgVOs = queryByCondition(PsnOrgVO.class,
					strSubCondition);
			if (psnOrgVOs != null && psnOrgVOs.length > 0) {
				psndocVO.setPsnOrgVO(psnOrgVOs[0]);
			}
		} else {
			psndocVO.setPsnOrgVO(queryByPk(PsnOrgVO.class, psndocVO
					.getPsnOrgVO().getPrimaryKey()));
		}
		if (psndocVO.getPsnJobVO().getPrimaryKey() == null) {
			strSubCondition = "{0}=''{1}'' and {2}=''{3}'' and {4}=''{5}'' and {6}= ''{7}'' order by {8}";
			strSubCondition = MessageFormat.format(strSubCondition,
					PsnJobVO.PK_PSNDOC, psndocVO.getPrimaryKey(),
					PsnJobVO.ISMAINJOB, UFBoolean.TRUE, PsnJobVO.LASTFLAG,
					UFBoolean.TRUE, PsnJobVO.PK_PSNORG, psndocVO.getPsnOrgVO()
							.getPrimaryKey(), PsnJobVO.ASSGID);
			PsnJobVO[] psnJobVOs = queryByCondition(PsnJobVO.class,
					strSubCondition);
			if (psnJobVOs != null && psnJobVOs.length > 0) {
				psndocVO.setPsnJobVO(psnJobVOs[0]);
			}
		} else {
			psndocVO.setPsnJobVO(queryByPk(PsnJobVO.class, psndocVO
					.getPsnJobVO().getPrimaryKey()));
		}
	}

	private ArrayList<String> getDefaultQueryFields() {
		return VOUtils.getDefaultQueryFields();
	}

	private String getJoinedTable(String[] strTableNames) {
		// ��ѯĳһ����֯��ϵ�е�������ְ����ʹ��֯��ϵ�����ˣ�������ְ��ǲ�Ҫ�������
		String strJoinedTable = " from bd_psndoc inner join hi_psnorg on hi_psnorg.pk_psndoc= bd_psndoc.pk_psndoc"
				+ " inner join hi_psnjob on hi_psnjob.pk_psnorg=hi_psnorg.pk_psnorg and hi_psnjob.lastflag='Y' and hi_psnjob.ismainjob = 'Y'  ";
		if (strTableNames == null || strTableNames.length < 1) {
			return strJoinedTable;
		}
		for (String tablename : strTableNames) {
			if (",bd_psndoc,hi_psnorg,hi_psnjob".indexOf(tablename) > 0) {
				continue;
			}
			if (tablename.equalsIgnoreCase("om_post")) {
				// ��λ����
				strJoinedTable += " left outer join " + tablename + " on "
						+ tablename + ".pk_post=hi_psnjob.pk_post";
			} else if (tablename.equalsIgnoreCase("org_dept")) {
				// ���Ź���
				strJoinedTable += " left outer join " + tablename + " on "
						+ tablename + ".pk_dept=hi_psnjob.pk_dept";
			} else {
				// һ���ӱ�
				strJoinedTable += " left outer join " + tablename + " on "
						+ tablename + ".pk_psndoc=bd_psndoc.pk_psndoc";
			}
		}
		return strJoinedTable;
	}

	/***************************************************************************
	 * ��������<br>
	 * Created on 2010-2-3 16:15:38<br>
	 * 
	 * @return String ��������
	 * @author Rocex Wang
	 ***************************************************************************/
	private synchronized String getNextOid() {
		if (stackOid.empty()) {
			String strOids[] = OidGenerator.getInstance().nextOid(
					OidGenerator.GROUP_PK_CORP, 10);
			if (strOids != null && strOids.length > 0) {
				stackOid.addAll(Arrays.asList(strOids));
			}
		}
		return stackOid.pop();
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-30 14:49:57<br>
	 * 
	 * @return Map
	 * @throws DAOException
	 * @author Rocex Wang
	 ***************************************************************************/
	public Map getPsndocUniqueRule() throws DAOException {
		// ��ѯĬ�Ϲ�������
		String sql = "select distinct t3.name,t3.displayname,t3.resid from bd_uniquerule t1,bd_uniquerule_item t2,md_property t3"
				+ " where t1.pk_rule=t2.pk_rule and t2.mdcolumnid=t3.id and t1.mdclassid='"
				+ HICommonValue.MD_ID_UAP_PSNDOC
				+ "' and t1.pk_rule <> '0001Z01000000005IQBL' ";
		GeneralVO[] list = (GeneralVO[]) baseDAOManager.executeQuery(sql,
				new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
		Map result = new HashMap<String, String>();
		if (list == null || list.length < 1) {
			return result;
		}
		for (int i = 0; i < list.length; i++) {
			result.put(
					list[i].getAttributeValue("name"),
					ResHelper.getString("10140psn",
							(String) list[i].getAttributeValue("resid")));
		}
		return result;
	}

	/***************************************************************************
	 * ����Ψһ�Ե�vo���Ҹ���Ա�Ƿ���ڣ�����PsndocAggVO
	 * 
	 * @param psndocVO
	 * @return PsndocVO
	 * @throws BusinessException
	 **************************************************************************/
	public PsndocAggVO getPsndocVOByUniqueVO(PsndocVO psndocVO)
			throws BusinessException {
		String strBaseWhere = "1=1";
		// ����vo�γ�where
		String strFields[] = (String[]) getPsndocUniqueRule().keySet().toArray(
				new String[0]);
		// Ҫ����6��������,���������޸�
		// strFields = (String[]) ArrayUtils.addAll(strFields, new String[] {
		// PsndocVO.NAME, PsndocVO.IDTYPE, PsndocVO.ID });
		if (!ArrayUtils.contains(strFields, PsndocVO.IDTYPE)) {
			strFields = (String[]) ArrayUtils.add(strFields, PsndocVO.IDTYPE);
		}

		if (!ArrayUtils.contains(strFields, PsndocVO.ID)) {
			strFields = (String[]) ArrayUtils.add(strFields, PsndocVO.ID);
		}

		if (!ArrayUtils.contains(strFields, PsndocVO.NAME)) {
			strFields = (String[]) ArrayUtils.addAll(strFields, new String[] {
					PsndocVO.NAME, PsndocVO.NAME2, PsndocVO.NAME3,
					PsndocVO.NAME4, PsndocVO.NAME5, PsndocVO.NAME6 });
		} else {
			strFields = (String[]) ArrayUtils.addAll(strFields, new String[] {
					PsndocVO.NAME2, PsndocVO.NAME3, PsndocVO.NAME4,
					PsndocVO.NAME5, PsndocVO.NAME6 });
		}

		String nameSql = "";
		for (String strField : strFields) {
			if (strField.equalsIgnoreCase("pk_psndoc")
					|| psndocVO.getAttributeValue(strField) == null
					|| !strField.startsWith(PsndocVO.NAME)) {
				// ����name�ֶβ�����
				continue;
			}
			String strFieldValue = (String) psndocVO
					.getAttributeValue(strField);
			if (StringUtils.isBlank(nameSql)) {

				nameSql = nameSql + " bd_psndoc." + strField + " = '"
						+ NCESAPI.sqlEncode(strFieldValue) + "' ";
			} else {
				nameSql = nameSql + " or bd_psndoc." + strField + " = '"
						+ NCESAPI.sqlEncode(strFieldValue) + "' ";
			}
			// if (StringUtils.isBlank(nameSql))
			// {
			// nameSql = nameSql + " bd_psndoc." + strField + " = '" +
			// psndocVO.getAttributeValue(strField) +
			// "' ";
			// }
			// else
			// {
			// nameSql = nameSql + " or bd_psndoc." + strField + " = '" +
			// psndocVO.getAttributeValue(strField)
			// + "' ";
			// }

		}
		strBaseWhere = strBaseWhere + " and ( " + nameSql + " )";

		for (String strField : strFields) {
			if (strField.equalsIgnoreCase("pk_psndoc")
					|| psndocVO.getAttributeValue(strField) == null
					|| strField.startsWith(PsndocVO.NAME)) {
				// ������name����ֶ�
				continue;
			}

			Object objValue = psndocVO.getAttributeValue(strField);
			if (objValue instanceof String || objValue instanceof UFBoolean
					|| objValue instanceof UFDateTime
					|| objValue instanceof UFTime) {
				if (PsndocVO.ID.equals(strField)) {

					String idtype = psndocVO.getIdtype();
					if (idtype.equals("1001Z01000000000AI36")) {
						String id = objValue.toString();
						String id15 = "";
						String id18 = "";
						if (id.length() == 15) {
							id15 = id;
							IDValidateUtil idVU = new IDValidateUtil(id);
							id18 = idVU.getUpgradeId();
						} else if (id.length() == 18) {
							id15 = id.substring(0, 6) + id.substring(8, 17);
							id18 = id;
						} else {
							id18 = id;
							id15 = id;
						}

						// ֤���ŵ�֤����Ϣ�в�
						strBaseWhere = strBaseWhere + " and ( hi_psndoc_cert."
								+ strField + " = '" + NCESAPI.sqlEncode(id18)
								+ "' or hi_psndoc_cert." + strField + " = '"
								+ NCESAPI.sqlEncode(id15) + "' ) ";
					} else {
						strBaseWhere = strBaseWhere + " and  hi_psndoc_cert."
								+ strField + " = '"
								+ NCESAPI.sqlEncode(objValue.toString()) + "' ";
					}
				} else {
					strBaseWhere = strBaseWhere + " and bd_psndoc." + strField
							+ " = '" + NCESAPI.sqlEncode(objValue.toString())
							+ "' ";
				}
			} else if (objValue instanceof UFLiteralDate) {
				strBaseWhere = strBaseWhere + " and bd_psndoc." + strField
						+ " like '" + ((UFLiteralDate) objValue).toStdString()
						+ "%' ";
			} else if (objValue instanceof UFDouble
					|| objValue instanceof Integer) {
				if (PsndocVO.IDTYPE.equals(strField)) {
					// ֤�����͵�֤����Ϣ�в�
					strBaseWhere = strBaseWhere + " and hi_psndoc_cert."
							+ strField + " = " + objValue.toString() + " ";
				} else {
					strBaseWhere = strBaseWhere + " and bd_psndoc." + strField
							+ " = " + objValue.toString() + " ";
				}
			}

		}
		// �������Ѿ���ϵͳ���ˣ���Ҫ�ų����ˣ�����Ƿ���������ظ���
		if (psndocVO.getPk_psndoc() != null) {
			strBaseWhere = strBaseWhere + " and bd_psndoc.pk_psndoc<>'"
					+ psndocVO.getPk_psndoc() + "'";
		}

		// ��ѯ��Ч֤����Ϣ
		String strOtherWhere = " and hi_psndoc_cert.iseffect = 'Y' and hi_psnorg.lastflag='Y'";
		ArrayList<String> listField = new ArrayList<String>();
		String strPsndocFieldCodes[] = new PsndocVO().getAttributeNames();
		for (int i = 0; i < strPsndocFieldCodes.length; i++) {
			listField.add("bd_psndoc." + strPsndocFieldCodes[i]
					+ " as bd_psndoc_" + strPsndocFieldCodes[i]);
		}
		String strPsnOrgFieldCodes[] = new PsnOrgVO().getAttributeNames();
		for (int i = 0; i < strPsnOrgFieldCodes.length; i++) {
			listField.add("hi_psnorg." + strPsnOrgFieldCodes[i]
					+ " as hi_psnorg_" + strPsnOrgFieldCodes[i]);
		}
		String strPsnJobFieldCodes[] = new PsnJobVO().getAttributeNames();
		for (int i = 0; i < strPsnJobFieldCodes.length; i++) {
			listField.add("hi_psnjob." + strPsnJobFieldCodes[i]
					+ " as hi_psnjob_" + strPsnJobFieldCodes[i]);
		}
		PsndocVO psndocVOs[] = queryPsndocVOByCondition(null, listField,
				new String[] { "hi_psndoc_cert" },
				strBaseWhere + strOtherWhere, null);
		if (psndocVOs == null || psndocVOs.length == 0) {
			return null;
		}
		PsndocAggVO psndocAggVO = new PsndocAggVO();
		psndocAggVO.setParentVO(psndocVOs[0]);
		return psndocAggVO;
	}

	private String getWholeSql(LoginContext context, List<String> listField,
			String[] strTableNames, String strCondition, String strOrder) {
		// ƴselectStr
		String selectStr = "";
		int fieldcount = listField.size();
		for (int i = 0; i < fieldcount; i++) {
			selectStr = selectStr + "," + listField.get(i);
		}
		selectStr = selectStr.substring(1);
		// �õ�������
		String joinedTableStr = getJoinedTable(strTableNames);
		String wheresql = "";
		String orderbysql = "";
		if (strCondition != null && strCondition.length() > 0) {
			wheresql = strCondition.trim();
			if (!wheresql.toLowerCase().startsWith("where")) {
				wheresql = "where " + wheresql;
			}
		}
		if (strOrder != null && strOrder.length() > 0) {
			orderbysql = strOrder.trim();
			if (!orderbysql.toLowerCase().startsWith("order by")) {
				orderbysql = "order by " + orderbysql;
			}
		}
		String sql = "select " + selectStr + joinedTableStr + " " + wheresql
				+ " " + orderbysql;
		return sql;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-5-7 11:28:57<br>
	 * 
	 * @param strPk_psnjobs
	 * @return PsndocAggVO[]
	 * @author Rocex Wang
	 * @param issyncwork
	 * @param strPk_hrorg
	 * @throws BusinessException
	 * @throws NamingException
	 ***************************************************************************/
	public void intoDoc(PsndocVO[] psndocvos, boolean issyncwork,
			String strPk_hrorg, String[] strPk_psnjobs)
			throws BusinessException {
		if (strPk_psnjobs == null || strPk_psnjobs.length == 0) {
			return;
		}
		InSQLCreator ttu = new InSQLCreator();

		// ��Ա������¼������ʱ��
		String inSql = ttu.getInSQL(strPk_psnjobs);
		//
		validateInDocInf(inSql);

		// ��ѯ������Ϣ
		PsnJobVO[] psnjob = (PsnJobVO[]) getRetrieve().retrieveByClause(null,
				PsnJobVO.class, " pk_psnjob in ( " + inSql + " ) ");

		// ����ת��ǰ�¼�
		String[] org = new String[psnjob.length];
		for (int i = 0; i < org.length; i++) {
			org[i] = strPk_hrorg;
		}
		HiBatchEventValueObject.fireEvent(psnjob, psnjob, org,
				HICommonValue.MD_ID_PSNDOC, IHiEventType.INDOC_BEFORE);

		PsndocVO[] doc = (PsndocVO[]) getRetrieve().retrieveByClause(
				null,
				PsndocVO.class,
				" pk_psndoc in ( select pk_psndoc from hi_psnjob where pk_psnjob in ( "
						+ inSql + " ) ) ");

		// ����������ts
		baseDAOManager.updateVOArray(doc);

		// ����UAP������ͣ����Ա
		dismissUAPPsn(doc, psnjob);

		// ������֯��ϵת����־
		String strSQL = " update hi_psnorg set indocflag='Y',pk_hrorg = '"
				+ strPk_hrorg
				+ "' where pk_psnorg in (select pk_psnorg from hi_psnjob where pk_psnjob in ( "
				+ inSql + " ) ) ";
		baseDAOManager.executeUpdate(strSQL);

		// ���¹�����¼��HR��֯
		String jobSql = " update hi_psnjob set pk_hrorg = '" + strPk_hrorg
				+ "' where pk_psnjob in ( " + inSql + " ) ";
		baseDAOManager.executeUpdate(jobSql);

		// ���»�����Ϣ����֯
		String psnSql = " update bd_psndoc set pk_org = ( select hi_psnjob.pk_org from hi_psnjob "
				+ "inner join hi_psnorg on hi_psnjob.pk_psnorg = hi_psnorg.pk_psnorg "
				+ "where hi_psnjob.pk_psndoc = bd_psndoc.pk_psndoc and hi_psnjob.lastflag = 'Y' and hi_psnjob.ismainjob = 'Y' "
				+ "and hi_psnorg.lastflag = 'Y' ) where pk_psndoc in (select pk_psndoc from hi_psnjob where pk_psnjob in ( "
				+ inSql + " )) ";
		baseDAOManager.executeUpdate(psnSql);

		// �������������У��,ͨ��sqlʵ��
		String validateSql = " select count(*) from hi_psndoc_psnchg p inner join hi_psnorg o on p.pk_psnorg = o.pk_psnorg and p.lastflag ='Y' "
				+ " inner join hi_psnjob j on o.pk_psnorg =j.pk_psnorg and j.pk_psnjob in ( "
				+ inSql
				+ " ) "
				+ " where ( isnull(p.enddate,'~')<>'~' and p.enddate>=j.begindate )"
				+ " or (isnull(p.enddate,'~')='~' and isnull(p.begindate,'~')<>'~' and p.begindate>=j.begindate )  ";
		Object obj = baseDAOManager.executeQuery(validateSql,
				new ColumnProcessor());
		if (obj != null && ((Integer) obj) > 0) {
			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0271")/*
									 * @ res
									 * "ͬ����������Ӽ��쳣:��ְ���ڲ������ڵ�����һ��¼�ĵ�ְ���ڻ���ְ���ڣ�"
									 */);
		}

		if (issyncwork) {
			ArrayList<WorkVO> workAddList = new ArrayList<WorkVO>();
			ArrayList<WorkVO> workUpdateList = new ArrayList<WorkVO>();
			/** ��������֯,����,��λ,ְ�������map,1��֯,2����,3��λ,4ְ�� 1+PK --- name */
			HashMap<String, String> workNameMap = new HashMap<String, String>();

			// ת��Ա������Ҫͬ����������
			// ����lastflag&recordnum;
			String sql1 = " update hi_psndoc_work set recordnum = recordnum+ (select cnt from (select pk_psndoc,count(*) cnt "
					+ "from hi_psnjob where pk_psnorg in (select pk_psnorg from hi_psnjob where pk_psnjob in ("
					+ inSql
					+ ")) group by pk_psndoc) temp where hi_psndoc_work.pk_psndoc = temp.pk_psndoc  ),lastflag = 'N' "
					+ "where pk_psndoc in (select pk_psndoc from hi_psnjob where pk_psnjob in ("
					+ inSql + ") ) ";
			baseDAOManager.executeUpdate(sql1);

			PsnJobVO[] psnjob2 = (PsnJobVO[]) getRetrieve().retrieveByClause(
					null,
					PsnJobVO.class,
					" pk_psnorg in ( select pk_psnorg from hi_psnjob where pk_psnjob in ( "
							+ inSql + " ) ) order by begindate ");
			HashMap<String, PsnJobVO> map = new HashMap<String, PsnJobVO>();
			for (PsnJobVO job : psnjob2) {
				map.put(job.getPk_psnjob(), job);
			}

			// ɾ����pk_psnjob֮ǰ��Ӧ�Ĺ��������Ķ�Ӧ��ϵ������֮
			WorkVO[] vos = (WorkVO[]) getRetrieve().retrieveByClause(
					null,
					WorkVO.class,
					" pk_psnjob in ("
							+ ttu.getInSQL(map.keySet().toArray(new String[0]))
							+ ") ");
			for (int i = 0; vos != null && i < vos.length; i++) {
				if (vos[i].getEnddate() == null) {
					PsnJobVO pj = map.get(vos[i].getPk_psnjob());
					if (vos[i].getBegindate() != null
							&& vos[i].getBegindate().afterDate(
									pj.getBegindate().getDateBefore(1))) {
						vos[i].setEnddate(vos[i].getBegindate());
					} else {
						vos[i].setEnddate(pj.getBegindate() == null ? PubEnv
								.getServerLiteralDate() : pj.getBegindate()
								.getDateBefore(1));
					}
				}
				vos[i].setPk_psnjob(null);
				vos[i].setStatus(VOStatus.UPDATED);
				workUpdateList.add(vos[i]);
			}

			HashMap<String, String> orgNameMap = getNameMap(psnjob2, 1);
			HashMap<String, String> deptNameMap = getNameMap(psnjob2, 2);
			HashMap<String, String> postNameMap = getNameMap(psnjob2, 3);
			HashMap<String, String> jobNameMap = getNameMap(psnjob2, 4);
			// ��ȡ������¼��������Ϣͬ���ֶ�ӳ��(��ѭ�����ᵽ���棬ֻ��ѯ����)
			HashMap<String, GeneralVO> mainSyncMap = getWorkSyncMap(
					strPk_hrorg, PubEnv.getPk_group(), true);
			HashMap<String, GeneralVO> partSyncMap = getWorkSyncMap(
					strPk_hrorg, PubEnv.getPk_group(), false);
			for (PsnJobVO job : psnjob2) {
				// ��ù�����¼��������Ϣͬ���ֶ�ӳ��
				HashMap<String, GeneralVO> syncMap = (job.getIsmainjob() != null && job
						.getIsmainjob().booleanValue()) ? mainSyncMap
						: partSyncMap;

				// ���������Ĺ�����¼,����һ���µ�������¼,����֮
				WorkVO newWork = new WorkVO();
				newWork.setPk_group(job.getPk_group());
				newWork.setPk_org(job.getPk_hrorg());
				newWork.setPk_psndoc(job.getPk_psndoc());
				newWork.setPk_psnjob(job.getPk_psnjob());
				newWork.setRecordnum(0);
				newWork.setLastflag(UFBoolean.TRUE);
				newWork.setBegindate(job.getBegindate());
				newWork.setEnddate(job.getEnddate());
				newWork.setMemo(job.getMemo());
				// ��֯
				// String orgName = getName(OrgVO.class, job.getPk_org(),
				// workNameMap, 1);
				String orgName = orgNameMap.get(job.getPk_org());
				newWork.setWorkcorp(orgName);
				// ����
				// String deptName = getName(DeptVO.class, job.getPk_dept(),
				// workNameMap, 2);
				String deptName = deptNameMap.get(job.getPk_dept());
				newWork.setWorkdept(deptName);
				// ��λ
				// String postName = getName(PostVO.class, job.getPk_post(),
				// workNameMap, 3);
				String postName = postNameMap.get(job.getPk_post());
				newWork.setWorkpost(postName);
				// ְ��
				// String jobName = getName(JobVO.class, job.getPk_job(),
				// workNameMap, 4);
				String jobName = jobNameMap.get(job.getPk_job());
				newWork.setWorkjob(jobName);

				// ����ӳ���ϵͬ��������¼��������¼
				if (syncMap != null) {
					String[] jobItems = syncMap.keySet().toArray(new String[0]);
					for (String jobItem : jobItems) {
						// ��֤����ͬ���ֶ��Ƿ���ʹ�ã�δʹ����ͬ��
						String[] jobAttrSet = job.getAttributeNames();
						String[] workAttrSet = newWork.getAttributeNames();
						if (ArrayUtils.contains(jobAttrSet, jobItem)
								&& ArrayUtils.contains(
										workAttrSet,
										syncMap.get(jobItem).getAttributeValue(
												"workcode"))) {
							Integer jobDataType = (Integer) syncMap
									.get(jobItem).getAttributeValue(
											"jobdatatype");
							Integer workDataType = (Integer) syncMap.get(
									jobItem).getAttributeValue("workdatatype");
							// �Ӳ���ͬ�����ı�,��Ҫ����������ͬ�����ı��ֶ�,����ֱ��ͬ��
							if (IBillItem.UFREF == jobDataType
									&& IBillItem.STRING == workDataType) {
								String pk_refinfo = (String) syncMap.get(
										jobItem).getAttributeValue(
										"jobrefmodule");
								Object value = job.getAttributeValue(jobItem);
								String pk_infoset_item = (String) syncMap.get(
										jobItem).getAttributeValue(
										"job_pk_infoset_item");
								// ���ն�Ӧ����
								String name = getRefItemName(pk_refinfo, value,
										pk_infoset_item);
								String workItemCode = (String) syncMap.get(
										jobItem).getAttributeValue("workcode");
								newWork.setAttributeValue(workItemCode, name);
							}
							// ������ֱ��ͬ��
							else {
								String workItemCode = (String) syncMap.get(
										jobItem).getAttributeValue("workcode");
								newWork.setAttributeValue(workItemCode,
										job.getAttributeValue(jobItem));
							}

						}
					}
				}

				workAddList.add(newWork);
			}

			if (workUpdateList.size() > 0) {
				baseDAOManager.updateVOArray(workUpdateList
						.toArray(new WorkVO[0]));
			}

			if (workAddList.size() > 0) {
				baseDAOManager
						.insertVOArray(workAddList.toArray(new WorkVO[0]));
			}
		}

		ArrayList<PsnChgVO> psnchgAddList = new ArrayList<PsnChgVO>();
		ArrayList<PsnChgVO> psnchgUpdateList = new ArrayList<PsnChgVO>();
		/** ������֯������Ӧ������˾������ */
		HashMap<String, String> corpMap = new HashMap<String, String>();

		ArrayList<String> docPKs = new ArrayList<String>();

		HashMap<String, PsnChgVO[]> psnchgMap = getPsnchgMap(psnjob);
		for (PsnJobVO job : psnjob) {
			docPKs.add(job.getPk_psndoc());

			// ת��Ա����Ҫͬ���������
			// getPsnrdsService().addPsnChgWhenIntoDoc(job);
			String pk_neworg = job.getPk_org();
			String pk_newcorp = corpMap.get(pk_neworg);
			if (StringUtils.isBlank(pk_newcorp)) {
				pk_newcorp = HiSQLHelper.getPkCorpByPkOrg(pk_neworg);
			}
			// if (!StringUtils.isBlank(pk_newcorp))
			// {
			corpMap.put(pk_neworg, pk_newcorp);

			// PsnChgVO[] psnchgVOs = (PsnChgVO[])
			// getRetrieve().retrieveByClause(null, PsnChgVO.class,
			// " pk_psnorg = '" + job.getPk_psnorg() + "' ");
			PsnChgVO[] psnchgVOs = psnchgMap.get(job.getPk_psnorg());
			boolean isNeedAddChg = true;
			for (int i = 0; psnchgVOs != null && i < psnchgVOs.length; i++) {
				boolean isEqualOrg = psnchgVOs[i].getPk_org()
						.equals(pk_newcorp)
						&& psnchgVOs[i].getEnddate() == null;
				if (psnchgVOs[i].getLastflag() != null
						&& psnchgVOs[i].getLastflag().booleanValue()
						&& !isEqualOrg) {
					psnchgVOs[i]
							.setEnddate(job.getBegindate().getDateBefore(1));
				}
				if (psnchgVOs[i].getLastflag() != null
						&& psnchgVOs[i].getLastflag().booleanValue()
						&& isEqualOrg) {
					isNeedAddChg = false;
				}
				if (!isEqualOrg) {
					psnchgVOs[i].setLastflag(UFBoolean.FALSE);
					psnchgVOs[i].setRecordnum(psnchgVOs[i].getRecordnum() + 1);
				}
				psnchgUpdateList.add(psnchgVOs[i]);
			}

			if (isNeedAddChg) {
				// �����¼�¼
				PsnChgVO psnchg = new PsnChgVO();
				psnchg.setRecordnum(0);
				psnchg.setLastflag(UFBoolean.TRUE);
				psnchg.setPk_group(job.getPk_hrgroup());
				psnchg.setPk_org(job.getPk_hrorg());
				psnchg.setPk_psndoc(job.getPk_psndoc());
				psnchg.setPk_psnorg(job.getPk_psnorg());
				psnchg.setAssgid(job.getAssgid());
				psnchg.setBegindate(job.getBegindate());
				psnchg.setComecorpname(null);
				psnchg.setTocorpname(null);
				psnchg.setPk_corp(pk_newcorp);
				psnchgAddList.add(psnchg);
			}

		}

		if (psnchgUpdateList.size() > 0) {
			baseDAOManager.updateVOArray(psnchgUpdateList
					.toArray(new PsnChgVO[0]));
		}

		if (psnchgAddList.size() > 0) {
			baseDAOManager
					.insertVOArray(psnchgAddList.toArray(new PsnChgVO[0]));
		}

		// ͬ����Ա������Ϣ
		updateDataAfterSubDataChanged(WorkVO.getDefaultTableName(),
				docPKs.toArray(new String[0]));
		// ͬ����Ա������Ϣ
		updateDataAfterSubDataChanged(PsnChgVO.getDefaultTableName(),
				docPKs.toArray(new String[0]));

		// ����ת�����¼�
		psnjob = (PsnJobVO[]) getRetrieve().retrieveByClause(null,
				PsnJobVO.class, " pk_psnjob in ( " + inSql + " ) ");
		String[] org2 = new String[psnjob.length];
		for (int i = 0; i < org2.length; i++) {
			org2[i] = strPk_hrorg;
		}

		// ר��
		// ����Ա�������¼� doc
		for (int i = 0; i < doc.length; i++) {
			nc.vo.bd.psn.PsndocVO psndocVO = new nc.vo.bd.psn.PsndocVO();
			List<nc.vo.bd.psn.PsnjobVO> psnjobVOList = new ArrayList<nc.vo.bd.psn.PsnjobVO>();
			try {
				for (int j = 0; j < psnjob.length; j++) {
					if (doc[i].getPk_psndoc().equals(psnjob[j].getPk_psndoc())) {
						nc.vo.bd.psn.PsnjobVO newPsnjobVO = new nc.vo.bd.psn.PsnjobVO();
						BeanUtils.copyProperties(newPsnjobVO, psnjob[j]);
						newPsnjobVO.setPsncode(doc[i].getCode());
						psnjobVOList.add(newPsnjobVO);
					}
				}

				BeanUtils.copyProperties(psndocVO, doc[i]);
				psndocVO.setPsnjobs(psnjobVOList
						.toArray(new nc.vo.bd.psn.PsnjobVO[0]));
				BDCommonEventUtil eventUtil = new BDCommonEventUtil(
						HICommonValue.MD_ID_UAP_PSNDOC);
				eventUtil.dispatchInsertAfterEvent(psndocVO);
			} catch (Exception e) {
				Logger.error(e.getMessage(), e);
			}
		}

		HiBatchEventValueObject.fireEvent(psnjob, psnjob, org2,
				HICommonValue.MD_ID_PSNDOC, IHiEventType.INDOC_AFTER);
		HiEventValueObject.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
		try {
			// �˴�һ���Ƿ�ͬһ�����
			PluginExecutor<IPsndocIntoDoc> executor = new PluginExecutor<IPsndocIntoDoc>(
					IPsndocIntoDoc.class);

			PsnIntoDocPluginExecDelegate delegate = new PsnIntoDocPluginExecDelegate(
					psndocvos, psnjob, psnjob, org2);

			executor.exec(delegate);
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
		}

		// ͬ������
		HiCacheUtils.synCache(PsndocVO.getDefaultTableName(),
				PsnJobVO.getDefaultTableName(), PsnOrgVO.getDefaultTableName());
	}

	private HashMap<String, PsnChgVO[]> getPsnchgMap(PsnJobVO[] vos)
			throws BusinessException {
		HashMap<String, PsnChgVO[]> map = new HashMap<String, PsnChgVO[]>();
		StringBuffer sql = new StringBuffer();
		InSQLCreator isc = new InSQLCreator();
		String insql = isc.getInSQL(vos, "pk_psnorg");
		sql.append(" pk_psnorg in (").append(insql)
				.append(") and lastflag = 'Y'");
		Collection<PsnChgVO> cvos = getDao().retrieveByClause(PsnChgVO.class,
				sql.toString());
		if (ArrayUtils.isEmpty(cvos.toArray())) {
			return map;
		}

		for (PsnChgVO vo : cvos) {
			String pk_psnorg = vo.getPk_psnorg();
			PsnChgVO[] tvos = map.get(pk_psnorg);
			tvos = (PsnChgVO[]) ArrayUtils.add(tvos, vo);
			map.put(pk_psnorg, tvos);
		}
		return map;
	}

	private HashMap<String, String> getNameMap(PsnJobVO[] vos, int type)
			throws BusinessException {
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuffer sql = new StringBuffer();
		InSQLCreator isc = new InSQLCreator();
		String insql = null;
		if (type == 1) { // ��ѯ��֯
			insql = isc.getInSQL(vos, "pk_org");
			sql.append("select pk_org pk,name");
			sql.append(MultiLangHelper.getLangIndex());
			sql.append(" name from org_orgs where pk_org in (");
			sql.append(insql);
			sql.append(")");
		} else if (type == 2) { // ��ѯ����
			insql = isc.getInSQL(vos, "pk_dept");
			sql.append("select pk_dept pk,name");
			sql.append(MultiLangHelper.getLangIndex());
			sql.append(" name from org_dept where pk_dept in (");
			sql.append(insql);
			sql.append(")");
		} else if (type == 3) {
			insql = isc.getInSQL(vos, "pk_post");
			sql.append("select pk_post pk,postname");
			sql.append(MultiLangHelper.getLangIndex());
			sql.append(" name from om_post where pk_post in (");
			sql.append(insql);
			sql.append(")");
		} else if (type == 4) {
			insql = isc.getInSQL(vos, "pk_job");
			sql.append("select pk_job pk,jobname");
			sql.append(MultiLangHelper.getLangIndex());
			sql.append(" name from om_job where pk_job in (");
			sql.append(insql);
			sql.append(")");
		}
		map = (HashMap<String, String>) getDao().executeQuery(sql.toString(),
				new ResultSetProcessor() {
					@Override
					public Object handleResultSet(ResultSet rs)
							throws SQLException {
						HashMap<String, String> vmap = new HashMap<String, String>();
						while (rs.next()) {
							String pk = rs.getString("pk");
							String name = rs.getString("name");
							vmap.put(pk, name);
						}
						return vmap;
					}
				});
		return map;
	}

	private <T extends SuperVO> String getName(Class<T> cls, String pk,
			HashMap<String, String> nameMap, int type) throws BusinessException {
		if (StringUtils.isBlank(pk)) {
			return null;
		}
		if (nameMap.get(type + pk) != null) {
			return nameMap.get(type + pk);
		}

		String name = VOUtils.getDocName(cls, pk);
		nameMap.put(type + pk, name);
		return name;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-6-12 13:52:32<br>
	 * 
	 * @param strPk_psndoc
	 * @return boolean
	 * @author Rocex Wang
	 * @throws DAOException
	 ***************************************************************************/
	public boolean isInJob(String strPk_psndoc) throws DAOException {
		String strSQL = "select psnjob.pk_psnjob from hi_psnjob psnjob inner join hi_psnorg psnorg on psnjob.pk_psnorg = psnorg.pk_psnorg"
				+ " where psnorg.endflag = 'N' and psnorg.psntype = 0 and psnorg.lastflag = 'Y' and psnorg.indocflag = 'Y'"
				+ " and psnjob.endflag = 'N' and psnjob.lastflag = 'Y' and psnjob.ismainjob = 'Y' and psnjob.pk_psndoc='"
				+ strPk_psndoc + "'";
		List objResult = (List) baseDAOManager.executeQuery(strSQL,
				new ArrayListProcessor());
		return objResult != null && objResult.size() > 0;
	}

	public Map<String, Boolean> isInJob(String[] pk_psndocs)
			throws BusinessException {
		InSQLCreator isc = new InSQLCreator();
		String insql = isc.getInSQL(pk_psndocs);

		String strSQL = "select psnjob.pk_psnjob from hi_psnjob psnjob inner join hi_psnorg psnorg on psnjob.pk_psnorg = psnorg.pk_psnorg"
				+ " where psnorg.endflag = 'N' and psnorg.psntype = 0 and psnorg.lastflag = 'Y' and psnorg.indocflag = 'Y'"
				+ " and psnjob.endflag = 'N' and psnjob.lastflag = 'Y' and psnjob.ismainjob = 'Y' and psnjob.pk_psndoc in ("
				+ insql + ")";
		Map<String, Boolean> resultMap = (Map<String, Boolean>) baseDAOManager
				.executeQuery(strSQL, new InJobMapProcess(pk_psndocs));
		return resultMap;
	}

	private class InJobMapProcess extends BaseProcessor {

		private static final long serialVersionUID = 1L;

		private String[] pkpsndocs = null;

		public InJobMapProcess(String[] pk_psndocs) {
			pkpsndocs = pk_psndocs;
		}

		@Override
		public Map<String, Boolean> processResultSet(ResultSet rs)
				throws SQLException {
			Map<String, Boolean> resultMap = new HashMap<String, Boolean>();
			if (rs.next()) {
				String pk_psnjob = rs.getString("pk_psnjob");
				if (StringUtils.isNotBlank(pk_psnjob)) {
					resultMap.put(pk_psnjob, true);
				}
			}
			for (int i = 0; i < pkpsndocs.length; i++) {
				if (resultMap.containsKey(pkpsndocs[i])) {
					continue;
				} else {
					resultMap.put(pkpsndocs[i], false);
				}
			}
			return resultMap;
		}
	}

	/**
	 * ����ְvoһһת��Ϊ��Ա����vo������n��������n��
	 * 
	 * @param psnjobVOCol
	 * @return
	 * @throws DAOException
	 */
	private <T extends PsnJobVO> PsndocVO[] processPsnJobVO2PsndocVO(
			Collection<T> psnjobVOCol) throws DAOException {
		if (null == psnjobVOCol || psnjobVOCol.isEmpty()) {
			return new PsndocVO[0];
		}

		Map<String, List<PsnJobVO>> psnJobVOCache = new HashMap<String, List<PsnJobVO>>();
		PsnJobVO[] psnJobVOs = psnjobVOCol.toArray(new PsnJobVO[0]);
		List<String> psndocPKs = new ArrayList<String>();
		for (int i = 0; i < psnJobVOs.length; i++) {
			if (!psndocPKs.contains(psnJobVOs[i].getPk_psndoc())) {
				psndocPKs.add(psnJobVOs[i].getPk_psndoc());
			}
			List<PsnJobVO> list = null;
			if (null == psnJobVOCache.get(psnJobVOs[i].getPk_psndoc())) {
				list = new ArrayList<PsnJobVO>();
			} else {
				list = psnJobVOCache.get(psnJobVOs[i].getPk_psndoc());
			}
			list.add(psnJobVOs[i]);
			psnJobVOCache.put(psnJobVOs[i].getPk_psndoc(), list);
		}
		Collection<PsndocAggVO> coll = new ArrayList<PsndocAggVO>();

		try {
			ArrayList<String> alAttr = new ArrayList<String>();
			for (String attr : new PsndocVO().getAttributeNames()) {
				if (PsndocVO.PHOTO.equals(attr)
						|| PsndocVO.PREVIEWPHOTO.equals(attr)) {
					continue;
				}
				alAttr.add(attr);
			}
			NCObject[] objs = MDPersistenceService
					.lookupPersistenceQueryService().queryBillOfNCObjectByPKs(
							PsndocVO.class, psndocPKs.toArray(new String[0]),
							alAttr.toArray(new String[0]), true);
			for (NCObject obj : objs) {
				if (null == obj)
					continue;

				coll.add((PsndocAggVO) obj.getContainmentObject());
			}
		} catch (MetaDataException e) {
			throw new DAOException(e);
		}

		if (null == coll || coll.isEmpty()) {
			return new PsndocVO[0];
		}

		// ����֯��ϵ��ѯ�Ż�
		String[] pk_psndocs = new String[coll.size()];
		PsndocAggVO[] psndocAggVOs = coll.toArray(new PsndocAggVO[0]);
		for (int i = 0; i < coll.size(); i++) {
			pk_psndocs[i] = psndocAggVOs[i].getParentVO().getPk_psndoc();
		}
		// ������Աpk������е��������ϵ
		String strSubCondition = "{0} in ( {1} ) and {2}=''{3}''";
		PsnOrgVO[] psnOrgVOs = null;
		try {
			strSubCondition = MessageFormat.format(strSubCondition,
					PsnOrgVO.PK_PSNDOC,
					new InSQLCreator().getInSQL(pk_psndocs), PsnOrgVO.LASTFLAG,
					UFBoolean.TRUE);
			psnOrgVOs = queryByCondition(PsnOrgVO.class, strSubCondition);
		} catch (BusinessException e) {
			throw new DAOException(e);
		}
		// ��Աpk��Ӧ������һ����֯��ϵ��map
		HashMap<String, PsnOrgVO> lastPsnOrgMap = new HashMap<String, PsnOrgVO>();
		for (PsnOrgVO psnOrgVO : psnOrgVOs) {
			if (lastPsnOrgMap.containsKey(psnOrgVO.getPk_psndoc()))
				continue;
			lastPsnOrgMap.put(psnOrgVO.getPk_psndoc(), psnOrgVO);
		}

		List<PsndocVO> resultList = new ArrayList<PsndocVO>();
		for (PsndocAggVO psndocAggVO : coll) {
			PsndocVO psndocVO = psndocAggVO.getParentVO();
			String psndocPk = psndocVO.getPk_psndoc();
			List<PsnJobVO> psnJobList = psnJobVOCache.get(psndocPk);
			if (null == psnJobList || psnJobList.isEmpty()) {
				continue;
			}

			if (null != lastPsnOrgMap.get(psndocVO.getPk_psndoc())) {
				psndocVO.setPsnOrgVO(lastPsnOrgMap.get(psndocVO.getPk_psndoc()));
			}

			for (PsnJobVO psnJobVO : psnJobList) {
				PsndocVO clonedDocVO = (PsndocVO) psndocVO.clone();
				clonedDocVO.setPsnJobVO(psnJobVO);
				resultList.add(clonedDocVO);
			}
		}
		return resultList.toArray(new PsndocVO[0]);
	}

	/***************************************************************************
	 * ��ѯ��Ա�б������մ�����ֶν��в�ѯ������߲�ѯ�ٶ�
	 * 
	 * @param context
	 * @param listField
	 * @param strTableNames
	 * @param strCondition
	 * @param strOrder
	 * @return PsndocVO[]
	 * @throws BusinessException
	 **************************************************************************/
	public PsndocVO[] queryPsndocVOByCondition(LoginContext context,
			List<String> listField, String[] strTableNames,
			String strCondition, String strOrder) throws BusinessException {
		String sql = getWholeSql(context, listField, strTableNames,
				strCondition, strOrder);
		// ��ѯ
		List<PsndocVO> list = (List<PsndocVO>) baseDAOManager.executeQuery(sql,
				new BeanListProcessor(PsndocVO.class));
		return list == null || list.size() == 0 ? null : list
				.toArray(new PsndocVO[0]);
	}

	/***************************************************************************
	 * ����ѯ
	 * 
	 * @param context
	 * @param strTableNames
	 * @param strCondition
	 * @param strOrder
	 * @return PsndocVO[]
	 * @throws BusinessException
	 **************************************************************************/
	public PsndocVO[] queryPsndocVOByCondition(LoginContext context,
			String[] strTableNames, String strCondition, String strOrder)
			throws BusinessException {
		ArrayList<String> fields = getDefaultQueryFields();
		return queryPsndocVOByCondition(context, fields, strTableNames,
				strCondition, strOrder);
	}

	/**
	 * @param pk_job
	 *            ְ������
	 * @param includeInPos
	 *            �Ƿ������ְ��
	 * @param includeOutPos
	 *            �Ƿ������ְ
	 * @param inCludeMainJob
	 *            �Ƿ������ְ
	 * @param includePartTimeJob
	 *            �Ƿ������ְ
	 * @param includeNoneIndoc
	 *            �Ƿ����δת����Ա��������Ա
	 * @return PsndocVO[]
	 * @throws DAOException
	 */
	public PsndocVO[] queryPsndocVOByJobPK(String pk_job, boolean includeInPos,
			boolean includeOutPos, boolean inCludeMainJob,
			boolean includePartTimeJob, boolean includeNoneIndoc)
			throws DAOException {
		return queryPsndocVOByPsnJobField(PsnJobVO.PK_JOB, pk_job,
				includeInPos, includeOutPos, inCludeMainJob,
				includePartTimeJob, includeNoneIndoc);
	}

	/***************************************************************************
	 * ����ְ�ȡ�ְ���������������Ȼ��ʹ�ô�ְ�ȡ�ְ���������Ч�Ĺ�����¼��������Ա����Ϣ,�����ҵ��ĵ�һ������<br>
	 * Created on 2010-4-15 14:17:32<br>
	 * 
	 * @param <T>
	 * @param vo
	 * @return <T>
	 * @throws BusinessException
	 * @author zengcheng
	 ***************************************************************************/
	public <T extends SuperVO> PsndocVO queryPsndocVOByJobPostFamily(T vo)
			throws BusinessException {
		return queryPsndocVOByJobPostFamily(vo, null);
	}

	/***************************************************************************
	 * ����ְ�ȡ�ְ���������������Ȼ��ʹ�ô�ְ�ȡ�ְ���������Ч�Ĺ�����¼��������Ա����Ϣ,�����ҵ��ĵ�һ������<br>
	 * Created on 2010-4-15 14:17:32<br>
	 * 
	 * @param <T>
	 * @param vo
	 * @param psnjobcond
	 * @return <T>
	 * @throws BusinessException
	 * @author zengcheng
	 ***************************************************************************/
	public <T extends SuperVO> PsndocVO queryPsndocVOByJobPostFamily(T vo,
			String psnjobcond) throws BusinessException {
		String pkFieldName = vo.getPKFieldName();
		String sql = "select top 1 pk_psndoc,name,name2,name3,name4,name5,name6,code from bd_psndoc where pk_psndoc in(select pk_psndoc from hi_psnjob where "
				+ pkFieldName + "=? and endflag='N'";
		if (StringUtils.isNotBlank(psnjobcond)) {
			sql += " and (" + psnjobcond + ")";
		}
		sql += ")";
		SQLParameter para = new SQLParameter();
		para.addParam(vo.getPrimaryKey());
		List<PsndocVO> list = (List<PsndocVO>) baseDAOManager.executeQuery(sql,
				para, new BeanListProcessor(PsndocVO.class));
		return list == null || list.size() == 0 ? null : list.get(0);
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-6-17 20:44:47<br>
	 * 
	 * @param strPks
	 * @return PsndocAggVO[]
	 * @author Rocex Wang
	 * @param blLazyLoad
	 * @throws BusinessException
	 ***************************************************************************/
	public PsndocAggVO[] queryPsndocVOByPks(boolean blLazyLoad,
			String... strPks) throws BusinessException {

		if (strPks == null || strPks.length == 0) {
			return null;
		}

		ArrayList<String> fields = getDefaultQueryFields();
		String fieldSql = "";
		for (String fld : fields) {
			fieldSql += "," + fld;
		}
		fieldSql = fieldSql.substring(1);

		ArrayList<PsndocVO> psns = null;
		InSQLCreator ttu = null;
		try {
			ttu = new InSQLCreator();
			String selectSql = " select "
					+ fieldSql
					+ " from bd_psndoc inner join hi_psnorg on bd_psndoc.pk_psndoc = hi_psnorg.pk_psndoc "
					+ " inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg where hi_psnjob.pk_psnjob in ( "
					+ ttu.getInSQL(strPks) + " ) order by hi_psnjob.showorder";
			psns = (ArrayList<PsndocVO>) baseDAOManager.executeQuery(selectSql,
					new BeanListProcessor(PsndocVO.class));

		}
		// �����쳣�Ե��������ѯ��ѭ��
		// catch (Exception e)
		// {
		// Logger.error(e.getMessage(), e);
		// }
		finally {
			if (ttu != null) {

				ttu.clear();

			}
		}

		if (psns == null || psns.size() == 0) {
			return null;
		}

		HashMap<String, PsndocVO> hm = new HashMap<String, PsndocVO>();
		for (PsndocVO psn : psns) {
			hm.put(psn.getPsnJobVO().getPk_psnjob(), psn);
		}

		ArrayList<PsndocAggVO> al = new ArrayList<PsndocAggVO>();
		for (String pk : strPks) {
			if (hm.get(pk) == null) {
				continue;
			}
			PsndocAggVO agg = new PsndocAggVO();
			agg.setParentVO(hm.get(pk));
			al.add(agg);
		}

		return al.size() > 0 ? al.toArray(new PsndocAggVO[0]) : null;

	}

	public Object[] queryPsndocVOByPks(boolean b, String[] strPks,
			Object treeObj) throws BusinessException {
		if (strPks == null || strPks.length == 0) {
			return null;
		}

		ArrayList<String> fields = getDefaultQueryFields();
		String fieldSql = "";
		for (String fld : fields) {
			fieldSql += "," + fld;
		}
		fieldSql = fieldSql.substring(1);

		ArrayList<PsndocVO> psns = null;
		InSQLCreator ttu = null;
		try {
			ttu = new InSQLCreator();
			String inSql = ttu.getInSQL(strPks);
			String selectSql = " select "
					+ fieldSql
					+ " from bd_psndoc inner join hi_psnorg on bd_psndoc.pk_psndoc = hi_psnorg.pk_psndoc "
					+ " inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg where hi_psnjob.pk_psnjob in ( "
					+ inSql + " )";
			psns = (ArrayList<PsndocVO>) baseDAOManager.executeQuery(selectSql,
					new BeanListProcessor(PsndocVO.class));

			if (psns == null || psns.size() == 0) {
				return null;
			}

			String sql = " select pk_psnorg , sum(case when endflag ='Y' then 0 else 1 end) as count from hi_psndoc_keypsn where pk_psnorg in ( select pk_psnorg from hi_psnjob where pk_psnjob in ( "
					+ inSql + " )) ";
			if (treeObj instanceof KeyPsnGrpVO) {
				sql += " and pk_keypsn_grp ='"
						+ ((KeyPsnGrpVO) treeObj).getPk_keypsn_group() + "' ";
			}

			sql += " group by pk_psnorg ";

			GeneralVO[] vos = (GeneralVO[]) new BaseDAO().executeQuery(sql,
					new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
			HashMap<String, UFBoolean> map = new HashMap<String, UFBoolean>();
			for (int i = 0; vos != null && i < vos.length; i++) {
				map.put((String) vos[i].getAttributeValue("pk_psnorg"),
						(vos[i].getAttributeValue("pk_psnorg") == null || new Integer(
								vos[i].getAttributeValue("count").toString()) <= 0) ? UFBoolean.TRUE
								: UFBoolean.FALSE);
			}

			HashMap<String, PsndocVO> hm = new HashMap<String, PsndocVO>();
			for (PsndocVO psn : psns) {
				psn.setIshiskeypsn(map.get(psn.getPsnOrgVO().getPk_psnorg()) == null ? UFBoolean.TRUE
						: map.get(psn.getPsnOrgVO().getPk_psnorg()));
				hm.put(psn.getPsnJobVO().getPk_psnjob(), psn);
			}

			ArrayList<PsndocAggVO> al = new ArrayList<PsndocAggVO>();
			for (String pk : strPks) {
				if (hm.get(pk) == null) {
					continue;
				}
				PsndocAggVO agg = new PsndocAggVO();
				agg.setParentVO(hm.get(pk));
				al.add(agg);
			}

			return al.size() > 0 ? al.toArray(new PsndocAggVO[0]) : null;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return null;
		} finally {
			if (ttu != null) {

				ttu.clear();

			}
		}
	}

	@SuppressWarnings("unused")
	private PsndocAggVO fillDocOrgVO(PsnJobVO psnJobVO, boolean blLazyLoad)
			throws BusinessException {
		PsndocAggVO agg = new PsndocAggVO();
		PsndocVO doc = queryByPk(PsndocVO.class, psnJobVO.getPk_psndoc(),
				blLazyLoad);
		doc = fillDateFormula(doc)[0];
		PsnOrgVO org = queryByPk(PsnOrgVO.class, psnJobVO.getPk_psnorg(),
				blLazyLoad);
		org = fillDateFormula(org)[0];
		psnJobVO = fillDateFormula(psnJobVO)[0];
		doc.setPsnJobVO(psnJobVO);
		doc.setPsnOrgVO(org);
		agg.setParentVO(doc);
		return agg;
	}

	/**
	 * �����ڹ�ʽ��ֵ,ֻ֧��psndoc/psnorg/psnjob/psnchg
	 * 
	 * @param <T>
	 * @param vos
	 * @return T[]
	 * @throws BusinessException
	 */
	public <T extends SuperVO> T[] fillDateFormula(T... vos)
			throws BusinessException {
		if (vos == null || vos.length == 0) {
			return null;
		}
		int dbtype = DataSourceCenter.getInstance().getDatabaseType();
		String cond = " pk_infoset in ( select pk_infoset from hr_infoset where infoset_code ='"
				+ vos[0].getTableName()
				+ "' and pk_infoset_sort = '"
				+ HICommonValue.PSNDOC_INFOSET_SORT_PK
				+ "' ) and data_type= "
				+ InfoItemVO.DATE_FORMULA;
		InfoItemVO[] items = queryByCondition(InfoItemVO.class, cond);

		// ���������ڹ�ʽƴ��һ��sql
		String formulaSql = "";
		for (int i = 0; items != null && i < items.length; i++) {
			if (StringUtils.isBlank(items[i].getItem_formula_sql())) {
				continue;
			}

			String fSql = items[i].getItem_formula_sql();

			fSql = FormuaDateHelper.getFuncParser().transToSql(fSql, dbtype);

			formulaSql += fSql + " as " + items[i].getItem_code() + " , ";
		}

		if (StringUtils.isBlank(formulaSql)) {
			return vos;
		}

		// �����������ȥ���µ�,������δת���������Ա�����,�����Ӽ�������ʾ�����ڹ�ʽֵ������ͬ��
		String querySql = " select "
				+ formulaSql
				+ " 1 from bd_psndoc inner join hi_psnorg on bd_psndoc.pk_psndoc = hi_psnorg.pk_psndoc "
				+ " inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg and bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc "
				+ " left outer join hi_psndoc_psnchg on hi_psnorg.pk_psnorg = hi_psndoc_psnchg.pk_psnorg  and hi_psndoc_psnchg.pk_psndoc = bd_psndoc.pk_psndoc "
				+ " where hi_psnorg.lastflag = 'Y' and hi_psnjob.lastflag ='Y' and hi_psnjob.ismainjob = 'Y' "
				+ " and isnull(hi_psndoc_psnchg.lastflag,'Y') = 'Y' ";

		for (int i = 0; i < vos.length; i++) {
			String sql = querySql + " and " + vos[0].getTableName()
					+ ".pk_psndoc = '"
					+ (String) vos[i].getAttributeValue("pk_psndoc") + "' ";
			ArrayList<HashMap> array = (ArrayList<HashMap>) baseDAOManager
					.executeQuery(sql, new MapListProcessor());
			if (array == null || array.size() <= 0) {
				continue;
			}
			HashMap result = array.get(0);
			for (int j = 0; items != null && j < items.length; j++) {
				if (StringUtils.isBlank(items[j].getItem_formula_sql())) {
					continue;
				}
				vos[i].setAttributeValue(items[j].getItem_code(),
						result.get(items[j].getItem_code()));
			}

		}

		return vos;
	}

	/**
	 * @param pk_post
	 *            ְ������
	 * @param includeInPos
	 *            �Ƿ������ְ��
	 * @param includeOutPos
	 *            �Ƿ������ְ
	 * @param inCludeMainJob
	 *            �Ƿ������ְ
	 * @param includePartTimeJob
	 *            �Ƿ������ְ
	 * @param includeNoneIndoc
	 *            �Ƿ����δת����Ա��������Ա
	 * @return PsndocVO[]
	 * @throws DAOException
	 */
	public PsndocVO[] queryPsndocVOByPostPK(String pk_post,
			boolean includeInPos, boolean includeOutPos,
			boolean inCludeMainJob, boolean includePartTimeJob,
			boolean includeNoneIndoc) throws DAOException {
		return queryPsndocVOByPsnJobField(PsnJobVO.PK_POST, pk_post,
				includeInPos, includeOutPos, inCludeMainJob,
				includePartTimeJob, includeNoneIndoc);
	}

	/**
	 * ���ݸ�λ��ѯ��Ա����Ҫ����level�����ˣ�0-��ǰ��λ��1-ֱ���¼���2-������...,-1�����¼�
	 * 
	 * @param pk_post
	 *            ��λ����
	 * @param inPos
	 *            ���Ƿ�����ְ��true��indocflag=Y&&psnorg.endflag=N&&psnjob.endflag=N,
	 *            false,indocflag=Y&&(psnorg.endflag =Y||psnjob.endflag=Y)
	 * @param level
	 * @return PsndocVO[]
	 * @throws DAOException
	 */
	public PsndocVO[] queryPsndocVOByPostPkAndLevel(String pk_post,
			boolean inPos, int level) throws DAOException {
		if (StringUtils.isBlank(pk_post)) {
			return null;
		}
		String postCond = null;
		String hi_psnjob = "hi_psnjob";
		String om_post = new PostVO().getTableName();
		if (level == 0) {
			postCond = hi_psnjob + "." + PsnJobVO.PK_POST + "=?";
		} else if (level < 0) {
			postCond = hi_psnjob + "." + PsnJobVO.PK_POST + " in (select "
					+ PostVO.PK_POST + " from " + om_post + " where "
					+ PostVO.INNERCODE + " like ? and " + PostVO.PK_POST
					+ "<>? )";
		} else {
			postCond = hi_psnjob + "." + PsnJobVO.PK_POST + " in ";
			StringBuilder inSql = new StringBuilder("select " + PostVO.PK_POST
					+ " from " + om_post + " where " + PostVO.SUPORIOR + "=?");
			Map<Integer, String> levelCondMap = new HashMap<Integer, String>();// �洢����-������map,key�Ǽ��Σ�value��������ע�⣬�˴��������ǵ��ڵ�ǰ���Σ����������ͼ��Σ����磬level=2��������ֻ���˳�����������������һ��
			levelCondMap.put(1, inSql.toString());
			for (int i = 1; i < level; i++) {
				inSql.insert(
						0,
						"select " + PostVO.PK_POST + " from " + om_post
								+ " where " + PostVO.SUPORIOR + " in(").append(
						") ");
				levelCondMap.put(i + 1, inSql.toString());
			}
			for (int i = 0; i < level; i++) {
				if (i > 0) {
					postCond += " or " + hi_psnjob + "." + PsnJobVO.PK_POST
							+ " in ";
				}
				postCond += "(" + levelCondMap.get(i + 1) + ")";
			}
			postCond = "(" + postCond + ")";
		}
		String sql = "select * from hi_psnjob inner join hi_psnorg on hi_psnjob.pk_psnorg=hi_psnorg.pk_psnorg where "
				+ postCond + " and " + "hi_psnorg.indocflag='Y' ";
		String inPosCond = inPos ? " hi_psnorg.endflag='N' and hi_psnjob.endflag='N' "
				: " hi_psnorg.endflag='Y' or hi_psnjob.endflag='Y' ";
		sql += " and (" + inPosCond + ") ";
		SQLParameter para = new SQLParameter();
		if (level == 0) {
			para.addParam(pk_post);
		}
		if (level < 0) {
			PostVO postVO = (PostVO) new BaseDAO().retrieveByPK(PostVO.class,
					pk_post);
			para.addParam(postVO.getInnercode() + "%");
			para.addParam(pk_post);
		} else {
			// int count = level*(level+1)/2;
			for (int i = 0; i < level; i++) {
				para.addParam(pk_post);
			}
		}
		PersistenceDAO dao = new PersistenceDAO();
		PsnBudgetVO[] vos = null;
		try {
			vos = dao.retrieveBySQL(PsnBudgetVO.class, sql, para);
		} catch (PersistenceDbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage(), e);
		}
		return processPsnJobVO2PsndocVO(vos == null || vos.length == 0 ? null
				: Arrays.asList(vos));
	}

	/**
	 * ������Ա��ְ����ĳ���ֶε�ֵ��ѯ��Ա
	 * 
	 * @param jobvoClz
	 * @param <T>
	 * @param fieldName
	 *            ��ְ�����ֶ���
	 * @param fieldVal
	 *            ��ְ���ֶε�ֵ
	 * @param includeInPos
	 *            �Ƿ����endflag=N����ְ
	 * @param includeOutPos
	 *            �Ƿ����endflag=Y����ְ
	 * @param inCludeMainJob
	 *            �Ƿ������ְ
	 * @param includePartTimeJob
	 *            �Ƿ������ְ
	 * @param includeNoneIndoc
	 *            �Ƿ����δת����Ա��������Ա
	 * @return PsndocVO[]
	 * @throws DAOException
	 */
	public <T extends PsnJobVO> PsndocVO[] queryPsndocVOByPsnJobField(
			Class<T> jobvoClz, String fieldName, Object fieldVal,
			boolean includeInPos, boolean includeOutPos,
			boolean inCludeMainJob, boolean includePartTimeJob,
			boolean includeNoneIndoc) throws DAOException {
		// �����ְ����ְ������������϶��鲻������
		if (!includeInPos && !includeOutPos) {
			return null;
		}
		// �����ְ��ְ������������϶��鲻������
		if (!inCludeMainJob && !includePartTimeJob) {
			return null;
		}
		String[] fields = new PsnJobVO().getAttributeNames();
		String sql = "select ";
		for (String field : fields) {
			sql += "hi_psnjob." + field + ",";
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += " from hi_psnjob ";
		StringBuilder sqlwhere = new StringBuilder();
		// ���δת����Ա��������Ա�����أ�����Ҫ������֯��ϵ������Ϊת����־����֯��ϵ����
		if (!includeNoneIndoc) {
			sql += " inner join hi_psnorg on hi_psnorg.pk_psnorg=hi_psnjob.pk_psnorg ";
			sqlwhere.append(" hi_psnorg.indocflag = 'Y'");
		}
		if (includeInPos && !includeOutPos) {
			sqlwhere.append(sqlwhere.length() > 0 ? " and " : "").append(
					" hi_psnjob.endflag = 'N' ");
		} else if (!includeInPos && includeOutPos) {
			sqlwhere.append(sqlwhere.length() > 0 ? " and " : "").append(
					" hi_psnjob.endflag = 'Y' ");
		}
		if (inCludeMainJob && !includePartTimeJob) {
			sqlwhere.append(sqlwhere.length() > 0 ? " and " : "").append(
					" hi_psnjob.ismainjob = 'Y' ");
		} else if (!inCludeMainJob && includePartTimeJob) {
			sqlwhere.append(sqlwhere.length() > 0 ? " and " : "").append(
					" hi_psnjob.ismainjob = 'N' ");
		}
		sqlwhere.insert(0, sqlwhere.length() > 0 ? " and " : "").insert(0,
				" hi_psnjob." + fieldName + " = ?");
		// sqlwhere.append(sqlwhere.length() > 0 ? " and " :
		// "").append(" hi_psnjob.").append(fieldName).append(" = ?");
		SQLParameter param = new SQLParameter();
		param.addParam(fieldVal);
		PersistenceDAO dao = new PersistenceDAO();
		T[] vos = null;
		try {
			vos = dao
					.retrieveBySQL(jobvoClz, sql + " where " + sqlwhere, param);
		} catch (PersistenceDbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage(), e);
		}
		return processPsnJobVO2PsndocVO(vos == null || vos.length == 0 ? null
				: Arrays.asList(vos));
	}

	/**
	 * ������Ա��ְ����ĳ���ֶε�ֵ��ѯ��Ա
	 * 
	 * @param fieldName
	 *            ��ְ�����ֶ���
	 * @param fieldVal
	 *            ��ְ���ֶε�ֵ
	 * @param includeInPos
	 *            �Ƿ������ְ��
	 * @param includeOutPos
	 *            �Ƿ������ְ
	 * @param inCludeMainJob
	 *            �Ƿ������ְ
	 * @param includePartTimeJob
	 *            �Ƿ������ְ
	 * @param includeNoneIndoc
	 *            �Ƿ����δת����Ա��������Ա
	 * @return PsndocVO[]
	 * @throws DAOException
	 */
	public PsndocVO[] queryPsndocVOByPsnJobField(String fieldName,
			Object fieldVal, boolean includeInPos, boolean includeOutPos,
			boolean inCludeMainJob, boolean includePartTimeJob,
			boolean includeNoneIndoc) throws DAOException {
		return queryPsndocVOByPsnJobField(PsnJobVO.class, fieldName, fieldVal,
				includeInPos, includeOutPos, inCludeMainJob,
				includePartTimeJob, includeNoneIndoc);
	}

	/**
	 * ��ѯ����ְ������ְ����Ա��ְ�����ڲ鿴ְ�񡢸�λ������ְ������ְ����λ������ְ����Ҫ�ж��Ƿ�ռ�࣬��Ҫ���⴦����
	 * inPos=true����ѯ����ְ��inPos=false����ѯ����ְ
	 * ����ְ�Ĺ��������ǣ���Ա��ת����Ա����������ְ(��֯��ϵendflag=N)������ְ��¼δ����
	 * ����ְ�Ĺ��������ǣ���ת����Ա�����ģ�����������ְ������������ְ
	 * 
	 * @param <T>
	 * @param clz
	 * @param fieldName
	 * @param fieldValue
	 * @param inPos
	 * @return T
	 * @throws DAOException
	 */
	public <T extends PsnJobVO> PsndocVO[] queryPsndocVOByWhetherInPost(
			Class<T> clz, String fieldName, String fieldValue, boolean inPos)
			throws DAOException {
		String sql = "select hi_psnjob.* from hi_psnjob inner join hi_psnorg on hi_psnjob.pk_psnorg=hi_psnorg.pk_psnorg where "
				+ fieldName + "=? and " + "hi_psnorg.indocflag='Y' ";
		String inPosCond = inPos ? " hi_psnorg.endflag='N' and hi_psnjob.endflag='N' "
				: " hi_psnorg.endflag='Y' or hi_psnjob.endflag='Y' ";
		sql += " and (" + inPosCond + ")";
		SQLParameter para = new SQLParameter();
		para.addParam(fieldValue);
		PersistenceDAO dao = new PersistenceDAO();
		T[] vos = null;
		try {
			vos = dao.retrieveBySQL(clz, sql, para);
		} catch (PersistenceDbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage(), e);
		}
		return processPsnJobVO2PsndocVO(vos == null || vos.length == 0 ? null
				: Arrays.asList(vos));
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-30 14:51:05<br>
	 * 
	 * @param strTableNames
	 * @param strCondition
	 * @param strOrder
	 * @param iPageSize
	 * @param iPageIndex
	 * @param blLazyLoad
	 *            �Ƿ��������ӱ�����
	 * @return PsndocVO[]
	 * @throws BusinessException
	 * @author Rocex Wang
	 ***************************************************************************/
	@SuppressWarnings("unused")
	private PsndocAggVO[] queryPsndocVOForPage(String[] strTableNames,
			String strCondition, String strOrder, int iPageSize,
			int iPageIndex, boolean blLazyLoad) throws BusinessException {
		// ArrayList<String> fields = getDefaultQueryFields();
		// String sql = getWholeSql(null, fields, strTableNames, strCondition,
		// strOrder);
		// �ع� �õ���ҳsql
		// String pageSql = new PageSplitUtils().getPageQuerySql(sql, iPageSize,
		// iPageIndex);
		if (strCondition == null || strCondition.trim().length() == 0) {
			strCondition = "1=1";
		}
		if (strOrder != null && strOrder.trim().length() > 0) {
			strCondition = strCondition + " order by " + strOrder;
		}
		String sql = "select bd_psndoc.pk_psndoc from bd_psndoc left join hi_psnorg on bd_psndoc.pk_psndoc=hi_psnorg.pk_psndoc"
				+ " left join hi_psnjob on bd_psndoc.pk_psndoc=hi_psnjob.pk_psndoc"
				+ " where " + strCondition;
		List<PsndocVO> list = (List<PsndocVO>) baseDAOManager.executeQuery(sql,
				new BeanListProcessor(PsndocVO.class));
		if (list == null || list.size() == 0) {
			return null;
		}
		int iStartRecordIndex = iPageIndex * iPageSize;
		int iEndRecordIndex = iStartRecordIndex + iPageSize;
		if (iStartRecordIndex > list.size() - 1) {
			iStartRecordIndex = list.size() - iPageSize;
		}
		if (iEndRecordIndex > list.size() - 1) {
			iEndRecordIndex = list.size();
		}
		List<String> listPk = new ArrayList<String>();
		for (int i = iStartRecordIndex; i < iEndRecordIndex; i++) {
			PsndocVO obj = list.get(i);
			listPk.add(obj.getPrimaryKey());
		}
		Object objResults[] = MDPersistenceService
				.lookupPersistenceQueryService().queryBillOfVOByPKsWithOrder(
						PsndocAggVO.class, listPk.toArray(new String[0]),
						blLazyLoad);
		if (objResults == null || objResults.length == 0) {
			return null;
		}
		// PsndocAggVO[] psndocAggVOs = coll.toArray((PsndocAggVO[])
		// Array.newInstance(PsndocAggVO.class,coll.size()));
		PsndocAggVO[] psndocAggVOs = new PsndocAggVO[objResults.length];
		for (int i = 0; i < objResults.length; i++) {
			psndocAggVOs[i] = (PsndocAggVO) objResults[i];
			fillOrgJobVO(psndocAggVOs[i].getParentVO());
		}
		return psndocAggVOs;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-30 14:50:55<br>
	 * 
	 * @param strWhere
	 * @return List<PsnInfoListVO>
	 * @throws BusinessException
	 * @author
	 **************************************************************************/
	public List<PsnJobVO> queryPsninfoByCondition(String strWhere)
			throws BusinessException {
		String sql = " select hi_psnjob.clerkcode, hi_psnjob.pk_hrorg,hi_psnjob.pk_org, hi_psnjob.pk_psncl, hi_psnjob.pk_dept, hi_psnjob.pk_job, hi_psnjob.pk_post, "
				+ "hi_psnjob.pk_psnjob,hi_psnjob.pk_psndoc from bd_psndoc inner join hi_psnorg on bd_psndoc.pk_psndoc = hi_psnorg.pk_psndoc "
				+ "inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg  where "
				+ strWhere;
		return (List<PsnJobVO>) baseDAOManager.executeQuery(sql,
				new BeanListProcessor(PsnJobVO.class));
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-1-30 14:50:55<br>
	 * 
	 * @param pks
	 * @return List<PsnInfoListVO>
	 * @throws BusinessException
	 * @author
	 **************************************************************************/
	public List<PsnJobVO> queryPsninfoByPks(String[] pks)
			throws BusinessException {
		String sql = " select hi_psnjob.clerkcode, hi_psnjob.pk_org, hi_psnjob.pk_psncl, hi_psnjob.pk_dept, hi_psnjob.pk_job, hi_psnjob.pk_post, "
				+ "hi_psnjob.pk_psnjob,hi_psnjob.pk_psndoc from bd_psndoc inner join hi_psnorg on bd_psndoc.pk_psndoc = hi_psnorg.pk_psndoc "
				+ "inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg ";
		String where = " where hi_psnjob.pk_psnjob in (";
		for (String pk : pks) {
			where += "'" + pk + "',";
		}
		where = where.substring(0, where.length() - 1) + ")";
		return (List<PsnJobVO>) baseDAOManager.executeQuery(sql + where,
				new BeanListProcessor(PsnJobVO.class));
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-4-26 13:05:59<br>
	 * 
	 * @param strPkPsnorg
	 * @param iAssigId
	 * @return PsnJobVO[]
	 * @author Rocex Wang
	 * @throws BusinessException
	 ***************************************************************************/
	public PsnJobVO[] queryPsnMainJob(String strPkPsnorg, int iAssigId)
			throws BusinessException {
		String strSQL = "{0}= ''{1}'' and {2}= {3} and {4}=''{5}''";
		strSQL = MessageFormat.format(strSQL, PsnJobVO.PK_PSNORG, strPkPsnorg,
				PsnJobVO.ASSGID, iAssigId, PsnJobVO.ISMAINJOB, UFBoolean.TRUE);
		return queryByCondition(PsnJobVO.class, strSQL);
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-4-26 13:23:46<br>
	 * 
	 * @param strPkPsnorg
	 * @param iAssigId
	 * @return PsnJobVO[]
	 * @author Rocex Wang
	 * @param strCondition
	 * @throws BusinessException
	 ***************************************************************************/
	public PsnJobVO[] queryPsnPartTimeJobs(String strPkPsnorg, int iAssigId,
			String strCondition) throws BusinessException {
		String strSQL = "{0}= ''{1}'' and {2}= {3} and {4}=''{5}''";
		strSQL = MessageFormat.format(strSQL, PsnJobVO.PK_PSNORG, strPkPsnorg,
				PsnJobVO.ASSGID, iAssigId, PsnJobVO.ISMAINJOB, UFBoolean.FALSE);
		if (strCondition != null && strCondition.length() > 0) {
			strSQL = strSQL + strCondition;
		}
		return queryByCondition(PsnJobVO.class, strSQL);
	}

	/**
	 * @param clazz
	 * @param strWhere
	 * @param strOrder
	 * @return SuperVO[]
	 * @throws BusinessException
	 */
	private String[] LABORINS = new String[] { "glbdef2", "glbdef3", "glbdef4",
			"glbdef7", "glbdef8" };
	private String[] HEALTHINS = new String[] { "glbdef6", "glbdef7",
			"glbdef16" };
	private String[] LABORDETAIL = new String[] { "glbdef3", "glbdef4",
			"glbdef5", "glbdef6", "glbdef7", "glbdef8", "glbdef9", "glbdef10",
			"glbdef11", "glbdef12", "glbdef13", "glbdef14", "glbdef15",
			"glbdef16", "glbdef17", "glbdef18", "glbdef19", "glbdef20",
			"glbdef21", "glbdef22", "glbdef23", "glbdef24", "glbdef25",
			"glbdef44", "glbdef27", "glbdef28", "glbdef29", "glbdef30",
			"glbdef31", "glbdef32", "glbdef33", "glbdef34", "glbdef35",
			"glbdef36", "glbdef45", "glbdef39" };
	private String[] LABORSUM = new String[] { "glbdef3", "glbdef4", "glbdef5",
			"glbdef6", "glbdef7", "glbdef8", "glbdef9", "glbdef10", "glbdef11",
			"glbdef12", "glbdef13", "glbdef15", "glbdef16", "glbdef26",
			"glbdef17", "glbdef19", "glbdef20", "glbdef21", "glbdef22",
			"glbdef14" };
	private String[] EXTHEALTHINS = new String[] { "glbdef3", "glbdef5",
			"glbdef6" };

	public SuperVO[] querySubByCondition(Class clazz, String strWhere,
			String strOrder) throws BusinessException {
		String strCondition = " ";
		if (strWhere != null && strWhere.trim().length() > 0) {
			strCondition += strWhere;
		}
		if (strOrder != null && strOrder.trim().length() > 0) {
			strCondition += " order by " + strOrder;
		}
		SuperVO[] vos = (SuperVO[]) queryByCondition(clazz, strCondition);
		if (vos == null || vos.length == 0) {
			return null;
		}
		// ��ǿ ���ݽ��� 2018-1-17 14:29:39 start
		for (int i = 0; i < vos.length; i++) {
			String tableName = vos[i].getTableName();
			// ���ݱ���ȥxml�ļ��в��Ҷ�Ӧ�ı���������Ӧ���ֶν���
			String xmlInfo = XmlMatchUtils.getXmlInfo(tableName);
			if (xmlInfo.equals("LABORINS")) {
				for (String glbdef : LABORINS) {
					double dv = 0;
					if (vos[i].getAttributeValue(glbdef) != null) {
						UFDouble value = (UFDouble) vos[i]
								.getAttributeValue(glbdef);
						dv = value.doubleValue();
					}
					double decrypt = (dv == 0 ? 0.0 : SalaryDecryptUtil
							.decrypt(dv));
					vos[i].setAttributeValue(glbdef, decrypt);
				}
			} else if (xmlInfo.equals("HEALTHINS")) {
				for (String glbdef : HEALTHINS) {
					double dv = 0;
					if (vos[i].getAttributeValue(glbdef) != null) {
						UFDouble value = (UFDouble) vos[i]
								.getAttributeValue(glbdef);
						dv = value.doubleValue();
					}
					double decrypt = (dv == 0 ? 0.0 : SalaryDecryptUtil
							.decrypt(dv));
					vos[i].setAttributeValue(glbdef, decrypt);
				}
			} else if (xmlInfo.equals("LABORDETAIL")) {
				for (String glbdef : LABORDETAIL) {
					double dv = 0;
					if (vos[i].getAttributeValue(glbdef) != null) {
						UFDouble value = (UFDouble) vos[i]
								.getAttributeValue(glbdef);
						dv = value.doubleValue();
					}
					double decrypt = (dv == 0 ? 0.0 : SalaryDecryptUtil
							.decrypt(dv));
					vos[i].setAttributeValue(glbdef, decrypt);
				}
			} else if (xmlInfo.equals("LABORSUM")) {
				for (String glbdef : LABORSUM) {
					double dv = 0;
					if (vos[i].getAttributeValue(glbdef) != null) {
						UFDouble value = (UFDouble) vos[i]
								.getAttributeValue(glbdef);
						dv = value.doubleValue();
					}
					double decrypt = (dv == 0 ? 0.0 : SalaryDecryptUtil
							.decrypt(dv));
					vos[i].setAttributeValue(glbdef, decrypt);
				}
			} else if (xmlInfo.equals("GROUPINS")) {
				double dv = 0;
				if (vos[i].getAttributeValue("glbdef6") != null) {
					UFDouble value = (UFDouble) vos[i]
							.getAttributeValue("glbdef6");
					dv = value.doubleValue();
				}
				double decrypt = (dv == 0 ? 0.0 : SalaryDecryptUtil.decrypt(dv));
				vos[i].setAttributeValue("glbdef6", decrypt);
			} else if (xmlInfo.equals("EXTHEALTHINS")) {
				for (String glbdef : EXTHEALTHINS) {
					double dv = 0;
					if (vos[i].getAttributeValue(glbdef) != null) {
						UFDouble value = (UFDouble) vos[i]
								.getAttributeValue(glbdef);
						dv = value.doubleValue();
					}
					double decrypt = (dv == 0 ? 0.0 : SalaryDecryptUtil
							.decrypt(dv));
					vos[i].setAttributeValue(glbdef, decrypt);
				}
			}
		}
		// ��ǿ ���ݽ��� 2018-1-17 14:29:39 end
		if (vos[0] instanceof PsnChgVO || vos[0] instanceof PsnJobVO) {
			// ������¼/������������ڹ�ʽ��ֵ
			vos = fillDateFormula(vos);
		}
		return vos;
	}

	PsndocVO[] retrievePsndocVOByDeptPK(String[] pk_depts,
			boolean incumbencyOnly, boolean includeNoneIndoc)
			throws BusinessException {
		StringBuilder sqlwhere = new StringBuilder();
		sqlwhere.append(" endflag = 'N' ");
		if (incumbencyOnly) {
			sqlwhere.append(" and ismainjob = 'Y' ");
		}

		InSQLCreator isc = null;
		try {
			isc = new InSQLCreator();
			String inClauses = isc.getInSQL(pk_depts);

			sqlwhere.append(" and pk_dept in (").append(inClauses).append(")");

			// ���˵������Ա
			sqlwhere.append(" and psntype <> 1 ");
			Collection<PsnJobVO> c = baseDAOManager.retrieveByClause(
					PsnJobVO.class, sqlwhere.toString());
			return processPsnJobVO2PsndocVO(c);
		} finally {
			if (isc != null) {
				isc.clear();
			}
		}
	}

	/***************************************************************************
	 * ע��Ĭ��ֵ��ǧ��Ҫ�Ӵ˴��ӡ��˴�ֻ������Աͨ�õı��档
	 * 
	 * @param psndocAggVO
	 * @return PsndocVO
	 * @throws BusinessException
	 **************************************************************************/
	private PsndocVO saveAggVO(PsndocAggVO psndocAggVO)
			throws BusinessException {
		// ���ȸ�������������״̬������pkֵ
		PsndocVO psndocVO = psndocAggVO.getParentVO();
		PsnOrgVO psnOrgVO = psndocVO.getPsnOrgVO();
		PsnJobVO psnJobVOs[] = (PsnJobVO[]) psndocAggVO.getTableVO(PsnJobVO
				.getDefaultTableName());
		// ������Ա
		if (psndocVO.getStatus() == VOStatus.NEW) {

			return saveNewPsndoc(psndocAggVO, psndocVO, psnOrgVO, psnJobVOs);
		}
		// �޸���Ա��������֯��ϵ
		if (psnOrgVO.getStatus() == VOStatus.NEW) {
			// �޸�ʱ�ȰѸ���Ա��˽����֯��ϵ��lastflag���óɡ�N�������汣����֯��ϵ��ʱ���ͨ��VO�е�ֵ��������
			String strSQL = "update " + PsnOrgVO.getDefaultTableName()
					+ " set " + PsnOrgVO.LASTFLAG + "='" + UFBoolean.FALSE
					+ "' where " + PsnOrgVO.PK_PSNDOC + "='"
					+ psndocAggVO.getParentVO().getPrimaryKey() + "'";
			baseDAOManager.executeUpdate(strSQL);
			return saveNewPsnOrg(psndocAggVO, psndocVO, psnOrgVO, psnJobVOs);
		}
		// ��֯��ϵ��������
		return saveNoNewPsnAndPsnOrg(psndocAggVO, psndocVO, psnOrgVO, psnJobVOs);
	}

	/**
	 * ���ڵ��빦�ܵ���Ա��Ϣ����
	 * 
	 * @param psndocAggVO
	 * @return
	 * @throws BusinessException
	 */
	private PsndocVO saveAggVOForImport(PsndocAggVO psndocAggVO)
			throws BusinessException {
		// ���ȸ�������������״̬������pkֵ
		PsndocVO psndocVO = psndocAggVO.getParentVO();
		PsnOrgVO psnOrgVO = psndocVO.getPsnOrgVO();
		PsnJobVO psnJobVOs[] = (PsnJobVO[]) psndocAggVO.getTableVO(PsnJobVO
				.getDefaultTableName());
		PsnOrgVO psnOrgVOs[] = (PsnOrgVO[]) psndocAggVO.getTableVO(PsnOrgVO
				.getDefaultTableName());
		PartTimeVO[] partTimeVOs = (PartTimeVO[]) psndocAggVO
				.getTableVO(PartTimeVO.getDefaultTableName());
		// ���� 2018��5��30��11:00:32 ���ػ���������
		for (SuperVO vo : psndocAggVO.getAllChildrenVO()) {
			String tableName = vo.getTableName();
			String xmlInfo = XmlMatchUtils.getXmlInfo(tableName);
			if (xmlInfo.equals("LABORINS")) {
				for (String glbdef : LABORINS) {
					double dv = 0;
					if (vo.getAttributeValue(glbdef) != null) {
						UFDouble value = (UFDouble) vo
								.getAttributeValue(glbdef);
						dv = value.doubleValue();
					}
					double decrypt = SalaryEncryptionUtil.encryption(dv);
					vo.setAttributeValue(glbdef, decrypt);
				}
			} else if (xmlInfo.equals("HEALTHINS")) {
				for (String glbdef : HEALTHINS) {
					double dv = 0;
					if (vo.getAttributeValue(glbdef) != null) {
						UFDouble value = (UFDouble) vo
								.getAttributeValue(glbdef);
						dv = value.doubleValue();
					}
					double decrypt = SalaryEncryptionUtil.encryption(dv);
					vo.setAttributeValue(glbdef, decrypt);
				}
			} else if (xmlInfo.equals("LABORDETAIL")) {
				for (String glbdef : LABORDETAIL) {
					double dv = 0;
					if (vo.getAttributeValue(glbdef) != null) {
						UFDouble value = (UFDouble) vo
								.getAttributeValue(glbdef);
						dv = value.doubleValue();
					}
					double decrypt = SalaryEncryptionUtil.encryption(dv);
					vo.setAttributeValue(glbdef, decrypt);
				}
			} else if (xmlInfo.equals("LABORSUM")) {
				for (String glbdef : LABORSUM) {
					double dv = 0;
					if (vo.getAttributeValue(glbdef) != null) {
						UFDouble value = (UFDouble) vo
								.getAttributeValue(glbdef);
						dv = value.doubleValue();
					}
					double decrypt = SalaryEncryptionUtil.encryption(dv);
					vo.setAttributeValue(glbdef, decrypt);
				}
			} else if (xmlInfo.equals("GROUPINS")) {
				double dv = 0;
				if (vo.getAttributeValue("glbdef6") != null) {
					UFDouble value = (UFDouble) vo.getAttributeValue("glbdef6");
					dv = value.doubleValue();
				}
				double decrypt = SalaryEncryptionUtil.encryption(dv);
				vo.setAttributeValue("glbdef6", decrypt);
			} else if (xmlInfo.equals("EXTHEALTHINS")) {
				for (String glbdef : EXTHEALTHINS) {
					double dv = 0;
					if (vo.getAttributeValue(glbdef) != null) {
						UFDouble value = (UFDouble) vo
								.getAttributeValue(glbdef);
						dv = value.doubleValue();
					}
					double decrypt = SalaryEncryptionUtil.encryption(dv);
					vo.setAttributeValue(glbdef, decrypt);
				}
			}
		}
		// ������Ա
		if (psndocVO.getStatus() == VOStatus.NEW) {
			return saveNewPsndocForImport(psndocAggVO, psndocVO, psnJobVOs,
					psnOrgVOs, partTimeVOs);
		}
		// �޸���Ա��������֯��ϵ
		if (psnOrgVO.getStatus() == VOStatus.NEW) {
			// �޸�ʱ�ȰѸ���Ա��˽����֯��ϵ��lastflag���óɡ�N�������汣����֯��ϵ��ʱ���ͨ��VO�е�ֵ��������
			String strSQL = "update " + PsnOrgVO.getDefaultTableName()
					+ " set " + PsnOrgVO.LASTFLAG + "='" + UFBoolean.FALSE
					+ "' where " + PsnOrgVO.PK_PSNDOC + "='"
					+ psndocAggVO.getParentVO().getPrimaryKey() + "'";
			baseDAOManager.executeUpdate(strSQL);
			return saveNewPsnOrg(psndocAggVO, psndocVO, psnOrgVO, psnJobVOs);
		}
		// ��֯��ϵ��������
		return saveNoNewPsnAndPsnOrgForImport(psndocAggVO, psndocVO, psnJobVOs);
	}

	private PsndocVO saveNewPsndoc(PsndocAggVO psndocAggVO, PsndocVO psndocVO,
			PsnOrgVO psnOrgVO, PsnJobVO[] psnJobVOs) throws MetaDataException,
			BusinessException {
		// ����bd_psndoc, �����Ч�����⣬�͸�Ϊvo�ı��棬����Ҫʹ��Ԫ���ݵı��档
		String strPk_psndoc = getNextOid();
		psndocVO.setPrimaryKey(strPk_psndoc);
		// ����hi_psnorg
		psnOrgVO.setStatus(VOStatus.NEW);
		psnOrgVO.setPk_psndoc(strPk_psndoc);
		String strPk_psnorg = getNextOid();
		psnOrgVO.setPrimaryKey(strPk_psnorg);
		// ����hi_psnjob
		String strPk_psnjob = null;
		if (psnJobVOs != null && psnJobVOs.length > 0) {
			for (int i = 0; i < psnJobVOs.length; i++) {
				psnJobVOs[i].setStatus(VOStatus.NEW);
				psnJobVOs[i].setPk_psnorg(strPk_psnorg);
				psnJobVOs[i].setPk_psndoc(strPk_psndoc);
				// �ҵ�����������,��������ʱ��������������¼������������Ϣ��
				if (psnJobVOs[i].getLastflag().booleanValue()) {
					strPk_psnjob = getNextOid();
					psnJobVOs[i].setPrimaryKey(strPk_psnjob);
				}
			}
		}
		for (SuperVO superVO : psndocAggVO.getAllChildrenVO()) {
			if (!(superVO instanceof PsnOrgVO)
					&& superVO.getAttributeValue(PsnOrgVO.PK_PSNORG) == null) {
				superVO.setAttributeValue(PsnOrgVO.PK_PSNORG, strPk_psnorg);
			}
			if (!(superVO instanceof PsnJobVO) && !(superVO instanceof WorkVO)
					&& superVO.getAttributeValue(PsnJobVO.PK_PSNJOB) == null) {
				superVO.setAttributeValue(PsnJobVO.PK_PSNJOB, strPk_psnjob);
			}
		}
		MDPersistenceService.lookupPersistenceService().saveBillWithRealDelete(
				psndocAggVO);
		updateDataAfterSubDataChanged(psndocAggVO);

		// 2016-04-29, ssx added for Taiwan NHI requirement
		// for re-modify after 2015-09-23 patch from YYHQ
		// 2017-05-16 upgrade to V65, from JD code
		PsndocNHIDAO.generateNHIInfo(psndocAggVO);

		// ssx added on 2018-03-20
		// for auto NHI enddate
		PsndocNHIDAO.dealNHIDates(psndocAggVO);

		PsndocAggVO aggVO = queryPsndocVOByPk(strPk_psndoc);
		return aggVO == null ? null : aggVO.getParentVO();
	}

	/**
	 * ����������Ա��Ϣ��Ϊ���빤�����
	 * 
	 * @param psndocAggVO
	 * @param psndocVO
	 * @param psnJobVOs
	 * @param psnOrgVOs
	 * @param partTimeVOs
	 * @return
	 * @throws MetaDataException
	 * @throws BusinessException
	 */
	private PsndocVO saveNewPsndocForImport(PsndocAggVO psndocAggVO,
			PsndocVO psndocVO, PsnJobVO[] psnJobVOs, PsnOrgVO[] psnOrgVOs,
			PartTimeVO[] partTimeVOs) throws MetaDataException,
			BusinessException {
		// ����bd_psndoc, �����Ч�����⣬�͸�Ϊvo�ı��棬����Ҫʹ��Ԫ���ݵı��档
		String strPk_psndoc = getNextOid();
		psndocVO.setPrimaryKey(strPk_psndoc);

		// ����hi_psnorg
		for (PsnOrgVO psnOrgVO : psnOrgVOs) {
			psnOrgVO.setPrimaryKey(getNextOid());
			psnOrgVO.setPk_psndoc(strPk_psndoc);
			psnOrgVO.setStatus(VOStatus.NEW);
		}

		// ����hi_psnjob
		String strPk_psnjob = null;
		String strPk_psnorg = null;
		if (psnJobVOs != null && psnJobVOs.length > 0) {
			for (int i = 0; i < psnJobVOs.length; i++) {
				UFLiteralDate jobBegindate = psnJobVOs[i].getBegindate();
				// ��Ϊ��֯��ϵ�Ľ������ڱ���ְ��ʼ������һ�죬�������Ϊ��ְ������¼����ʼ���ڼ�һ��
				if (psnJobVOs[i].getTrnsevent() == 4) {
					jobBegindate = jobBegindate.getDateBefore(1);
				}

				for (PsnOrgVO psnOrgVO : psnOrgVOs) {
					UFLiteralDate orgBegindate = psnOrgVO.getBegindate();
					UFLiteralDate orgEnddate = psnOrgVO.getEnddate();
					// ���ֻ��һ����֯��ϵ������û��¼����֯��ϵʱ����֯��ϵ�������һ��������¼���ɣ�
					if (psnOrgVOs.length == 1) {
						psnJobVOs[i].setPk_psnorg(psnOrgVO.getPrimaryKey());
						break;
					}

					// �ж�����֯��ϵ�����
					if (psnJobVOs[i].getPk_org().equals(psnOrgVO.getPk_org())) {
						if (orgEnddate != null
								&& !"".equals(orgEnddate.toString())) {
							// ��֯��ϵ��ʼ����<=������¼��ʼ����<=��֯��ϵ��������
							if ((jobBegindate.after(orgBegindate) || jobBegindate
									.equals(orgBegindate))
									&& (jobBegindate.before(orgEnddate) || jobBegindate
											.equals(orgEnddate))) {
								psnJobVOs[i].setPk_psnorg(psnOrgVO
										.getPrimaryKey());
								break;
							}
						} else if (jobBegindate.after(orgBegindate)
								|| jobBegindate.equals(orgBegindate)) { // ������¼��ʼ����>=��֯��ϵ��ʼ����
							psnJobVOs[i].setPk_psnorg(psnOrgVO.getPrimaryKey());
							break;
						}
					}
				}

				psnJobVOs[i].setStatus(VOStatus.NEW);
				psnJobVOs[i].setPk_psndoc(strPk_psndoc);
				// �ҵ�����������,��������ʱ��������������¼������������Ϣ��
				if (psnJobVOs[i].getLastflag().booleanValue()) {
					strPk_psnorg = psnJobVOs[i].getPk_psnorg();
					strPk_psnjob = getNextOid();
					psnJobVOs[i].setPrimaryKey(strPk_psnjob);
				}
			}
		}

		if (partTimeVOs != null && partTimeVOs.length > 0) {
			for (int i = 0; i < partTimeVOs.length; i++) {
				UFLiteralDate jobBegindate = partTimeVOs[i].getBegindate();

				for (PsnOrgVO psnOrgVO : psnOrgVOs) {
					UFLiteralDate orgBegindate = psnOrgVO.getBegindate();
					UFLiteralDate orgEnddate = psnOrgVO.getEnddate();
					// ���ֻ��һ����֯��ϵ������û��¼����֯��ϵʱ����֯��ϵ�������һ��������¼���ɣ�
					if (psnOrgVOs.length == 1) {
						partTimeVOs[i].setPk_psnorg(psnOrgVO.getPrimaryKey());
						break;
					}

					// �ж�����֯��ϵ�����
					if (partTimeVOs[i].getPk_org().equals(psnOrgVO.getPk_org())) {
						if (orgEnddate != null
								&& !"".equals(orgEnddate.toString())) {
							// ��֯��ϵ��ʼ����<=������¼��ʼ����<=��֯��ϵ��������
							if ((jobBegindate.after(orgBegindate) || jobBegindate
									.equals(orgBegindate))
									&& (jobBegindate.before(orgEnddate) || jobBegindate
											.equals(orgEnddate))) {
								partTimeVOs[i].setPk_psnorg(psnOrgVO
										.getPrimaryKey());
								break;
							}
						} else if (jobBegindate.after(orgBegindate)
								|| jobBegindate.equals(orgBegindate)) { // ������¼��ʼ����>=��֯��ϵ��ʼ����
							partTimeVOs[i].setPk_psnorg(psnOrgVO
									.getPrimaryKey());
							break;
						}
					}
				}

				partTimeVOs[i].setStatus(VOStatus.NEW);
				partTimeVOs[i].setPk_psndoc(strPk_psndoc);
				// �ҵ�����������,��������ʱ��������������¼������������Ϣ��
				if (partTimeVOs[i].getLastflag().booleanValue()) {
					strPk_psnorg = partTimeVOs[i].getPk_psnorg();
					strPk_psnjob = getNextOid();
					partTimeVOs[i].setPrimaryKey(strPk_psnjob);
				}
			}
		}

		for (SuperVO superVO : psndocAggVO.getAllChildrenVO()) {
			if (!(superVO instanceof PsnOrgVO)
					&& superVO.getAttributeValue(PsnOrgVO.PK_PSNORG) == null) {
				superVO.setAttributeValue(PsnOrgVO.PK_PSNORG, strPk_psnorg);
			}
			if (!(superVO instanceof PsnJobVO) && !(superVO instanceof WorkVO)
					&& superVO.getAttributeValue(PsnJobVO.PK_PSNJOB) == null) {
				superVO.setAttributeValue(PsnJobVO.PK_PSNJOB, strPk_psnjob);
			}
		}
		MDPersistenceService.lookupPersistenceService().saveBillWithRealDelete(
				psndocAggVO);
		updateDataAfterSubDataChanged(psndocAggVO);

		PsndocAggVO aggVO = queryPsndocVOByPk(strPk_psndoc);
		return aggVO == null ? null : aggVO.getParentVO();
	}

	public PsndocAggVO queryPsndocVOByPk(String pk_psndoc)
			throws BusinessException {
		return queryPsndocVOByPk(pk_psndoc, true, true);
	}

	public PsndocAggVO queryPsndocVOByPk(String pk_psndoc,
			boolean includePhoto, boolean includePreviewPhoto)
			throws BusinessException {

		ArrayList<String> str = new ArrayList<String>();
		String[] attrs = new PsndocVO().getAttributeNames();
		for (String attr : attrs) {
			if (PsndocVO.PHOTO.equals(attr) && !includePhoto) {
				continue;
			}
			if (PsndocVO.PREVIEWPHOTO.equals(attr) && !includePreviewPhoto) {
				continue;
			}
			str.add(attr);
		}
		NCObject doc = MDPersistenceService.lookupPersistenceQueryService()
				.queryBillOfNCObjectByPK(PsndocVO.class, pk_psndoc,
						str.toArray(new String[0]), true);
		if (doc == null) {
			return null;
		}
		PsndocAggVO agg = (PsndocAggVO) doc.getContainmentObject();
		PsnOrgVO[] psnorg = queryByCondition_LazyLoad(PsnOrgVO.class,
				" pk_psndoc = '" + pk_psndoc + "' and lastflag = 'Y' ");
		if (psnorg == null || psnorg.length == 0) {
			return agg;
		}
		agg.getParentVO().setPsnOrgVO(psnorg[0]);
		PsnJobVO[] psnjob = queryByCondition_LazyLoad(PsnJobVO.class,
				" pk_psnorg = '" + psnorg[0].getPk_psnorg()
						+ "' and lastflag = 'Y' and assgid = 1 ");
		if (psnjob == null || psnjob.length == 0) {
			return agg;
		}

		String pk_dept = psnjob[0].getPk_dept();
		psnjob[0].setDeptname(VOUtils.getDocName(DeptVO.class, pk_dept));
		agg.getParentVO().setPsnJobVO(psnjob[0]);
		return agg;

	}

	public Map<String, PsndocAggVO> queryPsndocVOByCondition(
			String[] psndocPks, String[] className) throws BusinessException {
		NCObject[] docs = MDPersistenceService
				.lookupPersistenceQueryService()
				.queryBillOfNCObjectByPKs(PsndocVO.class, psndocPks, null, true);
		if (ArrayUtils.isEmpty(docs)) {
			return null;
		}

		Map<String, PsndocAggVO> psndocAggVOMap = new HashMap<String, PsndocAggVO>();
		for (int i = 0; i < psndocPks.length; i++) {
			for (int j = 0; j < docs.length; j++) {
				PsndocAggVO agg = (PsndocAggVO) docs[j].getContainmentObject();
				if (agg == null) {
					continue;
				}
				if (psndocPks[i].equals(agg.getParentVO().getPk_psndoc())) {
					psndocAggVOMap.put(psndocPks[i], agg);
				}
			}
		}

		InSQLCreator isc = new InSQLCreator();
		String insql = isc.getInSQL(psndocPks);
		PsnOrgVO[] psnorgs = queryByCondition_LazyLoad(PsnOrgVO.class,
				" pk_psndoc in (" + insql + ") and lastflag = 'Y' ");
		if (ArrayUtils.isEmpty(docs)) {

		}

		PsnJobVO[] psnJobs = (PsnJobVO[]) getRetrieve().retrieveByClause(null,
				PsnJobVO.class, "");
		if (!ArrayUtils.isEmpty(psnJobs)) {
			for (int i = 0; i < psndocPks.length; i++) {
				List<PsnJobVO> psnJobList = new ArrayList<PsnJobVO>();
				for (int j = 0; j < psnJobs.length; j++) {
					if (psndocPks[i] == psnJobs[j].getPk_psndoc()) {
						psnJobList.add(psnJobs[j]);
					}
				}
				psndocAggVOMap.get(psndocPks[i]);
			}
		}

		// for (int i = 0; i < className.length; i++)
		// {
		// getRetrieve().retrieveByClause(null, arg1, arg2);
		// }
		//
		// PsndocAggVO agg = (PsndocAggVO) doc.getContainmentObject();
		return null;
	}

	private PsndocVO saveNewPsnOrg(PsndocAggVO psndocAggVO, PsndocVO psndocVO,
			PsnOrgVO psnOrgVO, PsnJobVO[] psnJobVOs) throws MetaDataException,
			BusinessException {
		String strPk_psndoc = psndocVO.getPk_psndoc();
		String strPk_psnorg = getNextOid();
		psnOrgVO.setPrimaryKey(strPk_psnorg);
		psnOrgVO.setPk_psndoc(strPk_psndoc);
		String strPk_psnjob = null;
		if (psnJobVOs != null && psnJobVOs.length > 0) {
			for (int i = 0; i < psnJobVOs.length; i++) {
				psnJobVOs[i].setPk_psnorg(strPk_psnorg);
				// �ҵ�����������,��������ʱ��������������¼������������Ϣ��
				if (psnJobVOs[i].getLastflag().booleanValue()) {
					strPk_psnjob = getNextOid();
					psnJobVOs[i].setPrimaryKey(strPk_psnjob);
				}
			}
		}
		for (SuperVO superVO : psndocAggVO.getAllChildrenVO()) {
			if (!(superVO instanceof PsnOrgVO)
					&& superVO.getAttributeValue(PsnOrgVO.PK_PSNORG) == null) {
				superVO.setAttributeValue(PsnOrgVO.PK_PSNORG, strPk_psnorg);
			}
			if (!(superVO instanceof PsnJobVO) && !(superVO instanceof WorkVO)
					&& superVO.getAttributeValue(PsnJobVO.PK_PSNJOB) == null) {
				superVO.setAttributeValue(PsnJobVO.PK_PSNJOB, strPk_psnjob);
			}
		}
		MDPersistenceService.lookupPersistenceService().saveBillWithRealDelete(
				psndocAggVO);
		updateDataAfterSubDataChanged(psndocAggVO);

		// ssx added on 2018-03-20
		// for auto NHI enddate
		PsndocNHIDAO.dealNHIDates(psndocAggVO);

		PsndocAggVO aggVO = queryPsndocVOByPk(strPk_psndoc);
		// queryPsndocVOByPks(true, getPkpsnjobByPkpsndoc(strPk_psndoc));
		return aggVO == null ? null : aggVO.getParentVO();
	}

	private PsndocVO saveNoNewPsnAndPsnOrg(PsndocAggVO psndocAggVO,
			PsndocVO psndocVO, PsnOrgVO psnOrgVO, PsnJobVO[] psnJobVOs)
			throws MetaDataException, BusinessException {
		String strPk_psndoc = psndocVO.getPk_psndoc();
		String strPk_psnorg = psnOrgVO.getPk_psnorg();
		// �����µĹ�����¼Ҫ������֯��ϵpk
		String strPk_psnjob = null;
		if (psnJobVOs != null && psnJobVOs.length > 0) {
			for (int i = 0; i < psnJobVOs.length; i++) {
				psnJobVOs[i].setPk_psnorg(strPk_psnorg);
				psnJobVOs[i].setPk_psndoc(strPk_psndoc);
				// �ҵ�����������,��������ʱ��������������¼������������Ϣ��
				if (psnJobVOs[i].getLastflag().booleanValue()) {
					strPk_psnjob = psnJobVOs[i].getPrimaryKey() == null ? getNextOid()
							: psnJobVOs[i].getPrimaryKey();
					psnJobVOs[i].setPrimaryKey(strPk_psnjob);
				}
			}
		}
		if (StringUtils.isBlank(strPk_psnjob)) {
			strPk_psnjob = psndocVO.getPsnJobVO().getPk_psnjob();
		}
		for (SuperVO superVO : psndocAggVO.getAllChildrenVO()) {
			if (!(superVO instanceof PsnOrgVO)
					&& superVO.getAttributeValue(PsnOrgVO.PK_PSNORG) == null) {
				superVO.setAttributeValue(PsnOrgVO.PK_PSNORG, strPk_psnorg);
			}
			if (!(superVO instanceof PsnJobVO) && !(superVO instanceof WorkVO)
					&& superVO.getAttributeValue(PsnJobVO.PK_PSNJOB) == null) {
				superVO.setAttributeValue(PsnJobVO.PK_PSNJOB, strPk_psnjob);
			}
		}
		MDPersistenceService.lookupPersistenceService().saveBillWithRealDelete(
				psndocAggVO);

		updateDataAfterSubDataChanged(psndocAggVO);

		// ssx added on 2018-03-20
		// for auto NHI enddate
		PsndocNHIDAO.dealNHIDates(psndocAggVO);

		PsndocAggVO aggVO = NCLocator.getInstance()
				.lookup(IPsndocQryService.class)
				.queryPsndocVOByPsnjobPk(strPk_psnjob);
		if (aggVO == null) {
			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0233")/*
									 * @ res "��Ա��ɾ��,��ˢ������"
									 */);
		}

		// queryPsndocVOByPks(true, getPkpsnjobByPkpsndoc(strPk_psndoc));
		return aggVO == null ? null : aggVO.getParentVO();
	}

	private PsndocVO saveNoNewPsnAndPsnOrgForImport(PsndocAggVO psndocAggVO,
			PsndocVO psndocVO, PsnJobVO[] psnJobVOs) throws MetaDataException,
			BusinessException {
		String strPk_psndoc = psndocVO.getPk_psndoc();
		String strPk_psnorg = psndocAggVO.getParentVO().getPsnOrgVO()
				.getPk_psnorg();
		PsnOrgVO[] psnOrgVOs = (PsnOrgVO[]) psndocAggVO.getTableVO(PsnOrgVO
				.getDefaultTableName());
		// �����µĹ�����¼Ҫ������֯��ϵpk
		String strPk_psnjob = null;
		if (psnJobVOs != null && psnJobVOs.length > 0) {
			for (int i = 0; i < psnJobVOs.length; i++) {
				UFLiteralDate jobBegindate = psnJobVOs[i].getBegindate();
				// ��Ϊ��֯��ϵ�Ľ������ڱ���ְ��ʼ������һ�죬�������Ϊ��ְ������¼����ʼ���ڼ�һ��
				if (psnJobVOs[i].getTrnsevent() == 4) {
					jobBegindate = jobBegindate.getDateBefore(1);
				}

				for (PsnOrgVO psnOrgVO : psnOrgVOs) {
					UFLiteralDate orgBegindate = psnOrgVO.getBegindate();
					UFLiteralDate orgEnddate = psnOrgVO.getEnddate();
					// ���ֻ��һ����֯��ϵ������û��¼����֯��ϵʱ����֯��ϵ�������һ��������¼���ɣ�
					if (psnOrgVOs.length == 1) {
						psnJobVOs[i].setPk_psnorg(psnOrgVO.getPrimaryKey());
						break;
					}

					// �ж�����֯��ϵ�����
					if (psnJobVOs[i].getPk_org().equals(psnOrgVO.getPk_org())) {
						if (orgEnddate != null
								&& !"".equals(orgEnddate.toString())) {
							// ��֯��ϵ��ʼ����<=������¼��ʼ����<=��֯��ϵ��������
							if ((jobBegindate.after(orgBegindate) || jobBegindate
									.equals(orgBegindate))
									&& (jobBegindate.before(orgEnddate) || jobBegindate
											.equals(orgEnddate))) {
								psnJobVOs[i].setPk_psnorg(psnOrgVO
										.getPrimaryKey());
								break;
							}
						} else if (jobBegindate.after(orgBegindate)
								|| jobBegindate.equals(orgBegindate)) { // ������¼��ʼ����>=��֯��ϵ��ʼ����
							psnJobVOs[i].setPk_psnorg(psnOrgVO.getPrimaryKey());
							break;
						}
					}
				}

				psnJobVOs[i].setPk_psndoc(strPk_psndoc);
				// �ҵ�����������,��������ʱ��������������¼������������Ϣ��
				if (psnJobVOs[i].getLastflag().booleanValue()) {
					strPk_psnorg = psnJobVOs[i].getPk_psnorg();
					strPk_psnjob = psnJobVOs[i].getPrimaryKey() == null ? getNextOid()
							: psnJobVOs[i].getPrimaryKey();
					psnJobVOs[i].setPrimaryKey(strPk_psnjob);
				}
			}
		}
		if (StringUtils.isBlank(strPk_psnjob)) {
			strPk_psnjob = psndocVO.getPsnJobVO().getPk_psnjob();
		}
		for (SuperVO superVO : psndocAggVO.getAllChildrenVO()) {
			if (!(superVO instanceof PsnOrgVO)
					&& superVO.getAttributeValue(PsnOrgVO.PK_PSNORG) == null) {
				superVO.setAttributeValue(PsnOrgVO.PK_PSNORG, strPk_psnorg);
			}
			if (!(superVO instanceof PsnJobVO) && !(superVO instanceof WorkVO)
					&& superVO.getAttributeValue(PsnJobVO.PK_PSNJOB) == null) {
				superVO.setAttributeValue(PsnJobVO.PK_PSNJOB, strPk_psnjob);
			}
		}
		MDPersistenceService.lookupPersistenceService().saveBillWithRealDelete(
				psndocAggVO);

		updateDataAfterSubDataChanged(psndocAggVO);
		PsndocAggVO aggVO = NCLocator.getInstance()
				.lookup(IPsndocQryService.class)
				.queryPsndocVOByPsnjobPk(strPk_psnjob);
		if (aggVO == null) {
			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0233")/*
									 * @ res "��Ա��ɾ��,��ˢ������"
									 */);
		}
		// queryPsndocVOByPks(true, getPkpsnjobByPkpsndoc(strPk_psndoc));
		return aggVO == null ? null : aggVO.getParentVO();
	}

	/**
	 * ���빤��ר�ñ�����Ա������ savePsndoc()������������ ȡ��PK�� ȡ��ʱ���У�� ȡ�������ύ ȡ����ѯ
	 * 
	 * @param psndocAggVO
	 * @param timerLogger
	 * @throws BusinessException
	 */
	public void savePsndocForImport(PsndocAggVO psndocAggVO,
			TimerLogger timerLogger) throws BusinessException {

		PsndocVO psndocVO = psndocAggVO.getParentVO();
		timerLogger.addLog("ǰ�¼���ʼ");
		// ǰ�¼�֪ͨ
		fireEventBefore(psndocVO);
		timerLogger.addLog("ǰ�¼�����");
		// ��������֤��
		// setCert(psndocVO, psndocAggVO);
		PsnJobVO oldPsnJobVO = null;
		timerLogger.addLog("��ѯ������¼�Ӽ���ʼ");
		if (psndocVO.getStatus() == VOStatus.UPDATED
				&& psndocVO.getPsnJobVO().getStatus() == VOStatus.UPDATED) {
			oldPsnJobVO = queryByPk(PsnJobVO.class, psndocVO.getPsnJobVO()
					.getPrimaryKey());
		}
		timerLogger.addLog("��ѯ������¼�Ӽ�����");
		timerLogger.addLog("�������ݿ�ʼ");
		// ����
		psndocVO = saveAggVOForImport(psndocAggVO);
		timerLogger.addLog("�������ݽ���");
		timerLogger.addLog("ͬ�����濪ʼ");
		// ͬ������
		HiCacheUtils.synCache(PsndocVO.getDefaultTableName(),
				PsnJobVO.getDefaultTableName(), PsnOrgVO.getDefaultTableName());
		timerLogger.addLog("ͬ���������");
		timerLogger.addLog("���¼���ʼ");
		// ���¼�֪ͨ
		fireEventAfter(psndocVO, oldPsnJobVO);
		timerLogger.addLog("���¼�����");

	}

	/**
	 * ������¼�֪ͨ
	 * 
	 * @param psndocVO
	 * @throws BusinessException
	 */
	private void fireEventAfter(PsndocVO psndocVO, PsnJobVO oldPsnJobVO)
			throws BusinessException {

		if (psndocVO.getStatus() == VOStatus.UPDATED
				&& psndocVO.getPsnJobVO().getStatus() == VOStatus.UPDATED) {
			oldPsnJobVO = queryByPk(PsnJobVO.class, psndocVO.getPsnJobVO()
					.getPrimaryKey());
		}
		// ���¼�֪ͨ
		if (psndocVO.getPsnOrgVO().getPsntype() != null
				&& psndocVO.getPsnJobVO().getBegindate() != null) {
			String strEventType = ObjectUtils.equals(psndocVO.getPsnOrgVO()
					.getPsntype(), PsnType.EMPLOYEE.value()) ? IHiEventType.SAVE_EMPLOYEE_AFTER
					: IHiEventType.SAVE_POI_AFTER;
			HiEventValueObject.fireEvent(oldPsnJobVO, psndocVO.getPsnJobVO(),
					psndocVO.getPsnJobVO().getPk_hrorg(),
					HICommonValue.MD_ID_PSNDOC, strEventType);
			HiEventValueObject
					.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
		}
	}

	/**
	 * ��������֤
	 */
	private void setCert(PsndocVO psndocVO, PsndocAggVO psndocAggVO) {
		boolean isNew = psndocVO.getStatus() == VOStatus.NEW;

		// �����޸�ʱ���������֤
		if (!isNew) {
			CertVO[] certVOs = (CertVO[]) psndocAggVO.getTableVO(CertVO
					.getDefaultTableName());
			if (certVOs != null) {
				for (CertVO certVO : certVOs) {
					if (certVO.getIdtype().equals("1001Z01000000000AI36")
							&& certVO.getIseffect().booleanValue() == true
							&& certVO.getIsstart().booleanValue() == true) {
						psndocAggVO.getParentVO().setId(certVO.getId());
						generateGenderAndBirthdayFromID(psndocAggVO
								.getParentVO());

					}
				}
			}
		}
	}

	/**
	 * ǰ�¼�֪ͨ
	 * 
	 * @throws BusinessException
	 */
	private void fireEventBefore(PsndocVO psndocVO) throws BusinessException {
		if (psndocVO.getPsnOrgVO().getPsntype() != null
				&& psndocVO.getPsnJobVO().getBegindate() != null) {
			String strEventType = ObjectUtils.equals(psndocVO.getPsnOrgVO()
					.getPsntype(), PsnType.EMPLOYEE.value()) ? IHiEventType.SAVE_EMPLOYEE_BEFORE
					: IHiEventType.SAVE_POI_BEFORE;
			HiEventValueObject.fireEvent(
					psndocVO.getStatus() == VOStatus.NEW ? null : psndocVO
							.getPsnJobVO(), psndocVO.getPsnJobVO(), psndocVO
							.getPsnJobVO().getPk_hrorg(),
					HICommonValue.MD_ID_PSNDOC, strEventType);
		}
	}

	/***************************************************************************
	 * ��������������أ�����ȡ�ӱ�����ʱ���и��ӵ�������ϵ����˴˴�ֻ�������棬��ѯ���⴦����
	 * �ӽ�����ȡ����PsndocAggVO�������������ϰ��õı�����<br>
	 * Created on 2010-1-30 15:02:50<br>
	 * 
	 * @param psndocAggVO
	 * @return PsndocVO
	 * @throws BusinessException
	 * @author Rocex Wang
	 ***************************************************************************/
	public PsndocAggVO savePsndoc(PsndocAggVO psndocAggVO)
			throws BusinessException {
		PsndocVO psndocVO = psndocAggVO.getParentVO();
		// ����
		getLocker().lock(UPDATE, psndocVO);
		// Ψһ��У��
		// BDUniqueRuleValidate ruleValidate = new BDUniqueRuleValidate();
		// DefaultValidationService validationService =
		// createValidationService(ruleValidate);
		// validationService.validate(psndocVO);
		// �汾У�飨ʱ���У�飩
		BDVersionValidationUtil.validateSuperVO(psndocVO);
		// // ���������Ϣ
		// �˴������������Ϣ ,��Ϊ�˴���vo״̬�Ǵ���
		// setAuditInfoAndTs(psndocAggVO);
		// ǰ�¼�֪ͨ
		if (psndocVO.getPsnOrgVO().getPsntype() != null
				&& psndocVO.getPsnJobVO().getBegindate() != null) {
			String strEventType = ObjectUtils.equals(psndocVO.getPsnOrgVO()
					.getPsntype(), PsnType.EMPLOYEE.value()) ? IHiEventType.SAVE_EMPLOYEE_BEFORE
					: IHiEventType.SAVE_POI_BEFORE;
			HiEventValueObject.fireEvent(
					psndocVO.getStatus() == VOStatus.NEW ? null : psndocVO
							.getPsnJobVO(), psndocVO.getPsnJobVO(), psndocVO
							.getPsnJobVO().getPk_hrorg(),
					HICommonValue.MD_ID_PSNDOC, strEventType);
		}
		PsnJobVO oldPsnJobVO = null;
		if (psndocVO.getStatus() == VOStatus.UPDATED
				&& psndocVO.getPsnJobVO().getStatus() == VOStatus.UPDATED) {
			oldPsnJobVO = queryByPk(PsnJobVO.class, psndocVO.getPsnJobVO()
					.getPrimaryKey());
		}

		boolean isNew = psndocVO.getStatus() == VOStatus.NEW;

		// �����޸�ʱ���������֤
		if (!isNew) {
			CertVO[] certVOs = (CertVO[]) psndocAggVO.getTableVO(CertVO
					.getDefaultTableName());
			if (certVOs != null) {
				for (CertVO certVO : certVOs) {
					if (certVO.getIdtype().equals("1001Z01000000000AI36")
							&& certVO.getIseffect().booleanValue() == true
							&& certVO.getIsstart().booleanValue() == true) {
						psndocAggVO.getParentVO().setId(certVO.getId());
						generateGenderAndBirthdayFromID(psndocAggVO
								.getParentVO());

					}
				}
			}
		} else {
			// ��Ա��������
			BillCodeContext billCodeContext = HiCacheUtils.getBillCodeContext(
					HICommonValue.NBCR_PSNDOC_CODE, PubEnv.getPk_group(),
					psndocVO.getPsnJobVO().getPk_hrorg());

			String[] billCodes = null;
			// ����뷽ʽʱ������Ա����ţ����Ÿ���ʱ����
			boolean isLeveled = (billCodeContext != null
					&& !billCodeContext.isPrecode() && JFCommonValue.LEVELED_CODE_TMP
					.equals(psndocVO.getCode()));
			if (isLeveled) {
				IHrBillCode service = NCLocator.getInstance().lookup(
						IHrBillCode.class);
				billCodes = service.getLeveledBillCode(
						HICommonValue.NBCR_PSNDOC_CODE, PubEnv.getPk_group(),
						psndocVO.getPsnJobVO().getPk_hrorg(),
						psndocVO.getPsnJobVO(), 1);
				psndocVO.setCode(billCodes[0]);

				// �����Ա����ȫ��Ψһ---�������Ϊ��У����Ա��������ȫ��Ψһ
				checkPsnCodeUnique(psndocAggVO.getParentVO());
			}

			// Ա���ź����
			BillCodeContext billCodeContext_clerkcode = HiCacheUtils
					.getBillCodeContext(HICommonValue.NBCR_PSNDOC_CLERKCODE,
							PubEnv.getPk_group(), psndocVO.getPsnJobVO()
									.getPk_hrorg());
			String[] clerkcodes = null;
			// ����뷽ʽʱ����Ա���ţ����Ÿ���ʱ����
			boolean isLeveled_clerkcode = (billCodeContext_clerkcode != null
					&& !billCodeContext_clerkcode.isPrecode() && JFCommonValue.LEVELED_CODE_TMP
					.equals(psndocVO.getPsnJobVO().getClerkcode()));
			if (isLeveled_clerkcode) {
				IHrBillCode service = NCLocator.getInstance().lookup(
						IHrBillCode.class);
				clerkcodes = service.getLeveledBillCode(
						HICommonValue.NBCR_PSNDOC_CLERKCODE, PubEnv
								.getPk_group(), psndocVO.getPsnJobVO()
								.getPk_hrorg(), psndocVO.getPsnJobVO(), 1);
				// psndocVO.getPsnJobVO().setClerkcode(clerkcodes[0]);

				// ��ְ�Ǽǣ���ְ��¼��Բ�¼����ʷ������¼���ݵ�Ա���ź����µ���ְ��¼Ա����ȫ��һ��
				PsnJobVO[] psnjobvos = (PsnJobVO[]) psndocAggVO
						.getTableVO(PsnJobVO.getDefaultTableName());
				if (!ArrayUtils.isEmpty(psnjobvos)) {
					for (PsnJobVO psnJobVO : psnjobvos) {
						if ((JFCommonValue.LEVELED_CODE_TMP).equals(psnJobVO
								.getClerkcode()))// ��Ҫ�Զ����ɵ�Ա���ż�Ĭ��ֵΪ.�Ż����ֵ
						{
							psnJobVO.setClerkcode(clerkcodes[0]);
						}
					}
				}

				// ��ְ�Ǽǣ�'��ְ��¼'�Ӽ�������Ϣ���򽫹�����¼��Ա���Ÿ�ֵ����ְ��¼
				SuperVO[] partjobVOs = psndocAggVO.getTableVO(PartTimeVO
						.getDefaultTableName());
				if (partjobVOs != null && partjobVOs.length > 0) {
					for (int i = 0; i < partjobVOs.length; i++) {
						((PartTimeVO) partjobVOs[i])
								.setClerkcode(clerkcodes[0]);
					}
				}

				// ���Ա������������ְ��֯�Ƿ�Ψһ---�������Ϊ��У��Ա���ź����
				checkClerkCodeUnique(psndocAggVO);
			}
		}

		psndocVO = saveAggVO(psndocAggVO);

		PsndocNHIDAO.dealNHIDates(psndocAggVO);
		// ͬ������
		HiCacheUtils.synCache(PsndocVO.getDefaultTableName(),
				PsnJobVO.getDefaultTableName(), PsnOrgVO.getDefaultTableName());

		// ���¼�֪ͨ
		if (psndocVO.getPsnOrgVO().getPsntype() != null
				&& psndocVO.getPsnJobVO().getBegindate() != null) {
			String strEventType = ObjectUtils.equals(psndocVO.getPsnOrgVO()
					.getPsntype(), PsnType.EMPLOYEE.value()) ? IHiEventType.SAVE_EMPLOYEE_AFTER
					: IHiEventType.SAVE_POI_AFTER;
			HiEventValueObject.fireEvent(oldPsnJobVO, psndocVO.getPsnJobVO(),
					psndocVO.getPsnJobVO().getPk_hrorg(),
					HICommonValue.MD_ID_PSNDOC, strEventType);
			HiEventValueObject
					.fireDataPermChangeEvent(HICommonValue.MD_ID_PSNJOB);
		}

		if (isNew) {
			IHrBillCode service = NCLocator.getInstance().lookup(
					IHrBillCode.class);
			BillCodeContext billCodeContext = HiCacheUtils.getBillCodeContext(
					HICommonValue.NBCR_PSNDOC_CODE, PubEnv.getPk_group(),
					psndocVO.getPsnJobVO().getPk_hrorg());

			if (billCodeContext != null) {

				service.commitPreBillCode(HICommonValue.NBCR_PSNDOC_CODE,
						PubEnv.getPk_group(), psndocVO.getPsnJobVO()
								.getPk_hrorg(), psndocVO.getCode());
			}

			billCodeContext = HiCacheUtils.getBillCodeContext(
					HICommonValue.NBCR_PSNDOC_CLERKCODE, PubEnv.getPk_group(),
					psndocVO.getPsnJobVO().getPk_hrorg());
			if (billCodeContext != null) {
				service.commitPreBillCode(HICommonValue.NBCR_PSNDOC_CLERKCODE,
						PubEnv.getPk_group(), psndocVO.getPsnJobVO()
								.getPk_hrorg(), psndocVO.getPsnJobVO()
								.getClerkcode());
			}
		}

		PsndocAggVO aggVO = NCLocator.getInstance()
				.lookup(IPsndocQryService.class)
				.queryPsndocVOByPsnjobPk(psndocVO.getPsnJobVO().getPk_psnjob());
		return aggVO == null ? null : aggVO;
	}

	private void generateGenderAndBirthdayFromID(PsndocVO psndocVO) {
		if (psndocVO == null) {
			return;
		}
		String id = psndocVO.getId();
		String idtype = psndocVO.getIdtype();
		if (idtype == null) {
			idtype = "1001Z01000000000AI36";
		}
		if (id == null || id.length() < 1
				|| !idtype.equals("1001Z01000000000AI36")) {
			return;
		}
		if (null == psndocVO.getSex()) {
			psndocVO.setSex(getSex(id));
		}
		UFLiteralDate birthday = null;
		try {
			birthday = getBirthdate(id) == null ? null : UFLiteralDate
					.getDate(getBirthdate(id));
		} catch (Exception e) {
			birthday = null;
		}
		// ���û������˳������ڣ��Ͳ���������֤��������
		if (null == psndocVO.getBirthdate()) {
			psndocVO.setBirthdate(birthday);
		}
	}

	private String getBirthdate(String ID) {
		if (ID.length() != 15 && ID.length() != 18) {
			// ����15λ��18λ����null
			return null;
		}
		String birth = ID.length() == 15 ? "19" + ID.substring(6, 12) : ID
				.substring(6, 14);
		String year = birth.substring(0, 4);
		String month = birth.substring(4, 6);
		String date = birth.substring(6);
		return year + "-" + month + "-" + date;
	}

	private Integer getSex(String ID) {
		if (ID.length() != 15 && ID.length() != 18) {
			return null;
		}
		int isex = 2;
		isex = ID.length() == 15 ? Integer.parseInt(ID.substring(14)) : Integer
				.parseInt(ID.substring(16, 17));
		return isex % 2 == 0 ? 2 : 1;
	}

	/***************************************************************************
	 * <br>
	 * Created on 2010-5-25 19:12:18<br>
	 * 
	 * @param psndocAggVO
	 * @author Rocex Wang
	 ***************************************************************************/
	public void setAuditInfoAndTs(PsndocAggVO psndocAggVO) {
		PsndocVO psndocVO = null;
		if (psndocAggVO == null
				|| (psndocVO = psndocAggVO.getParentVO()) == null) {
			return;
		}
		setAuditInfo(psndocVO, true);
		if (psndocVO.getPsnOrgVO() != null) {
			setAuditInfo(psndocVO.getPsnOrgVO(), true);
		}
		if (psndocVO.getPsnJobVO() != null) {
			setAuditInfo(psndocVO.getPsnJobVO(), true);
		}
		SuperVO superVOs[] = psndocAggVO.getAllChildrenVO();
		if (superVOs != null && superVOs.length > 0) {

			for (SuperVO superVO : superVOs) {
				if (VOStatus.UNCHANGED == superVO.getStatus()
						|| VOStatus.DELETED == superVO.getStatus()) {
					continue;
				}
				setAuditInfo(superVO, true);
			}
		}
	}

	private void setAuditInfo(SuperVO vo, boolean blProcessAuditInfo) {
		if (!blProcessAuditInfo) {
			return;
		}

		switch (vo.getStatus()) {
		case VOStatus.NEW:
			AuditInfoUtil.addData(vo);
			if (PubEnv.UAP_USER.equals(vo.getAttributeValue("creator"))) {
				vo.setAttributeValue("creator", INCSystemUserConst.NC_USER_PK);
			}
			break;
		case VOStatus.UPDATED:
			AuditInfoUtil.updateData(vo);
			if (PubEnv.UAP_USER.equals(vo.getAttributeValue("modifier"))) {
				vo.setAttributeValue("modifier", INCSystemUserConst.NC_USER_PK);
			}
			break;
		default:
			Logger.error("VO״̬����ȷ");
		}
	}

	private void updateDataAfterSubDataChanged(PsndocAggVO psndocAggVO)
			throws BusinessException {
		String pk_psndoc = psndocAggVO.getParentVO().getPk_psndoc();
		for (int i = 0; i < psndocAggVO.getTableCodes().length; i++) {
			SuperVO subVOs[] = psndocAggVO.getTableVO(psndocAggVO
					.getTableCodes()[i]);
			if (subVOs != null) {
				updateDataAfterSubDataChanged(psndocAggVO.getTableCodes()[i],
						pk_psndoc);
			}
		}
	}

	/***************************************************************************
	 * �ӱ����ݱ仯�����������Ϣ<br>
	 * 
	 * @param subtablename
	 * @param pk_psndoc
	 * @throws BusinessException
	 * @author Rocex Wang
	 ***************************************************************************/
	@SuppressWarnings("unchecked")
	public void updateDataAfterSubDataChanged(String subtablename,
			String... pk_psndoc) throws BusinessException {

		if (pk_psndoc == null || pk_psndoc.length == 0) {
			return;
		}

		// ҵ���Ӽ���tabCode
		final String[] busiSubset = PsndocAggVO.hashBusinessInfoSet
				.toArray(new String[0]);
		// new String[] { PsnOrgVO.getDefaultTableName(),
		// PsnJobVO.getDefaultTableName(), PartTimeVO.getDefaultTableName(),
		// TrialVO.getDefaultTableName(), PsnChgVO.getDefaultTableName(),
		// CtrtVO.getDefaultTableName(), RetireVO.getDefaultTableName(),
		// "hi_psndoc_training", "hi_psndoc_ass" };

		// ��ѯ��Ϣ��
		String condition = " pk_infoset_sort = '"
				+ HICommonValue.PSNDOC_INFOSET_SORT_PK + "' ";
		if (PartTimeVO.getDefaultTableName().equals(subtablename)) {
			condition += " and infoset_code = '" + subtablename + "' ";
		} else {
			condition += " and table_code = '" + subtablename
					+ "' and infoset_code <> '"
					+ PartTimeVO.getDefaultTableName() + "' ";
		}

		InfoSetVO[] set = (InfoSetVO[]) getRetrieve().retrieveByClause(null,
				InfoSetVO.class, condition);
		if (set == null || set.length == 0) {
			return;
		}

		// ��ѯ�ж�Ӧ������Ϣ�����Ϣ��
		InfoItemVO[] items = (InfoItemVO[]) getRetrieve().retrieveByClause(
				null,
				InfoItemVO.class,
				" pk_infoset = '" + set[0].getPk_infoset()
						+ "' and isnull(pk_main_item,'~') <> '~' ");
		if (items == null || items.length == 0) {
			return;
		}

		HashMap<String, InfoItemVO> itemMap = new HashMap<String, InfoItemVO>();
		// ���ӱ���Ϣ����������Ϣ���Ӧ����
		// for (int i = 0; items != null && i < items.length; i++)
		for (InfoItemVO iivo : items) {
			if (StringUtils.isBlank(iivo.getPk_main_item())) {
				continue;
			}
			itemMap.put(iivo.getPk_main_item(), iivo);
			// InfoItemVO mainitem = (InfoItemVO)
			// getRetrieve().retrieveByPk(null, InfoItemVO.class,
			// items[i].getPk_main_item());
			// if (mainitem == null)
			// {
			// // �������Ӧ������Ϣ��
			// continue;
			// }
			// subItemMainItemMap.put(items[i], mainitem);
		}
		HashMap<InfoItemVO, InfoItemVO> subItemMainItemMap = new HashMap<InfoItemVO, InfoItemVO>();
		InSQLCreator isc = new InSQLCreator();
		String insql = isc.getInSQL(items, "pk_main_item");
		String cond = "pk_infoset_item in (" + insql + ")";
		InfoItemVO[] mainvos = null;

		PersistenceDAO pdao = new PersistenceDAO();
		try {
			mainvos = (InfoItemVO[]) pdao.retrieveByClause(InfoItemVO.class,
					cond);
		} catch (PersistenceDbException e1) {
			// TODO Auto-generated catch block
			throw new BusinessException(e1.getMessage(), e1);
		}
		if (!ArrayUtils.isEmpty(mainvos)) {
			for (InfoItemVO mainvo : mainvos) {
				InfoItemVO tvo = itemMap.get(mainvo.getPk_infoset_item());
				if (tvo != null) {
					subItemMainItemMap.put(tvo, mainvo);
				}
			}
		}

		if (subItemMainItemMap.isEmpty()) {
			return;
		}

		// �õ��Ӽ���VO class
		Class<? extends SuperVO> cla = null;
		try {
			cla = (Class<? extends SuperVO>) Class.forName(set[0]
					.getVo_class_name());
		} catch (ClassNotFoundException e) {
			Logger.error(e.getMessage(), e);
			return;
		}

		String tableName = null;
		try {
			tableName = cla.newInstance().getTableName();
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			return;
		}

		ArrayList<PsndocVO> al = new ArrayList<PsndocVO>();
		ArrayList<String> alMainFld = new ArrayList<String>();
		HashMap<String, PsndocVO> psndocMap = new HashMap<String, PsndocVO>();
		String insql2 = new InSQLCreator().getInSQL(pk_psndoc);
		StringBuffer cond2 = new StringBuffer();
		cond2.append("pk_psndoc in (").append(insql2).append(")");
		PsndocVO[] psndocvos = null;
		;
		try {
			psndocvos = (PsndocVO[]) pdao.retrieveByClause(PsndocVO.class,
					cond2.toString());
		} catch (PersistenceDbException e) {
			// TODO Auto-generated catch block
			throw new BusinessException(e.getMessage(), e);
		}
		if (!ArrayUtils.isEmpty(psndocvos)) {
			for (PsndocVO psndocvo : psndocvos) {
				psndocMap.put(psndocvo.getPk_psndoc(), psndocvo);
			}
		}
		// ��ѯ������Ŀ��ֵ
		HashMap<String, Object[]> valueMap = new HashMap<String, Object[]>();
		for (InfoItemVO item : subItemMainItemMap.keySet()) {
			if (!alMainFld
					.contains(subItemMainItemMap.get(item).getItem_code())) {
				alMainFld.add(subItemMainItemMap.get(item).getItem_code());
			}
			String where = "";

			if (ArrayUtils.contains(busiSubset, subtablename)) {
				where = " pk_psnorg in ( select pk_psnorg from hi_psnorg where pk_psndoc in ("
						+ insql2 + ")) ";
			} else {
				where = " pk_psndoc in (" + insql2 + ") ";
			}

			if (!StringUtils.isBlank(item.getSub_formula_sql())) {
				where = where + " and " + item.getSub_formula_sql();

			} else {
				// ���û�ж����ӱ���Ϣ�ʽ,���ѯ����һ��������ֵ
				if (PartTimeVO.getDefaultTableName().equals(subtablename)) {
					// ��ְ
					where = where
							+ " and assgid > 1 and recordnum = 0 order by assgid desc ";
				} else if (PsnJobVO.getDefaultTableName().equals(subtablename)) {
					// ������¼
					where = where + " and assgid = 1 and recordnum = 0 ";
				} else if (ArrayUtils.contains(busiSubset, subtablename)) {
					// ��ְ��ְ���ҵ���Ӽ�
					where = where + " and recordnum = 0 ";
				} else {
					// ��ҵ���Ӽ�
					where = where + " and lastflag = 'Y' ";
				}
			}

			Object subValue = null;
			List<Object[]> vlist = null;
			try {
				vlist = (List<Object[]>) new BaseDAO().executeQuery(
						" select pk_psndoc," + item.getItem_code() + " from "
								+ tableName + " where  " + where,
						new ArrayListProcessor());
				for (Object[] objs : vlist) {
					String pkpsn = objs[0].toString();
					Object[] tobjs = new Object[2];
					tobjs[0] = subItemMainItemMap.get(item).getItem_code();
					tobjs[1] = objs[1];
					valueMap.put(pkpsn, tobjs);
				}
			} catch (DAOException daoex) {
				// ����û��ͬ�������ݿ��е��ֶ����쳣���ж�
				Logger.error(daoex.getMessage(), daoex);
			}
			// ��ֵ����Ӧ���Ƶ��⣬�ӱ����ж���ͬ����ͬ����ͬһ���ˣ���ѧ��
			// ���������е�doc���Ǵ�map��ȡ�ģ�al�����ֵ��ʵ��һ����ַ���Կ������������
			for (String docPK : pk_psndoc) {
				PsndocVO doc = psndocMap.get(docPK);// queryByPk(PsndocVO.class,
													// docPK, true);
				if (doc == null) {
					continue;
				}
				Object[] value = valueMap.get(docPK);
				// Ϊ�������
				if (ArrayUtils.isEmpty(value)) {
					doc.setAttributeValue(subItemMainItemMap.get(item)
							.getItem_code(), null);
					al.add(doc);
					continue;
				}
				doc.setAttributeValue((String) value[0], value[1]);
				al.add(doc);
			}

		}

		if (al.size() > 0) {
			baseDAOManager.updateVOArray(al.toArray(new PsndocVO[0]),
					alMainFld.toArray(new String[0]));
		}
	}

	private IPersistenceRetrieve getRetrieve() {
		if (retrieve == null) {
			retrieve = NCLocator.getInstance().lookup(
					IPersistenceRetrieve.class);
		}
		return retrieve;
	}

	private IPersonRecordService getPsnrdsService() {
		if (psnrdsService == null) {
			psnrdsService = NCLocator.getInstance().lookup(
					IPersonRecordService.class);
		}
		return psnrdsService;
	}

	public PsnJobVO[] queryPsndocVOsByDeptPK(String pkDept,
			boolean includeSubDept, FromWhereSQL fromWhereSQL)
			throws BusinessException {

		String deptSql = "";
		if (!includeSubDept) {
			deptSql = " select pk_dept from org_dept where pk_dept = '"
					+ pkDept + "'";
		} else {
			DeptVO dept = queryByPk(DeptVO.class, pkDept, true);
			deptSql = " select pk_dept from org_dept where innercode like '"
					+ dept.getInnercode() + "%' ";
		}

		Map<String, String> aliasMap = fromWhereSQL.getAliasMap();

		String jobAlias = aliasMap == null
				|| aliasMap.get(PsnJobVO.getDefaultTableName()) == null ? PsnJobVO
				.getDefaultTableName() : aliasMap.get(PsnJobVO
				.getDefaultTableName());
		String orgAlias = aliasMap == null
				|| aliasMap.get(PsnOrgVO.getDefaultTableName()) == null ? PsnOrgVO
				.getDefaultTableName() : aliasMap.get(PsnOrgVO
				.getDefaultTableName());

		String sql = "select " + jobAlias
				+ ".* from bd_psndoc  bd_psndoc inner join hi_psnorg "
				+ orgAlias + " on bd_psndoc.pk_psndoc = " + orgAlias
				+ ".pk_psndoc and " + orgAlias + ".lastflag ='Y' and "
				+ orgAlias + ".endflag = 'N' and " + orgAlias
				+ ".psntype = 0 and " + orgAlias
				+ ".indocflag ='Y' inner join hi_psnjob " + jobAlias + " on "
				+ orgAlias + ".pk_psnorg = " + jobAlias + ".pk_psnorg and "
				+ jobAlias + ".lastflag ='Y' and " + jobAlias
				+ ".endflag ='N' ";

		if (aliasMap != null && aliasMap.keySet().size() > 0) {
			for (String key : aliasMap.keySet().toArray(new String[0])) {
				if (ArrayUtils.contains(
						new String[] { PsndocVO.getDefaultTableName(),
								PsnJobVO.getDefaultTableName(),
								PsnOrgVO.getDefaultTableName(), "." }, key)) {
					continue;
				}
				String tabAlias = aliasMap.get(key);
				if (PsndocAggVO.hashBusinessInfoSet.contains(key)) {
					// ҵ���Ӽ�
					sql += " inner join " + key + " " + tabAlias + " on "
							+ tabAlias + ".pk_psnorg = " + orgAlias
							+ ".pk_psnorg ";
				} else {
					// ��ҵ���Ӽ�
					sql += " left outer join " + key + " " + tabAlias + " on "
							+ tabAlias + ".pk_psndoc = bd_psndoc.pk_psndoc ";
				}
			}
		}
		sql += " where "
				+ jobAlias
				+ ".pk_dept in ("
				+ deptSql
				+ ") "
				+ (StringUtils.isBlank(fromWhereSQL.getWhere()) ? ""
						: (" and " + fromWhereSQL.getWhere()));

		if (fromWhereSQL.getOrderBy() != null)
			sql += " order by " + fromWhereSQL.getOrderBy();

		ArrayList<PsnJobVO> al = (ArrayList<PsnJobVO>) baseDAOManager
				.executeQuery(sql, new BeanListProcessor(PsnJobVO.class));
		return al == null ? null : al.toArray(new PsnJobVO[0]);
	}

	public PsnJobVO[] queryPsndocVOsByPsnInfo(String psnCode, String psnName,
			String eMail, String phoneNo, String deptName, String pkGroup)
			throws BusinessException {
		String sql = "select hi_psnjob.* from bd_psndoc "
				+ "inner join hi_psnorg on bd_psndoc.pk_psndoc = hi_psnorg.pk_psndoc and hi_psnorg.lastflag ='Y' and hi_psnorg.endflag = 'N' and hi_psnorg.psntype = 0 and hi_psnorg.indocflag ='Y' "
				+ "inner join hi_psnjob on hi_psnorg.pk_psnorg = hi_psnjob.pk_psnorg and hi_psnjob.lastflag ='Y' and hi_psnjob.endflag ='N' "
				+ "left outer join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept where 1=1 and hi_psnjob.pk_group = '"
				+ pkGroup + "' ";
		if (!StringUtils.isBlank(psnCode)) {
			sql += " and bd_psndoc.code  like '%" + psnCode.trim() + "%' ";
		}

		if (!StringUtils.isBlank(eMail)) {
			sql += " and bd_psndoc.email  like '%" + eMail.trim() + "%' ";
		}

		if (!StringUtils.isBlank(phoneNo)) {
			sql += " and bd_psndoc.officephone  like '%" + phoneNo.trim()
					+ "%' ";
		}

		if (!StringUtils.isBlank(psnName)) {
			sql += " and (bd_psndoc.name  like '%" + psnName.trim()
					+ "%' or bd_psndoc.name2  like '%" + psnName.trim()
					+ "%' or bd_psndoc.name3  like '%" + psnName.trim()
					+ "%' or bd_psndoc.name4  like '%" + psnName.trim()
					+ "%' or bd_psndoc.name5  like '%" + psnName.trim()
					+ "%' or bd_psndoc.name6 like '%" + psnName.trim() + "%')";
		}

		if (!StringUtils.isBlank(deptName)) {
			sql += " and ( org_dept.name  like '%" + deptName.trim()
					+ "%' or org_dept.name2  like '%" + deptName.trim()
					+ "%' or org_dept.name3  like '%" + deptName.trim()
					+ "%' or org_dept.name4  like '%" + deptName.trim()
					+ "%' or org_dept.name5  like '%" + deptName.trim()
					+ "%' or org_dept.name6  like '%" + deptName.trim()
					+ "%' )";
		}

		ArrayList<PsnJobVO> al = (ArrayList<PsnJobVO>) baseDAOManager
				.executeQuery(sql, new BeanListProcessor(PsnJobVO.class));
		return al == null ? null : al.toArray(new PsnJobVO[0]);
	}

	/**
	 * ���Ա������������ְ��֯�Ƿ�Ψһ
	 * 
	 * @param psndocAggVO
	 * @throws BusinessException
	 */
	public void checkClerkCodeUnique(PsndocAggVO psndocAggVO)
			throws BusinessException {
		SuperVO[] mainjobVOs = psndocAggVO.getTableVO(PsnJobVO
				.getDefaultTableName());
		SuperVO[] partjobVOs = psndocAggVO.getTableVO(PartTimeVO
				.getDefaultTableName());
		Object[] allVO = ArrayUtils.addAll(mainjobVOs, partjobVOs);
		for (int i = 0; allVO != null && i < allVO.length; i++) {
			if (allVO[i] == null) {
				continue;
			}
			PsnJobVO jobVO = (PsnJobVO) allVO[i];
			String clerkCode = jobVO.getClerkcode();
			String pk_org = jobVO.getPk_org();
			String pk_psndoc = psndocAggVO.getParentVO().getPk_psndoc();
			// ��ǰ��ְ��֯��������Ա������ͬ��Ա����
			String condition = " clerkcode = '" + NCESAPI.sqlEncode(clerkCode)
					+ "' and pk_org = '" + pk_org + "' and pk_psndoc <>'"
					+ pk_psndoc + "' ";
			int count = getRetrieve().getCountByCondition(
					PsnJobVO.getDefaultTableName(), condition);
			// if (count > 0)
			// {
			// throw new
			// BillCodeRepeatBusinessException(ResHelper.getString("6007psn",
			// "06007psn0234"/*
			// * @res
			// * " Ա����[{0}]����֯[{1}]���Ѵ���,����."
			// */, clerkCode,
			// VOUtils.getDocName(OrgVO.class, pk_org)), "clerkcoderepeat");
			// }

			// ��ְ�Ǽǽڵ��£����������Ϊǰ�����Ҳ��ɱ༭�������������ظ�ʱ���������½����ϵı��� add by yanglt
			// 20140807
			if (count > 0) {
				BillCodeRepeatBusinessException ex = new BillCodeRepeatBusinessException(
						ResHelper.getString("6007psn", "06007psn0234"/*
																	 * @res
																	 * " Ա����[{0}]����֯[{1}]���Ѵ���,����."
																	 */,
								clerkCode,
								VOUtils.getDocName(OrgVO.class, pk_org)),
						"clerkcoderepeat");

				BillCodeContext billcodeContext = NCLocator
						.getInstance()
						.lookup(IBillcodeManage.class)
						.getBillCodeContext(
								HICommonValue.NBCR_PSNDOC_CLERKCODE,
								PubEnv.getPk_group(), jobVO.getPk_org());
				if (null != billcodeContext && billcodeContext.isPrecode()
						&& !billcodeContext.isEditable()
						&& StringUtils.isNotBlank(clerkCode)) {
					String strClerkCode = null;
					NCLocator
							.getInstance()
							.lookup(IBillcodeManage.class)
							.AbandonBillCode_RequiresNew(
									HICommonValue.NBCR_PSNDOC_CLERKCODE,
									PubEnv.getPk_group(), jobVO.getPk_org(),
									clerkCode);
					strClerkCode = NCLocator
							.getInstance()
							.lookup(IHrBillCode.class)
							.getBillCode(HICommonValue.NBCR_PSNDOC_CLERKCODE,
									PubEnv.getPk_group(), jobVO.getPk_org());
					ex.setNbcrcode(HICommonValue.NBCR_PSNDOC_CLERKCODE);
					ex.setNewCode(strClerkCode);
				}

				throw ex;
			}

		}
	}

	/**
	 * �Ӽ�����,����Ψһ��У��,�������¼�,��У��ʱ���
	 * 
	 * @param <T>
	 * @param vo
	 * @param blChangeAuditInfo
	 * @return
	 * @throws BusinessException
	 */
	public <T> T update4SubSet(T vo, boolean blChangeAuditInfo,
			boolean blNeedReturnValue) throws BusinessException {
		SuperVO mainVO = getMainVO(vo);

		// ����
		getLocker().lock(UPDATE, mainVO);

		// �汾У�飨ʱ���У�飩
		// BDVersionValidationUtil.validateSuperVO(mainVO);

		// ���������Ϣ
		mainVO.setStatus(VOStatus.UPDATED);
		setAuditInfoAndTs(mainVO, blChangeAuditInfo);

		getMDPersistenceService().saveBillWithRealDelete(vo);

		return blNeedReturnValue ? queryByPk((Class<T>) vo.getClass(),
				mainVO.getPrimaryKey(), true) : null;
	}

	/**
	 * �Ӽ����� ,����Ψһ��У��,�������¼�
	 * 
	 * @param <T>
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public <T> T insert4SubSet(T vo, boolean blNeedReturnValue)
			throws BusinessException {
		SuperVO mainVO = getMainVO(vo);
		// ���������Ϣ
		mainVO.setStatus(VOStatus.NEW);
		setAuditInfoAndTs(mainVO, true);
		String pk = getMDPersistenceService().saveBill(vo);

		return blNeedReturnValue ? queryByPk((Class<T>) vo.getClass(), pk, true)
				: null;
	}

	/**
	 * У���Ĭ������У�����
	 * 
	 * @author yangshuo
	 * @Created on 2012-11-22 ����08:25:42
	 */

	public void checkPsnUserUniqueRules(PsndocVO psndocVO)
			throws BusinessException {
		// ������й���pk
		String[] rulePks = getPsnUniqueRulePks(true);

		if (rulePks != null) {
			String strMsgs = "";
			Map<String, HashMap<String, String>> ruleMap = getPsnUserUniqueRule(rulePks);
			for (String pk_rule : rulePks) {
				// ����У���ֶ������Ƿ�Ϊ�գ���������ֶ���һ������Ϊ����У��
				boolean isBank = false;
				String[] strUniqueFields = null;
				HashMap<String, String> map = ruleMap.get(pk_rule);
				strUniqueFields = map.keySet().toArray(new String[0]);

				for (String strUniqueField : strUniqueFields) {
					if (psndocVO.getAttributeValue(strUniqueField) == null) {
						isBank = true;
					}
				}
				if (!isBank) {
					String strMsg = checkPsnUserUniqueRule(psndocVO, map);

					if (!StringUtils.isEmpty(strMsg)) {
						strMsgs += strMsg + "\n";
					}
				}
			}
			if (!StringUtils.isEmpty(strMsgs)) {
				throw new BusinessException(strMsgs);
			}
		}
	}

	/**
	 * @param isContainDefault
	 *            �Ƿ����Ĭ�Ϲ���
	 * @return
	 */
	public String[] getPsnUniqueRulePks(boolean isContainDefault)
			throws BusinessException {
		String sql = "select pk_rule from bd_uniquerule where mdclassid ='"
				+ HICommonValue.MD_ID_UAP_PSNDOC
				+ "' and pk_rule <> '0001Z01000000005IQBL' ";
		// ����Ĭ�Ϲ���
		if (!isContainDefault)
			sql += " and isdefault = 'N'";

		GeneralVO[] rulePks = (GeneralVO[]) baseDAOManager.executeQuery(sql,
				new GeneralVOProcessor<GeneralVO>(GeneralVO.class));

		if (null == rulePks)
			return null;

		String[] result = new String[rulePks.length];

		for (int i = 0; i < result.length; i++)
			result[i] = (String) rulePks[i].getAttributeValue("pk_rule");

		return result;
	}

	/**
	 * ����һ�������У��
	 * 
	 * @author yangshuo
	 * @Created on 2012-11-23 ����09:16:25
	 */
	private String checkPsnUserUniqueRule(PsndocVO psndocVO,
			HashMap<String, String> rule) throws BusinessException {
		String[] strUniqueFields = rule.keySet().toArray(new String[0]);

		// ���У�鷶Χ�������������������ֶμ���
		if (ArrayUtils.contains(strUniqueFields, PsndocVO.NAME)) {
			strUniqueFields = (String[]) ArrayUtils.addAll(strUniqueFields,
					new String[] { PsndocVO.NAME2, PsndocVO.NAME3,
							PsndocVO.NAME4, PsndocVO.NAME5, PsndocVO.NAME6 });
		}
		// ����֤���ŵ�������֤��������֤�����ͼ���
		if (ArrayUtils.contains(strUniqueFields, PsndocVO.ID)
				&& (!ArrayUtils.contains(strUniqueFields, PsndocVO.IDTYPE))) {
			strUniqueFields = (String[]) ArrayUtils.add(strUniqueFields,
					PsndocVO.IDTYPE);
		}

		PsndocVO newPsndoc = new PsndocVO();
		for (String strField : strUniqueFields) {
			newPsndoc.setAttributeValue(strField,
					psndocVO.getAttributeValue(strField));
		}
		// ����pk_org�Ա������ͬһ��֯��У��
		// newPsndoc.setAttributeValue("pk_org",
		// psndocVO.getPsnJobVO().getPk_hrorg());

		if (psndocVO.getPk_psndoc() != null) {
			newPsndoc.setPk_psndoc(psndocVO.getPk_psndoc());
		}

		// ����Ψһ�Թ�����Ҵ���֪����ڣ������ID��name��Ҫ�����ݽ��д�����
		PsndocAggVO psndocAggVO = getPsndocVOByUniqueVO(newPsndoc,
				strUniqueFields, true);

		// У������Ψһ����,���ش˴�У����Ϣ
		if (psndocAggVO != null && psndocAggVO.getParentVO() != null) {
			PsndocVO head = psndocAggVO.getParentVO();
			Map mapUniqueField = rule;

			String strMsg = "";
			PsnIdtypeVO psnIdtypeVO = (PsnIdtypeVO) getDao().retrieveByPK(
					PsnIdtypeVO.class,
					(String) psndocVO.getAttributeValue(CertVO.IDTYPE));
			for (String strField : strUniqueFields) {
				if (mapUniqueField.get(strField) == null) {
					continue;
				}
				if (CertVO.IDTYPE.equalsIgnoreCase(strField)) {
					strMsg += mapUniqueField.get(strField) + ":"
							+ VOUtils.getNameByVO(psnIdtypeVO) + ",";
				} else if (PsndocVO.NAME.equalsIgnoreCase(strField)) {
					strMsg += mapUniqueField.get(strField) + ":"
							+ VOUtils.getNameByVO(psndocVO) + ",";
				} else {
					String fieldValue = (String) psndocVO
							.getAttributeValue(strField);
					// ������ʾ�е�nullֵ
					fieldValue = StringUtils.isBlank(fieldValue) ? ResHelper
							.getString("6007psn", "06007psn0148")/*
																 * @ res "��"
																 */
					: fieldValue;
					strMsg += mapUniqueField.get(strField) + ":" + fieldValue
							+ ",";
				}
			}
			if (strMsg.length() > 0) {
				strMsg = strMsg.substring(0, strMsg.length() - 1);
			}

			String jobName = VOUtils.getDocName(PostVO.class, head
					.getPsnJobVO().getPk_post());
			jobName = StringUtils.isBlank(jobName) ? ResHelper.getString(
					"6007psn", "06007psn0148")/*
											 * @ res "��"
											 */: jobName;
			String deptName = VOUtils.getDocName(DeptVO.class, head
					.getPsnJobVO().getPk_dept());
			deptName = StringUtils.isBlank(deptName) ? ResHelper.getString(
					"6007psn", "06007psn0148")/*
											 * @ res "��"
											 */: deptName;
			String orgName = VOUtils.getDocName(OrgVO.class, head.getPsnJobVO()
					.getPk_org());
			orgName = StringUtils.isBlank(orgName) ? ResHelper.getString(
					"6007psn", "06007psn0148")/*
											 * @ res "��"
											 */: orgName;
			strMsg = ResHelper.getString("6007psn", "06007psn0231"/*
																 * @res
																 * "�����Ѵ���  {0} ����; ������Ϣ����  ������֯��{1}  ���ڲ��ţ�{2}  ���ڸ�λ: {3}"
																 */, strMsg,
					orgName, deptName, jobName);
			return strMsg;
		}

		return null;
	}

	/**
	 * ͨ��pk_rule���һ��У�����
	 * 
	 * @author yangshuo
	 * @Created on 2012-11-22 ����08:43:17
	 */

	public Map<String, HashMap<String, String>> getPsnUserUniqueRule(
			String[] pk_rules) throws DAOException {
		InSQLCreator isc = new InSQLCreator();
		String insql = null;
		try {
			insql = isc.getInSQL(pk_rules);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			throw new DAOException(e.getMessage(), e);
		}
		String sql = "select t3.name,t3.displayname,t3.resid,pk_rule from bd_uniquerule_item t2,md_property t3 "
				+ "where  t2.mdcolumnid=t3.id and pk_rule in (" + insql + ")";

		GeneralVO[] list = (GeneralVO[]) baseDAOManager.executeQuery(sql,
				new GeneralVOProcessor<GeneralVO>(GeneralVO.class));
		Map<String, HashMap<String, String>> result = new HashMap<String, HashMap<String, String>>();
		if (list == null || list.length < 1) {
			return result;
		}
		for (GeneralVO vo : list) {
			String pk_rule = (String) vo.getAttributeValue("pk_rule");
			HashMap<String, String> map = result.get(pk_rule);
			if (map == null) {
				map = new HashMap<String, String>();
			}
			map.put((String) vo.getAttributeValue("name"),
					ResHelper.getString("10140psn",
							(String) vo.getAttributeValue("resid")));
			result.put(pk_rule, map);
		}

		return result;
	}

	/**
	 * ����Ψһ�Ե�vo��У�������Ҹ���Ա�ڵ�ǰ��֯���Ƿ���ڣ�����PsndocAggVO
	 * 
	 * @author yangshuo
	 * @Created on 2012-11-23 ����10:29:53
	 */
	public PsndocAggVO getPsndocVOByUniqueVO(PsndocVO psndocVO,
			String[] strFields, boolean isInSelf) throws BusinessException {
		String strBaseWhere = "1=1";

		// ����vo�γ�where
		String nameSql = "";
		for (String strField : strFields) {
			if (strField.equalsIgnoreCase("pk_psndoc")
					|| psndocVO.getAttributeValue(strField) == null
					|| !strField.startsWith(PsndocVO.NAME)) {
				// ����name�ֶβ�����
				continue;
			}
			String strFieldValue = (String) psndocVO
					.getAttributeValue(strField);
			if (StringUtils.isBlank(nameSql)) {
				nameSql = nameSql + " bd_psndoc." + strField + " = '"
						+ NCESAPI.sqlEncode(strFieldValue) + "' ";
			} else {
				nameSql = nameSql + " or bd_psndoc." + strField + " = '"
						+ NCESAPI.sqlEncode(strFieldValue) + "' ";
			}

		}
		if (!StringUtils.isEmpty(nameSql)) {
			strBaseWhere = strBaseWhere + " and ( " + nameSql + " )";
		}

		for (String strField : strFields) {
			if (strField.equalsIgnoreCase("pk_psndoc")
					|| strField.startsWith(PsndocVO.NAME)) {
				continue;
			}

			if (psndocVO.getAttributeValue(strField) == null) {
				// ΨһԼ������һ������Ϊ�գ��򲻽���У��
				return null;
			}

			Object objValue = psndocVO.getAttributeValue(strField);
			if (objValue instanceof String || objValue instanceof UFBoolean
					|| objValue instanceof UFDateTime
					|| objValue instanceof UFTime) {
				if (PsndocVO.ID.equals(strField)) {

					String idtype = psndocVO.getIdtype();
					if (idtype.equals("1001Z01000000000AI36")) {
						String id = objValue.toString();
						String id15 = "";
						String id18 = "";
						if (id.length() == 15) {
							id15 = id;
							IDValidateUtil idVU = new IDValidateUtil(id);
							id18 = idVU.getUpgradeId();
							strBaseWhere = strBaseWhere
									+ " and ( hi_psndoc_cert." + strField
									+ " = '" + NCESAPI.sqlEncode(id18) + "' ) ";
						} else if (id.length() == 18) {
							id15 = id.substring(0, 6) + id.substring(8, 17);
							id18 = id;
							strBaseWhere = strBaseWhere
									+ " and ( hi_psndoc_cert." + strField
									+ " = '" + NCESAPI.sqlEncode(id18) + "' ) ";
						} else {
							id18 = id;
							id15 = id;
							strBaseWhere = strBaseWhere
									+ " and ( hi_psndoc_cert." + strField
									+ " = '" + NCESAPI.sqlEncode(id18)
									+ "' or hi_psndoc_cert." + strField
									+ " = '" + NCESAPI.sqlEncode(id15) + "' ) ";
						}

						// ֤���ŵ�֤����Ϣ�в�
						strBaseWhere = strBaseWhere + " and ( hi_psndoc_cert."
								+ strField + " = '" + NCESAPI.sqlEncode(id18)
								+ "' or hi_psndoc_cert." + strField + " = '"
								+ NCESAPI.sqlEncode(id15) + "' ) ";
					} else {
						strBaseWhere = strBaseWhere + " and  hi_psndoc_cert."
								+ strField + " = '"
								+ NCESAPI.sqlEncode(objValue.toString()) + "' ";
					}
				} else {
					strBaseWhere = strBaseWhere + " and bd_psndoc." + strField
							+ " = '" + NCESAPI.sqlEncode(objValue.toString())
							+ "' ";
				}
			} else if (objValue instanceof UFLiteralDate) {
				strBaseWhere = strBaseWhere + " and bd_psndoc." + strField
						+ " like '" + ((UFLiteralDate) objValue).toStdString()
						+ "%' ";
			} else if (objValue instanceof UFDouble
					|| objValue instanceof Integer) {
				if (PsndocVO.IDTYPE.equals(strField)) {
					// ֤�����͵�֤����Ϣ�в�
					strBaseWhere = strBaseWhere + " and hi_psndoc_cert."
							+ strField + " = " + objValue.toString() + " ";

				} else {
					strBaseWhere = strBaseWhere + " and bd_psndoc." + strField
							+ " = " + objValue.toString() + " ";
				}
			}

		}
		// �������Ѿ���ϵͳ���ˣ���Ҫ�ų����ˣ�����Ƿ���������ظ���
		if (isInSelf && psndocVO.getPk_psndoc() != null) {
			strBaseWhere = strBaseWhere + " and bd_psndoc.pk_psndoc<>'"
					+ psndocVO.getPk_psndoc() + "'";
		}

		// ��ѯ��Ч֤����Ϣ
		String strOtherWhere = " and hi_psndoc_cert.iseffect = 'Y' and hi_psnorg.lastflag='Y'";
		ArrayList<String> listField = new ArrayList<String>();
		String strPsndocFieldCodes[] = new PsndocVO().getAttributeNames();
		for (int i = 0; i < strPsndocFieldCodes.length; i++) {
			listField.add("bd_psndoc." + strPsndocFieldCodes[i]
					+ " as bd_psndoc_" + strPsndocFieldCodes[i]);
		}
		String strPsnOrgFieldCodes[] = new PsnOrgVO().getAttributeNames();
		for (int i = 0; i < strPsnOrgFieldCodes.length; i++) {
			listField.add("hi_psnorg." + strPsnOrgFieldCodes[i]
					+ " as hi_psnorg_" + strPsnOrgFieldCodes[i]);
		}
		String strPsnJobFieldCodes[] = new PsnJobVO().getAttributeNames();
		for (int i = 0; i < strPsnJobFieldCodes.length; i++) {
			listField.add("hi_psnjob." + strPsnJobFieldCodes[i]
					+ " as hi_psnjob_" + strPsnJobFieldCodes[i]);
		}

		// ��ͬһ��֯�ڽ���У��
		// strBaseWhere +=
		// " and bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc and hi_psnjob.ismainjob = 'Y' and hi_psnjob.endflag = 'N' and hi_psnjob.lastflag = 'Y' and hi_psnjob.pk_hrorg = '"
		// + psndocVO.getPk_org() + "'";

		for (String strField : strFields) {
			if (!strField.startsWith(PsndocVO.NAME)
					&& !strField.equals(PsndocVO.ID)
					&& !strField.equals(PsndocVO.BIRTHDATE)) {
				if (psndocVO.getAttributeValue(strField) != null) {
					strBaseWhere += "and bd_psndoc." + strField + " = '"
							+ psndocVO.getAttributeValue(strField) + "' ";
				}
			}
		}

		PsndocVO psndocVOs[] = queryPsndocVOByCondition(null, listField,
				new String[] { "hi_psndoc_cert", "bd_psndoc", /* "hi_psnjob" */},
				strBaseWhere + strOtherWhere, null);
		if (psndocVOs == null || psndocVOs.length == 0) {
			return null;
		}
		PsndocAggVO psndocAggVO = new PsndocAggVO();
		psndocAggVO.setParentVO(psndocVOs[0]);
		return psndocAggVO;
	}

	/**
	 * ����ʱУ���Ӽ��ǿ�
	 * 
	 * @pare PsndocAggVO
	 * @pare HashMap ����->����ʾ����
	 * @return ���Ӽ���ʾ����
	 * @author yangshuo
	 * @Created on 2012-12-7 ����01:14:54
	 */
	public String[] queryNotNullSubset(PsndocAggVO psndocAggVO,
			HashMap<String, String> showTab) throws BusinessException {
		String pk_psncl = psndocAggVO.getParentVO().getPsnJobVO().getPk_psncl();
		String pk_hrorg = psndocAggVO.getParentVO().getPsnOrgVO().getPk_hrorg();
		String[] showCodes = showTab.keySet().toArray(new String[0]);
		// �ڱ���ǰ�����hrhi.��metadataǰ׺��Ϊhrhi.
		for (int i = 0; i < showCodes.length; i++) {
			showCodes[i] = new String("hrhi." + showCodes[i]);
		}
		// ����showCodes����sql�������������ʾ���Ӽ�����������Ϊ�ǿ��Ӽ���metadata
		InSQLCreator inSqlCreator = new InSQLCreator();
		ArrayList<String> erroInfo = new ArrayList<String>();
		try {
			// �̳кͷǼ̳еĹ���һ����������������Щ���գ��߶������������Ǳ�������ȷ��
			String sql = "select metadata from  hr_psnclinfoset,hr_psnclrule where hr_psnclinfoset.pk_psnclrule = hr_psnclrule.pk_psnclrule "
					+ " and hr_psnclrule.pk_org = '"
					+ pk_hrorg
					+ "' "
					+ "and hr_psnclinfoset.metadata in ( "
					+ inSqlCreator.getInSQL(showCodes)
					+ " ) and mustentryflag = 'Y' "
					+ "and ( hr_psnclrule.PK_PSNCLRULE in ( select SOURCEPK from hr_psnclrule where PK_PSNCL = '"
					+ pk_psncl
					+ "' and INHERITFLAG = 'Y' ) "
					+ "or hr_psnclrule.PK_PSNCL = '" + pk_psncl + "' )";
			ArrayList<String> metadataList = (ArrayList<String>) NCLocator
					.getInstance().lookup(IPersistenceHome.class)
					.executeQuery(sql, new ColumnListProcessor());

			// ��������ʾ���Ӽ�û�зǿ�Լ����ֱ�ӷ���nul
			if (metadataList == null || metadataList.isEmpty())
				return null;

			String[] notNulSubSets = metadataList.toArray(new String[0]);
			for (int i = 0; i < notNulSubSets.length; i++) {
				// ��ȡ�ǿ��ӱ�������Ϊmetadataǰ׺��Ϊhrhi.
				notNulSubSets[i] = new String(notNulSubSets[i].substring(5,
						notNulSubSets[i].length()));
			}

			// У���Ӽ��Ƿ�գ�Ϊ�����Ӽ���ʾ���Ƽ���erroInfo
			for (String notNulSubSet : notNulSubSets) {
				// ��֯��ϵ�͹�����¼�ӱ���У�飬��Ϊ��������getTableVOȡ����ֵ�������������ӱ�������Ϊ��
				if (notNulSubSet.equals("hi_psnorg")
						|| notNulSubSet.equals("hi_psnjob")) {
					continue;
				}
				// �Ӽ�Ϊ�ռ���erroInfo
				if (psndocAggVO.getTableVO(notNulSubSet) == null) {
					erroInfo.add(showTab.get(notNulSubSet));
				}
			}

		} finally {
			if (inSqlCreator != null)
				inSqlCreator.clear();
		}

		if (erroInfo.isEmpty())
			return null;
		else
			return erroInfo.toArray(new String[0]);
	}

	/**
	 * ��Աת��ǰ����У��
	 * 
	 * 
	 */
	public void validateInDocInf(String inSql) throws BusinessException {
		List<String> listTableName = new ArrayList<String>();// .getTableNames("hr_temptable_");

		// ����У��
		String dieSql = " select code,"
				+ SQLHelper.getMultiLangNameColumn("name")
				+ " as name from bd_psndoc where pk_psndoc in (select pk_psndoc from hi_psnjob where pk_psnjob in ("
				+ inSql
				+ ")) and isnull(die_date,'~') <> '~' and isnull( die_remark,'~') <> '~' ";

		GeneralVO[] dieVOs = (GeneralVO[]) baseDAOManager.executeQuery(dieSql,
				new GeneralVOProcessor(GeneralVO.class));
		if (dieVOs != null && dieVOs.length > 0) {
			String name = "";
			for (int i = 0; i < dieVOs.length; i++) {
				name += ",[" + dieVOs[i].getAttributeValue("code") + ","
						+ dieVOs[i].getAttributeValue("name") + "]";
			}
			if (!StringUtils.isBlank(name)) {
				throw new BusinessException(ResHelper.getString("6007psn",
						"06007psn0347")/* "������Ա�Ѿ�����,����ת����Ա����:" */
						+ '\n' + name.substring(1));
			}
		}
		// ��ͬУ��
		String ctrtSql = " select bd_psndoc.code, "
				+ SQLHelper.getMultiLangNameColumn("bd_psndoc.name")
				+ " from hi_psnjob inner join bd_psndoc on  hi_psnjob.pk_psndoc  = bd_psndoc.pk_psndoc "
				+ " inner join hi_psnorg org1 on  org1.pk_psnorg  = hi_psnjob.pk_psnorg "
				+ " inner join hi_psndoc_ctrt c1 on  c1.pk_psnorg  = hi_psnjob.pk_psnorg"
				+ " inner join hi_psndoc_ctrt c2 on  c2.pk_psndoc  = hi_psnjob.pk_psndoc"
				+ " where org1.indocflag = 'N' and c1.lastflag = 'Y'and c1.conttype in (1,2,3) "
				+ " and  c2.lastflag = 'Y' and c2.conttype in (1,2,3) and c1.pk_psnorg <> c2.pk_psnorg and hi_psnjob.pk_psnjob in ("
				+ inSql + ")";

		GeneralVO[] ctrtVOs = (GeneralVO[]) baseDAOManager.executeQuery(
				ctrtSql, new GeneralVOProcessor(GeneralVO.class));
		if (ctrtVOs != null && ctrtVOs.length > 0) {
			String name = "";
			for (int i = 0; i < ctrtVOs.length; i++) {
				name += ",[" + ctrtVOs[i].getAttributeValue("code") + ","
						+ ctrtVOs[i].getAttributeValue("name") + "]";
			}
			if (!StringUtils.isBlank(name)) {
				throw new BusinessException(ResHelper.getString("6007psn",
						"06007psn0512")/* "������Ա��ְǰ�Ѵ���δ�����ĺ�ͬ��Ϣ,����ת����Ա����:" */
						+ '\n' + name.substring(1));
			}
		}
	}

	/**
	 * �޸ı���ʱУ���Ӽ��ǿ�
	 * 
	 * @pare PsndocAggVO
	 * @pare HashMap ����->����ʾ����
	 * @return ���Ӽ���ʾ����
	 * @author yangshuo
	 * @Created on 2012-12-7 ����03:32:07
	 */
	public String[] validateSubNotNull(PsndocAggVO psndocAggVO,
			HashMap<String, String> showTab) throws BusinessException {
		String pk_psncl = psndocAggVO.getParentVO().getPsnJobVO().getPk_psncl();
		String pk_hrorg = psndocAggVO.getParentVO().getPsnOrgVO().getPk_hrorg();
		String[] showCodes = showTab.keySet().toArray(new String[0]);
		// �ڱ���ǰ�����hrhi.��metadataǰ׺��Ϊhrhi.
		for (int i = 0; i < showCodes.length; i++) {
			showCodes[i] = new String("hrhi." + showCodes[i]);
		}
		// ����showCodes����sql�������������ʾ���Ӽ�����������Ϊ�ǿ��Ӽ���metadata
		InSQLCreator inSqlCreator = new InSQLCreator();
		ArrayList<String> erroInfo = new ArrayList<String>();
		try {
			// �̳кͷǼ̳еĹ���һ����������������Щ���գ��߶������������Ǳ�������ȷ��
			// String sql =
			// "select metadata from  hr_psnclinfoset,hr_psnclrule where hr_psnclinfoset.pk_psnclrule = hr_psnclrule.pk_psnclrule "
			// + " and hr_psnclrule.pk_org = '" + pk_hrorg + "' " +
			// "and hr_psnclinfoset.metadata in ( " +
			// inSqlCreator.getInSQL(showCodes)
			// + " ) and mustentryflag = 'Y' " +
			// "and ( hr_psnclrule.PK_PSNCLRULE in ( select SOURCEPK from hr_psnclrule where PK_PSNCL = '"
			// + pk_psncl + "' and INHERITFLAG = 'Y' ) " +
			// "or hr_psnclrule.PK_PSNCL = '" + pk_psncl + "' )";
			// Ϊ�����Ч�ʣ���Ϊ2������ ����heqiaoa 2015-03-11
			String sql = " SELECT HR_PSNCLINFOSET.metadata "
					+ " FROM HR_PSNCLINFOSET inner join hr_psnclrule on "
					+ " 	hr_psnclinfoset.pk_psnclrule = hr_psnclrule.pk_psnclrule "
					+ " WHERE hr_psnclrule.pk_org = '"
					+ pk_hrorg
					+ "' AND "
					+ " hr_psnclinfoset.metadata IN ( "
					+ inSqlCreator.getInSQL(showCodes)
					+ " ) AND "
					+ " mustentryflag = 'Y' AND "
					+ "( hr_psnclrule.PK_PSNCLRULE IN (SELECT SOURCEPK "
					+ " FROM hr_psnclrule "
					+ " WHERE PK_PSNCL = '1001A110000000000K8G' AND INHERITFLAG = 'Y' ) "
					+ " OR " + " hr_psnclrule.PK_PSNCL = '" + pk_psncl + "' ) ";
			ArrayList<String> metadataList = (ArrayList<String>) NCLocator
					.getInstance().lookup(IPersistenceHome.class)
					.executeQuery(sql, new ColumnListProcessor());

			// ��������ʾ���Ӽ�û�зǿ�Լ����ֱ�ӷ���null
			if (metadataList == null || metadataList.isEmpty()) {
				return null;
			}

			String[] notNulSubSets = metadataList.toArray(new String[0]);
			for (int i = 0; i < notNulSubSets.length; i++) {
				// ��ȡ�ǿ��ӱ�������Ϊmetadataǰ׺��Ϊhrhi.
				notNulSubSets[i] = new String(notNulSubSets[i].substring(5,
						notNulSubSets[i].length()));
			}

			// У���Ӽ��Ƿ�գ�Ϊ�����Ӽ���ʾ���Ƽ���erroInfo
			for (String notNulSubSet : notNulSubSets) {
				// ��֯��ϵ�͹�����¼�ӱ���У�飬��Ϊ��������getTableVOȡ����ֵ�������������ӱ�������Ϊ��
				if (notNulSubSet.equals("hi_psnorg")
						|| notNulSubSet.equals("hi_psnjob")
						|| notNulSubSet.equals("hi_psndoc_parttime")) {
					continue;
				}
				// �޸�ʱ���������ȡ����ֵ���п�����û���޸Ĵ��Ӽ����ݣ��������Ӽ��������Ƿǿ�
				if (psndocAggVO.getTableVO(notNulSubSet) == null) {
					// ��ѯ���ݿ����������ȡ����ֵ���������ݿ���Ҳû���ݣ��Ǿ��ǿ��Ӽ�
					sql = "select * from " + notNulSubSet
							+ " where pk_psndoc = '"
							+ psndocAggVO.getParentVO().getPk_psndoc() + "'";
					GeneralVO[] subVOs = (GeneralVO[]) baseDAOManager
							.executeQuery(sql,
									new GeneralVOProcessor<GeneralVO>(
											GeneralVO.class));
					if (subVOs == null) {
						erroInfo.add(showTab.get(notNulSubSet));
					}
				}
				// ������ȡ�õ�ֵҲ�����Ƿǿգ���Ϊ������Ӽ����������ݶ���ɾ��Ҳ�ǿ��Ӽ�
				else {
					SuperVO[] subVOs = psndocAggVO.getTableVO(notNulSubSet);
					// �ȼٶ������Ӽ����ݶ���delete״̬
					boolean isAlldeleted = true;
					// ֻҪ��һ��vo����delete״̬isAlldeleted��Ϊfalse
					for (SuperVO subVO : subVOs) {
						if (subVO.getStatus() != VOStatus.DELETED)
							isAlldeleted = false;
					}
					if (isAlldeleted)
						erroInfo.add(showTab.get(notNulSubSet));
				}

			}
		} finally {
			if (inSqlCreator != null)
				inSqlCreator.clear();
		}

		if (erroInfo.isEmpty())
			return null;
		else
			return erroInfo.toArray(new String[0]);
	}

	/**
	 * ��ȡ������¼���߼�ְ��¼��������¼ӳ���ϵ
	 * 
	 * @param pk_org
	 *            ��ǰ������¼��֯
	 * @param pk_group
	 *            ��ǰ������¼����
	 * @param isMainJob
	 *            �Ƿ���ְ������������Դ��Ϣ���ǹ�����¼���Ǽ�ְ��¼
	 * @return ���ع�����¼�ֶ�����Ӧһ��GeneralVO��GeneralVO���ֶ����£�jobcode workcode
	 *         jobdatatype workdatatype jobrefmodule
	 * @return �ֱ�Ϊ��������¼�ֶ�����������¼�ֶ�����������¼�ֶ��������ԡ� ������¼�ֶ��������ԡ�������¼������Ϣ��pk
	 * @author yangshuo
	 * @throws BusinessException
	 * @Created on 2012-12-17 ����09:46:31
	 */
	public HashMap<String, GeneralVO> getWorkSyncMap(String pk_org,
			String pk_group, boolean isMainJob) throws BusinessException {
		// ������¼��Ϣ������
		final String PK_JOBINFOSET = "1002Z710000000001FP5";
		// ��ְ��¼��Ϣ������
		final String PK_PARTTIMEINFOSET = "1001Z71000000000GJAH";

		// ������¼��Ϣ������
		String pk_workInfoset = "1002Z710000000006ZTO";
		// ȫ������
		String pk_global = IOrgConst.GLOBEORG;// "GLOBLE00000000000000";
		// Ϊpk_srcInfoset��ֵ
		// Դ��Ϣ��pk
		String pk_srcInfoset = isMainJob ? PK_JOBINFOSET : PK_PARTTIMEINFOSET;

		// ��ѭ��ϸ���ȣ����θ�����֯�����š�ȫ�ֲ�ѯӳ���ϵ
		String sql = " pk_infoset_map in (select pk_infoset_map from hr_infoset_map where pk_sourceinfoset = '"
				+ pk_srcInfoset
				+ "' and pk_targetinfoset = '"
				+ pk_workInfoset
				+ "' and pk_org = '" + pk_org + "') ";

		InfoItemMapVO[] infoitemmaps = (InfoItemMapVO[]) NCLocator
				.getInstance().lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, InfoItemMapVO.class, sql);
		if (null == infoitemmaps) {
			// ������֯��ѯΪ�գ���ѯ���ڼ����µ�ӳ���ϵ
			sql = " pk_infoset_map in (select pk_infoset_map from hr_infoset_map where pk_sourceinfoset = '"
					+ pk_srcInfoset
					+ "' and pk_targetinfoset = '"
					+ pk_workInfoset + "' and pk_org = '" + pk_group + "') ";

			infoitemmaps = (InfoItemMapVO[]) NCLocator.getInstance()
					.lookup(IPersistenceRetrieve.class)
					.retrieveByClause(null, InfoItemMapVO.class, sql);
		}
		if (null == infoitemmaps) {
			// ������֯�����Ų�ѯΪ�գ���ѯȫ���µ�ӳ���ϵ
			sql = " pk_infoset_map in (select pk_infoset_map from hr_infoset_map where pk_sourceinfoset = '"
					+ pk_srcInfoset
					+ "' and pk_targetinfoset = '"
					+ pk_workInfoset + "' and pk_org = '" + pk_global + "') ";

			infoitemmaps = (InfoItemMapVO[]) NCLocator.getInstance()
					.lookup(IPersistenceRetrieve.class)
					.retrieveByClause(null, InfoItemMapVO.class, sql);
		}

		// ��֯����ȫ��ӳ�䶼Ϊ�շ���null
		if (null == infoitemmaps)
			return null;

		// ������й�����¼��������¼��Ϣ��
		String sql2 = " pk_infoset = '" + pk_srcInfoset + "' or pk_infoset = '"
				+ pk_workInfoset + "' ";
		InfoItemVO[] list = (InfoItemVO[]) NCLocator.getInstance()
				.lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, InfoItemVO.class, sql2);
		// ��Ϣ��pk �� ��ӦInfoItemVOӳ��
		HashMap<String, InfoItemVO> infoitem = new HashMap<String, InfoItemVO>();
		for (InfoItemVO vo : list) {
			infoitem.put(vo.getPk_infoset_item(), vo);
		}
		// ���ؽ��ӳ��
		HashMap<String, GeneralVO> result = new HashMap<String, GeneralVO>();

		for (InfoItemMapVO infoitemmap : infoitemmaps) {
			// ������¼��������¼VO
			InfoItemVO jobItemVO = infoitem.get(infoitemmap
					.getPk_sourceinfoitem());
			InfoItemVO workItemVO = infoitem.get(infoitemmap
					.getPk_targetinfoitem());

			GeneralVO temp = new GeneralVO();
			// ������¼�ֶ���/������¼�ֶ���/������¼�ֶ���������/ ������¼�ֶ���������/ ������¼������Ϣ��pk/ ������¼��Ϣ������
			temp.setAttributeNames("jobcode", "workcode", "jobdatatype",
					"workdatatype", "jobrefmodule", "job_pk_infoset_item");
			temp.setAttributeValue("jobcode", jobItemVO.getItem_code());
			temp.setAttributeValue("workcode", workItemVO.getItem_code());
			temp.setAttributeValue("jobdatatype", jobItemVO.getData_type());
			temp.setAttributeValue("workdatatype", workItemVO.getData_type());
			temp.setAttributeValue("jobrefmodule",
					jobItemVO.getRef_model_name());
			temp.setAttributeValue("job_pk_infoset_item",
					jobItemVO.getPk_infoset_item());

			result.put(jobItemVO.getItem_code(), temp);
		}

		return result;
	}

	/**
	 * ����������ϢPK�Ͳ��ն�Ӧֵ�����ظ�ֵ��Ӧ�����ƣ�pkת��name��,pk_infoset_item���ڸ�ʽ���׳��Ĵ�����Ϣ
	 * 
	 * @author yangshuo
	 * @throws BusinessException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @Created on 2012-12-25 ����03:49:59
	 */
	public String getRefItemName(String pk_refinfo, Object value,
			String pk_infoset_item) throws BusinessException {
		if (value == null) {
			return null;
		}
		InfoItemVO infoItemVO = (InfoItemVO) baseDAOManager.retrieveByPK(
				InfoItemVO.class, pk_infoset_item);
		InfoSetVO infoSetVO = (InfoSetVO) baseDAOManager.retrieveByPK(
				InfoSetVO.class, infoItemVO.getPk_infoset());

		RefInfoVO refInfoVO = (RefInfoVO) baseDAOManager.retrieveByPK(
				RefInfoVO.class, pk_refinfo);
		String strErrMsg = ResHelper.getString("6001infset", "06001infset0073"
		/*
		 * @res"û�в�ѯ������������Ϣ������bd_refinfo�����Ƿ�ע�ᣡ��Ϣ�����ƣ�[{0}]����Ϣ�����룺[{1}]����Ϣ�����ƣ�[{2}]
		 * ����Ϣ����룺[{3}]����Ӧ�Ĳ�����Ϣ��[{4}]"
		 */
		, infoSetVO.getInfoset_name(), infoSetVO.getInfoset_code(),
				infoItemVO.getItem_name(), infoItemVO.getItem_code(),
				infoItemVO.getRef_model_name());

		if (refInfoVO == null) {
			throw new BusinessException(strErrMsg);
		}

		// �����ϸ��������ѯ���ų������ռ䲻ͬ�ĵ���������ͬ��ʵ�壬������鲻�����Ͳ��������ռ��ѯʵ��
		String strSQL = "md_class.name='"
				+ refInfoVO.getMetadataTypeName()
				+ "' and md_class.componentid in(select id from md_component where ownmodule='"
				+ refInfoVO.getModuleName() + "')";
		Collection<ClassVO> collRefClassVO = null;
		collRefClassVO = baseDAOManager.retrieveByClause(ClassVO.class, strSQL);

		if (collRefClassVO == null || collRefClassVO.isEmpty()) {
			strSQL = "md_class.name='" + refInfoVO.getMetadataTypeName() + "'";

			collRefClassVO = baseDAOManager.retrieveByClause(ClassVO.class,
					strSQL);

			if (collRefClassVO == null || collRefClassVO.isEmpty()) {
				throw new BusinessException(strErrMsg);
			}
		}
		// �õ���Ӧ��classvo
		ClassVO refClassVO = collRefClassVO.iterator().next();

		// ͨ��classvo ��ò��ն�Ӧ������
		String clsName = refClassVO.getFullClassName();
		// ���÷���ʵ������Ӧ ��VO
		Object vo;
		try {
			vo = Class.forName(clsName).newInstance();
		} catch (Exception e) {

			throw new BusinessException(e.getMessage(), e);
		}

		// ��Ӧ����
		String name = null;
		if (vo instanceof SuperVO) {
			// �����Ӧ���յ�VO
			vo = NCLocator.getInstance().lookup(IPersistenceRetrieve.class)
					.retrieveByPk(null, vo.getClass(), value.toString());
			name = vo == null ? "" : MultiLangHelper.getName((SuperVO) vo);
		} else {
			// �鲻����Ӧ����fullClassName����ֱ�Ӹ�ֵ
			name = value == null ? "" : value.toString();
		}

		return name;
	}

	/**
	 * ����UAP������ͣ����Ա</p> <li>�����UAP������ͣ����Ա������̨���ɵ���֯��ϵ�͹�����¼������������һ����ְ��¼
	 * 
	 * @param psndocVOs
	 * @param psnjobVOs
	 * @throws BusinessException
	 */
	public void dismissUAPPsn(PsndocVO[] psndocVOs, PsnJobVO[] psnjobVOs)
			throws BusinessException {
		//
		Map<String, Integer> enablestateMap = new HashMap<String, Integer>();
		for (PsndocVO psndocVO : psndocVOs) {
			int enablestate = psndocVO.getEnablestate();
			String pk_psndoc = psndocVO.getPk_psndoc();
			enablestateMap.put(pk_psndoc, enablestate);
		}

		for (PsnJobVO psnjobVO : psnjobVOs) {
			String pk_psndoc = psnjobVO.getPk_psndoc();
			String pk_psnjob = psnjobVO.getPk_psnjob();// ���¹�����¼����
			String pk_psnorg = psnjobVO.getPk_psnorg();// ������֯��ϵ����

			// ����״̬ �����ǰ��Ա����ͣ����Ա�����أ��ж���һ����
			int enablestate = enablestateMap.get(pk_psndoc);
			if (enablestate != IPubEnumConst.ENABLESTATE_DISABLE) {// ������ͣ��
				continue;
			}

			// �õ���̨���ɵĹ�����¼����֯��ϵ
			// �����Աû��������֯��ϵ����˵����ϵͳ��ԭ�������ڴ��ˣ������ж�ԭ���Ƿ�ΪUAP������Ա
			PsnOrgVO[] orgVOs = (PsnOrgVO[]) getRetrieve().retrieveByClause(
					null,
					PsnOrgVO.class,
					"pk_psndoc = '" + pk_psndoc + "' and pk_psnorg != '"
							+ pk_psnorg + "'");
			if (orgVOs == null || orgVOs.length == 0) {
				continue;
			}
			PsnJobVO[] jobVOs = (PsnJobVO[]) getRetrieve().retrieveByClause(
					null,
					PsnJobVO.class,
					"pk_psndoc = '" + pk_psndoc + "' and pk_psnjob != '"
							+ pk_psnjob + "' and ismainjob = 'Y'");

			// ������ְ��֯�ж��Ƿ�ΪUAP������ͣ����Ա��������ǣ��ж���һ����
			int count = NCLocator
					.getInstance()
					.lookup(IPersistenceRetrieve.class)
					.getCountByCondition("org_admin_enable",
							"pk_adminorg = '" + jobVOs[0].getPk_org() + "'");
			boolean isHREnabled = count == 0 ? Boolean.FALSE : Boolean.TRUE;
			// boolean isHREnabled =
			// NCLocator.getInstance().lookup(nc.itf.bd.psn.psndoc.IPsndocService.class).isHREnabled(jobVOs[0].getPk_org());
			if (isHREnabled) {
				continue;
			}

			// ����Ա��������֯��ϵ
			PsnOrgVO lastOrgVO = queryByPk(PsnOrgVO.class, pk_psnorg);

			// ��������
			UFLiteralDate enddate = lastOrgVO.getBegindate().getDateBefore(1);

			// ������֯��ϵ
			orgVOs[0].setEnddate(enddate);
			orgVOs[0].setEndflag(UFBoolean.TRUE);
			update4SubSet(orgVOs[0], false, true);

			// ���ݹ�����¼����һ����ְ��¼
			PsnJobVO dimissVO = (PsnJobVO) jobVOs[0].clone();
			dimissVO.setBegindate(lastOrgVO.getBegindate());
			dimissVO.setPk_psnjob(null);
			dimissVO.setTrnsevent(Integer.valueOf(TrnseventEnum.DISMISSION
					.getEnumValue().getValue()));
			dimissVO.setStatus(VOStatus.NEW);
			insert(dimissVO);

			// ������ְ
			jobVOs[0].setEnddate(enddate);
			jobVOs[0].setEndflag(UFBoolean.TRUE);
			jobVOs[0].setLastflag(UFBoolean.FALSE);
			jobVOs[0].setRecordnum(1);
			update4SubSet(jobVOs[0], false, true);

			// ������ְ
			PsnJobVO[] parttimeVOs = queryByCondition(PsnJobVO.class,
					"pk_psnorg = '" + orgVOs[0].getPk_psnorg()
							+ "' and endflag = 'N' and ismainjob = 'N'");
			if (parttimeVOs != null && parttimeVOs.length > 0) {
				for (PsnJobVO parttimeVO : parttimeVOs) {
					parttimeVO.setEnddate(enddate);
					parttimeVO.setEndflag(UFBoolean.TRUE);
				}
				NCLocator
						.getInstance()
						.lookup(IPersistenceUpdate.class)
						.updateVOArray(
								null,
								parttimeVOs,
								new String[] { PsnJobVO.ENDDATE,
										PsnJobVO.ENDFLAG }, null);
			}

			// �����������
			PsnChgVO lastVO = NCLocator.getInstance()
					.lookup(IPersonRecordService.class)
					.getLastVO(PsnChgVO.class, orgVOs[0].getPk_psnorg(), null);
			if (lastVO != null && lastVO.getEnddate() == null) {
				lastVO.setEnddate(enddate);
				update(lastVO, false);
			}
		}
	}

	// ���´˶δ���ΪHR�ƶ�Ӧ�ú�̨���룬���𶯣����� add by 2014-08-04 yanglt
	/**
	 * ����userid��ѯ��Ա���� 2014-4-18 ����06:49:59 yunana
	 * 
	 * @param userId
	 *            ����cp_user��
	 * @return
	 * @throws BusinessException
	 */
	public String queryPsnPkByUserID(String userId) throws BusinessException {
		String pk_psndoc = null;
		UserVO userVO = NCLocator.getInstance().lookup(IUserManageQuery.class)
				.findUserByIDs(new String[] { userId })[0];
		if (userVO != null && StringUtils.isNotEmpty(userVO.getPk_base_doc())) {
			pk_psndoc = userVO.getPk_base_doc();
		}
		return pk_psndoc;
	}

	/**
	 * ����pk_psndoc��ѯ������֯��¼���� 2014-4-18 ����06:49:59 yunana
	 * 
	 * @throws BusinessException
	 */
	public String queryOrgPkBypsndoc(String pk_psndoc) throws BusinessException {
		String pk_psnorg = null;
		String sql = "select pk_psnorg from hi_psnorg where pk_psndoc = '"
				+ pk_psndoc + "' and lastflag = 'Y' and endflag = 'N' ";
		Object obj = baseDAOManager.executeQuery(sql, new ColumnProcessor());
		if (obj != null && !"".equals(obj)) {
			pk_psnorg = obj.toString();
		}
		return pk_psnorg;
	}

	/**
	 * ����pk_psndoc��ѯ����
	 * 
	 * @throws BusinessException
	 */
	public String queryWorkageBypsndoc(String pk_psndoc)
			throws BusinessException {
		String workage = "";
		String sql = "select workage from bd_psndoc where pk_psndoc = '"
				+ pk_psndoc + "' ";
		Object obj = baseDAOManager.executeQuery(sql, new ColumnProcessor());
		if (obj != null && !"".equals(obj)) {
			workage = obj.toString();
		}
		return workage;
	}

	/**
	 * ����pk_psnorg��ѯ�������������¼��˾��
	 * 
	 * @throws BusinessException
	 */
	public String queryOrgageBypsnorg(String pk_psnorg)
			throws BusinessException {
		String orgage = "";
		String sql = "select orgage from hi_psndoc_psnchg where lastflag = 'Y' and RECORDNUM = 0 and pk_psnorg = '"
				+ pk_psnorg + "' ";
		Object obj = baseDAOManager.executeQuery(sql, new ColumnProcessor());
		if (obj != null && !"".equals(obj)) {
			orgage = obj.toString();
		}
		return orgage;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getPsnJobInfo(String pk_psnjob)
			throws BusinessException {
		Map<String, String> resultMap = new HashMap<String, String>();
		String sql = "select org_orgs.name orgname,"
				+ "org_dept.name deptname,"
				+ "om_job.jobname jobname,"
				+ "om_post.postname postname,"
				+ "om_joblevel.name jobgradename,"
				+ "om_jobrank.jobrankname jobrankname,"
				+ "bd_psncl.name psnclname "
				// "select org_orgs."
				// + SQLHelper.getMultiLangNameColumn("name")
				// + " orgname, org_dept."
				// + SQLHelper.getMultiLangNameColumn("name")
				// + " deptname, om_job."
				// + SQLHelper.getMultiLangNameColumn("jobname")
				// + " jobname, om_post."
				// + SQLHelper.getMultiLangNameColumn("postname")
				// + " postname, om_joblevel."
				// + SQLHelper.getMultiLangNameColumn("name")
				// + " jobgradename, om_jobrank."
				// + SQLHelper.getMultiLangNameColumn("jobrankname")
				// + " jobrankname, bd_psncl."
				// + SQLHelper.getMultiLangNameColumn("name")
				// + " psnclname "
				+ "from bd_psndoc inner join hi_psnjob on bd_psndoc.pk_psndoc = hi_psnjob.pk_psndoc "
				+ "left outer join org_orgs on  hi_psnjob.pk_org = org_orgs.pk_org "
				+ "left outer join org_dept on hi_psnjob.pk_dept = org_dept.pk_dept "
				+ "left outer join om_job on hi_psnjob.pk_job = om_job.pk_job "
				+ "left outer join om_post on hi_psnjob.pk_post = om_post.pk_post "
				+ "left outer join om_joblevel on hi_psnjob.pk_jobgrade = om_joblevel.pk_joblevel "
				+ "left outer join om_jobrank on hi_psnjob.pk_jobrank = om_jobrank.pk_jobrank "
				+ "left outer join bd_psncl on hi_psnjob.pk_psncl = bd_psncl.pk_psncl "
				+
				// "where hi_psnjob.lastflag = 'Y' and hi_psnjob.endflag = 'N' and hi_psnjob.pk_psnjob = '"
				// + pk_psnjob + "'";
				"where hi_psnjob.pk_psnjob = '" + pk_psnjob + "'";

		resultMap = (Map<String, String>) baseDAOManager.executeQuery(sql,
				new PsnInfoMapProcessor());
		return resultMap;
	}

	private class PsnInfoMapProcessor extends BaseProcessor {

		private static final long serialVersionUID = 1L;

		@Override
		public Map<String, String> processResultSet(ResultSet rs)
				throws SQLException {
			Map<String, String> resultMap = new HashMap<String, String>();
			if (rs.next()) {
				String orgname = rs.getString("orgname");
				String deptname = rs.getString("deptname");
				String jobname = rs.getString("jobname");
				String postname = rs.getString("postname");
				String jobgradename = rs.getString("jobgradename");
				String jobrankname = rs.getString("jobrankname");
				String psnclname = rs.getString("psnclname");
				resultMap.put("orgname", orgname);
				resultMap.put("deptname", deptname);
				resultMap.put("jobname", jobname);
				resultMap.put("postname", postname);
				resultMap.put("jobgradename", jobgradename);
				resultMap.put("jobrankname", jobrankname);
				resultMap.put("psnclname", psnclname);
			}
			return resultMap;
		}
	}

	/***
	 * ������֯���ŵ�������ѯ���Ż���֯�����˵����� 2014-4-16 ����11:22:22 yunana
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public HROrgVO queryPrincipalByOrgPk(String pk) throws BusinessException {
		HROrgVO orgVO = (HROrgVO) NCLocator
				.getInstance()
				.lookup(IPersistenceRetrieve.class)
				.retrieveByPk(HROrgVO.class, pk,
						new String[] { "principal", "isbusinessunit" });
		return orgVO;
	}

	/***
	 * ������Ա������ѯ��Ա��Ϣ��������Ƭ����������֯���ƣ��������ƣ���λ���ƣ�ְ������ 2014-4-16 ����11:07:42 yunana
	 * 
	 * @param psnPk
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Map<String, Object>> queryPsnInfoByPsnPk(String psnPk)
			throws BusinessException {
		String querySql = "select doc.photo photo,"
				+ "doc.sex sex,"
				+ "doc.name name,"
				+ "adminorg.name orgname,"
				+ "dept.name deptname,"
				+ "dept.pk_dept pk_dept,"
				+ "post.postname postname,"
				+ "omjob.jobname jobname "
				// "select doc.photo photo,doc.sex sex,doc."
				// + SQLHelper.getMultiLangNameColumn("name")
				// + " name"
				// + ",adminorg."
				// + SQLHelper.getMultiLangNameColumn("name")
				// + " orgname"
				// + ",dept."
				// + SQLHelper.getMultiLangNameColumn("name")
				// + " deptname"
				// + ", post."
				// + SQLHelper.getMultiLangNameColumn("postname")
				// + " postname"
				// + ",omjob."
				// + SQLHelper.getMultiLangNameColumn("jobname")
				// + " jobname "
				+ "from bd_psndoc doc"
				+ " inner join hi_psnorg org "
				+ "on doc.pk_psndoc = org.pk_psndoc "
				+ " inner join hi_psnjob job "
				+ " on org.pk_psnorg = job.pk_psnorg "
				+ " inner join org_adminorg adminorg "
				+ " on job.pk_org = adminorg.pk_adminorg "
				+ "  inner join org_dept dept "
				+ " on job.pk_dept = dept.pk_dept "
				+ " left outer join om_post post "
				+ "  on job.pk_post = post.pk_post "
				+ "   left outer join om_job omjob "
				+ "   on job.pk_job = omjob.pk_job  "
				// zlzhoulei 20151118
				// +
				// "where org.lastflag = 'Y' and job.lastflag = 'Y' and doc.pk_psndoc = '"
				// + psnPk + "'";
				+ "where org.lastflag = 'Y' and job.lastflag = 'Y' and doc.pk_psndoc = '"
				+ psnPk + "' and job.ismainjob = 'Y'";
		ArrayList<Map<String, Object>> processor = (ArrayList<Map<String, Object>>) baseDAOManager
				.executeQuery(querySql, new MapListProcessor());
		return processor;
	}

	/**
	 * ���ݲ���Pk��ѯ�ò����µ����� 2014-4-15 ����05:03:55 yunana
	 * 
	 * @param deptPk
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public Integer queryCountByDeptPk(String deptPk) throws BusinessException {
		// String querySql =
		// "select count(*) subcount from "
		// + "hi_psnorg psnorg inner join hi_psnjob psnjob "
		// + "on psnorg.pk_psnorg = psnjob.pk_psnorg "
		// + "where psnorg.lastflag = 'Y' and psnorg.indocflag = 'Y' "
		// +
		// "and psnjob.lastflag = 'Y' and psnjob.endflag = 'N' and psnorg.psntype = 0 and psnjob.ismainjob = 'Y' and psnjob.pk_dept = '"
		// + deptPk + "'";
		// liangshuai 20151209
		String querySql = "select count(*) subcount from ( select distinct psnjob.pk_psndoc from "
				+ "hi_psnorg psnorg inner join hi_psnjob psnjob "
				+ "on psnorg.pk_psnorg = psnjob.pk_psnorg "
				+ "where psnorg.lastflag = 'Y' and psnorg.indocflag = 'Y' "
				+ "and psnjob.lastflag = 'Y' and psnjob.endflag = 'N' and psnorg.psntype = 0 and psnjob.pk_dept = '"
				+ deptPk + "')subcount";
		ArrayList<HashMap<String, Integer>> processor = (ArrayList<HashMap<String, Integer>>) baseDAOManager
				.executeQuery(querySql, new MapListProcessor());
		Integer subcount = processor.get(0).get("subcount");

		return subcount;
	}

	/**
	 * ���ݲ�����������ѯ���¼����ŵ���Ϣ 2014-4-16 ����09:23:01 yunana
	 * 
	 * @param deptPk
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Map<String, Object>> querySubDeptInfoByDeptPk(String deptPk)
			throws BusinessException {
		ArrayList<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		// ��ѯ�����������µ������¼�����
		String condition = "innercode like ( select innercode || '%' from org_dept where pk_dept = '"
				+ deptPk + "') and hrcanceled = 'N' ";
		Collection<HRDeptVO> hrdeptVOs = baseDAOManager.retrieveByClause(
				HRDeptVO.class, condition);
		if (hrdeptVOs.isEmpty()) {
			return resultList;
		}
		// ��ѯ�����������µ����й�����¼
		// String querySql =
		// "select job.pk_dept"
		// +
		// " from hi_psnorg psnorg  inner join hi_psnjob job on psnorg.pk_psnorg = job.pk_psnorg  "
		// +
		// " where job.pk_dept in (select pk_dept from org_dept where innercode like ( select innercode || '%' from org_dept where pk_dept = '"
		// + deptPk
		// + "') )"
		// +
		// " and job.lastflag = 'Y' and job.ismainjob = 'Y' and psnorg.lastflag = 'Y' and psnorg.indocflag = 'Y' and psnorg.endflag = 'N' ";
		// liangshuai
		String querySql = "select job.pk_dept,job.pk_psndoc"
				+ " from hi_psnorg psnorg  inner join hi_psnjob job on psnorg.pk_psnorg = job.pk_psnorg  "
				+ " where job.pk_dept in (select pk_dept from org_dept where innercode like ( select innercode || '%' from org_dept where pk_dept = '"
				+ deptPk
				+ "') )"
				+ " and job.ENDFLAG = 'N' and job.lastflag = 'Y' and job.psntype = 0  and psnorg.lastflag = 'Y' and psnorg.indocflag = 'Y' and psnorg.endflag = 'N' ";
		GeneralVO[] psnjobVOs = (GeneralVO[]) baseDAOManager.executeQuery(
				querySql, new GeneralVOProcessor(GeneralVO.class));
		// if (ArrayUtils.isEmpty(psnjobVOs)) {
		// return resultList;
		// }

		for (Iterator<HRDeptVO> ite = hrdeptVOs.iterator(); ite.hasNext();) {
			List<String> subDeptList = new ArrayList<String>();
			HRDeptVO hrdeptVO = ite.next();
			if (!deptPk.equals(hrdeptVO.getPk_fatherorg())) {
				continue;
			}
			// �ݹ��ѯ--���ݲ���������ѯ�����������¼�����
			getDeptCountAndName(hrdeptVO.getPk_dept(), hrdeptVOs, subDeptList);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("subpkdept", hrdeptVO.getPk_dept());
			resultMap.put("subdeptname", hrdeptVO.getName());
			if (ArrayUtils.isEmpty(psnjobVOs)) {
				resultMap.put("subcount", String.valueOf(0));
			} else {
				resultMap.put("subcount", getPsnCount(psnjobVOs, subDeptList));
				resultMap.put("subDeptList", subDeptList); // �����ŵ������Ӳ��ŷ���MAP
															// heqiaoa 20150721
			}
			resultList.add(resultMap);
		}
		return resultList;
	}

	/***
	 * ͨ����֯��������ø���֯�µĲ�����Ϣ�����������������������ƣ���ÿ�������µ����� 2014-4-15 ����04:05:55 yunana
	 * 
	 * @param orgPk
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Map<String, Object>> queryDeptInfoByOrgPk(String orgPk)
			throws BusinessException {
		ArrayList<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		// ��ѯ��������֯�µ����в���
		String condition = " pk_org = '" + orgPk + "' and hrcanceled = 'N' ";
		Collection<HRDeptVO> hrdeptVOs = baseDAOManager.retrieveByClause(
				HRDeptVO.class, condition);
		if (hrdeptVOs.isEmpty()) {
			return resultList;
		}
		// ��ѯ��������֯�µ����й�����¼
		// String querySql =
		// "select job.pk_dept"
		// +
		// " from hi_psnorg psnorg  inner join hi_psnjob job on psnorg.pk_psnorg = job.pk_psnorg  "
		// + " where job.pk_org = '"
		// + orgPk
		// + "' "
		// +
		// " and job.lastflag = 'Y' and job.ismainjob = 'Y' and psnorg.lastflag = 'Y' and psnorg.indocflag = 'Y' and psnorg.endflag = 'N' ";
		// liangshuai 20151209
		String querySql = "select job.pk_dept,job.pk_psndoc"
				+ " from hi_psnorg psnorg  inner join hi_psnjob job on psnorg.pk_psnorg = job.pk_psnorg  "
				+ " where job.pk_org = '"
				+ orgPk
				+ "' "
				+ " and job.ENDFLAG = 'N' and job.psntype = 0 and job.lastflag = 'Y' and psnorg.lastflag = 'Y' and psnorg.indocflag = 'Y' and psnorg.endflag = 'N' ";
		GeneralVO[] psnjobVOs = (GeneralVO[]) baseDAOManager.executeQuery(
				querySql, new GeneralVOProcessor(GeneralVO.class));
		// if (ArrayUtils.isEmpty(psnjobVOs)) {
		// return resultList;
		// }

		for (Iterator<HRDeptVO> ite = hrdeptVOs.iterator(); ite.hasNext();) {
			List<String> subDeptList = new ArrayList<String>();
			HRDeptVO hrdeptVO = ite.next();
			if (StringUtils.isNotBlank(hrdeptVO.getPk_fatherorg())) {
				continue;
			}
			// �ݹ��ѯ--���ݲ���������ѯ�����������¼�����
			getDeptCountAndName(hrdeptVO.getPk_dept(), hrdeptVOs, subDeptList);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("subpkdept", hrdeptVO.getPk_dept());
			resultMap.put("subdeptname", hrdeptVO.getName());
			if (ArrayUtils.isEmpty(psnjobVOs)) {
				resultMap.put("subcount", String.valueOf(0));
			} else {
				resultMap.put("subcount", getPsnCount(psnjobVOs, subDeptList));
				resultMap.put("subDeptList", subDeptList); // �����ŵ������Ӳ��ŷ���MAP
															// heqiaoa 20150721
			}
			resultList.add(resultMap);
		}
		return resultList;
	}

	/**
	 * ͨ�������������飬���ÿ�����ŵ����ƣ�����������������ڵ����� 2014-4-16 ����03:09:49 yunana
	 * 
	 * @param pkList
	 * @return
	 * @throws BusinessException
	 */
	/**
	 * ͨ�������������飬���ÿ�����ŵ����ƣ�����������������ڵ����� 2014-4-16 ����03:09:49 yunana
	 * 
	 * @param pkList
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Map<String, Object>> queryDeptInfoByPks(
			ArrayList<String> pkList) throws BusinessException {
		ArrayList<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		String[] pks = pkList.toArray(new String[0]);

		for (int i = 0; i < pks.length; i++) {
			String deptPk = pks[i];
			Map<String, Object> resultMap = new HashMap<String, Object>();

			String namesql = "select name from org_dept where pk_dept = '"
					+ deptPk + "' ";
			Object name = baseDAOManager.executeQuery(namesql,
					new ColumnProcessor());
			if (name == null || "".equals(name)) {
				continue;
			}
			// ��ѯ��������֯�µ����й�����¼
			String querySql = "select job.pk_dept"
					+ " from hi_psnorg psnorg  inner join hi_psnjob job on psnorg.pk_psnorg = job.pk_psnorg  "
					+ " where job.pk_dept in (select pk_dept from org_dept where innercode like ( select innercode || '%' from org_dept where pk_dept = '"
					+ deptPk
					+ "'))"
					+ " and job.lastflag = 'Y' and job.ismainjob = 'Y' and psnorg.lastflag = 'Y' and psnorg.indocflag = 'Y' and psnorg.endflag = 'N' ";
			GeneralVO[] psnjobVOs = (GeneralVO[]) baseDAOManager.executeQuery(
					querySql, new GeneralVOProcessor(GeneralVO.class));
			if (ArrayUtils.isEmpty(psnjobVOs)) {
				resultMap.put("subcount", String.valueOf(0));
			} else {
				resultMap.put("subcount", String.valueOf(psnjobVOs.length));
			}
			resultMap.put("subpkdept", deptPk);
			resultMap.put("subdeptname", String.valueOf(name));
			resultList.add(resultMap);
		}
		return resultList;
	}

	/***
	 * ͨ����֯�������飬���ÿ����֯�����ƣ�����������������ڵ����� 2014-4-15 ����04:02:36 yunana
	 * 
	 * @param pkList
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Map<String, Object>> queryOrgsInfoByPkS(
			ArrayList<String> pkList) throws BusinessException {
		String[] pks = pkList.toArray(new String[0]);
		InSQLCreator isc = new InSQLCreator();
		String inSql = isc.getInSQL(pks);

		String querySql = "select adminorg.pk_adminorg subpkdept, adminorg.name"
				// + SQLHelper.getMultiLangNameColumn("name")
				+ " subdeptname, t.subcount subcount from org_adminorg adminorg left outer join "
				+ " (select job.pk_org,count(job.pk_psnjob) subcount from hi_psnorg psnorg inner join  hi_psnjob job on psnorg.pk_psnorg = job.pk_psnorg where job.pk_org in ( "
				+ " select pk_adminorg from org_adminorg where pk_adminorg in ("
				+ inSql
				+ ")) and psnorg.lastflag = 'Y' and psnorg.indocflag = 'Y' and job.lastflag = 'Y'  and job.ismainjob = 'Y'  and job.endflag = 'N'  and psnorg.psntype = 0 group by job.pk_org) t "
				+ " on adminorg.pk_adminorg = t.pk_org where adminorg.pk_adminorg in ("
				+ inSql + ") order by subdeptname";

		ArrayList<Map<String, Object>> processor = (ArrayList<Map<String, Object>>) baseDAOManager
				.executeQuery(querySql, new MapListProcessor());
		return processor;
	}

	/**
	 * ��ѯ���������֯��������Ա�Ĳ������� ��û���˵Ĳ��Ų���ʾ
	 * 
	 * @return
	 * @author heqiaoa
	 */
	public ArrayList<String> queryDeptPksByOrgPks(ArrayList<String> pkList)
			throws BusinessException {
		String[] pks = pkList.toArray(new String[0]);
		InSQLCreator isc = new InSQLCreator();
		String inSql = isc.getInSQL(pks);
		String querySql = " SELECT    distinct job.pk_dept pk_dept                              "
				+ " FROM                                                                "
				+ "     hi_psnorg psnorg                                                "
				+ "         INNER JOIN hi_psnjob job                                    "
				+ "         ON psnorg.pk_psnorg = job.pk_psnorg                         "
				+ "         left join org_adminorg org on job.pk_org = org.PK_ADMINORG  "
				+ " WHERE                                                               "
				+ "     psnorg.lastflag = 'Y' AND                                       "
				+ "     psnorg.indocflag = 'Y' AND                                      "
				+ "     job.lastflag = 'Y' AND                                          "
				+ "     job.ismainjob = 'Y' AND                                         "
				+ "     job.endflag = 'N' AND                                           "
				+ "    psnorg.psntype = 0 and                                           "
				+ "     org.pk_adminorg IN ( '"
				+ inSql
				+ "' )                          ";
		ArrayList<Map<String, String>> processor = (ArrayList<Map<String, String>>) baseDAOManager
				.executeQuery(querySql, new MapListProcessor());
		ArrayList<String> ret = new ArrayList<String>();
		for (Map<String, String> m : processor) {
			ret.add(m.get("pk_dept"));
		}
		return ret;
	}

	/***
	 * ������Ա��������为�����֯���� 2014-4-15 ����02:04:45 yunana
	 * 
	 * @param psnPk
	 * @return
	 * @throws BusinessException
	 */
	public ArrayList<String> getOrgPksByPsnPk(String psnPk)
			throws BusinessException {
		ArrayList<String> orgPkList = new ArrayList<String>();
		String condition = " pk_org in (select pk_adminorg from org_adminorg) and principal = '"
				+ psnPk + "' ";
		HROrgVO[] orgVOs = (HROrgVO[]) NCLocator.getInstance()
				.lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, HROrgVO.class, condition);
		if (ArrayUtils.isEmpty(orgVOs)) {
			return orgPkList;
		}
		for (HROrgVO hrOrgVO : orgVOs) {
			String pk_org = hrOrgVO.getPk_org();
			orgPkList.add(pk_org);
		}
		return orgPkList;
	}

	/***
	 * ������Ա��������为��Ĳ������� 2014-4-15 ����02:03:50 yunana
	 * 
	 * @param psnPk
	 * @return
	 * @throws BusinessException
	 */
	public ArrayList<String> getDeptPksByPsnPk(String psnPk)
			throws BusinessException {
		ArrayList<String> deptPkList = new ArrayList<String>();
		String condition = " principal = '" + psnPk + "'";
		HRDeptVO[] deptVOs = (HRDeptVO[]) NCLocator.getInstance()
				.lookup(IPersistenceRetrieve.class)
				.retrieveByClause(null, HRDeptVO.class, condition);
		if (ArrayUtils.isEmpty(deptVOs)) {
			return deptPkList;
		}
		for (HRDeptVO hrDeptVO : deptVOs) {
			String deptPk = hrDeptVO.getPk_dept();
			deptPkList.add(deptPk);
		}
		return deptPkList;
	}

	/**
	 * ���ݲ���������ѯ�������� 2014-4-16 ����05:18:58 yunana
	 * 
	 * @param deptPk
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public String queryDeptNameByDeptPk(String deptPk) throws BusinessException {
		String querySql = "select name"
		// "select " + SQLHelper.getMultiLangNameColumn("name")
				+ " name from org_dept where pk_dept = '" + deptPk + "'";
		ArrayList<Map<String, String>> processor = (ArrayList<Map<String, String>>) baseDAOManager
				.executeQuery(querySql, new MapListProcessor());
		return processor.get(0).get("name");
	}

	/**
	 * ͨ�����������������ѯ��Щ��������Ա��Ϣ
	 * 
	 * @param deptPks
	 * @return ArrayList<HashMap<String, String>>
	 * @throws BusinessException
	 * @author heqiaoa
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String, String>> queryPsnInfoByDeptPk(
			String[] deptPks) throws BusinessException {
		if (ArrayUtils.isEmpty(deptPks)) {
			return null;
		}
		// InSQLCreator isc = new InSQLCreator();
		// String insql = isc.getInSQL(deptPks);
		String insql = zJoinToInSql(deptPks);
		String querySql = "select doc.pk_psndoc,"
				+ " doc.name psnname,"
				+ " post.postname postname,omjob.jobname"
				+ " jobname  from "
				+ " bd_psndoc doc "
				+ "inner join hi_psnorg psnorg "
				+ "on doc.pk_psndoc  = psnorg.pk_psndoc "
				+ "inner join hi_psnjob psnjob "
				+ "on psnorg.pk_psnorg = psnjob.pk_psnorg "
				+ "left outer join om_post post "
				+ "on psnjob.pk_post=post.pk_post "
				+ "left outer join om_job omjob "
				+ "on psnjob.pk_job = omjob.pk_job "
				+ "where psnorg.indocflag ='Y' and psnorg.lastflag = 'Y' and psnjob.endflag = 'N' and psnjob.lastflag = 'Y' and psnjob.ismainjob = 'Y' "
				+ "and psnjob.pk_dept in (" + insql + ")";
		// System.out.println("-------------");
		ArrayList<HashMap<String, String>> processor = (ArrayList<HashMap<String, String>>) baseDAOManager
				.executeQuery(querySql, new MapListProcessor());
		return processor;
	}

	/**
	 * ͨ��������������ѯ�ò�������Ա��Ϣ 2014-4-16 ����04:40:47 yunana
	 * 
	 * @param deptPk
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<HashMap<String, String>> queryPsnInfoByDeptPk(String deptPk)
			throws BusinessException {
		// liangshuai20151209
		String querySql = "select doc.pk_psndoc, ismainjob, "
				+ " doc.name psnname,"
				+ " post.postname postname,omjob.jobname"
				// + SQLHelper.getMultiLangNameColumn("name")
				// + " psnname ,post."
				// + SQLHelper.getMultiLangNameColumn("postname")
				// + " postname ,omjob."
				// + SQLHelper.getMultiLangNameColumn("jobname")
				+ " jobname  from "
				+ " bd_psndoc doc "
				+ "inner join hi_psnorg psnorg "
				+ "on doc.pk_psndoc  = psnorg.pk_psndoc "
				+ "inner join hi_psnjob psnjob "
				+ "on psnorg.pk_psnorg = psnjob.pk_psnorg "
				+ "left outer join om_post post "
				+ "on psnjob.pk_post=post.pk_post "
				+ "left outer join om_job omjob "
				+ "on psnjob.pk_job = omjob.pk_job "
				+ "where psnorg.indocflag ='Y' and psnorg.lastflag = 'Y' and psnjob.endflag = 'N' and psnjob.lastflag = 'Y' and psnjob.psntype = 0 "
				+ "and psnjob.pk_dept = '" + deptPk + "'";
		ArrayList<HashMap<String, String>> processor = (ArrayList<HashMap<String, String>>) baseDAOManager
				.executeQuery(querySql, new MapListProcessor());
		// liangshuai 20151209
		ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
		Map<String, String> mapKey = new HashMap<String, String>();

		for (HashMap<String, String> map : processor) {
			String key = map.get("pk_psndoc");
			if (!mapKey.containsKey(key)
					|| (mapKey.containsKey(key) && mapKey.get(key) == "Y")) {
				mapKey.put(map.get("pk_psndoc"), map.get("ismainjob"));
				map.remove("ismainjob");
				arraylist.add(map);
			}
		}
		return arraylist;
	}

	/**
	 * ���ÿ�������¹�����¼��ְ����Ϣ 2014-4-21 ����02:14:15 yunana
	 * 
	 * @param psndocPks
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Map<String, String>> queryJobRankByPsnPks(
			String[] psndocPks) throws BusinessException {
		// InSQLCreator isc = new InSQLCreator();
		// String inSql = isc.getInSQL(psndocPks);
		String inSql = zJoinToInSql(psndocPks);

		String querySql = "select om_jobrank.jobrankname"
				// + SQLHelper.getMultiLangNameColumn("jobrankname")
				+ " name,psnorg.pk_psndoc pk_psndoc from hi_psnorg psnorg inner join  hi_psnjob psnjob on psnorg.pk_psnorg = psnjob.pk_psnorg inner join om_jobrank on psnjob.pk_jobrank = om_jobrank.pk_jobrank where psnorg.lastflag = 'Y' and ismainjob= 'Y' and psnorg.pk_psndoc in ("
				+ inSql + ")";
		ArrayList<Map<String, String>> processor = (ArrayList<Map<String, String>>) baseDAOManager
				.executeQuery(querySql, new MapListProcessor());
		return processor;
	}

	public ArrayList<Map<String, String>> queryWorkAge(String[] psndocPks)
			throws BusinessException {
		// InSQLCreator isc = new InSQLCreator();
		// String inSql = isc.getInSQL(psndocPks);
		//
		String inSql = zJoinToInSql(psndocPks);
		String querySql = "select pk_psndoc,workage from bd_psndoc where pk_psndoc in ("
				+ inSql + ")";
		ArrayList<Map<String, String>> processor = (ArrayList<Map<String, String>>) baseDAOManager
				.executeQuery(querySql, new MapListProcessor());
		/*
		 * Object obj = null; if (null!=processor&&processor.size()>0){
		 * for(Map<String, String> mp:processor){ obj = mp.get("joinworkdate");
		 * if(obj != null && !"".equals(obj)){ UFDate joinworkdate = new
		 * UFDate(obj.toString()); mp.put("workage",
		 * getAge(joinworkdate.toDate())) ; }else{ mp.put("workage", ""); } } }
		 */
		return processor;
	}

	/***
	 * ������Ա������ѯ��Աѧ����Ϣ 2014-4-21 ����09:26:25 yunana
	 * 
	 * @param pks
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Map<String, String>> queryEduInfoByPks(String[] pks)
			throws BusinessException {
		// InSQLCreator isc = new InSQLCreator();
		// String inSql = isc.getInSQL(pks);
		// zhoulei 20151209
		String inSql = zJoinToInSql(pks);
		// String querySql =
		// "select def.name"
		// // + SQLHelper.getMultiLangNameColumn("name")
		// + " name, psn.pk_psndoc from " +
		// "bd_psndoc psn inner join bd_defdoc def on psn.edu = def.pk_defdoc "
		// + "where psn.pk_psndoc in (" + inSql + ")";
		String querySql = "select def.name name, psn.pk_psndoc from "
				+ " hi_psndoc_edu psn  inner join bd_defdoc  def on psn.education = def.pk_defdoc "
				+ " where psn.pk_psndoc in (" + inSql
				+ ") and psn.lasteducation = 'Y' ";
		ArrayList<Map<String, String>> processor = (ArrayList<Map<String, String>>) baseDAOManager
				.executeQuery(querySql, new MapListProcessor());
		return processor;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Map<String, String>> queryAge(String[] psndocPks)
			throws BusinessException {

		// zlzhoulei 20151110
		// String inSql = isc.getInSQL(psndocPks);
		// InSQLCreator isc = new InSQLCreator();
		// String inSql = isc.getInSQL(psndocPks);

		// String inSql="'0001E310000000003GCQ','0001111000000000HCYN'";
		String inSql = zJoinToInSql(psndocPks);
		String querySql = "select age,pk_psndoc from bd_psndoc where pk_psndoc in ("
				+ inSql + ")";
		ArrayList<Map<String, String>> processor = (ArrayList<Map<String, String>>) baseDAOManager
				.executeQuery(querySql, new MapListProcessor());
		return processor;
	}

	/**
	 * ��ȡѧ����Ϣ���� 2014-4-18 ����07:27:48 yunana
	 * 
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> queryDegree() throws BusinessException {
		String degreeDoclistPk = "1001Z71000000000GP4V";// ѧ������pk
		String querySql = "select code, name"
				// + SQLHelper.getMultiLangNameColumn("name")
				+ " name from bd_defdoc where pk_defdoclist = '"
				+ degreeDoclistPk + "' order by code";
		ArrayList<Map<String, String>> processor = (ArrayList<Map<String, String>>) baseDAOManager
				.executeQuery(querySql, new MapListProcessor());
		ArrayList<String> degreeList = new ArrayList<String>();
		for (Map<String, String> map : processor) {
			String degree = map.get("name");
			degreeList.add(degree);
		}
		return degreeList;
	}

	/**
	 * ��õ�ǰ�����µ�ְ������ 2014-4-21 ����02:05:53 yunana
	 * 
	 * @param groupPk
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> queryJobRank(String groupPk)
			throws BusinessException {
		String querySql = "SELECT jobrankname"
		// + SQLHelper.getMultiLangNameColumn("jobrankname")
				+ " name FROM om_jobrank where pk_group = '" + groupPk + "'";
		ArrayList<Map<String, String>> processor = (ArrayList<Map<String, String>>) baseDAOManager
				.executeQuery(querySql, new MapListProcessor());
		ArrayList<String> jobRankList = new ArrayList<String>();
		for (Map<String, String> map : processor) {
			String name = map.get("name");
			jobRankList.add(name);
		}
		return jobRankList;
	}

	// �ݹ��ѯ--���ݲ���������ѯ�����������¼�����
	private List<String> getDeptCountAndName(String pk_dept,
			Collection<HRDeptVO> hrdeptVOs, List<String> deptList) {
		if (deptList.isEmpty()) {
			deptList.add(pk_dept);
		}
		for (Iterator<HRDeptVO> ite = hrdeptVOs.iterator(); ite.hasNext();) {
			HRDeptVO hrDeptVO = ite.next();
			if (StringUtils.isBlank(hrDeptVO.getPk_fatherorg())) {
				continue;
			}

			if (hrDeptVO.getPk_fatherorg().equals(pk_dept)) {
				deptList.add(hrDeptVO.getPk_dept());
				getDeptCountAndName(hrDeptVO.getPk_dept(), hrdeptVOs, deptList);
			}
		}
		return deptList;
	}

	private String getPsnCount(GeneralVO[] psnjobVOs, List<String> subDeptList) {

		// Integer count = 0;
		// for (int i = 0; i < psnjobVOs.length; i++)
		// {
		// GeneralVO psnjobVO = psnjobVOs[i];
		// if
		// (subDeptList.contains(psnjobVO.getAttributeValue(PsnJobVO.PK_DEPT)))
		// {
		// count++;
		// }
		// }
		// return String.valueOf(count);

		// liangshuai20151209
		Integer count = 0;
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < psnjobVOs.length; i++) {
			GeneralVO psnjobVO = psnjobVOs[i];
			if (subDeptList.contains(psnjobVO
					.getAttributeValue(PsnJobVO.PK_DEPT))) {
				if (!set.contains(psnjobVO
						.getAttributeValue(PsnJobVO.PK_PSNDOC))) {
					set.add((String) psnjobVO
							.getAttributeValue(PsnJobVO.PK_PSNDOC));
					count++;
				}
			}
		}
		return String.valueOf(count);
	}

	// ���ϴ˶δ���ΪHR�ƶ�Ӧ�ú�̨���룬���𶯣����� end by 2014-08-04 yanglt

	private String getAge(Date birthDay) {
		Calendar cal = Calendar.getInstance();
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(birthDay);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
		int age = yearNow - yearBirth;
		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				// monthNow==monthBirth
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				} else {
					// do nothing
				}
			} else {
				// monthNow>monthBirth
				age--;
			}
		} else {
			// monthNow<monthBirth
			// donothing
		}
		return String.valueOf(age);
	}

	public static String zJoinToInSql(String strValues[]) {
		int iCount = -1;
		if (strValues == null || strValues.length == 0) {
			return null;
		}

		List<String> arrListInSQL = new ArrayList<String>();
		List<String> arrListExistValue = new ArrayList<String>();// �Ѿ����ڵ�ֵ���Ա�֤���ظ�

		StringBuilder strbdrInSQL = new StringBuilder();

		for (int i = 0, iIndex = 0; i < strValues.length; i++) {
			if (StringUtils.isBlank(strValues[i])
					|| arrListExistValue.contains(strValues[i].trim())) {
				continue;
			}

			iIndex++;
			strbdrInSQL.append(",'")
					.append(StringUtils.replace(strValues[i], "'", "''"))
					.append("'");
			// 2015118 zlzhoulei
			// strbdrInSQL.append(",'").append(transfer(strValues[i])).append("'");

			strbdrInSQL.append(",'").append(transfer(strValues[i])).append("'");

			if (iCount == -1 && i == strValues.length - 1) {
				arrListInSQL.add(strbdrInSQL.substring(1));
				continue;
			}

			if (iIndex == iCount || i == strValues.length - 1) {
				if (strbdrInSQL.toString().trim().length() > 0) {
					arrListInSQL.add(strbdrInSQL.substring(1));
				}

				iCount = 0;
				strbdrInSQL = new StringBuilder();
			}
		}

		if (strbdrInSQL.length() > 0
				&& !arrListInSQL.contains(strbdrInSQL.toString()))// ��֤���һ��Ҳ��in�����б���
		{
			arrListInSQL.add(strbdrInSQL.substring(1));
		}

		return arrListInSQL.isEmpty() ? null : arrListInSQL.get(0);
	}

	public static String transfer(String strValue) {
		return StringUtils.replace(strValue, "'", "''");
	}

	public SuperVO[] querySubByConditionWithoutS(Class clazz, String strWhere,
			String strOrder) throws BusinessException {

		String strCondition = " ";
		if (strWhere != null && strWhere.trim().length() > 0) {
			strCondition += strWhere;
		}
		if (strOrder != null && strOrder.trim().length() > 0) {
			strCondition += " order by " + strOrder;
		}
		SuperVO[] vos = (SuperVO[]) queryByCondition(clazz, strCondition);
		if (vos == null || vos.length == 0) {
			return null;
		}
		if (vos[0] instanceof PsnChgVO || vos[0] instanceof PsnJobVO) {
			// ������¼/������������ڹ�ʽ��ֵ
			vos = fillDateFormula(vos);
		}
		return vos;
	}

}