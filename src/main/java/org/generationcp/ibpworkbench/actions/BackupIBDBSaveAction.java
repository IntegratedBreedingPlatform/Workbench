package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.IOException;
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

	private static final String BACKUP_DIR = "backup";
	
	private Window sourceWindow;
	private Window parentWindow;
	private Select select;

	
	@Autowired
    private WorkbenchDataManager workbenchDataManager;
	
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

	private ProjectBackup projectBackup;

	//TODO AUTOWIRE this
	private MySQLUtil dbUtil;

	private Project selectedProject;


	public BackupIBDBSaveAction(Select select,Window sourceWindow) {
		this.select = select;
		this.sourceWindow = sourceWindow;
		
    	// for now, manually init MySQLUtil
    	initDB();	
	}
	
    private void initDB() {
    	dbUtil = new MySQLUtil();
    	dbUtil.setMysqlDumpPath("C:/IBWorkflowSystem/infrastructure/mysql/bin/mysqldump.exe");
    	dbUtil.setBackupDir(BACKUP_DIR);
    	dbUtil.setMysqlDriver("com.mysql.jdbc.Driver");
        dbUtil.setMysqlHost("localhost");
        dbUtil.setMysqlPort(13306);
        dbUtil.setUsername("root");
	}

	@Override
	public void run() {
			try {
				//File backupFile = dbUtil.backupDatabase(selectedProject.getLocalDbName());
			
				//projectBackup.setBackupPath(backupFile.getAbsolutePath());
				
				workbenchDataManager.saveOrUpdateProjectBackup(projectBackup);
				
				parentWindow.showNotification("Backup Saved!");
			} catch (Exception e) {
				LOG.error(e.getMessage());
				MessageNotifier.showError(parentWindow,"An error occured while creating the backup",e.getLocalizedMessage());
			}
		
			
	}

	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed()) {
			LOG.debug("onClick > do save backup");
			
			selectedProject = ((BeanItem<Project>)select.getItem(select.getValue())).getBean();
			
			LOG.debug("Selected ProjectID: " + selectedProject.getProjectId());
			
			//sourceWindow = event.getButton().getWindow();
			parentWindow = sourceWindow.getParent();
			
			//sourceWindow.showNotification();
			
			MessageNotifier.showTrayNotification(parentWindow,"Backup in progress...","Creating a backup for " + selectedProject.getProjectName());
			
			projectBackup = new ProjectBackup();
	        projectBackup.setProjectId(selectedProject.getProjectId());
	        projectBackup.setBackupPath("target/resource");
	        projectBackup.setBackupTime(Calendar.getInstance().getTime());
			
	        ExecutorService ex = Executors.newSingleThreadExecutor();
			ex.execute(this);
			ex.shutdown();
			
			MessageNotifier.showMessage(parentWindow,"Success!","A backup for " + selectedProject.getProjectName() + " has been created");
	            
	        parentWindow.removeWindow(sourceWindow);
		}
	}	
}
