
package org.generationcp.ibpworkbench.ui.project.create;

import java.util.Set;

import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.database.MysqlAccountGenerator;
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

	private final AddProgramView view;

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
			this.program = this.view.createProjectPanel.projectBasicDetailsComponent.getProjectDetails();
			this.users = this.view.programMembersPanel.getSelectedUsers();
			return true;
		} catch (InvalidValueException e) {
			return false;
		}
	}

	public void enableProgramMethodsAndLocationsTab() {
		this.view.updateUIOnProgramSave(this.sessionData.getSelectedProject());
	}

	public void disableProgramMethodsAndLocationsTab() {
		this.view.disableOptionalTabsAndFinish();
	}

	public void doAddNewProgram() throws Exception {

		if (!this.validateAndGetBasicDetails()) {
			throw new Exception("basic_details_invalid");
		}

		this.programService.setCurrentUser(this.sessionData.getUserData());
		this.programService.setSelectedUsers(this.users);
		this.programService.setMySQLAccountGenerator(new MysqlAccountGenerator());
		this.programService.createNewProgram(this.program);
	}

	public void resetBasicDetails() {
		this.view.resetBasicDetails();
	}

	public void resetProgramMembers() {
		this.view.resetProgramMembers();
	}
}
