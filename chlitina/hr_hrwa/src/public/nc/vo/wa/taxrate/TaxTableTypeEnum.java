/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.wa.taxrate;

import nc.md.model.impl.MDEnum;
import nc.md.model.IEnumValue;
	
/**
 * <b> �ڴ˴���Ҫ��������Ĺ��� </b>
 * <p>
 *     �ڴ˴����Ӵ����������Ϣ
 * </p>
 * ��������:
 * @author 
 * @version NCPrj ??
 */
public class TaxTableTypeEnum extends MDEnum{
	public TaxTableTypeEnum(IEnumValue enumvalue){
		super(enumvalue);
	}
	
public static final TaxTableTypeEnum TAXTABLE= MDEnum.valueOf(TaxTableTypeEnum.class, 0);
public static final TaxTableTypeEnum FIXTAX= MDEnum.valueOf(TaxTableTypeEnum.class, 1);
public static final TaxTableTypeEnum WORKTAX= MDEnum.valueOf(TaxTableTypeEnum.class, 2);

public static final TaxTableTypeEnum FOREIGNWORKTAX= MDEnum.valueOf(TaxTableTypeEnum.class, 3);//add by ward 20180112 �����⼮�T���۶�
	

} 