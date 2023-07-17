
package org.generationcp.ibpworkbench.validator;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.service.api.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.annotation.Resource;
import java.util.regex.Pattern;

/**
 * Created by cyrus on 11/26/14.
 */

@Configurable
public class UserAccountValidator implements Validator {

	private static final Logger LOG = LoggerFactory.getLogger(UserAccountValidator.class);

	public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

	public static final String SIGNUP_FIELD_REQUIRED = "signup.field.required";
	public static final String SIGNUP_FIELD_PASSWORD_NOT_MATCH = "signup.field.password.not.match";
	public static final String SIGNUP_FIELD_USERNAME_EXISTS = "signup.field.username.exists";
	public static final String DATABASE_ERROR = "database.error";
	public static final String SIGNUP_FIELD_LENGTH_EXCEED = "signup.field.length.exceed";
	public static final String SIGNUP_FIELD_EMAIL_EXISTS = "signup.field.email.exists";
	public static final String SIGNUP_FIELD_INVALID_EMAIL_FORMAT = "signup.field.email.invalid";
	public static final String LOGIN_ATTEMPT_UNSUCCESSFUL = "login.attempt.unsuccessful";
	public static final String LOGIN_ATTEMPT_USER_INACTIVE = "login.attempt.user.inactive";
	public static final String PASSWORD_MINIMUM_LENGTH_MESSAGE = "login.password.minimum.length";
	public static final String PASSWORD_STRENGTH_CATEGORIES_MESSAGE = "login.password.strength.not.enough";
	public static final String PASSWORD_CONFIRMATION_DOES_NOT_MATCH = "login.password.confirmation.does.not.match";

	public static final String FIRST_NAME_STR = "First Name";
	public static final String LAST_NAME_STR = "Last Name";
	public static final String USERNAME_STR = "Username";
	public static final String EMAIL_STR = "Email";
	public static final String PASSWORD_STR = "Password";
	public static final String CONFIRMATION_PASSWORD_STR = "Confirmation Password";

	@Resource
	protected UserService userService;

	@Resource
	private WorkbenchUserService workbenchUserService;

	@Value("${security.login.password.minimum.length}")
	protected int passwordMinimumLength;

