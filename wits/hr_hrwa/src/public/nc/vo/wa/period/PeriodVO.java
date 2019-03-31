/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.wa.period;

import nc.vo.pub.SuperVO;
/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:2009-11-10 09:36:11
 * @author
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
public class PeriodVO extends SuperVO {
	private static final long serialVersionUID = -6390663631762768137L;
	private java.lang.String pk_wa_period;
	private java.lang.String pk_periodscheme;
	private java.lang.String cyear;
	private java.lang.String cperiod;
	private nc.vo.pub.lang.UFLiteralDate  cstartdate;
	private nc.vo.pub.lang.UFLiteralDate  cenddate;
	private java.lang.String vcalyear;
	private java.lang.String vcalmonth;
	private java.lang.String caccyear;
	private java.lang.String caccperiod;
// {MOD:新个税补丁}
// begin
	private java.lang.String taxyear;
	private java.lang.String taxperiod;
// end
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;
	private java.lang.String classid;

	public static final String PK_WA_PERIOD = "pk_wa_period";
	public static final String PK_PERIODSCHEME = "pk_periodscheme";
	public static final String CYEAR = "cyear";
	public static final String CPERIOD = "cperiod";
	public static final String CSTARTDATE = "cstartdate";
	public static final String CENDDATE = "cenddate";
	public static final String VCALYEAR = "vcalyear";
	public static final String VCALMONTH = "vcalmonth";
	public static final String CACCYEAR = "caccyear";
	public static final String CACCPERIOD = "caccperiod";
	public static final String CLASSID = "classid";
	// {MOD:新个税补丁}
	// begin
	public static final String TAXYEAR = "taxyear";
	public static final String TAXPERIOD = "taxperiod";
	// end

	/**
	 * 属性pk_wa_period的Getter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @return java.lang.String
	 */
	public java.lang.String getPk_wa_period () {
		return pk_wa_period;
	}
	/**
	 * 属性pk_wa_period的Setter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @param newPk_wa_period java.lang.String
	 */
	public void setPk_wa_period (java.lang.String newPk_wa_period ) {
		this.pk_wa_period = newPk_wa_period;
	}
	/**
	 * 属性pk_periodscheme的Getter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @return java.lang.String
	 */
	public java.lang.String getPk_periodscheme () {
		return pk_periodscheme;
	}
	/**
	 * 属性pk_periodscheme的Setter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @param newPk_periodscheme java.lang.String
	 */
	public void setPk_periodscheme (java.lang.String newPk_periodscheme ) {
		this.pk_periodscheme = newPk_periodscheme;
	}
	/**
	 * 属性cyear的Getter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @return java.lang.String
	 */
	public java.lang.String getCyear () {
		return cyear;
	}
	/**
	 * 属性cyear的Setter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @param newCyear java.lang.String
	 */
	public void setCyear (java.lang.String newCyear ) {
		this.cyear = newCyear;
	}
	/**
	 * 属性cperiod的Getter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @return java.lang.String
	 */
	public java.lang.String getCperiod () {
		return cperiod;
	}
	/**
	 * 属性cperiod的Setter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @param newCperiod java.lang.String
	 */
	public void setCperiod (java.lang.String newCperiod ) {
		this.cperiod = newCperiod;
	}
	/**
	 * 属性cstartdate的Getter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFLiteralDate  getCstartdate () {
		return cstartdate;
	}
	/**
	 * 属性cstartdate的Setter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @param newCstartdate nc.vo.pub.lang.UFDate
	 */
	public void setCstartdate (nc.vo.pub.lang.UFLiteralDate  newCstartdate ) {
		this.cstartdate = newCstartdate;
	}
	/**
	 * 属性cenddate的Getter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFLiteralDate  getCenddate () {
		return cenddate;
	}
	/**
	 * 属性cenddate的Setter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @param newCenddate nc.vo.pub.lang.UFDate
	 */
	public void setCenddate (nc.vo.pub.lang.UFLiteralDate  newCenddate ) {
		this.cenddate = newCenddate;
	}
	/**
	 * 属性vcalyear的Getter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @return java.lang.String
	 */
	public java.lang.String getVcalyear () {
		return vcalyear;
	}
	/**
	 * 属性vcalyear的Setter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @param newVcalyear java.lang.String
	 */
	public void setVcalyear (java.lang.String newVcalyear ) {
		this.vcalyear = newVcalyear;
	}
	/**
	 * 属性vcalmonth的Getter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @return java.lang.String
	 */
	public java.lang.String getVcalmonth () {
		return vcalmonth;
	}
	/**
	 * 属性vcalmonth的Setter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @param newVcalmonth java.lang.String
	 */
	public void setVcalmonth (java.lang.String newVcalmonth ) {
		this.vcalmonth = newVcalmonth;
	}
	/**
	 * 属性caccyear的Getter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @return java.lang.String
	 */
	public java.lang.String getCaccyear () {
		return caccyear;
	}
	/**
	 * 属性caccyear的Setter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @param newCaccyear java.lang.String
	 */
	public void setCaccyear (java.lang.String newCaccyear ) {
		this.caccyear = newCaccyear;
	}
	/**
	 * 属性caccperiod的Getter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @return java.lang.String
	 */
	public java.lang.String getCaccperiod () {
		return caccperiod;
	}
	/**
	 * 属性caccperiod的Setter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @param newCaccperiod java.lang.String
	 */
	public void setCaccperiod (java.lang.String newCaccperiod ) {
		this.caccperiod = newCaccperiod;
	}
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr () {
		return dr;
	}
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @param newDr java.lang.Integer
	 */
	public void setDr (java.lang.Integer newDr ) {
		this.dr = newDr;
	}
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs () {
		return ts;
	}
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:2009-11-10 09:36:11
	 * @param newTs nc.vo.pub.lang.UFDateTime
	 */
	public void setTs (nc.vo.pub.lang.UFDateTime newTs ) {
		this.ts = newTs;
	}

	/**
	 * <p>取得父VO主键字段.
	 * <p>
	 * 创建日期:2009-11-10 09:36:11
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getParentPKFieldName() {
		return null;
	}

	/**
	 * <p>取得表主键.
	 * <p>
	 * 创建日期:2009-11-10 09:36:11
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getPKFieldName() {
		return "pk_wa_period";
	}

	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2009-11-10 09:36:11
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getTableName() {
		return "wa_period";
	}

	/**
	 * 按照默认方式创建构造子.
	 *
	 * 创建日期:2009-11-10 09:36:11
	 */
	public PeriodVO() {
		super();
	}

	public void setClassid(java.lang.String classid) {
		this.classid = classid;
	}

	public java.lang.String getClassid() {
		return classid;
	}

	// {MOD:新个税补丁}
	// begin
	public java.lang.String getTaxyear() {
		return taxyear;
	}
	public void setTaxyear(java.lang.String taxyear) {
		this.taxyear = taxyear;
	}
	public java.lang.String getTaxperiod() {
		return taxperiod;
	}
	public void setTaxperiod(java.lang.String taxperiod) {
		this.taxperiod = taxperiod;
	}
	// end
	public static java.lang.String getDefaultTableName() {
		return "wa_period";
	}
}


