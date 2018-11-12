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
 * �Ӱ���� cardView
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
		// �Ƿ�����Ա�����о���ת��
		vo.setIsstuffdecidecomp(new UFBoolean(cardPanel.getSelfDecideCheckBox().isSelected()));
		// ���ռӰ�����?Сʱ
		vo.setDaylimit(new UFDouble(cardPanel.getTxtDayHours().getText()));
		// �Ƿ������¼Ӱ�����ͳ��
		vo.setIsincludewithlimit(new UFBoolean(cardPanel.getIsIncludeLimitCheckBox().isSelected()));
		// �׶μӰ���͕r��
		vo.setEffectivehours(new UFDouble(cardPanel.getTxtFirstOTHours().getText()));
		// �Ӱ೬��?Сʱ,�۳�?Сʱ
		vo.setDeductlowhours(new UFDouble(cardPanel.getTxtDeductBaseHours().getText()));
		vo.setDeductminutes(new UFDouble(cardPanel.getTxtDeductHous().getText()));
		// �Ӱ�ֶ�����
		vo.setPk_segrule(cardPanel.getRefSegRule().getRefPK());
	vo.setDate_type(cardPanel.getCboDateType().getSelectdItemValue() == null ? null
		: ((CalendarDateTypeEnum) cardPanel.getCboDateType().getSelectdItemValue()).toIntValue());
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
		// �Ƿ�����Ա�����о���ת��
		cardPanel.getSelfDecideCheckBox().setSelected(
				vo.getIsstuffdecidecomp() == null ? false : vo.getIsstuffdecidecomp().booleanValue());
		// ���ռӰ�����?Сʱ
		cardPanel.getTxtDayHours().setText(String.valueOf(vo.getDaylimit()));
		// �Ƿ������¼Ӱ�����ͳ��
		cardPanel.getIsIncludeLimitCheckBox().setSelected(
				vo.getIsincludewithlimit() == null ? false : vo.getIsincludewithlimit().booleanValue());
		// �׶μӰ����ʱ��
		cardPanel.getTxtFirstOTHours().setText(String.valueOf(vo.getEffectivehours()));
		// �Ӱ೬��?Сʱ,�۳�?Сʱ
		cardPanel.getTxtDeductBaseHours().setText(String.valueOf(vo.getDeductlowhours()));
		cardPanel.getTxtDeductHous().setText(String.valueOf(vo.getDeductminutes()));
		// �Ӱ�ֶ�����
		cardPanel.getRefSegRule().setPK(vo.getPk_segrule());
	if (vo.getDate_type() != null) {
	    cardPanel.getCboDateType().setSelectedItem(
		    CalendarDateTypeEnum.valueOf(CalendarDateTypeEnum.class, vo.getDate_type()));
	}
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