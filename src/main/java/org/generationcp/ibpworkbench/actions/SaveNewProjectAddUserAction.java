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

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.commons.vaadin.validator.ValidationUtil;
import org.generationcp.ibpworkbench.Message;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.ibpworkbench.ui.common.TwinTableSelect;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.HashSet;
import java.util.Set;

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
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private ContextUtil contextUtil;

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
			LOG.error(e.getMessage(), e);
			MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), e.getDescription());
			return;
		} catch (final InvalidValueException e) {
			LOG.error(e.getMessage(), e);
			MessageNotifier.showRequiredFieldError(event.getComponent().getWindow(), ValidationUtil.getMessageFor(e));
			return;
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
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

		contextUtil.logProgramActivity("Program Member",
				"Added a new user (" + userAccount.getUsername() + ") to " + contextUtil.getProjectInContext().getProjectName());

		final CloseWindowAction action = new CloseWindowAction();
		action.buttonClick(event);

	}

	protected void saveUserAccount(final UserAccountModel userAccount, final TwinTableSelect<User> membersSelect) {
		userAccount.trimAll();

		final User user = this.workbenchUserService.saveNewUserAccount(userAccount);

		// add new user to the TwinColumnSelect
		membersSelect.addItem(user);

		// get currently selected users and add the new user
		@SuppressWarnings("unchecked") final Set<User> selectedMembers = new HashSet<User>(membersSelect.getValue());
		selectedMembers.add(user);
		membersSelect.setValue(selectedMembers);
	}

}
