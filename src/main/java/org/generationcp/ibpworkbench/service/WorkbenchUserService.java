
package org.generationcp.ibpworkbench.service;

import java.util.Arrays;

import javax.annotation.Resource;

import org.generationcp.commons.util.DateUtil;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.SecurityQuestion;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.springframework.stereotype.Service;

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

		this.workbenchDataManager.addPerson(person);

		User user = new User();
		user.setPersonid(person.getId());
		user.setPerson(person);
		user.setName(userAccount.getUsername());
		user.setPassword(userAccount.getPassword());
		user.setAccess(0);

		String currentDate = DateUtil.getCurrentDateAsStringValue();

		user.setAdate(Integer.parseInt(currentDate));
		user.setCdate(Integer.parseInt(currentDate));
		user.setInstalid(0);
		user.setStatus(0);
		user.setType(0);

		// add user roles to the particular user
		user.setRoles(Arrays.asList(new UserRole(user, userAccount.getRole())));
		this.workbenchDataManager.addUser(user);

	}

	/**
	 * Updates the password of the user
	 * 
	 * @param userAccount
	 * @throws MiddlewareQueryException
	 */
	public void updateUserPassword(UserAccountModel userAccount) throws MiddlewareQueryException {
		this.workbenchDataManager.changeUserPassword(userAccount.getUsername(), userAccount.getPassword());
	}

	/**
	 * Checks validity of user
	 * 
	 * @param userAccount
	 * @return
	 * @throws MiddlewareQueryException
	 */
	public boolean isValidUserLogin(UserAccountModel userAccount) throws MiddlewareQueryException {
		return this.workbenchDataManager.isValidUserLogin(userAccount.getUsername(), userAccount.getPassword());
	}

	/**
	 * Retrieves User obj including the Person object information
	 * 
	 * @param username
	 * @return
	 * @throws MiddlewareQueryException
	 */
	public User getUserByUserName(String username) throws MiddlewareQueryException {
		User user = this.workbenchDataManager.getUserByName(username, 0, 1, Operation.EQUAL).get(0);
		Person person = this.workbenchDataManager.getPersonById(user.getPersonid());
		user.setPerson(person);

		return user;
	}

	/**
	 * Retreives User with Person object given user id
	 * 
	 * @param userId
	 * @return
	 * @throws MiddlewareQueryException
	 */
	public User getUserByUserid(Integer userId) throws MiddlewareQueryException {

		User user = this.workbenchDataManager.getUserById(userId);
		Person person = this.workbenchDataManager.getPersonById(user.getPersonid());
		user.setPerson(person);

		return user;

	}
}
