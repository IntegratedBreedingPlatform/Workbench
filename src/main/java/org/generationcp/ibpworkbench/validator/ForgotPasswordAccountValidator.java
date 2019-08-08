
package org.generationcp.ibpworkbench.validator;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;

/**
 * Created by cyrus on 1/19/15.
 */
public class ForgotPasswordAccountValidator extends UserAccountValidator {

	private static final Logger LOG = LoggerFactory.getLogger(ForgotPasswordAccountValidator.class);

	public static final String SIGNUP_FIELD_USERNAME_EMAIL_COMBO_NOT_EXISTS = "signup.field.username.email.combo.not.exists";

	@Override
	public void validate(Object o, Errors errors) {
		UserAccountModel userAccount = (UserAccountModel) o;

		this.validatePasswordConfirmationIfEquals(errors, userAccount);

		this.validateUsernameAndEmailIfNotExists(errors, userAccount);
	}

	protected void validateUsernameAndEmailIfNotExists(Errors errors, UserAccountModel userAccount) {
		try {
			if (!this.userService.isPersonWithUsernameAndEmailExists(userAccount.getUsername(), userAccount.getEmail())) {
				errors.rejectValue(UserAccountFields.USERNAME, ForgotPasswordAccountValidator.SIGNUP_FIELD_USERNAME_EMAIL_COMBO_NOT_EXISTS,
						new String[] {userAccount.getUsername(), userAccount.getEmail()}, null);
			}
		} catch (MiddlewareQueryException e) {
			errors.rejectValue(UserAccountFields.USERNAME, UserAccountValidator.DATABASE_ERROR);
			ForgotPasswordAccountValidator.LOG.error(e.getMessage(), e);
		}
	}
}
