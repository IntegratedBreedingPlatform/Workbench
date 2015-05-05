package org.generationcp.ibpworkbench.controller;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.security.ForgotPasswordEmailService;
import org.generationcp.ibpworkbench.security.InvalidResetTokenException;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.ibpworkbench.validator.ForgotPasswordAccountValidator;
import org.generationcp.ibpworkbench.validator.UserAccountValidator;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.mail.MessagingException;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationControllerTest {

	private static final String TEST_RESET_PASSWORD_TOKEN = "bla_bla_bla";
	@Mock
	private UserAccountValidator userAccountValidator;

	@Mock
	private ForgotPasswordAccountValidator forgotPasswordAccountValidator;

	@Mock
	private ForgotPasswordEmailService forgotPasswordEmailService;

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
				.validateForgotPasswordForm(userAccountModel, result);

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
				.validateForgotPasswordForm(userAccountModel, result);

		assertTrue("should output bad request status",
				out.getStatusCode().equals(HttpStatus.BAD_REQUEST));
		assertFalse("success = false", (Boolean) out.getBody().get("success"));

		assertTrue("should output bad request status",
				out2.getStatusCode().equals(HttpStatus.BAD_REQUEST));
		assertFalse("success = false", (Boolean) out.getBody().get("success"));

	}

	@Test
	public void testGetCreateNewPasswordPage() throws Exception {
		// assume everything is well
	    User user = new User();
		Model model = mock(Model.class);
		when(forgotPasswordEmailService.validateResetToken(TEST_RESET_PASSWORD_TOKEN)).thenReturn(
				user);

		String page = controller.getCreateNewPasswordPage(TEST_RESET_PASSWORD_TOKEN,model);

		verify(model, times(1)).addAttribute("user", user);
		assertEquals("should return new-password page", "new-password", page);
	}

	@Test
	public void testGetCreateNewPasswordPageInvalidToken() throws Exception {
		Model model = mock(Model.class);
		when(forgotPasswordEmailService.validateResetToken(TEST_RESET_PASSWORD_TOKEN)).thenThrow(
				new InvalidResetTokenException());

		String page = controller.getCreateNewPasswordPage(TEST_RESET_PASSWORD_TOKEN,model);

		assertEquals("should redirect to login page","redirect:" + AuthenticationController.URL,page);
	}

	@Test
	public void testDoSendResetPasswordRequestEmail() throws Exception {
		// default success scenario
		when(workbenchUserService.getUserByUserName(anyString())).thenReturn(mock(User.class));
		doNothing().when(forgotPasswordEmailService).doRequestPasswordReset(any(User.class));

		ResponseEntity<Map<String,Object>> result = controller.doSendResetPasswordRequestEmail(
				mock(UserAccountModel.class));

		assertEquals("no http errors",HttpStatus.OK,result.getStatusCode());
		assertEquals("is successful",Boolean.TRUE,result.getBody().get(AuthenticationController.SUCCESS));

	}

	@Test
	public void testDoSendResetPasswordRequestEmailWithErrors() throws Exception {
		// houston we have a problem
		when(workbenchUserService.getUserByUserName(anyString())).thenReturn(mock(User.class));
		doThrow(new MessagingException("i cant send me message :(")).when(forgotPasswordEmailService).doRequestPasswordReset(
				any(User.class));

		ResponseEntity<Map<String,Object>> result = controller.doSendResetPasswordRequestEmail(mock(UserAccountModel.class));

		assertEquals("no http errors",HttpStatus.BAD_REQUEST,result.getStatusCode());
		assertEquals("is successful",Boolean.FALSE,result.getBody().get(AuthenticationController.SUCCESS));
	}

	@Test
	public void testDoResetPassword() throws Exception {
		UserAccountModel userAccountModel = new UserAccountModel();

		boolean result = controller.doResetPassword(userAccountModel);

		verify(workbenchUserService,times(1)).updateUserPassword(userAccountModel);
		verify(forgotPasswordEmailService, times(1)).deleteToken(userAccountModel);

		assertTrue("success!",result);
	}

	@Test
	public void testDoResetPasswordGotError() throws Exception {
		UserAccountModel userAccountModel = new UserAccountModel();


		doThrow(new MiddlewareQueryException("oops i did it again")).when(forgotPasswordEmailService).deleteToken(userAccountModel);

		boolean result = controller.doResetPassword(userAccountModel);

		verify(workbenchUserService,times(1)).updateUserPassword(userAccountModel);
		verify(forgotPasswordEmailService, times(1)).deleteToken(userAccountModel);

		assertFalse("fail!", result);
	}
}