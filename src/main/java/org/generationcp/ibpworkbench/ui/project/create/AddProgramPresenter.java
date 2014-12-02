package org.generationcp.ibpworkbench.ui.project.create;

import java.util.Set;

import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.database.IBDBGeneratorCentralDb;
import org.generationcp.ibpworkbench.database.IBDBGeneratorLocalDb;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Validator.InvalidValueException;

/**
 * Created by cyrus on 5/19/14.
 */
@Configurable
public class AddProgramPresenter {
    private AddProgramView view;

    @Autowired
    private ProgramService programService;

    @Autowired
    private SessionData sessionData;
    
    private Set<User> users;
    protected Project program;
    
    public AddProgramPresenter(AddProgramView view) {
        this.view = view;
    }

    public boolean validateAndGetBasicDetails() {
        try {
            this.program = view.createProjectPanel.projectBasicDetailsComponent.getProjectDetails();
            this.users = this.view.programMembersPanel.getSelectedUsers();
            return true;
        } catch (InvalidValueException e) {
            return false;
        }
    }
    
    public void enableProgramMethodsAndLocationsTab() {
        view.updateUIOnProgramSave(sessionData.getSelectedProject());
    }

    public void disableProgramMethodsAndLocationsTab() { 
    	view.disableOptionalTabsAndFinish(); 
    }

    public void doAddNewProgram() throws Exception {

        if (!validateAndGetBasicDetails()) {
            throw new Exception("basic_details_invalid");
        }

        programService.setCurrentUser(this.sessionData.getUserData());
        programService.setSelectedUsers(this.users);
        programService.setCentralDbGenerator(new IBDBGeneratorCentralDb());
        programService.setLocalDbGenerator(new IBDBGeneratorLocalDb());
        programService.createNewProgram(this.program);
    }

    public void resetBasicDetails() {
        view.resetBasicDetails();
    }

    public void resetProgramMembers() {
        view.resetProgramMembers();
    }
}
