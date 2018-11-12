package nc.impl.wa.paydata.nhicalculate;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

public class PsnHealthDataVO extends SuperVO {
    /**
     * serial version id
     */
    private static final long serialVersionUID = -4659690173533276532L;

    private String pk_psndoc; // ��Ա��Ϣ
    private String psnType; // ����Ͷ������
    private String pk_legalorg; // ������֯
    private UFDouble healthRange = UFDouble.ZERO_DBL; // ��������
    private UFDouble personCount = UFDouble.ZERO_DBL; // ��������(������)
    private UFDouble healthAmount_Psn = UFDouble.ZERO_DBL; // �����ѳе����(����)
    private UFDouble healthAmount_Org = UFDouble.ZERO_DBL; // �����ѳе����(����)
    private UFDouble healthAmount_Family = UFDouble.ZERO_DBL;// �����ѳе���������
    private UFDouble govnHelpAmount = UFDouble.ZERO_DBL; // ��������������
    private UFDouble healthAmount = UFDouble.ZERO_DBL; // ������Ӧ�ɽ��
    private UFBoolean isContainLastMonth; // �Ƿ���������ͱ�

    public String getPk_psndoc() {
	return pk_psndoc;
    }

    public void setPk_psndoc(String pk_psndoc) {
	this.pk_psndoc = pk_psndoc;
    }

    public UFDouble getHealthRange() {
	if (healthRange == null) {
	    healthRange = UFDouble.ZERO_DBL;
	}
	return healthRange;
    }

    public void setHealthRange(UFDouble healthRange) {
	this.healthRange = healthRange;
    }

    public UFDouble getPersonCount() {
	if (personCount == null) {
	    personCount = UFDouble.ZERO_DBL;
	}
	return personCount;
    }

    public void setPersonCount(UFDouble personCount) {
	this.personCount = personCount;
    }

    public UFDouble getHealthAmount_Psn() {
	if (healthAmount_Psn == null) {
	    healthAmount_Psn = UFDouble.ZERO_DBL;
	}
	return healthAmount_Psn;
    }

    public void setHealthAmount_Psn(UFDouble healthAmount_Psn) {
	this.healthAmount_Psn = healthAmount_Psn;
    }

    public UFDouble getHealthAmount_Org() {
	if (healthAmount_Org == null) {
	    healthAmount_Org = UFDouble.ZERO_DBL;
	}
	return healthAmount_Org;
    }

    public void setHealthAmount_Org(UFDouble healthAmount_Org) {
	this.healthAmount_Org = healthAmount_Org;
    }

    public UFDouble getGovnHelpAmount() {
	if (govnHelpAmount == null) {
	    govnHelpAmount = UFDouble.ZERO_DBL;
	}
	return govnHelpAmount;
    }

    public void setGovnHelpAmount(UFDouble govnHelpAmount) {
	this.govnHelpAmount = govnHelpAmount;
    }

    public UFDouble getHealthAmount() {
	if (healthAmount == null) {
	    healthAmount = UFDouble.ZERO_DBL;
	}
	return healthAmount;
    }

    public void setHealthAmount(UFDouble healthAmount) {
	this.healthAmount = healthAmount;
    }

    public UFDouble getHealthAmount_Family() {
	return healthAmount_Family;
    }

    public void setHealthAmount_Family(UFDouble healthAmount_family) {
	this.healthAmount_Family = healthAmount_family;
    }

    public UFBoolean getIsContainLastMonth() {
	return isContainLastMonth;
    }

    public void setIsContainLastMonth(UFBoolean isContainLastMonth) {
	this.isContainLastMonth = isContainLastMonth;
    }

    public static long getSerialversionuid() {
	return serialVersionUID;
    }

    public String getPsnType() {
	return psnType;
    }

    public void setPsnType(String psnType) {
	this.psnType = psnType;
    }

    public String getPk_legalorg() {
	return pk_legalorg;
    }

    public void setPk_legalorg(String pk_legalorg) {
	this.pk_legalorg = pk_legalorg;
    }
}