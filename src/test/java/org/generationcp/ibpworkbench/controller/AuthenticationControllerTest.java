package org.generationcp.ibpworkbench.controller;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.security.InvalidResetTokenException;
import org.generationcp.ibpworkbench.security.WorkbenchEmailSenderService;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.ibpworkbench.validator.ForgotPasswordAccountValidator;
import org.generationcp.ibpworkbench.validator.UserAccountValidator;
import org.generationcp.middleware.api.role.RoleService;
import org.generationcp.middleware.pojos.workbench.Role;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.security.OneTimePasswordDto;
import org.generationcp.middleware.service.api.security.OneTimePasswordService;
import org.generationcp.middleware.service.api.security.UserDeviceMetaDataDto;
import org.generationcp.middleware.service.api.security.UserDeviceMetaDataService;
import org.generationcp.middleware.service.api.user.RoleSearchDto;
import org.generationcp.middleware.util.UserDeviceMetaDataUtil;
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
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

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

	@Mock
	private HttpServletRequest httpServletRequest;

	@Mock
	private OneTimePasswordService oneTimePasswordService;

	@Mock
	private UserDeviceMetaDataService userDeviceMetaDataService;

	private List<Role> roles;
	private Role selectedRole;

	@Before
	public void setup() {
		this.createTestRoles();
		Mockito.doReturn(this.selectedRole.getId()).when(this.userAccountModel).getRoleId();
		Mockito.doReturn(this.roles).when(this.roleService).getRoles(new RoleSearchDto(Boolean.TRUE, null, null));

		Mockito.doReturn("1.2.3.4").when(this.httpServletRequest).getHeader("x-forwarded-for");
		Mockito.doReturn("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.111 Safari/537.36")
			.when(this.httpServletRequest).getHeader("User-Agent");

		Mockito.when(this.userDeviceMetaDataService.findExistingDevice(any(), any(), any())).thenReturn(Optional.empty());

		this.controller.setEnable2FA(false);
		this.controller.setEnable2FAOnUnknownDevice(false);
		this.controller.setMaximumOtpVerificationAttempt(5);
		this.controller.setOtpVerificationAttemptExpiry(5);

	}

	@Test
	public void testIntialize() {
		this.controller.initialize();
		Mockito.verify(this.roleService).getRoles(new RoleSearchDto(Boolean.TRUE, null, null));
		assertEquals(this.roles, this.controller.getRoles());
	}

	@Test
	public void testGetLoginPage() throws Exception {
		final Model model = Mockito.mock(Model.class);
		assertEquals("should return the login url", "login", this.controller.getLoginPage(model));
		Mockito.verify(model).addAttribute(Mockito.eq("roles"), Mockito.anyObject());
		this.assertCommonAttributesWereAddedToModel(model);
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
		final Model model = Mockito.mock(Model.class);

		assertEquals("should return the login url", "login", this.controller.getLoginPage(model));
		Mockito.verify(model).addAttribute("isCreateAccountEnable", false);
	}

	@Test
	public void testSaveUserAccount() throws Exception {
		this.controller.setRoles(this.roles);
		Mockito.when(this.result.hasErrors()).thenReturn(false);

		final ResponseEntity<Map<String, Object>> out = this.controller.saveUserAccount(this.userAccountModel, this.result);
		Mockito.verify(this.userAccountModel).setRole(this.selectedRole);
		assertEquals("ok status", HttpStatus.OK, out.getStatusCode());

		Assert.assertTrue("success = true", (Boolean) out.getBody().get("success"));

	}

	@Test
	public void testSaveUserAccountWithErrors() throws Exception {
		this.controller.setRoles(this.roles);
		Mockito.when(this.result.hasErrors()).thenReturn(true);
		Mockito.when(this.result.getFieldErrors()).thenReturn(Collections.<FieldError>emptyList());

		final ResponseEntity<Map<String, Object>> out = this.controller.saveUserAccount(this.userAccountModel, this.result);

		assertEquals("should output bad request status", HttpStatus.BAD_REQUEST, out.getStatusCode());
		Assert.assertFalse("success = false", (Boolean) out.getBody().get("success"));

	}

	@Test
	public void testValidateLogin() throws Exception {

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername(RandomStringUtils.randomAlphabetic(10));
		userAccount.setPassword(RandomStringUtils.randomAlphabetic(10));

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		workbenchUser.setMultiFactorAuthenticationEnabled(false);
		Mockito.when(this.workbenchUserService.getUserByUserName(userAccount.getUsername())).thenReturn(workbenchUser);

		Mockito.when(this.workbenchUserService.isValidUserLogin(userAccount)).thenReturn(true);

		final ResponseEntity<Map<String, Object>> out =
			this.controller.validateLogin(userAccount, this.result, this.httpServletRequest);

		assertEquals("ok status", HttpStatus.OK, out.getStatusCode());

		Assert.assertTrue("success = true", (Boolean) out.getBody().get("success"));
	}

	@Test
	public void testValidateLogin_TwoFactorAuthenticationIsEnabledPerUser() throws Exception {

		// Enable two-factor-authentication in the system
		this.controller.setEnable2FA(true);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername(RandomStringUtils.randomAlphabetic(10));
		userAccount.setPassword(RandomStringUtils.randomAlphabetic(10));

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		// Enable two-factor authentication for this user
		workbenchUser.setMultiFactorAuthenticationEnabled(true);
		Mockito.when(this.workbenchUserService.getUserByUserName(userAccount.getUsername())).thenReturn(workbenchUser);

		Mockito.when(this.workbenchUserService.isValidUserLogin(userAccount)).thenReturn(true);

		final ResponseEntity<Map<String, Object>> out =
			this.controller.validateLogin(userAccount, this.result, this.httpServletRequest);

		assertEquals("ok status", HttpStatus.OK, out.getStatusCode());
		Assert.assertTrue("success = true", (Boolean) out.getBody().get("success"));
		Assert.assertTrue("requireOneTimePassword = true", (Boolean) out.getBody().get("requireOneTimePassword"));
	}

	@Test
	public void testValidateLogin_TwoFactorAuthenticationIsNotEnabledPerUser_UserLogsInToUnknownDevice() throws Exception {

		// Enable two-factor-authentication in the system
		this.controller.setEnable2FA(true);
		// Enforce two-factor authentication if the user logs in to an unknown device
		this.controller.setEnable2FAOnUnknownDevice(true);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername(RandomStringUtils.randomAlphabetic(10));
		userAccount.setPassword(RandomStringUtils.randomAlphabetic(10));

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		// Disable two-factor authentication for this user
		workbenchUser.setMultiFactorAuthenticationEnabled(false);
		Mockito.when(this.workbenchUserService.getUserByUserName(userAccount.getUsername())).thenReturn(workbenchUser);

		Mockito.when(this.workbenchUserService.isValidUserLogin(userAccount)).thenReturn(true);

		// Return en empty device, so that the system knows that it's the first time the user logs into the device
		Mockito.when(this.userDeviceMetaDataService.findExistingDevice(any(), any(), any())).thenReturn(Optional.empty());

		final ResponseEntity<Map<String, Object>> out =
			this.controller.validateLogin(userAccount, this.result, this.httpServletRequest);

		assertEquals("ok status", HttpStatus.OK, out.getStatusCode());
		Assert.assertTrue("success = true", (Boolean) out.getBody().get("success"));
		Assert.assertTrue("requireOneTimePassword = true", (Boolean) out.getBody().get("requireOneTimePassword"));
	}

	@Test
	public void testValidateLogin_TwoFactorAuthenticationIsNotEnabledPerUser_UserLogsInToAKnownDevice() throws Exception {

		// Enable two-factor-authentication in the system
		this.controller.setEnable2FA(true);
		// Enforce two-factor authentication if the user logs in to an unknown device
		this.controller.setEnable2FAOnUnknownDevice(true);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername(RandomStringUtils.randomAlphabetic(10));
		userAccount.setPassword(RandomStringUtils.randomAlphabetic(10));

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		// Disable two-factor authentication for this user
		workbenchUser.setMultiFactorAuthenticationEnabled(false);
		Mockito.when(this.workbenchUserService.getUserByUserName(userAccount.getUsername())).thenReturn(workbenchUser);

		Mockito.when(this.workbenchUserService.isValidUserLogin(userAccount)).thenReturn(true);

		// Return a known device, so that the system knows that it's not the first time the user logs into the device
		final UserDeviceMetaDataDto userDeviceMetaDataDto = new UserDeviceMetaDataDto();
		userDeviceMetaDataDto.setUserId(workbenchUser.getUserid());
		userDeviceMetaDataDto.setDeviceDetails(RandomStringUtils.randomAlphabetic(10));
		userDeviceMetaDataDto.setLocation(RandomStringUtils.randomAlphabetic(10));
		Mockito.when(this.userDeviceMetaDataService.findExistingDevice(any(), any(), any())).thenReturn(Optional.of(userDeviceMetaDataDto));

		final ResponseEntity<Map<String, Object>> out =
			this.controller.validateLogin(userAccount, this.result, this.httpServletRequest);

		assertEquals("ok status", HttpStatus.OK, out.getStatusCode());
		Assert.assertTrue("success = true", (Boolean) out.getBody().get("success"));
		Assert.assertFalse(out.getBody().containsKey("requireOneTimePassword"));
	}

	@Test
	public void testCreateOTP_TwoFactorAuthenticationIsNotEnabledPerUser() throws MessagingException {
		// Enable two-factor-authentication in the system
		this.controller.setEnable2FA(true);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername(RandomStringUtils.randomAlphabetic(10));
		userAccount.setPassword(RandomStringUtils.randomAlphabetic(10));

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		// Enable two-factor authentication for this user
		workbenchUser.setMultiFactorAuthenticationEnabled(true);
		Mockito.when(this.workbenchUserService.getUserByUserName(userAccount.getUsername())).thenReturn(workbenchUser);
		Mockito.when(this.workbenchUserService.isValidUserLogin(userAccount)).thenReturn(true);
		final OneTimePasswordDto oneTimePasswordDto = new OneTimePasswordDto();
		oneTimePasswordDto.setExpires(new Date());
		oneTimePasswordDto.setOtpCode(123456);
		Mockito.when(this.oneTimePasswordService.createOneTimePassword()).thenReturn(oneTimePasswordDto);

		final ResponseEntity<Map<String, Object>> out =
			this.controller.createOTP(userAccount, this.httpServletRequest);

		assertEquals("ok status", HttpStatus.OK, out.getStatusCode());
		Mockito.verify(this.workbenchEmailSenderService).doSendOneTimePasswordRequest(workbenchUser, oneTimePasswordDto.getOtpCode());
	}

	@Test
	public void testCreateOTP_TwoFactorAuthenticationIsNotEnabledPerUser_UserLogsInToAKnownDevice() throws MessagingException {
		// Enable two-factor-authentication in the system
		this.controller.setEnable2FA(true);
		// Enforce two-factor authentication if the user logs in to an unknown device
		this.controller.setEnable2FAOnUnknownDevice(true);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername(RandomStringUtils.randomAlphabetic(10));
		userAccount.setPassword(RandomStringUtils.randomAlphabetic(10));

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		// Disable two-factor authentication for this user
		workbenchUser.setMultiFactorAuthenticationEnabled(false);
		Mockito.when(this.workbenchUserService.getUserByUserName(userAccount.getUsername())).thenReturn(workbenchUser);
		Mockito.when(this.workbenchUserService.isValidUserLogin(userAccount)).thenReturn(true);
		final OneTimePasswordDto oneTimePasswordDto = new OneTimePasswordDto();
		oneTimePasswordDto.setExpires(new Date());
		oneTimePasswordDto.setOtpCode(123456);
		Mockito.when(this.oneTimePasswordService.createOneTimePassword()).thenReturn(oneTimePasswordDto);

		// Return a known device, so that the system knows that it's not the first time the user logs into the device
		final UserDeviceMetaDataDto userDeviceMetaDataDto = new UserDeviceMetaDataDto();
		userDeviceMetaDataDto.setUserId(workbenchUser.getUserid());
		userDeviceMetaDataDto.setDeviceDetails(RandomStringUtils.randomAlphabetic(10));
		userDeviceMetaDataDto.setLocation(RandomStringUtils.randomAlphabetic(10));
		Mockito.when(this.userDeviceMetaDataService.findExistingDevice(any(), any(), any())).thenReturn(Optional.of(userDeviceMetaDataDto));

		final ResponseEntity<Map<String, Object>> out =
			this.controller.createOTP(userAccount, this.httpServletRequest);

		final String location = UserDeviceMetaDataUtil.extractIp(this.httpServletRequest);
		final String deviceDetails = UserDeviceMetaDataUtil.getDeviceDetails(this.httpServletRequest);

		assertEquals("ok status", HttpStatus.OK, out.getStatusCode());
		// Make sure the system does not send an email.
		Mockito.verifyNoInteractions(this.workbenchEmailSenderService);
	}

	@Test
	public void testCreateOTP_TwoFactorAuthenticationIsNotEnabledPerUser_UserLogsInToAnUnknownDevice() throws MessagingException {
		// Enable two-factor-authentication in the system
		this.controller.setEnable2FA(true);
		// Enforce two-factor authentication if the user logs in to an unknown device
		this.controller.setEnable2FAOnUnknownDevice(true);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername(RandomStringUtils.randomAlphabetic(10));
		userAccount.setPassword(RandomStringUtils.randomAlphabetic(10));

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		// Disable two-factor authentication for this user
		workbenchUser.setMultiFactorAuthenticationEnabled(false);
		Mockito.when(this.workbenchUserService.getUserByUserName(userAccount.getUsername())).thenReturn(workbenchUser);
		Mockito.when(this.workbenchUserService.isValidUserLogin(userAccount)).thenReturn(true);
		final OneTimePasswordDto oneTimePasswordDto = new OneTimePasswordDto();
		oneTimePasswordDto.setExpires(new Date());
		oneTimePasswordDto.setOtpCode(123456);
		Mockito.when(this.oneTimePasswordService.createOneTimePassword()).thenReturn(oneTimePasswordDto);

		// Return en empty device, so that the system knows that it's the first time the user logs into the device
		Mockito.when(this.userDeviceMetaDataService.findExistingDevice(any(), any(), any())).thenReturn(Optional.empty());

		final ResponseEntity<Map<String, Object>> out =
			this.controller.createOTP(userAccount, this.httpServletRequest);

		final String location = UserDeviceMetaDataUtil.extractIp(this.httpServletRequest);
		final String deviceDetails = UserDeviceMetaDataUtil.getDeviceDetails(this.httpServletRequest);

		assertEquals("ok status", HttpStatus.OK, out.getStatusCode());
		Mockito.verify(this.workbenchEmailSenderService)
			.doSendOneTimePasswordRequestOnUnknownDevice(workbenchUser, oneTimePasswordDto.getOtpCode(), deviceDetails, location);
	}

	@Test
	public void testCreateOTP_ErrorInSendingEmail() throws MessagingException {
		// Enable two-factor-authentication in the system
		this.controller.setEnable2FA(true);
		// Enforce two-factor authentication if the user logs in to an unknown device
		this.controller.setEnable2FAOnUnknownDevice(false);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername(RandomStringUtils.randomAlphabetic(10));
		userAccount.setPassword(RandomStringUtils.randomAlphabetic(10));

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		// Disable two-factor authentication for this user
		workbenchUser.setMultiFactorAuthenticationEnabled(true);
		Mockito.when(this.workbenchUserService.getUserByUserName(userAccount.getUsername())).thenReturn(workbenchUser);
		Mockito.when(this.workbenchUserService.isValidUserLogin(userAccount)).thenReturn(true);
		final OneTimePasswordDto oneTimePasswordDto = new OneTimePasswordDto();
		oneTimePasswordDto.setExpires(new Date());
		oneTimePasswordDto.setOtpCode(123456);
		Mockito.when(this.oneTimePasswordService.createOneTimePassword()).thenReturn(oneTimePasswordDto);

		Mockito.doThrow(MessagingException.class).when(this.workbenchEmailSenderService)
			.doSendOneTimePasswordRequest(workbenchUser, oneTimePasswordDto.getOtpCode());

		Mockito.when(this.messageSource.getMessage("one.time.password.cannot.send.email", new String[] {}, "",
			LocaleContextHolder.getLocale())).thenReturn("error sending email");

		final ResponseEntity<Map<String, Object>> out =
			this.controller.createOTP(userAccount, this.httpServletRequest);

		final String location = UserDeviceMetaDataUtil.extractIp(this.httpServletRequest);
		final String deviceDetails = UserDeviceMetaDataUtil.getDeviceDetails(this.httpServletRequest);

		assertEquals("internal error status", HttpStatus.INTERNAL_SERVER_ERROR, out.getStatusCode());
		assertEquals("error sending email", (String) out.getBody().get("errors"));
	}

	@Test
	public void testCreateOTP_UserIsNotValid() throws MessagingException {
		// Enable two-factor-authentication in the system
		this.controller.setEnable2FA(true);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername(RandomStringUtils.randomAlphabetic(10));
		userAccount.setPassword(RandomStringUtils.randomAlphabetic(10));

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		// Disable two-factor authentication for this user
		workbenchUser.setMultiFactorAuthenticationEnabled(true);
		Mockito.when(this.workbenchUserService.getUserByUserName(userAccount.getUsername())).thenReturn(workbenchUser);
		Mockito.when(this.workbenchUserService.isValidUserLogin(userAccount)).thenReturn(false);

		Mockito.when(this.messageSource.getMessage("one.time.password.cannot.create.otp", new String[] {}, "",
			LocaleContextHolder.getLocale())).thenReturn("error in creating OTP");

		final ResponseEntity<Map<String, Object>> out =
			this.controller.createOTP(userAccount, this.httpServletRequest);

		assertEquals("unauthorized status", HttpStatus.UNAUTHORIZED, out.getStatusCode());
		assertEquals("error in creating OTP", (String) out.getBody().get("errors"));
		Mockito.verifyNoInteractions(this.workbenchEmailSenderService);
	}

	@Test
	public void testCreateOTP_TwoFactorIsNotEnabledInTheSystem() throws MessagingException {
		// Disable two-factor-authentication in the system
		this.controller.setEnable2FA(false);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername(RandomStringUtils.randomAlphabetic(10));
		userAccount.setPassword(RandomStringUtils.randomAlphabetic(10));

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		// Disable two-factor authentication for this user
		workbenchUser.setMultiFactorAuthenticationEnabled(true);
		Mockito.when(this.workbenchUserService.getUserByUserName(userAccount.getUsername())).thenReturn(workbenchUser);

		Mockito.when(this.messageSource.getMessage("one.time.password.cannot.create.otp", new String[] {}, "",
			LocaleContextHolder.getLocale())).thenReturn("error in creating OTP");

		final ResponseEntity<Map<String, Object>> out =
			this.controller.createOTP(userAccount, this.httpServletRequest);

		assertEquals("unauthorized status", HttpStatus.UNAUTHORIZED, out.getStatusCode());
		assertEquals("error in creating OTP", (String) out.getBody().get("errors"));
		Mockito.verifyNoInteractions(this.workbenchEmailSenderService);
	}

	@Test
	public void testValidateOTP_ValidOTP() {

		this.controller.setOtpVerificationAttemptCache(this.createTestOtpVerificationAttemptCache());

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername(RandomStringUtils.randomAlphabetic(10));
		userAccount.setPassword(RandomStringUtils.randomAlphabetic(10));
		userAccount.setOtpCode(123456);

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		// Disable two-factor authentication for this user
		workbenchUser.setMultiFactorAuthenticationEnabled(true);
		Mockito.when(this.workbenchUserService.getUserByUserName(userAccount.getUsername())).thenReturn(workbenchUser);
		Mockito.when(this.workbenchUserService.isValidUserLogin(userAccount)).thenReturn(true);
		Mockito.when(this.oneTimePasswordService.isOneTimePasswordValid(userAccount.getOtpCode())).thenReturn(true);
		final Token token = new Token();
		token.setToken(RandomStringUtils.randomAlphabetic(10));
		token.setExpires(12345l);
		Mockito.when(this.apiAuthenticationService.authenticate(userAccount.getUsername(), userAccount.getPassword())).thenReturn(token);

		final ResponseEntity<Map<String, Object>> out =
			this.controller.validateOTP(userAccount, this.httpServletRequest);

		assertEquals("ok status", HttpStatus.OK, out.getStatusCode());
		assertEquals(token.getToken(), (String) out.getBody().get("token"));
		assertEquals(token.getExpires(), (long) out.getBody().get("expires"));
		final String location = UserDeviceMetaDataUtil.extractIp(this.httpServletRequest);
		final String deviceDetails = UserDeviceMetaDataUtil.getDeviceDetails(this.httpServletRequest);
		Mockito.verify(this.userDeviceMetaDataService).addToExistingDevice(workbenchUser.getUserid(), deviceDetails, location);
	}

	@Test
	public void testValidateOTP_InvalidOTP() {

		this.controller.setOtpVerificationAttemptCache(this.createTestOtpVerificationAttemptCache());

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername(RandomStringUtils.randomAlphabetic(10));
		userAccount.setPassword(RandomStringUtils.randomAlphabetic(10));
		userAccount.setOtpCode(123456);

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		// Disable two-factor authentication for this user
		workbenchUser.setMultiFactorAuthenticationEnabled(true);
		Mockito.when(this.workbenchUserService.isValidUserLogin(userAccount)).thenReturn(true);
		Mockito.when(this.oneTimePasswordService.isOneTimePasswordValid(userAccount.getOtpCode())).thenReturn(false);
		Mockito.when(this.messageSource.getMessage("one.time.password.invalid.otp", new String[] {}, "",
			LocaleContextHolder.getLocale())).thenReturn("invalid OTP");

		final ResponseEntity<Map<String, Object>> out =
			this.controller.validateOTP(userAccount, this.httpServletRequest);

		assertEquals("unauthorized status", HttpStatus.UNAUTHORIZED, out.getStatusCode());
		assertEquals("invalid OTP", (String) out.getBody().get("errors"));
		Mockito.verifyNoInteractions(this.userDeviceMetaDataService);
	}

	@Test
	public void testValidateOTP_InvalidUser() {

		this.controller.setOtpVerificationAttemptCache(this.createTestOtpVerificationAttemptCache());

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername(RandomStringUtils.randomAlphabetic(10));
		userAccount.setPassword(RandomStringUtils.randomAlphabetic(10));
		userAccount.setOtpCode(123456);

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		// Disable two-factor authentication for this user
		workbenchUser.setMultiFactorAuthenticationEnabled(true);
		Mockito.when(this.workbenchUserService.isValidUserLogin(userAccount)).thenReturn(false);
		Mockito.when(this.messageSource.getMessage("one.time.password.invalid.otp", new String[] {}, "",
			LocaleContextHolder.getLocale())).thenReturn("invalid OTP");

		final ResponseEntity<Map<String, Object>> out =
			this.controller.validateOTP(userAccount, this.httpServletRequest);

		assertEquals("unauthorized status", HttpStatus.UNAUTHORIZED, out.getStatusCode());
		assertEquals("invalid OTP", (String) out.getBody().get("errors"));
		Mockito.verifyNoInteractions(this.userDeviceMetaDataService);

	}

	@Test
	public void testValidateOTP_MaximumVerificationAttemptError() {

		final LoadingCache<String, Integer> otpVerificationAttemptCache = this.createTestOtpVerificationAttemptCache();
		this.controller.setMaximumOtpVerificationAttempt(2);
		this.controller.setOtpVerificationAttemptExpiry(5);
		this.controller.setOtpVerificationAttemptCache(otpVerificationAttemptCache);

		final UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername(RandomStringUtils.randomAlphabetic(10));
		userAccount.setPassword(RandomStringUtils.randomAlphabetic(10));
		userAccount.setOtpCode(123456);

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		// Disable two-factor authentication for this user
		workbenchUser.setMultiFactorAuthenticationEnabled(true);
		Mockito.when(this.workbenchUserService.isValidUserLogin(userAccount)).thenReturn(true);
		Mockito.when(this.oneTimePasswordService.isOneTimePasswordValid(userAccount.getOtpCode())).thenReturn(false);
		Mockito.when(this.messageSource.getMessage("one.time.password.maximum.verification.attempt.exceeded", new String[] {"5"}, "",
			LocaleContextHolder.getLocale())).thenReturn("maximum verification attemp exceeded");
		Mockito.when(this.messageSource.getMessage("one.time.password.invalid.otp", new String[] {}, "",
			LocaleContextHolder.getLocale())).thenReturn("invalid OTP");

		// Attempt to verify OTP 2 times
		this.controller.validateOTP(userAccount, this.httpServletRequest);
		this.controller.validateOTP(userAccount, this.httpServletRequest);

		// Throw maximum verification attempt error on third call,
		final ResponseEntity<Map<String, Object>> out =
			this.controller.validateOTP(userAccount, this.httpServletRequest);

		assertEquals("unauthorized status", HttpStatus.UNAUTHORIZED, out.getStatusCode());
		assertEquals("maximum verification attemp exceeded", (String) out.getBody().get("errors"));
		Mockito.verifyNoInteractions(this.userDeviceMetaDataService);

		// Simulate number of attempts has expired
		otpVerificationAttemptCache.invalidateAll();
		final ResponseEntity<Map<String, Object>> out2 =
			this.controller.validateOTP(userAccount, this.httpServletRequest);

		assertEquals("unauthorized status", HttpStatus.UNAUTHORIZED, out2.getStatusCode());
		assertEquals("invalid OTP", (String) out2.getBody().get("errors"));
		Mockito.verifyNoInteractions(this.userDeviceMetaDataService);

	}

	@Test
	public void testAddOrUpdateKnownDevices_AddUnknownDevice() {

		// Return en empty device, so that the system knows that it's the first time the user logs into the device
		Mockito.when(this.userDeviceMetaDataService.findExistingDevice(any(), any(), any())).thenReturn(Optional.empty());
		final String location = UserDeviceMetaDataUtil.extractIp(this.httpServletRequest);
		final String deviceDetails = UserDeviceMetaDataUtil.getDeviceDetails(this.httpServletRequest);

		this.controller.addOrUpdateKnownDevices(1, this.httpServletRequest);

		Mockito.verify(this.userDeviceMetaDataService).addToExistingDevice(1, deviceDetails, location);

	}

	@Test
	public void testAddOrUpdateKnownDevices_UpdateLastLoggedInOfAKnownDevice() {
		final int userId = 1;
		// Return a known device, so that the system knows that it's not the first time the user logs into the device
		final UserDeviceMetaDataDto userDeviceMetaDataDto = new UserDeviceMetaDataDto();
		userDeviceMetaDataDto.setUserId(userId);
		userDeviceMetaDataDto.setDeviceDetails(RandomStringUtils.randomAlphabetic(10));
		userDeviceMetaDataDto.setLocation(RandomStringUtils.randomAlphabetic(10));
		Mockito.when(this.userDeviceMetaDataService.findExistingDevice(any(), any(), any())).thenReturn(Optional.of(userDeviceMetaDataDto));

		final String location = UserDeviceMetaDataUtil.extractIp(this.httpServletRequest);
		final String deviceDetails = UserDeviceMetaDataUtil.getDeviceDetails(this.httpServletRequest);

		this.controller.addOrUpdateKnownDevices(userId, this.httpServletRequest);

		Mockito.verify(this.userDeviceMetaDataService).updateLastLoggedIn(userId, deviceDetails, location);
	}

	@Test
	public void testForgotPassword() throws Exception {
		Mockito.when(this.result.hasErrors()).thenReturn(false);

		final ResponseEntity<Map<String, Object>> out = this.controller.validateForgotPasswordForm(this.userAccountModel, this.result);

		assertEquals("ok status", HttpStatus.OK, out.getStatusCode());

		Assert.assertTrue("success = true", (Boolean) out.getBody().get("success"));
	}

	@Test
	public void testValidateLoginAndForgotPasswordWithErrors() throws Exception {
		Mockito.when(this.result.hasErrors()).thenReturn(true);
		Mockito.when(this.result.getFieldErrors()).thenReturn(Collections.<FieldError>emptyList());

		final ResponseEntity<Map<String, Object>> out =
			this.controller.validateLogin(this.userAccountModel, this.result, this.httpServletRequest);

		final ResponseEntity<Map<String, Object>> out2 = this.controller.validateForgotPasswordForm(this.userAccountModel, this.result);

		assertEquals("should output bad request status", HttpStatus.BAD_REQUEST, out.getStatusCode());
		Assert.assertFalse("success = false", (Boolean) out.getBody().get("success"));

		assertEquals("should output bad request status", HttpStatus.BAD_REQUEST, out2.getStatusCode());
		Assert.assertFalse("success = false", (Boolean) out.getBody().get("success"));

	}

	@Test
	public void testGetCreateNewPasswordPage() throws Exception {
		// assume everything is well
		final WorkbenchUser user = new WorkbenchUser();
		final Model model = Mockito.mock(Model.class);
		Mockito.when(this.workbenchEmailSenderService.validateResetToken(AuthenticationControllerTest.TEST_RESET_PASSWORD_TOKEN))
			.thenReturn(user);

		final String page = this.controller.getCreateNewPasswordPage(AuthenticationControllerTest.TEST_RESET_PASSWORD_TOKEN, model);

		Mockito.verify(model, Mockito.times(1)).addAttribute("user", user);

		this.assertCommonAttributesWereAddedToModel(model);

		assertEquals("should return new-password page", "new-password", page);
	}

	private void assertCommonAttributesWereAddedToModel(final Model model) {
		Mockito.verify(model).addAttribute(Mockito.eq("instituteLogoPath"), Mockito.anyObject());
		Mockito.verify(model).addAttribute(Mockito.eq("footerMessage"), Mockito.anyObject());
		Mockito.verify(model).addAttribute(Mockito.eq("version"), Mockito.anyObject());
	}

	@Test
	public void testGetCreateNewPasswordPageInvalidToken() throws Exception {
		final Model model = Mockito.mock(Model.class);
		Mockito.when(this.workbenchEmailSenderService.validateResetToken(AuthenticationControllerTest.TEST_RESET_PASSWORD_TOKEN)).thenThrow(
			new InvalidResetTokenException());

		final String page = this.controller.getCreateNewPasswordPage(AuthenticationControllerTest.TEST_RESET_PASSWORD_TOKEN, model);

		assertEquals("should redirect to login page", "redirect:" + AuthenticationController.URL, page);
	}

	@Test
	public void testDoSendResetPasswordRequestEmail() throws Exception {
		// default success scenario
		final ResponseEntity<Map<String, Object>> result =
			this.controller.doSendResetPasswordRequestEmail(Mockito.mock(UserAccountModel.class));

		assertEquals("no http errors", HttpStatus.OK, result.getStatusCode());
		assertEquals("is successful", Boolean.TRUE, result.getBody().get(AuthenticationController.SUCCESS));

	}

	@Test
	public void testDoSendResetPasswordRequestEmailWithErrors() throws Exception {
		// houston we have a problem
		Mockito.when(this.workbenchUserService.getUserByUserName(ArgumentMatchers.<String>isNull()))
			.thenReturn(Mockito.mock(WorkbenchUser.class));
		Mockito.doThrow(new MessagingException("i cant send me message :(")).when(this.workbenchEmailSenderService)
			.doRequestPasswordReset(any(WorkbenchUser.class));

		final ResponseEntity<Map<String, Object>> result =
			this.controller.doSendResetPasswordRequestEmail(Mockito.mock(UserAccountModel.class));

		assertEquals("no http errors", HttpStatus.BAD_REQUEST, result.getStatusCode());
		assertEquals("is successful", Boolean.FALSE, result.getBody().get(AuthenticationController.SUCCESS));
	}

	@Test
	public void testDoResetPassword() throws Exception {
		final UserAccountModel userAccountModel = new UserAccountModel();
		userAccountModel.setUsername("naymesh");
		userAccountModel.setPassword("b");
		final ResponseEntity<Map<String, Object>> result = this.controller.doResetPassword(userAccountModel, this.result);

		Mockito.verify(this.workbenchUserService, Mockito.times(1))
			.updateUserPassword(userAccountModel.getUsername(), userAccountModel.getPassword());
		Mockito.verify(this.workbenchEmailSenderService, Mockito.times(1)).deleteToken(userAccountModel);

		assertEquals("no http errors", HttpStatus.OK, result.getStatusCode());
		assertEquals("is successful", Boolean.TRUE, result.getBody().get(AuthenticationController.SUCCESS));
	}

	@Test
	public void testTokenIsReturnedForSuccessfulAuthentication() {

		final UserAccountModel testUserAccountModel = new UserAccountModel();
		testUserAccountModel.setUsername("naymesh");
		testUserAccountModel.setPassword("b");
		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		workbenchUser.setMultiFactorAuthenticationEnabled(false);
		Mockito.when(this.workbenchUserService.getUserByUserName(testUserAccountModel.getUsername())).thenReturn(workbenchUser);

		Mockito.when(this.workbenchUserService.isValidUserLogin(testUserAccountModel)).thenReturn(true);
		final Token testToken = new Token("naymesh:1447734506586:3a7e599e28efc35a2d53e62715ffd3cb", 1447734506586L);
		Mockito.when(this.apiAuthenticationService
			.authenticate(testUserAccountModel.getUsername(), testUserAccountModel.getPassword())).thenReturn(
			testToken);

		final ResponseEntity<Map<String, Object>> out =
			this.controller.validateLogin(testUserAccountModel, this.result, this.httpServletRequest);
		assertEquals(testToken.getToken(), out.getBody().get("token"));
		assertEquals(testToken.getExpires(), out.getBody().get("expires"));
	}

	@Test
	public void testTokenIsNotReturnedWhenThereIsFailureInApiAuthentication() {

		final UserAccountModel testUserAccountModel = new UserAccountModel();
		testUserAccountModel.setUsername("naymesh");
		testUserAccountModel.setPassword("b");
		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setUserid(1);
		workbenchUser.setMultiFactorAuthenticationEnabled(false);

		Mockito.when(this.workbenchUserService.getUserByUserName(testUserAccountModel.getUsername())).thenReturn(workbenchUser);
		Mockito.when(this.workbenchUserService.isValidUserLogin(testUserAccountModel)).thenReturn(true);

		// Case when ApiAuthenticationService will return null token
		Mockito.when(
			this.apiAuthenticationService.authenticate(testUserAccountModel.getUsername(),
				testUserAccountModel.getPassword())).thenReturn(null);

		final ResponseEntity<Map<String, Object>> out =
			this.controller.validateLogin(testUserAccountModel, this.result, this.httpServletRequest);
		Mockito.verify(this.apiAuthenticationService).authenticate(Mockito.anyString(), Mockito.anyString());
		Assert.assertNull(out.getBody().get("token"));
		Assert.assertNull(out.getBody().get("expires"));
	}

	@Test
	public void testTokenIsNotReturnedForUnSuccessfulAuthentication() {

		final UserAccountModel testUserAccountModel = new UserAccountModel();
		Mockito.when(this.workbenchUserService.isValidUserLogin(testUserAccountModel)).thenReturn(false);
		final ResponseEntity<Map<String, Object>> out =
			this.controller.validateLogin(testUserAccountModel, this.result, this.httpServletRequest);
		Mockito.verify(this.apiAuthenticationService, Mockito.never()).authenticate(Mockito.anyString(), Mockito.anyString());
		Assert.assertNull(out.getBody().get("token"));
		Assert.assertNull(out.getBody().get("expires"));
	}

	@Test
	public void testSendResetPasswordEmail() throws Exception {
		// default success scenario
		final Integer id = RandomUtils.nextInt();

		Mockito.when(this.workbenchUserService.getUserByUserid(id))
			.thenReturn(Mockito.mock(WorkbenchUser.class));

		final ResponseEntity<Map<String, Object>> result = this.controller
			.sendResetPasswordEmail(id);

		assertEquals("no http errors", HttpStatus.OK, result.getStatusCode());
		assertEquals("is successful", Boolean.TRUE, result.getBody().get(AuthenticationController.SUCCESS));

	}

	@Test
	public void testSendResetPasswordEmailWithErrors() throws Exception {
		// houston we have a problem
		final Integer id = RandomUtils.nextInt();
		Mockito.when(this.workbenchUserService.getUserByUserName(ArgumentMatchers.<String>isNull()))
			.thenReturn(Mockito.mock(WorkbenchUser.class));
		Mockito.when(this.workbenchUserService.getUserByUserid(id))
			.thenReturn(Mockito.mock(WorkbenchUser.class));
		Mockito.doThrow(new MessagingException("i cant send me message :(")).when(this.workbenchEmailSenderService)
			.doRequestPasswordReset(any(WorkbenchUser.class));

		final ResponseEntity<Map<String, Object>> result = this.controller
			.sendResetPasswordEmail(id);

		assertEquals("no http errors", HttpStatus.BAD_REQUEST, result.getStatusCode());
		assertEquals("is successful", Boolean.FALSE, result.getBody().get(AuthenticationController.SUCCESS));
	}

	@Test
	public void testSendResetPasswordEmailToNullUser() throws Exception {
		final Integer id = RandomUtils.nextInt();
		Mockito.when(this.workbenchUserService.getUserByUserid(id)).thenReturn(null);

		final ResponseEntity<Map<String, Object>> result = this.controller.sendResetPasswordEmail(id);

		assertEquals("no http errors", HttpStatus.BAD_REQUEST, result.getStatusCode());
		assertEquals("is successful", Boolean.FALSE, result.getBody().get(AuthenticationController.SUCCESS));

	}

	@Test
	@Ignore
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

	private LoadingCache<String, Integer> createTestOtpVerificationAttemptCache() {
		return CacheBuilder.newBuilder().refreshAfterWrite(1, TimeUnit.MINUTES)
			.build(new CacheLoader<String, Integer>() {

				public Integer load(final String key) {
					return 0;
				}
			});
	}
}
