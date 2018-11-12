
delete md_association where ( componentid in ('83c64bb1-ec47-47aa-bdf2-0c9acf9e8ba6') and (dr=0 or dr is null) )
go

delete md_property where ( classid in ('57865653-4b0e-4ab4-ac76-6686a44359cc') and (dr=0 or dr is null) )
go

delete md_column where ( tableid in ('tbm_extrarest') and (dr=0 or dr is null) )
go

delete md_table where ( id in ('tbm_extrarest') and (dr=0 or dr is null) )
go

delete md_ormap where ( classid in ('57865653-4b0e-4ab4-ac76-6686a44359cc') and (dr=0 or dr is null) )
go

delete md_bizitfmap where ( classid in ('57865653-4b0e-4ab4-ac76-6686a44359cc') and (dr=0 or dr is null) )
go

delete md_accessorpara where ( id in ('57865653-4b0e-4ab4-ac76-6686a44359cc') and (dr=0 or dr is null) )
go

delete md_class where ( componentid in ('83c64bb1-ec47-47aa-bdf2-0c9acf9e8ba6') and (dr=0 or dr is null) )
go

delete md_component where ( id='83c64bb1-ec47-47aa-bdf2-0c9acf9e8ba6' and (versiontype='0') and (dr=0 or dr is null) )
go


insert into md_component(createtime,creator,description,displayname,dr,fromsourcebmf,help,id,industry,isbizmodel,modifier,modifytime,name,namespace,ownmodule,preload,resid,resmodule,ts,version,versiontype) values( '2018-09-11 14:25:14',null,null,'外加补休天数',0,'Y',null,'83c64bb1-ec47-47aa-bdf2-0c9acf9e8ba6','0','N','YONYOU NC','2018-09-11 18:30:37','leaveextrarest','hrta','hrta',null,null,'6017leaveextrarest','2018-09-11 18:32:39','13',0)
go


insert into md_class(accessorclassname,bizitfimpclassname,classtype,componentid,createtime,creator,defaulttablename,description,displayname,dr,fixedlength,fullclassname,help,id,industry,isactive,isauthen,iscreatesql,isextendbean,isprimary,keyattribute,modifier,modifytime,modinfoclassname,name,parentclassid,precise,refmodelname,resid,returntype,stereotype,ts,userdefclassname,versiontype) values( 'nc.md.model.access.javamap.AggVOStyle',null,201,'83c64bb1-ec47-47aa-bdf2-0c9acf9e8ba6','2018-09-11 14:25:20',null,'tbm_extrarest',null,'外加补休天数',0,null,'nc.vo.ta.leaveextrarest.LeaveExtraRestVO',null,'57865653-4b0e-4ab4-ac76-6686a44359cc','0',null,'Y','Y',null,'Y','0446501c-3e18-47e6-9ebb-b36a1cb82765','YONYOU NC','2018-09-11 18:32:31',null,'leaveextrarest',null,null,null,null,null,null,'2018-09-11 18:32:39',null,0)
go


insert into md_accessorpara(assosequence,dr,id,industry,paravalue,ts,versiontype) values( 0,0,'57865653-4b0e-4ab4-ac76-6686a44359cc',null,'nc.vo.ta.leaveextrarest.AggLeaveExtraRestVO','2018-09-11 18:32:39',null)
go


insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '2b226ae1-aad9-4abe-a405-36bcacaa94a8',null,'f16b64df-5613-4f2a-b8d3-68efa450bef5',null,'57865653-4b0e-4ab4-ac76-6686a44359cc',0,null,'0abfd569-b9cf-4c44-a8d9-45979c083b08','2018-09-11 18:32:39',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( 'ede1e93f-a86e-4588-b441-9db78f30219f',null,'608334fd-9da4-4e6c-abbe-6e335c5cd1a7',null,'57865653-4b0e-4ab4-ac76-6686a44359cc',0,null,'0c37faa9-6e38-4e27-b596-dabc8d5ad732','2018-09-11 18:32:39',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( 'cc8a1739-1dca-454c-b8ef-b175b79a3426',null,'e51e3cbe-0db2-419c-9341-e9b7c8863cea',null,'57865653-4b0e-4ab4-ac76-6686a44359cc',0,null,'2d2ee0e2-05b9-4bf5-b263-a39796d4f26b','2018-09-11 18:32:39',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '2b226ae1-aad9-4abe-a405-36bcacaa94a8',null,'e51e3cbe-0db2-419c-9341-e9b7c8863cea',null,'57865653-4b0e-4ab4-ac76-6686a44359cc',0,null,'49b6b1ae-cd5b-4b54-99cd-a6bb9fee7343','2018-09-11 18:32:39',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '2b226ae1-aad9-4abe-a405-36bcacaa94a8',null,'4aa9c715-5f6d-42a0-9a1d-8d1b192ec273',null,'57865653-4b0e-4ab4-ac76-6686a44359cc',0,null,'54c42368-5a43-4295-a7d4-74d276eed522','2018-09-11 18:32:39',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '6c8722b9-911a-489b-8d0d-18bd3734fcf6',null,'0446501c-3e18-47e6-9ebb-b36a1cb82765',null,'57865653-4b0e-4ab4-ac76-6686a44359cc',0,null,'5dd3c721-22ad-42b1-9c10-4351c236bc77','2018-09-11 18:32:39',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( 'ede1e93f-a86e-4588-b441-9db78f30219f',null,'a0794181-0271-4ded-8b46-773614869bfe',null,'57865653-4b0e-4ab4-ac76-6686a44359cc',0,null,'72145ab5-b7fa-448e-ad62-8e13cd547191','2018-09-11 18:32:39',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '6c8722b9-911a-489b-8d0d-18bd3734fcf6',null,null,null,'57865653-4b0e-4ab4-ac76-6686a44359cc',0,null,'89578a97-42fe-439b-827c-8eabd9e3604c','2018-09-11 18:32:39',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '6c8722b9-911a-489b-8d0d-18bd3734fcf6',null,'3d652b04-c21a-4dab-bf3c-12844be974f9',null,'57865653-4b0e-4ab4-ac76-6686a44359cc',0,null,'a47e6cda-09e4-480b-923f-ec6f41e3e06c','2018-09-11 18:32:39',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '6c8722b9-911a-489b-8d0d-18bd3734fcf6',null,null,null,'57865653-4b0e-4ab4-ac76-6686a44359cc',0,null,'c8334364-7ab9-4266-8d4b-e74537935e46','2018-09-11 18:32:39',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '6c8722b9-911a-489b-8d0d-18bd3734fcf6',null,null,null,'57865653-4b0e-4ab4-ac76-6686a44359cc',0,null,'d32cc17b-f415-415a-923f-0764443eb102','2018-09-11 18:32:39',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( 'cc8a1739-1dca-454c-b8ef-b175b79a3426',null,'f16b64df-5613-4f2a-b8d3-68efa450bef5',null,'57865653-4b0e-4ab4-ac76-6686a44359cc',0,null,'da02e89c-df72-4c8c-bfb4-c3ba94a7268c','2018-09-11 18:32:39',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '2b226ae1-aad9-4abe-a405-36bcacaa94a8',null,'067f0ea2-1777-4496-88e9-c163913e72d1',null,'57865653-4b0e-4ab4-ac76-6686a44359cc',0,null,'e5a2aef6-8d58-416a-b8b7-4dd10e13d788','2018-09-11 18:32:39',null)
go
insert into md_bizitfmap(bizinterfaceid,bizitfimpclassname,classattrid,classattrpath,classid,dr,industry,intattrid,ts,versiontype) values( '6c8722b9-911a-489b-8d0d-18bd3734fcf6',null,'a0794181-0271-4ded-8b46-773614869bfe',null,'57865653-4b0e-4ab4-ac76-6686a44359cc',0,null,'ecf1b76a-6e44-42e2-a55e-87596504775b','2018-09-11 18:32:39',null)
go


insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '0446501c-3e18-47e6-9ebb-b36a1cb82765','57865653-4b0e-4ab4-ac76-6686a44359cc','tbm_extrarest@@PK@@',0,'tbm_extrarest','2018-09-11 18:32:39')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '067f0ea2-1777-4496-88e9-c163913e72d1','57865653-4b0e-4ab4-ac76-6686a44359cc','tbm_extrarest@@@creator',0,'tbm_extrarest','2018-09-11 18:32:39')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '11a8fbaa-2eaa-4229-b9b9-9a79a7082ca2','57865653-4b0e-4ab4-ac76-6686a44359cc','tbm_extrarest@@@afterchange',0,'tbm_extrarest','2018-09-11 18:32:39')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '15420239-9121-4101-8c93-c3652b867d5b','57865653-4b0e-4ab4-ac76-6686a44359cc','tbm_extrarest@@@pk_psndoc',0,'tbm_extrarest','2018-09-11 18:32:39')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '3d652b04-c21a-4dab-bf3c-12844be974f9','57865653-4b0e-4ab4-ac76-6686a44359cc','tbm_extrarest@@@pk_group',0,'tbm_extrarest','2018-09-11 18:32:39')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '4aa9c715-5f6d-42a0-9a1d-8d1b192ec273','57865653-4b0e-4ab4-ac76-6686a44359cc','tbm_extrarest@@@modifier',0,'tbm_extrarest','2018-09-11 18:32:39')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '4f069fcc-224c-4e4a-ba0a-52584413b5a9','57865653-4b0e-4ab4-ac76-6686a44359cc','tbm_extrarest@@@changedayorhour',0,'tbm_extrarest','2018-09-11 18:32:39')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '5bf0698e-c3f6-4d70-979f-0fa8db350df6','57865653-4b0e-4ab4-ac76-6686a44359cc','tbm_extrarest@@@billdate',0,'tbm_extrarest','2018-09-11 18:32:39')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( '608334fd-9da4-4e6c-abbe-6e335c5cd1a7','57865653-4b0e-4ab4-ac76-6686a44359cc','tbm_extrarest@@@pk_org_v',0,'tbm_extrarest','2018-09-11 18:32:39')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'a0794181-0271-4ded-8b46-773614869bfe','57865653-4b0e-4ab4-ac76-6686a44359cc','tbm_extrarest@@@pk_org',0,'tbm_extrarest','2018-09-11 18:32:39')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'bff66084-89eb-4984-b043-8d225ad165a1','57865653-4b0e-4ab4-ac76-6686a44359cc','tbm_extrarest@@@beforechange',0,'tbm_extrarest','2018-09-11 18:32:39')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'e07f1daf-a8e4-44d4-b796-58f64a54916e','57865653-4b0e-4ab4-ac76-6686a44359cc','tbm_extrarest@@@pk_dept_v',0,'tbm_extrarest','2018-09-11 18:32:39')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'e51e3cbe-0db2-419c-9341-e9b7c8863cea','57865653-4b0e-4ab4-ac76-6686a44359cc','tbm_extrarest@@@creationtime',0,'tbm_extrarest','2018-09-11 18:32:39')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'f16b64df-5613-4f2a-b8d3-68efa450bef5','57865653-4b0e-4ab4-ac76-6686a44359cc','tbm_extrarest@@@modifiedtime',0,'tbm_extrarest','2018-09-11 18:32:39')
go
insert into md_ormap(attributeid,classid,columnid,dr,tableid,ts) values( 'feba05b6-f128-4792-890a-da7ba16c05a2','57865653-4b0e-4ab4-ac76-6686a44359cc','tbm_extrarest@@@changetype',0,'tbm_extrarest','2018-09-11 18:32:39')
go


