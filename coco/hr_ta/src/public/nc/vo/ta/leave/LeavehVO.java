/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.ta.leave;
	
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.annotation.IDColumn;
import nc.vo.ta.annotation.Table;
import nc.vo.ta.bill.ITimeScopeBillHeadVO;
import nc.vo.ta.timebill.annotation.ApproveStatusFieldName;
import nc.vo.ta.timebill.annotation.BillCodeFieldName;
import nc.vo.ta.timebill.annotation.PkPsndocFieldName;
import nc.vo.ta.timebill.annotation.PkPsnjobFieldName;
import nc.vo.ta.timebill.annotation.PkTimeItemFieldName;
import nc.vo.ta.timebill.annotation.TransTypeIDFieldName;

import org.apache.commons.lang.StringUtils;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:${vmObject.createdDate}
 * @author 
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
@Table(tableName="tbm_leaveh")
@IDColumn(idColumn="pk_leaveh")
@PkPsndocFieldName(fieldName="pk_psndoc")
@PkPsnjobFieldName(fieldName="pk_psnjob")
@BillCodeFieldName(fieldName="bill_code")
@PkTimeItemFieldName(fieldName="pk_leavetype")
@ApproveStatusFieldName(fieldName="approve_state")
@TransTypeIDFieldName(fieldName="transtypeid")
public class LeavehVO extends SuperVO implements ITimeScopeBillHeadVO{
	private java.lang.String pk_leaveh;
	private java.lang.String pk_group;
	private java.lang.String pk_org;
	private java.lang.String bill_code;
	private nc.vo.pub.lang.UFLiteralDate apply_date;
	private java.lang.String billmaker;
	private java.lang.String pk_psndoc;
	private String pk_psnorg;
	private java.lang.String pk_psnjob;
	private java.lang.String pk_leavetype;
	private java.lang.String pk_leavetypecopy;
	private java.lang.String leaveyear;
	private java.lang.String leavemonth;
	private Integer leaveindex;
	private nc.vo.pub.lang.UFDouble sumhour;
	private java.lang.String leaveremark;
	private java.lang.String relatetel;

	private UFDouble lactationhour;
	private UFDouble resteddayorhour;//已休时长
	private UFDouble realdayorhour;//享有时长
	private UFDouble restdayorhour;//结余时长
	private UFDouble freezedayorhour;//冻结时长
	private UFDouble usefuldayorhour;//可用时长
	private nc.vo.pub.lang.UFBoolean islactation;
	private java.lang.String fun_code;
	private nc.vo.pub.lang.UFBoolean ishrssbill;
	private java.lang.String transtype;
	private java.lang.String transtypeid;
	private java.lang.String pk_billtype;
	
	private java.lang.Integer approve_state;
	private java.lang.String approver;
	private java.lang.String approve_note;
	private nc.vo.pub.lang.UFDateTime approve_time;
	private nc.vo.pub.lang.UFDateTime creationtime;
	private java.lang.String creator;
	private nc.vo.pub.lang.UFDateTime modifiedtime;
	private java.lang.String modifier;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;
	private java.lang.String leaveplan;
	
	private java.lang.String pk_org_v; //组织多版本
	private java.lang.String pk_dept_v;//部门多版本
	
	//拆单id。在同一次拆单操作中生成的所有单据（包括申请单和登记单），都用同一个id。
	//并且在后续的修改单据时中新拆出来的单子，也使用此id
	//此id的作用是标志出所有一次拆成的单据。目前没有具体的作用
	private String splitid;

