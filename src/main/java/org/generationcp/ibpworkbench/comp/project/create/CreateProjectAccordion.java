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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * The accordion component for Create Project.
 * 
 * @author Joyce Avestro
 *
 */
@Configurable
public class CreateProjectAccordion extends Accordion implements InitializingBean{

    private static final long serialVersionUID = 1L;

    public static final int FIRST_TAB_BASIC_DETAILS = 1;
    public static final int SECOND_TAB_USER_ROLES = 2;
    public static final int THIRD_TAB_PROJECT_MEMBERS = 3;
    public static final int FOURTH_TAB_BREEDING_METHODS = 4;
    public static final int FIFTH_TAB_LOCATIONS = 5;

    // Tabs are disabled by default, except for basic details. Once required information are supplied, they will be enabled
    private boolean userRolesEnabled;
    private boolean projectMembersEnabled;
    private boolean breedingMethodsEnabled;
    private boolean locationsEnabled;

    private CreateProjectPanel createProjectPanel; // the containing panel
    
    
    private ProjectBasicDetailsComponent basicDetailsTab;
    private ProjectUserRolesComponent userRolesTab;
    private ProjectMembersComponent membersTab;
    private ProjectBreedingMethodsComponent breedingMethodsTab;
    private ProjectLocationsComponent locationsTab;

    private VerticalLayout layoutBasicDetails;
    private VerticalLayout layoutUserRoles;
    private VerticalLayout layoutProjectMembers;
    private VerticalLayout layoutBreedingMethods;
    private VerticalLayout layoutLocations;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public CreateProjectAccordion(CreateProjectPanel createProjectPanel) {
        this.createProjectPanel = createProjectPanel;
        initialize();
    }

    private void initialize() {
        userRolesEnabled = false;
        projectMembersEnabled = false;
        breedingMethodsEnabled = false;
        locationsEnabled = false;
    }

    public void selectedTabChangeAction() throws InternationalizableException {
        Component selected = this.getSelectedTab();
        Tab tab = this.getTab(selected);

        if (tab.getComponent() instanceof VerticalLayout) {

            if (((VerticalLayout) tab.getComponent()).getData().equals(SECOND_TAB_USER_ROLES)) {
                if (userRolesEnabled) {
                    if (layoutUserRoles.getComponentCount() == 0) {
                        userRolesTab = new ProjectUserRolesComponent(createProjectPanel);
                        layoutUserRoles.addComponent(userRolesTab);
                        layoutUserRoles.setSpacing(true);
                        layoutUserRoles.setMargin(true);
                    }
                } else {
                    MessageNotifier.showError(getWindow(), "Error",
                            "Please supply the necessary details in the previous tab before continuing.");
                }
            } else if (((VerticalLayout) tab.getComponent()).getData().equals(THIRD_TAB_PROJECT_MEMBERS)) {
                if (projectMembersEnabled) {
                    if (layoutProjectMembers.getComponentCount() == 0) {
                        membersTab = new ProjectMembersComponent(createProjectPanel);
                        layoutProjectMembers.addComponent(membersTab);
                        layoutProjectMembers.setSpacing(true);
                        layoutProjectMembers.setMargin(true);
                    }
                } else {
                    MessageNotifier.showError(getWindow(), "Error",
                            "Please supply the necessary details in the previous tab before continuing.");
                }
            } else if (((VerticalLayout) tab.getComponent()).getData().equals(FOURTH_TAB_BREEDING_METHODS)) {
                if (breedingMethodsEnabled) {
                    if (layoutBreedingMethods.getComponentCount() == 0) {
                        breedingMethodsTab = new ProjectBreedingMethodsComponent(createProjectPanel);
                        layoutBreedingMethods.addComponent(breedingMethodsTab);
                        layoutBreedingMethods.setSpacing(true);
                        layoutBreedingMethods.setMargin(true);
                    }
                } else {
                    MessageNotifier.showError(getWindow(), "Error",
                            "Please supply the necessary details in the previous tab before continuing.");
                }
            } else if (((VerticalLayout) tab.getComponent()).getData().equals(FIFTH_TAB_LOCATIONS)) {
                if (locationsEnabled) {
                    if (layoutLocations.getComponentCount() == 0) {
                        locationsTab = new ProjectLocationsComponent(createProjectPanel);
                        layoutLocations.addComponent(locationsTab);
                        layoutLocations.setSpacing(true);
                        layoutLocations.setMargin(true);
                    } 
                } else {
                    MessageNotifier.showError(getWindow(), "Error",
                            "Please supply the necessary details in the previous tab before continuing.");
                }
            }
        }
    }
    
