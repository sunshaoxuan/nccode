package nc.vo.ta.overtime;

import nc.vo.pub.IVOMeta;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> ��̎��Ҫ��������� </b>
 * <p>
 * ��̎�����������Ϣ
 * </p>
 * ��������:2018/9/7
 * 
 * @author
 * @version NCPrj ??
 */
public class SegDetailConsumeVO extends nc.vo.pub.SuperVO {

    private java.lang.String pk_segdetail;
    private java.lang.String pk_segdetailconsume;
    private java.lang.String pk_group;
    private java.lang.String pk_org;
    private java.lang.String pk_org_v;
    private java.lang.String creator;
    private nc.vo.pub.lang.UFDateTime creationtime;
    private java.lang.String modifier;
    private nc.vo.pub.lang.UFDateTime modifiedtime;
    private java.lang.String rowno;
    private nc.vo.pub.lang.UFLiteralDate bizdate;
    private java.lang.String pk_leavereg;
    private java.lang.Integer biztype;
    private nc.vo.pub.lang.UFDouble consumedhours;
    private nc.vo.pub.lang.UFDouble reversedhours;
    private java.lang.Integer dr = 0;
    private nc.vo.pub.lang.UFDateTime ts;

    public static final String PK_SEGDETAIL = "pk_segdetail";
    public static final String PK_SEGDETAILCONSUME = "pk_segdetailconsume";
    public static final String PK_GROUP = "pk_group";
    public static final String PK_ORG = "pk_org";
    public static final String PK_ORG_V = "pk_org_v";
    public static final String CREATOR = "creator";
    public static final String CREATIONTIME = "creationtime";
    public static final String MODIFIER = "modifier";
    public static final String MODIFIEDTIME = "modifiedtime";
    public static final String ROWNO = "rowno";
    public static final String BIZDATE = "bizdate";
    public static final String PK_LEAVEREG = "pk_leavereg";
    public static final String BIZTYPE = "biztype";
    public static final String CONSUMEDHOURS = "consumedhours";
    public static final String REVERSEDHOURS = "reversedhours";

    /**
     * ���� pk_segdetail��Getter����.��������parentPK ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_segdetail() {
	return pk_segdetail;
    }

    /**
     * ����pk_segdetail��Setter����.��������parentPK ��������:2018/9/7
     * 
     * @param newPk_segdetail
     *            java.lang.String
     */
    public void setPk_segdetail(java.lang.String newPk_segdetail) {
	this.pk_segdetail = newPk_segdetail;
    }

    /**
     * ���� pk_segdetailconsume��Getter����.���������Ӱ�ֶκ��Nӛ����I ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_segdetailconsume() {
	return pk_segdetailconsume;
    }

    /**
     * ����pk_segdetailconsume��Setter����.���������Ӱ�ֶκ��Nӛ����I ��������:2018/9/7
     * 
     * @param newPk_segdetailconsume
     *            java.lang.String
     */
    public void setPk_segdetailconsume(java.lang.String newPk_segdetailconsume) {
	this.pk_segdetailconsume = newPk_segdetailconsume;
    }

    /**
     * ���� pk_group��Getter����.�����������F ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_group() {
	return pk_group;
    }

    /**
     * ����pk_group��Setter����.�����������F ��������:2018/9/7
     * 
     * @param newPk_group
     *            java.lang.String
     */
    public void setPk_group(java.lang.String newPk_group) {
	this.pk_group = newPk_group;
    }

    /**
     * ���� pk_org��Getter����.���������M�� ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_org() {
	return pk_org;
    }

    /**
     * ����pk_org��Setter����.���������M�� ��������:2018/9/7
     * 
     * @param newPk_org
     *            java.lang.String
     */
    public void setPk_org(java.lang.String newPk_org) {
	this.pk_org = newPk_org;
    }

    /**
     * ���� pk_org_v��Getter����.���������M���汾 ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_org_v() {
	return pk_org_v;
    }

    /**
     * ����pk_org_v��Setter����.���������M���汾 ��������:2018/9/7
     * 
     * @param newPk_org_v
     *            java.lang.String
     */
    public void setPk_org_v(java.lang.String newPk_org_v) {
	this.pk_org_v = newPk_org_v;
    }

    /**
     * ���� creator��Getter����.�������������� ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getCreator() {
	return creator;
    }

    /**
     * ����creator��Setter����.�������������� ��������:2018/9/7
     * 
     * @param newCreator
     *            java.lang.String
     */
    public void setCreator(java.lang.String newCreator) {
	this.creator = newCreator;
    }

    /**
     * ���� creationtime��Getter����.�������������r�g ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDateTime
     */
    public nc.vo.pub.lang.UFDateTime getCreationtime() {
	return creationtime;
    }

    /**
     * ����creationtime��Setter����.�������������r�g ��������:2018/9/7
     * 
     * @param newCreationtime
     *            nc.vo.pub.lang.UFDateTime
     */
    public void setCreationtime(nc.vo.pub.lang.UFDateTime newCreationtime) {
	this.creationtime = newCreationtime;
    }

    /**
     * ���� modifier��Getter����.���������޸��� ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getModifier() {
	return modifier;
    }

    /**
     * ����modifier��Setter����.���������޸��� ��������:2018/9/7
     * 
     * @param newModifier
     *            java.lang.String
     */
    public void setModifier(java.lang.String newModifier) {
	this.modifier = newModifier;
    }

