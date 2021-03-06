package nc.impl.hi.psndoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import nc.bs.bd.psn.validator.PsnIdtypeQuery;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.ml.NCLangResOnserver;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.itf.hi.PsndocDefUtil;
import nc.itf.twhr.ITwhr_declarationMaintain;
import nc.pubitf.para.SysInitQuery;
import nc.vo.bd.psnid.PsnIdtypeVO;
import nc.vo.hi.psndoc.PTCostVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocDefVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.twhr.nhicalc.PsndocDefTableUtil;

public class PsndocNHIDAO {
	private static Map<String, UFBoolean> enableTaiwanNHI = null;

	public static UFBoolean ifEnableTaiwanNHI(String pk_org) throws BusinessException {
		if (enableTaiwanNHI == null) {
			enableTaiwanNHI = new HashMap<String, UFBoolean>();
		}

		if (!enableTaiwanNHI.containsKey(pk_org)) {
			enableTaiwanNHI.put(pk_org, SysInitQuery.getParaBoolean(pk_org, "TWHR01"));
		}

		return enableTaiwanNHI.get(pk_org);
	}

	public static void dealNHIDates(PsndocAggVO psndocAggVO) throws BusinessException {
		String pk_psndoc = psndocAggVO.getParentVO().getPk_psndoc();
		SuperVO[] laborVOs = psndocAggVO.getChildVOsByParentId(PsndocDefTableUtil.getPsnLaborTablename(), pk_psndoc);
		List<String> sqls = new ArrayList<String>();
		if (laborVOs != null && laborVOs.length > 0) {
			for (int i = laborVOs.length - 1; i >= 0; i--) {
				if (laborVOs[i].getStatus() == VOStatus.NEW) {
					//mod Ares.Tank 2018年10月15日17:49:33 如果上一行的结束日期为空则回写,如果不为空则不回写.
					SuperVO lastVO = null;
					// 勞保
					UFLiteralDate curLaborDate = (UFLiteralDate) laborVOs[i].getAttributeValue("begindate");
					if(null != curLaborDate){
						// 找上一行
						lastVO = getLastLaborVO(laborVOs, curLaborDate);
						
						if (lastVO != null && null == lastVO.getAttributeValue("enddate")) {
							sqls.add("update " + PsndocDefTableUtil.getPsnLaborTablename() + " set enddate='"
									+ curLaborDate.getDateBefore(1).toString() + "' where pk_psndoc_sub='"
									+ lastVO.getPrimaryKey() + "'");
						}
					}
					

					// 勞退
					UFLiteralDate curRetireDate = (UFLiteralDate) laborVOs[i].getAttributeValue("glbdef14");
					if(null != curRetireDate){
						// 找上一行
						lastVO = getLastRetireVO(laborVOs, curRetireDate);
						
						if (lastVO != null && null == lastVO.getAttributeValue("glbdef15")) {
							sqls.add("update " + PsndocDefTableUtil.getPsnLaborTablename() + " set glbdef15='"
									+ curRetireDate.getDateBefore(1).toString() + "' where pk_psndoc_sub='"
									+ lastVO.getPrimaryKey() + "'");
						}
					}
					//mod 2018年10月15日17:33:51 end
				}
			}
		}

		SuperVO[] healthVOs = psndocAggVO.getChildVOsByParentId(PsndocDefTableUtil.getPsnHealthTablename(), pk_psndoc);
		if (healthVOs != null && healthVOs.length > 0) {
			for (int i = healthVOs.length - 1; i >= 0; i--) {
				if (healthVOs[i].getStatus() == VOStatus.NEW) {
					UFLiteralDate curDate = (UFLiteralDate) healthVOs[i].getAttributeValue("begindate");
					String curID = (String) healthVOs[i].getAttributeValue("glbdef3");
					// 找上一行 
					//mod 2018年10月15日17:48:44  Ares.Tank 
					//健保涉及员工和眷属,眷属要跟着员工走????????????(这不是乱码),逻辑混乱,暂不回写结束日期  by Jessie
					/*SuperVO lastVO = getLastHealthVO(healthVOs, curDate, curID);
					if (lastVO != null) {
						sqls.add("update " + PsndocDefTableUtil.getPsnHealthTablename() + " set enddate='"
								+ curDate.getDateBefore(1).toString() + "' where pk_psndoc_sub='"
								+ lastVO.getPrimaryKey() + "'");
					}*/
					//mod 2018年10月15日17:48:44 end 
				}
			}
		}
		SuperVO[] pTCostVOs = psndocAggVO.getChildVOsByParentId("hi_psndoc_ptcost", pk_psndoc);
		if (pTCostVOs != null && pTCostVOs.length > 0) {
			for (int i = pTCostVOs.length - 1; i >= 0; i--) {
				if (pTCostVOs[i].getStatus() == VOStatus.NEW || pTCostVOs[i].getStatus() == VOStatus.UPDATED
						|| pTCostVOs[i].getStatus() == VOStatus.DELETED) {
					// 新增或者修改,都需要走回写到二代健保节点 Ares.Tank 2018-9-27 15:50:18
					ITwhr_declarationMaintain service = NCLocator.getInstance().lookup(ITwhr_declarationMaintain.class);
					service.writeBack4PTCost((PTCostVO) pTCostVOs[i], psndocAggVO.getParentVO().getPk_org(),
							psndocAggVO.getParentVO().getPk_group());
				}
			}
		}

		if (sqls.size() > 0) {
			BaseDAO dao = new BaseDAO();
			for (String sql : sqls) {
				dao.executeUpdate(sql);
			}
		}
	}

