package nc.vo.hrwa.sumincometax;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * 
 * @author ward.wong
 * @date 20180126
 * @version v1.0
 * @功能描述 申报明细档汇总单据子表VO
 * 
 */
public class CIncomeTaxVO extends SuperVO {
	/**
	 * 申报明细档主键
	 */
	public String pk_incometaxdetail;
	/**
	 * 员工个人所得税申报主键
	 */
	public String pk_incometax;
	/**
	 * 员工编号
	 */
	public String code;
	/**
	 * 员工姓名
	 */
	public String pk_psndoc;
	/**
	 * 身份证号
	 */
	public String id;
	/**
	 * 期间
	 */
	public String cperiod;
	/**
	 * 年度
	 */
	public String cyear;
	/**
	 * 薪资期间
	 */
	public String cyearperiod;
	/**
	 * 薪资方案
	 */
	public String pk_wa_class;
	/**
	 * 给付总额
	 */
	public UFDouble taxbase;
	/**
	 * 扣缴税额
	 */
	public UFDouble cacu_value;
	/**
	 * 给付净额
	 */
	public UFDouble netincome;
	/**
	 * 员工自提金额
	 */
	public UFDouble pickedup;
	/**
	 * 是否申报
	 */
	public UFBoolean isdeclare;
	/**
	 * 统一编号
	 */
	public String unifiednumber;
	/**
	 * 申报凭单格式
	 */
	public String declaretype;
	/**
	 * 是否外籍员工逐月申报
	 */
	public UFBoolean isforeignmonthdec;
	/**
	 * 创建人
	 */
	public String creator;
	/**
	 * 创建时间
	 */
	public UFDateTime creationtime;
	/**
	 * 修改人
	 */
	public String modifier;
	/**
	 * 修改时间
	 */
	public UFDateTime modifiedtime;
	/**
	 * 集团
	 */
	public String pk_group;
	/**
	 * 组织
	 */
	public String pk_org;
	/**
	 * 单据日期
	 */
	public UFDate billdate;
	/**
	 * 薪资数据表主键
	 */
	public String pk_wa_data;
	/**
	 * 到账日期
	 */
	public UFDate cpaydate;
	/**
	 * 发放标志
	 */
	public UFBoolean payoffflag;
	/**
	 * 人力资源组织
	 */
	public String pk_hrorg;
	/**
	 * 上层单据主键
	 */
	public String pk_sumincometax;
	/**
	 * 时间戳
	 */
	public UFDateTime ts;

	/**
	 * 是否外籍员工离境申报
	 */
	public UFBoolean isforeigndeprdec;

	public UFBoolean getIsforeigndeprdec() {
		return isforeigndeprdec;
	}

	public void setIsforeigndeprdec(UFBoolean isforeigndeprdec) {
		this.isforeigndeprdec = isforeigndeprdec;
	}

	public String getPk_incometaxdetail() {
		return pk_incometaxdetail;
	}

	public void setPk_incometaxdetail(String pk_incometaxdetail) {
		this.pk_incometaxdetail = pk_incometaxdetail;
	}

	/**
	 * 属性 pk_incometax的Getter方法.属性名：员工个人所得税申报主键 创建日期:2018-1-24
	 * 
	 * @return java.lang.String
	 */
	public String getPk_incometax() {
		return this.pk_incometax;
	}

	/**
	 * 属性pk_incometax的Setter方法.属性名：员工个人所得税申报主键 创建日期:2018-1-24
	 * 
	 * @param newPk_incometax
	 *            java.lang.String
	 */
	public void setPk_incometax(String pk_incometax) {
		this.pk_incometax = pk_incometax;
	}

