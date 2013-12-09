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

package org.generationcp.ibpworkbench.ui.workflow;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.ChangeWindowAction;
import org.generationcp.ibpworkbench.actions.ChangeWindowAction.WindowEnums;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction.ToolEnum;
import org.generationcp.ibpworkbench.ui.WorkflowConstants;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class ConventionalBreedingWorkflowDiagram extends VerticalLayout implements WorkflowConstants, InitializingBean, InternationalizableComponent {
    
    private static final long serialVersionUID = 1L;

    //this is in pixels and used for layouting
    private static final int WORKFLOW_STEP_HEIGHT = 125;
    private static final int WORKFLOW_STEP_EXTRA_HEIGHT = 195;
    private static final int PROJECT_PLANNING_HEIGHT = 205;
    private static final int WORKFLOW_STEP_WIDTH = 270;
    private static final int EXTRA_SPACE_BETWEEN_COMPONENTS = 10;
    private static final int ARROW_IMAGE_HEIGHT = 30;
    //private static final int ARROW_IMAGE_WIDTH = 40;
    private static final String FIRST_COLUMN_LEFT_FOR_ARROWS = "135px";
    private static final String DOWN_ARROW_THEME_RESOURCE = "../gcp-default/images/blc-arrow-d.png";
    
    private boolean workflowPreview;
    
    private Project project;

    private Label dashboardTitle;

    private Label projectPlanningTitle;
    private Label populationDevelopmentTitle;
    private Label fieldTrialManagementTitle;
    private Label statisticalAnalysisTitle;
    private Label breedingDecisionTitle;

    //links for tools
    private Button browseGermplasmButton;
    private Button browseStudiesButton;
    private Button browseGermplasmListsButton;
    private Button breedingManagerButton;
    private Button breedingViewButton;
    private Button breedingViewSingleSiteAnalysisCentralButton;
    private Button breedingViewSingleSiteAnalysisLocalButton;
    private Button fieldbookButton;
    private Button browseGenotypingDataButton;
    private Button optimasButton;
    //private Button headToHeadButton;
    //private Button headToHeadButton2;
    private Button mainHeadToHeadButton;
    private Button mainHeadToHeadButton2;
    
    private Embedded downArrowImage1;
    private Embedded downArrowImage2;
    private Embedded downArrowImage3;
    private Embedded downArrowImage4;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    private Button manageGermplasmListsButton;

    private Button breedingViewMultiSiteAnalysisButton;

    private Button makeCrossesButton;

    private Role role;

    private Button breedingPlannerButton;

    private Button germplasmImportButton;

    private Button germplasmImportButton2;

    private Button queryForAdaptedGermplasmButton;
    private Button queryForAdaptedGermplasmButton2;
    
    private Button breedingManagerListManager;
    private Button ontologyBrowserFBBtn;
    private Button metaAnalysisBtn;

    public ConventionalBreedingWorkflowDiagram(boolean workflowPreview, Project project, Role role) {
        this.workflowPreview = workflowPreview;
        
        if (!workflowPreview) {
            this.project = project;
        }
        
        this.role = role;
    }
    
    @Override
    public void afterPropertiesSet() {
        assemble();
    }

    protected void initializeComponents() {
        // dashboard title
        dashboardTitle = new Label();
        dashboardTitle.setStyleName("gcp-content-title");

        projectPlanningTitle = new Label(messageSource.getMessage(Message.PROJECT_PLANNING));
        projectPlanningTitle.setStyleName("gcp-section-title-large");
        //projectPlanningTitle.setSizeUndefined();

        populationDevelopmentTitle = new Label(messageSource.getMessage(Message.POPULATION_DEVELOPMENT));
        populationDevelopmentTitle.setStyleName("gcp-section-title-large");
        //populationDevelopmentTitle.setSizeUndefined();

        fieldTrialManagementTitle = new Label(messageSource.getMessage(Message.FIELD_TRIAL_MANAGEMENT));
        fieldTrialManagementTitle.setStyleName("gcp-section-title-large");
        //fieldTrialManagementTitle.setSizeUndefined();

        statisticalAnalysisTitle = new Label(messageSource.getMessage(Message.STATISTICAL_ANALYSIS));
        statisticalAnalysisTitle.setStyleName("gcp-section-title-large");
        //statisticalAnalysisTitle.setSizeUndefined();

        breedingDecisionTitle = new Label(messageSource.getMessage(Message.BREEDING_DECISION));
        breedingDecisionTitle.setStyleName("gcp-section-title-large");
        //breedingDecisionTitle.setSizeUndefined();
        
        breedingPlannerButton = new Button("Breeding Planner");
        breedingPlannerButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingPlannerButton.setSizeUndefined();
        breedingPlannerButton.setDescription("Click to launch the freestanding Breeding Planner application.");
        
        germplasmImportButton = new Button("IBFB Import Germplasm Lists");
        germplasmImportButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        germplasmImportButton.setSizeUndefined();
        germplasmImportButton.setDescription("Click to launch Fieldbook on Nursery Manager View.");
        
        germplasmImportButton2 = new Button("Import Germplasm Lists");
        germplasmImportButton2.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        germplasmImportButton2.setSizeUndefined();
        germplasmImportButton2.setDescription("Click to launch the Germplasm Import View.");
        
        
        browseGermplasmButton = new Button(messageSource.getMessage(Message.BROWSE_GERMPLASM_INFORMATION));
        browseGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        browseGermplasmButton.setSizeUndefined();
        browseGermplasmButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_GERMPLASM_BROWSER));
        
        browseGenotypingDataButton = new Button(messageSource.getMessage(Message.GENOTYPIC_DATA_BROWSER_LINK));
        browseGenotypingDataButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        browseGenotypingDataButton.setSizeUndefined();
        browseGenotypingDataButton.setDescription(messageSource.getMessage(Message.GENOTYPIC_DATA_BROWSER_DESC));

        browseStudiesButton = new Button(messageSource.getMessage(Message.BROWSE_STUDIES_AND_DATASETS));
        browseStudiesButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        browseStudiesButton.setSizeUndefined();
        browseStudiesButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_STUDY_BROWSER));

        browseGermplasmListsButton = new Button(messageSource.getMessage(Message.BROWSE_GERMPLAM_LISTS));
        browseGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        browseGermplasmListsButton.setSizeUndefined();
        browseGermplasmListsButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_GERMPLASM_LIST_BROWSER));
        
        breedingManagerButton = new Button(messageSource.getMessage(Message.MANAGE_NURSERIES));
        breedingManagerButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingManagerButton.setSizeUndefined();
        breedingManagerButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_MANAGER));

        makeCrossesButton = new Button(messageSource.getMessage(Message.MAKE_CROSSES));
        makeCrossesButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        makeCrossesButton.setSizeUndefined();
        makeCrossesButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_CROSSING_MANAGER));
                
        breedingViewButton = new Button(messageSource.getMessage(Message.BREEDING_VIEW));
        breedingViewButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingViewButton.setSizeUndefined();
        breedingViewButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW));
        
        breedingViewSingleSiteAnalysisCentralButton = new Button(messageSource.getMessage(Message.SINGLE_SITE_ANALYSIS_CENTRAL_LINK));
        breedingViewSingleSiteAnalysisCentralButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingViewSingleSiteAnalysisCentralButton.setSizeUndefined();
        breedingViewSingleSiteAnalysisCentralButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW_SINGLE_SITE_ANALYSIS_CENTRAL));

        breedingViewSingleSiteAnalysisLocalButton = new Button(messageSource.getMessage(Message.SINGLE_SITE_ANALYSIS_LOCAL_LINK));
        breedingViewSingleSiteAnalysisLocalButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingViewSingleSiteAnalysisLocalButton.setSizeUndefined();
        breedingViewSingleSiteAnalysisLocalButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL));
        
        manageGermplasmListsButton = new Button(messageSource.getMessage(Message.LIST_MANAGER));
        manageGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        manageGermplasmListsButton.setSizeUndefined();
        manageGermplasmListsButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_LIST_MANAGER));
        
        breedingViewMultiSiteAnalysisButton = new Button(messageSource.getMessage(Message.MULTI_SITE_ANALYSIS_LINK));
        breedingViewMultiSiteAnalysisButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingViewMultiSiteAnalysisButton.setSizeUndefined();
        breedingViewMultiSiteAnalysisButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW_MULTI_SITE_ANALYSIS));
        /*
        headToHeadButton = new Button(messageSource.getMessage(Message.HEAD_TO_HEAD_LAUNCH));
        headToHeadButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        headToHeadButton.setSizeUndefined();
        headToHeadButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_HEAD_TO_HEAD));
        
        headToHeadButton2 = new Button(messageSource.getMessage(Message.HEAD_TO_HEAD_LAUNCH));
        headToHeadButton2.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        headToHeadButton2.setSizeUndefined();
        headToHeadButton2.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_HEAD_TO_HEAD));
          */
        mainHeadToHeadButton = new Button(messageSource.getMessage(Message.MAIN_HEAD_TO_HEAD_LAUNCH));
        mainHeadToHeadButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        mainHeadToHeadButton.setSizeUndefined();
        mainHeadToHeadButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_MAIN_HEAD_TO_HEAD));
        
        mainHeadToHeadButton2 = new Button(messageSource.getMessage(Message.MAIN_HEAD_TO_HEAD_LAUNCH));
        mainHeadToHeadButton2.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        mainHeadToHeadButton2.setSizeUndefined();
        mainHeadToHeadButton2.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_MAIN_HEAD_TO_HEAD));
        
        fieldbookButton = new Button("Manage Trials");
        fieldbookButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        fieldbookButton.setSizeUndefined();
        fieldbookButton.setDescription("Click to launch Fieldbook in Trial Manager View");
        
        optimasButton = new Button(messageSource.getMessage(Message.OPTIMAS));
        optimasButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        optimasButton.setSizeUndefined();
        optimasButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_OPTIMAS));
        
        queryForAdaptedGermplasmButton = new Button(messageSource.getMessage(Message.QUERY_FOR_ADAPTED_GERMPLASM));
        queryForAdaptedGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        queryForAdaptedGermplasmButton.setSizeUndefined();
        queryForAdaptedGermplasmButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM));
        
        queryForAdaptedGermplasmButton2 = new Button(messageSource.getMessage(Message.QUERY_FOR_ADAPTED_GERMPLASM));
        queryForAdaptedGermplasmButton2.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        queryForAdaptedGermplasmButton2.setSizeUndefined();
        queryForAdaptedGermplasmButton2.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_QUERY_FOR_ADAPTED_GERMPLASM));
        
        breedingManagerListManager = new Button(messageSource.getMessage(Message.BREEDING_MANAGER_BROWSE_FOR_GERMPLASMS_AND_LISTS));
        breedingManagerListManager.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingManagerListManager.setSizeUndefined();
        breedingManagerListManager.setDescription(messageSource.getMessage(Message.CLICK_TO_BROWSE_FOR_GERMPLASMS_AND_LISTS));

        ontologyBrowserFBBtn = new Button("Manage Ontologies");
        ontologyBrowserFBBtn.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        ontologyBrowserFBBtn.setSizeUndefined();
        ontologyBrowserFBBtn.setDescription("Click to launch Fieldbook on Ontology Browser view");

        metaAnalysisBtn = new Button("Meta Analysis of Field Trials for Local Datasets");
        metaAnalysisBtn.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        metaAnalysisBtn.setSizeUndefined();
        metaAnalysisBtn.setDescription("Click to launch Meta Analysis of Field Trial Tool");

        downArrowImage1 = new Embedded("", new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        downArrowImage2 = new Embedded("", new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        downArrowImage3 = new Embedded("", new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        downArrowImage4 = new Embedded("", new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
    }

    protected void initializeLayout() {
        this.setSizeFull();
        this.setSpacing(true);
        this.setMargin(new MarginInfo(true,false,false,true));

        if (!workflowPreview)
            addComponent(dashboardTitle);

        Component workFlowArea = layoutWorkflowArea();
        addComponent(workFlowArea);
        this.setExpandRatio(workFlowArea, 1.0F);
    }

    protected Component layoutWorkflowArea() {
        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setScrollable(true);
        panel.setStyleName(Reindeer.PANEL_LIGHT);
        
        
        AbsoluteLayout layout = new AbsoluteLayout();
        layout.setMargin(true);
        layout.setWidth("300px");
        layout.setHeight("1200px");
        
        String extraSpace = EXTRA_SPACE_BETWEEN_COMPONENTS + "px";
        int top = 10;
        String topInPixels = "";
        
        //the steps on the first column
        Component projectPlanningArea = layoutProjectPlanning();
        layout.addComponent(projectPlanningArea, "top:" + extraSpace + "; left:" + extraSpace);
        
        top = top + PROJECT_PLANNING_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        layout.addComponent(downArrowImage1, "top:" + topInPixels + "; left:" + FIRST_COLUMN_LEFT_FOR_ARROWS);
        
        top = top + ARROW_IMAGE_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        Component populationDevelopmentArea = layoutPopulationDevelopment();
        layout.addComponent(populationDevelopmentArea, "top:" + topInPixels  + "; left:" + extraSpace);
        
        top = top + WORKFLOW_STEP_EXTRA_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        layout.addComponent(downArrowImage2, "top:" + topInPixels + "; left:" + FIRST_COLUMN_LEFT_FOR_ARROWS);
        
        top = top + ARROW_IMAGE_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        Component fieldTrialArea = layoutFieldTrialManagement();
        layout.addComponent(fieldTrialArea, "top:" + topInPixels  + "; left:" + extraSpace);
        
        top = top + WORKFLOW_STEP_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        layout.addComponent(downArrowImage3, "top:" + topInPixels + "; left:" + FIRST_COLUMN_LEFT_FOR_ARROWS);
        
        top = top + ARROW_IMAGE_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        Component statisticalAnalysisArea = layoutStatisticalAnalysis();
        layout.addComponent(statisticalAnalysisArea, "top:" + topInPixels  + "; left:" + extraSpace);

        top = top + WORKFLOW_STEP_EXTRA_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        layout.addComponent(downArrowImage4, "top:" + topInPixels + "; left:" + FIRST_COLUMN_LEFT_FOR_ARROWS);
        
        top = top + ARROW_IMAGE_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        Component breedingDecisionArea = layoutBreedingDecision();
        layout.addComponent(breedingDecisionArea, "top:" + topInPixels  + "; left:" + extraSpace);

        panel.setContent(layout);
        return panel;
    }

    protected Component layoutProjectPlanning() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);
        layout.setHeight(PROJECT_PLANNING_HEIGHT + "px");
        
        layout.addComponent(projectPlanningTitle);
        layout.setComponentAlignment(projectPlanningTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(projectPlanningTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("5px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        

        layout.addComponent(ontologyBrowserFBBtn);
        ontologyBrowserFBBtn.setHeight("20px");
        layout.setComponentAlignment(ontologyBrowserFBBtn, Alignment.TOP_CENTER);
        layout.setExpandRatio(ontologyBrowserFBBtn, 0);

        
        //layout.addComponent(browseGermplasmButton);
        //browseGermplasmButton.setHeight("20px");
        //layout.setComponentAlignment(browseGermplasmButton, Alignment.TOP_CENTER);
        //layout.setExpandRatio(browseGermplasmButton, 0);

        layout.addComponent(browseStudiesButton);
        browseStudiesButton.setHeight("20px");
        layout.setComponentAlignment(browseStudiesButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseStudiesButton, 0);
        
        layout.addComponent(browseGermplasmListsButton);
        browseGermplasmListsButton.setHeight("20px");
        layout.setComponentAlignment(browseGermplasmListsButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseGermplasmListsButton, 0);
        
        layout.addComponent(browseGenotypingDataButton);
        browseGenotypingDataButton.setHeight("20px");
        layout.setComponentAlignment(browseGenotypingDataButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseGenotypingDataButton, 0);
         /*
        layout.addComponent(headToHeadButton);
        headToHeadButton.setHeight("20px");
        layout.setComponentAlignment(headToHeadButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(headToHeadButton, 0);
           */
        layout.addComponent(mainHeadToHeadButton);
        mainHeadToHeadButton.setHeight("20px");
        layout.setComponentAlignment(mainHeadToHeadButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(mainHeadToHeadButton, 0);
        
        layout.addComponent(queryForAdaptedGermplasmButton);
        queryForAdaptedGermplasmButton.setHeight("20px");
        layout.setComponentAlignment(queryForAdaptedGermplasmButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(queryForAdaptedGermplasmButton, 0);
        
        /*layout.addComponent(breedingManagerListManager);
        breedingManagerListManager.setHeight("20px");
        layout.setComponentAlignment(breedingManagerListManager, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingManagerListManager, 0);
          */
        return layout;
    }

    protected Component layoutPopulationDevelopment() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);
        layout.setHeight(WORKFLOW_STEP_EXTRA_HEIGHT + "px");
        
        layout.setHeight(WORKFLOW_STEP_EXTRA_HEIGHT + "px");
        
        layout.addComponent(populationDevelopmentTitle);
        layout.setComponentAlignment(populationDevelopmentTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(populationDevelopmentTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("5px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        layout.addComponent(germplasmImportButton);
        germplasmImportButton.setHeight("20px");
        layout.setComponentAlignment(germplasmImportButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(germplasmImportButton, 0);

        layout.addComponent(germplasmImportButton2);
        germplasmImportButton2.setHeight("20px");
        layout.setComponentAlignment(germplasmImportButton2, Alignment.TOP_CENTER);
        layout.setExpandRatio(germplasmImportButton2, 0);
        
        /**layout.addComponent(manageGermplasmListsButton);
        manageGermplasmListsButton.setHeight("20px");
        layout.setComponentAlignment(manageGermplasmListsButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(manageGermplasmListsButton, 0);**/
        
        layout.addComponent(makeCrossesButton);
        makeCrossesButton.setHeight("20px");
        layout.setComponentAlignment(makeCrossesButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(makeCrossesButton, 0);

        layout.addComponent(breedingManagerButton);
        layout.setComponentAlignment(breedingManagerButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingManagerButton, 0);

        return layout;
    }

    protected Component layoutFieldTrialManagement() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(fieldTrialManagementTitle);
        layout.setComponentAlignment(fieldTrialManagementTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(fieldTrialManagementTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(fieldbookButton);
        layout.setComponentAlignment(fieldbookButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(fieldbookButton, 0);
        
        return layout;
    }

    protected Component layoutStatisticalAnalysis() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.setHeight(WORKFLOW_STEP_EXTRA_HEIGHT + "px");
                
        layout.addComponent(statisticalAnalysisTitle);
        layout.setComponentAlignment(statisticalAnalysisTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(statisticalAnalysisTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("5px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        layout.addComponent(breedingViewSingleSiteAnalysisLocalButton);
        breedingViewSingleSiteAnalysisLocalButton.setHeight("20px");
        layout.setComponentAlignment(breedingViewSingleSiteAnalysisLocalButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewSingleSiteAnalysisLocalButton, 0);

        layout.addComponent(metaAnalysisBtn);
        metaAnalysisBtn.setHeight("20px");
        layout.setComponentAlignment(metaAnalysisBtn, Alignment.TOP_CENTER);
        layout.setExpandRatio(metaAnalysisBtn, 0);

        layout.addComponent(breedingViewMultiSiteAnalysisButton);
        breedingViewMultiSiteAnalysisButton.setHeight("20px");
        layout.setComponentAlignment(breedingViewMultiSiteAnalysisButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewMultiSiteAnalysisButton, 0);
        
        layout.addComponent(breedingViewButton);
        layout.setComponentAlignment(breedingViewButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewButton, 0);
        
        return layout;
    }

    protected Component layoutBreedingDecision() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(breedingDecisionTitle);
        layout.setComponentAlignment(breedingDecisionTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingDecisionTitle, 0);
        
        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
          /*
        layout.addComponent(headToHeadButton2);
        layout.setComponentAlignment(headToHeadButton2, Alignment.TOP_CENTER);
        layout.setExpandRatio(headToHeadButton2, 0);
            */
        layout.addComponent(mainHeadToHeadButton2);
        layout.setComponentAlignment(mainHeadToHeadButton2, Alignment.TOP_CENTER);
        layout.setExpandRatio(mainHeadToHeadButton2, 0);
        
        layout.addComponent(queryForAdaptedGermplasmButton2);
        layout.setComponentAlignment(queryForAdaptedGermplasmButton2, Alignment.TOP_CENTER);
        layout.setExpandRatio(queryForAdaptedGermplasmButton2, 0);
        
        return layout;
    }

    protected Component createPanel(String caption, String... buttonCaptions) {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        Label titleLabel = new Label(caption);
        titleLabel.setStyleName("gcp-section-title");
        titleLabel.setSizeUndefined();

        layout.addComponent(titleLabel);
        layout.setComponentAlignment(titleLabel, Alignment.TOP_CENTER);
        layout.setExpandRatio(titleLabel, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        for (String buttonCaption : buttonCaptions) {
            Button button = new Button(buttonCaption);
            button.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");

            layout.addComponent(button);
            layout.setComponentAlignment(button, Alignment.TOP_CENTER);
            layout.setExpandRatio(button, 0);
        }

        return layout;
    }

    protected void configureWorkflowStepLayout(VerticalLayout layout) {
        layout.setWidth(WORKFLOW_STEP_WIDTH + "px");
        layout.setHeight(WORKFLOW_STEP_HEIGHT + "px");
        layout.setStyleName("gcp-workflow-step");
        layout.setMargin(false,false, true,false);
    }
    
    protected void initializeActions() {
        if (!workflowPreview) {
            germplasmImportButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.IBFB_GERMPLASM_IMPORT));
            germplasmImportButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_IMPORT));
            
            breedingPlannerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_PLANNER)); //TODO
            
            //headToHeadButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.HEAD_TO_HEAD_BROWSER));
            //headToHeadButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.HEAD_TO_HEAD_BROWSER));
            mainHeadToHeadButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.MAIN_HEAD_TO_HEAD_BROWSER));
            mainHeadToHeadButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.MAIN_HEAD_TO_HEAD_BROWSER));
            //browseGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_BROWSER));
            browseStudiesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.STUDY_BROWSER));
            browseGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BM_LIST_MANAGER));
            breedingManagerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.NURSERY_MANAGER_FIELDBOOK_WEB));
            breedingViewButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW));
         
            breedingViewSingleSiteAnalysisCentralButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW, project, WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_CENTRAL, this.role));
            breedingViewSingleSiteAnalysisLocalButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW, project, WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL, this.role));
            fieldbookButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.TRIAL_MANAGER_FIELDBOOK_WEB));
            optimasButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.OPTIMAS));
            browseGenotypingDataButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GDMS));
            makeCrossesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.CROSSING_MANAGER));
            manageGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));
            
            breedingViewMultiSiteAnalysisButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_GXE,this.project,this.role,null));
            queryForAdaptedGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.QUERY_FOR_ADAPTED_GERMPLASM));
            queryForAdaptedGermplasmButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.QUERY_FOR_ADAPTED_GERMPLASM));
            breedingManagerListManager.addListener(new LaunchWorkbenchToolAction(ToolEnum.BM_LIST_MANAGER));

            ontologyBrowserFBBtn.addListener(new LaunchWorkbenchToolAction(ToolEnum.ONTOLOGY_BROWSER_FIELDBOOK_WEB));
            metaAnalysisBtn.addListener(new ChangeWindowAction(WindowEnums.BV_META_ANALYSIS,this.project,this.role,null));

        }
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
        
        if (workflowPreview)
            this.setStyleName("gcp-removelink");
        
    }
    
    @Override
    public void attach() {
        super.attach();        
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        if (workflowPreview) {
            messageSource.setValue(dashboardTitle, Message.WORKFLOW_PREVIEW_TITLE, "Conventional Breeding");
        } else { 
            messageSource.setValue(dashboardTitle, Message.PROJECT_TITLE, project.getProjectName());
        }
        
        /*
        messageSource.setCaption(manageGermplasmListsButton,Message.GERMPLASM_LIST_MANAGER_LINK);
        messageSource.setCaption(breedingViewMultiSiteAnalysisButton,Message.MULTI_SITE_ANALYSIS_LINK);
        messageSource.setCaption(breedingViewButton,Message.BREEDING_VIEW_STANDALONE_LINK);
        */
    }
}
