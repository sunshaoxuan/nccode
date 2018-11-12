﻿ALTER TABLE wa_item ADD avgcalcsalflag nchar (1) DEFAULT 'N';

--BD_MODE_SELECTED表的删除语句
delete  BD_MODE_SELECTED WHERE PK_BDMODE ='1001ZZ1000000000KGWK';
--BD_MODE_SELECTEDnull



Insert into BD_MODE_SELECTED
(comp,dataoriginflag,dr,managemode,mdclassid,pk_bdmode,ts,uniquescope,visiblescope)
Values(null,0,0,1,'1001ZZ1000000000KGVY','1001ZZ1000000000KGWK','2018-08-15 13:07:57',1,1);



--BD_DEFDOCLIST表的删除语句
delete  BD_DEFDOCLIST WHERE PK_DEFDOCLIST ='1001ZZ1000000000KGVY';
--BD_DEFDOCLISTnull



Insert into BD_DEFDOCLIST
(associatename,bpfcomponentid,code,codectlgrade,coderule,comp,componentid,creationtime,creator,dataoriginflag,docclass,doclevel,doctype,dr,funcode,isgrade,isrelease,mngctlmode,modifiedtime,modifier,name,name2,name3,name4,name5,name6,pk_defdoclist,pk_group,pk_org,ts)
Values('bd_defdoc','2b6869e5-a049-11e8-839d-79b29e477abc','avgmoncount',null,null,null,'2a8adbf9-a049-11e8-839d-79b29e477abc','2018-08-15 13:07:53','NC_USER0000000000000',0,null,0,null,0,null,'N','N',0,'2018-08-15 15:34:02','NC_USER0000000000000','平均月数','平均月数',null,null,null,null,'1001ZZ1000000000KGVY','~','GLOBLE00000000000000','2018-08-15 15:34:03');


--BD_REFINFO表的删除语句
delete  BD_REFINFO WHERE PK_REFINFO='1001ZZ1000000000KGVZ';
--BD_REFINFOnull



Insert into BD_REFINFO
(code,dr,isneedpara,isspecialref,layer,metadatanamespace,metadatatypename,modulename,name,para1,para2,para3,pk_country,pk_industry,pk_refinfo,refclass,refsystem,reftype,reserv1,reserv2,reserv3,resid,residpath,ts,wherepart)
Values('D10093',0,'Y',null,-1,null,'Defdoc-avgmoncount','uap','平均月数(自定义档案)','1001ZZ1000000000KGVY','bd_defdoc',null,null,null,'1001ZZ1000000000KGVZ','nc.ui.bd.ref.model.DefdocGridRefModel',null,0,null,null,'avgmoncount','平均月数','10140uddb','2018-08-15 15:34:02',null);



--BD_DEFDOC表的删除语句
delete  BD_DEFDOC WHERE PK_DEFDOC IN ('1001ZZ1000000000KHO7','1001ZZ1000000000KHO8','1001ZZ1000000000KHO9','1001ZZ1000000000KHOA');
--BD_DEFDOCnull



Insert into BD_DEFDOC
(code,creationtime,creator,dataoriginflag,datatype,def1,def10,def11,def12,def13,def14,def15,def16,def17,def18,def19,def2,def20,def3,def4,def5,def6,def7,def8,def9,dr,enablestate,innercode,memo,mnecode,modifiedtime,modifier,name,name2,name3,name4,name5,name6,pid,pk_defdoc,pk_defdoclist,pk_group,pk_org,shortname,shortname2,shortname3,shortname4,shortname5,shortname6,ts)
Values('3','2018-08-15 14:37:22','NC_USER0000000000000',0,1,'~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~',0,2,null,null,null,'2018-08-15 15:06:11','NC_USER0000000000000','3个月','3个月',null,null,null,null,'~','1001ZZ1000000000KHO7','1001ZZ1000000000KGVY','GLOBLE00000000000000','GLOBLE00000000000000',null,null,null,null,null,null,'2018-08-15 15:06:11');

