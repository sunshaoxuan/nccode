/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.ta.psncalendar;

import nc.hr.utils.ResHelper;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.ta.dailydata.IDailyData;
import nc.vo.ta.psndoc.TBMPsndocVO;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 * 在此处添加此类的描述信息
 * </p>
 * 创建日期:2010-08-25 10:36:13
 *
 * @author
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
public class PsnCalendarVO extends SuperVO implements IDailyData {

	public static String[] getWeekArray() {
		return new String[] { ResHelper.getString("common", "UC001-0000052")
				/* @res "星期日" */, ResHelper.getString("common", "UC001-0000046")
				/* @res "星期一" */, ResHelper.getString("common", "UC001-0000047")
				/* @res "星期二" */, ResHelper.getString("common", "UC001-0000048")
				/* @res "星期三" */, ResHelper.getString("common", "UC001-0000049")
				/* @res "星期四" */, ResHelper.getString("common", "UC001-0000050")
				/* @res "星期五" */, ResHelper.getString("common", "UC001-0000051")
				/* @res "星期六" */ };
	}

	public static String[] getSimpleWeekArray() {
		return new String[] { ResHelper.getString("6017basedoc", "06017basedoc1707")
				/* @res "周日" */, ResHelper.getString("6017basedoc", "06017basedoc1701")
				/* @res "周一" */, ResHelper.getString("6017basedoc", "06017basedoc1702")
				/* @res "周二" */, ResHelper.getString("6017basedoc", "06017basedoc1703")
				/* @res "周三" */, ResHelper.getString("6017basedoc", "06017basedoc1704")
				/* @res "周四" */, ResHelper.getString("6017basedoc", "06017basedoc1705")
				/* @res "周五" */, ResHelper.getString("6017basedoc", "06017basedoc1706")
				/* @res "周六" */ };
	}

	private java.lang.String pk_psncalendar;
	private java.lang.String pk_group;
	private java.lang.String pk_org;
	private java.lang.String pk_psndoc;
	String pk_psnjob;// 虚字段
	String pk_tbm_psndoc;// 虚字段
	private UFLiteralDate calendar;
	private String original_shift_b4exg;
	private String original_shift_b4cut;
	private java.lang.String pk_shift;// 班次主键
	private nc.vo.pub.lang.UFDouble gzsj;
	private nc.vo.pub.lang.UFBoolean if_rest;
	private nc.vo.pub.lang.UFBoolean cancelflag;
	private nc.vo.pub.lang.UFBoolean iswtrecreate;// 此字段的作用是：为Y,则需要从psnworktime表查询工作时间段，为N，则从tbm_wt表查询即可
	private java.lang.Integer day_type;
	private nc.vo.pub.lang.UFBoolean isflexiblefinal;
	private nc.vo.pub.lang.UFBoolean dataimportstatus;
	private nc.vo.pub.lang.UFBoolean datacreatestatus;
	private nc.vo.pub.lang.UFBoolean kqdatacostatus;
	private nc.vo.pub.lang.UFBoolean overtimedatacostatus;
	private nc.vo.pub.lang.UFBoolean leavedatacostatus;
	private nc.vo.pub.lang.UFBoolean awaydatacostatus;
	private nc.vo.pub.lang.UFBoolean issolidifywhencalculation = UFBoolean.FALSE;// 是否生成考勤数据时固化,如果生成timedata时，一个弹性班被固化了，则固化后的工作时间段存入工作日历子表，且此字段设置为Y.详见pdm
	private UFBoolean isfromteam;// 这天的排班是否来源于班组排班
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;
	private java.lang.String creator;
	private nc.vo.pub.lang.UFDateTime creationtime;
	private java.lang.String modifier;
	private nc.vo.pub.lang.UFDateTime modifiedtime;
	// ssx added for Taiwan
	private java.lang.String pk_shiftholidaytype;
	public static final String PK_SHIFTHOLIDAYTYPE = "pk_shiftholidaytype";

	public java.lang.String getPk_shiftholidaytype() {
		return pk_shiftholidaytype;
	}

	public void setPk_shiftholidaytype(java.lang.String pk_shiftholidaytype) {
		this.pk_shiftholidaytype = pk_shiftholidaytype;
	}

	private java.lang.Integer date_daytype;