    /**
     * Used by the navigation buttons to enable and set the selected tabs
     * @param tab
     */
    public void setFocusToTab(int tab){
        switch (tab) {
            case FIRST_TAB_BASIC_DETAILS:
                this.setSelectedTab(layoutBasicDetails);   
                break;
            case SECOND_TAB_USER_ROLES:
                userRolesEnabled = true;
                this.setSelectedTab(layoutUserRoles);                
                break;
            case THIRD_TAB_PROJECT_MEMBERS:
                projectMembersEnabled = true;
                this.setSelectedTab(layoutProjectMembers);                
                break;
            case FOURTH_TAB_BREEDING_METHODS:
                breedingMethodsEnabled = true;
                this.setSelectedTab(layoutBreedingMethods);                
                break;
            case FIFTH_TAB_LOCATIONS:
                locationsEnabled = true;
                this.setSelectedTab(layoutLocations);                
                break;
        }
    }


    /**
     * 
     * Validates the values in the fields in each component. 
     * Assumption: Roles (except for Manager which is selected by default), Members, Methods and Location are not required
     * 
     * @return true if everything is valid, false otherwise
     * 
     */
    public boolean validate(){
        boolean success = true;
        
        success = basicDetailsTab.validateAndSave();
        
        if (success){ 
            if (userRolesTab != null){
                success = userRolesTab.validateAndSave();
            }
        }
        
        if (success){
            if (membersTab != null){
                success = membersTab.validateAndSave();
            }
        }
        
        if (success){
            if (breedingMethodsTab != null){
                success = breedingMethodsTab.validateAndSave();
            }
        }
        
        if (success){
            if (locationsTab != null){
                success = locationsTab.validateAndSave();
            }
        }
        
        return success;        
    }

    @Override
    public void afterPropertiesSet() {
        
        layoutBasicDetails = new VerticalLayout();
        layoutBasicDetails.setData(FIRST_TAB_BASIC_DETAILS);
        basicDetailsTab = new ProjectBasicDetailsComponent(createProjectPanel);
        layoutBasicDetails.addComponent(basicDetailsTab);
        layoutBasicDetails.setSpacing(true);
        layoutBasicDetails.setMargin(true);

        layoutUserRoles = new VerticalLayout();
        layoutUserRoles.setData(SECOND_TAB_USER_ROLES);

        layoutProjectMembers = new VerticalLayout();
        layoutProjectMembers.setData(THIRD_TAB_PROJECT_MEMBERS);

        layoutBreedingMethods = new VerticalLayout();
        layoutBreedingMethods.setData(FOURTH_TAB_BREEDING_METHODS);

        layoutLocations = new VerticalLayout();
        layoutLocations.setData(FIFTH_TAB_LOCATIONS);

        this.addTab(layoutBasicDetails, messageSource.getMessage(Message.BASIC_DETAILS_LABEL));
        this.addTab(layoutUserRoles, messageSource.getMessage(Message.USER_ROLES_LABEL));
        this.addTab(layoutProjectMembers, messageSource.getMessage(Message.PROJECT_MEMBERS_LABEL));
        this.addTab(layoutBreedingMethods, messageSource.getMessage(Message.BREEDING_METHODS_LABEL));
        this.addTab(layoutLocations, messageSource.getMessage(Message.LOCATIONS_LABEL));

        this.addListener(new WorkbenchSelectedTabChangeListener(this));
    }

    public List<ProjectUserRole> getProjectUserRoles(){
        if (userRolesTab != null) {
            return userRolesTab.getProjectUserRoles();
        }
        
        List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
        try {
            Role managerRole = workbenchDataManager.getRoleById(Role.MANAGER_ROLE_ID);
            
            ProjectUserRole projectUserRole = new ProjectUserRole();
            projectUserRole.setRole(managerRole);
            projectUserRoles.add(projectUserRole);
        }
        catch (MiddlewareQueryException e) {
            throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
        }
        
        return projectUserRoles;
    }

    public List<ProjectUserRole> getProjectMembers(){
        return membersTab == null ? new ArrayList<ProjectUserRole>() : membersTab.getProjectMembers();
    }

}