Insert into BD_DEFDOC
(code,creationtime,creator,dataoriginflag,datatype,def1,def10,def11,def12,def13,def14,def15,def16,def17,def18,def19,def2,def20,def3,def4,def5,def6,def7,def8,def9,dr,enablestate,innercode,memo,mnecode,modifiedtime,modifier,name,name2,name3,name4,name5,name6,pid,pk_defdoc,pk_defdoclist,pk_group,pk_org,shortname,shortname2,shortname3,shortname4,shortname5,shortname6,ts)
Values('6','2018-08-15 14:37:22','NC_USER0000000000000',0,1,'~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~',0,2,null,null,null,'2018-08-15 15:06:11','NC_USER0000000000000','6个月','6个月',null,null,null,null,'~','1001ZZ1000000000KHO8','1001ZZ1000000000KGVY','GLOBLE00000000000000','GLOBLE00000000000000',null,null,null,null,null,null,'2018-08-15 15:06:11');

Insert into BD_DEFDOC
(code,creationtime,creator,dataoriginflag,datatype,def1,def10,def11,def12,def13,def14,def15,def16,def17,def18,def19,def2,def20,def3,def4,def5,def6,def7,def8,def9,dr,enablestate,innercode,memo,mnecode,modifiedtime,modifier,name,name2,name3,name4,name5,name6,pid,pk_defdoc,pk_defdoclist,pk_group,pk_org,shortname,shortname2,shortname3,shortname4,shortname5,shortname6,ts)
Values('9','2018-08-15 14:37:22','NC_USER0000000000000',0,1,'~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~',0,2,null,null,null,'2018-08-15 15:06:11','NC_USER0000000000000','9个月','9个月',null,null,null,null,'~','1001ZZ1000000000KHO9','1001ZZ1000000000KGVY','GLOBLE00000000000000','GLOBLE00000000000000',null,null,null,null,null,null,'2018-08-15 15:06:11');

Insert into BD_DEFDOC
(code,creationtime,creator,dataoriginflag,datatype,def1,def10,def11,def12,def13,def14,def15,def16,def17,def18,def19,def2,def20,def3,def4,def5,def6,def7,def8,def9,dr,enablestate,innercode,memo,mnecode,modifiedtime,modifier,name,name2,name3,name4,name5,name6,pid,pk_defdoc,pk_defdoclist,pk_group,pk_org,shortname,shortname2,shortname3,shortname4,shortname5,shortname6,ts)
Values('12','2018-08-15 14:37:22','NC_USER0000000000000',0,1,'~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~',0,2,null,null,null,'2018-08-15 15:06:11','NC_USER0000000000000','12个月','12个月',null,null,null,null,'~','1001ZZ1000000000KHOA','1001ZZ1000000000KGVY','GLOBLE00000000000000','GLOBLE00000000000000',null,null,null,null,null,null,'2018-08-15 15:06:11');





--BD_DEFDOC表的删除语句
delete  BD_DEFDOC WHERE PK_DEFDOC IN ('1001ZZ100000000019J0','1001ZZ100000000019J1','1001ZZ100000000019J2','1001ZZ100000000019J7','1001ZZ100000000019J5','1001ZZ100000000019J6','1001ZZ100000000019J3','1001ZZ100000000019J3');
--BD_DEFDOCnull



Insert into BD_DEFDOC
(CODE,CREATIONTIME,CREATOR,DATAORIGINFLAG,DATATYPE,DEF1,DEF10,DEF11,DEF12,DEF13,DEF14,DEF15,DEF16,DEF17,DEF18,DEF19,DEF2,DEF20,DEF3,DEF4,DEF5,DEF6,DEF7,DEF8,DEF9,DR,ENABLESTATE,INNERCODE,MEMO,MNECODE,MODIFIEDTIME,MODIFIER,NAME,NAME2,NAME3,NAME4,NAME5,NAME6,PID,PK_DEFDOC,PK_DEFDOCLIST,PK_GROUP,PK_ORG,SHORTNAME,SHORTNAME2,SHORTNAME3,SHORTNAME4,SHORTNAME5,SHORTNAME6,TS)
Values('1','2018-09-28 21:04:41','1001A1100000000002W7',0,1,'~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~',0,2,null,null,null,null,null,'1个月',null,null,null,null,null,'~','1001ZZ100000000019J0','1001ZZ1000000000KGVY','0001A110000000000434','GLOBLE00000000000000',null,null,null,null,null,null,'2018-09-28 21:04:41');

