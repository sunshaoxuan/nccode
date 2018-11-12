
delete pub_query_condition where ( pk_templet in ('0001AA1000000005B7I8') and (dr=0 or dr is null) )
go

delete pub_query_templet where ( id='0001AA1000000005B7I8' and (pk_corp='@@@@') and (dr=0 or dr is null) )
go


insert into pub_query_templet(description,devorg,dr,fixcondition,fixquerytree,id,layer,metaclass,model_code,model_name,node_code,parentid,pk_corp,pk_org,resid,ts) values( null,null,0,null,null,'0001AA1000000005B7I8',0,'57865653-4b0e-4ab4-ac76-6686a44359cc','6017leaveextrarest','外加补休天数','6017leaveextrarest',null,'@@@@',null,null,'2018-09-11 18:14:19')
go


insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,0,1,null,0,null,null,null,null,null,'pk_extrarest',null,null,'0001AA1000000005B7I9','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005B7I8',null,null,2,null,null,'2018-09-11 18:14:19',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '人员',5,1,1,null,0,null,null,null,null,null,'pk_psndoc',null,null,'0001AA1000000005B7IA','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=','等于@',0,'@@@@','0001AA1000000005B7I8',null,'2psndoc-000018',2,null,null,'2018-09-11 18:14:19',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '行政组织版本',5,2,1,null,0,null,null,null,null,null,'pk_org_v',null,null,'0001AA1000000005B7IB','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=','等于@',0,'@@@@','0001AA1000000005B7I8',null,'2psndoc-000014',2,null,null,'2018-09-11 18:14:19',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '部门版本',5,3,1,null,0,null,null,null,null,null,'pk_dept_v',null,null,'0001AA1000000005B7IC','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=','等于@',0,'@@@@','0001AA1000000005B7I8',null,'2psndoc-000016',2,null,null,'2018-09-11 18:14:19',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '集团',5,4,1,null,0,null,null,null,null,null,'pk_group',null,null,'0001AA1000000005B7ID','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=','等于@',0,'@@@@','0001AA1000000005B7I8',null,'2UC000-001008',2,null,null,'2018-09-11 18:14:19',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '人力资源组织',5,5,1,null,0,null,null,null,null,null,'pk_org',null,null,'0001AA1000000005B7IE','N','Y','N','Y',null,'N','N','N','Y',null,'N','N','N','Y','Y',null,'Y',9999,null,'=@','等于@',0,'@@@@','0001AA1000000005B7I8',null,'2UC000-000694',2,null,null,'2018-09-11 18:14:19',null,'#mainorg#')
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '用户',5,6,1,null,0,null,null,null,null,null,'creator',null,null,'0001AA1000000005B7IF','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=','等于@',0,'@@@@','0001AA1000000005B7I8',null,'2UC000-000155',2,null,null,'2018-09-11 18:14:19',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',3,7,0,null,0,null,null,null,null,null,'billdate',null,null,'0001AA1000000005B7IG','N','Y','N','Y',null,'N','N','N','Y',null,'N','N','N','Y','Y',null,'Y',9999,null,'between@=@>@>=@<@<=@','介于@等于@大于@大于等于@小于@小于等于@',0,'@@@@','0001AA1000000005B7I8',null,null,2,null,null,'2018-09-11 18:14:19',null,'#day(0)#,#day(0)#')
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',8,8,1,null,0,null,null,null,null,null,'creationtime',null,null,'0001AA1000000005B7IH','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','Y','Y',null,'Y',9999,null,'between@=@>@>=@<@<=@','介于@等于@大于@大于等于@小于@小于等于@',0,'@@@@','0001AA1000000005B7I8',null,'2UC000-000157',2,null,null,'2018-09-11 18:14:19',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '用户',5,9,1,null,0,null,null,null,null,null,'modifier',null,null,'0001AA1000000005B7II','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=','等于@',0,'@@@@','0001AA1000000005B7I8',null,'2UC000-000098',2,null,null,'2018-09-11 18:14:19',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',8,10,1,null,0,null,null,null,null,null,'modifiedtime',null,null,'0001AA1000000005B7IJ','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','Y','Y',null,'Y',9999,null,'between@=@>@>=@<@<=@','介于@等于@大于@大于等于@小于@小于等于@',0,'@@@@','0001AA1000000005B7I8',null,'2UC000-000100',2,null,null,'2018-09-11 18:14:19',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,11,1,null,0,null,null,null,null,null,'beforechange',null,null,'0001AA1000000005B7IK','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005B7I8',null,null,2,null,null,'2018-09-11 18:14:19',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,12,1,null,0,null,null,null,null,null,'afterchange',null,null,'0001AA1000000005B7IL','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005B7I8',null,null,2,null,null,'2018-09-11 18:14:19',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,13,1,null,0,null,null,null,null,null,'changetype',null,null,'0001AA1000000005B7IM','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005B7I8',null,null,2,null,null,'2018-09-11 18:14:19',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '8',2,14,1,null,0,null,null,null,null,null,'changedayorhour',null,null,'0001AA1000000005B7IN','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'between@=@>@>=@<@<=@','介于@等于@大于@大于等于@小于@小于等于@',0,'@@@@','0001AA1000000005B7I8',null,null,2,null,null,'2018-09-11 18:14:19',null,null)
go
