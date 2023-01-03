
package org.generationcp.ibpworkbench.validator;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;

@RunWith(MockitoJUnitRunner.class)
public class UserAccountValidatorTest {

	@Mock
	private Errors errors;

	@Mock
	private UserService userService;

	@Mock
	private WorkbenchUserService workbenchUserService;

	@InjectMocks
	private UserAccountValidator validator;

	@Test
	public void testSupports() {
		Assert.assertTrue("UserAccountModel.class is supported by validator", this.validator.supports(UserAccountModel.class));
	}

	@Test
	public void testValidate() {
		final UserAccountValidator partialValidator = Mockito.spy(this.validator);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setFirstName("firstName");
		userAccount.setLastName("lastName");
		userAccount.setEmail("email@email.com");
		// Role ID 1 = ADMIN
		userAccount.setRole(new Role(1));
		userAccount.setUsername("username");
		userAccount.setPassword("password");
		userAccount.setPasswordConfirmation("password");

		Mockito.when(this.userService.isUsernameExists(userAccount.getUsername())).thenReturn(false);

		partialValidator.validate(userAccount, this.errors);

		Mockito.verify(partialValidator, Mockito.times(3))
			.validateFieldLength(ArgumentMatchers.any(Errors.class), ArgumentMatchers.anyString(),
				ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());

		Mockito.verify(partialValidator).validateEmailFormat(this.errors, userAccount);

		Mockito.verify(partialValidator).validateFieldsEmptyOrWhitespace(this.errors);

		Mockito.verify(partialValidator).validatePasswordConfirmationIfEquals(this.errors, userAccount);

		Mockito.verify(partialValidator).validateUsernameIfExists(this.errors, userAccount);

		Mockito.verify(partialValidator).validatePersonEmailIfExists(this.errors, userAccount);

	}

	@Test
	public void testValidateUserActive() {
		final ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);
		final UserAccountModel userAccount = new UserAccountModel();

		Mockito.when(this.workbenchUserService.isUserActive(userAccount)).thenReturn(false);

