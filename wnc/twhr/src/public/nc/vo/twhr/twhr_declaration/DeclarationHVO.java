package nc.vo.twhr.twhr_declaration;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

public class DeclarationHVO extends SuperVO {
/**
*审批时间
*/
public static final String APPROVEDATE="approvedate";
/**
*审批批语
*/
public static final String APPROVENOTE="approvenote";
/**
*审批人
*/
public static final String APPROVER="approver";
/**
*审批状态
*/
public static final String APPROVESTATUS="approvestatus";
/**
*单据日期
*/
public static final String BILLDATE="billdate";
/**
*单据ID
*/
public static final String BILLID="billid";
/**
*制单人
*/
public static final String BILLMAKER="billmaker";
/**
*单据号
*/
public static final String BILLNO="billno";
/**
*单据类型
*/
public static final String BILLTYPE="billtype";
/**
*单据版本pk
*/
public static final String BILLVERSIONPK="billversionpk";
/**
*业务类型
*/
public static final String BUSITYPE="busitype";
/**
*编码
*/
public static final String CODE="code";
/**
*创建时间
*/
public static final String CREATIONTIME="creationtime";
/**
*创建人
*/
public static final String CREATOR="creator";
/**
*修订枚举
*/
public static final String EMENDENUM="emendenum";
/**
*最后修改时间
*/
public static final String LASTMAKETIME="lastmaketime";
/**
*制单时间
*/
public static final String MAKETIME="maketime";
/**
*修改时间
*/
public static final String MODIFIEDTIME="modifiedtime";
/**
*修改人
*/
public static final String MODIFIER="modifier";
/**
*名称
*/
public static final String NAME="name";
/**
*主表主键
*/
public static final String PK_DECLARATION="pk_declaration";
/**
*集团
*/
public static final String PK_GROUP="pk_group";
/**
*组织
*/
public static final String PK_ORG="pk_org";
/**
*组织版本
*/
public static final String PK_ORG_V="pk_org_v";
/**
*所属组织
*/
public static final String PKORG="pkorg";
/**
*来源单据id
*/
public static final String SRCBILLID="srcbillid";
/**
*来源单据类型
*/
public static final String SRCBILLTYPE="srcbilltype";
/**
*交易类型
*/
public static final String TRANSTYPE="transtype";
/**
*交易类型pk
*/
public static final String TRANSTYPEPK="transtypepk";
/**
*时间戳
*/
public static final String TS="ts";
/**
*自定义项1
*/
public static final String VDEF1="vdef1";
/**
*自定义项10
*/
public static final String VDEF10="vdef10";
/**
*自定义项11
*/
public static final String VDEF11="vdef11";
/**
*自定义项12
*/
public static final String VDEF12="vdef12";
/**
*自定义项13
*/
public static final String VDEF13="vdef13";
/**
*自定义项14
*/
public static final String VDEF14="vdef14";
/**
*自定义项15
*/
public static final String VDEF15="vdef15";
/**
*自定义项16
*/
public static final String VDEF16="vdef16";
/**
*自定义项17
*/
public static final String VDEF17="vdef17";
/**
*自定义项18
*/
public static final String VDEF18="vdef18";
/**
*自定义项19
*/
public static final String VDEF19="vdef19";
/**
*自定义项2
*/
public static final String VDEF2="vdef2";
/**
*自定义项20
*/
public static final String VDEF20="vdef20";
/**
*自定义项3
*/
public static final String VDEF3="vdef3";
/**
*自定义项4
*/
public static final String VDEF4="vdef4";
/**
*自定义项5
*/
public static final String VDEF5="vdef5";
/**
*自定义项6
*/
public static final String VDEF6="vdef6";
/**
*自定义项7
*/
public static final String VDEF7="vdef7";
/**
*自定义项8
*/
public static final String VDEF8="vdef8";
/**
*自定义项9
*/
public static final String VDEF9="vdef9";
/** 
* 获取审批时间
*
* @return 审批时间
*/
public UFDateTime getApprovedate () {
return (UFDateTime) this.getAttributeValue( DeclarationHVO.APPROVEDATE);
 } 

/** 
* 设置审批时间
*
* @param approvedate 审批时间
*/
public void setApprovedate ( UFDateTime approvedate) {
this.setAttributeValue( DeclarationHVO.APPROVEDATE,approvedate);
 } 

/** 
* 获取审批批语
*
* @return 审批批语
*/
public String getApprovenote () {
return (String) this.getAttributeValue( DeclarationHVO.APPROVENOTE);
 } 

/** 
* 设置审批批语
*
* @param approvenote 审批批语
*/
public void setApprovenote ( String approvenote) {
this.setAttributeValue( DeclarationHVO.APPROVENOTE,approvenote);
 } 

/** 
* 获取审批人
*
* @return 审批人
*/
public String getApprover () {
return (String) this.getAttributeValue( DeclarationHVO.APPROVER);
 } 

/** 
* 设置审批人
*
* @param approver 审批人
*/
public void setApprover ( String approver) {
this.setAttributeValue( DeclarationHVO.APPROVER,approver);
 } 

/** 
* 获取审批状态
*
* @return 审批状态
* @see String
*/
public Integer getApprovestatus () {
return (Integer) this.getAttributeValue( DeclarationHVO.APPROVESTATUS);
 } 

/** 
* 设置审批状态
*
* @param approvestatus 审批状态
* @see String
*/
public void setApprovestatus ( Integer approvestatus) {
this.setAttributeValue( DeclarationHVO.APPROVESTATUS,approvestatus);
 } 

/** 
* 获取单据日期
*
* @return 单据日期
*/
public UFDate getBilldate () {
return (UFDate) this.getAttributeValue( DeclarationHVO.BILLDATE);
 } 

/** 
* 设置单据日期
*
* @param billdate 单据日期
*/
public void setBilldate ( UFDate billdate) {
this.setAttributeValue( DeclarationHVO.BILLDATE,billdate);
 } 

/** 
* 获取单据ID
*
* @return 单据ID
*/
public String getBillid () {
return (String) this.getAttributeValue( DeclarationHVO.BILLID);
 } 

/** 
* 设置单据ID
*
* @param billid 单据ID
*/
public void setBillid ( String billid) {
this.setAttributeValue( DeclarationHVO.BILLID,billid);
 } 

/** 
* 获取制单人
*
* @return 制单人
*/
public String getBillmaker () {
return (String) this.getAttributeValue( DeclarationHVO.BILLMAKER);
 } 

/** 
* 设置制单人
*
* @param billmaker 制单人
*/
public void setBillmaker ( String billmaker) {
this.setAttributeValue( DeclarationHVO.BILLMAKER,billmaker);
 } 

/** 
* 获取单据号
*
* @return 单据号
*/
public String getBillno () {
return (String) this.getAttributeValue( DeclarationHVO.BILLNO);
 } 

/** 
* 设置单据号
*
* @param billno 单据号
*/
public void setBillno ( String billno) {
this.setAttributeValue( DeclarationHVO.BILLNO,billno);
 } 

/** 
* 获取单据类型
*
* @return 单据类型
*/
public String getBilltype () {
return (String) this.getAttributeValue( DeclarationHVO.BILLTYPE);
 } 

/** 
* 设置单据类型
*
* @param billtype 单据类型
*/
public void setBilltype ( String billtype) {
this.setAttributeValue( DeclarationHVO.BILLTYPE,billtype);
 } 

/** 
* 获取单据版本pk
*
* @return 单据版本pk
*/
public String getBillversionpk () {
return (String) this.getAttributeValue( DeclarationHVO.BILLVERSIONPK);
 } 

/** 
* 设置单据版本pk
*
* @param billversionpk 单据版本pk
*/
public void setBillversionpk ( String billversionpk) {
this.setAttributeValue( DeclarationHVO.BILLVERSIONPK,billversionpk);
 } 

/** 
* 获取业务类型
*
* @return 业务类型
*/
public String getBusitype () {
return (String) this.getAttributeValue( DeclarationHVO.BUSITYPE);
 } 

/** 
* 设置业务类型
*
* @param busitype 业务类型
*/
public void setBusitype ( String busitype) {
this.setAttributeValue( DeclarationHVO.BUSITYPE,busitype);
 } 

/** 
* 获取编码
*
* @return 编码
*/
public String getCode () {
return (String) this.getAttributeValue( DeclarationHVO.CODE);
 } 

/** 
* 设置编码
*
* @param code 编码
*/
public void setCode ( String code) {
this.setAttributeValue( DeclarationHVO.CODE,code);
 } 

/** 
* 获取创建时间
*
* @return 创建时间
*/
public UFDateTime getCreationtime () {
return (UFDateTime) this.getAttributeValue( DeclarationHVO.CREATIONTIME);
 } 

/** 
* 设置创建时间
*
* @param creationtime 创建时间
*/
public void setCreationtime ( UFDateTime creationtime) {
this.setAttributeValue( DeclarationHVO.CREATIONTIME,creationtime);
 } 

/** 
* 获取创建人
*
* @return 创建人
*/
public String getCreator () {
return (String) this.getAttributeValue( DeclarationHVO.CREATOR);
 } 

/** 
* 设置创建人
*
* @param creator 创建人
*/
public void setCreator ( String creator) {
this.setAttributeValue( DeclarationHVO.CREATOR,creator);
 } 

/** 
* 获取修订枚举
*
* @return 修订枚举
*/
public Integer getEmendenum () {
return (Integer) this.getAttributeValue( DeclarationHVO.EMENDENUM);
 } 

/** 
* 设置修订枚举
*
* @param emendenum 修订枚举
*/
public void setEmendenum ( Integer emendenum) {
this.setAttributeValue( DeclarationHVO.EMENDENUM,emendenum);
 } 

/** 
* 获取最后修改时间
*
* @return 最后修改时间
*/
public UFDateTime getLastmaketime () {
return (UFDateTime) this.getAttributeValue( DeclarationHVO.LASTMAKETIME);
 } 

/** 
* 设置最后修改时间
*
* @param lastmaketime 最后修改时间
*/
public void setLastmaketime ( UFDateTime lastmaketime) {
this.setAttributeValue( DeclarationHVO.LASTMAKETIME,lastmaketime);
 } 

/** 
* 获取制单时间
*
* @return 制单时间
*/
public UFDateTime getMaketime () {
return (UFDateTime) this.getAttributeValue( DeclarationHVO.MAKETIME);
 } 

/** 
* 设置制单时间
*
* @param maketime 制单时间
*/
public void setMaketime ( UFDateTime maketime) {
this.setAttributeValue( DeclarationHVO.MAKETIME,maketime);
 } 

/** 
* 获取修改时间
*
* @return 修改时间
*/
public UFDateTime getModifiedtime () {
return (UFDateTime) this.getAttributeValue( DeclarationHVO.MODIFIEDTIME);
 } 

/** 
* 设置修改时间
*
* @param modifiedtime 修改时间
*/
public void setModifiedtime ( UFDateTime modifiedtime) {
this.setAttributeValue( DeclarationHVO.MODIFIEDTIME,modifiedtime);
 } 

/** 
* 获取修改人
*
* @return 修改人
*/
public String getModifier () {
return (String) this.getAttributeValue( DeclarationHVO.MODIFIER);
 } 

/** 
* 设置修改人
*
* @param modifier 修改人
*/
public void setModifier ( String modifier) {
this.setAttributeValue( DeclarationHVO.MODIFIER,modifier);
 } 

/** 
* 获取名称
*
* @return 名称
*/
public String getName () {
return (String) this.getAttributeValue( DeclarationHVO.NAME);
 } 

/** 
* 设置名称
*
* @param name 名称
*/
public void setName ( String name) {
this.setAttributeValue( DeclarationHVO.NAME,name);
 } 

/** 
* 获取主表主键
*
* @return 主表主键
*/
public String getPk_declaration () {
return (String) this.getAttributeValue( DeclarationHVO.PK_DECLARATION);
 } 

/** 
* 设置主表主键
*
* @param pk_declaration 主表主键
*/
public void setPk_declaration ( String pk_declaration) {
this.setAttributeValue( DeclarationHVO.PK_DECLARATION,pk_declaration);
 } 

/** 
* 获取集团
*
* @return 集团
*/
public String getPk_group () {
return (String) this.getAttributeValue( DeclarationHVO.PK_GROUP);
 } 

/** 
* 设置集团
*
* @param pk_group 集团
*/
public void setPk_group ( String pk_group) {
this.setAttributeValue( DeclarationHVO.PK_GROUP,pk_group);
 } 

/** 
* 获取组织
*
* @return 组织
*/
public String getPk_org () {
return (String) this.getAttributeValue( DeclarationHVO.PK_ORG);
 } 

/** 
* 设置组织
*
* @param pk_org 组织
*/
public void setPk_org ( String pk_org) {
this.setAttributeValue( DeclarationHVO.PK_ORG,pk_org);
 } 

/** 
* 获取组织版本
*
* @return 组织版本
*/
public String getPk_org_v () {
return (String) this.getAttributeValue( DeclarationHVO.PK_ORG_V);
 } 

/** 
* 设置组织版本
*
* @param pk_org_v 组织版本
*/
public void setPk_org_v ( String pk_org_v) {
this.setAttributeValue( DeclarationHVO.PK_ORG_V,pk_org_v);
 } 

/** 
* 获取所属组织
*
* @return 所属组织
*/
public String getPkorg () {
return (String) this.getAttributeValue( DeclarationHVO.PKORG);
 } 

/** 
* 设置所属组织
*
* @param pkorg 所属组织
*/
public void setPkorg ( String pkorg) {
this.setAttributeValue( DeclarationHVO.PKORG,pkorg);
 } 

/** 
* 获取来源单据id
*
* @return 来源单据id
*/
public String getSrcbillid () {
return (String) this.getAttributeValue( DeclarationHVO.SRCBILLID);
 } 

/** 
* 设置来源单据id
*
* @param srcbillid 来源单据id
*/
public void setSrcbillid ( String srcbillid) {
this.setAttributeValue( DeclarationHVO.SRCBILLID,srcbillid);
 } 

/** 
* 获取来源单据类型
*
* @return 来源单据类型
*/
public String getSrcbilltype () {
return (String) this.getAttributeValue( DeclarationHVO.SRCBILLTYPE);
 } 

/** 
* 设置来源单据类型
*
* @param srcbilltype 来源单据类型
*/
public void setSrcbilltype ( String srcbilltype) {
this.setAttributeValue( DeclarationHVO.SRCBILLTYPE,srcbilltype);
 } 

/** 
* 获取交易类型
*
* @return 交易类型
*/
public String getTranstype () {
return (String) this.getAttributeValue( DeclarationHVO.TRANSTYPE);
 } 

/** 
* 设置交易类型
*
* @param transtype 交易类型
*/
public void setTranstype ( String transtype) {
this.setAttributeValue( DeclarationHVO.TRANSTYPE,transtype);
 } 

/** 
* 获取交易类型pk
*
* @return 交易类型pk
*/
public String getTranstypepk () {
return (String) this.getAttributeValue( DeclarationHVO.TRANSTYPEPK);
 } 

/** 
* 设置交易类型pk
*
* @param transtypepk 交易类型pk
*/
public void setTranstypepk ( String transtypepk) {
this.setAttributeValue( DeclarationHVO.TRANSTYPEPK,transtypepk);
 } 

/** 
* 获取时间戳
*
* @return 时间戳
*/
public UFDateTime getTs () {
return (UFDateTime) this.getAttributeValue( DeclarationHVO.TS);
 } 

/** 
* 设置时间戳
*
* @param ts 时间戳
*/
public void setTs ( UFDateTime ts) {
this.setAttributeValue( DeclarationHVO.TS,ts);
 } 

/** 
* 获取自定义项1
*
* @return 自定义项1
*/
public String getVdef1 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF1);
 } 

