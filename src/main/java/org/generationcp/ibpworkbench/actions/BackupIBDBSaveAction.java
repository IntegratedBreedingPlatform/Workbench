package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.ui.common.ConfirmDialog;
import org.generationcp.ibpworkbench.service.BackupIBDBService;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.FileResource;
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

	//@Autowired
	//private MySQLUtil mysqlUtil;
	
	@Autowired
    private BackupIBDBService backupIBDBService;

	private Project selectedProject;

	public BackupIBDBSaveAction(Project project, Window window) {
    	this.sourceWindow = window;
    	this.selectedProject = project;
    	// for now, manually init MySQLUtil
    }


	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed()) {
			LOG.debug("Current ProjectID: " + selectedProject.getProjectId());
			File backupFile;
			try {
				backupFile = backupIBDBService.backupIBDB(selectedProject.getProjectId().toString(),selectedProject.getLocalDbName());
				
				 IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
	                User user = app.getSessionData().getUserData();

	                //TODO: internationalize this
	                ProjectActivity projAct = new ProjectActivity(new Integer(selectedProject.getProjectId().intValue()), selectedProject, "backup action", "backup performed on " + selectedProject.getProjectName(), user, new Date());

	                workbenchDataManager.addProjectActivity(projAct);
				
				
				FileResource fr = new FileResource(backupFile, sourceWindow.getApplication()) {
					private static final long serialVersionUID = 765143030552676513L;
					@Override
					public DownloadStream getStream() {
						DownloadStream ds;
						try {
							ds = new DownloadStream(new FileInputStream(
									getSourceFile()), getMIMEType(), getFilename());
							
							ds.setParameter("Content-Disposition", "attachment; filename="+getFilename());
							ds.setCacheTime(getCacheTime());
							return ds;
							
						} catch (FileNotFoundException e) {
							// No logging for non-existing files at this level.
							return null;
						}
					}
				};
				
				sourceWindow.getApplication().getMainWindow().open(fr);
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            //HttpServletRequest request = requestAttributes.getRequest();
            //String contextPath = request.getContextPath();
            //String url = contextPath + "/controller/backupIBDB?projectId=" + selectedProject.getProjectId() + "&localDbName=" + selectedProject.getLocalDbName();

            //sourceWindow.getApplication().getMainWindow().open(new ExternalResource(url));
            
			
			
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
		
	}
}
