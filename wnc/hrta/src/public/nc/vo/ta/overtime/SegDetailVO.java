package nc.vo.ta.overtime;

import nc.vo.pub.IVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此處簡要描述此類功能 </b>
 * <p>
 * 此處添加類的描述信息
 * </p>
 * 創建日期:2019/1/10
 * 
 * @author
 * @version NCPrj ??
 */
public class SegDetailVO extends nc.vo.pub.SuperVO {

	private java.lang.String pk_segdetail;
	private java.lang.String pk_group;
	private java.lang.String pk_org;
	private java.lang.String pk_org_v;
	private java.lang.String creator;
	private nc.vo.pub.lang.UFDateTime creationtime;
	private java.lang.String modifier;
	private nc.vo.pub.lang.UFDateTime modifiedtime;
	private nc.vo.pub.lang.UFDate maketime;
	private java.lang.String pk_parentsegdetail;
	private java.lang.Integer nodeno;
	private java.lang.String nodecode;
	private java.lang.String nodename;
	private nc.vo.pub.lang.UFLiteralDate regdate;
	private java.lang.String pk_segrule;
	private java.lang.String pk_segruleterm;
	private java.lang.String pk_psndoc;
	private java.lang.String pk_overtimereg;
	private nc.vo.pub.lang.UFDouble rulehours;
	private nc.vo.pub.lang.UFDouble hours;
	private nc.vo.pub.lang.UFDouble hourstaxfree;
	private nc.vo.pub.lang.UFDouble hourstaxable;
	private nc.vo.pub.lang.UFDouble hourlypay;
	private nc.vo.pub.lang.UFDouble taxfreerate;
	private nc.vo.pub.lang.UFDouble taxablerate;
	private nc.vo.pub.lang.UFDouble extrahourstaxable;
	private nc.vo.pub.lang.UFDouble extrataxablerate;
	private nc.vo.pub.lang.UFDouble extraamounttaxable;
	private nc.vo.pub.lang.UFDouble frozenhours;
	private nc.vo.pub.lang.UFDouble frozenhourstaxfree;
	private nc.vo.pub.lang.UFDouble frozenhourstaxable;
	private nc.vo.pub.lang.UFDouble consumedhours;
	private nc.vo.pub.lang.UFDouble consumedhourstaxfree;
	private nc.vo.pub.lang.UFDouble consumedhourstaxable;
	private nc.vo.pub.lang.UFDouble remainhours;
	private nc.vo.pub.lang.UFDouble remainhourstaxfree;
	private nc.vo.pub.lang.UFDouble remainhourstaxable;
	private nc.vo.pub.lang.UFDouble remainamount;
	private nc.vo.pub.lang.UFDouble remainamounttaxfree;
	private nc.vo.pub.lang.UFDouble remainamounttaxable;
	private nc.vo.pub.lang.UFBoolean iscanceled;
	private nc.vo.pub.lang.UFBoolean iscompensation;
	private nc.vo.pub.lang.UFDouble hourstorest;
	private nc.vo.pub.lang.UFBoolean isconsumed;
	private nc.vo.pub.lang.UFBoolean issettled;
	private nc.vo.pub.lang.UFLiteralDate settledate;
	private nc.vo.pub.lang.UFLiteralDate expirydate;
	private nc.vo.pub.lang.UFLiteralDate approveddate;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;

	private nc.vo.ta.overtime.SegDetailConsumeVO[] pk_segdetailconsume;

