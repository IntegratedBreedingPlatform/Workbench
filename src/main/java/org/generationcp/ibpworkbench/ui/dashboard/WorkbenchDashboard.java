/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.dashboard;

import java.text.SimpleDateFormat;
import java.util.*;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.util.Util;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenNewProjectAction;
import org.generationcp.ibpworkbench.actions.OpenSelectProjectForStudyAndDatasetViewAction;
import org.generationcp.ibpworkbench.actions.ShowProjectDetailAction;
import org.generationcp.ibpworkbench.ui.dashboard.listener.DashboardMainClickListener;
import org.generationcp.ibpworkbench.ui.dashboard.preview.GermplasmListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.preview.NurseryListPreview;
import org.generationcp.ibpworkbench.ui.dashboard.summaryview.SummaryView;
import org.generationcp.ibpworkbench.ui.gxe.ProjectTableCellStyleGenerator;
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
import com.vaadin.data.util.BeanContainer;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.themes.Reindeer;


@Configurable
public class WorkbenchDashboard extends VerticalLayout implements InitializingBean, InternationalizableComponent {

    private static final Logger LOG = LoggerFactory.getLogger(WorkbenchDashboard.class);
    private static final long serialVersionUID = 1L;

    private Table tblProject;
    
    private Project currentProject;
    
    private Button selectDatasetForBreedingViewButton;
    
    private TabSheet previewTab;

    private GermplasmListPreview germplasmListPreview;
    private NurseryListPreview nurseryListPreview;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private Project lastOpenedProject;
    
    public static final String PROGRAM_NAME_COLUMN_ID = "Workbench Dashboard Program Name Column Id";
    public static final String CROP_NAME_COLUMN_ID = "Workbench Dashboard Crop Name Column Id";
    public static final String BUTTON_LIST_MANAGER_COLUMN_ID = "Workbench Dashboard List Manager Button Column Id";
    
    private List<Project> projects = null;
    private SummaryView summaryView;

    public WorkbenchDashboard() {
        super();
    }

    @Override
    public void afterPropertiesSet() {
        assemble();
    }

    public void initializeComponents() {
    	selectDatasetForBreedingViewButton = new Button("View Studies and Datasets");
        selectDatasetForBreedingViewButton.setWidth("200px");

        initializeProjectTable();
        initializePreviewTable();
    }
    
    private void initializeProjectTable() {
        // project table components
        tblProject = new Table() {

            private static final long serialVersionUID = 1L;

            @Override
            protected String formatPropertyValue(Object rowId, Object colId, Property property) {
                if (property.getType() == Date.class) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return property.getValue() == null ? "" : sdf.format((Date) property.getValue());
                }

                return super.formatPropertyValue(rowId, colId, property);
            }
        };
        tblProject.setImmediate(true); // react at once when something is selected
        tblProject.setSelectable(true);
        tblProject.setStyleName("gcp-tblproject");

        tblProject.addContainerProperty(PROGRAM_NAME_COLUMN_ID, String.class, null);
        tblProject.addContainerProperty(CROP_NAME_COLUMN_ID, String.class, null);
        tblProject.addContainerProperty(BUTTON_LIST_MANAGER_COLUMN_ID, Button.class, null);

        tblProject.setColumnHeader(PROGRAM_NAME_COLUMN_ID, "PROGRAM NAME");
        tblProject.setColumnHeader(CROP_NAME_COLUMN_ID, "CROP");
        tblProject.setColumnHeader(BUTTON_LIST_MANAGER_COLUMN_ID, "LAUNCH");

