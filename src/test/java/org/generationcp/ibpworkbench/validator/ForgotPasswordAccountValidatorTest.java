package org.generationcp.ibpworkbench.validator;

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

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ForgotPasswordAccountValidatorTest {

	@Mock
	private Errors errors;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@InjectMocks
	private ForgotPasswordAccountValidator validator;

	@Test
	public void testValidate() throws Exception {
		ForgotPasswordAccountValidator validatorDUT = spy(validator);

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

		validatorDUT.validate(userAccount, errors);

		verify(validatorDUT).validateEmailFormat(errors, userAccount);

		verify(validatorDUT).validatePasswordConfirmationIfEquals(errors, userAccount);

		verify(validatorDUT).validateUsernameAndEmailIfNotExists(errors, userAccount);

	}

	@Test
	public void testValidateUsernameAndEmailIfNotExists() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("username");

		when(workbenchDataManager.isPersonWithUsernameAndEmailExists(userAccount.getUsername(),
				userAccount.getEmail())).thenReturn(false);

		ForgotPasswordAccountValidator validatorDUT = spy(validator);
		validatorDUT.validateUsernameAndEmailIfNotExists(errors, userAccount);

		verify(errors)
				.rejectValue(arg1.capture(), arg2.capture(), any(String[].class), anyString());
		assertEquals("error should output username field",
				UserAccountFields.USERNAME, arg1.getValue());
		assertEquals("show correct error code",
				ForgotPasswordAccountValidator.SIGNUP_FIELD_USERNAME_EMAIL_COMBO_NOT_EXISTS,
				arg2.getValue());
	}

	@Test
	public void testValidateUsernameAndEmailIfNotExistsDatabaseError() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("username");

		when(workbenchDataManager
				.isPersonWithUsernameAndEmailExists(userAccount.getUsername(),userAccount.getEmail())).thenThrow(
				MiddlewareQueryException.class);

		ForgotPasswordAccountValidator validatorDUT = spy(validator);
		validatorDUT.validateUsernameAndEmailIfNotExists(errors, userAccount);

		verify(errors).rejectValue(arg1.capture(), arg2.capture());
		assertEquals("error should output username field",
				UserAccountFields.USERNAME, arg1.getValue());
		assertEquals("show correct error code",
				UserAccountValidator.DATABASE_ERROR,
				arg2.getValue());
	}

	@Test
	public void testValidateUsernameAndEmailIfNotExistsNoError() throws Exception {
		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("username");

		when(workbenchDataManager
				.isPersonWithUsernameAndEmailExists(userAccount.getUsername(),userAccount.getEmail())).thenReturn(true);

		ForgotPasswordAccountValidator validatorDUT = spy(validator);
		validatorDUT.validateUsernameAndEmailIfNotExists(errors, userAccount);

		verify(errors, never()).rejectValue(anyString(), anyString());
	}
}