	public static final String PK_PSNCALENDAR = "pk_psncalendar";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String PK_PSNDOC = "pk_psndoc";
	public static final String CALENDAR = "calendar";
	public static final String ORIGINAL_SHIFT_B4EXG = "original_shift_b4exg";
	public static final String ORIGINAL_SHIFT_B4CUT = "original_shift_b4cut";
	public static final String PK_SHIFT = "pk_shift";
	public static final String GZSJ = "gzsj";
	public static final String IF_REST = "if_rest";
	public static final String CANCELFLAG = "cancelflag";
	public static final String ISWTRECREATE = "iswtrecreate";
	public static final String DAY_TYPE = "day_type";
	public static final String DATE_DAYTYPE = "date_daytype";
	public static final String FINAL_LB_TYPE = "final_lb_type";
	public static final String DATAIMPORTSTATUS = "dataimportstatus";
	public static final String DATACREATESTATUS = "datacreatestatus";
	public static final String KQDATACOSTATUS = "kqdatacostatus";

	public java.lang.Integer getDate_daytype() {
		return date_daytype;
	}

	public void setDate_daytype(java.lang.Integer date_daytype) {
		this.date_daytype = date_daytype;
	}

	public static final String OVERTIMEDATACOSTATUS = "overtimedatacostatus";
	public static final String LEAVEDATACOSTATUS = "leavedatacostatus";
	public static final String AWAYDATACOSTATUS = "awaydatacostatus";
	public static final String ISSOLIDIFYWHENCALCULATION = "issolidifywhencalculation";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";

	private java.lang.String pk_org_v;// 虚字段，组织版本信息
	private java.lang.String pk_dept_v;// 虚字段，最新部门版本信息

	/**
	 * 属性pk_psncalendar的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return java.lang.String
	 */
	public java.lang.String getPk_psncalendar() {
		return pk_psncalendar;
	}

	/**
	 * 属性pk_psncalendar的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newPk_psncalendar
	 *            java.lang.String
	 */
	public void setPk_psncalendar(java.lang.String newPk_psncalendar) {
		this.pk_psncalendar = newPk_psncalendar;
	}

	/**
	 * 属性pk_group的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group() {
		return pk_group;
	}

	/**
	 * 属性pk_group的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newPk_group
	 *            java.lang.String
	 */
	public void setPk_group(java.lang.String newPk_group) {
		this.pk_group = newPk_group;
	}

	/**
	 * 属性pk_org的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getPk_org() {
		return pk_org;
	}

	/**
	 * 属性pk_org的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newPk_org
	 *            java.lang.String
	 */
	@Override
	public void setPk_org(java.lang.String newPk_org) {
		this.pk_org = newPk_org;
	}

	/**
	 * 属性pk_psndoc的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getPk_psndoc() {
		return pk_psndoc;
	}

	/**
	 * 属性pk_psndoc的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newPk_psndoc
	 *            java.lang.String
	 */
	@Override
	public void setPk_psndoc(java.lang.String newPk_psndoc) {
		this.pk_psndoc = newPk_psndoc;
	}

	/**
	 * 属性calendar的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return java.lang.String
	 */
	public UFLiteralDate getCalendar() {
		return calendar;
	}

	/**
	 * 属性calendar的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newCalendar
	 *            java.lang.String
	 */
	public void setCalendar(UFLiteralDate newCalendar) {
		this.calendar = newCalendar;
	}

	/**
	 * 属性original_class的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return java.lang.String
	 */
	public java.lang.String getOriginal_shift_b4exg() {
		return original_shift_b4exg;
	}

	/**
	 * 属性original_class的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newOriginal_class
	 *            java.lang.String
	 */
	public void setOriginal_shift_b4exg(java.lang.String newOriginal_class) {
		this.original_shift_b4exg = newOriginal_class;
	}

	public String getOriginal_shift_b4cut() {
		return original_shift_b4cut;
	}

	public void setOriginal_shift_b4cut(String originalClassB4cut) {
		original_shift_b4cut = originalClassB4cut;
	}

	/**
	 * 属性pk_shift的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return java.lang.String
	 */
	public java.lang.String getPk_shift() {
		return pk_shift;
	}

