package nc.vo.ta.overtime;

import nc.vo.pub.IVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> �Ӱ�ֶ����� </b>
 * <p>
 * 
 * </p>
 * CreateDate:2018/9/7
 * 
 * @author
 * @version NC V65 TW Localization 3.2.1
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
    private nc.vo.pub.lang.UFDouble frozenhours;
    private nc.vo.pub.lang.UFDouble frozenhourstaxfree;
    private nc.vo.pub.lang.UFDouble frozenhourstaxable;
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
    public static final String ATTRNAME0 = "attrname0";
    public static final String ISCONSUMED = "isconsumed";
    public static final String ISSETTLED = "issettled";
    public static final String SETTLEDATE = "settledate";
    public static final String FROZENHOURS = "frozenhours";
    public static final String FROZENHOURSTAXFREE = "frozenhourstaxfree";
    public static final String FROZENHOURSTAXABLE = "frozenhourstaxable";
    public static final String EXPIRYDATE = "expirydate";
    public static final String APPROVEDDATE = "approveddate";

    /**
     * pk_segdetail Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_segdetail() {
	return pk_segdetail;
    }

    /**
     * pk_segdetail Create Date:2018/9/7
     * 
     * @param newPk_segdetail
     *            java.lang.String
     */
    public void setPk_segdetail(java.lang.String newPk_segdetail) {
	this.pk_segdetail = newPk_segdetail;
    }

    /**
     * pk_segdetailconsume Create Date:2018/9/7
     * 
     * @return nc.vo.ta.overtime.SegDetailConsumeVO[]
     */
    public nc.vo.ta.overtime.SegDetailConsumeVO[] getPk_segdetailconsume() {
	return pk_segdetailconsume;
    }

    /**
     * pk_segdetailconsume Create Date:2018/9/7
     * 
     * @param newPk_segdetailconsume
     *            nc.vo.ta.overtime.SegDetailConsumeVO[]
     */
    public void setPk_segdetailconsume(nc.vo.ta.overtime.SegDetailConsumeVO[] newPk_segdetailconsume) {
	this.pk_segdetailconsume = newPk_segdetailconsume;
    }

    /**
     * pk_group Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_group() {
	return pk_group;
    }

    /**
     * pk_group Create Date:2018/9/7
     * 
     * @param newPk_group
     *            java.lang.String
     */
    public void setPk_group(java.lang.String newPk_group) {
	this.pk_group = newPk_group;
    }

    /**
     * pk_org Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_org() {
	return pk_org;
    }

    /**
     * pk_org Create Date:2018/9/7
     * 
     * @param newPk_org
     *            java.lang.String
     */
    public void setPk_org(java.lang.String newPk_org) {
	this.pk_org = newPk_org;
    }

    /**
     * pk_org_v Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_org_v() {
	return pk_org_v;
    }

    /**
     * pk_org_v Create Date:2018/9/7
     * 
     * @param newPk_org_v
     *            java.lang.String
     */
    public void setPk_org_v(java.lang.String newPk_org_v) {
	this.pk_org_v = newPk_org_v;
    }

    /**
     * creator Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getCreator() {
	return creator;
    }

    /**
     * creator Create Date:2018/9/7
     * 
     * @param newCreator
     *            java.lang.String
     */
    public void setCreator(java.lang.String newCreator) {
	this.creator = newCreator;
    }

    /**
     * creationtime Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDateTime
     */
    public nc.vo.pub.lang.UFDateTime getCreationtime() {
	return creationtime;
    }

    /**
     * creationtime Create Date:2018/9/7
     * 
     * @param newCreationtime
     *            nc.vo.pub.lang.UFDateTime
     */
    public void setCreationtime(nc.vo.pub.lang.UFDateTime newCreationtime) {
	this.creationtime = newCreationtime;
    }

    /**
     * modifier Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getModifier() {
	return modifier;
    }

    /**
     * modifier Create Date:2018/9/7
     * 
     * @param newModifier
     *            java.lang.String
     */
    public void setModifier(java.lang.String newModifier) {
	this.modifier = newModifier;
    }

    /**
     * modifiedtime Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDateTime
     */
    public nc.vo.pub.lang.UFDateTime getModifiedtime() {
	return modifiedtime;
    }

    /**
     * modifiedtime Create Date:2018/9/7
     * 
     * @param newModifiedtime
     *            nc.vo.pub.lang.UFDateTime
     */
    public void setModifiedtime(nc.vo.pub.lang.UFDateTime newModifiedtime) {
	this.modifiedtime = newModifiedtime;
    }

    /**
     * maketime Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDate
     */
    public nc.vo.pub.lang.UFDate getMaketime() {
	return maketime;
    }

    /**
     * maketime Create Date:2018/9/7
     * 
     * @param newMaketime
     *            nc.vo.pub.lang.UFDate
     */
    public void setMaketime(nc.vo.pub.lang.UFDate newMaketime) {
	this.maketime = newMaketime;
    }

    /**
     * pk_parentsegdetail Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_parentsegdetail() {
	return pk_parentsegdetail;
    }

    /**
     * pk_parentsegdetail Create Date:2018/9/7
     * 
     * @param newPk_parentsegdetail
     *            java.lang.String
     */
    public void setPk_parentsegdetail(java.lang.String newPk_parentsegdetail) {
	this.pk_parentsegdetail = newPk_parentsegdetail;
    }

    /**
     * nodeno Create Date:2018/9/7
     * 
     * @return java.lang.Integer
     */
    public java.lang.Integer getNodeno() {
	return nodeno;
    }

    /**
     * nodeno Create Date:2018/9/7
     * 
     * @param newNodeno
     *            java.lang.Integer
     */
    public void setNodeno(java.lang.Integer newNodeno) {
	this.nodeno = newNodeno;
    }

    /**
     * nodecode Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getNodecode() {
	return nodecode;
    }

    /**
     * nodecode Create Date:2018/9/7
     * 
     * @param newNodecode
     *            java.lang.String
     */
    public void setNodecode(java.lang.String newNodecode) {
	this.nodecode = newNodecode;
    }

    /**
     * nodename Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getNodename() {
	return nodename;
    }

    /**
     * nodename Create Date:2018/9/7
     * 
     * @param newNodename
     *            java.lang.String
     */
    public void setNodename(java.lang.String newNodename) {
	this.nodename = newNodename;
    }

    /**
     * regdate Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFLiteralDate
     */
    public nc.vo.pub.lang.UFLiteralDate getRegdate() {
	return regdate;
    }

    /**
     * regdate的Setter Create Date:2018/9/7
     * 
     * @param newRegdate
     *            nc.vo.pub.lang.UFLiteralDate
     */
    public void setRegdate(nc.vo.pub.lang.UFLiteralDate newRegdate) {
	this.regdate = newRegdate;
    }

    /**
     * pk_segrule Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_segrule() {
	return pk_segrule;
    }

    /**
     * pk_segrule Create Date:2018/9/7
     * 
     * @param newPk_segrule
     *            java.lang.String
     */
    public void setPk_segrule(java.lang.String newPk_segrule) {
	this.pk_segrule = newPk_segrule;
    }

    /**
     * pk_segruleterm Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_segruleterm() {
	return pk_segruleterm;
    }

    /**
     * pk_segruleterm Create Date:2018/9/7
     * 
     * @param newPk_segruleterm
     *            java.lang.String
     */
    public void setPk_segruleterm(java.lang.String newPk_segruleterm) {
	this.pk_segruleterm = newPk_segruleterm;
    }

    /**
     * pk_psndoc Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_psndoc() {
	return pk_psndoc;
    }

    /**
     * pk_psndoc Create Date:2018/9/7
     * 
     * @param newPk_psndoc
     *            java.lang.String
     */
    public void setPk_psndoc(java.lang.String newPk_psndoc) {
	this.pk_psndoc = newPk_psndoc;
    }

    /**
     * pk_overtimereg Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_overtimereg() {
	return pk_overtimereg;
    }

    /**
     * pk_overtimereg Create Date:2018/9/7
     * 
     * @param newPk_overtimereg
     *            java.lang.String
     */
    public void setPk_overtimereg(java.lang.String newPk_overtimereg) {
	this.pk_overtimereg = newPk_overtimereg;
    }

    /**
     * rulehours Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getRulehours() {
	return rulehours;
    }

    /**
     * rulehours Create Date:2018/9/7
     * 
     * @param newRulehours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setRulehours(nc.vo.pub.lang.UFDouble newRulehours) {
	this.rulehours = newRulehours;
    }

    /**
     * hours的Getter Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getHours() {
	return hours;
    }

    /**
     * hours的Setter Create Date:2018/9/7
     * 
     * @param newHours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setHours(nc.vo.pub.lang.UFDouble newHours) {
	this.hours = newHours;
    }

    /**
     * hourstaxfree Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getHourstaxfree() {
	return hourstaxfree;
    }

    /**
     * hourstaxfree Create Date:2018/9/7
     * 
     * @param newHourstaxfree
     *            nc.vo.pub.lang.UFDouble
     */
    public void setHourstaxfree(nc.vo.pub.lang.UFDouble newHourstaxfree) {
	this.hourstaxfree = newHourstaxfree;
    }

    /**
     * hourstaxable Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getHourstaxable() {
	return hourstaxable;
    }

    /**
     * hourstaxable Create Date:2018/9/7
     * 
     * @param newHourstaxable
     *            nc.vo.pub.lang.UFDouble
     */
    public void setHourstaxable(nc.vo.pub.lang.UFDouble newHourstaxable) {
	this.hourstaxable = newHourstaxable;
    }

    /**
     * hourlypay Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getHourlypay() {
	return hourlypay;
    }

    /**
     * hourlypay Create Date:2018/9/7
     * 
     * @param newHourlypay
     *            nc.vo.pub.lang.UFDouble
     */
    public void setHourlypay(nc.vo.pub.lang.UFDouble newHourlypay) {
	this.hourlypay = newHourlypay;
    }

    /**
     * taxfreerate Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getTaxfreerate() {
	return taxfreerate;
    }

    /**
     * taxfreerate Create Date:2018/9/7
     * 
     * @param newTaxfreerate
     *            nc.vo.pub.lang.UFDouble
     */
    public void setTaxfreerate(nc.vo.pub.lang.UFDouble newTaxfreerate) {
	this.taxfreerate = newTaxfreerate;
    }

    /**
     * taxablerate Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getTaxablerate() {
	return taxablerate;
    }

    /**
     * taxablerate Create Date:2018/9/7
     * 
     * @param newTaxfreerate
     *            nc.vo.pub.lang.UFDouble
     */
    public void setTaxablerate(nc.vo.pub.lang.UFDouble newTaxablerate) {
	this.taxablerate = newTaxablerate;
    }

    /**
     * consumedhours Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getConsumedhours() {
	return consumedhours;
    }

    /**
     * consumedhours Create Date:2018/9/7
     * 
     * @param newConsumedhours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setConsumedhours(nc.vo.pub.lang.UFDouble newConsumedhours) {
	this.consumedhours = newConsumedhours;
    }

    /**
     * consumedhourstaxfree Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getConsumedhourstaxfree() {
	return consumedhourstaxfree;
    }

    /**
     * consumedhourstaxfree Create Date:2018/9/7
     * 
     * @param newConsumedhourstaxfree
     *            nc.vo.pub.lang.UFDouble
     */
    public void setConsumedhourstaxfree(nc.vo.pub.lang.UFDouble newConsumedhourstaxfree) {
	this.consumedhourstaxfree = newConsumedhourstaxfree;
    }

    /**
     * consumedhourstaxable Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getConsumedhourstaxable() {
	return consumedhourstaxable;
    }

    /**
     * consumedhourstaxable Create Date:2018/9/7
     * 
     * @param newConsumedhourstaxable
     *            nc.vo.pub.lang.UFDouble
     */
    public void setConsumedhourstaxable(nc.vo.pub.lang.UFDouble newConsumedhourstaxable) {
	this.consumedhourstaxable = newConsumedhourstaxable;
    }

    /**
     * remainhours Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getRemainhours() {
	return remainhours;
    }

    /**
     * remainhours Create Date:2018/9/7
     * 
     * @param newRemainhours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setRemainhours(nc.vo.pub.lang.UFDouble newRemainhours) {
	this.remainhours = newRemainhours;
    }

    /**
     * remainhourstaxfree Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getRemainhourstaxfree() {
	return remainhourstaxfree;
    }

    /**
     * remainhourstaxfree Create Date:2018/9/7
     * 
     * @param newRemainhourstaxfree
     *            nc.vo.pub.lang.UFDouble
     */
    public void setRemainhourstaxfree(nc.vo.pub.lang.UFDouble newRemainhourstaxfree) {
	this.remainhourstaxfree = newRemainhourstaxfree;
    }

    /**
     * remainhourstaxable Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getRemainhourstaxable() {
	return remainhourstaxable;
    }

    /**
     * remainhourstaxable Create Date:2018/9/7
     * 
     * @param newRemainhourstaxable
     *            nc.vo.pub.lang.UFDouble
     */
    public void setRemainhourstaxable(nc.vo.pub.lang.UFDouble newRemainhourstaxable) {
	this.remainhourstaxable = newRemainhourstaxable;
    }

    /**
     * remainamount Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getRemainamount() {
	return remainamount;
    }

    /**
     * remainamount Create Date:2018/9/7
     * 
     * @param newRemainamount
     *            nc.vo.pub.lang.UFDouble
     */
    public void setRemainamount(nc.vo.pub.lang.UFDouble newRemainamount) {
	this.remainamount = newRemainamount;
    }

    /**
     * remainamounttaxfree Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getRemainamounttaxfree() {
	return remainamounttaxfree;
    }

    /**
     * remainamounttaxfree Create Date:2018/9/7
     * 
     * @param newRemainamounttaxfree
     *            nc.vo.pub.lang.UFDouble
     */
    public void setRemainamounttaxfree(nc.vo.pub.lang.UFDouble newRemainamounttaxfree) {
	this.remainamounttaxfree = newRemainamounttaxfree;
    }

    /**
     * remainamounttaxable Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getRemainamounttaxable() {
	return remainamounttaxable;
    }

    /**
     * remainamounttaxable Create Date:2018/9/7
     * 
     * @param newRemainamounttaxable
     *            nc.vo.pub.lang.UFDouble
     */
    public void setRemainamounttaxable(nc.vo.pub.lang.UFDouble newRemainamounttaxable) {
	this.remainamounttaxable = newRemainamounttaxable;
    }

    /**
     * iscanceled Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFBoolean
     */
    public nc.vo.pub.lang.UFBoolean getIscanceled() {
	return iscanceled;
    }

    /**
     * iscanceled Create Date:2018/9/7
     * 
     * @param newIscanceled
     *            nc.vo.pub.lang.UFBoolean
     */
    public void setIscanceled(nc.vo.pub.lang.UFBoolean newIscanceled) {
	this.iscanceled = newIscanceled;
    }

    /**
     * iscompensation Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFBoolean
     */
    public nc.vo.pub.lang.UFBoolean getIscompensation() {
	return iscompensation;
    }

    /**
     * iscompensation Create Date:2018/9/7
     * 
     * @param newIscompensation
     *            nc.vo.pub.lang.UFBoolean
     */
    public void setIscompensation(nc.vo.pub.lang.UFBoolean newIscompensation) {
	this.iscompensation = newIscompensation;
    }

    /**
     * hourstorest Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getHourstorest() {
	return hourstorest;
    }

    /**
     * hourstorest Create Date:2018/9/7
     * 
     * @param newHourstorest
     *            nc.vo.pub.lang.UFDouble
     */
    public void setHourstorest(nc.vo.pub.lang.UFDouble newHourstorest) {
	this.hourstorest = newHourstorest;
    }

    /**
     * isconsumed Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFBoolean
     */
    public nc.vo.pub.lang.UFBoolean getIsconsumed() {
	return isconsumed;
    }

    /**
     * isconsumed Create Date:2018/9/7
     * 
     * @param newIsconsumed
     *            nc.vo.pub.lang.UFBoolean
     */
    public void setIsconsumed(nc.vo.pub.lang.UFBoolean newIsconsumed) {
	this.isconsumed = newIsconsumed;
    }

    /**
     * issettled Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFBoolean
     */
    public nc.vo.pub.lang.UFBoolean getIssettled() {
	return issettled;
    }

    /**
     * issettled Create Date:2018/9/7
     * 
     * @param newIssettled
     *            nc.vo.pub.lang.UFBoolean
     */
    public void setIssettled(nc.vo.pub.lang.UFBoolean newIssettled) {
	this.issettled = newIssettled;
    }

    /**
     * settledate Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFLiteralDate
     */
    public nc.vo.pub.lang.UFLiteralDate getSettledate() {
	return settledate;
    }

    /**
     * settledate Create Date:2018/9/7
     * 
     * @param newSettledate
     *            nc.vo.pub.lang.UFLiteralDate
     */
    public void setSettledate(nc.vo.pub.lang.UFLiteralDate newSettledate) {
	this.settledate = newSettledate;
    }

    public nc.vo.pub.lang.UFDouble getFrozenhours() {
	return frozenhours;
    }

    public void setFrozenhours(nc.vo.pub.lang.UFDouble frozenhours) {
	this.frozenhours = frozenhours;
    }

    public nc.vo.pub.lang.UFDouble getFrozenhourstaxfree() {
	return frozenhourstaxfree;
    }

    public void setFrozenhourstaxfree(nc.vo.pub.lang.UFDouble frozenhourstaxfree) {
	this.frozenhourstaxfree = frozenhourstaxfree;
    }

    public nc.vo.pub.lang.UFDouble getFrozenhourstaxable() {
	return frozenhourstaxable;
    }

    public void setFrozenhourstaxable(nc.vo.pub.lang.UFDouble frozenhourstaxable) {
	this.frozenhourstaxable = frozenhourstaxable;
    }

    public nc.vo.pub.lang.UFLiteralDate getExpirydate() {
	return expirydate;
    }

    public void setExpirydate(nc.vo.pub.lang.UFLiteralDate expirydate) {
	this.expirydate = expirydate;
    }

    public nc.vo.pub.lang.UFLiteralDate getApproveddate() {
	return approveddate;
    }

    public void setApproveddate(nc.vo.pub.lang.UFLiteralDate approveddate) {
	this.approveddate = approveddate;
    }

    /**
     * dr Create Date:2018/9/7
     * 
     * @return java.lang.Integer
     */
    public java.lang.Integer getDr() {
	return dr;
    }

    /**
     * dr Create Date:2018/9/7
     * 
     * @param newDr
     *            java.lang.Integer
     */
    public void setDr(java.lang.Integer newDr) {
	this.dr = newDr;
    }

    /**
     * ts Create Date:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDateTime
     */
    public nc.vo.pub.lang.UFDateTime getTs() {
	return ts;
    }

    /**
     * ts Create Date:2018/9/7
     * 
     * @param newTs
     *            nc.vo.pub.lang.UFDateTime
     */
    public void setTs(nc.vo.pub.lang.UFDateTime newTs) {
	this.ts = newTs;
    }

    /**
     * <p>
     * <p>
     * Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getParentPKFieldName() {
	return null;
    }

    /**
     * <p>
     * <p>
     * Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPKFieldName() {

	return "pk_segdetail";
    }

    /**
     * <p>
     * <p>
     * Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getTableName() {
	return "hrta_segdetail";
    }

    /**
     * <p>
     * <p>
     * Create Date:2018/9/7
     * 
     * @return java.lang.String
     */
    public static java.lang.String getDefaultTableName() {
	return "hrta_segdetail";
    }

    /**
     * 
     * 
     * Create Date:2018/9/7
     */
    public SegDetailVO() {
	super();
    }

    @nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.ta.overtime.SegDetailVO")
    public IVOMeta getMetaData() {
	return VOMetaFactory.getInstance().getVOMeta("overtime.segdetail");

    }

}