		this.validator.validateUserActive(userAccount, this.errors);

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture());
		Assert.assertEquals("error should output email field", UserAccountFields.USERNAME, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.LOGIN_ATTEMPT_USER_INACTIVE, arg2.getValue());

		Mockito.when(this.workbenchUserService.isUserActive(userAccount)).thenReturn(true);

		this.validator.validateUserActive(userAccount, this.errors);

		Assert.assertFalse(this.errors.hasErrors());

		Mockito.when(this.workbenchUserService.isUserActive(userAccount)).thenThrow(new MiddlewareQueryException(""));

		this.validator.validateUserActive(userAccount, this.errors);

		Mockito.verify(this.errors, Mockito.times(2)).rejectValue(arg1.capture(), arg2.capture());
		Assert.assertEquals("error should output email field", UserAccountFields.USERNAME, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.DATABASE_ERROR, arg2.getValue());
	}

	@Test
	public void testValidatePasswordConfirmationIfEquals() {
		final ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setPassword("password");
		userAccount.setPasswordConfirmation("passwordNotEquals");

		final UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validatePasswordConfirmationIfEquals(this.errors, userAccount);

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture());
		Assert.assertEquals("error should output password confirmation field", UserAccountFields.PASSWORD_CONFIRMATION, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.SIGNUP_FIELD_PASSWORD_NOT_MATCH, arg2.getValue());

	}

	@Test
	public void testValidatePasswordConfirmationIfEqualsNoError() {
		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setPassword("password");
		userAccount.setPasswordConfirmation("password");

		final UserAccountValidator partialValidator = Mockito.spy(this.validator);

		partialValidator.validatePasswordConfirmationIfEquals(this.errors, userAccount);

		Mockito.verify(this.errors, Mockito.never()).rejectValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
	}

	@Test
	public void testValidateUsernameIfExists() {
		final ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("username");

		Mockito.when(this.userService.isUsernameExists(userAccount.getUsername())).thenReturn(true);

		final UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateUsernameIfExists(this.errors, userAccount);

		Mockito.verify(this.errors)
			.rejectValue(arg1.capture(), arg2.capture(), ArgumentMatchers.any(String[].class), ArgumentMatchers.<String>isNull());
		Assert.assertEquals("error should output username field", UserAccountFields.USERNAME, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.SIGNUP_FIELD_USERNAME_EXISTS, arg2.getValue());
	}

	@Test
	public void testValidateUsernameIfExistsDatabaseError() {
		final ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("username");

		Mockito.when(this.userService.isUsernameExists(userAccount.getUsername())).thenThrow(MiddlewareQueryException.class);

		final UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateUsernameIfExists(this.errors, userAccount);

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture());
		Assert.assertEquals("error should output username field", UserAccountFields.USERNAME, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.DATABASE_ERROR, arg2.getValue());
	}

	@Test
	public void testValidateUsernameIfExistsNoError() {
		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("username");

		Mockito.when(this.userService.isUsernameExists(userAccount.getUsername())).thenReturn(false);

		final UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateUsernameIfExists(this.errors, userAccount);

		Mockito.verify(this.errors, Mockito.never()).rejectValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
	}

	@Test
	public void testValidateEmailFormat() {
		final ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setEmail("invalidEmail.com");

		final UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateEmailFormat(this.errors, userAccount);

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture());

		Assert.assertEquals("error should output email field", UserAccountFields.EMAIL, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.SIGNUP_FIELD_INVALID_EMAIL_FORMAT, arg2.getValue());
	}

	@Test
	public void testValidateEmailFormatValid() {
		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setEmail("validemail123@outlook.com");

		final UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateEmailFormat(this.errors, userAccount);

		Mockito.verify(this.errors, Mockito.never()).rejectValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

		userAccount.setEmail("christian.obermeier@agrar.uni-giessen.de");
		partialValidator.validateEmailFormat(this.errors, userAccount);

		Mockito.verify(this.errors, Mockito.never()).rejectValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
	}

	@Test
	public void testValidateFieldLength() {
		final UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateFieldLength(this.errors, "invalid length", "field.prop", "Field Name", 5);

		Mockito.verify(this.errors)
			.rejectValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class),
				ArgumentMatchers.<String>isNull());
	}

	@Test
	public void testValidateFieldLengthValidValue() {
		final UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateFieldLength(this.errors, "valid length", "field.prop", "Field Name", 30);

		Mockito.verify(this.errors, Mockito.never())
			.rejectValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class),
				ArgumentMatchers.anyString());
	}

	@Test
	public void testValidateEmailIfExists() {
		final ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setEmail("email@email.com");

		Mockito.when(this.userService.isPersonWithEmailExists(userAccount.getEmail())).thenReturn(true);

		final UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validatePersonEmailIfExists(this.errors, userAccount);

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture());
		Assert.assertEquals("error should output email field", UserAccountFields.EMAIL, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.SIGNUP_FIELD_EMAIL_EXISTS, arg2.getValue());
	}

	@Test
	public void testValidateEmailIfExistsDatabaseError() {
		final ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setEmail("email@email.com");

		Mockito.when(this.userService.isPersonWithEmailExists(userAccount.getEmail())).thenThrow(MiddlewareQueryException.class);

		final UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validatePersonEmailIfExists(this.errors, userAccount);

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture());
		Assert.assertEquals("error should output email field", UserAccountFields.EMAIL, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.DATABASE_ERROR, arg2.getValue());
	}

	@Test
	public void testValidateEmailIfExistsNoError() {
		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("email@email.com");

		Mockito.when(this.userService.isPersonWithEmailExists(userAccount.getEmail())).thenReturn(false);

		final UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validatePersonEmailIfExists(this.errors, userAccount);

		Mockito.verify(this.errors, Mockito.never()).rejectValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
	}

	@Test
	public void testValidatePasswordLength_PasswordIsLessThanMinimumLength() {

		this.validator.setPasswordMinimumLength(6);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setPassword(RandomStringUtils.randomAlphanumeric(5));

		this.validator.validatePasswordLength(userAccount, this.errors);

		Mockito.verify(this.errors).rejectValue(UserAccountFields.PASSWORD, UserAccountValidator.PASSWORD_MINIMUM_LENGTH_MESSAGE);
	}

	@Test
	public void testValidatePasswordLength_ValidLength() {

		this.validator.setPasswordMinimumLength(6);

		final UserAccountModel userAccount1 = new UserAccountModel();
		userAccount1.setPassword(RandomStringUtils.randomAlphanumeric(6));

		this.validator.validatePasswordLength(userAccount1, this.errors);

		Mockito.verify(this.errors, Mockito.times(0))
			.rejectValue(UserAccountFields.PASSWORD, UserAccountValidator.PASSWORD_MINIMUM_LENGTH_MESSAGE);

		final UserAccountModel userAccount2 = new UserAccountModel();
		userAccount2.setPassword(RandomStringUtils.randomAlphanumeric(7));

		this.validator.validatePasswordLength(userAccount2, this.errors);

		Mockito.verify(this.errors, Mockito.times(0))
			.rejectValue(UserAccountFields.PASSWORD, UserAccountValidator.PASSWORD_MINIMUM_LENGTH_MESSAGE);
	}

	@Test
	public void testValidatePasswordConfirmation_PasswordConfirmationIsMatched() {

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setPassword(RandomStringUtils.randomAlphanumeric(5));
		userAccount.setPasswordConfirmation(RandomStringUtils.randomAlphanumeric(5));

		this.validator.validatePasswordConfirmation(userAccount, this.errors);

		Mockito.verify(this.errors)
			.rejectValue(UserAccountFields.PASSWORD_CONFIRMATION, UserAccountValidator.PASSWORD_CONFIRMATION_DOES_NOT_MATCH);
	}

	@Test
	public void testValidatePasswordConfirmation_PasswordConfirmationDoesNotMatch() {

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setPassword(RandomStringUtils.randomAlphanumeric(5));
		userAccount.setPasswordConfirmation(userAccount.getPassword());

		this.validator.validatePasswordConfirmation(userAccount, this.errors);

		Mockito.verify(this.errors, Mockito.times(0))
			.rejectValue(UserAccountFields.PASSWORD_CONFIRMATION, UserAccountValidator.PASSWORD_CONFIRMATION_DOES_NOT_MATCH);
	}
}