insert into md_table(createtime,creator,databaseid,description,displayname,dr,fromsourcebmf,help,id,industry,isactive,isextendtable,modifier,modifytime,name,parenttable,resid,resmodule,ts,versiontype) values( '2018-09-11 14:25:20',null,null,null,'外加补休天数',0,'Y',null,'tbm_extrarest','0',null,null,'YONYOU NC','2018-09-11 18:32:31','tbm_extrarest',null,null,'6017leaveextrarest','2018-09-11 18:32:39',0)
go


insert into md_column(columnlength,columnsequence,columntype,createtime,creator,defaultvalue,description,displayname,dr,forlocale,groupid,help,id,identitied,incrementseed,incrementstep,isactive,modifier,modifytime,name,nullable,pkey,precise,resid,sqldatetype,tableid,ts,versiontype) values( 50,12,0,null,null,null,null,'变动后日历天',0,'N',null,null,'tbm_extrarest@@@afterchange',null,null,null,'Y',null,null,'afterchange','Y','N',0,null,'varchar','tbm_extrarest','2018-09-11 18:32:39',0)
go
insert into md_column(columnlength,columnsequence,columntype,createtime,creator,defaultvalue,description,displayname,dr,forlocale,groupid,help,id,identitied,incrementseed,incrementstep,isactive,modifier,modifytime,name,nullable,pkey,precise,resid,sqldatetype,tableid,ts,versiontype) values( 50,11,0,null,null,null,null,'变动前日历天',0,'N',null,null,'tbm_extrarest@@@beforechange',null,null,null,'Y',null,null,'beforechange','Y','N',0,null,'varchar','tbm_extrarest','2018-09-11 18:32:39',0)
go
insert into md_column(columnlength,columnsequence,columntype,createtime,creator,defaultvalue,description,displayname,dr,forlocale,groupid,help,id,identitied,incrementseed,incrementstep,isactive,modifier,modifytime,name,nullable,pkey,precise,resid,sqldatetype,tableid,ts,versiontype) values( 19,7,0,null,null,null,null,'单据日期',0,'N',null,null,'tbm_extrarest@@@billdate',null,null,null,'Y',null,null,'billdate','Y','N',0,null,'char','tbm_extrarest','2018-09-11 18:32:39',0)
go
insert into md_column(columnlength,columnsequence,columntype,createtime,creator,defaultvalue,description,displayname,dr,forlocale,groupid,help,id,identitied,incrementseed,incrementstep,isactive,modifier,modifytime,name,nullable,pkey,precise,resid,sqldatetype,tableid,ts,versiontype) values( 28,14,0,null,null,null,null,'变动外加补休时天数',0,'N',null,null,'tbm_extrarest@@@changedayorhour',null,null,null,'Y',null,null,'changedayorhour','Y','N',8,null,'decimal','tbm_extrarest','2018-09-11 18:32:39',0)
go
insert into md_column(columnlength,columnsequence,columntype,createtime,creator,defaultvalue,description,displayname,dr,forlocale,groupid,help,id,identitied,incrementseed,incrementstep,isactive,modifier,modifytime,name,nullable,pkey,precise,resid,sqldatetype,tableid,ts,versiontype) values( 50,13,0,null,null,null,null,'补休产生类型',0,'N',null,null,'tbm_extrarest@@@changetype',null,null,null,'Y',null,null,'changetype','Y','N',0,null,'varchar','tbm_extrarest','2018-09-11 18:32:39',0)
go
insert into md_column(columnlength,columnsequence,columntype,createtime,creator,defaultvalue,description,displayname,dr,forlocale,groupid,help,id,identitied,incrementseed,incrementstep,isactive,modifier,modifytime,name,nullable,pkey,precise,resid,sqldatetype,tableid,ts,versiontype) values( 19,8,0,null,null,null,null,'创建时间',0,'N',null,null,'tbm_extrarest@@@creationtime',null,null,null,'Y',null,null,'creationtime','Y','N',0,'2UC000-000157','char','tbm_extrarest','2018-09-11 18:32:39',0)
go
insert into md_column(columnlength,columnsequence,columntype,createtime,creator,defaultvalue,description,displayname,dr,forlocale,groupid,help,id,identitied,incrementseed,incrementstep,isactive,modifier,modifytime,name,nullable,pkey,precise,resid,sqldatetype,tableid,ts,versiontype) values( 20,6,0,null,null,'*~',null,'创建人',0,'N',null,null,'tbm_extrarest@@@creator',null,null,null,'Y',null,null,'creator','Y','N',0,'2UC000-000155','varchar','tbm_extrarest','2018-09-11 18:32:39',0)
go
insert into md_column(columnlength,columnsequence,columntype,createtime,creator,defaultvalue,description,displayname,dr,forlocale,groupid,help,id,identitied,incrementseed,incrementstep,isactive,modifier,modifytime,name,nullable,pkey,precise,resid,sqldatetype,tableid,ts,versiontype) values( 19,10,0,null,null,null,null,'修改时间',0,'N',null,null,'tbm_extrarest@@@modifiedtime',null,null,null,'Y',null,null,'modifiedtime','Y','N',0,'2UC000-000100','char','tbm_extrarest','2018-09-11 18:32:39',0)
go
insert into md_column(columnlength,columnsequence,columntype,createtime,creator,defaultvalue,description,displayname,dr,forlocale,groupid,help,id,identitied,incrementseed,incrementstep,isactive,modifier,modifytime,name,nullable,pkey,precise,resid,sqldatetype,tableid,ts,versiontype) values( 20,9,0,null,null,'*~',null,'修改人',0,'N',null,null,'tbm_extrarest@@@modifier',null,null,null,'Y',null,null,'modifier','Y','N',0,'2UC000-000098','varchar','tbm_extrarest','2018-09-11 18:32:39',0)
go
insert into md_column(columnlength,columnsequence,columntype,createtime,creator,defaultvalue,description,displayname,dr,forlocale,groupid,help,id,identitied,incrementseed,incrementstep,isactive,modifier,modifytime,name,nullable,pkey,precise,resid,sqldatetype,tableid,ts,versiontype) values( 20,3,0,null,null,'*~',null,'人员部门版本',0,'N',null,null,'tbm_extrarest@@@pk_dept_v',null,null,null,'Y',null,null,'pk_dept_v','Y','N',0,'2psndoc-000016','varchar','tbm_extrarest','2018-09-11 18:32:39',0)
go
insert into md_column(columnlength,columnsequence,columntype,createtime,creator,defaultvalue,description,displayname,dr,forlocale,groupid,help,id,identitied,incrementseed,incrementstep,isactive,modifier,modifytime,name,nullable,pkey,precise,resid,sqldatetype,tableid,ts,versiontype) values( 20,4,0,null,null,'*~',null,'集团主键',0,'N',null,null,'tbm_extrarest@@@pk_group',null,null,null,'Y',null,null,'pk_group','Y','N',0,'2UC000-001008','varchar','tbm_extrarest','2018-09-11 18:32:39',0)
go
insert into md_column(columnlength,columnsequence,columntype,createtime,creator,defaultvalue,description,displayname,dr,forlocale,groupid,help,id,identitied,incrementseed,incrementstep,isactive,modifier,modifytime,name,nullable,pkey,precise,resid,sqldatetype,tableid,ts,versiontype) values( 20,5,0,null,null,'*~',null,'组织主键',0,'N',null,null,'tbm_extrarest@@@pk_org',null,null,null,'Y',null,null,'pk_org','Y','N',0,'2UC000-000694','varchar','tbm_extrarest','2018-09-11 18:32:39',0)
go
insert into md_column(columnlength,columnsequence,columntype,createtime,creator,defaultvalue,description,displayname,dr,forlocale,groupid,help,id,identitied,incrementseed,incrementstep,isactive,modifier,modifytime,name,nullable,pkey,precise,resid,sqldatetype,tableid,ts,versiontype) values( 20,2,0,null,null,'*~',null,'人员组织版本',0,'N',null,null,'tbm_extrarest@@@pk_org_v',null,null,null,'Y',null,null,'pk_org_v','Y','N',0,'2psndoc-000014','varchar','tbm_extrarest','2018-09-11 18:32:39',0)
go
insert into md_column(columnlength,columnsequence,columntype,createtime,creator,defaultvalue,description,displayname,dr,forlocale,groupid,help,id,identitied,incrementseed,incrementstep,isactive,modifier,modifytime,name,nullable,pkey,precise,resid,sqldatetype,tableid,ts,versiontype) values( 20,1,0,null,null,'*~',null,'人员基本信息',0,'N',null,null,'tbm_extrarest@@@pk_psndoc',null,null,null,'Y',null,null,'pk_psndoc','Y','N',0,'2psndoc-000018','varchar','tbm_extrarest','2018-09-11 18:32:39',0)
go
insert into md_column(columnlength,columnsequence,columntype,createtime,creator,defaultvalue,description,displayname,dr,forlocale,groupid,help,id,identitied,incrementseed,incrementstep,isactive,modifier,modifytime,name,nullable,pkey,precise,resid,sqldatetype,tableid,ts,versiontype) values( 20,0,0,null,null,null,null,'外加补休天数主键',0,'N',null,null,'tbm_extrarest@@PK@@',null,null,null,'Y',null,null,'pk_extrarest','N','Y',0,null,'char','tbm_extrarest','2018-09-11 18:32:39',0)
go


insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,20,null,null,0,'N','57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'N','BS000010000100001051',300,null,null,'外加补休天数主键',0,'N',null,'N',null,'N','0446501c-3e18-47e6-9ebb-b36a1cb82765','0','Y','Y',null,null,'pk_extrarest','N','N',0,'N',null,null,'2018-09-11 18:32:39',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,20,null,null,6,'N','57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'N','f6f9a473-56c0-432f-8bc7-fbf8fde54fee',305,null,null,'创建人',0,'N',null,'N',null,'N','067f0ea2-1777-4496-88e9-c163913e72d1','0','Y','Y',null,null,'creator','N','Y',0,'N','用户','2UC000-000155','2018-09-11 18:32:39',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,50,null,null,12,'N','57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'N','BS000010000100001001',300,null,null,'变动后日历天',0,'N',null,'N',null,'N','11a8fbaa-2eaa-4229-b9b9-9a79a7082ca2','0','Y','Y',null,null,'afterchange','N','Y',0,'N',null,null,'2018-09-11 18:32:39',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,20,null,null,1,'N','57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'N','40d39c26-a2b6-4f16-a018-45664cac1a1f',305,null,null,'人员基本信息',0,'N',null,'N',null,'N','15420239-9121-4101-8c93-c3652b867d5b','0','Y','Y',null,null,'pk_psndoc','N','Y',0,'N','人员','2psndoc-000018','2018-09-11 18:32:39',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,20,null,null,4,'N','57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'N','3b6dd171-2900-47f3-bfbe-41e4483a2a65',305,null,null,'集团主键',0,'N',null,'N',null,'N','3d652b04-c21a-4dab-bf3c-12844be974f9','0','Y','Y',null,null,'pk_group','N','Y',0,'N','集团','2UC000-001008','2018-09-11 18:32:39',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,20,null,null,9,'N','57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'N','f6f9a473-56c0-432f-8bc7-fbf8fde54fee',305,null,null,'修改人',0,'N',null,'N',null,'N','4aa9c715-5f6d-42a0-9a1d-8d1b192ec273','0','Y','Y',null,null,'modifier','N','Y',0,'N','用户','2UC000-000098','2018-09-11 18:32:39',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,28,null,null,14,'N','57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'N','BS000010000100001031',300,null,null,'变动外加补休时天数',0,'N',null,'N',null,'N','4f069fcc-224c-4e4a-ba0a-52584413b5a9','0','Y','Y',null,null,'changedayorhour','N','Y',8,'N',null,null,'2018-09-11 18:32:39',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,19,null,null,7,'N','57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'N','BS000010000100001033',300,null,null,'单据日期',0,'N',null,'N',null,'N','5bf0698e-c3f6-4d70-979f-0fa8db350df6','0','Y','Y',null,null,'billdate','N','Y',0,'N',null,null,'2018-09-11 18:32:39',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,20,null,null,2,'N','57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'N','fa902409-20fe-4c49-89ee-85ee9312174b',305,null,null,'人员组织版本',0,'N',null,'N',null,'N','608334fd-9da4-4e6c-abbe-6e335c5cd1a7','0','Y','Y',null,null,'pk_org_v','N','Y',0,'N','行政组织版本','2psndoc-000014','2018-09-11 18:32:39',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,20,null,null,5,'N','57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'N','f3fed5ea-72f2-4a0a-ad43-d30b1c22c86c',305,null,null,'组织主键',0,'N',null,'N',null,'N','a0794181-0271-4ded-8b46-773614869bfe','0','Y','Y',null,null,'pk_org','N','Y',0,'N','人力资源组织','2UC000-000694','2018-09-11 18:32:39',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,50,null,null,11,'N','57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'N','BS000010000100001001',300,null,null,'变动前日历天',0,'N',null,'N',null,'N','bff66084-89eb-4984-b043-8d225ad165a1','0','Y','Y',null,null,'beforechange','N','Y',0,'N',null,null,'2018-09-11 18:32:39',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,20,null,null,3,'N','57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'N','66ed0cf6-e260-4f39-8fbb-172260efd677',305,null,null,'人员部门版本',0,'N',null,'N',null,'N','e07f1daf-a8e4-44d4-b796-58f64a54916e','0','Y','Y',null,null,'pk_dept_v','N','Y',0,'N','部门版本','2psndoc-000016','2018-09-11 18:32:39',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,19,null,null,8,'N','57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'N','BS000010000100001034',300,null,null,'创建时间',0,'N',null,'N',null,'N','e51e3cbe-0db2-419c-9341-e9b7c8863cea','0','Y','Y',null,null,'creationtime','N','Y',0,'N',null,'2UC000-000157','2018-09-11 18:32:39',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,19,null,null,10,'N','57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'N','BS000010000100001034',300,null,null,'修改时间',0,'N',null,'N',null,'N','f16b64df-5613-4f2a-b8d3-68efa450bef5','0','Y','Y',null,null,'modifiedtime','N','Y',0,'N',null,'2UC000-000100','2018-09-11 18:32:39',0,0)
go
insert into md_property(accessorclassname,accesspower,accesspowergroup,attrlength,attrmaxvalue,attrminvalue,attrsequence,calculation,classid,createindustry,createtime,creator,customattr,datatype,datatypestyle,defaultvalue,description,displayname,dr,dynamicattr,dynamictable,fixedlength,help,hided,id,industry,isactive,isauthen,modifier,modifytime,name,notserialize,nullable,precise,readonly,refmodelname,resid,ts,versiontype,visibility) values( null,'N',null,50,null,null,13,'N','57865653-4b0e-4ab4-ac76-6686a44359cc',null,null,null,'N','BS000010000100001001',300,null,null,'补休产生类型',0,'N',null,'N',null,'N','feba05b6-f128-4792-890a-da7ba16c05a2','0','Y','Y',null,null,'changetype','N','Y',0,'N',null,null,'2018-09-11 18:32:39',0,0)
go


