package org.generationcp.ibpworkbench.comp.window;

import java.text.SimpleDateFormat;
import java.util.List;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.BackupIBDBSaveAction;
import org.generationcp.ibpworkbench.actions.CreateContactAction;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.actions.OpenNewProjectAction;
import org.generationcp.ibpworkbench.actions.OpenToolVersionsAction;
import org.generationcp.ibpworkbench.actions.RestoreIBDBSaveAction;
import org.generationcp.ibpworkbench.actions.SignoutAction;
import org.generationcp.ibpworkbench.comp.ProjectMembersComponentPanel;
import org.generationcp.ibpworkbench.comp.WorkbenchDashboard;
import org.generationcp.ibpworkbench.comp.project.create.CreateProjectPanel;
import org.generationcp.ibpworkbench.navigation.CrumbTrail;
import org.generationcp.ibpworkbench.navigation.NavUriFragmentChangedListener;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.BaseTheme;

@Configurable
public class RestoreIBDBWindow extends Window implements InitializingBean, InternationalizableComponent {
    
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
	private static final String WINDOW_HEIGHT = "390px";
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

	private List<Project> projects;

	private Table table;

    
    
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
    	
	        select.setContainerDataSource(projectContainer);
    	
    	} catch (MiddlewareQueryException e) {
    		 LOG.error("Exception", e);
             throw new InternationalizableException(e, 
                     Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
		}
    	
    	
    	// TODO Initialize Table Data
    	String[] colProperties = {"backupName","date"};
		String[] colHeaders = {"Backup Name","Backup Date"};
		
		//for (int i = 0; i < colProperties.length; i++) {
		//	table.setColumnHeader(colProperties[i],colHeaders[i]);
		//}
		
		//table.setVisibleColumns(colProperties);
		
		table.addContainerProperty("Backup Name",String.class,null);
		table.addContainerProperty("Date",java.util.Date.class,null);
		
		// test data
		table.addItem(new Object[] { "Backup 1", new java.util.Date() },1);
		table.addItem(new Object[] { "Backup 2", new java.util.Date() },2);
		table.addItem(new Object[] { "Backup 3", new java.util.Date() },3);
		
		// BeanContainer<String, Project> projectContainer = new BeanContainer<String, Project>(Project.class);
		// projectContainer.setBeanIdProperty("projectId");
		// table.setContainerDataSource(projectContainer);
		// table.setCellStyleGenerator(new ProjectTableCellStyleGenerator(tblProject, null));
    	
    }
    
    protected void initializeComponents() throws Exception {
		select = new Select("Choose a project to restore");
		select.setNullSelectionAllowed(false);
		select.setNewItemsAllowed(false);
		select.setFilteringMode(Select.FILTERINGMODE_OFF);
		select.setImmediate(true);
		
		saveBtn = new Button("Save");
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
		
    }

    protected void initializeLayout() {
		this.setCaption("Restore IB Database");
		this.setWidth(WINDOW_WIDTH);
		this.setHeight(WINDOW_HEIGHT);
		this.setResizable(false);
		this.setModal(true);
				
		rootLayout = this.getContent();
		formLayout = new FormLayout();
		formLayout.setMargin(true);
		formLayout.setSizeFull();
		
		rootLayout.addComponent(formLayout);
		rootLayout.addComponent(table);
		
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
    
    	saveBtn.addListener(new RestoreIBDBSaveAction(select,table));
    	
    	// Select action
    	select.addListener(new Property.ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				RestoreIBDBWindow.LOG.info("Select > Item selected : " + event.getProperty().getValue());
			}
		});
    	
    	// Table actions
    	table.addListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				RestoreIBDBWindow.LOG.info("Backup Table > Item selected");
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
		messageSource.setCaption(select, Message.RESTORE_IBDB_TABLE_SELECT_CAPTION);
		messageSource.setCaption(saveBtn,Message.SAVE);
		messageSource.setCaption(cancelBtn,Message.CANCEL);
	}
}
