package org.generationcp.ibpworkbench.ui.project.create;

import com.vaadin.data.Validator;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Window;

import java.util.Set;

/**
 * Created by cyrus on 5/19/14.
 */
@Configurable
public class AddProgramPresenter {

	private final AddProgramView view;

	@Autowired
	private ProgramService programService;

	@Autowired
	private ContextUtil contextUtil;

	public AddProgramPresenter(final AddProgramView view) {
		this.view = view;
	}

	public void enableProgramMethodsAndLocationsTab(final Window window) {
		this.view.updateUIOnProgramSave(this.contextUtil.getProjectInContext(), window);
	}

	public void disableProgramMethodsAndLocationsTab() {
		this.view.disableOptionalTabsAndFinish();
	}

	public Project doAddNewProgram() {
		final Project program;

		try {
			program = AddProgramPresenter.this.view.createProjectPanel.projectBasicDetailsComponent.getProjectDetails();
			AddProgramPresenter.this.programService.createNewProgram(program);

		} catch (final Validator.InvalidValueException e) {
			throw e;
		} catch (final RuntimeException e) {
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


	public void setProgramService(ProgramService programService) {
		this.programService = programService;
	}


	public void setContextUtil(ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

}
