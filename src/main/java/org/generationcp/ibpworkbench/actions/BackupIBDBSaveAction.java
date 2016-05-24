
package org.generationcp.ibpworkbench.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;

import org.generationcp.commons.util.MySQLUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
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

	private static final long serialVersionUID = 3502237968419196524L;

	private static final Logger LOG = LoggerFactory.getLogger(BackupIBDBSaveAction.class);


	private final Window sourceWindow;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private MySQLUtil mysqlUtil;

	private final Project selectedProject;

	public BackupIBDBSaveAction(final Project project, final Window window) {
		this.sourceWindow = window;
		this.selectedProject = project;
		// for now, manually init MySQLUtil
	}

	@Override
	public void onClose(final ConfirmDialog dialog) {
		if (dialog.isConfirmed()) {
			this.doAction();
		}
	}

	public void doAction() {
		BackupIBDBSaveAction.LOG.debug("Current ProjectID: " + this.selectedProject.getProjectId());
		final File backupFile;
		try {
			// TODO review this for merged DB scheme. For now passing in the pointer to the merged db if backup action is executed..
			backupFile = this.mysqlUtil.backupDatabase(this.selectedProject.getDatabaseName(),
					this.mysqlUtil.getBackupFilename(this.selectedProject.getDatabaseName(), ".sql", "temp"), true);

			// TODO: remove test code
			final IBPWorkbenchApplication app = IBPWorkbenchApplication.get();

			final ProjectActivity projAct =
					new ProjectActivity(null, this.selectedProject, this.messageSource.getMessage(Message.CROP_DATABASE_BACKUP),
							this.messageSource.getMessage(Message.BACKUP_PERFORMED_ON) + " " + this.selectedProject.getDatabaseName(),
							app.getSessionData().getUserData(), new Date());

			this.workbenchDataManager.addProjectActivity(projAct);

			MessageNotifier.showMessage(this.sourceWindow, this.messageSource.getMessage(Message.SUCCESS),
					this.messageSource.getMessage(Message.BACKUP_IBDB_COMPLETE));

			final FileResource fr = new FileResource(backupFile, this.sourceWindow.getApplication()) {

				private static final long serialVersionUID = 765143030552676513L;

				@Override
				public DownloadStream getStream() {
					try {
						final DownloadStream ds = new DownloadStream(new FileInputStream(this.getSourceFile()), this.getMIMEType(), this
								.getFilename());

						ds.setParameter("Content-Disposition", "attachment; filename=" + this.getFilename());
						ds.setCacheTime(this.getCacheTime());
						return ds;

					} catch (final FileNotFoundException e) {
						LOG.warn(e.getMessage(),e);
						return null;
					}
				}
			};

			this.sourceWindow.getApplication().getMainWindow().open(fr);

		} catch (final Exception e) {
			LOG.error(e.getMessage(),e);
			MessageNotifier.showMessage(this.sourceWindow, this.messageSource.getMessage(Message.BACKUP_IBDB_CANNOT_PERFORM_OPERATION),
					e.getMessage());

		}
	}


	/**
	 * afterPropertiesSet() is called after Aspect4J weaves spring objects when this class is instantiated since this class is
	 * a @configurable that implements InitializingBean. Since we do not have any need for additional initialization after the weaving, this
	 * method remains unimplemented.
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// No state or initial values are required to be initialized for this layout
	}

	@Override
	public void buttonClick(final Button.ClickEvent clickEvent) {
		this.doAction();
	}
}
