package nc.vo.ta.overtime;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * <b>��̎��Ҫ������ö�e�Ĺ��� </b>
 * <p>
 * ��̎����ԓö�e��������Ϣ
 * </p>
 * ��������:2018/10/12
 * 
 * @author
 * @version NCPrj ??
 */
public class SoureBillTypeEnum extends MDEnum {
    public SoureBillTypeEnum(IEnumValue enumvalue) {
	super(enumvalue);
    }

    public static final SoureBillTypeEnum OVERTIMEREG = MDEnum.valueOf(SoureBillTypeEnum.class, Integer.valueOf(1));

    public static final SoureBillTypeEnum EXTRALEAVE = MDEnum.valueOf(SoureBillTypeEnum.class, Integer.valueOf(2));

}