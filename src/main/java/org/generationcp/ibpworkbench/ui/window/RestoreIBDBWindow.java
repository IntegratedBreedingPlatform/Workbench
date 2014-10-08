package org.generationcp.ibpworkbench.ui.window;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.actions.RestoreIBDBSaveAction;
import org.generationcp.ibpworkbench.ui.dashboard.WorkbenchDashboard;
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

import java.text.SimpleDateFormat;
import java.util.List;

@Configurable
public class RestoreIBDBWindow extends BaseSubWindow implements InitializingBean, InternationalizableComponent {
    
	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchDashboard.class);
    
	private static final long serialVersionUID = 1L;
    
    private static final String VERSION = "1.1.3.10";
    

    private Project project;
        
    // Components
	private ComponentContainer rootLayout;
	private Button cancelBtn;
	private Button saveBtn;
	
	private static final String WINDOW_WIDTH = "400px";
	private static final String WINDOW_HEIGHT = "430px";
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private SessionData sessionData;

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
    	User currentUser = sessionData.getUserData();
    	
    	try {
			projects = workbenchDataManager.getProjectsByUser(currentUser);
		
			// set the Project Table data source
	        BeanContainer<String, Project> projectContainer = new BeanContainer<String, Project>(Project.class);
	        projectContainer.setBeanIdProperty("projectName");
	        for (Project project : projects) {
	            projectContainer.addBean(project);
	        }
    	
	        projectBackupContainer = new BeanContainer<String, ProjectBackup>(ProjectBackup.class);
	        projectBackupContainer.setBeanIdProperty("projectBackupId");

	        table.setContainerDataSource(projectBackupContainer);
	        table.setVisibleColumns(new String[] {"backupTime","backupPath"});
	        table.setColumnHeader("backupTime","Backup Time");
	        table.setColumnHeader("backupPath","Backup Path");
	        
	        // init table contents
	        Project p = sessionData.getSelectedProject();
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
		saveBtn = new Button("Restore");
        saveBtn.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		saveBtn.setSizeUndefined();

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

		rootLayout.addComponent(new Label(messageSource.getMessage(Message.RESTORE_IBDB_TABLE_SELECT_CAPTION)));
		
		rootLayout.addComponent(table);
		
		// bind components to layout

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setSpacing(true);
		hl.setMargin(true);
		
		
		Label spacer = new Label("&nbsp;",Label.CONTENT_XHTML);
		spacer.setWidth("100%");
		
		
		hl.addComponent(spacer);
		hl.addComponent(cancelBtn);
        hl.addComponent(saveBtn);

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
    	
    	// Table actions
    	table.addListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				RestoreIBDBWindow.LOG.info("Backup Table > Item selected");
				
				saveBtn.setEnabled(true);
			}
    	});
    	
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
		messageSource.setCaption(saveBtn,Message.RESTORE);
		messageSource.setCaption(cancelBtn,Message.CANCEL);
		
		messageSource.setCaption(upload,Message.UPLOAD_IBDB_CAPTION);
	}
}
