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

package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.actions.HomeAction;
import org.generationcp.ibpworkbench.ui.projectlocations.ProjectLocationsView;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

/**
 * The create project panel 
 * 
 * @author Joyce Avestro
 *
 */
@Configurable
public class CreateProjectPanel extends Panel implements InitializingBean{

    private static final long serialVersionUID = 1L;
    
    protected CreateProjectAccordion createProjectAccordion;


    protected HorizontalLayout newProjectTitleArea;
    protected Button cancelButton;
    protected Button saveProjectButton;
    protected Component buttonArea;

    protected Project project;                // the project created
    protected List<Location> newLocations;    // locations added in Locations tab (ProjectLocationsComponent)
    protected List<Method> newMethods;        // methods added in Breeding Methods tab (ProjectBreedingMethodsComponent)
    protected List<User> newUsers;            // users added in Project Members tab (ProjectMembersComponent)
    protected User currentUser;               // should be the currently logged in user that will try to add / update a new project

	@Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
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
        newProjectTitleArea = new HorizontalLayout();
        newProjectTitleArea.setSpacing(true);
        
        project = new Project();

        createProjectAccordion = new CreateProjectAccordion(this);

        buttonArea = layoutButtonArea();
    }

    protected void initializeValues() {
    }

    protected void initializeLayout() {
        VerticalLayout root = new VerticalLayout();
        root.setMargin(new Layout.MarginInfo(false,true,true,true));
        root.setSpacing(false);
        root.setSizeUndefined();
        root.setWidth("800px");

        root.addComponent(newProjectTitleArea);
        root.addComponent(createProjectAccordion);
        root.addComponent(buttonArea);
        root.setComponentAlignment(buttonArea,Alignment.TOP_RIGHT);

        this.setScrollable(true);
        this.setSizeFull();
        this.setContent(root);
    }

    protected void initializeActions() {
        saveProjectButton.addListener(new SaveNewProjectAction(this));
        //cancelButton.addListener(new HomeAction());
        
        cancelButton.addListener(new Button.ClickListener() {
            private static final long serialVersionUID = 5980254872602301350L;

            @Override
            public void buttonClick(ClickEvent event) {
               onCancel(event);
            }
        });
    }
    
    public void onCancel(Button.ClickEvent event) {
        
        try {
            new HomeAction().buttonClick(event);
        } catch (Exception e) {
            if(e.getCause() instanceof InternationalizableException) {
                InternationalizableException i = (InternationalizableException) e.getCause();
                MessageNotifier.showError(event.getComponent().getWindow(), i.getCaption(), i.getDescription());
            }
            return;
        }

        
    }

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        cancelButton = new Button("Cancel");
        saveProjectButton = new Button("Save");
        saveProjectButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());

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
    
    public void setTitle(String label, String description) {
    	newProjectTitleArea.removeAllComponents();

        Label heading = new Label("Create A Program");
        heading.setStyleName(Bootstrap.Typography.H1.styleName());

    	Label title = new Label(label);
        title.setStyleName(Bootstrap.Typography.H2.styleName());

        newProjectTitleArea.addComponent(heading);
        newProjectTitleArea.addComponent(title);

    	Label descLbl = new Label(description);
    	descLbl.setWidth("300px");
    	
    	PopupView popup = new PopupView("?",descLbl);
    	popup.setStyleName("gcp-popup-view");
    	
    	newProjectTitleArea.addComponent(popup);
        newProjectTitleArea.setComponentAlignment(heading, Alignment.BOTTOM_LEFT);
        newProjectTitleArea.setComponentAlignment(title, Alignment.BOTTOM_LEFT);
        newProjectTitleArea.setComponentAlignment(popup, Alignment.MIDDLE_LEFT);

    }


    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }
}