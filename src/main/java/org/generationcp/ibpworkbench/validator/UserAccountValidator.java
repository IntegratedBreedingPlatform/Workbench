package org.generationcp.ibpworkbench.validator;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by cyrus on 11/26/14.
 */

@Configurable
public class UserAccountValidator implements Validator {

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Override public boolean supports(Class<?> aClass) {
		return UserAccountModel.class.isAssignableFrom(aClass);
	}

	@Override public void validate(Object o, Errors errors) {
		UserAccountModel userAccount = (UserAccountModel) o;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "signup.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "signup.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "signup.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "signup.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "signup.field.required");
		ValidationUtils
				.rejectIfEmptyOrWhitespace(errors, "passwordConfirmation", "signup.field.required");

		validatePasswordConfirmationIfEquals(errors, userAccount);

		validateUsernameIfExists(errors, userAccount);

		validatePersonIfExists(errors, userAccount);

	}

	protected void validatePasswordConfirmationIfEquals(Errors errors,
			UserAccountModel userAccount) {
		if (userAccount.getPassword() != null
				&& userAccount.getPasswordConfirmation() != null
				&& !userAccount.getPassword().equals(userAccount.getPasswordConfirmation())) {

			errors.rejectValue("password", "signup.field.password.not.match",
					"The passwords entered do not match. Please try again.");
		}
	}

	protected void validateUsernameIfExists(Errors errors, UserAccountModel userAccount) {
		try {
			if (workbenchDataManager.isUsernameExists(userAccount.getUsername())) {
				errors.rejectValue("username", "signup.field.username.exists",
						new String[] { userAccount.getUsername() },
						"Username with \"{0}\" already exists");
			}
		} catch (MiddlewareQueryException e) {
			errors.rejectValue("username", "database.error");
		}
	}

	protected void validatePersonIfExists(Errors errors, UserAccountModel userAccount) {
		try {
			if (workbenchDataManager
					.isPersonExists(userAccount.getFirstName(), userAccount.getLastName())) {
				errors.rejectValue("firstName", "signup.field.person.exists",
						new String[] { userAccount.getFirstName(), userAccount.getLastName() },
						"Person with First Name \"{0}\" and Last Name \"{1}\" already exists");
			}
		} catch (MiddlewareQueryException e) {
			errors.rejectValue("firstName", "database.error");
		}
	}
}
