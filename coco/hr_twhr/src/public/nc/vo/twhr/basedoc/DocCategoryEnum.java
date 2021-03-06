package nc.vo.twhr.basedoc;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class DocCategoryEnum extends MDEnum {

	public DocCategoryEnum(IEnumValue enumValue) {
		super(enumValue);
	}

	/**
	 * 劳保劳退参数
	 */
	public static final DocCategoryEnum LABORREF = MDEnum.valueOf(
			DocCategoryEnum.class, Integer.valueOf(1));

	/**
	 * 健保参数
	 */
	public static final DocCategoryEnum NHIREF = MDEnum.valueOf(
			DocCategoryEnum.class, Integer.valueOf(2));

	/**
	 * 补充保险参数
	 */
	public static final DocCategoryEnum EXNHIREF = MDEnum.valueOf(
			DocCategoryEnum.class, Integer.valueOf(3));

	/**
	 * 薪资参数
	 */
	public static final DocCategoryEnum WAGEREF = MDEnum.valueOf(
			DocCategoryEnum.class, Integer.valueOf(4));
	/**
	 * 劳健保申报参数
	 */
	public static final DocCategoryEnum LHIREF = MDEnum.valueOf(
			DocCategoryEnum.class, Integer.valueOf(5));
	/**
	 * 所得税申报参数
	 */
	public static final DocCategoryEnum ITAXREF = MDEnum.valueOf(
			DocCategoryEnum.class, Integer.valueOf(6));
	
	/**
	 * 台灣本地化參數
	 */
	public static final DocCategoryEnum ITWLOCAL = MDEnum.valueOf(
			DocCategoryEnum.class, Integer.valueOf(7));
}
