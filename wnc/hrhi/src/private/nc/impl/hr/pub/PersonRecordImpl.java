package nc.impl.hr.pub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nc.bs.bd.baseservice.ArrayClassConvertUtil;
import nc.bs.bd.baseservice.md.SingleBaseService;
import nc.bs.bd.util.DBAUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.sec.esapi.NCESAPI;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.hr.utils.InSQLCreator;
import nc.hr.utils.MultiLangHelper;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.SQLHelper;
import nc.impl.hi.psndoc.PsndocDAO;
import nc.itf.hi.IPersonRecordService;
import nc.itf.hi.IPsndocService;
import nc.itf.hi.IPsndocSubInfoService4JFS;
import nc.itf.hr.frame.IPersistenceHome;
import nc.itf.hr.frame.IPersistenceRetrieve;
import nc.itf.hr.frame.IPersistenceUpdate;
import nc.itf.uap.rbac.IUserManage;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.jdbc.framework.processor.MapProcessor;
import nc.md.persist.framework.MDPersistenceService;
import nc.pub.tools.HiCacheUtils;
import nc.pub.tools.HiSQLHelper;
import nc.pub.tools.VOUtils;
import nc.vo.bd.defdoc.DefdocVO;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.hi.psndoc.AssVO;
import nc.vo.hi.psndoc.BminfoVO;
import nc.vo.hi.psndoc.CapaVO;
import nc.vo.hi.psndoc.CertVO;
import nc.vo.hi.psndoc.CtrtVO;
import nc.vo.hi.psndoc.EduVO;
import nc.vo.hi.psndoc.PartTimeVO;
import nc.vo.hi.psndoc.PsnChgVO;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.hi.psndoc.QulifyVO;
import nc.vo.hi.psndoc.RetireVO;
import nc.vo.hi.psndoc.TrialVO;
import nc.vo.hi.psndoc.WainfoVO;
import nc.vo.hi.psndoc.WorkVO;
import nc.vo.hi.pub.HiBatchEventValueObject;
import nc.vo.hi.pub.HiEventValueObject;
import nc.vo.hr.infoset.InfoSetVO;
import nc.vo.hr.tools.pub.GeneralVO;
import nc.vo.hr.tools.pub.GeneralVOProcessor;
import nc.vo.om.job.JobVO;
import nc.vo.om.post.PostVO;
import nc.vo.org.AdminOrgVO;
import nc.vo.org.DeptVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.SuperVOUtil;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.sm.UserVO;
import nc.vo.sm.enumfactory.UserIdentityTypeEnumFactory;
import nc.vo.util.BDVersionValidationUtil;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class PersonRecordImpl extends SingleBaseService<PsndocVO> implements
		IPersonRecordService {
	public PersonRecordImpl() {
		super("218971f0-e5dc-408b-9a32-56529dddd4db");
	}

	public PsnJobVO addNewDimission(PsnJobVO dimission, String pkHrorg,
			boolean isDisablePsn) throws BusinessException {
		checkClerkCodeUnique(dimission);

		String pk_psnorg = dimission.getPk_psnorg();
		PsnJobVO before = (PsnJobVO) getLastVO(PsnJobVO.class, pk_psnorg,
				Integer.valueOf(1));
		if ((dimission.getPsntype() != null)
				&& (dimission.getPsntype().intValue() == 0)) {

			fireEvent(before, null, pkHrorg, "PSNJOB", "1001");
		}

		PsnOrgVO psnorg = (PsnOrgVO) getServiceTemplate().queryByPk(
				PsnOrgVO.class, pk_psnorg);
		psnorg.setPk_hrorg(pkHrorg);
		updateEndflagAndEnddate(psnorg, dimission.getBegindate());

		PsnJobVO[] psnjobs = (PsnJobVO[]) getServiceTemplate()
				.queryByCondition(
						PsnJobVO.class,
						" pk_psnorg = '" + pk_psnorg
								+ "' and ( endflag = 'N' or "
								+ SQLHelper.getNullSql("endflag") + " ) ");

		for (int i = 0; (psnjobs != null) && (i < psnjobs.length); i++) {
			updateEndflagAndEnddate(psnjobs[i], dimission.getBegindate());
			endWork(psnjobs[i], dimission);
		}

		PsnJobVO[] oldVOs = (PsnJobVO[]) getServiceTemplate().queryByCondition(
				PsnJobVO.class,
				" pk_psnorg = '" + pk_psnorg + "' and assgid = 1 ");
		updateRecordnumAndLastflag(oldVOs);

		TrialVO[] trial = (TrialVO[]) getServiceTemplate().queryByCondition(
				TrialVO.class,
				" pk_psnorg = '" + pk_psnorg + "' and  ( endflag = 'N' or "
						+ SQLHelper.getNullSql("endflag") + " ) ");

		for (int i = 0; (trial != null) && (i < trial.length); i++) {
			updateEndflagAndEnddate(trial[i], dimission.getBegindate());
		}

		getIPsndocService().updateDataAfterSubDataChanged(
				TrialVO.getDefaultTableName(),
				new String[] { dimission.getPk_psndoc() });

		synPkorgOfPsndoc(dimission.getPk_org(), dimission.getPk_psndoc());

		if ((dimission.getTrnstype() != null)
				&& ((dimission.getTrnstype().equals("1002Z710000000008GSV")) || (dimission
						.getTrnstype().equals("1002Z710000000008GSW")))) {

			String sql = " update bd_psndoc set retiredate = '"
					+ dimission.getBegindate() + "' where pk_psndoc = '"
					+ dimission.getPk_psndoc() + "' ";

			((IPersistenceUpdate) NCLocator.getInstance().lookup(
					IPersistenceUpdate.class))
					.executeSQLs(new String[] { sql });
		}

		PsnChgVO lastVO = (PsnChgVO) getLastVO(PsnChgVO.class, pk_psnorg, null);
		if ((lastVO != null) && (lastVO.getEnddate() == null)) {
			if ((lastVO.getBegindate() != null)
					&& (lastVO.getBegindate().compareTo(
							dimission.getBegindate()) >= 0)) {
				lastVO.setEnddate(lastVO.getBegindate());
			} else {
				lastVO.setEnddate(dimission.getBegindate().getDateBefore(1));
			}
			getServiceTemplate().update(lastVO, false);
			getIPsndocService().updateDataAfterSubDataChanged(
					PsnChgVO.getDefaultTableName(),
					new String[] { lastVO.getPk_psndoc() });
		}

		dimission.setPk_hrorg(pkHrorg);
		PsnJobVO after = (PsnJobVO) getServiceTemplate().insert(dimission);

		getIPsndocService().updateDataAfterSubDataChanged(
				PsnJobVO.getDefaultTableName(),
				new String[] { after.getPk_psndoc() });

		// ssx added for Taiwan NHI on 2015-10-15
		// Stop NHI Info
		// 2017-05-16 upgrade to V65, from JD code
		IPsndocSubInfoService4JFS nhiService = NCLocator.getInstance().lookup(
				IPsndocSubInfoService4JFS.class);
		nhiService.dismissPsnNHI(dimission.getPk_org(), dimission
				.getPk_psndoc(), dimission.getBegindate().getDateBefore(1));
		// ssx added on 2017-12-19
		// ???�?F�??Y??????
		nhiService.dismissPsnGroupIns(dimission.getPk_org(), dimission
				.getPk_psndoc(), dimission.getBegindate().getDateBefore(1));

		if (isDisablePsn) {
			PsndocVO psndocVO = (PsndocVO) ((IPersistenceRetrieve) NCLocator
					.getInstance().lookup(IPersistenceRetrieve.class))
					.retrieveByPk(null, PsndocVO.class,
							dimission.getPk_psndoc());

			disableSingleVO(psndocVO);
		}

		String pk_psndoc = after.getPk_psndoc();
		String condition = " base_doc_type = "
				+ UserIdentityTypeEnumFactory.TYPE_PERSON
				+ " and pk_base_doc = '" + pk_psndoc + "' and enablestate = "
				+ 2;

		UserVO[] users = ((IUserManageQuery) NCLocator.getInstance().lookup(
				IUserManageQuery.class)).queryUserByClause(condition);
		if ((users != null) && (users.length > 0)) {
			for (UserVO user : users) {
				((IUserManage) NCLocator.getInstance()
						.lookup(IUserManage.class)).disableUser(user);
			}
		}

		if ((dimission.getPsntype() != null)
				&& (dimission.getPsntype().intValue() == 0)) {

			fireEvent(before, after, pkHrorg, "PSNJOB", "1002");
			HiEventValueObject
					.fireDataPermChangeEvent("7156d223-4531-4337-b192-492ab40098f1");
		}

		HiCacheUtils
				.synCache(new String[] { PsndocVO.getDefaultTableName(),
						PsnJobVO.getDefaultTableName(),
						PsnOrgVO.getDefaultTableName() });

		return after;
	}

	public PsnJobVO addNewDimission(PsnJobVO dimission, String pkHrorg,
			boolean isDisablePsn, String pk_hrcm_org) throws BusinessException {
		checkClerkCodeUnique(dimission);

		String pk_psnorg = dimission.getPk_psnorg();
		PsnJobVO before = (PsnJobVO) getLastVO(PsnJobVO.class, pk_psnorg,
				Integer.valueOf(1));
		if ((dimission.getPsntype() != null)
				&& (dimission.getPsntype().intValue() == 0)) {

			fireEvent(before, null, pkHrorg, "PSNJOB", "1001");
		}

		PsnOrgVO psnorg = (PsnOrgVO) getServiceTemplate().queryByPk(
				PsnOrgVO.class, pk_psnorg);
		psnorg.setPk_hrorg(pkHrorg);
		updateEndflagAndEnddate(psnorg, dimission.getBegindate());

		PsnJobVO[] psnjobs = (PsnJobVO[]) getServiceTemplate()
				.queryByCondition(
						PsnJobVO.class,
						" pk_psnorg = '" + pk_psnorg
								+ "' and ( endflag = 'N' or "
								+ SQLHelper.getNullSql("endflag") + " ) ");

		for (int i = 0; (psnjobs != null) && (i < psnjobs.length); i++) {
			updateEndflagAndEnddate(psnjobs[i], dimission.getBegindate());
			endWork(psnjobs[i], dimission);
		}

		PsnJobVO[] oldVOs = (PsnJobVO[]) getServiceTemplate().queryByCondition(
				PsnJobVO.class,
				" pk_psnorg = '" + pk_psnorg + "' and assgid = 1 ");
		updateRecordnumAndLastflag(oldVOs);

		TrialVO[] trial = (TrialVO[]) getServiceTemplate().queryByCondition(
				TrialVO.class,
				" pk_psnorg = '" + pk_psnorg + "' and  ( endflag = 'N' or "
						+ SQLHelper.getNullSql("endflag") + " ) ");

		for (int i = 0; (trial != null) && (i < trial.length); i++) {
			updateEndflagAndEnddate(trial[i], dimission.getBegindate());
		}

		getIPsndocService().updateDataAfterSubDataChanged(
				TrialVO.getDefaultTableName(),
				new String[] { dimission.getPk_psndoc() });

		synPkorgOfPsndoc(dimission.getPk_org(), dimission.getPk_psndoc());

		if ((dimission.getTrnstype() != null)
				&& ((dimission.getTrnstype().equals("1002Z710000000008GSV")) || (dimission
						.getTrnstype().equals("1002Z710000000008GSW")))) {

			String sql = " update bd_psndoc set retiredate = '"
					+ dimission.getBegindate() + "' where pk_psndoc = '"
					+ dimission.getPk_psndoc() + "' ";

			((IPersistenceUpdate) NCLocator.getInstance().lookup(
					IPersistenceUpdate.class))
					.executeSQLs(new String[] { sql });
		}

		PsnChgVO lastVO = (PsnChgVO) getLastVO(PsnChgVO.class, pk_psnorg, null);
		if ((lastVO != null) && (lastVO.getEnddate() == null)) {
			if ((lastVO.getBegindate() != null)
					&& (lastVO.getBegindate().compareTo(
							dimission.getBegindate()) >= 0)) {
				lastVO.setEnddate(lastVO.getBegindate());
			} else {
				lastVO.setEnddate(dimission.getBegindate().getDateBefore(1));
			}
			getServiceTemplate().update(lastVO, false);
			getIPsndocService().updateDataAfterSubDataChanged(
					PsnChgVO.getDefaultTableName(),
					new String[] { lastVO.getPk_psndoc() });
		}

		dimission.setPk_hrorg(pkHrorg);
		PsnJobVO after = (PsnJobVO) getServiceTemplate().insert(dimission);

		getIPsndocService().updateDataAfterSubDataChanged(
				PsnJobVO.getDefaultTableName(),
				new String[] { after.getPk_psndoc() });

		if (isDisablePsn) {
			PsndocVO psndocVO = (PsndocVO) ((IPersistenceRetrieve) NCLocator
					.getInstance().lookup(IPersistenceRetrieve.class))
					.retrieveByPk(null, PsndocVO.class,
							dimission.getPk_psndoc());

			disableSingleVO(psndocVO);
		}

		String pk_psndoc = after.getPk_psndoc();
		String condition = " base_doc_type = "
				+ UserIdentityTypeEnumFactory.TYPE_PERSON
				+ " and pk_base_doc = '" + pk_psndoc + "' and enablestate = "
				+ 2;

		UserVO[] users = ((IUserManageQuery) NCLocator.getInstance().lookup(
				IUserManageQuery.class)).queryUserByClause(condition);
		if ((users != null) && (users.length > 0)) {
			for (UserVO user : users) {
				((IUserManage) NCLocator.getInstance()
						.lookup(IUserManage.class)).disableUser(user);
			}
		}

		if (StringUtils.isNotBlank(pk_hrcm_org)) {
			after.setAttributeValue("pk_hrcm_org", pk_hrcm_org);
		}

		if ((dimission.getPsntype() != null)
				&& (dimission.getPsntype().intValue() == 0)) {

			fireEvent(before, after, pkHrorg, "PSNJOB", "1002");
			HiEventValueObject
					.fireDataPermChangeEvent("7156d223-4531-4337-b192-492ab40098f1");
		}

		HiCacheUtils
				.synCache(new String[] { PsndocVO.getDefaultTableName(),
						PsnJobVO.getDefaultTableName(),
						PsnOrgVO.getDefaultTableName() });

		return after;
	}

	public void addNewPsnjobs(PsnJobVO[] psnjobs, boolean isSyncWork,
			String[] hrOrgs) throws BusinessException {
		if ((psnjobs == null) || (psnjobs.length == 0)) {
			return;
		}

		BaseDAO dao = new BaseDAO();
		IPersistenceRetrieve retrieve = (IPersistenceRetrieve) NCLocator
				.getInstance().lookup(IPersistenceRetrieve.class);
		IPersistenceUpdate update = (IPersistenceUpdate) NCLocator
				.getInstance().lookup(IPersistenceUpdate.class);

		ArrayList<String> pkDept = new ArrayList();
		ArrayList<String> pkPost = new ArrayList();
		for (int i = 0; i < psnjobs.length; i++) {
			if ((psnjobs[i] == null) || (psnjobs[i].getEndflag() == null)
					|| (!psnjobs[i].getEndflag().booleanValue())) {

				if ((psnjobs[i].getPk_dept() != null)
						&& (!pkDept.contains(psnjobs[i].getPk_dept()))) {
					pkDept.add(psnjobs[i].getPk_dept());
				}
				if ((psnjobs[i].getPk_post() != null)
						&& (!pkPost.contains(psnjobs[i].getPk_post()))) {
					pkPost.add(psnjobs[i].getPk_post());
				}
			}
		}
		String[] pk_psnorg = new String[psnjobs.length];
		for (int i = 0; i < psnjobs.length; i++) {
			pk_psnorg[i] = psnjobs[i].getPk_psnorg();
		}
		InSQLCreator ttu = null;
		try {
			ttu = new InSQLCreator();

			if (pkDept.size() > 0) {
				String sql1 = " select count(*) from org_dept where hrcanceled = 'Y' and pk_dept in ( "
						+ ttu.getInSQL((String[]) pkDept.toArray(new String[0]))
						+ " ) ";

				Object obj = dao.executeQuery(sql1, new ColumnProcessor());
				if ((obj != null) && (((Integer) obj).intValue() > 0)) {
					throw new BusinessException(ResHelper.getString("6007psn",
							"06007psn0323"));
				}
			}

			if (pkPost.size() > 0) {
				String sql2 = " select count(*) from om_post where hrcanceled ='Y' and pk_post in ( "
						+ ttu.getInSQL((String[]) pkPost.toArray(new String[0]))
						+ " ) ";

				Object obj = dao.executeQuery(sql2, new ColumnProcessor());
				if ((obj != null) && (((Integer) obj).intValue() > 0)) {
					throw new BusinessException(ResHelper.getString("6007psn",
							"06007psn0324"));
				}
			}

			String psnorgInSql = ttu.getInSQL(pk_psnorg);

			String validateSql = " select count(*) from hi_psnjob where lastflag = 'Y' and assgid = 1 and pk_psnorg in ("
					+ psnorgInSql
					+ ") and ( ( isnull(enddate,'~')<>'~' and enddate>='"
					+ psnjobs[0].getBegindate()
					+ "' ) or (isnull(enddate,'~')='~' and isnull(begindate,'~')<>'~' and begindate>='"
					+ psnjobs[0].getBegindate() + "' ) )";

			Object obj = dao.executeQuery(validateSql, new ColumnProcessor());
			if ((obj != null) && (((Integer) obj).intValue() > 0)) {
				throw new BusinessException(ResHelper.getString("6007psn",
						"06007psn0276"));
			}

			HashMap<String, PsnJobVO> afterMap = new HashMap();
			for (int i = 0; i < psnjobs.length; i++) {
				afterMap.put(psnjobs[i].getPk_psnorg(), psnjobs[i]);
			}

			String queryCond = " pk_psnorg in (" + psnorgInSql
					+ ") and assgid = 1 and lastflag = 'Y' ";

			PsnJobVO[] temp = (PsnJobVO[]) retrieve.retrieveByClause(null,
					PsnJobVO.class, queryCond);
			if ((temp == null) || (temp.length != psnjobs.length)) {
				throw new BusinessException(
						BDVersionValidationUtil.getUpdateInfo());
			}

			HashMap<String, PsnJobVO> beforeMap = new HashMap();
			for (int i = 0; i < temp.length; i++) {
				beforeMap.put(temp[i].getPk_psnorg(), temp[i]);
			}

			PsnJobVO[] lastVOs = new PsnJobVO[psnjobs.length];
			for (int i = 0; i < lastVOs.length; i++) {
				lastVOs[i] = ((PsnJobVO) beforeMap.get(psnjobs[i]
						.getPk_psnorg()));
			}

			ArrayList<String> al = new ArrayList();
			for (int i = 0; i < psnjobs.length; i++) {
				if (!al.contains(psnjobs[i].getPk_psndoc())) {
					al.add(psnjobs[i].getPk_psndoc());
				}
			}
			HiBatchEventValueObject.fireEvent(lastVOs, null, hrOrgs, "PSNJOB",
					"1001");

			checkClerkCodeUnique(psnjobs);

			UFLiteralDate enddate = psnjobs[0].getBegindate().getDateBefore(1);
			String updateSql1 = " update hi_psnjob set enddate = '"
					+ enddate
					+ "',endflag='Y',poststat ='N' where pk_psnorg in ("
					+ psnorgInSql
					+ ") and assgid = 1 and lastflag = 'Y' and isnull(enddate,'~') = '~' ";

			dao.executeUpdate(updateSql1);

			if (isSyncWork) {
				PsnJobVO[] linshi = (PsnJobVO[]) retrieve.retrieveByClause(
						null, PsnJobVO.class, queryCond);

				ArrayList<String> sqlList = new ArrayList();
				for (int i = 0; (linshi != null) && (i < linshi.length); i++) {
					String s = " update hi_psndoc_work set enddate = ( case when begindate <='"
							+ linshi[i].getEnddate()
							+ "' then '"
							+ linshi[i].getEnddate()
							+ "' else begindate end ) where pk_psnjob = '"
							+ linshi[i].getPk_psnjob() + "' ";

					sqlList.add(s);
				}
				if (sqlList.size() > 0) {
					update.executeSQLs((String[]) sqlList
							.toArray(new String[0]));
				}
			}

			String updateSql2 = " update hi_psnjob set lastflag ='N',recordnum = recordnum + 1 where pk_psnorg in ("
					+ psnorgInSql + ") and assgid = 1 ";

			dao.executeUpdate(updateSql2);

			TrialVO[] lastTrailVOs = (TrialVO[]) retrieve.retrieveByClause(
					null, TrialVO.class, " pk_psnorg in (" + psnorgInSql
							+ ") and lastflag = 'Y' ");

			for (int i = 0; (lastTrailVOs != null) && (i < lastTrailVOs.length); i++) {
				PsnJobVO job = (PsnJobVO) afterMap.get(lastTrailVOs[i]
						.getPk_psnorg());
				if ((lastTrailVOs[i].getEndflag() != null)
						&& (!lastTrailVOs[i].getEndflag().booleanValue())) {

					if ((job.getTrnsevent() != null)
							&& (job.getTrnsevent().intValue() == 2)) {
						lastTrailVOs[i].setEndflag(UFBoolean.TRUE);
						lastTrailVOs[i].setTrialresult(Integer.valueOf(1));
						lastTrailVOs[i].setRegulardate(psnjobs[0]
								.getBegindate());
						if (lastTrailVOs[i].getEnddate() == null) {
							lastTrailVOs[i].setEnddate(psnjobs[0]
									.getBegindate());
						}
						lastTrailVOs[i].setStatus(1);
					} else {
						job.setTrial_flag(UFBoolean.TRUE);
						job.setTrial_type(lastTrailVOs[i].getTrial_type());
					}

				} else {
					job.setTrial_flag(UFBoolean.FALSE);
					job.setTrial_type(null);
				}
			}

			dao.updateVOArray(lastTrailVOs, new String[] { "endflag",
					"trialresult", "regulardate", "enddate" });

			for (int i = 0; i < psnjobs.length; i++) {
				psnjobs[i].setPk_hrorg(hrOrgs[i]);
			}
			insert4SubVOs(psnjobs);

			PsnJobVO[] afterList = (PsnJobVO[]) retrieve.retrieveByClause(null,
					PsnJobVO.class, queryCond);
			if ((afterList == null) || (afterList.length != psnjobs.length)) {
				throw new BusinessException(
						BDVersionValidationUtil.getUpdateInfo());
			}

			HashMap<String, PsnJobVO> afterSaveMap = new HashMap();
			for (int i = 0; i < afterList.length; i++) {
				afterSaveMap.put(afterList[i].getPk_psnorg(), afterList[i]);
			}

			PsnJobVO[] afterVOs = new PsnJobVO[psnjobs.length];
			for (int i = 0; i < afterVOs.length; i++) {
				afterVOs[i] = ((PsnJobVO) afterSaveMap.get(psnjobs[i]
						.getPk_psnorg()));
			}

			batchAddPsnChg(lastVOs, afterVOs);

			if (isSyncWork) {

				batchAddWorkWhenPsnjobAdd(afterVOs);
			}

			ArrayList<String> updateSqls = new ArrayList();
			for (int i = 0; i < afterVOs.length; i++) {

				updateSqls.add(" update bd_psndoc set pk_org = '"
						+ afterVOs[i].getPk_org() + "' where pk_psndoc = '"
						+ afterVOs[i].getPk_psndoc() + "' ");

				updateSqls.add(" update hi_psnorg set pk_hrorg = '"
						+ afterVOs[i].getPk_hrorg() + "' where pk_psnorg = '"
						+ afterVOs[i].getPk_psnorg() + "' ");
			}

			if (updateSqls.size() > 0) {
				update.executeSQLs((String[]) updateSqls.toArray(new String[0]));
			}

			getIPsndocService().updateDataAfterSubDataChanged(
					WorkVO.getDefaultTableName(),
					(String[]) al.toArray(new String[0]));
			getIPsndocService().updateDataAfterSubDataChanged(
					TrialVO.getDefaultTableName(),
					(String[]) al.toArray(new String[0]));
			getIPsndocService().updateDataAfterSubDataChanged(
					PsnChgVO.getDefaultTableName(),
					(String[]) al.toArray(new String[0]));
			getIPsndocService().updateDataAfterSubDataChanged(
					PsnJobVO.getDefaultTableName(),
					(String[]) al.toArray(new String[0]));

			HiBatchEventValueObject.fireEvent(lastVOs, afterVOs, hrOrgs,
					"PSNJOB", "1002");
			HiEventValueObject
					.fireDataPermChangeEvent("7156d223-4531-4337-b192-492ab40098f1");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (ttu != null) {

				ttu.clear();
			}
		}

		HiCacheUtils
				.synCache(new String[] { PsndocVO.getDefaultTableName(),
						PsnJobVO.getDefaultTableName(),
						PsnOrgVO.getDefaultTableName() });
	}

	private void batchAddPsnChg(PsnJobVO[] lastVOs, PsnJobVO[] afterVOs)
			throws BusinessException {
		ArrayList<String> orgPKList = new ArrayList();
		for (int i = 0; i < afterVOs.length; i++) {
			if ((lastVOs[i].getPk_org() != null)
					&& (!orgPKList.contains(lastVOs[i].getPk_org()))) {
				orgPKList.add(lastVOs[i].getPk_org());
			}

			if ((afterVOs[i].getPk_org() != null)
					&& (!orgPKList.contains(afterVOs[i].getPk_org()))) {
				orgPKList.add(afterVOs[i].getPk_org());
			}
		}

		String id = DBAUtil.getIdGenerator().generate();
		String tableName = DBAUtil.createTempTable("temp" + id.substring(9),
				" pk_org varchar(20) ", "pk_org");
		String[] insertSql = new String[orgPKList.size()];

		for (int i = 0; i < insertSql.length; i++) {
			insertSql[i] = (" insert into " + tableName + " values ( '"
					+ (String) orgPKList.get(i) + "' )");
		}
		DBAUtil.execBatchSql(insertSql);

		String corpsql = " select  hr_orgs1.pk_corp pk_corp , hr_orgs2.pk_org                          from (select corp.pk_corp,                                                            a.innercode aos_innercode ,                                               "
				+ tableName
				+ ".pk_org pkorg                                               "
				+ "       from org_adminorg a                                                       "
				+ "        inner join org_corp corp                                                       "
				+ "        on a.pk_adminorg = corp.pk_corp                                             "
				+ "        inner join org_adminorg b                                                "
				+ "        on a.innercode = substring ( b.innercode, 1, len( a.innercode ) )         "
				+ "        inner join "
				+ tableName
				+ "        on b.pk_adminorg = "
				+ tableName
				+ ".pk_org                                       "
				+ "       where corp.enablestate =2                                              "
				+ "  )                                                                               "
				+ "  hr_orgs1                                                                        "
				+ "   inner join (select max ( len(a.innercode) ) inlength ,                         "
				+ "                "
				+ tableName
				+ ".pk_org                                             "
				+ "               from org_adminorg a                                               "
				+ "                inner join org_corp corp                                               "
				+ "                on a.pk_adminorg = corp.pk_corp                                     "
				+ "                inner join org_adminorg b                                        "
				+ "                on a.innercode = substring ( b.innercode, 1, len( a.innercode ) ) "
				+ "                inner join "
				+ tableName
				+ "                on b.pk_adminorg = "
				+ tableName
				+ ".pk_org                               "
				+ "               where corp.enablestate = 2                                      "
				+ "               group by "
				+ tableName
				+ ".pk_org                                     "
				+ "   )                                                                              "
				+ "   hr_orgs2                                                                       "
				+ "   on hr_orgs1.pkorg = hr_orgs2.pk_org                                            "
				+ " where len( hr_orgs1.aos_innercode ) = hr_orgs2.inlength                          ";

		BaseDAO dao = new BaseDAO();
		GeneralVO[] corpVOs = (GeneralVO[]) dao.executeQuery(corpsql,
				new GeneralVOProcessor(GeneralVO.class));
		HashMap<String, String> corpMap = new HashMap();
		for (int i = 0; (corpVOs != null) && (i < corpVOs.length); i++) {
			corpMap.put((String) corpVOs[i].getAttributeValue("pk_org"),
					(String) corpVOs[i].getAttributeValue("pk_corp"));
		}

		if (DBAUtil.isExist(" select 1 from " + tableName)) {

			DBAUtil.execBatchSql(new String[] { " delete  from  " + tableName });
		}

		IPersistenceRetrieve retrieve = (IPersistenceRetrieve) NCLocator
				.getInstance().lookup(IPersistenceRetrieve.class);
		String[] corpPK = (String[]) corpMap.keySet().toArray(new String[0]);
		HashMap<String, String> corpNameMap = new HashMap();
		InSQLCreator isc = null;
		try {
			isc = new InSQLCreator();
			OrgVO[] vos = (OrgVO[]) retrieve.retrieveByClause(null,
					OrgVO.class, " pk_org in ( " + isc.getInSQL(corpPK) + " )");

			for (int i = 0; (vos != null) && (i < vos.length); i++) {
				corpNameMap.put(vos[i].getPk_org(),
						MultiLangHelper.getName(vos[i]));
			}
		} finally {
			if (isc != null) {
				isc.clear();
			}
		}

		ArrayList<PsnJobVO> al = new ArrayList();
		for (int i = 0; i < afterVOs.length; i++) {

			String pk_oldorg = lastVOs[i].getPk_org();
			String pk_neworg = afterVOs[i].getPk_org();
			String pk_oldcorp = (String) corpMap.get(pk_oldorg);
			String pk_newcorp = (String) corpMap.get(pk_neworg);
			if ((pk_oldcorp != null) && (pk_newcorp != null)
					&& (!pk_oldcorp.equals(pk_newcorp))) {
				al.add(afterVOs[i]);
			}
		}

		if (al.size() == 0) {
			return;
		}

		String[] psnorgPk = new String[al.size()];
		for (int i = 0; i < al.size(); i++) {
			psnorgPk[i] = ((PsnJobVO) al.get(i)).getPk_psnorg();
		}
		InSQLCreator ttc = null;
		try {
			ttc = new InSQLCreator();

			String psnorgInSql = ttc.getInSQL(psnorgPk);

			UFLiteralDate beginDate = ((PsnJobVO) al.get(0)).getBegindate();

			String validateSql = " select count(*) from hi_psndoc_psnchg where lastflag = 'Y' and pk_psnorg in ("
					+ psnorgInSql
					+ ") and ( ( isnull(enddate,'~')<>'~' and enddate>='"
					+ beginDate
					+ "' ) or (isnull(enddate,'~')='~' and isnull(begindate,'~')<>'~' and begindate>='"
					+ beginDate + "' ) )";

			Object obj = dao.executeQuery(validateSql, new ColumnProcessor());
			if ((obj != null) && (((Integer) obj).intValue() > 0)) {
				throw new BusinessException(ResHelper.getString("6007psn",
						"06007psn0271"));
			}

			PsnChgVO[] hisVOs = (PsnChgVO[]) retrieve.retrieveByClause(null,
					PsnChgVO.class, " lastflag = 'Y' and pk_psnorg in ("
							+ psnorgInSql + ") ");

			if ((hisVOs == null) || (hisVOs.length != al.size())) {
				throw new BusinessException(ResHelper.getString("6007psn",
						"06007psn0350"));
			}

			HashMap<String, PsnChgVO> hisMap = new HashMap();
			for (PsnChgVO vo : hisVOs) {
				hisMap.put(vo.getPk_psnorg(), vo);
			}

			ArrayList<PsnChgVO> updateHisVO = new ArrayList();
			ArrayList<PsnChgVO> newVO = new ArrayList();

			for (int i = 0; i < al.size(); i++) {
				String preToCorpName = (String) corpNameMap.get(corpMap
						.get(((PsnJobVO) al.get(i)).getPk_org()));
				PsnChgVO last = (PsnChgVO) hisMap.get(((PsnJobVO) al.get(i))
						.getPk_psnorg());
				last.setTocorpname(preToCorpName);
				last.setEnddate(beginDate.getDateBefore(1));
				updateHisVO.add(last);

				PsnChgVO psnchg = new PsnChgVO();
				psnchg.setRecordnum(Integer.valueOf(0));
				psnchg.setLastflag(UFBoolean.TRUE);
				psnchg.setPk_group(((PsnJobVO) al.get(i)).getPk_hrgroup());
				psnchg.setPk_org(((PsnJobVO) al.get(i)).getPk_hrorg());
				psnchg.setPk_psndoc(((PsnJobVO) al.get(i)).getPk_psndoc());
				psnchg.setPk_psnorg(((PsnJobVO) al.get(i)).getPk_psnorg());
				psnchg.setAssgid(((PsnJobVO) al.get(i)).getAssgid());
				psnchg.setBegindate(((PsnJobVO) al.get(i)).getBegindate());
				psnchg.setComecorpname((String) corpNameMap.get(last
						.getPk_corp()));
				psnchg.setTocorpname(null);
				psnchg.setPk_corp((String) corpMap.get(((PsnJobVO) al.get(i))
						.getPk_org()));
				psnchg.setCreator(PubEnv.getPk_user());
				psnchg.setCreationtime(PubEnv.getServerTime());
				newVO.add(psnchg);
			}

			String updateSql = " update hi_psndoc_psnchg set lastflag = 'N',recordnum = recordnum+1 where pk_psnorg in ("
					+ psnorgInSql + ") ";

			dao.executeUpdate(updateSql);

			dao.updateVOArray((SuperVO[]) updateHisVO.toArray(new PsnChgVO[0]),
					new String[] { "tocorpname", "enddate" });

			dao.insertVOArray((SuperVO[]) newVO.toArray(new PsnChgVO[0]));

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (ttc != null) {

				ttc.clear();
			}
		}
	}

	public PsnJobVO addNewPsnjob(PsnJobVO saveData, boolean isSynWork,
			String pk_hrorg) throws BusinessException {
		checkClerkCodeUnique(saveData);

		checkDeptPostCanceled(saveData);

		String pk_psnorg = saveData.getPk_psnorg();
		PsnJobVO last = (PsnJobVO) getLastVO(PsnJobVO.class, pk_psnorg,
				Integer.valueOf(1));
		if ((last != null)
				&& ((last.getBegindate().afterDate(saveData.getBegindate())) || (last
						.getBegindate().isSameDate(saveData.getBegindate())))) {

			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0270"));
		}

		fireEvent(last, null, pk_hrorg, "PSNJOB", "1001");
		PsnJobVO[] hisVOs = (PsnJobVO[]) getServiceTemplate().queryByCondition(
				PsnJobVO.class,
				" pk_psnorg = '" + pk_psnorg + "' and assgid = 1 ");
		for (int i = 0; (hisVOs != null) && (i < hisVOs.length); i++) {
			if (hisVOs[i].getLastflag().booleanValue()) {

				hisVOs[i] = ((PsnJobVO) updateEndflagAndEnddate(hisVOs[i],
						saveData.getBegindate()));
				if (isSynWork) {
					endWork(hisVOs[i].getPk_psnjob(), hisVOs[i].getEnddate());
				}
			}
		}
		updateRecordnumAndLastflag(hisVOs);

		TrialVO trial = (TrialVO) getLastVO(TrialVO.class, pk_psnorg, null);
		if (trial != null) {
			if ((trial.getEndflag() != null)
					&& (!trial.getEndflag().booleanValue())) {
				if ((saveData.getTrnsevent() != null)
						&& (saveData.getTrnsevent().intValue() == 2)) {

					updateEndflagAndEnddate(trial, saveData.getBegindate());

					getIPsndocService().updateDataAfterSubDataChanged(
							TrialVO.getDefaultTableName(),
							new String[] { saveData.getPk_psndoc() });

				} else {
					saveData.setTrial_flag(UFBoolean.TRUE);
					saveData.setTrial_type(trial.getTrial_type());
				}

			} else {
				saveData.setTrial_flag(UFBoolean.FALSE);
				saveData.setTrial_type(null);
			}
		}

		saveData.setPk_hrorg(pk_hrorg);
		PsnJobVO retVO = (PsnJobVO) getServiceTemplate().insert(saveData);
		if (isSynWork) {

			addWork(retVO);
		}

		addPsnChg(retVO, true);

		getIPsndocService().updateDataAfterSubDataChanged(
				PsnJobVO.getDefaultTableName(),
				new String[] { retVO.getPk_psndoc() });

		synPkorgOfPsndoc(retVO.getPk_org(), retVO.getPk_psndoc());

		synPkhrorgOfPsnorg(retVO.getPk_psnorg(), retVO.getPk_hrorg());

		fireEvent(last, retVO, pk_hrorg, "PSNJOB", "1002");
		HiEventValueObject
				.fireDataPermChangeEvent("7156d223-4531-4337-b192-492ab40098f1");

		HiCacheUtils
				.synCache(new String[] { PsndocVO.getDefaultTableName(),
						PsnJobVO.getDefaultTableName(),
						PsnOrgVO.getDefaultTableName() });

		return retVO;
	}

	public PsnJobVO addNewPsnjobTran(PsnJobVO saveData, boolean isSynWork,
			boolean isFinshPart, String pk_hrorg) throws BusinessException {
		checkClerkCodeUnique(saveData);

		checkDeptPostCanceled(saveData);

		String pk_psnorg = saveData.getPk_psnorg();
		PsnJobVO last = (PsnJobVO) getLastVO(PsnJobVO.class, pk_psnorg,
				Integer.valueOf(1));
		if ((last != null)
				&& ((last.getBegindate().afterDate(saveData.getBegindate())) || (last
						.getBegindate().isSameDate(saveData.getBegindate())))) {

			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0270"));
		}

		fireEvent(last, null, pk_hrorg, "PSNJOB", "1001");
		PsnJobVO[] hisVOs = (PsnJobVO[]) getServiceTemplate().queryByCondition(
				PsnJobVO.class,
				" pk_psnorg = '" + pk_psnorg + "' and assgid = 1 ");
		for (int i = 0; (hisVOs != null) && (i < hisVOs.length); i++) {
			if (hisVOs[i].getLastflag().booleanValue()) {

				hisVOs[i] = ((PsnJobVO) updateEndflagAndEnddate(hisVOs[i],
						saveData.getBegindate()));
				if (isSynWork) {
					endWork(hisVOs[i].getPk_psnjob(), hisVOs[i].getEnddate());
				}
			}
		}
		updateRecordnumAndLastflag(hisVOs);

		TrialVO trial = (TrialVO) getLastVO(TrialVO.class, pk_psnorg, null);
		if (trial != null) {
			if ((trial.getEndflag() != null)
					&& (!trial.getEndflag().booleanValue())) {
				if ((saveData.getTrnsevent() != null)
						&& (saveData.getTrnsevent().intValue() == 2)) {

					updateEndflagAndEnddate(trial, saveData.getBegindate());

					getIPsndocService().updateDataAfterSubDataChanged(
							TrialVO.getDefaultTableName(),
							new String[] { saveData.getPk_psndoc() });

				} else {
					saveData.setTrial_flag(UFBoolean.TRUE);
					saveData.setTrial_type(trial.getTrial_type());
				}

			} else {
				saveData.setTrial_flag(UFBoolean.FALSE);
				saveData.setTrial_type(null);
			}
		}

		saveData.setPk_hrorg(pk_hrorg);
		PsnJobVO retVO = (PsnJobVO) getServiceTemplate().insert(saveData);
		if (isSynWork) {

			addWork(retVO);
		}

		if (isFinshPart) {

			PartTimeVO[] part = (PartTimeVO[]) getServiceTemplate()
					.queryByCondition(
							PartTimeVO.class,
							" pk_psnorg = '" + saveData.getPk_psnorg()
									+ "' and assgid > 1 and ( "
									+ SQLHelper.getNullSql("endflag")
									+ " or endflag = 'N' ) ");

			UFLiteralDate enddate = saveData.getBegindate();
			for (int i = 0; (part != null) && (i < part.length); i++) {
				getIPersonRecordService().updateEndflagAndEnddate(part[i],
						enddate);

				if (isSynWork) {

					getIPersonRecordService().endWork(part[i].getPk_psnjob(),
							enddate.getDateBefore(1));
				}
			}
		}

		addPsnChg(retVO, true);

		getIPsndocService().updateDataAfterSubDataChanged(
				PsnJobVO.getDefaultTableName(),
				new String[] { retVO.getPk_psndoc() });

		synPkorgOfPsndoc(retVO.getPk_org(), retVO.getPk_psndoc());

		synPkhrorgOfPsnorg(retVO.getPk_psnorg(), retVO.getPk_hrorg());

		updateUserOrg(retVO, pk_hrorg);

		fireEvent(last, retVO, pk_hrorg, "PSNJOB", "1002");
		HiEventValueObject
				.fireDataPermChangeEvent("7156d223-4531-4337-b192-492ab40098f1");

		HiCacheUtils
				.synCache(new String[] { PsndocVO.getDefaultTableName(),
						PsnJobVO.getDefaultTableName(),
						PsnOrgVO.getDefaultTableName() });

		return retVO;
	}

	private IPersonRecordService getIPersonRecordService() {
		return (IPersonRecordService) NCLocator.getInstance().lookup(
				IPersonRecordService.class);
	}

	public PsnJobVO addNewPsnjob(PsnJobVO saveData, boolean isSynWork,
			String pk_hrorg, String pk_hrcm_org) throws BusinessException {
		checkClerkCodeUnique(saveData);

		checkDeptPostCanceled(saveData);

		String pk_psnorg = saveData.getPk_psnorg();
		PsnJobVO last = (PsnJobVO) getLastVO(PsnJobVO.class, pk_psnorg,
				Integer.valueOf(1));
		if ((last != null)
				&& ((last.getBegindate().afterDate(saveData.getBegindate())) || (last
						.getBegindate().isSameDate(saveData.getBegindate())))) {

			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0270"));
		}

		fireEvent(last, null, pk_hrorg, "PSNJOB", "1001");
		PsnJobVO[] hisVOs = (PsnJobVO[]) getServiceTemplate().queryByCondition(
				PsnJobVO.class,
				" pk_psnorg = '" + pk_psnorg + "' and assgid = 1 ");
		for (int i = 0; (hisVOs != null) && (i < hisVOs.length); i++) {
			if (hisVOs[i].getLastflag().booleanValue()) {

				hisVOs[i] = ((PsnJobVO) updateEndflagAndEnddate(hisVOs[i],
						saveData.getBegindate()));
				if (isSynWork) {
					endWork(hisVOs[i].getPk_psnjob(), hisVOs[i].getEnddate());
				}
			}
		}
		updateRecordnumAndLastflag(hisVOs);

		TrialVO trial = (TrialVO) getLastVO(TrialVO.class, pk_psnorg, null);
		if (trial != null) {
			if ((trial.getEndflag() != null)
					&& (!trial.getEndflag().booleanValue())) {
				if ((saveData.getTrnsevent() != null)
						&& (saveData.getTrnsevent().intValue() == 2)) {

					updateEndflagAndEnddate(trial, saveData.getBegindate());

					getIPsndocService().updateDataAfterSubDataChanged(
							TrialVO.getDefaultTableName(),
							new String[] { saveData.getPk_psndoc() });

				} else {
					saveData.setTrial_flag(UFBoolean.TRUE);
					saveData.setTrial_type(trial.getTrial_type());
				}

			} else {
				saveData.setTrial_flag(UFBoolean.FALSE);
				saveData.setTrial_type(null);
			}
		}

		saveData.setPk_hrorg(pk_hrorg);
		PsnJobVO retVO = (PsnJobVO) getServiceTemplate().insert(saveData);
		if (isSynWork) {

			addWork(retVO);
		}

		addPsnChg(retVO, true);

		getIPsndocService().updateDataAfterSubDataChanged(
				PsnJobVO.getDefaultTableName(),
				new String[] { retVO.getPk_psndoc() });

		synPkorgOfPsndoc(retVO.getPk_org(), retVO.getPk_psndoc());

		synPkhrorgOfPsnorg(retVO.getPk_psnorg(), retVO.getPk_hrorg());

		if (StringUtils.isNotBlank(pk_hrcm_org)) {
			retVO.setAttributeValue("pk_hrcm_org", pk_hrcm_org);
		}

		updateUserOrg(retVO, pk_hrorg);

		fireEvent(last, retVO, pk_hrorg, "PSNJOB", "1002");
		HiEventValueObject
				.fireDataPermChangeEvent("7156d223-4531-4337-b192-492ab40098f1");

		HiCacheUtils
				.synCache(new String[] { PsndocVO.getDefaultTableName(),
						PsnJobVO.getDefaultTableName(),
						PsnOrgVO.getDefaultTableName() });

		return retVO;
	}

	public void updateUserOrg(PsnJobVO retVO, String pk_hrorg)
			throws BusinessException {
		if ((null == retVO) || (StringUtils.isBlank(pk_hrorg))) {
			return;
		}
		UserVO[] uesrvo = (UserVO[]) getServiceTemplate().queryByCondition(
				UserVO.class, " pk_base_doc = '" + retVO.getPk_psndoc() + "' ");
		if (ArrayUtils.isEmpty(uesrvo)) {
			return;
		}
		for (UserVO userVO : uesrvo) {
			userVO.setPk_org(pk_hrorg);
		}
		((IPersistenceUpdate) NCLocator.getInstance().lookup(
				IPersistenceUpdate.class)).updateVOArray(null, uesrvo, null,
				null);
	}

	public void checkDeptPostCanceled(PsnJobVO psnJobVO)
			throws BusinessException {
		if ((psnJobVO != null) && (psnJobVO.getEndflag() != null)
				&& (psnJobVO.getEndflag().booleanValue())) {

			return;
		}

		IPersistenceRetrieve retrieve = (IPersistenceRetrieve) NCLocator
				.getInstance().lookup(IPersistenceRetrieve.class);

		String pk_org = psnJobVO.getPk_org();
		AdminOrgVO org = StringUtils.isBlank(pk_org) ? null
				: (AdminOrgVO) retrieve.retrieveByPk(null, AdminOrgVO.class,
						pk_org);
		if ((org != null) && (org.getEnablestate() != null)
				&& (org.getEnablestate().intValue() != 2)) {
			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0477"));
		}

		String pk_dept = psnJobVO.getPk_dept();
		DeptVO dept = StringUtils.isBlank(pk_dept) ? null : (DeptVO) retrieve
				.retrieveByPk(null, DeptVO.class, pk_dept);
		if ((dept != null) && (dept.getHrcanceled() != null)
				&& (dept.getHrcanceled().booleanValue())) {
			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0323"));
		}

		if ((dept != null) && (dept.getEnablestate() != null)
				&& (dept.getEnablestate().intValue() != 2)) {
			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0372"));
		}

		String pk_post = psnJobVO.getPk_post();
		PostVO post = StringUtils.isBlank(pk_post) ? null : (PostVO) retrieve
				.retrieveByPk(null, PostVO.class, pk_post);
		if ((post != null) && (post.getHrcanceled() != null)
				&& (post.getHrcanceled().booleanValue())) {
			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0324"));
		}

		if ((post != null) && (post.getEnablestate() != null)
				&& (post.getEnablestate().intValue() != 2)) {
			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0373"));
		}

		String pk_job = psnJobVO.getPk_job();
		JobVO job = StringUtils.isBlank(pk_job) ? null : (JobVO) retrieve
				.retrieveByPk(null, JobVO.class, pk_job);

		if ((job != null) && (job.getEnablestate() != null)
				&& (job.getEnablestate().intValue() != 2)) {
			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0374"));
		}
	}

	public void synPkhrorgOfPsnorg(String pk_psnorg, String pk_hrorg)
			throws BusinessException {
		String sql = " update hi_psnorg set pk_hrorg = '" + pk_hrorg
				+ "' where pk_psnorg = '" + pk_psnorg + "' ";
		((IPersistenceUpdate) NCLocator.getInstance().lookup(
				IPersistenceUpdate.class)).executeSQLs(new String[] { sql });
	}

	public void addPsnChgWhenIntoDoc(PsnJobVO psnjobVO)
			throws BusinessException {
		String pk_neworg = psnjobVO.getPk_org();
		String pk_newcorp = HiSQLHelper.getPkCorpByPkOrg(pk_neworg);

		PsnChgVO lastVO = (PsnChgVO) getLastVO(PsnChgVO.class,
				psnjobVO.getPk_psnorg(), null);
		if (lastVO != null) {

			UFLiteralDate beginDate = psnjobVO.getBegindate();

			UFLiteralDate preRowBegindate = lastVO.getBegindate();

			UFLiteralDate preRowEnddate = lastVO.getEnddate();

			if ((preRowEnddate != null)
					&& ((preRowEnddate.compareTo(beginDate) == 0) || (preRowEnddate
							.afterDate(beginDate)))) {
				throw new BusinessException(ResHelper.getString("6007psn",
						"06007psn0271"));
			}

			if ((preRowBegindate != null)
					&& ((preRowBegindate.compareTo(beginDate) == 0) || (preRowBegindate
							.afterDate(beginDate)))) {
				throw new BusinessException(ResHelper.getString("6007psn",
						"06007psn0272"));
			}

			updateEndflagAndEnddate(lastVO, beginDate);

			PsnChgVO[] his = (PsnChgVO[]) getServiceTemplate()
					.queryByCondition(
							PsnChgVO.class,
							" pk_psnorg = '" + psnjobVO.getPk_psnorg()
									+ "' order by recordnum ");

			updateRecordnumAndLastflag(his);
			doAddPsnChg(psnjobVO, null, pk_newcorp, true);
		} else {
			doAddPsnChg(psnjobVO, null, pk_newcorp, true);
		}
	}

	private void doAddPsnChg(PsnJobVO psnjobVO, String comeCorpName,
			String pk_newcorp, boolean isSyncPsndoc) throws BusinessException {
		PsnChgVO psnchg = new PsnChgVO();
		psnchg.setRecordnum(Integer.valueOf(0));
		psnchg.setLastflag(UFBoolean.TRUE);
		psnchg.setPk_group(psnjobVO.getPk_hrgroup());
		psnchg.setPk_org(psnjobVO.getPk_hrorg());
		psnchg.setPk_psndoc(psnjobVO.getPk_psndoc());
		psnchg.setPk_psnorg(psnjobVO.getPk_psnorg());
		psnchg.setAssgid(psnjobVO.getAssgid());
		psnchg.setBegindate(psnjobVO.getBegindate());
		psnchg.setComecorpname(comeCorpName);
		psnchg.setTocorpname(null);
		psnchg.setPk_corp(pk_newcorp);

		psnchg = (PsnChgVO) new PsndocDAO().insert4SubSet(psnchg, true);
		if (isSyncPsndoc) {

			getIPsndocService().updateDataAfterSubDataChanged(
					PsnChgVO.getDefaultTableName(),
					new String[] { psnchg.getPk_psndoc() });
		}
	}

	public void addPsnChg(PsnJobVO psnjobVO, boolean isSyncPsndoc)
			throws BusinessException {
		String pk_oldorg = getPrePkOrg(psnjobVO);
		String pk_neworg = psnjobVO.getPk_org();
		String pk_oldcorp = HiSQLHelper.getPkCorpByPkOrg(pk_oldorg);
		String pk_newcorp = HiSQLHelper.getPkCorpByPkOrg(pk_neworg);
		String preToCorpName = VOUtils.getDocName(OrgVO.class, pk_newcorp);
		String comeCorpName = "";

		if (((pk_oldcorp != null) || (pk_newcorp != null))
				&& (!StringUtils.equals(pk_oldcorp, pk_newcorp))) {

			PsnChgVO lastVO = (PsnChgVO) getLastVO(PsnChgVO.class,
					psnjobVO.getPk_psnorg(), null);

			if (lastVO != null) {

				UFLiteralDate beginDate = psnjobVO.getBegindate();

				UFLiteralDate preRowBegindate = lastVO.getBegindate();

				UFLiteralDate preRowEnddate = lastVO.getEnddate();

				if ((preRowEnddate != null)
						&& ((preRowEnddate.compareTo(beginDate) == 0) || (preRowEnddate
								.afterDate(beginDate)))) {
					throw new BusinessException(ResHelper.getString("6007psn",
							"06007psn0271"));
				}

				if ((preRowBegindate != null)
						&& ((preRowBegindate.compareTo(beginDate) == 0) || (preRowBegindate
								.afterDate(beginDate)))) {
					throw new BusinessException(ResHelper.getString("6007psn",
							"06007psn0272"));
				}

				if (StringUtils.isBlank(lastVO.getTocorpname())) {

					lastVO.setTocorpname(preToCorpName);
				}

				lastVO.setPk_corp(HiSQLHelper.getPkCorpByPkOrg(lastVO
						.getPk_corp()));

				updateEndflagAndEnddate(lastVO, beginDate);

				comeCorpName = VOUtils.getDocName(OrgVO.class,
						lastVO.getPk_corp());
			}

			PsnChgVO[] his = (PsnChgVO[]) getServiceTemplate()
					.queryByCondition(
							PsnChgVO.class,
							" pk_psnorg = '" + psnjobVO.getPk_psnorg()
									+ "' order by recordnum ");

			updateRecordnumAndLastflag(his);

			doAddPsnChg(psnjobVO, comeCorpName, pk_newcorp, isSyncPsndoc);
		}
	}

	private String getPrePkOrg(PsnJobVO vo) throws BusinessException {
		PsnJobVO[] preVO = (PsnJobVO[]) getServiceTemplate().queryByCondition(
				PsnJobVO.class,
				" pk_psnorg = '" + vo.getPk_psnorg() + "' and assgid = "
						+ vo.getAssgid().intValue() + "  and recordnum > "
						+ vo.getRecordnum().intValue()
						+ "  order by recordnum ");

		if ((preVO != null) && (preVO.length > 0)) {
			return preVO[0].getPk_org();
		}
		return null;
	}

	public void addWork(PsnJobVO retVO) throws BusinessException {
		WorkVO[] vos = (WorkVO[]) getServiceTemplate().queryByCondition(
				WorkVO.class, " pk_psndoc = '" + retVO.getPk_psndoc() + "'");
		updateRecordnumAndLastflag(vos);

		WorkVO[] wvo = (WorkVO[]) getServiceTemplate().queryByCondition(
				WorkVO.class, " pk_psnjob = '" + retVO.getPk_psnjob() + "'");
		if ((null != wvo) && (wvo.length > 0)) {
			for (int i = 0; (wvo != null) && (i < wvo.length); i++) {
				wvo[i].setPk_psnjob(null);
				if (wvo[i].getEnddate() == null) {
					wvo[i].setEnddate(retVO.getBegindate() == null ? PubEnv
							.getServerLiteralDate() : retVO.getBegindate()
							.getDateBefore(1));
				}
			}

			((IPersistenceUpdate) NCLocator.getInstance().lookup(
					IPersistenceUpdate.class)).updateVOArray(null, wvo,
					new String[] { "pk_psnjob", "enddate" }, null);
		}

		WorkVO newWork = new WorkVO();
		newWork.setPk_group(retVO.getPk_group());
		newWork.setPk_org(retVO.getPk_hrorg());
		newWork.setPk_psndoc(retVO.getPk_psndoc());
		newWork.setPk_psnjob(retVO.getPk_psnjob());
		newWork.setRecordnum(Integer.valueOf(0));
		newWork.setLastflag(UFBoolean.TRUE);
		newWork.setBegindate(retVO.getBegindate());
		newWork.setEnddate(retVO.getEnddate());
		newWork.setMemo(retVO.getMemo());

		newWork.setWorkcorp(VOUtils.getDocName(OrgVO.class, retVO.getPk_org()));

		newWork.setWorkdept(VOUtils.getDocName(DeptVO.class, retVO.getPk_dept()));

		newWork.setWorkpost(VOUtils.getDocName(PostVO.class, retVO.getPk_post()));

		newWork.setWorkjob(VOUtils.getDocName(JobVO.class, retVO.getPk_job()));

		dealWorkSyncItem(newWork, retVO);

		newWork = (WorkVO) new PsndocDAO().insert4SubSet(newWork, true);

		getIPsndocService().updateDataAfterSubDataChanged(
				WorkVO.getDefaultTableName(),
				new String[] { newWork.getPk_psndoc() });
	}

	public void addWorkWithoutSynPsndoc(PsnJobVO retVO)
			throws BusinessException {
		WorkVO[] vos = (WorkVO[]) ((IPersistenceRetrieve) NCLocator
				.getInstance().lookup(IPersistenceRetrieve.class))
				.retrieveByClause(null, WorkVO.class,
						" pk_psndoc = '" + retVO.getPk_psndoc() + "'");

		for (int i = 0; (vos != null) && (i < vos.length); i++) {
			vos[i].setLastflag(UFBoolean.FALSE);
			vos[i].setRecordnum(Integer.valueOf(vos[i].getRecordnum()
					.intValue() + 1));
			if (retVO.getPk_psnjob().equals(vos[i].getPk_psnjob())) {
				vos[i].setPk_psnjob(null);
				if (vos[i].getEnddate() == null) {
					vos[i].setEnddate(retVO.getBegindate() == null ? PubEnv
							.getServerLiteralDate() : retVO.getBegindate()
							.getDateBefore(1));
				}
			}
		}

		new BaseDAO().updateVOArray(vos, new String[] { "lastflag",
				"recordnum", "pk_psnjob", "enddate" });

		WorkVO newWork = new WorkVO();
		newWork.setPk_group(retVO.getPk_group());
		newWork.setPk_org(retVO.getPk_hrorg());
		newWork.setPk_psndoc(retVO.getPk_psndoc());
		newWork.setPk_psnjob(retVO.getPk_psnjob());
		newWork.setRecordnum(Integer.valueOf(0));
		newWork.setLastflag(UFBoolean.TRUE);
		newWork.setBegindate(retVO.getBegindate());
		newWork.setEnddate(retVO.getEnddate());
		newWork.setMemo(retVO.getMemo());

		newWork.setWorkcorp(VOUtils.getDocName(OrgVO.class, retVO.getPk_org()));

		newWork.setWorkdept(VOUtils.getDocName(DeptVO.class, retVO.getPk_dept()));

		newWork.setWorkpost(VOUtils.getDocName(PostVO.class, retVO.getPk_post()));

		newWork.setWorkjob(VOUtils.getDocName(JobVO.class, retVO.getPk_job()));

		dealWorkSyncItem(newWork, retVO);

		new PsndocDAO().insert4SubSet(newWork, false);
	}

	private void dealWorkSyncItem(WorkVO newWork, PsnJobVO job)
			throws BusinessException {
		PsndocDAO psnDao = new PsndocDAO();

		HashMap<String, GeneralVO> syncMap = psnDao.getWorkSyncMap(
				job.getPk_hrorg(),
				job.getPk_group(),
				(job.getIsmainjob() != null)
						&& (job.getIsmainjob().booleanValue()));

		if (syncMap != null) {
			String[] jobItems = (String[]) syncMap.keySet().toArray(
					new String[0]);
			for (String jobItem : jobItems) {

				String[] jobAttrSet = job.getAttributeNames();
				String[] workAttrSet = newWork.getAttributeNames();
				if ((ArrayUtils.contains(jobAttrSet, jobItem))
						&& (ArrayUtils.contains(workAttrSet,
								(String) ((GeneralVO) syncMap.get(jobItem))
										.getAttributeValue("workcode")))) {

					Integer jobDataType = (Integer) ((GeneralVO) syncMap
							.get(jobItem)).getAttributeValue("jobdatatype");
					Integer workDataType = (Integer) ((GeneralVO) syncMap
							.get(jobItem)).getAttributeValue("workdatatype");

					if ((5 == jobDataType.intValue())
							&& (0 == workDataType.intValue())) {
						String pk_refinfo = (String) ((GeneralVO) syncMap
								.get(jobItem))
								.getAttributeValue("jobrefmodule");
						Object value = job.getAttributeValue(jobItem);
						String pk_infoset_item = (String) ((GeneralVO) syncMap
								.get(jobItem))
								.getAttributeValue("job_pk_infoset_item");

						String name = psnDao.getRefItemName(pk_refinfo, value,
								pk_infoset_item);
						String workItemCode = (String) ((GeneralVO) syncMap
								.get(jobItem)).getAttributeValue("workcode");
						newWork.setAttributeValue(workItemCode, name);

					} else {
						String workItemCode = (String) ((GeneralVO) syncMap
								.get(jobItem)).getAttributeValue("workcode");
						newWork.setAttributeValue(workItemCode,
								job.getAttributeValue(jobItem));
					}
				}
			}
		}
	}

	public void endWorkWithoutSynPsndoc(String pk_psnjob, UFLiteralDate endDate)
			throws BusinessException {
		WorkVO[] work = (WorkVO[]) ((IPersistenceRetrieve) NCLocator
				.getInstance().lookup(IPersistenceRetrieve.class))
				.retrieveByClause(null, WorkVO.class, " pk_psnjob = '"
						+ pk_psnjob + "' ");

		for (int j = 0; (work != null) && (j < work.length); j++) {
			UFLiteralDate begin = work[j].getBegindate();
			if ((begin != null) && (begin.afterDate(endDate))) {

				work[j].setEnddate(begin);
			} else {
				work[j].setEnddate(endDate);
			}
		}

		((IPersistenceUpdate) NCLocator.getInstance().lookup(
				IPersistenceUpdate.class)).updateVOArray(null, work,
				new String[] { "enddate" }, null);
	}

	public void endWork(String pk_psnjob, UFLiteralDate endDate)
			throws BusinessException {
		WorkVO[] work = (WorkVO[]) getServiceTemplate().queryByCondition(
				WorkVO.class, " pk_psnjob = '" + pk_psnjob + "' ");
		for (int j = 0; (work != null) && (j < work.length); j++) {
			UFLiteralDate begin = work[j].getBegindate();
			if ((begin != null) && (begin.afterDate(endDate))) {

				work[j].setEnddate(begin);
			} else {
				work[j].setEnddate(endDate);
			}
		}
		((IPersistenceUpdate) NCLocator.getInstance().lookup(
				IPersistenceUpdate.class)).updateVOArray(null, work,
				new String[] { "enddate" }, null);

		PsnJobVO pj = (PsnJobVO) getServiceTemplate().queryByPk(PsnJobVO.class,
				pk_psnjob);
		getIPsndocService().updateDataAfterSubDataChanged(
				WorkVO.getDefaultTableName(),
				new String[] { pj.getPk_psndoc() });
	}

	private void endWork(PsnJobVO psnjob, PsnJobVO dimission)
			throws BusinessException {
		if ((psnjob.getIsmainjob() == null)
				|| (!psnjob.getIsmainjob().booleanValue())) {
			endWork(psnjob.getPk_psnjob(), dimission.getBegindate()
					.getDateBefore(1));

		} else {

			WorkVO[] work = (WorkVO[]) getServiceTemplate().queryByCondition(
					WorkVO.class,
					" pk_psnjob = '" + psnjob.getPk_psnjob() + "' ");
			for (int j = 0; (work != null) && (j < work.length); j++) {
				UFLiteralDate begin = work[j].getBegindate();
				if ((begin != null)
						&& (begin.afterDate(dimission.getBegindate()
								.getDateBefore(1)))) {

					work[j].setEnddate(begin);
				} else {
					work[j].setEnddate(dimission.getBegindate()
							.getDateBefore(1));
				}
				work[j].setDimission_reason(VOUtils.getDocName(DefdocVO.class,
						dimission.getTrnsreason()));
			}
			((IPersistenceUpdate) NCLocator.getInstance().lookup(
					IPersistenceUpdate.class)).updateVOArray(null, work,
					new String[] { "enddate", "dimission_reason" }, null);

			getIPsndocService().updateDataAfterSubDataChanged(
					WorkVO.getDefaultTableName(),
					new String[] { psnjob.getPk_psndoc() });
		}
	}

	private void fireEvent(PsnJobVO before, PsnJobVO after, String pk_hrorg,
			String sourceID, String eventType) throws BusinessException {
		HiEventValueObject.fireEvent(before, after, pk_hrorg, sourceID,
				eventType);
	}

	private IPsndocService getIPsndocService() {
		return (IPsndocService) NCLocator.getInstance().lookup(
				IPsndocService.class);
	}

	public <T extends SuperVO> T getLastVO(Class<T> className,
			String pk_psnorg, Integer assgid) throws BusinessException {
		String where = " pk_psnorg = '" + pk_psnorg + "' and lastflag = 'Y' ";
		if (assgid != null) {
			where = where + " and assgid = " + assgid.intValue();
		}
		SuperVO[] vos = (SuperVO[]) getServiceTemplate().queryByCondition(
				className, where);
		if ((vos != null) && (vos.length > 0)) {
			return (T) vos[0];
		}
		return null;
	}

	public int getMaxAssgidByPsndoc(String pkPsndoc) throws BusinessException {
		String pk_psnorg = getPsnorgByPsndoc(pkPsndoc);
		String strSQL = "select max(assgid) as maxid from hi_psnjob where pk_psnorg = '"
				+ pk_psnorg + "' ";
		HashMap hm = (HashMap) ((IPersistenceHome) NCLocator.getInstance()
				.lookup(IPersistenceHome.class)).executeQuery(strSQL,
				new MapProcessor());
		if ((hm == null) || (hm.get("maxid") == null)
				|| (!(hm.get("maxid") instanceof Integer))) {
			return 2;
		}
		return ((Integer) hm.get("maxid")).intValue() + 1;
	}

	public String getNewPkpsnjob(String oldPkpsnjob) throws BusinessException {
		PsnJobVO oldvo = (PsnJobVO) getServiceTemplate().queryByPk(
				PsnJobVO.class, oldPkpsnjob);
		PsnJobVO[] newvo = (PsnJobVO[]) getServiceTemplate().queryByCondition(
				PsnJobVO.class,
				" pk_psnorg = '" + oldvo.getPk_psnorg() + "' and assgid = "
						+ oldvo.getAssgid().intValue()
						+ " and ismainjob = 'Y' and lastflag = 'Y' ");

		if ((newvo == null) || (newvo.length == 0)) {
			return oldvo.getPk_psnjob();
		}
		return newvo[0].getPk_psnjob();
	}

	public String getPsnjobByPsndoc(String pkPsndoc) throws BusinessException {
		String pk_psnorg = getPsnorgByPsndoc(pkPsndoc);
		PsnJobVO[] psnjob = (PsnJobVO[]) getServiceTemplate().queryByCondition(
				PsnJobVO.class,
				" pk_psnorg = '" + pk_psnorg
						+ "' and lastflag = 'Y' and assgid = 1 ");

		if ((psnjob == null) || (psnjob.length == 0)) {
			Logger.error("??�??????�????????");
			return null;
		}
		if (psnjob.length > 1) {
			Logger.error("??�??????�?�??????????");
			return psnjob[0].getPk_psnjob();
		}
		return psnjob[0].getPk_psnjob();
	}

	public String getPsnorgByPsndoc(String pkPsndoc) throws BusinessException {
		PsnOrgVO[] psnorg = (PsnOrgVO[]) getServiceTemplate().queryByCondition(
				PsnOrgVO.class,
				" pk_psndoc = '" + pkPsndoc + "' and lastflag = 'Y' ");
		if ((psnorg == null) || (psnorg.length == 0)) {
			Logger.error("??�??????�??????????");
			return null;
		}
		if (psnorg.length > 1) {
			Logger.error("??�??????�?�????????????");
			return psnorg[0].getPk_psnorg();
		}
		return psnorg[0].getPk_psnorg();
	}

	public String getEmpPsnorgByPsndoc(String pk_psndoc)
			throws BusinessException {
		PsnOrgVO[] psnorg = (PsnOrgVO[]) getServiceTemplate().queryByCondition(
				PsnOrgVO.class,
				" pk_psndoc = '" + pk_psndoc + "' order by orgrelaid desc ");

		if ((psnorg == null) || (psnorg.length == 0)) {
			Logger.error("??�?????????????");
			return null;
		}

		for (PsnOrgVO org : psnorg) {
			if ((org.getPsntype() == null)
					|| (org.getPsntype().intValue() != 1)) {

				return org.getPk_psnorg();
			}
		}
		return null;
	}

	public String getEmpPsnjobByPsndoc(String pk_psndoc)
			throws BusinessException {
		String pk_psnorg = getEmpPsnorgByPsndoc(pk_psndoc);
		if (StringUtils.isBlank(pk_psnorg)) {

			return null;
		}
		PsnJobVO[] psnjob = (PsnJobVO[]) getServiceTemplate().queryByCondition(
				PsnJobVO.class,
				" pk_psnorg = '" + pk_psnorg
						+ "' and lastflag = 'Y' and assgid = 1 ");

		if ((psnjob == null) || (psnjob.length == 0)) {
			Logger.error("??�??????�????????");
			return null;
		}
		if (psnjob.length > 1) {
			Logger.error("??�??????�?�??????????");
			return psnjob[0].getPk_psnjob();
		}
		return psnjob[0].getPk_psnjob();
	}

	private SimpleDocServiceTemplate getServiceTemplate() {
		SimpleDocServiceTemplate service = new SimpleDocServiceTemplate(
				"PERSONRECORD");
		service.setLazyLoad(true);
		return service;
	}

	public <T extends SuperVO> T insertRecord(T vo, InfoSetVO infoset)
			throws BusinessException {
		String condition = "";
		if (((vo instanceof PsnJobVO)) || ((vo instanceof PartTimeVO))) {

			String pk_psnorg = (String) vo.getAttributeValue("pk_psnorg");
			if (StringUtils.isBlank(pk_psnorg)) {
				throw new BusinessException(ResHelper.getString("6007psn",
						"06007psn0273"));
			}
			Integer assgid = (Integer) vo.getAttributeValue("assgid");
			if (assgid == null) {
				throw new BusinessException(ResHelper.getString("6007psn",
						"06007psn0274"));
			}
			condition = " pk_psnorg = '" + pk_psnorg.trim() + "' and assgid = "
					+ assgid.intValue();
		} else if (((vo instanceof TrialVO)) || ((vo instanceof CtrtVO))) {

			String pk_psnorg = (String) vo.getAttributeValue("pk_psnorg");
			if (StringUtils.isBlank(pk_psnorg)) {
				throw new BusinessException(ResHelper.getString("6007psn",
						"06007psn0273"));
			}
			condition = " pk_psnorg = '" + pk_psnorg.trim() + "' ";

		} else {
			String pk_psndoc = (String) vo.getAttributeValue("pk_psndoc");
			if (StringUtils.isBlank(pk_psndoc)) {
				throw new BusinessException(ResHelper.getString("6007psn",
						"06007psn0275"));
			}
			condition = " pk_psndoc = '" + pk_psndoc.trim() + "' ";
		}
		SuperVO[] hisVO = (SuperVO[]) getServiceTemplate().queryByCondition(
				vo.getClass(), condition);
		if ((infoset.getRecord_character() != null)
				&& (infoset.getRecord_character().intValue() == 3)) {

			for (int i = 0; (hisVO != null) && (i < hisVO.length); i++) {
				hisVO[i].setAttributeValue(
						"recordnum",
						Integer.valueOf(((Integer) hisVO[i]
								.getAttributeValue("recordnum")).intValue() + 1));
				hisVO[i].setAttributeValue("lastflag", UFBoolean.TRUE);
				getServiceTemplate().update(hisVO[i], false);
			}

		} else {
			updateRecordnumAndLastflag(hisVO);
		}

		vo.setAttributeValue("recordnum", Integer.valueOf(0));
		vo.setAttributeValue("lastflag", UFBoolean.TRUE);
		return getServiceTemplate().insert(vo);
	}

	public void synPkorgOfPsndoc(String pk_org, String pk_psndoc)
			throws BusinessException {
		String sql = " update bd_psndoc set pk_org = '" + pk_org
				+ "' where pk_psndoc = '" + pk_psndoc + "' ";
		((IPersistenceUpdate) NCLocator.getInstance().lookup(
				IPersistenceUpdate.class)).executeSQLs(new String[] { sql });
	}

	public void updateAllRecordnumAndLastflag(SuperVO[] vos)
			throws BusinessException {
		String[] updFields = { "recordnum", "lastflag" };
		if ((vos == null) || (vos.length == 0)) {
			return;
		}
		for (int i = 0; i < vos.length; i++) {
			vos[i].setAttributeValue("recordnum", Integer.valueOf(i));
			if (i == 0) {
				vos[i].setAttributeValue("lastflag", UFBoolean.TRUE);
				if ((vos[i] instanceof WorkVO)) {
					vos[i].setAttributeValue("enddate", null);
					updFields = new String[] { "recordnum", "lastflag",
							"enddate" };
				}

			} else {
				vos[i].setAttributeValue("lastflag", UFBoolean.FALSE);
			}
		}
		((IPersistenceUpdate) NCLocator.getInstance().lookup(
				IPersistenceUpdate.class)).updateVOArray(null, vos, updFields,
				null);
	}

	public SuperVO updateEndflagAndEnddate(SuperVO vo, UFLiteralDate date)
			throws BusinessException {
		vo.setAttributeValue("endflag", UFBoolean.TRUE);
		if ((vo instanceof PsnJobVO)) {

			vo.setAttributeValue("poststat", UFBoolean.FALSE);
			if (vo.getAttributeValue("enddate") == null) {
				if (!((PsnJobVO) vo).getIsmainjob().booleanValue()) {

					UFLiteralDate begindate = ((PsnJobVO) vo).getBegindate();
					if (begindate.compareTo(date) >= 0) {

						vo.setAttributeValue("enddate", begindate);
					} else {
						vo.setAttributeValue("enddate", date.getDateBefore(1));
					}

				} else {
					vo.setAttributeValue("enddate", date.getDateBefore(1));
				}
			} else {
				UFLiteralDate enddate = ((PsnJobVO) vo).getEnddate();
				if ((!((PsnJobVO) vo).getIsmainjob().booleanValue())
						&& ((enddate.afterDate(date)) || (enddate
								.isSameDate(date)))) {

					vo.setAttributeValue("enddate", date.getDateBefore(1));
				}
			}
		} else if ((vo instanceof PsnOrgVO)) {

			if (vo.getAttributeValue("enddate") == null) {
				UFLiteralDate begindate = ((PsnOrgVO) vo).getBegindate();
				if (begindate.compareTo(date) >= 0) {

					vo.setAttributeValue("enddate", begindate);
				} else {
					vo.setAttributeValue("enddate", date.getDateBefore(1));
				}
			} else {
				UFLiteralDate enddate = ((PsnOrgVO) vo).getEnddate();
				if ((enddate.afterDate(date)) || (enddate.isSameDate(date))) {

					vo.setAttributeValue("enddate", date.getDateBefore(1));
				}
			}
		} else if (((vo instanceof PsnChgVO)) || ((vo instanceof RetireVO))) {

			if (vo.getAttributeValue("enddate") == null) {
				vo.setAttributeValue("enddate", date.getDateBefore(1));
			}
		} else if ((vo instanceof TrialVO)) {
			((TrialVO) vo).setTrialresult(Integer.valueOf(1));
			if (((TrialVO) vo).getEnddate() == null) {
				((TrialVO) vo).setEnddate(date);
			}
			((TrialVO) vo).setRegulardate(date);
		}
		return updateRecord(vo);
	}

	public <T extends SuperVO> T updateRecord(T vo) throws BusinessException {
		return new PsndocDAO().update4SubSet(vo, false, true);
	}

	public void updateRecordnumAndLastflag(SuperVO[] vos)
			throws BusinessException {
		if ((vos == null) || (vos.length == 0)) {
			return;
		}
		for (int i = 0; i < vos.length; i++) {
			vos[i].setAttributeValue("recordnum", Integer
					.valueOf(((Integer) vos[i].getAttributeValue("recordnum"))
							.intValue() + 1));
			vos[i].setAttributeValue("lastflag", UFBoolean.FALSE);
		}

		((IPersistenceUpdate) NCLocator.getInstance().lookup(
				IPersistenceUpdate.class)).updateVOArray(null, vos,
				new String[] { "recordnum", "lastflag" }, null);
	}

	public void addPoiPsnjobs(PsnJobVO[] psnJobVO) throws BusinessException {
		if ((psnJobVO == null) || (psnJobVO.length == 0)) {
			return;
		}
		String[] pk_psnorg = new String[psnJobVO.length];
		for (int i = 0; i < psnJobVO.length; i++) {
			pk_psnorg[i] = psnJobVO[i].getPk_psnorg();
		}
		InSQLCreator ttu = null;
		try {
			ttu = new InSQLCreator();
			String psnorgInSql = ttu.getInSQL(pk_psnorg);

			BaseDAO dao = new BaseDAO();

			String validateSql = " select count(*) from hi_psnjob where lastflag = 'Y' and pk_psnorg in ("
					+ psnorgInSql
					+ ") and ( ( isnull(enddate,'~')<>'~' and enddate>='"
					+ psnJobVO[0].getBegindate()
					+ "' ) or (isnull(enddate,'~')='~' and isnull(begindate,'~')<>'~' and begindate>='"
					+ psnJobVO[0].getBegindate() + "' ) )";

			Object obj = dao.executeQuery(validateSql, new ColumnProcessor());
			if ((obj != null) && (((Integer) obj).intValue() > 0)) {
				throw new BusinessException(ResHelper.getString("6007psn",
						"06007psn0276"));
			}

			PsnJobVO[] before = (PsnJobVO[]) getServiceTemplate()
					.queryByCondition(
							PsnJobVO.class,
							" pk_psnorg in ("
									+ psnorgInSql
									+ ") and lastflag = 'Y' order by pk_psnorg ");

			if ((before == null) || (before.length != psnJobVO.length)) {
				throw new BusinessException(
						BDVersionValidationUtil.getUpdateInfo());
			}

			String[] pkOrg = new String[before.length];
			for (int i = 0; i < before.length; i++) {
				pkOrg[i] = before[i].getPk_hrorg();
			}

			HiBatchEventValueObject.fireEvent(before, null, pkOrg,
					"218971f0-e5dc-408b-9a32-56529dddd4db", "600709");

			UFLiteralDate end = psnJobVO[0].getBegindate().getDateBefore(1);
			String sql1 = " update hi_psnjob set enddate = '" + end
					+ "'  where lastflag = 'Y'  and pk_psnorg in ("
					+ psnorgInSql + ")  and  isnull(enddate,'~') = '~' ";

			dao.executeUpdate(sql1);

			String sql2 = " update hi_psnjob set lastflag = 'N' ,recordnum = recordnum+1 ,poststat = 'N',endflag = 'Y' where  pk_psnorg in ("
					+ psnorgInSql + ") ";

			dao.executeUpdate(sql2);

			HashMap<String, PsnJobVO> hm = new HashMap();
			for (PsnJobVO job : before) {
				hm.put(job.getPk_psnorg(), job);
			}

			String[] pkPsndoc = new String[psnJobVO.length];
			for (int i = 0; i < psnJobVO.length; i++) {
				String pkHrOrg = hm.get(psnJobVO[i].getPk_psnorg()) == null ? psnJobVO[i]
						.getPk_org() : ((PsnJobVO) hm.get(psnJobVO[i]
						.getPk_psnorg())).getPk_hrorg();

				psnJobVO[i].setPk_hrorg(pkHrOrg);
				pkPsndoc[i] = psnJobVO[i].getPk_psndoc();
			}
			dao.insertVOArray(psnJobVO);

			PsnJobVO[] after = (PsnJobVO[]) getServiceTemplate()
					.queryByCondition(
							PsnJobVO.class,
							" pk_psnorg in ("
									+ psnorgInSql
									+ ") and lastflag = 'Y' order by pk_psnorg ");

			if ((after != null) && (before.length != after.length)) {
				throw new BusinessException(
						BDVersionValidationUtil.getUpdateInfo());
			}

			getIPsndocService().updateDataAfterSubDataChanged(
					PsnJobVO.getDefaultTableName(), pkPsndoc);

			HiBatchEventValueObject.fireEvent(before, after, pkOrg,
					"218971f0-e5dc-408b-9a32-56529dddd4db", "600710");
			HiEventValueObject
					.fireDataPermChangeEvent("7156d223-4531-4337-b192-492ab40098f1");
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (ttu != null) {

				ttu.clear();
			}
		}

		HiCacheUtils
				.synCache(new String[] { PsndocVO.getDefaultTableName(),
						PsnJobVO.getDefaultTableName(),
						PsnOrgVO.getDefaultTableName() });
	}

	public void addPoiPsnjob(PsnJobVO psnJobVO) throws BusinessException {
		String pk_psnorg = psnJobVO.getPk_psnorg();
		PsnJobVO last = (PsnJobVO) getLastVO(PsnJobVO.class, pk_psnorg,
				Integer.valueOf(1));
		if ((last != null)
				&& ((last.getBegindate().afterDate(psnJobVO.getBegindate())) || (last
						.getBegindate().isSameDate(psnJobVO.getBegindate())))) {

			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0276"));
		}

		fireEvent(last, psnJobVO, last.getPk_hrorg(),
				"218971f0-e5dc-408b-9a32-56529dddd4db", "600709");
		PsnJobVO[] hisVOs = (PsnJobVO[]) getServiceTemplate().queryByCondition(
				PsnJobVO.class,
				" pk_psnorg = '" + pk_psnorg + "' and assgid = 1 ");
		for (int i = 0; (hisVOs != null) && (i < hisVOs.length); i++) {
			if (hisVOs[i].getLastflag().booleanValue()) {

				hisVOs[i] = ((PsnJobVO) updateEndflagAndEnddate(hisVOs[i],
						psnJobVO.getBegindate()));
			}
		}
		updateRecordnumAndLastflag(hisVOs);
		psnJobVO.setPk_hrorg(last.getPk_hrorg());
		PsnJobVO retVO = (PsnJobVO) getServiceTemplate().insert(psnJobVO);

		getIPsndocService().updateDataAfterSubDataChanged(
				PsnJobVO.getDefaultTableName(),
				new String[] { retVO.getPk_psndoc() });

		fireEvent(last, retVO, retVO.getPk_hrorg(),
				"218971f0-e5dc-408b-9a32-56529dddd4db", "600710");
		HiEventValueObject
				.fireDataPermChangeEvent("7156d223-4531-4337-b192-492ab40098f1");

		HiCacheUtils
				.synCache(new String[] { PsndocVO.getDefaultTableName(),
						PsnJobVO.getDefaultTableName(),
						PsnOrgVO.getDefaultTableName() });
	}

	public PartTimeVO savePartchgInf(PartTimeVO partTimeVO, boolean isSynWork,
			String pk_hrorg) throws BusinessException {
		checkClerkCodeUnique(partTimeVO);

		checkDeptPostCanceled(partTimeVO);

		PartTimeVO last = (PartTimeVO) getLastVO(PartTimeVO.class,
				partTimeVO.getPk_psnorg(), partTimeVO.getAssgid());
		fireEvent(last, null, pk_hrorg, "PARTTIME", "partchgbefore");
		if (last != null) {
			updateEndflagAndEnddate(last, partTimeVO.getBegindate());
			if (isSynWork) {

				endWork(last.getPk_psnjob(), last.getEnddate());
			}
		}

		PartTimeVO[] his = (PartTimeVO[]) getServiceTemplate()
				.queryByCondition(
						PartTimeVO.class,
						" pk_psnorg = '" + partTimeVO.getPk_psnorg()
								+ "' and assgid = " + partTimeVO.getAssgid());

		updateRecordnumAndLastflag(his);

		PartTimeVO retVO = (PartTimeVO) getServiceTemplate().insert(partTimeVO);
		fireEvent(last, retVO, pk_hrorg, "PARTTIME", "partchgafter");
		HiEventValueObject
				.fireDataPermChangeEvent("7156d223-4531-4337-b192-492ab40098f1");
		if (isSynWork) {

			addWork(retVO);
		}

		getIPsndocService().updateDataAfterSubDataChanged(
				PartTimeVO.getDefaultTableName(),
				new String[] { retVO.getPk_psndoc() });
		return retVO;
	}

	public void savePartchgInfo(PartTimeVO[] partTimeVOs, boolean isSynWork)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();

		ArrayList<String> pkDept = new ArrayList();
		ArrayList<String> pkPost = new ArrayList();
		for (int i = 0; i < partTimeVOs.length; i++) {
			if ((partTimeVOs[i] == null)
					|| (partTimeVOs[i].getEndflag() == null)
					|| (!partTimeVOs[i].getEndflag().booleanValue())) {

				if ((partTimeVOs[i].getPk_dept() != null)
						&& (!pkDept.contains(partTimeVOs[i].getPk_dept()))) {
					pkDept.add(partTimeVOs[i].getPk_dept());
				}
				if ((partTimeVOs[i].getPk_post() != null)
						&& (!pkPost.contains(partTimeVOs[i].getPk_post()))) {
					pkPost.add(partTimeVOs[i].getPk_post());
				}
			}
		}
		InSQLCreator ttu = null;
		try {
			ttu = new InSQLCreator();

			if (pkDept.size() > 0) {
				String sql1 = " select count(*) from org_dept where hrcanceled = 'Y' and pk_dept in ( "
						+ ttu.getInSQL((String[]) pkDept.toArray(new String[0]))
						+ " ) ";

				Object obj = dao.executeQuery(sql1, new ColumnProcessor());
				if ((obj != null) && (((Integer) obj).intValue() > 0)) {
					throw new BusinessException(ResHelper.getString("6007psn",
							"06007psn0323"));
				}
			}

			if (pkPost.size() > 0) {
				String sql2 = " select count(*) from om_post where hrcanceled ='Y' and pk_post in ( "
						+ ttu.getInSQL((String[]) pkPost.toArray(new String[0]))
						+ " ) ";

				Object obj = dao.executeQuery(sql2, new ColumnProcessor());
				if ((obj != null) && (((Integer) obj).intValue() > 0)) {
					throw new BusinessException(ResHelper.getString("6007psn",
							"06007psn0324"));
				}

			}

		} catch (Exception e) {

			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (ttu != null) {

				ttu.clear();
			}
		}

		String id = DBAUtil.getIdGenerator().generate();
		String tempTable = DBAUtil.createTempTable("temp" + id.substring(9),
				" pk_psnorg varchar(20) ,assgid int ", "pk_psnorg , assgid");
		String[] insertSql = new String[partTimeVOs.length];

		for (int i = 0; i < partTimeVOs.length; i++) {
			insertSql[i] = (" insert into " + tempTable + " values ('"
					+ partTimeVOs[i].getPk_psnorg() + "',"
					+ partTimeVOs[i].getAssgid() + ")");
		}

		DBAUtil.execBatchSql(insertSql);

		String querySql = " select hi_psnjob.* from hi_psnjob inner join "
				+ tempTable
				+ " on hi_psnjob.pk_psnorg = "
				+ tempTable
				+ ".pk_psnorg and hi_psnjob.assgid = "
				+ tempTable
				+ ".assgid  where hi_psnjob.lastflag = 'Y' order by hi_psnjob.pk_psnorg ";

		ArrayList beforeList = (ArrayList) dao.executeQuery(querySql,
				new BeanListProcessor(PartTimeVO.class));
		if ((beforeList == null) || (beforeList.size() != partTimeVOs.length)) {
			throw new BusinessException(BDVersionValidationUtil.getUpdateInfo());
		}
		PartTimeVO[] lastVOs = new PartTimeVO[partTimeVOs.length];
		String[] pkOrg = new String[partTimeVOs.length];
		ArrayList<String> al = new ArrayList();
		for (int i = 0; i < lastVOs.length; i++) {
			lastVOs[i] = ((PartTimeVO) beforeList.get(i));
			pkOrg[i] = ((PartTimeVO) beforeList.get(i)).getPk_hrorg();
			if (!al.contains(((PartTimeVO) beforeList.get(i)).getPk_psndoc())) {
				al.add(((PartTimeVO) beforeList.get(i)).getPk_psndoc());
			}
		}

		HiBatchEventValueObject.fireEvent(lastVOs, null, pkOrg, "PARTTIME",
				"partchgbefore");

		for (int i = 0; i < partTimeVOs.length; i++) {

			checkClerkCodeUnique(partTimeVOs[i]);
		}

		UFLiteralDate begindate = partTimeVOs[0].getBegindate();
		UFLiteralDate enddate = begindate.getDateBefore(1);
		String updateSql1 = " update hi_psnjob set endflag='Y',poststat ='N',enddate = ( case when isnull(enddate,'~')<>'~' and enddate >='"
				+ begindate
				+ "' then '"
				+ enddate
				+ "' when  isnull( enddate,'~')='~' then (case when begindate >='"
				+ begindate
				+ "' then begindate else '"
				+ enddate
				+ "' end) end)  where pk_psnjob in (select hi_psnjob.pk_psnjob from hi_psnjob inner join "
				+ tempTable
				+ " on hi_psnjob.pk_psnorg = "
				+ tempTable
				+ ".pk_psnorg and hi_psnjob.assgid = "
				+ tempTable
				+ ".assgid  where hi_psnjob.lastflag = 'Y') ";

		dao.executeUpdate(updateSql1);

		if (isSynWork) {

			WorkVO[] hisWorkVOs = (WorkVO[]) ((IPersistenceRetrieve) NCLocator
					.getInstance().lookup(IPersistenceRetrieve.class))
					.retrieveByClause(
							null,
							WorkVO.class,
							" pk_psnjob in ( select hi_psnjob.pk_psnjob from hi_psnjob inner join "
									+ tempTable
									+ " on hi_psnjob.pk_psnorg = "
									+ tempTable
									+ ".pk_psnorg and hi_psnjob.assgid = "
									+ tempTable
									+ ".assgid  where hi_psnjob.lastflag = 'Y' ) ");

			ArrayList tempList = (ArrayList) dao.executeQuery(querySql,
					new BeanListProcessor(PartTimeVO.class));
			HashMap<String, PartTimeVO> map = new HashMap();
			for (int i = 0; i < tempList.size(); i++) {
				map.put(((PartTimeVO) tempList.get(i)).getPk_psnjob(),
						(PartTimeVO) tempList.get(i));
			}

			for (int i = 0; (hisWorkVOs != null) && (i < hisWorkVOs.length); i++) {
				PartTimeVO vo = (PartTimeVO) map.get(hisWorkVOs[i]
						.getPk_psnjob());
				if (vo == null) {
					if (hisWorkVOs[i].getEnddate() == null) {
						hisWorkVOs[i].setEnddate(hisWorkVOs[i].getBegindate());
					}
				} else {
					UFLiteralDate begin = hisWorkVOs[i].getBegindate();
					UFLiteralDate end = vo.getBegindate().getDateBefore(1);
					if ((begin != null) && (begin.afterDate(end))) {

						hisWorkVOs[i].setEnddate(begin);
					} else {
						hisWorkVOs[i].setEnddate(end);
					}
				}
			}

			dao.updateVOArray(hisWorkVOs, new String[] { "enddate" });
		}

		String updateSql2 = " update hi_psnjob set lastflag ='N',recordnum = recordnum + 1 where pk_psnjob in (select  hi_psnjob.pk_psnjob  from hi_psnjob inner join "
				+ tempTable
				+ " on hi_psnjob.pk_psnorg = "
				+ tempTable
				+ ".pk_psnorg and hi_psnjob.assgid = "
				+ tempTable
				+ ".assgid  ) ";

		dao.executeUpdate(updateSql2);

		insert4SubVOs(partTimeVOs);

		ArrayList afterList = (ArrayList) dao.executeQuery(querySql,
				new BeanListProcessor(PartTimeVO.class));
		if ((afterList == null) || (afterList.size() != partTimeVOs.length)) {
			throw new BusinessException(BDVersionValidationUtil.getUpdateInfo());
		}
		PartTimeVO[] after = new PartTimeVO[partTimeVOs.length];
		for (int i = 0; i < partTimeVOs.length; i++) {
			after[i] = ((PartTimeVO) afterList.get(i));
		}

		if (isSynWork) {

			batchAddWorkWhenPsnjobAdd(after);
		}

		if (DBAUtil.isExist(" select 1 from " + tempTable)) {

			DBAUtil.execBatchSql(new String[] { " delete  from  " + tempTable });
		}

		HiBatchEventValueObject.fireEvent(lastVOs, after, pkOrg, "PARTTIME",
				"partchgafter");
		HiEventValueObject
				.fireDataPermChangeEvent("7156d223-4531-4337-b192-492ab40098f1");

		getIPsndocService().updateDataAfterSubDataChanged(
				PartTimeVO.getDefaultTableName(),
				(String[]) al.toArray(new String[0]));
		getIPsndocService().updateDataAfterSubDataChanged(
				WorkVO.getDefaultTableName(),
				(String[]) al.toArray(new String[0]));
	}

	private void batchAddWorkWhenPsnjobAdd(PsnJobVO[] after)
			throws BusinessException {
		InSQLCreator ttu = null;

		try {
			ArrayList<String> orgList = new ArrayList();
			ArrayList<String> deptList = new ArrayList();
			ArrayList<String> postList = new ArrayList();
			ArrayList<String> jobList = new ArrayList();

			for (int i = 0; i < after.length; i++) {
				if ((after[i].getPk_org() != null)
						&& (!orgList.contains(after[i].getPk_org()))) {
					orgList.add(after[i].getPk_org());
				}
				if ((after[i].getPk_dept() != null)
						&& (!deptList.contains(after[i].getPk_dept()))) {
					deptList.add(after[i].getPk_dept());
				}
				if ((after[i].getPk_post() != null)
						&& (!postList.contains(after[i].getPk_post()))) {
					postList.add(after[i].getPk_post());
				}
				if ((after[i].getPk_job() != null)
						&& (!jobList.contains(after[i].getPk_job()))) {
					jobList.add(after[i].getPk_job());
				}
			}

			ttu = new InSQLCreator();

			String orgSql = ttu.getInSQL((String[]) orgList
					.toArray(new String[0]));
			String deptSql = ttu.getInSQL((String[]) deptList
					.toArray(new String[0]));
			String postSql = ttu.getInSQL((String[]) postList
					.toArray(new String[0]));
			String jobSql = ttu.getInSQL((String[]) jobList
					.toArray(new String[0]));

			OrgVO[] orgVOs = null;
			DeptVO[] deptVOs = null;
			PostVO[] postVOs = null;
			JobVO[] jobVOs = null;

			IPersistenceRetrieve retrieve = (IPersistenceRetrieve) NCLocator
					.getInstance().lookup(IPersistenceRetrieve.class);

			if (orgSql != null) {
				orgVOs = (OrgVO[]) retrieve.retrieveByClause(null, OrgVO.class,
						" pk_org in ( " + orgSql + " ) ");
			}

			if (deptSql != null) {
				deptVOs = (DeptVO[]) retrieve.retrieveByClause(null,
						DeptVO.class, " pk_dept in ( " + deptSql + " ) ");
			}

			if (postSql != null) {
				postVOs = (PostVO[]) retrieve.retrieveByClause(null,
						PostVO.class, " pk_post in ( " + postSql + " ) ");
			}

			if (jobSql != null) {
				jobVOs = (JobVO[]) retrieve.retrieveByClause(null, JobVO.class,
						" pk_job in ( " + jobSql + " ) ");
			}

			HashMap<String, String> orgMap = new HashMap();
			HashMap<String, String> deptMap = new HashMap();
			HashMap<String, String> postMap = new HashMap();
			HashMap<String, String> jobMap = new HashMap();

			for (int i = 0; (orgVOs != null) && (i < orgVOs.length); i++) {
				orgMap.put(orgVOs[i].getPk_org(),
						MultiLangHelper.getName(orgVOs[i]));
			}

			for (int i = 0; (deptVOs != null) && (i < deptVOs.length); i++) {
				deptMap.put(deptVOs[i].getPk_dept(),
						MultiLangHelper.getName(deptVOs[i]));
			}

			for (int i = 0; (postVOs != null) && (i < postVOs.length); i++) {
				postMap.put(postVOs[i].getPk_post(),
						MultiLangHelper.getName(postVOs[i]));
			}

			for (int i = 0; (jobVOs != null) && (i < jobVOs.length); i++) {
				jobMap.put(jobVOs[i].getPk_job(),
						MultiLangHelper.getName(jobVOs[i]));
			}

			ArrayList<String> sqlList = new ArrayList();
			ArrayList<WorkVO> voList = new ArrayList();
			for (PsnJobVO vo : after) {

				String sql = " update hi_psndoc_work set lastflag = 'N',recordnum = recordnum + 1 where pk_psndoc = '"
						+ vo.getPk_psndoc() + "' ";

				sqlList.add(sql);

				WorkVO newWork = new WorkVO();
				newWork.setPk_group(vo.getPk_group());
				newWork.setPk_org(vo.getPk_hrorg());
				newWork.setPk_psndoc(vo.getPk_psndoc());
				newWork.setPk_psnjob(vo.getPk_psnjob());
				newWork.setRecordnum(Integer.valueOf(0));
				newWork.setLastflag(UFBoolean.TRUE);
				newWork.setBegindate(vo.getBegindate());
				newWork.setEnddate(vo.getEnddate());
				newWork.setMemo(vo.getMemo());

				newWork.setWorkcorp(vo.getPk_org() == null ? null
						: (String) orgMap.get(vo.getPk_org()));

				newWork.setWorkdept(vo.getPk_dept() == null ? null
						: (String) deptMap.get(vo.getPk_dept()));

				newWork.setWorkpost(vo.getPk_post() == null ? null
						: (String) postMap.get(vo.getPk_post()));

				newWork.setWorkjob(vo.getPk_job() == null ? null
						: (String) jobMap.get(vo.getPk_job()));

				dealWorkSyncItem(newWork, vo);

				newWork.setCreator(PubEnv.getPk_user());
				newWork.setCreationtime(PubEnv.getServerTime());
				voList.add(newWork);
			}

			((IPersistenceUpdate) NCLocator.getInstance().lookup(
					IPersistenceUpdate.class)).executeSQLs((String[]) sqlList
					.toArray(new String[0]));
			new BaseDAO().insertVOArray((SuperVO[]) voList
					.toArray(new WorkVO[0]));

		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		} finally {
			if (ttu != null) {

				ttu.clear();
			}
		}
	}

	public void addWorkWhenPsnjobAdd(PsnJobVO vo) throws BusinessException {
		String sql = " update hi_psndoc_work set lastflag = 'N',recordnum = recordnum + 1 where pk_psndoc = '"
				+ vo.getPk_psndoc() + "' ";
		new BaseDAO().executeUpdate(sql);

		WorkVO newWork = new WorkVO();
		newWork.setPk_group(vo.getPk_group());
		newWork.setPk_org(vo.getPk_hrorg());
		newWork.setPk_psndoc(vo.getPk_psndoc());
		newWork.setPk_psnjob(vo.getPk_psnjob());
		newWork.setRecordnum(Integer.valueOf(0));
		newWork.setLastflag(UFBoolean.TRUE);
		newWork.setBegindate(vo.getBegindate());
		newWork.setEnddate(vo.getEnddate());
		newWork.setMemo(vo.getMemo());

		newWork.setWorkcorp(VOUtils.getDocName(OrgVO.class, vo.getPk_org()));

		newWork.setWorkdept(VOUtils.getDocName(DeptVO.class, vo.getPk_dept()));

		newWork.setWorkpost(VOUtils.getDocName(PostVO.class, vo.getPk_post()));

		newWork.setWorkjob(VOUtils.getDocName(JobVO.class, vo.getPk_job()));

		dealWorkSyncItem(newWork, vo);

		new PsndocDAO().insert4SubSet(newWork, false);
	}

	private <T extends SuperVO> void insert4SubVOs(T[] vo)
			throws BusinessException {
		for (int i = 0; i < vo.length; i++) {
			vo[i].setStatus(2);
			SimpleDocServiceTemplate.setAuditInfoAndTs(vo[i], true);
		}
		MDPersistenceService.lookupPersistenceService().saveBill(vo);
	}

	public void checkBeginDate(PsnJobVO psnjobVO, UFLiteralDate jobDate,
			UFLiteralDate orgDate) throws BusinessException {
		IPersistenceRetrieve service = (IPersistenceRetrieve) NCLocator
				.getInstance().lookup(IPersistenceRetrieve.class);
		PsndocVO docVO = (psnjobVO == null)
				|| (psnjobVO.getPk_psndoc() == null) ? null
				: (PsndocVO) service.retrieveByPk(null, PsndocVO.class,
						psnjobVO.getPk_psndoc());

		if (docVO == null) {

			return;
		}

		PsnOrgVO preOrgVO = null;
		String orgCond = "";
		if (psnjobVO.getPk_psnorg() == null) {

			orgCond = " pk_psndoc = '" + psnjobVO.getPk_psndoc()
					+ "' and lastflag = 'Y' ";

		} else {
			orgCond = " pk_psndoc = '"
					+ psnjobVO.getPk_psndoc()
					+ "' and orgrelaid = ( select orgrelaid - 1 from hi_psnorg where pk_psnorg = '"
					+ psnjobVO.getPk_psnorg() + "' ) ";
		}

		PsnOrgVO[] temp = (PsnOrgVO[]) getServiceTemplate().queryByCondition(
				PsnOrgVO.class, orgCond);
		if ((temp != null) && (temp.length > 0)) {
			preOrgVO = temp[0];
		}

		if (preOrgVO == null) {

			return;
		}

		if (orgDate == null) {
			PsnOrgVO org = (PsnOrgVO) getServiceTemplate().queryByPk(
					PsnOrgVO.class, psnjobVO.getPk_psnorg());
			orgDate = org.getBegindate();
		}

		UFLiteralDate preOrgDate = preOrgVO.getEnddate() == null ? preOrgVO
				.getBegindate() : preOrgVO.getEnddate();
		preOrgDate = preOrgDate.getDateAfter(1);

		int curPsnType = psnjobVO.getPsntype().intValue();
		int prePsnType = preOrgVO.getPsntype().intValue();

		if (preOrgDate.afterDate(jobDate)) {
			String curJobName = curPsnType == 0 ? ResHelper.getString(
					"6007psn", "16007psn0014") : ResHelper.getString("6007psn",
					"06007psn0277");

			String preOrgName = prePsnType == 0 ? ResHelper.getString(
					"6007psn", "16007psn0012") : ResHelper.getString("6007psn",
					"16007psn0034");

			throw new BusinessException(ResHelper.getString(
					"6007psn",
					"06007psn0281",
					new String[] {
							psnjobVO.getClerkcode(),
							VOUtils.getDocName(PsndocVO.class,
									psnjobVO.getPk_psndoc()), curJobName,
							preOrgName }));
		}

		if (preOrgDate.afterDate(orgDate)) {
			String curOrgName = curPsnType == 0 ? ResHelper.getString(
					"6007psn", "16007psn0012") : ResHelper.getString("6007psn",
					"16007psn0034");

			String preOrgName = prePsnType == 0 ? ResHelper.getString(
					"6007psn", "16007psn0012") : ResHelper.getString("6007psn",
					"16007psn0034");

			throw new BusinessException(ResHelper.getString(
					"6007psn",
					"06007psn0283",
					new String[] {
							psnjobVO.getClerkcode(),
							VOUtils.getDocName(PsndocVO.class,
									psnjobVO.getPk_psndoc()), curOrgName,
							preOrgName }));
		}
	}

	public void checkClerkCodeUnique(PsnJobVO[] psnjobVOs)
			throws BusinessException {
		if (ArrayUtils.isEmpty(psnjobVOs))
			return;
		String[] fields = { "clerkcode", "pk_org", "pk_psndoc" };
		String tmpTableName = new InSQLCreator().insertValues(
				PsnJobVO.getDefaultTableName() + "tmpu", fields, fields,
				psnjobVOs);
		String sql = " select t.clerkcode clerkcode, t.pk_org pk_org from "
				+ tmpTableName
				+ " t where exists(select 1 from "
				+ PsnJobVO.getDefaultTableName()
				+ " s where s.pk_org=t.pk_org and s.clerkcode=t.clerkcode and s.pk_psndoc<>t.pk_psndoc)";

		GeneralVO[] c = (GeneralVO[]) new BaseDAO().executeQuery(sql,
				new GeneralVOProcessor(GeneralVO.class));
		if (ArrayUtils.isEmpty(c))
			return;
		throw new BusinessException(ResHelper.getString(
				"6007psn",
				"06007psn0234",
				new String[] {
						(String) c[0].getAttributeValue("clerkcode"),
						VOUtils.getDocName(OrgVO.class,
								(String) c[0].getAttributeValue("pk_org")) }));
	}

	public void checkClerkCodeUnique(PsnJobVO psnjobVO)
			throws BusinessException {
		String clerkCode = psnjobVO.getClerkcode();
		String pk_org = psnjobVO.getPk_org();
		String pk_psndoc = psnjobVO.getPk_psndoc();

		String condition = " clerkcode = '" + NCESAPI.sqlEncode(clerkCode)
				+ "' and pk_org = '" + pk_org + "' and pk_psndoc <>'"
				+ pk_psndoc + "' ";
		int count = ((IPersistenceRetrieve) NCLocator.getInstance().lookup(
				IPersistenceRetrieve.class)).getCountByCondition(
				PsnJobVO.getDefaultTableName(), condition);

		if (count > 0) {
			throw new BusinessException(ResHelper.getString(
					"6007psn",
					"06007psn0234",
					new String[] { clerkCode,
							VOUtils.getDocName(OrgVO.class, pk_org) }));
		}
	}

	public void checkPsnorg(String pk_psnjob) throws BusinessException {
		PsnJobVO job = (PsnJobVO) getServiceTemplate().queryByPk(
				PsnJobVO.class, pk_psnjob);

		PsnOrgVO org = (PsnOrgVO) getServiceTemplate().queryByPk(
				PsnOrgVO.class, job.getPk_psnorg());

		String sql = " pk_psndoc ='" + org.getPk_psndoc()
				+ "' and indocflag = 'Y' and orgrelaid > " + org.getOrgrelaid();

		int count = ((IPersistenceRetrieve) NCLocator.getInstance().lookup(
				IPersistenceRetrieve.class)).getCountByCondition("hi_psnorg",
				sql);
		if (count > 0) {
			throw new BusinessException(ResHelper.getString("6007psn",
					"06007psn0348"));
		}
	}

	public void syncPsnWaInfo(Map<String, WainfoVO[]> wainfo)
			throws BusinessException {
		if ((wainfo == null) || (wainfo.size() <= 0)) {
			return;
		}

		IPersistenceUpdate update = (IPersistenceUpdate) NCLocator
				.getInstance().lookup(IPersistenceUpdate.class);
		IPersistenceRetrieve retrieve = (IPersistenceRetrieve) NCLocator
				.getInstance().lookup(IPersistenceRetrieve.class);

		String[] docPKs = (String[]) wainfo.keySet().toArray(new String[0]);

		InSQLCreator isc = new InSQLCreator();

		try {
			WainfoVO[] old = (WainfoVO[]) retrieve.retrieveByClause(null,
					WainfoVO.class, " pk_psndoc in ( " + isc.getInSQL(docPKs)
							+ " ) ");

			if ((old != null) && (old.length > 0)) {
				update.deleteVOArray(null, old, null);
			}

		} finally {
			isc.clear();
		}

		ArrayList<WainfoVO> al = new ArrayList();

		for (String pk : docPKs) {

			WainfoVO[] vos = (WainfoVO[]) wainfo.get(pk);
			if ((vos != null) && (vos.length != 0)) {

				for (int i = 0; i < vos.length; i++) {
					vos[i].setPk_psndoc(pk);
					vos[i].setRecordnum(Integer.valueOf(vos.length - 1 - i));
					vos[i].setCreationtime(PubEnv.getServerTime());
					vos[i].setCreator(PubEnv.getPk_user());
					vos[i].setStatus(2);
					al.add(vos[i]);
				}
			}
		}
		if (al.size() > 0) {
			update.insertVOArray(null, (SuperVO[]) al.toArray(new WainfoVO[0]),
					null);
		}
		getIPsndocService().updateDataAfterSubDataChanged(
				WainfoVO.getDefaultTableName(), docPKs);

		if ((docPKs == null) || (docPKs.length == 0)) {
			return;
		}
		InSQLCreator ttu = new InSQLCreator();
		String inSql = ttu.getInSQL(docPKs);

		String updateSQL = " update hi_psndoc_wainfo set pk_psnorg = (select pk_psnorg from hi_psnjob where hi_psnjob.pk_psnjob = hi_psndoc_wainfo.pk_psnjob) where pk_psndoc in ( "
				+ inSql + " ) ";

		new BaseDAO().executeUpdate(updateSQL);
	}

	public void syncPsnBmInfo(Map<String, BminfoVO> bminfo, String pk_insurance)
			throws BusinessException {
		if ((bminfo == null) || (bminfo.size() <= 0)
				|| (StringUtils.isBlank(pk_insurance))) {
			return;
		}

		IPersistenceUpdate update = (IPersistenceUpdate) NCLocator
				.getInstance().lookup(IPersistenceUpdate.class);
		IPersistenceRetrieve retrieve = (IPersistenceRetrieve) NCLocator
				.getInstance().lookup(IPersistenceRetrieve.class);

		String[] docPKs = (String[]) bminfo.keySet().toArray(new String[0]);
		ArrayList<String> noList = new ArrayList();
		ArrayList<String> yesList = new ArrayList();
		for (String pk : docPKs) {
			BminfoVO vo = (BminfoVO) bminfo.get(pk);
			if (vo == null) {
				noList.add(pk);
			} else {
				yesList.add(pk);
			}
		}

		InSQLCreator isc = new InSQLCreator();

		try {
			if (noList.size() > 0) {
				String noInsql = isc.getInSQL((String[]) noList
						.toArray(new String[0]));
				BminfoVO[] old = (BminfoVO[]) retrieve.retrieveByClause(null,
						BminfoVO.class, " pk_psndoc in ( " + noInsql
								+ " ) and pk_insurance = '" + pk_insurance
								+ "' ");

				if ((old != null) && (old.length > 0)) {

					update.deleteVOArray(null, old, null);
				}

				BminfoVO[] psnBm = (BminfoVO[]) retrieve.retrieveByClause(null,
						BminfoVO.class, " pk_psndoc in ( " + noInsql
								+ " ) order by recordnum ");

				HashMap<String, ArrayList<BminfoVO>> hm = new HashMap();
				for (int i = 0; (psnBm != null) && (i < psnBm.length); i++) {
					if (hm.get(psnBm[i].getPk_psndoc()) == null) {
						hm.put(psnBm[i].getPk_psndoc(), new ArrayList());
					}
					((ArrayList) hm.get(psnBm[i].getPk_psndoc())).add(psnBm[i]);
				}
				ArrayList<BminfoVO> al = new ArrayList();
				for (String key : hm.keySet()) {
					ArrayList<BminfoVO> vos = (ArrayList) hm.get(key);
					for (int i = 0; (vos != null) && (i < vos.size()); i++) {
						((BminfoVO) vos.get(i))
								.setRecordnum(Integer.valueOf(i));
						if (i == 0) {
							((BminfoVO) vos.get(i)).setLastflag(UFBoolean.TRUE);
						} else {
							((BminfoVO) vos.get(i))
									.setLastflag(UFBoolean.FALSE);
						}
					}
					if ((vos != null) && (vos.size() > 0)) {
						al.addAll(vos);
					}
				}
				update.updateVOList(null, al, new String[] { "recordnum",
						"lastflag" }, null);
			}

			if (yesList.size() > 0) {
				String yesInsql = isc.getInSQL((String[]) yesList
						.toArray(new String[0]));
				BminfoVO[] old = (BminfoVO[]) retrieve.retrieveByClause(null,
						BminfoVO.class, " pk_psndoc in ( " + yesInsql
								+ " ) and pk_insurance = '" + pk_insurance
								+ "' ");

				HashMap<String, BminfoVO> map = new HashMap();
				for (int i = 0; (old != null) && (i < old.length); i++) {
					if (!map.containsKey(old[i].getPk_psndoc())) {

						map.put(old[i].getPk_psndoc(), old[i]);
					}
				}

				ArrayList<String> wuList = new ArrayList();
				ArrayList<String> youList = new ArrayList();
				for (String pk : yesList) {
					if (map.get(pk) != null) {
						youList.add(pk);
					} else {
						wuList.add(pk);
					}
				}

				if (youList.size() > 0) {
					ArrayList<BminfoVO> l = new ArrayList();
					for (String pk : youList) {
						BminfoVO vo = (BminfoVO) bminfo.get(pk);
						BminfoVO his = (BminfoVO) map.get(pk);

						vo.setPk_psndoc(his.getPk_psndoc());
						vo.setPk_psndoc_sub(his.getPk_psndoc_sub());
						vo.setRecordnum(his.getRecordnum());
						vo.setLastflag(his.getLastflag());
						vo.setCreationtime(his.getCreationtime());
						vo.setCreator(his.getCreator());
						vo.setStatus(1);
						l.add(vo);
					}
					update.updateVOList(null, l, null, null);
				}

				if (wuList.size() > 0) {

					String wuInsql = isc.getInSQL((String[]) wuList
							.toArray(new String[0]));
					BminfoVO[] psnBm = (BminfoVO[]) retrieve.retrieveByClause(
							null, BminfoVO.class, " pk_psndoc in ( " + wuInsql
									+ " ) ");
					for (int i = 0; (psnBm != null) && (i < psnBm.length); i++) {
						psnBm[i].setRecordnum(Integer.valueOf(psnBm[i]
								.getRecordnum().intValue() + 1));
						psnBm[i].setLastflag(UFBoolean.FALSE);
					}
					if ((psnBm != null) && (psnBm.length > 0)) {
						update.updateVOArray(null, psnBm, new String[] {
								"recordnum", "lastflag" }, null);
					}

					ArrayList<BminfoVO> bm = new ArrayList();
					for (String pk : wuList) {
						BminfoVO vo = (BminfoVO) bminfo.get(pk);

						vo.setPk_psndoc(pk);
						vo.setRecordnum(Integer.valueOf(0));
						vo.setLastflag(UFBoolean.TRUE);
						vo.setCreationtime(PubEnv.getServerTime());
						vo.setCreator(PubEnv.getPk_user());
						vo.setStatus(2);
						bm.add(vo);
					}

					update.insertVOList(null, bm, null);
				}

			}

		} finally {
			isc.clear();
		}

		getIPsndocService().updateDataAfterSubDataChanged(
				BminfoVO.getDefaultTableName(), docPKs);
	}

	public void syncPsnCapaInfo(Map<String, CapaVO[]> capainfo)
			throws BusinessException {
		if ((capainfo == null) || (capainfo.size() <= 0)) {
			return;
		}

		IPersistenceUpdate update = (IPersistenceUpdate) NCLocator
				.getInstance().lookup(IPersistenceUpdate.class);
		IPersistenceRetrieve retrieve = (IPersistenceRetrieve) NCLocator
				.getInstance().lookup(IPersistenceRetrieve.class);

		String[] jobPKs = (String[]) capainfo.keySet().toArray(new String[0]);
		ArrayList<String> docPKList = new ArrayList();
		for (String pk : jobPKs) {
			PsnJobVO job = (PsnJobVO) retrieve.retrieveByPk(null,
					PsnJobVO.class, pk);
			if (job != null) {

				CapaVO[] old = (CapaVO[]) retrieve.retrieveByClause(null,
						CapaVO.class, "pk_psndoc = '" + job.getPk_psndoc()
								+ "' and pk_psnorg = '" + job.getPk_psnorg()
								+ "' ");

				if ((old != null) && (old.length > 0)) {
					update.deleteVOArray(null, old, null);
				}

				CapaVO[] vos = (CapaVO[]) capainfo.get(pk);
				if ((vos != null) && (vos.length != 0)) {

					for (int i = 0; i < vos.length; i++) {
						vos[i].setPk_psndoc(job.getPk_psndoc());
						vos[i].setPk_psnorg(job.getPk_psnorg());
						vos[i].setRecordnum(Integer.valueOf(vos.length - 1 - i));
						vos[i].setLastflag(UFBoolean.TRUE);
						vos[i].setCreationtime(PubEnv.getServerTime());
						vos[i].setCreator(PubEnv.getPk_user());
						vos[i].setStatus(2);
					}
					update.insertVOArray(null, vos, null);
					if (!docPKList.contains(job.getPk_psndoc())) {
						docPKList.add(job.getPk_psndoc());
					}
				}
			}
		}
		getIPsndocService().updateDataAfterSubDataChanged(
				CapaVO.getDefaultTableName(),
				(String[]) docPKList.toArray(new String[0]));
	}

	public void syncPsnPeInfo(Map<String, AssVO[]> peinfo)
			throws BusinessException {
		if ((peinfo == null) || (peinfo.size() <= 0)) {
			return;
		}

		IPersistenceUpdate update = (IPersistenceUpdate) NCLocator
				.getInstance().lookup(IPersistenceUpdate.class);
		IPersistenceRetrieve retrieve = (IPersistenceRetrieve) NCLocator
				.getInstance().lookup(IPersistenceRetrieve.class);

		String[] jobPKs = (String[]) peinfo.keySet().toArray(new String[0]);

		InSQLCreator isc = new InSQLCreator();
		try {
			String jobInsql = isc.getInSQL(jobPKs);

			PsnJobVO[] jobVOs = (PsnJobVO[]) retrieve.retrieveByClause(null,
					PsnJobVO.class, " pk_psnjob in (" + jobInsql + ") ");

			ArrayList<String> psnorgPKList = new ArrayList();
			for (PsnJobVO job : jobVOs) {
				if (!psnorgPKList.contains(job.getPk_psnorg())) {
					psnorgPKList.add(job.getPk_psnorg());
				}
			}

			String orgInsql = isc.getInSQL((String[]) psnorgPKList
					.toArray(new String[0]));
			AssVO[] allOld = (AssVO[]) retrieve.retrieveByClause(null,
					AssVO.class, " pk_psnorg in (" + orgInsql
							+ ") order by recordnum desc ");

			HashMap<String, ArrayList<AssVO>> hm = new HashMap();
			for (int i = 0; (allOld != null) && (i < allOld.length); i++) {
				if (hm.get(allOld[i].getPk_psnorg()) == null) {
					hm.put(allOld[i].getPk_psnorg(), new ArrayList());
				}
				((ArrayList) hm.get(allOld[i].getPk_psnorg())).add(allOld[i]);
			}
			ArrayList<AssVO> deleteList = new ArrayList();
			ArrayList<AssVO> updateListAll = new ArrayList();
			ArrayList<AssVO> insertList = new ArrayList();
			ArrayList<String> docPKList = new ArrayList();

			for (PsnJobVO job : jobVOs) {
				String pk_psnjob = job.getPk_psnjob();

				AssVO[] vos = (AssVO[]) peinfo.get(pk_psnjob);
				if ((vos != null) && (vos.length != 0)) {

					AssVO[] old = null;
					if (hm.get(job.getPk_psnorg()) != null) {
						old = (AssVO[]) ((ArrayList) hm.get(job.getPk_psnorg()))
								.toArray(new AssVO[0]);
					}
					ArrayList<AssVO> updateList = new ArrayList();
					for (int i = 0; (old != null) && (i < old.length); i++) {
						if (exist(old[i], vos)) {

							deleteList.add(old[i]);
						}
						updateList.add(old[i]);
					}

					if (updateList.size() > 0) {
						for (int i = 0; i < updateList.size(); i++) {
							((AssVO) updateList.get(i)).setRecordnum(Integer
									.valueOf(updateList.size() - 1 - i
											+ vos.length));
							((AssVO) updateList.get(i))
									.setLastflag(UFBoolean.FALSE);
						}
						updateListAll.addAll(updateList);
					}

					for (int i = 0; i < vos.length; i++) {
						vos[i].setPk_psndoc(job.getPk_psndoc());
						vos[i].setPk_psnorg(job.getPk_psnorg());
						vos[i].setRecordnum(Integer.valueOf(vos.length - 1 - i));
						vos[i].setLastflag(UFBoolean.valueOf(vos[i]
								.getRecordnum().intValue() == 0));
						vos[i].setCreationtime(PubEnv.getServerTime());
						vos[i].setCreator(PubEnv.getPk_user());
						vos[i].setStatus(2);
						insertList.add(vos[i]);
					}
					if (!docPKList.contains(job.getPk_psndoc())) {
						docPKList.add(job.getPk_psndoc());
					}
				}
			}
			if (deleteList.size() > 0) {
				update.deleteVOList(null, deleteList, null);
			}

			if (updateListAll.size() > 0) {
				update.updateVOList(null, updateListAll, new String[] {
						"recordnum", "lastflag" }, null);
			}

			if (insertList.size() > 0) {
				update.insertVOList(null, insertList, null);
			}

			getIPsndocService().updateDataAfterSubDataChanged(
					AssVO.getDefaultTableName(),
					(String[]) docPKList.toArray(new String[0]));
		} finally {
			isc.clear();
		}
	}

	private boolean exist(AssVO assVO, AssVO[] vos) {
		for (AssVO vo : vos) {
			if (vo.getSchemecode().equals(assVO.getSchemecode())) {
				return true;
			}
		}
		return false;
	}

	public void delPsnPeInfo(String pk_scheme) throws BusinessException {
		AssVO[] ass = (AssVO[]) ((IPersistenceRetrieve) NCLocator.getInstance()
				.lookup(IPersistenceRetrieve.class)).retrieveByClause(null,
				AssVO.class, " schemecode = '" + pk_scheme + "' ");

		ArrayList<String> list = new ArrayList();
		ArrayList<String> docPKList = new ArrayList();
		for (int i = 0; (ass != null) && (i < ass.length); i++) {
			if (!list.contains(ass[i].getPk_psnorg())) {
				list.add(ass[i].getPk_psnorg());
			}

			if (!docPKList.contains(ass[i].getPk_psndoc())) {
				docPKList.add(ass[i].getPk_psndoc());
			}
		}

		if ((ass != null) && (ass.length > 0)) {
			((IPersistenceUpdate) NCLocator.getInstance().lookup(
					IPersistenceUpdate.class)).deleteVOArray(null, ass, null);

			InSQLCreator isc = new InSQLCreator();
			try {
				String insql = isc.getInSQL((String[]) list
						.toArray(new String[0]));
				AssVO[] assVOs = (AssVO[]) ((IPersistenceRetrieve) NCLocator
						.getInstance().lookup(IPersistenceRetrieve.class))
						.retrieveByClause(null, AssVO.class, " pk_psnorg in ("
								+ insql + ") ");

				HashMap<String, ArrayList<AssVO>> hm = new HashMap();
				for (int i = 0; (assVOs != null) && (i < assVOs.length); i++) {
					if (!hm.containsKey(assVOs[i].getPk_psnorg())) {
						hm.put(assVOs[i].getPk_psnorg(), new ArrayList());
					}
					((ArrayList) hm.get(assVOs[i].getPk_psnorg()))
							.add(assVOs[i]);
				}

				AssVO[] update = new AssVO[0];

				if (hm.keySet().size() > 0) {
					for (String key : hm.keySet()) {
						AssVO[] a = (AssVO[]) ((ArrayList) hm.get(key))
								.toArray(new AssVO[0]);
						SuperVOUtil.sortByAttributeName(a, "recordnum", true);
						for (int i = 0; i < a.length; i++) {
							a[i].setAttributeValue("recordnum",
									Integer.valueOf(i));
							if (i == 0) {
								a[i].setAttributeValue("lastflag",
										UFBoolean.TRUE);
							} else {
								a[i].setAttributeValue("lastflag",
										UFBoolean.FALSE);
							}
							a[i].setStatus(1);
						}
						update = (AssVO[]) ArrayUtils.addAll(update, a);
					}
				}

				if (update.length > 0) {
					((IPersistenceUpdate) NCLocator.getInstance().lookup(
							IPersistenceUpdate.class)).updateVOArray(null,
							update, null, null);
				}

			} finally {
				isc.clear();
			}

			getIPsndocService().updateDataAfterSubDataChanged(
					AssVO.getDefaultTableName(),
					(String[]) docPKList.toArray(new String[0]));
		}
	}

	public void updateRecordnumAndLastflagForHrss(Map<String, String[]> map)
			throws BusinessException {
		if ((map == null) || (map.size() == 0)) {
			return;
		}

		IPersistenceRetrieve retrieve = (IPersistenceRetrieve) NCLocator
				.getInstance().lookup(IPersistenceRetrieve.class);
		for (String pkPsndoc : map.keySet()) {
			String[] setPKs = (String[]) map.get(pkPsndoc);
			InfoSetVO[] set = (InfoSetVO[]) retrieve.retrieveByClause(null,
					InfoSetVO.class,
					" pk_infoset in (" + SQLHelper.joinToInSql(setPKs, -1)
							+ ") ");

			int i = 0;
			if ((set != null) && (i < set.length)) {
				try {
					SuperVO vo = (SuperVO) Class.forName(
							set[i].getVo_class_name()).newInstance();
					SuperVO[] vos = retrieve.retrieveByClause(null,
							Class.forName(set[i].getVo_class_name()),
							" pk_psndoc = '" + pkPsndoc
									+ "' order by recordnum,ts desc");

					if (((vo instanceof EduVO)) || ((vo instanceof CertVO))) {
						for (int j = 0; (vos != null) && (j < vos.length); j++) {
							vos[j].setAttributeValue("recordnum",
									Integer.valueOf(j));
							vos[j].setAttributeValue("lastflag", UFBoolean.TRUE);
						}
						((IPersistenceUpdate) NCLocator.getInstance().lookup(
								IPersistenceUpdate.class)).updateVOArray(null,
								vos, new String[] { "recordnum", "lastflag" },
								null);

					} else {
						updateAllRecordnumAndLastflag(vos);
					}
				} catch (Exception e) {
					Logger.error(e.getMessage(), e);
					throw new BusinessException(e);
				}
				i++;
			}
		}
	}

	public void syncPsnQulifyInfo(BatchOperateVO vo) throws BusinessException {
		if (vo == null) {
			return;
		}

		QulifyVO[] deledCurrtypeVOs = (QulifyVO[]) ArrayClassConvertUtil
				.convert(vo.getDelObjs(), QulifyVO.class);
		new BaseDAO().deleteVOArray(deledCurrtypeVOs);

		QulifyVO[] updatedCurrtypeVOs = (QulifyVO[]) ArrayClassConvertUtil
				.convert(vo.getUpdObjs(), QulifyVO.class);
		new BaseDAO().updateVOArray(updatedCurrtypeVOs);

		QulifyVO[] addedCurrObjects = (QulifyVO[]) ArrayClassConvertUtil
				.convert(vo.getAddObjs(), QulifyVO.class);
		new BaseDAO().insertVOArray(addedCurrObjects);
	}

	public HashMap<String, String[]> getAllPsnorgByPsndoc(String[] pk_psndocs)
			throws BusinessException {
		HashMap<String, String[]> mappks = new HashMap();

		InSQLCreator insql = new InSQLCreator();
		String conditon = " pk_psndoc in (" + insql.getInSQL(pk_psndocs) + ") ";
		PsnOrgVO[] psnorgs = (PsnOrgVO[]) getServiceTemplate()
				.queryByCondition(PsnOrgVO.class, conditon);
		if (ArrayUtils.isEmpty(psnorgs)) {
			Logger.error("??�??????�??????????");
			return null;
		}

		for (String pk_psndoc : pk_psndocs) {
			String[] pk_psnorgs = new String[psnorgs.length];
			for (int i = 0; i < psnorgs.length; i++) {
				if ((StringUtils.isNotBlank(pk_psndoc))
						&& (pk_psndoc.equals(psnorgs[i].getPk_psndoc())))
					pk_psnorgs[i] = psnorgs[i].getPk_psnorg();
			}
			mappks.put(pk_psndoc, pk_psnorgs);
		}

		return mappks;
	}

	@Override
	public PsnJobVO addNewDimissionWithDate(PsnJobVO dimission, String pkHrorg,
			boolean isDisablePsn, UFLiteralDate endDate)
			throws BusinessException {
		checkClerkCodeUnique(dimission);

		String pk_psnorg = dimission.getPk_psnorg();
		PsnJobVO before = (PsnJobVO) getLastVO(PsnJobVO.class, pk_psnorg,
				Integer.valueOf(1));
		if ((dimission.getPsntype() != null)
				&& (dimission.getPsntype().intValue() == 0)) {

			fireEvent(before, null, pkHrorg, "PSNJOB", "1001");
		}

		PsnOrgVO psnorg = (PsnOrgVO) getServiceTemplate().queryByPk(
				PsnOrgVO.class, pk_psnorg);
		psnorg.setPk_hrorg(pkHrorg);
		updateEndflagAndEnddate(psnorg, dimission.getBegindate());

		PsnJobVO[] psnjobs = (PsnJobVO[]) getServiceTemplate()
				.queryByCondition(
						PsnJobVO.class,
						" pk_psnorg = '" + pk_psnorg
								+ "' and ( endflag = 'N' or "
								+ SQLHelper.getNullSql("endflag") + " ) ");

		for (int i = 0; (psnjobs != null) && (i < psnjobs.length); i++) {
			updateEndflagAndEnddate(psnjobs[i], dimission.getBegindate());
			endWork(psnjobs[i], dimission);
		}

		PsnJobVO[] oldVOs = (PsnJobVO[]) getServiceTemplate().queryByCondition(
				PsnJobVO.class,
				" pk_psnorg = '" + pk_psnorg + "' and assgid = 1 ");
		updateRecordnumAndLastflag(oldVOs);

		TrialVO[] trial = (TrialVO[]) getServiceTemplate().queryByCondition(
				TrialVO.class,
				" pk_psnorg = '" + pk_psnorg + "' and  ( endflag = 'N' or "
						+ SQLHelper.getNullSql("endflag") + " ) ");

		for (int i = 0; (trial != null) && (i < trial.length); i++) {
			updateEndflagAndEnddate(trial[i], dimission.getBegindate());
		}

		getIPsndocService().updateDataAfterSubDataChanged(
				TrialVO.getDefaultTableName(),
				new String[] { dimission.getPk_psndoc() });

		synPkorgOfPsndoc(dimission.getPk_org(), dimission.getPk_psndoc());

		if ((dimission.getTrnstype() != null)
				&& ((dimission.getTrnstype().equals("1002Z710000000008GSV")) || (dimission
						.getTrnstype().equals("1002Z710000000008GSW")))) {

			String sql = " update bd_psndoc set retiredate = '"
					+ dimission.getBegindate() + "' where pk_psndoc = '"
					+ dimission.getPk_psndoc() + "' ";

			((IPersistenceUpdate) NCLocator.getInstance().lookup(
					IPersistenceUpdate.class))
					.executeSQLs(new String[] { sql });
		}

		PsnChgVO lastVO = (PsnChgVO) getLastVO(PsnChgVO.class, pk_psnorg, null);
		if ((lastVO != null) && (lastVO.getEnddate() == null)) {
			if ((lastVO.getBegindate() != null)
					&& (lastVO.getBegindate().compareTo(
							dimission.getBegindate()) >= 0)) {
				lastVO.setEnddate(lastVO.getBegindate());
			} else {
				lastVO.setEnddate(dimission.getBegindate().getDateBefore(1));
			}
			getServiceTemplate().update(lastVO, false);
			getIPsndocService().updateDataAfterSubDataChanged(
					PsnChgVO.getDefaultTableName(),
					new String[] { lastVO.getPk_psndoc() });
		}

		dimission.setPk_hrorg(pkHrorg);
		PsnJobVO after = (PsnJobVO) getServiceTemplate().insert(dimission);

		getIPsndocService().updateDataAfterSubDataChanged(
				PsnJobVO.getDefaultTableName(),
				new String[] { after.getPk_psndoc() });

		// ssx added for Taiwan NHI on 2015-10-15
		// Stop NHI Info
		// 2017-05-16 upgrade to V65, from JD code
		IPsndocSubInfoService4JFS nhiService = NCLocator.getInstance().lookup(
				IPsndocSubInfoService4JFS.class);
		nhiService.dismissPsnNHI(dimission.getPk_org(),
				dimission.getPk_psndoc(), endDate);
		// ssx added on 2017-12-19
		// ???�?F�??Y??????
		nhiService.dismissPsnGroupIns(dimission.getPk_org(), dimission
				.getPk_psndoc(), dimission.getBegindate().getDateBefore(1));

		if (isDisablePsn) {
			PsndocVO psndocVO = (PsndocVO) ((IPersistenceRetrieve) NCLocator
					.getInstance().lookup(IPersistenceRetrieve.class))
					.retrieveByPk(null, PsndocVO.class,
							dimission.getPk_psndoc());

			disableSingleVO(psndocVO);
		}

		String pk_psndoc = after.getPk_psndoc();
		String condition = " base_doc_type = "
				+ UserIdentityTypeEnumFactory.TYPE_PERSON
				+ " and pk_base_doc = '" + pk_psndoc + "' and enablestate = "
				+ 2;

		UserVO[] users = ((IUserManageQuery) NCLocator.getInstance().lookup(
				IUserManageQuery.class)).queryUserByClause(condition);
		if ((users != null) && (users.length > 0)) {
			for (UserVO user : users) {
				((IUserManage) NCLocator.getInstance()
						.lookup(IUserManage.class)).disableUser(user);
			}
		}

		if ((dimission.getPsntype() != null)
				&& (dimission.getPsntype().intValue() == 0)) {

			fireEvent(before, after, pkHrorg, "PSNJOB", "1002");
			HiEventValueObject
					.fireDataPermChangeEvent("7156d223-4531-4337-b192-492ab40098f1");
		}

		HiCacheUtils
				.synCache(new String[] { PsndocVO.getDefaultTableName(),
						PsnJobVO.getDefaultTableName(),
						PsnOrgVO.getDefaultTableName() });

		return after;
	}
}
