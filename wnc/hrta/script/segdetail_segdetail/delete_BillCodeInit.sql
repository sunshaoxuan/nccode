DELETE FROM pub_bcr_candiattr WHERE pk_nbcr = '0001ZZ10000000015XK5';
DELETE FROM pub_bcr_elem WHERE pk_billcodebase in ( select pk_billcodebase from pub_bcr_RuleBase where nbcrcode = 'SGDT' );
DELETE FROM pub_bcr_RuleBase WHERE nbcrcode = 'SGDT';
DELETE FROM pub_bcr_nbcr WHERE pk_nbcr = '0001ZZ10000000015XK5';
DELETE FROM pub_bcr_OrgRela WHERE pk_billcodebase = '0001ZZ10000000015XK6';
DELETE FROM pub_bcr_RuleBase WHERE pk_billcodebase = '0001ZZ10000000015XK6';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ10000000015XK7';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ10000000015XK8';
DELETE FROM pub_bcr_elem WHERE pk_billcodeelem = '0001ZZ10000000015XK9';
