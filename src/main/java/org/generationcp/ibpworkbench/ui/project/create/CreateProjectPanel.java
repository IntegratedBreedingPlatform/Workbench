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
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    protected static final Logger LOG = LoggerFactory.getLogger(CreateProjectPanel.class);

    protected ProjectBasicDetailsComponent projectBasicDetailsComponent;

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
	
	private Label heading;

    AddProgramPresenter presenter;

    public CreateProjectPanel() {}

    public CreateProjectPanel(AddProgramPresenter presenter) {
        this.presenter = presenter;
	}


    /*
    This should be moved to the presenter code

    public List<ProjectUserRole> getProjectMembers(){
    	ProjectMembersComponent projectMembersComponent = (ProjectMembersComponent) tabSheet.getTab(1).getComponent();
        return projectMembersComponent.getProjectMembers();
    }
    
    public List<ProjectUserRole> getProjectUserRoles(){
    	ProjectMembersComponent projectMembersComponent = (ProjectMembersComponent) tabSheet.getTab(1).getComponent();
        return projectMembersComponent.getProjectUserRoles();
    }
    */
    

 
    @Override
    public void afterPropertiesSet() {
        assemble();
    }

    protected void initializeComponents() {
    	
    	heading = new Label("<span class=\"fa fa-file-text-o\" style=\"color: #009DDA; font-size: 23px \" ></span>&nbsp;Basic Details",Label.CONTENT_XHTML);
        heading.setStyleName(Bootstrap.Typography.H4.styleName()); 
    	
        newProjectTitleArea = new HorizontalLayout();
        newProjectTitleArea.setSpacing(true);
        
        project = new Project();
        
        projectBasicDetailsComponent = new ProjectBasicDetailsComponent(this);

        buttonArea = layoutButtonArea();
    }

    protected void initializeValues() {
    }

    protected void initializeLayout() {
        VerticalLayout root = new VerticalLayout();
        root.setMargin(new Layout.MarginInfo(true,true,true,true));
        root.setSpacing(false);
        root.setSizeUndefined();
        root.setWidth("900px");
        
        root.addComponent(heading);
        root.addComponent(projectBasicDetailsComponent);
        root.addComponent(buttonArea);
        root.setComponentAlignment(buttonArea,Alignment.TOP_CENTER);

        this.setScrollable(true);
        this.setSizeFull();
        this.setContent(root);
        this.setStyleName(Reindeer.PANEL_LIGHT);
    }

    protected void initializeActions() {

        //saveProjectButton.addListener(new SaveNewProjectAction(this));
        saveProjectButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                // if basic details is valid
                if (presenter.validateAndSave()) {
                    presenter.enableProgramMethodsAndLocationsTab();
                }
            }
        });

        cancelButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                presenter.resetBasicDetails();
                presenter.disableProgramMethodsAndLocationsTab();
            }
        });
    }
        

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        cancelButton = new Button("Reset");
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

    /*
    Move to presenter
    public boolean validate(){
    	
    	ProjectMembersComponent projectMembersComponent = (ProjectMembersComponent) tabSheet.getTab(1).getComponent();
    	
    	 boolean success = true;

         success = projectBasicDetailsComponent.validateAndSave();

         if (success) {
             if (projectMembersComponent != null) {
                 success = projectMembersComponent.validateAndSave();
             }
         }

         return success;
    	
    }
    */
    
    public void setTitle(String label, String description) {
    	newProjectTitleArea.removeAllComponents();

    	Label title = new Label(label);
        title.setStyleName(Bootstrap.Typography.H2.styleName());

        newProjectTitleArea.addComponent(title);

    	Label descLbl = new Label(description);
    	descLbl.setWidth("300px");
    	
    	PopupView popup = new PopupView("?",descLbl);
    	popup.setStyleName("gcp-popup-view");
    	
    	
    	newProjectTitleArea.addComponent(popup);
        newProjectTitleArea.setComponentAlignment(title, Alignment.BOTTOM_LEFT);
        newProjectTitleArea.setComponentAlignment(popup, Alignment.MIDDLE_LEFT);

    }

    public void cropTypeChanged(CropType newCropType) {
        presenter.disableProgramMethodsAndLocationsTab();
    }
}