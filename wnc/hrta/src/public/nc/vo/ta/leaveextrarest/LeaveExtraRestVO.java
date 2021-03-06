package nc.vo.ta.leaveextrarest;

import nc.vo.pub.IVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此處簡要描述此類功能 </b>
 * <p>
 * 此處添加類的描述信息
 * </p>
 * 創建日期:2019/1/5
 * 
 * @author
 * @version NCPrj ??
 */
public class LeaveExtraRestVO extends nc.vo.pub.SuperVO {

	private java.lang.String pk_extrarest;
	private java.lang.String pk_psndoc;
	private java.lang.String pk_org_v;
	private java.lang.String pk_dept_v;
	private java.lang.String pk_group;
	private java.lang.String pk_org;
	private nc.vo.pub.lang.UFLiteralDate billdate;
	private nc.vo.pub.lang.UFLiteralDate datebeforechange;
	private java.lang.Integer typebeforechange;
	private nc.vo.pub.lang.UFLiteralDate dateafterchange;
	private java.lang.Integer typeafterchange;
	private java.lang.Integer changetype;
	private nc.vo.pub.lang.UFDouble changedayorhour;
	private nc.vo.pub.lang.UFLiteralDate expiredate;
	private nc.vo.pub.lang.UFBoolean issettled;
	private nc.vo.pub.lang.UFLiteralDate settledate;
	private java.lang.String settleyearmonth;
	private java.lang.String creator;
	private nc.vo.pub.lang.UFDateTime creationtime;
	private java.lang.String modifier;
	private nc.vo.pub.lang.UFDateTime modifiedtime;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;
	private String cyear;

	public static final String PK_EXTRAREST = "pk_extrarest";
	public static final String PK_PSNDOC = "pk_psndoc";
	public static final String PK_ORG_V = "pk_org_v";
	public static final String PK_DEPT_V = "pk_dept_v";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String BILLDATE = "billdate";
	public static final String DATEBEFORECHANGE = "datebeforechange";
	public static final String TYPEBEFORECHANGE = "typebeforechange";
	public static final String DATEAFTERCHANGE = "dateafterchange";
	public static final String TYPEAFTERCHANGE = "typeafterchange";
	public static final String CHANGETYPE = "changetype";
	public static final String CHANGEDAYORHOUR = "changedayorhour";
	public static final String EXPIREDATE = "expiredate";
	public static final String ISSETTLED = "issettled";
	public static final String SETTLEDATE = "settledate";
	public static final String SETTLEYEARMONTH = "settleyearmonth";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String CYEAR = "cyear";

	/**
	 * 屬性 pk_extrarest的Getter方法.屬性名：外加補休天數主鍵 創建日期:2019/1/5
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_extrarest() {
		return pk_extrarest;
	}

	/**
	 * 屬性pk_extrarest的Setter方法.屬性名：外加補休天數主鍵 創建日期:2019/1/5
	 * 
	 * @param newPk_extrarest
	 *            java.lang.String
	 */
	public void setPk_extrarest(java.lang.String newPk_extrarest) {
		this.pk_extrarest = newPk_extrarest;
	}

	/**
	 * 屬性 pk_psndoc的Getter方法.屬性名：人員基本信息 創建日期:2019/1/5
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_psndoc() {
		return pk_psndoc;
	}

	/**
	 * 屬性pk_psndoc的Setter方法.屬性名：人員基本信息 創建日期:2019/1/5
	 * 
	 * @param newPk_psndoc
	 *            java.lang.String
	 */
	public void setPk_psndoc(java.lang.String newPk_psndoc) {
		this.pk_psndoc = newPk_psndoc;
	}

	/**
	 * 屬性 pk_org_v的Getter方法.屬性名：人員組織版本 創建日期:2019/1/5
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org_v() {
		return pk_org_v;
	}

	/**
	 * 屬性pk_org_v的Setter方法.屬性名：人員組織版本 創建日期:2019/1/5
	 * 
	 * @param newPk_org_v
	 *            java.lang.String
	 */
	public void setPk_org_v(java.lang.String newPk_org_v) {
		this.pk_org_v = newPk_org_v;
	}

	/**
	 * 屬性 pk_dept_v的Getter方法.屬性名：人員部門版本 創建日期:2019/1/5
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_dept_v() {
		return pk_dept_v;
	}

	/**
	 * 屬性pk_dept_v的Setter方法.屬性名：人員部門版本 創建日期:2019/1/5
	 * 
	 * @param newPk_dept_v
	 *            java.lang.String
	 */
	public void setPk_dept_v(java.lang.String newPk_dept_v) {
		this.pk_dept_v = newPk_dept_v;
	}

	/**
	 * 屬性 pk_group的Getter方法.屬性名：集團主鍵 創建日期:2019/1/5
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group() {
		return pk_group;
	}

	/**
	 * 屬性pk_group的Setter方法.屬性名：集團主鍵 創建日期:2019/1/5
	 * 
	 * @param newPk_group
	 *            java.lang.String
	 */
	public void setPk_group(java.lang.String newPk_group) {
		this.pk_group = newPk_group;
	}

