
package org.generationcp.ibpworkbench.study.tree;

import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.study.StudyTabSheet;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class StudyTreeRenameItemWindow extends BaseSubWindow implements InitializingBean {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(StudyTreeRenameItemWindow.class);

	@Autowired
	private ContextUtil contextUtil;

	@Autowired
	private StudyDataManager studyDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private Button okButton;
	private Button cancelButton;
	private TextField nameTextField;
	private final StudyTree targetTree;
	private final StudyTabSheet tabSheet;
	private final Integer itemId;
	private final String currentName;
	private StudyFolderNameValidator validator = new StudyFolderNameValidator();

	public StudyTreeRenameItemWindow(final Integer itemId, final String currentName, final StudyTree targetTree,
			final StudyTabSheet tabSheet) {
		this.itemId = itemId;
		this.currentName = currentName;
		this.targetTree = targetTree;
		this.tabSheet = tabSheet;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.addListeners();
	}

	private void instantiateComponents() {
		this.setOverrideFocus(true);
		this.setCaption(this.messageSource.getMessage(Message.RENAME_ITEM));
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

		final Label l = new Label(this.messageSource.getMessage(Message.ITEM_NAME));
		l.addStyleName("gcp-form-title");

		this.nameTextField = new TextField();
		this.nameTextField.setMaxLength(50);
		this.nameTextField.setValue(this.currentName);
		this.nameTextField.setCursorPosition(this.nameTextField.getValue() == null ? 0 : this.nameTextField.getValue().toString().length());
		this.nameTextField.setWidth("200px");
		this.nameTextField.focus();

		formContainer.addComponent(l);
		formContainer.addComponent(this.nameTextField);

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

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final Button.ClickEvent event) {

				try {
					final String newName = StudyTreeRenameItemWindow.this.nameTextField.getValue().toString().trim();
					final String programUUID = StudyTreeRenameItemWindow.this.contextUtil.getProjectInContext().getUniqueID();
					if (!StudyTreeRenameItemWindow.this.currentName.equals(newName)) {
						if (!StudyTreeRenameItemWindow.this.validator.isValidNameInput(newName, StudyTreeRenameItemWindow.this)) {
							return;
						}

						StudyTreeRenameItemWindow.this.studyDataManager.renameSubFolder(newName, StudyTreeRenameItemWindow.this.itemId,
								programUUID);

						StudyTreeRenameItemWindow.this.targetTree.setItemCaption(StudyTreeRenameItemWindow.this.itemId, newName);
						StudyTreeRenameItemWindow.this.targetTree.select(StudyTreeRenameItemWindow.this.itemId);

						// if node is study - rename tab name to new name
						if (!StudyTreeRenameItemWindow.this.targetTree.isFolder(StudyTreeRenameItemWindow.this.itemId)) {
							StudyTreeRenameItemWindow.this.tabSheet.renameStudyTab(StudyTreeRenameItemWindow.this.currentName, newName);
						}
					}

				} catch (final MiddlewareQueryException e) {
					StudyTreeRenameItemWindow.LOG.error(e.getMessage(), e);
					MessageNotifier.showWarning(StudyTreeRenameItemWindow.this.targetTree.getWindow(),
							StudyTreeRenameItemWindow.this.messageSource.getMessage(Message.ERROR_DATABASE),
							StudyTreeRenameItemWindow.this.messageSource.getMessage(Message.ERROR_REPORT_TO));
				} catch (final Exception e) {
					StudyTreeRenameItemWindow.LOG.error(e.getMessage(), e);
					MessageNotifier.showError(StudyTreeRenameItemWindow.this.targetTree.getWindow(),
							StudyTreeRenameItemWindow.this.messageSource.getMessage(Message.ERROR_INTERNAL),
							StudyTreeRenameItemWindow.this.messageSource.getMessage(Message.ERROR_REPORT_TO));
					return;
				}

				StudyTreeRenameItemWindow.this.closePopup();
			}
		});

		this.cancelButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final Button.ClickEvent event) {
				StudyTreeRenameItemWindow.this.closePopup();
			}
		});
	}

	void closePopup() {
		final Window parentWindow = StudyTreeRenameItemWindow.this.getParent();
		parentWindow.focus();
		parentWindow.removeWindow(StudyTreeRenameItemWindow.this);
	}

	protected StudyTree getTargetTree() {
		return this.targetTree;
	}

	protected StudyTabSheet getTabSheet() {
		return this.tabSheet;
	}

	protected Integer getItemId() {
		return this.itemId;
	}

	protected String getCurrentName() {
		return this.currentName;
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

	protected Button getOkButton() {
		return this.okButton;
	}

	protected Button getCancelButton() {
		return this.cancelButton;
	}

	protected TextField getNameTextField() {
		return this.nameTextField;
	}

	protected void setValidator(final StudyFolderNameValidator validator) {
		this.validator = validator;
	}

}