Insert into BD_DEFDOC
(CODE,CREATIONTIME,CREATOR,DATAORIGINFLAG,DATATYPE,DEF1,DEF10,DEF11,DEF12,DEF13,DEF14,DEF15,DEF16,DEF17,DEF18,DEF19,DEF2,DEF20,DEF3,DEF4,DEF5,DEF6,DEF7,DEF8,DEF9,DR,ENABLESTATE,INNERCODE,MEMO,MNECODE,MODIFIEDTIME,MODIFIER,NAME,NAME2,NAME3,NAME4,NAME5,NAME6,PID,PK_DEFDOC,PK_DEFDOCLIST,PK_GROUP,PK_ORG,SHORTNAME,SHORTNAME2,SHORTNAME3,SHORTNAME4,SHORTNAME5,SHORTNAME6,TS)
Values('2','2018-09-28 21:04:41','1001A1100000000002W7',0,1,'~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~',0,2,null,null,null,null,null,'2个月',null,null,null,null,null,'~','1001ZZ100000000019J1','1001ZZ1000000000KGVY','0001A110000000000434','GLOBLE00000000000000',null,null,null,null,null,null,'2018-09-28 21:04:41');

Insert into BD_DEFDOC
(CODE,CREATIONTIME,CREATOR,DATAORIGINFLAG,DATATYPE,DEF1,DEF10,DEF11,DEF12,DEF13,DEF14,DEF15,DEF16,DEF17,DEF18,DEF19,DEF2,DEF20,DEF3,DEF4,DEF5,DEF6,DEF7,DEF8,DEF9,DR,ENABLESTATE,INNERCODE,MEMO,MNECODE,MODIFIEDTIME,MODIFIER,NAME,NAME2,NAME3,NAME4,NAME5,NAME6,PID,PK_DEFDOC,PK_DEFDOCLIST,PK_GROUP,PK_ORG,SHORTNAME,SHORTNAME2,SHORTNAME3,SHORTNAME4,SHORTNAME5,SHORTNAME6,TS)
Values('4','2018-09-28 21:04:41','1001A1100000000002W7',0,1,'~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~',0,2,null,null,null,null,null,'4个月',null,null,null,null,null,'~','1001ZZ100000000019J2','1001ZZ1000000000KGVY','0001A110000000000434','GLOBLE00000000000000',null,null,null,null,null,null,'2018-09-28 21:04:41');

Insert into BD_DEFDOC
(CODE,CREATIONTIME,CREATOR,DATAORIGINFLAG,DATATYPE,DEF1,DEF10,DEF11,DEF12,DEF13,DEF14,DEF15,DEF16,DEF17,DEF18,DEF19,DEF2,DEF20,DEF3,DEF4,DEF5,DEF6,DEF7,DEF8,DEF9,DR,ENABLESTATE,INNERCODE,MEMO,MNECODE,MODIFIEDTIME,MODIFIER,NAME,NAME2,NAME3,NAME4,NAME5,NAME6,PID,PK_DEFDOC,PK_DEFDOCLIST,PK_GROUP,PK_ORG,SHORTNAME,SHORTNAME2,SHORTNAME3,SHORTNAME4,SHORTNAME5,SHORTNAME6,TS)
Values('5','2018-09-28 21:04:41','1001A1100000000002W7',0,1,'~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~',0,2,null,null,null,null,null,'5个月',null,null,null,null,null,'~','1001ZZ100000000019J3','1001ZZ1000000000KGVY','0001A110000000000434','GLOBLE00000000000000',null,null,null,null,null,null,'2018-09-28 21:04:41');

