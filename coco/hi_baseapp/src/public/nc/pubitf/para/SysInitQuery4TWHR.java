package nc.pubitf.para;

import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ColumnListProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
/**
 * 辣鸡coding 水平,请各位看官完善
 * @author Ares.Tank
 *
 */
public class SysInitQuery4TWHR {
	// 表名
	private static final String TABLE_NAME = "twhr_basedoc";
	// number字段
	private static final String NUMBER_VALUE = "numbervalue";

	/**
	 * Twhr 参数设定-组织下的参数查询
	 * 
	 * @param pk_org
	 * @param initCode
	 * @return
	 * @throws BusinessException
	 * @date 2018年9月22日 上午11:07:47
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
