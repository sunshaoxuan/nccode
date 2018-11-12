--以户计算
alter table twhr_groupinsurancesetting add ishousehold char(1);

--眷属团保费
alter table twhr_groupinsurancesetting add familygroupinsurance char(1);



--HR_INFOSET表的删除语句
--delete  HR_INFOSET WHERE PK_INFOSET ='1001ZZ1000000000JZFY';
--HR_INFOSETnull



Insert into HR_INFOSET
(dr,infoset_code,infoset_name,infoset_name2,infoset_name3,infoset_name4,infoset_name5,infoset_name6,infoset_type,look_history_flag,main_table_flag,memo,meta_data,meta_data_id,pk_field_code,pk_group,pk_infoset,pk_infoset_sort,pk_org,record_character,resid,respath,showorder,sync_main_code,sync_main_table,table_code,ts,user_def_flag,vo_class_name)
Values(0,'groupinsurance_detail','员工与眷属團保投保明細',null,null,null,null,null,1,null,'N',null,null,null,'pk_psndoc_sub','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001Z710000000002XPO','GLOBLE00000000000000',1,'hi_psndoc_glbdef15','6007psn',45,null,'N','groupinsurance_detail','2018-09-19 17:44:32','Y','nc.vo.hi.psndoc.GroupInsuranceDetailVO');


--HR_INFOSET_ITEM表的删除语句
--delete   HR_INFOSET_ITEM WHERE PK_INFOSET_ITEM IN ('1001ZZ1000000000JZFZ','1001ZZ1000000000JZG0','1001ZZ1000000000JZG1', '1001ZZ1000000000JZG3','1001ZZ1000000000JZG2','1001ZZ1000000000JZG4','1001ZZ1000000000JZG5','1001ZZ1000000000JZG6','1001ZZ1000000000JZG7','1001ZZ1000000000JZG8');;
--HR_INFOSET_ITEMnull



Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,null,null,'N',0,null,null,null,null,0,null,null,null,null,'Y',null,null,null,'pk_psndoc_sub',null,null,'人员子表主键',null,null,null,null,null,20,null,'hrhi.groupinsurance_detail.pk_psndoc_sub',null,'2018-09-26 22:54:01','NC_USER0000000000000',null,'N','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001ZZ1000000000JZFZ','~','GLOBLE00000000000000',null,'N','N',null,'06007psn0133','6007psn',0,null,null,'2018-09-26 22:54:01','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,null,null,'N',0,null,null,null,null,0,null,null,null,null,'Y',null,null,null,'pk_psndoc',null,null,'人员档案主键',null,null,null,null,null,20,null,'hrhi.groupinsurance_detail.pk_psndoc',null,'2018-09-26 22:54:01','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001ZZ1000000000JZG0','~','GLOBLE00000000000000',null,'N','N',null,'UC000-0000139','6007psn',1,null,null,'2018-09-26 22:54:01','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,null,null,'N',20,null,null,null,null,0,null,null,null,null,'N',null,null,null,'begindate',null,null,'开始日期',null,null,null,null,null,10,null,'hrhi.groupinsurance_detail.begindate',null,'2018-09-26 22:54:01','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001ZZ1000000000JZG1','~','GLOBLE00000000000000',null,'N','N',null,'UC000-0001892','6007psn',2,null,null,'2018-09-26 22:54:01','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,null,null,'N',20,null,null,null,null,0,null,null,null,null,'N',null,null,null,'enddate',null,null,'结束日期',null,null,null,null,null,10,null,'hrhi.groupinsurance_detail.enddate',null,'2018-09-26 22:54:01','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001ZZ1000000000JZG2','~','GLOBLE00000000000000',null,'N','N',null,'UC000-0003230','6007psn',3,null,null,'2018-09-26 22:54:01','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,null,null,'N',1,null,null,null,null,0,null,null,null,null,'Y',null,null,null,'recordnum',null,null,'记录序号',null,null,null,null,null,8,null,'hrhi.groupinsurance_detail.recordnum',null,'2018-09-26 22:54:01','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001ZZ1000000000JZG3','~','GLOBLE00000000000000',null,'N','N',null,'06007psn0134','6007psn',4,null,null,'2018-09-26 22:54:01','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,null,null,'N',4,null,null,null,null,0,null,null,null,null,'Y',null,null,null,'lastflag',null,null,'最近记录标志',null,null,null,null,null,1,null,'hrhi.groupinsurance_detail.lastflag',null,'2018-09-26 22:54:01','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001ZZ1000000000JZG4','~','GLOBLE00000000000000',null,'N','N',null,'06007psn0135','6007psn',5,null,null,'2018-09-26 22:54:01','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,null,null,'N',5,null,null,null,null,0,null,null,null,null,'Y',null,null,null,'creator',null,null,'创建人',null,null,null,null,null,20,null,'hrhi.groupinsurance_detail.creator',null,'2018-09-26 22:54:01','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001ZZ1000000000JZG5','~','GLOBLE00000000000000',null,'Y','N','0001Z01000000001BVPB','UC001-0000091','common',100,null,null,'2018-09-26 22:54:01','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,null,null,'N',15,null,null,null,null,0,null,null,null,null,'Y',null,null,null,'creationtime',null,null,'创建时间',null,null,null,null,null,19,null,'hrhi.groupinsurance_detail.creationtime',null,'2018-09-26 22:54:01','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001ZZ1000000000JZG6','~','GLOBLE00000000000000',null,'Y','N',null,'UC001-0000092','common',101,null,null,'2018-09-26 22:54:01','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,null,null,'N',5,null,null,null,null,0,null,null,null,null,'Y',null,null,null,'modifier',null,null,'修改人',null,null,null,null,null,20,null,'hrhi.groupinsurance_detail.modifier',null,'2018-09-26 22:54:01','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001ZZ1000000000JZG7','~','GLOBLE00000000000000',null,'Y','N','0001Z01000000001BVPB','UC001-0000093','common',102,null,null,'2018-09-26 22:54:01','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,null,null,'N',15,null,null,null,null,0,null,null,null,null,'Y',null,null,null,'modifiedtime',null,null,'修改时间',null,null,null,null,null,19,null,'hrhi.groupinsurance_detail.modifiedtime',null,'2018-09-26 22:54:01','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001ZZ1000000000JZG8','~','GLOBLE00000000000000',null,'Y','N',null,'UC001-0000094','common',103,null,null,'2018-09-26 22:54:01','N',null);

