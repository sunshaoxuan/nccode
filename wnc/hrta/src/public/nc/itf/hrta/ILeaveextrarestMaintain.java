package nc.itf.hrta;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.ta.leaveextrarest.AggLeaveExtraRestVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFLiteralDate;

public interface ILeaveextrarestMaintain {

	public void delete(AggLeaveExtraRestVO[] vos) throws BusinessException;

	public AggLeaveExtraRestVO[] insert(AggLeaveExtraRestVO[] vos) throws BusinessException;

	public AggLeaveExtraRestVO[] update(AggLeaveExtraRestVO[] vos) throws BusinessException;

	public AggLeaveExtraRestVO[] query(IQueryScheme queryScheme)
			throws BusinessException;
	/**
	 * 根據年資起算日,計算外加補休的到期日
	 * @param pk_psndoc
	 * @param Datebeforechange 對比日期
	 * @param billDate 單據日期
	 * @return
	 * @throws BusinessException
	 */
	public UFLiteralDate calculateExpireDateByWorkAge(String pk_psndoc,
			UFLiteralDate dateBeforeChange,UFLiteralDate billDate) throws BusinessException;


}