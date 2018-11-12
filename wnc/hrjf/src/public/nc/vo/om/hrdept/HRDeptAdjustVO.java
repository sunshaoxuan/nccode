package nc.vo.om.hrdept;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此处简要描述此类功能 </b>
 * <p>
 *   此处添加累的描述信息
 * </p>
 *  创建日期:2018-11-3
 * @author YONYOU NC
 * @version NCPrj ??
 */
 
public class HRDeptAdjustVO extends SuperVO {
	
/**
*部门调整申请主键
*/
public String pk_deptadj;
/**
*集团
*/
public String pk_group;
/**
*组织
*/
public String pk_org;
/**
*组织版本
*/
public String pk_org_v;
/**
*创建人
*/
public String creator;
/**
*创建时间
*/
public UFDateTime creationtime;
/**
*修改人
*/
public String modifier;
/**
*修改时间
*/
public UFDateTime modifiedtime;
/**
*单据编码
*/
public String adj_code;
/**
*调整部门版本
*/
public String pk_dept_v;
/**
*生效日期
*/
public UFLiteralDate effectivedate;
/**
*是否已执行
*/
public UFBoolean iseffective;
/**
*部门主键
*/
public String pk_dept;
/**
*部门编码
*/
public String code;
/**
*部门名称
*/
public String name;
/**
*内部编码
*/
public String innercode;
/**
*上级部门
*/
public String pk_fatherorg;
/**
*部门类型
*/
public Integer depttype;
/**
*部门级别
*/
public String deptlevel;
/**
*部门职责
*/
public String deptduty;
/**
*部门成立时间
*/
public UFLiteralDate createdate;
/**
*简称
*/
public String shortname;
/**
*助记码
*/
public String mnecode;
/**
*HR撤销标志
*/
public UFBoolean hrcanceled;
/**
*部门撤销日期
*/
public UFLiteralDate deptcanceldate;
/**
*启用状态
*/
public Integer enablestate;
/**
*显示顺序
*/
public Integer displayorder;
/**
*分布式
*/
public Integer dataoriginflag;
/**
*报表
*/
public UFBoolean orgtype13;
/**
*预算
*/
public UFBoolean orgtype17;
/**
*负责人
*/
public String principal;
/**
*电话
*/
public String tel;
/**
*地址
*/
public String address;
/**
*备注
*/
public String memo;
/**
*是否最近版本
*/
public UFBoolean islastversion;
/**
*自定义项1
*/
public String def1;
/**
*自定义项2
*/
public String def2;
/**
*自定义项3
*/
public String def3;
/**
*自定义项4
*/
public String def4;
/**
*自定义项5
*/
public String def5;
/**
*自定义项6
*/
public String def6;
/**
*自定义项7
*/
public String def7;
/**
*自定义项8
*/
public String def8;
/**
*自定义项9
*/
public String def9;
/**
*自定义项10
*/
public String def10;
/**
*自定义项11
*/
public String def11;
/**
*自定义项12
*/
public String def12;
/**
*自定义项13
*/
public String def13;
/**
*自定义项14
*/
public String def14;
/**
*自定义项15
*/
public String def15;
/**
*自定义项16
*/
public String def16;
/**
*自定义项17
*/
public String def17;
/**
*自定义项18
*/
public String def18;
/**
*自定义项19
*/
public String def19;
/**
*自定义项20
*/
public String def20;
/**
*制单日期
*/
public UFDate billdate;
/**
*时间戳
*/
public UFDateTime ts;

/**
 * dr
 */
public Integer dr;
    
/**
 * @return dr
 */
public Integer getDr() {
	return dr;
}

/**
 * @param dr 要设置的 dr
 */
public void setDr(Integer dr) {
	this.dr = dr;
}

/**
* 属性 pk_deptadj的Getter方法.属性名：部门调整申请主键
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getPk_deptadj() {
return this.pk_deptadj;
} 

/**
* 属性pk_deptadj的Setter方法.属性名：部门调整申请主键
* 创建日期:2018-11-3
* @param newPk_deptadj java.lang.String
*/
public void setPk_deptadj ( String pk_deptadj) {
this.pk_deptadj=pk_deptadj;
} 
 
/**
* 属性 pk_group的Getter方法.属性名：集团
*  创建日期:2018-11-3
* @return nc.vo.org.GroupVO
*/
public String getPk_group() {
return this.pk_group;
} 

/**
* 属性pk_group的Setter方法.属性名：集团
* 创建日期:2018-11-3
* @param newPk_group nc.vo.org.GroupVO
*/
public void setPk_group ( String pk_group) {
this.pk_group=pk_group;
} 
 
/**
* 属性 pk_org的Getter方法.属性名：组织
*  创建日期:2018-11-3
* @return nc.vo.org.OrgVO
*/
public String getPk_org() {
return this.pk_org;
} 

/**
* 属性pk_org的Setter方法.属性名：组织
* 创建日期:2018-11-3
* @param newPk_org nc.vo.org.OrgVO
*/
public void setPk_org ( String pk_org) {
this.pk_org=pk_org;
} 
 
/**
* 属性 pk_org_v的Getter方法.属性名：组织版本
*  创建日期:2018-11-3
* @return nc.vo.vorg.OrgVersionVO
*/
public String getPk_org_v() {
return this.pk_org_v;
} 

/**
* 属性pk_org_v的Setter方法.属性名：组织版本
* 创建日期:2018-11-3
* @param newPk_org_v nc.vo.vorg.OrgVersionVO
*/
public void setPk_org_v ( String pk_org_v) {
this.pk_org_v=pk_org_v;
} 
 
/**
* 属性 creator的Getter方法.属性名：创建人
*  创建日期:2018-11-3
* @return nc.vo.sm.UserVO
*/
public String getCreator() {
return this.creator;
} 

/**
* 属性creator的Setter方法.属性名：创建人
* 创建日期:2018-11-3
* @param newCreator nc.vo.sm.UserVO
*/
public void setCreator ( String creator) {
this.creator=creator;
} 
 
/**
* 属性 creationtime的Getter方法.属性名：创建时间
*  创建日期:2018-11-3
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getCreationtime() {
return this.creationtime;
} 

/**
* 属性creationtime的Setter方法.属性名：创建时间
* 创建日期:2018-11-3
* @param newCreationtime nc.vo.pub.lang.UFDateTime
*/
public void setCreationtime ( UFDateTime creationtime) {
this.creationtime=creationtime;
} 
 
/**
* 属性 modifier的Getter方法.属性名：修改人
*  创建日期:2018-11-3
* @return nc.vo.sm.UserVO
*/
public String getModifier() {
return this.modifier;
} 

/**
* 属性modifier的Setter方法.属性名：修改人
* 创建日期:2018-11-3
* @param newModifier nc.vo.sm.UserVO
*/
public void setModifier ( String modifier) {
this.modifier=modifier;
} 
 
/**
* 属性 modifiedtime的Getter方法.属性名：修改时间
*  创建日期:2018-11-3
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getModifiedtime() {
return this.modifiedtime;
} 

/**
* 属性modifiedtime的Setter方法.属性名：修改时间
* 创建日期:2018-11-3
* @param newModifiedtime nc.vo.pub.lang.UFDateTime
*/
public void setModifiedtime ( UFDateTime modifiedtime) {
this.modifiedtime=modifiedtime;
} 
 
/**
* 属性 adj_code的Getter方法.属性名：单据编码
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getAdj_code() {
return this.adj_code;
} 

/**
* 属性adj_code的Setter方法.属性名：单据编码
* 创建日期:2018-11-3
* @param newAdj_code java.lang.String
*/
public void setAdj_code ( String adj_code) {
this.adj_code=adj_code;
} 
 
/**
* 属性 pk_dept_v的Getter方法.属性名：调整部门版本
*  创建日期:2018-11-3
* @return nc.vo.om.hrdept.HRDeptVersionVO
*/
public String getPk_dept_v() {
return this.pk_dept_v;
} 

/**
* 属性pk_dept_v的Setter方法.属性名：调整部门版本
* 创建日期:2018-11-3
* @param newPk_dept_v nc.vo.om.hrdept.HRDeptVersionVO
*/
public void setPk_dept_v ( String pk_dept_v) {
this.pk_dept_v=pk_dept_v;
} 
 
/**
* 属性 effectivedate的Getter方法.属性名：生效日期
*  创建日期:2018-11-3
* @return nc.vo.pub.lang.UFLiteralDate
*/
public UFLiteralDate getEffectivedate() {
return this.effectivedate;
} 

/**
* 属性effectivedate的Setter方法.属性名：生效日期
* 创建日期:2018-11-3
* @param newEffectivedate nc.vo.pub.lang.UFLiteralDate
*/
public void setEffectivedate ( UFLiteralDate effectivedate) {
this.effectivedate=effectivedate;
} 
 
/**
* 属性 iseffective的Getter方法.属性名：是否已执行
*  创建日期:2018-11-3
* @return nc.vo.pub.lang.UFBoolean
*/
public UFBoolean getIseffective() {
return this.iseffective;
} 

/**
* 属性iseffective的Setter方法.属性名：是否已执行
* 创建日期:2018-11-3
* @param newIseffective nc.vo.pub.lang.UFBoolean
*/
public void setIseffective ( UFBoolean iseffective) {
this.iseffective=iseffective;
} 
 
/**
* 属性 pk_dept的Getter方法.属性名：部门主键
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getPk_dept() {
return this.pk_dept;
} 

/**
* 属性pk_dept的Setter方法.属性名：部门主键
* 创建日期:2018-11-3
* @param newPk_dept java.lang.String
*/
public void setPk_dept ( String pk_dept) {
this.pk_dept=pk_dept;
} 
 
/**
* 属性 code的Getter方法.属性名：部门编码
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getCode() {
return this.code;
} 

/**
* 属性code的Setter方法.属性名：部门编码
* 创建日期:2018-11-3
* @param newCode java.lang.String
*/
public void setCode ( String code) {
this.code=code;
} 
 
/**
* 属性 name的Getter方法.属性名：部门名称
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getName() {
return this.name;
} 

/**
* 属性name的Setter方法.属性名：部门名称
* 创建日期:2018-11-3
* @param newName java.lang.String
*/
public void setName ( String name) {
this.name=name;
} 
 
/**
* 属性 innercode的Getter方法.属性名：内部编码
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getInnercode() {
return this.innercode;
} 

/**
* 属性innercode的Setter方法.属性名：内部编码
* 创建日期:2018-11-3
* @param newInnercode java.lang.String
*/
public void setInnercode ( String innercode) {
this.innercode=innercode;
} 
 
/**
* 属性 pk_fatherorg的Getter方法.属性名：上级部门
*  创建日期:2018-11-3
* @return nc.vo.om.hrdept.HRDeptVO
*/
public String getPk_fatherorg() {
return this.pk_fatherorg;
} 

/**
* 属性pk_fatherorg的Setter方法.属性名：上级部门
* 创建日期:2018-11-3
* @param newPk_fatherorg nc.vo.om.hrdept.HRDeptVO
*/
public void setPk_fatherorg ( String pk_fatherorg) {
this.pk_fatherorg=pk_fatherorg;
} 
 
/**
* 属性 depttype的Getter方法.属性名：部门类型
*  创建日期:2018-11-3
* @return nc.vo.uap.corp.depttype
*/
public Integer getDepttype() {
return this.depttype;
} 

/**
* 属性depttype的Setter方法.属性名：部门类型
* 创建日期:2018-11-3
* @param newDepttype nc.vo.uap.corp.depttype
*/
public void setDepttype ( Integer depttype) {
this.depttype=depttype;
} 
 
/**
* 属性 deptlevel的Getter方法.属性名：部门级别
*  创建日期:2018-11-3
* @return nc.vo.bd.defdoc.DefdocVO
*/
public String getDeptlevel() {
return this.deptlevel;
} 

/**
* 属性deptlevel的Setter方法.属性名：部门级别
* 创建日期:2018-11-3
* @param newDeptlevel nc.vo.bd.defdoc.DefdocVO
*/
public void setDeptlevel ( String deptlevel) {
this.deptlevel=deptlevel;
} 
 
/**
* 属性 deptduty的Getter方法.属性名：部门职责
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDeptduty() {
return this.deptduty;
} 

/**
* 属性deptduty的Setter方法.属性名：部门职责
* 创建日期:2018-11-3
* @param newDeptduty java.lang.String
*/
public void setDeptduty ( String deptduty) {
this.deptduty=deptduty;
} 
 
/**
* 属性 createdate的Getter方法.属性名：部门成立时间
*  创建日期:2018-11-3
* @return nc.vo.pub.lang.UFLiteralDate
*/
public UFLiteralDate getCreatedate() {
return this.createdate;
} 

/**
* 属性createdate的Setter方法.属性名：部门成立时间
* 创建日期:2018-11-3
* @param newCreatedate nc.vo.pub.lang.UFLiteralDate
*/
public void setCreatedate ( UFLiteralDate createdate) {
this.createdate=createdate;
} 
 
/**
* 属性 shortname的Getter方法.属性名：简称
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getShortname() {
return this.shortname;
} 

/**
* 属性shortname的Setter方法.属性名：简称
* 创建日期:2018-11-3
* @param newShortname java.lang.String
*/
public void setShortname ( String shortname) {
this.shortname=shortname;
} 
 
/**
* 属性 mnecode的Getter方法.属性名：助记码
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getMnecode() {
return this.mnecode;
} 

/**
* 属性mnecode的Setter方法.属性名：助记码
* 创建日期:2018-11-3
* @param newMnecode java.lang.String
*/
public void setMnecode ( String mnecode) {
this.mnecode=mnecode;
} 
 
/**
* 属性 hrcanceled的Getter方法.属性名：HR撤销标志
*  创建日期:2018-11-3
* @return nc.vo.pub.lang.UFBoolean
*/
public UFBoolean getHrcanceled() {
return this.hrcanceled;
} 

/**
* 属性hrcanceled的Setter方法.属性名：HR撤销标志
* 创建日期:2018-11-3
* @param newHrcanceled nc.vo.pub.lang.UFBoolean
*/
public void setHrcanceled ( UFBoolean hrcanceled) {
this.hrcanceled=hrcanceled;
} 
 
/**
* 属性 deptcanceldate的Getter方法.属性名：部门撤销日期
*  创建日期:2018-11-3
* @return nc.vo.pub.lang.UFLiteralDate
*/
public UFLiteralDate getDeptcanceldate() {
return this.deptcanceldate;
} 

/**
* 属性deptcanceldate的Setter方法.属性名：部门撤销日期
* 创建日期:2018-11-3
* @param newDeptcanceldate nc.vo.pub.lang.UFLiteralDate
*/
public void setDeptcanceldate ( UFLiteralDate deptcanceldate) {
this.deptcanceldate=deptcanceldate;
} 
 
/**
* 属性 enablestate的Getter方法.属性名：启用状态
*  创建日期:2018-11-3
* @return nc.vo.bd.pub.EnableStateEnum
*/
public Integer getEnablestate() {
return this.enablestate;
} 

/**
* 属性enablestate的Setter方法.属性名：启用状态
* 创建日期:2018-11-3
* @param newEnablestate nc.vo.bd.pub.EnableStateEnum
*/
public void setEnablestate ( Integer enablestate) {
this.enablestate=enablestate;
} 
 
/**
* 属性 displayorder的Getter方法.属性名：显示顺序
*  创建日期:2018-11-3
* @return java.lang.Integer
*/
public Integer getDisplayorder() {
return this.displayorder;
} 

/**
* 属性displayorder的Setter方法.属性名：显示顺序
* 创建日期:2018-11-3
* @param newDisplayorder java.lang.Integer
*/
public void setDisplayorder ( Integer displayorder) {
this.displayorder=displayorder;
} 
 
/**
* 属性 dataoriginflag的Getter方法.属性名：分布式
*  创建日期:2018-11-3
* @return nc.vo.wa.item.FromEnumVO
*/
public Integer getDataoriginflag() {
return this.dataoriginflag;
} 

/**
* 属性dataoriginflag的Setter方法.属性名：分布式
* 创建日期:2018-11-3
* @param newDataoriginflag nc.vo.wa.item.FromEnumVO
*/
public void setDataoriginflag ( Integer dataoriginflag) {
this.dataoriginflag=dataoriginflag;
} 
 
/**
* 属性 orgtype13的Getter方法.属性名：报表
*  创建日期:2018-11-3
* @return nc.vo.pub.lang.UFBoolean
*/
public UFBoolean getOrgtype13() {
return this.orgtype13;
} 

/**
* 属性orgtype13的Setter方法.属性名：报表
* 创建日期:2018-11-3
* @param newOrgtype13 nc.vo.pub.lang.UFBoolean
*/
public void setOrgtype13 ( UFBoolean orgtype13) {
this.orgtype13=orgtype13;
} 
 
/**
* 属性 orgtype17的Getter方法.属性名：预算
*  创建日期:2018-11-3
* @return nc.vo.pub.lang.UFBoolean
*/
public UFBoolean getOrgtype17() {
return this.orgtype17;
} 

/**
* 属性orgtype17的Setter方法.属性名：预算
* 创建日期:2018-11-3
* @param newOrgtype17 nc.vo.pub.lang.UFBoolean
*/
public void setOrgtype17 ( UFBoolean orgtype17) {
this.orgtype17=orgtype17;
} 
 
/**
* 属性 principal的Getter方法.属性名：负责人
*  创建日期:2018-11-3
* @return nc.vo.hi.psndoc.PsndocVO
*/
public String getPrincipal() {
return this.principal;
} 

/**
* 属性principal的Setter方法.属性名：负责人
* 创建日期:2018-11-3
* @param newPrincipal nc.vo.hi.psndoc.PsndocVO
*/
public void setPrincipal ( String principal) {
this.principal=principal;
} 
 
/**
* 属性 tel的Getter方法.属性名：电话
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getTel() {
return this.tel;
} 

/**
* 属性tel的Setter方法.属性名：电话
* 创建日期:2018-11-3
* @param newTel java.lang.String
*/
public void setTel ( String tel) {
this.tel=tel;
} 
 
/**
* 属性 address的Getter方法.属性名：地址
*  创建日期:2018-11-3
* @return nc.vo.bd.address.AddressVO
*/
public String getAddress() {
return this.address;
} 

/**
* 属性address的Setter方法.属性名：地址
* 创建日期:2018-11-3
* @param newAddress nc.vo.bd.address.AddressVO
*/
public void setAddress ( String address) {
this.address=address;
} 
 
/**
* 属性 memo的Getter方法.属性名：备注
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getMemo() {
return this.memo;
} 

/**
* 属性memo的Setter方法.属性名：备注
* 创建日期:2018-11-3
* @param newMemo java.lang.String
*/
public void setMemo ( String memo) {
this.memo=memo;
} 
 
/**
* 属性 islastversion的Getter方法.属性名：是否最近版本
*  创建日期:2018-11-3
* @return nc.vo.pub.lang.UFBoolean
*/
public UFBoolean getIslastversion() {
return this.islastversion;
} 

/**
* 属性islastversion的Setter方法.属性名：是否最近版本
* 创建日期:2018-11-3
* @param newIslastversion nc.vo.pub.lang.UFBoolean
*/
public void setIslastversion ( UFBoolean islastversion) {
this.islastversion=islastversion;
} 
 
/**
* 属性 def1的Getter方法.属性名：自定义项1
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef1() {
return this.def1;
} 

/**
* 属性def1的Setter方法.属性名：自定义项1
* 创建日期:2018-11-3
* @param newDef1 java.lang.String
*/
public void setDef1 ( String def1) {
this.def1=def1;
} 
 
/**
* 属性 def2的Getter方法.属性名：自定义项2
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef2() {
return this.def2;
} 

/**
* 属性def2的Setter方法.属性名：自定义项2
* 创建日期:2018-11-3
* @param newDef2 java.lang.String
*/
public void setDef2 ( String def2) {
this.def2=def2;
} 
 
/**
* 属性 def3的Getter方法.属性名：自定义项3
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef3() {
return this.def3;
} 

/**
* 属性def3的Setter方法.属性名：自定义项3
* 创建日期:2018-11-3
* @param newDef3 java.lang.String
*/
public void setDef3 ( String def3) {
this.def3=def3;
} 
 
/**
* 属性 def4的Getter方法.属性名：自定义项4
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef4() {
return this.def4;
} 

/**
* 属性def4的Setter方法.属性名：自定义项4
* 创建日期:2018-11-3
* @param newDef4 java.lang.String
*/
public void setDef4 ( String def4) {
this.def4=def4;
} 
 
/**
* 属性 def5的Getter方法.属性名：自定义项5
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef5() {
return this.def5;
} 

/**
* 属性def5的Setter方法.属性名：自定义项5
* 创建日期:2018-11-3
* @param newDef5 java.lang.String
*/
public void setDef5 ( String def5) {
this.def5=def5;
} 
 
/**
* 属性 def6的Getter方法.属性名：自定义项6
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef6() {
return this.def6;
} 

/**
* 属性def6的Setter方法.属性名：自定义项6
* 创建日期:2018-11-3
* @param newDef6 java.lang.String
*/
public void setDef6 ( String def6) {
this.def6=def6;
} 
 
/**
* 属性 def7的Getter方法.属性名：自定义项7
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef7() {
return this.def7;
} 

/**
* 属性def7的Setter方法.属性名：自定义项7
* 创建日期:2018-11-3
* @param newDef7 java.lang.String
*/
public void setDef7 ( String def7) {
this.def7=def7;
} 
 
/**
* 属性 def8的Getter方法.属性名：自定义项8
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef8() {
return this.def8;
} 

/**
* 属性def8的Setter方法.属性名：自定义项8
* 创建日期:2018-11-3
* @param newDef8 java.lang.String
*/
public void setDef8 ( String def8) {
this.def8=def8;
} 
 
/**
* 属性 def9的Getter方法.属性名：自定义项9
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef9() {
return this.def9;
} 

/**
* 属性def9的Setter方法.属性名：自定义项9
* 创建日期:2018-11-3
* @param newDef9 java.lang.String
*/
public void setDef9 ( String def9) {
this.def9=def9;
} 
 
/**
* 属性 def10的Getter方法.属性名：自定义项10
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef10() {
return this.def10;
} 

/**
* 属性def10的Setter方法.属性名：自定义项10
* 创建日期:2018-11-3
* @param newDef10 java.lang.String
*/
public void setDef10 ( String def10) {
this.def10=def10;
} 
 
/**
* 属性 def11的Getter方法.属性名：自定义项11
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef11() {
return this.def11;
} 

/**
* 属性def11的Setter方法.属性名：自定义项11
* 创建日期:2018-11-3
* @param newDef11 java.lang.String
*/
public void setDef11 ( String def11) {
this.def11=def11;
} 
 
/**
* 属性 def12的Getter方法.属性名：自定义项12
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef12() {
return this.def12;
} 

/**
* 属性def12的Setter方法.属性名：自定义项12
* 创建日期:2018-11-3
* @param newDef12 java.lang.String
*/
public void setDef12 ( String def12) {
this.def12=def12;
} 
 
/**
* 属性 def13的Getter方法.属性名：自定义项13
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef13() {
return this.def13;
} 

/**
* 属性def13的Setter方法.属性名：自定义项13
* 创建日期:2018-11-3
* @param newDef13 java.lang.String
*/
public void setDef13 ( String def13) {
this.def13=def13;
} 
 
/**
* 属性 def14的Getter方法.属性名：自定义项14
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef14() {
return this.def14;
} 

/**
* 属性def14的Setter方法.属性名：自定义项14
* 创建日期:2018-11-3
* @param newDef14 java.lang.String
*/
public void setDef14 ( String def14) {
this.def14=def14;
} 
 
/**
* 属性 def15的Getter方法.属性名：自定义项15
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef15() {
return this.def15;
} 

/**
* 属性def15的Setter方法.属性名：自定义项15
* 创建日期:2018-11-3
* @param newDef15 java.lang.String
*/
public void setDef15 ( String def15) {
this.def15=def15;
} 
 
/**
* 属性 def16的Getter方法.属性名：自定义项16
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef16() {
return this.def16;
} 

/**
* 属性def16的Setter方法.属性名：自定义项16
* 创建日期:2018-11-3
* @param newDef16 java.lang.String
*/
public void setDef16 ( String def16) {
this.def16=def16;
} 
 
/**
* 属性 def17的Getter方法.属性名：自定义项17
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef17() {
return this.def17;
} 

/**
* 属性def17的Setter方法.属性名：自定义项17
* 创建日期:2018-11-3
* @param newDef17 java.lang.String
*/
public void setDef17 ( String def17) {
this.def17=def17;
} 
 
/**
* 属性 def18的Getter方法.属性名：自定义项18
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef18() {
return this.def18;
} 

/**
* 属性def18的Setter方法.属性名：自定义项18
* 创建日期:2018-11-3
* @param newDef18 java.lang.String
*/
public void setDef18 ( String def18) {
this.def18=def18;
} 
 
/**
* 属性 def19的Getter方法.属性名：自定义项19
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef19() {
return this.def19;
} 

/**
* 属性def19的Setter方法.属性名：自定义项19
* 创建日期:2018-11-3
* @param newDef19 java.lang.String
*/
public void setDef19 ( String def19) {
this.def19=def19;
} 
 
/**
* 属性 def20的Getter方法.属性名：自定义项20
*  创建日期:2018-11-3
* @return java.lang.String
*/
public String getDef20() {
return this.def20;
} 

/**
* 属性def20的Setter方法.属性名：自定义项20
* 创建日期:2018-11-3
* @param newDef20 java.lang.String
*/
public void setDef20 ( String def20) {
this.def20=def20;
} 
 
/**
* 属性 billdate的Getter方法.属性名：制单日期
*  创建日期:2018-11-3
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getBilldate() {
return this.billdate;
} 

/**
* 属性billdate的Setter方法.属性名：制单日期
* 创建日期:2018-11-3
* @param newBilldate nc.vo.pub.lang.UFDate
*/
public void setBilldate ( UFDate billdate) {
this.billdate=billdate;
} 
 
/**
* 属性 生成时间戳的Getter方法.属性名：时间戳
*  创建日期:2018-11-3
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getTs() {
return this.ts;
}
/**
* 属性生成时间戳的Setter方法.属性名：时间戳
* 创建日期:2018-11-3
* @param newts nc.vo.pub.lang.UFDateTime
*/
public void setTs(UFDateTime ts){
this.ts=ts;
} 
     
    @Override
    public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("hrjf.deptadj");
    }
   }
    