	/**
	 * 属性 code的Getter方法.属性名：员工编号 创建日期:2018-1-24
	 * 
	 * @return java.lang.String
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * 属性code的Setter方法.属性名：员工编号 创建日期:2018-1-24
	 * 
	 * @param newCode
	 *            java.lang.String
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 属性 pk_psndoc的Getter方法.属性名：员工姓名 创建日期:2018-1-24
	 * 
	 * @return nc.vo.hi.psndoc.PsndocVO
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * 属性pk_psndoc的Setter方法.属性名：员工姓名 创建日期:2018-1-24
	 * 
	 * @param newPk_psndoc
	 *            nc.vo.hi.psndoc.PsndocVO
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * 属性 id的Getter方法.属性名：身份证号 创建日期:2018-1-24
	 * 
	 * @return java.lang.String
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * 属性id的Setter方法.属性名：身份证号 创建日期:2018-1-24
	 * 
	 * @param newId
	 *            java.lang.String
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 属性 cperiod的Getter方法.属性名：期间 创建日期:2018-1-24
	 * 
	 * @return java.lang.String
	 */
	public String getCperiod() {
		return this.cperiod;
	}

	/**
	 * 属性cperiod的Setter方法.属性名：期间 创建日期:2018-1-24
	 * 
	 * @param newCperiod
	 *            java.lang.String
	 */
	public void setCperiod(String cperiod) {
		this.cperiod = cperiod;
	}

	/**
	 * 属性 cyear的Getter方法.属性名：年度 创建日期:2018-1-24
	 * 
	 * @return java.lang.String
	 */
	public String getCyear() {
		return this.cyear;
	}

	/**
	 * 属性cyear的Setter方法.属性名：年度 创建日期:2018-1-24
	 * 
	 * @param newCyear
	 *            java.lang.String
	 */
	public void setCyear(String cyear) {
		this.cyear = cyear;
	}

	/**
	 * 属性 cyearperiod的Getter方法.属性名：薪资期间 创建日期:2018-1-24
	 * 
	 * @return nc.vo.wa.period.PeriodVO
	 */
	public String getCyearperiod() {
		return this.cyearperiod;
	}

	/**
	 * 属性cyearperiod的Setter方法.属性名：薪资期间 创建日期:2018-1-24
	 * 
	 * @param newCyearperiod
	 *            nc.vo.wa.period.PeriodVO
	 */
	public void setCyearperiod(String cyearperiod) {
		this.cyearperiod = cyearperiod;
	}

	/**
	 * 属性 pk_wa_class的Getter方法.属性名：薪资方案 创建日期:2018-1-24
	 * 
	 * @return nc.vo.wa.category.WaClassVO
	 */
	public String getPk_wa_class() {
		return this.pk_wa_class;
	}

	/**
	 * 属性pk_wa_class的Setter方法.属性名：薪资方案 创建日期:2018-1-24
	 * 
	 * @param newPk_wa_class
	 *            nc.vo.wa.category.WaClassVO
	 */
	public void setPk_wa_class(String pk_wa_class) {
		this.pk_wa_class = pk_wa_class;
	}

	/**
	 * 属性 taxbase的Getter方法.属性名：给付总额 创建日期:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getTaxbase() {
		return this.taxbase;
	}

	/**
	 * 属性taxbase的Setter方法.属性名：给付总额 创建日期:2018-1-24
	 * 
	 * @param newTaxbase
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setTaxbase(UFDouble taxbase) {
		this.taxbase = taxbase;
	}

	/**
	 * 属性 cacu_value的Getter方法.属性名：扣缴税额 创建日期:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getCacu_value() {
		return this.cacu_value;
	}

	/**
	 * 属性cacu_value的Setter方法.属性名：扣缴税额 创建日期:2018-1-24
	 * 
	 * @param newCacu_value
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setCacu_value(UFDouble cacu_value) {
		this.cacu_value = cacu_value;
	}

	/**
	 * 属性 netincome的Getter方法.属性名：给付净额 创建日期:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getNetincome() {
		return this.netincome;
	}

	/**
	 * 属性netincome的Setter方法.属性名：给付净额 创建日期:2018-1-24
	 * 
	 * @param newNetincome
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setNetincome(UFDouble netincome) {
		this.netincome = netincome;
	}

	/**
	 * 属性 pickedup的Getter方法.属性名：员工自提金额 创建日期:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getPickedup() {
		return this.pickedup;
	}

	/**
	 * 属性pickedup的Setter方法.属性名：员工自提金额 创建日期:2018-1-24
	 * 
	 * @param newPickedup
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setPickedup(UFDouble pickedup) {
		this.pickedup = pickedup;
	}

	/**
	 * 属性 isdeclare的Getter方法.属性名：是否申报 创建日期:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getIsdeclare() {
		return this.isdeclare;
	}

	/**
	 * 属性isdeclare的Setter方法.属性名：是否申报 创建日期:2018-1-24
	 * 
	 * @param newIsdeclare
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIsdeclare(UFBoolean isdeclare) {
		this.isdeclare = isdeclare;
	}

	/**
	 * 属性 unifiednumber的Getter方法.属性名：统一编号 创建日期:2018-1-24
	 * 
	 * @return nc.vo.bd.defdoc.DefdocVO
	 */
	public String getUnifiednumber() {
		return this.unifiednumber;
	}

