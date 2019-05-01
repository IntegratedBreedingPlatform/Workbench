
package org.generationcp.ibpworkbench.validator;

import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by cyrus on 11/26/14.
 */

@Configurable
public class UserAccountValidator implements Validator {

	private static final Logger LOG = LoggerFactory.getLogger(UserAccountValidator.class);

	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

	public static final String SIGNUP_FIELD_REQUIRED = "signup.field.required";
	public static final String SIGNUP_FIELD_INVALID_ROLE = "signup.field.invalid.role";
	public static final String SIGNUP_FIELD_PASSWORD_NOT_MATCH = "signup.field.password.not.match";
	public static final String SIGNUP_FIELD_USERNAME_EXISTS = "signup.field.username.exists";
	public static final String DATABASE_ERROR = "database.error";
	public static final String SIGNUP_FIELD_PERSON_EXISTS = "signup.field.person.exists";
	public static final String SIGNUP_FIELD_LENGTH_EXCEED = "signup.field.length.exceed";
	public static final String SIGNUP_FIELD_EMAIL_EXISTS = "signup.field.email.exists";
	public static final String SIGNUP_FIELD_INVALID_EMAIL_FORMAT = "signup.field.email.invalid";
	public static final String LOGIN_ATTEMPT_UNSUCCESSFUL = "login.attempt.unsuccessful";
	public static final String LOGIN_ATTEMPT_USER_INACTIVE = "login.attempt.user.inactive";

	public static final String FIRST_NAME_STR = "First Name";
	public static final String LAST_NAME_STR = "Last Name";
	public static final String USERNAME_STR = "Username";
	public static final String EMAIL_STR = "Email";
	public static final String PASSWORD_STR = "Password";
	public static final String CONFIRMATION_PASSWORD_STR = "Confirmation Password";

	@Resource
	protected WorkbenchDataManager workbenchDataManager;

	@Resource
	private WorkbenchUserService workbenchUserService;

	@Override
	public boolean supports(Class<?> aClass) {
		return UserAccountModel.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		UserAccountModel userAccount = (UserAccountModel) o;

		this.validateFieldsEmptyOrWhitespace(errors);

		this.validateFieldLength(errors, userAccount.getFirstName(), UserAccountFields.FIRST_NAME, UserAccountValidator.FIRST_NAME_STR, 20);
		this.validateFieldLength(errors, userAccount.getLastName(), UserAccountFields.LAST_NAME, UserAccountValidator.LAST_NAME_STR, 50);
		this.validateFieldLength(errors, userAccount.getUsername(), UserAccountFields.USERNAME, UserAccountValidator.USERNAME_STR, 30);

		this.validateEmailFormat(errors, userAccount);

		this.validatePasswordConfirmationIfEquals(errors, userAccount);

		this.validateUsernameIfExists(errors, userAccount);

		this.validatePersonEmailIfExists(errors, userAccount);

	}

	public void validateUserActive(UserAccountModel userAccount, Errors errors) {
		try {
			if (!this.workbenchUserService.isUserActive(userAccount)) {
				errors.rejectValue(UserAccountFields.USERNAME, UserAccountValidator.LOGIN_ATTEMPT_USER_INACTIVE);
			}
		} catch (MiddlewareQueryException e) {
			errors.rejectValue(UserAccountFields.USERNAME, UserAccountValidator.DATABASE_ERROR);
			UserAccountValidator.LOG.error(e.getMessage(), e);
		}
	}

	protected void validateEmailFormat(Errors errors, UserAccountModel userAccount) {
		if (!Pattern.compile(UserAccountValidator.EMAIL_PATTERN).matcher(userAccount.getEmail()).matches()) {
			errors.rejectValue(UserAccountFields.EMAIL, UserAccountValidator.SIGNUP_FIELD_INVALID_EMAIL_FORMAT);
		}
	}

	protected void validateFieldsEmptyOrWhitespace(Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, UserAccountFields.FIRST_NAME, UserAccountValidator.SIGNUP_FIELD_REQUIRED,
				new String[] {UserAccountValidator.FIRST_NAME_STR});
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, UserAccountFields.LAST_NAME, UserAccountValidator.SIGNUP_FIELD_REQUIRED,
				new String[] {UserAccountValidator.LAST_NAME_STR});
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, UserAccountFields.EMAIL, UserAccountValidator.SIGNUP_FIELD_REQUIRED,
				new String[] {UserAccountValidator.EMAIL_STR});
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, UserAccountFields.USERNAME, UserAccountValidator.SIGNUP_FIELD_REQUIRED,
				new String[] {UserAccountValidator.USERNAME_STR});
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, UserAccountFields.PASSWORD, UserAccountValidator.SIGNUP_FIELD_REQUIRED,
				new String[] {UserAccountValidator.PASSWORD_STR});
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, UserAccountFields.PASSWORD_CONFIRMATION,
				UserAccountValidator.SIGNUP_FIELD_REQUIRED);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, UserAccountFields.ROLE, UserAccountValidator.SIGNUP_FIELD_REQUIRED,
				new String[] {UserAccountValidator.CONFIRMATION_PASSWORD_STR});
	}

	protected void validateFieldLength(Errors errors, String fieldValue, String fieldProperty, String fieldName, Integer maxLength) {

		if (maxLength < fieldValue.length()) {
			errors.rejectValue(fieldProperty, UserAccountValidator.SIGNUP_FIELD_LENGTH_EXCEED, new String[] {Integer.toString(maxLength),
					fieldName}, null);
		}
	}

	protected void validatePasswordConfirmationIfEquals(Errors errors, UserAccountModel userAccount) {
		if (userAccount.getPassword() != null && userAccount.getPasswordConfirmation() != null
				&& !userAccount.getPassword().equals(userAccount.getPasswordConfirmation())) {

			errors.rejectValue(UserAccountFields.PASSWORD_CONFIRMATION, UserAccountValidator.SIGNUP_FIELD_PASSWORD_NOT_MATCH);
		}
	}

	protected void validateUsernameIfExists(Errors errors, UserAccountModel userAccount) {
		try {
			if (this.workbenchDataManager.isUsernameExists(userAccount.getUsername())) {
				errors.rejectValue(UserAccountFields.USERNAME, UserAccountValidator.SIGNUP_FIELD_USERNAME_EXISTS,
						new String[] {userAccount.getUsername()}, null);
			}
		} catch (MiddlewareQueryException e) {
			errors.rejectValue(UserAccountFields.USERNAME, UserAccountValidator.DATABASE_ERROR);
			UserAccountValidator.LOG.error(e.getMessage(), e);
		}
	}

	protected void validatePersonEmailIfExists(Errors errors, UserAccountModel userAccount) {
		try {
			if (this.workbenchDataManager.isPersonWithEmailExists(userAccount.getEmail())) {
				errors.rejectValue(UserAccountFields.EMAIL, UserAccountValidator.SIGNUP_FIELD_EMAIL_EXISTS);
			}
		} catch (MiddlewareQueryException e) {
			errors.rejectValue(UserAccountFields.EMAIL, UserAccountValidator.DATABASE_ERROR);
			UserAccountValidator.LOG.error(e.getMessage(), e);
		}
	}
}
