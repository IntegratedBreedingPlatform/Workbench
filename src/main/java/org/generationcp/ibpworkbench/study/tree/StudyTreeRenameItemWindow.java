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

	@Autowired
	private ContextUtil contextUtil;
	
	@Autowired
	private StudyDataManager studyDataManager;
	
	@Autowired
	private SimpleResourceBundleMessageSource messageSource;
	
	private Button okButton;
	private Button cancelButton;
	private TextField nameTextField;
	private StudyTree targetTree;
	private StudyTabSheet tabSheet;
	private Integer itemId;
	private String currentName;
	
	public StudyTreeRenameItemWindow(final Integer itemId, final String currentName, final StudyTree targetTree, final StudyTabSheet tabSheet) {
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
		nameTextField.setMaxLength(50);
		nameTextField.setValue(currentName);
		nameTextField.setCursorPosition(nameTextField.getValue() == null ? 0 : nameTextField.getValue().toString().length());
		nameTextField.setWidth("200px");
		nameTextField.focus();

		formContainer.addComponent(l);
		formContainer.addComponent(nameTextField);

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
		cancelButton.setClickShortcut(KeyCode.ESCAPE);

		btnContainer.addComponent(this.okButton);
		btnContainer.addComponent(cancelButton);

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
					final String newName = nameTextField.getValue().toString().trim();
					final String programUUID = contextUtil.getProjectInContext().getUniqueID();
					if (!currentName.equals(newName)) {
						final StudyFolderNameValidator validator = new StudyFolderNameValidator();
						if (!validator.isValidNameInput(newName, StudyTreeRenameItemWindow.this)) {
							return;
						}

						studyDataManager.renameSubFolder(newName, itemId, programUUID);

						targetTree.setItemCaption(itemId, newName);
						targetTree.select(itemId);

						// if node is study - rename tab name to new name
						if (!targetTree.isFolder(itemId)) {
							tabSheet.renameStudyTab(currentName, newName);
						}
					}

				} catch (final MiddlewareQueryException e) {
					MessageNotifier.showWarning(targetTree.getWindow(),
							messageSource.getMessage(Message.ERROR_DATABASE),
							messageSource.getMessage(Message.ERROR_REPORT_TO));
				} catch (final Exception e) {
					MessageNotifier.showError(targetTree.getWindow(),
							messageSource.getMessage(Message.ERROR_INTERNAL),
							messageSource.getMessage(Message.ERROR_REPORT_TO));
					return;
				}

				// close popup
				StudyTreeRenameItemWindow.this.getParent().removeWindow(StudyTreeRenameItemWindow.this);
			}
		});
		
		this.cancelButton.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final Button.ClickEvent event) {
				final Window parentWindow = StudyTreeRenameItemWindow.this.getParent();
				parentWindow.focus();
				parentWindow.removeWindow(StudyTreeRenameItemWindow.this);
			}
		});
	}

}
