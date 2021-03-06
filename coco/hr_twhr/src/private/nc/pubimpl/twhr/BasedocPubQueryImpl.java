package nc.pubimpl.twhr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.pubitf.twhr.IBasedocPubQuery;
import nc.vo.pub.BusinessException;
import nc.vo.twhr.basedoc.BaseDocVO;

public class BasedocPubQueryImpl implements IBasedocPubQuery {

	@SuppressWarnings("unchecked")
	@Override
	public BaseDocVO[] queryAllBaseDoc(String pk_org) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		//组织下的参数
		List<BaseDocVO> result = (List<BaseDocVO>)dao.retrieveByClause(BaseDocVO.class,
				"dr=0 and pk_org='" + pk_org + "' ");
		//全局的参数
		List<BaseDocVO> result_glb = (List<BaseDocVO>)dao.retrieveByClause(BaseDocVO.class,
				"dr=0 and pk_org='GLOBLE00000000000000' ");
		//全局中有，然鹅组织中没有的参数
		List<BaseDocVO> differentlist = new ArrayList<BaseDocVO>();
		for(BaseDocVO basedocvo : result_glb){
			int i = 0;
			for(BaseDocVO org_basedocvo : result){
				if(basedocvo.getCode().equals(org_basedocvo.getCode())){
					i++;
				}
			}
			if(i==0){
				differentlist.add(basedocvo);
			}
		}
		result.addAll(differentlist);
		if (result == null) {
			return null;
		}
		return result.toArray(new BaseDocVO[0]);
	}

	@Override
	public BaseDocVO queryBaseDocByCode(String pk_org, String strCode)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		Collection<BaseDocVO> result = dao.retrieveByClause(BaseDocVO.class,
				" pk_org='" + pk_org + "' and code='" + strCode + "' and dr=0");
		if (result != null&&result.size()>0) {//修复一个空指针异常 Ares.Tank 2018-7-24 18:07:59
			return (BaseDocVO) result.toArray()[0];
		}
		return null;
		
	}

	@Override
	public BaseDocVO queryBaseDocByCode(String strCode)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		Collection<BaseDocVO> result = dao.retrieveByClause(BaseDocVO.class,
				" pk_org='GLOBLE00000000000000' and code='" + strCode + "' and dr=0");
		if (result != null&&result.size()>0) {//修复一个空指针异常 Ares.Tank 2018-7-24 18:07:59
			return (BaseDocVO) result.toArray()[0];
		}
		return null;
	}
}