	@Override
	public boolean supports(final Class<?> aClass) {
		return UserAccountModel.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(final Object o, final Errors errors) {
		final UserAccountModel userAccount = (UserAccountModel) o;

		this.validateFieldsEmptyOrWhitespace(errors);

		this.validateFieldLength(errors, userAccount.getFirstName(), UserAccountFields.FIRST_NAME, UserAccountValidator.FIRST_NAME_STR, 20);
		this.validateFieldLength(errors, userAccount.getLastName(), UserAccountFields.LAST_NAME, UserAccountValidator.LAST_NAME_STR, 50);
		this.validateFieldLength(errors, userAccount.getUsername(), UserAccountFields.USERNAME, UserAccountValidator.USERNAME_STR, 30);

		this.validateEmailFormat(errors, userAccount);

		this.validatePasswordConfirmationIfEquals(errors, userAccount);

		this.validateUsernameIfExists(errors, userAccount);

		this.validatePersonEmailIfExists(errors, userAccount);

	}

	public void validateUserActive(final UserAccountModel userAccount, final Errors errors) {
		try {
			if (!this.workbenchUserService.isUserActive(userAccount)) {
				errors.rejectValue(UserAccountFields.USERNAME, UserAccountValidator.LOGIN_ATTEMPT_USER_INACTIVE);
			}
		} catch (final MiddlewareQueryException e) {
			errors.rejectValue(UserAccountFields.USERNAME, UserAccountValidator.DATABASE_ERROR);
			UserAccountValidator.LOG.error(e.getMessage(), e);
		}
	}

	public void validatePasswordLength(final UserAccountModel userAccount, final Errors errors) {
		if (StringUtils.isEmpty(userAccount.getPassword()) || userAccount.getPassword().length() < this.passwordMinimumLength) {
			errors.rejectValue(UserAccountFields.PASSWORD, UserAccountValidator.PASSWORD_MINIMUM_LENGTH_MESSAGE,
				new String[] {this.passwordMinimumLength + ""}, null);
		}
	}

	/**
	 * Validations related to password strength (IBPS-1025)
	 * Password should pass at least 3 of the 5 categories
	 *
	 * @param userAccount
	 * @param errors
	 */
	public void validatePasswordStrength(final UserAccountModel userAccount, final Errors errors) {
		final String password = userAccount.getPassword();
		Integer passwordScore = 0;

		if (!password.matches("\\A\\p{ASCII}*\\z")) { //password contains non-ascii character
			passwordScore++;
			password.replaceAll("\\A\\p{ASCII}*\\z", "");
		}
		if (password.matches(".*[A-Z].*")) {
			passwordScore++;
		}
		if (password.matches(".*[a-z].*")) {
			passwordScore++;
		}
		if (password.matches(".*\\d.*")) {
			passwordScore++;
		}
		if (!StringUtils.isAlphanumeric(password)) { //contains symbols
			passwordScore++;
		}

		if (passwordScore <= 3) {
			errors.rejectValue(UserAccountFields.PASSWORD, UserAccountValidator.PASSWORD_STRENGTH_CATEGORIES_MESSAGE,
				new String[] {this.passwordMinimumLength + ""}, null);
		}
	}

	public void validatePasswordConfirmation(final UserAccountModel userAccount, final Errors errors) {
		if (StringUtils.isNotEmpty(userAccount.getPassword()) && !userAccount.getPassword().equals(userAccount.getPasswordConfirmation())) {
			errors.rejectValue(UserAccountFields.PASSWORD_CONFIRMATION, UserAccountValidator.PASSWORD_CONFIRMATION_DOES_NOT_MATCH);
		}
	}

	protected void validateEmailFormat(final Errors errors, final UserAccountModel userAccount) {
		if (!Pattern.compile(UserAccountValidator.EMAIL_PATTERN).matcher(userAccount.getEmail()).matches()) {
			errors.rejectValue(UserAccountFields.EMAIL, UserAccountValidator.SIGNUP_FIELD_INVALID_EMAIL_FORMAT);
		}
	}

	protected void validateFieldsEmptyOrWhitespace(final Errors errors) {
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

	protected void validateFieldLength(final Errors errors, final String fieldValue, final String fieldProperty, final String fieldName,
		final Integer maxLength) {

		if (maxLength < fieldValue.length()) {
			errors.rejectValue(fieldProperty, UserAccountValidator.SIGNUP_FIELD_LENGTH_EXCEED, new String[] {
				Integer.toString(maxLength),
				fieldName}, null);
		}
	}

	protected void validatePasswordConfirmationIfEquals(final Errors errors, final UserAccountModel userAccount) {
		if (userAccount.getPassword() != null && userAccount.getPasswordConfirmation() != null
			&& !userAccount.getPassword().equals(userAccount.getPasswordConfirmation())) {

			errors.rejectValue(UserAccountFields.PASSWORD_CONFIRMATION, UserAccountValidator.SIGNUP_FIELD_PASSWORD_NOT_MATCH);
		}
	}

	protected void validateUsernameIfExists(final Errors errors, final UserAccountModel userAccount) {
		try {
			if (this.userService.isUsernameExists(userAccount.getUsername())) {
				errors.rejectValue(UserAccountFields.USERNAME, UserAccountValidator.SIGNUP_FIELD_USERNAME_EXISTS,
					new String[] {userAccount.getUsername()}, null);
			}
		} catch (final MiddlewareQueryException e) {
			errors.rejectValue(UserAccountFields.USERNAME, UserAccountValidator.DATABASE_ERROR);
			UserAccountValidator.LOG.error(e.getMessage(), e);
		}
	}

	protected void validatePersonEmailIfExists(final Errors errors, final UserAccountModel userAccount) {
		try {
			if (this.userService.isPersonWithEmailExists(userAccount.getEmail())) {
				errors.rejectValue(UserAccountFields.EMAIL, UserAccountValidator.SIGNUP_FIELD_EMAIL_EXISTS);
			}
		} catch (final MiddlewareQueryException e) {
			errors.rejectValue(UserAccountFields.EMAIL, UserAccountValidator.DATABASE_ERROR);
			UserAccountValidator.LOG.error(e.getMessage(), e);
		}
	}

	public void setPasswordMinimumLength(final int passwordMinimumLength) {
		this.passwordMinimumLength = passwordMinimumLength;
	}
}
