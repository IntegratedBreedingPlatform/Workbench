
package org.generationcp.ibpworkbench.ui.recovery;

import java.io.File;

import org.generationcp.commons.help.document.HelpButton;
import org.generationcp.commons.help.document.HelpModule;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.BackupIBDBSaveAction;
import org.generationcp.ibpworkbench.actions.RestoreIBDBSaveAction;
import org.generationcp.ibpworkbench.ui.common.UploadField;
import org.generationcp.ibpworkbench.ui.programmethods.ProgramMethodsView;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Validator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;


//TODO Localise messages in this class
@Configurable
public class BackupAndRestoreView extends CustomComponent implements InitializingBean {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ProgramMethodsView.class);
	public static final String NO_FILE = "NO_FILE";
	public static final String NO_FILE_SELECTED = "NO_FILE_SELECTED";
	private Button backupBtn;
	private UploadField uploadFrm;
	private Button restoreBtn;
	private TabSheet tabSheet;
	private Panel backupPanel;
	private Panel restorePanel;

	@Autowired
	private SessionData sessionData;
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	@Autowired
	private WorkbenchDataManager workbenchDataManager;
	private final Panel root = new Panel();

	public BackupAndRestoreView() {
		this.setCompositionRoot(this.root);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.initializeComponents();
		this.initializeLayout();
	}

	@Override
	public void attach() {
		super.attach();
		this.initializeActions();
	}

	public void initializeComponents() {
		this.backupBtn = new Button(this.messageSource.getMessage("BACKUP_BMS_BUTTON"));
		this.uploadFrm = new UploadField() {

			@Override
			public void uploadFinished(Upload.FinishedEvent event) {
				super.uploadFinished(event);

				BackupAndRestoreView.this.restoreBtn.setEnabled(true);
			}

			@Override
			public void validate() throws Validator.InvalidValueException {
				if (this.getLastFileName() == null) {
					throw new Validator.InvalidValueException(NO_FILE);
				} else if (!this.isValid()) {
					throw new Validator.InvalidValueException("NOT_VALID");
				}
			}

			@Override
			public boolean isValid() {
				return this.getLastFileName() != null && this.getExtension(this.getLastFileName()).toLowerCase().contains("sql");
			}

			private String getExtension(String f) {
				String ext = null;
				int i = f.lastIndexOf('.');

				if (i > 0 && i < f.length() - 1) {
					ext = f.substring(i + 1).toLowerCase();
				}

				if (ext == null) {
					return "";
				}
				return ext;
			}
		};

		this.uploadFrm.setNoFileSelectedText(this.messageSource.getMessage(NO_FILE_SELECTED));
		this.uploadFrm.setSelectedFileText("<b>" + this.messageSource.getMessage("SELECTED_BACKUP_FILE") + "</b>");
		this.uploadFrm.setDeleteCaption(this.messageSource.getMessage("CLEAR"));
		this.uploadFrm.setFieldType(UploadField.FieldType.FILE);

		this.restoreBtn = new Button(this.messageSource.getMessage("RESTORE_BMS_BUTTON"));

		this.tabSheet = new TabSheet();
		this.tabSheet.setImmediate(true);
		this.tabSheet.setStyleName(Reindeer.TABSHEET_MINIMAL);
		this.tabSheet.setStyleName("panel-border");

		this.backupPanel = new Panel();
		this.backupPanel.setStyleName(Reindeer.PANEL_LIGHT);
		this.restorePanel = new Panel();
		this.restorePanel.setStyleName(Reindeer.PANEL_LIGHT);
	}


	/**
	 * If possible, move to a controller class in the future
	 */
	public void initializeActions() {
		final BackupIBDBSaveAction backupAction = new BackupIBDBSaveAction(this.sessionData.getLastOpenedProject(), this.getWindow()) {

			@Override
			public void doAction() {
				super.doAction();
			}
		};

		this.backupBtn.addListener(backupAction);

		final RestoreIBDBSaveAction restoreAction =
				new RestoreIBDBSaveAction(this.sessionData.getLastOpenedProject(), this.getWindow()) {

					@Override
					public void onClose(ConfirmDialog dialog) {
						super.onClose(dialog);
					}
				};

		this.restoreBtn.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {
				// validate file upload

				try {
					BackupAndRestoreView.this.uploadFrm.validate();

				} catch (Validator.InvalidValueException e) {
					BackupAndRestoreView.LOG.error(e.getMessage(), e);
					if (NO_FILE.equals(e.getMessage())) {
						MessageNotifier.showError(clickEvent.getComponent().getWindow(),
								BackupAndRestoreView.this.messageSource.getMessage(Message.ERROR_UPLOAD),
								BackupAndRestoreView.this.messageSource.getMessage(NO_FILE_SELECTED));
						return;
					} else {
						MessageNotifier.showError(clickEvent.getComponent().getWindow(),
								BackupAndRestoreView.this.messageSource.getMessage(Message.ERROR_UPLOAD),
								BackupAndRestoreView.this.messageSource.getMessage(Message.ERROR_INVALID_FILE));
						return;
					}
				}

				String restoreDescMessageFormat = "<b style='color:red'>%s</b><br/><br/>%s";

				ConfirmDialog dialog =
						ConfirmDialog.show(clickEvent.getComponent().getWindow(), BackupAndRestoreView.this.messageSource
								.getMessage(Message.RESTORE_IBDB_WINDOW_CAPTION), String.format(restoreDescMessageFormat,
								BackupAndRestoreView.this.messageSource.getMessage(Message.RESTORE_IBDB_CONFIRM, 
										BackupAndRestoreView.this.sessionData.getLastOpenedProject().getDatabaseName()),
								BackupAndRestoreView.this.messageSource.getMessage(Message.RESTORE_BMS_WARN)),
								BackupAndRestoreView.this.messageSource.getMessage(Message.RESTORE),
								BackupAndRestoreView.this.messageSource.getMessage(Message.CANCEL), restoreAction);
				dialog.setContentMode(ConfirmDialog.CONTENT_HTML);
			}
		});

		this.uploadFrm.setFileFactory(restoreAction);

		this.uploadFrm.setDeleteButtonListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
					BackupAndRestoreView.this.restoreBtn.setEnabled(true);
			}
		});
	}

	public void initializeLayout() {
		this.tabSheet.addTab(this.backupPanel);
		this.tabSheet.getTab(this.backupPanel).setClosable(false);
		this.tabSheet.getTab(this.backupPanel).setCaption("Backup");

		this.tabSheet.addTab(this.restorePanel);
		this.tabSheet.getTab(this.restorePanel).setClosable(false);
		this.tabSheet.getTab(this.restorePanel).setCaption("Restore");

		this.backupBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.backupBtn.addStyleName("marginTop10");

		this.restoreBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.restoreBtn.addStyleName("marginTop10");

		this.uploadFrm.getRootLayout().setStyleName("bms-upload-container");
		this.uploadFrm.getRootLayout().setWidth("100%");
		this.uploadFrm.setButtonCaption("Browse");

		final Label pageTitle = new Label(this.messageSource.getMessage("BACKUP_RESTORE_TITLE"));
		pageTitle.setStyleName(Bootstrap.Typography.H1.styleName());

		final Label restoreUploadTitle = new Label(this.messageSource.getMessage("RESTORE_BMS_UPLOAD"));
		restoreUploadTitle.setStyleName(Bootstrap.Typography.H6.styleName());

		this.root.setSizeFull();
		this.root.setScrollable(true);
		this.root.setStyleName(Reindeer.PANEL_LIGHT);

		final VerticalLayout rootContent = new VerticalLayout();
		this.root.setContent(rootContent);

		rootContent.setSizeUndefined();
		rootContent.setWidth("100%");
		rootContent.setMargin(new Layout.MarginInfo(false, true, true, true));
		rootContent.setSpacing(true);

		rootContent.addComponent(pageTitle);

		this.backupPanel.addComponent(new Label("<div style='height: 10px'></div>", Label.CONTENT_XHTML));
		this.backupPanel.addComponent(this.setUpHeadings(HelpModule.BACKUP_PROGRAM_DATA, this.messageSource.getMessage("BACKUP_BMS_TITLE"),
				"124px"));
		this.backupPanel.addComponent(new Label(this.messageSource.getMessage("BACKUP_BMS_DESCRIPTION", this.sessionData.getLastOpenedProject()
				.getProjectName()), Label.CONTENT_XHTML));
		this.backupPanel.addComponent(this.backupBtn);

		this.restorePanel.addComponent(new Label("<div style='height: 20px'></div>", Label.CONTENT_XHTML));
		this.restorePanel.addComponent(this.setUpHeadings(HelpModule.RESTORE_PROGRAM_DATA, this.messageSource.getMessage("RESTORE_BMS_TITLE"),
				"228px"));
		this.restorePanel.addComponent(new Label(this.messageSource.getMessage("RESTORE_BMS_DESCRIPTION")));
		this.restorePanel.addComponent(restoreUploadTitle);
		this.restorePanel.addComponent(this.uploadFrm);
		this.restorePanel.addComponent(this.restoreBtn);

		rootContent.addComponent(this.tabSheet);
	}

	public HorizontalLayout setUpHeadings(HelpModule module, String heading, String width) {
		HorizontalLayout titleLayout = new HorizontalLayout();
		titleLayout.setSpacing(true);
		titleLayout.setHeight("40px");

		Label toolTitle = new Label(heading);
		toolTitle.addStyleName(Bootstrap.Typography.H4.styleName());
		toolTitle.setContentMode(Label.CONTENT_XHTML);
		toolTitle.setWidth(width);

		titleLayout.addComponent(toolTitle);
		final HelpButton helpButton = new HelpButton(module, "View " + heading + " Tutorial");
		helpButton.addStyleName("bms-backup-restore-help-icon");
		titleLayout.addComponent(helpButton);

		return titleLayout;
	}
}