	/**
	 * 屬性 pk_org的Getter方法.屬性名：組織主鍵 創建日期:2019/1/5
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org() {
		return pk_org;
	}

	/**
	 * 屬性pk_org的Setter方法.屬性名：組織主鍵 創建日期:2019/1/5
	 * 
	 * @param newPk_org
	 *            java.lang.String
	 */
	public void setPk_org(java.lang.String newPk_org) {
		this.pk_org = newPk_org;
	}

	/**
	 * 屬性 billdate的Getter方法.屬性名：變動日期 創建日期:2019/1/5
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public nc.vo.pub.lang.UFLiteralDate getBilldate() {
		return billdate;
	}

	/**
	 * 屬性billdate的Setter方法.屬性名：變動日期 創建日期:2019/1/5
	 * 
	 * @param newBilldate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setBilldate(nc.vo.pub.lang.UFLiteralDate newBilldate) {
		this.billdate = newBilldate;
	}

	/**
	 * 屬性 datebeforechange的Getter方法.屬性名：變動前日期 創建日期:2019/1/5
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public nc.vo.pub.lang.UFLiteralDate getDatebeforechange() {
		return datebeforechange;
	}

	/**
	 * 屬性datebeforechange的Setter方法.屬性名：變動前日期 創建日期:2019/1/5
	 * 
	 * @param newDatebeforechange
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setDatebeforechange(nc.vo.pub.lang.UFLiteralDate newDatebeforechange) {
		this.datebeforechange = newDatebeforechange;
	}

	/**
	 * 屬性 typebeforechange的Getter方法.屬性名：變動前日期類型 創建日期:2019/1/5
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getTypebeforechange() {
		return typebeforechange;
	}

	/**
	 * 屬性typebeforechange的Setter方法.屬性名：變動前日期類型 創建日期:2019/1/5
	 * 
	 * @param newTypebeforechange
	 *            java.lang.Integer
	 */
	public void setTypebeforechange(java.lang.Integer newTypebeforechange) {
		this.typebeforechange = newTypebeforechange;
	}

	/**
	 * 屬性 dateafterchange的Getter方法.屬性名：變動後日期 創建日期:2019/1/5
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public nc.vo.pub.lang.UFLiteralDate getDateafterchange() {
		return dateafterchange;
	}

	/**
	 * 屬性dateafterchange的Setter方法.屬性名：變動後日期 創建日期:2019/1/5
	 * 
	 * @param newDateafterchange
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setDateafterchange(nc.vo.pub.lang.UFLiteralDate newDateafterchange) {
		this.dateafterchange = newDateafterchange;
	}

	/**
	 * 屬性 typeafterchange的Getter方法.屬性名：變動後日期類型 創建日期:2019/1/5
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getTypeafterchange() {
		return typeafterchange;
	}

	/**
	 * 屬性typeafterchange的Setter方法.屬性名：變動後日期類型 創建日期:2019/1/5
	 * 
	 * @param newTypeafterchange
	 *            java.lang.Integer
	 */
	public void setTypeafterchange(java.lang.Integer newTypeafterchange) {
		this.typeafterchange = newTypeafterchange;
	}

	/**
	 * 屬性 changetype的Getter方法.屬性名：補休產生類型 創建日期:2019/1/5
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getChangetype() {
		return changetype;
	}

	/**
	 * 屬性changetype的Setter方法.屬性名：補休產生類型 創建日期:2019/1/5
	 * 
	 * @param newChangetype
	 *            java.lang.Integer
	 */
	public void setChangetype(java.lang.Integer newChangetype) {
		this.changetype = newChangetype;
	}