/** 
* 设置自定义项1
*
* @param vdef1 自定义项1
*/
public void setVdef1 ( String vdef1) {
this.setAttributeValue( DeclarationHVO.VDEF1,vdef1);
 } 

/** 
* 获取自定义项10
*
* @return 自定义项10
*/
public String getVdef10 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF10);
 } 

/** 
* 设置自定义项10
*
* @param vdef10 自定义项10
*/
public void setVdef10 ( String vdef10) {
this.setAttributeValue( DeclarationHVO.VDEF10,vdef10);
 } 

/** 
* 获取自定义项11
*
* @return 自定义项11
*/
public String getVdef11 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF11);
 } 

/** 
* 设置自定义项11
*
* @param vdef11 自定义项11
*/
public void setVdef11 ( String vdef11) {
this.setAttributeValue( DeclarationHVO.VDEF11,vdef11);
 } 

/** 
* 获取自定义项12
*
* @return 自定义项12
*/
public String getVdef12 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF12);
 } 

/** 
* 设置自定义项12
*
* @param vdef12 自定义项12
*/
public void setVdef12 ( String vdef12) {
this.setAttributeValue( DeclarationHVO.VDEF12,vdef12);
 } 

/** 
* 获取自定义项13
*
* @return 自定义项13
*/
public String getVdef13 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF13);
 } 

