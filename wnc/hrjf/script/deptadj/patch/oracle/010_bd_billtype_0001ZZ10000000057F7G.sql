
delete bd_billtype where ( PK_BILLTYPEID='0001ZZ10000000057F7G' and (dr=0 or dr is null) )
/


insert into bd_billtype(accountclass,billcoderule,billstyle,billtypename,billtypename2,billtypename3,billtypename4,billtypename5,billtypename6,canextendtransaction,checkclassname,classname,comp,component,datafinderclz,def1,def2,def3,dr,emendenumclass,forwardbilltype,isaccount,isapprovebill,isbizflowbill,iseditableproperty,isenablebutton,isenabletranstypebcr,islock,isroot,istransaction,ncbrcode,nodecode,parentbilltype,pk_billtypecode,pk_billtypeid,pk_group,pk_org,referclassname,systemcode,transtype_class,ts,webnodecode,wherestring) values( null,null,null,'6005',null,null,null,null,null,'Y',null,null,null,'deptadj',null,null,null,null,null,null,null,null,'Y',null,null,null,null,null,null,'N',null,'60050deptadj',null,'6005','0001ZZ10000000057F7G',null,'GLOBLE00000000000000',null,'HRJF',null,'2018-11-06 12:34:17',null,null)
/
