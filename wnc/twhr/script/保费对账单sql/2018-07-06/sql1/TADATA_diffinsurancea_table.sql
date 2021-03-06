drop table twhr_diffinsurance;

create table twhr_diffinsurance (
id char(20) NOT NULL,
pk_group varchar(20) NOT NULL,
pk_org varchar(20) default '~' NULL,
pk_org_v varchar(20) default '~' NULL,
creator varchar(20) default '~' NULL,
creationtime char(19) NULL,
modifier varchar(20) default '~' NULL,
modifiedtime char(19) NULL,
begindate char(19) NULL,
enddate char(19) NULL,
pk_period varchar(100) NULL,
pk_psndoc varchar(20) default '~' NULL,
name varchar(50) NULL,
idno int NULL,
ichecktype int NULL,
idifftype int NULL,
org_amount decimal(28,8) NULL,
org_psnamount decimal(28,8) NULL,
org_orgamount decimal(28,8) NULL,
diff_amount decimal(28,8) NULL,
check_psnamount decimal(28,8) NULL,
check_orgamount decimal(28,8) NULL,
diff_psnamount decimal(28,8) NULL,
diff_orgamount decimal(28,8) NULL,
isadjusted varchar(50) NULL,
code varchar(50) NULL,
namea varchar(50) NULL,
CONSTRAINT PK_R_DIFFINSURANCE PRIMARY KEY (id),
ts char(19) NULL,
dr smallint default 0 
)


