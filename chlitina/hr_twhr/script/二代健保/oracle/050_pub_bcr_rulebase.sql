
delete pub_bcr_rulebase where ( pk_billcodebase in ('0001ZZ10000000051GBB') and (dr=0 or dr is null) )
;


insert into pub_bcr_rulebase(codemode,codescope,dataoriginflag,dr,format,isautofill,isdefault,iseditable,isgetpk,islenvar,isused,nbcrcode,pk_billcodebase,pk_group,rulecode,rulename,rulename2,rulename3,rulename4,rulename5,rulename6,ts) values( 'after','g',0,0,'yyyyMMdd','Y','N','N','N','Y','Y','NHI1','0001ZZ10000000051GBB','GLOBLE00000000000000','NHI1','NHI1',null,null,null,null,null,'2018-09-26 10:47:49')
;
