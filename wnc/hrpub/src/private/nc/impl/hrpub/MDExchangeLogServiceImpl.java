package nc.impl.hrpub;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.RuntimeEnv;
import nc.itf.hrpub.IMDExchangeLogService;
import nc.jdbc.framework.processor.ColumnProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;

public class MDExchangeLogServiceImpl implements IMDExchangeLogService {
	private BaseDAO baseDAO;
	private static boolean isLogTableExists = false;

	public BaseDAO getBaseDAO() {
		if (baseDAO == null) {
			baseDAO = new BaseDAO();
		}
		return baseDAO;
	}

	@Override
	public void logging(String sessionid, String classid, String oprType,
			String status, UFDateTime oprTime) throws BusinessException {
		String sql = "";
		if (isSessionExists(sessionid)) {
			sql = "update " + IMDExchangeLogService.LOG_TABLE + " set "
					+ " status='" + status + "', operationtime='"
					+ oprTime.toString() + "' where sessionid='" + sessionid
					+ "'";
		} else {
			sql = "insert into " + IMDExchangeLogService.LOG_TABLE
					+ "(sessionid, classid, iotype, operationtime, status)"
					+ " values('" + sessionid + "', '" + classid + "', '"
					+ oprType + "', '" + oprTime.toString() + "', '" + status
					+ "')";
		}
		this.getBaseDAO().executeUpdate(sql);
	}

	@Override
	public void createLogTable() throws BusinessException {
		if (!isLogTableExists) {
			String sql = "select count(*) from user_tables where table_name = '"
					+ IMDExchangeLogService.LOG_TABLE + "'";
			Integer tablecount = (Integer) this.getBaseDAO().executeQuery(sql,
					new ColumnProcessor());
			if (tablecount == 0) {
				sql = "CREATE TABLE "
						+ IMDExchangeLogService.LOG_TABLE
						+ " ("
						+ "sessionid CHAR(200) NOT NULL, "
						+ "classid CHAR(50) NOT NULL, "
						+ "iotype CHAR(20), "
						+ "operationtime CHAR(19), "
						+ "status CHAR(100), "
						+ "ts CHAR(19) DEFAULT TO_CHAR(SYSDATE,'yyyy-mm-dd hh24:mi:ss'), "
						+ "PRIMARY KEY (sessionid) " + ")";
				this.getBaseDAO().executeUpdate(sql);
			}

			isLogTableExists = true;
		}
	}

	@Override
	public boolean isSessionExists(String sessionid) throws BusinessException {
		String sql = "select count(*) from " + IMDExchangeLogService.LOG_TABLE
				+ " where sessionid = '" + sessionid + "'";
		Integer ssncount = (Integer) this.getBaseDAO().executeQuery(sql,
				new ColumnProcessor());
		if (ssncount > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void saveDataFile(String sessionid, String oprTime, String strData,
			boolean isInput) throws BusinessException {
		String filePath = RuntimeEnv.getInstance().getNCHome()
				+ "/importdata/hr/dataexchange/interface/";
		try {
			File logFile = new File(filePath
					+ oprTime.trim().replace("/", "-").replace(":", "-")
							.replace(" ", "_") + "." + sessionid + "."
					+ (isInput ? "INPUT" : "OUTPUT") + ".json");
			logFile.createNewFile();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(logFile), StandardCharsets.UTF_8));
			writer.write(strData);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}

}
