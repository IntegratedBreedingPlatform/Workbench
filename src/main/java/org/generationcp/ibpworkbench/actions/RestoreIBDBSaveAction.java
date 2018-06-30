package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import org.generationcp.commons.util.InstallationDirectoryUtil;
import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.database.CropDatabaseGenerator;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.easyuploads.FileFactory;

import com.vaadin.ui.Window;

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
	private org.generationcp.commons.spring.util.ContextUtil contextUtil;

	@Autowired
	private ProgramService programService;
	
	private InstallationDirectoryUtil installationDirectoryUtil = new InstallationDirectoryUtil();
	
	private final Project project;

	private File restoreFile;

	private static final String BACKUP_DIR = "temp";
	private static final String SQL_FILE_EXTENSION = ".sql";
	private static final String UNDERSCORE = "_";

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
				// Store the crop type of current program before restoring
				final CropType cropType = this.project.getCropType();
				
				// Restore the database
				this.mysqlUtil.restoreDatabase(this.project.getDatabaseName(), this.restoreFile, new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						final CropDatabaseGenerator cropDatabaseGenerator =
								new CropDatabaseGenerator(contextUtil.getProjectInContext().getCropType());
						return cropDatabaseGenerator.generateDatabase();
					}
				});

				// Show success message
				MessageNotifier.showMessage(this.sourceWindow, this.messageSource.getMessage(Message.SUCCESS),
						this.messageSource.getMessage(Message.RESTORE_IBDB_COMPLETE));

				// Set current user as owner of restored germplasm lists
				final Integer userId =
						this.workbenchDataManager.getLocalIbdbUserId(contextUtil.getCurrentWorkbenchUserId(), this.project.getProjectId());
				this.updateGermplasmListOwnership(userId);

				// Add current user and users with SUPERADMIN role as members of all restored programs
				final List<Project> restoredPrograms = this.workbenchDataManager.getProjectsByCrop(cropType);
				this.addSuperAdminAndCurrentUserAsMembersOfRestoredPrograms(restoredPrograms);
				
				// Remove directories for old programs and generate new folders for programs of restored backup file
				this.installationDirectoryUtil.resetWorkspaceDirectoryForCrop(cropType, restoredPrograms);

				// Log a record in ProjectActivity
				if (userId != null) {
					this.contextUtil.logProgramActivity(this.messageSource.getMessage(Message.CROP_DATABASE_RESTORE),
							this.messageSource.getMessage(Message.RESTORED_BACKUP_FROM) + " " + this.restoreFile.getName());
				}

				this.hasRestoreError = false;
			} catch (final Exception e) {
				RestoreIBDBSaveAction.LOG.error(e.getMessage(), e);
				MessageNotifier
						.showError(this.sourceWindow, this.messageSource.getMessage(Message.RESTORE_OPERATION_ERROR), e.getMessage());
				this.hasRestoreError = true;
			}
		} else {
			this.hasRestoreError = true;
		}
	}

	void updateGermplasmListOwnership(final Integer userId) throws IOException, SQLException {
		if (userId != null) {
			this.mysqlUtil.updateOwnerships(this.project.getDatabaseName(), userId);
		}
	}

	/*
	 * Call ProgramService to add SUPERADMIN user(s) and current user (if he is not a super admin user) as program members
	 */
	void addSuperAdminAndCurrentUserAsMembersOfRestoredPrograms(final List<Project> projects) {

		final WorkbenchUser currentUser = contextUtil.getCurrentWorkbenchUser();
		final HashSet<WorkbenchUser> users = new HashSet<>();
		users.add(currentUser);

		for (final Project proj : projects) {
			// The SUPERADMIN user(s) is being added in ProgramService
			this.programService.saveProgramMembers(proj, users);
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
				final String temp = fileName.substring(0, fileName.lastIndexOf(".")) + RestoreIBDBSaveAction.UNDERSCORE + x
						+ RestoreIBDBSaveAction.SQL_FILE_EXTENSION;
				if (!new File(saveDir.getAbsolutePath() + "/" + temp).exists()) {
					sb.append(fileName.substring(0, fileName.lastIndexOf(".")));
					sb.append(RestoreIBDBSaveAction.UNDERSCORE).append(x).append(RestoreIBDBSaveAction.SQL_FILE_EXTENSION);
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

	public void setContextUtil(final org.generationcp.commons.spring.util.ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	public void setWorkbenchDataManager(final WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public void setRestoreFile(final File restoreFile) {
		this.restoreFile = restoreFile;
	}

	public void setProgramService(final ProgramService programService) {
		this.programService = programService;
	}

	public void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public boolean isHasRestoreError() {
		return this.hasRestoreError;
	}

	
	public Project getProject() {
		return project;
	}

	
	public void setInstallationDirectoryUtil(InstallationDirectoryUtil installationDirectoryUtil) {
		this.installationDirectoryUtil = installationDirectoryUtil;
	}

}
