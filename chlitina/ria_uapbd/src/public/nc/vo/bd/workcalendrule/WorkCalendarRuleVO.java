/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.bd.workcalendrule;
	
import nc.vo.pub.*;

/**
 * <b> �ڴ˴���Ҫ��������Ĺ��� </b>
 * <p>
 *     �ڴ˴����Ӵ����������Ϣ
 * </p>
 * ��������:
 * @author 
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
public class WorkCalendarRuleVO extends SuperVO {
	private java.lang.String pk_workcalendrule;
	private java.lang.String pk_group;
	private java.lang.String pk_org;
	private java.lang.String code;
	private java.lang.String name;
	private java.lang.String name2;
	private java.lang.String name3;
	private java.lang.String name4;
	private java.lang.String name5;
	private java.lang.String name6;
	private java.lang.String ondutytime;
	private java.lang.String offdutytime;
	private java.lang.String memo;
	private nc.vo.pub.lang.UFBoolean sunday;
	private nc.vo.pub.lang.UFBoolean monday;
	private nc.vo.pub.lang.UFBoolean tuesday;
	private nc.vo.pub.lang.UFBoolean wednesday;
	private nc.vo.pub.lang.UFBoolean thursday;
	private nc.vo.pub.lang.UFBoolean friday;
	private nc.vo.pub.lang.UFBoolean saturday;
	private java.lang.String creator;
	private nc.vo.pub.lang.UFDateTime creationtime;
	private java.lang.String modifier;
	private nc.vo.pub.lang.UFDateTime modifiedtime;
	private java.lang.Integer dataoriginflag = 0;
	private nc.vo.pub.lang.UFBoolean sunday1;
	private nc.vo.pub.lang.UFBoolean monday1;
	private nc.vo.pub.lang.UFBoolean tuesday1;
	private nc.vo.pub.lang.UFBoolean wednesday1;
	private nc.vo.pub.lang.UFBoolean thursday1;
	private nc.vo.pub.lang.UFBoolean friday1;
	private nc.vo.pub.lang.UFBoolean saturday1;
	
	private nc.vo.pub.lang.UFBoolean sunday2;
	private nc.vo.pub.lang.UFBoolean monday2;
	private nc.vo.pub.lang.UFBoolean tuesday2;
	private nc.vo.pub.lang.UFBoolean wednesday2;
	private nc.vo.pub.lang.UFBoolean thursday2;
	private nc.vo.pub.lang.UFBoolean friday2;
	private nc.vo.pub.lang.UFBoolean saturday2;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;

	public static final String PK_WORKCALENDRULE = "pk_workcalendrule";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String CODE = "code";
	public static final String NAME = "name";
	public static final String NAME2 = "name2";
	public static final String NAME3 = "name3";
	public static final String NAME4 = "name4";
	public static final String NAME5 = "name5";
	public static final String NAME6 = "name6";
	public static final String ONDUTYTIME = "ondutytime";
	public static final String OFFDUTYTIME = "offdutytime";
	public static final String MEMO = "memo";
	public static final String SUNDAY = "sunday";
	public static final String MONDAY = "monday";
	public static final String TUESDAY = "tuesday";
	public static final String WEDNESDAY = "wednesday";
	public static final String THURSDAY = "thursday";
	public static final String FRIDAY = "friday";
	public static final String SATURDAY = "saturday";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String DATAORIGINFLAG = "dataoriginflag";
	public static final String SUNDAY1 = "sunday1";
	public static final String MONDAY1 = "monday1";
	public static final String TUESDAY1 = "tuesday1";
	public static final String WEDNESDAY1 = "wednesday1";
	public static final String THURSDAY1 = "thursday1";
	public static final String FRIDAY1 = "friday1";
	public static final String SATURDAY1 = "saturday1";
	
	public static final String SUNDAY2 = "sunday2";
	public static final String MONDAY2 = "monday2";
	public static final String TUESDAY2 = "tuesday2";
	public static final String WEDNESDAY2 = "wednesday2";
	public static final String THURSDAY2 = "thursday2";
	public static final String FRIDAY2 = "friday2";
	public static final String SATURDAY2= "saturday2";
			
	/**
	 * ����pk_workcalendrule��Getter����.������������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_workcalendrule () {
		return pk_workcalendrule;
	}   
	/**
	 * ����pk_workcalendrule��Setter����.������������
	 * ��������:
	 * @param newPk_workcalendrule java.lang.String
	 */
	public void setPk_workcalendrule (java.lang.String newPk_workcalendrule ) {
	 	this.pk_workcalendrule = newPk_workcalendrule;
	} 	  
	/**
	 * ����pk_group��Getter����.����������������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group () {
		return pk_group;
	}   
	/**3333
	 * ����pk_group��Setter����.����������������
	 * ��������:
	 * @param newPk_group java.lang.String
	 */
	public void setPk_group (java.lang.String newPk_group ) {
	 	this.pk_group = newPk_group;
	} 	  
	/**
	 * ����pk_org��Getter����.��������������֯
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org () {
		return pk_org;
	}   
	/**
	 * ����pk_org��Setter����.��������������֯
	 * ��������:
	 * @param newPk_org java.lang.String
	 */
	public void setPk_org (java.lang.String newPk_org ) {
	 	this.pk_org = newPk_org;
	} 	  
	/**
	 * ����code��Getter����.�����������������������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getCode () {
		return code;
	}   
	/**
	 * ����code��Setter����.�����������������������
	 * ��������:
	 * @param newCode java.lang.String
	 */
	public void setCode (java.lang.String newCode ) {
	 	this.code = newCode;
	} 	  
	/**
	 * ����name��Getter����.��������$map.displayName
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getName () {
		return name;
	}   
	/**
	 * ����name��Setter����.��������$map.displayName
	 * ��������:
	 * @param newName java.lang.String
	 */
	public void setName (java.lang.String newName ) {
	 	this.name = newName;
	} 	  
	/**
	 * ����name2��Getter����.��������$map.displayName
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getName2 () {
		return name2;
	}   
	/**
	 * ����name2��Setter����.��������$map.displayName
	 * ��������:
	 * @param newName2 java.lang.String
	 */
	public void setName2 (java.lang.String newName2 ) {
	 	this.name2 = newName2;
	} 	  
	/**
	 * ����name3��Getter����.��������$map.displayName
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getName3 () {
		return name3;
	}   
	/**
	 * ����name3��Setter����.��������$map.displayName
	 * ��������:
	 * @param newName3 java.lang.String
	 */
	public void setName3 (java.lang.String newName3 ) {
	 	this.name3 = newName3;
	} 	  
	/**
	 * ����name4��Getter����.��������$map.displayName
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getName4 () {
		return name4;
	}   
	/**
	 * ����name4��Setter����.��������$map.displayName
	 * ��������:
	 * @param newName4 java.lang.String
	 */
	public void setName4 (java.lang.String newName4 ) {
	 	this.name4 = newName4;
	} 	  
	/**
	 * ����name5��Getter����.��������$map.displayName
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getName5 () {
		return name5;
	}   
	/**
	 * ����name5��Setter����.��������$map.displayName
	 * ��������:
	 * @param newName5 java.lang.String
	 */
	public void setName5 (java.lang.String newName5 ) {
	 	this.name5 = newName5;
	} 	  
	/**
	 * ����name6��Getter����.��������$map.displayName
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getName6 () {
		return name6;
	}   
	/**
	 * ����name6��Setter����.��������$map.displayName
	 * ��������:
	 * @param newName6 java.lang.String
	 */
	public void setName6 (java.lang.String newName6 ) {
	 	this.name6 = newName6;
	} 	  
	/**
	 * ����ondutytime��Getter����.���������ϰ�ʱ��
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getOndutytime () {
		return ondutytime;
	}   
	/**
	 * ����ondutytime��Setter����.���������ϰ�ʱ��
	 * ��������:
	 * @param newOndutytime java.lang.String
	 */
	public void setOndutytime (java.lang.String newOndutytime ) {
	 	this.ondutytime = newOndutytime;
	} 	  
	/**
	 * ����offdutytime��Getter����.���������°�ʱ��
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getOffdutytime () {
		return offdutytime;
	}   
	/**
	 * ����offdutytime��Setter����.���������°�ʱ��
	 * ��������:
	 * @param newOffdutytime java.lang.String
	 */
	public void setOffdutytime (java.lang.String newOffdutytime ) {
	 	this.offdutytime = newOffdutytime;
	} 	  
	/**
	 * ����memo��Getter����.����������ע
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getMemo () {
		return memo;
	}   
	/**
	 * ����memo��Setter����.����������ע
	 * ��������:
	 * @param newMemo java.lang.String
	 */
	public void setMemo (java.lang.String newMemo ) {
	 	this.memo = newMemo;
	} 	  
	/**
	 * ����sunday��Getter����.������������
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getSunday () {
		return sunday;
	}   
	/**
	 * ����sunday��Setter����.������������
	 * ��������:
	 * @param newSunday nc.vo.pub.lang.UFBoolean
	 */
	public void setSunday (nc.vo.pub.lang.UFBoolean newSunday ) {
	 	this.sunday = newSunday;
	} 	  
	/**
	 * ����monday��Getter����.����������һ
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getMonday () {
		return monday;
	}   
	/**
	 * ����monday��Setter����.����������һ
	 * ��������:
	 * @param newMonday nc.vo.pub.lang.UFBoolean
	 */
	public void setMonday (nc.vo.pub.lang.UFBoolean newMonday ) {
	 	this.monday = newMonday;
	} 	  
	/**
	 * ����tuesday��Getter����.���������ܶ�
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getTuesday () {
		return tuesday;
	}   
	/**
	 * ����tuesday��Setter����.���������ܶ�
	 * ��������:
	 * @param newTuesday nc.vo.pub.lang.UFBoolean
	 */
	public void setTuesday (nc.vo.pub.lang.UFBoolean newTuesday ) {
	 	this.tuesday = newTuesday;
	} 	  
	/**
	 * ����wednesday��Getter����.������������
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getWednesday () {
		return wednesday;
	}   
	/**
	 * ����wednesday��Setter����.������������
	 * ��������:
	 * @param newWednesday nc.vo.pub.lang.UFBoolean
	 */
	public void setWednesday (nc.vo.pub.lang.UFBoolean newWednesday ) {
	 	this.wednesday = newWednesday;
	} 	  
	/**
	 * ����thursday��Getter����.������������
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getThursday () {
		return thursday;
	}   
	/**
	 * ����thursday��Setter����.������������
	 * ��������:
	 * @param newThursday nc.vo.pub.lang.UFBoolean
	 */
	public void setThursday (nc.vo.pub.lang.UFBoolean newThursday ) {
	 	this.thursday = newThursday;
	} 	  
	/**
	 * ����friday��Getter����.������������
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getFriday () {
		return friday;
	}   
	/**
	 * ����friday��Setter����.������������
	 * ��������:
	 * @param newFriday nc.vo.pub.lang.UFBoolean
	 */
	public void setFriday (nc.vo.pub.lang.UFBoolean newFriday ) {
	 	this.friday = newFriday;
	} 	  
	/**
	 * ����saturday��Getter����.������������
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getSaturday () {
		return saturday;
	}   
	/**
	 * ����saturday��Setter����.������������
	 * ��������:
	 * @param newSaturday nc.vo.pub.lang.UFBoolean
	 */
	public void setSaturday (nc.vo.pub.lang.UFBoolean newSaturday ) {
	 	this.saturday = newSaturday;
	} 	  
	/**
	 * ����creator��Getter����.��������������
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getCreator () {
		return creator;
	}   
	/**
	 * ����creator��Setter����.��������������
	 * ��������:
	 * @param newCreator java.lang.String
	 */
	public void setCreator (java.lang.String newCreator ) {
	 	this.creator = newCreator;
	} 	  
	/**
	 * ����creationtime��Getter����.������������ʱ��
	 * ��������:
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getCreationtime () {
		return creationtime;
	}   
	/**
	 * ����creationtime��Setter����.������������ʱ��
	 * ��������:
	 * @param newCreationtime nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime (nc.vo.pub.lang.UFDateTime newCreationtime ) {
	 	this.creationtime = newCreationtime;
	} 	  
	/**
	 * ����modifier��Getter����.������������޸���
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getModifier () {
		return modifier;
	}   
	/**
	 * ����modifier��Setter����.������������޸���
	 * ��������:
	 * @param newModifier java.lang.String
	 */
	public void setModifier (java.lang.String newModifier ) {
	 	this.modifier = newModifier;
	} 	  
	/**
	 * ����modifiedtime��Getter����.������������޸�ʱ��
	 * ��������:
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getModifiedtime () {
		return modifiedtime;
	}   
	/**
	 * ����modifiedtime��Setter����.������������޸�ʱ��
	 * ��������:
	 * @param newModifiedtime nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime (nc.vo.pub.lang.UFDateTime newModifiedtime ) {
	 	this.modifiedtime = newModifiedtime;
	} 	  
	/**
	 * ����dataoriginflag��Getter����.���������ֲ�ʽ
	 * ��������:
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDataoriginflag () {
		return dataoriginflag;
	}   
	/**
	 * ����dataoriginflag��Setter����.���������ֲ�ʽ
	 * ��������:
	 * @param newDataoriginflag java.lang.Integer
	 */
	public void setDataoriginflag (java.lang.Integer newDataoriginflag ) {
	 	this.dataoriginflag = newDataoriginflag;
	} 	  
	/**
	 * ����sunday1��Getter����.������������1
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getSunday1 () {
		return sunday1;
	}   
	/**
	 * ����sunday1��Setter����.������������1
	 * ��������:
	 * @param newSunday1 nc.vo.pub.lang.UFBoolean
	 */
	public void setSunday1 (nc.vo.pub.lang.UFBoolean newSunday1 ) {
	 	this.sunday1 = newSunday1;
	} 	  
	/**
	 * ����monday1��Getter����.����������һ1
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getMonday1 () {
		return monday1;
	}   
	/**
	 * ����monday1��Setter����.����������һ1
	 * ��������:
	 * @param newMonday1 nc.vo.pub.lang.UFBoolean
	 */
	public void setMonday1 (nc.vo.pub.lang.UFBoolean newMonday1 ) {
	 	this.monday1 = newMonday1;
	} 	  
	/**
	 * ����tuesday1��Getter����.���������ܶ�1
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getTuesday1 () {
		return tuesday1;
	}   
	/**
	 * ����tuesday1��Setter����.���������ܶ�1
	 * ��������:
	 * @param newTuesday1 nc.vo.pub.lang.UFBoolean
	 */
	public void setTuesday1 (nc.vo.pub.lang.UFBoolean newTuesday1 ) {
	 	this.tuesday1 = newTuesday1;
	} 	  
	/**
	 * ����wednesday1��Getter����.������������1
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getWednesday1 () {
		return wednesday1;
	}   
	/**
	 * ����wednesday1��Setter����.������������1
	 * ��������:
	 * @param newWednesday1 nc.vo.pub.lang.UFBoolean
	 */
	public void setWednesday1 (nc.vo.pub.lang.UFBoolean newWednesday1 ) {
	 	this.wednesday1 = newWednesday1;
	} 	  
	/**
	 * ����thursday1��Getter����.������������1
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getThursday1 () {
		return thursday1;
	}   
	/**
	 * ����thursday1��Setter����.������������1
	 * ��������:
	 * @param newThursday1 nc.vo.pub.lang.UFBoolean
	 */
	public void setThursday1 (nc.vo.pub.lang.UFBoolean newThursday1 ) {
	 	this.thursday1 = newThursday1;
	} 	  
	/**
	 * ����friday1��Getter����.������������1
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getFriday1 () {
		return friday1;
	}   
	/**
	 * ����friday1��Setter����.������������1
	 * ��������:
	 * @param newFriday1 nc.vo.pub.lang.UFBoolean
	 */
	public void setFriday1 (nc.vo.pub.lang.UFBoolean newFriday1 ) {
	 	this.friday1 = newFriday1;
	} 	  
	/**
	 * ����saturday1��Getter����.������������1
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getSaturday1 () {
		return saturday1;
	}   
	/**
	 * ����saturday1��Setter����.������������1
	 * ��������:
	 * @param newSaturday1 nc.vo.pub.lang.UFBoolean
	 */
	public void setSaturday1 (nc.vo.pub.lang.UFBoolean newSaturday1 ) {
	 	this.saturday1 = newSaturday1;
	} 	  
	
	/**
	 * ����sunday2��Getter����.������������2
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getSunday2 () {
		return sunday2;
	}   
	/**
	 * ����sunday2��Setter����.������������2
	 * ��������:
	 * @param newSunday2 nc.vo.pub.lang.UFBoolean
	 */
	public void setSunday2 (nc.vo.pub.lang.UFBoolean newSunday2 ) {
	 	this.sunday2 = newSunday2;
	} 	  
	/**
	 * ����monday2��Getter����.����������һ2
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getMonday2 () {
		return monday2;
	}   
	/**
	 * ����monday2��Setter����.����������һ2
	 * ��������:
	 * @param newMonday2 nc.vo.pub.lang.UFBoolean
	 */
	public void setMonday2 (nc.vo.pub.lang.UFBoolean newMonday2 ) {
	 	this.monday2 = newMonday2;
	} 	  
	/**
	 * ����tuesday2��Getter����.���������ܶ�2
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getTuesday2 () {
		return tuesday2;
	}   
	/**
	 * ����tuesday2��Setter����.���������ܶ�2
	 * ��������:
	 * @param newTuesday2 nc.vo.pub.lang.UFBoolean
	 */
	public void setTuesday2 (nc.vo.pub.lang.UFBoolean newTuesday2 ) {
	 	this.tuesday2 = newTuesday2;
	} 	  
	/**
	 * ����wednesday2��Getter����.������������2
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getWednesday2 () {
		return wednesday2;
	}   
	/**
	 * ����wednesday2��Setter����.������������2
	 * ��������:
	 * @param newWednesday2 nc.vo.pub.lang.UFBoolean
	 */
	public void setWednesday2 (nc.vo.pub.lang.UFBoolean newWednesday2 ) {
	 	this.wednesday2 = newWednesday2;
	} 	  
	/**
	 * ����thursday2��Getter����.������������2
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getThursday2 () {
		return thursday2;
	}   
	/**
	 * ����thursday2��Setter����.������������2
	 * ��������:
	 * @param newThursday2 nc.vo.pub.lang.UFBoolean
	 */
	public void setThursday2 (nc.vo.pub.lang.UFBoolean newThursday2 ) {
	 	this.thursday2 = newThursday2;
	} 	  
	/**
	 * ����friday2��Getter����.������������2
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getFriday2 () {
		return friday2;
	}   
	/**
	 * ����friday2��Setter����.������������2
	 * ��������:
	 * @param newFriday2 nc.vo.pub.lang.UFBoolean
	 */
	public void setFriday2 (nc.vo.pub.lang.UFBoolean newFriday2 ) {
	 	this.friday2 = newFriday2;
	} 	  
	/**
	 * ����saturday2��Getter����.������������2
	 * ��������:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getSaturday2 () {
		return saturday2;
	}   
	/**
	 * ����saturday2��Setter����.������������2
	 * ��������:
	 * @param newSaturday2 nc.vo.pub.lang.UFBoolean
	 */
	public void setSaturday2 (nc.vo.pub.lang.UFBoolean newSaturday2 ) {
	 	this.saturday2 = newSaturday2;
	} 	  
	
	
	/**
	 * ����dr��Getter����.��������dr
	 * ��������:
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr () {
		return dr;
	}   
	/**
	 * ����dr��Setter����.��������dr
	 * ��������:
	 * @param newDr java.lang.Integer
	 */
	public void setDr (java.lang.Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * ����ts��Getter����.��������ts
	 * ��������:
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs () {
		return ts;
	}   
	/**
	 * ����ts��Setter����.��������ts
	 * ��������:
	 * @param newTs nc.vo.pub.lang.UFDateTime
	 */
	public void setTs (nc.vo.pub.lang.UFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
 
	/**
	  * <p>ȡ�ø�VO�����ֶ�.
	  * <p>
	  * ��������:
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>ȡ�ñ�����.
	  * <p>
	  * ��������:
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
	  return "pk_workcalendrule";
	}
    
	/**
	 * <p>���ر�����.
	 * <p>
	 * ��������:
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "bd_workcalendrule";
	}    
	
	/**
	 * <p>���ر�����.
	 * <p>
	 * ��������:
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "bd_workcalendrule";
	}    
    
    /**
	  * ����Ĭ�Ϸ�ʽ����������.
	  *
	  * ��������:
	  */
     public WorkCalendarRuleVO() {
		super();	
	}    
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName =  "nc.vo.bd.workcalendrule.WorkCalendarRuleVO" )
	public IVOMeta getMetaData() {
   		return null;
  	}
} 