Insert into BD_DEFDOC
(CODE,CREATIONTIME,CREATOR,DATAORIGINFLAG,DATATYPE,DEF1,DEF10,DEF11,DEF12,DEF13,DEF14,DEF15,DEF16,DEF17,DEF18,DEF19,DEF2,DEF20,DEF3,DEF4,DEF5,DEF6,DEF7,DEF8,DEF9,DR,ENABLESTATE,INNERCODE,MEMO,MNECODE,MODIFIEDTIME,MODIFIER,NAME,NAME2,NAME3,NAME4,NAME5,NAME6,PID,PK_DEFDOC,PK_DEFDOCLIST,PK_GROUP,PK_ORG,SHORTNAME,SHORTNAME2,SHORTNAME3,SHORTNAME4,SHORTNAME5,SHORTNAME6,TS)
Values('8','2018-09-28 21:04:41','1001A1100000000002W7',0,1,'~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~',0,2,null,null,null,null,null,'8个月',null,null,null,null,null,'~','1001ZZ100000000019J5','1001ZZ1000000000KGVY','0001A110000000000434','GLOBLE00000000000000',null,null,null,null,null,null,'2018-09-28 21:04:41');

Insert into BD_DEFDOC
(CODE,CREATIONTIME,CREATOR,DATAORIGINFLAG,DATATYPE,DEF1,DEF10,DEF11,DEF12,DEF13,DEF14,DEF15,DEF16,DEF17,DEF18,DEF19,DEF2,DEF20,DEF3,DEF4,DEF5,DEF6,DEF7,DEF8,DEF9,DR,ENABLESTATE,INNERCODE,MEMO,MNECODE,MODIFIEDTIME,MODIFIER,NAME,NAME2,NAME3,NAME4,NAME5,NAME6,PID,PK_DEFDOC,PK_DEFDOCLIST,PK_GROUP,PK_ORG,SHORTNAME,SHORTNAME2,SHORTNAME3,SHORTNAME4,SHORTNAME5,SHORTNAME6,TS)
Values('10','2018-09-28 21:04:41','1001A1100000000002W7',0,1,'~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~',0,2,null,null,null,null,null,'10个月',null,null,null,null,null,'~','1001ZZ100000000019J6','1001ZZ1000000000KGVY','0001A110000000000434','GLOBLE00000000000000',null,null,null,null,null,null,'2018-09-28 21:04:41');

Insert into BD_DEFDOC
(CODE,CREATIONTIME,CREATOR,DATAORIGINFLAG,DATATYPE,DEF1,DEF10,DEF11,DEF12,DEF13,DEF14,DEF15,DEF16,DEF17,DEF18,DEF19,DEF2,DEF20,DEF3,DEF4,DEF5,DEF6,DEF7,DEF8,DEF9,DR,ENABLESTATE,INNERCODE,MEMO,MNECODE,MODIFIEDTIME,MODIFIER,NAME,NAME2,NAME3,NAME4,NAME5,NAME6,PID,PK_DEFDOC,PK_DEFDOCLIST,PK_GROUP,PK_ORG,SHORTNAME,SHORTNAME2,SHORTNAME3,SHORTNAME4,SHORTNAME5,SHORTNAME6,TS)
Values('11','2018-09-28 21:04:41','1001A1100000000002W7',0,1,'~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~','~',0,2,null,null,null,null,null,'11个月',null,null,null,null,null,'~','1001ZZ100000000019J7','1001ZZ1000000000KGVY','0001A110000000000434','GLOBLE00000000000000',null,null,null,null,null,null,'2018-09-28 21:04:41');


