package nc.impl.wa.paydata;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import nc.bs.dao.BaseDAO;
import nc.bs.logging.Logger;
import nc.hr.frame.persistence.SimpleDocServiceTemplate;
import nc.itf.hr.wa.IPsndocWa;
import nc.pub.encryption.util.SalaryDecryptUtil;
import nc.pub.encryption.util.SalaryEncryptionUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.wa.paydata.PsncomputeVO;
import nc.vo.wa.paydata.PsndocWaVO;
import nc.vo.wa.pub.PeriodStateVO;
import nc.vo.wa.pub.WaLoginVO;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class PsndocWaImpl implements IPsndocWa {
	private final String DOC_NAME = "psndocwa";

	private SimpleDocServiceTemplate serviceTemplate;

	private SimpleDocServiceTemplate getServiceTemplate() {
		if (serviceTemplate == null) {
			serviceTemplate = new SimpleDocServiceTemplate("psndocwa");
		}
		return serviceTemplate;
	}

	public PsndocWaVO insert(PsndocWaVO wavo) throws BusinessException {
		//台湾本地化加密
		wavo.setNowmoney(new UFDouble(SalaryEncryptionUtil.encryption(wavo.getNowmoney().doubleValue())));
		wavo.setNmoney(new UFDouble(SalaryEncryptionUtil.encryption(wavo.getNmoney().doubleValue())));
		wavo.setNbeforemoney(new UFDouble(SalaryEncryptionUtil.encryption(wavo.getNbeforemoney().doubleValue())));
		return (PsndocWaVO) getServiceTemplate().insert(wavo);
	}

	public PsndocWaVO update(PsndocWaVO wavo) throws BusinessException {
		//台湾本地化加密
		wavo.setNowmoney(new UFDouble(SalaryEncryptionUtil.encryption(wavo.getNowmoney().doubleValue())));
		wavo.setNmoney(new UFDouble(SalaryEncryptionUtil.encryption(wavo.getNmoney().doubleValue())));
		wavo.setNbeforemoney(new UFDouble(SalaryEncryptionUtil.encryption(wavo.getNbeforemoney().doubleValue())));
		return (PsndocWaVO) getServiceTemplate().update(wavo, true);
	}

	public void updatePsndocWas(PsndocWaVO[] psndocWas)
			throws BusinessException {
		List<PsndocWaVO> insertList = new ArrayList();
		PsndocWaDAO dmo = new PsndocWaDAO();
		dmo.deletePsndocWa(psndocWas);
		for (PsndocWaVO wavo : psndocWas) {
			//数据需要加密
			wavo.setNowmoney(new UFDouble(SalaryEncryptionUtil.encryption(wavo.getNowmoney().doubleValue())));
			wavo.setNmoney(new UFDouble(SalaryEncryptionUtil.encryption(wavo.getNmoney().doubleValue())));
			wavo.setNbeforemoney(new UFDouble(SalaryEncryptionUtil.encryption(wavo.getNbeforemoney().doubleValue())));
			
			insertList.add(wavo);
		}
		
		dmo.getBaseDao().insertVOList(insertList);
	}

	public PsncomputeVO[] queryAllShowResult(WaLoginVO vo, String deptpower,
			String psnclspower) throws BusinessException {
		PsncomputeVO[] tempvos = null;
		try {
			PsndocWaDAO dmo = new PsndocWaDAO();
			PsndocWaVO[] psndovVos = dmo.queryAllShowResult(vo, deptpower,
					psnclspower);
			if (ArrayUtils.isEmpty(psndovVos)) {
				return null;
			}
			tempvos = toWaComputeVO(psndovVos,
					vo.getPeriodVO().getCstartdate(), vo.getPeriodVO()
							.getCenddate());
			WaWorkDay waWorkDay = new WaWorkDay();
			waWorkDay.setWorkDayAndWage(tempvos, vo);
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e);
		}

		return tempvos;
	}

	private PsncomputeVO[] toWaComputeVO(PsndocWaVO[] rsVOs,
			UFLiteralDate begindate, UFLiteralDate enddate) {
		int count = 0;
		String pk = "";
		String pk_item = "";
		Vector<PsncomputeVO> v = new Vector();
		PsncomputeVO[] computevos = null;

		for (PsndocWaVO rsVO : rsVOs) {
			PsncomputeVO tempvo = new PsncomputeVO();
			PsndocWaVO psndocWa = new PsndocWaVO();

			tempvo.setPk_psndoc_sub(rsVO.getPk_psndoc_sub());
			tempvo.setPk_psndoc(rsVO.getPk_psndoc());
			tempvo.setClerkcode(rsVO.getClerkcode());
			tempvo.setPsncode(rsVO.getPsncode());
			tempvo.setPsnname(rsVO.getPsnname());
			tempvo.setDeptname(rsVO.getDeptname());
			tempvo.setPk_wa_item(rsVO.getPk_wa_item());
			tempvo.setAssgid(rsVO.getAssgid());

			if (pk.equals(rsVO.getPk_psndoc())) {
				if (!pk_item.equals(rsVO.getPk_wa_item())) {
					count = 0;
				}
			} else {
				count = 0;
			}
			pk = rsVO.getPk_psndoc();
			pk_item = rsVO.getPk_wa_item();

			tempvo.setItemvname(rsVO.getItemname());
			tempvo.setBegindate(rsVO.getBegindate());
			tempvo.setEnddate(rsVO.getEnddate());
			tempvo.setDangbiename(rsVO.getDbname());
			tempvo.setJibiename(rsVO.getJbname());
			tempvo.setWadocnmoney(rsVO.getNowmoney());
			tempvo.setBasedays(rsVO.getBasedays());
			String nowjidangname = "";
			if (tempvo.getJibiename() != null) {
				nowjidangname = tempvo.getJibiename();
			}
			if ((tempvo.getDangbiename() != null)
					&& (nowjidangname.length() > 0)) {
				nowjidangname = nowjidangname + "/" + tempvo.getDangbiename();
			}
			tempvo.setNowJiDangName(nowjidangname);
			psndocWa.setPk_psndoc_wa(rsVO.getPk_psndoc_wa());
			tempvo.setPk_psncompute(psndocWa.getPk_psndoc_wa());
			if ((rsVO.isCheckflag())
					|| (!StringUtil.isEmpty(rsVO.getPk_psndoc_wa()))) {
				tempvo.setIscompute(new Boolean(true));
			} else {
				tempvo.setIscompute(new Boolean(false));
			}
			tempvo.setWanmoney(rsVO.getNmoney());
			tempvo.setWanbeforemoney(rsVO.getNbeforemoney());
			tempvo.setWanceforedays(rsVO.getNceforedays());
			tempvo.setWanaftermoney(rsVO.getNaftermoney());
			tempvo.setWanafterdays(rsVO.getNafterdays());
			tempvo.setDaywage(rsVO.getDaywage());
			tempvo.setPsndocwavo(rsVO);
			tempvo.setPsnbasdocPK(rsVO.getPk_psndoc());
			tempvo.setSub_ts(rsVO.getWadocts());
			if (count <= 1) {
				if (count == 0) {
					UFLiteralDate tempBegindate = new UFLiteralDate(tempvo
							.getBegindate().getMillis());
					if (!tempBegindate.after(enddate)) {
						v.addElement(tempvo);
						count++;
					}
				} else {
					((PsncomputeVO) v.get(v.size() - 1))
							.setOldJiDangName(tempvo.getNowJiDangName());
					((PsncomputeVO) v.get(v.size() - 1))
							.setOldwadocnmoney(tempvo.getWadocnmoney());
					((PsncomputeVO) v.get(v.size() - 1)).setOldbegindate(tempvo
							.getBegindate());
					((PsncomputeVO) v.get(v.size() - 1)).setOldenddate(tempvo
							.getEnddate() == null ? ((PsncomputeVO) v.get(v
							.size() - 1)).getBegindate().getDateBefore(1)
							: tempvo.getEnddate());
					((PsncomputeVO) v.get(v.size() - 1)).setPre_sub_id(tempvo
							.getPk_psndoc_sub());
					((PsncomputeVO) v.get(v.size() - 1)).setPre_sub_ts(tempvo
							.getSub_ts());
					((PsncomputeVO) v.get(v.size() - 1)).setIscompute(Boolean
							.valueOf((((PsncomputeVO) v.get(v.size() - 1))
									.getIscompute().booleanValue())
									&& (tempvo.getIscompute().booleanValue())));
					count++;
				}
			}
		}
		computevos = new PsncomputeVO[v.size()];
		if (v.size() > 0) {
			v.copyInto(computevos);
		}
		for (PsncomputeVO psncomputeVO : computevos) {
			if ((psncomputeVO.getEnddate() != null)
					&& (psncomputeVO.getEnddate().before(enddate))) {
				psncomputeVO.setOldJiDangName(psncomputeVO.getNowJiDangName());
				psncomputeVO.setOldwadocnmoney(psncomputeVO.getWadocnmoney());
				psncomputeVO.setOldbegindate(psncomputeVO.getBegindate());
				psncomputeVO.setOldenddate(psncomputeVO.getEnddate());
				psncomputeVO.setNowJiDangName(null);
				psncomputeVO.setWadocnmoney(UFDouble.ZERO_DBL);
				psncomputeVO.setBegindate(psncomputeVO.getEnddate()
						.getDateAfter(1));
				psncomputeVO.setEnddate(null);
				psncomputeVO.setPre_sub_id(psncomputeVO.getPk_psndoc_sub());
				psncomputeVO.setPre_sub_ts(psncomputeVO.getSub_ts());
				psncomputeVO.setPk_psndoc_sub(null);
				psncomputeVO.setSub_ts(null);
			}
		}

		return computevos;
	}

	public boolean isCheckPsndocWaHave(WaLoginVO waLoginVO, String pk_psndoc)
			throws BusinessException {
		boolean bool = false;
		try {
			PsndocWaDAO dmo = new PsndocWaDAO();
			bool = dmo.ischeck(waLoginVO, pk_psndoc);
		} catch (Exception e) {
			Logger.error(e);
		}
		return bool;
	}

	public boolean isExistUnCaculatePsn(WaLoginVO waLoginVO, String deptpower,
			String powclpower) throws BusinessException {
		PsndocWaDAO dmo = new PsndocWaDAO();
		try {
			PsndocWaVO[] psndovVos = dmo.queryAllShowResult(waLoginVO,
					deptpower, powclpower);
			dmo.deletePsndocWaWithoutSD(waLoginVO, psndovVos);
			if (ArrayUtils.isEmpty(psndovVos)) {
				return false;
			}
			for (PsndocWaVO psndovVo : psndovVos) {
				if (StringUtils.isEmpty(psndovVo.getPk_psndoc_wa())) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e);
		}
	}
}