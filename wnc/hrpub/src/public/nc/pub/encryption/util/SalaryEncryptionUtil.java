package nc.pub.encryption.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.paydata.DataVO;

/**
 * 加密工具类
 * 
 * @author zszyff
 */
public class SalaryEncryptionUtil {
    
	//明文最大值(此数一旦开始使用,不得修改,要和解密工具类对应)
	public final static int MAX_VALUE= 10000000;
	//明文最大精度(明文最大值的位数)(此数一旦开始使用,不得修改,要和解密工具类对应)
	public final static int MAX_DEGREE= 7;
	//(-1,0)区间映射的整数部分(需要比明文最大值要大)(此数一旦开始使用,不得修改,要和解密工具类对应)
	public final static int POINT_INT_VALUE= 99999999;
    

	/**
	 * 根据明文数据，将数据拆分为整数与小数部分，分别处理
	 * 
	 * @param m
	 * @return
	 */
	public final static double encryption(double m) {
		// 解析m类型 是否是科学计数法
		int jd = 0;
		String m_s = "";
		if (String.valueOf(m).contains("E")) {
			jd = Integer.valueOf(String.valueOf(m).split("E")[1]) + 1;// 没有办法
																		// 只能这样
																		// 取得精度值6
			UFDouble dd = new UFDouble(m, jd);
			m_s = dd.toString();
		} else if (String.valueOf(m).contains("e")) {
			jd = Integer.valueOf(String.valueOf(m).split("e")[1]) + 1;// 没有办法
																		// 只能这样
																		// 取得精度值6
			UFDouble dd = new UFDouble(m, jd);
			m_s = dd.toString();
		} else {
			//精度定最高6位,防止double出现实际小数位数大于有效数字的情况 tank 2020年2月18日23:35:38
			String c_s = String.format("%."+MAX_DEGREE+"f", m);
			m_s = new Double(Double.parseDouble(c_s)).toString();
		}
		// 2016-11-21 zhousze 薪资加密：将明文数据转换成string后，判断是否包含小数
		String[] split = m_s.split("\\.");
		double p_i = Double.parseDouble(split[0]); // 整数部分
		double p_d = Double.parseDouble(split[1]); // 小数部分
		if (p_d < 0.000000001) {
			return encryption4part(p_i);
		} else {

			// 2016-12-22 zhousze 薪资加密：这里处理小数部分，倒序排序加密处理
			StringBuffer s1 = new StringBuffer(split[1]);
			p_d = Double.parseDouble(s1.reverse().toString());

			p_i = encryption4part(p_i);
			p_d = encryption4part(p_d);
			StringBuffer s2 = new StringBuffer(String.valueOf(p_d));
			String p_d_s = s2.reverse().toString();
			double rs = p_i + Double.parseDouble(p_d_s);
			//解决(-1,0)区间内的映射问题 tank 2020年2月19日01:04:27
			if(m<0 && rs > 0 && rs < 1){
				rs = -(1-rs+POINT_INT_VALUE);
			}
			return rs;
		}
	}

	/**
	 * m代表明文消息数据，这里必须满足两个条件：（1）0<k<n；（2）k与n互素
	 * 
	 * @param m
	 * @param k
	 * @param n
	 * @return
	 */
	private static double encryption4part(double m) {
		// 2016-11-21 zhousze 薪资加密：这里c代表加密后数据，k代表密钥，n代表明文消息数据最大值，
		// 通过下面乘法替代算法加密数据。
		double k = 6666667;
		double n = MAX_VALUE;
		double c = 0;
		c = (m * k) % n;
		return c;
	}

	/**
	 * 处理所有薪资VO数据保存时的加密处理
	 * 
	 * @param dataVos
	 * @return
	 */
	public final static DataVO[] encryption4Array(DataVO[] dataVos) {
		for (int i = 0; i < dataVos.length; i++) {
			HashMap<String, Object> map = dataVos[i].appValueHashMap;
			Object[] pks = map.keySet().toArray();
			// 2016-11-22 zhousze 薪资加密：过滤数值型薪资项目，进行加密处理
			ArrayList<String> itemPks = new ArrayList<String>();
			// itemPks.add("expense_deduction"); // 费用扣除标准
			// itemPks.add("taxable_income"); // 应纳税所得额
			// itemPks.add("taxrate"); // 税率
			// itemPks.add("nquickdebuct"); // 速算扣除数
			for (int j = 0; j < pks.length; j++) {
				if (pks[j].toString().startsWith("f_")) {
					itemPks.add(pks[j].toString());
				}
			}
			// 2016-11-22 zhousze 薪资加密：这里替换原来VO中的数值型字薪资项目，替换成加密后的薪资项目
			for (int h = 0; h < itemPks.size(); h++) {
				UFDouble itemValueBefore = (UFDouble) dataVos[i].getAttributeValue(itemPks.get(h));
				if (UFDouble.ZERO_DBL.equals(itemValueBefore)) {
					continue;
				}
				NumberFormat nf = NumberFormat.getInstance();
				// 设置此格式中不使用分组
				nf.setGroupingUsed(false);
				// 设置数的小数部分所允许的最大位数。
				nf.setMaximumFractionDigits(8);
				if (itemValueBefore != null) {
					if (itemValueBefore.toDouble() == (5.1E-4)) {
						System.out.println("你他妈的坑死我了知道吗？");
					}
					String format = nf.format(itemValueBefore.toDouble());
					Double valueOf = Double.valueOf(format);
					double itemValueAfter = encryption(itemValueBefore.doubleValue());
					dataVos[i].setAttributeValue(itemPks.get(h), new UFDouble(itemValueAfter));
				}
			}
		}
		return dataVos;
	}
}
