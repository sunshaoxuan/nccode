package nc.ui.org.ref;
import java.util.List;

import nc.bs.IconResources;
import nc.hr.utils.InSQLCreator;
import nc.itf.org.IOrgResourceCodeConst;
import nc.pubitf.twhr.utils.LegalOrgUtilsEX;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.org.CorpVO;
import nc.vo.org.util.OrgTreeCellRendererIconPolicy;
import nc.vo.pub.BusinessException;

/**
 * 法人组织 树形参照
 * @version NC6.0
 * @author guoting
 */
public class LPOrgDefaultRefModel extends OrgBaseTreeDefaultRefModel { 


	public LPOrgDefaultRefModel() {
		super();
		reset();
	}

	public void reset() {
		setRefNodeName("法人组织");/*-=notranslate=-*/
		
		setFieldCode(new String[] { CorpVO.CODE, CorpVO.NAME});
		setFieldName(new String[] { 
				NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0003279") /* @res "编码" */,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001155") /* @res "名称" */
					});
		setHiddenFieldCode(new String[] {CorpVO.PK_CORP, CorpVO.PK_FATHERORG });
		setPkFieldCode(CorpVO.PK_CORP);
		setRefCodeField(CorpVO.CODE);
		setRefNameField(CorpVO.NAME);
		setFatherField(CorpVO.PK_FATHERORG);
		setChildField(CorpVO.PK_CORP);
		setTableName(CorpVO.getDefaultTableName());
		
		setOrderPart(CorpVO.CODE);
		
		setResourceID(IOrgResourceCodeConst.HRORG);
		
		//打开启用过滤条件开关
		setAddEnableStateWherePart(true);
		
		setFilterRefNodeName(new String[] {"集团"});/*-=notranslate=-*/
		
		resetFieldName();
		
    this.setTreeIconPolicy(new OrgTreeCellRendererIconPolicy(
        IconResources.ICON_Bu));
	}

	@Override
	public void filterValueChanged(ValueChangedEvent changedValue) {
		super.filterValueChanged(changedValue);
		String[] selectedPKs = (String[]) changedValue.getNewValue();
		if (selectedPKs != null && selectedPKs.length > 0) {
			setPk_group(selectedPKs[0]);
		}
	}

	
	
	
}
