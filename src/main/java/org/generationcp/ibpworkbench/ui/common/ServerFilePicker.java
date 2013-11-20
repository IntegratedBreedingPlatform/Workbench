package org.generationcp.ibpworkbench.ui.common;

import java.io.File;
import java.io.FilenameFilter;

import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.ui.common.customfield.CustomField;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@Configurable
public class ServerFilePicker extends CustomField implements InitializingBean {
	private HorizontalLayout root;
	private TextField pathFld;
	private Button browseBtn;
	private Window pickerWindow;
	private Label pathLbl;
	private FilesystemContainer fsContainer;
	private TreeTable treetable;
	private Window parentWin;
	
	@Autowired
    private SimpleResourceBundleMessageSource messageSource;
    

	public ServerFilePicker(Window parentWindow) {
		this.parentWin = parentWindow;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		root = new HorizontalLayout();
		root.setSpacing(true);
		
		this.setCompositionRoot(root);
		
		pathFld = new TextField();
		pathLbl = new Label();
		browseBtn = new Button("Browse");
		pathFld.setNullRepresentation("");
	
		root.addComponent(pathFld);
		root.addComponent(browseBtn);
		
		
		
		buildFilePicker();
		
		this.setPropertyDataSource(pathFld.getPropertyDataSource());
			
		browseBtn.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				//LOG.debug("pause here");
				
				
				//parentWin.addWindow(pickerWindow);
				parentWin.getParent().addWindow(pickerWindow);
			}
		});
		
	}

	private void buildFilePicker() {
		pickerWindow = new Window("Select an executable file");
        pickerWindow.addStyleName(Reindeer.WINDOW_LIGHT);
		pickerWindow.center();
		pickerWindow.setModal(true);
		
		pickerWindow.setWidth("500px");
		pickerWindow.setHeight("400px");
		pickerWindow.setResizable(false);

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

        selectBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

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
				ServerFilePicker.this.setValue((new File(treetable.getValue().toString())).getAbsolutePath());
				parentWin.getApplication().getMainWindow().removeWindow(pickerWindow);				}
		});
	}
	
	public TextField getPathField() {
		return pathFld;
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
	public Class<?> getType() {
		return pathFld.getType();
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
	
	@Override
	public void addValidator(Validator validator) {
		//this.getPathField().addValidator(validator);
		super.addValidator(validator);
	}
		
	@Override
	protected boolean isEmpty() {
		
		boolean isEmpty;
		
		
		if (this.getPathField().getValue() == null) {
			isEmpty = true;
		}
		else { isEmpty = this.getPathField().getValue().toString().isEmpty(); }
		
		
		return isEmpty;
	}
	
	@Override
    public void validate() throws Validator.InvalidValueException {
		super.validate();
    }
	
	@Override
	public void setRequired(boolean required) {
		super.setRequired(required);
		//this.getPathField().setRequired(true);
	}
	
	@Override
	public void setRequiredError(String requiredMessage) {
		super.setRequiredError(requiredMessage);
	}
	
	
	
	
}
