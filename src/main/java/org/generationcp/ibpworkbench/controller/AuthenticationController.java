package org.generationcp.ibpworkbench.controller;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
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
import org.generationcp.middleware.service.api.user.RoleSearchDto;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Controller
@RequestMapping(AuthenticationController.URL)
public class AuthenticationController {

	public static final String URL = "/auth";

	public static final String SUCCESS = "success";
	public static final String ERRORS = "errors";

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

	private static final String NOT_EXISTENT_USER = "User does not exist";

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
	private ServletContext servletContext;

	@Resource
	@Qualifier("workbenchProperties")
	private Properties workbenchProperties;

	//TODO: Disable this option until decide which is the best
	// way to create user with roles in the Login page. ISSUE IBP-2958
	//@Value("${workbench.enable.create.account}")
	//private String enableCreateAccount;

	@Value("${workbench.is.single.user.only}")
	private String isSingleUserOnly;

	@Value("${institute.logo.path}")
	private String instituteLogoPath;

	@Value("${footer.message}")
	private String footerMessage;

	@Value("${bms.version}")
	private String workbenchVersion;

	private List<Role> roles;

	@PostConstruct
	public void initialize() {
		this.roles = this.roleService.getRoles(new RoleSearchDto(Boolean.TRUE, null, null));
		this.footerMessage = Sanitizers.FORMATTING.sanitize(this.footerMessage);
	}

	@RequestMapping(value = "/login")
	public String getLoginPage(final Model model) {

		model.addAttribute("isCreateAccountEnable", this.isAccountCreationEnabled());
		model.addAttribute("roles", this.roles);
		populateCommomModelAttributes(model);

		return "login";
	}

	/**
	 * Return img logo or emtpy if file not present
	 *
	 * @param path path to logo image
	 * @return img src
	 */
	protected String findInstituteLogo(final String path) {
		if (servletContext.getResourceAsStream("/WEB-INF/" + path) != null) {
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

			populateCommomModelAttributes(model);

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
	}

	// TODO: Migrate this to BMSAPI.
	@ResponseBody
	@RequestMapping(value = "/verifyOTP", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> validateOTP(@RequestBody final UserAccountModel model,
		final BindingResult result) {
		if (this.workbenchUserService.isValidUserLogin(model) && this.oneTimePasswordService.isOneTimePasswordValid(model.getOtpCode())) {
			/*
			 * This is crucial for frontend apps which needs the authentication token to make calls to BMSAPI services.
			 * See login.js and bmsAuth.js client side scripts to see how this token is used by front-end code via the local storage
			 * service in browsers.
			 */
			final Map<String, Object> response = new LinkedHashMap<>();
			final Token apiAuthToken = this.apiAuthenticationService.authenticate(model.getUsername(), model.getPassword());
			if (apiAuthToken != null) {
				response.put("token", apiAuthToken.getToken());
				response.put("expires", apiAuthToken.getExpires());
			}
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}

	@ResponseBody
	@RequestMapping(value = "/validateLogin", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> validateLogin(@ModelAttribute("userAccount") final UserAccountModel model,
		final BindingResult result) {
		final Map<String, Object> out = new LinkedHashMap<>();

		if (this.workbenchUserService.isValidUserLogin(model)) {

			this.userAccountValidator.validateUserActive(model, result);
			if (result.hasErrors()) {
				this.generateErrors(result, out);
				return new ResponseEntity<>(out, HttpStatus.BAD_REQUEST);
			}

			final WorkbenchUser workbenchUser = this.workbenchUserService.getUserByUserName(model.getUsername());

			if (workbenchUser.isMultiFactorAuthenticationEnabled()) {
				//
				final OneTimePasswordDto oneTimePasswordDto = this.oneTimePasswordService.createOneTimePassword();
				try {
					this.workbenchEmailSenderService.doSendOneTimePasswordRequest(workbenchUser, oneTimePasswordDto.getOtpCode());
				} catch (final MessagingException e) {
					// Do nothing
				}
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
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> saveUserAccount(@ModelAttribute("userAccount") final UserAccountModel model,
		final BindingResult result) {
		final Map<String, Object> out = new LinkedHashMap<>();
		HttpStatus isSuccess = HttpStatus.BAD_REQUEST;

		if (!isAccountCreationEnabled()) {
			new ResponseEntity<>(out, HttpStatus.FORBIDDEN);
		}
		final ImmutableMap<Integer, Role> roleMap = Maps.uniqueIndex(this.roles, new Function<Role, Integer>() {

			@Override
			public Integer apply(final Role role) {
				return role.getId();
			}
		});
		model.setRole(roleMap.get(model.getRoleId()));
		this.userAccountValidator.validate(model, result);

		if (result.hasErrors()) {

			this.generateErrors(result, out);

		} else {
			// attempt to save the user to the database
			this.workbenchUserService.saveUserAccount(model);

			isSuccess = HttpStatus.OK;
			out.put(AuthenticationController.SUCCESS, Boolean.TRUE);

		}

		return new ResponseEntity<>(out, isSuccess);
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
		return sendResetEmail(model.getUsername());
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
		return sendResetEmail(user.getName());

	}

	private ResponseEntity<Map<String, Object>> sendResetEmail(final String username) {
		final Map<String, Object> out = new LinkedHashMap<>();
		HttpStatus isSuccess = HttpStatus.BAD_REQUEST;

		try {
			// success! send an email request
			this.workbenchEmailSenderService.doRequestPasswordReset(this.workbenchUserService.getUserByUserName(username));

			isSuccess = HttpStatus.OK;
			out.put(AuthenticationController.SUCCESS, Boolean.TRUE);

		} catch (MessagingException | MailException e) {
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

	protected boolean isAccountCreationEnabled() {

		// Do not display the Create Account link if BMS is in single user mode.
		/*if (Boolean.parseBoolean(isSingleUserOnly)) {
			return false;
		} else {
			return Boolean.parseBoolean(this.enableCreateAccount);
		}*/
		return false;
	}

	/*protected void setEnableCreateAccount(final String enableCreateAccount) {
		this.enableCreateAccount = enableCreateAccount;
	}*/

	protected void setIsSingleUserOnly(final String isSingleUserOnly) {
		this.isSingleUserOnly = isSingleUserOnly;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
}
