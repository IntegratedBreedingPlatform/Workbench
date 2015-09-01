
package org.generationcp.ibpworkbench.ui.window;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.common.ServerFilePicker;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class UserToolsManagerWindow extends BaseSubWindow implements InitializingBean {

	private static final long serialVersionUID = -6946635521199054537L;

	private Form userToolsForm;
	private Button addBtn;
	private Button cancelBtn;
	private ListSelect userToolsListSelect;

	private Window thisWindow;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private static final Logger LOG = LoggerFactory.getLogger(UserToolsManagerWindow.class);
	private static final String WIDTH = "780px";
	private static final String HEIGHT = "470px";

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private PlatformTransactionManager transactionManager;
	
	private Button editBtn;
	private BeanItemContainer<Tool> userToolsListContainer;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.assemble();
	}

	protected void assemble() throws MiddlewareQueryException {
		final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				UserToolsManagerWindow.this.initializeComponents();
				UserToolsManagerWindow.this.initializeData();
				UserToolsManagerWindow.this.initializeLayout();
				UserToolsManagerWindow.this.initializeActions();
			}
		});
	}

	private void initializeActions() {

		// ADD ACTION
		final ConfirmDialog.Listener onAddAction = new ConfirmDialog.Listener() {

			private static final long serialVersionUID = 4058077735930043412L;

			@Override
			public void onClose(ConfirmDialog dialog) {
				// USER did not continue
				if (!dialog.isConfirmed()) {
					return;
				}

				@SuppressWarnings("unchecked")
				BeanItem<Tool> userToolBeanItem = (BeanItem<Tool>) UserToolsManagerWindow.this.userToolsForm.getItemDataSource();
				Tool userToolFormData = userToolBeanItem.getBean();

				UserToolsManagerWindow.LOG.debug(userToolFormData.toString());

				userToolFormData.setToolId(null);

				if (userToolFormData.getParameter() == null) {
					userToolFormData.setParameter("");
				}

				userToolFormData.setUserTool(true);

				if (userToolFormData.getVersion() == null) {
					userToolFormData.setVersion("");
				}

				try {
					UserToolsManagerWindow.this.workbenchDataManager.getToolDao().save(userToolFormData);

					Tool newTool =
							UserToolsManagerWindow.this.workbenchDataManager.getToolDao().getByToolName(userToolFormData.getToolName());

					UserToolsManagerWindow.this.userToolsListContainer.addItemAt(0, newTool);

					// clear the form data for new entry
					UserToolsManagerWindow.this.userToolsForm.setItemDataSource(new BeanItem<Tool>(new Tool()),
							Arrays.asList("toolName", "title", "toolType", "version", "path", "parameter"));

					MessageNotifier.showMessage(UserToolsManagerWindow.this.thisWindow,
							UserToolsManagerWindow.this.messageSource.getMessage(Message.SUCCESS),
							UserToolsManagerWindow.this.messageSource.getMessage(Message.USER_TOOLS_ADDED));

				} catch (MiddlewareQueryException e) {
					MessageNotifier.showError(UserToolsManagerWindow.this.thisWindow,
							UserToolsManagerWindow.this.messageSource.getMessage(Message.FAIL),
							UserToolsManagerWindow.this.messageSource.getMessage(Message.USER_TOOLS_ADD_EXISTS_ERROR));
				}

			}
		};

		// EDIT ACTION
		final ConfirmDialog.Listener onEditAction = new ConfirmDialog.Listener() {

			private static final long serialVersionUID = -1215274789464656574L;

			@Override
			public void onClose(ConfirmDialog dialog) {

				// USER did not continue
				if (!dialog.isConfirmed()) {
					return;
				}

				final Tool selected = (Tool) UserToolsManagerWindow.this.userToolsListSelect.getValue();

				try {
					Collection<Tool> toolList = UserToolsManagerWindow.this.userToolsListContainer.getItemIds();

					@SuppressWarnings("unchecked")
					BeanItem<Tool> userToolBeanItem = (BeanItem<Tool>) UserToolsManagerWindow.this.userToolsForm.getItemDataSource();
					Tool userToolFormData = userToolBeanItem.getBean();

					for (Tool t : toolList) {
						if (t.getToolName().equals(userToolFormData.getToolName()) && t.getToolId() != userToolFormData.getToolId()) {
							MessageNotifier.showError(UserToolsManagerWindow.this.thisWindow,
									UserToolsManagerWindow.this.messageSource.getMessage(Message.FAIL),
									UserToolsManagerWindow.this.messageSource.getMessage(Message.USER_TOOLS_ADD_EXISTS_ERROR));
							return;
						}
					}

					selected.setToolId(userToolFormData.getToolId());

					if (userToolFormData.getParameter() == null) {
						selected.setParameter("");
					} else {
						selected.setParameter(userToolFormData.getParameter());
					}
					selected.setPath(userToolFormData.getPath());
					selected.setTitle(userToolFormData.getTitle());
					selected.setToolName(userToolFormData.getToolName());
					selected.setToolType(userToolFormData.getToolType());
					selected.setUserTool(true);
					selected.setVersion(userToolFormData.getVersion());

					UserToolsManagerWindow.this.workbenchDataManager.getToolDao().update(selected);

					UserToolsManagerWindow.this.userToolsListContainer.addItem(selected);
					UserToolsManagerWindow.this.userToolsListSelect.setItemCaption(selected, userToolFormData.getTitle());

					MessageNotifier.showMessage(UserToolsManagerWindow.this.thisWindow,
							UserToolsManagerWindow.this.messageSource.getMessage(Message.SUCCESS),
							UserToolsManagerWindow.this.messageSource.getMessage(Message.USER_TOOLS_UPDATED));

				} catch (MiddlewareQueryException e) {
					MessageNotifier.showError(UserToolsManagerWindow.this.thisWindow,
							UserToolsManagerWindow.this.messageSource.getMessage(Message.FAIL),
							UserToolsManagerWindow.this.messageSource.getMessage(Message.USER_TOOLS_EDIT_EXISTS_ERROR));
				}
			}
		};

		// LISTENERS REGISTRATION
		this.addBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -2546119785522908833L;

			@Override
			public void buttonClick(ClickEvent event) {
				UserToolsManagerWindow.this.userToolsForm.setComponentError(null);

				try {
					UserToolsManagerWindow.this.userToolsForm.commit();
				} catch (Validator.EmptyValueException e) {
					MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), e.getLocalizedMessage());
					return;
				} catch (InvalidValueException e) {
					MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), e.getLocalizedMessage());
					return;
				}

				// TODO FIXME Internationalize the messages
				ConfirmDialog.show(UserToolsManagerWindow.this.getParent(),
						UserToolsManagerWindow.this.messageSource.getMessage(Message.USER_TOOLS_WINDOW_CAPTION), "Click OK to proceed",
						UserToolsManagerWindow.this.messageSource.getMessage(Message.OK),
						UserToolsManagerWindow.this.messageSource.getMessage(Message.CANCEL), onAddAction);
			}
		});

		this.editBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = -4324125510112093794L;

			@Override
			public void buttonClick(ClickEvent event) {
				UserToolsManagerWindow.this.userToolsForm.setComponentError(null);

				try {
					UserToolsManagerWindow.this.userToolsForm.commit();
				} catch (Validator.EmptyValueException e) {
					MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), e.getLocalizedMessage());
					return;
				} catch (InvalidValueException e) {
					MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), e.getLocalizedMessage());
					return;
				}

				// TODO FIXME Internationalize the messages
				ConfirmDialog.show(UserToolsManagerWindow.this.getParent(),
						UserToolsManagerWindow.this.messageSource.getMessage(Message.USER_TOOLS_WINDOW_CAPTION), "Click OK to proceed",
						UserToolsManagerWindow.this.messageSource.getMessage(Message.OK),
						UserToolsManagerWindow.this.messageSource.getMessage(Message.CANCEL), onEditAction);
			}
		});

		this.cancelBtn.addListener(new Button.ClickListener() {

			private static final long serialVersionUID = 639662700599872921L;

			@Override
			public void buttonClick(ClickEvent event) {
				UserToolsManagerWindow.this.thisWindow.getApplication().getMainWindow()
						.removeWindow(UserToolsManagerWindow.this.thisWindow);
			}
		});

		this.userToolsListSelect.addListener(new Property.ValueChangeListener() {

			private static final long serialVersionUID = 5847065240320462232L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				// event.getProperty returns the itemID of the selected item

				Tool selectedTool = (Tool) event.getProperty().getValue();

				// clone selectedTool to userToolFormData
				Tool userToolFormData = new Tool();
				userToolFormData.setParameter(selectedTool.getParameter());
				userToolFormData.setPath(selectedTool.getPath());
				userToolFormData.setTitle(selectedTool.getTitle());
				userToolFormData.setToolId(selectedTool.getToolId());
				userToolFormData.setToolName(selectedTool.getToolName());
				userToolFormData.setToolType(selectedTool.getToolType());
				userToolFormData.setVersion(selectedTool.getVersion());
				userToolFormData.setUserTool(selectedTool.getUserTool());

				UserToolsManagerWindow.LOG.debug(selectedTool.toString());

				UserToolsManagerWindow.this.userToolsForm.setItemDataSource(new BeanItem<Tool>(userToolFormData),
						Arrays.asList("toolName", "title", "toolType", "version", "path", "parameter"));

				UserToolsManagerWindow.this.editBtn.setEnabled(true);
			}
		});
	}

	private void initializeLayout() {
		this.addStyleName(Reindeer.WINDOW_LIGHT);
		this.setModal(true);
		this.setWidth(UserToolsManagerWindow.WIDTH);
		this.setHeight(UserToolsManagerWindow.HEIGHT);
		this.setCaption(this.messageSource.getMessage(Message.USER_TOOLS_WINDOW_CAPTION));

		HorizontalLayout mainPanel = new HorizontalLayout();
		Label spacer2 = new Label("&nbsp;", Label.CONTENT_XHTML);
		spacer2.setWidth("20px");

		VerticalLayout vl = new VerticalLayout();
		vl.setCaption(this.messageSource.getMessage(Message.TOOL_NAME));
		vl.addComponent(new Label("<div style='height: 5px'></div>", Label.CONTENT_XHTML));
		vl.addComponent(this.userToolsListSelect);
		vl.setWidth("100%");

		mainPanel.addComponent(vl);
		mainPanel.addComponent(spacer2);
		mainPanel.addComponent(this.userToolsForm);
		mainPanel.setSpacing(true);
		mainPanel.setMargin(false, true, false, false);
		mainPanel.setWidth("100%");
		mainPanel.setExpandRatio(vl, 1.0f);

		Label spacer = new Label("&nbsp;", Label.CONTENT_XHTML);

		HorizontalLayout btnPanel = new HorizontalLayout();
		btnPanel.setWidth("100%");
		btnPanel.setSpacing(true);
		btnPanel.setMargin(true);

		btnPanel.addComponentAsFirst(spacer);
		btnPanel.addComponent(this.cancelBtn);
		btnPanel.addComponent(this.addBtn);
		btnPanel.addComponent(this.editBtn);

		btnPanel.setComponentAlignment(this.addBtn, Alignment.MIDDLE_RIGHT);
		btnPanel.setComponentAlignment(this.editBtn, Alignment.MIDDLE_RIGHT);
		btnPanel.setComponentAlignment(this.cancelBtn, Alignment.MIDDLE_RIGHT);
		btnPanel.setExpandRatio(spacer, 1.0f);

		this.getContent().addComponent(new Label(this.messageSource.getMessage(Message.USER_TOOLS_REMINDER_TEXT), Label.CONTENT_XHTML));
		this.getContent().addComponent(new Label("<div style='height: 20px'></div>", Label.CONTENT_XHTML));
		this.getContent().addComponent(mainPanel);
		this.getContent().addComponent(btnPanel);
	}

	private void initializeData() {

		try {
			List<Tool> userTools = this.workbenchDataManager.getToolDao().getUserTools();

			this.userToolsListContainer = new BeanItemContainer<Tool>(Tool.class, userTools);
			this.userToolsListSelect.setContainerDataSource(this.userToolsListContainer);
			this.userToolsForm.setItemDataSource(new BeanItem<Tool>(new Tool()),
					Arrays.asList("toolName", "title", "toolType", "version", "path", "parameter"));

			this.userToolsListSelect.setItemCaptionPropertyId("title");

			this.userToolsForm.setVisibleItemProperties(new String[] {"toolName", "title", "toolType", "version", "path", "parameter"});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		}
	}

	private void initializeComponents() {
		if (this.thisWindow == null) {
			this.thisWindow = this;
		}

		this.setResizable(false);

		this.userToolsForm = new Form();

		this.userToolsForm.setWidth("380px");
		this.userToolsForm.setHeight("250px");
		this.userToolsForm.setFormFieldFactory(new UserToolsFormFieldFactory(this.thisWindow));
		this.userToolsForm.setComponentError(null);
		this.userToolsForm.setWriteThrough(false);
		this.userToolsForm.setInvalidCommitted(false);
		this.userToolsForm.setValidationVisibleOnCommit(false);

		this.addBtn = new Button(this.messageSource.getMessage(Message.ADD));
		this.addBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

		this.editBtn = new Button(this.messageSource.getMessage(Message.EDIT));
		this.editBtn.setEnabled(false);
		this.editBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

		this.cancelBtn = new Button(this.messageSource.getMessage(Message.CANCEL), this.userToolsForm, "discard");

		this.userToolsListSelect = new ListSelect();
		this.userToolsListSelect.setWidth("100%");
		this.userToolsListSelect.setNullSelectionAllowed(false);

		this.userToolsListSelect.setImmediate(true);

	}

	class UserToolsFormFieldFactory implements FormFieldFactory {

		private static final long serialVersionUID = 294398306909312914L;

		private ComboBox toolTypeFld;
		private TextField nameFld;
		private TextField titleFld;
		private TextField versionFld;
		private TextField parameterFld;
		private ServerFilePicker filePicker;

		public UserToolsFormFieldFactory(Window parentWindow) {
			// TODO: replace with internationalized messages
			this.nameFld = new TextField(UserToolsManagerWindow.this.messageSource.getMessage(Message.NAME));
			this.nameFld.setWidth("190px");
			this.nameFld.setRequired(true);
			this.nameFld.setRequiredError(UserToolsManagerWindow.this.messageSource
					.getMessage(Message.FORM_VALIDATION_USER_TOOLS_NAME_REQUIRED));

			this.titleFld = new TextField(UserToolsManagerWindow.this.messageSource.getMessage(Message.TITLE));
			this.titleFld.setWidth("190px");
			this.titleFld.setRequired(true);
			this.titleFld.setRequiredError(UserToolsManagerWindow.this.messageSource
					.getMessage(Message.FORM_VALIDATION_USER_TOOLS_TITLE_REQUIRED));

			this.parameterFld = new TextField(UserToolsManagerWindow.this.messageSource.getMessage(Message.PARAMETER));
			this.parameterFld.setWidth("190px");

			this.versionFld = new TextField(UserToolsManagerWindow.this.messageSource.getMessage(Message.VERSION));
			this.versionFld.setWidth("80px");

			this.toolTypeFld =
					new ComboBox(UserToolsManagerWindow.this.messageSource.getMessage(Message.TOOL_TYPE), Arrays.asList(new ToolType[] {
							ToolType.WEB, ToolType.WEB_WITH_LOGIN, ToolType.NATIVE}));
			this.toolTypeFld.setWidth("120px");
			this.toolTypeFld.setRequired(true);
			this.toolTypeFld.setRequiredError(UserToolsManagerWindow.this.messageSource
					.getMessage(Message.FORM_VALIDATION_USER_TOOLS_TYPE_REQUIRED));

			this.nameFld.setNullRepresentation("");
			this.titleFld.setNullRepresentation("");
			this.parameterFld.setNullRepresentation("");
			this.versionFld.setNullRepresentation("");

			// init validators here
			this.nameFld.addValidator(new Validator() {

				private static final long serialVersionUID = -2137448642107085498L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if (!this.isValid(value)) {
						throw new InvalidValueException(UserToolsManagerWindow.this.messageSource
								.getMessage(Message.FORM_VALIDATION_ALPHANUM_ONLY));
					}

				}

				@Override
				public boolean isValid(Object value) {
					return value.toString().matches("^[a-zA-Z0-9\\-_]{0,40}$");
				}
			});

			this.filePicker = new ServerFilePicker(parentWindow);
			this.filePicker.setCaption(UserToolsManagerWindow.this.messageSource.getMessage(Message.TOOL_PATH));
			this.filePicker.setWidth("190px");
			this.filePicker.setRequired(true);
			this.filePicker.setRequiredError(UserToolsManagerWindow.this.messageSource
					.getMessage(Message.FORM_VALIDATION_USER_TOOLS_PATH_REQUIRED));

			this.filePicker.addValidator(new Validator() {

				private static final long serialVersionUID = -8613371582243919131L;

				@Override
				public void validate(Object value) throws InvalidValueException {
					if (!this.isValid(value)) {
						throw new InvalidValueException(UserToolsManagerWindow.this.messageSource
								.getMessage(Message.FORM_VALIDATION_INVALID_URL));
					} else {
						UserToolsFormFieldFactory.this.filePicker.setComponentError(null);

					}

				}

				@Override
				public boolean isValid(Object value) {

					try {
						UserToolsManagerWindow.LOG.debug("value: " + value.toString());

						if (UserToolsFormFieldFactory.this.toolTypeFld.getValue().toString().toLowerCase().contains("web")) {
							// this is a web tool, validate to URL
							if (!value.toString().matches("^(https?|ftp|file)://.+$")) {
								return false;
							}
						} else {
							if (!value
									.toString()
									.matches(
											"^(?:[a-zA-Z]\\:(\\\\|\\/)|file\\:\\/\\/|\\\\\\\\|\\.(\\/|\\\\))([^\\\\\\/\\:\\*\\?\\<\\>\\\"\\|]+(\\\\|\\/){0,1})+$")) {
								return false;
							}
						}
					} catch (Exception e) {
						return false;
					}
					return true;
				}
			});
		}

		@Override
		public Field createField(Item item, Object propertyId, Component uiContext) {
			if ("toolType".equals(propertyId)) {
				return this.toolTypeFld;
			} else if ("toolName".equals(propertyId)) {
				return this.nameFld;
			} else if ("title".equals(propertyId)) {
				return this.titleFld;
			} else if ("version".equals(propertyId)) {
				return this.versionFld;
			} else if ("path".equals(propertyId)) {
				return this.filePicker;
			} else if ("parameter".equals(propertyId)) {
				return this.parameterFld;
			}

			return null;
		}
	}
}