	/**
	 * 属性pk_shift的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newPk_shift
	 *            java.lang.String
	 */
	public void setPk_shift(java.lang.String newPk_shift) {
		this.pk_shift = newPk_shift;
	}

	/**
	 * 属性gzsj的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGzsj() {
		return gzsj;
	}

	/**
	 * 属性gzsj的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newGzsj
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setGzsj(nc.vo.pub.lang.UFDouble newGzsj) {
		this.gzsj = newGzsj;
	}

	/**
	 * 属性if_rest的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getIf_rest() {
		return if_rest;
	}

	/**
	 * 属性if_rest的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newIf_rest
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIf_rest(nc.vo.pub.lang.UFBoolean newIf_rest) {
		this.if_rest = newIf_rest;
	}

	/**
	 * 属性cancelflag的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getCancelflag() {
		return cancelflag;
	}

	/**
	 * 属性cancelflag的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newCancelflag
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setCancelflag(nc.vo.pub.lang.UFBoolean newCancelflag) {
		this.cancelflag = newCancelflag;
	}

	/**
	 * 属性iswtrecreate的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getIswtrecreate() {
		return iswtrecreate;
	}

	/**
	 * 属性iswtrecreate的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newIswtrecreate
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIswtrecreate(nc.vo.pub.lang.UFBoolean newIswtrecreate) {
		this.iswtrecreate = newIswtrecreate;
	}

	/**
	 * 属性day_type的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDay_type() {
		return day_type;
	}

	/**
	 * 属性day_type的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newDay_type
	 *            java.lang.Integer
	 */
	public void setDay_type(java.lang.Integer newDay_type) {
		this.day_type = newDay_type;
	}

	/**
	 * 属性final_lb_type的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return java.lang.Integer
	 */
	public nc.vo.pub.lang.UFBoolean getIsflexiblefinal() {
		return isflexiblefinal;
	}

	/**
	 * 属性final_lb_type的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newFinal_lb_type
	 *            java.lang.Integer
	 */
	public void setIsflexiblefinal(nc.vo.pub.lang.UFBoolean isflexiblefinal) {
		this.isflexiblefinal = isflexiblefinal;
	}

	/**
	 * 属性dataimportstatus的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getDataimportstatus() {
		return dataimportstatus;
	}

	/**
	 * 属性dataimportstatus的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newDataimportstatus
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setDataimportstatus(nc.vo.pub.lang.UFBoolean newDataimportstatus) {
		this.dataimportstatus = newDataimportstatus;
	}

	/**
	 * 属性datacreatestatus的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getDatacreatestatus() {
		return datacreatestatus;
	}

	/**
	 * 属性datacreatestatus的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newDatacreatestatus
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setDatacreatestatus(nc.vo.pub.lang.UFBoolean newDatacreatestatus) {
		this.datacreatestatus = newDatacreatestatus;
	}

	/**
	 * 属性kqdatacostatus的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getKqdatacostatus() {
		return kqdatacostatus;
	}

	/**
	 * 属性kqdatacostatus的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newKqdatacostatus
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setKqdatacostatus(nc.vo.pub.lang.UFBoolean newKqdatacostatus) {
		this.kqdatacostatus = newKqdatacostatus;
	}

	/**
	 * 属性overtimedatacostatus的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getOvertimedatacostatus() {
		return overtimedatacostatus;
	}

	/**
	 * 属性overtimedatacostatus的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newOvertimedatacostatus
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setOvertimedatacostatus(nc.vo.pub.lang.UFBoolean newOvertimedatacostatus) {
		this.overtimedatacostatus = newOvertimedatacostatus;
	}

	/**
	 * 属性leavedatacostatus的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getLeavedatacostatus() {
		return leavedatacostatus;
	}

	/**
	 * 属性leavedatacostatus的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newLeavedatacostatus
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setLeavedatacostatus(nc.vo.pub.lang.UFBoolean newLeavedatacostatus) {
		this.leavedatacostatus = newLeavedatacostatus;
	}

	/**
	 * 属性awaydatacostatus的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getAwaydatacostatus() {
		return awaydatacostatus;
	}

	/**
	 * 属性awaydatacostatus的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newAwaydatacostatus
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setAwaydatacostatus(nc.vo.pub.lang.UFBoolean newAwaydatacostatus) {
		this.awaydatacostatus = newAwaydatacostatus;
	}

	/**
	 * 属性dr的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr() {
		return dr;
	}

	/**
	 * 属性dr的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newDr
	 *            java.lang.Integer
	 */
	public void setDr(java.lang.Integer newDr) {
		this.dr = newDr;
	}

