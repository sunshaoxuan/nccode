
delete pub_query_condition where ( pk_templet in ('0001AA1000000005HZWW') and (dr=0 or dr is null) )
go

delete pub_query_templet where ( id='0001AA1000000005HZWW' and (pk_corp='@@@@') and (dr=0 or dr is null) )
go


insert into pub_query_templet(description,devorg,dr,fixcondition,fixquerytree,id,layer,metaclass,model_code,model_name,node_code,parentid,pk_corp,pk_org,resid,ts) values( null,null,0,null,null,'0001AA1000000005HZWW',0,'5feea0da-60da-4707-9f77-da4dea35595b','60170leaveplans','休假计划','60170leaveplans',null,'@@@@',null,null,'2018-09-21 15:39:31')
go


insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,0,1,null,0,null,null,null,null,null,'pk_leaveplan',null,null,'0001AA1000000005HZWX','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '集团',5,1,1,null,0,null,null,null,null,null,'pk_group',null,null,'0001AA1000000005HZWY','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=','等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '人力资源组织',5,2,1,null,0,null,null,null,null,null,'pk_org',null,null,'0001AA1000000005HZWZ','N','Y','N','Y',null,'N','N','N','Y',null,'N','N','N','Y','Y',null,'Y',9999,null,'=@','等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,'#mainorg#')
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '行政组织版本',5,3,1,null,0,null,null,null,null,null,'pk_org_v',null,null,'0001AA1000000005HZX0','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=','等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( 'HR人员',5,4,1,null,0,null,null,null,null,null,'pk_psndoc',null,null,'0001AA1000000005HZX1','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=','等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '部门HR',5,5,1,null,0,null,null,null,null,null,'pk_dept',null,null,'0001AA1000000005HZX2','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=','等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '部门版本',5,6,1,null,0,null,null,null,null,null,'pk_dept_v',null,null,'0001AA1000000005HZX3','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=','等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '休假类别',5,7,1,null,0,null,null,null,null,null,'pk_leavetype',null,null,'0001AA1000000005HZX4','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=','等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',10,8,1,null,0,null,null,null,null,null,'begindate',null,null,'0001AA1000000005HZX5','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','Y','Y',null,'Y',9999,null,'between@=@>@>=@<@<=@','介于@等于@大于@大于等于@小于@小于等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',10,9,1,null,0,null,null,null,null,null,'enddate',null,null,'0001AA1000000005HZX6','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','Y','Y',null,'Y',9999,null,'between@=@>@>=@<@<=@','介于@等于@大于@大于等于@小于@小于等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',1,10,1,null,0,null,null,null,null,null,'enableddays',null,null,'0001AA1000000005HZX7','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'between@=@>@>=@<@<=@','介于@等于@大于@大于等于@小于@小于等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',1,11,1,null,0,null,null,null,null,null,'useddays',null,null,'0001AA1000000005HZX8','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'between@=@>@>=@<@<=@','介于@等于@大于@大于等于@小于@小于等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',1,12,1,null,0,null,null,null,null,null,'remaineddays',null,null,'0001AA1000000005HZX9','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'between@=@>@>=@<@<=@','介于@等于@大于@大于等于@小于@小于等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',4,13,1,null,0,null,null,null,null,null,'isattachment',null,null,'0001AA1000000005HZXA','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@','等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',4,14,1,null,0,null,null,null,null,null,'iscontinuous',null,null,'0001AA1000000005HZXB','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@','等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',3,15,1,null,0,null,null,null,null,null,'lastmaketime',null,null,'0001AA1000000005HZXC','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','Y','Y',null,'Y',9999,null,'between@=@>@>=@<@<=@','介于@等于@大于@大于等于@小于@小于等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,16,1,null,0,null,null,null,null,null,'billmaker',null,null,'0001AA1000000005HZXD','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,17,1,null,0,null,null,null,null,null,'pkorg',null,null,'0001AA1000000005HZXE','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '用户',5,18,1,null,0,null,null,null,null,null,'creator',null,null,'0001AA1000000005HZXF','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=','等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',3,19,1,null,0,null,null,null,null,null,'creationtime',null,null,'0001AA1000000005HZXG','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','Y','Y',null,'Y',9999,null,'between@=@>@>=@<@<=@','介于@等于@大于@大于等于@小于@小于等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '用户',5,20,1,null,0,null,null,null,null,null,'modifier',null,null,'0001AA1000000005HZXH','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=','等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',8,21,1,null,0,null,null,null,null,null,'modifiedtime',null,null,'0001AA1000000005HZXI','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','Y','Y',null,'Y',9999,null,'between@=@>@>=@<@<=@','介于@等于@大于@大于等于@小于@小于等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,22,1,null,0,null,null,null,null,null,'name',null,null,'0001AA1000000005HZXJ','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,23,1,null,0,null,null,null,null,null,'code',null,null,'0001AA1000000005HZXK','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',3,24,0,null,0,null,null,null,null,null,'dbilldate',null,null,'0001AA1000000005HZXL','N','Y','N','Y',null,'N','N','N','Y',null,'N','N','N','Y','Y',null,'Y',9999,null,'between@=@>@>=@<@<=@','介于@等于@大于@大于等于@小于@小于等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,'#day(0)#,#day(0)#')
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,25,1,null,0,null,null,null,null,null,'def1',null,null,'0001AA1000000005HZXM','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,26,1,null,0,null,null,null,null,null,'def2',null,null,'0001AA1000000005HZXN','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,27,1,null,0,null,null,null,null,null,'def3',null,null,'0001AA1000000005HZXO','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,28,1,null,0,null,null,null,null,null,'def4',null,null,'0001AA1000000005HZXP','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,29,1,null,0,null,null,null,null,null,'def5',null,null,'0001AA1000000005HZXQ','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,30,1,null,0,null,null,null,null,null,'def6',null,null,'0001AA1000000005HZXR','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,31,1,null,0,null,null,null,null,null,'def7',null,null,'0001AA1000000005HZXS','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,32,1,null,0,null,null,null,null,null,'def8',null,null,'0001AA1000000005HZXT','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,33,1,null,0,null,null,null,null,null,'def9',null,null,'0001AA1000000005HZXU','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,34,1,null,0,null,null,null,null,null,'def10',null,null,'0001AA1000000005HZXV','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,35,1,null,0,null,null,null,null,null,'def11',null,null,'0001AA1000000005HZXW','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,36,1,null,0,null,null,null,null,null,'def12',null,null,'0001AA1000000005HZXX','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,37,1,null,0,null,null,null,null,null,'def13',null,null,'0001AA1000000005HZXY','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,38,1,null,0,null,null,null,null,null,'def14',null,null,'0001AA1000000005HZXZ','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,39,1,null,0,null,null,null,null,null,'def15',null,null,'0001AA1000000005HZY0','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,40,1,null,0,null,null,null,null,null,'def16',null,null,'0001AA1000000005HZY1','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,41,1,null,0,null,null,null,null,null,'def17',null,null,'0001AA1000000005HZY2','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,42,1,null,0,null,null,null,null,null,'def18',null,null,'0001AA1000000005HZY3','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,43,1,null,0,null,null,null,null,null,'def19',null,null,'0001AA1000000005HZY4','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,44,1,null,0,null,null,null,null,null,'def20',null,null,'0001AA1000000005HZY5','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',8,45,1,null,0,null,null,null,null,null,'maketime',null,null,'0001AA1000000005HZY6','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','Y','Y',null,'Y',9999,null,'between@=@>@>=@<@<=@','介于@等于@大于@大于等于@小于@小于等于@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go
insert into pub_query_condition(consult_code,data_type,disp_sequence,disp_type,disp_value,dr,ext1,ext2,ext3,ext4,ext5,field_code,field_name,guideline,id,if_attrrefused,if_autocheck,if_datapower,if_default,if_desc,if_group,if_immobility,if_multicorpref,if_must,if_notmdcondition,if_order,if_subincluded,if_sum,if_sysfuncrefused,if_used,instrumentsql,iscondition,limits,max_length,opera_code,opera_name,order_sequence,pk_corp,pk_templet,prerestrict,resid,return_type,table_code,table_name,ts,userdefflag,value) values( '-99',0,46,1,null,0,null,null,null,null,null,'billcode',null,null,'0001AA1000000005HZY7','N','Y','N','N',null,'N','N','N','N',null,'N','N','N','N','Y',null,'Y',9999,null,'=@like@left like@right like@','等于@包含@左包含@右包含@',0,'@@@@','0001AA1000000005HZWW',null,null,2,null,null,'2018-09-21 15:39:31',null,null)
go