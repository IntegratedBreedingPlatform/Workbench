package org.generationcp.ibpworkbench.comp;

import org.generationcp.ibpworkbench.model.provider.IProjectProvider;
import org.generationcp.middleware.pojos.workbench.Project;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

// TODO: rename this to MarsProjectThumbnailPanel?
public class ProjectThumbnailPanel extends VerticalLayout implements IProjectProvider {
    private static final long serialVersionUID = 1L;
    
    private Project project;
    
    private Label projectTitle;
    private Label workflowTitle;
    
    // Breeding Management
    private Panel genotypingPanel;
    private Panel fieldTrialPanel;
    private Panel populationDevelopmentPanel;
    private Panel projectPlanningPanel;
    
    // Market Trait Analysis
    private Panel loadDataSetsPanel;
    private Panel phenotypicAnalysisPanel;
    private Panel genotypicAnalysisPanel;
    private Panel qtlAnalysisPanel;
    
    // Marker Implementation
    private Panel projectCompletionPanel;
    private Panel ideotypeDesignPanel;
    private Panel subProcessPanel;
    
    public ProjectThumbnailPanel(Project project) {
        this.project = project;
        
        assemble();
    }
    
    @Override
    public Project getProject() {
        return project;
    }
    
    protected void initializeComponents() {
        projectTitle = new Label(project.getProjectName());
        
        workflowTitle = new Label("MARS");
        
        // Breeding Management
        projectPlanningPanel = createPanel("1. Project", "Planning"); // Project Planning
        populationDevelopmentPanel = createPanel("2. Population",  "Development"); // Population Development
        fieldTrialPanel = createPanel("3. Field Trial",  "Management"); // Field Trial Management
        genotypingPanel = createPanel("4. Genotyping"); // Genotyping
        
        // Market Trait Analysis
        loadDataSetsPanel = createPanel("5. Load",  "Datasets");
        phenotypicAnalysisPanel = createPanel("6. Phenotypic",  "Analysis");
        genotypicAnalysisPanel = createPanel("7. Genotypic",  "Analysis");
        qtlAnalysisPanel = createPanel("8. QTL",  "Analysis");
        
        // Marker Implementation
        ideotypeDesignPanel = createPanel("9. Ideotype",  "Design");
        subProcessPanel = createPanel("10. XYZ",  "Subprocess");
        projectCompletionPanel = createPanel("11. Project",  "Completion");
        
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
        
        Component summaryPanel = layoutSummaryPanel();
        summaryPanel.setSizeFull();
        layout.addComponent(summaryPanel);
        layout.setExpandRatio(summaryPanel, 1.0f);
        
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
        
        layoutPanel(projectPlanningPanel);
        layout.addComponent(projectPlanningPanel);
        
        layoutPanel(populationDevelopmentPanel);
        layout.addComponent(populationDevelopmentPanel);
        
        layoutPanel(fieldTrialPanel);
        layout.addComponent(fieldTrialPanel);
        
        layoutPanel(genotypingPanel);
        layout.addComponent(genotypingPanel);
        
        return layout;
    }
    
    protected Component layoutMarkerTraitAnalysis() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        
        layoutPanel(loadDataSetsPanel);
        layout.addComponent(loadDataSetsPanel);
        
        layoutPanel(phenotypicAnalysisPanel);
        layout.addComponent(phenotypicAnalysisPanel);
        
        layoutPanel(genotypicAnalysisPanel);
        layout.addComponent(genotypicAnalysisPanel);
        
        layoutPanel(qtlAnalysisPanel);
        layout.addComponent(qtlAnalysisPanel);
        
        return layout;
    }
    
    protected Component layoutMarkerImplementation() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);
        
        layoutPanel(ideotypeDesignPanel);
        layout.addComponent(ideotypeDesignPanel);
        
        layoutPanel(subProcessPanel);
        layout.addComponent(subProcessPanel);
        
        layoutPanel(projectCompletionPanel);
        layout.addComponent(projectCompletionPanel);
        
        return layout;
    }
    
    protected Panel createPanel(String... captions) {
        Panel panel = new Panel();
        
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        
        for(String caption : captions)
        {
	        Label label = new Label(caption);
	        label.setSizeUndefined();
	        
	        layout.addComponent(label);
	        layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        }
        
        panel.setContent(layout);
        return panel;
    }
    
    protected void layoutPanel(Panel panel) {
        panel.setWidth("100px");
        panel.setHeight("50px");
    }
}
