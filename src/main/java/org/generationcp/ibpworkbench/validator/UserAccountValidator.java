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

	@Override
	public boolean supports(Class<?> aClass) {
		return UserAccountModel.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		UserAccountModel userAccount = (UserAccountModel) o;

		validateFieldsEmptyOrWhitespace(errors);

		validatePasswordConfirmationIfEquals(errors, userAccount);

		validateUsernameIfExists(errors, userAccount);

		validatePersonIfExists(errors, userAccount);

		validateUserRole(errors, userAccount);
	}

	protected void validateFieldsEmptyOrWhitespace(Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "signup.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "signup.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "signup.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "signup.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "signup.field.required");
		ValidationUtils
				.rejectIfEmptyOrWhitespace(errors, "passwordConfirmation", "signup.field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "role", "signup.field.required");
	}

	protected void validateUserRole(Errors errors, UserAccountModel userAccount) {
		if (!UserAccountModel.ADMIN_ROLE.equals(userAccount.getRole()) &&
				!UserAccountModel.BREEDER_ROLE.equals(userAccount.getRole()) &&
				!UserAccountModel.TECHNICIAN_ROLE.equals(userAccount.getRole())) {
			errors.rejectValue("role", "signup.field.invalid.role");
		}
	}

	protected void validatePasswordConfirmationIfEquals(Errors errors,
			UserAccountModel userAccount) {
		if (userAccount.getPassword() != null
				&& userAccount.getPasswordConfirmation() != null
				&& !userAccount.getPassword().equals(userAccount.getPasswordConfirmation())) {

			errors.rejectValue("password", "signup.field.password.not.match");
		}
	}

	protected void validateUsernameIfExists(Errors errors, UserAccountModel userAccount) {
		try {
			if (workbenchDataManager.isUsernameExists(userAccount.getUsername())) {
				errors.rejectValue("username", "signup.field.username.exists");
			}
		} catch (MiddlewareQueryException e) {
			errors.rejectValue("username", "database.error");
		}
	}

	protected void validatePersonIfExists(Errors errors, UserAccountModel userAccount) {
		try {
			if (workbenchDataManager
					.isPersonExists(userAccount.getFirstName(), userAccount.getLastName())) {
				errors.rejectValue("firstName", "signup.field.person.exists");
			}
		} catch (MiddlewareQueryException e) {
			errors.rejectValue("firstName", "database.error");
		}
	}
}
