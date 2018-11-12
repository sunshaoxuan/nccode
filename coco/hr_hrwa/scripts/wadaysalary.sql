--此脚本作为元数据发布时参考使用,以便开发者了解元数据信息在数据库中的记录.
--严禁将此脚本直接或修改后在数据库中执行,如由上述行为引发问题,概不支持.


delete from md_db_relation where startattrid in (select id from md_property where classid in ('a8a05ebf-1b3e-4977-b0e8-ee9ee3c50b8d','c8b62afe-4bbb-4319-8f47-0989f39c0945','@@@@' ) )  and starttableid in ('wa_daysalary','@@@@')
;delete from md_ORMap where ATTRIBUTEID in(select id from md_property where classid in ('a8a05ebf-1b3e-4977-b0e8-ee9ee3c50b8d','c8b62afe-4bbb-4319-8f47-0989f39c0945','@@@@' ) )  and tableid in ('wa_daysalary','@@@@') 
;delete from md_table where id not in (select distinct tableid from md_ormap )
;delete from md_column where  tableid in('wa_daysalary','@@@@') and id not in(select distinct columnid from md_ormap) and (isnull(groupid ,'~')='~' or groupid = id )  and name <> 'extend_bean_tag' 
;delete from md_column where tableid in ('wa_daysalary','@@@@')and groupid is not null and groupid not in( select distinct id from md_column where tableid in ('wa_daysalary','@@@@') ) 
;delete from md_db_relation where starttableid not in (select id from md_table)
;delete from md_property where classID in (select distinct id from md_class where componentID='d6aedba4-d717-4f89-8f30-8e3b2d994a33'  )  
;delete from md_association where componentID = 'd6aedba4-d717-4f89-8f30-8e3b2d994a33'  
;delete from md_bizItfMap where classID in (select distinct id from md_class where componentID='d6aedba4-d717-4f89-8f30-8e3b2d994a33'  )
;delete from md_bizItfMap where classID in ( select id from md_opInterface where componentID = 'd6aedba4-d717-4f89-8f30-8e3b2d994a33'  )
;delete from md_accessorPara where id in (select distinct id from md_class where componentID='d6aedba4-d717-4f89-8f30-8e3b2d994a33'  )
;delete from md_enumValue where id in (select distinct id from md_class where componentID='d6aedba4-d717-4f89-8f30-8e3b2d994a33'  ) 
;delete from md_class where componentID = 'd6aedba4-d717-4f89-8f30-8e3b2d994a33'  
;delete from md_component where id = 'd6aedba4-d717-4f89-8f30-8e3b2d994a33' 
;delete from md_table where (id='wa_daysalary')
;delete from md_column where (id='wa_daysalary@@PK@@')
;delete from md_column where (id='wa_daysalary@@@pk_hrorg')
;delete from md_column where (id='wa_daysalary@@@pk_wa_class')
;delete from md_column where (id='wa_daysalary@@@pk_wa_item')
;delete from md_column where (id='wa_daysalary@@@pk_psndoc_sub')
;delete from md_column where (id='wa_daysalary@@@wadocts')
;delete from md_column where (id='wa_daysalary@@@pk_psndoc')
;delete from md_column where (id='wa_daysalary@@@pk_psnjob')
;delete from md_column where (id='wa_daysalary@@@salarydate')
;delete from md_column where (id='wa_daysalary@@@cyear')
;delete from md_column where (id='wa_daysalary@@@cperiod')
;delete from md_column where (id='wa_daysalary@@@daysalary')
;delete from md_column where (id='wa_daysalary@@@hoursalary')



