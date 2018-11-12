package nc.ui.om.hrdept;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class hrdeptbatch_config extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.vo.uif2.LoginContext getContext(){
 if(context.get("context")!=null)
 return (nc.vo.uif2.LoginContext)context.get("context");
  nc.vo.uif2.LoginContext bean = new nc.vo.uif2.LoginContext();
  context.put("context",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.model.DeptModelService getModelService(){
 if(context.get("modelService")!=null)
 return (nc.ui.om.hrdept.model.DeptModelService)context.get("modelService");
  nc.ui.om.hrdept.model.DeptModelService bean = new nc.ui.om.hrdept.model.DeptModelService();
  context.put("modelService",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.model.OrgTreeModelService getTreeModelService(){
 if(context.get("treeModelService")!=null)
 return (nc.ui.om.hrdept.model.OrgTreeModelService)context.get("treeModelService");
  nc.ui.om.hrdept.model.OrgTreeModelService bean = new nc.ui.om.hrdept.model.OrgTreeModelService();
  context.put("treeModelService",bean);
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

public nc.ui.om.psnnavi.view.AOSNaviTreeCreateStrategy getTreeCreateStrategy(){
 if(context.get("treeCreateStrategy")!=null)
 return (nc.ui.om.psnnavi.view.AOSNaviTreeCreateStrategy)context.get("treeCreateStrategy");
  nc.ui.om.psnnavi.view.AOSNaviTreeCreateStrategy bean = new nc.ui.om.psnnavi.view.AOSNaviTreeCreateStrategy();
  context.put("treeCreateStrategy",bean);
  bean.setFactory(getBoadatorfactory());
  bean.setRootName(getI18nFB_4cc12db2());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_4cc12db2(){
 if(context.get("nc.ui.uif2.I18nFB#4cc12db2")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#4cc12db2");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#4cc12db2",bean);  bean.setResDir("menucode");
  bean.setDefaultValue("行政组织");
  bean.setResId("X600531");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#4cc12db2",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

public nc.ui.om.hrdept.model.DeptAppModel getManageAppModel(){
 if(context.get("manageAppModel")!=null)
 return (nc.ui.om.hrdept.model.DeptAppModel)context.get("manageAppModel");
  nc.ui.om.hrdept.model.DeptAppModel bean = new nc.ui.om.hrdept.model.DeptAppModel();
  context.put("manageAppModel",bean);
  bean.setService(getModelService());
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.model.TreeDeptAppModel getHAppModel(){
 if(context.get("HAppModel")!=null)
 return (nc.ui.om.hrdept.model.TreeDeptAppModel)context.get("HAppModel");
  nc.ui.om.hrdept.model.TreeDeptAppModel bean = new nc.ui.om.hrdept.model.TreeDeptAppModel();
  context.put("HAppModel",bean);
  bean.setService(getModelService());
  bean.setTreeCreateStrategy(getTreeCreateStrategy());
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.model.DeptBatchDataManager getMgrModelDataManager(){
 if(context.get("mgrModelDataManager")!=null)
 return (nc.ui.om.hrdept.model.DeptBatchDataManager)context.get("mgrModelDataManager");
  nc.ui.om.hrdept.model.DeptBatchDataManager bean = new nc.ui.om.hrdept.model.DeptBatchDataManager();
  context.put("mgrModelDataManager",bean);
  bean.setModel(getManageAppModel());
  bean.setService(getModelService());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.model.OrgTreeDataManager getModelDataManager(){
 if(context.get("modelDataManager")!=null)
 return (nc.ui.om.hrdept.model.OrgTreeDataManager)context.get("modelDataManager");
  nc.ui.om.hrdept.model.OrgTreeDataManager bean = new nc.ui.om.hrdept.model.OrgTreeDataManager();
  context.put("modelDataManager",bean);
  bean.setModel(getHAppModel());
  bean.setService(getTreeModelService());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.editor.TemplateContainer getTemplateContainer(){
 if(context.get("templateContainer")!=null)
 return (nc.ui.uif2.editor.TemplateContainer)context.get("templateContainer");
  nc.ui.uif2.editor.TemplateContainer bean = new nc.ui.uif2.editor.TemplateContainer();
  context.put("templateContainer",bean);
  bean.setContext(getContext());
  bean.setNodeKeies(getManagedList0());
  bean.load();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList0(){  List list = new ArrayList();  list.add("hrdept");  return list;}

public nc.ui.om.hrdept.view.DeptTreePanel getTreePanel(){
 if(context.get("treePanel")!=null)
 return (nc.ui.om.hrdept.view.DeptTreePanel)context.get("treePanel");
  nc.ui.om.hrdept.view.DeptTreePanel bean = new nc.ui.om.hrdept.view.DeptTreePanel();
  context.put("treePanel",bean);
  bean.setModel(getHAppModel());
  bean.setSearchMode("filter");
  bean.setFilterByText(getBdobjectFilter());
  bean.setRootvisibleflag(false);
  bean.setTreeCellRenderer(getTreeCellRenderer());
  bean.init();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.model.OrgtreeDeptMediator getTypeAndDocMediator(){
 if(context.get("typeAndDocMediator")!=null)
 return (nc.ui.om.hrdept.model.OrgtreeDeptMediator)context.get("typeAndDocMediator");
  nc.ui.om.hrdept.model.OrgtreeDeptMediator bean = new nc.ui.om.hrdept.model.OrgtreeDeptMediator();
  context.put("typeAndDocMediator",bean);
  bean.setTypeAppModel(getHAppModel());
  bean.setDocModel(getManageAppModel());
  bean.setDocModelDataManager(getMgrModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pub.beans.tree.BDObjectFilterByText getBdobjectFilter(){
 if(context.get("bdobjectFilter")!=null)
 return (nc.ui.pub.beans.tree.BDObjectFilterByText)context.get("bdobjectFilter");
  nc.ui.pub.beans.tree.BDObjectFilterByText bean = new nc.ui.pub.beans.tree.BDObjectFilterByText();
  context.put("bdobjectFilter",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.view.DeptTreeCellRenderer getTreeCellRenderer(){
 if(context.get("treeCellRenderer")!=null)
 return (nc.ui.om.hrdept.view.DeptTreeCellRenderer)context.get("treeCellRenderer");
  nc.ui.om.hrdept.view.DeptTreeCellRenderer bean = new nc.ui.om.hrdept.view.DeptTreeCellRenderer();
  context.put("treeCellRenderer",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.view.DeptCardForm getBillFormEditor(){
 if(context.get("billFormEditor")!=null)
 return (nc.ui.om.hrdept.view.DeptCardForm)context.get("billFormEditor");
  nc.ui.om.hrdept.view.DeptCardForm bean = new nc.ui.om.hrdept.view.DeptCardForm();
  context.put("billFormEditor",bean);
  bean.setModel(getManageAppModel());
  bean.setTreeAppModel(getHAppModel());
  bean.setTemplateContainer(getTemplateContainer());
  bean.setNodekey("hrdept");
  bean.setTabActions(getManagedList1());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList1(){  List list = new ArrayList();  list.add(getAddLineAction());  list.add(getInsertLineAction());  list.add(getDelLineAction());  list.add(getCopyLineAction());  list.add(getPasteLineAction());  return list;}

public java.lang.String getResourceCode(){
 if(context.get("resourceCode")!=null)
 return (java.lang.String)context.get("resourceCode");
  java.lang.String bean = new java.lang.String("60050deptinfo");  context.put("resourceCode",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.print.DirectPrintAction getPrintDirectAction(){
 if(context.get("printDirectAction")!=null)
 return (nc.ui.hr.uif2.action.print.DirectPrintAction)context.get("printDirectAction");
  nc.ui.hr.uif2.action.print.DirectPrintAction bean = new nc.ui.hr.uif2.action.print.DirectPrintAction();
  context.put("printDirectAction",bean);
  bean.setModel(getManageAppModel());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.action.print.DirectPreviewAction getPrintPreviewAction(){
 if(context.get("printPreviewAction")!=null)
 return (nc.ui.hr.uif2.action.print.DirectPreviewAction)context.get("printPreviewAction");
  nc.ui.hr.uif2.action.print.DirectPreviewAction bean = new nc.ui.hr.uif2.action.print.DirectPreviewAction();
  context.put("printPreviewAction",bean);
  bean.setModel(getManageAppModel());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.orginfo.action.ExportListOrgdocAction getListOutputAction(){
 if(context.get("listOutputAction")!=null)
 return (nc.ui.om.orginfo.action.ExportListOrgdocAction)context.get("listOutputAction");
  nc.ui.om.orginfo.action.ExportListOrgdocAction bean = new nc.ui.om.orginfo.action.ExportListOrgdocAction();
  context.put("listOutputAction",bean);
  bean.setModel(getManageAppModel());
  bean.setListView(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.SeparatorAction getSeparatorAction(){
 if(context.get("separatorAction")!=null)
 return (nc.funcnode.ui.action.SeparatorAction)context.get("separatorAction");
  nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
  context.put("separatorAction",bean);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.AddDeptAction getAddAction(){
 if(context.get("AddAction")!=null)
 return (nc.ui.om.hrdept.action.AddDeptAction)context.get("AddAction");
  nc.ui.om.hrdept.action.AddDeptAction bean = new nc.ui.om.hrdept.action.AddDeptAction();
  context.put("AddAction",bean);
  bean.setShowTipsOnTree(true);
  bean.setModel(getManageAppModel());
  bean.setTreeModel(getHAppModel());
  bean.setResourceCode(getResourceCode());
  bean.setFormEditor(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.EditDeptAction getEditAction(){
 if(context.get("EditAction")!=null)
 return (nc.ui.om.hrdept.action.EditDeptAction)context.get("EditAction");
  nc.ui.om.hrdept.action.EditDeptAction bean = new nc.ui.om.hrdept.action.EditDeptAction();
  context.put("EditAction",bean);
  bean.setShowTipsOnTree(true);
  bean.setModel(getManageAppModel());
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.BatchEditDeptAction getBatchEditAction(){
 if(context.get("BatchEditAction")!=null)
 return (nc.ui.om.hrdept.action.BatchEditDeptAction)context.get("BatchEditAction");
  nc.ui.om.hrdept.action.BatchEditDeptAction bean = new nc.ui.om.hrdept.action.BatchEditDeptAction();
  context.put("BatchEditAction",bean);
  bean.setModel(getManageAppModel());
  bean.setFormEditor(getBillFormEditor());
  bean.setListView(getListView());
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.DeleteDeptAction getDeleteAction(){
 if(context.get("DeleteAction")!=null)
 return (nc.ui.om.hrdept.action.DeleteDeptAction)context.get("DeleteAction");
  nc.ui.om.hrdept.action.DeleteDeptAction bean = new nc.ui.om.hrdept.action.DeleteDeptAction();
  context.put("DeleteAction",bean);
  bean.setShowTipsOnTree(true);
  bean.setModel(getManageAppModel());
  bean.setValidationService(getDeleteValidator());
  bean.setResourceCode(getResourceCode());
  bean.setDataManager(getMgrModelDataManager());
  bean.setTreeDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.CopyDeptAction getCopyDeptAction(){
 if(context.get("CopyDeptAction")!=null)
 return (nc.ui.om.hrdept.action.CopyDeptAction)context.get("CopyDeptAction");
  nc.ui.om.hrdept.action.CopyDeptAction bean = new nc.ui.om.hrdept.action.CopyDeptAction();
  context.put("CopyDeptAction",bean);
  bean.setModel(getManageAppModel());
  bean.setTreeModel(getHAppModel());
  bean.setTreeDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.RefreshDeptAction getRefreshAction(){
 if(context.get("RefreshAction")!=null)
 return (nc.ui.om.hrdept.action.RefreshDeptAction)context.get("RefreshAction");
  nc.ui.om.hrdept.action.RefreshDeptAction bean = new nc.ui.om.hrdept.action.RefreshDeptAction();
  context.put("RefreshAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.RenameDeptAction getRenameDeptAction(){
 if(context.get("RenameDeptAction")!=null)
 return (nc.ui.om.hrdept.action.RenameDeptAction)context.get("RenameDeptAction");
  nc.ui.om.hrdept.action.RenameDeptAction bean = new nc.ui.om.hrdept.action.RenameDeptAction();
  context.put("RenameDeptAction",bean);
  bean.setModel(getManageAppModel());
  bean.setValidationService(getModificationValidator());
  bean.setResourceCode(getResourceCode());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.BatchRenameDeptAction getBatchRenameDeptAction(){
 if(context.get("BatchRenameDeptAction")!=null)
 return (nc.ui.om.hrdept.action.BatchRenameDeptAction)context.get("BatchRenameDeptAction");
  nc.ui.om.hrdept.action.BatchRenameDeptAction bean = new nc.ui.om.hrdept.action.BatchRenameDeptAction();
  context.put("BatchRenameDeptAction",bean);
  bean.setModel(getManageAppModel());
  bean.setValidationService(getBatchRenameValidator());
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.validator.DeptBatchRenameValidator getBatchRenameValidator(){
 if(context.get("batchRenameValidator")!=null)
 return (nc.ui.om.hrdept.validator.DeptBatchRenameValidator)context.get("batchRenameValidator");
  nc.ui.om.hrdept.validator.DeptBatchRenameValidator bean = new nc.ui.om.hrdept.validator.DeptBatchRenameValidator();
  context.put("batchRenameValidator",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.MergeDeptAction getMergeDeptAction(){
 if(context.get("MergeDeptAction")!=null)
 return (nc.ui.om.hrdept.action.MergeDeptAction)context.get("MergeDeptAction");
  nc.ui.om.hrdept.action.MergeDeptAction bean = new nc.ui.om.hrdept.action.MergeDeptAction();
  context.put("MergeDeptAction",bean);
  bean.setModel(getManageAppModel());
  bean.setValidationService(getModificationValidator());
  bean.setDataManager(getModelDataManager());
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.ShiftDeptAction getShiftDeptAction(){
 if(context.get("ShiftDeptAction")!=null)
 return (nc.ui.om.hrdept.action.ShiftDeptAction)context.get("ShiftDeptAction");
  nc.ui.om.hrdept.action.ShiftDeptAction bean = new nc.ui.om.hrdept.action.ShiftDeptAction();
  context.put("ShiftDeptAction",bean);
  bean.setModel(getManageAppModel());
  bean.setTreeModel(getHAppModel());
  bean.setValidationService(getModificationValidator());
  bean.setResourceCode(getResourceCode());
  bean.setDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.OuterShiftDeptAction getOuterShiftDeptAction(){
 if(context.get("OuterShiftDeptAction")!=null)
 return (nc.ui.om.hrdept.action.OuterShiftDeptAction)context.get("OuterShiftDeptAction");
  nc.ui.om.hrdept.action.OuterShiftDeptAction bean = new nc.ui.om.hrdept.action.OuterShiftDeptAction();
  context.put("OuterShiftDeptAction",bean);
  bean.setModel(getManageAppModel());
  bean.setTreeDataManager(getModelDataManager());
  bean.setValidationService(getModificationValidator());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.SaveDeptAction getSaveAction(){
 if(context.get("SaveAction")!=null)
 return (nc.ui.om.hrdept.action.SaveDeptAction)context.get("SaveAction");
  nc.ui.om.hrdept.action.SaveDeptAction bean = new nc.ui.om.hrdept.action.SaveDeptAction();
  context.put("SaveAction",bean);
  bean.setModel(getManageAppModel());
  bean.setEditor(getBillFormEditor());
  bean.setValidationService(getBillNotNullValidator());
  bean.setTreeDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.SaveAddDeptAction getSaveAddAction(){
 if(context.get("SaveAddAction")!=null)
 return (nc.ui.om.hrdept.action.SaveAddDeptAction)context.get("SaveAddAction");
  nc.ui.om.hrdept.action.SaveAddDeptAction bean = new nc.ui.om.hrdept.action.SaveAddDeptAction();
  context.put("SaveAddAction",bean);
  bean.setModel(getManageAppModel());
  bean.setValidationService(getBillNotNullValidator());
  bean.setAddAction(getAddAction());
  bean.setSaveAction(getSaveAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.CancelDeptAction getCancelAction(){
 if(context.get("CancelAction")!=null)
 return (nc.ui.om.hrdept.action.CancelDeptAction)context.get("CancelAction");
  nc.ui.om.hrdept.action.CancelDeptAction bean = new nc.ui.om.hrdept.action.CancelDeptAction();
  context.put("CancelAction",bean);
  bean.setModel(getManageAppModel());
  bean.setBillForm(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.SubTableEnableJudge getActionanleJudge(){
 if(context.get("ActionanleJudge")!=null)
 return (nc.ui.om.hrdept.action.SubTableEnableJudge)context.get("ActionanleJudge");
  nc.ui.om.hrdept.action.SubTableEnableJudge bean = new nc.ui.om.hrdept.action.SubTableEnableJudge();
  context.put("ActionanleJudge",bean);
  bean.setModel(getManageAppModel());
  bean.setCardPanel(getBillFormEditor());
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

public nc.ui.om.hrdept.action.DeptAddLineAction getAddLineAction(){
 if(context.get("AddLineAction")!=null)
 return (nc.ui.om.hrdept.action.DeptAddLineAction)context.get("AddLineAction");
  nc.ui.om.hrdept.action.DeptAddLineAction bean = new nc.ui.om.hrdept.action.DeptAddLineAction();
  context.put("AddLineAction",bean);
  bean.setModel(getManageAppModel());
  bean.setCardPanel(getBillFormEditor());
  bean.setJudge(getActionanleJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.DeptInsertLineAction getInsertLineAction(){
 if(context.get("InsertLineAction")!=null)
 return (nc.ui.om.hrdept.action.DeptInsertLineAction)context.get("InsertLineAction");
  nc.ui.om.hrdept.action.DeptInsertLineAction bean = new nc.ui.om.hrdept.action.DeptInsertLineAction();
  context.put("InsertLineAction",bean);
  bean.setModel(getManageAppModel());
  bean.setCardPanel(getBillFormEditor());
  bean.setJudge(getActionanleJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.DeptCopyLineAction getCopyLineAction(){
 if(context.get("CopyLineAction")!=null)
 return (nc.ui.om.hrdept.action.DeptCopyLineAction)context.get("CopyLineAction");
  nc.ui.om.hrdept.action.DeptCopyLineAction bean = new nc.ui.om.hrdept.action.DeptCopyLineAction();
  context.put("CopyLineAction",bean);
  bean.setModel(getManageAppModel());
  bean.setCardPanel(getBillFormEditor());
  bean.setJudge(getActionanleJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.DeptDelLineAction getDelLineAction(){
 if(context.get("DelLineAction")!=null)
 return (nc.ui.om.hrdept.action.DeptDelLineAction)context.get("DelLineAction");
  nc.ui.om.hrdept.action.DeptDelLineAction bean = new nc.ui.om.hrdept.action.DeptDelLineAction();
  context.put("DelLineAction",bean);
  bean.setModel(getManageAppModel());
  bean.setCardPanel(getBillFormEditor());
  bean.setJudge(getActionanleJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.DeptPasteLineAction getPasteLineAction(){
 if(context.get("PasteLineAction")!=null)
 return (nc.ui.om.hrdept.action.DeptPasteLineAction)context.get("PasteLineAction");
  nc.ui.om.hrdept.action.DeptPasteLineAction bean = new nc.ui.om.hrdept.action.DeptPasteLineAction();
  context.put("PasteLineAction",bean);
  bean.setModel(getManageAppModel());
  bean.setCardPanel(getBillFormEditor());
  bean.setJudge(getActionanleJudge());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.MenuAction getVersionAction(){
 if(context.get("VersionAction")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("VersionAction");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("VersionAction",bean);
  bean.setCode("version");
  bean.setName(getI18nFB_6c4ce583());
  bean.setActions(getManagedList2());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_6c4ce583(){
 if(context.get("nc.ui.uif2.I18nFB#6c4ce583")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#6c4ce583");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#6c4ce583",bean);  bean.setResDir("menucode");
  bean.setDefaultValue("部门版本化");
  bean.setResId("X600506");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#6c4ce583",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList2(){  List list = new ArrayList();  list.add(getCreateDeptStruVersionAction());  list.add(getCreateDeptVersionAction());  return list;}

public nc.ui.om.hrdept.action.CreateDeptStruVersionAction getCreateDeptStruVersionAction(){
 if(context.get("CreateDeptStruVersionAction")!=null)
 return (nc.ui.om.hrdept.action.CreateDeptStruVersionAction)context.get("CreateDeptStruVersionAction");
  nc.ui.om.hrdept.action.CreateDeptStruVersionAction bean = new nc.ui.om.hrdept.action.CreateDeptStruVersionAction();
  context.put("CreateDeptStruVersionAction",bean);
  bean.setModel(getManageAppModel());
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.CreateDeptVersionAction getCreateDeptVersionAction(){
 if(context.get("CreateDeptVersionAction")!=null)
 return (nc.ui.om.hrdept.action.CreateDeptVersionAction)context.get("CreateDeptVersionAction");
  nc.ui.om.hrdept.action.CreateDeptVersionAction bean = new nc.ui.om.hrdept.action.CreateDeptVersionAction();
  context.put("CreateDeptVersionAction",bean);
  bean.setModel(getManageAppModel());
  bean.setResourceCode(getResourceCode());
  bean.setNodekey("deptversion");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.MenuAction getFilterAction(){
 if(context.get("FilterAction")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("FilterAction");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("FilterAction",bean);
  bean.setCode(getI18nFB_755b5f30());
  bean.setName(getI18nFB_29bbc63c());
  bean.setActions(getManagedList3());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_755b5f30(){
 if(context.get("nc.ui.uif2.I18nFB#755b5f30")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#755b5f30");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#755b5f30",bean);  bean.setResDir("menucode");
  bean.setDefaultValue("过滤");
  bean.setResId("X600508");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#755b5f30",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private java.lang.String getI18nFB_29bbc63c(){
 if(context.get("nc.ui.uif2.I18nFB#29bbc63c")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#29bbc63c");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#29bbc63c",bean);  bean.setResDir("menucode");
  bean.setDefaultValue("过滤");
  bean.setResId("X600509");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#29bbc63c",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList3(){  List list = new ArrayList();  list.add(getShowHRCanceledDataAction());  list.add(getShowHRFutureDataAction());  return list;}

public nc.ui.om.hrdept.action.ShowCanceledDeptDataAction getShowHRCanceledDataAction(){
 if(context.get("ShowHRCanceledDataAction")!=null)
 return (nc.ui.om.hrdept.action.ShowCanceledDeptDataAction)context.get("ShowHRCanceledDataAction");
  nc.ui.om.hrdept.action.ShowCanceledDeptDataAction bean = new nc.ui.om.hrdept.action.ShowCanceledDeptDataAction();
  context.put("ShowHRCanceledDataAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getMgrModelDataManager());
  bean.setTreeDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.ShowFutureDeptDataAction getShowHRFutureDataAction(){
 if(context.get("ShowHRFutureDataAction")!=null)
 return (nc.ui.om.hrdept.action.ShowFutureDeptDataAction)context.get("ShowHRFutureDataAction");
  nc.ui.om.hrdept.action.ShowFutureDeptDataAction bean = new nc.ui.om.hrdept.action.ShowFutureDeptDataAction();
  context.put("ShowHRFutureDataAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getMgrModelDataManager());
  bean.setTreeDataManager(getModelDataManager());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.GroupAction getHRCancelGroupAction(){
 if(context.get("HRCancelGroupAction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("HRCancelGroupAction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("HRCancelGroupAction",bean);
  bean.setCode("seal");
  bean.setName(getI18nFB_272778ae());
  bean.setActions(getManagedList4());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_272778ae(){
 if(context.get("nc.ui.uif2.I18nFB#272778ae")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#272778ae");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#272778ae",bean);  bean.setResDir("menucode");
  bean.setDefaultValue("撤销");
  bean.setResId("X600510");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#272778ae",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList4(){  List list = new ArrayList();  list.add(getHRCancelAction());  list.add(getHRUnCancelAction());  return list;}

public nc.ui.om.hrdept.action.HRCancelAction getHRCancelAction(){
 if(context.get("HRCancelAction")!=null)
 return (nc.ui.om.hrdept.action.HRCancelAction)context.get("HRCancelAction");
  nc.ui.om.hrdept.action.HRCancelAction bean = new nc.ui.om.hrdept.action.HRCancelAction();
  context.put("HRCancelAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.HRBatchCancelAction getHRBatchCancelAction(){
 if(context.get("HRBatchCancelAction")!=null)
 return (nc.ui.om.hrdept.action.HRBatchCancelAction)context.get("HRBatchCancelAction");
  nc.ui.om.hrdept.action.HRBatchCancelAction bean = new nc.ui.om.hrdept.action.HRBatchCancelAction();
  context.put("HRBatchCancelAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getMgrModelDataManager());
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.HRUnCancelAction getHRUnCancelAction(){
 if(context.get("HRUnCancelAction")!=null)
 return (nc.ui.om.hrdept.action.HRUnCancelAction)context.get("HRUnCancelAction");
  nc.ui.om.hrdept.action.HRUnCancelAction bean = new nc.ui.om.hrdept.action.HRUnCancelAction();
  context.put("HRUnCancelAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.HRBatchUnCancelAction getHRBatchUnCancelAction(){
 if(context.get("HRBatchUnCancelAction")!=null)
 return (nc.ui.om.hrdept.action.HRBatchUnCancelAction)context.get("HRBatchUnCancelAction");
  nc.ui.om.hrdept.action.HRBatchUnCancelAction bean = new nc.ui.om.hrdept.action.HRBatchUnCancelAction();
  context.put("HRBatchUnCancelAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getMgrModelDataManager());
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.MenuAction getCardAlterationGroupAction(){
 if(context.get("cardAlterationGroupAction")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("cardAlterationGroupAction");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("cardAlterationGroupAction",bean);
  bean.setCode("alteration");
  bean.setName(getI18nFB_2cccf134());
  bean.setActions(getManagedList5());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_2cccf134(){
 if(context.get("nc.ui.uif2.I18nFB#2cccf134")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#2cccf134");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#2cccf134",bean);  bean.setResDir("menucode");
  bean.setDefaultValue("变更");
  bean.setResId("X600511");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#2cccf134",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList5(){  List list = new ArrayList();  list.add(getHRCancelAction());  list.add(getHRUnCancelAction());  return list;}

public nc.funcnode.ui.action.MenuAction getAlterationGroupAction(){
 if(context.get("AlterationGroupAction")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("AlterationGroupAction");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("AlterationGroupAction",bean);
  bean.setCode("alteration");
  bean.setName(getI18nFB_3c18942());
  bean.setActions(getManagedList6());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_3c18942(){
 if(context.get("nc.ui.uif2.I18nFB#3c18942")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#3c18942");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#3c18942",bean);  bean.setResDir("menucode");
  bean.setDefaultValue("变更");
  bean.setResId("X600511");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#3c18942",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList6(){  List list = new ArrayList();  list.add(getHRCancelAction());  list.add(getHRUnCancelAction());  return list;}

public nc.funcnode.ui.action.GroupAction getPrintGroupAction(){
 if(context.get("PrintGroupAction")!=null)
 return (nc.funcnode.ui.action.GroupAction)context.get("PrintGroupAction");
  nc.funcnode.ui.action.GroupAction bean = new nc.funcnode.ui.action.GroupAction();
  context.put("PrintGroupAction",bean);
  bean.setActions(getManagedList7());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList7(){  List list = new ArrayList();  list.add(getTemplatePrintAction());  list.add(getTemplatePreviewAction());  return list;}

public nc.ui.om.hrdept.model.DeptMetaDataDataSource getDatasource(){
 if(context.get("datasource")!=null)
 return (nc.ui.om.hrdept.model.DeptMetaDataDataSource)context.get("datasource");
  nc.ui.om.hrdept.model.DeptMetaDataDataSource bean = new nc.ui.om.hrdept.model.DeptMetaDataDataSource();
  context.put("datasource",bean);
  bean.setModel(getHAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.TemplatePriviewDeptAction getTemplatePreviewAction(){
 if(context.get("TemplatePreviewAction")!=null)
 return (nc.ui.om.hrdept.action.TemplatePriviewDeptAction)context.get("TemplatePreviewAction");
  nc.ui.om.hrdept.action.TemplatePriviewDeptAction bean = new nc.ui.om.hrdept.action.TemplatePriviewDeptAction();
  context.put("TemplatePreviewAction",bean);
  bean.setModel(getHAppModel());
  bean.setPrintDlgParentConatiner(getBillFormEditor());
  bean.setDatasource(getDatasource());
  bean.setNodeKey("hrdept");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.TemplatePrintDeptAction getTemplatePrintAction(){
 if(context.get("TemplatePrintAction")!=null)
 return (nc.ui.om.hrdept.action.TemplatePrintDeptAction)context.get("TemplatePrintAction");
  nc.ui.om.hrdept.action.TemplatePrintDeptAction bean = new nc.ui.om.hrdept.action.TemplatePrintDeptAction();
  context.put("TemplatePrintAction",bean);
  bean.setModel(getHAppModel());
  bean.setPrintDlgParentConatiner(getBillFormEditor());
  bean.setDatasource(getDatasource());
  bean.setNodeKey("hrdept");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.DeptFileManageAction getFileManageAction(){
 if(context.get("FileManageAction")!=null)
 return (nc.ui.om.hrdept.action.DeptFileManageAction)context.get("FileManageAction");
  nc.ui.om.hrdept.action.DeptFileManageAction bean = new nc.ui.om.hrdept.action.DeptFileManageAction();
  context.put("FileManageAction",bean);
  bean.setModel(getManageAppModel());
  bean.setResourceCode(getResourceCode());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.funcnode.ui.action.MenuAction getAssiciatedQueryAction(){
 if(context.get("AssiciatedQueryAction")!=null)
 return (nc.funcnode.ui.action.MenuAction)context.get("AssiciatedQueryAction");
  nc.funcnode.ui.action.MenuAction bean = new nc.funcnode.ui.action.MenuAction();
  context.put("AssiciatedQueryAction",bean);
  bean.setCode("AssiciatedQuery");
  bean.setName(getI18nFB_743c3520());
  bean.setActions(getManagedList8());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private java.lang.String getI18nFB_743c3520(){
 if(context.get("nc.ui.uif2.I18nFB#743c3520")!=null)
 return (java.lang.String)context.get("nc.ui.uif2.I18nFB#743c3520");
  nc.ui.uif2.I18nFB bean = new nc.ui.uif2.I18nFB();
    context.put("&nc.ui.uif2.I18nFB#743c3520",bean);  bean.setResDir("menucode");
  bean.setDefaultValue("联查");
  bean.setResId("X600512");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
 try {
     Object product = bean.getObject();
    context.put("nc.ui.uif2.I18nFB#743c3520",product);
     return (java.lang.String)product;
}
catch(Exception e) { throw new RuntimeException(e);}}

private List getManagedList8(){  List list = new ArrayList();  list.add(getQueryPostAction());  list.add(getQueryPersonAction());  return list;}

public nc.ui.om.hrdept.action.QueryPostAction getQueryPostAction(){
 if(context.get("QueryPostAction")!=null)
 return (nc.ui.om.hrdept.action.QueryPostAction)context.get("QueryPostAction");
  nc.ui.om.hrdept.action.QueryPostAction bean = new nc.ui.om.hrdept.action.QueryPostAction();
  context.put("QueryPostAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.QueryPersonAction getQueryPersonAction(){
 if(context.get("QueryPersonAction")!=null)
 return (nc.ui.om.hrdept.action.QueryPersonAction)context.get("QueryPersonAction");
  nc.ui.om.hrdept.action.QueryPersonAction bean = new nc.ui.om.hrdept.action.QueryPersonAction();
  context.put("QueryPersonAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.pub.validator.OMBillNotNullValidateService getBillNotNullValidator(){
 if(context.get("billNotNullValidator")!=null)
 return (nc.ui.om.pub.validator.OMBillNotNullValidateService)context.get("billNotNullValidator");
  nc.ui.om.pub.validator.OMBillNotNullValidateService bean = new nc.ui.om.pub.validator.OMBillNotNullValidateService(getBillFormEditor(),"pk_vid");  context.put("billNotNullValidator",bean);
  bean.setNextValidateService(getInsertUpdateValidator());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.validator.DeptInsertUpdateValidator getInsertUpdateValidator(){
 if(context.get("insertUpdateValidator")!=null)
 return (nc.ui.om.hrdept.validator.DeptInsertUpdateValidator)context.get("insertUpdateValidator");
  nc.ui.om.hrdept.validator.DeptInsertUpdateValidator bean = new nc.ui.om.hrdept.validator.DeptInsertUpdateValidator();
  context.put("insertUpdateValidator",bean);
  bean.setBillForm(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.validator.DeptDeleteValidator getDeleteValidator(){
 if(context.get("deleteValidator")!=null)
 return (nc.ui.om.hrdept.validator.DeptDeleteValidator)context.get("deleteValidator");
  nc.ui.om.hrdept.validator.DeptDeleteValidator bean = new nc.ui.om.hrdept.validator.DeptDeleteValidator();
  context.put("deleteValidator",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.validator.DeptModificationValidator getModificationValidator(){
 if(context.get("modificationValidator")!=null)
 return (nc.ui.om.hrdept.validator.DeptModificationValidator)context.get("modificationValidator");
  nc.ui.om.hrdept.validator.DeptModificationValidator bean = new nc.ui.om.hrdept.validator.DeptModificationValidator();
  context.put("modificationValidator",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.FunNodeClosingHandler getClosingListener(){
 if(context.get("ClosingListener")!=null)
 return (nc.ui.uif2.FunNodeClosingHandler)context.get("ClosingListener");
  nc.ui.uif2.FunNodeClosingHandler bean = new nc.ui.uif2.FunNodeClosingHandler();
  context.put("ClosingListener",bean);
  bean.setModel(getManageAppModel());
  bean.setSaveaction(getSaveAction());
  bean.setCancelaction(getCancelAction());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.QueryDeptAction getQueryAction(){
 if(context.get("QueryAction")!=null)
 return (nc.ui.om.hrdept.action.QueryDeptAction)context.get("QueryAction");
  nc.ui.om.hrdept.action.QueryDeptAction bean = new nc.ui.om.hrdept.action.QueryDeptAction();
  context.put("QueryAction",bean);
  bean.setModel(getManageAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setQueryDelegator(getHrQueryDelegator_6842c101());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.hr.uif2.HrQueryDelegator getHrQueryDelegator_6842c101(){
 if(context.get("nc.ui.hr.uif2.HrQueryDelegator#6842c101")!=null)
 return (nc.ui.hr.uif2.HrQueryDelegator)context.get("nc.ui.hr.uif2.HrQueryDelegator#6842c101");
  nc.ui.hr.uif2.HrQueryDelegator bean = new nc.ui.hr.uif2.HrQueryDelegator();
  context.put("nc.ui.hr.uif2.HrQueryDelegator#6842c101",bean);
  bean.setNodeKey("hrdept");
  bean.setContext(getContext());
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.hr.uif2.mediator.HyperLinkClickMediator getHyperLinkClickMediator(){
 if(context.get("hyperLinkClickMediator")!=null)
 return (nc.ui.hr.uif2.mediator.HyperLinkClickMediator)context.get("hyperLinkClickMediator");
  nc.ui.hr.uif2.mediator.HyperLinkClickMediator bean = new nc.ui.hr.uif2.mediator.HyperLinkClickMediator();
  context.put("hyperLinkClickMediator",bean);
  bean.setModel(getManageAppModel());
  bean.setShowUpComponent(getBillFormEditor());
  bean.setHyperLinkColumn("code");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.view.DeptBatchOrgRefPanel getOrgpanel(){
 if(context.get("orgpanel")!=null)
 return (nc.ui.om.hrdept.view.DeptBatchOrgRefPanel)context.get("orgpanel");
  nc.ui.om.hrdept.view.DeptBatchOrgRefPanel bean = new nc.ui.om.hrdept.view.DeptBatchOrgRefPanel();
  context.put("orgpanel",bean);
  bean.setModel(getHAppModel());
  bean.setDataManager(getModelDataManager());
  bean.setPk_orgtype("HRORGTYPE00000000000");
  bean.setControlType(0);
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.view.DeptListView getListView(){
 if(context.get("listView")!=null)
 return (nc.ui.om.hrdept.view.DeptListView)context.get("listView");
  nc.ui.om.hrdept.view.DeptListView bean = new nc.ui.om.hrdept.view.DeptListView();
  context.put("listView",bean);
  bean.setModel(getManageAppModel());
  bean.setMultiSelectionEnable(true);
  bean.setMultiSelectionMode(1);
  bean.setPos("head");
  bean.setNodekey("hrdept");
  bean.setTemplateContainer(getTemplateContainer());
  bean.setDataManager(getMgrModelDataManager());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.tangramlayout.node.VSNode getNaviNode(){
 if(context.get("naviNode")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("naviNode");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("naviNode",bean);
  bean.setUp(getCNode_234cd86c());
  bean.setDown(getCNode_2c48cede());
  bean.setDividerLocation(55f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_234cd86c(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#234cd86c")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#234cd86c");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#234cd86c",bean);
  bean.setComponent((java.lang.Object)findBeanInUIF2BeanFactory("naviStylePanel"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_2c48cede(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#2c48cede")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#2c48cede");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#2c48cede",bean);
  bean.setComponent((java.lang.Object)findBeanInUIF2BeanFactory("treeContainer"));
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setModel(getManageAppModel());
  bean.setTangramLayoutRoot(getVSNode_120aa40b());
  bean.setActions(getManagedList9());
  bean.setEditActions(getManagedList10());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_120aa40b(){
 if(context.get("nc.ui.uif2.tangramlayout.node.VSNode#120aa40b")!=null)
 return (nc.ui.uif2.tangramlayout.node.VSNode)context.get("nc.ui.uif2.tangramlayout.node.VSNode#120aa40b");
  nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
  context.put("nc.ui.uif2.tangramlayout.node.VSNode#120aa40b",bean);
  bean.setShowMode("NoDivider");
  bean.setUp(getCNode_39c87b42());
  bean.setDown(getHSNode_68e2d03e());
  bean.setDividerLocation(30f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_39c87b42(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#39c87b42")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#39c87b42");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#39c87b42",bean);
  bean.setComponent(getOrgpanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_68e2d03e(){
 if(context.get("nc.ui.uif2.tangramlayout.node.HSNode#68e2d03e")!=null)
 return (nc.ui.uif2.tangramlayout.node.HSNode)context.get("nc.ui.uif2.tangramlayout.node.HSNode#68e2d03e");
  nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
  context.put("nc.ui.uif2.tangramlayout.node.HSNode#68e2d03e",bean);
  bean.setLeft(getCNode_47fcefb3());
  bean.setRight(getCNode_236c098());
  bean.setDividerLocation(0.2f);
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_47fcefb3(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#47fcefb3")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#47fcefb3");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#47fcefb3",bean);
  bean.setComponent(getTreePanel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_236c098(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#236c098")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#236c098");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#236c098",bean);
  bean.setComponent(getBillFormEditor());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList9(){  List list = new ArrayList();  list.add(getAddAction());  list.add(getEditAction());  list.add(getDeleteAction());  list.add(getCopyDeptAction());  list.add(getNullaction());  list.add(getQueryAction());  list.add(getRefreshAction());  list.add(getFilterAction());  list.add(getNullaction());  list.add(getAlterationGroupAction());  list.add(getVersionAction());  list.add(getFileManageAction());  list.add(getNullaction());  list.add(getAssiciatedQueryAction());  list.add(getNullaction());  list.add(getPrintGroupAction());  list.add(getNullaction());  list.add(getCheckVersionErrorAction());  return list;}

private List getManagedList10(){  List list = new ArrayList();  list.add(getSaveAction());  list.add(getSaveAddAction());  list.add(getNullaction());  list.add(getCancelAction());  return list;}

public nc.ui.uif2.actions.ShowMeUpAction getEditorReturnAction(){
 if(context.get("editorReturnAction")!=null)
 return (nc.ui.uif2.actions.ShowMeUpAction)context.get("editorReturnAction");
  nc.ui.uif2.actions.ShowMeUpAction bean = new nc.ui.uif2.actions.ShowMeUpAction();
  context.put("editorReturnAction",bean);
  bean.setGoComponent(getListView());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel getEditorToolBarPanel(){
 if(context.get("editorToolBarPanel")!=null)
 return (nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel)context.get("editorToolBarPanel");
  nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel bean = new nc.ui.uif2.tangramlayout.CardLayoutToolbarPanel();
  context.put("editorToolBarPanel",bean);
  bean.setModel(getManageAppModel());
  bean.setTitleAction(getEditorReturnAction());
  bean.setActions(getManagedList11());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList11(){  List list = new ArrayList();  list.add(getFirstLineAction());  list.add(getPreLineAction());  list.add(getNextLineAction());  list.add(getLastLineAction());  return list;}

public nc.ui.uif2.actions.FirstLineAction getFirstLineAction(){
 if(context.get("FirstLineAction")!=null)
 return (nc.ui.uif2.actions.FirstLineAction)context.get("FirstLineAction");
  nc.ui.uif2.actions.FirstLineAction bean = new nc.ui.uif2.actions.FirstLineAction();
  context.put("FirstLineAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.PreLineAction getPreLineAction(){
 if(context.get("PreLineAction")!=null)
 return (nc.ui.uif2.actions.PreLineAction)context.get("PreLineAction");
  nc.ui.uif2.actions.PreLineAction bean = new nc.ui.uif2.actions.PreLineAction();
  context.put("PreLineAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.NextLineAction getNextLineAction(){
 if(context.get("NextLineAction")!=null)
 return (nc.ui.uif2.actions.NextLineAction)context.get("NextLineAction");
  nc.ui.uif2.actions.NextLineAction bean = new nc.ui.uif2.actions.NextLineAction();
  context.put("NextLineAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.actions.LastLineAction getLastLineAction(){
 if(context.get("LastLineAction")!=null)
 return (nc.ui.uif2.actions.LastLineAction)context.get("LastLineAction");
  nc.ui.uif2.actions.LastLineAction bean = new nc.ui.uif2.actions.LastLineAction();
  context.put("LastLineAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.om.hrdept.action.CheckVersionErrorAction getCheckVersionErrorAction(){
 if(context.get("CheckVersionErrorAction")!=null)
 return (nc.ui.om.hrdept.action.CheckVersionErrorAction)context.get("CheckVersionErrorAction");
  nc.ui.om.hrdept.action.CheckVersionErrorAction bean = new nc.ui.om.hrdept.action.CheckVersionErrorAction();
  context.put("CheckVersionErrorAction",bean);
  bean.setModel(getManageAppModel());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