--delete from hr_infoset_item where pk_infoset_item in ('1001ZZ1000000000N1S1','1001ZZ1000000000N1S2','1001ZZ1000000000N1S3','1001ZZ1000000000N1S4','1001ZZ1000000000N1S5');

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-09-18 11:50:41','NC_USER0000000000000','Y',0,null,null,null,null,0,null,null,null,null,'N',null,null,null,'insurancecode',null,null,'险种','险种',null,null,null,null,128,null,'hrhi.groupinsurance_detail.insurancecode',null,'2018-09-26 22:54:01','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001ZZ1000000000N1S1','~','GLOBLE00000000000000',null,'N','N',null,'hi_psndoc_glbdef15_40899','6007psn',104,null,null,'2018-09-26 22:54:01','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-09-18 11:50:41','NC_USER0000000000000','Y',0,null,null,null,null,0,null,null,null,null,'N',null,null,null,'identitycode',null,null,'身份','身份',null,null,null,null,128,null,'hrhi.groupinsurance_detail.identitycode',null,'2018-09-26 22:54:01','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001ZZ1000000000N1S2','~','GLOBLE00000000000000',null,'N','N',null,'hi_psndoc_glbdef15_40900','6007psn',105,null,null,'2018-09-26 22:54:01','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-09-18 11:50:41','NC_USER0000000000000','Y',4,null,null,null,null,0,null,null,null,null,'N',null,null,null,'ishousehold',null,null,'是否以户计算','是否以户计算',null,null,null,null,1,null,'hrhi.groupinsurance_detail.ishousehold',null,'2018-09-26 22:54:01','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001ZZ1000000000N1S3','~','GLOBLE00000000000000',null,'N','N',null,'hi_psndoc_glbdef15_40901','6007psn',106,null,null,'2018-09-26 22:54:01','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-09-18 11:50:41','NC_USER0000000000000','Y',0,null,null,null,null,0,null,null,null,null,'N',null,null,null,'name',null,null,'姓名','姓名',null,null,null,null,128,null,'hrhi.groupinsurance_detail.name',null,'2018-09-26 22:54:01','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001ZZ1000000000N1S4','~','GLOBLE00000000000000',null,'N','N',null,'hi_psndoc_glbdef15_40902','6007psn',107,null,null,'2018-09-26 22:54:01','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-09-18 11:50:41','NC_USER0000000000000','Y',2,null,null,null,null,0,null,null,null,null,'N',null,null,null,'insuranceamount',null,null,'金额','金额',null,null,null,null,28,null,'hrhi.groupinsurance_detail.insuranceamount',null,'2018-09-26 22:54:01','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1001ZZ1000000000JZFY','1001ZZ1000000000N1S5','~','GLOBLE00000000000000',0,'N','N',null,'hi_psndoc_glbdef15_40903','6007psn',108,null,null,'2018-09-26 22:54:01','N',null);