	public static final String PK_SEGDETAIL = "pk_segdetail";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String PK_ORG_V = "pk_org_v";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String MAKETIME = "maketime";
	public static final String PK_PARENTSEGDETAIL = "pk_parentsegdetail";
	public static final String NODENO = "nodeno";
	public static final String NODECODE = "nodecode";
	public static final String NODENAME = "nodename";
	public static final String REGDATE = "regdate";
	public static final String PK_SEGRULE = "pk_segrule";
	public static final String PK_SEGRULETERM = "pk_segruleterm";
	public static final String PK_PSNDOC = "pk_psndoc";
	public static final String PK_OVERTIMEREG = "pk_overtimereg";
	public static final String RULEHOURS = "rulehours";
	public static final String HOURS = "hours";
	public static final String HOURSTAXFREE = "hourstaxfree";
	public static final String HOURSTAXABLE = "hourstaxable";
	public static final String HOURLYPAY = "hourlypay";
	public static final String TAXFREERATE = "taxfreerate";
	public static final String TAXABLERATE = "taxablerate";
	public static final String EXTRAHOURSTAXABLE = "extrahourstaxable";
	public static final String EXTRATAXABLERATE = "extrataxablerate";
	public static final String EXTRAAMOUNTTAXABLE = "extraamounttaxable";
	public static final String FROZENHOURS = "frozenhours";
	public static final String FROZENHOURSTAXFREE = "frozenhourstaxfree";
	public static final String FROZENHOURSTAXABLE = "frozenhourstaxable";
	public static final String CONSUMEDHOURS = "consumedhours";
	public static final String CONSUMEDHOURSTAXFREE = "consumedhourstaxfree";
	public static final String CONSUMEDHOURSTAXABLE = "consumedhourstaxable";
	public static final String REMAINHOURS = "remainhours";
	public static final String REMAINHOURSTAXFREE = "remainhourstaxfree";
	public static final String REMAINHOURSTAXABLE = "remainhourstaxable";
	public static final String REMAINAMOUNT = "remainamount";
	public static final String REMAINAMOUNTTAXFREE = "remainamounttaxfree";
	public static final String REMAINAMOUNTTAXABLE = "remainamounttaxable";
	public static final String ISCANCELED = "iscanceled";
	public static final String ISCOMPENSATION = "iscompensation";
	public static final String HOURSTOREST = "hourstorest";
	public static final String ISCONSUMED = "isconsumed";
	public static final String ISSETTLED = "issettled";
	public static final String SETTLEDATE = "settledate";
	public static final String EXPIRYDATE = "expirydate";
	public static final String APPROVEDDATE = "approveddate";

	/**
	 * 屬性 pk_segdetail的Getter方法.屬性名：加班分段明細主鍵 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_segdetail() {
		return pk_segdetail;
	}

	/**
	 * 屬性pk_segdetail的Setter方法.屬性名：加班分段明細主鍵 創建日期:2019/1/10
	 * 
	 * @param newPk_segdetail
	 *            java.lang.String
	 */
	public void setPk_segdetail(java.lang.String newPk_segdetail) {
		this.pk_segdetail = newPk_segdetail;
	}

	/**
	 * 屬性 pk_segdetailconsume的Getter方法.屬性名：加班分段核銷記錄 創建日期:2019/1/10
	 * 
	 * @return nc.vo.ta.overtime.SegDetailConsumeVO[]
	 */
	public nc.vo.ta.overtime.SegDetailConsumeVO[] getPk_segdetailconsume() {
		return pk_segdetailconsume;
	}

	/**
	 * 屬性pk_segdetailconsume的Setter方法.屬性名：加班分段核銷記錄 創建日期:2019/1/10
	 * 
	 * @param newPk_segdetailconsume
	 *            nc.vo.ta.overtime.SegDetailConsumeVO[]
	 */
	public void setPk_segdetailconsume(nc.vo.ta.overtime.SegDetailConsumeVO[] newPk_segdetailconsume) {
		this.pk_segdetailconsume = newPk_segdetailconsume;
	}

	/**
	 * 屬性 pk_group的Getter方法.屬性名：集團 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group() {
		return pk_group;
	}

	/**
	 * 屬性pk_group的Setter方法.屬性名：集團 創建日期:2019/1/10
	 * 
	 * @param newPk_group
	 *            java.lang.String
	 */
	public void setPk_group(java.lang.String newPk_group) {
		this.pk_group = newPk_group;
	}

	/**
	 * 屬性 pk_org的Getter方法.屬性名：組織 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org() {
		return pk_org;
	}

	/**
	 * 屬性pk_org的Setter方法.屬性名：組織 創建日期:2019/1/10
	 * 
	 * @param newPk_org
	 *            java.lang.String
	 */
	public void setPk_org(java.lang.String newPk_org) {
		this.pk_org = newPk_org;
	}

	/**
	 * 屬性 pk_org_v的Getter方法.屬性名：組織版本 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org_v() {
		return pk_org_v;
	}

	/**
	 * 屬性pk_org_v的Setter方法.屬性名：組織版本 創建日期:2019/1/10
	 * 
	 * @param newPk_org_v
	 *            java.lang.String
	 */
	public void setPk_org_v(java.lang.String newPk_org_v) {
		this.pk_org_v = newPk_org_v;
	}

