--加班轉調休組織參數
DELETE FROM pub_sysinittemp WHERE initcode IN ('TWHRT08', 'TWHRT09');
INSERT INTO pub_sysinittemp (afterclass, apptag, checkclass, dataclass, dataoriginflag, defaultvalue, domainflag, dr, editcomponentctrlclass, groupcode, groupname, initcode, initname, mainflag, mutexflag, orgtypeconvertmode, paratype, pk_orgtype, pk_refinfo, pk_sysinittemp, remark, showflag, stateflag, sysflag, sysindex, ts, valuelist, valuetype) VALUES (null, null, null, null, 0, '', '6017', null, 'nc.ui.ta.validator.SysInitLeaveTypeParaEditCtrl', '~', null, 'TWHRT08', '加班補休指定假別', 'N', 0, 'HRORGTYPE00000000000', 'business', 'HRORGTYPE00000000000', '0001Z7FF000000005ZQ8', '1001ZZ1000000000K1GC', '加班轉補休使用假期類別，若將假勤設置為補休指定假別，則該假勤就不顯示在假勤計算裡。', 'Y', 5, 'N', 0, '2018-09-16 23:14:16', '', 2);
INSERT INTO PUB_SYSINITTEMP (AFTERCLASS, APPTAG, CHECKCLASS, DATACLASS, DATAORIGINFLAG, DEFAULTVALUE, DOMAINFLAG, DR, EDITCOMPONENTCTRLCLASS, GROUPCODE, GROUPNAME, INITCODE, INITNAME, MAINFLAG, MUTEXFLAG, ORGTYPECONVERTMODE, PARATYPE, PK_ORGTYPE, PK_REFINFO, PK_SYSINITTEMP, REMARK, SHOWFLAG, STATEFLAG, SYSFLAG, SYSINDEX, TS, VALUELIST, VALUETYPE) VALUES (null, null, null, null, 0, '0', '6017', null, null, '~', null, 'TWHRT09', '補修結算方式', 'N', 0, 'HRORGTYPE00000000000', 'business', 'HRORGTYPE00000000000', '~', '1001ZZ1000000000K1GD', '加班補休計算方式，參數設定後須至考勤規則-組織中進行配置。', 'Y', 1, 'N', 0, '2018-09-26 23:16:11', 'C,按照加班日期往後N個月份結算=0,按照加班審批日期往後N個月結算=1,固定週期結算=2,按年資起算結算=3', 2);


--增補考勤規則字段
ALTER TABLE
    tbm_timerule ADD ctrlothours3 DECIMAL(28,8);
ALTER TABLE
    tbm_timerule ADD isrestrictctrlot3 nchar(1);
ALTER TABLE
    tbm_timerule ADD ctrlothours1of3 DECIMAL(28,8);
ALTER TABLE
    tbm_timerule ADD isrestrictctrlot1of3 nchar(1);
ALTER TABLE
    tbm_timerule ADD monthafterotdate DECIMAL(28,8);
ALTER TABLE
    tbm_timerule ADD monthafterapproved DECIMAL(28,8);
ALTER TABLE
    tbm_timerule ADD startcycleyearmonth CHAR(6);
ALTER TABLE
    tbm_timerule ADD monthofcycle DECIMAL(28,8);
ALTER TABLE
    tbm_timerule ADD isautotransfersalary nchar(1);
	
--增補加班類別字段
ALTER TABLE
    tbm_timeitemcopy ADD isstuffdecidecomp nchar(1) DEFAULT 'Y';
ALTER TABLE
    tbm_timeitemcopy ADD daylimit DECIMAL(10,4) DEFAULT 0;
ALTER TABLE
    tbm_timeitemcopy ADD isincludewithlimit nchar(1) DEFAULT 'N';
ALTER TABLE
    tbm_timeitemcopy ADD effectivehours DECIMAL(10,4) DEFAULT 0;
ALTER TABLE
    tbm_timeitemcopy ADD deductlowhours DECIMAL(10,4) DEFAULT 0;
ALTER TABLE
    tbm_timeitemcopy ADD deductminutes DECIMAL(10,4) DEFAULT 0;
ALTER TABLE
    tbm_timeitemcopy ADD pk_segrule nchar(20);
ALTER TABLE
    tbm_timeitemcopy ADD date_type INT;
ALTER TABLE
    tbm_timeitemcopy ADD isdatetypedefault nchar(1) DEFAULT 'N';
