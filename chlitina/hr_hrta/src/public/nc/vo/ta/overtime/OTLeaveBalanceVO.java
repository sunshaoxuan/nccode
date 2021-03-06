package nc.vo.ta.overtime;

import nc.vo.pub.IVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此處簡要描述此類功能 </b>
 * <p>
 * 此處添加類的描述信息
 * </p>
 * 創建日期:2018/10/25
 * 
 * @author
 * @version NCPrj ??
 */
public class OTLeaveBalanceVO extends nc.vo.pub.SuperVO {

    private java.lang.String pk_otleavebalance;
    private java.lang.String pk_psndoc;
    private java.lang.String pk_group;
    private java.lang.String pk_org;
    private java.lang.String pk_org_v;
    private nc.vo.pub.lang.UFDouble totalhours;
    private nc.vo.pub.lang.UFDouble consumedhours;
    private nc.vo.pub.lang.UFDouble remainhours;
    private nc.vo.pub.lang.UFDouble frozenhours;
    private nc.vo.pub.lang.UFDouble freehours;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;

    private nc.vo.ta.overtime.OTBalanceDetailVO[] id_otbalancedetail;

    public static final String PK_OTLEAVEBALANCE = "pk_otleavebalance";
    public static final String PK_PSNDOC = "pk_psndoc";
    public static final String PK_GROUP = "pk_group";
    public static final String PK_ORG = "pk_org";
    public static final String PK_ORG_V = "pk_org_v";
    public static final String TOTALHOURS = "totalhours";
    public static final String CONSUMEDHOURS = "consumedhours";
    public static final String REMAINHOURS = "remainhours";
    public static final String FROZENHOURS = "frozenhours";
    public static final String FREEHOURS = "freehours";

    /**
     * 屬性 pk_otleavebalance的Getter方法.屬性名：主表主鍵 創建日期:2018/10/25
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_otleavebalance() {
	return pk_otleavebalance;
    }

    /**
     * 屬性pk_otleavebalance的Setter方法.屬性名：主表主鍵 創建日期:2018/10/25
     * 
     * @param newPk_otleavebalance
     *            java.lang.String
     */
    public void setPk_otleavebalance(java.lang.String newPk_otleavebalance) {
	this.pk_otleavebalance = newPk_otleavebalance;
    }

    /**
     * 屬性 pk_psndoc的Getter方法.屬性名：員工信息 創建日期:2018/10/25
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_psndoc() {
	return pk_psndoc;
    }

    /**
     * 屬性pk_psndoc的Setter方法.屬性名：員工信息 創建日期:2018/10/25
     * 
     * @param newPk_psndoc
     *            java.lang.String
     */
    public void setPk_psndoc(java.lang.String newPk_psndoc) {
	this.pk_psndoc = newPk_psndoc;
    }

    /**
     * 屬性 pk_group的Getter方法.屬性名：集團 創建日期:2018/10/25
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_group() {
	return pk_group;
    }

    /**
     * 屬性pk_group的Setter方法.屬性名：集團 創建日期:2018/10/25
     * 
     * @param newPk_group
     *            java.lang.String
     */
    public void setPk_group(java.lang.String newPk_group) {
	this.pk_group = newPk_group;
    }

    /**
     * 屬性 pk_org的Getter方法.屬性名：組織 創建日期:2018/10/25
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_org() {
	return pk_org;
    }

    /**
     * 屬性pk_org的Setter方法.屬性名：組織 創建日期:2018/10/25
     * 
     * @param newPk_org
     *            java.lang.String
     */
    public void setPk_org(java.lang.String newPk_org) {
	this.pk_org = newPk_org;
    }

    /**
     * 屬性 pk_org_v的Getter方法.屬性名：組織版本 創建日期:2018/10/25
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_org_v() {
	return pk_org_v;
    }

    /**
     * 屬性pk_org_v的Setter方法.屬性名：組織版本 創建日期:2018/10/25
     * 
     * @param newPk_org_v
     *            java.lang.String
     */
    public void setPk_org_v(java.lang.String newPk_org_v) {
	this.pk_org_v = newPk_org_v;
    }

