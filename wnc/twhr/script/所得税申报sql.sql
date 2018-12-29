delete from WA_EXPFORMAT_ITEM where  pk_formathead='6886TW100000IITR2018' and (linenumber=2 or linenumber=1);

INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0001', 1, 1, 'HCOUNTY', '縣市別', 2, 1, 0, null, null, null, null, ' ', 2, 'org_hrorg org', 'com.code=''%REF:VATNUMBER%''', 'county.code', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0002', 1, 2, 'HDEPT', '機關別', 2, 2, 0, null, null, null, null, ' ', 2, '%INNER%bd_defdoc county', 'county.pk_defdoc=org.%REF:COUNTYDS%', 'right(dept.code, 2)', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0003', 1, 3, 'HROWNO', '流水號', 2, 8, 2, null, null, null, null, ' ', 2, '%INNER%bd_defdoc dept', 'dept.pk_defdoc=org.%REF:DEPTDS%', ''' ''', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0004', 1, 4, 'HVATNO', '申報單位統一編號', 2, 8, 2, null, null, null, null, ' ', 2, null, null, '''%REF:VATNUMBER%''', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0005', 1, 5, 'HSPLT', '資料區分', 1, 1, 0, null, null, null, null, ' ', 0, null, null, '1', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0006', 1, 6, 'COMNAME', '申報單位名稱', 2, 36, 2, ' ', null, null, null, ' ', 2, '%INNER%bd_defdoc com', 'com.pk_defdoc=org.%REF:VATNUMBERDS%', '(select textvalue from WNCNL.TWHR_BASEDOC where pk_org=''GLOBLE00000000000000'' and code=''TWTAX003'')', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0007', 1, 7, 'COMADDR', '申報單位地址', 2, 52, 2, null, null, null, null, ' ', 2, null, null, 'isnull(org.%REF:COMADDRESSDS%, '' '')', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0008', 1, 8, 'PRINCPLENAME', '扣繳義務人名稱', 2, 40, 2, null, null, null, null, ' ', 2, null, null, 'isnull(org.%REF:COMPRINCIPALDS%, '' '')', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0009', 1, 9, 'LINKMAN', '聯絡人姓名', 2, 40, 2, null, null, null, null, ' ', 2, null, null, 'isnull(''%REF:COMLINKMAN%'', '' '')', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0010', 1, 10, 'PHONENO', '聯絡人電話', 2, 15, 2, null, null, null, null, ' ', 2, null, null, 'isnull(''%REF:COMTELNO%'', '' '')', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0011', 1, 11, 'EMAIL', '申報單位電子郵件信箱帳號', 2, 30, 2, null, null, null, null, ' ', 2, null, null, 'isnull(''%REF:COMEMAIL%'', '' '')', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0012', 1, 12, 'TAXNO', '申報單位稅籍編號', 2, 9, 2, null, null, null, null, ' ', 2, null, null, 'isnull(org.%REF:COMTAXNODS%, '' '')', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0013', 1, 13, 'ISHQ', '總分支機構註記', 2, 1, 0, null, null, null, null, ' ', 2, null, null, 'isnull(org.%REF:COMISHQDS%, '' '')', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0014', 1, 14, 'REPCOUNT', '申報次數', 1, 2, 1, '0', null, null, null, ' ', 1, null, null, '''%REF:APPLYCOUNT%''', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0015', 1, 15, 'REPREASON', '重複申報原因', 2, 2, 2, null, null, null, null, ' ', 1, null, null, 'isnull(''%REF:APPLYREASON%'','' '')', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0016', 1, 16, 'INSTOCK', '上市公司註記', 2, 1, 0, null, null, null, null, ' ', 2, null, null, 'isnull(org.%REF:COMINSTOCKDS%, '' '')', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0017', 1, 17, 'ISBANK', '金融機構註記', 2, 1, 1, null, null, null, null, ' ', 2, null, null, 'case when org.%REF:COMISBANKDS%=''Y'' then ''1'' else '' '' end', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0018', 1, 18, 'ISAGENT', '事務所代理註記', 2, 1, 0, null, null, null, null, ' ', 2, null, null, 'isnull(org.%REF:COMISAGENTDS%, '' '')', '2018-03-09 09:00:00', 0);



INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0019', 2, 1, 'LCOUNTY', '縣市別', 2, 1, 0, null, null, null, null, ' ', 2, null, null, '''%VALUE:LINE:1:HCOUNTY%''', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0020', 2, 2, 'LDEPT', '機關別', 2, 2, 0, null, null, null, null, ' ', 2, 'hrwa_sumincometax tax', 'tax.pk_sumincometax in (%REF:TEMPTABLENAME% )', '''%VALUE:LINE:1:HDEPT%''', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0021', 2, 3, 'LROWNO', '流水號', 1, 8, 1, '0', null, null, null, ' ', 2, null, null, '%ROWNO:psn.code%', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0022', 2, 4, 'LVATNO', '申報單位統一編號', 2, 8, 2, null, null, null, null, ' ', 2, '%LEFT%bd_defdoc bizno', 'tax.businessno=bizno.pk_defdoc', '''%VALUE:LINE:1:HVATNO%''', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0023', 2, 5, 'REMARK', '註記', 2, 1, 1, null, null, null, null, ' ', 2, null, null, ''' ''', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0024', 2, 6, 'FORMAT', '格式', 2, 2, 0, null, null, null, null, ' ', 1, null, null, '''%REF:FORMAT%''', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0025', 2, 7, 'IDNO', '所得人統一編(證)號', 2, 10, 0, null, null, null, null, ' ', 2, '%LEFT%bd_defdoc feeno', 'tax.costno=feeno.pk_defdoc', 'tax.id', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0026', 2, 8, 'CARDTYPE', '證號別', 2, 1, 0, null, null, null, null, ' ', 2, '%LEFT%bd_defdoc prjno', 'tax.projectno=prjno.pk_defdoc', 'tax.idtypeno', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0027', 2, 9, 'TOTALPAY', '扣繳憑單給付總額', 1, 10, 1, '0', null, null, null, ' ', 2, null, null, 'cast(isnull(tax.taxbase, 0)+isnull(tax.taxbaseadjust,0) as int)', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0028', 2, 10, 'TOTALTAXED', '扣繳憑單扣繳稅額', 1, 10, 1, '0', null, null, null, ' ', 2, null, null, 'cast(isnull(tax.cacu_value,0)+isnull(tax.cacu_valueadjust,0) as int)', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0029', 2, 11, 'TOTALNET', '給付淨額', 1, 10, 1, '0', null, null, null, ' ', 2, null, null, 'cast(isnull(tax.netincome,0)+isnull(tax.taxbaseadjust,0)-isnull(tax.cacu_valueadjust,0) as int)', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0030', 2, 12, 'PUB', '公共區域', 2, 12, 2, null, null, null, null, ' ', 2, null, null, 'case when ''%REF:FORMAT%''=''50'' or ''%REF:FORMAT%''=''91'' then tax.code || ''000'' when ''%REF:FORMAT%''=''9A'' then bizno.code when ''%REF:FORMAT%''=''9B'' then feeno.code when ''%REF:FORMAT%''=''92'' then prjno.code else '''' end', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0031', 2, 13, 'SOFTREMARK', '軟體註記', 2, 1, 0, null, null, null, null, ' ', 0, null, null, 'B', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0032', 2, 14, 'ERRORREMARK', '錯誤註記', 2, 1, 1, null, null, null, null, ' ', 2, null, null, ''' ''', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0033', 2, 15, 'PAYYEAR', '給付年度', 2, 3, 1, '0', null, null, null, ' ', 2, null, null, '%REFTWYEAR%', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0034', 2, 16, 'NAME', '所得人姓名或名稱', 3, 40, 2, null, null, null, null, ' ', 2, '%INNER%bd_psndoc psn', 'tax.pk_psndoc=psn.pk_psndoc', 'psn.name2', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0035', 2, 17, 'ADDRESS', '所得人地址', 3, 60, 2, null, null, null, null, ' ', 2, null, null, 'isnull(psn.glbdef8, '' '')', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0036', 2, 18, 'PERIOD', '所得所屬期間', 2, 10, 0, null, null, null, null, ' ', 2, null, null, 'cast(left(tax.beginperiod, 4)-1911 as char(3))+right(tax.beginperiod, 2)+cast(left(tax.endperiod, 4)-1911 as char(3))+right(tax.endperiod, 2)', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0037', 2, 19, 'RETIRESELF', '員工自提退休金', 1, 10, 1, '0', null, null, null, ' ', 2, null, null, 'cast(isnull(tax.pickedup,0)+isnull(tax.pickedupadjust,0) as int)', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0038', 2, 20, 'BLANK1', '空白1', 2, 37, 1, null, null, null, null, ' ', 2, null, null, ''' ''', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0039', 2, 21, 'TAXREMARK', '扣抵稅額註記', 2, 1, 1, null, null, null, null, ' ', 2, null, null, ''' ''', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0040', 2, 22, 'FILLMODE', '憑單填發方式', 2, 1, 2, null, null, null, null, ' ', 2, null, null, '%REF:GRANTTYPE%', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0041', 2, 23, 'STAY183', '是否滿183天', 2, 1, 1, null, null, null, null, ' ', 2, null, null, 'case when tax.idtypeno=''0'' or tax.idtypeno=''3'' then '' '' when tax.idtypeno=''5'' or tax.idtypeno=''7'' or tax.idtypeno=''9'' then ''N'' else '''' end', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0042', 2, 24, 'COUNTRYCODE', '國家代碼', 2, 2, 1, null, null, null, null, ' ', 2, '%LEFT%bd_countryzone ctz', 'psn.country=ctz.pk_country', 'case when tax.idtypeno=''0'' or tax.idtypeno=''3'' then '' '' when tax.idtypeno=''5'' or tax.idtypeno=''7'' or tax.idtypeno=''9'' then (case when ctz.code is not null then ctz.code else ''ZZ'' end) else ''ZZ'' end', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0043', 2, 25, 'CUSTOMCODE', '租稅協定代碼', 2, 2, 1, null, null, null, null, ' ', 2, null, null, ''' ''', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0044', 2, 26, 'BLANK2', '空白2', 2, 2, 1, null, null, null, null, ' ', 2, null, null, ''' ''', '2018-03-09 09:00:00', 0);
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0045', 2, 27, 'MAKEDATE', '檔案製作日期或給付日期', 2, 4, 0, null, null, null, null, ' ', 2, null, null, '(right(''%TWDATE%'',4))', '2018-03-09 09:00:00', 0);

delete from WA_EXPFORMAT_ITEM where PK_FORMATITEM='6886TW100000IITR0035';
INSERT INTO WA_EXPFORMAT_ITEM (PK_FORMATHEAD, PK_FORMATITEM, LINENUMBER, POSNUMBER, ITEMCODE, ITEMNAME, DATATYPE, BYTELENGTH, FILLMODE, FILLSTR, PREFIX, SUFFIX, SPLITTER, ISSUM, DATASOURCE, DATATABLE, JOINKEY, DATACONTEXT, TS, DR) VALUES ('6886TW100000IITR2018', '6886TW100000IITR0035', 2, 17, 'ADDRESS', '所得人地址', 3, 60, 2, null, null, null, null, ' ', 2, null, null, 'isnull(psn.censusaddr, '' '')', '2018-03-09 09:00:00', 0);
