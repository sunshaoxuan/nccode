
delete pub_print_cell where ( ctemplateid in ('0001ZZ10000000051FTZ') and (dr=0 or dr is null) )
;

delete pub_print_template where ( ctemplateid='0001ZZ10000000051FTZ' and (pk_corp='@@@@') and (dr=0 or dr is null) )
;


insert into pub_print_template(bdirector,bdispagenum,bdistotalpagenum,billspace,bnormalcolor,ctemplateid,devorg,dr,extendattr,ffontstyle,fpagination,ibotmargin,ibreakposition,ifontsize,igridcolor,ileftmargin,ipageheight,ipagelocate,ipagewidth,irightmargin,iscale,itopmargin,itype,layer,mdclass,model_type,modelheight,modelwidth,pk_corp,pk_org,prepare1,prepare2,ptemplateid,ts,vdefaultprinter,vfontname,vleftnote,vmidnote,vnodecode,vrightnote,vtemplatecode,vtemplatename) values( 'Y','N','N',null,'N','0001ZZ10000000051FTZ',null,0,'<nc.vo.pub.print.PrintTemplateExtVO>
  <isBindUp>false</isBindUp>
  <zdline__position>0.0</zdline__position>
  <pagehead__position>0.0</pagehead__position>
  <pagetail__position>0.0</pagetail__position>
  <pagenumber__position>0.0</pagenumber__position>
  <m__withFullPageNumber>false</m__withFullPageNumber>
  <baseLineWeight>0.65</baseLineWeight>
  <initPageNo>1</initPageNo>
</nc.vo.pub.print.PrintTemplateExtVO>',0,0,10,0,9,-4144960,10,596,'21',842,10,100,10,1,1,'80a16e9a-9cfc-48ca-a1cb-718050e3f125',null,null,null,'@@@@',null,null,null,null,'2018-09-26 10:47:37',null,'dialog.plain',null,null,'68J61710',null,'68J61710','二代健保申报主表')
;


insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004004004004','0001ZZ10000000051G45','1004000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,320,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004005004005','0001ZZ10000000051G46','1004000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,400,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004006004006','0001ZZ10000000051G47','1004000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,480,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004007004007','0001ZZ10000000051G48','1004000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,560,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004008004008','0001ZZ10000000051G49','1004000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,640,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004009004009','0001ZZ10000000051G4A','1004000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,720,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004010004010','0001ZZ10000000051G4B','1004000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,800,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004011004011','0001ZZ10000000051G4C','1004000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,880,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004012004012','0001ZZ10000000051G4D','1004000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,960,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004013004013','0001ZZ10000000051G4E','1004000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,1040,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005000005000','0001ZZ10000000051G4G','1005000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,95,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','非兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005000005013','0001ZZ10000000051G4U','0','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,95,0,1120.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','非兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005001005001','0001ZZ10000000051G4H','1005000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,95,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','非兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005002005002','0001ZZ10000000051G4I','1005000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,95,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','非兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005003005003','0001ZZ10000000051G4J','1005000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,95,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','非兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005004005004','0001ZZ10000000051G4K','1005000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,95,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','非兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005005005005','0001ZZ10000000051G4L','1005000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,95,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','非兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005006005006','0001ZZ10000000051G4M','1005000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,480,95,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','非兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005007005007','0001ZZ10000000051G4N','1005000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,560,95,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','非兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005008005008','0001ZZ10000000051G4O','1005000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,640,95,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','非兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005009005009','0001ZZ10000000051G4P','1005000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,720,95,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','非兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005010005010','0001ZZ10000000051G4Q','1005000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,800,95,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','非兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005011005011','0001ZZ10000000051G4R','1005000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,880,95,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','非兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005012005012','0001ZZ10000000051G4S','1005000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,960,95,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','非兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','005013005013','0001ZZ10000000051G4T','1005000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,1040,95,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','非兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004003004003','0001ZZ10000000051G44','1004000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,240,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004002004002','0001ZZ10000000051G43','1004000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,160,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004001004001','0001ZZ10000000051G42','1004000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,80,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004000004013','0001ZZ10000000051G4F','0','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,0,90,0,1120.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','004000004000','0001ZZ10000000051G41','1004000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,0,90,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003013003013','0001ZZ10000000051G40','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,1040,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单据日期','billdate')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003012003012','0001ZZ10000000051G3Z','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,960,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单据日期',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003011003011','0001ZZ10000000051G3Y','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,880,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单据版本pk','billversionpk')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003010003010','0001ZZ10000000051G3X','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,800,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单据版本pk',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003009003009','0001ZZ10000000051G3W','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,720,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','修订枚举','emendenum')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003008003008','0001ZZ10000000051G3V','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,640,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','修订枚举',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003007003007','0001ZZ10000000051G3U','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,560,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','来源单据id','srcbillid')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003006003006','0001ZZ10000000051G3T','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,480,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','来源单据id',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003005003005','0001ZZ10000000051G3S','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','来源单据类型','srcbilltype')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003004003004','0001ZZ10000000051G3R','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','来源单据类型',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003003003003','0001ZZ10000000051G3Q','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','交易类型pk','transtypepk')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003002003002','0001ZZ10000000051G3P','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','交易类型pk',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003001003001','0001ZZ10000000051G3O','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单据类型','billtype')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','003000003000','0001ZZ10000000051G3N','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,70,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单据类型',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002013002013','0001ZZ10000000051G3M','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,1040,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','交易类型','transtype')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002012002012','0001ZZ10000000051G3L','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,960,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','交易类型',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002011002011','0001ZZ10000000051G3K','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,880,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','审批批语','approvenote')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002010002010','0001ZZ10000000051G3J','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,800,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','审批批语',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002009002009','0001ZZ10000000051G3I','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,720,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','审批状态','approvestatus')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002008002008','0001ZZ10000000051G3H','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,640,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','审批状态',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002007002007','0001ZZ10000000051G3G','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,560,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','业务类型','busitype')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002006002006','0001ZZ10000000051G3F','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,480,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','业务类型',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002005002005','0001ZZ10000000051G3E','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','所属组织','pkorg')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002004002004','0001ZZ10000000051G3D','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','所属组织',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002003002003','0001ZZ10000000051G3C','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单据号','billno')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002002002002','0001ZZ10000000051G3B','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单据号',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002001002001','0001ZZ10000000051G3A','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单据ID','billid')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','002000002000','0001ZZ10000000051G39','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,50,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单据ID',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001013001013','0001ZZ10000000051G38','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,1040,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','最后修改时间','lastmaketime')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001012001012','0001ZZ10000000051G37','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,960,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','最后修改时间',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001011001011','0001ZZ10000000051G36','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,880,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','制单时间','maketime')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001010001010','0001ZZ10000000051G35','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,800,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','制单时间',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001009001009','0001ZZ10000000051G34','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,720,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','名称','name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001008001008','0001ZZ10000000051G33','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,640,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','名称',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001007001007','0001ZZ10000000051G32','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,560,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','编码','code')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001006001006','0001ZZ10000000051G31','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,480,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','编码',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001005001005','0001ZZ10000000051G30','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织版本','pk_org_v.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001004001004','0001ZZ10000000051G2Z','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织版本',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001003001003','0001ZZ10000000051G2Y','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织','pk_org.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001002001002','0001ZZ10000000051G2X','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001001001001','0001ZZ10000000051G2W','0','1110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','集团','pk_group.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','001000001000','0001ZZ10000000051G2V','0','0110','10',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,30,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','集团',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000013000013','0001ZZ10000000051G2T','1000000','0110','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,1040,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','二代健保申报主表',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000012000012','0001ZZ10000000051G2S','1000000','0110','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,960,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','二代健保申报主表',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000011000011','0001ZZ10000000051G2R','1000000','0110','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,880,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','二代健保申报主表',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000010000010','0001ZZ10000000051G2Q','1000000','0110','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,800,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','二代健保申报主表',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000009000009','0001ZZ10000000051G2P','1000000','0110','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,720,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','二代健保申报主表',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000008000008','0001ZZ10000000051G2O','1000000','0110','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,640,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','二代健保申报主表',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000007000007','0001ZZ10000000051G2N','1000000','0110','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,560,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','二代健保申报主表',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000006000006','0001ZZ10000000051G2M','1000000','0110','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,480,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','二代健保申报主表',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000005000005','0001ZZ10000000051G2L','1000000','0110','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,400,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','二代健保申报主表',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000000000013','0001ZZ10000000051G2U','0','0110','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,0,0,0,1120.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','二代健保申报主表',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006000006000','0001ZZ10000000051G4V','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,115,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','行号',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006001006001','0001ZZ10000000051G4W','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,115,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','集团',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006002006002','0001ZZ10000000051G4X','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,115,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006003006003','0001ZZ10000000051G4Y','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,115,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织版本',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006004006004','0001ZZ10000000051G4Z','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,115,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','部门',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006005006005','0001ZZ10000000051G50','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,115,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','序号',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006006006006','0001ZZ10000000051G51','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,480,115,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','给付日期',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006007006007','0001ZZ10000000051G52','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,560,115,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','所得人身份证号',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006008006008','0001ZZ10000000051G53','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,640,115,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','所得人姓名',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006009006009','0001ZZ10000000051G54','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,720,115,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单次给付奖金金额',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006010006010','0001ZZ10000000051G55','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,800,115,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单次扣缴补充保险费金额',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000004000004','0001ZZ10000000051G2K','1000000','0110','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,320,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','二代健保申报主表',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000003000003','0001ZZ10000000051G2J','1000000','0110','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,240,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','二代健保申报主表',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000002000002','0001ZZ10000000051G2I','1000000','0110','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,160,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','二代健保申报主表',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000001000001','0001ZZ10000000051G2H','1000000','0110','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,80,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','二代健保申报主表',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','000000000000','0001ZZ10000000051G2G','1000000','0110','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,16,30.00000000,0,0,null,null,null,0,0,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:37','Dialog','二代健保申报主表',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','010000010000','0001ZZ10000000051G6H','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,180,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','行号',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','011002011002','0001ZZ10000000051G6X','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,200,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织','id_parttimebvo.pk_org.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','011003011003','0001ZZ10000000051G6Y','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,200,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织版本','id_parttimebvo.pk_org_v.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','011004011004','0001ZZ10000000051G6Z','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,200,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','部门','id_parttimebvo.pk_dept.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','011005011005','0001ZZ10000000051G70','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,200,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','序号','id_parttimebvo.num')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','011006011006','0001ZZ10000000051G71','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,480,200,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','给付日期','id_parttimebvo.pay_date')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','011007011007','0001ZZ10000000051G72','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,560,200,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','所得人身份证号','id_parttimebvo.beneficiary_id')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','011008011008','0001ZZ10000000051G73','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,640,200,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','所得人姓名','id_parttimebvo.beneficiary_name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','011009011009','0001ZZ10000000051G74','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,720,200,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单次所得给付金额','id_parttimebvo.single_pay')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','011010011010','0001ZZ10000000051G75','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,800,200,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单次扣缴补充保险费金额','id_parttimebvo.single_withholding')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','011011011011','0001ZZ10000000051G76','0','0110','N',null,'00000000','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,880,200,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','011012011012','0001ZZ10000000051G77','0','0110','N',null,'00000000','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,960,200,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','011013011013','0001ZZ10000000051G78','0','0110','N',null,'00000000','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,1040,200,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','012000012000','0001ZZ10000000051G79','1012000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,0,220,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','012001012001','0001ZZ10000000051G7A','1012000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,80,220,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','012002012002','0001ZZ10000000051G7B','1012000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,160,220,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','012003012003','0001ZZ10000000051G7C','1012000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,240,220,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','012004012004','0001ZZ10000000051G7D','1012000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,320,220,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','012005012005','0001ZZ10000000051G7E','1012000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,400,220,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','012006012006','0001ZZ10000000051G7F','1012000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,480,220,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','012007012007','0001ZZ10000000051G7G','1012000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,560,220,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','012008012008','0001ZZ10000000051G7H','1012000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,640,220,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','012009012009','0001ZZ10000000051G7I','1012000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,720,220,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','012010012010','0001ZZ10000000051G7J','1012000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,800,220,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','012011012011','0001ZZ10000000051G7K','1012000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,880,220,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','012012012012','0001ZZ10000000051G7L','1012000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,960,220,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','012013012013','0001ZZ10000000051G7M','1012000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,1040,220,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','012000012013','0001ZZ10000000051G7N','0','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,0,220,0,1120.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','013000013000','0001ZZ10000000051G7O','1013000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,225,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','执行业务所得补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','013001013001','0001ZZ10000000051G7P','1013000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,225,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','执行业务所得补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','013002013002','0001ZZ10000000051G7Q','1013000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,225,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','执行业务所得补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','010001010001','0001ZZ10000000051G6I','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,180,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','集团',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','010002010002','0001ZZ10000000051G6J','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,180,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','010003010003','0001ZZ10000000051G6K','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,180,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织版本',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','010004010004','0001ZZ10000000051G6L','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,180,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','部门',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','010005010005','0001ZZ10000000051G6M','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,180,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','序号',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','010006010006','0001ZZ10000000051G6N','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,480,180,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','给付日期',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','010007010007','0001ZZ10000000051G6O','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,560,180,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','所得人身份证号',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','010008010008','0001ZZ10000000051G6P','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,640,180,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','所得人姓名',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','010009010009','0001ZZ10000000051G6Q','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,720,180,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单次所得给付金额',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','010010010010','0001ZZ10000000051G6R','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,800,180,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单次扣缴补充保险费金额',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006011006011','0001ZZ10000000051G56','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,880,115,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','投保单位代号',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006012006012','0001ZZ10000000051G57','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,960,115,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','扣费当月投保金额',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','006013006013','0001ZZ10000000051G58','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,1040,115,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','同年度累计奖金金额',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007000007000','0001ZZ10000000051G59','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,135,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','行号','id_nonparttimebvo.rowno')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007001007001','0001ZZ10000000051G5A','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,135,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','集团','id_nonparttimebvo.pk_group.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007002007002','0001ZZ10000000051G5B','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,135,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织','id_nonparttimebvo.pk_org.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007003007003','0001ZZ10000000051G5C','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,135,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织版本','id_nonparttimebvo.pk_org_v.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007004007004','0001ZZ10000000051G5D','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,135,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','部门','id_nonparttimebvo.pk_dept.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007005007005','0001ZZ10000000051G5E','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,135,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','序号','id_nonparttimebvo.num')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007006007006','0001ZZ10000000051G5F','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,480,135,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','给付日期','id_nonparttimebvo.pay_date')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007007007007','0001ZZ10000000051G5G','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,560,135,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','所得人身份证号','id_nonparttimebvo.beneficiary_id')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007008007008','0001ZZ10000000051G5H','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,640,135,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','所得人姓名','id_nonparttimebvo.beneficiary_name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007009007009','0001ZZ10000000051G5I','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,720,135,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单次给付奖金金额','id_nonparttimebvo.single_pay')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007010007010','0001ZZ10000000051G5J','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,800,135,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单次扣缴补充保险费金额','id_nonparttimebvo.single_withholding')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007011007011','0001ZZ10000000051G5K','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,880,135,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','投保单位代号','id_nonparttimebvo.insurance_unit_code')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007012007012','0001ZZ10000000051G5L','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,960,135,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','扣费当月投保金额','id_nonparttimebvo.deductions_month_insure')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','007013007013','0001ZZ10000000051G5M','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,1040,135,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','同年度累计奖金金额','id_nonparttimebvo.totalbonusforyear')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008000008000','0001ZZ10000000051G5N','1008000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,0,155,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008001008001','0001ZZ10000000051G5O','1008000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,80,155,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008002008002','0001ZZ10000000051G5P','1008000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,160,155,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008003008003','0001ZZ10000000051G5Q','1008000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,240,155,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008004008004','0001ZZ10000000051G5R','1008000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,320,155,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008005008005','0001ZZ10000000051G5S','1008000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,400,155,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008006008006','0001ZZ10000000051G5T','1008000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,480,155,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008007008007','0001ZZ10000000051G5U','1008000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,560,155,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008008008008','0001ZZ10000000051G5V','1008000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,640,155,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008009008009','0001ZZ10000000051G5W','1008000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,720,155,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008010008010','0001ZZ10000000051G5X','1008000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,800,155,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008011008011','0001ZZ10000000051G5Y','1008000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,880,155,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008012008012','0001ZZ10000000051G5Z','1008000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,960,155,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008013008013','0001ZZ10000000051G60','1008000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,1040,155,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','008000008013','0001ZZ10000000051G61','0','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,0,155,0,1120.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009000009000','0001ZZ10000000051G62','1009000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,160,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009001009001','0001ZZ10000000051G63','1009000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,160,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009002009002','0001ZZ10000000051G64','1009000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,160,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009003009003','0001ZZ10000000051G65','1009000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,160,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009004009004','0001ZZ10000000051G66','1009000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,160,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009005009005','0001ZZ10000000051G67','1009000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,160,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009006009006','0001ZZ10000000051G68','1009000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,480,160,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009007009007','0001ZZ10000000051G69','1009000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,560,160,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009008009008','0001ZZ10000000051G6A','1009000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,640,160,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009009009009','0001ZZ10000000051G6B','1009000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,720,160,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009010009010','0001ZZ10000000051G6C','1009000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,800,160,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009011009011','0001ZZ10000000051G6D','1009000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,880,160,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009012009012','0001ZZ10000000051G6E','1009000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,960,160,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009013009013','0001ZZ10000000051G6F','1009000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,1040,160,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','010011010011','0001ZZ10000000051G6S','0','0110','N',null,'00000000','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,880,180,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','010012010012','0001ZZ10000000051G6T','0','0110','N',null,'00000000','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,960,180,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','010013010013','0001ZZ10000000051G6U','0','0110','N',null,'00000000','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,1040,180,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','011000011000','0001ZZ10000000051G6V','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,200,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','行号','id_parttimebvo.rowno')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','009000009013','0001ZZ10000000051G6G','0','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,160,0,1120.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','兼职人员补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','011001011001','0001ZZ10000000051G6W','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,200,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','集团','id_parttimebvo.pk_group.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','013003013003','0001ZZ10000000051G7R','1013000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,225,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','执行业务所得补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','017008017008','0001ZZ10000000051G9I','1017000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,640,290,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','017009017009','0001ZZ10000000051G9J','1017000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,720,290,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','017010017010','0001ZZ10000000051G9K','1017000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,800,290,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','017011017011','0001ZZ10000000051G9L','1017000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,880,290,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','017012017012','0001ZZ10000000051G9M','1017000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,960,290,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','017013017013','0001ZZ10000000051G9N','1017000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,1040,290,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','017000017013','0001ZZ10000000051G9O','0','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,290,0,1120.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','018000018000','0001ZZ10000000051G9P','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,310,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','行号',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','018001018001','0001ZZ10000000051G9Q','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,310,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','集团',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','018002018002','0001ZZ10000000051G9R','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,310,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','018003018003','0001ZZ10000000051G9S','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,310,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织版本',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','018004018004','0001ZZ10000000051G9T','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,310,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','部门',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','018005018005','0001ZZ10000000051G9U','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,310,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','序号',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','018006018006','0001ZZ10000000051G9V','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,480,310,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','薪资方案',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','018007018007','0001ZZ10000000051G9W','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,560,310,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','给付日期',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','018008018008','0001ZZ10000000051G9X','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,640,310,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','给付金额',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','018009018009','0001ZZ10000000051G9Y','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,720,310,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','人员',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','018010018010','0001ZZ10000000051G9Z','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,800,310,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','投保总额',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','018011018011','0001ZZ10000000051GA0','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,880,310,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','补充保费费基',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','018012018012','0001ZZ10000000051GA1','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,960,310,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司承担补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','018013018013','0001ZZ10000000051GA2','0','0110','N',null,'00000000','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,1040,310,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','019000019000','0001ZZ10000000051GA3','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,330,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','行号','id_companybvo.rowno')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','019001019001','0001ZZ10000000051GA4','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,330,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','集团','id_companybvo.pk_group.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','019002019002','0001ZZ10000000051GA5','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,330,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织','id_companybvo.pk_org.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','019003019003','0001ZZ10000000051GA6','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,330,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织版本','id_companybvo.pk_org_v.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','019004019004','0001ZZ10000000051GA7','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,330,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','部门','id_companybvo.pk_dept.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','019005019005','0001ZZ10000000051GA8','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,330,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','序号','id_companybvo.num')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','019006019006','0001ZZ10000000051GA9','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,480,330,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','薪资方案','id_companybvo.pk_wa_class.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','019007019007','0001ZZ10000000051GAA','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,560,330,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','给付日期','id_companybvo.pay_date')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','019008019008','0001ZZ10000000051GAB','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,640,330,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','给付金额','id_companybvo.pay_money')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','013004013004','0001ZZ10000000051G7S','1013000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,225,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','执行业务所得补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','013005013005','0001ZZ10000000051G7T','1013000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,225,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','执行业务所得补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','013006013006','0001ZZ10000000051G7U','1013000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,480,225,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','执行业务所得补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','013007013007','0001ZZ10000000051G7V','1013000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,560,225,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','执行业务所得补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','013008013008','0001ZZ10000000051G7W','1013000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,640,225,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','执行业务所得补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','013009013009','0001ZZ10000000051G7X','1013000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,720,225,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','执行业务所得补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','013010013010','0001ZZ10000000051G7Y','1013000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,800,225,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','执行业务所得补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','013011013011','0001ZZ10000000051G7Z','1013000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,880,225,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','执行业务所得补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','013012013012','0001ZZ10000000051G80','1013000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,960,225,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','执行业务所得补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','013013013013','0001ZZ10000000051G81','1013000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,1040,225,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','执行业务所得补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','013000013013','0001ZZ10000000051G82','0','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,225,0,1120.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','执行业务所得补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','014000014000','0001ZZ10000000051G83','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,245,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','行号',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','014001014001','0001ZZ10000000051G84','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,245,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','集团',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','014002014002','0001ZZ10000000051G85','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,245,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','014003014003','0001ZZ10000000051G86','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,245,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织版本',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','014004014004','0001ZZ10000000051G87','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,245,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','部门',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','014005014005','0001ZZ10000000051G88','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,245,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','序号',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','014006014006','0001ZZ10000000051G89','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,480,245,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','给付日期',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','014007014007','0001ZZ10000000051G8A','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,560,245,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','所得人身份证号',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','014008014008','0001ZZ10000000051G8B','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,640,245,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','所得人姓名',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','014009014009','0001ZZ10000000051G8C','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,720,245,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单次给付金额',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','014010014010','0001ZZ10000000051G8D','0','0110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,800,245,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单次扣缴补充保险费金额',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','014011014011','0001ZZ10000000051G8E','0','0110','N',null,'00000000','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,880,245,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','014012014012','0001ZZ10000000051G8F','0','0110','N',null,'00000000','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,960,245,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','014013014013','0001ZZ10000000051G8G','0','0110','N',null,'00000000','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,1040,245,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','015000015000','0001ZZ10000000051G8H','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,265,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','行号','id_businessbvo.rowno')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','015001015001','0001ZZ10000000051G8I','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,265,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','集团','id_businessbvo.pk_group.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','015002015002','0001ZZ10000000051G8J','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,265,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织','id_businessbvo.pk_org.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','015003015003','0001ZZ10000000051G8K','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,265,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','组织版本','id_businessbvo.pk_org_v.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','015004015004','0001ZZ10000000051G8L','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,265,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','部门','id_businessbvo.pk_dept.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','015005015005','0001ZZ10000000051G8M','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,265,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','序号','id_businessbvo.num')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','015006015006','0001ZZ10000000051G8N','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,480,265,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','给付日期','id_businessbvo.pay_date')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','015007015007','0001ZZ10000000051G8O','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,560,265,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','所得人身份证号','id_businessbvo.beneficiary_id')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','015008015008','0001ZZ10000000051G8P','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,640,265,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','所得人姓名','id_businessbvo.beneficiary_name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','015009015009','0001ZZ10000000051G8Q','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,720,265,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单次给付金额','id_businessbvo.single_pay')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','015010015010','0001ZZ10000000051G8R','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,800,265,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','单次扣缴补充保险费金额','id_businessbvo.single_withholding')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','015011015011','0001ZZ10000000051G8S','0','0110','N',null,'00000000','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,880,265,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','015012015012','0001ZZ10000000051G8T','0','0110','N',null,'00000000','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,960,265,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','015013015013','0001ZZ10000000051G8U','0','0110','N',null,'00000000','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,1040,265,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','016000016000','0001ZZ10000000051G8V','1016000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,0,285,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','016001016001','0001ZZ10000000051G8W','1016000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,80,285,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','016002016002','0001ZZ10000000051G8X','1016000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,160,285,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','016003016003','0001ZZ10000000051G8Y','1016000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,240,285,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','016004016004','0001ZZ10000000051G8Z','1016000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,320,285,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','016000016013','0001ZZ10000000051G99','0','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,0,285,0,1120.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','016005016005','0001ZZ10000000051G90','1016000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,400,285,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','016006016006','0001ZZ10000000051G91','1016000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,480,285,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','016007016007','0001ZZ10000000051G92','1016000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,560,285,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','016008016008','0001ZZ10000000051G93','1016000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,640,285,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','016009016009','0001ZZ10000000051G94','1016000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,720,285,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','016010016010','0001ZZ10000000051G95','1016000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,800,285,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','016011016011','0001ZZ10000000051G96','1016000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,880,285,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','016012016012','0001ZZ10000000051G97','1016000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,960,285,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','016013016013','0001ZZ10000000051G98','1016000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,1040,285,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','017000017000','0001ZZ10000000051G9A','1017000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,0,290,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','017001017001','0001ZZ10000000051G9B','1017000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,80,290,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','017002017002','0001ZZ10000000051G9C','1017000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,160,290,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','017003017003','0001ZZ10000000051G9D','1017000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,240,290,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','017004017004','0001ZZ10000000051G9E','1017000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,320,290,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','017005017005','0001ZZ10000000051G9F','1017000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,400,290,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','017006017006','0001ZZ10000000051G9G','1017000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,480,290,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','017007017007','0001ZZ10000000051G9H','1017000','0020','10',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,560,290,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司补充保费',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','019009019009','0001ZZ10000000051GAC','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,720,330,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','人员','id_companybvo.pk_psndoc.name')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','019010019010','0001ZZ10000000051GAD','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,800,330,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','投保总额','id_companybvo.totalinsure')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','019011019011','0001ZZ10000000051GAE','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,880,330,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','补充保费费基','id_companybvo.replenis_base')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','019012019012','0001ZZ10000000051GAF','0','1110','N',null,'11111111','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,960,330,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','公司承担补充保费','id_companybvo.company_bear')
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','019013019013','0001ZZ10000000051GAG','0','0110','N',null,'00000000','0001ZZ10000000051FTZ',0,0,null,0,-1,0,-16777216,12,20.00000000,0,0,null,null,null,1040,330,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','020000020000','0001ZZ10000000051GAH','1020000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,0,350,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','020001020001','0001ZZ10000000051GAI','1020000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,80,350,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','020002020002','0001ZZ10000000051GAJ','1020000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,160,350,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','020003020003','0001ZZ10000000051GAK','1020000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,240,350,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','020004020004','0001ZZ10000000051GAL','1020000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,320,350,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','020005020005','0001ZZ10000000051GAM','1020000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,400,350,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','020006020006','0001ZZ10000000051GAN','1020000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,480,350,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','020007020007','0001ZZ10000000051GAO','1020000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,560,350,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','020008020008','0001ZZ10000000051GAP','1020000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,640,350,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','020009020009','0001ZZ10000000051GAQ','1020000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,720,350,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','020010020010','0001ZZ10000000051GAR','1020000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,800,350,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','020011020011','0001ZZ10000000051GAS','1020000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,880,350,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','020012020012','0001ZZ10000000051GAT','1020000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,960,350,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','020013020013','0001ZZ10000000051GAU','1020000','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,1040,350,0,80.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
insert into pub_print_cell(bunderline,ccellcode,ccellid,ccombineproperty,ccontentproperty,cisfrozen,cisheadtail,clineproperty,ctemplateid,dr,ffontstyle,fixedstyle,fshadowtype,ibgcolor,ibotlinecolor,ifgcolor,ifonrsize,iheight,ileftlinecolor,irightcolor,isdycolgroup,isdynrowheight,islastpagetrail,istartx,istarty,itoplinecolor,iwidth,options,prepare1,prepare2,table_code,ts,vfontname,vtext,vvar) values( '0','020000020013','0001ZZ10000000051GAV','0','0020','Y',null,'00000000','0001ZZ10000000051FTZ',0,1,null,0,-1,0,-16777216,12,5.00000000,0,0,null,null,null,0,350,0,1120.00000000,'<nc.vo.pub.print.PrintCellExtVO>
  <height__decimal>0.0</height__decimal>
  <textDirection>0</textDirection>
  <lineSpaceBetween>0</lineSpaceBetween>
  <formatType>0</formatType>
</nc.vo.pub.print.PrintCellExtVO>',null,null,null,'2018-09-26 10:47:38','Dialog','',null)
;
