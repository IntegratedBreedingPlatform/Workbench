package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.data.Validator;
import org.generationcp.ibpworkbench.ui.dashboard.WorkbenchDashboard;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by cyrus on 5/19/14.
 */
public class AddProgramPresenter {
    private static final Logger LOG = LoggerFactory.getLogger(AddProgramPresenter.class);

    private AddProgramPanel view;

    private Set<User> users;
    private Project project;

    public AddProgramPresenter(AddProgramPanel view) {
        this.view = view;
    }

    public boolean validateAndSaveBasicDetails() {
        LOG.debug("Do validate basic details");
        try {
            //this.users = view.programMembersPanel.validateAndSave();
            this.project = view.createProjectPanel.projectBasicDetailsComponent.validateAndSave();
            return true;
        } catch (Validator.InvalidValueException e) {
            return false;
        }
    }

    public void enableProgramMethodsAndLocationsTab() {
        view.enableProgramMethodsAndLocationsTab(project.getCropType());
    }


    /*
    public boolean validateBasicDetailsAndMembers() {


        // validate basic details first
        try{
            this.users = view.programMembersPanel.validateAndSave();

            return true;
        } catch (Validator.InvalidValueException e) {
            return false;
        }
    } */



}
