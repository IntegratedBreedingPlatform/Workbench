package org.generationcp.ibpworkbench.controller;

import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.ibpworkbench.validator.UserAccountValidator;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.SecurityQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping(AuthenticationController.URL)
public class AuthenticationController {
	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

	public static final String URL = "/auth";

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private UserAccountValidator userAccountValidator;

	@RequestMapping(value = "/login")
	public String getLoginPage() {
		return "login";
	}

	@ResponseBody
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public Map<String,Object> saveUserAccount(@ModelAttribute("userAccount") UserAccountModel model,BindingResult result) {
		Map<String,Object> out = new LinkedHashMap<>();

		userAccountValidator.validate(model,result);

		if (result.hasErrors()) {

			Map<String, String> errors = new LinkedHashMap<String, String>();
			for (FieldError error : result.getFieldErrors()) {
				errors.put(error.getField(),error.getCode());
			}

			out.put("success",Boolean.FALSE);
			out.put("errors",errors);

		} else {
			// attempt to save the user to the database
			try {
				saveUserAccount(model);

				out.put("success",Boolean.TRUE);

			} catch (MiddlewareQueryException e) {

				out.put("success",Boolean.FALSE);
				out.put("errors",e.getMessage());

				LOG.error(e.getMessage(),e);
			}
		}

		return out;
	}

	/**
	 * Cretes new user account
	 * @param userAccount
	 * @throws MiddlewareQueryException
	 */
	protected void saveUserAccount(UserAccountModel userAccount) throws MiddlewareQueryException {
		userAccount.trimAll();

		Person person = new Person();
		person.setFirstName(userAccount.getFirstName());
		person.setMiddleName(userAccount.getMiddleName());
		person.setLastName(userAccount.getLastName());
		person.setEmail(userAccount.getEmail());
		person.setTitle("-");
		person.setContact("-");
		person.setExtension("-");
		person.setFax("-");
		person.setInstituteId(0);
		person.setLanguage(0);
		person.setNotes("-");
		person.setPositionName("-");
		person.setPhone("-");

		workbenchDataManager.addPerson(person);

		User user = new User();
		user.setPersonid(person.getId());
		user.setPerson(person);
		user.setName(userAccount.getUsername());
		user.setPassword(userAccount.getPassword());
		user.setAccess(0);
		user.setAdate(0);
		user.setCdate(0);
		user.setInstalid(0);
		user.setStatus(0);
		user.setType(0);

		workbenchDataManager.addUser(user);

		SecurityQuestion question = new SecurityQuestion();
		question.setUserId(user.getUserid());
		question.setSecurityQuestion(userAccount.getSecurityQuestion());
		question.setSecurityAnswer(userAccount.getSecurityAnswer());

		workbenchDataManager.addSecurityQuestion(question);
	}

}
