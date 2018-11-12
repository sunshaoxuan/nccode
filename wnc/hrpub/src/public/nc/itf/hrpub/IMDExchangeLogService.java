package nc.itf.hrpub;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;

public interface IMDExchangeLogService {
	public static final String LOG_TABLE = "HRPUB_IOLOG";

	public void logging(String sessionid, String classid, String oprType,
			String status, UFDateTime oprTime) throws BusinessException;

	public void saveDataFile(String sessionid, String oprTime, String strData,
			boolean isInput) throws BusinessException;

	public void createLogTable() throws BusinessException;

	public boolean isSessionExists(String sessionid) throws BusinessException;
}
