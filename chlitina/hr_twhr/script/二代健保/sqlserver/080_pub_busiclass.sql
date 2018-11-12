
delete pub_busiclass where ( pk_busiclass in ('0001ZZ10000000051GB4', '0001ZZ10000000051GB5', '0001ZZ10000000051GB6', '0001ZZ10000000051GB7', '0001ZZ10000000051GB8', '0001ZZ10000000051GB9') and (dr=0 or dr is null) )
;


insert into pub_busiclass(actiontype,classname,dr,isbefore,pk_billtype,pk_billtypeid,pk_busiclass,pk_businesstype,pk_group,ts) values( 'SAVE','N_NHI1_SAVE',0,'N','NHI1','0001ZZ10000000051GAX','0001ZZ10000000051GB4','~','~','2018-09-26 10:47:49')
;
insert into pub_busiclass(actiontype,classname,dr,isbefore,pk_billtype,pk_billtypeid,pk_busiclass,pk_businesstype,pk_group,ts) values( 'APPROVE','N_NHI1_APPROVE',0,'N','NHI1','0001ZZ10000000051GAX','0001ZZ10000000051GB5','~','~','2018-09-26 10:47:49')
;
insert into pub_busiclass(actiontype,classname,dr,isbefore,pk_billtype,pk_billtypeid,pk_busiclass,pk_businesstype,pk_group,ts) values( 'UNSAVEBILL','N_NHI1_UNSAVEBILL',0,'N','NHI1','0001ZZ10000000051GAX','0001ZZ10000000051GB6','~','~','2018-09-26 10:47:49')
;
insert into pub_busiclass(actiontype,classname,dr,isbefore,pk_billtype,pk_billtypeid,pk_busiclass,pk_businesstype,pk_group,ts) values( 'UNAPPROVE','N_NHI1_UNAPPROVE',0,'N','NHI1','0001ZZ10000000051GAX','0001ZZ10000000051GB7','~','~','2018-09-26 10:47:49')
;
insert into pub_busiclass(actiontype,classname,dr,isbefore,pk_billtype,pk_billtypeid,pk_busiclass,pk_businesstype,pk_group,ts) values( 'DELETE','N_NHI1_DELETE',0,'N','NHI1','0001ZZ10000000051GAX','0001ZZ10000000051GB8','~','~','2018-09-26 10:47:49')
;
insert into pub_busiclass(actiontype,classname,dr,isbefore,pk_billtype,pk_billtypeid,pk_busiclass,pk_businesstype,pk_group,ts) values( 'SAVEBASE','N_NHI1_SAVEBASE',0,'N','NHI1','0001ZZ10000000051GAX','0001ZZ10000000051GB9','~','~','2018-09-26 10:47:49')
;
