package nc.ui.wa.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uap.iweb.log.Logger;
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.MapListProcessor;
import nc.vo.pub.BusinessException;

/**
 * 用于获取台湾本地化参数
 * @author ward
 *
 *
 */
public class LocalizationSysinitUtil {
	private static Map<String ,String> twhrlorg=null;
	private static Map<String ,String> twhrlpsn=null;
	private static IUAPQueryBS queryBS = null;
	
	public static IUAPQueryBS getUAPQueryBS() {
		if (queryBS == null) {
			queryBS = NCLocator.getInstance().lookup(IUAPQueryBS.class);
		}
		return queryBS;
	}
	
	public static String getTwhrlOrg(String key){
		if(null==twhrlorg){
			twhrlorg=getMap("TWHRLORG", twhrlorg);
		}
		return twhrlorg.get(key);
	}
	
	public static String getTwhrlPsn(String key){
		if(null==twhrlpsn){
			twhrlpsn=getMap("TWHRLPSN", twhrlpsn);
		}
		return twhrlpsn.get(key);
	}
	
	public static String getTwhrzzxx(String key){
		if(null==twhrlpsn){
			twhrlpsn=getMap("TWHRZZXX", twhrlpsn);
		}
		return twhrlpsn.get(key);
	}
	
	public static Map<String,String> getMap(String initcode,Map<String,String> map){
		String qrySql="select initcode,value from pub_sysinit where initcode like '"+initcode+"%' and isnull(dr,0)=0";
		try {
			List<HashMap<String, Object>> listMaps=(ArrayList<HashMap<String, Object>>)getUAPQueryBS().executeQuery(qrySql.toString(),new MapListProcessor());
			if(listMaps!=null&&listMaps.size()>0){
				map=new HashMap<String,String>();
				for (int i = 0; i < listMaps.size(); i++) {
					HashMap<String, Object> hashMap=listMaps.get(i);
					String initcode2=hashMap.get("initcode")!=null?hashMap.get("initcode").toString():"";
					String value=hashMap.get("value")!=null?hashMap.get("value").toString():"";
					map.put(initcode2, value);
				}
			}
		} catch (BusinessException e) {
			nc.bs.logging.Logger.error(e);
		}
		return map;
	}
	
	
}
