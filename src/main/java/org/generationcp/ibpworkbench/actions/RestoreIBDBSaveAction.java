package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
    public static final String BACKUP_FILE_STRING_PATTERN = "ibdbv2_([a-zA-Z]*)_\\d+_local_\\d+_\\d+_\\d+_(.*).sql";
    public static final Pattern BACKUP_FILE_PATTERN = Pattern.compile(RestoreIBDBSaveAction.BACKUP_FILE_STRING_PATTERN);

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

    private static final String BACKUP_DIR = "temp";

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

            File currentDbBackupFile = null;
            try {


                File restoreFile = file;

                if (!this.isUpload())
                    restoreFile = new File(pb.getBackupPath());

                Matcher matcher = BACKUP_FILE_PATTERN.matcher(restoreFile.getName());
                                if (matcher.matches()) {
                                    String cropName = matcher.group(1);
                                    if (!cropName.equals(sessionData.getLastOpenedProject().getCropType().getCropName())) {
                                        MessageNotifier.showError(sourceWindow, messageSource.getMessage(Message.ERROR_UPLOAD), "Invalid backup file. Selected backup file is for crop " + cropName);
                                        LOG.error("Invalid backup file provided during restore : wrong crop type");
                                        return;
                                    }
                                }

                toolUtil.closeAllNativeTools();
                currentDbBackupFile = mysqlUtil.createCurrentDbBackupFile(project.getLocalDbName());




                //drop schema version
                //we need the schema version inserted from the backup file, not from the previous upgrade
                mysqlUtil.dropSchemaVersion(project.getLocalDbName());
                // restore the database
                mysqlUtil.restoreDatabase(project.getLocalDbName(),restoreFile,currentDbBackupFile);
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


            }  catch(IOException e) {
            	LOG.error("Error during restore", e);
            	//need to restore original state 
            	try {
            		mysqlUtil.restoreOriginalState(project.getLocalDbName(), currentDbBackupFile);
            	} catch(Exception e2) {
            		LOG.error("Error during restore", e2);
            	}            	
            	MessageNotifier.showError(sourceWindow,messageSource.getMessage(Message.ERROR_UPLOAD), e.getMessage());
            } catch(Exception e) {
            	LOG.error("Error during restore", e);
            	//need to restore original state 
            	try {
            		mysqlUtil.restoreOriginalState(project.getLocalDbName(), currentDbBackupFile);
            	} catch(Exception e2) {
            		LOG.error("Error during restore", e2);
            	} 
                MessageNotifier.showError(sourceWindow, messageSource.getMessage(Message.ERROR_UPLOAD), "An error occurred during restoration");
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
        
        StringBuilder sb = new StringBuilder();
        if (new File(saveDir.getAbsolutePath() + "/" + fileName).exists()) {
        	//sb.append(fileName.substring(0, fileName.lastIndexOf(".")) + "_1.sql");
        	for (int x = 1; x < 10000;x++) {
        		String temp = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + x + ".sql";
        		if (!new File(saveDir.getAbsolutePath() + "/" + temp).exists()){
        			sb.append(fileName.substring(0, fileName.lastIndexOf(".")));
                	sb.append("_" + x + ".sql");
        			break;
        		}
        	}
        }else{
        	sb.append(fileName);
        }
        	
        this.file = new File(saveDir, sb.toString());
        return this.file;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setIsUpload(boolean value) {
        isUpload = value;
    }
}
