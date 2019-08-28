/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.ta.psndoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nc.hr.utils.CommonUtils;
import nc.itf.ta.algorithm.IDateScope;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFLiteralDate;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 * 在此处添加此类的描述信息
 * </p>
 * 创建日期:2009-11-18 20:27:22
 * 
 * @author
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
public class TBMPsndocVO extends SuperVO implements IDateScope, Comparable<TBMPsndocVO> {

	public static final int TBM_PROP_NO = 0;
	public static final int TBM_PROP_MANUAL = 1;
	public static final int TBM_PROP_MACHINE = 2;

	private java.lang.String pk_tbm_psndoc;
	private java.lang.String pk_psndoc;
	private java.lang.String pk_psnjob;
	private java.lang.String pk_psnorg;// 组织关系
	private String pk_joborg;// 任职组织pk，冗余字段。因为工作日历等场合需要在代码中将vo按任职组织分组，因此需要记录
	private TimeZone timezone;// 任职组织的时区，冗余字段
	private String pk_dept;// 冗余字段。因为考勤日报等场合需要在代码中将vo按部门分组，因此需要记录pk_dept
	private java.lang.String pk_group;
	private java.lang.String pk_org;
	private java.lang.String pk_adminorg;
	private nc.vo.pub.lang.UFLiteralDate begindate;
	private nc.vo.pub.lang.UFLiteralDate enddate;
	private java.lang.Integer tbm_prop;
	private java.lang.String timecardid;
	private java.lang.String secondcardid;
	private java.lang.String pk_place;
	private String pk_team;// 所属班组主键
	private java.lang.String creator;
	private nc.vo.pub.lang.UFDateTime creationtime;
	private java.lang.String modifier;
	private nc.vo.pub.lang.UFDateTime modifiedtime;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;
	private String pk_region; // 考勤区域
	private java.lang.String pk_org_v;// 冗余字段 在日报，考勤数据生成 等时使用
	private java.lang.String pk_dept_v;// 冗余字段 在日报，考勤数据生成时等使用
	private nc.vo.pub.lang.UFBoolean islatest;
	private java.lang.String signtype;// 移动签到客户设置的常用签到方式（内勤--in,外勤--out)

	// MOD (台灣一例一休)
	// ssx added on 2018-06-09
	private java.lang.Integer weekform;

	public static final String TABLE_NAME = "tbm_psndoc";
	private String specialrest;// 特休结算
	private java.lang.Integer overtimecontrol;

	public static final String SPECIALREST = "specialrest";
	public static final String PK_TBM_PSNDOC = "pk_tbm_psndoc";
	public static final String PK_PSNDOC = "pk_psndoc";
	public static final String PK_PSNJOB = "pk_psnjob";
	public static final String PK_PSNORG = "pk_psnorg";
	public static final String PK_JOBORG = "pk_joborg";
	public static final String TIMEZONE = "timezone";
	public static final String PK_DEPT = "pk_dept";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String PK_ADMINORG = "pk_adminorg";
	public static final String BEGINDATE = "begindate";
	public static final String ENDDATE = "enddate";
	public static final String TBM_PROP = "tbm_prop";
	public static final String TIMECARDID = "timecardid";
	public static final String SECONDCARDID = "secondcardid";
	public static final String PK_PLACE = "pk_place";
	public static final String PK_TEAM = "pk_team";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String PK_ORG_V = "pk_org_v";
	public static final String PK_DEPT_V = "pk_dept_v";
	public static final String ISLATEST = "islatest";
	public static final String PK_REGION = "pk_region";
	public static final String SIGNTYPE = "signtype";
	public static final String WEEKFORM = "weekform";
	public static final String OVERTIMECONTROL = "overtimecontrol";

	/**
	 * 属性pk_tbm_psndoc的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_tbm_psndoc() {
		return pk_tbm_psndoc;
	}

	/**
	 * 属性pk_tbm_psndoc的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newPk_tbm_psndoc
	 *            java.lang.String
	 */
	public void setPk_tbm_psndoc(java.lang.String newPk_tbm_psndoc) {
		this.pk_tbm_psndoc = newPk_tbm_psndoc;
	}

