package nc.pubitf.para;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
/**
 * ����coding ˮƽ,���λ��������
 * @author Ares.Tank
 *
 */
public class SysInitQuery4TWHR {
	// ����
	private static final String TABLE_NAME = "twhr_basedoc";
	// number�ֶ�
	private static final String NUMBER_VALUE = "numbervalue";

	/**
	 * Twhr �����趨-��֯�µĲ�����ѯ
	 * 
	 * @param pk_org
	 * @param initCode
	 * @return
	 * @throws BusinessException
	 * @date 2018��9��22�� ����11:07:47
	 * @description
	 */
	public static UFDouble getParaDbl(String pk_org, String initCode) throws BusinessException {
		String sqlStr = "select wb." + NUMBER_VALUE + " from " + TABLE_NAME + " wb" + " where code = '" + initCode
				+ "' and pk_org='" + pk_org + "'";
		
		
			IUAPQueryBS iUAPQueryBS = (IUAPQueryBS) NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
			List qryResultList = (List) iUAPQueryBS.executeQuery(sqlStr, new ColumnListProcessor());
			for(Object obj : qryResultList){
				try{
					return new UFDouble(obj.toString());
				}catch(Exception e){
					throw new BusinessException (e);
				}
				
			}
			throw new BusinessException ("The data whose initcode is TWEP0001 is dirty!");
			
			
		
	}

}