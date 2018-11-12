alter table twhr_groupinsurancesetting add insurancecompany VARCHAR (100)



--HR_INFOSET表的删除语句
--delete  HR_INFOSET WHERE PK_INFOSET='TWHRA21000000000DEF6';;
--HR_INFOSETnull



Insert into HR_INFOSET
(dr,infoset_code,infoset_name,infoset_name2,infoset_name3,infoset_name4,infoset_name5,infoset_name6,infoset_type,look_history_flag,main_table_flag,memo,meta_data,meta_data_id,pk_field_code,pk_group,pk_infoset,pk_infoset_sort,pk_org,record_character,resid,respath,showorder,sync_main_code,sync_main_table,table_code,ts,user_def_flag,vo_class_name)
Values(0,'hi_psndoc_glbdef13','团保信息',null,null,null,null,null,1,null,'N',null,'hrhi.hi_psndoc_glbdef13','8bfc5950-4bf6-4cf3-b099-25556d6ab554','pk_psndoc_sub','~','TWHRA21000000000DEF6','1001Z710000000002XPO','GLOBLE00000000000000',1,'hi_psndoc_glbdef13','6007psn',36,null,'N','hi_psndoc_glbdef13','2018-09-16 12:25:16','Y','nc.vo.hi.psndoc.Glbdef13VO');


--HR_INFOSET_ITEM表的删除语句
--delete  HR_INFOSET_ITEM WHERE PK_INFOSET_ITEM='1001ZZ1000000000MOF4';
--HR_INFOSET_ITEMnull



Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-09-16 12:25:16','1001A1100000000001JN','Y',5,null,null,null,null,0,null,null,null,null,'N',null,null,null,'insurancecompany',null,null,'保险公司','保险公司',null,null,null,null,20,null,null,null,null,null,null,'Y','GLOBLE00000000000000','TWHRA21000000000DEF6','1001ZZ1000000000MOF4','~','GLOBLE00000000000000',null,'N','N','1001ZZ1000000000MODW','hi_psndoc_glbdef13_15418','6007psn',111,null,null,'2018-09-16 12:25:16','N',null);



--BD_MODE_SELECTED表的删除语句
delete  BD_MODE_SELECTED WHERE PK_BDMODE='1001ZZ1000000000MPWT';
--BD_MODE_SELECTEDnull



Insert into BD_MODE_SELECTED
(comp,dataoriginflag,dr,managemode,mdclassid,pk_bdmode,ts,uniquescope,visiblescope)
Values(null,0,0,3,'1001ZZ1000000000MODV','1001ZZ1000000000MPWT','2018-09-16 13:39:14',3,3);



--BD_DEFDOCLIST表的删除语句
delete  BD_DEFDOCLIST WHERE PK_DEFDOCLIST='1001ZZ1000000000MODV';
--BD_DEFDOCLISTnull



Insert into BD_DEFDOCLIST
(associatename,bpfcomponentid,code,codectlgrade,coderule,comp,componentid,creationtime,creator,dataoriginflag,docclass,doclevel,doctype,dr,funcode,isgrade,isrelease,mngctlmode,modifiedtime,modifier,name,name2,name3,name4,name5,name6,pk_defdoclist,pk_group,pk_org,ts)
Values('bd_defdoc','e33ae36f-b966-11e8-a42e-ab44e9fc28de','HRBXGS',null,null,null,'e2381943-b966-11e8-a42e-ab44e9fc28de','2018-09-16 12:13:36','1001A1100000000001JN',0,null,0,null,0,null,'N','N',4,'2018-09-16 13:39:12','1001A1100000000001JN','团保保险公司','团保保险公司',null,null,null,null,'1001ZZ1000000000MODV','~','GLOBLE00000000000000','2018-09-16 13:39:14');



--BD_REFINFO表的删除语句
delete  BD_REFINFO WHERE PK_REFINFO='1001ZZ1000000000MODW';
--BD_REFINFOnull



Insert into BD_REFINFO
(code,dr,isneedpara,isspecialref,layer,metadatanamespace,metadatatypename,modulename,name,para1,para2,para3,pk_country,pk_industry,pk_refinfo,refclass,refsystem,reftype,reserv1,reserv2,reserv3,resid,residpath,ts,wherepart)
Values('D10104',0,'Y',null,-1,null,'Defdoc-HRBXGS','uap','团保保险公司(自定义档案)','1001ZZ1000000000MODV','bd_defdoc',null,null,null,'1001ZZ1000000000MODW','nc.ui.bd.ref.model.DefdocGridRefModel',null,0,null,null,'HRBXGS','团保保险公司','10140uddb','2018-09-16 13:39:12',null);
