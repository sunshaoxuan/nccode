/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.ta.daystat;
	
import nc.vo.pub.*;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.annotation.FKColumn;
import nc.vo.ta.annotation.Table;
import nc.vo.ta.annotation.UniqueColumns;
import nc.vo.ta.statistic.IStatb;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:2010-09-30 13:58:34
 * @author 
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
@Table(tableName="tbm_daystatb_notcurrmonth")
@FKColumn(fkColumn="pk_daystat")
public class DaystatbNotCurrmonthVO extends SuperVO implements IStatb{
	

	private java.lang.String pk_daystatb_notcurrmonth;
	private java.lang.String pk_daystat;
	private java.lang.String pk_group;
	private java.lang.String pk_org;
	private java.lang.Integer type;
	private java.lang.String pk_timeitem;
	private nc.vo.pub.lang.UFDouble hournum=UFDouble.ZERO_DBL;
	private nc.vo.pub.lang.UFDouble toresthour;
	
	private java.lang.Integer timeitemunit;//1：天；2：小时
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;
	private java.lang.String pk_billsource;
	private UFLiteralDate calendar;
	private UFLiteralDate approve_date;
	
	
	public static final String PK_DAYSTATB = "pk_daystatb";
	public static final String PK_DAYSTAT = "pk_daystat";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String TYPE = "type";
	public static final String PK_TIMEITEM = "pk_timeitem";
	public static final String HOURNUM = "hournum";
	public static final String ORIHOURNUM = "orihournum";
	public static final String TORESTHOUR = "toresthour";
	public static final String TIMEITEMUNIT = "timeitemunit";
			
	
	
	public java.lang.String getPk_daystatb_notcurrmonth() {
		return pk_daystatb_notcurrmonth;
	}
	public void setPk_daystatb_notcurrmonth(
			java.lang.String pk_daystatb_notcurrmonth) {
		this.pk_daystatb_notcurrmonth = pk_daystatb_notcurrmonth;
	}
	public java.lang.String getPk_daystat() {
		return pk_daystat;
	}
	public void setPk_daystat(java.lang.String pk_daystat) {
		this.pk_daystat = pk_daystat;
	}
	public java.lang.String getPk_group() {
		return pk_group;
	}
	public void setPk_group(java.lang.String pk_group) {
		this.pk_group = pk_group;
	}
	public java.lang.String getPk_org() {
		return pk_org;
	}
	public void setPk_org(java.lang.String pk_org) {
		this.pk_org = pk_org;
	}
	public java.lang.Integer getType() {
		return type;
	}
	public void setType(java.lang.Integer type) {
		this.type = type;
	}
	public java.lang.String getPk_timeitem() {
		return pk_timeitem;
	}
	public void setPk_timeitem(java.lang.String pk_timeitem) {
		this.pk_timeitem = pk_timeitem;
	}
	public nc.vo.pub.lang.UFDouble getHournum() {
		return hournum;
	}
	public void setHournum(nc.vo.pub.lang.UFDouble hournum) {
		this.hournum = hournum;
	}
	public nc.vo.pub.lang.UFDouble getToresthour() {
		return toresthour;
	}
	public void setToresthour(nc.vo.pub.lang.UFDouble toresthour) {
		this.toresthour = toresthour;
	}
	public java.lang.Integer getTimeitemunit() {
		return timeitemunit;
	}
	public void setTimeitemunit(java.lang.Integer timeitemunit) {
		this.timeitemunit = timeitemunit;
	}
	public java.lang.String getPk_billsource() {
		return pk_billsource;
	}
	public void setPk_billsource(java.lang.String pk_billsource) {
		this.pk_billsource = pk_billsource;
	}
	public UFLiteralDate getCalendar() {
		return calendar;
	}
	public void setCalendar(UFLiteralDate calendar) {
		this.calendar = calendar;
	}
	public UFLiteralDate getApprove_date() {
		return approve_date;
	}
	public void setApprove_date(UFLiteralDate approve_date) {
		this.approve_date = approve_date;
	}
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2010-09-30 13:58:34
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2010-09-30 13:58:34
	 * @param newDr java.lang.Integer
	 */
	public void setDr (java.lang.Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2010-09-30 13:58:34
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2010-09-30 13:58:34
	 * @param newTs nc.vo.pub.lang.UFDateTime
	 */
	public void setTs (nc.vo.pub.lang.UFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2010-09-30 13:58:34
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2010-09-30 13:58:34
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
	  return "pk_daystatb_notcurrmonth";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2010-09-30 13:58:34
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "tbm_daystatb_notcurrmonth";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2010-09-30 13:58:34
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "tbm_daystatb_notcurrmonth";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2010-09-30 13:58:34
	  */
     public DaystatbNotCurrmonthVO() {
		super();	
	}

	public String getFk() {
		return pk_daystat;
	}

	public void setFk(String fk) {
		pk_daystat=fk;
	}
	
	
} 