	/**
	 * 屬性 creator的Getter方法.屬性名：創建人 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCreator() {
		return creator;
	}

	/**
	 * 屬性creator的Setter方法.屬性名：創建人 創建日期:2019/1/10
	 * 
	 * @param newCreator
	 *            java.lang.String
	 */
	public void setCreator(java.lang.String newCreator) {
		this.creator = newCreator;
	}

	/**
	 * 屬性 creationtime的Getter方法.屬性名：創建時間 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getCreationtime() {
		return creationtime;
	}

	/**
	 * 屬性creationtime的Setter方法.屬性名：創建時間 創建日期:2019/1/10
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(nc.vo.pub.lang.UFDateTime newCreationtime) {
		this.creationtime = newCreationtime;
	}

	/**
	 * 屬性 modifier的Getter方法.屬性名：修改人 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getModifier() {
		return modifier;
	}

	/**
	 * 屬性modifier的Setter方法.屬性名：修改人 創建日期:2019/1/10
	 * 
	 * @param newModifier
	 *            java.lang.String
	 */
	public void setModifier(java.lang.String newModifier) {
		this.modifier = newModifier;
	}

	/**
	 * 屬性 modifiedtime的Getter方法.屬性名：修改時間 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getModifiedtime() {
		return modifiedtime;
	}

	/**
	 * 屬性modifiedtime的Setter方法.屬性名：修改時間 創建日期:2019/1/10
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(nc.vo.pub.lang.UFDateTime newModifiedtime) {
		this.modifiedtime = newModifiedtime;
	}

	/**
	 * 屬性 maketime的Getter方法.屬性名：制單時間 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getMaketime() {
		return maketime;
	}

	/**
	 * 屬性maketime的Setter方法.屬性名：制單時間 創建日期:2019/1/10
	 * 
	 * @param newMaketime
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setMaketime(nc.vo.pub.lang.UFDate newMaketime) {
		this.maketime = newMaketime;
	}

	/**
	 * 屬性 pk_parentsegdetail的Getter方法.屬性名：父節點 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_parentsegdetail() {
		return pk_parentsegdetail;
	}

	/**
	 * 屬性pk_parentsegdetail的Setter方法.屬性名：父節點 創建日期:2019/1/10
	 * 
	 * @param newPk_parentsegdetail
	 *            java.lang.String
	 */
	public void setPk_parentsegdetail(java.lang.String newPk_parentsegdetail) {
		this.pk_parentsegdetail = newPk_parentsegdetail;
	}

	/**
	 * 屬性 nodeno的Getter方法.屬性名：節點序號 創建日期:2019/1/10
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getNodeno() {
		return nodeno;
	}

	/**
	 * 屬性nodeno的Setter方法.屬性名：節點序號 創建日期:2019/1/10
	 * 
	 * @param newNodeno
	 *            java.lang.Integer
	 */
	public void setNodeno(java.lang.Integer newNodeno) {
		this.nodeno = newNodeno;
	}

	/**
	 * 屬性 nodecode的Getter方法.屬性名：節點編碼 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getNodecode() {
		return nodecode;
	}

	/**
	 * 屬性nodecode的Setter方法.屬性名：節點編碼 創建日期:2019/1/10
	 * 
	 * @param newNodecode
	 *            java.lang.String
	 */
	public void setNodecode(java.lang.String newNodecode) {
		this.nodecode = newNodecode;
	}

	/**
	 * 屬性 nodename的Getter方法.屬性名：節點名稱 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getNodename() {
		return nodename;
	}

	/**
	 * 屬性nodename的Setter方法.屬性名：節點名稱 創建日期:2019/1/10
	 * 
	 * @param newNodename
	 *            java.lang.String
	 */
	public void setNodename(java.lang.String newNodename) {
		this.nodename = newNodename;
	}

	/**
	 * 屬性 regdate的Getter方法.屬性名：加班日期 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public nc.vo.pub.lang.UFLiteralDate getRegdate() {
		return regdate;
	}

	/**
	 * 屬性regdate的Setter方法.屬性名：加班日期 創建日期:2019/1/10
	 * 
	 * @param newRegdate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setRegdate(nc.vo.pub.lang.UFLiteralDate newRegdate) {
		this.regdate = newRegdate;
	}

	/**
	 * 屬性 pk_segrule的Getter方法.屬性名：加班分段依據 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_segrule() {
		return pk_segrule;
	}

	/**
	 * 屬性pk_segrule的Setter方法.屬性名：加班分段依據 創建日期:2019/1/10
	 * 
	 * @param newPk_segrule
	 *            java.lang.String
	 */
	public void setPk_segrule(java.lang.String newPk_segrule) {
		this.pk_segrule = newPk_segrule;
	}

