package nc.ui.ta.timeitem.view;

import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.ta.basedoc.RefDefVOWrapper;
import nc.vo.ta.overtime.CalendarDateTypeEnum;
import nc.vo.ta.timeitem.OverTimeTypeCopyVO;
import nc.vo.ta.timeitem.TimeItemCopyVO;
import nc.vo.ta.timeitem.TimeItemVO;
import nc.vo.uif2.LoginContext;

/**
 * 加班类别 cardView
 * 
 * @author yucheng
 * 
 */
@SuppressWarnings("serial")
public class OverTimeTypeCardView extends TimeItemCardView {

    private OverTimeTypeCardPanel cardPanel;

    @Override
    public void setDefaultValue() {
	OverTimeTypeCopyVO vo = new OverTimeTypeCopyVO();
	vo = (OverTimeTypeCopyVO) getPubDefaultValue(vo);
	setValue(vo);
    }

    @Override
    public Object getValue() {
	OverTimeTypeCopyVO vo = new OverTimeTypeCopyVO();
	vo = (OverTimeTypeCopyVO) getPubValue(vo);
	OverTimeTypeCardPanel cardPanel = getCardPanel();
	cardPanel.stopEditing();
	vo.setOvertimetorest((UFDouble) cardPanel.getOvertimetorest().getValue());
	vo.setTimeunit(cardPanel.getTimeunit().getValue() == null ? UFDouble.ZERO_DBL : (UFDouble) cardPanel
		.getTimeunit().getValue());
	vo.setItemtype(TimeItemCopyVO.OVERTIME_TYPE);

	// ssx added on 20180425
	// for Taiwan New Law Requirements
	// 是否允许员工自行决定转休
	vo.setIsstuffdecidecomp(new UFBoolean(cardPanel.getSelfDecideCheckBox().isSelected()));
	// 单日加班上限?小时
	vo.setDaylimit(new UFDouble(cardPanel.getTxtDayHours().getText()));
	// 是否纳入月加班上限统计
	vo.setIsincludewithlimit(new UFBoolean(cardPanel.getIsIncludeLimitCheckBox().isSelected()));
	// 首段加班最低時數
	vo.setEffectivehours(new UFDouble(cardPanel.getTxtFirstOTHours().getText()));
	// 加班超过?小时,扣除?小时
	vo.setDeductlowhours(new UFDouble(cardPanel.getTxtDeductBaseHours().getText()));
	vo.setDeductminutes(new UFDouble(cardPanel.getTxtDeductHous().getText()));
	// 加班分段依據
	vo.setPk_segrule(cardPanel.getRefSegRule().getRefPK());
	// 日历天类型
	vo.setDate_type(cardPanel.getCboDateType().getSelectdItemValue() == null ? null
		: ((CalendarDateTypeEnum) cardPanel.getCboDateType().getSelectdItemValue()).toIntValue());
	// 日历天默认
	vo.setIsdatetypedefault(new UFBoolean(cardPanel.getDateTypeDefault().isSelected()));
	//

	return vo;
    }

    @Override
    public void setValue(Object object) {
	super.setValue(object);
	if (!(object instanceof OverTimeTypeCopyVO))
	    return;
	OverTimeTypeCopyVO vo = (OverTimeTypeCopyVO) object;
	OverTimeTypeCardPanel cardPanel = getCardPanel();
	cardPanel.getOvertimetorest().setValue(
		vo.getOvertimetorest() == null ? UFDouble.ZERO_DBL : vo.getOvertimetorest());
	cardPanel.getTimeunit().setValue(vo.getTimeunit());

	// ssx added on 20180425
	// for Taiwan New Law Requirements
	// 是否允许员工自行决定转休
	cardPanel.getSelfDecideCheckBox().setSelected(
		vo.getIsstuffdecidecomp() == null ? false : vo.getIsstuffdecidecomp().booleanValue());
	// 单日加班上限?小时
	cardPanel.getTxtDayHours().setText(String.valueOf(vo.getDaylimit()));
	// 是否纳入月加班上限统计
	cardPanel.getIsIncludeLimitCheckBox().setSelected(
		vo.getIsincludewithlimit() == null ? false : vo.getIsincludewithlimit().booleanValue());
	// 首段加班最低时数
	cardPanel.getTxtFirstOTHours().setText(String.valueOf(vo.getEffectivehours()));
	// 扣減基準時數
	cardPanel.getTxtDeductBaseHours().setText(String.valueOf(vo.getDeductlowhours()));
	// 加班扣減時數
	cardPanel.getTxtDeductHous().setText(String.valueOf(vo.getDeductminutes()));
	// 加班分段依據
	cardPanel.getRefSegRule().setPK(vo.getPk_segrule());
	// 日历天类型
	if (vo.getDate_type() != null) {
	    cardPanel.getCboDateType().setSelectedItem(
		    CalendarDateTypeEnum.valueOf(CalendarDateTypeEnum.class, vo.getDate_type()));
	}
	// 日历天默认
	cardPanel.getDateTypeDefault().setSelected(
		vo.getIsdatetypedefault() != null && vo.getIsdatetypedefault().booleanValue());
	//
    }

    @Override
    public TimeItemCopyVO[] onRef(RefDefVOWrapper<TimeItemVO> objs) throws BusinessException {
	return getModel().refOvertimeTypes(getModel().getContext(), objs);
    }

    @Override
    public void onSynchronize() throws BusinessException {
	getModel().synchronizeOvertimeTypes(getModel().getContext());
    }

    @Override
    public RefDefVOWrapper<TimeItemVO> queryRefDefVOs() throws BusinessException {
	return getModel().queryOvertimeRefDefVOs(getModel().getContext());
    }

    @Override
    public void onUIStateChange(LoginContext context) {
	getCardPanel().setTWUI(context.getPk_org());
    }

    public OverTimeTypeCardPanel getCardPanel() {
	if (cardPanel == null)
	    cardPanel = new OverTimeTypeCardPanel();
	return cardPanel;
    }

    public void setCardPanel(OverTimeTypeCardPanel cardPanel) {
	this.cardPanel = cardPanel;
    }
}