	public static final String PK_LEAVEH = "pk_leaveh";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String BILL_CODE = "bill_code";
	public static final String APPLY_DATE = "apply_date";
	public static final String BILLMAKER = "billmaker";
	public static final String PK_PSNDOC = "pk_psndoc";
	public static final String PK_PSNORG = "pk_psnorg";
	public static final String PK_PSNJOB = "pk_psnjob";
	public static final String PK_LEAVETYPE = "pk_leavetype";
	public static final String PK_LEAVETYPECOPY = "pk_leavetypecopy";
	public static final String LEAVEYEAR = "leaveyear";
	public static final String LEAVEMONTH = "leavemonth";
	public static final String LEAVEINDEX = "leaveindex";
	public static final String LEAVEREMARK = "leaveremark";
	public static final String RELATETEL = "relatetel";
	public static final String SUMHOUR = "sumhour";
	public static final String RESTEDDAYORHOUR = "resteddayorhour";//已休时长
	public static final String REALDAYORHOUR = "realdayorhour";//享有时长
	public static final String RESTDAYORHOUR = "restdayorhour";//结余时长
	public static final String FREEZEDAYORHOUR = "freezedayorhour";//冻结时长
	public static final String USEFULDAYORHOUR = "usefuldayorhour";//可用时长
	public static final String LACTATIONHOUR = "lactationhour";
	public static final String ISLACTATION = "islactation";
//	public static final String PK_AGENTPSN = "pk_agentpsn";
//	public static final String WORKPROCESS = "workprocess";
	public static final String FUN_CODE = "fun_code";
	public static final String ISHRSSBILL = "ishrssbill";
	public static final String TRANSTYPE = "transtype";
	public static final String TRANSTYPEID = "transtypeid";
	public static final String PK_BILLTYPE = "pk_billtype";
	public static final String APPROVE_STATE = "approve_state";
	public static final String APPROVER = "approver";
	public static final String APPROVE_NOTE = "approve_note";
	public static final String APPROVE_TIME = "approve_time";
	public static final String CREATIONTIME = "creationtime";
	public static final String CREATOR = "creator";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String MODIFIER = "modifier";
	public static final String LEAVEPLAN = "leaveplan";
	
	public static final String PK_ORG_V = "pk_org_v";
	public static final String PK_DEPT_V= "pk_dept_v";
	public static final String SPLITID= "splitid";
			
	/**
	 * 属性pk_leaveh的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getPk_leaveh () {
		return pk_leaveh;
	}   
	/**
	 * 属性pk_leaveh的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newPk_leaveh java.lang.String
	 */
	public void setPk_leaveh (java.lang.String newPk_leaveh ) {
	 	this.pk_leaveh = newPk_leaveh;
	} 	  
	/**
	 * 属性pk_group的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group () {
		return pk_group;
	}   
	/**
	 * 属性pk_group的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newPk_group java.lang.String
	 */
	public void setPk_group (java.lang.String newPk_group ) {
	 	this.pk_group = newPk_group;
	} 	  
	/**
	 * 属性pk_org的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org () {
		return pk_org;
	}   
	/**
	 * 属性pk_org的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newPk_org java.lang.String
	 */
	public void setPk_org (java.lang.String newPk_org ) {
	 	this.pk_org = newPk_org;
	} 	  
	/**
	 * 属性bill_code的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getBill_code () {
		return bill_code;
	}   
	/**
	 * 属性bill_code的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newBill_code java.lang.String
	 */
	public void setBill_code (java.lang.String newBill_code ) {
	 	this.bill_code = newBill_code;
	} 	  
	/**
	 * 属性apply_date的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFLiteralDate getApply_date () {
		return apply_date;
	}   
	/**
	 * 属性apply_date的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newApply_date nc.vo.pub.lang.UFDate
	 */
	public void setApply_date (nc.vo.pub.lang.UFLiteralDate newApply_date ) {
	 	this.apply_date = newApply_date;
	} 	  
	/**
	 * 属性billmaker的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getBillmaker () {
		return billmaker;
	}   
	/**
	 * 属性billmaker的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newBillmaker java.lang.String
	 */
	public void setBillmaker (java.lang.String newBillmaker ) {
	 	this.billmaker = newBillmaker;
	} 	  
	/**
	 * 属性pk_psndoc的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getPk_psndoc () {
		return pk_psndoc;
	}   
	/**
	 * 属性pk_psndoc的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newPk_psndoc java.lang.String
	 */
	public void setPk_psndoc (java.lang.String newPk_psndoc ) {
	 	this.pk_psndoc = newPk_psndoc;
	} 	  
	/**
	 * 属性pk_psnjob的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getPk_psnjob () {
		return pk_psnjob;
	}   
	/**
	 * 属性pk_psnjob的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newPk_psnjob java.lang.String
	 */
	public void setPk_psnjob (java.lang.String newPk_psnjob ) {
	 	this.pk_psnjob = newPk_psnjob;
	} 	  
	/**
	 * 属性pk_leavetype的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getPk_leavetype () {
		return pk_leavetype;
	}   
	/**
	 * 属性pk_leavetype的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newPk_leavetype java.lang.String
	 */
	public void setPk_leavetype (java.lang.String newPk_leavetype ) {
	 	this.pk_leavetype = newPk_leavetype;
	} 	  
	/**
	 * 属性pk_leavetypecopy的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getPk_leavetypecopy () {
		return pk_leavetypecopy;
	}   
	/**
	 * 属性pk_leavetypecopy的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newPk_leavetypecopy java.lang.String
	 */
	public void setPk_leavetypecopy (java.lang.String newPk_leavetypecopy ) {
	 	this.pk_leavetypecopy = newPk_leavetypecopy;
	} 	  
	/**
	 * 属性leaveyear的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getLeaveyear () {
		return leaveyear;
	}   
	/**
	 * 属性leaveyear的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newLeaveyear java.lang.String
	 */
	public void setLeaveyear (java.lang.String newLeaveyear ) {
	 	this.leaveyear = newLeaveyear;
	} 	  
	/**
	 * 属性leavemonth的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getLeavemonth () {
		return leavemonth;
	}   
	/**
	 * 属性leavemonth的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newLeavemonth java.lang.String
	 */
	public void setLeavemonth (java.lang.String newLeavemonth ) {
	 	this.leavemonth = newLeavemonth;
	} 	  
	/**
	 * 属性sumhour的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getSumhour () {
		return sumhour;
	}   
	/**
	 * 属性sumhour的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newSumhour nc.vo.pub.lang.UFDouble
	 */
	public void setSumhour (nc.vo.pub.lang.UFDouble newSumhour ) {
	 	this.sumhour = newSumhour;
	} 	  
	/**
	 * 属性leaveremark的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getLeaveremark () {
		return leaveremark;
	}   
	/**
	 * 属性leaveremark的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newLeaveremark java.lang.String
	 */
	public void setLeaveremark (java.lang.String newLeaveremark ) {
	 	this.leaveremark = newLeaveremark;
	} 	  
	/**
	 * 属性relatetel的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getRelatetel () {
		return relatetel;
	}   
	/**
	 * 属性relatetel的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newRelatetel java.lang.String
	 */
	public void setRelatetel (java.lang.String newRelatetel ) {
	 	this.relatetel = newRelatetel;
	} 	  