        tblProject.setColumnCollapsingAllowed(false);
        tblProject.setCellStyleGenerator(new ProjectTableCellStyleGenerator(tblProject, null));
    
    }

    private void initializePreviewTable() {
        germplasmListPreview = new GermplasmListPreview(null);
        nurseryListPreview = new NurseryListPreview(null);

        previewTab = new TabSheet();
        previewTab.setHeight(100,Sizeable.UNITS_PERCENTAGE);
        previewTab.addTab(germplasmListPreview, "Lists");

        previewTab.addTab(nurseryListPreview, "Nurseries & Trials");

        previewTab.setImmediate(true);
    }
    
    protected void initializeLayout() {
        this.setSizeUndefined();
        this.setMargin(new MarginInfo(false,true,true,true));
        this.setWidth("100%");

        final Label dashboardLbl = new Label(messageSource.getMessage(Message.DASHBOARD));
        dashboardLbl.setStyleName(Bootstrap.Typography.H1.styleName());

        this.addComponent(dashboardLbl);
        this.addComponent(layoutProjectTableArea());
        this.addComponent(layoutProjectDetailArea());
    }

    protected void initializeData() {
        //TODO: Verify the try-catch flow
        // Get the list of Projects
        
        lastOpenedProject = null;
        
        IBPWorkbenchApplication app = IBPWorkbenchApplication.get();
        
        try {
            User currentUser = app.getSessionData().getUserData();
            projects = workbenchDataManager.getProjectsByUser(currentUser);
            lastOpenedProject = workbenchDataManager.getLastOpenedProject(
            		currentUser.getUserid());
        } catch (MiddlewareQueryException e) {
            LOG.error("Exception", e);
            throw new InternationalizableException(e, 
                    Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
        }

        app.getSessionData().setLastOpenedProject(lastOpenedProject);

        if (currentProject == null) currentProject = lastOpenedProject;

        app.getSessionData().setSelectedProject(currentProject);

        // set the Project Table data source
        BeanContainer<String, Project> projectContainer = new BeanContainer<String, Project>(Project.class);
        projectContainer.setBeanIdProperty("projectName");

        int i =0;
        Project project;
        for(i = projects.size() - 1 ; i >=0 ; i--){
            project = projects.get(i);

            Button button = new Button("<span class='glyphicon glyphicon-play'></span>");
            button.setHtmlContentAllowed(true);
            button.setData(BUTTON_LIST_MANAGER_COLUMN_ID);
            button.setStyleName(Bootstrap.Buttons.LINK.styleName() + " launch");
            button.setWidth("26px"); button.setHeight("26px");
            button.addListener(new DashboardMainClickListener(this, project.getProjectId()));
            button.setEnabled(false);

            Long lastOpenedProjectId = lastOpenedProject == null ? null : lastOpenedProject.getProjectId();
            boolean sameProject = lastOpenedProjectId == null ? project.getProjectId() == null : lastOpenedProjectId.equals(project.getProjectId());
            
            if (sameProject) {
                WorkbenchDashboard.this.lasSelectedProjectButton = button;

                button.setEnabled(true);
            }

            // capitalization done on CSS
            tblProject.addItem(new Object[]{project.getProjectName(),project.getCropType().getCropName(), button}, project.getProjectId());
        }

        if (lastOpenedProject != null)
            tblProject.select(lastOpenedProject.getProjectId());

    }

    private Button lasSelectedProjectButton = null;

    protected void initializeActions() {
        
        OpenSelectProjectForStudyAndDatasetViewAction openSelectDatasetForBreedingViewAction = new OpenSelectProjectForStudyAndDatasetViewAction(null);
        selectDatasetForBreedingViewButton.addListener(openSelectDatasetForBreedingViewAction);

        tblProject.addListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                Object selectedButton =  tblProject.getItem(event.getItemId()).getItemProperty(BUTTON_LIST_MANAGER_COLUMN_ID).getValue();

                // disable previously selected button

                if (lasSelectedProjectButton != null) {
                    lasSelectedProjectButton.setEnabled(false);
                }

                if (selectedButton instanceof Button && selectedButton != null) {
                    ((Button)selectedButton).setEnabled(true);
                    WorkbenchDashboard.this.lasSelectedProjectButton = (Button) selectedButton;

                    if (event.isDoubleClick()) {

                        // hack manual trigger button
                        Map vars = new HashMap();
                        vars.put("state",true);
                        ((Button)selectedButton).changeVariables(this, vars);

                    }
                }
            }
        });

        tblProject.addListener(new ShowProjectDetailAction(tblProject, summaryView, selectDatasetForBreedingViewButton, openSelectDatasetForBreedingViewAction,currentProject, germplasmListPreview, nurseryListPreview, previewTab, projects));

    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeData();
        initializeActions();
    }

    private Component layoutProjectTableArea() {
        final HorizontalSplitPanel root = new HorizontalSplitPanel();
        root.setSplitPosition(529,UNITS_PIXELS,true);
        root.setStyleName(Reindeer.SPLITPANEL_SMALL + " gcp-program-table-area");

        final VerticalLayout programArea = new VerticalLayout();
        programArea.setSizeFull();
        programArea.setMargin(new MarginInfo(false,true,false,false));

        final HorizontalLayout programHeaderArea = new HorizontalLayout();
        programHeaderArea.setWidth("100%");
        final Label programLbl = new Label(messageSource.getMessage(Message.PROGRAMS_LABEL));
        programLbl.setStyleName(Bootstrap.Typography.H2.styleName());

        final Label programDescLbl = new Label(messageSource.getMessage(Message.PROGRAM_TABLE_TOOLTIP));
        programDescLbl.setStyleName(Bootstrap.Typography.H6.styleName());

        final Button addProgramBtn = new Button("<span class='glyphicon glyphicon-plus' style='right: 4px'></span> " + messageSource.getMessage(Message.ADD_A_PROGRAM));//"Add a Program");
        addProgramBtn.setHtmlContentAllowed(true);
        addProgramBtn.addListener(new OpenNewProjectAction());
        addProgramBtn.addStyleName(Bootstrap.Buttons.INFO.styleName());
        addProgramBtn.setWidth("145px");

        programHeaderArea.addComponent(programLbl);

        final HorizontalLayout headerContainer = new HorizontalLayout();
        headerContainer.setSizeUndefined();
        headerContainer.setSpacing(true);

        final Embedded headerImg = new Embedded(null,new ThemeResource("images/programs.png"));
        headerImg.setStyleName("header-img");

        headerContainer.addComponent(headerImg);
        headerContainer.addComponent(programLbl);
        headerContainer.addComponent(programDescLbl);

        headerContainer.setComponentAlignment(headerImg, Alignment.BOTTOM_LEFT);
        headerContainer.setComponentAlignment(programLbl, Alignment.BOTTOM_LEFT);
        headerContainer.setComponentAlignment(programDescLbl,Alignment.BOTTOM_LEFT);


        programHeaderArea.addComponent(headerContainer);
        programHeaderArea.addComponent(addProgramBtn);
        programHeaderArea.setComponentAlignment(addProgramBtn,Alignment.MIDDLE_LEFT);
        programHeaderArea.setExpandRatio(headerContainer,1.0F);

        tblProject.setSizeFull();        
        tblProject.setStyleName("program-tab");

        programArea.addComponent(programHeaderArea);
        programArea.addComponent(tblProject);
        programArea.setExpandRatio(tblProject, 1.0F);

        final VerticalLayout previewArea = new VerticalLayout();
        previewArea.setStyleName("preview-area");
        previewArea.setSizeFull();
        previewArea.setMargin(new MarginInfo(true,false,false,false));

        previewArea.addComponent(previewTab);
        previewTab.addStyleName("preview-tab");

        root.setFirstComponent(programArea);
        root.setSecondComponent(previewArea);
        root.setHeight("400px");

        return root;
    }
    
    private Component layoutProjectDetailArea() {
        this.summaryView = new SummaryView();

        return summaryView;
    }
    
    @Override
    public void attach() {
        super.attach();
        
        updateLabels();
    }

    @Override
    public void updateLabels() {
        messageSource.setColumnHeader(tblProject, "startDate", Message.START_DATE);
        messageSource.setColumnHeader(tblProject, "projectName", Message.PROJECT);
        messageSource.setColumnHeader(tblProject, "action", Message.ACTION);
        messageSource.setColumnHeader(tblProject, "status", Message.STATUS);
        messageSource.setColumnHeader(tblProject, "owner", Message.OWNER);

        tblProject.setItemDescriptionGenerator(new ItemDescriptionGenerator() {
            private static final long serialVersionUID = 1L;

            @Override
            public String generateDescription(Component source, Object itemId, Object propertyId) {
                return messageSource.getMessage(Message.PROGRAM_TABLE_TOOLTIP);
            }
        });
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
     }

    //hacky hack hack
    public ShowProjectDetailAction initializeDashboardContents() {
        // update other pards
        return new ShowProjectDetailAction(tblProject, summaryView, selectDatasetForBreedingViewButton, new OpenSelectProjectForStudyAndDatasetViewAction(null),lastOpenedProject, germplasmListPreview, nurseryListPreview, previewTab, projects);

    }
}
