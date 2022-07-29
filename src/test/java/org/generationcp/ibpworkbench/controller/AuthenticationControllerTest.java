package org.generationcp.ibpworkbench.controller;

import org.apache.commons.lang.math.RandomUtils;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.security.InvalidResetTokenException;
import org.generationcp.ibpworkbench.security.WorkbenchEmailSenderService;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.ibpworkbench.validator.ForgotPasswordAccountValidator;
import org.generationcp.ibpworkbench.validator.UserAccountValidator;
import org.generationcp.middleware.api.role.RoleService;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.RoleSearchDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

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

	@Mock
	private ServletContext servletContext;

	@Mock
	private Properties workbenchProperties;

	@InjectMocks
	private AuthenticationController controller;

	@Mock
	private RoleService roleService;

	private List<Role> roles;
	private Role selectedRole;

	@Before
	public void setup() {
		this.createTestRoles();
		Mockito.doReturn(this.selectedRole.getId()).when(this.userAccountModel).getRoleId();
		Mockito.doReturn(this.roles).when(this.roleService).getRoles(new RoleSearchDto(Boolean.TRUE, null, null));
	}

	@Test
	public void testIntialize() {
		this.controller.initialize();
		Mockito.verify(this.roleService).getRoles(new RoleSearchDto(Boolean.TRUE, null, null));
		Assert.assertEquals(this.roles, this.controller.getRoles());
	}

	@Test
	public void testGetLoginPage() throws Exception {
		Model model = Mockito.mock(Model.class);
		Assert.assertEquals("should return the login url", "login", this.controller.getLoginPage(model));
		Mockito.verify(model).addAttribute(Mockito.eq("roles"), Mockito.anyObject());
		assertCommonAttributesWereAddedToModel(model);
	}

	@Test
	public void testFindInstituteLogo() {
		final String path = "src/main/web/src/images/institute";

		Mockito.when(this.servletContext.getResourceAsStream(ArgumentMatchers.anyString())).thenReturn(null);
		Assert.assertThat(this.controller.findInstituteLogo(path), is(""));

		final InputStream inputStream = Mockito.mock(InputStream.class);
		Mockito.when(this.servletContext.getResourceAsStream(ArgumentMatchers.anyString())).thenReturn(inputStream);
		Assert.assertThat(this.controller.findInstituteLogo(path), is(not("")));
	}

	@Test
	public void loginPageIsPopulatedWithEnableAccountCreationParameter() throws Exception {
		Model model = Mockito.mock(Model.class);

		Assert.assertEquals("should return the login url", "login", this.controller.getLoginPage(model));
		Mockito.verify(model).addAttribute("isCreateAccountEnable", false);
	}

	@Test
	public void testSaveUserAccount() throws Exception {
		this.controller.setRoles(this.roles);
		Mockito.when(this.result.hasErrors()).thenReturn(false);

		ResponseEntity<Map<String, Object>> out = this.controller.saveUserAccount(this.userAccountModel, this.result);
		Mockito.verify(this.userAccountModel).setRole(this.selectedRole);
		Assert.assertTrue("ok status", out.getStatusCode().equals(HttpStatus.OK));

		Assert.assertTrue("success = true", (Boolean) out.getBody().get("success"));

	}

	@Test
	public void testSaveUserAccountWithErrors() throws Exception {
		this.controller.setRoles(this.roles);
		Mockito.when(this.result.hasErrors()).thenReturn(true);
		Mockito.when(this.result.getFieldErrors()).thenReturn(Collections.<FieldError>emptyList());

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
		WorkbenchUser user = new WorkbenchUser();
		Model model = Mockito.mock(Model.class);
		Mockito.when(this.workbenchEmailSenderService.validateResetToken(AuthenticationControllerTest.TEST_RESET_PASSWORD_TOKEN))
		.thenReturn(user);

		String page = this.controller.getCreateNewPasswordPage(AuthenticationControllerTest.TEST_RESET_PASSWORD_TOKEN, model);

		Mockito.verify(model, Mockito.times(1)).addAttribute("user", user);

		assertCommonAttributesWereAddedToModel(model);

		Assert.assertEquals("should return new-password page", "new-password", page);
	}

	private void assertCommonAttributesWereAddedToModel(Model model) {
		Mockito.verify(model).addAttribute(Mockito.eq("instituteLogoPath"), Mockito.anyObject());
		Mockito.verify(model).addAttribute(Mockito.eq("footerMessage"), Mockito.anyObject());
		Mockito.verify(model).addAttribute(Mockito.eq("version"), Mockito.anyObject());
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
		ResponseEntity<Map<String, Object>> result = this.controller.doSendResetPasswordRequestEmail(Mockito.mock(UserAccountModel.class));

		Assert.assertEquals("no http errors", HttpStatus.OK, result.getStatusCode());
		Assert.assertEquals("is successful", Boolean.TRUE, result.getBody().get(AuthenticationController.SUCCESS));

	}

	@Test
	public void testDoSendResetPasswordRequestEmailWithErrors() throws Exception {
		// houston we have a problem
		Mockito.when(this.workbenchUserService.getUserByUserName(ArgumentMatchers.<String>isNull())).thenReturn(Mockito.mock(WorkbenchUser.class));
		Mockito.doThrow(new MessagingException("i cant send me message :(")).when(this.workbenchEmailSenderService)
		.doRequestPasswordReset(ArgumentMatchers.any(WorkbenchUser.class));

		ResponseEntity<Map<String, Object>> result = this.controller.doSendResetPasswordRequestEmail(Mockito.mock(UserAccountModel.class));

		Assert.assertEquals("no http errors", HttpStatus.BAD_REQUEST, result.getStatusCode());
		Assert.assertEquals("is successful", Boolean.FALSE, result.getBody().get(AuthenticationController.SUCCESS));
	}

	@Test
	public void testDoResetPassword() throws Exception {
		UserAccountModel userAccountModel = new UserAccountModel();
		userAccountModel.setUsername("naymesh");
		userAccountModel.setPassword("b");
		ResponseEntity<Map<String, Object>> result = this.controller.doResetPassword(userAccountModel, this.result);

		Mockito.verify(this.workbenchUserService, Mockito.times(1)).updateUserPassword(userAccountModel.getUsername(), userAccountModel.getPassword());
		Mockito.verify(this.workbenchEmailSenderService, Mockito.times(1)).deleteToken(userAccountModel);

		Assert.assertEquals("no http errors", HttpStatus.OK, result.getStatusCode());
		Assert.assertEquals("is successful", Boolean.TRUE, result.getBody().get(AuthenticationController.SUCCESS));
	}

	@Test
	public void testTokenIsReturnedForSuccessfulAuthentication() {

		UserAccountModel testUserAccountModel = new UserAccountModel();
		testUserAccountModel.setUsername("naymesh");
		testUserAccountModel.setPassword("b");
		Mockito.when(this.workbenchUserService.isValidUserLogin(testUserAccountModel)).thenReturn(true);
		final Token testToken = new Token("naymesh:1447734506586:3a7e599e28efc35a2d53e62715ffd3cb", 1447734506586L);
		Mockito.when(this.apiAuthenticationService
				.authenticate(Mockito.eq(testUserAccountModel.getUsername()), Mockito.eq(testUserAccountModel.getPassword()))).thenReturn(
				testToken);

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
	public void testSendResetPasswordEmail() throws Exception {
		// default success scenario
		Integer id = RandomUtils.nextInt();

		Mockito.when(this.workbenchUserService.getUserByUserid(id))
				.thenReturn(Mockito.mock(WorkbenchUser.class));

		ResponseEntity<Map<String, Object>> result = this.controller
				.sendResetPasswordEmail(id);

		Assert.assertEquals("no http errors", HttpStatus.OK, result.getStatusCode());
		Assert.assertEquals("is successful", Boolean.TRUE, result.getBody().get(AuthenticationController.SUCCESS));

	}

	@Test
	public void testSendResetPasswordEmailWithErrors() throws Exception {
		// houston we have a problem
		Integer id = RandomUtils.nextInt();
		Mockito.when(this.workbenchUserService.getUserByUserName(ArgumentMatchers.<String>isNull()))
				.thenReturn(Mockito.mock(WorkbenchUser.class));
		Mockito.when(this.workbenchUserService.getUserByUserid(id))
				.thenReturn(Mockito.mock(WorkbenchUser.class));
		Mockito.doThrow(new MessagingException("i cant send me message :(")).when(this.workbenchEmailSenderService)
				.doRequestPasswordReset(ArgumentMatchers.any(WorkbenchUser.class));

		ResponseEntity<Map<String, Object>> result = this.controller
				.sendResetPasswordEmail(id);

		Assert.assertEquals("no http errors", HttpStatus.BAD_REQUEST, result.getStatusCode());
		Assert.assertEquals("is successful", Boolean.FALSE, result.getBody().get(AuthenticationController.SUCCESS));
	}

	@Test public void testSendResetPasswordEmailToNullUser() throws Exception {
		Integer id = RandomUtils.nextInt();
		Mockito.when(this.workbenchUserService.getUserByUserid(id)).thenReturn(null);

		ResponseEntity<Map<String, Object>> result = this.controller.sendResetPasswordEmail(id);

		Assert.assertEquals("no http errors", HttpStatus.BAD_REQUEST, result.getStatusCode());
		Assert.assertEquals("is successful", Boolean.FALSE, result.getBody().get(AuthenticationController.SUCCESS));

	}

	@Test @Ignore
	public void testIsAccountCreationEnabled() {

		// If SingleUserOnly mode is enabled, it will override EnableCreateAccount
		this.controller.setIsSingleUserOnly("true");
		//this.controller.setEnableCreateAccount("true");

		Assert.assertFalse(this.controller.isAccountCreationEnabled());

		this.controller.setIsSingleUserOnly("true");
		//this.controller.setEnableCreateAccount("false");

		Assert.assertFalse(this.controller.isAccountCreationEnabled());


		// If SingleUserOnly mode is disabled, return value is EnableCreateAccount
		this.controller.setIsSingleUserOnly("false");
		//this.controller.setEnableCreateAccount("true");

		Assert.assertTrue(this.controller.isAccountCreationEnabled());

		this.controller.setIsSingleUserOnly("false");
		//this.controller.setEnableCreateAccount("false");

		Assert.assertFalse(this.controller.isAccountCreationEnabled());
	}

	private void createTestRoles() {
		this.roles = new ArrayList<>();
		this.roles.add(new Role(1, "Admin"));
		this.selectedRole = new Role(2, "Breeder");
		this.roles.add(this.selectedRole);
		this.roles.add(new Role(3, "Technician"));
	}
}
