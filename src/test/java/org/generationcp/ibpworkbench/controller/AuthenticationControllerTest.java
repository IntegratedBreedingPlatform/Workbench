package org.generationcp.ibpworkbench.controller;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.ibpworkbench.validator.ForgotPasswordAccountValidator;
import org.generationcp.ibpworkbench.validator.UserAccountValidator;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationControllerTest {

	@Mock
	private UserAccountValidator userAccountValidator;

	@Mock
	private ForgotPasswordAccountValidator forgotPasswordAccountValidator;

	@Mock
	private WorkbenchUserService workbenchUserService;

	@Mock
	private UserAccountModel userAccountModel;

	@Mock
	private MessageSource messageSource;

	@Mock
	private BindingResult result;

	@InjectMocks
	private AuthenticationController controller;

	@Test
	public void testGetLoginPage() throws Exception {
		assertEquals("should return the login url", "login", controller.getLoginPage());
	}

	@Test
	public void testSaveUserAccount() throws Exception {
		when(result.hasErrors()).thenReturn(false);

		ResponseEntity<Map<String, Object>> out = controller
				.saveUserAccount(userAccountModel, result);

		assertTrue("ok status", out.getStatusCode().equals(HttpStatus.OK));

		assertTrue("success = true", (Boolean) out.getBody().get("success"));

	}

	@Test
	public void testSaveUserAccountWithErrors() throws Exception {
		when(result.hasErrors()).thenReturn(true);
		when(result.getFieldErrors()).thenReturn(Collections.EMPTY_LIST);

		ResponseEntity<Map<String, Object>> out = controller
				.saveUserAccount(userAccountModel, result);

		assertTrue("should output bad request status",
				out.getStatusCode().equals(HttpStatus.BAD_REQUEST));
		assertFalse("success = false", (Boolean) out.getBody().get("success"));

	}

	@Test
	public void testSaveUserAccountWithDatabaseError() throws Exception {
		when(result.hasErrors()).thenReturn(false);

		doThrow(MiddlewareQueryException.class).when(workbenchUserService).saveUserAccount(
				userAccountModel);

		ResponseEntity<Map<String, Object>> out = controller
				.saveUserAccount(userAccountModel, result);

		assertTrue("should output bad request status",
				out.getStatusCode().equals(HttpStatus.BAD_REQUEST));
		assertFalse("success = false", (Boolean) out.getBody().get("success"));

	}

	@Test
	public void testValidateLogin() throws Exception {
		when(workbenchUserService.isValidUserLogin(userAccountModel)).thenReturn(true);

		ResponseEntity<Map<String, Object>> out = controller
				.validateLogin(userAccountModel, result);

		assertTrue("ok status", out.getStatusCode().equals(HttpStatus.OK));

		assertTrue("success = true", (Boolean) out.getBody().get("success"));
	}

	@Test
	public void testForgotPassword() throws Exception {
		when(result.hasErrors()).thenReturn(false);

		ResponseEntity<Map<String, Object>> out = controller
				.forgotPassword(userAccountModel, result);

		assertTrue("ok status", out.getStatusCode().equals(HttpStatus.OK));

		assertTrue("success = true", (Boolean) out.getBody().get("success"));
	}

	@Test
	public void testValidateLoginAndForgotPasswordWithErrors() throws Exception {
		when(result.hasErrors()).thenReturn(true);
		when(result.getFieldErrors()).thenReturn(Collections.EMPTY_LIST);

		when(messageSource.getMessage(anyString(), any(Object[].class), any(Locale.class)))
				.thenReturn(
						anyString());

		ResponseEntity<Map<String, Object>> out = controller
				.validateLogin(userAccountModel, result);

		ResponseEntity<Map<String, Object>> out2 = controller
				.forgotPassword(userAccountModel, result);

		assertTrue("should output bad request status",
				out.getStatusCode().equals(HttpStatus.BAD_REQUEST));
		assertFalse("success = false", (Boolean) out.getBody().get("success"));

		assertTrue("should output bad request status",
				out2.getStatusCode().equals(HttpStatus.BAD_REQUEST));
		assertFalse("success = false", (Boolean) out.getBody().get("success"));

	}
}