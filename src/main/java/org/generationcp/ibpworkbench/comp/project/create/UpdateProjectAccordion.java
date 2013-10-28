package org.generationcp.ibpworkbench.comp.project.create;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.VerticalLayout;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.ibpworkbench.IBPWorkbenchApplication;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.InitializingBean;

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

        // update basicDetailsTabUI
        basicDetailsTab.updateProjectDetailsFormField(createProjectPanel.getProject());

        layoutBasicDetails.addComponent(basicDetailsTab);

        layoutBasicDetails.setComponentAlignment(basicDetailsTab,VerticalLayout.ALIGNMENT_HORIZONTAL_CENTER,VerticalLayout.ALIGNMENT_VERTICAL_CENTER);

        layoutBasicDetails.setSpacing(true);
        layoutBasicDetails.setMargin(true);

        layoutUserRoles = new VerticalLayout();
        layoutUserRoles.setData(SECOND_TAB_USER_ROLES);

        this.addTab(layoutBasicDetails, messageSource.getMessage(Message.BASIC_DETAILS_LABEL));
        this.addTab(layoutUserRoles, messageSource.getMessage(Message.BREEDING_WORKFLOWS_LABEL));

        this.addListener(new WorkbenchSelectedTabChangeListener(this));

        //TODO: internationalize this
        createProjectPanel.setTitle(messageSource.getMessage(Message.BASIC_DETAILS_LABEL),"Update the name or date for this project.");
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
                createProjectPanel.setTitle(messageSource.getMessage(Message.BASIC_DETAILS_LABEL),"Update the name or date for this project.");
                break;
            case SECOND_TAB_USER_ROLES:
                createProjectPanel.setTitle(messageSource.getMessage(Message.BREEDING_WORKFLOWS_LABEL),"Mark the breeding workflow checkboxes that this project will be using.");
                break;
        }
    }


}
