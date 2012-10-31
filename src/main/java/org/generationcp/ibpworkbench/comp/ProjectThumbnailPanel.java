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
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * This class will be deleted soon.
 * 
 * @author Glenn
 */
@Deprecated
public abstract class ProjectThumbnailPanel extends VerticalLayout implements IProjectProvider{

    private static final long serialVersionUID = 1L;

    protected Project project;

    protected Label projectTitle;
    protected Label workflowTitle;

    protected boolean isLastOpenedProject;
    protected String workflowTitleString;

    public ProjectThumbnailPanel(Project project) {
        this(project, false);        
    }
    
    public ProjectThumbnailPanel(Project project, boolean isLastOpenedProject) {
        this.project = project;
        this.setLastOpenedProject(isLastOpenedProject);
        this.workflowTitleString = project.getTemplate().getName();
        assemble();
    }

    @Override
    public Project getProject() {
        return project;
    }

    public boolean isLastOpenedProject() {
        return isLastOpenedProject;
    }

    public void setLastOpenedProject(boolean isLastOpenedProject) {
        this.isLastOpenedProject = isLastOpenedProject;
    }
    
    protected void initializeComponents() {
        addStyleName("gcp-hand-cursor");
        
        projectTitle = new Label(project.getProjectName());
        projectTitle.setDescription("Click here");

        workflowTitle = new Label(workflowTitleString);
        workflowTitle.setDescription("Click here");
    }

    protected void initializeLayout() {
        setSizeUndefined();
        
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
        
        if(isLastOpenedProject()) {
            layout.setStyleName("gcp-highlight");
        }

        layout.addComponent(workflowTitle);
        layout.setExpandRatio(workflowTitle, 0);
        
        Component summaryPanel = layoutSummaryPanel();
        summaryPanel.setSizeFull();
        layout.addComponent(summaryPanel);
        layout.setExpandRatio(summaryPanel, 100);

        panel.setContent(layout);
        return panel;
    }
    
    /** 
     * Implement to provide layout of the summary panel based on a specific workflow template.
     */
    protected abstract Component layoutSummaryPanel();
    

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
