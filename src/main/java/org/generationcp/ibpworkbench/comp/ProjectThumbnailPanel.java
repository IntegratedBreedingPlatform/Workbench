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
public class ProjectThumbnailPanel extends VerticalLayout implements IProjectProvider {
    private static final long serialVersionUID = 1L;
    
    private Project project;
    
    private Label projectTitle;
    private Label workflowTitle;
    
    // Breeding Management
    private Panel genotypingPanel;
    private Panel fieldTrialPanel;
    private Panel populationManagementPanel;
    private Panel projectPlanningPanel;
    
    // Market Trait Analysis
    private Panel loadDataSetsPanel;
    private Panel phenotypicAnalysisPanel;
    private Panel genotypicAnalysisPanel;
    private Panel qtlAnalysisPanel;
    
    // Marker Implementation
    private Panel projectCompletionPanel;
    private Panel ideotypeDesignPanel;
    
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
        genotypingPanel = createPanel(""); // Genotyping
        fieldTrialPanel = createPanel(""); // Field Trial Management
        populationManagementPanel = createPanel(""); // Population Management
        projectPlanningPanel = createPanel(""); // Project Planning
        
        // Market Trait Analysis
        loadDataSetsPanel = createPanel("");
        phenotypicAnalysisPanel = createPanel("");
        genotypicAnalysisPanel = createPanel("");
        qtlAnalysisPanel = createPanel("");
        
        // Marker Implementation
        projectCompletionPanel = createPanel("");
        ideotypeDesignPanel = createPanel("");
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
        
        layoutPanel(genotypingPanel);
        layout.addComponent(genotypingPanel);
        
        layoutPanel(fieldTrialPanel);
        layout.addComponent(fieldTrialPanel);
        
        layoutPanel(populationManagementPanel);
        layout.addComponent(populationManagementPanel);
        
        layoutPanel(projectPlanningPanel);
        layout.addComponent(projectPlanningPanel);
        
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
        
        layoutPanel(projectCompletionPanel);
        layout.addComponent(projectCompletionPanel);
        
        layoutPanel(ideotypeDesignPanel);
        layout.addComponent(ideotypeDesignPanel);
        
        return layout;
    }
    
    protected Panel createPanel(String caption) {
        Panel panel = new Panel();
        
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        
        Label label = new Label(caption);
        label.setSizeUndefined();
        
        layout.addComponent(label);
        layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        
        panel.setContent(layout);
        return panel;
    }
    
    protected void layoutPanel(Panel panel) {
        panel.setWidth("100px");
        panel.setHeight("50px");
    }
}