--HR_INFOSET_ITEM表的删除语句
--delete  HR_INFOSET_ITEM WHERE PK_INFOSET_ITEM IN ('1001ZZ1000000000KURZ','1001ZZ1000000000KUS0','1001ZZ1000000000KUS1','1001ZZ1000000000KUS2','1001ZZ1000000000KU07');
--HR_INFOSET_ITEMnull



Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-08-22 15:39:47','NC_USER0000000000000','Y',3,null,null,null,null,0,null,null,null,null,'N','22d28c96-01c5-4283-8f4f-9d343fd2e162',null,null,'sepaydate',null,null,N'资遣日期',N'资遣日期',null,null,null,null,19,null,'hrhi.bd_psndoc.sepaydate',null,'2018-08-27 14:01:13','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1002Z7100000000046GP','1001ZZ1000000000KU07','~','GLOBLE00000000000000',null,'N','N',null,'bd_psndoc_86421','6007psn',113,null,null,'2018-08-27 14:01:13','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-08-23 09:39:24','NC_USER0000000000000','Y',4,null,null,null,null,0,null,null,null,null,'N','7be3a328-d33e-4049-ae48-fcfb702e9f7b',null,null,'isoldemply',null,null,N'是否旧制员工',N'是否旧制员工',null,null,null,null,1,null,'hrhi.bd_psndoc.isoldemply',null,'2018-08-27 14:01:13','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1002Z7100000000046GP','1001ZZ1000000000KURZ','~','GLOBLE00000000000000',null,'N','N',null,'bd_psndoc_64380','6007psn',114,null,null,'2018-08-27 14:01:13','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-08-23 09:39:24','NC_USER0000000000000','Y',101,null,null,null,null,0,null,null,null,null,'N','f8ab655d-5ac1-4448-a109-feda3e05328c',null,null,'oldsysbegindate',null,null,N'旧制起始日期',N'旧制起始日期',null,null,null,null,19,null,'hrhi.bd_psndoc.oldsysbegindate',null,'2018-08-27 14:01:13','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1002Z7100000000046GP','1001ZZ1000000000KUS0','~','GLOBLE00000000000000',null,'N','N',null,'bd_psndoc_64381','6007psn',115,null,null,'2018-08-27 14:01:13','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-08-23 09:39:24','NC_USER0000000000000','Y',102,null,null,null,null,0,null,null,null,null,'N','31bf7e34-6c3c-4134-aaf0-493fac5838f1',null,null,'oldsysenddate',null,null,N'旧制结束日期',N'旧制结束日期',null,null,null,null,19,null,'hrhi.bd_psndoc.oldsysenddate',null,'2018-08-27 14:01:13','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1002Z7100000000046GP','1001ZZ1000000000KUS1','~','GLOBLE00000000000000',null,'N','N',null,'bd_psndoc_64382','6007psn',116,null,null,'2018-08-27 14:01:13','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-08-23 09:39:24','NC_USER0000000000000','Y',3,null,null,null,null,0,null,null,null,null,'N','6a5053d1-3c78-46cd-9a63-f96eaf75d712',null,null,'retirementdate',null,null,N'退休日期',N'退休日期',null,null,null,null,19,null,'hrhi.bd_psndoc.retirementdate',null,'2018-08-27 14:01:13','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1002Z7100000000046GP','1001ZZ1000000000KUS2','~','GLOBLE00000000000000',null,'N','N',null,'bd_psndoc_64383','6007psn',117,null,null,'2018-08-27 14:01:13','N',null);
go


--HR_INFOSET_ITEM表的删除语句
	--delete  HR_INFOSET_ITEM WHERE PK_INFOSET_ITEM ='1001ZZ1000000000L9FT';
--HR_INFOSET_ITEMnull



Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-09-21 17:48:19','NC_USER0000000000000','Y',4,null,null,null,null,0,null,null,null,null,'N',null,null,null,'ismonsalary',null,null,'是否月薪',null,null,null,null,null,1,null,null,null,null,null,null,'Y','GLOBLE00000000000000','GLOBLE00000000000000','1001ZZ1000000000L9FT','~','GLOBLE00000000000000',null,'N','N',null,'hi_psnjob_99149','6007psn',56,null,null,'2018-09-21 17:48:21','N',null);

