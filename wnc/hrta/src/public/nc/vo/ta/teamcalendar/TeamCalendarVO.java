/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.ta.teamcalendar;

import nc.vo.pub.*;
import nc.vo.pub.lang.UFBoolean;

@SuppressWarnings("serial")
public class TeamCalendarVO extends SuperVO {
	private java.lang.String pk_teamcalendar;
	private java.lang.String pk_group;
	private java.lang.String pk_org;
	private java.lang.String pk_team;
	private nc.vo.pub.lang.UFLiteralDate calendar;
	private java.lang.String original_shift_b4exg;
	private java.lang.String original_shift_b4cut;
	private java.lang.String pk_shift;
	private nc.vo.pub.lang.UFDouble gzsj;
	private nc.vo.pub.lang.UFBoolean if_rest;
	private nc.vo.pub.lang.UFBoolean cancelflag;
	private nc.vo.pub.lang.UFBoolean iswtrecreate;
	private nc.vo.pub.lang.UFBoolean isflexiblefinal;
	private nc.vo.pub.lang.UFBoolean ismanuctrl = UFBoolean.FALSE;
	private java.lang.String cwkid;
	private java.lang.String vnote;
	private java.lang.String pk_org_v;
	private java.lang.String creator;
	private nc.vo.pub.lang.UFDateTime creationtime;
	private java.lang.String modifier;
	private nc.vo.pub.lang.UFDateTime modifiedtime;
	private java.lang.Integer dr = 0;

	private nc.vo.pub.lang.UFDateTime ts;
	private java.lang.Integer date_daytype;

	public static final String PK_TEAMCALENDAR = "pk_teamcalendar";

	public static final String PK_GROUP = "pk_group";

	public static final String PK_ORG = "pk_org";

	public static final String PK_TEAM = "pk_team";
	public static final String CALENDAR = "calendar";
	public static final String ORIGINAL_SHIFT_B4EXG = "original_shift_b4exg";
	public static final String ORIGINAL_SHIFT_B4CUT = "original_shift_b4cut";
	public static final String PK_SHIFT = "pk_shift";
	public static final String GZSJ = "gzsj";
	public static final String IF_REST = "if_rest";
	public static final String CANCELFLAG = "cancelflag";
	public static final String ISWTRECREATE = "iswtrecreate";
	public static final String ISFLEXIBLEFINAL = "isflexiblefinal";
	public static final String ISMANUCTRL = "ismanuctrl";
	public static final String CWKID = "cwkid";
	public static final String VNOTE = "vnote";
	public static final String PK_ORG_V = "pk_org_v";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String DATE_DAYTYPE = "date_daytype";

	/**
	 * 属性day_daytype的Getter方法.属性名：日期类型
	 * 创建日期:
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDate_daytype() {
		return date_daytype;
	}   
	public void setDate_daytype(java.lang.Integer date_daytype) {
		this.date_daytype = date_daytype;
	}
	/**
	 * 属性day_daytype的Setter方法.属性名：日期类型
	 * 创建日期:
	 * @param newDay_daytype java.lang.Integer
	 */
	public java.lang.String getPk_teamcalendar () {
		return pk_teamcalendar;
	}

	public void setPk_teamcalendar (java.lang.String newPk_teamcalendar ) {
	 	this.pk_teamcalendar = newPk_teamcalendar;
	}

	public java.lang.String getPk_group () {
		return pk_group;
	}

	public void setPk_group (java.lang.String newPk_group ) {
	 	this.pk_group = newPk_group;
	}

	public java.lang.String getPk_org () {
		return pk_org;
	}

	public void setPk_org (java.lang.String newPk_org ) {
	 	this.pk_org = newPk_org;
	}

	public java.lang.String getPk_team () {
		return pk_team;
	}

	public void setPk_team (java.lang.String newPk_team ) {
	 	this.pk_team = newPk_team;
	}

	public nc.vo.pub.lang.UFLiteralDate getCalendar () {
		return calendar;
	}

	public void setCalendar (nc.vo.pub.lang.UFLiteralDate newCalendar ) {
	 	this.calendar = newCalendar;
	}

	public java.lang.String getOriginal_shift_b4exg () {
		return original_shift_b4exg;
	}

	public void setOriginal_shift_b4exg (java.lang.String newOriginal_shift_b4exg ) {
	 	this.original_shift_b4exg = newOriginal_shift_b4exg;
	}

	public java.lang.String getOriginal_shift_b4cut () {
		return original_shift_b4cut;
	}

