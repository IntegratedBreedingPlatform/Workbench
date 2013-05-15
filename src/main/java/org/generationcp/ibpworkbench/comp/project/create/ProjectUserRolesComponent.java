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
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.actions.OpenWorkflowPreviewAction;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.WorkflowTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * The second tab (Breeding Workflows) in Create Project Accordion Component.
 *
 * @author Joyce Avestro
 */
@Configurable
public class ProjectUserRolesComponent extends VerticalLayout implements InitializingBean{

    private static final Logger LOG = LoggerFactory.getLogger(ProjectUserRolesComponent.class);
    private static final long serialVersionUID = 1L;
    
    private CreateProjectPanel createProjectPanel;

    private List<CheckBox> userRoleCheckBoxList;

    private Button previousButton;
    private Button nextButton;
    private Component buttonArea;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;
	private VerticalLayout rolesLayout;

    public ProjectUserRolesComponent(CreateProjectPanel createProjectPanel) {
        this.createProjectPanel = createProjectPanel;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        assemble();
    }

    protected void assemble() {
        initializeComponents();
        initializeValues();
        initializeLayout();
        initializeActions();
    }

    protected void initializeComponents() {
        setSpacing(true);
        setMargin(true);

        userRoleCheckBoxList = createUserRolesCheckBoxList();
        rolesLayout = new VerticalLayout();
        
        Label instruction = new Label();
        instruction.setCaption("Identify the workflow(s) you want to use for this project:");
        rolesLayout.addComponent(instruction);
        rolesLayout.setWidth("350px");
        
        Label emptyLabel = new Label(" ");
        emptyLabel.setWidth("100%");
        emptyLabel.setHeight("20px");
        rolesLayout.addComponent(emptyLabel);
        
        String managerRoleLabel = "";
        try {
            Role managerRole = workbenchDataManager.getRoleByNameAndWorkflowTemplate(Role.MANAGER_ROLE_NAME, workbenchDataManager
                    .getWorkflowTemplateByName(WorkflowTemplate.MANAGER_NAME).get(0));
            managerRoleLabel = managerRole.getLabel();
        } catch (MiddlewareQueryException e) {
            LOG.error("Error in getting role");
        }

        for (CheckBox checkBox : userRoleCheckBoxList) {
            if (checkBox.getCaption().contains(managerRoleLabel)) {
                //add some space before the Manager role option
                Label emptyLabel2 = new Label(" ");
                emptyLabel2.setWidth("100%");
                emptyLabel2.setHeight("10px");
                rolesLayout.addComponent(emptyLabel2);
                
                Label emptyLabel3 = new Label(" ");
                emptyLabel3.setWidth("100%");
                emptyLabel3.setHeight("10px");
                rolesLayout.addComponent(emptyLabel3);
                
                Label emptyLabel4 = new Label(" ");
                emptyLabel4.setWidth("100%");
                emptyLabel4.setHeight("10px");
                rolesLayout.addComponent(emptyLabel4);
            }
            
            HorizontalLayout checkboxButtonLayout = new HorizontalLayout();
            checkboxButtonLayout.setSpacing(true);
            
            Button showButton = new Button("[View]");
            showButton.setStyleName(Reindeer.BUTTON_LINK + " gcp-btn-link-brown");
            showButton.addListener(new OpenWorkflowPreviewAction());
            showButton.setData(checkBox.getData());
            
            checkboxButtonLayout.addComponent(checkBox);
            checkboxButtonLayout.addComponent(showButton);
            rolesLayout.addComponent(checkboxButtonLayout);
        }

        
        
        addComponent(rolesLayout);

        buttonArea = layoutButtonArea();
        addComponent(buttonArea);

    }

    protected void initializeValues() {
    }

    protected void initializeLayout() {
        setSpacing(true);
        setMargin(true);
        
        setComponentAlignment(rolesLayout,Alignment.TOP_CENTER);
        setComponentAlignment(buttonArea, Alignment.TOP_CENTER);
    }

    protected void initializeActions() {
        previousButton.addListener(new PreviousButtonClickListener());
        nextButton.addListener(new NextButtonClickListener());
    }

    protected Component layoutButtonArea() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setMargin(true, false, false, false);

        previousButton = new Button("Previous");
        nextButton = new Button("Next");
        buttonLayout.addComponent(previousButton);
        buttonLayout.addComponent(nextButton);
        return buttonLayout;
    }

    private List<CheckBox> createUserRolesCheckBoxList() {
        List<Role> roles = null;
        List<CheckBox> rolesCheckBoxList = new ArrayList<CheckBox>();
        try {
            roles = workbenchDataManager.getAllRolesOrderedByLabel();
        } catch (MiddlewareQueryException e) {
            LOG.error("Error encountered while getting roles", e);
            throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
        }

        for (Role role : roles) {
            CheckBox cb = new CheckBox(role.getName());
            cb.setData(role.getRoleId());
            if (role.getName().equals(Role.MANAGER_ROLE_NAME)) {
                //set default checked value
                cb.setValue(true);
            }
            cb.setCaption(role.getLabel());
            rolesCheckBoxList.add(cb);

        }

        return rolesCheckBoxList;

    }

    public boolean validate() { 
        // Check if at least one role is selected
        boolean withCheckedItem = false;
        
        for (CheckBox cb : userRoleCheckBoxList) {
            if ((Boolean) cb.getValue() == true){
                withCheckedItem = true;
                break;
            }
        }
        
        if (!withCheckedItem){
            MessageNotifier.showError(getWindow(), "Error", "No breeding workflow(s) selected.");
            return false;
        }
        return true;
    }

    public boolean validateAndSave() {
        if (validate()) {
            return true;
        }
        return false; 

    }

    private class PreviousButtonClickListener implements ClickListener{

        private static final long serialVersionUID = 1L;

        @Override
        public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
            createProjectPanel.getCreateProjectAccordion().setFocusToTab(CreateProjectAccordion.FIRST_TAB_BASIC_DETAILS);
        }
    }

    private class NextButtonClickListener implements ClickListener{

        private static final long serialVersionUID = 1L;

        @Override
        public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
            if (validate()){
                createProjectPanel.getCreateProjectAccordion().setFocusToTab(CreateProjectAccordion.THIRD_TAB_PROJECT_MEMBERS);
            }
        }
    }
    
    public List<ProjectUserRole> getProjectUserRoles() {
        List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
        for (CheckBox cb : userRoleCheckBoxList) {
            if ((Boolean) cb.getValue() == true) {
                Role role;
                try {
                    role = workbenchDataManager.getRoleById((Integer) cb.getData());
                    ProjectUserRole projectUserRole = new ProjectUserRole();
                    projectUserRole.setRole(role);
                    projectUserRoles.add(projectUserRole);
                } catch (MiddlewareQueryException e) {
                  LOG.error("Error encountered while getting project user roles", e);
                  throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
                }
            }
        }
        return projectUserRoles;

    }
    
    public List<Role> getRolesForProjectMembers(){
        List<Role> roles = new ArrayList<Role>();

        for (CheckBox cb : userRoleCheckBoxList) {
            if ((Boolean) cb.getValue() == true) {
                try {
                    Role role = workbenchDataManager.getRoleById((Integer) cb.getData());
                    if (!role.getName().contains(Role.MANAGER_ROLE_NAME)){
                        roles.add(role);
                    }
                } catch (MiddlewareQueryException e) {
                  LOG.error("Error encountered while getting creator user roles", e);
                  throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
                }
            }
        }
        return roles;
}

}
