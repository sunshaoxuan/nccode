package nc.ui.wa.formular;

import java.awt.Dimension;

import javax.swing.SwingConstants;

import nc.hr.utils.ResHelper;
import nc.ui.bd.ref.model.DefdocGridRefModel;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.vo.hr.dataio.ConstEnumFactory;

/**
 * #21266 ���պϼƼӰ�Ѻ������
 * 
 * @author ssx
 * @date 2019-4-5
 */
public class LeaveOverTimeFeeFunctionEditor extends WaAbstractFunctionEditor {

	/**
	 * serial no
	 */
	private static final long serialVersionUID = -4468484862772531470L;

	private UILabel label = null;
	private UIComboBox yOrnCBox = null;

	// н����Ŀ������� Ares.Tank 2019��1��20��21:17:47
	private UILabel groupLabel = null;
	private UIRefPane groupRef = null;

	@Override
	public void setModel(AbstractUIAppModel model) {
		// TODO Auto-generated method stub
		super.setModel(model);
	}

	/**
	 * WaParaPanel ������ע�⡣
	 */
	public LeaveOverTimeFeeFunctionEditor() {
		super();
		initialize();
	}

	private static final String funcname = "@" + ResHelper.getString("6013commonbasic", "06013commonbasic0274") + "@";

	// "taxRate";
	@Override
	public String getFuncName() {
		// TODO Auto-generated method stub
		return funcname;
	}

	/**
	 * ��ʼ���ࡣ
	 */
	/* ���棺�˷������������ɡ� */
	private void initialize() {
		try {
			setLayout(null);
			setSize(300, 150);
			setTitle("Ո�x�񅢔�");

			add(getUILabel(), getUILabel().getName());
			add(getYOrnCBox(), getYOrnCBox().getName());

			add(getGroupLabel(), getGroupLabel().getName());
			add(getGroupRef(), getGroupRef().getName());

			add(getOkButton(), getOkButton().getName());
			add(getCancelButton(), getCancelButton().getName());

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}

		initConnection();

	}

	public UILabel getGroupLabel() {
		if (this.groupLabel == null) {
			try {
				this.groupLabel = new UILabel();
				this.groupLabel.setName("UILabel3");
				this.groupLabel.setText("н����Ŀ���飺");
				this.groupLabel.setHorizontalAlignment(SwingConstants.RIGHT);
				this.groupLabel.setBounds(10, 50, 100, 22);
			} catch (Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return this.groupLabel;
	}

	public UIRefPane getGroupRef() {
		if (groupRef == null) {
			groupRef = new UIRefPane();
			DefdocGridRefModel model = new DefdocGridRefModel();
			// ����bd_refinfo�е�refclass
			groupRef.setRefModel(model);
			// ��������(Ҫ��bd_refinfo�е�һ��)
			groupRef.setRefNodeName("н����Ŀ����");
			groupRef.getRefModel().setMutilLangNameRef(false);
			groupRef.setPk_org(null);
			// refGroupIns.set
			groupRef.setVisible(true);
			groupRef.setPreferredSize(new Dimension(50, 20));
			groupRef.setButtonFireEvent(true);
			groupRef.getUITextField().setShowMustInputHint(true);
			groupRef.setName("groupRef");
			groupRef.setBounds(120, 50, 150, 22);

		}
		return groupRef;
	}

	/**
	 * ���� UILabel3 ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UILabel
	 */
	/* ���棺�˷������������ɡ� */
	private UILabel getUILabel() {
		if (label == null) {
			try {
				label = new UILabel();
				label.setName("label");
				label.setText(ResHelper.getString("6013commonbasic", "06013commonbasic0269")/*
																							 * @
																							 * res
																							 * "�Ƿ���˰"
																							 */);
				label.setBounds(10, 20, 100, 22);
				label.setHorizontalAlignment(SwingConstants.RIGHT);

			} catch (java.lang.Throwable labExc) {
				handleException(labExc);
			}
		}
		return label;
	}

	/**
	 * ���� refPeriodItem ����ֵ��
	 * 
	 * @return nc.ui.pub.beans.UIRefPane
	 */
	/* ���棺�˷������������ɡ� */
	private UIComboBox getYOrnCBox() {
		if (yOrnCBox == null) {
			try {
				yOrnCBox = new UIComboBox();
				String[] ml = new String[2];
				ml[0] = ResHelper.getString("6013commonbasic", "06013commonbasic0270")/*
																					 * @
																					 * res
																					 * "��"
																					 */;
				ml[1] = ResHelper.getString("6013commonbasic", "06013commonbasic0271")/*
																					 * @
																					 * res
																					 * "��"
																					 */;

				Integer[] mlDefault = new Integer[] { 0, 1 };
				ConstEnumFactory<Integer> mPairFactory = new ConstEnumFactory<Integer>(ml, mlDefault);
				yOrnCBox.addItems(mPairFactory.getAllConstEnums());
				yOrnCBox.setBounds(120, 20, 150, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return yOrnCBox;
	}

	/**
	 * /** * ���������Ϸ���У�� * @param ����˵�� * @return ����ֵ * @exception �쳣���� * @see
	 * ��Ҫ�μ����������� * @since �������һ���汾���˷��������ӽ���������ѡ�� *
	 * 
	 * @deprecated�÷����������һ���汾���Ѿ������������滻������ѡ�� *-/
	 * 
	 * @return java.lang.String
	 */
	public boolean checkPara(int dataType) {
		try {
			// �жϷǿ�
			String nullstr = "";
			if (getYOrnCBox().getSelectedIndex() < 0) {
				if (nullstr.length() > 0)
					nullstr += ",";
				nullstr += ResHelper.getString("6013commonbasic", "06013commonbasic0269")/*
																						 * @
																						 * res
																						 * "�Ƿ���˰"
																						 */;
			}
			if (nullstr.length() > 0)
				throw new Exception(nullstr + ResHelper.getString("6013commonbasic", "06013commonbasic0021")/*
																											 * @
																											 * res
																											 * "����Ϊ�գ�"
																											 */);
			if (getGroupRef().getRefPK() == null) {
				throw new Exception("Ո�x��һ��н�Y�Ŀ�ֽM!");
			}
			return true;

		} catch (Exception e) {
			handleException(e);
			showErrMsg(e.getMessage());
			return false;
		}
	}

	/**
	 * ���ڼ���Ŀ����ȡָ������µ�н�����ݣ���������ǰ����µ��ڼ䣬н�ʷ�����н����Ŀ��
	 * 
	 * 
	 * @return java.lang.String
	 */
	public String[] getPara() {
		String[] paras = new String[2];
		// �Ƿ���˰ 0�� 1��
		paras[0] = getYOrnCBox().getSelectdItemValue().toString();
		paras[1] = "\"" + getGroupRef().getRefPK() + "\"";
		return paras;
	}

	public UIButton getOkButton() {
		if (okButton == null) {
			okButton = new UIButton(ResHelper.getString("common", "UC001-0000044"));
			okButton.setBounds(48, 98, 80, 22);
		}
		return okButton;
	}

	public UIButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new UIButton(ResHelper.getString("common", "UC001-0000008"));
			cancelButton.setBounds(172, 98, 80, 22);
		}
		return cancelButton;
	}

}