	/**
	 * 属性unifiednumber的Setter方法.属性名：统一编号 创建日期:2018-1-24
	 * 
	 * @param newUnifiednumber
	 *            nc.vo.bd.defdoc.DefdocVO
	 */
	public void setUnifiednumber(String unifiednumber) {
		this.unifiednumber = unifiednumber;
	}

	/**
	 * 属性 declaretype的Getter方法.属性名：申报凭单格式 创建日期:2018-1-24
	 * 
	 * @return nc.vo.bd.defdoc.DefdocVO
	 */
	public String getDeclaretype() {
		return this.declaretype;
	}

	/**
	 * 属性declaretype的Setter方法.属性名：申报凭单格式 创建日期:2018-1-24
	 * 
	 * @param newDeclaretype
	 *            nc.vo.bd.defdoc.DefdocVO
	 */
	public void setDeclaretype(String declaretype) {
		this.declaretype = declaretype;
	}

	/**
	 * 属性 isforeignmonthdec的Getter方法.属性名：是否外籍员工逐月申报 创建日期:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getIsforeignmonthdec() {
		return this.isforeignmonthdec;
	}

	/**
	 * 属性isforeignmonthdec的Setter方法.属性名：是否外籍员工逐月申报 创建日期:2018-1-24
	 * 
	 * @param newIsforeignmonthdec
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIsforeignmonthdec(UFBoolean isforeignmonthdec) {
		this.isforeignmonthdec = isforeignmonthdec;
	}

	/**
	 * 属性 creator的Getter方法.属性名：创建人 创建日期:2018-1-24
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getCreator() {
		return this.creator;
	}

	/**
	 * 属性creator的Setter方法.属性名：创建人 创建日期:2018-1-24
	 * 
	 * @param newCreator
	 *            nc.vo.sm.UserVO
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * 属性 creationtime的Getter方法.属性名：创建时间 创建日期:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getCreationtime() {
		return this.creationtime;
	}

	/**
	 * 属性creationtime的Setter方法.属性名：创建时间 创建日期:2018-1-24
	 * 
	 * @param newCreationtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	/**
	 * 属性 modifier的Getter方法.属性名：修改人 创建日期:2018-1-24
	 * 
	 * @return nc.vo.sm.UserVO
	 */
	public String getModifier() {
		return this.modifier;
	}

	/**
	 * 属性modifier的Setter方法.属性名：修改人 创建日期:2018-1-24
	 * 
	 * @param newModifier
	 *            nc.vo.sm.UserVO
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * 属性 modifiedtime的Getter方法.属性名：修改时间 创建日期:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getModifiedtime() {
		return this.modifiedtime;
	}

	/**
	 * 属性modifiedtime的Setter方法.属性名：修改时间 创建日期:2018-1-24
	 * 
	 * @param newModifiedtime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
	}

	/**
	 * 属性 pk_group的Getter方法.属性名：集团 创建日期:2018-1-24
	 * 
	 * @return nc.vo.org.GroupVO
	 */
	public String getPk_group() {
		return this.pk_group;
	}

