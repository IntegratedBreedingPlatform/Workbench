
package org.generationcp.ibpworkbench.ui.project.create;

import java.util.Set;

import com.vaadin.data.Validator;
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
		final Project program;
		final Set<User> users;
		try {
			program = AddProgramPresenter.this.view.createProjectPanel.projectBasicDetailsComponent.getProjectDetails();
			users = AddProgramPresenter.this.view.programMembersPanel.getSelectedUsers();
			AddProgramPresenter.this.programService.createNewProgram(program, users);

		} catch (Validator.InvalidValueException e) {
			throw e;
		} catch (RuntimeException e) {
			throw new AddProgramException(
				"The application could not successfully create a program. Please contact support for further help.", e);
		}
		return program;
	}

	public void resetBasicDetails() {
		this.view.resetBasicDetails();
	}

	public void resetProgramMembers() {
		this.view.resetProgramMembers();
	}

}
