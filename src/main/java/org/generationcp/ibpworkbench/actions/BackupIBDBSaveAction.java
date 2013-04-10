package org.generationcp.ibpworkbench.actions;

import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.comp.common.ConfirmDialog;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Select;
import com.vaadin.ui.Window;

@Configurable
public class BackupIBDBSaveAction implements ConfirmDialog.Listener, Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(BackupIBDBSaveAction.class);
	
	private Window sourceWindow;
	private Window parentWindow;
	private Select select;
	
	@Autowired
    private WorkbenchDataManager workbenchDataManager;
	
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

	private ProjectBackup projectBackup;


	public BackupIBDBSaveAction(Select select,Window sourceWindow) {
		this.select = select;
		this.sourceWindow = sourceWindow;
	}
	
	@Override
	public void run() {
		try {
			workbenchDataManager.saveOrUpdateProjectBackup(projectBackup);
			parentWindow.showNotification("Backup Saved!");
			
			MySQLUtil mysqlutil = new MySQLUtil();
			
			
		} catch (MiddlewareQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed()) {
			LOG.debug("onClick > do save backup");
			
			Project p = ((BeanItem<Project>)select.getItem(select.getValue())).getBean();
			
			LOG.debug("Selected ProjectID: " + p.getProjectId());
			
			//sourceWindow = event.getButton().getWindow();
			parentWindow = sourceWindow.getParent();
			
			//sourceWindow.showNotification();
			
			MessageNotifier.showTrayNotification(parentWindow,"Backup in progress...","Creating a backup for " + p.getProjectName());
			
			projectBackup = new ProjectBackup();
	        projectBackup.setProjectId(p.getProjectId());
	        projectBackup.setBackupPath("target/resource");
	        projectBackup.setBackupTime(Calendar.getInstance().getTime());
			
	        
	        try {
				workbenchDataManager.saveOrUpdateProjectBackup(projectBackup);
				//parentWindow.showNotification("Backup Saved!");
			
		        MessageNotifier.showMessage(parentWindow,"Success!","A backup for " + p.getProjectName() + " has been created");				
			} catch (MiddlewareQueryException e) {
				e.printStackTrace();
			
		        MessageNotifier.showError(parentWindow,"Error!",e.getMessage());
			}
	        
	        //ExecutorService ex = Executors.newSingleThreadExecutor();
	        
	        //ex.execute(this);
	        //ex.shutdown();
	        	        
	        parentWindow.removeWindow(sourceWindow);
		}
	}	
}
