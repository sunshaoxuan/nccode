
delete pub_systemplate_base where ( pk_systemplate in ('0001AA1000000005I01Q', '0001AA1000000005HZY8', '0001AA1000000005HZWV') and (dr=0 or dr is null) )
go


insert into pub_systemplate_base(devorg,dr,funnode,layer,moduleid,nodekey,pk_country,pk_industry,pk_systemplate,templateid,tempstyle,ts) values( '00001',0,'60170leaveplans',0,'6017','bt',null,null,'0001AA1000000005HZWV','0001AA1000000005HZVG',0,'2018-09-21 15:39:30')
go
insert into pub_systemplate_base(devorg,dr,funnode,layer,moduleid,nodekey,pk_country,pk_industry,pk_systemplate,templateid,tempstyle,ts) values( '00001',0,'60170leaveplans',0,'6017','qt',null,null,'0001AA1000000005HZY8','0001AA1000000005HZWW',1,'2018-09-21 15:39:31')
go
insert into pub_systemplate_base(devorg,dr,funnode,layer,moduleid,nodekey,pk_country,pk_industry,pk_systemplate,templateid,tempstyle,ts) values( '00001',0,'60170leaveplans',0,'6017','ot',null,null,'0001AA1000000005I01Q','0001AA1000000005HZY9',3,'2018-09-21 15:39:33')
go
