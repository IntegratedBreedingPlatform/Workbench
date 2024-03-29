
package org.generationcp.ibpworkbench.service;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class WorkbenchUserService {

	@Resource
	private UserService userService;

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

}
