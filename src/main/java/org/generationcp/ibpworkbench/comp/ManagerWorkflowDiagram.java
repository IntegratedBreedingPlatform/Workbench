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

package org.generationcp.ibpworkbench.comp;

import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.ChangeWindowAction;
import org.generationcp.ibpworkbench.actions.ChangeWindowAction.WindowEnums;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction.ToolEnum;
import org.generationcp.ibpworkbench.actions.OpenProjectLocationAction;
import org.generationcp.ibpworkbench.actions.OpenProjectMethodsAction;
import org.generationcp.ibpworkbench.actions.OpenWindowAction;
import org.generationcp.ibpworkbench.actions.OpenWindowAction.WindowEnum;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class ManagerWorkflowDiagram extends VerticalLayout implements WorkflowConstants, InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;

    //this is in pixels and used for layouting
    private static final int WORKFLOW_STEP_HEIGHT = 180;
    private static final int WORKFLOW_STEP_WIDTH = 270;
    private static final int EXTRA_SPACE_BETWEEN_COMPONENTS = 10;
    
    private boolean workflowPreview;
    
    private Project project;
    private Role role;

    private Label dashboardTitle;

    private Label administrationTitle;
    private Label genoTypingTitle;
    private Label projectPlanningTitle;
    private Label breedingManagementTitle;
    private Label analysisPipelineTitle;
    private Label decisionSupportTitle;

    //links for tools
    private Button browseGermplasmButton;
    private Button membersButton;
    private Button browseStudiesButton;
    private Button browseGermplasmListsButton;
    private Button gdmsButton;
    private Button mbdtButton;
    private Button breedingViewButton;
    private Button breedingViewSingleSiteAnalysisCentralButton;
    private Button breedingViewSingleSiteAnalysisLocalButton;
    private Button breedingViewGxeAnalysisLocalButton;
    private Button fieldbookButton;
    private Button optimasButton;
    private Button nurseryManagerButton;
    private Button projectLocationButton;
    private Button projectMethodsButton;
    private Button createTemplatesButton;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

	private Button backupIBDBButton;
	private Link backupIBDBLink;
	private Button restoreIBDBButton;

	private Button breedingViewMultiSiteAnalysisButton;

	private Button manageGermplasmListsButton;
	
	@Autowired
    private WorkbenchDataManager workbenchDataManager;

	private Button breedingManagerButton;

	private Button makeCrossesButton;

	private Button breedingPlannerButton;

	private Button germplasmImportButton;
    

    public ManagerWorkflowDiagram(boolean workflowPreview, Project project, Role role) {
        this.workflowPreview = workflowPreview;
        
        if (!workflowPreview) {
            this.project = project;
            this.role = role;
        }
    }
    
    @Override
    public void afterPropertiesSet() {
        assemble();
    }

    protected void initializeComponents() {
        // dashboard title
        dashboardTitle = new Label();
        dashboardTitle.setStyleName("gcp-content-title");

        administrationTitle = new Label("Administration & Configuration");
        administrationTitle.setStyleName("gcp-section-title-large");
        //administrationTitle.setSizeUndefined();

        genoTypingTitle = new Label("Genotyping");
        genoTypingTitle.setStyleName("gcp-section-title-large");
        //genoTypingTitle.setSizeUndefined();

        projectPlanningTitle = new Label("Project Planning");
        projectPlanningTitle.setStyleName("gcp-section-title-large");
        //projectPlanningTitle.setSizeUndefined();
        
        breedingManagementTitle = new Label("Breeding Management");
        breedingManagementTitle.setStyleName("gcp-section-title-large");
        //breedingManagementTitle.setSizeUndefined();

        analysisPipelineTitle = new Label("Analysis Pipeline");
        analysisPipelineTitle.setStyleName("gcp-section-title-large");
        //analysisPipelineTitle.setSizeUndefined();

        decisionSupportTitle = new Label("Decision Support");
        decisionSupportTitle.setStyleName("gcp-section-title-large");
        //decisionSupportTitle.setSizeUndefined();
        
        membersButton = new Button("Members");
        membersButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        membersButton.setSizeUndefined();
        membersButton.setDescription(messageSource.getMessage(Message.MEMBERS_LINK_DESC));
        
        breedingPlannerButton = new Button(messageSource.getMessage(Message.BREEDING_PLANNER_LINK));
        breedingPlannerButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingPlannerButton.setSizeUndefined();
        breedingPlannerButton.setDescription("Click to launch the freestanding Breeding Planner application.");
        
        browseGermplasmButton = new Button("Germplasm Browser");
        browseGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        browseGermplasmButton.setSizeUndefined();
        browseGermplasmButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_GERMPLASM_BROWSER));

        browseStudiesButton = new Button("Study Browser");
        browseStudiesButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        browseStudiesButton.setSizeUndefined();
        browseStudiesButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_STUDY_BROWSER));

        browseGermplasmListsButton = new Button("Germplasm List Browser");
        browseGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        browseGermplasmListsButton.setSizeUndefined();
        browseGermplasmListsButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_GERMPLASM_LIST_BROWSER));
        
        manageGermplasmListsButton = new Button("List Manager");
        manageGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        manageGermplasmListsButton.setSizeUndefined();
        manageGermplasmListsButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_LIST_MANAGER));
        
        breedingViewButton = new Button("Breeding View Standalone (all analysis)");
        breedingViewButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link" + " gcp-linkwrap");
        breedingViewButton.setSizeUndefined();
        breedingViewButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW));
        
        breedingViewSingleSiteAnalysisCentralButton = new Button("Single-Site Analysis for Central Datasets");
        breedingViewSingleSiteAnalysisCentralButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingViewSingleSiteAnalysisCentralButton.setSizeUndefined();
        breedingViewSingleSiteAnalysisCentralButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW_SINGLE_SITE_ANALYSIS_CENTRAL));
        
        breedingViewSingleSiteAnalysisLocalButton = new Button("Single-Site Analysis for Local Datasets");
        breedingViewSingleSiteAnalysisLocalButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingViewSingleSiteAnalysisLocalButton.setSizeUndefined();
        breedingViewSingleSiteAnalysisLocalButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL));
        
        breedingViewGxeAnalysisLocalButton = new Button("GxE Analysis");
        breedingViewGxeAnalysisLocalButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingViewGxeAnalysisLocalButton.setSizeUndefined();
        breedingViewGxeAnalysisLocalButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL));
        
        breedingViewMultiSiteAnalysisButton = new Button();
        breedingViewMultiSiteAnalysisButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingViewMultiSiteAnalysisButton.setSizeUndefined();
        breedingViewMultiSiteAnalysisButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_VIEW_MULTI_SITE_ANALYSIS));
        
        gdmsButton = new Button("GDMS");
        gdmsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        gdmsButton.setSizeUndefined();
        gdmsButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_GDMS));
        
        mbdtButton = new Button("MBDT"); 
        mbdtButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        mbdtButton.setSizeUndefined();
        mbdtButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_MBDT));
        
        fieldbookButton = new Button("Fieldbook");
        fieldbookButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        fieldbookButton.setSizeUndefined();
        fieldbookButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_FIELDBOOK));
        
        optimasButton = new Button("OptiMAS");
        optimasButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        optimasButton.setSizeUndefined();
        optimasButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_OPTIMAS));
        
        nurseryManagerButton = new Button("Nursery Manager");
        nurseryManagerButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        nurseryManagerButton.setSizeUndefined();
        nurseryManagerButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_NURSERY_MANAGER));
        
        germplasmImportButton = new Button("Germplasm Import");
        germplasmImportButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        germplasmImportButton.setSizeUndefined();
        germplasmImportButton.setDescription("Click to launch Fieldbook on Nursery Manager View.");
        
        projectMethodsButton = new Button("Project Methods");
        projectMethodsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        projectMethodsButton.setSizeUndefined();
        projectMethodsButton.setDescription(messageSource.getMessage(Message.PROJECT_METHODS_DESC));
        
        projectLocationButton = new Button("Project Locations");
        projectLocationButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        projectLocationButton.setSizeUndefined();
        projectLocationButton.setDescription(messageSource.getMessage(Message.PROJECT_LOCATIONS_DESC));

        backupIBDBButton = new Button(messageSource.getMessage(Message.BACKUP_IBDB_LINK));
        backupIBDBButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        backupIBDBButton.setSizeUndefined();
        backupIBDBButton.setDescription(messageSource.getMessage(Message.BACKUP_IBDB_LINK_DESC));

        restoreIBDBButton = new Button(messageSource.getMessage(Message.RESTORE_IBDB_LINK));
        restoreIBDBButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        restoreIBDBButton.setSizeUndefined();
        restoreIBDBButton.setDescription(messageSource.getMessage(Message.RESTORE_IBDB_LINK_DESC));
        
        
        breedingManagerButton = new Button(messageSource.getMessage(Message.MANAGE_NURSERIES));
        breedingManagerButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingManagerButton.setSizeUndefined();
        breedingManagerButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_BREEDING_MANAGER));

        makeCrossesButton = new Button(messageSource.getMessage(Message.CROSS_MANAGER_LINK));
        makeCrossesButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        makeCrossesButton.setSizeUndefined();
        makeCrossesButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_CROSSING_MANAGER));
        
        createTemplatesButton = new Button(messageSource.getMessage(Message.NURSERY_TEMPLATE));
        createTemplatesButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        createTemplatesButton.setSizeUndefined();
        createTemplatesButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_NURSERY_TEMPLATE));
    }

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);
        setWidth("1000px");

        dashboardTitle.setSizeUndefined();
        addComponent(dashboardTitle);

        Component workFlowArea = layoutWorkflowArea();
        workFlowArea.setSizeUndefined();
        addComponent(workFlowArea);

    }

    protected Component layoutWorkflowArea() {
        Panel panel = new Panel();
        panel.setStyleName(Reindeer.PANEL_LIGHT);
        
        AbsoluteLayout layout = new AbsoluteLayout();
        
        layout.setMargin(true);
        layout.setWidth("850px");
        layout.setHeight("420px");
        
        String extraSpace = EXTRA_SPACE_BETWEEN_COMPONENTS + "px";
        int top = 10;
        String topInPixels = "";
        
        //the steps on the first column
        Component populationManagementArea = layoutAdministration();
        layout.addComponent(populationManagementArea, "top:" + extraSpace + "; left:" + extraSpace);
        
        top = top + WORKFLOW_STEP_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        Component fieldTrialArea = layoutProjectPlanning();
        layout.addComponent(fieldTrialArea, "top:" + topInPixels  + "; left:" + extraSpace);
        
        //the steps on the second column   
        top = EXTRA_SPACE_BETWEEN_COMPONENTS; 
        topInPixels = top + "px";
        int left = EXTRA_SPACE_BETWEEN_COMPONENTS + WORKFLOW_STEP_WIDTH + EXTRA_SPACE_BETWEEN_COMPONENTS;
        String leftInPixels = left + "px";
        
        Component projectPlanningArea = layoutBreedingManagement();
        layout.addComponent(projectPlanningArea, "top:" + topInPixels + "; left:" + leftInPixels);
        
        top = top + WORKFLOW_STEP_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        Component genotypingArea = layoutDataManagement();
        layout.addComponent(genotypingArea, "top:" + topInPixels  + "; left:" + leftInPixels);
        
        //the steps on the third column
        top = EXTRA_SPACE_BETWEEN_COMPONENTS; 
        topInPixels = top + "px";
        left = EXTRA_SPACE_BETWEEN_COMPONENTS + WORKFLOW_STEP_WIDTH + EXTRA_SPACE_BETWEEN_COMPONENTS
            + WORKFLOW_STEP_WIDTH + EXTRA_SPACE_BETWEEN_COMPONENTS;
        leftInPixels = left + "px";
        
        Component progenySelectionArea = layoutAnalysisPipeline();
        layout.addComponent(progenySelectionArea, "top:" + topInPixels  + "; left:" + leftInPixels);

        top = top + WORKFLOW_STEP_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        Component projectCompletionArea = layoutDecisionSupport();
        layout.addComponent(projectCompletionArea, "top:" + topInPixels  + "; left:" + leftInPixels);

        panel.setContent(layout);
        return panel;
    }

    protected Component layoutProjectPlanning() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);
        
        layout.addComponent(projectPlanningTitle);
        layout.setComponentAlignment(projectPlanningTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(projectPlanningTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        layout.addComponent(breedingPlannerButton);
        breedingPlannerButton.setHeight("20px");
        layout.setComponentAlignment(breedingPlannerButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingPlannerButton, 0);
        
        
        layout.addComponent(browseGermplasmButton);
        browseGermplasmButton.setHeight("20px");
        layout.setComponentAlignment(browseGermplasmButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseGermplasmButton, 0);
        
        layout.addComponent(browseGermplasmListsButton);
        browseGermplasmListsButton.setHeight("20px");
        layout.setComponentAlignment(browseGermplasmListsButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseGermplasmListsButton, 0);

        layout.addComponent(browseStudiesButton);
        //browseStudiesButton.setHeight("20px");
        layout.setComponentAlignment(browseStudiesButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseStudiesButton, 0);
        
        
        /*
        layout.addComponent(gdmsButton);
        layout.setComponentAlignment(gdmsButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(gdmsButton, 0);
        */
        
        return layout;
    }

    protected Component layoutAdministration() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(administrationTitle);
        layout.setComponentAlignment(administrationTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(administrationTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        
        layout.addComponent(membersButton);
        membersButton.setHeight("20px");
        layout.setComponentAlignment(membersButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(membersButton, 0);

        layout.addComponent(backupIBDBButton);
        backupIBDBButton.setHeight("20px");
        layout.setComponentAlignment(backupIBDBButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(backupIBDBButton, 0);

        /*
        layout.addComponent(backupIBDBLink);
        backupIBDBLink.setHeight("20px");
        layout.setComponentAlignment(backupIBDBLink, Alignment.TOP_CENTER);
        layout.setExpandRatio(backupIBDBLink, 0);
		*/
        
        /*layout.addComponent(backupIBDBCustomLayout);
        backupIBDBCustomLayout.setHeight("20px");
        layout.setComponentAlignment(backupIBDBCustomLayout, Alignment.TOP_CENTER);
        layout.setExpandRatio(backupIBDBCustomLayout, 0);*/

        layout.addComponent(restoreIBDBButton);
        restoreIBDBButton.setHeight("20px");
        layout.setComponentAlignment(restoreIBDBButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(restoreIBDBButton, 0);

        
        layout.addComponent(projectMethodsButton);
        projectMethodsButton.setHeight("20px");
        layout.setComponentAlignment(projectMethodsButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(projectMethodsButton, 0);
        
        layout.addComponent(projectLocationButton);
        projectLocationButton.setHeight("20px");
        layout.setComponentAlignment(projectLocationButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(projectLocationButton, 0);
        
        layout.addComponent(createTemplatesButton);
        layout.setComponentAlignment(createTemplatesButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(createTemplatesButton, 0);
        
        return layout;
    }

    protected Component layoutDataManagement() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(genoTypingTitle);
        layout.setComponentAlignment(genoTypingTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(genoTypingTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        layout.addComponent(fieldbookButton);
        fieldbookButton.setHeight("20px");
        layout.setComponentAlignment(fieldbookButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(fieldbookButton, 0);
        
        layout.addComponent(gdmsButton);
        layout.setComponentAlignment(gdmsButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(gdmsButton, 0);

        
        /*
        layout.addComponent(projectMethodsButton);
        projectMethodsButton.setHeight("20px");
        layout.setComponentAlignment(projectMethodsButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(projectMethodsButton, 0);
        
        layout.addComponent(projectLocationButton);
        browseGermplasmButton.setHeight("20px");
        layout.setComponentAlignment(projectLocationButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(projectLocationButton, 0);
         */
        return layout;
    }

    protected Component layoutBreedingManagement() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(breedingManagementTitle);
        layout.setComponentAlignment(breedingManagementTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingManagementTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        
        layout.addComponent(germplasmImportButton);
        germplasmImportButton.setHeight("20px");
        layout.setComponentAlignment(germplasmImportButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(germplasmImportButton, 0);
        
        layout.addComponent(manageGermplasmListsButton);
        manageGermplasmListsButton.setHeight("20px");
        layout.setComponentAlignment(manageGermplasmListsButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(manageGermplasmListsButton, 0);
                
        layout.addComponent(makeCrossesButton);
        makeCrossesButton.setHeight("20px");
        layout.setComponentAlignment(makeCrossesButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(makeCrossesButton, 0);

        layout.addComponent(nurseryManagerButton);
        //nurseryManagerButton.setHeight("20px");
        layout.setComponentAlignment(nurseryManagerButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(nurseryManagerButton, 0);
        
        /*
        layout.addComponent(breedingManagerButton);
        layout.setComponentAlignment(breedingManagerButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingManagerButton, 0);
        */
        return layout;
    }

    protected Component layoutAnalysisPipeline() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(analysisPipelineTitle);
        layout.setComponentAlignment(analysisPipelineTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(analysisPipelineTitle, 0);
        
        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("68px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(breedingViewSingleSiteAnalysisLocalButton);
        breedingViewSingleSiteAnalysisLocalButton.setHeight("20px");
        layout.setComponentAlignment(breedingViewSingleSiteAnalysisLocalButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewSingleSiteAnalysisLocalButton, 0);
        
        /*layout.addComponent(breedingViewSingleSiteAnalysisCentralButton);
        breedingViewSingleSiteAnalysisCentralButton.setHeight("20px");
        layout.setComponentAlignment(breedingViewSingleSiteAnalysisCentralButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewSingleSiteAnalysisCentralButton, 0);
		*/
        /*
        layout.addComponent(breedingViewGxeAnalysisLocalButton);
        breedingViewGxeAnalysisLocalButton.setHeight("20px");
        layout.setComponentAlignment(breedingViewGxeAnalysisLocalButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewGxeAnalysisLocalButton, 0);
        */

        layout.addComponent(breedingViewMultiSiteAnalysisButton);
        breedingViewMultiSiteAnalysisButton.setHeight("20px");
        layout.setComponentAlignment(breedingViewMultiSiteAnalysisButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewMultiSiteAnalysisButton, 0);

        
        layout.addComponent(breedingViewButton);
        //breedingViewMultiSiteAnalysisButton.setHeight("28px");
        layout.setComponentAlignment(breedingViewButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewButton, 0);
        
        return layout;
    }

    protected Component layoutDecisionSupport() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(decisionSupportTitle);
        layout.setComponentAlignment(decisionSupportTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(decisionSupportTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(mbdtButton);
        mbdtButton.setHeight("20px");
        layout.setComponentAlignment(mbdtButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(mbdtButton, 0);
        
        layout.addComponent(optimasButton);
        //optimasButton.setHeight("20px");
        layout.setComponentAlignment(optimasButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(optimasButton, 0);
          
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
        layout.setMargin(false,false,true,false);
    }
    
    protected void initializeActions() {
        if (!workflowPreview) {
            browseGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_BROWSER));
            browseStudiesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.STUDY_BROWSER));
            browseGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_LIST_BROWSER));
            
            breedingPlannerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_PLANNER)); //TODO: change this to run breeding planner tool
            gdmsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GDMS));
            mbdtButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.MBDT));
            breedingViewButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW));
            breedingViewSingleSiteAnalysisCentralButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW, project, WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_CENTRAL));
            breedingViewSingleSiteAnalysisLocalButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW, project, WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL));

            //breedingViewGxeAnalysisLocalButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_GXE));

            //breedingViewMultiSiteAnalysisButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW,project,WorkflowConstants.BREEDING_VIEW_MULTI_SITE_ANALYSIS));
            breedingViewMultiSiteAnalysisButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_GXE,this.project,this.role,null));
            
            fieldbookButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.FIELDBOOK));
            
            membersButton.addListener(new ChangeWindowAction(WindowEnums.MEMBER, this.project));
            
            backupIBDBButton.addListener(new OpenWindowAction(WindowEnum.BACKUP_IBDB,this.project));
            
            restoreIBDBButton.addListener(new OpenWindowAction(WindowEnum.RESTORE_IBDB,this.project));
            
            germplasmImportButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));
            optimasButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.OPTIMAS));
            manageGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));
            nurseryManagerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));
            
            projectLocationButton.addListener(new OpenProjectLocationAction(project, role));
            projectMethodsButton.addListener(new OpenProjectMethodsAction(project, role));
            
            makeCrossesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.CROSSING_MANAGER));
            breedingManagerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));
            createTemplatesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.NURSERY_TEMPLATE_WIZARD));
            
        }
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
        
        initializeLabels();
        
        if (workflowPreview)
        	this.setStyleName("gcp-removelink");
        
    }
    
    private void initializeLabels() {
    	messageSource.setValue(administrationTitle,Message.ADMINISTRATION_TITLE);
        messageSource.setCaption(membersButton,Message.MEMBERS_LINK);
        messageSource.setCaption(backupIBDBButton,Message.BACKUP_IBDB_LINK);
        messageSource.setCaption(restoreIBDBButton,Message.RESTORE_IBDB_LINK);
        messageSource.setCaption(projectMethodsButton,Message.PROJECT_METHODS_LINK);
        messageSource.setCaption(projectLocationButton,Message.PROJECT_LOCATIONS_LINK);
        
        messageSource.setValue(projectPlanningTitle,Message.PROJECT_PLANNING);
        messageSource.setCaption(browseGermplasmButton,Message.GERMPLASM_BROWSER_LINK);
        messageSource.setCaption(browseStudiesButton,Message.STUDY_BROWSER_LINK);
        messageSource.setCaption(browseGermplasmListsButton,Message.GERMPLASM_LIST_BROWSER_LINK);
        
        messageSource.setValue(analysisPipelineTitle,Message.ANALYSIS_PIPELINE_TITLE);
        messageSource.setCaption(breedingViewSingleSiteAnalysisLocalButton,Message.SINGLE_SITE_ANALYSIS_LINK);
        messageSource.setCaption(breedingViewSingleSiteAnalysisCentralButton,Message.SINGLE_SITE_ANALYSIS_CENTRAL_LINK);
        messageSource.setCaption(breedingViewMultiSiteAnalysisButton,Message.MULTI_SITE_ANALYSIS_LINK);
        messageSource.setCaption(breedingViewButton,Message.BREEDING_VIEW_QTL);
        messageSource.setValue(genoTypingTitle,Message.DATA_MANAGEMENT_TITLE);
        messageSource.setCaption(gdmsButton,Message.GDMS_LINK);
        
        messageSource.setValue(breedingManagementTitle,Message.BREEDING_MANAGEMENT_TITLE);
        messageSource.setCaption(fieldbookButton,Message.FIELDBOOK);
        messageSource.setCaption(nurseryManagerButton,Message.NURSERY_MANAGER_LINK);
        
        
        messageSource.setValue(decisionSupportTitle,Message.DECISION_SUPPORT_TITLE);
        messageSource.setCaption(optimasButton, Message.OPTIMAS);
        messageSource.setCaption(mbdtButton,Message.MBDT_LINK);
    }

	@Override
    public void attach() {
        super.attach();        
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        if (workflowPreview) {
            messageSource.setValue(dashboardTitle, Message.WORKFLOW_PREVIEW_TITLE, "MENU");
        } else {
            messageSource.setValue(dashboardTitle, Message.PROJECT_TITLE, project.getProjectName());
        }
    }
}
