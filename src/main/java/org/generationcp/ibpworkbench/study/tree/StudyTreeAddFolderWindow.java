
package org.generationcp.ibpworkbench.study.tree;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class StudyTreeAddFolderWindow extends BaseSubWindow implements InitializingBean {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(StudyTreeAddFolderWindow.class);

	@Autowired
	private ContextUtil contextUtil;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private Button okButton;
	private Button cancelButton;
	private TextField folderTextField;
	private final Object parentItemId;
	private StudyTree targetTree;

	private StudyFolderNameValidator validator = new StudyFolderNameValidator();

	public StudyTreeAddFolderWindow(final Object parentItemId, final StudyTree studyTree) {
		super("Add new folder");
		this.parentItemId = parentItemId;
		this.targetTree = studyTree;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.addListeners();
	}

	private void instantiateComponents() {
		this.setOverrideFocus(true);
		this.setWidth("320px");
		this.setHeight("160px");
		this.setModal(true);
		this.setResizable(false);
		this.setStyleName(Reindeer.WINDOW_LIGHT);

		final VerticalLayout container = new VerticalLayout();
		container.setSpacing(true);
		container.setMargin(true);

		final HorizontalLayout formContainer = new HorizontalLayout();
		formContainer.setSpacing(true);

		final Label l = new Label("Folder Name");
		l.addStyleName("gcp-form-title");
		this.folderTextField = new TextField();
		this.folderTextField.setMaxLength(50);
		this.folderTextField.setWidth("190px");
		this.folderTextField.focus();

		formContainer.addComponent(l);
		formContainer.addComponent(this.folderTextField);

		final HorizontalLayout btnContainer = new HorizontalLayout();
		btnContainer.setSpacing(true);
		btnContainer.setWidth("100%");

		final Label spacer = new Label("");
		btnContainer.addComponent(spacer);
		btnContainer.setExpandRatio(spacer, 1.0F);

		this.okButton = new Button("Ok");
		this.okButton.setClickShortcut(KeyCode.ENTER);
		this.okButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

		this.cancelButton = new Button("Cancel");
		this.cancelButton.setClickShortcut(KeyCode.ESCAPE);

		btnContainer.addComponent(this.okButton);
		btnContainer.addComponent(this.cancelButton);

		container.addComponent(formContainer);
		container.addComponent(btnContainer);

		this.setContent(container);
	}

	private void addListeners() {
		this.okButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -6313787074401316900L;

			@Override
			public void buttonClick(final Button.ClickEvent event) {
				Integer newFolderId = null;
				final String newFolderName = StudyTreeAddFolderWindow.this.folderTextField.getValue().toString();
				int parentFolderId = DmsProject.SYSTEM_FOLDER_ID;
				final String programUUID = StudyTreeAddFolderWindow.this.contextUtil.getProjectInContext().getUniqueID();
				try {
					if (!StudyTreeAddFolderWindow.this.validator.isValidNameInput(newFolderName,
							StudyTreeAddFolderWindow.this.getWindow())) {
						return;
					}

					if (StudyTreeAddFolderWindow.this.parentItemId != null
							&& StudyTreeAddFolderWindow.this.parentItemId instanceof Integer) {
						if (StudyTreeAddFolderWindow.this.targetTree.isFolder((Integer) StudyTreeAddFolderWindow.this.parentItemId)) {
							parentFolderId = ((Integer) StudyTreeAddFolderWindow.this.parentItemId).intValue();
						} else {
							final int selectItemId = ((Integer) StudyTreeAddFolderWindow.this.parentItemId).intValue();
							final DmsProject parentFolder = StudyTreeAddFolderWindow.this.studyDataManager.getParentFolder(selectItemId);
							parentFolderId = parentFolder.getProjectId().intValue();
						}
					}

					newFolderId = Integer.valueOf(StudyTreeAddFolderWindow.this.studyDataManager.addSubFolder(parentFolderId, newFolderName,
							newFolderName, programUUID, newFolderName));
				} catch (final MiddlewareQueryException ex) {
					StudyTreeAddFolderWindow.LOG.error("Error with adding a study folder.", ex);
					MessageNotifier.showError(StudyTreeAddFolderWindow.this,
							StudyTreeAddFolderWindow.this.messageSource.getMessage(Message.ERROR_DATABASE),
							StudyTreeAddFolderWindow.this.messageSource.getMessage(Message.PLEASE_SEE_ERROR_LOG));
					return;
				}

				// update UI
				if (newFolderId != null) {
					StudyTreeAddFolderWindow.this.targetTree.addItem(newFolderId);
					StudyTreeAddFolderWindow.this.targetTree.setItemCaption(newFolderId, newFolderName);
					StudyTreeAddFolderWindow.this.targetTree.setItemIcon(newFolderId,
							new ThemeResource("../vaadin-retro/svg/folder-icon.svg"));
					StudyTreeAddFolderWindow.this.targetTree.setChildrenAllowed(newFolderId, true);

					if (DmsProject.SYSTEM_FOLDER_ID.equals(StudyTreeAddFolderWindow.this.parentItemId)) {
						StudyTreeAddFolderWindow.this.targetTree.setChildrenAllowed(StudyTree.STUDY_ROOT_NODE, true);
						StudyTreeAddFolderWindow.this.targetTree.setParent(newFolderId, StudyTree.STUDY_ROOT_NODE);
					} else {
						StudyTreeAddFolderWindow.this.targetTree.setChildrenAllowed(Integer.valueOf(parentFolderId), true);
						StudyTreeAddFolderWindow.this.targetTree.setParent(newFolderId, Integer.valueOf(parentFolderId));
					}

					if (StudyTreeAddFolderWindow.this.targetTree.getValue() != null) {
						if (!StudyTreeAddFolderWindow.this.targetTree.isExpanded(StudyTreeAddFolderWindow.this.targetTree.getValue())) {
							StudyTreeAddFolderWindow.this.targetTree.expandItem(StudyTreeAddFolderWindow.this.parentItemId);
						}
					} else {
						StudyTreeAddFolderWindow.this.targetTree.expandItem(StudyTree.STUDY_ROOT_NODE);
					}

					StudyTreeAddFolderWindow.this.targetTree.selectItem(newFolderId);
				}

				StudyTreeAddFolderWindow.this.closePopup();
			}

		});

		this.cancelButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -6542741100092010158L;

			@Override
			public void buttonClick(final Button.ClickEvent event) {
				StudyTreeAddFolderWindow.this.closePopup();

			}
		});

	}

	void closePopup() {
		final Window parentWindow = StudyTreeAddFolderWindow.this.getParent();
		parentWindow.focus();
		parentWindow.removeWindow(StudyTreeAddFolderWindow.this);
	}

	protected Object getParentItemId() {
		return this.parentItemId;
	}

	protected StudyTree getTargetTree() {
		return this.targetTree;
	}

	protected Button getOkButton() {
		return this.okButton;
	}

	protected Button getCancelButton() {
		return this.cancelButton;
	}

	protected TextField getFolderTextField() {
		return this.folderTextField;
	}

	protected void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	protected void setStudyDataManager(final StudyDataManager studyDataManager) {
		this.studyDataManager = studyDataManager;
	}

	protected void setMessageSource(final SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	protected void setTargetTree(final StudyTree targetTree) {
		this.targetTree = targetTree;
	}

	protected void setValidator(final StudyFolderNameValidator validator) {
		this.validator = validator;
	}

}
