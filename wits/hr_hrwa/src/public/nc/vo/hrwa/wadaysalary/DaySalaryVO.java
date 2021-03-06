package nc.vo.hrwa.wadaysalary;

import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFLiteralDate;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;

/**
 * <b> 此处简要描述此类功能 </b>
 * <p>
 * 此处添加累的描述信息
 * </p>
 * 创建日期:2018-9-12
 * 
 * @author YONYOU NC
 * @version NCPrj ??
 */

public class DaySalaryVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2688504682185934163L;
	/**
	 * 日薪主键
	 */
	public String pk_daysalary;
	/**
	 * 薪资方案主键
	 */
	public String pk_wa_class;
	/**
	 * 薪资项目
	 */
	public String pk_wa_item;
	/**
	 * 薪资变动情况
	 */
	public String pk_psndoc_sub;
	/**
	 * 薪资变动情况时间戳
	 */
	public UFDateTime wadocts;
	/**
	 * 人员基本主键
	 */
	public String pk_psndoc;
	/**
	 * 人员任职主键
	 */
	public String pk_psnjob;
	/**
	 * 人力資源組織
	 */
	public String pk_hrorg;
	/**
	 * 薪资日期
	 */
	public UFLiteralDate salarydate;
	/**
	 * 薪资年
	 */
	public Integer cyear;
	/**
	 * 薪资月
	 */
	public Integer cperiod;
	/**
	 * 定调资日薪
	 */
	public UFDouble daysalary;
	/**
	 * 定调资时薪
	 */
	public UFDouble hoursalary;
	/**
	*天数取值方式
	*/
	public Integer initcode;
	/**
	 * 时间戳
	 */
	public UFDateTime ts;
	

	/**
	 * 属性 initcode的Getter方法.属性名：天数取值方式 创建日期:2018-12-13
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getInitcode() {
		return this.initcode;
	}

	/**
	 * 属性initcode的Setter方法.属性名：天数取值方式 创建日期:2018-12-13
	 * 
	 * @param newInitcode
	 *            java.lang.Integer
	 */
	public void setInitcode(Integer initcode) {
		this.initcode = initcode;
	}

	/**
	 * 属性 pk_daysalary的Getter方法.属性名：日薪主键 创建日期:2018-9-12
	 * 
	 * @return java.lang.String
	 */
	public String getPk_daysalary() {
		return this.pk_daysalary;
	}

	/**
	 * 属性pk_daysalary的Setter方法.属性名：日薪主键 创建日期:2018-9-12
	 * 
	 * @param newPk_daysalary
	 *            java.lang.String
	 */
	public void setPk_daysalary(String pk_daysalary) {
		this.pk_daysalary = pk_daysalary;
	}

	/**
	 * 属性 pk_wa_item的Getter方法.属性名：薪资项目 创建日期:2018-9-12
	 * 
	 * @return java.lang.String
	 */
	public String getPk_wa_item() {
		return this.pk_wa_item;
	}

	/**
	 * 属性pk_wa_item的Setter方法.属性名：薪资项目 创建日期:2018-9-12
	 * 
	 * @param newPk_wa_item
	 *            java.lang.String
	 */
	public void setPk_wa_item(String pk_wa_item) {
		this.pk_wa_item = pk_wa_item;
	}

	/**
	 * 属性 pk_psndoc_sub的Getter方法.属性名：薪资变动情况 创建日期:2018-9-12
	 * 
	 * @return java.lang.String
	 */
	public String getPk_psndoc_sub() {
		return this.pk_psndoc_sub;
	}

	/**
	 * 属性pk_psndoc_sub的Setter方法.属性名：薪资变动情况 创建日期:2018-9-12
	 * 
	 * @param newPk_psndoc_sub
	 *            java.lang.String
	 */
	public void setPk_psndoc_sub(String pk_psndoc_sub) {
		this.pk_psndoc_sub = pk_psndoc_sub;
	}

	/**
	 * 属性 pk_psndoc的Getter方法.属性名：人员基本主键 创建日期:2018-9-12
	 * 
	 * @return nc.vo.hi.psndoc.PsndocVO
	 */
	public String getPk_psndoc() {
		return this.pk_psndoc;
	}

	/**
	 * 属性pk_psndoc的Setter方法.属性名：人员基本主键 创建日期:2018-9-12
	 * 
	 * @param newPk_psndoc
	 *            nc.vo.hi.psndoc.PsndocVO
	 */
	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}

	/**
	 * 属性 pk_psnjob的Getter方法.属性名：人员任职主键 创建日期:2018-9-12
	 * 
	 * @return nc.vo.hi.psndoc.PsnJobVO
	 */
	public String getPk_psnjob() {
		return this.pk_psnjob;
	}

	/**
	 * 属性pk_psnjob的Setter方法.属性名：人员任职主键 创建日期:2018-9-12
	 * 
	 * @param newPk_psnjob
	 *            nc.vo.hi.psndoc.PsnJobVO
	 */
	public void setPk_psnjob(String pk_psnjob) {
		this.pk_psnjob = pk_psnjob;
	}

	/**
	 * 属性 pk_hrorg的Getter方法.属性名：人力資源組織 创建日期:2018-9-12
	 * 
	 * @return nc.vo.org.HROrgVO
	 */
	public String getPk_hrorg() {
		return this.pk_hrorg;
	}

	/**
	 * 属性pk_hrorg的Setter方法.属性名：人力資源組織 创建日期:2018-9-12
	 * 
	 * @param newPk_hrorg
	 *            nc.vo.org.HROrgVO
	 */
	public void setPk_hrorg(String pk_hrorg) {
		this.pk_hrorg = pk_hrorg;
	}

	/**
	 * 属性 salarydate的Getter方法.属性名：薪资日期 创建日期:2018-9-12
	 * 
	 * @return nc.vo.pub.lang.UFLiteralDate
	 */
	public UFLiteralDate getSalarydate() {
		return this.salarydate;
	}

	/**
	 * 属性salarydate的Setter方法.属性名：薪资日期 创建日期:2018-9-12
	 * 
	 * @param newSalarydate
	 *            nc.vo.pub.lang.UFLiteralDate
	 */
	public void setSalarydate(UFLiteralDate salarydate) {
		this.salarydate = salarydate;
	}

	/**
	 * 属性 cyear的Getter方法.属性名：薪资年 创建日期:2018-9-12
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getCyear() {
		return this.cyear;
	}

	/**
	 * 属性cyear的Setter方法.属性名：薪资年 创建日期:2018-9-12
	 * 
	 * @param newCyear
	 *            java.lang.Integer
	 */
	public void setCyear(Integer cyear) {
		this.cyear = cyear;
	}

	/**
	 * 属性 cperiod的Getter方法.属性名：薪资月 创建日期:2018-9-12
	 * 
	 * @return java.lang.String
	 */
	public Integer getCperiod() {
		return this.cperiod;
	}

	/**
	 * 属性cperiod的Setter方法.属性名：薪资月 创建日期:2018-9-12
	 * 
	 * @param newCperiod
	 *            java.lang.String
	 */
	public void setCperiod(Integer cperiod) {
		this.cperiod = cperiod;
	}

	/**
	 * 属性 daysalary的Getter方法.属性名：定调资日薪 创建日期:2018-9-12
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getDaysalary() {
		return this.daysalary;
	}

	/**
	 * 属性daysalary的Setter方法.属性名：定调资日薪 创建日期:2018-9-12
	 * 
	 * @param newDaysalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setDaysalary(UFDouble daysalary) {
		this.daysalary = daysalary;
	}

	/**
	 * 属性 hoursalary的Getter方法.属性名：定调资时薪 创建日期:2018-9-12
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getHoursalary() {
		return this.hoursalary;
	}

	/**
	 * 属性hoursalary的Setter方法.属性名：定调资时薪 创建日期:2018-9-12
	 * 
	 * @param newHoursalary
	 *            nc.vo.pub.lang.UFDouble
	 */
	public void setHoursalary(UFDouble hoursalary) {
		this.hoursalary = hoursalary;
	}
	
	

	public String getPk_wa_class() {
		return pk_wa_class;
	}

	public void setPk_wa_class(String pk_wa_class) {
		this.pk_wa_class = pk_wa_class;
	}

	public UFDateTime getWadocts() {
		return wadocts;
	}

	public void setWadocts(UFDateTime wadocts) {
		this.wadocts = wadocts;
	}

	/**
	 * 属性 生成时间戳的Getter方法.属性名：时间戳 创建日期:2018-9-12
	 * 
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public UFDateTime getTs() {
		return this.ts;
	}

	/**
	 * 属性生成时间戳的Setter方法.属性名：时间戳 创建日期:2018-9-12
	 * 
	 * @param newts
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public IVOMeta getMetaData() {
		return VOMetaFactory.getInstance().getVOMeta("hrwa.DaySalaryVO");
	}
}
