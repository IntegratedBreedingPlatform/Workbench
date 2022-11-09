
package org.generationcp.ibpworkbench.service;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;

import org.generationcp.commons.util.DateUtil;
import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WorkbenchUserService {

	@Resource
	private UserService userService;

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	/**
	 * Cretes new user account
	 *
	 * @param userAccount
	 */
	public void saveUserAccount(UserAccountModel userAccount) {
		// user.access =  0 - Default User
		// user.instalid =  0 - Access all areas (legacy from the ICIS system) (not used)
		// user.status = 0 - Unassigned
		// user.type = 0 - Default user type (not used)

		userAccount.trimAll();
		Integer currentDate = DateUtil.getCurrentDateAsIntegerValue();
		Person person = this.createPerson(userAccount);

		WorkbenchUser user = new WorkbenchUser();
		user.setPerson(person);
		user.setName(userAccount.getUsername());
		user.setPassword(passwordEncoder.encode(userAccount.getPassword()));
		user.setAccess(0);
		user.setAssignDate(currentDate);
		user.setCloseDate(currentDate);
		user.setInstalid(0);
		user.setStatus(0);
		user.setType(0);
		user.setMultiFactorAuthenticationEnabled(true);

		// add user roles to the particular user
		user.setRoles(Arrays.asList(new UserRole(user, userAccount.getRole())));
		this.userService.addUser(user);

	}

	/**
	 * Updates the password of the user
	 *
	 * @param username
	 * @param password
	 * @throws MiddlewareQueryException
	 */
	public boolean updateUserPassword(String username, String password) {
		return this.userService.changeUserPassword(username, passwordEncoder.encode(password));
	}

	/**
	 * Checks if user is active
	 *
	 * @param userAccount
	 * @return
	 */
	public boolean isUserActive(UserAccountModel userAccount) {
		WorkbenchUser user = this.getUserByUserName(userAccount.getUsername());

		if (user != null) {
			Integer status = user.getStatus();
			return user.getStatus() != null && status.equals(0);
		}

		return false;
	}

	/**
	 * Checks validity of user
	 *
	 * @param userAccount
	 * @return
	 */
	public boolean isValidUserLogin(UserAccountModel userAccount) {
		WorkbenchUser user = this.getUserByUserName(userAccount.getUsername());

		if (user != null) {
			return passwordEncoder.matches(userAccount.getPassword(), user.getPassword());
		}

		return false;
	}

	/**
	 * Retrieves User obj including the Person object information
	 *
	 * @param username
	 * @return
	 */
	public WorkbenchUser getUserByUserName(String username) {
		List<WorkbenchUser> userList = this.userService.getUserByName(username, 0, 1, Operation.EQUAL);

		if (!userList.isEmpty()) {
			return userList.get(0);
		}

		return null;
	}

	/**
	 * Retreives User with Person object given user id
	 *
	 * @param userId
	 * @return
	 */
	public WorkbenchUser getUserByUserid(Integer userId) {
		return this.userService.getUserById(userId);
	}

	private Person createPerson(UserAccountModel userAccount) {
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
		this.userService.addPerson(person);

		return person;
	}
}
