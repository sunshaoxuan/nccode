package nc.bs.twhr.basedoc.ace.rule;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import nc.bs.ml.NCLangResOnserver;
import nc.impl.pubapp.pattern.data.vo.VOQuery;
import nc.impl.pubapp.pattern.database.DataAccessUtils;
import nc.impl.pubapp.pattern.pub.LockOperator;
import nc.impl.pubapp.pattern.rule.IRule;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pubapp.pattern.data.IRowSet;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.twhr.basedoc.BaseDocVO;

public class DataUniqueCheckRule implements IRule<BatchOperateVO> {

	@Override
	public void process(BatchOperateVO[] vos) {
		if (vos == null || vos.length == 0) {
			return;
		}
		Object[] oadd = vos[0].getAddObjs();
		Object[] oupd = vos[0].getUpdObjs();
		// ���û���������޸ĵ����ݣ�����ҪУ��
		BaseDocVO[] vosadd = null;
		if (oadd != null && oadd.length > 0) {
			vosadd = this.convertArrayType(oadd);
			this.checkDBUnique(vosadd);
			// return;
		}
		BaseDocVO[] vosupd = null;
		if (oupd != null && oupd.length > 0) {
			vosupd = this.convertArrayType(oupd);
			this.checkDBUnique(vosupd);
			// return;
		}
	}

	private List<String> codeList = new ArrayList<String>();
	private List<String> nameList = new ArrayList<String>();

	public void checkDBUnique(BaseDocVO[] bills) {
		if (bills == null || bills.length == 0) {
			return;
		}

		for (int j = 0; j < bills.length; j++) {
			BaseDocVO vo = bills[j];
			if (vo.getPrimaryKey() == null) {
				// ���������Ĕ���
				IRowSet rowSet = new DataAccessUtils().query(this.getCheckSql(
						vo, "INS"));
				if (rowSet.size() > 0) {
					ExceptionUtils
							.wrappBusinessException(NCLangResOnserver
									.getInstance().getStrByID(
											"68861005",
											"DataUniqueCheckRule-0000",
											null,
											new String[] { vo.getCode(),
													vo.getName() })/*
																	 * ����ʧ�ܣ�
																	 * ��ǰ�����������Ѿ����ڱ���
																	 * [
																	 * {0}]������[{
																	 * 1}]��ͬ�ļ�¼��
																	 */);
				}
			} else {
				// ���θ��µĔ���
				BaseDocVO[] dbvo = new VOQuery<BaseDocVO>(BaseDocVO.class)
						.query(new String[] { vo.getPrimaryKey() });
				this.doLock(dbvo);
				IRowSet rowSet = new DataAccessUtils().query(this.getCheckSql(
						vo, "UPD"));
				if (rowSet.size() > 0) {
					ExceptionUtils
							.wrappBusinessException(NCLangResOnserver
									.getInstance().getStrByID(
											"68861005",
											"DataUniqueCheckRule-0001",
											null,
											new String[] { vo.getCode(),
													vo.getName() })/*
																	 * ����ʧ�ܣ�
																	 * ��ǰ�޸ĵ������Ѿ����ڱ���
																	 * [
																	 * {0}]������[{
																	 * 1}]��ͬ�ļ�¼��
																	 */);
				}
			}
			// ���α��������ظ�
			if (codeList.contains(vo.getCode())
					|| nameList.contains(vo.getName())) {
				ExceptionUtils
						.wrappBusinessException(NCLangResOnserver.getInstance()
								.getStrByID(
										"68861005",
										"DataUniqueCheckRule-0002",
										null,
										new String[] { vo.getCode(),
												vo.getName() })/*
																 * ����ʧ�ܣ���ǰά�������ݾ����ڱ���
																 * [
																 * {0}]������[{1}]��ͬ�ļ�¼
																 * ��
																 */);
			} else {
				codeList.add(vo.getCode());
				nameList.add(vo.getName());
			}
		}
	}

	private BaseDocVO[] convertArrayType(Object[] vos) {
		BaseDocVO[] smartVOs = (BaseDocVO[]) Array.newInstance(BaseDocVO.class,
				vos.length);
		System.arraycopy(vos, 0, smartVOs, 0, vos.length);
		return smartVOs;
	}

	private void doLock(BaseDocVO[] bills) {
		List<String> lockobj = new ArrayList<String>();
		for (int i = 0; i < bills.length; i++) {
			lockobj.add("#code_name#");
		}
		LockOperator lock = new LockOperator();
		lock.lock(
				lockobj.toArray(new String[lockobj.size()]),
				NCLangResOnserver.getInstance().getStrByID("68861005",
						"DataUniqueCheckRule-0003")/* ��ǰ���ݼ�¼�������û��ڲ��������Ժ�ˢ�º��ٲ��� */);
	}

	/**
	 * ƴ��Ψһ��У���sql
	 * 
	 * @param bill
	 * @param opr
	 * @return
	 */
	private String getCheckSql(BaseDocVO vo, String opr) {
		StringBuffer sql = new StringBuffer();
		sql.append("select code,name ");
		sql.append("  from ");
		sql.append(vo.getTableName());

		sql.append(" where ");
		sql.append(" (code ='");
		sql.append(vo.getCode());
		sql.append("' ");
		sql.append(" or ");
		sql.append(" name='");
		sql.append(vo.getName());
		sql.append("' ");
		sql.append(") and dr=0 ");
		sql.append(" and pk_org='" + vo.getPk_org() + "' ");
		if (opr.equals("UPD")) {
			sql.append(" and id <> '" + vo.getPrimaryKey() + "'");
		}
		// sql.append(" group by code ");
		// sql.append(" having count(1) > 1;");
		return sql.toString();
	}

}