	/**
	 * 属性islactation的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getIslactation () {
		return islactation;
	}   
	/**
	 * 属性islactation的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newIslactation nc.vo.pub.lang.UFBoolean
	 */
	public void setIslactation (nc.vo.pub.lang.UFBoolean newIslactation ) {
	 	this.islactation = newIslactation;
	} 	  
	/**
	 * 属性fun_code的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getFun_code () {
		return fun_code;
	}   
	/**
	 * 属性fun_code的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newFun_code java.lang.String
	 */
	public void setFun_code (java.lang.String newFun_code ) {
	 	this.fun_code = newFun_code;
	} 	  
	/**
	 * 属性ishrssbill的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getIshrssbill () {
		return ishrssbill;
	}   
	/**
	 * 属性ishrssbill的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newIshrssbill nc.vo.pub.lang.UFBoolean
	 */
	public void setIshrssbill (nc.vo.pub.lang.UFBoolean newIshrssbill ) {
	 	this.ishrssbill = newIshrssbill;
	} 	  
	/**
	 * 属性transtype的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getTranstype () {
		return transtype;
	}   
	/**
	 * 属性transtype的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newTranstype java.lang.String
	 */
	public void setTranstype (java.lang.String newTranstype ) {
	 	this.transtype = newTranstype;
	} 	  
	/**
	 * 属性approve_state的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getApprove_state () {
		return approve_state;
	}   
	/**
	 * 属性approve_state的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newApprove_state java.lang.Integer
	 */
	public void setApprove_state (java.lang.Integer newApprove_state ) {
	 	this.approve_state = newApprove_state;
	} 	  
	/**
	 * 属性approver的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getApprover () {
		return approver;
	}   
	/**
	 * 属性approver的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newApprover java.lang.String
	 */
	public void setApprover (java.lang.String newApprover ) {
	 	this.approver = newApprover;
	} 	  
	/**
	 * 属性approve_note的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getApprove_note () {
		return approve_note;
	}   
	/**
	 * 属性approve_note的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newApprove_note java.lang.String
	 */
	public void setApprove_note (java.lang.String newApprove_note ) {
	 	this.approve_note = newApprove_note;
	} 	  
	/**
	 * 属性approve_time的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getApprove_time () {
		return approve_time;
	}   
	/**
	 * 属性approve_time的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newApprove_time nc.vo.pub.lang.UFDateTime
	 */
	public void setApprove_time (nc.vo.pub.lang.UFDateTime newApprove_time ) {
	 	this.approve_time = newApprove_time;
	} 	  
	/**
	 * 属性creationtime的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getCreationtime () {
		return creationtime;
	}   
	/**
	 * 属性creationtime的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newCreationtime nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime (nc.vo.pub.lang.UFDateTime newCreationtime ) {
	 	this.creationtime = newCreationtime;
	} 	  
	/**
	 * 属性creator的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getCreator () {
		return creator;
	}   
	/**
	 * 属性creator的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newCreator java.lang.String
	 */
	public void setCreator (java.lang.String newCreator ) {
	 	this.creator = newCreator;
	} 	  
	/**
	 * 属性modifiedtime的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getModifiedtime () {
		return modifiedtime;
	}   
	/**
	 * 属性modifiedtime的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newModifiedtime nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime (nc.vo.pub.lang.UFDateTime newModifiedtime ) {
	 	this.modifiedtime = newModifiedtime;
	} 	  
	/**
	 * 属性modifier的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getModifier () {
		return modifier;
	}   
	/**
	 * 属性modifier的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newModifier java.lang.String
	 */
	public void setModifier (java.lang.String newModifier ) {
	 	this.modifier = newModifier;
	} 	  
	/**
	 * 属性dr的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newDr java.lang.Integer
	 */
	public void setDr (java.lang.Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性ts的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newTs nc.vo.pub.lang.UFDateTime
	 */
	public void setTs (nc.vo.pub.lang.UFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:${vmObject.createdDate}
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:${vmObject.createdDate}
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
	  return "pk_leaveh";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:${vmObject.createdDate}
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "tbm_leaveh";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:${vmObject.createdDate}
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "tbm_leaveh";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:${vmObject.createdDate}
	  */
     public LeavehVO() {
		super();	
	}
	public UFDouble getResteddayorhour() {
		return resteddayorhour;
	}
	public void setResteddayorhour(UFDouble resteddayorhour) {
		this.resteddayorhour = resteddayorhour;
	}
	public UFDouble getRealdayorhour() {
		return realdayorhour;
	}
	public void setRealdayorhour(UFDouble realdayorhour) {
		this.realdayorhour = realdayorhour;
	}
	public UFDouble getRestdayorhour() {
		return restdayorhour;
	}
	public void setRestdayorhour(UFDouble restdayorhour) {
		this.restdayorhour = restdayorhour;
	}
	public UFDouble getLactationhour() {
		return lactationhour;
	}
	public void setLactationhour(UFDouble lactationhour) {
		this.lactationhour = lactationhour;
	}
	@Override
	public UFDouble getLength() {
		return sumhour;
	}
	@Override
	public void setLength(UFDouble len) {
		sumhour=len;
	}
	@Override
	public String getPk_timeitem() {
		return pk_leavetype;
	}
	public java.lang.String getTranstypeid() {
		return transtypeid;
	}
	public void setTranstypeid(java.lang.String transtypeid) {
		this.transtypeid = transtypeid;
	}
	public java.lang.String getPk_billtype() {
		return pk_billtype;
	}
	public void setPk_billtype(java.lang.String pk_billtype) {
		this.pk_billtype = pk_billtype;
	}
	public Integer getLeaveindex() {
		return leaveindex;
	}
	public void setLeaveindex(Integer leaveindex) {
		this.leaveindex = leaveindex;
	}
	public String getPk_psnorg() {
		return pk_psnorg;
	}
	public void setPk_psnorg(String pkPsnorg) {
		pk_psnorg = pkPsnorg;
	}
	public java.lang.String getPk_org_v() {
		return pk_org_v;
	}
	public void setPk_org_v(java.lang.String pkOrgV) {
		pk_org_v = pkOrgV;
	}
	public java.lang.String getPk_dept_v() {
		return pk_dept_v;
	}
	public void setPk_dept_v(java.lang.String pkDeptV) {
		pk_dept_v = pkDeptV;
	}
	public String getSplitid() {
		return splitid;
	}
	public void setSplitid(String splitid) {
		this.splitid = splitid;
	}
	public UFDouble getFreezedayorhour() {
		return freezedayorhour;
	}
	public void setFreezedayorhour(UFDouble freezedayorhour) {
		this.freezedayorhour = freezedayorhour;
	}
	public UFDouble getUsefuldayorhour() {
		return usefuldayorhour;
	}
	public void setUsefuldayorhour(UFDouble usefuldayorhour) {
		this.usefuldayorhour = usefuldayorhour;
	}
	public String getLeaveplan(){
		return leaveplan;
	}
	public void setLeaveplan(String leaveplan){
		this.leaveplan=leaveplan;
	}
	public String getYearmonth(){
		if(StringUtils.isEmpty(getLeavemonth()))
			return getLeaveyear();
		return getLeaveyear()+getLeavemonth();
	}
 
} 


