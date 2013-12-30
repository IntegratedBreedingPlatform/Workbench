package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.ui.VerticalLayout;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.ProjectUserRole;
import org.generationcp.middleware.pojos.workbench.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cyrus
 * Date: 10/28/13
 * Time: 11:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateProjectAccordion extends CreateProjectAccordion {
    public UpdateProjectAccordion(CreateProjectPanel createProjectPanel) {
        super(createProjectPanel);
    }

    @Override
    public void afterPropertiesSet() {

        previousTabOnFocus = FIRST_TAB_BASIC_DETAILS; // the focus is initially on the first tab

        layoutBasicDetails = new VerticalLayout();
        layoutBasicDetails.setData(FIRST_TAB_BASIC_DETAILS);
        basicDetailsTab = new ProjectBasicDetailsComponent(createProjectPanel);
        basicDetailsTab.disableCropTypeCombo();

        // update basicDetailsTabUI
        basicDetailsTab.updateProjectDetailsFormField(createProjectPanel.getProject());
        basicDetailsTab.removeNextBtn();
        layoutBasicDetails.addComponent(basicDetailsTab);

        layoutBasicDetails.setComponentAlignment(basicDetailsTab,VerticalLayout.ALIGNMENT_HORIZONTAL_CENTER,VerticalLayout.ALIGNMENT_VERTICAL_CENTER);

        layoutBasicDetails.setSpacing(true);
        layoutBasicDetails.setMargin(true);

        //layoutUserRoles = new VerticalLayout();
        //layoutUserRoles.setData(SECOND_TAB_USER_ROLES);

        this.addTab(layoutBasicDetails, messageSource.getMessage(Message.BASIC_DETAILS_LABEL));
        //this.addTab(layoutUserRoles, messageSource.getMessage(Message.BREEDING_WORKFLOWS_LABEL));

        this.addListener(new WorkbenchSelectedTabChangeListener(this));

        //TODO: internationalize this
        createProjectPanel.setTitle(messageSource.getMessage(Message.BASIC_DETAILS_LABEL),"Update the name or date for this program.");
    }

    private boolean rolesInitialized = false;

    @Override
    public void selectedTabChangeAction() throws InternationalizableException {
        super.selectedTabChangeAction();    //To change body of overridden methods use File | Settings | File Templates.

        Integer selectedTab = this.getSelectedProjectTab();

        // remove the next button if on userRolesTab, set initial values for roles
        if (!rolesInitialized && selectedTab.intValue() == SECOND_TAB_USER_ROLES && userRolesTab != null) {
            userRolesTab.removeNextBtn(); // just attempt to remove the next button

            try {
                userRolesTab.setRolesForProjectMembers(createProjectPanel.getProject(),createProjectPanel.getCurrentUser());
            } catch (MiddlewareQueryException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            if (!rolesInitialized) rolesInitialized = true;

        }

        // Override Title Display switching
        switch(selectedTab.intValue()) {
            case FIRST_TAB_BASIC_DETAILS:
                createProjectPanel.setTitle(messageSource.getMessage(Message.BASIC_DETAILS_LABEL),"Update the name or date for this program.");
                break;
            //case SECOND_TAB_USER_ROLES:
            //    createProjectPanel.setTitle(messageSource.getMessage(Message.BREEDING_WORKFLOWS_LABEL),"Mark the breeding workflow checkboxes that this program will be using.");
            //    break;
        }
    }

    @Override
    public boolean validate() {
        if (basicDetailsTab.validateAndSave()) {
            //if (userRolesTab != null) {
            //    return userRolesTab.validateAndSave();
            //}
            return true;
        }

        return false;
    }
    /*
    @Override
    public List<ProjectUserRole> getProjectUserRoles() {
        if (userRolesTab != null) {
            return userRolesTab.getProjectUserRoles();
        }

        List<ProjectUserRole> projectUserRoles = new ArrayList<ProjectUserRole>();
        try {

            List<Role> currentUserRoles =  workbenchDataManager.getRolesByProjectAndUser(createProjectPanel.getProject(),createProjectPanel.getCurrentUser());

            for (Role role : currentUserRoles) {
                ProjectUserRole projectUserRole = new ProjectUserRole();
                projectUserRole.setRole(role);
                projectUserRole.setUserId(createProjectPanel.getCurrentUser().getUserid());
                projectUserRole.setProject(createProjectPanel.getProject());

                projectUserRoles.add(projectUserRole);
            }

        } catch (MiddlewareQueryException e) {
            throw new InternationalizableException(e, Message.DATABASE_ERROR, Message.CONTACT_ADMIN_ERROR_DESC);
        }

        return projectUserRoles;
    }
    */

}
