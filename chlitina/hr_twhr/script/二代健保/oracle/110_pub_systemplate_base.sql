
delete pub_systemplate_base where ( pk_systemplate in ('0001ZZ10000000051GAW', '0001ZZ10000000051FTY', '0001ZZ10000000051FSF') and (dr=0 or dr is null) )
;


insert into pub_systemplate_base(devorg,dr,funnode,layer,moduleid,nodekey,pk_country,pk_industry,pk_systemplate,templateid,tempstyle,ts) values( '00001',0,'68J61710',0,'68J6','bt','~','~','0001ZZ10000000051FSF','0001ZZ10000000051FN0',0,'2018-09-26 10:47:32')
;
insert into pub_systemplate_base(devorg,dr,funnode,layer,moduleid,nodekey,pk_country,pk_industry,pk_systemplate,templateid,tempstyle,ts) values( '00001',0,'68J61710',0,'68J6','qt','~','~','0001ZZ10000000051FTY','0001ZZ10000000051FSG',1,'2018-09-26 10:47:34')
;
insert into pub_systemplate_base(devorg,dr,funnode,layer,moduleid,nodekey,pk_country,pk_industry,pk_systemplate,templateid,tempstyle,ts) values( '00001',0,'68J61710',0,'68J6','ot','~','~','0001ZZ10000000051GAW','0001ZZ10000000051FTZ',3,'2018-09-26 10:47:38')
;
