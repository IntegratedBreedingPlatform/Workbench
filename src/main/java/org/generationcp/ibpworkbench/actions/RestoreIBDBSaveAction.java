
package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Callable;

import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.database.CropDatabaseGenerator;
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

import com.vaadin.ui.Window;

// TODO: internationalize this class
@Configurable
public class RestoreIBDBSaveAction implements ConfirmDialog.Listener, InitializingBean, FileFactory {

	private static final long serialVersionUID = 7008891907589673916L;

	private static final Logger LOG = LoggerFactory.getLogger(RestoreIBDBSaveAction.class);

	private final Window sourceWindow;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private MySQLUtil mysqlUtil;

	@Autowired
	private SessionData sessionData;

	private final Project project;

	private File restoreFile;

	private static final String BACKUP_DIR = "temp";

	// this would be the indicator if there is an error during the restore process
	private boolean hasRestoreError = false;

	public RestoreIBDBSaveAction(final Project project, final Window sourceWindow) {
		this.sourceWindow = sourceWindow;
		this.project = project;
	}

	@Override
	public void onClose(final ConfirmDialog dialog) {
		this.hasRestoreError = false;
		if (dialog.isConfirmed()) {
			RestoreIBDBSaveAction.LOG.debug("onClick > do Restore IBDB");

			try {
				// restore the database
				this.mysqlUtil.restoreDatabase(this.project.getDatabaseName(), this.restoreFile, new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						final CropDatabaseGenerator cropDatabaseGenerator =
								new CropDatabaseGenerator(RestoreIBDBSaveAction.this.sessionData.getLastOpenedProject().getCropType());
						cropDatabaseGenerator.setWorkbenchDataManager(RestoreIBDBSaveAction.this.workbenchDataManager);
						return cropDatabaseGenerator.generateDatabase();
					}
				});

				final Integer userId = this.workbenchDataManager.getLocalIbdbUserId(this.sessionData.getUserData().getUserid(),
								this.project.getProjectId());

				if (userId != null) {
					this.mysqlUtil.updateOwnerships(this.project.getDatabaseName(), userId);
				}

				// the restored database may be old
				// and needs to be upgraded for it to be usable
				final WorkbenchSetting setting = this.workbenchDataManager.getWorkbenchSetting();
				final File schemaDir = new File(setting.getInstallationDirectory(), "database/merged/common-update");
				this.mysqlUtil.upgradeDatabase(this.project.getDatabaseName(), schemaDir);

				MessageNotifier.showMessage(this.sourceWindow, this.messageSource.getMessage(Message.SUCCESS),
						this.messageSource.getMessage(Message.RESTORE_IBDB_COMPLETE));

				// LOG to project activity
				// if there is no user id, it means there is no user data
				if (userId != null) {
					final ProjectActivity projAct =
							new ProjectActivity(null, this.project, "Crop Database Restore", "Restored backup from: "
									+ this.restoreFile.getName(), this.sessionData.getUserData(), new Date());
					this.workbenchDataManager.addProjectActivity(projAct);
				}

				this.hasRestoreError = false;
			} catch (final Exception e) {
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
	public File createFile(final String fileName, final String mimeType) {
		final File saveDir = new File(new File(RestoreIBDBSaveAction.BACKUP_DIR).getAbsolutePath());
		if (!saveDir.exists() || !saveDir.isDirectory()) {
			saveDir.mkdirs();
		}

		final StringBuilder sb = new StringBuilder();
		if (new File(saveDir.getAbsolutePath() + "/" + fileName).exists()) {
			for (int x = 1; x < 10000; x++) {
				final String temp = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + x + ".sql";
				if (!new File(saveDir.getAbsolutePath() + "/" + temp).exists()) {
					sb.append(fileName.substring(0, fileName.lastIndexOf(".")));
					sb.append("_").append(x).append(".sql");
					break;
				}
			}
		} else {
			sb.append(fileName);
		}

		this.restoreFile = new File(saveDir, sb.toString());
		return this.restoreFile;
	}

	// for unit testing to be able to inject test attribute
	public void setMysqlUtil(final MySQLUtil mysqlUtil) {
		this.mysqlUtil = mysqlUtil;
	}

	public void setSessionData(final SessionData sessionData) {
		this.sessionData = sessionData;
	}

	public void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public void setRestoreFile(final File restoreFile) {
		this.restoreFile = restoreFile;
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public boolean isHasRestoreError() {
		return this.hasRestoreError;
	}

}