--HR_INFOSET_ITEM表的删除语句
--delete  HR_INFOSET_ITEM WHERE PK_INFOSET_ITEM IN ('1001ZZ1000000000N1HE','1001ZZ1000000000N1HF');;
--HR_INFOSET_ITEMnull



Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-09-18 11:21:10','NC_USER0000000000000','Y',2,null,null,null,null,0,null,null,null,null,'N',null,null,null,'fayinsurancemoney',null,null,'眷属团保费','眷属团保费',null,null,null,null,28,null,null,null,null,null,null,'Y','GLOBLE00000000000000','TWHRA21000000000DEF7','1001ZZ1000000000N1HE','~','GLOBLE00000000000000',0,'N','Y',null,'hi_psndoc_glbdef14_70093','6007psn',106,null,null,'2018-09-18 11:21:10','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-09-18 11:21:10','NC_USER0000000000000','Y',2,null,null,null,null,0,null,null,null,null,'N',null,null,null,'empinsurancemoney',null,null,'员工团保费','员工团保费',null,null,null,null,28,null,null,null,null,null,null,'Y','GLOBLE00000000000000','TWHRA21000000000DEF7','1001ZZ1000000000N1HF','~','GLOBLE00000000000000',0,'N','N',null,'hi_psndoc_glbdef14_70095','6007psn',107,null,null,'2018-09-18 11:21:10','N',null);



--不太确定的sql语句
UPDATE bd_defdoc set code='hi_psndoc_glbdef13' where pk_defdoc='TWHRA21TWDEF0000DEF6';



--BD_DEFDOC表的删除语句
delete  FROM BD_DEFDOC WHERE PK_DEFDOC='TWHRA21TWDEF0000N1RQ';
--BD_DEFDOCnull



Insert into BD_DEFDOC
(code,creationtime,creator,dataoriginflag,datatype,def1,def10,def11,def12,def13,def14,def15,def16,def17,def18,def19,def2,def20,def3,def4,def5,def6,def7,def8,def9,dr,enablestate,innercode,memo,mnecode,modifiedtime,modifier,name,name2,name3,name4,name5,name6,pid,pk_defdoc,pk_defdoclist,pk_group,pk_org,shortname,shortname2,shortname3,shortname4,shortname5,shortname6,ts)
Values('groupinsurance_detail',null,'NC_USER0000000000000',0,1,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,0,2,null,null,null,null,null,'员工与眷属团保投保明细',null,null,null,null,null,'~','TWHRA21TWDEF0000N1RQ','TWHRA210000000DEFSET','GLOBLE00000000000000','GLOBLE00000000000000',null,null,null,null,null,null,'2018-07-13 16:05:21');

