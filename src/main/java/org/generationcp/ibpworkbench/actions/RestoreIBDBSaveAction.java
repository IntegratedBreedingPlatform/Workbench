package org.generationcp.ibpworkbench.actions;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.database.IBDBGeneratorLocalDb;
import org.generationcp.ibpworkbench.util.ToolUtil;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.generationcp.middleware.pojos.workbench.WorkbenchSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.easyuploads.FileFactory;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configurable
public class RestoreIBDBSaveAction implements ConfirmDialog.Listener, InitializingBean, FileFactory {
    protected static final Logger LOG = LoggerFactory.getLogger(RestoreIBDBSaveAction.class);

    protected Window sourceWindow;
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


    public RestoreIBDBSaveAction(Project project, Table table, Window sourceWindow) {
        pb = ((BeanItem<ProjectBackup>) table.getItem(table.getValue())).getBean();

        this.sourceWindow = sourceWindow;
        this.project = project;
    }

    public void setProjectBackup(ProjectBackup pb) {
        this.pb = pb;
    }

    public RestoreIBDBSaveAction(Project project, ProjectBackup pb, Window sourceWindow) {
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

            File restoreFile = file;

            try {

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

                // restore the database
                mysqlUtil.restoreDatabase(project.getLocalDbName(), restoreFile, new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        IBDBGeneratorLocalDb generateLocalDB = new IBDBGeneratorLocalDb(sessionData.getLastOpenedProject().getCropType(),sessionData.getLastOpenedProject().getProjectId());
                        return generateLocalDB.generateDatabase();
                    }
                });

                Integer userId = workbenchDataManager.
                        getLocalIbdbUserId(sessionData.getUserData().getUserid(),
                                project.getProjectId());

                mysqlUtil.updateOwnerships(project.getLocalDbName(), userId);

                // the restored database may be old
                // and needs to be upgraded for it to be usable
                WorkbenchSetting setting = workbenchDataManager.getWorkbenchSetting();
                File schemaDir = new File(setting.getInstallationDirectory(), "database/local/common-update");
                mysqlUtil.upgradeDatabase(project.getLocalDbName(), schemaDir);

                MessageNotifier.showMessage(sourceWindow, messageSource.getMessage(Message.SUCCESS), messageSource.getMessage(Message.RESTORE_IBDB_COMPLETE));

                // LOG to project activity
                //TODO: internationalize this
                ProjectActivity projAct = new ProjectActivity(new Integer(project.getProjectId().intValue()), project, "Program Local Database Restore", "Restore performed on " + project.getProjectName(), sessionData.getUserData(), new Date());
                workbenchDataManager.addProjectActivity(projAct);


            } catch(Exception e) {
                MessageNotifier.showError(sourceWindow, "Cannot perform restore operation", e.getMessage());
            }


        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        mysqlUtil.setBackupDir(BACKUP_DIR);
    }

    @Override
    public File createFile(String fileName, String mimeType) {
        File saveDir = new File(new File(BACKUP_DIR).getAbsolutePath());
        if (!saveDir.exists() || !saveDir.isDirectory()) {
            saveDir.mkdirs();
        }

        StringBuilder sb = new StringBuilder();
        if (new File(saveDir.getAbsolutePath() + "/" + fileName).exists()) {
            for (int x = 1; x < 10000; x++) {
                String temp = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + x + ".sql";
                if (!new File(saveDir.getAbsolutePath() + "/" + temp).exists()) {
                    sb.append(fileName.substring(0, fileName.lastIndexOf(".")));
                    sb.append("_" + x + ".sql");
                    break;
                }
            }
        } else {
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
