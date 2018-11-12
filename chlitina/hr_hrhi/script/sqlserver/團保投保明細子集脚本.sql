﻿delete from hr_infoset_item where pk_infoset = 'TWHRA21000000000DEF7' and meta_data is null;
delete from hr_infoset where pk_infoset = 'TWHRA21000000000DEF7' and meta_data is null;

INSERT INTO hr_infoset (dr, infoset_code, infoset_name, infoset_type, look_history_flag, main_table_flag, memo, meta_data, meta_data_id, pk_field_code, pk_group, pk_infoset, pk_infoset_sort, pk_org, record_character, resid, respath, showorder, sync_main_code, sync_main_table, table_code, ts, user_def_flag, vo_class_name, infoset_name2, infoset_name3, infoset_name4, infoset_name5, infoset_name6) VALUES (0, 'hi_psndoc_groupinsrecord', '员工团保投保明细', 1, null, 'N', null, null, null, 'pk_psndoc_sub', '~', 'TWHRA21000000000DEF7', '1001Z710000000002XPO', 'GLOBLE00000000000000', 1, 'hi_psndoc_groupinsrecord', '6007psn', 37, null, 'N', 'hi_psndoc_groupinsrecord', '2017-08-30 13:34:15', 'Y', 'nc.vo.hi.psndoc.GroupInsuranceRecordVO', null, null, null, null, null);

INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility, item_name2, item_name3, item_name4, item_name5, item_name6) VALUES (null, null, null, null, null, 'N', 0, null, null, null, null, 0, null, null, null, null, 'Y', null, null, null, 'pk_psndoc_sub', null, null, '人员子表主键', 20, null, null, null, null, null, null, 'N', '~', 'TWHRA21000000000DEF7', '1001ZZ10000000004VXQ', '~', 'GLOBLE00000000000000', null, 'N', 'N', null, '06007psn0133', '6007psn', 0, null, null, '2017-08-30 13:31:41', 'N', null, null, null, null, null, null);
INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility, item_name2, item_name3, item_name4, item_name5, item_name6) VALUES (null, null, null, null, null, 'N', 0, null, null, null, null, 0, null, null, null, null, 'Y', null, null, null, 'pk_psndoc', null, null, '人员档案主键', 20, null, null, null, null, null, null, 'Y', 'GLOBLE00000000000000', 'TWHRA21000000000DEF7', '1001ZZ10000000004VXR', '~', 'GLOBLE00000000000000', null, 'N', 'N', null, 'UC000-0000139', '6007psn', 1, null, null, '2017-08-30 13:31:41', 'N', null, null, null, null, null, null);
INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility, item_name2, item_name3, item_name4, item_name5, item_name6) VALUES (null, null, null, null, null, 'N', 20, null, null, null, null, 0, null, null, null, null, 'N', null, null, null, 'begindate', null, null, '开始日期', 10, null, null, null, null, null, null, 'Y', 'GLOBLE00000000000000', 'TWHRA21000000000DEF7', '1001ZZ10000000004VXS', '~', 'GLOBLE00000000000000', null, 'N', 'N', null, 'UC000-0001892', '6007psn', 2, null, null, '2017-08-30 13:31:41', 'N', null, null, null, null, null, null);
INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility, item_name2, item_name3, item_name4, item_name5, item_name6) VALUES (null, null, null, null, null, 'N', 20, null, null, null, null, 0, null, null, null, null, 'N', null, null, null, 'enddate', null, null, '结束日期', 10, null, null, null, null, null, null, 'Y', 'GLOBLE00000000000000', 'TWHRA21000000000DEF7', '1001ZZ10000000004VXT', '~', 'GLOBLE00000000000000', null, 'N', 'N', null, 'UC000-0003230', '6007psn', 3, null, null, '2017-08-30 13:31:41', 'N', null, null, null, null, null, null);
INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility, item_name2, item_name3, item_name4, item_name5, item_name6) VALUES (null, null, null, null, null, 'N', 1, null, null, null, null, 0, null, null, null, null, 'Y', null, null, null, 'recordnum', null, null, '记录序号', 8, null, null, null, null, null, null, 'Y', 'GLOBLE00000000000000', 'TWHRA21000000000DEF7', '1001ZZ10000000004VXU', '~', 'GLOBLE00000000000000', null, 'N', 'N', null, '06007psn0134', '6007psn', 4, null, null, '2017-08-30 13:31:41', 'N', null, null, null, null, null, null);
INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility, item_name2, item_name3, item_name4, item_name5, item_name6) VALUES (null, null, null, null, null, 'N', 4, null, null, null, null, 0, null, null, null, null, 'Y', null, null, null, 'lastflag', null, null, '最近记录标志', 1, null, null, null, null, null, null, 'Y', 'GLOBLE00000000000000', 'TWHRA21000000000DEF7', '1001ZZ10000000004VXV', '~', 'GLOBLE00000000000000', null, 'N', 'N', null, '06007psn0135', '6007psn', 5, null, null, '2017-08-30 13:31:41', 'N', null, null, null, null, null, null);
INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility, item_name2, item_name3, item_name4, item_name5, item_name6) VALUES (null, null, null, null, null, 'N', 5, null, null, null, null, 0, null, null, null, null, 'Y', null, null, null, 'creator', null, null, '创建人', 20, null, null, null, null, null, null, 'Y', 'GLOBLE00000000000000', 'TWHRA21000000000DEF7', '1001ZZ10000000004VXW', '~', 'GLOBLE00000000000000', null, 'Y', 'N', '0001Z01000000001BVPB', 'UC001-0000091', 'common', 100, null, null, '2017-08-30 13:31:41', 'N', null, null, null, null, null, null);
INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility, item_name2, item_name3, item_name4, item_name5, item_name6) VALUES (null, null, null, null, null, 'N', 15, null, null, null, null, 0, null, null, null, null, 'Y', null, null, null, 'creationtime', null, null, '创建时间', 19, null, null, null, null, null, null, 'Y', 'GLOBLE00000000000000', 'TWHRA21000000000DEF7', '1001ZZ10000000004VXX', '~', 'GLOBLE00000000000000', null, 'Y', 'N', null, 'UC001-0000092', 'common', 101, null, null, '2017-08-30 13:31:41', 'N', null, null, null, null, null, null);
INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility, item_name2, item_name3, item_name4, item_name5, item_name6) VALUES (null, null, null, null, null, 'N', 5, null, null, null, null, 0, null, null, null, null, 'Y', null, null, null, 'modifier', null, null, '修改人', 20, null, null, null, null, null, null, 'Y', 'GLOBLE00000000000000', 'TWHRA21000000000DEF7', '1001ZZ10000000004VXY', '~', 'GLOBLE00000000000000', null, 'Y', 'N', '0001Z01000000001BVPB', 'UC001-0000093', 'common', 102, null, null, '2017-08-30 13:31:41', 'N', null, null, null, null, null, null);
INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility, item_name2, item_name3, item_name4, item_name5, item_name6) VALUES (null, null, null, null, null, 'N', 15, null, null, null, null, 0, null, null, null, null, 'Y', null, null, null, 'modifiedtime', null, null, '修改时间', 19, null, null, null, null, null, null, 'Y', 'GLOBLE00000000000000', 'TWHRA21000000000DEF7', '1001ZZ10000000004VXZ', '~', 'GLOBLE00000000000000', null, 'Y', 'N', null, 'UC001-0000094', 'common', 103, null, null, '2017-08-30 13:31:41', 'N', null, null, null, null, null, null);
INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility, item_name2, item_name3, item_name4, item_name5, item_name6) VALUES (null, null, null, null, null, 'Y', 2, null, null, null, null, 0, null, null, null, null, 'N', null, null, null, 'stuffpay', null, null, '员工负担', 28, null, null, null, null, null, null, 'Y', 'GLOBLE00000000000000', 'TWHRA21000000000DEF7', '1001ZZ10000000004VY0', '~', 'GLOBLE00000000000000', 0, 'N', 'N', null, 'gidetail_6982', '6007psn', 104, null, null, '2017-08-30 13:34:15', 'N', null, null, null, null, null, null);
INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility, item_name2, item_name3, item_name4, item_name5, item_name6) VALUES (null, null, null, null, null, 'Y', 2, null, null, null, null, 0, null, null, null, null, 'N', null, null, null, 'companypay', null, null, '公司负担', 28, null, null, null, null, null, null, 'Y', 'GLOBLE00000000000000', 'TWHRA21000000000DEF7', '1001ZZ10000000004VY1', '~', 'GLOBLE00000000000000', 0, 'N', 'N', null, 'gidetail_6983', '6007psn', 105, null, null, '2017-08-30 13:34:15', 'N', null, null, null, null, null, null);
Insert into hr_infoset_item (accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-09-18 11:21:10','NC_USER0000000000000','Y',2,null,null,null,null,0,null,null,null,null,'N',null,null,null,'fayinsurancemoney',null,null,'眷属团保费','眷属团保费',null,null,null,null,28,null,null,null,null,null,null,'Y','GLOBLE00000000000000','TWHRA21000000000DEF7','1001ZZ1000000000N1HE','~','GLOBLE00000000000000',0,'N','Y',null,'hi_psndoc_glbdef14_70093','6007psn',106,null,null,'2018-09-18 11:21:10','N',null);
Insert into hr_infoset_item (accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-09-18 11:21:10','NC_USER0000000000000','Y',2,null,null,null,null,0,null,null,null,null,'N',null,null,null,'empinsurancemoney',null,null,'员工团保费','员工团保费',null,null,null,null,28,null,null,null,null,null,null,'Y','GLOBLE00000000000000','TWHRA21000000000DEF7','1001ZZ1000000000N1HF','~','GLOBLE00000000000000',0,'N','N',null,'gidetail_70095','6007psn',107,null,null,'2018-09-18 11:21:10','N',null);

