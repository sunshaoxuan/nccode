
delete pub_billaction where ( pk_billaction in ('0001ZZ10000000051GAY', '0001ZZ10000000051GAZ', '0001ZZ10000000051GB0', '0001ZZ10000000051GB1', '0001ZZ10000000051GB2', '0001ZZ10000000051GB3') and (dr=0 or dr is null) )
;


insert into pub_billaction(action_type,actionnote,actionnote2,actionnote3,actionnote4,actionnote5,actionnote6,actionstyle,actionstyleremark,actiontype,constrictflag,controlflag,dr,finishflag,pk_billaction,pk_billtype,pk_billtypeid,pushflag,showhint,showhint2,showhint3,showhint4,showhint5,showhint6,ts) values( 10,'送审',null,null,null,null,null,'1',null,'SAVE','N','N',null,'N','0001ZZ10000000051GAY','NHI1','0001ZZ10000000051GAX',null,null,null,null,null,null,null,'2018-09-26 10:47:49')
;
insert into pub_billaction(action_type,actionnote,actionnote2,actionnote3,actionnote4,actionnote5,actionnote6,actionstyle,actionstyleremark,actiontype,constrictflag,controlflag,dr,finishflag,pk_billaction,pk_billtype,pk_billtypeid,pushflag,showhint,showhint2,showhint3,showhint4,showhint5,showhint6,ts) values( 11,'审核',null,null,null,null,null,'2',null,'APPROVE','N','N',null,'N','0001ZZ10000000051GAZ','NHI1','0001ZZ10000000051GAX',null,null,null,null,null,null,null,'2018-09-26 10:47:49')
;
insert into pub_billaction(action_type,actionnote,actionnote2,actionnote3,actionnote4,actionnote5,actionnote6,actionstyle,actionstyleremark,actiontype,constrictflag,controlflag,dr,finishflag,pk_billaction,pk_billtype,pk_billtypeid,pushflag,showhint,showhint2,showhint3,showhint4,showhint5,showhint6,ts) values( 13,'收回',null,null,null,null,null,'3',null,'UNSAVEBILL','N','Y',null,'Y','0001ZZ10000000051GB0','NHI1','0001ZZ10000000051GAX',null,null,null,null,null,null,null,'2018-09-26 10:47:49')
;
insert into pub_billaction(action_type,actionnote,actionnote2,actionnote3,actionnote4,actionnote5,actionnote6,actionstyle,actionstyleremark,actiontype,constrictflag,controlflag,dr,finishflag,pk_billaction,pk_billtype,pk_billtypeid,pushflag,showhint,showhint2,showhint3,showhint4,showhint5,showhint6,ts) values( 12,'弃审',null,null,null,null,null,'3',null,'UNAPPROVE','N','N',null,'Y','0001ZZ10000000051GB1','NHI1','0001ZZ10000000051GAX',null,null,null,null,null,null,null,'2018-09-26 10:47:49')
;
insert into pub_billaction(action_type,actionnote,actionnote2,actionnote3,actionnote4,actionnote5,actionnote6,actionstyle,actionstyleremark,actiontype,constrictflag,controlflag,dr,finishflag,pk_billaction,pk_billtype,pk_billtypeid,pushflag,showhint,showhint2,showhint3,showhint4,showhint5,showhint6,ts) values( 30,'删除',null,null,null,null,null,'3',null,'DELETE','N','N',null,'N','0001ZZ10000000051GB2','NHI1','0001ZZ10000000051GAX',null,null,null,null,null,null,null,'2018-09-26 10:47:49')
;
insert into pub_billaction(action_type,actionnote,actionnote2,actionnote3,actionnote4,actionnote5,actionnote6,actionstyle,actionstyleremark,actiontype,constrictflag,controlflag,dr,finishflag,pk_billaction,pk_billtype,pk_billtypeid,pushflag,showhint,showhint2,showhint3,showhint4,showhint5,showhint6,ts) values( 31,'保存',null,null,null,null,null,'1',null,'SAVEBASE','Y','N',null,'N','0001ZZ10000000051GB3','NHI1','0001ZZ10000000051GAX',null,null,null,null,null,null,null,'2018-09-26 10:47:49')
;
