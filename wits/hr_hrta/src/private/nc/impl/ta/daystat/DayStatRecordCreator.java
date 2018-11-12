package nc.impl.ta.daystat;

import nc.bs.logging.Logger;
import nc.impl.ta.dailydata.AbstractRecordCreator;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.exception.DbException;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.daystat.DayStatVO;
import nc.vo.ta.daystat.DayStatbVO;

public class DayStatRecordCreator extends AbstractRecordCreator {

	public DayStatRecordCreator() {
		super();
	}

	public DayStatRecordCreator(int creator_type) {
		super(creator_type);
	}

	@Override
	protected String getDateField() {
		return DayStatVO.CALENDAR;
	}

	@Override
	protected String getPkField() {
		return DayStatVO.PK_DAYSTAT;
	}

	@Override
	protected String getPsndocField() {
		return DayStatVO.PK_PSNDOC;
	}

	@Override
	protected String getTableAlias() {
		return "daystat";
	}

	@Override
	protected String getTableName() {
		return "tbm_daystat";
	}

	@Override
	protected  Class<? extends SuperVO> getVOClass() {
		return DayStatVO.class;
	}


	//MOD James

	@Override
	protected String[] getSubTableFkFields() {
		return new String[]{DayStatbVO.PK_DAYSTAT,DayStatbVO.PK_DAYSTAT};
	}
	//MOD James

	@Override
	protected String[] getSubTableNames() {
		return new String[]{"tbm_daystatb","tbm_daystatb_notcurrmonth"};
	}

	@Override
	protected int getTBMProp() {
		return -1;
	}

	@Override
	protected void deleteGarbageData(String pk_org, UFLiteralDate beginDate,
			UFLiteralDate endDate) throws BusinessException {
		//删除本组织垃圾数据
		String where = " where pk_org = '"+pk_org+"' and calendar between '"+beginDate.toString()+"' and '"+endDate.toString()+"' " + 
				" and not exists(select 1 from tbm_psndoc where tbm_psndoc.pk_org = tbm_daystat.pk_org and tbm_daystat.calendar between tbm_psndoc.begindate and tbm_psndoc.enddate and tbm_psndoc.pk_psndoc = tbm_daystat.pk_psndoc)";
		String delSubSql = " delete from tbm_daystatb where pk_daystat in ( select pk_daystat from tbm_daystat" + where + " ) ";


		String delSql = " delete from tbm_daystat " + where;
		//删除其他组织的垃圾数据
		String othWhere = " where pk_org <> '"+pk_org+"' and calendar between '"+beginDate.toString()+"' and '"+endDate.toString()+"' " + 
			" and exists(select 1 from tbm_psndoc where tbm_psndoc.pk_org = '"+pk_org+"' and tbm_daystat.calendar between tbm_psndoc.begindate and tbm_psndoc.enddate and tbm_psndoc.pk_psndoc = tbm_daystat.pk_psndoc)";
		String delOthSubSql = " delete from tbm_daystatb where pk_daystat in ( select pk_daystat from tbm_daystat" + othWhere + " ) ";
		String delOthSql = " delete from tbm_daystat " + othWhere;
		JdbcSession session = null;
		try{
	    	session = new JdbcSession();
    		session.addBatch(delSubSql);
    		session.addBatch(delSql);
    		session.addBatch(delOthSubSql);
    		session.addBatch(delOthSql);
	    	session.executeBatch();
	    }catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	    finally{
	    	if(session!=null)
	    		session.closeAll();
	    }
	}

}