	/**
	 * 属性pk_psndoc的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_psndoc() {
		return pk_psndoc;
	}

	/**
	 * 属性pk_psndoc的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newPk_psndoc
	 *            java.lang.String
	 */
	public void setPk_psndoc(java.lang.String newPk_psndoc) {
		this.pk_psndoc = newPk_psndoc;
	}

	/**
	 * 属性pk_psnjob的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_psnjob() {
		return pk_psnjob;
	}

	/**
	 * 属性pk_psnjob的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newPk_psnjob
	 *            java.lang.String
	 */
	public void setPk_psnjob(java.lang.String newPk_psnjob) {
		this.pk_psnjob = newPk_psnjob;
	}

	/**
	 * 属性pk_group的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group() {
		return pk_group;
	}

	/**
	 * 属性pk_group的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newPk_group
	 *            java.lang.String
	 */
	public void setPk_group(java.lang.String newPk_group) {
		this.pk_group = newPk_group;
	}

	/**
	 * 属性pk_org的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org() {
		return pk_org;
	}

	/**
	 * 属性pk_org的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newPk_org
	 *            java.lang.String
	 */
	public void setPk_org(java.lang.String newPk_org) {
		this.pk_org = newPk_org;
	}

	public java.lang.String getPk_adminorg() {
		return pk_adminorg;
	}

	public void setPk_adminorg(java.lang.String pk_adminorg) {
		this.pk_adminorg = pk_adminorg;
	}

	/**
	 * 属性begindate的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public nc.vo.pub.lang.UFLiteralDate getBegindate() {
		return begindate;
	}

	/**
	 * 属性begindate的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newBegindate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setBegindate(nc.vo.pub.lang.UFLiteralDate newBegindate) {
		this.begindate = newBegindate;
	}

	/**
	 * 属性enddate的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public nc.vo.pub.lang.UFLiteralDate getEnddate() {
		return enddate;
	}

	/**
	 * 属性enddate的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newEnddate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setEnddate(nc.vo.pub.lang.UFLiteralDate newEnddate) {
		this.enddate = newEnddate;
	}

	/**
	 * 属性tbm_prop的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getTbm_prop() {
		return tbm_prop;
	}

	/**
	 * 属性tbm_prop的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newTbm_prop
	 *            java.lang.Integer
	 */
	public void setTbm_prop(java.lang.Integer newTbm_prop) {
		this.tbm_prop = newTbm_prop;
	}

	public java.lang.Integer getOvertimecontrol() {
		return overtimecontrol;
	}

	/**
	 * 属性timecardid的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 */
	public void setOvertimecontrol(java.lang.Integer newOvertimecontrol) {
		this.overtimecontrol = newOvertimecontrol;
	}

	/**
	 * @return java.lang.String
	 */
	public java.lang.String getTimecardid() {
		return timecardid;
	}

	/**
	 * 属性timecardid的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newTimecardid
	 *            java.lang.String
	 */
	public void setTimecardid(java.lang.String newTimecardid) {
		this.timecardid = newTimecardid;
	}

	/**
	 * 属性secondcardid的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getSecondcardid() {
		return secondcardid;
	}

	/**
	 * 属性secondcardid的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newSecondcardid
	 *            java.lang.String
	 */
	public void setSecondcardid(java.lang.String newSecondcardid) {
		this.secondcardid = newSecondcardid;
	}

	/**
	 * 属性pk_place的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getPk_place() {
		return pk_place;
	}

	/**
	 * 属性pk_place的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newPk_place
	 *            java.lang.String
	 */
	public void setPk_place(java.lang.String newPk_place) {
		this.pk_place = newPk_place;
	}

	public nc.vo.pub.lang.UFBoolean getIslatest() {
		return islatest;
	}

	public void setIslatest(nc.vo.pub.lang.UFBoolean islatest) {
		this.islatest = islatest;
	}

	/**
	 * 属性creator的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCreator() {
		return creator;
	}

	/**
	 * 属性creator的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newCreator
	 *            java.lang.String
	 */
	public void setCreator(java.lang.String newCreator) {
		this.creator = newCreator;
	}

	/**
	 * 属性creationtime的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getCreationtime() {
		return creationtime;
	}

	/**
	 * 属性creationtime的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(nc.vo.pub.lang.UFDateTime newCreationtime) {
		this.creationtime = newCreationtime;
	}

	/**
	 * 属性modifier的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getModifier() {
		return modifier;
	}

	/**
	 * 属性modifier的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newModifier
	 *            java.lang.String
	 */
	public void setModifier(java.lang.String newModifier) {
		this.modifier = newModifier;
	}

