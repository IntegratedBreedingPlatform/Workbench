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

package org.generationcp.ibpworkbench.comp.project.create;

import java.util.List;

import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * The create project panel 
 * 
 * @author Joyce Avestro
 *
 */
@Configurable
public class CreateProjectPanel extends VerticalLayout implements InitializingBean{

    private static final long serialVersionUID = 1L;
    
    private CreateProjectAccordion createProjectAccordion;

    private Label newProjectTitle;
    private Button cancelButton;
    private Button saveProjectButton;
    private Component buttonArea;
    
    private Project project;                // the project created
    private List<Location> newLocations;    // locations added in Locations tab (ProjectLocationsComponent)
    private List<Method> newMethods;        // methods added in Breeding Methods tab (ProjectBreedingMethodsComponent)
    private List<User> newUsers;            // users added in Project Members tab (ProjectMembersComponent)

    public CreateProjectPanel() {
        super();
    }
    
    public CropType getSelectedCropType() {
        return project.getCropType();
    }

    public void setSelectedCropType(CropType cropType) {
        project.setCropType(cropType);
    }

    public Project getProject() {
        if (project == null){
            project = new Project();
        }
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }

    public List<Location> getNewLocations() {
        return newLocations;
    }
    
    public void setNewLocations(List<Location> newLocations) {
        this.newLocations = newLocations;
    }
    
    public List<Method> getNewMethods() {
        return newMethods;
    }
    
    public void setNewMethods(List<Method> newMethods) {
        this.newMethods = newMethods;
    }

    public List<User> getNewUsers() {
        return newUsers;
    }
    
    public void setNewUsers(List<User> newUsers) {
        this.newUsers = newUsers;
    }

    public List<ProjectUserRole> getProjectUserRoles(){
        return createProjectAccordion.getProjectUserRoles();
    }

    public List<ProjectUserRole> getProjectMembers(){
        return createProjectAccordion.getProjectMembers();
    }

    public CreateProjectAccordion getCreateProjectAccordion() {
        return createProjectAccordion;
    }
    
    @Override
    public void afterPropertiesSet() {
        assemble();
    }

    protected void initializeComponents() {
        newProjectTitle = new Label("Create New Project");
        newProjectTitle.setStyleName("gcp-content-title");
        addComponent(newProjectTitle);
        
        project = new Project();

        createProjectAccordion = new CreateProjectAccordion(this);
        addComponent(createProjectAccordion);

        buttonArea = layoutButtonArea();
        addComponent(buttonArea);
        
    }

    protected void initializeValues() {
    }

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);
        setComponentAlignment(buttonArea, Alignment.TOP_RIGHT);
    }

    protected void initializeActions() {
        saveProjectButton.addListener(new SaveNewProjectAction(this));
        cancelButton.addListener(new HomeAction());
    }

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        cancelButton = new Button("Cancel");
        saveProjectButton = new Button("Save");

        buttonLayout.addComponent(cancelButton);
        buttonLayout.addComponent(saveProjectButton);

        return buttonLayout;
    }

    protected void assemble() {
        initializeComponents();
        initializeValues();
        initializeLayout();
        initializeActions();
    }
    
    public boolean validate(){
        return createProjectAccordion.validate();
    }

}