	public void setOriginal_shift_b4cut (java.lang.String newOriginal_shift_b4cut ) {
	 	this.original_shift_b4cut = newOriginal_shift_b4cut;
	}

	public java.lang.String getPk_shift () {
		return pk_shift;
	}

	public void setPk_shift (java.lang.String newPk_shift ) {
	 	this.pk_shift = newPk_shift;
	}

	public nc.vo.pub.lang.UFDouble getGzsj () {
		return gzsj;
	}

	public void setGzsj (nc.vo.pub.lang.UFDouble newGzsj ) {
	 	this.gzsj = newGzsj;
	}

	public nc.vo.pub.lang.UFBoolean getIf_rest () {
		return if_rest;
	}

	public void setIf_rest (nc.vo.pub.lang.UFBoolean newIf_rest ) {
	 	this.if_rest = newIf_rest;
	}

	public nc.vo.pub.lang.UFBoolean getCancelflag () {
		return cancelflag;
	}

	public void setCancelflag (nc.vo.pub.lang.UFBoolean newCancelflag ) {
	 	this.cancelflag = newCancelflag;
	}

	public nc.vo.pub.lang.UFBoolean getIswtrecreate () {
		return iswtrecreate;
	}

	public void setIswtrecreate (nc.vo.pub.lang.UFBoolean newIswtrecreate ) {
	 	this.iswtrecreate = newIswtrecreate;
	}

	public nc.vo.pub.lang.UFBoolean getIsflexiblefinal () {
		return isflexiblefinal;
	}

	public void setIsflexiblefinal (nc.vo.pub.lang.UFBoolean newIsflexiblefinal ) {
	 	this.isflexiblefinal = newIsflexiblefinal;
	}

	public nc.vo.pub.lang.UFBoolean getIsmanuctrl() {
		return ismanuctrl;
	}

	public void setIsmanuctrl(nc.vo.pub.lang.UFBoolean ishrctrl) {
		this.ismanuctrl = ishrctrl;
	}

	public java.lang.String getCwkid() {
		return cwkid;
	}

	public void setCwkid(java.lang.String cwkid) {
		this.cwkid = cwkid;
	}

	public java.lang.String getVnote() {
		return vnote;
	}

	public void setVnote(java.lang.String vnote) {
		this.vnote = vnote;
	}

	public java.lang.String getPk_org_v() {
		return pk_org_v;
	}

	public void setPk_org_v(java.lang.String pk_org_v) {
		this.pk_org_v = pk_org_v;
	}

	public java.lang.String getCreator () {
		return creator;
	}

	public void setCreator (java.lang.String newCreator ) {
	 	this.creator = newCreator;
	}

	public nc.vo.pub.lang.UFDateTime getCreationtime () {
		return creationtime;
	}

	public void setCreationtime (nc.vo.pub.lang.UFDateTime newCreationtime ) {
	 	this.creationtime = newCreationtime;
	}

	public java.lang.String getModifier () {
		return modifier;
	}

	public void setModifier (java.lang.String newModifier ) {
	 	this.modifier = newModifier;
	}

	public nc.vo.pub.lang.UFDateTime getModifiedtime () {
		return modifiedtime;
	}

	public void setModifiedtime (nc.vo.pub.lang.UFDateTime newModifiedtime ) {
	 	this.modifiedtime = newModifiedtime;
	}

	public java.lang.Integer getDr () {
		return dr;
	}

	public void setDr (java.lang.Integer newDr ) {
	 	this.dr = newDr;
	}

	public nc.vo.pub.lang.UFDateTime getTs () {
		return ts;
	}

	public void setTs (nc.vo.pub.lang.UFDateTime newTs ) {
	 	this.ts = newTs;
	}

	public java.lang.String getParentPKFieldName() {
		return null;
	}

	public java.lang.String getPKFieldName() {
		return "pk_teamcalendar";
	}

	public java.lang.String getTableName() {
		return "bd_teamcalendar";
	}

	public static java.lang.String getDefaultTableName() {
		return "bd_teamcalendar";
	}
     public TeamCalendarVO() {
		super();	
	}    
	public boolean isHolidayCancel() {
 		return getCancelflag()!=null&&getCancelflag().booleanValue();
	}

	public boolean isWTRcreate() {
    	return getIswtrecreate()!=null&&getIswtrecreate().booleanValue();
	}

	public boolean isManuCtrl() {
    	return getIsmanuctrl()!=null&&getIsmanuctrl().booleanValue();
	}
}