	/**
	 * 属性modifiedtime的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getModifiedtime() {
		return modifiedtime;
	}

	/**
	 * 属性modifiedtime的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(nc.vo.pub.lang.UFDateTime newModifiedtime) {
		this.modifiedtime = newModifiedtime;
	}

	/**
	 * 属性dr的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr() {
		return dr;
	}

	/**
	 * 属性dr的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newDr
	 *            java.lang.Integer
	 */
	public void setDr(java.lang.Integer newDr) {
		this.dr = newDr;
	}

	/**
	 * 属性ts的Getter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs() {
		return ts;
	}

	/**
	 * 属性ts的Setter方法. 创建日期:2009-11-18 20:27:22
	 * 
	 * @param newTs
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(nc.vo.pub.lang.UFDateTime newTs) {
		this.ts = newTs;
	}

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2009-11-18 20:27:22
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getParentPKFieldName() {
		return null;
	}

	/**
	 * <p>
	 * 取得表主键.
	 * <p>
	 * 创建日期:2009-11-18 20:27:22
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getPKFieldName() {
		return "pk_tbm_psndoc";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2009-11-18 20:27:22
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getTableName() {
		return "tbm_psndoc";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2009-11-18 20:27:22
	 * 
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "tbm_psndoc";
	}

	/**
	 * 按照默认方式创建构造子.
	 * 
	 * 创建日期:2009-11-18 20:27:22
	 */
	public TBMPsndocVO() {
		super();
	}

	/**
	 * 从一个考勤档案vo数组中，找出与date表示的日期有交集的
	 * 
	 * @param vos
	 * @param date
	 * @return
	 */
	public static TBMPsndocVO[] findIntersectionVOs(TBMPsndocVO[] vos, String date) {
		if (vos == null || vos.length == 0)
			return vos;
		List<TBMPsndocVO> retList = new ArrayList<TBMPsndocVO>();
		for (TBMPsndocVO vo : vos) {
			if (date.compareTo(vo.getBegindate().toString()) >= 0 && date.compareTo(vo.getEnddate().toString()) <= 0)
				retList.add(vo);
		}
		return retList.toArray(new TBMPsndocVO[0]);
	}

	/**
	 * 从一个考勤档案vo数组中，找出与date表示的日期有交集的第一个vo
	 * 
	 * @param vos
	 * @param date
	 * @return
	 */
	public static TBMPsndocVO findIntersectionVO(TBMPsndocVO[] vos, String date) {
		if (vos == null || vos.length == 0)
			return null;
		for (TBMPsndocVO vo : vos) {
			if (date.compareTo(vo.getBegindate().toString()) >= 0 && date.compareTo(vo.getEnddate().toString()) <= 0)
				return vo;
		}
		return null;
	}

	/**
	 * 从一个考勤档案vo数组中，找出与date表示的日期有交集的
	 * 
	 * @param vos
	 * @param date
	 * @return
	 */
	public static TBMPsndocVO[] findIntersectionVOs(List<TBMPsndocVO> voList, String date) {
		if (voList == null || voList.size() == 0)
			return null;
		return findIntersectionVOs(voList.toArray(new TBMPsndocVO[0]), date);
	}

	/**
	 * 从一个考勤档案vo数组中，找出与date表示的日期有交集的第一个vo
	 * 
	 * @param vos
	 * @param date
	 * @return
	 */
	public static TBMPsndocVO findIntersectionVO(List<TBMPsndocVO> voList, String date) {
		if (CollectionUtils.isEmpty(voList))
			return null;
		return findIntersectionVO(voList.toArray(new TBMPsndocVO[0]), date);
	}

	/**
	 * 检查日期和考勤档案数组是否有交集
	 * 
	 * @param vos
	 * @param date
	 * @return
	 */
	public static boolean isIntersect(TBMPsndocVO[] vos, String date) {
		TBMPsndocVO[] intersectVOs = findIntersectionVOs(vos, date);
		return intersectVOs != null && intersectVOs.length > 0;
	}

