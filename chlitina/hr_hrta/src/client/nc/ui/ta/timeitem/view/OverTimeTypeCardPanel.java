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
 * 加班类别 cardPanel
 * 
 * @author yucheng
 * 
 */
@SuppressWarnings("serial")
public class OverTimeTypeCardPanel extends TimeItemCardPanel {

    // 加班转调休数值
    private UITextField overtimetorest;

    // 最小时间单位
    private UITextField timeunit;

    // 用于切换显示 分钟 或 小时 的uiLabel
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
     * 取加班类别panel V6.3使用
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
	/* @res "启用状态" */);
	builder.append(getEnablestate());
	builder.nextLine();
	builder.nextLine();

	builder.append(PublicLangRes.COMPUNIT());
	builder.append(getTimeitemunit());

	UILabel restLabel = new UILabel(ResHelper.getString("6017basedoc", "06017basedoc1536")
	/* @res "加班转调休比例(%)" */);
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
	UILabel lblSegRule = new UILabel("加班分段依據");
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
	// 是否允许员工自行决定转休
	builder.append(getSelfDecideCheckBox());
	// 单日加班上限?小时
	UILabel lblDayHours = new UILabel("單日加班上限時數");
	builder.append(lblDayHours);
	builder.append(getTxtDayHours());
	builder.nextLine();
	builder.nextLine();

	builder.append(new UILabel());
	// 是否纳入月加班上限统计
	builder.append(getIsIncludeLimitCheckBox());
	// 加班开始前?小时后开始计入上限统计
	UILabel lblFirstOTHours = new UILabel("納入統計前時數");
	builder.append(lblFirstOTHours);
	builder.append(getTxtFirstOTHours());
	builder.nextLine();
	builder.nextLine();

	// 加班超过?小时,扣除?小时
	UILabel lblDeductBaseHours = new UILabel("扣減基準時數");
	builder.append(lblDeductBaseHours);
	builder.append(getTxtDeductBaseHours());
	UILabel lblDeductHours = new UILabel("加班扣減時數");
	builder.append(lblDeductHours);
	builder.append(getTxtDeductHous());
	builder.nextLine();
	builder.nextLine();

	// 日历天
	UILabel lblDateType = new UILabel("日曆天類型");
	builder.append(lblDateType);
	builder.append(getCboDateType());
	// 日历天默认
	builder.append(getDateTypeDefault());
	builder.nextLine();
	builder.nextLine();
    }

    private UICheckBox chkDateTypeDef = null;

    public UICheckBox getDateTypeDefault() {
	if (chkDateTypeDef == null) {
	    chkDateTypeDef = new UICheckBox();
	    chkDateTypeDef.setName("ISDATETYPEDEF");
	    chkDateTypeDef.setText("是否默認");
	    getComponentList().add(chkDateTypeDef);
	}
	return chkDateTypeDef;
    }

    UIComboBox cboDateType = null;

    @SuppressWarnings("unchecked")
    public UIComboBox getCboDateType() {
	if (cboDateType == null) {
	    cboDateType = new UIComboBox();
	    cboDateType.addItem(new DefaultConstEnum(CalendarDateTypeEnum.NORMAL, "平日"));
	    cboDateType.addItem(new DefaultConstEnum(CalendarDateTypeEnum.OFFDAY, "休息日"));
	    cboDateType.addItem(new DefaultConstEnum(CalendarDateTypeEnum.HOLIDAY, "例假日"));
	    cboDateType.addItem(new DefaultConstEnum(CalendarDateTypeEnum.NATIONALDAY, "國定假日"));
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
	    chkIncludeLimit.setText("是否納入月加班上限統計");
	    getComponentList().add(chkIncludeLimit);
	}
	return chkIncludeLimit;
    }

    UICheckBox chkSelfDecide = null;

    public UICheckBox getSelfDecideCheckBox() {
	if (chkSelfDecide == null) {
	    chkSelfDecide = new UICheckBox();
	    chkSelfDecide.setName("SELFDECIDE");
	    chkSelfDecide.setText("員工允許自行決定轉休");
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