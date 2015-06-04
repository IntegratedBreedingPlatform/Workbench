
package org.generationcp.ibpworkbench.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.SecurityQuestion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WorkbenchUserServiceTest {

	private static final int TEST_USER_ID = 1;
	private static final Integer TEST_PERSON_ID = 2;
	private static final String TEST_USERNAME = "TEST_USERNAME";
	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@InjectMocks
	private WorkbenchUserService userService;

	@Test
	public void testSaveUserAccount() throws Exception {
		UserAccountModel userAccount = this.createUserAccount();

		this.userService.saveUserAccount(userAccount);

		Mockito.verify(this.workbenchDataManager).addPerson(Matchers.any(Person.class));
		Mockito.verify(this.workbenchDataManager).addUser(Matchers.any(User.class));
		Mockito.verify(this.workbenchDataManager).addSecurityQuestion(Matchers.any(SecurityQuestion.class));
	}

	protected UserAccountModel createUserAccount() {
		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setFirstName("firstName");
		userAccount.setLastName("lastName");
		userAccount.setEmail("email@email.com");
		userAccount.setRole("ADMIN");
		userAccount.setUsername("username");
		userAccount.setPassword("password");
		return userAccount;
	}

	@Test
	public void testIsValidUserLogin() throws Exception {
		UserAccountModel userAccount = this.createUserAccount();

		this.userService.isValidUserLogin(userAccount);

		Mockito.verify(this.workbenchDataManager).isValidUserLogin(userAccount.getUsername(), userAccount.getPassword());
	}

	@Test
	public void testUpdateUserPassword() throws Exception {
		UserAccountModel userAccount = this.createUserAccount();

		this.userService.updateUserPassword(userAccount);

		Mockito.verify(this.workbenchDataManager).changeUserPassword(userAccount.getUsername(), userAccount.getPassword());
	}

	@Test
	public void testGetUserByUserName() throws Exception {
		User user = new User();
		user.setPersonid(WorkbenchUserServiceTest.TEST_PERSON_ID);
		Person person = new Person();
		List<User> userList = new ArrayList<>();
		userList.add(user);

		Mockito.when(this.workbenchDataManager.getUserByName(WorkbenchUserServiceTest.TEST_USERNAME, 0, 1, Operation.EQUAL)).thenReturn(
				userList);
		Mockito.when(this.workbenchDataManager.getPersonById(WorkbenchUserServiceTest.TEST_PERSON_ID)).thenReturn(person);

		User resultUser = this.userService.getUserByUserName(WorkbenchUserServiceTest.TEST_USERNAME);

		Assert.assertEquals("Should be the same as the setup user", user, resultUser);

	}

	@Test
	public void testGetUserByUserId() throws Exception {
		User user = new User();
		user.setPersonid(WorkbenchUserServiceTest.TEST_PERSON_ID);
		Person person = new Person();
		Mockito.when(this.workbenchDataManager.getUserById(WorkbenchUserServiceTest.TEST_USER_ID)).thenReturn(user);
		Mockito.when(this.workbenchDataManager.getPersonById(WorkbenchUserServiceTest.TEST_PERSON_ID)).thenReturn(person);

		User resultUser = this.userService.getUserByUserid(WorkbenchUserServiceTest.TEST_USER_ID);

		Assert.assertEquals("Should be the same as the setup user", user, resultUser);

	}

}
