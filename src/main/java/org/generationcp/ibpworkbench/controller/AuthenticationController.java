package org.generationcp.ibpworkbench.controller;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.security.InvalidResetTokenException;
import org.generationcp.ibpworkbench.security.WorkbenchEmailSenderService;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.ibpworkbench.validator.ForgotPasswordAccountValidator;
import org.generationcp.ibpworkbench.validator.UserAccountFields;
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
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping(AuthenticationController.URL)
public class AuthenticationController {

	public static final String URL = "/auth";

	public static final String SUCCESS = "success";
	public static final String ERRORS = "errors";

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

	private static final String NOT_EXISTENT_USER = "User does not exist";
	public static final String USER_AGENT = "User-Agent";

	@Resource
	private WorkbenchUserService workbenchUserService;

	@Resource
	private RoleService roleService;

	@Resource
	private UserAccountValidator userAccountValidator;

	@Resource
	private ForgotPasswordAccountValidator forgotPasswordAccountValidator;

	@Resource
	private WorkbenchEmailSenderService workbenchEmailSenderService;

	@Resource
	private MessageSource messageSource;

	@Resource
	private ApiAuthenticationService apiAuthenticationService;

	@Resource
	private OneTimePasswordService oneTimePasswordService;

	@Resource
	private UserDeviceMetaDataService userDeviceMetaDataService;

	@Resource
	private ServletContext servletContext;

	@Resource
	@Qualifier("workbenchProperties")
	private Properties workbenchProperties;

	@Value("${institute.logo.path}")
	private String instituteLogoPath;

	@Value("${footer.message}")
	private String footerMessage;

	@Value("${bms.version}")
	private String workbenchVersion;

	@Value("${bv.design.validation.on.login.enabled}")
	private Boolean bvDesignValidationEnabled;

	@Value("${security.2fa.enabled}")
	private boolean enableTwoFactorAuthentication;

	@Value("${security.2fa.enforce.otp.on.unknown.device}")
	private boolean enable2FAOnUnknownDevice;

	@Value("${security.2fa.otp.maximum.verification.attempts}")
	private Integer maximumOtpVerificationAttempt;

	@Value("${security.2fa.otp.maximum.verification.attempts.expiry.interval}")
	private Integer otpVerificationAttemptExpiry;

	@Value("${security.2fa.otp.length}")
	private int otpCodeLength;

	// Stores the number of times the OTP verification is called per user.
	// The value stored will expire in a specified number of minutes (otpVerificationAttemptExpiry).
	private LoadingCache<String, Integer> otpVerificationAttemptCache;

	@PostConstruct
	public void initialize() {
		this.footerMessage = Sanitizers.FORMATTING.sanitize(this.footerMessage);
		// This is to track the number of OTP verification attempts per user.
		this.otpVerificationAttemptCache = CacheBuilder.newBuilder().refreshAfterWrite(this.otpVerificationAttemptExpiry, TimeUnit.MINUTES)
			.build(new CacheLoader<String, Integer>() {

				public Integer load(final String key) {
					return 1;
				}
			});
	}

	@RequestMapping(value = "/login")
	public String getLoginPage(final Model model) {

		model.addAttribute("otpCodeLength", this.otpCodeLength);
		this.populateCommomModelAttributes(model);

		return "login";
	}

	/**
	 * Return img logo or emtpy if file not present
	 *
	 * @param path path to logo image
	 * @return img src
	 */
	protected String findInstituteLogo(final String path) {
		if (this.servletContext.getResourceAsStream("/WEB-INF/" + path) != null) {
			return "/controller/" + path;
		} else {
			return "";
		}
	}

	@RequestMapping(value = "/reset/{token}", method = RequestMethod.GET)
	public String getCreateNewPasswordPage(@PathVariable final String token, final Model model) {

		// verify token if valid
		try {
			final WorkbenchUser user = this.workbenchEmailSenderService.validateResetToken(token);

			model.addAttribute("user", user);

			this.populateCommomModelAttributes(model);

			return "new-password";

		} catch (final InvalidResetTokenException e) {
			AuthenticationController.LOG.debug(e.getMessage(), e);
			return "redirect:" + AuthenticationController.URL;
		}
	}

	private void populateCommomModelAttributes(final Model model) {
		model.addAttribute("instituteLogoPath", this.findInstituteLogo(this.instituteLogoPath));
		model.addAttribute("footerMessage", this.footerMessage);
		model.addAttribute("version", this.workbenchVersion);
		model.addAttribute("isLicenseValidationEnabled", this.bvDesignValidationEnabled);
	}

