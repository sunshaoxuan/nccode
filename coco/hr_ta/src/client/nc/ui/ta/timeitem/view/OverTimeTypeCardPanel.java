package nc.ui.ta.timeitem.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.Collection;

import nc.bs.framework.common.NCLocator;
import nc.hr.utils.ResHelper;
import nc.itf.uap.IUAPQueryBS;
import nc.pubitf.para.SysInitQuery;
import nc.ui.pub.beans.RefEditEvent;
import nc.ui.pub.beans.RefEditListener;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextField;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.beans.textfield.UITextType;
import nc.ui.pub.beans.textfield.formatter.DefaultTextFiledFormatterFactory;
import nc.ui.ta.overtime.refmodel.SegruleRefModel;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.ta.PublicLangRes;
import nc.vo.ta.overtime.CalendarDateTypeEnum;
import nc.vo.ta.overtime.SegRuleVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * �Ӱ���� cardPanel
 * 
 * @author yucheng
 * 
 */
@SuppressWarnings("serial")
public class OverTimeTypeCardPanel extends TimeItemCardPanel {

    // �Ӱ�ת������ֵ
    private UITextField overtimetorest;

    // ��Сʱ�䵥λ
    private UITextField timeunit;

    // �����л���ʾ ���� �� Сʱ ��uiLabel
    private UILabel timeItemUnitLabel;

    public OverTimeTypeCardPanel() {
	buildPanel();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
	if (e.getSource() == this.getTimeitemunit()) {
	    getTimeunit().setValue(UFDouble.ZERO_DBL);
	    getTimeItemUnitLabel().setText(PublicLangRes.MINTIMEUNIT(getTimeitemunit().getSelectedIndex()));
	    getTimeunit().setMaxValue(
		    getTimeitemunit().getSelectedIndex() == TimeItemCopyVO.TIMEITEMUNIT_DAY ? 1 : 1440);
	}
    }

    /**
     * ȡ�Ӱ����panel V6.3ʹ��
     */
    @Override
    public UIPanel getUpPanel() {
	UIPanel upPanel = new UIPanel();
	FormLayout layout = new FormLayout(colwidth, "p,3dlu,p,3dlu,p,3dlu,p,3dlu,p,3dlu,p,3dlu,p,3dlu,p,3dlu,p");
	DefaultFormBuilder builder = new DefaultFormBuilder(layout, upPanel);

	builder.append(PublicLangRes.CODE());
	builder.append(getTimeitemcode());

	builder.append(PublicLangRes.NAME());
	builder.append(getTimeitemname());

	builder.append(ResHelper.getString("common", "UC001-0000118")
	/* @res "����״̬" */);
	builder.append(getEnablestate());
	builder.nextLine();
	builder.nextLine();

	builder.append(PublicLangRes.COMPUNIT());
	builder.append(getTimeitemunit());

	UILabel restLabel = new UILabel(ResHelper.getString("6017basedoc", "06017basedoc1536")
	/* @res "�Ӱ�ת���ݱ���(%)" */);
	builder.append(restLabel);
	builder.append(getOvertimetorest());

	builder.append(getTimeItemUnitLabel());
	builder.append(getTimeunit());
	builder.nextLine();
	builder.nextLine();

	builder.append(PublicLangRes.ROUNDMODE());
	builder.append(getRoundmode());

	// ssx added on 20180425
	// for Taiwan New Law Requirements
	UILabel lblSegRule = new UILabel("�Ӱ�ֶ�����");
	builder.append(lblSegRule);
	builder.append(getRefSegRule());
	builder.nextLine();
	builder.nextLine();

	appendNewOvertimeComponents(builder);
	builder.nextLine();
	builder.nextLine();
	//

	builder.append(getNotePanel(), 11);
	return upPanel;
    }

    // ssx added on 20180425
    // for Taiwan New Law Requirements
    private void appendNewOvertimeComponents(DefaultFormBuilder builder) {
	builder.append(new UILabel());
	// �Ƿ�����Ա�����о���ת��
	builder.append(getSelfDecideCheckBox());
	// ���ռӰ�����?Сʱ
	UILabel lblDayHours = new UILabel("���ռӰ����ޕr��");
	builder.append(lblDayHours);
	builder.append(getTxtDayHours());
	builder.nextLine();
	builder.nextLine();

	builder.append(new UILabel());
	// �Ƿ������¼Ӱ�����ͳ��
	builder.append(getIsIncludeLimitCheckBox());
	// �Ӱ࿪ʼǰ?Сʱ��ʼ��������ͳ��
	UILabel lblFirstOTHours = new UILabel("�{��yӋǰ�r��");
	builder.append(lblFirstOTHours);
	builder.append(getTxtFirstOTHours());
	builder.nextLine();
	builder.nextLine();

	// �Ӱ೬��?Сʱ,�۳�?Сʱ
	UILabel lblDeductBaseHours = new UILabel("�ۜp���ʕr��");
	builder.append(lblDeductBaseHours);
	builder.append(getTxtDeductBaseHours());
	UILabel lblDeductHours = new UILabel("�Ӱ�ۜp�r��");
	builder.append(lblDeductHours);
	builder.append(getTxtDeductHous());
	builder.nextLine();
	builder.nextLine();

	// ������
	UILabel lblDateType = new UILabel("�Օ������");
	builder.append(lblDateType);
	builder.append(getCboDateType());
	// ������Ĭ��
	builder.append(getDateTypeDefault());
	builder.nextLine();
	builder.nextLine();
    }

