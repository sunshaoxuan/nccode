package nc.impl.hi.psndoc;

import java.util.HashMap;
import java.util.Map;

import nc.bs.bd.psn.validator.PsnIdtypeQuery;
import nc.bs.ml.NCLangResOnserver;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.itf.hi.PsndocDefUtil;
import nc.pubitf.para.SysInitQuery;
import nc.vo.bd.psnid.PsnIdtypeVO;
import nc.vo.hi.psndoc.PsndocAggVO;
import nc.vo.hi.psndoc.PsndocDefVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.lang.StringUtils;

public class PsndocNHIDAO {
	private static Map<String, UFBoolean> enableTaiwanNHI = null;

	public static UFBoolean ifEnableTaiwanNHI(String pk_org)
			throws BusinessException {
		if (enableTaiwanNHI == null) {
			enableTaiwanNHI = new HashMap<String, UFBoolean>();
		}

		if (!enableTaiwanNHI.containsKey(pk_org)) {
			enableTaiwanNHI.put(pk_org,
					SysInitQuery.getParaBoolean(pk_org, "TWHR01"));
		}

		return enableTaiwanNHI.get(pk_org);
	}

	/**
	 * 生成勞健保信息行
	 * 
	 * @param psndocAggVO
	 *            保存的人員信息主檔
	 * @throws BusinessException
	 */
	public static void generateNHIInfo(PsndocAggVO psndocAggVO)
			throws BusinessException {
		try {
			PsndocVO headVO = psndocAggVO.getParentVO();
			String pk_hrorg = headVO.getPk_hrorg();
			if (ifEnableTaiwanNHI(pk_hrorg).booleanValue()) {
				SimpleDocServiceTemplate service = new SimpleDocServiceTemplate(
						"TWHRGLBDEF");
				service.setLazyLoad(true);

				UFBoolean genLaborInfo = SysInitQuery.getParaBoolean(pk_hrorg,
						"TWHR02");
				if (genLaborInfo.booleanValue()) {
					String pk_psntype = SysInitQuery.getParaString(pk_hrorg,
							"TWHR03");
					if (StringUtils.isEmpty(pk_psntype)) {
						throw new BusinessException(NCLangResOnserver
								.getInstance().getStrByID("twhr_personalmgt",
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

				UFBoolean genHealthInfo = SysInitQuery.getParaBoolean(pk_hrorg,
						"TWHR04");
				if (genHealthInfo.booleanValue()) {
					String pk_psntype = SysInitQuery.getParaString(pk_hrorg,
							"TWHR05");
					if (StringUtils.isEmpty(pk_psntype)) {
						throw new BusinessException(NCLangResOnserver
								.getInstance().getStrByID("twhr_personalmgt",
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
							PsnIdtypeVO psnidtype = PsnIdtypeQuery
									.getPsnIdtypeVo(headVO.getIdtype());
							if (psnidtype.getCode().equals("TW01")) {
								defVO.setAttributeValue("glbdef3",
										headVO.getId());
							}
						}

						if (headVO.getBirthdate() != null) {
							defVO.setAttributeValue("glbdef4", new UFDateTime(
									headVO.getBirthdate().toDate())); // 出生日期
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
