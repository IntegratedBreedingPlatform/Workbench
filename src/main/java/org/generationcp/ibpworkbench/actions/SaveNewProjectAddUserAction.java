/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.ibpworkbench.actions;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.security.Role;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.commons.vaadin.validator.ValidationUtil;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.ProgramService;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;

/**
 * <b>Description</b>: Listener responsible for saving new Users and Persons records created from the Create New Project screen.
 *
 * <br>
 * <br>
 *
 * <b>Author</b>: Mark Agarrado <br>
 * <b>File Created</b>: October 15, 2012
 */
@Configurable
public class SaveNewProjectAddUserAction implements ClickListener {

	private static final Logger LOG = LoggerFactory.getLogger(SaveNewProjectAddUserAction.class);

	private static final long serialVersionUID = 5386242653138617919L;

	private final Form userAccountForm;
	private final TwinTableSelect<User> membersSelect;

	@Autowired
	private WorkbenchUserService workbenchUserService;

	@Autowired
	private ProgramService programService;

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private SessionData sessionData;

	public SaveNewProjectAddUserAction(final Form userAccountForm, final TwinTableSelect<User> membersSelect) {
		this.userAccountForm = userAccountForm;
		this.membersSelect = membersSelect;
	}

	// Code reviewed by Cyrus: Logic quite similar to SaveUserAccountAction,
	// this can be consolidated to avoid redundant code
	@Override
	public void buttonClick(final ClickEvent event) {
		@SuppressWarnings("unchecked") final BeanItem<UserAccountModel> bean =
				(BeanItem<UserAccountModel>) this.userAccountForm.getItemDataSource();
		final UserAccountModel userAccount = bean.getBean();

		try {
			this.userAccountForm.commit();
		} catch (final InternationalizableException e) {
			SaveNewProjectAddUserAction.LOG.error(e.getMessage(), e);
			MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), e.getDescription());
			return;
		} catch (final InvalidValueException e) {
			SaveNewProjectAddUserAction.LOG.error(e.getMessage(), e);
			MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), ValidationUtil.getMessageFor(e));
			return;
		} catch (final Exception e) {
			SaveNewProjectAddUserAction.LOG.error(e.getMessage(), e);
			return;
		}

		try {
			this.saveUserAccount(userAccount, this.membersSelect);
		} catch (final MiddlewareQueryException e) {
			SaveNewProjectAddUserAction.LOG.error("Error encountered while trying to save user account details.", e);
			MessageNotifier.showError(event.getComponent().getWindow(), this.messageSource.getMessage(Message.DATABASE_ERROR),
					this.messageSource.getMessage(Message.SAVE_USER_ACCOUNT_ERROR_DESC));
			return;
		}

		try {
			final User user = this.sessionData.getUserData();
			final Project currentProject = this.sessionData.getLastOpenedProject();

			if (currentProject != null) {
				final ProjectActivity projAct =
						new ProjectActivity(new Integer(currentProject.getProjectId().intValue()), currentProject, "Program Member",
								"Added a new user (" + userAccount.getUsername() + ") to " + currentProject.getProjectName(), user,
								new Date());
				this.workbenchUserService.addProjectActivity(projAct);
			}
		} catch (final MiddlewareQueryException e) {
			SaveNewProjectAddUserAction.LOG.error("Cannot log project activity", e);
		}

		final CloseWindowAction action = new CloseWindowAction();
		action.buttonClick(event);

	}

	protected void saveUserAccount(final UserAccountModel userAccount, final TwinTableSelect<User> membersSelect) {
		userAccount.trimAll();

		final User user = this.workbenchUserService.saveNewUserAccount(userAccount);

		// if admin, add user as program member of all programs of the current crop
		this.addUserToAllProgramsOfCurrentCropIfAdmin(user);

		// add new user to the TwinColumnSelect
		membersSelect.addItem(user);

		// get currently selected users and add the new user
		final Set<User> selectedMembers = new HashSet<User>(membersSelect.getValue());
		selectedMembers.add(user);
		membersSelect.setValue(selectedMembers);
	}

	public void addUserToAllProgramsOfCurrentCropIfAdmin(final User user) {
		final Project currentProject = this.sessionData.getLastOpenedProject();
		this.programService.addUserToAllProgramsOfCropTypeIfAdmin(user, currentProject.getCropType());

		// disable the user item in the member selection table if the role is ADMIN
		user.setEnabled(!user.hasRole(Role.ADMIN.toString()));
	}

}
