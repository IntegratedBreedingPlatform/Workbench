package org.generationcp.ibpworkbench.validator;

import org.generationcp.commons.security.Role;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

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
		assertTrue("UserAccountModel.class is supported by validator", validator.supports(
				UserAccountModel.class));
	}

	@Test
	public void testValidate() throws Exception {
		UserAccountValidator partialValidator = spy(validator);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setFirstName("firstName");
		userAccount.setLastName("lastName");
		userAccount.setEmail("email@email.com");
		userAccount.setRole("ADMIN");
		userAccount.setUsername("username");
		userAccount.setPassword("password");
		userAccount.setPasswordConfirmation("password");

		when(workbenchDataManager.isUsernameExists(userAccount.getUsername())).thenReturn(false);
		when(workbenchDataManager
				.isPersonExists(userAccount.getFirstName(), userAccount.getLastName())).thenReturn(
				false);

		partialValidator.validate(userAccount, errors);

		verify(partialValidator).validateFieldsEmptyOrWhitespace(errors);

		verify(partialValidator).validatePasswordConfirmationIfEquals(errors, userAccount);

		verify(partialValidator).validateUsernameIfExists(errors, userAccount);

		verify(partialValidator).validatePersonIfExists(errors, userAccount);

		verify(partialValidator).validateUserRole(errors, userAccount);

	}


	@Test
	public void testValidatePasswordConfirmationIfEquals() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setPassword("password");
		userAccount.setPasswordConfirmation("passwordNotEquals");

		UserAccountValidator partialValidator = spy(validator);
		partialValidator.validatePasswordConfirmationIfEquals(errors, userAccount);

		verify(errors).rejectValue(arg1.capture(), arg2.capture());
		assertEquals("error should output password confirmation field",
				UserAccountValidator.UserAccountFields.PASSWORD_CONFIRMATION, arg1.getValue());
		assertEquals("show correct error code",
				UserAccountValidator.SIGNUP_FIELD_PASSWORD_NOT_MATCH,
				arg2.getValue());

	}

	@Test
	public void testValidatePasswordConfirmationIfEqualsNoError() throws Exception {
		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setPassword("password");
		userAccount.setPasswordConfirmation("password");

		UserAccountValidator partialValidator = spy(validator);

		partialValidator.validatePasswordConfirmationIfEquals(errors, userAccount);

		verify(errors,never()).rejectValue(anyString(),anyString());
	}

	@Test
	public void testValidateUsernameIfExists() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("username");

		when(workbenchDataManager.isUsernameExists(userAccount.getUsername())).thenReturn(true);

		UserAccountValidator partialValidator = spy(validator);
		partialValidator.validateUsernameIfExists(errors, userAccount);

		verify(errors).rejectValue(arg1.capture(),arg2.capture(),any(String[].class),anyString());
		assertEquals("error should output username field",
				UserAccountValidator.UserAccountFields.USERNAME, arg1.getValue());
		assertEquals("show correct error code",
				UserAccountValidator.SIGNUP_FIELD_USERNAME_EXISTS,
				arg2.getValue());
	}

	@Test
	public void testValidateUsernameIfExistsDatabaseError() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("username");

		when(workbenchDataManager.isUsernameExists(userAccount.getUsername())).thenThrow(
				MiddlewareQueryException.class);

		UserAccountValidator partialValidator = spy(validator);
		partialValidator.validateUsernameIfExists(errors, userAccount);

		verify(errors).rejectValue(arg1.capture(),arg2.capture());
		assertEquals("error should output username field",
				UserAccountValidator.UserAccountFields.USERNAME, arg1.getValue());
		assertEquals("show correct error code",
				UserAccountValidator.DATABASE_ERROR,
				arg2.getValue());
	}

	@Test
	public void testValidateUsernameIfExistsNoError() throws Exception {
		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("username");

		when(workbenchDataManager.isUsernameExists(userAccount.getUsername())).thenReturn(false);

		UserAccountValidator partialValidator = spy(validator);
		partialValidator.validateUsernameIfExists(errors, userAccount);

		verify(errors,never()).rejectValue(anyString(),anyString());
	}

	@Test
	public void testValidatePersonIfExists() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setFirstName("firstName");
		userAccount.setLastName("lastName");

		when(workbenchDataManager.isPersonExists(userAccount.getFirstName(),userAccount.getLastName())).thenReturn(true);

		UserAccountValidator partialValidator = spy(validator);
		partialValidator.validatePersonIfExists(errors, userAccount);

		verify(errors).rejectValue(arg1.capture(),arg2.capture(),any(String[].class),
		anyString());
		assertEquals("error should output first_name field",
				UserAccountValidator.UserAccountFields.FIRST_NAME, arg1.getValue());
		assertEquals("show correct error code",
				UserAccountValidator.SIGNUP_FIELD_PERSON_EXISTS,
				arg2.getValue());
	}

	@Test
	public void testValidatePersonIfExistsDatabaseError() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setFirstName("firstName");
		userAccount.setLastName("lastName");

		when(workbenchDataManager.isPersonExists(userAccount.getFirstName(),userAccount.getLastName())).thenThrow(MiddlewareQueryException.class);

		UserAccountValidator partialValidator = spy(validator);
		partialValidator.validatePersonIfExists(errors, userAccount);

		verify(errors).rejectValue(arg1.capture(),arg2.capture());
		assertEquals("error should output first_name field",
				UserAccountValidator.UserAccountFields.FIRST_NAME, arg1.getValue());
		assertEquals("show correct error code",
				UserAccountValidator.DATABASE_ERROR,
				arg2.getValue());
	}

	@Test
	public void testValidatePersonIfExistsNoError() throws Exception {
		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setFirstName("firstName");
		userAccount.setLastName("lastName");

		when(workbenchDataManager.isPersonExists(userAccount.getFirstName(),userAccount.getLastName())).thenReturn(false);

		UserAccountValidator partialValidator = spy(validator);
		partialValidator.validatePersonIfExists(errors, userAccount);

		verify(errors,never()).rejectValue(anyString(),anyString());
	}

	@Test
	public void testValidateUserRole() throws Exception {
		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setRole(Role.ADMIN.name());

		UserAccountValidator partialValidator = spy(validator);
		partialValidator.validateUserRole(errors, userAccount);

		verify(errors,never()).rejectValue(anyString(),anyString());
	}

	@Test
	public void testValidateUserRoleNotExists() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);


		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setRole("NON_EXISTING_ROLE");

		UserAccountValidator partialValidator = spy(validator);
		partialValidator.validateUserRole(errors, userAccount);

		verify(errors).rejectValue(arg1.capture(),arg2.capture());

		assertEquals("error should output role field",
				UserAccountValidator.UserAccountFields.ROLE, arg1.getValue());
		assertEquals("show correct error code",
				UserAccountValidator.SIGNUP_FIELD_INVALID_ROLE,
				arg2.getValue());
	}
}