create table twhr_declaration (
pk_declaration char(20) NOT NULL,
pk_group varchar(20) default '~' NULL,
pk_org varchar(20) default '~' NULL,
pk_org_v varchar(20) default '~' NULL,
creator varchar(20) default '~' NULL,
creationtime char(19) NULL,
modifier varchar(20) default '~' NULL,
modifiedtime char(19) NULL,
code varchar(50) NULL,
name varchar(50) NULL,
maketime char(19) NULL,
lastmaketime char(19) NULL,
billid varchar(50) NULL,
billno varchar(50) NULL,
pkorg varchar(50) NULL,
busitype varchar(50) NULL,
billmaker varchar(50) NULL,
approver varchar(50) NULL,
approvestatus int NULL,
approvenote varchar(50) NULL,
approvedate char(19) NULL,
transtype varchar(50) NULL,
billtype varchar(50) NULL,
transtypepk char(20) NULL,
srcbilltype varchar(50) NULL,
srcbillid varchar(50) NULL,
emendenum int NULL,
billversionpk varchar(50) NULL,
billdate char(19) NULL,
vdef1 varchar(101) NULL,
vdef2 varchar(101) NULL,
vdef3 varchar(101) NULL,
vdef4 varchar(101) NULL,
vdef5 varchar(101) NULL,
vdef6 varchar(101) NULL,
vdef7 varchar(101) NULL,
vdef8 varchar(101) NULL,
vdef9 varchar(101) NULL,
vdef10 varchar(101) NULL,
vdef11 varchar(101) NULL,
vdef12 varchar(101) NULL,
vdef13 varchar(101) NULL,
vdef14 varchar(101) NULL,
vdef15 varchar(101) NULL,
vdef16 varchar(101) NULL,
vdef17 varchar(101) NULL,
vdef18 varchar(101) NULL,
vdef19 varchar(101) NULL,
vdef20 varchar(101) NULL,
CONSTRAINT PK_WHR_DECLARATION PRIMARY KEY (pk_declaration),
ts char(19) NULL,
dr smallint default 0 
);

create table declaration_nonparttime (
pk_nonparttime char(20) NOT NULL,
rowno varchar(50) NULL,
pk_group varchar(20) default '~' NULL,
pk_org varchar(20) default '~' NULL,
pk_org_v varchar(20) default '~' NULL,
pk_dept varchar(20) default '~' NULL,
num int NULL,
pay_date char(19) NULL,
beneficiary_id varchar(50) NULL,
beneficiary_name varchar(50) NULL,
single_pay decimal(28,8) NULL,
single_withholding decimal(28,8) NULL,
insurance_unit_code varchar(50) NULL,
deductions_month_insure decimal(28,8) NULL,
totalbonusforyear decimal(28,8) NULL,
vbdef1 varchar(101) NULL,
vbdef2 varchar(20) default '~' NULL,
vbdef3 varchar(20) default '~' NULL,
vbdef4 varchar(20) default '~' NULL,
vbdef5 varchar(101) NULL,
vbdef6 varchar(101) NULL,
vbdef7 varchar(101) NULL,
vbdef8 varchar(101) NULL,
vbdef9 varchar(101) NULL,
vbdef10 varchar(101) NULL,
vbdef11 varchar(101) NULL,
vbdef12 varchar(101) NULL,
vbdef13 varchar(101) NULL,
vbdef14 varchar(101) NULL,
vbdef15 varchar(101) NULL,
vbdef16 varchar(101) NULL,
vbdef17 varchar(101) NULL,
vbdef18 varchar(101) NULL,
vbdef19 varchar(101) NULL,
vbdef20 varchar(101) NULL,
pk_declaration char(20) NOT NULL,
CONSTRAINT PK_ION_NONPARTTIME PRIMARY KEY (pk_nonparttime),
ts char(19) NULL,
dr smallint default 0 
);

