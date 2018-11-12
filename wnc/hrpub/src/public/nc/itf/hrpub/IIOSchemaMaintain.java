package nc.itf.hrpub;

import nc.itf.pubapp.pub.smart.ISmartService;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.hrpub.mdmapping.IOSchemaVO;
import nc.vo.pub.BusinessException;

public interface IIOSchemaMaintain extends ISmartService {

	public IOSchemaVO[] query(IQueryScheme queryScheme)
			throws BusinessException, Exception;

	public IOSchemaVO[] queryAll() throws BusinessException;
}