    /**
     * 屬性 totalhours的Getter方法.屬性名：享有 創建日期:2018/10/25
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getTotalhours() {
	return totalhours;
    }

    /**
     * 屬性totalhours的Setter方法.屬性名：享有 創建日期:2018/10/25
     * 
     * @param newTotalhours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setTotalhours(nc.vo.pub.lang.UFDouble newTotalhours) {
	this.totalhours = newTotalhours;
    }

    /**
     * 屬性 consumedhours的Getter方法.屬性名：已休 創建日期:2018/10/25
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getConsumedhours() {
	return consumedhours;
    }

    /**
     * 屬性consumedhours的Setter方法.屬性名：已休 創建日期:2018/10/25
     * 
     * @param newConsumedhours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setConsumedhours(nc.vo.pub.lang.UFDouble newConsumedhours) {
	this.consumedhours = newConsumedhours;
    }

    /**
     * 屬性 remainhours的Getter方法.屬性名：結餘 創建日期:2018/10/25
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getRemainhours() {
	return remainhours;
    }

    /**
     * 屬性remainhours的Setter方法.屬性名：結餘 創建日期:2018/10/25
     * 
     * @param newRemainhours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setRemainhours(nc.vo.pub.lang.UFDouble newRemainhours) {
	this.remainhours = newRemainhours;
    }

    /**
     * 屬性 frozenhours的Getter方法.屬性名：凍結 創建日期:2018/10/25
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getFrozenhours() {
	return frozenhours;
    }

    /**
     * 屬性frozenhours的Setter方法.屬性名：凍結 創建日期:2018/10/25
     * 
     * @param newFrozenhours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setFrozenhours(nc.vo.pub.lang.UFDouble newFrozenhours) {
	this.frozenhours = newFrozenhours;
    }

    /**
     * 屬性 freehours的Getter方法.屬性名：可用 創建日期:2018/10/25
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getFreehours() {
	return freehours;
    }

    /**
     * 屬性freehours的Setter方法.屬性名：可用 創建日期:2018/10/25
     * 
     * @param newFreehours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setFreehours(nc.vo.pub.lang.UFDouble newFreehours) {
	this.freehours = newFreehours;
    }

    /**
     * 屬性 id_otbalancedetail的Getter方法.屬性名：id_otbalancedetail 創建日期:2018/10/25
     * 
     * @return nc.vo.ta.overtime.OTBalanceDetailVO[]
     */
    public nc.vo.ta.overtime.OTBalanceDetailVO[] getId_otbalancedetail() {
	return id_otbalancedetail;
    }

    /**
     * 屬性id_otbalancedetail的Setter方法.屬性名：id_otbalancedetail 創建日期:2018/10/25
     * 
     * @param newId_otbalancedetail
     *            nc.vo.ta.overtime.OTBalanceDetailVO[]
     */
    public void setId_otbalancedetail(nc.vo.ta.overtime.OTBalanceDetailVO[] newId_otbalancedetail) {
	this.id_otbalancedetail = newId_otbalancedetail;
    }

    /**
     * 屬性 dr的Getter方法.屬性名：dr 創建日期:2018/10/25
     * 
     * @return java.lang.Integer
     */
    public java.lang.Integer getDr() {
	return dr;
    }

    /**
     * 屬性dr的Setter方法.屬性名：dr 創建日期:2018/10/25
     * 
     * @param newDr
     *            java.lang.Integer
     */
    public void setDr(java.lang.Integer newDr) {
	this.dr = newDr;
    }

    /**
     * 屬性 ts的Getter方法.屬性名：ts 創建日期:2018/10/25
     * 
     * @return nc.vo.pub.lang.UFDateTime
     */
    public nc.vo.pub.lang.UFDateTime getTs() {
	return ts;
    }

    /**
     * 屬性ts的Setter方法.屬性名：ts 創建日期:2018/10/25
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
     * 創建日期:2018/10/25
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
     * 創建日期:2018/10/25
     * 
     * @return java.lang.String
     */
    public java.lang.String getPKFieldName() {

	return "pk_otleavebalance";
    }

    /**
     * <p>
     * 返回表名稱
     * <p>
     * 創建日期:2018/10/25
     * 
     * @return java.lang.String
     */
    public java.lang.String getTableName() {
	return "hrta_otleavebalance";
    }

    /**
     * <p>
     * 返回表名稱.
     * <p>
     * 創建日期:2018/10/25
     * 
     * @return java.lang.String
     */
    public static java.lang.String getDefaultTableName() {
	return "hrta_otleavebalance";
    }

    /**
     * 按照默認方式創建構造子.
     * 
     * 創建日期:2018/10/25
     */
    public OTLeaveBalanceVO() {
	super();
    }

    @nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.ta.overtime.OTLeaveBalanceVO")
    public IVOMeta getMetaData() {
	return VOMetaFactory.getInstance().getVOMeta("overtime.otleavebalance");

    }

}
