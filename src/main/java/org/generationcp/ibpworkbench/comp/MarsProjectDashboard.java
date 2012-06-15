/***************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the 
 * GNU General Public License (http://bit.ly/8Ztv8M) and the 
 * provisions of Part F of the Generation Challenge Programme 
 * Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 **************************************************************/
package org.generationcp.ibpworkbench.comp;

import org.generationcp.ibpworkbench.actions.FieldBookUploadSucceededListener;
import org.generationcp.ibpworkbench.actions.FileUploadFailedListener;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction;
import org.generationcp.ibpworkbench.actions.LaunchWorkbenchToolAction.ToolId;
import org.generationcp.ibpworkbench.actions.OpenProjectWorkflowAction;
import org.generationcp.ibpworkbench.comp.window.FileUploadWindow;
import org.generationcp.ibpworkbench.comp.window.IContentWindow;
import org.generationcp.middleware.pojos.workbench.Project;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class MarsProjectDashboard extends VerticalLayout {
    private static final long serialVersionUID = 1L;
    
    private Project project;

    private Label dashboardTitle;

    // Breeding Management controls
    private Label breedingManagementTitle;
    
    private Label projectPlanningTitle;
    private Label fieldTrialManagementTitle;
    private Label genotypingTitle;
    
    private Label loadDataSetsTitle;
    private Label phenotypicAnalysisTitle;
    private Label genotypicAnalysisTitle;
    private Label qtlAnalysisTitle;
    
    private Label plantSelectionTitle;
    
    //private Link fieldBookLink;
    private Button fieldBookButton;
    private Button uploadFieldBookDataButton; // NOTE: We can remove this later
    
    private Label populationDevelopmentTitle;
    private Button browseGermplasmButton;
    private Button retrieveGermplasmPhenotypicButton;
    private Button gdmsButton;
    
    private Button breedingViewButton;
    
    private Button optimasButton;
    
    // Marker Trait Analysis controls
    private Label markerTraitAnalysisTitle;
    private Label markerTraitAnalysisAreaTitle;
    
    // Marker Implementation controls
    private Label markerImplementationTitle;
    
    private Label recombinationCycleTitle;
    
    private Button breedingManagerButton;
    
    //Temporary Back Button Navigation
    private Button backButton;
    private HorizontalLayout buttonLayout;

    public MarsProjectDashboard(Project project) {
        this.project = project;
        
        assemble();
    }
    
    protected void initializeComponents() {
        // dashboard title
        dashboardTitle = new Label("Project: " + project.getProjectName());
        dashboardTitle.setStyleName("gcp-content-title");
        
        // breeding management
        breedingManagementTitle = new Label("Breeding Management");
        breedingManagementTitle.setStyleName("gcp-section-title");
        
        // project planning
        projectPlanningTitle = new Label("1. Project Planning");
        projectPlanningTitle.setStyleName("gcp-section-title");
        projectPlanningTitle.setSizeUndefined();
        
        populationDevelopmentTitle = new Label("2. Population Development");
        populationDevelopmentTitle.setStyleName("gcp-section-title");
        populationDevelopmentTitle.setSizeUndefined();
        
        fieldTrialManagementTitle = new Label("3. Field Trial Management");
        fieldTrialManagementTitle.setStyleName("gcp-section-title");
        fieldTrialManagementTitle.setSizeUndefined();
        
        genotypingTitle = new Label("4. Genotyping");
        genotypingTitle.setStyleName("gcp-section-title");
        genotypingTitle.setSizeUndefined();
        
        loadDataSetsTitle = new Label("5. Load Datasets");
        loadDataSetsTitle.setStyleName("gcp-section-title");
        loadDataSetsTitle.setSizeUndefined();
        
        phenotypicAnalysisTitle = new Label("6. Phenotypic Analysis");
        phenotypicAnalysisTitle.setStyleName("gcp-section-title");
        phenotypicAnalysisTitle.setSizeUndefined();
        
        genotypicAnalysisTitle = new Label("7. Genotypic Analysis");
        genotypicAnalysisTitle.setStyleName("gcp-section-title");
        genotypicAnalysisTitle.setSizeUndefined();
        
        qtlAnalysisTitle = new Label("8. QTL Analysis");
        qtlAnalysisTitle.setStyleName("gcp-section-title");
        qtlAnalysisTitle.setSizeUndefined();
        
//        fieldBookLink = new Link("Field Book", new ExternalResource("http://localhost:10080/ibfb/master.jnlp"));
//        fieldBookLink.setSizeUndefined();
        
        fieldBookButton = new Button("Field Book");
        fieldBookButton.setStyleName(BaseTheme.BUTTON_LINK);
        fieldBookButton.setSizeUndefined();
        
        uploadFieldBookDataButton = new Button("Upload Field Book Data");
        uploadFieldBookDataButton.setStyleName(BaseTheme.BUTTON_LINK);
        uploadFieldBookDataButton.setSizeUndefined();
        
        browseGermplasmButton = new Button("Browse Germplasm Information");
        browseGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK);
        browseGermplasmButton.setSizeUndefined();
        
        retrieveGermplasmPhenotypicButton = new Button("Browse Studies and Datasets");
        retrieveGermplasmPhenotypicButton.setStyleName(BaseTheme.BUTTON_LINK);
        retrieveGermplasmPhenotypicButton.setSizeUndefined();
        
        gdmsButton = new Button("Manage Genotyping Data");
        gdmsButton.setStyleName(BaseTheme.BUTTON_LINK);
        gdmsButton.setSizeUndefined();
        
        breedingViewButton = new Button("Breeding View");
        breedingViewButton.setStyleName(BaseTheme.BUTTON_LINK);
        breedingViewButton.setSizeUndefined();
        
        optimasButton = new Button("OptiMAS");
        optimasButton.setStyleName(BaseTheme.BUTTON_LINK);
        optimasButton.setSizeUndefined();
        
        // marker trait analysis
        markerTraitAnalysisTitle = new Label("Marker Trait Analysis");
        markerTraitAnalysisTitle.setStyleName("gcp-section-title");
        markerTraitAnalysisTitle.setSizeUndefined();
        
        markerTraitAnalysisAreaTitle = new Label("5. Marker Trait Analysis");
        markerTraitAnalysisAreaTitle.setStyleName("gcp-section-title");
        markerTraitAnalysisAreaTitle.setSizeUndefined();
        
        // marker implementation
        markerImplementationTitle = new Label("Marker Implementation");
        markerImplementationTitle.setStyleName("gcp-section-title");
        
        plantSelectionTitle = new Label("6. Plant Selection");
        plantSelectionTitle.setStyleName("gcp-section-title");
        plantSelectionTitle.setSizeUndefined();
        
        recombinationCycleTitle = new Label("7. Recombination Cycle");
        recombinationCycleTitle.setStyleName("gcp-section-title");
        recombinationCycleTitle.setSizeUndefined();
        
        breedingManagerButton = new Button("Breeding Manager");
        breedingManagerButton.setStyleName(BaseTheme.BUTTON_LINK);
        breedingManagerButton.setSizeUndefined();
        
        backButton = new Button("Back", new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                Component component = event.getComponent();
                IContentWindow window = (IContentWindow) component.getWindow();
                
                ProjectDashboard projectDashboard = new ProjectDashboard(project);
                projectDashboard.addProjectThumbnailPanelListener(new OpenProjectWorkflowAction());
                
                window.showContent(projectDashboard);
            }
        });
        
        buttonLayout = new HorizontalLayout();
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
        
        addButtonLayout();
    }
    
    private void addButtonLayout() {
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true);
        buttonLayout.setWidth("100%");
        
        buttonLayout.addComponent(backButton);
        buttonLayout.setComponentAlignment(backButton, Alignment.MIDDLE_RIGHT);
        
        addComponent(buttonLayout);
    }
    
    protected Component layoutWorkflowArea() {
        Panel panel = new Panel();
        
        HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        
        Component breedingManagementArea = layoutBreedingManagementArea();
        breedingManagementArea.setHeight("100%");
        layout.addComponent(breedingManagementArea);
        
        Component markerTraitAnalysisArea = layoutMarkerTraitAnalysis();
        markerTraitAnalysisArea.setHeight("100%");
        layout.addComponent(markerTraitAnalysisArea);
        
        Component markerImplementationArea = layoutMarkerImplementation();
        markerImplementationArea.setHeight("100%");
        layout.addComponent(markerImplementationArea);
        
        panel.setContent(layout);
        return panel;
    }
    
    protected Component layoutBreedingManagementArea() {
        Panel panel = new Panel();
        
        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("520px");
        layout.setMargin(true);
        layout.setSpacing(true);
        
        breedingManagementTitle.setSizeUndefined();
        layout.addComponent(breedingManagementTitle);
        layout.setComponentAlignment(breedingManagementTitle, Alignment.TOP_CENTER);
        
        Component projectPlanningArea = layoutProjectPlanning();
        layout.addComponent(projectPlanningArea);
        
        Component populationManagementArea = layoutPopulationDevelopment();
        layout.addComponent(populationManagementArea);
        
        Component fieldTrialArea = layoutFieldTrialManagement();
        layout.addComponent(fieldTrialArea);
        
        Component genotypingArea = layoutGenotyping();
        layout.addComponent(genotypingArea);
        
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
        
        layout.addComponent(browseGermplasmButton);
        browseGermplasmButton.setHeight("20px");
        layout.setComponentAlignment(browseGermplasmButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseGermplasmButton, 0);

        layout.addComponent(retrieveGermplasmPhenotypicButton);
        layout.setComponentAlignment(retrieveGermplasmPhenotypicButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(retrieveGermplasmPhenotypicButton, 0);
        
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
    
    protected Component layoutMarkerTraitAnalysis() {
        Panel panel = new Panel();
        
        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("520px");
        layout.setMargin(true);
        layout.setSpacing(true);
        
        layout.addComponent(markerTraitAnalysisTitle);
        layout.setComponentAlignment(markerTraitAnalysisTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(markerTraitAnalysisTitle, 0);
        
        Component markerTraitAnalysisArea = layoutMarkerTraitAnalysisButton();
        layout.addComponent(markerTraitAnalysisArea);
        
        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        panel.setContent(layout);
        return panel;
    }
    
    protected Component layoutMarkerTraitAnalysisButton() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(markerTraitAnalysisAreaTitle);
        layout.setComponentAlignment(markerTraitAnalysisAreaTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(markerTraitAnalysisAreaTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);
        
        layout.addComponent(breedingViewButton);
        layout.setComponentAlignment(breedingViewButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingViewButton, 0);
        
        return layout;
    }
    
    protected Component layoutMarkerImplementation() {
        Panel panel = new Panel();
        
        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("520px");
        layout.setMargin(true);
        layout.setSpacing(true);
        
        markerImplementationTitle.setSizeUndefined();
        layout.addComponent(markerImplementationTitle);
        layout.setComponentAlignment(markerImplementationTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(markerImplementationTitle, 0);
        
//        Component ideotypeDesignArea = createPanel("9. Ideotype Design", "Selection Index");
//        layout.addComponent(ideotypeDesignArea);
        
        Component plantSelectionArea = layoutPlantSelection();
        layout.addComponent(plantSelectionArea);
        layout.setExpandRatio(plantSelectionArea, 0);
        
        Component recombinationCycleArea = layoutRecombinationCycle();
        layout.addComponent(recombinationCycleArea);
        layout.setExpandRatio(recombinationCycleArea, 0);
        
//        Component genotypingArea = createPanel("Genotyping", "Lab Book");
//        layout.addComponent(genotypingArea);
        
        Component projectCompletionArea = createPanel("8. Project Completion");
        layout.addComponent(projectCompletionArea);
        layout.setExpandRatio(recombinationCycleArea, 0);
        
        panel.setContent(layout);
        return panel;
    }
    
    protected Component layoutPlantSelection() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(plantSelectionTitle);
        layout.setComponentAlignment(plantSelectionTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(plantSelectionTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(optimasButton);
        layout.setComponentAlignment(optimasButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(optimasButton, 0);

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
        layout.setWidth("270px");
        layout.setHeight("110px");
        layout.setStyleName("gcp-mars-workflow-step");
        layout.setMargin(true, true, true, true);
    }
    
    protected void initializeActions() {
        uploadFieldBookDataButton.addListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                FileUploadWindow fileUploadWindow = new FileUploadWindow();
                fileUploadWindow.setWidth("380px");
                fileUploadWindow.setHeight("240px");
                fileUploadWindow.setModal(true);
                
                // set allowed mime types
                fileUploadWindow.getUpload().addAllowedMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                fileUploadWindow.getUpload().addAllowedMimeType("application/vnd.ms-excel");
                
                // set the upload listeners
                fileUploadWindow.getUpload().addListener(new FieldBookUploadSucceededListener(fileUploadWindow));
                fileUploadWindow.getUpload().addListener(new FileUploadFailedListener());
                
                getWindow().addWindow(fileUploadWindow);
                fileUploadWindow.center();
            }
        });
        
        browseGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolId.GERMPLASM_BROWSER));
        retrieveGermplasmPhenotypicButton.addListener(new LaunchWorkbenchToolAction(ToolId.GERMPLASM_PHENOTYPIC));
        
        gdmsButton.addListener(new LaunchWorkbenchToolAction(ToolId.GDMS));
        
        fieldBookButton.addListener(new LaunchWorkbenchToolAction(ToolId.FIELDBOOK));
        
        optimasButton.addListener(new LaunchWorkbenchToolAction(ToolId.OPTIMAS));
        
        breedingManagerButton.addListener(new LaunchWorkbenchToolAction(ToolId.BREEDING_MANAGER));
        
        breedingViewButton.addListener(new LaunchWorkbenchToolAction(ToolId.BREEDING_VIEW));
    }
    
    protected void assemble() {
        initializeComponents();
        initializeLayout();
        initializeActions();
    }
}