	/**
	 * 属性ts的Getter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs() {
		return ts;
	}

	/**
	 * 属性ts的Setter方法. 创建日期:2010-08-25 10:36:13
	 *
	 * @param newTs
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(nc.vo.pub.lang.UFDateTime newTs) {
		this.ts = newTs;
	}

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2010-08-25 10:36:13
	 *
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getParentPKFieldName() {
		return null;
	}

	/**
	 * <p>
	 * 取得表主键.
	 * <p>
	 * 创建日期:2010-08-25 10:36:13
	 *
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getPKFieldName() {
		return "pk_psncalendar";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2010-08-25 10:36:13
	 *
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getTableName() {
		return "tbm_psncalendar";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2010-08-25 10:36:13
	 *
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "tbm_psncalendar";
	}

	/**
	 * 按照默认方式创建构造子.
	 *
	 * 创建日期:2010-08-25 10:36:13
	 */
	public PsnCalendarVO() {
		super();
	}

	public java.lang.String getCreator() {
		return creator;
	}

	public void setCreator(java.lang.String creator) {
		this.creator = creator;
	}

	public nc.vo.pub.lang.UFDateTime getCreationtime() {
		return creationtime;
	}

	public void setCreationtime(nc.vo.pub.lang.UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	public java.lang.String getModifier() {
		return modifier;
	}

	public void setModifier(java.lang.String modifier) {
		this.modifier = modifier;
	}

	public nc.vo.pub.lang.UFDateTime getModifiedtime() {
		return modifiedtime;
	}

	public void setModifiedtime(nc.vo.pub.lang.UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	@Override
	public UFLiteralDate getDate() {
		return getCalendar();
	}

	public boolean isHolidayCancel() {
		return getCancelflag() != null && getCancelflag().booleanValue();
	}

	public boolean isRest() {
		return getIf_rest() != null && getIf_rest().booleanValue();
	}

	public boolean isWtRecreate() {
		return getIswtrecreate() != null && getIswtrecreate().booleanValue();
	}

	public boolean isFlexibleFinal() {
		return getIsflexiblefinal() != null && getIsflexiblefinal().booleanValue();
	}

	@Override
	public void setDate(UFLiteralDate date) {
		setCalendar(date);
	}

	public nc.vo.pub.lang.UFBoolean getIssolidifywhencalculation() {
		return issolidifywhencalculation;
	}

	public void setIssolidifywhencalculation(nc.vo.pub.lang.UFBoolean issolidifywhencalculation) {
		this.issolidifywhencalculation = issolidifywhencalculation;
	}

	@Override
	public String getPk_psnjob() {
		return pk_psnjob;
	}

	@Override
	public String getPk_tbm_psndoc() {
		return pk_tbm_psndoc;
	}

	@Override
	public void setPk_psnjob(String pk_psnjob) {
		this.pk_psnjob = pk_psnjob;

	}

	@Override
	public void setPk_tbm_psndoc(String pk_tbm_psndoc) {
		this.pk_tbm_psndoc = pk_tbm_psndoc;

	}

	@Override
	public TBMPsndocVO toTBMPsndocVO() {
		TBMPsndocVO vo = new TBMPsndocVO();
		vo.setPrimaryKey(pk_tbm_psndoc);
		vo.setPk_psndoc(pk_psndoc);
		vo.setPk_psnjob(pk_psnjob);
		return vo;
	}

	public UFBoolean getIsfromteam() {
		return isfromteam;
	}

	public void setIsfromteam(UFBoolean isfromteam) {
		this.isfromteam = isfromteam;
	}

	@Override
	public java.lang.String getPk_org_v() {
		return pk_org_v;
	}

	@Override
	public void setPk_org_v(java.lang.String pkOrgV) {
		pk_org_v = pkOrgV;
	}

	@Override
	public java.lang.String getPk_dept_v() {
		return pk_dept_v;
	}

	@Override
	public void setPk_dept_v(java.lang.String pkDeptV) {
		pk_dept_v = pkDeptV;
	}
}
