DELETE FROM pub_bcr_candiattr WHERE pk_nbcr = '0001ZZ10000000015XK5';
DELETE FROM pub_bcr_elem WHERE pk_billcodebase in ( select pk_billcodebase from pub_bcr_RuleBase where nbcrcode = 'SGDT' );
DELETE FROM pub_bcr_RuleBase WHERE nbcrcode = 'SGDT';
DELETE FROM pub_bcr_nbcr WHERE pk_nbcr = '0001ZZ10000000015XK5';
DELETE FROM pub_bcr_OrgRela WHERE pk_billcodebase = '0001ZZ10000000015XK6';
DELETE FROM pub_bcr_RuleBase WHERE pk_billcodebase = '0001ZZ10000000015XK6';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ10000000015XK7';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ10000000015XK8';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ10000000015XK9';
DELETE FROM bd_billtype2 WHERE pk_billtypeid = '0001ZZ10000000015XK0';
DELETE FROM bd_fwdbilltype WHERE pk_billtypeid = '0001ZZ10000000015XK0';
DELETE FROM pub_function WHERE pk_billtype = 'SGDT';
DELETE FROM pub_billaction WHERE pk_billtypeid = '0001ZZ10000000015XK0';
DELETE FROM pub_billactiongroup WHERE pk_billtype = 'SGDT';
DELETE FROM bd_billtype WHERE pk_billtypeid = '0001ZZ10000000015XK0';
delete from temppkts;
DELETE FROM sm_rule_type WHERE pk_rule_type = null;
DELETE FROM sm_permission_res WHERE pk_permission_res = null;
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ10000000015XK1';
DELETE FROM pub_billaction WHERE pk_billaction = '0001ZZ10000000015XK2';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ10000000015XK3';
DELETE FROM pub_busiclass WHERE pk_busiclass = '0001ZZ10000000015XK4';
DELETE FROM pub_systemplate_base where pk_systemplate = '0001ZZ10000000015XJZ';
delete from pub_print_datasource where ctemplateid = '0001ZZ10000000015XBS';
delete from pub_print_cell where ctemplateid = '0001ZZ10000000015XBS';
delete from pub_print_line where ctemplateid = '0001ZZ10000000015XBS';
delete from pub_print_variable where ctemplateid = '0001ZZ10000000015XBS';
delete from pub_print_template where ctemplateid = '0001ZZ10000000015XBS';
DELETE FROM pub_systemplate_base where pk_systemplate = '0001ZZ10000000015XBR';
delete from pub_query_condition where pk_templet = '0001ZZ10000000015XAV';
delete from pub_query_templet where id = '0001ZZ10000000015XAV';
DELETE FROM pub_systemplate_base where pk_systemplate = '0001ZZ10000000015XAU';
delete from pub_billtemplet_b where pk_billtemplet = '0001ZZ10000000015X9G';
delete from pub_billtemplet where pk_billtemplet = '0001ZZ10000000015X9G';
DELETE FROM pub_billtemplet_t WHERE pk_billtemplet = '0001ZZ10000000015X9G';
DELETE FROM sm_menuitemreg WHERE pk_menuitem = '0001ZZ10000000015X9F';
DELETE FROM sm_funcregister WHERE cfunid = '0001ZZ10000000015X9D';
DELETE FROM sm_paramregister WHERE pk_param = '0001ZZ10000000015X9E';