	/**
	 * 屬性 pk_segruleterm的Getter方法.屬性名：加班分段規則 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_segruleterm() {
		return pk_segruleterm;
	}

	/**
	 * 屬性pk_segruleterm的Setter方法.屬性名：加班分段規則 創建日期:2019/1/10
	 * 
	 * @param newPk_segruleterm
	 *            java.lang.String
	 */
	public void setPk_segruleterm(java.lang.String newPk_segruleterm) {
		this.pk_segruleterm = newPk_segruleterm;
	}

	/**
	 * 屬性 pk_psndoc的Getter方法.屬性名：人員 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_psndoc() {
		return pk_psndoc;
	}

	/**
	 * 屬性pk_psndoc的Setter方法.屬性名：人員 創建日期:2019/1/10
	 * 
	 * @param newPk_psndoc
	 *            java.lang.String
	 */
	public void setPk_psndoc(java.lang.String newPk_psndoc) {
		this.pk_psndoc = newPk_psndoc;
	}

	/**
	 * 屬性 pk_overtimereg的Getter方法.屬性名：加班登記單 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_overtimereg() {
		return pk_overtimereg;
	}

	/**
	 * 屬性pk_overtimereg的Setter方法.屬性名：加班登記單 創建日期:2019/1/10
	 * 
	 * @param newPk_overtimereg
	 *            java.lang.String
	 */
	public void setPk_overtimereg(java.lang.String newPk_overtimereg) {
		this.pk_overtimereg = newPk_overtimereg;
	}

