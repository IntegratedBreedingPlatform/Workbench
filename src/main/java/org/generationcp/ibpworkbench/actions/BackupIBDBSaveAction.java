package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.util.StringUtil;
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
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Select;
import com.vaadin.ui.Window;

@Configurable
public class BackupIBDBSaveAction implements ConfirmDialog.Listener {
	private static final Logger LOG = LoggerFactory.getLogger(BackupIBDBSaveAction.class);

	private static final String BACKUP_DIR = "backup";
	
	private Window sourceWindow;
	private Window parentWindow;

	
	@Autowired
    private WorkbenchDataManager workbenchDataManager;
	
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

	private ProjectBackup projectBackup;

	//TODO AUTOWIRE this
	private MySQLUtil dbUtil;

	private Project selectedProject;

	public BackupIBDBSaveAction(Project project, Window window) {
    	this.sourceWindow = sourceWindow;
    	this.selectedProject = project;
    	// for now, manually init MySQLUtil
    	initDB();	
    }

	private void initDB() {
    	dbUtil = new MySQLUtil();
    	
    	File saveDir = new File(BACKUP_DIR);
    	if (!saveDir.exists() || !saveDir.isDirectory())
    		saveDir.mkdirs();
    	
    	dbUtil.setMysqlDumpPath("C:/IBWorkflowSystem/infrastructure/mysql/bin/mysqldump.exe");
    	dbUtil.setBackupDir(BACKUP_DIR);
    	dbUtil.setMysqlDriver("com.mysql.jdbc.Driver");
        dbUtil.setMysqlHost("localhost");
        dbUtil.setMysqlPort(13306);
        dbUtil.setUsername("root");
	}

	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed()) {
			LOG.debug("onClick > do save backup");
			LOG.debug("Current ProjectID: " + selectedProject.getProjectId());
			
			//sourceWindow = event.getButton().getWindow();
			parentWindow = sourceWindow.getParent();
			
			//MessageNotifier.showTrayNotification(parentWindow,"Backup in progress...","Creating a backup for " + selectedProject.getProjectName());
			
			projectBackup = new ProjectBackup();
	        projectBackup.setProjectId(selectedProject.getProjectId());
	        projectBackup.setBackupTime(Calendar.getInstance().getTime());
			
	        try {
        		// Attempt backup, store absolutepath to projectBackup bean
				projectBackup.setBackupPath(dbUtil.backupDatabase(selectedProject.getLocalDbName()).getAbsolutePath());
				
				// save result to DB
				workbenchDataManager.saveOrUpdateProjectBackup(projectBackup);
		
				MessageNotifier.showMessage(parentWindow,"Success!","A backup for " + selectedProject.getProjectName() + " has been created");
				
        	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MiddlewareQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				MessageNotifier.showError(parentWindow, "Error saving to database",e.getMessage());
			} finally {
			    parentWindow.removeWindow(sourceWindow);			
			}
		}
	}
}
