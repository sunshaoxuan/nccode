package nc.itf.twhr;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.nhicalc.AggNhiCalcVO;
import nc.vo.twhr.nhicalc.NhiCalcVO;
import nc.vo.uif2.LoginContext;

public interface INhicalcMaintain extends ISmartService {

    public void delete(AggNhiCalcVO[] vos) throws BusinessException;

    public AggNhiCalcVO[] insert(AggNhiCalcVO[] vos) throws BusinessException;

    public AggNhiCalcVO[] update(AggNhiCalcVO[] vos) throws BusinessException;

    public AggNhiCalcVO[] query(IQueryScheme queryScheme) throws BusinessException;

    public boolean isAudit(String pk_org, String cyear, String cperiod) throws BusinessException;

    public void audit(String pk_org, String cyear, String cperiod) throws BusinessException;

    public void unAudit(String pk_org, String cyear, String cperiod) throws BusinessException;

    public AggNhiCalcVO[] queryByCondition(LoginContext context, String condition) throws BusinessException;

    public void updateNhiCalcVO(NhiCalcVO[] vos) throws BusinessException;

}