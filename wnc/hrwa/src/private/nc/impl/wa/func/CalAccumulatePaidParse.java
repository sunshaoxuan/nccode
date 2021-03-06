package nc.impl.wa.func;

import nc.vo.hr.func.FunctionReplaceVO;
import nc.vo.pub.BusinessException;
import nc.vo.wa.paydata.IFormula;
import nc.vo.wa.pub.WaLoginContext;

/**
 * @since 2019-05-28
 * @author kk
 * @description 福委會累計給付總額
 * @description the sum of 本次的扣稅基數 of paid salary scheme which have paid
 *              betweent period
 *
 */
public class CalAccumulatePaidParse extends AbstractPreExcutorFormulaParse {
	private static final long serialVersionUID = 1L;

	public FunctionReplaceVO getReplaceStr(String formula) throws BusinessException {
		excute(formula, getContext());
		FunctionReplaceVO fvo = new FunctionReplaceVO();
		fvo.setAliTableName("wa_cacu_data");
		fvo.setReplaceStr(coalesce("wa_cacu_data.cacu_value"));
		return fvo;
	}

	@Override
	public void excute(Object formula, WaLoginContext context) throws BusinessException {
		IFormula excutor = new CalAccumulatePaidFormulaExecutor();
		excutor.excute(null, getContext());
	}

}
