package org.generationcp.ibpworkbench.actions;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.comp.common.ConfirmDialog;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@Configurable
public class RestoreIBDBSaveAction implements ConfirmDialog.Listener {
	private static final Logger LOG = LoggerFactory.getLogger(RestoreIBDBSaveAction.class);
	private Window sourceWindow;
	private Select select;
	private Table table;
	
	@Autowired
    private WorkbenchDataManager workbenchDataManager;
	
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
	public RestoreIBDBSaveAction(Select select,Table table,Window sourceWindow) {
		this.select = select;
		this.table = table;
		this.sourceWindow = sourceWindow;
	}
	
	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed()) {
			LOG.debug("onClick > do Restore IBDB");
			
			//sourceWindow = event.getButton().getWindow();

			try {
				ProjectBackup pb = ((BeanItem<ProjectBackup>) table.getItem(table.getValue())).getBean();
				
				MessageNotifier.showTrayNotification(sourceWindow.getParent(),"Restore in progress...","");
				
				//TODO do restore here
				LOG.debug("TODO: do restore here");
				
				//workbenchDataManager.
				
				MessageNotifier.showMessage(sourceWindow.getParent(),"Backup Restoration Complete","");
				
				sourceWindow.getParent().removeWindow(sourceWindow);
				
				LOG.debug("selected backup: " + pb.getProjectBackupId());
			
			} catch(Exception e) {
				sourceWindow.showNotification("No project backup is selected");
			}		
		}
	}

}
