
package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.service.BackupIBDBService;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.FileResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;

@Configurable
public class BackupIBDBSaveAction implements ConfirmDialog.Listener, Button.ClickListener, InitializingBean {

	/**
	 *
	 */
	private static final long serialVersionUID = 3502237968419196524L;

	private static final Logger LOG = LoggerFactory.getLogger(BackupIBDBSaveAction.class);


	private final Window sourceWindow;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private BackupIBDBService backupIBDBService;

	private final Project selectedProject;

	public BackupIBDBSaveAction(Project project, Window window) {
		this.sourceWindow = window;
		this.selectedProject = project;
		// for now, manually init MySQLUtil
	}

	@Override
	public void onClose(ConfirmDialog dialog) {
		if (dialog.isConfirmed()) {
			this.doAction();
		}
	}

	public void doAction() {
		BackupIBDBSaveAction.LOG.debug("Current ProjectID: " + this.selectedProject.getProjectId());
		File backupFile;
		try {
			// TODO review this for merged DB scheme. For now passing in the pointer to the merged db if backup action is executed..
			backupFile =
					this.backupIBDBService.backupIBDB(this.selectedProject.getProjectId().toString(),
							this.selectedProject.getDatabaseName());

			// TODO: remove test code

			IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
			User user = app.getSessionData().getUserData();

			// TODO: internationalize this
			ProjectActivity projAct =
					new ProjectActivity(new Integer(this.selectedProject.getProjectId().intValue()), this.selectedProject, "backup action",
							"backup performed on " + this.selectedProject.getProjectName(), user, new Date());

			this.workbenchDataManager.addProjectActivity(projAct);

			MessageNotifier.showMessage(this.sourceWindow, this.messageSource.getMessage(Message.SUCCESS),
					this.messageSource.getMessage(Message.BACKUP_IBDB_COMPLETE));

			FileResource fr = new FileResource(backupFile, this.sourceWindow.getApplication()) {

				private static final long serialVersionUID = 765143030552676513L;

				@Override
				public DownloadStream getStream() {
					DownloadStream ds;
					try {
						ds = new DownloadStream(new FileInputStream(this.getSourceFile()), this.getMIMEType(), this.getFilename());

						ds.setParameter("Content-Disposition", "attachment; filename=" + this.getFilename());
						ds.setCacheTime(this.getCacheTime());
						return ds;

					} catch (FileNotFoundException e) {
						LOG.info(e.getMessage(),e);
						// No logging for non-existing files at this level.
						return null;
					}
				}
			};

			this.sourceWindow.getApplication().getMainWindow().open(fr);

		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
			MessageNotifier.showMessage(this.sourceWindow, this.messageSource.getMessage(Message.BACKUP_IBDB_CANNOT_PERFORM_OPERATION),
					e.getMessage());

		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// do nothing
	}

	@Override
	public void buttonClick(Button.ClickEvent clickEvent) {
		this.doAction();
	}
}
