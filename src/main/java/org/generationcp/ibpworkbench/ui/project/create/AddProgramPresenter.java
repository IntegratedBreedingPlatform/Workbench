
package org.generationcp.ibpworkbench.ui.project.create;

import java.util.Set;

import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

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

	public AddProgramPresenter(AddProgramView view) {
		this.view = view;
	}


	public void enableProgramMethodsAndLocationsTab() {
		this.view.updateUIOnProgramSave(this.sessionData.getSelectedProject());
	}

	public void disableProgramMethodsAndLocationsTab() {
		this.view.disableOptionalTabsAndFinish();
	}

	public Project doAddNewProgram() {
		try {
			final Project program = AddProgramPresenter.this.view.createProjectPanel.projectBasicDetailsComponent.getProjectDetails();
			final Set<User> users = AddProgramPresenter.this.view.programMembersPanel.getSelectedUsers();
			AddProgramPresenter.this.programService.createNewProgram(program, users);
			return program;
		} catch (RuntimeException e) {
			throw new AddProgramException("The application could not successfully create"
					+ " a program. Please contact support for further help.",e);
		}
	}

	public void resetBasicDetails() {
		this.view.resetBasicDetails();
	}

	public void resetProgramMembers() {
		this.view.resetProgramMembers();
	}

}
