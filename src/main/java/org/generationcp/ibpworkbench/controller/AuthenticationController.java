package org.generationcp.ibpworkbench.controller;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.service.WorkbenchUserService;
import org.generationcp.ibpworkbench.validator.UserAccountValidator;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

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
	private MessageSource messageSource;

	@RequestMapping(value = "/login")
	public String getLoginPage() {
		return "login";
	}

	@ResponseBody
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public ResponseEntity<Map<String, Object>> saveUserAccount(
			@ModelAttribute("userAccount") UserAccountModel model, BindingResult result) {
		Map<String, Object> out = new LinkedHashMap<>();
		HttpStatus isSuccess = HttpStatus.BAD_REQUEST;
		userAccountValidator.validate(model, result);

		if (result.hasErrors()) {

			Map<String, String> errors = new LinkedHashMap<String, String>();
			for (FieldError error : result.getFieldErrors()) {
				errors.put(error.getField(), messageSource
						.getMessage(error.getCode(), error.getArguments(),
								error.getDefaultMessage(),
								LocaleContextHolder.getLocale()));
			}

			out.put(SUCCESS, Boolean.FALSE);
			out.put(ERRORS, errors);

		} else {
			// attempt to save the user to the database
			try {
				workbenchUserService.saveUserAccount(model);

				isSuccess = HttpStatus.OK;
				out.put(SUCCESS, Boolean.TRUE);

			} catch (MiddlewareQueryException e) {

				out.put(SUCCESS, Boolean.FALSE);
				out.put(ERRORS, e.getMessage());

				LOG.error(e.getMessage(), e);
			}
		}

		return new ResponseEntity<Map<String, Object>>(out, isSuccess);
	}
}
