package nc.impl.ta.leave;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import nc.bs.bd.cache.CacheProxy;
import nc.bs.bd.pub.distribution.util.BDDistTokenUtil;
import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.InvocationInfo;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.execute.Executor;
import nc.bs.logging.Logger;
import nc.bs.uif2.validation.DefaultValidationService;
import nc.hr.frame.persistence.HrBatchService;
import nc.hr.utils.FromWhereSQLUtils;
import nc.hr.utils.ResHelper;
import nc.hr.utils.StringPiecer;
import nc.impl.am.common.InSqlManager;
import nc.impl.ta.algorithm.BillValidatorAtServer;
import nc.impl.ta.leave.validator.LeaveRegDeleteValidator;
import nc.impl.ta.leave.validator.LeaveRegValidatorService;
import nc.impl.ta.timebill.BillMethods;
import nc.itf.ta.ILeaveBalanceManageService;
import nc.itf.ta.ILeaveRegisterInfoDisplayer;
import nc.itf.ta.ILeaveRegisterManageMaintain;
import nc.itf.ta.ILeaveRegisterQueryMaintain;
import nc.itf.ta.ITBMPsndocQueryService;
import nc.itf.ta.ITimeItemQueryService;
import nc.itf.ta.ITimeRuleQueryService;
import nc.itf.ta.PeriodServiceFacade;
import nc.itf.ta.algorithm.DateScopeUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.itf.ta.algorithm.ITimeScopeWithBillInfo;
import nc.jdbc.framework.generator.SequenceGenerator;
import nc.jdbc.framework.processor.BeanListProcessor;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.ui.querytemplate.querytree.FromWhereSQL;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.leave.LeaveCheckResult;
import nc.vo.ta.leave.LeaveRegVO;
import nc.vo.ta.leave.SplitBillResult;
import nc.vo.ta.leavebalance.LeaveBalanceVO;
import nc.vo.ta.log.TaBusilogUtil;
import nc.vo.ta.period.PeriodVO;
import nc.vo.ta.psndoc.TBMPsndocVO;
import nc.vo.ta.pub.CommonMethods;
import nc.vo.ta.pub.IMetaDataIDConst;
import nc.vo.ta.pub.PubPermissionUtils;
import nc.vo.ta.pub.TaBillRegQueryParams;
import nc.vo.ta.pub.TaNormalQueryUtils;
import nc.vo.ta.timeitem.LeaveTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timerule.TimeRuleVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class LeaveRegisterMaintainImpl implements ILeaveRegisterQueryMaintain,
		ILeaveRegisterManageMaintain{

	private HrBatchService serviceTemplate;
	
	public HrBatchService getServiceTemplate() {
		if(serviceTemplate == null) {
			serviceTemplate = new HrBatchService(IMetaDataIDConst.LEAVE);
		}
		return serviceTemplate;
	}

	@SuppressWarnings("unchecked")
	@Override
	public LeaveRegVO[] queryByCond(LoginContext context, FromWhereSQL fromWhereSQL, Object etraConds)
			throws BusinessException {
		//ȡ������
		String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, LeaveRegVO.getDefaultTableName());
		//ƴ��֯��sql
		String etraCondsNext = "("+alias + "." + LeaveRegVO.PK_ORG +
							(context.getPk_org()==null?" is null": (" = '" + context.getPk_org() + "' "))
							+ " or " + alias + "." + LeaveRegVO.PK_ADMINORG +
							(context.getPk_org()==null?" is null": (" = '" + context.getPk_org() + "' "))+")";
		//����Ȩ��sql��ʹ�ù�����¼��Դʵ���µġ�ʱ�����ͨ�����á�Ԫ���ݲ���
		fromWhereSQL = PubPermissionUtils.addPubQueryPermission2FromWhereSQL
		(fromWhereSQL, LeaveRegVO.getDefaultTableName(), LeaveRegVO.getDefaultTableName(), LeaveRegVO.PK_PSNJOB);

		//���ڼ��ѯ
		String periodSql = TaNormalQueryUtils.getPeriodSql(context, LeaveRegVO.class, alias,(TaBillRegQueryParams)etraConds);
		if(periodSql != null) {
			etraCondsNext += " and " + periodSql;
		}

		String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, "tbm_leavereg", etraCondsNext, null);
		Collection<LeaveRegVO> aggvos = (Collection<LeaveRegVO>)new BaseDAO().executeQuery(sql, new BeanListProcessor(
				LeaveRegVO.class));
		if(CollectionUtils.isEmpty(aggvos))
			return null;
		return aggvos.toArray(new LeaveRegVO[0]);
	}

	@Override
	public LeaveRegVO queryByPk(String pk) throws BusinessException {
		return getServiceTemplate().queryByPk(LeaveRegVO.class, pk);
	}

	@Override
	public void deleteArrayData(LeaveRegVO[] vos) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return;
