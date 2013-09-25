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
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.Role;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class MarsProjectDashboard extends VerticalLayout implements InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;
    
    private static final String DOWN_ARROW_THEME_RESOURCE = "images/blc-arrow-d.png";
    
    private boolean workflowPreview;

    private Project project;

    private Label dashboardTitle;

    // titles
    private Label projectPlanningTitle;
    private Label populationDevelopmentTitle;
    private Label fieldTrialManagementTitle;
    private Label genotypingTitle;
    private Button headToHeadButton;
    private Button headToHeadButton2;
    private Button mainHeadToHeadButton;
    private Button mainHeadToHeadButton2;
    private Label phenotypicAnalysisTitle;
    private Label qtlAnalysisTitle;
    private Label singleSiteAnalysisTitle;

    private Label qtlSelectionTitle;
    
    private Label recombinationCycleTitle;
    
    private Label finalBreedingDecisionTitle;

    // buttons
    private Button browseGermplasmButton;
    private Button browseStudiesButton;
    private Button browseGermplasmListsButton;
    private Button browseGenotypingDataButton;

    private Button breedingManagerButton;
    
    private Button fieldBookButton;
    
    private Button gdmsButton;

    private Button phenotypicBreedingViewButton;
    private Button breedingViewSingleSiteAnalysisCentralButton;
    private Button breedingViewSingleSiteAnalysisLocalButton;
    
    private Button qtlBreedingViewButton;

    private Button optimasButton;
    
    private Button manageGermplasmListsButton;
    
    private Button breedingViewMultiSiteAnalysisButton;
    
    private Button makeCrossesButton;
    private Button recomMakeCrossesButton;

    private Embedded downArrow11;
    private Embedded downArrow12;
    private Embedded downArrow13;
    
    private Embedded downArrow21;
    
    private Embedded downArrow31;
    private Embedded downArrow32;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

	private Button recomBreedingManagerButton;

	private Role role;

	private Button breedingPlannerButton;

	private Button germplasmImportButton;

	
    public MarsProjectDashboard(boolean workflowPreview, Project project,Role role) {
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

        // project planning
        projectPlanningTitle = new Label();
        projectPlanningTitle.setStyleName("gcp-section-title");
        //projectPlanningTitle.setSizeUndefined();

        populationDevelopmentTitle = new Label();
        populationDevelopmentTitle.setStyleName("gcp-section-title");
        //populationDevelopmentTitle.setSizeUndefined();

        fieldTrialManagementTitle = new Label();
        fieldTrialManagementTitle.setStyleName("gcp-section-title");
        //fieldTrialManagementTitle.setSizeUndefined();

        genotypingTitle = new Label();
        genotypingTitle.setStyleName("gcp-section-title");
        //genotypingTitle.setSizeUndefined();

        phenotypicAnalysisTitle = new Label();
        phenotypicAnalysisTitle.setStyleName("gcp-section-title");
        //phenotypicAnalysisTitle.setSizeUndefined();
        
        qtlAnalysisTitle = new Label();
        qtlAnalysisTitle.setStyleName("gcp-section-title");
        //qtlAnalysisTitle.setSizeUndefined();
        
        singleSiteAnalysisTitle = new Label();
        singleSiteAnalysisTitle.setStyleName("gcp-section-title");
        //singleSiteAnalysisTitle.setSizeUndefined();
        
        qtlSelectionTitle = new Label();
        qtlSelectionTitle.setStyleName("gcp-section-title");
        //qtlSelectionTitle.setSizeUndefined();
        
        recombinationCycleTitle = new Label();
        recombinationCycleTitle.setStyleName("gcp-section-title");
        //recombinationCycleTitle.setSizeUndefined();
        
        finalBreedingDecisionTitle = new Label();
        finalBreedingDecisionTitle.setStyleName("gcp-section-title");
        //finalBreedingDecisionTitle.setSizeUndefined();

        // project planning buttons
        browseGermplasmButton = new Button();
        browseGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        browseGermplasmButton.setSizeUndefined();

        browseStudiesButton = new Button();
        browseStudiesButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        browseStudiesButton.setSizeUndefined();
        
        browseGermplasmListsButton = new Button();
        browseGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        browseGermplasmListsButton.setSizeUndefined();
        
        browseGenotypingDataButton = new Button("Browse Genotyping Data");
        browseGenotypingDataButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        browseGenotypingDataButton.setSizeUndefined();
        browseGenotypingDataButton.setDescription("Click to launch genotyping data");
        
        
        // population development buttons
        breedingManagerButton = new Button();
        breedingManagerButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingManagerButton.setSizeUndefined();
        
        breedingPlannerButton = new Button(messageSource.getMessage(Message.BREEDING_PLANNER_MARS));
        breedingPlannerButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingPlannerButton.setSizeUndefined();
        breedingPlannerButton.setDescription("Click to launch the freestanding Breeding Planner application.");
        
        germplasmImportButton = new Button("Import Germplasm Lists");
        germplasmImportButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        germplasmImportButton.setSizeUndefined();
        germplasmImportButton.setDescription("Click to launch Fieldbook on Nursery Manager View.");
        
        // field trial management buttons
        fieldBookButton = new Button();
        fieldBookButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        fieldBookButton.setSizeUndefined();

        // genotyping buttons
        gdmsButton = new Button();
        gdmsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        gdmsButton.setSizeUndefined();

        // phenotypic analysis buttons
        phenotypicBreedingViewButton = new Button();
        phenotypicBreedingViewButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        phenotypicBreedingViewButton.setSizeUndefined();

        // qtl analysis buttons
        qtlBreedingViewButton = new Button();
        qtlBreedingViewButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        qtlBreedingViewButton.setSizeUndefined();
        
        breedingViewSingleSiteAnalysisCentralButton = new Button("Single-Site Analysis for Central Datasets");
        breedingViewSingleSiteAnalysisCentralButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingViewSingleSiteAnalysisCentralButton.setSizeUndefined();
        breedingViewSingleSiteAnalysisCentralButton.setDescription("Click to launch Single-Site Analysis on Study Datasets from Central IBDB");
        
        breedingViewSingleSiteAnalysisLocalButton = new Button("Single-Site Analysis for Local Datasets");
        breedingViewSingleSiteAnalysisLocalButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingViewSingleSiteAnalysisLocalButton.setSizeUndefined();
        breedingViewSingleSiteAnalysisLocalButton.setDescription("Click to launch Single-Site Analysis on Study Datasets from Local IBDB");
        
        // recombination cycle selection buttons
        optimasButton = new Button();
        optimasButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        optimasButton.setSizeUndefined();
        
        recomBreedingManagerButton = new Button();
        recomBreedingManagerButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        recomBreedingManagerButton.setSizeUndefined();
        
        
        manageGermplasmListsButton = new Button();
        manageGermplasmListsButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        manageGermplasmListsButton.setSizeUndefined();
        
        breedingViewMultiSiteAnalysisButton = new Button();
        breedingViewMultiSiteAnalysisButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        breedingViewMultiSiteAnalysisButton.setSizeUndefined();
        
        makeCrossesButton = new Button();
        makeCrossesButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        makeCrossesButton.setSizeUndefined();
        
        recomMakeCrossesButton = new Button();
        recomMakeCrossesButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        recomMakeCrossesButton.setSizeUndefined();
        
        // arrows
        downArrow11 = new Embedded(null, new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        downArrow12 = new Embedded(null, new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        downArrow13 = new Embedded(null, new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        
        downArrow21 = new Embedded(null, new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        
        downArrow31 = new Embedded(null, new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        downArrow32 = new Embedded(null, new ThemeResource(DOWN_ARROW_THEME_RESOURCE));
        
        headToHeadButton = new Button(messageSource.getMessage(Message.HEAD_TO_HEAD_LAUNCH));
        headToHeadButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        headToHeadButton.setSizeUndefined();
        headToHeadButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_HEAD_TO_HEAD));

        headToHeadButton2 = new Button(messageSource.getMessage(Message.HEAD_TO_HEAD_LAUNCH));
        headToHeadButton2.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        headToHeadButton2.setSizeUndefined();
        headToHeadButton2.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_HEAD_TO_HEAD));
        
        mainHeadToHeadButton = new Button(messageSource.getMessage(Message.MAIN_HEAD_TO_HEAD_LAUNCH));
        mainHeadToHeadButton.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        mainHeadToHeadButton.setSizeUndefined();
        mainHeadToHeadButton.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_MAIN_HEAD_TO_HEAD));

        mainHeadToHeadButton2 = new Button(messageSource.getMessage(Message.MAIN_HEAD_TO_HEAD_LAUNCH));
        mainHeadToHeadButton2.setStyleName(BaseTheme.BUTTON_LINK + " gcp-workflow-link");
        mainHeadToHeadButton2.setSizeUndefined();
        mainHeadToHeadButton2.setDescription(messageSource.getMessage(Message.CLICK_TO_LAUNCH_MAIN_HEAD_TO_HEAD));

    }

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);
        setWidth("870px");
        setHeight("1200px");

        dashboardTitle.setSizeUndefined();
        addComponent(dashboardTitle);

        Component workFlowArea = layoutWorkflowArea();
        //workFlowArea.setSizeUndefined();
        workFlowArea.setHeight("1200px");
        addComponent(workFlowArea);

    }

    protected Component layoutWorkflowArea() {
        Panel panel = new Panel();
        panel.setStyleName(Reindeer.PANEL_LIGHT);
        HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(false);
        layout.setSpacing(false);

        Component breedingManagementArea = layoutBreedingManagementArea();
        breedingManagementArea.setHeight("100%");
        layout.addComponent(breedingManagementArea);

        Component markerTraitAnalysisArea = layoutMarkerTraitAnalysisArea();
        markerTraitAnalysisArea.setHeight("100%");
        layout.addComponent(markerTraitAnalysisArea);

        Component markerImplementationArea = layoutMarkerImplementationArea();
        markerImplementationArea.setHeight("100%");
        layout.addComponent(markerImplementationArea);

        panel.setContent(layout);
        return panel;
    }

    protected Component layoutBreedingManagementArea() {
        Panel panel = new Panel();
        panel.setStyleName(Reindeer.PANEL_LIGHT);
        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("750px");
        layout.setMargin(true);
        layout.setSpacing(true);

        Component projectPlanningArea = layoutProjectPlanning();
        layout.addComponent(projectPlanningArea);
        
        layout.addComponent(downArrow11);
        layout.setComponentAlignment(downArrow11, Alignment.MIDDLE_CENTER);

        Component populationManagementArea = layoutPopulationDevelopment();
        layout.addComponent(populationManagementArea);
        
        layout.addComponent(downArrow12);
        layout.setComponentAlignment(downArrow12, Alignment.MIDDLE_CENTER);

        Component fieldTrialArea = layoutFieldTrialManagement();
        layout.addComponent(fieldTrialArea);
        
        layout.addComponent(downArrow13);
        layout.setComponentAlignment(downArrow13, Alignment.MIDDLE_CENTER);

        Component genotypingArea = layoutGenotyping();
        layout.addComponent(genotypingArea);
        
        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

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
        emptyLabel.setHeight("5px");
        //layout.addComponent(emptyLabel);
        //layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(breedingPlannerButton);
        //breedingPlannerButton.setHeight("20px");
        layout.setComponentAlignment(breedingPlannerButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingPlannerButton, 0);
        
        layout.addComponent(browseGermplasmButton);
       // browseGermplasmButton.setHeight("20px");
        layout.setComponentAlignment(browseGermplasmButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseGermplasmButton, 0);

        layout.addComponent(browseStudiesButton);
        //browseStudiesButton.setHeight("20px");
        layout.setComponentAlignment(browseStudiesButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseStudiesButton, 0);
        
        layout.addComponent(browseGermplasmListsButton);
        //browseGermplasmListsButton.setHeight("20px");
        layout.setComponentAlignment(browseGermplasmListsButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseGermplasmListsButton, 0);
        
        layout.addComponent(browseGenotypingDataButton);
       // browseGenotypingDataButton.setHeight("20px");
        layout.setComponentAlignment(browseGenotypingDataButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseGenotypingDataButton, 0);

        layout.addComponent(headToHeadButton2);
        //headToHeadButton2.setHeight("20px");
        layout.setComponentAlignment(headToHeadButton2, Alignment.TOP_CENTER);
        layout.setExpandRatio(headToHeadButton2, 0);
        
        layout.addComponent(mainHeadToHeadButton2);
        //headToHeadButton2.setHeight("20px");
        layout.setComponentAlignment(mainHeadToHeadButton2, Alignment.TOP_CENTER);
        layout.setExpandRatio(mainHeadToHeadButton2, 0);

        
        return layout;
    }

    protected Component layoutPopulationDevelopment() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(populationDevelopmentTitle);
        layout.setComponentAlignment(populationDevelopmentTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(populationDevelopmentTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        layout.addComponent(germplasmImportButton);
        layout.setComponentAlignment(germplasmImportButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(germplasmImportButton, 0);
        
        /**layout.addComponent(manageGermplasmListsButton);
        layout.setComponentAlignment(manageGermplasmListsButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(manageGermplasmListsButton, 0);**/

        layout.addComponent(makeCrossesButton);
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

        layout.addComponent(fieldBookButton);
        layout.setComponentAlignment(fieldBookButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(fieldBookButton, 0);

        return layout;
    }

    protected Component layoutGenotyping() {
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

    protected Component layoutMarkerTraitAnalysisArea() {
        Panel panel = new Panel();
        panel.setStyleName(Reindeer.PANEL_LIGHT);

        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("750px");
        layout.setMargin(true);
        layout.setSpacing(true);

        Component markerTraitAnalysisArea = layoutPhenotypicAnalysis();
        layout.addComponent(markerTraitAnalysisArea);
        
        layout.addComponent(downArrow21);
        layout.setComponentAlignment(downArrow21, Alignment.MIDDLE_CENTER);
        
        Component qtlAnalysisArea = layoutQtlAnalysis();
        layout.addComponent(qtlAnalysisArea);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        panel.setContent(layout);
        return panel;
    }

    protected Component layoutPhenotypicAnalysis() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);
        //layout.setHeight("145px");

        layout.addComponent(phenotypicAnalysisTitle);
        layout.setComponentAlignment(phenotypicAnalysisTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(phenotypicAnalysisTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        layout.addComponent(breedingViewSingleSiteAnalysisLocalButton);
        //breedingViewSingleSiteAnalysisLocalButton.setHeight("20px");
        layout.setComponentAlignment(breedingViewSingleSiteAnalysisLocalButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewSingleSiteAnalysisLocalButton, 0);
        
        
        //layout.addComponent(breedingViewSingleSiteAnalysisCentralButton);
        //breedingViewSingleSiteAnalysisCentralButton.setHeight("20px");
        //layout.setComponentAlignment(breedingViewSingleSiteAnalysisCentralButton, Alignment.TOP_CENTER);
        //layout.setExpandRatio(breedingViewSingleSiteAnalysisCentralButton, 0);
		
        
        layout.addComponent(breedingViewMultiSiteAnalysisButton);
        //multiSiteAnalysisButton.setHeight("20px");
        layout.setComponentAlignment(breedingViewMultiSiteAnalysisButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewMultiSiteAnalysisButton, 0);
        
        layout.addComponent(phenotypicBreedingViewButton);
        layout.setComponentAlignment(phenotypicBreedingViewButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(phenotypicBreedingViewButton, 0);

        return layout;
    }
    
    protected Component layoutQtlAnalysis() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(qtlAnalysisTitle);
        layout.setComponentAlignment(qtlAnalysisTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(qtlAnalysisTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(qtlBreedingViewButton);
        layout.setComponentAlignment(qtlBreedingViewButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(qtlBreedingViewButton, 0);

        return layout;
    }
    
    protected Component layoutSingleSiteAnalysis() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(singleSiteAnalysisTitle);
        layout.setComponentAlignment(singleSiteAnalysisTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(singleSiteAnalysisTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(breedingViewSingleSiteAnalysisLocalButton);
        layout.setComponentAlignment(breedingViewSingleSiteAnalysisLocalButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewSingleSiteAnalysisLocalButton, 0);
        
        
        layout.addComponent(breedingViewSingleSiteAnalysisCentralButton);
        layout.setComponentAlignment(breedingViewSingleSiteAnalysisCentralButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewSingleSiteAnalysisCentralButton, 0);
		
        return layout;
    }

    protected Component layoutMarkerImplementationArea() {
        Panel panel = new Panel();
        panel.setStyleName(Reindeer.PANEL_LIGHT);
        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("750px");
        //layout.setWidth("274px");
        layout.setMargin(true);
        layout.setSpacing(true);

        Component qtlSelectionArea = layoutQtlSelection();
        layout.addComponent(qtlSelectionArea);
        
        layout.addComponent(downArrow31);
        layout.setComponentAlignment(downArrow31, Alignment.MIDDLE_CENTER);
        
        Component recombinationCycleArea = layoutRecombinationCycle();
        layout.addComponent(recombinationCycleArea);

        layout.addComponent(downArrow32);
        layout.setComponentAlignment(downArrow32, Alignment.MIDDLE_CENTER);
        
        Component finalBreedingDecisionArea = layoutFinalBreedingDecision();
        layout.addComponent(finalBreedingDecisionArea);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        panel.setContent(layout);
        return panel;
    }

    protected Component layoutQtlSelection() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(qtlSelectionTitle);
        layout.setComponentAlignment(qtlSelectionTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(qtlSelectionTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

       

        return layout;
    }

    protected Component layoutRecombinationCycle() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(recombinationCycleTitle);
        layout.setComponentAlignment(recombinationCycleTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(recombinationCycleTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        layout.addComponent(optimasButton);
        layout.setComponentAlignment(optimasButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(optimasButton, 0);
        
        layout.addComponent(recomMakeCrossesButton);
        layout.setComponentAlignment(recomMakeCrossesButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(recomMakeCrossesButton, 0);
        
        layout.addComponent(recomBreedingManagerButton);
        layout.setComponentAlignment(recomBreedingManagerButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(recomBreedingManagerButton, 0);
        
        return layout;
    }
    
    protected Component layoutFinalBreedingDecision() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(finalBreedingDecisionTitle);
        layout.setComponentAlignment(finalBreedingDecisionTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(finalBreedingDecisionTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        layout.addComponent(headToHeadButton);
        layout.setComponentAlignment(headToHeadButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(headToHeadButton, 0);
        
        layout.addComponent(mainHeadToHeadButton);
        layout.setComponentAlignment(mainHeadToHeadButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(mainHeadToHeadButton, 0);

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
        layout.setWidth("240px");
        layout.setHeight("150px");
        layout.setStyleName("gcp-workflow-step");
        layout.setMargin(false, false, true, false);
    }

    protected void initializeActions() {
        if (!workflowPreview) {
        	germplasmImportButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));
            breedingPlannerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_PLANNER)); //TODO
        	
            headToHeadButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.HEAD_TO_HEAD_BROWSER));      
            headToHeadButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.HEAD_TO_HEAD_BROWSER));  
            
            mainHeadToHeadButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.MAIN_HEAD_TO_HEAD_BROWSER));      
            mainHeadToHeadButton2.addListener(new LaunchWorkbenchToolAction(ToolEnum.MAIN_HEAD_TO_HEAD_BROWSER));  

            
            browseGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_BROWSER));
            browseStudiesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.STUDY_BROWSER));
            browseGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_LIST_BROWSER));
            
            browseGenotypingDataButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GDMS));
            
            gdmsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GDMS));

            fieldBookButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.FIELDBOOK));

            optimasButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.OPTIMAS));
            breedingManagerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));

            recomBreedingManagerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));
            
            phenotypicBreedingViewButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW));
            qtlBreedingViewButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW));
            
            breedingViewSingleSiteAnalysisCentralButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW, project, WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_CENTRAL));
            breedingViewSingleSiteAnalysisLocalButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_VIEW, project, WorkflowConstants.BREEDING_VIEW_SINGLE_SITE_ANALYSIS_LOCAL));
            
            manageGermplasmListsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));
            
            breedingViewMultiSiteAnalysisButton.addListener(new ChangeWindowAction(WindowEnums.BREEDING_GXE,this.project,this.role,null));
            
            makeCrossesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.CROSSING_MANAGER));
            recomMakeCrossesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.CROSSING_MANAGER));
             
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
            messageSource.setValue(dashboardTitle, Message.WORKFLOW_PREVIEW_TITLE, "MARS");
        } else {
            messageSource.setValue(dashboardTitle, Message.PROJECT_TITLE, project.getProjectName());
        }
        
        // titles
        messageSource.setValue(projectPlanningTitle, Message.PROJECT_PLANNING);
        messageSource.setValue(populationDevelopmentTitle, Message.POPULATION_DEVELOPMENT);
        messageSource.setValue(fieldTrialManagementTitle, Message.FIELD_TRIAL_MANAGEMENT);
        messageSource.setValue(genotypingTitle, Message.GENOTYPING);

        messageSource.setValue(phenotypicAnalysisTitle, Message.PHENOTYPIC_ANALYSIS);
        messageSource.setValue(qtlAnalysisTitle, Message.QTL_ANALYSIS);

        messageSource.setValue(qtlSelectionTitle, Message.QTL_SELECTION);
        
        messageSource.setValue(recombinationCycleTitle, Message.RECOMBINATION_CYCLE);
        
        messageSource.setValue(finalBreedingDecisionTitle, Message.FINAL_BREEDING_DECISION);
        
        // buttons
        messageSource.setCaption(browseGermplasmButton, Message.BROWSE_GERMPLASM_INFORMATION);
        messageSource.setDescription(browseGermplasmButton, Message.CLICK_TO_LAUNCH_GERMPLASM_BROWSER);
        
        messageSource.setCaption(browseStudiesButton, Message.BROWSE_STUDIES_AND_DATASETS);
        messageSource.setDescription(browseStudiesButton, Message.CLICK_TO_LAUNCH_STUDY_BROWSER);
        
        messageSource.setCaption(browseGermplasmListsButton, Message.BROWSE_GERMPLAM_LISTS);
        messageSource.setDescription(browseGermplasmListsButton, Message.CLICK_TO_LAUNCH_GERMPLASM_LIST_BROWSER);

        //messageSource.setCaption(browseGenotypingDataButton, Message.BROWSE_GENOTYPING_DATA);
        //messageSource.setDescription(browseGenotypingDataButton, Message.CLICK_TO_LAUNCH_GENOTYPING_DATA);
	
        messageSource.setCaption(breedingManagerButton, Message.BREEDING_MANAGER);
        messageSource.setDescription(breedingManagerButton, Message.CLICK_TO_LAUNCH_BREEDING_MANAGER);
        
        messageSource.setCaption(recomBreedingManagerButton, Message.BREEDING_MANAGER);
        messageSource.setDescription(recomBreedingManagerButton, Message.CLICK_TO_LAUNCH_BREEDING_MANAGER);
        
        messageSource.setCaption(fieldBookButton, Message.FIELDBOOK_CREATE);
        messageSource.setDescription(fieldBookButton, Message.CLICK_TO_LAUNCH_FIELDBOOK);
        
        messageSource.setCaption(gdmsButton, Message.MANAGE_GENOTYPING_DATA);
        messageSource.setDescription(gdmsButton, Message.CLICK_TO_LAUNCH_GDMS);

        messageSource.setCaption(phenotypicBreedingViewButton, Message.BREEDING_VIEW);
        messageSource.setDescription(phenotypicBreedingViewButton, Message.CLICK_TO_LAUNCH_BREEDING_VIEW);
        
        messageSource.setCaption(qtlBreedingViewButton, Message.BREEDING_VIEW);
        messageSource.setDescription(qtlBreedingViewButton, Message.CLICK_TO_LAUNCH_BREEDING_VIEW);
        
        messageSource.setCaption(optimasButton, Message.OPTIMAS_MARS);
        messageSource.setDescription(optimasButton, Message.CLICK_TO_LAUNCH_OPTIMAS);
        
        messageSource.setCaption(breedingViewMultiSiteAnalysisButton, Message.MULTI_SITE_ANALYSIS_LINK);
        
        messageSource.setCaption(manageGermplasmListsButton, Message.LIST_MANAGER);
        messageSource.setDescription(manageGermplasmListsButton, Message.CLICK_TO_LAUNCH_LIST_MANAGER);
        
        messageSource.setCaption(makeCrossesButton, Message.MAKE_CROSSES);
        messageSource.setDescription(makeCrossesButton, Message.CLICK_TO_LAUNCH_CROSSING_MANAGER);
        
        messageSource.setCaption(recomMakeCrossesButton, Message.MAKE_CROSSES);
        messageSource.setDescription(recomMakeCrossesButton, Message.CLICK_TO_LAUNCH_CROSSING_MANAGER);
        
    }
}
