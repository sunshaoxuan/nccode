package nc.impl.hrpub;

import nc.bs.framework.common.NCLocator;
import nc.itf.hrpub.IMDExchangeService;
import nc.itf.hrpub.IWNCTASKQuery;

public class WNCTASKQueryImpl implements IWNCTASKQuery {

	@Override
	public String doQuery(String sessionid) throws Exception {
		IMDExchangeService deSrv = NCLocator.getInstance().lookup(IMDExchangeService.class);
		return deSrv.taskQuery(sessionid);
	}

}
