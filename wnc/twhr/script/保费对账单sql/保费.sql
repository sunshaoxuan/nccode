---先执行第一局sql，
alter table twhr_baoaccounta  modify IDNO varchar2(50);
--如果不成功，执行以下sql,成功则不执行以下sql

ALTER TABLE twhr_baoaccounta RENAME COLUMN IDNO TO IDNOX;
ALTER TABLE twhr_baoaccounta ADD IDNO VARCHAR2(50);
UPDATE twhr_baoaccounta SET IDNO = CAST(IDNOX AS VARCHAR2(50));
ALTER TABLE twhr_baoaccounta DROP COLUMN IDNOX;