    /**
     * ���� modifiedtime��Getter����.���������޸ĕr�g ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDateTime
     */
    public nc.vo.pub.lang.UFDateTime getModifiedtime() {
	return modifiedtime;
    }

    /**
     * ����modifiedtime��Setter����.���������޸ĕr�g ��������:2018/9/7
     * 
     * @param newModifiedtime
     *            nc.vo.pub.lang.UFDateTime
     */
    public void setModifiedtime(nc.vo.pub.lang.UFDateTime newModifiedtime) {
	this.modifiedtime = newModifiedtime;
    }

    /**
     * ���� rowno��Getter����.����������̖ ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getRowno() {
	return rowno;
    }

    /**
     * ����rowno��Setter����.����������̖ ��������:2018/9/7
     * 
     * @param newRowno
     *            java.lang.String
     */
    public void setRowno(java.lang.String newRowno) {
	this.rowno = newRowno;
    }

    /**
     * ���� bizdate��Getter����.����������ӛ���� ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFLiteralDate
     */
    public nc.vo.pub.lang.UFLiteralDate getBizdate() {
	return bizdate;
    }

    /**
     * ����bizdate��Setter����.����������ӛ���� ��������:2018/9/7
     * 
     * @param newBizdate
     *            nc.vo.pub.lang.UFLiteralDate
     */
    public void setBizdate(nc.vo.pub.lang.UFLiteralDate newBizdate) {
	this.bizdate = newBizdate;
    }

    /**
     * ���� pk_leavereg��Getter����.���������ݼ�Դ�� ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPk_leavereg() {
	return pk_leavereg;
    }

    /**
     * ����pk_leavereg��Setter����.���������ݼ�Դ�� ��������:2018/9/7
     * 
     * @param newPk_leavereg
     *            java.lang.String
     */
    public void setPk_leavereg(java.lang.String newPk_leavereg) {
	this.pk_leavereg = newPk_leavereg;
    }

    /**
     * ���� biztype��Getter����.���������I����� ��������:2018/9/7
     * 
     * @return java.lang.Integer
     */
    public java.lang.Integer getBiztype() {
	return biztype;
    }

    /**
     * ����biztype��Setter����.���������I����� ��������:2018/9/7
     * 
     * @param newBiztype
     *            java.lang.Integer
     */
    public void setBiztype(java.lang.Integer newBiztype) {
	this.biztype = newBiztype;
    }

    /**
     * ���� consumedhours��Getter����.�����������N�r�L ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getConsumedhours() {
	return consumedhours;
    }

    /**
     * ����consumedhours��Setter����.�����������N�r�L ��������:2018/9/7
     * 
     * @param newConsumedhours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setConsumedhours(nc.vo.pub.lang.UFDouble newConsumedhours) {
	this.consumedhours = newConsumedhours;
    }

    /**
     * ���� reversedhours��Getter����.�������������N�r�L ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDouble
     */
    public nc.vo.pub.lang.UFDouble getReversedhours() {
	return reversedhours;
    }

    /**
     * ����reversedhours��Setter����.�������������N�r�L ��������:2018/9/7
     * 
     * @param newReversedhours
     *            nc.vo.pub.lang.UFDouble
     */
    public void setReversedhours(nc.vo.pub.lang.UFDouble newReversedhours) {
	this.reversedhours = newReversedhours;
    }

    /**
     * ���� dr��Getter����.��������dr ��������:2018/9/7
     * 
     * @return java.lang.Integer
     */
    public java.lang.Integer getDr() {
	return dr;
    }

    /**
     * ����dr��Setter����.��������dr ��������:2018/9/7
     * 
     * @param newDr
     *            java.lang.Integer
     */
    public void setDr(java.lang.Integer newDr) {
	this.dr = newDr;
    }

    /**
     * ���� ts��Getter����.��������ts ��������:2018/9/7
     * 
     * @return nc.vo.pub.lang.UFDateTime
     */
    public nc.vo.pub.lang.UFDateTime getTs() {
	return ts;
    }

    /**
     * ����ts��Setter����.��������ts ��������:2018/9/7
     * 
     * @param newTs
     *            nc.vo.pub.lang.UFDateTime
     */
    public void setTs(nc.vo.pub.lang.UFDateTime newTs) {
	this.ts = newTs;
    }

    /**
     * <p>
     * ȡ�ø�VO���I�ֶ�.
     * <p>
     * ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getParentPKFieldName() {
	return "pk_segdetail";
    }

    /**
     * <p>
     * ȡ�ñ����I.
     * <p>
     * ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getPKFieldName() {

	return "pk_segdetailconsume";
    }

    /**
     * <p>
     * ���ر����Q
     * <p>
     * ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public java.lang.String getTableName() {
	return "hrta_segdetailconsume";
    }

    /**
     * <p>
     * ���ر����Q.
     * <p>
     * ��������:2018/9/7
     * 
     * @return java.lang.String
     */
    public static java.lang.String getDefaultTableName() {
	return "hrta_segdetailconsume";
    }

    /**
     * ����Ĭ�J��ʽ����������.
     * 
     * ��������:2018/9/7
     */
    public SegDetailConsumeVO() {
	super();
    }

    @nc.vo.annotation.MDEntityInfo(beanFullclassName = "nc.vo.ta.overtime.SegDetailConsumeVO")
    public IVOMeta getMetaData() {
	return VOMetaFactory.getInstance().getVOMeta("overtime.segdetailconsume");

    }

}