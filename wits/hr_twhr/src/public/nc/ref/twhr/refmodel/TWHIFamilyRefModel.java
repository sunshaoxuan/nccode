package nc.ref.twhr.refmodel;

import nc.ui.bd.ref.AbstractRefModel;

public class TWHIFamilyRefModel extends AbstractRefModel {
	private String pk_psndoc;

	public TWHIFamilyRefModel() {
		super();
		super.setOrderPart("name");
		super.setMutilLangNameRef(false);
	}

	public String[] getFieldCode() {
		return new String[] { "fm.mem_name name", "def.name relation",
				"fm.glbdef2 idnumber", "fm.mem_birthday birthday" };
	}

	public String[] getFieldName() {
		return new String[] { "姓名", "與本人關係", "身份證字號", "出生日期" };
	}

	public int getDefaultFieldCount() {
		return 4;
	}

	public java.lang.String[] getHiddenFieldCode() {
		return new String[] { "fm.pk_psndoc_sub code" };
	}

	public java.lang.String getPkFieldCode() {
		return "fm.pk_psndoc_sub code";
	}

	public java.lang.String getRefTitle() {
		return "眷屬";
	}

	public java.lang.String getRefSql() {
		String sql = "select name, '本人' relation, id idnumber, birthdate birthday, code from bd_psndoc where pk_psndoc = '"
				+ pk_psndoc + "' union all ";
		sql += super.getRefSql();
		return sql;
	}

	public java.lang.String getTableName() {
		return " hi_psndoc_family fm inner join bd_defdoc def on fm.mem_relation = def.pk_defdoc inner join bd_defdoclist lst on def.pk_defdoclist = lst.pk_defdoclist ";
	}

	public String getWherePart() {
		return " lst.code = 'HR024_0xx' and pk_psndoc = '" + pk_psndoc + "' ";
	}

	public String getPk_psndoc() {
		return pk_psndoc;
	}

	public void setPk_psndoc(String pk_psndoc) {
		this.pk_psndoc = pk_psndoc;
	}
}