	private static SuperVO getLastRetireVO(SuperVO[] laborVOs, UFLiteralDate newestDate) {
		UFLiteralDate lastDate = new UFLiteralDate("0000-01-01");
		SuperVO ret = null;
		for (int i = 0; i < laborVOs.length; i++) {
			UFLiteralDate curDate = (UFLiteralDate) laborVOs[i].getAttributeValue("glbdef14");
			if (curDate != null && lastDate.beforeDate(curDate) && curDate.beforeDate(newestDate)) {
				lastDate = curDate;
				ret = laborVOs[i];
			}
		}
		return ret;
	}

	private static SuperVO getLastHealthVO(SuperVO[] laborVOs, UFLiteralDate newestDate, String newestID) {
		UFLiteralDate lastDate = new UFLiteralDate("0000-01-01");
		SuperVO ret = null;
		for (int i = 0; i < laborVOs.length; i++) {
			UFLiteralDate curDate = (UFLiteralDate) laborVOs[i].getAttributeValue("begindate");
			String curID = (String) laborVOs[i].getAttributeValue("glbdef3");
			if (newestID.equals(curID) && lastDate.before(curDate) && curDate.before(newestDate)) {
				lastDate = curDate;
				ret = laborVOs[i];
			}
		}
		return ret;
	}

	private static SuperVO getLastLaborVO(SuperVO[] laborVOs, UFLiteralDate newestDate) {
		UFLiteralDate lastDate = new UFLiteralDate("0000-01-01");
		SuperVO ret = null;
		for (int i = 0; i < laborVOs.length; i++) {
			UFLiteralDate curDate = (UFLiteralDate) laborVOs[i].getAttributeValue("begindate");
			if (lastDate.before(curDate) && curDate.before(newestDate)) {
				lastDate = curDate;
				ret = laborVOs[i];
			}
		}
		return ret;
	}

	/**
	 * 生成勞健保信息行
	 *
	 * @param psndocAggVO
	 *            保存的人員信息主檔
	 * @throws BusinessException
	 */
	public static void generateNHIInfo(PsndocAggVO psndocAggVO) throws BusinessException {
		try {
			PsndocVO headVO = psndocAggVO.getParentVO();
			String pk_hrorg = headVO.getPk_hrorg();
			if (ifEnableTaiwanNHI(pk_hrorg).booleanValue()) {
				SimpleDocServiceTemplate service = new SimpleDocServiceTemplate("TWHRGLBDEF");
				service.setLazyLoad(true);

				UFBoolean genLaborInfo = SysInitQuery.getParaBoolean(pk_hrorg, "TWHR02");
				if (genLaborInfo.booleanValue()) {
					String pk_psntype = SysInitQuery.getParaString(pk_hrorg, "TWHR03");
					if (StringUtils.isEmpty(pk_psntype)) {
						throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("twhr_personalmgt",
								"PsndocNHIDAO-0000")/*
													 * 未指定默认劳保身份 。
													 */);
					} else {

						PsndocDefVO defVO = PsndocDefUtil.getPsnLaborVO();
						defVO.setPk_psndoc(headVO.getPk_psndoc());
						defVO.setBegindate(headVO.getPsnJobVO().getBegindate());
						defVO.setEnddate(new UFLiteralDate("9999-12-31"));
						defVO.setRecordnum(1);
						defVO.setLastflag(UFBoolean.TRUE);
						defVO.setAttributeValue("glbdef1", pk_psntype); // 勞保身份
						service.insert(defVO);

					}
				}

				UFBoolean genHealthInfo = SysInitQuery.getParaBoolean(pk_hrorg, "TWHR04");
				if (genHealthInfo.booleanValue()) {
					String pk_psntype = SysInitQuery.getParaString(pk_hrorg, "TWHR05");
					if (StringUtils.isEmpty(pk_psntype)) {
						throw new BusinessException(NCLangResOnserver.getInstance().getStrByID("twhr_personalmgt",
								"PsndocNHIDAO-0001")/*
													 * 未指定默认健保身份 。
													 */);
					} else {
						PsndocDefVO defVO = PsndocDefUtil.getPsnHealthVO();
						defVO.setPk_psndoc(headVO.getPk_psndoc());
						defVO.setBegindate(headVO.getPsnJobVO().getBegindate());
						defVO.setEnddate(new UFLiteralDate("9999-12-31"));
						defVO.setRecordnum(1);
						defVO.setLastflag(UFBoolean.TRUE);
						defVO.setAttributeValue("glbdef1", headVO.getName()); // 投保人或眷属姓名

						// 臺灣身份證字號同步
						// ssx added on 2015-12-07
						if (headVO.getIdtype() != null) {
							PsnIdtypeVO psnidtype = PsnIdtypeQuery.getPsnIdtypeVo(headVO.getIdtype());
							if (psnidtype.getCode().equals("TW01")) {
								defVO.setAttributeValue("glbdef3", headVO.getId());
							}
						}

						if (headVO.getBirthdate() != null) {
							defVO.setAttributeValue("glbdef4", new UFDateTime(headVO.getBirthdate().toDate())); // 出生日期
						}
						defVO.setAttributeValue("glbdef5", pk_psntype); // 健保身份
						defVO.setAttributeValue("glbdef14", UFBoolean.TRUE); // 是否投保
						defVO.setAttributeValue("glbdef15", UFBoolean.TRUE); // 是否所得税抚养人
						service.insert(defVO);
					}
				}
			}
		} catch (Exception ex) {
			throw new BusinessException(ex.getMessage());
		}
	}
}
