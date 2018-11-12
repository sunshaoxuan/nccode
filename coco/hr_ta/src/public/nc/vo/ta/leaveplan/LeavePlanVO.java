package nc.vo.ta.leaveplan;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> �˴���Ҫ�������๦�� </b>
 * <p>
 *   �˴������۵�������Ϣ
 * </p>
 *  ��������:2018-9-21
 * @author 
 * @version NCPrj ??
 */
 
public class LeavePlanVO extends SuperVO {
	
/**
*����
*/
public String pk_leaveplan;
/**
*����
*/
public String pk_group;
/**
*��֯
*/
public String pk_org;
/**
*��֯�汾
*/
public String pk_org_v;
/**
*Ա��
*/
public String pk_psndoc;
/**
*����
*/
public String pk_dept;
/**
*���Ű汾
*/
public String pk_dept_v;
/**
*�ݼ����
*/
public String pk_leavetype;
/**
*���ݿ�ʼ����
*/
public UFLiteralDate begindate;
/**
*���ݽ�������
*/
public UFLiteralDate enddate;
/**
*��������
*/
public Integer enableddays;
/**
*��������
*/
public Integer useddays;
/**
*ʣ������
*/
public Integer remaineddays;
/**
*�Ƿ񸽼�
*/
public UFBoolean isattachment;
/**
*�Ƿ�������
*/
public UFBoolean iscontinuous;
/**
*����޸�ʱ��
*/
public UFDate lastmaketime;
/**
*�Ƶ���
*/
public String billmaker;
/**
*������֯
*/
public String pkorg;
/**
*������
*/
public String creator;
/**
*����ʱ��
*/
public UFDate creationtime;
/**
*�޸���
*/
public String modifier;
/**
*�޸�ʱ��
*/
public UFDateTime modifiedtime;
/**
*name
*/
public String name;
/**
*code
*/
public String code;
/**
*��������
*/
public UFDate dbilldate;
/**
*�Զ�����1
*/
public String def1;
/**
*�Զ�����2
*/
public String def2;
/**
*�Զ�����3
*/
public String def3;
/**
*�Զ�����4
*/
public String def4;
/**
*�Զ�����5
*/
public String def5;
/**
*�Զ�����6
*/
public String def6;
/**
*�Զ�����7
*/
public String def7;
/**
*�Զ�����8
*/
public String def8;
/**
*�Զ�����9
*/
public String def9;
/**
*�Զ�����10
*/
public String def10;
/**
*�Զ�����11
*/
public String def11;
/**
*�Զ�����12
*/
public String def12;
/**
*�Զ�����13
*/
public String def13;
/**
*�Զ�����14
*/
public String def14;
/**
*�Զ�����15
*/
public String def15;
/**
*�Զ�����16
*/
public String def16;
/**
*�Զ�����17
*/
public String def17;
/**
*�Զ�����18
*/
public String def18;
/**
*�Զ�����19
*/
public String def19;
/**
*�Զ�����20
*/
public String def20;
/**
*�Ƶ�ʱ��
*/
public UFDateTime maketime;
/**
*���ݱ���
*/
public String billcode;
/**
*ʱ���
*/
public UFDateTime ts;
    
    
/**
* ���� pk_leaveplan��Getter����.������������
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getPk_leaveplan() {
return this.pk_leaveplan;
} 

/**
* ����pk_leaveplan��Setter����.������������
* ��������:2018-9-21
* @param newPk_leaveplan java.lang.String
*/
public void setPk_leaveplan ( String pk_leaveplan) {
this.pk_leaveplan=pk_leaveplan;
} 
 
/**
* ���� pk_group��Getter����.������������
*  ��������:2018-9-21
* @return nc.vo.org.GroupVO
*/
public String getPk_group() {
return this.pk_group;
} 

/**
* ����pk_group��Setter����.������������
* ��������:2018-9-21
* @param newPk_group nc.vo.org.GroupVO
*/
public void setPk_group ( String pk_group) {
this.pk_group=pk_group;
} 
 
/**
* ���� pk_org��Getter����.����������֯
*  ��������:2018-9-21
* @return nc.vo.org.HROrgVO
*/
public String getPk_org() {
return this.pk_org;
} 

/**
* ����pk_org��Setter����.����������֯
* ��������:2018-9-21
* @param newPk_org nc.vo.org.HROrgVO
*/
public void setPk_org ( String pk_org) {
this.pk_org=pk_org;
} 
 
/**
* ���� pk_org_v��Getter����.����������֯�汾
*  ��������:2018-9-21
* @return nc.vo.vorg.AdminOrgVersionVO
*/
public String getPk_org_v() {
return this.pk_org_v;
} 

/**
* ����pk_org_v��Setter����.����������֯�汾
* ��������:2018-9-21
* @param newPk_org_v nc.vo.vorg.AdminOrgVersionVO
*/
public void setPk_org_v ( String pk_org_v) {
this.pk_org_v=pk_org_v;
} 
 
/**
* ���� pk_psndoc��Getter����.��������Ա��
*  ��������:2018-9-21
* @return nc.vo.hi.psndoc.PsndocVO
*/
public String getPk_psndoc() {
return this.pk_psndoc;
} 

/**
* ����pk_psndoc��Setter����.��������Ա��
* ��������:2018-9-21
* @param newPk_psndoc nc.vo.hi.psndoc.PsndocVO
*/
public void setPk_psndoc ( String pk_psndoc) {
this.pk_psndoc=pk_psndoc;
} 
 
/**
* ���� pk_dept��Getter����.������������
*  ��������:2018-9-21
* @return nc.vo.om.hrdept.HRDeptVO
*/
public String getPk_dept() {
return this.pk_dept;
} 

/**
* ����pk_dept��Setter����.������������
* ��������:2018-9-21
* @param newPk_dept nc.vo.om.hrdept.HRDeptVO
*/
public void setPk_dept ( String pk_dept) {
this.pk_dept=pk_dept;
} 
 
/**
* ���� pk_dept_v��Getter����.�����������Ű汾
*  ��������:2018-9-21
* @return nc.vo.vorg.DeptVersionVO
*/
public String getPk_dept_v() {
return this.pk_dept_v;
} 

/**
* ����pk_dept_v��Setter����.�����������Ű汾
* ��������:2018-9-21
* @param newPk_dept_v nc.vo.vorg.DeptVersionVO
*/
public void setPk_dept_v ( String pk_dept_v) {
this.pk_dept_v=pk_dept_v;
} 
 
/**
* ���� pk_leavetype��Getter����.���������ݼ����
*  ��������:2018-9-21
* @return nc.vo.ta.timeitem.LeaveTypeVO
*/
public String getPk_leavetype() {
return this.pk_leavetype;
} 

/**
* ����pk_leavetype��Setter����.���������ݼ����
* ��������:2018-9-21
* @param newPk_leavetype nc.vo.ta.timeitem.LeaveTypeVO
*/
public void setPk_leavetype ( String pk_leavetype) {
this.pk_leavetype=pk_leavetype;
} 
 
/**
* ���� begindate��Getter����.�����������ݿ�ʼ����
*  ��������:2018-9-21
* @return nc.vo.pub.lang.UFLiteralDate
*/
public UFLiteralDate getBegindate() {
return this.begindate;
} 

/**
* ����begindate��Setter����.�����������ݿ�ʼ����
* ��������:2018-9-21
* @param newBegindate nc.vo.pub.lang.UFLiteralDate
*/
public void setBegindate ( UFLiteralDate begindate) {
this.begindate=begindate;
} 
 
/**
* ���� enddate��Getter����.�����������ݽ�������
*  ��������:2018-9-21
* @return nc.vo.pub.lang.UFLiteralDate
*/
public UFLiteralDate getEnddate() {
return this.enddate;
} 

/**
* ����enddate��Setter����.�����������ݽ�������
* ��������:2018-9-21
* @param newEnddate nc.vo.pub.lang.UFLiteralDate
*/
public void setEnddate ( UFLiteralDate enddate) {
this.enddate=enddate;
} 
 
/**
* ���� enableddays��Getter����.����������������
*  ��������:2018-9-21
* @return java.lang.Integer
*/
public Integer getEnableddays() {
return this.enableddays;
} 

/**
* ����enableddays��Setter����.����������������
* ��������:2018-9-21
* @param newEnableddays java.lang.Integer
*/
public void setEnableddays ( Integer enableddays) {
this.enableddays=enableddays;
} 
 
/**
* ���� useddays��Getter����.����������������
*  ��������:2018-9-21
* @return java.lang.Integer
*/
public Integer getUseddays() {
return this.useddays;
} 

/**
* ����useddays��Setter����.����������������
* ��������:2018-9-21
* @param newUseddays java.lang.Integer
*/
public void setUseddays ( Integer useddays) {
this.useddays=useddays;
} 
 
/**
* ���� remaineddays��Getter����.��������ʣ������
*  ��������:2018-9-21
* @return java.lang.Integer
*/
public Integer getRemaineddays() {
return this.remaineddays;
} 

/**
* ����remaineddays��Setter����.��������ʣ������
* ��������:2018-9-21
* @param newRemaineddays java.lang.Integer
*/
public void setRemaineddays ( Integer remaineddays) {
this.remaineddays=remaineddays;
} 
 
/**
* ���� isattachment��Getter����.���������Ƿ񸽼�
*  ��������:2018-9-21
* @return nc.vo.pub.lang.UFBoolean
*/
public UFBoolean getIsattachment() {
return this.isattachment;
} 

/**
* ����isattachment��Setter����.���������Ƿ񸽼�
* ��������:2018-9-21
* @param newIsattachment nc.vo.pub.lang.UFBoolean
*/
public void setIsattachment ( UFBoolean isattachment) {
this.isattachment=isattachment;
} 
 
/**
* ���� iscontinuous��Getter����.���������Ƿ�������
*  ��������:2018-9-21
* @return nc.vo.pub.lang.UFBoolean
*/
public UFBoolean getIscontinuous() {
return this.iscontinuous;
} 

/**
* ����iscontinuous��Setter����.���������Ƿ�������
* ��������:2018-9-21
* @param newIscontinuous nc.vo.pub.lang.UFBoolean
*/
public void setIscontinuous ( UFBoolean iscontinuous) {
this.iscontinuous=iscontinuous;
} 
 
/**
* ���� lastmaketime��Getter����.������������޸�ʱ��
*  ��������:2018-9-21
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getLastmaketime() {
return this.lastmaketime;
} 

/**
* ����lastmaketime��Setter����.������������޸�ʱ��
* ��������:2018-9-21
* @param newLastmaketime nc.vo.pub.lang.UFDate
*/
public void setLastmaketime ( UFDate lastmaketime) {
this.lastmaketime=lastmaketime;
} 
 
/**
* ���� billmaker��Getter����.���������Ƶ���
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getBillmaker() {
return this.billmaker;
} 

/**
* ����billmaker��Setter����.���������Ƶ���
* ��������:2018-9-21
* @param newBillmaker java.lang.String
*/
public void setBillmaker ( String billmaker) {
this.billmaker=billmaker;
} 
 
/**
* ���� pkorg��Getter����.��������������֯
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getPkorg() {
return this.pkorg;
} 

/**
* ����pkorg��Setter����.��������������֯
* ��������:2018-9-21
* @param newPkorg java.lang.String
*/
public void setPkorg ( String pkorg) {
this.pkorg=pkorg;
} 
 
/**
* ���� creator��Getter����.��������������
*  ��������:2018-9-21
* @return nc.vo.sm.UserVO
*/
public String getCreator() {
return this.creator;
} 

/**
* ����creator��Setter����.��������������
* ��������:2018-9-21
* @param newCreator nc.vo.sm.UserVO
*/
public void setCreator ( String creator) {
this.creator=creator;
} 
 
/**
* ���� creationtime��Getter����.������������ʱ��
*  ��������:2018-9-21
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getCreationtime() {
return this.creationtime;
} 

/**
* ����creationtime��Setter����.������������ʱ��
* ��������:2018-9-21
* @param newCreationtime nc.vo.pub.lang.UFDate
*/
public void setCreationtime ( UFDate creationtime) {
this.creationtime=creationtime;
} 
 
/**
* ���� modifier��Getter����.���������޸���
*  ��������:2018-9-21
* @return nc.vo.sm.UserVO
*/
public String getModifier() {
return this.modifier;
} 

/**
* ����modifier��Setter����.���������޸���
* ��������:2018-9-21
* @param newModifier nc.vo.sm.UserVO
*/
public void setModifier ( String modifier) {
this.modifier=modifier;
} 
 
/**
* ���� modifiedtime��Getter����.���������޸�ʱ��
*  ��������:2018-9-21
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getModifiedtime() {
return this.modifiedtime;
} 

/**
* ����modifiedtime��Setter����.���������޸�ʱ��
* ��������:2018-9-21
* @param newModifiedtime nc.vo.pub.lang.UFDateTime
*/
public void setModifiedtime ( UFDateTime modifiedtime) {
this.modifiedtime=modifiedtime;
} 
 
/**
* ���� name��Getter����.��������name
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getName() {
return this.name;
} 

/**
* ����name��Setter����.��������name
* ��������:2018-9-21
* @param newName java.lang.String
*/
public void setName ( String name) {
this.name=name;
} 
 
/**
* ���� code��Getter����.��������code
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getCode() {
return this.code;
} 

/**
* ����code��Setter����.��������code
* ��������:2018-9-21
* @param newCode java.lang.String
*/
public void setCode ( String code) {
this.code=code;
} 
 
/**
* ���� dbilldate��Getter����.����������������
*  ��������:2018-9-21
* @return nc.vo.pub.lang.UFDate
*/
public UFDate getDbilldate() {
return this.dbilldate;
} 

/**
* ����dbilldate��Setter����.����������������
* ��������:2018-9-21
* @param newDbilldate nc.vo.pub.lang.UFDate
*/
public void setDbilldate ( UFDate dbilldate) {
this.dbilldate=dbilldate;
} 
 
/**
* ���� def1��Getter����.���������Զ�����1
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef1() {
return this.def1;
} 

/**
* ����def1��Setter����.���������Զ�����1
* ��������:2018-9-21
* @param newDef1 java.lang.String
*/
public void setDef1 ( String def1) {
this.def1=def1;
} 
 
/**
* ���� def2��Getter����.���������Զ�����2
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef2() {
return this.def2;
} 

/**
* ����def2��Setter����.���������Զ�����2
* ��������:2018-9-21
* @param newDef2 java.lang.String
*/
public void setDef2 ( String def2) {
this.def2=def2;
} 
 
/**
* ���� def3��Getter����.���������Զ�����3
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef3() {
return this.def3;
} 

/**
* ����def3��Setter����.���������Զ�����3
* ��������:2018-9-21
* @param newDef3 java.lang.String
*/
public void setDef3 ( String def3) {
this.def3=def3;
} 
 
/**
* ���� def4��Getter����.���������Զ�����4
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef4() {
return this.def4;
} 

/**
* ����def4��Setter����.���������Զ�����4
* ��������:2018-9-21
* @param newDef4 java.lang.String
*/
public void setDef4 ( String def4) {
this.def4=def4;
} 
 
/**
* ���� def5��Getter����.���������Զ�����5
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef5() {
return this.def5;
} 

/**
* ����def5��Setter����.���������Զ�����5
* ��������:2018-9-21
* @param newDef5 java.lang.String
*/
public void setDef5 ( String def5) {
this.def5=def5;
} 
 
/**
* ���� def6��Getter����.���������Զ�����6
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef6() {
return this.def6;
} 

/**
* ����def6��Setter����.���������Զ�����6
* ��������:2018-9-21
* @param newDef6 java.lang.String
*/
public void setDef6 ( String def6) {
this.def6=def6;
} 
 
/**
* ���� def7��Getter����.���������Զ�����7
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef7() {
return this.def7;
} 

/**
* ����def7��Setter����.���������Զ�����7
* ��������:2018-9-21
* @param newDef7 java.lang.String
*/
public void setDef7 ( String def7) {
this.def7=def7;
} 
 
/**
* ���� def8��Getter����.���������Զ�����8
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef8() {
return this.def8;
} 

/**
* ����def8��Setter����.���������Զ�����8
* ��������:2018-9-21
* @param newDef8 java.lang.String
*/
public void setDef8 ( String def8) {
this.def8=def8;
} 
 
/**
* ���� def9��Getter����.���������Զ�����9
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef9() {
return this.def9;
} 

/**
* ����def9��Setter����.���������Զ�����9
* ��������:2018-9-21
* @param newDef9 java.lang.String
*/
public void setDef9 ( String def9) {
this.def9=def9;
} 
 
/**
* ���� def10��Getter����.���������Զ�����10
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef10() {
return this.def10;
} 

/**
* ����def10��Setter����.���������Զ�����10
* ��������:2018-9-21
* @param newDef10 java.lang.String
*/
public void setDef10 ( String def10) {
this.def10=def10;
} 
 
/**
* ���� def11��Getter����.���������Զ�����11
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef11() {
return this.def11;
} 

/**
* ����def11��Setter����.���������Զ�����11
* ��������:2018-9-21
* @param newDef11 java.lang.String
*/
public void setDef11 ( String def11) {
this.def11=def11;
} 
 
/**
* ���� def12��Getter����.���������Զ�����12
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef12() {
return this.def12;
} 

/**
* ����def12��Setter����.���������Զ�����12
* ��������:2018-9-21
* @param newDef12 java.lang.String
*/
public void setDef12 ( String def12) {
this.def12=def12;
} 
 
/**
* ���� def13��Getter����.���������Զ�����13
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef13() {
return this.def13;
} 

/**
* ����def13��Setter����.���������Զ�����13
* ��������:2018-9-21
* @param newDef13 java.lang.String
*/
public void setDef13 ( String def13) {
this.def13=def13;
} 
 
/**
* ���� def14��Getter����.���������Զ�����14
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef14() {
return this.def14;
} 

/**
* ����def14��Setter����.���������Զ�����14
* ��������:2018-9-21
* @param newDef14 java.lang.String
*/
public void setDef14 ( String def14) {
this.def14=def14;
} 
 
/**
* ���� def15��Getter����.���������Զ�����15
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef15() {
return this.def15;
} 

/**
* ����def15��Setter����.���������Զ�����15
* ��������:2018-9-21
* @param newDef15 java.lang.String
*/
public void setDef15 ( String def15) {
this.def15=def15;
} 
 
/**
* ���� def16��Getter����.���������Զ�����16
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef16() {
return this.def16;
} 

/**
* ����def16��Setter����.���������Զ�����16
* ��������:2018-9-21
* @param newDef16 java.lang.String
*/
public void setDef16 ( String def16) {
this.def16=def16;
} 
 
/**
* ���� def17��Getter����.���������Զ�����17
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef17() {
return this.def17;
} 

/**
* ����def17��Setter����.���������Զ�����17
* ��������:2018-9-21
* @param newDef17 java.lang.String
*/
public void setDef17 ( String def17) {
this.def17=def17;
} 
 
/**
* ���� def18��Getter����.���������Զ�����18
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef18() {
return this.def18;
} 

/**
* ����def18��Setter����.���������Զ�����18
* ��������:2018-9-21
* @param newDef18 java.lang.String
*/
public void setDef18 ( String def18) {
this.def18=def18;
} 
 
/**
* ���� def19��Getter����.���������Զ�����19
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef19() {
return this.def19;
} 

/**
* ����def19��Setter����.���������Զ�����19
* ��������:2018-9-21
* @param newDef19 java.lang.String
*/
public void setDef19 ( String def19) {
this.def19=def19;
} 
 
/**
* ���� def20��Getter����.���������Զ�����20
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getDef20() {
return this.def20;
} 

/**
* ����def20��Setter����.���������Զ�����20
* ��������:2018-9-21
* @param newDef20 java.lang.String
*/
public void setDef20 ( String def20) {
this.def20=def20;
} 
 
/**
* ���� maketime��Getter����.���������Ƶ�ʱ��
*  ��������:2018-9-21
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getMaketime() {
return this.maketime;
} 

/**
* ����maketime��Setter����.���������Ƶ�ʱ��
* ��������:2018-9-21
* @param newMaketime nc.vo.pub.lang.UFDateTime
*/
public void setMaketime ( UFDateTime maketime) {
this.maketime=maketime;
} 
 
/**
* ���� billcode��Getter����.�����������ݱ���
*  ��������:2018-9-21
* @return java.lang.String
*/
public String getBillcode() {
return this.billcode;
} 

/**
* ����billcode��Setter����.�����������ݱ���
* ��������:2018-9-21
* @param newBillcode java.lang.String
*/
public void setBillcode ( String billcode) {
this.billcode=billcode;
} 
 
/**
* ���� ����ʱ�����Getter����.��������ʱ���
*  ��������:2018-9-21
* @return nc.vo.pub.lang.UFDateTime
*/
public UFDateTime getTs() {
return this.ts;
}
/**
* ��������ʱ�����Setter����.��������ʱ���
* ��������:2018-9-21
* @param newts nc.vo.pub.lang.UFDateTime
*/
public void setTs(UFDateTime ts){
this.ts=ts;
} 
     
    @Override
    public IVOMeta getMetaData() {
    return VOMetaFactory.getInstance().getVOMeta("hrta.leaveplan");
    }
   }
    