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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

@Configurable
public class MasProjectDashboard extends VerticalLayout implements InitializingBean, InternationalizableComponent {

    private static final long serialVersionUID = 1L;

    private Project project;

    private Label dashboardTitle;

    // Breeding Management controls
    private Label breedingManagementTitle;

    private Label projectPlanningTitle;
    private Label populationDevelopmentTitle;
    private Label fieldTrialManagementTitle;
    
    // Marker Implementation controls
    private Label markerImplementationTitle;
    private Label markerTraitSelectionTitle;
    private Label progenySelectionTitle;
    private Label projectCompletionTitle;

    private Button browseGermplasmButton;
    private Button browseStudiesButton;
    private Button gdmsButton;
    private Button breedingManagerButton;
    private Button breedingManager2Button;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public MasProjectDashboard(Project project) {
        this.project = project;
    }
    
    @Override
    public void afterPropertiesSet() {
        assemble();
    }

    protected void initializeComponents() {
        // dashboard title
        dashboardTitle = new Label();
        dashboardTitle.setStyleName("gcp-content-title");

        // Breeding Management
        breedingManagementTitle = new Label("Breeding Management");
        breedingManagementTitle.setStyleName("gcp-section-title");
        breedingManagementTitle.setSizeUndefined();

        projectPlanningTitle = new Label("1. Project Planning");
        projectPlanningTitle.setStyleName("gcp-section-title");
        projectPlanningTitle.setSizeUndefined();

        populationDevelopmentTitle = new Label("2. Population Development");
        populationDevelopmentTitle.setStyleName("gcp-section-title");
        populationDevelopmentTitle.setSizeUndefined();

        fieldTrialManagementTitle = new Label("3. Field Trial Management");
        fieldTrialManagementTitle.setStyleName("gcp-section-title");
        fieldTrialManagementTitle.setSizeUndefined();

        browseGermplasmButton = new Button("Browse Germplasm Information");
        browseGermplasmButton.setStyleName(BaseTheme.BUTTON_LINK);
        browseGermplasmButton.setSizeUndefined();
        browseGermplasmButton.setDescription("Click to launch Germplasm Browser");

        browseStudiesButton = new Button("Browse Studies and Datasets");
        browseStudiesButton.setStyleName(BaseTheme.BUTTON_LINK);
        browseStudiesButton.setSizeUndefined();
        browseStudiesButton.setDescription("Click to launch Study Browser");

        breedingManagerButton = new Button("Breeding Manager");
        breedingManagerButton.setStyleName(BaseTheme.BUTTON_LINK);
        breedingManagerButton.setSizeUndefined();
        breedingManagerButton.setDescription("Click to launch Breeding Manager");

        breedingManager2Button = new Button("Breeding Manager");
        breedingManager2Button.setStyleName(BaseTheme.BUTTON_LINK);
        breedingManager2Button.setSizeUndefined();
        breedingManager2Button.setDescription("Click to launch Breeding Manager");

        gdmsButton = new Button("GDMS");
        gdmsButton.setStyleName(BaseTheme.BUTTON_LINK);
        gdmsButton.setSizeUndefined();
        gdmsButton.setDescription("Click to launch GDMS");

        // Marker Implementation
        markerImplementationTitle = new Label("Marker Implementation");
        markerImplementationTitle.setStyleName("gcp-section-title");
        markerImplementationTitle.setSizeUndefined();

        markerTraitSelectionTitle = new Label("4. Marker Trait Selection");
        markerTraitSelectionTitle.setStyleName("gcp-section-title");
        markerTraitSelectionTitle.setSizeUndefined();

        progenySelectionTitle = new Label("5. Progeny Selection");
        progenySelectionTitle.setStyleName("gcp-section-title");
        progenySelectionTitle.setSizeUndefined();

        projectCompletionTitle = new Label("6. Project Completion");
        projectCompletionTitle.setStyleName("gcp-section-title");
        projectCompletionTitle.setSizeUndefined();


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

        HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        Component breedingManagementArea = layoutBreedingManagementArea();
        breedingManagementArea.setHeight("100%");
        layout.addComponent(breedingManagementArea);

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

        layout.addComponent(breedingManagementTitle);
        layout.setComponentAlignment(breedingManagementTitle, Alignment.TOP_CENTER);

        Component projectPlanningArea = layoutProjectPlanning();
        layout.addComponent(projectPlanningArea);

        Component populationManagementArea = layoutPopulationDevelopment();
        layout.addComponent(populationManagementArea);

        Component fieldTrialArea = layoutFieldTrialManagement();
        layout.addComponent(fieldTrialArea);

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

        layout.addComponent(browseStudiesButton);
        layout.setComponentAlignment(browseStudiesButton, Alignment.TOP_CENTER);
        layout.setExpandRatio(browseStudiesButton, 0);

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

        return layout;
    }

    protected Component layoutMarkerImplementation() {
        Panel panel = new Panel();

        VerticalLayout layout = new VerticalLayout();
        layout.setHeight("520px");
        layout.setMargin(true);
        layout.setSpacing(true);

        layout.addComponent(markerImplementationTitle);
        layout.setComponentAlignment(markerImplementationTitle, Alignment.TOP_CENTER);

        Component markerTraitSelectionArea = layoutMarkerTraitSelection();
        layout.addComponent(markerTraitSelectionArea);

        Component progenySelectionArea = layoutProgenySelection();
        layout.addComponent(progenySelectionArea);

        Component projectCompletionArea = layoutProjectCompletion();
        layout.addComponent(projectCompletionArea);

        panel.setContent(layout);
        return panel;
    }

    protected Component layoutMarkerTraitSelection() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(markerTraitSelectionTitle);
        layout.setComponentAlignment(markerTraitSelectionTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(markerTraitSelectionTitle, 0);

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

    protected Component layoutProgenySelection() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(progenySelectionTitle);
        layout.setComponentAlignment(progenySelectionTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(progenySelectionTitle, 0);

        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        layout.addComponent(emptyLabel);
        layout.setExpandRatio(emptyLabel, 100);

        layout.addComponent(breedingManager2Button);
        layout.setComponentAlignment(breedingManager2Button, Alignment.TOP_CENTER);
        layout.setExpandRatio(breedingManager2Button, 0);

        return layout;
    }

    protected Component layoutProjectCompletion() {
        VerticalLayout layout = new VerticalLayout();
        configureWorkflowStepLayout(layout);

        layout.addComponent(projectCompletionTitle);
        layout.setComponentAlignment(projectCompletionTitle, Alignment.TOP_CENTER);
        layout.setExpandRatio(projectCompletionTitle, 0);

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
        browseGermplasmButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_BROWSER));
        browseStudiesButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GERMPLASM_PHENOTYPIC));
        gdmsButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.GDMS));
        breedingManagerButton.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));
        breedingManager2Button.addListener(new LaunchWorkbenchToolAction(ToolEnum.BREEDING_MANAGER));
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
        messageSource.setValue(dashboardTitle, Message.project_title, project.getProjectName());
    }
}