	/**
	 * 屬性 changedayorhour的Getter方法.屬性名：變動外加補休時天數 創建日期:2019/1/5
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getChangedayorhour() {
		return changedayorhour;
	}

	/**
	 * 屬性changedayorhour的Setter方法.屬性名：變動外加補休時天數 創建日期:2019/1/5
	 * 
	 * @param newChangedayorhour
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setChangedayorhour(nc.vo.pub.lang.UFDouble newChangedayorhour) {
		this.changedayorhour = newChangedayorhour;
	}

	/**
	 * 屬性 expiredate的Getter方法.屬性名：到期日 創建日期:2019/1/5
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public nc.vo.pub.lang.UFLiteralDate getExpiredate() {
		return expiredate;
	}

	/**
	 * 屬性expiredate的Setter方法.屬性名：到期日 創建日期:2019/1/5
	 * 
	 * @param newExpiredate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setExpiredate(nc.vo.pub.lang.UFLiteralDate newExpiredate) {
		this.expiredate = newExpiredate;
	}

	/**
	 * 屬性 issettled的Getter方法.屬性名：是否結算 創建日期:2019/1/5
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getIssettled() {
		return issettled;
	}

	/**
	 * 屬性issettled的Setter方法.屬性名：是否結算 創建日期:2019/1/5
	 * 
	 * @param newIssettled
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIssettled(nc.vo.pub.lang.UFBoolean newIssettled) {
		this.issettled = newIssettled;
	}

	/**
	 * 屬性 settledate的Getter方法.屬性名：結算日期 創建日期:2019/1/5
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public nc.vo.pub.lang.UFLiteralDate getSettledate() {
		return settledate;
	}

	/**
	 * 屬性settledate的Setter方法.屬性名：結算日期 創建日期:2019/1/5
	 * 
	 * @param newSettledate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setSettledate(nc.vo.pub.lang.UFLiteralDate newSettledate) {
		this.settledate = newSettledate;
	}

	/**
	 * 屬性 settleyearmonth的Getter方法.屬性名：結算年月 創建日期:2019/1/5
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getSettleyearmonth() {
		return settleyearmonth;
	}

	/**
	 * 屬性settleyearmonth的Setter方法.屬性名：結算年月 創建日期:2019/1/5
	 * 
	 * @param newSettleyearmonth
	 *            java.lang.String
	 */
	public void setSettleyearmonth(java.lang.String newSettleyearmonth) {
		this.settleyearmonth = newSettleyearmonth;
	}

	/**
	 * 屬性 creator的Getter方法.屬性名：創建人 創建日期:2019/1/5
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCreator() {
		return creator;
	}

	/**
	 * 屬性creator的Setter方法.屬性名：創建人 創建日期:2019/1/5
	 * 
	 * @param newCreator
	 *            java.lang.String
	 */
	public void setCreator(java.lang.String newCreator) {
		this.creator = newCreator;
	}

	/**
	 * 屬性 creationtime的Getter方法.屬性名：創建時間 創建日期:2019/1/5
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getCreationtime() {
		return creationtime;
	}

	/**
	 * 屬性creationtime的Setter方法.屬性名：創建時間 創建日期:2019/1/5
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(nc.vo.pub.lang.UFDateTime newCreationtime) {
		this.creationtime = newCreationtime;
	}

	/**
	 * 屬性 modifier的Getter方法.屬性名：修改人 創建日期:2019/1/5
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getModifier() {
		return modifier;
	}

	/**
	 * 屬性modifier的Setter方法.屬性名：修改人 創建日期:2019/1/5
	 * 
	 * @param newModifier
	 *            java.lang.String
	 */
	public void setModifier(java.lang.String newModifier) {
		this.modifier = newModifier;
	}

	/**
	 * 屬性 modifiedtime的Getter方法.屬性名：修改時間 創建日期:2019/1/5
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getModifiedtime() {
		return modifiedtime;
	}

	/**
	 * 屬性modifiedtime的Setter方法.屬性名：修改時間 創建日期:2019/1/5
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(nc.vo.pub.lang.UFDateTime newModifiedtime) {
		this.modifiedtime = newModifiedtime;
	}

	/**
	 * 屬性 dr的Getter方法.屬性名：dr 創建日期:2019/1/5
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr() {
		return dr;
	}

	/**
	 * 屬性dr的Setter方法.屬性名：dr 創建日期:2019/1/5
	 * 
	 * @param newDr
	 *            java.lang.Integer
	 */
	public void setDr(java.lang.Integer newDr) {
		this.dr = newDr;
	}

	/**
	 * 屬性 ts的Getter方法.屬性名：ts 創建日期:2019/1/5
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs() {
		return ts;
	}

	/**
	 * 屬性ts的Setter方法.屬性名：ts 創建日期:2019/1/5
	 * 
	 * @param newTs
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(nc.vo.pub.lang.UFDateTime newTs) {
		this.ts = newTs;
	}

	/**
	 * <p>
	 * 取得父VO主鍵字段.
	 * <p>
	 * 創建日期:2019/1/5
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getParentPKFieldName() {
		return null;
	}

	/**
	 * <p>
	 * 取得表主鍵.
	 * <p>
	 * 創建日期:2019/1/5
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPKFieldName() {

		return "pk_extrarest";
	}

	/**
	 * <p>
	 * 返回表名稱
	 * <p>
	 * 創建日期:2019/1/5
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "tbm_extrarest";
	}

	/**
	 * <p>
	 * 返回表名稱.
	 * <p>
	 * 創建日期:2019/1/5
	 * 
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "tbm_extrarest";
	}

	/**
	 * 按照默認方式創建構造子.
	 * 
	 * 創建日期:2019/1/5
	 */
	public LeaveExtraRestVO() {
		super();
	}

	@nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.ta.leaveextrarest.LeaveExtraRestVO")
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("hrta.leaveextrarest");

	}

	public String getCyear() {
		return cyear;
	}

	public void setCyear(String cyear) {
		this.cyear = cyear;
	}

}
