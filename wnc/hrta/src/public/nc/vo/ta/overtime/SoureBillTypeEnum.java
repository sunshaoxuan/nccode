package nc.vo.ta.overtime;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * <b>此处简要描述此枚举的功能 </b>
 * <p>
 * 此处添加该枚举的描述信息
 * </p>
 * 创建日期:2019/3/7
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