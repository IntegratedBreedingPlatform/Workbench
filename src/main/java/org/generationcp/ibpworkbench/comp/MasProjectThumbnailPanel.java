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

public class MasProjectThumbnailPanel extends ProjectThumbnailPanel implements IProjectProvider{

    private static final long serialVersionUID = 1L;

    // Breeding Management
    private VerticalLayout projectPlanningPanel;
    private VerticalLayout populationDevelopmentPanel;
    private VerticalLayout fieldTrialPanel;

    // Marker Implementation
    private VerticalLayout markerTraitSelectionPanel;
    private VerticalLayout progenySelectionPanel;
    private VerticalLayout projectCompletionPanel;
    
    public MasProjectThumbnailPanel(Project project) {
        this(project, false);
    }
    
    public MasProjectThumbnailPanel(Project project, boolean isLastOpenedProject) {
        super(project, isLastOpenedProject);
    }

    protected void initializeComponents() {
        super.initializeComponents();

        // Breeding Management
        projectPlanningPanel = createWorkflowStep("1. Project", "Planning");  
        populationDevelopmentPanel = createWorkflowStep("2. Population", "Development");  
        fieldTrialPanel = createWorkflowStep("3. Field Trial", "Management");  

        // Marker Implementation
        markerTraitSelectionPanel = createWorkflowStep("4. Marker Trait", "Selection");
        progenySelectionPanel = createWorkflowStep("5. Progeny", "Selection");
        projectCompletionPanel = createWorkflowStep("6. Project", "Completion");
    }

    protected void initializeLayout() {
        setWidth("350px");
        super.initializeLayout();
    }

    protected Component layoutSummaryPanel() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);

        Component breedingManagementArea = layoutBreedingManagementArea();
        layout.addComponent(breedingManagementArea);
        layout.setExpandRatio(breedingManagementArea, 50);

        Component markerImplementationArea = layoutMarkerImplementation();
        layout.addComponent(markerImplementationArea);
        layout.setExpandRatio(markerImplementationArea, 50);

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

        return layout;
    }

    protected Component layoutMarkerImplementation() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);

        configureWorkflowStepLayout(markerTraitSelectionPanel);
        layout.addComponent(markerTraitSelectionPanel);

        configureWorkflowStepLayout(progenySelectionPanel);
        layout.addComponent(progenySelectionPanel);

        configureWorkflowStepLayout(projectCompletionPanel);
        layout.addComponent(projectCompletionPanel);

        return layout;
    }


}