	@ResponseBody
	@RequestMapping(value = "/otp/create", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> createOTP(@RequestBody final UserAccountModel model, final HttpServletRequest request) {
		final Map<String, Object> response = new LinkedHashMap<>();
		// Generate one-time password (OTP code) and then send it to the user's email
		final WorkbenchUser workbenchUser = this.workbenchUserService.getUserByUserName(model.getUsername());
		// Generate OTP Code only if two-factor authentication is enabled in the system
		// and username + password is valid
		if (this.enableTwoFactorAuthentication && this.workbenchUserService.isValidUserLogin(model)) {
			final OneTimePasswordDto oneTimePasswordDto = this.oneTimePasswordService.createOneTimePassword();

			final Optional<UserDeviceMetaDataDto> existingUserDeviceMetaDataDtoOptional =
				this.findExistingUserDevice(workbenchUser.getUserid(), request);

			try {

				if (workbenchUser.isMultiFactorAuthenticationEnabled()) {
					// If the user is configured for 2FA, send an OTP email
					this.workbenchEmailSenderService.sendOneTimePasswordRequest(workbenchUser, oneTimePasswordDto.getOtpCode());

				} else if (this.enable2FAOnUnknownDevice && this.userDeviceMetaDataService.countUserDevices(workbenchUser.getUserid()) > 0
					&& !existingUserDeviceMetaDataDtoOptional.isPresent()) {
					// If the user is not configured for 2FA, but is logging in a new device (Only if the user already has history of devices),
					// send OTP email for unknown device
					final String location = UserDeviceMetaDataUtil.extractIp(request);
					final String deviceDetails = request.getHeader(USER_AGENT);

					this.workbenchEmailSenderService.sendOneTimePasswordRequestForUnknownDevice(workbenchUser,
						oneTimePasswordDto.getOtpCode(),
						UserDeviceMetaDataUtil.parseDeviceDetailsForDisplay(deviceDetails), location);
				}

				return new ResponseEntity<>(response, HttpStatus.OK);
			} catch (final MessagingException e) {
				final String errorMessage = this.messageSource.getMessage("one.time.password.cannot.send.email", new String[] {}, "",
					LocaleContextHolder.getLocale());
				AuthenticationController.LOG.error(errorMessage, e);
				response.put(ERRORS, errorMessage);
				return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			response.put(ERRORS, this.messageSource.getMessage("one.time.password.cannot.create.otp", new String[] {}, "",
				LocaleContextHolder.getLocale()));
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}

	}

	@ResponseBody
	@RequestMapping(value = "/otp/verify", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> validateOTP(@RequestBody final UserAccountModel model, final HttpServletRequest request)
		throws ExecutionException {
		final Map<String, Object> response = new LinkedHashMap<>();

		// To prevent a brute force attack, add a maximum number of OTP code verification attempts
		final Integer numberOfAttempts = this.otpVerificationAttemptCache.get(model.getUsername());
		if (numberOfAttempts > this.maximumOtpVerificationAttempt) {
			response.put(ERRORS,
				this.messageSource.getMessage("one.time.password.maximum.verification.attempt.exceeded",
					new String[] {String.valueOf(this.otpVerificationAttemptExpiry)}, "",
					LocaleContextHolder.getLocale()));
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}
		this.otpVerificationAttemptCache.put(model.getUsername(), numberOfAttempts + 1);

		// Verify if the OTP code is valid
		if (this.workbenchUserService.isValidUserLogin(model) && this.oneTimePasswordService.isOneTimePasswordValid(model.getOtpCode())) {

			final WorkbenchUser workbenchUser = this.workbenchUserService.getUserByUserName(model.getUsername());
			// If OTP is valid, return a valid token
			/*
			 * This is crucial for frontend apps which needs the authentication token to make calls to BMSAPI services.
			 * See login.js and bmsAuth.js client side scripts to see how this token is used by front-end code via the local storage
			 * service in browsers.
			 */

			final Token apiAuthToken = this.apiAuthenticationService.authenticate(model.getUsername(), model.getPassword());
			if (apiAuthToken != null) {
				response.put("token", apiAuthToken.getToken());
				response.put("expires", apiAuthToken.getExpires());
			}
			this.addOrUpdateUserDevice(workbenchUser.getUserid(), request);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.put(ERRORS,
				this.messageSource.getMessage("one.time.password.invalid.otp", new String[] {}, "", LocaleContextHolder.getLocale()));
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/validateLogin", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> validateLogin(@ModelAttribute("userAccount") final UserAccountModel model,
		final BindingResult result, final HttpServletRequest request) {
		final Map<String, Object> out = new LinkedHashMap<>();

		if (this.workbenchUserService.isValidUserLogin(model)) {

			this.userAccountValidator.validateUserActive(model, result);
			if (result.hasErrors()) {
				this.generateErrors(result, out);
				return new ResponseEntity<>(out, HttpStatus.BAD_REQUEST);
			}

			final WorkbenchUser workbenchUser = this.workbenchUserService.getUserByUserName(model.getUsername());
			final Optional<UserDeviceMetaDataDto> knownUserDeviceMetaDataDtoOptional =
				this.findExistingUserDevice(workbenchUser.getUserid(), request);

			// Require one time password verification if:
			// The user is explicitly enabled for two-factor authentication
			// Or the user logged in from a new device/location (Only if the user already has history of devices)
			if (this.enableTwoFactorAuthentication && (workbenchUser.isMultiFactorAuthenticationEnabled() || (this.enable2FAOnUnknownDevice
				&& this.userDeviceMetaDataService.countUserDevices(workbenchUser.getUserid()) > 0
				&& !knownUserDeviceMetaDataDtoOptional.isPresent()))) {
				out.put("requireOneTimePassword", Boolean.TRUE);
			} else {
				// If the user account is not enabled for two-factor authentication, immediately create authentication token.
				/*
				 * This is crucial for frontend apps which needs the authentication token to make calls to BMSAPI services.
				 * See login.js and bmsAuth.js client side scripts to see how this token is used by front-end code via the local storage
				 * service in browsers.
				 */
				final Token apiAuthToken = this.apiAuthenticationService.authenticate(model.getUsername(), model.getPassword());
				if (apiAuthToken != null) {
					out.put("token", apiAuthToken.getToken());
					out.put("expires", apiAuthToken.getExpires());
				}
				this.addOrUpdateUserDevice(workbenchUser.getUserid(), request);
			}

			out.put(AuthenticationController.SUCCESS, Boolean.TRUE);

			return new ResponseEntity<>(out, HttpStatus.OK);
		} else {
			final Map<String, String> errors = new LinkedHashMap<>();

			errors.put(UserAccountFields.USERNAME, this.messageSource
				.getMessage(UserAccountValidator.LOGIN_ATTEMPT_UNSUCCESSFUL, new String[] {}, "", LocaleContextHolder.getLocale()));

			out.put(AuthenticationController.SUCCESS, Boolean.FALSE);
			out.put(AuthenticationController.ERRORS, errors);

			return new ResponseEntity<>(out, HttpStatus.BAD_REQUEST);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> validateForgotPasswordForm(@ModelAttribute("userAccount") final UserAccountModel model,
		final BindingResult result) {
		final Map<String, Object> out = new LinkedHashMap<>();
		HttpStatus isSuccess = HttpStatus.BAD_REQUEST;

		this.forgotPasswordAccountValidator.validate(model, result);

		if (!result.hasErrors()) {
			this.userAccountValidator.validateUserActive(model, result);
		}

		if (result.hasErrors()) {
			this.generateErrors(result, out);
		} else {

			isSuccess = HttpStatus.OK;
			out.put(AuthenticationController.SUCCESS, Boolean.TRUE);

		}

		return new ResponseEntity<>(out, isSuccess);
	}

	@ResponseBody
	@RequestMapping(value = "/sendResetEmail", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> doSendResetPasswordRequestEmail(
		@ModelAttribute("userAccount") final UserAccountModel model) {
		return this.sendResetEmail(model.getUsername());
	}

	@RequestMapping(value = "/sendResetEmail/{userId}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> sendResetPasswordEmail(@PathVariable final Integer userId) {
		final Map<String, Object> out = new LinkedHashMap<>();
		final HttpStatus isSuccess = HttpStatus.BAD_REQUEST;

		final WorkbenchUser user = this.workbenchUserService.getUserByUserid(userId);
		if (user == null) {
			out.put(AuthenticationController.SUCCESS, Boolean.FALSE);
			out.put(AuthenticationController.ERRORS, NOT_EXISTENT_USER);
			return new ResponseEntity<>(out, isSuccess);
		}
		return this.sendResetEmail(user.getName());

	}

	private ResponseEntity<Map<String, Object>> sendResetEmail(final String username) {
		final Map<String, Object> out = new LinkedHashMap<>();
		HttpStatus isSuccess = HttpStatus.BAD_REQUEST;

		try {
			// success! send an email request
			this.workbenchEmailSenderService.doRequestPasswordReset(this.workbenchUserService.getUserByUserName(username));

			isSuccess = HttpStatus.OK;
			out.put(AuthenticationController.SUCCESS, Boolean.TRUE);

		} catch (final MessagingException | MailException e) {
			out.put(AuthenticationController.SUCCESS, Boolean.FALSE);
			out.put(AuthenticationController.ERRORS, e.getMessage());

			AuthenticationController.LOG.error(e.getMessage(), e);
		}

		return new ResponseEntity<>(out, isSuccess);
	}

	@ResponseBody
	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> doResetPassword(@ModelAttribute("userAccount") final UserAccountModel model,
		final BindingResult result) {
		final Map<String, Object> out = new LinkedHashMap<>();
		HttpStatus isSuccess = HttpStatus.BAD_REQUEST;

		AuthenticationController.LOG.debug("reset password submitted");

		this.userAccountValidator.validateUserActive(model, result);
		this.userAccountValidator.validatePasswordLength(model, result);
		this.userAccountValidator.validatePasswordStrength(model, result);
		this.userAccountValidator.validatePasswordConfirmation(model, result);

		if (result.hasErrors()) {
			this.generateErrors(result, out);
		} else {
			// 1. replace password
			this.workbenchUserService.updateUserPassword(model.getUsername(), model.getPassword());

			// 2. remove token
			this.workbenchEmailSenderService.deleteToken(model);

			isSuccess = HttpStatus.OK;
			out.put(AuthenticationController.SUCCESS, Boolean.TRUE);
		}

		return new ResponseEntity<>(out, isSuccess);
	}

	protected void generateErrors(final BindingResult result, final Map<String, Object> out) {
		final Map<String, String> errors = new LinkedHashMap<>();
		for (final FieldError error : result.getFieldErrors()) {
			errors.put(error.getField(), this.messageSource
				.getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(), LocaleContextHolder.getLocale()));
		}

		out.put(AuthenticationController.SUCCESS, Boolean.FALSE);
		out.put(AuthenticationController.ERRORS, errors);
	}

	protected void addOrUpdateUserDevice(final Integer userId, final HttpServletRequest httpServletRequest) {

		final String location = UserDeviceMetaDataUtil.extractIp(httpServletRequest);
		final String deviceDetails = httpServletRequest.getHeader(USER_AGENT);

		final Optional<UserDeviceMetaDataDto> knownUserDevice = this.findExistingUserDevice(userId, httpServletRequest);
		if (!knownUserDevice.isPresent()) {
			// Only add device details if it doesn't exist yet
			this.userDeviceMetaDataService.addUserDevice(userId, deviceDetails, location);
		} else {
			// If it already exists, update last_logged_in date of this device
			this.userDeviceMetaDataService.updateUserDeviceLastLoggedIn(userId, deviceDetails, location);
		}
	}

	protected Optional<UserDeviceMetaDataDto> findExistingUserDevice(final Integer userId, final HttpServletRequest httpServletRequest) {

		final String location = UserDeviceMetaDataUtil.extractIp(httpServletRequest);
		final String deviceDetails = httpServletRequest.getHeader(USER_AGENT);

		return this.userDeviceMetaDataService.findUserDevice(userId, deviceDetails, location);
	}

	protected void setEnableTwoFactorAuthentication(final boolean enableTwoFactorAuthentication) {
		this.enableTwoFactorAuthentication = enableTwoFactorAuthentication;
	}

	protected void setEnable2FAOnUnknownDevice(final boolean enable2FAOnUnknownDevice) {
		this.enable2FAOnUnknownDevice = enable2FAOnUnknownDevice;
	}

	protected void setMaximumOtpVerificationAttempt(final Integer maximumOtpVerificationAttempt) {
		this.maximumOtpVerificationAttempt = maximumOtpVerificationAttempt;
	}

	protected void setOtpVerificationAttemptExpiry(final Integer otpVerificationAttemptExpiry) {
		this.otpVerificationAttemptExpiry = otpVerificationAttemptExpiry;
	}

	protected void setOtpVerificationAttemptCache(final LoadingCache<String, Integer> otpVerificationAttemptCache) {
		this.otpVerificationAttemptCache = otpVerificationAttemptCache;
	}
}