	/**
	 * 屬性 rulehours的Getter方法.屬性名：分段規則時長 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getRulehours() {
		return rulehours;
	}

	/**
	 * 屬性rulehours的Setter方法.屬性名：分段規則時長 創建日期:2019/1/10
	 * 
	 * @param newRulehours
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRulehours(nc.vo.pub.lang.UFDouble newRulehours) {
		this.rulehours = newRulehours;
	}

	/**
	 * 屬性 hours的Getter方法.屬性名：核定加班總時長 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getHours() {
		return hours;
	}

	/**
	 * 屬性hours的Setter方法.屬性名：核定加班總時長 創建日期:2019/1/10
	 * 
	 * @param newHours
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHours(nc.vo.pub.lang.UFDouble newHours) {
		this.hours = newHours;
	}

	/**
	 * 屬性 hourstaxfree的Getter方法.屬性名：核定加班免稅時長 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getHourstaxfree() {
		return hourstaxfree;
	}

	/**
	 * 屬性hourstaxfree的Setter方法.屬性名：核定加班免稅時長 創建日期:2019/1/10
	 * 
	 * @param newHourstaxfree
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHourstaxfree(nc.vo.pub.lang.UFDouble newHourstaxfree) {
		this.hourstaxfree = newHourstaxfree;
	}

	/**
	 * 屬性 hourstaxable的Getter方法.屬性名：核定加班應稅時長 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getHourstaxable() {
		return hourstaxable;
	}

	/**
	 * 屬性hourstaxable的Setter方法.屬性名：核定加班應稅時長 創建日期:2019/1/10
	 * 
	 * @param newHourstaxable
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHourstaxable(nc.vo.pub.lang.UFDouble newHourstaxable) {
		this.hourstaxable = newHourstaxable;
	}

	/**
	 * 屬性 hourlypay的Getter方法.屬性名：核定加班時薪 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getHourlypay() {
		return hourlypay;
	}

	/**
	 * 屬性hourlypay的Setter方法.屬性名：核定加班時薪 創建日期:2019/1/10
	 * 
	 * @param newHourlypay
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHourlypay(nc.vo.pub.lang.UFDouble newHourlypay) {
		this.hourlypay = newHourlypay;
	}

	/**
	 * 屬性 taxfreerate的Getter方法.屬性名：免稅加班費率 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getTaxfreerate() {
		return taxfreerate;
	}

	/**
	 * 屬性taxfreerate的Setter方法.屬性名：免稅加班費率 創建日期:2019/1/10
	 * 
	 * @param newTaxfreerate
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setTaxfreerate(nc.vo.pub.lang.UFDouble newTaxfreerate) {
		this.taxfreerate = newTaxfreerate;
	}

	/**
	 * 屬性 taxablerate的Getter方法.屬性名：應稅加班費率 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getTaxablerate() {
		return taxablerate;
	}

	/**
	 * 屬性taxablerate的Setter方法.屬性名：應稅加班費率 創建日期:2019/1/10
	 * 
	 * @param newTaxablerate
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setTaxablerate(nc.vo.pub.lang.UFDouble newTaxablerate) {
		this.taxablerate = newTaxablerate;
	}

	/**
	 * 屬性 extrahourstaxable的Getter方法.屬性名：外加應稅時長 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getExtrahourstaxable() {
		return extrahourstaxable;
	}

	/**
	 * 屬性extrahourstaxable的Setter方法.屬性名：外加應稅時長 創建日期:2019/1/10
	 * 
	 * @param newExtrahourstaxable
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setExtrahourstaxable(nc.vo.pub.lang.UFDouble newExtrahourstaxable) {
		this.extrahourstaxable = newExtrahourstaxable;
	}

	/**
	 * 屬性 extrataxablerate的Getter方法.屬性名：外加應稅費率 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getExtrataxablerate() {
		return extrataxablerate;
	}

	/**
	 * 屬性extrataxablerate的Setter方法.屬性名：外加應稅費率 創建日期:2019/1/10
	 * 
	 * @param newExtrataxablerate
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setExtrataxablerate(nc.vo.pub.lang.UFDouble newExtrataxablerate) {
		this.extrataxablerate = newExtrataxablerate;
	}

	/**
	 * 屬性 extraamounttaxable的Getter方法.屬性名：外加應稅金額 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getExtraamounttaxable() {
		return extraamounttaxable;
	}

	/**
	 * 屬性extraamounttaxable的Setter方法.屬性名：外加應稅金額 創建日期:2019/1/10
	 * 
	 * @param newExtraamounttaxable
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setExtraamounttaxable(nc.vo.pub.lang.UFDouble newExtraamounttaxable) {
		this.extraamounttaxable = newExtraamounttaxable;
	}

	/**
	 * 屬性 frozenhours的Getter方法.屬性名：凍結總時長 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getFrozenhours() {
		return frozenhours;
	}

	/**
	 * 屬性frozenhours的Setter方法.屬性名：凍結總時長 創建日期:2019/1/10
	 * 
	 * @param newFrozenhours
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setFrozenhours(nc.vo.pub.lang.UFDouble newFrozenhours) {
		this.frozenhours = newFrozenhours;
	}

	/**
	 * 屬性 frozenhourstaxfree的Getter方法.屬性名：凍結免稅時長 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getFrozenhourstaxfree() {
		return frozenhourstaxfree;
	}

	/**
	 * 屬性frozenhourstaxfree的Setter方法.屬性名：凍結免稅時長 創建日期:2019/1/10
	 * 
	 * @param newFrozenhourstaxfree
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setFrozenhourstaxfree(nc.vo.pub.lang.UFDouble newFrozenhourstaxfree) {
		this.frozenhourstaxfree = newFrozenhourstaxfree;
	}

	/**
	 * 屬性 frozenhourstaxable的Getter方法.屬性名：凍結應稅時長 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getFrozenhourstaxable() {
		return frozenhourstaxable;
	}

	/**
	 * 屬性frozenhourstaxable的Setter方法.屬性名：凍結應稅時長 創建日期:2019/1/10
	 * 
	 * @param newFrozenhourstaxable
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setFrozenhourstaxable(nc.vo.pub.lang.UFDouble newFrozenhourstaxable) {
		this.frozenhourstaxable = newFrozenhourstaxable;
	}

	/**
	 * 屬性 consumedhours的Getter方法.屬性名：已核銷總時長 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getConsumedhours() {
		return consumedhours;
	}

	/**
	 * 屬性consumedhours的Setter方法.屬性名：已核銷總時長 創建日期:2019/1/10
	 * 
	 * @param newConsumedhours
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setConsumedhours(nc.vo.pub.lang.UFDouble newConsumedhours) {
		this.consumedhours = newConsumedhours;
	}

	/**
	 * 屬性 consumedhourstaxfree的Getter方法.屬性名：已核銷免稅時長 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getConsumedhourstaxfree() {
		return consumedhourstaxfree;
	}

	/**
	 * 屬性consumedhourstaxfree的Setter方法.屬性名：已核銷免稅時長 創建日期:2019/1/10
	 * 
	 * @param newConsumedhourstaxfree
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setConsumedhourstaxfree(nc.vo.pub.lang.UFDouble newConsumedhourstaxfree) {
		this.consumedhourstaxfree = newConsumedhourstaxfree;
	}

	/**
	 * 屬性 consumedhourstaxable的Getter方法.屬性名：已核銷應稅時長 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getConsumedhourstaxable() {
		return consumedhourstaxable;
	}

	/**
	 * 屬性consumedhourstaxable的Setter方法.屬性名：已核銷應稅時長 創建日期:2019/1/10
	 * 
	 * @param newConsumedhourstaxable
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setConsumedhourstaxable(nc.vo.pub.lang.UFDouble newConsumedhourstaxable) {
		this.consumedhourstaxable = newConsumedhourstaxable;
	}

	/**
	 * 屬性 remainhours的Getter方法.屬性名：剩餘總時長 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getRemainhours() {
		return remainhours;
	}

	/**
	 * 屬性remainhours的Setter方法.屬性名：剩餘總時長 創建日期:2019/1/10
	 * 
	 * @param newRemainhours
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRemainhours(nc.vo.pub.lang.UFDouble newRemainhours) {
		this.remainhours = newRemainhours;
	}

	/**
	 * 屬性 remainhourstaxfree的Getter方法.屬性名：剩餘免稅時長 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getRemainhourstaxfree() {
		return remainhourstaxfree;
	}

	/**
	 * 屬性remainhourstaxfree的Setter方法.屬性名：剩餘免稅時長 創建日期:2019/1/10
	 * 
	 * @param newRemainhourstaxfree
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRemainhourstaxfree(nc.vo.pub.lang.UFDouble newRemainhourstaxfree) {
		this.remainhourstaxfree = newRemainhourstaxfree;
	}

	/**
	 * 屬性 remainhourstaxable的Getter方法.屬性名：剩餘應稅時長 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getRemainhourstaxable() {
		return remainhourstaxable;
	}

	/**
	 * 屬性remainhourstaxable的Setter方法.屬性名：剩餘應稅時長 創建日期:2019/1/10
	 * 
	 * @param newRemainhourstaxable
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRemainhourstaxable(nc.vo.pub.lang.UFDouble newRemainhourstaxable) {
		this.remainhourstaxable = newRemainhourstaxable;
	}

	/**
	 * 屬性 remainamount的Getter方法.屬性名：剩餘總金額 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getRemainamount() {
		return remainamount;
	}

	/**
	 * 屬性remainamount的Setter方法.屬性名：剩餘總金額 創建日期:2019/1/10
	 * 
	 * @param newRemainamount
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRemainamount(nc.vo.pub.lang.UFDouble newRemainamount) {
		this.remainamount = newRemainamount;
	}

	/**
	 * 屬性 remainamounttaxfree的Getter方法.屬性名：剩餘免稅金額 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getRemainamounttaxfree() {
		return remainamounttaxfree;
	}

	/**
	 * 屬性remainamounttaxfree的Setter方法.屬性名：剩餘免稅金額 創建日期:2019/1/10
	 * 
	 * @param newRemainamounttaxfree
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRemainamounttaxfree(nc.vo.pub.lang.UFDouble newRemainamounttaxfree) {
		this.remainamounttaxfree = newRemainamounttaxfree;
	}

	/**
	 * 屬性 remainamounttaxable的Getter方法.屬性名：剩餘應稅金額 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getRemainamounttaxable() {
		return remainamounttaxable;
	}

	/**
	 * 屬性remainamounttaxable的Setter方法.屬性名：剩餘應稅金額 創建日期:2019/1/10
	 * 
	 * @param newRemainamounttaxable
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setRemainamounttaxable(nc.vo.pub.lang.UFDouble newRemainamounttaxable) {
		this.remainamounttaxable = newRemainamounttaxable;
	}

	/**
	 * 屬性 iscanceled的Getter方法.屬性名：是否作廢 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getIscanceled() {
		return iscanceled;
	}

	/**
	 * 屬性iscanceled的Setter方法.屬性名：是否作廢 創建日期:2019/1/10
	 * 
	 * @param newIscanceled
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIscanceled(nc.vo.pub.lang.UFBoolean newIscanceled) {
		this.iscanceled = newIscanceled;
	}

	/**
	 * 屬性 iscompensation的Getter方法.屬性名：是否轉調休 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getIscompensation() {
		return iscompensation;
	}

	/**
	 * 屬性iscompensation的Setter方法.屬性名：是否轉調休 創建日期:2019/1/10
	 * 
	 * @param newIscompensation
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIscompensation(nc.vo.pub.lang.UFBoolean newIscompensation) {
		this.iscompensation = newIscompensation;
	}

	/**
	 * 屬性 hourstorest的Getter方法.屬性名：轉調休時長 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getHourstorest() {
		return hourstorest;
	}

	/**
	 * 屬性hourstorest的Setter方法.屬性名：轉調休時長 創建日期:2019/1/10
	 * 
	 * @param newHourstorest
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHourstorest(nc.vo.pub.lang.UFDouble newHourstorest) {
		this.hourstorest = newHourstorest;
	}

	/**
	 * 屬性 isconsumed的Getter方法.屬性名：是否核銷完成 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getIsconsumed() {
		return isconsumed;
	}

	/**
	 * 屬性isconsumed的Setter方法.屬性名：是否核銷完成 創建日期:2019/1/10
	 * 
	 * @param newIsconsumed
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIsconsumed(nc.vo.pub.lang.UFBoolean newIsconsumed) {
		this.isconsumed = newIsconsumed;
	}

	/**
	 * 屬性 issettled的Getter方法.屬性名：是否結算 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getIssettled() {
		return issettled;
	}

	/**
	 * 屬性issettled的Setter方法.屬性名：是否結算 創建日期:2019/1/10
	 * 
	 * @param newIssettled
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIssettled(nc.vo.pub.lang.UFBoolean newIssettled) {
		this.issettled = newIssettled;
	}

	/**
	 * 屬性 settledate的Getter方法.屬性名：結算日期 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public nc.vo.pub.lang.UFLiteralDate getSettledate() {
		return settledate;
	}

	/**
	 * 屬性settledate的Setter方法.屬性名：結算日期 創建日期:2019/1/10
	 * 
	 * @param newSettledate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setSettledate(nc.vo.pub.lang.UFLiteralDate newSettledate) {
		this.settledate = newSettledate;
	}

	/**
	 * 屬性 expirydate的Getter方法.屬性名：到期日 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public nc.vo.pub.lang.UFLiteralDate getExpirydate() {
		return expirydate;
	}

	/**
	 * 屬性expirydate的Setter方法.屬性名：到期日 創建日期:2019/1/10
	 * 
	 * @param newExpirydate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setExpirydate(nc.vo.pub.lang.UFLiteralDate newExpirydate) {
		this.expirydate = newExpirydate;
	}

	/**
	 * 屬性 approveddate的Getter方法.屬性名：審核日期 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public nc.vo.pub.lang.UFLiteralDate getApproveddate() {
		return approveddate;
	}

	/**
	 * 屬性approveddate的Setter方法.屬性名：審核日期 創建日期:2019/1/10
	 * 
	 * @param newApproveddate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setApproveddate(nc.vo.pub.lang.UFLiteralDate newApproveddate) {
		this.approveddate = newApproveddate;
	}

	/**
	 * 屬性 dr的Getter方法.屬性名：dr 創建日期:2019/1/10
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr() {
		return dr;
	}

	/**
	 * 屬性dr的Setter方法.屬性名：dr 創建日期:2019/1/10
	 * 
	 * @param newDr
	 *            java.lang.Integer
	 */
	public void setDr(java.lang.Integer newDr) {
		this.dr = newDr;
	}

	/**
	 * 屬性 ts的Getter方法.屬性名：ts 創建日期:2019/1/10
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs() {
		return ts;
	}

	/**
	 * 屬性ts的Setter方法.屬性名：ts 創建日期:2019/1/10
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
	 * 創建日期:2019/1/10
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
	 * 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPKFieldName() {

		return "pk_segdetail";
	}

	/**
	 * <p>
	 * 返回表名稱
	 * <p>
	 * 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "hrta_segdetail";
	}

	/**
	 * <p>
	 * 返回表名稱.
	 * <p>
	 * 創建日期:2019/1/10
	 * 
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "hrta_segdetail";
	}

	/**
	 * 按照默認方式創建構造子.
	 * 
	 * 創建日期:2019/1/10
	 */
	public SegDetailVO() {
		super();
	}

	@nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.ta.overtime.SegDetailVO")
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("overtime.segdetail");

	}

}
