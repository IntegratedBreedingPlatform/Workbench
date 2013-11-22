package org.generationcp.ibpworkbench.ui.window;

import java.text.SimpleDateFormat;
import java.util.List;

import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.RestoreIBDBSaveAction;
import org.generationcp.ibpworkbench.ui.dashboard.WorkbenchDashboard;
import org.generationcp.ibpworkbench.ui.common.ConfirmDialog;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectBackup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;

@Configurable
public class RestoreIBDBWindow extends Window implements InitializingBean, InternationalizableComponent {
    
	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchDashboard.class);
    
	private static final long serialVersionUID = 1L;
    
    private static final String VERSION = "1.1.3.10";
    

    private Project project;
        
    // Components
	private ComponentContainer rootLayout;
	//private FormLayout formLayout;
	//private Select select;
	private Button cancelBtn;
	private Button saveBtn;
	
	private static final String WINDOW_WIDTH = "400px";
	private static final String WINDOW_HEIGHT = "400px";
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

	private List<Project> projects;

	private Table table;

	private BeanContainer<String, ProjectBackup> projectBackupContainer;

	private Upload upload;

    public RestoreIBDBWindow(Project project) {
    	this.project = project;
    }

    /**
     * Assemble the UI after all dependencies has been set.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();
    }
    
    protected void initializeData() {
    	User currentUser = IBPWorkbenchApplication.get().getSessionData().getUserData();
    	
    	try {
			projects = workbenchDataManager.getProjectsByUser(currentUser);
		
			// set the Project Table data source
	        BeanContainer<String, Project> projectContainer = new BeanContainer<String, Project>(Project.class);
	        projectContainer.setBeanIdProperty("projectName");
	        for (Project project : projects) {
	            projectContainer.addBean(project);
	        }
    	
	        //select.setContainerDataSource(projectContainer);
	        //select.setValue(select.getItemIds().iterator().next());
	        
	        projectBackupContainer = new BeanContainer<String, ProjectBackup>(ProjectBackup.class);
	        projectBackupContainer.setBeanIdProperty("projectBackupId");
	        
	        /*
	        String _log = "";
	        for (String i : projectBackupContainer.getContainerPropertyIds()) {
	        	_log +=  i + ", ";
	        }
	        
	        LOG.debug(_log);
	        */
	        
	        table.setContainerDataSource(projectBackupContainer);
	        table.setVisibleColumns(new String[] {"backupTime","backupPath"});
	        table.setColumnHeader("backupTime","Backup Time");
	        table.setColumnHeader("backupPath","Backup Path");
	        
	        // init table contents
	        Project p = IBPWorkbenchApplication.get().getSessionData().getSelectedProject();
	        for (ProjectBackup pb : workbenchDataManager.getProjectBackups(p)) {
	        	projectBackupContainer.addBean(pb);
	        }
	        
	        if (table.getItemIds().isEmpty())
	        	saveBtn.setEnabled(false);
	        
	        table.setValue(table.firstItemId());
	        
    	} catch (MiddlewareQueryException e) {
    		 LOG.error("Exception", e);
             throw new InternationalizableException(e, 
                     Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
		}
    }
    
    protected void initializeComponents() throws Exception {
		//select = new Select("Choose a project to restore");
		//select.setNullSelectionAllowed(false);
		//select.setNewItemsAllowed(false);
		//select.setFilteringMode(Select.FILTERINGMODE_OFF);
		//select.setImmediate(true);
		
		saveBtn = new Button("Restore");
        saveBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		saveBtn.setSizeUndefined();
		//saveBtn.setEnabled(false);
		
		cancelBtn = new Button("Cancel");
		cancelBtn.setSizeUndefined();
    
		// Backup Table
		table = new Table() {
			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property property) {

				if (property.getType() == java.util.Date.class) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				    return property.getValue() == null ? "" : sdf.format((java.util.Date) property.getValue());
				}
				
				return super.formatPropertyValue(rowId, colId, property);
			}
		};
		
		table.setSelectable(true);
		table.setImmediate(true);
		table.setWidth("100%");
		table.setHeight("200px");
		
		upload = new Upload("Or upload an IB local backup file here:",null);
		
		
    }

    protected void initializeLayout() {
        this.addStyleName(Reindeer.WINDOW_LIGHT);

		this.setCaption("Restore IB Database");
		this.setWidth(WINDOW_WIDTH);
		this.setHeight(WINDOW_HEIGHT);
		this.setResizable(false);
		this.setModal(true);
				
		rootLayout = this.getContent();
		
		//formLayout = new FormLayout();
		//formLayout.setMargin(true);
		//formLayout.setSizeFull();
		
		//rootLayout.addComponent(formLayout);
		
		rootLayout.addComponent(new Label(messageSource.getMessage(Message.RESTORE_IBDB_TABLE_SELECT_CAPTION)));
		
		rootLayout.addComponent(table);
		
		// bind components to layout
		//formLayout.addComponent(select);
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setSpacing(true);
		hl.setMargin(true);
		
		
		Label spacer = new Label("&nbsp;",Label.CONTENT_XHTML);
		spacer.setWidth("100%");
		
		
		hl.addComponent(spacer);
		hl.addComponent(saveBtn);
		hl.addComponent(cancelBtn);
		
		hl.setComponentAlignment(saveBtn, Alignment.MIDDLE_RIGHT);
		hl.setComponentAlignment(cancelBtn, Alignment.MIDDLE_RIGHT);
		hl.setExpandRatio(spacer,1.0f);
		
		rootLayout.addComponent(hl);
		
		// add upload
		rootLayout.addComponent(upload);
		
    }

    protected void initializeActions() {
    	
    	final RestoreIBDBSaveAction restoreAction = new RestoreIBDBSaveAction(project, table, this);
    	
    	// DO button listeners + actions here
    	cancelBtn.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				event.getButton().getWindow().getParent().removeWindow(event.getButton().getWindow());
			}
		});
    
    	
    	saveBtn.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				final Window sourceWindow = event.getButton().getWindow();
				
				ConfirmDialog.show(sourceWindow.getParent(),
						messageSource.getMessage(Message.RESTORE_IBDB_WINDOW_CAPTION),
						messageSource.getMessage(Message.RESTORE_IBDB_CONFIRM),
						messageSource.getMessage(Message.RESTORE),
						messageSource.getMessage(Message.CANCEL),
						restoreAction);
			}
		});
    	
    	//saveBtn.addListener(new RestoreIBDBSaveAction(select,table));
    	
    	// Select action
    	/*
    	select.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				RestoreIBDBWindow.LOG.info("Select > Item selected : " + event.getProperty().getValue());
				
				Project p = ((BeanItem<Project>)select.getItem(select.getValue())).getBean();
				
				projectBackupContainer.removeAllItems();
				
				try {
					for (ProjectBackup pb : workbenchDataManager.getProjectBackups(p)) {
			        	projectBackupContainer.addBean(pb);
			        }
					
					if (table.getItemIds().size() > 0)
						table.setValue(table.firstItemId());
					
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MiddlewareQueryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		});
    	*/
    	
    	// Table actions
    	table.addListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				RestoreIBDBWindow.LOG.info("Backup Table > Item selected");
				
				saveBtn.setEnabled(true);
			}
    	});
    	
    	upload.setReceiver(restoreAction);
    	upload.addListener(restoreAction);
    }

    protected void assemble() throws Exception {
        initializeComponents();
        initializeLayout();
        initializeData();
        initializeActions();
    }

    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }

	@Override
	public void updateLabels() {
		messageSource.setCaption(this, Message.RESTORE_IBDB_WINDOW_CAPTION);
		//messageSource.setCaption(select, Message.RESTORE_IBDB_TABLE_SELECT_CAPTION);
		messageSource.setCaption(saveBtn,Message.RESTORE);
		messageSource.setCaption(cancelBtn,Message.CANCEL);
		
		messageSource.setCaption(upload,Message.UPLOAD_IBDB_CAPTION);
	}
}
