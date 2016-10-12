
package org.generationcp.ibpworkbench.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.servlet.ServletContext;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.security.InvalidResetTokenException;
import org.generationcp.ibpworkbench.security.WorkbenchEmailSenderService;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.ibpworkbench.validator.ForgotPasswordAccountValidator;
import org.generationcp.ibpworkbench.validator.UserAccountFields;
import org.generationcp.ibpworkbench.validator.UserAccountValidator;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(AuthenticationController.URL)
public class AuthenticationController {

	public static final String URL = "/auth";

	public static final String SUCCESS = "success";
	public static final String ERRORS = "errors";

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

	@Resource
	private WorkbenchUserService workbenchUserService;

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
	private ServletContext servletContext;

	@Value("${workbench.enable.create.account}")
	private String enableCreateAccount;

	@Value("${institute.logo.path}")
	private String instituteLogoPath;

	private boolean isAccountCreationEnabled;

	@PostConstruct
	public void initialize(){
		// ensuring that the link is disable by default
		isAccountCreationEnabled = enableCreateAccount==null ? false : Boolean.valueOf(enableCreateAccount);
	}

	@RequestMapping(value = "/login")
	public String getLoginPage(Model model) {

		model.addAttribute("isCreateAccountEnable", isAccountCreationEnabled);
		model.addAttribute("instituteLogo", findInstituteLogo(instituteLogoPath));
		return "login";
	}

	protected String findInstituteLogo(String path) {
		if (servletContext.getResourceAsStream("/WEB-INF/" + path) != null) {
			return "/controller/" + path;
		} else {
			return "";
		}
	}

	@RequestMapping(value = "/reset/{token}", method = RequestMethod.GET)
	public String getCreateNewPasswordPage(@PathVariable String token, Model model) {

		// verify token if valid
		try {
			User user = this.workbenchEmailSenderService.validateResetToken(token);

			model.addAttribute("user", user);

			return "new-password";

		} catch (InvalidResetTokenException e) {
			AuthenticationController.LOG.debug(e.getMessage(), e);
			return "redirect:" + AuthenticationController.URL;
		}
	}

	@ResponseBody
	@RequestMapping(value = "/validateLogin", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> validateLogin(@ModelAttribute("userAccount") UserAccountModel model, BindingResult result) {
		Map<String, Object> out = new LinkedHashMap<>();
		HttpStatus isSuccess = HttpStatus.BAD_REQUEST;

		try {
			if (!this.workbenchUserService.isUserActive(model)) {
				Map<String, String> errors = new LinkedHashMap<>();

				errors.put(UserAccountFields.USERNAME,
						this.messageSource.getMessage(UserAccountValidator.LOGIN_ATTEMPT_USER_INACTIVE, new String[] {},
								"Your user account is not currently active. Please contact your system administrator",
								LocaleContextHolder.getLocale()));

				out.put(AuthenticationController.SUCCESS, Boolean.FALSE);
				out.put(AuthenticationController.ERRORS, errors);

			} else if (this.workbenchUserService.isValidUserLogin(model)) {
				isSuccess = HttpStatus.OK;
				out.put(AuthenticationController.SUCCESS, Boolean.TRUE);

				/**
				 * This is crucial for Ontology Manager UI which needs the authentication token to make calls to BMSAPI Ontology services.
				 * See login.js and bmsAuth.js client side scripts to see how this token is used by front-end code via the local storage
				 * service in browsers.
				 */
				final Token apiAuthToken = this.apiAuthenticationService.authenticate(model.getUsername(), model.getPassword());
				if (apiAuthToken != null) {
					out.put("token", apiAuthToken.getToken());
					out.put("expires", apiAuthToken.getExpires());
				}

			} else {
				Map<String, String> errors = new LinkedHashMap<>();

				errors.put(UserAccountFields.USERNAME, this.messageSource.getMessage(UserAccountValidator.LOGIN_ATTEMPT_UNSUCCESSFUL,
						new String[] {}, "Your login attempt was not successful. Please try again.", LocaleContextHolder.getLocale()));

				out.put(AuthenticationController.SUCCESS, Boolean.FALSE);
				out.put(AuthenticationController.ERRORS, errors);
			}
		} catch (MiddlewareQueryException e) {
			out.put(AuthenticationController.SUCCESS, Boolean.FALSE);
			out.put(AuthenticationController.ERRORS, e.getMessage());

			AuthenticationController.LOG.error(e.getMessage(), e);
		}

		return new ResponseEntity<>(out, isSuccess);
	}

	@ResponseBody
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> saveUserAccount(@ModelAttribute("userAccount") UserAccountModel model, BindingResult result) {
		Map<String, Object> out = new LinkedHashMap<>();
		HttpStatus isSuccess = HttpStatus.BAD_REQUEST;

		if(!isAccountCreationEnabled ){
			new ResponseEntity<>(out, HttpStatus.FORBIDDEN);
		}

		this.userAccountValidator.validate(model, result);

		if (result.hasErrors()) {

			this.generateErrors(result, out);

		} else {
			// attempt to save the user to the database
			try {
				this.workbenchUserService.saveUserAccount(model);

				isSuccess = HttpStatus.OK;
				out.put(AuthenticationController.SUCCESS, Boolean.TRUE);

			} catch (MiddlewareQueryException e) {

				out.put(AuthenticationController.SUCCESS, Boolean.FALSE);
				out.put(AuthenticationController.ERRORS, e.getMessage());

				AuthenticationController.LOG.error(e.getMessage(), e);
			}
		}

		return new ResponseEntity<>(out, isSuccess);
	}

	@ResponseBody
	@RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> validateForgotPasswordForm(@ModelAttribute("userAccount") UserAccountModel model,
			BindingResult result) {
		Map<String, Object> out = new LinkedHashMap<>();
		HttpStatus isSuccess = HttpStatus.BAD_REQUEST;

		this.forgotPasswordAccountValidator.validate(model, result);

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
	public ResponseEntity<Map<String, Object>> doSendResetPasswordRequestEmail(@ModelAttribute("userAccount") UserAccountModel model) {
		return sendResetEmail(model.getUsername());
	}

	@RequestMapping(value = "/sendResetEmail/{username}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> sendResetPasswordEmail(@PathVariable final String username) {
		return sendResetEmail(username);
	}

	private ResponseEntity<Map<String, Object>> sendResetEmail(final String username) {
		Map<String, Object> out = new LinkedHashMap<>();
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
	public Boolean doResetPassword(@ModelAttribute("userAccount") UserAccountModel model) {
		AuthenticationController.LOG.debug("reset password submitted");

		try {
			// 1. replace password
			this.workbenchUserService.updateUserPassword(model.getUsername(), model.getPassword());

			// 2. remove token
			this.workbenchEmailSenderService.deleteToken(model);

			return true;

		} catch (MiddlewareQueryException e) {
			AuthenticationController.LOG.error(e.getMessage(), e);

			return false;
		}
	}

	protected void generateErrors(BindingResult result, Map<String, Object> out) {
		Map<String, String> errors = new LinkedHashMap<>();
		for (FieldError error : result.getFieldErrors()) {
			errors.put(
					error.getField(),
					this.messageSource.getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(),
							LocaleContextHolder.getLocale()));
		}

		out.put(AuthenticationController.SUCCESS, Boolean.FALSE);
		out.put(AuthenticationController.ERRORS, errors);
	}
}
