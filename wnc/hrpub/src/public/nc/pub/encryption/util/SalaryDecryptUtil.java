package nc.pub.encryption.util;

import java.util.ArrayList;
import java.util.HashMap;

import nc.vo.pub.lang.UFDouble;
import nc.vo.wa.paydata.DataVO;

/**
 * 解密工具类
 * 
 * @author zszyff
 * 
 */
public class SalaryDecryptUtil {
    	//明文最大值(此数一旦开始使用,不得修改,要和加密工具类对应)
  	public final static int MAX_VALUE= 10000000;
  	//明文最大精度(明文最大值的位数)(此数一旦开始使用,不得修改,要和加密工具类对应)
  	public final static int MAX_DEGREE= 7;
  	//(-1,0)区间映射的整数部分(需要比明文最大值要大)(此数一旦开始使用,不得修改,要和加密工具类对应)
  	public final static int POINT_INT_VALUE= 99999999;
	/**
	 * 根据密文数据，拆分为整数、小数部分，分别处理解密
	 * 
	 * @param c
	 * @return
	 */
  	public final static double decrypt(double c) {
		//如果超过了最大加密值
		boolean isMaping = false;
		if(c<-MAX_VALUE){
			c = c+POINT_INT_VALUE;
			isMaping = true;
		}

		String c_s = String.format("%."+MAX_DEGREE+"f", c);
		Double c_s_d = new Double(Double.parseDouble(c_s));
		c_s = new Double(Double.parseDouble(c_s)).toString();
		if (c_s.contains(".")) {
			// 2016-11-21 zhousze 薪资解密：将明文数据转换成string后，判断是否包含小数
			String[] split = c_s.split("\\.");
			double p_i = Double.parseDouble(split[0]); // 整数部分
			double p_d = Double.parseDouble(split[1]); // 小数部分

			if (p_d < 0.00000001) {
				return decrypt4part(p_i);
			} else {
				//处理负小数出现解密错误的情况 tank 2020年2月18日22:24:13
				if(split[0].charAt(0)=='-'){
					//得到真正的小数部分
					double real_p_d = 1+(c-c_s_d.intValue());
					if(isMaping){
						//如果是映射的小数,那么只计算小数部分
						return -decrypt(real_p_d);
					}else {
						//真正的整数部分
						p_i = p_i - 1;
						//按照正数的去解密,然后加上负号
						return -decrypt(-(p_i)+real_p_d);
					}


				}
					// 2016-12-22 zhousze 薪资解密：这里处理小数数据解密，倒序解密
					StringBuffer s1 = new StringBuffer(split[1]);
					p_d = Double.parseDouble(s1.reverse().toString());

					p_i = decrypt4part(p_i);
					p_d = decrypt4part(p_d);
					StringBuffer s = new StringBuffer(String.valueOf(p_d));
					String p_d_s = s.reverse().toString();
					return p_i + Double.parseDouble(p_d_s);

			}
		} else {
			return decrypt4part(c);
		}
	}

  	public final static UFDouble decrypt(UFDouble c) {
		return c == null ? UFDouble.ZERO_DBL : (new UFDouble(decrypt(c.doubleValue())));
	}

	/**
	 * 根据传进来的密文数据c，首先求得模运算的逆元，然后根据逆元求得明文数据
	 * 
	 * @param c
	 * @return
	 */
  	private static double decrypt4part(double c) {
		// 2016-11-21 zhousze 薪资解密：根据k，n得出对应的逆元k1，然后得到明文数据m
		double k = 6666667;
		double n = MAX_VALUE;
		double k1 = inverseElement((int) k, (int) n);
		double m = 0;
		m = (c * k1) % n;
		return m;
	}

	/**
	 * 根据密钥k以及总数n，通过处理得到密钥的逆元用于解密
	 * 
	 * @param k
	 * @param n
	 * @return
	 */
  	private static int inverseElement(int k, int n) {
		// 2016-11-21 zhousze 薪资解密：求解逆元方法：这里要求k相对于n的逆元k1，这里k<n。此时存在a*k + q ==
		// n，即a*k + q同余0(mod n)，
		// 这里a=[n/k]，q=n%k。在等式两边同时乘以k' * q'，其中k'是k的逆元，q'是q的逆元，得到：a*q' +
		// k'同余0(mod n)。移项得到：
		// k'=-a * q'。（或者使用扩展欧几里得算法，或者费马小定理）
		if (k == 0) {
			return -1;
		}
		if (k == 1) {
			return 1;
		}
		int ret = inverseElement(n % k, n);
		if (ret == -1) {
			return ret;
		}
		return (-n / k * ret % n + n) % n;
	}

	/**
	 * 处理所有薪资查询等情况下，解密VO数据
	 * 
	 * @param dataVos
	 * @return
	 */
	public final static DataVO[] decrypt4Array(DataVO[] dataVos) {
		for (int i = 0; i < dataVos.length; i++) {
			HashMap<String, Object> map = dataVos[i].appValueHashMap;
			Object[] pks = map.keySet().toArray();
			// 2016-11-22 zhousze 薪资解密：过滤数值型薪资项目以及税率相关字段，进行解密处理
			ArrayList<String> itemPks = new ArrayList<String>();
			// itemPks.add("expense_deduction");
			// itemPks.add("taxable_income");
			// itemPks.add("taxrate");
			// itemPks.add("nquickdebuct");
			for (int j = 0; j < pks.length; j++) {
				if (pks[j].toString().startsWith("f_")) {
					itemPks.add(pks[j].toString());
				}
			}
			// 2016-11-22 zhousze 薪资解密：这里替换原来VO中的数值型字薪资项目，替换成解密后的薪资项目
			for (int k = 0; k < itemPks.size(); k++) {
				UFDouble itemValueBefore = (UFDouble) dataVos[i].getAttributeValue(itemPks.get(k));
				if (itemValueBefore != null && itemValueBefore.doubleValue() != 0) {
					double itemValueAfter = decrypt(itemValueBefore.doubleValue());
					dataVos[i].setAttributeValue(itemPks.get(k), new UFDouble(itemValueAfter));
				}
			}
		}
		return dataVos;
	}
}