/** 
* 设置自定义项13
*
* @param vdef13 自定义项13
*/
public void setVdef13 ( String vdef13) {
this.setAttributeValue( DeclarationHVO.VDEF13,vdef13);
 } 

/** 
* 获取自定义项14
*
* @return 自定义项14
*/
public String getVdef14 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF14);
 } 

/** 
* 设置自定义项14
*
* @param vdef14 自定义项14
*/
public void setVdef14 ( String vdef14) {
this.setAttributeValue( DeclarationHVO.VDEF14,vdef14);
 } 

/** 
* 获取自定义项15
*
* @return 自定义项15
*/
public String getVdef15 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF15);
 } 

/** 
* 设置自定义项15
*
* @param vdef15 自定义项15
*/
public void setVdef15 ( String vdef15) {
this.setAttributeValue( DeclarationHVO.VDEF15,vdef15);
 } 

/** 
* 获取自定义项16
*
* @return 自定义项16
*/
public String getVdef16 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF16);
 } 

/** 
* 设置自定义项16
*
* @param vdef16 自定义项16
*/
public void setVdef16 ( String vdef16) {
this.setAttributeValue( DeclarationHVO.VDEF16,vdef16);
 } 

/** 
* 获取自定义项17
*
* @return 自定义项17
*/
public String getVdef17 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF17);
 } 

