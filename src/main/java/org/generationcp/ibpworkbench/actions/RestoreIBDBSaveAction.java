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
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
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
import org.vaadin.easyuploads.FileFactory;

@Configurable
public class RestoreIBDBSaveAction implements ConfirmDialog.Listener, InitializingBean, FileFactory {
	protected static final Logger LOG = LoggerFactory.getLogger(RestoreIBDBSaveAction.class);

	protected Window sourceWindow;
	//private Select select;
    private ProjectBackup pb;
	
	
	@Autowired
    private WorkbenchDataManager workbenchDataManager;
	
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private MySQLUtil mysqlUtil;

    @Autowired
    private ToolUtil toolUtil;

    @Autowired
    private SessionData sessionData;

    private Project project;

	private File file;

    private static final String BACKUP_DIR = "backup";

    private boolean isUpload = false;


    public RestoreIBDBSaveAction(Project project, Table table,Window sourceWindow) {
        pb = ((BeanItem<ProjectBackup>) table.getItem(table.getValue())).getBean();

		this.sourceWindow = sourceWindow;
		this.project = project;
	}

    public void setProjectBackup(ProjectBackup pb) {
        this.pb = pb;
    }

    public RestoreIBDBSaveAction(Project project, ProjectBackup pb,Window sourceWindow) {
        //this.select = select;
        this.pb = pb;
        this.sourceWindow = sourceWindow;
        this.project = project;
    }

    public void setSourceWindow(Window sourceWindow) {
        this.sourceWindow = sourceWindow;
    }

	@Override
	public void onClose(ConfirmDialog dialog) {

        if (pb != null)
            LOG.debug("selected backup: " + pb.getProjectBackupId());

        if (dialog.isConfirmed()) {
            LOG.debug("onClick > do Restore IBDB");

            try {
                toolUtil.closeAllNativeTools();

                File restoreFile = file;

                if (!this.isUpload())
                    restoreFile = new File(pb.getBackupPath());

                // restore the database
                mysqlUtil.restoreDatabase(project.getLocalDbName(),restoreFile);
                Integer userId = workbenchDataManager.
                		getLocalIbdbUserId(sessionData.getUserData().getUserid(),
                				project.getProjectId());
                mysqlUtil.updateOwnerships(project.getLocalDbName(),userId);
                // the restored database may be old
                // and needs to be upgraded for it to be usable
                WorkbenchSetting setting = workbenchDataManager.getWorkbenchSetting();
                File schemaDir = new File(setting.getInstallationDirectory(), "database/local/common-update");
                mysqlUtil.upgradeDatabase(project.getLocalDbName(), schemaDir);
                
                //GCP-7958 - since users and persons tables are no longer restored, the line below is no longer needed - uncomment if it's no longer the case
                //new SaveUsersInProjectAfterRestoreAction(project).doAction(null);

                MessageNotifier.showMessage(sourceWindow, messageSource.getMessage(Message.SUCCESS), messageSource.getMessage(Message.RESTORE_IBDB_COMPLETE));

                // LOG to project activity
                //TODO: internationalize this
                ProjectActivity projAct = new ProjectActivity(new Integer(project.getProjectId().intValue()), project, "Program Local Database Restore", "Restore performed on " + project.getProjectName(),sessionData.getUserData(), new Date());
                workbenchDataManager.addProjectActivity(projAct);


            }  catch(IOException ex) {
                MessageNotifier.showError(sourceWindow,messageSource.getMessage(Message.ERROR_UPLOAD), ex.getMessage());
            } catch(Exception e) {
                MessageNotifier.showError(sourceWindow, messageSource.getMessage(Message.ERROR_UPLOAD), "No project backup is selected");
            }



		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		mysqlUtil.setBackupDir(BACKUP_DIR);
	}

    @Override
    public File createFile(String fileName, String mimeType) {
        File saveDir = new File(BACKUP_DIR);
        if (!saveDir.exists() || !saveDir.isDirectory()) {
            saveDir.mkdirs();
        }
        
        this.file = new File(saveDir, fileName);
        return this.file;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setIsUpload(boolean value) {
        isUpload = value;
    }
}
