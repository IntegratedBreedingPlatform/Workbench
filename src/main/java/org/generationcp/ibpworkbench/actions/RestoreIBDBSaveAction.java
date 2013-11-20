package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.common.ConfirmDialog;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
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

import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Table;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.Window;

@Configurable
public class RestoreIBDBSaveAction implements ConfirmDialog.Listener, Receiver, SucceededListener, InitializingBean {
	private static final Logger LOG = LoggerFactory.getLogger(RestoreIBDBSaveAction.class);
	
	private Window sourceWindow;
	//private Select select;
	private Table table;
	
	
	@Autowired
    private WorkbenchDataManager workbenchDataManager;
	
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
    @Autowired
    private MySQLUtil mysqlUtil;
	
    private Project project;

	private File file;
	private static final String BACKUP_DIR = "backup";
	
	public RestoreIBDBSaveAction(Project project, Table table,Window sourceWindow) {
		//this.select = select;
		this.table = table;
		this.sourceWindow = sourceWindow;
		this.project = project;
	}
		
	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed()) {
			LOG.debug("onClick > do Restore IBDB");
			
			//sourceWindow = event.getButton().getWindow();

			try {
				ProjectBackup pb = ((BeanItem<ProjectBackup>) table.getItem(table.getValue())).getBean();
			
				mysqlUtil.restoreDatabase(project.getLocalDbName(),new File(pb.getBackupPath()));
				
				MessageNotifier.showMessage(sourceWindow.getParent(),messageSource.getMessage(Message.RESTORE_IBDB_COMPLETE),"");
				
				sourceWindow.getParent().removeWindow(sourceWindow);
				
				LOG.debug("selected backup: " + pb.getProjectBackupId());
			
			} catch(Exception e) {
				sourceWindow.showNotification("No project backup is selected");
			}		
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		mysqlUtil.setBackupDir(BACKUP_DIR);
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		FileResource fileResource = new FileResource(file,sourceWindow.getApplication());
		File tmpFile = fileResource.getSourceFile();
		
		// validate file
		
		if (!getExtension(tmpFile).toLowerCase().contains("sql")) {
			MessageNotifier.showError(sourceWindow, messageSource.getMessage(Message.ERROR_UPLOAD),messageSource.getMessage(Message.ERROR_INVALID_FILE));
		
			tmpFile.delete();
			
			return;
		}
			
		File destFile = new File(BACKUP_DIR + File.separator + tmpFile.getName());
		// write to backup databases if not exists else update
		ProjectBackup pb;
		try {
			FileUtils.copyFile(tmpFile,destFile);
			pb = workbenchDataManager.saveOrUpdateProjectBackup(new ProjectBackup(null, project.getProjectId(),new Date(),destFile.getAbsolutePath()));
		
			mysqlUtil.restoreDatabase(project.getLocalDbName(),new File(pb.getBackupPath()));			
		
			 IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
             User user = app.getSessionData().getUserData();

             //TODO: internationalize this
             ProjectActivity projAct = new ProjectActivity(new Integer(project.getProjectId().intValue()), project, "restore action", "Restore performed on " + project.getProjectName(), user, new Date());

             workbenchDataManager.addProjectActivity(projAct);
		
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage());
			
			MessageNotifier.showError(sourceWindow,messageSource.getMessage(Message.ERROR_UPLOAD),e.getMessage());
		} catch (IOException e) {
			LOG.error(e.getMessage());
			MessageNotifier.showError(sourceWindow,messageSource.getMessage(Message.ERROR_UPLOAD),e.getMessage());
		} catch (SQLException e) {
			LOG.error(e.getMessage());
			MessageNotifier.showError(sourceWindow, messageSource.getMessage(Message.ERROR_UPLOAD),e.getMessage());
		}
		

		// notify user of success
		MessageNotifier.showMessage(sourceWindow.getParent(),messageSource.getMessage(Message.RESTORE_IBDB_COMPLETE),"");
		
		// close the window
		sourceWindow.getParent().removeWindow(sourceWindow);
	}

	
	/**
	 * Will return null if error
	 */
	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {
	    
		FileOutputStream fos = null;
		
		try {
			
			new File("tmp/uploads").mkdirs();	// create the directories if not exist
			
			file = new File("tmp/uploads/" + filename);
			fos = new FileOutputStream(file);
		
		} catch (FileNotFoundException e) {
			LOG.error(e.getLocalizedMessage());
			
			return null;
		}
        
        return fos;
	}
	
	/**
	 * Get filename Extension
	 * @param f
	 * @return
	 */
	private static String getExtension(File f)
	{
	String ext = null;
	String s = f.getName();
	int i = s.lastIndexOf('.');

	if (i > 0 && i < s.length() - 1)
	ext = s.substring(i+1).toLowerCase();

	if(ext == null)
	return "";
	return ext;
	}

}
