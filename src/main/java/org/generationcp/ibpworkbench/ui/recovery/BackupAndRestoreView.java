
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
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * Created by cyrus on 2/20/14.
 */
@Configurable
public class BackupAndRestoreView extends CustomComponent implements InitializingBean {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ProgramMethodsView.class);
	private ListSelect restoreList;
	private Button backupBtn;
	private UploadField uploadFrm;
	private Button restoreBtn;

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

				BackupAndRestoreView.this.restoreList.setEnabled(false);
			}

			@Override
			public void validate() throws Validator.InvalidValueException {
				if (this.getLastFileName() == null) {
					throw new Validator.InvalidValueException("NO_FILE");
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

		this.uploadFrm.setNoFileSelectedText(this.messageSource.getMessage("NO_FILE_SELECTED"));
		this.uploadFrm.setSelectedFileText("<b>" + this.messageSource.getMessage("SELECTED_BACKUP_FILE") + "</b>");
		this.uploadFrm.setDeleteCaption(this.messageSource.getMessage("CLEAR"));
		this.uploadFrm.setFieldType(UploadField.FieldType.FILE);

		this.restoreBtn = new Button(this.messageSource.getMessage("RESTORE_BMS_BUTTON"));
		this.restoreList = new ListSelect();

		this.populateRestoreList();

		this.restoreList.setMultiSelect(false);
		this.restoreList.setRows(1);
		this.restoreList.setNullSelectionAllowed(true);

	}

	public void populateRestoreList() {
		this.restoreList.removeAllItems();

		try {
			for (ProjectBackup pb : this.workbenchDataManager.getProjectBackups(this.sessionData.getLastOpenedProject())) {
				if (!new File(pb.getBackupPath()).exists()) {
					continue;
				}

				this.restoreList.addItem(pb);

				String dateStr = DateUtil.getSimpleDateFormat("MMMM dd, yyyy").format(pb.getBackupTime());

				this.restoreList.setItemCaption(pb, dateStr + " - " + pb.getBackupPath());
				this.restoreList.setValue(pb);
			}
		} catch (MiddlewareQueryException e) {
			BackupAndRestoreView.LOG.error(e.getMessage(), e);
		}

		if (this.restoreList.getItemIds().isEmpty()) {
			this.restoreBtn.setEnabled(false);
		} else {
			this.restoreBtn.setEnabled(true);
		}

	}

	/**
	 * If possible, move to a controller class in the future
	 */
	public void initializeActions() {
		final BackupIBDBSaveAction backupAction = new BackupIBDBSaveAction(this.sessionData.getLastOpenedProject(), this.getWindow()) {

			@Override
			public void doAction() {
				super.doAction();
				BackupAndRestoreView.this.populateRestoreList();
			}
		};

		this.backupBtn.addListener(backupAction);

		final RestoreIBDBSaveAction restoreAction =
				new RestoreIBDBSaveAction(this.sessionData.getLastOpenedProject(), (ProjectBackup) null, this.getWindow()) {

					@Override
					public void onClose(ConfirmDialog dialog) {
						super.onClose(dialog);

						if (dialog.isConfirmed()) {
							BackupAndRestoreView.this.populateRestoreList();
						}
					}
				};

		this.restoreBtn.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(Button.ClickEvent clickEvent) {
				// validate file upload

				try {

					BackupAndRestoreView.this.uploadFrm.validate();
					restoreAction.setIsUpload(true);

				} catch (Validator.InvalidValueException e) {
					BackupAndRestoreView.LOG.error(e.getMessage(), e);
					if (!"NO_FILE".equals(e.getMessage())) {
						MessageNotifier.showError(clickEvent.getComponent().getWindow(),
								BackupAndRestoreView.this.messageSource.getMessage(Message.ERROR_UPLOAD),
								BackupAndRestoreView.this.messageSource.getMessage(Message.ERROR_INVALID_FILE));
						return;
					}

					if (BackupAndRestoreView.this.restoreList.getValue() == null) {
						MessageNotifier.showError(clickEvent.getComponent().getWindow(),
								BackupAndRestoreView.this.messageSource.getMessage(Message.ERROR_UPLOAD), "No project backup is selected");
						return;
					}

					restoreAction.setProjectBackup((ProjectBackup) BackupAndRestoreView.this.restoreList.getValue());
					restoreAction.setIsUpload(false);
				}

				String restoreDescMessageFormat = "%s<br/><br/><b style='color:red'>%s</b>";

				ConfirmDialog dialog =
						ConfirmDialog.show(clickEvent.getComponent().getWindow(), BackupAndRestoreView.this.messageSource
								.getMessage(Message.RESTORE_IBDB_WINDOW_CAPTION), String.format(restoreDescMessageFormat,
								BackupAndRestoreView.this.messageSource.getMessage(Message.RESTORE_IBDB_CONFIRM),
								BackupAndRestoreView.this.messageSource.getMessage("RESTORE_BMS_WARN")),
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
				BackupAndRestoreView.this.restoreList.setEnabled(true);

				if (BackupAndRestoreView.this.restoreList.getItemIds().isEmpty()) {
					BackupAndRestoreView.this.restoreBtn.setEnabled(false);
				} else {
					BackupAndRestoreView.this.restoreBtn.setEnabled(true);
				}
			}
		});
	}

	public void initializeLayout() {
		this.backupBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.backupBtn.addStyleName("marginTop10");

		this.restoreBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		this.restoreBtn.addStyleName("marginTop10");

		this.uploadFrm.getRootLayout().setStyleName("bms-upload-container");
		this.uploadFrm.getRootLayout().setWidth("100%");
		this.uploadFrm.setButtonCaption("Browse");

		this.restoreList.setWidth("400px");

		final Label pageTitle = new Label(this.messageSource.getMessage("BACKUP_RESTORE_TITLE"));
		pageTitle.setStyleName(Bootstrap.Typography.H1.styleName());

		final Label restoreDropdownTitle = new Label(this.messageSource.getMessage("RESTORE_CHOOSE_BACKUP"));
		restoreDropdownTitle.setStyleName(Bootstrap.Typography.H6.styleName());

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
		rootContent.addComponent(new Label("<div style='height: 10px'></div>", Label.CONTENT_XHTML));
		rootContent.addComponent(this.setUpHeadings(HelpModule.BACKUP_PROGRAM_DATA, this.messageSource.getMessage("BACKUP_BMS_TITLE"),
				"124px"));
		rootContent.addComponent(new Label(this.messageSource.getMessage("BACKUP_BMS_DESCRIPTION", this.sessionData.getLastOpenedProject()
				.getProjectName()), Label.CONTENT_XHTML));
		rootContent.addComponent(this.backupBtn);
		rootContent.addComponent(new Label("<div style='height: 20px'></div>", Label.CONTENT_XHTML));
		rootContent.addComponent(this.setUpHeadings(HelpModule.RESTORE_PROGRAM_DATA, this.messageSource.getMessage("RESTORE_BMS_TITLE"),
				"228px"));
		rootContent.addComponent(new Label(this.messageSource.getMessage("RESTORE_BMS_DESCRIPTION")));
		rootContent.addComponent(restoreDropdownTitle);
		rootContent.addComponent(this.restoreList);
		rootContent.addComponent(new Label("", Label.CONTENT_XHTML));
		rootContent.addComponent(new Label("<div style='margin: 5px 0;'>Or</div>", Label.CONTENT_XHTML));
		rootContent.addComponent(restoreUploadTitle);
		rootContent.addComponent(this.uploadFrm);
		rootContent.addComponent(this.restoreBtn);

	}

	public HorizontalLayout setUpHeadings(HelpModule module, String heading, String width) {
		HorizontalLayout titleLayout = new HorizontalLayout();
		titleLayout.setSpacing(true);
		titleLayout.setHeight("40px");

		Label toolTitle = new Label(heading);
		toolTitle.setStyleName(Bootstrap.Typography.H4.styleName());
		toolTitle.setContentMode(Label.CONTENT_XHTML);
		toolTitle.setWidth(width);

		titleLayout.addComponent(toolTitle);
		titleLayout.addComponent(new HelpButton(module, "View " + heading + " Tutorial"));

		return titleLayout;
	}
}
