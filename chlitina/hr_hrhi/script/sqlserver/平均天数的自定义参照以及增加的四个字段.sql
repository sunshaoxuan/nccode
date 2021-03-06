ALTER TABLE wa_item ADD avgcalcsalflag nchar (1) DEFAULT 'N';

--BD_MODE_SELECTED表的删除语句
delete  BD_MODE_SELECTED WHERE PK_BDMODE ='1001ZZ1000000000KGWK';
SELECT * FROM BD_DEFDOCLIST WHERE PK_DEFDOCLIST ='1001ZZ1000000000KGVY';
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
Values(null,null,null,'2018-08-22 15:39:47','NC_USER0000000000000','Y',3,null,null,null,null,0,null,null,null,null,'N','22d28c96-01c5-4283-8f4f-9d343fd2e162',null,null,'sepaydate',null,null,'资遣日期','资遣日期',null,null,null,null,19,null,'hrhi.bd_psndoc.sepaydate',null,'2018-08-27 14:01:13','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1002Z7100000000046GP','1001ZZ1000000000KU07','~','GLOBLE00000000000000',null,'N','N',null,'bd_psndoc_86421','6007psn',113,null,null,'2018-08-27 14:01:13','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-08-23 09:39:24','NC_USER0000000000000','Y',4,null,null,null,null,0,null,null,null,null,'N','7be3a328-d33e-4049-ae48-fcfb702e9f7b',null,null,'isoldemply',null,null,'是否旧制员工','是否旧制员工',null,null,null,null,1,null,'hrhi.bd_psndoc.isoldemply',null,'2018-08-27 14:01:13','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1002Z7100000000046GP','1001ZZ1000000000KURZ','~','GLOBLE00000000000000',null,'N','N',null,'bd_psndoc_64380','6007psn',114,null,null,'2018-08-27 14:01:13','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-08-23 09:39:24','NC_USER0000000000000','Y',101,null,null,null,null,0,null,null,null,null,'N','f8ab655d-5ac1-4448-a109-feda3e05328c',null,null,'oldsysbegindate',null,null,'旧制起始日期','旧制起始日期',null,null,null,null,19,null,'hrhi.bd_psndoc.oldsysbegindate',null,'2018-08-27 14:01:13','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1002Z7100000000046GP','1001ZZ1000000000KUS0','~','GLOBLE00000000000000',null,'N','N',null,'bd_psndoc_64381','6007psn',115,null,null,'2018-08-27 14:01:13','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-08-23 09:39:24','NC_USER0000000000000','Y',102,null,null,null,null,0,null,null,null,null,'N','31bf7e34-6c3c-4134-aaf0-493fac5838f1',null,null,'oldsysenddate',null,null,'旧制结束日期','旧制结束日期',null,null,null,null,19,null,'hrhi.bd_psndoc.oldsysenddate',null,'2018-08-27 14:01:13','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1002Z7100000000046GP','1001ZZ1000000000KUS1','~','GLOBLE00000000000000',null,'N','N',null,'bd_psndoc_64382','6007psn',116,null,null,'2018-08-27 14:01:13','N',null);

Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-08-23 09:39:24','NC_USER0000000000000','Y',3,null,null,null,null,0,null,null,null,null,'N','6a5053d1-3c78-46cd-9a63-f96eaf75d712',null,null,'retirementdate',null,null,'退休日期','退休日期',null,null,null,null,19,null,'hrhi.bd_psndoc.retirementdate',null,'2018-08-27 14:01:13','NC_USER0000000000000',null,'Y','GLOBLE00000000000000','1002Z7100000000046GP','1001ZZ1000000000KUS2','~','GLOBLE00000000000000',null,'N','N',null,'bd_psndoc_64383','6007psn',117,null,null,'2018-08-27 14:01:13','N',null);