/** 
* 设置自定义项17
*
* @param vdef17 自定义项17
*/
public void setVdef17 ( String vdef17) {
this.setAttributeValue( DeclarationHVO.VDEF17,vdef17);
 } 

/** 
* 获取自定义项18
*
* @return 自定义项18
*/
public String getVdef18 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF18);
 } 

/** 
* 设置自定义项18
*
* @param vdef18 自定义项18
*/
public void setVdef18 ( String vdef18) {
this.setAttributeValue( DeclarationHVO.VDEF18,vdef18);
 } 

/** 
* 获取自定义项19
*
* @return 自定义项19
*/
public String getVdef19 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF19);
 } 

/** 
* 设置自定义项19
*
* @param vdef19 自定义项19
*/
public void setVdef19 ( String vdef19) {
this.setAttributeValue( DeclarationHVO.VDEF19,vdef19);
 } 

/** 
* 获取自定义项2
*
* @return 自定义项2
*/
public String getVdef2 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF2);
 } 

/** 
* 设置自定义项2
*
* @param vdef2 自定义项2
*/
public void setVdef2 ( String vdef2) {
this.setAttributeValue( DeclarationHVO.VDEF2,vdef2);
 } 

/** 
* 获取自定义项20
*
* @return 自定义项20
*/
public String getVdef20 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF20);
 } 

