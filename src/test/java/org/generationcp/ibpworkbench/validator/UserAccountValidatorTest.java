
package org.generationcp.ibpworkbench.validator;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.workbench.Role;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

@RunWith(MockitoJUnitRunner.class)
public class UserAccountValidatorTest {

	@Mock
	private Errors errors;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private WorkbenchUserService workbenchUserService;

	@InjectMocks
	private UserAccountValidator validator;

	@Test
	public void testSupports() throws Exception {
		Assert.assertTrue("UserAccountModel.class is supported by validator", this.validator.supports(UserAccountModel.class));
	}

	@Test
	public void testValidate() throws Exception {
		UserAccountValidator partialValidator = Mockito.spy(this.validator);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setFirstName("firstName");
		userAccount.setLastName("lastName");
		userAccount.setEmail("email@email.com");
		// Role ID 1 = ADMIN
		userAccount.setRole(new Role(1));
		userAccount.setUsername("username");
		userAccount.setPassword("password");
		userAccount.setPasswordConfirmation("password");

		Mockito.when(this.workbenchDataManager.isUsernameExists(userAccount.getUsername())).thenReturn(false);

		partialValidator.validate(userAccount, this.errors);

		Mockito.verify(partialValidator, Mockito.times(3)).validateFieldLength(ArgumentMatchers.any(Errors.class), ArgumentMatchers.anyString(),
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
	public void testValidatePasswordConfirmationIfEquals() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setPassword("password");
		userAccount.setPasswordConfirmation("passwordNotEquals");

		UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validatePasswordConfirmationIfEquals(this.errors, userAccount);

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture());
		Assert.assertEquals("error should output password confirmation field", UserAccountFields.PASSWORD_CONFIRMATION, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.SIGNUP_FIELD_PASSWORD_NOT_MATCH, arg2.getValue());

	}

	@Test
	public void testValidatePasswordConfirmationIfEqualsNoError() throws Exception {
		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setPassword("password");
		userAccount.setPasswordConfirmation("password");

		UserAccountValidator partialValidator = Mockito.spy(this.validator);

		partialValidator.validatePasswordConfirmationIfEquals(this.errors, userAccount);

		Mockito.verify(this.errors, Mockito.never()).rejectValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
	}

	@Test
	public void testValidateUsernameIfExists() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("username");

		Mockito.when(this.workbenchDataManager.isUsernameExists(userAccount.getUsername())).thenReturn(true);

		UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateUsernameIfExists(this.errors, userAccount);

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture(), ArgumentMatchers.any(String[].class), ArgumentMatchers.<String>isNull());
		Assert.assertEquals("error should output username field", UserAccountFields.USERNAME, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.SIGNUP_FIELD_USERNAME_EXISTS, arg2.getValue());
	}

	@Test
	public void testValidateUsernameIfExistsDatabaseError() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("username");

		Mockito.when(this.workbenchDataManager.isUsernameExists(userAccount.getUsername())).thenThrow(MiddlewareQueryException.class);

		UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateUsernameIfExists(this.errors, userAccount);

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture());
		Assert.assertEquals("error should output username field", UserAccountFields.USERNAME, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.DATABASE_ERROR, arg2.getValue());
	}

	@Test
	public void testValidateUsernameIfExistsNoError() throws Exception {
		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("username");

		Mockito.when(this.workbenchDataManager.isUsernameExists(userAccount.getUsername())).thenReturn(false);

		UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateUsernameIfExists(this.errors, userAccount);

		Mockito.verify(this.errors, Mockito.never()).rejectValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
	}

	@Test
	public void testValidateEmailFormat() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setEmail("invalidEmail.com");

		UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateEmailFormat(this.errors, userAccount);

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture());

		Assert.assertEquals("error should output email field", UserAccountFields.EMAIL, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.SIGNUP_FIELD_INVALID_EMAIL_FORMAT, arg2.getValue());
	}

	@Test
	public void testValidateEmailFormatValid() throws Exception {
		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setEmail("validemail123@outlook.com");

		UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateEmailFormat(this.errors, userAccount);

		Mockito.verify(this.errors, Mockito.never()).rejectValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
	}

	@Test
	public void testValidateFieldLength() throws Exception {
		UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateFieldLength(this.errors, "invalid length", "field.prop", "Field Name", 5);

		Mockito.verify(this.errors).rejectValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class),
				ArgumentMatchers.<String>isNull());
	}

	@Test
	public void testValidateFieldLengthValidValue() throws Exception {
		UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateFieldLength(this.errors, "valid length", "field.prop", "Field Name", 30);

		Mockito.verify(this.errors, Mockito.never()).rejectValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class),
				ArgumentMatchers.anyString());
	}

	@Test
	public void testValidateEmailIfExists() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setEmail("email@email.com");

		Mockito.when(this.workbenchDataManager.isPersonWithEmailExists(userAccount.getEmail())).thenReturn(true);

		UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validatePersonEmailIfExists(this.errors, userAccount);

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture());
		Assert.assertEquals("error should output email field", UserAccountFields.EMAIL, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.SIGNUP_FIELD_EMAIL_EXISTS, arg2.getValue());
	}

	@Test
	public void testValidateEmailIfExistsDatabaseError() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setEmail("email@email.com");

		Mockito.when(this.workbenchDataManager.isPersonWithEmailExists(userAccount.getEmail())).thenThrow(MiddlewareQueryException.class);

		UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validatePersonEmailIfExists(this.errors, userAccount);

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture());
		Assert.assertEquals("error should output email field", UserAccountFields.EMAIL, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.DATABASE_ERROR, arg2.getValue());
	}

	@Test
	public void testValidateEmailIfExistsNoError() throws Exception {
		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("email@email.com");

		Mockito.when(this.workbenchDataManager.isPersonWithEmailExists(userAccount.getEmail())).thenReturn(false);

		UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validatePersonEmailIfExists(this.errors, userAccount);

		Mockito.verify(this.errors, Mockito.never()).rejectValue(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
	}
}