create table declaration_parttime (
pk_parttime char(20) NOT NULL,
rowno varchar(50) NULL,
pk_group varchar(20) default '~' NULL,
pk_org varchar(20) default '~' NULL,
pk_org_v varchar(20) default '~' NULL,
pk_dept varchar(20) default '~' NULL,
num int NULL,
pay_date char(19) NULL,
beneficiary_id varchar(50) NULL,
beneficiary_name varchar(50) NULL,
single_pay decimal(28,8) NULL,
single_withholding decimal(28,8) NULL,
vbdef1 varchar(101) NULL,
vbdef2 varchar(20) default '~' NULL,
vbdef3 varchar(20) default '~' NULL,
vbdef4 varchar(20) default '~' NULL,
vbdef5 varchar(101) NULL,
vbdef6 varchar(101) NULL,
vbdef7 varchar(101) NULL,
vbdef8 varchar(101) NULL,
vbdef9 varchar(101) NULL,
vbdef10 varchar(101) NULL,
vbdef11 varchar(101) NULL,
vbdef12 varchar(101) NULL,
vbdef13 varchar(101) NULL,
vbdef14 varchar(101) NULL,
vbdef15 varchar(101) NULL,
vbdef16 varchar(101) NULL,
vbdef17 varchar(101) NULL,
vbdef18 varchar(101) NULL,
vbdef19 varchar(101) NULL,
vbdef20 varchar(101) NULL,
pk_declaration char(20) NOT NULL,
CONSTRAINT PK_RATION_PARTTIME PRIMARY KEY (pk_parttime),
ts char(19) NULL,
dr smallint default 0 
);

create table declaration_business (
pk_business char(20) NOT NULL,
rowno varchar(50) NULL,
pk_group varchar(20) default '~' NULL,
pk_org varchar(20) default '~' NULL,
pk_org_v varchar(20) default '~' NULL,
pk_dept varchar(20) default '~' NULL,
num int NULL,
pay_date char(19) NULL,
beneficiary_id varchar(50) NULL,
beneficiary_name varchar(50) NULL,
single_pay decimal(28,8) NULL,
single_withholding decimal(28,8) NULL,
vbdef1 varchar(101) NULL,
vbdef2 varchar(20) default '~' NULL,
vbdef3 varchar(20) default '~' NULL,
vbdef4 varchar(20) default '~' NULL,
vbdef5 varchar(101) NULL,
vbdef6 varchar(101) NULL,
vbdef7 varchar(101) NULL,
vbdef8 varchar(101) NULL,
vbdef9 varchar(101) NULL,
vbdef10 varchar(101) NULL,
vbdef11 varchar(101) NULL,
vbdef12 varchar(101) NULL,
vbdef13 varchar(101) NULL,
vbdef14 varchar(101) NULL,
vbdef15 varchar(101) NULL,
vbdef16 varchar(101) NULL,
vbdef17 varchar(101) NULL,
vbdef18 varchar(101) NULL,
vbdef19 varchar(101) NULL,
vbdef20 varchar(101) NULL,
pk_declaration char(20) NOT NULL,
CONSTRAINT PK_RATION_BUSINESS PRIMARY KEY (pk_business),
ts char(19) NULL,
dr smallint default 0 
);

create table declaration_company (
pk_company char(20) NOT NULL,
rowno varchar(50) NULL,
pk_group varchar(20) default '~' NULL,
pk_org varchar(20) default '~' NULL,
pk_org_v varchar(20) default '~' NULL,
pk_dept varchar(20) default '~' NULL,
num int NULL,
salary_plan varchar(20) default '~' NULL,
pay_date char(19) NULL,
pay_money decimal(28,8) NULL,
pk_psndoc varchar(20) default '~' NULL,
totalinsure decimal(28,8) NULL,
replenis_base decimal(28,8) NULL,
company_bear decimal(28,8) NULL,
vbdef1 varchar(101) NULL,
vbdef2 varchar(20) default '~' NULL,
vbdef3 varchar(20) default '~' NULL,
vbdef4 varchar(20) default '~' NULL,
vbdef5 varchar(101) NULL,
vbdef6 varchar(101) NULL,
vbdef7 varchar(101) NULL,
vbdef8 varchar(101) NULL,
vbdef9 varchar(101) NULL,
vbdef10 varchar(101) NULL,
vbdef11 varchar(101) NULL,
vbdef12 varchar(101) NULL,
vbdef13 varchar(101) NULL,
vbdef14 varchar(101) NULL,
vbdef15 varchar(101) NULL,
vbdef16 varchar(101) NULL,
vbdef17 varchar(101) NULL,
vbdef18 varchar(101) NULL,
vbdef19 varchar(101) NULL,
vbdef20 varchar(101) NULL,
pk_declaration char(20) NOT NULL,
CONSTRAINT PK_ARATION_COMPANY PRIMARY KEY (pk_company),
ts char(19) NULL,
dr smallint default 0 
);


