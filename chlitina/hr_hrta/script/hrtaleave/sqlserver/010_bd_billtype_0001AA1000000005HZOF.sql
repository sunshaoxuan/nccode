
delete bd_billtype where ( pk_billtypeid='0001AA1000000005HZOF' and (dr=0 or dr is null) )
go


insert into bd_billtype(accountclass,billcoderule,billstyle,billtypename,billtypename2,billtypename3,billtypename4,billtypename5,billtypename6,canextendtransaction,checkclassname,classname,comp,component,datafinderclz,def1,def2,def3,dr,emendenumclass,forwardbilltype,isaccount,isapprovebill,isbizflowbill,iseditableproperty,isenablebutton,isenabletranstypebcr,islock,isroot,istransaction,ncbrcode,nodecode,parentbilltype,pk_billtypecode,pk_billtypeid,pk_group,pk_org,referclassname,systemcode,transtype_class,ts,webnodecode,wherestring) values( null,null,null,'PLAN',null,null,null,null,null,'Y',null,null,null,'leaveplan',null,null,null,null,null,null,null,null,'Y',null,null,null,null,null,null,'N',null,'60170leaveplan',null,'PLAN','0001AA1000000005HZOF',null,'GLOBLE00000000000000',null,'hrta',null,'2018-09-21 11:19:01',null,null)
go