insert into md_association(cascadedelete,cascadeupdate,componentid,createtime,creator,dr,endcardinality,endelementid,id,industry,isactive,modifier,modifytime,name,startbeanid,startcardinality,startelementid,ts,type,versiontype) values( null,null,'83c64bb1-ec47-47aa-bdf2-0c9acf9e8ba6',null,null,0,'1','40d39c26-a2b6-4f16-a018-45664cac1a1f','27f4e197-a550-4346-80f5-956a5e1030f8','0','Y',null,null,'leaveextrarest_pk_psndoc','57865653-4b0e-4ab4-ac76-6686a44359cc','1','15420239-9121-4101-8c93-c3652b867d5b','2018-09-11 18:32:39',3,0)
go
insert into md_association(cascadedelete,cascadeupdate,componentid,createtime,creator,dr,endcardinality,endelementid,id,industry,isactive,modifier,modifytime,name,startbeanid,startcardinality,startelementid,ts,type,versiontype) values( null,null,'83c64bb1-ec47-47aa-bdf2-0c9acf9e8ba6',null,null,0,'1','3b6dd171-2900-47f3-bfbe-41e4483a2a65','44262d4b-601a-43a8-9be0-046bc46762cb','0','Y',null,null,'leaveextrarest_pk_group','57865653-4b0e-4ab4-ac76-6686a44359cc','1','3d652b04-c21a-4dab-bf3c-12844be974f9','2018-09-11 18:32:39',3,0)
go
insert into md_association(cascadedelete,cascadeupdate,componentid,createtime,creator,dr,endcardinality,endelementid,id,industry,isactive,modifier,modifytime,name,startbeanid,startcardinality,startelementid,ts,type,versiontype) values( null,null,'83c64bb1-ec47-47aa-bdf2-0c9acf9e8ba6',null,null,0,'1','66ed0cf6-e260-4f39-8fbb-172260efd677','6daeb1b6-c5fe-4858-a9f0-bba3d5f5d194','0','Y',null,null,'leaveextrarest_pk_dept_v','57865653-4b0e-4ab4-ac76-6686a44359cc','1','e07f1daf-a8e4-44d4-b796-58f64a54916e','2018-09-11 18:32:39',3,0)
go
insert into md_association(cascadedelete,cascadeupdate,componentid,createtime,creator,dr,endcardinality,endelementid,id,industry,isactive,modifier,modifytime,name,startbeanid,startcardinality,startelementid,ts,type,versiontype) values( null,null,'83c64bb1-ec47-47aa-bdf2-0c9acf9e8ba6',null,null,0,'1','f6f9a473-56c0-432f-8bc7-fbf8fde54fee','76abbdcf-8ded-4482-855a-3a53533e7358','0','Y',null,null,'leaveextrarest_creator','57865653-4b0e-4ab4-ac76-6686a44359cc','1','067f0ea2-1777-4496-88e9-c163913e72d1','2018-09-11 18:32:39',3,0)
go
insert into md_association(cascadedelete,cascadeupdate,componentid,createtime,creator,dr,endcardinality,endelementid,id,industry,isactive,modifier,modifytime,name,startbeanid,startcardinality,startelementid,ts,type,versiontype) values( null,null,'83c64bb1-ec47-47aa-bdf2-0c9acf9e8ba6',null,null,0,'1','f6f9a473-56c0-432f-8bc7-fbf8fde54fee','8756ad6b-3d66-42c5-bc94-7188e00d19ff','0','Y',null,null,'leaveextrarest_modifier','57865653-4b0e-4ab4-ac76-6686a44359cc','1','4aa9c715-5f6d-42a0-9a1d-8d1b192ec273','2018-09-11 18:32:39',3,0)
go
insert into md_association(cascadedelete,cascadeupdate,componentid,createtime,creator,dr,endcardinality,endelementid,id,industry,isactive,modifier,modifytime,name,startbeanid,startcardinality,startelementid,ts,type,versiontype) values( null,null,'83c64bb1-ec47-47aa-bdf2-0c9acf9e8ba6',null,null,0,'1','fa902409-20fe-4c49-89ee-85ee9312174b','d81ce37f-7c10-4cab-9414-9c417bb814ea','0','Y',null,null,'leaveextrarest_pk_org_v','57865653-4b0e-4ab4-ac76-6686a44359cc','1','608334fd-9da4-4e6c-abbe-6e335c5cd1a7','2018-09-11 18:32:39',3,0)
go
insert into md_association(cascadedelete,cascadeupdate,componentid,createtime,creator,dr,endcardinality,endelementid,id,industry,isactive,modifier,modifytime,name,startbeanid,startcardinality,startelementid,ts,type,versiontype) values( null,null,'83c64bb1-ec47-47aa-bdf2-0c9acf9e8ba6',null,null,0,'1','f3fed5ea-72f2-4a0a-ad43-d30b1c22c86c','e99dd1ad-ba3e-48cc-8208-33b835e5bf4b','0','Y',null,null,'leaveextrarest_pk_org','57865653-4b0e-4ab4-ac76-6686a44359cc','1','a0794181-0271-4ded-8b46-773614869bfe','2018-09-11 18:32:39',3,0)
go
