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
	private Object parentItemId;
	private StudyTree targetTree;
	
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
				final String newFolderName = folderTextField.getValue().toString();
				// 1 by default because root study folder has id = 1
				int parentFolderId = 1;
				final String programUUID = contextUtil.getProjectInContext().getUniqueID();
				try {
					final StudyFolderNameValidator validator = new StudyFolderNameValidator();
					if (!validator.isValidNameInput(newFolderName, StudyTreeAddFolderWindow.this.getWindow())) {
						return;
					}

					if (parentItemId != null && parentItemId instanceof Integer) {
						if (targetTree.isFolder((Integer) parentItemId)) {
							parentFolderId = ((Integer) parentItemId).intValue();
						} else {
							final int selectItemId = ((Integer) parentItemId).intValue();
							final DmsProject parentFolder = studyDataManager.getParentFolder(selectItemId);
							parentFolderId = parentFolder.getProjectId().intValue();
						}
					}

					newFolderId =
							Integer.valueOf(studyDataManager.addSubFolder(parentFolderId, newFolderName, newFolderName,
									programUUID, newFolderName));
				} catch (final MiddlewareQueryException ex) {
					StudyTreeAddFolderWindow.LOG.error("Error with adding a study folder.", ex);
					MessageNotifier.showError(StudyTreeAddFolderWindow.this,
							messageSource.getMessage(Message.ERROR_DATABASE),
							messageSource.getMessage(Message.PLEASE_SEE_ERROR_LOG));
					return;
				}

				// update UI
				if (newFolderId != null) {
					targetTree.addItem(newFolderId);
					targetTree.setItemCaption(newFolderId, newFolderName);
					targetTree.setItemIcon(newFolderId, new ThemeResource("../vaadin-retro/svg/folder-icon.svg"));
					targetTree.setChildrenAllowed(newFolderId, true);

					targetTree.setSelectedNodeId(newFolderId);

					if (parentFolderId == 1) {
						targetTree.setChildrenAllowed(StudyTree.STUDY_ROOT_NODE, true);
						targetTree.setParent(newFolderId, StudyTree.STUDY_ROOT_NODE);
					} else {
						targetTree.setChildrenAllowed(Integer.valueOf(parentFolderId), true);
						targetTree.setParent(newFolderId, Integer.valueOf(parentFolderId));
					}

					if (targetTree.getValue() != null) {
						if (!targetTree.isExpanded(targetTree.getValue())) {
							targetTree.expandItem(parentItemId);
						}
					} else {
						targetTree.expandItem(StudyTree.STUDY_ROOT_NODE);
					}

					targetTree.selectStudy(newFolderId);
				}

				// close popup
				StudyTreeAddFolderWindow.this.getParent().removeWindow(StudyTreeAddFolderWindow.this);
			}

		});
		
		this.cancelButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -6542741100092010158L;

			@Override
			public void buttonClick(final Button.ClickEvent event) {
				final Window parentWindow = StudyTreeAddFolderWindow.this.getParent();
				parentWindow.focus();
				parentWindow.removeWindow(StudyTreeAddFolderWindow.this);

			}
		});
		
	}
	

}
