package org.generationcp.ibpworkbench.ui.recovery;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.RestoreIBDBSaveAction;
import org.generationcp.ibpworkbench.ui.common.UploadField;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Validator;
import com.vaadin.ui.Button;

@Configurable
public class RestoreButtonClickListener implements Button.ClickListener {
	
    private static final Logger LOG = LoggerFactory.getLogger(RestoreButtonClickListener.class);

    @Autowired
	private SimpleResourceBundleMessageSource messageSource;
    
	private final Project project;
	private final RestoreIBDBSaveAction restoreAction;
	private final UploadField uploadField;

	public RestoreButtonClickListener(final Project project, final RestoreIBDBSaveAction restoreAction, final UploadField uploadField) {
		this.project = project;
		this.restoreAction = restoreAction;
		this.uploadField = uploadField;
	}

	@Override
	public void buttonClick(final Button.ClickEvent clickEvent) {
		// validate file upload

		try {
			this.uploadField.validate();

		} catch (final Validator.InvalidValueException e) {
			RestoreButtonClickListener.LOG.error(e.getMessage(), e);
			if (BackupAndRestoreView.NO_FILE.equals(e.getMessage())) {
				MessageNotifier.showError(clickEvent.getComponent().getWindow(),
						this.messageSource.getMessage(Message.ERROR_UPLOAD),
						this.messageSource.getMessage(BackupAndRestoreView.NO_FILE_SELECTED));
				return;
			} else {
				MessageNotifier.showError(clickEvent.getComponent().getWindow(),
						this.messageSource.getMessage(Message.ERROR_UPLOAD),
						this.messageSource.getMessage(Message.ERROR_INVALID_FILE));
				return;
			}
		}

		final String restoreDescMessageFormat = "<b style='color:red'>%s</b><br/><br/>%s";

		final ConfirmDialog dialog = ConfirmDialog.show(clickEvent.getComponent().getWindow(),
				this.messageSource.getMessage(Message.RESTORE_IBDB_WINDOW_CAPTION),
				String.format(restoreDescMessageFormat,
						this.messageSource.getMessage(Message.RESTORE_IBDB_CONFIRM, project.getDatabaseName()),
						this.messageSource.getMessage(Message.RESTORE_BMS_WARN)),
				this.messageSource.getMessage(Message.RESTORE),
				this.messageSource.getMessage(Message.CANCEL), restoreAction);
		dialog.setContentMode(ConfirmDialog.CONTENT_HTML);
	}

	
	public Project getProject() {
		return project;
	}

	
	public RestoreIBDBSaveAction getRestoreAction() {
		return restoreAction;
	}

	
	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
