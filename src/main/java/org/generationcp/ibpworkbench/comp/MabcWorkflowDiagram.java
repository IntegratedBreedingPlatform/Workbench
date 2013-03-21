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
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction.ToolEnum;
import org.generationcp.middleware.pojos.workbench.Project;
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

@Configurable
public class MabcWorkflowDiagram extends VerticalLayout implements WorkflowConstants, InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;

    //this is in pixels and used for layouting
    private static final int WORKFLOW_STEP_HEIGHT = 110;
    private static final int WORKFLOW_STEP_EXTRA_HEIGHT = 130;
    private static final int WORKFLOW_STEP_WIDTH = 270;
    private static final int EXTRA_SPACE_BETWEEN_COMPONENTS = 10;
    private static final int ARROW_IMAGE_HEIGHT = 30;
    private static final int ARROW_IMAGE_WIDTH = 40;
    private static final String FIRST_COLUMN_LEFT_FOR_ARROWS = "135px";
    private static final String DOWN_ARROW_THEME_RESOURCE = "../gcp-default/images/blc-arrow-d.png";
    private static final String TWO_HEADED_ARROW_THEME_RESOURCE = "../gcp-default/images/blc-arrow-lr.png";
    
    private boolean workflowPreview;
    
    private Project project;

    private Label dashboardTitle;

    private Label projectPlanningTitle;
    private Label backcrossingTitle;
    private Label fieldTrialManagementTitle;
    
    private Label genotypingTitle;
    private Label statisticalAnalysisTitle;
    private Label breedingDecisionTitle;

    //links for tools
    private Button browseGermplasmButton;
    private Button browseStudiesButton;
    private Button browseGermplasmListsButton;
    private Button gdmsButton;
    private Button breedingViewButton;
    private Button breedingViewSingleSiteAnalysisCentralButton;
    private Button breedingViewSingleSiteAnalysisLocalButton;
    private Button fieldbookButton;
    private Button optimasButton;
    private Button mbdtButton;
    
    private Embedded downArrowImage1;
    private Embedded downArrowImage2;
    private Embedded downArrowImage3;
    private Embedded downArrowImage4;
    private Embedded twoHeadedArrowImage;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public MabcWorkflowDiagram(boolean workflowPreview, Project project) {
        this.workflowPreview = workflowPreview;
        
        if (!workflowPreview) {
            this.project = project;
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

        projectPlanningTitle = new Label("Project Planning");
        projectPlanningTitle.setStyleName("gcp-section-title-large");
        projectPlanningTitle.setSizeUndefined();

        backcrossingTitle = new Label("Backcrossing");
        backcrossingTitle.setStyleName("gcp-section-title-large");
        backcrossingTitle.setSizeUndefined();

        fieldTrialManagementTitle = new Label("Field Trial Management");
        fieldTrialManagementTitle.setStyleName("gcp-section-title-large");
        fieldTrialManagementTitle.setSizeUndefined();

        genotypingTitle = new Label("Genotyping");
        genotypingTitle.setStyleName("gcp-section-title-large");
        genotypingTitle.setSizeUndefined();

        statisticalAnalysisTitle = new Label("Statistical Analysis");
        statisticalAnalysisTitle.setStyleName("gcp-section-title-large");
        statisticalAnalysisTitle.setSizeUndefined();

        breedingDecisionTitle = new Label("Breeding Decision");
        breedingDecisionTitle.setStyleName("gcp-section-title-large");
        breedingDecisionTitle.setSizeUndefined();
        
        browseGermplasmButton = new Button("Browse Germplasm Information");
        browseGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK);
        browseGermplasmButton.setSizeUndefined();
        browseGermplasmButton.setDescription("Click to launch Germplasm Browser");

        browseStudiesButton = new Button("Browse Studies and Datasets");
        browseStudiesButton.setStyleName(BaseTheme.BUTTON_LINK);
        browseStudiesButton.setSizeUndefined();
        browseStudiesButton.setDescription("Click to launch Study Browser");

        browseGermplasmListsButton = new Button("Browse Germplasm Lists");
        browseGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK);
        browseGermplasmListsButton.setSizeUndefined();
        browseGermplasmListsButton.setDescription("Click to launch Germplasm List Browser");
        
        breedingViewButton = new Button("Breeding View Standalone (all analysis)");
        breedingViewButton.setStyleName(BaseTheme.BUTTON_LINK);
        breedingViewButton.setSizeUndefined();
        breedingViewButton.setDescription("Click to launch Breeding View");
        
        breedingViewSingleSiteAnalysisCentralButton = new Button("Single-Site Analysis for Central Datasets");
        breedingViewSingleSiteAnalysisCentralButton.setStyleName(BaseTheme.BUTTON_LINK);
        breedingViewSingleSiteAnalysisCentralButton.setSizeUndefined();
        breedingViewSingleSiteAnalysisCentralButton.setDescription("Click to launch Single-Site Analysis on Study Datasets from Central IBDB");
        
        breedingViewSingleSiteAnalysisLocalButton = new Button("Single-Site Analysis for Local Datasets");
        breedingViewSingleSiteAnalysisLocalButton.setStyleName(BaseTheme.BUTTON_LINK);
        breedingViewSingleSiteAnalysisLocalButton.setSizeUndefined();
        breedingViewSingleSiteAnalysisLocalButton.setDescription("Click to launch Single-Site Analysis on Study Datasets from Local IBDB");

        gdmsButton = new Button("Manage Genotyping Data");
        gdmsButton.setStyleName(BaseTheme.BUTTON_LINK);
        gdmsButton.setSizeUndefined();
        gdmsButton.setDescription("Click to launch GDMS");
        
        mbdtButton = new Button("MBDT");
        mbdtButton.setStyleName(BaseTheme.BUTTON_LINK);
        mbdtButton.setSizeUndefined();
        mbdtButton.setDescription("Click to launch MBDT");
        
        fieldbookButton = new Button("Fieldbook");
        fieldbookButton.setStyleName(BaseTheme.BUTTON_LINK);
        fieldbookButton.setSizeUndefined();
        fieldbookButton.setDescription("Click to launch Fieldbook");
        
        optimasButton = new Button("OptiMAS");
        optimasButton.setStyleName(BaseTheme.BUTTON_LINK);
        optimasButton.setSizeUndefined();
        optimasButton.setDescription("Click to launch OptiMAS");
        
        downArrowImage1 = new Embedded("", new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        downArrowImage2 = new Embedded("", new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        downArrowImage3 = new Embedded("", new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        downArrowImage4 = new Embedded("", new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        twoHeadedArrowImage = new Embedded("", new ThemeResource(TWO_HEADED_ARROW_THEME_RESOURCE));
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

        AbsoluteLayout layout = new AbsoluteLayout();
        layout.setMargin(true);
        layout.setWidth("620px");
        layout.setHeight("800px");
        
        String extraSpace = EXTRA_SPACE_BETWEEN_COMPONENTS + "px";
        int top = 10;
        String topInPixels = "";
        
        //the steps on the first column
        Component projectPlanningArea = layoutProjectPlanning();
        layout.addComponent(projectPlanningArea, "top:" + extraSpace + "; left:" + extraSpace);
        
        top = top + WORKFLOW_STEP_EXTRA_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        layout.addComponent(downArrowImage1, "top:" + topInPixels + "; left:" + FIRST_COLUMN_LEFT_FOR_ARROWS); 
        
        top = top + ARROW_IMAGE_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        Component backcrossingArea = layoutBackcrossing();
        layout.addComponent(backcrossingArea, "top:" + topInPixels  + "; left:" + extraSpace);
        
        top = top + WORKFLOW_STEP_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
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

        top = top + WORKFLOW_STEP_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        layout.addComponent(downArrowImage4, "top:" + topInPixels + "; left:" + FIRST_COLUMN_LEFT_FOR_ARROWS);
        
        top = top + ARROW_IMAGE_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS;
        topInPixels = top + "px";
        Component breedingDecisionArea = layoutBreedingDecision();
        layout.addComponent(breedingDecisionArea, "top:" + topInPixels  + "; left:" + extraSpace);
        
        //the steps on the second column   
        top = EXTRA_SPACE_BETWEEN_COMPONENTS + WORKFLOW_STEP_EXTRA_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS
                + ARROW_IMAGE_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS; 
        topInPixels = top + "px";
        int left = EXTRA_SPACE_BETWEEN_COMPONENTS + WORKFLOW_STEP_WIDTH + EXTRA_SPACE_BETWEEN_COMPONENTS
                + ARROW_IMAGE_WIDTH + EXTRA_SPACE_BETWEEN_COMPONENTS;
        String leftInPixels = left + "px";
        
        Component genotypingArea = layoutGenotypingStep();
        layout.addComponent(genotypingArea, "top:" + topInPixels  + "; left:" + leftInPixels);
        
        top = EXTRA_SPACE_BETWEEN_COMPONENTS + WORKFLOW_STEP_EXTRA_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS
                + ARROW_IMAGE_HEIGHT + EXTRA_SPACE_BETWEEN_COMPONENTS + 40; 
        topInPixels = top + "px";
        left = EXTRA_SPACE_BETWEEN_COMPONENTS + WORKFLOW_STEP_WIDTH + EXTRA_SPACE_BETWEEN_COMPONENTS
                + EXTRA_SPACE_BETWEEN_COMPONENTS;
        leftInPixels = left + "px";
        layout.addComponent(twoHeadedArrowImage, "top:" + topInPixels + "; left:" + leftInPixels);

        panel.setContent(layout);
        return panel;
    }

    protected Component layoutProjectPlanning() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);
        layout.setHeight(WORKFLOW_STEP_EXTRA_HEIGHT + "px");
        
        layout.addComponent(projectPlanningTitle);
        layout.setComponentAlignment(projectPlanningTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(projectPlanningTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        layout.addComponent(browseGermplasmButton);
        browseGermplasmButton.setHeight("20px");
        layout.setComponentAlignment(browseGermplasmButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseGermplasmButton, 0);

        layout.addComponent(browseStudiesButton);
        browseStudiesButton.setHeight("20px");
        layout.setComponentAlignment(browseStudiesButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseStudiesButton, 0);
        
        layout.addComponent(browseGermplasmListsButton);
        layout.setComponentAlignment(browseGermplasmListsButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseGermplasmListsButton, 0);

        return layout;
    }

    protected Component layoutBackcrossing() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(backcrossingTitle);
        layout.setComponentAlignment(backcrossingTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(backcrossingTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

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

    protected Component layoutGenotypingStep() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(genotypingTitle);
        layout.setComponentAlignment(genotypingTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(genotypingTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(gdmsButton);
        layout.setComponentAlignment(gdmsButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(gdmsButton, 0);
        
        

        return layout;
    }

    protected Component layoutStatisticalAnalysis() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(statisticalAnalysisTitle);
        layout.setComponentAlignment(statisticalAnalysisTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(statisticalAnalysisTitle, 0);

        layout.addComponent(breedingViewSingleSiteAnalysisLocalButton);
        breedingViewSingleSiteAnalysisLocalButton.setHeight("20px");
        layout.setComponentAlignment(breedingViewSingleSiteAnalysisLocalButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewSingleSiteAnalysisLocalButton, 0);
        
        layout.addComponent(breedingViewSingleSiteAnalysisCentralButton);
        breedingViewSingleSiteAnalysisCentralButton.setHeight("20px");
        layout.setComponentAlignment(breedingViewSingleSiteAnalysisCentralButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewSingleSiteAnalysisCentralButton, 0);

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

        layout.addComponent(mbdtButton);
        layout.setComponentAlignment(mbdtButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(mbdtButton, 0);
        
        /*
        layout.addComponent(optimasButton);
        layout.setComponentAlignment(optimasButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(optimasButton, 0);
        */
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
            button.setStyleName(BaseTheme.BUTTON_LINK);

            layout.addComponent(button);
            layout.setComponentAlignment(button, Alignment.TOP_CENTER);
            layout.setExpandRatio(button, 0);
        }

        return layout;
    }

    protected void configureWorkflowStepLayout(VerticalLayout layout) {
        layout.setWidth(WORKFLOW_STEP_WIDTH + "px");
        layout.setHeight(WORKFLOW_STEP_HEIGHT + "px");
        layout.setStyleName("gcp-mars-workflow-step");
        layout.setMargin(true, true, true, true);
    }
    
    protected void initializeActions() {
        if (!workflowPreview) {
        	mbdtButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.MBDT));
            browseGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_BROWSER));
            browseStudiesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.STUDY_BROWSER));
            browseGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_LIST_BROWSER));
            gdmsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GDMS));
            breedingViewButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW));
            breedingViewSingleSiteAnalysisCentralButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW, project, WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_CENTRAL));
            breedingViewSingleSiteAnalysisLocalButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW, project, WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL));
            fieldbookButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.FIELDBOOK));
            optimasButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.OPTIMAS));
        }
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
    
    @Override
    public void attach() {
        super.attach();        
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        if (workflowPreview) {
            messageSource.setValue(dashboardTitle, Message.WORKFLOW_PREVIEW_TITLE, "MABC");
        } else {
            messageSource.setValue(dashboardTitle, Message.PROJECT_TITLE, project.getProjectName());
        }
    }
}
