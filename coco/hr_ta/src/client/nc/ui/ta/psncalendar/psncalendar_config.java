package nc.ui.ta.psncalendar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class psncalendar_config extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.vo.ta.pub.TALoginContext getContext(){
 if(context.get("context")!=null)
 return (nc.vo.ta.pub.TALoginContext)context.get("context");
  nc.vo.ta.pub.TALoginContext bean = new nc.vo.ta.pub.TALoginContext();
  context.put("context",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.vo.bd.meta.BDObjectAdpaterFactory getBoadatorfactory(){
 if(context.get("boadatorfactory")!=null)
 return (nc.vo.bd.meta.BDObjectAdpaterFactory)context.get("boadatorfactory");
  nc.vo.bd.meta.BDObjectAdpaterFactory bean = new nc.vo.bd.meta.BDObjectAdpaterFactory();
  context.put("boadatorfactory",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.psncalendar.model.PsnCalendarAppModel getModel(){
 if(context.get("model")!=null)
 return (nc.ui.ta.psncalendar.model.PsnCalendarAppModel)context.get("model");
  nc.ui.ta.psncalendar.model.PsnCalendarAppModel bean = new nc.ui.ta.psncalendar.model.PsnCalendarAppModel();
  context.put("model",bean);
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.pub.QueryEditorListener getQueryEditorListener(){
 if(context.get("queryEditorListener")!=null)
 return (nc.ui.ta.pub.QueryEditorListener)context.get("queryEditorListener");
  nc.ui.ta.pub.QueryEditorListener bean = new nc.ui.ta.pub.QueryEditorListener();
  context.put("queryEditorListener",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.ActionContributors getToftpanelActionContributors(){
 if(context.get("toftpanelActionContributors")!=null)
 return (nc.ui.uif2.actions.ActionContributors)context.get("toftpanelActionContributors");
  nc.ui.uif2.actions.ActionContributors bean = new nc.ui.uif2.actions.ActionContributors();
  context.put("toftpanelActionContributors",bean);
  bean.setContributors(getManagedList0());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList0(){  List list = new ArrayList();  list.add(getCalendarActions());  list.add(getGridActions());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getCalendarActions(){
 if(context.get("calendarActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("calendarActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getCalendarView());  context.put("calendarActions",bean);
  bean.setActions(getManagedList1());
  bean.setEditActions(getManagedList2());
  bean.setModel(getModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList1(){  List list = new ArrayList();  list.add(getEditAction());  list.add(getQueryAction());  list.add(getRefreshAction());  list.add(getNullaction());  list.add(getViewDetailAction());  return list;}

private List getManagedList2(){  List list = new ArrayList();  list.add(getSaveAction());  list.add(getNullaction());  list.add(getCancelAction());  return list;}

public nc.ui.uif2.actions.StandAloneToftPanelActionContainer getGridActions(){
 if(context.get("gridActions")!=null)
 return (nc.ui.uif2.actions.StandAloneToftPanelActionContainer)context.get("gridActions");
  nc.ui.uif2.actions.StandAloneToftPanelActionContainer bean = new nc.ui.uif2.actions.StandAloneToftPanelActionContainer(getGridView());  context.put("gridActions",bean);
  bean.setActions(getManagedList3());
  bean.setEditActions(getManagedList4());
  bean.setModel(getModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList3(){  List list = new ArrayList();  list.add(getEditActionGroup());  list.add(getNullaction());  list.add(getQueryAction());  list.add(getRefreshAction());  list.add(getNullaction());  list.add(getArrangeCalendarActionGroup());  list.add(getViewDetailAction());  list.add(getNullaction());  list.add(getExOrImpActionGroup());  list.add(getPrintActiongroup());  return list;}

private List getManagedList4(){  List list = new ArrayList();  list.add(getSaveAction());  list.add(getNullaction());  list.add(getCancelAction());  return list;}

public nc.ui.ta.psncalendar.model.PsnCalendarAppModelDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.ta.psncalendar.model.PsnCalendarAppModelDataManager)context.get("modelDataManager");
  nc.ui.ta.psncalendar.model.PsnCalendarAppModelDataManager bean = new nc.ui.ta.psncalendar.model.PsnCalendarAppModelDataManager();
  context.put("modelDataManager",bean);
  bean.setContext(getContext());
  bean.setModel(getModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.psncalendar.view.GridPanel getGridView(){
 if(context.get("gridView")!=null)
 return (nc.ui.ta.psncalendar.view.GridPanel)context.get("gridView");
  nc.ui.ta.psncalendar.view.GridPanel bean = new nc.ui.ta.psncalendar.view.GridPanel();
  context.put("gridView",bean);
  bean.setModel(getModel());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.psncalendar.view.CalendarPanel getCalendarView(){
 if(context.get("calendarView")!=null)
 return (nc.ui.ta.psncalendar.view.CalendarPanel)context.get("calendarView");
  nc.ui.ta.psncalendar.view.CalendarPanel bean = new nc.ui.ta.psncalendar.view.CalendarPanel();
  context.put("calendarView",bean);
  bean.setModel(getModel());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.SeparatorAction getNullaction(){
 if(context.get("nullaction")!=null)
 return (nc.funcnode.ui.action.SeparatorAction)context.get("nullaction");
  nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
  context.put("nullaction",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.MenuAction getArrangeCalendarActionGroup(){
 if(context.get("arrangeCalendarActionGroup")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("arrangeCalendarActionGroup");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("arrangeCalendarActionGroup",bean);
  bean.setCode("arrangeClass");
  bean.setName(getI18nFB_1edce720());
  bean.setActions(getManagedList5());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_1edce720(){
 if(context.get("nc.ui.uif2.I18nFB#1edce720")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#1edce720");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#1edce720",bean);  bean.setResDir("6017psncalendar");
  bean.setDefaultValue("排班");
  bean.setResId("X6017psncal01");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#1edce720",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList5(){  List list = new ArrayList();  list.add(getCircularlyArrangeAction());  list.add(getUseDefaultAction());  return list;}

public nc.ui.ta.psncalendar.action.CircularlyArrangeAction getCircularlyArrangeAction(){
 if(context.get("CircularlyArrangeAction")!=null)
 return (nc.ui.ta.psncalendar.action.CircularlyArrangeAction)context.get("CircularlyArrangeAction");
  nc.ui.ta.psncalendar.action.CircularlyArrangeAction bean = new nc.ui.ta.psncalendar.action.CircularlyArrangeAction();
  context.put("CircularlyArrangeAction",bean);
  bean.setModel(getModel());
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.psncalendar.action.UseDefaultAction getUseDefaultAction(){
 if(context.get("UseDefaultAction")!=null)
 return (nc.ui.ta.psncalendar.action.UseDefaultAction)context.get("UseDefaultAction");
  nc.ui.ta.psncalendar.action.UseDefaultAction bean = new nc.ui.ta.psncalendar.action.UseDefaultAction();
  context.put("UseDefaultAction",bean);
  bean.setModel(getModel());
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getEditActionGroup(){
 if(context.get("editActionGroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("editActionGroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("editActionGroup",bean);
  bean.setCode("edit");
  bean.setName(getI18nFB_38c9f5b4());
  bean.setActions(getManagedList6());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_38c9f5b4(){
 if(context.get("nc.ui.uif2.I18nFB#38c9f5b4")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#38c9f5b4");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#38c9f5b4",bean);  bean.setResDir("common");
  bean.setDefaultValue("修改");
  bean.setResId("UC001-0000045");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#38c9f5b4",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList6(){  List list = new ArrayList();  list.add(getEditAction());  list.add(getBatchChangeAction());  list.add(getBatchChangeCalendarDayTypeAction());  return list;}

public nc.ui.ta.psncalendar.action.EditCalendarAction getEditAction(){
 if(context.get("EditAction")!=null)
 return (nc.ui.ta.psncalendar.action.EditCalendarAction)context.get("EditAction");
  nc.ui.ta.psncalendar.action.EditCalendarAction bean = new nc.ui.ta.psncalendar.action.EditCalendarAction();
  context.put("EditAction",bean);
  bean.setModel(getModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.psncalendar.action.BatchChangeCalendarAction getBatchChangeAction(){
 if(context.get("BatchChangeAction")!=null)
 return (nc.ui.ta.psncalendar.action.BatchChangeCalendarAction)context.get("BatchChangeAction");
  nc.ui.ta.psncalendar.action.BatchChangeCalendarAction bean = new nc.ui.ta.psncalendar.action.BatchChangeCalendarAction();
  context.put("BatchChangeAction",bean);
  bean.setModel(getModel());
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.psncalendar.action.BatchChangeCalendarDayTypeAction getBatchChangeCalendarDayTypeAction(){
 if(context.get("BatchChangeCalendarDayTypeAction")!=null)
 return (nc.ui.ta.psncalendar.action.BatchChangeCalendarDayTypeAction)context.get("BatchChangeCalendarDayTypeAction");
  nc.ui.ta.psncalendar.action.BatchChangeCalendarDayTypeAction bean = new nc.ui.ta.psncalendar.action.BatchChangeCalendarDayTypeAction();
  context.put("BatchChangeCalendarDayTypeAction",bean);
  bean.setModel(getModel());
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.psncalendar.action.QueryCalendarAction getQueryAction(){
 if(context.get("QueryAction")!=null)
 return (nc.ui.ta.psncalendar.action.QueryCalendarAction)context.get("QueryAction");
  nc.ui.ta.psncalendar.action.QueryCalendarAction bean = new nc.ui.ta.psncalendar.action.QueryCalendarAction();
  context.put("QueryAction",bean);
  bean.setModel(getModel());
  bean.setDataManager(getModelDataManager());
  bean.setPsnCalendarAppModelDataManager(getModelDataManager());
  bean.setQueryDelegator(getPsnCalendarQueryDelegator_5322198c());
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.ta.psncalendar.action.PsnCalendarQueryDelegator getPsnCalendarQueryDelegator_5322198c(){
 if(context.get("nc.ui.ta.psncalendar.action.PsnCalendarQueryDelegator#5322198c")!=null)
 return (nc.ui.ta.psncalendar.action.PsnCalendarQueryDelegator)context.get("nc.ui.ta.psncalendar.action.PsnCalendarQueryDelegator#5322198c");
  nc.ui.ta.psncalendar.action.PsnCalendarQueryDelegator bean = new nc.ui.ta.psncalendar.action.PsnCalendarQueryDelegator();
  context.put("nc.ui.ta.psncalendar.action.PsnCalendarQueryDelegator#5322198c",bean);
  bean.setNodeKey("psnquery");
  bean.setContext(getContext());
  bean.setModel(getModel());
  bean.setQueryEditorListener(getQueryEditorListener());
  bean.getQueryDlg();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.psncalendar.action.RefreshCalendarAction getRefreshAction(){
 if(context.get("RefreshAction")!=null)
 return (nc.ui.ta.psncalendar.action.RefreshCalendarAction)context.get("RefreshAction");
  nc.ui.ta.psncalendar.action.RefreshCalendarAction bean = new nc.ui.ta.psncalendar.action.RefreshCalendarAction();
  context.put("RefreshAction",bean);
  bean.setModel(getModel());
  bean.setDataManager(getModelDataManager());
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.psncalendar.action.SaveCalendarAction getSaveAction(){
 if(context.get("SaveAction")!=null)
 return (nc.ui.ta.psncalendar.action.SaveCalendarAction)context.get("SaveAction");
  nc.ui.ta.psncalendar.action.SaveCalendarAction bean = new nc.ui.ta.psncalendar.action.SaveCalendarAction();
  context.put("SaveAction",bean);
  bean.setModel(getModel());
  bean.setEditor(getEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.pub.action.CancelAction getCancelAction(){
 if(context.get("CancelAction")!=null)
 return (nc.ui.ta.pub.action.CancelAction)context.get("CancelAction");
  nc.ui.ta.pub.action.CancelAction bean = new nc.ui.ta.pub.action.CancelAction();
  context.put("CancelAction",bean);
  bean.setModel(getModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.psncalendar.action.ViewDetailAction getViewDetailAction(){
 if(context.get("ViewDetailAction")!=null)
 return (nc.ui.ta.psncalendar.action.ViewDetailAction)context.get("ViewDetailAction");
  nc.ui.ta.psncalendar.action.ViewDetailAction bean = new nc.ui.ta.psncalendar.action.ViewDetailAction();
  context.put("ViewDetailAction",bean);
  bean.setModel(getModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.MenuAction getExOrImpActionGroup(){
 if(context.get("exOrImpActionGroup")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("exOrImpActionGroup");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("exOrImpActionGroup",bean);
  bean.setCode("exOrImp");
  bean.setName(getI18nFB_63bb60f7());
  bean.setActions(getManagedList7());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_63bb60f7(){
 if(context.get("nc.ui.uif2.I18nFB#63bb60f7")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#63bb60f7");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#63bb60f7",bean);  bean.setResDir("6017basedoc");
  bean.setDefaultValue("导入导出");
  bean.setResId("06017basedoc1818");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#63bb60f7",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList7(){  List list = new ArrayList();  list.add(getImportAction());  list.add(getExportAction());  return list;}

public nc.ui.ta.psncalendar.action.ImportAction getImportAction(){
 if(context.get("ImportAction")!=null)
 return (nc.ui.ta.psncalendar.action.ImportAction)context.get("ImportAction");
  nc.ui.ta.psncalendar.action.ImportAction bean = new nc.ui.ta.psncalendar.action.ImportAction();
  context.put("ImportAction",bean);
  bean.setModel(getModel());
  bean.setDataManager(getModelDataManager());
  bean.setNcActionStatusJudge(getEnableJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.psncalendar.action.ExportAction getExportAction(){
 if(context.get("ExportAction")!=null)
 return (nc.ui.ta.psncalendar.action.ExportAction)context.get("ExportAction");
  nc.ui.ta.psncalendar.action.ExportAction bean = new nc.ui.ta.psncalendar.action.ExportAction();
  context.put("ExportAction",bean);
  bean.setModel(getModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getPrintActiongroup(){
 if(context.get("PrintActiongroup")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("PrintActiongroup");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("PrintActiongroup",bean);
  bean.setActions(getManagedList8());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList8(){  List list = new ArrayList();  list.add(getPrintDirectAction());  list.add(getPrintPreviewAction());  list.add(getExportListAction());  return list;}

public nc.ui.ta.psncalendar.action.PrintPsnCalendarPreviewAction getPrintPreviewAction(){
 if(context.get("printPreviewAction")!=null)
 return (nc.ui.ta.psncalendar.action.PrintPsnCalendarPreviewAction)context.get("printPreviewAction");
  nc.ui.ta.psncalendar.action.PrintPsnCalendarPreviewAction bean = new nc.ui.ta.psncalendar.action.PrintPsnCalendarPreviewAction();
  context.put("printPreviewAction",bean);
  bean.setModel(getModel());
  bean.setPanel(getGridView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.psncalendar.action.PrintPsnCalendarAction getPrintDirectAction(){
 if(context.get("printDirectAction")!=null)
 return (nc.ui.ta.psncalendar.action.PrintPsnCalendarAction)context.get("printDirectAction");
  nc.ui.ta.psncalendar.action.PrintPsnCalendarAction bean = new nc.ui.ta.psncalendar.action.PrintPsnCalendarAction();
  context.put("printDirectAction",bean);
  bean.setModel(getModel());
  bean.setPanel(getGridView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.psncalendar.action.OutputPsnCalendarAction getExportListAction(){
 if(context.get("exportListAction")!=null)
 return (nc.ui.ta.psncalendar.action.OutputPsnCalendarAction)context.get("exportListAction");
  nc.ui.ta.psncalendar.action.OutputPsnCalendarAction bean = new nc.ui.ta.psncalendar.action.OutputPsnCalendarAction();
  context.put("exportListAction",bean);
  bean.setModel(getModel());
  bean.setPanel(getGridView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.FunNodeClosingHandler getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.uif2.FunNodeClosingHandler)context.get("ClosingListener");
  nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
  context.put("ClosingListener",bean);
  bean.setModel(getModel());
  bean.setSaveaction(getSaveAction());
  bean.setCancelaction(getCancelAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.psncalendar.view.PsnCalendarShiftDetailHandler getShowDetailpreprocessor(){
 if(context.get("ShowDetailpreprocessor")!=null)
 return (nc.ui.ta.psncalendar.view.PsnCalendarShiftDetailHandler)context.get("ShowDetailpreprocessor");
  nc.ui.ta.psncalendar.view.PsnCalendarShiftDetailHandler bean = new nc.ui.ta.psncalendar.view.PsnCalendarShiftDetailHandler();
  context.put("ShowDetailpreprocessor",bean);
  bean.setModel(getModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setTangramLayoutRoot(getVSNode_4966ef91());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_4966ef91(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#4966ef91")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#4966ef91");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#4966ef91",bean);
  bean.setUp(getCNode_22db2b48());
  bean.setDown(getTBNode_5a966b6d());
  bean.setDividerLocation(30f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_22db2b48(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#22db2b48")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#22db2b48");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#22db2b48",bean);
  bean.setComponent(getOrgpanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_5a966b6d(){
 if(context.get("nc.ui.uif2.tangramlayout.node.TBNode#5a966b6d")!=null)
 return (nc.ui.uif2.tangramlayout.node.TBNode)context.get("nc.ui.uif2.tangramlayout.node.TBNode#5a966b6d");
  nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
  context.put("nc.ui.uif2.tangramlayout.node.TBNode#5a966b6d",bean);
  bean.setTabs(getManagedList9());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList9(){  List list = new ArrayList();  list.add(getCNode_2ae7caaa());  list.add(getCNode_77e116a7());  return list;}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_2ae7caaa(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#2ae7caaa")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#2ae7caaa");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#2ae7caaa",bean);
  bean.setName(getI18nFB_62f695d8());
  bean.setComponent(getGridView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_62f695d8(){
 if(context.get("nc.ui.uif2.I18nFB#62f695d8")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#62f695d8");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#62f695d8",bean);  bean.setResDir("6017psncalendar");
  bean.setDefaultValue("时间段");
  bean.setResId("X6017psncal03");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#62f695d8",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_77e116a7(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#77e116a7")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#77e116a7");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#77e116a7",bean);
  bean.setName(getI18nFB_57aa74e7());
  bean.setComponent(getCalendarView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_57aa74e7(){
 if(context.get("nc.ui.uif2.I18nFB#57aa74e7")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#57aa74e7");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#57aa74e7",bean);  bean.setResDir("6017basedoc");
  bean.setDefaultValue("日历");
  bean.setResId("06017basedoc1768");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#57aa74e7",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.ta.psncalendar.view.Editor getEditor(){
 if(context.get("editor")!=null)
 return (nc.ui.ta.psncalendar.view.Editor)context.get("editor");
  nc.ui.ta.psncalendar.view.Editor bean = new nc.ui.ta.psncalendar.view.Editor();
  context.put("editor",bean);
  bean.setGridPanel(getGridView());
  bean.setCalendarPanel(getCalendarView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.pub.view.TAParamOrgPanel getOrgpanel(){
 if(context.get("orgpanel")!=null)
 return (nc.ui.ta.pub.view.TAParamOrgPanel)context.get("orgpanel");
  nc.ui.ta.pub.view.TAParamOrgPanel bean = new nc.ui.ta.pub.view.TAParamOrgPanel();
  context.put("orgpanel",bean);
  bean.setModel(getModel());
  bean.setDataManager(getModelDataManager());
  bean.setPk_orgtype("HRORGTYPE00000000000");
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.ta.pub.action.EnableJudge getEnableJudge(){
 if(context.get("enableJudge")!=null)
 return (nc.ui.ta.pub.action.EnableJudge)context.get("enableJudge");
  nc.ui.ta.pub.action.EnableJudge bean = new nc.ui.ta.pub.action.EnableJudge();
  context.put("enableJudge",bean);
  bean.setModel(getModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
