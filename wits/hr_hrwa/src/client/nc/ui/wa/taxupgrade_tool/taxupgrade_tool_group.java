package nc.ui.wa.taxupgrade_tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class taxupgrade_tool_group extends AbstractJavaBeanDefinition{
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

public nc.ui.wa.taxupgrade_tool.model.Taxupgrade_toolModelService getModelService(){
 if(context.get("ModelService")!=null)
 return (nc.ui.wa.taxupgrade_tool.model.Taxupgrade_toolModelService)context.get("ModelService");
  nc.ui.wa.taxupgrade_tool.model.Taxupgrade_toolModelService bean = new nc.ui.wa.taxupgrade_tool.model.Taxupgrade_toolModelService();
  context.put("ModelService",bean);
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

public nc.ui.wa.taxupgrade_tool.model.Taxupgrade_toolAppModel getAppModel(){
 if(context.get("appModel")!=null)
 return (nc.ui.wa.taxupgrade_tool.model.Taxupgrade_toolAppModel)context.get("appModel");
  nc.ui.wa.taxupgrade_tool.model.Taxupgrade_toolAppModel bean = new nc.ui.wa.taxupgrade_tool.model.Taxupgrade_toolAppModel();
  context.put("appModel",bean);
  bean.setService(getModelService());
  bean.setBusinessObjectAdapterFactory(getBoadatorfactory());
  bean.setContext(getContext());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxupgrade_tool.view.Taxupgrade_toolForm getViewForm(){
 if(context.get("viewForm")!=null)
 return (nc.ui.wa.taxupgrade_tool.view.Taxupgrade_toolForm)context.get("viewForm");
  nc.ui.wa.taxupgrade_tool.view.Taxupgrade_toolForm bean = new nc.ui.wa.taxupgrade_tool.view.Taxupgrade_toolForm();
  context.put("viewForm",bean);
  bean.setModel(getAppModel());
  bean.initUI();
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

public nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitItemAction getInitItemAction(){
 if(context.get("initItemAction")!=null)
 return (nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitItemAction)context.get("initItemAction");
  nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitItemAction bean = new nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitItemAction();
  context.put("initItemAction",bean);
  bean.setModel(getAppModel());
  bean.setEditor(getViewForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitClassItemAction getInitClassItemAction(){
 if(context.get("initClassItemAction")!=null)
 return (nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitClassItemAction)context.get("initClassItemAction");
  nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitClassItemAction bean = new nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitClassItemAction();
  context.put("initClassItemAction",bean);
  bean.setModel(getAppModel());
  bean.setEditor(getViewForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitTaxTableAction getInitTaxAction(){
 if(context.get("initTaxAction")!=null)
 return (nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitTaxTableAction)context.get("initTaxAction");
  nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitTaxTableAction bean = new nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitTaxTableAction();
  context.put("initTaxAction",bean);
  bean.setModel(getAppModel());
  bean.setEditor(getViewForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitPeriodAction getInitPeriodAction(){
 if(context.get("initPeriodAction")!=null)
 return (nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitPeriodAction)context.get("initPeriodAction");
  nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitPeriodAction bean = new nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolInitPeriodAction();
  context.put("initPeriodAction",bean);
  bean.setModel(getAppModel());
  bean.setEditor(getViewForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolRefreshAction getRefreshAction(){
 if(context.get("refreshAction")!=null)
 return (nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolRefreshAction)context.get("refreshAction");
  nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolRefreshAction bean = new nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolRefreshAction();
  context.put("refreshAction",bean);
  bean.setModel(getAppModel());
  bean.setEditor(getViewForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolUnIninitAction getUnInitAction(){
 if(context.get("unInitAction")!=null)
 return (nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolUnIninitAction)context.get("unInitAction");
  nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolUnIninitAction bean = new nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolUnIninitAction();
  context.put("unInitAction",bean);
  bean.setModel(getAppModel());
  bean.setEditor(getViewForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolDeductionUpgradeAction getDeductionUpgradeAction(){
 if(context.get("deductionUpgradeAction")!=null)
 return (nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolDeductionUpgradeAction)context.get("deductionUpgradeAction");
  nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolDeductionUpgradeAction bean = new nc.ui.wa.taxupgrade_tool.action.Taxupgrade_toolDeductionUpgradeAction();
  context.put("deductionUpgradeAction",bean);
  bean.setModel(getAppModel());
  bean.setEditor(getViewForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxupgrade_tool.action.Taxupgrade_taxorgUpgradeAction getTaxorgUpgradeAction(){
 if(context.get("taxorgUpgradeAction")!=null)
 return (nc.ui.wa.taxupgrade_tool.action.Taxupgrade_taxorgUpgradeAction)context.get("taxorgUpgradeAction");
  nc.ui.wa.taxupgrade_tool.action.Taxupgrade_taxorgUpgradeAction bean = new nc.ui.wa.taxupgrade_tool.action.Taxupgrade_taxorgUpgradeAction();
  context.put("taxorgUpgradeAction",bean);
  bean.setModel(getAppModel());
  bean.setEditor(getViewForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.wa.taxupgrade_tool.action.Taxupgrade_totalIncomeUpgradeAction getTotalIncomeUpgradeAction(){
 if(context.get("totalIncomeUpgradeAction")!=null)
 return (nc.ui.wa.taxupgrade_tool.action.Taxupgrade_totalIncomeUpgradeAction)context.get("totalIncomeUpgradeAction");
  nc.ui.wa.taxupgrade_tool.action.Taxupgrade_totalIncomeUpgradeAction bean = new nc.ui.wa.taxupgrade_tool.action.Taxupgrade_totalIncomeUpgradeAction();
  context.put("totalIncomeUpgradeAction",bean);
  bean.setModel(getAppModel());
  bean.setEditor(getViewForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.uif2.TangramContainer getContainer(){
 if(context.get("container")!=null)
 return (nc.ui.uif2.TangramContainer)context.get("container");
  nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
  context.put("container",bean);
  bean.setTangramLayoutRoot(getCNode_aaadb5());
  bean.setActions(getManagedList0());
  bean.setModel(getAppModel());
  bean.initUI();
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private nc.ui.uif2.tangramlayout.node.CNode getCNode_aaadb5(){
 if(context.get("nc.ui.uif2.tangramlayout.node.CNode#aaadb5")!=null)
 return (nc.ui.uif2.tangramlayout.node.CNode)context.get("nc.ui.uif2.tangramlayout.node.CNode#aaadb5");
  nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
  context.put("nc.ui.uif2.tangramlayout.node.CNode#aaadb5",bean);
  bean.setComponent(getViewForm());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

private List getManagedList0(){  List list = new ArrayList();  list.add(getRefreshAction());  list.add(getSeparatorAction());  list.add(getInitPeriodAction());  list.add(getSeparatorAction());  list.add(getInitTaxAction());  list.add(getSeparatorAction());  list.add(getInitItemAction());  list.add(getSeparatorAction());  list.add(getInitClassItemAction());  list.add(getSeparatorAction());  list.add(getTaxorgUpgradeAction());  list.add(getSeparatorAction());  list.add(getUnInitAction());  list.add(getSeparatorAction());  list.add(getDeductionUpgradeAction());  list.add(getSeparatorAction());  list.add(getTotalIncomeUpgradeAction());  return list;}

}
