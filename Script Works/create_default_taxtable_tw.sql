INSERT INTO wa_taxbase (code, creationtime, creator, dr, itbltype, mdebuctamount, modifiedtime, modifier, name, name2, name3, name4, name5, name6, ndebuctamount, ndebuctlowest, ndebuctrate, ndeductcritn, nfixrate, pk_country, pk_group, pk_org, pk_wa_taxbase) VALUES ('incometax', '2017-07-13 14:03:10', '~', 0, 0, 0, '2017-07-13 14:09:52', '~', '所得扣繳級距稅額表', '所得扣繳級距稅額表', null, null, null, null, 0, 0, 0.000000, 0, 0.000000, '0001Z010000000079UJK', (select pk_group from org_group), (select pk_group from org_group), '1001A110000000000CIV');
INSERT INTO wa_taxbase (code, creationtime, creator, dr, itbltype, mdebuctamount, modifiedtime, modifier, name, name2, name3, name4, name5, name6, ndebuctamount, ndebuctlowest, ndebuctrate, ndeductcritn, nfixrate, pk_country, pk_group, pk_org, pk_wa_taxbase) VALUES ('fixedrate-5', '2017-07-13 14:08:24', '~', 0, 1, 0, '2017-07-13 14:09:52', '~', '固定稅率扣繳(5%)', '固定稅率扣繳(5%) 50', null, null, null, null, 0, 0, 0.000000, 0, 0.000000, '0001Z010000000079UJK', (select pk_group from org_group), (select pk_group from org_group), '1001A110000000000CJ3');
INSERT INTO wa_taxbase (code, creationtime, creator, dr, itbltype, mdebuctamount, modifiedtime, modifier, name, name2, name3, name4, name5, name6, ndebuctamount, ndebuctlowest, ndebuctrate, ndeductcritn, nfixrate, pk_country, pk_group, pk_org, pk_wa_taxbase) VALUES ('fixedrate-10', '2017-07-13 14:10:27', '~', 0, 1, 0, '2017-07-13 14:10:47', '~', '固定税率扣缴(10%)', '固定稅率扣繳(10%) 9A', null, null, null, null, 0, 0, 0.000000, 0, 0.000000, '0001Z010000000079UJK', (select pk_group from org_group), (select pk_group from org_group), '1001A110000000000CJ5');

INSERT INTO wa_taxtable (dr, itaxlevel, ndebuctamount, ndebuctrate, nmaxamount, nminamount, nquickdebuct, ntaxrate, pk_wa_taxbase, pk_wa_taxtable) VALUES (0, 1, 0, 0.000000, 540000.00000000, 0, 0, 5.000000, '1001A110000000000CIV', '1001A110000000000CIW');
INSERT INTO wa_taxtable (dr, itaxlevel, ndebuctamount, ndebuctrate, nmaxamount, nminamount, nquickdebuct, ntaxrate, pk_wa_taxbase, pk_wa_taxtable) VALUES (0, 2, 0, 0.000000, 1210000.00000000, 540001.00000000, 37800.00000000, 12.000000, '1001A110000000000CIV', '1001A110000000000CIX');
INSERT INTO wa_taxtable (dr, itaxlevel, ndebuctamount, ndebuctrate, nmaxamount, nminamount, nquickdebuct, ntaxrate, pk_wa_taxbase, pk_wa_taxtable) VALUES (0, 3, 0, 0.000000, 2420000.00000000, 1210001.00000000, 134600.00000000, 20.000000, '1001A110000000000CIV', '1001A110000000000CIZ');
INSERT INTO wa_taxtable (dr, itaxlevel, ndebuctamount, ndebuctrate, nmaxamount, nminamount, nquickdebuct, ntaxrate, pk_wa_taxbase, pk_wa_taxtable) VALUES (0, 4, 0, 0.000000, 4530000.00000000, 2420001.00000000, 376600.00000000, 30.000000, '1001A110000000000CIV', '1001A110000000000CJ0');
INSERT INTO wa_taxtable (dr, itaxlevel, ndebuctamount, ndebuctrate, nmaxamount, nminamount, nquickdebuct, ntaxrate, pk_wa_taxbase, pk_wa_taxtable) VALUES (0, 5, 0, 0.000000, 10310000.00000000, 4530001.00000000, 829600.00000000, 40.000000, '1001A110000000000CIV', '1001A110000000000CJ1');
INSERT INTO wa_taxtable (dr, itaxlevel, ndebuctamount, ndebuctrate, nmaxamount, nminamount, nquickdebuct, ntaxrate, pk_wa_taxbase, pk_wa_taxtable) VALUES (0, 6, 0, 0.000000, null, 10310001.00000000, 1345100.00000000, 45.000000, '1001A110000000000CIV', '1001A110000000000CJ2');
INSERT INTO wa_taxtable (dr, itaxlevel, ndebuctamount, ndebuctrate, nmaxamount, nminamount, nquickdebuct, ntaxrate, pk_wa_taxbase, pk_wa_taxtable) VALUES (0, 1, 0, 0.000000, null, 73501.00000000, 0, 5.000000, '1001A110000000000CJ3', '1001A110000000000CJ4');
INSERT INTO wa_taxtable (dr, itaxlevel, ndebuctamount, ndebuctrate, nmaxamount, nminamount, nquickdebuct, ntaxrate, pk_wa_taxbase, pk_wa_taxtable) VALUES (0, 1, 0, 0.000000, null, 21009.00000000, 0, 10.000000, '1001A110000000000CJ5', '1001A110000000000CJ6');
