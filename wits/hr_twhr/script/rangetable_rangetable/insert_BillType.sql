INSERT INTO bd_billtype (ts, iseditableproperty , pk_billtypecode , ncbrcode , parentbilltype , canextendtransaction , isbizflowbill , istransaction , datafinderclz , referclassname , isaccount , isroot , pk_org , component , billtypename , billcoderule , emendenumclass , dr , nodecode , isenablebutton , pk_billtypeid , systemcode , classname , checkclassname , accountclass , islock , forwardbilltype , billtypename2 , billtypename3 , transtype_class , billtypename4 , billtypename5 , billtypename6 , pk_group , webnodecode , billstyle , def3 , def2 , isapprovebill , wherestring , def1 ) VALUES ('2014-07-29 17:21:25', null , 'TWRT' , '~' , '~' , 'Y' , null , 'N' , null , null , null , null , 'GLOBLE00000000000000' , 'rangetable' , 'TWRT' , '~' , null , null , '68861025' , null , '0001ZZ10000000001GYB' , 'TWHR' , null , null , null , null , null , null , null , null , null , null , null , '~' , '~' , null , null , null , 'Y' , null , null );
INSERT INTO pub_billaction (ts, finishflag , showhint6 , showhint5 , showhint4 , showhint2 , showhint3 , constrictflag , action_type , actionstyle , showhint , dr , pk_billtype , pushflag , actionstyleremark , pk_billtypeid , controlflag , actionnote6 , pk_billaction , actiontype , actionnote4 , actionnote , actionnote5 , actionnote2 , actionnote3 ) VALUES ('2014-07-29 17:21:26', 'N' , null , null , null , null , null , 'N' , 30 , '3' , null , null , 'TWRT' , null , null , '0001ZZ10000000001GYB' , 'N' , null , '0001ZZ10000000001GYC' , 'DELETE' , null , '删除' , null , null , null );
INSERT INTO pub_billaction (ts, finishflag , showhint6 , showhint5 , showhint4 , showhint2 , showhint3 , constrictflag , action_type , actionstyle , showhint , dr , pk_billtype , pushflag , actionstyleremark , pk_billtypeid , controlflag , actionnote6 , pk_billaction , actiontype , actionnote4 , actionnote , actionnote5 , actionnote2 , actionnote3 ) VALUES ('2014-07-29 17:21:26', 'N' , null , null , null , null , null , 'Y' , 31 , '1' , null , null , 'TWRT' , null , null , '0001ZZ10000000001GYB' , 'N' , null , '0001ZZ10000000001GYD' , 'SAVEBASE' , null , '保存' , null , null , null );
INSERT INTO pub_busiclass (ts, pk_billtypeid , pk_businesstype , classname , isbefore , actiontype , pk_group , dr , pk_billtype , pk_busiclass ) VALUES ('2014-07-29 17:21:26', '0001ZZ10000000001GYB' , '~' , 'N_TWRT_DELETE' , 'N' , 'DELETE' , '~' , 0 , 'TWRT' , '0001ZZ10000000001GYE' );
INSERT INTO pub_busiclass (ts, pk_billtypeid , pk_businesstype , classname , isbefore , actiontype , pk_group , dr , pk_billtype , pk_busiclass ) VALUES ('2014-07-29 17:21:26', '0001ZZ10000000001GYB' , '~' , 'N_TWRT_SAVEBASE' , 'N' , 'SAVEBASE' , '~' , 0 , 'TWRT' , '0001ZZ10000000001GYF' );
