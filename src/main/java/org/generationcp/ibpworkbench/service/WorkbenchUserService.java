package org.generationcp.ibpworkbench.service;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.SecurityQuestion;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by cyrus on 11/27/14.
 */
@Service
public class WorkbenchUserService {

	@Resource
	private WorkbenchDataManager workbenchDataManager;

	/**
	 * Cretes new user account
	 *
	 * @param userAccount
	 * @throws org.generationcp.middleware.exceptions.MiddlewareQueryException
	 */
	public void saveUserAccount(UserAccountModel userAccount) throws MiddlewareQueryException {
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

		String currentDate = new SimpleDateFormat("yyyyMMdd").format(
				Calendar.getInstance().getTime());

		user.setAdate(Integer.parseInt(currentDate));
		user.setCdate(Integer.parseInt(currentDate));
		user.setInstalid(0);
		user.setStatus(0);
		user.setType(0);

		// add user roles to the particular user
		user.setRoles(Arrays.asList(new UserRole(user, userAccount.getRole())));
		workbenchDataManager.addUser(user);

		SecurityQuestion question = new SecurityQuestion();
		question.setUserId(user.getUserid());
		question.setSecurityQuestion(userAccount.getSecurityQuestion());
		question.setSecurityAnswer(userAccount.getSecurityAnswer());

		workbenchDataManager.addSecurityQuestion(question);

	}
}
