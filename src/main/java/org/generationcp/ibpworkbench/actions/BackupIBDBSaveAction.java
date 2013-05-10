package org.generationcp.ibpworkbench.actions;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.comp.common.ConfirmDialog;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Window;

@Configurable
public class BackupIBDBSaveAction implements ConfirmDialog.Listener, InitializingBean {
	private static final Logger LOG = LoggerFactory.getLogger(BackupIBDBSaveAction.class);

	private static final String BACKUP_DIR = "backup";
	
	private Window sourceWindow;
	
	@Autowired
    private WorkbenchDataManager workbenchDataManager;
	
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

	private ProjectBackup projectBackup;

	@Autowired
	private MySQLUtil mysqlUtil;

	private Project selectedProject;

	public BackupIBDBSaveAction(Project project, Window window) {
    	this.sourceWindow = window;
    	this.selectedProject = project;
    	// for now, manually init MySQLUtil
    }


	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed()) {
			ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            String contextPath = request.getContextPath();
            String url = contextPath + "/controller/backupIBDB?projectId=" + selectedProject.getProjectId() + "&localDbName=" + selectedProject.getLocalDbName();

            sourceWindow.getApplication().getMainWindow().open(new ExternalResource(url));
            
			/*
			LOG.debug("onClick > do save backup");
			LOG.debug("Current ProjectID: " + selectedProject.getProjectId());
			
			projectBackup = new ProjectBackup();
	        projectBackup.setProjectId(selectedProject.getProjectId());
	        projectBackup.setBackupTime(Calendar.getInstance().getTime());
			
	        try {
        		// Attempt backup, store absolutepath to projectBackup bean
				projectBackup.setBackupPath(mysqlUtil.backupDatabase(selectedProject.getLocalDbName()).getAbsolutePath());
				
				// save result to DB
				workbenchDataManager.saveOrUpdateProjectBackup(projectBackup);
		
				MessageNotifier.showMessage(sourceWindow,"Success!","A backup for " + selectedProject.getProjectName() + " has been created");
				
        	} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MiddlewareQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				MessageNotifier.showError(sourceWindow, "Error saving to database",e.getMessage());
			}
			*/
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
    	File saveDir = new File(BACKUP_DIR);
    	if (!saveDir.exists() || !saveDir.isDirectory())
    		saveDir.mkdirs();
    	
    	mysqlUtil.setBackupDir(BACKUP_DIR);
    	
    	LOG.debug("dumppath: " + new File(mysqlUtil.getMysqlDumpPath()).getAbsolutePath() );
	}
}
