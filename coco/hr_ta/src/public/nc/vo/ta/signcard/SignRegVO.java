/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.ta.signcard;

import nc.vo.ta.timebill.annotation.BillDateFieldName;
import nc.vo.ta.timebill.annotation.PkPsnjobFieldName;

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
@BillDateFieldName(fieldName="signdate")
@PkPsnjobFieldName(fieldName="pk_psnjob")
public class SignRegVO extends SignCommonVO{
	private java.lang.String pk_signreg;
	private java.lang.Integer billsource;
	private java.lang.String pk_billsourceh;
	private java.lang.String pk_billsourceb;
	private java.lang.String creator;
	private nc.vo.pub.lang.UFDateTime creationtime;
	private java.lang.String modifier;
	private nc.vo.pub.lang.UFDateTime modifiedtime;
	private String signarea;
	private String signshift;

	public static final String PK_SIGNREG = "pk_signreg";
	public static final String BILLSOURCE = "billsource";
	public static final String PK_BILLSOURCEH = "pk_billsourceh";
	public static final String PK_BILLSOURCEB = "pk_billsourceb";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String SIGNAREA="signarea";
	public static final String SIGNSHIFT="signshift";		
	/**
	 * 属性pk_signreg的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getPk_signreg () {
		return pk_signreg;
	}   
	/**
	 * 属性pk_signreg的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newPk_signreg java.lang.String
	 */
	public void setPk_signreg (java.lang.String newPk_signreg ) {
	 	this.pk_signreg = newPk_signreg;
	} 	
	/**
	 * 属性billsource的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getBillsource () {
		return billsource;
	}   
	/**
	 * 属性billsource的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newBillsource java.lang.Integer
	 */
	public void setBillsource (java.lang.Integer newBillsource ) {
	 	this.billsource = newBillsource;
	} 	  
	/**
	 * 属性pk_billsourceh的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getPk_billsourceh () {
		return pk_billsourceh;
	}   
	/**
	 * 属性pk_billsourceh的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newPk_billsourceh java.lang.String
	 */
	public void setPk_billsourceh (java.lang.String newPk_billsourceh ) {
	 	this.pk_billsourceh = newPk_billsourceh;
	} 	  
	/**
	 * 属性pk_billsourceb的Getter方法.
	 * 创建日期:$vmObject.createdDate
	 * @return java.lang.String
	 */
	public java.lang.String getPk_billsourceb () {
		return pk_billsourceb;
	}   
	/**
	 * 属性pk_billsourceb的Setter方法.
	 * 创建日期:$vmObject.createdDate
	 * @param newPk_billsourceb java.lang.String
	 */
	public void setPk_billsourceb (java.lang.String newPk_billsourceb ) {
	 	this.pk_billsourceb = newPk_billsourceb;
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
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:${vmObject.createdDate}
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
	  return "pk_signreg";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:${vmObject.createdDate}
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "tbm_signreg";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:${vmObject.createdDate}
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "tbm_signreg";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:${vmObject.createdDate}
	  */
     public SignRegVO() {
		super();	
	}
     
    @Override
    public int hashCode() {
    	return super.hashCode();
    }
    
    @Override
 	public boolean equals(Object obj) {
 		if(obj == null || !(obj instanceof SignRegVO))
 				return false;
 		SignRegVO compVo = (SignRegVO)obj;
 		//如果有主键，则主键相等即可
 		if(getPk_signreg() != null && compVo.getPk_signreg() != null ) {
 			return getPk_signreg().equals(compVo.getPk_signreg());
 		}
 		//如果没有主键，则组织、人员，签卡时间相等即算相等
 		if(getPk_psndoc() != null && compVo.getPk_psndoc() != null) {
 			return getPk_psndoc().equals(compVo.getPk_psndoc());
 		}
 		if(getSigntime() != null && compVo.getSigntime() != null) {
 			return getSigntime().equals(compVo.getSigntime());
 		}
 		return false;
 	}
	@Override
	public boolean isAppBill() {
		return false;
	}
	@Override
	public void setAppBill(boolean isAppBill) {
		
	}
	public String getSignarea() {
		return signarea;
	}
	public void setSignarea(String signarea) {
		this.signarea = signarea;
	}
	public String getSignshift() {
		return signshift;
	}
	public void setSignshift(String signshift) {
		this.signshift = signshift;
	}
	
	
} 


