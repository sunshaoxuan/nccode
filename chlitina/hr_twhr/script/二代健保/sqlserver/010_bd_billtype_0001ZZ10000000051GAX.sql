
delete bd_billtype where ( pk_billtypeid='0001ZZ10000000051GAX' and (dr=0 or dr is null) )
;


insert into bd_billtype(accountclass,billcoderule,billstyle,billtypename,billtypename2,billtypename3,billtypename4,billtypename5,billtypename6,canextendtransaction,checkclassname,classname,comp,component,datafinderclz,def1,def2,def3,dr,emendenumclass,forwardbilltype,isaccount,isapprovebill,isbizflowbill,iseditableproperty,isenablebutton,isenabletranstypebcr,islock,isroot,istransaction,ncbrcode,nodecode,parentbilltype,pk_billtypecode,pk_billtypeid,pk_group,pk_org,referclassname,systemcode,transtype_class,ts,webnodecode,wherestring) values( null,'~',null,'NHI1',null,null,null,null,null,'Y',null,null,null,'twhr_declaration',null,null,null,null,null,null,null,null,'Y',null,null,null,null,null,null,'N','~','68J61710','~','NHI1','0001ZZ10000000051GAX','~','GLOBLE00000000000000',null,'TWHR',null,'2018-09-26 10:47:41','~',null)
;
