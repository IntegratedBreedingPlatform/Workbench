package org.generationcp.ibpworkbench.ui.recovery;

import org.generationcp.commons.help.document.HelpButton;
import org.generationcp.commons.help.document.HelpModule;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.actions.BackupIBDBSaveAction;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.actions.RestoreIBDBSaveAction;
import org.generationcp.ibpworkbench.ui.common.UploadField;
import org.generationcp.middleware.pojos.workbench.Project;
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
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class BackupAndRestoreView extends CustomComponent implements InitializingBean {

	private static final long serialVersionUID = 1L;

	public static final String NO_FILE = "NO_FILE";
	public static final String NO_FILE_SELECTED = "NO_FILE_SELECTED";
	public static final String NOT_VALID = "NOT_VALID";
	private static final String MARGIN_TOP_10 = "marginTop10";
	public static final String BMS_LABEL_BOTTOM_SPACE_STYLE = "bms-label-bottom-space";

	private Button backupButton;
	private UploadField uploadField;
	private Button restoreButton;
	private TabSheet tabSheet;
	private Panel backupPanel;
	private Panel restorePanel;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private ContextUtil contextUtil;

	private final Panel root = new Panel();

	public BackupAndRestoreView() {
		this.root.setDebugId("rootBackupAndRestore");
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
		this.backupButton = new Button(this.messageSource.getMessage("BACKUP_BMS_BUTTON"));
		this.backupButton.setDebugId("backupBtn");
		this.uploadField = new UploadField() {

			@Override
			public void uploadFinished(final Upload.FinishedEvent event) {
				super.uploadFinished(event);

				BackupAndRestoreView.this.restoreButton.setEnabled(true);
			}

			@Override
			public void validate() {
				if (this.getLastFileName() == null) {
					throw new Validator.InvalidValueException(NO_FILE);
				} else if (!this.isValid()) {
					throw new Validator.InvalidValueException(NOT_VALID);
				}
			}

			@Override
			public boolean isValid() {
				return this.getLastFileName() != null && this.getExtension(this.getLastFileName()).toLowerCase().contains("sql");
			}

			private String getExtension(final String f) {
				String ext = null;
				final int i = f.lastIndexOf('.');

				if (i > 0 && i < f.length() - 1) {
					ext = f.substring(i + 1).toLowerCase();
				}

				if (ext == null) {
					return "";
				}
				return ext;
			}
		};

		this.uploadField.setNoFileSelectedText(this.messageSource.getMessage(NO_FILE_SELECTED));
		this.uploadField.setSelectedFileText("<b>" + this.messageSource.getMessage("SELECTED_BACKUP_FILE") + "</b>");
		this.uploadField.setDeleteCaption(this.messageSource.getMessage("CLEAR"));
		this.uploadField.setFieldType(UploadField.FieldType.FILE);

		this.restoreButton = new Button(this.messageSource.getMessage("RESTORE_BMS_BUTTON"));
		this.restoreButton.setDebugId("restoreBtn");

		this.tabSheet = new TabSheet();
		this.tabSheet.setDebugId("tabSheet");
		this.tabSheet.setImmediate(true);
		this.tabSheet.setStyleName(Reindeer.TABSHEET_MINIMAL);
		this.tabSheet.setStyleName("panel-border");

		this.backupPanel = new Panel();
		this.backupPanel.setDebugId("backupPanel");
		this.backupPanel.setStyleName(Reindeer.PANEL_LIGHT);
		this.restorePanel = new Panel();
		this.restorePanel.setDebugId("restorePanel");
		this.restorePanel.setStyleName(Reindeer.PANEL_LIGHT);
	}

	/**
	 * If possible, move to a controller class in the future
	 */
	public void initializeActions() {

		final Project project = contextUtil.getProjectInContext();

		final BackupIBDBSaveAction backupAction = new BackupIBDBSaveAction(project, this.getWindow()) {

			@Override
			public void doAction() {
				super.doAction();
			}
		};

		this.backupButton.addListener(backupAction);

		final RestoreIBDBSaveAction restoreAction = new RestoreIBDBSaveAction(project, this.getWindow()) {

			@Override
			public void onClose(final ConfirmDialog dialog) {
				super.onClose(dialog);
				// go back to dashboard
				new HomeAction().doAction(BackupAndRestoreView.this.getWindow(), "/Home", true);
			}
		};

		this.restoreButton.addListener(new RestoreButtonClickListener(project, restoreAction, this.uploadField));

		this.uploadField.setFileFactory(restoreAction);

		this.uploadField.setDeleteButtonListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final ClickEvent event) {
				BackupAndRestoreView.this.restoreButton.setEnabled(true);
			}
		});
	}

	public void initializeLayout() {
		this.tabSheet.addTab(this.backupPanel);
		this.tabSheet.getTab(this.backupPanel).setClosable(false);
		this.tabSheet.getTab(this.backupPanel).setCaption(this.messageSource.getMessage("BACKUP_LABEL"));

		this.tabSheet.addTab(this.restorePanel);
		this.tabSheet.getTab(this.restorePanel).setClosable(false);
		this.tabSheet.getTab(this.restorePanel).setCaption(this.messageSource.getMessage("RESTORE_LABEL"));

		this.backupButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.backupButton.addStyleName(MARGIN_TOP_10);

		this.restoreButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.restoreButton.addStyleName(MARGIN_TOP_10);

		this.uploadField.getRootLayout().addStyleName("bms-upload-container");
		this.uploadField.getRootLayout().setWidth("100%");
		this.uploadField.setButtonCaption(this.messageSource.getMessage("BROWSE"));
		this.uploadField.addStyleName(BMS_LABEL_BOTTOM_SPACE_STYLE);

		final Label pageTitle = new Label(this.messageSource.getMessage("BACKUP_RESTORE_TITLE"));
		pageTitle.setDebugId("pageTitle");
		pageTitle.setStyleName(Bootstrap.Typography.H1.styleName());

		final Label restoreUploadTitle = new Label(this.messageSource.getMessage("RESTORE_BMS_UPLOAD"));
		restoreUploadTitle.setDebugId("restoreUploadTitle");
		restoreUploadTitle.setStyleName(Bootstrap.Typography.H6.styleName());

		this.root.setSizeFull();
		this.root.setScrollable(true);
		this.root.setStyleName(Reindeer.PANEL_LIGHT);

		final VerticalLayout rootContent = new VerticalLayout();
		rootContent.setDebugId("rootContent");
		this.root.setContent(rootContent);

		rootContent.setSizeUndefined();
		rootContent.setWidth("100%");
		rootContent.setMargin(new Layout.MarginInfo(false, true, true, true));
		rootContent.setSpacing(true);

		rootContent.addComponent(pageTitle);

		this.backupPanel.addComponent(new Label("<div style='height: 10px'></div>", Label.CONTENT_XHTML));
		this.backupPanel.addComponent(
				this.setUpHeadings(HelpModule.BACKUP_PROGRAM_DATA, this.messageSource.getMessage("BACKUP_BMS_TITLE"), "124px"));
		final Label backupTextLabel =
				new Label(this.messageSource.getMessage("BACKUP_BMS_DESCRIPTION", contextUtil.getProjectInContext().getProjectName()));
		backupTextLabel.addStyleName(BMS_LABEL_BOTTOM_SPACE_STYLE);
		this.backupPanel.addComponent(backupTextLabel);
		this.backupPanel.addComponent(this.backupButton);

		this.restorePanel.addComponent(new Label("<div style='height: 20px'></div>", Label.CONTENT_XHTML));
		this.restorePanel.addComponent(
				this.setUpHeadings(HelpModule.RESTORE_PROGRAM_DATA, this.messageSource.getMessage("RESTORE_BMS_TITLE"), "228px"));
		final Label restoreDescriptionLabel = new Label(this.messageSource.getMessage("RESTORE_BMS_DESCRIPTION"));
		restoreDescriptionLabel.setDebugId("restoreDescriptionLabel");
		restoreDescriptionLabel.addStyleName(BMS_LABEL_BOTTOM_SPACE_STYLE);
		this.restorePanel.addComponent(restoreDescriptionLabel);
		this.restorePanel.addComponent(restoreUploadTitle);
		this.restorePanel.addComponent(this.uploadField);
		this.restorePanel.addComponent(this.restoreButton);

		rootContent.addComponent(this.tabSheet);
	}

	private HorizontalLayout setUpHeadings(final HelpModule module, final String heading, final String width) {
		final HorizontalLayout titleLayout = new HorizontalLayout();
		titleLayout.setDebugId("titleLayoutBackupAndRestore");
		titleLayout.setSpacing(true);
		titleLayout.setHeight("40px");

		final Label toolTitle = new Label(heading);
		toolTitle.setDebugId("toolTitleHeadingBackupAndRestore");
		toolTitle.addStyleName(Bootstrap.Typography.H4.styleName());
		toolTitle.setContentMode(Label.CONTENT_XHTML);
		toolTitle.setWidth(width);

		titleLayout.addComponent(toolTitle);
		final HelpButton helpButton = new HelpButton(module,
				this.messageSource.getMessage("VIEW") + " " + heading + " " + this.messageSource.getMessage("TUTORIAL"));
		helpButton.addStyleName("bms-backup-restore-help-icon");
		titleLayout.addComponent(helpButton);

		return titleLayout;
	}

	
	public Button getBackupButton() {
		return backupButton;
	}

	
	public Button getRestoreButton() {
		return restoreButton;
	}
	

	public void setUploadField(UploadField uploadField) {
		this.uploadField = uploadField;
	}
}