	/**
	 * 属性pk_group的Setter方法.属性名：集团 创建日期:2018-1-24
	 * 
	 * @param newPk_group
	 *            nc.vo.org.GroupVO
	 */
	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	/**
	 * 属性 pk_org的Getter方法.属性名：组织 创建日期:2018-1-24
	 * 
	 * @return nc.vo.org.OrgVO
	 */
	public String getPk_org() {
		return this.pk_org;
	}

	/**
	 * 属性pk_org的Setter方法.属性名：组织 创建日期:2018-1-24
	 * 
	 * @param newPk_org
	 *            nc.vo.org.OrgVO
	 */
	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	/**
	 * 属性 billdate的Getter方法.属性名：单据日期 创建日期:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDate getBilldate() {
		return this.billdate;
	}

	/**
	 * 属性billdate的Setter方法.属性名：单据日期 创建日期:2018-1-24
	 * 
	 * @param newBilldate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setBilldate(UFDate billdate) {
		this.billdate = billdate;
	}

	/**
	 * 属性 pk_wa_data的Getter方法.属性名：薪资数据表主键 创建日期:2018-1-24
	 * 
	 * @return nc.vo.wa.payslip.WaDataVO
	 */
	public String getPk_wa_data() {
		return this.pk_wa_data;
	}

	/**
	 * 属性pk_wa_data的Setter方法.属性名：薪资数据表主键 创建日期:2018-1-24
	 * 
	 * @param newPk_wa_data
	 *            nc.vo.wa.payslip.WaDataVO
	 */
	public void setPk_wa_data(String pk_wa_data) {
		this.pk_wa_data = pk_wa_data;
	}

	/**
	 * 属性 cpaydate的Getter方法.属性名：到账日期 创建日期:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public UFDate getCpaydate() {
		return this.cpaydate;
	}

	/**
	 * 属性cpaydate的Setter方法.属性名：到账日期 创建日期:2018-1-24
	 * 
	 * @param newCpaydate
	 *            nc.vo.pub.lang.UFDate
	 */
	public void setCpaydate(UFDate cpaydate) {
		this.cpaydate = cpaydate;
	}

	/**
	 * 属性 payoffflag的Getter方法.属性名：发放标志 创建日期:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getPayoffflag() {
		return this.payoffflag;
	}

	/**
	 * 属性payoffflag的Setter方法.属性名：发放标志 创建日期:2018-1-24
	 * 
	 * @param newPayoffflag
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setPayoffflag(UFBoolean payoffflag) {
		this.payoffflag = payoffflag;
	}

	/**
	 * 属性 pk_hrorg的Getter方法.属性名：人力资源组织 创建日期:2018-1-24
	 * 
	 * @return nc.vo.org.HROrgVO
	 */
	public String getPk_hrorg() {
		return this.pk_hrorg;
	}

	/**
	 * 属性pk_hrorg的Setter方法.属性名：人力资源组织 创建日期:2018-1-24
	 * 
	 * @param newPk_hrorg
	 *            nc.vo.org.HROrgVO
	 */
	public void setPk_hrorg(String pk_hrorg) {
		this.pk_hrorg = pk_hrorg;
	}

	/**
	 * 属性 生成上层主键的Getter方法.属性名：上层主键 创建日期:2018-1-24
	 * 
	 * @return String
	 */
	public String getPk_sumincometax() {
		return this.pk_sumincometax;
	}

	/**
	 * 属性生成上层主键的Setter方法.属性名：上层主键 创建日期:2018-1-24
	 * 
	 * @param newPk_sumincometax
	 *            String
	 */
	public void setPk_sumincometax(String pk_sumincometax) {
		this.pk_sumincometax = pk_sumincometax;
	}

	/**
	 * 属性 生成时间戳的Getter方法.属性名：时间戳 创建日期:2018-1-24
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * 属性生成时间戳的Setter方法.属性名：时间戳 创建日期:2018-1-24
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("hrwa.CIncomeTaxVO");
	}
}
