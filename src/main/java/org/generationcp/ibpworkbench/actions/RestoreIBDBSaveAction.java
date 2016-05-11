
package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.database.CropDatabaseGenerator;
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

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

@Configurable
public class RestoreIBDBSaveAction implements ConfirmDialog.Listener, InitializingBean, FileFactory {

	/**
	 *
	 */
	private static final long serialVersionUID = 7008891907589673916L;

	private static final Logger LOG = LoggerFactory.getLogger(RestoreIBDBSaveAction.class);

	protected Window sourceWindow;
	private ProjectBackup pb;

	public static final String BACKUP_FILE_STRING_PATTERN = "ibdbv2_([a-zA-Z]*)_merged_\\d+_\\d+_\\d+_(.*).sql";
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

	private final Project project;

	private File file;

	private static final String BACKUP_DIR = "temp";

	// this would be the indicator if there is an error during the restore process
	private boolean hasRestoreError = false;

	public RestoreIBDBSaveAction(Project project, Table table, Window sourceWindow) {
		this.pb = ((BeanItem<ProjectBackup>) table.getItem(table.getValue())).getBean();

		this.sourceWindow = sourceWindow;
		this.project = project;
	}

	public RestoreIBDBSaveAction(Project project, ProjectBackup pb, Window sourceWindow) {
		this.pb = pb;
		this.sourceWindow = sourceWindow;
		this.project = project;
	}

	public void setProjectBackup(ProjectBackup pb) {
		this.pb = pb;
	}

	public void setSourceWindow(Window sourceWindow) {
		this.sourceWindow = sourceWindow;
	}

	@Override
	public void onClose(ConfirmDialog dialog) {
		this.hasRestoreError = false;
		if (this.pb != null) {
			RestoreIBDBSaveAction.LOG.debug("selected backup: " + this.pb.getProjectBackupId());
		}

		if (dialog.isConfirmed()) {
			RestoreIBDBSaveAction.LOG.debug("onClick > do Restore IBDB");

			File restoreFile = this.file;

			try {
				Matcher matcher = RestoreIBDBSaveAction.BACKUP_FILE_PATTERN.matcher(restoreFile.getName());
				if (matcher.matches()) {
					String cropName = matcher.group(1);
					if (!cropName.equals(this.sessionData.getLastOpenedProject().getCropType().getCropName())) {
						MessageNotifier.showError(this.sourceWindow, this.messageSource.getMessage(Message.ERROR_UPLOAD),
								"Invalid backup file. Selected backup file is for crop " + cropName);
						RestoreIBDBSaveAction.LOG.error("Invalid backup file provided during restore : wrong crop type");
						return;
					}
				}
				if (this.toolUtil != null) {
					this.toolUtil.closeAllNativeTools();
				}

				// restore the database
				this.mysqlUtil.restoreDatabase(this.project.getDatabaseName(), restoreFile, new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						CropDatabaseGenerator cropDatabaseGenerator =
								new CropDatabaseGenerator(RestoreIBDBSaveAction.this.sessionData.getLastOpenedProject().getCropType());
						cropDatabaseGenerator.setWorkbenchDataManager(RestoreIBDBSaveAction.this.workbenchDataManager);
						return cropDatabaseGenerator.generateDatabase();
					}
				});

				Integer userId =
						this.workbenchDataManager.getLocalIbdbUserId(this.sessionData.getUserData().getUserid(),
								this.project.getProjectId());

				if (userId != null) {
					this.mysqlUtil.updateOwnerships(this.project.getDatabaseName(), userId);
				}

				// the restored database may be old
				// and needs to be upgraded for it to be usable
				WorkbenchSetting setting = this.workbenchDataManager.getWorkbenchSetting();
				File schemaDir = new File(setting.getInstallationDirectory(), "database/merged/common-update");
				this.mysqlUtil.upgradeDatabase(this.project.getDatabaseName(), schemaDir);

				MessageNotifier.showMessage(this.sourceWindow, this.messageSource.getMessage(Message.SUCCESS),
						this.messageSource.getMessage(Message.RESTORE_IBDB_COMPLETE));

				// LOG to project activity
				// TODO: internationalize this
				// if there is no user id, it means there is no user data
				if (userId != null) {
					ProjectActivity projAct =
							new ProjectActivity(null, this.project, "Crop Database Restore", "Restored backup from: "
									+ restoreFile.getName(), this.sessionData.getUserData(), new Date());
					this.workbenchDataManager.addProjectActivity(projAct);
				}

				this.hasRestoreError = false;
			} catch (Exception e) {
				RestoreIBDBSaveAction.LOG.error(e.getMessage(), e);
				MessageNotifier.showError(this.sourceWindow, "Error performing restore operation", e.getMessage());
				this.hasRestoreError = true;
			}

		} else {
			this.hasRestoreError = true;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.mysqlUtil.setBackupDir(RestoreIBDBSaveAction.BACKUP_DIR);
	}

	@Override
	public File createFile(String fileName, String mimeType) {
		File saveDir = new File(new File(RestoreIBDBSaveAction.BACKUP_DIR).getAbsolutePath());
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

	// for unit testing to be able to inject test attribute
	public void setMysqlUtil(MySQLUtil mysqlUtil) {
		this.mysqlUtil = mysqlUtil;
	}

	public void setSessionData(SessionData sessionData) {
		this.sessionData = sessionData;
	}

	public void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public boolean isHasRestoreError() {
		return this.hasRestoreError;
	}

}
