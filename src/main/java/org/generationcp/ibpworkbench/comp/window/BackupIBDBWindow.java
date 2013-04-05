package org.generationcp.ibpworkbench.comp.window;

import java.util.List;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.BackupIBDBSaveAction;
import org.generationcp.ibpworkbench.comp.WorkbenchDashboard;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

@Configurable
public class BackupIBDBWindow extends Window implements InitializingBean, InternationalizableComponent {
    
	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchDashboard.class);
    
	private static final long serialVersionUID = 1L;
    
    private static final String VERSION = "1.1.3.10";
    

    private Project project;
        
    // Components
	private ComponentContainer rootLayout;
	private FormLayout formLayout;
	private Select select;
	private Button cancelBtn;
	private Button saveBtn;
	
	private static final String WINDOW_WIDTH = "400px";
	private static final String WINDOW_HEIGHT = "190px";
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

	private List<Project> projects;

    
    
    public BackupIBDBWindow(Project project) {
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
    	
	        select.setContainerDataSource(projectContainer);
    	
    	} catch (MiddlewareQueryException e) {
    		 LOG.error("Exception", e);
             throw new InternationalizableException(e, 
                     Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
		}
    }
    
    protected void initializeComponents() throws Exception {
		select = new Select("Choose a project to backup");
		select.setNullSelectionAllowed(false);
		select.setNewItemsAllowed(false);
		select.setFilteringMode(Select.FILTERINGMODE_OFF);
		select.setImmediate(true);
		
		saveBtn = new Button("Save");
		saveBtn.setSizeUndefined();
		cancelBtn = new Button("Cancel");
		cancelBtn.setSizeUndefined();
    
		
    }

    protected void initializeLayout() {
		this.setCaption("Backup IB Database");
		this.setWidth(WINDOW_WIDTH);
		this.setHeight(WINDOW_HEIGHT);
		this.setResizable(false);
		this.setModal(true);
				
		rootLayout = this.getContent();
		formLayout = new FormLayout();
		formLayout.setMargin(true);
		formLayout.setSizeFull();
		
		rootLayout.addComponent(formLayout);
		// bind components to layout
		formLayout.addComponent(select);
		
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
    }

    protected void initializeActions() {
    	// DO button listeners + actions here
    	cancelBtn.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				event.getButton().getWindow().getParent().removeWindow(event.getButton().getWindow());
			}
		});
    
    	saveBtn.addListener(new BackupIBDBSaveAction(select));
    	
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
		//messageSource.setCaption(this, Message.BACKUP_IBDBWINDOW);
		//messageSource.setCaption(select, Message.BACKUP_IBDBROJ_DROPDOWN);
		messageSource.setCaption(saveBtn,Message.SAVE);
		messageSource.setCaption(cancelBtn,Message.CANCEL);
	}
}