--修正劳退开始结束日期类型
UPDATE HR_INFOSET_ITEM SET DATA_TYPE = 20, MAX_LENGTH = 10 WHERE PK_INFOSET = '1001ZZ10000000001PQV' AND ITEM_CODE = 'glbdef14';
UPDATE HR_INFOSET_ITEM SET DATA_TYPE = 20, MAX_LENGTH = 10 WHERE PK_INFOSET = '1001ZZ10000000001PQV' AND ITEM_CODE = 'glbdef15';
UPDATE MD_PROPERTY SET ATTRLENGTH = 10, DATATYPE = 'BS000010000100001039' where classid = (select id from md_class where defaulttablename = 'hi_psndoc_glbdef1') and name in ('glbdef14', 'glbdef15');
--家庭信息增加字段
INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, item_name2, item_name3, item_name4, item_name5, item_name6, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility) VALUES (null, null, null, '2017-07-18 17:42:32', null, 'Y', 4, null, null, null, null, 0, null, null, null, null, 'N', null, null, null, 'isnhifeed', null, null, '是否所得税抚养人', '是否所得稅撫養人', null, null, null, null, 1, null, null, null, '2018-04-12 09:15:59', null, null, 'Y', (select top 1 pk_group from org_group), '1002Z710000000006ZLI', '1001ZZ10000000000NX7', '~', 'GLOBLE00000000000000', null, 'N', 'N', null, 'hi_psndoc_family_51991', '6007psn', 28, null, null, '2018-04-12 09:15:59', 'N', null);
INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, item_name2, item_name3, item_name4, item_name5, item_name6, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility) VALUES (null, null, null, '2017-09-13 15:40:32', null, 'Y', 0, null, null, null, null, 0, null, null, null, null, 'N', null, null, null, 'idnumber', null, null, '身份证号码', '身份證號碼', null, null, null, null, 128, null, null, null, '2018-04-12 09:15:59', null, null, 'Y', (select top 1 pk_group from org_group), '1002Z710000000006ZLI', '1001A110000000001NJY', '~', 'GLOBLE00000000000000', null, 'N', 'N', null, 'hi_psndoc_family_32110', '6007psn', 29, null, null, '2018-04-12 09:15:59', 'N', null);

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

go

delete from hr_infoset_item where pk_infoset = 'TWHRA21000000000DEF7' and meta_data is null;
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

go
INSERT INTO HR_INFOSET_ITEM (ACCESSOR_CLASSNAME, CALCULATION, CLASS_ID, CREATIONTIME, CREATOR, CUSTOM_ATTR, DATA_TYPE, DATA_TYPE_ID, DATA_TYPE_STYLE, DEFAULT_VALUE, DESCRIPTION, DR, DYNAMIC_FLAG, ENUM_ID, FIXED_LENGTH, HELP, HIDED, ID, IS_ACTIVE, IS_AUTHEN, ITEM_CODE, ITEM_FORMULA, ITEM_FORMULA_SQL, ITEM_NAME, ITEM_NAME2, ITEM_NAME3, ITEM_NAME4, ITEM_NAME5, ITEM_NAME6, MAX_LENGTH, MAX_VALUE, META_DATA, MIN_VALUE, MODIFIEDTIME, MODIFIER, NOT_SERIALIZE, NULLABLE, PK_GROUP, PK_INFOSET, PK_INFOSET_ITEM, PK_MAIN_ITEM, PK_ORG, PRECISE, READ_ONLY, REF_LEAF_FLAG, REF_MODEL_NAME, RESID, RESPATH, SHOWORDER, SUB_FORMULA, SUB_FORMULA_SQL, TS, UNIQUE_FLAG, VISIBILITY) VALUES (null, null, null, '2018-10-08 12:51:47', '1001A110000000000NYW', 'Y', 5, null, null, null, null, 0, null, null, null, null, 'N', null, null, null, 'pk_hrorg', null, null, '人力资源组织', null, null, null, null, null, 20, null, null, null, null, null, null, 'N', (select top 1 pk_group from org_group), '1001ZZ10000000002U2R', '1001ZZ10000000006HSS', '~', 'GLOBLE00000000000000', null, 'Y', 'N', '0001Z01000000001BVPV', 'hi_psndoc_glbdef4_07560', '6007psn', 130, null, null, '2018-10-08 12:51:48', 'N', null);
go
--HR_INFOSET_ITEM表的删除语句
--delete  HR_INFOSET_ITEM WHERE PK_INFOSET_ITEM='1001ZZ10000000003011';
--HR_INFOSET_ITEMnull



