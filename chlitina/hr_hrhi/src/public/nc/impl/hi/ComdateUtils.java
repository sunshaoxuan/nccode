package nc.impl.hi;
 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFLiteralDate;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatter;

public class ComdateUtils {

	 
	/**
	 * 如何获取两个时间段之间重合的部分
	 * @date 2018年8月22日
	 */

public static String getAlphalDate(String startdate, String cbaseDate, String begindate, String enddate) {
	
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long sblong=0;
		long eblong=0;
		try {
			// 标准时间
			Date st = sdf.parse(startdate.toString());
			Date ed = sdf.parse(cbaseDate.toString());
			// 目标时间
			Date bt = sdf.parse(begindate.toString());
			Date ot = sdf.parse(enddate.toString());
			
			
			long btlong = Math.min(bt.getTime(), ot.getTime());// 开始时间
			long otlong = Math.max(bt.getTime(), ot.getTime());// 结束时间
			long stlong = Math.min(st.getTime(), ed.getTime());// 开始时间
			long edlong = Math.max(st.getTime(), ed.getTime());// 结束时间
			
			
			// 具体算法如下
			// 首先看是否有包含关系
		
			if ((stlong >= btlong && stlong <= otlong) || (edlong >= btlong && edlong <= otlong) || stlong<= btlong && edlong >= otlong) {
			// 一定有重叠部分
			sblong = stlong >= btlong ? stlong : btlong;
			eblong = otlong >= edlong ? edlong : otlong;
			}
		
		} catch (Exception e) {
		e.printStackTrace();
		}
		if(sblong == 0 || eblong == 0){
			return null;
		}
		
		return sdf.format(sblong)+":"+sdf.format(eblong);
		
		}
		
		

//获得最后一天，传入的参数是一个int类型的年份与月份
public Date getMonthEndTime(int year, int month) throws ParseException {
    //选中月份的最后一天
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1);
    int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

    //格式化日期，不需要时分秒的话可以去掉
    String endDateStr = year + "-" + month + "-" + lastDay ;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Date endDate = df.parse(endDateStr);

    return endDate;
}

//两个日期的差异月份
public int getmonths(UFDate startdate, UFDate cbaseDate){
	DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    DateTime start = formatter.parseDateTime(startdate.toString());
    DateTime end = formatter.parseDateTime(cbaseDate.toString());
    int months = Months.monthsBetween(start, end).getMonths();
    return months;
}
/**
 * 包左包右
 * @param fDate
 * @param oDate
 * @return
 */
public static int daysOfTwo(String fDate, String oDate) {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	 int day1 = 0;
	 int day2 = 0;
	try {
	Date btfDate;
		btfDate = sdf.parse(fDate.toString());
	
	Date btoDate = sdf.parse(oDate.toString());
    Calendar aCalendar = Calendar.getInstance();

    aCalendar.setTime(btfDate);

    day1 = aCalendar.get(Calendar.DAY_OF_YEAR);

    aCalendar.setTime(btoDate);

    day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
	} catch (ParseException e) {
		e.printStackTrace();
	}
    return (day2 - day1)+1;

 }

/**
2      * <li>功能描述：时间相减得到天数（包左不包右）
3      * @param beginDateStr
4      * @param endDateStr
5      * @return
6      * long 
7      * @author Administrator
8      */
     public static int getDaySub(String beginDateStr,String endDateStr)
     {
         int day=0;
         java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");    
         java.util.Date beginDate;
         java.util.Date endDate;
         try
         {
             beginDate = format.parse(beginDateStr);
             endDate= format.parse(endDateStr);    
             day=(int) ((endDate.getTime()-beginDate.getTime())/(24*60*60*1000));    
             //System.out.println("相隔的天数="+day);   
         } catch (ParseException e)
         {
             // TODO 自动生成 catch 块
             e.printStackTrace();
         }   
         return day;
     }
     
     /**
      * 日期加n天
      */
     public static String getcheckdate(String date, int i){
    	 SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd"); //字符串转换
    	 Date newdate = null;
		try {
			newdate = formatDate.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

    	 Calendar c = Calendar.getInstance();  

    	 c.setTime(newdate);  

    	 c.add(Calendar.DATE, i);  
    	 String dateday = formatDate.format(c.getTime());
    	  
    	  return dateday;
     }
     /**
      * 日期月份减n个月
      * @param cBaseDate
      * @param avgmoncount
      * @return
      */
     public static UFDate calendarclac(UFDate cBaseDate, String avgmoncount) {
    		UFDate startdate = null;
    		try {
    		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    		    Date dt = sdf.parse(cBaseDate.toString());

    		    Calendar rightNow = Calendar.getInstance();
    		    rightNow.setTime(dt);// 使用给定的 Date 设置此 Calendar 的时间。
    		    Integer avgmonday = Integer.parseInt(avgmoncount);
    		    rightNow.add(Calendar.MONTH, (0 - avgmonday));// 日期减avgmonday个月
    		    Date dt1 = rightNow.getTime();
    		    startdate = new UFDate(dt1);
    		} catch (ParseException e) {
    		    e.printStackTrace();
    		}
    		return startdate;
    	    }
}