//		for(LeaveRegVO vo:vos){
//			PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(), vo.getBegindate(), vo.getEnddate());
//		}
		
		IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(vos);
		PeriodServiceFacade.checkMonth(vos[0].getPk_org(), StringPiecer.getStrArray(vos, LeaveRegVO.PK_PSNDOC), maxDateScope.getBegindate(), maxDateScope.getEnddate());
		
		DefaultValidationService vService = new DefaultValidationService();
        vService.addValidator(new LeaveRegDeleteValidator());
		vService.validate(vos);
		getServiceTemplate().delete(vos);
		ILeaveBalanceManageService balanceService = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class);
		balanceService.queryAndCalLeaveBalanceVO(vos[0].getPk_org(), (Object[])vos);
		//ҵ����־
		TaBusilogUtil.writeLeaveRegDeleteBusiLog(vos);
		CacheProxy.fireDataDeletedBatch(LeaveRegVO.getDefaultTableName(), StringPiecer.getStrArray(vos, LeaveRegVO.PK_LEAVEREG));
	}

	@Override
	public void deleteData(LeaveRegVO vo) throws BusinessException {
		if(vo == null)
			return;
		deleteArrayData(new LeaveRegVO[]{vo});
	}

	@Override
	public LeaveRegVO[] insertArrayData(LeaveRegVO[] vos) throws BusinessException {
		BillMethods.processBeginEndDatePkJobOrgTimeZone(vos);
		return insertData(vos, true);
	}
	
	@Override
	public LeaveRegVO[] insertData(LeaveRegVO[] vos,boolean needCalAndValidate) throws BusinessException {
		if(ArrayUtils.isEmpty(vos))
			return null;
		if(needCalAndValidate){
			new LeaveRegValidatorService().validate(vos);
//			check(vos[0].getPk_org(), vos);//����ǰ����ʾ��У�����Ѿ�У����ˣ�Ϊ��Ч�ʴ˴�����У��
			BillValidatorAtServer.checkCalendarCompleteForLeaveAndShutdown(vos[0].getPk_org(),vos);
		}
		
		//��ѯ���ڵ���
		IDateScope maxDateScope = DateScopeUtils.getMaxRangeDateScope(vos);
		// �п�����A��֯���ݼټ�¼��Ȼ����䵽B��֯��������Ҫ����֯��ѯ
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		Map<String, List<TBMPsndocVO>> psndocMap = psndocService.queryTBMPsndocMapByPsndocs(null, StringPiecer.getStrArrayDistinct(vos, LeaveRegVO.PK_PSNDOC), maxDateScope.getBegindate(), maxDateScope.getEnddate(), false);
		if(MapUtils.isEmpty(psndocMap))
			return null;
		for(LeaveRegVO vo : vos) {
			if(vo.getLeaveindex()==null)
				vo.setLeaveindex(1);
			if(vo.getIsleaveoff()==null)
				vo.setIsleaveoff(UFBoolean.FALSE);
			// ���ݽ��������ҹ����Ŀ��ڵ���
			TBMPsndocVO psndocVO = TBMPsndocVO.findIntersectionVO(psndocMap.get(vo.getPk_psndoc()), vo.getEnddate().toStdString());
			if(psndocVO == null)
				throw new BusinessException(ResHelper.getString("6017psndoc","06017psndoc0131"/*@res "����ʧ��,��Ա{0}�Ŀ��ڵ���ʱ�䷶Χ��������ʱ���"*/,psndocService.getPsnName(vo.getPk_psndoc())));
			// ���ù�����֯
			vo.setPk_adminorg(psndocVO.getPk_adminorg());
			
			//MOD ��������ʱ��Ϊ��Ȼʱ�� by James
			vo.setApprove_time(new UFDateTime());
		}
		//����ͨ����ҪУ��������ݼ������Ƿ��Ѿ�������
		LeaveRegVO[] retvos = getServiceTemplate().insert(vos);
		afterInsert(vos, retvos);
		return retvos;
	}

	private void afterInsert(final LeaveRegVO[] vos, final LeaveRegVO[] retvos)
			throws BusinessException {
		
		final InvocationInfo invocationInfo = BDDistTokenUtil.getInvocationInfo();
		new Executor(new Runnable() {
			@Override
			public void run() {
				BDDistTokenUtil.setInvocationInfo(invocationInfo);
				try {
					NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(vos[0].getPk_org(), (Object[])vos);
					//ҵ����־
					TaBusilogUtil.writeLeaveRegAddBusiLog(retvos);
					CacheProxy.fireDataInserted(LeaveRegVO.getDefaultTableName());
				} catch (BusinessException e) {
					Logger.error(e.getMessage(),e);
				}
			}
		}).start();
		
	}

	@Override
	public LeaveRegVO insertData(LeaveRegVO vo) throws BusinessException {
		if(vo == null)
			return null;
		return insertArrayData(new LeaveRegVO[]{vo})[0];
	}

	@Override
	public LeaveRegVO[] updateArrayData(LeaveRegVO[] vos) throws BusinessException {
		return null;
	}

	@Override
	public LeaveRegVO updateData(LeaveRegVO vo) throws BusinessException {
		if(vo == null)
			return null;
		PeriodServiceFacade.checkMonth(vo.getPk_org(), vo.getPk_psndoc(), vo.getBegindate(), vo.getEnddate());
		LeaveRegVO oldvo = getServiceTemplate().queryByPk(LeaveRegVO.class,vo.getPk_leavereg());
		//TODO:����û��޸������,��ԭ���Ľ����¼ҲҪ���¼���
		new LeaveRegValidatorService().validate(vo);
//		new LeaveApplyApproveManageMaintainImpl().checkCrossPeriodBills(vo.getPk_org(), new LeaveCommonVO[]{vo});
		check(vo);
		BillValidatorAtServer.checkCalendarCompleteForLeaveAndShutdown(vo);

		ITimeItemQueryService itemService = NCLocator.getInstance().lookup(ITimeItemQueryService.class);
		TimeItemCopyVO typeVO = itemService.queryCopyTypesByDefPK(vo.getPk_org(), vo.getPk_leavetype(), TimeItemCopyVO.LEAVE_TYPE);

		BillValidatorAtServer.checkLeaveRestrictPara(typeVO,vo.getLeavehour(),vo.getRestdayorhour());

		ILeaveBalanceManageService balanceService = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class);
		
		//�޸ı���ʱ���¹�����֯������ʧ��insertҲ���ں�̨�����
		ITBMPsndocQueryService psndocService = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		TBMPsndocVO psndocVO = psndocService.queryTBMPsndocByPsndoc(vo.getPk_org(), vo.getPk_psndoc(), vo.getEnddate());
		if(psndocVO == null)
			throw new BusinessException(ResHelper.getString("6017psndoc","06017psndoc0131"/*@res "����ʧ��,��Ա{0}�Ŀ��ڵ���ʱ�䷶Χ��������ʱ���"*/,psndocService.getPsnName(vo.getPk_psndoc())));
		// ���ù�����֯
		vo.setPk_adminorg(psndocVO.getPk_adminorg());
		
		LeaveRegVO regvo =  getServiceTemplate().update(true, vo)[0];
		balanceService.queryAndCalLeaveBalanceVO(vo.getPk_org(), vo);
		//ҵ����־
		TaBusilogUtil.writeLeaveRegEditBusiLog(new LeaveRegVO[]{regvo},new LeaveRegVO[]{oldvo});
		CacheProxy.fireDataUpdated(LeaveRegVO.getDefaultTableName());
		return regvo;
	}

	@Override
	public LeaveRegVO leaveOff(LeaveRegVO vo,ICalendar factBeginTime,ICalendar factEndTime) throws BusinessException {
		if(vo == null)
			return null;
		if(factBeginTime instanceof UFDateTime && factEndTime instanceof UFDateTime)
			return leaveOff(vo, (UFDateTime)factBeginTime, (UFDateTime)factEndTime);
		if(factBeginTime instanceof UFLiteralDate && factEndTime instanceof UFLiteralDate)
			return leaveOff(vo, (UFLiteralDate)factBeginTime, (UFLiteralDate)factEndTime);
		throw new IllegalArgumentException("params must have the same type!");
	}

	
	private LeaveRegVO leaveOff(LeaveRegVO vo,UFLiteralDate factBeginDate,UFLiteralDate factEndDate) throws BusinessException {
		if(vo == null){
			return null;
		}
		//MOD James
		validateRegCalculated(vo);
		
		validateAndUpdateBanlance(vo);
		//2015-09-17����������Ϊ����ٿ�ʼʱ���Ѿ���浫��Ӧ���������٣��޸�ΪֻҪ��������û�з�����������
//		validatePeriodSeal(vo.getPk_org(),factBeginDate,factEndDate,false);
//		validatePeriodSeal(vo.getPk_org(),vo.getLeavebegindate(),vo.getLeaveenddate(),true);
		validatePeriodSeal(vo.getPk_org(),factEndDate,factEndDate,false);
		validatePeriodSeal(vo.getPk_org(),vo.getLeaveenddate(),vo.getLeaveenddate(),true);

		ITBMPsndocQueryService s = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		s.checkTBMPsndocDate(vo.getPk_org(), vo.getPk_psndoc(), factBeginDate,factEndDate);


		vo.setLeavebegindate(factBeginDate);
		vo.setLeaveenddate(factEndDate);
		vo.setIsleaveoff(UFBoolean.TRUE);
		vo.setBacktime(new UFDateTime(System.currentTimeMillis()));

		BillValidatorAtServer.checkLeave(vo);
		BillValidatorAtServer.checkCalendarCompleteForLeaveAndShutdown(vo);
		
		//ҵ����־
		LeaveRegVO oldvo = getServiceTemplate().queryByPk(LeaveRegVO.class, vo.getPk_leavereg());
		TaBusilogUtil.writeLeaveRegOffBusiLog(new LeaveRegVO[]{vo},new LeaveRegVO[]{oldvo});

		vo = getServiceTemplate().update(true, vo)[0];
		
		NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(vo.getPk_org(), vo);

		return vo;
	}

	private LeaveRegVO leaveOff(LeaveRegVO vo,UFDateTime factBeginDateTime,UFDateTime factEndDateTime) throws BusinessException {
		if(vo == null){
			return null;
		}
		//MOD James
		validateRegCalculated(vo);
//		UFLiteralDate applyBeginDate = vo.getBegindate();
		UFLiteralDate applyEndDate = vo.getEnddate();
		//2015-09-17����������Ϊ����ٿ�ʼʱ���Ѿ���浫��Ӧ���������٣��޸�ΪֻҪ��������û�з�����������
//		validatePeriodSeal(vo.getPk_org(),applyBeginDate,applyEndDate,true);
		validatePeriodSeal(vo.getPk_org(),applyEndDate,applyEndDate,true);
		vo.setLeavebegintime(factBeginDateTime);
		vo.setLeaveendtime(factEndDateTime);
		TimeRuleVO timeRuleVO = NCLocator.getInstance().lookup(ITimeRuleQueryService.class).queryByOrg(vo.getPk_org());
		CommonMethods.processBeginEndDate(vo, timeRuleVO.getTimeZoneMap());
//		UFLiteralDate beginDate = vo.getBegindate();
		UFLiteralDate endDate = vo.getEnddate();
		validateAndUpdateBanlance(vo);
		//2015-09-17����������Ϊ����ٿ�ʼʱ���Ѿ���浫��Ӧ���������٣��޸�ΪֻҪ��������û�з�����������
//		validatePeriodSeal(vo.getPk_org(),beginDate,endDate,false);
		validatePeriodSeal(vo.getPk_org(),endDate,endDate,false);

		ITBMPsndocQueryService s = NCLocator.getInstance().lookup(ITBMPsndocQueryService.class);
		s.checkTBMPsndocTime(vo.getPk_org(), vo.getPk_psndoc(), factBeginDateTime,factEndDateTime);

		vo.setIsleaveoff(UFBoolean.TRUE);
		vo.setBacktime(new UFDateTime(System.currentTimeMillis()));

		BillValidatorAtServer.checkLeave(vo);
		BillValidatorAtServer.checkCalendarCompleteForLeaveAndShutdown(vo);
		//ҵ����־
		LeaveRegVO oldvo = getServiceTemplate().queryByPk(LeaveRegVO.class, vo.getPk_leavereg());
		TaBusilogUtil.writeLeaveRegOffBusiLog(new LeaveRegVO[]{vo},new LeaveRegVO[]{oldvo});

		vo = getServiceTemplate().update(true, vo)[0];
		
		NCLocator.getInstance().lookup(ILeaveBalanceManageService.class).queryAndCalLeaveBalanceVO(vo.getPk_org(), vo);

		return vo;
	}

	/**
	 *
	 * @param pk_org
	 * @param beginDate
	 * @param endDate
	 * @param title	����orʵ��
	 * @throws BusinessException
	 */
	private void validatePeriodSeal(String pk_org,UFLiteralDate beginDate,UFLiteralDate endDate,boolean isApply) throws BusinessException{
		PeriodVO[] vos = PeriodServiceFacade.queryPeriodsByDateScope(pk_org,beginDate, endDate);
		if(vos==null||vos.length<1){
			String msg = isApply?ResHelper.getString("6017leave","06017leave0219")/*@res "δ���ָ���������ڶ��ڵĿ����ڼ�!"*/:
				ResHelper.getString("6017leave","06017leave0220")/*@res "δ���ָ��ʵ�����ڶ��ڵĿ����ڼ�!"*/;
			throw new BusinessException(msg);
		}
		//MOD ȡ��������� James
//		for(PeriodVO periodVO : vos){
//			if(periodVO.getSealflag()!=null&&periodVO.getSealflag().booleanValue()) {
//				String msg = isApply?ResHelper.getString("6017leave","06017leave0221")/*@res "ָ���������ڶ��ڵĿ����ڼ��Ѿ����,��������!"*/:
//					ResHelper.getString("6017leave","06017leave0222")/*@res "ָ��ʵ�����ڶ��ڵĿ����ڼ��Ѿ����,��������!"*/;
//				throw new BusinessException(msg);
//			}
//		}
		
	}
	
	private void validateRegCalculated(LeaveRegVO vo) throws BusinessException{
		
		if(vo.getWaperiod() !=null && !StringUtil.isEmpty(vo.getWaperiod())){
			String msg = ResHelper.getString("leave","2leaveoffExt-00001")/*@res "δ���ָ��ʵ�����ڶ��ڵĿ����ڼ�!"*/;
			throw new BusinessException(msg);
		}
		
	}

	private void validateAndUpdateBanlance(LeaveRegVO vo) throws BusinessException{
		//��ѯ������Ϣ������ʱ����
		ILeaveBalanceManageService balanceService = NCLocator.getInstance().lookup(ILeaveBalanceManageService.class);
		Map<String, LeaveBalanceVO> balanceMap = balanceService.queryAndCalLeaveBalanceVO(vo.getPk_org(), vo);
		if(MapUtils.isEmpty(balanceMap))
			return;
		LeaveBalanceVO balanceVO = balanceMap.get(vo.getPk_psnorg()+vo.getPk_leavetype()+vo.getYearmonth());
		if(balanceVO==null)
			return;
		if(balanceVO.getIssettlement().booleanValue()||balanceVO.getIsuse().booleanValue())
			throw new BusinessException(ResHelper.getString("6017leave","06017leave0223")/*@res "�޷���ɲ���,��ǰ�ݼ������ݼ��ڼ�Ľ��������ѽ���!"*/);
		UFDouble oldHour = vo.getLeavehour();
		ILeaveRegisterInfoDisplayer displayer = new LeaveRegisterInfoDisplayer();
		vo = displayer.calculate(vo, TimeZone.getDefault());
		UFDouble trueHour = vo.getLeavehour().sub(oldHour);
		//����ʱ��
		balanceVO.setYidayorhour(balanceVO.getYidayorhour().add(trueHour));
		new BaseDAO().updateVO(balanceVO, new String[]{LeaveBalanceVO.YIDAYORHOUR});
//		getServiceTemplate().update(true, balanceVO);
	}


	@Override
	public Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> check(
			LeaveRegVO vo) throws BusinessException{
		return BillValidatorAtServer.checkLeave(vo);
	}

	@Override
	public Map<String, Map<Integer, ITimeScopeWithBillInfo[]>> check(
			String pk_org, LeaveRegVO[] vos) throws BusinessException{
		return BillValidatorAtServer.checkLeave(pk_org, vos);
	}

	@Override
	public LeaveCheckResult<LeaveRegVO> checkWhenSave(LeaveRegVO regVO)
			throws BusinessException {
		new LeaveRegValidatorService().validate(regVO);
		LeaveCheckResult<LeaveRegVO> checkResult = new LeaveCheckResult<LeaveRegVO>();
		checkResult.setMutexCheckResult(BillValidatorAtServer.checkLeave(regVO));
		SplitBillResult<LeaveRegVO> splitResult = SplitLeaveBillUtils.split(regVO);
		checkResult.setSplitResult(splitResult);
		return checkResult;
	}

	@Override
	public LeaveRegVO[] insertData(SplitBillResult<LeaveRegVO> splitResult)
			throws BusinessException {
		String splitid = new SequenceGenerator().generate();
		LeaveRegVO[] result = splitResult.getSplitResult();
		BillMethods.processBeginEndDatePkJobOrgTimeZone(result);
		//��ȡ�ݼ���� @author xiepch
		Map<String,Integer> typecoypMap = new HashMap<String,Integer>();//�ݼ����copy�������������
		Set<String> pk_typecoprySet = new HashSet<String>();
		for(LeaveRegVO vo : result){
			pk_typecoprySet.add(vo.getPk_leavetypecopy());
		}
		if(pk_typecoprySet.size() > 0){
			BaseDAO dao = new BaseDAO();
			String typecopysqlCondition = "  pk_timeitemcopy  in "+InSqlManager.getInSQLValue(pk_typecoprySet.toArray(new String[]{}));
			@SuppressWarnings("unchecked")
			Collection<LeaveTypeCopyVO> typecopyvos = dao.retrieveByClause(LeaveTypeCopyVO.class, typecopysqlCondition);
			if(typecopyvos != null && typecopyvos.size() > 0){
				for(LeaveTypeCopyVO copyvo : typecopyvos){
					typecoypMap.put(copyvo.getPk_timeitemcopy(), copyvo.getLeavesetperiod());
				}
			}
		}
		for(int i=0;i<result.length;i++){
			//tangcht
			if(result[i].getLeavebegindate().getYear() != result[i].getLeaveenddate().getYear()){
				throw new BusinessException(ResHelper.getString("6017leave","06017leave0265"))
						/*@res "�ݼ�ʱ�����,���ܱ���!"*/;
			}
			
			if(result[i].getBegindate().getYear()< Integer.valueOf(result[i].getLeaveyear()) 
					&& typecoypMap.get(result[i].getPk_leavetypecopy()) == 1){//�ݼ����Ϊ�������ʱ��ת�������Ϊ��Ȼ��
				result[i].setLeaveyear(result[i].getBegindate().getYear()+"");
			}
			result[i].setSplitid(splitid);
		}
		// �Ѿ�У���,����Ҫ��У��
		return insertData(result, false);
	}

	@Override
	public LeaveRegVO[] updateData(SplitBillResult<LeaveRegVO> splitResult)
			throws BusinessException {
		LeaveRegVO oriRegVO = (LeaveRegVO) new BaseDAO().retrieveByPK(LeaveRegVO.class, splitResult.getOriginalBill().getPk_leavereg());
		String splitid = oriRegVO.getSplitid();
		LeaveRegVO[] result = splitResult.getSplitResult();
		BillMethods.processBeginEndDatePkJobOrgTimeZone(result);
		List<LeaveRegVO> addList = new ArrayList<LeaveRegVO>();
		int index = 0; // ���µĵ���λ��
		//��ȡ�ݼ���� @author xiepch
		Map<String,Integer> typecoypMap = new HashMap<String,Integer>();//�ݼ����copy�������������
		Set<String> pk_typecoprySet = new HashSet<String>();
		for(LeaveRegVO vo : result){
			pk_typecoprySet.add(vo.getPk_leavetypecopy());
		}
		if(pk_typecoprySet.size() > 0){
			BaseDAO dao = new BaseDAO();
			String typecopysqlCondition = "  pk_timeitemcopy  in "+InSqlManager.getInSQLValue(pk_typecoprySet.toArray(new String[]{}));
			@SuppressWarnings("unchecked")
			Collection<LeaveTypeCopyVO> typecopyvos = dao.retrieveByClause(LeaveTypeCopyVO.class, typecopysqlCondition);
			if(typecopyvos != null && typecopyvos.size() > 0){
				for(LeaveTypeCopyVO copyvo : typecopyvos){
					typecoypMap.put(copyvo.getPk_timeitemcopy(), copyvo.getLeavesetperiod());
				}
			}
		}
		for(int i=0;i<result.length;i++){
			//tangcht
			if(result[i].getLeavebegindate().getYear() != result[i].getLeaveenddate().getYear()){
				throw new BusinessException(ResHelper.getString("6017leave","06017leave0265"))
						/*@res "�ݼ�ʱ�����,���ܱ���!"*/;
			}
			
			if(result[i].getBegindate().getYear()< Integer.valueOf(result[i].getLeaveyear()) 
					&& typecoypMap.get(result[i].getPk_leavetypecopy()) == 1){//�ݼ����Ϊ�������ʱ��ת�������Ϊ��Ȼ��
				result[i].setLeaveyear(result[i].getBegindate().getYear()+"");
			}
			LeaveRegVO regVO = result[i];
			regVO.setSplitid(splitid);
			if(oriRegVO.getPk_leavereg().equals(regVO.getPk_leavereg())){
				index = i;
				result[i] = updateData(regVO);
				continue;
			}
			addList.add(regVO);
		}
		if(CollectionUtils.isEmpty(addList))
			return result;
		LeaveRegVO[] finalResult = insertData(addList.toArray(new LeaveRegVO[0]), false);
		finalResult = (LeaveRegVO[]) ArrayUtils.add(finalResult, index, result[index]);
		return finalResult;
	}

	@Override
	public LeaveRegVO[] queryByPks(String[] pks) throws BusinessException {
		if(ArrayUtils.isEmpty(pks))
			return null;
		return getServiceTemplate().queryByPks(LeaveRegVO.class, pks);
	}
	@Override
	public LeaveRegVO queryByPsndate(String pk_psndoc, UFLiteralDate date,String pk_org)
			throws BusinessException {
		if(date == null)
			return null;
		String condition = " pk_org = '" + pk_org + "' and pk_psndoc = '" + pk_psndoc + "' and '" + date.toString() +
							"' between leavebegindate and leaveenddate order by leavebegintime desc ";
		LeaveRegVO[] vos = getServiceTemplate().queryByCondition(LeaveRegVO.class, condition);
		if(ArrayUtils.isEmpty(vos))
			return null;
		return vos[0];
	}
	
	@Override
	public String[] queryPKsByFromWhereSQL(LoginContext context,
			FromWhereSQL fromWhereSQL, Object etraConds)
			throws BusinessException {
		String cond=getSQLCondByFromWhereSQL(context, fromWhereSQL, etraConds);
		String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, LeaveRegVO.getDefaultTableName());
		List<String> result = excuteQueryPksBycond(cond, alias);
		return CollectionUtils.isEmpty(result) ? null : (String[])result.toArray(new String[0]);
	}
	
	public String getSQLCondByFromWhereSQL(LoginContext context,FromWhereSQL fromWhereSQL,
			Object etraConds) throws BusinessException{
		//ȡ������
		String alias = FromWhereSQLUtils.getMainTableAlias(fromWhereSQL, LeaveRegVO.getDefaultTableName());
		//ƴ��֯��sql
		String etraCondsNext = "("+alias + "." + LeaveRegVO.PK_ORG +
							(context.getPk_org()==null?" is null": (" = '" + context.getPk_org() + "' "))
							+ " or " + alias + "." + LeaveRegVO.PK_ADMINORG +
							(context.getPk_org()==null?" is null": (" = '" + context.getPk_org() + "' "))+")";
		//����Ȩ��sql��ʹ�ù�����¼��Դʵ���µġ�ʱ�����ͨ�����á�Ԫ���ݲ���
		fromWhereSQL = PubPermissionUtils.addPubQueryPermission2FromWhereSQL
		(fromWhereSQL, LeaveRegVO.getDefaultTableName(), LeaveRegVO.getDefaultTableName(), LeaveRegVO.PK_PSNJOB);

		//���ڼ��ѯ
		String periodSql = TaNormalQueryUtils.getPeriodSql(context, LeaveRegVO.class, alias,(TaBillRegQueryParams)etraConds);
		if(periodSql != null) {
			etraCondsNext += " and " + periodSql;
		}
		String sql = FromWhereSQLUtils.createSelectSQL(fromWhereSQL, LeaveRegVO.getDefaultTableName(),  
				new String[]{LeaveRegVO.PK_LEAVEREG}, null, null, null, null);
		etraCondsNext+=" and "+LeaveRegVO.PK_LEAVEREG+" in ( "+sql+" ) ";
		return etraCondsNext;
	}
	
	private List<String> excuteQueryPksBycond(String cond, String alias)throws BusinessException{
		String sql = "select "+(StringUtils.isEmpty(alias)?"":alias+".")+LeaveRegVO.PK_LEAVEREG+ 
				" from " + LeaveRegVO.getDefaultTableName() + " " + (StringUtils.isEmpty(alias) ? "" : alias);
		if (!StringUtils.isEmpty(cond))
			sql = sql + " where " + cond;
		List<String> result = (List)new BaseDAO().executeQuery(sql, new ColumnListProcessor());
		return result;
	}
}