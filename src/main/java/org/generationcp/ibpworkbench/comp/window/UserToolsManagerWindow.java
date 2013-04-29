package org.generationcp.ibpworkbench.comp.window;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.comp.WorkbenchDashboard;
import org.generationcp.ibpworkbench.comp.common.customfield.CustomField;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Tool;
import org.generationcp.middleware.pojos.workbench.ToolType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.MessageSourceResolvable;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Configurable
public class UserToolsManagerWindow extends Window implements InitializingBean {

	private Form userToolsForm;
	private Button addBtn;
	private Button cancelBtn;
	private ListSelect userToolsListSelect;
	
	private Tool userToolFormData = new Tool();
	
	private Window thisWindow;
	
	@Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchDashboard.class);
	private static final String WIDTH = "780px";
	private static final String HEIGHT = "350px";
    
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
		addBtn.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				
				try {
					userToolsForm.commit();
				} catch (InvalidValueException e) {
					MessageNotifier.showError(thisWindow,messageSource.getMessage(Message.FORM_VALIDATION_FAIL),
							messageSource.getMessage(Message.FORM_VALIDATION_FIELDS_INVALID));
					return;
				}
				
				LOG.debug(userToolFormData.toString());
				
				Tool clone = new Tool();
				clone.setToolId(null);
				clone.setParameter(userToolFormData.getParameter());
				clone.setPath(userToolFormData.getPath());
				clone.setTitle(userToolFormData.getTitle());
				clone.setToolName(userToolFormData.getToolName());
				clone.setToolType(userToolFormData.getToolType());
				clone.setUserTool(true);
				clone.setVersion(userToolFormData.getVersion());
				

				try {
					workbenchDataManager.getToolDao().save(clone);
					
					Tool newTool = workbenchDataManager.getToolDao().getByToolName(userToolFormData.getToolName());
					
					userToolsListContainer.addItem(newTool);
					userToolsListSelect.select(newTool);
					
					MessageNotifier.showMessage(thisWindow,messageSource.getMessage(Message.SUCCESS),messageSource.getMessage(Message.USER_TOOLS_ADDED));
					
				} catch (MiddlewareQueryException e) {
					MessageNotifier.showError(thisWindow,messageSource.getMessage(Message.FAIL),messageSource.getMessage(Message.USER_TOOLS_ADD_EXISTS_ERROR));
				}
			}
		});

		editBtn.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {

				try {
					userToolsForm.commit();
				} catch (InvalidValueException e) {
					MessageNotifier.showError(thisWindow,messageSource.getMessage(Message.FORM_VALIDATION_FAIL),
							messageSource.getMessage(Message.FORM_VALIDATION_FIELDS_INVALID));
					return;
				}
				
				userToolsForm.commit();

				try {
					Tool selected = (Tool) userToolsListSelect.getValue();
					//userToolsListContainer.removeItem(selected);

					Collection<Tool> toolList = userToolsListContainer.getItemIds();
					
					for (Tool t : toolList) {
						if (t.getToolName().equals(userToolFormData.getToolName()) && t.getToolId() != userToolFormData.getToolId()) {
							MessageNotifier.showError(thisWindow,messageSource.getMessage(Message.FAIL),messageSource.getMessage(Message.USER_TOOLS_ADD_EXISTS_ERROR));
							return;
						}
					}
					
					
					selected.setToolId(userToolFormData.getToolId());
					selected.setParameter(userToolFormData.getParameter());
					selected.setPath(userToolFormData.getPath());
					selected.setTitle(userToolFormData.getTitle());
					selected.setToolName(userToolFormData.getToolName());
					selected.setToolType(userToolFormData.getToolType());
					selected.setUserTool(true);
					selected.setVersion(userToolFormData.getVersion());
					
					userToolsListContainer.addItem(selected);
					userToolsListSelect.setItemCaption(selected,userToolFormData.getTitle());
					
					workbenchDataManager.getToolDao().update(selected);
					
					MessageNotifier.showMessage(thisWindow,messageSource.getMessage(Message.SUCCESS),messageSource.getMessage(Message.USER_TOOLS_UPDATED));
										
				} catch (MiddlewareQueryException e) {
					MessageNotifier.showError(thisWindow,messageSource.getMessage(Message.FAIL),messageSource.getMessage(Message.USER_TOOLS_EDIT_EXISTS_ERROR));
				}
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
				userToolFormData.setParameter(selectedTool.getParameter());
				userToolFormData.setPath(selectedTool.getPath());
				userToolFormData.setTitle(selectedTool.getTitle());
				userToolFormData.setToolId(selectedTool.getToolId());
				userToolFormData.setToolName(selectedTool.getToolName());
				userToolFormData.setToolType(selectedTool.getToolType());
				userToolFormData.setVersion(selectedTool.getVersion());
				userToolFormData.setUserTool(selectedTool.getUserTool());
				
				// hack: access the customField path directly to set the value since it is not updated even if the formBean is changed
				userToolsForm.getField("path").setValue(selectedTool.getPath());
				
				userToolsForm.commit();
				
				
				//userToolsForm.setItemDataSource(new BeanItem<Tool>(userToolFormData));
				
				editBtn.setEnabled(true);
			}
		});
	}

	private void initializeLayout() {
		this.setModal(true);
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setCaption(messageSource.getMessage(Message.USER_TOOLS_WINDOW_CAPTION));
		
		ComponentContainer rootLayout = this.getContent();
		
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
		btnPanel.addComponent(addBtn);
		btnPanel.addComponent(editBtn);
		btnPanel.addComponent(cancelBtn);
		
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
			userToolsForm.setItemDataSource(new BeanItem<Tool>(userToolFormData));
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
		userToolsForm.setFormFieldFactory(new UserToolsFormFieldFactory(thisWindow));
		//userToolsForm.setCaption(messageSource.getMessage(Message.USER_TOOLS_FORM_CAPTION));
		
		addBtn = new Button(messageSource.getMessage(Message.ADD),userToolsForm,"commit");
		editBtn = new Button(messageSource.getMessage(Message.EDIT),userToolsForm,"commit");
		editBtn.setEnabled(false);
		
		cancelBtn = new Button(messageSource.getMessage(Message.CANCEL),userToolsForm,"discard");

		userToolsListSelect = new ListSelect();
		userToolsListSelect.setWidth("100%");
		userToolsListSelect.setNullSelectionAllowed(false);
		
		userToolsListSelect.setImmediate(true);
		
		
	}

	class UserToolsFormFieldFactory implements FormFieldFactory {
		private Field toolTypeFld;
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
			filePicker.addValidator(new Validator() {
				
				@Override
				public void validate(Object value) throws InvalidValueException {
					if (!this.isValid(value))
						throw new InvalidValueException(messageSource.getMessage(Message.FORM_VALIDATION_INVALID_URL));
				}
				
				@Override
				public boolean isValid(Object value) {
					
					if (!value.toString().matches("^(?:[a-zA-Z]\\:(\\\\|\\/)|file\\:\\/\\/|\\\\\\\\|\\.(\\/|\\\\))([^\\\\\\/\\:\\*\\?\\<\\>\\\"\\|]+(\\\\|\\/){0,1})+$")) {
						if (!value.toString().matches("^(https?|ftp|file)://.+$")) {
							return false;
						}
					}
					return true;
				}
			});
			
			filePicker.getPathField().setRequired(true);
			filePicker.getPathField().setRequiredError(messageSource.getMessage(Message.FORM_VALIDATION_USER_TOOLS_PATH_REQUIRED));
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
	
	
	// TODO: Refactor this later into a reusable component
	class ServerFilePicker extends CustomField {
		private HorizontalLayout root;
		private TextField pathFld;
		private Button browseBtn;
		private Window pickerWindow;
		private Label pathLbl;
		private FilesystemContainer fsContainer;
		private TreeTable treetable;
		private Window parentWin;
		
		private ServerFilePicker thisInstance;
		
		public ServerFilePicker(Window parentWindow) {
			thisInstance = this;
			
			this.parentWin = parentWindow;
			
			root = new HorizontalLayout();
			root.setSpacing(true);
			
			this.setCompositionRoot(root);
			
			pathFld = new TextField();
			pathLbl = new Label();
			browseBtn = new Button("Browse");
			pathFld.setNullRepresentation("");
		
			root.addComponent(pathFld);
			root.addComponent(browseBtn);
			
			
			
			initPicker();
			
			this.setPropertyDataSource(pathFld.getPropertyDataSource());
				
			browseBtn.addListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					
					LOG.debug("pause here");
					
					
					//parentWin.addWindow(pickerWindow);
					parentWin.getParent().addWindow(pickerWindow);
				}
			});
		}
		
		@Override
		public void addValidator(Validator validator) {
			//super.addValidator(validator);
			this.getPathField().addValidator(validator);
		}
		
		

		@Override
		public boolean isValid() {
			return this.getPathField().isValid();
		}

		@Override
		public void validate() throws InvalidValueException {
			this.getPathField().validate();
		}

		private void initPicker() {
			pickerWindow = new Window("Select an executable file");
			pickerWindow.center();
			pickerWindow.setModal(true);
			
			pickerWindow.setWidth("500px");
			pickerWindow.setHeight("400px");
			pickerWindow.setResizable(false);
			//pickerWindow.setModal(true);
			
			final HorizontalLayout hl = new HorizontalLayout();
			
			hl.addComponent(new Label("Selected file: "));
			hl.addComponent(pathLbl);
			
			treetable = new TreeTable();
			
			//TODO: refactor this to make this more generic / reusable
			fsContainer = new FilesystemContainer(new File("tools"),new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					File pathName = new File(dir.getAbsolutePath() + File.separator + name);

					if (pathName.isDirectory()) {
						
						for (File children : pathName.listFiles()) {
							 if (this.accept(pathName,children.getName()))
								 return true;
						}
						
					} else if (pathName.getName().endsWith(".exe") || pathName.getName().endsWith(".bat") || pathName.getName().endsWith(".com") || pathName.getName().endsWith(".sh") )
						return true;
					
					return false;
				}
			},true);
	        
	        treetable.setContainerDataSource(fsContainer);
	        
	        // Set the row header icon by the file type
	        treetable.setItemIconPropertyId("Icon");

	        // Do not show the Icon column
	        treetable.setVisibleColumns(new Object[]{"Name", "Size",
	                                                 "Last Modified"});
	        // END-EXAMPLE: datamodel.container.filesystemcontainer.basic
	        
	        treetable.setImmediate(true);
	        treetable.setWidth("100%");
	        treetable.setHeight("240px");
	        //treetable.setSizeFull();
	        treetable.setSelectable(true);
	        treetable.addListener(new Property.ValueChangeListener() {

				@Override
				public void valueChange(
						com.vaadin.data.Property.ValueChangeEvent event) {
					System.out.println(event.getProperty().getValue().toString());
					
					String filePath = event.getProperty().getValue().toString();
					
					if (!(new File(filePath)).isDirectory()) {
						pathLbl.setValue((new File(filePath)).getAbsolutePath());
					} else
						pathLbl.setValue("");
				}
				
			});
	        
	        
	        pickerWindow.addComponent(treetable);
		
	        pickerWindow.addComponent(hl);
	        
	        
	        
	        final HorizontalLayout btnPanel = new HorizontalLayout();
			final Label spacer = new Label("&nbsp;",Label.CONTENT_XHTML);

			final Button selectBtn = new Button(messageSource.getMessage(Message.SELECT));
			final Button cancelSelectBtn = new Button(messageSource.getMessage(Message.CANCEL));
			
			
	        btnPanel.setWidth("100%");
			btnPanel.setSpacing(true);
			btnPanel.setMargin(true);
			
			btnPanel.addComponentAsFirst(spacer);
			btnPanel.addComponent(selectBtn);
			btnPanel.addComponent(cancelSelectBtn);
			
			btnPanel.setComponentAlignment(selectBtn, Alignment.MIDDLE_RIGHT);
			btnPanel.setComponentAlignment(cancelSelectBtn, Alignment.MIDDLE_RIGHT);
			btnPanel.setExpandRatio(spacer,1.0f);
			
			pickerWindow.getContent().addComponent(btnPanel);

			// Listeners:
			cancelSelectBtn.addListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					parentWin.getApplication().getMainWindow().removeWindow(pickerWindow);
				}
			});
			
			selectBtn.addListener(new Button.ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					// TODO Auto-generated method stub
					thisInstance.setValue((new File(treetable.getValue().toString())).getAbsolutePath());
					parentWin.getApplication().getMainWindow().removeWindow(pickerWindow);				}
			});
		}
		
		public TextField getPathField() {
			return pathFld;
		}

		@Override
		public Class<?> getType() {
			return pathFld.getType();
		}

		@Override
		public Object getValue() {
			return pathFld.getValue();
		}
		
		@Override
		public Object getData() {
			return pathFld.getData();
		}
		
		
		@Override
		public void setValue(Object newValue) throws ReadOnlyException,
				ConversionException {
			super.setValue(newValue);
			pathFld.setValue(newValue);
		}
		
		@Override
		protected void setInternalValue(Object newValue) {
			super.setInternalValue(newValue);
			pathFld.setValue(newValue);
		}

		@Override
		public void setWidth(String width) {
			//super.setWidth(width);
			pathFld.setWidth(width);
		}
	}
}
