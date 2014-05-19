package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.data.Validator;
import org.generationcp.ibpworkbench.ui.programlocations.LocationViewModel;
import org.generationcp.ibpworkbench.ui.programmethods.MethodView;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;

/**
 * Created by cyrus on 5/19/14.
 */
public class AddProgramPresenter {
    private static final Logger LOG = LoggerFactory.getLogger(AddProgramPresenter.class);

    private AddProgramView view;

    private Set<User> users;
    private Project project;
    private Collection<LocationViewModel> favoriteLocations;
    private Collection<MethodView> favoriteMethods;

    public AddProgramPresenter(AddProgramView view) {
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

    public boolean validateAndSaveProgramMembers() {
        this.users = this.view.programMembersPanel.validateAndSave();

        return true;    // always allow.
    }

    public void enableProgramMethodsAndLocationsTab() {
        boolean isGenericCrop = true;
        for (CropType.CropEnum cropEnum : CropType.CropEnum.values()) {
            if (project.getCropType().getCropName().equalsIgnoreCase(cropEnum.toString())) {
                isGenericCrop = false;

                break;
            }
        }

        if (isGenericCrop)
            view.enableFinishBtn();
        else
            view.enableOptionalTabsAndFinish(project.getCropType());

    }

    public void disableProgramMethodsAndLocationsTab() { view.disableOptionalTabsAndFinish(); }

    public void doAddNewProgram() {

        if (!validateAndSaveBasicDetails())
            return;

        if (!validateAndSaveProgramMembers())
            return;

        // Add Program logic here
        // set program members logic here
        // save logic for program location
        // save logic for program methods
    }

}
