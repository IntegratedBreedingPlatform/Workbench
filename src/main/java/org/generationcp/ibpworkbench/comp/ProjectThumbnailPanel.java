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

import org.generationcp.ibpworkbench.model.provider.IProjectProvider;
import org.generationcp.middleware.pojos.workbench.Project;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

// TODO: rename this to MarsProjectThumbnailPanel?
public class ProjectThumbnailPanel extends VerticalLayout implements IProjectProvider{

    private static final long serialVersionUID = 1L;

    private Project project;

    private Label projectTitle;
    private Label workflowTitle;

    // Breeding Management
    private VerticalLayout genotypingPanel;
    private VerticalLayout fieldTrialPanel;
    private VerticalLayout populationDevelopmentPanel;
    private VerticalLayout projectPlanningPanel;

    // Market Trait Analysis
    private VerticalLayout markerTraitAnalysisPanel;

    // Marker Implementation
    private VerticalLayout plantSelectionPanel;
    private VerticalLayout recombinationCycle;
    private VerticalLayout projectCompletionPanel;

    public ProjectThumbnailPanel(Project project) {
        this.project = project;

        assemble();
    }

    @Override
    public Project getProject() {
        return project;
    }

    protected void initializeComponents() {
        addStyleName("gcp-hand-cursor");
        
        projectTitle = new Label(project.getProjectName());
        projectTitle.setDescription("Click here");

        workflowTitle = new Label("MARS");
        workflowTitle.setDescription("Click here");

        // Breeding Management
        projectPlanningPanel = createWorkflowStep("1. Project", "Planning"); // Project
                                                                             // Planning
        populationDevelopmentPanel = createWorkflowStep("2. Population", "Development"); // Population
                                                                                         // Development
        fieldTrialPanel = createWorkflowStep("3. Field Trial", "Management"); // Field
                                                                              // Trial
                                                                              // Management
        genotypingPanel = createWorkflowStep("4. Genotyping"); // Genotyping

        // Market Trait Analysis
        markerTraitAnalysisPanel = createWorkflowStep("5. Marker Trait", "Analysis");

        // Marker Implementation
        plantSelectionPanel = createWorkflowStep("6. Plant", "Selection");
        recombinationCycle = createWorkflowStep("7. Recombination", "Cycle");
        projectCompletionPanel = createWorkflowStep("8. Project", "Completion");
    }

    protected void initializeLayout() {
        setSizeUndefined();
        setWidth("480px");

        projectTitle.setWidth("100%");
        projectTitle.setHeight("32px");
        addComponent(projectTitle);

        Component workFlowPanel = layoutWorkflowPanel();
        workFlowPanel.setHeight("320px");
        workFlowPanel.setWidth("100%");
        addComponent(workFlowPanel);
        setExpandRatio(workFlowPanel, 1.0f);
    }

    protected void assemble() {
        initializeComponents();
        initializeLayout();
    }

    protected Component layoutWorkflowPanel() {
        Panel panel = new Panel();

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(true);

        layout.addComponent(workflowTitle);
        layout.setExpandRatio(workflowTitle, 0);

        Component summaryPanel = layoutSummaryPanel();
        summaryPanel.setSizeFull();
        layout.addComponent(summaryPanel);
        layout.setExpandRatio(summaryPanel, 100);

        panel.setContent(layout);
        return panel;
    }

    protected Component layoutSummaryPanel() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);

        Component breedingManagementArea = layoutBreedingManagementArea();
        layout.addComponent(breedingManagementArea);

        Component markerTraitAnalysisArea = layoutMarkerTraitAnalysis();
        layout.addComponent(markerTraitAnalysisArea);

        Component markerImplementationArea = layoutMarkerImplementation();
        layout.addComponent(markerImplementationArea);

        return layout;
    }

    protected Component layoutBreedingManagementArea() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);

        configureWorkflowStepLayout(projectPlanningPanel);
        layout.addComponent(projectPlanningPanel);

        configureWorkflowStepLayout(populationDevelopmentPanel);
        layout.addComponent(populationDevelopmentPanel);

        configureWorkflowStepLayout(fieldTrialPanel);
        layout.addComponent(fieldTrialPanel);

        configureWorkflowStepLayout(genotypingPanel);
        layout.addComponent(genotypingPanel);

        return layout;
    }

    protected Component layoutMarkerTraitAnalysis() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);

        configureWorkflowStepLayout(markerTraitAnalysisPanel);
        layout.addComponent(markerTraitAnalysisPanel);

        return layout;
    }

    protected Component layoutMarkerImplementation() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);

        configureWorkflowStepLayout(plantSelectionPanel);
        layout.addComponent(plantSelectionPanel);

        configureWorkflowStepLayout(recombinationCycle);
        layout.addComponent(recombinationCycle);

        configureWorkflowStepLayout(projectCompletionPanel);
        layout.addComponent(projectCompletionPanel);

        return layout;
    }

    protected VerticalLayout createWorkflowStep(String... captions) {
        VerticalLayout layout = new VerticalLayout();

        for (String caption : captions) {
            Label label = new Label(caption);
            label.setSizeUndefined();
            label.setDescription("Click here");

            layout.addComponent(label);
            layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        }

        return layout;
    }

    protected void configureWorkflowStepLayout(VerticalLayout layout) {
        layout.setWidth("120px");
        layout.setHeight("50px");
        layout.setStyleName("gcp-mars-workflow-step-thumb");
        layout.setMargin(false);
    }
}
