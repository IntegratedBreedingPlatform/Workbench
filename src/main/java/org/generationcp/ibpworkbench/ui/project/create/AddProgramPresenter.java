
package org.generationcp.ibpworkbench.ui.project.create;

import java.util.Set;

import javax.annotation.Resource;

import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Validator.InvalidValueException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

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

	@Resource
	private PlatformTransactionManager transactionManager;

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

	public void doAddNewProgram() {
		if (!AddProgramPresenter.this.validateAndGetBasicDetails()) {
			throw new AddProgramException("basic_details_invalid");
		}

		try {
			new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
					AddProgramPresenter.this.programService.setCurrentUser(AddProgramPresenter.this.sessionData.getUserData());
					AddProgramPresenter.this.programService.setSelectedUsers(AddProgramPresenter.this.users);
					AddProgramPresenter.this.programService.createNewProgram(AddProgramPresenter.this.program);
				}
			});
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
