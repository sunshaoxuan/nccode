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
	 * ��λ�ȡ����ʱ���֮���غϵĲ���
	 * @date 2018��8��22��
	 */

public static String getAlphalDate(String startdate, String cbaseDate, String begindate, String enddate) {
	
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long sblong=0;
		long eblong=0;
		try {
			// ��׼ʱ��
			Date st = sdf.parse(startdate.toString());
			Date ed = sdf.parse(cbaseDate.toString());
			// Ŀ��ʱ��
			Date bt = sdf.parse(begindate.toString());
			Date ot = sdf.parse(enddate.toString());
			
			
			long btlong = Math.min(bt.getTime(), ot.getTime());// ��ʼʱ��
			long otlong = Math.max(bt.getTime(), ot.getTime());// ����ʱ��
			long stlong = Math.min(st.getTime(), ed.getTime());// ��ʼʱ��
			long edlong = Math.max(st.getTime(), ed.getTime());// ����ʱ��
			
			
			// �����㷨����
			// ���ȿ��Ƿ��а�����ϵ
		
			if ((stlong >= btlong && stlong <= otlong) || (edlong >= btlong && edlong <= otlong) || stlong<= btlong && edlong >= otlong) {
			// һ�����ص�����
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
		
		

//������һ�죬����Ĳ�����һ��int���͵�������·�
public Date getMonthEndTime(int year, int month) throws ParseException {
    //ѡ���·ݵ����һ��
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1);
    int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

    //��ʽ�����ڣ�����Ҫʱ����Ļ�����ȥ��
    String endDateStr = year + "-" + month + "-" + lastDay ;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Date endDate = df.parse(endDateStr);

    return endDate;
}

//�������ڵĲ����·�
public int getmonths(UFDate startdate, UFDate cbaseDate){
	DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    DateTime start = formatter.parseDateTime(startdate.toString());
    DateTime end = formatter.parseDateTime(cbaseDate.toString());
    int months = Months.monthsBetween(start, end).getMonths();
    return months;
}
/**
 * �������
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
2      * <li>����������ʱ������õ����������󲻰��ң�
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
             //System.out.println("���������="+day);   
         } catch (ParseException e)
         {
             // TODO �Զ����� catch ��
             e.printStackTrace();
         }   
         return day;
     }
     
     /**
      * ���ڼ�n��
      */
     public static String getcheckdate(String date, int i){
    	 SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd"); //�ַ���ת��
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
      * �����·ݼ�n����
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
    		    rightNow.setTime(dt);// ʹ�ø����� Date ���ô� Calendar ��ʱ�䡣
    		    Integer avgmonday = Integer.parseInt(avgmoncount);
    		    rightNow.add(Calendar.MONTH, (0 - avgmonday));// ���ڼ�avgmonday����
    		    Date dt1 = rightNow.getTime();
    		    startdate = new UFDate(dt1);
    		} catch (ParseException e) {
    		    e.printStackTrace();
    		}
    		return startdate;
    	    }
}