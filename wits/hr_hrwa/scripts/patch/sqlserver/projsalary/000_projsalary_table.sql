create table wa_projsalary (
pk_projsalary char(20) NOT NULL,
pk_group varchar(20) default '~' NULL,
pk_org varchar(20) default '~' NULL,
pk_org_v varchar(20) default '~' NULL,
vbillcode varchar(40) NULL,
dbilldate char(19) NULL,
billmaker varchar(20) default '~' NULL,
dmakedate char(19) NULL,
creator varchar(20) default '~' NULL,
creationtime char(19) NULL,
modifier varchar(20) default '~' NULL,
modifiedtime char(19) NULL,
pk_wa_calss varchar(20) default '~' NULL,
cperiod varchar(50) NULL,
pk_psndoc varchar(20) default '~' NULL,
pk_project varchar(50) NULL,
pk_classitem varchar(20) default '~' NULL,
salaryamt decimal(28,8) NULL,
def1 varchar(101) NULL,
def2 varchar(101) NULL,
def3 varchar(101) NULL,
def4 varchar(101) NULL,
def5 varchar(101) NULL,
def6 varchar(101) NULL,
def7 varchar(101) NULL,
def8 varchar(101) NULL,
def9 varchar(101) NULL,
def10 varchar(101) NULL,
def11 varchar(101) NULL,
def12 varchar(101) NULL,
def13 varchar(101) NULL,
def14 varchar(101) NULL,
def15 varchar(101) NULL,
def16 varchar(101) NULL,
def17 varchar(101) NULL,
def18 varchar(101) NULL,
def19 varchar(101) NULL,
def20 varchar(101) NULL,
cbilltypeid varchar(20) default '~' NULL,
ctrantypeid varchar(20) default '~' NULL,
vtrantypecode varchar(20) default '~' NULL,
cbiztypeid varchar(20) default '~' NULL,
approver varchar(20) default '~' NULL,
taudittime char(19) NULL,
vapprovenote varchar(500) default '~' NULL,
fstatusflag int default 1 NULL,
CONSTRAINT PK_WA_PROJSALARY PRIMARY KEY (pk_projsalary),
ts char(19) NULL,
dr smallint default 0 
)
go

