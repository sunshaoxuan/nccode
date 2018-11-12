
delete pub_billtemplet_t where ( pk_billtemplet in ('0001AA1000000005B7HO') and (dr=0 or dr is null) );


delete pub_billtemplet_b where ( pk_billtemplet in ('0001AA1000000005B7HO') and (dr=0 or dr is null) );


delete pub_billtemplet where ( pk_billtemplet='0001AA1000000005B7HO' and (bill_templetname='SYSTEM') and (dr=0 or dr is null) );



insert into pub_billtemplet(bill_templetcaption,bill_templetname,devorg,dividerproportion,dr,funccode,layer,metadataclass,model_type,modulecode,nodecode,options,pk_billtemplet,pk_billtypecode,pk_corp,pk_org,resid,shareflag,ts,validateformula) values( '外加补休天数','SYSTEM',null,null,0,null,0,'hrta.leaveextrarest',null,'6017','6017leaveextrarest',null,'0001AA1000000005B7HO','6017leavee','@@@@',null,null,null,'2018-09-11 18:14:17',null);



insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'pk_extrarest',0,'N',1,'N','N',1,null,0,'pk_extrarest','hrta.leaveextrarest.pk_extrarest',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7HP','@@@@',0,null,null,null,'N',0,1,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);

insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'pk_psndoc',0,'N',1,'N','Y',2,null,0,'pk_psndoc','hrta.leaveextrarest.pk_psndoc',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7HQ','@@@@',0,null,null,null,'N',1,2,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);

insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'pk_org_v',0,'N',1,'N','Y',3,null,0,'pk_org_v','hrta.leaveextrarest.pk_org_v',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7HR','@@@@',0,null,null,null,'N',1,3,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);

insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'pk_dept_v',0,'N',1,'N','Y',4,null,0,'pk_dept_v','hrta.leaveextrarest.pk_dept_v',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7HS','@@@@',0,null,null,null,'N',1,4,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);

insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'pk_group',0,'N',1,'N','Y',5,null,0,'pk_group','hrta.leaveextrarest.pk_group',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7HT','@@@@',0,null,null,null,'N',1,5,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);

insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'pk_org',0,'N',1,'N','Y',6,null,0,'pk_org','hrta.leaveextrarest.pk_org',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7HU','@@@@',0,null,null,null,'N',1,6,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);

insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'creator',0,'N',1,'N','Y',7,null,0,'creator','hrta.leaveextrarest.creator',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7HV','@@@@',0,null,null,null,'N',1,7,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);

insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'billdate',0,'N',1,'N','Y',8,null,0,'billdate','hrta.leaveextrarest.billdate',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7HW','@@@@',0,null,null,null,'N',1,8,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);

insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'creationtime',0,'N',1,'N','Y',9,null,0,'creationtime','hrta.leaveextrarest.creationtime',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7HX','@@@@',0,null,null,null,'N',1,9,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);

insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'modifier',0,'N',1,'N','Y',10,null,0,'modifier','hrta.leaveextrarest.modifier',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7HY','@@@@',0,null,null,null,'N',1,10,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);

insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'modifiedtime',0,'N',1,'N','Y',11,null,0,'modifiedtime','hrta.leaveextrarest.modifiedtime',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7HZ','@@@@',0,null,null,null,'N',1,11,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);

insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'beforechange',0,'N',1,'N','Y',12,null,0,'beforechange','hrta.leaveextrarest.beforechange',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7I0','@@@@',0,null,null,null,'N',1,12,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);

insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'afterchange',0,'N',1,'N','Y',13,null,0,'afterchange','hrta.leaveextrarest.afterchange',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7I1','@@@@',0,null,null,null,'N',1,13,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);

insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'changetype',0,'N',1,'N','Y',14,null,0,'changetype','hrta.leaveextrarest.changetype',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7I2','@@@@',0,null,null,null,'N',1,14,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);

insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'changedayorhour',0,'N',1,'N','Y',15,null,0,'changedayorhour','hrta.leaveextrarest.changedayorhour',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7I3','@@@@',0,null,null,null,'N',1,15,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);

insert into pub_billtemplet_b(cardflag,datatype,defaultshowname,defaultvalue,dr,editflag,editformula,foreground,hyperlinkflag,idcolname,inputlength,itemkey,itemtype,leafflag,listflag,listhyperlinkflag,listshowflag,listshoworder,loadformula,lockflag,metadatapath,metadataproperty,metadatarelation,newlineflag,nullflag,options,pk_billtemplet,pk_billtemplet_b,pk_corp,pos,reftype,resid,resid_tabname,reviseflag,showflag,showorder,table_code,table_name,totalflag,ts,userdefflag,userdefine1,userdefine2,userdefine3,usereditflag,userflag,userreviseflag,usershowflag,validateformula,width) values( 1,-1,null,null,0,1,null,-1,'N',null,-1,'ts',0,'N',1,'N','N',16,null,0,'ts','hrta.leaveextrarest.ts',null,'N',0,null,'0001AA1000000005B7HO','0001AA1000000005B7I4','@@@@',0,null,null,null,'N',0,16,'leaveextrarest','外加补休天数',0,'2018-09-11 18:14:17','N',null,null,null,1,1,'N',1,null,1);



insert into pub_billtemplet_t(basetab,dr,metadataclass,metadatapath,mixindex,pk_billtemplet,pk_billtemplet_t,pk_layout,pos,position,resid,tabcode,tabindex,tabname,ts,vdef1,vdef2,vdef3) values( 'tailerInfo',0,'hrta.leaveextrarest','leaveextrarest',2,'0001AA1000000005B7HO','0001AA1000000005B7I5',null,2,null,null,'auditInfo',0,'审计信息','2018-09-11 18:14:18',null,null,null);

insert into pub_billtemplet_t(basetab,dr,metadataclass,metadatapath,mixindex,pk_billtemplet,pk_billtemplet_t,pk_layout,pos,position,resid,tabcode,tabindex,tabname,ts,vdef1,vdef2,vdef3) values( null,0,'hrta.leaveextrarest','leaveextrarest',null,'0001AA1000000005B7HO','0001AA1000000005B7I6',null,0,null,null,'leaveextrarest',0,'外加补休天数','2018-09-11 18:14:18',null,null,null);

