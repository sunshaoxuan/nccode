 package nc.ui.twhr.actions.batch;
 
 import java.awt.event.ActionEvent;

import nc.ui.uif2.actions.batch.BatchSaveAction;
import nc.bs.uif2.validation.IBatchValidationService;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.GlbBatchBillTableModel;
import nc.uif2.annoations.MethodType;
import nc.uif2.annoations.ModelMethod;
import nc.uif2.annoations.ModelType;
import nc.uif2.annoations.ViewMethod;
import nc.uif2.annoations.ViewType;
 
 
 
 
 
 
 
 
 
 
 public class GlbBatchSaveAction
   extends BatchSaveAction
 {
   private static final long serialVersionUID = -7359817260677159564L;
   private GlbBatchBillTableModel model;
   
   private BatchBillTable editor = null;
   
 
   private IBatchValidationService validationService = null;
   
   public GlbBatchSaveAction()
   {
	   super();
   }
   
   public void doAction(ActionEvent e)
     throws Exception
   {
	   if (this.editor.beforeSave())
	       {
	        if ((this.validationService != null) && (this.model.getValidationService() == null))
	          this.model.setValidationService(this.validationService);
	         this.model.save();
	       
	         showSuccessInfo();
	       }
   }
   
   @ViewMethod(viewType=ViewType.BatchBillTable, methodType=MethodType.GETTER)
     public BatchBillTable getEditor() {
        return this.editor;
     }
     
     @ViewMethod(viewType=ViewType.BatchBillTable, methodType=MethodType.SETTER)
     public void setEditor(BatchBillTable editor) {
        this.editor = editor;
     }
     

     
     public GlbBatchBillTableModel getModel() {
		return model;
	}

	public void setModel(GlbBatchBillTableModel model) {
		this.model = model;
	}

	public IBatchValidationService getValidationService() {
    	     return this.validationService;
    	    }
    	    
     public void setValidationService(IBatchValidationService validationService) {
    	     this.validationService = validationService;
    	    }
 }

