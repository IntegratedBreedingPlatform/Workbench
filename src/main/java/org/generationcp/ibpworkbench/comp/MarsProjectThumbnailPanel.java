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

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class MarsProjectThumbnailPanel extends ProjectThumbnailPanel implements IProjectProvider{

    private static final long serialVersionUID = 1L;
 
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
  
    public MarsProjectThumbnailPanel(Project project) {
        this(project, false);
    }
    
    public MarsProjectThumbnailPanel(Project project, boolean isLastOpenedProject) {
        super(project, isLastOpenedProject);
    }

    protected void initializeComponents() {
        super.initializeComponents();

        // Breeding Management
        projectPlanningPanel = createWorkflowStep("1. Project", "Planning"); // Project Planning
        populationDevelopmentPanel = createWorkflowStep("2. Population", "Development"); //  Development
        fieldTrialPanel = createWorkflowStep("3. Field Trial", "Management"); // Field Trial Management
        genotypingPanel = createWorkflowStep("4. Genotyping"); // Genotyping

        // Market Trait Analysis
        markerTraitAnalysisPanel = createWorkflowStep("5. Marker Trait", "Analysis");

        // Marker Implementation
        plantSelectionPanel = createWorkflowStep("6. Plant", "Selection");
        recombinationCycle = createWorkflowStep("7. Recombination", "Cycle");
        projectCompletionPanel = createWorkflowStep("8. Project", "Completion");
    }

    protected void initializeLayout() {
        setWidth("480px");
        super.initializeLayout();
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

}