    private UICheckBox chkDateTypeDef = null;

    public UICheckBox getDateTypeDefault() {
	if (chkDateTypeDef == null) {
	    chkDateTypeDef = new UICheckBox();
	    chkDateTypeDef.setName("ISDATETYPEDEF");
	    chkDateTypeDef.setText("�Ƿ�Ĭ�J");
	    getComponentList().add(chkDateTypeDef);
	}
	return chkDateTypeDef;
    }

    UIComboBox cboDateType = null;

    @SuppressWarnings("unchecked")
    public UIComboBox getCboDateType() {
	if (cboDateType == null) {
	    cboDateType = new UIComboBox();
	    cboDateType.addItem(new DefaultConstEnum(CalendarDateTypeEnum.NORMAL, "ƽ��"));
	    cboDateType.addItem(new DefaultConstEnum(CalendarDateTypeEnum.OFFDAY, "��Ϣ��"));
	    cboDateType.addItem(new DefaultConstEnum(CalendarDateTypeEnum.HOLIDAY, "������"));
	    cboDateType.addItem(new DefaultConstEnum(CalendarDateTypeEnum.NATIONALDAY, "��������"));
	    cboDateType.addActionListener(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
		    if (e.getSource() != null) {
			UIComboBox dateType = (UIComboBox) e.getSource();
			if (dateType.getSelectedItem() != null && cboDateType.isEnabled()) {
			    CalendarDateTypeEnum value = (CalendarDateTypeEnum) dateType.getSelectdItemValue();
			    IUAPQueryBS query = NCLocator.getInstance().lookup(IUAPQueryBS.class);
			    Collection rule;
			    try {
				rule = query.retrieveByClause(SegRuleVO.class,
					"isnull(dr,0)=0 and isdefault='Y' and datetype=" + value.toIntValue());

				if (rule != null && rule.size() > 0) {
				    getRefSegRule().setPK(
					    ((SegRuleVO) rule.toArray(new SegRuleVO[0])[0]).getPk_segrule());
				} else {
				    getRefSegRule().setPK(null);
				}
			    } catch (BusinessException ex) {
				ExceptionUtils.wrappBusinessException(ex.getMessage());
			    }
			}
		    }
		}

	    });
	    getComponentList().add(cboDateType);
	}
	return cboDateType;
    }

    UIRefPane refSegRule = null;

    public UIRefPane getRefSegRule() {
	if (refSegRule == null) {
	    refSegRule = new UIRefPane();
	    refSegRule.setRefModel(new SegruleRefModel());
	    refSegRule.addRefEditListener(new RefEditListener() {
		@Override
		public boolean beforeEdit(RefEditEvent e) {
		    ((SegruleRefModel) refSegRule.getRefModel()).setDateType(((CalendarDateTypeEnum) cboDateType
			    .getSelectdItemValue()).toIntValue());
		    return true;
		}
	    });
	    refSegRule.setName("SEGRULE");
	    refSegRule.setMultiSelectedEnabled(false);
	    refSegRule.getUITextField().setShowMustInputHint(true);
	    refSegRule.getRefModel().setMutilLangNameRef(false);
	    getComponentList().add(refSegRule);
	}
	return refSegRule;
    }

    UITextField txtDeductHours = null;

    public UITextField getTxtDeductHous() {
	if (txtDeductHours == null) {
	    txtDeductHours = new UITextField();
	    txtDeductHours.setName("DEDUCTBASE");
	    txtDeductHours.setTextType(UITextType.TextDbl);
	    txtDeductHours.setMinValue(0);
	    txtDeductHours.setNumPoint(2);
	    getComponentList().add(txtDeductHours);
	}
	return txtDeductHours;
    }

    UITextField txtDeductBaseHours = null;

    public UITextField getTxtDeductBaseHours() {
	if (txtDeductBaseHours == null) {
	    txtDeductBaseHours = new UITextField();
	    txtDeductBaseHours.setName("DEDUCTBASE");
	    txtDeductBaseHours.setTextType(UITextType.TextDbl);
	    txtDeductBaseHours.setMinValue(0);
	    txtDeductBaseHours.setNumPoint(2);
	    getComponentList().add(txtDeductBaseHours);
	}
	return txtDeductBaseHours;
    }

    UITextField txtFirstOTHours = null;

    public UITextField getTxtFirstOTHours() {
	if (txtFirstOTHours == null) {
	    txtFirstOTHours = new UITextField();
	    txtFirstOTHours.setName("FIRSTOTHOURS");
	    txtFirstOTHours.setTextType(UITextType.TextDbl);
	    txtFirstOTHours.setMinValue(0);
	    txtFirstOTHours.setNumPoint(2);
	    getComponentList().add(txtFirstOTHours);
	}
	return txtFirstOTHours;
    }

    UITextField txtDayHours = null;

    public UITextField getTxtDayHours() {
	if (txtDayHours == null) {
	    txtDayHours = new UITextField();
	    txtDayHours.setName("DAYHOURS");
	    txtDayHours.setTextType(UITextType.TextDbl);
	    txtDayHours.setMinValue(0);
	    txtDayHours.setNumPoint(2);
	    getComponentList().add(txtDayHours);
	}
	return txtDayHours;
    }

    UICheckBox chkIncludeLimit = null;

    public UICheckBox getIsIncludeLimitCheckBox() {
	if (chkIncludeLimit == null) {
	    chkIncludeLimit = new UICheckBox();
	    chkIncludeLimit.setName("INCLUDELIMIT");
	    chkIncludeLimit.setText("�Ƿ�{���¼Ӱ����޽yӋ");
	    getComponentList().add(chkIncludeLimit);
	}
	return chkIncludeLimit;
    }

    UICheckBox chkSelfDecide = null;

    public UICheckBox getSelfDecideCheckBox() {
	if (chkSelfDecide == null) {
	    chkSelfDecide = new UICheckBox();
	    chkSelfDecide.setName("SELFDECIDE");
	    chkSelfDecide.setText("�T�����S���ЛQ���D��");
	    getComponentList().add(chkSelfDecide);
	}
	return chkSelfDecide;
    }

    //

    public UILabel getTimeItemUnitLabel() {
	if (timeItemUnitLabel == null)
	    timeItemUnitLabel = new UILabel(PublicLangRes.MINTIMEUNIT());
	return timeItemUnitLabel;
    }

    public UITextField getOvertimetorest() {
	if (overtimetorest == null) {
	    overtimetorest = new UITextField();
	    overtimetorest.setTextType(UITextType.TextDbl);
	    overtimetorest.setMinValue(0);
	    // overtimetorest.setMaxValue(100);
	    overtimetorest.setNumPoint(2);
	    overtimetorest.setTextFiledFormatterFactory(new DefaultTextFiledFormatterFactory(
		    new PercentUFDoubleTextFiledFormatter(overtimetorest)));
	    getComponentList().add(overtimetorest);
	}
	return overtimetorest;
    }

    public UITextField getTimeunit() {
	if (timeunit == null) {
	    timeunit = new UITextField();
	    timeunit.setTextType(UITextType.TextDbl);
	    timeunit.setNumPoint(2);
	    timeunit.setMinValue(0);
	    getComponentList().add(timeunit);
	}
	return timeunit;
    }

    // ssx added on 20180425
    // for Taiwan New Law Requirements
    public void setTWUI(String pk_org) {
	if (!StringUtils.isEmpty(pk_org)) {
	    try {
		UFBoolean isTWEnabled = SysInitQuery.getParaBoolean(pk_org, "TWHR01");
		UFBoolean isOTSegEnabled = SysInitQuery.getParaBoolean(pk_org, "TBMOTSEG");
		if (isTWEnabled != null && isTWEnabled.booleanValue() && isOTSegEnabled != null
			&& isOTSegEnabled.booleanValue()) {
		    getRefSegRule().setRefEditable(true);
		    getSelfDecideCheckBox().setEnabled(true);
		    getTxtDayHours().setEditable(true);
		    getIsIncludeLimitCheckBox().setEnabled(true);
		    getTxtFirstOTHours().setEditable(true);
		    getTxtDeductBaseHours().setEditable(true);
		    getTxtDeductHous().setEditable(true);
		    getOvertimetorest().setEditable(false);
		} else {
		    getRefSegRule().setRefEditable(false);
		    getSelfDecideCheckBox().setEnabled(false);
		    getTxtDayHours().setEditable(false);
		    getIsIncludeLimitCheckBox().setEnabled(false);
		    getTxtFirstOTHours().setEditable(false);
		    getTxtDeductBaseHours().setEditable(false);
		    getTxtDeductHous().setEditable(false);
		    getOvertimetorest().setEditable(true);
		}
	    } catch (BusinessException e) {

	    }
	}
    }
    // end
}