Insert into HR_INFOSET_ITEM
(ACCESSOR_CLASSNAME,CALCULATION,CLASS_ID,CREATIONTIME,CREATOR,CUSTOM_ATTR,DATA_TYPE,DATA_TYPE_ID,DATA_TYPE_STYLE,DEFAULT_VALUE,DESCRIPTION,DR,DYNAMIC_FLAG,ENUM_ID,FIXED_LENGTH,HELP,HIDED,ID,IS_ACTIVE,IS_AUTHEN,ITEM_CODE,ITEM_FORMULA,ITEM_FORMULA_SQL,ITEM_NAME,ITEM_NAME2,ITEM_NAME3,ITEM_NAME4,ITEM_NAME5,ITEM_NAME6,MAX_LENGTH,MAX_VALUE,META_DATA,MIN_VALUE,MODIFIEDTIME,MODIFIER,NOT_SERIALIZE,NULLABLE,PK_GROUP,PK_INFOSET,PK_INFOSET_ITEM,PK_MAIN_ITEM,PK_ORG,PRECISE,READ_ONLY,REF_LEAF_FLAG,REF_MODEL_NAME,RESID,RESPATH,SHOWORDER,SUB_FORMULA,SUB_FORMULA_SQL,TS,UNIQUE_FLAG,VISIBILITY)
Values(null,null,null,'2018-10-01 12:30:53','1001A1100000000002W7','Y',6,null,null,null,null,0,null,'0739ecf4-2b69-4f16-81d1-8f0bfc258c19',null,null,'N',null,null,null,'overtimecontrol',null,null,'加班控管',null,null,null,null,null,50,null,'hrjf.hrdept.overtimecontrol',null,'2018-10-01 14:52:59','1001A1100000000002W7',null,'Y','0001A110000000000434','1002Z71000000001K3AH','1001ZZ10000000003011','~','GLOBLE00000000000000',null,'N','N',null,'hrdept_53026','6005dept',61,null,null,'2018-10-01 14:53:01','N',null);
go
UPDATE MD_PROPERTY SET ATTRLENGTH = 10, DATATYPE = 'BS000010000100001039' where classid = (select id from md_class where defaulttablename = 'hi_psndoc_glbdef1') and name in ('glbdef14', 'glbdef15');

go

DELETE FROM md_enumvalue WHERE id = '131dfa10-8654-44ca-b735-2dc6c4b47028';
DELETE FROM md_class WHERE id = '131dfa10-8654-44ca-b735-2dc6c4b47028';
DELETE FROM md_enumvalue WHERE id = '1feb5b3e-7ee6-44dc-b3ad-a89fa20afe3c';
DELETE FROM md_class WHERE id = '1feb5b3e-7ee6-44dc-b3ad-a89fa20afe3c';
INSERT INTO md_class (accessorclassname, bizitfimpclassname, classtype, componentid, createtime, creator, defaulttablename, description, displayname, dr, fixedlength, fullclassname, help, id, industry, isactive, isauthen, iscreatesql, isextendbean, isprimary, keyattribute, modifier, modifytime, modinfoclassname, name, parentclassid, precise, refmodelname, resid, returntype, stereotype, ts, userdefclassname, versiontype) VALUES (null, null, 203, '4520fac6-9e19-4726-b64a-13eb99836686', null, null, 'otchkscope', null, '加班上限统计范围枚舉', null, null, 'nc.vo.ta.timerule.OvertimeCheckScopeEnum', null, '1feb5b3e-7ee6-44dc-b3ad-a89fa20afe3c', '0', null, null, null, null, 'N', null, null, null, null, 'otchkscope', null, null, null, null, 'BS000010000100001004', null, '2018-06-06 23:12:52', null, 0);

INSERT INTO md_enumvalue (description, dr, enumsequence, hidden, id, industry, name, resid, ts, value, versiontype) VALUES (null, 0, 0, 'N', '1feb5b3e-7ee6-44dc-b3ad-a89fa20afe3c', '0', '一個月', null, '2018-06-06 00:00:00', '1', 0);
INSERT INTO md_enumvalue (description, dr, enumsequence, hidden, id, industry, name, resid, ts, value, versiontype) VALUES (null, 0, 1, 'N', '1feb5b3e-7ee6-44dc-b3ad-a89fa20afe3c', '0', '三個月', null, '2018-06-06 00:00:00', '2', 0);

UPDATE HR_INFOSET_ITEM SET ENUM_ID = '1feb5b3e-7ee6-44dc-b3ad-a89fa20afe3c' WHERE ITEM_CODE = 'otchkscope';
update md_property set datatype = '1feb5b3e-7ee6-44dc-b3ad-a89fa20afe3c' where name = 'otchkscope';
go


