DELETE FROM bd_billtype2 WHERE pk_billtypeid = '0001ZZ10000000015QJP';
DELETE FROM bd_fwdbilltype WHERE pk_billtypeid = '0001ZZ10000000015QJP';
DELETE FROM pub_function WHERE pk_billtype = 'SGRL';
DELETE FROM pub_billaction WHERE pk_billtypeid = '0001ZZ10000000015QJP';
DELETE FROM pub_billactiongroup WHERE pk_billtype = 'SGRL';
DELETE FROM bd_billtype WHERE pk_billtypeid = '0001ZZ10000000015QJP';
delete from temppkts;
DELETE FROM sm_rule_type WHERE pk_rule_type = null;
DELETE FROM sm_permission_res WHERE pk_permission_res = null;
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ10000000015QJQ';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ10000000015QJR';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ10000000015QJS';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ10000000015QJT';
