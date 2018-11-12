

--WA_EXPFORMAT_HEAD表的删除语句
delete  WA_EXPFORMAT_HEAD WHERE PK_FORMATHEAD IN ('6886TW10000FUBON2018','6886TW10063FUBON2018','6886TW10065FUBON2018');;
--WA_EXPFORMAT_HEADnull



Insert into WA_EXPFORMAT_HEAD
(pk_formathead,code,name,linecount,ts,dr)
Values('6886TW10000FUBON2018','SECOND_HINSURANCE_TW_62','二代健保申报格式(62)',3,'2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_HEAD
(pk_formathead,code,name,linecount,ts,dr)
Values('6886TW10063FUBON2018','SECOND_HINSURANCE_TW_63','二代健保申报格式(63)',3,'2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_HEAD
(pk_formathead,code,name,linecount,ts,dr)
Values('6886TW10065FUBON2018','SECOND_HINSURANCE_TW_65','二代健保申报格式(65)',3,'2018-09-27 20:02:33',0);





--WA_EXPFORMAT_ITEM表的删除语句
delete from wa_expformat_item where pk_formatitem in ('6886TW10000SECON1101','6886TW10000SECON1102','6886TW10000SECON1403','6886TW10000SECON1404','6886TW10000SECON1205','6886TW10000SECON1406','6886TW10000SECON1207','6886TW10000SECON1308','6886TW10000SECON1409','6886TW10000SECON1310');
delete from wa_expformat_item where pk_formatitem in ('6886TW10000SECON1111','6886TW10000SECON1112','6886TW10000SECON1113','6886TW10000SECON1114',
'6886TW10000SECON1115','6886TW10000SECON1116','6886TW10000SECON1117','6886TW10000SECON1118','6886TW10000SECON1219','6886TW10000SECON1120','6886TW10000SECON1621',
'6886TW10000SECON1522','6886TW10000SECON1323','6886TW10000SECON1224','6886TW10000SECON1125','6886TW10000SECON1226','6886TW10000SECON1227','6886TW10000SECON1128');
delete from wa_expformat_item where pk_formatitem in ('6886TW10000SECON1129','6886TW10000SECON1230','6886TW10000SECON1331','6886TW10000SECON1432','6886TW10000SECON1533','6886TW10000SECON1634','6886TW10000SECON1735','6886TW10000SECON1836','6886TW10000SECON1937');
delete from wa_expformat_item where pk_formatitem in ('6886TW10000SECON0101','6886TW10000SECON0102','6886TW10000SECON0403','6886TW10000SECON0404','6886TW10000SECON0205','6886TW10000SECON0406','6886TW10000SECON0207','6886TW10000SECON0308','6886TW10000SECON0409','6886TW10000SECON0310');
delete from wa_expformat_item where pk_formatitem in ('6886TW10000SECON0111','6886TW10000SECON0112','6886TW10000SECON0113','6886TW10000SECON0114',
'6886TW10000SECON0115','6886TW10000SECON0116','6886TW10000SECON0117','6886TW10000SECON0118','6886TW10000SECON0219','6886TW10000SECON0120','6886TW10000SECON0621',
'6886TW10000SECON0522','6886TW10000SECON0323','6886TW10000SECON0224','6886TW10000SECON0125','6886TW10000SECON0226','6886TW10000SECON0227','6886TW10000SECON0128');
delete from wa_expformat_item where pk_formatitem in ('6886TW10000SECON0129','6886TW10000SECON0230','6886TW10000SECON0331','6886TW10000SECON0432','6886TW10000SECON0533','6886TW10000SECON0634','6886TW10000SECON0735','6886TW10000SECON0836','6886TW10000SECON0937');
delete from wa_expformat_item where pk_formatitem in ('6886TW10000SECON0001','6886TW10000SECON0002','6886TW10000SECON0003','6886TW10000SECON0004','6886TW10000SECON0005','6886TW10000SECON0006','6886TW10000SECON0007','6886TW10000SECON0008','6886TW10000SECON0009','6886TW10000SECON0010');
delete from wa_expformat_item where pk_formatitem in ('6886TW10000SECON0011','6886TW10000SECON0012','6886TW10000SECON0013','6886TW10000SECON0014',
'6886TW10000SECON0015','6886TW10000SECON0016','6886TW10000SECON0017','6886TW10000SECON0018','6886TW10000SECON0019','6886TW10000SECON0020','6886TW10000SECON0021',
'6886TW10000SECON0022','6886TW10000SECON0023','6886TW10000SECON0024','6886TW10000SECON0025','6886TW10000SECON0026','6886TW10000SECON0027','6886TW10000SECON0028');
delete from wa_expformat_item where pk_formatitem in ('6886TW10000SECON0029','6886TW10000SECON0030','6886TW10000SECON0031','6886TW10000SECON0032','6886TW10000SECON0033','6886TW10000SECON0034','6886TW10000SECON0035','6886TW10000SECON0036','6886TW10000SECON0037');
--WA_EXPFORMAT_ITEMnull



Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0001',1,1,'DATA_IDENTY_CODE','资料识别码',0,1,0,'','','','','N',0,'','','1','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0002',1,2,'LVATNO','扣费单位统一编号',2,8,0,'','','','','N',2,'twhr_basedoc a ','a.code=''TWTAX005''  and b.code=''TWTAX006'' and a.pk_org=''%REF:legal_pk_org%''','bdd.code','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0003',1,3,'INCATEGORY','所得收入类别',0,2,0,'','','','','N',1,'','','''%REF:IncomeCategory%''','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0004',1,4,'STARTDATE','所得給付起始年月',0,5,0,'','','','','N',1,'','','%STARTPERIOD_TW%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0005',1,5,'ENDDATE','所得給付結束年月',0,5,0,'','','','','N',1,'','','%ENDPERIOD_TW%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0006',1,6,'CURDATE','檔案製作日期',0,7,0,'','','','','N',1,'','','%TWDATE%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0007',1,7,'HEADNUMBER','總機構統一編號',2,8,0,'','','','','N',2,'%INNER%twhr_basedoc b ','a.pk_org= b.pk_org','bdd.code','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0008',1,8,'E_EMAIL','扣費單位電子郵件信箱帳號',0,30,2,' ','','','','N',1,'','','''%REF:contactEmail%''','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0009',1,9,'DEDUCTIONNAME','扣費義務人名稱',0,50,2,' ',' ','','','N',2,'%LEFT%bd_defdoc bdd','bdd.pk_defdoc=a.refvalue','b.textvalue','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0010',1,10,'RESERVED','保留欄位',0,9,2,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0011',2,1,'LINE_IDENTY_CODE','资料识别码',0,1,0,'','','','','N',0,'','','2','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0012',2,2,'LINE_LVATNO','扣費單位統一編號',2,8,0,'','','','','N',2,'declaration_nonparttime dpart','dpart.pk_declaration=(select pk_declaration from twhr_declaration where pk_org=''%REF:pk_org%'')  and dpart.pk_org=''%REF:legal_PK_ORG%''','(select code from bd_defdoc where pk_defdoc=(select refvalue from twhr_basedoc where code=''TWTAX005'' and pk_org =''%REF:pk_org%''))','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0013',2,3,'LINE_INCATEGORY','所得收入类别',0,2,0,'','','','','N',1,'','','''%REF:IncomeCategory%''','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0014',2,4,'LINE_NUM','流水序号',0,5,0,'','','','','N',2,'','','%ROWNO%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0015',2,5,'HANDLEWAY','資料處理方式',0,1,0,'','','','','N',0,'','','1/R','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0016',2,6,'INCOMEDATE','所得給付日期',2,7,0,'','','','','N',2,'','','cast((cast(left(dpart.pay_date,4) as int)-1911) as varchar(3))+right(left(dpart.pay_date, 7),2)+right(left(dpart.pay_date, 10), 2)','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0017',2,7,'BENEID','所得人身分證號',2,10,0,'','','','','N',2,'','','dpart.beneficiary_id','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0018',2,8,'DECLARNUMBER','申报编号',2,30,1,'0','','','','N',2,'','','dpart.num','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0019',2,9,'SINGLEPAY','所得(收入)給付金額',1,14,1,'0','','','','N',2,'','','CONVERT(INTEGER,dpart.single_pay)','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0020',2,10,'WITHHOLDING','扣繳補充保費金額',1,10,1,'0','','','','N',2,'','','CONVERT(INTEGER,dpart.single_withholding)','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0021',2,11,'UNIT','投保單位',2,9,1,' ','','','','N',2,'','','dpart.insurance_unit_code','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0022',2,12,'MONTHAMOUNT','扣費當月投保金額',1,6,1,'0','','','','N',2,'','','CONVERT(INTEGER,dpart.totalbonusforyear)','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0023',2,13,'UNITAMOUNT','同年度累積金額',1,10,1,'','','','','N',2,'','','CONVERT(INTEGER,dpart.deductions_month_insure)','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0024',2,14,'FBLANK','15位空白',0,15,1,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0025',2,15,'TRUENOTE','信託註記',0,1,0,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0026',2,16,'BENEFNAME','所得人姓名',3,50,2,' ','','','','N',2,'','','dpart.beneficiary_name','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0027',2,17,'DATANOTE','資料註記',0,1,0,'','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0028',2,18,'E_RESERVED','保留欄位',0,16,1,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0029',3,1,'SUM_IDENTY_CODE','资料识别码',0,1,0,'','','','','N',0,'','','3','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0030',3,2,'SUM_LVATNO','扣費單位統一編號',2,8,0,'','','','','N',2,'declaration_nonparttime dpart','dpart.pk_declaration=(select pk_declaration from twhr_declaration where pk_org=''%REF:pk_org%'')  and dpart.pk_org=''%REF:legal_PK_ORG%'' ','(select code from bd_defdoc where pk_defdoc=(select refvalue from twhr_basedoc where code=''TWTAX005'' and pk_org =''%REF:pk_org%''))','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0031',3,3,'SUM_INCATEGORY','所得收入类别',0,2,0,'','','','','N',1,'','','''%REF:IncomeCategory%''','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0032',3,4,'SUM_COUNT','申报总笔数',1,9,1,'0','','','','N',2,'','','%COUNT:LINE:2:*%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0033',3,5,'SUM_PAYAMOUNT','所得(收入)給付總額',1,20,1,'0','','','','N',2,'','','%SUM:LINE:2:SINGLEPAY%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0034',3,6,'SUM_WITHHOLDING','扣繳補衝保費總額',0,16,1,'0','','','','N',2,'','','%SUM:LINE:2:WITHHOLDING%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0035',3,7,'SUM_CONTTEL','聯絡電話',2,15,0,'','','','','N',1,'','','%REF:conttel%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0036',3,8,'SUM_CONTNAME','聯絡姓名',1,50,2,' ','','','','N',1,'','','%REF:contactName%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10000FUBON2018','6886TW10000SECON0037',3,9,'SUM_RESERVED','保留欄位',1,79,2,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0101',1,1,'DATA_IDENTY_CODE','资料识别码',0,1,0,'','','','','N',0,'','','1','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0102',1,2,'LVATNO','扣费单位统一编号',2,8,0,'','','','','N',2,'twhr_basedoc a ','a.code=''TWTAX005''  and b.code=''TWTAX006'' and a.pk_org=''%REF:legal_pk_org%''','bdd.code','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0111',2,1,'LINE_IDENTY_CODE','资料识别码',0,1,0,'','','','','N',0,'','','2','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0112',2,2,'LINE_LVATNO','扣費單位統一編號',2,8,0,'','','','','N',2,'declaration_parttime dpart','dpart.pk_declaration=(select pk_declaration from twhr_declaration where pk_org=''%REF:pk_org%'')  and dpart.pk_org=''%REF:legal_PK_ORG%''','(select code from bd_defdoc where pk_defdoc=(select refvalue from twhr_basedoc where code=''TWTAX005'' and pk_org =''%REF:pk_org%''))','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0113',2,3,'LINE_INCATEGORY','所得收入类别',0,2,0,'','','','','N',1,'','','''%REF:IncomeCategory%''','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0114',2,4,'LINE_NUM','流水序号',0,5,0,'','','','','N',2,'','','%ROWNO%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0115',2,5,'HANDLEWAY','資料處理方式',0,1,0,'','','','','N',0,'','','1/R','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0116',2,6,'INCOMEDATE','所得給付日期',2,7,0,'','','','','N',2,'','','dpart.pay_date','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0117',2,7,'BENEID','所得人身分證號',2,10,0,'','','','','N',2,'','','dpart.beneficiary_id','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0118',2,8,'DECLARNUMBER','申报编号',2,30,1,'0','','','','N',2,'','','dpart.num','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0120',2,10,'WITHHOLDING','扣繳補充保費金額',1,10,1,'0','','','','N',2,'','','CONVERT(INTEGER,dpart.single_withholding)','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0125',2,15,'TRUENOTE','信託註記',0,1,0,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0128',2,18,'E_RESERVED','保留欄位',0,16,1,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0129',3,1,'SUM_IDENTY_CODE','资料识别码',0,1,0,'','','','','N',0,'','','3','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0205',1,5,'ENDDATE','所得給付結束年月',0,5,0,'','','','','N',1,'','','%ENDPERIOD_TW%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0207',1,7,'HEADNUMBER','總機構統一編號',2,8,0,'','','','','N',2,'%INNER%twhr_basedoc b ','a.pk_org= b.pk_org','bdd.code','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0219',2,9,'SINGLEPAY','所得(收入)給付金額',1,14,1,'0','','','','N',2,'','','CONVERT(INTEGER,dpart.single_pay)','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0224',2,14,'FBLANK','15位空白',0,15,1,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0226',2,16,'BENEFNAME','所得人姓名',3,50,2,' ','','','','N',2,'','','dpart.beneficiary_name','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0227',2,17,'DATANOTE','資料註記',0,1,0,'','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0230',3,2,'SUM_LVATNO','扣費單位統一編號',2,8,0,'','','','','N',2,'declaration_parttime dpart','dpart.pk_declaration=(select pk_declaration from twhr_declaration where pk_org=''%REF:pk_org%'')  and dpart.pk_org=''%REF:legal_PK_ORG%''','(select code from bd_defdoc where pk_defdoc=(select refvalue from twhr_basedoc where code=''TWTAX005'' and pk_org =''%REF:pk_org%''))','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0308',1,8,'E_EMAIL','扣費單位電子郵件信箱帳號',0,30,2,' ','','','','N',1,'','','''%REF:contactEmail%''','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0310',1,10,'RESERVED','保留欄位',0,9,2,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0323',2,13,'UNITAMOUNT','同年度累積金額',1,10,1,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0331',3,3,'SUM_INCATEGORY','所得收入类别',0,2,0,'','','','','N',1,'','','''%REF:IncomeCategory%''','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0403',1,3,'INCATEGORY','所得收入类别',0,2,0,'','','','','N',1,'','','''%REF:IncomeCategory%''','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0404',1,4,'STARTDATE','所得給付起始年月',0,5,0,'','','','','N',1,'','','%STARTPERIOD_TW%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0406',1,6,'CURDATE','檔案製作日期',0,7,0,'','','','','N',1,'','','%TWDATE%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0409',1,9,'DEDUCTIONNAME','扣費義務人名稱',0,50,2,' ',' ','','','N',2,'%LEFT%bd_defdoc bdd','bdd.pk_defdoc=a.refvalue','b.textvalue','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0432',3,4,'SUM_COUNT','申报总笔数',1,9,1,'0','','','','N',2,'','','%COUNT:LINE:2:*%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0522',2,12,'MONTHAMOUNT','扣費當月投保金額',1,6,1,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0533',3,5,'SUM_PAYAMOUNT','所得(收入)給付總額',1,20,1,'0','','','','N',2,'','','%SUM:LINE:2:SINGLEPAY%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0621',2,11,'UNIT','投保單位',2,9,1,' ','','','','N',0,'','',' ','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0634',3,6,'SUM_WITHHOLDING','扣繳補衝保費總額',0,16,1,'0','','','','N',2,'','','%SUM:LINE:2:WITHHOLDING%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0735',3,7,'SUM_CONTTEL','聯絡電話',2,15,0,'','','','','N',1,'','','%REF:conttel%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0836',3,8,'SUM_CONTNAME','聯絡姓名',1,50,2,' ','','','','N',1,'','','%REF:contactName%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10063FUBON2018','6886TW10000SECON0937',3,9,'SUM_RESERVED','保留欄位',1,79,2,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1101',1,1,'DATA_IDENTY_CODE','资料识别码',0,1,0,'','','','','N',0,'','','1','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1102',1,2,'LVATNO','扣费单位统一编号',2,8,0,'','','','','N',2,'twhr_basedoc a ','a.code=''TWTAX005''  and b.code=''TWTAX006'' and a.pk_org=''%REF:legal_pk_org%''','bdd.code','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1111',2,1,'LINE_IDENTY_CODE','资料识别码',0,1,0,'','','','','N',0,'','','2','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1112',2,2,'LINE_LVATNO','扣費單位統一編號',2,8,0,'','','','','N',2,'declaration_business dpart','dpart.pk_declaration=(select pk_declaration from twhr_declaration where pk_org=''%REF:pk_org%'')  and dpart.pk_org=''%REF:legal_PK_ORG%''','(select code from bd_defdoc where pk_defdoc=(select refvalue from twhr_basedoc where code=''TWTAX005'' and pk_org =''%REF:pk_org%''))','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1113',2,3,'LINE_INCATEGORY','所得收入类别',0,2,0,'','','','','N',1,'','','''%REF:IncomeCategory%''','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1114',2,4,'LINE_NUM','流水序号',0,5,0,'','','','','N',2,'','','%ROWNO%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1115',2,5,'HANDLEWAY','資料處理方式',0,1,0,'','','','','N',0,'','','1/R','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1116',2,6,'INCOMEDATE','所得給付日期',2,7,0,'','','','','N',2,'','','cast((cast(left(dpart.pay_date,4) as int)-1911) as varchar(3))+right(left(dpart.pay_date, 7),2)+right(left(dpart.pay_date, 10), 2)','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1117',2,7,'BENEID','所得人身分證號',2,10,0,'','','','','N',2,'','','dpart.beneficiary_id','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1118',2,8,'DECLARNUMBER','申报编号',2,30,1,'0','','','','N',2,'','','dpart.num','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1120',2,10,'WITHHOLDING','扣繳補充保費金額',1,10,1,'0','','','','N',2,'','','CONVERT(INTEGER,dpart.single_withholding)','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1125',2,15,'TRUENOTE','信託註記',0,1,0,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1128',2,18,'E_RESERVED','保留欄位',0,16,1,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1129',3,1,'SUM_IDENTY_CODE','资料识别码',0,1,0,'','','','','N',0,'','','3','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1205',1,5,'ENDDATE','所得給付結束年月',0,5,0,'','','','','N',1,'','','%ENDPERIOD_TW%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1207',1,7,'HEADNUMBER','總機構統一編號',2,8,0,'','','','','N',2,'%INNER%twhr_basedoc b ','a.pk_org= b.pk_org','bdd.code','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1219',2,9,'SINGLEPAY','所得(收入)給付金額',1,14,1,'0','','','','N',2,'','','CONVERT(INTEGER,dpart.single_pay)','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1224',2,14,'FBLANK','15位空白',0,15,1,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1226',2,16,'BENEFNAME','所得人姓名',3,50,2,' ','','','','N',2,'','','dpart.beneficiary_name','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1227',2,17,'DATANOTE','資料註記',0,1,0,'','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1230',3,2,'SUM_LVATNO','扣費單位統一編號',2,8,0,'','','','','N',2,'declaration_business dpart','dpart.pk_declaration=(select pk_declaration from twhr_declaration where pk_org=''%REF:pk_org%'')  and dpart.pk_org=''%REF:legal_PK_ORG%''','(select code from bd_defdoc where pk_defdoc=(select refvalue from twhr_basedoc where code=''TWTAX005'' and pk_org =''%REF:pk_org%''))','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1308',1,8,'E_EMAIL','扣費單位電子郵件信箱帳號',0,30,2,' ','','','','N',1,'','','''%REF:contactEmail%''','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1310',1,10,'RESERVED','保留欄位',0,9,2,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1323',2,13,'UNITAMOUNT','同年度累積金額',1,10,1,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1331',3,3,'SUM_INCATEGORY','所得收入类别',0,2,0,'','','','','N',1,'','','''%REF:IncomeCategory%''','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1403',1,3,'INCATEGORY','所得收入类别',0,2,0,'','','','','N',1,'','','''%REF:IncomeCategory%''','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1404',1,4,'STARTDATE','所得給付起始年月',0,5,0,'','','','','N',1,'','','%STARTPERIOD_TW%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1406',1,6,'CURDATE','檔案製作日期',0,7,0,'','','','','N',1,'','','%TWDATE%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1409',1,9,'DEDUCTIONNAME','扣費義務人名稱',0,50,2,' ',' ','','','N',2,'%LEFT%bd_defdoc bdd','bdd.pk_defdoc=a.refvalue','b.textvalue','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1432',3,4,'SUM_COUNT','申报总笔数',1,9,1,'0','','','','N',2,'','','%COUNT:LINE:2:*%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1522',2,12,'MONTHAMOUNT','扣費當月投保金額',1,6,1,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1533',3,5,'SUM_PAYAMOUNT','所得(收入)給付總額',1,20,1,'0','','','','N',2,'','','%SUM:LINE:2:SINGLEPAY%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1621',2,11,'UNIT','投保單位',2,9,1,' ','','','','N',0,'','',' ','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1634',3,6,'SUM_WITHHOLDING','扣繳補衝保費總額',0,16,1,'0','','','','N',2,'','','%SUM:LINE:2:SUM_WITHHOLDING%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1735',3,7,'SUM_CONTTEL','聯絡電話',2,15,0,'','','','','N',1,'','','%REF:conttel%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1836',3,8,'SUM_CONTNAME','聯絡姓名',1,50,2,' ','','','','N',1,'','','%REF:contactName%','2018-09-27 20:02:33',0);

Insert into WA_EXPFORMAT_ITEM
(pk_formathead,pk_formatitem,linenumber,posnumber,itemcode,itemname,datatype,byteLength,fillmode,fillstr,prefix,suffix,splitter,issum,datasource,datatable,joinkey,datacontext,ts,dr)
Values('6886TW10065FUBON2018','6886TW10000SECON1937',3,9,'SUM_RESERVED','保留欄位',1,79,2,' ','','','','N',0,'','','','2018-09-27 20:02:33',0);
