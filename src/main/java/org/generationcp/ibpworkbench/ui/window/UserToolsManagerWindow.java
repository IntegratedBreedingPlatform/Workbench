package org.generationcp.ibpworkbench.ui.window;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.dashboard.WorkbenchDashboard;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
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
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Configurable
public class UserToolsManagerWindow extends Window implements InitializingBean {

	private Form userToolsForm;
	private Button addBtn;
	private Button cancelBtn;
	private ListSelect userToolsListSelect;
	
	//private Tool userToolFormData = new Tool();
	
	private Window thisWindow;
	
	@Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchDashboard.class);
	private static final String WIDTH = "780px";
	private static final String HEIGHT = "420px";
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
	private Button editBtn;
	private BeanItemContainer<Tool> userToolsListContainer;
    
    
	@Override
	public void afterPropertiesSet() throws Exception {
		assemble();
	}

	protected void assemble() throws MiddlewareQueryException {
		
		initializeComponents();
		initializeData();
		initializeLayout();
		initializeActions();
	}
	
	private void initializeActions() {
		
		// ADD ACTION
		final ConfirmDialog.Listener onAddAction = new ConfirmDialog.Listener() {
			
			@Override
			public void onClose(ConfirmDialog dialog) {
				// USER did not continue
				if (!dialog.isConfirmed())
					return;
				
				try {
					userToolsForm.commit();
				} catch (InvalidValueException e) {
					MessageNotifier.showError(thisWindow,messageSource.getMessage(Message.FORM_VALIDATION_FAIL),
							messageSource.getMessage(Message.FORM_VALIDATION_FIELDS_INVALID));
					return;
				}

				BeanItem<Tool> userToolBeanItem = (BeanItem<Tool>) userToolsForm.getItemDataSource();
				Tool userToolFormData = userToolBeanItem.getBean();
				
				LOG.debug(userToolFormData.toString());
				
				userToolFormData.setToolId(null);

				if (userToolFormData.getParameter() == null)
					userToolFormData.setParameter("");
			
				userToolFormData.setUserTool(true);
				
				if (userToolFormData.getVersion() == null)
					userToolFormData.setVersion("");

				try {
					workbenchDataManager.getToolDao().save(userToolFormData);
					
					Tool newTool = workbenchDataManager.getToolDao().getByToolName(userToolFormData.getToolName());
					
					userToolsListContainer.addItemAt(0,newTool);
					
					//userToolsListSelect.select(newTool);
					// clear the form data for new entry
					userToolsForm.setItemDataSource(new BeanItem<Tool>(new Tool()),Arrays.asList(new String[] {"toolName","title","toolType","version","path","parameter"}));
					
					
					MessageNotifier.showMessage(thisWindow,messageSource.getMessage(Message.SUCCESS),messageSource.getMessage(Message.USER_TOOLS_ADDED));
					
				} catch (MiddlewareQueryException e) {
					MessageNotifier.showError(thisWindow,messageSource.getMessage(Message.FAIL),messageSource.getMessage(Message.USER_TOOLS_ADD_EXISTS_ERROR));
				}
					
			}
		};
		
		// EDIT ACTION
		final ConfirmDialog.Listener onEditAction = new ConfirmDialog.Listener() {
			
			@Override
			public void onClose(ConfirmDialog dialog) {
				
				// USER did not continue
				if (!dialog.isConfirmed())
					return;
				
				final Tool selected = (Tool) userToolsListSelect.getValue();

				try {
					Collection<Tool> toolList = userToolsListContainer.getItemIds();
					
					BeanItem<Tool> userToolBeanItem = (BeanItem<Tool>) userToolsForm.getItemDataSource();
					Tool userToolFormData = userToolBeanItem.getBean();
					
					
					for (Tool t : toolList) {
						if (t.getToolName().equals(userToolFormData.getToolName()) && t.getToolId() != userToolFormData.getToolId()) {
							MessageNotifier.showError(thisWindow,messageSource.getMessage(Message.FAIL),messageSource.getMessage(Message.USER_TOOLS_ADD_EXISTS_ERROR));
							return;
						}
					}
					
					selected.setToolId(userToolFormData.getToolId());
					
					if (userToolFormData.getParameter() == null)
						selected.setParameter("");
					else
						selected.setParameter(userToolFormData.getParameter());
						selected.setPath(userToolFormData.getPath());
						selected.setTitle(userToolFormData.getTitle());
						selected.setToolName(userToolFormData.getToolName());
						selected.setToolType(userToolFormData.getToolType());
						selected.setUserTool(true);
						selected.setVersion(userToolFormData.getVersion());
					
						
					workbenchDataManager.getToolDao().update(selected);
						
					userToolsListContainer.addItem(selected);
					userToolsListSelect.setItemCaption(selected,userToolFormData.getTitle());
					
					MessageNotifier.showMessage(thisWindow,messageSource.getMessage(Message.SUCCESS),messageSource.getMessage(Message.USER_TOOLS_UPDATED));
										
				} catch (MiddlewareQueryException e) {
					MessageNotifier.showError(thisWindow,messageSource.getMessage(Message.FAIL),messageSource.getMessage(Message.USER_TOOLS_EDIT_EXISTS_ERROR));
				}
			}
		};
		
		// LISTENERS REGISTRATION
		
		addBtn.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				userToolsForm.setComponentError(null);
				userToolsForm.setValidationVisible(false);
				
				try {
					userToolsForm.commit();
				} catch (InvalidValueException e) {
					MessageNotifier.showError(thisWindow,messageSource.getMessage(Message.FORM_VALIDATION_FAIL),
							messageSource.getMessage(Message.FORM_VALIDATION_FIELDS_INVALID));
					return;
				}
				
				// TODO FIXME Internationalize the messages
				ConfirmDialog.show(UserToolsManagerWindow.this.getParent(),
						messageSource.getMessage(Message.USER_TOOLS_WINDOW_CAPTION),
						"Click OK to proceed",
						messageSource.getMessage(Message.OK),
						messageSource.getMessage(Message.CANCEL),
						onAddAction
				);
			}
		});

		editBtn.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				userToolsForm.setComponentError(null);
				userToolsForm.setValidationVisible(false);
				
				try {
					userToolsForm.commit();
				} catch (InvalidValueException e) {
					MessageNotifier.showError(thisWindow,messageSource.getMessage(Message.FORM_VALIDATION_FAIL),
							messageSource.getMessage(Message.FORM_VALIDATION_FIELDS_INVALID));
					return;
				}
				
				// TODO FIXME Internationalize the messages
				ConfirmDialog.show(UserToolsManagerWindow.this.getParent(),
						messageSource.getMessage(Message.USER_TOOLS_WINDOW_CAPTION),
						"Click OK to proceed",
						messageSource.getMessage(Message.OK),
						messageSource.getMessage(Message.CANCEL),
						onEditAction);
			}
		});
		
		cancelBtn.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				thisWindow.getApplication().getMainWindow().removeWindow(thisWindow);
			}
		});

		userToolsListSelect.addListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				// event.getProperty returns the itemID of the selected item
				
				//Tool selectedTool = (Tool)event.getProperty().getValue();
			
				Tool selectedTool = (Tool)event.getProperty().getValue();
						
				
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
				
				LOG.debug(selectedTool.toString());
				
				userToolsForm.setItemDataSource(new BeanItem<Tool>(userToolFormData),Arrays.asList(new String[] {"toolName","title","toolType","version","path","parameter"}));
				
				
				editBtn.setEnabled(true);
			}
		});
	}

	private void initializeLayout() {
        this.addStyleName(Reindeer.WINDOW_LIGHT);
		this.setModal(true);
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setCaption(messageSource.getMessage(Message.USER_TOOLS_WINDOW_CAPTION));
		
		HorizontalLayout mainPanel = new HorizontalLayout();
		Label spacer2 = new Label("&nbsp;",Label.CONTENT_XHTML);
		spacer2.setWidth("20px");
		
		VerticalLayout vl = new VerticalLayout();
		vl.setCaption(messageSource.getMessage(Message.TOOL_NAME));
		vl.addComponent(userToolsListSelect);
		vl.setWidth("100%");
		
		mainPanel.addComponent(vl);
		mainPanel.addComponent(spacer2);
		mainPanel.addComponent(userToolsForm);
		mainPanel.setSpacing(true);
		mainPanel.setMargin(false,true,false,false);
		mainPanel.setWidth("100%");
		mainPanel.setExpandRatio(vl,1.0f);
		
		Label spacer = new Label("&nbsp;",Label.CONTENT_XHTML);
		
		HorizontalLayout btnPanel = new HorizontalLayout();
		btnPanel.setWidth("100%");
		btnPanel.setSpacing(true);
		btnPanel.setMargin(true);
		
		btnPanel.addComponentAsFirst(spacer);
		btnPanel.addComponent(cancelBtn);
        btnPanel.addComponent(addBtn);
        btnPanel.addComponent(editBtn);

        btnPanel.setComponentAlignment(addBtn, Alignment.MIDDLE_RIGHT);
		btnPanel.setComponentAlignment(editBtn, Alignment.MIDDLE_RIGHT);
		btnPanel.setComponentAlignment(cancelBtn, Alignment.MIDDLE_RIGHT);
		btnPanel.setExpandRatio(spacer,1.0f);
		
		this.getContent().addComponent(mainPanel);
		this.getContent().addComponent(btnPanel);
	}

	private void initializeData() {

		try {
			List<Tool> userTools = workbenchDataManager.getToolDao().getUserTools();
			
			userToolsListContainer = new BeanItemContainer<Tool>(Tool.class,userTools);
			userToolsListSelect.setContainerDataSource(userToolsListContainer);
			userToolsForm.setItemDataSource(new BeanItem<Tool>(new Tool()),Arrays.asList(new String[] {"toolName","title","toolType","version","path","parameter"}));
			userToolsForm.setWriteThrough(false);
			/*
			if (userTools.size() > 0) {
				Tool item = userTools.iterator().next();
				userToolsListSelect.setValue(item);
				userToolsForm.setItemDataSource(new BeanItem<Tool>(item));
			}*/
				
			userToolsListSelect.setItemCaptionPropertyId("title");
			
			userToolsForm.setVisibleItemProperties(new String[] {"toolName","title","toolType","version","path","parameter"});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		}
	}

	private void initializeComponents() {
		if (thisWindow == null)
			thisWindow = this;
		
		this.setResizable(false);
		
		
		userToolsForm = new Form();
			
		userToolsForm.setWidth("380px");
		userToolsForm.setHeight("250px");
		userToolsForm.setFormFieldFactory(new UserToolsFormFieldFactory(thisWindow));
		//userToolsForm.setValidationVisibleOnCommit(false);
		//userToolsForm.setImmediate(true);
		//userToolsForm.setCaption(messageSource.getMessage(Message.USER_TOOLS_FORM_CAPTION));
		
		addBtn = new Button(messageSource.getMessage(Message.ADD),userToolsForm,"commit");
		addBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

        editBtn = new Button(messageSource.getMessage(Message.EDIT),userToolsForm,"commit");
		editBtn.setEnabled(false);
		editBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());


		cancelBtn = new Button(messageSource.getMessage(Message.CANCEL),userToolsForm,"discard");

		userToolsListSelect = new ListSelect();
		userToolsListSelect.setWidth("100%");
		userToolsListSelect.setNullSelectionAllowed(false);
		
		userToolsListSelect.setImmediate(true);
		
		
	}

	class UserToolsFormFieldFactory implements FormFieldFactory {
		private NativeSelect toolTypeFld;
		private TextField nameFld;
		private TextField titleFld;
		private TextField versionFld;
		//private TextField pathFld;
		private TextField parameterFld;
		private ServerFilePicker filePicker;

		public UserToolsFormFieldFactory(Window parentWindow) {
			//TODO: replace with internationalized messages
			nameFld = new TextField(messageSource.getMessage(Message.NAME));
			nameFld.setWidth("190px");
			nameFld.setRequired(true);
			nameFld.setRequiredError(messageSource.getMessage(Message.FORM_VALIDATION_USER_TOOLS_NAME_REQUIRED));
			
			titleFld = new TextField(messageSource.getMessage(Message.TITLE));
			titleFld.setWidth("190px");
			titleFld.setRequired(true);
			titleFld.setRequiredError(messageSource.getMessage(Message.FORM_VALIDATION_USER_TOOLS_TITLE_REQUIRED));
			
			parameterFld = new TextField(messageSource.getMessage(Message.PARAMETER));
			parameterFld.setWidth("190px");
			
			versionFld = new TextField(messageSource.getMessage(Message.VERSION));
			versionFld.setWidth("80px");
			
			toolTypeFld = new NativeSelect(messageSource.getMessage(Message.TOOL_TYPE),Arrays.asList(new ToolType[] {ToolType.WEB,ToolType.WEB_WITH_LOGIN,ToolType.NATIVE}));
			toolTypeFld.setWidth("120px");
			toolTypeFld.setRequired(true);
			toolTypeFld.setRequiredError(messageSource.getMessage(Message.FORM_VALIDATION_USER_TOOLS_TYPE_REQUIRED));			
			
			nameFld.setNullRepresentation("");
			titleFld.setNullRepresentation("");
			//pathFld.setNullRepresentation("");
			parameterFld.setNullRepresentation("");
			versionFld.setNullRepresentation("");

			// init validators here
			//pathFld.addValidator(this.initPathValidator());
			nameFld.addValidator(new Validator() {

				@Override
				public void validate(Object value) throws InvalidValueException {
					if (!this.isValid(value))
						throw new InvalidValueException(messageSource.getMessage(Message.FORM_VALIDATION_ALPHANUM_ONLY));
					
				}

				@Override
				public boolean isValid(Object value) {
					return value.toString().matches("^[a-zA-Z0-9\\-_]{0,40}$");
				}});
			
			
			filePicker = new ServerFilePicker(parentWindow);
			filePicker.setCaption(messageSource.getMessage(Message.TOOL_PATH));
			filePicker.setWidth("190px");
			filePicker.setRequired(true);
			filePicker.setRequiredError(messageSource.getMessage(Message.FORM_VALIDATION_USER_TOOLS_PATH_REQUIRED));
			//filePicker.setImmediate(true);
			
			
			filePicker.addValidator(new Validator() {
				
				@Override
				public void validate(Object value) throws InvalidValueException {
					if (!this.isValid(value)) {
					
						//MessageNotifier.showError(UserToolsManagerWindow.this,messageSource.getMessage(Message.ERROR),messageSource.getMessage(Message.FORM_VALIDATION_INVALID_URL));
						
						throw new InvalidValueException(messageSource.getMessage(Message.FORM_VALIDATION_INVALID_URL));
					} else {
						filePicker.setComponentError(null);
						
					}
				
					
				}
				
				@Override
				public boolean isValid(Object value) {
					
					try {
						LOG.debug("value: " + value.toString());
						
					if (toolTypeFld.getValue().toString().toLowerCase().contains("web")) {
						// this is a web tool, validate to URL
						if (!value.toString().matches("^(https?|ftp|file)://.+$")) {
							return false;
						}
					} else {
						if (!value.toString().matches("^(?:[a-zA-Z]\\:(\\\\|\\/)|file\\:\\/\\/|\\\\\\\\|\\.(\\/|\\\\))([^\\\\\\/\\:\\*\\?\\<\\>\\\"\\|]+(\\\\|\\/){0,1})+$")) {
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
			if ( "toolType".equals((String)propertyId) ) {
				return toolTypeFld;
			}
			else if ("toolName".equals((String)propertyId)) {
				return nameFld;
			}
			else if ("title".equals((String)propertyId)) {
				return titleFld;
			}
			else if ("version".equals((String)propertyId)) {
				return versionFld;
			}
			else if ("path".equals((String)propertyId)) {
				return filePicker;
			}
			else if ("parameter".equals((String)propertyId)) {
				return parameterFld;
			}
			
			return null;
		}		
	}
}
