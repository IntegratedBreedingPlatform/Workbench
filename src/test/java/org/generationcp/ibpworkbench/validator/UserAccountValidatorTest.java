
package org.generationcp.ibpworkbench.validator;

import org.generationcp.commons.security.Role;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
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
		userAccount.setRole("ADMIN");
		userAccount.setUsername("username");
		userAccount.setPassword("password");
		userAccount.setPasswordConfirmation("password");

		Mockito.when(this.workbenchDataManager.isUsernameExists(userAccount.getUsername())).thenReturn(false);
		Mockito.when(this.workbenchDataManager.isPersonExists(userAccount.getFirstName(), userAccount.getLastName())).thenReturn(false);

		partialValidator.validate(userAccount, this.errors);

		Mockito.verify(partialValidator, Mockito.times(3)).validateFieldLength(Matchers.any(Errors.class), Matchers.anyString(),
				Matchers.anyString(), Matchers.anyString(), Matchers.anyInt());

		Mockito.verify(partialValidator).validateEmailFormat(this.errors, userAccount);

		Mockito.verify(partialValidator).validateFieldsEmptyOrWhitespace(this.errors);

		Mockito.verify(partialValidator).validatePasswordConfirmationIfEquals(this.errors, userAccount);

		Mockito.verify(partialValidator).validateUsernameIfExists(this.errors, userAccount);

		Mockito.verify(partialValidator).validatePersonEmailIfExists(this.errors, userAccount);

		Mockito.verify(partialValidator).validateUserRole(this.errors, userAccount);

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

		Mockito.verify(this.errors, Mockito.never()).rejectValue(Matchers.anyString(), Matchers.anyString());
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

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture(), Matchers.any(String[].class), Matchers.anyString());
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

		Mockito.verify(this.errors, Mockito.never()).rejectValue(Matchers.anyString(), Matchers.anyString());
	}

	@Test
	public void testValidateUserRole() throws Exception {
		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setRole(Role.ADMIN.name());

		UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateUserRole(this.errors, userAccount);

		Mockito.verify(this.errors, Mockito.never()).rejectValue(Matchers.anyString(), Matchers.anyString());
	}

	@Test
	public void testValidateUserRoleNotExists() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setRole("NON_EXISTING_ROLE");

		UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateUserRole(this.errors, userAccount);

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture());

		Assert.assertEquals("error should output role field", UserAccountFields.ROLE, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.SIGNUP_FIELD_INVALID_ROLE, arg2.getValue());
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

		Mockito.verify(this.errors, Mockito.never()).rejectValue(Matchers.anyString(), Matchers.anyString());
	}

	@Test
	public void testValidateFieldLength() throws Exception {
		UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateFieldLength(this.errors, "invalid length", "field.prop", "Field Name", 5);

		Mockito.verify(this.errors).rejectValue(Matchers.anyString(), Matchers.anyString(), Matchers.any(String[].class),
				Matchers.anyString());
	}

	@Test
	public void testValidateFieldLengthValidValue() throws Exception {
		UserAccountValidator partialValidator = Mockito.spy(this.validator);
		partialValidator.validateFieldLength(this.errors, "valid length", "field.prop", "Field Name", 30);

		Mockito.verify(this.errors, Mockito.never()).rejectValue(Matchers.anyString(), Matchers.anyString(), Matchers.any(String[].class),
				Matchers.anyString());
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

		Mockito.verify(this.errors, Mockito.never()).rejectValue(Matchers.anyString(), Matchers.anyString());
	}
}
