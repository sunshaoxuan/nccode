package nc.ui.hrwa.sumincometax.ace.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class Sumincometax_config extends AbstractJavaBeanDefinition {
	private Map<String, Object> context = new HashMap();

	public nc.vo.uif2.LoginContext getContext() {
		if (context.get("context") != null)
			return (nc.vo.uif2.LoginContext) context.get("context");
		nc.vo.uif2.LoginContext bean = new nc.vo.uif2.LoginContext();
		context.put("context", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hrwa.sumincometax.ace.serviceproxy.AceSumincometaxMaintainProxy getBmModelModelService() {
		if (context.get("bmModelModelService") != null)
			return (nc.ui.hrwa.sumincometax.ace.serviceproxy.AceSumincometaxMaintainProxy) context
					.get("bmModelModelService");
		nc.ui.hrwa.sumincometax.ace.serviceproxy.AceSumincometaxMaintainProxy bean = new nc.ui.hrwa.sumincometax.ace.serviceproxy.AceSumincometaxMaintainProxy();
		context.put("bmModelModelService", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.vo.bd.meta.GeneralBDObjectAdapterFactory getBOAdapterFactory() {
		if (context.get("BOAdapterFactory") != null)
			return (nc.vo.bd.meta.GeneralBDObjectAdapterFactory) context
					.get("BOAdapterFactory");
		nc.vo.bd.meta.GeneralBDObjectAdapterFactory bean = new nc.vo.bd.meta.GeneralBDObjectAdapterFactory();
		context.put("BOAdapterFactory", bean);
		bean.setMode("MD");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.BillManageModel getBmModel() {
		if (context.get("bmModel") != null)
			return (nc.ui.pubapp.uif2app.model.BillManageModel) context
					.get("bmModel");
		nc.ui.pubapp.uif2app.model.BillManageModel bean = new nc.ui.pubapp.uif2app.model.BillManageModel();
		context.put("bmModel", bean);
		bean.setContext(getContext());
		bean.setBusinessObjectAdapterFactory(getBOAdapterFactory());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.query2.model.ModelDataManager getBmModelModelDataManager() {
		if (context.get("bmModelModelDataManager") != null)
			return (nc.ui.pubapp.uif2app.query2.model.ModelDataManager) context
					.get("bmModelModelDataManager");
		nc.ui.pubapp.uif2app.query2.model.ModelDataManager bean = new nc.ui.pubapp.uif2app.query2.model.ModelDataManager();
		context.put("bmModelModelDataManager", bean);
		bean.setModel(getBmModel());
		bean.setService(getBmModelModelService());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.TemplateContainer getTemplateContainer() {
		if (context.get("templateContainer") != null)
			return (nc.ui.pubapp.uif2app.view.TemplateContainer) context
					.get("templateContainer");
		nc.ui.pubapp.uif2app.view.TemplateContainer bean = new nc.ui.pubapp.uif2app.view.TemplateContainer();
		context.put("templateContainer", bean);
		bean.setContext(getContext());
		bean.setNodeKeies(getManagedList0());
		bean.load();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList0() {
		List list = new ArrayList();
		list.add("bt");
		return list;
	}

	public nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell getViewa() {
		if (context.get("viewa") != null)
			return (nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell) context
					.get("viewa");
		nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell bean = new nc.ui.pubapp.uif2app.tangramlayout.UEQueryAreaShell();
		context.put("viewa", bean);
		bean.setQueryAreaCreator(getDefaultQueryAction());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.ShowUpableBillListView getBillListView() {
		if (context.get("billListView") != null)
			return (nc.ui.pubapp.uif2app.view.ShowUpableBillListView) context
					.get("billListView");
		nc.ui.pubapp.uif2app.view.ShowUpableBillListView bean = new nc.ui.pubapp.uif2app.view.ShowUpableBillListView();
		context.put("billListView", bean);
		bean.setModel(getBmModel());
		bean.setNodekey("bt");
		bean.setMultiSelectionEnable(true);
		bean.setMultiSelectionMode(1);
		bean.setTemplateContainer(getTemplateContainer());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel getViewb() {
		if (context.get("viewb") != null)
			return (nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel) context
					.get("viewb");
		nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel bean = new nc.ui.pubapp.uif2app.tangramlayout.UECardLayoutToolbarPanel();
		context.put("viewb", bean);
		bean.setModel(getBmModel());
		bean.setTitleAction(getReturnAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.UEReturnAction getReturnAction() {
		if (context.get("returnAction") != null)
			return (nc.ui.pubapp.uif2app.actions.UEReturnAction) context
					.get("returnAction");
		nc.ui.pubapp.uif2app.actions.UEReturnAction bean = new nc.ui.pubapp.uif2app.actions.UEReturnAction();
		context.put("returnAction", bean);
		bean.setGoComponent(getBillListView());
		bean.setSaveAction(getSaveAction());
		bean.setModel(getBmModel());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.ShowUpableBillForm getBillForm() {
		if (context.get("billForm") != null)
			return (nc.ui.pubapp.uif2app.view.ShowUpableBillForm) context
					.get("billForm");
		nc.ui.pubapp.uif2app.view.ShowUpableBillForm bean = new nc.ui.pubapp.uif2app.view.ShowUpableBillForm();
		context.put("billForm", bean);
		bean.setModel(getBmModel());
		bean.setNodekey("bt");
		bean.setBodyLineActions(getManagedList1());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList1() {
		List list = new ArrayList();
		return list;
	}

	public nc.ui.uif2.TangramContainer getContainer() {
		if (context.get("container") != null)
			return (nc.ui.uif2.TangramContainer) context.get("container");
		nc.ui.uif2.TangramContainer bean = new nc.ui.uif2.TangramContainer();
		context.put("container", bean);
		bean.setTangramLayoutRoot(getTBNode_6c7b3659());
		bean.setActions(getManagedList3());
		bean.setEditActions(getManagedList4());
		bean.setModel(getBmModel());
		bean.initUI();
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.TBNode getTBNode_6c7b3659() {
		if (context.get("nc.ui.uif2.tangramlayout.node.TBNode#6c7b3659") != null)
			return (nc.ui.uif2.tangramlayout.node.TBNode) context
					.get("nc.ui.uif2.tangramlayout.node.TBNode#6c7b3659");
		nc.ui.uif2.tangramlayout.node.TBNode bean = new nc.ui.uif2.tangramlayout.node.TBNode();
		context.put("nc.ui.uif2.tangramlayout.node.TBNode#6c7b3659", bean);
		bean.setTabs(getManagedList2());
		bean.setName("cardLayout");
		bean.setShowMode("CardLayout");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList2() {
		List list = new ArrayList();
		list.add(getHSNode_386930d0());
		list.add(getVSNode_d45f77d());
		return list;
	}

	private nc.ui.uif2.tangramlayout.node.HSNode getHSNode_386930d0() {
		if (context.get("nc.ui.uif2.tangramlayout.node.HSNode#386930d0") != null)
			return (nc.ui.uif2.tangramlayout.node.HSNode) context
					.get("nc.ui.uif2.tangramlayout.node.HSNode#386930d0");
		nc.ui.uif2.tangramlayout.node.HSNode bean = new nc.ui.uif2.tangramlayout.node.HSNode();
		context.put("nc.ui.uif2.tangramlayout.node.HSNode#386930d0", bean);
		bean.setLeft(getCNode_4ee3ed7c());
		bean.setRight(getCNode_55fc905a());
		bean.setDividerLocation(215.0f);
		bean.setName("�б�");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_4ee3ed7c() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#4ee3ed7c") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#4ee3ed7c");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#4ee3ed7c", bean);
		bean.setComponent(getViewa());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_55fc905a() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#55fc905a") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#55fc905a");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#55fc905a", bean);
		bean.setComponent(getBillListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.VSNode getVSNode_d45f77d() {
		if (context.get("nc.ui.uif2.tangramlayout.node.VSNode#d45f77d") != null)
			return (nc.ui.uif2.tangramlayout.node.VSNode) context
					.get("nc.ui.uif2.tangramlayout.node.VSNode#d45f77d");
		nc.ui.uif2.tangramlayout.node.VSNode bean = new nc.ui.uif2.tangramlayout.node.VSNode();
		context.put("nc.ui.uif2.tangramlayout.node.VSNode#d45f77d", bean);
		bean.setUp(getCNode_203c49f());
		bean.setDown(getCNode_4561a3c1());
		bean.setDividerLocation(43.0f);
		bean.setName("��Ƭ");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_203c49f() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#203c49f") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#203c49f");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#203c49f", bean);
		bean.setComponent(getViewb());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.uif2.tangramlayout.node.CNode getCNode_4561a3c1() {
		if (context.get("nc.ui.uif2.tangramlayout.node.CNode#4561a3c1") != null)
			return (nc.ui.uif2.tangramlayout.node.CNode) context
					.get("nc.ui.uif2.tangramlayout.node.CNode#4561a3c1");
		nc.ui.uif2.tangramlayout.node.CNode bean = new nc.ui.uif2.tangramlayout.node.CNode();
		context.put("nc.ui.uif2.tangramlayout.node.CNode#4561a3c1", bean);
		bean.setComponent(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList3() {
		List list = new ArrayList();
		list.add(getEditAction());
		list.add(getDeleteAction());
		list.add(getDefaultQueryAction());
		list.add(getSeparatorAction());
		list.add(getDefaultRefreshAction());
		list.add(getSeparatorAction());
		list.add(getMarkAction());
		list.add(getUnMarkAction());
		list.add(getSeparatorAction());
		list.add(getExportSumIncomeAction());
		list.add(getSeparatorAction());
		list.add(getExportApplyAction());
		return list;
	}

	private List getManagedList4() {
		List list = new ArrayList();
		list.add(getSaveAction());
		list.add(getCancelAction());
		return list;
	}

	public nc.ui.hrwa.sumincometax.ace.listener.SumIncomeFuncNodeInitDataListener getInitDataListener() {
		if (context.get("InitDataListener") != null)
			return (nc.ui.hrwa.sumincometax.ace.listener.SumIncomeFuncNodeInitDataListener) context
					.get("InitDataListener");
		nc.ui.hrwa.sumincometax.ace.listener.SumIncomeFuncNodeInitDataListener bean = new nc.ui.hrwa.sumincometax.ace.listener.SumIncomeFuncNodeInitDataListener();
		context.put("InitDataListener", bean);
		bean.setModel(getBmModel());
		bean.setContext(getContext());
		bean.setVoClassName("nc.vo.hrwa.sumincometax.AggSumIncomeTaxVO");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.common.validateservice.ClosingCheck getClosingListener() {
		if (context.get("ClosingListener") != null)
			return (nc.ui.pubapp.common.validateservice.ClosingCheck) context
					.get("ClosingListener");
		nc.ui.pubapp.common.validateservice.ClosingCheck bean = new nc.ui.pubapp.common.validateservice.ClosingCheck();
		context.put("ClosingListener", bean);
		bean.setModel(getBmModel());
		bean.setSaveAction(getSaveAction());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.model.AppEventHandlerMediator getBmModelEventMediator() {
		if (context.get("bmModelEventMediator") != null)
			return (nc.ui.pubapp.uif2app.model.AppEventHandlerMediator) context
					.get("bmModelEventMediator");
		nc.ui.pubapp.uif2app.model.AppEventHandlerMediator bean = new nc.ui.pubapp.uif2app.model.AppEventHandlerMediator();
		context.put("bmModelEventMediator", bean);
		bean.setModel(getBmModel());
		bean.setHandlerGroup(getManagedList5());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList5() {
		List list = new ArrayList();
		list.add(getEventHandlerGroup_16f94a87());
		list.add(getEventHandlerGroup_5b715bc4());
		list.add(getEventHandlerGroup_4661029d());
		return list;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_16f94a87() {
		if (context
				.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#16f94a87") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
					.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#16f94a87");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#16f94a87",
				bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.OrgChangedEvent");
		bean.setHandler(getAceOrgChangeHandler_d2d44b3());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.hrwa.sumincometax.ace.handler.AceOrgChangeHandler getAceOrgChangeHandler_d2d44b3() {
		if (context
				.get("nc.ui.hrwa.sumincometax.ace.handler.AceOrgChangeHandler#d2d44b3") != null)
			return (nc.ui.hrwa.sumincometax.ace.handler.AceOrgChangeHandler) context
					.get("nc.ui.hrwa.sumincometax.ace.handler.AceOrgChangeHandler#d2d44b3");
		nc.ui.hrwa.sumincometax.ace.handler.AceOrgChangeHandler bean = new nc.ui.hrwa.sumincometax.ace.handler.AceOrgChangeHandler();
		context.put(
				"nc.ui.hrwa.sumincometax.ace.handler.AceOrgChangeHandler#d2d44b3",
				bean);
		bean.setBillForm(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_5b715bc4() {
		if (context
				.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#5b715bc4") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
					.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#5b715bc4");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#5b715bc4",
				bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.billform.AddEvent");
		bean.setHandler(getAceAddHandler_488bf0d9());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.hrwa.sumincometax.ace.handler.AceAddHandler getAceAddHandler_488bf0d9() {
		if (context
				.get("nc.ui.hrwa.sumincometax.ace.handler.AceAddHandler#488bf0d9") != null)
			return (nc.ui.hrwa.sumincometax.ace.handler.AceAddHandler) context
					.get("nc.ui.hrwa.sumincometax.ace.handler.AceAddHandler#488bf0d9");
		nc.ui.hrwa.sumincometax.ace.handler.AceAddHandler bean = new nc.ui.hrwa.sumincometax.ace.handler.AceAddHandler();
		context.put(
				"nc.ui.hrwa.sumincometax.ace.handler.AceAddHandler#488bf0d9",
				bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.event.EventHandlerGroup getEventHandlerGroup_4661029d() {
		if (context
				.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#4661029d") != null)
			return (nc.ui.pubapp.uif2app.event.EventHandlerGroup) context
					.get("nc.ui.pubapp.uif2app.event.EventHandlerGroup#4661029d");
		nc.ui.pubapp.uif2app.event.EventHandlerGroup bean = new nc.ui.pubapp.uif2app.event.EventHandlerGroup();
		context.put("nc.ui.pubapp.uif2app.event.EventHandlerGroup#4661029d",
				bean);
		bean.setEvent("nc.ui.pubapp.uif2app.event.card.CardHeadTailAfterEditEvent");
		bean.setHandler(getAceHeadAfterEditHandler_6f3fcfc2());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.hrwa.sumincometax.ace.handler.AceHeadAfterEditHandler getAceHeadAfterEditHandler_6f3fcfc2() {
		if (context
				.get("nc.ui.hrwa.sumincometax.ace.handler.AceHeadAfterEditHandler#6f3fcfc2") != null)
			return (nc.ui.hrwa.sumincometax.ace.handler.AceHeadAfterEditHandler) context
					.get("nc.ui.hrwa.sumincometax.ace.handler.AceHeadAfterEditHandler#6f3fcfc2");
		nc.ui.hrwa.sumincometax.ace.handler.AceHeadAfterEditHandler bean = new nc.ui.hrwa.sumincometax.ace.handler.AceHeadAfterEditHandler();
		context.put(
				"nc.ui.hrwa.sumincometax.ace.handler.AceHeadAfterEditHandler#6f3fcfc2",
				bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hrwa.sumincometax.ace.handler.SumincometaxBillLazilyLoader getBillLazilyLoader() {
		if (context.get("billLazilyLoader") != null)
			return (nc.ui.hrwa.sumincometax.ace.handler.SumincometaxBillLazilyLoader) context
					.get("billLazilyLoader");
		nc.ui.hrwa.sumincometax.ace.handler.SumincometaxBillLazilyLoader bean = new nc.ui.hrwa.sumincometax.ace.handler.SumincometaxBillLazilyLoader();
		context.put("billLazilyLoader", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager getBmModelLasilyLodadMediator() {
		if (context.get("bmModelLasilyLodadMediator") != null)
			return (nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager) context
					.get("bmModelLasilyLodadMediator");
		nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager bean = new nc.ui.pubapp.uif2app.lazilyload.LazilyLoadManager();
		context.put("bmModelLasilyLodadMediator", bean);
		bean.setModel(getBmModel());
		bean.setLoader(getBillLazilyLoader());
		bean.setLazilyLoadSupporter(getManagedList6());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList6() {
		List list = new ArrayList();
		list.add(getCardPanelLazilyLoad_af2b584());
		list.add(getListPanelLazilyLoad_446af2b9());
		return list;
	}

	private nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad getCardPanelLazilyLoad_af2b584() {
		if (context
				.get("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#af2b584") != null)
			return (nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad) context
					.get("nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#af2b584");
		nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad();
		context.put(
				"nc.ui.pubapp.uif2app.lazilyload.CardPanelLazilyLoad#af2b584",
				bean);
		bean.setBillform(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad getListPanelLazilyLoad_446af2b9() {
		if (context
				.get("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#446af2b9") != null)
			return (nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad) context
					.get("nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#446af2b9");
		nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad bean = new nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad();
		context.put(
				"nc.ui.pubapp.uif2app.lazilyload.ListPanelLazilyLoad#446af2b9",
				bean);
		bean.setListView(getBillListView());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.RowNoMediator getRowNoMediator() {
		if (context.get("rowNoMediator") != null)
			return (nc.ui.pubapp.uif2app.view.RowNoMediator) context
					.get("rowNoMediator");
		nc.ui.pubapp.uif2app.view.RowNoMediator bean = new nc.ui.pubapp.uif2app.view.RowNoMediator();
		context.put("rowNoMediator", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator getMouseClickShowPanelMediator() {
		if (context.get("mouseClickShowPanelMediator") != null)
			return (nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator) context
					.get("mouseClickShowPanelMediator");
		nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator bean = new nc.ui.pubapp.uif2app.view.MouseClickShowPanelMediator();
		context.put("mouseClickShowPanelMediator", bean);
		bean.setListView(getBillListView());
		bean.setShowUpComponent(getBillForm());
		bean.setHyperLinkColumn("null");
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.AddAction getAddAction() {
		if (context.get("addAction") != null)
			return (nc.ui.pubapp.uif2app.actions.AddAction) context
					.get("addAction");
		nc.ui.pubapp.uif2app.actions.AddAction bean = new nc.ui.pubapp.uif2app.actions.AddAction();
		context.put("addAction", bean);
		bean.setModel(getBmModel());
		bean.setInterceptor(getCompositeActionInterceptor_23524acf());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor getCompositeActionInterceptor_23524acf() {
		if (context
				.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#23524acf") != null)
			return (nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor) context
					.get("nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#23524acf");
		nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor();
		context.put(
				"nc.ui.pubapp.uif2app.actions.interceptor.CompositeActionInterceptor#23524acf",
				bean);
		bean.setInterceptors(getManagedList7());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	private List getManagedList7() {
		List list = new ArrayList();
		list.add(getShowUpComponentInterceptor_496572c6());
		return list;
	}

	private nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor getShowUpComponentInterceptor_496572c6() {
		if (context
				.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#496572c6") != null)
			return (nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor) context
					.get("nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#496572c6");
		nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor bean = new nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor();
		context.put(
				"nc.ui.pubapp.uif2app.actions.interceptor.ShowUpComponentInterceptor#496572c6",
				bean);
		bean.setShowUpComponent(getBillForm());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hrwa.sumincometax.action.EditAction getEditAction() {
		if (context.get("editAction") != null)
			return (nc.ui.hrwa.sumincometax.action.EditAction) context
					.get("editAction");
		nc.ui.hrwa.sumincometax.action.EditAction bean = new nc.ui.hrwa.sumincometax.action.EditAction();
		context.put("editAction", bean);
		bean.setModel(getBmModel());
		bean.setShowUpComponent(getBillForm());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hrwa.sumincometax.action.DeleteAction getDeleteAction() {
		if (context.get("deleteAction") != null)
			return (nc.ui.hrwa.sumincometax.action.DeleteAction) context
					.get("deleteAction");
		nc.ui.hrwa.sumincometax.action.DeleteAction bean = new nc.ui.hrwa.sumincometax.action.DeleteAction();
		context.put("deleteAction", bean);
		bean.setModel(getBmModel());
		bean.setSingleBillService(getBmModelModelService());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.editor.QueryTemplateContainer getDefaultQueryActionQueryTemplateContainer() {
		if (context.get("defaultQueryActionQueryTemplateContainer") != null)
			return (nc.ui.uif2.editor.QueryTemplateContainer) context
					.get("defaultQueryActionQueryTemplateContainer");
		nc.ui.uif2.editor.QueryTemplateContainer bean = new nc.ui.uif2.editor.QueryTemplateContainer();
		context.put("defaultQueryActionQueryTemplateContainer", bean);
		bean.setNodeKey("qt");
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction getDefaultQueryAction() {
		if (context.get("defaultQueryAction") != null)
			return (nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction) context
					.get("defaultQueryAction");
		nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultQueryAction();
		context.put("defaultQueryAction", bean);
		bean.setModel(getBmModel());
		bean.setTemplateContainer(getDefaultQueryActionQueryTemplateContainer());
		bean.setNodeKey("qt");
		bean.setDataManager(getBmModelModelDataManager());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction getDefaultRefreshAction() {
		if (context.get("defaultRefreshAction") != null)
			return (nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction) context
					.get("defaultRefreshAction");
		nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction bean = new nc.ui.pubapp.uif2app.query2.action.DefaultRefreshAction();
		context.put("defaultRefreshAction", bean);
		bean.setModel(getBmModel());
		bean.setDataManager(getBmModelModelDataManager());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction getMetaDataBasedPrintAction() {
		if (context.get("metaDataBasedPrintAction") != null)
			return (nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction) context
					.get("metaDataBasedPrintAction");
		nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction bean = new nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction();
		context.put("metaDataBasedPrintAction", bean);
		bean.setModel(getBmModel());
		bean.setActioncode("Preview");
		bean.setActionname("Ԥ��");
		bean.setPreview(true);
		bean.setNodeKey("ot");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction getMetaDataBasedPrintActiona() {
		if (context.get("metaDataBasedPrintActiona") != null)
			return (nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction) context
					.get("metaDataBasedPrintActiona");
		nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction bean = new nc.ui.pubapp.uif2app.actions.MetaDataBasedPrintAction();
		context.put("metaDataBasedPrintActiona", bean);
		bean.setModel(getBmModel());
		bean.setActioncode("Print");
		bean.setActionname("��ӡ");
		bean.setPreview(false);
		bean.setNodeKey("ot");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.OutputAction getOutputAction() {
		if (context.get("outputAction") != null)
			return (nc.ui.pubapp.uif2app.actions.OutputAction) context
					.get("outputAction");
		nc.ui.pubapp.uif2app.actions.OutputAction bean = new nc.ui.pubapp.uif2app.actions.OutputAction();
		context.put("outputAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setNodeKey("ot");
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.DifferentVOSaveAction getSaveAction() {
		if (context.get("saveAction") != null)
			return (nc.ui.pubapp.uif2app.actions.DifferentVOSaveAction) context
					.get("saveAction");
		nc.ui.pubapp.uif2app.actions.DifferentVOSaveAction bean = new nc.ui.pubapp.uif2app.actions.DifferentVOSaveAction();
		context.put("saveAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setService(getBmModelModelService());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.pubapp.uif2app.actions.CancelAction getCancelAction() {
		if (context.get("cancelAction") != null)
			return (nc.ui.pubapp.uif2app.actions.CancelAction) context
					.get("cancelAction");
		nc.ui.pubapp.uif2app.actions.CancelAction bean = new nc.ui.pubapp.uif2app.actions.CancelAction();
		context.put("cancelAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hrwa.sumincometax.action.MarkAction getMarkAction() {
		if (context.get("markAction") != null)
			return (nc.ui.hrwa.sumincometax.action.MarkAction) context
					.get("markAction");
		nc.ui.hrwa.sumincometax.action.MarkAction bean = new nc.ui.hrwa.sumincometax.action.MarkAction();
		context.put("markAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setService(getBmModelModelService());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hrwa.sumincometax.action.UnMarkAction getUnMarkAction() {
		if (context.get("unMarkAction") != null)
			return (nc.ui.hrwa.sumincometax.action.UnMarkAction) context
					.get("unMarkAction");
		nc.ui.hrwa.sumincometax.action.UnMarkAction bean = new nc.ui.hrwa.sumincometax.action.UnMarkAction();
		context.put("unMarkAction", bean);
		bean.setModel(getBmModel());
		bean.setEditor(getBillForm());
		bean.setService(getBmModelModelService());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hrwa.sumincometax.action.ExportSumIncomeAction getExportSumIncomeAction() {
		if (context.get("exportSumIncomeAction") != null)
			return (nc.ui.hrwa.sumincometax.action.ExportSumIncomeAction) context
					.get("exportSumIncomeAction");
		nc.ui.hrwa.sumincometax.action.ExportSumIncomeAction bean = new nc.ui.hrwa.sumincometax.action.ExportSumIncomeAction();
		context.put("exportSumIncomeAction", bean);
		bean.setModel(getBmModel());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.funcnode.ui.action.SeparatorAction getSeparatorAction() {
		if (context.get("separatorAction") != null)
			return (nc.funcnode.ui.action.SeparatorAction) context
					.get("separatorAction");
		nc.funcnode.ui.action.SeparatorAction bean = new nc.funcnode.ui.action.SeparatorAction();
		context.put("separatorAction", bean);
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.uif2.DefaultExceptionHanler getExceptionHandler() {
		if (context.get("exceptionHandler") != null)
			return (nc.ui.uif2.DefaultExceptionHanler) context
					.get("exceptionHandler");
		nc.ui.uif2.DefaultExceptionHanler bean = new nc.ui.uif2.DefaultExceptionHanler(
				getContainer());
		context.put("exceptionHandler", bean);
		bean.setContext(getContext());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

	public nc.ui.hrwa.sumincometax.action.ExportApplyAction getExportApplyAction() {
		if (context.get("exportApplyAction") != null)
			return (nc.ui.hrwa.sumincometax.action.ExportApplyAction) context
					.get("exportApplyAction");
		nc.ui.hrwa.sumincometax.action.ExportApplyAction bean = new nc.ui.hrwa.sumincometax.action.ExportApplyAction();
		context.put("exportApplyAction", bean);
		bean.setModel(getBmModel());
		bean.setDataManager(getBmModelModelDataManager());
		bean.setBillForm(getBillForm());
		bean.setExceptionHandler(getExceptionHandler());
		setBeanFacotryIfBeanFacatoryAware(bean);
		invokeInitializingBean(bean);
		return bean;
	}

}