	/**
	 * 检查日期和考勤档案List是否有交集
	 * 
	 * @param vos
	 * @param date
	 * @return
	 */
	public static boolean isIntersect(List<TBMPsndocVO> voList, String date) {
		TBMPsndocVO[] intersectVOs = findIntersectionVOs(voList, date);
		return intersectVOs != null && intersectVOs.length > 0;
	}

	// 检查该日期所对应的班组是什么
	public static TBMPsndocVO getvoForTeam(List<TBMPsndocVO> voList, String date) {
		if (voList == null || voList.size() == 0)
			return null;
		TBMPsndocVO tempvo = null;
		for (TBMPsndocVO vo : voList) {
			if (date.compareTo(vo.getBegindate().toString()) >= 0 && date.compareTo(vo.getEnddate().toString()) <= 0) {
				tempvo = vo;
				break;
			}
		}
		return tempvo;
	}

	// 如果主键相同就是相同
	@Override
	public boolean equals(Object obj) {
		if (obj == null || (!(obj instanceof TBMPsndocVO)))
			return false;
		if (((TBMPsndocVO) obj).getPk_tbm_psndoc() != null && getPk_tbm_psndoc() != null
				&& ((TBMPsndocVO) obj).getPk_tbm_psndoc().equals(getPk_tbm_psndoc()))
			return true;
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public int compareTo(TBMPsndocVO o) {
		return getBegindate().compareTo(o.getBegindate());
	}

	public boolean isLatest() {
		return getIslatest() == null ? false : getIslatest().booleanValue();
	}

	public String getPk_dept() {
		return pk_dept;
	}

	public void setPk_dept(String pkDept) {
		pk_dept = pkDept;
	}

	public java.lang.String getPk_psnorg() {
		return pk_psnorg;
	}

	public void setPk_psnorg(java.lang.String pk_psnorg) {
		this.pk_psnorg = pk_psnorg;
	}

	public String getPk_team() {
		return pk_team;
	}

	public void setPk_team(String pkTeam) {
		pk_team = pkTeam;
	}

	public String getPk_joborg() {
		return pk_joborg;
	}

	public void setPk_joborg(String pkJoborg) {
		pk_joborg = pkJoborg;
	}

	/**
	 * 根据人员的考勤档案，构造一个map，key是人员主键，value的key是日期，value的value是此人此天的任职所属业务单元主键
	 * 
	 * @param tbmPsndocVOListMap
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public static Map<String, Map<UFLiteralDate, String>> createDateOrgMapByTbmPsndocVOMap(
			Map<String, List<TBMPsndocVO>> tbmPsndocVOListMap, UFLiteralDate beginDate, UFLiteralDate endDate) {
		if (MapUtils.isEmpty(tbmPsndocVOListMap))
			return null;
		Map<String, Map<UFLiteralDate, String>> retMap = new HashMap<String, Map<UFLiteralDate, String>>();
		UFLiteralDate[] allDates = CommonUtils.createDateArray(beginDate, endDate);
		for (String pk_psndoc : tbmPsndocVOListMap.keySet()) {
			List<TBMPsndocVO> psndocVOList = tbmPsndocVOListMap.get(pk_psndoc);
			Map<UFLiteralDate, String> dateOrgMap = new HashMap<UFLiteralDate, String>();
			retMap.put(pk_psndoc, dateOrgMap);
			for (UFLiteralDate date : allDates) {
				TBMPsndocVO vo = TBMPsndocVO.findIntersectionVO(psndocVOList, date.toString());
				if (vo == null)
					continue;
				dateOrgMap.put(date, vo.getPk_joborg());
			}
		}
		return retMap;
	}

	public TimeZone getTimezone() {
		return timezone;
	}

	public void setTimezone(TimeZone timeZone) {
		this.timezone = timeZone;
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

	public String getPk_region() {
		return pk_region;
	}

	public void setPk_region(String pk_region) {
		this.pk_region = pk_region;
	}

	public java.lang.String getSigntype() {
		return StringUtils.isBlank(signtype) ? "in" : signtype;
	}

	public void setSigntype(java.lang.String signtype) {
		this.signtype = signtype;
	}

	public java.lang.Integer getWeekform() {
		return weekform;
	}

	public void setWeekform(java.lang.Integer newWeekform) {
		this.weekform = newWeekform;
	}

	public String getSpecialrest() {
		return specialrest;
	}

	public void setSpecialrest(String specialrest) {
		this.specialrest = specialrest;
	}
}
