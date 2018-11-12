package nc.impl.ta.psndoc.listener;

import nc.bs.framework.common.NCLocator;
import nc.itf.om.IAOSQueryService;
import nc.itf.ta.ITBMPsndocManageMaintain;
import nc.itf.ta.ITBMPsndocQueryMaintain;
import nc.itf.ta.ITimeRuleQueryService;
import nc.plugin.hi.IPsndocIntoDoc;
import nc.vo.hi.psndoc.PsnJobVO;
import nc.vo.hi.psndoc.PsndocVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.psndoc.TBMPsndocCommonValue;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * ת����Ա�������Ӹ���Ա���ڵ�����¼,����ʧ��Ҳ���ٻع�����Ա��Ϣ���� Ҫ������������
 */

public class PsnChangeEventExpoint implements IPsndocIntoDoc{

	//private ITBMPsndocQueryMaintain  manageMaintain;
	
	public void afterPsnIntoDoc(PsnJobVO[] before, PsnJobVO[] after,String[] pkHrorg) throws BusinessException {
		afterPsnIntoDoc(null,before,after,pkHrorg);
	}
	
	@Override
	public void afterPsnIntoDoc(PsndocVO[] psndocvos,PsnJobVO[] before, PsnJobVO[] after,String[] pkHrorg) throws BusinessException {
			// ת����Ա�����Ƿ���Ҫ���п���
		//	boolean needToTBMpsndoc = true;
			//ֻ��ת����Ա����������������ڵ���
			if(ArrayUtils.isEmpty(after)) {
				return;
			}else{
				for(int i=0;i<after.length;i++){
					// ת����Ա�����Ƿ���Ҫ���п��ڣ�0���ǣ�1����
					//Integer needToTBMPsndocInt = SysInitQuery.getParaInt(pkHrorg[i],ITaParaConst.PARA_NEEDTO_PSNDOC);
					Integer needToTBMPsndocInt = null;
//						SysInitQuery.getParaInt(pkHrorg[i],TBMPsndocCommonValue.PARA_NEEDTO_PSNDOC);
					//2013-06-17�޸����⣬����ְ��hr��֯A,������Ϣ��hr��֯B���˴���psnjobvo��pk_hrog��A��֯��pk_org��B��֯�����ǿ��ڵ�����Ӧ�ü���B��֯��
					String pk_org = after[i].getPk_org();
					OrgVO  hrorg = NCLocator.getInstance().lookup(IAOSQueryService.class).queryHROrgByOrgPK(pk_org);
					String pk_hrorg = hrorg.getPk_org();
//					TimeRuleVO timerulevo = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pkHrorg[i]);
					TimeRuleVO timerulevo = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_hrorg);
					if(timerulevo!=null)
						needToTBMPsndocInt = timerulevo.getTotbmpsntype();
					//needToTBMpsndoc = needToTBMPsndocInt == null ? true: (needToTBMPsndocInt == 0 ? true : false);
					//if (!needToTBMpsndoc)
					//	continue;
					
					if(needToTBMPsndocInt==null||needToTBMPsndocInt==0)//Ĭ�Ϻ�Ϊ0ʱ����ʾ������
						continue;
					else
					addTBMPsndoc(psndocvos,after[i], null,needToTBMPsndocInt,pk_hrorg);
				}
			}
//		catch(Throwable e){
//			//��Ϊ�쳣�׳��˷������ڿ���̨��nclogs���Ҳ���������־����˳������ܶ�λ���⡣Ϊ��
//			//��������⣬������catch�쳣����ӡ��־��Ȼ�����׳�����ӡ�Ĵ�����־������nc-log.log���ҵ�
//			Logger.error(e.getMessage(), e);
//			if(e instanceof Error)
//				throw (Error)e;
//			if(e instanceof BusinessException)
//				throw (BusinessException)e;
//			throw new BusinessException(e.getMessage(), e);
//		}
	}

	/**
	 * ����������¼
	 * 
	 * @param psnJobVO
	 * @param tbmpsndocBeginDate
	 *            �����ڵ�����ʼ����
	 * @param pk_hrorg 
	 * @throws BusinessException
	 */
	private void addTBMPsndoc(PsndocVO[] psndocvos,PsnJobVO psnJobVO,
			UFLiteralDate tbmpsndocBeginDate,Integer tbmprop, String pk_hrorg) throws BusinessException {
		TBMPsndocVO psndocvo = getPsndocVOByPsnJob(psnJobVO,tbmpsndocBeginDate,pk_hrorg);
		if(psndocvo == null)
			return;
		
		if(tbmprop==1)//�ֹ�����
			psndocvo.setTbm_prop(TBMPsndocCommonValue.PROP_MANUAL);
		if(tbmprop==2)//��������
			psndocvo.setTbm_prop(TBMPsndocCommonValue.PROP_MACHINE);
		getITBMPsndocManageMaintain().insert(psndocvos,psndocvo,psnJobVO, true,true);
	}
	

	/**
	 * ���ݹ�����¼�������ڵ�����¼
	 * @param psnJobVO
	 * @param tbmpsndocBeginDate �����ڵ�����ʼ����
	 * @param pk_hrorg 
	 * @return
	 */
	private TBMPsndocVO getPsndocVOByPsnJob(PsnJobVO psnJobVO,UFLiteralDate tbmpsndocBeginDate, String pk_hrorg) {
		if(psnJobVO.getPk_psndoc() == null || psnJobVO.getPk_psnjob() == null)
			return null;
		TBMPsndocVO psndocvo = new TBMPsndocVO();
		psndocvo = getTBMPsndocByPsnjob(psnJobVO, psndocvo,tbmpsndocBeginDate,pk_hrorg);
		psndocvo.setStatus(VOStatus.NEW);
		return psndocvo;
	}
	/**
	 * ���ݹ�����¼��Ϣ���ÿ��ڵ�����Ϣ
	 * @param psnJobVO
	 * @param psndocvo
	 * @param tbmpsndocBeginDate �����ڵ�����ʼ����
	 * @param pk_hrorg 
	 * @return
	 */
	private TBMPsndocVO getTBMPsndocByPsnjob(PsnJobVO psnJobVO, TBMPsndocVO psndocvo,UFLiteralDate tbmpsndocBeginDate, String pk_hrorg) {
		psndocvo.setPk_psndoc(psnJobVO.getPk_psndoc());
		psndocvo.setPk_psnjob(psnJobVO.getPk_psnjob());
		psndocvo.setPk_group(psnJobVO.getPk_group());
//		psndocvo.setPk_org(psnJobVO.getPk_hrorg());//  2013-06-17�˴������⣬����ְ��hr��֯A,������Ϣ��hr��֯B���˴���psnjobvo��pk_hrog��A��֯��pk_org��B��֯�����ǿ��ڵ�����Ӧ�ü���B��֯��
		psndocvo.setPk_org(pk_hrorg);
		psndocvo.setPk_psnorg(psnJobVO.getPk_psnorg());
//		psndocvo.setPk_adminorg(psnJobVO.getPk_hrorg());//�����������֯
		psndocvo.setPk_adminorg(pk_hrorg);//�����������֯
		//���û�п�ʼ���ڣ���ȡԱ����ְ����
		psndocvo.setBegindate(tbmpsndocBeginDate==null?
				psnJobVO.getBegindate()
				:tbmpsndocBeginDate);
		if(psndocvo.getBegindate() == null) {
			psndocvo.setBegindate(new UFLiteralDate());
		}
		psndocvo.setEnddate(UFLiteralDate.getDate(TBMPsndocCommonValue.END_DATA));
		//��������
		psndocvo.setTbm_prop(TBMPsndocCommonValue.PROP_MACHINE);
		return psndocvo;
	}

	
	/**
	 * ��ѯ��Աδ��Ŀ��ڵ�����¼
	 * @param pk_psndoc
	 * @throws BusinessException 
	 */
	public ITBMPsndocQueryMaintain getManageMaintain() {
		return NCLocator.getInstance().lookup(ITBMPsndocQueryMaintain.class);
	}
	
	public ITBMPsndocManageMaintain getITBMPsndocManageMaintain(){
		return NCLocator.getInstance().lookup(ITBMPsndocManageMaintain.class);
	}
}