;insert into md_component ( createtime, creator, description, displayname, dr, fromsourcebmf, help, id, industry, isbizmodel, modifier, modifytime, name, namespace, ownmodule, preload, resid, resmodule, ts, version, versiontype ) values (  '2018-04-25 14:25:02', 'YONYOU NC', null, 'wadaysalary',0, 'Y', null, 'd6aedba4-d717-4f89-8f30-8e3b2d994a33', '0', 'N', 'YONYOU NC', '2018-10-28 15:59:07', 'wadaysalary', 'hrwa', 'hrwa', null, null, 'wadaysalary', '2018-10-28 16:13:15', '38',0 ) 
;insert into md_class ( id, accessorclassname, classtype, componentid, createtime, creator, description, displayname, dr, fullclassname, help, isactive, keyattribute, modifier, modifytime, name, parentclassid, precise, refmodelname, returntype, ts, versiontype, isprimary, defaulttablename, stereotype, isauthen, resid, bizitfimpclassname, modinfoclassname, iscreatesql, isextendbean, userdefclassname, industry, fixedlength ) values (  'c8b62afe-4bbb-4319-8f47-0989f39c0945', 'nc.md.model.access.javamap.NCBeanStyle',201, 'd6aedba4-d717-4f89-8f30-8e3b2d994a33', '2018-05-02 23:19:08', 'YONYOU NC', null, '日薪',0, 'nc.vo.hrwa.wadaysalary.DaySalaryVO', null, null, '67a43946-bba8-4aa6-9ebf-72ad69dd6530', 'YONYOU NC', '2018-10-28 16:12:49', 'DaySalaryVO', null,null, null, null, '2018-10-28 16:13:15',0, 'Y', 'wa_daysalary', null, 'Y', null, null, null, 'Y', null, null, '0', null ) 
;insert into md_class ( id, accessorclassname, classtype, componentid, createtime, creator, description, displayname, dr, fullclassname, help, isactive, keyattribute, modifier, modifytime, name, parentclassid, precise, refmodelname, returntype, ts, versiontype, isprimary, defaulttablename, stereotype, isauthen, resid, bizitfimpclassname, modinfoclassname, iscreatesql, isextendbean, userdefclassname, industry, fixedlength ) values (  'a8a05ebf-1b3e-4977-b0e8-ee9ee3c50b8d', null,203, 'd6aedba4-d717-4f89-8f30-8e3b2d994a33', '2018-10-28 15:59:07', 'YONYOU NC', null, '平均月数',0, 'nc.vo.hrwa.wadaysalary.AvageMonthEnum', null, null, null, 'YONYOU NC', '2018-10-28 16:02:36', 'AvageMonthEnum', null,null, null, 'BS000010000100001004', '2018-10-28 16:13:15',0, 'N', 'AvageMonthEnum', null, null, null, null, null, null, null, null, '0', null ) 
;insert into md_enumValue ( id, description, dr, name, ts, value, versiontype, hidden, resid, enumsequence, industry ) values (  'a8a05ebf-1b3e-4977-b0e8-ee9ee3c50b8d', null,0, '1月份', '2018-10-28 16:13:15', '1',0, 'N', null,0, '0' ) 
;insert into md_enumValue ( id, description, dr, name, ts, value, versiontype, hidden, resid, enumsequence, industry ) values (  'a8a05ebf-1b3e-4977-b0e8-ee9ee3c50b8d', null,0, '2月份', '2018-10-28 16:13:15', '2',0, 'N', null,1, '0' ) 
;insert into md_enumValue ( id, description, dr, name, ts, value, versiontype, hidden, resid, enumsequence, industry ) values (  'a8a05ebf-1b3e-4977-b0e8-ee9ee3c50b8d', null,0, '3月份', '2018-10-28 16:13:15', '3',0, 'N', null,2, '0' ) 
;insert into md_enumValue ( id, description, dr, name, ts, value, versiontype, hidden, resid, enumsequence, industry ) values (  'a8a05ebf-1b3e-4977-b0e8-ee9ee3c50b8d', null,0, '4月份', '2018-10-28 16:13:15', '4',0, 'N', null,3, '0' ) 
;insert into md_enumValue ( id, description, dr, name, ts, value, versiontype, hidden, resid, enumsequence, industry ) values (  'a8a05ebf-1b3e-4977-b0e8-ee9ee3c50b8d', null,0, '5月份', '2018-10-28 16:13:15', '5',0, 'N', null,4, '0' ) 
;insert into md_enumValue ( id, description, dr, name, ts, value, versiontype, hidden, resid, enumsequence, industry ) values (  'a8a05ebf-1b3e-4977-b0e8-ee9ee3c50b8d', null,0, '6月份', '2018-10-28 16:13:15', '6',0, 'N', null,5, '0' ) 
;insert into md_enumValue ( id, description, dr, name, ts, value, versiontype, hidden, resid, enumsequence, industry ) values (  'a8a05ebf-1b3e-4977-b0e8-ee9ee3c50b8d', null,0, '7月份', '2018-10-28 16:13:15', '7',0, 'N', null,6, '0' ) 
;insert into md_enumValue ( id, description, dr, name, ts, value, versiontype, hidden, resid, enumsequence, industry ) values (  'a8a05ebf-1b3e-4977-b0e8-ee9ee3c50b8d', null,0, '8月份', '2018-10-28 16:13:15', '8',0, 'N', null,7, '0' ) 
;insert into md_enumValue ( id, description, dr, name, ts, value, versiontype, hidden, resid, enumsequence, industry ) values (  'a8a05ebf-1b3e-4977-b0e8-ee9ee3c50b8d', null,0, '9月份', '2018-10-28 16:13:15', '9',0, 'N', null,8, '0' ) 
;insert into md_enumValue ( id, description, dr, name, ts, value, versiontype, hidden, resid, enumsequence, industry ) values (  'a8a05ebf-1b3e-4977-b0e8-ee9ee3c50b8d', null,0, '10月份', '2018-10-28 16:13:15', '10',0, 'N', null,9, '0' ) 
;insert into md_enumValue ( id, description, dr, name, ts, value, versiontype, hidden, resid, enumsequence, industry ) values (  'a8a05ebf-1b3e-4977-b0e8-ee9ee3c50b8d', null,0, '11月份', '2018-10-28 16:13:15', '11',0, 'N', null,10, '0' ) 
;insert into md_enumValue ( id, description, dr, name, ts, value, versiontype, hidden, resid, enumsequence, industry ) values (  'a8a05ebf-1b3e-4977-b0e8-ee9ee3c50b8d', null,0, '12月份', '2018-10-28 16:13:15', '12',0, 'N', null,11, '0' ) 
;insert into md_property ( id, accessorclassname, classid, createtime, creator, datatype, datatypestyle, defaultvalue, description, displayname, dr, help, isactive, hided, nullable, readonly, attrlength, attrmaxvalue, attrminvalue, modifier, modifytime, name, precise, refmodelname, attrsequence, ts, visibility, versiontype, fixedlength, calculation, isauthen, resid, customattr, notserialize, dynamicattr, dynamictable, accesspower, accesspowergroup, industry ) values (  '67a43946-bba8-4aa6-9ebf-72ad69dd6530', null, 'c8b62afe-4bbb-4319-8f47-0989f39c0945', null, null, 'BS000010000100001051',300, null, null, '日薪主键',0, null, 'Y', 'N', 'N', 'N',20, null, null, null, null, 'pk_daysalary',0, null,0, '2018-10-28 16:13:15',0,0, 'N', 'N', 'Y', null, 'N', 'N', 'N', null, 'N', null, '0' ) 
;insert into md_property ( id, accessorclassname, classid, createtime, creator, datatype, datatypestyle, defaultvalue, description, displayname, dr, help, isactive, hided, nullable, readonly, attrlength, attrmaxvalue, attrminvalue, modifier, modifytime, name, precise, refmodelname, attrsequence, ts, visibility, versiontype, fixedlength, calculation, isauthen, resid, customattr, notserialize, dynamicattr, dynamictable, accesspower, accesspowergroup, industry ) values (  'cbc1cec7-938c-4122-9afe-2911e364277a', null, 'c8b62afe-4bbb-4319-8f47-0989f39c0945', null, null, 'f3fed5ea-72f2-4a0a-ad43-d30b1c22c86c',305, null, null, '人力資源組織',0, null, 'Y', 'N', 'Y', 'N',20, null, null, null, null, 'pk_hrorg',0, null,1, '2018-10-28 16:13:15',0,0, 'N', 'N', 'Y', null, 'N', 'N', 'N', null, 'N', null, '0' ) 
;insert into md_property ( id, accessorclassname, classid, createtime, creator, datatype, datatypestyle, defaultvalue, description, displayname, dr, help, isactive, hided, nullable, readonly, attrlength, attrmaxvalue, attrminvalue, modifier, modifytime, name, precise, refmodelname, attrsequence, ts, visibility, versiontype, fixedlength, calculation, isauthen, resid, customattr, notserialize, dynamicattr, dynamictable, accesspower, accesspowergroup, industry ) values (  '78915563-da0b-4656-93bc-8bc76e90cfdf', null, 'c8b62afe-4bbb-4319-8f47-0989f39c0945', null, null, 'BS000010000100001001',300, null, null, '薪资方案',0, null, 'Y', 'N', 'Y', 'N',50, null, null, null, null, 'pk_wa_class',0, null,2, '2018-10-28 16:13:15',0,0, 'N', 'N', 'Y', null, 'N', 'N', 'N', null, 'N', null, '0' ) 
;insert into md_property ( id, accessorclassname, classid, createtime, creator, datatype, datatypestyle, defaultvalue, description, displayname, dr, help, isactive, hided, nullable, readonly, attrlength, attrmaxvalue, attrminvalue, modifier, modifytime, name, precise, refmodelname, attrsequence, ts, visibility, versiontype, fixedlength, calculation, isauthen, resid, customattr, notserialize, dynamicattr, dynamictable, accesspower, accesspowergroup, industry ) values (  'fb095705-d73b-4eaa-976e-4723c4c7d73c', null, 'c8b62afe-4bbb-4319-8f47-0989f39c0945', null, null, 'BS000010000100001001',300, null, null, '薪资项目',0, null, 'Y', 'N', 'Y', 'N',20, null, null, null, null, 'pk_wa_item',0, null,3, '2018-10-28 16:13:15',0,0, 'N', 'N', 'Y', null, 'N', 'N', 'N', null, 'N', null, '0' ) 
;insert into md_property ( id, accessorclassname, classid, createtime, creator, datatype, datatypestyle, defaultvalue, description, displayname, dr, help, isactive, hided, nullable, readonly, attrlength, attrmaxvalue, attrminvalue, modifier, modifytime, name, precise, refmodelname, attrsequence, ts, visibility, versiontype, fixedlength, calculation, isauthen, resid, customattr, notserialize, dynamicattr, dynamictable, accesspower, accesspowergroup, industry ) values (  '159a1748-4c29-45c3-a103-52f6103baef6', null, 'c8b62afe-4bbb-4319-8f47-0989f39c0945', null, null, 'BS000010000100001001',300, null, null, '薪资变动情况',0, null, 'Y', 'N', 'Y', 'N',20, null, null, null, null, 'pk_psndoc_sub',0, null,4, '2018-10-28 16:13:15',0,0, 'N', 'N', 'Y', null, 'N', 'N', 'N', null, 'N', null, '0' ) 
;insert into md_property ( id, accessorclassname, classid, createtime, creator, datatype, datatypestyle, defaultvalue, description, displayname, dr, help, isactive, hided, nullable, readonly, attrlength, attrmaxvalue, attrminvalue, modifier, modifytime, name, precise, refmodelname, attrsequence, ts, visibility, versiontype, fixedlength, calculation, isauthen, resid, customattr, notserialize, dynamicattr, dynamictable, accesspower, accesspowergroup, industry ) values (  '08b84e7e-1349-4831-ad1b-aa99c5ff2815', null, 'c8b62afe-4bbb-4319-8f47-0989f39c0945', null, null, 'BS000010000100001034',300, null, null, '薪资变动情况时间戳',0, null, 'Y', 'N', 'Y', 'N',19, null, null, null, null, 'wadocts',0, null,5, '2018-10-28 16:13:15',0,0, 'N', 'N', 'Y', null, 'N', 'N', 'N', null, 'N', null, '0' ) 
;insert into md_property ( id, accessorclassname, classid, createtime, creator, datatype, datatypestyle, defaultvalue, description, displayname, dr, help, isactive, hided, nullable, readonly, attrlength, attrmaxvalue, attrminvalue, modifier, modifytime, name, precise, refmodelname, attrsequence, ts, visibility, versiontype, fixedlength, calculation, isauthen, resid, customattr, notserialize, dynamicattr, dynamictable, accesspower, accesspowergroup, industry ) values (  '61e843d2-6d65-4d70-a0f7-b223c86faa4e', null, 'c8b62afe-4bbb-4319-8f47-0989f39c0945', null, null, '218971f0-e5dc-408b-9a32-56529dddd4db',305, null, null, '人员基本主键',0, null, 'Y', 'N', 'Y', 'N',20, null, null, null, null, 'pk_psndoc',0, 'HR人员',6, '2018-10-28 16:13:15',0,0, 'N', 'N', 'Y', null, 'N', 'N', 'N', null, 'N', null, '0' ) 
;insert into md_property ( id, accessorclassname, classid, createtime, creator, datatype, datatypestyle, defaultvalue, description, displayname, dr, help, isactive, hided, nullable, readonly, attrlength, attrmaxvalue, attrminvalue, modifier, modifytime, name, precise, refmodelname, attrsequence, ts, visibility, versiontype, fixedlength, calculation, isauthen, resid, customattr, notserialize, dynamicattr, dynamictable, accesspower, accesspowergroup, industry ) values (  'da6221c8-2d95-4e0e-9a79-fa42631ccb1b', null, 'c8b62afe-4bbb-4319-8f47-0989f39c0945', null, null, '7156d223-4531-4337-b192-492ab40098f1',305, null, null, '人员任职主键',0, null, 'Y', 'N', 'Y', 'N',20, null, null, null, null, 'pk_psnjob',0, '人员工作记录',7, '2018-10-28 16:13:15',0,0, 'N', 'N', 'Y', null, 'N', 'N', 'N', null, 'N', null, '0' ) 
;insert into md_property ( id, accessorclassname, classid, createtime, creator, datatype, datatypestyle, defaultvalue, description, displayname, dr, help, isactive, hided, nullable, readonly, attrlength, attrmaxvalue, attrminvalue, modifier, modifytime, name, precise, refmodelname, attrsequence, ts, visibility, versiontype, fixedlength, calculation, isauthen, resid, customattr, notserialize, dynamicattr, dynamictable, accesspower, accesspowergroup, industry ) values (  '398095a5-37b0-407c-989d-005bba5df4d6', null, 'c8b62afe-4bbb-4319-8f47-0989f39c0945', null, null, 'BS000010000100001039',300, null, null, '薪资日期',0, null, 'Y', 'N', 'Y', 'N',10, null, null, null, null, 'salarydate',0, null,8, '2018-10-28 16:13:15',0,0, 'N', 'N', 'Y', null, 'N', 'N', 'N', null, 'N', null, '0' ) 
;insert into md_property ( id, accessorclassname, classid, createtime, creator, datatype, datatypestyle, defaultvalue, description, displayname, dr, help, isactive, hided, nullable, readonly, attrlength, attrmaxvalue, attrminvalue, modifier, modifytime, name, precise, refmodelname, attrsequence, ts, visibility, versiontype, fixedlength, calculation, isauthen, resid, customattr, notserialize, dynamicattr, dynamictable, accesspower, accesspowergroup, industry ) values (  'f08360e0-0c61-4182-a61e-d3fa76746aa9', null, 'c8b62afe-4bbb-4319-8f47-0989f39c0945', null, null, 'BS000010000100001004',300, null, null, '薪资年',0, null, 'Y', 'N', 'Y', 'N',0, null, null, null, null, 'cyear',0, null,9, '2018-10-28 16:13:15',0,0, 'N', 'N', 'Y', null, 'N', 'N', 'N', null, 'N', null, '0' ) 
;insert into md_property ( id, accessorclassname, classid, createtime, creator, datatype, datatypestyle, defaultvalue, description, displayname, dr, help, isactive, hided, nullable, readonly, attrlength, attrmaxvalue, attrminvalue, modifier, modifytime, name, precise, refmodelname, attrsequence, ts, visibility, versiontype, fixedlength, calculation, isauthen, resid, customattr, notserialize, dynamicattr, dynamictable, accesspower, accesspowergroup, industry ) values (  '20188956-007c-44d7-be41-9e37ce6d4a32', null, 'c8b62afe-4bbb-4319-8f47-0989f39c0945', null, null, 'BS000010000100001004',300, null, null, '薪资月',0, null, 'Y', 'N', 'Y', 'N',0, null, null, null, null, 'cperiod',0, null,10, '2018-10-28 16:13:15',0,0, 'N', 'N', 'Y', null, 'N', 'N', 'N', null, 'N', null, '0' ) 
;insert into md_property ( id, accessorclassname, classid, createtime, creator, datatype, datatypestyle, defaultvalue, description, displayname, dr, help, isactive, hided, nullable, readonly, attrlength, attrmaxvalue, attrminvalue, modifier, modifytime, name, precise, refmodelname, attrsequence, ts, visibility, versiontype, fixedlength, calculation, isauthen, resid, customattr, notserialize, dynamicattr, dynamictable, accesspower, accesspowergroup, industry ) values (  '23c233cd-0683-47ef-bbd8-a30a5a957f9c', null, 'c8b62afe-4bbb-4319-8f47-0989f39c0945', null, null, 'BS000010000100001031',300, null, null, '定调资日薪',0, null, 'Y', 'N', 'Y', 'N',28, null, null, null, null, 'daysalary',8, null,11, '2018-10-28 16:13:15',0,0, 'N', 'N', 'Y', null, 'N', 'N', 'N', null, 'N', null, '0' ) 
;insert into md_property ( id, accessorclassname, classid, createtime, creator, datatype, datatypestyle, defaultvalue, description, displayname, dr, help, isactive, hided, nullable, readonly, attrlength, attrmaxvalue, attrminvalue, modifier, modifytime, name, precise, refmodelname, attrsequence, ts, visibility, versiontype, fixedlength, calculation, isauthen, resid, customattr, notserialize, dynamicattr, dynamictable, accesspower, accesspowergroup, industry ) values (  '5772cff2-ef6c-4ba4-9501-7f6d53b14ace', null, 'c8b62afe-4bbb-4319-8f47-0989f39c0945', null, null, 'BS000010000100001031',300, null, null, '定调资时薪',0, null, 'Y', 'N', 'Y', 'N',28, null, null, null, null, 'hoursalary',8, null,12, '2018-10-28 16:13:15',0,0, 'N', 'N', 'Y', null, 'N', 'N', 'N', null, 'N', null, '0' ) 
;insert into md_association ( id, componentid, creator, createtime, dr, endcardinality, startbeanid, endelementid, isactive, modifier, modifytime, name, startcardinality, startelementid, ts, type, versiontype, industry ) values (  '6b707aa6-c937-4e19-a064-663ea0437994', 'd6aedba4-d717-4f89-8f30-8e3b2d994a33', null, null,0, '1', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', 'f3fed5ea-72f2-4a0a-ad43-d30b1c22c86c', 'Y', null, null, 'DaySalaryVO_pk_hrorg', '1', 'cbc1cec7-938c-4122-9afe-2911e364277a', '2018-10-28 16:13:15',3,0, '0' ) 
;insert into md_association ( id, componentid, creator, createtime, dr, endcardinality, startbeanid, endelementid, isactive, modifier, modifytime, name, startcardinality, startelementid, ts, type, versiontype, industry ) values (  'b7526b6d-70a3-450e-ab2f-306f69f03683', 'd6aedba4-d717-4f89-8f30-8e3b2d994a33', null, null,0, '1', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', '218971f0-e5dc-408b-9a32-56529dddd4db', 'Y', null, null, 'DaySalaryVO_pk_psndoc', '1', '61e843d2-6d65-4d70-a0f7-b223c86faa4e', '2018-10-28 16:13:15',3,0, '0' ) 
;insert into md_association ( id, componentid, creator, createtime, dr, endcardinality, startbeanid, endelementid, isactive, modifier, modifytime, name, startcardinality, startelementid, ts, type, versiontype, industry ) values (  '1561c8ad-852b-4900-bb18-8d2d3010f59c', 'd6aedba4-d717-4f89-8f30-8e3b2d994a33', null, null,0, '1', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', '7156d223-4531-4337-b192-492ab40098f1', 'Y', null, null, 'DaySalaryVO_pk_psnjob', '1', 'da6221c8-2d95-4e0e-9a79-fa42631ccb1b', '2018-10-28 16:13:15',3,0, '0' ) 
;insert into md_table ( id, createtime, creator, databaseid, description, displayname, dr, help, isactive, fromsourcebmf, modifier, modifytime, name, ts, versiontype, resmodule, resid, parenttable, isextendtable, industry ) values (  'wa_daysalary', '2018-05-02 23:19:08', 'YONYOU NC', null, null, '日薪',0, null, null, 'Y', 'YONYOU NC', '2018-10-28 16:12:49', 'wa_daysalary', '2018-10-28 16:13:15',0, 'wadaysalary', null, null, null, '0' ) 
;insert into md_column ( id, sqldatetype, createtime, creator, defaultvalue, description, displayname, dr, help, incrementseed, incrementstep, isactive, identitied, pkey, nullable, columnlength, modifier, modifytime, name, precise, columnsequence, tableid, ts, versiontype, resid, groupid, columntype, forlocale ) values (  'wa_daysalary@@PK@@', 'char', null, null, null, null, '日薪主键',0, null,null,null, 'Y', null, 'Y', 'N',20, null, null, 'pk_daysalary',0,0, 'wa_daysalary', '2018-10-28 16:13:15',0, null, null,0, 'N' ) 
;insert into md_column ( id, sqldatetype, createtime, creator, defaultvalue, description, displayname, dr, help, incrementseed, incrementstep, isactive, identitied, pkey, nullable, columnlength, modifier, modifytime, name, precise, columnsequence, tableid, ts, versiontype, resid, groupid, columntype, forlocale ) values (  'wa_daysalary@@@pk_hrorg', 'varchar', null, null, '*~', null, '人力資源組織',0, null,null,null, 'Y', null, 'N', 'Y',20, null, null, 'pk_hrorg',0,1, 'wa_daysalary', '2018-10-28 16:13:15',0, null, null,0, 'N' ) 
;insert into md_column ( id, sqldatetype, createtime, creator, defaultvalue, description, displayname, dr, help, incrementseed, incrementstep, isactive, identitied, pkey, nullable, columnlength, modifier, modifytime, name, precise, columnsequence, tableid, ts, versiontype, resid, groupid, columntype, forlocale ) values (  'wa_daysalary@@@pk_wa_class', 'varchar', null, null, null, null, '薪资方案',0, null,null,null, 'Y', null, 'N', 'Y',50, null, null, 'pk_wa_class',0,2, 'wa_daysalary', '2018-10-28 16:13:15',0, null, null,0, 'N' ) 
;insert into md_column ( id, sqldatetype, createtime, creator, defaultvalue, description, displayname, dr, help, incrementseed, incrementstep, isactive, identitied, pkey, nullable, columnlength, modifier, modifytime, name, precise, columnsequence, tableid, ts, versiontype, resid, groupid, columntype, forlocale ) values (  'wa_daysalary@@@pk_wa_item', 'varchar', null, null, null, null, '薪资项目',0, null,null,null, 'Y', null, 'N', 'Y',20, null, null, 'pk_wa_item',0,3, 'wa_daysalary', '2018-10-28 16:13:15',0, null, null,0, 'N' ) 
;insert into md_column ( id, sqldatetype, createtime, creator, defaultvalue, description, displayname, dr, help, incrementseed, incrementstep, isactive, identitied, pkey, nullable, columnlength, modifier, modifytime, name, precise, columnsequence, tableid, ts, versiontype, resid, groupid, columntype, forlocale ) values (  'wa_daysalary@@@pk_psndoc_sub', 'varchar', null, null, null, null, '薪资变动情况',0, null,null,null, 'Y', null, 'N', 'Y',20, null, null, 'pk_psndoc_sub',0,4, 'wa_daysalary', '2018-10-28 16:13:15',0, null, null,0, 'N' ) 
;insert into md_column ( id, sqldatetype, createtime, creator, defaultvalue, description, displayname, dr, help, incrementseed, incrementstep, isactive, identitied, pkey, nullable, columnlength, modifier, modifytime, name, precise, columnsequence, tableid, ts, versiontype, resid, groupid, columntype, forlocale ) values (  'wa_daysalary@@@wadocts', 'char', null, null, null, null, '薪资变动情况时间戳',0, null,null,null, 'Y', null, 'N', 'Y',19, null, null, 'wadocts',0,5, 'wa_daysalary', '2018-10-28 16:13:15',0, null, null,0, 'N' ) 
;insert into md_column ( id, sqldatetype, createtime, creator, defaultvalue, description, displayname, dr, help, incrementseed, incrementstep, isactive, identitied, pkey, nullable, columnlength, modifier, modifytime, name, precise, columnsequence, tableid, ts, versiontype, resid, groupid, columntype, forlocale ) values (  'wa_daysalary@@@pk_psndoc', 'varchar', null, null, '*~', null, '人员基本主键',0, null,null,null, 'Y', null, 'N', 'Y',20, null, null, 'pk_psndoc',0,6, 'wa_daysalary', '2018-10-28 16:13:15',0, null, null,0, 'N' ) 
;insert into md_column ( id, sqldatetype, createtime, creator, defaultvalue, description, displayname, dr, help, incrementseed, incrementstep, isactive, identitied, pkey, nullable, columnlength, modifier, modifytime, name, precise, columnsequence, tableid, ts, versiontype, resid, groupid, columntype, forlocale ) values (  'wa_daysalary@@@pk_psnjob', 'varchar', null, null, '*~', null, '人员任职主键',0, null,null,null, 'Y', null, 'N', 'Y',20, null, null, 'pk_psnjob',0,7, 'wa_daysalary', '2018-10-28 16:13:15',0, null, null,0, 'N' ) 
;insert into md_column ( id, sqldatetype, createtime, creator, defaultvalue, description, displayname, dr, help, incrementseed, incrementstep, isactive, identitied, pkey, nullable, columnlength, modifier, modifytime, name, precise, columnsequence, tableid, ts, versiontype, resid, groupid, columntype, forlocale ) values (  'wa_daysalary@@@salarydate', 'char', null, null, null, null, '薪资日期',0, null,null,null, 'Y', null, 'N', 'Y',10, null, null, 'salarydate',0,8, 'wa_daysalary', '2018-10-28 16:13:15',0, null, null,0, 'N' ) 
;insert into md_column ( id, sqldatetype, createtime, creator, defaultvalue, description, displayname, dr, help, incrementseed, incrementstep, isactive, identitied, pkey, nullable, columnlength, modifier, modifytime, name, precise, columnsequence, tableid, ts, versiontype, resid, groupid, columntype, forlocale ) values (  'wa_daysalary@@@cyear', 'int', null, null, null, null, '薪资年',0, null,null,null, 'Y', null, 'N', 'Y',0, null, null, 'cyear',0,9, 'wa_daysalary', '2018-10-28 16:13:15',0, null, null,0, 'N' ) 
;insert into md_column ( id, sqldatetype, createtime, creator, defaultvalue, description, displayname, dr, help, incrementseed, incrementstep, isactive, identitied, pkey, nullable, columnlength, modifier, modifytime, name, precise, columnsequence, tableid, ts, versiontype, resid, groupid, columntype, forlocale ) values (  'wa_daysalary@@@cperiod', 'int', null, null, null, null, '薪资月',0, null,null,null, 'Y', null, 'N', 'Y',0, null, null, 'cperiod',0,10, 'wa_daysalary', '2018-10-28 16:13:15',0, null, null,0, 'N' ) 
;insert into md_column ( id, sqldatetype, createtime, creator, defaultvalue, description, displayname, dr, help, incrementseed, incrementstep, isactive, identitied, pkey, nullable, columnlength, modifier, modifytime, name, precise, columnsequence, tableid, ts, versiontype, resid, groupid, columntype, forlocale ) values (  'wa_daysalary@@@daysalary', 'decimal', null, null, null, null, '定调资日薪',0, null,null,null, 'Y', null, 'N', 'Y',28, null, null, 'daysalary',8,11, 'wa_daysalary', '2018-10-28 16:13:15',0, null, null,0, 'N' ) 
;insert into md_column ( id, sqldatetype, createtime, creator, defaultvalue, description, displayname, dr, help, incrementseed, incrementstep, isactive, identitied, pkey, nullable, columnlength, modifier, modifytime, name, precise, columnsequence, tableid, ts, versiontype, resid, groupid, columntype, forlocale ) values (  'wa_daysalary@@@hoursalary', 'decimal', null, null, null, null, '定调资时薪',0, null,null,null, 'Y', null, 'N', 'Y',28, null, null, 'hoursalary',8,12, 'wa_daysalary', '2018-10-28 16:13:15',0, null, null,0, 'N' ) 
;insert into md_ORMap ( attributeid, classid, columnid, dr, tableid, ts ) values (  '67a43946-bba8-4aa6-9ebf-72ad69dd6530', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', 'wa_daysalary@@PK@@',0, 'wa_daysalary', '2018-10-28 16:13:15' ) 
;insert into md_ORMap ( attributeid, classid, columnid, dr, tableid, ts ) values (  '08b84e7e-1349-4831-ad1b-aa99c5ff2815', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', 'wa_daysalary@@@wadocts',0, 'wa_daysalary', '2018-10-28 16:13:15' ) 
;insert into md_ORMap ( attributeid, classid, columnid, dr, tableid, ts ) values (  'f08360e0-0c61-4182-a61e-d3fa76746aa9', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', 'wa_daysalary@@@cyear',0, 'wa_daysalary', '2018-10-28 16:13:15' ) 
;insert into md_ORMap ( attributeid, classid, columnid, dr, tableid, ts ) values (  '20188956-007c-44d7-be41-9e37ce6d4a32', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', 'wa_daysalary@@@cperiod',0, 'wa_daysalary', '2018-10-28 16:13:15' ) 
;insert into md_ORMap ( attributeid, classid, columnid, dr, tableid, ts ) values (  '5772cff2-ef6c-4ba4-9501-7f6d53b14ace', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', 'wa_daysalary@@@hoursalary',0, 'wa_daysalary', '2018-10-28 16:13:15' ) 
;insert into md_ORMap ( attributeid, classid, columnid, dr, tableid, ts ) values (  '23c233cd-0683-47ef-bbd8-a30a5a957f9c', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', 'wa_daysalary@@@daysalary',0, 'wa_daysalary', '2018-10-28 16:13:15' ) 
;insert into md_ORMap ( attributeid, classid, columnid, dr, tableid, ts ) values (  '78915563-da0b-4656-93bc-8bc76e90cfdf', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', 'wa_daysalary@@@pk_wa_class',0, 'wa_daysalary', '2018-10-28 16:13:15' ) 
;insert into md_ORMap ( attributeid, classid, columnid, dr, tableid, ts ) values (  'cbc1cec7-938c-4122-9afe-2911e364277a', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', 'wa_daysalary@@@pk_hrorg',0, 'wa_daysalary', '2018-10-28 16:13:15' ) 
;insert into md_ORMap ( attributeid, classid, columnid, dr, tableid, ts ) values (  'da6221c8-2d95-4e0e-9a79-fa42631ccb1b', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', 'wa_daysalary@@@pk_psnjob',0, 'wa_daysalary', '2018-10-28 16:13:15' ) 
;insert into md_ORMap ( attributeid, classid, columnid, dr, tableid, ts ) values (  '159a1748-4c29-45c3-a103-52f6103baef6', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', 'wa_daysalary@@@pk_psndoc_sub',0, 'wa_daysalary', '2018-10-28 16:13:15' ) 
;insert into md_ORMap ( attributeid, classid, columnid, dr, tableid, ts ) values (  'fb095705-d73b-4eaa-976e-4723c4c7d73c', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', 'wa_daysalary@@@pk_wa_item',0, 'wa_daysalary', '2018-10-28 16:13:15' ) 
;insert into md_ORMap ( attributeid, classid, columnid, dr, tableid, ts ) values (  '61e843d2-6d65-4d70-a0f7-b223c86faa4e', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', 'wa_daysalary@@@pk_psndoc',0, 'wa_daysalary', '2018-10-28 16:13:15' ) 
;insert into md_ORMap ( attributeid, classid, columnid, dr, tableid, ts ) values (  '398095a5-37b0-407c-989d-005bba5df4d6', 'c8b62afe-4bbb-4319-8f47-0989f39c0945', 'wa_daysalary@@@salarydate',0, 'wa_daysalary', '2018-10-28 16:13:15' ) 
