--PUB_SYSINITTEMP表的删除语句
delete  PUB_SYSINITTEMP WHERE INITCODE ='TWHR13';
--PUB_SYSINITTEMPnull



Insert into PUB_SYSINITTEMP
(AFTERCLASS,APPTAG,CHECKCLASS,DATACLASS,DATAORIGINFLAG,DEFAULTVALUE,DOMAINFLAG,DR,EDITCOMPONENTCTRLCLASS,GROUPCODE,GROUPNAME,INITCODE,INITNAME,MAINFLAG,MUTEXFLAG,ORGTYPECONVERTMODE,PARATYPE,PK_ORGTYPE,PK_REFINFO,PK_SYSINITTEMP,REMARK,SHOWFLAG,STATEFLAG,SYSFLAG,SYSINDEX,TS,VALUELIST,VALUETYPE)
Values(null,null,null,null,0,'N','68J6',null,null,'TWHR','台灣人力資源','TWHR13','承認年資是否影響年資計算','N',0,'HRORGTYPE00000000000','business','HRORGTYPE00000000000','~','1001ZZ10000000005LIZ','如果客戶維護的承認年資是需要併入按照年資起算日周期結算的規則進行計算，則需進行配置','Y',2,'N',0,'2018-10-05 17:43:00','Y/N',1);
