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
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;

/**
 * The second tab (My Roles) in Create Project Accordion Component.
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
        VerticalLayout rolesLayout = new VerticalLayout();

        for (CheckBox checkBox : userRoleCheckBoxList) {
            rolesLayout.addComponent(checkBox);
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
        setComponentAlignment(buttonArea, Alignment.TOP_RIGHT);
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
            roles = workbenchDataManager.getAllRoles();
        } catch (MiddlewareQueryException e) {
            LOG.error("Error encountered while getting roles", e);
            throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
        }

        for (Role role : roles) {
            CheckBox cb = new CheckBox(role.getName());
            cb.setData(role.getRoleId());
            if (cb.getCaption().contains("Manager")) {
                cb.setValue(true);
            }
            rolesCheckBoxList.add(cb);

        }

        return rolesCheckBoxList;

    }

    private boolean validate() { 
        // Check if at least one role is selected
        boolean withCheckedItem = false;
        
        for (CheckBox cb : userRoleCheckBoxList) {
            if ((Boolean) cb.getValue() == true){
                withCheckedItem = true;
                break;
            }
        }
        
        if (!withCheckedItem){
            MessageNotifier.showError(getWindow(), "Error", "No role selected.");
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
                  LOG.error("Error encountered while saving project user roles", e);
                  throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
                }
            }
        }
        return projectUserRoles;

    }

}