--HR_INFOSET_ITEM表的删除语句
--delete  HR_INFOSET_ITEM WHERE  PK_INFOSET_ITEM='1001X1100000DVOHOLEA';
--HR_INFOSET_ITEMnull

go
Insert into HR_INFOSET_ITEM
(ACCESSOR_CLASSNAME,CALCULATION,CLASS_ID,CREATIONTIME,CREATOR,CUSTOM_ATTR,DATA_TYPE,DATA_TYPE_ID,DATA_TYPE_STYLE,DEFAULT_VALUE,DESCRIPTION,DR,DYNAMIC_FLAG,ENUM_ID,FIXED_LENGTH,HELP,HIDED,ID,IS_ACTIVE,IS_AUTHEN,ITEM_CODE,ITEM_FORMULA,ITEM_FORMULA_SQL,ITEM_NAME,ITEM_NAME2,ITEM_NAME3,ITEM_NAME4,ITEM_NAME5,ITEM_NAME6,MAX_LENGTH,MAX_VALUE,META_DATA,MIN_VALUE,MODIFIEDTIME,MODIFIER,NOT_SERIALIZE,NULLABLE,PK_GROUP,PK_INFOSET,PK_INFOSET_ITEM,PK_MAIN_ITEM,PK_ORG,PRECISE,READ_ONLY,REF_LEAF_FLAG,REF_MODEL_NAME,RESID,RESPATH,SHOWORDER,SUB_FORMULA,SUB_FORMULA_SQL,TS,UNIQUE_FLAG,VISIBILITY)
Values(null,null,null,null,null,'Y',6,null,null,null,null,0,null,'5b0a8d05-b881-4f55-b3c1-3d7a5e579ba4',null,null,'N','8885cdb9-4abe-4d2c-a651-e042e14b401f',null,null,'weekform',null,null,'工时类型','工時類型',null,null,null,null,50,null,'hrjf.hrdept.weekform',null,'2018-10-01 14:52:59','1001A1100000000002W7',null,'Y','GLOBLE00000000000000','1002Z71000000001K3AH','1001X1100000DVOHOLEA','~','GLOBLE00000000000000',0,'N','N',null,'bd_psndoc_99013','6007psn',60,null,null,'2018-10-01 14:53:01','N',null);
go

DELETE FROM bd_defdoc WHERE pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'TWHR000');
INSERT INTO bd_defdoc (code, creationtime, creator, dataoriginflag, datatype, dr, enablestate, innercode, memo, mnecode, modifiedtime, modifier, name, name2, name3, name4, name5, name6, pid, pk_defdoc, pk_defdoclist, pk_group, pk_org, shortname, shortname2, shortname3, shortname4, shortname5, shortname6) 
SELECT infoset_code, null, 'NC_USER0000000000000', 0, 1, 0, 2, null, null, null, null, null, infoset_name, null, null, null, null, null, '~', replace(pk_infoset, '00000', 'TWDEF'), 'TWHRA210000000DEFSET', (select top 1 pk_group from org_group ), (select top 1 pk_group from org_group ), null, null, null, null, null, null
FROM hr_infoset WHERE infoset_code like 'hi_psndoc_glbdef%' and dr=0;
go
INSERT INTO bd_defdoc (code, creationtime, creator, dataoriginflag, datatype, dr, enablestate, innercode, memo, mnecode, modifiedtime, modifier, name, name2, name3, name4, name5, name6, pid, pk_defdoc, pk_defdoclist, pk_group, pk_org, shortname, shortname2, shortname3, shortname4, shortname5, shortname6) 
SELECT replace(meta_data, 'hrhi.', ''), null, 'NC_USER0000000000000', 0, 1, 0, 2, null, null, null, null, null, item_name, null, null, null, null, null, '~', replace(pk_infoset_item, '000', 'TWD'), 'TWHRA210000000DEFSET', (select top 1 pk_group from org_group), (select top 1 pk_group from org_group), null, null, null, null, null, null
FROM hr_infoset_item WHERE pk_infoset_item = 'TWHRA01000INFOSET004';
go





