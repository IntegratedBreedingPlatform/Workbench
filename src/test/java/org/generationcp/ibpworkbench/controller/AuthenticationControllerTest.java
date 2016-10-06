
package org.generationcp.ibpworkbench.controller;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.security.InvalidResetTokenException;
import org.generationcp.ibpworkbench.security.WorkbenchEmailSenderService;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.ibpworkbench.validator.ForgotPasswordAccountValidator;
import org.generationcp.ibpworkbench.validator.UserAccountValidator;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationControllerTest {

	private static final String TEST_RESET_PASSWORD_TOKEN = "bla_bla_bla";
	@Mock
	private UserAccountValidator userAccountValidator;

	@Mock
	private ForgotPasswordAccountValidator forgotPasswordAccountValidator;

	@Mock
	private WorkbenchEmailSenderService workbenchEmailSenderService;

	@Mock
	private WorkbenchUserService workbenchUserService;

	@Mock
	private UserAccountModel userAccountModel;

	@Mock
	private MessageSource messageSource;

	@Mock
	private BindingResult result;

	@Mock
	private ApiAuthenticationService apiAuthenticationService;

	@InjectMocks
	private AuthenticationController controller;

	@Before
	public void beforeEachTest() {
		Mockito.when(this.workbenchUserService.isUserActive(Matchers.any(UserAccountModel.class))).thenReturn(true);
	}

	@Test
	public void testGetLoginPage() throws Exception {
		Model model = Mockito.mock(Model.class);
		Assert.assertEquals("should return the login url", "login", this.controller.getLoginPage(model));
	}

	@Test
	public void loginPageIsPopulatedWithEnableAccountCreationParameter() throws Exception {
		Model model = Mockito.mock(Model.class);

		Assert.assertEquals("should return the login url", "login", this.controller.getLoginPage(model));
		Mockito.verify(model).addAttribute("isCreateAccountEnable", false);
	}

	@Test
	public void testSaveUserAccount() throws Exception {
		Mockito.when(this.result.hasErrors()).thenReturn(false);

		ResponseEntity<Map<String, Object>> out = this.controller.saveUserAccount(this.userAccountModel, this.result);

		Assert.assertTrue("ok status", out.getStatusCode().equals(HttpStatus.OK));

		Assert.assertTrue("success = true", (Boolean) out.getBody().get("success"));

	}

	@Test
	public void testSaveUserAccountWithErrors() throws Exception {
		Mockito.when(this.result.hasErrors()).thenReturn(true);
		Mockito.when(this.result.getFieldErrors()).thenReturn(Collections.<FieldError>emptyList());

		ResponseEntity<Map<String, Object>> out = this.controller.saveUserAccount(this.userAccountModel, this.result);

		Assert.assertTrue("should output bad request status", out.getStatusCode().equals(HttpStatus.BAD_REQUEST));
		Assert.assertFalse("success = false", (Boolean) out.getBody().get("success"));

	}

	@Test
	public void testSaveUserAccountWithDatabaseError() throws Exception {
		Mockito.when(this.result.hasErrors()).thenReturn(false);

		Mockito.doThrow(MiddlewareQueryException.class).when(this.workbenchUserService).saveUserAccount(this.userAccountModel);

		ResponseEntity<Map<String, Object>> out = this.controller.saveUserAccount(this.userAccountModel, this.result);

		Assert.assertTrue("should output bad request status", out.getStatusCode().equals(HttpStatus.BAD_REQUEST));
		Assert.assertFalse("success = false", (Boolean) out.getBody().get("success"));

	}

	@Test
	public void testValidateLogin() throws Exception {
		Mockito.when(this.workbenchUserService.isValidUserLogin(this.userAccountModel)).thenReturn(true);

		ResponseEntity<Map<String, Object>> out = this.controller.validateLogin(this.userAccountModel, this.result);

		Assert.assertTrue("ok status", out.getStatusCode().equals(HttpStatus.OK));

		Assert.assertTrue("success = true", (Boolean) out.getBody().get("success"));
	}

	@Test
	public void testForgotPassword() throws Exception {
		Mockito.when(this.result.hasErrors()).thenReturn(false);

		ResponseEntity<Map<String, Object>> out = this.controller.validateForgotPasswordForm(this.userAccountModel, this.result);

		Assert.assertTrue("ok status", out.getStatusCode().equals(HttpStatus.OK));

		Assert.assertTrue("success = true", (Boolean) out.getBody().get("success"));
	}

	@Test
	public void testValidateLoginAndForgotPasswordWithErrors() throws Exception {
		Mockito.when(this.result.hasErrors()).thenReturn(true);
		Mockito.when(this.result.getFieldErrors()).thenReturn(Collections.<FieldError>emptyList());

		Mockito.when(this.messageSource.getMessage(Matchers.anyString(), Matchers.any(Object[].class), Matchers.any(Locale.class)))
		.thenReturn(Matchers.anyString());

		ResponseEntity<Map<String, Object>> out = this.controller.validateLogin(this.userAccountModel, this.result);

		ResponseEntity<Map<String, Object>> out2 = this.controller.validateForgotPasswordForm(this.userAccountModel, this.result);

		Assert.assertTrue("should output bad request status", out.getStatusCode().equals(HttpStatus.BAD_REQUEST));
		Assert.assertFalse("success = false", (Boolean) out.getBody().get("success"));

		Assert.assertTrue("should output bad request status", out2.getStatusCode().equals(HttpStatus.BAD_REQUEST));
		Assert.assertFalse("success = false", (Boolean) out.getBody().get("success"));

	}

	@Test
	public void testGetCreateNewPasswordPage() throws Exception {
		// assume everything is well
		User user = new User();
		Model model = Mockito.mock(Model.class);
		Mockito.when(this.workbenchEmailSenderService.validateResetToken(AuthenticationControllerTest.TEST_RESET_PASSWORD_TOKEN))
		.thenReturn(user);

		String page = this.controller.getCreateNewPasswordPage(AuthenticationControllerTest.TEST_RESET_PASSWORD_TOKEN, model);

		Mockito.verify(model, Mockito.times(1)).addAttribute("user", user);
		Assert.assertEquals("should return new-password page", "new-password", page);
	}

	@Test
	public void testGetCreateNewPasswordPageInvalidToken() throws Exception {
		Model model = Mockito.mock(Model.class);
		Mockito.when(this.workbenchEmailSenderService.validateResetToken(AuthenticationControllerTest.TEST_RESET_PASSWORD_TOKEN)).thenThrow(
				new InvalidResetTokenException());

		String page = this.controller.getCreateNewPasswordPage(AuthenticationControllerTest.TEST_RESET_PASSWORD_TOKEN, model);

		Assert.assertEquals("should redirect to login page", "redirect:" + AuthenticationController.URL, page);
	}

	@Test
	public void testDoSendResetPasswordRequestEmail() throws Exception {
		// default success scenario
		Mockito.when(this.workbenchUserService.getUserByUserName(Matchers.anyString())).thenReturn(Mockito.mock(User.class));
		Mockito.doNothing().when(this.workbenchEmailSenderService).doRequestPasswordReset(Matchers.any(User.class));

		ResponseEntity<Map<String, Object>> result = this.controller.doSendResetPasswordRequestEmail(Mockito.mock(UserAccountModel.class));

		Assert.assertEquals("no http errors", HttpStatus.OK, result.getStatusCode());
		Assert.assertEquals("is successful", Boolean.TRUE, result.getBody().get(AuthenticationController.SUCCESS));

	}

	@Test
	public void testDoSendResetPasswordRequestEmailWithErrors() throws Exception {
		// houston we have a problem
		Mockito.when(this.workbenchUserService.getUserByUserName(Matchers.anyString())).thenReturn(Mockito.mock(User.class));
		Mockito.doThrow(new MessagingException("i cant send me message :(")).when(this.workbenchEmailSenderService)
		.doRequestPasswordReset(Matchers.any(User.class));

		ResponseEntity<Map<String, Object>> result = this.controller.doSendResetPasswordRequestEmail(Mockito.mock(UserAccountModel.class));

		Assert.assertEquals("no http errors", HttpStatus.BAD_REQUEST, result.getStatusCode());
		Assert.assertEquals("is successful", Boolean.FALSE, result.getBody().get(AuthenticationController.SUCCESS));
	}

	@Test
	public void testDoResetPassword() throws Exception {
		UserAccountModel userAccountModel = new UserAccountModel();

		boolean result = this.controller.doResetPassword(userAccountModel);

		Mockito.verify(this.workbenchUserService, Mockito.times(1)).updateUserPassword(userAccountModel.getUsername(), userAccountModel.getPassword());
		Mockito.verify(this.workbenchEmailSenderService, Mockito.times(1)).deleteToken(userAccountModel);

		Assert.assertTrue("success!", result);
	}

	@Test
	public void testDoResetPasswordGotError() throws Exception {
		UserAccountModel userAccountModel = new UserAccountModel();

		Mockito.doThrow(new MiddlewareQueryException("oops i did it again")).when(this.workbenchEmailSenderService)
		.deleteToken(userAccountModel);

		boolean result = this.controller.doResetPassword(userAccountModel);

		Mockito.verify(this.workbenchUserService, Mockito.times(1)).updateUserPassword(userAccountModel.getUsername(), userAccountModel.getPassword());
		Mockito.verify(this.workbenchEmailSenderService, Mockito.times(1)).deleteToken(userAccountModel);

		Assert.assertFalse("fail!", result);
	}

	@Test
	public void testTokenIsReturnedForSuccessfulAuthentication() {

		UserAccountModel testUserAccountModel = new UserAccountModel();
		testUserAccountModel.setUsername("naymesh");
		testUserAccountModel.setPassword("b");
		Mockito.when(this.workbenchUserService.isValidUserLogin(testUserAccountModel)).thenReturn(true);
		final Token testToken = new Token("naymesh:1447734506586:3a7e599e28efc35a2d53e62715ffd3cb", 1447734506586L);
		Mockito.when(
				this.apiAuthenticationService.authenticate(Mockito.eq(testUserAccountModel.getUsername()),
						Mockito.eq(testUserAccountModel.getPassword()))).thenReturn(testToken);

		ResponseEntity<Map<String, Object>> out = this.controller.validateLogin(testUserAccountModel, this.result);
		Assert.assertEquals(testToken.getToken(), out.getBody().get("token"));
		Assert.assertEquals(testToken.getExpires(), out.getBody().get("expires"));
	}

	@Test
	public void testTokenIsNotReturnedWhenThereIsFailureInApiAuthentication() {

		UserAccountModel testUserAccountModel = new UserAccountModel();
		testUserAccountModel.setUsername("naymesh");
		testUserAccountModel.setPassword("b");
		Mockito.when(this.workbenchUserService.isValidUserLogin(testUserAccountModel)).thenReturn(true);

		// Case when ApiAuthenticationService will return null token
		Mockito.when(
				this.apiAuthenticationService.authenticate(Mockito.eq(testUserAccountModel.getUsername()),
						Mockito.eq(testUserAccountModel.getPassword()))).thenReturn(null);

		ResponseEntity<Map<String, Object>> out = this.controller.validateLogin(testUserAccountModel, this.result);
		Mockito.verify(this.apiAuthenticationService).authenticate(Mockito.anyString(), Mockito.anyString());
		Assert.assertNull(out.getBody().get("token"));
		Assert.assertNull(out.getBody().get("expires"));
	}

	@Test
	public void testTokenIsNotReturnedForUnSuccessfulAuthentication() {

		UserAccountModel testUserAccountModel = new UserAccountModel();
		Mockito.when(this.workbenchUserService.isValidUserLogin(testUserAccountModel)).thenReturn(false);
		ResponseEntity<Map<String, Object>> out = this.controller.validateLogin(testUserAccountModel, this.result);
		Mockito.verify(this.apiAuthenticationService, Mockito.never()).authenticate(Mockito.anyString(), Mockito.anyString());
		Assert.assertNull(out.getBody().get("token"));
		Assert.assertNull(out.getBody().get("expires"));
	}
	
	@Test
	public void testsendResetPasswordEmail() throws Exception {
		// default success scenario
		Mockito.when(this.workbenchUserService.getUserByUserName(Matchers.anyString()))
				.thenReturn(Mockito.mock(User.class));
		Mockito.doNothing().when(this.workbenchEmailSenderService).doRequestPasswordReset(Matchers.any(User.class));

		ResponseEntity<Map<String, Object>> result = this.controller
				.sendResetPasswordEmail(Mockito.mock(UserAccountModel.class).getUsername());

		Assert.assertEquals("no http errors", HttpStatus.OK, result.getStatusCode());
		Assert.assertEquals("is successful", Boolean.TRUE, result.getBody().get(AuthenticationController.SUCCESS));

	}

	@Test
	public void testsendResetPasswordEmailWithErrors() throws Exception {
		// houston we have a problem
		Mockito.when(this.workbenchUserService.getUserByUserName(Matchers.anyString()))
				.thenReturn(Mockito.mock(User.class));
		Mockito.doThrow(new MessagingException("i cant send me message :(")).when(this.workbenchEmailSenderService)
				.doRequestPasswordReset(Matchers.any(User.class));

		ResponseEntity<Map<String, Object>> result = this.controller
				.sendResetPasswordEmail(Mockito.mock(UserAccountModel.class).getUsername());

		Assert.assertEquals("no http errors", HttpStatus.BAD_REQUEST, result.getStatusCode());
		Assert.assertEquals("is successful", Boolean.FALSE, result.getBody().get(AuthenticationController.SUCCESS));
	}
}
