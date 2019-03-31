package nc.impl.ta.leavebalance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.hr.utils.CommonUtils;
import nc.hr.utils.PubEnv;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.ta.persistence.TASimpleDocServiceTemplate;
import nc.itf.om.IOrgInfoQueryService;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.ILeaveBalanceQueryService;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.impl.DefaultDateScope;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.hi.psndoc.PsnOrgVO;
import nc.vo.ml.MultiLangUtil;
import nc.vo.om.orginfo.HROrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.AggLeaveVO;
import nc.vo.ta.leave.LeaveCommonVO;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.LeavebVO;
import nc.vo.ta.leave.LeavehVO;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.pub.IMetaDataIDConst;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author zengcheng
 *
 */
public class LeaveBalanceServiceImpl implements ILeaveBalanceQueryService,
		ILeaveBalanceManageService {
	
	private TASimpleDocServiceTemplate serviceTemplate;
	public TASimpleDocServiceTemplate getServiceTemplate() {
		if(serviceTemplate == null)
			serviceTemplate = new TASimpleDocServiceTemplate(IMetaDataIDConst.LEAVEBALANCE);
		return serviceTemplate;
	}

	@Override
	public Map<String, LeaveBalanceVO> queryAndCalLeaveBalanceVO(String pk_org, Object... leaveCommonVOs) throws BusinessException {
		if(ArrayUtils.isEmpty(leaveCommonVOs))
			return null;
		boolean isReg = leaveCommonVOs[0] instanceof LeaveRegVO;
		// ���ݼٵ����ݼ������ȡ��ڼ����
		Map<String, List<Object>> balanceMap = new HashMap<String, List<Object>>();
		for (Object vo : leaveCommonVOs) {
			Object tmp = null;
			String primaryKey = null;
			if(isReg){
				LeaveRegVO regVO = ((LeaveRegVO)vo);
				primaryKey = regVO.getPk_leavetype()+regVO.getYearmonth();
				tmp = regVO;
			}
			else{
				LeavehVO hVO = (vo instanceof AggLeaveVO)?((AggLeaveVO)vo).getLeavehVO():(LeavehVO)vo;
				primaryKey = hVO.getPk_leavetype()+hVO.getLeaveyear()+(StringUtils.isEmpty(hVO.getLeavemonth())?"":hVO.getLeavemonth());
				tmp = hVO;
			}
			List<Object> voList = balanceMap.get(primaryKey);
			if (CollectionUtils.isEmpty(voList)){
				voList = new ArrayList<Object>();
				balanceMap.put(primaryKey, voList);
			}
			voList.add(tmp);
		}
		// ��������������������
		Map<String, LeaveTypeCopyVO> typeMap = NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryLeaveCopyTypeMapByOrg(pk_org);
		Map<String, LeaveBalanceVO> resultMap = new HashMap<String, LeaveBalanceVO>();
		for(String key:balanceMap.keySet()) {
			SuperVO[] vos = balanceMap.get(key).toArray(new SuperVO[0]);
			LeaveTypeCopyVO typeVO = typeMap.get(isReg?((LeaveRegVO)vos[0]).getPk_leavetype():((LeavehVO)vos[0]).getPk_leavetype());
			String[] pk_psnorgs = StringPiecer.getStrArrayDistinct(vos, LeaveCommonVO.PK_PSNORG);
			String year = isReg?((LeaveRegVO)vos[0]).getLeaveyear():((LeavehVO)vos[0]).getLeaveyear();
			String month = isReg?((LeaveRegVO)vos[0]).getLeavemonth():((LeavehVO)vos[0]).getLeavemonth();
			// ����Ҫ��������ʱ��
			Map<String, LeaveBalanceVO> calMap = queryAndCalLeaveBalanceVO(pk_org, typeVO, pk_psnorgs, year, month, vos);
			if(MapUtils.isEmpty(calMap))
				continue;
			resultMap.putAll(calMap);
		}
		return resultMap;
	}
	
	/**
	 * ��ѯ��������ڼ�¼
	 * @param pk_org
	 * @param typeVO
	 * @param pk_psnorgs
	 * @param year
	 * @param month
	 * @return Map<pk_psnorg+pk_timeitem+year+month, List<LeaveBalanceVO>>
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, LeaveBalanceVO> queryAndCalLeaveBalanceVO(String pk_org, LeaveTypeCopyVO typeVO, String[] pk_psnorgs,String year,String month, Object[] sources)throws BusinessException {
		if(typeVO==null)
			return null;
		int leavesetperiod = typeVO.getLeavesetperiod().intValue();
		UFLiteralDate periodBeginDate = null;
		UFLiteralDate periodEndDate = null;
		boolean isNotHire = leavesetperiod==LeaveTypeCopyVO.LEAVESETPERIOD_MONTH||leavesetperiod==LeaveTypeCopyVO.LEAVESETPERIOD_YEAR;
		if(isNotHire){// ���ǰ���ְ���ڽ����
			boolean isYear = leavesetperiod==TimeItemCopyVO.LEAVESETPERIOD_YEAR;
			if(isYear){
				PeriodVO[] periodVOs = PeriodServiceFacade.queryByYear(pk_org, year);
				if(ArrayUtils.isEmpty(periodVOs))
					return null;
				periodBeginDate = periodVOs[0].getBegindate();
				periodEndDate = periodVOs[periodVOs.length-1].getEnddate();
			}
			else{
				PeriodVO periodVO = PeriodServiceFacade.queryByYearMonth(pk_org, year, month);
				if(periodVO==null)
					return null;
				periodBeginDate = periodVO.getBegindate();
				periodEndDate = periodVO.getEnddate();
			}
		}
		
		// ����ǰ���ְ���ڽ��㣬��Ҫ֪����ְ����
		Map<String, PsnOrgVO> psnOrgMap = new HashMap<String, PsnOrgVO>();
		if(!isNotHire){
			PsnOrgVO[] psnOrgVOs = CommonUtils.toArray(PsnOrgVO.class, (Collection<PsnOrgVO>)MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(PsnOrgVO.class, pk_psnorgs, false));
			psnOrgMap = CommonUtils.toMap(PsnOrgVO.PK_PSNORG, psnOrgVOs);
		}
		LeaveBalanceMaintainImpl impl = new LeaveBalanceMaintainImpl();
		IDateScope[] scopes = new IDateScope[pk_psnorgs.length]; 
		for(int i = 0;i < pk_psnorgs.length;i++){
//			UFLiteralDate begindate = isNotHire ? periodBeginDate:impl.getHireBeginDate(year, impl.getHireDate(psnOrgMap.get(pk_psnorgs[i])));
//			UFLiteralDate enddate = isNotHire ? periodEndDate:impl.getHireEndDate(year, impl.getHireDate(psnOrgMap.get(pk_psnorgs[i])));
			
			//BEGIN �ź�    ��65���������յ����ϵ�63   2018/8/28
			// ssx added on 2018-03-16
			// for changes of start date of company age
			UFLiteralDate begindate = null;
			UFLiteralDate enddate = null;
			//MOD James
			if (leavesetperiod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
				begindate = isNotHire ? periodBeginDate : impl
						.getHireBeginDate(year, impl.getHireStartDate(psnOrgMap
								.get(pk_psnorgs[i])));
				enddate = isNotHire ? periodEndDate : impl.getHireEndDate(year,
						impl.getHireStartDate(psnOrgMap.get(pk_psnorgs[i])));
			}
			else{
				begindate = isNotHire ? periodBeginDate : impl
						.getHireBeginDate(year,
								impl.getHireDate(psnOrgMap.get(pk_psnorgs[i])));
				enddate = isNotHire ? periodEndDate : impl.getHireEndDate(year,
						impl.getHireDate(psnOrgMap.get(pk_psnorgs[i])));
			}
			//END �ź�    ��65���������յ����ϵ�63   2018/8/28
			
			scopes[i] = new DefaultDateScope(begindate, enddate);
			if(!isNotHire){
				if(null==periodBeginDate){
					periodBeginDate = begindate;
				}
				if(null==periodEndDate){
					periodEndDate = enddate;
				}
			}
		}
		// �ڼ䷶Χ���Ƿ��п��ڵ�����Ϣ�����û�п��ڵ�������϶������н����¼
		String[] noDocPsnOrgs = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).existsTBMPsndocs(pk_org, pk_psnorgs, scopes);
		if(!ArrayUtils.isEmpty(noDocPsnOrgs))
			return null;
		// ��ѯ���ڼ����¼
		Map<String, List<LeaveBalanceVO>> balanceMap = new LeaveBalanceDAO().queryLeaveBalanceMapByPsnOrgs(pk_org, typeVO, pk_psnorgs, year, month);
		return findNewestBalanceVO(pk_org, typeVO, pk_psnorgs, year, month, periodBeginDate, periodEndDate, balanceMap, sources);
	}
	
	/**
	 * ��ѯ���µļ��ڼ�¼
	 * @param pk_org
	 * @param typeVO
	 * @param pk_psnorgs
	 * @param year
	 * @param month
	 * @param periodBeginDate
	 * @param periodEndDate
	 * @param balanceMap Map<pk_psnorg+pk_timeitem+year+month, List<LeaveBalanceVO>>���ݿ������еļ��ڼ����¼
	 * @param pk_excludSelf
	 * @param isApp
	 * @return Map<pk_psnorg+pk_timeitem+year+month, List<LeaveBalanceVO>>
	 * @throws BusinessException
	 */
	private Map<String, LeaveBalanceVO> findNewestBalanceVO(String pk_org,LeaveTypeCopyVO typeVO,String[] pk_psnorgs,
			String year,String month,UFLiteralDate periodBeginDate,UFLiteralDate periodEndDate,Map<String, List<LeaveBalanceVO>> balanceMap,Object[] sources) throws BusinessException{
		List<LeaveBalanceVO> newVOList = new ArrayList<LeaveBalanceVO>(); // ���µļ��ڼ�������
		Map<String, Integer> needNewMap = new HashMap<String, Integer>(); // ��Ҫ�������ڼ�������
		List<LeaveBalanceVO> settVOList = new ArrayList<LeaveBalanceVO>(); // ���µ��ѽ���ļ�������
		List<String> psnorgList = new ArrayList<String>(); // settVOList�е���Ա�б�
		List<IDateScope> scopeList = new ArrayList<IDateScope>(); // settVOList�е����ڶ��б�
		int leavesetPeriod = typeVO.getLeavesetperiod().intValue();
		for(String pk_psnorg:pk_psnorgs) {
			List<LeaveBalanceVO> dbVOs = MapUtils.isEmpty(balanceMap)?null:balanceMap.get(pk_psnorg);
			if(CollectionUtils.isEmpty(dbVOs)){ // �޼��ڼ�������
				needNewMap.put(pk_psnorg, 1);
				continue;
			}
			LeaveBalanceVO lastSettVO = null; //���µ��ѽ���������� 
			for(LeaveBalanceVO dbVO:dbVOs) {
				if(!dbVO.isSettlement()){ //���δ���㣬�������µ�
					newVOList.add(dbVO);
					lastSettVO = null;
					break;
				}
				if(lastSettVO==null || lastSettVO.getSettlementdate().before(dbVO.getSettlementdate()))
					lastSettVO = dbVO;
			}
			if(lastSettVO==null)
				continue;
			// �����Ľ������ݣ����������Ƿ����ڼ����֮��
			UFLiteralDate endDate = leavesetPeriod==LeaveTypeCopyVO.LEAVESETPERIOD_DATE?lastSettVO.getHireenddate():periodEndDate;
			if(!lastSettVO.getSettlementdate().before(endDate)) { // �����������ڼ����֮��
//				lastSettVO.setPeriodbegindate(leavesetPeriod==LeaveTypeCopyVO.LEAVESETPERIOD_DATE?lastSettVO.getHirebegindate():periodBeginDate);
				
				//BEGIN �ź�    ��65���������յ����ϵ�63   2018/8/28
				lastSettVO.setPeriodbegindate((leavesetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_DATE || 
						                       leavesetPeriod == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) 
						                      ? lastSettVO.getHirebegindate() : periodBeginDate);
				//END �ź�    ��65���������յ����ϵ�63   2018/8/28
				
				lastSettVO.setPeriodenddate(periodEndDate);
				lastSettVO.setPeriodextendenddate(periodEndDate.getDateAfter(typeVO.getExtendDaysCount()));
				newVOList.add(lastSettVO);
				continue;
			}
			// ����������ڵ��ڼ�������ڻ����ڿ��ڵ�������newһ������������ھͷ������Ľ����¼
			psnorgList.add(pk_psnorg);
			scopeList.add(new DefaultDateScope(lastSettVO.getSettlementdate().getDateAfter(1), endDate));
			settVOList.add(lastSettVO); //�������µ��ѽ���������������List
		}
		// ����������ѽ�������
		if(CollectionUtils.isNotEmpty(settVOList)) { 
			String[] noDocPsns = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class).existsTBMPsndocs(pk_org, psnorgList.toArray(new String[0]), scopeList.toArray(new IDateScope[0]));
			for(LeaveBalanceVO settVO:settVOList) {
				String pk_psnorg = settVO.getPk_psnorg();
				if(ArrayUtils.contains(noDocPsns, pk_psnorg)) {//����������ڵ��ڼ��������û�п��ڵ������ͷ������Ľ����¼
					newVOList.add(settVO);
					continue;
				}
				// ������ڿ��ڵ�������Ҫnewһ���µļ��ڼ�¼
				needNewMap.put(pk_psnorg, balanceMap.get(pk_psnorg).size()+1);
			}
		}
		
		// ������Ҫnew���ڼ�¼����Ա
		if(MapUtils.isNotEmpty(needNewMap)) {
			CollectionUtils.addAll(newVOList, createNewAndCalculate(pk_org, typeVO, needNewMap, year, month));
		}
		// ����ʱ��������ʱ��
		LeaveBalanceVO[] balanceVOs = newVOList.toArray(new LeaveBalanceVO[0]);
		if(ArrayUtils.isEmpty(balanceVOs))
			return null;
		new LeaveBalanceMaintainImpl().calculate(pk_org, typeVO, year, month, balanceVOs, new UFDateTime());
		execHourInfo(balanceVOs, sources);
		Map<String, LeaveBalanceVO> resultMap = new HashMap<String, LeaveBalanceVO>();
		for(LeaveBalanceVO vo:balanceVOs)
			resultMap.put(vo.getPk_psnorg()+vo.getPk_timeitem()+vo.getCuryear()+(StringUtils.isEmpty(vo.getCurmonth())?"":vo.getCurmonth()), vo);
		return resultMap;
	}
	
	/**
	 * ��������ʱ��������ʱ��
	 * @param balanceVOs
	 * @param sources
	 * @throws BusinessException 
	 */
	private void execHourInfo(LeaveBalanceVO[] balanceVOs, Object[] sources) throws BusinessException{
		if(ArrayUtils.isEmpty(balanceVOs)||ArrayUtils.isEmpty(sources))
			return;
		Object simpleVO = sources[0];
		if(!(simpleVO instanceof AggLeaveVO||simpleVO instanceof LeavehVO||simpleVO instanceof LeaveCommonVO))
			return;
		Set<String> keyList = new HashSet<String>();
		for(Object source:sources){
			String primaryKey = (source instanceof AggLeaveVO)?((AggLeaveVO)source).getLeavehVO().getPk_leaveh():((SuperVO)source).getPrimaryKey();
			if(source instanceof AggLeaveVO)
				primaryKey = ((AggLeaveVO)source).getLeavehVO().getPk_leaveh();
			else if(source instanceof LeavebVO) // ��Ϊ�ӱ�ʱ���Ƚ����⣬��Ҫ��ѯ������Ϣ
				primaryKey = ((LeavebVO)source).getPk_leaveh();
			else 
				primaryKey = ((SuperVO)source).getPrimaryKey();
			if(StringUtils.isEmpty(primaryKey))
				continue;
			keyList.add(primaryKey);
		}
		// ���û���޸�̬�ģ�����Ҫ����
		if(CollectionUtils.isEmpty(keyList))
			return;
		// �Ƿ����뵥
		boolean isApp = !(simpleVO instanceof LeaveRegVO);
		Object[] dbVOs = getServiceTemplate().queryByPks(isApp?AggLeaveVO.class:LeaveRegVO.class, keyList.toArray(new String[0]));
		if(ArrayUtils.isEmpty(dbVOs))
			return;
		for(LeaveBalanceVO vo:balanceVOs){
			for(Object dbVO:dbVOs){
				if(isApp){
					LeavehVO hVO = ((AggLeaveVO)dbVO).getLeavehVO();
					if(!vo.getPk_psnorg().equals(hVO.getPk_psnorg())||
							!vo.getLeaveindex().equals(hVO.getLeaveindex())||
							!vo.getPk_timeitem().equals(hVO.getPk_timeitem())||
							!vo.getCuryear().equals(hVO.getLeaveyear()) ||
							(!vo.isYearDateSetPeriod() && !vo.getCurmonth().equals(hVO.getLeavemonth())))
						continue;
					// Ӧ�ò���Ҫ�����¼���һ��
//					BillProcessHelperAtServer.calLeaveLength(aggVO);
					//�Ӷ����п۳������뵥ʱ��
					vo.setFreezedayorhour(vo.getFreezedayorhour()==null?new UFDouble(0-hVO.getSumhour().doubleValue()):vo.getFreezedayorhour().sub(hVO.getSumhour()));
					continue;
				}
				LeaveRegVO regVO = (LeaveRegVO) dbVO;
				if(!vo.getPk_psnorg().equals(regVO.getPk_psnorg())||
						!vo.getLeaveindex().equals(regVO.getLeaveindex())||
						!vo.getPk_timeitem().equals(regVO.getPk_timeitem())||
						!vo.getCuryear().equals(regVO.getLeaveyear())||
						(!vo.isYearDateSetPeriod() && !vo.getCurmonth().equals(regVO.getLeavemonth())))
					continue;
//				BillProcessHelperAtServer.calLeaveLength(regVO);
				//�������п۳��������м��ϱ��Ǽǵ���ʱ��
				vo.setYidayorhour(vo.getYidayorhour()==null?new UFDouble(0-regVO.getLeaveHourValue()):vo.getYidayorhour().sub(regVO.getLeavehour()));
				vo.setRestdayorhour(vo.getRestdayorhour()==null?regVO.getLeavehour():vo.getRestdayorhour().add(regVO.getLeavehour()));
			}
		}
	}
	
	/**
	 * �����µļ��ڼ�¼������
	 * @param pk_org
	 * @param typeVO
	 * @param psnorgMap Map<pk_psnorg, leaveIndex>
	 * @param year
	 * @param month
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private LeaveBalanceVO[] createNewAndCalculate(String pk_org,LeaveTypeCopyVO typeVO,Map<String, Integer> psnorgMap,String year,String month) throws BusinessException{
		if(MapUtils.isEmpty(psnorgMap))
			return null;
		String[] pk_psnorgs = psnorgMap.keySet().toArray(new String[0]);
		PsnOrgVO[] psnOrgVOs = CommonUtils.toArray(PsnOrgVO.class, (Collection<PsnOrgVO>)MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(PsnOrgVO.class, pk_psnorgs, false));
		Map<String, PsnOrgVO> psnOrgVOMap = CommonUtils.toMap(PsnOrgVO.PK_PSNORG, psnOrgVOs);
		String pk_group = PubEnv.getPk_group();
//		boolean isHireSett = LeaveTypeCopyVO.LEAVESETPERIOD_DATE == typeVO.getLeavesetperiod().intValue();
		
		//BEGIN �ź�    ��65���������յ����ϵ�63   2018/8/28
		// ssx added on 2018-03-16
		// for changes of start date of company age
		boolean isHireSett = LeaveTypeCopyVO.LEAVESETPERIOD_DATE == typeVO
				.getLeavesetperiod().intValue()
				|| LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE == typeVO
						.getLeavesetperiod().intValue();
		//END �ź�    ��65���������յ����ϵ�63   2018/8/28
		
		LeaveBalanceMaintainImpl impl = new LeaveBalanceMaintainImpl();
		List<LeaveBalanceVO> results = new ArrayList<LeaveBalanceVO>();
		for(String pk_psnorg:pk_psnorgs) {
			PsnOrgVO psnorgVO = psnOrgVOMap.get(pk_psnorg);
			LeaveBalanceVO retVO = new LeaveBalanceVO();
			results.add(retVO);
			retVO.setPk_group(pk_group);
			retVO.setPk_org(pk_org);
			retVO.setPk_psndoc(psnorgVO.getPk_psndoc());
			retVO.setPk_psnorg(pk_psnorg);
			retVO.setPk_timeitem(typeVO.getPk_timeitem());
			retVO.setLeavesetperiod(typeVO.getLeavesetperiod());
			retVO.setCuryear(year);
			retVO.setCurmonth(month);
			retVO.setLeaveindex(psnorgMap.get(pk_psnorg));
			if(!isHireSett) //������ǰ���ְ���ڽ���
				continue;
			// ����ְ���ڽ���ʱ��Ҫ������ʼ���ںͽ�������
//			UFLiteralDate hireBeginDate = impl.getHireBeginDate(year, impl.getHireDate(psnorgVO));
//			UFLiteralDate hireEndDate = impl.getHireEndDate(year, impl.getHireDate(psnorgVO));
			
			//BEGIN �ź�    ��65���������յ����ϵ�63   2018/8/28
			UFLiteralDate hireBeginDate = null;
			UFLiteralDate hireEndDate = null;
			if (LeaveTypeCopyVO.LEAVESETPERIOD_DATE == typeVO
					.getLeavesetperiod().intValue()) {
				hireBeginDate = impl.getHireBeginDate(year,
						impl.getHireDate(psnorgVO));
				hireEndDate = impl.getHireEndDate(year,
						impl.getHireDate(psnorgVO));
			} else if (LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE == typeVO
					.getLeavesetperiod().intValue()) {
				hireBeginDate = impl.getHireBeginDate(year,
						impl.getHireStartDate(psnorgVO));
				hireEndDate = impl.getHireEndDate(year,
						impl.getHireStartDate(psnorgVO));
			}
			//END �ź�    ��65���������յ����ϵ�63   2018/8/28
			
			retVO.setHirebegindate(hireBeginDate);
			retVO.setPeriodbegindate(hireBeginDate);
			retVO.setHireenddate(hireEndDate);
			retVO.setPeriodenddate(hireEndDate);
			retVO.setPeriodextendenddate(hireEndDate.getDateAfter(typeVO.getExtendDaysCount()));
		}
		return results.toArray(new LeaveBalanceVO[0]);
	}
	
	@Override
	public String calAndSettlement_RequiresNew(String pk_org,
			boolean autoSettlement,boolean settlementtocurr) throws BusinessException {
		HROrgVO hrorgvo=((HROrgVO) NCLocator.getInstance().lookup(IOrgInfoQueryService.class).queryByPk(pk_org).getParentVO());
		String orgname=MultiLangUtil.getSuperVONameOfCurrentLang(hrorgvo, "name", null);
		StringBuffer sb = new StringBuffer();
		//���Ȳ�ѯ�ݼ����
		ITimeItemQueryService timeitemService = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		LeaveTypeCopyVO[] typeVOs = (LeaveTypeCopyVO[]) timeitemService.queryLeaveCopyTypesByOrg(pk_org);
		if(ArrayUtils.isEmpty(typeVOs))
			return sb.toString();
		//��ѯ��ǰʱ���Ӧ����֯����
		//�������ڣ���Ҫ������ʱ����HR��֯ʱ�����бȶ�
		TimeRuleVO timeRuleVO = null;
		try {
			timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		} catch (BusinessException e) {
			Logger.error(e.getMessage(), e);
			return e.getMessage() + "<br>";
		}
		TimeZone timeZone = timeRuleVO.getTimeZone();
		UFDateTime calculateTime = new UFDateTime();
		UFLiteralDate calDate = UFLiteralDate.getDate(calculateTime.toStdString(timeZone).substring(0, 10));
		int intYear = calDate.getYear();
		String year = Integer.toString(intYear);//��ǰ��Ȼ���
		String preYear = Integer.toString(intYear-1);//��һ��Ȼ���(���ڰ���ְ�ս���ĳ�������Ҫ������һ��Ⱥ͵�ǰ���)
		//ȡ�������ڶ�Ӧ�Ŀ����ڼ�
		PeriodVO periodVO = PeriodServiceFacade.queryByDate(pk_org, calDate);
		LeaveBalanceMaintainImpl maintainImpl = new LeaveBalanceMaintainImpl();
		//������calDate����������������ȡ��ڼ�
		Map<String, String[]> canSettlementTypeAndYearMonthMap = querySettlementYearMonth(typeVOs, calDate);
		for(LeaveTypeCopyVO typeVO:typeVOs){
			String typename=typeVO.getMultilangName();
			if(typeVO.getEnablestate().intValue()==IPubEnumConst.ENABLESTATE_DISABLE||typeVO.isLactation())//�������Ѿ�ͣ�ã������ǲ���٣��򲻴���
				continue;
			String salaryYear = null;
			String salaryMonth = null;
			int setPeriod = typeVO.getLeavesetperiod();
//			if(setPeriod==TimeItemCopyVO.LEAVESETPERIOD_DATE){//����ǰ���ְ�ս���
			
			//BEGIN �ź�    ��65���������յ����ϵ�63   2018/8/28
			// ssx added on 2018-03-16
			// for changes of start date of company age
			if (setPeriod == TimeItemCopyVO.LEAVESETPERIOD_DATE
					|| setPeriod == TimeItemCopyVO.LEAVESETPERIOD_STARTDATE) {// ����ǰ���ְ�ս���
			//END �ź�    ��65���������յ����ϵ�63   2018/8/28
			
				LeaveBalanceVO[] balanceVOs = maintainImpl.queryByCondition(pk_org, typeVO, preYear, null, null);
				if(!ArrayUtils.isEmpty(balanceVOs)){
					balanceVOs = maintainImpl.calculate(pk_org, typeVO, preYear, null, balanceVOs, calculateTime,true);
					if(autoSettlement){
						if(TimeItemCopyVO.LEAVESETTLEMENT_MONEY==typeVO.getLeavesettlement()){//תн�ʵ���Ҫ���������㵽�ĸ��ڼ��Է���н��ȡ��
							if(settlementtocurr){//���㵽��ǰ
								salaryYear = periodVO.getYear();
								salaryMonth = periodVO.getMonth();
							}else{//
								PeriodVO[] periodvos = PeriodServiceFacade.queryByYear(pk_org, preYear);
								PeriodVO dataPeriodVO = periodvos[periodvos.length-1];
								salaryYear = dataPeriodVO.getYear();
								salaryMonth = dataPeriodVO.getMonth();
							}
						}
						maintainImpl.secondSettlement4HireDate(pk_org, typeVO, preYear, balanceVOs, calDate, false, false, false,true, salaryMonth, salaryMonth);
					}
				}
				balanceVOs = maintainImpl.queryByCondition(pk_org, typeVO, year, null, null);
				if(ArrayUtils.isEmpty(balanceVOs))
					continue;
				balanceVOs = maintainImpl.calculate(pk_org, typeVO, year, null, balanceVOs, calculateTime,true);
				//������ְ�ս�������ͣ����겻�����п��Խ����
				continue;
			}
			if(periodVO==null)
				continue;
			String month = setPeriod==TimeItemCopyVO.LEAVESETPERIOD_MONTH?periodVO.getTimemonth():null;
			LeaveBalanceVO[] balanceVOs = maintainImpl.queryByCondition(pk_org, typeVO, periodVO.getTimeyear(), month, null);
			if(ArrayUtils.isEmpty(balanceVOs)){
				sb.append((ResHelper.getString("6017basedoc","06017basedoc1851"/*@ress"{0}�µ�{1}���û���ܷ��ϼ��������������Ա!"*/,
						orgname,typename))+"<br>");
				continue;
			}
			balanceVOs = maintainImpl.calculate(pk_org, typeVO, periodVO.getTimeyear(), month, balanceVOs, calculateTime,true);
			if(!autoSettlement)
				continue;
			//��������Ƿ������calDate���н���
			String[] settlementYearMonth = canSettlementTypeAndYearMonthMap.get(typeVO.getPk_timeitem());
			if(ArrayUtils.isEmpty(settlementYearMonth)){
				sb.append((ResHelper.getString("6017basedoc","06017basedoc1852"/*@ress"{0}�µ�{1}���Ľ������ڲ����Ͻ��������������!"*/,
						orgname,typename))+"<br>");
				continue;
			}
			//�����ĳ�����/�ڼ�Ŀ�����calDate���н����ˣ������֮
			String settlementYear = settlementYearMonth[0];
			String settlementMonth = settlementYearMonth.length>1?settlementYearMonth[1]:null;
			balanceVOs = maintainImpl.queryByCondition(pk_org, typeVO, settlementYear, settlementMonth, null);
			
			if(TimeItemCopyVO.LEAVESETTLEMENT_MONEY==typeVO.getLeavesettlement()){//תн�ʵ���Ҫ���������㵽�ĸ��ڼ��Է���н��ȡ��
				if(settlementtocurr){//���㵽��ǰ
					salaryYear = periodVO.getYear();
					salaryMonth = periodVO.getMonth();
				}else{
					salaryYear = settlementYear;
					salaryMonth = settlementMonth;
				}
			}
			
			maintainImpl.secondSettlement4YearMonth(pk_org, typeVO,
					settlementYear, settlementMonth, balanceVOs, calDate, false, false, false,true, salaryYear, salaryMonth);
		}
		return sb.toString();
	}
	/**
	 * ��ѯ������date���н�������з���ְ�����key��pk_timeitem,value�ǿ��Խ��н�������/�ڼ�
	 * @param typeVOs
	 * @param date
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, String[]> querySettlementYearMonth(LeaveTypeCopyVO[] typeVOs,UFLiteralDate date) throws BusinessException{
		Map<String, String[]> retMap = new HashMap<String, String[]>();
		if(ArrayUtils.isEmpty(typeVOs))
			return retMap;
		for(LeaveTypeCopyVO typeVO:typeVOs){
			if(typeVO.isLactation())
				continue;
			int setPeriod = typeVO.getLeavesetperiod();
//			if(setPeriod==TimeItemCopyVO.LEAVESETPERIOD_DATE)//����ǰ���ְ�ս������ô�������Ϊ��ְ�ս�������ͣ�ÿ�춼�п������˽���
			
			//BEGIN �ź�    ��65���������յ����ϵ�63   2018/8/28
			if (setPeriod == TimeItemCopyVO.LEAVESETPERIOD_DATE// ����ǰ���ְ�ս������ô�������Ϊ��ְ�ս�������ͣ�ÿ�춼�п������˽���
			// ssx added on 2018-03-16
			// for changes of start date of company age
			|| setPeriod == TimeItemCopyVO.LEAVESETPERIOD_STARTDATE)
			//END �ź�    ��65���������յ����ϵ�63   2018/8/28
			
				continue;
			int effectiveExtendCount = typeVO.getExtendDaysCount();//��Ч���ӳ�����
			//������date�ս��н�������/�ڼ�����һ��
			UFLiteralDate periodEndDate = date.getDateBefore(effectiveExtendCount+1);
			PeriodVO periodVO = PeriodServiceFacade.queryByDate(typeVO.getPk_org(), periodEndDate);
			if(periodVO==null)
				continue;
			if(setPeriod==TimeItemCopyVO.LEAVESETPERIOD_MONTH){
				if(periodVO.getEnddate().equals(periodEndDate)){//����ǰ��ڼ���㣬�Ҵ��ڼ�����һ��պõ���periodEndDate�����ʾ��������һ�����
					retMap.put(typeVO.getPk_timeitem(), new String[]{periodVO.getTimeyear(),periodVO.getTimemonth()});
				}
				continue;
			}
			PeriodVO[] yearPeriodVOs = PeriodServiceFacade.queryByYear(typeVO.getPk_org(), periodVO.getTimeyear());
			if(yearPeriodVOs[yearPeriodVOs.length-1].getEnddate().equals(periodEndDate)){//����ǰ�����㣬�Ҵ�������һ��պõ���periodEndDate�����ʾ��������һ�����
				retMap.put(typeVO.getPk_timeitem(), new String[]{periodVO.getTimeyear()});
			}
		}
		return retMap;
	}

	@Override
	public void processBeforeRestOvertime(String pk_org,
			Map<String, UFDouble> hourMap, String year, String month,
			Boolean isToRest) throws BusinessException {
		if(MapUtils.isEmpty(hourMap))
			return;
		String outErrMsg = StringUtils.isEmpty(month)?ResHelper.getString("6017leave","06017leave0257")/*@res "ת����ȳ������ڵ���ʱ�䷶Χ��"*/ :
				ResHelper.getString("6017leave","06017leave0235")/*@res "ת���ڼ䳬�����ڵ���ʱ�䷶Χ��"*/;
		String[] pk_psnorgs = hourMap.keySet().toArray(new String[0]);
		LeaveTypeCopyVO typeVO = (LeaveTypeCopyVO) NCLocator.getInstance().lookup(ITimeItemQueryService.class).queryCopyTypesByDefPK(pk_org, LeaveBalanceVO.TIMETOLEAVETYPE, TimeItemCopyVO.LEAVE_TYPE);
		Map<String, LeaveBalanceVO> balanceMap = queryAndCalLeaveBalanceVO(pk_org, typeVO, pk_psnorgs, year, month, null);
		if(MapUtils.isEmpty(balanceMap))
			throw new BusinessException(outErrMsg);
		TimeRuleVO timeRule = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(pk_org);
		boolean isHourType = TimeItemCopyVO.TIMEITEMUNIT_HOUR==typeVO.getTimeitemunit().intValue();
		List<LeaveBalanceVO> resultList = new ArrayList<LeaveBalanceVO>();
		for(String pk_psnorg:pk_psnorgs) {
			UFDouble torestHour = hourMap.get(pk_psnorg);
			// ʱ��Ϊ0������
			if(torestHour==null || torestHour.equals(UFDouble.ZERO_DBL))
				continue;
			torestHour = isHourType ? torestHour : torestHour.div(timeRule.getDaytohour2());
			String key = pk_psnorg+typeVO.getPk_timeitem()+year+(StringUtils.isEmpty(month)?"":month);
			LeaveBalanceVO vo = balanceMap.get(key);
			if(vo==null)
				throw new BusinessException(outErrMsg);
			if(vo.isSettlement()||vo.isUse())
				throw new BusinessException(ResHelper.getString("6017leave","06017leave0236")/*@res "�ڼ�Ľ��������ѽ�����ѱ�н��ʹ��,���ܲ�����"*/);
			// ���뵽�������
			resultList.add(vo);
			// ת����
			if(isToRest) {
				vo.setCurdayorhour(vo.getCurdayorhour().add(torestHour));
				vo.setRealdayorhour(vo.getRealdayorhour().add(torestHour));
				vo.setRestdayorhour(vo.getRestdayorhour().add(torestHour));
				continue;
			}
			// ��ת����
			if(vo.getRestdayorhour().compareTo(torestHour)<0)
				throw new BusinessException(ResHelper.getString("6017leave","06017leave0237")/*@res "ת�����ڼ�Ľ���ʱ��С�ڷ�ת����ʱ����"*/);
			vo.setCurdayorhour(vo.getCurdayorhour().sub(torestHour));
			vo.setRealdayorhour(vo.getRealdayorhour().sub(torestHour));
			vo.setRestdayorhour(vo.getRestdayorhour().sub(torestHour));
		}
		
		//��������
		getServiceTemplate().batchUpdate(resultList.toArray(new LeaveBalanceVO[0]), true);
	}

	@Override
	public Map<String, List<LeaveBalanceVO>> queryAndCalLeaveBalanceVOBatchForPreHoliday(String pk_org, Map<String, LeaveTypeCopyVO[]> dependMap,
			Map<String, LeaveTypeCopyVO> leaveTypeVOMap, TimeRuleVO timeRuleVO, LeaveCommonVO... leaveCommonVOs)
			throws BusinessException {
		if(ArrayUtils.isEmpty(leaveCommonVOs))
			return null;
		PeriodVO[] periodVOs = CommonUtils.retrieveByClause(PeriodVO.class, " pk_org = '"+timeRuleVO.getPk_org()+"' ");
		// ���ݼٵ����ݼ������ȡ��ڼ����
		Map<String, List<LeaveCommonVO>> keyMap = new HashMap<String, List<LeaveCommonVO>>();
		for (LeaveCommonVO vo : leaveCommonVOs) {
			String primaryKey = vo.getPk_leavetype()+vo.getYearmonth();
			List<LeaveCommonVO> voList = keyMap.get(primaryKey);
			if (CollectionUtils.isEmpty(voList)){
				voList = new ArrayList<LeaveCommonVO>();
				keyMap.put(primaryKey, voList);
			}
			voList.add(vo);
		}
		// ��������������������
		Map<String, List<LeaveBalanceVO>> result = new HashMap<String, List<LeaveBalanceVO>>();
		for(String key:keyMap.keySet()) {
			LeaveCommonVO[] vos = keyMap.get(key).toArray(new LeaveCommonVO[0]);
			LeaveTypeCopyVO[] dependVOs = dependMap.get(vos[0].getPk_leavetype());//�û�¼����������ǰ�����
			LeaveTypeCopyVO typeCopyVO = leaveTypeVOMap.get(vos[0].getPk_leavetype());
			dependVOs = (LeaveTypeCopyVO[]) ArrayUtils.add(dependVOs, typeCopyVO);//��ǰ�������û�¼�����ϲ���һ�����飬��������ʹ��
			int setPeriod = typeCopyVO.getLeavesetperiod().intValue();//�����������ͣ���/�ڼ�/��ְ��
			Map<String, List<LeaveBalanceVO>> balanceMap = null;
			//����ǰ���/��ְ�ս��㣬��ô�Ƚϼ򵥣���Ϊ��ǰ�üٵĽ�������Ҳ�϶�����/��ְ��
//			if(setPeriod == TimeItemCopyVO.LEAVESETPERIOD_YEAR||setPeriod==TimeItemCopyVO.LEAVESETPERIOD_DATE)
			
				//BEGIN �ź�    ��65���������յ����ϵ�63   2018/8/28
				if (setPeriod == TimeItemCopyVO.LEAVESETPERIOD_YEAR
						|| setPeriod == TimeItemCopyVO.LEAVESETPERIOD_DATE
						// ssx added on 2018-03-16
						// for changes of start date of company age
						|| setPeriod == TimeItemCopyVO.LEAVESETPERIOD_STARTDATE
				//
				)
				//END �ź�    ��65���������յ����ϵ�63   2018/8/28
			
				balanceMap = queryAndCalLeaveBalanceVOForPreHolidayYearDate(timeRuleVO, vos[0].getLeaveyear(), periodVOs, typeCopyVO, dependVOs, vos);
			//�ߵ������ʾ�û�¼����ݼ�����ǰ��ڼ����ģ������ڼ��������ǰ�ü��п����ǰ���ģ�Ҳ�п����ǰ��ڼ�ģ��涨�����а���ģ�������ڰ��ڼ��֮ǰ��
			else
				balanceMap = queryAndCalLeaveBalanceVOForPreHolidayPeriod(timeRuleVO, vos[0].getLeaveyear(), vos[0].getLeavemonth(), periodVOs, typeCopyVO, dependVOs, vos);
			if(MapUtils.isEmpty(balanceMap))
				continue;
			// ����ѯ����ϲ������ؽ����
			for(String pk_psnorg:balanceMap.keySet()){
				List<LeaveBalanceVO> balanceList = result.get(pk_psnorg);
				if(balanceList == null){
					balanceList = balanceMap.get(pk_psnorg);
					result.put(pk_psnorg, balanceList);
					continue;
				}
				balanceList.addAll(balanceMap.get(pk_psnorg));
			}
		}
		return result;
	}
	
	/**
	 * ��ѯ����Ҫ������ǰ�ü�+�û���д�����Ľ�����Ϣ���������û���д������ǰ���/��ְ�ս���
	 * @param timeRuleVO
	 * @param year
	 * @param typeCopyVO
	 * @param dependVOsAddSelf
	 * @param leaveCommonVOs
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, List<LeaveBalanceVO>> queryAndCalLeaveBalanceVOForPreHolidayYearDate(TimeRuleVO timeRuleVO, String year, PeriodVO[] periodVOs,
			LeaveTypeCopyVO typeCopyVO,LeaveTypeCopyVO[] dependVOsAddSelf, LeaveCommonVO[] leaveCommonVOs) throws BusinessException{
		PsnOrgVO[] psnOrgVOs = CommonUtils.toArray(PsnOrgVO.class, (Collection<PsnOrgVO>)MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(PsnOrgVO.class, StringPiecer.getStrArrayDistinct(leaveCommonVOs, LeaveCommonVO.PK_PSNORG), false));
		Map<String, PsnOrgVO> psnOrgVOMap = CommonUtils.toMap(PsnOrgVO.PK_PSNORG, psnOrgVOs);
		Map<String, LeaveTypeCopyVO> typeMap = CommonUtils.toMap(LeaveTypeCopyVO.PK_TIMEITEM, dependVOsAddSelf);
		UFLiteralDate hireDate = null;//��ְ��
		// ����Map<pk_timeitem, Map<year, LeaveCommonVO[]>>
		Map<String, Map<String, List<LeaveCommonVO>>> voMap = new HashMap<String, Map<String, List<LeaveCommonVO>>>();
		for(LeaveCommonVO leaveCommonVO:leaveCommonVOs){
			// ������ְ���ڣ���̫Ӱ��Ч�ʣ�����ÿ����ȡһ�£�����Ҳ�޷���
//			hireDate = new LeaveBalanceMaintainImpl().getHireDate(psnOrgVOMap.get(leaveCommonVO.getPk_psnorg()));
			
			//BEGIN �ź�    ��65���������յ����ϵ�63   2018/8/28
			// ssx added on 2018-03-16
			// for changes of start date of company age
			if (typeCopyVO.getLeavesetperiod() == LeaveTypeCopyVO.LEAVESETPERIOD_STARTDATE) {
				hireDate = new LeaveBalanceMaintainImpl()
						.getHireStartDate(psnOrgVOMap.get(leaveCommonVO
								.getPk_psnorg()));
			} else {
				// ������ְ���ڣ���̫Ӱ��Ч�ʣ�����ÿ����ȡһ�£�����Ҳ�޷���
				hireDate = new LeaveBalanceMaintainImpl()
						.getHireDate(psnOrgVOMap.get(leaveCommonVO
								.getPk_psnorg()));
			}
			//END �ź�    ��65���������յ����ϵ�63   2018/8/28
			
			//�洢��Ч���ӳ��������ཻ����������map��<��Ч���ӳ����������ݼ�ʱ���н����Ŀ������/��ְ��>
			for(LeaveTypeCopyVO typeVO:dependVOsAddSelf){
				String[] crossYears = null;//���ݼ����ڷ�Χ�н��������п���/��ְ��ȣ���Ҫ������Ч���ӳ�
				// �ݼ����Ϊ�������
				if(typeVO.getLeavesetperiod() == TimeItemCopyVO.LEAVESETPERIOD_YEAR)  //typeCopyVO.getLeavesetperiod()==TimeItemCopyVO.LEAVESETPERIOD_YEAR
					crossYears = PreHolidayLeaveBalanceUtils.queryRelatedYearsWithExtendCount(typeVO.getExtendDaysCount(),periodVOs,leaveCommonVO.getLeavebegindate(), leaveCommonVO.getLeaveenddate());
				else
					crossYears = PreHolidayLeaveBalanceUtils.queryRelatedHireYearsWithExtendCount(typeVO.getExtendDaysCount(), leaveCommonVO.getPk_psndoc(),hireDate,leaveCommonVO.getLeavebegindate(), leaveCommonVO.getLeaveenddate());
				if(ArrayUtils.isEmpty(crossYears))
					continue;
				Map<String, List<LeaveCommonVO>> yearMap = voMap.get(typeVO.getPk_timeitem());
				if(MapUtils.isEmpty(yearMap)){
					yearMap = new HashMap<String, List<LeaveCommonVO>>();
					voMap.put(typeVO.getPk_timeitem(), yearMap);
				}
				for(String curYear:crossYears){
					List<LeaveCommonVO> voList = yearMap.get(curYear);
					if(CollectionUtils.isEmpty(voList)){
						voList = new ArrayList<LeaveCommonVO>();
						yearMap.put(curYear, voList);
					}
					voList.add(leaveCommonVO);
				}
			}
		}
		if(MapUtils.isEmpty(voMap))
			return null;
		// �����������������
		Map<String, List<LeaveBalanceVO>> resultMap = new HashMap<String, List<LeaveBalanceVO>>();
		for(String pk_timeitem:voMap.keySet()) {
			Map<String, List<LeaveCommonVO>> yearMap = voMap.get(pk_timeitem);
			if(MapUtils.isEmpty(yearMap))
				continue;
			for(String curYear:yearMap.keySet()){
				LeaveCommonVO[] commonVOs = yearMap.get(curYear).toArray(new LeaveCommonVO[0]);
				Map<String, LeaveBalanceVO> balanceMap = queryAndCalLeaveBalanceVO(typeCopyVO.getPk_org(), typeMap.get(pk_timeitem), StringPiecer.getStrArrayDistinct(commonVOs, LeaveCommonVO.PK_PSNORG), curYear, null, commonVOs);
				if(MapUtils.isEmpty(balanceMap))
					continue;
				for(String key:balanceMap.keySet()){
					LeaveBalanceVO vo = balanceMap.get(key);
					String pk_psnorg = vo.getPk_psnorg();
					List<LeaveBalanceVO> balanceList = resultMap.get(pk_psnorg);
					if(CollectionUtils.isEmpty(balanceList)){
						balanceList = new ArrayList<LeaveBalanceVO>();
						resultMap.put(pk_psnorg, balanceList);
					}
					balanceList.add(vo);
				}
			}
		}
		if(MapUtils.isEmpty(resultMap))
			return null;
		//����Ҫ����ЩLeaveBalanceVO[]��������
		//����Ĺ����ǣ������ڽ�������ΪY����order by ��ȣ����
		//�����ڽ�������ΪN�����û�δ������ȣ��������������һ��
		//���û���������ȣ�����Ҫ���û��������ȡ����������leavebalancevo��¼��ǰ�ᣬ�ᵽ��һ�����������leavebalance��¼֮ǰ
		for(String pk_psnorg:resultMap.keySet()){
			List<LeaveBalanceVO> balanceList = resultMap.get(pk_psnorg);
			if(CollectionUtils.isEmpty(balanceList))
				continue;
			LeaveBalanceVO[] retArray = balanceList.toArray(new LeaveBalanceVO[0]);
			PreHolidayLeaveBalanceUtils.sortByYearDate(retArray, timeRuleVO.isPreHolidayFirst(), typeCopyVO.getPk_timeitem(), year, dependVOsAddSelf);
			resultMap.put(pk_psnorg, Arrays.asList(retArray));
		}
		return resultMap;
	}
	
	/**
	 * ��ѯ����Ҫ������ǰ�ü�+�û���д�����Ľ�����Ϣ���������û���д������ǰ��ڼ����
	 * ���ڼ����������ǰ�ü��п����ǰ������ģ���������ǰ�üٵ�ǰ�棬�����ڰ��ڼ�����ǰ�üٺ���
	 * @param timeRuleVO
	 * @param year
	 * @param month
	 * @param typeCopyVO
	 * @param dependVOsAddSelf
	 * @param leaveCommonVOs
	 * @return
	 * @throws BusinessException
	 */
	private Map<String, List<LeaveBalanceVO>> queryAndCalLeaveBalanceVOForPreHolidayPeriod(TimeRuleVO timeRuleVO, String year,String month, PeriodVO[] periodVOs,
			LeaveTypeCopyVO typeCopyVO,LeaveTypeCopyVO[] dependVOsAddSelf,LeaveCommonVO[] leaveCommonVOs) throws BusinessException{
		Map<String, LeaveTypeCopyVO> typeMap = CommonUtils.toMap(LeaveTypeCopyVO.PK_TIMEITEM, dependVOsAddSelf);
		// ����Map<pk_timeitem, Map<year-month, LeaveCommonVO[]>>
		Map<String, Map<String, List<LeaveCommonVO>>> voMap = new HashMap<String, Map<String, List<LeaveCommonVO>>>();
		for(LeaveCommonVO leaveCommonVO:leaveCommonVOs){
			for(LeaveTypeCopyVO typeVO:dependVOsAddSelf){
				if(typeVO.getLeavesetperiod() == TimeItemCopyVO.LEAVESETPERIOD_YEAR){
					String[] crossYears = PreHolidayLeaveBalanceUtils.queryRelatedYearsWithExtendCount(typeVO.getExtendDaysCount(),periodVOs,leaveCommonVO.getLeavebegindate(), leaveCommonVO.getLeaveenddate());
					if(ArrayUtils.isEmpty(crossYears))
						continue;
					Map<String, List<LeaveCommonVO>> yearMap = voMap.get(typeVO.getPk_timeitem());
					if(MapUtils.isEmpty(yearMap)){
						yearMap = new HashMap<String, List<LeaveCommonVO>>();
						voMap.put(typeVO.getPk_timeitem(), yearMap);
					}
					for(String curYear:crossYears){
						List<LeaveCommonVO> voList = yearMap.get(curYear);
						if(CollectionUtils.isEmpty(voList)){
							voList = new ArrayList<LeaveCommonVO>();
							yearMap.put(curYear, voList);
						}
						voList.add(leaveCommonVO);
					}
					continue;
				}
				PeriodVO[] crossPeriodVOs = PreHolidayLeaveBalanceUtils.queryRelatedPeriodsWithExtendCount(typeVO.getExtendDaysCount(),periodVOs,leaveCommonVO.getLeavebegindate(), leaveCommonVO.getLeaveenddate());
				if(ArrayUtils.isEmpty(crossPeriodVOs))
					continue;
				Map<String, List<LeaveCommonVO>> yearMap = voMap.get(typeVO.getPk_timeitem());
				if(MapUtils.isEmpty(yearMap)){
					yearMap = new HashMap<String, List<LeaveCommonVO>>();
					voMap.put(typeVO.getPk_timeitem(), yearMap);
				}
				for(PeriodVO curPeriod:crossPeriodVOs){
					String key = curPeriod.getTimeyear()+"-"+curPeriod.getTimemonth();
					List<LeaveCommonVO> voList = yearMap.get(key);
					if(CollectionUtils.isEmpty(voList)){
						voList = new ArrayList<LeaveCommonVO>();
						yearMap.put(key, voList);
					}
					voList.add(leaveCommonVO);
				}
			}
		}
		if(MapUtils.isEmpty(voMap))
			return null;
		// �����������������
		Map<String, List<LeaveBalanceVO>> resultMap = new HashMap<String, List<LeaveBalanceVO>>();
		for(String pk_timeitem:voMap.keySet()) {
			Map<String, List<LeaveCommonVO>> yearMap = voMap.get(pk_timeitem);
			if(MapUtils.isEmpty(yearMap))
				continue;
			LeaveTypeCopyVO curType = typeMap.get(pk_timeitem);
			for(String curYear:yearMap.keySet()){
				LeaveCommonVO[] commonVOs = yearMap.get(curYear).toArray(new LeaveCommonVO[0]);
				String thisYear = null;
				String thisMonth = null;
				if(curType.getLeavesetperiod() == TimeItemCopyVO.LEAVESETPERIOD_YEAR)
					thisYear = curYear;
				else {
					String[] arr = curYear.split("-");
					thisYear = arr[0];
					thisMonth = arr[1];
				}
				Map<String, LeaveBalanceVO> balanceMap = queryAndCalLeaveBalanceVO(typeCopyVO.getPk_org(), typeMap.get(pk_timeitem), StringPiecer.getStrArrayDistinct(commonVOs, LeaveCommonVO.PK_PSNORG), thisYear, thisMonth, commonVOs);
				if(MapUtils.isEmpty(balanceMap))
					continue;
				for(String key:balanceMap.keySet()){
					LeaveBalanceVO vo = balanceMap.get(key);
					String pk_psnorg = vo.getPk_psnorg();
					List<LeaveBalanceVO> balanceList = resultMap.get(pk_psnorg);
					if(CollectionUtils.isEmpty(balanceList)){
						balanceList = new ArrayList<LeaveBalanceVO>();
						resultMap.put(pk_psnorg, balanceList);
					}
					balanceList.add(vo);
				}
			}
		}
		if(MapUtils.isEmpty(resultMap))
			return null;
		//����Ҫ����ЩLeaveBalanceVO[]��������
		//����Ĺ����ǣ������ڽ�������ΪY����order by ��ȣ����
		//�����ڽ�������ΪN�����û�δ������ȣ��������������һ��
		//���û���������ȣ�����Ҫ���û��������ȡ����������leavebalancevo��¼��ǰ�ᣬ�ᵽ��һ�����������leavebalance��¼֮ǰ
		for(String pk_psnorg:resultMap.keySet()){
			List<LeaveBalanceVO> balanceList = resultMap.get(pk_psnorg);
			if(CollectionUtils.isEmpty(balanceList))
				continue;
			LeaveBalanceVO[] retArray = balanceList.toArray(new LeaveBalanceVO[0]);
			PreHolidayLeaveBalanceUtils.sortByPeriod(retArray, timeRuleVO.isPreHolidayFirst(), typeCopyVO.getPk_timeitem(), year,month, dependVOsAddSelf);
			resultMap.put(pk_psnorg, Arrays.asList(retArray));
		}
		return resultMap;
	}
}