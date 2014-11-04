package org.generationcp.ibpworkbench.ui.dashboard.listener;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreviewPresenter;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.Button;
import com.vaadin.ui.Tree;

public class DeleteConfirmDialogListener implements ConfirmDialog.Listener{
	private static final Logger LOG = LoggerFactory.getLogger(DeleteConfirmDialogListener.class);
	
	private static final long serialVersionUID = 1L;
	
	private NurseryListPreviewPresenter presenter;
	private Tree treeView;
	private Integer finalId;
	private Button.ClickEvent event;
	
	@Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
	public DeleteConfirmDialogListener(NurseryListPreviewPresenter presenter, Tree treeView, Integer finalId, Button.ClickEvent event){
		super();
		this.presenter = presenter;
		this.treeView = treeView;
		this.finalId = finalId;
		this.event = event;
	}

    @Override
    public void onClose(ConfirmDialog dialog) {
        if (dialog.isConfirmed()) {
        	deleteStudy();
        }
    }
    
    protected void deleteStudy(){
    	 try {
             DmsProject parent = (DmsProject) presenter.getStudyNodeParent(finalId);
             presenter.deleteNurseryListFolder(finalId);
             treeView.removeItem(treeView.getValue());
             if (parent.getProjectId().intValue() == NurseryListPreview.ROOT_FOLDER) {
                 treeView.select(NurseryListPreview.MY_STUDIES);
                 presenter.processToolbarButtons(NurseryListPreview.MY_STUDIES);
             } else {
                 treeView.select(parent.getProjectId());
                 presenter.processToolbarButtons(parent.getProjectId());
             }
             treeView.setImmediate(true);
         } catch (Exception e) {
        	 LOG.error(e.getMessage(), e);
             MessageNotifier.showError(event.getComponent().getWindow(),messageSource.getMessage(Message.INVALID_OPERATION), e.getMessage());
         }
    }

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
    
}