/** 
* 设置自定义项20
*
* @param vdef20 自定义项20
*/
public void setVdef20 ( String vdef20) {
this.setAttributeValue( DeclarationHVO.VDEF20,vdef20);
 } 

/** 
* 获取自定义项3
*
* @return 自定义项3
*/
public String getVdef3 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF3);
 } 

/** 
* 设置自定义项3
*
* @param vdef3 自定义项3
*/
public void setVdef3 ( String vdef3) {
this.setAttributeValue( DeclarationHVO.VDEF3,vdef3);
 } 

/** 
* 获取自定义项4
*
* @return 自定义项4
*/
public String getVdef4 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF4);
 } 

/** 
* 设置自定义项4
*
* @param vdef4 自定义项4
*/
public void setVdef4 ( String vdef4) {
this.setAttributeValue( DeclarationHVO.VDEF4,vdef4);
 } 

/** 
* 获取自定义项5
*
* @return 自定义项5
*/
public String getVdef5 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF5);
 } 

/** 
* 设置自定义项5
*
* @param vdef5 自定义项5
*/
public void setVdef5 ( String vdef5) {
this.setAttributeValue( DeclarationHVO.VDEF5,vdef5);
 } 

/** 
* 获取自定义项6
*
* @return 自定义项6
*/
public String getVdef6 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF6);
 } 

/** 
* 设置自定义项6
*
* @param vdef6 自定义项6
*/
public void setVdef6 ( String vdef6) {
this.setAttributeValue( DeclarationHVO.VDEF6,vdef6);
 } 

/** 
* 获取自定义项7
*
* @return 自定义项7
*/
public String getVdef7 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF7);
 } 

/** 
* 设置自定义项7
*
* @param vdef7 自定义项7
*/
public void setVdef7 ( String vdef7) {
this.setAttributeValue( DeclarationHVO.VDEF7,vdef7);
 } 

/** 
* 获取自定义项8
*
* @return 自定义项8
*/
public String getVdef8 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF8);
 } 

/** 
* 设置自定义项8
*
* @param vdef8 自定义项8
*/
public void setVdef8 ( String vdef8) {
this.setAttributeValue( DeclarationHVO.VDEF8,vdef8);
 } 

/** 
* 获取自定义项9
*
* @return 自定义项9
*/
public String getVdef9 () {
return (String) this.getAttributeValue( DeclarationHVO.VDEF9);
 } 

/** 
* 设置自定义项9
*
* @param vdef9 自定义项9
*/
public void setVdef9 ( String vdef9) {
this.setAttributeValue( DeclarationHVO.VDEF9,vdef9);
 } 


  @Override
  public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("twhr.DeclarationHVO");
  }
}